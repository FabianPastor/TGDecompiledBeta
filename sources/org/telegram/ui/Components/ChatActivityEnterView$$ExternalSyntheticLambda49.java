package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda49 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda49 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda49();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda49() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
