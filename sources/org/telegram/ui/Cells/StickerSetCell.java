package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
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
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.RadialProgressView;
/* loaded from: classes3.dex */
public class StickerSetCell extends FrameLayout {
    private TextView addButtonView;
    private CheckBox2 checkBox;
    private boolean emojis;
    private BackupImageView imageView;
    private boolean needDivider;
    private final int option;
    private ImageView optionsButton;
    private PremiumButtonView premiumButtonView;
    private RadialProgressView progressView;
    private Rect rect;
    private TextView removeButtonView;
    private ImageView reorderButton;
    private FrameLayout sideButtons;
    private AnimatorSet stateAnimator;
    private TLRPC$TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    protected void onAddButtonClick() {
    }

    protected void onPremiumButtonClick() {
    }

    protected void onRemoveButtonClick() {
    }

    public StickerSetCell(Context context, int i) {
        this(context, null, i);
    }

    public StickerSetCell(Context context, Theme.ResourcesProvider resourcesProvider, int i) {
        super(context);
        this.rect = new Rect();
        this.option = i;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        BackupImageView backupImageView2 = this.imageView;
        boolean z = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(40, 40.0f, (z ? 5 : 3) | 48, z ? 0.0f : 13.0f, 9.0f, z ? 13.0f : 0.0f, 0.0f));
        if (i == 2) {
            RadialProgressView radialProgressView = new RadialProgressView(getContext());
            this.progressView = radialProgressView;
            radialProgressView.setProgressColor(Theme.getColor("dialogProgressCircle"));
            this.progressView.setSize(AndroidUtilities.dp(30.0f));
            RadialProgressView radialProgressView2 = this.progressView;
            boolean z2 = LocaleController.isRTL;
            addView(radialProgressView2, LayoutHelper.createFrame(48, 48.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 12.0f, 5.0f, z2 ? 12.0f : 0.0f, 0.0f));
        } else if (i != 0) {
            ImageView imageView = new ImageView(context);
            this.optionsButton = imageView;
            imageView.setFocusable(false);
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            if (i != 3) {
                this.optionsButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            }
            if (i == 1) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(R.drawable.msg_actions);
                this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? 3 : 5) | 16));
                ImageView imageView2 = new ImageView(context);
                this.reorderButton = imageView2;
                imageView2.setAlpha(0.0f);
                this.reorderButton.setVisibility(8);
                this.reorderButton.setScaleType(ImageView.ScaleType.CENTER);
                this.reorderButton.setImageResource(R.drawable.list_reorder);
                this.reorderButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
                addView(this.reorderButton, LayoutHelper.createFrameRelatively(58.0f, 58.0f, 8388613));
                CheckBox2 checkBox2 = new CheckBox2(context, 21);
                this.checkBox = checkBox2;
                checkBox2.setColor(null, "windowBackgroundWhite", "checkboxCheck");
                this.checkBox.setDrawUnchecked(false);
                this.checkBox.setDrawBackgroundAsArc(3);
                addView(this.checkBox, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388611, 34.0f, 30.0f, 0.0f, 0.0f));
            } else if (i == 3) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(R.drawable.floating_check);
                ImageView imageView3 = this.optionsButton;
                boolean z3 = LocaleController.isRTL;
                addView(imageView3, LayoutHelper.createFrame(40, 40.0f, (z3 ? 3 : 5) | 48, z3 ? 10 : 0, 9.0f, z3 ? 0 : 10, 0.0f));
            }
        }
        this.sideButtons = new FrameLayout(getContext());
        TextView textView = new TextView(context);
        this.addButtonView = textView;
        textView.setTextSize(1, 14.0f);
        this.addButtonView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addButtonView.setText(LocaleController.getString("Add", R.string.Add));
        this.addButtonView.setTextColor(Theme.getColor("featuredStickers_buttonText", resourcesProvider));
        this.addButtonView.setBackground(Theme.AdaptiveRipple.createRect(Theme.getColor("featuredStickers_addButton", resourcesProvider), Theme.getColor("featuredStickers_addButtonPressed", resourcesProvider), 4.0f));
        this.addButtonView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
        this.addButtonView.setGravity(17);
        this.addButtonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StickerSetCell.this.lambda$new$0(view);
            }
        });
        this.sideButtons.addView(this.addButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, (LocaleController.isRTL ? 3 : 5) | 16));
        TextView textView2 = new TextView(context);
        this.removeButtonView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.removeButtonView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.removeButtonView.setText(LocaleController.getString("StickersRemove", R.string.StickersRemove));
        this.removeButtonView.setTextColor(Theme.getColor("featuredStickers_removeButtonText", resourcesProvider));
        this.removeButtonView.setBackground(Theme.AdaptiveRipple.createRect(0, Theme.getColor("featuredStickers_addButton", resourcesProvider) & NUM, 4.0f));
        this.removeButtonView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        this.removeButtonView.setGravity(17);
        this.removeButtonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StickerSetCell.this.lambda$new$1(view);
            }
        });
        this.sideButtons.addView(this.removeButtonView, LayoutHelper.createFrameRelatively(-2.0f, 32.0f, (LocaleController.isRTL ? 3 : 5) | 16, 0.0f, -2.0f, 0.0f, 0.0f));
        PremiumButtonView premiumButtonView = new PremiumButtonView(context, AndroidUtilities.dp(4.0f), false);
        this.premiumButtonView = premiumButtonView;
        premiumButtonView.setIcon(R.raw.unlock_icon);
        this.premiumButtonView.setButton(LocaleController.getString("Unlock", R.string.Unlock), new View.OnClickListener() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StickerSetCell.this.lambda$new$2(view);
            }
        });
        try {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.premiumButtonView.getIconView().getLayoutParams();
            marginLayoutParams.leftMargin = AndroidUtilities.dp(1.0f);
            marginLayoutParams.topMargin = AndroidUtilities.dp(1.0f);
            int dp = AndroidUtilities.dp(20.0f);
            marginLayoutParams.height = dp;
            marginLayoutParams.width = dp;
            ((ViewGroup.MarginLayoutParams) this.premiumButtonView.getTextView().getLayoutParams()).leftMargin = AndroidUtilities.dp(3.0f);
            this.premiumButtonView.getChildAt(0).setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        } catch (Exception unused) {
        }
        this.sideButtons.addView(this.premiumButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, (LocaleController.isRTL ? 3 : 5) | 16));
        this.sideButtons.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        addView(this.sideButtons, LayoutHelper.createFrame(-2, -1.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 0.0f, 0.0f, 0.0f));
        this.sideButtons.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StickerSetCell.this.lambda$new$3(view);
            }
        });
        TextView textView3 = new TextView(context);
        this.textView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 9.0f, 70.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.valueTextView = textView4;
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.valueTextView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 32.0f, 70.0f, 0.0f));
        updateButtonState(0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        onAddButtonClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        onRemoveButtonClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        onPremiumButtonClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        if (this.premiumButtonView.getVisibility() == 0 && this.premiumButtonView.isEnabled()) {
            this.premiumButtonView.performClick();
        } else if (this.addButtonView.getVisibility() == 0 && this.addButtonView.isEnabled()) {
            this.addButtonView.performClick();
        } else if (this.removeButtonView.getVisibility() != 0 || !this.removeButtonView.isEnabled()) {
        } else {
            this.removeButtonView.performClick();
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setNeedDivider(boolean z) {
        this.needDivider = z;
    }

    public void setStickersSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z) {
        setStickersSet(tLRPC$TL_messages_stickerSet, z, false);
    }

    public void setSearchQuery(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, String str, Theme.ResourcesProvider resourcesProvider) {
        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
        String str2 = tLRPC$StickerSet.title;
        Locale locale = Locale.ROOT;
        int indexOf = str2.toLowerCase(locale).indexOf(str);
        if (indexOf != -1) {
            SpannableString spannableString = new SpannableString(tLRPC$StickerSet.title);
            spannableString.setSpan(new ForegroundColorSpanThemable("windowBackgroundWhiteBlueText4", resourcesProvider), indexOf, str.length() + indexOf, 0);
            this.textView.setText(spannableString);
        }
        int indexOf2 = tLRPC$StickerSet.short_name.toLowerCase(locale).indexOf(str);
        if (indexOf2 != -1) {
            String str3 = tLRPC$StickerSet.emojis ? "t.me/addemoji/" : "t.me/addstickers/";
            int length = indexOf2 + str3.length();
            SpannableString spannableString2 = new SpannableString(str3 + tLRPC$StickerSet.short_name);
            spannableString2.setSpan(new ForegroundColorSpanThemable("windowBackgroundWhiteBlueText4", resourcesProvider), length, str.length() + length, 0);
            this.valueTextView.setText(spannableString2);
        }
    }

    @SuppressLint({"SetTextI18n"})
    public void setStickersSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z, boolean z2) {
        ImageLocation forSticker;
        this.needDivider = z;
        this.stickersSet = tLRPC$TL_messages_stickerSet;
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
        boolean z3 = tLRPC$TL_messages_stickerSet.set.emojis;
        this.emojis = z3;
        int i = 8;
        this.sideButtons.setVisibility(z3 ? 0 : 8);
        ImageView imageView = this.optionsButton;
        if (!this.emojis) {
            i = 0;
        }
        imageView.setVisibility(i);
        ArrayList<TLRPC$Document> arrayList = tLRPC$TL_messages_stickerSet.documents;
        TLRPC$Document tLRPC$Document = null;
        String str = "EmojiCount";
        if (arrayList != null && !arrayList.isEmpty()) {
            TextView textView = this.valueTextView;
            if (!this.emojis) {
                str = "Stickers";
            }
            textView.setText(LocaleController.formatPluralString(str, arrayList.size(), new Object[0]));
            int i2 = 0;
            while (true) {
                if (i2 < arrayList.size()) {
                    TLRPC$Document tLRPC$Document2 = arrayList.get(i2);
                    if (tLRPC$Document2 != null && tLRPC$Document2.id == tLRPC$TL_messages_stickerSet.set.thumb_document_id) {
                        tLRPC$Document = tLRPC$Document2;
                        break;
                    }
                    i2++;
                } else {
                    break;
                }
            }
            if (tLRPC$Document == null) {
                tLRPC$Document = arrayList.get(0);
            }
            TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_messages_stickerSet.set.thumbs, 90);
            if (closestPhotoSizeWithSize == null) {
                closestPhotoSizeWithSize = tLRPC$Document;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$TL_messages_stickerSet.set.thumbs, "windowBackgroundGray", 1.0f);
            boolean z4 = closestPhotoSizeWithSize instanceof TLRPC$Document;
            if (z4) {
                forSticker = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
            } else {
                forSticker = ImageLocation.getForSticker((TLRPC$PhotoSize) closestPhotoSizeWithSize, tLRPC$Document, tLRPC$TL_messages_stickerSet.set.thumb_version);
            }
            ImageLocation imageLocation = forSticker;
            if ((!z4 || !MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) && !MessageObject.isVideoSticker(tLRPC$Document)) {
                if (imageLocation != null && imageLocation.imageType == 1) {
                    this.imageView.setImage(imageLocation, "50_50", "tgs", svgThumb, tLRPC$TL_messages_stickerSet);
                } else {
                    this.imageView.setImage(imageLocation, "50_50", "webp", svgThumb, tLRPC$TL_messages_stickerSet);
                }
            } else if (svgThumb != null) {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", svgThumb, 0, tLRPC$TL_messages_stickerSet);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", imageLocation, (String) null, 0, tLRPC$TL_messages_stickerSet);
            }
        } else {
            TextView textView2 = this.valueTextView;
            if (!tLRPC$TL_messages_stickerSet.set.emojis) {
                str = "Stickers";
            }
            textView2.setText(LocaleController.formatPluralString(str, 0, new Object[0]));
            this.imageView.setImageDrawable(null);
        }
        if (z2) {
            TextView textView3 = this.valueTextView;
            StringBuilder sb = new StringBuilder();
            sb.append(tLRPC$TL_messages_stickerSet.set.emojis ? "t.me/addemoji/" : "t.me/addstickers/");
            sb.append(tLRPC$TL_messages_stickerSet.set.short_name);
            textView3.setText(sb.toString());
        }
    }

    public void setChecked(boolean z) {
        setChecked(z, true);
    }

    public boolean isChecked() {
        int i = this.option;
        if (i == 1) {
            return this.checkBox.isChecked();
        }
        return i == 3 ? this.optionsButton.getVisibility() == 0 : this.emojis && this.sideButtons.getVisibility() == 0;
    }

    public void setChecked(final boolean z, boolean z2) {
        int i = this.option;
        if (i == 1) {
            this.checkBox.setChecked(z, z2);
            return;
        }
        float f = 0.0f;
        int i2 = 0;
        float f2 = 1.0f;
        if (this.emojis) {
            if (z2) {
                this.sideButtons.animate().cancel();
                ViewPropertyAnimator listener = this.sideButtons.animate().setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.StickerSetCell.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            StickerSetCell.this.sideButtons.setVisibility(4);
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        if (z) {
                            StickerSetCell.this.sideButtons.setVisibility(0);
                        }
                    }
                });
                if (z) {
                    f = 1.0f;
                }
                ViewPropertyAnimator scaleX = listener.alpha(f).scaleX(z ? 1.0f : 0.1f);
                if (!z) {
                    f2 = 0.1f;
                }
                scaleX.scaleY(f2).setDuration(150L).start();
                return;
            }
            FrameLayout frameLayout = this.sideButtons;
            if (!z) {
                i2 = 4;
            }
            frameLayout.setVisibility(i2);
            if (z) {
                return;
            }
            this.sideButtons.setScaleX(0.1f);
            this.sideButtons.setScaleY(0.1f);
        } else if (i != 3) {
        } else {
            if (z2) {
                this.optionsButton.animate().cancel();
                ViewPropertyAnimator listener2 = this.optionsButton.animate().setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.StickerSetCell.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            StickerSetCell.this.optionsButton.setVisibility(4);
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        if (z) {
                            StickerSetCell.this.optionsButton.setVisibility(0);
                        }
                    }
                });
                if (z) {
                    f = 1.0f;
                }
                ViewPropertyAnimator scaleX2 = listener2.alpha(f).scaleX(z ? 1.0f : 0.1f);
                if (!z) {
                    f2 = 0.1f;
                }
                scaleX2.scaleY(f2).setDuration(150L).start();
                return;
            }
            ImageView imageView = this.optionsButton;
            if (!z) {
                i2 = 4;
            }
            imageView.setVisibility(i2);
            if (z) {
                return;
            }
            this.optionsButton.setScaleX(0.1f);
            this.optionsButton.setScaleY(0.1f);
        }
    }

    public void setReorderable(boolean z) {
        setReorderable(z, true);
    }

    public void setReorderable(final boolean z, boolean z2) {
        if (this.option == 1) {
            float[] fArr = new float[2];
            float f = 0.0f;
            float f2 = 1.0f;
            int i = 0;
            fArr[0] = z ? 1.0f : 0.0f;
            if (!z) {
                f = 1.0f;
            }
            fArr[1] = f;
            float[] fArr2 = new float[2];
            fArr2[0] = z ? 1.0f : 0.66f;
            if (z) {
                f2 = 0.66f;
            }
            fArr2[1] = f2;
            if (z2) {
                this.reorderButton.setVisibility(0);
                ViewPropertyAnimator duration = this.reorderButton.animate().alpha(fArr[0]).scaleX(fArr2[0]).scaleY(fArr2[0]).setDuration(200L);
                Interpolator interpolator = Easings.easeOutSine;
                duration.setInterpolator(interpolator).withEndAction(new Runnable() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        StickerSetCell.this.lambda$setReorderable$4(z);
                    }
                }).start();
                if (this.emojis) {
                    this.sideButtons.setVisibility(0);
                    this.sideButtons.animate().alpha(fArr[1]).scaleX(fArr2[1]).scaleY(fArr2[1]).setDuration(200L).setInterpolator(interpolator).withEndAction(new Runnable() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda8
                        @Override // java.lang.Runnable
                        public final void run() {
                            StickerSetCell.this.lambda$setReorderable$5(z);
                        }
                    }).start();
                    return;
                }
                this.optionsButton.setVisibility(0);
                this.optionsButton.animate().alpha(fArr[1]).scaleX(fArr2[1]).scaleY(fArr2[1]).setDuration(200L).setInterpolator(interpolator).withEndAction(new Runnable() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        StickerSetCell.this.lambda$setReorderable$6(z);
                    }
                }).start();
                return;
            }
            this.reorderButton.setVisibility(z ? 0 : 8);
            this.reorderButton.setAlpha(fArr[0]);
            this.reorderButton.setScaleX(fArr2[0]);
            this.reorderButton.setScaleY(fArr2[0]);
            if (this.emojis) {
                FrameLayout frameLayout = this.sideButtons;
                if (z) {
                    i = 8;
                }
                frameLayout.setVisibility(i);
                this.sideButtons.setAlpha(fArr[1]);
                this.sideButtons.setScaleX(fArr2[1]);
                this.sideButtons.setScaleY(fArr2[1]);
                return;
            }
            ImageView imageView = this.optionsButton;
            if (z) {
                i = 8;
            }
            imageView.setVisibility(i);
            this.optionsButton.setAlpha(fArr[1]);
            this.optionsButton.setScaleX(fArr2[1]);
            this.optionsButton.setScaleY(fArr2[1]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setReorderable$4(boolean z) {
        if (!z) {
            this.reorderButton.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setReorderable$5(boolean z) {
        if (z) {
            this.sideButtons.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setReorderable$6(boolean z) {
        if (z) {
            this.optionsButton.setVisibility(8);
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void setOnReorderButtonTouchListener(View.OnTouchListener onTouchListener) {
        this.reorderButton.setOnTouchListener(onTouchListener);
    }

    public void setOnOptionsClick(View.OnClickListener onClickListener) {
        ImageView imageView = this.optionsButton;
        if (imageView == null) {
            return;
        }
        imageView.setOnClickListener(onClickListener);
    }

    public TLRPC$TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        FrameLayout frameLayout;
        ImageView imageView;
        int i = Build.VERSION.SDK_INT;
        if (i >= 21 && getBackground() != null && (imageView = this.optionsButton) != null) {
            imageView.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
        }
        if (i >= 21 && getBackground() != null && this.emojis && (frameLayout = this.sideButtons) != null) {
            frameLayout.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(71.0f), getHeight() - 1, (getWidth() - getPaddingRight()) - (LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : 0), getHeight() - 1, Theme.dividerPaint);
        }
    }

    public void updateRightMargin() {
        this.sideButtons.measure(View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), NUM));
        int measuredWidth = this.sideButtons.getMeasuredWidth();
        if (LocaleController.isRTL) {
            ((ViewGroup.MarginLayoutParams) this.textView.getLayoutParams()).leftMargin = measuredWidth;
            ((ViewGroup.MarginLayoutParams) this.valueTextView.getLayoutParams()).leftMargin = measuredWidth;
            return;
        }
        ((ViewGroup.MarginLayoutParams) this.textView.getLayoutParams()).rightMargin = measuredWidth;
        ((ViewGroup.MarginLayoutParams) this.valueTextView.getLayoutParams()).rightMargin = measuredWidth;
    }

    public void updateButtonState(final int i, boolean z) {
        AnimatorSet animatorSet = this.stateAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.stateAnimator = null;
        }
        if (i == 1) {
            this.premiumButtonView.setButton(LocaleController.getString("Unlock", R.string.Unlock), new View.OnClickListener() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    StickerSetCell.this.lambda$updateButtonState$7(view);
                }
            });
        } else if (i == 2) {
            this.premiumButtonView.setButton(LocaleController.getString("Restore", R.string.Restore), new View.OnClickListener() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    StickerSetCell.this.lambda$updateButtonState$8(view);
                }
            });
        }
        int i2 = 0;
        this.premiumButtonView.setEnabled(i == 1 || i == 2);
        this.addButtonView.setEnabled(i == 3);
        this.removeButtonView.setEnabled(i == 4);
        float f = 0.0f;
        float f2 = 0.6f;
        if (z) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.stateAnimator = animatorSet2;
            Animator[] animatorArr = new Animator[9];
            PremiumButtonView premiumButtonView = this.premiumButtonView;
            Property property = FrameLayout.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = (i == 1 || i == 2) ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(premiumButtonView, property, fArr);
            PremiumButtonView premiumButtonView2 = this.premiumButtonView;
            Property property2 = FrameLayout.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = (i == 1 || i == 2) ? 1.0f : 0.6f;
            animatorArr[1] = ObjectAnimator.ofFloat(premiumButtonView2, property2, fArr2);
            PremiumButtonView premiumButtonView3 = this.premiumButtonView;
            Property property3 = FrameLayout.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = (i == 1 || i == 2) ? 1.0f : 0.6f;
            animatorArr[2] = ObjectAnimator.ofFloat(premiumButtonView3, property3, fArr3);
            TextView textView = this.addButtonView;
            Property property4 = FrameLayout.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = i == 3 ? 1.0f : 0.0f;
            animatorArr[3] = ObjectAnimator.ofFloat(textView, property4, fArr4);
            TextView textView2 = this.addButtonView;
            Property property5 = FrameLayout.SCALE_X;
            float[] fArr5 = new float[1];
            fArr5[0] = i == 3 ? 1.0f : 0.6f;
            animatorArr[4] = ObjectAnimator.ofFloat(textView2, property5, fArr5);
            TextView textView3 = this.addButtonView;
            Property property6 = FrameLayout.SCALE_Y;
            float[] fArr6 = new float[1];
            fArr6[0] = i == 3 ? 1.0f : 0.6f;
            animatorArr[5] = ObjectAnimator.ofFloat(textView3, property6, fArr6);
            TextView textView4 = this.removeButtonView;
            Property property7 = FrameLayout.ALPHA;
            float[] fArr7 = new float[1];
            if (i == 4) {
                f = 1.0f;
            }
            fArr7[0] = f;
            animatorArr[6] = ObjectAnimator.ofFloat(textView4, property7, fArr7);
            TextView textView5 = this.removeButtonView;
            Property property8 = FrameLayout.SCALE_X;
            float[] fArr8 = new float[1];
            fArr8[0] = i == 4 ? 1.0f : 0.6f;
            animatorArr[7] = ObjectAnimator.ofFloat(textView5, property8, fArr8);
            TextView textView6 = this.removeButtonView;
            Property property9 = FrameLayout.SCALE_Y;
            float[] fArr9 = new float[1];
            if (i == 4) {
                f2 = 1.0f;
            }
            fArr9[0] = f2;
            animatorArr[8] = ObjectAnimator.ofFloat(textView6, property9, fArr9);
            animatorSet2.playTogether(animatorArr);
            this.stateAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.StickerSetCell.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    StickerSetCell.this.premiumButtonView.setVisibility(0);
                    StickerSetCell.this.addButtonView.setVisibility(0);
                    StickerSetCell.this.removeButtonView.setVisibility(0);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    PremiumButtonView premiumButtonView4 = StickerSetCell.this.premiumButtonView;
                    int i3 = i;
                    int i4 = 8;
                    premiumButtonView4.setVisibility((i3 == 1 || i3 == 2) ? 0 : 8);
                    StickerSetCell.this.addButtonView.setVisibility(i == 3 ? 0 : 8);
                    TextView textView7 = StickerSetCell.this.removeButtonView;
                    if (i == 4) {
                        i4 = 0;
                    }
                    textView7.setVisibility(i4);
                    StickerSetCell.this.updateRightMargin();
                }
            });
            this.stateAnimator.setDuration(250L);
            this.stateAnimator.setInterpolator(new OvershootInterpolator(1.02f));
            this.stateAnimator.start();
            return;
        }
        this.premiumButtonView.setAlpha((i == 1 || i == 2) ? 1.0f : 0.0f);
        this.premiumButtonView.setScaleX((i == 1 || i == 2) ? 1.0f : 0.6f);
        this.premiumButtonView.setScaleY((i == 1 || i == 2) ? 1.0f : 0.6f);
        this.premiumButtonView.setVisibility((i == 1 || i == 2) ? 0 : 8);
        this.addButtonView.setAlpha(i == 3 ? 1.0f : 0.0f);
        this.addButtonView.setScaleX(i == 3 ? 1.0f : 0.6f);
        this.addButtonView.setScaleY(i == 3 ? 1.0f : 0.6f);
        this.addButtonView.setVisibility(i == 3 ? 0 : 8);
        TextView textView7 = this.removeButtonView;
        if (i == 4) {
            f = 1.0f;
        }
        textView7.setAlpha(f);
        this.removeButtonView.setScaleX(i == 4 ? 1.0f : 0.6f);
        TextView textView8 = this.removeButtonView;
        if (i == 4) {
            f2 = 1.0f;
        }
        textView8.setScaleY(f2);
        TextView textView9 = this.removeButtonView;
        if (i != 4) {
            i2 = 8;
        }
        textView9.setVisibility(i2);
        updateRightMargin();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButtonState$7(View view) {
        onPremiumButtonClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButtonState$8(View view) {
        onPremiumButtonClick();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 == null || !checkBox2.isChecked()) {
            return;
        }
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(true);
    }
}
