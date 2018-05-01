package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.R.string;

public final class zzca
{
  private final Resources zzgbt;
  private final String zzgbu;
  
  public zzca(Context paramContext)
  {
    zzbq.checkNotNull(paramContext);
    this.zzgbt = paramContext.getResources();
    this.zzgbu = this.zzgbt.getResourcePackageName(R.string.common_google_play_services_unknown_issue);
  }
  
  public final String getString(String paramString)
  {
    int i = this.zzgbt.getIdentifier(paramString, "string", this.zzgbu);
    if (i == 0) {
      return null;
    }
    return this.zzgbt.getString(i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzca.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */