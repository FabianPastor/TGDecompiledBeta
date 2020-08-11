package j$.util.stream;

class L2 extends CLASSNAMEy5 {
    final /* synthetic */ M2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    L2(M2 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(long t) {
        W2 result = (W2) this.b.m.a(t);
        if (result != null) {
            try {
                result.sequential().i(new U(this));
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

    public /* synthetic */ void a(long i) {
        this.a.accept(i);
    }
}
