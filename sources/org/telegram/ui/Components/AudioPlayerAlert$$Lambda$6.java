package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.messenger.MediaController;

final /* synthetic */ class AudioPlayerAlert$$Lambda$6 implements OnClickListener {
    static final OnClickListener $instance = new AudioPlayerAlert$$Lambda$6();

    private AudioPlayerAlert$$Lambda$6() {
    }

    public void onClick(View view) {
        MediaController.getInstance().playPreviousMessage();
    }
}
