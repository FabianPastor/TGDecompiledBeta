package j$.util.stream;

import j$.util.function.Consumer;
import java.util.List;

/* renamed from: j$.util.stream.b1  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb1 implements Consumer {
    public final /* synthetic */ List a;

    public /* synthetic */ CLASSNAMEb1(List list) {
        this.a = list;
    }

    public final void accept(Object obj) {
        this.a.add(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
