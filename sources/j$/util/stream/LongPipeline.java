package j$.util.stream;

import j$.util.LongSummaryStatistics;
import j$.util.OptionalDouble;
import j$.util.OptionalLong;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.IntFunction;
import j$.util.function.LongBinaryOperator;
import j$.util.function.LongConsumer;
import j$.util.function.LongFunction;
import j$.util.function.LongPredicate;
import j$.util.function.LongToDoubleFunction;
import j$.util.function.LongToIntFunction;
import j$.util.function.LongUnaryOperator;
import j$.util.function.ObjLongConsumer;
import j$.util.function.Supplier;
import j$.util.stream.DoublePipeline;
import j$.util.stream.IntPipeline;
import j$.util.stream.MatchOps;
import j$.util.stream.Node;
import j$.util.stream.ReferencePipeline;
import j$.util.stream.Sink;
import j$.util.stream.StreamSpliterators;

abstract class LongPipeline<E_IN> extends AbstractPipeline<E_IN, Long, LongStream> implements LongStream {
    public /* bridge */ /* synthetic */ LongStream parallel() {
        return (LongStream) super.parallel();
    }

    public /* bridge */ /* synthetic */ LongStream sequential() {
        return (LongStream) super.sequential();
    }

    LongPipeline(Supplier<? extends Spliterator<Long>> supplier, int sourceFlags, boolean parallel) {
        super((Supplier<? extends Spliterator<?>>) supplier, sourceFlags, parallel);
    }

    LongPipeline(Spliterator<Long> spliterator, int sourceFlags, boolean parallel) {
        super((Spliterator<?>) spliterator, sourceFlags, parallel);
    }

    LongPipeline(AbstractPipeline<?, E_IN, ?> upstream, int opFlags) {
        super(upstream, opFlags);
    }

    private static LongConsumer adapt(Sink<Long> sink) {
        if (sink instanceof LongConsumer) {
            return (LongConsumer) sink;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using LongStream.adapt(Sink<Long> s)");
        }
        sink.getClass();
        return new LongPipeline$$ExternalSyntheticLambda7(sink);
    }

    /* access modifiers changed from: private */
    public static Spliterator.OfLong adapt(Spliterator<Long> spliterator) {
        if (spliterator instanceof Spliterator.OfLong) {
            return (Spliterator.OfLong) spliterator;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using LongStream.adapt(Spliterator<Long> s)");
        }
        throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
    }

    /* access modifiers changed from: package-private */
    public final StreamShape getOutputShape() {
        return StreamShape.LONG_VALUE;
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Node<Long> evaluateToNode(PipelineHelper<Long> helper, Spliterator<P_IN> spliterator, boolean flattenTree, IntFunction<Long[]> intFunction) {
        return Nodes.collectLong(helper, spliterator, flattenTree);
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Spliterator<Long> wrap(PipelineHelper<Long> ph, Supplier<Spliterator<P_IN>> supplier, boolean isParallel) {
        return new StreamSpliterators.LongWrappingSpliterator(ph, supplier, isParallel);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator.OfLong lazySpliterator(Supplier<? extends Spliterator<Long>> supplier) {
        return new StreamSpliterators.DelegatingSpliterator.OfLong(supplier);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:1:0x0008 A[LOOP:0: B:1:0x0008->B:4:0x0012, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void forEachWithCancel(j$.util.Spliterator<java.lang.Long> r4, j$.util.stream.Sink<java.lang.Long> r5) {
        /*
            r3 = this;
            j$.util.Spliterator$OfLong r0 = adapt((j$.util.Spliterator<java.lang.Long>) r4)
            j$.util.function.LongConsumer r1 = adapt((j$.util.stream.Sink<java.lang.Long>) r5)
        L_0x0008:
            boolean r2 = r5.cancellationRequested()
            if (r2 != 0) goto L_0x0014
            boolean r2 = r0.tryAdvance((j$.util.function.LongConsumer) r1)
            if (r2 != 0) goto L_0x0008
        L_0x0014:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.LongPipeline.forEachWithCancel(j$.util.Spliterator, j$.util.stream.Sink):void");
    }

    /* access modifiers changed from: package-private */
    public final Node.Builder<Long> makeNodeBuilder(long exactSizeIfKnown, IntFunction<Long[]> intFunction) {
        return Nodes.longBuilder(exactSizeIfKnown);
    }

    public final PrimitiveIterator.OfLong iterator() {
        return Spliterators.iterator(spliterator());
    }

    public final Spliterator.OfLong spliterator() {
        return adapt((Spliterator<Long>) super.spliterator());
    }

    public final DoubleStream asDoubleStream() {
        return new DoublePipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Long> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedLong<Double>(sink) {
                    public void accept(long t) {
                        this.downstream.accept((double) t);
                    }
                };
            }
        };
    }

    public final Stream<Long> boxed() {
        return mapToObj(LongPipeline$$ExternalSyntheticLambda8.INSTANCE);
    }

    public final LongStream map(LongUnaryOperator mapper) {
        mapper.getClass();
        final LongUnaryOperator longUnaryOperator = mapper;
        return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedLong<Long>(sink) {
                    public void accept(long t) {
                        this.downstream.accept(longUnaryOperator.applyAsLong(t));
                    }
                };
            }
        };
    }

    public final <U> Stream<U> mapToObj(LongFunction<? extends U> longFunction) {
        longFunction.getClass();
        final LongFunction<? extends U> longFunction2 = longFunction;
        return new ReferencePipeline.StatelessOp<Long, U>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Long> opWrapSink(int flags, Sink<U> sink) {
                return new Sink.ChainedLong<U>(sink) {
                    public void accept(long t) {
                        this.downstream.accept(longFunction2.apply(t));
                    }
                };
            }
        };
    }

    public final IntStream mapToInt(LongToIntFunction mapper) {
        mapper.getClass();
        final LongToIntFunction longToIntFunction = mapper;
        return new IntPipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Long> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedLong<Integer>(sink) {
                    public void accept(long t) {
                        this.downstream.accept(longToIntFunction.applyAsInt(t));
                    }
                };
            }
        };
    }

    public final DoubleStream mapToDouble(LongToDoubleFunction mapper) {
        mapper.getClass();
        final LongToDoubleFunction longToDoubleFunction = mapper;
        return new DoublePipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Long> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedLong<Double>(sink) {
                    public void accept(long t) {
                        this.downstream.accept(longToDoubleFunction.applyAsDouble(t));
                    }
                };
            }
        };
    }

    public final LongStream flatMap(LongFunction<? extends LongStream> longFunction) {
        final LongFunction<? extends LongStream> longFunction2 = longFunction;
        return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedLong<Long>(sink) {
                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(long t) {
                        LongStream result = (LongStream) longFunction2.apply(t);
                        if (result != null) {
                            try {
                                result.sequential().forEach(new LongPipeline$6$1$$ExternalSyntheticLambda0(this));
                            } catch (Throwable th) {
                            }
                        }
                        if (result != null) {
                            result.close();
                            return;
                        }
                        return;
                        throw th;
                    }

                    /* renamed from: lambda$accept$0$java-util-stream-LongPipeline$6$1  reason: not valid java name */
                    public /* synthetic */ void m4lambda$accept$0$javautilstreamLongPipeline$6$1(long i) {
                        this.downstream.accept(i);
                    }
                };
            }
        };
    }

    public LongStream unordered() {
        if (!isOrdered()) {
            return this;
        }
        return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_ORDERED) {
            /* access modifiers changed from: package-private */
            public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                return sink;
            }
        };
    }

    public final LongStream filter(LongPredicate predicate) {
        predicate.getClass();
        final LongPredicate longPredicate = predicate;
        return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedLong<Long>(sink) {
                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(long t) {
                        if (longPredicate.test(t)) {
                            this.downstream.accept(t);
                        }
                    }
                };
            }
        };
    }

    public final LongStream peek(LongConsumer action) {
        action.getClass();
        final LongConsumer longConsumer = action;
        return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, 0) {
            /* access modifiers changed from: package-private */
            public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedLong<Long>(sink) {
                    public void accept(long t) {
                        longConsumer.accept(t);
                        this.downstream.accept(t);
                    }
                };
            }
        };
    }

    public final LongStream limit(long maxSize) {
        if (maxSize >= 0) {
            return SliceOps.makeLong(this, 0, maxSize);
        }
        throw new IllegalArgumentException(Long.toString(maxSize));
    }

    public final LongStream skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        } else if (n == 0) {
            return this;
        } else {
            return SliceOps.makeLong(this, n, -1);
        }
    }

    public final LongStream sorted() {
        return SortedOps.makeLong(this);
    }

    public final LongStream distinct() {
        return boxed().distinct().mapToLong(LongPipeline$$ExternalSyntheticLambda13.INSTANCE);
    }

    public void forEach(LongConsumer action) {
        evaluate(ForEachOps.makeLong(action, false));
    }

    public void forEachOrdered(LongConsumer action) {
        evaluate(ForEachOps.makeLong(action, true));
    }

    public final long sum() {
        return reduce(0, LongPipeline$$ExternalSyntheticLambda4.INSTANCE);
    }

    public final OptionalLong min() {
        return reduce(LongPipeline$$ExternalSyntheticLambda6.INSTANCE);
    }

    public final OptionalLong max() {
        return reduce(LongPipeline$$ExternalSyntheticLambda5.INSTANCE);
    }

    static /* synthetic */ long[] lambda$average$1() {
        return new long[2];
    }

    public final OptionalDouble average() {
        long[] avg = (long[]) collect(LongPipeline$$ExternalSyntheticLambda12.INSTANCE, LongPipeline$$ExternalSyntheticLambda11.INSTANCE, LongPipeline$$ExternalSyntheticLambda1.INSTANCE);
        if (avg[0] <= 0) {
            return OptionalDouble.empty();
        }
        double d = (double) avg[1];
        double d2 = (double) avg[0];
        Double.isNaN(d);
        Double.isNaN(d2);
        return OptionalDouble.of(d / d2);
    }

    static /* synthetic */ void lambda$average$2(long[] ll, long i) {
        ll[0] = ll[0] + 1;
        ll[1] = ll[1] + i;
    }

    static /* synthetic */ void lambda$average$3(long[] ll, long[] rr) {
        ll[0] = ll[0] + rr[0];
        ll[1] = ll[1] + rr[1];
    }

    static /* synthetic */ long lambda$count$4(long e) {
        return 1;
    }

    public final long count() {
        return map(LongPipeline$$ExternalSyntheticLambda9.INSTANCE).sum();
    }

    public final LongSummaryStatistics summaryStatistics() {
        return (LongSummaryStatistics) collect(Collectors$$ExternalSyntheticLambda82.INSTANCE, LongPipeline$$ExternalSyntheticLambda10.INSTANCE, LongPipeline$$ExternalSyntheticLambda0.INSTANCE);
    }

    public final long reduce(long identity, LongBinaryOperator op) {
        return ((Long) evaluate(ReduceOps.makeLong(identity, op))).longValue();
    }

    public final OptionalLong reduce(LongBinaryOperator op) {
        return (OptionalLong) evaluate(ReduceOps.makeLong(op));
    }

    public final <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> objLongConsumer, BiConsumer<R, R> biConsumer) {
        return evaluate(ReduceOps.makeLong(supplier, objLongConsumer, new LongPipeline$$ExternalSyntheticLambda2(biConsumer)));
    }

    public final boolean anyMatch(LongPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeLong(predicate, MatchOps.MatchKind.ANY))).booleanValue();
    }

    public final boolean allMatch(LongPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeLong(predicate, MatchOps.MatchKind.ALL))).booleanValue();
    }

    public final boolean noneMatch(LongPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeLong(predicate, MatchOps.MatchKind.NONE))).booleanValue();
    }

    public final OptionalLong findFirst() {
        return (OptionalLong) evaluate(FindOps.makeLong(true));
    }

    public final OptionalLong findAny() {
        return (OptionalLong) evaluate(FindOps.makeLong(false));
    }

    static /* synthetic */ Long[] lambda$toArray$6(int x$0) {
        return new Long[x$0];
    }

    public final long[] toArray() {
        return (long[]) Nodes.flattenLong((Node.OfLong) evaluateToArrayNode(LongPipeline$$ExternalSyntheticLambda3.INSTANCE)).asPrimitiveArray();
    }

    static class Head<E_IN> extends LongPipeline<E_IN> {
        public /* bridge */ /* synthetic */ LongStream parallel() {
            return (LongStream) LongPipeline.super.parallel();
        }

        public /* bridge */ /* synthetic */ LongStream sequential() {
            return (LongStream) LongPipeline.super.sequential();
        }

        Head(Supplier<? extends Spliterator<Long>> supplier, int sourceFlags, boolean parallel) {
            super(supplier, sourceFlags, parallel);
        }

        Head(Spliterator<Long> spliterator, int sourceFlags, boolean parallel) {
            super(spliterator, sourceFlags, parallel);
        }

        /* access modifiers changed from: package-private */
        public final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        /* access modifiers changed from: package-private */
        public final Sink<E_IN> opWrapSink(int flags, Sink<Long> sink) {
            throw new UnsupportedOperationException();
        }

        public void forEach(LongConsumer action) {
            if (!isParallel()) {
                LongPipeline.adapt((Spliterator<Long>) sourceStageSpliterator()).forEachRemaining(action);
            } else {
                LongPipeline.super.forEach(action);
            }
        }

        public void forEachOrdered(LongConsumer action) {
            if (!isParallel()) {
                LongPipeline.adapt((Spliterator<Long>) sourceStageSpliterator()).forEachRemaining(action);
            } else {
                LongPipeline.super.forEachOrdered(action);
            }
        }
    }

    static abstract class StatelessOp<E_IN> extends LongPipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        public /* bridge */ /* synthetic */ LongStream parallel() {
            return (LongStream) LongPipeline.super.parallel();
        }

        public /* bridge */ /* synthetic */ LongStream sequential() {
            return (LongStream) LongPipeline.super.sequential();
        }

        StatelessOp(AbstractPipeline<?, E_IN, ?> upstream, StreamShape inputShape, int opFlags) {
            super(upstream, opFlags);
            if (!$assertionsDisabled && upstream.getOutputShape() != inputShape) {
                throw new AssertionError();
            }
        }

        /* access modifiers changed from: package-private */
        public final boolean opIsStateful() {
            return false;
        }
    }

    static abstract class StatefulOp<E_IN> extends LongPipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        /* access modifiers changed from: package-private */
        public abstract <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Long[]> intFunction);

        public /* bridge */ /* synthetic */ LongStream parallel() {
            return (LongStream) LongPipeline.super.parallel();
        }

        public /* bridge */ /* synthetic */ LongStream sequential() {
            return (LongStream) LongPipeline.super.sequential();
        }

        StatefulOp(AbstractPipeline<?, E_IN, ?> upstream, StreamShape inputShape, int opFlags) {
            super(upstream, opFlags);
            if (!$assertionsDisabled && upstream.getOutputShape() != inputShape) {
                throw new AssertionError();
            }
        }

        /* access modifiers changed from: package-private */
        public final boolean opIsStateful() {
            return true;
        }
    }
}
