package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class zzca
  implements DataItem
{
  private Uri mUri;
  private Map<String, DataItemAsset> zzbSE;
  private byte[] zzbdY;
  
  public zzca(DataItem paramDataItem)
  {
    this.mUri = paramDataItem.getUri();
    this.zzbdY = paramDataItem.getData();
    HashMap localHashMap = new HashMap();
    paramDataItem = paramDataItem.getAssets().entrySet().iterator();
    while (paramDataItem.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramDataItem.next();
      if (localEntry.getKey() != null) {
        localHashMap.put((String)localEntry.getKey(), (DataItemAsset)((DataItemAsset)localEntry.getValue()).freeze());
      }
    }
    this.zzbSE = Collections.unmodifiableMap(localHashMap);
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
  
  public final DataItem setData(byte[] paramArrayOfByte)
  {
    throw new UnsupportedOperationException();
  }
  
  public final String toString()
  {
    boolean bool = Log.isLoggable("DataItem", 3);
    StringBuilder localStringBuilder = new StringBuilder("DataItemEntity{ ");
    Object localObject1 = String.valueOf(this.mUri);
    localStringBuilder.append(String.valueOf(localObject1).length() + 4 + "uri=" + (String)localObject1);
    if (this.zzbdY == null) {}
    for (localObject1 = "null";; localObject1 = Integer.valueOf(this.zzbdY.length))
    {
      localObject1 = String.valueOf(localObject1);
      localStringBuilder.append(String.valueOf(localObject1).length() + 9 + ", dataSz=" + (String)localObject1);
      int i = this.zzbSE.size();
      localStringBuilder.append(23 + ", numAssets=" + i);
      if ((!bool) || (this.zzbSE.isEmpty())) {
        break label332;
      }
      localStringBuilder.append(", assets=[");
      Iterator localIterator = this.zzbSE.entrySet().iterator();
      for (localObject1 = ""; localIterator.hasNext(); localObject1 = ", ")
      {
        Object localObject2 = (Map.Entry)localIterator.next();
        String str = (String)((Map.Entry)localObject2).getKey();
        localObject2 = String.valueOf(((DataItemAsset)((Map.Entry)localObject2).getValue()).getId());
        localStringBuilder.append(String.valueOf(localObject1).length() + 2 + String.valueOf(str).length() + String.valueOf(localObject2).length() + (String)localObject1 + str + ": " + (String)localObject2);
      }
    }
    localStringBuilder.append("]");
    label332:
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzca.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */