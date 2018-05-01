package com.google.android.gms.internal.measurement;

import com.google.android.gms.common.internal.Preconditions;
import java.util.List;
import java.util.Map;

final class zzfn
  implements Runnable
{
  private final String packageName;
  private final int status;
  private final zzfm zzajh;
  private final Throwable zzaji;
  private final byte[] zzajj;
  private final Map<String, List<String>> zzajk;
  
  private zzfn(String paramString, zzfm paramzzfm, int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte, Map<String, List<String>> paramMap)
  {
    Preconditions.checkNotNull(paramzzfm);
    this.zzajh = paramzzfm;
    this.status = paramInt;
    this.zzaji = paramThrowable;
    this.zzajj = paramArrayOfByte;
    this.packageName = paramString;
    this.zzajk = paramMap;
  }
  
  public final void run()
  {
    this.zzajh.zza(this.packageName, this.status, this.zzaji, this.zzajj, this.zzajk);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */