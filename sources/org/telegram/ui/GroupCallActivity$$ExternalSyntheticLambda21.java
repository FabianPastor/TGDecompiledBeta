package org.telegram.ui;

import android.view.View;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda21 implements View.OnClickListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ TLRPC$TL_groupCallParticipant f$3;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda21(GroupCallActivity groupCallActivity, int i, ArrayList arrayList, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant) {
        this.f$0 = groupCallActivity;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = tLRPC$TL_groupCallParticipant;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showMenuForCell$60(this.f$1, this.f$2, this.f$3, view);
    }
}
