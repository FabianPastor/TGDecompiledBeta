package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.ProfileActivity.C22663;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ProfileActivity$3$$Lambda$1 */
final /* synthetic */ class ProfileActivity$3$$Lambda$1 implements OnClickListener {
    private final C22663 arg$1;
    private final User arg$2;

    ProfileActivity$3$$Lambda$1(C22663 c22663, User user) {
        this.arg$1 = c22663;
        this.arg$2 = user;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$1$ProfileActivity$3(this.arg$2, dialogInterface, i);
    }
}
