package j$.util.function;

public final /* synthetic */ class p implements q {
    public final /* synthetic */ q a;
    public final /* synthetic */ q b;

    public /* synthetic */ p(q qVar, q qVar2) {
        this.a = qVar;
        this.b = qVar2;
    }

    public final void accept(long j) {
        q qVar = this.a;
        q qVar2 = this.b;
        qVar.accept(j);
        qVar2.accept(j);
    }

    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }
}
