package org.telegram.ui;

import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;

final /* synthetic */ class ProfileActivity$$Lambda$7 implements ChatRightsEditActivityDelegate {
    private final ProfileActivity arg$1;
    private final int arg$2;
    private final ChatParticipant arg$3;

    ProfileActivity$$Lambda$7(ProfileActivity profileActivity, int i, ChatParticipant chatParticipant) {
        this.arg$1 = profileActivity;
        this.arg$2 = i;
        this.arg$3 = chatParticipant;
    }

    public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.arg$1.lambda$openRightsEdit$13$ProfileActivity(this.arg$2, this.arg$3, i, tL_chatAdminRights, tL_chatBannedRights);
    }
}
