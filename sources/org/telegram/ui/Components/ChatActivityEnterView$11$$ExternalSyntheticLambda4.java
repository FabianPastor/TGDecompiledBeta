package org.telegram.ui.Components;

import androidx.core.view.inputmethod.InputContentInfoCompat;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatActivityEnterView;

public final /* synthetic */ class ChatActivityEnterView$11$$ExternalSyntheticLambda4 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivityEnterView.AnonymousClass11 f$0;
    public final /* synthetic */ InputContentInfoCompat f$1;

    public /* synthetic */ ChatActivityEnterView$11$$ExternalSyntheticLambda4(ChatActivityEnterView.AnonymousClass11 r1, InputContentInfoCompat inputContentInfoCompat) {
        this.f$0 = r1;
        this.f$1 = inputContentInfoCompat;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onCreateInputConnection$0(this.f$1, z, i);
    }
}
