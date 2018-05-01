package com.google.android.gms.wallet.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wallet.R.styleable;

public final class WalletFragmentOptions
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<WalletFragmentOptions> CREATOR = new zzf();
  private int environment;
  private int mode;
  private int theme;
  private WalletFragmentStyle zzfz;
  
  private WalletFragmentOptions()
  {
    this.environment = 3;
    this.zzfz = new WalletFragmentStyle();
  }
  
  WalletFragmentOptions(int paramInt1, int paramInt2, WalletFragmentStyle paramWalletFragmentStyle, int paramInt3)
  {
    this.environment = paramInt1;
    this.theme = paramInt2;
    this.zzfz = paramWalletFragmentStyle;
    this.mode = paramInt3;
  }
  
  public static Builder newBuilder()
  {
    return new Builder(new WalletFragmentOptions(), null);
  }
  
  public static WalletFragmentOptions zza(Context paramContext, AttributeSet paramAttributeSet)
  {
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.WalletFragmentOptions);
    int i = paramAttributeSet.getInt(R.styleable.WalletFragmentOptions_appTheme, 0);
    int j = paramAttributeSet.getInt(R.styleable.WalletFragmentOptions_environment, 1);
    int k = paramAttributeSet.getResourceId(R.styleable.WalletFragmentOptions_fragmentStyle, 0);
    int m = paramAttributeSet.getInt(R.styleable.WalletFragmentOptions_fragmentMode, 1);
    paramAttributeSet.recycle();
    paramAttributeSet = new WalletFragmentOptions();
    paramAttributeSet.theme = i;
    paramAttributeSet.environment = j;
    paramAttributeSet.zzfz = new WalletFragmentStyle().setStyleResourceId(k);
    paramAttributeSet.zzfz.zza(paramContext);
    paramAttributeSet.mode = m;
    return paramAttributeSet;
  }
  
  public final int getEnvironment()
  {
    return this.environment;
  }
  
  public final WalletFragmentStyle getFragmentStyle()
  {
    return this.zzfz;
  }
  
  public final int getMode()
  {
    return this.mode;
  }
  
  public final int getTheme()
  {
    return this.theme;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, getEnvironment());
    SafeParcelWriter.writeInt(paramParcel, 3, getTheme());
    SafeParcelWriter.writeParcelable(paramParcel, 4, getFragmentStyle(), paramInt, false);
    SafeParcelWriter.writeInt(paramParcel, 5, getMode());
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final void zza(Context paramContext)
  {
    if (this.zzfz != null) {
      this.zzfz.zza(paramContext);
    }
  }
  
  public final class Builder
  {
    private Builder() {}
    
    public final WalletFragmentOptions build()
    {
      return WalletFragmentOptions.this;
    }
    
    public final Builder setEnvironment(int paramInt)
    {
      WalletFragmentOptions.zza(WalletFragmentOptions.this, paramInt);
      return this;
    }
    
    public final Builder setFragmentStyle(WalletFragmentStyle paramWalletFragmentStyle)
    {
      WalletFragmentOptions.zza(WalletFragmentOptions.this, paramWalletFragmentStyle);
      return this;
    }
    
    public final Builder setMode(int paramInt)
    {
      WalletFragmentOptions.zzc(WalletFragmentOptions.this, paramInt);
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/fragment/WalletFragmentOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */