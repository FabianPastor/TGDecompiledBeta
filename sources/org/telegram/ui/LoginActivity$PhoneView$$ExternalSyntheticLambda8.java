package org.telegram.ui;

import j$.util.function.Function;
import org.telegram.ui.CountrySelectActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda8 implements Function {
    public static final /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda8 INSTANCE = new LoginActivity$PhoneView$$ExternalSyntheticLambda8();

    private /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda8() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return ((CountrySelectActivity.Country) obj).name;
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
