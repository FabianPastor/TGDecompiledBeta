package com.googlecode.mp4parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.logging.Logger;

public class Version
{
  private static final Logger LOG = Logger.getLogger(Version.class.getName());
  public static final String VERSION;
  
  static
  {
    Object localObject = new LineNumberReader(new InputStreamReader(Version.class.getResourceAsStream("/version.txt")));
    try
    {
      localObject = ((LineNumberReader)localObject).readLine();
      VERSION = (String)localObject;
      return;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        LOG.warning(localIOException.getMessage());
        String str = "unknown";
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/Version.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */