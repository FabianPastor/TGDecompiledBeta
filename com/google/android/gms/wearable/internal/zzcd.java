package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.zzc;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class zzcd
  extends zzc
  implements DataItem
{
  private final int zzbcP;
  
  public zzcd(DataHolder paramDataHolder, int paramInt1, int paramInt2)
  {
    super(paramDataHolder, paramInt1);
    this.zzbcP = paramInt2;
  }
  
  public final Map<String, DataItemAsset> getAssets()
  {
    HashMap localHashMap = new HashMap(this.zzbcP);
    int i = 0;
    while (i < this.zzbcP)
    {
      zzbz localzzbz = new zzbz(this.zzaCX, this.zzaFx + i);
      if (localzzbz.getDataItemKey() != null) {
        localHashMap.put(localzzbz.getDataItemKey(), localzzbz);
      }
      i += 1;
    }
    return localHashMap;
  }
  
  public final byte[] getData()
  {
    return getByteArray("data");
  }
  
  public final Uri getUri()
  {
    return Uri.parse(getString("path"));
  }
  
  public final DataItem setData(byte[] paramArrayOfByte)
  {
    throw new UnsupportedOperationException();
  }
  
  public final String toString()
  {
    boolean bool = Log.isLoggable("DataItem", 3);
    Object localObject1 = getData();
    Object localObject2 = getAssets();
    StringBuilder localStringBuilder = new StringBuilder("DataItemInternal{ ");
    String str = String.valueOf(getUri());
    localStringBuilder.append(String.valueOf(str).length() + 4 + "uri=" + str);
    if (localObject1 == null) {}
    for (localObject1 = "null";; localObject1 = Integer.valueOf(localObject1.length))
    {
      localObject1 = String.valueOf(localObject1);
      localStringBuilder.append(String.valueOf(localObject1).length() + 9 + ", dataSz=" + (String)localObject1);
      int i = ((Map)localObject2).size();
      localStringBuilder.append(23 + ", numAssets=" + i);
      if ((!bool) || (((Map)localObject2).isEmpty())) {
        break label334;
      }
      localStringBuilder.append(", assets=[");
      localObject2 = ((Map)localObject2).entrySet().iterator();
      for (localObject1 = ""; ((Iterator)localObject2).hasNext(); localObject1 = ", ")
      {
        Object localObject3 = (Map.Entry)((Iterator)localObject2).next();
        str = (String)((Map.Entry)localObject3).getKey();
        localObject3 = String.valueOf(((DataItemAsset)((Map.Entry)localObject3).getValue()).getId());
        localStringBuilder.append(String.valueOf(localObject1).length() + 2 + String.valueOf(str).length() + String.valueOf(localObject3).length() + (String)localObject1 + str + ": " + (String)localObject3);
      }
    }
    localStringBuilder.append("]");
    label334:
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzcd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */