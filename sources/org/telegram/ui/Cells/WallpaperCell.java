package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
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
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.WallpaperCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.WallpapersListActivity;

public class WallpaperCell extends FrameLayout {
    /* access modifiers changed from: private */
    public Paint backgroundPaint;
    /* access modifiers changed from: private */
    public Drawable checkDrawable;
    /* access modifiers changed from: private */
    public Paint circlePaint;
    private int currentType;
    /* access modifiers changed from: private */
    public Paint framePaint;
    private boolean isBottom;
    private boolean isTop;
    private int spanCount = 3;
    private WallpaperView[] wallpaperViews = new WallpaperView[5];

    /* access modifiers changed from: protected */
    public void onWallpaperClick(Object obj, int i) {
    }

    /* access modifiers changed from: protected */
    public boolean onWallpaperLongClick(Object obj, int i) {
        return false;
    }

    private class WallpaperView extends FrameLayout {
        /* access modifiers changed from: private */
        public AnimatorSet animator;
        private CheckBox checkBox;
        /* access modifiers changed from: private */
        public Object currentWallpaper;
        private BackupImageView imageView;
        private ImageView imageView2;
        /* access modifiers changed from: private */
        public boolean isSelected;
        private View selector;

        public WallpaperView(Context context) {
            super(context);
            setWillNotDraw(false);
            AnonymousClass1 r1 = new BackupImageView(context, WallpaperCell.this) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (WallpaperView.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
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
            this.imageView = r1;
            addView(r1, LayoutHelper.createFrame(-1, -1, 51));
            ImageView imageView3 = new ImageView(context);
            this.imageView2 = imageView3;
            imageView3.setImageResource(NUM);
            this.imageView2.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView2, LayoutHelper.createFrame(-1, -1, 51));
            View view = new View(context);
            this.selector = view;
            view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
            CheckBox checkBox2 = new CheckBox(context, NUM);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 2.0f, 2.0f, 0.0f));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
            }
            return super.onTouchEvent(motionEvent);
        }

        public void setWallpaper(Object obj, String str, Drawable drawable, boolean z) {
            Object obj2 = obj;
            String str2 = str;
            this.currentWallpaper = obj2;
            this.imageView.setVisibility(0);
            this.imageView2.setVisibility(4);
            TLRPC$PhotoSize tLRPC$PhotoSize = null;
            this.imageView.setBackgroundDrawable((Drawable) null);
            this.imageView.getImageReceiver().setColorFilter((ColorFilter) null);
            this.imageView.getImageReceiver().setAlpha(1.0f);
            if (obj2 instanceof TLRPC$TL_wallPaper) {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) obj2;
                this.isSelected = str2.equals(tLRPC$TL_wallPaper.slug);
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_wallPaper.document.thumbs, 100);
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_wallPaper.document.thumbs, 320);
                if (closestPhotoSizeWithSize2 != closestPhotoSizeWithSize) {
                    tLRPC$PhotoSize = closestPhotoSizeWithSize2;
                }
                int i = tLRPC$PhotoSize != null ? tLRPC$PhotoSize.size : tLRPC$TL_wallPaper.document.size;
                if (tLRPC$TL_wallPaper.pattern) {
                    this.imageView.setBackgroundColor(tLRPC$TL_wallPaper.settings.background_color | -16777216);
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$PhotoSize, tLRPC$TL_wallPaper.document), "100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$TL_wallPaper.document), (String) null, "jpg", i, 1, tLRPC$TL_wallPaper);
                    this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(tLRPC$TL_wallPaper.settings.background_color), PorterDuff.Mode.SRC_IN));
                    this.imageView.getImageReceiver().setAlpha(((float) tLRPC$TL_wallPaper.settings.intensity) / 100.0f);
                } else if (tLRPC$PhotoSize != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$PhotoSize, tLRPC$TL_wallPaper.document), "100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$TL_wallPaper.document), "100_100_b", "jpg", i, 1, tLRPC$TL_wallPaper);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$TL_wallPaper.document), "100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$TL_wallPaper.document), "100_100_b", "jpg", i, 1, tLRPC$TL_wallPaper);
                }
            } else if (obj2 instanceof WallpapersListActivity.ColorWallpaper) {
                WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj2;
                File file = colorWallpaper.path;
                if (file != null) {
                    this.imageView.setImage(file.getAbsolutePath(), "100_100", (Drawable) null);
                } else {
                    this.imageView.setImageBitmap((Bitmap) null);
                    if (colorWallpaper.gradientColor != 0) {
                        this.imageView.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{colorWallpaper.color | -16777216, colorWallpaper.gradientColor | -16777216}));
                    } else {
                        this.imageView.setBackgroundColor(colorWallpaper.color | -16777216);
                    }
                }
                this.isSelected = str2.equals(colorWallpaper.slug);
            } else if (obj2 instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj2;
                this.isSelected = str2.equals(fileWallpaper.slug);
                File file2 = fileWallpaper.originalPath;
                if (file2 != null) {
                    this.imageView.setImage(file2.getAbsolutePath(), "100_100", (Drawable) null);
                    return;
                }
                File file3 = fileWallpaper.path;
                if (file3 != null) {
                    this.imageView.setImage(file3.getAbsolutePath(), "100_100", (Drawable) null);
                } else if ("t".equals(fileWallpaper.slug)) {
                    BackupImageView backupImageView = this.imageView;
                    backupImageView.setImageDrawable(Theme.getThemedWallpaper(true, backupImageView));
                } else {
                    this.imageView.setImageResource(fileWallpaper.thumbResId);
                }
            } else if (obj2 instanceof MediaController.SearchImage) {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj2;
                TLRPC$Photo tLRPC$Photo = searchImage.photo;
                if (tLRPC$Photo != null) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, 100);
                    TLRPC$PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(searchImage.photo.sizes, 320);
                    if (closestPhotoSizeWithSize4 != closestPhotoSizeWithSize3) {
                        tLRPC$PhotoSize = closestPhotoSizeWithSize4;
                    }
                    this.imageView.setImage(ImageLocation.getForPhoto(tLRPC$PhotoSize, searchImage.photo), "100_100", ImageLocation.getForPhoto(closestPhotoSizeWithSize3, searchImage.photo), "100_100_b", "jpg", tLRPC$PhotoSize != null ? tLRPC$PhotoSize.size : 0, 1, searchImage);
                    return;
                }
                this.imageView.setImage(searchImage.thumbUrl, "100_100", (Drawable) null);
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
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (WallpaperView.this.animator != null && WallpaperView.this.animator.equals(animator)) {
                            AnimatorSet unused = WallpaperView.this.animator = null;
                            if (!z) {
                                WallpaperView.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (WallpaperView.this.animator != null && WallpaperView.this.animator.equals(animator)) {
                            AnimatorSet unused = WallpaperView.this.animator = null;
                        }
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

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.checkBox.isChecked() || !this.imageView.getImageReceiver().hasBitmapImage() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), WallpaperCell.this.backgroundPaint);
            }
        }
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
                wallpaperView.setOnClickListener(new View.OnClickListener(wallpaperView, i) {
                    public final /* synthetic */ WallpaperCell.WallpaperView f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(View view) {
                        WallpaperCell.this.lambda$new$0$WallpaperCell(this.f$1, this.f$2, view);
                    }
                });
                wallpaperView.setOnLongClickListener(new View.OnLongClickListener(wallpaperView, i) {
                    public final /* synthetic */ WallpaperCell.WallpaperView f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final boolean onLongClick(View view) {
                        return WallpaperCell.this.lambda$new$1$WallpaperCell(this.f$1, this.f$2, view);
                    }
                });
                i++;
            } else {
                Paint paint = new Paint();
                this.framePaint = paint;
                paint.setColor(NUM);
                this.circlePaint = new Paint(1);
                this.checkDrawable = context.getResources().getDrawable(NUM).mutate();
                Paint paint2 = new Paint();
                this.backgroundPaint = paint2;
                paint2.setColor(Theme.getColor("sharedMedia_photoPlaceholder"));
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = size - AndroidUtilities.dp((float) (((this.spanCount - 1) * 6) + 28));
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
                this.wallpaperViews[i4].measure(View.MeasureSpec.makeMeasureSpec(i4 == i5 + -1 ? dp : i3, NUM), View.MeasureSpec.makeMeasureSpec(dp2, NUM));
                dp -= i3;
                i4++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
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

    public void setWallpaper(int i, int i2, Object obj, String str, Drawable drawable, boolean z) {
        this.currentType = i;
        if (obj == null) {
            this.wallpaperViews[i2].setVisibility(8);
            this.wallpaperViews[i2].clearAnimation();
            return;
        }
        this.wallpaperViews[i2].setVisibility(0);
        this.wallpaperViews[i2].setWallpaper(obj, str, drawable, z);
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
