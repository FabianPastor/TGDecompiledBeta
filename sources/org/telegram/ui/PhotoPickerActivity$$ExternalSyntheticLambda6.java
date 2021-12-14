package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class PhotoPickerActivity$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ PhotoPickerActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ PhotoPickerActivity$$ExternalSyntheticLambda6(PhotoPickerActivity photoPickerActivity, TLObject tLObject, boolean z) {
        this.f$0 = photoPickerActivity;
        this.f$1 = tLObject;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$searchBotUser$8(this.f$1, this.f$2);
    }
}
