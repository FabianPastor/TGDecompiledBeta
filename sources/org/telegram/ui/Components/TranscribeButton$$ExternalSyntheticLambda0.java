package org.telegram.ui.Components;

import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class TranscribeButton$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ TranscribeButton$$ExternalSyntheticLambda0(int i, MessageObject messageObject) {
        this.f$0 = i;
        this.f$1 = messageObject;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.voiceTranscriptionUpdate, this.f$1, null, null, Boolean.FALSE, null);
    }
}
