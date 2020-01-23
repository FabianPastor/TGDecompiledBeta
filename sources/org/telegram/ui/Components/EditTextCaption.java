package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
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
import android.view.ActionMode.Callback2;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.TextStyleSpan.TextStyleRun;

public class EditTextCaption extends EditTextBoldCursor {
    private boolean allowTextEntitiesIntersection;
    private String caption;
    private StaticLayout captionLayout;
    private boolean copyPasteShowed;
    private EditTextCaptionDelegate delegate;
    private int hintColor;
    private int selectionEnd = -1;
    private int selectionStart = -1;
    private int triesCount = 0;
    private int userNameLength;
    private int xOffset;
    private int yOffset;

    public interface EditTextCaptionDelegate {
        void onSpansChanged();
    }

    public EditTextCaption(Context context) {
        super(context);
    }

    public void setCaption(String str) {
        String str2 = this.caption;
        if ((str2 != null && str2.length() != 0) || (str != null && str.length() != 0)) {
            str2 = this.caption;
            if (str2 == null || !str2.equals(str)) {
                this.caption = str;
                str = this.caption;
                if (str != null) {
                    this.caption = str.replace(10, ' ');
                }
                requestLayout();
            }
        }
    }

    public void setDelegate(EditTextCaptionDelegate editTextCaptionDelegate) {
        this.delegate = editTextCaptionDelegate;
    }

    public void setAllowTextEntitiesIntersection(boolean z) {
        this.allowTextEntitiesIntersection = z;
    }

    public void makeSelectedBold() {
        TextStyleRun textStyleRun = new TextStyleRun();
        textStyleRun.flags |= 1;
        applyTextStyleToSelection(new TextStyleSpan(textStyleRun));
    }

    public void makeSelectedItalic() {
        TextStyleRun textStyleRun = new TextStyleRun();
        textStyleRun.flags |= 2;
        applyTextStyleToSelection(new TextStyleSpan(textStyleRun));
    }

    public void makeSelectedMono() {
        TextStyleRun textStyleRun = new TextStyleRun();
        textStyleRun.flags |= 4;
        applyTextStyleToSelection(new TextStyleSpan(textStyleRun));
    }

    public void makeSelectedStrike() {
        TextStyleRun textStyleRun = new TextStyleRun();
        textStyleRun.flags |= 8;
        applyTextStyleToSelection(new TextStyleSpan(textStyleRun));
    }

    public void makeSelectedUnderline() {
        TextStyleRun textStyleRun = new TextStyleRun();
        textStyleRun.flags |= 16;
        applyTextStyleToSelection(new TextStyleSpan(textStyleRun));
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x00c2  */
    public void makeSelectedUrl() {
        /*
        r9 = this;
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r1 = r9.getContext();
        r0.<init>(r1);
        r1 = "CreateLink";
        r2 = NUM; // 0x7f0e034f float:1.8876755E38 double:1.053162575E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r0.setTitle(r1);
        r1 = new org.telegram.ui.Components.EditTextCaption$1;
        r2 = r9.getContext();
        r1.<init>(r2);
        r2 = 1;
        r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r1.setTextSize(r2, r3);
        r3 = "http://";
        r1.setText(r3);
        r3 = "dialogTextBlack";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r1.setTextColor(r3);
        r3 = "URL";
        r4 = NUM; // 0x7f0e0b64 float:1.8880952E38 double:1.0531635973E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
        r1.setHintText(r3);
        r3 = "windowBackgroundWhiteBlueHeader";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r1.setHeaderHintColor(r3);
        r1.setSingleLine(r2);
        r1.setFocusable(r2);
        r1.setTransformHintToHeader(r2);
        r3 = "windowBackgroundWhiteInputField";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r4 = "windowBackgroundWhiteInputFieldActivated";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r5 = "windowBackgroundWhiteRedText3";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r1.setLineColors(r3, r4, r5);
        r3 = 6;
        r1.setImeOptions(r3);
        r3 = 0;
        r1.setBackgroundDrawable(r3);
        r1.requestFocus();
        r4 = 0;
        r1.setPadding(r4, r4, r4, r4);
        r0.setView(r1);
        r5 = r9.selectionStart;
        if (r5 < 0) goto L_0x0089;
    L_0x007f:
        r6 = r9.selectionEnd;
        if (r6 < 0) goto L_0x0089;
    L_0x0083:
        r7 = -1;
        r9.selectionEnd = r7;
        r9.selectionStart = r7;
        goto L_0x0091;
    L_0x0089:
        r5 = r9.getSelectionStart();
        r6 = r9.getSelectionEnd();
    L_0x0091:
        r7 = NUM; // 0x7f0e0776 float:1.8878911E38 double:1.0531631003E-314;
        r8 = "OK";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r8 = new org.telegram.ui.Components.-$$Lambda$EditTextCaption$BQIhHIR0EWfMGyyXmJJ-pkFKO1Y;
        r8.<init>(r9, r5, r6, r1);
        r0.setPositiveButton(r7, r8);
        r5 = NUM; // 0x7f0e021c float:1.8876133E38 double:1.0531624234E-314;
        r6 = "Cancel";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r0.setNegativeButton(r5, r3);
        r0 = r0.show();
        r3 = new org.telegram.ui.Components.-$$Lambda$EditTextCaption$8tXURyNItaU0tMIyLqohmCvoG40;
        r3.<init>(r1);
        r0.setOnShowListener(r3);
        r0 = r1.getLayoutParams();
        r0 = (android.view.ViewGroup.MarginLayoutParams) r0;
        if (r0 == 0) goto L_0x00e0;
    L_0x00c2:
        r3 = r0 instanceof android.widget.FrameLayout.LayoutParams;
        if (r3 == 0) goto L_0x00cb;
    L_0x00c6:
        r3 = r0;
        r3 = (android.widget.FrameLayout.LayoutParams) r3;
        r3.gravity = r2;
    L_0x00cb:
        r2 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.leftMargin = r2;
        r0.rightMargin = r2;
        r2 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.height = r2;
        r1.setLayoutParams(r0);
    L_0x00e0:
        r0 = r1.getText();
        r0 = r0.length();
        r1.setSelection(r4, r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EditTextCaption.makeSelectedUrl():void");
    }

    public /* synthetic */ void lambda$makeSelectedUrl$0$EditTextCaption(int i, int i2, EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface, int i3) {
        Editable text = getText();
        CharacterStyle[] characterStyleArr = (CharacterStyle[]) text.getSpans(i, i2, CharacterStyle.class);
        if (characterStyleArr != null && characterStyleArr.length > 0) {
            for (Object obj : characterStyleArr) {
                int spanStart = text.getSpanStart(obj);
                int spanEnd = text.getSpanEnd(obj);
                text.removeSpan(obj);
                if (spanStart < i) {
                    text.setSpan(obj, spanStart, i, 33);
                }
                if (spanEnd > i2) {
                    text.setSpan(obj, i2, spanEnd, 33);
                }
            }
        }
        try {
            text.setSpan(new URLSpanReplacement(editTextBoldCursor.getText().toString()), i, i2, 33);
        } catch (Exception unused) {
        }
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    static /* synthetic */ void lambda$makeSelectedUrl$1(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public void makeSelectedRegular() {
        applyTextStyleToSelection(null);
    }

    public void setSelectionOverride(int i, int i2) {
        this.selectionStart = i;
        this.selectionEnd = i2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0023  */
    private void applyTextStyleToSelection(org.telegram.ui.Components.TextStyleSpan r5) {
        /*
        r4 = this;
        r0 = r4.selectionStart;
        if (r0 < 0) goto L_0x000e;
    L_0x0004:
        r1 = r4.selectionEnd;
        if (r1 < 0) goto L_0x000e;
    L_0x0008:
        r2 = -1;
        r4.selectionEnd = r2;
        r4.selectionStart = r2;
        goto L_0x0016;
    L_0x000e:
        r0 = r4.getSelectionStart();
        r1 = r4.getSelectionEnd();
    L_0x0016:
        r2 = r4.getText();
        r3 = r4.allowTextEntitiesIntersection;
        org.telegram.messenger.MediaDataController.addStyleToText(r5, r0, r1, r2, r3);
        r5 = r4.delegate;
        if (r5 == 0) goto L_0x0026;
    L_0x0023:
        r5.onSpansChanged();
    L_0x0026:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EditTextCaption.applyTextStyleToSelection(org.telegram.ui.Components.TextStyleSpan):void");
    }

    public void onWindowFocusChanged(boolean z) {
        if (VERSION.SDK_INT >= 23 || z || !this.copyPasteShowed) {
            super.onWindowFocusChanged(z);
        }
    }

    private Callback overrideCallback(final Callback callback) {
        final AnonymousClass2 anonymousClass2 = new Callback() {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                EditTextCaption.this.copyPasteShowed = true;
                return callback.onCreateActionMode(actionMode, menu);
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return callback.onPrepareActionMode(actionMode, menu);
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == NUM) {
                    EditTextCaption.this.makeSelectedRegular();
                    actionMode.finish();
                    return true;
                } else if (menuItem.getItemId() == NUM) {
                    EditTextCaption.this.makeSelectedBold();
                    actionMode.finish();
                    return true;
                } else if (menuItem.getItemId() == NUM) {
                    EditTextCaption.this.makeSelectedItalic();
                    actionMode.finish();
                    return true;
                } else if (menuItem.getItemId() == NUM) {
                    EditTextCaption.this.makeSelectedMono();
                    actionMode.finish();
                    return true;
                } else if (menuItem.getItemId() == NUM) {
                    EditTextCaption.this.makeSelectedUrl();
                    actionMode.finish();
                    return true;
                } else if (menuItem.getItemId() == NUM) {
                    EditTextCaption.this.makeSelectedStrike();
                    actionMode.finish();
                    return true;
                } else if (menuItem.getItemId() == NUM) {
                    EditTextCaption.this.makeSelectedUnderline();
                    actionMode.finish();
                    return true;
                } else {
                    try {
                        return callback.onActionItemClicked(actionMode, menuItem);
                    } catch (Exception unused) {
                        return true;
                    }
                }
            }

            public void onDestroyActionMode(ActionMode actionMode) {
                EditTextCaption.this.copyPasteShowed = false;
                callback.onDestroyActionMode(actionMode);
            }
        };
        return VERSION.SDK_INT >= 23 ? new Callback2() {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return anonymousClass2.onCreateActionMode(actionMode, menu);
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return anonymousClass2.onPrepareActionMode(actionMode, menu);
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return anonymousClass2.onActionItemClicked(actionMode, menuItem);
            }

            public void onDestroyActionMode(ActionMode actionMode) {
                anonymousClass2.onDestroyActionMode(actionMode);
            }

            public void onGetContentRect(ActionMode actionMode, View view, Rect rect) {
                Callback callback = callback;
                if (callback instanceof Callback2) {
                    ((Callback2) callback).onGetContentRect(actionMode, view, rect);
                } else {
                    super.onGetContentRect(actionMode, view, rect);
                }
            }
        } : anonymousClass2;
    }

    public ActionMode startActionMode(Callback callback, int i) {
        return super.startActionMode(overrideCallback(callback), i);
    }

    public ActionMode startActionMode(Callback callback) {
        return super.startActionMode(overrideCallback(callback));
    }

    /* Access modifiers changed, original: protected */
    @SuppressLint({"DrawAllocation"})
    public void onMeasure(int i, int i2) {
        try {
            super.onMeasure(i, i2);
        } catch (Exception e) {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(51.0f));
            FileLog.e(e);
        }
        this.captionLayout = null;
        String str = this.caption;
        if (str != null && str.length() > 0) {
            Editable text = getText();
            if (text.length() > 1 && text.charAt(0) == '@') {
                int indexOf = TextUtils.indexOf(text, ' ');
                if (indexOf != -1) {
                    TextPaint paint = getPaint();
                    indexOf++;
                    CharSequence subSequence = text.subSequence(0, indexOf);
                    i = (int) Math.ceil((double) paint.measureText(text, 0, indexOf));
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
                        this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2) + AndroidUtilities.dp(0.5f);
                    } catch (Exception e2) {
                        FileLog.e(e2);
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (this.captionLayout != null && this.userNameLength == length()) {
                TextPaint paint = getPaint();
                int color = getPaint().getColor();
                paint.setColor(this.hintColor);
                canvas.save();
                canvas.translate((float) this.xOffset, (float) this.yOffset);
                this.captionLayout.draw(canvas);
                canvas.restore();
                paint.setColor(color);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (!TextUtils.isEmpty(this.caption)) {
            if (VERSION.SDK_INT >= 26) {
                accessibilityNodeInfo.setHintText(this.caption);
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(accessibilityNodeInfo.getText());
            stringBuilder.append(", ");
            stringBuilder.append(this.caption);
            accessibilityNodeInfo.setText(stringBuilder.toString());
        }
    }
}
