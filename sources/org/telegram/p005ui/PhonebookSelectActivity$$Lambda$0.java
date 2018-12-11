package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.PhonebookSelectActivity$$Lambda$0 */
final /* synthetic */ class PhonebookSelectActivity$$Lambda$0 implements OnItemClickListener {
    private final PhonebookSelectActivity arg$1;

    PhonebookSelectActivity$$Lambda$0(PhonebookSelectActivity phonebookSelectActivity) {
        this.arg$1 = phonebookSelectActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$PhonebookSelectActivity(view, i);
    }
}
