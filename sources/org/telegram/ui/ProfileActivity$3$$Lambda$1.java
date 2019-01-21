package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ProfileActivity.AnonymousClass3;

final /* synthetic */ class ProfileActivity$3$$Lambda$1 implements OnClickListener {
    private final AnonymousClass3 arg$1;
    private final User arg$2;

    ProfileActivity$3$$Lambda$1(AnonymousClass3 anonymousClass3, User user) {
        this.arg$1 = anonymousClass3;
        this.arg$2 = user;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$1$ProfileActivity$3(this.arg$2, dialogInterface, i);
    }
}
