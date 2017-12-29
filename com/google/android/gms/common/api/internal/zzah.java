package com.google.android.gms.common.api.internal;

import android.support.v4.util.ArraySet;
import com.google.android.gms.common.ConnectionResult;

public class zzah extends zzo {
    private zzbm zzfmi;
    private final ArraySet<zzh<?>> zzfqs;

    private final void zzahy() {
        if (!this.zzfqs.isEmpty()) {
            this.zzfmi.zza(this);
        }
    }

    public final void onResume() {
        super.onResume();
        zzahy();
    }

    public final void onStart() {
        super.onStart();
        zzahy();
    }

    public final void onStop() {
        super.onStop();
        this.zzfmi.zzb(this);
    }

    protected final void zza(ConnectionResult connectionResult, int i) {
        this.zzfmi.zza(connectionResult, i);
    }

    protected final void zzagz() {
        this.zzfmi.zzagz();
    }

    final ArraySet<zzh<?>> zzahx() {
        return this.zzfqs;
    }
}
