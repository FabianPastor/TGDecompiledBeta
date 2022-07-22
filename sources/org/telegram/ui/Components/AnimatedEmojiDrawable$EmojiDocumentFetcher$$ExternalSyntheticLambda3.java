package org.telegram.ui.Components;

import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.ui.Components.AnimatedEmojiDrawable;

public final /* synthetic */ class AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ AnimatedEmojiDrawable.EmojiDocumentFetcher f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ HashSet f$2;

    public /* synthetic */ AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda3(AnimatedEmojiDrawable.EmojiDocumentFetcher emojiDocumentFetcher, ArrayList arrayList, HashSet hashSet) {
        this.f$0 = emojiDocumentFetcher;
        this.f$1 = arrayList;
        this.f$2 = hashSet;
    }

    public final void run() {
        this.f$0.lambda$loadFromDatabase$1(this.f$1, this.f$2);
    }
}
