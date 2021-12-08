package j$.util.stream;

import j$.util.function.Consumer;
import java.util.List;

public final /* synthetic */ class SpinedBuffer$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ List f$0;

    public /* synthetic */ SpinedBuffer$$ExternalSyntheticLambda0(List list) {
        this.f$0 = list;
    }

    public final void accept(Object obj) {
        this.f$0.add(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
