package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.Components.ChatAttachAlert.AttachBotButton;

/* renamed from: org.telegram.ui.Components.ChatAttachAlert$AttachBotButton$$Lambda$0 */
final /* synthetic */ class ChatAttachAlert$AttachBotButton$$Lambda$0 implements OnClickListener {
    private final AttachBotButton arg$1;

    ChatAttachAlert$AttachBotButton$$Lambda$0(AttachBotButton attachBotButton) {
        this.arg$1 = attachBotButton;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onLongPress$0$ChatAttachAlert$AttachBotButton(dialogInterface, i);
    }
}
