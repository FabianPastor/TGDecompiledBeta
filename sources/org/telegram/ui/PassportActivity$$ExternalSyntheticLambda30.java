package org.telegram.ui;

import android.view.View;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$TL_secureRequiredType;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda30 implements View.OnClickListener {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC$TL_secureRequiredType f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda30(PassportActivity passportActivity, ArrayList arrayList, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, boolean z) {
        this.f$0 = passportActivity;
        this.f$1 = arrayList;
        this.f$2 = tLRPC$TL_secureRequiredType;
        this.f$3 = z;
    }

    public final void onClick(View view) {
        this.f$0.lambda$addField$64(this.f$1, this.f$2, this.f$3, view);
    }
}
