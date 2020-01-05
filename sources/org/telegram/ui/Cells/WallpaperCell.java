package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.WallpapersListActivity.ColorWallpaper;
import org.telegram.ui.WallpapersListActivity.FileWallpaper;

public class WallpaperCell extends FrameLayout {
    private Paint backgroundPaint;
    private Drawable checkDrawable;
    private Paint circlePaint;
    private int currentType;
    private Paint framePaint;
    private boolean isBottom;
    private boolean isTop;
    private int spanCount = 3;
    private WallpaperView[] wallpaperViews = new WallpaperView[5];

    private class WallpaperView extends FrameLayout {
        private AnimatorSet animator;
        private AnimatorSet animatorSet;
        private CheckBox checkBox;
        private Object currentWallpaper;
        private BackupImageView imageView;
        private ImageView imageView2;
        private boolean isSelected;
        private View selector;

        public WallpaperView(Context context) {
            super(context);
            setWillNotDraw(false);
            this.imageView = new BackupImageView(context, WallpaperCell.this) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (WallpaperView.this.currentWallpaper instanceof ColorWallpaper) {
                        Canvas canvas2 = canvas;
                        canvas2.drawLine(1.0f, 0.0f, (float) (getMeasuredWidth() - 1), 0.0f, WallpaperCell.this.framePaint);
                        Canvas canvas3 = canvas;
                        canvas3.drawLine(0.0f, 0.0f, 0.0f, (float) getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas2.drawLine((float) (getMeasuredWidth() - 1), 0.0f, (float) (getMeasuredWidth() - 1), (float) getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas3.drawLine(1.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - 1), (float) (getMeasuredHeight() - 1), WallpaperCell.this.framePaint);
                    }
                    if (WallpaperView.this.isSelected) {
                        WallpaperCell.this.circlePaint.setColor(Theme.serviceMessageColorBackup);
                        int measuredWidth = getMeasuredWidth() / 2;
                        int measuredHeight = getMeasuredHeight() / 2;
                        canvas.drawCircle((float) measuredWidth, (float) measuredHeight, (float) AndroidUtilities.dp(20.0f), WallpaperCell.this.circlePaint);
                        WallpaperCell.this.checkDrawable.setBounds(measuredWidth - (WallpaperCell.this.checkDrawable.getIntrinsicWidth() / 2), measuredHeight - (WallpaperCell.this.checkDrawable.getIntrinsicHeight() / 2), measuredWidth + (WallpaperCell.this.checkDrawable.getIntrinsicWidth() / 2), measuredHeight + (WallpaperCell.this.checkDrawable.getIntrinsicHeight() / 2));
                        WallpaperCell.this.checkDrawable.draw(canvas);
                    }
                }
            };
            addView(this.imageView, LayoutHelper.createFrame(-1, -1, 51));
            this.imageView2 = new ImageView(context);
            this.imageView2.setImageResource(NUM);
            this.imageView2.setScaleType(ScaleType.CENTER);
            addView(this.imageView2, LayoutHelper.createFrame(-1, -1, 51));
            this.selector = new View(context);
            this.selector.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
            this.checkBox = new CheckBox(context, NUM);
            this.checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 2.0f, 2.0f, 0.0f));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
            }
            return super.onTouchEvent(motionEvent);
        }

        public void setWallpaper(Object obj, long j, Drawable drawable, boolean z) {
            Object obj2 = obj;
            this.currentWallpaper = obj2;
            boolean z2 = false;
            if (obj2 == null) {
                this.imageView.setVisibility(4);
                this.imageView2.setVisibility(0);
                if (z) {
                    this.imageView2.setImageDrawable(drawable);
                    this.imageView2.setScaleType(ScaleType.CENTER_CROP);
                    return;
                }
                ImageView imageView = this.imageView2;
                int i = (j == -1 || j == 1000001) ? NUM : NUM;
                imageView.setBackgroundColor(i);
                this.imageView2.setScaleType(ScaleType.CENTER);
                this.imageView2.setImageResource(NUM);
                return;
            }
            this.imageView.setVisibility(0);
            this.imageView2.setVisibility(4);
            PhotoSize photoSize = null;
            this.imageView.setBackgroundDrawable(null);
            this.imageView.getImageReceiver().setColorFilter(null);
            this.imageView.getImageReceiver().setAlpha(1.0f);
            PhotoSize closestPhotoSizeWithSize;
            if (obj2 instanceof TL_wallPaper) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) obj2;
                if (tL_wallPaper.id == j) {
                    z2 = true;
                }
                this.isSelected = z2;
                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tL_wallPaper.document.thumbs, 100);
                PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tL_wallPaper.document.thumbs, 320);
                if (closestPhotoSizeWithSize2 == closestPhotoSizeWithSize) {
                    closestPhotoSizeWithSize2 = null;
                }
                int i2 = closestPhotoSizeWithSize2 != null ? closestPhotoSizeWithSize2.size : tL_wallPaper.document.size;
                if (tL_wallPaper.pattern) {
                    this.imageView.setBackgroundColor(tL_wallPaper.settings.background_color | -16777216);
                    this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize2, tL_wallPaper.document), "100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, tL_wallPaper.document), null, "jpg", i2, 1, tL_wallPaper);
                    this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(tL_wallPaper.settings.background_color), Mode.SRC_IN));
                    this.imageView.getImageReceiver().setAlpha(((float) tL_wallPaper.settings.intensity) / 100.0f);
                    return;
                } else if (closestPhotoSizeWithSize2 != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize2, tL_wallPaper.document), "100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, tL_wallPaper.document), "100_100_b", "jpg", i2, 1, tL_wallPaper);
                    return;
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(tL_wallPaper.document), "100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, tL_wallPaper.document), "100_100_b", "jpg", i2, 1, tL_wallPaper);
                    return;
                }
            }
            String str = "100_100";
            File file;
            if (obj2 instanceof ColorWallpaper) {
                ColorWallpaper colorWallpaper = (ColorWallpaper) obj2;
                file = colorWallpaper.path;
                if (file != null) {
                    this.imageView.setImage(file.getAbsolutePath(), str, null);
                } else {
                    this.imageView.setImageBitmap(null);
                    this.imageView.setBackgroundColor(colorWallpaper.color | -16777216);
                }
                if (colorWallpaper.id == j) {
                    z2 = true;
                }
                this.isSelected = z2;
            } else if (obj2 instanceof FileWallpaper) {
                FileWallpaper fileWallpaper = (FileWallpaper) obj2;
                if (fileWallpaper.id == j) {
                    z2 = true;
                }
                this.isSelected = z2;
                file = fileWallpaper.originalPath;
                if (file != null) {
                    this.imageView.setImage(file.getAbsolutePath(), str, null);
                    return;
                }
                file = fileWallpaper.path;
                if (file != null) {
                    this.imageView.setImage(file.getAbsolutePath(), str, null);
                } else if (((long) fileWallpaper.resId) == -2) {
                    this.imageView.setImageDrawable(Theme.getThemedWallpaper(true));
                } else {
                    this.imageView.setImageResource(fileWallpaper.thumbResId);
                }
            } else if (obj2 instanceof SearchImage) {
                SearchImage searchImage = (SearchImage) obj2;
                Photo photo = searchImage.photo;
                if (photo != null) {
                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 100);
                    PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(searchImage.photo.sizes, 320);
                    if (closestPhotoSizeWithSize3 != closestPhotoSizeWithSize) {
                        photoSize = closestPhotoSizeWithSize3;
                    }
                    this.imageView.setImage(ImageLocation.getForPhoto(photoSize, searchImage.photo), "100_100", ImageLocation.getForPhoto(closestPhotoSizeWithSize, searchImage.photo), "100_100_b", "jpg", photoSize != null ? photoSize.size : 0, 1, searchImage);
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
                this.animator = new AnimatorSet();
                AnimatorSet animatorSet2 = this.animator;
                Animator[] animatorArr = new Animator[2];
                BackupImageView backupImageView = this.imageView;
                float[] fArr = new float[1];
                fArr[0] = z ? 0.8875f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, "scaleX", fArr);
                backupImageView = this.imageView;
                fArr = new float[1];
                if (!z) {
                    f = 1.0f;
                }
                fArr[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(backupImageView, "scaleY", fArr);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (WallpaperView.this.animator != null && WallpaperView.this.animator.equals(animator)) {
                            WallpaperView.this.animator = null;
                            if (!z) {
                                WallpaperView.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (WallpaperView.this.animator != null && WallpaperView.this.animator.equals(animator)) {
                            WallpaperView.this.animator = null;
                        }
                    }
                });
                this.animator.start();
            } else {
                this.imageView.setScaleX(z ? 0.8875f : 1.0f);
                BackupImageView backupImageView2 = this.imageView;
                if (!z) {
                    f = 1.0f;
                }
                backupImageView2.setScaleY(f);
            }
            invalidate();
        }

        public void invalidate() {
            super.invalidate();
            this.imageView.invalidate();
        }

        public void clearAnimation() {
            super.clearAnimation();
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.checkBox.isChecked() || !this.imageView.getImageReceiver().hasBitmapImage() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), WallpaperCell.this.backgroundPaint);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onWallpaperClick(Object obj, int i) {
    }

    /* Access modifiers changed, original: protected */
    public boolean onWallpaperLongClick(Object obj, int i) {
        return false;
    }

    public WallpaperCell(Context context) {
        super(context);
        int i = 0;
        while (true) {
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            if (i < wallpaperViewArr.length) {
                WallpaperView wallpaperView = new WallpaperView(context);
                wallpaperViewArr[i] = wallpaperView;
                addView(wallpaperView);
                wallpaperView.setOnClickListener(new -$$Lambda$WallpaperCell$fnKXvqmCCrXx3zL9kUDMI1LGdcU(this, wallpaperView, i));
                wallpaperView.setOnLongClickListener(new -$$Lambda$WallpaperCell$KQeRkjIAB8SBAWriFhep5_TPLbU(this, wallpaperView, i));
                i++;
            } else {
                this.framePaint = new Paint();
                this.framePaint.setColor(NUM);
                this.circlePaint = new Paint(1);
                this.checkDrawable = context.getResources().getDrawable(NUM).mutate();
                this.backgroundPaint = new Paint();
                this.backgroundPaint.setColor(Theme.getColor("sharedMedia_photoPlaceholder"));
                return;
            }
        }
    }

    public /* synthetic */ void lambda$new$0$WallpaperCell(WallpaperView wallpaperView, int i, View view) {
        onWallpaperClick(wallpaperView.currentWallpaper, i);
    }

    public /* synthetic */ boolean lambda$new$1$WallpaperCell(WallpaperView wallpaperView, int i, View view) {
        return onWallpaperLongClick(wallpaperView.currentWallpaper, i);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        i2 = i - AndroidUtilities.dp((float) (((this.spanCount - 1) * 6) + 28));
        int i3 = i2 / this.spanCount;
        int dp = this.currentType == 0 ? AndroidUtilities.dp(180.0f) : i3;
        float f = 14.0f;
        int i4 = 0;
        int dp2 = (this.isTop ? AndroidUtilities.dp(14.0f) : 0) + dp;
        if (!this.isBottom) {
            f = 6.0f;
        }
        setMeasuredDimension(i, dp2 + AndroidUtilities.dp(f));
        while (true) {
            i = this.spanCount;
            if (i4 < i) {
                this.wallpaperViews[i4].measure(MeasureSpec.makeMeasureSpec(i4 == i + -1 ? i2 : i3, NUM), MeasureSpec.makeMeasureSpec(dp, NUM));
                i2 -= i3;
                i4++;
            } else {
                return;
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        i = AndroidUtilities.dp(14.0f);
        i3 = 0;
        int dp = this.isTop ? AndroidUtilities.dp(14.0f) : 0;
        while (i3 < this.spanCount) {
            i2 = this.wallpaperViews[i3].getMeasuredWidth();
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            wallpaperViewArr[i3].layout(i, dp, i + i2, wallpaperViewArr[i3].getMeasuredHeight() + dp);
            i += i2 + AndroidUtilities.dp(6.0f);
            i3++;
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

    public void setWallpaper(int i, int i2, Object obj, long j, Drawable drawable, boolean z) {
        this.currentType = i;
        if (obj == null) {
            this.wallpaperViews[i2].setVisibility(8);
            this.wallpaperViews[i2].clearAnimation();
            return;
        }
        this.wallpaperViews[i2].setVisibility(0);
        this.wallpaperViews[i2].setWallpaper(obj, j, drawable, z);
    }

    public void setChecked(int i, boolean z, boolean z2) {
        this.wallpaperViews[i].setChecked(z, z2);
    }

    public void invalidate() {
        super.invalidate();
        for (int i = 0; i < this.spanCount; i++) {
            this.wallpaperViews[i].invalidate();
        }
    }
}
