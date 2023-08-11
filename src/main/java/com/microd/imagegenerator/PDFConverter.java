package com.microd.imagegenerator;

import java.io.File;
import org.apache.batik.apps.rasterizer.Main;
import org.apache.batik.apps.rasterizer.SVGConverterSource;
/**
 *
 * @author Mary
 * 
 *
 * see
  * http://www.docjar.org/html/api/org/apache/batik/apps/rasterizer/Main.java.html
  *
	*  where, as options:
	*  
	*  -d dir|file : specifies the output directory,
	*     or the output file if there is only a single input file, 
	*   
	*  -m mime-type : specifies the output MIME type, which should be one
	*     of image/png, image/jpeg, image/tiff or application/pdf, 
	*     
	*  -w width : specifies the output width as a floating point value, 
	*   
	*  -h height : specifies the output height as a floating point value, 
	*   
	*  -maxw width : specifies the maximum output width as a floating point value, 
	*   
	*  -maxh height : specifies the maximum output height as a floating point value, 
	*   
	*  -a x , y , width , height : specifies the area of interest (as floating point values) of 
	*     the SVG file to rasterize (and if not specified, will be determined by the width 
	*     / height / viewBox attributes if specified in the document, and be 0,0,400,400 otherwise), 
	*     
	*  -bg alpha . red . green . blue : specifies the background fill color as an ARGB
	*     quadruple, where each component is an integer in the range 0-255,
	*     
	*  -cssMedia media : specifies the CSS media type used for matching CSS rules,
	*   
	*  -cssAlternate file|uri : specifies the CSS alternate stylesheet
	*    to use, 
	*    
	*  -cssUser file|uri : specifies the CSS user stylesheet to
	*     use in addition to any other referenced or embedded stylesheets,
	*     
	*  -font-family defaultFontFamily : specifies default font family to
	*     be used when none is specified, 
	*     
	*  -lang language-code : specifies the RFC 3066 language code to use, 
	*  
	*  -q quality : specifies the quality of the output image, as a floating 
	*     point number in the range 0 < quality < 1 when generating JPEG images, 
	*     
	*  -indexed 1|2|4|8 : specifies the number of bits per pixel of the output
	*     image, using an adaptive pallete, resulting in an indexed image
	*     when generating PNG images, 
	*     
	*  -dpi resolution : specifies the resolution of the output image in dots per inch, 
	*  
	*  -validate : specifies that the source SVG files must be validated against
	*     their DTDs, 
	*     
	*  -onload : specifies that the SVG files should be rasterized after 
	*     dispatching the SVG load event, 
	*      
	*  -snapshotTime : specifies the document time that should be seeked to before
	*     rasterizing the document, implying -onload, 
	*     
	*  -scriptSecurityOff :  specifies that any security checks on the scripts running as a
	*     result of dispatching the SVG load event will be bypassed,
	*      
	* -anyScriptOrigin : specifies that scripts can be loaded from any
	*     location, while by default they can only be loaded from the same
	*     location as the document, and 
	* 
	* -scripts allowed-script-types : specifies a list of script types (i.e., values for the type
	*     attribute on script elements) that should be loaded.
  *
 */
public class PDFConverter extends Main {
  public boolean success = false;
  public String error = "";
  
  public PDFConverter(String[] args) {
    super(args);
  }

  @Override
  public void onSourceTranscodingSuccess(SVGConverterSource source, File dest) {
    super.onSourceTranscodingSuccess(source, dest);
    success = true;
  }

  @Override
  public boolean proceedOnSourceTranscodingFailure(SVGConverterSource source, File dest, String errorCode) {
    success = false;
    error = errorCode;
    return super.proceedOnSourceTranscodingFailure(source, dest, errorCode);
  }
}
