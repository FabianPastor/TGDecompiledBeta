package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

final class zzer<T> {
    private final Map<T, zzhk<T>> zzhhi = new HashMap();

    zzer() {
    }

    public final void zzbr(IBinder iBinder) {
        synchronized (this.zzhhi) {
            zzep com_google_android_gms_wearable_internal_zzep;
            if (iBinder == null) {
                com_google_android_gms_wearable_internal_zzep = null;
            } else {
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IWearableService");
                if (queryLocalInterface instanceof zzep) {
                    com_google_android_gms_wearable_internal_zzep = (zzep) queryLocalInterface;
                } else {
                    Object com_google_android_gms_wearable_internal_zzeq = new zzeq(iBinder);
                }
            }
            zzek com_google_android_gms_wearable_internal_zzgz = new zzgz();
            for (Entry entry : this.zzhhi.entrySet()) {
                zzhk com_google_android_gms_wearable_internal_zzhk = (zzhk) entry.getValue();
                try {
                    com_google_android_gms_wearable_internal_zzep.zza(com_google_android_gms_wearable_internal_zzgz, new zzd(com_google_android_gms_wearable_internal_zzhk));
                    if (Log.isLoggable("WearableClient", 3)) {
                        String valueOf = String.valueOf(entry.getKey());
                        String valueOf2 = String.valueOf(com_google_android_gms_wearable_internal_zzhk);
                        Log.d("WearableClient", new StringBuilder((String.valueOf(valueOf).length() + 27) + String.valueOf(valueOf2).length()).append("onPostInitHandler: added: ").append(valueOf).append("/").append(valueOf2).toString());
                    }
                } catch (RemoteException e) {
                    String valueOf3 = String.valueOf(entry.getKey());
                    String valueOf4 = String.valueOf(com_google_android_gms_wearable_internal_zzhk);
                    Log.w("WearableClient", new StringBuilder((String.valueOf(valueOf3).length() + 32) + String.valueOf(valueOf4).length()).append("onPostInitHandler: Didn't add: ").append(valueOf3).append("/").append(valueOf4).toString());
                }
            }
        }
    }
}
