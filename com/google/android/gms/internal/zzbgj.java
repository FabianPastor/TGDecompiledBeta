package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.common.internal.zzbo;
import java.util.ArrayList;
import java.util.Map;

public final class zzbgj<I, O>
  extends zza
{
  public static final zzbgm CREATOR = new zzbgm();
  protected final int zzaIH;
  protected final boolean zzaII;
  protected final int zzaIJ;
  protected final boolean zzaIK;
  protected final String zzaIL;
  protected final int zzaIM;
  protected final Class<? extends zzbgi> zzaIN;
  private String zzaIO;
  private zzbgo zzaIP;
  private zzbgk<I, O> zzaIQ;
  private final int zzaku;
  
  zzbgj(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, String paramString1, int paramInt4, String paramString2, zzbgc paramzzbgc)
  {
    this.zzaku = paramInt1;
    this.zzaIH = paramInt2;
    this.zzaII = paramBoolean1;
    this.zzaIJ = paramInt3;
    this.zzaIK = paramBoolean2;
    this.zzaIL = paramString1;
    this.zzaIM = paramInt4;
    if (paramString2 == null) {
      this.zzaIN = null;
    }
    for (this.zzaIO = null; paramzzbgc == null; this.zzaIO = paramString2)
    {
      this.zzaIQ = null;
      return;
      this.zzaIN = zzbgt.class;
    }
    this.zzaIQ = paramzzbgc.zzrK();
  }
  
  private zzbgj(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, String paramString, int paramInt3, Class<? extends zzbgi> paramClass, zzbgk<I, O> paramzzbgk)
  {
    this.zzaku = 1;
    this.zzaIH = paramInt1;
    this.zzaII = paramBoolean1;
    this.zzaIJ = paramInt2;
    this.zzaIK = paramBoolean2;
    this.zzaIL = paramString;
    this.zzaIM = paramInt3;
    this.zzaIN = paramClass;
    if (paramClass == null) {}
    for (this.zzaIO = null;; this.zzaIO = paramClass.getCanonicalName())
    {
      this.zzaIQ = paramzzbgk;
      return;
    }
  }
  
  public static zzbgj zza(String paramString, int paramInt, zzbgk<?, ?> paramzzbgk, boolean paramBoolean)
  {
    return new zzbgj(7, false, 0, false, paramString, paramInt, null, paramzzbgk);
  }
  
  public static <T extends zzbgi> zzbgj<T, T> zza(String paramString, int paramInt, Class<T> paramClass)
  {
    return new zzbgj(11, false, 11, false, paramString, paramInt, paramClass, null);
  }
  
  public static <T extends zzbgi> zzbgj<ArrayList<T>, ArrayList<T>> zzb(String paramString, int paramInt, Class<T> paramClass)
  {
    return new zzbgj(11, true, 11, true, paramString, paramInt, paramClass, null);
  }
  
  public static zzbgj<Integer, Integer> zzj(String paramString, int paramInt)
  {
    return new zzbgj(0, false, 0, false, paramString, paramInt, null, null);
  }
  
  public static zzbgj<Boolean, Boolean> zzk(String paramString, int paramInt)
  {
    return new zzbgj(6, false, 6, false, paramString, paramInt, null, null);
  }
  
  public static zzbgj<String, String> zzl(String paramString, int paramInt)
  {
    return new zzbgj(7, false, 7, false, paramString, paramInt, null, null);
  }
  
  private String zzrN()
  {
    if (this.zzaIO == null) {
      return null;
    }
    return this.zzaIO;
  }
  
  public final I convertBack(O paramO)
  {
    return (I)this.zzaIQ.convertBack(paramO);
  }
  
  public final String toString()
  {
    zzbg localzzbg = zzbe.zzt(this).zzg("versionCode", Integer.valueOf(this.zzaku)).zzg("typeIn", Integer.valueOf(this.zzaIH)).zzg("typeInArray", Boolean.valueOf(this.zzaII)).zzg("typeOut", Integer.valueOf(this.zzaIJ)).zzg("typeOutArray", Boolean.valueOf(this.zzaIK)).zzg("outputFieldName", this.zzaIL).zzg("safeParcelFieldId", Integer.valueOf(this.zzaIM)).zzg("concreteTypeName", zzrN());
    Class localClass = this.zzaIN;
    if (localClass != null) {
      localzzbg.zzg("concreteType.class", localClass.getCanonicalName());
    }
    if (this.zzaIQ != null) {
      localzzbg.zzg("converterName", this.zzaIQ.getClass().getCanonicalName());
    }
    return localzzbg.toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.zzaku);
    zzd.zzc(paramParcel, 2, this.zzaIH);
    zzd.zza(paramParcel, 3, this.zzaII);
    zzd.zzc(paramParcel, 4, this.zzaIJ);
    zzd.zza(paramParcel, 5, this.zzaIK);
    zzd.zza(paramParcel, 6, this.zzaIL, false);
    zzd.zzc(paramParcel, 7, this.zzaIM);
    zzd.zza(paramParcel, 8, zzrN(), false);
    if (this.zzaIQ == null) {}
    for (Object localObject = null;; localObject = zzbgc.zza(this.zzaIQ))
    {
      zzd.zza(paramParcel, 9, (Parcelable)localObject, paramInt, false);
      zzd.zzI(paramParcel, i);
      return;
    }
  }
  
  public final void zza(zzbgo paramzzbgo)
  {
    this.zzaIP = paramzzbgo;
  }
  
  public final int zzrM()
  {
    return this.zzaIM;
  }
  
  public final boolean zzrO()
  {
    return this.zzaIQ != null;
  }
  
  public final Map<String, zzbgj<?, ?>> zzrP()
  {
    zzbo.zzu(this.zzaIO);
    zzbo.zzu(this.zzaIP);
    return this.zzaIP.zzcJ(this.zzaIO);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */