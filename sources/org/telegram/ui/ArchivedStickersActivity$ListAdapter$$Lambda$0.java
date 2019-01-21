package org.telegram.ui;

import org.telegram.ui.Components.Switch;
import org.telegram.ui.Components.Switch.OnCheckedChangeListener;

final /* synthetic */ class ArchivedStickersActivity$ListAdapter$$Lambda$0 implements OnCheckedChangeListener {
    private final ListAdapter arg$1;

    ArchivedStickersActivity$ListAdapter$$Lambda$0(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public void onCheckedChanged(Switch switchR, boolean z) {
        this.arg$1.lambda$onCreateViewHolder$0$ArchivedStickersActivity$ListAdapter(switchR, z);
    }
}
