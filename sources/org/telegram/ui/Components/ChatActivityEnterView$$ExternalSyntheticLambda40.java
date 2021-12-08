package org.telegram.ui.Components;

import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda40 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivityEnterView f$0;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda40(ChatActivityEnterView chatActivityEnterView) {
        this.f$0 = chatActivityEnterView;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendMessageInternal(z, i);
    }
}
