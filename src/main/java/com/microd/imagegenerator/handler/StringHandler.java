package com.microd.imagegenerator.handler;

import com.microd.imagegenerator.ImageGeneratorInput;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

public class StringHandler extends SpringBootRequestHandler<ImageGeneratorInput,Object> {
}
