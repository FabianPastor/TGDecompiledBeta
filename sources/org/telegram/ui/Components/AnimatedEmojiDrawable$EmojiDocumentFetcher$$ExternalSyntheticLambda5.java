package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.AnimatedEmojiDrawable;

public final /* synthetic */ class AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ AnimatedEmojiDrawable.EmojiDocumentFetcher f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda5(AnimatedEmojiDrawable.EmojiDocumentFetcher emojiDocumentFetcher, ArrayList arrayList) {
        this.f$0 = emojiDocumentFetcher;
        this.f$1 = arrayList;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadFromServer$4(this.f$1, tLObject, tLRPC$TL_error);
    }
}
