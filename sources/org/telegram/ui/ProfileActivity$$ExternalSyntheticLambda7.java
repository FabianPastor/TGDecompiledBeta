package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda7 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC$ChatParticipant f$2;
    public final /* synthetic */ TLRPC$ChannelParticipant f$3;
    public final /* synthetic */ TLRPC$User f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda7(ProfileActivity profileActivity, ArrayList arrayList, TLRPC$ChatParticipant tLRPC$ChatParticipant, TLRPC$ChannelParticipant tLRPC$ChannelParticipant, TLRPC$User tLRPC$User, boolean z) {
        this.f$0 = profileActivity;
        this.f$1 = arrayList;
        this.f$2 = tLRPC$ChatParticipant;
        this.f$3 = tLRPC$ChannelParticipant;
        this.f$4 = tLRPC$User;
        this.f$5 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onMemberClick$18(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
