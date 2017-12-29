package com.google.android.gms.dynamic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

final class zzf implements OnClickListener {
    private /* synthetic */ Context val$context;
    private /* synthetic */ Intent zzgwl;

    zzf(Context context, Intent intent) {
        this.val$context = context;
        this.zzgwl = intent;
    }

    public final void onClick(View view) {
        try {
            this.val$context.startActivity(this.zzgwl);
        } catch (Throwable e) {
            Log.e("DeferredLifecycleHelper", "Failed to start resolution intent", e);
        }
    }
}
