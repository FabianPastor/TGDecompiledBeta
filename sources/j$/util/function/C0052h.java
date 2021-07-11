package j$.util.function;

/* renamed from: j$.util.function.h  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh implements C {
    public final /* synthetic */ C a;
    public final /* synthetic */ C b;

    public /* synthetic */ CLASSNAMEh(C c, C c2) {
        this.a = c;
        this.b = c2;
    }

    public final void accept(long j) {
        C c = this.a;
        C c2 = this.b;
        c.accept(j);
        c2.accept(j);
    }

    public C f(C c) {
        c.getClass();
        return new CLASSNAMEh(this, c);
    }
}
