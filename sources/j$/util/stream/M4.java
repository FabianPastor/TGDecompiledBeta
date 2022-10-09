package j$.util.stream;
/* loaded from: classes2.dex */
class M4 implements Runnable {
    final /* synthetic */ Runnable a;
    final /* synthetic */ Runnable b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public M4(Runnable runnable, Runnable runnable2) {
        this.a = runnable;
        this.b = runnable2;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.a.run();
            this.b.run();
        } catch (Throwable th) {
            try {
                this.b.run();
            } catch (Throwable unused) {
            }
            throw th;
        }
    }
}
