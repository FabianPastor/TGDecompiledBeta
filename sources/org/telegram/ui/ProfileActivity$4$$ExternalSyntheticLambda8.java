package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$4$$ExternalSyntheticLambda8 implements DialogsActivity.DialogsActivityDelegate {
    public final /* synthetic */ ProfileActivity.AnonymousClass4 f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ ProfileActivity$4$$ExternalSyntheticLambda8(ProfileActivity.AnonymousClass4 r1, TLRPC.User user) {
        this.f$0 = r1;
        this.f$1 = user;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.m3724lambda$onItemClick$3$orgtelegramuiProfileActivity$4(this.f$1, dialogsActivity, arrayList, charSequence, z);
    }
}
