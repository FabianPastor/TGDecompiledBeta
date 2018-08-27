package org.telegram.ui;

import java.util.Comparator;
import org.telegram.ui.CountrySelectActivity.Country;

final /* synthetic */ class CountrySelectActivity$CountryAdapter$$Lambda$1 implements Comparator {
    static final Comparator $instance = new CountrySelectActivity$CountryAdapter$$Lambda$1();

    private CountrySelectActivity$CountryAdapter$$Lambda$1() {
    }

    public int compare(Object obj, Object obj2) {
        return ((Country) obj).name.compareTo(((Country) obj2).name);
    }
}
