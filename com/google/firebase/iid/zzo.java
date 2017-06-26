package com.google.firebase.iid;

import android.content.Intent;
import android.os.ConditionVariable;
import android.util.Log;
import com.google.android.gms.iid.InstanceID;
import java.io.IOException;

final class zzo implements zzp {
    private Intent intent;
    private final ConditionVariable zzckG;
    private String zzckH;

    private zzo() {
        this.zzckG = new ConditionVariable();
    }

    public final void onError(String str) {
        this.zzckH = str;
        this.zzckG.open();
    }

    public final Intent zzJT() throws IOException {
        if (!this.zzckG.block(30000)) {
            Log.w("InstanceID/Rpc", "No response");
            throw new IOException(InstanceID.ERROR_TIMEOUT);
        } else if (this.zzckH == null) {
            return this.intent;
        } else {
            throw new IOException(this.zzckH);
        }
    }

    public final void zzq(Intent intent) {
        this.intent = intent;
        this.zzckG.open();
    }
}
