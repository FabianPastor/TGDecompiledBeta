package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ExternalActionActivity$$ExternalSyntheticLambda2 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ ExternalActionActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ ExternalActionActivity$$ExternalSyntheticLambda2(ExternalActionActivity externalActionActivity, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = externalActionActivity;
        this.f$1 = tLRPC$TL_error;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.lambda$handleIntent$8(this.f$1, dialogInterface);
    }
}
