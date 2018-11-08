package org.telegram.p005ui;

import java.util.Comparator;
import org.telegram.p005ui.CountrySelectActivity.Country;

/* renamed from: org.telegram.ui.CountrySelectActivity$CountryAdapter$$Lambda$1 */
final /* synthetic */ class CountrySelectActivity$CountryAdapter$$Lambda$1 implements Comparator {
    static final Comparator $instance = new CountrySelectActivity$CountryAdapter$$Lambda$1();

    private CountrySelectActivity$CountryAdapter$$Lambda$1() {
    }

    public int compare(Object obj, Object obj2) {
        return ((Country) obj).name.compareTo(((Country) obj2).name);
    }
}
