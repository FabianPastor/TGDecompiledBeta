package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda73 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ TLRPC.TL_secureRequiredType f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda73(PassportActivity passportActivity, TLRPC.TL_secureRequiredType tL_secureRequiredType, boolean z) {
        this.f$0 = passportActivity;
        this.f$1 = tL_secureRequiredType;
        this.f$2 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3360lambda$addField$63$orgtelegramuiPassportActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
