package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzcfy
{
  private final String zzBN;
  private boolean zzaAI;
  private final boolean zzbrD;
  private boolean zzbrE;
  
  public zzcfy(zzcfw paramzzcfw, String paramString, boolean paramBoolean)
  {
    zzbo.zzcF(paramString);
    this.zzBN = paramString;
    this.zzbrD = true;
  }
  
  @WorkerThread
  public final boolean get()
  {
    if (!this.zzbrE)
    {
      this.zzbrE = true;
      this.zzaAI = zzcfw.zza(this.zzbrF).getBoolean(this.zzBN, this.zzbrD);
    }
    return this.zzaAI;
  }
  
  @WorkerThread
  public final void set(boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = zzcfw.zza(this.zzbrF).edit();
    localEditor.putBoolean(this.zzBN, paramBoolean);
    localEditor.apply();
    this.zzaAI = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */