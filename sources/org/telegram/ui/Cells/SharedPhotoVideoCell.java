package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoViewer;

public class SharedPhotoVideoCell extends FrameLayout {
    private Paint backgroundPaint = new Paint();
    private int currentAccount = UserConfig.selectedAccount;
    private SharedPhotoVideoCellDelegate delegate;
    private boolean ignoreLayout;
    private int[] indeces;
    private boolean isFirst;
    private int itemsCount;
    private MessageObject[] messageObjects;
    private PhotoVideoView[] photoVideoViews;

    private class PhotoVideoView extends FrameLayout {
        private AnimatorSet animator;
        private CheckBox checkBox;
        private FrameLayout container;
        private MessageObject currentMessageObject;
        private BackupImageView imageView;
        private View selector;
        private FrameLayout videoInfoContainer;
        private TextView videoTextView;

        public PhotoVideoView(Context context) {
            super(context);
            setWillNotDraw(false);
            this.container = new FrameLayout(context);
            addView(this.container, LayoutHelper.createFrame(-1, -1.0f));
            this.imageView = new BackupImageView(context);
            this.imageView.getImageReceiver().setNeedsQualityThumb(true);
            this.imageView.getImageReceiver().setShouldGenerateQualityThumb(true);
            this.container.addView(this.imageView, LayoutHelper.createFrame(-1, -1.0f));
            this.videoInfoContainer = new FrameLayout(context, SharedPhotoVideoCell.this) {
                private RectF rect = new RectF();

                protected void onDraw(Canvas canvas) {
                    this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                }
            };
            this.videoInfoContainer.setWillNotDraw(false);
            this.videoInfoContainer.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), 0);
            this.container.addView(this.videoInfoContainer, LayoutHelper.createFrame(-2, 17.0f, 83, 4.0f, 0.0f, 0.0f, 4.0f));
            ImageView imageView1 = new ImageView(context);
            imageView1.setImageResource(R.drawable.play_mini_video);
            this.videoInfoContainer.addView(imageView1, LayoutHelper.createFrame(-2, -2, 19));
            this.videoTextView = new TextView(context);
            this.videoTextView.setTextColor(-1);
            this.videoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.videoTextView.setTextSize(1, 12.0f);
            this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 13.0f, -0.7f, 0.0f, 0.0f));
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

        public void setChecked(final boolean checked, boolean animated) {
            float f = 0.85f;
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
            if (this.animator != null) {
                this.animator.cancel();
                this.animator = null;
            }
            FrameLayout frameLayout;
            if (animated) {
                this.animator = new AnimatorSet();
                AnimatorSet animatorSet = this.animator;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout2 = this.container;
                String str = "scaleX";
                float[] fArr = new float[1];
                fArr[0] = checked ? 0.85f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, str, fArr);
                frameLayout = this.container;
                String str2 = "scaleY";
                float[] fArr2 = new float[1];
                if (!checked) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, str2, fArr2);
                animatorSet.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (PhotoVideoView.this.animator != null && PhotoVideoView.this.animator.equals(animation)) {
                            PhotoVideoView.this.animator = null;
                            if (!checked) {
                                PhotoVideoView.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (PhotoVideoView.this.animator != null && PhotoVideoView.this.animator.equals(animation)) {
                            PhotoVideoView.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            float f2;
            FrameLayout frameLayout3 = this.container;
            if (checked) {
                f2 = 0.85f;
            } else {
                f2 = 1.0f;
            }
            frameLayout3.setScaleX(f2);
            frameLayout = this.container;
            if (!checked) {
                f = 1.0f;
            }
            frameLayout.setScaleY(f);
        }

        public void setMessageObject(MessageObject messageObject) {
            this.currentMessageObject = messageObject;
            this.imageView.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(messageObject), false);
            if (messageObject.isVideo()) {
                this.videoInfoContainer.setVisibility(0);
                int duration = messageObject.getDuration();
                int seconds = duration - ((duration / 60) * 60);
                this.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)}));
                Document document = messageObject.getDocument();
                PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                PhotoSize qualityThumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                if (thumb == qualityThumb) {
                    qualityThumb = null;
                }
                if (thumb != null) {
                    this.imageView.setImage(qualityThumb, "100_100", ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.photo_placeholder_in), null, thumb, "b", null, 0, messageObject);
                } else {
                    this.imageView.setImageResource(R.drawable.photo_placeholder_in);
                }
            } else if (!(messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.messageOwner.media.photo == null || messageObject.photoThumbs.isEmpty()) {
                this.videoInfoContainer.setVisibility(4);
                this.imageView.setImageResource(R.drawable.photo_placeholder_in);
            } else {
                this.videoInfoContainer.setVisibility(4);
                PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 320);
                PhotoSize currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 50);
                if (messageObject.mediaExists || DownloadController.getInstance(SharedPhotoVideoCell.this.currentAccount).canDownloadMedia(messageObject)) {
                    if (currentPhotoObject == currentPhotoObjectThumb) {
                        currentPhotoObjectThumb = null;
                    }
                    this.imageView.getImageReceiver().setImage(currentPhotoObject, "100_100", currentPhotoObjectThumb, "b", currentPhotoObject.size, null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
                    return;
                }
                this.imageView.setImage(null, null, ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.photo_placeholder_in), null, currentPhotoObjectThumb, "b", null, 0, messageObject);
            }
        }

        public void clearAnimation() {
            super.clearAnimation();
            if (this.animator != null) {
                this.animator.cancel();
                this.animator = null;
            }
        }

        protected void onDraw(Canvas canvas) {
            if (this.checkBox.isChecked() || !this.imageView.getImageReceiver().hasBitmapImage() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f || PhotoViewer.isShowingImage(this.currentMessageObject)) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), SharedPhotoVideoCell.this.backgroundPaint);
            }
        }
    }

    public interface SharedPhotoVideoCellDelegate {
        void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);

        boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);
    }

    public SharedPhotoVideoCell(Context context) {
        super(context);
        this.backgroundPaint.setColor(Theme.getColor("sharedMedia_photoPlaceholder"));
        this.messageObjects = new MessageObject[6];
        this.photoVideoViews = new PhotoVideoView[6];
        this.indeces = new int[6];
        for (int a = 0; a < 6; a++) {
            this.photoVideoViews[a] = new PhotoVideoView(context);
            addView(this.photoVideoViews[a]);
            this.photoVideoViews[a].setVisibility(4);
            this.photoVideoViews[a].setTag(Integer.valueOf(a));
            this.photoVideoViews[a].setOnClickListener(new SharedPhotoVideoCell$$Lambda$0(this));
            this.photoVideoViews[a].setOnLongClickListener(new SharedPhotoVideoCell$$Lambda$1(this));
        }
    }

    final /* synthetic */ void lambda$new$0$SharedPhotoVideoCell(View v) {
        if (this.delegate != null) {
            int a1 = ((Integer) v.getTag()).intValue();
            this.delegate.didClickItem(this, this.indeces[a1], this.messageObjects[a1], a1);
        }
    }

    final /* synthetic */ boolean lambda$new$1$SharedPhotoVideoCell(View v) {
        if (this.delegate == null) {
            return false;
        }
        int a12 = ((Integer) v.getTag()).intValue();
        return this.delegate.didLongClickItem(this, this.indeces[a12], this.messageObjects[a12], a12);
    }

    public void updateCheckboxColor() {
        for (int a = 0; a < 6; a++) {
            this.photoVideoViews[a].checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
        }
    }

    public void setDelegate(SharedPhotoVideoCellDelegate delegate) {
        this.delegate = delegate;
    }

    public void setItemsCount(int count) {
        int a = 0;
        while (a < this.photoVideoViews.length) {
            this.photoVideoViews[a].clearAnimation();
            this.photoVideoViews[a].setVisibility(a < count ? 0 : 4);
            a++;
        }
        this.itemsCount = count;
    }

    public BackupImageView getImageView(int a) {
        if (a >= this.itemsCount) {
            return null;
        }
        return this.photoVideoViews[a].imageView;
    }

    public MessageObject getMessageObject(int a) {
        if (a >= this.itemsCount) {
            return null;
        }
        return this.messageObjects[a];
    }

    public void setIsFirst(boolean first) {
        this.isFirst = first;
    }

    public void setChecked(int a, boolean checked, boolean animated) {
        this.photoVideoViews[a].setChecked(checked, animated);
    }

    public void setItem(int a, int index, MessageObject messageObject) {
        this.messageObjects[a] = messageObject;
        this.indeces[a] = index;
        if (messageObject != null) {
            this.photoVideoViews[a].setVisibility(0);
            this.photoVideoViews[a].setMessageObject(messageObject);
            return;
        }
        this.photoVideoViews[a].clearAnimation();
        this.photoVideoViews[a].setVisibility(4);
        this.messageObjects[a] = null;
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int itemWidth;
        int i = 0;
        if (AndroidUtilities.isTablet()) {
            itemWidth = (AndroidUtilities.dp(490.0f) - ((this.itemsCount - 1) * AndroidUtilities.dp(2.0f))) / this.itemsCount;
        } else {
            itemWidth = (AndroidUtilities.displaySize.x - ((this.itemsCount - 1) * AndroidUtilities.dp(2.0f))) / this.itemsCount;
        }
        this.ignoreLayout = true;
        for (int a = 0; a < this.itemsCount; a++) {
            LayoutParams layoutParams = (LayoutParams) this.photoVideoViews[a].getLayoutParams();
            layoutParams.topMargin = this.isFirst ? 0 : AndroidUtilities.dp(2.0f);
            layoutParams.leftMargin = (AndroidUtilities.dp(2.0f) + itemWidth) * a;
            if (a != this.itemsCount - 1) {
                layoutParams.width = itemWidth;
            } else if (AndroidUtilities.isTablet()) {
                layoutParams.width = AndroidUtilities.dp(490.0f) - ((this.itemsCount - 1) * (AndroidUtilities.dp(2.0f) + itemWidth));
            } else {
                layoutParams.width = AndroidUtilities.displaySize.x - ((this.itemsCount - 1) * (AndroidUtilities.dp(2.0f) + itemWidth));
            }
            layoutParams.height = itemWidth;
            layoutParams.gravity = 51;
            this.photoVideoViews[a].setLayoutParams(layoutParams);
        }
        this.ignoreLayout = false;
        if (!this.isFirst) {
            i = AndroidUtilities.dp(2.0f);
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(i + itemWidth, NUM));
    }
}
