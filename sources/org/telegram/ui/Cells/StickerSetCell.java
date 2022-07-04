package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;

public class StickerSetCell extends FrameLayout {
    private static final String LINK_PREFIX = "t.me/addstickers/";
    private CheckBox2 checkBox;
    private BackupImageView imageView;
    private boolean needDivider;
    private final int option;
    /* access modifiers changed from: private */
    public ImageView optionsButton;
    private RadialProgressView progressView;
    private Rect rect = new Rect();
    private ImageView reorderButton;
    private TLRPC.TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StickerSetCell(Context context, int option2) {
        super(context);
        Context context2 = context;
        int i = option2;
        this.option = i;
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 9.0f, 46.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.valueTextView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 32.0f, 46.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        int i2 = 5;
        addView(this.imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 13.0f, 9.0f, LocaleController.isRTL ? 13.0f : 0.0f, 0.0f));
        if (i == 2) {
            RadialProgressView radialProgressView = new RadialProgressView(getContext());
            this.progressView = radialProgressView;
            radialProgressView.setProgressColor(Theme.getColor("dialogProgressCircle"));
            this.progressView.setSize(AndroidUtilities.dp(30.0f));
            addView(this.progressView, LayoutHelper.createFrame(48, 48.0f, (!LocaleController.isRTL ? 3 : i2) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 5.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        } else if (i != 0) {
            ImageView imageView2 = new ImageView(context2);
            this.optionsButton = imageView2;
            int i3 = 0;
            imageView2.setFocusable(false);
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            if (i != 3) {
                this.optionsButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            }
            if (i == 1) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(NUM);
                this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? 3 : i2) | 16));
                ImageView imageView3 = new ImageView(context2);
                this.reorderButton = imageView3;
                imageView3.setAlpha(0.0f);
                this.reorderButton.setVisibility(8);
                this.reorderButton.setScaleType(ImageView.ScaleType.CENTER);
                this.reorderButton.setImageResource(NUM);
                this.reorderButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
                addView(this.reorderButton, LayoutHelper.createFrameRelatively(58.0f, 58.0f, 8388613));
                CheckBox2 checkBox2 = new CheckBox2(context2, 21);
                this.checkBox = checkBox2;
                checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
                this.checkBox.setDrawUnchecked(false);
                this.checkBox.setDrawBackgroundAsArc(3);
                addView(this.checkBox, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388611, 34.0f, 30.0f, 0.0f, 0.0f));
            } else if (i == 3) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(NUM);
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 3 : i2) | 48, (float) (LocaleController.isRTL ? 10 : 0), 9.0f, (float) (!LocaleController.isRTL ? 10 : i3), 0.0f));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f) + (this.needDivider ? 1 : 0), NUM));
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
            this.imageView.setImageResource(icon, Theme.getColor("windowBackgroundWhiteGrayIcon"));
            this.imageView.setVisibility(0);
            RadialProgressView radialProgressView = this.progressView;
            if (radialProgressView != null) {
                radialProgressView.setVisibility(4);
                return;
            }
            return;
        }
        this.imageView.setVisibility(4);
        RadialProgressView radialProgressView2 = this.progressView;
        if (radialProgressView2 != null) {
            radialProgressView2.setVisibility(0);
        }
    }

    public void setNeedDivider(boolean needDivider2) {
        this.needDivider = needDivider2;
    }

    public void setStickersSet(TLRPC.TL_messages_stickerSet set, boolean divider) {
        setStickersSet(set, divider, false);
    }

    public void setSearchQuery(TLRPC.TL_messages_stickerSet tlSet, String query, Theme.ResourcesProvider resourcesProvider) {
        TLRPC.StickerSet set = tlSet.set;
        int titleIndex = set.title.toLowerCase(Locale.ROOT).indexOf(query);
        if (titleIndex != -1) {
            SpannableString spannableString = new SpannableString(set.title);
            spannableString.setSpan(new ForegroundColorSpanThemable("windowBackgroundWhiteBlueText4", resourcesProvider), titleIndex, query.length() + titleIndex, 0);
            this.textView.setText(spannableString);
        }
        int linkIndex = set.short_name.toLowerCase(Locale.ROOT).indexOf(query);
        if (linkIndex != -1) {
            int linkIndex2 = linkIndex + "t.me/addstickers/".length();
            SpannableString spannableString2 = new SpannableString("t.me/addstickers/" + set.short_name);
            spannableString2.setSpan(new ForegroundColorSpanThemable("windowBackgroundWhiteBlueText4", resourcesProvider), linkIndex2, query.length() + linkIndex2, 0);
            this.valueTextView.setText(spannableString2);
        }
    }

    public void setStickersSet(TLRPC.TL_messages_stickerSet set, boolean divider, boolean groupSearch) {
        TLObject object;
        ImageLocation imageLocation;
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = set;
        this.needDivider = divider;
        this.stickersSet = tL_messages_stickerSet;
        this.imageView.setVisibility(0);
        RadialProgressView radialProgressView = this.progressView;
        if (radialProgressView != null) {
            radialProgressView.setVisibility(4);
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
        ArrayList<TLRPC.Document> documents = tL_messages_stickerSet.documents;
        if (documents == null || documents.isEmpty()) {
            this.valueTextView.setText(LocaleController.formatPluralString("Stickers", 0, new Object[0]));
            this.imageView.setImageDrawable((Drawable) null);
        } else {
            this.valueTextView.setText(LocaleController.formatPluralString("Stickers", documents.size(), new Object[0]));
            TLRPC.Document sticker = documents.get(0);
            TLObject object2 = FileLoader.getClosestPhotoSizeWithSize(tL_messages_stickerSet.set.thumbs, 90);
            if (object2 == null) {
                object = sticker;
            } else {
                object = object2;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tL_messages_stickerSet.set.thumbs, "windowBackgroundGray", 1.0f);
            if (object instanceof TLRPC.Document) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90), sticker);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) object, sticker, tL_messages_stickerSet.set.thumb_version);
            }
            if ((!(object instanceof TLRPC.Document) || !MessageObject.isAnimatedStickerDocument(sticker, true)) && !MessageObject.isVideoSticker(sticker)) {
                if (imageLocation == null || imageLocation.imageType != 1) {
                    this.imageView.setImage(imageLocation, "50_50", "webp", (Drawable) svgThumb, (Object) set);
                } else {
                    this.imageView.setImage(imageLocation, "50_50", "tgs", (Drawable) svgThumb, (Object) set);
                }
            } else if (svgThumb != null) {
                this.imageView.setImage(ImageLocation.getForDocument(sticker), "50_50", (Drawable) svgThumb, 0, (Object) set);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(sticker), "50_50", imageLocation, (String) null, 0, (Object) set);
            }
        }
        if (groupSearch) {
            TextView textView2 = this.valueTextView;
            textView2.setText("t.me/addstickers/" + tL_messages_stickerSet.set.short_name);
        }
    }

    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    public boolean isChecked() {
        int i = this.option;
        if (i == 1) {
            return this.checkBox.isChecked();
        }
        return i == 3 && this.optionsButton.getVisibility() == 0;
    }

    public void setChecked(final boolean checked, boolean animated) {
        switch (this.option) {
            case 1:
                this.checkBox.setChecked(checked, animated);
                return;
            case 3:
                float f = 0.1f;
                if (animated) {
                    this.optionsButton.animate().cancel();
                    ViewPropertyAnimator scaleX = this.optionsButton.animate().setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (!checked) {
                                StickerSetCell.this.optionsButton.setVisibility(4);
                            }
                        }

                        public void onAnimationStart(Animator animation) {
                            if (checked) {
                                StickerSetCell.this.optionsButton.setVisibility(0);
                            }
                        }
                    }).alpha(checked ? 1.0f : 0.0f).scaleX(checked ? 1.0f : 0.1f);
                    if (checked) {
                        f = 1.0f;
                    }
                    scaleX.scaleY(f).setDuration(150).start();
                    return;
                }
                this.optionsButton.setVisibility(checked ? 0 : 4);
                if (!checked) {
                    this.optionsButton.setScaleX(0.1f);
                    this.optionsButton.setScaleY(0.1f);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void setReorderable(boolean reorderable) {
        setReorderable(reorderable, true);
    }

    public void setReorderable(boolean reorderable, boolean animated) {
        if (this.option == 1) {
            float[] alphaValues = new float[2];
            float f = 0.0f;
            float f2 = 1.0f;
            int i = 0;
            alphaValues[0] = reorderable ? 1.0f : 0.0f;
            if (!reorderable) {
                f = 1.0f;
            }
            alphaValues[1] = f;
            float[] scaleValues = new float[2];
            scaleValues[0] = reorderable ? 1.0f : 0.66f;
            if (reorderable) {
                f2 = 0.66f;
            }
            scaleValues[1] = f2;
            if (animated) {
                this.reorderButton.setVisibility(0);
                this.reorderButton.animate().alpha(alphaValues[0]).scaleX(scaleValues[0]).scaleY(scaleValues[0]).setDuration(200).setInterpolator(Easings.easeOutSine).withEndAction(new StickerSetCell$$ExternalSyntheticLambda0(this, reorderable)).start();
                this.optionsButton.setVisibility(0);
                this.optionsButton.animate().alpha(alphaValues[1]).scaleX(scaleValues[1]).scaleY(scaleValues[1]).setDuration(200).setInterpolator(Easings.easeOutSine).withEndAction(new StickerSetCell$$ExternalSyntheticLambda1(this, reorderable)).start();
                return;
            }
            this.reorderButton.setVisibility(reorderable ? 0 : 8);
            this.reorderButton.setAlpha(alphaValues[0]);
            this.reorderButton.setScaleX(scaleValues[0]);
            this.reorderButton.setScaleY(scaleValues[0]);
            ImageView imageView2 = this.optionsButton;
            if (reorderable) {
                i = 8;
            }
            imageView2.setVisibility(i);
            this.optionsButton.setAlpha(alphaValues[1]);
            this.optionsButton.setScaleX(scaleValues[1]);
            this.optionsButton.setScaleY(scaleValues[1]);
        }
    }

    /* renamed from: lambda$setReorderable$0$org-telegram-ui-Cells-StickerSetCell  reason: not valid java name */
    public /* synthetic */ void m2821lambda$setReorderable$0$orgtelegramuiCellsStickerSetCell(boolean reorderable) {
        if (!reorderable) {
            this.reorderButton.setVisibility(8);
        }
    }

    /* renamed from: lambda$setReorderable$1$org-telegram-ui-Cells-StickerSetCell  reason: not valid java name */
    public /* synthetic */ void m2822lambda$setReorderable$1$orgtelegramuiCellsStickerSetCell(boolean reorderable) {
        if (reorderable) {
            this.optionsButton.setVisibility(8);
        }
    }

    public void setOnReorderButtonTouchListener(View.OnTouchListener listener) {
        this.reorderButton.setOnTouchListener(listener);
    }

    public void setOnOptionsClick(View.OnClickListener listener) {
        ImageView imageView2 = this.optionsButton;
        if (imageView2 != null) {
            imageView2.setOnClickListener(listener);
        }
    }

    public TLRPC.TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    public boolean onTouchEvent(MotionEvent event) {
        ImageView imageView2;
        if (!(Build.VERSION.SDK_INT < 21 || getBackground() == null || (imageView2 = this.optionsButton) == null)) {
            imageView2.getHitRect(this.rect);
            if (this.rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(71.0f), (float) (getHeight() - 1), (float) ((getWidth() - getPaddingRight()) - (LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : 0)), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && checkBox2.isChecked()) {
            info.setCheckable(true);
            info.setChecked(true);
        }
    }
}
