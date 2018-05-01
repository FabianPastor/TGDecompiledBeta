package com.google.android.gms.wearable;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.internal.adp;
import com.google.android.gms.internal.hc;
import com.google.android.gms.internal.hd;
import java.util.List;

public class PutDataMapRequest
{
  private final DataMap zzbRf;
  private final PutDataRequest zzbRg;
  
  private PutDataMapRequest(PutDataRequest paramPutDataRequest, DataMap paramDataMap)
  {
    this.zzbRg = paramPutDataRequest;
    this.zzbRf = new DataMap();
    if (paramDataMap != null) {
      this.zzbRf.putAll(paramDataMap);
    }
  }
  
  public static PutDataMapRequest create(String paramString)
  {
    return new PutDataMapRequest(PutDataRequest.create(paramString), null);
  }
  
  public static PutDataMapRequest createFromDataMapItem(DataMapItem paramDataMapItem)
  {
    return new PutDataMapRequest(PutDataRequest.zzt(paramDataMapItem.getUri()), paramDataMapItem.getDataMap());
  }
  
  public static PutDataMapRequest createWithAutoAppendedId(String paramString)
  {
    return new PutDataMapRequest(PutDataRequest.createWithAutoAppendedId(paramString), null);
  }
  
  public PutDataRequest asPutDataRequest()
  {
    Object localObject = hc.zza(this.zzbRf);
    this.zzbRg.setData(adp.zzc(((hd)localObject).zzbTF));
    int j = ((hd)localObject).zzbTG.size();
    int i = 0;
    while (i < j)
    {
      String str1 = Integer.toString(i);
      Asset localAsset = (Asset)((hd)localObject).zzbTG.get(i);
      if (str1 == null)
      {
        localObject = String.valueOf(localAsset);
        throw new IllegalStateException(String.valueOf(localObject).length() + 26 + "asset key cannot be null: " + (String)localObject);
      }
      if (localAsset == null)
      {
        localObject = String.valueOf(str1);
        if (((String)localObject).length() != 0) {}
        for (localObject = "asset cannot be null: key=".concat((String)localObject);; localObject = new String("asset cannot be null: key=")) {
          throw new IllegalStateException((String)localObject);
        }
      }
      if (Log.isLoggable("DataMap", 3))
      {
        String str2 = String.valueOf(localAsset);
        Log.d("DataMap", String.valueOf(str1).length() + 33 + String.valueOf(str2).length() + "asPutDataRequest: adding asset: " + str1 + " " + str2);
      }
      this.zzbRg.putAsset(str1, localAsset);
      i += 1;
    }
    return this.zzbRg;
  }
  
  public DataMap getDataMap()
  {
    return this.zzbRf;
  }
  
  public Uri getUri()
  {
    return this.zzbRg.getUri();
  }
  
  public boolean isUrgent()
  {
    return this.zzbRg.isUrgent();
  }
  
  public PutDataMapRequest setUrgent()
  {
    this.zzbRg.setUrgent();
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/PutDataMapRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */