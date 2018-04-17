package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;

public class StickerSetCell extends FrameLayout {
    private BackupImageView imageView;
    private boolean needDivider;
    private ImageView optionsButton;
    private RadialProgressView progressView;
    private Rect rect = new Rect();
    private TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    public StickerSetCell(Context context, int option) {
        Context context2 = context;
        int i = option;
        super(context);
        this.textView = new TextView(context2);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i2 = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(r0.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        r0.valueTextView = new TextView(context2);
        r0.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        r0.valueTextView.setTextSize(1, 13.0f);
        r0.valueTextView.setLines(1);
        r0.valueTextView.setMaxLines(1);
        r0.valueTextView.setSingleLine(true);
        r0.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(r0.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        r0.imageView = new BackupImageView(context2);
        r0.imageView.setAspectFit(true);
        addView(r0.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        View view;
        if (i == 2) {
            r0.progressView = new RadialProgressView(getContext());
            r0.progressView.setProgressColor(Theme.getColor(Theme.key_dialogProgressCircle));
            r0.progressView.setSize(AndroidUtilities.dp(30.0f));
            view = r0.progressView;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(view, LayoutHelper.createFrame(48, 48.0f, i2 | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        } else if (i != 0) {
            r0.optionsButton = new ImageView(context2);
            int i3 = 0;
            r0.optionsButton.setFocusable(false);
            r0.optionsButton.setScaleType(ScaleType.CENTER);
            r0.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            if (i == 1) {
                r0.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), Mode.MULTIPLY));
                r0.optionsButton.setImageResource(R.drawable.msg_actions);
                view = r0.optionsButton;
                if (LocaleController.isRTL) {
                    i2 = 3;
                }
                addView(view, LayoutHelper.createFrame(40, 40, i2 | 48));
            } else if (i == 3) {
                r0.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
                r0.optionsButton.setImageResource(R.drawable.sticker_added);
                view = r0.optionsButton;
                if (LocaleController.isRTL) {
                    i2 = 3;
                }
                int i4 = i2 | 48;
                float f = (float) (LocaleController.isRTL ? 10 : 0);
                if (!LocaleController.isRTL) {
                    i3 = 10;
                }
                addView(view, LayoutHelper.createFrame(40, 40.0f, i4, f, 12.0f, (float) i3, 0.0f));
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + this.needDivider, NUM));
    }

    public void setText(String title, String subtitle, int icon, boolean divider) {
        this.needDivider = divider;
        this.stickersSet = null;
        this.textView.setText(title);
        this.valueTextView.setText(subtitle);
        if (TextUtils.isEmpty(subtitle)) {
            this.textView.setTranslationY((float) AndroidUtilities.dp(10.0f));
        } else {
            this.textView.setTranslationY(0.0f);
        }
        if (icon != 0) {
            this.imageView.setImageResource(icon, Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon));
            this.imageView.setVisibility(0);
            if (this.progressView != null) {
                this.progressView.setVisibility(4);
                return;
            }
            return;
        }
        this.imageView.setVisibility(4);
        if (this.progressView != null) {
            this.progressView.setVisibility(0);
        }
    }

    public void setStickersSet(TL_messages_stickerSet set, boolean divider) {
        this.needDivider = divider;
        this.stickersSet = set;
        this.imageView.setVisibility(0);
        if (this.progressView != null) {
            this.progressView.setVisibility(4);
        }
        this.textView.setTranslationY(0.0f);
        this.textView.setText(this.stickersSet.set.title);
        if (this.stickersSet.set.archived) {
            this.textView.setAlpha(0.5f);
            this.valueTextView.setAlpha(0.5f);
            this.imageView.setAlpha(0.5f);
        } else {
            this.textView.setAlpha(1.0f);
            this.valueTextView.setAlpha(1.0f);
            this.imageView.setAlpha(1.0f);
        }
        ArrayList<Document> documents = set.documents;
        if (documents == null || documents.isEmpty()) {
            this.valueTextView.setText(LocaleController.formatPluralString("Stickers", 0));
            return;
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", documents.size()));
        Document document = (Document) documents.get(0);
        if (!(document.thumb == null || document.thumb.location == null)) {
            this.imageView.setImage(document.thumb.location, null, "webp", null);
        }
    }

    public void setChecked(boolean checked) {
        if (this.optionsButton != null) {
            this.optionsButton.setVisibility(checked ? 0 : 4);
        }
    }

    public void setOnOptionsClick(OnClickListener listener) {
        if (this.optionsButton != null) {
            this.optionsButton.setOnClickListener(listener);
        }
    }

    public TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!(VERSION.SDK_INT < 21 || getBackground() == null || this.optionsButton == null)) {
            this.optionsButton.getHitRect(this.rect);
            if (this.rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
