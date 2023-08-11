package com.microd.imagegenerator;

public class ImageGeneratorOutputResult {
  /** Base64 encoded image data */
	private String data;
  /** URL of the generated image (in GCS) */
	private String url;
	
	public String getData() {
		return this.data;
	}
	
	public void setData(String s) {
		this.data= s;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return this.url;
	}

}
