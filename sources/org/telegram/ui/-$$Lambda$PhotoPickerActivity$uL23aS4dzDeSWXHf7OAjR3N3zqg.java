package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoPickerActivity$uL23aS4dzDeSWXHf7OAjR3N3zqg implements RequestDelegate {
    private final /* synthetic */ PhotoPickerActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$PhotoPickerActivity$uL23aS4dzDeSWXHf7OAjR3N3zqg(PhotoPickerActivity photoPickerActivity, int i, boolean z) {
        this.f$0 = photoPickerActivity;
        this.f$1 = i;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchImages$9$PhotoPickerActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
