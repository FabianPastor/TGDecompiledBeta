package j$.util.stream;

/* renamed from: j$.util.stream.p1  reason: case insensitive filesystem */
class CLASSNAMEp1 extends CLASSNAMEp5 {
    boolean b;
    Object c;

    CLASSNAMEp1(CLASSNAMEr1 r1Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
    }

    public void accept(Object obj) {
        if (obj != null) {
            Object obj2 = this.c;
            if (obj2 == null || !obj.equals(obj2)) {
                CLASSNAMEt5 t5Var = this.a;
                this.c = obj;
                t5Var.accept(obj);
            }
        } else if (!this.b) {
            this.b = true;
            CLASSNAMEt5 t5Var2 = this.a;
            this.c = null;
            t5Var2.accept((Object) null);
        }
    }

    public void m() {
        this.b = false;
        this.c = null;
        this.a.m();
    }

    public void n(long j) {
        this.b = false;
        this.c = null;
        this.a.n(-1);
    }
}
