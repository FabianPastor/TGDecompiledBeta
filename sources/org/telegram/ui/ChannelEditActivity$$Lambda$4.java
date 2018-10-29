package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;

final /* synthetic */ class ChannelEditActivity$$Lambda$4 implements OnClickListener {
    private final ChannelEditActivity arg$1;
    private final ArrayList arg$2;
    private final int arg$3;
    private final ChannelParticipant arg$4;
    private final TL_chatChannelParticipant arg$5;

    ChannelEditActivity$$Lambda$4(ChannelEditActivity channelEditActivity, ArrayList arrayList, int i, ChannelParticipant channelParticipant, TL_chatChannelParticipant tL_chatChannelParticipant) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = arrayList;
        this.arg$3 = i;
        this.arg$4 = channelParticipant;
        this.arg$5 = tL_chatChannelParticipant;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createMenuForParticipant$6$ChannelEditActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, dialogInterface, i);
    }
}
