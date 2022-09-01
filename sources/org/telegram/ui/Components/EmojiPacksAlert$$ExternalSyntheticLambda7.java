package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.messenger.Utilities;

public final /* synthetic */ class EmojiPacksAlert$$ExternalSyntheticLambda7 implements Utilities.Callback {
    public final /* synthetic */ EmojiPacksAlert f$0;
    public final /* synthetic */ int[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ EmojiPacksAlert$$ExternalSyntheticLambda7(EmojiPacksAlert emojiPacksAlert, int[] iArr, int i, ArrayList arrayList) {
        this.f$0 = emojiPacksAlert;
        this.f$1 = iArr;
        this.f$2 = i;
        this.f$3 = arrayList;
    }

    public final void run(Object obj) {
        this.f$0.lambda$updateButton$8(this.f$1, this.f$2, this.f$3, (Boolean) obj);
    }
}
