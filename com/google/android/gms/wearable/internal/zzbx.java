package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.DataItemAsset;

public final class zzbx
  implements DataItemAsset
{
  private final String zzBN;
  private final String zzIi;
  
  public zzbx(DataItemAsset paramDataItemAsset)
  {
    this.zzIi = paramDataItemAsset.getId();
    this.zzBN = paramDataItemAsset.getDataItemKey();
  }
  
  public final String getDataItemKey()
  {
    return this.zzBN;
  }
  
  public final String getId()
  {
    return this.zzIi;
  }
  
  public final boolean isDataValid()
  {
    return true;
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("DataItemAssetEntity[");
    localStringBuilder.append("@");
    localStringBuilder.append(Integer.toHexString(hashCode()));
    if (this.zzIi == null) {
      localStringBuilder.append(",noid");
    }
    for (;;)
    {
      localStringBuilder.append(", key=");
      localStringBuilder.append(this.zzBN);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
      localStringBuilder.append(",");
      localStringBuilder.append(this.zzIi);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzbx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */