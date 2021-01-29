package j$.util.function;

/* renamed from: j$.util.function.f  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEf implements q {
    public final /* synthetic */ q a;
    public final /* synthetic */ q b;

    public /* synthetic */ CLASSNAMEf(q qVar, q qVar2) {
        this.a = qVar;
        this.b = qVar2;
    }

    public final void accept(double d) {
        q qVar = this.a;
        q qVar2 = this.b;
        qVar.accept(d);
        qVar2.accept(d);
    }

    public q k(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
    }
}
