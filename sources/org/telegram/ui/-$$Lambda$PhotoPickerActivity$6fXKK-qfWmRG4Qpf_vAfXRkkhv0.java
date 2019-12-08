package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoPickerActivity$6fXKK-qfWmRG4Qpf_vAfXRkkhv0 implements RequestDelegate {
    private final /* synthetic */ PhotoPickerActivity f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$PhotoPickerActivity$6fXKK-qfWmRG4Qpf_vAfXRkkhv0(PhotoPickerActivity photoPickerActivity, boolean z) {
        this.f$0 = photoPickerActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchBotUser$9$PhotoPickerActivity(this.f$1, tLObject, tL_error);
    }
}
