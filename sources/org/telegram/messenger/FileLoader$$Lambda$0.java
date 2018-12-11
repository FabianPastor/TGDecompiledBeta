package org.telegram.messenger;

final /* synthetic */ class FileLoader$$Lambda$0 implements Runnable {
    private final FileLoader arg$1;
    private final boolean arg$2;
    private final String arg$3;

    FileLoader$$Lambda$0(FileLoader fileLoader, boolean z, String str) {
        this.arg$1 = fileLoader;
        this.arg$2 = z;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$cancelUploadFile$0$FileLoader(this.arg$2, this.arg$3);
    }
}
