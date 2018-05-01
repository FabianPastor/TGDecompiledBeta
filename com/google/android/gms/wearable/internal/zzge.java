package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.List;

public final class zzge
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzge> CREATOR = new zzgf();
  private final int statusCode;
  private final long zzep;
  private final List<zzfs> zzer;
  
  public zzge(int paramInt, long paramLong, List<zzfs> paramList)
  {
    this.statusCode = paramInt;
    this.zzep = paramLong;
    this.zzer = paramList;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.statusCode);
    SafeParcelWriter.writeLong(paramParcel, 3, this.zzep);
    SafeParcelWriter.writeTypedList(paramParcel, 4, this.zzer, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */