package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda208 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ Object f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda208(ChatActivity chatActivity, Object obj) {
        this.f$0 = chatActivity;
        this.f$1 = obj;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$createView$42(this.f$1, z, i);
    }
}
