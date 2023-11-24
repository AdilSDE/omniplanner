package com.microd.imagegenerator;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomplannerImageGeneratorException extends Exception {

  public static void ReportException(Throwable cause, AmazonS3 storage, ImageGeneratorInput paramDictInput, APIGatewayProxyRequestEvent request,AmazonSNSClient snsClient) {
    String reportLocation = StoreErrorReport(cause.getMessage(), "error-report", cause, storage, paramDictInput, request,snsClient);

//    Common.Error(reportLocation, new RoomplannerImageGeneratorException("Error report stored to:\n"+reportLocation, cause));
    Common.Error(reportLocation, new RoomplannerImageGeneratorException(reportLocation, cause));
  }

  public static void ReportTimeLimitExceeding(long time, AmazonS3 storage, ImageGeneratorInput paramDictInput, APIGatewayProxyRequestEvent request, AmazonSNSClient snsClient) {
    String message = "Wrn: Request took " + time + " ms.";
    String reportLocation = StoreErrorReport(message, "time-limit", null, storage, paramDictInput, request,snsClient);
    Common.Log(message+" Report generated to: " + reportLocation);
  }

  private RoomplannerImageGeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

  private static String StoreErrorReport(String message, String fn, Throwable cause, AmazonS3 storage, ImageGeneratorInput paramDictInput,
                                         APIGatewayProxyRequestEvent request,AmazonSNSClient snsClient) {
    String bucket = System.getenv("ERROR_REPORT_BUCKET_NAME");
    if (bucket == null || bucket.isEmpty()) {
      Common.Log("Wrn: ERROR_REPORT_BUCKET_NAME value missing in app.yaml. Using \"omniplanner-error-reports\".");
      bucket = "revalize-omniplanner-error-reports";
    }

    String build = System.getenv("BUILD_NAME");
    if (build != null && !build.isEmpty()) {
      fn += '-' + build;
    }

    String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());
    String keyPath = fn + '-' + timestamp + '-' + Common.getSaltString(8) + ".json";

    JSONObject jo = new JSONObject();

    // common data
    jo.put("name", "RoomplannerImageGenerator Error report");
    jo.put("version", Common.GetAppVersion());
    jo.put("timestamp", timestamp);
    jo.put("filename", keyPath);

    // request data
    JSONObject requestObj = new JSONObject();
    requestObj.put("method", request.getHttpMethod());
    requestObj.put("requestURL", request.getPath());
    requestObj.put("referer", request.getHeaders()!=null?request.getHeaders().get("referer"):"custom_referrer");
    requestObj.put("user-agent", request.getHeaders()!=null?request.getHeaders().get("user-agent"):"custom_user-agent");
    requestObj.put("payload", paramDictInput!=null? paramDictInput.getJson():"No payload received in Request ");
    jo.put("request", requestObj);

    // exception data
    JSONObject excObj = new JSONObject();
    if (cause != null) {
      excObj.put("message", message);
      excObj.put("exception", cause.getClass().getName());

      // Using a StringWriter,
      // to convert trace into a String:
      StringWriter sw = new StringWriter();
      // create a PrintWriter
      PrintWriter pw = new PrintWriter(sw);
      cause.printStackTrace(pw);
      excObj.put("stacktrace", sw.toString());
      jo.put("exception", excObj);
    }
    else {
      jo.put("message", message);
    }
    byte[] data = jo.toString(2).getBytes();

    String reportLocation;
    try {


      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType("text/json");
      metadata.setContentDisposition("attachment; filename=" + fn + '-' +timestamp + ".json");

      Common.Log("writing file to aws .....!!!!!!!!!!");
      storage.putObject(new PutObjectRequest(bucket, keyPath, new ByteArrayInputStream(data), metadata));
      String url = storage.getUrl(bucket, keyPath).toString();

      PublishRequest publishRequest=new PublishRequest(System.getenv("topicArn"),buildEmailBody(url,fn,message),"Notification: Omni-planner " +fn+ " alert..!! ");
      snsClient.publish(publishRequest);

      Common.Log("Report written to "+url+" and Notification: Alert  Sent");
      reportLocation=bucket + "/" + keyPath;

      /*INFO for GCp...
      storage = StorageOptions.getDefaultInstance().getService();
      // save to bucket with correct mime type
      // setContentDisposition ensures the file will be downloaded without the prefix ["generated/" + UUID.randomUUID().toString()]
      Blob blob = storage.create(
              BlobInfo.newBuilder(bucket, keyPath).setContentType("text/json")
                      .setContentDisposition("attachment; filename=" + fn + '-' +timestamp + ".json")
                      .build(),
              data);
      reportLocation = blob.getBucket() + '/' + blob.getName();
      Common.Log("Report written to "+blob.getMediaLink());*/

    } catch(Exception e) {
      Common.Error("Error saving error report to Cloud Storage: "+e.getMessage());
      reportLocation = "Unable to store the error report";
    }
    return reportLocation;
  }

  private static String buildEmailBody(String url, String errorDescription, String message) {
    String filePath = "email.txt";
    String emailContent = readEmailFile(filePath);

    // Replace the {{dynamicContent}} placeholder with the actual dynamic content
    emailContent = replacePlaceholder(emailContent, "{{errorDescription}}", errorDescription);
    emailContent = replacePlaceholder(emailContent, "{{exceptionMessage}}", message);
    emailContent = replacePlaceholder(emailContent, "{{url}}", url);

    // Now, emailContent contains the modified Text with the dynamic content replaced
    return emailContent;
  }
  private static String readEmailFile(String resourcePath) {
    try {

      // Create a ClassPathResource based on the resource path
      Resource resource = new ClassPathResource(resourcePath);

      // Open an input stream to read the file content
      InputStream inputStream = resource.getInputStream();

      // Use Apache Commons IOUtils to read the content as a String
      String fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

      // You can now use the "fileContent" as needed
      return fileContent;
    } catch (IOException e) {
      e.printStackTrace();
      return "An error occurred while reading email.txt file"; // Handle the exception appropriately
    }
  }

  private static String replacePlaceholder(String emailContent, String placeholder, String dynamicContent) {
    return emailContent.replace(placeholder, dynamicContent);
  }


}
