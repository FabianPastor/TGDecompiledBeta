package org.telegram.ui;

import android.view.View;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda13 implements View.OnClickListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ TLRPC.TL_groupCallParticipant f$3;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda13(GroupCallActivity groupCallActivity, int i, ArrayList arrayList, TLRPC.TL_groupCallParticipant tL_groupCallParticipant) {
        this.f$0 = groupCallActivity;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = tL_groupCallParticipant;
    }

    public final void onClick(View view) {
        this.f$0.m3543lambda$showMenuForCell$60$orgtelegramuiGroupCallActivity(this.f$1, this.f$2, this.f$3, view);
    }
}
