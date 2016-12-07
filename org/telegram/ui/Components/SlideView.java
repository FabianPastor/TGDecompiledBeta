package org.telegram.ui.Components;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SlideView extends LinearLayout {
    public SlideView(Context context) {
        super(context);
    }

    public String getHeaderName() {
        return "";
    }

    public void setParams(Bundle params) {
    }

    public void onBackPressed() {
    }

    public void onShow() {
    }

    public void onDestroyActivity() {
    }

    public void onNextPressed() {
    }

    public void saveStateParams(Bundle bundle) {
    }

    public void restoreStateParams(Bundle bundle) {
    }

    public boolean needBackButton() {
        return false;
    }
}
