package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.utils.CopyUtilities;
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
    /* access modifiers changed from: private */
    public boolean isInitLineCount;
    /* access modifiers changed from: private */
    public int lineCount;
    private float offsetY;
    private final Theme.ResourcesProvider resourcesProvider;
    private int selectionEnd = -1;
    private int selectionStart = -1;
    private int userNameLength;
    private int xOffset;
    private int yOffset;

    public interface EditTextCaptionDelegate {
        void onSpansChanged();
    }

    /* access modifiers changed from: protected */
    public void onContextMenuClose() {
    }

    /* access modifiers changed from: protected */
    public void onContextMenuOpen() {
    }

    /* access modifiers changed from: protected */
    public void onLineCountChanged(int i, int i2) {
    }

    public EditTextCaption(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
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
        setClipToPadding(true);
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

    public void makeSelectedSpoiler() {
        TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
        textStyleRun.flags |= 256;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
        builder.setTitle(LocaleController.getString("CreateLink", NUM));
        AnonymousClass2 r1 = new EditTextBoldCursor(this, getContext()) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
            }
        };
        r1.setTextSize(1, 18.0f);
        r1.setText("http://");
        r1.setTextColor(getThemedColor("dialogTextBlack"));
        r1.setHintText(LocaleController.getString("URL", NUM));
        r1.setHeaderHintColor(getThemedColor("windowBackgroundWhiteBlueHeader"));
        r1.setSingleLine(true);
        r1.setFocusable(true);
        r1.setTransformHintToHeader(true);
        r1.setLineColors(getThemedColor("windowBackgroundWhiteInputField"), getThemedColor("windowBackgroundWhiteInputFieldActivated"), getThemedColor("windowBackgroundWhiteRedText3"));
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
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new EditTextCaption$$ExternalSyntheticLambda0(this, i2, i, r1));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.show().setOnShowListener(new EditTextCaption$$ExternalSyntheticLambda1(r1));
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$makeSelectedUrl$0(int i, int i2, EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface, int i3) {
        Editable text = getText();
        CharacterStyle[] characterStyleArr = (CharacterStyle[]) text.getSpans(i, i2, CharacterStyle.class);
        if (characterStyleArr != null && characterStyleArr.length > 0) {
            for (CharacterStyle characterStyle : characterStyleArr) {
                if (!(characterStyle instanceof AnimatedEmojiSpan)) {
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$makeSelectedUrl$1(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
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
            try {
                super.onWindowFocusChanged(z);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    private ActionMode.Callback overrideCallback(final ActionMode.Callback callback) {
        final AnonymousClass3 r0 = new ActionMode.Callback() {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                boolean unused = EditTextCaption.this.copyPasteShowed = true;
                EditTextCaption.this.onContextMenuOpen();
                return callback.onCreateActionMode(actionMode, menu);
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return callback.onPrepareActionMode(actionMode, menu);
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (EditTextCaption.this.performMenuAction(menuItem.getItemId())) {
                    actionMode.finish();
                    return true;
                }
                try {
                    return callback.onActionItemClicked(actionMode, menuItem);
                } catch (Exception unused) {
                    return true;
                }
            }

            public void onDestroyActionMode(ActionMode actionMode) {
                boolean unused = EditTextCaption.this.copyPasteShowed = false;
                EditTextCaption.this.onContextMenuClose();
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

    /* access modifiers changed from: private */
    public boolean performMenuAction(int i) {
        if (i == NUM) {
            makeSelectedRegular();
            return true;
        } else if (i == NUM) {
            makeSelectedBold();
            return true;
        } else if (i == NUM) {
            makeSelectedItalic();
            return true;
        } else if (i == NUM) {
            makeSelectedMono();
            return true;
        } else if (i == NUM) {
            makeSelectedUrl();
            return true;
        } else if (i == NUM) {
            makeSelectedStrike();
            return true;
        } else if (i == NUM) {
            makeSelectedUnderline();
            return true;
        } else if (i != NUM) {
            return false;
        } else {
            makeSelectedSpoiler();
            return true;
        }
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
            this.isInitLineCount = getMeasuredWidth() == 0 && getMeasuredHeight() == 0;
            super.onMeasure(i, i2);
            if (this.isInitLineCount) {
                this.lineCount = getLineCount();
            }
            this.isInitLineCount = false;
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

    public void setOffsetY(float f) {
        this.offsetY = f;
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
        canvas.restore();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        AccessibilityNodeInfoCompat wrap = AccessibilityNodeInfoCompat.wrap(accessibilityNodeInfo);
        if (!TextUtils.isEmpty(this.caption)) {
            wrap.setHintText(this.caption);
        }
        List<AccessibilityNodeInfoCompat.AccessibilityActionCompat> actionList = wrap.getActionList();
        int i = 0;
        int size = actionList.size();
        while (true) {
            if (i >= size) {
                break;
            }
            AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat = actionList.get(i);
            if (accessibilityActionCompat.getId() == NUM) {
                wrap.removeAction(accessibilityActionCompat);
                break;
            }
            i++;
        }
        if (hasSelection()) {
            wrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Spoiler", NUM)));
            wrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Bold", NUM)));
            wrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Italic", NUM)));
            wrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Mono", NUM)));
            wrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Strike", NUM)));
            wrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Underline", NUM)));
            wrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("CreateLink", NUM)));
            wrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(NUM, LocaleController.getString("Regular", NUM)));
        }
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        return performMenuAction(i) || super.performAccessibilityAction(i, bundle);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    public boolean onTextContextMenuItem(int i) {
        if (i == 16908322) {
            ClipData primaryClip = ((ClipboardManager) getContext().getSystemService("clipboard")).getPrimaryClip();
            if (primaryClip != null && primaryClip.getItemCount() == 1 && primaryClip.getDescription().hasMimeType("text/html")) {
                try {
                    Spannable fromHTML = CopyUtilities.fromHTML(primaryClip.getItemAt(0).getHtmlText());
                    Emoji.replaceEmoji(fromHTML, getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, (int[]) null, true);
                    AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) fromHTML.getSpans(0, fromHTML.length(), AnimatedEmojiSpan.class);
                    if (animatedEmojiSpanArr != null) {
                        for (AnimatedEmojiSpan applyFontMetrics : animatedEmojiSpanArr) {
                            applyFontMetrics.applyFontMetrics(getPaint().getFontMetricsInt(), AnimatedEmojiDrawable.getCacheTypeForEnterView());
                        }
                    }
                    int max = Math.max(0, getSelectionStart());
                    setText(getText().replace(max, Math.min(getText().length(), getSelectionEnd()), fromHTML));
                    setSelection(fromHTML.length() + max, max + fromHTML.length());
                    return true;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        } else if (i == 16908321) {
            try {
                AndroidUtilities.addToClipboard(getText().subSequence(Math.max(0, getSelectionStart()), Math.min(getText().length(), getSelectionEnd())));
                return true;
            } catch (Exception unused) {
            }
        } else if (i == 16908320) {
            int max2 = Math.max(0, getSelectionStart());
            int min = Math.min(getText().length(), getSelectionEnd());
            AndroidUtilities.addToClipboard(getText().subSequence(max2, min));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (max2 != 0) {
                spannableStringBuilder.append(getText().subSequence(0, max2));
            }
            if (min != getText().length()) {
                spannableStringBuilder.append(getText().subSequence(min, getText().length()));
            }
            setText(spannableStringBuilder);
            return true;
        }
        return super.onTextContextMenuItem(i);
    }
}
