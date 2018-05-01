package com.google.android.gms.internal;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import java.util.Collections;
import java.util.Map;

public abstract class zzp<T>
  implements Comparable<zzp<T>>
{
  private final zzab.zza zzB;
  private final int zzC;
  private final String zzD;
  private final int zzE;
  private final zzu zzF;
  private Integer zzG;
  private zzs zzH;
  private boolean zzI;
  private boolean zzJ;
  private boolean zzK;
  private boolean zzL;
  private zzx zzM;
  private zzc zzN;
  
  public zzp(int paramInt, String paramString, zzu paramzzu)
  {
    zzab.zza localzza;
    if (zzab.zza.zzai)
    {
      localzza = new zzab.zza();
      this.zzB = localzza;
      this.zzI = true;
      this.zzJ = false;
      this.zzK = false;
      this.zzL = false;
      this.zzN = null;
      this.zzC = paramInt;
      this.zzD = paramString;
      this.zzF = paramzzu;
      this.zzM = new zzg();
      if (TextUtils.isEmpty(paramString)) {
        break label118;
      }
      paramString = Uri.parse(paramString);
      if (paramString == null) {
        break label118;
      }
      paramString = paramString.getHost();
      if (paramString == null) {
        break label118;
      }
    }
    label118:
    for (paramInt = paramString.hashCode();; paramInt = 0)
    {
      this.zzE = paramInt;
      return;
      localzza = null;
      break;
    }
  }
  
  public static String zzf()
  {
    String str = String.valueOf("UTF-8");
    if (str.length() != 0) {
      return "application/x-www-form-urlencoded; charset=".concat(str);
    }
    return new String("application/x-www-form-urlencoded; charset=");
  }
  
  public Map<String, String> getHeaders()
    throws zza
  {
    return Collections.emptyMap();
  }
  
  public final int getMethod()
  {
    return this.zzC;
  }
  
  public final String getUrl()
  {
    return this.zzD;
  }
  
  public String toString()
  {
    String str1 = String.valueOf(Integer.toHexString(this.zzE));
    if (str1.length() != 0) {}
    for (str1 = "0x".concat(str1);; str1 = new String("0x"))
    {
      String str2 = String.valueOf(this.zzD);
      String str3 = String.valueOf(zzr.zzS);
      String str4 = String.valueOf(this.zzG);
      return String.valueOf("[ ] ").length() + 3 + String.valueOf(str2).length() + String.valueOf(str1).length() + String.valueOf(str3).length() + String.valueOf(str4).length() + "[ ] " + str2 + " " + str1 + " " + str3 + " " + str4;
    }
  }
  
  public final zzp<?> zza(int paramInt)
  {
    this.zzG = Integer.valueOf(paramInt);
    return this;
  }
  
  public final zzp<?> zza(zzc paramzzc)
  {
    this.zzN = paramzzc;
    return this;
  }
  
  public final zzp<?> zza(zzs paramzzs)
  {
    this.zzH = paramzzs;
    return this;
  }
  
  protected abstract zzt<T> zza(zzn paramzzn);
  
  protected abstract void zza(T paramT);
  
  public final void zzb(zzaa paramzzaa)
  {
    if (this.zzF != null) {
      this.zzF.zzd(paramzzaa);
    }
  }
  
  public final void zzb(String paramString)
  {
    if (zzab.zza.zzai) {
      this.zzB.zza(paramString, Thread.currentThread().getId());
    }
  }
  
  public final int zzc()
  {
    return this.zzE;
  }
  
  final void zzc(String paramString)
  {
    if (this.zzH != null) {
      this.zzH.zzd(this);
    }
    long l;
    if (zzab.zza.zzai)
    {
      l = Thread.currentThread().getId();
      if (Looper.myLooper() != Looper.getMainLooper()) {
        new Handler(Looper.getMainLooper()).post(new zzq(this, paramString, l));
      }
    }
    else
    {
      return;
    }
    this.zzB.zza(paramString, l);
    this.zzB.zzc(toString());
  }
  
  public final String zzd()
  {
    return this.zzD;
  }
  
  public final zzc zze()
  {
    return this.zzN;
  }
  
  public byte[] zzg()
    throws zza
  {
    return null;
  }
  
  public final boolean zzh()
  {
    return this.zzI;
  }
  
  public final int zzi()
  {
    return this.zzM.zza();
  }
  
  public final zzx zzj()
  {
    return this.zzM;
  }
  
  public final void zzk()
  {
    this.zzK = true;
  }
  
  public final boolean zzl()
  {
    return this.zzK;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */