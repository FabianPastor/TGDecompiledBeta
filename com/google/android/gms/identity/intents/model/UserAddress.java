package com.google.android.gms.identity.intents.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class UserAddress
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<UserAddress> CREATOR = new zzb();
  private String name;
  private String zzk;
  private String zzl;
  private String zzm;
  private String zzn;
  private String zzo;
  private String zzp;
  private String zzq;
  private String zzr;
  private String zzs;
  private String zzt;
  private String zzu;
  private boolean zzv;
  private String zzw;
  private String zzx;
  
  UserAddress() {}
  
  UserAddress(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, String paramString12, boolean paramBoolean, String paramString13, String paramString14)
  {
    this.name = paramString1;
    this.zzl = paramString2;
    this.zzm = paramString3;
    this.zzn = paramString4;
    this.zzo = paramString5;
    this.zzp = paramString6;
    this.zzq = paramString7;
    this.zzr = paramString8;
    this.zzk = paramString9;
    this.zzs = paramString10;
    this.zzt = paramString11;
    this.zzu = paramString12;
    this.zzv = paramBoolean;
    this.zzw = paramString13;
    this.zzx = paramString14;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.name, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzl, false);
    SafeParcelWriter.writeString(paramParcel, 4, this.zzm, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzn, false);
    SafeParcelWriter.writeString(paramParcel, 6, this.zzo, false);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzp, false);
    SafeParcelWriter.writeString(paramParcel, 8, this.zzq, false);
    SafeParcelWriter.writeString(paramParcel, 9, this.zzr, false);
    SafeParcelWriter.writeString(paramParcel, 10, this.zzk, false);
    SafeParcelWriter.writeString(paramParcel, 11, this.zzs, false);
    SafeParcelWriter.writeString(paramParcel, 12, this.zzt, false);
    SafeParcelWriter.writeString(paramParcel, 13, this.zzu, false);
    SafeParcelWriter.writeBoolean(paramParcel, 14, this.zzv);
    SafeParcelWriter.writeString(paramParcel, 15, this.zzw, false);
    SafeParcelWriter.writeString(paramParcel, 16, this.zzx, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/identity/intents/model/UserAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */