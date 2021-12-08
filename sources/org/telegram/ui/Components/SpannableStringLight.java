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

    public SpannableStringLight(CharSequence source) {
        super(source);
        try {
            this.mSpansOverride = (Object[]) mSpansField.get(this);
            this.mSpanDataOverride = (int[]) mSpanDataField.get(this);
            this.mSpanCountOverride = ((Integer) mSpanCountField.get(this)).intValue();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void setSpansCount(int count) {
        int i = this.mSpanCountOverride;
        int count2 = count + i;
        Object[] objArr = new Object[count2];
        this.mSpansOverride = objArr;
        this.mSpanDataOverride = new int[(count2 * 3)];
        this.num = i;
        this.mSpanCountOverride = count2;
        try {
            mSpansField.set(this, objArr);
            mSpanDataField.set(this, this.mSpanDataOverride);
            mSpanCountField.set(this, Integer.valueOf(this.mSpanCountOverride));
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static boolean isFieldsAvailable() {
        if (!fieldsAvailable && mSpansField == null) {
            try {
                Field declaredField = SpannableString.class.getSuperclass().getDeclaredField("mSpans");
                mSpansField = declaredField;
                declaredField.setAccessible(true);
                Field declaredField2 = SpannableString.class.getSuperclass().getDeclaredField("mSpanData");
                mSpanDataField = declaredField2;
                declaredField2.setAccessible(true);
                Field declaredField3 = SpannableString.class.getSuperclass().getDeclaredField("mSpanCount");
                mSpanCountField = declaredField3;
                declaredField3.setAccessible(true);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            fieldsAvailable = true;
        }
        if (mSpansField != null) {
            return true;
        }
        return false;
    }

    public void setSpanLight(Object what, int start, int end, int flags) {
        Object[] objArr = this.mSpansOverride;
        int i = this.num;
        objArr[i] = what;
        int[] iArr = this.mSpanDataOverride;
        iArr[i * 3] = start;
        iArr[(i * 3) + 1] = end;
        iArr[(i * 3) + 2] = flags;
        this.num = i + 1;
    }

    public void removeSpan(Object what) {
        super.removeSpan(what);
    }
}
