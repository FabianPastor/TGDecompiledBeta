package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;

interface Sink<T> extends Consumer<T> {
    void accept(double d);

    void accept(int i);

    void accept(long j);

    void begin(long j);

    boolean cancellationRequested();

    void end();

    /* renamed from: j$.util.stream.Sink$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$begin(Sink _this, long size) {
        }

        public static void $default$end(Sink _this) {
        }

        public static boolean $default$cancellationRequested(Sink _this) {
            return false;
        }

        public static void $default$accept(Sink _this, int value) {
            throw new IllegalStateException("called wrong accept method");
        }

        public static void $default$accept(Sink _this, long value) {
            throw new IllegalStateException("called wrong accept method");
        }

        public static void $default$accept(Sink _this, double value) {
            throw new IllegalStateException("called wrong accept method");
        }
    }

    public interface OfInt extends Sink<Integer>, IntConsumer {
        void accept(int i);

        void accept(Integer num);

        /* renamed from: j$.util.stream.Sink$OfInt$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$accept(OfInt _this, Integer i) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
                }
                _this.accept(i.intValue());
            }
        }
    }

    public interface OfLong extends Sink<Long>, LongConsumer {
        void accept(long j);

        void accept(Long l);

        /* renamed from: j$.util.stream.Sink$OfLong$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$accept(OfLong _this, Long i) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Sink.OfLong.accept(Long)");
                }
                _this.accept(i.longValue());
            }
        }
    }

    public interface OfDouble extends Sink<Double>, DoubleConsumer {
        void accept(double d);

        void accept(Double d);

        /* renamed from: j$.util.stream.Sink$OfDouble$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$accept(OfDouble _this, Double i) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
                }
                _this.accept(i.doubleValue());
            }
        }
    }

    public static abstract class ChainedReference<T, E_OUT> implements Sink<T> {
        protected final Sink<? super E_OUT> downstream;

        public /* synthetic */ void accept(double d) {
            CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(int i) {
            CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(long j) {
            CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public ChainedReference(Sink<? super E_OUT> downstream2) {
            downstream2.getClass();
            this.downstream = downstream2;
        }

        public void begin(long size) {
            this.downstream.begin(size);
        }

        public void end() {
            this.downstream.end();
        }

        public boolean cancellationRequested() {
            return this.downstream.cancellationRequested();
        }
    }

    public static abstract class ChainedInt<E_OUT> implements OfInt {
        protected final Sink<? super E_OUT> downstream;

        public /* synthetic */ void accept(double d) {
            CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(long j) {
            CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ void accept(Integer num) {
            OfInt.CC.$default$accept((OfInt) this, num);
        }

        public /* bridge */ /* synthetic */ void accept(Object obj) {
            accept((Integer) obj);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return IntConsumer.CC.$default$andThen(this, intConsumer);
        }

        public ChainedInt(Sink<? super E_OUT> downstream2) {
            downstream2.getClass();
            this.downstream = downstream2;
        }

        public void begin(long size) {
            this.downstream.begin(size);
        }

        public void end() {
            this.downstream.end();
        }

        public boolean cancellationRequested() {
            return this.downstream.cancellationRequested();
        }
    }

    public static abstract class ChainedLong<E_OUT> implements OfLong {
        protected final Sink<? super E_OUT> downstream;

        public /* synthetic */ void accept(double d) {
            CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(int i) {
            CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(Long l) {
            OfLong.CC.$default$accept((OfLong) this, l);
        }

        public /* bridge */ /* synthetic */ void accept(Object obj) {
            accept((Long) obj);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
            return LongConsumer.CC.$default$andThen(this, longConsumer);
        }

        public ChainedLong(Sink<? super E_OUT> downstream2) {
            downstream2.getClass();
            this.downstream = downstream2;
        }

        public void begin(long size) {
            this.downstream.begin(size);
        }

        public void end() {
            this.downstream.end();
        }

        public boolean cancellationRequested() {
            return this.downstream.cancellationRequested();
        }
    }

    public static abstract class ChainedDouble<E_OUT> implements OfDouble {
        protected final Sink<? super E_OUT> downstream;

        public /* synthetic */ void accept(int i) {
            CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(long j) {
            CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ void accept(Double d) {
            OfDouble.CC.$default$accept((OfDouble) this, d);
        }

        public /* bridge */ /* synthetic */ void accept(Object obj) {
            accept((Double) obj);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
            return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
        }

        public ChainedDouble(Sink<? super E_OUT> downstream2) {
            downstream2.getClass();
            this.downstream = downstream2;
        }

        public void begin(long size) {
            this.downstream.begin(size);
        }

        public void end() {
            this.downstream.end();
        }

        public boolean cancellationRequested() {
            return this.downstream.cancellationRequested();
        }
    }
}
