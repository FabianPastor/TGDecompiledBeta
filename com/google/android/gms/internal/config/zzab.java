package com.google.android.gms.internal.config;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.List;

public final class zzab
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzab> CREATOR = new zzac();
  private final String mPackageName;
  private final List<zzl> zzaa;
  private final int zzh;
  private final int zzi;
  private final int zzj;
  private final long zzu;
  private final DataHolder zzv;
  private final String zzw;
  private final String zzx;
  private final String zzy;
  private final List<String> zzz;
  
  public zzab(String paramString1, long paramLong, DataHolder paramDataHolder, String paramString2, String paramString3, String paramString4, List<String> paramList, int paramInt1, List<zzl> paramList1, int paramInt2, int paramInt3)
  {
    this.mPackageName = paramString1;
    this.zzu = paramLong;
    this.zzv = paramDataHolder;
    this.zzw = paramString2;
    this.zzx = paramString3;
    this.zzy = paramString4;
    this.zzz = paramList;
    this.zzh = paramInt1;
    this.zzaa = paramList1;
    this.zzj = paramInt2;
    this.zzi = paramInt3;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.mPackageName, false);
    SafeParcelWriter.writeLong(paramParcel, 3, this.zzu);
    SafeParcelWriter.writeParcelable(paramParcel, 4, this.zzv, paramInt, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzw, false);
    SafeParcelWriter.writeString(paramParcel, 6, this.zzx, false);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzy, false);
    SafeParcelWriter.writeStringList(paramParcel, 8, this.zzz, false);
    SafeParcelWriter.writeInt(paramParcel, 9, this.zzh);
    SafeParcelWriter.writeTypedList(paramParcel, 10, this.zzaa, false);
    SafeParcelWriter.writeInt(paramParcel, 11, this.zzj);
    SafeParcelWriter.writeInt(paramParcel, 12, this.zzi);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */