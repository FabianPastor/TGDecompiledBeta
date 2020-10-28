package j$.util.stream;

import j$.util.function.Predicate;

class U2 extends Y2 {
    final /* synthetic */ Z2 c;
    final /* synthetic */ Predicate d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    U2(Z2 z2, Predicate predicate) {
        super(z2);
        this.c = z2;
        this.d = predicate;
    }

    public void accept(Object obj) {
        if (!this.a && this.d.test(obj) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }
}
