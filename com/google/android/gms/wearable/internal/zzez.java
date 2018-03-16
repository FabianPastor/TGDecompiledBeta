package com.google.android.gms.wearable.internal;

import android.content.Context;
import com.google.android.gms.common.api.GoogleApi.zza;
import com.google.android.gms.common.internal.zzbj;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageClient;

public final class zzez extends MessageClient {
    private MessageApi zzlku = new zzeu();

    public zzez(Context context, zza com_google_android_gms_common_api_GoogleApi_zza) {
        super(context, com_google_android_gms_common_api_GoogleApi_zza);
    }

    public final Task<Integer> sendMessage(String str, String str2, byte[] bArr) {
        return zzbj.zza(this.zzlku.sendMessage(zzago(), str, str2, bArr), zzfa.zzgnw);
    }
}
