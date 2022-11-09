package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.k4  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEk4 extends AbstractCLASSNAMEl4 implements Consumer {
    final Object[] b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEk4(int i) {
        this.b = new Object[i];
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        Object[] objArr = this.b;
        int i = this.a;
        this.a = i + 1;
        objArr[i] = obj;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }
}
