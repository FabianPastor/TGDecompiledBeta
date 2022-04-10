package j$.util.stream;

/* renamed from: j$.util.stream.q  reason: case insensitive filesystem */
class CLASSNAMEq extends CLASSNAMEj3 {
    boolean b;
    Object c;

    CLASSNAMEq(CLASSNAMEs sVar, CLASSNAMEn3 n3Var) {
        super(n3Var);
    }

    public void accept(Object obj) {
        if (obj != null) {
            Object obj2 = this.c;
            if (obj2 == null || !obj.equals(obj2)) {
                CLASSNAMEn3 n3Var = this.a;
                this.c = obj;
                n3Var.accept(obj);
            }
        } else if (!this.b) {
            this.b = true;
            CLASSNAMEn3 n3Var2 = this.a;
            this.c = null;
            n3Var2.accept(null);
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
