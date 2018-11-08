package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;

/* renamed from: org.telegram.ui.Components.AudioPlayerAlert$$Lambda$12 */
final /* synthetic */ class AudioPlayerAlert$$Lambda$12 implements OnClickListener {
    private final boolean[] arg$1;

    AudioPlayerAlert$$Lambda$12(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        AudioPlayerAlert.lambda$onSubItemClick$11$AudioPlayerAlert(this.arg$1, view);
    }
}
