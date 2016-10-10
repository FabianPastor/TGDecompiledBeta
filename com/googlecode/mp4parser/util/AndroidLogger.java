package com.googlecode.mp4parser.util;

import android.util.Log;

public class AndroidLogger
  extends Logger
{
  private static final String TAG = "isoparser";
  String name;
  
  public AndroidLogger(String paramString)
  {
    this.name = paramString;
  }
  
  public void logDebug(String paramString)
  {
    Log.d("isoparser", this.name + ":" + paramString);
  }
  
  public void logError(String paramString)
  {
    Log.e("isoparser", this.name + ":" + paramString);
  }
  
  public void logWarn(String paramString)
  {
    Log.w("isoparser", this.name + ":" + paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/AndroidLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */