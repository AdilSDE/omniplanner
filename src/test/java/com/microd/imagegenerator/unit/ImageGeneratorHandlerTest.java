package com.microd.imagegenerator.unit;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.json.Jackson;
import com.microd.imagegenerator.Generator;
import com.microd.imagegenerator.ImageGeneratorInput;
import com.microd.imagegenerator.ImageGeneratorOutput;
import com.microd.imagegenerator.PDFConverter;
import com.microd.imagegenerator.mock.MockImageGenerationInput;
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

@DisplayName("ImageGenerator units Tests")
public class ImageGeneratorHandlerTest {

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
            "Success : Test case to convert Svgs format to Pdf and converted file store into bucket"
    )
    public void testHandleRequest_Success_SvgsToPdf() throws Exception {
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

    @Test()
    @DisplayName(
            "Success : Test case to convert Svgs format to Jpg and converted file store into bucket"
    )
    public void testHandleRequest_Success_SvgsToJpg() throws Exception {
        // Create mock input objects
        FileInputStream fis = new FileInputStream("samples/jpg.json");
        String stringTooLong = IOUtils.toString(fis, "UTF-8");
        ImageGeneratorInput input = Jackson.fromJsonString(stringTooLong,ImageGeneratorInput.class);
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
    @Test()
    @DisplayName(
            "Success : Test case to convert Svgs format to Png and converted file store into bucket"
    )
    public void testHandleRequest_Success_SvgsToPng() throws Exception {
        // Create mock input objects
        FileInputStream fis = new FileInputStream("samples/png.json");
        String stringTooLong = IOUtils.toString(fis, "UTF-8");
        ImageGeneratorInput input = Jackson.fromJsonString(stringTooLong,ImageGeneratorInput.class);
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

    @Test()
    @DisplayName(
            " Test case with Invalid Input and error json(result) store to error bucket "
    )
    public void testHandleRequest_Success_Invalid_Input() throws Exception {
        // Create mock input objects
        ImageGeneratorInput input = MockImageGenerationInput.getImageGeneratorInput("png");
        input.setParamDictionary(null);
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

        // Set up behavior for mock S3Client
        when(s3Client.putObject(ArgumentMatchers.any(PutObjectRequest.class))).thenReturn(ArgumentMatchers.any());
        URL mockUrl = new URL("http://mock.aws.com");
        when(s3Client.getUrl(ArgumentMatchers.any(), "error-report-2023-08-24T23-40-04-D9KXUZQT.json")).thenReturn(mockUrl);

        // Call theM method being tested
        ImageGeneratorOutput output = generator.handleRequest(input, request);

        Assertions.assertFalse(output.getSuccess());
        Mockito.verify(s3Client,Mockito.times(1)).putObject(ArgumentMatchers.any(PutObjectRequest.class));
    }
    @Test()
    @DisplayName(
            "Test case with No input parameters and error json(result) store to error bucket "
    )
    public void testHandleRequest_Success_NoInputParameter() throws Exception {
        // Create mock input objects
        ImageGeneratorInput input = null;
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

        // Set up behavior for mock S3Client
        when(s3Client.putObject(ArgumentMatchers.any(PutObjectRequest.class))).thenReturn(ArgumentMatchers.any());
        URL mockUrl = new URL("http://mock.aws.com");
        when(s3Client.getUrl(ArgumentMatchers.any(), "error-report-2023-08-24T23-40-04-D9KXUZQT.json")).thenReturn(mockUrl);

        // Call theM method being tested
        ImageGeneratorOutput output = generator.handleRequest(input, request);

        Assertions.assertFalse(output.getSuccess());
        Mockito.verify(s3Client,Mockito.times(1)).putObject(ArgumentMatchers.any(PutObjectRequest.class));
    }
    @Test()
    @DisplayName(
            "Test case with Parameters sanity check failed and error json(result) store to error bucket "
    )
    public void testHandleRequest_Success_Sanity_Check_fail() throws Exception {
        // Create mock input objects
        ImageGeneratorInput input = MockImageGenerationInput.getImageGeneratorInput("png");
        input.getParamDictionary().setHeight(8);
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

        // Set up behavior for mock S3Client
        when(s3Client.putObject(ArgumentMatchers.any(PutObjectRequest.class))).thenReturn(ArgumentMatchers.any());
        URL mockUrl = new URL("http://mock.aws.com");
        when(s3Client.getUrl(ArgumentMatchers.any(), "error-report-2023-08-24T23-40-04-D9KXUZQT.json")).thenReturn(mockUrl);

        // Call theM method being tested
        ImageGeneratorOutput output = generator.handleRequest(input, request);

        Assertions.assertFalse(output.getSuccess());
        Mockito.verify(s3Client,Mockito.times(1)).putObject(ArgumentMatchers.any(PutObjectRequest.class));
    }
    @Test()
    @DisplayName(
            "Test case with Unable to store the error report and  null or empty Url received from Aws S3 storage bucket"
    )
    public void testHandleRequest_Success_Aws_Exception() throws Exception {
        // Create mock input objects
        ImageGeneratorInput input = MockImageGenerationInput.getImageGeneratorInput("png");
        input.getParamDictionary().setHeight(8);
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

        // Set up behavior for mock S3Client
        when(s3Client.putObject(ArgumentMatchers.any(PutObjectRequest.class))).thenReturn(ArgumentMatchers.any());
        when(s3Client.getUrl(ArgumentMatchers.any(), "error-report-2023-08-24T23-40-04-D9KXUZQT.json")).thenReturn(null);

        // Call theM method being tested
        ImageGeneratorOutput output = generator.handleRequest(input, request);

        Assertions.assertFalse(output.getSuccess());
        Mockito.verify(s3Client,Mockito.times(1)).putObject(ArgumentMatchers.any(PutObjectRequest.class));
    }
}
