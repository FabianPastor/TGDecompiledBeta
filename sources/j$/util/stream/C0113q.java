package j$.util.stream;

/* renamed from: j$.util.stream.q  reason: case insensitive filesystem */
class CLASSNAMEq extends CLASSNAMEi3 {
    boolean b;
    Object c;

    CLASSNAMEq(CLASSNAMEs sVar, CLASSNAMEm3 m3Var) {
        super(m3Var);
    }

    public void accept(Object obj) {
        if (obj != null) {
            Object obj2 = this.c;
            if (obj2 == null || !obj.equals(obj2)) {
                CLASSNAMEm3 m3Var = this.a;
                this.c = obj;
                m3Var.accept(obj);
            }
        } else if (!this.b) {
            this.b = true;
            CLASSNAMEm3 m3Var2 = this.a;
            this.c = null;
            m3Var2.accept(null);
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
