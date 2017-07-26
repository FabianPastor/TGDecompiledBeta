package com.google.android.gms.wallet.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class WalletFragmentStyle extends zza {
    public static final Creator<WalletFragmentStyle> CREATOR = new zzg();
    private Bundle zzbQx;
    private int zzbQy;

    @Retention(RetentionPolicy.SOURCE)
    public @interface BuyButtonAppearance {
        public static final int ANDROID_PAY_DARK = 4;
        public static final int ANDROID_PAY_LIGHT = 5;
        public static final int ANDROID_PAY_LIGHT_WITH_BORDER = 6;
        @Deprecated
        public static final int GOOGLE_WALLET_CLASSIC = 1;
        @Deprecated
        public static final int GOOGLE_WALLET_GRAYSCALE = 2;
        @Deprecated
        public static final int GOOGLE_WALLET_MONOCHROME = 3;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface BuyButtonText {
        public static final int BUY_WITH = 5;
        public static final int DONATE_WITH = 7;
        public static final int LOGO_ONLY = 6;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Dimension {
        public static final int MATCH_PARENT = -1;
        public static final int UNIT_DIP = 1;
        public static final int UNIT_IN = 4;
        public static final int UNIT_MM = 5;
        public static final int UNIT_PT = 3;
        public static final int UNIT_PX = 0;
        public static final int UNIT_SP = 2;
        public static final int WRAP_CONTENT = -2;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface LogoImageType {
        public static final int ANDROID_PAY = 3;
        @Deprecated
        public static final int GOOGLE_WALLET_CLASSIC = 1;
        @Deprecated
        public static final int GOOGLE_WALLET_MONOCHROME = 2;
    }

    public WalletFragmentStyle() {
        this.zzbQx = new Bundle();
        this.zzbQx.putInt("buyButtonAppearanceDefault", 4);
        this.zzbQx.putInt("maskedWalletDetailsLogoImageTypeDefault", 3);
    }

    WalletFragmentStyle(Bundle bundle, int i) {
        this.zzbQx = bundle;
        this.zzbQy = i;
    }

    private final void zza(TypedArray typedArray, int i, String str) {
        if (!this.zzbQx.containsKey(str)) {
            TypedValue peekValue = typedArray.peekValue(i);
            if (peekValue != null) {
                long zzl;
                Bundle bundle = this.zzbQx;
                switch (peekValue.type) {
                    case 5:
                        zzl = zzl(128, peekValue.data);
                        break;
                    case 16:
                        zzl = zzbO(peekValue.data);
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected dimension type: " + peekValue.type);
                }
                bundle.putLong(str, zzl);
            }
        }
    }

    private final void zza(TypedArray typedArray, int i, String str, String str2) {
        if (!this.zzbQx.containsKey(str) && !this.zzbQx.containsKey(str2)) {
            TypedValue peekValue = typedArray.peekValue(i);
            if (peekValue == null) {
                return;
            }
            if (peekValue.type < 28 || peekValue.type > 31) {
                this.zzbQx.putInt(str2, peekValue.resourceId);
            } else {
                this.zzbQx.putInt(str, peekValue.data);
            }
        }
    }

    private static long zzb(int i, float f) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return zzl(i, Float.floatToIntBits(f));
            default:
                throw new IllegalArgumentException("Unrecognized unit: " + i);
        }
    }

    private final void zzb(TypedArray typedArray, int i, String str) {
        if (!this.zzbQx.containsKey(str)) {
            TypedValue peekValue = typedArray.peekValue(i);
            if (peekValue != null) {
                this.zzbQx.putInt(str, peekValue.data);
            }
        }
    }

    private static long zzbO(int i) {
        if (i >= 0) {
            return zzb(0, (float) i);
        }
        if (i == -1 || i == -2) {
            return zzl(TsExtractor.TS_STREAM_TYPE_AC3, i);
        }
        throw new IllegalArgumentException("Unexpected dimension value: " + i);
    }

    private static long zzl(int i, int i2) {
        return (((long) i) << 32) | (((long) i2) & 4294967295L);
    }

    public final WalletFragmentStyle setBuyButtonAppearance(int i) {
        this.zzbQx.putInt("buyButtonAppearance", i);
        return this;
    }

    public final WalletFragmentStyle setBuyButtonHeight(int i) {
        this.zzbQx.putLong("buyButtonHeight", zzbO(i));
        return this;
    }

    public final WalletFragmentStyle setBuyButtonHeight(int i, float f) {
        this.zzbQx.putLong("buyButtonHeight", zzb(i, f));
        return this;
    }

    public final WalletFragmentStyle setBuyButtonText(int i) {
        this.zzbQx.putInt("buyButtonText", i);
        return this;
    }

    public final WalletFragmentStyle setBuyButtonWidth(int i) {
        this.zzbQx.putLong("buyButtonWidth", zzbO(i));
        return this;
    }

    public final WalletFragmentStyle setBuyButtonWidth(int i, float f) {
        this.zzbQx.putLong("buyButtonWidth", zzb(i, f));
        return this;
    }

    public final WalletFragmentStyle setMaskedWalletDetailsBackgroundColor(int i) {
        this.zzbQx.remove("maskedWalletDetailsBackgroundResource");
        this.zzbQx.putInt("maskedWalletDetailsBackgroundColor", i);
        return this;
    }

    public final WalletFragmentStyle setMaskedWalletDetailsBackgroundResource(int i) {
        this.zzbQx.remove("maskedWalletDetailsBackgroundColor");
        this.zzbQx.putInt("maskedWalletDetailsBackgroundResource", i);
        return this;
    }

    public final WalletFragmentStyle setMaskedWalletDetailsButtonBackgroundColor(int i) {
        this.zzbQx.remove("maskedWalletDetailsButtonBackgroundResource");
        this.zzbQx.putInt("maskedWalletDetailsButtonBackgroundColor", i);
        return this;
    }

    public final WalletFragmentStyle setMaskedWalletDetailsButtonBackgroundResource(int i) {
        this.zzbQx.remove("maskedWalletDetailsButtonBackgroundColor");
        this.zzbQx.putInt("maskedWalletDetailsButtonBackgroundResource", i);
        return this;
    }

    public final WalletFragmentStyle setMaskedWalletDetailsButtonTextAppearance(int i) {
        this.zzbQx.putInt("maskedWalletDetailsButtonTextAppearance", i);
        return this;
    }

    public final WalletFragmentStyle setMaskedWalletDetailsHeaderTextAppearance(int i) {
        this.zzbQx.putInt("maskedWalletDetailsHeaderTextAppearance", i);
        return this;
    }

    public final WalletFragmentStyle setMaskedWalletDetailsLogoImageType(int i) {
        this.zzbQx.putInt("maskedWalletDetailsLogoImageType", i);
        return this;
    }

    @Deprecated
    public final WalletFragmentStyle setMaskedWalletDetailsLogoTextColor(int i) {
        this.zzbQx.putInt("maskedWalletDetailsLogoTextColor", i);
        return this;
    }

    public final WalletFragmentStyle setMaskedWalletDetailsTextAppearance(int i) {
        this.zzbQx.putInt("maskedWalletDetailsTextAppearance", i);
        return this;
    }

    public final WalletFragmentStyle setStyleResourceId(int i) {
        this.zzbQy = i;
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbQx, false);
        zzd.zzc(parcel, 3, this.zzbQy);
        zzd.zzI(parcel, zze);
    }

    public final int zza(String str, DisplayMetrics displayMetrics, int i) {
        if (!this.zzbQx.containsKey(str)) {
            return i;
        }
        int i2;
        long j = this.zzbQx.getLong(str);
        int i3 = (int) (j >>> 32);
        i = (int) j;
        switch (i3) {
            case 0:
                i2 = 0;
                break;
            case 1:
                i2 = 1;
                break;
            case 2:
                i2 = 2;
                break;
            case 3:
                i2 = 3;
                break;
            case 4:
                i2 = 4;
                break;
            case 5:
                i2 = 5;
                break;
            case 128:
                return TypedValue.complexToDimensionPixelSize(i, displayMetrics);
            case TsExtractor.TS_STREAM_TYPE_AC3 /*129*/:
                return i;
            default:
                throw new IllegalStateException("Unexpected unit or type: " + i3);
        }
        return Math.round(TypedValue.applyDimension(i2, Float.intBitsToFloat(i), displayMetrics));
    }

    public final void zzby(Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(this.zzbQy <= 0 ? R.style.WalletFragmentDefaultStyle : this.zzbQy, R.styleable.WalletFragmentStyle);
        zza(obtainStyledAttributes, R.styleable.WalletFragmentStyle_buyButtonWidth, "buyButtonWidth");
        zza(obtainStyledAttributes, R.styleable.WalletFragmentStyle_buyButtonHeight, "buyButtonHeight");
        zzb(obtainStyledAttributes, R.styleable.WalletFragmentStyle_buyButtonText, "buyButtonText");
        zzb(obtainStyledAttributes, R.styleable.WalletFragmentStyle_buyButtonAppearance, "buyButtonAppearance");
        zzb(obtainStyledAttributes, R.styleable.WalletFragmentStyle_maskedWalletDetailsTextAppearance, "maskedWalletDetailsTextAppearance");
        zzb(obtainStyledAttributes, R.styleable.WalletFragmentStyle_maskedWalletDetailsHeaderTextAppearance, "maskedWalletDetailsHeaderTextAppearance");
        zza(obtainStyledAttributes, R.styleable.WalletFragmentStyle_maskedWalletDetailsBackground, "maskedWalletDetailsBackgroundColor", "maskedWalletDetailsBackgroundResource");
        zzb(obtainStyledAttributes, R.styleable.WalletFragmentStyle_maskedWalletDetailsButtonTextAppearance, "maskedWalletDetailsButtonTextAppearance");
        zza(obtainStyledAttributes, R.styleable.WalletFragmentStyle_maskedWalletDetailsButtonBackground, "maskedWalletDetailsButtonBackgroundColor", "maskedWalletDetailsButtonBackgroundResource");
        zzb(obtainStyledAttributes, R.styleable.WalletFragmentStyle_maskedWalletDetailsLogoTextColor, "maskedWalletDetailsLogoTextColor");
        zzb(obtainStyledAttributes, R.styleable.WalletFragmentStyle_maskedWalletDetailsLogoImageType, "maskedWalletDetailsLogoImageType");
        obtainStyledAttributes.recycle();
    }
}
