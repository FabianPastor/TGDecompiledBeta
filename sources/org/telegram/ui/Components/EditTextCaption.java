package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import android.view.MenuItem;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout.LayoutParams;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0501R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;

public class EditTextCaption extends EditTextBoldCursor {
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

    public void setCaption(String value) {
        if ((this.caption != null && this.caption.length() != 0) || (value != null && value.length() != 0)) {
            if (this.caption == null || value == null || !this.caption.equals(value)) {
                this.caption = value;
                if (this.caption != null) {
                    this.caption = this.caption.replace('\n', ' ');
                }
                requestLayout();
            }
        }
    }

    public void setDelegate(EditTextCaptionDelegate editTextCaptionDelegate) {
        this.delegate = editTextCaptionDelegate;
    }

    public void makeSelectedBold() {
        applyTextStyleToSelection(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")));
    }

    public void makeSelectedItalic() {
        applyTextStyleToSelection(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")));
    }

    public void makeSelectedMono() {
        applyTextStyleToSelection(new TypefaceSpan(Typeface.MONOSPACE));
    }

    public void makeSelectedUrl() {
        int start;
        int end;
        Builder builder = new Builder(getContext());
        builder.setTitle(LocaleController.getString("CreateLink", C0501R.string.CreateLink));
        final EditTextBoldCursor editText = new EditTextBoldCursor(getContext()) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
            }
        };
        editText.setTextSize(1, 18.0f);
        editText.setText("http://");
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        editText.setHintText(LocaleController.getString("URL", C0501R.string.URL));
        editText.setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        editText.setSingleLine(true);
        editText.setFocusable(true);
        editText.setTransformHintToHeader(true);
        editText.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
        editText.setImeOptions(6);
        editText.setBackgroundDrawable(null);
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
        builder.setPositiveButton(LocaleController.getString("OK", C0501R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Editable editable = EditTextCaption.this.getText();
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
                editable.setSpan(new URLSpanReplacement(editText.getText().toString()), start, end, 33);
                if (EditTextCaption.this.delegate != null) {
                    EditTextCaption.this.delegate.onSpansChanged();
                }
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0501R.string.Cancel), null);
        builder.show().setOnShowListener(new OnShowListener() {
            public void onShow(DialogInterface dialog) {
                editText.requestFocus();
                AndroidUtilities.showKeyboard(editText);
            }
        });
        if (editText != null) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) editText.getLayoutParams();
            if (layoutParams != null) {
                if (layoutParams instanceof LayoutParams) {
                    ((LayoutParams) layoutParams).gravity = 1;
                }
                int dp = AndroidUtilities.dp(24.0f);
                layoutParams.leftMargin = dp;
                layoutParams.rightMargin = dp;
                layoutParams.height = AndroidUtilities.dp(36.0f);
                editText.setLayoutParams(layoutParams);
            }
            editText.setSelection(0, editText.getText().length());
        }
    }

    public void makeSelectedRegular() {
        applyTextStyleToSelection(null);
    }

    public void setSelectionOverride(int start, int end) {
        this.selectionStart = start;
        this.selectionEnd = end;
    }

    private void applyTextStyleToSelection(TypefaceSpan span) {
        int start;
        int end;
        if (this.selectionStart < 0 || this.selectionEnd < 0) {
            start = getSelectionStart();
            end = getSelectionEnd();
        } else {
            start = this.selectionStart;
            end = this.selectionEnd;
            this.selectionEnd = -1;
            this.selectionStart = -1;
        }
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
        if (span != null) {
            editable.setSpan(span, start, end, 33);
        }
        if (this.delegate != null) {
            this.delegate.onSpansChanged();
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (VERSION.SDK_INT >= 23 || hasWindowFocus || !this.copyPasteShowed) {
            super.onWindowFocusChanged(hasWindowFocus);
        }
    }

    private Callback overrideCallback(final Callback callback) {
        return new Callback() {
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                EditTextCaption.this.copyPasteShowed = true;
                return callback.onCreateActionMode(mode, menu);
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return callback.onPrepareActionMode(mode, menu);
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean z = true;
                if (item.getItemId() == C0501R.id.menu_regular) {
                    EditTextCaption.this.makeSelectedRegular();
                    mode.finish();
                } else if (item.getItemId() == C0501R.id.menu_bold) {
                    EditTextCaption.this.makeSelectedBold();
                    mode.finish();
                } else if (item.getItemId() == C0501R.id.menu_italic) {
                    EditTextCaption.this.makeSelectedItalic();
                    mode.finish();
                } else if (item.getItemId() == C0501R.id.menu_mono) {
                    EditTextCaption.this.makeSelectedMono();
                    mode.finish();
                } else if (item.getItemId() == C0501R.id.menu_link) {
                    EditTextCaption.this.makeSelectedUrl();
                    mode.finish();
                } else {
                    try {
                        z = callback.onActionItemClicked(mode, item);
                    } catch (Exception e) {
                    }
                }
                return z;
            }

            public void onDestroyActionMode(ActionMode mode) {
                EditTextCaption.this.copyPasteShowed = false;
                callback.onDestroyActionMode(mode);
            }
        };
    }

    public ActionMode startActionMode(Callback callback, int type) {
        return super.startActionMode(overrideCallback(callback), type);
    }

    public ActionMode startActionMode(Callback callback) {
        return super.startActionMode(overrideCallback(callback));
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Throwable e) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(51.0f));
            FileLog.m3e(e);
        }
        this.captionLayout = null;
        if (this.caption != null && this.caption.length() > 0) {
            CharSequence text = getText();
            if (text.length() > 1 && text.charAt(0) == '@') {
                int index = TextUtils.indexOf(text, ' ');
                if (index != -1) {
                    TextPaint paint = getPaint();
                    int size = (int) Math.ceil((double) paint.measureText(text, 0, index + 1));
                    int width = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                    this.userNameLength = text.subSequence(0, index + 1).length();
                    CharSequence captionFinal = TextUtils.ellipsize(this.caption, paint, (float) (width - size), TruncateAt.END);
                    this.xOffset = size;
                    try {
                        this.captionLayout = new StaticLayout(captionFinal, getPaint(), width - size, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        if (this.captionLayout.getLineCount() > 0) {
                            this.xOffset = (int) (((float) this.xOffset) + (-this.captionLayout.getLineLeft(0)));
                        }
                        this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2) + AndroidUtilities.dp(0.5f);
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

    public void setHintColor(int value) {
        super.setHintColor(value);
        this.hintColor = value;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }
}
