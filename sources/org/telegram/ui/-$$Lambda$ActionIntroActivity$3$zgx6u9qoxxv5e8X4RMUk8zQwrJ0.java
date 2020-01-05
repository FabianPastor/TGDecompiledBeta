package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionIntroActivity.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ActionIntroActivity$3$zgx6u9qoxxv5e8X4RMUk8zQwrJ0 implements Runnable {
    private final /* synthetic */ AnonymousClass3 f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ byte[] f$2;
    private final /* synthetic */ TL_error f$3;
    private final /* synthetic */ AlertDialog f$4;

    public /* synthetic */ -$$Lambda$ActionIntroActivity$3$zgx6u9qoxxv5e8X4RMUk8zQwrJ0(AnonymousClass3 anonymousClass3, TLObject tLObject, byte[] bArr, TL_error tL_error, AlertDialog alertDialog) {
        this.f$0 = anonymousClass3;
        this.f$1 = tLObject;
        this.f$2 = bArr;
        this.f$3 = tL_error;
        this.f$4 = alertDialog;
    }

    public final void run() {
        this.f$0.lambda$null$7$ActionIntroActivity$3(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
