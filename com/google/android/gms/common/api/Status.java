package com.google.android.gms.common.api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender.SendIntentException;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzab.zza;

public final class Status
  extends AbstractSafeParcelable
  implements Result, ReflectedParcelable
{
  public static final Parcelable.Creator<Status> CREATOR = new zzh();
  public static final Status vY = new Status(0);
  public static final Status vZ = new Status(14);
  public static final Status wa = new Status(8);
  public static final Status wb = new Status(15);
  public static final Status wc = new Status(16);
  public static final Status wd = new Status(17);
  public static final Status we = new Status(18);
  private final PendingIntent mPendingIntent;
  private final int mVersionCode;
  private final int rR;
  private final String uK;
  
  public Status(int paramInt)
  {
    this(paramInt, null);
  }
  
  Status(int paramInt1, int paramInt2, String paramString, PendingIntent paramPendingIntent)
  {
    this.mVersionCode = paramInt1;
    this.rR = paramInt2;
    this.uK = paramString;
    this.mPendingIntent = paramPendingIntent;
  }
  
  public Status(int paramInt, String paramString)
  {
    this(1, paramInt, paramString, null);
  }
  
  public Status(int paramInt, String paramString, PendingIntent paramPendingIntent)
  {
    this(1, paramInt, paramString, paramPendingIntent);
  }
  
  private String zzaqi()
  {
    if (this.uK != null) {
      return this.uK;
    }
    return CommonStatusCodes.getStatusCodeString(this.rR);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Status)) {}
    do
    {
      return false;
      paramObject = (Status)paramObject;
    } while ((this.mVersionCode != ((Status)paramObject).mVersionCode) || (this.rR != ((Status)paramObject).rR) || (!zzab.equal(this.uK, ((Status)paramObject).uK)) || (!zzab.equal(this.mPendingIntent, ((Status)paramObject).mPendingIntent)));
    return true;
  }
  
  public PendingIntent getResolution()
  {
    return this.mPendingIntent;
  }
  
  public Status getStatus()
  {
    return this;
  }
  
  public int getStatusCode()
  {
    return this.rR;
  }
  
  @Nullable
  public String getStatusMessage()
  {
    return this.uK;
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public boolean hasResolution()
  {
    return this.mPendingIntent != null;
  }
  
  public int hashCode()
  {
    return zzab.hashCode(new Object[] { Integer.valueOf(this.mVersionCode), Integer.valueOf(this.rR), this.uK, this.mPendingIntent });
  }
  
  public boolean isCanceled()
  {
    return this.rR == 16;
  }
  
  public boolean isInterrupted()
  {
    return this.rR == 14;
  }
  
  public boolean isSuccess()
  {
    return this.rR <= 0;
  }
  
  public void startResolutionForResult(Activity paramActivity, int paramInt)
    throws IntentSender.SendIntentException
  {
    if (!hasResolution()) {
      return;
    }
    paramActivity.startIntentSenderForResult(this.mPendingIntent.getIntentSender(), paramInt, null, 0, 0, 0);
  }
  
  public String toString()
  {
    return zzab.zzx(this).zzg("statusCode", zzaqi()).zzg("resolution", this.mPendingIntent).toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzh.zza(this, paramParcel, paramInt);
  }
  
  PendingIntent zzaqh()
  {
    return this.mPendingIntent;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/Status.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */