package j$.util.stream;

import j$.util.Optional;
import j$.util.OptionalDouble;
import j$.util.OptionalInt;
import j$.util.OptionalLong;
import j$.util.Spliterator;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Consumer;
import j$.util.function.DoubleBinaryOperator;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntBinaryOperator;
import j$.util.function.IntConsumer;
import j$.util.function.LongBinaryOperator;
import j$.util.function.LongConsumer;
import j$.util.function.ObjDoubleConsumer;
import j$.util.function.ObjIntConsumer;
import j$.util.function.ObjLongConsumer;
import j$.util.function.Supplier;
import j$.util.stream.Collector;
import j$.util.stream.Sink;
import j$.util.stream.TerminalOp;
import java.util.concurrent.CountedCompleter;

final class ReduceOps {

    private interface AccumulatingSink<T, R, K extends AccumulatingSink<T, R, K>> extends TerminalSink<T, R> {
        void combine(K k);
    }

    private ReduceOps() {
    }

    public static <T, U> TerminalOp<T, U> makeRef(final U seed, final BiFunction<U, ? super T, U> biFunction, final BinaryOperator<U> binaryOperator) {
        biFunction.getClass();
        binaryOperator.getClass();
        return new ReduceOp<T, U, AnonymousClass1ReducingSink>(StreamShape.REFERENCE) {
            public AnonymousClass1ReducingSink makeSink() {
                return new AccumulatingSink<T, U, AnonymousClass1ReducingSink>(seed, biFunction, binaryOperator) {
                    final /* synthetic */ BinaryOperator val$combiner;
                    final /* synthetic */ BiFunction val$reducer;
                    final /* synthetic */ Object val$seed;

                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept((Sink) this, d);
                    }

                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    public /* synthetic */ Consumer andThen(Consumer consumer) {
                        return Consumer.CC.$default$andThen(this, consumer);
                    }

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    {
                        this.val$seed = r1;
                        this.val$reducer = r2;
                        this.val$combiner = r3;
                    }

                    public void begin(long size) {
                        this.state = this.val$seed;
                    }

                    public void accept(T t) {
                        this.state = this.val$reducer.apply(this.state, t);
                    }

                    public void combine(AnonymousClass1ReducingSink other) {
                        this.state = this.val$combiner.apply(this.state, other.state);
                    }
                };
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> makeRef(final BinaryOperator<T> binaryOperator) {
        binaryOperator.getClass();
        return new ReduceOp<T, Optional<T>, AnonymousClass2ReducingSink>(StreamShape.REFERENCE) {
            public AnonymousClass2ReducingSink makeSink() {
                return new AccumulatingSink<T, Optional<T>, AnonymousClass2ReducingSink>() {
                    private boolean empty;
                    private T state;

                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept((Sink) this, d);
                    }

                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    public /* synthetic */ Consumer andThen(Consumer consumer) {
                        return Consumer.CC.$default$andThen(this, consumer);
                    }

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    public void begin(long size) {
                        this.empty = true;
                        this.state = null;
                    }

                    public void accept(T t) {
                        if (this.empty) {
                            this.empty = false;
                            this.state = t;
                            return;
                        }
                        this.state = BinaryOperator.this.apply(this.state, t);
                    }

                    public Optional<T> get() {
                        return this.empty ? Optional.empty() : Optional.of(this.state);
                    }

                    public void combine(AnonymousClass2ReducingSink other) {
                        if (!other.empty) {
                            accept(other.state);
                        }
                    }
                };
            }
        };
    }

    public static <T, I> TerminalOp<T, I> makeRef(Collector<? super T, I, ?> collector) {
        collector.getClass();
        Supplier supplier = collector.supplier();
        BiConsumer<I, ? super T> accumulator = collector.accumulator();
        final BinaryOperator<I> combiner = collector.combiner();
        final BiConsumer<I, ? super T> biConsumer = accumulator;
        final Supplier supplier2 = supplier;
        final Collector<? super T, I, ?> collector2 = collector;
        return new ReduceOp<T, I, AnonymousClass3ReducingSink>(StreamShape.REFERENCE) {
            public AnonymousClass3ReducingSink makeSink() {
                return new AccumulatingSink<T, I, AnonymousClass3ReducingSink>(biConsumer, combiner) {
                    final /* synthetic */ BiConsumer val$accumulator;
                    final /* synthetic */ BinaryOperator val$combiner;

                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept((Sink) this, d);
                    }

                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    public /* synthetic */ Consumer andThen(Consumer consumer) {
                        return Consumer.CC.$default$andThen(this, consumer);
                    }

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    {
                        this.val$accumulator = r2;
                        this.val$combiner = r3;
                    }

                    public void begin(long size) {
                        this.state = Supplier.this.get();
                    }

                    public void accept(T t) {
                        this.val$accumulator.accept(this.state, t);
                    }

                    public void combine(AnonymousClass3ReducingSink other) {
                        this.state = this.val$combiner.apply(this.state, other.state);
                    }
                };
            }

            public int getOpFlags() {
                if (collector2.characteristics().contains(Collector.Characteristics.UNORDERED)) {
                    return StreamOpFlag.NOT_ORDERED;
                }
                return 0;
            }
        };
    }

