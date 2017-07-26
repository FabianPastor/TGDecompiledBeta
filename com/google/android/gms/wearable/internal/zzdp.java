package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.wearable.WearableStatusCodes;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

final class zzdp<T> {
    private final Map<T, zzga<T>> zzaWU = new HashMap();

    zzdp() {
    }

    public final void zza(zzfw com_google_android_gms_wearable_internal_zzfw, zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, T t) throws RemoteException {
        synchronized (this.zzaWU) {
            zzga com_google_android_gms_wearable_internal_zzga = (zzga) this.zzaWU.remove(t);
            if (com_google_android_gms_wearable_internal_zzga == null) {
                com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status.setResult(new Status(WearableStatusCodes.UNKNOWN_LISTENER));
                return;
            }
            com_google_android_gms_wearable_internal_zzga.clear();
            ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zza(new zzdr(this.zzaWU, t, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status), new zzeo(com_google_android_gms_wearable_internal_zzga));
        }
    }

    public final void zza(zzfw com_google_android_gms_wearable_internal_zzfw, zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, T t, zzga<T> com_google_android_gms_wearable_internal_zzga_T) throws RemoteException {
        synchronized (this.zzaWU) {
            if (this.zzaWU.get(t) != null) {
                com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status.setResult(new Status(WearableStatusCodes.DUPLICATE_LISTENER));
                return;
            }
            this.zzaWU.put(t, com_google_android_gms_wearable_internal_zzga_T);
            try {
                ((zzdn) com_google_android_gms_wearable_internal_zzfw.zzrf()).zza(new zzdq(this.zzaWU, t, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status), new zzd(com_google_android_gms_wearable_internal_zzga_T));
            } catch (RemoteException e) {
                this.zzaWU.remove(t);
                throw e;
            }
        }
    }

    public final void zzam(IBinder iBinder) {
        synchronized (this.zzaWU) {
            zzdn com_google_android_gms_wearable_internal_zzdn;
            if (iBinder == null) {
                com_google_android_gms_wearable_internal_zzdn = null;
            } else {
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IWearableService");
                if (queryLocalInterface instanceof zzdn) {
                    com_google_android_gms_wearable_internal_zzdn = (zzdn) queryLocalInterface;
                } else {
                    Object com_google_android_gms_wearable_internal_zzdo = new zzdo(iBinder);
                }
            }
            zzdi com_google_android_gms_wearable_internal_zzfp = new zzfp();
            for (Entry entry : this.zzaWU.entrySet()) {
                zzga com_google_android_gms_wearable_internal_zzga = (zzga) entry.getValue();
                try {
                    com_google_android_gms_wearable_internal_zzdn.zza(com_google_android_gms_wearable_internal_zzfp, new zzd(com_google_android_gms_wearable_internal_zzga));
                    if (Log.isLoggable("WearableClient", 2)) {
                        String valueOf = String.valueOf(entry.getKey());
                        String valueOf2 = String.valueOf(com_google_android_gms_wearable_internal_zzga);
                        Log.d("WearableClient", new StringBuilder((String.valueOf(valueOf).length() + 27) + String.valueOf(valueOf2).length()).append("onPostInitHandler: added: ").append(valueOf).append("/").append(valueOf2).toString());
                    }
                } catch (RemoteException e) {
                    String valueOf3 = String.valueOf(entry.getKey());
                    String valueOf4 = String.valueOf(com_google_android_gms_wearable_internal_zzga);
                    Log.d("WearableClient", new StringBuilder((String.valueOf(valueOf3).length() + 32) + String.valueOf(valueOf4).length()).append("onPostInitHandler: Didn't add: ").append(valueOf3).append("/").append(valueOf4).toString());
                }
            }
        }
    }
}
