package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.R.string;

public final class zzby
{
  private final Resources zzaIw;
  private final String zzaIx;
  
  public zzby(Context paramContext)
  {
    zzbo.zzu(paramContext);
    this.zzaIw = paramContext.getResources();
    this.zzaIx = this.zzaIw.getResourcePackageName(R.string.common_google_play_services_unknown_issue);
  }
  
  public final String getString(String paramString)
  {
    int i = this.zzaIw.getIdentifier(paramString, "string", this.zzaIx);
    if (i == 0) {
      return null;
    }
    return this.zzaIw.getString(i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzby.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */