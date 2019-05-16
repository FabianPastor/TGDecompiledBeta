package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$UNhwNBqeTy1Z6WZHWcGGmYV-bRw implements Runnable {
    private final /* synthetic */ MessageObject f$0;

    public /* synthetic */ -$$Lambda$MediaController$UNhwNBqeTy1Z6WZHWcGGmYV-bRw(MessageObject messageObject) {
        this.f$0 = messageObject;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0.currentAccount).postNotificationName(NotificationCenter.fileDidLoad, FileLoader.getAttachFileName(this.f$0.getDocument()));
    }
}
