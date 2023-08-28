package com.microd.imagegenerator.fucntioncontroller;

import com.microd.imagegenerator.Generator;
import com.microd.imagegenerator.ImageGeneratorInput;
import com.microd.imagegenerator.ImageGeneratorOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.function.Function;


public class Generate implements Function<ImageGeneratorInput, ImageGeneratorOutput> {

    @Autowired
    private Generator generator;

    @Override
    public ImageGeneratorOutput apply(ImageGeneratorInput event) {
        return generator.handleRequest(event, event);
    }
}
