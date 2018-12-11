package org.telegram.messenger;

import android.content.Intent;

final /* synthetic */ class GoogleVoiceClientService$$Lambda$0 implements Runnable {
    private final Intent arg$1;

    GoogleVoiceClientService$$Lambda$0(Intent intent) {
        this.arg$1 = intent;
    }

    public void run() {
        GoogleVoiceClientService.lambda$performAction$0$GoogleVoiceClientService(this.arg$1);
    }
}
