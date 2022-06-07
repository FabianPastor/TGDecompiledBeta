package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class EmojiAnimationsOverlay$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ EmojiAnimationsOverlay f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ MessageObject f$2;

    public /* synthetic */ EmojiAnimationsOverlay$$ExternalSyntheticLambda2(EmojiAnimationsOverlay emojiAnimationsOverlay, TLObject tLObject, MessageObject messageObject) {
        this.f$0 = emojiAnimationsOverlay;
        this.f$1 = tLObject;
        this.f$2 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$showAnimationForCell$0(this.f$1, this.f$2);
    }
}
