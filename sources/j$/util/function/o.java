package j$.util.function;

public final /* synthetic */ class o implements p {
    public final /* synthetic */ p a;
    public final /* synthetic */ p b;

    public /* synthetic */ o(p pVar, p pVar2) {
        this.a = pVar;
        this.b = pVar2;
    }

    public final void accept(long j) {
        p pVar = this.a;
        p pVar2 = this.b;
        pVar.accept(j);
        pVar2.accept(j);
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }
}
