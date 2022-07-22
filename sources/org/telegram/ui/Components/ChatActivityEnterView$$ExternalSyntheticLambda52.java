package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda52 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda52 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda52();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda52() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
