package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Cells.RadioButtonCell;

public final /* synthetic */ class ChatEditActivity$$ExternalSyntheticLambda18 implements View.OnClickListener {
    public final /* synthetic */ ChatEditActivity f$0;
    public final /* synthetic */ RadioButtonCell[] f$1;
    public final /* synthetic */ BottomSheet.Builder f$2;

    public /* synthetic */ ChatEditActivity$$ExternalSyntheticLambda18(ChatEditActivity chatEditActivity, RadioButtonCell[] radioButtonCellArr, BottomSheet.Builder builder) {
        this.f$0 = chatEditActivity;
        this.f$1 = radioButtonCellArr;
        this.f$2 = builder;
    }

    public final void onClick(View view) {
        this.f$0.lambda$createView$12(this.f$1, this.f$2, view);
    }
}
