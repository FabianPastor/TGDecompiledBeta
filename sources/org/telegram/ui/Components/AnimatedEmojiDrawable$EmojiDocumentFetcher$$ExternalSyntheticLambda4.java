package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.Components.AnimatedEmojiDrawable;

public final /* synthetic */ class AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ AnimatedEmojiDrawable.EmojiDocumentFetcher f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda4(AnimatedEmojiDrawable.EmojiDocumentFetcher emojiDocumentFetcher, ArrayList arrayList, TLObject tLObject) {
        this.f$0 = emojiDocumentFetcher;
        this.f$1 = arrayList;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadFromServer$3(this.f$1, this.f$2);
    }
}
