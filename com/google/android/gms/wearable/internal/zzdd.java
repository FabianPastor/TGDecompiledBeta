package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class zzdd
  extends AbstractSafeParcelable
  implements DataItem
{
  public static final Parcelable.Creator<zzdd> CREATOR = new zzde();
  private byte[] data;
  private final Uri uri;
  private final Map<String, DataItemAsset> zzdo;
  
  zzdd(Uri paramUri, Bundle paramBundle, byte[] paramArrayOfByte)
  {
    this.uri = paramUri;
    HashMap localHashMap = new HashMap();
    paramBundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
    paramUri = paramBundle.keySet().iterator();
    while (paramUri.hasNext())
    {
      String str = (String)paramUri.next();
      localHashMap.put(str, (DataItemAssetParcelable)paramBundle.getParcelable(str));
    }
    this.zzdo = localHashMap;
    this.data = paramArrayOfByte;
  }
  
  public final byte[] getData()
  {
    return this.data;
  }
  
  public final Uri getUri()
  {
    return this.uri;
  }
  
  public final String toString()
  {
    boolean bool = Log.isLoggable("DataItem", 3);
    StringBuilder localStringBuilder = new StringBuilder("DataItemParcelable[");
    localStringBuilder.append("@");
    localStringBuilder.append(Integer.toHexString(hashCode()));
    if (this.data == null)
    {
      localObject = "null";
      localObject = String.valueOf(localObject);
      localStringBuilder.append(String.valueOf(localObject).length() + 8 + ",dataSz=" + (String)localObject);
      int i = this.zzdo.size();
      localStringBuilder.append(23 + ", numAssets=" + i);
      localObject = String.valueOf(this.uri);
      localStringBuilder.append(String.valueOf(localObject).length() + 6 + ", uri=" + (String)localObject);
      if (bool) {
        break label195;
      }
      localStringBuilder.append("]");
    }
    for (Object localObject = localStringBuilder.toString();; localObject = localStringBuilder.toString())
    {
      return (String)localObject;
      localObject = Integer.valueOf(this.data.length);
      break;
      label195:
      localStringBuilder.append("]\n  assets: ");
      localObject = this.zzdo.keySet().iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str1 = (String)((Iterator)localObject).next();
        String str2 = String.valueOf(this.zzdo.get(str1));
        localStringBuilder.append(String.valueOf(str1).length() + 7 + String.valueOf(str2).length() + "\n    " + str1 + ": " + str2);
      }
      localStringBuilder.append("\n  ]");
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeParcelable(paramParcel, 2, getUri(), paramInt, false);
    Bundle localBundle = new Bundle();
    localBundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
    Iterator localIterator = this.zzdo.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localBundle.putParcelable((String)localEntry.getKey(), new DataItemAssetParcelable((DataItemAsset)localEntry.getValue()));
    }
    SafeParcelWriter.writeBundle(paramParcel, 4, localBundle, false);
    SafeParcelWriter.writeByteArray(paramParcel, 5, getData(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzdd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */