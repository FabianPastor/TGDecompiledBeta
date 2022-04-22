package org.telegram.ui.Components.voip;

import android.view.View;
import org.telegram.ui.Cells.CheckBoxCell;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda13 implements View.OnClickListener {
    public final /* synthetic */ boolean[] f$0;
    public final /* synthetic */ CheckBoxCell f$1;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda13(boolean[] zArr, CheckBoxCell checkBoxCell) {
        this.f$0 = zArr;
        this.f$1 = checkBoxCell;
    }

    public final void onClick(View view) {
        VoIPHelper.lambda$showRateAlert$10(this.f$0, this.f$1, view);
    }
}
