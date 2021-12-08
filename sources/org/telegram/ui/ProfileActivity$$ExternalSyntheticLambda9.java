package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda9 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ TLRPC$ChannelParticipant f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC$User f$3;
    public final /* synthetic */ TLRPC$ChatParticipant f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda9(ProfileActivity profileActivity, TLRPC$ChannelParticipant tLRPC$ChannelParticipant, int i, TLRPC$User tLRPC$User, TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z) {
        this.f$0 = profileActivity;
        this.f$1 = tLRPC$ChannelParticipant;
        this.f$2 = i;
        this.f$3 = tLRPC$User;
        this.f$4 = tLRPC$ChatParticipant;
        this.f$5 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onMemberClick$16(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
