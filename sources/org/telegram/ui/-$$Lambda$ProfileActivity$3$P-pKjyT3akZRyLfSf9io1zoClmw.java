package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.ProfileActivity.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$3$P-pKjyT3akZRyLfSf9io1zoClmw implements DialogsActivityDelegate {
    private final /* synthetic */ AnonymousClass3 f$0;
    private final /* synthetic */ User f$1;

    public /* synthetic */ -$$Lambda$ProfileActivity$3$P-pKjyT3akZRyLfSf9io1zoClmw(AnonymousClass3 anonymousClass3, User user) {
        this.f$0 = anonymousClass3;
        this.f$1 = user;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.lambda$onItemClick$2$ProfileActivity$3(this.f$1, dialogsActivity, arrayList, charSequence, z);
    }
}
