package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.ui.Components.ChatAttachAlert;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ ChatAttachAlert.AttachBotButton f$1;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda0(ChatAttachAlert chatAttachAlert, ChatAttachAlert.AttachBotButton attachBotButton) {
        this.f$0 = chatAttachAlert;
        this.f$1 = attachBotButton;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$new$7(this.f$1, dialogInterface, i);
    }
}
