package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda47 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ ChatMessageCell f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda47(ChatMessageCell chatMessageCell) {
        this.f$0 = chatMessageCell;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        ChatActivity.lambda$didPressMessageUrl$246(this.f$0, dialogInterface);
    }
}
