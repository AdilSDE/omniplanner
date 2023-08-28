package com.microd.imagegenerator;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.json.JSONObject;

public class ImageGeneratorInput extends  APIGatewayProxyRequestEvent {
	private ImageGeneratorInputData data;
	
	public void setParamDictionary(ImageGeneratorInputData d) {
		this.data = d;
	}
	
	public ImageGeneratorInputData getParamDictionary() {
		return this.data;
	}

  public JSONObject getJson() {
    JSONObject jo = new JSONObject();
    jo.put("paramDictionary", data!=null?data.getJson():"No paramDictionary data received in request !!!");
    return jo;
  }
  
	public String toString() {
		return "-- ImageGeneratorInput: "+this.data;
	}
}
