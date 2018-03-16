package com.google.android.gms.wearable;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.wearable.internal.zzaa;
import com.google.android.gms.wearable.internal.zzaj;
import com.google.android.gms.wearable.internal.zzbv;
import com.google.android.gms.wearable.internal.zzbw;
import com.google.android.gms.wearable.internal.zzeu;
import com.google.android.gms.wearable.internal.zzez;
import com.google.android.gms.wearable.internal.zzfg;
import com.google.android.gms.wearable.internal.zzgi;
import com.google.android.gms.wearable.internal.zzh;
import com.google.android.gms.wearable.internal.zzhg;
import com.google.android.gms.wearable.internal.zzhq;
import com.google.android.gms.wearable.internal.zzk;
import com.google.android.gms.wearable.internal.zzo;

public class Wearable {
    @Deprecated
    public static final Api<WearableOptions> API = new Api("Wearable.API", zzebg, zzebf);
    @Deprecated
    public static final CapabilityApi CapabilityApi = new zzo();
    @Deprecated
    public static final ChannelApi ChannelApi = new zzaj();
    @Deprecated
    public static final DataApi DataApi = new zzbw();
    @Deprecated
    public static final MessageApi MessageApi = new zzeu();
    @Deprecated
    public static final NodeApi NodeApi = new zzfg();
    private static final zzf<zzhg> zzebf = new zzf();
    private static final zza<zzhg, WearableOptions> zzebg = new zzj();
    @Deprecated
    private static zzc zzlgy = new zzk();
    @Deprecated
    private static zza zzlgz = new zzh();
    @Deprecated
    private static zzf zzlha = new zzbv();
    @Deprecated
    private static zzi zzlhb = new zzgi();
    @Deprecated
    private static zzu zzlhc = new zzhq();

    public static final class WearableOptions implements Optional {
        private final Looper zzfml;

        public static class Builder {
            private Looper zzfml;
        }

        private WearableOptions(Builder builder) {
            this.zzfml = builder.zzfml;
        }
    }

    public static CapabilityClient getCapabilityClient(Context context) {
        return new zzaa(context, GoogleApi.zza.zzfmj);
    }

    public static MessageClient getMessageClient(Context context) {
        return new zzez(context, GoogleApi.zza.zzfmj);
    }
}
