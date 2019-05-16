package org.telegram.ui;

import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$QONFZUnsCsoEQ085dztQiYr-zvw implements ChatRightsEditActivityDelegate {
    private final /* synthetic */ ProfileActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ChatParticipant f$2;

    public /* synthetic */ -$$Lambda$ProfileActivity$QONFZUnsCsoEQ085dztQiYr-zvw(ProfileActivity profileActivity, int i, ChatParticipant chatParticipant) {
        this.f$0 = profileActivity;
        this.f$1 = i;
        this.f$2 = chatParticipant;
    }

    public final void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.f$0.lambda$openRightsEdit$13$ProfileActivity(this.f$1, this.f$2, i, tL_chatAdminRights, tL_chatBannedRights);
    }
}
