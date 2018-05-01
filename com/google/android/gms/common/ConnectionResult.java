package com.google.android.gms.common;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender.SendIntentException;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Objects.ToStringHelper;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class ConnectionResult
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<ConnectionResult> CREATOR = new ConnectionResultCreator();
  public static final ConnectionResult RESULT_SUCCESS = new ConnectionResult(0);
  private final int zzal;
  private final int zzam;
  private final PendingIntent zzan;
  private final String zzao;
  
  public ConnectionResult(int paramInt)
  {
    this(paramInt, null, null);
  }
  
  ConnectionResult(int paramInt1, int paramInt2, PendingIntent paramPendingIntent, String paramString)
  {
    this.zzal = paramInt1;
    this.zzam = paramInt2;
    this.zzan = paramPendingIntent;
    this.zzao = paramString;
  }
  
  public ConnectionResult(int paramInt, PendingIntent paramPendingIntent)
  {
    this(paramInt, paramPendingIntent, null);
  }
  
  public ConnectionResult(int paramInt, PendingIntent paramPendingIntent, String paramString)
  {
    this(1, paramInt, paramPendingIntent, paramString);
  }
  
  static String zza(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = 31 + "UNKNOWN_ERROR_CODE(" + paramInt + ")";
    }
    for (;;)
    {
      return str;
      str = "SUCCESS";
      continue;
      str = "SERVICE_MISSING";
      continue;
      str = "SERVICE_VERSION_UPDATE_REQUIRED";
      continue;
      str = "SERVICE_DISABLED";
      continue;
      str = "SIGN_IN_REQUIRED";
      continue;
      str = "INVALID_ACCOUNT";
      continue;
      str = "RESOLUTION_REQUIRED";
      continue;
      str = "NETWORK_ERROR";
      continue;
      str = "INTERNAL_ERROR";
      continue;
      str = "SERVICE_INVALID";
      continue;
      str = "DEVELOPER_ERROR";
      continue;
      str = "LICENSE_CHECK_FAILED";
      continue;
      str = "CANCELED";
      continue;
      str = "TIMEOUT";
      continue;
      str = "INTERRUPTED";
      continue;
      str = "API_UNAVAILABLE";
      continue;
      str = "SIGN_IN_FAILED";
      continue;
      str = "SERVICE_UPDATING";
      continue;
      str = "SERVICE_MISSING_PERMISSION";
      continue;
      str = "RESTRICTED_PROFILE";
      continue;
      str = "API_VERSION_UPDATE_REQUIRED";
      continue;
      str = "DRIVE_EXTERNAL_STORAGE_REQUIRED";
      continue;
      str = "UNFINISHED";
      continue;
      str = "UNKNOWN";
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == this) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof ConnectionResult))
      {
        bool = false;
      }
      else
      {
        paramObject = (ConnectionResult)paramObject;
        if ((this.zzam != ((ConnectionResult)paramObject).zzam) || (!Objects.equal(this.zzan, ((ConnectionResult)paramObject).zzan)) || (!Objects.equal(this.zzao, ((ConnectionResult)paramObject).zzao))) {
          bool = false;
        }
      }
    }
  }
  
  public final int getErrorCode()
  {
    return this.zzam;
  }
  
  public final String getErrorMessage()
  {
    return this.zzao;
  }
  
  public final PendingIntent getResolution()
  {
    return this.zzan;
  }
  
  public final boolean hasResolution()
  {
    if ((this.zzam != 0) && (this.zzan != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { Integer.valueOf(this.zzam), this.zzan, this.zzao });
  }
  
  public final boolean isSuccess()
  {
    if (this.zzam == 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final void startResolutionForResult(Activity paramActivity, int paramInt)
    throws IntentSender.SendIntentException
  {
    if (!hasResolution()) {}
    for (;;)
    {
      return;
      paramActivity.startIntentSenderForResult(this.zzan.getIntentSender(), paramInt, null, 0, 0, 0);
    }
  }
  
  public final String toString()
  {
    return Objects.toStringHelper(this).add("statusCode", zza(this.zzam)).add("resolution", this.zzan).add("message", this.zzao).toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.zzal);
    SafeParcelWriter.writeInt(paramParcel, 2, getErrorCode());
    SafeParcelWriter.writeParcelable(paramParcel, 3, getResolution(), paramInt, false);
    SafeParcelWriter.writeString(paramParcel, 4, getErrorMessage(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/ConnectionResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */