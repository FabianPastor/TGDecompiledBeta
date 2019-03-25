package org.telegram.ui;

import java.io.File;
import org.telegram.ui.SecretMediaViewer.AnonymousClass1;

final /* synthetic */ class SecretMediaViewer$1$$Lambda$0 implements Runnable {
    private final AnonymousClass1 arg$1;
    private final File arg$2;

    SecretMediaViewer$1$$Lambda$0(AnonymousClass1 anonymousClass1, File file) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = file;
    }

    public void run() {
        this.arg$1.lambda$onError$0$SecretMediaViewer$1(this.arg$2);
    }
}
