package org.telegram.ui.Components;

import android.text.SpannableString;
import java.lang.reflect.Field;
import org.telegram.messenger.FileLog;

public class SpannableStringLight extends SpannableString {
    private static boolean fieldsAvailable;
    private static Field mSpanCountField;
    private static Field mSpanDataField;
    private static Field mSpansField;
    private int mSpanCountOverride;
    private int[] mSpanDataOverride;
    private Object[] mSpansOverride;
    private int num;

    public SpannableStringLight(CharSequence charSequence) {
        super(charSequence);
        try {
            this.mSpansOverride = (Object[]) mSpansField.get(this);
            this.mSpanDataOverride = (int[]) mSpanDataField.get(this);
            this.mSpanCountOverride = ((Integer) mSpanCountField.get(this)).intValue();
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void setSpansCount(int i) {
        int i2 = this.mSpanCountOverride;
        i += i2;
        this.mSpansOverride = new Object[i];
        this.mSpanDataOverride = new int[(i * 3)];
        this.num = i2;
        this.mSpanCountOverride = i;
        try {
            mSpansField.set(this, this.mSpansOverride);
            mSpanDataField.set(this, this.mSpanDataOverride);
            mSpanCountField.set(this, Integer.valueOf(this.mSpanCountOverride));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public static boolean isFieldsAvailable() {
        if (!fieldsAvailable && mSpansField == null) {
            try {
                mSpansField = SpannableString.class.getSuperclass().getDeclaredField("mSpans");
                mSpansField.setAccessible(true);
                mSpanDataField = SpannableString.class.getSuperclass().getDeclaredField("mSpanData");
                mSpanDataField.setAccessible(true);
                mSpanCountField = SpannableString.class.getSuperclass().getDeclaredField("mSpanCount");
                mSpanCountField.setAccessible(true);
            } catch (Throwable th) {
                FileLog.e(th);
            }
            fieldsAvailable = true;
        }
        if (mSpansField != null) {
            return true;
        }
        return false;
    }

    public void setSpanLight(Object obj, int i, int i2, int i3) {
        Object[] objArr = this.mSpansOverride;
        int i4 = this.num;
        objArr[i4] = obj;
        int[] iArr = this.mSpanDataOverride;
        iArr[i4 * 3] = i;
        iArr[(i4 * 3) + 1] = i2;
        iArr[(i4 * 3) + 2] = i3;
        this.num = i4 + 1;
    }

    public void removeSpan(Object obj) {
        super.removeSpan(obj);
    }
}
