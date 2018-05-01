package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.common.data.DataBufferRef;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class zzdf
  extends DataBufferRef
  implements DataItem
{
  private final int zzdl;
  
  public zzdf(DataHolder paramDataHolder, int paramInt1, int paramInt2)
  {
    super(paramDataHolder, paramInt1);
    this.zzdl = paramInt2;
  }
  
  public final Map<String, DataItemAsset> getAssets()
  {
    HashMap localHashMap = new HashMap(this.zzdl);
    for (int i = 0; i < this.zzdl; i++)
    {
      zzdb localzzdb = new zzdb(this.mDataHolder, this.mDataRow + i);
      if (localzzdb.getDataItemKey() != null) {
        localHashMap.put(localzzdb.getDataItemKey(), localzzdb);
      }
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
  
  public final String toString()
  {
    boolean bool = Log.isLoggable("DataItem", 3);
    Object localObject1 = getData();
    Object localObject2 = getAssets();
    StringBuilder localStringBuilder = new StringBuilder("DataItemRef{ ");
    Object localObject3 = String.valueOf(getUri());
    localStringBuilder.append(String.valueOf(localObject3).length() + 4 + "uri=" + (String)localObject3);
    if (localObject1 == null) {}
    for (localObject3 = "null";; localObject3 = Integer.valueOf(localObject1.length))
    {
      localObject3 = String.valueOf(localObject3);
      localStringBuilder.append(String.valueOf(localObject3).length() + 9 + ", dataSz=" + (String)localObject3);
      int i = ((Map)localObject2).size();
      localStringBuilder.append(23 + ", numAssets=" + i);
      if ((!bool) || (((Map)localObject2).isEmpty())) {
        break label333;
      }
      localStringBuilder.append(", assets=[");
      localObject2 = ((Map)localObject2).entrySet().iterator();
      for (localObject3 = ""; ((Iterator)localObject2).hasNext(); localObject3 = ", ")
      {
        Object localObject4 = (Map.Entry)((Iterator)localObject2).next();
        localObject1 = (String)((Map.Entry)localObject4).getKey();
        localObject4 = ((DataItemAsset)((Map.Entry)localObject4).getValue()).getId();
        localStringBuilder.append(String.valueOf(localObject3).length() + 2 + String.valueOf(localObject1).length() + String.valueOf(localObject4).length() + (String)localObject3 + (String)localObject1 + ": " + (String)localObject4);
      }
    }
    localStringBuilder.append("]");
    label333:
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzdf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */