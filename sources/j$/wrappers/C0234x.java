package j$.wrappers;

import java.util.function.Consumer;
/* renamed from: j$.wrappers.x  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEx implements Consumer {
    final /* synthetic */ j$.util.function.Consumer a;

    private /* synthetic */ CLASSNAMEx(j$.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer a(j$.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof CLASSNAMEw ? ((CLASSNAMEw) consumer).a : new CLASSNAMEx(consumer);
    }

    @Override // java.util.function.Consumer
    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    @Override // java.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return a(this.a.andThen(CLASSNAMEw.b(consumer)));
    }
}
