package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class FiltersSetupActivity$$ExternalSyntheticLambda1 implements RequestDelegate {
    public static final /* synthetic */ FiltersSetupActivity$$ExternalSyntheticLambda1 INSTANCE = new FiltersSetupActivity$$ExternalSyntheticLambda1();

    private /* synthetic */ FiltersSetupActivity$$ExternalSyntheticLambda1() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FiltersSetupActivity.lambda$onFragmentDestroy$0(tLObject, tLRPC$TL_error);
    }
}
