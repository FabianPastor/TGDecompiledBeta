package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;

public final /* synthetic */ class ChatLinkActivity$$ExternalSyntheticLambda4 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatLinkActivity f$0;
    public final /* synthetic */ TLRPC$ChatFull f$1;
    public final /* synthetic */ TLRPC$Chat f$2;

    public /* synthetic */ ChatLinkActivity$$ExternalSyntheticLambda4(ChatLinkActivity chatLinkActivity, TLRPC$ChatFull tLRPC$ChatFull, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = chatLinkActivity;
        this.f$1 = tLRPC$ChatFull;
        this.f$2 = tLRPC$Chat;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showLinkAlert$9(this.f$1, this.f$2, dialogInterface, i);
    }
}
