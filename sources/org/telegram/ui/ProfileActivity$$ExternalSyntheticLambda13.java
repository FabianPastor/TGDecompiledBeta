package org.telegram.ui;

import android.view.View;
import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda13 implements View.OnClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ TLRPC$Chat f$1;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda13(ProfileActivity profileActivity, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = profileActivity;
        this.f$1 = tLRPC$Chat;
    }

    public final void onClick(View view) {
        this.f$0.lambda$createView$9(this.f$1, view);
    }
}
