package org.telegram.messenger;

import com.google.android.gms.tasks.OnSuccessListener;

final /* synthetic */ class AndroidUtilities$$Lambda$1 implements OnSuccessListener {
    static final OnSuccessListener $instance = new AndroidUtilities$$Lambda$1();

    private AndroidUtilities$$Lambda$1() {
    }

    public void onSuccess(Object obj) {
        AndroidUtilities.lambda$setWaitingForSms$1$AndroidUtilities((Void) obj);
    }
}
