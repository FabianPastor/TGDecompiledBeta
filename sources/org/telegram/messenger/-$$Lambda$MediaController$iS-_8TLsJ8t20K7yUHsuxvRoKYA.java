package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$iS-_8TLsJ8t20K7yUHsuxvRoKYA implements Runnable {
    private final /* synthetic */ MediaController f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ MessageObject f$3;

    public /* synthetic */ -$$Lambda$MediaController$iS-_8TLsJ8t20K7yUHsuxvRoKYA(MediaController mediaController, String str, String str2, MessageObject messageObject) {
        this.f$0 = mediaController;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$generateWaveform$18$MediaController(this.f$1, this.f$2, this.f$3);
    }
}