package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnLongClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$nEg_Vj2QPwH3tdG2rclMcvQTAgo implements OnLongClickListener {
    private final /* synthetic */ ChatActivityEnterView f$0;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$nEg_Vj2QPwH3tdG2rclMcvQTAgo(ChatActivityEnterView chatActivityEnterView) {
        this.f$0 = chatActivityEnterView;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.onSendLongClick(view);
    }
}
