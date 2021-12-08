package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda56 implements Runnable {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ PassportActivity.ErrorRunnable f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ TLRPC.TL_secureRequiredType f$4;
    public final /* synthetic */ TLRPC.TL_secureRequiredType f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ Runnable f$8;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda56(PassportActivity passportActivity, TLRPC.TL_error tL_error, PassportActivity.ErrorRunnable errorRunnable, boolean z, TLRPC.TL_secureRequiredType tL_secureRequiredType, TLRPC.TL_secureRequiredType tL_secureRequiredType2, boolean z2, ArrayList arrayList, Runnable runnable) {
        this.f$0 = passportActivity;
        this.f$1 = tL_error;
        this.f$2 = errorRunnable;
        this.f$3 = z;
        this.f$4 = tL_secureRequiredType;
        this.f$5 = tL_secureRequiredType2;
        this.f$6 = z2;
        this.f$7 = arrayList;
        this.f$8 = runnable;
    }

    public final void run() {
        this.f$0.m3411lambda$deleteValueInternal$59$orgtelegramuiPassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
