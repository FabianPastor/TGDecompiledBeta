package org.telegram.ui;

import android.text.style.URLSpan;
import android.view.View;
import org.telegram.ui.Components.TranslateAlert;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda254 implements TranslateAlert.OnLinkPress {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ View f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda254(ChatActivity chatActivity, View view) {
        this.f$0 = chatActivity;
        this.f$1 = view;
    }

    public final boolean run(URLSpan uRLSpan) {
        return this.f$0.lambda$createMenu$174(this.f$1, uRLSpan);
    }
}
