package j$;

import j$.util.function.BiConsumer;

/* renamed from: j$.q  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEq implements BiConsumer {
    final /* synthetic */ java.util.function.BiConsumer a;

    private /* synthetic */ CLASSNAMEq(java.util.function.BiConsumer biConsumer) {
        this.a = biConsumer;
    }

    public static /* synthetic */ BiConsumer b(java.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof r ? ((r) biConsumer).a : new CLASSNAMEq(biConsumer);
    }

    public /* synthetic */ BiConsumer a(BiConsumer biConsumer) {
        return b(this.a.andThen(r.a(biConsumer)));
    }

    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.a.accept(obj, obj2);
    }
}
