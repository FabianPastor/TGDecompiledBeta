package com.google.android.gms.wallet.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class WalletFragmentOptions extends zzbfm implements ReflectedParcelable {
    public static final Creator<WalletFragmentOptions> CREATOR = new zzf();
    private int mTheme;
    private int zzgir;
    private int zzlea;
    private WalletFragmentStyle zzlfh;

    public final class Builder {
        private /* synthetic */ WalletFragmentOptions zzlfi;

        private Builder(WalletFragmentOptions walletFragmentOptions) {
            this.zzlfi = walletFragmentOptions;
        }

        public final WalletFragmentOptions build() {
            return this.zzlfi;
        }

        public final Builder setEnvironment(int i) {
            this.zzlfi.zzlea = i;
            return this;
        }

        public final Builder setFragmentStyle(WalletFragmentStyle walletFragmentStyle) {
            this.zzlfi.zzlfh = walletFragmentStyle;
            return this;
        }

        public final Builder setMode(int i) {
            this.zzlfi.zzgir = i;
            return this;
        }
    }

    private WalletFragmentOptions() {
        this.zzlea = 3;
        this.zzlfh = new WalletFragmentStyle();
    }

    WalletFragmentOptions(int i, int i2, WalletFragmentStyle walletFragmentStyle, int i3) {
        this.zzlea = i;
        this.mTheme = i2;
        this.zzlfh = walletFragmentStyle;
        this.zzgir = i3;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static WalletFragmentOptions zza(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.WalletFragmentOptions);
        int i = obtainStyledAttributes.getInt(R.styleable.WalletFragmentOptions_appTheme, 0);
        int i2 = obtainStyledAttributes.getInt(R.styleable.WalletFragmentOptions_environment, 1);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.WalletFragmentOptions_fragmentStyle, 0);
        int i3 = obtainStyledAttributes.getInt(R.styleable.WalletFragmentOptions_fragmentMode, 1);
        obtainStyledAttributes.recycle();
        WalletFragmentOptions walletFragmentOptions = new WalletFragmentOptions();
        walletFragmentOptions.mTheme = i;
        walletFragmentOptions.zzlea = i2;
        walletFragmentOptions.zzlfh = new WalletFragmentStyle().setStyleResourceId(resourceId);
        walletFragmentOptions.zzlfh.zzeo(context);
        walletFragmentOptions.zzgir = i3;
        return walletFragmentOptions;
    }

    public final int getEnvironment() {
        return this.zzlea;
    }

    public final WalletFragmentStyle getFragmentStyle() {
        return this.zzlfh;
    }

    public final int getMode() {
        return this.zzgir;
    }

    public final int getTheme() {
        return this.mTheme;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, getEnvironment());
        zzbfp.zzc(parcel, 3, getTheme());
        zzbfp.zza(parcel, 4, getFragmentStyle(), i, false);
        zzbfp.zzc(parcel, 5, getMode());
        zzbfp.zzai(parcel, zze);
    }

    public final void zzeo(Context context) {
        if (this.zzlfh != null) {
            this.zzlfh.zzeo(context);
        }
    }
}
