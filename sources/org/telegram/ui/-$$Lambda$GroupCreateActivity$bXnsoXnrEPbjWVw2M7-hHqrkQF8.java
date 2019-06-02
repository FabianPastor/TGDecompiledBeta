package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GroupCreateActivity$bXnsoXnrEPbjWVw2M7-hHqrkQF8 implements OnClickListener {
    private final /* synthetic */ GroupCreateActivity f$0;
    private final /* synthetic */ User f$1;

    public /* synthetic */ -$$Lambda$GroupCreateActivity$bXnsoXnrEPbjWVw2M7-hHqrkQF8(GroupCreateActivity groupCreateActivity, User user) {
        this.f$0 = groupCreateActivity;
        this.f$1 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$2$GroupCreateActivity(this.f$1, dialogInterface, i);
    }
}
