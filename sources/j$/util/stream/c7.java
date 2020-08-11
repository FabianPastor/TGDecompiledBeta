package j$.util.stream;

class c7 implements Runnable {
    final /* synthetic */ Runnable a;
    final /* synthetic */ Runnable b;

    c7(Runnable runnable, Runnable runnable2) {
        this.a = runnable;
        this.b = runnable2;
    }

    public void run() {
        try {
            this.a.run();
            this.b.run();
            return;
        } catch (Throwable th) {
        }
        throw e1;
    }
}
