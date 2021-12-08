package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TextStyleSpan;

public class EditTextCaption extends EditTextBoldCursor {
    private static final int ACCESSIBILITY_ACTION_SHARE = NUM;
    private boolean allowTextEntitiesIntersection;
    private String caption;
    private StaticLayout captionLayout;
    /* access modifiers changed from: private */
    public boolean copyPasteShowed;
    private EditTextCaptionDelegate delegate;
    private int hintColor;
    /* access modifiers changed from: private */
    public boolean isInitLineCount;
    /* access modifiers changed from: private */
    public int lineCount;
    private float offsetY;
    private final Theme.ResourcesProvider resourcesProvider;
    private int selectionEnd = -1;
    private int selectionStart = -1;
    private int triesCount = 0;
    private int userNameLength;
    private int xOffset;
    private int yOffset;

    public interface EditTextCaptionDelegate {
        void onSpansChanged();
    }

    public EditTextCaption(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (EditTextCaption.this.lineCount != EditTextCaption.this.getLineCount()) {
                    if (!EditTextCaption.this.isInitLineCount && EditTextCaption.this.getMeasuredWidth() > 0) {
                        EditTextCaption editTextCaption = EditTextCaption.this;
                        editTextCaption.onLineCountChanged(editTextCaption.lineCount, EditTextCaption.this.getLineCount());
                    }
                    EditTextCaption editTextCaption2 = EditTextCaption.this;
                    int unused = editTextCaption2.lineCount = editTextCaption2.getLineCount();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onLineCountChanged(int oldLineCount, int newLineCount) {
    }

    public void setCaption(String value) {
        String str = this.caption;
        if ((str != null && str.length() != 0) || (value != null && value.length() != 0)) {
            String str2 = this.caption;
            if (str2 == null || !str2.equals(value)) {
                this.caption = value;
                if (value != null) {
                    this.caption = value.replace(10, ' ');
                }
                requestLayout();
            }
        }
    }

    public void setDelegate(EditTextCaptionDelegate editTextCaptionDelegate) {
        this.delegate = editTextCaptionDelegate;
    }

    public void setAllowTextEntitiesIntersection(boolean value) {
        this.allowTextEntitiesIntersection = value;
    }

    public void makeSelectedBold() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 1;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedItalic() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 2;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedMono() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 4;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedStrike() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 8;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedUnderline() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 16;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedUrl() {
        int end;
        int start;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
        builder.setTitle(LocaleController.getString("CreateLink", NUM));
        EditTextBoldCursor editText = new EditTextBoldCursor(getContext()) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
            }
        };
        editText.setTextSize(1, 18.0f);
        editText.setText("http://");
        editText.setTextColor(getThemedColor("dialogTextBlack"));
        editText.setHintText(LocaleController.getString("URL", NUM));
        editText.setHeaderHintColor(getThemedColor("windowBackgroundWhiteBlueHeader"));
        editText.setSingleLine(true);
        editText.setFocusable(true);
        editText.setTransformHintToHeader(true);
        editText.setLineColors(getThemedColor("windowBackgroundWhiteInputField"), getThemedColor("windowBackgroundWhiteInputFieldActivated"), getThemedColor("windowBackgroundWhiteRedText3"));
        editText.setImeOptions(6);
        editText.setBackgroundDrawable((Drawable) null);
        editText.requestFocus();
        editText.setPadding(0, 0, 0, 0);
        builder.setView(editText);
        if (this.selectionStart < 0 || this.selectionEnd < 0) {
            start = getSelectionStart();
            end = getSelectionEnd();
        } else {
            start = this.selectionStart;
            end = this.selectionEnd;
            this.selectionEnd = -1;
            this.selectionStart = -1;
        }
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new EditTextCaption$$ExternalSyntheticLambda0(this, start, end, editText));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.show().setOnShowListener(new EditTextCaption$$ExternalSyntheticLambda1(editText));
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
        if (layoutParams != null) {
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) layoutParams).gravity = 1;
            }
            int dp = AndroidUtilities.dp(24.0f);
            layoutParams.leftMargin = dp;
            layoutParams.rightMargin = dp;
            layoutParams.height = AndroidUtilities.dp(36.0f);
            editText.setLayoutParams(layoutParams);
        }
        editText.setSelection(0, editText.getText().length());
    }

    /* renamed from: lambda$makeSelectedUrl$0$org-telegram-ui-Components-EditTextCaption  reason: not valid java name */
    public /* synthetic */ void m2230x8e956f7d(int start, int end, EditTextBoldCursor editText, DialogInterface dialogInterface, int i) {
        Editable editable = getText();
        CharacterStyle[] spans = (CharacterStyle[]) editable.getSpans(start, end, CharacterStyle.class);
        if (spans != null && spans.length > 0) {
            for (CharacterStyle oldSpan : spans) {
                int spanStart = editable.getSpanStart(oldSpan);
                int spanEnd = editable.getSpanEnd(oldSpan);
                editable.removeSpan(oldSpan);
                if (spanStart < start) {
                    editable.setSpan(oldSpan, spanStart, start, 33);
                }
                if (spanEnd > end) {
                    editable.setSpan(oldSpan, end, spanEnd, 33);
                }
            }
        }
        try {
            editable.setSpan(new URLSpanReplacement(editText.getText().toString()), start, end, 33);
        } catch (Exception e) {
        }
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    static /* synthetic */ void lambda$makeSelectedUrl$1(EditTextBoldCursor editText, DialogInterface dialog) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    public void makeSelectedRegular() {
        applyTextStyleToSelection((TextStyleSpan) null);
    }

    public void setSelectionOverride(int start, int end) {
        this.selectionStart = start;
        this.selectionEnd = end;
    }

    private void applyTextStyleToSelection(TextStyleSpan span) {
        int end;
        int start;
        if (this.selectionStart < 0 || this.selectionEnd < 0) {
            start = getSelectionStart();
            end = getSelectionEnd();
        } else {
            start = this.selectionStart;
            end = this.selectionEnd;
            this.selectionEnd = -1;
            this.selectionStart = -1;
        }
        MediaDataController.addStyleToText(span, start, end, getText(), this.allowTextEntitiesIntersection);
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (Build.VERSION.SDK_INT >= 23 || hasWindowFocus || !this.copyPasteShowed) {
            try {
                super.onWindowFocusChanged(hasWindowFocus);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    private ActionMode.Callback overrideCallback(final ActionMode.Callback callback) {
        final ActionMode.Callback wrap = new ActionMode.Callback() {
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                boolean unused = EditTextCaption.this.copyPasteShowed = true;
                return callback.onCreateActionMode(mode, menu);
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return callback.onPrepareActionMode(mode, menu);
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (EditTextCaption.this.performMenuAction(item.getItemId())) {
                    mode.finish();
                    return true;
                }
                try {
                    return callback.onActionItemClicked(mode, item);
                } catch (Exception e) {
                    return true;
                }
            }

            public void onDestroyActionMode(ActionMode mode) {
                boolean unused = EditTextCaption.this.copyPasteShowed = false;
                callback.onDestroyActionMode(mode);
            }
        };
        return Build.VERSION.SDK_INT >= 23 ? new ActionMode.Callback2() {
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return wrap.onCreateActionMode(mode, menu);
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return wrap.onPrepareActionMode(mode, menu);
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return wrap.onActionItemClicked(mode, item);
            }

            public void onDestroyActionMode(ActionMode mode) {
                wrap.onDestroyActionMode(mode);
            }

            public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
                ActionMode.Callback callback = callback;
                if (callback instanceof ActionMode.Callback2) {
                    ((ActionMode.Callback2) callback).onGetContentRect(mode, view, outRect);
                } else {
                    super.onGetContentRect(mode, view, outRect);
                }
            }
        } : wrap;
    }

    /* access modifiers changed from: private */
    public boolean performMenuAction(int itemId) {
        if (itemId == NUM) {
            makeSelectedRegular();
            return true;
        } else if (itemId == NUM) {
            makeSelectedBold();
            return true;
        } else if (itemId == NUM) {
            makeSelectedItalic();
            return true;
        } else if (itemId == NUM) {
            makeSelectedMono();
            return true;
        } else if (itemId == NUM) {
            makeSelectedUrl();
            return true;
        } else if (itemId == NUM) {
            makeSelectedStrike();
            return true;
        } else if (itemId != NUM) {
            return false;
        } else {
            makeSelectedUnderline();
            return true;
        }
    }

    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        return super.startActionMode(overrideCallback(callback), type);
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        return super.startActionMode(overrideCallback(callback));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int index;
        try {
            this.isInitLineCount = getMeasuredWidth() == 0 && getMeasuredHeight() == 0;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.isInitLineCount) {
                this.lineCount = getLineCount();
            }
            this.isInitLineCount = false;
        } catch (Exception e) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(51.0f));
            FileLog.e((Throwable) e);
        }
        this.captionLayout = null;
        String str = this.caption;
        if (str != null && str.length() > 0) {
            CharSequence text = getText();
            if (text.length() > 1 && text.charAt(0) == '@' && (index = TextUtils.indexOf(text, ' ')) != -1) {
                TextPaint paint = getPaint();
                CharSequence str2 = text.subSequence(0, index + 1);
                int size = (int) Math.ceil((double) paint.measureText(text, 0, index + 1));
                int width = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                this.userNameLength = str2.length();
                CharSequence captionFinal = TextUtils.ellipsize(this.caption, paint, (float) (width - size), TextUtils.TruncateAt.END);
                this.xOffset = size;
                try {
                    StaticLayout staticLayout = new StaticLayout(captionFinal, getPaint(), width - size, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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

    public void setHintColor(int value) {
        super.setHintColor(value);
        this.hintColor = value;
        invalidate();
    }

    public void setOffsetY(float offset) {
        this.offsetY = offset;
        invalidate();
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(0.0f, this.offsetY);
        super.onDraw(canvas);
        try {
            if (this.captionLayout != null && this.userNameLength == length()) {
                Paint paint = getPaint();
                int oldColor = getPaint().getColor();
                paint.setColor(this.hintColor);
                canvas.save();
                canvas.translate((float) this.xOffset, (float) this.yOffset);
                this.captionLayout.draw(canvas);
                canvas.restore();
                paint.setColor(oldColor);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        canvas.restore();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        AccessibilityNodeInfoCompat infoCompat = AccessibilityNodeInfoCompat.wrap(info);
        if (!TextUtils.isEmpty(this.caption)) {
            infoCompat.setHintText(this.caption);
        }
        List<AccessibilityNodeInfoCompat.AccessibilityActionCompat> actions = infoCompat.getActionList();
        int i = 0;
        int size = actions.size();
        while (true) {
            if (i >= size) {
                break;
            }
            AccessibilityNodeInfoCompat.AccessibilityActionCompat action = actions.get(i);
            if (action.getId() == NUM) {
                infoCompat.removeAction(action);
                break;
            }
            i++;
        }
        if (hasSelection() != 0) {
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Bold", NUM)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Italic", NUM)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Mono", NUM)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Strike", NUM)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Underline", NUM)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("CreateLink", NUM)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Regular", NUM)));
        }
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        return performMenuAction(action) || super.performAccessibilityAction(action, arguments);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
