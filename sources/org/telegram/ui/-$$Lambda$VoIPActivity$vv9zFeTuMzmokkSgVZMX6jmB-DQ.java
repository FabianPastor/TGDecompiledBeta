package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BottomSheet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VoIPActivity$vv9zFeTuMzmokkSgVZMX6jmB-DQ implements OnClickListener {
    private final /* synthetic */ VoIPActivity f$0;
    private final /* synthetic */ BottomSheet f$1;

    public /* synthetic */ -$$Lambda$VoIPActivity$vv9zFeTuMzmokkSgVZMX6jmB-DQ(VoIPActivity voIPActivity, BottomSheet bottomSheet) {
        this.f$0 = voIPActivity;
        this.f$1 = bottomSheet;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showMessagesSheet$6$VoIPActivity(this.f$1, view);
    }
}
