package org.telegram.p005ui.Components;

import java.io.File;
import org.telegram.p005ui.Components.ChatAttachAlert.C122510;

/* renamed from: org.telegram.ui.Components.ChatAttachAlert$10$$Lambda$3 */
final /* synthetic */ class ChatAttachAlert$10$$Lambda$3 implements Runnable {
    private final C122510 arg$1;
    private final File arg$2;
    private final boolean arg$3;

    ChatAttachAlert$10$$Lambda$3(C122510 c122510, File file, boolean z) {
        this.arg$1 = c122510;
        this.arg$2 = file;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$shutterReleased$3$ChatAttachAlert$10(this.arg$2, this.arg$3);
    }
}
