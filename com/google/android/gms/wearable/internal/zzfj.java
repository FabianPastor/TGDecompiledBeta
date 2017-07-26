package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import java.io.OutputStream;

final class zzfj extends zzfc<GetOutputStreamResult> {
    private final zzbd zzbTf;

    public zzfj(zzbaz<GetOutputStreamResult> com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_Channel_GetOutputStreamResult, zzbd com_google_android_gms_wearable_internal_zzbd) {
        super(com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_Channel_GetOutputStreamResult);
        this.zzbTf = (zzbd) zzbo.zzu(com_google_android_gms_wearable_internal_zzbd);
    }

    public final void zza(zzcm com_google_android_gms_wearable_internal_zzcm) {
        OutputStream outputStream = null;
        if (com_google_android_gms_wearable_internal_zzcm.zzbSI != null) {
            outputStream = new zzax(new AutoCloseOutputStream(com_google_android_gms_wearable_internal_zzcm.zzbSI));
            this.zzbTf.zza(new zzay(outputStream));
        }
        zzR(new zzat(new Status(com_google_android_gms_wearable_internal_zzcm.statusCode), outputStream));
    }
}