    public static <T, R> TerminalOp<T, R> makeRef(final Supplier<R> supplier, final BiConsumer<R, ? super T> biConsumer, final BiConsumer<R, R> biConsumer2) {
        supplier.getClass();
        biConsumer.getClass();
        biConsumer2.getClass();
        return new ReduceOp<T, R, AnonymousClass4ReducingSink>(StreamShape.REFERENCE) {
            public AnonymousClass4ReducingSink makeSink() {
                return new AccumulatingSink<T, R, AnonymousClass4ReducingSink>(biConsumer, biConsumer2) {
                    final /* synthetic */ BiConsumer val$accumulator;
                    final /* synthetic */ BiConsumer val$reducer;

                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept((Sink) this, d);
                    }

                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    public /* synthetic */ Consumer andThen(Consumer consumer) {
                        return Consumer.CC.$default$andThen(this, consumer);
                    }

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    {
                        this.val$accumulator = r2;
                        this.val$reducer = r3;
                    }

                    public void begin(long size) {
                        this.state = Supplier.this.get();
                    }

                    public void accept(T t) {
                        this.val$accumulator.accept(this.state, t);
                    }

                    public void combine(AnonymousClass4ReducingSink other) {
                        this.val$reducer.accept(this.state, other.state);
                    }
                };
            }
        };
    }

    public static TerminalOp<Integer, Integer> makeInt(final int identity, final IntBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Integer, Integer, AnonymousClass5ReducingSink>(StreamShape.INT_VALUE) {
            public AnonymousClass5ReducingSink makeSink() {
                return new Object(identity, operator) {
                    private int state;
                    final /* synthetic */ int val$identity;
                    final /* synthetic */ IntBinaryOperator val$operator;

                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept((Sink) this, d);
                    }

                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    public /* synthetic */ void accept(Integer num) {
                        Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
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

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    {
                        this.val$identity = r1;
                        this.val$operator = r2;
                    }

                    public void begin(long size) {
                        this.state = this.val$identity;
                    }

                    public void accept(int t) {
                        this.state = this.val$operator.applyAsInt(this.state, t);
                    }

                    public Integer get() {
                        return Integer.valueOf(this.state);
                    }

                    public void combine(AnonymousClass5ReducingSink other) {
                        accept(other.state);
                    }
                };
            }
        };
    }

    public static TerminalOp<Integer, OptionalInt> makeInt(final IntBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Integer, OptionalInt, AnonymousClass6ReducingSink>(StreamShape.INT_VALUE) {
            public AnonymousClass6ReducingSink makeSink() {
                return new Object() {
                    private boolean empty;
                    private int state;

                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept((Sink) this, d);
                    }

                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    public /* synthetic */ void accept(Integer num) {
                        Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
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

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    public void begin(long size) {
                        this.empty = true;
                        this.state = 0;
                    }

                    public void accept(int t) {
                        if (this.empty) {
                            this.empty = false;
                            this.state = t;
                            return;
                        }
                        this.state = IntBinaryOperator.this.applyAsInt(this.state, t);
                    }

                    public OptionalInt get() {
                        return this.empty ? OptionalInt.empty() : OptionalInt.of(this.state);
                    }

                    public void combine(AnonymousClass6ReducingSink other) {
                        if (!other.empty) {
                            accept(other.state);
                        }
                    }
                };
            }
        };
    }

    public static <R> TerminalOp<Integer, R> makeInt(final Supplier<R> supplier, final ObjIntConsumer<R> objIntConsumer, final BinaryOperator<R> binaryOperator) {
        supplier.getClass();
        objIntConsumer.getClass();
        binaryOperator.getClass();
        return new ReduceOp<Integer, R, AnonymousClass7ReducingSink>(StreamShape.INT_VALUE) {
            public AnonymousClass7ReducingSink makeSink() {
                return new Box<R>(objIntConsumer, binaryOperator) {
                    final /* synthetic */ ObjIntConsumer val$accumulator;
                    final /* synthetic */ BinaryOperator val$combiner;

                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept((Sink) this, d);
                    }

                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    public /* synthetic */ void accept(Integer num) {
                        Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
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

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    {
                        this.val$accumulator = r2;
                        this.val$combiner = r3;
                    }

                    public void begin(long size) {
                        this.state = Supplier.this.get();
                    }

                    public void accept(int t) {
                        this.val$accumulator.accept(this.state, t);
                    }

                    public void combine(AnonymousClass7ReducingSink other) {
                        this.state = this.val$combiner.apply(this.state, other.state);
                    }
                };
            }
        };
    }

    public static TerminalOp<Long, Long> makeLong(final long identity, final LongBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Long, Long, AnonymousClass8ReducingSink>(StreamShape.LONG_VALUE) {
            public AnonymousClass8ReducingSink makeSink() {
                return new Object(identity, operator) {
                    private long state;
                    final /* synthetic */ long val$identity;
                    final /* synthetic */ LongBinaryOperator val$operator;

                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept((Sink) this, d);
                    }

                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    public /* synthetic */ void accept(Long l) {
                        Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
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

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    {
                        this.val$identity = r1;
                        this.val$operator = r3;
                    }

                    public void begin(long size) {
                        this.state = this.val$identity;
                    }

                    public void accept(long t) {
                        this.state = this.val$operator.applyAsLong(this.state, t);
                    }

                    public Long get() {
                        return Long.valueOf(this.state);
                    }

                    public void combine(AnonymousClass8ReducingSink other) {
                        accept(other.state);
                    }
                };
            }
        };
    }

    public static TerminalOp<Long, OptionalLong> makeLong(final LongBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Long, OptionalLong, AnonymousClass9ReducingSink>(StreamShape.LONG_VALUE) {
            public AnonymousClass9ReducingSink makeSink() {
                return new Object() {
                    private boolean empty;
                    private long state;

                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept((Sink) this, d);
                    }

                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    public /* synthetic */ void accept(Long l) {
                        Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
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

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    public void begin(long size) {
                        this.empty = true;
                        this.state = 0;
                    }

                    public void accept(long t) {
                        if (this.empty) {
                            this.empty = false;
                            this.state = t;
                            return;
                        }
                        this.state = LongBinaryOperator.this.applyAsLong(this.state, t);
                    }

                    public OptionalLong get() {
                        return this.empty ? OptionalLong.empty() : OptionalLong.of(this.state);
                    }

                    public void combine(AnonymousClass9ReducingSink other) {
                        if (!other.empty) {
                            accept(other.state);
                        }
                    }
                };
            }
        };
    }

    public static <R> TerminalOp<Long, R> makeLong(final Supplier<R> supplier, final ObjLongConsumer<R> objLongConsumer, final BinaryOperator<R> binaryOperator) {
        supplier.getClass();
        objLongConsumer.getClass();
        binaryOperator.getClass();
        return new ReduceOp<Long, R, AnonymousClass10ReducingSink>(StreamShape.LONG_VALUE) {
            public AnonymousClass10ReducingSink makeSink() {
                return new Box<R>(objLongConsumer, binaryOperator) {
                    final /* synthetic */ ObjLongConsumer val$accumulator;
                    final /* synthetic */ BinaryOperator val$combiner;

                    public /* synthetic */ void accept(double d) {
                        Sink.CC.$default$accept((Sink) this, d);
                    }

                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    public /* synthetic */ void accept(Long l) {
                        Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
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

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    {
                        this.val$accumulator = r2;
                        this.val$combiner = r3;
                    }

                    public void begin(long size) {
                        this.state = Supplier.this.get();
                    }

                    public void accept(long t) {
                        this.val$accumulator.accept(this.state, t);
                    }

                    public void combine(AnonymousClass10ReducingSink other) {
                        this.state = this.val$combiner.apply(this.state, other.state);
                    }
                };
            }
        };
    }

    public static TerminalOp<Double, Double> makeDouble(final double identity, final DoubleBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Double, Double, AnonymousClass11ReducingSink>(StreamShape.DOUBLE_VALUE) {
            public AnonymousClass11ReducingSink makeSink() {
                return new Object(identity, operator) {
                    private double state;
                    final /* synthetic */ double val$identity;
                    final /* synthetic */ DoubleBinaryOperator val$operator;

                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    public /* synthetic */ void accept(Double d) {
                        Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
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

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    {
                        this.val$identity = r1;
                        this.val$operator = r3;
                    }

                    public void begin(long size) {
                        this.state = this.val$identity;
                    }

                    public void accept(double t) {
                        this.state = this.val$operator.applyAsDouble(this.state, t);
                    }

                    public Double get() {
                        return Double.valueOf(this.state);
                    }

                    public void combine(AnonymousClass11ReducingSink other) {
                        accept(other.state);
                    }
                };
            }
        };
    }

    public static TerminalOp<Double, OptionalDouble> makeDouble(final DoubleBinaryOperator operator) {
        operator.getClass();
        return new ReduceOp<Double, OptionalDouble, AnonymousClass12ReducingSink>(StreamShape.DOUBLE_VALUE) {
            public AnonymousClass12ReducingSink makeSink() {
                return new Object() {
                    private boolean empty;
                    private double state;

                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    public /* synthetic */ void accept(Double d) {
                        Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
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

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    public void begin(long size) {
                        this.empty = true;
                        this.state = 0.0d;
                    }

                    public void accept(double t) {
                        if (this.empty) {
                            this.empty = false;
                            this.state = t;
                            return;
                        }
                        this.state = DoubleBinaryOperator.this.applyAsDouble(this.state, t);
                    }

                    public OptionalDouble get() {
                        return this.empty ? OptionalDouble.empty() : OptionalDouble.of(this.state);
                    }

                    public void combine(AnonymousClass12ReducingSink other) {
                        if (!other.empty) {
                            accept(other.state);
                        }
                    }
                };
            }
        };
    }

    public static <R> TerminalOp<Double, R> makeDouble(final Supplier<R> supplier, final ObjDoubleConsumer<R> objDoubleConsumer, final BinaryOperator<R> binaryOperator) {
        supplier.getClass();
        objDoubleConsumer.getClass();
        binaryOperator.getClass();
        return new ReduceOp<Double, R, AnonymousClass13ReducingSink>(StreamShape.DOUBLE_VALUE) {
            public AnonymousClass13ReducingSink makeSink() {
                return new Box<R>(objDoubleConsumer, binaryOperator) {
                    final /* synthetic */ ObjDoubleConsumer val$accumulator;
                    final /* synthetic */ BinaryOperator val$combiner;

                    public /* synthetic */ void accept(int i) {
                        Sink.CC.$default$accept((Sink) this, i);
                    }

                    public /* synthetic */ void accept(long j) {
                        Sink.CC.$default$accept((Sink) this, j);
                    }

                    public /* synthetic */ void accept(Double d) {
                        Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
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

                    public /* synthetic */ boolean cancellationRequested() {
                        return Sink.CC.$default$cancellationRequested(this);
                    }

                    public /* synthetic */ void end() {
                        Sink.CC.$default$end(this);
                    }

                    {
                        this.val$accumulator = r2;
                        this.val$combiner = r3;
                    }

                    public void begin(long size) {
                        this.state = Supplier.this.get();
                    }

                    public void accept(double t) {
                        this.val$accumulator.accept(this.state, t);
                    }

                    public void combine(AnonymousClass13ReducingSink other) {
                        this.state = this.val$combiner.apply(this.state, other.state);
                    }
                };
            }
        };
    }

    private static abstract class Box<U> {
        U state;

        Box() {
        }

        public U get() {
            return this.state;
        }
    }

    private static abstract class ReduceOp<T, R, S extends AccumulatingSink<T, R, S>> implements TerminalOp<T, R> {
        private final StreamShape inputShape;

        public /* synthetic */ int getOpFlags() {
            return TerminalOp.CC.$default$getOpFlags(this);
        }

        public abstract S makeSink();

        ReduceOp(StreamShape shape) {
            this.inputShape = shape;
        }

        public StreamShape inputShape() {
            return this.inputShape;
        }

        public <P_IN> R evaluateSequential(PipelineHelper<T> helper, Spliterator<P_IN> spliterator) {
            return ((AccumulatingSink) helper.wrapAndCopyInto(makeSink(), spliterator)).get();
        }

        public <P_IN> R evaluateParallel(PipelineHelper<T> helper, Spliterator<P_IN> spliterator) {
            return ((AccumulatingSink) new ReduceTask(this, helper, spliterator).invoke()).get();
        }
    }

    private static final class ReduceTask<P_IN, P_OUT, R, S extends AccumulatingSink<P_OUT, R, S>> extends AbstractTask<P_IN, P_OUT, S, ReduceTask<P_IN, P_OUT, R, S>> {
        private final ReduceOp<P_OUT, R, S> op;

        ReduceTask(ReduceOp<P_OUT, R, S> op2, PipelineHelper<P_OUT> helper, Spliterator<P_IN> spliterator) {
            super(helper, spliterator);
            this.op = op2;
        }

        ReduceTask(ReduceTask<P_IN, P_OUT, R, S> parent, Spliterator<P_IN> spliterator) {
            super(parent, spliterator);
            this.op = parent.op;
        }

        /* access modifiers changed from: protected */
        public ReduceTask<P_IN, P_OUT, R, S> makeChild(Spliterator<P_IN> spliterator) {
            return new ReduceTask<>(this, spliterator);
        }

        /* access modifiers changed from: protected */
        public S doLeaf() {
            return (AccumulatingSink) this.helper.wrapAndCopyInto(this.op.makeSink(), this.spliterator);
        }

        public void onCompletion(CountedCompleter<?> caller) {
            if (!isLeaf()) {
                S leftResult = (AccumulatingSink) ((ReduceTask) this.leftChild).getLocalResult();
                leftResult.combine((AccumulatingSink) ((ReduceTask) this.rightChild).getLocalResult());
                setLocalResult(leftResult);
            }
            super.onCompletion(caller);
        }
    }
}
