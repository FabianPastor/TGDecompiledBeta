package org.telegram.ui.Components;

import android.text.InputFilter;
import android.text.Spanned;

public class CodepointsLengthInputFilter implements InputFilter {
    private final int mMax;

    public CodepointsLengthInputFilter(int max) {
        this.mMax = max;
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int keep = this.mMax - (Character.codePointCount(dest, 0, dest.length()) - Character.codePointCount(dest, dstart, dend));
        if (keep <= 0) {
            return "";
        }
        if (keep >= Character.codePointCount(source, start, end)) {
            return null;
        }
        int keep2 = keep + start;
        if (!Character.isHighSurrogate(source.charAt(keep2 - 1)) || keep2 - 1 != start) {
            return source.subSequence(start, keep2);
        }
        return "";
    }

    public int getMax() {
        return this.mMax;
    }
}
