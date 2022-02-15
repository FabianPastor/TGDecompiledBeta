package org.telegram.ui;

import android.text.style.URLSpan;
import android.view.View;
import org.telegram.ui.Components.TranslateAlert;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda224 implements TranslateAlert.OnLinkPress {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ View f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda224(ChatActivity chatActivity, View view) {
        this.f$0 = chatActivity;
        this.f$1 = view;
    }

    public final void run(URLSpan uRLSpan) {
        this.f$0.lambda$createMenu$148(this.f$1, uRLSpan);
    }
}
