package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC.Document;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$f0V3X4ZqkkxYVwPpugUT__2f3kU implements ScheduleDatePickerDelegate {
    private final /* synthetic */ ChatActivityEnterView f$0;
    private final /* synthetic */ Document f$1;
    private final /* synthetic */ Object f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$f0V3X4ZqkkxYVwPpugUT__2f3kU(ChatActivityEnterView chatActivityEnterView, Document document, Object obj, boolean z) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = document;
        this.f$2 = obj;
        this.f$3 = z;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onStickerSelected$27$ChatActivityEnterView(this.f$1, this.f$2, this.f$3, z, i);
    }
}
