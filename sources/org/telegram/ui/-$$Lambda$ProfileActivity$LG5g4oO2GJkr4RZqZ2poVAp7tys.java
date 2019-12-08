package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$LG5g4oO2GJkr4RZqZ2poVAp7tys implements OnClickListener {
    private final /* synthetic */ ProfileActivity f$0;
    private final /* synthetic */ ChannelParticipant f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ User f$3;
    private final /* synthetic */ ChatParticipant f$4;

    public /* synthetic */ -$$Lambda$ProfileActivity$LG5g4oO2GJkr4RZqZ2poVAp7tys(ProfileActivity profileActivity, ChannelParticipant channelParticipant, int i, User user, ChatParticipant chatParticipant) {
        this.f$0 = profileActivity;
        this.f$1 = channelParticipant;
        this.f$2 = i;
        this.f$3 = user;
        this.f$4 = chatParticipant;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$4$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
