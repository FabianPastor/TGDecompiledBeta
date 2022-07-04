package org.telegram.ui.Components;

import org.telegram.messenger.MessageObject;

public final /* synthetic */ class TranscribeButton$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ TranscribeButton$$ExternalSyntheticLambda1(int i, MessageObject messageObject) {
        this.f$0 = i;
        this.f$1 = messageObject;
    }

    public final void run() {
        TranscribeButton.lambda$transcribePressed$2(this.f$0, this.f$1);
    }
}
