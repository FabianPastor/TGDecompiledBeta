package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda197 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ ChatMessageCell f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda197(ChatMessageCell chatMessageCell) {
        this.f$0 = chatMessageCell;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        ChatActivity.lambda$didPressMessageUrl$238(this.f$0, dialogInterface);
    }
}
