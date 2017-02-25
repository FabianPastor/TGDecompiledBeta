package com.google.android.gms.identity.intents;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.HasOptions;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.internal.zzaqo;

public final class Address {
    public static final Api<AddressOptions> API = new Api("Address.API", zzaie, zzaid);
    static final zzf<zzaqo> zzaid = new zzf();
    private static final com.google.android.gms.common.api.Api.zza<zzaqo, AddressOptions> zzaie = new com.google.android.gms.common.api.Api.zza<zzaqo, AddressOptions>() {
        public zzaqo zza(Context context, Looper looper, zzg com_google_android_gms_common_internal_zzg, AddressOptions addressOptions, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            zzac.zzb(context instanceof Activity, (Object) "An Activity must be used for Address APIs");
            if (addressOptions == null) {
                addressOptions = new AddressOptions();
            }
            return new zzaqo((Activity) context, looper, com_google_android_gms_common_internal_zzg, addressOptions.theme, connectionCallbacks, onConnectionFailedListener);
        }
    };

    public static final class AddressOptions implements HasOptions {
        public final int theme;

        public AddressOptions() {
            this.theme = 0;
        }

        public AddressOptions(int i) {
            this.theme = i;
        }
    }

    private static abstract class zza extends com.google.android.gms.internal.zzaad.zza<Status, zzaqo> {
        public zza(GoogleApiClient googleApiClient) {
            super(Address.API, googleApiClient);
        }

        public /* synthetic */ void setResult(Object obj) {
            super.zzb((Status) obj);
        }

        public Status zzb(Status status) {
            return status;
        }

        public /* synthetic */ Result zzc(Status status) {
            return zzb(status);
        }
    }

    class AnonymousClass2 extends zza {
        final /* synthetic */ int val$requestCode;
        final /* synthetic */ UserAddressRequest zzbhq;

        AnonymousClass2(GoogleApiClient googleApiClient, UserAddressRequest userAddressRequest, int i) {
            this.zzbhq = userAddressRequest;
            this.val$requestCode = i;
            super(googleApiClient);
        }

        protected void zza(zzaqo com_google_android_gms_internal_zzaqo) throws RemoteException {
            com_google_android_gms_internal_zzaqo.zza(this.zzbhq, this.val$requestCode);
            zzb(Status.zzazx);
        }
    }

    public static void requestUserAddress(GoogleApiClient googleApiClient, UserAddressRequest userAddressRequest, int i) {
        googleApiClient.zza(new AnonymousClass2(googleApiClient, userAddressRequest, i));
    }
}
