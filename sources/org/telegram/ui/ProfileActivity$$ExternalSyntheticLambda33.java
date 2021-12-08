package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda33 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC.ChatParticipant f$2;
    public final /* synthetic */ TLRPC.ChannelParticipant f$3;
    public final /* synthetic */ TLRPC.User f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda33(ProfileActivity profileActivity, ArrayList arrayList, TLRPC.ChatParticipant chatParticipant, TLRPC.ChannelParticipant channelParticipant, TLRPC.User user, boolean z) {
        this.f$0 = profileActivity;
        this.f$1 = arrayList;
        this.f$2 = chatParticipant;
        this.f$3 = channelParticipant;
        this.f$4 = user;
        this.f$5 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3709lambda$onMemberClick$17$orgtelegramuiProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
