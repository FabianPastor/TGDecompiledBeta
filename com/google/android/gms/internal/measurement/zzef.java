package com.google.android.gms.internal.measurement;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzef
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzef> CREATOR = new zzeg();
  public String packageName;
  public String zzaek;
  public zzjs zzael;
  public long zzaem;
  public boolean zzaen;
  public String zzaeo;
  public zzeu zzaep;
  public long zzaeq;
  public zzeu zzaer;
  public long zzaes;
  public zzeu zzaet;
  
  zzef(zzef paramzzef)
  {
    Preconditions.checkNotNull(paramzzef);
    this.packageName = paramzzef.packageName;
    this.zzaek = paramzzef.zzaek;
    this.zzael = paramzzef.zzael;
    this.zzaem = paramzzef.zzaem;
    this.zzaen = paramzzef.zzaen;
    this.zzaeo = paramzzef.zzaeo;
    this.zzaep = paramzzef.zzaep;
    this.zzaeq = paramzzef.zzaeq;
    this.zzaer = paramzzef.zzaer;
    this.zzaes = paramzzef.zzaes;
    this.zzaet = paramzzef.zzaet;
  }
  
  zzef(String paramString1, String paramString2, zzjs paramzzjs, long paramLong1, boolean paramBoolean, String paramString3, zzeu paramzzeu1, long paramLong2, zzeu paramzzeu2, long paramLong3, zzeu paramzzeu3)
  {
    this.packageName = paramString1;
    this.zzaek = paramString2;
    this.zzael = paramzzjs;
    this.zzaem = paramLong1;
    this.zzaen = paramBoolean;
    this.zzaeo = paramString3;
    this.zzaep = paramzzeu1;
    this.zzaeq = paramLong2;
    this.zzaer = paramzzeu2;
    this.zzaes = paramLong3;
    this.zzaet = paramzzeu3;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.packageName, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzaek, false);
    SafeParcelWriter.writeParcelable(paramParcel, 4, this.zzael, paramInt, false);
    SafeParcelWriter.writeLong(paramParcel, 5, this.zzaem);
    SafeParcelWriter.writeBoolean(paramParcel, 6, this.zzaen);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzaeo, false);
    SafeParcelWriter.writeParcelable(paramParcel, 8, this.zzaep, paramInt, false);
    SafeParcelWriter.writeLong(paramParcel, 9, this.zzaeq);
    SafeParcelWriter.writeParcelable(paramParcel, 10, this.zzaer, paramInt, false);
    SafeParcelWriter.writeLong(paramParcel, 11, this.zzaes);
    SafeParcelWriter.writeParcelable(paramParcel, 12, this.zzaet, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzef.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */