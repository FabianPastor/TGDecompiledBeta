package com.google.android.gms.wallet.fragment;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

public final class WalletFragmentInitParams
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<WalletFragmentInitParams> CREATOR = new zzd();
  private String zzci;
  private MaskedWalletRequest zzfi;
  private MaskedWallet zzfj;
  private int zzfx;
  
  private WalletFragmentInitParams()
  {
    this.zzfx = -1;
  }
  
  WalletFragmentInitParams(String paramString, MaskedWalletRequest paramMaskedWalletRequest, int paramInt, MaskedWallet paramMaskedWallet)
  {
    this.zzci = paramString;
    this.zzfi = paramMaskedWalletRequest;
    this.zzfx = paramInt;
    this.zzfj = paramMaskedWallet;
  }
  
  public static Builder newBuilder()
  {
    return new Builder(new WalletFragmentInitParams(), null);
  }
  
  public final String getAccountName()
  {
    return this.zzci;
  }
  
  public final MaskedWallet getMaskedWallet()
  {
    return this.zzfj;
  }
  
  public final MaskedWalletRequest getMaskedWalletRequest()
  {
    return this.zzfi;
  }
  
  public final int getMaskedWalletRequestCode()
  {
    return this.zzfx;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, getAccountName(), false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, getMaskedWalletRequest(), paramInt, false);
    SafeParcelWriter.writeInt(paramParcel, 4, getMaskedWalletRequestCode());
    SafeParcelWriter.writeParcelable(paramParcel, 5, getMaskedWallet(), paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final class Builder
  {
    private Builder() {}
    
    public final WalletFragmentInitParams build()
    {
      boolean bool1 = true;
      if (((WalletFragmentInitParams.zza(WalletFragmentInitParams.this) != null) && (WalletFragmentInitParams.zzb(WalletFragmentInitParams.this) == null)) || ((WalletFragmentInitParams.zza(WalletFragmentInitParams.this) == null) && (WalletFragmentInitParams.zzb(WalletFragmentInitParams.this) != null)))
      {
        bool2 = true;
        Preconditions.checkState(bool2, "Exactly one of MaskedWallet or MaskedWalletRequest is required");
        if (WalletFragmentInitParams.zzc(WalletFragmentInitParams.this) < 0) {
          break label78;
        }
      }
      label78:
      for (boolean bool2 = bool1;; bool2 = false)
      {
        Preconditions.checkState(bool2, "masked wallet request code is required and must be non-negative");
        return WalletFragmentInitParams.this;
        bool2 = false;
        break;
      }
    }
    
    public final Builder setMaskedWalletRequest(MaskedWalletRequest paramMaskedWalletRequest)
    {
      WalletFragmentInitParams.zza(WalletFragmentInitParams.this, paramMaskedWalletRequest);
      return this;
    }
    
    public final Builder setMaskedWalletRequestCode(int paramInt)
    {
      WalletFragmentInitParams.zza(WalletFragmentInitParams.this, paramInt);
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/fragment/WalletFragmentInitParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */