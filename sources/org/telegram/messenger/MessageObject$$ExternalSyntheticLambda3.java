package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.AnimatedEmojiDrawable;

public final /* synthetic */ class MessageObject$$ExternalSyntheticLambda3 implements AnimatedEmojiDrawable.ReceivedDocument {
    public final /* synthetic */ MessageObject f$0;

    public /* synthetic */ MessageObject$$ExternalSyntheticLambda3(MessageObject messageObject) {
        this.f$0 = messageObject;
    }

    public final void run(TLRPC$Document tLRPC$Document) {
        this.f$0.lambda$loadAnimatedEmojiDocument$1(tLRPC$Document);
    }
}
