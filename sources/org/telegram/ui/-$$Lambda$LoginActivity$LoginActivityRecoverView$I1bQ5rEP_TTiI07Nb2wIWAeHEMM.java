package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.LoginActivity.LoginActivityRecoverView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityRecoverView$I1bQ5rEP_TTiI07Nb2wIWAeHEMM implements OnClickListener {
    private final /* synthetic */ LoginActivityRecoverView f$0;
    private final /* synthetic */ TLObject f$1;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityRecoverView$I1bQ5rEP_TTiI07Nb2wIWAeHEMM(LoginActivityRecoverView loginActivityRecoverView, TLObject tLObject) {
        this.f$0 = loginActivityRecoverView;
        this.f$1 = tLObject;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$3$LoginActivity$LoginActivityRecoverView(this.f$1, dialogInterface, i);
    }
}
