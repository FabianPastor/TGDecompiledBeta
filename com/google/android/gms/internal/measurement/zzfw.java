package com.google.android.gms.internal.measurement;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.Preconditions;

public final class zzfw
{
  private String value;
  private boolean zzako;
  private final String zzaku;
  private final String zznt;
  
  public zzfw(zzfr paramzzfr, String paramString1, String paramString2)
  {
    Preconditions.checkNotEmpty(paramString1);
    this.zznt = paramString1;
    this.zzaku = null;
  }
  
  public final void zzbn(String paramString)
  {
    if (zzjv.zzs(paramString, this.value)) {}
    for (;;)
    {
      return;
      SharedPreferences.Editor localEditor = zzfr.zza(this.zzakp).edit();
      localEditor.putString(this.zznt, paramString);
      localEditor.apply();
      this.value = paramString;
    }
  }
  
  public final String zzjc()
  {
    if (!this.zzako)
    {
      this.zzako = true;
      this.value = zzfr.zza(this.zzakp).getString(this.zznt, null);
    }
    return this.value;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */