package com.google.android.gms.dynamic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

final class zzf implements OnClickListener {
    private /* synthetic */ Intent zzaSA;
    private /* synthetic */ Context zztF;

    zzf(Context context, Intent intent) {
        this.zztF = context;
        this.zzaSA = intent;
    }

    public final void onClick(View view) {
        try {
            this.zztF.startActivity(this.zzaSA);
        } catch (Throwable e) {
            Log.e("DeferredLifecycleHelper", "Failed to start resolution intent", e);
        }
    }
}
