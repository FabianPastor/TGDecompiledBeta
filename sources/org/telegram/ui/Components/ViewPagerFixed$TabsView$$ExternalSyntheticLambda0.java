package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ViewPagerFixed;

public final /* synthetic */ class ViewPagerFixed$TabsView$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final /* synthetic */ ViewPagerFixed$TabsView$$ExternalSyntheticLambda0 INSTANCE = new ViewPagerFixed$TabsView$$ExternalSyntheticLambda0();

    private /* synthetic */ ViewPagerFixed$TabsView$$ExternalSyntheticLambda0() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        ViewPagerFixed.TabsView.lambda$setIsEditing$1(tLObject, tL_error);
    }
}
