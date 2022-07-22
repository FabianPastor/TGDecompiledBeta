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
import android.graphics.drawable.Drawable;
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

public class StickerSetCell extends FrameLayout {
    /* access modifiers changed from: private */
    public TextView addButtonView;
    private CheckBox2 checkBox;
    private boolean emojis;
    private BackupImageView imageView;
    private boolean needDivider;
    private final int option;
    /* access modifiers changed from: private */
    public ImageView optionsButton;
    /* access modifiers changed from: private */
    public PremiumButtonView premiumButtonView;
    private RadialProgressView progressView;
    private Rect rect;
    /* access modifiers changed from: private */
    public TextView removeButtonView;
    private ImageView reorderButton;
    /* access modifiers changed from: private */
    public FrameLayout sideButtons;
    private AnimatorSet stateAnimator;
    private TLRPC$TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    /* access modifiers changed from: protected */
    public void onAddButtonClick() {
    }

    /* access modifiers changed from: protected */
    public void onPremiumButtonClick() {
    }

    /* access modifiers changed from: protected */
    public void onRemoveButtonClick() {
    }

    public StickerSetCell(Context context, int i) {
        this(context, (Theme.ResourcesProvider) null, i);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StickerSetCell(Context context, Theme.ResourcesProvider resourcesProvider, int i) {
        super(context);
        Context context2 = context;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        int i2 = i;
        this.rect = new Rect();
        this.option = i2;
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
        BackupImageView backupImageView2 = this.imageView;
        boolean z = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(40, 40.0f, (z ? 5 : 3) | 48, z ? 0.0f : 13.0f, 9.0f, z ? 13.0f : 0.0f, 0.0f));
        if (i2 == 2) {
            RadialProgressView radialProgressView = new RadialProgressView(getContext());
            this.progressView = radialProgressView;
            radialProgressView.setProgressColor(Theme.getColor("dialogProgressCircle"));
            this.progressView.setSize(AndroidUtilities.dp(30.0f));
            RadialProgressView radialProgressView2 = this.progressView;
            boolean z2 = LocaleController.isRTL;
            addView(radialProgressView2, LayoutHelper.createFrame(48, 48.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 12.0f, 5.0f, z2 ? 12.0f : 0.0f, 0.0f));
        } else if (i2 != 0) {
            ImageView imageView2 = new ImageView(context2);
            this.optionsButton = imageView2;
            imageView2.setFocusable(false);
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            if (i2 != 3) {
                this.optionsButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            }
            if (i2 == 1) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(NUM);
                this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? 3 : 5) | 16));
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
            } else if (i2 == 3) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(NUM);
                ImageView imageView4 = this.optionsButton;
                boolean z3 = LocaleController.isRTL;
                addView(imageView4, LayoutHelper.createFrame(40, 40.0f, (z3 ? 3 : 5) | 48, (float) (z3 ? 10 : 0), 9.0f, (float) (z3 ? 0 : 10), 0.0f));
            }
        }
        this.sideButtons = new FrameLayout(getContext());
        TextView textView4 = new TextView(context2);
        this.addButtonView = textView4;
        textView4.setTextSize(1, 14.0f);
        this.addButtonView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addButtonView.setText(LocaleController.getString("Add", NUM));
        this.addButtonView.setTextColor(Theme.getColor("featuredStickers_buttonText", resourcesProvider2));
        this.addButtonView.setBackground(Theme.AdaptiveRipple.createRect(Theme.getColor("featuredStickers_addButton", resourcesProvider2), Theme.getColor("featuredStickers_addButtonPressed", resourcesProvider2), 4.0f));
        this.addButtonView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
        this.addButtonView.setGravity(17);
        this.addButtonView.setOnClickListener(new StickerSetCell$$ExternalSyntheticLambda4(this));
        this.sideButtons.addView(this.addButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, (LocaleController.isRTL ? 3 : 5) | 16));
        TextView textView5 = new TextView(context2);
        this.removeButtonView = textView5;
        textView5.setTextSize(1, 14.0f);
        this.removeButtonView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.removeButtonView.setText(LocaleController.getString("StickersRemove", NUM));
        this.removeButtonView.setTextColor(Theme.getColor("featuredStickers_addButton", resourcesProvider2));
        this.removeButtonView.setBackground(Theme.AdaptiveRipple.createRect(0, Theme.getColor("featuredStickers_addButton", resourcesProvider2) & NUM, 4.0f));
        this.removeButtonView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        this.removeButtonView.setGravity(17);
        this.removeButtonView.setOnClickListener(new StickerSetCell$$ExternalSyntheticLambda1(this));
        this.sideButtons.addView(this.removeButtonView, LayoutHelper.createFrameRelatively(-2.0f, 32.0f, (LocaleController.isRTL ? 3 : 5) | 16, 0.0f, -2.0f, 0.0f, 0.0f));
        PremiumButtonView premiumButtonView2 = new PremiumButtonView(context2, AndroidUtilities.dp(4.0f), false);
        this.premiumButtonView = premiumButtonView2;
        premiumButtonView2.setIcon(NUM);
        this.premiumButtonView.setButton(LocaleController.getString("Unlock", NUM), new StickerSetCell$$ExternalSyntheticLambda5(this));
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
        this.sideButtons.setOnClickListener(new StickerSetCell$$ExternalSyntheticLambda2(this));
        updateButtonState(0, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        onAddButtonClick();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        onRemoveButtonClick();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        onPremiumButtonClick();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        if (this.premiumButtonView.getVisibility() == 0) {
            onPremiumButtonClick();
        } else if (this.addButtonView.getVisibility() == 0) {
            onAddButtonClick();
        } else if (this.removeButtonView.getVisibility() == 0) {
            onRemoveButtonClick();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
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
        ImageLocation imageLocation;
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
        ImageView imageView2 = this.optionsButton;
        if (!this.emojis) {
            i = 0;
        }
        imageView2.setVisibility(i);
        ArrayList<TLRPC$Document> arrayList = tLRPC$TL_messages_stickerSet.documents;
        String str = "EmojiCount";
        if (arrayList == null || arrayList.isEmpty()) {
            TextView textView2 = this.valueTextView;
            if (!tLRPC$TL_messages_stickerSet.set.emojis) {
                str = "Stickers";
            }
            textView2.setText(LocaleController.formatPluralString(str, 0, new Object[0]));
            this.imageView.setImageDrawable((Drawable) null);
        } else {
            TextView textView3 = this.valueTextView;
            if (!this.emojis) {
                str = "Stickers";
            }
            textView3.setText(LocaleController.formatPluralString(str, arrayList.size(), new Object[0]));
            TLRPC$Document tLRPC$Document = arrayList.get(0);
            TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_messages_stickerSet.set.thumbs, 90);
            if (closestPhotoSizeWithSize == null) {
                closestPhotoSizeWithSize = tLRPC$Document;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$TL_messages_stickerSet.set.thumbs, "windowBackgroundGray", 1.0f);
            boolean z4 = closestPhotoSizeWithSize instanceof TLRPC$Document;
            if (z4) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) closestPhotoSizeWithSize, tLRPC$Document, tLRPC$TL_messages_stickerSet.set.thumb_version);
            }
            if ((!z4 || !MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) && !MessageObject.isVideoSticker(tLRPC$Document)) {
                if (imageLocation == null || imageLocation.imageType != 1) {
                    this.imageView.setImage(imageLocation, "50_50", "webp", (Drawable) svgThumb, (Object) tLRPC$TL_messages_stickerSet);
                } else {
                    this.imageView.setImage(imageLocation, "50_50", "tgs", (Drawable) svgThumb, (Object) tLRPC$TL_messages_stickerSet);
                }
            } else if (svgThumb != null) {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", (Drawable) svgThumb, 0, (Object) tLRPC$TL_messages_stickerSet);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", imageLocation, (String) null, 0, (Object) tLRPC$TL_messages_stickerSet);
            }
        }
        if (z2) {
            TextView textView4 = this.valueTextView;
            StringBuilder sb = new StringBuilder();
            sb.append(tLRPC$TL_messages_stickerSet.set.emojis ? "t.me/addemoji/" : "t.me/addstickers/");
            sb.append(tLRPC$TL_messages_stickerSet.set.short_name);
            textView4.setText(sb.toString());
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
        if (i == 3) {
            if (this.optionsButton.getVisibility() == 0) {
                return true;
            }
            return false;
        } else if (!this.emojis || this.sideButtons.getVisibility() != 0) {
            return false;
        } else {
            return true;
        }
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
                ViewPropertyAnimator listener = this.sideButtons.animate().setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            StickerSetCell.this.sideButtons.setVisibility(4);
                        }
                    }

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
                scaleX.scaleY(f2).setDuration(150).start();
                return;
            }
            FrameLayout frameLayout = this.sideButtons;
            if (!z) {
                i2 = 4;
            }
            frameLayout.setVisibility(i2);
            if (!z) {
                this.sideButtons.setScaleX(0.1f);
                this.sideButtons.setScaleY(0.1f);
            }
        } else if (i != 3) {
        } else {
            if (z2) {
                this.optionsButton.animate().cancel();
                ViewPropertyAnimator listener2 = this.optionsButton.animate().setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            StickerSetCell.this.optionsButton.setVisibility(4);
                        }
                    }

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
                scaleX2.scaleY(f2).setDuration(150).start();
                return;
            }
            ImageView imageView2 = this.optionsButton;
            if (!z) {
                i2 = 4;
            }
            imageView2.setVisibility(i2);
            if (!z) {
                this.optionsButton.setScaleX(0.1f);
                this.optionsButton.setScaleY(0.1f);
            }
        }
    }

    public void setReorderable(boolean z) {
        setReorderable(z, true);
    }

    public void setReorderable(boolean z, boolean z2) {
        TLRPC$StickerSet tLRPC$StickerSet;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickersSet;
        int i = 0;
        if (!(tLRPC$TL_messages_stickerSet == null || (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set) == null || !tLRPC$StickerSet.emojis)) {
            z = false;
        }
        if (this.option == 1) {
            float[] fArr = new float[2];
            float f = 0.0f;
            float f2 = 1.0f;
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
                ViewPropertyAnimator duration = this.reorderButton.animate().alpha(fArr[0]).scaleX(fArr2[0]).scaleY(fArr2[0]).setDuration(200);
                Interpolator interpolator = Easings.easeOutSine;
                duration.setInterpolator(interpolator).withEndAction(new StickerSetCell$$ExternalSyntheticLambda7(this, z)).start();
                if (this.emojis) {
                    this.sideButtons.setVisibility(0);
                    this.sideButtons.animate().alpha(fArr[1]).scaleX(fArr2[1]).scaleY(fArr2[1]).setDuration(200).setInterpolator(interpolator).withEndAction(new StickerSetCell$$ExternalSyntheticLambda8(this, z)).start();
                    return;
                }
                this.optionsButton.setVisibility(0);
                this.optionsButton.animate().alpha(fArr[1]).scaleX(fArr2[1]).scaleY(fArr2[1]).setDuration(200).setInterpolator(interpolator).withEndAction(new StickerSetCell$$ExternalSyntheticLambda6(this, z)).start();
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
            ImageView imageView2 = this.optionsButton;
            if (z) {
                i = 8;
            }
            imageView2.setVisibility(i);
            this.optionsButton.setAlpha(fArr[1]);
            this.optionsButton.setScaleX(fArr2[1]);
            this.optionsButton.setScaleY(fArr2[1]);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setReorderable$4(boolean z) {
        if (!z) {
            this.reorderButton.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setReorderable$5(boolean z) {
        if (z) {
            this.sideButtons.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
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
        ImageView imageView2 = this.optionsButton;
        if (imageView2 != null) {
            imageView2.setOnClickListener(onClickListener);
        }
    }

    public TLRPC$TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        FrameLayout frameLayout;
        ImageView imageView2;
        int i = Build.VERSION.SDK_INT;
        if (!(i < 21 || getBackground() == null || (imageView2 = this.optionsButton) == null)) {
            imageView2.getHitRect(this.rect);
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(71.0f), (float) (getHeight() - 1), (float) ((getWidth() - getPaddingRight()) - (LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : 0)), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void updateButtonState(int i, boolean z) {
        final int i2 = i;
        AnimatorSet animatorSet = this.stateAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.stateAnimator = null;
        }
        if (i2 == 1) {
            this.premiumButtonView.setButton(LocaleController.getString("Unlock", NUM), new StickerSetCell$$ExternalSyntheticLambda3(this));
        } else if (i2 == 2) {
            this.premiumButtonView.setButton(LocaleController.getString("Restore", NUM), new StickerSetCell$$ExternalSyntheticLambda0(this));
        }
        int i3 = 0;
        this.premiumButtonView.setEnabled(i2 == 1 || i2 == 2);
        this.addButtonView.setEnabled(i2 == 3);
        this.removeButtonView.setEnabled(i2 == 4);
        float f = 0.0f;
        float f2 = 0.6f;
        if (z) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.stateAnimator = animatorSet2;
            Animator[] animatorArr = new Animator[9];
            PremiumButtonView premiumButtonView2 = this.premiumButtonView;
            Property property = FrameLayout.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = (i2 == 1 || i2 == 2) ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(premiumButtonView2, property, fArr);
            PremiumButtonView premiumButtonView3 = this.premiumButtonView;
            Property property2 = FrameLayout.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = (i2 == 1 || i2 == 2) ? 1.0f : 0.6f;
            animatorArr[1] = ObjectAnimator.ofFloat(premiumButtonView3, property2, fArr2);
            PremiumButtonView premiumButtonView4 = this.premiumButtonView;
            Property property3 = FrameLayout.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = (i2 == 1 || i2 == 2) ? 1.0f : 0.6f;
            animatorArr[2] = ObjectAnimator.ofFloat(premiumButtonView4, property3, fArr3);
            TextView textView2 = this.addButtonView;
            Property property4 = FrameLayout.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = i2 == 3 ? 1.0f : 0.0f;
            animatorArr[3] = ObjectAnimator.ofFloat(textView2, property4, fArr4);
            TextView textView3 = this.addButtonView;
            Property property5 = FrameLayout.SCALE_X;
            float[] fArr5 = new float[1];
            fArr5[0] = i2 == 3 ? 1.0f : 0.6f;
            animatorArr[4] = ObjectAnimator.ofFloat(textView3, property5, fArr5);
            TextView textView4 = this.addButtonView;
            Property property6 = FrameLayout.SCALE_Y;
            float[] fArr6 = new float[1];
            fArr6[0] = i2 == 3 ? 1.0f : 0.6f;
            animatorArr[5] = ObjectAnimator.ofFloat(textView4, property6, fArr6);
            TextView textView5 = this.removeButtonView;
            Property property7 = FrameLayout.ALPHA;
            float[] fArr7 = new float[1];
            if (i2 == 4) {
                f = 1.0f;
            }
            fArr7[0] = f;
            animatorArr[6] = ObjectAnimator.ofFloat(textView5, property7, fArr7);
            TextView textView6 = this.removeButtonView;
            Property property8 = FrameLayout.SCALE_X;
            float[] fArr8 = new float[1];
            fArr8[0] = i2 == 4 ? 1.0f : 0.6f;
            animatorArr[7] = ObjectAnimator.ofFloat(textView6, property8, fArr8);
            TextView textView7 = this.removeButtonView;
            Property property9 = FrameLayout.SCALE_Y;
            float[] fArr9 = new float[1];
            if (i2 == 4) {
                f2 = 1.0f;
            }
            fArr9[0] = f2;
            animatorArr[8] = ObjectAnimator.ofFloat(textView7, property9, fArr9);
            animatorSet2.playTogether(animatorArr);
            this.stateAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    StickerSetCell.this.premiumButtonView.setVisibility(0);
                    StickerSetCell.this.addButtonView.setVisibility(0);
                    StickerSetCell.this.removeButtonView.setVisibility(0);
                }

                public void onAnimationEnd(Animator animator) {
                    PremiumButtonView access$200 = StickerSetCell.this.premiumButtonView;
                    int i = i2;
                    int i2 = 8;
                    access$200.setVisibility((i == 1 || i == 2) ? 0 : 8);
                    StickerSetCell.this.addButtonView.setVisibility(i2 == 3 ? 0 : 8);
                    TextView access$400 = StickerSetCell.this.removeButtonView;
                    if (i2 == 4) {
                        i2 = 0;
                    }
                    access$400.setVisibility(i2);
                }
            });
            this.stateAnimator.setDuration(250);
            this.stateAnimator.setInterpolator(new OvershootInterpolator(1.02f));
            this.stateAnimator.start();
            return;
        }
        this.premiumButtonView.setAlpha((i2 == 1 || i2 == 2) ? 1.0f : 0.0f);
        this.premiumButtonView.setScaleX((i2 == 1 || i2 == 2) ? 1.0f : 0.6f);
        this.premiumButtonView.setScaleY((i2 == 1 || i2 == 2) ? 1.0f : 0.6f);
        this.premiumButtonView.setVisibility((i2 == 1 || i2 == 2) ? 0 : 8);
        this.addButtonView.setAlpha(i2 == 3 ? 1.0f : 0.0f);
        this.addButtonView.setScaleX(i2 == 3 ? 1.0f : 0.6f);
        this.addButtonView.setScaleY(i2 == 3 ? 1.0f : 0.6f);
        this.addButtonView.setVisibility(i2 == 3 ? 0 : 8);
        TextView textView8 = this.removeButtonView;
        if (i2 == 4) {
            f = 1.0f;
        }
        textView8.setAlpha(f);
        this.removeButtonView.setScaleX(i2 == 4 ? 1.0f : 0.6f);
        TextView textView9 = this.removeButtonView;
        if (i2 == 4) {
            f2 = 1.0f;
        }
        textView9.setScaleY(f2);
        TextView textView10 = this.removeButtonView;
        if (i2 != 4) {
            i3 = 8;
        }
        textView10.setVisibility(i3);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButtonState$7(View view) {
        onPremiumButtonClick();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButtonState$8(View view) {
        onPremiumButtonClick();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && checkBox2.isChecked()) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(true);
        }
    }
}
