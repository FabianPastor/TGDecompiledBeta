package android.support.v4.print;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;

class PrintHelperApi23 extends PrintHelperApi20 {
    protected Builder copyAttributes(PrintAttributes other) {
        Builder b = super.copyAttributes(other);
        if (other.getDuplexMode() != 0) {
            b.setDuplexMode(other.getDuplexMode());
        }
        return b;
    }

    PrintHelperApi23(Context context) {
        super(context);
        this.mIsMinMarginsHandlingCorrect = false;
    }
}
