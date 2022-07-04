package j$.util.stream;

import j$.util.IntSummaryStatistics;
import j$.util.OptionalDouble;
import j$.util.OptionalInt;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.IntBinaryOperator;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.IntPredicate;
import j$.util.function.IntToDoubleFunction;
import j$.util.function.IntToLongFunction;
import j$.util.function.IntUnaryOperator;
import j$.util.function.ObjIntConsumer;
import j$.util.function.Supplier;
import j$.util.stream.DoublePipeline;
import j$.util.stream.LongPipeline;
import j$.util.stream.MatchOps;
import j$.util.stream.Node;
import j$.util.stream.ReferencePipeline;
import j$.util.stream.Sink;
import j$.util.stream.StreamSpliterators;

abstract class IntPipeline<E_IN> extends AbstractPipeline<E_IN, Integer, IntStream> implements IntStream {
    public /* bridge */ /* synthetic */ IntStream parallel() {
        return (IntStream) super.parallel();
    }

    public /* bridge */ /* synthetic */ IntStream sequential() {
        return (IntStream) super.sequential();
    }

    IntPipeline(Supplier<? extends Spliterator<Integer>> supplier, int sourceFlags, boolean parallel) {
        super((Supplier<? extends Spliterator<?>>) supplier, sourceFlags, parallel);
    }

    IntPipeline(Spliterator<Integer> spliterator, int sourceFlags, boolean parallel) {
        super((Spliterator<?>) spliterator, sourceFlags, parallel);
    }

    IntPipeline(AbstractPipeline<?, E_IN, ?> upstream, int opFlags) {
        super(upstream, opFlags);
    }

    private static IntConsumer adapt(Sink<Integer> sink) {
        if (sink instanceof IntConsumer) {
            return (IntConsumer) sink;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using IntStream.adapt(Sink<Integer> s)");
        }
        sink.getClass();
        return new IntPipeline$$ExternalSyntheticLambda6(sink);
    }

    /* access modifiers changed from: private */
    public static Spliterator.OfInt adapt(Spliterator<Integer> spliterator) {
        if (spliterator instanceof Spliterator.OfInt) {
            return (Spliterator.OfInt) spliterator;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using IntStream.adapt(Spliterator<Integer> s)");
        }
        throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
    }

