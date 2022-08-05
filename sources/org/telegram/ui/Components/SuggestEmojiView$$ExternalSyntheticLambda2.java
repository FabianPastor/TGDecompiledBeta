package org.telegram.ui.Components;

import java.util.ArrayList;

public final /* synthetic */ class SuggestEmojiView$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ SuggestEmojiView f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ SuggestEmojiView$$ExternalSyntheticLambda2(SuggestEmojiView suggestEmojiView, String str, ArrayList arrayList) {
        this.f$0 = suggestEmojiView;
        this.f$1 = str;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$searchAnimated$3(this.f$1, this.f$2);
    }
}
