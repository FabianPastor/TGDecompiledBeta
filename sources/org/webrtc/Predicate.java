package org.webrtc;

public interface Predicate<T> {
    Predicate<T> and(Predicate<? super T> predicate);

    Predicate<T> negate();

    Predicate<T> or(Predicate<? super T> predicate);

    boolean test(T t);

    /* renamed from: org.webrtc.Predicate$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Predicate $default$or(Predicate _this, Predicate predicate) {
            return new Predicate<T>(predicate) {
                final /* synthetic */ Predicate val$other;

                public /* synthetic */ Predicate<T> and(Predicate<? super T> predicate) {
                    return CC.$default$and(this, predicate);
                }

                public /* synthetic */ Predicate<T> negate() {
                    return CC.$default$negate(this);
                }

                public /* synthetic */ Predicate<T> or(Predicate<? super T> predicate) {
                    return CC.$default$or(this, predicate);
                }

                {
                    this.val$other = r2;
                }

                public boolean test(T t) {
                    return Predicate.this.test(t) || this.val$other.test(t);
                }
            };
        }

        public static Predicate $default$and(Predicate _this, Predicate predicate) {
            return new Predicate<T>(predicate) {
                final /* synthetic */ Predicate val$other;

                public /* synthetic */ Predicate<T> and(Predicate<? super T> predicate) {
                    return CC.$default$and(this, predicate);
                }

                public /* synthetic */ Predicate<T> negate() {
                    return CC.$default$negate(this);
                }

                public /* synthetic */ Predicate<T> or(Predicate<? super T> predicate) {
                    return CC.$default$or(this, predicate);
                }

                {
                    this.val$other = r2;
                }

                public boolean test(T t) {
                    return Predicate.this.test(t) && this.val$other.test(t);
                }
            };
        }

        public static Predicate $default$negate(Predicate _this) {
            return new Predicate<T>() {
                public /* synthetic */ Predicate<T> and(Predicate<? super T> predicate) {
                    return CC.$default$and(this, predicate);
                }

                public /* synthetic */ Predicate<T> negate() {
                    return CC.$default$negate(this);
                }

                public /* synthetic */ Predicate<T> or(Predicate<? super T> predicate) {
                    return CC.$default$or(this, predicate);
                }

                public boolean test(T t) {
                    return !Predicate.this.test(t);
                }
            };
        }
    }
}
