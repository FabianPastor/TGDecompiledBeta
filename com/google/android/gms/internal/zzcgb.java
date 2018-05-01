package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzcgb
{
  private String mValue;
  private final String zzBN;
  private boolean zzbrE;
  private final String zzbrK;
  
  public zzcgb(zzcfw paramzzcfw, String paramString1, String paramString2)
  {
    zzbo.zzcF(paramString1);
    this.zzBN = paramString1;
    this.zzbrK = null;
  }
  
  @WorkerThread
  public final void zzef(String paramString)
  {
    if (zzcjl.zzR(paramString, this.mValue)) {
      return;
    }
    SharedPreferences.Editor localEditor = zzcfw.zza(this.zzbrF).edit();
    localEditor.putString(this.zzBN, paramString);
    localEditor.apply();
    this.mValue = paramString;
  }
  
  @WorkerThread
  public final String zzyL()
  {
    if (!this.zzbrE)
    {
      this.zzbrE = true;
      this.mValue = zzcfw.zza(this.zzbrF).getString(this.zzBN, null);
    }
    return this.mValue;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */