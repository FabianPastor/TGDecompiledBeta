package j$;

import j$.util.function.BiConsumer;

/* renamed from: j$.n  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn implements BiConsumer {
    final /* synthetic */ java.util.function.BiConsumer a;

    private /* synthetic */ CLASSNAMEn(java.util.function.BiConsumer biConsumer) {
        this.a = biConsumer;
    }

    public static /* synthetic */ BiConsumer b(java.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof CLASSNAMEo ? ((CLASSNAMEo) biConsumer).a : new CLASSNAMEn(biConsumer);
    }

    public /* synthetic */ BiConsumer a(BiConsumer biConsumer) {
        return b(this.a.andThen(CLASSNAMEo.a(biConsumer)));
    }

    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.a.accept(obj, obj2);
    }
}
