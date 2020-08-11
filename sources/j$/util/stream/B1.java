package j$.util.stream;

class B1 extends CLASSNAMEw5 {
    final /* synthetic */ C1 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    B1(C1 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(double t) {
        M1 result = (M1) this.b.m.a(t);
        if (result != null) {
            try {
                result.sequential().n(new CLASSNAMEo(this));
            } catch (Throwable th) {
            }
        }
        if (result != null) {
            result.close();
            return;
        }
        return;
        throw th;
    }

    public /* synthetic */ void a(double i) {
        this.a.accept(i);
    }
}
