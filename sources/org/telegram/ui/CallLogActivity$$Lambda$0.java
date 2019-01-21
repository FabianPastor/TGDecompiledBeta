package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class CallLogActivity$$Lambda$0 implements OnItemClickListener {
    private final CallLogActivity arg$1;

    CallLogActivity$$Lambda$0(CallLogActivity callLogActivity) {
        this.arg$1 = callLogActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$CallLogActivity(view, i);
    }
}
