package com.microd.imagegenerator.unit;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.json.Jackson;
import com.microd.imagegenerator.Generator;
import com.microd.imagegenerator.ImageGeneratorInput;
import com.microd.imagegenerator.ImageGeneratorOutput;
import com.microd.imagegenerator.PDFConverter;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.FileInputStream;
import java.net.URL;

import static org.mockito.Mockito.when;

@DisplayName("ImageGenerator Time Limit units Tests")
public class TimeLimitExceededTests {


    @Mock
    private AmazonS3 s3Client;
    @Mock
    private PDFConverter pdfConverter;

    @InjectMocks
    private Generator generator;


    @BeforeEach
    public void setup() {

        MockitoAnnotations.initMocks(this);
    }

    @Test()
    @DisplayName(
            "Success : Test case to convert Svgs format to Pdf/any and converted file store into error bucket" +
                    "with time-limit extension"
    )
    public void testHandleRequest_Success_TimeLimitExceeded() throws Exception {
        // Create mock input objects
        FileInputStream fis = new FileInputStream("samples/pdf.json");
        String stringTooLong = IOUtils.toString(fis, "UTF-8");
        ImageGeneratorInput input = Jackson.fromJsonString(stringTooLong,ImageGeneratorInput.class);//MockImageGenerationInput.getImageGeneratorInput("pdf");
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

        // Set up behavior for mock S3Client
        when(s3Client.putObject(ArgumentMatchers.any(PutObjectRequest.class))).thenReturn(null);
        URL mockUrl = new URL("http://mock.aws.com");
        when(s3Client.getUrl(ArgumentMatchers.any(), ArgumentMatchers.any(String.class))).thenReturn(mockUrl);
        // Call theM method being tested
        ImageGeneratorOutput output = generator.handleRequest(input, request);

        Assertions.assertTrue(output.getSuccess());
        Mockito.verify(s3Client,Mockito.times(1)).putObject(ArgumentMatchers.any(PutObjectRequest.class));
    }
}
