package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AudioPlayerAlert$pQ-5LDOKmp9BWnrwt0sO-Br-q0g implements SeekBarViewDelegate {
    public static final /* synthetic */ -$$Lambda$AudioPlayerAlert$pQ-5LDOKmp9BWnrwt0sO-Br-q0g INSTANCE = new -$$Lambda$AudioPlayerAlert$pQ-5LDOKmp9BWnrwt0sO-Br-q0g();

    private /* synthetic */ -$$Lambda$AudioPlayerAlert$pQ-5LDOKmp9BWnrwt0sO-Br-q0g() {
    }

    public final void onSeekBarDrag(float f) {
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), f);
    }
}
