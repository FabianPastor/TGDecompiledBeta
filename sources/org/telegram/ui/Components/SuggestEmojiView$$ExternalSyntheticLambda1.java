package org.telegram.ui.Components;

import java.util.ArrayList;

public final /* synthetic */ class SuggestEmojiView$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ SuggestEmojiView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ SuggestEmojiView$$ExternalSyntheticLambda1(SuggestEmojiView suggestEmojiView, int i, String str, ArrayList arrayList) {
        this.f$0 = suggestEmojiView;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$searchAnimated$3(this.f$1, this.f$2, this.f$3);
    }
}
