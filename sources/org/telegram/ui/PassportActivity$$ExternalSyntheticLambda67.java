package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_secureRequiredType;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda67 implements RequestDelegate {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ PassportActivity.ErrorRunnable f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC$TL_secureRequiredType f$3;
    public final /* synthetic */ TLRPC$TL_secureRequiredType f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ Runnable f$7;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda67(PassportActivity passportActivity, PassportActivity.ErrorRunnable errorRunnable, boolean z, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2, boolean z2, ArrayList arrayList, Runnable runnable) {
        this.f$0 = passportActivity;
        this.f$1 = errorRunnable;
        this.f$2 = z;
        this.f$3 = tLRPC$TL_secureRequiredType;
        this.f$4 = tLRPC$TL_secureRequiredType2;
        this.f$5 = z2;
        this.f$6 = arrayList;
        this.f$7 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$deleteValueInternal$60(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
    }
}