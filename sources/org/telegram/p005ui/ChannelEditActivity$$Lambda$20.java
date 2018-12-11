package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.ActionBar.BottomSheet.Builder;
import org.telegram.p005ui.Cells.RadioButtonCell;

/* renamed from: org.telegram.ui.ChannelEditActivity$$Lambda$20 */
final /* synthetic */ class ChannelEditActivity$$Lambda$20 implements OnClickListener {
    private final ChannelEditActivity arg$1;
    private final RadioButtonCell[] arg$2;
    private final Builder arg$3;

    ChannelEditActivity$$Lambda$20(ChannelEditActivity channelEditActivity, RadioButtonCell[] radioButtonCellArr, Builder builder) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = radioButtonCellArr;
        this.arg$3 = builder;
    }

    public void onClick(View view) {
        this.arg$1.lambda$null$8$ChannelEditActivity(this.arg$2, this.arg$3, view);
    }
}
