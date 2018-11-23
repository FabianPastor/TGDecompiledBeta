package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.CountrySelectActivity.CountrySearchAdapter;

/* renamed from: org.telegram.ui.CountrySelectActivity$CountrySearchAdapter$$Lambda$1 */
final /* synthetic */ class CountrySelectActivity$CountrySearchAdapter$$Lambda$1 implements Runnable {
    private final CountrySearchAdapter arg$1;
    private final ArrayList arg$2;

    CountrySelectActivity$CountrySearchAdapter$$Lambda$1(CountrySearchAdapter countrySearchAdapter, ArrayList arrayList) {
        this.arg$1 = countrySearchAdapter;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.mo16883xa883bb23(this.arg$2);
    }
}
