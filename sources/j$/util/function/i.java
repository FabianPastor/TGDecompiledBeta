package j$.util.function;

public final /* synthetic */ class i implements A {
    public final /* synthetic */ A a;
    public final /* synthetic */ A b;

    public /* synthetic */ i(A a2, A a3) {
        this.a = a2;
        this.b = a3;
    }

    public A a(A a2) {
        a2.getClass();
        return new i(this, a2);
    }

    public final long applyAsLong(long j) {
        return this.b.applyAsLong(this.a.applyAsLong(j));
    }

    public A b(A a2) {
        a2.getClass();
        return new j(this, a2);
    }
}
