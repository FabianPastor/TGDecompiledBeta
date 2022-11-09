package j$.wrappers;

import java.util.function.LongConsumer;
/* renamed from: j$.wrappers.f0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEf0 implements j$.util.function.q {
    final /* synthetic */ LongConsumer a;

    private /* synthetic */ CLASSNAMEf0(LongConsumer longConsumer) {
        this.a = longConsumer;
    }

    public static /* synthetic */ j$.util.function.q b(LongConsumer longConsumer) {
        if (longConsumer == null) {
            return null;
        }
        return longConsumer instanceof CLASSNAMEg0 ? ((CLASSNAMEg0) longConsumer).a : new CLASSNAMEf0(longConsumer);
    }

    @Override // j$.util.function.q
    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    @Override // j$.util.function.q
    public /* synthetic */ j$.util.function.q f(j$.util.function.q qVar) {
        return b(this.a.andThen(CLASSNAMEg0.a(qVar)));
    }
}
