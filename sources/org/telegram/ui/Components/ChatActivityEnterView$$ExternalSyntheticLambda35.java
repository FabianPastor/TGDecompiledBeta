package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda35 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda35 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda35();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda35() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
