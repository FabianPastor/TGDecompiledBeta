package j$;

import java.util.function.BiConsumer;

/* renamed from: j$.z  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEz implements BiConsumer {
    final /* synthetic */ j$.util.function.BiConsumer a;

    private /* synthetic */ CLASSNAMEz(j$.util.function.BiConsumer biConsumer) {
        this.a = biConsumer;
    }

    public static /* synthetic */ BiConsumer a(j$.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof CLASSNAMEy ? ((CLASSNAMEy) biConsumer).a : new CLASSNAMEz(biConsumer);
    }

    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.a.accept(obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return a(this.a.a(CLASSNAMEy.b(biConsumer)));
    }
}
