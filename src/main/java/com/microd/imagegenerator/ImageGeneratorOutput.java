package com.microd.imagegenerator;

/** The data returned to the caller in JSON format */
public class ImageGeneratorOutput {
  private Boolean success;
  /** "OK" or error message */
  private String message;
  /** result data */
  private ImageGeneratorOutputResult result;
  /** Version number of image-generator */
  private String version;


  public Boolean getSuccess() {
    return this.success;
  }

  public void setSuccess(Boolean s) {
    this.success= s;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String s) {
    this.message= s;
  }

  public ImageGeneratorOutputResult getResult() {
    return this.result;
  }

  public void setResult(ImageGeneratorOutputResult r) {
    this.result= r;
  }

  public void setVersion(String v) {
    this.version = v;
  }

  public String getVersion() {
    return this.version;
  }

  @Override
  public String toString() {
    if(this.success) {
      return "Success => "+this.result.getData();
    } else {
      return "Failure=> "+this.message;
    }
	}
	
  // remove public once done testing.
  public ImageGeneratorOutput(String errorMessage) {
    this.success= false;
    this.result= null;
    this.message= errorMessage;
  }

  ImageGeneratorOutput(String format, String base64Data, String url) {
    String data= null;
    switch(format)
    {
    case "pdf": 
      data= "data:application/pdf;base64,"+base64Data;
      break;
    case "png": 
      data= "data:image/png;base64,"+base64Data;
      break;
    case "jpg": 
      data= "data:image/jpeg;base64,"+base64Data;
      break;
    case "tiff": 
      data= "data:image/tiff;base64,"+base64Data;
      break;
    }

    this.version = Common.GetAppVersion();

    if(data != null)
    {
      this.success= true;
      this.message= "";
      this.result = new ImageGeneratorOutputResult();
      this.result.setData(data);
      if(url != null)
      {
        this.result.setUrl(url);
      }
    } else {
      this.success= false;
      this.message ="Unknown format: "+format;
    }
	}
}

