package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatEditActivity$JEC7_ZqWh_rfTyptKpz62bQdTEA implements Runnable {
    private final /* synthetic */ ChatEditActivity f$0;
    private final /* synthetic */ InputFile f$1;
    private final /* synthetic */ PhotoSize f$2;
    private final /* synthetic */ PhotoSize f$3;

    public /* synthetic */ -$$Lambda$ChatEditActivity$JEC7_ZqWh_rfTyptKpz62bQdTEA(ChatEditActivity chatEditActivity, InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        this.f$0 = chatEditActivity;
        this.f$1 = inputFile;
        this.f$2 = photoSize;
        this.f$3 = photoSize2;
    }

    public final void run() {
        this.f$0.lambda$didUploadPhoto$16$ChatEditActivity(this.f$1, this.f$2, this.f$3);
    }
}
