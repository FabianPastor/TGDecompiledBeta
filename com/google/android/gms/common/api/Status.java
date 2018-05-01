package com.google.android.gms.common.api;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Objects.ToStringHelper;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class Status
  extends AbstractSafeParcelable
  implements Result, ReflectedParcelable
{
  public static final Parcelable.Creator<Status> CREATOR = new zze();
  public static final Status RESULT_CANCELED;
  public static final Status RESULT_DEAD_CLIENT;
  public static final Status RESULT_INTERNAL_ERROR;
  public static final Status RESULT_INTERRUPTED;
  public static final Status RESULT_SUCCESS = new Status(0);
  public static final Status RESULT_TIMEOUT;
  private static final Status zzdq;
  private final int zzal;
  private final int zzam;
  private final PendingIntent zzan;
  private final String zzao;
  
  static
  {
    RESULT_INTERRUPTED = new Status(14);
    RESULT_INTERNAL_ERROR = new Status(8);
    RESULT_TIMEOUT = new Status(15);
    RESULT_CANCELED = new Status(16);
    zzdq = new Status(17);
    RESULT_DEAD_CLIENT = new Status(18);
  }
  
  public Status(int paramInt)
  {
    this(paramInt, null);
  }
  
  Status(int paramInt1, int paramInt2, String paramString, PendingIntent paramPendingIntent)
  {
    this.zzal = paramInt1;
    this.zzam = paramInt2;
    this.zzao = paramString;
    this.zzan = paramPendingIntent;
  }
  
  public Status(int paramInt, String paramString)
  {
    this(1, paramInt, paramString, null);
  }
  
  public Status(int paramInt, String paramString, PendingIntent paramPendingIntent)
  {
    this(1, paramInt, paramString, paramPendingIntent);
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool1 = false;
    boolean bool2;
    if (!(paramObject instanceof Status)) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      paramObject = (Status)paramObject;
      bool2 = bool1;
      if (this.zzal == ((Status)paramObject).zzal)
      {
        bool2 = bool1;
        if (this.zzam == ((Status)paramObject).zzam)
        {
          bool2 = bool1;
          if (Objects.equal(this.zzao, ((Status)paramObject).zzao))
          {
            bool2 = bool1;
            if (Objects.equal(this.zzan, ((Status)paramObject).zzan)) {
              bool2 = true;
            }
          }
        }
      }
    }
  }
  
  public final Status getStatus()
  {
    return this;
  }
  
  public final int getStatusCode()
  {
    return this.zzam;
  }
  
  public final String getStatusMessage()
  {
    return this.zzao;
  }
  
  public final boolean hasResolution()
  {
    if (this.zzan != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { Integer.valueOf(this.zzal), Integer.valueOf(this.zzam), this.zzao, this.zzan });
  }
  
  public final boolean isSuccess()
  {
    if (this.zzam <= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final String toString()
  {
    return Objects.toStringHelper(this).add("statusCode", zzp()).add("resolution", this.zzan).toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, getStatusCode());
    SafeParcelWriter.writeString(paramParcel, 2, getStatusMessage(), false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, this.zzan, paramInt, false);
    SafeParcelWriter.writeInt(paramParcel, 1000, this.zzal);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final String zzp()
  {
    if (this.zzao != null) {}
    for (String str = this.zzao;; str = CommonStatusCodes.getStatusCodeString(this.zzam)) {
      return str;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/Status.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */