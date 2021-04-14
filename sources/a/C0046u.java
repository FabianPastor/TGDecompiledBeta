package a;

import java.util.function.BiConsumer;

/* renamed from: a.u  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEu implements BiConsumer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j$.util.function.BiConsumer var_a;

    private /* synthetic */ CLASSNAMEu(j$.util.function.BiConsumer biConsumer) {
        this.var_a = biConsumer;
    }

    public static /* synthetic */ BiConsumer a(j$.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof CLASSNAMEt ? ((CLASSNAMEt) biConsumer).var_a : new CLASSNAMEu(biConsumer);
    }

    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.var_a.accept(obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return a(this.var_a.a(CLASSNAMEt.b(biConsumer)));
    }
}
