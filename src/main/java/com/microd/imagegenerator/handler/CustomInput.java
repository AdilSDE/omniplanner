package com.microd.imagegenerator.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.microd.imagegenerator.ImageGeneratorInput;

public class CustomInput extends APIGatewayProxyRequestEvent {

    public ImageGeneratorInput generatorInput;

    public ImageGeneratorInput getGeneratorInput() {
        return generatorInput;
    }

    public void setGeneratorInput(ImageGeneratorInput generatorInput) {
        this.generatorInput = generatorInput;
    }
}
