package org.telegram.messenger;

final /* synthetic */ class FileLoader$$Lambda$3 implements Runnable {
    private final FileLoader arg$1;
    private final boolean arg$2;
    private final String arg$3;
    private final long arg$4;
    private final long arg$5;

    FileLoader$$Lambda$3(FileLoader fileLoader, boolean z, String str, long j, long j2) {
        this.arg$1 = fileLoader;
        this.arg$2 = z;
        this.arg$3 = str;
        this.arg$4 = j;
        this.arg$5 = j2;
    }

    public void run() {
        this.arg$1.lambda$checkUploadNewDataAvailable$3$FileLoader(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
