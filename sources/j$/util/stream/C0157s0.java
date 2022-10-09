package j$.util.stream;

import j$.util.CLASSNAMEh;
import j$.util.function.BiConsumer;
/* renamed from: j$.util.stream.s0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEs0 implements BiConsumer {
    public static final /* synthetic */ CLASSNAMEs0 a = new CLASSNAMEs0();

    private /* synthetic */ CLASSNAMEs0() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((CLASSNAMEh) obj).b((CLASSNAMEh) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new j$.util.concurrent.a(this, biConsumer);
    }
}
