package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class AudioSelectActivity$$Lambda$0 implements OnItemClickListener {
    private final AudioSelectActivity arg$1;

    AudioSelectActivity$$Lambda$0(AudioSelectActivity audioSelectActivity) {
        this.arg$1 = audioSelectActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$AudioSelectActivity(view, i);
    }
}
