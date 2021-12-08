package org.telegram.messenger;

import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda23 implements Runnable {
    public final /* synthetic */ MediaController f$0;
    public final /* synthetic */ MessagesController.EmojiSound f$1;
    public final /* synthetic */ AccountInstance f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda23(MediaController mediaController, MessagesController.EmojiSound emojiSound, AccountInstance accountInstance, boolean z) {
        this.f$0 = mediaController;
        this.f$1 = emojiSound;
        this.f$2 = accountInstance;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.m97lambda$playEmojiSound$18$orgtelegrammessengerMediaController(this.f$1, this.f$2, this.f$3);
    }
}
