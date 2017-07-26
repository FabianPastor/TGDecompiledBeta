package com.google.firebase.iid;

import android.content.Intent;
import android.os.ConditionVariable;
import android.util.Log;
import com.google.android.gms.iid.InstanceID;
import java.io.IOException;

final class zzo implements zzp {
    private Intent intent;
    private final ConditionVariable zzckK;
    private String zzckL;

    private zzo() {
        this.zzckK = new ConditionVariable();
    }

    public final void onError(String str) {
        this.zzckL = str;
        this.zzckK.open();
    }

    public final Intent zzJW() throws IOException {
        if (!this.zzckK.block(30000)) {
            Log.w("InstanceID/Rpc", "No response");
            throw new IOException(InstanceID.ERROR_TIMEOUT);
        } else if (this.zzckL == null) {
            return this.intent;
        } else {
            throw new IOException(this.zzckL);
        }
    }

    public final void zzq(Intent intent) {
        this.intent = intent;
        this.zzckK.open();
    }
}
