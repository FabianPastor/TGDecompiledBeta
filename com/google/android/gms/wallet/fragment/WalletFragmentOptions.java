package com.google.android.gms.wallet.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class WalletFragmentOptions extends zza implements ReflectedParcelable {
    public static final Creator<WalletFragmentOptions> CREATOR = new zzb();
    private int mTheme;
    private int zzaKF;
    private int zzbRx;
    private WalletFragmentStyle zzbSn;

    public final class Builder {
        final /* synthetic */ WalletFragmentOptions zzbSo;

        private Builder(WalletFragmentOptions walletFragmentOptions) {
            this.zzbSo = walletFragmentOptions;
        }

        public WalletFragmentOptions build() {
            return this.zzbSo;
        }

        public Builder setEnvironment(int i) {
            this.zzbSo.zzbRx = i;
            return this;
        }

        public Builder setFragmentStyle(int i) {
            this.zzbSo.zzbSn = new WalletFragmentStyle().setStyleResourceId(i);
            return this;
        }

        public Builder setFragmentStyle(WalletFragmentStyle walletFragmentStyle) {
            this.zzbSo.zzbSn = walletFragmentStyle;
            return this;
        }

        public Builder setMode(int i) {
            this.zzbSo.zzaKF = i;
            return this;
        }

        public Builder setTheme(int i) {
            this.zzbSo.mTheme = i;
            return this;
        }
    }

    private WalletFragmentOptions() {
        this.zzbRx = 3;
        this.zzbSn = new WalletFragmentStyle();
    }

    WalletFragmentOptions(int i, int i2, WalletFragmentStyle walletFragmentStyle, int i3) {
        this.zzbRx = i;
        this.mTheme = i2;
        this.zzbSn = walletFragmentStyle;
        this.zzaKF = i3;
    }

    public static Builder newBuilder() {
        WalletFragmentOptions walletFragmentOptions = new WalletFragmentOptions();
        walletFragmentOptions.getClass();
        return new Builder();
    }

    public static WalletFragmentOptions zzc(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.WalletFragmentOptions);
        int i = obtainStyledAttributes.getInt(R.styleable.WalletFragmentOptions_appTheme, 0);
        int i2 = obtainStyledAttributes.getInt(R.styleable.WalletFragmentOptions_environment, 1);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.WalletFragmentOptions_fragmentStyle, 0);
        int i3 = obtainStyledAttributes.getInt(R.styleable.WalletFragmentOptions_fragmentMode, 1);
        obtainStyledAttributes.recycle();
        WalletFragmentOptions walletFragmentOptions = new WalletFragmentOptions();
        walletFragmentOptions.mTheme = i;
        walletFragmentOptions.zzbRx = i2;
        walletFragmentOptions.zzbSn = new WalletFragmentStyle().setStyleResourceId(resourceId);
        walletFragmentOptions.zzbSn.zzci(context);
        walletFragmentOptions.zzaKF = i3;
        return walletFragmentOptions;
    }

    public int getEnvironment() {
        return this.zzbRx;
    }

    public WalletFragmentStyle getFragmentStyle() {
        return this.zzbSn;
    }

    public int getMode() {
        return this.zzaKF;
    }

    public int getTheme() {
        return this.mTheme;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public void zzci(Context context) {
        if (this.zzbSn != null) {
            this.zzbSn.zzci(context);
        }
    }
}
