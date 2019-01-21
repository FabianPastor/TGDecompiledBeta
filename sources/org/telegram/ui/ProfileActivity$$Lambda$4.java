package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;

final /* synthetic */ class ProfileActivity$$Lambda$4 implements OnClickListener {
    private final ProfileActivity arg$1;
    private final Chat arg$2;

    ProfileActivity$$Lambda$4(ProfileActivity profileActivity, Chat chat) {
        this.arg$1 = profileActivity;
        this.arg$2 = chat;
    }

    public void onClick(View view) {
        this.arg$1.lambda$createView$10$ProfileActivity(this.arg$2, view);
    }
}
