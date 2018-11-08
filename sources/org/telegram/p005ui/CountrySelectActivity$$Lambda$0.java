package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.CountrySelectActivity$$Lambda$0 */
final /* synthetic */ class CountrySelectActivity$$Lambda$0 implements OnItemClickListener {
    private final CountrySelectActivity arg$1;

    CountrySelectActivity$$Lambda$0(CountrySelectActivity countrySelectActivity) {
        this.arg$1 = countrySelectActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$CountrySelectActivity(view, i);
    }
}
