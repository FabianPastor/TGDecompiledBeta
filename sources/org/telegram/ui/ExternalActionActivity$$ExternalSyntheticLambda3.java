package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ExternalActionActivity$$ExternalSyntheticLambda3 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ ExternalActionActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ ExternalActionActivity$$ExternalSyntheticLambda3(ExternalActionActivity externalActionActivity, TLRPC.TL_error tL_error) {
        this.f$0 = externalActionActivity;
        this.f$1 = tL_error;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.m2139lambda$handleIntent$8$orgtelegramuiExternalActionActivity(this.f$1, dialogInterface);
    }
}
