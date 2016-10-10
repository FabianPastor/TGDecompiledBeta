package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.R.string;

public class zzaj
{
  private final Resources Dc;
  private final String Dd;
  
  public zzaj(Context paramContext)
  {
    zzac.zzy(paramContext);
    this.Dc = paramContext.getResources();
    this.Dd = this.Dc.getResourcePackageName(R.string.common_google_play_services_unknown_issue);
  }
  
  public String getString(String paramString)
  {
    int i = this.Dc.getIdentifier(paramString, "string", this.Dd);
    if (i == 0) {
      return null;
    }
    return this.Dc.getString(i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzaj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */