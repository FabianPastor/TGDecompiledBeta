package org.telegram.messenger;

import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$E4t72zCnnsM26yjhlvXuBB6_2HQ implements Runnable {
    private final /* synthetic */ MessageObject f$0;
    private final /* synthetic */ File f$1;

    public /* synthetic */ -$$Lambda$MediaController$E4t72zCnnsM26yjhlvXuBB6_2HQ(MessageObject messageObject, File file) {
        this.f$0 = messageObject;
        this.f$1 = file;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0.currentAccount).postNotificationName(NotificationCenter.fileDidLoad, FileLoader.getAttachFileName(this.f$0.getDocument()), this.f$1);
    }
}
