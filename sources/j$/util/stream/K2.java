package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
/* loaded from: classes2.dex */
class K2 extends T2 implements S2 {
    final /* synthetic */ j$.util.function.y b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ BiConsumer d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public K2(j$.util.function.y yVar, BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.b = yVar;
        this.c = biConsumer;
        this.d = biConsumer2;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void accept(double d) {
        AbstractCLASSNAMEo1.f(this);
        throw null;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void accept(int i) {
        AbstractCLASSNAMEo1.d(this);
        throw null;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3, j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractCLASSNAMEo1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        this.c.accept(this.a, obj);
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.S2
    public void h(S2 s2) {
        this.d.accept(this.a, ((K2) s2).a);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        this.a = this.b.get();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ boolean o() {
        return false;
    }
}
