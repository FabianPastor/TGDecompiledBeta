package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$1W4kZzPjzRDtXCpazovyaTPi9gk implements RequestDelegate {
    private final /* synthetic */ LoginActivitySmsView f$0;
    private final /* synthetic */ TL_account_confirmPhone f$1;

    public /* synthetic */ -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$1W4kZzPjzRDtXCpazovyaTPi9gk(LoginActivitySmsView loginActivitySmsView, TL_account_confirmPhone tL_account_confirmPhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_account_confirmPhone;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onNextPressed$7$CancelAccountDeletionActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
    }
}
