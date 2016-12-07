package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.zzd;
import java.util.Iterator;

public class zzqi extends zzqd {
    public void onStop() {
        Object obj = null;
        super.onStop();
        Iterator it = obj.iterator();
        while (it.hasNext()) {
            ((zzd) it.next()).release();
        }
        obj.clear();
        obj.zza(this);
    }

    protected void zza(ConnectionResult connectionResult, int i) {
        zzqt com_google_android_gms_internal_zzqt = null;
        com_google_android_gms_internal_zzqt.zza(connectionResult, i);
    }

    protected void zzaqk() {
        zzqt com_google_android_gms_internal_zzqt = null;
        com_google_android_gms_internal_zzqt.zzaqk();
    }
}
