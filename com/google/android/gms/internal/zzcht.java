package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;
import java.util.List;
import java.util.Map;

final class zzcht
  implements Runnable
{
  private final String mPackageName;
  private final int zzcbc;
  private final Throwable zzdfl;
  private final zzchs zzjch;
  private final byte[] zzjci;
  private final Map<String, List<String>> zzjcj;
  
  private zzcht(String paramString, zzchs paramzzchs, int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte, Map<String, List<String>> paramMap)
  {
    zzbq.checkNotNull(paramzzchs);
    this.zzjch = paramzzchs;
    this.zzcbc = paramInt;
    this.zzdfl = paramThrowable;
    this.zzjci = paramArrayOfByte;
    this.mPackageName = paramString;
    this.zzjcj = paramMap;
  }
  
  public final void run()
  {
    this.zzjch.zza(this.mPackageName, this.zzcbc, this.zzdfl, this.zzjci, this.zzjcj);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcht.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */