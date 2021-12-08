package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_chatChannelParticipant;
import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class StatisticActivity$MemberData$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ StatisticActivity.MemberData f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC$ChatFull f$2;
    public final /* synthetic */ TLRPC$TL_chatChannelParticipant f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ StatisticActivity f$5;

    public /* synthetic */ StatisticActivity$MemberData$$ExternalSyntheticLambda0(StatisticActivity.MemberData memberData, ArrayList arrayList, TLRPC$ChatFull tLRPC$ChatFull, TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant, boolean z, StatisticActivity statisticActivity) {
        this.f$0 = memberData;
        this.f$1 = arrayList;
        this.f$2 = tLRPC$ChatFull;
        this.f$3 = tLRPC$TL_chatChannelParticipant;
        this.f$4 = z;
        this.f$5 = statisticActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onLongClick$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
