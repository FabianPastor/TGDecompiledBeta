package com.google.android.gms.gcm;

import android.net.Uri;
import android.os.Bundle;
import java.util.List;

public class TaskParams
{
  private final Bundle extras;
  private final String tag;
  private final List<Uri> zzbgo;
  
  public TaskParams(String paramString)
  {
    this(paramString, null, null);
  }
  
  public TaskParams(String paramString, Bundle paramBundle)
  {
    this(paramString, paramBundle, null);
  }
  
  public TaskParams(String paramString, Bundle paramBundle, List<Uri> paramList)
  {
    this.tag = paramString;
    this.extras = paramBundle;
    this.zzbgo = paramList;
  }
  
  public Bundle getExtras()
  {
    return this.extras;
  }
  
  public String getTag()
  {
    return this.tag;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/TaskParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */