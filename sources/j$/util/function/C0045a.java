package j$.util.function;

/* renamed from: j$.util.function.a  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa implements BiConsumer {
    public final /* synthetic */ BiConsumer a;
    public final /* synthetic */ BiConsumer b;

    public /* synthetic */ CLASSNAMEa(BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.a = biConsumer;
        this.b = biConsumer2;
    }

    public BiConsumer a(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new CLASSNAMEa(this, biConsumer);
    }

    public final void accept(Object obj, Object obj2) {
        BiConsumer biConsumer = this.a;
        BiConsumer biConsumer2 = this.b;
        biConsumer.accept(obj, obj2);
        biConsumer2.accept(obj, obj2);
    }
}
