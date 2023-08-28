package com.microd.imagegenerator.mock;

import com.microd.imagegenerator.ImageGeneratorInput;
import com.microd.imagegenerator.ImageGeneratorInputData;

public final class MockImageGenerationInput {
    private MockImageGenerationInput(){}

    public static ImageGeneratorInput getImageGeneratorInput(String format) {
        ImageGeneratorInput input = new ImageGeneratorInput();
        ImageGeneratorInputData inputData = new ImageGeneratorInputData();
        inputData.setCmd("pdf_or_png");
        inputData.setFormat(format);
        inputData.setWidth(240);
        inputData.setHeight(240);
        inputData.setSvgs(new String[]{"svgs format"});
        input.setParamDictionary(inputData);
        return input;
    }

}
