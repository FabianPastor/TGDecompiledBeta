package j$.util.stream;

class I2 extends CLASSNAMEo5 {
    final /* synthetic */ J2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    I2(J2 j2, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = j2;
    }

    public void accept(long j) {
        T2 t2 = (T2) this.b.l.apply(j);
        if (t2 != null) {
            try {
                t2.sequential().e(new Q(this));
            } catch (Throwable unused) {
            }
        }
        if (t2 != null) {
            t2.close();
            return;
        }
        return;
        throw th;
    }

    public void n(long j) {
        this.a.n(-1);
    }
}
