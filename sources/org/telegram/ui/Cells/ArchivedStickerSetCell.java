package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoSizeProgressive;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.ViewHelper;

@SuppressLint({"ViewConstructor"})
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
    private TLRPC$StickerSetCovered stickersSet;
    private final TextView textView;
    private final TextView valueTextView;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ArchivedStickerSetCell archivedStickerSetCell, boolean z);
    }

    public ArchivedStickerSetCell(Context context, boolean z) {
        super(context);
        this.checkable = z;
        if (z) {
            ProgressButton progressButton = new ProgressButton(context);
            this.addButton = progressButton;
            this.currentButton = progressButton;
            progressButton.setText(LocaleController.getString("Add", NUM));
            this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.addButton.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
            this.addButton.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
            addView(this.addButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
            int dp = AndroidUtilities.dp(60.0f);
            ProgressButton progressButton2 = new ProgressButton(context);
            this.deleteButton = progressButton2;
            progressButton2.setAllCaps(false);
            this.deleteButton.setMinWidth(dp);
            this.deleteButton.setMinimumWidth(dp);
            this.deleteButton.setTextSize(1, 14.0f);
            this.deleteButton.setTextColor(Theme.getColor("featuredStickers_removeButtonText"));
            this.deleteButton.setText(LocaleController.getString("StickersRemove", NUM));
            this.deleteButton.setBackground(Theme.getRoundRectSelectorDrawable(Theme.getColor("featuredStickers_removeButtonText")));
            this.deleteButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            ViewHelper.setPadding(this.deleteButton, 8.0f, 0.0f, 8.0f, 0.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                this.deleteButton.setOutlineProvider((ViewOutlineProvider) null);
            }
            addView(this.deleteButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
            $$Lambda$ArchivedStickerSetCell$3Z2Nu1fZ98AbJFFFoZgJ6g2jG5I r13 = new View.OnClickListener() {
                public final void onClick(View view) {
                    ArchivedStickerSetCell.this.lambda$new$0$ArchivedStickerSetCell(view);
                }
            };
            this.addButton.setOnClickListener(r13);
            this.deleteButton.setOnClickListener(r13);
            syncButtons(false);
        } else {
            this.addButton = null;
            this.deleteButton = null;
        }
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 10.0f, 21.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.valueTextView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 35.0f, 21.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrameRelatively(48.0f, 48.0f, 8388659, 12.0f, 8.0f, 0.0f, 0.0f));
    }

    public /* synthetic */ void lambda$new$0$ArchivedStickerSetCell(View view) {
        toggle();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    /* access modifiers changed from: protected */
    public void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        if (this.checkable && view == this.textView) {
            i2 += Math.max(this.addButton.getMeasuredWidth(), this.deleteButton.getMeasuredWidth());
        }
        super.measureChildWithMargins(view, i, i2, i3, i4);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void setDrawProgress(boolean z, boolean z2) {
        ProgressButton progressButton = this.addButton;
        if (progressButton != null) {
            progressButton.setDrawProgress(z, z2);
        }
    }

    public void setStickersSet(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z) {
        TLObject tLObject;
        ImageLocation imageLocation;
        this.needDivider = z;
        this.stickersSet = tLRPC$StickerSetCovered;
        setWillNotDraw(!z);
        this.textView.setText(this.stickersSet.set.title);
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", tLRPC$StickerSetCovered.set.count));
        TLRPC$Document tLRPC$Document = tLRPC$StickerSetCovered.cover;
        if (tLRPC$Document == null) {
            tLRPC$Document = !tLRPC$StickerSetCovered.covers.isEmpty() ? tLRPC$StickerSetCovered.covers.get(0) : null;
        }
        if (tLRPC$Document != null) {
            TLRPC$PhotoSize tLRPC$PhotoSize = tLRPC$StickerSetCovered.set.thumb;
            if ((tLRPC$PhotoSize instanceof TLRPC$TL_photoSize) || (tLRPC$PhotoSize instanceof TLRPC$TL_photoSizeProgressive)) {
                tLObject = tLRPC$StickerSetCovered.set.thumb;
            } else {
                tLObject = tLRPC$Document;
            }
            boolean z2 = tLObject instanceof TLRPC$Document;
            if (z2) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) tLObject, tLRPC$Document);
            }
            ImageLocation imageLocation2 = imageLocation;
            if (z2 && MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", imageLocation2, (String) null, 0, (Object) tLRPC$StickerSetCovered);
            } else if (imageLocation2 == null || imageLocation2.imageType != 1) {
                this.imageView.setImage(imageLocation2, "50_50", "webp", (Drawable) null, tLRPC$StickerSetCovered);
            } else {
                this.imageView.setImage(imageLocation2, "50_50", "tgs", (Drawable) null, tLRPC$StickerSetCovered);
            }
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, tLRPC$StickerSetCovered);
        }
    }

    public TLRPC$StickerSetCovered getStickersSet() {
        return this.stickersSet;
    }

    private void syncButtons(boolean z) {
        if (this.checkable) {
            AnimatorSet animatorSet2 = this.animatorSet;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            float f = 1.0f;
            float f2 = this.checked ? 1.0f : 0.0f;
            if (this.checked) {
                f = 0.0f;
            }
            int i = 4;
            if (z) {
                this.currentButton = this.checked ? this.deleteButton : this.addButton;
                this.addButton.setVisibility(0);
                this.deleteButton.setVisibility(0);
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.setDuration(250);
                this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.deleteButton, View.ALPHA, new float[]{f2}), ObjectAnimator.ofFloat(this.deleteButton, View.SCALE_X, new float[]{f2}), ObjectAnimator.ofFloat(this.deleteButton, View.SCALE_Y, new float[]{f2}), ObjectAnimator.ofFloat(this.addButton, View.ALPHA, new float[]{f}), ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, new float[]{f}), ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, new float[]{f})});
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
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
            this.deleteButton.setVisibility(this.checked ? 0 : 4);
            this.deleteButton.setAlpha(f2);
            this.deleteButton.setScaleX(f2);
            this.deleteButton.setScaleY(f2);
            ProgressButton progressButton = this.addButton;
            if (!this.checked) {
                i = 0;
            }
            progressButton.setVisibility(i);
            this.addButton.setAlpha(f);
            this.addButton.setScaleX(f);
            this.addButton.setScaleY(f);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener2) {
        this.onCheckedChangeListener = onCheckedChangeListener2;
    }

    public void setChecked(boolean z) {
        setChecked(z, true);
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(z, z2, true);
    }

    public void setChecked(boolean z, boolean z2, boolean z3) {
        OnCheckedChangeListener onCheckedChangeListener2;
        if (this.checkable && this.checked != z) {
            this.checked = z;
            syncButtons(z2);
            if (z3 && (onCheckedChangeListener2 = this.onCheckedChangeListener) != null) {
                onCheckedChangeListener2.onCheckedChanged(this, z);
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
