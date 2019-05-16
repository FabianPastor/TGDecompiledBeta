package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$iWlc9ep_wbwBIz7hlp_XPY9A0O0 implements OnClickListener {
    private final /* synthetic */ ProfileActivity f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ ChannelParticipant f$2;
    private final /* synthetic */ ChatParticipant f$3;
    private final /* synthetic */ User f$4;

    public /* synthetic */ -$$Lambda$ProfileActivity$iWlc9ep_wbwBIz7hlp_XPY9A0O0(ProfileActivity profileActivity, ArrayList arrayList, ChannelParticipant channelParticipant, ChatParticipant chatParticipant, User user) {
        this.f$0 = profileActivity;
        this.f$1 = arrayList;
        this.f$2 = channelParticipant;
        this.f$3 = chatParticipant;
        this.f$4 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$5$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
