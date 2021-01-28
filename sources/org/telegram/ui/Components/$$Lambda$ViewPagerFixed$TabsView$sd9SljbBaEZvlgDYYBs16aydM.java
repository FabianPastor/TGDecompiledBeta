package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.ViewPagerFixed;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ViewPagerFixed$TabsView$sd9S-ljbBaEZvlgDYYB-s16aydM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ViewPagerFixed$TabsView$sd9SljbBaEZvlgDYYBs16aydM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$ViewPagerFixed$TabsView$sd9SljbBaEZvlgDYYBs16aydM INSTANCE = new $$Lambda$ViewPagerFixed$TabsView$sd9SljbBaEZvlgDYYBs16aydM();

    private /* synthetic */ $$Lambda$ViewPagerFixed$TabsView$sd9SljbBaEZvlgDYYBs16aydM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ViewPagerFixed.TabsView.lambda$setIsEditing$1(tLObject, tLRPC$TL_error);
    }
}
