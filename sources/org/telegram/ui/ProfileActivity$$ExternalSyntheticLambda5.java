package org.telegram.ui;

import android.view.View;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda5 implements View.OnClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ TLRPC.Chat f$1;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda5(ProfileActivity profileActivity, TLRPC.Chat chat) {
        this.f$0 = profileActivity;
        this.f$1 = chat;
    }

    public final void onClick(View view) {
        this.f$0.m3037lambda$createView$10$orgtelegramuiProfileActivity(this.f$1, view);
    }
}
