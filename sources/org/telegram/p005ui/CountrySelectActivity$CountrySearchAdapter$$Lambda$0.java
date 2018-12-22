package org.telegram.p005ui;

import org.telegram.p005ui.CountrySelectActivity.CountrySearchAdapter;

/* renamed from: org.telegram.ui.CountrySelectActivity$CountrySearchAdapter$$Lambda$0 */
final /* synthetic */ class CountrySelectActivity$CountrySearchAdapter$$Lambda$0 implements Runnable {
    private final CountrySearchAdapter arg$1;
    private final String arg$2;

    CountrySelectActivity$CountrySearchAdapter$$Lambda$0(CountrySearchAdapter countrySearchAdapter, String str) {
        this.arg$1 = countrySearchAdapter;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.mo18274xa1825eb2(this.arg$2);
    }
}
