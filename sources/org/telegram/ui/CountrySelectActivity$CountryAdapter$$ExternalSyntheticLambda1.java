package org.telegram.ui;

import java.util.Comparator;
import org.telegram.ui.CountrySelectActivity;

public final /* synthetic */ class CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda1 INSTANCE = new CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda1();

    private /* synthetic */ CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda1() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((CountrySelectActivity.Country) obj).name.compareTo(((CountrySelectActivity.Country) obj2).name);
    }
}
