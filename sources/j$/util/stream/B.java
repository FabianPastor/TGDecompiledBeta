package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiConsumer;

public final /* synthetic */ class B implements BiConsumer {
    public static final /* synthetic */ B a = new B();

    private /* synthetic */ B() {
    }

    public final void accept(Object obj, Object obj2) {
        double[] dArr = (double[]) obj;
        double[] dArr2 = (double[]) obj2;
        CLASSNAMEl.b(dArr, dArr2[0]);
        CLASSNAMEl.b(dArr, dArr2[1]);
        dArr[2] = dArr[2] + dArr2[2];
    }

    public BiConsumer c(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new a((BiConsumer) this, biConsumer);
    }
}
