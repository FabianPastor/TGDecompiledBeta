package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$WallPaperSettings;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.WallpapersListActivity;
/* loaded from: classes3.dex */
public class WallpaperCell extends FrameLayout {
    private Paint backgroundPaint;
    private Drawable checkDrawable;
    private Paint circlePaint;
    private int currentType;
    private Paint framePaint;
    private boolean isBottom;
    private boolean isTop;
    private int spanCount;
    private WallpaperView[] wallpaperViews;

    protected void onWallpaperClick(Object obj, int i) {
    }

    protected boolean onWallpaperLongClick(Object obj, int i) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class WallpaperView extends FrameLayout {
        private AnimatorSet animator;
        private CheckBox checkBox;
        private Object currentWallpaper;
        private BackupImageView imageView;
        private ImageView imageView2;
        private boolean isSelected;
        private View selector;

        public WallpaperView(Context context) {
            super(context);
            setWillNotDraw(false);
            BackupImageView backupImageView = new BackupImageView(context, WallpaperCell.this) { // from class: org.telegram.ui.Cells.WallpaperCell.WallpaperView.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.Components.BackupImageView, android.view.View
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if ((WallpaperView.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) || (WallpaperView.this.currentWallpaper instanceof WallpapersListActivity.FileWallpaper)) {
                        canvas.drawLine(1.0f, 0.0f, getMeasuredWidth() - 1, 0.0f, WallpaperCell.this.framePaint);
                        canvas.drawLine(0.0f, 0.0f, 0.0f, getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas.drawLine(getMeasuredWidth() - 1, 0.0f, getMeasuredWidth() - 1, getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas.drawLine(1.0f, getMeasuredHeight() - 1, getMeasuredWidth() - 1, getMeasuredHeight() - 1, WallpaperCell.this.framePaint);
                    }
                    if (WallpaperView.this.isSelected) {
                        WallpaperCell.this.circlePaint.setColor(Theme.serviceMessageColorBackup);
                        int measuredWidth = getMeasuredWidth() / 2;
                        int measuredHeight = getMeasuredHeight() / 2;
                        canvas.drawCircle(measuredWidth, measuredHeight, AndroidUtilities.dp(20.0f), WallpaperCell.this.circlePaint);
                        WallpaperCell.this.checkDrawable.setBounds(measuredWidth - (WallpaperCell.this.checkDrawable.getIntrinsicWidth() / 2), measuredHeight - (WallpaperCell.this.checkDrawable.getIntrinsicHeight() / 2), measuredWidth + (WallpaperCell.this.checkDrawable.getIntrinsicWidth() / 2), measuredHeight + (WallpaperCell.this.checkDrawable.getIntrinsicHeight() / 2));
                        WallpaperCell.this.checkDrawable.draw(canvas);
                    }
                }
            };
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(-1, -1, 51));
            ImageView imageView = new ImageView(context);
            this.imageView2 = imageView;
            imageView.setImageResource(R.drawable.ic_gallery_background);
            this.imageView2.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView2, LayoutHelper.createFrame(-1, -1, 51));
            View view = new View(context);
            this.selector = view;
            view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
            CheckBox checkBox = new CheckBox(context, R.drawable.round_check2);
            this.checkBox = checkBox;
            checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 2.0f, 2.0f, 0.0f));
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
            }
            return super.onTouchEvent(motionEvent);
        }

        public void setWallpaper(Object obj, Object obj2, Drawable drawable, boolean z) {
            int patternColor;
            int patternColor2;
            this.currentWallpaper = obj;
            int i = 0;
            this.imageView.setVisibility(0);
            this.imageView2.setVisibility(4);
            TLRPC$PhotoSize tLRPC$PhotoSize = null;
            this.imageView.setBackgroundDrawable(null);
            this.imageView.getImageReceiver().setColorFilter(null);
            this.imageView.getImageReceiver().setAlpha(1.0f);
            this.imageView.getImageReceiver().setBlendMode(null);
            this.imageView.getImageReceiver().setGradientBitmap(null);
            this.isSelected = obj == obj2;
            String str = "180_180";
            String str2 = "100_100_b";
            if (obj instanceof TLRPC$TL_wallPaper) {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) obj;
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_wallPaper.document.thumbs, AndroidUtilities.dp(100));
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_wallPaper.document.thumbs, AndroidUtilities.dp(180));
                if (closestPhotoSizeWithSize2 != closestPhotoSizeWithSize) {
                    tLRPC$PhotoSize = closestPhotoSizeWithSize2;
                }
                long j = tLRPC$PhotoSize != null ? tLRPC$PhotoSize.size : tLRPC$TL_wallPaper.document.size;
                if (!tLRPC$TL_wallPaper.pattern) {
                    if (tLRPC$PhotoSize != null) {
                        this.imageView.setImage(ImageLocation.getForDocument(tLRPC$PhotoSize, tLRPC$TL_wallPaper.document), str, ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$TL_wallPaper.document), str2, "jpg", j, 1, tLRPC$TL_wallPaper);
                        return;
                    } else {
                        this.imageView.setImage(ImageLocation.getForDocument(tLRPC$TL_wallPaper.document), str, ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$TL_wallPaper.document), str2, "jpg", j, 1, tLRPC$TL_wallPaper);
                        return;
                    }
                }
                TLRPC$WallPaperSettings tLRPC$WallPaperSettings = tLRPC$TL_wallPaper.settings;
                if (tLRPC$WallPaperSettings.third_background_color != 0) {
                    TLRPC$WallPaperSettings tLRPC$WallPaperSettings2 = tLRPC$TL_wallPaper.settings;
                    MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(tLRPC$WallPaperSettings2.background_color, tLRPC$WallPaperSettings2.second_background_color, tLRPC$WallPaperSettings2.third_background_color, tLRPC$WallPaperSettings2.fourth_background_color, true);
                    if (tLRPC$TL_wallPaper.settings.intensity >= 0 || !Theme.getActiveTheme().isDark()) {
                        this.imageView.setBackground(motionBackgroundDrawable);
                        if (Build.VERSION.SDK_INT >= 29) {
                            this.imageView.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
                        }
                    } else {
                        this.imageView.getImageReceiver().setGradientBitmap(motionBackgroundDrawable.getBitmap());
                    }
                    TLRPC$WallPaperSettings tLRPC$WallPaperSettings3 = tLRPC$TL_wallPaper.settings;
                    patternColor2 = MotionBackgroundDrawable.getPatternColor(tLRPC$WallPaperSettings3.background_color, tLRPC$WallPaperSettings3.second_background_color, tLRPC$WallPaperSettings3.third_background_color, tLRPC$WallPaperSettings3.fourth_background_color);
                } else {
                    this.imageView.setBackgroundColor(Theme.getWallpaperColor(tLRPC$WallPaperSettings.background_color));
                    patternColor2 = AndroidUtilities.getPatternColor(tLRPC$TL_wallPaper.settings.background_color);
                }
                if (Build.VERSION.SDK_INT < 29 || tLRPC$TL_wallPaper.settings.third_background_color == 0) {
                    this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(patternColor2), PorterDuff.Mode.SRC_IN));
                }
                if (tLRPC$PhotoSize != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$PhotoSize, tLRPC$TL_wallPaper.document), str, ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$TL_wallPaper.document), null, "jpg", j, 1, tLRPC$TL_wallPaper);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$TL_wallPaper.document), str, null, null, "jpg", j, 1, tLRPC$TL_wallPaper);
                }
                this.imageView.getImageReceiver().setAlpha(Math.abs(tLRPC$TL_wallPaper.settings.intensity) / 100.0f);
            } else if (obj instanceof WallpapersListActivity.ColorWallpaper) {
                WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
                if (colorWallpaper.path != null || colorWallpaper.pattern != null || "d".equals(colorWallpaper.slug)) {
                    if (colorWallpaper.gradientColor2 != 0) {
                        MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable(colorWallpaper.color, colorWallpaper.gradientColor1, colorWallpaper.gradientColor2, colorWallpaper.gradientColor3, true);
                        if (colorWallpaper.intensity >= 0.0f) {
                            this.imageView.setBackground(new MotionBackgroundDrawable(colorWallpaper.color, colorWallpaper.gradientColor1, colorWallpaper.gradientColor2, colorWallpaper.gradientColor3, true));
                            if (Build.VERSION.SDK_INT >= 29) {
                                this.imageView.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
                            }
                        } else {
                            this.imageView.getImageReceiver().setGradientBitmap(motionBackgroundDrawable2.getBitmap());
                        }
                        patternColor = MotionBackgroundDrawable.getPatternColor(colorWallpaper.color, colorWallpaper.gradientColor1, colorWallpaper.gradientColor2, colorWallpaper.gradientColor3);
                    } else {
                        patternColor = AndroidUtilities.getPatternColor(colorWallpaper.color);
                    }
                    if ("d".equals(colorWallpaper.slug)) {
                        if (colorWallpaper.defaultCache == null) {
                            colorWallpaper.defaultCache = SvgHelper.getBitmap(R.raw.default_pattern, 100, 180, -16777216);
                        }
                        this.imageView.setImageBitmap(colorWallpaper.defaultCache);
                        this.imageView.getImageReceiver().setAlpha(Math.abs(colorWallpaper.intensity));
                        return;
                    }
                    File file = colorWallpaper.path;
                    if (file != null) {
                        this.imageView.setImage(file.getAbsolutePath(), str, null);
                        return;
                    }
                    TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(colorWallpaper.pattern.document.thumbs, 100);
                    this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize3, colorWallpaper.pattern.document), str, null, null, "jpg", closestPhotoSizeWithSize3 != null ? closestPhotoSizeWithSize3.size : colorWallpaper.pattern.document.size, 1, colorWallpaper.pattern);
                    this.imageView.getImageReceiver().setAlpha(Math.abs(colorWallpaper.intensity));
                    if (Build.VERSION.SDK_INT >= 29 && colorWallpaper.gradientColor2 != 0) {
                        return;
                    }
                    this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(patternColor), PorterDuff.Mode.SRC_IN));
                    return;
                }
                this.imageView.setImageBitmap(null);
                if (colorWallpaper.isGradient) {
                    this.imageView.setBackground(new MotionBackgroundDrawable(colorWallpaper.color, colorWallpaper.gradientColor1, colorWallpaper.gradientColor2, colorWallpaper.gradientColor3, true));
                } else if (colorWallpaper.gradientColor1 != 0) {
                    this.imageView.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{colorWallpaper.color | (-16777216), colorWallpaper.gradientColor1 | (-16777216)}));
                } else {
                    this.imageView.setBackgroundColor(colorWallpaper.color | (-16777216));
                }
            } else if (obj instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj;
                File file2 = fileWallpaper.originalPath;
                if (file2 != null) {
                    this.imageView.setImage(file2.getAbsolutePath(), str, null);
                    return;
                }
                File file3 = fileWallpaper.path;
                if (file3 != null) {
                    this.imageView.setImage(file3.getAbsolutePath(), str, null);
                } else if ("t".equals(fileWallpaper.slug)) {
                    BackupImageView backupImageView = this.imageView;
                    backupImageView.setImageDrawable(Theme.getThemedWallpaper(true, backupImageView));
                } else {
                    this.imageView.setImageResource(fileWallpaper.thumbResId);
                }
            } else if (obj instanceof MediaController.SearchImage) {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                TLRPC$Photo tLRPC$Photo = searchImage.photo;
                if (tLRPC$Photo != null) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, AndroidUtilities.dp(100));
                    TLRPC$PhotoSize closestPhotoSizeWithSize5 = FileLoader.getClosestPhotoSizeWithSize(searchImage.photo.sizes, AndroidUtilities.dp(180));
                    if (closestPhotoSizeWithSize5 != closestPhotoSizeWithSize4) {
                        tLRPC$PhotoSize = closestPhotoSizeWithSize5;
                    }
                    if (tLRPC$PhotoSize != null) {
                        i = tLRPC$PhotoSize.size;
                    }
                    this.imageView.setImage(ImageLocation.getForPhoto(tLRPC$PhotoSize, searchImage.photo), str, ImageLocation.getForPhoto(closestPhotoSizeWithSize4, searchImage.photo), str2, "jpg", i, 1, searchImage);
                    return;
                }
                this.imageView.setImage(searchImage.thumbUrl, str, null);
            } else {
                this.isSelected = false;
            }
        }

        public void setChecked(final boolean z, boolean z2) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
            float f = 0.8875f;
            if (z2) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animator = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                BackupImageView backupImageView = this.imageView;
                float[] fArr = new float[1];
                fArr[0] = z ? 0.8875f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, "scaleX", fArr);
                BackupImageView backupImageView2 = this.imageView;
                float[] fArr2 = new float[1];
                if (!z) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(backupImageView2, "scaleY", fArr2);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(200L);
                this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.WallpaperCell.WallpaperView.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (WallpaperView.this.animator == null || !WallpaperView.this.animator.equals(animator)) {
                            return;
                        }
                        WallpaperView.this.animator = null;
                        if (z) {
                            return;
                        }
                        WallpaperView.this.setBackgroundColor(0);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        if (WallpaperView.this.animator == null || !WallpaperView.this.animator.equals(animator)) {
                            return;
                        }
                        WallpaperView.this.animator = null;
                    }
                });
                this.animator.start();
            } else {
                this.imageView.setScaleX(z ? 0.8875f : 1.0f);
                BackupImageView backupImageView3 = this.imageView;
                if (!z) {
                    f = 1.0f;
                }
                backupImageView3.setScaleY(f);
            }
            invalidate();
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            this.imageView.invalidate();
        }

        @Override // android.view.View
        public void clearAnimation() {
            super.clearAnimation();
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.checkBox.isChecked() || !this.imageView.getImageReceiver().hasBitmapImage() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f) {
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), WallpaperCell.this.backgroundPaint);
            }
        }
    }

    public WallpaperCell(Context context) {
        super(context);
        this.spanCount = 3;
        this.wallpaperViews = new WallpaperView[5];
        final int i = 0;
        while (true) {
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            if (i < wallpaperViewArr.length) {
                final WallpaperView wallpaperView = new WallpaperView(context);
                wallpaperViewArr[i] = wallpaperView;
                addView(wallpaperView);
                wallpaperView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.WallpaperCell$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        WallpaperCell.this.lambda$new$0(wallpaperView, i, view);
                    }
                });
                wallpaperView.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Cells.WallpaperCell$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view) {
                        boolean lambda$new$1;
                        lambda$new$1 = WallpaperCell.this.lambda$new$1(wallpaperView, i, view);
                        return lambda$new$1;
                    }
                });
                i++;
            } else {
                Paint paint = new Paint();
                this.framePaint = paint;
                paint.setColor(NUM);
                this.circlePaint = new Paint(1);
                this.checkDrawable = context.getResources().getDrawable(R.drawable.background_selected).mutate();
                Paint paint2 = new Paint();
                this.backgroundPaint = paint2;
                paint2.setColor(Theme.getColor("sharedMedia_photoPlaceholder"));
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(WallpaperView wallpaperView, int i, View view) {
        onWallpaperClick(wallpaperView.currentWallpaper, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1(WallpaperView wallpaperView, int i, View view) {
        return onWallpaperLongClick(wallpaperView.currentWallpaper, i);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = size - AndroidUtilities.dp(((this.spanCount - 1) * 6) + 28);
        int i3 = dp / this.spanCount;
        int dp2 = this.currentType == 0 ? AndroidUtilities.dp(180.0f) : i3;
        float f = 14.0f;
        int i4 = 0;
        int dp3 = (this.isTop ? AndroidUtilities.dp(14.0f) : 0) + dp2;
        if (!this.isBottom) {
            f = 6.0f;
        }
        setMeasuredDimension(size, dp3 + AndroidUtilities.dp(f));
        while (true) {
            int i5 = this.spanCount;
            if (i4 < i5) {
                this.wallpaperViews[i4].measure(View.MeasureSpec.makeMeasureSpec(i4 == i5 + (-1) ? dp : i3, NUM), View.MeasureSpec.makeMeasureSpec(dp2, NUM));
                dp -= i3;
                i4++;
            } else {
                return;
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int dp = AndroidUtilities.dp(14.0f);
        int dp2 = this.isTop ? AndroidUtilities.dp(14.0f) : 0;
        for (int i5 = 0; i5 < this.spanCount; i5++) {
            int measuredWidth = this.wallpaperViews[i5].getMeasuredWidth();
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            wallpaperViewArr[i5].layout(dp, dp2, dp + measuredWidth, wallpaperViewArr[i5].getMeasuredHeight() + dp2);
            dp += measuredWidth + AndroidUtilities.dp(6.0f);
        }
    }

    public void setParams(int i, boolean z, boolean z2) {
        this.spanCount = i;
        this.isTop = z;
        this.isBottom = z2;
        int i2 = 0;
        while (true) {
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            if (i2 < wallpaperViewArr.length) {
                wallpaperViewArr[i2].setVisibility(i2 < i ? 0 : 8);
                this.wallpaperViews[i2].clearAnimation();
                i2++;
            } else {
                return;
            }
        }
    }

    public void setWallpaper(int i, int i2, Object obj, Object obj2, Drawable drawable, boolean z) {
        this.currentType = i;
        if (obj == null) {
            this.wallpaperViews[i2].setVisibility(8);
            this.wallpaperViews[i2].clearAnimation();
            return;
        }
        this.wallpaperViews[i2].setVisibility(0);
        this.wallpaperViews[i2].setWallpaper(obj, obj2, drawable, z);
    }

    public void setChecked(int i, boolean z, boolean z2) {
        this.wallpaperViews[i].setChecked(z, z2);
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        for (int i = 0; i < this.spanCount; i++) {
            this.wallpaperViews[i].invalidate();
        }
    }
}
