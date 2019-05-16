package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$M0pE8LsEZwD1lKkfGrG8ZoBFh8o implements OnClickListener {
    private final /* synthetic */ ProfileActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ User f$2;
    private final /* synthetic */ ChatParticipant f$3;
    private final /* synthetic */ ChannelParticipant f$4;

    public /* synthetic */ -$$Lambda$ProfileActivity$M0pE8LsEZwD1lKkfGrG8ZoBFh8o(ProfileActivity profileActivity, int i, User user, ChatParticipant chatParticipant, ChannelParticipant channelParticipant) {
        this.f$0 = profileActivity;
        this.f$1 = i;
        this.f$2 = user;
        this.f$3 = chatParticipant;
        this.f$4 = channelParticipant;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$4$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
