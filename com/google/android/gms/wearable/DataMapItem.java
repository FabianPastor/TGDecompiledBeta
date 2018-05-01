package com.google.android.gms.wearable;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.internal.ado;
import com.google.android.gms.internal.hc;
import com.google.android.gms.internal.hd;
import com.google.android.gms.internal.he;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataMapItem
{
  private final Uri mUri;
  private final DataMap zzbRf;
  
  private DataMapItem(DataItem paramDataItem)
  {
    this.mUri = paramDataItem.getUri();
    this.zzbRf = zza((DataItem)paramDataItem.freeze());
  }
  
  public static DataMapItem fromDataItem(DataItem paramDataItem)
  {
    if (paramDataItem == null) {
      throw new IllegalStateException("provided dataItem is null");
    }
    return new DataMapItem(paramDataItem);
  }
  
  private static DataMap zza(DataItem paramDataItem)
  {
    if ((paramDataItem.getData() == null) && (paramDataItem.getAssets().size() > 0)) {
      throw new IllegalArgumentException("Cannot create DataMapItem from a DataItem  that wasn't made with DataMapItem.");
    }
    if (paramDataItem.getData() == null) {
      return new DataMap();
    }
    try
    {
      localObject1 = new ArrayList();
      j = paramDataItem.getAssets().size();
      i = 0;
    }
    catch (ado localado)
    {
      for (;;)
      {
        Object localObject1;
        int j;
        int i;
        Object localObject2 = String.valueOf(paramDataItem.getUri());
        String str = String.valueOf(Base64.encodeToString(paramDataItem.getData(), 0));
        Log.w("DataItem", String.valueOf(localObject2).length() + 50 + String.valueOf(str).length() + "Unable to parse datamap from dataItem. uri=" + (String)localObject2 + ", data=" + str);
        paramDataItem = String.valueOf(paramDataItem.getUri());
        throw new IllegalStateException(String.valueOf(paramDataItem).length() + 44 + "Unable to parse datamap from dataItem.  uri=" + paramDataItem, localado);
        localado.add(Asset.createFromRef(((DataItemAsset)localObject2).getId()));
        i += 1;
      }
      DataMap localDataMap = hc.zza(new hd(he.zzy(paramDataItem.getData()), localado));
      return localDataMap;
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;) {}
    }
    if (i < j)
    {
      localObject2 = (DataItemAsset)paramDataItem.getAssets().get(Integer.toString(i));
      if (localObject2 == null)
      {
        localObject1 = String.valueOf(paramDataItem);
        throw new IllegalStateException(String.valueOf(localObject1).length() + 64 + "Cannot find DataItemAsset referenced in data at " + i + " for " + (String)localObject1);
      }
    }
  }
  
  public DataMap getDataMap()
  {
    return this.zzbRf;
  }
  
  public Uri getUri()
  {
    return this.mUri;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/DataMapItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */