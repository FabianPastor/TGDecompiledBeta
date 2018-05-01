package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class EditTextCaption extends EditTextBoldCursor {
    private String caption;
    private StaticLayout captionLayout;
    private boolean copyPasteShowed;
    private int hintColor;
    private int triesCount = null;
    private int userNameLength;
    private int xOffset;
    private int yOffset;

    public EditTextCaption(Context context) {
        super(context);
    }

    public void setCaption(String str) {
        if (!((this.caption == null || this.caption.length() == 0) && (str == null || str.length() == 0))) {
            if (this.caption == null || str == null || !this.caption.equals(str)) {
                this.caption = str;
                if (this.caption != null) {
                    this.caption = this.caption.replace('\n', ' ');
                }
                requestLayout();
            }
        }
    }

    private void makeSelectedBold() {
        applyTextStyleToSelection(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")));
    }

    private void makeSelectedItalic() {
        applyTextStyleToSelection(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")));
    }

    private void makeSelectedRegular() {
        applyTextStyleToSelection(null);
    }

    private void applyTextStyleToSelection(TypefaceSpan typefaceSpan) {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        Editable text = getText();
        CharacterStyle[] characterStyleArr = (CharacterStyle[]) text.getSpans(selectionStart, selectionEnd, CharacterStyle.class);
        if (characterStyleArr != null && characterStyleArr.length > 0) {
            for (Object obj : characterStyleArr) {
                int spanStart = text.getSpanStart(obj);
                int spanEnd = text.getSpanEnd(obj);
                text.removeSpan(obj);
                if (spanStart < selectionStart) {
                    text.setSpan(obj, spanStart, selectionStart, 33);
                }
                if (spanEnd > selectionEnd) {
                    text.setSpan(obj, selectionEnd, spanEnd, 33);
                }
            }
        }
        if (typefaceSpan != null) {
            text.setSpan(typefaceSpan, selectionStart, selectionEnd, 33);
        }
    }

    public void onWindowFocusChanged(boolean z) {
        if (VERSION.SDK_INT >= 23 || z || !this.copyPasteShowed) {
            super.onWindowFocusChanged(z);
        }
    }

    private Callback overrideCallback(final Callback callback) {
        return new Callback() {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                EditTextCaption.this.copyPasteShowed = true;
                return callback.onCreateActionMode(actionMode, menu);
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return callback.onPrepareActionMode(actionMode, menu);
            }

            public boolean onActionItemClicked(android.view.ActionMode r4, android.view.MenuItem r5) {
                /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                /*
                r3 = this;
                r0 = r5.getItemId();
                r1 = 1;
                r2 = NUM; // 0x7f080054 float:1.8077671E38 double:1.0529679236E-314;
                if (r0 != r2) goto L_0x0013;
            L_0x000a:
                r5 = org.telegram.ui.Components.EditTextCaption.this;
                r5.makeSelectedRegular();
                r4.finish();
                return r1;
            L_0x0013:
                r0 = r5.getItemId();
                r2 = NUM; // 0x7f080051 float:1.8077665E38 double:1.052967922E-314;
                if (r0 != r2) goto L_0x0025;
            L_0x001c:
                r5 = org.telegram.ui.Components.EditTextCaption.this;
                r5.makeSelectedBold();
                r4.finish();
                return r1;
            L_0x0025:
                r0 = r5.getItemId();
                r2 = NUM; // 0x7f080053 float:1.807767E38 double:1.052967923E-314;
                if (r0 != r2) goto L_0x0037;
            L_0x002e:
                r5 = org.telegram.ui.Components.EditTextCaption.this;
                r5.makeSelectedItalic();
                r4.finish();
                return r1;
            L_0x0037:
                r0 = r2;	 Catch:{ Exception -> 0x003e }
                r4 = r0.onActionItemClicked(r4, r5);	 Catch:{ Exception -> 0x003e }
                return r4;
            L_0x003e:
                return r1;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EditTextCaption.1.onActionItemClicked(android.view.ActionMode, android.view.MenuItem):boolean");
            }

            public void onDestroyActionMode(ActionMode actionMode) {
                EditTextCaption.this.copyPasteShowed = false;
                callback.onDestroyActionMode(actionMode);
            }
        };
    }

    public ActionMode startActionMode(Callback callback, int i) {
        return super.startActionMode(overrideCallback(callback), i);
    }

    public ActionMode startActionMode(Callback callback) {
        return super.startActionMode(overrideCallback(callback));
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int i, int i2) {
        try {
            super.onMeasure(i, i2);
        } catch (Throwable e) {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(51.0f));
            FileLog.m3e(e);
        }
        this.captionLayout = 0;
        if (this.caption != 0 && this.caption.length() > 0) {
            i = getText();
            if (i.length() > 1 && i.charAt(0) == '@') {
                int indexOf = TextUtils.indexOf(i, ' ');
                if (indexOf != -1) {
                    TextPaint paint = getPaint();
                    indexOf++;
                    CharSequence subSequence = i.subSequence(0, indexOf);
                    i = (int) Math.ceil((double) paint.measureText(i, 0, indexOf));
                    indexOf = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                    this.userNameLength = subSequence.length();
                    int i3 = indexOf - i;
                    CharSequence ellipsize = TextUtils.ellipsize(this.caption, paint, (float) i3, TruncateAt.END);
                    this.xOffset = i;
                    try {
                        this.captionLayout = new StaticLayout(ellipsize, getPaint(), i3, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        if (this.captionLayout.getLineCount() > 0) {
                            this.xOffset = (int) (((float) this.xOffset) + (-this.captionLayout.getLineLeft(0)));
                        }
                        this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2) + AndroidUtilities.dp(NUM);
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                }
            }
        }
    }

    public String getCaption() {
        return this.caption;
    }

    public void setHintColor(int i) {
        super.setHintColor(i);
        this.hintColor = i;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (this.captionLayout != null && this.userNameLength == length()) {
                Paint paint = getPaint();
                int color = getPaint().getColor();
                paint.setColor(this.hintColor);
                canvas.save();
                canvas.translate((float) this.xOffset, (float) this.yOffset);
                this.captionLayout.draw(canvas);
                canvas.restore();
                paint.setColor(color);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }
}
