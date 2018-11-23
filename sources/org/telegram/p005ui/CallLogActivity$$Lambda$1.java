package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.CallLogActivity$$Lambda$1 */
final /* synthetic */ class CallLogActivity$$Lambda$1 implements OnItemLongClickListener {
    private final CallLogActivity arg$1;

    CallLogActivity$$Lambda$1(CallLogActivity callLogActivity) {
        this.arg$1 = callLogActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$CallLogActivity(view, i);
    }
}
