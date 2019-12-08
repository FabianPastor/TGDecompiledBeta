package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import org.telegram.ui.ActionBar.BottomSheet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VoIPActivity$qS8Hi4upiPbU6ePN9io5C_dQhsw implements OnClickListener {
    private final /* synthetic */ VoIPActivity f$0;
    private final /* synthetic */ EditText f$1;
    private final /* synthetic */ BottomSheet f$2;

    public /* synthetic */ -$$Lambda$VoIPActivity$qS8Hi4upiPbU6ePN9io5C_dQhsw(VoIPActivity voIPActivity, EditText editText, BottomSheet bottomSheet) {
        this.f$0 = voIPActivity;
        this.f$1 = editText;
        this.f$2 = bottomSheet;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showMessagesSheet$7$VoIPActivity(this.f$1, this.f$2, view);
    }
}
