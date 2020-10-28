package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ViewPagerFixed;

/* renamed from: org.telegram.ui.-$$Lambda$ViewPagerFixed$TabsView$kEasDyzuu3wchfo8pVCslw6OutE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ViewPagerFixed$TabsView$kEasDyzuu3wchfo8pVCslw6OutE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$ViewPagerFixed$TabsView$kEasDyzuu3wchfo8pVCslw6OutE INSTANCE = new $$Lambda$ViewPagerFixed$TabsView$kEasDyzuu3wchfo8pVCslw6OutE();

    private /* synthetic */ $$Lambda$ViewPagerFixed$TabsView$kEasDyzuu3wchfo8pVCslw6OutE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ViewPagerFixed.TabsView.lambda$setIsEditing$1(tLObject, tLRPC$TL_error);
    }
}
