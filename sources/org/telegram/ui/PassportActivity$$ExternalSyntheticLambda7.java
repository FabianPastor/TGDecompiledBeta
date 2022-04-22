package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$TL_auth_passwordRecovery;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda7 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ TLRPC$TL_auth_passwordRecovery f$1;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda7(PassportActivity passportActivity, TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery) {
        this.f$0 = passportActivity;
        this.f$1 = tLRPC$TL_auth_passwordRecovery;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createPasswordInterface$8(this.f$1, dialogInterface, i);
    }
}
