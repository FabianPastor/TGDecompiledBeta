package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda139 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_document f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ Object f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda139(ChatActivity chatActivity, TLRPC.TL_document tL_document, String str, Object obj) {
        this.f$0 = chatActivity;
        this.f$1 = tL_document;
        this.f$2 = str;
        this.f$3 = obj;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.m1684lambda$createView$43$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3, z, i);
    }
}
