package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.n0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEn0 extends AbstractCLASSNAMEo0 {
    final Consumer b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEn0(Consumer consumer, boolean z) {
        super(z);
        this.b = consumer;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        this.b.accept(obj);
    }
}
