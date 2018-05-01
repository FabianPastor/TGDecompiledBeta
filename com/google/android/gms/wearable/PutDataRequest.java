package com.google.android.gms.wearable;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.internal.DataItemAssetParcelable;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PutDataRequest
  extends zza
{
  public static final Parcelable.Creator<PutDataRequest> CREATOR = new zzh();
  public static final String WEAR_URI_SCHEME = "wear";
  private static final long zzbRh = TimeUnit.MINUTES.toMillis(30L);
  private static final Random zzbRi = new SecureRandom();
  private final Uri mUri;
  private final Bundle zzbRj;
  private long zzbRk;
  private byte[] zzbdY;
  
  private PutDataRequest(Uri paramUri)
  {
    this(paramUri, new Bundle(), null, zzbRh);
  }
  
  PutDataRequest(Uri paramUri, Bundle paramBundle, byte[] paramArrayOfByte, long paramLong)
  {
    this.mUri = paramUri;
    this.zzbRj = paramBundle;
    this.zzbRj.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
    this.zzbdY = paramArrayOfByte;
    this.zzbRk = paramLong;
  }
  
  public static PutDataRequest create(String paramString)
  {
    return zzt(zzgj(paramString));
  }
  
  public static PutDataRequest createFromDataItem(DataItem paramDataItem)
  {
    PutDataRequest localPutDataRequest = zzt(paramDataItem.getUri());
    Iterator localIterator = paramDataItem.getAssets().entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (((DataItemAsset)localEntry.getValue()).getId() == null)
      {
        paramDataItem = String.valueOf((String)localEntry.getKey());
        if (paramDataItem.length() != 0) {}
        for (paramDataItem = "Cannot create an asset for a put request without a digest: ".concat(paramDataItem);; paramDataItem = new String("Cannot create an asset for a put request without a digest: ")) {
          throw new IllegalStateException(paramDataItem);
        }
      }
      localPutDataRequest.putAsset((String)localEntry.getKey(), Asset.createFromRef(((DataItemAsset)localEntry.getValue()).getId()));
    }
    localPutDataRequest.setData(paramDataItem.getData());
    return localPutDataRequest;
  }
  
  public static PutDataRequest createWithAutoAppendedId(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramString);
    if (!paramString.endsWith("/")) {
      localStringBuilder.append("/");
    }
    localStringBuilder.append("PN").append(zzbRi.nextLong());
    return new PutDataRequest(zzgj(localStringBuilder.toString()));
  }
  
  private static Uri zzgj(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("An empty path was supplied.");
    }
    if (!paramString.startsWith("/")) {
      throw new IllegalArgumentException("A path must start with a single / .");
    }
    if (paramString.startsWith("//")) {
      throw new IllegalArgumentException("A path must start with a single / .");
    }
    return new Uri.Builder().scheme("wear").path(paramString).build();
  }
  
  public static PutDataRequest zzt(Uri paramUri)
  {
    return new PutDataRequest(paramUri);
  }
  
  public Asset getAsset(String paramString)
  {
    return (Asset)this.zzbRj.getParcelable(paramString);
  }
  
  public Map<String, Asset> getAssets()
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = this.zzbRj.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localHashMap.put(str, (Asset)this.zzbRj.getParcelable(str));
    }
    return Collections.unmodifiableMap(localHashMap);
  }
  
  public byte[] getData()
  {
    return this.zzbdY;
  }
  
  public Uri getUri()
  {
    return this.mUri;
  }
  
  public boolean hasAsset(String paramString)
  {
    return this.zzbRj.containsKey(paramString);
  }
  
  public boolean isUrgent()
  {
    return this.zzbRk == 0L;
  }
  
  public PutDataRequest putAsset(String paramString, Asset paramAsset)
  {
    zzbo.zzu(paramString);
    zzbo.zzu(paramAsset);
    this.zzbRj.putParcelable(paramString, paramAsset);
    return this;
  }
  
  public PutDataRequest removeAsset(String paramString)
  {
    this.zzbRj.remove(paramString);
    return this;
  }
  
  public PutDataRequest setData(byte[] paramArrayOfByte)
  {
    this.zzbdY = paramArrayOfByte;
    return this;
  }
  
  public PutDataRequest setUrgent()
  {
    this.zzbRk = 0L;
    return this;
  }
  
  public String toString()
  {
    return toString(Log.isLoggable("DataMap", 3));
  }
  
  public String toString(boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder("PutDataRequest[");
    if (this.zzbdY == null) {}
    for (Object localObject = "null";; localObject = Integer.valueOf(this.zzbdY.length))
    {
      localObject = String.valueOf(localObject);
      localStringBuilder.append(String.valueOf(localObject).length() + 7 + "dataSz=" + (String)localObject);
      int i = this.zzbRj.size();
      localStringBuilder.append(23 + ", numAssets=" + i);
      localObject = String.valueOf(this.mUri);
      localStringBuilder.append(String.valueOf(localObject).length() + 6 + ", uri=" + (String)localObject);
      long l = this.zzbRk;
      localStringBuilder.append(35 + ", syncDeadline=" + l);
      if (paramBoolean) {
        break;
      }
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
    localStringBuilder.append("]\n  assets: ");
    localObject = this.zzbRj.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str1 = (String)((Iterator)localObject).next();
      String str2 = String.valueOf(this.zzbRj.getParcelable(str1));
      localStringBuilder.append(String.valueOf(str1).length() + 7 + String.valueOf(str2).length() + "\n    " + str1 + ": " + str2);
    }
    localStringBuilder.append("\n  ]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, getUri(), paramInt, false);
    zzd.zza(paramParcel, 4, this.zzbRj, false);
    zzd.zza(paramParcel, 5, getData(), false);
    zzd.zza(paramParcel, 6, this.zzbRk);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/PutDataRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */