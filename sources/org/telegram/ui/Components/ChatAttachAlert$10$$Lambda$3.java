package org.telegram.ui.Components;

import java.io.File;
import org.telegram.ui.Components.ChatAttachAlert.AnonymousClass10;

final /* synthetic */ class ChatAttachAlert$10$$Lambda$3 implements Runnable {
    private final AnonymousClass10 arg$1;
    private final File arg$2;
    private final boolean arg$3;

    ChatAttachAlert$10$$Lambda$3(AnonymousClass10 anonymousClass10, File file, boolean z) {
        this.arg$1 = anonymousClass10;
        this.arg$2 = file;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$shutterReleased$3$ChatAttachAlert$10(this.arg$2, this.arg$3);
    }
}
