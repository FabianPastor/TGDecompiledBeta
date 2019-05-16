package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatEditTypeActivity$oi4gxdiAY04rSah4zOjzmCLASSNAMEoJg implements OnClickListener {
    private final /* synthetic */ ChatEditTypeActivity f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$ChatEditTypeActivity$oi4gxdiAY04rSah4zOjzmCLASSNAMEoJg(ChatEditTypeActivity chatEditTypeActivity, Chat chat) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = chat;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$13$ChatEditTypeActivity(this.f$1, dialogInterface, i);
    }
}
