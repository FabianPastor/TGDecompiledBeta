package j$;

import java.util.function.BiConsumer;

/* renamed from: j$.u  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEu implements BiConsumer {
    final /* synthetic */ j$.util.function.BiConsumer a;

    private /* synthetic */ CLASSNAMEu(j$.util.function.BiConsumer biConsumer) {
        this.a = biConsumer;
    }

    public static /* synthetic */ BiConsumer a(j$.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof CLASSNAMEt ? ((CLASSNAMEt) biConsumer).a : new CLASSNAMEu(biConsumer);
    }

    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.a.accept(obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return a(this.a.a(CLASSNAMEt.b(biConsumer)));
    }
}
