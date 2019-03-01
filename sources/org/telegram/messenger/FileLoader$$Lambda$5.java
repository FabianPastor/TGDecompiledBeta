package org.telegram.messenger;

final /* synthetic */ class FileLoader$$Lambda$5 implements Runnable {
    private final FileLoader arg$1;
    private final boolean arg$2;
    private final String arg$3;
    private final int arg$4;
    private final int arg$5;
    private final boolean arg$6;

    FileLoader$$Lambda$5(FileLoader fileLoader, boolean z, String str, int i, int i2, boolean z2) {
        this.arg$1 = fileLoader;
        this.arg$2 = z;
        this.arg$3 = str;
        this.arg$4 = i;
        this.arg$5 = i2;
        this.arg$6 = z2;
    }

    public void run() {
        this.arg$1.lambda$uploadFile$5$FileLoader(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
