package j$;

import java.util.function.BiConsumer;

public final /* synthetic */ class r implements BiConsumer {
    final /* synthetic */ j$.util.function.BiConsumer a;

    private /* synthetic */ r(j$.util.function.BiConsumer biConsumer) {
        this.a = biConsumer;
    }

    public static /* synthetic */ BiConsumer a(j$.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof CLASSNAMEq ? ((CLASSNAMEq) biConsumer).a : new r(biConsumer);
    }

    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.a.accept(obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return a(this.a.a(CLASSNAMEq.b(biConsumer)));
    }
}
