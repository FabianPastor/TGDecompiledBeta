package org.telegram.messenger.ringtone;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class RingtoneUploader$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ RingtoneUploader f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ RingtoneUploader$$ExternalSyntheticLambda1(RingtoneUploader ringtoneUploader, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = ringtoneUploader;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$error$2(this.f$1);
    }
}
