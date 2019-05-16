package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GroupCreateFinalActivity$NmRkjRZ6-MrsxxvtnDOrMWsjyWE implements Runnable {
    private final /* synthetic */ GroupCreateFinalActivity f$0;
    private final /* synthetic */ InputFile f$1;
    private final /* synthetic */ PhotoSize f$2;
    private final /* synthetic */ PhotoSize f$3;

    public /* synthetic */ -$$Lambda$GroupCreateFinalActivity$NmRkjRZ6-MrsxxvtnDOrMWsjyWE(GroupCreateFinalActivity groupCreateFinalActivity, InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        this.f$0 = groupCreateFinalActivity;
        this.f$1 = inputFile;
        this.f$2 = photoSize;
        this.f$3 = photoSize2;
    }

    public final void run() {
        this.f$0.lambda$didUploadPhoto$5$GroupCreateFinalActivity(this.f$1, this.f$2, this.f$3);
    }
}
