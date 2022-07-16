package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_messages_getCustomEmojiDocuments;
import org.telegram.ui.Components.AnimatedEmojiDrawable;

public final /* synthetic */ class AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ AnimatedEmojiDrawable.EmojiDocumentFetcher f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$TL_messages_getCustomEmojiDocuments f$2;

    public /* synthetic */ AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda1(AnimatedEmojiDrawable.EmojiDocumentFetcher emojiDocumentFetcher, TLObject tLObject, TLRPC$TL_messages_getCustomEmojiDocuments tLRPC$TL_messages_getCustomEmojiDocuments) {
        this.f$0 = emojiDocumentFetcher;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$TL_messages_getCustomEmojiDocuments;
    }

    public final void run() {
        this.f$0.lambda$fetchDocument$0(this.f$1, this.f$2);
    }
}
