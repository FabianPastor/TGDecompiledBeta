package j$.wrappers;

import j$.util.function.Consumer;
/* renamed from: j$.wrappers.w  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEw implements Consumer {
    final /* synthetic */ java.util.function.Consumer a;

    private /* synthetic */ CLASSNAMEw(java.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer b(java.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof CLASSNAMEx ? ((CLASSNAMEx) consumer).a : new CLASSNAMEw(consumer);
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return b(this.a.andThen(CLASSNAMEx.a(consumer)));
    }
}
