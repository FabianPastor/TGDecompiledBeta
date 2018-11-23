package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.PhonebookShareActivity$$Lambda$1 */
final /* synthetic */ class PhonebookShareActivity$$Lambda$1 implements OnItemLongClickListener {
    private final PhonebookShareActivity arg$1;

    PhonebookShareActivity$$Lambda$1(PhonebookShareActivity phonebookShareActivity) {
        this.arg$1 = phonebookShareActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$3$PhonebookShareActivity(view, i);
    }
}
