package org.telegram.ui;

import android.view.View;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda15 implements View.OnClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ ChatActivity.PulledDialog f$1;
    public final /* synthetic */ TLRPC$Chat f$2;
    public final /* synthetic */ TLRPC$User f$3;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda15(ProfileActivity profileActivity, ChatActivity.PulledDialog pulledDialog, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User) {
        this.f$0 = profileActivity;
        this.f$1 = pulledDialog;
        this.f$2 = tLRPC$Chat;
        this.f$3 = tLRPC$User;
    }

    public final void onClick(View view) {
        this.f$0.lambda$createActionBar$1(this.f$1, this.f$2, this.f$3, view);
    }
}
