package com.google.android.gms.wearable.internal;

import android.content.Context;
import com.google.android.gms.common.api.GoogleApi.zza;
import com.google.android.gms.common.internal.zzbj;
import com.google.android.gms.common.internal.zzc;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;

public final class zzaa extends CapabilityClient {
    private final CapabilityApi zzlir = new zzo();

    public zzaa(Context context, zza com_google_android_gms_common_api_GoogleApi_zza) {
        super(context, com_google_android_gms_common_api_GoogleApi_zza);
    }

    public final Task<CapabilityInfo> getCapability(String str, int i) {
        zzc.zzb(str, "capability must not be null");
        return zzbj.zza(this.zzlir.getCapability(zzago(), str, i), zzab.zzgnw);
    }
}
