package org.telegram.messenger.ringtone;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class RingtoneUploader$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ RingtoneUploader f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;

    public /* synthetic */ RingtoneUploader$$ExternalSyntheticLambda0(RingtoneUploader ringtoneUploader, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = ringtoneUploader;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$0(this.f$1, this.f$2);
    }
}
