package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda23 implements DialogInterface.OnClickListener {
    public final /* synthetic */ BaseFragment f$0;
    public final /* synthetic */ TLRPC.User f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda23(BaseFragment baseFragment, TLRPC.User user, boolean z) {
        this.f$0 = baseFragment;
        this.f$1 = user;
        this.f$2 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createCallDialogAlert$21(this.f$0, this.f$1, this.f$2, dialogInterface, i);
    }
}
