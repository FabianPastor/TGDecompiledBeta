package com.google.android.gms.internal;

import android.accounts.Account;
import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.identity.intents.AddressConstants.ErrorCodes;
import com.google.android.gms.identity.intents.AddressConstants.Extras;
import com.google.android.gms.identity.intents.UserAddressRequest;

public final class zzcbd extends zzz<zzcbh> {
    private Activity mActivity;
    private final int mTheme;
    private final String zzakh;
    private zzcbe zzbgD;

    public zzcbd(Activity activity, Looper looper, zzq com_google_android_gms_common_internal_zzq, int i, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(activity, looper, 12, com_google_android_gms_common_internal_zzq, connectionCallbacks, onConnectionFailedListener);
        this.zzakh = com_google_android_gms_common_internal_zzq.getAccountName();
        this.mActivity = activity;
        this.mTheme = i;
    }

    public final void disconnect() {
        super.disconnect();
        if (this.zzbgD != null) {
            this.zzbgD.setActivity(null);
            this.zzbgD = null;
        }
    }

    public final void zza(UserAddressRequest userAddressRequest, int i) {
        super.zzre();
        this.zzbgD = new zzcbe(i, this.mActivity);
        try {
            Bundle bundle = new Bundle();
            bundle.putString("com.google.android.gms.identity.intents.EXTRA_CALLING_PACKAGE_NAME", getContext().getPackageName());
            if (!TextUtils.isEmpty(this.zzakh)) {
                bundle.putParcelable("com.google.android.gms.identity.intents.EXTRA_ACCOUNT", new Account(this.zzakh, "com.google"));
            }
            bundle.putInt("com.google.android.gms.identity.intents.EXTRA_THEME", this.mTheme);
            ((zzcbh) super.zzrf()).zza(this.zzbgD, userAddressRequest, bundle);
        } catch (Throwable e) {
            Log.e("AddressClientImpl", "Exception requesting user address", e);
            Bundle bundle2 = new Bundle();
            bundle2.putInt(Extras.EXTRA_ERROR_CODE, ErrorCodes.ERROR_CODE_NO_APPLICABLE_ADDRESSES);
            this.zzbgD.zze(1, bundle2);
        }
    }

    protected final /* synthetic */ IInterface zzd(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.identity.intents.internal.IAddressService");
        return queryLocalInterface instanceof zzcbh ? (zzcbh) queryLocalInterface : new zzcbi(iBinder);
    }

    protected final String zzdb() {
        return "com.google.android.gms.identity.service.BIND";
    }

    protected final String zzdc() {
        return "com.google.android.gms.identity.intents.internal.IAddressService";
    }

    public final boolean zzrg() {
        return true;
    }
}
