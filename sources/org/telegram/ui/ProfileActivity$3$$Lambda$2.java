package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.ProfileActivity.AnonymousClass3;

final /* synthetic */ class ProfileActivity$3$$Lambda$2 implements DialogsActivityDelegate {
    private final AnonymousClass3 arg$1;
    private final User arg$2;

    ProfileActivity$3$$Lambda$2(AnonymousClass3 anonymousClass3, User user) {
        this.arg$1 = anonymousClass3;
        this.arg$2 = user;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$2$ProfileActivity$3(this.arg$2, dialogsActivity, arrayList, charSequence, z);
    }
}
