package com.microd.imagegenerator;

import com.google.cloud.storage.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public class RoomplannerImageGeneratorException extends Exception {

  public static void ReportException(Throwable cause, Storage storage, ImageGeneratorInput paramDictInput, HttpServletRequest request) {
    String reportLocation = StoreErrorReport(cause.getMessage(), "error-report", cause, storage, paramDictInput, request);
    
//    Common.Error(reportLocation, new RoomplannerImageGeneratorException("Error report stored to:\n"+reportLocation, cause));
    Common.Error(reportLocation, new RoomplannerImageGeneratorException(reportLocation, cause));
  }

  public static void ReportTimeLimitExceeding(long time, Storage storage, ImageGeneratorInput paramDictInput, HttpServletRequest request) {
    String message = "Wrn: Request took " + time + " ms.";
    String reportLocation = StoreErrorReport(message, "time-limit", null, storage, paramDictInput, request);
    Common.Log(message+" Report generated to: " + reportLocation);
  }
  
  private RoomplannerImageGeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

  private static String StoreErrorReport(String message, String fn, Throwable cause, Storage storage, ImageGeneratorInput paramDictInput, HttpServletRequest request) {
    String bucket = System.getenv("ERROR_REPORT_BUCKET_NAME");
    if (bucket == null || bucket.isEmpty()) {
  		Common.Log("Wrn: ERROR_REPORT_BUCKET_NAME value missing in app.yaml. Using \"omniplanner-error-reports\".");
    	bucket = "omniplanner-error-reports";
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
    requestObj.put("method", request.getMethod());
    requestObj.put("requestURL", request.getRequestURL());
    requestObj.put("referer", request.getHeader("referer"));
    requestObj.put("user-agent", request.getHeader("user-agent"));
    requestObj.put("payload", paramDictInput.getJson());
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
      storage = StorageOptions.getDefaultInstance().getService();
      // save to bucket with correct mime type
      // setContentDisposition ensures the file will be downloaded without the prefix ["generated/" + UUID.randomUUID().toString()]        
      Blob blob = storage.create(
          BlobInfo.newBuilder(bucket, keyPath).setContentType("text/json")
              .setContentDisposition("attachment; filename=" + fn + '-' +timestamp + ".json")
              .build(),
              data);
      reportLocation = blob.getBucket() + '/' + blob.getName();
      Common.Log("Report written to "+blob.getMediaLink());
    } catch(Exception e) {
      Common.Error("Error saving error report to Cloud Storage: "+e.getMessage());
      reportLocation = "Unable to store the error report";
    }
    return reportLocation;
  }

}
