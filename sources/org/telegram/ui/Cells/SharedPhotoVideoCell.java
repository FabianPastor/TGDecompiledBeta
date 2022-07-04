package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.PhotoViewer;

public class SharedPhotoVideoCell extends FrameLayout {
    public static final int VIEW_TYPE_DEFAULT = 0;
    public static final int VIEW_TYPE_GLOBAL_SEARCH = 1;
    /* access modifiers changed from: private */
    public Paint backgroundPaint;
    /* access modifiers changed from: private */
    public int currentAccount;
    private SharedPhotoVideoCellDelegate delegate;
    private boolean ignoreLayout;
    private int[] indeces;
    private boolean isFirst;
    private int itemsCount;
    private MessageObject[] messageObjects;
    private PhotoVideoView[] photoVideoViews;
    private int type;

    public interface SharedPhotoVideoCellDelegate {
        void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);

        boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);
    }

    public SharedPhotoVideoCellDelegate getDelegate() {
        return this.delegate;
    }

    public class PhotoVideoView extends FrameLayout {
        /* access modifiers changed from: private */
        public AnimatorSet animator;
        /* access modifiers changed from: private */
        public CheckBox2 checkBox;
        private FrameLayout container;
        private MessageObject currentMessageObject;
        /* access modifiers changed from: private */
        public BackupImageView imageView;
        private View selector;
        final /* synthetic */ SharedPhotoVideoCell this$0;
        private FrameLayout videoInfoContainer;
        private TextView videoTextView;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhotoVideoView(org.telegram.ui.Cells.SharedPhotoVideoCell r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                r2 = r19
                r0.this$0 = r1
                r0.<init>(r2)
                r3 = 0
                r0.setWillNotDraw(r3)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                r0.container = r4
                r5 = -1
                r6 = -1082130432(0xffffffffbvar_, float:-1.0)
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6)
                r0.addView(r4, r7)
                org.telegram.ui.Components.BackupImageView r4 = new org.telegram.ui.Components.BackupImageView
                r4.<init>(r2)
                r0.imageView = r4
                org.telegram.messenger.ImageReceiver r4 = r4.getImageReceiver()
                r7 = 1
                r4.setNeedsQualityThumb(r7)
                org.telegram.ui.Components.BackupImageView r4 = r0.imageView
                org.telegram.messenger.ImageReceiver r4 = r4.getImageReceiver()
                r4.setShouldGenerateQualityThumb(r7)
                android.widget.FrameLayout r4 = r0.container
                org.telegram.ui.Components.BackupImageView r8 = r0.imageView
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6)
                r4.addView(r8, r9)
                org.telegram.ui.Cells.SharedPhotoVideoCell$PhotoVideoView$1 r4 = new org.telegram.ui.Cells.SharedPhotoVideoCell$PhotoVideoView$1
                r4.<init>(r2, r1)
                r0.videoInfoContainer = r4
                r4.setWillNotDraw(r3)
                android.widget.FrameLayout r4 = r0.videoInfoContainer
                r8 = 1084227584(0x40a00000, float:5.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r4.setPadding(r9, r3, r8, r3)
                android.widget.FrameLayout r4 = r0.container
                android.widget.FrameLayout r8 = r0.videoInfoContainer
                r9 = -2
                r10 = 1099431936(0x41880000, float:17.0)
                r11 = 83
                r12 = 1082130432(0x40800000, float:4.0)
                r13 = 0
                r14 = 0
                r15 = 1082130432(0x40800000, float:4.0)
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r4.addView(r8, r9)
                android.widget.ImageView r4 = new android.widget.ImageView
                r4.<init>(r2)
                r8 = 2131166064(0x7var_, float:1.7946363E38)
                r4.setImageResource(r8)
                android.widget.FrameLayout r8 = r0.videoInfoContainer
                r9 = -2
                r10 = 19
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r10)
                r8.addView(r4, r9)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r2)
                r0.videoTextView = r8
                r8.setTextColor(r5)
                android.widget.TextView r8 = r0.videoTextView
                java.lang.String r9 = "fonts/rmedium.ttf"
                android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
                r8.setTypeface(r9)
                android.widget.TextView r8 = r0.videoTextView
                r9 = 1094713344(0x41400000, float:12.0)
                r8.setTextSize(r7, r9)
                android.widget.TextView r8 = r0.videoTextView
                r9 = 2
                r8.setImportantForAccessibility(r9)
                android.widget.FrameLayout r8 = r0.videoInfoContainer
                android.widget.TextView r9 = r0.videoTextView
                r10 = -2
                r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r12 = 19
                r13 = 1095761920(0x41500000, float:13.0)
                r14 = -1087163597(0xffffffffbvar_, float:-0.7)
                r15 = 0
                r16 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r8.addView(r9, r10)
                android.view.View r8 = new android.view.View
                r8.<init>(r2)
                r0.selector = r8
                android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r3)
                r8.setBackgroundDrawable(r9)
                android.view.View r8 = r0.selector
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6)
                r0.addView(r8, r5)
                org.telegram.ui.Components.CheckBox2 r5 = new org.telegram.ui.Components.CheckBox2
                r6 = 21
                r5.<init>(r2, r6)
                r0.checkBox = r5
                r6 = 4
                r5.setVisibility(r6)
                org.telegram.ui.Components.CheckBox2 r5 = r0.checkBox
                r6 = 0
                java.lang.String r8 = "sharedMedia_photoPlaceholder"
                java.lang.String r9 = "checkboxCheck"
                r5.setColor(r6, r8, r9)
                org.telegram.ui.Components.CheckBox2 r5 = r0.checkBox
                r5.setDrawUnchecked(r3)
                org.telegram.ui.Components.CheckBox2 r3 = r0.checkBox
                r3.setDrawBackgroundAsArc(r7)
                org.telegram.ui.Components.CheckBox2 r3 = r0.checkBox
                r5 = 24
                r6 = 1103101952(0x41CLASSNAME, float:24.0)
                r7 = 53
                r8 = 0
                r9 = 1065353216(0x3var_, float:1.0)
                r10 = 1065353216(0x3var_, float:1.0)
                r11 = 0
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
                r0.addView(r3, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedPhotoVideoCell.PhotoVideoView.<init>(org.telegram.ui.Cells.SharedPhotoVideoCell, android.content.Context):void");
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(event.getX(), event.getY());
            }
            return super.onTouchEvent(event);
        }

        public void setChecked(boolean checked, boolean animated) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animator = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout = this.container;
                Property property = View.SCALE_X;
                float[] fArr = new float[1];
                fArr[0] = checked ? 0.81f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
                FrameLayout frameLayout2 = this.container;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[1];
                if (checked) {
                    f = 0.81f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (PhotoVideoView.this.animator != null && PhotoVideoView.this.animator.equals(animation)) {
                            AnimatorSet unused = PhotoVideoView.this.animator = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (PhotoVideoView.this.animator != null && PhotoVideoView.this.animator.equals(animation)) {
                            AnimatorSet unused = PhotoVideoView.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            this.container.setScaleX(checked ? 0.85f : 1.0f);
            FrameLayout frameLayout3 = this.container;
            if (checked) {
                f = 0.85f;
            }
            frameLayout3.setScaleY(f);
        }

        public void setMessageObject(MessageObject messageObject) {
            TLRPC.PhotoSize currentPhotoObjectThumb;
            TLRPC.PhotoSize qualityThumb;
            MessageObject messageObject2 = messageObject;
            this.currentMessageObject = messageObject2;
            this.imageView.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(messageObject), false);
            if (!TextUtils.isEmpty(MessagesController.getRestrictionReason(messageObject2.messageOwner.restriction_reason))) {
                this.videoInfoContainer.setVisibility(4);
                this.imageView.setImageResource(NUM);
            } else if (messageObject.isVideo()) {
                this.videoInfoContainer.setVisibility(0);
                this.videoTextView.setText(AndroidUtilities.formatShortDuration(messageObject.getDuration()));
                TLRPC.Document document = messageObject.getDocument();
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                TLRPC.PhotoSize qualityThumb2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                if (thumb == qualityThumb2) {
                    qualityThumb = null;
                } else {
                    qualityThumb = qualityThumb2;
                }
                if (thumb == null) {
                    this.imageView.setImageResource(NUM);
                } else if (messageObject2.strippedThumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(qualityThumb, document), "100_100", (String) null, (Drawable) messageObject2.strippedThumb, (Object) messageObject);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(qualityThumb, document), "100_100", ImageLocation.getForDocument(thumb, document), "b", ApplicationLoader.applicationContext.getResources().getDrawable(NUM), (Bitmap) null, (String) null, 0, messageObject);
                }
            } else if (!(messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) || messageObject2.messageOwner.media.photo == null || messageObject2.photoThumbs.isEmpty()) {
                this.videoInfoContainer.setVisibility(4);
                this.imageView.setImageResource(NUM);
            } else {
                this.videoInfoContainer.setVisibility(4);
                TLRPC.PhotoSize currentPhotoObjectThumb2 = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 50);
                TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 320, false, currentPhotoObjectThumb2, false);
                if (messageObject2.mediaExists || DownloadController.getInstance(this.this$0.currentAccount).canDownloadMedia(messageObject2)) {
                    if (currentPhotoObject == currentPhotoObjectThumb2) {
                        currentPhotoObjectThumb = null;
                    } else {
                        currentPhotoObjectThumb = currentPhotoObjectThumb2;
                    }
                    long j = 0;
                    if (messageObject2.strippedThumb != null) {
                        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
                        ImageLocation forObject = ImageLocation.getForObject(currentPhotoObject, messageObject2.photoThumbsObject);
                        BitmapDrawable bitmapDrawable = messageObject2.strippedThumb;
                        if (currentPhotoObject != null) {
                            j = (long) currentPhotoObject.size;
                        }
                        imageReceiver.setImage(forObject, "100_100", (ImageLocation) null, (String) null, bitmapDrawable, j, (String) null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                        return;
                    }
                    ImageReceiver imageReceiver2 = this.imageView.getImageReceiver();
                    ImageLocation forObject2 = ImageLocation.getForObject(currentPhotoObject, messageObject2.photoThumbsObject);
                    ImageLocation forObject3 = ImageLocation.getForObject(currentPhotoObjectThumb, messageObject2.photoThumbsObject);
                    if (currentPhotoObject != null) {
                        j = (long) currentPhotoObject.size;
                    }
                    imageReceiver2.setImage(forObject2, "100_100", forObject3, "b", j, (String) null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                } else if (messageObject2.strippedThumb != null) {
                    this.imageView.setImage((ImageLocation) null, (String) null, (ImageLocation) null, (String) null, messageObject2.strippedThumb, (Bitmap) null, (String) null, 0, messageObject);
                } else {
                    this.imageView.setImage((ImageLocation) null, (String) null, ImageLocation.getForObject(currentPhotoObjectThumb2, messageObject2.photoThumbsObject), "b", ApplicationLoader.applicationContext.getResources().getDrawable(NUM), (Bitmap) null, (String) null, 0, messageObject);
                }
            }
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
            if (this.checkBox.isChecked() || !this.imageView.getImageReceiver().hasBitmapImage() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f || PhotoViewer.isShowingImage(this.currentMessageObject)) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.this$0.backgroundPaint);
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            if (this.currentMessageObject.isVideo()) {
                info.setText(LocaleController.getString("AttachVideo", NUM) + ", " + LocaleController.formatDuration(this.currentMessageObject.getDuration()));
            } else {
                info.setText(LocaleController.getString("AttachPhoto", NUM));
            }
            if (this.checkBox.isChecked()) {
                info.setCheckable(true);
                info.setChecked(true);
            }
        }
    }

    public SharedPhotoVideoCell(Context context) {
        this(context, 0);
    }

    public SharedPhotoVideoCell(Context context, int type2) {
        super(context);
        this.backgroundPaint = new Paint();
        this.currentAccount = UserConfig.selectedAccount;
        this.type = type2;
        this.backgroundPaint.setColor(Theme.getColor("sharedMedia_photoPlaceholder"));
        this.messageObjects = new MessageObject[6];
        this.photoVideoViews = new PhotoVideoView[6];
        this.indeces = new int[6];
        for (int a = 0; a < 6; a++) {
            this.photoVideoViews[a] = new PhotoVideoView(this, context);
            addView(this.photoVideoViews[a]);
            this.photoVideoViews[a].setVisibility(4);
            this.photoVideoViews[a].setTag(Integer.valueOf(a));
            this.photoVideoViews[a].setOnClickListener(new SharedPhotoVideoCell$$ExternalSyntheticLambda0(this));
            this.photoVideoViews[a].setOnLongClickListener(new SharedPhotoVideoCell$$ExternalSyntheticLambda1(this));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-SharedPhotoVideoCell  reason: not valid java name */
    public /* synthetic */ void m2819lambda$new$0$orgtelegramuiCellsSharedPhotoVideoCell(View v) {
        if (this.delegate != null) {
            int a1 = ((Integer) v.getTag()).intValue();
            this.delegate.didClickItem(this, this.indeces[a1], this.messageObjects[a1], a1);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-SharedPhotoVideoCell  reason: not valid java name */
    public /* synthetic */ boolean m2820lambda$new$1$orgtelegramuiCellsSharedPhotoVideoCell(View v) {
        if (this.delegate == null) {
            return false;
        }
        int a12 = ((Integer) v.getTag()).intValue();
        return this.delegate.didLongClickItem(this, this.indeces[a12], this.messageObjects[a12], a12);
    }

    public void updateCheckboxColor() {
        for (int a = 0; a < 6; a++) {
            this.photoVideoViews[a].checkBox.invalidate();
        }
    }

    public void invalidate() {
        for (int a = 0; a < 6; a++) {
            this.photoVideoViews[a].invalidate();
        }
        super.invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setDelegate(SharedPhotoVideoCellDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setItemsCount(int count) {
        int a = 0;
        while (true) {
            PhotoVideoView[] photoVideoViewArr = this.photoVideoViews;
            if (a < photoVideoViewArr.length) {
                photoVideoViewArr[a].clearAnimation();
                this.photoVideoViews[a].setVisibility(a < count ? 0 : 4);
                a++;
            } else {
                this.itemsCount = count;
                return;
            }
        }
    }

    public BackupImageView getImageView(int a) {
        if (a >= this.itemsCount) {
            return null;
        }
        return this.photoVideoViews[a].imageView;
    }

    public PhotoVideoView getView(int a) {
        if (a >= this.itemsCount) {
            return null;
        }
        return this.photoVideoViews[a];
    }

    public MessageObject getMessageObject(int a) {
        if (a >= this.itemsCount) {
            return null;
        }
        return this.messageObjects[a];
    }

    public int getIndeces(int a) {
        if (a >= this.itemsCount) {
            return -1;
        }
        return this.indeces[a];
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

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int itemWidth;
        int i;
        if (this.type == 1) {
            itemWidth = (View.MeasureSpec.getSize(widthMeasureSpec) - ((this.itemsCount - 1) * AndroidUtilities.dp(2.0f))) / this.itemsCount;
        } else {
            itemWidth = getItemSize(this.itemsCount);
        }
        this.ignoreLayout = true;
        int a = 0;
        while (true) {
            i = 0;
            if (a >= this.itemsCount) {
                break;
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.photoVideoViews[a].getLayoutParams();
            if (!this.isFirst) {
                i = AndroidUtilities.dp(2.0f);
            }
            layoutParams.topMargin = i;
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
            a++;
        }
        this.ignoreLayout = false;
        if (!this.isFirst) {
            i = AndroidUtilities.dp(2.0f);
        }
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(i + itemWidth, NUM));
    }

    public static int getItemSize(int itemsCount2) {
        if (AndroidUtilities.isTablet()) {
            return (AndroidUtilities.dp(490.0f) - ((itemsCount2 - 1) * AndroidUtilities.dp(2.0f))) / itemsCount2;
        }
        return (AndroidUtilities.displaySize.x - ((itemsCount2 - 1) * AndroidUtilities.dp(2.0f))) / itemsCount2;
    }
}
