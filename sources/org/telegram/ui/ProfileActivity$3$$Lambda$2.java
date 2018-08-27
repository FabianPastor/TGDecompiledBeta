package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.ProfileActivity.C16893;

final /* synthetic */ class ProfileActivity$3$$Lambda$2 implements DialogsActivityDelegate {
    private final C16893 arg$1;
    private final User arg$2;

    ProfileActivity$3$$Lambda$2(C16893 c16893, User user) {
        this.arg$1 = c16893;
        this.arg$2 = user;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$2$ProfileActivity$3(this.arg$2, dialogsActivity, arrayList, charSequence, z);
    }
}
