package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SettingsActivity$y3N6AebEX9cIH8T9V2hR3xgRVDs implements Runnable {
    private final /* synthetic */ SettingsActivity f$0;
    private final /* synthetic */ InputFile f$1;
    private final /* synthetic */ PhotoSize f$2;
    private final /* synthetic */ PhotoSize f$3;

    public /* synthetic */ -$$Lambda$SettingsActivity$y3N6AebEX9cIH8T9V2hR3xgRVDs(SettingsActivity settingsActivity, InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        this.f$0 = settingsActivity;
        this.f$1 = inputFile;
        this.f$2 = photoSize;
        this.f$3 = photoSize2;
    }

    public final void run() {
        this.f$0.lambda$didUploadPhoto$10$SettingsActivity(this.f$1, this.f$2, this.f$3);
    }
}
