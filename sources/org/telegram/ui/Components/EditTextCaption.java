package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TextStyleSpan;

public class EditTextCaption extends EditTextBoldCursor {
    private boolean allowTextEntitiesIntersection;
    private String caption;
    private StaticLayout captionLayout;
    /* access modifiers changed from: private */
    public boolean copyPasteShowed;
    private EditTextCaptionDelegate delegate;
    private int hintColor;
    private int selectionEnd = -1;
    private int selectionStart = -1;
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
            String str3 = this.caption;
            if (str3 == null || !str3.equals(str)) {
                this.caption = str;
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
        TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
        textStyleRun.flags |= 1;
        applyTextStyleToSelection(new TextStyleSpan(textStyleRun));
    }

    public void makeSelectedItalic() {
        TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
        textStyleRun.flags |= 2;
        applyTextStyleToSelection(new TextStyleSpan(textStyleRun));
    }

    public void makeSelectedMono() {
        TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
        textStyleRun.flags |= 4;
        applyTextStyleToSelection(new TextStyleSpan(textStyleRun));
    }

    public void makeSelectedStrike() {
        TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
        textStyleRun.flags |= 8;
        applyTextStyleToSelection(new TextStyleSpan(textStyleRun));
    }

    public void makeSelectedUnderline() {
        TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
        textStyleRun.flags |= 16;
        applyTextStyleToSelection(new TextStyleSpan(textStyleRun));
    }

    public void makeSelectedUrl() {
        int i;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getString("CreateLink", NUM));
        AnonymousClass1 r1 = new EditTextBoldCursor(this, getContext()) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
            }
        };
        r1.setTextSize(1, 18.0f);
        r1.setText("http://");
        r1.setTextColor(Theme.getColor("dialogTextBlack"));
        r1.setHintText(LocaleController.getString("URL", NUM));
        r1.setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
        r1.setSingleLine(true);
        r1.setFocusable(true);
        r1.setTransformHintToHeader(true);
        r1.setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
        r1.setImeOptions(6);
        r1.setBackgroundDrawable((Drawable) null);
        r1.requestFocus();
        r1.setPadding(0, 0, 0, 0);
        builder.setView(r1);
        int i2 = this.selectionStart;
        if (i2 < 0 || (i = this.selectionEnd) < 0) {
            i2 = getSelectionStart();
            i = getSelectionEnd();
        } else {
            this.selectionEnd = -1;
            this.selectionStart = -1;
        }
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(i2, i, r1) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ EditTextBoldCursor f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                EditTextCaption.this.lambda$makeSelectedUrl$0$EditTextCaption(this.f$1, this.f$2, this.f$3, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.show().setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                EditTextCaption.lambda$makeSelectedUrl$1(EditTextBoldCursor.this, dialogInterface);
            }
        });
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) r1.getLayoutParams();
        if (marginLayoutParams != null) {
            if (marginLayoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) marginLayoutParams).gravity = 1;
            }
            int dp = AndroidUtilities.dp(24.0f);
            marginLayoutParams.leftMargin = dp;
            marginLayoutParams.rightMargin = dp;
            marginLayoutParams.height = AndroidUtilities.dp(36.0f);
            r1.setLayoutParams(marginLayoutParams);
        }
        r1.setSelection(0, r1.getText().length());
    }

    public /* synthetic */ void lambda$makeSelectedUrl$0$EditTextCaption(int i, int i2, EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface, int i3) {
        Editable text = getText();
        CharacterStyle[] characterStyleArr = (CharacterStyle[]) text.getSpans(i, i2, CharacterStyle.class);
        if (characterStyleArr != null && characterStyleArr.length > 0) {
            for (CharacterStyle characterStyle : characterStyleArr) {
                int spanStart = text.getSpanStart(characterStyle);
                int spanEnd = text.getSpanEnd(characterStyle);
                text.removeSpan(characterStyle);
                if (spanStart < i) {
                    text.setSpan(characterStyle, spanStart, i, 33);
                }
                if (spanEnd > i2) {
                    text.setSpan(characterStyle, i2, spanEnd, 33);
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
        applyTextStyleToSelection((TextStyleSpan) null);
    }

    public void setSelectionOverride(int i, int i2) {
        this.selectionStart = i;
        this.selectionEnd = i2;
    }

    private void applyTextStyleToSelection(TextStyleSpan textStyleSpan) {
        int i;
        int i2 = this.selectionStart;
        if (i2 < 0 || (i = this.selectionEnd) < 0) {
            i2 = getSelectionStart();
            i = getSelectionEnd();
        } else {
            this.selectionEnd = -1;
            this.selectionStart = -1;
        }
        MediaDataController.addStyleToText(textStyleSpan, i2, i, getText(), this.allowTextEntitiesIntersection);
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    public void onWindowFocusChanged(boolean z) {
        if (Build.VERSION.SDK_INT >= 23 || z || !this.copyPasteShowed) {
            super.onWindowFocusChanged(z);
        }
    }

    private ActionMode.Callback overrideCallback(final ActionMode.Callback callback) {
        final AnonymousClass2 r0 = new ActionMode.Callback() {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                boolean unused = EditTextCaption.this.copyPasteShowed = true;
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
                boolean unused = EditTextCaption.this.copyPasteShowed = false;
                callback.onDestroyActionMode(actionMode);
            }
        };
        return Build.VERSION.SDK_INT >= 23 ? new ActionMode.Callback2(this) {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return r0.onCreateActionMode(actionMode, menu);
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return r0.onPrepareActionMode(actionMode, menu);
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return r0.onActionItemClicked(actionMode, menuItem);
            }

            public void onDestroyActionMode(ActionMode actionMode) {
                r0.onDestroyActionMode(actionMode);
            }

            public void onGetContentRect(ActionMode actionMode, View view, Rect rect) {
                ActionMode.Callback callback = callback;
                if (callback instanceof ActionMode.Callback2) {
                    ((ActionMode.Callback2) callback).onGetContentRect(actionMode, view, rect);
                } else {
                    super.onGetContentRect(actionMode, view, rect);
                }
            }
        } : r0;
    }

    public ActionMode startActionMode(ActionMode.Callback callback, int i) {
        return super.startActionMode(overrideCallback(callback), i);
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        return super.startActionMode(overrideCallback(callback));
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"DrawAllocation"})
    public void onMeasure(int i, int i2) {
        int indexOf;
        try {
            super.onMeasure(i, i2);
        } catch (Exception e) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(51.0f));
            FileLog.e((Throwable) e);
        }
        this.captionLayout = null;
        String str = this.caption;
        if (str != null && str.length() > 0) {
            Editable text = getText();
            if (text.length() > 1 && text.charAt(0) == '@' && (indexOf = TextUtils.indexOf(text, ' ')) != -1) {
                TextPaint paint = getPaint();
                int i3 = indexOf + 1;
                CharSequence subSequence = text.subSequence(0, i3);
                int ceil = (int) Math.ceil((double) paint.measureText(text, 0, i3));
                this.userNameLength = subSequence.length();
                int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - ceil;
                CharSequence ellipsize = TextUtils.ellipsize(this.caption, paint, (float) measuredWidth, TextUtils.TruncateAt.END);
                this.xOffset = ceil;
                try {
                    StaticLayout staticLayout = new StaticLayout(ellipsize, getPaint(), measuredWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.captionLayout = staticLayout;
                    if (staticLayout.getLineCount() > 0) {
                        this.xOffset = (int) (((float) this.xOffset) + (-this.captionLayout.getLineLeft(0)));
                    }
                    this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2) + AndroidUtilities.dp(0.5f);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
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

    /* access modifiers changed from: protected */
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
            FileLog.e((Throwable) e);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (TextUtils.isEmpty(this.caption)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            accessibilityNodeInfo.setHintText(this.caption);
            return;
        }
        accessibilityNodeInfo.setText(accessibilityNodeInfo.getText() + ", " + this.caption);
    }
}
