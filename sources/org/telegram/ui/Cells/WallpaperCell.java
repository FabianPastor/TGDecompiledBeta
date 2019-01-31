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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
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
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (WallpaperView.this.currentWallpaper instanceof ColorWallpaper) {
                        canvas.drawLine(1.0f, 0.0f, (float) (getMeasuredWidth() - 1), 0.0f, WallpaperCell.this.framePaint);
                        canvas.drawLine(0.0f, 0.0f, 0.0f, (float) getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas.drawLine((float) (getMeasuredWidth() - 1), 0.0f, (float) (getMeasuredWidth() - 1), (float) getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas.drawLine(1.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - 1), (float) (getMeasuredHeight() - 1), WallpaperCell.this.framePaint);
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
            addView(this.imageView, LayoutHelper.createFrame(-1, -1, 51));
            this.imageView2 = new ImageView(context);
            this.imageView2.setImageResource(R.drawable.ic_gallery_background);
            this.imageView2.setScaleType(ScaleType.CENTER);
            addView(this.imageView2, LayoutHelper.createFrame(-1, -1, 51));
            this.selector = new View(context);
            this.selector.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
            this.checkBox = new CheckBox(context, R.drawable.round_check2);
            this.checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 2.0f, 2.0f, 0.0f));
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(event.getX(), event.getY());
            }
            return super.onTouchEvent(event);
        }

        public void setWallpaper(Object object, long selectedBackground, Drawable themedWallpaper, boolean themed) {
            this.currentWallpaper = object;
            if (object == null) {
                this.imageView.setVisibility(4);
                this.imageView2.setVisibility(0);
                if (themed) {
                    this.imageView2.setImageDrawable(themedWallpaper);
                    this.imageView2.setScaleType(ScaleType.CENTER_CROP);
                    return;
                }
                ImageView imageView = this.imageView2;
                int i = (selectedBackground == -1 || selectedBackground == 1000001) ? NUM : NUM;
                imageView.setBackgroundColor(i);
                this.imageView2.setScaleType(ScaleType.CENTER);
                this.imageView2.setImageResource(R.drawable.ic_gallery_background);
                return;
            }
            this.imageView.setVisibility(0);
            this.imageView2.setVisibility(4);
            this.imageView.setBackgroundDrawable(null);
            this.imageView.getImageReceiver().setColorFilter(null);
            this.imageView.getImageReceiver().setAlpha(1.0f);
            if (object instanceof TL_wallPaper) {
                TL_wallPaper wallPaper = (TL_wallPaper) object;
                this.isSelected = wallPaper.id == selectedBackground;
                TLObject thumb = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 100);
                TLObject image = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 320);
                if (image == thumb) {
                    image = null;
                }
                int size = image != null ? image.size : wallPaper.document.size;
                if (wallPaper.pattern) {
                    this.imageView.setBackgroundColor(-16777216 | wallPaper.settings.background_color);
                    this.imageView.setImage(image, "100_100", thumb, null, "jpg", size, 1, wallPaper);
                    this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(wallPaper.settings.background_color), Mode.SRC_IN));
                    this.imageView.getImageReceiver().setAlpha(((float) wallPaper.settings.intensity) / 100.0f);
                    return;
                }
                this.imageView.setImage(image != null ? image : wallPaper.document, "100_100", thumb, "100_100_b", "jpg", size, 1, wallPaper);
            } else if (object instanceof ColorWallpaper) {
                ColorWallpaper wallPaper2 = (ColorWallpaper) object;
                if (wallPaper2.path != null) {
                    this.imageView.setImage(wallPaper2.path.getAbsolutePath(), "100_100", null);
                } else {
                    this.imageView.setImageBitmap(null);
                    this.imageView.setBackgroundColor(-16777216 | wallPaper2.color);
                }
                this.isSelected = wallPaper2.id == selectedBackground;
            } else if (object instanceof FileWallpaper) {
                FileWallpaper wallPaper3 = (FileWallpaper) object;
                this.isSelected = wallPaper3.id == selectedBackground;
                if (wallPaper3.originalPath != null) {
                    this.imageView.setImage(wallPaper3.originalPath.getAbsolutePath(), "100_100", null);
                } else if (wallPaper3.path != null) {
                    this.imageView.setImage(wallPaper3.path.getAbsolutePath(), "100_100", null);
                } else if (((long) wallPaper3.resId) == -2) {
                    this.imageView.setImageDrawable(Theme.getThemedWallpaper(true));
                } else {
                    this.imageView.setImageResource(wallPaper3.thumbResId);
                }
            } else if (object instanceof SearchImage) {
                SearchImage wallPaper4 = (SearchImage) object;
                if (wallPaper4.photo != null) {
                    PhotoSize thumb2 = FileLoader.getClosestPhotoSizeWithSize(wallPaper4.photo.sizes, 100);
                    PhotoSize image2 = FileLoader.getClosestPhotoSizeWithSize(wallPaper4.photo.sizes, 320);
                    if (image2 == thumb2) {
                        image2 = null;
                    }
                    this.imageView.setImage(image2, "100_100", thumb2, "100_100_b", "jpg", image2 != null ? image2.size : 0, 1, wallPaper4);
                    return;
                }
                this.imageView.setImage(wallPaper4.thumbUrl, "100_100", null);
            } else {
                this.isSelected = false;
            }
        }

        public void setChecked(final boolean checked, boolean animated) {
            float f = 0.8875f;
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
            if (this.animator != null) {
                this.animator.cancel();
                this.animator = null;
            }
            BackupImageView backupImageView;
            if (animated) {
                this.animator = new AnimatorSet();
                AnimatorSet animatorSet = this.animator;
                Animator[] animatorArr = new Animator[2];
                BackupImageView backupImageView2 = this.imageView;
                String str = "scaleX";
                float[] fArr = new float[1];
                fArr[0] = checked ? 0.8875f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(backupImageView2, str, fArr);
                backupImageView = this.imageView;
                String str2 = "scaleY";
                float[] fArr2 = new float[1];
                if (!checked) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(backupImageView, str2, fArr2);
                animatorSet.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (WallpaperView.this.animator != null && WallpaperView.this.animator.equals(animation)) {
                            WallpaperView.this.animator = null;
                            if (!checked) {
                                WallpaperView.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (WallpaperView.this.animator != null && WallpaperView.this.animator.equals(animation)) {
                            WallpaperView.this.animator = null;
                        }
                    }
                });
                this.animator.start();
            } else {
                float f2;
                BackupImageView backupImageView3 = this.imageView;
                if (checked) {
                    f2 = 0.8875f;
                } else {
                    f2 = 1.0f;
                }
                backupImageView3.setScaleX(f2);
                backupImageView = this.imageView;
                if (!checked) {
                    f = 1.0f;
                }
                backupImageView.setScaleY(f);
            }
            invalidate();
        }

        public void invalidate() {
            super.invalidate();
            this.imageView.invalidate();
        }

        public void clearAnimation() {
            super.clearAnimation();
            if (this.animator != null) {
                this.animator.cancel();
                this.animator = null;
            }
        }

        protected void onDraw(Canvas canvas) {
            if (this.checkBox.isChecked() || !this.imageView.getImageReceiver().hasBitmapImage() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), WallpaperCell.this.backgroundPaint);
            }
        }
    }

    public WallpaperCell(Context context) {
        super(context);
        for (int a = 0; a < this.wallpaperViews.length; a++) {
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            WallpaperView wallpaperView = new WallpaperView(context);
            wallpaperViewArr[a] = wallpaperView;
            int num = a;
            addView(wallpaperView);
            wallpaperView.setOnClickListener(new WallpaperCell$$Lambda$0(this, wallpaperView, num));
            wallpaperView.setOnLongClickListener(new WallpaperCell$$Lambda$1(this, wallpaperView, num));
        }
        this.framePaint = new Paint();
        this.framePaint.setColor(NUM);
        this.circlePaint = new Paint(1);
        this.checkDrawable = context.getResources().getDrawable(R.drawable.background_selected).mutate();
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(Theme.getColor("sharedMedia_photoPlaceholder"));
    }

    final /* synthetic */ void lambda$new$0$WallpaperCell(WallpaperView wallpaperView, int num, View v) {
        onWallpaperClick(wallpaperView.currentWallpaper, num);
    }

    final /* synthetic */ boolean lambda$new$1$WallpaperCell(WallpaperView wallpaperView, int num, View v) {
        return onWallpaperLongClick(wallpaperView.currentWallpaper, num);
    }

    protected void onWallpaperClick(Object wallPaper, int index) {
    }

    protected boolean onWallpaperLongClick(Object wallPaper, int index) {
        return false;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = width - AndroidUtilities.dp((float) (((this.spanCount - 1) * 6) + 28));
        int itemWidth = availableWidth / this.spanCount;
        if (this.currentType == 0) {
            height = AndroidUtilities.dp(180.0f);
        } else {
            height = itemWidth;
        }
        setMeasuredDimension(width, AndroidUtilities.dp(this.isBottom ? 14.0f : 6.0f) + (height + (this.isTop ? AndroidUtilities.dp(14.0f) : 0)));
        for (int a = 0; a < this.spanCount; a++) {
            int i;
            WallpaperView wallpaperView = this.wallpaperViews[a];
            if (a == this.spanCount - 1) {
                i = availableWidth;
            } else {
                i = itemWidth;
            }
            wallpaperView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
            availableWidth -= itemWidth;
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int l = AndroidUtilities.dp(14.0f);
        int t = this.isTop ? AndroidUtilities.dp(14.0f) : 0;
        for (int a = 0; a < this.spanCount; a++) {
            int w = this.wallpaperViews[a].getMeasuredWidth();
            this.wallpaperViews[a].layout(l, t, l + w, this.wallpaperViews[a].getMeasuredHeight() + t);
            l += AndroidUtilities.dp(6.0f) + w;
        }
    }

    public void setParams(int columns, boolean top, boolean bottom) {
        this.spanCount = columns;
        this.isTop = top;
        this.isBottom = bottom;
        int a = 0;
        while (a < this.wallpaperViews.length) {
            this.wallpaperViews[a].setVisibility(a < columns ? 0 : 8);
            this.wallpaperViews[a].clearAnimation();
            a++;
        }
    }

    public void setWallpaper(int type, int index, Object wallpaper, long selectedBackground, Drawable themedWallpaper, boolean themed) {
        this.currentType = type;
        if (wallpaper == null) {
            this.wallpaperViews[index].setVisibility(8);
            this.wallpaperViews[index].clearAnimation();
            return;
        }
        this.wallpaperViews[index].setVisibility(0);
        this.wallpaperViews[index].setWallpaper(wallpaper, selectedBackground, themedWallpaper, themed);
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
