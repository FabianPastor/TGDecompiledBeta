package org.telegram.ui.Components;

public final /* synthetic */ class SuggestEmojiView$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ SuggestEmojiView f$0;
    public final /* synthetic */ String[] f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ SuggestEmojiView$$ExternalSyntheticLambda3(SuggestEmojiView suggestEmojiView, String[] strArr, String str, int i) {
        this.f$0 = suggestEmojiView;
        this.f$1 = strArr;
        this.f$2 = str;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$searchKeywords$2(this.f$1, this.f$2, this.f$3);
    }
}
