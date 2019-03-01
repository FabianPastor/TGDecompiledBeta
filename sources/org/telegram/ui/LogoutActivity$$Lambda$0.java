package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended;

final /* synthetic */ class LogoutActivity$$Lambda$0 implements OnItemClickListenerExtended {
    private final LogoutActivity arg$1;

    LogoutActivity$$Lambda$0(LogoutActivity logoutActivity) {
        this.arg$1 = logoutActivity;
    }

    public void onItemClick(View view, int i, float f, float f2) {
        this.arg$1.lambda$createView$1$LogoutActivity(view, i, f, f2);
    }
}
