package org.telegram.messenger;

import java.io.File;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda34 implements Runnable {
    public final /* synthetic */ MessageObject f$0;
    public final /* synthetic */ File f$1;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda34(MessageObject messageObject, File file) {
        this.f$0 = messageObject;
        this.f$1 = file;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0.currentAccount).postNotificationName(NotificationCenter.fileLoaded, FileLoader.getAttachFileName(this.f$0.getDocument()), this.f$1);
    }
}