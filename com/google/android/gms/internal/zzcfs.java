package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;
import java.util.List;
import java.util.Map;

@WorkerThread
final class zzcfs
  implements Runnable
{
  private final String mPackageName;
  private final int zzLe;
  private final Throwable zzaaS;
  private final zzcfr zzbra;
  private final byte[] zzbrb;
  private final Map<String, List<String>> zzbrc;
  
  private zzcfs(String paramString, zzcfr paramzzcfr, int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte, Map<String, List<String>> paramMap)
  {
    zzbo.zzu(paramzzcfr);
    this.zzbra = paramzzcfr;
    this.zzLe = paramInt;
    this.zzaaS = paramThrowable;
    this.zzbrb = paramArrayOfByte;
    this.mPackageName = paramString;
    this.zzbrc = paramMap;
  }
  
  public final void run()
  {
    this.zzbra.zza(this.mPackageName, this.zzLe, this.zzaaS, this.zzbrb, this.zzbrc);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */