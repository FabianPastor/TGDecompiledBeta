package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.ActionBar.BottomSheet.Builder;
import org.telegram.p005ui.Cells.DialogRadioCell;

/* renamed from: org.telegram.ui.ChannelEditActivity$$Lambda$21 */
final /* synthetic */ class ChannelEditActivity$$Lambda$21 implements OnClickListener {
    private final ChannelEditActivity arg$1;
    private final DialogRadioCell[] arg$2;
    private final Builder arg$3;

    ChannelEditActivity$$Lambda$21(ChannelEditActivity channelEditActivity, DialogRadioCell[] dialogRadioCellArr, Builder builder) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = dialogRadioCellArr;
        this.arg$3 = builder;
    }

    public void onClick(View view) {
        this.arg$1.lambda$null$6$ChannelEditActivity(this.arg$2, this.arg$3, view);
    }
}
