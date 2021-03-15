package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import java.util.List;

/* renamed from: j$.util.stream.b1  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb1 implements Consumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ List var_a;

    public /* synthetic */ CLASSNAMEb1(List list) {
        this.var_a = list;
    }

    public final void accept(Object obj) {
        this.var_a.add(obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
