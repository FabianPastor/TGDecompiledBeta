package j$.util.stream;

import j$.util.CLASSNAMEg;
import j$.util.function.BiConsumer;
/* renamed from: j$.util.stream.t  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEt implements BiConsumer {
    public static final /* synthetic */ CLASSNAMEt a = new CLASSNAMEt();

    private /* synthetic */ CLASSNAMEt() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((CLASSNAMEg) obj).b((CLASSNAMEg) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new j$.util.concurrent.a(this, biConsumer);
    }
}
