package j$.util.stream;

class N4 implements Runnable {
    final /* synthetic */ Runnable a;
    final /* synthetic */ Runnable b;

    N4(Runnable runnable, Runnable runnable2) {
        this.a = runnable;
        this.b = runnable2;
    }

    public void run() {
        try {
            this.a.run();
            this.b.run();
            return;
        } catch (Throwable unused) {
        }
        throw th;
    }
}
