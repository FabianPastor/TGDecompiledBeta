package org.telegram.ui.Components;

import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatActivityEnterView;

public final /* synthetic */ class ChatActivityEnterView$17$$ExternalSyntheticLambda0 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivityEnterView.AnonymousClass17 f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ChatActivityEnterView$17$$ExternalSyntheticLambda0(ChatActivityEnterView.AnonymousClass17 r1, String str) {
        this.f$0 = r1;
        this.f$1 = str;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onItemClick$0(this.f$1, z, i);
    }
}
