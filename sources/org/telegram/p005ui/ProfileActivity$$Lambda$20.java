package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipant;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$20 */
final /* synthetic */ class ProfileActivity$$Lambda$20 implements OnClickListener {
    private final ProfileActivity arg$1;
    private final ArrayList arg$2;
    private final ChatParticipant arg$3;
    private final ChannelParticipant arg$4;

    ProfileActivity$$Lambda$20(ProfileActivity profileActivity, ArrayList arrayList, ChatParticipant chatParticipant, ChannelParticipant channelParticipant) {
        this.arg$1 = profileActivity;
        this.arg$2 = arrayList;
        this.arg$3 = chatParticipant;
        this.arg$4 = channelParticipant;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$6$ProfileActivity(this.arg$2, this.arg$3, this.arg$4, dialogInterface, i);
    }
}
