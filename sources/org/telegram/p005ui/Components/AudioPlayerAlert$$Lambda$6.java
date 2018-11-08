package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.AudioPlayerAlert$$Lambda$6 */
final /* synthetic */ class AudioPlayerAlert$$Lambda$6 implements OnClickListener {
    static final OnClickListener $instance = new AudioPlayerAlert$$Lambda$6();

    private AudioPlayerAlert$$Lambda$6() {
    }

    public void onClick(View view) {
        MediaController.getInstance().playPreviousMessage();
    }
}
