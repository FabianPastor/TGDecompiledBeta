package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

@Deprecated
public final class zza
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zza> CREATOR = new zzb();
  private String name;
  private String zze;
  private String zzf;
  private String zzg;
  private String zzh;
  private String zzi;
  private String zzj;
  private String zzk;
  private String zzl;
  private boolean zzm;
  private String zzn;
  
  zza() {}
  
  zza(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, boolean paramBoolean, String paramString10)
  {
    this.name = paramString1;
    this.zze = paramString2;
    this.zzf = paramString3;
    this.zzg = paramString4;
    this.zzh = paramString5;
    this.zzi = paramString6;
    this.zzj = paramString7;
    this.zzk = paramString8;
    this.zzl = paramString9;
    this.zzm = paramBoolean;
    this.zzn = paramString10;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.name, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zze, false);
    SafeParcelWriter.writeString(paramParcel, 4, this.zzf, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzg, false);
    SafeParcelWriter.writeString(paramParcel, 6, this.zzh, false);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzi, false);
    SafeParcelWriter.writeString(paramParcel, 8, this.zzj, false);
    SafeParcelWriter.writeString(paramParcel, 9, this.zzk, false);
    SafeParcelWriter.writeString(paramParcel, 10, this.zzl, false);
    SafeParcelWriter.writeBoolean(paramParcel, 11, this.zzm);
    SafeParcelWriter.writeString(paramParcel, 12, this.zzn, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */