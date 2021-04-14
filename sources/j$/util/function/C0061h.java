package j$.util.function;

/* renamed from: j$.util.function.h  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh implements C {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ C var_a;
    public final /* synthetic */ C b;

    public /* synthetic */ CLASSNAMEh(C c, C c2) {
        this.var_a = c;
        this.b = c2;
    }

    public final void accept(long j) {
        C c = this.var_a;
        C c2 = this.b;
        c.accept(j);
        c2.accept(j);
    }

    public C g(C c) {
        c.getClass();
        return new CLASSNAMEh(this, c);
    }
}
