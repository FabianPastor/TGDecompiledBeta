package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Cells.DialogCell;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda29 implements View.OnClickListener {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ DialogCell f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda29(DialogsActivity dialogsActivity, DialogCell dialogCell, long j) {
        this.f$0 = dialogsActivity;
        this.f$1 = dialogCell;
        this.f$2 = j;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showChatPreview$28(this.f$1, this.f$2, view);
    }
}
