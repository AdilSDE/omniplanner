package com.microd.imagegenerator.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.microd.imagegenerator.ImageGeneratorInput;
import com.microd.imagegenerator.ImageGeneratorOutput;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

public class RequestHandler extends SpringBootRequestHandler<ImageGeneratorInput, Object> {
}