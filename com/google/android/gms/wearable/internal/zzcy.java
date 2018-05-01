package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.data.DataBufferRef;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.DataItem;

public final class zzcy
  extends DataBufferRef
{
  private final int zzdl;
  
  public zzcy(DataHolder paramDataHolder, int paramInt1, int paramInt2)
  {
    super(paramDataHolder, paramInt1);
    this.zzdl = paramInt2;
  }
  
  public final DataItem getDataItem()
  {
    return new zzdf(this.mDataHolder, this.mDataRow, this.zzdl);
  }
  
  public final int getType()
  {
    return getInteger("event_type");
  }
  
  public final String toString()
  {
    String str1;
    if (getType() == 1) {
      str1 = "changed";
    }
    for (;;)
    {
      String str2 = String.valueOf(getDataItem());
      return String.valueOf(str1).length() + 32 + String.valueOf(str2).length() + "DataEventRef{ type=" + str1 + ", dataitem=" + str2 + " }";
      if (getType() == 2) {
        str1 = "deleted";
      } else {
        str1 = "unknown";
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzcy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */