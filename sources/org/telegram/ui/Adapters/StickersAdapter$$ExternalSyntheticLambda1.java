package org.telegram.ui.Adapters;

import java.util.ArrayList;
import org.telegram.messenger.MediaDataController;

public final /* synthetic */ class StickersAdapter$$ExternalSyntheticLambda1 implements MediaDataController.KeywordResultCallback {
    public final /* synthetic */ StickersAdapter f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ StickersAdapter$$ExternalSyntheticLambda1(StickersAdapter stickersAdapter, String str) {
        this.f$0 = stickersAdapter;
        this.f$1 = str;
    }

    public final void run(ArrayList arrayList, String str) {
        this.f$0.lambda$searchEmojiByKeyword$0(this.f$1, arrayList, str);
    }
}
