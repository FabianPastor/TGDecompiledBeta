package com.google.android.gms.internal;

import java.io.UnsupportedEncodingException;

public class zzab
  extends zzk<String>
{
  private final zzm.zzb<String> zzcg;
  
  public zzab(int paramInt, String paramString, zzm.zzb<String> paramzzb, zzm.zza paramzza)
  {
    super(paramInt, paramString, paramzza);
    this.zzcg = paramzzb;
  }
  
  protected zzm<String> zza(zzi paramzzi)
  {
    try
    {
      String str1 = new String(paramzzi.data, zzx.zza(paramzzi.zzz));
      return zzm.zza(str1, zzx.zzb(paramzzi));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      for (;;)
      {
        String str2 = new String(paramzzi.data);
      }
    }
  }
  
  protected void zzi(String paramString)
  {
    this.zzcg.zzb(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */