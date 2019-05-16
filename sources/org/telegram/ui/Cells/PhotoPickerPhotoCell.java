package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoPickerPhotoCell extends FrameLayout {
    private AnimatorSet animator;
    private AnimatorSet animatorSet;
    public CheckBox checkBox;
    public FrameLayout checkFrame;
    public int itemWidth;
    public BackupImageView photoImage;
    public FrameLayout videoInfoContainer;
    public TextView videoTextView;
    private boolean zoomOnSelect;

    public PhotoPickerPhotoCell(Context context, boolean z) {
        super(context);
        this.zoomOnSelect = z;
        this.photoImage = new BackupImageView(context);
        addView(this.photoImage, LayoutHelper.createFrame(-1, -1.0f));
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
        this.checkBox.setColor(-10043398, -1);
        addView(this.checkBox, LayoutHelper.createFrame(z ? 30 : 26, z ? 30.0f : 26.0f, 53, 0.0f, 4.0f, 4.0f, 0.0f));
        setFocusable(true);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(this.itemWidth, NUM), MeasureSpec.makeMeasureSpec(this.itemWidth, NUM));
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

    public void setImage(SearchImage searchImage) {
        Drawable drawable = getResources().getDrawable(NUM);
        PhotoSize photoSize = searchImage.thumbPhotoSize;
        if (photoSize != null) {
            this.photoImage.setImage(ImageLocation.getForPhoto(photoSize, searchImage.photo), null, drawable, (Object) searchImage);
            return;
        }
        photoSize = searchImage.photoSize;
        if (photoSize != null) {
            this.photoImage.setImage(ImageLocation.getForPhoto(photoSize, searchImage.photo), "80_80", drawable, (Object) searchImage);
            return;
        }
        String str = searchImage.thumbPath;
        if (str != null) {
            this.photoImage.setImage(str, null, drawable);
            return;
        }
        str = searchImage.thumbUrl;
        if (str != null && str.length() > 0) {
            this.photoImage.setImage(searchImage.thumbUrl, null, drawable);
        } else if (MessageObject.isDocumentHasThumb(searchImage.document)) {
            this.photoImage.setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(searchImage.document.thumbs, 320), searchImage.document), null, drawable, (Object) searchImage);
        } else {
            this.photoImage.setImageDrawable(drawable);
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
            i = -16119286;
            float f = 0.85f;
            if (z2) {
                if (z) {
                    setBackgroundColor(-16119286);
                }
                this.animator = new AnimatorSet();
                animatorSet = this.animator;
                Animator[] animatorArr = new Animator[2];
                BackupImageView backupImageView = this.photoImage;
                float[] fArr = new float[1];
                fArr[0] = z ? 0.85f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, "scaleX", fArr);
                backupImageView = this.photoImage;
                fArr = new float[1];
                if (!z) {
                    f = 1.0f;
                }
                fArr[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(backupImageView, "scaleY", fArr);
                animatorSet.playTogether(animatorArr);
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
            if (!z) {
                i = 0;
            }
            setBackgroundColor(i);
            this.photoImage.setScaleX(z ? 0.85f : 1.0f);
            BackupImageView backupImageView2 = this.photoImage;
            if (!z) {
                f = 1.0f;
            }
            backupImageView2.setScaleY(f);
        }
    }
}
