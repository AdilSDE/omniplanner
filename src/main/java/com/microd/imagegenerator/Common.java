package com.microd.imagegenerator;

import java.io.InputStream;
import java.util.Random;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Common {

  private static final Logger log = Logger.getLogger(Common.class.getName());
  
	public static void Log(String message) {
// 		System.out.println(instanceID + ' ' + message);
    log.info(" LOGGER.info " + message);
	}

  public static void Error(String message) {
//		System.err.println(instanceID + " ERR: " + message);
    log.severe(" LOGGER.severe " + message);
	}

  public static void Error(String message, RoomplannerImageGeneratorException e) {
    log.log(Level.SEVERE, message, e);
//    log.log(Level.SEVERE, instanceID + " with EXC " + message, e);
	}

  // generate random alphanumerical string of given length
  static String getSaltString(int length) {
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < length) {
      int index = (int) (rnd.nextFloat() * SALTCHARS.length());
      salt.append(SALTCHARS.charAt(index));
    }
    String saltStr = salt.toString();
    return saltStr;
  }
  
  // retrieve Implementation-Version from MANIFEST.MF (defined in pom.xml)
	public static String GetAppVersion() {
    try {
      Manifest man = getManifest(ImageGeneratorApplication.class);
      Attributes attr = man.getMainAttributes();
      String version = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);  //"Implementation-Version");
      
      String build = System.getenv("BUILD_NAME");
      if (build != null)
        version = build + '-' + version;
      
      return version;
    } catch (Exception e) {
      Error("Error obtaining version number from MANIFEST: " + e.getMessage());
      return "?.?";
    }
  }
  
  static Manifest getManifest(Class<?> clz) {
    String resource = "/" + clz.getName().replace(".", "/") + ".class";
    String fullPath = clz.getResource(resource).toString();
    String archivePath = fullPath.substring(0, fullPath.length() - resource.length());
    if (archivePath.endsWith("\\WEB-INF\\classes") || archivePath.endsWith("/WEB-INF/classes")) {
      archivePath = archivePath.substring(0, archivePath.length() - "/WEB-INF/classes".length()); // Required for wars
    }
    try (InputStream input = new java.net.URL(archivePath + "/META-INF/MANIFEST.MF").openStream()) {
      return new Manifest(input);
    } catch (Exception e) {
      throw new RuntimeException("Loading MANIFEST for class " + clz + " failed!", e);
    }
  }  
  
  /**
  * Limit the string to a certain number of characters, adding "..." if it was truncated
  * 
  * @param value
  *        The string to limit.
  * @param length
  *        the length to limit to (as an int).
  * @return The limited string.
  */
  public static String limit(String value, int length)
  {
    if (value == null || value.length() == 0)
      return "";
    StringBuilder buf = new StringBuilder(value);
    if (buf.length() > length)
    {
      buf.setLength(length);
      buf.append("...");
    }

    return buf.toString();
  }  
}
