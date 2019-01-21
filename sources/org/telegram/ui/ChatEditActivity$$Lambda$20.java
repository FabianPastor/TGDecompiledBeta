package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Cells.RadioButtonCell;

final /* synthetic */ class ChatEditActivity$$Lambda$20 implements OnClickListener {
    private final ChatEditActivity arg$1;
    private final RadioButtonCell[] arg$2;
    private final Builder arg$3;

    ChatEditActivity$$Lambda$20(ChatEditActivity chatEditActivity, RadioButtonCell[] radioButtonCellArr, Builder builder) {
        this.arg$1 = chatEditActivity;
        this.arg$2 = radioButtonCellArr;
        this.arg$3 = builder;
    }

    public void onClick(View view) {
        this.arg$1.lambda$null$6$ChatEditActivity(this.arg$2, this.arg$3, view);
    }
}
