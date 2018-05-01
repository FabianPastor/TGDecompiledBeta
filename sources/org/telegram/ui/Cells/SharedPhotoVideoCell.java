package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoViewer;

public class SharedPhotoVideoCell extends FrameLayout {
    private SharedPhotoVideoCellDelegate delegate;
    private int[] indeces = new int[6];
    private boolean isFirst;
    private int itemsCount;
    private MessageObject[] messageObjects = new MessageObject[6];
    private PhotoVideoView[] photoVideoViews = new PhotoVideoView[6];

    /* renamed from: org.telegram.ui.Cells.SharedPhotoVideoCell$1 */
    class C08941 implements OnClickListener {
        C08941() {
        }

        public void onClick(View view) {
            if (SharedPhotoVideoCell.this.delegate != null) {
                view = ((Integer) view.getTag()).intValue();
                SharedPhotoVideoCell.this.delegate.didClickItem(SharedPhotoVideoCell.this, SharedPhotoVideoCell.this.indeces[view], SharedPhotoVideoCell.this.messageObjects[view], view);
            }
        }
    }

    /* renamed from: org.telegram.ui.Cells.SharedPhotoVideoCell$2 */
    class C08952 implements OnLongClickListener {
        C08952() {
        }

        public boolean onLongClick(View view) {
            if (SharedPhotoVideoCell.this.delegate == null) {
                return null;
            }
            view = ((Integer) view.getTag()).intValue();
            return SharedPhotoVideoCell.this.delegate.didLongClickItem(SharedPhotoVideoCell.this, SharedPhotoVideoCell.this.indeces[view], SharedPhotoVideoCell.this.messageObjects[view], view);
        }
    }

    private class PhotoVideoView extends FrameLayout {
        private AnimatorSet animator;
        private CheckBox checkBox;
        private FrameLayout container;
        private BackupImageView imageView;
        private View selector;
        private FrameLayout videoInfoContainer;
        private TextView videoTextView;

