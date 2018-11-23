package org.telegram.messenger;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.InstanceIdResult;

final /* synthetic */ class ApplicationLoader$$Lambda$3 implements OnSuccessListener {
    static final OnSuccessListener $instance = new ApplicationLoader$$Lambda$3();

    private ApplicationLoader$$Lambda$3() {
    }

    public void onSuccess(Object obj) {
        ApplicationLoader.lambda$null$0$ApplicationLoader((InstanceIdResult) obj);
    }
}
