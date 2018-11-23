package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.ActionBar.BaseFragment;

/* renamed from: org.telegram.ui.Components.ChatAttachAlert$$Lambda$1 */
final /* synthetic */ class ChatAttachAlert$$Lambda$1 implements OnClickListener {
    private final ChatAttachAlert arg$1;
    private final BaseFragment arg$2;

    ChatAttachAlert$$Lambda$1(ChatAttachAlert chatAttachAlert, BaseFragment baseFragment) {
        this.arg$1 = chatAttachAlert;
        this.arg$2 = baseFragment;
    }

    public void onClick(View view) {
        this.arg$1.lambda$new$1$ChatAttachAlert(this.arg$2, view);
    }
}
