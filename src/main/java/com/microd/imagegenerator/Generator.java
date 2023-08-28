package com.microd.imagegenerator;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
public class Generator {

  private static final String DEFAULT_BUCKET_NAME = "revalize-image-generator";

 /* INFO :: for Gcp
 private Storage storage = null;*/

	@Value("${application.bucket.name}")
	private String bucketName;

	@Autowired
	private AmazonS3 s3Client;

  public ImageGeneratorOutput handleRequest(ImageGeneratorInput paramDictInput,  APIGatewayProxyRequestEvent request) {

    // check input parameters
    try {
      checkParameters(paramDictInput);
    } catch (Exception e) {
      RoomplannerImageGeneratorException.ReportException(e, s3Client, paramDictInput, request);
      return new ImageGeneratorOutput(e.getMessage());
    }

	// try to read environment variable (or use the default)
    String bucket = System.getenv("BUCKET_NAME");
    if (bucket == null || bucket.isEmpty()) {
  		Common.Log("Wrn: No bucket name specified. Using the hard-coded one instead.");
    	bucket = DEFAULT_BUCKET_NAME;
    }

		ImageGeneratorOutput output = null;
		ImageGeneratorInputData input = paramDictInput.getParamDictionary();

		System.setProperty("java.awt.headless", "true");
		System.setProperty("javax.accessibility.assistive_technologies", "");

		Common.Log("Input: " + input + "\n");

		// 1) Create a tmp folder
		File tmpDirectory = new File("/tmp");
		if (!tmpDirectory.exists()) {
			tmpDirectory.mkdir();
		}

		// 2) Create a temporary filename
		try {
      long start = System.nanoTime();

			String[] svgs = input.getSvgs();
			ArrayList<String> outputFiles= new ArrayList<String>();

			String suffix;
			String mimeType;
			switch (input.getFormat().toLowerCase()) {
			case "pdf":
				suffix = "pdf";
				mimeType = "application/pdf";
				break;
			case "jpg":
				suffix = "jpg";
				mimeType = "image/jpeg";
				break;
			case "tiff":
				suffix = "tif";
				mimeType = "image/tiff";
				break;
			case "png":
			default:
				suffix = "png";
				mimeType = "image/png";
				break;
			}

			for(int pageIndex = 0; pageIndex<svgs.length; pageIndex++)
			{
				File srcFile = File.createTempFile("src", "svg", tmpDirectory);

				// Write the SVG to the temporary file.
				PrintWriter out = new PrintWriter(srcFile.getAbsolutePath());
				out.println(svgs[pageIndex]);
				out.close();

				// log("Wrote: " + svgs[pageIndex] +
				// " to "+srcFile.getAbsolutePath());
				// for parameters description see PDFConverter.java
				ArrayList<String> args = new ArrayList<String>();
				args.add("-scriptSecurityOff");
				args.addAll(Arrays.asList("-bg", "255.255.255.255"));
				args.addAll(Arrays.asList("-m", mimeType));
				args.addAll(Arrays.asList("-w", input.getWidth().toString()));
				args.addAll(Arrays.asList("-h", input.getHeight().toString()));

				switch (input.getFormat().toLowerCase())
				{
					case "jpg":
						args.addAll(Arrays.asList("-q", "0.8"));
						break;
					case "pdf":
						args.addAll(Arrays.asList("-dpi", "300"));
						break;
					default:
						break;
				}

				// add the source file
				args.add(srcFile.getAbsolutePath());

				// add the output file
				String name = srcFile.getName();
				name = name.replace("src", "dst");
				name = name.concat("." + suffix);
				File dstFile = new File(tmpDirectory, name);
				args.addAll(Arrays.asList("-d", dstFile.getAbsolutePath()));
				//
				// String [] args= {
				// "-scriptSecurityOff",
				// "-bg","255.255.255.255", // background is white. // -a,
				// "-m","application/pdf",
				// "-q","0.8",
				// srcFile.getAbsolutePath(),
				// "-d", dstFile.getAbsolutePath()
				// };

//				Common.Log("Calling with " + args.toString());

				try {
					PDFConverter main = new PDFConverter(args.toArray(new String[1]));
					main.execute();

					// clean up after ourselves.
					srcFile.delete();

//					Common.Log("Back from Main- wrote to " + dstFile.getAbsolutePath());

          if (main.success) {
            // add to the output files.
  					Common.Log("Converting page ("+pageIndex+"}: Success");
            outputFiles.add(dstFile.getAbsolutePath());
          } else {
            throw new Exception("Converting page ("+pageIndex+"}: Error: "+main.error);
          }
				} catch (Exception e) {
          RoomplannerImageGeneratorException.ReportException(e, s3Client, paramDictInput, request);
					output = new ImageGeneratorOutput(e.getMessage());
				}
			}

			// if more than one page, we can only handle that in the case of PDF.
			File finalFile = null;
			if(outputFiles.size() > 1 && suffix.equals("pdf")) {
				finalFile = mergePdfPages(outputFiles, tmpDirectory);
			} else if(outputFiles.size()==1) {
				// only one, use that one.
				finalFile = new File(outputFiles.get(0));
			}

			if(finalFile != null)
			{
				byte[] data = loadFileAsBytesArray(finalFile.getAbsolutePath());

				String url = null;

				String planName = (input.getName()==null || input.getName().isEmpty()) ? "New Plan" : input.getName();
				String keyPath = "generated/" + UUID.randomUUID().toString() + "_" + planName+ "." + suffix;

				try {

					ObjectMetadata metadata = new ObjectMetadata();
					metadata.setContentType(mimeType);
					metadata.setContentDisposition("attachment; filename=" + planName + "." + suffix);

					Common.Log("writing file to aws .....!!!!!!!!!!");
					s3Client.putObject(new PutObjectRequest(bucket, keyPath, new ByteArrayInputStream(data), metadata));

					url = s3Client.getUrl(bucket, keyPath).toString();
					System.out.println("Result written to " + url);

					/*INFO :: for GCP Storage...
					storage = StorageOptions.getDefaultInstance().getService();
					// save to bucket with correct mime type
					// setContentDisposition ensures the file will be downloaded without the prefix ["generated/" + UUID.randomUUID().toString()]
					Blob blob = storage.create(
	            BlobInfo.newBuilder(bucket, keyPath).setContentType(mimeType).setContentDisposition("attachment; filename="+planName+ "." + suffix).build(),
	            data);
					url = blob.getMediaLink();*/
					Common.Log("Result written to "+url);
				} catch(Exception e) {
					Common.Error("Error saving to Cloud Storage: "+e.getMessage());
          RoomplannerImageGeneratorException.ReportException(e, s3Client, paramDictInput, request);
				}

				// convert to base64 8859-01 charset; does that matter? is that
				// ascii?
				// 1.7 version
				String encodeStr = DatatypeConverter.printBase64Binary(data);

				// 1.8 version
				//				String encodeStr = Base64.getEncoder().encodeToString(
				//						loadFileAsBytesArray(dstFile.getAbsolutePath()));

				// remove the dest file.
				finalFile.delete();

				// create the output!
				output = new ImageGeneratorOutput(input.getFormat(), encodeStr, url);
			} else {
				// this really shouldn't happen, as if there are no pages, then they either sent none
				// or there was an exception (which set the Output before)
				if(output==null) {
					output = new ImageGeneratorOutput("Error generating pdf. No pages!");
				}
			}

      // write report if we have exceeded the time limit specified in env. variable REPORT_LONG_TASKS_LIMIT
      long finish = System.nanoTime();
      long timeElapsedMs = (finish - start)/1000000;  // convert nano to mili
      String strLimit = System.getenv("REPORT_LONG_TASKS_LIMIT");
      long limit = Long.MAX_VALUE;  // do not report if the limit is not specified in yaml
      if (strLimit != null && !strLimit.isEmpty()) {
        limit = Long.parseLong(strLimit);
      }
      if (output.getSuccess() && timeElapsedMs > limit) {
        RoomplannerImageGeneratorException.ReportTimeLimitExceeding(timeElapsedMs, s3Client, paramDictInput, request);
      }
		} catch (Throwable e) {
      RoomplannerImageGeneratorException.ReportException(e, s3Client, paramDictInput, request);
			output = new ImageGeneratorOutput(e.getMessage());
		}

		return output;
	}

