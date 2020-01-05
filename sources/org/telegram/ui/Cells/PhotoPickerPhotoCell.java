package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoPickerPhotoCell extends FrameLayout {
    private AnimatorSet animator;
    private AnimatorSet animatorSet;
    private Paint backgroundPaint = new Paint();
    public CheckBox checkBox;
    public FrameLayout checkFrame;
    public BackupImageView imageView;
    public int itemWidth;
    private PhotoEntry photoEntry;
    public FrameLayout videoInfoContainer;
    public TextView videoTextView;
    private boolean zoomOnSelect;

    public PhotoPickerPhotoCell(Context context, boolean z) {
        super(context);
        setWillNotDraw(false);
        this.zoomOnSelect = z;
        this.imageView = new BackupImageView(context);
        addView(this.imageView, LayoutHelper.createFrame(-1, -1.0f));
        this.checkFrame = new FrameLayout(context);
        addView(this.checkFrame, LayoutHelper.createFrame(42, 42, 53));
        this.videoInfoContainer = new FrameLayout(context);
        this.videoInfoContainer.setBackgroundResource(NUM);
        this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        addView(this.videoInfoContainer, LayoutHelper.createFrame(-1, 16, 83));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(NUM);
        this.videoInfoContainer.addView(imageView, LayoutHelper.createFrame(-2, -2, 19));
        this.videoTextView = new TextView(context);
        this.videoTextView.setTextColor(-1);
        this.videoTextView.setTextSize(1, 12.0f);
        this.videoTextView.setImportantForAccessibility(2);
        this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, -0.7f, 0.0f, 0.0f));
        this.checkBox = new CheckBox(context, NUM);
        this.checkBox.setSize(z ? 30 : 26);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0f));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(Theme.getColor("dialogFloatingButton"), -1);
        addView(this.checkBox, LayoutHelper.createFrame(z ? 30 : 26, z ? 30.0f : 26.0f, 53, 0.0f, 4.0f, 4.0f, 0.0f));
        setFocusable(true);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(this.itemWidth, NUM), MeasureSpec.makeMeasureSpec(this.itemWidth, NUM));
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
    }

    public void updateColors() {
        this.checkBox.setColor(Theme.getColor("dialogFloatingButton"), -1);
    }

    public void showCheck(boolean z) {
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        this.animatorSet = new AnimatorSet();
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(180);
        animatorSet = this.animatorSet;
        Animator[] animatorArr = new Animator[2];
        FrameLayout frameLayout = this.videoInfoContainer;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        String str = "alpha";
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
        CheckBox checkBox = this.checkBox;
        fArr = new float[1];
        if (!z) {
            f = 0.0f;
        }
        fArr[0] = f;
        animatorArr[1] = ObjectAnimator.ofFloat(checkBox, str, fArr);
        animatorSet.playTogether(animatorArr);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PhotoPickerPhotoCell.this.animatorSet)) {
                    PhotoPickerPhotoCell.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
    }

    public void setNum(int i) {
        this.checkBox.setNum(i);
    }

    public void setImage(PhotoEntry photoEntry) {
        Drawable drawable = this.zoomOnSelect ? Theme.chat_attachEmptyDrawable : getResources().getDrawable(NUM);
        this.photoEntry = photoEntry;
        photoEntry = this.photoEntry;
        String str = photoEntry.thumbPath;
        if (str != null) {
            this.imageView.setImage(str, null, drawable);
        } else if (photoEntry.path != null) {
            this.imageView.setOrientation(photoEntry.orientation, true);
            str = ":";
            BackupImageView backupImageView;
            StringBuilder stringBuilder;
            if (this.photoEntry.isVideo) {
                this.videoInfoContainer.setVisibility(0);
                this.videoTextView.setText(AndroidUtilities.formatShortDuration(this.photoEntry.duration));
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(LocaleController.getString("AttachVideo", NUM));
                stringBuilder2.append(", ");
                stringBuilder2.append(LocaleController.formatCallDuration(this.photoEntry.duration));
                setContentDescription(stringBuilder2.toString());
                backupImageView = this.imageView;
                stringBuilder = new StringBuilder();
                stringBuilder.append("vthumb://");
                stringBuilder.append(this.photoEntry.imageId);
                stringBuilder.append(str);
                stringBuilder.append(this.photoEntry.path);
                backupImageView.setImage(stringBuilder.toString(), null, drawable);
                return;
            }
            this.videoInfoContainer.setVisibility(4);
            setContentDescription(LocaleController.getString("AttachPhoto", NUM));
            backupImageView = this.imageView;
            stringBuilder = new StringBuilder();
            stringBuilder.append("thumb://");
            stringBuilder.append(this.photoEntry.imageId);
            stringBuilder.append(str);
            stringBuilder.append(this.photoEntry.path);
            backupImageView.setImage(stringBuilder.toString(), null, drawable);
        } else {
            this.imageView.setImageDrawable(drawable);
        }
    }

    public void setImage(SearchImage searchImage) {
        Drawable drawable = this.zoomOnSelect ? Theme.chat_attachEmptyDrawable : getResources().getDrawable(NUM);
        PhotoSize photoSize = searchImage.thumbPhotoSize;
        if (photoSize != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(photoSize, searchImage.photo), null, drawable, (Object) searchImage);
            return;
        }
        photoSize = searchImage.photoSize;
        if (photoSize != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(photoSize, searchImage.photo), "80_80", drawable, (Object) searchImage);
            return;
        }
        String str = searchImage.thumbPath;
        if (str != null) {
            this.imageView.setImage(str, null, drawable);
            return;
        }
        str = searchImage.thumbUrl;
        if (str != null && str.length() > 0) {
            this.imageView.setImage(searchImage.thumbUrl, null, drawable);
        } else if (MessageObject.isDocumentHasThumb(searchImage.document)) {
            this.imageView.setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(searchImage.document.thumbs, 320), searchImage.document), null, drawable, (Object) searchImage);
        } else {
            this.imageView.setImageDrawable(drawable);
        }
    }

    public void setChecked(int i, final boolean z, boolean z2) {
        this.checkBox.setChecked(i, z, z2);
        AnimatorSet animatorSet = this.animator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animator = null;
        }
        if (this.zoomOnSelect) {
            float f = 0.85f;
            if (z2) {
                this.animator = new AnimatorSet();
                AnimatorSet animatorSet2 = this.animator;
                Animator[] animatorArr = new Animator[2];
                BackupImageView backupImageView = this.imageView;
                Property property = View.SCALE_X;
                float[] fArr = new float[1];
                fArr[0] = z ? 0.85f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, property, fArr);
                backupImageView = this.imageView;
                property = View.SCALE_Y;
                fArr = new float[1];
                if (!z) {
                    f = 1.0f;
                }
                fArr[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(backupImageView, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animator)) {
                            PhotoPickerPhotoCell.this.animator = null;
                            if (!z) {
                                PhotoPickerPhotoCell.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animator)) {
                            PhotoPickerPhotoCell.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            this.imageView.setScaleX(z ? 0.85f : 1.0f);
            BackupImageView backupImageView2 = this.imageView;
            if (!z) {
                f = 1.0f;
            }
            backupImageView2.setScaleY(f);
        }
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Missing block: B:14:0x003d, code skipped:
            if (org.telegram.ui.PhotoViewer.isShowingImage(r0.path) != false) goto L_0x003f;
     */
    public void onDraw(android.graphics.Canvas r9) {
        /*
        r8 = this;
        r0 = r8.zoomOnSelect;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r8.checkBox;
        r0 = r0.isChecked();
        if (r0 != 0) goto L_0x003f;
    L_0x000d:
        r0 = r8.imageView;
        r0 = r0.getScaleX();
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 != 0) goto L_0x003f;
    L_0x0019:
        r0 = r8.imageView;
        r0 = r0.getImageReceiver();
        r0 = r0.hasNotThumb();
        if (r0 == 0) goto L_0x003f;
    L_0x0025:
        r0 = r8.imageView;
        r0 = r0.getImageReceiver();
        r0 = r0.getCurrentAlpha();
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 != 0) goto L_0x003f;
    L_0x0033:
        r0 = r8.photoEntry;
        if (r0 == 0) goto L_0x0060;
    L_0x0037:
        r0 = r0.path;
        r0 = org.telegram.ui.PhotoViewer.isShowingImage(r0);
        if (r0 == 0) goto L_0x0060;
    L_0x003f:
        r0 = r8.backgroundPaint;
        r1 = "chat_attachPhotoBackground";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setColor(r1);
        r3 = 0;
        r4 = 0;
        r0 = r8.imageView;
        r0 = r0.getMeasuredWidth();
        r5 = (float) r0;
        r0 = r8.imageView;
        r0 = r0.getMeasuredHeight();
        r6 = (float) r0;
        r7 = r8.backgroundPaint;
        r2 = r9;
        r2.drawRect(r3, r4, r5, r6, r7);
    L_0x0060:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.PhotoPickerPhotoCell.onDraw(android.graphics.Canvas):void");
    }
}
