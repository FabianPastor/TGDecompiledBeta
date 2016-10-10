package com.googlecode.mp4parser.util;

import java.util.logging.Level;

public class JuliLogger
  extends Logger
{
  java.util.logging.Logger logger;
  
  public JuliLogger(String paramString)
  {
    this.logger = java.util.logging.Logger.getLogger(paramString);
  }
  
  public void logDebug(String paramString)
  {
    this.logger.log(Level.FINE, paramString);
  }
  
  public void logError(String paramString)
  {
    this.logger.log(Level.SEVERE, paramString);
  }
  
  public void logWarn(String paramString)
  {
    this.logger.log(Level.WARNING, paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/JuliLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */