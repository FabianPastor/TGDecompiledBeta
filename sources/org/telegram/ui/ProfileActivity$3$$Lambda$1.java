package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ProfileActivity.C16893;

final /* synthetic */ class ProfileActivity$3$$Lambda$1 implements OnClickListener {
    private final C16893 arg$1;
    private final User arg$2;

    ProfileActivity$3$$Lambda$1(C16893 c16893, User user) {
        this.arg$1 = c16893;
        this.arg$2 = user;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$1$ProfileActivity$3(this.arg$2, dialogInterface, i);
    }
}
