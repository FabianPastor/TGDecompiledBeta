package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.PhoneView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$PhoneView$w7_kYbY4pRrWACLASSNAMExO5d-AXHa18 implements RequestDelegate {
    private final /* synthetic */ PhoneView f$0;
    private final /* synthetic */ HashMap f$1;

    public /* synthetic */ -$$Lambda$LoginActivity$PhoneView$w7_kYbY4pRrWACLASSNAMExO5d-AXHa18(PhoneView phoneView, HashMap hashMap) {
        this.f$0 = phoneView;
        this.f$1 = hashMap;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$new$7$LoginActivity$PhoneView(this.f$1, tLObject, tL_error);
    }
}
