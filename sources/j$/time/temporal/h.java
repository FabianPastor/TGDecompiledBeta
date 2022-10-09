package j$.time.temporal;
/* loaded from: classes2.dex */
enum h implements k {
    DAY_OF_QUARTER { // from class: j$.time.temporal.d
        @Override // j$.time.temporal.k
        public n a() {
            return n.d(1L, 90L, 92L);
        }

        @Override // java.lang.Enum
        public String toString() {
            return "DayOfQuarter";
        }
    },
    QUARTER_OF_YEAR { // from class: j$.time.temporal.e
        @Override // j$.time.temporal.k
        public n a() {
            return n.c(1L, 4L);
        }

        @Override // java.lang.Enum
        public String toString() {
            return "QuarterOfYear";
        }
    },
    WEEK_OF_WEEK_BASED_YEAR { // from class: j$.time.temporal.f
        @Override // j$.time.temporal.k
        public n a() {
            return n.d(1L, 52L, 53L);
        }

        @Override // java.lang.Enum
        public String toString() {
            return "WeekOfWeekBasedYear";
        }
    },
    WEEK_BASED_YEAR { // from class: j$.time.temporal.g
        @Override // j$.time.temporal.k
        public n a() {
            return a.YEAR.a();
        }

        @Override // java.lang.Enum
        public String toString() {
            return "WeekBasedYear";
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    h(c cVar) {
    }
}
