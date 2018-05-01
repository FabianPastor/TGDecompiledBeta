package com.google.android.gms.internal.measurement;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzec
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzec> CREATOR = new zzed();
  public final String packageName;
  public final String zzadh;
  public final String zzadj;
  public final long zzadn;
  public final String zzado;
  public final long zzadp;
  public final long zzadq;
  public final boolean zzadr;
  public final long zzads;
  public final boolean zzadt;
  public final boolean zzadu;
  public final String zzaef;
  public final boolean zzaeg;
  public final long zzaeh;
  public final int zzaei;
  public final boolean zzaej;
  public final String zztc;
  
  zzec(String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, long paramLong2, long paramLong3, String paramString5, boolean paramBoolean1, boolean paramBoolean2, String paramString6, long paramLong4, long paramLong5, int paramInt, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    Preconditions.checkNotEmpty(paramString1);
    this.packageName = paramString1;
    paramString1 = paramString2;
    if (TextUtils.isEmpty(paramString2)) {
      paramString1 = null;
    }
    this.zzadh = paramString1;
    this.zztc = paramString3;
    this.zzadn = paramLong1;
    this.zzado = paramString4;
    this.zzadp = paramLong2;
    this.zzadq = paramLong3;
    this.zzaef = paramString5;
    this.zzadr = paramBoolean1;
    this.zzaeg = paramBoolean2;
    this.zzadj = paramString6;
    this.zzads = paramLong4;
    this.zzaeh = paramLong5;
    this.zzaei = paramInt;
    this.zzadt = paramBoolean3;
    this.zzadu = paramBoolean4;
    this.zzaej = paramBoolean5;
  }
  
  zzec(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2, String paramString5, boolean paramBoolean1, boolean paramBoolean2, long paramLong3, String paramString6, long paramLong4, long paramLong5, int paramInt, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    this.packageName = paramString1;
    this.zzadh = paramString2;
    this.zztc = paramString3;
    this.zzadn = paramLong3;
    this.zzado = paramString4;
    this.zzadp = paramLong1;
    this.zzadq = paramLong2;
    this.zzaef = paramString5;
    this.zzadr = paramBoolean1;
    this.zzaeg = paramBoolean2;
    this.zzadj = paramString6;
    this.zzads = paramLong4;
    this.zzaeh = paramLong5;
    this.zzaei = paramInt;
    this.zzadt = paramBoolean3;
    this.zzadu = paramBoolean4;
    this.zzaej = paramBoolean5;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.packageName, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzadh, false);
    SafeParcelWriter.writeString(paramParcel, 4, this.zztc, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzado, false);
    SafeParcelWriter.writeLong(paramParcel, 6, this.zzadp);
    SafeParcelWriter.writeLong(paramParcel, 7, this.zzadq);
    SafeParcelWriter.writeString(paramParcel, 8, this.zzaef, false);
    SafeParcelWriter.writeBoolean(paramParcel, 9, this.zzadr);
    SafeParcelWriter.writeBoolean(paramParcel, 10, this.zzaeg);
    SafeParcelWriter.writeLong(paramParcel, 11, this.zzadn);
    SafeParcelWriter.writeString(paramParcel, 12, this.zzadj, false);
    SafeParcelWriter.writeLong(paramParcel, 13, this.zzads);
    SafeParcelWriter.writeLong(paramParcel, 14, this.zzaeh);
    SafeParcelWriter.writeInt(paramParcel, 15, this.zzaei);
    SafeParcelWriter.writeBoolean(paramParcel, 16, this.zzadt);
    SafeParcelWriter.writeBoolean(paramParcel, 17, this.zzadu);
    SafeParcelWriter.writeBoolean(paramParcel, 18, this.zzaej);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */