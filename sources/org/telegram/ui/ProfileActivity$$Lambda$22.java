package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ProfileActivity$$Lambda$22 implements OnClickListener {
    private final ProfileActivity arg$1;
    private final int arg$2;
    private final User arg$3;
    private final ChatParticipant arg$4;
    private final ChannelParticipant arg$5;

    ProfileActivity$$Lambda$22(ProfileActivity profileActivity, int i, User user, ChatParticipant chatParticipant, ChannelParticipant channelParticipant) {
        this.arg$1 = profileActivity;
        this.arg$2 = i;
        this.arg$3 = user;
        this.arg$4 = chatParticipant;
        this.arg$5 = channelParticipant;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$4$ProfileActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, dialogInterface, i);
    }
}
