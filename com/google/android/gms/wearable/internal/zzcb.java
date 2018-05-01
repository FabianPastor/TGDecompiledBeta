package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class zzcb
  extends zza
  implements DataItem
{
  public static final Parcelable.Creator<zzcb> CREATOR = new zzcc();
  private final Uri mUri;
  private final Map<String, DataItemAsset> zzbSE;
  private byte[] zzbdY;
  
  zzcb(Uri paramUri, Bundle paramBundle, byte[] paramArrayOfByte)
  {
    this.mUri = paramUri;
    paramUri = new HashMap();
    paramBundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramUri.put(str, (DataItemAssetParcelable)paramBundle.getParcelable(str));
    }
    this.zzbSE = paramUri;
    this.zzbdY = paramArrayOfByte;
  }
  
  public final Map<String, DataItemAsset> getAssets()
  {
    return this.zzbSE;
  }
  
  public final byte[] getData()
  {
    return this.zzbdY;
  }
  
  public final Uri getUri()
  {
    return this.mUri;
  }
  
  public final boolean isDataValid()
  {
    return true;
  }
  
  public final String toString()
  {
    boolean bool = Log.isLoggable("DataItem", 3);
    StringBuilder localStringBuilder = new StringBuilder("DataItemParcelable[");
    localStringBuilder.append("@");
    localStringBuilder.append(Integer.toHexString(hashCode()));
    if (this.zzbdY == null) {}
    for (Object localObject = "null";; localObject = Integer.valueOf(this.zzbdY.length))
    {
      localObject = String.valueOf(localObject);
      localStringBuilder.append(String.valueOf(localObject).length() + 8 + ",dataSz=" + (String)localObject);
      int i = this.zzbSE.size();
      localStringBuilder.append(23 + ", numAssets=" + i);
      localObject = String.valueOf(this.mUri);
      localStringBuilder.append(String.valueOf(localObject).length() + 6 + ", uri=" + (String)localObject);
      if (bool) {
        break;
      }
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
    localStringBuilder.append("]\n  assets: ");
    localObject = this.zzbSE.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str1 = (String)((Iterator)localObject).next();
      String str2 = String.valueOf(this.zzbSE.get(str1));
      localStringBuilder.append(String.valueOf(str1).length() + 7 + String.valueOf(str2).length() + "\n    " + str1 + ": " + str2);
    }
    localStringBuilder.append("\n  ]");
    return localStringBuilder.toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, getUri(), paramInt, false);
    Bundle localBundle = new Bundle();
    localBundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
    Iterator localIterator = this.zzbSE.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localBundle.putParcelable((String)localEntry.getKey(), new DataItemAssetParcelable((DataItemAsset)localEntry.getValue()));
    }
    zzd.zza(paramParcel, 4, localBundle, false);
    zzd.zza(paramParcel, 5, getData(), false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzcb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */