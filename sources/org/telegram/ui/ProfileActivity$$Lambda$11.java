package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.BooleanCallback;

final /* synthetic */ class ProfileActivity$$Lambda$11 implements BooleanCallback {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$11(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void run(boolean z) {
        this.arg$1.lambda$leaveChatPressed$17$ProfileActivity(z);
    }
}
