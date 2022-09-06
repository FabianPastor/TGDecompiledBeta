package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public final /* synthetic */ class SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda11 implements AlertsCreator.StatusUntilDatePickerDelegate {
    public final /* synthetic */ SelectAnimatedEmojiDialog.SelectStatusDurationDialog f$0;
    public final /* synthetic */ boolean[] f$1;

    public /* synthetic */ SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda11(SelectAnimatedEmojiDialog.SelectStatusDurationDialog selectStatusDurationDialog, boolean[] zArr) {
        this.f$0 = selectStatusDurationDialog;
        this.f$1 = zArr;
    }

    public final void didSelectDate(int i) {
        this.f$0.lambda$new$4(this.f$1, i);
    }
}
