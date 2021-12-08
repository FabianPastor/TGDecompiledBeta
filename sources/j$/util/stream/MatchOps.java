package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.DoublePredicate;
import j$.util.function.IntConsumer;
import j$.util.function.IntPredicate;
import j$.util.function.LongConsumer;
import j$.util.function.LongPredicate;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import j$.util.stream.Sink;

final class MatchOps {
    private MatchOps() {
    }

    enum MatchKind {
        ANY(true, true),
        ALL(false, false),
        NONE(true, false);
        
        /* access modifiers changed from: private */
        public final boolean shortCircuitResult;
        /* access modifiers changed from: private */
        public final boolean stopOnPredicateMatches;

        private MatchKind(boolean stopOnPredicateMatches2, boolean shortCircuitResult2) {
            this.stopOnPredicateMatches = stopOnPredicateMatches2;
            this.shortCircuitResult = shortCircuitResult2;
        }
    }

    public static <T> TerminalOp<T, Boolean> makeRef(Predicate<? super T> predicate, MatchKind matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new MatchOp(StreamShape.REFERENCE, matchKind, new MatchOps$$ExternalSyntheticLambda3(matchKind, predicate));
    }

    static /* synthetic */ BooleanTerminalSink lambda$makeRef$0(MatchKind matchKind, Predicate predicate) {
        return new BooleanTerminalSink<T>(predicate) {
            final /* synthetic */ Predicate val$predicate;

            {
                this.val$predicate = r2;
            }

            public void accept(T t) {
                if (!this.stop && this.val$predicate.test(t) == MatchKind.this.stopOnPredicateMatches) {
                    this.stop = true;
                    this.value = MatchKind.this.shortCircuitResult;
                }
            }
        };
    }

    public static TerminalOp<Integer, Boolean> makeInt(IntPredicate predicate, MatchKind matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new MatchOp(StreamShape.INT_VALUE, matchKind, new MatchOps$$ExternalSyntheticLambda1(matchKind, predicate));
    }

