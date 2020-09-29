package j$;

import j$.util.function.BiConsumer;

/* renamed from: j$.y  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEy implements BiConsumer {
    final /* synthetic */ java.util.function.BiConsumer a;

    private /* synthetic */ CLASSNAMEy(java.util.function.BiConsumer biConsumer) {
        this.a = biConsumer;
    }

    public static /* synthetic */ BiConsumer b(java.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof CLASSNAMEz ? ((CLASSNAMEz) biConsumer).a : new CLASSNAMEy(biConsumer);
    }

    public /* synthetic */ BiConsumer a(BiConsumer biConsumer) {
        return b(this.a.andThen(CLASSNAMEz.a(biConsumer)));
    }

    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.a.accept(obj, obj2);
    }
}
