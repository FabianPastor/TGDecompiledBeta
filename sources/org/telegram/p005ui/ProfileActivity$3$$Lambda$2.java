package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.p005ui.ProfileActivity.C15553;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ProfileActivity$3$$Lambda$2 */
final /* synthetic */ class ProfileActivity$3$$Lambda$2 implements DialogsActivityDelegate {
    private final C15553 arg$1;
    private final User arg$2;

    ProfileActivity$3$$Lambda$2(C15553 c15553, User user) {
        this.arg$1 = c15553;
        this.arg$2 = user;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$2$ProfileActivity$3(this.arg$2, dialogsActivity, arrayList, charSequence, z);
    }
}
