package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class EmojiAnimationsOverlay$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ EmojiAnimationsOverlay f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ EmojiAnimationsOverlay$$ExternalSyntheticLambda3(EmojiAnimationsOverlay emojiAnimationsOverlay, MessageObject messageObject) {
        this.f$0 = emojiAnimationsOverlay;
        this.f$1 = messageObject;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3443x9867d307(this.f$1, tLObject, tL_error);
    }
}