    static /* synthetic */ BooleanTerminalSink lambda$makeInt$1(MatchKind matchKind, IntPredicate predicate) {
        return new Sink.OfInt(predicate) {
            final /* synthetic */ IntPredicate val$predicate;

            public /* synthetic */ void accept(Integer num) {
                Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
            }

            public /* bridge */ /* synthetic */ void accept(Object obj) {
                accept((Integer) obj);
            }

            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return IntConsumer.CC.$default$andThen(this, intConsumer);
            }

            {
                this.val$predicate = r2;
            }

            public void accept(int t) {
                if (!this.stop && this.val$predicate.test(t) == MatchKind.this.stopOnPredicateMatches) {
                    this.stop = true;
                    this.value = MatchKind.this.shortCircuitResult;
                }
            }
        };
    }

    public static TerminalOp<Long, Boolean> makeLong(LongPredicate predicate, MatchKind matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new MatchOp(StreamShape.LONG_VALUE, matchKind, new MatchOps$$ExternalSyntheticLambda2(matchKind, predicate));
    }

    static /* synthetic */ BooleanTerminalSink lambda$makeLong$2(MatchKind matchKind, LongPredicate predicate) {
        return new Sink.OfLong(predicate) {
            final /* synthetic */ LongPredicate val$predicate;

            public /* synthetic */ void accept(Long l) {
                Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
            }

            public /* bridge */ /* synthetic */ void accept(Object obj) {
                accept((Long) obj);
            }

            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return LongConsumer.CC.$default$andThen(this, longConsumer);
            }

            {
                this.val$predicate = r2;
            }

            public void accept(long t) {
                if (!this.stop && this.val$predicate.test(t) == MatchKind.this.stopOnPredicateMatches) {
                    this.stop = true;
                    this.value = MatchKind.this.shortCircuitResult;
                }
            }
        };
    }

    public static TerminalOp<Double, Boolean> makeDouble(DoublePredicate predicate, MatchKind matchKind) {
        predicate.getClass();
        matchKind.getClass();
        return new MatchOp(StreamShape.DOUBLE_VALUE, matchKind, new MatchOps$$ExternalSyntheticLambda0(matchKind, predicate));
    }

    static /* synthetic */ BooleanTerminalSink lambda$makeDouble$3(MatchKind matchKind, DoublePredicate predicate) {
        return new Sink.OfDouble(predicate) {
            final /* synthetic */ DoublePredicate val$predicate;

            public /* synthetic */ void accept(Double d) {
                Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
            }

            public /* bridge */ /* synthetic */ void accept(Object obj) {
                accept((Double) obj);
            }

            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
            }

            {
                this.val$predicate = r2;
            }

            public void accept(double t) {
                if (!this.stop && this.val$predicate.test(t) == MatchKind.this.stopOnPredicateMatches) {
                    this.stop = true;
                    this.value = MatchKind.this.shortCircuitResult;
                }
            }
        };
    }

    private static final class MatchOp<T> implements TerminalOp<T, Boolean> {
        private final StreamShape inputShape;
        final MatchKind matchKind;
        final Supplier<BooleanTerminalSink<T>> sinkSupplier;

        MatchOp(StreamShape shape, MatchKind matchKind2, Supplier<BooleanTerminalSink<T>> supplier) {
            this.inputShape = shape;
            this.matchKind = matchKind2;
            this.sinkSupplier = supplier;
        }

        public int getOpFlags() {
            return StreamOpFlag.IS_SHORT_CIRCUIT | StreamOpFlag.NOT_ORDERED;
        }

        public StreamShape inputShape() {
            return this.inputShape;
        }

        public <S> Boolean evaluateSequential(PipelineHelper<T> helper, Spliterator<S> spliterator) {
            return Boolean.valueOf(((BooleanTerminalSink) helper.wrapAndCopyInto(this.sinkSupplier.get(), spliterator)).getAndClearState());
        }

        public <S> Boolean evaluateParallel(PipelineHelper<T> helper, Spliterator<S> spliterator) {
            return (Boolean) new MatchTask(this, helper, spliterator).invoke();
        }
    }

    private static abstract class BooleanTerminalSink<T> implements Sink<T> {
        boolean stop;
        boolean value;

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

        public /* synthetic */ void begin(long j) {
            Sink.CC.$default$begin(this, j);
        }

        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        BooleanTerminalSink(MatchKind matchKind) {
            this.value = !matchKind.shortCircuitResult;
        }

        public boolean getAndClearState() {
            return this.value;
        }

        public boolean cancellationRequested() {
            return this.stop;
        }
    }

    private static final class MatchTask<P_IN, P_OUT> extends AbstractShortCircuitTask<P_IN, P_OUT, Boolean, MatchTask<P_IN, P_OUT>> {
        private final MatchOp<P_OUT> op;

        MatchTask(MatchOp<P_OUT> op2, PipelineHelper<P_OUT> helper, Spliterator<P_IN> spliterator) {
            super(helper, spliterator);
            this.op = op2;
        }

        MatchTask(MatchTask<P_IN, P_OUT> parent, Spliterator<P_IN> spliterator) {
            super(parent, spliterator);
            this.op = parent.op;
        }

        /* access modifiers changed from: protected */
        public MatchTask<P_IN, P_OUT> makeChild(Spliterator<P_IN> spliterator) {
            return new MatchTask<>(this, spliterator);
        }

        /* access modifiers changed from: protected */
        public Boolean doLeaf() {
            boolean b = ((BooleanTerminalSink) this.helper.wrapAndCopyInto(this.op.sinkSupplier.get(), this.spliterator)).getAndClearState();
            if (b != this.op.matchKind.shortCircuitResult) {
                return null;
            }
            shortCircuit(Boolean.valueOf(b));
            return null;
        }

        /* access modifiers changed from: protected */
        public Boolean getEmptyResult() {
            return Boolean.valueOf(!this.op.matchKind.shortCircuitResult);
        }
    }
}
