package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;
import org.telegram.ui.PassportActivity.19.C15191;

final /* synthetic */ class PassportActivity$19$1$$Lambda$1 implements RequestDelegate {
    private final C15191 arg$1;
    private final String arg$2;
    private final TL_secureRequiredType arg$3;
    private final PassportActivityDelegate arg$4;
    private final ErrorRunnable arg$5;

    PassportActivity$19$1$$Lambda$1(C15191 c15191, String str, TL_secureRequiredType tL_secureRequiredType, PassportActivityDelegate passportActivityDelegate, ErrorRunnable errorRunnable) {
        this.arg$1 = c15191;
        this.arg$2 = str;
        this.arg$3 = tL_secureRequiredType;
        this.arg$4 = passportActivityDelegate;
        this.arg$5 = errorRunnable;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$2$PassportActivity$19$1(this.arg$2, this.arg$3, this.arg$4, this.arg$5, tLObject, tL_error);
    }
}
