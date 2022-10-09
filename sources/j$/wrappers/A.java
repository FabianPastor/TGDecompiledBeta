package j$.wrappers;

import java.util.function.DoubleConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class A implements j$.util.function.f {
    final /* synthetic */ DoubleConsumer a;

    private /* synthetic */ A(DoubleConsumer doubleConsumer) {
        this.a = doubleConsumer;
    }

    public static /* synthetic */ j$.util.function.f b(DoubleConsumer doubleConsumer) {
        if (doubleConsumer == null) {
            return null;
        }
        return doubleConsumer instanceof B ? ((B) doubleConsumer).a : new A(doubleConsumer);
    }

    @Override // j$.util.function.f
    public /* synthetic */ void accept(double d) {
        this.a.accept(d);
    }

    @Override // j$.util.function.f
    public /* synthetic */ j$.util.function.f j(j$.util.function.f fVar) {
        return b(this.a.andThen(B.a(fVar)));
    }
}
