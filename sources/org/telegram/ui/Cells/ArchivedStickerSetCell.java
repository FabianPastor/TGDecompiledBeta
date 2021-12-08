package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.ViewHelper;

public class ArchivedStickerSetCell extends FrameLayout implements Checkable {
    /* access modifiers changed from: private */
    public final ProgressButton addButton;
    private AnimatorSet animatorSet;
    private final boolean checkable;
    private boolean checked;
    /* access modifiers changed from: private */
    public Button currentButton;
    /* access modifiers changed from: private */
    public final Button deleteButton;
    private final BackupImageView imageView;
    private boolean needDivider;
    private OnCheckedChangeListener onCheckedChangeListener;
    private TLRPC.StickerSetCovered stickersSet;
    private final TextView textView;
    private final TextView valueTextView;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ArchivedStickerSetCell archivedStickerSetCell, boolean z);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ArchivedStickerSetCell(Context context, boolean checkable2) {
        super(context);
        Context context2 = context;
        boolean z = checkable2;
        this.checkable = z;
        if (z) {
            ProgressButton progressButton = new ProgressButton(context2);
            this.addButton = progressButton;
            this.currentButton = progressButton;
            progressButton.setText(LocaleController.getString("Add", NUM));
            progressButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            progressButton.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
            progressButton.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
            addView(progressButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
            int minWidth = AndroidUtilities.dp(60.0f);
            ProgressButton progressButton2 = new ProgressButton(context2);
            this.deleteButton = progressButton2;
            progressButton2.setAllCaps(false);
            progressButton2.setMinWidth(minWidth);
            progressButton2.setMinimumWidth(minWidth);
            progressButton2.setTextSize(1, 14.0f);
            progressButton2.setTextColor(Theme.getColor("featuredStickers_removeButtonText"));
            progressButton2.setText(LocaleController.getString("StickersRemove", NUM));
            progressButton2.setBackground(Theme.getRoundRectSelectorDrawable(Theme.getColor("featuredStickers_removeButtonText")));
            progressButton2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            ViewHelper.setPadding(progressButton2, 8.0f, 0.0f, 8.0f, 0.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                progressButton2.setOutlineProvider((ViewOutlineProvider) null);
            }
            addView(progressButton2, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
            View.OnClickListener toggleListener = new ArchivedStickerSetCell$$ExternalSyntheticLambda0(this);
            progressButton.setOnClickListener(toggleListener);
            progressButton2.setOnClickListener(toggleListener);
            syncButtons(false);
        } else {
            this.addButton = null;
            this.deleteButton = null;
        }
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView2.setTextSize(1, 16.0f);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(textView2, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 10.0f, 21.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        textView3.setTextSize(1, 13.0f);
        textView3.setLines(1);
        textView3.setMaxLines(1);
        textView3.setSingleLine(true);
        textView3.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(textView3, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 35.0f, 21.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        backupImageView.setLayerNum(1);
        addView(backupImageView, LayoutHelper.createFrameRelatively(48.0f, 48.0f, 8388659, 12.0f, 8.0f, 0.0f, 0.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-ArchivedStickerSetCell  reason: not valid java name */
    public /* synthetic */ void m1526lambda$new$0$orgtelegramuiCellsArchivedStickerSetCell(View v) {
        toggle();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    /* access modifiers changed from: protected */
    public void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (this.checkable && child == this.textView) {
            widthUsed += Math.max(this.addButton.getMeasuredWidth(), this.deleteButton.getMeasuredWidth());
        }
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void setDrawProgress(boolean drawProgress, boolean animated) {
        ProgressButton progressButton = this.addButton;
        if (progressButton != null) {
            progressButton.setDrawProgress(drawProgress, animated);
        }
    }

    public void setStickersSet(TLRPC.StickerSetCovered set, boolean divider) {
        TLRPC.Document sticker;
        TLObject object;
        ImageLocation imageLocation;
        TLRPC.StickerSetCovered stickerSetCovered = set;
        boolean z = divider;
        this.needDivider = z;
        this.stickersSet = stickerSetCovered;
        setWillNotDraw(!z);
        this.textView.setText(this.stickersSet.set.title);
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count));
        if (stickerSetCovered.cover != null) {
            sticker = stickerSetCovered.cover;
        } else if (!stickerSetCovered.covers.isEmpty()) {
            sticker = stickerSetCovered.covers.get(0);
        } else {
            sticker = null;
        }
        if (sticker != null) {
            TLObject object2 = FileLoader.getClosestPhotoSizeWithSize(stickerSetCovered.set.thumbs, 90);
            if (object2 == null) {
                object = sticker;
            } else {
                object = object2;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(stickerSetCovered.set.thumbs, "windowBackgroundGray", 1.0f);
            if (object instanceof TLRPC.Document) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90), sticker);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) object, sticker, stickerSetCovered.set.thumb_version);
            }
            if (!(object instanceof TLRPC.Document) || !MessageObject.isAnimatedStickerDocument(sticker, true)) {
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
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, (Object) set);
        }
    }

    public TLRPC.StickerSetCovered getStickersSet() {
        return this.stickersSet;
    }

    private void syncButtons(boolean animated) {
        if (this.checkable) {
            AnimatorSet animatorSet2 = this.animatorSet;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            boolean z = this.checked;
            float addButtonValue = 1.0f;
            float deleteButtonValue = z ? 1.0f : 0.0f;
            if (z) {
                addButtonValue = 0.0f;
            }
            int i = 4;
            if (animated) {
                this.currentButton = z ? this.deleteButton : this.addButton;
                this.addButton.setVisibility(0);
                this.deleteButton.setVisibility(0);
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.setDuration(250);
                this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.deleteButton, View.ALPHA, new float[]{deleteButtonValue}), ObjectAnimator.ofFloat(this.deleteButton, View.SCALE_X, new float[]{deleteButtonValue}), ObjectAnimator.ofFloat(this.deleteButton, View.SCALE_Y, new float[]{deleteButtonValue}), ObjectAnimator.ofFloat(this.addButton, View.ALPHA, new float[]{addButtonValue}), ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, new float[]{addButtonValue}), ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, new float[]{addButtonValue})});
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ArchivedStickerSetCell.this.currentButton == ArchivedStickerSetCell.this.addButton) {
                            ArchivedStickerSetCell.this.deleteButton.setVisibility(4);
                        } else {
                            ArchivedStickerSetCell.this.addButton.setVisibility(4);
                        }
                    }
                });
                this.animatorSet.setInterpolator(new OvershootInterpolator(1.02f));
                this.animatorSet.start();
                return;
            }
            this.deleteButton.setVisibility(z ? 0 : 4);
            this.deleteButton.setAlpha(deleteButtonValue);
            this.deleteButton.setScaleX(deleteButtonValue);
            this.deleteButton.setScaleY(deleteButtonValue);
            ProgressButton progressButton = this.addButton;
            if (!this.checked) {
                i = 0;
            }
            progressButton.setVisibility(i);
            this.addButton.setAlpha(addButtonValue);
            this.addButton.setScaleX(addButtonValue);
            this.addButton.setScaleY(addButtonValue);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    public void setChecked(boolean checked2) {
        setChecked(checked2, true);
    }

    public void setChecked(boolean checked2, boolean animated) {
        setChecked(checked2, animated, true);
    }

    public void setChecked(boolean checked2, boolean animated, boolean notify) {
        OnCheckedChangeListener onCheckedChangeListener2;
        if (this.checkable && this.checked != checked2) {
            this.checked = checked2;
            syncButtons(animated);
            if (notify && (onCheckedChangeListener2 = this.onCheckedChangeListener) != null) {
                onCheckedChangeListener2.onCheckedChanged(this, checked2);
            }
        }
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void toggle() {
        if (this.checkable) {
            setChecked(!isChecked());
        }
    }
}
