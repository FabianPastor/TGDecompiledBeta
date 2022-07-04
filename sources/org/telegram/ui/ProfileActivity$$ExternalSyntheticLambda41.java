package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda41 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ TLRPC.ChannelParticipant f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.User f$3;
    public final /* synthetic */ TLRPC.ChatParticipant f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda41(ProfileActivity profileActivity, TLRPC.ChannelParticipant channelParticipant, int i, TLRPC.User user, TLRPC.ChatParticipant chatParticipant, boolean z) {
        this.f$0 = profileActivity;
        this.f$1 = channelParticipant;
        this.f$2 = i;
        this.f$3 = user;
        this.f$4 = chatParticipant;
        this.f$5 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m4413lambda$onMemberClick$17$orgtelegramuiProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
