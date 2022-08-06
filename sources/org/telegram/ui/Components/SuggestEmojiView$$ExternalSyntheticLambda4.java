package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.messenger.MediaDataController;

public final /* synthetic */ class SuggestEmojiView$$ExternalSyntheticLambda4 implements MediaDataController.KeywordResultCallback {
    public final /* synthetic */ SuggestEmojiView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ SuggestEmojiView$$ExternalSyntheticLambda4(SuggestEmojiView suggestEmojiView, int i, String str) {
        this.f$0 = suggestEmojiView;
        this.f$1 = i;
        this.f$2 = str;
    }

    public final void run(ArrayList arrayList, String str) {
        this.f$0.lambda$searchKeywords$1(this.f$1, this.f$2, arrayList, str);
    }
}
