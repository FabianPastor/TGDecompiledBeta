package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.AnimatedEmojiDrawable;

public final /* synthetic */ class AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ AnimatedEmojiDrawable.EmojiDocumentFetcher f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda1(AnimatedEmojiDrawable.EmojiDocumentFetcher emojiDocumentFetcher, ArrayList arrayList) {
        this.f$0 = emojiDocumentFetcher;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$loadFromDatabase$2(this.f$1);
    }
}
