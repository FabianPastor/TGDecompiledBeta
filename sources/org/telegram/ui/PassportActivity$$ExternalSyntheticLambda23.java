package org.telegram.ui;

import android.view.View;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda23 implements View.OnClickListener {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC.TL_secureRequiredType f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda23(PassportActivity passportActivity, ArrayList arrayList, TLRPC.TL_secureRequiredType tL_secureRequiredType, boolean z) {
        this.f$0 = passportActivity;
        this.f$1 = arrayList;
        this.f$2 = tL_secureRequiredType;
        this.f$3 = z;
    }

    public final void onClick(View view) {
        this.f$0.m4029lambda$addField$64$orgtelegramuiPassportActivity(this.f$1, this.f$2, this.f$3, view);
    }
}
