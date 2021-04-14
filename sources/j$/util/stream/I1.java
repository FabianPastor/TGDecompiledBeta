package j$.util.stream;

import j$.util.function.Predicate;

class I1 extends M1<T> {
    final /* synthetic */ N1 c;
    final /* synthetic */ Predicate d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    I1(N1 n1, Predicate predicate) {
        super(n1);
        this.c = n1;
        this.d = predicate;
    }

    public void accept(Object obj) {
        if (!this.var_a && this.d.test(obj) == this.c.var_a) {
            this.var_a = true;
            this.b = this.c.b;
        }
    }
}
