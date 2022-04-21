package j$.util.stream;

import j$.util.function.Consumer;

public final /* synthetic */ class SortedOps$RefSortingSink$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ Sink f$0;

    public /* synthetic */ SortedOps$RefSortingSink$$ExternalSyntheticLambda0(Sink sink) {
        this.f$0 = sink;
    }

    public final void accept(Object obj) {
        this.f$0.accept(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
