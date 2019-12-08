package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoPickerActivity$qpeBmgUBgVV3zpHgxM1HYvyfk7w implements RequestDelegate {
    private final /* synthetic */ PhotoPickerActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$PhotoPickerActivity$qpeBmgUBgVV3zpHgxM1HYvyfk7w(PhotoPickerActivity photoPickerActivity, int i, boolean z) {
        this.f$0 = photoPickerActivity;
        this.f$1 = i;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchImages$8$PhotoPickerActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
