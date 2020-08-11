package j$.util.function;

/* renamed from: j$.util.function.a  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa implements BiConsumer {
    public final /* synthetic */ BiConsumer a;
    public final /* synthetic */ BiConsumer b;

    public /* synthetic */ CLASSNAMEa(BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.a = biConsumer;
        this.b = biConsumer2;
    }

    public /* synthetic */ BiConsumer a(BiConsumer biConsumer) {
        return CLASSNAMEm.a(this, biConsumer);
    }

    public final void accept(Object obj, Object obj2) {
        CLASSNAMEm.b(this.a, this.b, obj, obj2);
    }
}
