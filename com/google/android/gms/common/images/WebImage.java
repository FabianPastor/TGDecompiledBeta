package com.google.android.gms.common.images;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public final class WebImage
  extends zza
{
  public static final Parcelable.Creator<WebImage> CREATOR = new zze();
  private int zzaku;
  private final Uri zzauQ;
  private final int zzrW;
  private final int zzrX;
  
  WebImage(int paramInt1, Uri paramUri, int paramInt2, int paramInt3)
  {
    this.zzaku = paramInt1;
    this.zzauQ = paramUri;
    this.zzrW = paramInt2;
    this.zzrX = paramInt3;
  }
  
  public WebImage(Uri paramUri)
    throws IllegalArgumentException
  {
    this(paramUri, 0, 0);
  }
  
  public WebImage(Uri paramUri, int paramInt1, int paramInt2)
    throws IllegalArgumentException
  {
    this(1, paramUri, paramInt1, paramInt2);
    if (paramUri == null) {
      throw new IllegalArgumentException("url cannot be null");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException("width and height must not be negative");
    }
  }
  
  public WebImage(JSONObject paramJSONObject)
    throws IllegalArgumentException
  {
    this(zzp(paramJSONObject), paramJSONObject.optInt("width", 0), paramJSONObject.optInt("height", 0));
  }
  
  private static Uri zzp(JSONObject paramJSONObject)
  {
    Uri localUri = null;
    if (paramJSONObject.has("url")) {}
    try
    {
      localUri = Uri.parse(paramJSONObject.getString("url"));
      return localUri;
    }
    catch (JSONException paramJSONObject) {}
    return null;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (!(paramObject instanceof WebImage))) {
        return false;
      }
      paramObject = (WebImage)paramObject;
    } while ((zzbe.equal(this.zzauQ, ((WebImage)paramObject).zzauQ)) && (this.zzrW == ((WebImage)paramObject).zzrW) && (this.zzrX == ((WebImage)paramObject).zzrX));
    return false;
  }
  
  public final int getHeight()
  {
    return this.zzrX;
  }
  
  public final Uri getUrl()
  {
    return this.zzauQ;
  }
  
  public final int getWidth()
  {
    return this.zzrW;
  }
  
  public final int hashCode()
  {
    return Arrays.hashCode(new Object[] { this.zzauQ, Integer.valueOf(this.zzrW), Integer.valueOf(this.zzrX) });
  }
  
  public final JSONObject toJson()
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("url", this.zzauQ.toString());
      localJSONObject.put("width", this.zzrW);
      localJSONObject.put("height", this.zzrX);
      return localJSONObject;
    }
    catch (JSONException localJSONException) {}
    return localJSONObject;
  }
  
  public final String toString()
  {
    return String.format(Locale.US, "Image %dx%d %s", new Object[] { Integer.valueOf(this.zzrW), Integer.valueOf(this.zzrX), this.zzauQ.toString() });
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.zzaku);
    zzd.zza(paramParcel, 2, getUrl(), paramInt, false);
    zzd.zzc(paramParcel, 3, getWidth());
    zzd.zzc(paramParcel, 4, getHeight());
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/images/WebImage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */