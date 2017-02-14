package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_inputPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhotoCropView;
import org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PhotoPaintView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;

public class PhotoViewer implements NotificationCenterDelegate, OnGestureListener, OnDoubleTapListener {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile PhotoViewer Instance = null;
    private static DecelerateInterpolator decelerateInterpolator = null;
    private static final int gallery_menu_delete = 6;
    private static final int gallery_menu_masks = 13;
    private static final int gallery_menu_mute = 12;
    private static final int gallery_menu_openin = 11;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_send = 3;
    private static final int gallery_menu_share = 10;
    private static final int gallery_menu_showall = 2;
    private static Drawable[] progressDrawables;
    private static Paint progressPaint;
    private ActionBar actionBar;
    private Context actvityContext;
    private boolean allowMentions;
    private boolean allowShare;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private float[][] animationValues = ((float[][]) Array.newInstance(Float.TYPE, new int[]{2, 8}));
    private boolean applying;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private boolean attachedToWindow;
    private ArrayList<Photo> avatarsArr = new ArrayList();
    private int avatarsDialogId;
    private BackgroundDrawable backgroundDrawable = new BackgroundDrawable(-16777216);
    private Paint blackPaint = new Paint();
    private FrameLayout bottomLayout;
    private boolean bottomTouchEnabled = true;
    private boolean canDragDown = true;
    private boolean canShowBottom = true;
    private boolean canZoom = true;
    private PhotoViewerCaptionEnterView captionEditText;
    private TextView captionTextView;
    private TextView captionTextViewNew;
    private TextView captionTextViewOld;
    private ImageReceiver centerImage = new ImageReceiver();
    private AnimatorSet changeModeAnimation;
    private boolean changingPage;
    private CheckBox checkImageView;
    private int classGuid;
    private FrameLayoutDrawer containerView;
    private ImageView cropItem;
    private AnimatorSet currentActionBarAnimation;
    private AnimatedFileDrawable currentAnimation;
    private BotInlineResult currentBotInlineResult;
    private long currentDialogId;
    private int currentEditMode;
    private FileLocation currentFileLocation;
    private String[] currentFileNames = new String[3];
    private int currentIndex;
    private MessageObject currentMessageObject;
    private String currentPathObject;
    private PlaceProviderObject currentPlaceObject;
    private Bitmap currentThumb;
    private FileLocation currentUserAvatarLocation = null;
    private TextView dateTextView;
    private boolean disableShowCheck;
    private boolean discardTap;
    private boolean doneButtonPressed;
    private boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private float dragY;
    private boolean draggingDown;
    private PickerBottomLayoutViewer editorDoneLayout;
    private boolean[] endReached = new boolean[]{false, true};
    private GestureDetector gestureDetector;
    private PlaceProviderObject hideAfterAnimation;
    private boolean ignoreDidSetImage;
    private AnimatorSet imageMoveAnimation;
    private ArrayList<MessageObject> imagesArr = new ArrayList();
    private ArrayList<Object> imagesArrLocals = new ArrayList();
    private ArrayList<FileLocation> imagesArrLocations = new ArrayList();
    private ArrayList<Integer> imagesArrLocationsSizes = new ArrayList();
    private ArrayList<MessageObject> imagesArrTemp = new ArrayList();
    private HashMap<Integer, MessageObject>[] imagesByIds = new HashMap[]{new HashMap(), new HashMap()};
    private HashMap<Integer, MessageObject>[] imagesByIdsTemp = new HashMap[]{new HashMap(), new HashMap()};
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private boolean invalidCoords;
    private boolean isActionBarVisible = true;
    private boolean isFirstLoading;
    private boolean isPlaying;
    private boolean isVisible;
    private Object lastInsets;
    private String lastTitle;
    private ImageReceiver leftImage = new ImageReceiver();
    private boolean loadingMoreImages;
    private ActionBarMenuItem masksItem;
    private float maxX;
    private float maxY;
    private LinearLayoutManager mentionLayoutManager;
    private AnimatorSet mentionListAnimation;
    private RecyclerListView mentionListView;
    private MentionsAdapter mentionsAdapter;
    private ActionBarMenuItem menuItem;
    private long mergeDialogId;
    private float minX;
    private float minY;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private ActionBarMenuItem muteItem;
    private boolean muteItemAvailable;
    private boolean muteVideo;
    private TextView nameTextView;
    private boolean needCaptionLayout;
    private boolean needSearchImageInArr;
    private boolean opennedFromMedia;
    private ImageView paintItem;
    private Activity parentActivity;
    private ChatAttachAlert parentAlert;
    private ChatActivity parentChatActivity;
    private PhotoCropView photoCropView;
    private PhotoFilterView photoFilterView;
    private PhotoPaintView photoPaintView;
    private PickerBottomLayoutViewer pickerView;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale = 1.0f;
    private float pinchStartX;
    private float pinchStartY;
    private PhotoViewerProvider placeProvider;
    private RadialProgressView[] radialProgressViews = new RadialProgressView[3];
    private TextView resetButton;
    private ImageReceiver rightImage = new ImageReceiver();
    private float scale = 1.0f;
    private Scroller scroller;
    private int sendPhotoType;
    private ImageView shareButton;
    private PlaceProviderObject showAfterAnimation;
    private int switchImageAfterAnimation;
    private boolean textureUploaded;
    private int totalImagesCount;
    private int totalImagesCountMerge;
    private long transitionAnimationStartTime;
    private float translationX;
    private float translationY;
    private ImageView tuneItem;
    private Runnable updateProgressRunnable = new Runnable() {
        public void run() {
            if (!(PhotoViewer.this.videoPlayer == null || PhotoViewer.this.videoPlayerSeekbar == null || PhotoViewer.this.videoPlayerSeekbar.isDragging())) {
                PhotoViewer.this.videoPlayerSeekbar.setProgress(((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration()));
                PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                PhotoViewer.this.updateVideoPlayerTime();
            }
            if (PhotoViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 100);
            }
        }
    };
    private VelocityTracker velocityTracker;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private ImageView videoPlayButton;
    private VideoPlayer videoPlayer;
    private FrameLayout videoPlayerControlFrameLayout;
    private SeekBar videoPlayerSeekbar;
    private TextView videoPlayerTime;
    private TextureView videoTextureView;
    private AlertDialog visibleDialog;
    private boolean wasLayout;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;
    private boolean zoomAnimation;
    private boolean zooming;

    private class BackgroundDrawable extends ColorDrawable {
        private boolean allowDrawContent;
        private Runnable drawRunnable;

        public BackgroundDrawable(int color) {
            super(color);
        }

        public void setAlpha(int alpha) {
            if (PhotoViewer.this.parentActivity instanceof LaunchActivity) {
                boolean z = (PhotoViewer.this.isVisible && alpha == 255) ? false : true;
                this.allowDrawContent = z;
                ((LaunchActivity) PhotoViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(this.allowDrawContent);
                if (PhotoViewer.this.parentAlert != null) {
                    if (!this.allowDrawContent) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (PhotoViewer.this.parentAlert != null) {
                                    PhotoViewer.this.parentAlert.setAllowDrawContent(BackgroundDrawable.this.allowDrawContent);
                                }
                            }
                        }, 50);
                    } else if (PhotoViewer.this.parentAlert != null) {
                        PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
                    }
                }
            }
            super.setAlpha(alpha);
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != 0 && this.drawRunnable != null) {
                this.drawRunnable.run();
                this.drawRunnable = null;
            }
        }
    }

    public interface PhotoViewerProvider {
        boolean allowCaption();

        boolean cancelButtonPressed();

        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i);

        int getSelectedCount();

        Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i);

        boolean isPhotoChecked(int i);

        boolean scaleToFill();

        void sendButtonPressed(int i);

        void setPhotoChecked(int i);

        void updatePhotoAtIndex(int i);

        void willHidePhotoViewer();

        void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i);
    }

    public static class PlaceProviderObject {
        public int clipBottomAddition;
        public int clipTopAddition;
        public int dialogId;
        public ImageReceiver imageReceiver;
        public int index;
        public View parentView;
        public int radius;
        public float scale = 1.0f;
        public int size;
        public Bitmap thumb;
        public int viewX;
        public int viewY;
    }

    private class RadialProgressView {
        private float alpha = 1.0f;
        private float animatedAlphaValue = 1.0f;
        private float animatedProgressValue = 0.0f;
        private float animationProgressStart = 0.0f;
        private int backgroundState = -1;
        private float currentProgress = 0.0f;
        private long currentProgressTime = 0;
        private long lastUpdateTime = 0;
        private View parent = null;
        private int previousBackgroundState = -2;
        private RectF progressRect = new RectF();
        private float radOffset = 0.0f;
        private float scale = 1.0f;
        private int size = AndroidUtilities.dp(64.0f);

        public RadialProgressView(Context context, View parentView) {
            if (PhotoViewer.decelerateInterpolator == null) {
                PhotoViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                PhotoViewer.progressPaint = new Paint(1);
                PhotoViewer.progressPaint.setStyle(Style.STROKE);
                PhotoViewer.progressPaint.setStrokeCap(Cap.ROUND);
                PhotoViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                PhotoViewer.progressPaint.setColor(-1);
            }
            this.parent = parentView;
        }

        private void updateAnimation() {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (this.animatedProgressValue != 1.0f) {
                this.radOffset += ((float) (360 * dt)) / 3000.0f;
                float progressDiff = this.currentProgress - this.animationProgressStart;
                if (progressDiff > 0.0f) {
                    this.currentProgressTime += dt;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = this.currentProgress;
                        this.animationProgressStart = this.currentProgress;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = this.animationProgressStart + (PhotoViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / BitmapDescriptorFactory.HUE_MAGENTA) * progressDiff);
                    }
                }
                this.parent.invalidate();
            }
            if (this.animatedProgressValue >= 1.0f && this.previousBackgroundState != -2) {
                this.animatedAlphaValue -= ((float) dt) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousBackgroundState = -2;
                }
                this.parent.invalidate();
            }
        }

        public void setProgress(float value, boolean animated) {
            if (animated) {
                this.animationProgressStart = this.animatedProgressValue;
            } else {
                this.animatedProgressValue = value;
                this.animationProgressStart = value;
            }
            this.currentProgress = value;
            this.currentProgressTime = 0;
        }

        public void setBackgroundState(int state, boolean animated) {
            this.lastUpdateTime = System.currentTimeMillis();
            if (!animated || this.backgroundState == state) {
                this.previousBackgroundState = -2;
            } else {
                this.previousBackgroundState = this.backgroundState;
                this.animatedAlphaValue = 1.0f;
            }
            this.backgroundState = state;
            this.parent.invalidate();
        }

        public void setAlpha(float value) {
            this.alpha = value;
        }

        public void setScale(float value) {
            this.scale = value;
        }

        public void onDraw(Canvas canvas) {
            Drawable drawable;
            int sizeScaled = (int) (((float) this.size) * this.scale);
            int x = (PhotoViewer.this.getContainerViewWidth() - sizeScaled) / 2;
            int y = (PhotoViewer.this.getContainerViewHeight() - sizeScaled) / 2;
            if (this.previousBackgroundState >= 0 && this.previousBackgroundState < 4) {
                drawable = PhotoViewer.progressDrawables[this.previousBackgroundState];
                if (drawable != null) {
                    drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState >= 0 && this.backgroundState < 4) {
                drawable = PhotoViewer.progressDrawables[this.backgroundState];
                if (drawable != null) {
                    if (this.previousBackgroundState != -2) {
                        drawable.setAlpha((int) (((1.0f - this.animatedAlphaValue) * 255.0f) * this.alpha));
                    } else {
                        drawable.setAlpha((int) (this.alpha * 255.0f));
                    }
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState == 0 || this.backgroundState == 1 || this.previousBackgroundState == 0 || this.previousBackgroundState == 1) {
                int diff = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    PhotoViewer.progressPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                } else {
                    PhotoViewer.progressPaint.setAlpha((int) (this.alpha * 255.0f));
                }
                this.progressRect.set((float) (x + diff), (float) (y + diff), (float) ((x + sizeScaled) - diff), (float) ((y + sizeScaled) - diff));
                canvas.drawArc(this.progressRect, this.radOffset - 0.049804688f, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, PhotoViewer.progressPaint);
                updateAnimation();
            }
        }
    }

    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            return null;
        }

        public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            return null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        }

        public void willHidePhotoViewer() {
        }

        public boolean isPhotoChecked(int index) {
            return false;
        }

        public void setPhotoChecked(int index) {
        }

        public boolean cancelButtonPressed() {
            return true;
        }

        public void sendButtonPressed(int index) {
        }

        public int getSelectedCount() {
            return 0;
        }

        public void updatePhotoAtIndex(int index) {
        }

        public boolean allowCaption() {
            return true;
        }

        public boolean scaleToFill() {
            return false;
        }
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        private Paint paint = new Paint();

        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
            this.paint.setColor(855638016);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            measureChildWithMargins(PhotoViewer.this.captionEditText, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int inputFieldHeight = PhotoViewer.this.captionEditText.getMeasuredHeight();
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (!(child.getVisibility() == 8 || child == PhotoViewer.this.captionEditText)) {
                    if (!PhotoViewer.this.captionEditText.isPopupView(child)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    } else if (!AndroidUtilities.isInMultiwindow) {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                    } else if (AndroidUtilities.isTablet()) {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), (heightSize - inputFieldHeight) - AndroidUtilities.statusBarHeight), NUM));
                    } else {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec((heightSize - inputFieldHeight) - AndroidUtilities.statusBarHeight, NUM));
                    }
                }
            }
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int count = getChildCount();
            int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    int childLeft;
                    int childTop;
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int verticalGravity = gravity & 112;
                    switch ((gravity & 7) & 7) {
                        case 1:
                            childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case 5:
                            childLeft = ((r - l) - width) - lp.rightMargin;
                            break;
                        default:
                            childLeft = lp.leftMargin;
                            break;
                    }
                    switch (verticalGravity) {
                        case 16:
                            childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                            break;
                        case 48:
                            childTop = lp.topMargin;
                            break;
                        case 80:
                            childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                            break;
                    }
                    if (child == PhotoViewer.this.mentionListView) {
                        childTop -= PhotoViewer.this.captionEditText.getMeasuredHeight();
                    } else if (PhotoViewer.this.captionEditText.isPopupView(child)) {
                        if (AndroidUtilities.isInMultiwindow) {
                            childTop = (PhotoViewer.this.captionEditText.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                        } else {
                            childTop = PhotoViewer.this.captionEditText.getBottom();
                        }
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
            }
            notifyHeightChanged();
        }

        protected void onDraw(Canvas canvas) {
            PhotoViewer.this.onDraw(canvas);
            if (VERSION.SDK_INT >= 21 && AndroidUtilities.statusBarHeight != 0) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.statusBarHeight, this.paint);
            }
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == PhotoViewer.this.mentionListView || child == PhotoViewer.this.captionEditText) {
                if (!PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.captionEditText.getEmojiPadding() == 0 && ((AndroidUtilities.usingHardwareInput && getTag() == null) || getKeyboardHeight() == 0)) {
                    return false;
                }
            } else if (child == PhotoViewer.this.pickerView || child == PhotoViewer.this.captionTextViewNew || child == PhotoViewer.this.captionTextViewOld || child == PhotoViewer.this.checkImageView) {
                int paddingBottom;
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) {
                    paddingBottom = 0;
                } else {
                    paddingBottom = PhotoViewer.this.captionEditText.getEmojiPadding();
                }
                if (PhotoViewer.this.captionEditText.isPopupShowing() || ((AndroidUtilities.usingHardwareInput && getTag() != null) || getKeyboardHeight() > 0 || paddingBottom != 0)) {
                    PhotoViewer.this.bottomTouchEnabled = false;
                    return false;
                }
                PhotoViewer.this.bottomTouchEnabled = true;
            }
            if (child == PhotoViewer.this.aspectRatioFrameLayout || !super.drawChild(canvas, child, drawingTime)) {
                return false;
            }
            return true;
        }
    }

    public static PhotoViewer getInstance() {
        PhotoViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        PhotoViewer localInstance2 = new PhotoViewer();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public PhotoViewer() {
        this.blackPaint.setColor(-16777216);
    }

    public void didReceivedNotification(int id, Object... args) {
        String location;
        int a;
        if (id == NotificationCenter.FileDidFailedLoad) {
            location = args[0];
            a = 0;
            while (a < 3) {
                if (this.currentFileNames[a] == null || !this.currentFileNames[a].equals(location)) {
                    a++;
                } else {
                    this.radialProgressViews[a].setProgress(1.0f, true);
                    checkProgress(a, true);
                    return;
                }
            }
        } else if (id == NotificationCenter.FileDidLoaded) {
            location = (String) args[0];
            a = 0;
            while (a < 3) {
                if (this.currentFileNames[a] == null || !this.currentFileNames[a].equals(location)) {
                    a++;
                } else {
                    this.radialProgressViews[a].setProgress(1.0f, true);
                    checkProgress(a, true);
                    if (VERSION.SDK_INT >= 16 && a == 0) {
                        if (this.currentMessageObject == null || !this.currentMessageObject.isVideo()) {
                            if (this.currentBotInlineResult == null) {
                                return;
                            }
                            if (!(this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                                return;
                            }
                        }
                        onActionClick(false);
                        return;
                    }
                    return;
                }
            }
        } else if (id == NotificationCenter.FileLoadProgressChanged) {
            location = (String) args[0];
            a = 0;
            while (a < 3) {
                if (this.currentFileNames[a] != null && this.currentFileNames[a].equals(location)) {
                    this.radialProgressViews[a].setProgress(args[1].floatValue(), true);
                }
                a++;
            }
        } else if (id == NotificationCenter.dialogPhotosLoaded) {
            guid = ((Integer) args[4]).intValue();
            if (this.avatarsDialogId == ((Integer) args[0]).intValue() && this.classGuid == guid) {
                boolean fromCache = ((Boolean) args[3]).booleanValue();
                int setToImage = -1;
                ArrayList<Photo> photos = args[5];
                if (!photos.isEmpty()) {
                    this.imagesArrLocations.clear();
                    this.imagesArrLocationsSizes.clear();
                    this.avatarsArr.clear();
                    for (a = 0; a < photos.size(); a++) {
                        Photo photo = (Photo) photos.get(a);
                        if (!(photo == null || (photo instanceof TL_photoEmpty) || photo.sizes == null)) {
                            PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 640);
                            if (sizeFull != null) {
                                if (setToImage == -1 && this.currentFileLocation != null) {
                                    for (int b = 0; b < photo.sizes.size(); b++) {
                                        PhotoSize size = (PhotoSize) photo.sizes.get(b);
                                        if (size.location.local_id == this.currentFileLocation.local_id && size.location.volume_id == this.currentFileLocation.volume_id) {
                                            setToImage = this.imagesArrLocations.size();
                                            break;
                                        }
                                    }
                                }
                                this.imagesArrLocations.add(sizeFull.location);
                                this.imagesArrLocationsSizes.add(Integer.valueOf(sizeFull.size));
                                this.avatarsArr.add(photo);
                            }
                        }
                    }
                    if (this.avatarsArr.isEmpty()) {
                        this.menuItem.hideSubItem(6);
                    } else {
                        this.menuItem.showSubItem(6);
                    }
                    this.needSearchImageInArr = false;
                    this.currentIndex = -1;
                    if (setToImage != -1) {
                        setImageIndex(setToImage, true);
                    } else {
                        this.avatarsArr.add(0, new TL_photoEmpty());
                        this.imagesArrLocations.add(0, this.currentFileLocation);
                        this.imagesArrLocationsSizes.add(0, Integer.valueOf(0));
                        setImageIndex(0, true);
                    }
                    if (fromCache) {
                        MessagesController.getInstance().loadDialogPhotos(this.avatarsDialogId, 0, 80, 0, false, this.classGuid);
                    }
                }
            }
        } else if (id == NotificationCenter.mediaCountDidLoaded) {
            uid = ((Long) args[0]).longValue();
            if (uid == this.currentDialogId || uid == this.mergeDialogId) {
                if (uid == this.currentDialogId) {
                    this.totalImagesCount = ((Integer) args[1]).intValue();
                } else if (uid == this.mergeDialogId) {
                    this.totalImagesCountMerge = ((Integer) args[1]).intValue();
                }
                if (this.needSearchImageInArr && this.isFirstLoading) {
                    this.isFirstLoading = false;
                    this.loadingMoreImages = true;
                    SharedMediaQuery.loadMedia(this.currentDialogId, 0, 80, 0, 0, true, this.classGuid);
                } else if (!this.imagesArr.isEmpty()) {
                    if (this.opennedFromMedia) {
                        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge)));
                    } else {
                        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf((((this.totalImagesCount + this.totalImagesCountMerge) - this.imagesArr.size()) + this.currentIndex) + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge)));
                    }
                }
            }
        } else if (id == NotificationCenter.mediaDidLoaded) {
            uid = ((Long) args[0]).longValue();
            guid = ((Integer) args[3]).intValue();
            if ((uid == this.currentDialogId || uid == this.mergeDialogId) && guid == this.classGuid) {
                this.loadingMoreImages = false;
                int loadIndex = uid == this.currentDialogId ? 0 : 1;
                ArrayList<MessageObject> arr = args[2];
                this.endReached[loadIndex] = ((Boolean) args[5]).booleanValue();
                int added;
                MessageObject message;
                if (!this.needSearchImageInArr) {
                    added = 0;
                    Iterator it = arr.iterator();
                    while (it.hasNext()) {
                        message = (MessageObject) it.next();
                        if (!this.imagesByIds[loadIndex].containsKey(Integer.valueOf(message.getId()))) {
                            added++;
                            if (this.opennedFromMedia) {
                                this.imagesArr.add(message);
                            } else {
                                this.imagesArr.add(0, message);
                            }
                            this.imagesByIds[loadIndex].put(Integer.valueOf(message.getId()), message);
                        }
                    }
                    if (this.opennedFromMedia) {
                        if (added == 0) {
                            this.totalImagesCount = this.imagesArr.size();
                            this.totalImagesCountMerge = 0;
                        }
                    } else if (added != 0) {
                        int index = this.currentIndex;
                        this.currentIndex = -1;
                        setImageIndex(index + added, true);
                    } else {
                        this.totalImagesCount = this.imagesArr.size();
                        this.totalImagesCountMerge = 0;
                    }
                } else if (!arr.isEmpty() || (loadIndex == 0 && this.mergeDialogId != 0)) {
                    int foundIndex = -1;
                    MessageObject currentMessage = (MessageObject) this.imagesArr.get(this.currentIndex);
                    added = 0;
                    for (a = 0; a < arr.size(); a++) {
                        message = (MessageObject) arr.get(a);
                        if (!this.imagesByIdsTemp[loadIndex].containsKey(Integer.valueOf(message.getId()))) {
                            this.imagesByIdsTemp[loadIndex].put(Integer.valueOf(message.getId()), message);
                            if (this.opennedFromMedia) {
                                this.imagesArrTemp.add(message);
                                if (message.getId() == currentMessage.getId()) {
                                    foundIndex = added;
                                }
                                added++;
                            } else {
                                added++;
                                this.imagesArrTemp.add(0, message);
                                if (message.getId() == currentMessage.getId()) {
                                    foundIndex = arr.size() - added;
                                }
                            }
                        }
                    }
                    if (added == 0 && (loadIndex != 0 || this.mergeDialogId == 0)) {
                        this.totalImagesCount = this.imagesArr.size();
                        this.totalImagesCountMerge = 0;
                    }
                    if (foundIndex != -1) {
                        this.imagesArr.clear();
                        this.imagesArr.addAll(this.imagesArrTemp);
                        for (a = 0; a < 2; a++) {
                            this.imagesByIds[a].clear();
                            this.imagesByIds[a].putAll(this.imagesByIdsTemp[a]);
                            this.imagesByIdsTemp[a].clear();
                        }
                        this.imagesArrTemp.clear();
                        this.needSearchImageInArr = false;
                        this.currentIndex = -1;
                        if (foundIndex >= this.imagesArr.size()) {
                            foundIndex = this.imagesArr.size() - 1;
                        }
                        setImageIndex(foundIndex, true);
                        return;
                    }
                    int loadFromMaxId;
                    if (this.opennedFromMedia) {
                        loadFromMaxId = this.imagesArrTemp.isEmpty() ? 0 : ((MessageObject) this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getId();
                        if (loadIndex == 0 && this.endReached[loadIndex] && this.mergeDialogId != 0) {
                            loadIndex = 1;
                            if (!(this.imagesArrTemp.isEmpty() || ((MessageObject) this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getDialogId() == this.mergeDialogId)) {
                                loadFromMaxId = 0;
                            }
                        }
                    } else {
                        if (this.imagesArrTemp.isEmpty()) {
                            loadFromMaxId = 0;
                        } else {
                            loadFromMaxId = ((MessageObject) this.imagesArrTemp.get(0)).getId();
                        }
                        if (loadIndex == 0 && this.endReached[loadIndex] && this.mergeDialogId != 0) {
                            loadIndex = 1;
                            if (!(this.imagesArrTemp.isEmpty() || ((MessageObject) this.imagesArrTemp.get(0)).getDialogId() == this.mergeDialogId)) {
                                loadFromMaxId = 0;
                            }
                        }
                    }
                    if (!this.endReached[loadIndex]) {
                        this.loadingMoreImages = true;
                        if (this.opennedFromMedia) {
                            long j;
                            if (loadIndex == 0) {
                                j = this.currentDialogId;
                            } else {
                                j = this.mergeDialogId;
                            }
                            SharedMediaQuery.loadMedia(j, 0, 80, loadFromMaxId, 0, true, this.classGuid);
                            return;
                        }
                        SharedMediaQuery.loadMedia(loadIndex == 0 ? this.currentDialogId : this.mergeDialogId, 0, 80, loadFromMaxId, 0, true, this.classGuid);
                    }
                } else {
                    this.needSearchImageInArr = false;
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded && this.captionTextView != null) {
            this.captionTextView.invalidate();
        }
    }

    private void onSharePressed() {
        Throwable e;
        if (this.parentActivity != null && this.allowShare) {
            File f = null;
            boolean isVideo = false;
            try {
                if (this.currentMessageObject != null) {
                    isVideo = this.currentMessageObject.isVideo();
                    if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                        File f2 = new File(this.currentMessageObject.messageOwner.attachPath);
                        try {
                            if (f2.exists()) {
                                f = f2;
                            } else {
                                f = null;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            f = f2;
                            FileLog.e(e);
                        }
                    }
                    if (f == null) {
                        f = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
                    }
                } else if (this.currentFileLocation != null) {
                    f = FileLoader.getPathToAttach(this.currentFileLocation, this.avatarsDialogId != 0);
                }
                if (f.exists()) {
                    Intent intent = new Intent("android.intent.action.SEND");
                    if (isVideo) {
                        intent.setType(MimeTypes.VIDEO_MP4);
                    } else {
                        intent.setType("image/jpeg");
                    }
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                    this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                    return;
                }
                Builder builder = new Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                showAlertDialog(builder);
            } catch (Exception e3) {
                e = e3;
                FileLog.e(e);
            }
        }
    }

    private void setScaleToFill() {
        float bitmapWidth = (float) this.centerImage.getBitmapWidth();
        float containerWidth = (float) getContainerViewWidth();
        float bitmapHeight = (float) this.centerImage.getBitmapHeight();
        float containerHeight = (float) getContainerViewHeight();
        float scaleFit = Math.min(containerHeight / bitmapHeight, containerWidth / bitmapWidth);
        this.scale = Math.max(containerWidth / ((float) ((int) (bitmapWidth * scaleFit))), containerHeight / ((float) ((int) (bitmapHeight * scaleFit))));
        updateMinMax(this.scale);
    }

    public void setParentAlert(ChatAttachAlert alert) {
        this.parentAlert = alert;
    }

    public void setParentActivity(Activity activity) {
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.actvityContext = new ContextThemeWrapper(this.parentActivity, R.style.Theme.TMessages);
            if (progressDrawables == null) {
                progressDrawables = new Drawable[4];
                progressDrawables[0] = this.parentActivity.getResources().getDrawable(R.drawable.circle_big);
                progressDrawables[1] = this.parentActivity.getResources().getDrawable(R.drawable.cancel_big);
                progressDrawables[2] = this.parentActivity.getResources().getDrawable(R.drawable.load_big);
                progressDrawables[3] = this.parentActivity.getResources().getDrawable(R.drawable.play_big);
            }
            this.scroller = new Scroller(activity);
            this.windowView = new FrameLayout(activity) {
                private Runnable attachRunnable;

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return PhotoViewer.this.isVisible && super.onInterceptTouchEvent(ev);
                }

                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.isVisible && PhotoViewer.this.onTouchEvent(event);
                }

                protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    if (VERSION.SDK_INT >= 21 && child == PhotoViewer.this.animatingImageView && PhotoViewer.this.lastInsets != null) {
                        canvas.drawRect(0.0f, (float) getMeasuredHeight(), (float) getMeasuredWidth(), (float) (getMeasuredHeight() + ((WindowInsets) PhotoViewer.this.lastInsets).getSystemWindowInsetBottom()), PhotoViewer.this.blackPaint);
                    }
                    return result;
                }

                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                    if (VERSION.SDK_INT >= 21 && PhotoViewer.this.lastInsets != null) {
                        WindowInsets insets = (WindowInsets) PhotoViewer.this.lastInsets;
                        if (AndroidUtilities.incorrectDisplaySizeFix) {
                            if (heightSize > AndroidUtilities.displaySize.y) {
                                heightSize = AndroidUtilities.displaySize.y;
                            }
                            heightSize += AndroidUtilities.statusBarHeight;
                        }
                        heightSize -= insets.getSystemWindowInsetBottom();
                        widthSize -= insets.getSystemWindowInsetRight();
                    } else if (heightSize > AndroidUtilities.displaySize.y) {
                        heightSize = AndroidUtilities.displaySize.y;
                    }
                    setMeasuredDimension(widthSize, heightSize);
                    if (VERSION.SDK_INT >= 21 && PhotoViewer.this.lastInsets != null) {
                        widthSize -= ((WindowInsets) PhotoViewer.this.lastInsets).getSystemWindowInsetLeft();
                    }
                    ViewGroup.LayoutParams layoutParams = PhotoViewer.this.animatingImageView.getLayoutParams();
                    PhotoViewer.this.animatingImageView.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
                    PhotoViewer.this.containerView.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
                }

                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int x = 0;
                    if (VERSION.SDK_INT >= 21 && PhotoViewer.this.lastInsets != null) {
                        x = 0 + ((WindowInsets) PhotoViewer.this.lastInsets).getSystemWindowInsetLeft();
                    }
                    PhotoViewer.this.animatingImageView.layout(x, 0, PhotoViewer.this.animatingImageView.getMeasuredWidth() + x, PhotoViewer.this.animatingImageView.getMeasuredHeight());
                    PhotoViewer.this.containerView.layout(x, 0, PhotoViewer.this.containerView.getMeasuredWidth() + x, PhotoViewer.this.containerView.getMeasuredHeight());
                    PhotoViewer.this.wasLayout = true;
                    if (changed) {
                        if (!PhotoViewer.this.dontResetZoomOnFirstLayout) {
                            PhotoViewer.this.scale = 1.0f;
                            PhotoViewer.this.translationX = 0.0f;
                            PhotoViewer.this.translationY = 0.0f;
                            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                        }
                        if (PhotoViewer.this.checkImageView != null) {
                            PhotoViewer.this.checkImageView.post(new Runnable() {
                                public void run() {
                                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.checkImageView.getLayoutParams();
                                    int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                                    float f = (rotation == 3 || rotation == 1) ? 58.0f : 68.0f;
                                    layoutParams.topMargin = (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(f);
                                    PhotoViewer.this.checkImageView.setLayoutParams(layoutParams);
                                }
                            });
                        }
                    }
                    if (PhotoViewer.this.dontResetZoomOnFirstLayout) {
                        PhotoViewer.this.setScaleToFill();
                        PhotoViewer.this.dontResetZoomOnFirstLayout = false;
                    }
                }

                protected void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    PhotoViewer.this.attachedToWindow = true;
                }

                protected void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    PhotoViewer.this.attachedToWindow = false;
                    PhotoViewer.this.wasLayout = false;
                }

                public boolean dispatchKeyEventPreIme(KeyEvent event) {
                    if (event == null || event.getKeyCode() != 4 || event.getAction() != 1) {
                        return super.dispatchKeyEventPreIme(event);
                    }
                    if (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible()) {
                        PhotoViewer.this.closeCaptionEnter(false);
                        return false;
                    }
                    PhotoViewer.getInstance().closePhoto(true, false);
                    return true;
                }

                public ActionMode startActionModeForChild(View originalView, Callback callback, int type) {
                    if (VERSION.SDK_INT >= 23) {
                        View view = PhotoViewer.this.parentActivity.findViewById(16908290);
                        if (view instanceof ViewGroup) {
                            try {
                                return ((ViewGroup) view).startActionModeForChild(originalView, callback, type);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    }
                    return super.startActionModeForChild(originalView, callback, type);
                }
            };
            this.windowView.setBackgroundDrawable(this.backgroundDrawable);
            this.windowView.setClipChildren(true);
            this.windowView.setFocusable(false);
            this.animatingImageView = new ClippingImageView(activity);
            this.animatingImageView.setAnimationValues(this.animationValues);
            this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            if (VERSION.SDK_INT >= 21) {
                this.containerView.setFitsSystemWindows(true);
                this.containerView.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
                    @SuppressLint({"NewApi"})
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        WindowInsets oldInsets = (WindowInsets) PhotoViewer.this.lastInsets;
                        PhotoViewer.this.lastInsets = insets;
                        if (oldInsets == null || !oldInsets.toString().equals(insets.toString())) {
                            PhotoViewer.this.windowView.requestLayout();
                        }
                        return insets.consumeSystemWindowInsets();
                    }
                });
                this.containerView.setSystemUiVisibility(1280);
            }
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -NUM;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.actionBar = new ActionBar(activity);
            this.actionBar.setTitleColor(-1);
            this.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.actionBar.setOccupyStatusBar(VERSION.SDK_INT >= 21);
            this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(1), Integer.valueOf(1)));
            this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    if (id == -1) {
                        if (PhotoViewer.this.needCaptionLayout && (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible())) {
                            PhotoViewer.this.closeCaptionEnter(false);
                        } else {
                            PhotoViewer.this.closePhoto(true, false);
                        }
                    } else if (id == 1) {
                        if (VERSION.SDK_INT < 23 || PhotoViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                            File f = null;
                            if (PhotoViewer.this.currentMessageObject != null) {
                                f = FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
                            } else if (PhotoViewer.this.currentFileLocation != null) {
                                f = FileLoader.getPathToAttach(PhotoViewer.this.currentFileLocation, PhotoViewer.this.avatarsDialogId != 0);
                            }
                            if (f == null || !f.exists()) {
                                builder = new Builder(PhotoViewer.this.parentActivity);
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                                PhotoViewer.this.showAlertDialog(builder);
                                return;
                            }
                            String file = f.toString();
                            Context access$600 = PhotoViewer.this.parentActivity;
                            if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isVideo()) {
                                r2 = 0;
                            } else {
                                r2 = 1;
                            }
                            MediaController.saveFile(file, access$600, r2, null, null);
                            return;
                        }
                        PhotoViewer.this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    } else if (id == 2) {
                        if (PhotoViewer.this.opennedFromMedia) {
                            PhotoViewer.this.closePhoto(true, false);
                        } else if (PhotoViewer.this.currentDialogId != 0) {
                            PhotoViewer.this.disableShowCheck = true;
                            Bundle args2 = new Bundle();
                            args2.putLong("dialog_id", PhotoViewer.this.currentDialogId);
                            BaseFragment mediaActivity = new MediaActivity(args2);
                            if (PhotoViewer.this.parentChatActivity != null) {
                                mediaActivity.setChatInfo(PhotoViewer.this.parentChatActivity.getCurrentChatInfo());
                            }
                            PhotoViewer.this.closePhoto(false, false);
                            ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(mediaActivity, false, true);
                        }
                    } else if (id == 3) {
                    } else {
                        if (id == 6) {
                            if (PhotoViewer.this.parentActivity != null) {
                                builder = new Builder(PhotoViewer.this.parentActivity);
                                if (PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.currentMessageObject.isVideo()) {
                                    builder.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", R.string.AreYouSureDeleteVideo, new Object[0]));
                                } else if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isGif()) {
                                    builder.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", R.string.AreYouSureDeletePhoto, new Object[0]));
                                } else {
                                    builder.setMessage(LocaleController.formatString("AreYouSure", R.string.AreYouSure, new Object[0]));
                                }
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                final boolean[] deleteForAll = new boolean[1];
                                if (PhotoViewer.this.currentMessageObject != null) {
                                    int lower_id = (int) PhotoViewer.this.currentMessageObject.getDialogId();
                                    if (lower_id != 0) {
                                        User currentUser;
                                        Chat currentChat;
                                        if (lower_id > 0) {
                                            currentUser = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
                                            currentChat = null;
                                        } else {
                                            currentUser = null;
                                            currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                                        }
                                        if (!(currentUser == null && ChatObject.isChannel(currentChat))) {
                                            int currentDate = ConnectionsManager.getInstance().getCurrentTime();
                                            if (!((currentUser == null || currentUser.id == UserConfig.getClientUserId()) && currentChat == null) && ((PhotoViewer.this.currentMessageObject.messageOwner.action == null || (PhotoViewer.this.currentMessageObject.messageOwner.action instanceof TL_messageActionEmpty)) && PhotoViewer.this.currentMessageObject.isOut() && currentDate - PhotoViewer.this.currentMessageObject.messageOwner.date <= 172800)) {
                                                int dp;
                                                View frameLayout = new FrameLayout(PhotoViewer.this.parentActivity);
                                                CheckBoxCell cell = new CheckBoxCell(PhotoViewer.this.parentActivity, true);
                                                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                                if (currentChat != null) {
                                                    cell.setText(LocaleController.getString("DeleteForAll", R.string.DeleteForAll), "", false, false);
                                                } else {
                                                    cell.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, UserObject.getFirstName(currentUser)), "", false, false);
                                                }
                                                if (LocaleController.isRTL) {
                                                    r2 = AndroidUtilities.dp(16.0f);
                                                } else {
                                                    r2 = AndroidUtilities.dp(8.0f);
                                                }
                                                if (LocaleController.isRTL) {
                                                    dp = AndroidUtilities.dp(8.0f);
                                                } else {
                                                    dp = AndroidUtilities.dp(16.0f);
                                                }
                                                cell.setPadding(r2, 0, dp, 0);
                                                frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                                cell.setOnClickListener(new OnClickListener() {
                                                    public void onClick(View v) {
                                                        boolean z;
                                                        CheckBoxCell cell = (CheckBoxCell) v;
                                                        boolean[] zArr = deleteForAll;
                                                        if (deleteForAll[0]) {
                                                            z = false;
                                                        } else {
                                                            z = true;
                                                        }
                                                        zArr[0] = z;
                                                        cell.setChecked(deleteForAll[0], true);
                                                    }
                                                });
                                                builder.setView(frameLayout);
                                            }
                                        }
                                    }
                                }
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (PhotoViewer.this.imagesArr.isEmpty()) {
                                            if (!PhotoViewer.this.avatarsArr.isEmpty() && PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.avatarsArr.size()) {
                                                Photo photo = (Photo) PhotoViewer.this.avatarsArr.get(PhotoViewer.this.currentIndex);
                                                FileLocation currentLocation = (FileLocation) PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
                                                if (photo instanceof TL_photoEmpty) {
                                                    photo = null;
                                                }
                                                boolean current = false;
                                                if (PhotoViewer.this.currentUserAvatarLocation != null) {
                                                    if (photo != null) {
                                                        Iterator it = photo.sizes.iterator();
                                                        while (it.hasNext()) {
                                                            PhotoSize size = (PhotoSize) it.next();
                                                            if (size.location.local_id == PhotoViewer.this.currentUserAvatarLocation.local_id && size.location.volume_id == PhotoViewer.this.currentUserAvatarLocation.volume_id) {
                                                                current = true;
                                                                break;
                                                            }
                                                        }
                                                    } else if (currentLocation.local_id == PhotoViewer.this.currentUserAvatarLocation.local_id && currentLocation.volume_id == PhotoViewer.this.currentUserAvatarLocation.volume_id) {
                                                        current = true;
                                                    }
                                                }
                                                if (current) {
                                                    MessagesController.getInstance().deleteUserPhoto(null);
                                                    PhotoViewer.this.closePhoto(false, false);
                                                } else if (photo != null) {
                                                    TL_inputPhoto inputPhoto = new TL_inputPhoto();
                                                    inputPhoto.id = photo.id;
                                                    inputPhoto.access_hash = photo.access_hash;
                                                    MessagesController.getInstance().deleteUserPhoto(inputPhoto);
                                                    MessagesStorage.getInstance().clearUserPhoto(PhotoViewer.this.avatarsDialogId, photo.id);
                                                    PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                                                    PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                                                    PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                                                    if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
                                                        PhotoViewer.this.closePhoto(false, false);
                                                        return;
                                                    }
                                                    int index = PhotoViewer.this.currentIndex;
                                                    if (index >= PhotoViewer.this.avatarsArr.size()) {
                                                        index = PhotoViewer.this.avatarsArr.size() - 1;
                                                    }
                                                    PhotoViewer.this.currentIndex = -1;
                                                    PhotoViewer.this.setImageIndex(index, true);
                                                }
                                            }
                                        } else if (PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size()) {
                                            MessageObject obj = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                                            if (obj.isSent()) {
                                                PhotoViewer.this.closePhoto(false, false);
                                                ArrayList<Integer> arr = new ArrayList();
                                                arr.add(Integer.valueOf(obj.getId()));
                                                ArrayList<Long> random_ids = null;
                                                EncryptedChat encryptedChat = null;
                                                if (((int) obj.getDialogId()) == 0 && obj.messageOwner.random_id != 0) {
                                                    random_ids = new ArrayList();
                                                    random_ids.add(Long.valueOf(obj.messageOwner.random_id));
                                                    encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (obj.getDialogId() >> 32)));
                                                }
                                                MessagesController.getInstance().deleteMessages(arr, random_ids, encryptedChat, obj.messageOwner.to_id.channel_id, deleteForAll[0]);
                                            }
                                        }
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                PhotoViewer.this.showAlertDialog(builder);
                            }
                        } else if (id == 10) {
                            PhotoViewer.this.onSharePressed();
                        } else if (id == 11) {
                            try {
                                AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                                PhotoViewer.this.closePhoto(false, false);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        } else if (id == 12) {
                            PhotoViewer.this.muteVideo = !PhotoViewer.this.muteVideo;
                            if (PhotoViewer.this.videoPlayer != null) {
                                PhotoViewer.this.videoPlayer.setMute(PhotoViewer.this.muteVideo);
                            }
                            if (PhotoViewer.this.muteVideo) {
                                PhotoViewer.this.actionBar.setTitle(LocaleController.getString("AttachGif", R.string.AttachGif));
                                PhotoViewer.this.muteItem.setIcon(R.drawable.volume_off);
                                return;
                            }
                            PhotoViewer.this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                            PhotoViewer.this.muteItem.setIcon(R.drawable.volume_on);
                        } else if (id == 13 && PhotoViewer.this.parentActivity != null && PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.currentMessageObject.messageOwner.media != null && PhotoViewer.this.currentMessageObject.messageOwner.media.photo != null) {
                            new StickersAlert(PhotoViewer.this.parentActivity, PhotoViewer.this.currentMessageObject.messageOwner.media.photo).show();
                        }
                    }
                }

                public boolean canOpenMenu() {
                    if (PhotoViewer.this.currentMessageObject != null) {
                        if (FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner).exists()) {
                            return true;
                        }
                    } else if (PhotoViewer.this.currentFileLocation != null) {
                        if (FileLoader.getPathToAttach(PhotoViewer.this.currentFileLocation, PhotoViewer.this.avatarsDialogId != 0).exists()) {
                            return true;
                        }
                    }
                    return false;
                }
            });
            ActionBarMenu menu = this.actionBar.createMenu();
            this.masksItem = menu.addItem(13, (int) R.drawable.ic_masks_msk1);
            this.muteItem = menu.addItem(12, (int) R.drawable.volume_on);
            this.menuItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
            this.menuItem.addSubItem(11, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
            this.menuItem.addSubItem(2, LocaleController.getString("ShowAllMedia", R.string.ShowAllMedia));
            this.menuItem.addSubItem(10, LocaleController.getString("ShareFile", R.string.ShareFile));
            this.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
            this.menuItem.addSubItem(6, LocaleController.getString("Delete", R.string.Delete));
            this.bottomLayout = new FrameLayout(this.actvityContext);
            this.bottomLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.captionTextViewOld = new TextView(this.actvityContext) {
                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.captionTextViewOld.setMaxLines(10);
            this.captionTextViewOld.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.captionTextViewOld.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
            this.captionTextViewOld.setLinkTextColor(-1);
            this.captionTextViewOld.setTextColor(-1);
            this.captionTextViewOld.setGravity(19);
            this.captionTextViewOld.setTextSize(1, 16.0f);
            this.captionTextViewOld.setVisibility(4);
            this.containerView.addView(this.captionTextViewOld, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.captionTextViewOld.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (PhotoViewer.this.cropItem.getVisibility() == 0) {
                        PhotoViewer.this.openCaptionEnter();
                    }
                }
            });
            TextView anonymousClass7 = new TextView(this.actvityContext) {
                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.captionTextViewNew = anonymousClass7;
            this.captionTextView = anonymousClass7;
            this.captionTextViewNew.setMaxLines(10);
            this.captionTextViewNew.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.captionTextViewNew.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
            this.captionTextViewNew.setLinkTextColor(-1);
            this.captionTextViewNew.setTextColor(-1);
            this.captionTextViewNew.setGravity(19);
            this.captionTextViewNew.setTextSize(1, 16.0f);
            this.captionTextViewNew.setVisibility(4);
            this.containerView.addView(this.captionTextViewNew, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.captionTextViewNew.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (PhotoViewer.this.cropItem.getVisibility() == 0) {
                        PhotoViewer.this.openCaptionEnter();
                    }
                }
            });
            this.radialProgressViews[0] = new RadialProgressView(this.containerView.getContext(), this.containerView);
            this.radialProgressViews[0].setBackgroundState(0, false);
            this.radialProgressViews[1] = new RadialProgressView(this.containerView.getContext(), this.containerView);
            this.radialProgressViews[1].setBackgroundState(0, false);
            this.radialProgressViews[2] = new RadialProgressView(this.containerView.getContext(), this.containerView);
            this.radialProgressViews[2].setBackgroundState(0, false);
            this.shareButton = new ImageView(this.containerView.getContext());
            this.shareButton.setImageResource(R.drawable.share);
            this.shareButton.setScaleType(ScaleType.CENTER);
            this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.bottomLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1, 53));
            this.shareButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PhotoViewer.this.onSharePressed();
                }
            });
            this.nameTextView = new TextView(this.containerView.getContext());
            this.nameTextView.setTextSize(1, 14.0f);
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setEllipsize(TruncateAt.END);
            this.nameTextView.setTextColor(-1);
            this.nameTextView.setGravity(3);
            this.bottomLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 5.0f, BitmapDescriptorFactory.HUE_YELLOW, 0.0f));
            this.dateTextView = new TextView(this.containerView.getContext());
            this.dateTextView.setTextSize(1, 13.0f);
            this.dateTextView.setSingleLine(true);
            this.dateTextView.setMaxLines(1);
            this.dateTextView.setEllipsize(TruncateAt.END);
            this.dateTextView.setTextColor(-1);
            this.dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.dateTextView.setGravity(3);
            this.bottomLayout.addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 25.0f, 50.0f, 0.0f));
            if (VERSION.SDK_INT >= 16) {
                this.videoPlayerSeekbar = new SeekBar(this.containerView.getContext());
                this.videoPlayerSeekbar.setColors(NUM, -1, -1);
                this.videoPlayerSeekbar.setDelegate(new SeekBarDelegate() {
                    public void onSeekBarDrag(float progress) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            PhotoViewer.this.videoPlayer.seekTo((long) ((int) (((float) PhotoViewer.this.videoPlayer.getDuration()) * progress)));
                        }
                    }
                });
                this.videoPlayerControlFrameLayout = new FrameLayout(this.containerView.getContext()) {
                    public boolean onTouchEvent(MotionEvent event) {
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        if (!PhotoViewer.this.videoPlayerSeekbar.onTouch(event.getAction(), event.getX() - ((float) AndroidUtilities.dp(48.0f)), event.getY())) {
                            return super.onTouchEvent(event);
                        }
                        getParent().requestDisallowInterceptTouchEvent(true);
                        invalidate();
                        return true;
                    }

                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        long duration;
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        if (PhotoViewer.this.videoPlayer != null) {
                            duration = PhotoViewer.this.videoPlayer.getDuration();
                            if (duration == C.TIME_UNSET) {
                                duration = 0;
                            }
                        } else {
                            duration = 0;
                        }
                        duration /= 1000;
                        PhotoViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) PhotoViewer.this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60), Long.valueOf(duration / 60), Long.valueOf(duration % 60)})))), getMeasuredHeight());
                    }

                    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                        super.onLayout(changed, left, top, right, bottom);
                        float progress = 0.0f;
                        if (PhotoViewer.this.videoPlayer != null) {
                            progress = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                        }
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(progress);
                    }

                    protected void onDraw(Canvas canvas) {
                        canvas.save();
                        canvas.translate((float) AndroidUtilities.dp(48.0f), 0.0f);
                        PhotoViewer.this.videoPlayerSeekbar.draw(canvas);
                        canvas.restore();
                    }
                };
                this.videoPlayerControlFrameLayout.setWillNotDraw(false);
                this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
                this.videoPlayButton = new ImageView(this.containerView.getContext());
                this.videoPlayButton.setScaleType(ScaleType.CENTER);
                this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
                this.videoPlayButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (PhotoViewer.this.videoPlayer == null) {
                            return;
                        }
                        if (PhotoViewer.this.isPlaying) {
                            PhotoViewer.this.videoPlayer.pause();
                        } else {
                            PhotoViewer.this.videoPlayer.play();
                        }
                    }
                });
                this.videoPlayerTime = new TextView(this.containerView.getContext());
                this.videoPlayerTime.setTextColor(-1);
                this.videoPlayerTime.setGravity(16);
                this.videoPlayerTime.setTextSize(1, 13.0f);
                this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 0.0f, 8.0f, 0.0f));
            }
            this.pickerView = new PickerBottomLayoutViewer(this.actvityContext) {
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev);
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(ev);
                }

                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.pickerView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.containerView.addView(this.pickerView, LayoutHelper.createFrame(-1, 48, 83));
            this.pickerView.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.placeProvider instanceof EmptyPhotoViewerProvider) {
                        PhotoViewer.this.closePhoto(false, false);
                    } else if (PhotoViewer.this.placeProvider != null) {
                        PhotoViewer.this.closePhoto(!PhotoViewer.this.placeProvider.cancelButtonPressed(), false);
                    }
                }
            });
            this.pickerView.doneButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.placeProvider != null && !PhotoViewer.this.doneButtonPressed) {
                        PhotoViewer.this.doneButtonPressed = true;
                        PhotoViewer.this.placeProvider.sendButtonPressed(PhotoViewer.this.currentIndex);
                        PhotoViewer.this.closePhoto(false, false);
                    }
                }
            });
            LinearLayout itemsLayout = new LinearLayout(this.parentActivity);
            itemsLayout.setOrientation(0);
            this.pickerView.addView(itemsLayout, LayoutHelper.createFrame(-2, 48, 49));
            this.tuneItem = new ImageView(this.parentActivity);
            this.tuneItem.setScaleType(ScaleType.CENTER);
            this.tuneItem.setImageResource(R.drawable.photo_tools);
            this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            itemsLayout.addView(this.tuneItem, LayoutHelper.createLinear(56, 48));
            this.tuneItem.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PhotoViewer.this.switchToEditMode(2);
                }
            });
            this.paintItem = new ImageView(this.parentActivity);
            this.paintItem.setScaleType(ScaleType.CENTER);
            this.paintItem.setImageResource(R.drawable.photo_paint);
            this.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            itemsLayout.addView(this.paintItem, LayoutHelper.createLinear(56, 48));
            this.paintItem.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PhotoViewer.this.switchToEditMode(3);
                }
            });
            this.cropItem = new ImageView(this.parentActivity);
            this.cropItem.setScaleType(ScaleType.CENTER);
            this.cropItem.setImageResource(R.drawable.photo_crop);
            this.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            itemsLayout.addView(this.cropItem, LayoutHelper.createLinear(56, 48));
            this.cropItem.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PhotoViewer.this.switchToEditMode(1);
                }
            });
            this.editorDoneLayout = new PickerBottomLayoutViewer(this.actvityContext);
            this.editorDoneLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.editorDoneLayout.updateSelectedCount(0, false);
            this.editorDoneLayout.setVisibility(8);
            this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.editorDoneLayout.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.currentEditMode == 1) {
                        PhotoViewer.this.photoCropView.cancelAnimationRunnable();
                    }
                    PhotoViewer.this.switchToEditMode(0);
                }
            });
            this.editorDoneLayout.doneButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.currentEditMode != 1 || PhotoViewer.this.photoCropView.isReady()) {
                        PhotoViewer.this.applyCurrentEditMode();
                        PhotoViewer.this.switchToEditMode(0);
                    }
                }
            });
            this.resetButton = new TextView(this.actvityContext);
            this.resetButton.setVisibility(8);
            this.resetButton.setTextSize(1, 14.0f);
            this.resetButton.setTextColor(-1);
            this.resetButton.setGravity(17);
            this.resetButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
            this.resetButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            this.resetButton.setText(LocaleController.getString("Reset", R.string.CropReset).toUpperCase());
            this.resetButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.editorDoneLayout.addView(this.resetButton, LayoutHelper.createFrame(-2, -1, 49));
            this.resetButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PhotoViewer.this.photoCropView.reset();
                }
            });
            this.gestureDetector = new GestureDetector(this.containerView.getContext(), this);
            this.gestureDetector.setOnDoubleTapListener(this);
            ImageReceiverDelegate imageReceiverDelegate = new ImageReceiverDelegate() {
                public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
                    if (imageReceiver == PhotoViewer.this.centerImage && set && !thumb && PhotoViewer.this.currentEditMode == 1 && PhotoViewer.this.photoCropView != null) {
                        Bitmap bitmap = imageReceiver.getBitmap();
                        if (bitmap != null) {
                            PhotoViewer.this.photoCropView.setBitmap(bitmap, imageReceiver.getOrientation(), PhotoViewer.this.sendPhotoType != 1);
                        }
                    }
                    if (imageReceiver != PhotoViewer.this.centerImage || !set || PhotoViewer.this.placeProvider == null || !PhotoViewer.this.placeProvider.scaleToFill() || PhotoViewer.this.ignoreDidSetImage) {
                        return;
                    }
                    if (PhotoViewer.this.wasLayout) {
                        PhotoViewer.this.setScaleToFill();
                    } else {
                        PhotoViewer.this.dontResetZoomOnFirstLayout = true;
                    }
                }
            };
            this.centerImage.setParentView(this.containerView);
            this.centerImage.setCrossfadeAlpha((byte) 2);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setDelegate(imageReceiverDelegate);
            this.leftImage.setParentView(this.containerView);
            this.leftImage.setCrossfadeAlpha((byte) 2);
            this.leftImage.setInvalidateAll(true);
            this.leftImage.setDelegate(imageReceiverDelegate);
            this.rightImage.setParentView(this.containerView);
            this.rightImage.setCrossfadeAlpha((byte) 2);
            this.rightImage.setInvalidateAll(true);
            this.rightImage.setDelegate(imageReceiverDelegate);
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.checkImageView = new CheckBox(this.containerView.getContext(), R.drawable.selectphoto_large) {
                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.checkImageView.setDrawBackground(true);
            this.checkImageView.setSize(45);
            this.checkImageView.setCheckOffset(AndroidUtilities.dp(1.0f));
            this.checkImageView.setColor(-12793105, -1);
            this.checkImageView.setVisibility(8);
            FrameLayoutDrawer frameLayoutDrawer = this.containerView;
            View view = this.checkImageView;
            float f = (rotation == 3 || rotation == 1) ? 58.0f : 68.0f;
            frameLayoutDrawer.addView(view, LayoutHelper.createFrame(45, 45.0f, 53, 0.0f, f, 10.0f, 0.0f));
            if (VERSION.SDK_INT >= 21) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.checkImageView.getLayoutParams();
                layoutParams.topMargin += AndroidUtilities.statusBarHeight;
            }
            this.checkImageView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (PhotoViewer.this.placeProvider != null) {
                        PhotoViewer.this.placeProvider.setPhotoChecked(PhotoViewer.this.currentIndex);
                        PhotoViewer.this.checkImageView.setChecked(PhotoViewer.this.placeProvider.isPhotoChecked(PhotoViewer.this.currentIndex), true);
                        PhotoViewer.this.updateSelectedCount();
                    }
                }
            });
            this.captionEditText = new PhotoViewerCaptionEnterView(this.actvityContext, this.containerView, this.windowView) {
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    try {
                        return !PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev);
                    } catch (Throwable e) {
                        FileLog.e(e);
                        return false;
                    }
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    try {
                        return !PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(ev);
                    } catch (Throwable e) {
                        FileLog.e(e);
                        return false;
                    }
                }

                public boolean onTouchEvent(MotionEvent event) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.captionEditText.setDelegate(new PhotoViewerCaptionEnterViewDelegate() {
                public void onCaptionEnter() {
                    PhotoViewer.this.closeCaptionEnter(true);
                }

                public void onTextChanged(CharSequence text) {
                    if (PhotoViewer.this.mentionsAdapter != null && PhotoViewer.this.captionEditText != null && PhotoViewer.this.parentChatActivity != null && text != null) {
                        PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(text.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages);
                    }
                }

                public void onWindowSizeChanged(int size) {
                    int i;
                    int min = Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36;
                    if (PhotoViewer.this.mentionsAdapter.getItemCount() > 3) {
                        i = 18;
                    } else {
                        i = 0;
                    }
                    if (size - (ActionBar.getCurrentActionBarHeight() * 2) < AndroidUtilities.dp((float) (i + min))) {
                        PhotoViewer.this.allowMentions = false;
                        if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setVisibility(4);
                            return;
                        }
                        return;
                    }
                    PhotoViewer.this.allowMentions = true;
                    if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 4) {
                        PhotoViewer.this.mentionListView.setVisibility(0);
                    }
                }
            });
            this.containerView.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
            this.mentionListView = new RecyclerListView(this.actvityContext) {
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev);
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(ev);
                }

                public boolean onTouchEvent(MotionEvent event) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.mentionListView.setTag(Integer.valueOf(5));
            this.mentionLayoutManager = new LinearLayoutManager(this.actvityContext) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            this.mentionLayoutManager.setOrientation(1);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.mentionListView.setVisibility(8);
            this.mentionListView.setClipToPadding(true);
            this.mentionListView.setOverScrollMode(2);
            this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
            RecyclerListView recyclerListView = this.mentionListView;
            Adapter mentionsAdapter = new MentionsAdapter(this.actvityContext, true, 0, new MentionsAdapterDelegate() {
                public void needChangePanelVisibility(boolean show) {
                    if (show) {
                        int i;
                        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) PhotoViewer.this.mentionListView.getLayoutParams();
                        int min = Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36;
                        if (PhotoViewer.this.mentionsAdapter.getItemCount() > 3) {
                            i = 18;
                        } else {
                            i = 0;
                        }
                        int height = min + i;
                        layoutParams3.height = AndroidUtilities.dp((float) height);
                        layoutParams3.topMargin = -AndroidUtilities.dp((float) height);
                        PhotoViewer.this.mentionListView.setLayoutParams(layoutParams3);
                        if (PhotoViewer.this.mentionListAnimation != null) {
                            PhotoViewer.this.mentionListAnimation.cancel();
                            PhotoViewer.this.mentionListAnimation = null;
                        }
                        if (PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setAlpha(1.0f);
                            return;
                        }
                        PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
                        if (PhotoViewer.this.allowMentions) {
                            PhotoViewer.this.mentionListView.setVisibility(0);
                            PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                            PhotoViewer.this.mentionListAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[]{0.0f, 1.0f})});
                            PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animation)) {
                                        PhotoViewer.this.mentionListAnimation = null;
                                    }
                                }
                            });
                            PhotoViewer.this.mentionListAnimation.setDuration(200);
                            PhotoViewer.this.mentionListAnimation.start();
                            return;
                        }
                        PhotoViewer.this.mentionListView.setAlpha(1.0f);
                        PhotoViewer.this.mentionListView.setVisibility(4);
                        return;
                    }
                    if (PhotoViewer.this.mentionListAnimation != null) {
                        PhotoViewer.this.mentionListAnimation.cancel();
                        PhotoViewer.this.mentionListAnimation = null;
                    }
                    if (PhotoViewer.this.mentionListView.getVisibility() == 8) {
                        return;
                    }
                    if (PhotoViewer.this.allowMentions) {
                        PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                        AnimatorSet access$7300 = PhotoViewer.this.mentionListAnimation;
                        Animator[] animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[]{0.0f});
                        access$7300.playTogether(animatorArr);
                        PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animation)) {
                                    PhotoViewer.this.mentionListView.setVisibility(8);
                                    PhotoViewer.this.mentionListAnimation = null;
                                }
                            }
                        });
                        PhotoViewer.this.mentionListAnimation.setDuration(200);
                        PhotoViewer.this.mentionListAnimation.start();
                        return;
                    }
                    PhotoViewer.this.mentionListView.setVisibility(8);
                }

                public void onContextSearch(boolean searching) {
                }

                public void onContextClick(BotInlineResult result) {
                }
            });
            this.mentionsAdapter = mentionsAdapter;
            recyclerListView.setAdapter(mentionsAdapter);
            this.mentionsAdapter.setAllowNewMentions(false);
            this.mentionListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    User object = PhotoViewer.this.mentionsAdapter.getItem(position);
                    int start = PhotoViewer.this.mentionsAdapter.getResultStartPosition();
                    int len = PhotoViewer.this.mentionsAdapter.getResultLength();
                    if (object instanceof User) {
                        User user = object;
                        if (user != null) {
                            PhotoViewer.this.captionEditText.replaceWithText(start, len, "@" + user.username + " ");
                        }
                    } else if (object instanceof String) {
                        PhotoViewer.this.captionEditText.replaceWithText(start, len, object + " ");
                    }
                }
            });
            this.mentionListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemClick(View view, int position) {
                    if (!(PhotoViewer.this.mentionsAdapter.getItem(position) instanceof String)) {
                        return false;
                    }
                    Builder builder = new Builder(PhotoViewer.this.parentActivity);
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PhotoViewer.this.mentionsAdapter.clearRecentHashtags();
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    PhotoViewer.this.showAlertDialog(builder);
                    return true;
                }
            });
        }
    }

    private void openCaptionEnter() {
        if (this.imageMoveAnimation == null && this.changeModeAnimation == null && this.currentEditMode == 0) {
            this.captionEditText.setTag(Integer.valueOf(1));
            this.captionEditText.openKeyboard();
            this.lastTitle = this.actionBar.getTitle();
            this.actionBar.setTitle(LocaleController.getString("PhotoCaption", R.string.PhotoCaption));
        }
    }

    private void updateVideoPlayerTime() {
        String newText;
        if (this.videoPlayer == null) {
            newText = "00:00 / 00:00";
        } else {
            long current = this.videoPlayer.getCurrentPosition();
            long total = this.videoPlayer.getDuration();
            if (this.muteItemAvailable) {
                if (total >= 30000) {
                    if (this.muteItem.getVisibility() == 0) {
                        this.muteItem.setVisibility(8);
                    }
                } else if (this.muteItem.getVisibility() != 0) {
                    this.muteItem.setVisibility(0);
                }
            }
            if (total == C.TIME_UNSET || current == C.TIME_UNSET) {
                newText = "00:00 / 00:00";
            } else {
                current /= 1000;
                total /= 1000;
                newText = String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(current / 60), Long.valueOf(current % 60), Long.valueOf(total / 60), Long.valueOf(total % 60)});
            }
        }
        if (!TextUtils.equals(this.videoPlayerTime.getText(), newText)) {
            this.videoPlayerTime.setText(newText);
        }
    }

    @SuppressLint({"NewApi"})
    private void preparePlayer(File file, boolean playWhenReady) {
        if (this.parentActivity != null) {
            releasePlayer();
            if (this.videoTextureView == null) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity);
                this.aspectRatioFrameLayout.setVisibility(4);
                this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                this.videoTextureView = new TextureView(this.parentActivity);
                this.videoTextureView.setOpaque(false);
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView.setAlpha(0.0f);
            this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            if (this.videoPlayer == null) {
                long duration;
                this.videoPlayer = new VideoPlayer();
                this.videoPlayer.setTextureView(this.videoTextureView);
                this.videoPlayer.setDelegate(new VideoPlayerDelegate() {
                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            if (playbackState == 4 || playbackState == 1) {
                                try {
                                    PhotoViewer.this.parentActivity.getWindow().clearFlags(128);
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            } else {
                                try {
                                    PhotoViewer.this.parentActivity.getWindow().addFlags(128);
                                } catch (Throwable e2) {
                                    FileLog.e(e2);
                                }
                            }
                            if (playbackState == 3 && PhotoViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                PhotoViewer.this.aspectRatioFrameLayout.setVisibility(0);
                            }
                            if (!PhotoViewer.this.videoPlayer.isPlaying() || playbackState == 4) {
                                if (PhotoViewer.this.isPlaying) {
                                    PhotoViewer.this.isPlaying = false;
                                    PhotoViewer.this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
                                    AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.updateProgressRunnable);
                                    if (playbackState == 4 && !PhotoViewer.this.videoPlayerSeekbar.isDragging()) {
                                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                                        PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                                        PhotoViewer.this.videoPlayer.seekTo(0);
                                        PhotoViewer.this.videoPlayer.pause();
                                    }
                                }
                            } else if (!PhotoViewer.this.isPlaying) {
                                PhotoViewer.this.isPlaying = true;
                                PhotoViewer.this.videoPlayButton.setImageResource(R.drawable.inline_video_pause);
                                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable);
                            }
                            PhotoViewer.this.updateVideoPlayerTime();
                        }
                    }

                    public void onError(Exception e) {
                        FileLog.e((Throwable) e);
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                                int temp = width;
                                width = height;
                                height = temp;
                            }
                            PhotoViewer.this.aspectRatioFrameLayout.setAspectRatio(height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height), unappliedRotationDegrees);
                        }
                    }

                    public void onRenderedFirstFrame() {
                        if (!PhotoViewer.this.textureUploaded) {
                            PhotoViewer.this.textureUploaded = true;
                            PhotoViewer.this.containerView.invalidate();
                        }
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }
                });
                if (this.videoPlayer != null) {
                    duration = this.videoPlayer.getDuration();
                    if (duration == C.TIME_UNSET) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                duration /= 1000;
                int ceil = (int) Math.ceil((double) this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60), Long.valueOf(duration / 60), Long.valueOf(duration % 60)})));
            }
            this.videoPlayer.preparePlayer(Uri.fromFile(file), "other");
            if (this.videoPlayerControlFrameLayout != null) {
                if (this.currentBotInlineResult != null && (this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                    this.bottomLayout.setVisibility(0);
                    this.bottomLayout.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                }
                this.videoPlayerControlFrameLayout.setVisibility(0);
                this.dateTextView.setVisibility(8);
                this.nameTextView.setVisibility(8);
                if (this.allowShare) {
                    this.shareButton.setVisibility(8);
                    this.menuItem.showSubItem(10);
                }
            }
            this.videoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    private void releasePlayer() {
        if (this.videoPlayer != null) {
            this.videoPlayer.releasePlayer();
            this.videoPlayer = null;
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (this.aspectRatioFrameLayout != null) {
            this.containerView.removeView(this.aspectRatioFrameLayout);
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        if (this.videoPlayerControlFrameLayout != null) {
            this.videoPlayerControlFrameLayout.setVisibility(8);
            this.dateTextView.setVisibility(0);
            this.nameTextView.setVisibility(0);
            if (this.allowShare) {
                this.shareButton.setVisibility(0);
                this.menuItem.hideSubItem(10);
            }
        }
    }

    private void updateCaptionTextForCurrentPhoto(Object object) {
        CharSequence caption = null;
        if (object instanceof PhotoEntry) {
            caption = ((PhotoEntry) object).caption;
        } else if (!(object instanceof BotInlineResult) && (object instanceof SearchImage)) {
            caption = ((SearchImage) object).caption;
        }
        if (caption == null || caption.length() == 0) {
            this.captionEditText.setFieldText("");
        } else {
            this.captionEditText.setFieldText(caption);
        }
    }

    private void closeCaptionEnter(boolean apply) {
        if (this.currentIndex >= 0 && this.currentIndex < this.imagesArrLocals.size()) {
            Object object = this.imagesArrLocals.get(this.currentIndex);
            if (apply) {
                if (object instanceof PhotoEntry) {
                    ((PhotoEntry) object).caption = this.captionEditText.getFieldCharSequence();
                } else if (object instanceof SearchImage) {
                    ((SearchImage) object).caption = this.captionEditText.getFieldCharSequence();
                }
                if (!(this.captionEditText.getFieldCharSequence().length() == 0 || this.placeProvider.isPhotoChecked(this.currentIndex))) {
                    this.placeProvider.setPhotoChecked(this.currentIndex);
                    this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), true);
                    updateSelectedCount();
                }
            }
            this.captionEditText.setTag(null);
            if (this.lastTitle != null) {
                this.actionBar.setTitle(this.lastTitle);
                this.lastTitle = null;
            }
            updateCaptionTextForCurrentPhoto(object);
            setCurrentCaption(this.captionEditText.getFieldCharSequence());
            if (this.captionEditText.isPopupShowing()) {
                this.captionEditText.hidePopup();
            }
            this.captionEditText.closeKeyboard();
        }
    }

    public void showAlertDialog(Builder builder) {
        if (this.parentActivity != null) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            try {
                this.visibleDialog = builder.show();
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        PhotoViewer.this.visibleDialog = null;
                    }
                });
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
    }

    private void applyCurrentEditMode() {
        Bitmap bitmap = null;
        ArrayList<InputDocument> stickers = null;
        if (this.currentEditMode == 1) {
            bitmap = this.photoCropView.getBitmap();
        } else if (this.currentEditMode == 2) {
            bitmap = this.photoFilterView.getBitmap();
        } else if (this.currentEditMode == 3) {
            bitmap = this.photoPaintView.getBitmap();
            stickers = this.photoPaintView.getMasks();
        }
        if (bitmap != null) {
            PhotoSize size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
            if (size != null) {
                PhotoEntry object = this.imagesArrLocals.get(this.currentIndex);
                if (object instanceof PhotoEntry) {
                    PhotoEntry entry = object;
                    entry.imagePath = FileLoader.getPathToAttach(size, true).toString();
                    size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), 70, false, 101, 101);
                    if (size != null) {
                        entry.thumbPath = FileLoader.getPathToAttach(size, true).toString();
                    }
                    if (stickers != null) {
                        entry.stickers.addAll(stickers);
                    }
                } else if (object instanceof SearchImage) {
                    SearchImage entry2 = (SearchImage) object;
                    entry2.imagePath = FileLoader.getPathToAttach(size, true).toString();
                    size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), 70, false, 101, 101);
                    if (size != null) {
                        entry2.thumbPath = FileLoader.getPathToAttach(size, true).toString();
                    }
                    if (stickers != null) {
                        entry2.stickers.addAll(stickers);
                    }
                }
                if (this.sendPhotoType == 0 && this.placeProvider != null) {
                    this.placeProvider.updatePhotoAtIndex(this.currentIndex);
                    if (!this.placeProvider.isPhotoChecked(this.currentIndex)) {
                        this.placeProvider.setPhotoChecked(this.currentIndex);
                        this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), true);
                        updateSelectedCount();
                    }
                }
                if (this.currentEditMode == 1) {
                    float scaleX = this.photoCropView.getRectSizeX() / ((float) getContainerViewWidth());
                    float scaleY = this.photoCropView.getRectSizeY() / ((float) getContainerViewHeight());
                    if (scaleX <= scaleY) {
                        scaleX = scaleY;
                    }
                    this.scale = scaleX;
                    this.translationX = (this.photoCropView.getRectX() + (this.photoCropView.getRectSizeX() / 2.0f)) - ((float) (getContainerViewWidth() / 2));
                    this.translationY = (this.photoCropView.getRectY() + (this.photoCropView.getRectSizeY() / 2.0f)) - ((float) (getContainerViewHeight() / 2));
                    this.zoomAnimation = true;
                    this.applying = true;
                    this.photoCropView.onDisappear();
                }
                this.centerImage.setParentView(null);
                this.centerImage.setOrientation(0, true);
                this.ignoreDidSetImage = true;
                this.centerImage.setImageBitmap(bitmap);
                this.ignoreDidSetImage = false;
                this.centerImage.setParentView(this.containerView);
            }
        }
    }

    private void switchToEditMode(int mode) {
        if (this.currentEditMode == mode || this.centerImage.getBitmap() == null || this.changeModeAnimation != null || this.imageMoveAnimation != null || this.radialProgressViews[0].backgroundState != -1 || this.captionEditText.getTag() != null) {
            return;
        }
        final int i;
        if (mode == 0) {
            if (this.currentEditMode != 2 || this.photoFilterView.getToolsView().getVisibility() == 0) {
                if (this.centerImage.getBitmap() != null) {
                    float scale;
                    float newScale;
                    int bitmapWidth = this.centerImage.getBitmapWidth();
                    int bitmapHeight = this.centerImage.getBitmapHeight();
                    float scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                    float scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    float newScaleX = ((float) getContainerViewWidth(0)) / ((float) bitmapWidth);
                    float newScaleY = ((float) getContainerViewHeight(0)) / ((float) bitmapHeight);
                    if (scaleX > scaleY) {
                        scale = scaleY;
                    } else {
                        scale = scaleX;
                    }
                    if (newScaleX > newScaleY) {
                        newScale = newScaleY;
                    } else {
                        newScale = newScaleX;
                    }
                    if (this.sendPhotoType != 1 || this.applying) {
                        this.animateToScale = newScale / scale;
                    } else {
                        float fillScale;
                        float minSide = (float) Math.min(getContainerViewWidth(), getContainerViewHeight());
                        scaleX = minSide / ((float) bitmapWidth);
                        scaleY = minSide / ((float) bitmapHeight);
                        if (scaleX > scaleY) {
                            fillScale = scaleX;
                        } else {
                            fillScale = scaleY;
                        }
                        this.scale = fillScale / scale;
                        this.animateToScale = (this.scale * newScale) / fillScale;
                    }
                    this.animateToX = 0.0f;
                    if (this.currentEditMode == 1) {
                        this.animateToY = (float) AndroidUtilities.dp(58.0f);
                    } else if (this.currentEditMode == 2) {
                        this.animateToY = (float) AndroidUtilities.dp(62.0f);
                    } else if (this.currentEditMode == 3) {
                        this.animateToY = (float) ((AndroidUtilities.dp(48.0f) - ActionBar.getCurrentActionBarHeight()) / 2);
                    }
                    if (VERSION.SDK_INT >= 21) {
                        this.animateToY -= (float) (AndroidUtilities.statusBarHeight / 2);
                    }
                    this.animationStartTime = System.currentTimeMillis();
                    this.zoomAnimation = true;
                }
                this.imageMoveAnimation = new AnimatorSet();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (this.currentEditMode == 1) {
                    animatorSet = this.imageMoveAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.editorDoneLayout, "translationY", new float[]{(float) AndroidUtilities.dp(48.0f)});
                    animatorArr[1] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.photoCropView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                } else if (this.currentEditMode == 2) {
                    this.photoFilterView.shutdown();
                    animatorSet = this.imageMoveAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.photoFilterView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(126.0f)});
                    animatorArr[1] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f});
                    animatorSet.playTogether(animatorArr);
                } else if (this.currentEditMode == 3) {
                    this.photoPaintView.shutdown();
                    AnimatorSet animatorSet2 = this.imageMoveAnimation;
                    Animator[] animatorArr2 = new Animator[4];
                    animatorArr2[0] = ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(126.0f)});
                    animatorArr2[1] = ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), "translationX", new float[]{(float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW)});
                    ActionBar actionBar = this.photoPaintView.getActionBar();
                    String str = "translationY";
                    float[] fArr = new float[1];
                    fArr[0] = (float) ((-ActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
                    animatorArr2[2] = ObjectAnimator.ofFloat(actionBar, str, fArr);
                    animatorArr2[3] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f});
                    animatorSet2.playTogether(animatorArr2);
                }
                this.imageMoveAnimation.setDuration(200);
                i = mode;
                this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (PhotoViewer.this.currentEditMode == 1) {
                            PhotoViewer.this.editorDoneLayout.setVisibility(8);
                            PhotoViewer.this.photoCropView.setVisibility(8);
                        } else if (PhotoViewer.this.currentEditMode == 2) {
                            PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoFilterView);
                            PhotoViewer.this.photoFilterView = null;
                        } else if (PhotoViewer.this.currentEditMode == 3) {
                            PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoPaintView);
                            PhotoViewer.this.photoPaintView = null;
                        }
                        PhotoViewer.this.imageMoveAnimation = null;
                        PhotoViewer.this.currentEditMode = i;
                        PhotoViewer.this.applying = false;
                        PhotoViewer.this.animateToScale = 1.0f;
                        PhotoViewer.this.animateToX = 0.0f;
                        PhotoViewer.this.animateToY = 0.0f;
                        PhotoViewer.this.scale = 1.0f;
                        PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                        PhotoViewer.this.containerView.invalidate();
                        AnimatorSet animatorSet = new AnimatorSet();
                        ArrayList<Animator> arrayList = new ArrayList();
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, "translationY", new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, "translationY", new float[]{0.0f}));
                        if (PhotoViewer.this.needCaptionLayout) {
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, "translationY", new float[]{0.0f}));
                        }
                        if (PhotoViewer.this.sendPhotoType == 0) {
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, "alpha", new float[]{1.0f}));
                        }
                        animatorSet.playTogether(arrayList);
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animation) {
                                PhotoViewer.this.pickerView.setVisibility(0);
                                PhotoViewer.this.actionBar.setVisibility(0);
                                if (PhotoViewer.this.needCaptionLayout) {
                                    PhotoViewer.this.captionTextView.setVisibility(PhotoViewer.this.captionTextView.getTag() != null ? 0 : 4);
                                }
                                if (PhotoViewer.this.sendPhotoType == 0) {
                                    PhotoViewer.this.checkImageView.setVisibility(0);
                                }
                            }
                        });
                        animatorSet.start();
                    }
                });
                this.imageMoveAnimation.start();
                return;
            }
            this.photoFilterView.switchToOrFromEditMode();
        } else if (mode == 1) {
            if (this.photoCropView == null) {
                this.photoCropView = new PhotoCropView(this.actvityContext);
                this.photoCropView.setVisibility(8);
                this.containerView.addView(this.photoCropView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                this.photoCropView.setDelegate(new PhotoCropViewDelegate() {
                    public void needMoveImageTo(float x, float y, float s, boolean animated) {
                        if (animated) {
                            PhotoViewer.this.animateTo(s, x, y, true);
                            return;
                        }
                        PhotoViewer.this.translationX = x;
                        PhotoViewer.this.translationY = y;
                        PhotoViewer.this.scale = s;
                        PhotoViewer.this.containerView.invalidate();
                    }

                    public Bitmap getBitmap() {
                        return PhotoViewer.this.centerImage.getBitmap();
                    }

                    public void onChange(boolean reset) {
                        PhotoViewer.this.resetButton.setVisibility(reset ? 8 : 0);
                    }
                });
            }
            this.photoCropView.onAppear();
            this.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", R.string.Crop));
            this.editorDoneLayout.doneButton.setTextColor(-11420173);
            this.changeModeAnimation = new AnimatorSet();
            arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            arrayList.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[]{0.0f, (float) (-this.actionBar.getHeight())}));
            if (this.needCaptionLayout) {
                arrayList.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            }
            if (this.sendPhotoType == 0) {
                arrayList.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[]{1.0f, 0.0f}));
            }
            this.changeModeAnimation.playTogether(arrayList);
            this.changeModeAnimation.setDuration(200);
            i = mode;
            this.changeModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PhotoViewer.this.changeModeAnimation = null;
                    PhotoViewer.this.pickerView.setVisibility(8);
                    if (PhotoViewer.this.needCaptionLayout) {
                        PhotoViewer.this.captionTextView.setVisibility(4);
                    }
                    if (PhotoViewer.this.sendPhotoType == 0) {
                        PhotoViewer.this.checkImageView.setVisibility(8);
                    }
                    Bitmap bitmap = PhotoViewer.this.centerImage.getBitmap();
                    if (bitmap != null) {
                        float scale;
                        float newScale;
                        PhotoViewer.this.photoCropView.setBitmap(bitmap, PhotoViewer.this.centerImage.getOrientation(), PhotoViewer.this.sendPhotoType != 1);
                        int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                        int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                        float scaleX = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                        float scaleY = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                        float newScaleX = ((float) PhotoViewer.this.getContainerViewWidth(1)) / ((float) bitmapWidth);
                        float newScaleY = ((float) PhotoViewer.this.getContainerViewHeight(1)) / ((float) bitmapHeight);
                        if (scaleX > scaleY) {
                            scale = scaleY;
                        } else {
                            scale = scaleX;
                        }
                        if (newScaleX > newScaleY) {
                            newScale = newScaleY;
                        } else {
                            newScale = newScaleX;
                        }
                        if (PhotoViewer.this.sendPhotoType == 1) {
                            float minSide = (float) Math.min(PhotoViewer.this.getContainerViewWidth(1), PhotoViewer.this.getContainerViewHeight(1));
                            newScaleX = minSide / ((float) bitmapWidth);
                            newScaleY = minSide / ((float) bitmapHeight);
                            if (newScaleX > newScaleY) {
                                newScale = newScaleX;
                            } else {
                                newScale = newScaleY;
                            }
                        }
                        PhotoViewer.this.animateToScale = newScale / scale;
                        PhotoViewer.this.animateToX = 0.0f;
                        PhotoViewer.this.animateToY = (float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0) + (-AndroidUtilities.dp(56.0f)));
                        PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                        PhotoViewer.this.zoomAnimation = true;
                    }
                    PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                    AnimatorSet access$8200 = PhotoViewer.this.imageMoveAnimation;
                    r13 = new Animator[3];
                    r13[0] = ObjectAnimator.ofFloat(PhotoViewer.this.editorDoneLayout, "translationY", new float[]{(float) AndroidUtilities.dp(48.0f), 0.0f});
                    float[] fArr = new float[2];
                    r13[1] = ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[]{0.0f, 1.0f});
                    fArr = new float[2];
                    r13[2] = ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, "alpha", new float[]{0.0f, 1.0f});
                    access$8200.playTogether(r13);
                    PhotoViewer.this.imageMoveAnimation.setDuration(200);
                    PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                            PhotoViewer.this.editorDoneLayout.setVisibility(0);
                            PhotoViewer.this.photoCropView.setVisibility(0);
                        }

                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.photoCropView.onAppeared();
                            PhotoViewer.this.imageMoveAnimation = null;
                            PhotoViewer.this.currentEditMode = i;
                            PhotoViewer.this.animateToScale = 1.0f;
                            PhotoViewer.this.animateToX = 0.0f;
                            PhotoViewer.this.animateToY = 0.0f;
                            PhotoViewer.this.scale = 1.0f;
                            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                            PhotoViewer.this.containerView.invalidate();
                        }
                    });
                    PhotoViewer.this.imageMoveAnimation.start();
                }
            });
            this.changeModeAnimation.start();
        } else if (mode == 2) {
            if (this.photoFilterView == null) {
                this.photoFilterView = new PhotoFilterView(this.parentActivity, this.centerImage.getBitmap(), this.centerImage.getOrientation());
                this.containerView.addView(this.photoFilterView, LayoutHelper.createFrame(-1, -1.0f));
                this.photoFilterView.getDoneTextView().setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PhotoViewer.this.applyCurrentEditMode();
                        PhotoViewer.this.switchToEditMode(0);
                    }
                });
                this.photoFilterView.getCancelTextView().setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (!PhotoViewer.this.photoFilterView.hasChanges()) {
                            PhotoViewer.this.switchToEditMode(0);
                        } else if (PhotoViewer.this.parentActivity != null) {
                            Builder builder = new Builder(PhotoViewer.this.parentActivity);
                            builder.setMessage(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    PhotoViewer.this.switchToEditMode(0);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            PhotoViewer.this.showAlertDialog(builder);
                        }
                    }
                });
                this.photoFilterView.getToolsView().setTranslationY((float) AndroidUtilities.dp(126.0f));
            }
            this.changeModeAnimation = new AnimatorSet();
            arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            arrayList.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[]{0.0f, (float) (-this.actionBar.getHeight())}));
            if (this.needCaptionLayout) {
                arrayList.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            }
            if (this.sendPhotoType == 0) {
                arrayList.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[]{1.0f, 0.0f}));
            }
            this.changeModeAnimation.playTogether(arrayList);
            this.changeModeAnimation.setDuration(200);
            i = mode;
            this.changeModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PhotoViewer.this.changeModeAnimation = null;
                    PhotoViewer.this.pickerView.setVisibility(8);
                    PhotoViewer.this.actionBar.setVisibility(8);
                    if (PhotoViewer.this.needCaptionLayout) {
                        PhotoViewer.this.captionTextView.setVisibility(4);
                    }
                    if (PhotoViewer.this.sendPhotoType == 0) {
                        PhotoViewer.this.checkImageView.setVisibility(8);
                    }
                    if (PhotoViewer.this.centerImage.getBitmap() != null) {
                        float scale;
                        float newScale;
                        int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                        int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                        float scaleX = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                        float scaleY = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                        float newScaleX = ((float) PhotoViewer.this.getContainerViewWidth(2)) / ((float) bitmapWidth);
                        float newScaleY = ((float) PhotoViewer.this.getContainerViewHeight(2)) / ((float) bitmapHeight);
                        if (scaleX > scaleY) {
                            scale = scaleY;
                        } else {
                            scale = scaleX;
                        }
                        if (newScaleX > newScaleY) {
                            newScale = newScaleY;
                        } else {
                            newScale = newScaleX;
                        }
                        PhotoViewer.this.animateToScale = newScale / scale;
                        PhotoViewer.this.animateToX = 0.0f;
                        PhotoViewer.this.animateToY = (float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0) + (-AndroidUtilities.dp(62.0f)));
                        PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                        PhotoViewer.this.zoomAnimation = true;
                    }
                    PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                    AnimatorSet access$8200 = PhotoViewer.this.imageMoveAnimation;
                    r12 = new Animator[2];
                    float[] fArr = new float[2];
                    r12[0] = ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[]{0.0f, 1.0f});
                    r12[1] = ObjectAnimator.ofFloat(PhotoViewer.this.photoFilterView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(126.0f), 0.0f});
                    access$8200.playTogether(r12);
                    PhotoViewer.this.imageMoveAnimation.setDuration(200);
                    PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                        }

                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.photoFilterView.init();
                            PhotoViewer.this.imageMoveAnimation = null;
                            PhotoViewer.this.currentEditMode = i;
                            PhotoViewer.this.animateToScale = 1.0f;
                            PhotoViewer.this.animateToX = 0.0f;
                            PhotoViewer.this.animateToY = 0.0f;
                            PhotoViewer.this.scale = 1.0f;
                            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                            PhotoViewer.this.containerView.invalidate();
                        }
                    });
                    PhotoViewer.this.imageMoveAnimation.start();
                }
            });
            this.changeModeAnimation.start();
        } else if (mode == 3) {
            if (this.photoPaintView == null) {
                this.photoPaintView = new PhotoPaintView(this.parentActivity, this.centerImage.getBitmap(), this.centerImage.getOrientation());
                this.containerView.addView(this.photoPaintView, LayoutHelper.createFrame(-1, -1.0f));
                this.photoPaintView.getDoneTextView().setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PhotoViewer.this.applyCurrentEditMode();
                        PhotoViewer.this.switchToEditMode(0);
                    }
                });
                this.photoPaintView.getCancelTextView().setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PhotoViewer.this.photoPaintView.maybeShowDismissalAlert(PhotoViewer.this, PhotoViewer.this.parentActivity, new Runnable() {
                            public void run() {
                                PhotoViewer.this.switchToEditMode(0);
                            }
                        });
                    }
                });
                this.photoPaintView.getColorPicker().setTranslationX((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW));
                this.photoPaintView.getToolsView().setTranslationY((float) AndroidUtilities.dp(126.0f));
                this.photoPaintView.getActionBar().setTranslationY((float) ((-ActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }
            this.changeModeAnimation = new AnimatorSet();
            arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            arrayList.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[]{0.0f, (float) (-this.actionBar.getHeight())}));
            if (this.needCaptionLayout) {
                arrayList.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            }
            if (this.sendPhotoType == 0) {
                arrayList.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[]{1.0f, 0.0f}));
            }
            this.changeModeAnimation.playTogether(arrayList);
            this.changeModeAnimation.setDuration(200);
            i = mode;
            this.changeModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PhotoViewer.this.changeModeAnimation = null;
                    PhotoViewer.this.pickerView.setVisibility(8);
                    if (PhotoViewer.this.needCaptionLayout) {
                        PhotoViewer.this.captionTextView.setVisibility(4);
                    }
                    if (PhotoViewer.this.sendPhotoType == 0) {
                        PhotoViewer.this.checkImageView.setVisibility(8);
                    }
                    if (PhotoViewer.this.centerImage.getBitmap() != null) {
                        float scale;
                        float newScale;
                        int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                        int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                        float scaleX = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                        float scaleY = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                        float newScaleX = ((float) PhotoViewer.this.getContainerViewWidth(3)) / ((float) bitmapWidth);
                        float newScaleY = ((float) PhotoViewer.this.getContainerViewHeight(3)) / ((float) bitmapHeight);
                        if (scaleX > scaleY) {
                            scale = scaleY;
                        } else {
                            scale = scaleX;
                        }
                        if (newScaleX > newScaleY) {
                            newScale = newScaleY;
                        } else {
                            newScale = newScaleX;
                        }
                        PhotoViewer.this.animateToScale = newScale / scale;
                        PhotoViewer.this.animateToX = 0.0f;
                        PhotoViewer.this.animateToY = (float) (((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f))) / 2);
                        PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                        PhotoViewer.this.zoomAnimation = true;
                    }
                    PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                    AnimatorSet access$8200 = PhotoViewer.this.imageMoveAnimation;
                    r13 = new Animator[4];
                    float[] fArr = new float[2];
                    r13[0] = ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[]{0.0f, 1.0f});
                    r13[1] = ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getColorPicker(), "translationX", new float[]{(float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW), 0.0f});
                    r13[2] = ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(126.0f), 0.0f});
                    ActionBar actionBar = PhotoViewer.this.photoPaintView.getActionBar();
                    String str = "translationY";
                    float[] fArr2 = new float[2];
                    fArr2[0] = (float) ((-ActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
                    fArr2[1] = 0.0f;
                    r13[3] = ObjectAnimator.ofFloat(actionBar, str, fArr2);
                    access$8200.playTogether(r13);
                    PhotoViewer.this.imageMoveAnimation.setDuration(200);
                    PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                        }

                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.photoPaintView.init();
                            PhotoViewer.this.imageMoveAnimation = null;
                            PhotoViewer.this.currentEditMode = i;
                            PhotoViewer.this.animateToScale = 1.0f;
                            PhotoViewer.this.animateToX = 0.0f;
                            PhotoViewer.this.animateToY = 0.0f;
                            PhotoViewer.this.scale = 1.0f;
                            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                            PhotoViewer.this.containerView.invalidate();
                        }
                    });
                    PhotoViewer.this.imageMoveAnimation.start();
                }
            });
            this.changeModeAnimation.start();
        }
    }

    private void toggleCheckImageView(boolean show) {
        float f = 1.0f;
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> arrayList = new ArrayList();
        PickerBottomLayoutViewer pickerBottomLayoutViewer = this.pickerView;
        String str = "alpha";
        float[] fArr = new float[1];
        fArr[0] = show ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(pickerBottomLayoutViewer, str, fArr));
        if (this.needCaptionLayout) {
            float f2;
            TextView textView = this.captionTextView;
            str = "alpha";
            fArr = new float[1];
            if (show) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr));
        }
        if (this.sendPhotoType == 0) {
            CheckBox checkBox = this.checkImageView;
            String str2 = "alpha";
            float[] fArr2 = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr2[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(checkBox, str2, fArr2));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void toggleActionBar(boolean show, boolean animated) {
        float f = 1.0f;
        if (show) {
            this.actionBar.setVisibility(0);
            if (this.canShowBottom) {
                this.bottomLayout.setVisibility(0);
                if (this.captionTextView.getTag() != null) {
                    this.captionTextView.setVisibility(0);
                }
            }
        }
        this.isActionBarVisible = show;
        this.actionBar.setEnabled(show);
        this.bottomLayout.setEnabled(show);
        float f2;
        if (animated) {
            ArrayList<Animator> arrayList = new ArrayList();
            ActionBar actionBar = this.actionBar;
            String str = "alpha";
            float[] fArr = new float[1];
            if (show) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, str, fArr));
            FrameLayout frameLayout = this.bottomLayout;
            str = "alpha";
            fArr = new float[1];
            if (show) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, str, fArr));
            if (this.captionTextView.getTag() != null) {
                TextView textView = this.captionTextView;
                String str2 = "alpha";
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(textView, str2, fArr2));
            }
            this.currentActionBarAnimation = new AnimatorSet();
            this.currentActionBarAnimation.playTogether(arrayList);
            if (!show) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (PhotoViewer.this.currentActionBarAnimation != null && PhotoViewer.this.currentActionBarAnimation.equals(animation)) {
                            PhotoViewer.this.actionBar.setVisibility(8);
                            if (PhotoViewer.this.canShowBottom) {
                                PhotoViewer.this.bottomLayout.setVisibility(8);
                                if (PhotoViewer.this.captionTextView.getTag() != null) {
                                    PhotoViewer.this.captionTextView.setVisibility(4);
                                }
                            }
                            PhotoViewer.this.currentActionBarAnimation = null;
                        }
                    }
                });
            }
            this.currentActionBarAnimation.setDuration(200);
            this.currentActionBarAnimation.start();
            return;
        }
        this.actionBar.setAlpha(show ? 1.0f : 0.0f);
        frameLayout = this.bottomLayout;
        if (show) {
            f2 = 1.0f;
        } else {
            f2 = 0.0f;
        }
        frameLayout.setAlpha(f2);
        if (this.captionTextView.getTag() != null) {
            textView = this.captionTextView;
            if (!show) {
                f = 0.0f;
            }
            textView.setAlpha(f);
        }
        if (!show) {
            this.actionBar.setVisibility(8);
            if (this.canShowBottom) {
                this.bottomLayout.setVisibility(8);
                if (this.captionTextView.getTag() != null) {
                    this.captionTextView.setVisibility(4);
                }
            }
        }
    }

    private String getFileName(int index) {
        if (index < 0) {
            return null;
        }
        if (this.imagesArrLocations.isEmpty() && this.imagesArr.isEmpty()) {
            if (this.imagesArrLocals.isEmpty() || index >= this.imagesArrLocals.size()) {
                return null;
            }
            SearchImage object = this.imagesArrLocals.get(index);
            if (object instanceof SearchImage) {
                SearchImage searchImage = object;
                if (searchImage.document != null) {
                    return FileLoader.getAttachFileName(searchImage.document);
                }
                if (!(searchImage.type == 1 || searchImage.localUrl == null || searchImage.localUrl.length() <= 0)) {
                    File file = new File(searchImage.localUrl);
                    if (file.exists()) {
                        return file.getName();
                    }
                    searchImage.localUrl = "";
                }
                return Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg");
            } else if (!(object instanceof BotInlineResult)) {
                return null;
            } else {
                BotInlineResult botInlineResult = (BotInlineResult) object;
                if (botInlineResult.document != null) {
                    return FileLoader.getAttachFileName(botInlineResult.document);
                }
                if (botInlineResult.photo != null) {
                    return FileLoader.getAttachFileName(FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize()));
                }
                if (botInlineResult.content_url != null) {
                    return Utilities.MD5(botInlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content_url, "jpg");
                }
                return null;
            }
        } else if (this.imagesArrLocations.isEmpty()) {
            if (this.imagesArr.isEmpty() || index >= this.imagesArr.size()) {
                return null;
            }
            return FileLoader.getMessageFileName(((MessageObject) this.imagesArr.get(index)).messageOwner);
        } else if (index >= this.imagesArrLocations.size()) {
            return null;
        } else {
            FileLocation location = (FileLocation) this.imagesArrLocations.get(index);
            return location.volume_id + "_" + location.local_id + ".jpg";
        }
    }

    private FileLocation getFileLocation(int index, int[] size) {
        if (index < 0) {
            return null;
        }
        if (this.imagesArrLocations.isEmpty()) {
            if (this.imagesArr.isEmpty() || index >= this.imagesArr.size()) {
                return null;
            }
            MessageObject message = (MessageObject) this.imagesArr.get(index);
            PhotoSize sizeFull;
            if (message.messageOwner instanceof TL_messageService) {
                if (message.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                    return message.messageOwner.action.newUserPhoto.photo_big;
                }
                sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    size[0] = sizeFull.size;
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                    return sizeFull.location;
                }
                size[0] = -1;
                return null;
            } else if (((message.messageOwner.media instanceof TL_messageMediaPhoto) && message.messageOwner.media.photo != null) || ((message.messageOwner.media instanceof TL_messageMediaWebPage) && message.messageOwner.media.webpage != null)) {
                sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    size[0] = sizeFull.size;
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                    return sizeFull.location;
                }
                size[0] = -1;
                return null;
            } else if (message.getDocument() == null || message.getDocument().thumb == null) {
                return null;
            } else {
                size[0] = message.getDocument().thumb.size;
                if (size[0] == 0) {
                    size[0] = -1;
                }
                return message.getDocument().thumb.location;
            }
        } else if (index >= this.imagesArrLocations.size()) {
            return null;
        } else {
            size[0] = ((Integer) this.imagesArrLocationsSizes.get(index)).intValue();
            return (FileLocation) this.imagesArrLocations.get(index);
        }
    }

    private void updateSelectedCount() {
        if (this.placeProvider != null) {
            this.pickerView.updateSelectedCount(this.placeProvider.getSelectedCount(), false);
        }
    }

    private void onPhotoShow(MessageObject messageObject, FileLocation fileLocation, ArrayList<MessageObject> messages, ArrayList<Object> photos, int index, PlaceProviderObject object) {
        int a;
        this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
        this.currentMessageObject = null;
        this.currentFileLocation = null;
        this.currentPathObject = null;
        this.currentBotInlineResult = null;
        this.currentIndex = -1;
        this.currentFileNames[0] = null;
        this.currentFileNames[1] = null;
        this.currentFileNames[2] = null;
        this.avatarsDialogId = 0;
        this.totalImagesCount = 0;
        this.totalImagesCountMerge = 0;
        this.currentEditMode = 0;
        this.isFirstLoading = true;
        this.needSearchImageInArr = false;
        this.loadingMoreImages = false;
        this.endReached[0] = false;
        this.endReached[1] = this.mergeDialogId == 0;
        this.opennedFromMedia = false;
        this.needCaptionLayout = false;
        this.canShowBottom = true;
        this.imagesArr.clear();
        this.imagesArrLocations.clear();
        this.imagesArrLocationsSizes.clear();
        this.avatarsArr.clear();
        this.imagesArrLocals.clear();
        for (a = 0; a < 2; a++) {
            this.imagesByIds[a].clear();
            this.imagesByIdsTemp[a].clear();
        }
        this.imagesArrTemp.clear();
        this.currentUserAvatarLocation = null;
        this.containerView.setPadding(0, 0, 0, 0);
        this.currentThumb = object != null ? object.thumb : null;
        this.menuItem.setVisibility(0);
        this.bottomLayout.setVisibility(0);
        this.bottomLayout.setTranslationY(0.0f);
        this.shareButton.setVisibility(8);
        this.allowShare = false;
        this.menuItem.hideSubItem(2);
        this.menuItem.hideSubItem(10);
        this.menuItem.hideSubItem(11);
        this.actionBar.setTranslationY(0.0f);
        this.pickerView.setTranslationY(0.0f);
        this.checkImageView.setAlpha(1.0f);
        this.pickerView.setAlpha(1.0f);
        this.checkImageView.setVisibility(8);
        this.pickerView.setVisibility(8);
        this.paintItem.setVisibility(8);
        this.cropItem.setVisibility(8);
        this.tuneItem.setVisibility(8);
        this.captionEditText.setVisibility(8);
        this.mentionListView.setVisibility(8);
        this.muteItem.setVisibility(8);
        this.masksItem.setVisibility(8);
        this.muteItemAvailable = false;
        this.muteVideo = false;
        this.muteItem.setIcon(R.drawable.volume_on);
        this.editorDoneLayout.setVisibility(8);
        this.captionTextView.setTag(null);
        this.captionTextView.setVisibility(4);
        if (this.photoCropView != null) {
            this.photoCropView.setVisibility(8);
        }
        if (this.photoFilterView != null) {
            this.photoFilterView.setVisibility(8);
        }
        for (a = 0; a < 3; a++) {
            if (this.radialProgressViews[a] != null) {
                this.radialProgressViews[a].setBackgroundState(-1, false);
            }
        }
        if (messageObject != null && messages == null) {
            this.imagesArr.add(messageObject);
            if (this.currentAnimation != null) {
                this.needSearchImageInArr = false;
            } else if (!(messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty))) {
                this.needSearchImageInArr = true;
                this.imagesByIds[0].put(Integer.valueOf(messageObject.getId()), messageObject);
                this.menuItem.showSubItem(2);
            }
            setImageIndex(0, true);
        } else if (fileLocation != null) {
            this.avatarsDialogId = object.dialogId;
            this.imagesArrLocations.add(fileLocation);
            this.imagesArrLocationsSizes.add(Integer.valueOf(object.size));
            this.avatarsArr.add(new TL_photoEmpty());
            r3 = this.shareButton;
            r2 = (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0) ? 0 : 8;
            r3.setVisibility(r2);
            this.allowShare = true;
            this.menuItem.hideSubItem(2);
            if (this.shareButton.getVisibility() == 0) {
                this.menuItem.hideSubItem(10);
            } else {
                this.menuItem.showSubItem(10);
            }
            setImageIndex(0, true);
            this.currentUserAvatarLocation = fileLocation;
        } else if (messages != null) {
            this.menuItem.showSubItem(2);
            this.opennedFromMedia = true;
            this.imagesArr.addAll(messages);
            if (!this.opennedFromMedia) {
                Collections.reverse(this.imagesArr);
                index = (this.imagesArr.size() - index) - 1;
            }
            for (a = 0; a < this.imagesArr.size(); a++) {
                MessageObject message = (MessageObject) this.imagesArr.get(a);
                this.imagesByIds[message.getDialogId() == this.currentDialogId ? 0 : 1].put(Integer.valueOf(message.getId()), message);
            }
            setImageIndex(index, true);
        } else if (photos != null) {
            if (this.sendPhotoType == 0) {
                this.checkImageView.setVisibility(0);
            }
            this.menuItem.setVisibility(8);
            this.imagesArrLocals.addAll(photos);
            setImageIndex(index, true);
            this.pickerView.setVisibility(0);
            this.bottomLayout.setVisibility(8);
            this.canShowBottom = false;
            Object obj = this.imagesArrLocals.get(index);
            if (obj instanceof PhotoEntry) {
                if (((PhotoEntry) obj).isVideo) {
                    this.cropItem.setVisibility(8);
                    this.bottomLayout.setVisibility(0);
                    this.bottomLayout.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                } else {
                    this.cropItem.setVisibility(0);
                }
            } else if (obj instanceof BotInlineResult) {
                this.cropItem.setVisibility(8);
            } else {
                r3 = this.cropItem;
                r2 = ((obj instanceof SearchImage) && ((SearchImage) obj).type == 0) ? 0 : 8;
                r3.setVisibility(r2);
            }
            if (this.parentChatActivity != null && (this.parentChatActivity.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.parentChatActivity.currentEncryptedChat.layer) >= 46)) {
                this.mentionsAdapter.setChatInfo(this.parentChatActivity.info);
                this.mentionsAdapter.setNeedUsernames(this.parentChatActivity.currentChat != null);
                this.mentionsAdapter.setNeedBotContext(false);
                boolean z = this.cropItem.getVisibility() == 0 && (this.placeProvider == null || (this.placeProvider != null && this.placeProvider.allowCaption()));
                this.needCaptionLayout = z;
                this.captionEditText.setVisibility(this.needCaptionLayout ? 0 : 8);
                if (this.captionTextView.getTag() == null && this.needCaptionLayout) {
                    this.captionTextView.setText(LocaleController.getString("AddCaption", R.string.AddCaption));
                    this.captionTextView.setTag("empty");
                    this.captionTextView.setTextColor(-NUM);
                    this.captionTextView.setVisibility(0);
                } else {
                    this.captionTextView.setTextColor(-1);
                }
                if (this.needCaptionLayout) {
                    this.captionEditText.onCreate();
                }
            }
            if (VERSION.SDK_INT >= 16) {
                this.paintItem.setVisibility(this.cropItem.getVisibility());
                this.tuneItem.setVisibility(this.cropItem.getVisibility());
            }
            updateSelectedCount();
        }
        if (this.currentAnimation == null) {
            if (this.currentDialogId != 0 && this.totalImagesCount == 0) {
                SharedMediaQuery.getMediaCount(this.currentDialogId, 0, this.classGuid, true);
                if (this.mergeDialogId != 0) {
                    SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                }
            } else if (this.avatarsDialogId != 0) {
                MessagesController.getInstance().loadDialogPhotos(this.avatarsDialogId, 0, 80, 0, true, this.classGuid);
            }
        }
        if ((this.currentMessageObject != null && this.currentMessageObject.isVideo()) || (this.currentBotInlineResult != null && (this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document)))) {
            onActionClick(false);
        } else if (!this.imagesArrLocals.isEmpty()) {
            PhotoEntry entry = this.imagesArrLocals.get(index);
            if (entry instanceof PhotoEntry) {
                PhotoEntry photoEntry = entry;
                if (photoEntry.isVideo) {
                    preparePlayer(new File(photoEntry.path), false);
                }
            }
        }
    }

    public boolean isMuteVideo() {
        return this.muteVideo;
    }

    private void setImages() {
        if (this.animationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex);
            setIndexToImage(this.rightImage, this.currentIndex + 1);
            setIndexToImage(this.leftImage, this.currentIndex - 1);
        }
    }

    private void setImageIndex(int index, boolean init) {
        if (this.currentIndex != index && this.placeProvider != null) {
            if (!init) {
                this.currentThumb = null;
            }
            this.currentFileNames[0] = getFileName(index);
            this.currentFileNames[1] = getFileName(index + 1);
            this.currentFileNames[2] = getFileName(index - 1);
            this.placeProvider.willSwitchFromPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
            int prevIndex = this.currentIndex;
            this.currentIndex = index;
            boolean isVideo = false;
            boolean sameImage = false;
            ImageView imageView;
            int i;
            if (this.imagesArr.isEmpty()) {
                if (!this.imagesArrLocations.isEmpty()) {
                    this.nameTextView.setText("");
                    this.dateTextView.setText("");
                    if (this.avatarsDialogId != UserConfig.getClientUserId() || this.avatarsArr.isEmpty()) {
                        this.menuItem.hideSubItem(6);
                    } else {
                        this.menuItem.showSubItem(6);
                    }
                    FileLocation old = this.currentFileLocation;
                    if (index < 0 || index >= this.imagesArrLocations.size()) {
                        closePhoto(false, false);
                        return;
                    }
                    this.currentFileLocation = (FileLocation) this.imagesArrLocations.get(index);
                    if (old != null && this.currentFileLocation != null && old.local_id == this.currentFileLocation.local_id && old.volume_id == this.currentFileLocation.volume_id) {
                        sameImage = true;
                    }
                    this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.imagesArrLocations.size())));
                    this.menuItem.showSubItem(1);
                    this.allowShare = true;
                    imageView = this.shareButton;
                    i = (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0) ? 0 : 8;
                    imageView.setVisibility(i);
                    if (this.shareButton.getVisibility() == 0) {
                        this.menuItem.hideSubItem(10);
                    } else {
                        this.menuItem.showSubItem(10);
                    }
                } else if (!this.imagesArrLocals.isEmpty()) {
                    if (index < 0 || index >= this.imagesArrLocals.size()) {
                        closePhoto(false, false);
                        return;
                    }
                    PhotoEntry object = this.imagesArrLocals.get(index);
                    boolean fromCamera = false;
                    CharSequence caption = null;
                    if (object instanceof PhotoEntry) {
                        PhotoEntry photoEntry = object;
                        this.currentPathObject = photoEntry.path;
                        fromCamera = photoEntry.bucketId == 0 && photoEntry.dateTaken == 0 && this.imagesArrLocals.size() == 1;
                        caption = photoEntry.caption;
                        isVideo = photoEntry.isVideo;
                    } else if (object instanceof BotInlineResult) {
                        BotInlineResult botInlineResult = (BotInlineResult) object;
                        this.currentBotInlineResult = botInlineResult;
                        if (botInlineResult.document != null) {
                            isVideo = MessageObject.isVideoDocument(botInlineResult.document);
                            this.currentPathObject = FileLoader.getPathToAttach(botInlineResult.document).getAbsolutePath();
                        } else if (botInlineResult.photo != null) {
                            this.currentPathObject = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize())).getAbsolutePath();
                        } else if (botInlineResult.content_url != null) {
                            this.currentPathObject = botInlineResult.content_url;
                            isVideo = botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO);
                        }
                    } else if (object instanceof SearchImage) {
                        SearchImage searchImage = (SearchImage) object;
                        if (searchImage.document != null) {
                            this.currentPathObject = FileLoader.getPathToAttach(searchImage.document, true).getAbsolutePath();
                        } else {
                            this.currentPathObject = searchImage.imageUrl;
                        }
                        caption = searchImage.caption;
                    }
                    if (!fromCamera) {
                        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.imagesArrLocals.size())));
                    } else if (isVideo) {
                        this.muteItemAvailable = true;
                        this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                    } else {
                        this.actionBar.setTitle(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                    }
                    if (this.sendPhotoType == 0) {
                        this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), false);
                    }
                    setCurrentCaption(caption);
                    updateCaptionTextForCurrentPhoto(object);
                }
            } else if (this.currentIndex < 0 || this.currentIndex >= this.imagesArr.size()) {
                closePhoto(false, false);
                return;
            } else {
                MessageObject newMessageObject = (MessageObject) this.imagesArr.get(this.currentIndex);
                sameImage = this.currentMessageObject != null && this.currentMessageObject.getId() == newMessageObject.getId();
                this.currentMessageObject = newMessageObject;
                isVideo = this.currentMessageObject.isVideo();
                ActionBarMenuItem actionBarMenuItem = this.masksItem;
                i = (!this.currentMessageObject.hasPhotoStickers() || ((int) this.currentMessageObject.getDialogId()) == 0) ? 4 : 0;
                actionBarMenuItem.setVisibility(i);
                if (this.currentMessageObject.canDeleteMessage(null)) {
                    this.menuItem.showSubItem(6);
                } else {
                    this.menuItem.hideSubItem(6);
                }
                if (!isVideo || VERSION.SDK_INT < 16) {
                    this.menuItem.hideSubItem(11);
                } else {
                    this.menuItem.showSubItem(11);
                }
                if (this.currentMessageObject.isFromUser()) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
                    if (user != null) {
                        this.nameTextView.setText(UserObject.getUserName(user));
                    } else {
                        this.nameTextView.setText("");
                    }
                } else {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
                    if (chat != null) {
                        this.nameTextView.setText(chat.title);
                    } else {
                        this.nameTextView.setText("");
                    }
                }
                long date = ((long) this.currentMessageObject.messageOwner.date) * 1000;
                String dateString = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(date)), LocaleController.getInstance().formatterDay.format(new Date(date)));
                if (this.currentFileNames[0] == null || !isVideo) {
                    this.dateTextView.setText(dateString);
                } else {
                    this.dateTextView.setText(String.format("%s (%s)", new Object[]{dateString, AndroidUtilities.formatFileSize((long) this.currentMessageObject.getDocument().size)}));
                }
                setCurrentCaption(this.currentMessageObject.caption);
                if (this.currentAnimation != null) {
                    this.menuItem.hideSubItem(1);
                    this.menuItem.hideSubItem(10);
                    if (!this.currentMessageObject.canDeleteMessage(null)) {
                        this.menuItem.setVisibility(8);
                    }
                    this.allowShare = true;
                    this.shareButton.setVisibility(0);
                    this.actionBar.setTitle(LocaleController.getString("AttachGif", R.string.AttachGif));
                } else {
                    if (this.totalImagesCount + this.totalImagesCountMerge == 0 || this.needSearchImageInArr) {
                        if (this.currentMessageObject.messageOwner.media instanceof TL_messageMediaWebPage) {
                            if (this.currentMessageObject.isVideo()) {
                                this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                            } else {
                                this.actionBar.setTitle(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                            }
                        }
                    } else if (this.opennedFromMedia) {
                        if (this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge && !this.loadingMoreImages && this.currentIndex > this.imagesArr.size() - 5) {
                            loadFromMaxId = this.imagesArr.isEmpty() ? 0 : ((MessageObject) this.imagesArr.get(this.imagesArr.size() - 1)).getId();
                            loadIndex = 0;
                            if (this.endReached[0] && this.mergeDialogId != 0) {
                                loadIndex = 1;
                                if (!(this.imagesArr.isEmpty() || ((MessageObject) this.imagesArr.get(this.imagesArr.size() - 1)).getDialogId() == this.mergeDialogId)) {
                                    loadFromMaxId = 0;
                                }
                            }
                            SharedMediaQuery.loadMedia(loadIndex == 0 ? this.currentDialogId : this.mergeDialogId, 0, 80, loadFromMaxId, 0, true, this.classGuid);
                            this.loadingMoreImages = true;
                        }
                        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge)));
                    } else {
                        if (this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge && !this.loadingMoreImages && this.currentIndex < 5) {
                            loadFromMaxId = this.imagesArr.isEmpty() ? 0 : ((MessageObject) this.imagesArr.get(0)).getId();
                            loadIndex = 0;
                            if (this.endReached[0] && this.mergeDialogId != 0) {
                                loadIndex = 1;
                                if (!(this.imagesArr.isEmpty() || ((MessageObject) this.imagesArr.get(0)).getDialogId() == this.mergeDialogId)) {
                                    loadFromMaxId = 0;
                                }
                            }
                            SharedMediaQuery.loadMedia(loadIndex == 0 ? this.currentDialogId : this.mergeDialogId, 0, 80, loadFromMaxId, 0, true, this.classGuid);
                            this.loadingMoreImages = true;
                        }
                        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf((((this.totalImagesCount + this.totalImagesCountMerge) - this.imagesArr.size()) + this.currentIndex) + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge)));
                    }
                    if (this.currentMessageObject.messageOwner.ttl == 0 || this.currentMessageObject.messageOwner.ttl >= 3600) {
                        this.allowShare = true;
                        this.menuItem.showSubItem(1);
                        imageView = this.shareButton;
                        i = (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0) ? 0 : 8;
                        imageView.setVisibility(i);
                        if (this.shareButton.getVisibility() == 0) {
                            this.menuItem.hideSubItem(10);
                        } else {
                            this.menuItem.showSubItem(10);
                        }
                    } else {
                        this.allowShare = false;
                        this.menuItem.hideSubItem(1);
                        this.shareButton.setVisibility(8);
                        this.menuItem.hideSubItem(10);
                    }
                }
            }
            if (this.currentPlaceObject != null) {
                if (this.animationInProgress == 0) {
                    this.currentPlaceObject.imageReceiver.setVisible(true, true);
                } else {
                    this.showAfterAnimation = this.currentPlaceObject;
                }
            }
            this.currentPlaceObject = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
            if (this.currentPlaceObject != null) {
                if (this.animationInProgress == 0) {
                    this.currentPlaceObject.imageReceiver.setVisible(false, true);
                } else {
                    this.hideAfterAnimation = this.currentPlaceObject;
                }
            }
            if (!sameImage) {
                this.draggingDown = false;
                this.translationX = 0.0f;
                this.translationY = 0.0f;
                this.scale = 1.0f;
                this.animateToX = 0.0f;
                this.animateToY = 0.0f;
                this.animateToScale = 1.0f;
                this.animationStartTime = 0;
                this.imageMoveAnimation = null;
                this.changeModeAnimation = null;
                if (this.aspectRatioFrameLayout != null) {
                    this.aspectRatioFrameLayout.setVisibility(4);
                }
                releasePlayer();
                this.pinchStartDistance = 0.0f;
                this.pinchStartScale = 1.0f;
                this.pinchCenterX = 0.0f;
                this.pinchCenterY = 0.0f;
                this.pinchStartX = 0.0f;
                this.pinchStartY = 0.0f;
                this.moveStartX = 0.0f;
                this.moveStartY = 0.0f;
                this.zooming = false;
                this.moving = false;
                this.doubleTap = false;
                this.invalidCoords = false;
                this.canDragDown = true;
                this.changingPage = false;
                this.switchImageAfterAnimation = 0;
                boolean z = (this.imagesArrLocals.isEmpty() && (this.currentFileNames[0] == null || isVideo || this.radialProgressViews[0].backgroundState == 0)) ? false : true;
                this.canZoom = z;
                updateMinMax(this.scale);
            }
            if (prevIndex == -1) {
                setImages();
                for (int a = 0; a < 3; a++) {
                    checkProgress(a, false);
                }
                return;
            }
            checkProgress(0, false);
            ImageReceiver temp;
            RadialProgressView tempProgress;
            if (prevIndex > this.currentIndex) {
                temp = this.rightImage;
                this.rightImage = this.centerImage;
                this.centerImage = this.leftImage;
                this.leftImage = temp;
                tempProgress = this.radialProgressViews[0];
                this.radialProgressViews[0] = this.radialProgressViews[2];
                this.radialProgressViews[2] = tempProgress;
                setIndexToImage(this.leftImage, this.currentIndex - 1);
                checkProgress(1, false);
                checkProgress(2, false);
            } else if (prevIndex < this.currentIndex) {
                temp = this.leftImage;
                this.leftImage = this.centerImage;
                this.centerImage = this.rightImage;
                this.rightImage = temp;
                tempProgress = this.radialProgressViews[0];
                this.radialProgressViews[0] = this.radialProgressViews[1];
                this.radialProgressViews[1] = tempProgress;
                setIndexToImage(this.rightImage, this.currentIndex + 1);
                checkProgress(1, false);
                checkProgress(2, false);
            }
        }
    }

    private void setCurrentCaption(CharSequence caption) {
        if (caption != null && caption.length() > 0) {
            this.captionTextView = this.captionTextViewOld;
            this.captionTextViewOld = this.captionTextViewNew;
            this.captionTextViewNew = this.captionTextView;
            Theme.createChatResources(null, true);
            CharSequence str = Emoji.replaceEmoji(new SpannableStringBuilder(caption.toString()), this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            this.captionTextView.setTag(str);
            try {
                this.captionTextView.setText(str);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            this.captionTextView.setTextColor(-1);
            TextView textView = this.captionTextView;
            float f = (this.bottomLayout.getVisibility() == 0 || this.pickerView.getVisibility() == 0) ? 1.0f : 0.0f;
            textView.setAlpha(f);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    int i = 4;
                    PhotoViewer.this.captionTextViewOld.setTag(null);
                    PhotoViewer.this.captionTextViewOld.setVisibility(4);
                    TextView access$1900 = PhotoViewer.this.captionTextViewNew;
                    if (PhotoViewer.this.bottomLayout.getVisibility() == 0 || PhotoViewer.this.pickerView.getVisibility() == 0) {
                        i = 0;
                    }
                    access$1900.setVisibility(i);
                }
            });
        } else if (this.needCaptionLayout) {
            try {
                this.captionTextView.setText(LocaleController.getString("AddCaption", R.string.AddCaption));
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
            this.captionTextView.setTag("empty");
            this.captionTextView.setVisibility(0);
            this.captionTextView.setTextColor(-NUM);
        } else {
            this.captionTextView.setTextColor(-1);
            this.captionTextView.setTag(null);
            this.captionTextView.setVisibility(4);
        }
    }

    private void checkProgress(int a, boolean animated) {
        if (this.currentFileNames[a] != null) {
            int index = this.currentIndex;
            if (a == 1) {
                index++;
            } else if (a == 2) {
                index--;
            }
            File f = null;
            boolean isVideo = false;
            if (this.currentMessageObject != null) {
                MessageObject messageObject = (MessageObject) this.imagesArr.get(index);
                if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                    f = new File(messageObject.messageOwner.attachPath);
                    if (!f.exists()) {
                        f = null;
                    }
                }
                if (f == null) {
                    f = FileLoader.getPathToMessage(messageObject.messageOwner);
                }
                isVideo = messageObject.isVideo();
            } else if (this.currentBotInlineResult != null) {
                BotInlineResult botInlineResult = (BotInlineResult) this.imagesArrLocals.get(index);
                if (botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(botInlineResult.document)) {
                    if (botInlineResult.document != null) {
                        f = FileLoader.getPathToAttach(botInlineResult.document);
                    } else if (botInlineResult.content_url != null) {
                        f = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(botInlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content_url, "mp4"));
                    }
                    isVideo = true;
                } else if (botInlineResult.document != null) {
                    f = new File(FileLoader.getInstance().getDirectory(3), this.currentFileNames[a]);
                } else if (botInlineResult.photo != null) {
                    f = new File(FileLoader.getInstance().getDirectory(0), this.currentFileNames[a]);
                }
                if (f == null || !f.exists()) {
                    f = new File(FileLoader.getInstance().getDirectory(4), this.currentFileNames[a]);
                }
            } else if (this.currentFileLocation != null) {
                f = FileLoader.getPathToAttach((FileLocation) this.imagesArrLocations.get(index), this.avatarsDialogId != 0);
            } else if (this.currentPathObject != null) {
                f = new File(FileLoader.getInstance().getDirectory(3), this.currentFileNames[a]);
                if (!f.exists()) {
                    f = new File(FileLoader.getInstance().getDirectory(4), this.currentFileNames[a]);
                }
            }
            if (f == null || !f.exists()) {
                if (!isVideo) {
                    this.radialProgressViews[a].setBackgroundState(0, animated);
                } else if (FileLoader.getInstance().isLoadingFile(this.currentFileNames[a])) {
                    this.radialProgressViews[a].setBackgroundState(1, false);
                } else {
                    this.radialProgressViews[a].setBackgroundState(2, false);
                }
                Float progress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[a]);
                if (progress == null) {
                    progress = Float.valueOf(0.0f);
                }
                this.radialProgressViews[a].setProgress(progress.floatValue(), false);
            } else if (isVideo) {
                this.radialProgressViews[a].setBackgroundState(3, animated);
            } else {
                this.radialProgressViews[a].setBackgroundState(-1, animated);
            }
            if (a == 0) {
                boolean z = (this.imagesArrLocals.isEmpty() && (this.currentFileNames[0] == null || isVideo || this.radialProgressViews[0].backgroundState == 0)) ? false : true;
                this.canZoom = z;
                return;
            }
            return;
        }
        this.radialProgressViews[a].setBackgroundState(-1, animated);
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int index) {
        imageReceiver.setOrientation(0, false);
        Bitmap placeHolder;
        if (this.imagesArrLocals.isEmpty()) {
            int[] size = new int[1];
            FileLocation fileLocation = getFileLocation(index, size);
            if (fileLocation != null) {
                MessageObject messageObject = null;
                if (!this.imagesArr.isEmpty()) {
                    messageObject = (MessageObject) this.imagesArr.get(index);
                }
                imageReceiver.setParentMessageObject(messageObject);
                if (messageObject != null) {
                    imageReceiver.setShouldGenerateQualityThumb(true);
                }
                PhotoSize thumbLocation;
                Drawable bitmapDrawable;
                Drawable drawable;
                if (messageObject != null && messageObject.isVideo()) {
                    imageReceiver.setNeedsQualityThumb(true);
                    if (messageObject.photoThumbs == null || messageObject.photoThumbs.isEmpty()) {
                        imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                        return;
                    }
                    placeHolder = null;
                    if (this.currentThumb != null && imageReceiver == this.centerImage) {
                        placeHolder = this.currentThumb;
                    }
                    thumbLocation = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
                    if (placeHolder != null) {
                        bitmapDrawable = new BitmapDrawable(null, placeHolder);
                    } else {
                        drawable = null;
                    }
                    imageReceiver.setImage(null, null, null, drawable, thumbLocation.location, "b", 0, null, true);
                    return;
                } else if (messageObject == null || this.currentAnimation == null) {
                    imageReceiver.setNeedsQualityThumb(false);
                    placeHolder = null;
                    if (this.currentThumb != null && imageReceiver == this.centerImage) {
                        placeHolder = this.currentThumb;
                    }
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                    thumbLocation = messageObject != null ? FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100) : null;
                    if (placeHolder != null) {
                        bitmapDrawable = new BitmapDrawable(null, placeHolder);
                    } else {
                        drawable = null;
                    }
                    imageReceiver.setImage(fileLocation, null, null, drawable, thumbLocation != null ? thumbLocation.location : null, "b", size[0], null, this.avatarsDialogId != 0);
                    return;
                } else {
                    imageReceiver.setImageBitmap(this.currentAnimation);
                    this.currentAnimation.setSecondParentView(this.containerView);
                    return;
                }
            }
            imageReceiver.setNeedsQualityThumb(false);
            imageReceiver.setParentMessageObject(null);
            if (size[0] == 0) {
                imageReceiver.setImageBitmap((Bitmap) null);
                return;
            } else {
                imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                return;
            }
        }
        imageReceiver.setParentMessageObject(null);
        if (index < 0 || index >= this.imagesArrLocals.size()) {
            imageReceiver.setImageBitmap((Bitmap) null);
            return;
        }
        PhotoEntry object = this.imagesArrLocals.get(index);
        int size2 = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
        placeHolder = null;
        if (this.currentThumb != null && imageReceiver == this.centerImage) {
            placeHolder = this.currentThumb;
        }
        if (placeHolder == null) {
            placeHolder = this.placeProvider.getThumbForPhoto(null, null, index);
        }
        String path = null;
        Document document = null;
        FileLocation photo = null;
        int imageSize = 0;
        String filter = null;
        if (object instanceof PhotoEntry) {
            PhotoEntry photoEntry = object;
            if (!photoEntry.isVideo) {
                if (photoEntry.imagePath != null) {
                    path = photoEntry.imagePath;
                } else {
                    imageReceiver.setOrientation(photoEntry.orientation, false);
                    path = photoEntry.path;
                }
                filter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)});
            }
        } else if (object instanceof BotInlineResult) {
            BotInlineResult botInlineResult = (BotInlineResult) object;
            if (botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(botInlineResult.document)) {
                if (botInlineResult.document != null) {
                    photo = botInlineResult.document.thumb.location;
                } else {
                    path = botInlineResult.thumb_url;
                }
            } else if (botInlineResult.type.equals("gif") && botInlineResult.document != null) {
                document = botInlineResult.document;
                imageSize = botInlineResult.document.size;
                filter = "d";
            } else if (botInlineResult.photo != null) {
                PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize());
                photo = sizeFull.location;
                imageSize = sizeFull.size;
                filter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)});
            } else if (botInlineResult.content_url != null) {
                if (botInlineResult.type.equals("gif")) {
                    filter = "d";
                } else {
                    filter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)});
                }
                path = botInlineResult.content_url;
            }
        } else if (object instanceof SearchImage) {
            SearchImage photoEntry2 = (SearchImage) object;
            if (photoEntry2.imagePath != null) {
                path = photoEntry2.imagePath;
            } else if (photoEntry2.document != null) {
                document = photoEntry2.document;
                imageSize = photoEntry2.document.size;
            } else {
                path = photoEntry2.imageUrl;
                imageSize = photoEntry2.size;
            }
            filter = "d";
        }
        if (document != null) {
            Drawable bitmapDrawable2;
            FileLocation fileLocation2;
            path = "d";
            if (placeHolder != null) {
                bitmapDrawable2 = new BitmapDrawable(null, placeHolder);
            } else {
                bitmapDrawable2 = null;
            }
            if (placeHolder == null) {
                fileLocation2 = document.thumb.location;
            } else {
                fileLocation2 = null;
            }
            imageReceiver.setImage(document, null, path, bitmapDrawable2, fileLocation2, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)}), imageSize, null, null);
        } else if (photo != null) {
            imageReceiver.setImage(photo, null, filter, placeHolder != null ? new BitmapDrawable(null, placeHolder) : null, null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)}), imageSize, null, false);
        } else {
            imageReceiver.setImage(path, filter, placeHolder != null ? new BitmapDrawable(null, placeHolder) : null, null, imageSize);
        }
    }

    public boolean isShowingImage(MessageObject object) {
        return (!this.isVisible || this.disableShowCheck || object == null || this.currentMessageObject == null || this.currentMessageObject.getId() != object.getId()) ? false : true;
    }

    public boolean isShowingImage(FileLocation object) {
        return this.isVisible && !this.disableShowCheck && object != null && this.currentFileLocation != null && object.local_id == this.currentFileLocation.local_id && object.volume_id == this.currentFileLocation.volume_id && object.dc_id == this.currentFileLocation.dc_id;
    }

    public boolean isShowingImage(String object) {
        return (!this.isVisible || this.disableShowCheck || object == null || this.currentPathObject == null || !object.equals(this.currentPathObject)) ? false : true;
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        this.parentChatActivity = chatActivity;
    }

    public boolean openPhoto(MessageObject messageObject, long dialogId, long mergeDialogId, PhotoViewerProvider provider) {
        return openPhoto(messageObject, null, null, null, 0, provider, null, dialogId, mergeDialogId);
    }

    public boolean openPhoto(FileLocation fileLocation, PhotoViewerProvider provider) {
        return openPhoto(null, fileLocation, null, null, 0, provider, null, 0, 0);
    }

    public boolean openPhoto(ArrayList<MessageObject> messages, int index, long dialogId, long mergeDialogId, PhotoViewerProvider provider) {
        return openPhoto((MessageObject) messages.get(index), null, messages, null, index, provider, null, dialogId, mergeDialogId);
    }

    public boolean openPhotoForSelect(ArrayList<Object> photos, int index, int type, PhotoViewerProvider provider, ChatActivity chatActivity) {
        this.sendPhotoType = type;
        if (this.pickerView != null) {
            this.pickerView.doneButton.setText(this.sendPhotoType == 1 ? LocaleController.getString("Set", R.string.Set).toUpperCase() : LocaleController.getString("Send", R.string.Send).toUpperCase());
        }
        return openPhoto(null, null, null, photos, index, provider, chatActivity, 0, 0);
    }

    private boolean checkAnimation() {
        if (this.animationInProgress != 0 && Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            if (this.animationEndRunnable != null) {
                this.animationEndRunnable.run();
                this.animationEndRunnable = null;
            }
            this.animationInProgress = 0;
        }
        if (this.animationInProgress != 0) {
            return true;
        }
        return false;
    }

    public boolean openPhoto(MessageObject messageObject, FileLocation fileLocation, ArrayList<MessageObject> messages, ArrayList<Object> photos, int index, PhotoViewerProvider provider, ChatActivity chatActivity, long dialogId, long mDialogId) {
        if (this.parentActivity == null || this.isVisible || ((provider == null && checkAnimation()) || (messageObject == null && fileLocation == null && messages == null && photos == null))) {
            return false;
        }
        final PlaceProviderObject object = provider.getPlaceForPhoto(messageObject, fileLocation, index);
        if (object == null && photos == null) {
            return false;
        }
        this.lastInsets = null;
        WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
        if (this.attachedToWindow) {
            try {
                wm.removeView(this.windowView);
            } catch (Exception e) {
            }
        }
        try {
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -NUM;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.windowLayoutParams.softInputMode = 272;
            this.windowView.setFocusable(false);
            this.containerView.setFocusable(false);
            wm.addView(this.windowView, this.windowLayoutParams);
            this.doneButtonPressed = false;
            this.parentChatActivity = chatActivity;
            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(1), Integer.valueOf(1)));
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileLoadProgressChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaCountDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogPhotosLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            this.placeProvider = provider;
            this.mergeDialogId = mDialogId;
            this.currentDialogId = dialogId;
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.isVisible = true;
            toggleActionBar(true, false);
            if (object != null) {
                float scale;
                this.disableShowCheck = true;
                this.animationInProgress = 1;
                if (messageObject != null) {
                    this.currentAnimation = object.imageReceiver.getAnimation();
                }
                onPhotoShow(messageObject, fileLocation, messages, photos, index, object);
                Rect drawRegion = object.imageReceiver.getDrawRegion();
                int orientation = object.imageReceiver.getOrientation();
                int animatedOrientation = object.imageReceiver.getAnimatedOrientation();
                if (animatedOrientation != 0) {
                    orientation = animatedOrientation;
                }
                this.animatingImageView.setVisibility(0);
                this.animatingImageView.setRadius(object.radius);
                this.animatingImageView.setOrientation(orientation);
                this.animatingImageView.setNeedRadius(object.radius != 0);
                this.animatingImageView.setImageBitmap(object.thumb);
                this.animatingImageView.setAlpha(1.0f);
                this.animatingImageView.setPivotX(0.0f);
                this.animatingImageView.setPivotY(0.0f);
                this.animatingImageView.setScaleX(object.scale);
                this.animatingImageView.setScaleY(object.scale);
                this.animatingImageView.setTranslationX(((float) object.viewX) + (((float) drawRegion.left) * object.scale));
                this.animatingImageView.setTranslationY(((float) object.viewY) + (((float) drawRegion.top) * object.scale));
                ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                layoutParams.width = drawRegion.right - drawRegion.left;
                layoutParams.height = drawRegion.bottom - drawRegion.top;
                this.animatingImageView.setLayoutParams(layoutParams);
                float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                float scaleY = ((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) / ((float) layoutParams.height);
                if (scaleX > scaleY) {
                    scale = scaleY;
                } else {
                    scale = scaleX;
                }
                float xPos = (((float) AndroidUtilities.displaySize.x) - (((float) layoutParams.width) * scale)) / 2.0f;
                float yPos = (((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) - (((float) layoutParams.height) * scale)) / 2.0f;
                int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
                int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());
                int[] coords2 = new int[2];
                object.parentView.getLocationInWindow(coords2);
                int clipTop = ((coords2[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (object.viewY + drawRegion.top)) + object.clipTopAddition;
                if (clipTop < 0) {
                    clipTop = 0;
                }
                int clipBottom = ((layoutParams.height + (object.viewY + drawRegion.top)) - ((object.parentView.getHeight() + coords2[1]) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + object.clipBottomAddition;
                if (clipBottom < 0) {
                    clipBottom = 0;
                }
                clipTop = Math.max(clipTop, clipVertical);
                clipBottom = Math.max(clipBottom, clipVertical);
                this.animationValues[0][0] = this.animatingImageView.getScaleX();
                this.animationValues[0][1] = this.animatingImageView.getScaleY();
                this.animationValues[0][2] = this.animatingImageView.getTranslationX();
                this.animationValues[0][3] = this.animatingImageView.getTranslationY();
                this.animationValues[0][4] = ((float) clipHorizontal) * object.scale;
                this.animationValues[0][5] = ((float) clipTop) * object.scale;
                this.animationValues[0][6] = ((float) clipBottom) * object.scale;
                this.animationValues[0][7] = (float) this.animatingImageView.getRadius();
                this.animationValues[1][0] = scale;
                this.animationValues[1][1] = scale;
                this.animationValues[1][2] = xPos;
                this.animationValues[1][3] = yPos;
                this.animationValues[1][4] = 0.0f;
                this.animationValues[1][5] = 0.0f;
                this.animationValues[1][6] = 0.0f;
                this.animationValues[1][7] = 0.0f;
                this.animatingImageView.setAnimationProgress(0.0f);
                this.backgroundDrawable.setAlpha(0);
                this.containerView.setAlpha(0.0f);
                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[]{0, 255}), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f, 1.0f})});
                final ArrayList<Object> arrayList = photos;
                this.animationEndRunnable = new Runnable() {
                    public void run() {
                        if (PhotoViewer.this.containerView != null && PhotoViewer.this.windowView != null) {
                            if (VERSION.SDK_INT >= 18) {
                                PhotoViewer.this.containerView.setLayerType(0, null);
                            }
                            PhotoViewer.this.animationInProgress = 0;
                            PhotoViewer.this.transitionAnimationStartTime = 0;
                            PhotoViewer.this.setImages();
                            PhotoViewer.this.containerView.invalidate();
                            PhotoViewer.this.animatingImageView.setVisibility(8);
                            if (PhotoViewer.this.showAfterAnimation != null) {
                                PhotoViewer.this.showAfterAnimation.imageReceiver.setVisible(true, true);
                            }
                            if (PhotoViewer.this.hideAfterAnimation != null) {
                                PhotoViewer.this.hideAfterAnimation.imageReceiver.setVisible(false, true);
                            }
                            if (arrayList != null && PhotoViewer.this.sendPhotoType != 3) {
                                if (VERSION.SDK_INT >= 21) {
                                    PhotoViewer.this.windowLayoutParams.flags = -NUM;
                                } else {
                                    PhotoViewer.this.windowLayoutParams.flags = 0;
                                }
                                PhotoViewer.this.windowLayoutParams.softInputMode = 272;
                                ((WindowManager) PhotoViewer.this.parentActivity.getSystemService("window")).updateViewLayout(PhotoViewer.this.windowView, PhotoViewer.this.windowLayoutParams);
                                PhotoViewer.this.windowView.setFocusable(true);
                                PhotoViewer.this.containerView.setFocusable(true);
                            }
                        }
                    }
                };
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance().setAnimationInProgress(false);
                                if (PhotoViewer.this.animationEndRunnable != null) {
                                    PhotoViewer.this.animationEndRunnable.run();
                                    PhotoViewer.this.animationEndRunnable = null;
                                }
                            }
                        });
                    }
                });
                this.transitionAnimationStartTime = System.currentTimeMillis();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded, NotificationCenter.mediaDidLoaded, NotificationCenter.dialogPhotosLoaded});
                        NotificationCenter.getInstance().setAnimationInProgress(true);
                        animatorSet.start();
                    }
                });
                if (VERSION.SDK_INT >= 18) {
                    this.containerView.setLayerType(2, null);
                }
                this.backgroundDrawable.drawRunnable = new Runnable() {
                    public void run() {
                        PhotoViewer.this.disableShowCheck = false;
                        object.imageReceiver.setVisible(false, true);
                    }
                };
            } else {
                if (!(photos == null || this.sendPhotoType == 3)) {
                    if (VERSION.SDK_INT >= 21) {
                        this.windowLayoutParams.flags = -NUM;
                    } else {
                        this.windowLayoutParams.flags = 0;
                    }
                    this.windowLayoutParams.softInputMode = 272;
                    wm.updateViewLayout(this.windowView, this.windowLayoutParams);
                    this.windowView.setFocusable(true);
                    this.containerView.setFocusable(true);
                }
                this.backgroundDrawable.setAlpha(255);
                this.containerView.setAlpha(1.0f);
                onPhotoShow(messageObject, fileLocation, messages, photos, index, object);
            }
            return true;
        } catch (Throwable e2) {
            FileLog.e(e2);
            return false;
        }
    }

    public void closePhoto(boolean animated, boolean fromEditMode) {
        if (fromEditMode || this.currentEditMode == 0) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (this.currentEditMode != 0) {
                if (this.currentEditMode == 2) {
                    this.photoFilterView.shutdown();
                    this.containerView.removeView(this.photoFilterView);
                    this.photoFilterView = null;
                } else if (this.currentEditMode == 1) {
                    this.editorDoneLayout.setVisibility(8);
                    this.photoCropView.setVisibility(8);
                }
                this.currentEditMode = 0;
            }
            if (this.parentActivity != null && this.isVisible && !checkAnimation() && this.placeProvider != null) {
                if (!this.captionEditText.hideActionMode() || fromEditMode) {
                    releasePlayer();
                    this.captionEditText.onDestroy();
                    this.parentChatActivity = null;
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileLoadProgressChanged);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaCountDidLoaded);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaDidLoaded);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogPhotosLoaded);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
                    ConnectionsManager.getInstance().cancelRequestsForGuid(this.classGuid);
                    this.isActionBarVisible = false;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                    ConnectionsManager.getInstance().cancelRequestsForGuid(this.classGuid);
                    PlaceProviderObject object = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
                    AnimatorSet animatorSet;
                    Animator[] animatorArr;
                    final PlaceProviderObject placeProviderObject;
                    if (animated) {
                        float scale2;
                        this.animationInProgress = 1;
                        this.animatingImageView.setVisibility(0);
                        this.containerView.invalidate();
                        animatorSet = new AnimatorSet();
                        ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                        Rect drawRegion = null;
                        int orientation = this.centerImage.getOrientation();
                        int animatedOrientation = 0;
                        if (!(object == null || object.imageReceiver == null)) {
                            animatedOrientation = object.imageReceiver.getAnimatedOrientation();
                        }
                        if (animatedOrientation != 0) {
                            orientation = animatedOrientation;
                        }
                        this.animatingImageView.setOrientation(orientation);
                        if (object != null) {
                            this.animatingImageView.setNeedRadius(object.radius != 0);
                            drawRegion = object.imageReceiver.getDrawRegion();
                            layoutParams.width = drawRegion.right - drawRegion.left;
                            layoutParams.height = drawRegion.bottom - drawRegion.top;
                            this.animatingImageView.setImageBitmap(object.thumb);
                        } else {
                            this.animatingImageView.setNeedRadius(false);
                            layoutParams.width = this.centerImage.getImageWidth();
                            layoutParams.height = this.centerImage.getImageHeight();
                            this.animatingImageView.setImageBitmap(this.centerImage.getBitmap());
                        }
                        this.animatingImageView.setLayoutParams(layoutParams);
                        float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                        float scaleY = ((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) / ((float) layoutParams.height);
                        if (scaleX > scaleY) {
                            scale2 = scaleY;
                        } else {
                            scale2 = scaleX;
                        }
                        float yPos = (((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) - ((((float) layoutParams.height) * this.scale) * scale2)) / 2.0f;
                        this.animatingImageView.setTranslationX(this.translationX + ((((float) AndroidUtilities.displaySize.x) - ((((float) layoutParams.width) * this.scale) * scale2)) / 2.0f));
                        this.animatingImageView.setTranslationY(this.translationY + yPos);
                        this.animatingImageView.setScaleX(this.scale * scale2);
                        this.animatingImageView.setScaleY(this.scale * scale2);
                        if (object != null) {
                            object.imageReceiver.setVisible(false, true);
                            int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
                            int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());
                            int[] coords2 = new int[2];
                            object.parentView.getLocationInWindow(coords2);
                            int clipTop = ((coords2[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (object.viewY + drawRegion.top)) + object.clipTopAddition;
                            if (clipTop < 0) {
                                clipTop = 0;
                            }
                            int clipBottom = (((drawRegion.bottom - drawRegion.top) + (object.viewY + drawRegion.top)) - ((object.parentView.getHeight() + coords2[1]) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + object.clipBottomAddition;
                            if (clipBottom < 0) {
                                clipBottom = 0;
                            }
                            clipTop = Math.max(clipTop, clipVertical);
                            clipBottom = Math.max(clipBottom, clipVertical);
                            this.animationValues[0][0] = this.animatingImageView.getScaleX();
                            this.animationValues[0][1] = this.animatingImageView.getScaleY();
                            this.animationValues[0][2] = this.animatingImageView.getTranslationX();
                            this.animationValues[0][3] = this.animatingImageView.getTranslationY();
                            this.animationValues[0][4] = 0.0f;
                            this.animationValues[0][5] = 0.0f;
                            this.animationValues[0][6] = 0.0f;
                            this.animationValues[0][7] = 0.0f;
                            this.animationValues[1][0] = object.scale;
                            this.animationValues[1][1] = object.scale;
                            this.animationValues[1][2] = ((float) object.viewX) + (((float) drawRegion.left) * object.scale);
                            this.animationValues[1][3] = ((float) object.viewY) + (((float) drawRegion.top) * object.scale);
                            this.animationValues[1][4] = ((float) clipHorizontal) * object.scale;
                            this.animationValues[1][5] = ((float) clipTop) * object.scale;
                            this.animationValues[1][6] = ((float) clipBottom) * object.scale;
                            this.animationValues[1][7] = (float) object.radius;
                            animatorArr = new Animator[3];
                            float[] fArr = new float[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f});
                            animatorArr[1] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[]{0});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            float f;
                            int h = AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                            Animator[] animatorArr2 = new Animator[4];
                            animatorArr2[0] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[]{0});
                            animatorArr2[1] = ObjectAnimator.ofFloat(this.animatingImageView, "alpha", new float[]{0.0f});
                            ClippingImageView clippingImageView = this.animatingImageView;
                            String str = "translationY";
                            float[] fArr2 = new float[1];
                            if (this.translationY >= 0.0f) {
                                f = (float) h;
                            } else {
                                f = (float) (-h);
                            }
                            fArr2[0] = f;
                            animatorArr2[2] = ObjectAnimator.ofFloat(clippingImageView, str, fArr2);
                            animatorArr2[3] = ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr2);
                        }
                        placeProviderObject = object;
                        this.animationEndRunnable = new Runnable() {
                            public void run() {
                                if (VERSION.SDK_INT >= 18) {
                                    PhotoViewer.this.containerView.setLayerType(0, null);
                                }
                                PhotoViewer.this.animationInProgress = 0;
                                PhotoViewer.this.onPhotoClosed(placeProviderObject);
                            }
                        };
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (PhotoViewer.this.animationEndRunnable != null) {
                                            PhotoViewer.this.animationEndRunnable.run();
                                            PhotoViewer.this.animationEndRunnable = null;
                                        }
                                    }
                                });
                            }
                        });
                        this.transitionAnimationStartTime = System.currentTimeMillis();
                        if (VERSION.SDK_INT >= 18) {
                            this.containerView.setLayerType(2, null);
                        }
                        animatorSet.start();
                    } else {
                        animatorSet = new AnimatorSet();
                        animatorArr = new Animator[4];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "scaleX", new float[]{0.9f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.containerView, "scaleY", new float[]{0.9f});
                        animatorArr[2] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[]{0});
                        animatorArr[3] = ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                        this.animationInProgress = 2;
                        placeProviderObject = object;
                        this.animationEndRunnable = new Runnable() {
                            public void run() {
                                if (PhotoViewer.this.containerView != null) {
                                    if (VERSION.SDK_INT >= 18) {
                                        PhotoViewer.this.containerView.setLayerType(0, null);
                                    }
                                    PhotoViewer.this.animationInProgress = 0;
                                    PhotoViewer.this.onPhotoClosed(placeProviderObject);
                                    PhotoViewer.this.containerView.setScaleX(1.0f);
                                    PhotoViewer.this.containerView.setScaleY(1.0f);
                                }
                            }
                        };
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (PhotoViewer.this.animationEndRunnable != null) {
                                    PhotoViewer.this.animationEndRunnable.run();
                                    PhotoViewer.this.animationEndRunnable = null;
                                }
                            }
                        });
                        this.transitionAnimationStartTime = System.currentTimeMillis();
                        if (VERSION.SDK_INT >= 18) {
                            this.containerView.setLayerType(2, null);
                        }
                        animatorSet.start();
                    }
                    if (this.currentAnimation != null) {
                        this.currentAnimation.setSecondParentView(null);
                        this.currentAnimation = null;
                        this.centerImage.setImageBitmap((Drawable) null);
                    }
                    if (this.placeProvider instanceof EmptyPhotoViewerProvider) {
                        this.placeProvider.cancelButtonPressed();
                    }
                }
            }
        } else if (this.currentEditMode != 3 || this.photoPaintView == null) {
            if (this.currentEditMode == 1) {
                this.photoCropView.cancelAnimationRunnable();
            }
            switchToEditMode(0);
        } else {
            this.photoPaintView.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() {
                public void run() {
                    PhotoViewer.this.switchToEditMode(0);
                }
            });
        }
    }

    public void destroyPhotoViewer() {
        if (this.parentActivity != null && this.windowView != null) {
            releasePlayer();
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (this.captionEditText != null) {
                this.captionEditText.onDestroy();
            }
            Instance = null;
        }
    }

    private void onPhotoClosed(PlaceProviderObject object) {
        this.isVisible = false;
        this.disableShowCheck = true;
        this.currentMessageObject = null;
        this.currentBotInlineResult = null;
        this.currentFileLocation = null;
        this.currentPathObject = null;
        this.currentThumb = null;
        this.parentAlert = null;
        if (this.currentAnimation != null) {
            this.currentAnimation.setSecondParentView(null);
            this.currentAnimation = null;
        }
        for (int a = 0; a < 3; a++) {
            if (this.radialProgressViews[a] != null) {
                this.radialProgressViews[a].setBackgroundState(-1, false);
            }
        }
        this.centerImage.setImageBitmap((Bitmap) null);
        this.leftImage.setImageBitmap((Bitmap) null);
        this.rightImage.setImageBitmap((Bitmap) null);
        this.containerView.post(new Runnable() {
            public void run() {
                PhotoViewer.this.animatingImageView.setImageBitmap(null);
                try {
                    if (PhotoViewer.this.windowView.getParent() != null) {
                        ((WindowManager) PhotoViewer.this.parentActivity.getSystemService("window")).removeView(PhotoViewer.this.windowView);
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        });
        if (this.placeProvider != null) {
            this.placeProvider.willHidePhotoViewer();
        }
        this.placeProvider = null;
        this.disableShowCheck = false;
        if (object != null) {
            object.imageReceiver.setVisible(true, true);
        }
    }

    private void redraw(final int count) {
        if (count < 6 && this.containerView != null) {
            this.containerView.invalidate();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    PhotoViewer.this.redraw(count + 1);
                }
            }, 100);
        }
    }

    public void onResume() {
        redraw(0);
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false, false);
        } else if (this.lastTitle != null) {
            closeCaptionEnter(true);
        }
    }

    public boolean isVisible() {
        return this.isVisible && this.placeProvider != null;
    }

    private void updateMinMax(float scale) {
        int maxW = ((int) ((((float) this.centerImage.getImageWidth()) * scale) - ((float) getContainerViewWidth()))) / 2;
        int maxH = ((int) ((((float) this.centerImage.getImageHeight()) * scale) - ((float) getContainerViewHeight()))) / 2;
        if (maxW > 0) {
            this.minX = (float) (-maxW);
            this.maxX = (float) maxW;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (maxH > 0) {
            this.minY = (float) (-maxH);
            this.maxY = (float) maxH;
        } else {
            this.maxY = 0.0f;
            this.minY = 0.0f;
        }
        if (this.currentEditMode == 1) {
            this.maxX += this.photoCropView.getLimitX();
            this.maxY += this.photoCropView.getLimitY();
            this.minX -= this.photoCropView.getLimitWidth();
            this.minY -= this.photoCropView.getLimitHeight();
        }
    }

    private int getAdditionX() {
        if (this.currentEditMode == 0 || this.currentEditMode == 3) {
            return 0;
        }
        return AndroidUtilities.dp(14.0f);
    }

    private int getAdditionY() {
        int i = 0;
        int currentActionBarHeight;
        if (this.currentEditMode == 3) {
            currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
            if (VERSION.SDK_INT >= 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            return i + currentActionBarHeight;
        } else if (this.currentEditMode == 0) {
            return 0;
        } else {
            currentActionBarHeight = AndroidUtilities.dp(14.0f);
            if (VERSION.SDK_INT >= 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            return i + currentActionBarHeight;
        }
    }

    private int getContainerViewWidth() {
        return getContainerViewWidth(this.currentEditMode);
    }

    private int getContainerViewWidth(int mode) {
        int width = this.containerView.getWidth();
        if (mode == 0 || mode == 3) {
            return width;
        }
        return width - AndroidUtilities.dp(28.0f);
    }

    private int getContainerViewHeight() {
        return getContainerViewHeight(this.currentEditMode);
    }

    private int getContainerViewHeight(int mode) {
        int height = AndroidUtilities.displaySize.y;
        if (mode == 0 && VERSION.SDK_INT >= 21) {
            height += AndroidUtilities.statusBarHeight;
        }
        if (mode == 1) {
            return height - AndroidUtilities.dp(144.0f);
        }
        if (mode == 2) {
            return height - AndroidUtilities.dp(154.0f);
        }
        if (mode == 3) {
            return height - (AndroidUtilities.dp(48.0f) + ActionBar.getCurrentActionBarHeight());
        }
        return height;
    }

    private boolean onTouchEvent(MotionEvent ev) {
        if (this.animationInProgress != 0 || this.animationStartTime != 0) {
            return false;
        }
        if (this.currentEditMode == 2) {
            this.photoFilterView.onTouch(ev);
            return true;
        } else if (this.currentEditMode == 1) {
            return true;
        } else {
            if (this.captionEditText.isPopupShowing() || this.captionEditText.isKeyboardVisible()) {
                if (ev.getAction() == 1) {
                    closeCaptionEnter(true);
                }
                return true;
            } else if (this.currentEditMode == 0 && ev.getPointerCount() == 1 && this.gestureDetector.onTouchEvent(ev) && this.doubleTap) {
                this.doubleTap = false;
                this.moving = false;
                this.zooming = false;
                checkMinMax(false);
                return true;
            } else {
                if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
                    if (this.currentEditMode == 1) {
                        this.photoCropView.cancelAnimationRunnable();
                    }
                    this.discardTap = false;
                    if (!this.scroller.isFinished()) {
                        this.scroller.abortAnimation();
                    }
                    if (!(this.draggingDown || this.changingPage)) {
                        if (this.canZoom && ev.getPointerCount() == 2) {
                            this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)));
                            this.pinchStartScale = this.scale;
                            this.pinchCenterX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                            this.pinchCenterY = (ev.getY(0) + ev.getY(1)) / 2.0f;
                            this.pinchStartX = this.translationX;
                            this.pinchStartY = this.translationY;
                            this.zooming = true;
                            this.moving = false;
                            if (this.velocityTracker != null) {
                                this.velocityTracker.clear();
                            }
                        } else if (ev.getPointerCount() == 1) {
                            this.moveStartX = ev.getX();
                            float y = ev.getY();
                            this.moveStartY = y;
                            this.dragY = y;
                            this.draggingDown = false;
                            this.canDragDown = true;
                            if (this.velocityTracker != null) {
                                this.velocityTracker.clear();
                            }
                        }
                    }
                } else if (ev.getActionMasked() == 2) {
                    if (this.currentEditMode == 1) {
                        this.photoCropView.cancelAnimationRunnable();
                    }
                    if (this.canZoom && ev.getPointerCount() == 2 && !this.draggingDown && this.zooming && !this.changingPage) {
                        this.discardTap = true;
                        this.scale = (((float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)))) / this.pinchStartDistance) * this.pinchStartScale;
                        this.translationX = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                        this.translationY = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (this.scale / this.pinchStartScale));
                        updateMinMax(this.scale);
                        this.containerView.invalidate();
                    } else if (ev.getPointerCount() == 1) {
                        if (this.velocityTracker != null) {
                            this.velocityTracker.addMovement(ev);
                        }
                        float dx = Math.abs(ev.getX() - this.moveStartX);
                        float dy = Math.abs(ev.getY() - this.dragY);
                        if (dx > ((float) AndroidUtilities.dp(3.0f)) || dy > ((float) AndroidUtilities.dp(3.0f))) {
                            this.discardTap = true;
                        }
                        if (!(this.placeProvider instanceof EmptyPhotoViewerProvider) && this.currentEditMode == 0 && this.canDragDown && !this.draggingDown && this.scale == 1.0f && dy >= ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) && dy / 2.0f > dx) {
                            this.draggingDown = true;
                            this.moving = false;
                            this.dragY = ev.getY();
                            if (this.isActionBarVisible && this.canShowBottom) {
                                toggleActionBar(false, true);
                            } else if (this.pickerView.getVisibility() == 0) {
                                toggleActionBar(false, true);
                                toggleCheckImageView(false);
                            }
                            return true;
                        } else if (this.draggingDown) {
                            this.translationY = ev.getY() - this.dragY;
                            this.containerView.invalidate();
                        } else if (this.invalidCoords || this.animationStartTime != 0) {
                            this.invalidCoords = false;
                            this.moveStartX = ev.getX();
                            this.moveStartY = ev.getY();
                        } else {
                            float moveDx = this.moveStartX - ev.getX();
                            float moveDy = this.moveStartY - ev.getY();
                            if (this.moving || this.currentEditMode != 0 || ((this.scale == 1.0f && Math.abs(moveDy) + ((float) AndroidUtilities.dp(12.0f)) < Math.abs(moveDx)) || this.scale != 1.0f)) {
                                if (!this.moving) {
                                    moveDx = 0.0f;
                                    moveDy = 0.0f;
                                    this.moving = true;
                                    this.canDragDown = false;
                                }
                                this.moveStartX = ev.getX();
                                this.moveStartY = ev.getY();
                                updateMinMax(this.scale);
                                if ((this.translationX < this.minX && !(this.currentEditMode == 0 && this.rightImage.hasImage())) || (this.translationX > this.maxX && !(this.currentEditMode == 0 && this.leftImage.hasImage()))) {
                                    moveDx /= 3.0f;
                                }
                                if (this.maxY == 0.0f && this.minY == 0.0f && this.currentEditMode == 0) {
                                    if (this.translationY - moveDy < this.minY) {
                                        this.translationY = this.minY;
                                        moveDy = 0.0f;
                                    } else if (this.translationY - moveDy > this.maxY) {
                                        this.translationY = this.maxY;
                                        moveDy = 0.0f;
                                    }
                                } else if (this.translationY < this.minY || this.translationY > this.maxY) {
                                    moveDy /= 3.0f;
                                }
                                this.translationX -= moveDx;
                                if (!(this.scale == 1.0f && this.currentEditMode == 0)) {
                                    this.translationY -= moveDy;
                                }
                                this.containerView.invalidate();
                            }
                        }
                    }
                } else if (ev.getActionMasked() == 3 || ev.getActionMasked() == 1 || ev.getActionMasked() == 6) {
                    if (this.currentEditMode == 1) {
                        this.photoCropView.startAnimationRunnable();
                    }
                    if (this.zooming) {
                        this.invalidCoords = true;
                        if (this.scale < 1.0f) {
                            updateMinMax(1.0f);
                            animateTo(1.0f, 0.0f, 0.0f, true);
                        } else if (this.scale > 3.0f) {
                            float atx = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                            float aty = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                            updateMinMax(3.0f);
                            if (atx < this.minX) {
                                atx = this.minX;
                            } else if (atx > this.maxX) {
                                atx = this.maxX;
                            }
                            if (aty < this.minY) {
                                aty = this.minY;
                            } else if (aty > this.maxY) {
                                aty = this.maxY;
                            }
                            animateTo(3.0f, atx, aty, true);
                        } else {
                            checkMinMax(true);
                        }
                        this.zooming = false;
                    } else if (this.draggingDown) {
                        if (Math.abs(this.dragY - ev.getY()) > ((float) getContainerViewHeight()) / 6.0f) {
                            closePhoto(true, false);
                        } else {
                            if (this.pickerView.getVisibility() == 0) {
                                toggleActionBar(true, true);
                                toggleCheckImageView(true);
                            }
                            animateTo(1.0f, 0.0f, 0.0f, false);
                        }
                        this.draggingDown = false;
                    } else if (this.moving) {
                        float moveToX = this.translationX;
                        float moveToY = this.translationY;
                        updateMinMax(this.scale);
                        this.moving = false;
                        this.canDragDown = true;
                        float velocity = 0.0f;
                        if (this.velocityTracker != null && this.scale == 1.0f) {
                            this.velocityTracker.computeCurrentVelocity(1000);
                            velocity = this.velocityTracker.getXVelocity();
                        }
                        if (this.currentEditMode == 0) {
                            if ((this.translationX < this.minX - ((float) (getContainerViewWidth() / 3)) || velocity < ((float) (-AndroidUtilities.dp(650.0f)))) && this.rightImage.hasImage()) {
                                goToNext();
                                return true;
                            } else if ((this.translationX > this.maxX + ((float) (getContainerViewWidth() / 3)) || velocity > ((float) AndroidUtilities.dp(650.0f))) && this.leftImage.hasImage()) {
                                goToPrev();
                                return true;
                            }
                        }
                        if (this.translationX < this.minX) {
                            moveToX = this.minX;
                        } else if (this.translationX > this.maxX) {
                            moveToX = this.maxX;
                        }
                        if (this.translationY < this.minY) {
                            moveToY = this.minY;
                        } else if (this.translationY > this.maxY) {
                            moveToY = this.maxY;
                        }
                        animateTo(this.scale, moveToX, moveToY, false);
                    }
                }
                return false;
            }
        }
    }

    private void checkMinMax(boolean zoom) {
        float moveToX = this.translationX;
        float moveToY = this.translationY;
        updateMinMax(this.scale);
        if (this.translationX < this.minX) {
            moveToX = this.minX;
        } else if (this.translationX > this.maxX) {
            moveToX = this.maxX;
        }
        if (this.translationY < this.minY) {
            moveToY = this.minY;
        } else if (this.translationY > this.maxY) {
            moveToY = this.maxY;
        }
        animateTo(this.scale, moveToX, moveToY, zoom);
    }

    private void goToNext() {
        float extra = 0.0f;
        if (this.scale != 1.0f) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale;
        }
        this.switchImageAfterAnimation = 1;
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - extra) - ((float) (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / 2)), this.translationY, false);
    }

    private void goToPrev() {
        float extra = 0.0f;
        if (this.scale != 1.0f) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale;
        }
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, ((this.maxX + ((float) getContainerViewWidth())) + extra) + ((float) (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / 2)), this.translationY, false);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom) {
        animateTo(newScale, newTx, newTy, isZoom, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom, int duration) {
        if (this.scale != newScale || this.translationX != newTx || this.translationY != newTy) {
            this.zoomAnimation = isZoom;
            this.animateToScale = newScale;
            this.animateToX = newTx;
            this.animateToY = newTy;
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f})});
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) duration);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PhotoViewer.this.imageMoveAnimation = null;
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    public void setAnimationValue(float value) {
        this.animationValue = value;
        this.containerView.invalidate();
    }

    public float getAnimationValue() {
        return this.animationValue;
    }

    @SuppressLint({"NewApi"})
    private void onDraw(Canvas canvas) {
        if (this.animationInProgress == 1) {
            return;
        }
        if (this.isVisible || this.animationInProgress == 2) {
            float currentScale;
            float currentTranslationY;
            float currentTranslationX;
            float scaleDiff;
            float alpha;
            int bitmapWidth;
            int bitmapHeight;
            float scaleX;
            float scaleY;
            float scale;
            int width;
            int height;
            float aty = -1.0f;
            if (this.imageMoveAnimation != null) {
                if (!this.scroller.isFinished()) {
                    this.scroller.abortAnimation();
                }
                float ts = this.scale + ((this.animateToScale - this.scale) * this.animationValue);
                float tx = this.translationX + ((this.animateToX - this.translationX) * this.animationValue);
                float ty = this.translationY + ((this.animateToY - this.translationY) * this.animationValue);
                if (this.currentEditMode == 1) {
                    this.photoCropView.setAnimationProgress(this.animationValue);
                }
                if (this.animateToScale == 1.0f && this.scale == 1.0f && this.translationX == 0.0f) {
                    aty = ty;
                }
                currentScale = ts;
                currentTranslationY = ty;
                currentTranslationX = tx;
                this.containerView.invalidate();
            } else {
                if (this.animationStartTime != 0) {
                    this.translationX = this.animateToX;
                    this.translationY = this.animateToY;
                    this.scale = this.animateToScale;
                    this.animationStartTime = 0;
                    if (this.currentEditMode == 1) {
                        this.photoCropView.setAnimationProgress(1.0f);
                    }
                    updateMinMax(this.scale);
                    this.zoomAnimation = false;
                }
                if (!this.scroller.isFinished() && this.scroller.computeScrollOffset()) {
                    if (((float) this.scroller.getStartX()) < this.maxX && ((float) this.scroller.getStartX()) > this.minX) {
                        this.translationX = (float) this.scroller.getCurrX();
                    }
                    if (((float) this.scroller.getStartY()) < this.maxY && ((float) this.scroller.getStartY()) > this.minY) {
                        this.translationY = (float) this.scroller.getCurrY();
                    }
                    this.containerView.invalidate();
                }
                if (this.switchImageAfterAnimation != 0) {
                    if (this.switchImageAfterAnimation == 1) {
                        setImageIndex(this.currentIndex + 1, false);
                    } else if (this.switchImageAfterAnimation == 2) {
                        setImageIndex(this.currentIndex - 1, false);
                    }
                    this.switchImageAfterAnimation = 0;
                }
                currentScale = this.scale;
                currentTranslationY = this.translationY;
                currentTranslationX = this.translationX;
                if (!this.moving) {
                    aty = this.translationY;
                }
            }
            if (this.currentEditMode != 0 || this.scale != 1.0f || aty == -1.0f || this.zoomAnimation) {
                this.backgroundDrawable.setAlpha(255);
            } else {
                float maxValue = ((float) getContainerViewHeight()) / 4.0f;
                this.backgroundDrawable.setAlpha((int) Math.max(127.0f, 255.0f * (1.0f - (Math.min(Math.abs(aty), maxValue) / maxValue))));
            }
            ImageReceiver sideImage = null;
            if (this.currentEditMode == 0) {
                if (!(this.scale < 1.0f || this.zoomAnimation || this.zooming)) {
                    if (currentTranslationX > this.maxX + ((float) AndroidUtilities.dp(5.0f))) {
                        sideImage = this.leftImage;
                    } else if (currentTranslationX < this.minX - ((float) AndroidUtilities.dp(5.0f))) {
                        sideImage = this.rightImage;
                    }
                }
                this.changingPage = sideImage != null;
            }
            if (sideImage == this.rightImage) {
                float tranlateX = currentTranslationX;
                scaleDiff = 0.0f;
                alpha = 1.0f;
                if (!this.zoomAnimation && tranlateX < this.minX) {
                    alpha = Math.min(1.0f, (this.minX - tranlateX) / ((float) canvas.getWidth()));
                    scaleDiff = (1.0f - alpha) * 0.3f;
                    tranlateX = (float) ((-canvas.getWidth()) - (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / 2));
                }
                if (sideImage.hasBitmapImage()) {
                    canvas.save();
                    canvas.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                    canvas.translate(((float) (canvas.getWidth() + (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / 2))) + tranlateX, 0.0f);
                    canvas.scale(1.0f - scaleDiff, 1.0f - scaleDiff);
                    bitmapWidth = sideImage.getBitmapWidth();
                    bitmapHeight = sideImage.getBitmapHeight();
                    scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                    scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    if (scaleX > scaleY) {
                        scale = scaleY;
                    } else {
                        scale = scaleX;
                    }
                    width = (int) (((float) bitmapWidth) * scale);
                    height = (int) (((float) bitmapHeight) * scale);
                    sideImage.setAlpha(alpha);
                    sideImage.setImageCoords((-width) / 2, (-height) / 2, width, height);
                    sideImage.draw(canvas);
                    canvas.restore();
                }
                canvas.save();
                canvas.translate(tranlateX, currentTranslationY / currentScale);
                canvas.translate(((((float) canvas.getWidth()) * (this.scale + 1.0f)) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE))) / 2.0f, (-currentTranslationY) / currentScale);
                this.radialProgressViews[1].setScale(1.0f - scaleDiff);
                this.radialProgressViews[1].setAlpha(alpha);
                this.radialProgressViews[1].onDraw(canvas);
                canvas.restore();
            }
            float translateX = currentTranslationX;
            scaleDiff = 0.0f;
            alpha = 1.0f;
            if (!this.zoomAnimation && translateX > this.maxX && this.currentEditMode == 0) {
                alpha = Math.min(1.0f, (translateX - this.maxX) / ((float) canvas.getWidth()));
                scaleDiff = alpha * 0.3f;
                alpha = 1.0f - alpha;
                translateX = this.maxX;
            }
            boolean drawTextureView = VERSION.SDK_INT >= 16 && this.aspectRatioFrameLayout != null && this.aspectRatioFrameLayout.getVisibility() == 0;
            if (this.centerImage.hasBitmapImage()) {
                canvas.save();
                canvas.translate((float) ((getContainerViewWidth() / 2) + getAdditionX()), (float) ((getContainerViewHeight() / 2) + getAdditionY()));
                canvas.translate(translateX, currentTranslationY);
                canvas.scale(currentScale - scaleDiff, currentScale - scaleDiff);
                if (this.currentEditMode == 1) {
                    this.photoCropView.setBitmapParams(currentScale, translateX, currentTranslationY);
                }
                bitmapWidth = this.centerImage.getBitmapWidth();
                bitmapHeight = this.centerImage.getBitmapHeight();
                if (drawTextureView && this.textureUploaded && Math.abs((((float) bitmapWidth) / ((float) bitmapHeight)) - (((float) this.videoTextureView.getMeasuredWidth()) / ((float) this.videoTextureView.getMeasuredHeight()))) > 0.01f) {
                    bitmapWidth = this.videoTextureView.getMeasuredWidth();
                    bitmapHeight = this.videoTextureView.getMeasuredHeight();
                }
                scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                if (scaleX > scaleY) {
                    scale = scaleY;
                } else {
                    scale = scaleX;
                }
                width = (int) (((float) bitmapWidth) * scale);
                height = (int) (((float) bitmapHeight) * scale);
                if (!(drawTextureView && this.textureUploaded && this.videoCrossfadeStarted && this.videoCrossfadeAlpha == 1.0f)) {
                    this.centerImage.setAlpha(alpha);
                    this.centerImage.setImageCoords((-width) / 2, (-height) / 2, width, height);
                    this.centerImage.draw(canvas);
                }
                if (drawTextureView) {
                    if (!this.videoCrossfadeStarted && this.textureUploaded) {
                        this.videoCrossfadeStarted = true;
                        this.videoCrossfadeAlpha = 0.0f;
                        this.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                    }
                    canvas.translate((float) ((-width) / 2), (float) ((-height) / 2));
                    this.videoTextureView.setAlpha(this.videoCrossfadeAlpha * alpha);
                    this.aspectRatioFrameLayout.draw(canvas);
                    if (this.videoCrossfadeStarted && this.videoCrossfadeAlpha < 1.0f) {
                        long newUpdateTime = System.currentTimeMillis();
                        long dt = newUpdateTime - this.videoCrossfadeAlphaLastTime;
                        this.videoCrossfadeAlphaLastTime = newUpdateTime;
                        this.videoCrossfadeAlpha += ((float) dt) / BitmapDescriptorFactory.HUE_MAGENTA;
                        this.containerView.invalidate();
                        if (this.videoCrossfadeAlpha > 1.0f) {
                            this.videoCrossfadeAlpha = 1.0f;
                        }
                    }
                }
                canvas.restore();
            }
            if (!drawTextureView && (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0)) {
                canvas.save();
                canvas.translate(translateX, currentTranslationY / currentScale);
                this.radialProgressViews[0].setScale(1.0f - scaleDiff);
                this.radialProgressViews[0].setAlpha(alpha);
                this.radialProgressViews[0].onDraw(canvas);
                canvas.restore();
            }
            if (sideImage == this.leftImage) {
                if (sideImage.hasBitmapImage()) {
                    canvas.save();
                    canvas.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                    canvas.translate(((-((((float) canvas.getWidth()) * (this.scale + 1.0f)) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)))) / 2.0f) + currentTranslationX, 0.0f);
                    bitmapWidth = sideImage.getBitmapWidth();
                    bitmapHeight = sideImage.getBitmapHeight();
                    scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                    scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    scale = scaleX > scaleY ? scaleY : scaleX;
                    width = (int) (((float) bitmapWidth) * scale);
                    height = (int) (((float) bitmapHeight) * scale);
                    sideImage.setAlpha(1.0f);
                    sideImage.setImageCoords((-width) / 2, (-height) / 2, width, height);
                    sideImage.draw(canvas);
                    canvas.restore();
                }
                canvas.save();
                canvas.translate(currentTranslationX, currentTranslationY / currentScale);
                canvas.translate((-((((float) canvas.getWidth()) * (this.scale + 1.0f)) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)))) / 2.0f, (-currentTranslationY) / currentScale);
                this.radialProgressViews[2].setScale(1.0f);
                this.radialProgressViews[2].setAlpha(1.0f);
                this.radialProgressViews[2].onDraw(canvas);
                canvas.restore();
            }
        }
    }

    private void onActionClick(boolean download) {
        if ((this.currentMessageObject != null || this.currentBotInlineResult != null) && this.currentFileNames[0] != null) {
            File file = null;
            if (this.currentMessageObject != null) {
                if (!(this.currentMessageObject.messageOwner.attachPath == null || this.currentMessageObject.messageOwner.attachPath.length() == 0)) {
                    file = new File(this.currentMessageObject.messageOwner.attachPath);
                    if (!file.exists()) {
                        file = null;
                    }
                }
                if (file == null) {
                    file = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
                    if (!file.exists()) {
                        file = null;
                    }
                }
            } else if (this.currentBotInlineResult != null) {
                if (this.currentBotInlineResult.document != null) {
                    file = FileLoader.getPathToAttach(this.currentBotInlineResult.document);
                    if (!file.exists()) {
                        file = null;
                    }
                } else {
                    file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.currentBotInlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.currentBotInlineResult.content_url, "mp4"));
                    if (!file.exists()) {
                        file = null;
                    }
                }
            }
            if (file == null) {
                if (!download) {
                    return;
                }
                if (this.currentMessageObject != null) {
                    if (FileLoader.getInstance().isLoadingFile(this.currentFileNames[0])) {
                        FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.getDocument());
                    } else {
                        FileLoader.getInstance().loadFile(this.currentMessageObject.getDocument(), true, false);
                    }
                } else if (this.currentBotInlineResult == null) {
                } else {
                    if (this.currentBotInlineResult.document != null) {
                        if (FileLoader.getInstance().isLoadingFile(this.currentFileNames[0])) {
                            FileLoader.getInstance().cancelLoadFile(this.currentBotInlineResult.document);
                        } else {
                            FileLoader.getInstance().loadFile(this.currentBotInlineResult.document, true, false);
                        }
                    } else if (ImageLoader.getInstance().isLoadingHttpFile(this.currentBotInlineResult.content_url)) {
                        ImageLoader.getInstance().cancelLoadHttpFile(this.currentBotInlineResult.content_url);
                    } else {
                        ImageLoader.getInstance().loadHttpFile(this.currentBotInlineResult.content_url, "mp4");
                    }
                }
            } else if (VERSION.SDK_INT >= 16) {
                preparePlayer(file, true);
            } else {
                Intent intent = new Intent("android.intent.action.VIEW");
                if (VERSION.SDK_INT >= 24) {
                    intent.setFlags(1);
                    intent.setDataAndType(FileProvider.getUriForFile(this.parentActivity, "org.telegram.messenger.beta.provider", file), MimeTypes.VIDEO_MP4);
                } else {
                    intent.setDataAndType(Uri.fromFile(file), MimeTypes.VIDEO_MP4);
                }
                this.parentActivity.startActivityForResult(intent, 500);
            }
        }
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.scale != 1.0f) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(velocityX), Math.round(velocityY), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.containerView.postInvalidate();
        }
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        boolean z = false;
        if (this.discardTap) {
            return false;
        }
        int state;
        float x;
        float y;
        if (this.canShowBottom) {
            boolean drawTextureView;
            if (VERSION.SDK_INT < 16 || this.aspectRatioFrameLayout == null || this.aspectRatioFrameLayout.getVisibility() != 0) {
                drawTextureView = false;
            } else {
                drawTextureView = true;
            }
            if (!(this.radialProgressViews[0] == null || this.containerView == null || drawTextureView)) {
                state = this.radialProgressViews[0].backgroundState;
                if (state > 0 && state <= 3) {
                    x = e.getX();
                    y = e.getY();
                    if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                        onActionClick(true);
                        checkProgress(0, true);
                        return true;
                    }
                }
            }
            if (!this.isActionBarVisible) {
                z = true;
            }
            toggleActionBar(z, true);
            return true;
        } else if (this.sendPhotoType == 0) {
            this.checkImageView.performClick();
            return true;
        } else if (this.currentBotInlineResult == null) {
            return true;
        } else {
            if (!this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) && !MessageObject.isVideoDocument(this.currentBotInlineResult.document)) {
                return true;
            }
            state = this.radialProgressViews[0].backgroundState;
            if (state <= 0 || state > 3) {
                return true;
            }
            x = e.getX();
            y = e.getY();
            if (x < ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f || x > ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f || y < ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f || y > ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                return true;
            }
            onActionClick(true);
            checkProgress(0, true);
            return true;
        }
    }

    public boolean onDoubleTap(MotionEvent e) {
        if (!this.canZoom || (this.scale == 1.0f && (this.translationY != 0.0f || this.translationX != 0.0f))) {
            return false;
        }
        if (this.animationStartTime != 0 || this.animationInProgress != 0) {
            return false;
        }
        if (this.scale == 1.0f) {
            float atx = (e.getX() - ((float) (getContainerViewWidth() / 2))) - (((e.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
            float aty = (e.getY() - ((float) (getContainerViewHeight() / 2))) - (((e.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
            updateMinMax(3.0f);
            if (atx < this.minX) {
                atx = this.minX;
            } else if (atx > this.maxX) {
                atx = this.maxX;
            }
            if (aty < this.minY) {
                aty = this.minY;
            } else if (aty > this.maxY) {
                aty = this.maxY;
            }
            animateTo(3.0f, atx, aty, true);
        } else {
            animateTo(1.0f, 0.0f, 0.0f, true);
        }
        this.doubleTap = true;
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}
