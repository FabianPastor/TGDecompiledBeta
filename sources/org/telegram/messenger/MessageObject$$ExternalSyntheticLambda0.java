package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class MessageObject$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MessageObject f$0;
    public final /* synthetic */ TLRPC$Document f$1;

    public /* synthetic */ MessageObject$$ExternalSyntheticLambda0(MessageObject messageObject, TLRPC$Document tLRPC$Document) {
        this.f$0 = messageObject;
        this.f$1 = tLRPC$Document;
    }

    public final void run() {
        this.f$0.lambda$loadAnimatedEmojiDocument$0(this.f$1);
    }
}
