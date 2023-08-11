package com.microd.imagegenerator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.*;


@SpringBootApplication
@RestController
public class ImageGeneratorApplication /*extends SpringBootServletInitializer*/ {

  @Autowired
  private HttpServletRequest request;

	public static void main(String[] args) {

        SpringApplication.run(ImageGeneratorApplication.class, args);
	}
	
  @CrossOrigin(origins = "*", maxAge = 3600)
  @RequestMapping(method = RequestMethod.POST, value="/generate")
  @ResponseBody
  public ImageGeneratorOutput generate(@RequestBody ImageGeneratorInput param) {
		return new Generator().handleRequest(param, request);
  }

  /** Readiness check: GCP built in check, specified in yaml */
  @RequestMapping(method = RequestMethod.GET, value="/readiness_check")
  public String readiness_check() {
//		System.out.println("readiness_check test ok.");
		return "OK";
  }

  /** Liveness check: GCP built in check, specified in yaml */
  @RequestMapping(method = RequestMethod.GET, value="/liveness_check")
  public String liveness_check() {
//		System.out.println("liveness_check test ok.");
		return "OK";
  }

  /** For Stackdriver monitoring */
//  static int xxx_counter = 0;
//  @CrossOrigin(origins = "*", maxAge = 3600)
  @RequestMapping(method = RequestMethod.GET, value="/uptime_check")
  public String uptime_check() {
    return "OK";
/*		System.out.println("uptime_check test " + xxx_counter);
    xxx_counter++;
    if (xxx_counter%20 < 15)
      return "OK";
    else
      return "uptime_check failed:"+xxx_counter;*/
  }

  /** For manual testing */
  @CrossOrigin(origins = "*", maxAge = 3600)
  @RequestMapping(method = RequestMethod.GET, value="/health")
  public String health() {
		System.out.println("health test ok.");
		return "OK";
  }

  /** For debugging */
  @CrossOrigin(origins = "*", maxAge = 3600)
  @RequestMapping(method = RequestMethod.GET, value="/gc")
  public String gc() {
		System.out.println("********** gb (garbage collection) called");
    System.gc();
		return "OK";
  }

  /** For debugging */
  @CrossOrigin(origins = "*", maxAge = 3600)
  @RequestMapping(method = RequestMethod.GET, value="/tmplist")
  public String tmplist() {
		System.out.println("********** tmplist called");
    
    String dirName = "/tmp/";
    try {
      java.nio.file.Files.list(new java.io.File(dirName).toPath())
              .limit(20)
              .forEach(path -> {
                System.out.println(path);    
              });
    } catch (IOException ex) {
      Logger.getLogger(ImageGeneratorApplication.class.getName()).log(Level.SEVERE, null, ex);
    }
		return "OK";
  }
}
