package com.google.android.gms.wallet.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class WalletFragmentOptions extends zza implements ReflectedParcelable {
    public static final Creator<WalletFragmentOptions> CREATOR = new zzf();
    private int mTheme;
    private int zzaLU;
    private int zzbPR;
    private WalletFragmentStyle zzbQt;

    public final class Builder {
        private /* synthetic */ WalletFragmentOptions zzbQu;

        private Builder(WalletFragmentOptions walletFragmentOptions) {
            this.zzbQu = walletFragmentOptions;
        }

        public final WalletFragmentOptions build() {
            return this.zzbQu;
        }

        public final Builder setEnvironment(int i) {
            this.zzbQu.zzbPR = i;
            return this;
        }

        public final Builder setFragmentStyle(int i) {
            this.zzbQu.zzbQt = new WalletFragmentStyle().setStyleResourceId(i);
            return this;
        }

        public final Builder setFragmentStyle(WalletFragmentStyle walletFragmentStyle) {
            this.zzbQu.zzbQt = walletFragmentStyle;
            return this;
        }

        public final Builder setMode(int i) {
            this.zzbQu.zzaLU = i;
            return this;
        }

        public final Builder setTheme(int i) {
            this.zzbQu.mTheme = i;
            return this;
        }
    }

    private WalletFragmentOptions() {
        this.zzbPR = 3;
        this.zzbQt = new WalletFragmentStyle();
    }

    WalletFragmentOptions(int i, int i2, WalletFragmentStyle walletFragmentStyle, int i3) {
        this.zzbPR = i;
        this.mTheme = i2;
        this.zzbQt = walletFragmentStyle;
        this.zzaLU = i3;
    }

    public static Builder newBuilder() {
        WalletFragmentOptions walletFragmentOptions = new WalletFragmentOptions();
        walletFragmentOptions.getClass();
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
        walletFragmentOptions.zzbPR = i2;
        walletFragmentOptions.zzbQt = new WalletFragmentStyle().setStyleResourceId(resourceId);
        walletFragmentOptions.zzbQt.zzby(context);
        walletFragmentOptions.zzaLU = i3;
        return walletFragmentOptions;
    }

    public final int getEnvironment() {
        return this.zzbPR;
    }

    public final WalletFragmentStyle getFragmentStyle() {
        return this.zzbQt;
    }

    public final int getMode() {
        return this.zzaLU;
    }

    public final int getTheme() {
        return this.mTheme;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, getEnvironment());
        zzd.zzc(parcel, 3, getTheme());
        zzd.zza(parcel, 4, getFragmentStyle(), i, false);
        zzd.zzc(parcel, 5, getMode());
        zzd.zzI(parcel, zze);
    }

    public final void zzby(Context context) {
        if (this.zzbQt != null) {
            this.zzbQt.zzby(context);
        }
    }
}