	private void checkParameters(ImageGeneratorInput paramDictInput) {
    if (paramDictInput == null)
      throw new IllegalArgumentException("No input parameters");
    if (paramDictInput.getParamDictionary() == null)
      throw new IllegalArgumentException("Missing paramDictionary");
    String sanityCheckErrMessage = paramDictInput.getParamDictionary().sanityCheck();
    if (sanityCheckErrMessage != null)
      throw new IllegalArgumentException("Parameters sanity check: " + sanityCheckErrMessage);
  }

	private String exceptionToString(Throwable ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));

		return ex.getClass().getName() + ": " + ex.getMessage() + " Stack: "
		+ sw.toString();
	}

	private byte[] loadFileAsBytesArray(String fileName)
			throws Exception {
		File file = new File(fileName);
		int length = (int) file.length();
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[length];
		reader.read(bytes, 0, length);
		reader.close();
		return bytes;
	}

	private File mergePdfPages(ArrayList<String> outputFiles, File tmpDirectory)
    throws FileNotFoundException, IOException {

    // we need to combine multiple PDFS.
    PDFMergerUtility ut = new PDFMergerUtility();

    for (String mergeSrcFile: outputFiles) {
      ut.addSource(mergeSrcFile);
    }

    File mergedFile = File.createTempFile("mrg", "pdf", tmpDirectory);
    if (mergedFile.exists())
    {
      mergedFile.delete();
    }

    ut.setDestinationFileName(mergedFile.getAbsolutePath());
    try {
      Common.Log("Merging "+outputFiles.size()+" PDF documents!");
      ut.mergeDocuments(null);

      // now delete the source files.
      for (String mergeSrcFile: outputFiles) {
        File f = new File(mergeSrcFile);
        f.delete();
      }
    } catch(IOException e) {
      Common.Error("Error merging pdf pages: " + exceptionToString(e));
      throw e;
    }

    return mergedFile;
  }
}
