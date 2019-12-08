package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VoIPActivity$EkpRskxpSAZKneCZj3Wgdr5iMNs implements OnClickListener {
    private final /* synthetic */ VoIPActivity f$0;
    private final /* synthetic */ FrameLayout f$1;
    private final /* synthetic */ BottomSheetCell f$2;
    private final /* synthetic */ EditText f$3;

    public /* synthetic */ -$$Lambda$VoIPActivity$EkpRskxpSAZKneCZj3Wgdr5iMNs(VoIPActivity voIPActivity, FrameLayout frameLayout, BottomSheetCell bottomSheetCell, EditText editText) {
        this.f$0 = voIPActivity;
        this.f$1 = frameLayout;
        this.f$2 = bottomSheetCell;
        this.f$3 = editText;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showMessagesSheet$8$VoIPActivity(this.f$1, this.f$2, this.f$3, view);
    }
}
