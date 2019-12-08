package org.telegram.ui.ActionBar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FloatingToolbar$FloatingToolbarPopup$RtHxtTZRy0svpOSVJ9SzTq9Z7Rk implements OnItemClickListener {
    private final /* synthetic */ FloatingToolbarPopup f$0;
    private final /* synthetic */ OverflowPanel f$1;

    public /* synthetic */ -$$Lambda$FloatingToolbar$FloatingToolbarPopup$RtHxtTZRy0svpOSVJ9SzTq9Z7Rk(FloatingToolbarPopup floatingToolbarPopup, OverflowPanel overflowPanel) {
        this.f$0 = floatingToolbarPopup;
        this.f$1 = overflowPanel;
    }

    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        this.f$0.lambda$createOverflowPanel$1$FloatingToolbar$FloatingToolbarPopup(this.f$1, adapterView, view, i, j);
    }
}
