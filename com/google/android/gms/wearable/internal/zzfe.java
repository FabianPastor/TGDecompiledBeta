package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.MessageEvent;

public final class zzfe
  extends AbstractSafeParcelable
  implements MessageEvent
{
  public static final Parcelable.Creator<zzfe> CREATOR = new zzff();
  private final byte[] data;
  private final String zzcl;
  private final int zzeh;
  private final String zzek;
  
  public zzfe(int paramInt, String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    this.zzeh = paramInt;
    this.zzcl = paramString1;
    this.data = paramArrayOfByte;
    this.zzek = paramString2;
  }
  
  public final byte[] getData()
  {
    return this.data;
  }
  
  public final String getPath()
  {
    return this.zzcl;
  }
  
  public final int getRequestId()
  {
    return this.zzeh;
  }
  
  public final String getSourceNodeId()
  {
    return this.zzek;
  }
  
  public final String toString()
  {
    int i = this.zzeh;
    String str = this.zzcl;
    if (this.data == null) {}
    for (Object localObject = "null";; localObject = Integer.valueOf(this.data.length))
    {
      localObject = String.valueOf(localObject);
      return String.valueOf(str).length() + 43 + String.valueOf(localObject).length() + "MessageEventParcelable[" + i + "," + str + ", size=" + (String)localObject + "]";
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, getRequestId());
    SafeParcelWriter.writeString(paramParcel, 3, getPath(), false);
    SafeParcelWriter.writeByteArray(paramParcel, 4, getData(), false);
    SafeParcelWriter.writeString(paramParcel, 5, getSourceNodeId(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzfe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */