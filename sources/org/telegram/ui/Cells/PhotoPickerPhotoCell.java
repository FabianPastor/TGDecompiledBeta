package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoViewer;

public class PhotoPickerPhotoCell extends FrameLayout {
    /* access modifiers changed from: private */
    public AnimatorSet animator;
    private Paint backgroundPaint = new Paint();
    public CheckBox checkBox;
    public FrameLayout checkFrame;
    public BackupImageView imageView;
    public int itemWidth;
    private MediaController.PhotoEntry photoEntry;
    public FrameLayout videoInfoContainer;
    public TextView videoTextView;
    private boolean zoomOnSelect;

    public PhotoPickerPhotoCell(Context context, boolean z) {
        super(context);
        setWillNotDraw(false);
        this.zoomOnSelect = z;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        this.checkFrame = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(42, 42, 53));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.videoInfoContainer = frameLayout2;
        frameLayout2.setBackgroundResource(NUM);
        this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        addView(this.videoInfoContainer, LayoutHelper.createFrame(-1, 16, 83));
        ImageView imageView2 = new ImageView(context);
        imageView2.setImageResource(NUM);
        this.videoInfoContainer.addView(imageView2, LayoutHelper.createFrame(-2, -2, 19));
        TextView textView = new TextView(context);
        this.videoTextView = textView;
        textView.setTextColor(-1);
        this.videoTextView.setTextSize(1, 12.0f);
        this.videoTextView.setImportantForAccessibility(2);
        this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, -0.7f, 0.0f, 0.0f));
        CheckBox checkBox2 = new CheckBox(context, NUM);
        this.checkBox = checkBox2;
        checkBox2.setSize(z ? 30 : 26);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0f));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(Theme.getColor("dialogFloatingButton"), -1);
        addView(this.checkBox, LayoutHelper.createFrame(z ? 30 : 26, z ? 30.0f : 26.0f, 53, 0.0f, 4.0f, 4.0f, 0.0f));
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemWidth, NUM), View.MeasureSpec.makeMeasureSpec(this.itemWidth, NUM));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
    }

    public void updateColors() {
        this.checkBox.setColor(Theme.getColor("dialogFloatingButton"), -1);
    }

    public void setNum(int i) {
        this.checkBox.setNum(i);
    }

    public void setImage(MediaController.PhotoEntry photoEntry2) {
        Drawable drawable = this.zoomOnSelect ? Theme.chat_attachEmptyDrawable : getResources().getDrawable(NUM);
        this.photoEntry = photoEntry2;
        String str = photoEntry2.thumbPath;
        if (str != null) {
            this.imageView.setImage(str, (String) null, drawable);
        } else if (photoEntry2.path != null) {
            this.imageView.setOrientation(photoEntry2.orientation, true);
            if (this.photoEntry.isVideo) {
                this.videoInfoContainer.setVisibility(0);
                this.videoTextView.setText(AndroidUtilities.formatShortDuration(this.photoEntry.duration));
                setContentDescription(LocaleController.getString("AttachVideo", NUM) + ", " + LocaleController.formatCallDuration(this.photoEntry.duration));
                BackupImageView backupImageView = this.imageView;
                backupImageView.setImage("vthumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, (String) null, drawable);
                return;
            }
            this.videoInfoContainer.setVisibility(4);
            setContentDescription(LocaleController.getString("AttachPhoto", NUM));
            BackupImageView backupImageView2 = this.imageView;
            backupImageView2.setImage("thumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, (String) null, drawable);
        } else {
            this.imageView.setImageDrawable(drawable);
        }
    }

    public void setImage(MediaController.SearchImage searchImage) {
        Drawable drawable = this.zoomOnSelect ? Theme.chat_attachEmptyDrawable : getResources().getDrawable(NUM);
        TLRPC$PhotoSize tLRPC$PhotoSize = searchImage.thumbPhotoSize;
        if (tLRPC$PhotoSize != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(tLRPC$PhotoSize, searchImage.photo), (String) null, drawable, (Object) searchImage);
            return;
        }
        TLRPC$PhotoSize tLRPC$PhotoSize2 = searchImage.photoSize;
        if (tLRPC$PhotoSize2 != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(tLRPC$PhotoSize2, searchImage.photo), "80_80", drawable, (Object) searchImage);
            return;
        }
        String str = searchImage.thumbPath;
        if (str != null) {
            this.imageView.setImage(str, (String) null, drawable);
            return;
        }
        String str2 = searchImage.thumbUrl;
        if (str2 != null && str2.length() > 0) {
            this.imageView.setImage(searchImage.thumbUrl, (String) null, drawable);
        } else if (MessageObject.isDocumentHasThumb(searchImage.document)) {
            this.imageView.setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(searchImage.document.thumbs, 320), searchImage.document), (String) null, drawable, (Object) searchImage);
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
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animator = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                BackupImageView backupImageView = this.imageView;
                Property property = View.SCALE_X;
                float[] fArr = new float[1];
                fArr[0] = z ? 0.85f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, property, fArr);
                BackupImageView backupImageView2 = this.imageView;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[1];
                if (!z) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(backupImageView2, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animator)) {
                            AnimatorSet unused = PhotoPickerPhotoCell.this.animator = null;
                            if (!z) {
                                PhotoPickerPhotoCell.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animator)) {
                            AnimatorSet unused = PhotoPickerPhotoCell.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            this.imageView.setScaleX(z ? 0.85f : 1.0f);
            BackupImageView backupImageView3 = this.imageView;
            if (!z) {
                f = 1.0f;
            }
            backupImageView3.setScaleY(f);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        MediaController.PhotoEntry photoEntry2;
        if (this.zoomOnSelect) {
            if (this.checkBox.isChecked() || this.imageView.getScaleX() != 1.0f || !this.imageView.getImageReceiver().hasNotThumb() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f || ((photoEntry2 = this.photoEntry) != null && PhotoViewer.isShowingImage(photoEntry2.path))) {
                this.backgroundPaint.setColor(Theme.getColor("chat_attachPhotoBackground"));
                canvas.drawRect(0.0f, 0.0f, (float) this.imageView.getMeasuredWidth(), (float) this.imageView.getMeasuredHeight(), this.backgroundPaint);
            }
        }
    }
}
