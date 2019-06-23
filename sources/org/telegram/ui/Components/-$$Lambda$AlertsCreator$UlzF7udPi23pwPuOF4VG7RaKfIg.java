package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Cells.CheckBoxCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$UlzF7udPi23pwPuOF4VG7RaKfIg implements OnClickListener {
    private final /* synthetic */ CheckBoxCell[] f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$UlzF7udPi23pwPuOF4VG7RaKfIg(CheckBoxCell[] checkBoxCellArr) {
        this.f$0 = checkBoxCellArr;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$showBlockReportSpamAlert$3(this.f$0, view);
    }
}
