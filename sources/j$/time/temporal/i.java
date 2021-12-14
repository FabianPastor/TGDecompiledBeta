package j$.time.temporal;

import j$.time.b;

enum i implements m {
    WEEK_BASED_YEARS("WeekBasedYears", b.c(31556952)),
    QUARTER_YEARS("QuarterYears", b.c(7889238));
    
    private final String a;

    private i(String str, b bVar) {
        this.a = str;
    }

    public String toString() {
        return this.a;
    }
}
