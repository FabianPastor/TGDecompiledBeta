package j$.util.stream;

/* renamed from: j$.util.stream.c3  reason: case insensitive filesystem */
enum CLASSNAMEc3 {
    ANY(true, true),
    ALL(false, false),
    NONE(true, false);
    
    /* access modifiers changed from: private */
    public final boolean a;
    /* access modifiers changed from: private */
    public final boolean b;

    private CLASSNAMEc3(boolean stopOnPredicateMatches, boolean shortCircuitResult) {
        this.a = stopOnPredicateMatches;
        this.b = shortCircuitResult;
    }
}
