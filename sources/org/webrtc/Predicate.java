package org.webrtc;

public interface Predicate<T> {
    Predicate<T> and(Predicate<? super T> predicate);

    Predicate<T> negate();

    Predicate<T> or(Predicate<? super T> predicate);

    boolean test(T t);

    /* renamed from: org.webrtc.Predicate$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Predicate $default$or(Predicate _this, Predicate other) {
            return new Predicate<T>(other) {
                final /* synthetic */ Predicate val$other;

                public /* synthetic */ Predicate and(Predicate predicate) {
                    return CC.$default$and(this, predicate);
                }

                public /* synthetic */ Predicate negate() {
                    return CC.$default$negate(this);
                }

                public /* synthetic */ Predicate or(Predicate predicate) {
                    return CC.$default$or(this, predicate);
                }

                {
                    this.val$other = r2;
                }

                public boolean test(T arg) {
                    return Predicate.this.test(arg) || this.val$other.test(arg);
                }
            };
        }

        public static Predicate $default$and(Predicate _this, Predicate other) {
            return new Predicate<T>(other) {
                final /* synthetic */ Predicate val$other;

                public /* synthetic */ Predicate and(Predicate predicate) {
                    return CC.$default$and(this, predicate);
                }

                public /* synthetic */ Predicate negate() {
                    return CC.$default$negate(this);
                }

                public /* synthetic */ Predicate or(Predicate predicate) {
                    return CC.$default$or(this, predicate);
                }

                {
                    this.val$other = r2;
                }

                public boolean test(T arg) {
                    return Predicate.this.test(arg) && this.val$other.test(arg);
                }
            };
        }

        public static Predicate $default$negate(Predicate _this) {
            return new Predicate<T>() {
                public /* synthetic */ Predicate and(Predicate predicate) {
                    return CC.$default$and(this, predicate);
                }

                public /* synthetic */ Predicate negate() {
                    return CC.$default$negate(this);
                }

                public /* synthetic */ Predicate or(Predicate predicate) {
                    return CC.$default$or(this, predicate);
                }

                public boolean test(T arg) {
                    return !Predicate.this.test(arg);
                }
            };
        }
    }
}
