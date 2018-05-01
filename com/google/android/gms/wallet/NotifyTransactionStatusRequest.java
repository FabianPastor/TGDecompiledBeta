package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

@Deprecated
public final class NotifyTransactionStatusRequest
  extends zza
{
  public static final Parcelable.Creator<NotifyTransactionStatusRequest> CREATOR = new zzu();
  int status;
  String zzbOq;
  String zzbPF;
  
  NotifyTransactionStatusRequest() {}
  
  NotifyTransactionStatusRequest(String paramString1, int paramInt, String paramString2)
  {
    this.zzbOq = paramString1;
    this.status = paramInt;
    this.zzbPF = paramString2;
  }
  
  public static Builder newBuilder()
  {
    NotifyTransactionStatusRequest localNotifyTransactionStatusRequest = new NotifyTransactionStatusRequest();
    localNotifyTransactionStatusRequest.getClass();
    return new Builder(null);
  }
  
  public final String getDetailedReason()
  {
    return this.zzbPF;
  }
  
  public final String getGoogleTransactionId()
  {
    return this.zzbOq;
  }
  
  public final int getStatus()
  {
    return this.status;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbOq, false);
    zzd.zzc(paramParcel, 3, this.status);
    zzd.zza(paramParcel, 4, this.zzbPF, false);
    zzd.zzI(paramParcel, paramInt);
  }
  
  public final class Builder
  {
    private Builder() {}
    
    public final NotifyTransactionStatusRequest build()
    {
      boolean bool2 = true;
      if (!TextUtils.isEmpty(NotifyTransactionStatusRequest.this.zzbOq))
      {
        bool1 = true;
        zzbo.zzb(bool1, "googleTransactionId is required");
        if ((NotifyTransactionStatusRequest.this.status <= 0) || (NotifyTransactionStatusRequest.this.status > 8)) {
          break label63;
        }
      }
      label63:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        zzbo.zzb(bool1, "status is an unrecognized value");
        return NotifyTransactionStatusRequest.this;
        bool1 = false;
        break;
      }
    }
    
    public final Builder setDetailedReason(String paramString)
    {
      NotifyTransactionStatusRequest.this.zzbPF = paramString;
      return this;
    }
    
    public final Builder setGoogleTransactionId(String paramString)
    {
      NotifyTransactionStatusRequest.this.zzbOq = paramString;
      return this;
    }
    
    public final Builder setStatus(int paramInt)
    {
      NotifyTransactionStatusRequest.this.status = paramInt;
      return this;
    }
  }
  
  public static abstract interface Status
  {
    public static final int SUCCESS = 1;
    
    public static abstract interface Error
    {
      public static final int AVS_DECLINE = 7;
      public static final int BAD_CARD = 4;
      public static final int BAD_CVC = 3;
      public static final int DECLINED = 5;
      public static final int FRAUD_DECLINE = 8;
      public static final int OTHER = 6;
      public static final int UNKNOWN = 2;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/NotifyTransactionStatusRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */