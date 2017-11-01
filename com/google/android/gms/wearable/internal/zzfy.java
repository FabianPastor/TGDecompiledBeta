package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbaz;
import java.io.File;
import java.io.FileNotFoundException;
import org.telegram.tgnet.ConnectionsManager;

final class zzfy implements Runnable {
    private /* synthetic */ String zzakq;
    private /* synthetic */ boolean zzbSl;
    private /* synthetic */ zzbaz zzbTq;
    private /* synthetic */ zzfw zzbTr;
    private /* synthetic */ Uri zzbzR;

    zzfy(zzfw com_google_android_gms_wearable_internal_zzfw, Uri uri, zzbaz com_google_android_gms_internal_zzbaz, boolean z, String str) {
        this.zzbTr = com_google_android_gms_wearable_internal_zzfw;
        this.zzbzR = uri;
        this.zzbTq = com_google_android_gms_internal_zzbaz;
        this.zzbSl = z;
        this.zzakq = str;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        ParcelFileDescriptor open;
        if (Log.isLoggable("WearableClient", 2)) {
            Log.v("WearableClient", "Executing receiveFileFromChannelTask");
        }
        if ("file".equals(this.zzbzR.getScheme())) {
            Object file = new File(this.zzbzR.getPath());
            try {
                open = ParcelFileDescriptor.open(file, (this.zzbSl ? ConnectionsManager.FileTypeVideo : 0) | 671088640);
            } catch (FileNotFoundException e) {
                String valueOf = String.valueOf(file);
                Log.w("WearableClient", new StringBuilder(String.valueOf(valueOf).length() + 49).append("File couldn't be opened for Channel.receiveFile: ").append(valueOf).toString());
                this.zzbTq.zzr(new Status(13));
                return;
            }
            try {
                ((zzdn) this.zzbTr.zzrf()).zza(new zzfv(this.zzbTq), this.zzakq, open);
                try {
                    open.close();
                } catch (Throwable e2) {
                    Log.w("WearableClient", "Failed to close targetFd", e2);
                }
            } catch (Throwable e22) {
                Log.w("WearableClient", "Channel.receiveFile failed.", e22);
                this.zzbTq.zzr(new Status(8));
            } catch (Throwable th) {
                try {
                    open.close();
                } catch (Throwable e3) {
                    Log.w("WearableClient", "Failed to close targetFd", e3);
                }
            }
        } else {
            Log.w("WearableClient", "Channel.receiveFile used with non-file URI");
            this.zzbTq.zzr(new Status(10, "Channel.receiveFile used with non-file URI"));
        }
    }
}
