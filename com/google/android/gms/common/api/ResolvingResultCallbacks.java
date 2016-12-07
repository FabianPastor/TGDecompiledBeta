package com.google.android.gms.common.api;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.internal.zzaa;

public abstract class ResolvingResultCallbacks<R extends Result> extends ResultCallbacks<R> {
    private final Activity mActivity;
    private final int xW;

    protected ResolvingResultCallbacks(@NonNull Activity activity, int i) {
        this.mActivity = (Activity) zzaa.zzb((Object) activity, (Object) "Activity must not be null");
        this.xW = i;
    }

    public final void onFailure(@NonNull Status status) {
        if (status.hasResolution()) {
            try {
                status.startResolutionForResult(this.mActivity, this.xW);
                return;
            } catch (Throwable e) {
                Log.e("ResolvingResultCallback", "Failed to start resolution", e);
                onUnresolvableFailure(new Status(8));
                return;
            }
        }
        onUnresolvableFailure(status);
    }

    public abstract void onSuccess(@NonNull R r);

    public abstract void onUnresolvableFailure(@NonNull Status status);
}
