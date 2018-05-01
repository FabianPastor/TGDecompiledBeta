package com.google.android.gms.wearable;

import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;

public class Asset
  extends zza
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<Asset> CREATOR = new zze();
  private Uri uri;
  private String zzbQX;
  private ParcelFileDescriptor zzbQY;
  private byte[] zzbdY;
  
  Asset(byte[] paramArrayOfByte, String paramString, ParcelFileDescriptor paramParcelFileDescriptor, Uri paramUri)
  {
    this.zzbdY = paramArrayOfByte;
    this.zzbQX = paramString;
    this.zzbQY = paramParcelFileDescriptor;
    this.uri = paramUri;
  }
  
  public static Asset createFromBytes(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("Asset data cannot be null");
    }
    return new Asset(paramArrayOfByte, null, null, null);
  }
  
  public static Asset createFromFd(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    if (paramParcelFileDescriptor == null) {
      throw new IllegalArgumentException("Asset fd cannot be null");
    }
    return new Asset(null, null, paramParcelFileDescriptor, null);
  }
  
  public static Asset createFromRef(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Asset digest cannot be null");
    }
    return new Asset(null, paramString, null, null);
  }
  
  public static Asset createFromUri(Uri paramUri)
  {
    if (paramUri == null) {
      throw new IllegalArgumentException("Asset uri cannot be null");
    }
    return new Asset(null, null, null, paramUri);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof Asset)) {
        return false;
      }
      paramObject = (Asset)paramObject;
    } while ((Arrays.equals(this.zzbdY, ((Asset)paramObject).zzbdY)) && (zzbe.equal(this.zzbQX, ((Asset)paramObject).zzbQX)) && (zzbe.equal(this.zzbQY, ((Asset)paramObject).zzbQY)) && (zzbe.equal(this.uri, ((Asset)paramObject).uri)));
    return false;
  }
  
  public final byte[] getData()
  {
    return this.zzbdY;
  }
  
  public String getDigest()
  {
    return this.zzbQX;
  }
  
  public ParcelFileDescriptor getFd()
  {
    return this.zzbQY;
  }
  
  public Uri getUri()
  {
    return this.uri;
  }
  
  public int hashCode()
  {
    return Arrays.deepHashCode(new Object[] { this.zzbdY, this.zzbQX, this.zzbQY, this.uri });
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Asset[@");
    localStringBuilder.append(Integer.toHexString(hashCode()));
    if (this.zzbQX == null) {
      localStringBuilder.append(", nodigest");
    }
    for (;;)
    {
      if (this.zzbdY != null)
      {
        localStringBuilder.append(", size=");
        localStringBuilder.append(this.zzbdY.length);
      }
      if (this.zzbQY != null)
      {
        localStringBuilder.append(", fd=");
        localStringBuilder.append(this.zzbQY);
      }
      if (this.uri != null)
      {
        localStringBuilder.append(", uri=");
        localStringBuilder.append(this.uri);
      }
      localStringBuilder.append("]");
      return localStringBuilder.toString();
      localStringBuilder.append(", ");
      localStringBuilder.append(this.zzbQX);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt |= 0x1;
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbdY, false);
    zzd.zza(paramParcel, 3, getDigest(), false);
    zzd.zza(paramParcel, 4, this.zzbQY, paramInt, false);
    zzd.zza(paramParcel, 5, this.uri, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/Asset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */