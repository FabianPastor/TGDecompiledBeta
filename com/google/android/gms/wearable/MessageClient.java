package com.google.android.gms.wearable;

import android.content.Context;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApi.zza;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Wearable.WearableOptions;

public abstract class MessageClient extends GoogleApi<WearableOptions> {
    public MessageClient(Context context, zza com_google_android_gms_common_api_GoogleApi_zza) {
        super(context, Wearable.API, null, com_google_android_gms_common_api_GoogleApi_zza);
    }

    public abstract Task<Integer> sendMessage(String str, String str2, byte[] bArr);
}
