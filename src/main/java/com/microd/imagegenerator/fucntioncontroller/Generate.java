package com.microd.imagegenerator.fucntioncontroller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.util.json.Jackson;
import com.microd.imagegenerator.Generator;
import com.microd.imagegenerator.ImageGeneratorInput;
import com.microd.imagegenerator.ImageGeneratorOutput;
import com.microd.imagegenerator.handler.CustomInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.function.Function;


public class Generate implements Function<CustomInput, ImageGeneratorOutput> {

    @Autowired
    private Generator generator;

    @Override
    public ImageGeneratorOutput apply(CustomInput event) {
        return generator.handleRequest(event.getGeneratorInput(), event);
    }
}
