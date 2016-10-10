package com.googlecode.mp4parser.util;

public abstract class Logger
{
  public static Logger getLogger(Class paramClass)
  {
    if (System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik")) {
      return new AndroidLogger(paramClass.getSimpleName());
    }
    return new JuliLogger(paramClass.getSimpleName());
  }
  
  public abstract void logDebug(String paramString);
  
  public abstract void logError(String paramString);
  
  public abstract void logWarn(String paramString);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/Logger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */