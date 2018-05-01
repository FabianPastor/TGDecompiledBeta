package com.google.android.gms.wallet.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wallet.R.style;
import com.google.android.gms.wallet.R.styleable;

public final class WalletFragmentStyle
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<WalletFragmentStyle> CREATOR = new zzg();
  private Bundle zzgb;
  private int zzgc;
  
  public WalletFragmentStyle()
  {
    this.zzgb = new Bundle();
    this.zzgb.putInt("buyButtonAppearanceDefault", 4);
    this.zzgb.putInt("maskedWalletDetailsLogoImageTypeDefault", 3);
  }
  
  WalletFragmentStyle(Bundle paramBundle, int paramInt)
  {
    this.zzgb = paramBundle;
    this.zzgc = paramInt;
  }
  
  private static long zza(int paramInt)
  {
    if (paramInt < 0) {
      if ((paramInt != -1) && (paramInt != -2)) {}
    }
    for (long l = zzc(129, paramInt);; l = zza(0, paramInt))
    {
      return l;
      throw new IllegalArgumentException(39 + "Unexpected dimension value: " + paramInt);
    }
  }
  
  private static long zza(int paramInt, float paramFloat)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException(30 + "Unrecognized unit: " + paramInt);
    }
    return zzc(paramInt, Float.floatToIntBits(paramFloat));
  }
  
  private final void zza(TypedArray paramTypedArray, int paramInt, String paramString)
  {
    if (this.zzgb.containsKey(paramString)) {}
    TypedValue localTypedValue;
    do
    {
      return;
      localTypedValue = paramTypedArray.peekValue(paramInt);
    } while (localTypedValue == null);
    paramTypedArray = this.zzgb;
    switch (localTypedValue.type)
    {
    default: 
      paramInt = localTypedValue.type;
      throw new IllegalArgumentException(38 + "Unexpected dimension type: " + paramInt);
    }
    for (long l = zza(localTypedValue.data);; l = zzc(128, localTypedValue.data))
    {
      paramTypedArray.putLong(paramString, l);
      break;
    }
  }
  
  private final void zza(TypedArray paramTypedArray, int paramInt, String paramString1, String paramString2)
  {
    if ((this.zzgb.containsKey(paramString1)) || (this.zzgb.containsKey(paramString2))) {}
    for (;;)
    {
      return;
      paramTypedArray = paramTypedArray.peekValue(paramInt);
      if (paramTypedArray != null) {
        if ((paramTypedArray.type >= 28) && (paramTypedArray.type <= 31)) {
          this.zzgb.putInt(paramString1, paramTypedArray.data);
        } else {
          this.zzgb.putInt(paramString2, paramTypedArray.resourceId);
        }
      }
    }
  }
  
  private final void zzb(TypedArray paramTypedArray, int paramInt, String paramString)
  {
    if (this.zzgb.containsKey(paramString)) {}
    for (;;)
    {
      return;
      paramTypedArray = paramTypedArray.peekValue(paramInt);
      if (paramTypedArray != null) {
        this.zzgb.putInt(paramString, paramTypedArray.data);
      }
    }
  }
  
  private static long zzc(int paramInt1, int paramInt2)
  {
    return paramInt1 << 32 | paramInt2 & 0xFFFFFFFF;
  }
  
  public final WalletFragmentStyle setBuyButtonAppearance(int paramInt)
  {
    this.zzgb.putInt("buyButtonAppearance", paramInt);
    return this;
  }
  
  public final WalletFragmentStyle setBuyButtonText(int paramInt)
  {
    this.zzgb.putInt("buyButtonText", paramInt);
    return this;
  }
  
  public final WalletFragmentStyle setBuyButtonWidth(int paramInt)
  {
    this.zzgb.putLong("buyButtonWidth", zza(paramInt));
    return this;
  }
  
  public final WalletFragmentStyle setStyleResourceId(int paramInt)
  {
    this.zzgc = paramInt;
    return this;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBundle(paramParcel, 2, this.zzgb, false);
    SafeParcelWriter.writeInt(paramParcel, 3, this.zzgc);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public final int zza(String paramString, DisplayMetrics paramDisplayMetrics, int paramInt)
  {
    int i;
    int j;
    if (this.zzgb.containsKey(paramString))
    {
      long l = this.zzgb.getLong(paramString);
      i = (int)(l >>> 32);
      j = (int)l;
      paramInt = j;
    }
    switch (i)
    {
    default: 
      throw new IllegalStateException(36 + "Unexpected unit or type: " + i);
    case 128: 
      paramInt = TypedValue.complexToDimensionPixelSize(j, paramDisplayMetrics);
    case 129: 
      return paramInt;
    case 0: 
      paramInt = 0;
    }
    for (;;)
    {
      paramInt = Math.round(TypedValue.applyDimension(paramInt, Float.intBitsToFloat(j), paramDisplayMetrics));
      break;
      paramInt = 1;
      continue;
      paramInt = 2;
      continue;
      paramInt = 3;
      continue;
      paramInt = 4;
      continue;
      paramInt = 5;
    }
  }
  
  public final void zza(Context paramContext)
  {
    if (this.zzgc <= 0) {}
    for (int i = R.style.WalletFragmentDefaultStyle;; i = this.zzgc)
    {
      paramContext = paramContext.obtainStyledAttributes(i, R.styleable.WalletFragmentStyle);
      zza(paramContext, R.styleable.WalletFragmentStyle_buyButtonWidth, "buyButtonWidth");
      zza(paramContext, R.styleable.WalletFragmentStyle_buyButtonHeight, "buyButtonHeight");
      zzb(paramContext, R.styleable.WalletFragmentStyle_buyButtonText, "buyButtonText");
      zzb(paramContext, R.styleable.WalletFragmentStyle_buyButtonAppearance, "buyButtonAppearance");
      zzb(paramContext, R.styleable.WalletFragmentStyle_maskedWalletDetailsTextAppearance, "maskedWalletDetailsTextAppearance");
      zzb(paramContext, R.styleable.WalletFragmentStyle_maskedWalletDetailsHeaderTextAppearance, "maskedWalletDetailsHeaderTextAppearance");
      zza(paramContext, R.styleable.WalletFragmentStyle_maskedWalletDetailsBackground, "maskedWalletDetailsBackgroundColor", "maskedWalletDetailsBackgroundResource");
      zzb(paramContext, R.styleable.WalletFragmentStyle_maskedWalletDetailsButtonTextAppearance, "maskedWalletDetailsButtonTextAppearance");
      zza(paramContext, R.styleable.WalletFragmentStyle_maskedWalletDetailsButtonBackground, "maskedWalletDetailsButtonBackgroundColor", "maskedWalletDetailsButtonBackgroundResource");
      zzb(paramContext, R.styleable.WalletFragmentStyle_maskedWalletDetailsLogoTextColor, "maskedWalletDetailsLogoTextColor");
      zzb(paramContext, R.styleable.WalletFragmentStyle_maskedWalletDetailsLogoImageType, "maskedWalletDetailsLogoImageType");
      paramContext.recycle();
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/fragment/WalletFragmentStyle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */