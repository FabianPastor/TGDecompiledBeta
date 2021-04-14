package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEa;

public final /* synthetic */ class N implements BiConsumer {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ N var_a = new N();

    private /* synthetic */ N() {
    }

    public BiConsumer a(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new CLASSNAMEa(this, biConsumer);
    }

    public final void accept(Object obj, Object obj2) {
        long[] jArr = (long[]) obj;
        long[] jArr2 = (long[]) obj2;
        jArr[0] = jArr[0] + jArr2[0];
        jArr[1] = jArr[1] + jArr2[1];
    }
}
