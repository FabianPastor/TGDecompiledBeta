package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
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

    private class WallpaperView extends FrameLayout {
        /* access modifiers changed from: private */
        public AnimatorSet animator;
        private AnimatorSet animatorSet;
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
                    if ((WallpaperView.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) || (WallpaperView.this.currentWallpaper instanceof WallpapersListActivity.FileWallpaper)) {
                        Canvas canvas2 = canvas;
                        canvas2.drawLine(1.0f, 0.0f, (float) (getMeasuredWidth() - 1), 0.0f, WallpaperCell.this.framePaint);
                        Canvas canvas3 = canvas;
                        canvas3.drawLine(0.0f, 0.0f, 0.0f, (float) getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas2.drawLine((float) (getMeasuredWidth() - 1), 0.0f, (float) (getMeasuredWidth() - 1), (float) getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas3.drawLine(1.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - 1), (float) (getMeasuredHeight() - 1), WallpaperCell.this.framePaint);
                    }
                    if (WallpaperView.this.isSelected) {
                        WallpaperCell.this.circlePaint.setColor(Theme.serviceMessageColorBackup);
                        int cx = getMeasuredWidth() / 2;
                        int cy = getMeasuredHeight() / 2;
                        canvas.drawCircle((float) cx, (float) cy, (float) AndroidUtilities.dp(20.0f), WallpaperCell.this.circlePaint);
                        WallpaperCell.this.checkDrawable.setBounds(cx - (WallpaperCell.this.checkDrawable.getIntrinsicWidth() / 2), cy - (WallpaperCell.this.checkDrawable.getIntrinsicHeight() / 2), (WallpaperCell.this.checkDrawable.getIntrinsicWidth() / 2) + cx, (WallpaperCell.this.checkDrawable.getIntrinsicHeight() / 2) + cy);
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

        public boolean onTouchEvent(MotionEvent event) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(event.getX(), event.getY());
            }
            return super.onTouchEvent(event);
        }

        public void setWallpaper(Object object, Object selectedWallpaper, Drawable themedWallpaper, boolean themed) {
            int patternColor;
            int patternColor2;
            Object obj = object;
            this.currentWallpaper = obj;
            this.imageView.setVisibility(0);
            this.imageView2.setVisibility(4);
            this.imageView.setBackgroundDrawable((Drawable) null);
            this.imageView.getImageReceiver().setColorFilter((ColorFilter) null);
            this.imageView.getImageReceiver().setAlpha(1.0f);
            this.imageView.getImageReceiver().setBlendMode((Object) null);
            this.imageView.getImageReceiver().setGradientBitmap((Bitmap) null);
            this.isSelected = obj == selectedWallpaper;
            if (obj instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) obj;
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 100);
                TLRPC.PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 320);
                if (image == thumb) {
                    image = null;
                }
                int size = image != null ? image.size : wallPaper.document.size;
                if (wallPaper.pattern) {
                    if (wallPaper.settings.third_background_color != 0) {
                        MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.third_background_color, wallPaper.settings.fourth_background_color, true);
                        if (wallPaper.settings.intensity >= 0 || !Theme.getActiveTheme().isDark()) {
                            this.imageView.setBackground(motionBackgroundDrawable);
                            if (Build.VERSION.SDK_INT >= 29) {
                                this.imageView.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
                            }
                        } else {
                            this.imageView.getImageReceiver().setGradientBitmap(motionBackgroundDrawable.getBitmap());
                        }
                        patternColor2 = MotionBackgroundDrawable.getPatternColor(wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.third_background_color, wallPaper.settings.fourth_background_color);
                    } else {
                        this.imageView.setBackgroundColor(Theme.getWallpaperColor(wallPaper.settings.background_color));
                        patternColor2 = AndroidUtilities.getPatternColor(wallPaper.settings.background_color);
                    }
                    if (Build.VERSION.SDK_INT < 29 || wallPaper.settings.third_background_color == 0) {
                        this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(patternColor2), PorterDuff.Mode.SRC_IN));
                    }
                    if (image != null) {
                        this.imageView.setImage(ImageLocation.getForDocument(image, wallPaper.document), "100_100", ImageLocation.getForDocument(thumb, wallPaper.document), (String) null, "jpg", size, 1, wallPaper);
                    } else {
                        this.imageView.setImage(ImageLocation.getForDocument(thumb, wallPaper.document), "100_100", (ImageLocation) null, (String) null, "jpg", size, 1, wallPaper);
                    }
                    this.imageView.getImageReceiver().setAlpha(((float) Math.abs(wallPaper.settings.intensity)) / 100.0f);
                } else if (image != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(image, wallPaper.document), "100_100", ImageLocation.getForDocument(thumb, wallPaper.document), "100_100_b", "jpg", size, 1, wallPaper);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(wallPaper.document), "100_100", ImageLocation.getForDocument(thumb, wallPaper.document), "100_100_b", "jpg", size, 1, wallPaper);
                }
            } else if (obj instanceof WallpapersListActivity.ColorWallpaper) {
                WallpapersListActivity.ColorWallpaper wallPaper2 = (WallpapersListActivity.ColorWallpaper) obj;
                if (wallPaper2.path == null && wallPaper2.pattern == null && !"d".equals(wallPaper2.slug)) {
                    this.imageView.setImageBitmap((Bitmap) null);
                    if (wallPaper2.isGradient) {
                        this.imageView.setBackground(new MotionBackgroundDrawable(wallPaper2.color, wallPaper2.gradientColor1, wallPaper2.gradientColor2, wallPaper2.gradientColor3, true));
                    } else if (wallPaper2.gradientColor1 != 0) {
                        this.imageView.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{wallPaper2.color | -16777216, wallPaper2.gradientColor1 | -16777216}));
                    } else {
                        this.imageView.setBackgroundColor(wallPaper2.color | -16777216);
                    }
                } else {
                    if (wallPaper2.gradientColor2 != 0) {
                        MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable(wallPaper2.color, wallPaper2.gradientColor1, wallPaper2.gradientColor2, wallPaper2.gradientColor3, true);
                        if (wallPaper2.intensity >= 0.0f) {
                            this.imageView.setBackground(new MotionBackgroundDrawable(wallPaper2.color, wallPaper2.gradientColor1, wallPaper2.gradientColor2, wallPaper2.gradientColor3, true));
                            if (Build.VERSION.SDK_INT >= 29) {
                                this.imageView.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
                            }
                        } else {
                            this.imageView.getImageReceiver().setGradientBitmap(motionBackgroundDrawable2.getBitmap());
                        }
                        patternColor = MotionBackgroundDrawable.getPatternColor(wallPaper2.color, wallPaper2.gradientColor1, wallPaper2.gradientColor2, wallPaper2.gradientColor3);
                    } else {
                        patternColor = AndroidUtilities.getPatternColor(wallPaper2.color);
                    }
                    if ("d".equals(wallPaper2.slug)) {
                        if (wallPaper2.defaultCache == null) {
                            wallPaper2.defaultCache = SvgHelper.getBitmap(NUM, 100, 180, -16777216);
                        }
                        this.imageView.setImageBitmap(wallPaper2.defaultCache);
                        this.imageView.getImageReceiver().setAlpha(Math.abs(wallPaper2.intensity));
                    } else if (wallPaper2.path != null) {
                        this.imageView.setImage(wallPaper2.path.getAbsolutePath(), "100_100", (Drawable) null);
                    } else {
                        TLRPC.PhotoSize thumb2 = FileLoader.getClosestPhotoSizeWithSize(wallPaper2.pattern.document.thumbs, 100);
                        this.imageView.setImage(ImageLocation.getForDocument(thumb2, wallPaper2.pattern.document), "100_100", (ImageLocation) null, (String) null, "jpg", thumb2 != null ? thumb2.size : wallPaper2.pattern.document.size, 1, wallPaper2.pattern);
                        this.imageView.getImageReceiver().setAlpha(Math.abs(wallPaper2.intensity));
                        if (Build.VERSION.SDK_INT < 29 || wallPaper2.gradientColor2 == 0) {
                            this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(patternColor), PorterDuff.Mode.SRC_IN));
                        }
                    }
                }
            } else if (obj instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper wallPaper3 = (WallpapersListActivity.FileWallpaper) obj;
                if (wallPaper3.originalPath != null) {
                    this.imageView.setImage(wallPaper3.originalPath.getAbsolutePath(), "100_100", (Drawable) null);
                } else if (wallPaper3.path != null) {
                    this.imageView.setImage(wallPaper3.path.getAbsolutePath(), "100_100", (Drawable) null);
                } else if ("t".equals(wallPaper3.slug)) {
                    BackupImageView backupImageView = this.imageView;
                    backupImageView.setImageDrawable(Theme.getThemedWallpaper(true, backupImageView));
                } else {
                    this.imageView.setImageResource(wallPaper3.thumbResId);
                }
            } else if (obj instanceof MediaController.SearchImage) {
                MediaController.SearchImage wallPaper4 = (MediaController.SearchImage) obj;
                if (wallPaper4.photo != null) {
                    TLRPC.PhotoSize thumb3 = FileLoader.getClosestPhotoSizeWithSize(wallPaper4.photo.sizes, 100);
                    TLRPC.PhotoSize image2 = FileLoader.getClosestPhotoSizeWithSize(wallPaper4.photo.sizes, 320);
                    if (image2 == thumb3) {
                        image2 = null;
                    }
                    this.imageView.setImage(ImageLocation.getForPhoto(image2, wallPaper4.photo), "100_100", ImageLocation.getForPhoto(thumb3, wallPaper4.photo), "100_100_b", "jpg", image2 != null ? image2.size : 0, 1, wallPaper4);
                    return;
                }
                this.imageView.setImage(wallPaper4.thumbUrl, "100_100", (Drawable) null);
            } else {
                this.isSelected = false;
            }
        }

        public void setChecked(final boolean checked, boolean animated) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
            AnimatorSet animatorSet2 = this.animator;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.animator = null;
            }
            float f = 0.8875f;
            if (animated) {
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animator = animatorSet3;
                Animator[] animatorArr = new Animator[2];
                BackupImageView backupImageView = this.imageView;
                float[] fArr = new float[1];
                fArr[0] = checked ? 0.8875f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, "scaleX", fArr);
                BackupImageView backupImageView2 = this.imageView;
                float[] fArr2 = new float[1];
                if (!checked) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(backupImageView2, "scaleY", fArr2);
                animatorSet3.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (WallpaperView.this.animator != null && WallpaperView.this.animator.equals(animation)) {
                            AnimatorSet unused = WallpaperView.this.animator = null;
                            if (!checked) {
                                WallpaperView.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (WallpaperView.this.animator != null && WallpaperView.this.animator.equals(animation)) {
                            AnimatorSet unused = WallpaperView.this.animator = null;
                        }
                    }
                });
                this.animator.start();
            } else {
                this.imageView.setScaleX(checked ? 0.8875f : 1.0f);
                BackupImageView backupImageView3 = this.imageView;
                if (!checked) {
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
            AnimatorSet animatorSet2 = this.animator;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
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
        int a = 0;
        while (true) {
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            if (a < wallpaperViewArr.length) {
                WallpaperView wallpaperView = new WallpaperView(context);
                wallpaperViewArr[a] = wallpaperView;
                WallpaperView wallpaperView2 = wallpaperView;
                int num = a;
                addView(wallpaperView2);
                wallpaperView2.setOnClickListener(new WallpaperCell$$ExternalSyntheticLambda0(this, wallpaperView2, num));
                wallpaperView2.setOnLongClickListener(new WallpaperCell$$ExternalSyntheticLambda1(this, wallpaperView2, num));
                a++;
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

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-WallpaperCell  reason: not valid java name */
    public /* synthetic */ void m1566lambda$new$0$orgtelegramuiCellsWallpaperCell(WallpaperView wallpaperView, int num, View v) {
        onWallpaperClick(wallpaperView.currentWallpaper, num);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-WallpaperCell  reason: not valid java name */
    public /* synthetic */ boolean m1567lambda$new$1$orgtelegramuiCellsWallpaperCell(WallpaperView wallpaperView, int num, View v) {
        return onWallpaperLongClick(wallpaperView.currentWallpaper, num);
    }

    /* access modifiers changed from: protected */
    public void onWallpaperClick(Object wallPaper, int index) {
    }

    /* access modifiers changed from: protected */
    public boolean onWallpaperLongClick(Object wallPaper, int index) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = width - AndroidUtilities.dp((float) (((this.spanCount - 1) * 6) + 28));
        int itemWidth = availableWidth / this.spanCount;
        int height = this.currentType == 0 ? AndroidUtilities.dp(180.0f) : itemWidth;
        float f = 14.0f;
        int dp = (this.isTop ? AndroidUtilities.dp(14.0f) : 0) + height;
        if (!this.isBottom) {
            f = 6.0f;
        }
        setMeasuredDimension(width, dp + AndroidUtilities.dp(f));
        int a = 0;
        while (true) {
            int i = this.spanCount;
            if (a < i) {
                this.wallpaperViews[a].measure(View.MeasureSpec.makeMeasureSpec(a == i + -1 ? availableWidth : itemWidth, NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
                availableWidth -= itemWidth;
                a++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int l = AndroidUtilities.dp(14.0f);
        int t = this.isTop ? AndroidUtilities.dp(14.0f) : 0;
        for (int a = 0; a < this.spanCount; a++) {
            int w = this.wallpaperViews[a].getMeasuredWidth();
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            wallpaperViewArr[a].layout(l, t, l + w, wallpaperViewArr[a].getMeasuredHeight() + t);
            l += AndroidUtilities.dp(6.0f) + w;
        }
    }

    public void setParams(int columns, boolean top, boolean bottom) {
        this.spanCount = columns;
        this.isTop = top;
        this.isBottom = bottom;
        int a = 0;
        while (true) {
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            if (a < wallpaperViewArr.length) {
                wallpaperViewArr[a].setVisibility(a < columns ? 0 : 8);
                this.wallpaperViews[a].clearAnimation();
                a++;
            } else {
                return;
            }
        }
    }

    public void setWallpaper(int type, int index, Object wallpaper, Object selectedWallpaper, Drawable themedWallpaper, boolean themed) {
        this.currentType = type;
        if (wallpaper == null) {
            this.wallpaperViews[index].setVisibility(8);
            this.wallpaperViews[index].clearAnimation();
            return;
        }
        this.wallpaperViews[index].setVisibility(0);
        this.wallpaperViews[index].setWallpaper(wallpaper, selectedWallpaper, themedWallpaper, themed);
    }

    public void setChecked(int index, boolean checked, boolean animated) {
        this.wallpaperViews[index].setChecked(checked, animated);
    }

    public void invalidate() {
        super.invalidate();
        for (int a = 0; a < this.spanCount; a++) {
            this.wallpaperViews[a].invalidate();
        }
    }
}
