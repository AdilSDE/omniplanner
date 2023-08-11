package com.microd.imagegenerator;
import org.json.JSONObject;

public class ImageGeneratorInput {
	private ImageGeneratorInputData data;
	
	public void setParamDictionary(ImageGeneratorInputData d) {
		this.data = d;
	}
	
	public ImageGeneratorInputData getParamDictionary() {
		return this.data;
	}

  public JSONObject getJson() {
    JSONObject jo = new JSONObject();
    jo.put("paramDictionary", data.getJson());
    return jo;
  }
  
	public String toString() {
		return "-- ImageGeneratorInput: "+this.data;
	}
}
