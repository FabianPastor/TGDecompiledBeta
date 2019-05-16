package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$FOe0dD4xjkU1Fka8Vb1wiDf0EaA implements ChatRightsEditActivityDelegate {
    private final /* synthetic */ ProfileActivity f$0;

    public /* synthetic */ -$$Lambda$ProfileActivity$FOe0dD4xjkU1Fka8Vb1wiDf0EaA(ProfileActivity profileActivity) {
        this.f$0 = profileActivity;
    }

    public final void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.f$0.lambda$null$9$ProfileActivity(i, tL_chatAdminRights, tL_chatBannedRights);
    }
}
