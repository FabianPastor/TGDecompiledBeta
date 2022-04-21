package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda72 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ TLRPC.TL_secureRequiredType f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda72(PassportActivity passportActivity, TLRPC.TL_secureRequiredType tL_secureRequiredType, ArrayList arrayList, boolean z) {
        this.f$0 = passportActivity;
        this.f$1 = tL_secureRequiredType;
        this.f$2 = arrayList;
        this.f$3 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m2700lambda$addField$61$orgtelegramuiPassportActivity(this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}
