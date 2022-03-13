package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatActivityEnterView;

public final /* synthetic */ class ChatActivityEnterView$54$$ExternalSyntheticLambda1 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivityEnterView.AnonymousClass54 f$0;
    public final /* synthetic */ View f$1;
    public final /* synthetic */ Object f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ Object f$4;

    public /* synthetic */ ChatActivityEnterView$54$$ExternalSyntheticLambda1(ChatActivityEnterView.AnonymousClass54 r1, View view, Object obj, String str, Object obj2) {
        this.f$0 = r1;
        this.f$1 = view;
        this.f$2 = obj;
        this.f$3 = str;
        this.f$4 = obj2;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onGifSelected$0(this.f$1, this.f$2, this.f$3, this.f$4, z, i);
    }
}
