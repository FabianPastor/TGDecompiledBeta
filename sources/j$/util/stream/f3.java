package j$.util.stream;

class f3 implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Runnable var_a;
    final /* synthetic */ Runnable b;

    f3(Runnable runnable, Runnable runnable2) {
        this.var_a = runnable;
        this.b = runnable2;
    }

    public void run() {
        try {
            this.var_a.run();
            this.b.run();
            return;
        } catch (Throwable unused) {
        }
        throw th;
    }
}
