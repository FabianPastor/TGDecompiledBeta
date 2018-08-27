package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;

final /* synthetic */ class ImageLoader$2$$Lambda$7 implements Runnable {
    private final int arg$1;
    private final String arg$2;
    private final InputFile arg$3;
    private final InputEncryptedFile arg$4;
    private final byte[] arg$5;
    private final byte[] arg$6;
    private final long arg$7;

    ImageLoader$2$$Lambda$7(int i, String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
        this.arg$1 = i;
        this.arg$2 = str;
        this.arg$3 = inputFile;
        this.arg$4 = inputEncryptedFile;
        this.arg$5 = bArr;
        this.arg$6 = bArr2;
        this.arg$7 = j;
    }

    public void run() {
        NotificationCenter.getInstance(this.arg$1).postNotificationName(NotificationCenter.FileDidUpload, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, Long.valueOf(this.arg$7));
    }
}
