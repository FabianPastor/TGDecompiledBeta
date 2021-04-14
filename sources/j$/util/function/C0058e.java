package j$.util.function;

/* renamed from: j$.util.function.e  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe implements Consumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Consumer var_a;
    public final /* synthetic */ Consumer b;

    public /* synthetic */ CLASSNAMEe(Consumer consumer, Consumer consumer2) {
        this.var_a = consumer;
        this.b = consumer2;
    }

    public final void accept(Object obj) {
        Consumer consumer = this.var_a;
        Consumer consumer2 = this.b;
        consumer.accept(obj);
        consumer2.accept(obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
