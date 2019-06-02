package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$0nOnj72e_Go4w-fy56ow2OoyZ5U implements ChatRightsEditActivityDelegate {
    private final /* synthetic */ ChatUsersActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$0nOnj72e_Go4w-fy56ow2OoyZ5U(ChatUsersActivity chatUsersActivity, int i, int i2, int i3) {
        this.f$0 = chatUsersActivity;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
    }

    public final void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        this.f$0.lambda$openRightsEdit2$6$ChatUsersActivity(this.f$1, this.f$2, this.f$3, i, tL_chatAdminRights, tL_chatBannedRights);
    }
}
