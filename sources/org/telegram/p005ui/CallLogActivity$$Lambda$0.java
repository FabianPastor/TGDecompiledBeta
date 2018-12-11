package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.CallLogActivity$$Lambda$0 */
final /* synthetic */ class CallLogActivity$$Lambda$0 implements OnItemClickListener {
    private final CallLogActivity arg$1;

    CallLogActivity$$Lambda$0(CallLogActivity callLogActivity) {
        this.arg$1 = callLogActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$CallLogActivity(view, i);
    }
}
