package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.AudioPlayerAlert$$Lambda$8 */
final /* synthetic */ class AudioPlayerAlert$$Lambda$8 implements OnClickListener {
    static final OnClickListener $instance = new AudioPlayerAlert$$Lambda$8();

    private AudioPlayerAlert$$Lambda$8() {
    }

    public void onClick(View view) {
        MediaController.getInstance().playNextMessage();
    }
}
