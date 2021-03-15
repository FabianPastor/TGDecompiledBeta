package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEa;

/* renamed from: j$.util.stream.p  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEp implements BiConsumer {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ CLASSNAMEp var_a = new CLASSNAMEp();

    private /* synthetic */ CLASSNAMEp() {
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
    }
}
