package android.support.v4.print;

import android.content.Context;

class PrintHelperApi20 extends PrintHelperKitkat {
    PrintHelperApi20(Context context) {
        super(context);
        this.mPrintActivityRespectsOrientation = false;
    }
}
