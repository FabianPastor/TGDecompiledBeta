package org.telegram.ui;

import j$.util.function.Predicate;

public final /* synthetic */ class RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda3 implements Predicate {
    public final /* synthetic */ String f$0;

    public /* synthetic */ RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda3(String str) {
        this.f$0 = str;
    }

    public /* synthetic */ Predicate and(Predicate predicate) {
        return Predicate.CC.$default$and(this, predicate);
    }

    public /* synthetic */ Predicate negate() {
        return Predicate.CC.$default$negate(this);
    }

    public /* synthetic */ Predicate or(Predicate predicate) {
        return Predicate.CC.$default$or(this, predicate);
    }

    public final boolean test(Object obj) {
        return RestrictedLanguagesSelectActivity.lambda$createView$0(this.f$0, (String) obj);
    }
}
