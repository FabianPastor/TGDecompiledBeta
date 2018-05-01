package com.google.android.gms.internal;

import java.io.UnsupportedEncodingException;

public class zzar
  extends zzp<String>
{
  private final zzv<String> zzaD;
  
  public zzar(int paramInt, String paramString, zzv<String> paramzzv, zzu paramzzu)
  {
    super(paramInt, paramString, paramzzu);
    this.zzaD = paramzzv;
  }
  
  protected final zzt<String> zza(zzn paramzzn)
  {
    try
    {
      String str1 = new String(paramzzn.data, zzam.zza(paramzzn.zzy));
      return zzt.zza(str1, zzam.zzb(paramzzn));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      for (;;)
      {
        String str2 = new String(paramzzn.data);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */