package org.telegram.p005ui.Components;

import android.widget.FrameLayout.LayoutParams;

/* renamed from: org.telegram.ui.Components.ChatAttachAlert$$Lambda$6 */
final /* synthetic */ class ChatAttachAlert$$Lambda$6 implements Runnable {
    private final ChatAttachAlert arg$1;
    private final LayoutParams arg$2;

    ChatAttachAlert$$Lambda$6(ChatAttachAlert chatAttachAlert, LayoutParams layoutParams) {
        this.arg$1 = chatAttachAlert;
        this.arg$2 = layoutParams;
    }

    public void run() {
        this.arg$1.lambda$applyCameraViewPosition$6$ChatAttachAlert(this.arg$2);
    }
}
