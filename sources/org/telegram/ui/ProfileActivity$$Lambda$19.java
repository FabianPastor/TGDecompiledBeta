package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;

final /* synthetic */ class ProfileActivity$$Lambda$19 implements ChatRightsEditActivityDelegate {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$19(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.arg$1.lambda$null$9$ProfileActivity(i, tL_chatAdminRights, tL_chatBannedRights);
    }
}
