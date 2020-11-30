package j$;

import j$.util.function.BiConsumer;

/* renamed from: j$.t  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEt implements BiConsumer {
    final /* synthetic */ java.util.function.BiConsumer a;

    private /* synthetic */ CLASSNAMEt(java.util.function.BiConsumer biConsumer) {
        this.a = biConsumer;
    }

    public static /* synthetic */ BiConsumer b(java.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof CLASSNAMEu ? ((CLASSNAMEu) biConsumer).a : new CLASSNAMEt(biConsumer);
    }

    public /* synthetic */ BiConsumer a(BiConsumer biConsumer) {
        return b(this.a.andThen(CLASSNAMEu.a(biConsumer)));
    }

    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.a.accept(obj, obj2);
    }
}
