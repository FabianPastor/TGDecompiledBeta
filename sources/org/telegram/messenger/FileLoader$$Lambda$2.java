package org.telegram.messenger;

final /* synthetic */ class FileLoader$$Lambda$2 implements Runnable {
    private final FileLoader arg$1;
    private final boolean arg$2;

    FileLoader$$Lambda$2(FileLoader fileLoader, boolean z) {
        this.arg$1 = fileLoader;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$onNetworkChanged$2$FileLoader(this.arg$2);
    }
}
