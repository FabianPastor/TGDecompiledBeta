package org.telegram.messenger;

final /* synthetic */ class MusicPlayerService$$Lambda$1 implements Runnable {
    private final MusicPlayerService arg$1;

    MusicPlayerService$$Lambda$1(MusicPlayerService musicPlayerService) {
        this.arg$1 = musicPlayerService;
    }

    public void run() {
        this.arg$1.stopSelf();
    }
}
