package j$.util.stream;

import j$.util.DesugarArrays;
import j$.util.DoubleSummaryStatistics;
import j$.util.Iterator;
import j$.util.OptionalDouble;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.DoubleBinaryOperator;
import j$.util.function.DoubleConsumer;
import j$.util.function.DoubleFunction;
import j$.util.function.DoublePredicate;
import j$.util.function.DoubleSupplier;
import j$.util.function.DoubleToIntFunction;
import j$.util.function.DoubleToLongFunction;
import j$.util.function.DoubleUnaryOperator;
import j$.util.function.ObjDoubleConsumer;
import j$.util.function.Supplier;
import j$.util.stream.StreamSpliterators;
import j$.util.stream.Streams;

public interface DoubleStream extends BaseStream<Double, DoubleStream> {
    boolean allMatch(DoublePredicate doublePredicate);

    boolean anyMatch(DoublePredicate doublePredicate);

    OptionalDouble average();

    Stream<Double> boxed();

    <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> objDoubleConsumer, BiConsumer<R, R> biConsumer);

    long count();

    DoubleStream distinct();

    DoubleStream filter(DoublePredicate doublePredicate);

    OptionalDouble findAny();

    OptionalDouble findFirst();

    DoubleStream flatMap(DoubleFunction<? extends DoubleStream> doubleFunction);

    void forEach(DoubleConsumer doubleConsumer);

    void forEachOrdered(DoubleConsumer doubleConsumer);

    PrimitiveIterator.OfDouble iterator();

    DoubleStream limit(long j);

    DoubleStream map(DoubleUnaryOperator doubleUnaryOperator);

    IntStream mapToInt(DoubleToIntFunction doubleToIntFunction);

    LongStream mapToLong(DoubleToLongFunction doubleToLongFunction);

    <U> Stream<U> mapToObj(DoubleFunction<? extends U> doubleFunction);

    OptionalDouble max();

    OptionalDouble min();

    boolean noneMatch(DoublePredicate doublePredicate);

    DoubleStream parallel();

    DoubleStream peek(DoubleConsumer doubleConsumer);

    double reduce(double d, DoubleBinaryOperator doubleBinaryOperator);

    OptionalDouble reduce(DoubleBinaryOperator doubleBinaryOperator);

    DoubleStream sequential();

    DoubleStream skip(long j);

    DoubleStream sorted();

    Spliterator.OfDouble spliterator();

    double sum();

    DoubleSummaryStatistics summaryStatistics();

    double[] toArray();

    /* renamed from: j$.util.stream.DoubleStream$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Builder builder() {
            return new Streams.DoubleStreamBuilderImpl();
        }

        public static DoubleStream empty() {
            return StreamSupport.doubleStream(Spliterators.emptyDoubleSpliterator(), false);
        }

        public static DoubleStream of(double t) {
            return StreamSupport.doubleStream(new Streams.DoubleStreamBuilderImpl(t), false);
        }

        public static DoubleStream of(double... values) {
            return DesugarArrays.stream(values);
        }

        public static DoubleStream iterate(double seed, DoubleUnaryOperator f) {
            f.getClass();
            return StreamSupport.doubleStream(Spliterators.spliteratorUnknownSize(new Object(seed, f) {
                double t;
                final /* synthetic */ DoubleUnaryOperator val$f;
                final /* synthetic */ double val$seed;

                public /* synthetic */ void forEachRemaining(Consumer consumer) {
                    PrimitiveIterator.OfDouble.CC.$default$forEachRemaining((PrimitiveIterator.OfDouble) this, consumer);
                }

                public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                    PrimitiveIterator.OfDouble.CC.$default$forEachRemaining((PrimitiveIterator.OfDouble) this, doubleConsumer);
                }

                public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
                    forEachRemaining((DoubleConsumer) obj);
                }

                public /* synthetic */ void remove() {
                    Iterator.CC.$default$remove(this);
                }

                {
                    this.val$seed = r1;
                    this.val$f = r3;
                    this.t = r1;
                }

                public boolean hasNext() {
                    return true;
                }

                public double nextDouble() {
                    double v = this.t;
                    this.t = this.val$f.applyAsDouble(this.t);
                    return v;
                }
            }, 1296), false);
        }

        public static DoubleStream generate(DoubleSupplier s) {
            s.getClass();
            return StreamSupport.doubleStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfDouble(Long.MAX_VALUE, s), false);
        }

        public static DoubleStream concat(DoubleStream a, DoubleStream b) {
            a.getClass();
            b.getClass();
            return (DoubleStream) StreamSupport.doubleStream(new Streams.ConcatSpliterator.OfDouble(a.spliterator(), b.spliterator()), a.isParallel() || b.isParallel()).onClose(Streams.composedClose(a, b));
        }
    }

    public interface Builder extends DoubleConsumer {
        void accept(double d);

        Builder add(double d);

        DoubleStream build();

        /* renamed from: j$.util.stream.DoubleStream$Builder$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static Builder $default$add(Builder _this, double t) {
                _this.accept(t);
                return _this;
            }
        }
    }
}