    /* access modifiers changed from: package-private */
    public final StreamShape getOutputShape() {
        return StreamShape.INT_VALUE;
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Node<Integer> evaluateToNode(PipelineHelper<Integer> helper, Spliterator<P_IN> spliterator, boolean flattenTree, IntFunction<Integer[]> intFunction) {
        return Nodes.collectInt(helper, spliterator, flattenTree);
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Spliterator<Integer> wrap(PipelineHelper<Integer> ph, Supplier<Spliterator<P_IN>> supplier, boolean isParallel) {
        return new StreamSpliterators.IntWrappingSpliterator(ph, supplier, isParallel);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator.OfInt lazySpliterator(Supplier<? extends Spliterator<Integer>> supplier) {
        return new StreamSpliterators.DelegatingSpliterator.OfInt(supplier);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:1:0x0008 A[LOOP:0: B:1:0x0008->B:4:0x0012, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void forEachWithCancel(j$.util.Spliterator<java.lang.Integer> r4, j$.util.stream.Sink<java.lang.Integer> r5) {
        /*
            r3 = this;
            j$.util.Spliterator$OfInt r0 = adapt((j$.util.Spliterator<java.lang.Integer>) r4)
            j$.util.function.IntConsumer r1 = adapt((j$.util.stream.Sink<java.lang.Integer>) r5)
        L_0x0008:
            boolean r2 = r5.cancellationRequested()
            if (r2 != 0) goto L_0x0014
            boolean r2 = r0.tryAdvance((j$.util.function.IntConsumer) r1)
            if (r2 != 0) goto L_0x0008
        L_0x0014:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.IntPipeline.forEachWithCancel(j$.util.Spliterator, j$.util.stream.Sink):void");
    }

    /* access modifiers changed from: package-private */
    public final Node.Builder<Integer> makeNodeBuilder(long exactSizeIfKnown, IntFunction<Integer[]> intFunction) {
        return Nodes.intBuilder(exactSizeIfKnown);
    }

    public final PrimitiveIterator.OfInt iterator() {
        return Spliterators.iterator(spliterator());
    }

    public final Spliterator.OfInt spliterator() {
        return adapt((Spliterator<Integer>) super.spliterator());
    }

    public final LongStream asLongStream() {
        return new LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Integer> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedInt<Long>(sink) {
                    public void accept(int t) {
                        this.downstream.accept((long) t);
                    }
                };
            }
        };
    }

    public final DoubleStream asDoubleStream() {
        return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Integer> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedInt<Double>(sink) {
                    public void accept(int t) {
                        this.downstream.accept((double) t);
                    }
                };
            }
        };
    }

    public final Stream<Integer> boxed() {
        return mapToObj(IntPipeline$$ExternalSyntheticLambda7.INSTANCE);
    }

    public final IntStream map(IntUnaryOperator mapper) {
        mapper.getClass();
        final IntUnaryOperator intUnaryOperator = mapper;
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) {
                    public void accept(int t) {
                        this.downstream.accept(intUnaryOperator.applyAsInt(t));
                    }
                };
            }
        };
    }

    public final <U> Stream<U> mapToObj(IntFunction<? extends U> intFunction) {
        intFunction.getClass();
        final IntFunction<? extends U> intFunction2 = intFunction;
        return new ReferencePipeline.StatelessOp<Integer, U>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Integer> opWrapSink(int flags, Sink<U> sink) {
                return new Sink.ChainedInt<U>(sink) {
                    public void accept(int t) {
                        this.downstream.accept(intFunction2.apply(t));
                    }
                };
            }
        };
    }

    public final LongStream mapToLong(IntToLongFunction mapper) {
        mapper.getClass();
        final IntToLongFunction intToLongFunction = mapper;
        return new LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Integer> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedInt<Long>(sink) {
                    public void accept(int t) {
                        this.downstream.accept(intToLongFunction.applyAsLong(t));
                    }
                };
            }
        };
    }

    public final DoubleStream mapToDouble(IntToDoubleFunction mapper) {
        mapper.getClass();
        final IntToDoubleFunction intToDoubleFunction = mapper;
        return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Integer> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedInt<Double>(sink) {
                    public void accept(int t) {
                        this.downstream.accept(intToDoubleFunction.applyAsDouble(t));
                    }
                };
            }
        };
    }

    public final IntStream flatMap(IntFunction<? extends IntStream> intFunction) {
        final IntFunction<? extends IntStream> intFunction2 = intFunction;
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) {
                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(int t) {
                        IntStream result = (IntStream) intFunction2.apply(t);
                        if (result != null) {
                            try {
                                result.sequential().forEach(new IntPipeline$7$1$$ExternalSyntheticLambda0(this));
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

                    /* renamed from: lambda$accept$0$java-util-stream-IntPipeline$7$1  reason: not valid java name */
                    public /* synthetic */ void m522lambda$accept$0$javautilstreamIntPipeline$7$1(int i) {
                        this.downstream.accept(i);
                    }
                };
            }
        };
    }

    public IntStream unordered() {
        if (!isOrdered()) {
            return this;
        }
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_ORDERED) {
            /* access modifiers changed from: package-private */
            public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return sink;
            }
        };
    }

    public final IntStream filter(IntPredicate predicate) {
        predicate.getClass();
        final IntPredicate intPredicate = predicate;
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) {
                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(int t) {
                        if (intPredicate.test(t)) {
                            this.downstream.accept(t);
                        }
                    }
                };
            }
        };
    }

    public final IntStream peek(IntConsumer action) {
        action.getClass();
        final IntConsumer intConsumer = action;
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, 0) {
            /* access modifiers changed from: package-private */
            public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) {
                    public void accept(int t) {
                        intConsumer.accept(t);
                        this.downstream.accept(t);
                    }
                };
            }
        };
    }

    public final IntStream limit(long maxSize) {
        if (maxSize >= 0) {
            return SliceOps.makeInt(this, 0, maxSize);
        }
        throw new IllegalArgumentException(Long.toString(maxSize));
    }

    public final IntStream skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        } else if (n == 0) {
            return this;
        } else {
            return SliceOps.makeInt(this, n, -1);
        }
    }

    public final IntStream sorted() {
        return SortedOps.makeInt(this);
    }

    public final IntStream distinct() {
        return boxed().distinct().mapToInt(IntPipeline$$ExternalSyntheticLambda13.INSTANCE);
    }

    public void forEach(IntConsumer action) {
        evaluate(ForEachOps.makeInt(action, false));
    }

    public void forEachOrdered(IntConsumer action) {
        evaluate(ForEachOps.makeInt(action, true));
    }

    public final int sum() {
        return reduce(0, IntPipeline$$ExternalSyntheticLambda3.INSTANCE);
    }

    public final OptionalInt min() {
        return reduce(IntPipeline$$ExternalSyntheticLambda5.INSTANCE);
    }

    public final OptionalInt max() {
        return reduce(IntPipeline$$ExternalSyntheticLambda4.INSTANCE);
    }

    static /* synthetic */ long lambda$count$1(int e) {
        return 1;
    }

    public final long count() {
        return mapToLong(IntPipeline$$ExternalSyntheticLambda9.INSTANCE).sum();
    }

    static /* synthetic */ long[] lambda$average$2() {
        return new long[2];
    }

    public final OptionalDouble average() {
        long[] avg = (long[]) collect(IntPipeline$$ExternalSyntheticLambda12.INSTANCE, IntPipeline$$ExternalSyntheticLambda11.INSTANCE, IntPipeline$$ExternalSyntheticLambda1.INSTANCE);
        if (avg[0] <= 0) {
            return OptionalDouble.empty();
        }
        double d = (double) avg[1];
        double d2 = (double) avg[0];
        Double.isNaN(d);
        Double.isNaN(d2);
        return OptionalDouble.of(d / d2);
    }

    static /* synthetic */ void lambda$average$3(long[] ll, int i) {
        ll[0] = ll[0] + 1;
        ll[1] = ll[1] + ((long) i);
    }

    static /* synthetic */ void lambda$average$4(long[] ll, long[] rr) {
        ll[0] = ll[0] + rr[0];
        ll[1] = ll[1] + rr[1];
    }

    public final IntSummaryStatistics summaryStatistics() {
        return (IntSummaryStatistics) collect(Collectors$$ExternalSyntheticLambda81.INSTANCE, IntPipeline$$ExternalSyntheticLambda10.INSTANCE, IntPipeline$$ExternalSyntheticLambda0.INSTANCE);
    }

    public final int reduce(int identity, IntBinaryOperator op) {
        return ((Integer) evaluate(ReduceOps.makeInt(identity, op))).intValue();
    }

    public final OptionalInt reduce(IntBinaryOperator op) {
        return (OptionalInt) evaluate(ReduceOps.makeInt(op));
    }

    public final <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> objIntConsumer, BiConsumer<R, R> biConsumer) {
        return evaluate(ReduceOps.makeInt(supplier, objIntConsumer, new IntPipeline$$ExternalSyntheticLambda2(biConsumer)));
    }

    public final boolean anyMatch(IntPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeInt(predicate, MatchOps.MatchKind.ANY))).booleanValue();
    }

    public final boolean allMatch(IntPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeInt(predicate, MatchOps.MatchKind.ALL))).booleanValue();
    }

    public final boolean noneMatch(IntPredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeInt(predicate, MatchOps.MatchKind.NONE))).booleanValue();
    }

    public final OptionalInt findFirst() {
        return (OptionalInt) evaluate(FindOps.makeInt(true));
    }

    public final OptionalInt findAny() {
        return (OptionalInt) evaluate(FindOps.makeInt(false));
    }

    static /* synthetic */ Integer[] lambda$toArray$6(int x$0) {
        return new Integer[x$0];
    }

    public final int[] toArray() {
        return (int[]) Nodes.flattenInt((Node.OfInt) evaluateToArrayNode(IntPipeline$$ExternalSyntheticLambda8.INSTANCE)).asPrimitiveArray();
    }

    static class Head<E_IN> extends IntPipeline<E_IN> {
        public /* bridge */ /* synthetic */ IntStream parallel() {
            return (IntStream) IntPipeline.super.parallel();
        }

        public /* bridge */ /* synthetic */ IntStream sequential() {
            return (IntStream) IntPipeline.super.sequential();
        }

        Head(Supplier<? extends Spliterator<Integer>> supplier, int sourceFlags, boolean parallel) {
            super(supplier, sourceFlags, parallel);
        }

        Head(Spliterator<Integer> spliterator, int sourceFlags, boolean parallel) {
            super(spliterator, sourceFlags, parallel);
        }

        /* access modifiers changed from: package-private */
        public final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        /* access modifiers changed from: package-private */
        public final Sink<E_IN> opWrapSink(int flags, Sink<Integer> sink) {
            throw new UnsupportedOperationException();
        }

        public void forEach(IntConsumer action) {
            if (!isParallel()) {
                IntPipeline.adapt((Spliterator<Integer>) sourceStageSpliterator()).forEachRemaining(action);
            } else {
                IntPipeline.super.forEach(action);
            }
        }

        public void forEachOrdered(IntConsumer action) {
            if (!isParallel()) {
                IntPipeline.adapt((Spliterator<Integer>) sourceStageSpliterator()).forEachRemaining(action);
            } else {
                IntPipeline.super.forEachOrdered(action);
            }
        }
    }

    static abstract class StatelessOp<E_IN> extends IntPipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        public /* bridge */ /* synthetic */ IntStream parallel() {
            return (IntStream) IntPipeline.super.parallel();
        }

        public /* bridge */ /* synthetic */ IntStream sequential() {
            return (IntStream) IntPipeline.super.sequential();
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

    static abstract class StatefulOp<E_IN> extends IntPipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        /* access modifiers changed from: package-private */
        public abstract <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Integer[]> intFunction);

        public /* bridge */ /* synthetic */ IntStream parallel() {
            return (IntStream) IntPipeline.super.parallel();
        }

        public /* bridge */ /* synthetic */ IntStream sequential() {
            return (IntStream) IntPipeline.super.sequential();
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
