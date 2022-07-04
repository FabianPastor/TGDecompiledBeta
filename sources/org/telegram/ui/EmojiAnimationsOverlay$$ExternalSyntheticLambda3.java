package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class EmojiAnimationsOverlay$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ EmojiAnimationsOverlay f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ EmojiAnimationsOverlay$$ExternalSyntheticLambda3(EmojiAnimationsOverlay emojiAnimationsOverlay, MessageObject messageObject) {
        this.f$0 = emojiAnimationsOverlay;
        this.f$1 = messageObject;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$showAnimationForCell$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
