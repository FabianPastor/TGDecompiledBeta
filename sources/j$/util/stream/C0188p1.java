package j$.util.stream;

/* renamed from: j$.util.stream.p1  reason: case insensitive filesystem */
class CLASSNAMEp1 extends CLASSNAMEz5 {
    boolean b;
    Object c;

    CLASSNAMEp1(CLASSNAMEr1 this$0, G5 downstream) {
        super(downstream);
    }

    public void s(long size) {
        this.b = false;
        this.c = null;
        this.a.s(-1);
    }

    public void r() {
        this.b = false;
        this.c = null;
        this.a.r();
    }

    public void accept(Object t) {
        if (t != null) {
            Object obj = this.c;
            if (obj == null || !t.equals(obj)) {
                G5 g5 = this.a;
                this.c = t;
                g5.accept(t);
            }
        } else if (!this.b) {
            this.b = true;
            G5 g52 = this.a;
            this.c = null;
            g52.accept((Object) null);
        }
    }
}
