package a;

import j$.util.function.BiConsumer;

/* renamed from: a.t  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEt implements BiConsumer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ java.util.function.BiConsumer var_a;

    private /* synthetic */ CLASSNAMEt(java.util.function.BiConsumer biConsumer) {
        this.var_a = biConsumer;
    }

    public static /* synthetic */ BiConsumer b(java.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof CLASSNAMEu ? ((CLASSNAMEu) biConsumer).var_a : new CLASSNAMEt(biConsumer);
    }

    public /* synthetic */ BiConsumer a(BiConsumer biConsumer) {
        return b(this.var_a.andThen(CLASSNAMEu.a(biConsumer)));
    }

    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.var_a.accept(obj, obj2);
    }
}
