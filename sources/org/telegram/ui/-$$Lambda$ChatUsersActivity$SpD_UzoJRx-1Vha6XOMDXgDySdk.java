package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$SpD_UzoJRx-1Vha6XOMDXgDySdk implements ChatRightsEditActivityDelegate {
    private final /* synthetic */ ChatUsersActivity f$0;
    private final /* synthetic */ TLObject f$1;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$SpD_UzoJRx-1Vha6XOMDXgDySdk(ChatUsersActivity chatUsersActivity, TLObject tLObject) {
        this.f$0 = chatUsersActivity;
        this.f$1 = tLObject;
    }

    public final void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.f$0.lambda$null$12$ChatUsersActivity(this.f$1, i, tL_chatAdminRights, tL_chatBannedRights);
    }
}
