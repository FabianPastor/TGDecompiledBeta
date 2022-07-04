package org.telegram.ui.Components;

import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class TranscribeButton$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ MessageObject f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ TranscribeButton$$ExternalSyntheticLambda2(MessageObject messageObject, long j, String str) {
        this.f$0 = messageObject;
        this.f$1 = j;
        this.f$2 = str;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0.currentAccount).postNotificationName(NotificationCenter.voiceTranscriptionUpdate, this.f$0, Long.valueOf(this.f$1), this.f$2, true, true);
    }
}
