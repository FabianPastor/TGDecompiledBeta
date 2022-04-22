package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$TL_secureRequiredType;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda9 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ TLRPC$TL_secureRequiredType f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda9(PassportActivity passportActivity, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, boolean z) {
        this.f$0 = passportActivity;
        this.f$1 = tLRPC$TL_secureRequiredType;
        this.f$2 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$addField$63(this.f$1, this.f$2, dialogInterface, i);
    }
}
