package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbaz;
import java.io.File;
import java.io.FileNotFoundException;

final class zzfz implements Runnable {
    private /* synthetic */ String zzakq;
    private /* synthetic */ long zzbSm;
    private /* synthetic */ long zzbSn;
    private /* synthetic */ zzbaz zzbTq;
    private /* synthetic */ zzfw zzbTr;
    private /* synthetic */ Uri zzbzR;

    zzfz(zzfw com_google_android_gms_wearable_internal_zzfw, Uri uri, zzbaz com_google_android_gms_internal_zzbaz, String str, long j, long j2) {
        this.zzbTr = com_google_android_gms_wearable_internal_zzfw;
        this.zzbzR = uri;
        this.zzbTq = com_google_android_gms_internal_zzbaz;
        this.zzakq = str;
        this.zzbSm = j;
        this.zzbSn = j2;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        if (Log.isLoggable("WearableClient", 2)) {
            Log.v("WearableClient", "Executing sendFileToChannelTask");
        }
        if ("file".equals(this.zzbzR.getScheme())) {
            File file = new File(this.zzbzR.getPath());
            try {
                ParcelFileDescriptor open = ParcelFileDescriptor.open(file, 268435456);
                try {
                    ((zzdn) this.zzbTr.zzrf()).zza(new zzfs(this.zzbTq), this.zzakq, open, this.zzbSm, this.zzbSn);
                    try {
                        open.close();
                        return;
                    } catch (Throwable e) {
                        Log.w("WearableClient", "Failed to close sourceFd", e);
                        return;
                    }
                } catch (Throwable e2) {
                    Log.w("WearableClient", "Channel.sendFile failed.", e2);
                    this.zzbTq.zzr(new Status(8));
                    return;
                } catch (Throwable th) {
                    try {
                        open.close();
                    } catch (Throwable e3) {
                        Log.w("WearableClient", "Failed to close sourceFd", e3);
                    }
                }
            } catch (FileNotFoundException e4) {
                String valueOf = String.valueOf(file);
                Log.w("WearableClient", new StringBuilder(String.valueOf(valueOf).length() + 46).append("File couldn't be opened for Channel.sendFile: ").append(valueOf).toString());
                this.zzbTq.zzr(new Status(13));
                return;
            }
        }
        Log.w("WearableClient", "Channel.sendFile used with non-file URI");
        this.zzbTq.zzr(new Status(10, "Channel.sendFile used with non-file URI"));
    }
}