        public PhotoVideoView(Context context) {
            super(context);
            this.container = new FrameLayout(context);
            addView(this.container, LayoutHelper.createFrame(-1, -1.0f));
            this.imageView = new BackupImageView(context);
            this.imageView.getImageReceiver().setNeedsQualityThumb(true);
            this.imageView.getImageReceiver().setShouldGenerateQualityThumb(true);
            this.container.addView(this.imageView, LayoutHelper.createFrame(-1, -1.0f));
            this.videoInfoContainer = new FrameLayout(context);
            this.videoInfoContainer.setBackgroundResource(C0446R.drawable.phototime);
            this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
            this.container.addView(this.videoInfoContainer, LayoutHelper.createFrame(-1, 16, 83));
            SharedPhotoVideoCell imageView = new ImageView(context);
            imageView.setImageResource(C0446R.drawable.ic_video);
            this.videoInfoContainer.addView(imageView, LayoutHelper.createFrame(-2, -2, 19));
            this.videoTextView = new TextView(context);
            this.videoTextView.setTextColor(-1);
            this.videoTextView.setTextSize(1, 12.0f);
            this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, -0.7f, 0.0f, 0.0f));
            this.selector = new View(context);
            this.selector.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
            this.checkBox = new CheckBox(context, C0446R.drawable.round_check2);
            this.checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 2.0f, 2.0f, 0.0f));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
            }
            return super.onTouchEvent(motionEvent);
        }

        public void setChecked(final boolean z, boolean z2) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
            if (this.animator != null) {
                this.animator.cancel();
                this.animator = null;
            }
            int i = -657931;
            float f = 1.0f;
            if (z2) {
                if (z) {
                    setBackgroundColor(-657931);
                }
                this.animator = new AnimatorSet();
                z2 = this.animator;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout = this.container;
                String str = "scaleX";
                float[] fArr = new float[1];
                fArr[0] = z ? 0.85f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
                frameLayout = this.container;
                str = "scaleY";
                fArr = new float[1];
                if (z) {
                    f = 0.85f;
                }
                fArr[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
                z2.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (PhotoVideoView.this.animator != null && PhotoVideoView.this.animator.equals(animator) != null) {
                            PhotoVideoView.this.animator = null;
                            if (z == null) {
                                PhotoVideoView.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (PhotoVideoView.this.animator != null && PhotoVideoView.this.animator.equals(animator) != null) {
                            PhotoVideoView.this.animator = null;
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
            this.container.setScaleX(z ? 0.85f : 1.0f);
            z2 = this.container;
            if (z) {
                f = 0.85f;
            }
            z2.setScaleY(f);
        }

        public void clearAnimation() {
            super.clearAnimation();
            if (this.animator != null) {
                this.animator.cancel();
                this.animator = null;
            }
        }
    }

    public interface SharedPhotoVideoCellDelegate {
        void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);

        boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);
    }

    public SharedPhotoVideoCell(Context context) {
        super(context);
        for (int i = 0; i < 6; i++) {
            this.photoVideoViews[i] = new PhotoVideoView(context);
            addView(this.photoVideoViews[i]);
            this.photoVideoViews[i].setVisibility(4);
            this.photoVideoViews[i].setTag(Integer.valueOf(i));
            this.photoVideoViews[i].setOnClickListener(new C08941());
            this.photoVideoViews[i].setOnLongClickListener(new C08952());
        }
    }

    public void updateCheckboxColor() {
        for (int i = 0; i < 6; i++) {
            this.photoVideoViews[i].checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
        }
    }

    public void setDelegate(SharedPhotoVideoCellDelegate sharedPhotoVideoCellDelegate) {
        this.delegate = sharedPhotoVideoCellDelegate;
    }

    public void setItemsCount(int i) {
        int i2 = 0;
        while (i2 < this.photoVideoViews.length) {
            this.photoVideoViews[i2].clearAnimation();
            this.photoVideoViews[i2].setVisibility(i2 < i ? 0 : 4);
            i2++;
        }
        this.itemsCount = i;
    }

    public BackupImageView getImageView(int i) {
        if (i >= this.itemsCount) {
            return 0;
        }
        return this.photoVideoViews[i].imageView;
    }

    public MessageObject getMessageObject(int i) {
        if (i >= this.itemsCount) {
            return 0;
        }
        return this.messageObjects[i];
    }

    public void setIsFirst(boolean z) {
        this.isFirst = z;
    }

    public void setChecked(int i, boolean z, boolean z2) {
        this.photoVideoViews[i].setChecked(z, z2);
    }

    public void setItem(int i, int i2, MessageObject messageObject) {
        MessageObject messageObject2 = messageObject;
        this.messageObjects[i] = messageObject2;
        this.indeces[i] = i2;
        if (messageObject2 != null) {
            r0.photoVideoViews[i].setVisibility(0);
            PhotoVideoView photoVideoView = r0.photoVideoViews[i];
            photoVideoView.imageView.getImageReceiver().setParentMessageObject(messageObject2);
            photoVideoView.imageView.getImageReceiver().setVisible(PhotoViewer.isShowingImage(messageObject) ^ true, false);
            if (messageObject.isVideo()) {
                int i3;
                photoVideoView.videoInfoContainer.setVisibility(0);
                for (i3 = 0; i3 < messageObject.getDocument().attributes.size(); i3++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) messageObject.getDocument().attributes.get(i3);
                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                        i3 = documentAttribute.duration;
                        break;
                    }
                }
                i3 = 0;
                i3 -= (i3 / 60) * 60;
                photoVideoView.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(r4), Integer.valueOf(i3)}));
                if (messageObject.getDocument().thumb != null) {
                    photoVideoView.imageView.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(C0446R.drawable.photo_placeholder_in), null, messageObject.getDocument().thumb.location, "b", null, 0);
                    return;
                }
                photoVideoView.imageView.setImageResource(C0446R.drawable.photo_placeholder_in);
                return;
            } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject2.messageOwner.media.photo == null || messageObject2.photoThumbs.isEmpty()) {
                photoVideoView.videoInfoContainer.setVisibility(4);
                photoVideoView.imageView.setImageResource(C0446R.drawable.photo_placeholder_in);
                return;
            } else {
                photoVideoView.videoInfoContainer.setVisibility(4);
                photoVideoView.imageView.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(C0446R.drawable.photo_placeholder_in), null, FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 80).location, "b", null, 0);
                return;
            }
        }
        r0.photoVideoViews[i].clearAnimation();
        r0.photoVideoViews[i].setVisibility(4);
        r0.messageObjects[i] = null;
    }

    protected void onMeasure(int i, int i2) {
        if (AndroidUtilities.isTablet() != 0) {
            i2 = (AndroidUtilities.dp(NUM) - ((this.itemsCount + 1) * AndroidUtilities.dp(4.0f))) / this.itemsCount;
        } else {
            i2 = (AndroidUtilities.displaySize.x - ((this.itemsCount + 1) * AndroidUtilities.dp(4.0f))) / this.itemsCount;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < this.itemsCount; i4++) {
            LayoutParams layoutParams = (LayoutParams) this.photoVideoViews[i4].getLayoutParams();
            layoutParams.topMargin = this.isFirst ? 0 : AndroidUtilities.dp(4.0f);
            layoutParams.leftMargin = ((AndroidUtilities.dp(4.0f) + i2) * i4) + AndroidUtilities.dp(4.0f);
            layoutParams.width = i2;
            layoutParams.height = i2;
            layoutParams.gravity = 51;
            this.photoVideoViews[i4].setLayoutParams(layoutParams);
        }
        if (!this.isFirst) {
            i3 = AndroidUtilities.dp(4.0f);
        }
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(i3 + i2, NUM));
    }
}
