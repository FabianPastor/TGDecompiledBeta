package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.EditTextBoldCursor;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VoIPActivity$qot2mYrZZZaecrEqkIoWDTO7CBw implements OnClickListener {
    private final /* synthetic */ VoIPActivity f$0;
    private final /* synthetic */ EditTextBoldCursor f$1;
    private final /* synthetic */ BottomSheet f$2;

    public /* synthetic */ -$$Lambda$VoIPActivity$qot2mYrZZZaecrEqkIoWDTO7CBw(VoIPActivity voIPActivity, EditTextBoldCursor editTextBoldCursor, BottomSheet bottomSheet) {
        this.f$0 = voIPActivity;
        this.f$1 = editTextBoldCursor;
        this.f$2 = bottomSheet;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showMessagesSheet$7$VoIPActivity(this.f$1, this.f$2, view);
    }
}
