package j$.util.function;

/* renamed from: j$.util.function.d  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd implements Consumer {
    public final /* synthetic */ Consumer a;
    public final /* synthetic */ Consumer b;

    public /* synthetic */ CLASSNAMEd(Consumer consumer, Consumer consumer2) {
        this.a = consumer;
        this.b = consumer2;
    }

    public final void accept(Object obj) {
        CLASSNAMEq.b(this.a, this.b, obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }
}
