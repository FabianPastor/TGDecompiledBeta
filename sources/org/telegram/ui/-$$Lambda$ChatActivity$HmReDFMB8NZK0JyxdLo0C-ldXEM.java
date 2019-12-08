package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$HmReDFMB8NZK0JyxdLo0C-ldXEM implements ScheduleDatePickerDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ TL_document f$1;
    private final /* synthetic */ Object f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$HmReDFMB8NZK0JyxdLo0C-ldXEM(ChatActivity chatActivity, TL_document tL_document, Object obj) {
        this.f$0 = chatActivity;
        this.f$1 = tL_document;
        this.f$2 = obj;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$null$48$ChatActivity(this.f$1, this.f$2, z, i);
    }
}
