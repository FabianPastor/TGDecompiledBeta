package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class ChatSettingsActivity$$Lambda$4 implements OnClickListener {
    private final ChatSettingsActivity arg$1;
    private final boolean[] arg$2;
    private final int arg$3;

    ChatSettingsActivity$$Lambda$4(ChatSettingsActivity chatSettingsActivity, boolean[] zArr, int i) {
        this.arg$1 = chatSettingsActivity;
        this.arg$2 = zArr;
        this.arg$3 = i;
    }

    public void onClick(View view) {
        this.arg$1.lambda$null$3$ChatSettingsActivity(this.arg$2, this.arg$3, view);
    }
}
