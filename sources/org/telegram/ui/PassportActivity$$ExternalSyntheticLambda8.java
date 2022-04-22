package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$TL_secureRequiredType;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda8 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ TLRPC$TL_secureRequiredType f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda8(PassportActivity passportActivity, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, ArrayList arrayList, boolean z) {
        this.f$0 = passportActivity;
        this.f$1 = tLRPC$TL_secureRequiredType;
        this.f$2 = arrayList;
        this.f$3 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$addField$61(this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}
