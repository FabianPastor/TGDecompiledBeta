package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.messenger.MediaDataController;

public final /* synthetic */ class SelectAnimatedEmojiDialog$$ExternalSyntheticLambda16 implements MediaDataController.KeywordResultCallback {
    public final /* synthetic */ SelectAnimatedEmojiDialog f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ SelectAnimatedEmojiDialog$$ExternalSyntheticLambda16(SelectAnimatedEmojiDialog selectAnimatedEmojiDialog, String str, ArrayList arrayList, boolean z) {
        this.f$0 = selectAnimatedEmojiDialog;
        this.f$1 = str;
        this.f$2 = arrayList;
        this.f$3 = z;
    }

    public final void run(ArrayList arrayList, String str) {
        this.f$0.lambda$search$11(this.f$1, this.f$2, this.f$3, arrayList, str);
    }
}
