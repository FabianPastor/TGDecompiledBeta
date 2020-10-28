package j$.util.stream;

class U4 extends CLASSNAMEp5 {
    final /* synthetic */ V4 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    U4(V4 v4, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = v4;
    }

    public void accept(Object obj) {
        this.a.accept(this.b.l.apply(obj));
    }
}
