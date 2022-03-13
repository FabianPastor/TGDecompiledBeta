package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Cells.CheckBoxCell;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda59 implements View.OnClickListener {
    public final /* synthetic */ CheckBoxCell[] f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda59(CheckBoxCell[] checkBoxCellArr) {
        this.f$0 = checkBoxCellArr;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$showBlockReportSpamReplyAlert$7(this.f$0, view);
    }
}
