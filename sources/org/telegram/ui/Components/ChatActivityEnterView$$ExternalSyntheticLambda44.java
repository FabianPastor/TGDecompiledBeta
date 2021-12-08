package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda44 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda44 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda44();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda44() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
