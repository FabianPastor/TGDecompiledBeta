package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.Components.EditTextBoldCursor;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VoIPActivity$oYWreFShsVaxuR-CC8wb_y63Qek implements OnClickListener {
    private final /* synthetic */ VoIPActivity f$0;
    private final /* synthetic */ FrameLayout f$1;
    private final /* synthetic */ BottomSheetCell f$2;
    private final /* synthetic */ EditTextBoldCursor f$3;

    public /* synthetic */ -$$Lambda$VoIPActivity$oYWreFShsVaxuR-CC8wb_y63Qek(VoIPActivity voIPActivity, FrameLayout frameLayout, BottomSheetCell bottomSheetCell, EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = voIPActivity;
        this.f$1 = frameLayout;
        this.f$2 = bottomSheetCell;
        this.f$3 = editTextBoldCursor;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showMessagesSheet$9$VoIPActivity(this.f$1, this.f$2, this.f$3, view);
    }
}
