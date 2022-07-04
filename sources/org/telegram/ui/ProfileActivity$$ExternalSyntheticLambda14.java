package org.telegram.ui;

import android.view.View;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.SimpleTextView;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda14 implements View.OnClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ TLRPC$User f$1;
    public final /* synthetic */ SimpleTextView f$2;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda14(ProfileActivity profileActivity, TLRPC$User tLRPC$User, SimpleTextView simpleTextView) {
        this.f$0 = profileActivity;
        this.f$1 = tLRPC$User;
        this.f$2 = simpleTextView;
    }

    public final void onClick(View view) {
        this.f$0.lambda$updateProfileData$32(this.f$1, this.f$2, view);
    }
}
