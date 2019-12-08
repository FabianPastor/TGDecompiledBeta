package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$_c9ye99difEKRRO05psDolLw5uU implements Runnable {
    public static final /* synthetic */ -$$Lambda$ChatActivityEnterView$_c9ye99difEKRRO05psDolLw5uU INSTANCE = new -$$Lambda$ChatActivityEnterView$_c9ye99difEKRRO05psDolLw5uU();

    private /* synthetic */ -$$Lambda$ChatActivityEnterView$_c9ye99difEKRRO05psDolLw5uU() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
