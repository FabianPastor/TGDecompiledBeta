package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ProfileActivity$$Lambda$21 implements OnClickListener {
    private final ProfileActivity arg$1;
    private final ArrayList arg$2;
    private final ChannelParticipant arg$3;
    private final ChatParticipant arg$4;
    private final User arg$5;

    ProfileActivity$$Lambda$21(ProfileActivity profileActivity, ArrayList arrayList, ChannelParticipant channelParticipant, ChatParticipant chatParticipant, User user) {
        this.arg$1 = profileActivity;
        this.arg$2 = arrayList;
        this.arg$3 = channelParticipant;
        this.arg$4 = chatParticipant;
        this.arg$5 = user;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$5$ProfileActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, dialogInterface, i);
    }
}
