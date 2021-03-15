package j$.util.function;

/* renamed from: j$.util.function.a  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa implements BiConsumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ BiConsumer var_a;
    public final /* synthetic */ BiConsumer b;

    public /* synthetic */ CLASSNAMEa(BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.var_a = biConsumer;
        this.b = biConsumer2;
    }

    public BiConsumer a(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new CLASSNAMEa(this, biConsumer);
    }

    public final void accept(Object obj, Object obj2) {
        BiConsumer biConsumer = this.var_a;
        BiConsumer biConsumer2 = this.b;
        biConsumer.accept(obj, obj2);
        biConsumer2.accept(obj, obj2);
    }
}
