package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEa;

/* renamed from: j$.util.stream.v  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv implements BiConsumer {
    public static final /* synthetic */ CLASSNAMEv a = new CLASSNAMEv();

    private /* synthetic */ CLASSNAMEv() {
    }

    public BiConsumer a(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new CLASSNAMEa(this, biConsumer);
    }

    public final void accept(Object obj, Object obj2) {
        double[] dArr = (double[]) obj;
        double[] dArr2 = (double[]) obj2;
        CLASSNAMEn1.b(dArr, dArr2[0]);
        CLASSNAMEn1.b(dArr, dArr2[1]);
        dArr[2] = dArr[2] + dArr2[2];
        dArr[3] = dArr[3] + dArr2[3];
    }
}
