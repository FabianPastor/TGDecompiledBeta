package j$.util.function;
/* loaded from: classes2.dex */
public final /* synthetic */ class p implements q {
    public final /* synthetic */ q a;
    public final /* synthetic */ q b;

    public /* synthetic */ p(q qVar, q qVar2) {
        this.a = qVar;
        this.b = qVar2;
    }

    @Override // j$.util.function.q
    public final void accept(long j) {
        q qVar = this.a;
        q qVar2 = this.b;
        qVar.accept(j);
        qVar2.accept(j);
    }

    @Override // j$.util.function.q
    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }
}
