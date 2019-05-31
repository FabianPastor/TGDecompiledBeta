package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$3$O9MuPZ9kD1RfxaVvbxm2MMOXv_E implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ InputFile f$2;
    private final /* synthetic */ InputEncryptedFile f$3;
    private final /* synthetic */ byte[] f$4;
    private final /* synthetic */ byte[] f$5;
    private final /* synthetic */ long f$6;

    public /* synthetic */ -$$Lambda$ImageLoader$3$O9MuPZ9kD1RfxaVvbxm2MMOXv_E(int i, String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
        this.f$0 = i;
        this.f$1 = str;
        this.f$2 = inputFile;
        this.f$3 = inputEncryptedFile;
        this.f$4 = bArr;
        this.f$5 = bArr2;
        this.f$6 = j;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileDidUpload, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, Long.valueOf(this.f$6));
    }
}
