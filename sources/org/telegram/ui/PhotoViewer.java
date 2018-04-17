package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v4.content.FileProvider;
import android.text.Layout.Alignment;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiSuggestion;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SavedFilterState;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.LinearSmoothScrollerEnd;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_inputPhoto;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_pageBlockAuthorDate;
import org.telegram.tgnet.TLRPC.TL_pageFull;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.NumberPicker.Formatter;
import org.telegram.ui.Components.PhotoCropView;
import org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PhotoPaintView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.PipVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanUserMentionPhotoViewer;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.telegram.ui.Components.VideoTimelinePlayView;
import org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

public class PhotoViewer implements OnDoubleTapListener, OnGestureListener, NotificationCenterDelegate {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile PhotoViewer Instance = null;
    private static volatile PhotoViewer PipInstance = null;
    private static DecelerateInterpolator decelerateInterpolator = null;
    private static final int gallery_menu_cancel_loading = 7;
    private static final int gallery_menu_delete = 6;
    private static final int gallery_menu_masks = 13;
    private static final int gallery_menu_openin = 11;
    private static final int gallery_menu_pip = 5;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_send = 3;
    private static final int gallery_menu_share = 10;
    private static final int gallery_menu_showall = 2;
    private static final int gallery_menu_showinchat = 4;
    private static Drawable[] progressDrawables;
    private static Paint progressPaint;
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimator;
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
    private float[][] animationValues = ((float[][]) Array.newInstance(float.class, new int[]{2, 8}));
    private boolean applying;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private boolean attachedToWindow;
    private long audioFramesSize;
    private ArrayList<Photo> avatarsArr = new ArrayList();
    private int avatarsDialogId;
    private BackgroundDrawable backgroundDrawable = new BackgroundDrawable(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
    private int bitrate;
    private Paint blackPaint = new Paint();
    private FrameLayout bottomLayout;
    private boolean bottomTouchEnabled = true;
    private ImageView cameraItem;
    private boolean canDragDown = true;
    private boolean canZoom = true;
    private PhotoViewerCaptionEnterView captionEditText;
    private TextView captionTextView;
    private ImageReceiver centerImage = new ImageReceiver();
    private AnimatorSet changeModeAnimation;
    private TextureView changedTextureView;
    private boolean changingPage;
    private boolean changingTextureView;
    private CheckBox checkImageView;
    private int classGuid;
    private ImageView compressItem;
    private AnimatorSet compressItemAnimation;
    private int compressionsCount = -1;
    private FrameLayoutDrawer containerView;
    private ImageView cropItem;
    private int currentAccount;
    private AnimatedFileDrawable currentAnimation;
    private Bitmap currentBitmap;
    private BotInlineResult currentBotInlineResult;
    private AnimatorSet currentCaptionAnimation;
    private long currentDialogId;
    private int currentEditMode;
    private FileLocation currentFileLocation;
    private String[] currentFileNames = new String[3];
    private int currentIndex;
    private AnimatorSet currentListViewAnimation;
    private Runnable currentLoadingVideoRunnable;
    private MessageObject currentMessageObject;
    private String currentPathObject;
    private PlaceProviderObject currentPlaceObject;
    private Uri currentPlayingVideoFile;
    private String currentSubtitle;
    private BitmapHolder currentThumb;
    private FileLocation currentUserAvatarLocation = null;
    private boolean currentVideoFinishedLoading;
    private int dateOverride;
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
    private long endTime;
    private long estimatedDuration;
    private int estimatedSize;
    private boolean firstAnimationDelay;
    boolean fromCamera;
    private GestureDetector gestureDetector;
    private GroupedPhotosListView groupedPhotosListView;
    private PlaceProviderObject hideAfterAnimation;
    private AnimatorSet hintAnimation;
    private Runnable hintHideRunnable;
    private TextView hintTextView;
    private boolean ignoreDidSetImage;
    private AnimatorSet imageMoveAnimation;
    private ArrayList<MessageObject> imagesArr = new ArrayList();
    private ArrayList<Object> imagesArrLocals = new ArrayList();
    private ArrayList<FileLocation> imagesArrLocations = new ArrayList();
    private ArrayList<Integer> imagesArrLocationsSizes = new ArrayList();
    private ArrayList<MessageObject> imagesArrTemp = new ArrayList();
    private SparseArray<MessageObject>[] imagesByIds = new SparseArray[]{new SparseArray(), new SparseArray()};
    private SparseArray<MessageObject>[] imagesByIdsTemp = new SparseArray[]{new SparseArray(), new SparseArray()};
    private boolean inPreview;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private boolean invalidCoords;
    private boolean isActionBarVisible = true;
    private boolean isCurrentVideo;
    private boolean isEvent;
    private boolean isFirstLoading;
    private boolean isInline;
    private boolean isPhotosListViewVisible;
    private boolean isPlaying;
    private boolean isStreaming;
    private boolean isVisible;
    private LinearLayout itemsLayout;
    private boolean keepScreenOnFlagSet;
    private long lastBufferedPositionCheck;
    private Object lastInsets;
    private String lastTitle;
    private ImageReceiver leftImage = new ImageReceiver();
    private boolean loadInitialVideo;
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
    private AnimatorSet miniProgressAnimator;
    private Runnable miniProgressShowRunnable = new C15831();
    private RadialProgressView miniProgressView;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private ImageView muteItem;
    private boolean muteVideo;
    private String nameOverride;
    private TextView nameTextView;
    private boolean needCaptionLayout;
    private boolean needSearchImageInArr;
    private boolean opennedFromMedia;
    private int originalBitrate;
    private int originalHeight;
    private long originalSize;
    private int originalWidth;
    private ImageView paintItem;
    private Activity parentActivity;
    private ChatAttachAlert parentAlert;
    private ChatActivity parentChatActivity;
    private PhotoCropView photoCropView;
    private PhotoFilterView photoFilterView;
    private PhotoPaintView photoPaintView;
    private PhotoProgressView[] photoProgressViews = new PhotoProgressView[3];
    private CounterView photosCounterView;
    private FrameLayout pickerView;
    private ImageView pickerViewSendButton;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale = 1.0f;
    private float pinchStartX;
    private float pinchStartY;
    private boolean pipAnimationInProgress;
    private boolean pipAvailable;
    private ActionBarMenuItem pipItem;
    private int[] pipPosition = new int[2];
    private PipVideoView pipVideoView;
    private PhotoViewerProvider placeProvider;
    private int previewViewEnd;
    private int previousCompression;
    private RadialProgressView progressView;
    private QualityChooseView qualityChooseView;
    private AnimatorSet qualityChooseViewAnimation;
    private PickerBottomLayoutViewer qualityPicker;
    private boolean requestingPreview;
    private TextView resetButton;
    private int resultHeight;
    private int resultWidth;
    private ImageReceiver rightImage = new ImageReceiver();
    private int rotationValue;
    private float scale = 1.0f;
    private Scroller scroller;
    private float seekToProgressPending;
    private int selectedCompression;
    private ListAdapter selectedPhotosAdapter;
    private RecyclerListView selectedPhotosListView;
    private ActionBarMenuItem sendItem;
    private int sendPhotoType;
    private ImageView shareButton;
    private PlaceProviderObject showAfterAnimation;
    private int slideshowMessageId;
    private long startTime;
    private long startedPlayTime;
    private boolean streamingAlertShown;
    private SurfaceTextureListener surfaceTextureListener = new C15984();
    private TextView switchCaptionTextView;
    private int switchImageAfterAnimation;
    private Runnable switchToInlineRunnable = new C15933();
    private boolean switchingInlineMode;
    private int switchingToIndex;
    private ImageView textureImageView;
    private boolean textureUploaded;
    private ImageView timeItem;
    private int totalImagesCount;
    private int totalImagesCountMerge;
    private long transitionAnimationStartTime;
    private float translationX;
    private float translationY;
    private boolean tryStartRequestPreviewOnFinish;
    private ImageView tuneItem;
    private Runnable updateProgressRunnable = new C15892();
    private VelocityTracker velocityTracker;
    private ImageView videoBackwardButton;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private float videoDuration;
    private ImageView videoForwardButton;
    private long videoFramesSize;
    private boolean videoHasAudio;
    private ImageView videoPlayButton;
    private VideoPlayer videoPlayer;
    private FrameLayout videoPlayerControlFrameLayout;
    private SeekBar videoPlayerSeekbar;
    private SimpleTextView videoPlayerTime;
    private MessageObject videoPreviewMessageObject;
    private TextureView videoTextureView;
    private VideoTimelinePlayView videoTimelineView;
    private AlertDialog visibleDialog;
    private int waitingForDraw;
    private int waitingForFirstTextureUpload;
    private boolean wasLayout;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;
    private boolean zoomAnimation;
    private boolean zooming;

    /* renamed from: org.telegram.ui.PhotoViewer$1 */
    class C15831 implements Runnable {
        C15831() {
        }

        public void run() {
            PhotoViewer.this.toggleMiniProgressInternal(true);
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$2 */
    class C15892 implements Runnable {
        C15892() {
        }

        public void run() {
            if (PhotoViewer.this.videoPlayer != null) {
                float progress;
                if (PhotoViewer.this.isCurrentVideo) {
                    if (!PhotoViewer.this.videoTimelineView.isDragging()) {
                        progress = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                        if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                            PhotoViewer.this.videoTimelineView.setProgress(progress);
                        } else if (progress >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                            PhotoViewer.this.videoPlayer.pause();
                            PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                            PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                            PhotoViewer.this.containerView.invalidate();
                        } else {
                            progress -= PhotoViewer.this.videoTimelineView.getLeftProgress();
                            if (progress < 0.0f) {
                                progress = 0.0f;
                            }
                            progress /= PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress();
                            if (progress > 1.0f) {
                                progress = 1.0f;
                            }
                            PhotoViewer.this.videoTimelineView.setProgress(progress);
                        }
                        PhotoViewer.this.updateVideoPlayerTime();
                    }
                } else if (!PhotoViewer.this.videoPlayerSeekbar.isDragging()) {
                    float bufferedProgress;
                    progress = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (PhotoViewer.this.currentVideoFinishedLoading) {
                        bufferedProgress = 1.0f;
                    } else {
                        long newTime = SystemClock.uptimeMillis();
                        if (Math.abs(newTime - PhotoViewer.this.lastBufferedPositionCheck) >= 500) {
                            if (PhotoViewer.this.isStreaming) {
                                bufferedProgress = FileLoader.getInstance(PhotoViewer.this.currentAccount).getBufferedProgressFromPosition(PhotoViewer.this.seekToProgressPending != 0.0f ? PhotoViewer.this.seekToProgressPending : progress, PhotoViewer.this.currentFileNames[0]);
                            } else {
                                bufferedProgress = 1.0f;
                            }
                            PhotoViewer.this.lastBufferedPositionCheck = newTime;
                        } else {
                            bufferedProgress = -1.0f;
                        }
                    }
                    if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                        if (PhotoViewer.this.seekToProgressPending == 0.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setProgress(progress);
                        }
                        if (bufferedProgress != -1.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setBufferedProgress(bufferedProgress);
                            if (PhotoViewer.this.pipVideoView != null) {
                                PhotoViewer.this.pipVideoView.setBufferedProgress(bufferedProgress);
                            }
                        }
                    } else if (progress >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                        PhotoViewer.this.videoPlayer.pause();
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                        PhotoViewer.this.containerView.invalidate();
                    } else {
                        progress -= PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (progress < 0.0f) {
                            progress = 0.0f;
                        }
                        progress /= PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (progress > 1.0f) {
                            progress = 1.0f;
                        }
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(progress);
                    }
                    PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                    PhotoViewer.this.updateVideoPlayerTime();
                }
            }
            if (PhotoViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 17);
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$3 */
    class C15933 implements Runnable {
        C15933() {
        }

        public void run() {
            PhotoViewer.this.switchingInlineMode = false;
            if (PhotoViewer.this.currentBitmap != null) {
                PhotoViewer.this.currentBitmap.recycle();
                PhotoViewer.this.currentBitmap = null;
            }
            PhotoViewer.this.changingTextureView = true;
            if (PhotoViewer.this.textureImageView != null) {
                try {
                    PhotoViewer.this.currentBitmap = Bitmaps.createBitmap(PhotoViewer.this.videoTextureView.getWidth(), PhotoViewer.this.videoTextureView.getHeight(), Config.ARGB_8888);
                    PhotoViewer.this.videoTextureView.getBitmap(PhotoViewer.this.currentBitmap);
                } catch (Throwable e) {
                    if (PhotoViewer.this.currentBitmap != null) {
                        PhotoViewer.this.currentBitmap.recycle();
                        PhotoViewer.this.currentBitmap = null;
                    }
                    FileLog.m3e(e);
                }
                if (PhotoViewer.this.currentBitmap != null) {
                    PhotoViewer.this.textureImageView.setVisibility(0);
                    PhotoViewer.this.textureImageView.setImageBitmap(PhotoViewer.this.currentBitmap);
                } else {
                    PhotoViewer.this.textureImageView.setImageDrawable(null);
                }
            }
            PhotoViewer.this.isInline = true;
            PhotoViewer.this.pipVideoView = new PipVideoView();
            PhotoViewer.this.changedTextureView = PhotoViewer.this.pipVideoView.show(PhotoViewer.this.parentActivity, PhotoViewer.this, PhotoViewer.this.aspectRatioFrameLayout.getAspectRatio(), PhotoViewer.this.aspectRatioFrameLayout.getVideoRotation());
            PhotoViewer.this.changedTextureView.setVisibility(4);
            PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoTextureView);
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$4 */
    class C15984 implements SurfaceTextureListener {

        /* renamed from: org.telegram.ui.PhotoViewer$4$1 */
        class C15951 implements OnPreDrawListener {

            /* renamed from: org.telegram.ui.PhotoViewer$4$1$1 */
            class C15941 implements Runnable {
                C15941() {
                }

                public void run() {
                    if (PhotoViewer.this.isInline) {
                        PhotoViewer.this.dismissInternal();
                    }
                }
            }

            C15951() {
            }

            public boolean onPreDraw() {
                PhotoViewer.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (PhotoViewer.this.textureImageView != null) {
                    PhotoViewer.this.textureImageView.setVisibility(4);
                    PhotoViewer.this.textureImageView.setImageDrawable(null);
                    if (PhotoViewer.this.currentBitmap != null) {
                        PhotoViewer.this.currentBitmap.recycle();
                        PhotoViewer.this.currentBitmap = null;
                    }
                }
                AndroidUtilities.runOnUIThread(new C15941());
                PhotoViewer.this.waitingForFirstTextureUpload = 0;
                return true;
            }
        }

        C15984() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (PhotoViewer.this.videoTextureView == null || !PhotoViewer.this.changingTextureView) {
                return true;
            }
            if (PhotoViewer.this.switchingInlineMode) {
                PhotoViewer.this.waitingForFirstTextureUpload = 2;
            }
            PhotoViewer.this.videoTextureView.setSurfaceTexture(surface);
            PhotoViewer.this.videoTextureView.setVisibility(0);
            PhotoViewer.this.changingTextureView = false;
            PhotoViewer.this.containerView.invalidate();
            return false;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (PhotoViewer.this.waitingForFirstTextureUpload == 1) {
                PhotoViewer.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new C15951());
                PhotoViewer.this.changedTextureView.invalidate();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$6 */
    class C16086 implements OnApplyWindowInsetsListener {
        C16086() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
            WindowInsets oldInsets = (WindowInsets) PhotoViewer.this.lastInsets;
            PhotoViewer.this.lastInsets = insets;
            if (oldInsets == null || !oldInsets.toString().equals(insets.toString())) {
                if (PhotoViewer.this.animationInProgress == 1) {
                    PhotoViewer.this.animatingImageView.setTranslationX(PhotoViewer.this.animatingImageView.getTranslationX() - ((float) PhotoViewer.this.getLeftInset()));
                    PhotoViewer.this.animationValues[0][2] = PhotoViewer.this.animatingImageView.getTranslationX();
                }
                PhotoViewer.this.windowView.requestLayout();
            }
            return insets.consumeSystemWindowInsets();
        }
    }

    private class BackgroundDrawable extends ColorDrawable {
        private boolean allowDrawContent;
        private Runnable drawRunnable;

        /* renamed from: org.telegram.ui.PhotoViewer$BackgroundDrawable$1 */
        class C16151 implements Runnable {
            C16151() {
            }

            public void run() {
                if (PhotoViewer.this.parentAlert != null) {
                    PhotoViewer.this.parentAlert.setAllowDrawContent(BackgroundDrawable.this.allowDrawContent);
                }
            }
        }

        public BackgroundDrawable(int color) {
            super(color);
        }

        @Keep
        public void setAlpha(int alpha) {
            if (PhotoViewer.this.parentActivity instanceof LaunchActivity) {
                boolean z;
                if (PhotoViewer.this.isVisible) {
                    if (alpha == 255) {
                        z = false;
                        this.allowDrawContent = z;
                        ((LaunchActivity) PhotoViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(this.allowDrawContent);
                        if (PhotoViewer.this.parentAlert != null) {
                            if (!this.allowDrawContent) {
                                AndroidUtilities.runOnUIThread(new C16151(), 50);
                            } else if (PhotoViewer.this.parentAlert != null) {
                                PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
                            }
                        }
                    }
                }
                z = true;
                this.allowDrawContent = z;
                ((LaunchActivity) PhotoViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(this.allowDrawContent);
                if (PhotoViewer.this.parentAlert != null) {
                    if (!this.allowDrawContent) {
                        AndroidUtilities.runOnUIThread(new C16151(), 50);
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
                AndroidUtilities.runOnUIThread(this.drawRunnable);
                this.drawRunnable = null;
            }
        }
    }

    private class CounterView extends View {
        private int currentCount = 0;
        private int height;
        private Paint paint;
        private RectF rect;
        private float rotation;
        private StaticLayout staticLayout;
        private TextPaint textPaint = new TextPaint(1);
        private int width;

        public CounterView(Context context) {
            super(context);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(18.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textPaint.setColor(-1);
            this.paint = new Paint(1);
            this.paint.setColor(-1);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.paint.setStyle(Style.STROKE);
            this.paint.setStrokeJoin(Join.ROUND);
            this.rect = new RectF();
            setCount(0);
        }

        @Keep
        public void setScaleX(float scaleX) {
            super.setScaleX(scaleX);
            invalidate();
        }

        @Keep
        public void setRotationX(float rotationX) {
            this.rotation = rotationX;
            invalidate();
        }

        public float getRotationX() {
            return this.rotation;
        }

        public void setCount(int value) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(Math.max(1, value));
            this.staticLayout = new StaticLayout(stringBuilder.toString(), this.textPaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.width = (int) Math.ceil((double) this.staticLayout.getLineWidth(0));
            this.height = this.staticLayout.getLineBottom(0);
            AnimatorSet animatorSet = new AnimatorSet();
            if (value == 0) {
                Animator[] animatorArr = new Animator[4];
                animatorArr[0] = ObjectAnimator.ofFloat(this, "scaleX", new float[]{0.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this, "scaleY", new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofInt(this.paint, "alpha", new int[]{0});
                animatorArr[3] = ObjectAnimator.ofInt(this.textPaint, "alpha", new int[]{0});
                animatorSet.playTogether(animatorArr);
                animatorSet.setInterpolator(new DecelerateInterpolator());
            } else if (this.currentCount == 0) {
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "scaleX", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this, "scaleY", new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(this.paint, "alpha", new int[]{0, 255}), ObjectAnimator.ofInt(this.textPaint, "alpha", new int[]{0, 255})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
            } else if (value < this.currentCount) {
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.1f, 1.0f}), ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.1f, 1.0f})});
                animatorSet.setInterpolator(new OvershootInterpolator());
            } else {
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "scaleX", new float[]{0.9f, 1.0f}), ObjectAnimator.ofFloat(this, "scaleY", new float[]{0.9f, 1.0f})});
                animatorSet.setInterpolator(new OvershootInterpolator());
            }
            animatorSet.setDuration(180);
            animatorSet.start();
            requestLayout();
            this.currentCount = value;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(Math.max(this.width + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(30.0f)), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
        }

        protected void onDraw(Canvas canvas) {
            int cy = getMeasuredHeight() / 2;
            this.paint.setAlpha(255);
            this.rect.set((float) AndroidUtilities.dp(1.0f), (float) (cy - AndroidUtilities.dp(14.0f)), (float) (getMeasuredWidth() - AndroidUtilities.dp(1.0f)), (float) (AndroidUtilities.dp(14.0f) + cy));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), this.paint);
            if (this.staticLayout != null) {
                this.textPaint.setAlpha((int) ((1.0f - this.rotation) * 255.0f));
                canvas.save();
                canvas.translate((float) ((getMeasuredWidth() - this.width) / 2), (((float) ((getMeasuredHeight() - this.height) / 2)) + AndroidUtilities.dpf2(0.2f)) + (this.rotation * ((float) AndroidUtilities.dp(5.0f))));
                this.staticLayout.draw(canvas);
                canvas.restore();
                this.paint.setAlpha((int) (this.rotation * 255.0f));
                int cx = (int) this.rect.centerX();
                cy = (int) (((float) ((int) this.rect.centerY())) - ((((float) AndroidUtilities.dp(5.0f)) * (1.0f - this.rotation)) + ((float) AndroidUtilities.dp(3.0f))));
                canvas.drawLine((float) (AndroidUtilities.dp(0.5f) + cx), (float) (cy - AndroidUtilities.dp(0.5f)), (float) (cx - AndroidUtilities.dp(6.0f)), (float) (AndroidUtilities.dp(6.0f) + cy), this.paint);
                canvas.drawLine((float) (cx - AndroidUtilities.dp(0.5f)), (float) (cy - AndroidUtilities.dp(0.5f)), (float) (AndroidUtilities.dp(6.0f) + cx), (float) (AndroidUtilities.dp(6.0f) + cy), this.paint);
            }
        }
    }

    private class GroupedPhotosListView extends View implements OnGestureListener {
        private boolean animateAllLine;
        private int animateToDX;
        private int animateToDXStart;
        private int animateToItem = -1;
        private Paint backgroundPaint = new Paint();
        private long currentGroupId;
        private int currentImage;
        private float currentItemProgress = 1.0f;
        private ArrayList<Object> currentObjects = new ArrayList();
        private ArrayList<TLObject> currentPhotos = new ArrayList();
        private int drawDx;
        private GestureDetector gestureDetector;
        private boolean ignoreChanges;
        private ArrayList<ImageReceiver> imagesToDraw = new ArrayList();
        private int itemHeight;
        private int itemSpacing;
        private int itemWidth;
        private int itemY;
        private long lastUpdateTime;
        private float moveLineProgress;
        private boolean moving;
        private int nextImage;
        private float nextItemProgress = 0.0f;
        private int nextPhotoScrolling = -1;
        private Scroller scroll;
        private boolean scrolling;
        private boolean stopedScrolling;
        private ArrayList<ImageReceiver> unusedReceivers = new ArrayList();

        public GroupedPhotosListView(Context context) {
            super(context);
            this.gestureDetector = new GestureDetector(context, this);
            this.scroll = new Scroller(context);
            this.itemWidth = AndroidUtilities.dp(42.0f);
            this.itemHeight = AndroidUtilities.dp(56.0f);
            this.itemSpacing = AndroidUtilities.dp(1.0f);
            this.itemY = AndroidUtilities.dp(3.0f);
            this.backgroundPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        }

        public void clear() {
            this.currentPhotos.clear();
            this.currentObjects.clear();
            this.imagesToDraw.clear();
        }

        public void fillList() {
            if (this.ignoreChanges) {
                this.ignoreChanges = false;
                return;
            }
            int max;
            int a;
            boolean changed = false;
            int newCount = 0;
            Object currentObject = null;
            if (!PhotoViewer.this.imagesArrLocations.isEmpty()) {
                FileLocation location = (FileLocation) PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
                newCount = PhotoViewer.this.imagesArrLocations.size();
                currentObject = location;
            } else if (!PhotoViewer.this.imagesArr.isEmpty()) {
                MessageObject messageObject = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                currentObject = messageObject;
                if (messageObject.messageOwner.grouped_id == this.currentGroupId) {
                    max = Math.min(PhotoViewer.this.currentIndex + 10, PhotoViewer.this.imagesArr.size());
                    for (a = PhotoViewer.this.currentIndex; a < max; a++) {
                        MessageObject object = (MessageObject) PhotoViewer.this.imagesArr.get(a);
                        if (PhotoViewer.this.slideshowMessageId == 0 && object.messageOwner.grouped_id != this.currentGroupId) {
                            break;
                        }
                        newCount++;
                    }
                    a = Math.max(PhotoViewer.this.currentIndex - 10, 0);
                    for (int a2 = PhotoViewer.this.currentIndex - 1; a2 >= a; a2--) {
                        MessageObject object2 = (MessageObject) PhotoViewer.this.imagesArr.get(a2);
                        if (PhotoViewer.this.slideshowMessageId == 0 && object2.messageOwner.grouped_id != this.currentGroupId) {
                            break;
                        }
                        newCount++;
                    }
                } else {
                    changed = true;
                    this.currentGroupId = messageObject.messageOwner.grouped_id;
                }
            }
            if (currentObject != null) {
                if (!changed) {
                    if (newCount == this.currentPhotos.size()) {
                        if (this.currentObjects.indexOf(currentObject) != -1) {
                            max = this.currentObjects.indexOf(currentObject);
                            if (!(this.currentImage == max || max == -1)) {
                                if (this.animateAllLine) {
                                    this.animateToItem = max;
                                    this.nextImage = max;
                                    this.animateToDX = (this.currentImage - max) * (this.itemWidth + this.itemSpacing);
                                    this.moving = true;
                                    this.animateAllLine = false;
                                    this.lastUpdateTime = System.currentTimeMillis();
                                    invalidate();
                                } else {
                                    fillImages(true, (this.currentImage - max) * (this.itemWidth + this.itemSpacing));
                                    this.currentImage = max;
                                    this.moving = false;
                                }
                                this.drawDx = 0;
                            }
                        }
                    }
                    changed = true;
                }
                if (changed) {
                    this.animateAllLine = false;
                    this.currentPhotos.clear();
                    this.currentObjects.clear();
                    if (!PhotoViewer.this.imagesArrLocations.isEmpty()) {
                        this.currentObjects.addAll(PhotoViewer.this.imagesArrLocations);
                        this.currentPhotos.addAll(PhotoViewer.this.imagesArrLocations);
                        this.currentImage = PhotoViewer.this.currentIndex;
                        this.animateToItem = -1;
                    } else if (!PhotoViewer.this.imagesArr.isEmpty() && (this.currentGroupId != 0 || PhotoViewer.this.slideshowMessageId != 0)) {
                        max = Math.min(PhotoViewer.this.currentIndex + 10, PhotoViewer.this.imagesArr.size());
                        for (a = PhotoViewer.this.currentIndex; a < max; a++) {
                            object2 = (MessageObject) PhotoViewer.this.imagesArr.get(a);
                            if (PhotoViewer.this.slideshowMessageId == 0 && object2.messageOwner.grouped_id != this.currentGroupId) {
                                break;
                            }
                            this.currentObjects.add(object2);
                            this.currentPhotos.add(FileLoader.getClosestPhotoSizeWithSize(object2.photoThumbs, 56, true));
                        }
                        this.currentImage = 0;
                        this.animateToItem = -1;
                        int min = Math.max(PhotoViewer.this.currentIndex - 10, 0);
                        for (a = PhotoViewer.this.currentIndex - 1; a >= min; a--) {
                            object2 = (MessageObject) PhotoViewer.this.imagesArr.get(a);
                            if (PhotoViewer.this.slideshowMessageId == 0 && object2.messageOwner.grouped_id != this.currentGroupId) {
                                break;
                            }
                            this.currentObjects.add(0, object2);
                            this.currentPhotos.add(0, FileLoader.getClosestPhotoSizeWithSize(object2.photoThumbs, 56, true));
                            this.currentImage++;
                        }
                    }
                    if (this.currentPhotos.size() == 1) {
                        this.currentPhotos.clear();
                        this.currentObjects.clear();
                    }
                    fillImages(false, 0);
                }
            }
        }

        public void setMoveProgress(float progress) {
            if (!this.scrolling) {
                if (this.animateToItem < 0) {
                    if (progress > 0.0f) {
                        this.nextImage = this.currentImage - 1;
                    } else {
                        this.nextImage = this.currentImage + 1;
                    }
                    if (this.nextImage < 0 || this.nextImage >= this.currentPhotos.size()) {
                        this.currentItemProgress = 1.0f;
                    } else {
                        this.currentItemProgress = 1.0f - Math.abs(progress);
                    }
                    this.nextItemProgress = 1.0f - this.currentItemProgress;
                    this.moving = progress != 0.0f;
                    invalidate();
                    if (!this.currentPhotos.isEmpty() && (progress >= 0.0f || this.currentImage != this.currentPhotos.size() - 1)) {
                        if (progress <= 0.0f || this.currentImage != 0) {
                            this.drawDx = (int) (((float) (this.itemWidth + this.itemSpacing)) * progress);
                            fillImages(true, this.drawDx);
                        }
                    }
                }
            }
        }

        private ImageReceiver getFreeReceiver() {
            ImageReceiver receiver;
            if (this.unusedReceivers.isEmpty()) {
                receiver = new ImageReceiver(this);
            } else {
                receiver = (ImageReceiver) this.unusedReceivers.get(0);
                this.unusedReceivers.remove(0);
            }
            this.imagesToDraw.add(receiver);
            receiver.setCurrentAccount(PhotoViewer.this.currentAccount);
            return receiver;
        }

        private void fillImages(boolean move, int dx) {
            GroupedPhotosListView groupedPhotosListView = this;
            int a = 0;
            if (!(move || groupedPhotosListView.imagesToDraw.isEmpty())) {
                groupedPhotosListView.unusedReceivers.addAll(groupedPhotosListView.imagesToDraw);
                groupedPhotosListView.imagesToDraw.clear();
                groupedPhotosListView.moving = false;
                groupedPhotosListView.moveLineProgress = 1.0f;
                groupedPhotosListView.currentItemProgress = 1.0f;
                groupedPhotosListView.nextItemProgress = 0.0f;
            }
            invalidate();
            if (getMeasuredWidth() != 0) {
                if (!groupedPhotosListView.currentPhotos.isEmpty()) {
                    int addRightIndex;
                    int addLeftIndex;
                    int count;
                    ImageReceiver receiver;
                    int width = getMeasuredWidth();
                    int startX = (getMeasuredWidth() / 2) - (groupedPhotosListView.itemWidth / 2);
                    if (move) {
                        addRightIndex = Integer.MIN_VALUE;
                        addLeftIndex = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        count = groupedPhotosListView.imagesToDraw.size();
                        while (a < count) {
                            ImageReceiver receiver2 = (ImageReceiver) groupedPhotosListView.imagesToDraw.get(a);
                            int num = receiver2.getParam();
                            int x = (((num - groupedPhotosListView.currentImage) * (groupedPhotosListView.itemWidth + groupedPhotosListView.itemSpacing)) + startX) + dx;
                            if (x > width || groupedPhotosListView.itemWidth + x < 0) {
                                groupedPhotosListView.unusedReceivers.add(receiver2);
                                groupedPhotosListView.imagesToDraw.remove(a);
                                count--;
                                a--;
                            }
                            addLeftIndex = Math.min(addLeftIndex, num - 1);
                            addRightIndex = Math.max(addRightIndex, num + 1);
                            a++;
                        }
                    } else {
                        addRightIndex = groupedPhotosListView.currentImage;
                        addLeftIndex = groupedPhotosListView.currentImage - 1;
                    }
                    a = addLeftIndex;
                    if (addRightIndex != Integer.MIN_VALUE) {
                        addLeftIndex = groupedPhotosListView.currentPhotos.size();
                        for (count = addRightIndex; count < addLeftIndex; count++) {
                            int x2 = (((count - groupedPhotosListView.currentImage) * (groupedPhotosListView.itemWidth + groupedPhotosListView.itemSpacing)) + startX) + dx;
                            if (x2 >= width) {
                                break;
                            }
                            TLObject location = (TLObject) groupedPhotosListView.currentPhotos.get(count);
                            if (location instanceof PhotoSize) {
                                location = ((PhotoSize) location).location;
                            }
                            receiver = getFreeReceiver();
                            receiver.setImageCoords(x2, groupedPhotosListView.itemY, groupedPhotosListView.itemWidth, groupedPhotosListView.itemHeight);
                            ImageReceiver receiver3 = receiver;
                            receiver.setImage(null, null, null, null, (FileLocation) location, "80_80", 0, null, 1);
                            receiver3.setParam(count);
                        }
                    }
                    if (a != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        for (addLeftIndex = a; addLeftIndex >= 0; addLeftIndex--) {
                            count = ((((addLeftIndex - groupedPhotosListView.currentImage) * (groupedPhotosListView.itemWidth + groupedPhotosListView.itemSpacing)) + startX) + dx) + groupedPhotosListView.itemWidth;
                            if (count <= 0) {
                                break;
                            }
                            TLObject location2 = (TLObject) groupedPhotosListView.currentPhotos.get(addLeftIndex);
                            if (location2 instanceof PhotoSize) {
                                location2 = ((PhotoSize) location2).location;
                            }
                            receiver = getFreeReceiver();
                            receiver.setImageCoords(count, groupedPhotosListView.itemY, groupedPhotosListView.itemWidth, groupedPhotosListView.itemHeight);
                            ImageReceiver receiver4 = receiver;
                            receiver.setImage(null, null, null, null, (FileLocation) location2, "80_80", 0, null, 1);
                            receiver4.setParam(addLeftIndex);
                        }
                    }
                }
            }
        }

        public boolean onDown(MotionEvent e) {
            if (!this.scroll.isFinished()) {
                this.scroll.abortAnimation();
            }
            this.animateToItem = -1;
            return true;
        }

        public void onShowPress(MotionEvent e) {
        }

        public boolean onSingleTapUp(MotionEvent e) {
            stopScrolling();
            int count = this.imagesToDraw.size();
            for (int a = 0; a < count; a++) {
                ImageReceiver receiver = (ImageReceiver) this.imagesToDraw.get(a);
                if (receiver.isInsideImage(e.getX(), e.getY())) {
                    int num = receiver.getParam();
                    if (num >= 0) {
                        if (num < this.currentObjects.size()) {
                            int idx;
                            if (!PhotoViewer.this.imagesArr.isEmpty()) {
                                idx = PhotoViewer.this.imagesArr.indexOf((MessageObject) this.currentObjects.get(num));
                                if (PhotoViewer.this.currentIndex == idx) {
                                    return true;
                                }
                                this.moveLineProgress = 1.0f;
                                this.animateAllLine = true;
                                PhotoViewer.this.currentIndex = -1;
                                if (PhotoViewer.this.currentThumb != null) {
                                    PhotoViewer.this.currentThumb.release();
                                    PhotoViewer.this.currentThumb = null;
                                }
                                PhotoViewer.this.setImageIndex(idx, true);
                            } else if (!PhotoViewer.this.imagesArrLocations.isEmpty()) {
                                idx = PhotoViewer.this.imagesArrLocations.indexOf((FileLocation) this.currentObjects.get(num));
                                if (PhotoViewer.this.currentIndex == idx) {
                                    return true;
                                }
                                this.moveLineProgress = 1.0f;
                                this.animateAllLine = true;
                                PhotoViewer.this.currentIndex = -1;
                                if (PhotoViewer.this.currentThumb != null) {
                                    PhotoViewer.this.currentThumb.release();
                                    PhotoViewer.this.currentThumb = null;
                                }
                                PhotoViewer.this.setImageIndex(idx, true);
                            }
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        private void updateAfterScroll() {
            int indexChange = 0;
            int dx = this.drawDx;
            if (Math.abs(dx) > (this.itemWidth / 2) + this.itemSpacing) {
                if (dx > 0) {
                    dx -= (this.itemWidth / 2) + this.itemSpacing;
                    indexChange = 0 + 1;
                } else {
                    dx += (this.itemWidth / 2) + this.itemSpacing;
                    indexChange = 0 - 1;
                }
                indexChange += dx / (this.itemWidth + (this.itemSpacing * 2));
            }
            this.nextPhotoScrolling = this.currentImage - indexChange;
            if (PhotoViewer.this.currentIndex != this.nextPhotoScrolling && this.nextPhotoScrolling >= 0 && this.nextPhotoScrolling < this.currentPhotos.size()) {
                MessageObject photo = this.currentObjects.get(this.nextPhotoScrolling);
                int nextPhoto = -1;
                if (!PhotoViewer.this.imagesArr.isEmpty()) {
                    nextPhoto = PhotoViewer.this.imagesArr.indexOf(photo);
                } else if (!PhotoViewer.this.imagesArrLocations.isEmpty()) {
                    nextPhoto = PhotoViewer.this.imagesArrLocations.indexOf((FileLocation) photo);
                }
                if (nextPhoto >= 0) {
                    this.ignoreChanges = true;
                    PhotoViewer.this.currentIndex = -1;
                    if (PhotoViewer.this.currentThumb != null) {
                        PhotoViewer.this.currentThumb.release();
                        PhotoViewer.this.currentThumb = null;
                    }
                    PhotoViewer.this.setImageIndex(nextPhoto, true);
                }
            }
            if (!this.scrolling) {
                this.scrolling = true;
                this.stopedScrolling = false;
            }
            fillImages(true, this.drawDx);
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            this.drawDx = (int) (((float) this.drawDx) - distanceX);
            int min = getMinScrollX();
            int max = getMaxScrollX();
            if (this.drawDx < min) {
                this.drawDx = min;
            } else if (this.drawDx > max) {
                this.drawDx = max;
            }
            updateAfterScroll();
            return false;
        }

        public void onLongPress(MotionEvent e) {
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            this.scroll.abortAnimation();
            if (this.currentPhotos.size() >= 10) {
                this.scroll.fling(this.drawDx, 0, Math.round(velocityX), 0, getMinScrollX(), getMaxScrollX(), 0, 0);
            }
            return false;
        }

        private void stopScrolling() {
            this.scrolling = false;
            if (!this.scroll.isFinished()) {
                this.scroll.abortAnimation();
            }
            if (this.nextPhotoScrolling >= 0 && this.nextPhotoScrolling < this.currentObjects.size()) {
                this.stopedScrolling = true;
                int i = this.nextPhotoScrolling;
                this.animateToItem = i;
                this.nextImage = i;
                this.animateToDX = (this.currentImage - this.nextPhotoScrolling) * (this.itemWidth + this.itemSpacing);
                this.animateToDXStart = this.drawDx;
                this.moveLineProgress = 1.0f;
                this.nextPhotoScrolling = -1;
            }
            invalidate();
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean z = false;
            if (!this.currentPhotos.isEmpty()) {
                if (getAlpha() == 1.0f) {
                    boolean result;
                    if (!this.gestureDetector.onTouchEvent(event)) {
                        if (!super.onTouchEvent(event)) {
                            result = z;
                            if (this.scrolling && event.getAction() == 1 && this.scroll.isFinished()) {
                                stopScrolling();
                            }
                            return result;
                        }
                    }
                    z = true;
                    result = z;
                    stopScrolling();
                    return result;
                }
            }
            return false;
        }

        private int getMinScrollX() {
            return (-((this.currentPhotos.size() - this.currentImage) - 1)) * (this.itemWidth + (this.itemSpacing * 2));
        }

        private int getMaxScrollX() {
            return this.currentImage * (this.itemWidth + (this.itemSpacing * 2));
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            fillImages(false, 0);
        }

        protected void onDraw(Canvas canvas) {
            if (!this.imagesToDraw.isEmpty()) {
                PhotoSize photoSize;
                int trueWidth;
                int maxItemWidth;
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), r0.backgroundPaint);
                int count = r0.imagesToDraw.size();
                int moveX = r0.drawDx;
                int maxItemWidth2 = (int) (((float) r0.itemWidth) * NUM);
                int padding = AndroidUtilities.dp(NUM);
                TLObject object = (TLObject) r0.currentPhotos.get(r0.currentImage);
                if (object instanceof PhotoSize) {
                    photoSize = (PhotoSize) object;
                    trueWidth = Math.max(r0.itemWidth, (int) (((float) photoSize.f43w) * (((float) r0.itemHeight) / ((float) photoSize.f42h))));
                } else {
                    trueWidth = r0.itemHeight;
                }
                int currentPaddings = (int) (((float) (padding * 2)) * r0.currentItemProgress);
                int trueWidth2 = (r0.itemWidth + ((int) (((float) (Math.min(maxItemWidth2, trueWidth) - r0.itemWidth)) * r0.currentItemProgress))) + currentPaddings;
                if (r0.nextImage < 0 || r0.nextImage >= r0.currentPhotos.size()) {
                    trueWidth = r0.itemWidth;
                } else {
                    object = (TLObject) r0.currentPhotos.get(r0.nextImage);
                    if (object instanceof PhotoSize) {
                        photoSize = (PhotoSize) object;
                        trueWidth = Math.max(r0.itemWidth, (int) (((float) photoSize.f43w) * (((float) r0.itemHeight) / ((float) photoSize.f42h))));
                    } else {
                        trueWidth = r0.itemHeight;
                    }
                }
                trueWidth = Math.min(maxItemWidth2, trueWidth);
                int nextPaddings = (int) (((float) (padding * 2)) * r0.nextItemProgress);
                moveX = (int) (((float) moveX) + ((((float) (((trueWidth + nextPaddings) - r0.itemWidth) / 2)) * r0.nextItemProgress) * ((float) (r0.nextImage > r0.currentImage ? -1 : 1))));
                int nextTrueWidth = (r0.itemWidth + ((int) (((float) (trueWidth - r0.itemWidth)) * r0.nextItemProgress))) + nextPaddings;
                trueWidth = (getMeasuredWidth() - trueWidth2) / 2;
                int a = 0;
                while (a < count) {
                    int count2;
                    ImageReceiver receiver = (ImageReceiver) r0.imagesToDraw.get(a);
                    int num = receiver.getParam();
                    if (num == r0.currentImage) {
                        receiver.setImageX((trueWidth + moveX) + (currentPaddings / 2));
                        receiver.setImageWidth(trueWidth2 - currentPaddings);
                        count2 = count;
                        maxItemWidth = maxItemWidth2;
                    } else {
                        if (r0.nextImage >= r0.currentImage) {
                            count2 = count;
                            maxItemWidth = maxItemWidth2;
                            if (num < r0.currentImage) {
                                receiver.setImageX((((receiver.getParam() - r0.currentImage) * (r0.itemWidth + r0.itemSpacing)) + trueWidth) + moveX);
                            } else if (num <= r0.nextImage) {
                                receiver.setImageX((((trueWidth + trueWidth2) + r0.itemSpacing) + (((receiver.getParam() - r0.currentImage) - 1) * (r0.itemWidth + r0.itemSpacing))) + moveX);
                            } else {
                                receiver.setImageX(((((trueWidth + trueWidth2) + r0.itemSpacing) + (((receiver.getParam() - r0.currentImage) - 2) * (r0.itemWidth + r0.itemSpacing))) + (r0.itemSpacing + nextTrueWidth)) + moveX);
                            }
                        } else if (num < r0.currentImage) {
                            if (num <= r0.nextImage) {
                                count2 = count;
                                receiver.setImageX((((((receiver.getParam() - r0.currentImage) + 1) * (r0.itemWidth + r0.itemSpacing)) + trueWidth) - (r0.itemSpacing + nextTrueWidth)) + moveX);
                            } else {
                                count2 = count;
                                receiver.setImageX((((receiver.getParam() - r0.currentImage) * (r0.itemWidth + r0.itemSpacing)) + trueWidth) + moveX);
                            }
                            maxItemWidth = maxItemWidth2;
                        } else {
                            count2 = count;
                            maxItemWidth = maxItemWidth2;
                            receiver.setImageX((((trueWidth + trueWidth2) + r0.itemSpacing) + (((receiver.getParam() - r0.currentImage) - 1) * (r0.itemWidth + r0.itemSpacing))) + moveX);
                        }
                        if (num == r0.nextImage) {
                            receiver.setImageWidth(nextTrueWidth - nextPaddings);
                            receiver.setImageX(receiver.getImageX() + (nextPaddings / 2));
                        } else {
                            receiver.setImageWidth(r0.itemWidth);
                        }
                    }
                    receiver.draw(canvas);
                    a++;
                    count = count2;
                    maxItemWidth2 = maxItemWidth;
                }
                maxItemWidth = maxItemWidth2;
                count = canvas;
                long newTime = System.currentTimeMillis();
                long dt = newTime - r0.lastUpdateTime;
                if (dt > 17) {
                    dt = 17;
                }
                long dt2 = dt;
                r0.lastUpdateTime = newTime;
                if (r0.animateToItem >= 0) {
                    if (r0.moveLineProgress > 0.0f) {
                        r0.moveLineProgress -= ((float) dt2) / 200.0f;
                        if (r0.animateToItem == r0.currentImage) {
                            if (r0.currentItemProgress < 1.0f) {
                                r0.currentItemProgress += ((float) dt2) / 200.0f;
                                if (r0.currentItemProgress > 1.0f) {
                                    r0.currentItemProgress = 1.0f;
                                }
                            }
                            r0.drawDx = r0.animateToDXStart + ((int) Math.ceil((double) (r0.currentItemProgress * ((float) (r0.animateToDX - r0.animateToDXStart)))));
                        } else {
                            r0.nextItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - r0.moveLineProgress);
                            if (r0.stopedScrolling) {
                                if (r0.currentItemProgress > 0.0f) {
                                    r0.currentItemProgress -= ((float) dt2) / 200.0f;
                                    if (r0.currentItemProgress < 0.0f) {
                                        r0.currentItemProgress = 0.0f;
                                    }
                                }
                                r0.drawDx = r0.animateToDXStart + ((int) Math.ceil((double) (r0.nextItemProgress * ((float) (r0.animateToDX - r0.animateToDXStart)))));
                            } else {
                                r0.currentItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(r0.moveLineProgress);
                                r0.drawDx = (int) Math.ceil((double) (r0.nextItemProgress * ((float) r0.animateToDX)));
                            }
                        }
                        if (r0.moveLineProgress <= 0.0f) {
                            r0.currentImage = r0.animateToItem;
                            r0.moveLineProgress = 1.0f;
                            r0.currentItemProgress = 1.0f;
                            r0.nextItemProgress = 0.0f;
                            r0.moving = false;
                            r0.stopedScrolling = false;
                            r0.drawDx = 0;
                            r0.animateToItem = -1;
                        }
                    }
                    fillImages(true, r0.drawDx);
                    invalidate();
                }
                if (r0.scrolling && r0.currentItemProgress > 0.0f) {
                    r0.currentItemProgress -= ((float) dt2) / 200.0f;
                    if (r0.currentItemProgress < 0.0f) {
                        r0.currentItemProgress = 0.0f;
                    }
                    invalidate();
                }
                if (!r0.scroll.isFinished()) {
                    if (r0.scroll.computeScrollOffset()) {
                        r0.drawDx = r0.scroll.getCurrX();
                        updateAfterScroll();
                        invalidate();
                    }
                    if (r0.scroll.isFinished()) {
                        stopScrolling();
                    }
                }
            }
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return false;
            }
        }
    }

    private class PhotoProgressView {
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

        public PhotoProgressView(Context context, View parentView) {
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
            if (dt > 18) {
                dt = 18;
            }
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
                        this.animatedProgressValue = this.animationProgressStart + (PhotoViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f) * progressDiff);
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
            if (this.backgroundState != state || !animated) {
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
                    PhotoViewer.progressPaint.setAlpha((int) ((255.0f * this.animatedAlphaValue) * this.alpha));
                } else {
                    PhotoViewer.progressPaint.setAlpha((int) (255.0f * this.alpha));
                }
                this.progressRect.set((float) (x + diff), (float) (y + diff), (float) ((x + sizeScaled) - diff), (float) ((y + sizeScaled) - diff));
                canvas.drawArc(this.progressRect, -90.0f + this.radOffset, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, PhotoViewer.progressPaint);
                updateAnimation();
            }
        }
    }

    public interface PhotoViewerProvider {
        boolean allowCaption();

        boolean allowGroupPhotos();

        boolean canScrollAway();

        boolean cancelButtonPressed();

        int getPhotoIndex(int i);

        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i);

        int getSelectedCount();

        HashMap<Object, Object> getSelectedPhotos();

        ArrayList<Object> getSelectedPhotosOrder();

        BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i);

        boolean isPhotoChecked(int i);

        void needAddMorePhotos();

        boolean scaleToFill();

        void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo);

        int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo);

        void toggleGroupPhotosEnabled();

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
        public boolean isEvent;
        public View parentView;
        public int radius;
        public float scale = 1.0f;
        public int size;
        public BitmapHolder thumb;
        public int viewX;
        public int viewY;
    }

    private class QualityChooseView extends View {
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private boolean moving;
        private Paint paint = new Paint(1);
        private int sideSide;
        private boolean startMoving;
        private int startMovingQuality;
        private float startX;
        private TextPaint textPaint = new TextPaint(1);

        public QualityChooseView(Context context) {
            super(context);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.textPaint.setColor(-3289651);
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            boolean z = false;
            int a;
            int cx;
            if (event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                a = 0;
                while (a < PhotoViewer.this.compressionsCount) {
                    cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                    if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                        a++;
                    } else {
                        if (a == PhotoViewer.this.selectedCompression) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingQuality = PhotoViewer.this.selectedCompression;
                    }
                }
            } else if (event.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    a = 0;
                    while (a < PhotoViewer.this.compressionsCount) {
                        int cx2 = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                        cx = ((this.lineSize / 2) + (this.circleSize / 2)) + this.gapSize;
                        if (x <= ((float) (cx2 - cx)) || x >= ((float) (cx2 + cx))) {
                            a++;
                        } else if (PhotoViewer.this.selectedCompression != a) {
                            PhotoViewer.this.selectedCompression = a;
                            PhotoViewer.this.didChangedCompressionLevel(false);
                            invalidate();
                        }
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (!this.moving) {
                    a = 0;
                    while (a < PhotoViewer.this.compressionsCount) {
                        cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                        if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                            a++;
                        } else if (PhotoViewer.this.selectedCompression != a) {
                            PhotoViewer.this.selectedCompression = a;
                            PhotoViewer.this.didChangedCompressionLevel(true);
                            invalidate();
                        }
                    }
                } else if (PhotoViewer.this.selectedCompression != this.startMovingQuality) {
                    PhotoViewer.this.requestVideoPreview(1);
                }
                this.startMoving = false;
                this.moving = false;
            }
            return true;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            this.circleSize = AndroidUtilities.dp(12.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(18.0f);
        }

        protected void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            if (PhotoViewer.this.compressionsCount != 1) {
                r0.lineSize = (((getMeasuredWidth() - (r0.circleSize * PhotoViewer.this.compressionsCount)) - (r0.gapSize * 8)) - (r0.sideSide * 2)) / (PhotoViewer.this.compressionsCount - 1);
            } else {
                r0.lineSize = ((getMeasuredWidth() - (r0.circleSize * PhotoViewer.this.compressionsCount)) - (r0.gapSize * 8)) - (r0.sideSide * 2);
            }
            int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(6.0f);
            int a = 0;
            while (true) {
                int a2 = a;
                if (a2 < PhotoViewer.this.compressionsCount) {
                    String text;
                    String text2;
                    float width;
                    int x;
                    int cx = (r0.sideSide + (((r0.lineSize + (r0.gapSize * 2)) + r0.circleSize) * a2)) + (r0.circleSize / 2);
                    if (a2 <= PhotoViewer.this.selectedCompression) {
                        r0.paint.setColor(-11292945);
                    } else {
                        r0.paint.setColor(NUM);
                    }
                    if (a2 == PhotoViewer.this.compressionsCount - 1) {
                        text = new StringBuilder();
                        text.append(Math.min(PhotoViewer.this.originalWidth, PhotoViewer.this.originalHeight));
                        text.append(TtmlNode.TAG_P);
                        text = text.toString();
                    } else if (a2 == 0) {
                        text = "240p";
                    } else if (a2 == 1) {
                        text = "360p";
                    } else if (a2 == 2) {
                        text = "480p";
                    } else {
                        text = "720p";
                        text2 = text;
                        width = r0.textPaint.measureText(text2);
                        canvas2.drawCircle((float) cx, (float) cy, (float) (a2 != PhotoViewer.this.selectedCompression ? AndroidUtilities.dp(8.0f) : r0.circleSize / 2), r0.paint);
                        canvas2.drawText(text2, ((float) cx) - (width / 2.0f), (float) (cy - AndroidUtilities.dp(16.0f)), r0.textPaint);
                        if (a2 != 0) {
                            x = ((cx - (r0.circleSize / 2)) - r0.gapSize) - r0.lineSize;
                            canvas2.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (r0.lineSize + x), (float) (AndroidUtilities.dp(2.0f) + cy), r0.paint);
                        }
                        a = a2 + 1;
                    }
                    text2 = text;
                    width = r0.textPaint.measureText(text2);
                    if (a2 != PhotoViewer.this.selectedCompression) {
                    }
                    canvas2.drawCircle((float) cx, (float) cy, (float) (a2 != PhotoViewer.this.selectedCompression ? AndroidUtilities.dp(8.0f) : r0.circleSize / 2), r0.paint);
                    canvas2.drawText(text2, ((float) cx) - (width / 2.0f), (float) (cy - AndroidUtilities.dp(16.0f)), r0.textPaint);
                    if (a2 != 0) {
                        x = ((cx - (r0.circleSize / 2)) - r0.gapSize) - r0.lineSize;
                        canvas2.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (r0.lineSize + x), (float) (AndroidUtilities.dp(2.0f) + cy), r0.paint);
                    }
                    a = a2 + 1;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$8 */
    class C22308 extends ActionBarMenuOnItemClick {
        C22308() {
        }

        public void onItemClick(int id) {
            C22308 c22308 = this;
            int i = id;
            int i2 = 1;
            if (i == -1) {
                if (PhotoViewer.this.needCaptionLayout && (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible())) {
                    PhotoViewer.this.closeCaptionEnter(false);
                    return;
                }
                PhotoViewer.this.closePhoto(true, false);
            } else if (i == 1) {
                if (VERSION.SDK_INT < 23 || PhotoViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                    File f = null;
                    if (PhotoViewer.this.currentMessageObject != null) {
                        if ((PhotoViewer.this.currentMessageObject.messageOwner.media instanceof TL_messageMediaWebPage) && PhotoViewer.this.currentMessageObject.messageOwner.media.webpage != null && PhotoViewer.this.currentMessageObject.messageOwner.media.webpage.document == null) {
                            f = FileLoader.getPathToAttach(PhotoViewer.this.getFileLocation(PhotoViewer.this.currentIndex, null), true);
                        } else {
                            f = FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
                        }
                    } else if (PhotoViewer.this.currentFileLocation != null) {
                        boolean z;
                        TLObject access$8200 = PhotoViewer.this.currentFileLocation;
                        if (PhotoViewer.this.avatarsDialogId == 0) {
                            if (!PhotoViewer.this.isEvent) {
                                z = false;
                                f = FileLoader.getPathToAttach(access$8200, z);
                            }
                        }
                        z = true;
                        f = FileLoader.getPathToAttach(access$8200, z);
                    }
                    if (f == null || !f.exists()) {
                        PhotoViewer.this.showDownloadAlert();
                    } else {
                        String file = f.toString();
                        Context access$2500 = PhotoViewer.this.parentActivity;
                        if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isVideo()) {
                            i2 = 0;
                        }
                        MediaController.saveFile(file, access$2500, i2, null, null);
                    }
                } else {
                    PhotoViewer.this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                }
            } else if (i == 2) {
                if (PhotoViewer.this.currentDialogId != 0) {
                    PhotoViewer.this.disableShowCheck = true;
                    args2 = new Bundle();
                    args2.putLong("dialog_id", PhotoViewer.this.currentDialogId);
                    MediaActivity mediaActivity = new MediaActivity(args2);
                    if (PhotoViewer.this.parentChatActivity != null) {
                        mediaActivity.setChatInfo(PhotoViewer.this.parentChatActivity.getCurrentChatInfo());
                    }
                    PhotoViewer.this.closePhoto(false, false);
                    ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(mediaActivity, false, true);
                }
            } else if (i == 4) {
                if (PhotoViewer.this.currentMessageObject != null) {
                    boolean remove;
                    args2 = new Bundle();
                    int lower_part = (int) PhotoViewer.this.currentDialogId;
                    high_id = (int) (PhotoViewer.this.currentDialogId >> 32);
                    if (lower_part == 0) {
                        args2.putInt("enc_id", high_id);
                    } else if (high_id == 1) {
                        args2.putInt("chat_id", lower_part);
                    } else if (lower_part > 0) {
                        args2.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        Chat chat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-lower_part));
                        if (!(chat == null || chat.migrated_to == null)) {
                            args2.putInt("migrated_to", lower_part);
                            lower_part = -chat.migrated_to.channel_id;
                        }
                        args2.putInt("chat_id", -lower_part);
                    }
                    args2.putInt("message_id", PhotoViewer.this.currentMessageObject.getId());
                    NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    LaunchActivity launchActivity = (LaunchActivity) PhotoViewer.this.parentActivity;
                    if (launchActivity.getMainFragmentsCount() <= 1) {
                        if (!AndroidUtilities.isTablet()) {
                            remove = false;
                            launchActivity.presentFragment(new ChatActivity(args2), remove, true);
                            PhotoViewer.this.currentMessageObject = null;
                            PhotoViewer.this.closePhoto(false, false);
                        }
                    }
                    remove = true;
                    launchActivity.presentFragment(new ChatActivity(args2), remove, true);
                    PhotoViewer.this.currentMessageObject = null;
                    PhotoViewer.this.closePhoto(false, false);
                }
            } else if (i == 3) {
                if (PhotoViewer.this.currentMessageObject != null) {
                    if (PhotoViewer.this.parentActivity != null) {
                        ((LaunchActivity) PhotoViewer.this.parentActivity).switchToAccount(PhotoViewer.this.currentMessageObject.currentAccount, true);
                        Bundle args = new Bundle();
                        args.putBoolean("onlySelect", true);
                        args.putInt("dialogsType", 3);
                        DialogsActivity fragment = new DialogsActivity(args);
                        final ArrayList<MessageObject> fmessages = new ArrayList();
                        fmessages.add(PhotoViewer.this.currentMessageObject);
                        fragment.setDelegate(new DialogsActivityDelegate() {
                            public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                                int lower_part;
                                C22291 c22291 = this;
                                ArrayList arrayList = dids;
                                int a = 0;
                                if (dids.size() <= 1 && ((Long) arrayList.get(0)).longValue() != ((long) UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId())) {
                                    if (message == null) {
                                        long did = ((Long) arrayList.get(0)).longValue();
                                        lower_part = (int) did;
                                        int high_part = (int) (did >> 32);
                                        Bundle args = new Bundle();
                                        args.putBoolean("scrollToTopOnResume", true);
                                        if (lower_part == 0) {
                                            args.putInt("enc_id", high_part);
                                        } else if (lower_part > 0) {
                                            args.putInt("user_id", lower_part);
                                        } else if (lower_part < 0) {
                                            args.putInt("chat_id", -lower_part);
                                        }
                                        NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                        ChatActivity chatActivity = new ChatActivity(args);
                                        if (((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(chatActivity, true, false)) {
                                            chatActivity.showReplyPanel(true, null, fmessages, null, false);
                                            return;
                                        }
                                        fragment.finishFragment();
                                        return;
                                    }
                                }
                                while (true) {
                                    lower_part = a;
                                    if (lower_part < dids.size()) {
                                        long did2;
                                        long did3 = ((Long) arrayList.get(lower_part)).longValue();
                                        if (message != null) {
                                            did2 = did3;
                                            SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(message.toString(), did3, null, null, true, null, null, null);
                                        } else {
                                            did2 = did3;
                                        }
                                        SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(fmessages, did2);
                                        a = lower_part + 1;
                                        ArrayList<Long> arrayList2 = dids;
                                    } else {
                                        fragment.finishFragment();
                                        return;
                                    }
                                }
                            }
                        });
                        ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(fragment, false, true);
                        PhotoViewer.this.closePhoto(false, false);
                    }
                }
            } else if (i == 6) {
                if (PhotoViewer.this.parentActivity != null) {
                    Builder builder = new Builder(PhotoViewer.this.parentActivity);
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
                        high_id = (int) PhotoViewer.this.currentMessageObject.getDialogId();
                        if (high_id != 0) {
                            User currentUser;
                            Chat currentChat;
                            if (high_id > 0) {
                                currentUser = MessagesController.getInstance(PhotoViewer.this.currentAccount).getUser(Integer.valueOf(high_id));
                                currentChat = null;
                            } else {
                                currentUser = null;
                                currentChat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-high_id));
                            }
                            if (!(currentUser == null && ChatObject.isChannel(currentChat))) {
                                int currentDate = ConnectionsManager.getInstance(PhotoViewer.this.currentAccount).getCurrentTime();
                                if (!((currentUser == null || currentUser.id == UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId()) && currentChat == null) && ((PhotoViewer.this.currentMessageObject.messageOwner.action == null || (PhotoViewer.this.currentMessageObject.messageOwner.action instanceof TL_messageActionEmpty)) && PhotoViewer.this.currentMessageObject.isOut() && currentDate - PhotoViewer.this.currentMessageObject.messageOwner.date <= 172800)) {
                                    FrameLayout frameLayout = new FrameLayout(PhotoViewer.this.parentActivity);
                                    CheckBoxCell cell = new CheckBoxCell(PhotoViewer.this.parentActivity, 1);
                                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    if (currentChat != null) {
                                        cell.setText(LocaleController.getString("DeleteForAll", R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    } else {
                                        cell.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, UserObject.getFirstName(currentUser)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    }
                                    cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                    cell.setOnClickListener(new OnClickListener() {
                                        public void onClick(View v) {
                                            CheckBoxCell cell = (CheckBoxCell) v;
                                            deleteForAll[0] = deleteForAll[0] ^ true;
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
                                if (!PhotoViewer.this.avatarsArr.isEmpty() && PhotoViewer.this.currentIndex >= 0) {
                                    if (PhotoViewer.this.currentIndex < PhotoViewer.this.avatarsArr.size()) {
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
                                            MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(null);
                                            PhotoViewer.this.closePhoto(false, false);
                                        } else if (photo != null) {
                                            TL_inputPhoto inputPhoto = new TL_inputPhoto();
                                            inputPhoto.id = photo.id;
                                            inputPhoto.access_hash = photo.access_hash;
                                            MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(inputPhoto);
                                            MessagesStorage.getInstance(PhotoViewer.this.currentAccount).clearUserPhoto(PhotoViewer.this.avatarsDialogId, photo.id);
                                            PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                                            PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                                            PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                                            if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
                                                PhotoViewer.this.closePhoto(false, false);
                                            } else {
                                                int index = PhotoViewer.this.currentIndex;
                                                if (index >= PhotoViewer.this.avatarsArr.size()) {
                                                    index = PhotoViewer.this.avatarsArr.size() - 1;
                                                }
                                                PhotoViewer.this.currentIndex = -1;
                                                PhotoViewer.this.setImageIndex(index, true);
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            if (PhotoViewer.this.currentIndex >= 0) {
                                if (PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size()) {
                                    MessageObject obj = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                                    if (obj.isSent()) {
                                        PhotoViewer.this.closePhoto(false, false);
                                        ArrayList<Integer> arr = new ArrayList();
                                        if (PhotoViewer.this.slideshowMessageId != 0) {
                                            arr.add(Integer.valueOf(PhotoViewer.this.slideshowMessageId));
                                        } else {
                                            arr.add(Integer.valueOf(obj.getId()));
                                        }
                                        ArrayList<Long> random_ids = null;
                                        EncryptedChat encryptedChat = null;
                                        if (((int) obj.getDialogId()) == 0 && obj.messageOwner.random_id != 0) {
                                            random_ids = new ArrayList();
                                            random_ids.add(Long.valueOf(obj.messageOwner.random_id));
                                            encryptedChat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (obj.getDialogId() >> 32)));
                                        }
                                        ArrayList<Long> random_ids2 = random_ids;
                                        EncryptedChat encryptedChat2 = encryptedChat;
                                        MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteMessages(arr, random_ids2, encryptedChat2, obj.messageOwner.to_id.channel_id, deleteForAll[0]);
                                    }
                                }
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    PhotoViewer.this.showAlertDialog(builder);
                }
            } else if (i == 10) {
                PhotoViewer.this.onSharePressed();
            } else if (i == 11) {
                try {
                    AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                    PhotoViewer.this.closePhoto(false, false);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } else if (i == 13) {
                if (!(PhotoViewer.this.parentActivity == null || PhotoViewer.this.currentMessageObject == null || PhotoViewer.this.currentMessageObject.messageOwner.media == null)) {
                    if (PhotoViewer.this.currentMessageObject.messageOwner.media.photo != null) {
                        new StickersAlert(PhotoViewer.this.parentActivity, PhotoViewer.this.currentMessageObject.messageOwner.media.photo).show();
                    }
                }
            } else if (i == 5) {
                if (PhotoViewer.this.pipItem.getAlpha() == 1.0f) {
                    PhotoViewer.this.switchToPip();
                }
            } else if (i == 7 && PhotoViewer.this.currentMessageObject != null) {
                FileLoader.getInstance(PhotoViewer.this.currentAccount).cancelLoadFile(PhotoViewer.this.currentMessageObject.getDocument());
                PhotoViewer.this.releasePlayer();
                PhotoViewer.this.bottomLayout.setTag(Integer.valueOf(1));
                PhotoViewer.this.bottomLayout.setVisibility(0);
            }
        }

        public boolean canOpenMenu() {
            if (PhotoViewer.this.currentMessageObject != null) {
                if (FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner).exists()) {
                    return true;
                }
            } else if (PhotoViewer.this.currentFileLocation != null) {
                boolean z;
                TLObject access$8200 = PhotoViewer.this.currentFileLocation;
                if (PhotoViewer.this.avatarsDialogId == 0) {
                    if (!PhotoViewer.this.isEvent) {
                        z = false;
                        if (FileLoader.getPathToAttach(access$8200, z).exists()) {
                        }
                    }
                }
                z = true;
                return FileLoader.getPathToAttach(access$8200, z).exists();
            }
        }
    }

    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            return null;
        }

        public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            return null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        }

        public void willHidePhotoViewer() {
        }

        public boolean isPhotoChecked(int index) {
            return false;
        }

        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            return -1;
        }

        public boolean cancelButtonPressed() {
            return true;
        }

        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
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

        public void toggleGroupPhotosEnabled() {
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return null;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return null;
        }

        public boolean canScrollAway() {
            return true;
        }

        public boolean allowGroupPhotos() {
            return true;
        }

        public void needAddMorePhotos() {
        }

        public int getPhotoIndex(int index) {
            return -1;
        }
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        private Paint paint = new Paint();

        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(null);
            this.paint.setColor(855638016);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            measureChildWithMargins(PhotoViewer.this.captionEditText, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int inputFieldHeight = PhotoViewer.this.captionEditText.getMeasuredHeight();
            int childCount = getChildCount();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < childCount) {
                    View child = getChildAt(i2);
                    if (child.getVisibility() != 8) {
                        if (child != PhotoViewer.this.captionEditText) {
                            if (child == PhotoViewer.this.aspectRatioFrameLayout) {
                                measureChildWithMargins(child, widthMeasureSpec, 0, MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0), NUM), 0);
                            } else if (!PhotoViewer.this.captionEditText.isPopupView(child)) {
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
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            FrameLayoutDrawer frameLayoutDrawer = this;
            int count = getChildCount();
            int i = 0;
            int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
            while (true) {
                int i2 = i;
                if (i2 < count) {
                    int count2;
                    View child = getChildAt(i2);
                    if (child.getVisibility() == 8) {
                        count2 = count;
                    } else {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        int i3 = (gravity & 7) & 7;
                        count2 = count;
                        if (i3 == 1) {
                            count = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                        } else if (i3 != 5) {
                            count = lp.leftMargin;
                        } else {
                            count = ((r - l) - width) - lp.rightMargin;
                        }
                        if (verticalGravity == 16) {
                            i3 = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                        } else if (verticalGravity == 48) {
                            i3 = lp.topMargin;
                        } else if (verticalGravity != 80) {
                            i3 = lp.topMargin;
                        } else {
                            i3 = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                        }
                        int childTop = i3;
                        if (child == PhotoViewer.this.mentionListView) {
                            childTop -= PhotoViewer.this.captionEditText.getMeasuredHeight();
                        } else if (PhotoViewer.this.captionEditText.isPopupView(child)) {
                            if (AndroidUtilities.isInMultiwindow) {
                                childTop = (PhotoViewer.this.captionEditText.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                            } else {
                                childTop = PhotoViewer.this.captionEditText.getBottom();
                            }
                        } else if (child == PhotoViewer.this.selectedPhotosListView) {
                            childTop = PhotoViewer.this.actionBar.getMeasuredHeight();
                        } else {
                            if (child != PhotoViewer.this.captionTextView) {
                                if (child != PhotoViewer.this.switchCaptionTextView) {
                                    if (PhotoViewer.this.hintTextView != null && child == PhotoViewer.this.hintTextView) {
                                        childTop = PhotoViewer.this.selectedPhotosListView.getBottom() + AndroidUtilities.dp(3.0f);
                                    } else if (child == PhotoViewer.this.cameraItem) {
                                        childTop = (PhotoViewer.this.pickerView.getTop() - AndroidUtilities.dp(15.0f)) - PhotoViewer.this.cameraItem.getMeasuredHeight();
                                    }
                                }
                            }
                            int offset = 0;
                            if (!PhotoViewer.this.groupedPhotosListView.currentPhotos.isEmpty()) {
                                offset = 0 + PhotoViewer.this.groupedPhotosListView.getMeasuredHeight();
                            }
                            childTop -= offset;
                        }
                        child.layout(count, childTop, count + width, childTop + height);
                    }
                    i = i2 + 1;
                    count = count2;
                } else {
                    notifyHeightChanged();
                    return;
                }
            }
        }

        protected void onDraw(Canvas canvas) {
            PhotoViewer.this.onDraw(canvas);
            if (VERSION.SDK_INT >= 21 && AndroidUtilities.statusBarHeight != 0 && PhotoViewer.this.actionBar != null) {
                this.paint.setAlpha((int) ((255.0f * PhotoViewer.this.actionBar.getAlpha()) * 0.2f));
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.statusBarHeight, this.paint);
            }
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            boolean z = true;
            if (child != PhotoViewer.this.mentionListView) {
                if (child != PhotoViewer.this.captionEditText) {
                    if (!(child == PhotoViewer.this.cameraItem || child == PhotoViewer.this.pickerView || child == PhotoViewer.this.pickerViewSendButton || child == PhotoViewer.this.captionTextView)) {
                        if (PhotoViewer.this.muteItem.getVisibility() != 0 || child != PhotoViewer.this.bottomLayout) {
                            if (child != PhotoViewer.this.checkImageView) {
                                if (child != PhotoViewer.this.photosCounterView) {
                                    if (child == PhotoViewer.this.miniProgressView) {
                                        return false;
                                    }
                                    if (child != PhotoViewer.this.aspectRatioFrameLayout || !super.drawChild(canvas, child, drawingTime)) {
                                        z = false;
                                    }
                                    return z;
                                }
                            }
                            if (PhotoViewer.this.captionEditText.getTag() != null) {
                                PhotoViewer.this.bottomTouchEnabled = false;
                                return false;
                            }
                            PhotoViewer.this.bottomTouchEnabled = true;
                            if (child != PhotoViewer.this.aspectRatioFrameLayout) {
                            }
                            z = false;
                            return z;
                        }
                    }
                    int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
                    if (!PhotoViewer.this.captionEditText.isPopupShowing() && ((!AndroidUtilities.usingHardwareInput || PhotoViewer.this.captionEditText.getTag() == null) && getKeyboardHeight() <= 0)) {
                        if (paddingBottom == 0) {
                            PhotoViewer.this.bottomTouchEnabled = true;
                            if (child != PhotoViewer.this.aspectRatioFrameLayout) {
                            }
                            z = false;
                            return z;
                        }
                    }
                    PhotoViewer.this.bottomTouchEnabled = false;
                    return false;
                }
            }
            if (!PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.captionEditText.getEmojiPadding() == 0 && ((AndroidUtilities.usingHardwareInput && PhotoViewer.this.captionEditText.getTag() == null) || getKeyboardHeight() == 0)) {
                return false;
            }
            try {
                if (child != PhotoViewer.this.aspectRatioFrameLayout) {
                }
                z = false;
                return z;
            } catch (Throwable th) {
                return true;
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.PhotoViewer$ListAdapter$1 */
        class C16161 implements OnClickListener {
            C16161() {
            }

            public void onClick(View v) {
                int idx = PhotoViewer.this.imagesArrLocals.indexOf(((View) v.getParent()).getTag());
                if (idx >= 0) {
                    int num = PhotoViewer.this.placeProvider.setPhotoChecked(idx, PhotoViewer.this.getCurrentVideoEditedInfo());
                    boolean checked = PhotoViewer.this.placeProvider.isPhotoChecked(idx);
                    if (idx == PhotoViewer.this.currentIndex) {
                        PhotoViewer.this.checkImageView.setChecked(-1, false, true);
                    }
                    if (num >= 0) {
                        if (PhotoViewer.this.placeProvider.allowGroupPhotos()) {
                            num++;
                        }
                        PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(num);
                    }
                    PhotoViewer.this.updateSelectedCount();
                }
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            if (PhotoViewer.this.placeProvider == null || PhotoViewer.this.placeProvider.getSelectedPhotosOrder() == null) {
                return 0;
            }
            if (PhotoViewer.this.placeProvider.allowGroupPhotos()) {
                return 1 + PhotoViewer.this.placeProvider.getSelectedPhotosOrder().size();
            }
            return PhotoViewer.this.placeProvider.getSelectedPhotosOrder().size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new ImageView(this.mContext) {
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0f), NUM), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), NUM));
                    }
                };
                view.setScaleType(ScaleType.CENTER);
                view.setImageResource(R.drawable.photos_group);
            } else {
                view = new PhotoPickerPhotoCell(this.mContext, false);
                view.checkFrame.setOnClickListener(new C16161());
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            ListAdapter listAdapter = this;
            ViewHolder viewHolder = holder;
            ColorFilter colorFilter = null;
            switch (holder.getItemViewType()) {
                case 0:
                    int position2;
                    PhotoPickerPhotoCell cell = viewHolder.itemView;
                    cell.itemWidth = AndroidUtilities.dp(82.0f);
                    BackupImageView imageView = cell.photoImage;
                    imageView.setOrientation(0, true);
                    ArrayList<Object> order = PhotoViewer.this.placeProvider.getSelectedPhotosOrder();
                    if (PhotoViewer.this.placeProvider.allowGroupPhotos()) {
                        position2 = position - 1;
                    } else {
                        position2 = position;
                    }
                    PhotoEntry object = PhotoViewer.this.placeProvider.getSelectedPhotos().get(order.get(position2));
                    if (object instanceof PhotoEntry) {
                        PhotoEntry photoEntry = object;
                        cell.setTag(photoEntry);
                        cell.videoInfoContainer.setVisibility(4);
                        if (photoEntry.thumbPath != null) {
                            imageView.setImage(photoEntry.thumbPath, null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                        } else if (photoEntry.path != null) {
                            imageView.setOrientation(photoEntry.orientation, true);
                            StringBuilder stringBuilder;
                            if (photoEntry.isVideo) {
                                cell.videoInfoContainer.setVisibility(0);
                                int seconds = photoEntry.duration - ((photoEntry.duration / 60) * 60);
                                cell.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)}));
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("vthumb://");
                                stringBuilder.append(photoEntry.imageId);
                                stringBuilder.append(":");
                                stringBuilder.append(photoEntry.path);
                                imageView.setImage(stringBuilder.toString(), null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                            } else {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("thumb://");
                                stringBuilder.append(photoEntry.imageId);
                                stringBuilder.append(":");
                                stringBuilder.append(photoEntry.path);
                                imageView.setImage(stringBuilder.toString(), null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                            }
                        } else {
                            imageView.setImageResource(R.drawable.nophotos);
                        }
                        cell.setChecked(-1, true, false);
                        cell.checkBox.setVisibility(0);
                        return;
                    } else if (object instanceof SearchImage) {
                        SearchImage photoEntry2 = (SearchImage) object;
                        cell.setTag(photoEntry2);
                        if (photoEntry2.thumbPath != null) {
                            imageView.setImage(photoEntry2.thumbPath, null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                        } else if (photoEntry2.thumbUrl != null && photoEntry2.thumbUrl.length() > 0) {
                            imageView.setImage(photoEntry2.thumbUrl, null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                        } else if (photoEntry2.document == null || photoEntry2.document.thumb == null) {
                            imageView.setImageResource(R.drawable.nophotos);
                        } else {
                            imageView.setImage(photoEntry2.document.thumb.location, null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                        }
                        cell.videoInfoContainer.setVisibility(4);
                        cell.setChecked(-1, true, false);
                        cell.checkBox.setVisibility(0);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    ImageView imageView2 = viewHolder.itemView;
                    if (SharedConfig.groupPhotosEnabled) {
                        colorFilter = new PorterDuffColorFilter(-10043398, Mode.MULTIPLY);
                    }
                    imageView2.setColorFilter(colorFilter);
                    break;
                default:
                    break;
            }
        }

        public int getItemViewType(int i) {
            if (i == 0 && PhotoViewer.this.placeProvider.allowGroupPhotos()) {
                return 1;
            }
            return 0;
        }
    }

    public static PhotoViewer getPipInstance() {
        return PipInstance;
    }

    public static PhotoViewer getInstance() {
        PhotoViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    PhotoViewer photoViewer = new PhotoViewer();
                    localInstance = photoViewer;
                    Instance = photoViewer;
                }
            }
        }
        return localInstance;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public PhotoViewer() {
        this.blackPaint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        PhotoViewer photoViewer = this;
        int i = id;
        int i2 = 3;
        boolean z = true;
        int a = 0;
        String location;
        if (i == NotificationCenter.FileDidFailedLoad) {
            location = args[0];
            while (a < 3) {
                if (photoViewer.currentFileNames[a] != null && photoViewer.currentFileNames[a].equals(location)) {
                    photoViewer.photoProgressViews[a].setProgress(1.0f, true);
                    checkProgress(a, true);
                    break;
                }
                a++;
            }
        } else if (i == NotificationCenter.FileDidLoaded) {
            location = (String) args[0];
            a = 0;
            while (a < 3) {
                if (photoViewer.currentFileNames[a] == null || !photoViewer.currentFileNames[a].equals(location)) {
                    a++;
                } else {
                    photoViewer.photoProgressViews[a].setProgress(1.0f, true);
                    checkProgress(a, true);
                    if (photoViewer.videoPlayer == null && a == 0 && ((photoViewer.currentMessageObject != null && photoViewer.currentMessageObject.isVideo()) || (photoViewer.currentBotInlineResult != null && (photoViewer.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(photoViewer.currentBotInlineResult.document))))) {
                        onActionClick(false);
                    }
                    if (a == 0 && photoViewer.videoPlayer != null) {
                        photoViewer.currentVideoFinishedLoading = true;
                    }
                }
            }
        } else if (i == NotificationCenter.FileLoadProgressChanged) {
            location = (String) args[0];
            int a2 = 0;
            while (a2 < i2) {
                if (photoViewer.currentFileNames[a2] != null && photoViewer.currentFileNames[a2].equals(location)) {
                    Float loadProgress = args[z];
                    photoViewer.photoProgressViews[a2].setProgress(loadProgress.floatValue(), z);
                    if (!(a2 != 0 || photoViewer.videoPlayer == null || photoViewer.videoPlayerSeekbar == null)) {
                        float bufferedProgress;
                        float progress;
                        if (photoViewer.currentVideoFinishedLoading) {
                            bufferedProgress = 1.0f;
                        } else {
                            long newTime = SystemClock.uptimeMillis();
                            if (Math.abs(newTime - photoViewer.lastBufferedPositionCheck) >= 500) {
                                long newTime2;
                                float progress2 = 0.0f;
                                if (photoViewer.seekToProgressPending == 0.0f) {
                                    long duration = photoViewer.videoPlayer.getDuration();
                                    newTime2 = newTime;
                                    long position = photoViewer.videoPlayer.getCurrentPosition();
                                    if (duration >= 0 && duration != C0539C.TIME_UNSET && position >= 0) {
                                        progress2 = ((float) position) / ((float) duration);
                                    }
                                    progress = progress2;
                                } else {
                                    newTime2 = newTime;
                                    progress = photoViewer.seekToProgressPending;
                                }
                                float bufferedProgress2 = photoViewer.isStreaming ? FileLoader.getInstance(photoViewer.currentAccount).getBufferedProgressFromPosition(progress, photoViewer.currentFileNames[0]) : 1.0f;
                                photoViewer.lastBufferedPositionCheck = newTime2;
                                bufferedProgress = bufferedProgress2;
                            } else {
                                bufferedProgress = -1.0f;
                            }
                        }
                        progress = bufferedProgress;
                        if (progress != -1.0f) {
                            photoViewer.videoPlayerSeekbar.setBufferedProgress(progress);
                            if (photoViewer.pipVideoView != null) {
                                photoViewer.pipVideoView.setBufferedProgress(progress);
                            }
                            photoViewer.videoPlayerControlFrameLayout.invalidate();
                        }
                        checkBufferedProgress(loadProgress.floatValue());
                    }
                }
                a2++;
                i2 = 3;
                z = true;
            }
        } else {
            int i3 = -1;
            int guid;
            if (i == NotificationCenter.dialogPhotosLoaded) {
                guid = ((Integer) args[3]).intValue();
                if (photoViewer.avatarsDialogId == ((Integer) args[0]).intValue() && photoViewer.classGuid == guid) {
                    boolean fromCache = ((Boolean) args[2]).booleanValue();
                    ArrayList<Photo> photos = args[4];
                    if (!photos.isEmpty()) {
                        ArrayList<Photo> photos2;
                        photoViewer.imagesArrLocations.clear();
                        photoViewer.imagesArrLocationsSizes.clear();
                        photoViewer.avatarsArr.clear();
                        int setToImage = -1;
                        a = 0;
                        while (a < photos.size()) {
                            int guid2;
                            Photo photo = (Photo) photos.get(a);
                            if (!(photo == null || (photo instanceof TL_photoEmpty))) {
                                if (photo.sizes == null) {
                                    guid2 = guid;
                                    photos2 = photos;
                                } else {
                                    PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 640);
                                    if (sizeFull != null) {
                                        if (setToImage == i3 && photoViewer.currentFileLocation != null) {
                                            int b = 0;
                                            while (b < photo.sizes.size()) {
                                                PhotoSize size = (PhotoSize) photo.sizes.get(b);
                                                if (size.location.local_id == photoViewer.currentFileLocation.local_id) {
                                                    guid2 = guid;
                                                    photos2 = photos;
                                                    if (size.location.volume_id == photoViewer.currentFileLocation.volume_id) {
                                                        setToImage = photoViewer.imagesArrLocations.size();
                                                        break;
                                                    }
                                                } else {
                                                    guid2 = guid;
                                                    photos2 = photos;
                                                }
                                                b++;
                                                guid = guid2;
                                                photos = photos2;
                                            }
                                        }
                                        guid2 = guid;
                                        photos2 = photos;
                                        photoViewer.imagesArrLocations.add(sizeFull.location);
                                        photoViewer.imagesArrLocationsSizes.add(Integer.valueOf(sizeFull.size));
                                        photoViewer.avatarsArr.add(photo);
                                    }
                                }
                                a++;
                                guid = guid2;
                                photos = photos2;
                                i3 = -1;
                            }
                            guid2 = guid;
                            photos2 = photos;
                            a++;
                            guid = guid2;
                            photos = photos2;
                            i3 = -1;
                        }
                        photos2 = photos;
                        if (photoViewer.avatarsArr.isEmpty()) {
                            photoViewer.menuItem.hideSubItem(6);
                        } else {
                            photoViewer.menuItem.showSubItem(6);
                        }
                        photoViewer.needSearchImageInArr = false;
                        photoViewer.currentIndex = -1;
                        if (setToImage != -1) {
                            setImageIndex(setToImage, true);
                        } else {
                            photoViewer.avatarsArr.add(0, new TL_photoEmpty());
                            photoViewer.imagesArrLocations.add(0, photoViewer.currentFileLocation);
                            photoViewer.imagesArrLocationsSizes.add(0, Integer.valueOf(0));
                            setImageIndex(0, true);
                        }
                        if (fromCache) {
                            MessagesController.getInstance(photoViewer.currentAccount).loadDialogPhotos(photoViewer.avatarsDialogId, 80, 0, false, photoViewer.classGuid);
                        }
                    }
                }
            } else if (i == NotificationCenter.mediaCountDidLoaded) {
                uid = ((Long) args[0]).longValue();
                if (uid == photoViewer.currentDialogId || uid == photoViewer.mergeDialogId) {
                    if (uid == photoViewer.currentDialogId) {
                        photoViewer.totalImagesCount = ((Integer) args[1]).intValue();
                    } else if (uid == photoViewer.mergeDialogId) {
                        photoViewer.totalImagesCountMerge = ((Integer) args[1]).intValue();
                    }
                    if (photoViewer.needSearchImageInArr && photoViewer.isFirstLoading) {
                        photoViewer.isFirstLoading = false;
                        photoViewer.loadingMoreImages = true;
                        DataQuery.getInstance(photoViewer.currentAccount).loadMedia(photoViewer.currentDialogId, 80, 0, 0, true, photoViewer.classGuid);
                    } else if (!photoViewer.imagesArr.isEmpty()) {
                        if (photoViewer.opennedFromMedia) {
                            photoViewer.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(photoViewer.currentIndex + 1), Integer.valueOf(photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge)));
                        } else {
                            photoViewer.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf((((photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge) - photoViewer.imagesArr.size()) + photoViewer.currentIndex) + 1), Integer.valueOf(photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge)));
                        }
                    }
                }
            } else if (i == NotificationCenter.mediaDidLoaded) {
                uid = ((Long) args[0]).longValue();
                i3 = ((Integer) args[3]).intValue();
                if ((uid == photoViewer.currentDialogId || uid == photoViewer.mergeDialogId) && i3 == photoViewer.classGuid) {
                    photoViewer.loadingMoreImages = false;
                    a = uid == photoViewer.currentDialogId ? 0 : 1;
                    ArrayList<MessageObject> arr = args[2];
                    photoViewer.endReached[a] = ((Boolean) args[5]).booleanValue();
                    if (!photoViewer.needSearchImageInArr) {
                        guid = 0;
                        Iterator it = arr.iterator();
                        while (it.hasNext()) {
                            message = (MessageObject) it.next();
                            if (photoViewer.imagesByIds[a].indexOfKey(message.getId()) < 0) {
                                guid++;
                                if (photoViewer.opennedFromMedia) {
                                    photoViewer.imagesArr.add(message);
                                } else {
                                    photoViewer.imagesArr.add(0, message);
                                }
                                photoViewer.imagesByIds[a].put(message.getId(), message);
                            }
                        }
                        if (photoViewer.opennedFromMedia) {
                            if (guid == 0) {
                                photoViewer.totalImagesCount = photoViewer.imagesArr.size();
                                photoViewer.totalImagesCountMerge = 0;
                            }
                        } else if (guid != 0) {
                            int index = photoViewer.currentIndex;
                            photoViewer.currentIndex = -1;
                            setImageIndex(index + guid, true);
                        } else {
                            photoViewer.totalImagesCount = photoViewer.imagesArr.size();
                            photoViewer.totalImagesCountMerge = 0;
                        }
                    } else if (!arr.isEmpty() || (a == 0 && photoViewer.mergeDialogId != 0)) {
                        MessageObject currentMessage = (MessageObject) photoViewer.imagesArr.get(photoViewer.currentIndex);
                        int added = 0;
                        int foundIndex = -1;
                        for (int a3 = 0; a3 < arr.size(); a3++) {
                            MessageObject message = (MessageObject) arr.get(a3);
                            if (photoViewer.imagesByIdsTemp[a].indexOfKey(message.getId()) < 0) {
                                photoViewer.imagesByIdsTemp[a].put(message.getId(), message);
                                if (photoViewer.opennedFromMedia) {
                                    photoViewer.imagesArrTemp.add(message);
                                    if (message.getId() == currentMessage.getId()) {
                                        foundIndex = added;
                                    }
                                    added++;
                                } else {
                                    added++;
                                    photoViewer.imagesArrTemp.add(0, message);
                                    if (message.getId() == currentMessage.getId()) {
                                        foundIndex = arr.size() - added;
                                    }
                                }
                            }
                        }
                        if (added == 0 && (a != 0 || photoViewer.mergeDialogId == 0)) {
                            photoViewer.totalImagesCount = photoViewer.imagesArr.size();
                            photoViewer.totalImagesCountMerge = 0;
                        }
                        if (foundIndex != -1) {
                            boolean z2;
                            photoViewer.imagesArr.clear();
                            photoViewer.imagesArr.addAll(photoViewer.imagesArrTemp);
                            for (i2 = 0; i2 < 2; i2++) {
                                photoViewer.imagesByIds[i2] = photoViewer.imagesByIdsTemp[i2].clone();
                                photoViewer.imagesByIdsTemp[i2].clear();
                            }
                            photoViewer.imagesArrTemp.clear();
                            photoViewer.needSearchImageInArr = false;
                            photoViewer.currentIndex = -1;
                            if (foundIndex >= photoViewer.imagesArr.size()) {
                                z2 = true;
                                foundIndex = photoViewer.imagesArr.size() - 1;
                            } else {
                                z2 = true;
                            }
                            setImageIndex(foundIndex, z2);
                            long j = uid;
                        } else {
                            if (photoViewer.opennedFromMedia) {
                                i2 = photoViewer.imagesArrTemp.isEmpty() ? 0 : ((MessageObject) photoViewer.imagesArrTemp.get(photoViewer.imagesArrTemp.size() - 1)).getId();
                                if (a == 0 && photoViewer.endReached[a] && photoViewer.mergeDialogId != 0) {
                                    a = 1;
                                    if (!photoViewer.imagesArrTemp.isEmpty()) {
                                        if (((MessageObject) photoViewer.imagesArrTemp.get(photoViewer.imagesArrTemp.size() - 1)).getDialogId() != photoViewer.mergeDialogId) {
                                            i2 = 0;
                                        }
                                    }
                                }
                            } else {
                                i2 = photoViewer.imagesArrTemp.isEmpty() != null ? null : ((MessageObject) photoViewer.imagesArrTemp.get(0)).getId();
                                if (!(a != 0 || photoViewer.endReached[a] == null || photoViewer.mergeDialogId == 0)) {
                                    int i4;
                                    if (photoViewer.imagesArrTemp.isEmpty()) {
                                        i4 = 1;
                                    } else {
                                        i4 = 1;
                                        if (((MessageObject) photoViewer.imagesArrTemp.get(0)).getDialogId() != photoViewer.mergeDialogId) {
                                            i2 = 0;
                                        }
                                    }
                                    a = i4;
                                }
                            }
                            if (photoViewer.endReached[a] == null) {
                                photoViewer.loadingMoreImages = 1;
                                if (photoViewer.opennedFromMedia != null) {
                                    DataQuery.getInstance(photoViewer.currentAccount).loadMedia(a == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i2, 0, true, photoViewer.classGuid);
                                } else {
                                    DataQuery.getInstance(photoViewer.currentAccount).loadMedia(a == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i2, 0, true, photoViewer.classGuid);
                                }
                            }
                        }
                    } else {
                        photoViewer.needSearchImageInArr = false;
                    }
                }
            } else if (i == NotificationCenter.emojiDidLoaded) {
                if (photoViewer.captionTextView != null) {
                    photoViewer.captionTextView.invalidate();
                }
            } else if (i == NotificationCenter.FilePreparingFailed) {
                message = args[0];
                if (photoViewer.loadInitialVideo) {
                    photoViewer.loadInitialVideo = false;
                    photoViewer.progressView.setVisibility(4);
                    preparePlayer(photoViewer.currentPlayingVideoFile, false, false);
                } else if (photoViewer.tryStartRequestPreviewOnFinish) {
                    releasePlayer();
                    photoViewer.tryStartRequestPreviewOnFinish = MediaController.getInstance().scheduleVideoConvert(photoViewer.videoPreviewMessageObject, true) ^ true;
                } else if (message == photoViewer.videoPreviewMessageObject) {
                    photoViewer.requestingPreview = false;
                    photoViewer.progressView.setVisibility(4);
                }
            } else if (i == NotificationCenter.FileNewChunkAvailable && args[0] == photoViewer.videoPreviewMessageObject) {
                String finalPath = args[1];
                if (((Long) args[3]).longValue() != 0) {
                    photoViewer.requestingPreview = false;
                    photoViewer.progressView.setVisibility(4);
                    preparePlayer(Uri.fromFile(new File(finalPath)), false, true);
                }
            }
        }
    }

    private void showDownloadAlert() {
        Builder builder = new Builder(this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        boolean z = false;
        if (this.currentMessageObject != null && this.currentMessageObject.isVideo() && FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingFile(this.currentFileNames[0])) {
            z = true;
        }
        if (z) {
            builder.setMessage(LocaleController.getString("PleaseStreamDownload", R.string.PleaseStreamDownload));
        } else {
            builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
        }
        showAlertDialog(builder);
    }

    private void onSharePressed() {
        if (this.parentActivity != null) {
            if (this.allowShare) {
                File f = null;
                boolean isVideo = false;
                try {
                    if (this.currentMessageObject != null) {
                        isVideo = this.currentMessageObject.isVideo();
                        if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                            f = new File(this.currentMessageObject.messageOwner.attachPath);
                            if (!f.exists()) {
                                f = null;
                            }
                        }
                        if (f == null) {
                            f = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
                        }
                    } else if (this.currentFileLocation != null) {
                        boolean z;
                        TLObject tLObject = this.currentFileLocation;
                        if (this.avatarsDialogId == 0) {
                            if (!this.isEvent) {
                                z = false;
                                f = FileLoader.getPathToAttach(tLObject, z);
                            }
                        }
                        z = true;
                        f = FileLoader.getPathToAttach(tLObject, z);
                    }
                    if (f.exists()) {
                        Intent intent = new Intent("android.intent.action.SEND");
                        if (isVideo) {
                            intent.setType(MimeTypes.VIDEO_MP4);
                        } else if (this.currentMessageObject != null) {
                            intent.setType(this.currentMessageObject.getMimeType());
                        } else {
                            intent.setType("image/jpeg");
                        }
                        if (VERSION.SDK_INT >= 24) {
                            try {
                                intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this.parentActivity, "org.telegram.messenger.beta.provider", f));
                                intent.setFlags(1);
                            } catch (Exception e) {
                                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                            }
                        } else {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                        }
                        this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                    } else {
                        showDownloadAlert();
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
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
        Context context = activity;
        this.currentAccount = UserConfig.selectedAccount;
        this.centerImage.setCurrentAccount(this.currentAccount);
        this.leftImage.setCurrentAccount(this.currentAccount);
        this.rightImage.setCurrentAccount(this.currentAccount);
        if (this.parentActivity != context) {
            float f;
            FrameLayout.LayoutParams layoutParams;
            float f2;
            FrameLayout.LayoutParams layoutParams2;
            RecyclerListView recyclerListView;
            Adapter listAdapter;
            Adapter mentionsAdapter;
            r0.parentActivity = context;
            r0.actvityContext = new ContextThemeWrapper(r0.parentActivity, R.style.Theme.TMessages);
            if (progressDrawables == null) {
                progressDrawables = new Drawable[4];
                progressDrawables[0] = r0.parentActivity.getResources().getDrawable(R.drawable.circle_big);
                progressDrawables[1] = r0.parentActivity.getResources().getDrawable(R.drawable.cancel_big);
                progressDrawables[2] = r0.parentActivity.getResources().getDrawable(R.drawable.load_big);
                progressDrawables[3] = r0.parentActivity.getResources().getDrawable(R.drawable.play_big);
            }
            r0.scroller = new Scroller(context);
            r0.windowView = new FrameLayout(context) {
                private Runnable attachRunnable;

                /* renamed from: org.telegram.ui.PhotoViewer$5$1 */
                class C15991 implements Runnable {
                    C15991() {
                    }

                    public void run() {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.checkImageView.getLayoutParams();
                        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                        int i = 0;
                        layoutParams.topMargin = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(40.0f)) / 2) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                        PhotoViewer.this.checkImageView.setLayoutParams(layoutParams);
                        layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.photosCounterView.getLayoutParams();
                        int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(40.0f)) / 2;
                        if (VERSION.SDK_INT >= 21) {
                            i = AndroidUtilities.statusBarHeight;
                        }
                        layoutParams.topMargin = currentActionBarHeight + i;
                        PhotoViewer.this.photosCounterView.setLayoutParams(layoutParams);
                    }
                }

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
                            PhotoViewer.this.checkImageView.post(new C15991());
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
                    if (!PhotoViewer.this.captionEditText.isPopupShowing()) {
                        if (!PhotoViewer.this.captionEditText.isKeyboardVisible()) {
                            PhotoViewer.getInstance().closePhoto(true, false);
                            return true;
                        }
                    }
                    PhotoViewer.this.closeCaptionEnter(false);
                    return false;
                }

                public ActionMode startActionModeForChild(View originalView, Callback callback, int type) {
                    if (VERSION.SDK_INT >= 23) {
                        View view = PhotoViewer.this.parentActivity.findViewById(16908290);
                        if (view instanceof ViewGroup) {
                            try {
                                return ((ViewGroup) view).startActionModeForChild(originalView, callback, type);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    }
                    return super.startActionModeForChild(originalView, callback, type);
                }
            };
            r0.windowView.setBackgroundDrawable(r0.backgroundDrawable);
            r0.windowView.setClipChildren(true);
            r0.windowView.setFocusable(false);
            r0.animatingImageView = new ClippingImageView(context);
            r0.animatingImageView.setAnimationValues(r0.animationValues);
            r0.windowView.addView(r0.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
            r0.containerView = new FrameLayoutDrawer(context);
            r0.containerView.setFocusable(false);
            r0.windowView.addView(r0.containerView, LayoutHelper.createFrame(-1, -1, 51));
            if (VERSION.SDK_INT >= 21) {
                r0.containerView.setFitsSystemWindows(true);
                r0.containerView.setOnApplyWindowInsetsListener(new C16086());
                r0.containerView.setSystemUiVisibility(1280);
            }
            r0.windowLayoutParams = new LayoutParams();
            r0.windowLayoutParams.height = -1;
            r0.windowLayoutParams.format = -3;
            r0.windowLayoutParams.width = -1;
            r0.windowLayoutParams.gravity = 51;
            r0.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                r0.windowLayoutParams.flags = -NUM;
            } else {
                r0.windowLayoutParams.flags = 8;
            }
            r0.actionBar = new ActionBar(context) {
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    PhotoViewer.this.containerView.invalidate();
                }
            };
            r0.actionBar.setTitleColor(-1);
            r0.actionBar.setSubtitleColor(-1);
            r0.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.actionBar.setOccupyStatusBar(VERSION.SDK_INT >= 21);
            r0.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
            r0.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            r0.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(1), Integer.valueOf(1)));
            r0.containerView.addView(r0.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            r0.actionBar.setActionBarMenuOnItemClick(new C22308());
            ActionBarMenu menu = r0.actionBar.createMenu();
            r0.masksItem = menu.addItem(13, (int) R.drawable.ic_masks_msk1);
            r0.pipItem = menu.addItem(5, (int) R.drawable.ic_goinline);
            r0.sendItem = menu.addItem(3, (int) R.drawable.msg_panel_reply);
            r0.menuItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
            r0.menuItem.addSubItem(11, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp)).setTextColor(-328966);
            r0.menuItem.addSubItem(2, LocaleController.getString("ShowAllMedia", R.string.ShowAllMedia)).setTextColor(-328966);
            r0.menuItem.addSubItem(4, LocaleController.getString("ShowInChat", R.string.ShowInChat)).setTextColor(-328966);
            r0.menuItem.addSubItem(10, LocaleController.getString("ShareFile", R.string.ShareFile)).setTextColor(-328966);
            r0.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", R.string.SaveToGallery)).setTextColor(-328966);
            r0.menuItem.addSubItem(6, LocaleController.getString("Delete", R.string.Delete)).setTextColor(-328966);
            r0.menuItem.addSubItem(7, LocaleController.getString("StopDownload", R.string.StopDownload)).setTextColor(-328966);
            r0.menuItem.redrawPopup(-115203550);
            r0.bottomLayout = new FrameLayout(r0.actvityContext);
            r0.bottomLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.containerView.addView(r0.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            r0.groupedPhotosListView = new GroupedPhotosListView(r0.actvityContext);
            r0.containerView.addView(r0.groupedPhotosListView, LayoutHelper.createFrame(-1, 62.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            r0.captionTextView = createCaptionTextView();
            r0.switchCaptionTextView = createCaptionTextView();
            for (int a = 0; a < 3; a++) {
                r0.photoProgressViews[a] = new PhotoProgressView(r0.containerView.getContext(), r0.containerView);
                r0.photoProgressViews[a].setBackgroundState(0, false);
            }
            r0.miniProgressView = new RadialProgressView(r0.actvityContext) {
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    if (PhotoViewer.this.containerView != null) {
                        PhotoViewer.this.containerView.invalidate();
                    }
                }

                public void invalidate() {
                    super.invalidate();
                    if (PhotoViewer.this.containerView != null) {
                        PhotoViewer.this.containerView.invalidate();
                    }
                }
            };
            r0.miniProgressView.setUseSelfAlpha(true);
            r0.miniProgressView.setProgressColor(-1);
            r0.miniProgressView.setSize(AndroidUtilities.dp(54.0f));
            r0.miniProgressView.setBackgroundResource(R.drawable.circle_big);
            r0.miniProgressView.setVisibility(4);
            r0.miniProgressView.setAlpha(0.0f);
            r0.containerView.addView(r0.miniProgressView, LayoutHelper.createFrame(64, 64, 17));
            r0.shareButton = new ImageView(r0.containerView.getContext());
            r0.shareButton.setImageResource(R.drawable.share);
            r0.shareButton.setScaleType(ScaleType.CENTER);
            r0.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.bottomLayout.addView(r0.shareButton, LayoutHelper.createFrame(50, -1, 53));
            r0.shareButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PhotoViewer.this.onSharePressed();
                }
            });
            r0.nameTextView = new TextView(r0.containerView.getContext());
            r0.nameTextView.setTextSize(1, 14.0f);
            r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r0.nameTextView.setSingleLine(true);
            r0.nameTextView.setMaxLines(1);
            r0.nameTextView.setEllipsize(TruncateAt.END);
            r0.nameTextView.setTextColor(-1);
            r0.nameTextView.setGravity(3);
            r0.bottomLayout.addView(r0.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 5.0f, 60.0f, 0.0f));
            r0.dateTextView = new TextView(r0.containerView.getContext());
            r0.dateTextView.setTextSize(1, 13.0f);
            r0.dateTextView.setSingleLine(true);
            r0.dateTextView.setMaxLines(1);
            r0.dateTextView.setEllipsize(TruncateAt.END);
            r0.dateTextView.setTextColor(-1);
            r0.dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r0.dateTextView.setGravity(3);
            r0.bottomLayout.addView(r0.dateTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 25.0f, 50.0f, 0.0f));
            createVideoControlsInterface();
            r0.progressView = new RadialProgressView(r0.parentActivity);
            r0.progressView.setProgressColor(-1);
            r0.progressView.setBackgroundResource(R.drawable.circle_big);
            r0.progressView.setVisibility(4);
            r0.containerView.addView(r0.progressView, LayoutHelper.createFrame(54, 54, 17));
            r0.qualityPicker = new PickerBottomLayoutViewer(r0.parentActivity);
            r0.qualityPicker.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.qualityPicker.updateSelectedCount(0, false);
            r0.qualityPicker.setTranslationY((float) AndroidUtilities.dp(120.0f));
            r0.qualityPicker.doneButton.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
            r0.containerView.addView(r0.qualityPicker, LayoutHelper.createFrame(-1, 48, 83));
            r0.qualityPicker.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    PhotoViewer.this.selectedCompression = PhotoViewer.this.previousCompression;
                    PhotoViewer.this.didChangedCompressionLevel(false);
                    PhotoViewer.this.showQualityView(false);
                    PhotoViewer.this.requestVideoPreview(2);
                }
            });
            r0.qualityPicker.doneButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    PhotoViewer.this.showQualityView(false);
                    PhotoViewer.this.requestVideoPreview(2);
                }
            });
            r0.qualityChooseView = new QualityChooseView(r0.parentActivity);
            r0.qualityChooseView.setTranslationY((float) AndroidUtilities.dp(120.0f));
            r0.qualityChooseView.setVisibility(4);
            r0.qualityChooseView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.containerView.addView(r0.qualityChooseView, LayoutHelper.createFrame(-1, 70.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            r0.pickerView = new FrameLayout(r0.actvityContext) {
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
            r0.pickerView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.containerView.addView(r0.pickerView, LayoutHelper.createFrame(-1, -2, 83));
            r0.videoTimelineView = new VideoTimelinePlayView(r0.parentActivity);
            r0.videoTimelineView.setDelegate(new VideoTimelineViewDelegate() {
                public void onLeftProgressChanged(float progress) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        if (PhotoViewer.this.videoPlayer.isPlaying()) {
                            PhotoViewer.this.videoPlayer.pause();
                            PhotoViewer.this.containerView.invalidate();
                        }
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * progress)));
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                        PhotoViewer.this.updateVideoInfo();
                    }
                }

                public void onRightProgressChanged(float progress) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        if (PhotoViewer.this.videoPlayer.isPlaying()) {
                            PhotoViewer.this.videoPlayer.pause();
                            PhotoViewer.this.containerView.invalidate();
                        }
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * progress)));
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                        PhotoViewer.this.updateVideoInfo();
                    }
                }

                public void onPlayProgressChanged(float progress) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * progress)));
                    }
                }

                public void didStartDragging() {
                }

                public void didStopDragging() {
                }
            });
            r0.pickerView.addView(r0.videoTimelineView, LayoutHelper.createFrame(-1, 58.0f, 51, 0.0f, 8.0f, 0.0f, 88.0f));
            r0.pickerViewSendButton = new ImageView(r0.parentActivity);
            r0.pickerViewSendButton.setScaleType(ScaleType.CENTER);
            r0.pickerViewSendButton.setBackgroundDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), -10043398, -10043398));
            r0.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
            r0.pickerViewSendButton.setPadding(AndroidUtilities.dp(4.0f), 0, 0, 0);
            r0.pickerViewSendButton.setImageResource(R.drawable.ic_send);
            r0.containerView.addView(r0.pickerViewSendButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 14.0f, 14.0f));
            r0.pickerViewSendButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (!(PhotoViewer.this.captionEditText.getTag() != null || PhotoViewer.this.placeProvider == null || PhotoViewer.this.doneButtonPressed)) {
                        PhotoViewer.this.placeProvider.sendButtonPressed(PhotoViewer.this.currentIndex, PhotoViewer.this.getCurrentVideoEditedInfo());
                        PhotoViewer.this.doneButtonPressed = true;
                        PhotoViewer.this.closePhoto(false, false);
                    }
                }
            });
            r0.itemsLayout = new LinearLayout(r0.parentActivity);
            r0.itemsLayout.setOrientation(0);
            r0.pickerView.addView(r0.itemsLayout, LayoutHelper.createFrame(-2, 48.0f, 81, 0.0f, 0.0f, 34.0f, 0.0f));
            r0.cropItem = new ImageView(r0.parentActivity);
            r0.cropItem.setScaleType(ScaleType.CENTER);
            r0.cropItem.setImageResource(R.drawable.photo_crop);
            r0.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.itemsLayout.addView(r0.cropItem, LayoutHelper.createLinear(70, 48));
            r0.cropItem.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (PhotoViewer.this.captionEditText.getTag() == null) {
                        PhotoViewer.this.switchToEditMode(1);
                    }
                }
            });
            r0.paintItem = new ImageView(r0.parentActivity);
            r0.paintItem.setScaleType(ScaleType.CENTER);
            r0.paintItem.setImageResource(R.drawable.photo_paint);
            r0.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.itemsLayout.addView(r0.paintItem, LayoutHelper.createLinear(70, 48));
            r0.paintItem.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (PhotoViewer.this.captionEditText.getTag() == null) {
                        PhotoViewer.this.switchToEditMode(3);
                    }
                }
            });
            r0.compressItem = new ImageView(r0.parentActivity);
            r0.compressItem.setTag(Integer.valueOf(1));
            r0.compressItem.setScaleType(ScaleType.CENTER);
            r0.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.selectedCompression = MessagesController.getGlobalMainSettings().getInt("compress_video2", 1);
            if (r0.selectedCompression <= 0) {
                r0.compressItem.setImageResource(R.drawable.video_240);
            } else if (r0.selectedCompression == 1) {
                r0.compressItem.setImageResource(R.drawable.video_360);
            } else if (r0.selectedCompression == 2) {
                r0.compressItem.setImageResource(R.drawable.video_480);
            } else if (r0.selectedCompression == 3) {
                r0.compressItem.setImageResource(R.drawable.video_720);
            } else if (r0.selectedCompression == 4) {
                r0.compressItem.setImageResource(R.drawable.video_1080);
            }
            r0.itemsLayout.addView(r0.compressItem, LayoutHelper.createLinear(70, 48));
            r0.compressItem.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (PhotoViewer.this.captionEditText.getTag() == null) {
                        PhotoViewer.this.showQualityView(true);
                        PhotoViewer.this.requestVideoPreview(1);
                    }
                }
            });
            r0.muteItem = new ImageView(r0.parentActivity);
            r0.muteItem.setScaleType(ScaleType.CENTER);
            r0.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.itemsLayout.addView(r0.muteItem, LayoutHelper.createLinear(70, 48));
            r0.muteItem.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (PhotoViewer.this.captionEditText.getTag() == null) {
                        PhotoViewer.this.muteVideo = PhotoViewer.this.muteVideo ^ 1;
                        if (!PhotoViewer.this.muteVideo || PhotoViewer.this.checkImageView.isChecked()) {
                            Object object = PhotoViewer.this.imagesArrLocals.get(PhotoViewer.this.currentIndex);
                            if (object instanceof PhotoEntry) {
                                ((PhotoEntry) object).editedInfo = PhotoViewer.this.getCurrentVideoEditedInfo();
                            }
                        } else {
                            PhotoViewer.this.checkImageView.callOnClick();
                        }
                        PhotoViewer.this.updateMuteButton();
                    }
                }
            });
            r0.cameraItem = new ImageView(r0.parentActivity);
            r0.cameraItem.setScaleType(ScaleType.CENTER);
            r0.cameraItem.setImageResource(R.drawable.photo_add);
            r0.cameraItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.containerView.addView(r0.cameraItem, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 16.0f, 0.0f));
            r0.cameraItem.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (PhotoViewer.this.placeProvider != null) {
                        if (PhotoViewer.this.captionEditText.getTag() == null) {
                            PhotoViewer.this.placeProvider.needAddMorePhotos();
                            PhotoViewer.this.closePhoto(true, false);
                        }
                    }
                }
            });
            r0.tuneItem = new ImageView(r0.parentActivity);
            r0.tuneItem.setScaleType(ScaleType.CENTER);
            r0.tuneItem.setImageResource(R.drawable.photo_tools);
            r0.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.itemsLayout.addView(r0.tuneItem, LayoutHelper.createLinear(70, 48));
            r0.tuneItem.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (PhotoViewer.this.captionEditText.getTag() == null) {
                        PhotoViewer.this.switchToEditMode(2);
                    }
                }
            });
            r0.timeItem = new ImageView(r0.parentActivity);
            r0.timeItem.setScaleType(ScaleType.CENTER);
            r0.timeItem.setImageResource(R.drawable.photo_timer);
            r0.timeItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.itemsLayout.addView(r0.timeItem, LayoutHelper.createLinear(70, 48));
            r0.timeItem.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.PhotoViewer$22$1 */
                class C15841 implements OnTouchListener {
                    C15841() {
                    }

                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                }

                /* renamed from: org.telegram.ui.PhotoViewer$22$2 */
                class C15852 implements OnTouchListener {
                    C15852() {
                    }

                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                }

                /* renamed from: org.telegram.ui.PhotoViewer$22$3 */
                class C22273 implements Formatter {
                    C22273() {
                    }

                    public String format(int value) {
                        if (value == 0) {
                            return LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
                        }
                        if (value < 1 || value >= 21) {
                            return LocaleController.formatTTLString((value - 16) * 5);
                        }
                        return LocaleController.formatTTLString(value);
                    }
                }

                public void onClick(View v) {
                    if (PhotoViewer.this.parentActivity != null) {
                        if (PhotoViewer.this.captionEditText.getTag() == null) {
                            String str;
                            int i;
                            FrameLayout buttonsLayout;
                            TextView textView;
                            BottomSheet.Builder builder = new BottomSheet.Builder(PhotoViewer.this.parentActivity);
                            builder.setUseHardwareLayer(false);
                            LinearLayout linearLayout = new LinearLayout(PhotoViewer.this.parentActivity);
                            linearLayout.setOrientation(1);
                            builder.setCustomView(linearLayout);
                            TextView titleView = new TextView(PhotoViewer.this.parentActivity);
                            titleView.setLines(1);
                            titleView.setSingleLine(true);
                            titleView.setText(LocaleController.getString("MessageLifetime", R.string.MessageLifetime));
                            titleView.setTextColor(-1);
                            titleView.setTextSize(1, 16.0f);
                            titleView.setEllipsize(TruncateAt.MIDDLE);
                            titleView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(4.0f));
                            titleView.setGravity(16);
                            linearLayout.addView(titleView, LayoutHelper.createFrame(-1, -2.0f));
                            titleView.setOnTouchListener(new C15841());
                            titleView = new TextView(PhotoViewer.this.parentActivity);
                            if (PhotoViewer.this.isCurrentVideo) {
                                str = "MessageLifetimeVideo";
                                i = R.string.MessageLifetimeVideo;
                            } else {
                                str = "MessageLifetimePhoto";
                                i = R.string.MessageLifetimePhoto;
                            }
                            titleView.setText(LocaleController.getString(str, i));
                            titleView.setTextColor(-8355712);
                            titleView.setTextSize(1, 14.0f);
                            titleView.setEllipsize(TruncateAt.MIDDLE);
                            titleView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
                            titleView.setGravity(16);
                            linearLayout.addView(titleView, LayoutHelper.createFrame(-1, -2.0f));
                            titleView.setOnTouchListener(new C15852());
                            final BottomSheet bottomSheet = builder.create();
                            final NumberPicker numberPicker = new NumberPicker(PhotoViewer.this.parentActivity);
                            numberPicker.setMinValue(0);
                            numberPicker.setMaxValue(28);
                            Object object = PhotoViewer.this.imagesArrLocals.get(PhotoViewer.this.currentIndex);
                            if (object instanceof PhotoEntry) {
                                i = ((PhotoEntry) object).ttl;
                            } else if (object instanceof SearchImage) {
                                i = ((SearchImage) object).ttl;
                            } else {
                                i = 0;
                                if (i != 0) {
                                    numberPicker.setValue(MessagesController.getGlobalMainSettings().getInt("self_destruct", 7));
                                } else if (i >= 0 || i >= 21) {
                                    numberPicker.setValue((21 + (i / 5)) - 5);
                                } else {
                                    numberPicker.setValue(i);
                                }
                                numberPicker.setTextColor(-1);
                                numberPicker.setSelectorColor(-11711155);
                                numberPicker.setFormatter(new C22273());
                                linearLayout.addView(numberPicker, LayoutHelper.createLinear(-1, -2));
                                buttonsLayout = new FrameLayout(PhotoViewer.this.parentActivity) {
                                    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                                        int count = getChildCount();
                                        View positiveButton = null;
                                        int width = right - left;
                                        for (int a = 0; a < count; a++) {
                                            View child = getChildAt(a);
                                            if (((Integer) child.getTag()).intValue() == -1) {
                                                positiveButton = child;
                                                child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), (width - getPaddingRight()) + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                            } else if (((Integer) child.getTag()).intValue() == -2) {
                                                int x = (width - getPaddingRight()) - child.getMeasuredWidth();
                                                if (positiveButton != null) {
                                                    x -= positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                                }
                                                child.layout(x, getPaddingTop(), child.getMeasuredWidth() + x, getPaddingTop() + child.getMeasuredHeight());
                                            } else {
                                                child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                            }
                                        }
                                    }
                                };
                                buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                                linearLayout.addView(buttonsLayout, LayoutHelper.createLinear(-1, 52));
                                textView = new TextView(PhotoViewer.this.parentActivity);
                                textView.setMinWidth(AndroidUtilities.dp(64.0f));
                                textView.setTag(Integer.valueOf(-1));
                                textView.setTextSize(1, 14.0f);
                                textView.setTextColor(-11944718);
                                textView.setGravity(17);
                                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                                textView.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
                                textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                                textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                                buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 53));
                                textView.setOnClickListener(new OnClickListener() {
                                    public void onClick(View v) {
                                        int seconds;
                                        int value = numberPicker.getValue();
                                        Editor editor = MessagesController.getGlobalMainSettings().edit();
                                        editor.putInt("self_destruct", value);
                                        editor.commit();
                                        bottomSheet.dismiss();
                                        if (value < 0 || value >= 21) {
                                            seconds = (value - 16) * 5;
                                        } else {
                                            seconds = value;
                                        }
                                        Object object = PhotoViewer.this.imagesArrLocals.get(PhotoViewer.this.currentIndex);
                                        if (object instanceof PhotoEntry) {
                                            ((PhotoEntry) object).ttl = seconds;
                                        } else if (object instanceof SearchImage) {
                                            ((SearchImage) object).ttl = seconds;
                                        }
                                        PhotoViewer.this.timeItem.setColorFilter(seconds != 0 ? new PorterDuffColorFilter(-12734994, Mode.MULTIPLY) : null);
                                        if (!PhotoViewer.this.checkImageView.isChecked()) {
                                            PhotoViewer.this.checkImageView.callOnClick();
                                        }
                                    }
                                });
                                textView = new TextView(PhotoViewer.this.parentActivity);
                                textView.setMinWidth(AndroidUtilities.dp(64.0f));
                                textView.setTag(Integer.valueOf(-2));
                                textView.setTextSize(1, 14.0f);
                                textView.setTextColor(-11944718);
                                textView.setGravity(17);
                                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                                textView.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
                                textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                                textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                                buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 53));
                                textView.setOnClickListener(new OnClickListener() {
                                    public void onClick(View v) {
                                        bottomSheet.dismiss();
                                    }
                                });
                                bottomSheet.show();
                                bottomSheet.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
                            }
                            if (i != 0) {
                                if (i >= 0) {
                                }
                                numberPicker.setValue((21 + (i / 5)) - 5);
                            } else {
                                numberPicker.setValue(MessagesController.getGlobalMainSettings().getInt("self_destruct", 7));
                            }
                            numberPicker.setTextColor(-1);
                            numberPicker.setSelectorColor(-11711155);
                            numberPicker.setFormatter(new C22273());
                            linearLayout.addView(numberPicker, LayoutHelper.createLinear(-1, -2));
                            buttonsLayout = /* anonymous class already generated */;
                            buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                            linearLayout.addView(buttonsLayout, LayoutHelper.createLinear(-1, 52));
                            textView = new TextView(PhotoViewer.this.parentActivity);
                            textView.setMinWidth(AndroidUtilities.dp(64.0f));
                            textView.setTag(Integer.valueOf(-1));
                            textView.setTextSize(1, 14.0f);
                            textView.setTextColor(-11944718);
                            textView.setGravity(17);
                            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                            textView.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
                            textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                            textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                            buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 53));
                            textView.setOnClickListener(/* anonymous class already generated */);
                            textView = new TextView(PhotoViewer.this.parentActivity);
                            textView.setMinWidth(AndroidUtilities.dp(64.0f));
                            textView.setTag(Integer.valueOf(-2));
                            textView.setTextSize(1, 14.0f);
                            textView.setTextColor(-11944718);
                            textView.setGravity(17);
                            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                            textView.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
                            textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                            textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                            buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 53));
                            textView.setOnClickListener(/* anonymous class already generated */);
                            bottomSheet.show();
                            bottomSheet.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
                        }
                    }
                }
            });
            r0.editorDoneLayout = new PickerBottomLayoutViewer(r0.actvityContext);
            r0.editorDoneLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.editorDoneLayout.updateSelectedCount(0, false);
            r0.editorDoneLayout.setVisibility(8);
            r0.containerView.addView(r0.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
            r0.editorDoneLayout.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.currentEditMode == 1) {
                        PhotoViewer.this.photoCropView.cancelAnimationRunnable();
                    }
                    PhotoViewer.this.switchToEditMode(0);
                }
            });
            r0.editorDoneLayout.doneButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.currentEditMode != 1 || PhotoViewer.this.photoCropView.isReady()) {
                        PhotoViewer.this.applyCurrentEditMode();
                        PhotoViewer.this.switchToEditMode(0);
                    }
                }
            });
            r0.resetButton = new TextView(r0.actvityContext);
            r0.resetButton.setVisibility(8);
            r0.resetButton.setTextSize(1, 14.0f);
            r0.resetButton.setTextColor(-1);
            r0.resetButton.setGravity(17);
            r0.resetButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
            r0.resetButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            r0.resetButton.setText(LocaleController.getString("Reset", R.string.CropReset).toUpperCase());
            r0.resetButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r0.editorDoneLayout.addView(r0.resetButton, LayoutHelper.createFrame(-2, -1, 49));
            r0.resetButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PhotoViewer.this.photoCropView.reset();
                }
            });
            r0.gestureDetector = new GestureDetector(r0.containerView.getContext(), r0);
            r0.gestureDetector.setOnDoubleTapListener(r0);
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
            r0.centerImage.setParentView(r0.containerView);
            r0.centerImage.setCrossfadeAlpha((byte) 2);
            r0.centerImage.setInvalidateAll(true);
            r0.centerImage.setDelegate(imageReceiverDelegate);
            r0.leftImage.setParentView(r0.containerView);
            r0.leftImage.setCrossfadeAlpha((byte) 2);
            r0.leftImage.setInvalidateAll(true);
            r0.leftImage.setDelegate(imageReceiverDelegate);
            r0.rightImage.setParentView(r0.containerView);
            r0.rightImage.setCrossfadeAlpha((byte) 2);
            r0.rightImage.setInvalidateAll(true);
            r0.rightImage.setDelegate(imageReceiverDelegate);
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            r0.checkImageView = new CheckBox(r0.containerView.getContext(), R.drawable.selectphoto_large) {
                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            r0.checkImageView.setDrawBackground(true);
            r0.checkImageView.setHasBorder(true);
            r0.checkImageView.setSize(40);
            r0.checkImageView.setCheckOffset(AndroidUtilities.dp(1.0f));
            r0.checkImageView.setColor(-10043398, -1);
            r0.checkImageView.setVisibility(8);
            FrameLayoutDrawer frameLayoutDrawer = r0.containerView;
            View view = r0.checkImageView;
            if (rotation != 3) {
                if (rotation != 1) {
                    f = 68.0f;
                    frameLayoutDrawer.addView(view, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, f, 10.0f, 0.0f));
                    if (VERSION.SDK_INT >= 21) {
                        layoutParams = (FrameLayout.LayoutParams) r0.checkImageView.getLayoutParams();
                        layoutParams.topMargin += AndroidUtilities.statusBarHeight;
                    }
                    r0.checkImageView.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if (PhotoViewer.this.captionEditText.getTag() == null) {
                                PhotoViewer.this.setPhotoChecked();
                            }
                        }
                    });
                    r0.photosCounterView = new CounterView(r0.parentActivity);
                    frameLayoutDrawer = r0.containerView;
                    view = r0.photosCounterView;
                    if (rotation != 3) {
                        if (rotation == 1) {
                            f2 = 68.0f;
                            frameLayoutDrawer.addView(view, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, f2, 66.0f, 0.0f));
                            if (VERSION.SDK_INT >= 21) {
                                layoutParams2 = (FrameLayout.LayoutParams) r0.photosCounterView.getLayoutParams();
                                layoutParams2.topMargin += AndroidUtilities.statusBarHeight;
                            }
                            r0.photosCounterView.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    if (!(PhotoViewer.this.captionEditText.getTag() != null || PhotoViewer.this.placeProvider == null || PhotoViewer.this.placeProvider.getSelectedPhotosOrder() == null)) {
                                        if (!PhotoViewer.this.placeProvider.getSelectedPhotosOrder().isEmpty()) {
                                            PhotoViewer.this.togglePhotosListView(PhotoViewer.this.isPhotosListViewVisible ^ true, true);
                                        }
                                    }
                                }
                            });
                            r0.selectedPhotosListView = new RecyclerListView(r0.parentActivity);
                            r0.selectedPhotosListView.setVisibility(8);
                            r0.selectedPhotosListView.setAlpha(0.0f);
                            r0.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                            r0.selectedPhotosListView.addItemDecoration(new ItemDecoration() {
                                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                                    int position = parent.getChildAdapterPosition(view);
                                    if ((view instanceof PhotoPickerPhotoCell) && position == 0) {
                                        outRect.left = AndroidUtilities.dp(3.0f);
                                    } else {
                                        outRect.left = 0;
                                    }
                                    outRect.right = AndroidUtilities.dp(3.0f);
                                }
                            });
                            ((DefaultItemAnimator) r0.selectedPhotosListView.getItemAnimator()).setDelayAnimations(false);
                            r0.selectedPhotosListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                            r0.selectedPhotosListView.setPadding(0, AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f));
                            r0.selectedPhotosListView.setLayoutManager(new LinearLayoutManager(r0.parentActivity, 0, false) {
                                public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
                                    LinearSmoothScrollerEnd linearSmoothScroller = new LinearSmoothScrollerEnd(recyclerView.getContext());
                                    linearSmoothScroller.setTargetPosition(position);
                                    startSmoothScroll(linearSmoothScroller);
                                }
                            });
                            recyclerListView = r0.selectedPhotosListView;
                            listAdapter = new ListAdapter(r0.parentActivity);
                            r0.selectedPhotosAdapter = listAdapter;
                            recyclerListView.setAdapter(listAdapter);
                            r0.containerView.addView(r0.selectedPhotosListView, LayoutHelper.createFrame(-1, 88, 51));
                            r0.selectedPhotosListView.setOnItemClickListener(new OnItemClickListener() {
                                public void onItemClick(View view, int position) {
                                    if (position == 0 && PhotoViewer.this.placeProvider.allowGroupPhotos()) {
                                        boolean enabled = SharedConfig.groupPhotosEnabled;
                                        SharedConfig.toggleGroupPhotosEnabled();
                                        PhotoViewer.this.placeProvider.toggleGroupPhotosEnabled();
                                        ((ImageView) view).setColorFilter(!enabled ? new PorterDuffColorFilter(-10043398, Mode.MULTIPLY) : null);
                                        PhotoViewer.this.showHint(false, enabled ^ 1);
                                        return;
                                    }
                                    PhotoViewer.this.ignoreDidSetImage = true;
                                    int idx = PhotoViewer.this.imagesArrLocals.indexOf(view.getTag());
                                    if (idx >= 0) {
                                        PhotoViewer.this.currentIndex = -1;
                                        PhotoViewer.this.setImageIndex(idx, true);
                                    }
                                    PhotoViewer.this.ignoreDidSetImage = false;
                                }
                            });
                            r0.captionEditText = new PhotoViewerCaptionEnterView(r0.actvityContext, r0.containerView, r0.windowView) {
                                public boolean dispatchTouchEvent(MotionEvent ev) {
                                    boolean z = false;
                                    try {
                                        if (!PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev)) {
                                            z = true;
                                        }
                                        return z;
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                        return false;
                                    }
                                }

                                public boolean onInterceptTouchEvent(MotionEvent ev) {
                                    boolean z = false;
                                    try {
                                        if (!PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(ev)) {
                                            z = true;
                                        }
                                        return z;
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                        return false;
                                    }
                                }

                                public boolean onTouchEvent(MotionEvent event) {
                                    return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                                }
                            };
                            r0.captionEditText.setDelegate(new PhotoViewerCaptionEnterViewDelegate() {
                                public void onCaptionEnter() {
                                    PhotoViewer.this.closeCaptionEnter(true);
                                }

                                public void onTextChanged(CharSequence text) {
                                    if (PhotoViewer.this.mentionsAdapter != null && PhotoViewer.this.captionEditText != null && PhotoViewer.this.parentChatActivity != null && text != null) {
                                        PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(text.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages, false);
                                    }
                                }

                                public void onWindowSizeChanged(int size) {
                                    if (size - (ActionBar.getCurrentActionBarHeight() * 2) < AndroidUtilities.dp((float) ((36 * Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount())) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0)))) {
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
                            r0.containerView.addView(r0.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
                            r0.mentionListView = new RecyclerListView(r0.actvityContext) {
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
                            r0.mentionListView.setTag(Integer.valueOf(5));
                            r0.mentionLayoutManager = new LinearLayoutManager(r0.actvityContext) {
                                public boolean supportsPredictiveItemAnimations() {
                                    return false;
                                }
                            };
                            r0.mentionLayoutManager.setOrientation(1);
                            r0.mentionListView.setLayoutManager(r0.mentionLayoutManager);
                            r0.mentionListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                            r0.mentionListView.setVisibility(8);
                            r0.mentionListView.setClipToPadding(true);
                            r0.mentionListView.setOverScrollMode(2);
                            r0.containerView.addView(r0.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
                            recyclerListView = r0.mentionListView;
                            mentionsAdapter = new MentionsAdapter(r0.actvityContext, true, 0, new MentionsAdapterDelegate() {

                                /* renamed from: org.telegram.ui.PhotoViewer$37$1 */
                                class C15901 extends AnimatorListenerAdapter {
                                    C15901() {
                                    }

                                    public void onAnimationEnd(Animator animation) {
                                        if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animation)) {
                                            PhotoViewer.this.mentionListAnimation = null;
                                        }
                                    }
                                }

                                /* renamed from: org.telegram.ui.PhotoViewer$37$2 */
                                class C15912 extends AnimatorListenerAdapter {
                                    C15912() {
                                    }

                                    public void onAnimationEnd(Animator animation) {
                                        if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animation)) {
                                            PhotoViewer.this.mentionListView.setVisibility(8);
                                            PhotoViewer.this.mentionListAnimation = null;
                                        }
                                    }
                                }

                                public void needChangePanelVisibility(boolean show) {
                                    if (show) {
                                        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) PhotoViewer.this.mentionListView.getLayoutParams();
                                        int height = (36 * Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount())) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0);
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
                                            PhotoViewer.this.mentionListAnimation.addListener(new C15901());
                                            PhotoViewer.this.mentionListAnimation.setDuration(200);
                                            PhotoViewer.this.mentionListAnimation.start();
                                        } else {
                                            PhotoViewer.this.mentionListView.setAlpha(1.0f);
                                            PhotoViewer.this.mentionListView.setVisibility(4);
                                        }
                                    } else {
                                        if (PhotoViewer.this.mentionListAnimation != null) {
                                            PhotoViewer.this.mentionListAnimation.cancel();
                                            PhotoViewer.this.mentionListAnimation = null;
                                        }
                                        if (PhotoViewer.this.mentionListView.getVisibility() != 8) {
                                            if (PhotoViewer.this.allowMentions) {
                                                PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                                                AnimatorSet access$12200 = PhotoViewer.this.mentionListAnimation;
                                                Animator[] animatorArr = new Animator[1];
                                                animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[]{0.0f});
                                                access$12200.playTogether(animatorArr);
                                                PhotoViewer.this.mentionListAnimation.addListener(new C15912());
                                                PhotoViewer.this.mentionListAnimation.setDuration(200);
                                                PhotoViewer.this.mentionListAnimation.start();
                                            } else {
                                                PhotoViewer.this.mentionListView.setVisibility(8);
                                            }
                                        }
                                    }
                                }

                                public void onContextSearch(boolean searching) {
                                }

                                public void onContextClick(BotInlineResult result) {
                                }
                            });
                            r0.mentionsAdapter = mentionsAdapter;
                            recyclerListView.setAdapter(mentionsAdapter);
                            r0.mentionListView.setOnItemClickListener(new OnItemClickListener() {
                                public void onItemClick(View view, int position) {
                                    User object = PhotoViewer.this.mentionsAdapter.getItem(position);
                                    int start = PhotoViewer.this.mentionsAdapter.getResultStartPosition();
                                    int len = PhotoViewer.this.mentionsAdapter.getResultLength();
                                    if (object instanceof User) {
                                        User user = object;
                                        if (user.username != null) {
                                            PhotoViewerCaptionEnterView access$4300 = PhotoViewer.this.captionEditText;
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append("@");
                                            stringBuilder.append(user.username);
                                            stringBuilder.append(" ");
                                            access$4300.replaceWithText(start, len, stringBuilder.toString(), false);
                                        } else {
                                            String name = UserObject.getFirstName(user);
                                            StringBuilder stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append(name);
                                            stringBuilder2.append(" ");
                                            Spannable spannable = new SpannableString(stringBuilder2.toString());
                                            StringBuilder stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append(TtmlNode.ANONYMOUS_REGION_ID);
                                            stringBuilder3.append(user.id);
                                            spannable.setSpan(new URLSpanUserMentionPhotoViewer(stringBuilder3.toString(), true), 0, spannable.length(), 33);
                                            PhotoViewer.this.captionEditText.replaceWithText(start, len, spannable, false);
                                        }
                                    } else if (object instanceof String) {
                                        PhotoViewerCaptionEnterView access$43002 = PhotoViewer.this.captionEditText;
                                        StringBuilder stringBuilder4 = new StringBuilder();
                                        stringBuilder4.append(object);
                                        stringBuilder4.append(" ");
                                        access$43002.replaceWithText(start, len, stringBuilder4.toString(), false);
                                    } else if (object instanceof EmojiSuggestion) {
                                        String code = ((EmojiSuggestion) object).emoji;
                                        PhotoViewer.this.captionEditText.addEmojiToRecent(code);
                                        PhotoViewer.this.captionEditText.replaceWithText(start, len, code, true);
                                    }
                                }
                            });
                            r0.mentionListView.setOnItemLongClickListener(new OnItemLongClickListener() {

                                /* renamed from: org.telegram.ui.PhotoViewer$39$1 */
                                class C15921 implements DialogInterface.OnClickListener {
                                    C15921() {
                                    }

                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        PhotoViewer.this.mentionsAdapter.clearRecentHashtags();
                                    }
                                }

                                public boolean onItemClick(View view, int position) {
                                    if (!(PhotoViewer.this.mentionsAdapter.getItem(position) instanceof String)) {
                                        return false;
                                    }
                                    Builder builder = new Builder(PhotoViewer.this.parentActivity);
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                                    builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new C15921());
                                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                    PhotoViewer.this.showAlertDialog(builder);
                                    return true;
                                }
                            });
                        }
                    }
                    f2 = 58.0f;
                    frameLayoutDrawer.addView(view, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, f2, 66.0f, 0.0f));
                    if (VERSION.SDK_INT >= 21) {
                        layoutParams2 = (FrameLayout.LayoutParams) r0.photosCounterView.getLayoutParams();
                        layoutParams2.topMargin += AndroidUtilities.statusBarHeight;
                    }
                    r0.photosCounterView.setOnClickListener(/* anonymous class already generated */);
                    r0.selectedPhotosListView = new RecyclerListView(r0.parentActivity);
                    r0.selectedPhotosListView.setVisibility(8);
                    r0.selectedPhotosListView.setAlpha(0.0f);
                    r0.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                    r0.selectedPhotosListView.addItemDecoration(/* anonymous class already generated */);
                    ((DefaultItemAnimator) r0.selectedPhotosListView.getItemAnimator()).setDelayAnimations(false);
                    r0.selectedPhotosListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                    r0.selectedPhotosListView.setPadding(0, AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f));
                    r0.selectedPhotosListView.setLayoutManager(/* anonymous class already generated */);
                    recyclerListView = r0.selectedPhotosListView;
                    listAdapter = new ListAdapter(r0.parentActivity);
                    r0.selectedPhotosAdapter = listAdapter;
                    recyclerListView.setAdapter(listAdapter);
                    r0.containerView.addView(r0.selectedPhotosListView, LayoutHelper.createFrame(-1, 88, 51));
                    r0.selectedPhotosListView.setOnItemClickListener(/* anonymous class already generated */);
                    r0.captionEditText = /* anonymous class already generated */;
                    r0.captionEditText.setDelegate(/* anonymous class already generated */);
                    r0.containerView.addView(r0.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
                    r0.mentionListView = /* anonymous class already generated */;
                    r0.mentionListView.setTag(Integer.valueOf(5));
                    r0.mentionLayoutManager = /* anonymous class already generated */;
                    r0.mentionLayoutManager.setOrientation(1);
                    r0.mentionListView.setLayoutManager(r0.mentionLayoutManager);
                    r0.mentionListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                    r0.mentionListView.setVisibility(8);
                    r0.mentionListView.setClipToPadding(true);
                    r0.mentionListView.setOverScrollMode(2);
                    r0.containerView.addView(r0.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
                    recyclerListView = r0.mentionListView;
                    mentionsAdapter = new MentionsAdapter(r0.actvityContext, true, 0, /* anonymous class already generated */);
                    r0.mentionsAdapter = mentionsAdapter;
                    recyclerListView.setAdapter(mentionsAdapter);
                    r0.mentionListView.setOnItemClickListener(/* anonymous class already generated */);
                    r0.mentionListView.setOnItemLongClickListener(/* anonymous class already generated */);
                }
            }
            f = 58.0f;
            frameLayoutDrawer.addView(view, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, f, 10.0f, 0.0f));
            if (VERSION.SDK_INT >= 21) {
                layoutParams = (FrameLayout.LayoutParams) r0.checkImageView.getLayoutParams();
                layoutParams.topMargin += AndroidUtilities.statusBarHeight;
            }
            r0.checkImageView.setOnClickListener(/* anonymous class already generated */);
            r0.photosCounterView = new CounterView(r0.parentActivity);
            frameLayoutDrawer = r0.containerView;
            view = r0.photosCounterView;
            if (rotation != 3) {
                if (rotation == 1) {
                    f2 = 68.0f;
                    frameLayoutDrawer.addView(view, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, f2, 66.0f, 0.0f));
                    if (VERSION.SDK_INT >= 21) {
                        layoutParams2 = (FrameLayout.LayoutParams) r0.photosCounterView.getLayoutParams();
                        layoutParams2.topMargin += AndroidUtilities.statusBarHeight;
                    }
                    r0.photosCounterView.setOnClickListener(/* anonymous class already generated */);
                    r0.selectedPhotosListView = new RecyclerListView(r0.parentActivity);
                    r0.selectedPhotosListView.setVisibility(8);
                    r0.selectedPhotosListView.setAlpha(0.0f);
                    r0.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                    r0.selectedPhotosListView.addItemDecoration(/* anonymous class already generated */);
                    ((DefaultItemAnimator) r0.selectedPhotosListView.getItemAnimator()).setDelayAnimations(false);
                    r0.selectedPhotosListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                    r0.selectedPhotosListView.setPadding(0, AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f));
                    r0.selectedPhotosListView.setLayoutManager(/* anonymous class already generated */);
                    recyclerListView = r0.selectedPhotosListView;
                    listAdapter = new ListAdapter(r0.parentActivity);
                    r0.selectedPhotosAdapter = listAdapter;
                    recyclerListView.setAdapter(listAdapter);
                    r0.containerView.addView(r0.selectedPhotosListView, LayoutHelper.createFrame(-1, 88, 51));
                    r0.selectedPhotosListView.setOnItemClickListener(/* anonymous class already generated */);
                    r0.captionEditText = /* anonymous class already generated */;
                    r0.captionEditText.setDelegate(/* anonymous class already generated */);
                    r0.containerView.addView(r0.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
                    r0.mentionListView = /* anonymous class already generated */;
                    r0.mentionListView.setTag(Integer.valueOf(5));
                    r0.mentionLayoutManager = /* anonymous class already generated */;
                    r0.mentionLayoutManager.setOrientation(1);
                    r0.mentionListView.setLayoutManager(r0.mentionLayoutManager);
                    r0.mentionListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                    r0.mentionListView.setVisibility(8);
                    r0.mentionListView.setClipToPadding(true);
                    r0.mentionListView.setOverScrollMode(2);
                    r0.containerView.addView(r0.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
                    recyclerListView = r0.mentionListView;
                    mentionsAdapter = new MentionsAdapter(r0.actvityContext, true, 0, /* anonymous class already generated */);
                    r0.mentionsAdapter = mentionsAdapter;
                    recyclerListView.setAdapter(mentionsAdapter);
                    r0.mentionListView.setOnItemClickListener(/* anonymous class already generated */);
                    r0.mentionListView.setOnItemLongClickListener(/* anonymous class already generated */);
                }
            }
            f2 = 58.0f;
            frameLayoutDrawer.addView(view, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, f2, 66.0f, 0.0f));
            if (VERSION.SDK_INT >= 21) {
                layoutParams2 = (FrameLayout.LayoutParams) r0.photosCounterView.getLayoutParams();
                layoutParams2.topMargin += AndroidUtilities.statusBarHeight;
            }
            r0.photosCounterView.setOnClickListener(/* anonymous class already generated */);
            r0.selectedPhotosListView = new RecyclerListView(r0.parentActivity);
            r0.selectedPhotosListView.setVisibility(8);
            r0.selectedPhotosListView.setAlpha(0.0f);
            r0.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
            r0.selectedPhotosListView.addItemDecoration(/* anonymous class already generated */);
            ((DefaultItemAnimator) r0.selectedPhotosListView.getItemAnimator()).setDelayAnimations(false);
            r0.selectedPhotosListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.selectedPhotosListView.setPadding(0, AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f));
            r0.selectedPhotosListView.setLayoutManager(/* anonymous class already generated */);
            recyclerListView = r0.selectedPhotosListView;
            listAdapter = new ListAdapter(r0.parentActivity);
            r0.selectedPhotosAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            r0.containerView.addView(r0.selectedPhotosListView, LayoutHelper.createFrame(-1, 88, 51));
            r0.selectedPhotosListView.setOnItemClickListener(/* anonymous class already generated */);
            r0.captionEditText = /* anonymous class already generated */;
            r0.captionEditText.setDelegate(/* anonymous class already generated */);
            r0.containerView.addView(r0.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
            r0.mentionListView = /* anonymous class already generated */;
            r0.mentionListView.setTag(Integer.valueOf(5));
            r0.mentionLayoutManager = /* anonymous class already generated */;
            r0.mentionLayoutManager.setOrientation(1);
            r0.mentionListView.setLayoutManager(r0.mentionLayoutManager);
            r0.mentionListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.mentionListView.setVisibility(8);
            r0.mentionListView.setClipToPadding(true);
            r0.mentionListView.setOverScrollMode(2);
            r0.containerView.addView(r0.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
            recyclerListView = r0.mentionListView;
            mentionsAdapter = new MentionsAdapter(r0.actvityContext, true, 0, /* anonymous class already generated */);
            r0.mentionsAdapter = mentionsAdapter;
            recyclerListView.setAdapter(mentionsAdapter);
            r0.mentionListView.setOnItemClickListener(/* anonymous class already generated */);
            r0.mentionListView.setOnItemLongClickListener(/* anonymous class already generated */);
        }
    }

    private boolean checkInlinePermissions() {
        if (this.parentActivity == null) {
            return false;
        }
        if (VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this.parentActivity)) {
                new Builder(this.parentActivity).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", R.string.PermissionDrawAboveOtherApps)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                    @TargetApi(23)
                    public void onClick(DialogInterface dialog, int which) {
                        if (PhotoViewer.this.parentActivity != null) {
                            try {
                                Activity access$2500 = PhotoViewer.this.parentActivity;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("package:");
                                stringBuilder.append(PhotoViewer.this.parentActivity.getPackageName());
                                access$2500.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(stringBuilder.toString())));
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    }
                }).show();
                return false;
            }
        }
        return true;
    }

    private TextView createCaptionTextView() {
        TextView textView = new TextView(this.actvityContext) {
            public boolean onTouchEvent(MotionEvent event) {
                return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
            }
        };
        textView.setMovementMethod(new LinkMovementMethodMy());
        textView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        textView.setLinkTextColor(-1);
        textView.setTextColor(-1);
        textView.setHighlightColor(872415231);
        textView.setEllipsize(TruncateAt.END);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView.setTextSize(1, 16.0f);
        textView.setVisibility(4);
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PhotoViewer.this.needCaptionLayout) {
                    PhotoViewer.this.openCaptionEnter();
                }
            }
        });
        return textView;
    }

    private int getLeftInset() {
        if (this.lastInsets == null || VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) this.lastInsets).getSystemWindowInsetLeft();
    }

    private void dismissInternal() {
        try {
            if (this.windowView.getParent() != null) {
                ((LaunchActivity) this.parentActivity).drawerLayoutContainer.setAllowDrawContent(true);
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void switchToPip() {
        if (!(this.videoPlayer == null || !this.textureUploaded || !checkInlinePermissions() || this.changingTextureView || this.switchingInlineMode)) {
            if (!this.isInline) {
                if (PipInstance != null) {
                    PipInstance.destroyPhotoViewer();
                }
                PipInstance = Instance;
                Instance = null;
                this.switchingInlineMode = true;
                this.isVisible = false;
                if (this.currentPlaceObject != null) {
                    this.currentPlaceObject.imageReceiver.setVisible(true, true);
                }
                if (VERSION.SDK_INT >= 21) {
                    this.pipAnimationInProgress = true;
                    org.telegram.ui.Components.Rect rect = PipVideoView.getPipRect(this.aspectRatioFrameLayout.getAspectRatio());
                    float scale = rect.width / ((float) this.videoTextureView.getWidth());
                    rect.f27y += (float) AndroidUtilities.statusBarHeight;
                    AnimatorSet animatorSet = new AnimatorSet();
                    Animator[] animatorArr = new Animator[13];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.textureImageView, "scaleX", new float[]{scale});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.textureImageView, "scaleY", new float[]{scale});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.textureImageView, "translationX", new float[]{rect.f26x});
                    animatorArr[3] = ObjectAnimator.ofFloat(this.textureImageView, "translationY", new float[]{rect.f27y});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.videoTextureView, "scaleX", new float[]{scale});
                    animatorArr[5] = ObjectAnimator.ofFloat(this.videoTextureView, "scaleY", new float[]{scale});
                    animatorArr[6] = ObjectAnimator.ofFloat(this.videoTextureView, "translationX", new float[]{rect.f26x - this.aspectRatioFrameLayout.getX()});
                    animatorArr[7] = ObjectAnimator.ofFloat(this.videoTextureView, "translationY", new float[]{rect.f27y - this.aspectRatioFrameLayout.getY()});
                    animatorArr[8] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[]{0});
                    animatorArr[9] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f});
                    animatorArr[10] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f});
                    animatorArr[11] = ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[]{0.0f});
                    animatorArr[12] = ObjectAnimator.ofFloat(this.groupedPhotosListView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.setDuration(250);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.pipAnimationInProgress = false;
                            PhotoViewer.this.switchToInlineRunnable.run();
                        }
                    });
                    animatorSet.start();
                } else {
                    this.switchToInlineRunnable.run();
                    dismissInternal();
                }
            }
        }
    }

    public VideoPlayer getVideoPlayer() {
        return this.videoPlayer;
    }

    public void exitFromPip() {
        if (this.isInline) {
            if (Instance != null) {
                Instance.closePhoto(false, true);
            }
            Instance = PipInstance;
            PipInstance = null;
            this.switchingInlineMode = true;
            if (this.currentBitmap != null) {
                this.currentBitmap.recycle();
                this.currentBitmap = null;
            }
            this.changingTextureView = true;
            this.isInline = false;
            this.videoTextureView.setVisibility(4);
            this.aspectRatioFrameLayout.addView(this.videoTextureView);
            if (ApplicationLoader.mainInterfacePaused) {
                try {
                    this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
            if (VERSION.SDK_INT >= 21) {
                this.pipAnimationInProgress = true;
                org.telegram.ui.Components.Rect rect = PipVideoView.getPipRect(this.aspectRatioFrameLayout.getAspectRatio());
                float scale = rect.width / ((float) this.textureImageView.getLayoutParams().width);
                rect.f27y += (float) AndroidUtilities.statusBarHeight;
                this.textureImageView.setScaleX(scale);
                this.textureImageView.setScaleY(scale);
                this.textureImageView.setTranslationX(rect.f26x);
                this.textureImageView.setTranslationY(rect.f27y);
                this.videoTextureView.setScaleX(scale);
                this.videoTextureView.setScaleY(scale);
                this.videoTextureView.setTranslationX(rect.f26x - this.aspectRatioFrameLayout.getX());
                this.videoTextureView.setTranslationY(rect.f27y - this.aspectRatioFrameLayout.getY());
            } else {
                this.pipVideoView.close();
                this.pipVideoView = null;
            }
            try {
                this.isVisible = true;
                ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
                if (this.currentPlaceObject != null) {
                    this.currentPlaceObject.imageReceiver.setVisible(false, false);
                }
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
            if (VERSION.SDK_INT >= 21) {
                this.waitingForDraw = 4;
            }
        }
    }

    private void createVideoControlsInterface() {
        this.videoPlayerSeekbar = new SeekBar(this.containerView.getContext());
        this.videoPlayerSeekbar.setLineHeight(AndroidUtilities.dp(4.0f));
        this.videoPlayerSeekbar.setColors(NUM, NUM, -2764585, -1, -1);
        this.videoPlayerSeekbar.setDelegate(new SeekBarDelegate() {
            public void onSeekBarDrag(float progress) {
                if (PhotoViewer.this.videoPlayer != null) {
                    if (!PhotoViewer.this.inPreview && PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
                        progress = PhotoViewer.this.videoTimelineView.getLeftProgress() + ((PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress()) * progress);
                    }
                    long duration = PhotoViewer.this.videoPlayer.getDuration();
                    if (duration == C0539C.TIME_UNSET) {
                        PhotoViewer.this.seekToProgressPending = progress;
                    } else {
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (((float) duration) * progress)));
                    }
                }
            }
        });
        this.videoPlayerControlFrameLayout = new FrameLayout(this.containerView.getContext()) {
            public boolean onTouchEvent(MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (!PhotoViewer.this.videoPlayerSeekbar.onTouch(event.getAction(), event.getX() - ((float) AndroidUtilities.dp(48.0f)), event.getY())) {
                    return true;
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
                    if (duration == C0539C.TIME_UNSET) {
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
                    if (!PhotoViewer.this.inPreview && PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
                        progress -= PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (progress < 0.0f) {
                            progress = 0.0f;
                        }
                        progress /= PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (progress > 1.0f) {
                            progress = 1.0f;
                        }
                    }
                }
                PhotoViewer.this.videoPlayerSeekbar.setProgress(progress);
                PhotoViewer.this.videoTimelineView.setProgress(progress);
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
        this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48.0f, 51, 4.0f, 0.0f, 0.0f, 0.0f));
        this.videoPlayButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PhotoViewer.this.videoPlayer != null) {
                    if (PhotoViewer.this.isPlaying) {
                        PhotoViewer.this.videoPlayer.pause();
                    } else {
                        if (PhotoViewer.this.isCurrentVideo) {
                            if (Math.abs(PhotoViewer.this.videoTimelineView.getProgress() - 1.0f) < 0.01f || PhotoViewer.this.videoPlayer.getCurrentPosition() == PhotoViewer.this.videoPlayer.getDuration()) {
                                PhotoViewer.this.videoPlayer.seekTo(0);
                            }
                        } else if (Math.abs(PhotoViewer.this.videoPlayerSeekbar.getProgress() - 1.0f) < 0.01f || PhotoViewer.this.videoPlayer.getCurrentPosition() == PhotoViewer.this.videoPlayer.getDuration()) {
                            PhotoViewer.this.videoPlayer.seekTo(0);
                        }
                        PhotoViewer.this.videoPlayer.play();
                    }
                    PhotoViewer.this.containerView.invalidate();
                }
            }
        });
        this.videoPlayerTime = new SimpleTextView(this.containerView.getContext());
        this.videoPlayerTime.setTextColor(-1);
        this.videoPlayerTime.setGravity(53);
        this.videoPlayerTime.setTextSize(13);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 17.0f, 7.0f, 0.0f));
    }

    private void openCaptionEnter() {
        if (this.imageMoveAnimation == null && this.changeModeAnimation == null) {
            if (this.currentEditMode == 0) {
                this.selectedPhotosListView.setVisibility(8);
                this.selectedPhotosListView.setEnabled(false);
                this.selectedPhotosListView.setAlpha(0.0f);
                this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                this.photosCounterView.setRotationX(0.0f);
                this.isPhotosListViewVisible = false;
                this.captionEditText.setTag(Integer.valueOf(1));
                this.captionEditText.openKeyboard();
                this.lastTitle = this.actionBar.getTitle();
                if (this.isCurrentVideo) {
                    String str;
                    int i;
                    ActionBar actionBar = this.actionBar;
                    if (this.muteVideo) {
                        str = "GifCaption";
                        i = R.string.GifCaption;
                    } else {
                        str = "VideoCaption";
                        i = R.string.VideoCaption;
                    }
                    actionBar.setTitle(LocaleController.getString(str, i));
                    this.actionBar.setSubtitle(null);
                } else {
                    this.actionBar.setTitle(LocaleController.getString("PhotoCaption", R.string.PhotoCaption));
                }
            }
        }
    }

    private VideoEditedInfo getCurrentVideoEditedInfo() {
        if (this.isCurrentVideo && this.currentPlayingVideoFile != null) {
            if (this.compressionsCount != 0) {
                VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
                videoEditedInfo.startTime = this.startTime;
                videoEditedInfo.endTime = this.endTime;
                videoEditedInfo.rotationValue = this.rotationValue;
                videoEditedInfo.originalWidth = this.originalWidth;
                videoEditedInfo.originalHeight = this.originalHeight;
                videoEditedInfo.bitrate = this.bitrate;
                videoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
                videoEditedInfo.estimatedSize = (long) this.estimatedSize;
                videoEditedInfo.estimatedDuration = this.estimatedDuration;
                int i = -1;
                if (this.muteVideo || !(this.compressItem.getTag() == null || this.selectedCompression == this.compressionsCount - 1)) {
                    if (this.muteVideo) {
                        this.selectedCompression = 1;
                        updateWidthHeightBitrateForCompression();
                    }
                    videoEditedInfo.resultWidth = this.resultWidth;
                    videoEditedInfo.resultHeight = this.resultHeight;
                    if (!this.muteVideo) {
                        i = this.bitrate;
                    }
                    videoEditedInfo.bitrate = i;
                    videoEditedInfo.muted = this.muteVideo;
                } else {
                    videoEditedInfo.resultWidth = this.originalWidth;
                    videoEditedInfo.resultHeight = this.originalHeight;
                    if (!this.muteVideo) {
                        i = this.originalBitrate;
                    }
                    videoEditedInfo.bitrate = i;
                    videoEditedInfo.muted = this.muteVideo;
                }
                return videoEditedInfo;
            }
        }
        return null;
    }

    private void closeCaptionEnter(boolean apply) {
        if (this.currentIndex >= 0) {
            if (this.currentIndex < this.imagesArrLocals.size()) {
                PhotoEntry object = this.imagesArrLocals.get(this.currentIndex);
                CharSequence[] result = new CharSequence[]{this.captionEditText.getFieldCharSequence()};
                if (apply) {
                    ArrayList<MessageEntity> entities = DataQuery.getInstance(this.currentAccount).getEntities(result);
                    if (object instanceof PhotoEntry) {
                        PhotoEntry photoEntry = object;
                        photoEntry.caption = result[0];
                        photoEntry.entities = entities;
                    } else if (object instanceof SearchImage) {
                        SearchImage photoEntry2 = (SearchImage) object;
                        photoEntry2.caption = result[0];
                        photoEntry2.entities = entities;
                    }
                    if (!(this.captionEditText.getFieldCharSequence().length() == 0 || this.placeProvider.isPhotoChecked(this.currentIndex))) {
                        setPhotoChecked();
                    }
                }
                this.captionEditText.setTag(null);
                if (this.lastTitle != null) {
                    this.actionBar.setTitle(this.lastTitle);
                    this.lastTitle = null;
                }
                if (this.isCurrentVideo) {
                    this.actionBar.setSubtitle(this.muteVideo ? null : this.currentSubtitle);
                }
                updateCaptionTextForCurrentPhoto(object);
                setCurrentCaption(null, result[0], false);
                if (this.captionEditText.isPopupShowing()) {
                    this.captionEditText.hidePopup();
                }
                this.captionEditText.closeKeyboard();
            }
        }
    }

    private void updateVideoPlayerTime() {
        String newText;
        if (this.videoPlayer == null) {
            newText = String.format("%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)});
        } else {
            long current = this.videoPlayer.getCurrentPosition();
            if (current < 0) {
                current = 0;
            }
            long total = this.videoPlayer.getDuration();
            if (total < 0) {
                total = 0;
            }
            if (total == C0539C.TIME_UNSET || current == C0539C.TIME_UNSET) {
                newText = String.format("%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)});
                this.videoPlayerTime.setText(newText);
            }
            if (!this.inPreview && this.videoTimelineView.getVisibility() == 0) {
                total = (long) (((float) total) * (this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()));
                current = (long) (((float) current) - (this.videoTimelineView.getLeftProgress() * ((float) total)));
                if (current > total) {
                    current = total;
                }
            }
            current /= 1000;
            total /= 1000;
            newText = String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(current / 60), Long.valueOf(current % 60), Long.valueOf(total / 60), Long.valueOf(total % 60)});
        }
        this.videoPlayerTime.setText(newText);
    }

    private void checkBufferedProgress(float progress) {
        if (!(!this.isStreaming || this.parentActivity == null || this.streamingAlertShown || this.videoPlayer == null)) {
            if (this.currentMessageObject != null) {
                Document document = this.currentMessageObject.getDocument();
                if (document != null && progress < 0.9f && ((((float) document.size) * progress >= 5242880.0f || progress >= 0.5f) && Math.abs(SystemClock.elapsedRealtime() - this.startedPlayTime) >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS)) {
                    if (this.videoPlayer.getDuration() == C0539C.TIME_UNSET) {
                        Toast.makeText(this.parentActivity, LocaleController.getString("VideoDoesNotSupportStreaming", R.string.VideoDoesNotSupportStreaming), 1).show();
                    }
                    this.streamingAlertShown = true;
                }
            }
        }
    }

    private void preparePlayer(Uri uri, boolean playWhenReady, boolean preview) {
        if (!preview) {
            this.currentPlayingVideoFile = uri;
        }
        if (this.parentActivity != null) {
            int i = 0;
            this.streamingAlertShown = false;
            this.startedPlayTime = SystemClock.elapsedRealtime();
            this.currentVideoFinishedLoading = false;
            this.lastBufferedPositionCheck = 0;
            this.seekToProgressPending = 0.0f;
            this.firstAnimationDelay = true;
            this.inPreview = preview;
            releasePlayer();
            if (this.videoTextureView == null) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity) {
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        if (PhotoViewer.this.textureImageView != null) {
                            ViewGroup.LayoutParams layoutParams = PhotoViewer.this.textureImageView.getLayoutParams();
                            layoutParams.width = getMeasuredWidth();
                            layoutParams.height = getMeasuredHeight();
                        }
                    }
                };
                this.aspectRatioFrameLayout.setVisibility(4);
                this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                this.videoTextureView = new TextureView(this.parentActivity);
                this.videoTextureView.setPivotX(0.0f);
                this.videoTextureView.setPivotY(0.0f);
                this.videoTextureView.setOpaque(false);
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            if (VERSION.SDK_INT >= 21 && this.textureImageView == null) {
                this.textureImageView = new ImageView(this.parentActivity);
                this.textureImageView.setBackgroundColor(-65536);
                this.textureImageView.setPivotX(0.0f);
                this.textureImageView.setPivotY(0.0f);
                this.textureImageView.setVisibility(4);
                this.containerView.addView(this.textureImageView);
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView.setAlpha(0.0f);
            this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            if (this.videoPlayer == null) {
                this.videoPlayer = new VideoPlayer();
                this.videoPlayer.setTextureView(this.videoTextureView);
                this.videoPlayer.setDelegate(new VideoPlayerDelegate() {

                    /* renamed from: org.telegram.ui.PhotoViewer$48$1 */
                    class C15961 implements DialogInterface.OnClickListener {
                        C15961() {
                        }

                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                                PhotoViewer.this.closePhoto(false, false);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    }

                    /* renamed from: org.telegram.ui.PhotoViewer$48$2 */
                    class C15972 extends AnimatorListenerAdapter {
                        C15972() {
                        }

                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.pipAnimationInProgress = false;
                        }
                    }

                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            if (PhotoViewer.this.isStreaming) {
                                PhotoViewer.this.toggleMiniProgress(playbackState == 2, true);
                            }
                            if (!playWhenReady || playbackState == 4 || playbackState == 1) {
                                try {
                                    PhotoViewer.this.parentActivity.getWindow().clearFlags(128);
                                    PhotoViewer.this.keepScreenOnFlagSet = false;
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            } else {
                                try {
                                    PhotoViewer.this.parentActivity.getWindow().addFlags(128);
                                    PhotoViewer.this.keepScreenOnFlagSet = true;
                                } catch (Throwable e2) {
                                    FileLog.m3e(e2);
                                }
                            }
                            if (PhotoViewer.this.seekToProgressPending != 0.0f && (playbackState == 3 || playbackState == 1)) {
                                PhotoViewer.this.videoPlayer.seekTo((long) ((int) (((float) PhotoViewer.this.videoPlayer.getDuration()) * PhotoViewer.this.seekToProgressPending)));
                                PhotoViewer.this.seekToProgressPending = 0.0f;
                            }
                            if (playbackState == 3) {
                                if (PhotoViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                    PhotoViewer.this.aspectRatioFrameLayout.setVisibility(0);
                                }
                                if (!PhotoViewer.this.pipItem.isEnabled()) {
                                    PhotoViewer.this.pipAvailable = true;
                                    PhotoViewer.this.pipItem.setEnabled(true);
                                    PhotoViewer.this.pipItem.setAlpha(1.0f);
                                }
                            }
                            if (!PhotoViewer.this.videoPlayer.isPlaying() || playbackState == 4) {
                                if (PhotoViewer.this.isPlaying) {
                                    PhotoViewer.this.isPlaying = false;
                                    PhotoViewer.this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
                                    AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.updateProgressRunnable);
                                    if (playbackState == 4) {
                                        if (!PhotoViewer.this.isCurrentVideo) {
                                            if (!PhotoViewer.this.isActionBarVisible) {
                                                PhotoViewer.this.toggleActionBar(true, true);
                                            }
                                            if (!PhotoViewer.this.videoPlayerSeekbar.isDragging()) {
                                                PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                                                PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                                                if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                                                    PhotoViewer.this.videoPlayer.seekTo(0);
                                                } else {
                                                    PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                                                }
                                                PhotoViewer.this.videoPlayer.pause();
                                            }
                                        } else if (!PhotoViewer.this.videoTimelineView.isDragging()) {
                                            PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                                            if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                                                PhotoViewer.this.videoPlayer.seekTo(0);
                                            } else {
                                                PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                                            }
                                            PhotoViewer.this.videoPlayer.pause();
                                            PhotoViewer.this.containerView.invalidate();
                                        }
                                        if (PhotoViewer.this.pipVideoView != null) {
                                            PhotoViewer.this.pipVideoView.onVideoCompleted();
                                        }
                                    }
                                }
                            } else if (!PhotoViewer.this.isPlaying) {
                                PhotoViewer.this.isPlaying = true;
                                PhotoViewer.this.videoPlayButton.setImageResource(R.drawable.inline_video_pause);
                                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable);
                            }
                            if (PhotoViewer.this.pipVideoView != null) {
                                PhotoViewer.this.pipVideoView.updatePlayButton();
                            }
                            PhotoViewer.this.updateVideoPlayerTime();
                        }
                    }

                    public void onError(Exception e) {
                        FileLog.m3e((Throwable) e);
                        if (PhotoViewer.this.menuItem.isSubItemVisible(11)) {
                            Builder builder = new Builder(PhotoViewer.this.parentActivity);
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setMessage(LocaleController.getString("CantPlayVideo", R.string.CantPlayVideo));
                            builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new C15961());
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            PhotoViewer.this.showAlertDialog(builder);
                        }
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
                        if (PhotoViewer.this.changingTextureView) {
                            PhotoViewer.this.changingTextureView = false;
                            if (PhotoViewer.this.isInline) {
                                if (PhotoViewer.this.isInline) {
                                    PhotoViewer.this.waitingForFirstTextureUpload = 1;
                                }
                                PhotoViewer.this.changedTextureView.setSurfaceTexture(surfaceTexture);
                                PhotoViewer.this.changedTextureView.setSurfaceTextureListener(PhotoViewer.this.surfaceTextureListener);
                                PhotoViewer.this.changedTextureView.setVisibility(0);
                                return true;
                            }
                        }
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                        if (PhotoViewer.this.waitingForFirstTextureUpload == 2) {
                            if (PhotoViewer.this.textureImageView != null) {
                                PhotoViewer.this.textureImageView.setVisibility(4);
                                PhotoViewer.this.textureImageView.setImageDrawable(null);
                                if (PhotoViewer.this.currentBitmap != null) {
                                    PhotoViewer.this.currentBitmap.recycle();
                                    PhotoViewer.this.currentBitmap = null;
                                }
                            }
                            PhotoViewer.this.switchingInlineMode = false;
                            if (VERSION.SDK_INT >= 21) {
                                PhotoViewer.this.aspectRatioFrameLayout.getLocationInWindow(PhotoViewer.this.pipPosition);
                                int[] access$13800 = PhotoViewer.this.pipPosition;
                                access$13800[0] = access$13800[0] - PhotoViewer.this.getLeftInset();
                                access$13800 = PhotoViewer.this.pipPosition;
                                access$13800[1] = (int) (((float) access$13800[1]) - PhotoViewer.this.containerView.getTranslationY());
                                AnimatorSet animatorSet = new AnimatorSet();
                                Animator[] animatorArr = new Animator[13];
                                animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, "scaleX", new float[]{1.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, "scaleY", new float[]{1.0f});
                                animatorArr[2] = ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, "translationX", new float[]{(float) PhotoViewer.this.pipPosition[0]});
                                animatorArr[3] = ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, "translationY", new float[]{(float) PhotoViewer.this.pipPosition[1]});
                                animatorArr[4] = ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, "scaleX", new float[]{1.0f});
                                animatorArr[5] = ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, "scaleY", new float[]{1.0f});
                                animatorArr[6] = ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, "translationX", new float[]{((float) PhotoViewer.this.pipPosition[0]) - PhotoViewer.this.aspectRatioFrameLayout.getX()});
                                animatorArr[7] = ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, "translationY", new float[]{((float) PhotoViewer.this.pipPosition[1]) - PhotoViewer.this.aspectRatioFrameLayout.getY()});
                                animatorArr[8] = ObjectAnimator.ofInt(PhotoViewer.this.backgroundDrawable, "alpha", new int[]{255});
                                animatorArr[9] = ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, "alpha", new float[]{1.0f});
                                animatorArr[10] = ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, "alpha", new float[]{1.0f});
                                animatorArr[11] = ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, "alpha", new float[]{1.0f});
                                animatorArr[12] = ObjectAnimator.ofFloat(PhotoViewer.this.groupedPhotosListView, "alpha", new float[]{1.0f});
                                animatorSet.playTogether(animatorArr);
                                animatorSet.setInterpolator(new DecelerateInterpolator());
                                animatorSet.setDuration(250);
                                animatorSet.addListener(new C15972());
                                animatorSet.start();
                            }
                            PhotoViewer.this.waitingForFirstTextureUpload = 0;
                        }
                    }
                });
            }
            this.videoPlayer.preparePlayer(uri, "other");
            this.videoPlayerSeekbar.setProgress(0.0f);
            this.videoTimelineView.setProgress(0.0f);
            this.videoPlayerSeekbar.setBufferedProgress(0.0f);
            if (this.currentBotInlineResult != null && (this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                this.bottomLayout.setVisibility(0);
                this.bottomLayout.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
            }
            FrameLayout frameLayout = this.videoPlayerControlFrameLayout;
            if (this.isCurrentVideo) {
                i = 8;
            }
            frameLayout.setVisibility(i);
            this.dateTextView.setVisibility(8);
            this.nameTextView.setVisibility(8);
            if (this.allowShare) {
                this.shareButton.setVisibility(8);
                this.menuItem.showSubItem(10);
            }
            this.videoPlayer.setPlayWhenReady(playWhenReady);
            this.inPreview = preview;
        }
    }

    private void releasePlayer() {
        if (this.videoPlayer != null) {
            this.videoPlayer.releasePlayer();
            this.videoPlayer = null;
        }
        toggleMiniProgress(false, false);
        this.pipAvailable = false;
        if (this.pipItem.isEnabled()) {
            this.pipItem.setEnabled(false);
            this.pipItem.setAlpha(0.5f);
        }
        if (this.keepScreenOnFlagSet) {
            try {
                this.parentActivity.getWindow().clearFlags(128);
                this.keepScreenOnFlagSet = false;
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
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
        if (!this.inPreview && !this.requestingPreview) {
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
        } else if (!(object instanceof BotInlineResult)) {
            if (object instanceof SearchImage) {
                caption = ((SearchImage) object).caption;
            }
        }
        if (caption != null) {
            if (caption.length() != 0) {
                this.captionEditText.setFieldText(caption);
                return;
            }
        }
        this.captionEditText.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public void showAlertDialog(Builder builder) {
        if (this.parentActivity != null) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
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
                FileLog.m3e(e2);
            }
        }
    }

    private void applyCurrentEditMode() {
        Bitmap bitmap = null;
        ArrayList<InputDocument> stickers = null;
        SavedFilterState savedFilterState = null;
        boolean removeSavedState = false;
        if (this.currentEditMode == 1) {
            bitmap = r0.photoCropView.getBitmap();
            removeSavedState = true;
        } else if (r0.currentEditMode == 2) {
            bitmap = r0.photoFilterView.getBitmap();
            savedFilterState = r0.photoFilterView.getSavedFilterState();
        } else if (r0.currentEditMode == 3) {
            bitmap = r0.photoPaintView.getBitmap();
            stickers = r0.photoPaintView.getMasks();
            removeSavedState = true;
        }
        if (bitmap != null) {
            PhotoSize size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
            if (size != null) {
                View view;
                PhotoEntry object = r0.imagesArrLocals.get(r0.currentIndex);
                PhotoEntry entry;
                int i;
                PhotoEntry object2;
                if (object instanceof PhotoEntry) {
                    SavedFilterState savedFilterState2;
                    PhotoEntry entry2 = object;
                    entry2.imagePath = FileLoader.getPathToAttach(size, true).toString();
                    entry = entry2;
                    i = -12734994;
                    object2 = object;
                    size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(120.0f), (float) AndroidUtilities.dp(120.0f), 70, false, 101, 101);
                    if (size != null) {
                        entry.thumbPath = FileLoader.getPathToAttach(size, true).toString();
                    }
                    if (stickers != null) {
                        entry.stickers.addAll(stickers);
                    }
                    if (r0.currentEditMode == 1) {
                        r0.cropItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        entry.isCropped = true;
                    } else if (r0.currentEditMode == 2) {
                        r0.tuneItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        entry.isFiltered = true;
                    } else if (r0.currentEditMode == 3) {
                        r0.paintItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        entry.isPainted = true;
                    }
                    if (savedFilterState != null) {
                        entry.savedFilterState = savedFilterState;
                    } else if (removeSavedState) {
                        savedFilterState2 = null;
                        entry.savedFilterState = null;
                        view = savedFilterState2;
                        entry = object2;
                    }
                    savedFilterState2 = null;
                    view = savedFilterState2;
                    entry = object2;
                } else {
                    i = -12734994;
                    object2 = object;
                    View view2 = null;
                    entry = object2;
                    if (entry instanceof SearchImage) {
                        SearchImage entry3 = (SearchImage) entry;
                        entry3.imagePath = FileLoader.getPathToAttach(size, true).toString();
                        SearchImage entry4 = entry3;
                        size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(120.0f), (float) AndroidUtilities.dp(120.0f), 70, false, 101, 101);
                        if (size != null) {
                            entry4.thumbPath = FileLoader.getPathToAttach(size, true).toString();
                        }
                        if (stickers != null) {
                            entry4.stickers.addAll(stickers);
                        }
                        if (r0.currentEditMode == 1) {
                            r0.cropItem.setColorFilter(new PorterDuffColorFilter(-12734994, Mode.MULTIPLY));
                            entry4.isCropped = true;
                        } else if (r0.currentEditMode == 2) {
                            r0.tuneItem.setColorFilter(new PorterDuffColorFilter(-12734994, Mode.MULTIPLY));
                            entry4.isFiltered = true;
                        } else if (r0.currentEditMode == 3) {
                            r0.paintItem.setColorFilter(new PorterDuffColorFilter(-12734994, Mode.MULTIPLY));
                            entry4.isPainted = true;
                        }
                        if (savedFilterState != null) {
                            entry4.savedFilterState = savedFilterState;
                        } else if (removeSavedState) {
                            view = null;
                            entry4.savedFilterState = null;
                        }
                        view = null;
                    } else {
                        view = view2;
                    }
                }
                if (r0.sendPhotoType == 0 && r0.placeProvider != null) {
                    r0.placeProvider.updatePhotoAtIndex(r0.currentIndex);
                    if (!r0.placeProvider.isPhotoChecked(r0.currentIndex)) {
                        setPhotoChecked();
                    }
                }
                if (r0.currentEditMode == 1) {
                    float scaleX = r0.photoCropView.getRectSizeX() / ((float) getContainerViewWidth());
                    float scaleY = r0.photoCropView.getRectSizeY() / ((float) getContainerViewHeight());
                    r0.scale = scaleX > scaleY ? scaleX : scaleY;
                    r0.translationX = (r0.photoCropView.getRectX() + (r0.photoCropView.getRectSizeX() / 2.0f)) - ((float) (getContainerViewWidth() / 2));
                    r0.translationY = (r0.photoCropView.getRectY() + (r0.photoCropView.getRectSizeY() / 2.0f)) - ((float) (getContainerViewHeight() / 2));
                    r0.zoomAnimation = true;
                    r0.applying = true;
                    r0.photoCropView.onDisappear();
                }
                r0.centerImage.setParentView(view);
                r0.centerImage.setOrientation(0, true);
                r0.ignoreDidSetImage = true;
                r0.centerImage.setImageBitmap(bitmap);
                r0.ignoreDidSetImage = false;
                r0.centerImage.setParentView(r0.containerView);
            }
        }
    }

    private void setPhotoChecked() {
        if (this.placeProvider != null) {
            int num = this.placeProvider.setPhotoChecked(this.currentIndex, getCurrentVideoEditedInfo());
            boolean checked = this.placeProvider.isPhotoChecked(this.currentIndex);
            this.checkImageView.setChecked(checked, true);
            if (num >= 0) {
                if (this.placeProvider.allowGroupPhotos()) {
                    num++;
                }
                if (checked) {
                    this.selectedPhotosAdapter.notifyItemInserted(num);
                    this.selectedPhotosListView.smoothScrollToPosition(num);
                } else {
                    this.selectedPhotosAdapter.notifyItemRemoved(num);
                }
            }
            updateSelectedCount();
        }
    }

    private void switchToEditMode(int mode) {
        final int i = mode;
        if (this.currentEditMode != i && r0.centerImage.getBitmap() != null && r0.changeModeAnimation == null && r0.imageMoveAnimation == null && r0.photoProgressViews[0].backgroundState == -1) {
            if (r0.captionEditText.getTag() == null) {
                if (i == 0) {
                    if (r0.centerImage.getBitmap() != null) {
                        int bitmapWidth = r0.centerImage.getBitmapWidth();
                        int bitmapHeight = r0.centerImage.getBitmapHeight();
                        float scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                        float scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                        float newScaleX = ((float) getContainerViewWidth(0)) / ((float) bitmapWidth);
                        float newScaleY = ((float) getContainerViewHeight(0)) / ((float) bitmapHeight);
                        float scale = scaleX > scaleY ? scaleY : scaleX;
                        float newScale = newScaleX > newScaleY ? newScaleY : newScaleX;
                        if (r0.sendPhotoType != 1 || r0.applying) {
                            r0.animateToScale = newScale / scale;
                        } else {
                            float minSide = (float) Math.min(getContainerViewWidth(), getContainerViewHeight());
                            scaleX = minSide / ((float) bitmapWidth);
                            scaleY = minSide / ((float) bitmapHeight);
                            float fillScale = scaleX > scaleY ? scaleX : scaleY;
                            r0.scale = fillScale / scale;
                            r0.animateToScale = (r0.scale * newScale) / fillScale;
                        }
                        r0.animateToX = 0.0f;
                        if (r0.currentEditMode == 1) {
                            r0.animateToY = (float) AndroidUtilities.dp(58.0f);
                        } else if (r0.currentEditMode == 2) {
                            r0.animateToY = (float) AndroidUtilities.dp(92.0f);
                        } else if (r0.currentEditMode == 3) {
                            r0.animateToY = (float) AndroidUtilities.dp(44.0f);
                        }
                        if (VERSION.SDK_INT >= 21) {
                            r0.animateToY -= (float) (AndroidUtilities.statusBarHeight / 2);
                        }
                        r0.animationStartTime = System.currentTimeMillis();
                        r0.zoomAnimation = true;
                    }
                    r0.imageMoveAnimation = new AnimatorSet();
                    AnimatorSet animatorSet;
                    Animator[] animatorArr;
                    if (r0.currentEditMode == 1) {
                        animatorSet = r0.imageMoveAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(r0.editorDoneLayout, "translationY", new float[]{(float) AndroidUtilities.dp(48.0f)});
                        animatorArr[1] = ObjectAnimator.ofFloat(r0, "animationValue", new float[]{0.0f, 1.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(r0.photoCropView, "alpha", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    } else if (r0.currentEditMode == 2) {
                        r0.photoFilterView.shutdown();
                        animatorSet = r0.imageMoveAnimation;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(r0.photoFilterView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(186.0f)});
                        animatorArr[1] = ObjectAnimator.ofFloat(r0, "animationValue", new float[]{0.0f, 1.0f});
                        animatorSet.playTogether(animatorArr);
                    } else if (r0.currentEditMode == 3) {
                        r0.photoPaintView.shutdown();
                        animatorSet = r0.imageMoveAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(r0.photoPaintView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(126.0f)});
                        animatorArr[1] = ObjectAnimator.ofFloat(r0.photoPaintView.getColorPicker(), "translationY", new float[]{(float) AndroidUtilities.dp(126.0f)});
                        animatorArr[2] = ObjectAnimator.ofFloat(r0, "animationValue", new float[]{0.0f, 1.0f});
                        animatorSet.playTogether(animatorArr);
                    }
                    r0.imageMoveAnimation.setDuration(200);
                    r0.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {

                        /* renamed from: org.telegram.ui.PhotoViewer$50$1 */
                        class C16001 extends AnimatorListenerAdapter {
                            C16001() {
                            }

                            public void onAnimationStart(Animator animation) {
                                PhotoViewer.this.pickerView.setVisibility(0);
                                PhotoViewer.this.pickerViewSendButton.setVisibility(0);
                                PhotoViewer.this.actionBar.setVisibility(0);
                                if (PhotoViewer.this.needCaptionLayout) {
                                    PhotoViewer.this.captionTextView.setVisibility(PhotoViewer.this.captionTextView.getTag() != null ? 0 : 4);
                                }
                                if (PhotoViewer.this.sendPhotoType == 0 || (PhotoViewer.this.sendPhotoType == 2 && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                    PhotoViewer.this.checkImageView.setVisibility(0);
                                    PhotoViewer.this.photosCounterView.setVisibility(0);
                                }
                            }
                        }

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
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, "translationY", new float[]{0.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, "translationY", new float[]{0.0f}));
                            if (PhotoViewer.this.needCaptionLayout) {
                                arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, "translationY", new float[]{0.0f}));
                            }
                            if (PhotoViewer.this.sendPhotoType == 0) {
                                arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, "alpha", new float[]{1.0f}));
                                arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.photosCounterView, "alpha", new float[]{1.0f}));
                            }
                            if (PhotoViewer.this.cameraItem.getTag() != null) {
                                PhotoViewer.this.cameraItem.setVisibility(0);
                                arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.cameraItem, "alpha", new float[]{1.0f}));
                            }
                            animatorSet.playTogether(arrayList);
                            animatorSet.setDuration(200);
                            animatorSet.addListener(new C16001());
                            animatorSet.start();
                        }
                    });
                    r0.imageMoveAnimation.start();
                } else if (i == 1) {
                    if (r0.photoCropView == null) {
                        r0.photoCropView = new PhotoCropView(r0.actvityContext);
                        r0.photoCropView.setVisibility(8);
                        r0.containerView.addView(r0.photoCropView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                        r0.photoCropView.setDelegate(new PhotoCropViewDelegate() {
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
                    r0.photoCropView.onAppear();
                    r0.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", R.string.Crop));
                    r0.editorDoneLayout.doneButton.setTextColor(-11420173);
                    r0.changeModeAnimation = new AnimatorSet();
                    arrayList = new ArrayList();
                    arrayList.add(ObjectAnimator.ofFloat(r0.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    arrayList.add(ObjectAnimator.ofFloat(r0.pickerViewSendButton, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    arrayList.add(ObjectAnimator.ofFloat(r0.actionBar, "translationY", new float[]{0.0f, (float) (-r0.actionBar.getHeight())}));
                    if (r0.needCaptionLayout) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.captionTextView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    }
                    if (r0.sendPhotoType == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.checkImageView, "alpha", new float[]{1.0f, 0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.photosCounterView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.selectedPhotosListView.getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.selectedPhotosListView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.cameraItem.getTag() != null) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.cameraItem, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    r0.changeModeAnimation.playTogether(arrayList);
                    r0.changeModeAnimation.setDuration(200);
                    r0.changeModeAnimation.addListener(new AnimatorListenerAdapter() {

                        /* renamed from: org.telegram.ui.PhotoViewer$52$1 */
                        class C16011 extends AnimatorListenerAdapter {
                            C16011() {
                            }

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
                        }

                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.changeModeAnimation = null;
                            PhotoViewer.this.pickerView.setVisibility(8);
                            PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                            PhotoViewer.this.cameraItem.setVisibility(8);
                            PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                            PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                            PhotoViewer.this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                            PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                            PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                            PhotoViewer.this.isPhotosListViewVisible = false;
                            if (PhotoViewer.this.needCaptionLayout) {
                                PhotoViewer.this.captionTextView.setVisibility(4);
                            }
                            if (PhotoViewer.this.sendPhotoType == 0 || (PhotoViewer.this.sendPhotoType == 2 && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                PhotoViewer.this.checkImageView.setVisibility(8);
                                PhotoViewer.this.photosCounterView.setVisibility(8);
                            }
                            Bitmap bitmap = PhotoViewer.this.centerImage.getBitmap();
                            if (bitmap != null) {
                                PhotoViewer.this.photoCropView.setBitmap(bitmap, PhotoViewer.this.centerImage.getOrientation(), PhotoViewer.this.sendPhotoType != 1);
                                int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                                int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                                float scaleX = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                                float scaleY = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                                float newScaleX = ((float) PhotoViewer.this.getContainerViewWidth(1)) / ((float) bitmapWidth);
                                float newScaleY = ((float) PhotoViewer.this.getContainerViewHeight(1)) / ((float) bitmapHeight);
                                float scale = scaleX > scaleY ? scaleY : scaleX;
                                float newScale = newScaleX > newScaleY ? newScaleY : newScaleX;
                                if (PhotoViewer.this.sendPhotoType == 1) {
                                    float minSide = (float) Math.min(PhotoViewer.this.getContainerViewWidth(1), PhotoViewer.this.getContainerViewHeight(1));
                                    newScaleX = minSide / ((float) bitmapWidth);
                                    newScaleY = minSide / ((float) bitmapHeight);
                                    newScale = newScaleX > newScaleY ? newScaleX : newScaleY;
                                }
                                PhotoViewer.this.animateToScale = newScale / scale;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(56.0f)) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                                PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                                PhotoViewer.this.zoomAnimation = true;
                            }
                            PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                            AnimatorSet access$14500 = PhotoViewer.this.imageMoveAnimation;
                            r3 = new Animator[3];
                            r3[0] = ObjectAnimator.ofFloat(PhotoViewer.this.editorDoneLayout, "translationY", new float[]{(float) AndroidUtilities.dp(48.0f), 0.0f});
                            r3[1] = ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[]{0.0f, 1.0f});
                            r3[2] = ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, "alpha", new float[]{0.0f, 1.0f});
                            access$14500.playTogether(r3);
                            PhotoViewer.this.imageMoveAnimation.setDuration(200);
                            PhotoViewer.this.imageMoveAnimation.addListener(new C16011());
                            PhotoViewer.this.imageMoveAnimation.start();
                        }
                    });
                    r0.changeModeAnimation.start();
                } else if (i == 2) {
                    if (r0.photoFilterView == null) {
                        Bitmap bitmap;
                        SavedFilterState state = null;
                        String originalPath = null;
                        int orientation = 0;
                        if (!r0.imagesArrLocals.isEmpty()) {
                            PhotoEntry object = r0.imagesArrLocals.get(r0.currentIndex);
                            if (object instanceof PhotoEntry) {
                                PhotoEntry entry = object;
                                if (entry.imagePath == null) {
                                    originalPath = entry.path;
                                    state = entry.savedFilterState;
                                }
                                orientation = entry.orientation;
                            } else if (object instanceof SearchImage) {
                                SearchImage entry2 = (SearchImage) object;
                                state = entry2.savedFilterState;
                                originalPath = entry2.imageUrl;
                            }
                        }
                        if (state == null) {
                            bitmap = r0.centerImage.getBitmap();
                            orientation = r0.centerImage.getOrientation();
                        } else {
                            bitmap = BitmapFactory.decodeFile(originalPath);
                        }
                        r0.photoFilterView = new PhotoFilterView(r0.parentActivity, bitmap, orientation, state);
                        r0.containerView.addView(r0.photoFilterView, LayoutHelper.createFrame(-1, -1.0f));
                        r0.photoFilterView.getDoneTextView().setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                PhotoViewer.this.applyCurrentEditMode();
                                PhotoViewer.this.switchToEditMode(0);
                            }
                        });
                        r0.photoFilterView.getCancelTextView().setOnClickListener(new OnClickListener() {

                            /* renamed from: org.telegram.ui.PhotoViewer$54$1 */
                            class C16021 implements DialogInterface.OnClickListener {
                                C16021() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    PhotoViewer.this.switchToEditMode(0);
                                }
                            }

                            public void onClick(View v) {
                                if (!PhotoViewer.this.photoFilterView.hasChanges()) {
                                    PhotoViewer.this.switchToEditMode(0);
                                } else if (PhotoViewer.this.parentActivity != null) {
                                    Builder builder = new Builder(PhotoViewer.this.parentActivity);
                                    builder.setMessage(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C16021());
                                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                    PhotoViewer.this.showAlertDialog(builder);
                                }
                            }
                        });
                        r0.photoFilterView.getToolsView().setTranslationY((float) AndroidUtilities.dp(186.0f));
                    }
                    r0.changeModeAnimation = new AnimatorSet();
                    arrayList = new ArrayList();
                    arrayList.add(ObjectAnimator.ofFloat(r0.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    arrayList.add(ObjectAnimator.ofFloat(r0.pickerViewSendButton, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    arrayList.add(ObjectAnimator.ofFloat(r0.actionBar, "translationY", new float[]{0.0f, (float) (-r0.actionBar.getHeight())}));
                    if (r0.sendPhotoType == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.checkImageView, "alpha", new float[]{1.0f, 0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.photosCounterView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.selectedPhotosListView.getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.selectedPhotosListView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.cameraItem.getTag() != null) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.cameraItem, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    r0.changeModeAnimation.playTogether(arrayList);
                    r0.changeModeAnimation.setDuration(200);
                    r0.changeModeAnimation.addListener(new AnimatorListenerAdapter() {

                        /* renamed from: org.telegram.ui.PhotoViewer$55$1 */
                        class C16031 extends AnimatorListenerAdapter {
                            C16031() {
                            }

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
                        }

                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.changeModeAnimation = null;
                            PhotoViewer.this.pickerView.setVisibility(8);
                            PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                            PhotoViewer.this.actionBar.setVisibility(8);
                            PhotoViewer.this.cameraItem.setVisibility(8);
                            PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                            PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                            PhotoViewer.this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                            PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                            PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                            PhotoViewer.this.isPhotosListViewVisible = false;
                            if (PhotoViewer.this.needCaptionLayout) {
                                PhotoViewer.this.captionTextView.setVisibility(4);
                            }
                            if (PhotoViewer.this.sendPhotoType == 0 || (PhotoViewer.this.sendPhotoType == 2 && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                PhotoViewer.this.checkImageView.setVisibility(8);
                                PhotoViewer.this.photosCounterView.setVisibility(8);
                            }
                            if (PhotoViewer.this.centerImage.getBitmap() != null) {
                                int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                                int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                                float scaleX = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                                float scaleY = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                                float newScaleX = ((float) PhotoViewer.this.getContainerViewWidth(2)) / ((float) bitmapWidth);
                                float newScaleY = ((float) PhotoViewer.this.getContainerViewHeight(2)) / ((float) bitmapHeight);
                                PhotoViewer.this.animateToScale = (newScaleX > newScaleY ? newScaleY : newScaleX) / (scaleX > scaleY ? scaleY : scaleX);
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(92.0f)) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                                PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                                PhotoViewer.this.zoomAnimation = true;
                            }
                            PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                            AnimatorSet access$14500 = PhotoViewer.this.imageMoveAnimation;
                            Animator[] animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[]{0.0f, 1.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(PhotoViewer.this.photoFilterView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(186.0f), 0.0f});
                            access$14500.playTogether(animatorArr);
                            PhotoViewer.this.imageMoveAnimation.setDuration(200);
                            PhotoViewer.this.imageMoveAnimation.addListener(new C16031());
                            PhotoViewer.this.imageMoveAnimation.start();
                        }
                    });
                    r0.changeModeAnimation.start();
                } else if (i == 3) {
                    if (r0.photoPaintView == null) {
                        r0.photoPaintView = new PhotoPaintView(r0.parentActivity, r0.centerImage.getBitmap(), r0.centerImage.getOrientation());
                        r0.containerView.addView(r0.photoPaintView, LayoutHelper.createFrame(-1, -1.0f));
                        r0.photoPaintView.getDoneTextView().setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                PhotoViewer.this.applyCurrentEditMode();
                                PhotoViewer.this.switchToEditMode(0);
                            }
                        });
                        r0.photoPaintView.getCancelTextView().setOnClickListener(new OnClickListener() {

                            /* renamed from: org.telegram.ui.PhotoViewer$57$1 */
                            class C16041 implements Runnable {
                                C16041() {
                                }

                                public void run() {
                                    PhotoViewer.this.switchToEditMode(0);
                                }
                            }

                            public void onClick(View v) {
                                PhotoViewer.this.photoPaintView.maybeShowDismissalAlert(PhotoViewer.this, PhotoViewer.this.parentActivity, new C16041());
                            }
                        });
                        r0.photoPaintView.getColorPicker().setTranslationY((float) AndroidUtilities.dp(126.0f));
                        r0.photoPaintView.getToolsView().setTranslationY((float) AndroidUtilities.dp(126.0f));
                    }
                    r0.changeModeAnimation = new AnimatorSet();
                    arrayList = new ArrayList();
                    arrayList.add(ObjectAnimator.ofFloat(r0.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    arrayList.add(ObjectAnimator.ofFloat(r0.pickerViewSendButton, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    arrayList.add(ObjectAnimator.ofFloat(r0.actionBar, "translationY", new float[]{0.0f, (float) (-r0.actionBar.getHeight())}));
                    if (r0.needCaptionLayout) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.captionTextView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    }
                    if (r0.sendPhotoType == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.checkImageView, "alpha", new float[]{1.0f, 0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(r0.photosCounterView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.selectedPhotosListView.getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.selectedPhotosListView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.cameraItem.getTag() != null) {
                        arrayList.add(ObjectAnimator.ofFloat(r0.cameraItem, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    r0.changeModeAnimation.playTogether(arrayList);
                    r0.changeModeAnimation.setDuration(200);
                    r0.changeModeAnimation.addListener(new AnimatorListenerAdapter() {

                        /* renamed from: org.telegram.ui.PhotoViewer$58$1 */
                        class C16051 extends AnimatorListenerAdapter {
                            C16051() {
                            }

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
                        }

                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.changeModeAnimation = null;
                            PhotoViewer.this.pickerView.setVisibility(8);
                            PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                            PhotoViewer.this.cameraItem.setVisibility(8);
                            PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                            PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                            PhotoViewer.this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                            PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                            PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                            PhotoViewer.this.isPhotosListViewVisible = false;
                            if (PhotoViewer.this.needCaptionLayout) {
                                PhotoViewer.this.captionTextView.setVisibility(4);
                            }
                            if (PhotoViewer.this.sendPhotoType == 0 || (PhotoViewer.this.sendPhotoType == 2 && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                PhotoViewer.this.checkImageView.setVisibility(8);
                                PhotoViewer.this.photosCounterView.setVisibility(8);
                            }
                            if (PhotoViewer.this.centerImage.getBitmap() != null) {
                                int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                                int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                                float scaleX = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                                float scaleY = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                                float newScaleX = ((float) PhotoViewer.this.getContainerViewWidth(3)) / ((float) bitmapWidth);
                                float newScaleY = ((float) PhotoViewer.this.getContainerViewHeight(3)) / ((float) bitmapHeight);
                                PhotoViewer.this.animateToScale = (newScaleX > newScaleY ? newScaleY : newScaleX) / (scaleX > scaleY ? scaleY : scaleX);
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(44.0f)) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                                PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                                PhotoViewer.this.zoomAnimation = true;
                            }
                            PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                            AnimatorSet access$14500 = PhotoViewer.this.imageMoveAnimation;
                            r3 = new Animator[3];
                            r3[1] = ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getColorPicker(), "translationY", new float[]{(float) AndroidUtilities.dp(126.0f), 0.0f});
                            r3[2] = ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(126.0f), 0.0f});
                            access$14500.playTogether(r3);
                            PhotoViewer.this.imageMoveAnimation.setDuration(200);
                            PhotoViewer.this.imageMoveAnimation.addListener(new C16051());
                            PhotoViewer.this.imageMoveAnimation.start();
                        }
                    });
                    r0.changeModeAnimation.start();
                }
            }
        }
    }

    private void toggleCheckImageView(boolean show) {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> arrayList = new ArrayList();
        FrameLayout frameLayout = this.pickerView;
        String str = "alpha";
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = show ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout, str, fArr));
        ImageView imageView = this.pickerViewSendButton;
        str = "alpha";
        fArr = new float[1];
        fArr[0] = show ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(imageView, str, fArr));
        if (this.needCaptionLayout) {
            TextView textView = this.captionTextView;
            str = "alpha";
            fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr));
        }
        if (this.sendPhotoType == 0) {
            CheckBox checkBox = this.checkImageView;
            str = "alpha";
            fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(checkBox, str, fArr));
            CounterView counterView = this.photosCounterView;
            str = "alpha";
            float[] fArr2 = new float[1];
            if (show) {
                f = 1.0f;
            }
            fArr2[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(counterView, str, fArr2));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void toggleMiniProgressInternal(final boolean show) {
        if (show) {
            this.miniProgressView.setVisibility(0);
        }
        this.miniProgressAnimator = new AnimatorSet();
        AnimatorSet animatorSet = this.miniProgressAnimator;
        Animator[] animatorArr = new Animator[1];
        RadialProgressView radialProgressView = this.miniProgressView;
        String str = "alpha";
        float[] fArr = new float[1];
        fArr[0] = show ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, str, fArr);
        animatorSet.playTogether(animatorArr);
        this.miniProgressAnimator.setDuration(200);
        this.miniProgressAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PhotoViewer.this.miniProgressAnimator)) {
                    if (!show) {
                        PhotoViewer.this.miniProgressView.setVisibility(4);
                    }
                    PhotoViewer.this.miniProgressAnimator = null;
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (animation.equals(PhotoViewer.this.miniProgressAnimator)) {
                    PhotoViewer.this.miniProgressAnimator = null;
                }
            }
        });
        this.miniProgressAnimator.start();
    }

    private void toggleMiniProgress(boolean show, boolean animated) {
        int i = 0;
        if (animated) {
            toggleMiniProgressInternal(show);
            if (show) {
                if (this.miniProgressAnimator != null) {
                    this.miniProgressAnimator.cancel();
                    this.miniProgressAnimator = null;
                }
                AndroidUtilities.cancelRunOnUIThread(this.miniProgressShowRunnable);
                if (this.firstAnimationDelay) {
                    this.firstAnimationDelay = false;
                    toggleMiniProgressInternal(true);
                    return;
                }
                AndroidUtilities.runOnUIThread(this.miniProgressShowRunnable, 500);
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.miniProgressShowRunnable);
            if (this.miniProgressAnimator != null) {
                this.miniProgressAnimator.cancel();
                toggleMiniProgressInternal(false);
                return;
            }
            return;
        }
        if (this.miniProgressAnimator != null) {
            this.miniProgressAnimator.cancel();
            this.miniProgressAnimator = null;
        }
        this.miniProgressView.setAlpha(show ? 1.0f : 0.0f);
        RadialProgressView radialProgressView = this.miniProgressView;
        if (!show) {
            i = 4;
        }
        radialProgressView.setVisibility(i);
    }

    private void toggleActionBar(final boolean show, boolean animated) {
        if (this.actionBarAnimator != null) {
            this.actionBarAnimator.cancel();
        }
        if (show) {
            this.actionBar.setVisibility(0);
            if (this.bottomLayout.getTag() != null) {
                this.bottomLayout.setVisibility(0);
            }
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(0);
            }
        }
        this.isActionBarVisible = show;
        if (VERSION.SDK_INT >= 21) {
            LayoutParams layoutParams;
            if (show) {
                if ((this.windowLayoutParams.flags & 1024) != 0) {
                    layoutParams = this.windowLayoutParams;
                    layoutParams.flags &= -1025;
                    if (this.windowView != null) {
                        try {
                            ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
                        } catch (Exception e) {
                        }
                    }
                }
            } else if ((this.windowLayoutParams.flags & 1024) == 0) {
                layoutParams = this.windowLayoutParams;
                layoutParams.flags |= 1024;
                if (this.windowView != null) {
                    try {
                        ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
                    } catch (Exception e2) {
                    }
                }
            }
        }
        float f = 0.0f;
        if (animated) {
            ArrayList<Animator> arrayList = new ArrayList();
            ActionBar actionBar = this.actionBar;
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, str, fArr));
            if (this.bottomLayout != null) {
                FrameLayout frameLayout = this.bottomLayout;
                str = "alpha";
                fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(frameLayout, str, fArr));
            }
            GroupedPhotosListView groupedPhotosListView = this.groupedPhotosListView;
            str = "alpha";
            fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView, str, fArr));
            if (this.captionTextView.getTag() != null) {
                TextView textView = this.captionTextView;
                str = "alpha";
                float[] fArr2 = new float[1];
                if (show) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr2));
            }
            this.actionBarAnimator = new AnimatorSet();
            this.actionBarAnimator.playTogether(arrayList);
            this.actionBarAnimator.setDuration(200);
            this.actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(PhotoViewer.this.actionBarAnimator)) {
                        if (!show) {
                            PhotoViewer.this.actionBar.setVisibility(4);
                            if (PhotoViewer.this.bottomLayout.getTag() != null) {
                                PhotoViewer.this.bottomLayout.setVisibility(4);
                            }
                            if (PhotoViewer.this.captionTextView.getTag() != null) {
                                PhotoViewer.this.captionTextView.setVisibility(4);
                            }
                        }
                        PhotoViewer.this.actionBarAnimator = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(PhotoViewer.this.actionBarAnimator)) {
                        PhotoViewer.this.actionBarAnimator = null;
                    }
                }
            });
            this.actionBarAnimator.start();
            return;
        }
        this.actionBar.setAlpha(show ? 1.0f : 0.0f);
        this.bottomLayout.setAlpha(show ? 1.0f : 0.0f);
        this.groupedPhotosListView.setAlpha(show ? 1.0f : 0.0f);
        TextView textView2 = this.captionTextView;
        if (show) {
            f = 1.0f;
        }
        textView2.setAlpha(f);
    }

    private void togglePhotosListView(boolean show, boolean animated) {
        if (show != this.isPhotosListViewVisible) {
            if (show) {
                this.selectedPhotosListView.setVisibility(0);
            }
            this.isPhotosListViewVisible = show;
            this.selectedPhotosListView.setEnabled(show);
            float f = 1.0f;
            if (animated) {
                ArrayList<Animator> arrayList = new ArrayList();
                RecyclerListView recyclerListView = this.selectedPhotosListView;
                String str = "alpha";
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(recyclerListView, str, fArr));
                recyclerListView = this.selectedPhotosListView;
                str = "translationY";
                fArr = new float[1];
                fArr[0] = show ? 0.0f : (float) (-AndroidUtilities.dp(10.0f));
                arrayList.add(ObjectAnimator.ofFloat(recyclerListView, str, fArr));
                CounterView counterView = this.photosCounterView;
                String str2 = "rotationX";
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(counterView, str2, fArr2));
                this.currentListViewAnimation = new AnimatorSet();
                this.currentListViewAnimation.playTogether(arrayList);
                if (!show) {
                    this.currentListViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (PhotoViewer.this.currentListViewAnimation != null && PhotoViewer.this.currentListViewAnimation.equals(animation)) {
                                PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                                PhotoViewer.this.currentListViewAnimation = null;
                            }
                        }
                    });
                }
                this.currentListViewAnimation.setDuration(200);
                this.currentListViewAnimation.start();
            } else {
                this.selectedPhotosListView.setAlpha(show ? 1.0f : 0.0f);
                this.selectedPhotosListView.setTranslationY(show ? 0.0f : (float) (-AndroidUtilities.dp(10.0f)));
                CounterView counterView2 = this.photosCounterView;
                if (!show) {
                    f = 0.0f;
                }
                counterView2.setRotationX(f);
                if (!show) {
                    this.selectedPhotosListView.setVisibility(8);
                }
            }
        }
    }

    private String getFileName(int index) {
        if (index < 0) {
            return null;
        }
        if (this.imagesArrLocations.isEmpty()) {
            if (this.imagesArr.isEmpty()) {
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
                        searchImage.localUrl = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(Utilities.MD5(searchImage.imageUrl));
                    stringBuilder.append(".");
                    stringBuilder.append(ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg"));
                    return stringBuilder.toString();
                }
                if (object instanceof BotInlineResult) {
                    BotInlineResult botInlineResult = (BotInlineResult) object;
                    if (botInlineResult.document != null) {
                        return FileLoader.getAttachFileName(botInlineResult.document);
                    }
                    if (botInlineResult.photo != null) {
                        return FileLoader.getAttachFileName(FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize()));
                    }
                    if (botInlineResult.content instanceof TL_webDocument) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(Utilities.MD5(botInlineResult.content.url));
                        stringBuilder2.append(".");
                        stringBuilder2.append(ImageLoader.getHttpUrlExtension(botInlineResult.content.url, FileLoader.getExtensionByMime(botInlineResult.content.mime_type)));
                        return stringBuilder2.toString();
                    }
                }
                return null;
            }
        }
        if (this.imagesArrLocations.isEmpty()) {
            if (this.imagesArr.isEmpty() || index >= this.imagesArr.size()) {
                return null;
            }
            return FileLoader.getMessageFileName(((MessageObject) this.imagesArr.get(index)).messageOwner);
        } else if (index >= this.imagesArrLocations.size()) {
            return null;
        } else {
            FileLocation location = (FileLocation) this.imagesArrLocations.get(index);
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(location.volume_id);
            stringBuilder3.append("_");
            stringBuilder3.append(location.local_id);
            stringBuilder3.append(".jpg");
            return stringBuilder3.toString();
        }
    }

    private TLObject getFileLocation(int index, int[] size) {
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
                    if (size != null) {
                        size[0] = sizeFull.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return sizeFull.location;
                } else if (size != null) {
                    size[0] = -1;
                }
            } else if (((message.messageOwner.media instanceof TL_messageMediaPhoto) && message.messageOwner.media.photo != null) || ((message.messageOwner.media instanceof TL_messageMediaWebPage) && message.messageOwner.media.webpage != null)) {
                sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    if (size != null) {
                        size[0] = sizeFull.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return sizeFull.location;
                } else if (size != null) {
                    size[0] = -1;
                }
            } else if (message.messageOwner.media instanceof TL_messageMediaInvoice) {
                return ((TL_messageMediaInvoice) message.messageOwner.media).photo;
            } else {
                if (!(message.getDocument() == null || message.getDocument().thumb == null)) {
                    if (size != null) {
                        size[0] = message.getDocument().thumb.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return message.getDocument().thumb.location;
                }
            }
            return null;
        } else if (index >= this.imagesArrLocations.size()) {
            return null;
        } else {
            if (size != null) {
                size[0] = ((Integer) this.imagesArrLocationsSizes.get(index)).intValue();
            }
            return (TLObject) this.imagesArrLocations.get(index);
        }
    }

    private void updateSelectedCount() {
        if (this.placeProvider != null) {
            int count = this.placeProvider.getSelectedCount();
            this.photosCounterView.setCount(count);
            if (count == 0) {
                togglePhotosListView(false, true);
            }
        }
    }

    private void onPhotoShow(MessageObject messageObject, FileLocation fileLocation, ArrayList<MessageObject> messages, ArrayList<Object> photos, int index, PlaceProviderObject object) {
        int a;
        User user;
        PhotoEntry entry;
        User user2;
        PhotoEntry photoEntry;
        MessageObject messageObject2 = messageObject;
        FileLocation fileLocation2 = fileLocation;
        ArrayList<MessageObject> arrayList = messages;
        ArrayList<Object> arrayList2 = photos;
        int i = index;
        PlaceProviderObject placeProviderObject = object;
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.currentMessageObject = null;
        this.currentFileLocation = null;
        this.currentPathObject = null;
        this.fromCamera = false;
        this.currentBotInlineResult = null;
        this.currentIndex = -1;
        this.currentFileNames[0] = null;
        boolean z = true;
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
        r0.opennedFromMedia = false;
        r0.needCaptionLayout = false;
        r0.containerView.setTag(Integer.valueOf(1));
        r0.isCurrentVideo = false;
        r0.imagesArr.clear();
        r0.imagesArrLocations.clear();
        r0.imagesArrLocationsSizes.clear();
        r0.avatarsArr.clear();
        r0.imagesArrLocals.clear();
        for (a = 0; a < 2; a++) {
            r0.imagesByIds[a].clear();
            r0.imagesByIdsTemp[a].clear();
        }
        r0.imagesArrTemp.clear();
        r0.currentUserAvatarLocation = null;
        r0.containerView.setPadding(0, 0, 0, 0);
        if (r0.currentThumb != null) {
            r0.currentThumb.release();
        }
        r0.currentThumb = placeProviderObject != null ? placeProviderObject.thumb : null;
        boolean z2 = placeProviderObject != null && placeProviderObject.isEvent;
        r0.isEvent = z2;
        r0.menuItem.setVisibility(0);
        int i2 = 8;
        r0.sendItem.setVisibility(8);
        r0.pipItem.setVisibility(8);
        r0.cameraItem.setVisibility(8);
        r0.cameraItem.setTag(null);
        r0.bottomLayout.setVisibility(0);
        r0.bottomLayout.setTag(Integer.valueOf(1));
        r0.bottomLayout.setTranslationY(0.0f);
        r0.captionTextView.setTranslationY(0.0f);
        r0.shareButton.setVisibility(8);
        if (r0.qualityChooseView != null) {
            r0.qualityChooseView.setVisibility(4);
            r0.qualityPicker.setVisibility(4);
            r0.qualityChooseView.setTag(null);
        }
        if (r0.qualityChooseViewAnimation != null) {
            r0.qualityChooseViewAnimation.cancel();
            r0.qualityChooseViewAnimation = null;
        }
        r0.allowShare = false;
        r0.slideshowMessageId = 0;
        r0.nameOverride = null;
        r0.dateOverride = 0;
        r0.menuItem.hideSubItem(2);
        r0.menuItem.hideSubItem(4);
        r0.menuItem.hideSubItem(10);
        r0.menuItem.hideSubItem(11);
        r0.actionBar.setTranslationY(0.0f);
        r0.checkImageView.setAlpha(1.0f);
        r0.checkImageView.setVisibility(8);
        r0.actionBar.setTitleRightMargin(0);
        r0.photosCounterView.setAlpha(1.0f);
        r0.photosCounterView.setVisibility(8);
        r0.pickerView.setVisibility(8);
        r0.pickerViewSendButton.setVisibility(8);
        r0.pickerViewSendButton.setTranslationY(0.0f);
        r0.pickerView.setAlpha(1.0f);
        r0.pickerViewSendButton.setAlpha(1.0f);
        r0.pickerView.setTranslationY(0.0f);
        r0.paintItem.setVisibility(8);
        r0.cropItem.setVisibility(8);
        r0.tuneItem.setVisibility(8);
        r0.timeItem.setVisibility(8);
        r0.videoTimelineView.setVisibility(8);
        r0.compressItem.setVisibility(8);
        r0.captionEditText.setVisibility(8);
        r0.mentionListView.setVisibility(8);
        r0.muteItem.setVisibility(8);
        r0.actionBar.setSubtitle(null);
        r0.masksItem.setVisibility(8);
        r0.muteVideo = false;
        r0.muteItem.setImageResource(R.drawable.volume_on);
        r0.editorDoneLayout.setVisibility(8);
        r0.captionTextView.setTag(null);
        r0.captionTextView.setVisibility(4);
        if (r0.photoCropView != null) {
            r0.photoCropView.setVisibility(8);
        }
        if (r0.photoFilterView != null) {
            r0.photoFilterView.setVisibility(8);
        }
        for (a = 0; a < 3; a++) {
            if (r0.photoProgressViews[a] != null) {
                r0.photoProgressViews[a].setBackgroundState(-1, false);
            }
        }
        if (messageObject2 != null && arrayList == null) {
            if ((messageObject2.messageOwner.media instanceof TL_messageMediaWebPage) && messageObject2.messageOwner.media.webpage != null) {
                WebPage webPage = messageObject2.messageOwner.media.webpage;
                String siteName = webPage.site_name;
                if (siteName != null) {
                    siteName = siteName.toLowerCase();
                    if (siteName.equals("instagram") || siteName.equals("twitter") || "telegram_album".equals(webPage.type)) {
                        if (!TextUtils.isEmpty(webPage.author)) {
                            r0.nameOverride = webPage.author;
                        }
                        if (webPage.cached_page instanceof TL_pageFull) {
                            for (int a2 = 0; a2 < webPage.cached_page.blocks.size(); a2++) {
                                PageBlock block = (PageBlock) webPage.cached_page.blocks.get(a2);
                                if (block instanceof TL_pageBlockAuthorDate) {
                                    r0.dateOverride = ((TL_pageBlockAuthorDate) block).published_date;
                                    break;
                                }
                            }
                        }
                        ArrayList<MessageObject> arrayList3 = messageObject2.getWebPagePhotos(null, null);
                        if (!arrayList3.isEmpty()) {
                            r0.slideshowMessageId = messageObject.getId();
                            r0.needSearchImageInArr = false;
                            r0.imagesArr.addAll(arrayList3);
                            r0.totalImagesCount = r0.imagesArr.size();
                            setImageIndex(r0.imagesArr.indexOf(messageObject2), true);
                        }
                    }
                }
            }
            if (r0.slideshowMessageId == 0) {
                r0.imagesArr.add(messageObject2);
                if (r0.currentAnimation == null) {
                    if (messageObject2.eventId == 0) {
                        if (!((messageObject2.messageOwner.media instanceof TL_messageMediaInvoice) || (messageObject2.messageOwner.media instanceof TL_messageMediaWebPage) || (messageObject2.messageOwner.action != null && !(messageObject2.messageOwner.action instanceof TL_messageActionEmpty)))) {
                            r0.needSearchImageInArr = true;
                            r0.imagesByIds[0].put(messageObject.getId(), messageObject2);
                            r0.menuItem.showSubItem(2);
                            r0.sendItem.setVisibility(0);
                        }
                        setImageIndex(0, true);
                    }
                }
                r0.needSearchImageInArr = false;
                setImageIndex(0, true);
            }
        } else if (fileLocation2 != null) {
            r0.avatarsDialogId = placeProviderObject.dialogId;
            r0.imagesArrLocations.add(fileLocation2);
            r0.imagesArrLocationsSizes.add(Integer.valueOf(placeProviderObject.size));
            r0.avatarsArr.add(new TL_photoEmpty());
            ImageView imageView = r0.shareButton;
            if (r0.videoPlayerControlFrameLayout.getVisibility() != 0) {
                i2 = 0;
            }
            imageView.setVisibility(i2);
            r0.allowShare = true;
            r0.menuItem.hideSubItem(2);
            if (r0.shareButton.getVisibility() == 0) {
                r0.menuItem.hideSubItem(10);
            } else {
                r0.menuItem.showSubItem(10);
            }
            setImageIndex(0, true);
            r0.currentUserAvatarLocation = fileLocation2;
        } else {
            int a3;
            if (arrayList != null) {
                r0.opennedFromMedia = true;
                r0.menuItem.showSubItem(4);
                r0.sendItem.setVisibility(0);
                r0.imagesArr.addAll(arrayList);
                for (a3 = 0; a3 < r0.imagesArr.size(); a3++) {
                    MessageObject message = (MessageObject) r0.imagesArr.get(a3);
                    r0.imagesByIds[message.getDialogId() == r0.currentDialogId ? 0 : 1].put(message.getId(), message);
                }
                setImageIndex(i, true);
            } else if (arrayList2 != null) {
                boolean z3;
                boolean z4;
                if (r0.sendPhotoType == 0 || (r0.sendPhotoType == 2 && photos.size() > 1)) {
                    r0.checkImageView.setVisibility(0);
                    r0.photosCounterView.setVisibility(0);
                    r0.actionBar.setTitleRightMargin(AndroidUtilities.dp(100.0f));
                }
                if (r0.sendPhotoType == 2) {
                    r0.cameraItem.setVisibility(0);
                    r0.cameraItem.setTag(Integer.valueOf(1));
                }
                r0.menuItem.setVisibility(8);
                r0.imagesArrLocals.addAll(arrayList2);
                Object obj = r0.imagesArrLocals.get(i);
                if (obj instanceof PhotoEntry) {
                    if (((PhotoEntry) obj).isVideo) {
                        r0.cropItem.setVisibility(8);
                        r0.bottomLayout.setVisibility(0);
                        r0.bottomLayout.setTag(Integer.valueOf(1));
                        r0.bottomLayout.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                    } else {
                        r0.cropItem.setVisibility(0);
                    }
                    z3 = true;
                } else if (obj instanceof BotInlineResult) {
                    r0.cropItem.setVisibility(8);
                    z3 = false;
                } else {
                    ImageView imageView2 = r0.cropItem;
                    a3 = ((obj instanceof SearchImage) && ((SearchImage) obj).type == 0) ? 0 : 8;
                    imageView2.setVisibility(a3);
                    z3 = r0.cropItem.getVisibility() == 0;
                    if (r0.parentChatActivity != null && (r0.parentChatActivity.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(r0.parentChatActivity.currentEncryptedChat.layer) >= 46)) {
                        r0.mentionsAdapter.setChatInfo(r0.parentChatActivity.info);
                        r0.mentionsAdapter.setNeedUsernames(r0.parentChatActivity.currentChat == null);
                        r0.mentionsAdapter.setNeedBotContext(false);
                        z4 = z3 && (r0.placeProvider == null || (r0.placeProvider != null && r0.placeProvider.allowCaption()));
                        r0.needCaptionLayout = z4;
                        r0.captionEditText.setVisibility(r0.needCaptionLayout ? 0 : 8);
                        if (r0.needCaptionLayout) {
                            r0.captionEditText.onCreate();
                        }
                    }
                    r0.pickerView.setVisibility(0);
                    r0.pickerViewSendButton.setVisibility(0);
                    r0.pickerViewSendButton.setTranslationY(0.0f);
                    r0.pickerViewSendButton.setAlpha(1.0f);
                    r0.bottomLayout.setVisibility(8);
                    user = null;
                    r0.bottomLayout.setTag(null);
                    r0.containerView.setTag(null);
                    setImageIndex(i, true);
                    r0.paintItem.setVisibility(r0.cropItem.getVisibility());
                    r0.tuneItem.setVisibility(r0.cropItem.getVisibility());
                    updateSelectedCount();
                    if (r0.currentAnimation == null && !r0.isEvent) {
                        if (r0.currentDialogId == 0 && r0.totalImagesCount == 0) {
                            DataQuery.getInstance(r0.currentAccount).getMediaCount(r0.currentDialogId, 0, r0.classGuid, true);
                            if (r0.mergeDialogId != 0) {
                                DataQuery.getInstance(r0.currentAccount).getMediaCount(r0.mergeDialogId, 0, r0.classGuid, true);
                            }
                        } else if (r0.avatarsDialogId != 0) {
                            MessagesController.getInstance(r0.currentAccount).loadDialogPhotos(r0.avatarsDialogId, 80, 0, true, r0.classGuid);
                        }
                    }
                    if ((r0.currentMessageObject == null && r0.currentMessageObject.isVideo()) || (r0.currentBotInlineResult != null && (r0.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(r0.currentBotInlineResult.document)))) {
                        onActionClick(false);
                        return;
                    } else if (r0.imagesArrLocals.isEmpty()) {
                        entry = r0.imagesArrLocals.get(i);
                        user2 = r0.parentChatActivity == null ? r0.parentChatActivity.getCurrentUser() : user;
                        z2 = (r0.parentChatActivity != null || r0.parentChatActivity.isSecretChat() || user2 == null || user2.bot) ? false : true;
                        if (entry instanceof PhotoEntry) {
                            photoEntry = entry;
                            if (photoEntry.isVideo) {
                                preparePlayer(Uri.fromFile(new File(photoEntry.path)), false, false);
                            }
                        } else if (z2 && (entry instanceof SearchImage)) {
                            if (((SearchImage) entry).type != 0) {
                                z = false;
                            }
                            z2 = z;
                        }
                        if (!z2) {
                            r0.timeItem.setVisibility(0);
                        }
                    }
                }
                r0.mentionsAdapter.setChatInfo(r0.parentChatActivity.info);
                if (r0.parentChatActivity.currentChat == null) {
                }
                r0.mentionsAdapter.setNeedUsernames(r0.parentChatActivity.currentChat == null);
                r0.mentionsAdapter.setNeedBotContext(false);
                if (!z3) {
                }
                r0.needCaptionLayout = z4;
                if (r0.needCaptionLayout) {
                }
                r0.captionEditText.setVisibility(r0.needCaptionLayout ? 0 : 8);
                if (r0.needCaptionLayout) {
                    r0.captionEditText.onCreate();
                }
                r0.pickerView.setVisibility(0);
                r0.pickerViewSendButton.setVisibility(0);
                r0.pickerViewSendButton.setTranslationY(0.0f);
                r0.pickerViewSendButton.setAlpha(1.0f);
                r0.bottomLayout.setVisibility(8);
                user = null;
                r0.bottomLayout.setTag(null);
                r0.containerView.setTag(null);
                setImageIndex(i, true);
                r0.paintItem.setVisibility(r0.cropItem.getVisibility());
                r0.tuneItem.setVisibility(r0.cropItem.getVisibility());
                updateSelectedCount();
                if (r0.currentDialogId == 0) {
                }
                if (r0.avatarsDialogId != 0) {
                    MessagesController.getInstance(r0.currentAccount).loadDialogPhotos(r0.avatarsDialogId, 80, 0, true, r0.classGuid);
                }
                if (r0.currentMessageObject == null) {
                }
                if (r0.imagesArrLocals.isEmpty()) {
                    entry = r0.imagesArrLocals.get(i);
                    if (r0.parentChatActivity == null) {
                    }
                    if (r0.parentChatActivity != null) {
                    }
                    if (entry instanceof PhotoEntry) {
                        photoEntry = entry;
                        if (photoEntry.isVideo) {
                            preparePlayer(Uri.fromFile(new File(photoEntry.path)), false, false);
                        }
                    } else {
                        if (((SearchImage) entry).type != 0) {
                            z = false;
                        }
                        z2 = z;
                    }
                    if (!z2) {
                        r0.timeItem.setVisibility(0);
                    }
                }
            }
            user = null;
            if (r0.currentDialogId == 0) {
            }
            if (r0.avatarsDialogId != 0) {
                MessagesController.getInstance(r0.currentAccount).loadDialogPhotos(r0.avatarsDialogId, 80, 0, true, r0.classGuid);
            }
            if (r0.currentMessageObject == null) {
            }
            if (r0.imagesArrLocals.isEmpty()) {
                entry = r0.imagesArrLocals.get(i);
                if (r0.parentChatActivity == null) {
                }
                if (r0.parentChatActivity != null) {
                }
                if (entry instanceof PhotoEntry) {
                    photoEntry = entry;
                    if (photoEntry.isVideo) {
                        preparePlayer(Uri.fromFile(new File(photoEntry.path)), false, false);
                    }
                } else {
                    if (((SearchImage) entry).type != 0) {
                        z = false;
                    }
                    z2 = z;
                }
                if (!z2) {
                    r0.timeItem.setVisibility(0);
                }
            }
        }
        user = null;
        if (r0.currentDialogId == 0) {
        }
        if (r0.avatarsDialogId != 0) {
            MessagesController.getInstance(r0.currentAccount).loadDialogPhotos(r0.avatarsDialogId, 80, 0, true, r0.classGuid);
        }
        if (r0.currentMessageObject == null) {
        }
        if (r0.imagesArrLocals.isEmpty()) {
            entry = r0.imagesArrLocals.get(i);
            if (r0.parentChatActivity == null) {
            }
            if (r0.parentChatActivity != null) {
            }
            if (entry instanceof PhotoEntry) {
                if (((SearchImage) entry).type != 0) {
                    z = false;
                }
                z2 = z;
            } else {
                photoEntry = entry;
                if (photoEntry.isVideo) {
                    preparePlayer(Uri.fromFile(new File(photoEntry.path)), false, false);
                }
            }
            if (!z2) {
                r0.timeItem.setVisibility(0);
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

    private void setIsAboutToSwitchToIndex(int index, boolean init) {
        PhotoViewer photoViewer = this;
        int i = index;
        if (init || photoViewer.switchingToIndex != i) {
            photoViewer.switchingToIndex = i;
            boolean isVideo = false;
            CharSequence caption = null;
            String newFileName = getFileName(index);
            MessageObject newMessageObject = null;
            boolean sameImage;
            if (photoViewer.imagesArr.isEmpty()) {
                sameImage = false;
                boolean z;
                if (!photoViewer.imagesArrLocations.isEmpty()) {
                    if (i >= 0) {
                        if (i < photoViewer.imagesArrLocations.size()) {
                            photoViewer.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                            photoViewer.dateTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                            if (photoViewer.avatarsDialogId != UserConfig.getInstance(photoViewer.currentAccount).getClientUserId() || photoViewer.avatarsArr.isEmpty()) {
                                photoViewer.menuItem.hideSubItem(6);
                            } else {
                                photoViewer.menuItem.showSubItem(6);
                            }
                            if (photoViewer.isEvent) {
                                photoViewer.actionBar.setTitle(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                                z = true;
                            } else {
                                ActionBar actionBar = photoViewer.actionBar;
                                r8 = new Object[2];
                                z = true;
                                r8[0] = Integer.valueOf(photoViewer.switchingToIndex + 1);
                                r8[1] = Integer.valueOf(photoViewer.imagesArrLocations.size());
                                actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, r8));
                            }
                            photoViewer.menuItem.showSubItem(z);
                            photoViewer.allowShare = z;
                            photoViewer.shareButton.setVisibility(photoViewer.videoPlayerControlFrameLayout.getVisibility() != 0 ? 0 : 8);
                            if (photoViewer.shareButton.getVisibility() == 0) {
                                photoViewer.menuItem.hideSubItem(10);
                            } else {
                                photoViewer.menuItem.showSubItem(10);
                            }
                            photoViewer.groupedPhotosListView.fillList();
                        }
                    }
                    return;
                } else if (!photoViewer.imagesArrLocals.isEmpty()) {
                    if (i >= 0) {
                        if (i < photoViewer.imagesArrLocals.size()) {
                            BotInlineResult object = photoViewer.imagesArrLocals.get(i);
                            int ttl = 0;
                            boolean isFiltered = false;
                            boolean isPainted = false;
                            z = false;
                            if (object instanceof BotInlineResult) {
                                BotInlineResult botInlineResult = object;
                                photoViewer.currentBotInlineResult = botInlineResult;
                                if (botInlineResult.document != null) {
                                    isVideo = MessageObject.isVideoDocument(botInlineResult.document);
                                } else if (botInlineResult.content instanceof TL_webDocument) {
                                    isVideo = botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO);
                                }
                                photoViewer.pickerView.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
                            } else {
                                PhotoEntry photoEntry;
                                boolean z2;
                                String pathObject = null;
                                boolean isAnimation = false;
                                if (object instanceof PhotoEntry) {
                                    photoEntry = (PhotoEntry) object;
                                    pathObject = photoEntry.path;
                                    isVideo = photoEntry.isVideo;
                                } else if (object instanceof SearchImage) {
                                    SearchImage searchImage = (SearchImage) object;
                                    if (searchImage.document != null) {
                                        z2 = true;
                                        pathObject = FileLoader.getPathToAttach(searchImage.document, true).getAbsolutePath();
                                    } else {
                                        z2 = true;
                                        pathObject = searchImage.imageUrl;
                                    }
                                    if (searchImage.type == z2) {
                                        isAnimation = true;
                                    }
                                }
                                if (isVideo) {
                                    photoViewer.muteItem.setVisibility(0);
                                    photoViewer.compressItem.setVisibility(0);
                                    photoViewer.isCurrentVideo = true;
                                    z2 = false;
                                    if (object instanceof PhotoEntry) {
                                        photoEntry = (PhotoEntry) object;
                                        boolean z3 = photoEntry.editedInfo != null && photoEntry.editedInfo.muted;
                                        z2 = z3;
                                    }
                                    processOpenVideo(pathObject, z2);
                                    photoViewer.videoTimelineView.setVisibility(0);
                                    photoViewer.paintItem.setVisibility(8);
                                    photoViewer.cropItem.setVisibility(8);
                                    photoViewer.tuneItem.setVisibility(8);
                                } else {
                                    photoViewer.videoTimelineView.setVisibility(8);
                                    photoViewer.muteItem.setVisibility(8);
                                    photoViewer.isCurrentVideo = false;
                                    photoViewer.compressItem.setVisibility(8);
                                    if (isAnimation) {
                                        photoViewer.pickerView.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
                                        photoViewer.paintItem.setVisibility(8);
                                        photoViewer.cropItem.setVisibility(8);
                                        photoViewer.tuneItem.setVisibility(8);
                                    } else {
                                        int i2;
                                        if (photoViewer.sendPhotoType != 1) {
                                            i2 = 0;
                                            photoViewer.pickerView.setPadding(0, 0, 0, 0);
                                        } else {
                                            i2 = 0;
                                        }
                                        photoViewer.paintItem.setVisibility(i2);
                                        photoViewer.cropItem.setVisibility(i2);
                                        photoViewer.tuneItem.setVisibility(i2);
                                    }
                                    photoViewer.actionBar.setSubtitle(null);
                                }
                                if (object instanceof PhotoEntry) {
                                    PhotoEntry photoEntry2 = (PhotoEntry) object;
                                    boolean z4 = photoEntry2.bucketId == 0 && photoEntry2.dateTaken == 0 && photoViewer.imagesArrLocals.size() == 1;
                                    photoViewer.fromCamera = z4;
                                    caption = photoEntry2.caption;
                                    ttl = photoEntry2.ttl;
                                    isFiltered = photoEntry2.isFiltered;
                                    isPainted = photoEntry2.isPainted;
                                    z = photoEntry2.isCropped;
                                } else if (object instanceof SearchImage) {
                                    SearchImage searchImage2 = (SearchImage) object;
                                    caption = searchImage2.caption;
                                    ttl = searchImage2.ttl;
                                    isFiltered = searchImage2.isFiltered;
                                    isPainted = searchImage2.isPainted;
                                    z = searchImage2.isCropped;
                                }
                            }
                            if (photoViewer.bottomLayout.getVisibility() != 8) {
                                photoViewer.bottomLayout.setVisibility(8);
                            }
                            photoViewer.bottomLayout.setTag(null);
                            if (!photoViewer.fromCamera) {
                                photoViewer.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(photoViewer.switchingToIndex + 1), Integer.valueOf(photoViewer.imagesArrLocals.size())));
                            } else if (isVideo) {
                                photoViewer.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                            } else {
                                photoViewer.actionBar.setTitle(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                            }
                            if (photoViewer.parentChatActivity != null) {
                                Chat chat = photoViewer.parentChatActivity.getCurrentChat();
                                if (chat != null) {
                                    photoViewer.actionBar.setTitle(chat.title);
                                } else {
                                    User user = photoViewer.parentChatActivity.getCurrentUser();
                                    if (user != null) {
                                        photoViewer.actionBar.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                                    }
                                }
                            }
                            if (photoViewer.sendPhotoType == 0 || (photoViewer.sendPhotoType == 2 && photoViewer.imagesArrLocals.size() > 1)) {
                                photoViewer.checkImageView.setChecked(photoViewer.placeProvider.isPhotoChecked(photoViewer.switchingToIndex), false);
                            }
                            updateCaptionTextForCurrentPhoto(object);
                            ColorFilter filter = new PorterDuffColorFilter(-12734994, Mode.MULTIPLY);
                            photoViewer.timeItem.setColorFilter(ttl != 0 ? filter : null);
                            photoViewer.paintItem.setColorFilter(isPainted ? filter : null);
                            photoViewer.cropItem.setColorFilter(z ? filter : null);
                            photoViewer.tuneItem.setColorFilter(isFiltered ? filter : null);
                        }
                    }
                    return;
                }
            }
            if (photoViewer.switchingToIndex < 0) {
                sameImage = false;
            } else if (photoViewer.switchingToIndex >= photoViewer.imagesArr.size()) {
                sameImage = false;
            } else {
                newMessageObject = (MessageObject) photoViewer.imagesArr.get(photoViewer.switchingToIndex);
                isVideo = newMessageObject.isVideo();
                boolean isInvoice = newMessageObject.isInvoice();
                boolean isVideo2;
                if (isInvoice) {
                    photoViewer.masksItem.setVisibility(8);
                    photoViewer.menuItem.hideSubItem(6);
                    photoViewer.menuItem.hideSubItem(11);
                    caption = newMessageObject.messageOwner.media.description;
                    photoViewer.allowShare = false;
                    photoViewer.bottomLayout.setTranslationY((float) AndroidUtilities.dp(48.0f));
                    photoViewer.captionTextView.setTranslationY((float) AndroidUtilities.dp(48.0f));
                    isVideo2 = isVideo;
                    sameImage = false;
                } else {
                    ActionBarMenuItem actionBarMenuItem = photoViewer.masksItem;
                    int i3 = (!newMessageObject.hasPhotoStickers() || ((int) newMessageObject.getDialogId()) == 0) ? 8 : 0;
                    actionBarMenuItem.setVisibility(i3);
                    if (newMessageObject.canDeleteMessage(null) && photoViewer.slideshowMessageId == 0) {
                        photoViewer.menuItem.showSubItem(6);
                    } else {
                        photoViewer.menuItem.hideSubItem(6);
                    }
                    if (isVideo) {
                        boolean z5;
                        photoViewer.menuItem.showSubItem(11);
                        if (photoViewer.pipItem.getVisibility() != 0) {
                            z5 = false;
                            photoViewer.pipItem.setVisibility(0);
                        } else {
                            z5 = false;
                        }
                        if (!photoViewer.pipAvailable) {
                            photoViewer.pipItem.setEnabled(z5);
                            photoViewer.pipItem.setAlpha(0.5f);
                        }
                    } else {
                        photoViewer.menuItem.hideSubItem(11);
                        if (photoViewer.pipItem.getVisibility() != 8) {
                            photoViewer.pipItem.setVisibility(8);
                        }
                    }
                    if (photoViewer.nameOverride != null) {
                        photoViewer.nameTextView.setText(photoViewer.nameOverride);
                    } else if (newMessageObject.isFromUser()) {
                        User user2 = MessagesController.getInstance(photoViewer.currentAccount).getUser(Integer.valueOf(newMessageObject.messageOwner.from_id));
                        if (user2 != null) {
                            photoViewer.nameTextView.setText(UserObject.getUserName(user2));
                        } else {
                            photoViewer.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        }
                    } else {
                        Chat chat2 = MessagesController.getInstance(photoViewer.currentAccount).getChat(Integer.valueOf(newMessageObject.messageOwner.to_id.channel_id));
                        if (chat2 != null) {
                            photoViewer.nameTextView.setText(chat2.title);
                        } else {
                            photoViewer.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        }
                    }
                    long date;
                    if (photoViewer.dateOverride != 0) {
                        date = ((long) photoViewer.dateOverride) * 1000;
                    } else {
                        date = ((long) newMessageObject.messageOwner.date) * 1000;
                    }
                    String dateString = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(date)), LocaleController.getInstance().formatterDay.format(new Date(date)));
                    if (newFileName == null || !isVideo) {
                        isVideo2 = isVideo;
                        sameImage = false;
                        photoViewer.dateTextView.setText(dateString);
                    } else {
                        TextView textView = photoViewer.dateTextView;
                        Object[] objArr = new Object[2];
                        objArr[0] = dateString;
                        isVideo2 = isVideo;
                        sameImage = false;
                        objArr[true] = AndroidUtilities.formatFileSize((long) newMessageObject.getDocument().size);
                        textView.setText(String.format("%s (%s)", objArr));
                    }
                    caption = newMessageObject.caption;
                }
                if (photoViewer.currentAnimation != null) {
                    photoViewer.menuItem.hideSubItem(true);
                    photoViewer.menuItem.hideSubItem(true);
                    if (!newMessageObject.canDeleteMessage(null)) {
                        photoViewer.menuItem.setVisibility(true);
                    }
                    photoViewer.allowShare = true;
                    photoViewer.shareButton.setVisibility(false);
                    photoViewer.actionBar.setTitle(LocaleController.getString("AttachGif", R.string.AttachGif));
                } else {
                    if (photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge == 0 || photoViewer.needSearchImageInArr) {
                        if (photoViewer.slideshowMessageId == 0 && (newMessageObject.messageOwner.media instanceof TL_messageMediaWebPage)) {
                            if (newMessageObject.isVideo()) {
                                photoViewer.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                            } else {
                                photoViewer.actionBar.setTitle(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                            }
                        } else if (isInvoice) {
                            photoViewer.actionBar.setTitle(newMessageObject.messageOwner.media.title);
                        }
                    } else if (photoViewer.opennedFromMedia) {
                        int i4;
                        if (photoViewer.imagesArr.size() >= photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge || photoViewer.loadingMoreImages || photoViewer.switchingToIndex <= photoViewer.imagesArr.size() - 5) {
                            i4 = 1;
                        } else {
                            loadFromMaxId = photoViewer.imagesArr.isEmpty() ? 0 : ((MessageObject) photoViewer.imagesArr.get(photoViewer.imagesArr.size() - true)).getId();
                            sameImage = false;
                            if (photoViewer.endReached[0] && photoViewer.mergeDialogId != 0) {
                                sameImage = true;
                                if (!(photoViewer.imagesArr.isEmpty() || ((MessageObject) photoViewer.imagesArr.get(photoViewer.imagesArr.size() - 1)).getDialogId() == photoViewer.mergeDialogId)) {
                                    loadFromMaxId = 0;
                                }
                            }
                            DataQuery.getInstance(photoViewer.currentAccount).loadMedia(!sameImage ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, loadFromMaxId, 0, true, photoViewer.classGuid);
                            i4 = 1;
                            photoViewer.loadingMoreImages = true;
                        }
                        photoViewer.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(photoViewer.switchingToIndex + i4), Integer.valueOf(photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge)));
                    } else {
                        if (photoViewer.imagesArr.size() < photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge && !photoViewer.loadingMoreImages && photoViewer.switchingToIndex < 5) {
                            loadFromMaxId = photoViewer.imagesArr.isEmpty() ? 0 : ((MessageObject) photoViewer.imagesArr.get(false)).getId();
                            sameImage = false;
                            if (photoViewer.endReached[0] && photoViewer.mergeDialogId != 0) {
                                sameImage = true;
                                if (!(photoViewer.imagesArr.isEmpty() || ((MessageObject) photoViewer.imagesArr.get(0)).getDialogId() == photoViewer.mergeDialogId)) {
                                    loadFromMaxId = 0;
                                }
                            }
                            DataQuery.getInstance(photoViewer.currentAccount).loadMedia(!sameImage ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, loadFromMaxId, 0, true, photoViewer.classGuid);
                            photoViewer.loadingMoreImages = true;
                        }
                        photoViewer.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf((((photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge) - photoViewer.imagesArr.size()) + photoViewer.switchingToIndex) + 1), Integer.valueOf(photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge)));
                    }
                    if (((int) photoViewer.currentDialogId) == 0) {
                        photoViewer.sendItem.setVisibility(true);
                    }
                    if (newMessageObject.messageOwner.ttl == 0 || newMessageObject.messageOwner.ttl >= true) {
                        photoViewer.allowShare = true;
                        photoViewer.menuItem.showSubItem(1);
                        photoViewer.shareButton.setVisibility(photoViewer.videoPlayerControlFrameLayout.getVisibility() ? false : true);
                        if (photoViewer.shareButton.getVisibility() == 0) {
                            photoViewer.menuItem.hideSubItem(true);
                        } else {
                            photoViewer.menuItem.showSubItem(true);
                        }
                    } else {
                        photoViewer.allowShare = false;
                        photoViewer.menuItem.hideSubItem(true);
                        photoViewer.shareButton.setVisibility(true);
                        photoViewer.menuItem.hideSubItem(true);
                    }
                }
                photoViewer.groupedPhotosListView.fillList();
            }
            return;
            setCurrentCaption(newMessageObject, caption, init ^ 1);
        }
    }

    private void setImageIndex(int index, boolean init) {
        int i = index;
        boolean z = init;
        if (this.currentIndex != i) {
            if (r0.placeProvider != null) {
                boolean sameImage;
                boolean sameImage2;
                boolean z2;
                int a;
                ImageReceiver temp;
                PhotoProgressView tempProgress;
                if (!(z || r0.currentThumb == null)) {
                    r0.currentThumb.release();
                    r0.currentThumb = null;
                }
                r0.currentFileNames[0] = getFileName(index);
                r0.currentFileNames[1] = getFileName(i + 1);
                r0.currentFileNames[2] = getFileName(i - 1);
                r0.placeProvider.willSwitchFromPhoto(r0.currentMessageObject, r0.currentFileLocation, r0.currentIndex);
                int prevIndex = r0.currentIndex;
                r0.currentIndex = i;
                setIsAboutToSwitchToIndex(r0.currentIndex, z);
                boolean isVideo = false;
                Uri videoPath = null;
                if (!r0.imagesArr.isEmpty()) {
                    if (r0.currentIndex >= 0) {
                        if (r0.currentIndex < r0.imagesArr.size()) {
                            MessageObject newMessageObject = (MessageObject) r0.imagesArr.get(r0.currentIndex);
                            boolean z3 = r0.currentMessageObject != null && r0.currentMessageObject.getId() == newMessageObject.getId();
                            sameImage = z3;
                            r0.currentMessageObject = newMessageObject;
                            isVideo = newMessageObject.isVideo();
                        }
                    }
                    closePhoto(false, false);
                    return;
                } else if (r0.imagesArrLocations.isEmpty()) {
                    sameImage2 = false;
                    if (!r0.imagesArrLocals.isEmpty()) {
                        if (i >= 0) {
                            if (i < r0.imagesArrLocals.size()) {
                                BotInlineResult object = r0.imagesArrLocals.get(i);
                                if (object instanceof BotInlineResult) {
                                    BotInlineResult botInlineResult = object;
                                    r0.currentBotInlineResult = botInlineResult;
                                    if (botInlineResult.document != null) {
                                        r0.currentPathObject = FileLoader.getPathToAttach(botInlineResult.document).getAbsolutePath();
                                        isVideo = MessageObject.isVideoDocument(botInlineResult.document);
                                    } else if (botInlineResult.photo != null) {
                                        r0.currentPathObject = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize())).getAbsolutePath();
                                    } else if (botInlineResult.content instanceof TL_webDocument) {
                                        r0.currentPathObject = botInlineResult.content.url;
                                        isVideo = botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO);
                                    }
                                } else if (object instanceof PhotoEntry) {
                                    PhotoEntry photoEntry = (PhotoEntry) object;
                                    r0.currentPathObject = photoEntry.path;
                                    isVideo = photoEntry.isVideo;
                                    videoPath = Uri.fromFile(new File(photoEntry.path));
                                } else if (object instanceof SearchImage) {
                                    SearchImage searchImage = (SearchImage) object;
                                    if (searchImage.document != null) {
                                        r0.currentPathObject = FileLoader.getPathToAttach(searchImage.document, true).getAbsolutePath();
                                    } else {
                                        r0.currentPathObject = searchImage.imageUrl;
                                    }
                                }
                            }
                        }
                        closePhoto(false, false);
                        return;
                    }
                    if (r0.currentPlaceObject != null) {
                        if (r0.animationInProgress != 0) {
                            r0.currentPlaceObject.imageReceiver.setVisible(true, true);
                        } else {
                            r0.showAfterAnimation = r0.currentPlaceObject;
                        }
                    }
                    r0.currentPlaceObject = r0.placeProvider.getPlaceForPhoto(r0.currentMessageObject, r0.currentFileLocation, r0.currentIndex);
                    if (r0.currentPlaceObject != null) {
                        if (r0.animationInProgress != 0) {
                            r0.currentPlaceObject.imageReceiver.setVisible(false, true);
                        } else {
                            r0.hideAfterAnimation = r0.currentPlaceObject;
                        }
                    }
                    if (!sameImage2) {
                        r0.draggingDown = false;
                        r0.translationX = 0.0f;
                        r0.translationY = 0.0f;
                        r0.scale = 1.0f;
                        r0.animateToX = 0.0f;
                        r0.animateToY = 0.0f;
                        r0.animateToScale = 1.0f;
                        r0.animationStartTime = 0;
                        r0.imageMoveAnimation = null;
                        r0.changeModeAnimation = null;
                        if (r0.aspectRatioFrameLayout != null) {
                            r0.aspectRatioFrameLayout.setVisibility(4);
                        }
                        r0.pinchStartDistance = 0.0f;
                        r0.pinchStartScale = 1.0f;
                        r0.pinchCenterX = 0.0f;
                        r0.pinchCenterY = 0.0f;
                        r0.pinchStartX = 0.0f;
                        r0.pinchStartY = 0.0f;
                        r0.moveStartX = 0.0f;
                        r0.moveStartY = 0.0f;
                        r0.zooming = false;
                        r0.moving = false;
                        r0.doubleTap = false;
                        r0.invalidCoords = false;
                        r0.canDragDown = true;
                        r0.changingPage = false;
                        r0.switchImageAfterAnimation = 0;
                        if (r0.imagesArrLocals.isEmpty()) {
                            if (r0.currentFileNames[0] != null || isVideo || r0.photoProgressViews[0].backgroundState == 0) {
                                z2 = false;
                                r0.canZoom = z2;
                                updateMinMax(r0.scale);
                                releasePlayer();
                            }
                        }
                        z2 = true;
                        r0.canZoom = z2;
                        updateMinMax(r0.scale);
                        releasePlayer();
                    }
                    if (isVideo && videoPath != null) {
                        r0.isStreaming = false;
                        preparePlayer(videoPath, false, false);
                    }
                    if (prevIndex != -1) {
                        setImages();
                        for (a = 0; a < 3; a++) {
                            checkProgress(a, false);
                        }
                    } else {
                        checkProgress(0, false);
                        if (prevIndex > r0.currentIndex) {
                            temp = r0.rightImage;
                            r0.rightImage = r0.centerImage;
                            r0.centerImage = r0.leftImage;
                            r0.leftImage = temp;
                            tempProgress = r0.photoProgressViews[0];
                            r0.photoProgressViews[0] = r0.photoProgressViews[2];
                            r0.photoProgressViews[2] = tempProgress;
                            setIndexToImage(r0.leftImage, r0.currentIndex - 1);
                            checkProgress(1, false);
                            checkProgress(2, false);
                        } else if (prevIndex < r0.currentIndex) {
                            temp = r0.leftImage;
                            r0.leftImage = r0.centerImage;
                            r0.centerImage = r0.rightImage;
                            r0.rightImage = temp;
                            tempProgress = r0.photoProgressViews[0];
                            r0.photoProgressViews[0] = r0.photoProgressViews[1];
                            r0.photoProgressViews[1] = tempProgress;
                            setIndexToImage(r0.rightImage, r0.currentIndex + 1);
                            checkProgress(1, false);
                            checkProgress(2, false);
                        }
                    }
                } else {
                    if (i < 0) {
                        sameImage2 = false;
                    } else if (i >= r0.imagesArrLocations.size()) {
                        sameImage2 = false;
                    } else {
                        FileLocation old = r0.currentFileLocation;
                        FileLocation newLocation = (FileLocation) r0.imagesArrLocations.get(i);
                        if (old == null || newLocation == null || old.local_id != newLocation.local_id) {
                            sameImage2 = false;
                        } else {
                            sameImage2 = false;
                            if (old.volume_id == newLocation.volume_id) {
                                sameImage = true;
                                r0.currentFileLocation = (FileLocation) r0.imagesArrLocations.get(i);
                            }
                        }
                        sameImage = sameImage2;
                        r0.currentFileLocation = (FileLocation) r0.imagesArrLocations.get(i);
                    }
                    closePhoto(false, false);
                    return;
                }
                sameImage2 = sameImage;
                if (r0.currentPlaceObject != null) {
                    if (r0.animationInProgress != 0) {
                        r0.showAfterAnimation = r0.currentPlaceObject;
                    } else {
                        r0.currentPlaceObject.imageReceiver.setVisible(true, true);
                    }
                }
                r0.currentPlaceObject = r0.placeProvider.getPlaceForPhoto(r0.currentMessageObject, r0.currentFileLocation, r0.currentIndex);
                if (r0.currentPlaceObject != null) {
                    if (r0.animationInProgress != 0) {
                        r0.hideAfterAnimation = r0.currentPlaceObject;
                    } else {
                        r0.currentPlaceObject.imageReceiver.setVisible(false, true);
                    }
                }
                if (sameImage2) {
                    r0.draggingDown = false;
                    r0.translationX = 0.0f;
                    r0.translationY = 0.0f;
                    r0.scale = 1.0f;
                    r0.animateToX = 0.0f;
                    r0.animateToY = 0.0f;
                    r0.animateToScale = 1.0f;
                    r0.animationStartTime = 0;
                    r0.imageMoveAnimation = null;
                    r0.changeModeAnimation = null;
                    if (r0.aspectRatioFrameLayout != null) {
                        r0.aspectRatioFrameLayout.setVisibility(4);
                    }
                    r0.pinchStartDistance = 0.0f;
                    r0.pinchStartScale = 1.0f;
                    r0.pinchCenterX = 0.0f;
                    r0.pinchCenterY = 0.0f;
                    r0.pinchStartX = 0.0f;
                    r0.pinchStartY = 0.0f;
                    r0.moveStartX = 0.0f;
                    r0.moveStartY = 0.0f;
                    r0.zooming = false;
                    r0.moving = false;
                    r0.doubleTap = false;
                    r0.invalidCoords = false;
                    r0.canDragDown = true;
                    r0.changingPage = false;
                    r0.switchImageAfterAnimation = 0;
                    if (r0.imagesArrLocals.isEmpty()) {
                        if (r0.currentFileNames[0] != null) {
                        }
                        z2 = false;
                        r0.canZoom = z2;
                        updateMinMax(r0.scale);
                        releasePlayer();
                    }
                    z2 = true;
                    r0.canZoom = z2;
                    updateMinMax(r0.scale);
                    releasePlayer();
                }
                r0.isStreaming = false;
                preparePlayer(videoPath, false, false);
                if (prevIndex != -1) {
                    checkProgress(0, false);
                    if (prevIndex > r0.currentIndex) {
                        temp = r0.rightImage;
                        r0.rightImage = r0.centerImage;
                        r0.centerImage = r0.leftImage;
                        r0.leftImage = temp;
                        tempProgress = r0.photoProgressViews[0];
                        r0.photoProgressViews[0] = r0.photoProgressViews[2];
                        r0.photoProgressViews[2] = tempProgress;
                        setIndexToImage(r0.leftImage, r0.currentIndex - 1);
                        checkProgress(1, false);
                        checkProgress(2, false);
                    } else if (prevIndex < r0.currentIndex) {
                        temp = r0.leftImage;
                        r0.leftImage = r0.centerImage;
                        r0.centerImage = r0.rightImage;
                        r0.rightImage = temp;
                        tempProgress = r0.photoProgressViews[0];
                        r0.photoProgressViews[0] = r0.photoProgressViews[1];
                        r0.photoProgressViews[1] = tempProgress;
                        setIndexToImage(r0.rightImage, r0.currentIndex + 1);
                        checkProgress(1, false);
                        checkProgress(2, false);
                    }
                } else {
                    setImages();
                    for (a = 0; a < 3; a++) {
                        checkProgress(a, false);
                    }
                }
            }
        }
    }

    private void setCurrentCaption(MessageObject messageObject, CharSequence caption, boolean animated) {
        MessageObject messageObject2 = messageObject;
        if (this.needCaptionLayout) {
            if (r1.captionTextView.getParent() != r1.pickerView) {
                r1.captionTextView.setBackgroundDrawable(null);
                r1.containerView.removeView(r1.captionTextView);
                r1.pickerView.addView(r1.captionTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 76.0f, 48.0f));
            }
        } else if (r1.captionTextView.getParent() != r1.containerView) {
            r1.captionTextView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r1.pickerView.removeView(r1.captionTextView);
            r1.containerView.addView(r1.captionTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        }
        if (r1.isCurrentVideo) {
            r1.captionTextView.setMaxLines(1);
            r1.captionTextView.setSingleLine(true);
        } else {
            r1.captionTextView.setSingleLine(false);
            r1.captionTextView.setMaxLines(10);
        }
        boolean wasVisisble = r1.captionTextView.getTag() != null;
        if (TextUtils.isEmpty(caption)) {
            CharSequence charSequence = caption;
            if (r1.needCaptionLayout) {
                r1.captionTextView.setText(LocaleController.getString("AddCaption", R.string.AddCaption));
                r1.captionTextView.setTag("empty");
                r1.captionTextView.setVisibility(0);
                r1.captionTextView.setTextColor(-NUM);
                return;
            }
            r1.captionTextView.setTextColor(-1);
            r1.captionTextView.setTag(null);
            if (animated && wasVisisble) {
                r1.currentCaptionAnimation = new AnimatorSet();
                r1.currentCaptionAnimation.setDuration(200);
                r1.currentCaptionAnimation.setInterpolator(decelerateInterpolator);
                r1.currentCaptionAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(PhotoViewer.this.currentCaptionAnimation)) {
                            PhotoViewer.this.captionTextView.setVisibility(4);
                            PhotoViewer.this.currentCaptionAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (animation.equals(PhotoViewer.this.currentCaptionAnimation)) {
                            PhotoViewer.this.currentCaptionAnimation = null;
                        }
                    }
                });
                AnimatorSet animatorSet = r1.currentCaptionAnimation;
                r5 = new Animator[2];
                r5[0] = ObjectAnimator.ofFloat(r1.captionTextView, "alpha", new float[]{0.0f});
                r5[1] = ObjectAnimator.ofFloat(r1.captionTextView, "translationY", new float[]{(float) AndroidUtilities.dp(5.0f)});
                animatorSet.playTogether(r5);
                r1.currentCaptionAnimation.start();
                return;
            }
            r1.captionTextView.setVisibility(4);
            return;
        }
        CharSequence str;
        Theme.createChatResources(null, true);
        if (messageObject2 == null || messageObject2.messageOwner.entities.isEmpty()) {
            str = Emoji.replaceEmoji(new SpannableStringBuilder(caption), r1.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        } else {
            Spannable spannableString = SpannableString.valueOf(caption.toString());
            messageObject2.addEntitiesToText(spannableString, true, false);
            str = Emoji.replaceEmoji(spannableString, r1.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            charSequence = caption;
        }
        r1.captionTextView.setTag(str);
        if (r1.currentCaptionAnimation != null) {
            r1.currentCaptionAnimation.cancel();
            r1.currentCaptionAnimation = null;
        }
        try {
            r1.captionTextView.setText(str);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        r1.captionTextView.setTextColor(-1);
        boolean visible = r1.isActionBarVisible && (r1.bottomLayout.getVisibility() == 0 || r1.pickerView.getVisibility() == 0);
        if (visible) {
            r1.captionTextView.setVisibility(0);
            if (!animated || wasVisisble) {
                r1.captionTextView.setAlpha(1.0f);
            } else {
                r1.currentCaptionAnimation = new AnimatorSet();
                r1.currentCaptionAnimation.setDuration(200);
                r1.currentCaptionAnimation.setInterpolator(decelerateInterpolator);
                r1.currentCaptionAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(PhotoViewer.this.currentCaptionAnimation)) {
                            PhotoViewer.this.currentCaptionAnimation = null;
                        }
                    }
                });
                AnimatorSet animatorSet2 = r1.currentCaptionAnimation;
                Animator[] animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(r1.captionTextView, "alpha", new float[]{0.0f, 1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(r1.captionTextView, "translationY", new float[]{(float) AndroidUtilities.dp(5.0f), 0.0f});
                animatorSet2.playTogether(animatorArr);
                r1.currentCaptionAnimation.start();
            }
        } else if (r1.captionTextView.getVisibility() == 0) {
            r1.captionTextView.setVisibility(4);
            r1.captionTextView.setAlpha(0.0f);
        }
    }

    private void checkProgress(int a, boolean animated) {
        int i = a;
        boolean z = animated;
        int index = this.currentIndex;
        if (i == 1) {
            index++;
        } else if (i == 2) {
            index--;
        }
        boolean exists;
        if (r0.currentFileNames[i] != null) {
            boolean z2;
            boolean z3;
            File f = null;
            boolean isVideo = false;
            boolean canStream = false;
            if (r0.currentMessageObject != null) {
                if (index >= 0) {
                    if (index < r0.imagesArr.size()) {
                        MessageObject messageObject = (MessageObject) r0.imagesArr.get(index);
                        if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                            f = new File(messageObject.messageOwner.attachPath);
                            if (!f.exists()) {
                                f = null;
                            }
                        }
                        if (f == null) {
                            if ((messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.document == null) {
                                f = FileLoader.getPathToAttach(getFileLocation(index, null), true);
                            } else {
                                f = FileLoader.getPathToMessage(messageObject.messageOwner);
                            }
                        }
                        boolean z4 = SharedConfig.streamMedia && messageObject.isVideo() && ((int) messageObject.getDialogId()) != 0;
                        canStream = z4;
                        isVideo = messageObject.isVideo();
                    }
                }
                r0.photoProgressViews[i].setBackgroundState(-1, z);
                return;
            } else if (r0.currentBotInlineResult != null) {
                if (index >= 0) {
                    if (index < r0.imagesArrLocals.size()) {
                        BotInlineResult botInlineResult = (BotInlineResult) r0.imagesArrLocals.get(index);
                        if (!botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                            if (!MessageObject.isVideoDocument(botInlineResult.document)) {
                                if (botInlineResult.document != null) {
                                    f = new File(FileLoader.getDirectory(3), r0.currentFileNames[i]);
                                } else if (botInlineResult.photo != null) {
                                    f = new File(FileLoader.getDirectory(0), r0.currentFileNames[i]);
                                }
                                if (f == null || !f.exists()) {
                                    f = new File(FileLoader.getDirectory(4), r0.currentFileNames[i]);
                                }
                            }
                        }
                        if (botInlineResult.document != null) {
                            f = FileLoader.getPathToAttach(botInlineResult.document);
                        } else if (botInlineResult.content instanceof TL_webDocument) {
                            File directory = FileLoader.getDirectory(4);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(Utilities.MD5(botInlineResult.content.url));
                            stringBuilder.append(".");
                            stringBuilder.append(ImageLoader.getHttpUrlExtension(botInlineResult.content.url, "mp4"));
                            f = new File(directory, stringBuilder.toString());
                        }
                        isVideo = true;
                        f = new File(FileLoader.getDirectory(4), r0.currentFileNames[i]);
                    }
                }
                r0.photoProgressViews[i].setBackgroundState(-1, z);
                return;
            } else if (r0.currentFileLocation != null) {
                if (index >= 0) {
                    if (index < r0.imagesArrLocations.size()) {
                        boolean z5;
                        FileLocation location = (FileLocation) r0.imagesArrLocations.get(index);
                        if (r0.avatarsDialogId == 0) {
                            if (!r0.isEvent) {
                                z5 = false;
                                f = FileLoader.getPathToAttach(location, z5);
                            }
                        }
                        z5 = true;
                        f = FileLoader.getPathToAttach(location, z5);
                    }
                }
                r0.photoProgressViews[i].setBackgroundState(-1, z);
                return;
            } else if (r0.currentPathObject != null) {
                f = new File(FileLoader.getDirectory(3), r0.currentFileNames[i]);
                if (!f.exists()) {
                    f = new File(FileLoader.getDirectory(4), r0.currentFileNames[i]);
                }
            }
            exists = f.exists();
            if (f == null || !(exists || canStream)) {
                if (!isVideo) {
                    z2 = true;
                    r0.photoProgressViews[i].setBackgroundState(0, z);
                } else if (FileLoader.getInstance(r0.currentAccount).isLoadingFile(r0.currentFileNames[i])) {
                    z2 = true;
                    r0.photoProgressViews[i].setBackgroundState(1, false);
                } else {
                    r0.photoProgressViews[i].setBackgroundState(2, false);
                    z2 = true;
                }
                Float progress = ImageLoader.getInstance().getFileProgress(r0.currentFileNames[i]);
                if (progress == null) {
                    progress = Float.valueOf(0.0f);
                }
                z3 = false;
                r0.photoProgressViews[i].setProgress(progress.floatValue(), false);
            } else {
                if (isVideo) {
                    r0.photoProgressViews[i].setBackgroundState(3, z);
                } else {
                    r0.photoProgressViews[i].setBackgroundState(-1, z);
                }
                if (i == 0) {
                    if (exists) {
                        r0.menuItem.hideSubItem(7);
                    } else if (FileLoader.getInstance(r0.currentAccount).isLoadingFile(r0.currentFileNames[i])) {
                        r0.menuItem.showSubItem(7);
                    } else {
                        r0.menuItem.hideSubItem(7);
                    }
                }
                z2 = true;
                z3 = false;
            }
            if (i == 0) {
                if (r0.imagesArrLocals.isEmpty()) {
                    if (r0.currentFileNames[z3] == null || isVideo || r0.photoProgressViews[z3].backgroundState == 0) {
                        r0.canZoom = z3;
                    }
                }
                z3 = z2;
                r0.canZoom = z3;
            }
        } else {
            exists = false;
            if (!r0.imagesArrLocals.isEmpty() && index >= 0 && index < r0.imagesArrLocals.size()) {
                PhotoEntry object = r0.imagesArrLocals.get(index);
                if (object instanceof PhotoEntry) {
                    exists = object.isVideo;
                }
            }
            if (exists) {
                r0.photoProgressViews[i].setBackgroundState(3, z);
            } else {
                r0.photoProgressViews[i].setBackgroundState(-1, z);
            }
        }
    }

    public int getSelectiongLength() {
        return this.captionEditText != null ? this.captionEditText.getSelectionLength() : 0;
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int index) {
        ImageReceiver imageReceiver2 = imageReceiver;
        int i = index;
        imageReceiver2.setOrientation(0, false);
        if (this.imagesArrLocals.isEmpty()) {
            int[] size = new int[1];
            TLObject fileLocation = getFileLocation(i, size);
            if (fileLocation != null) {
                boolean z;
                MessageObject messageObject = null;
                if (!r0.imagesArr.isEmpty()) {
                    messageObject = (MessageObject) r0.imagesArr.get(i);
                }
                MessageObject messageObject2 = messageObject;
                imageReceiver2.setParentMessageObject(messageObject2);
                if (messageObject2 != null) {
                    z = true;
                    imageReceiver2.setShouldGenerateQualityThumb(true);
                } else {
                    z = true;
                }
                BitmapHolder placeHolder;
                BitmapHolder placeHolder2;
                PhotoSize thumbLocation;
                if (messageObject2 != null && messageObject2.isVideo()) {
                    imageReceiver2.setNeedsQualityThumb(z);
                    if (messageObject2.photoThumbs == null || messageObject2.photoThumbs.isEmpty()) {
                        imageReceiver2.setImageBitmap(r0.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                    } else {
                        placeHolder = null;
                        if (r0.currentThumb != null && imageReceiver2 == r0.centerImage) {
                            placeHolder = r0.currentThumb;
                        }
                        placeHolder2 = placeHolder;
                        thumbLocation = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 100);
                        imageReceiver2.setImage(null, null, null, placeHolder2 != null ? new BitmapDrawable(placeHolder2.bitmap) : null, thumbLocation.location, "b", 0, null, 1);
                    }
                } else if (messageObject2 == null || r0.currentAnimation == null) {
                    boolean z2;
                    BitmapHolder bitmapHolder;
                    imageReceiver2.setNeedsQualityThumb(true);
                    placeHolder = null;
                    if (r0.currentThumb != null && imageReceiver2 == r0.centerImage) {
                        placeHolder = r0.currentThumb;
                    }
                    placeHolder2 = placeHolder;
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                    PhotoSize thumbLocation2 = messageObject2 != null ? FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 100) : null;
                    if (thumbLocation2 != null && thumbLocation2.location == fileLocation) {
                        thumbLocation2 = null;
                    }
                    thumbLocation = thumbLocation2;
                    if ((messageObject2 == null || !messageObject2.isWebpage()) && r0.avatarsDialogId == 0) {
                        if (!r0.isEvent) {
                            z2 = false;
                            bitmapHolder = placeHolder2;
                            imageReceiver2.setImage(fileLocation, null, null, placeHolder2 == null ? new BitmapDrawable(placeHolder2.bitmap) : null, thumbLocation == null ? thumbLocation.location : null, "b", size[0], null, z2 ? true : null);
                        }
                    }
                    z2 = true;
                    if (placeHolder2 == null) {
                    }
                    if (thumbLocation == null) {
                    }
                    if (z2) {
                    }
                    bitmapHolder = placeHolder2;
                    imageReceiver2.setImage(fileLocation, null, null, placeHolder2 == null ? new BitmapDrawable(placeHolder2.bitmap) : null, thumbLocation == null ? thumbLocation.location : null, "b", size[0], null, z2 ? true : null);
                } else {
                    imageReceiver2.setImageBitmap(r0.currentAnimation);
                    r0.currentAnimation.setSecondParentView(r0.containerView);
                }
                return;
            }
            imageReceiver2.setNeedsQualityThumb(true);
            imageReceiver2.setParentMessageObject(null);
            if (size[0] == 0) {
                imageReceiver2.setImageBitmap((Bitmap) null);
                return;
            } else {
                imageReceiver2.setImageBitmap(r0.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                return;
            }
        }
        imageReceiver2.setParentMessageObject(null);
        if (i < 0 || i >= r0.imagesArrLocals.size()) {
            imageReceiver2.setImageBitmap((Bitmap) null);
            return;
        }
        String path;
        TLObject webDocument;
        TLObject photo;
        int imageSize;
        boolean isVideo;
        TLObject document;
        String document2;
        Drawable bitmapDrawable;
        Drawable drawable;
        PhotoEntry object = r0.imagesArrLocals.get(i);
        int size2 = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
        BitmapHolder placeHolder3 = null;
        if (r0.currentThumb != null && imageReceiver2 == r0.centerImage) {
            placeHolder3 = r0.currentThumb;
        }
        if (placeHolder3 == null) {
            placeHolder3 = r0.placeProvider.getThumbForPhoto(null, null, i);
        }
        BitmapHolder placeHolder4 = placeHolder3;
        TLObject webDocument2 = null;
        TLObject photo2 = null;
        int imageSize2 = 0;
        String str = null;
        TLObject document3;
        String path2;
        if (object instanceof PhotoEntry) {
            PhotoEntry photoEntry = object;
            boolean isVideo2 = photoEntry.isVideo;
            if (photoEntry.isVideo) {
                document3 = null;
                if (photoEntry.thumbPath != null) {
                    path2 = photoEntry.thumbPath;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("vthumb://");
                    stringBuilder.append(photoEntry.imageId);
                    stringBuilder.append(":");
                    stringBuilder.append(photoEntry.path);
                    path2 = stringBuilder.toString();
                }
            } else {
                if (photoEntry.imagePath != null) {
                    path2 = photoEntry.imagePath;
                } else {
                    imageReceiver2.setOrientation(photoEntry.orientation, false);
                    path2 = photoEntry.path;
                }
                String path3 = path2;
                document3 = null;
                str = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)});
                path2 = path3;
            }
            path = path2;
            webDocument = null;
            photo = null;
            imageSize = 0;
            isVideo = isVideo2;
            document = document3;
        } else {
            TL_webDocument tL_webDocument;
            document2 = null;
            if (object instanceof BotInlineResult) {
                BotInlineResult botInlineResult = (BotInlineResult) object;
                if (botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                    tL_webDocument = null;
                } else if (MessageObject.isVideoDocument(botInlineResult.document)) {
                    tL_webDocument = null;
                } else if (botInlineResult.type.equals("gif") && botInlineResult.document != null) {
                    Document document4 = botInlineResult.document;
                    imageSize2 = botInlineResult.document.size;
                    str = "d";
                    tL_webDocument = null;
                    document2 = document4;
                } else if (botInlineResult.photo != null) {
                    PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize());
                    photo2 = sizeFull.location;
                    imageSize2 = sizeFull.size;
                    tL_webDocument = null;
                    str = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)});
                } else {
                    tL_webDocument = null;
                    if ((botInlineResult.content instanceof TL_webDocument) != null) {
                        if (botInlineResult.type.equals("gif") != null) {
                            path2 = "d";
                        } else {
                            path2 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)});
                        }
                        str = path2;
                        webDocument2 = (TL_webDocument) botInlineResult.content;
                    }
                }
                if (botInlineResult.document != null) {
                    photo2 = botInlineResult.document.thumb.location;
                } else if (botInlineResult.thumb instanceof TL_webDocument) {
                    webDocument2 = (TL_webDocument) botInlineResult.thumb;
                }
            } else {
                tL_webDocument = null;
                if (object instanceof SearchImage) {
                    SearchImage photoEntry2 = (SearchImage) object;
                    if (photoEntry2.imagePath != null) {
                        path2 = photoEntry2.imagePath;
                    } else if (photoEntry2.document != null) {
                        TLObject document5 = photoEntry2.document;
                        imageSize2 = photoEntry2.document.size;
                        document3 = document5;
                        path2 = tL_webDocument;
                    } else {
                        path2 = photoEntry2.imageUrl;
                        imageSize2 = photoEntry2.size;
                    }
                    path = path2;
                    webDocument = null;
                    photo = null;
                    imageSize = imageSize2;
                    isVideo = false;
                    document = document3;
                    document2 = "d";
                    if (document == null) {
                        imageReceiver2.setImage(document, null, "d", placeHolder4 == null ? new BitmapDrawable(placeHolder4.bitmap) : null, placeHolder4 != null ? document.thumb.location : null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)}), imageSize, null, null);
                    } else {
                        if (photo != null) {
                            imageReceiver2.setImage(photo, null, document2, placeHolder4 == null ? new BitmapDrawable(placeHolder4.bitmap) : null, null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)}), imageSize, null, 0);
                        } else if (webDocument == null) {
                            if (placeHolder4 == null) {
                                bitmapDrawable = new BitmapDrawable(placeHolder4.bitmap);
                            } else if (isVideo || r0.parentActivity == null) {
                                drawable = null;
                                imageReceiver2.setImage(webDocument, document2, drawable, null, imageSize);
                            } else {
                                bitmapDrawable = r0.parentActivity.getResources().getDrawable(R.drawable.nophotos);
                            }
                            drawable = bitmapDrawable;
                            imageReceiver2.setImage(webDocument, document2, drawable, null, imageSize);
                        } else {
                            if (placeHolder4 == null) {
                                bitmapDrawable = new BitmapDrawable(placeHolder4.bitmap);
                            } else if (isVideo || r0.parentActivity == null) {
                                drawable = null;
                                imageReceiver2.setImage(path, document2, drawable, null, imageSize);
                            } else {
                                bitmapDrawable = r0.parentActivity.getResources().getDrawable(R.drawable.nophotos);
                            }
                            drawable = bitmapDrawable;
                            imageReceiver2.setImage(path, document2, drawable, null, imageSize);
                        }
                    }
                }
            }
            photo = photo2;
            imageSize = imageSize2;
            isVideo = false;
            document = document2;
            path = tL_webDocument;
            webDocument = webDocument2;
        }
        document2 = str;
        if (document == null) {
            if (photo != null) {
                if (placeHolder4 == null) {
                }
                imageReceiver2.setImage(photo, null, document2, placeHolder4 == null ? new BitmapDrawable(placeHolder4.bitmap) : null, null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)}), imageSize, null, 0);
            } else if (webDocument == null) {
                if (placeHolder4 == null) {
                    if (isVideo) {
                    }
                    drawable = null;
                    imageReceiver2.setImage(path, document2, drawable, null, imageSize);
                } else {
                    bitmapDrawable = new BitmapDrawable(placeHolder4.bitmap);
                }
                drawable = bitmapDrawable;
                imageReceiver2.setImage(path, document2, drawable, null, imageSize);
            } else {
                if (placeHolder4 == null) {
                    if (isVideo) {
                    }
                    drawable = null;
                    imageReceiver2.setImage(webDocument, document2, drawable, null, imageSize);
                } else {
                    bitmapDrawable = new BitmapDrawable(placeHolder4.bitmap);
                }
                drawable = bitmapDrawable;
                imageReceiver2.setImage(webDocument, document2, drawable, null, imageSize);
            }
        } else {
            if (placeHolder4 == null) {
            }
            if (placeHolder4 != null) {
            }
            imageReceiver2.setImage(document, null, "d", placeHolder4 == null ? new BitmapDrawable(placeHolder4.bitmap) : null, placeHolder4 != null ? document.thumb.location : null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size2), Integer.valueOf(size2)}), imageSize, null, null);
        }
    }

    public static boolean isShowingImage(MessageObject object) {
        boolean result = false;
        boolean z = false;
        if (Instance != null) {
            boolean z2 = (Instance.pipAnimationInProgress || !Instance.isVisible || Instance.disableShowCheck || object == null || Instance.currentMessageObject == null || Instance.currentMessageObject.getId() != object.getId()) ? false : true;
            result = z2;
        }
        if (result || PipInstance == null) {
            return result;
        }
        if (!(!PipInstance.isVisible || PipInstance.disableShowCheck || object == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != object.getId())) {
            z = true;
        }
        return z;
    }

    public static boolean isShowingImage(FileLocation object) {
        if (Instance == null) {
            return false;
        }
        boolean z = Instance.isVisible && !Instance.disableShowCheck && object != null && Instance.currentFileLocation != null && object.local_id == Instance.currentFileLocation.local_id && object.volume_id == Instance.currentFileLocation.volume_id && object.dc_id == Instance.currentFileLocation.dc_id;
        return z;
    }

    public static boolean isShowingImage(BotInlineResult object) {
        if (Instance == null) {
            return false;
        }
        boolean z = (!Instance.isVisible || Instance.disableShowCheck || object == null || Instance.currentBotInlineResult == null || object.id != Instance.currentBotInlineResult.id) ? false : true;
        return z;
    }

    public static boolean isShowingImage(String object) {
        if (Instance == null) {
            return false;
        }
        boolean z = (!Instance.isVisible || Instance.disableShowCheck || object == null || Instance.currentPathObject == null || !object.equals(Instance.currentPathObject)) ? false : true;
        return z;
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
        if (this.pickerViewSendButton != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) r12.itemsLayout.getLayoutParams();
            if (r12.sendPhotoType == 1) {
                r12.pickerView.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
                r12.pickerViewSendButton.setImageResource(R.drawable.bigcheck);
                r12.pickerViewSendButton.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
                layoutParams.bottomMargin = AndroidUtilities.dp(16.0f);
            } else {
                r12.pickerView.setPadding(0, 0, 0, 0);
                r12.pickerViewSendButton.setImageResource(R.drawable.ic_send);
                r12.pickerViewSendButton.setPadding(AndroidUtilities.dp(4.0f), 0, 0, 0);
                layoutParams.bottomMargin = 0;
            }
            r12.itemsLayout.setLayoutParams(layoutParams);
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
        Throwable e;
        WindowManager windowManager;
        MessageObject messageObject2 = messageObject;
        FileLocation fileLocation2 = fileLocation;
        final ArrayList<Object> arrayList = photos;
        PhotoViewerProvider photoViewerProvider = provider;
        if (!(this.parentActivity == null || r8.isVisible || (photoViewerProvider == null && checkAnimation()))) {
            if (messageObject2 != null || fileLocation2 != null || messages != null || arrayList != null) {
                int i = index;
                PlaceProviderObject object = photoViewerProvider.getPlaceForPhoto(messageObject2, fileLocation2, i);
                if (object == null && arrayList == null) {
                    return false;
                }
                r8.lastInsets = null;
                WindowManager wm = (WindowManager) r8.parentActivity.getSystemService("window");
                if (r8.attachedToWindow) {
                    try {
                        wm.removeView(r8.windowView);
                    } catch (Exception e2) {
                    }
                }
                PlaceProviderObject placeProviderObject;
                try {
                    boolean z;
                    r8.windowLayoutParams.type = 99;
                    if (VERSION.SDK_INT >= 21) {
                        try {
                            r8.windowLayoutParams.flags = -NUM;
                        } catch (Throwable e3) {
                            e = e3;
                            windowManager = wm;
                            placeProviderObject = object;
                            FileLog.m3e(e);
                            return false;
                        }
                    }
                    r8.windowLayoutParams.flags = 8;
                    r8.windowLayoutParams.softInputMode = 272;
                    r8.windowView.setFocusable(false);
                    r8.containerView.setFocusable(false);
                    wm.addView(r8.windowView, r8.windowLayoutParams);
                    r8.doneButtonPressed = false;
                    r8.parentChatActivity = chatActivity;
                    r8.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(1), Integer.valueOf(1)));
                    NotificationCenter.getInstance(r8.currentAccount).addObserver(r8, NotificationCenter.FileDidFailedLoad);
                    NotificationCenter.getInstance(r8.currentAccount).addObserver(r8, NotificationCenter.FileDidLoaded);
                    NotificationCenter.getInstance(r8.currentAccount).addObserver(r8, NotificationCenter.FileLoadProgressChanged);
                    NotificationCenter.getInstance(r8.currentAccount).addObserver(r8, NotificationCenter.mediaCountDidLoaded);
                    NotificationCenter.getInstance(r8.currentAccount).addObserver(r8, NotificationCenter.mediaDidLoaded);
                    NotificationCenter.getInstance(r8.currentAccount).addObserver(r8, NotificationCenter.dialogPhotosLoaded);
                    NotificationCenter.getGlobalInstance().addObserver(r8, NotificationCenter.emojiDidLoaded);
                    NotificationCenter.getInstance(r8.currentAccount).addObserver(r8, NotificationCenter.FilePreparingFailed);
                    NotificationCenter.getInstance(r8.currentAccount).addObserver(r8, NotificationCenter.FileNewChunkAvailable);
                    r8.placeProvider = photoViewerProvider;
                    r8.mergeDialogId = mDialogId;
                    PlaceProviderObject object2 = object;
                    r8.currentDialogId = dialogId;
                    r8.selectedPhotosAdapter.notifyDataSetChanged();
                    if (r8.velocityTracker == null) {
                        r8.velocityTracker = VelocityTracker.obtain();
                    }
                    r8.isVisible = true;
                    toggleActionBar(true, false);
                    togglePhotosListView(false, false);
                    WindowManager wm2;
                    if (object2 != null) {
                        r8.disableShowCheck = true;
                        r8.animationInProgress = 1;
                        if (messageObject2 != null) {
                            placeProviderObject = object2;
                            r8.currentAnimation = placeProviderObject.imageReceiver.getAnimation();
                        } else {
                            placeProviderObject = object2;
                        }
                        wm2 = wm;
                        onPhotoShow(messageObject2, fileLocation2, messages, arrayList, i, placeProviderObject);
                        Rect drawRegion = placeProviderObject.imageReceiver.getDrawRegion();
                        int orientation = placeProviderObject.imageReceiver.getOrientation();
                        int animatedOrientation = placeProviderObject.imageReceiver.getAnimatedOrientation();
                        if (animatedOrientation != 0) {
                            orientation = animatedOrientation;
                        }
                        r8.animatingImageView.setVisibility(null);
                        r8.animatingImageView.setRadius(placeProviderObject.radius);
                        r8.animatingImageView.setOrientation(orientation);
                        r8.animatingImageView.setNeedRadius(placeProviderObject.radius != null ? true : null);
                        r8.animatingImageView.setImageBitmap(placeProviderObject.thumb);
                        r8.animatingImageView.setAlpha(1.0f);
                        r8.animatingImageView.setPivotX(0.0f);
                        r8.animatingImageView.setPivotY(0.0f);
                        r8.animatingImageView.setScaleX(placeProviderObject.scale);
                        r8.animatingImageView.setScaleY(placeProviderObject.scale);
                        r8.animatingImageView.setTranslationX(((float) placeProviderObject.viewX) + (((float) drawRegion.left) * placeProviderObject.scale));
                        r8.animatingImageView.setTranslationY(((float) placeProviderObject.viewY) + (((float) drawRegion.top) * placeProviderObject.scale));
                        ViewGroup.LayoutParams layoutParams = r8.animatingImageView.getLayoutParams();
                        layoutParams.width = drawRegion.right - drawRegion.left;
                        layoutParams.height = drawRegion.bottom - drawRegion.top;
                        r8.animatingImageView.setLayoutParams(layoutParams);
                        float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                        wm = ((float) (AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : null))) / ((float) layoutParams.height);
                        float scale = scaleX > wm ? wm : scaleX;
                        float width = ((float) layoutParams.width) * scale;
                        float height = ((float) layoutParams.height) * scale;
                        float xPos = (((float) AndroidUtilities.displaySize.x) - width) / 2.0f;
                        float scaleY = wm;
                        wm = (((float) (AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) - height) / NUM;
                        int clipHorizontal = Math.abs(drawRegion.left - placeProviderObject.imageReceiver.getImageX());
                        orientation = Math.abs(drawRegion.top - placeProviderObject.imageReceiver.getImageY());
                        int[] coords2 = new int[2];
                        placeProviderObject.parentView.getLocationInWindow(coords2);
                        int clipTop = ((coords2[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (placeProviderObject.viewY + drawRegion.top)) + placeProviderObject.clipTopAddition;
                        if (clipTop < 0) {
                            clipTop = 0;
                        }
                        int clipTop2 = clipTop;
                        int clipBottom = (((placeProviderObject.viewY + drawRegion.top) + layoutParams.height) - ((coords2[1] + placeProviderObject.parentView.getHeight()) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + placeProviderObject.clipBottomAddition;
                        if (clipBottom < 0) {
                            clipBottom = 0;
                        }
                        int clipTop3 = Math.max(clipTop2, orientation);
                        int clipBottom2 = Math.max(clipBottom, orientation);
                        r8.animationValues[0][0] = r8.animatingImageView.getScaleX();
                        r8.animationValues[0][1] = r8.animatingImageView.getScaleY();
                        r8.animationValues[0][2] = r8.animatingImageView.getTranslationX();
                        r8.animationValues[0][3] = r8.animatingImageView.getTranslationY();
                        r8.animationValues[0][4] = ((float) clipHorizontal) * placeProviderObject.scale;
                        r8.animationValues[0][5] = ((float) clipTop3) * placeProviderObject.scale;
                        r8.animationValues[0][6] = ((float) clipBottom2) * placeProviderObject.scale;
                        r8.animationValues[0][7] = (float) r8.animatingImageView.getRadius();
                        r8.animationValues[1][0] = scale;
                        r8.animationValues[1][1] = scale;
                        r8.animationValues[1][2] = xPos;
                        r8.animationValues[1][3] = wm;
                        r8.animationValues[1][4] = 0.0f;
                        r8.animationValues[1][5] = 0.0f;
                        r8.animationValues[1][6] = 0.0f;
                        r8.animationValues[1][7] = 0.0f;
                        r8.animatingImageView.setAnimationProgress(0.0f);
                        r8.backgroundDrawable.setAlpha(0);
                        r8.containerView.setAlpha(0.0f);
                        final AnimatorSet animatorSet = new AnimatorSet();
                        Animator[] animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(r8.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f});
                        animatorArr[1] = ObjectAnimator.ofInt(r8.backgroundDrawable, "alpha", new int[]{0, 255});
                        animatorArr[2] = ObjectAnimator.ofFloat(r8.containerView, "alpha", new float[]{0.0f, 1.0f});
                        animatorSet.playTogether(animatorArr);
                        r8.animationEndRunnable = new Runnable() {
                            public void run() {
                                if (PhotoViewer.this.containerView != null) {
                                    if (PhotoViewer.this.windowView != null) {
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
                                        if (!(arrayList == null || PhotoViewer.this.sendPhotoType == 3)) {
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
                            }
                        };
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {

                            /* renamed from: org.telegram.ui.PhotoViewer$65$1 */
                            class C16071 implements Runnable {
                                C16071() {
                                }

                                public void run() {
                                    NotificationCenter.getInstance(PhotoViewer.this.currentAccount).setAnimationInProgress(false);
                                    if (PhotoViewer.this.animationEndRunnable != null) {
                                        PhotoViewer.this.animationEndRunnable.run();
                                        PhotoViewer.this.animationEndRunnable = null;
                                    }
                                }
                            }

                            public void onAnimationEnd(Animator animation) {
                                AndroidUtilities.runOnUIThread(new C16071());
                            }
                        });
                        r8.transitionAnimationStartTime = System.currentTimeMillis();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(PhotoViewer.this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded, NotificationCenter.mediaDidLoaded, NotificationCenter.dialogPhotosLoaded});
                                NotificationCenter.getInstance(PhotoViewer.this.currentAccount).setAnimationInProgress(true);
                                animatorSet.start();
                            }
                        });
                        if (VERSION.SDK_INT >= 18) {
                            r8.containerView.setLayerType(2, null);
                        }
                        r8.backgroundDrawable.drawRunnable = new Runnable() {
                            public void run() {
                                PhotoViewer.this.disableShowCheck = false;
                                placeProviderObject.imageReceiver.setVisible(false, true);
                            }
                        };
                        windowManager = wm2;
                        z = true;
                    } else {
                        wm2 = wm;
                        placeProviderObject = object2;
                        z = true;
                        if (arrayList == null || r8.sendPhotoType == 3) {
                        } else {
                            if (VERSION.SDK_INT >= 21) {
                                r8.windowLayoutParams.flags = -NUM;
                            } else {
                                r8.windowLayoutParams.flags = 0;
                            }
                            r8.windowLayoutParams.softInputMode = 272;
                            wm2.updateViewLayout(r8.windowView, r8.windowLayoutParams);
                            r8.windowView.setFocusable(true);
                            r8.containerView.setFocusable(true);
                        }
                        r8.backgroundDrawable.setAlpha(255);
                        r8.containerView.setAlpha(1.0f);
                        onPhotoShow(messageObject2, fileLocation, messages, arrayList, index, placeProviderObject);
                    }
                    return z;
                } catch (Throwable e32) {
                    windowManager = wm;
                    placeProviderObject = object;
                    e = e32;
                    FileLog.m3e(e);
                    return false;
                }
            }
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void closePhoto(boolean animated, boolean fromEditMode) {
        PhotoViewer photoViewer = this;
        if (fromEditMode || photoViewer.currentEditMode == 0) {
            if (photoViewer.qualityChooseView == null || photoViewer.qualityChooseView.getTag() == null) {
                try {
                    if (photoViewer.visibleDialog != null) {
                        photoViewer.visibleDialog.dismiss();
                        photoViewer.visibleDialog = null;
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                if (!(VERSION.SDK_INT < 21 || photoViewer.actionBar == null || (photoViewer.windowLayoutParams.flags & 1024) == 0)) {
                    LayoutParams layoutParams = photoViewer.windowLayoutParams;
                    layoutParams.flags &= -1025;
                    ((WindowManager) photoViewer.parentActivity.getSystemService("window")).updateViewLayout(photoViewer.windowView, photoViewer.windowLayoutParams);
                }
                if (photoViewer.currentEditMode != 0) {
                    if (photoViewer.currentEditMode == 2) {
                        photoViewer.photoFilterView.shutdown();
                        photoViewer.containerView.removeView(photoViewer.photoFilterView);
                        photoViewer.photoFilterView = null;
                    } else if (photoViewer.currentEditMode == 1) {
                        photoViewer.editorDoneLayout.setVisibility(8);
                        photoViewer.photoCropView.setVisibility(8);
                    }
                    photoViewer.currentEditMode = 0;
                }
                if (photoViewer.parentActivity != null && ((photoViewer.isInline || photoViewer.isVisible) && !checkAnimation())) {
                    if (photoViewer.placeProvider != null) {
                        if (!photoViewer.captionEditText.hideActionMode() || fromEditMode) {
                            releasePlayer();
                            photoViewer.captionEditText.onDestroy();
                            photoViewer.parentChatActivity = null;
                            removeObservers();
                            photoViewer.isActionBarVisible = false;
                            if (photoViewer.velocityTracker != null) {
                                photoViewer.velocityTracker.recycle();
                                photoViewer.velocityTracker = null;
                            }
                            final PlaceProviderObject object = photoViewer.placeProvider.getPlaceForPhoto(photoViewer.currentMessageObject, photoViewer.currentFileLocation, photoViewer.currentIndex);
                            if (photoViewer.isInline) {
                                photoViewer.isInline = false;
                                photoViewer.animationInProgress = 0;
                                onPhotoClosed(object);
                                photoViewer.containerView.setScaleX(1.0f);
                                photoViewer.containerView.setScaleY(1.0f);
                            } else {
                                Object obj;
                                Animator[] animatorArr;
                                if (animated) {
                                    Rect drawRegion;
                                    photoViewer.animationInProgress = 1;
                                    photoViewer.animatingImageView.setVisibility(0);
                                    photoViewer.containerView.invalidate();
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    ViewGroup.LayoutParams layoutParams2 = photoViewer.animatingImageView.getLayoutParams();
                                    int orientation = photoViewer.centerImage.getOrientation();
                                    int animatedOrientation = 0;
                                    if (!(object == null || object.imageReceiver == null)) {
                                        animatedOrientation = object.imageReceiver.getAnimatedOrientation();
                                    }
                                    if (animatedOrientation != 0) {
                                        orientation = animatedOrientation;
                                    }
                                    photoViewer.animatingImageView.setOrientation(orientation);
                                    if (object != null) {
                                        photoViewer.animatingImageView.setNeedRadius(object.radius != 0);
                                        drawRegion = object.imageReceiver.getDrawRegion();
                                        layoutParams2.width = drawRegion.right - drawRegion.left;
                                        layoutParams2.height = drawRegion.bottom - drawRegion.top;
                                        photoViewer.animatingImageView.setImageBitmap(object.thumb);
                                    } else {
                                        photoViewer.animatingImageView.setNeedRadius(false);
                                        layoutParams2.width = photoViewer.centerImage.getImageWidth();
                                        layoutParams2.height = photoViewer.centerImage.getImageHeight();
                                        photoViewer.animatingImageView.setImageBitmap(photoViewer.centerImage.getBitmapSafe());
                                        drawRegion = null;
                                    }
                                    photoViewer.animatingImageView.setLayoutParams(layoutParams2);
                                    float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams2.width);
                                    float scaleY = ((float) (AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / ((float) layoutParams2.height);
                                    float scale2 = scaleX > scaleY ? scaleY : scaleX;
                                    float width = (((float) layoutParams2.width) * photoViewer.scale) * scale2;
                                    float height = (((float) layoutParams2.height) * photoViewer.scale) * scale2;
                                    float xPos = (((float) AndroidUtilities.displaySize.x) - width) / 2.0f;
                                    float yPos = (((float) (AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) - height) / 2.0f;
                                    photoViewer.animatingImageView.setTranslationX(photoViewer.translationX + xPos);
                                    photoViewer.animatingImageView.setTranslationY(photoViewer.translationY + yPos);
                                    photoViewer.animatingImageView.setScaleX(photoViewer.scale * scale2);
                                    photoViewer.animatingImageView.setScaleY(photoViewer.scale * scale2);
                                    int yPos2;
                                    if (object != null) {
                                        object.imageReceiver.setVisible(false, Float.MIN_VALUE);
                                        yPos2 = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
                                        int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());
                                        int[] coords2 = new int[2];
                                        object.parentView.getLocationInWindow(coords2);
                                        int clipTop = ((coords2[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (object.viewY + drawRegion.top)) + object.clipTopAddition;
                                        if (clipTop < 0) {
                                            clipTop = 0;
                                        }
                                        int clipTop2 = clipTop;
                                        int clipBottom = (((object.viewY + drawRegion.top) + (drawRegion.bottom - drawRegion.top)) - ((coords2[1] + object.parentView.getHeight()) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + object.clipBottomAddition;
                                        if (clipBottom < 0) {
                                            clipBottom = 0;
                                        }
                                        clipTop2 = Math.max(clipTop2, clipVertical);
                                        height = Math.max(clipBottom, clipVertical);
                                        photoViewer.animationValues[0][0] = photoViewer.animatingImageView.getScaleX();
                                        photoViewer.animationValues[0][1] = photoViewer.animatingImageView.getScaleY();
                                        photoViewer.animationValues[0][2] = photoViewer.animatingImageView.getTranslationX();
                                        photoViewer.animationValues[0][3] = photoViewer.animatingImageView.getTranslationY();
                                        photoViewer.animationValues[0][4] = 0.0f;
                                        photoViewer.animationValues[0][5] = 0.0f;
                                        photoViewer.animationValues[0][6] = 0.0f;
                                        photoViewer.animationValues[0][7] = 0.0f;
                                        photoViewer.animationValues[1][0] = object.scale;
                                        photoViewer.animationValues[1][1] = object.scale;
                                        photoViewer.animationValues[1][2] = (((float) object.viewX) + (((float) drawRegion.left) * object.scale)) - ((float) getLeftInset());
                                        photoViewer.animationValues[1][3] = ((float) object.viewY) + (((float) drawRegion.top) * object.scale);
                                        photoViewer.animationValues[1][4] = ((float) yPos2) * object.scale;
                                        photoViewer.animationValues[1][5] = ((float) clipTop2) * object.scale;
                                        photoViewer.animationValues[1][6] = ((float) height) * object.scale;
                                        photoViewer.animationValues[1][7] = (float) object.radius;
                                        animatorArr = new Animator[3];
                                        int clipHorizontal = yPos2;
                                        animatorArr[0] = ObjectAnimator.ofFloat(photoViewer.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f});
                                        animatorArr[1] = ObjectAnimator.ofInt(photoViewer.backgroundDrawable, "alpha", new int[]{0});
                                        animatorArr[2] = ObjectAnimator.ofFloat(photoViewer.containerView, "alpha", new float[]{0.0f});
                                        animatorSet.playTogether(animatorArr);
                                    } else {
                                        float f = height;
                                        ViewGroup.LayoutParams layoutParams3 = layoutParams2;
                                        float f2 = xPos;
                                        float f3 = scaleY;
                                        yPos2 = AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                                        animatorArr = new Animator[4];
                                        animatorArr[0] = ObjectAnimator.ofInt(photoViewer.backgroundDrawable, "alpha", new int[]{0});
                                        animatorArr[1] = ObjectAnimator.ofFloat(photoViewer.animatingImageView, "alpha", new float[]{0.0f});
                                        ClippingImageView clippingImageView = photoViewer.animatingImageView;
                                        String str = "translationY";
                                        float[] fArr = new float[1];
                                        fArr[0] = photoViewer.translationY >= 0.0f ? (float) yPos2 : (float) (-yPos2);
                                        animatorArr[2] = ObjectAnimator.ofFloat(clippingImageView, str, fArr);
                                        animatorArr[3] = ObjectAnimator.ofFloat(photoViewer.containerView, "alpha", new float[]{0.0f});
                                        animatorSet.playTogether(animatorArr);
                                    }
                                    photoViewer.animationEndRunnable = new Runnable() {
                                        public void run() {
                                            if (VERSION.SDK_INT >= 18) {
                                                PhotoViewer.this.containerView.setLayerType(0, null);
                                            }
                                            PhotoViewer.this.animationInProgress = 0;
                                            PhotoViewer.this.onPhotoClosed(object);
                                        }
                                    };
                                    animatorSet.setDuration(200);
                                    animatorSet.addListener(new AnimatorListenerAdapter() {

                                        /* renamed from: org.telegram.ui.PhotoViewer$70$1 */
                                        class C16091 implements Runnable {
                                            C16091() {
                                            }

                                            public void run() {
                                                if (PhotoViewer.this.animationEndRunnable != null) {
                                                    PhotoViewer.this.animationEndRunnable.run();
                                                    PhotoViewer.this.animationEndRunnable = null;
                                                }
                                            }
                                        }

                                        public void onAnimationEnd(Animator animation) {
                                            AndroidUtilities.runOnUIThread(new C16091());
                                        }
                                    });
                                    photoViewer.transitionAnimationStartTime = System.currentTimeMillis();
                                    if (VERSION.SDK_INT >= 18) {
                                        photoViewer.containerView.setLayerType(2, null);
                                    }
                                    animatorSet.start();
                                    obj = null;
                                } else {
                                    AnimatorSet animatorSet2 = new AnimatorSet();
                                    animatorArr = new Animator[4];
                                    animatorArr[0] = ObjectAnimator.ofFloat(photoViewer.containerView, "scaleX", new float[]{0.9f});
                                    animatorArr[1] = ObjectAnimator.ofFloat(photoViewer.containerView, "scaleY", new float[]{0.9f});
                                    animatorArr[2] = ObjectAnimator.ofInt(photoViewer.backgroundDrawable, "alpha", new int[]{0});
                                    animatorArr[3] = ObjectAnimator.ofFloat(photoViewer.containerView, "alpha", new float[]{0.0f});
                                    animatorSet2.playTogether(animatorArr);
                                    photoViewer.animationInProgress = 2;
                                    photoViewer.animationEndRunnable = new Runnable() {
                                        public void run() {
                                            if (PhotoViewer.this.containerView != null) {
                                                if (VERSION.SDK_INT >= 18) {
                                                    PhotoViewer.this.containerView.setLayerType(0, null);
                                                }
                                                PhotoViewer.this.animationInProgress = 0;
                                                PhotoViewer.this.onPhotoClosed(object);
                                                PhotoViewer.this.containerView.setScaleX(1.0f);
                                                PhotoViewer.this.containerView.setScaleY(1.0f);
                                            }
                                        }
                                    };
                                    animatorSet2.setDuration(200);
                                    animatorSet2.addListener(new AnimatorListenerAdapter() {
                                        public void onAnimationEnd(Animator animation) {
                                            if (PhotoViewer.this.animationEndRunnable != null) {
                                                PhotoViewer.this.animationEndRunnable.run();
                                                PhotoViewer.this.animationEndRunnable = null;
                                            }
                                        }
                                    });
                                    photoViewer.transitionAnimationStartTime = System.currentTimeMillis();
                                    if (VERSION.SDK_INT >= 18) {
                                        obj = null;
                                        photoViewer.containerView.setLayerType(2, null);
                                    } else {
                                        obj = null;
                                    }
                                    animatorSet2.start();
                                }
                                if (photoViewer.currentAnimation != null) {
                                    photoViewer.currentAnimation.setSecondParentView(obj);
                                    photoViewer.currentAnimation = obj;
                                    photoViewer.centerImage.setImageBitmap((Drawable) obj);
                                }
                                if (!(photoViewer.placeProvider == null || photoViewer.placeProvider.canScrollAway())) {
                                    photoViewer.placeProvider.cancelButtonPressed();
                                }
                            }
                            return;
                        }
                        return;
                    }
                }
                return;
            }
            photoViewer.qualityPicker.cancelButton.callOnClick();
        } else if (photoViewer.currentEditMode != 3 || photoViewer.photoPaintView == null) {
            if (photoViewer.currentEditMode == 1) {
                photoViewer.photoCropView.cancelAnimationRunnable();
            }
            switchToEditMode(0);
        } else {
            photoViewer.photoPaintView.maybeShowDismissalAlert(photoViewer, photoViewer.parentActivity, new Runnable() {
                public void run() {
                    PhotoViewer.this.switchToEditMode(0);
                }
            });
        }
    }

    private void removeObservers() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FilePreparingFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileNewChunkAvailable);
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
    }

    public void destroyPhotoViewer() {
        if (this.parentActivity != null) {
            if (this.windowView != null) {
                if (this.pipVideoView != null) {
                    this.pipVideoView.close();
                    this.pipVideoView = null;
                }
                removeObservers();
                releasePlayer();
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                    }
                    this.windowView = null;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                if (this.currentThumb != null) {
                    this.currentThumb.release();
                    this.currentThumb = null;
                }
                this.animatingImageView.setImageBitmap(null);
                if (this.captionEditText != null) {
                    this.captionEditText.onDestroy();
                }
                if (this == PipInstance) {
                    PipInstance = null;
                } else {
                    Instance = null;
                }
            }
        }
    }

    private void onPhotoClosed(PlaceProviderObject object) {
        this.isVisible = false;
        this.disableShowCheck = true;
        this.currentMessageObject = null;
        this.currentBotInlineResult = null;
        this.currentFileLocation = null;
        this.currentPathObject = null;
        if (this.currentThumb != null) {
            this.currentThumb.release();
            this.currentThumb = null;
        }
        this.parentAlert = null;
        if (this.currentAnimation != null) {
            this.currentAnimation.setSecondParentView(null);
            this.currentAnimation = null;
        }
        for (int a = 0; a < 3; a++) {
            if (this.photoProgressViews[a] != null) {
                this.photoProgressViews[a].setBackgroundState(-1, false);
            }
        }
        requestVideoPreview(0);
        if (this.videoTimelineView != null) {
            this.videoTimelineView.destroy();
        }
        Bitmap bitmap = (Bitmap) null;
        this.centerImage.setImageBitmap(bitmap);
        this.leftImage.setImageBitmap(bitmap);
        this.rightImage.setImageBitmap(bitmap);
        this.containerView.post(new Runnable() {
            public void run() {
                PhotoViewer.this.animatingImageView.setImageBitmap(null);
                try {
                    if (PhotoViewer.this.windowView.getParent() != null) {
                        ((WindowManager) PhotoViewer.this.parentActivity.getSystemService("window")).removeView(PhotoViewer.this.windowView);
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
        if (this.placeProvider != null) {
            this.placeProvider.willHidePhotoViewer();
        }
        this.groupedPhotosListView.clear();
        this.placeProvider = null;
        this.selectedPhotosAdapter.notifyDataSetChanged();
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
        if (this.videoPlayer != null) {
            this.videoPlayer.seekTo(this.videoPlayer.getCurrentPosition() + 1);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (this.pipVideoView != null) {
            this.pipVideoView.onConfigurationChanged();
        }
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false, false);
            return;
        }
        if (this.lastTitle != null) {
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
        int dp;
        if (this.currentEditMode == 3) {
            dp = AndroidUtilities.dp(8.0f);
            if (VERSION.SDK_INT >= 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            return dp + i;
        } else if (this.currentEditMode == 0) {
            return 0;
        } else {
            dp = AndroidUtilities.dp(14.0f);
            if (VERSION.SDK_INT >= 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            return dp + i;
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
            return height - AndroidUtilities.dp(214.0f);
        }
        if (mode == 3) {
            return height - (AndroidUtilities.dp(48.0f) + ActionBar.getCurrentActionBarHeight());
        }
        return height;
    }

    private boolean onTouchEvent(MotionEvent ev) {
        if (this.animationInProgress == 0) {
            if (this.animationStartTime == 0) {
                if (this.currentEditMode == 2) {
                    this.photoFilterView.onTouch(ev);
                    return true;
                } else if (this.currentEditMode == 1) {
                    return true;
                } else {
                    if (!this.captionEditText.isPopupShowing()) {
                        if (!this.captionEditText.isKeyboardVisible()) {
                            if (this.currentEditMode == 0 && ev.getPointerCount() == 1 && this.gestureDetector.onTouchEvent(ev) && this.doubleTap) {
                                this.doubleTap = false;
                                this.moving = false;
                                this.zooming = false;
                                checkMinMax(false);
                                return true;
                            }
                            float dx;
                            if (ev.getActionMasked() != 0) {
                                if (ev.getActionMasked() != 5) {
                                    float dy;
                                    float moveDx;
                                    float moveDy;
                                    if (ev.getActionMasked() == 2) {
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
                                            dx = Math.abs(ev.getX() - this.moveStartX);
                                            dy = Math.abs(ev.getY() - this.dragY);
                                            if (dx > ((float) AndroidUtilities.dp(3.0f)) || dy > ((float) AndroidUtilities.dp(3.0f))) {
                                                this.discardTap = true;
                                                if (this.qualityChooseView != null && this.qualityChooseView.getVisibility() == 0) {
                                                    return true;
                                                }
                                            }
                                            if (this.placeProvider.canScrollAway() && this.currentEditMode == 0 && this.canDragDown && !this.draggingDown && this.scale == 1.0f && dy >= ((float) AndroidUtilities.dp(30.0f)) && dy / 2.0f > dx) {
                                                this.draggingDown = true;
                                                this.moving = false;
                                                this.dragY = ev.getY();
                                                if (this.isActionBarVisible && this.containerView.getTag() != null) {
                                                    toggleActionBar(false, true);
                                                } else if (this.pickerView.getVisibility() == 0) {
                                                    toggleActionBar(false, true);
                                                    togglePhotosListView(false, true);
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
                                                moveDx = this.moveStartX - ev.getX();
                                                moveDy = this.moveStartY - ev.getY();
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
                                                dx = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                                                moveDx = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                                                updateMinMax(3.0f);
                                                if (dx < this.minX) {
                                                    dx = this.minX;
                                                } else if (dx > this.maxX) {
                                                    dx = this.maxX;
                                                }
                                                if (moveDx < this.minY) {
                                                    moveDx = this.minY;
                                                } else if (moveDx > this.maxY) {
                                                    moveDx = this.maxY;
                                                }
                                                animateTo(3.0f, dx, moveDx, true);
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
                                            dx = this.translationX;
                                            dy = this.translationY;
                                            updateMinMax(this.scale);
                                            this.moving = false;
                                            this.canDragDown = true;
                                            moveDy = 0.0f;
                                            if (this.velocityTracker != null && this.scale == 1.0f) {
                                                this.velocityTracker.computeCurrentVelocity(1000);
                                                moveDy = this.velocityTracker.getXVelocity();
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
                                                dx = this.minX;
                                            } else if (this.translationX > this.maxX) {
                                                dx = this.maxX;
                                            }
                                            if (this.translationY < this.minY) {
                                                dy = this.minY;
                                            } else if (this.translationY > this.maxY) {
                                                dy = this.maxY;
                                            }
                                            animateTo(this.scale, dx, dy, false);
                                        }
                                    }
                                    return false;
                                }
                            }
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
                                    dx = ev.getY();
                                    this.moveStartY = dx;
                                    this.dragY = dx;
                                    this.draggingDown = false;
                                    this.canDragDown = true;
                                    if (this.velocityTracker != null) {
                                        this.velocityTracker.clear();
                                    }
                                }
                            }
                            return false;
                        }
                    }
                    if (ev.getAction() == 1) {
                        closeCaptionEnter(true);
                    }
                    return true;
                }
            }
        }
        return false;
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
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - extra) - ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void goToPrev() {
        float extra = 0.0f;
        if (this.scale != 1.0f) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale;
        }
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, ((this.maxX + ((float) getContainerViewWidth())) + extra) + ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
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

    @Keep
    public void setAnimationValue(float value) {
        this.animationValue = value;
        this.containerView.invalidate();
    }

    @Keep
    public float getAnimationValue() {
        return this.animationValue;
    }

    private void hideHint() {
        this.hintAnimation = new AnimatorSet();
        AnimatorSet animatorSet = this.hintAnimation;
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        this.hintAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PhotoViewer.this.hintAnimation)) {
                    PhotoViewer.this.hintAnimation = null;
                    PhotoViewer.this.hintHideRunnable = null;
                    if (PhotoViewer.this.hintTextView != null) {
                        PhotoViewer.this.hintTextView.setVisibility(8);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (animation.equals(PhotoViewer.this.hintAnimation)) {
                    PhotoViewer.this.hintHideRunnable = null;
                    PhotoViewer.this.hintHideRunnable = null;
                }
            }
        });
        this.hintAnimation.setDuration(300);
        this.hintAnimation.start();
    }

    private void showHint(boolean hide, boolean enabled) {
        if (this.containerView != null) {
            if (!hide || this.hintTextView != null) {
                if (this.hintTextView == null) {
                    this.hintTextView = new TextView(this.containerView.getContext());
                    this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
                    this.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
                    this.hintTextView.setTextSize(1, 14.0f);
                    this.hintTextView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
                    this.hintTextView.setGravity(16);
                    this.hintTextView.setAlpha(0.0f);
                    this.containerView.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 5.0f, 0.0f, 5.0f, 3.0f));
                }
                if (hide) {
                    if (this.hintAnimation != null) {
                        this.hintAnimation.cancel();
                        this.hintAnimation = null;
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.hintHideRunnable);
                    this.hintHideRunnable = null;
                    hideHint();
                    return;
                }
                String str;
                int i;
                TextView textView = this.hintTextView;
                if (enabled) {
                    str = "GroupPhotosHelp";
                    i = R.string.GroupPhotosHelp;
                } else {
                    str = "SinglePhotosHelp";
                    i = R.string.SinglePhotosHelp;
                }
                textView.setText(LocaleController.getString(str, i));
                if (this.hintHideRunnable != null) {
                    if (this.hintAnimation != null) {
                        this.hintAnimation.cancel();
                        this.hintAnimation = null;
                    } else {
                        AndroidUtilities.cancelRunOnUIThread(this.hintHideRunnable);
                        Runnable anonymousClass77 = new Runnable() {
                            public void run() {
                                PhotoViewer.this.hideHint();
                            }
                        };
                        this.hintHideRunnable = anonymousClass77;
                        AndroidUtilities.runOnUIThread(anonymousClass77, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        return;
                    }
                } else if (this.hintAnimation != null) {
                    return;
                }
                this.hintTextView.setVisibility(0);
                this.hintAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.hintAnimation;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
                this.hintAnimation.addListener(new AnimatorListenerAdapter() {

                    /* renamed from: org.telegram.ui.PhotoViewer$78$1 */
                    class C16101 implements Runnable {
                        C16101() {
                        }

                        public void run() {
                            PhotoViewer.this.hideHint();
                        }
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(PhotoViewer.this.hintAnimation)) {
                            PhotoViewer.this.hintAnimation = null;
                            AndroidUtilities.runOnUIThread(PhotoViewer.this.hintHideRunnable = new C16101(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (animation.equals(PhotoViewer.this.hintAnimation)) {
                            PhotoViewer.this.hintAnimation = null;
                        }
                    }
                });
                this.hintAnimation.setDuration(300);
                this.hintAnimation.start();
            }
        }
    }

    @SuppressLint({"NewApi", "DrawAllocation"})
    private void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.animationInProgress != 1) {
            if (r1.isVisible || r1.animationInProgress == 2 || r1.pipAnimationInProgress) {
                float ts;
                float tx;
                float ty;
                float currentScale;
                float currentTranslationY;
                float currentTranslationX;
                float alpha;
                int bitmapWidth;
                float scale;
                int bitmapHeight;
                float f;
                float aty = -1.0f;
                if (r1.imageMoveAnimation != null) {
                    if (!r1.scroller.isFinished()) {
                        r1.scroller.abortAnimation();
                    }
                    ts = r1.scale + ((r1.animateToScale - r1.scale) * r1.animationValue);
                    tx = r1.translationX + ((r1.animateToX - r1.translationX) * r1.animationValue);
                    ty = r1.translationY + ((r1.animateToY - r1.translationY) * r1.animationValue);
                    if (r1.currentEditMode == 1) {
                        r1.photoCropView.setAnimationProgress(r1.animationValue);
                    }
                    if (r1.animateToScale == 1.0f && r1.scale == 1.0f && r1.translationX == 0.0f) {
                        aty = ty;
                    }
                    currentScale = ts;
                    currentTranslationY = ty;
                    currentTranslationX = tx;
                    r1.containerView.invalidate();
                } else {
                    if (r1.animationStartTime != 0) {
                        r1.translationX = r1.animateToX;
                        r1.translationY = r1.animateToY;
                        r1.scale = r1.animateToScale;
                        r1.animationStartTime = 0;
                        if (r1.currentEditMode == 1) {
                            r1.photoCropView.setAnimationProgress(1.0f);
                        }
                        updateMinMax(r1.scale);
                        r1.zoomAnimation = false;
                    }
                    if (!r1.scroller.isFinished() && r1.scroller.computeScrollOffset()) {
                        if (((float) r1.scroller.getStartX()) < r1.maxX && ((float) r1.scroller.getStartX()) > r1.minX) {
                            r1.translationX = (float) r1.scroller.getCurrX();
                        }
                        if (((float) r1.scroller.getStartY()) < r1.maxY && ((float) r1.scroller.getStartY()) > r1.minY) {
                            r1.translationY = (float) r1.scroller.getCurrY();
                        }
                        r1.containerView.invalidate();
                    }
                    if (r1.switchImageAfterAnimation != 0) {
                        if (r1.switchImageAfterAnimation == 1) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    PhotoViewer.this.setImageIndex(PhotoViewer.this.currentIndex + 1, false);
                                }
                            });
                        } else if (r1.switchImageAfterAnimation == 2) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    PhotoViewer.this.setImageIndex(PhotoViewer.this.currentIndex - 1, false);
                                }
                            });
                        }
                        r1.switchImageAfterAnimation = 0;
                    }
                    currentScale = r1.scale;
                    currentTranslationY = r1.translationY;
                    currentTranslationX = r1.translationX;
                    if (!r1.moving) {
                        aty = r1.translationY;
                    }
                }
                if (!(r1.animationInProgress == 2 || r1.pipAnimationInProgress || r1.isInline)) {
                    if (r1.currentEditMode != 0 || r1.scale != 1.0f || aty == -1.0f || r1.zoomAnimation) {
                        r1.backgroundDrawable.setAlpha(255);
                    } else {
                        ts = ((float) getContainerViewHeight()) / 4.0f;
                        r1.backgroundDrawable.setAlpha((int) Math.max(127.0f, 255.0f * (1.0f - (Math.min(Math.abs(aty), ts) / ts))));
                    }
                }
                ImageReceiver sideImage = null;
                if (r1.currentEditMode == 0) {
                    if (!(r1.scale < 1.0f || r1.zoomAnimation || r1.zooming)) {
                        if (currentTranslationX > r1.maxX + ((float) AndroidUtilities.dp(5.0f))) {
                            sideImage = r1.leftImage;
                        } else if (currentTranslationX < r1.minX - ((float) AndroidUtilities.dp(5.0f))) {
                            sideImage = r1.rightImage;
                        } else {
                            r1.groupedPhotosListView.setMoveProgress(0.0f);
                        }
                    }
                    r1.changingPage = sideImage != null;
                }
                if (sideImage == r1.rightImage) {
                    float translateX = currentTranslationX;
                    float scaleDiff = 0.0f;
                    float alpha2 = 1.0f;
                    if (!r1.zoomAnimation && translateX < r1.minX) {
                        alpha2 = Math.min(1.0f, (r1.minX - translateX) / ((float) canvas.getWidth()));
                        scaleDiff = (1.0f - alpha2) * 0.3f;
                        translateX = (float) ((-canvas.getWidth()) - (AndroidUtilities.dp(30.0f) / 2));
                    }
                    alpha = alpha2;
                    if (sideImage.hasBitmapImage()) {
                        canvas.save();
                        canvas2.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                        canvas2.translate(((float) (canvas.getWidth() + (AndroidUtilities.dp(30.0f) / 2))) + translateX, 0.0f);
                        canvas2.scale(1.0f - scaleDiff, 1.0f - scaleDiff);
                        bitmapWidth = sideImage.getBitmapWidth();
                        int bitmapHeight2 = sideImage.getBitmapHeight();
                        float scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                        float scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight2);
                        scale = scaleX > scaleY ? scaleY : scaleX;
                        int width = (int) (((float) bitmapWidth) * scale);
                        aty = (int) (((float) bitmapHeight2) * scale);
                        sideImage.setAlpha(alpha);
                        sideImage.setImageCoords((-width) / 2, (-aty) / 2, width, aty);
                        sideImage.draw(canvas2);
                        canvas.restore();
                    }
                    r1.groupedPhotosListView.setMoveProgress(-alpha);
                    canvas.save();
                    canvas2.translate(translateX, currentTranslationY / currentScale);
                    canvas2.translate(((((float) canvas.getWidth()) * (r1.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f))) / 2.0f, (-currentTranslationY) / currentScale);
                    r1.photoProgressViews[1].setScale(1.0f - scaleDiff);
                    r1.photoProgressViews[1].setAlpha(alpha);
                    r1.photoProgressViews[1].onDraw(canvas2);
                    canvas.restore();
                }
                aty = currentTranslationX;
                alpha = 0.0f;
                float alpha3 = 1.0f;
                if (!r1.zoomAnimation && aty > r1.maxX && r1.currentEditMode == 0) {
                    alpha3 = Math.min(1.0f, (aty - r1.maxX) / ((float) canvas.getWidth()));
                    alpha = alpha3 * 0.3f;
                    alpha3 = 1.0f - alpha3;
                    aty = r1.maxX;
                }
                boolean drawTextureView = r1.aspectRatioFrameLayout != null && r1.aspectRatioFrameLayout.getVisibility() == 0;
                if (r1.centerImage.hasBitmapImage()) {
                    int i;
                    canvas.save();
                    canvas2.translate((float) ((getContainerViewWidth() / 2) + getAdditionX()), (float) ((getContainerViewHeight() / 2) + getAdditionY()));
                    canvas2.translate(aty, currentTranslationY);
                    canvas2.scale(currentScale - alpha, currentScale - alpha);
                    if (r1.currentEditMode == 1) {
                        r1.photoCropView.setBitmapParams(currentScale, aty, currentTranslationY);
                    }
                    int bitmapWidth2 = r1.centerImage.getBitmapWidth();
                    bitmapHeight = r1.centerImage.getBitmapHeight();
                    if (drawTextureView && r1.textureUploaded && Math.abs((((float) bitmapWidth2) / ((float) bitmapHeight)) - (((float) r1.videoTextureView.getMeasuredWidth()) / ((float) r1.videoTextureView.getMeasuredHeight()))) > 0.01f) {
                        bitmapWidth2 = r1.videoTextureView.getMeasuredWidth();
                        bitmapHeight = r1.videoTextureView.getMeasuredHeight();
                    }
                    tx = ((float) getContainerViewWidth()) / ((float) bitmapWidth2);
                    ty = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    scale = tx > ty ? ty : tx;
                    bitmapWidth = (int) (((float) bitmapWidth2) * scale);
                    bitmapWidth2 = (int) (((float) bitmapHeight) * scale);
                    if (drawTextureView) {
                        if (r1.textureUploaded != 0 && r1.videoCrossfadeStarted) {
                            if (r1.videoCrossfadeAlpha == 1.0f) {
                                float f2 = ty;
                                float f3 = scale;
                                if (drawTextureView) {
                                    i = bitmapWidth;
                                    f = currentTranslationX;
                                } else {
                                    if (r1.videoCrossfadeStarted && r1.textureUploaded) {
                                        r1.videoCrossfadeStarted = true;
                                        r1.videoCrossfadeAlpha = 0.0f;
                                        f = currentTranslationX;
                                        r1.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                                    } else {
                                        f = currentTranslationX;
                                    }
                                    canvas2.translate((float) ((-bitmapWidth) / 2), (float) ((-bitmapWidth2) / 2));
                                    r1.videoTextureView.setAlpha(r1.videoCrossfadeAlpha * alpha3);
                                    r1.aspectRatioFrameLayout.draw(canvas2);
                                    if (r1.videoCrossfadeStarted || r1.videoCrossfadeAlpha >= 1.0f) {
                                        i = bitmapWidth;
                                    } else {
                                        currentTranslationX = System.currentTimeMillis();
                                        bitmapWidth = currentTranslationX - r1.videoCrossfadeAlphaLastTime;
                                        r1.videoCrossfadeAlphaLastTime = currentTranslationX;
                                        r1.videoCrossfadeAlpha += ((float) bitmapWidth) / 200.0f;
                                        r1.containerView.invalidate();
                                        if (r1.videoCrossfadeAlpha > 1.0f) {
                                            r1.videoCrossfadeAlpha = 1.0f;
                                        }
                                    }
                                }
                                canvas.restore();
                            }
                        }
                    }
                    r1.centerImage.setAlpha(alpha3);
                    r1.centerImage.setImageCoords((-bitmapWidth) / 2, (-bitmapWidth2) / 2, bitmapWidth, bitmapWidth2);
                    r1.centerImage.draw(canvas2);
                    if (drawTextureView) {
                        i = bitmapWidth;
                        f = currentTranslationX;
                    } else {
                        if (r1.videoCrossfadeStarted) {
                        }
                        f = currentTranslationX;
                        canvas2.translate((float) ((-bitmapWidth) / 2), (float) ((-bitmapWidth2) / 2));
                        r1.videoTextureView.setAlpha(r1.videoCrossfadeAlpha * alpha3);
                        r1.aspectRatioFrameLayout.draw(canvas2);
                        if (r1.videoCrossfadeStarted) {
                        }
                        i = bitmapWidth;
                    }
                    canvas.restore();
                } else {
                    f = currentTranslationX;
                }
                boolean drawProgress = r1.isCurrentVideo ? r1.progressView.getVisibility() != 0 && (r1.videoPlayer == null || !r1.videoPlayer.isPlaying()) : (drawTextureView || r1.videoPlayerControlFrameLayout.getVisibility() == 0) ? false : true;
                if (drawProgress) {
                    canvas.save();
                    canvas2.translate(aty, currentTranslationY / currentScale);
                    r1.photoProgressViews[0].setScale(1.0f - alpha);
                    r1.photoProgressViews[0].setAlpha(alpha3);
                    r1.photoProgressViews[0].onDraw(canvas2);
                    canvas.restore();
                }
                if (!r1.pipAnimationInProgress && (r1.miniProgressView.getVisibility() == 0 || r1.miniProgressAnimator != null)) {
                    canvas.save();
                    canvas2.translate(((float) r1.miniProgressView.getLeft()) + aty, ((float) r1.miniProgressView.getTop()) + (currentTranslationY / currentScale));
                    r1.miniProgressView.draw(canvas2);
                    canvas.restore();
                }
                float f4;
                boolean z;
                boolean z2;
                if (sideImage == r1.leftImage) {
                    if (sideImage.hasBitmapImage()) {
                        canvas.save();
                        canvas2.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                        canvas2.translate(((-((((float) canvas.getWidth()) * (r1.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f) + f, 0.0f);
                        bitmapHeight = sideImage.getBitmapWidth();
                        bitmapWidth = sideImage.getBitmapHeight();
                        ty = ((float) getContainerViewWidth()) / ((float) bitmapHeight);
                        currentTranslationX = ((float) getContainerViewHeight()) / ((float) bitmapWidth);
                        scale = ty > currentTranslationX ? currentTranslationX : ty;
                        aty = (int) (((float) bitmapHeight) * scale);
                        alpha = (int) (((float) bitmapWidth) * scale);
                        sideImage.setAlpha(true);
                        sideImage.setImageCoords((-aty) / 2, (-alpha) / 2, aty, alpha);
                        sideImage.draw(canvas2);
                        canvas.restore();
                    } else {
                        f4 = alpha;
                        z = drawTextureView;
                        z2 = drawProgress;
                    }
                    r1.groupedPhotosListView.setMoveProgress(1.0f - alpha3);
                    canvas.save();
                    canvas2.translate(f, currentTranslationY / currentScale);
                    canvas2.translate((-((((float) canvas.getWidth()) * (r1.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f, (-currentTranslationY) / currentScale);
                    r1.photoProgressViews[2].setScale(1.0f);
                    r1.photoProgressViews[2].setAlpha(1.0f);
                    r1.photoProgressViews[2].onDraw(canvas2);
                    canvas.restore();
                } else {
                    f4 = alpha;
                    z = drawTextureView;
                    z2 = drawProgress;
                    currentTranslationX = f;
                }
                if (r1.waitingForDraw != 0) {
                    r1.waitingForDraw--;
                    if (r1.waitingForDraw == 0) {
                        if (r1.textureImageView != null) {
                            try {
                                r1.currentBitmap = Bitmaps.createBitmap(r1.videoTextureView.getWidth(), r1.videoTextureView.getHeight(), Config.ARGB_8888);
                                r1.changedTextureView.getBitmap(r1.currentBitmap);
                            } catch (Throwable th) {
                                Throwable e = th;
                                if (r1.currentBitmap != null) {
                                    r1.currentBitmap.recycle();
                                    r1.currentBitmap = null;
                                }
                                FileLog.m3e(e);
                            }
                            if (r1.currentBitmap != null) {
                                r1.textureImageView.setVisibility(0);
                                r1.textureImageView.setImageBitmap(r1.currentBitmap);
                            } else {
                                r1.textureImageView.setImageDrawable(null);
                            }
                        }
                        r1.pipVideoView.close();
                        r1.pipVideoView = null;
                    } else {
                        r1.containerView.invalidate();
                    }
                }
            }
        }
    }

    private void onActionClick(boolean download) {
        if ((this.currentMessageObject != null || this.currentBotInlineResult != null) && this.currentFileNames[0] != null) {
            Uri uri = null;
            File file = null;
            this.isStreaming = false;
            StringBuilder stringBuilder;
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
                        if (SharedConfig.streamMedia && ((int) this.currentMessageObject.getDialogId()) != 0 && this.currentMessageObject.isVideo() && this.currentMessageObject.canStreamVideo()) {
                            try {
                                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
                                Document document = this.currentMessageObject.getDocument();
                                String params = new StringBuilder();
                                params.append("?account=");
                                params.append(this.currentMessageObject.currentAccount);
                                params.append("&id=");
                                params.append(document.id);
                                params.append("&hash=");
                                params.append(document.access_hash);
                                params.append("&dc=");
                                params.append(document.dc_id);
                                params.append("&size=");
                                params.append(document.size);
                                params.append("&mime=");
                                params.append(URLEncoder.encode(document.mime_type, C0539C.UTF8_NAME));
                                params.append("&name=");
                                params.append(URLEncoder.encode(FileLoader.getDocumentFileName(document), C0539C.UTF8_NAME));
                                params = params.toString();
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("tg://");
                                stringBuilder.append(this.currentMessageObject.getFileName());
                                stringBuilder.append(params);
                                uri = Uri.parse(stringBuilder.toString());
                                this.isStreaming = true;
                                checkProgress(0, false);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            } else if (this.currentBotInlineResult != null) {
                if (this.currentBotInlineResult.document != null) {
                    file = FileLoader.getPathToAttach(this.currentBotInlineResult.document);
                    if (!file.exists()) {
                        file = null;
                    }
                } else if (this.currentBotInlineResult.content instanceof TL_webDocument) {
                    File directory = FileLoader.getDirectory(4);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(Utilities.MD5(this.currentBotInlineResult.content.url));
                    stringBuilder.append(".");
                    stringBuilder.append(ImageLoader.getHttpUrlExtension(this.currentBotInlineResult.content.url, "mp4"));
                    file = new File(directory, stringBuilder.toString());
                    if (!file.exists()) {
                        file = null;
                    }
                }
            }
            if (file != null && uri == null) {
                uri = Uri.fromFile(file);
            }
            if (uri != null) {
                preparePlayer(uri, true, false);
            } else if (download) {
                if (this.currentMessageObject != null) {
                    if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
                    } else {
                        FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
                    }
                } else if (this.currentBotInlineResult != null) {
                    if (this.currentBotInlineResult.document != null) {
                        if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentBotInlineResult.document);
                        } else {
                            FileLoader.getInstance(this.currentAccount).loadFile(this.currentBotInlineResult.document, true, 0);
                        }
                    } else if (this.currentBotInlineResult.content instanceof TL_webDocument) {
                        if (ImageLoader.getInstance().isLoadingHttpFile(this.currentBotInlineResult.content.url)) {
                            ImageLoader.getInstance().cancelLoadHttpFile(this.currentBotInlineResult.content.url);
                        } else {
                            ImageLoader.getInstance().loadHttpFile(this.currentBotInlineResult.content.url, "mp4", this.currentAccount);
                        }
                    }
                }
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
        if (this.discardTap) {
            return false;
        }
        float x;
        if (this.containerView.getTag() != null) {
            boolean drawTextureView = this.aspectRatioFrameLayout != null && this.aspectRatioFrameLayout.getVisibility() == 0;
            x = e.getX();
            float y = e.getY();
            if (!(this.photoProgressViews[0] == null || this.containerView == null || drawTextureView)) {
                int state = this.photoProgressViews[0].backgroundState;
                if (state > 0 && state <= 3 && x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                    onActionClick(true);
                    checkProgress(0, true);
                    return true;
                }
            }
            toggleActionBar(this.isActionBarVisible ^ true, true);
        } else if (this.sendPhotoType == 0) {
            if (this.isCurrentVideo) {
                this.videoPlayButton.callOnClick();
            } else {
                this.checkImageView.performClick();
            }
        } else if (this.currentBotInlineResult != null && (this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
            int state2 = this.photoProgressViews[0].backgroundState;
            if (state2 > 0 && state2 <= 3) {
                float x2 = e.getX();
                x = e.getY();
                if (x2 >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x2 <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && x >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                    onActionClick(true);
                    checkProgress(0, true);
                    return true;
                }
            }
        } else if (this.sendPhotoType == 2 && this.isCurrentVideo) {
            this.videoPlayButton.callOnClick();
        }
        return true;
    }

    public boolean onDoubleTap(MotionEvent e) {
        if (this.videoPlayer != null && r0.videoPlayerControlFrameLayout.getVisibility() == 0) {
            long current = r0.videoPlayer.getCurrentPosition();
            long total = r0.videoPlayer.getDuration();
            if (total >= 0 && current >= 0 && total != C0539C.TIME_UNSET && current != C0539C.TIME_UNSET) {
                long current2;
                long current3;
                int width = getContainerViewWidth();
                float x = e.getX();
                long old = current;
                if (x >= ((float) ((width / 3) * 2))) {
                    current2 = current + 10000;
                } else if (x < ((float) (width / 3))) {
                    current2 = current - 10000;
                } else {
                    current2 = current;
                    if (old != current2) {
                        if (current2 > total) {
                            current2 = total;
                        } else if (current2 < 0) {
                            current2 = 0;
                        }
                        current3 = current2;
                        r0.videoPlayer.seekTo(current3);
                        r0.containerView.invalidate();
                        r0.videoPlayerSeekbar.setProgress(((float) current3) / ((float) total));
                        r0.videoPlayerControlFrameLayout.invalidate();
                        return true;
                    }
                }
                if (old != current2) {
                    if (current2 > total) {
                        current2 = total;
                    } else if (current2 < 0) {
                        current2 = 0;
                    }
                    current3 = current2;
                    r0.videoPlayer.seekTo(current3);
                    r0.containerView.invalidate();
                    r0.videoPlayerSeekbar.setProgress(((float) current3) / ((float) total));
                    r0.videoPlayerControlFrameLayout.invalidate();
                    return true;
                }
            }
        }
        if (r0.canZoom) {
            if (r0.scale == 1.0f) {
                if (r0.translationY == 0.0f) {
                    if (r0.translationX != 0.0f) {
                    }
                }
            }
            if (r0.animationStartTime == 0) {
                if (r0.animationInProgress == 0) {
                    if (r0.scale == 1.0f) {
                        float atx = (e.getX() - ((float) (getContainerViewWidth() / 2))) - (((e.getX() - ((float) (getContainerViewWidth() / 2))) - r0.translationX) * (3.0f / r0.scale));
                        float aty = (e.getY() - ((float) (getContainerViewHeight() / 2))) - (((e.getY() - ((float) (getContainerViewHeight() / 2))) - r0.translationY) * (3.0f / r0.scale));
                        updateMinMax(3.0f);
                        if (atx < r0.minX) {
                            atx = r0.minX;
                        } else if (atx > r0.maxX) {
                            atx = r0.maxX;
                        }
                        if (aty < r0.minY) {
                            aty = r0.minY;
                        } else if (aty > r0.maxY) {
                            aty = r0.maxY;
                        }
                        animateTo(3.0f, atx, aty, true);
                    } else {
                        animateTo(1.0f, 0.0f, 0.0f, true);
                    }
                    r0.doubleTap = true;
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    public void updateMuteButton() {
        if (this.videoPlayer != null) {
            this.videoPlayer.setMute(this.muteVideo);
        }
        if (this.videoHasAudio) {
            this.muteItem.setEnabled(true);
            this.muteItem.setClickable(true);
            this.muteItem.setAlpha(1.0f);
            if (this.muteVideo) {
                this.actionBar.setSubtitle(null);
                this.muteItem.setImageResource(R.drawable.volume_off);
                this.muteItem.setColorFilter(new PorterDuffColorFilter(-12734994, Mode.MULTIPLY));
                if (this.compressItem.getTag() != null) {
                    this.compressItem.setClickable(false);
                    this.compressItem.setAlpha(0.5f);
                    this.compressItem.setEnabled(false);
                }
                this.videoTimelineView.setMaxProgressDiff(30000.0f / this.videoDuration);
                return;
            }
            this.muteItem.setColorFilter(null);
            this.actionBar.setSubtitle(this.currentSubtitle);
            this.muteItem.setImageResource(R.drawable.volume_on);
            if (this.compressItem.getTag() != null) {
                this.compressItem.setClickable(true);
                this.compressItem.setAlpha(1.0f);
                this.compressItem.setEnabled(true);
            }
            this.videoTimelineView.setMaxProgressDiff(1.0f);
            return;
        }
        this.muteItem.setEnabled(false);
        this.muteItem.setClickable(false);
        this.muteItem.setAlpha(0.5f);
    }

    private void didChangedCompressionLevel(boolean request) {
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("compress_video2", this.selectedCompression);
        editor.commit();
        updateWidthHeightBitrateForCompression();
        updateVideoInfo();
        if (request) {
            requestVideoPreview(1);
        }
    }

    private void updateVideoInfo() {
        if (this.actionBar != null) {
            CharSequence charSequence = null;
            if (this.compressionsCount == 0) {
                this.actionBar.setSubtitle(null);
                return;
            }
            int i;
            int i2;
            String videoDimension;
            int seconds;
            String videoTimeSize;
            ActionBar actionBar;
            if (this.selectedCompression == 0) {
                this.compressItem.setImageResource(R.drawable.video_240);
            } else if (this.selectedCompression == 1) {
                this.compressItem.setImageResource(R.drawable.video_360);
            } else if (this.selectedCompression == 2) {
                this.compressItem.setImageResource(R.drawable.video_480);
            } else if (this.selectedCompression == 3) {
                this.compressItem.setImageResource(R.drawable.video_720);
            } else if (this.selectedCompression == 4) {
                this.compressItem.setImageResource(R.drawable.video_1080);
            }
            this.estimatedDuration = (long) Math.ceil((double) ((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
            if (this.compressItem.getTag() != null) {
                if (this.selectedCompression != this.compressionsCount - 1) {
                    if (this.rotationValue != 90) {
                        if (this.rotationValue != 270) {
                            i = this.resultWidth;
                            if (this.rotationValue != 90) {
                                if (this.rotationValue == 270) {
                                    i2 = this.resultHeight;
                                    this.estimatedSize = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * (((float) this.estimatedDuration) / this.videoDuration));
                                    this.estimatedSize += (this.estimatedSize / 32768) * 16;
                                    if (this.videoTimelineView.getLeftProgress() != 0.0f) {
                                        this.startTime = -1;
                                    } else {
                                        this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
                                    }
                                    if (this.videoTimelineView.getRightProgress() != 1.0f) {
                                        this.endTime = -1;
                                    } else {
                                        this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
                                    }
                                    videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                                    seconds = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                                    videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                                    this.currentSubtitle = String.format("%s, %s", new Object[]{videoDimension, videoTimeSize});
                                    actionBar = this.actionBar;
                                    if (!this.muteVideo) {
                                        charSequence = this.currentSubtitle;
                                    }
                                    actionBar.setSubtitle(charSequence);
                                }
                            }
                            i2 = this.resultWidth;
                            this.estimatedSize = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * (((float) this.estimatedDuration) / this.videoDuration));
                            this.estimatedSize += (this.estimatedSize / 32768) * 16;
                            if (this.videoTimelineView.getLeftProgress() != 0.0f) {
                                this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
                            } else {
                                this.startTime = -1;
                            }
                            if (this.videoTimelineView.getRightProgress() != 1.0f) {
                                this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
                            } else {
                                this.endTime = -1;
                            }
                            videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                            seconds = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                            videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                            this.currentSubtitle = String.format("%s, %s", new Object[]{videoDimension, videoTimeSize});
                            actionBar = this.actionBar;
                            if (!this.muteVideo) {
                                charSequence = this.currentSubtitle;
                            }
                            actionBar.setSubtitle(charSequence);
                        }
                    }
                    i = this.resultHeight;
                    if (this.rotationValue != 90) {
                        if (this.rotationValue == 270) {
                            i2 = this.resultHeight;
                            this.estimatedSize = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * (((float) this.estimatedDuration) / this.videoDuration));
                            this.estimatedSize += (this.estimatedSize / 32768) * 16;
                            if (this.videoTimelineView.getLeftProgress() != 0.0f) {
                                this.startTime = -1;
                            } else {
                                this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
                            }
                            if (this.videoTimelineView.getRightProgress() != 1.0f) {
                                this.endTime = -1;
                            } else {
                                this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
                            }
                            videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                            seconds = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                            videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                            this.currentSubtitle = String.format("%s, %s", new Object[]{videoDimension, videoTimeSize});
                            actionBar = this.actionBar;
                            if (!this.muteVideo) {
                                charSequence = this.currentSubtitle;
                            }
                            actionBar.setSubtitle(charSequence);
                        }
                    }
                    i2 = this.resultWidth;
                    this.estimatedSize = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * (((float) this.estimatedDuration) / this.videoDuration));
                    this.estimatedSize += (this.estimatedSize / 32768) * 16;
                    if (this.videoTimelineView.getLeftProgress() != 0.0f) {
                        this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
                    } else {
                        this.startTime = -1;
                    }
                    if (this.videoTimelineView.getRightProgress() != 1.0f) {
                        this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
                    } else {
                        this.endTime = -1;
                    }
                    videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                    seconds = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                    videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                    this.currentSubtitle = String.format("%s, %s", new Object[]{videoDimension, videoTimeSize});
                    actionBar = this.actionBar;
                    if (!this.muteVideo) {
                        charSequence = this.currentSubtitle;
                    }
                    actionBar.setSubtitle(charSequence);
                }
            }
            if (this.rotationValue != 90) {
                if (this.rotationValue != 270) {
                    i = this.originalWidth;
                    if (this.rotationValue != 90) {
                        if (this.rotationValue == 270) {
                            i2 = this.originalHeight;
                            this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.estimatedDuration) / this.videoDuration));
                            if (this.videoTimelineView.getLeftProgress() != 0.0f) {
                                this.startTime = -1;
                            } else {
                                this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
                            }
                            if (this.videoTimelineView.getRightProgress() != 1.0f) {
                                this.endTime = -1;
                            } else {
                                this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
                            }
                            videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                            seconds = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                            videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                            this.currentSubtitle = String.format("%s, %s", new Object[]{videoDimension, videoTimeSize});
                            actionBar = this.actionBar;
                            if (!this.muteVideo) {
                                charSequence = this.currentSubtitle;
                            }
                            actionBar.setSubtitle(charSequence);
                        }
                    }
                    i2 = this.originalWidth;
                    this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.estimatedDuration) / this.videoDuration));
                    if (this.videoTimelineView.getLeftProgress() != 0.0f) {
                        this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
                    } else {
                        this.startTime = -1;
                    }
                    if (this.videoTimelineView.getRightProgress() != 1.0f) {
                        this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
                    } else {
                        this.endTime = -1;
                    }
                    videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                    seconds = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                    videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                    this.currentSubtitle = String.format("%s, %s", new Object[]{videoDimension, videoTimeSize});
                    actionBar = this.actionBar;
                    if (!this.muteVideo) {
                        charSequence = this.currentSubtitle;
                    }
                    actionBar.setSubtitle(charSequence);
                }
            }
            i = this.originalHeight;
            if (this.rotationValue != 90) {
                if (this.rotationValue == 270) {
                    i2 = this.originalHeight;
                    this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.estimatedDuration) / this.videoDuration));
                    if (this.videoTimelineView.getLeftProgress() != 0.0f) {
                        this.startTime = -1;
                    } else {
                        this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
                    }
                    if (this.videoTimelineView.getRightProgress() != 1.0f) {
                        this.endTime = -1;
                    } else {
                        this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
                    }
                    videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                    seconds = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                    videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                    this.currentSubtitle = String.format("%s, %s", new Object[]{videoDimension, videoTimeSize});
                    actionBar = this.actionBar;
                    if (!this.muteVideo) {
                        charSequence = this.currentSubtitle;
                    }
                    actionBar.setSubtitle(charSequence);
                }
            }
            i2 = this.originalWidth;
            this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.estimatedDuration) / this.videoDuration));
            if (this.videoTimelineView.getLeftProgress() != 0.0f) {
                this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
            } else {
                this.startTime = -1;
            }
            if (this.videoTimelineView.getRightProgress() != 1.0f) {
                this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
            } else {
                this.endTime = -1;
            }
            videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
            seconds = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
            videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
            this.currentSubtitle = String.format("%s, %s", new Object[]{videoDimension, videoTimeSize});
            actionBar = this.actionBar;
            if (!this.muteVideo) {
                charSequence = this.currentSubtitle;
            }
            actionBar.setSubtitle(charSequence);
        }
    }

    private void requestVideoPreview(int request) {
        if (this.videoPreviewMessageObject != null) {
            MediaController.getInstance().cancelVideoConvert(this.videoPreviewMessageObject);
        }
        boolean wasRequestingPreview = this.requestingPreview && !this.tryStartRequestPreviewOnFinish;
        this.requestingPreview = false;
        this.loadInitialVideo = false;
        this.progressView.setVisibility(4);
        if (request != 1) {
            this.tryStartRequestPreviewOnFinish = false;
            if (request == 2) {
                preparePlayer(this.currentPlayingVideoFile, false, false);
            }
        } else if (this.selectedCompression == this.compressionsCount - 1) {
            this.tryStartRequestPreviewOnFinish = false;
            if (wasRequestingPreview) {
                this.progressView.setVisibility(0);
                this.loadInitialVideo = true;
            } else {
                preparePlayer(this.currentPlayingVideoFile, false, false);
            }
        } else {
            this.requestingPreview = true;
            releasePlayer();
            if (this.videoPreviewMessageObject == null) {
                TL_message message = new TL_message();
                message.id = 0;
                message.message = TtmlNode.ANONYMOUS_REGION_ID;
                message.media = new TL_messageMediaEmpty();
                message.action = new TL_messageActionEmpty();
                this.videoPreviewMessageObject = new MessageObject(UserConfig.selectedAccount, message, false);
                this.videoPreviewMessageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), "video_preview.mp4").getAbsolutePath();
                this.videoPreviewMessageObject.videoEditedInfo = new VideoEditedInfo();
                this.videoPreviewMessageObject.videoEditedInfo.rotationValue = this.rotationValue;
                this.videoPreviewMessageObject.videoEditedInfo.originalWidth = this.originalWidth;
                this.videoPreviewMessageObject.videoEditedInfo.originalHeight = this.originalHeight;
                this.videoPreviewMessageObject.videoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
            }
            VideoEditedInfo videoEditedInfo = this.videoPreviewMessageObject.videoEditedInfo;
            long j = this.startTime;
            videoEditedInfo.startTime = j;
            long start = j;
            VideoEditedInfo videoEditedInfo2 = this.videoPreviewMessageObject.videoEditedInfo;
            long j2 = this.endTime;
            videoEditedInfo2.endTime = j2;
            long end = j2;
            if (start == -1) {
                start = 0;
            }
            if (end == -1) {
                end = (long) (this.videoDuration * 1000.0f);
            }
            if (end - start > 5000000) {
                this.videoPreviewMessageObject.videoEditedInfo.endTime = start + 5000000;
            }
            this.videoPreviewMessageObject.videoEditedInfo.bitrate = this.bitrate;
            this.videoPreviewMessageObject.videoEditedInfo.resultWidth = this.resultWidth;
            this.videoPreviewMessageObject.videoEditedInfo.resultHeight = this.resultHeight;
            if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {
                this.tryStartRequestPreviewOnFinish = true;
            }
            this.requestingPreview = true;
            this.progressView.setVisibility(0);
        }
        this.containerView.invalidate();
    }

    private void updateWidthHeightBitrateForCompression() {
        if (this.compressionsCount > 0) {
            if (this.selectedCompression >= this.compressionsCount) {
                this.selectedCompression = this.compressionsCount - 1;
            }
            if (this.selectedCompression != this.compressionsCount - 1) {
                float maxSize;
                int targetBitrate;
                switch (this.selectedCompression) {
                    case 0:
                        maxSize = 426.0f;
                        targetBitrate = 400000;
                        break;
                    case 1:
                        maxSize = 640.0f;
                        targetBitrate = 900000;
                        break;
                    case 2:
                        maxSize = 854.0f;
                        targetBitrate = 1100000;
                        break;
                    default:
                        targetBitrate = 2500000;
                        maxSize = 1280.0f;
                        break;
                }
                float scale = maxSize / ((float) (this.originalWidth > this.originalHeight ? this.originalWidth : this.originalHeight));
                this.resultWidth = Math.round((((float) this.originalWidth) * scale) / 2.0f) * 2;
                this.resultHeight = Math.round((((float) this.originalHeight) * scale) / 2.0f) * 2;
                if (this.bitrate != 0) {
                    this.bitrate = Math.min(targetBitrate, (int) (((float) this.originalBitrate) / scale));
                    this.videoFramesSize = (long) ((((float) (this.bitrate / 8)) * this.videoDuration) / 1000.0f);
                }
            }
        }
    }

    private void showQualityView(final boolean show) {
        if (show) {
            this.previousCompression = this.selectedCompression;
        }
        if (this.qualityChooseViewAnimation != null) {
            this.qualityChooseViewAnimation.cancel();
        }
        this.qualityChooseViewAnimation = new AnimatorSet();
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (show) {
            this.qualityChooseView.setTag(Integer.valueOf(1));
            animatorSet = this.qualityChooseViewAnimation;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(152.0f)});
            animatorArr[1] = ObjectAnimator.ofFloat(this.pickerViewSendButton, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(152.0f)});
            animatorArr[2] = ObjectAnimator.ofFloat(this.bottomLayout, "translationY", new float[]{(float) (-AndroidUtilities.dp(48.0f)), (float) AndroidUtilities.dp(104.0f)});
            animatorSet.playTogether(animatorArr);
        } else {
            this.qualityChooseView.setTag(null);
            animatorSet = this.qualityChooseViewAnimation;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.qualityChooseView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(166.0f)});
            animatorArr[1] = ObjectAnimator.ofFloat(this.qualityPicker, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(166.0f)});
            animatorArr[2] = ObjectAnimator.ofFloat(this.bottomLayout, "translationY", new float[]{(float) (-AndroidUtilities.dp(48.0f)), (float) AndroidUtilities.dp(118.0f)});
            animatorSet.playTogether(animatorArr);
        }
        this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() {

            /* renamed from: org.telegram.ui.PhotoViewer$81$1 */
            class C16131 extends AnimatorListenerAdapter {
                C16131() {
                }

                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                        PhotoViewer.this.qualityChooseViewAnimation = null;
                    }
                }
            }

            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                    PhotoViewer.this.qualityChooseViewAnimation = new AnimatorSet();
                    AnimatorSet access$17600;
                    Animator[] animatorArr;
                    if (show) {
                        PhotoViewer.this.qualityChooseView.setVisibility(0);
                        PhotoViewer.this.qualityPicker.setVisibility(0);
                        access$17600 = PhotoViewer.this.qualityChooseViewAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.qualityChooseView, "translationY", new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(PhotoViewer.this.qualityPicker, "translationY", new float[]{0.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, "translationY", new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                        access$17600.playTogether(animatorArr);
                    } else {
                        PhotoViewer.this.qualityChooseView.setVisibility(4);
                        PhotoViewer.this.qualityPicker.setVisibility(4);
                        access$17600 = PhotoViewer.this.qualityChooseViewAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, "translationY", new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, "translationY", new float[]{0.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, "translationY", new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                        access$17600.playTogether(animatorArr);
                    }
                    PhotoViewer.this.qualityChooseViewAnimation.addListener(new C16131());
                    PhotoViewer.this.qualityChooseViewAnimation.setDuration(200);
                    PhotoViewer.this.qualityChooseViewAnimation.setInterpolator(new AccelerateInterpolator());
                    PhotoViewer.this.qualityChooseViewAnimation.start();
                }
            }

            public void onAnimationCancel(Animator animation) {
                PhotoViewer.this.qualityChooseViewAnimation = null;
            }
        });
        this.qualityChooseViewAnimation.setDuration(200);
        this.qualityChooseViewAnimation.setInterpolator(new DecelerateInterpolator());
        this.qualityChooseViewAnimation.start();
    }

    private void processOpenVideo(final String videoPath, boolean muted) {
        if (this.currentLoadingVideoRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.currentLoadingVideoRunnable);
            this.currentLoadingVideoRunnable = null;
        }
        this.videoPreviewMessageObject = null;
        setCompressItemEnabled(false, true);
        this.muteVideo = muted;
        this.videoTimelineView.setVideoPath(videoPath);
        this.compressionsCount = -1;
        this.rotationValue = 0;
        this.originalSize = new File(videoPath).length();
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        Runnable anonymousClass82 = new Runnable() {
            public void run() {
                Throwable e;
                boolean isAvc;
                IsoFile isoFile;
                List<Box> boxes;
                Throwable isAvc2;
                Throwable e2;
                final boolean isAvcFinal;
                if (PhotoViewer.this.currentLoadingVideoRunnable == r1) {
                    TrackHeaderBox trackHeaderBox;
                    TrackHeaderBox trackHeaderBox2;
                    boolean isAvc3 = true;
                    try {
                        IsoFile isoFile2 = new IsoFile(videoPath);
                        List<Box> boxes2 = Path.getPaths(isoFile2, "/moov/trak/");
                        if (Path.getPath(isoFile2, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") == null && BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("video hasn't mp4a atom");
                        }
                        if (Path.getPath(isoFile2, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("video hasn't avc1 atom");
                            }
                            isAvc3 = false;
                        }
                        try {
                            long j = 0;
                            PhotoViewer.this.audioFramesSize = 0;
                            PhotoViewer.this.videoFramesSize = 0;
                            trackHeaderBox = null;
                            TrackHeaderBox trackHeaderBox3 = null;
                            while (trackHeaderBox3 < boxes2.size()) {
                                try {
                                    if (PhotoViewer.this.currentLoadingVideoRunnable == r1) {
                                        long sampleSizes;
                                        TrackBox trackBox = (TrackBox) ((Box) boxes2.get(trackHeaderBox3));
                                        long trackBitrate = j;
                                        try {
                                            MediaBox mediaBox = trackBox.getMediaBox();
                                            MediaHeaderBox mediaHeaderBox = mediaBox.getMediaHeaderBox();
                                            long[] sizes = mediaBox.getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes();
                                            sampleSizes = 0;
                                            int a = 0;
                                            while (a < sizes.length) {
                                                try {
                                                    try {
                                                        if (PhotoViewer.this.currentLoadingVideoRunnable == r1) {
                                                            a++;
                                                            sampleSizes += sizes[a];
                                                        } else {
                                                            return;
                                                        }
                                                    } catch (Exception e3) {
                                                        e = e3;
                                                        isAvc = isAvc3;
                                                        isoFile = isoFile2;
                                                        boxes = boxes2;
                                                    }
                                                } catch (Throwable e4) {
                                                    isAvc = isAvc3;
                                                    isoFile = isoFile2;
                                                    boxes = boxes2;
                                                    isAvc2 = e4;
                                                }
                                            }
                                            isAvc = isAvc3;
                                            isoFile = isoFile2;
                                            MediaHeaderBox mediaHeaderBox2 = mediaHeaderBox;
                                            try {
                                                boxes = boxes2;
                                            } catch (Throwable e42) {
                                                boxes = boxes2;
                                                isAvc2 = e42;
                                                try {
                                                    FileLog.m3e(isAvc2);
                                                    if (PhotoViewer.this.currentLoadingVideoRunnable == r1) {
                                                        isAvc3 = trackBox.getTrackHeaderBox();
                                                        if (isAvc3.getWidth() != 0.0d) {
                                                        }
                                                        PhotoViewer.this.audioFramesSize = PhotoViewer.this.audioFramesSize + sampleSizes;
                                                        trackHeaderBox3++;
                                                        isoFile2 = isoFile;
                                                        isAvc3 = isAvc;
                                                        boxes2 = boxes;
                                                        j = 0;
                                                    } else {
                                                        return;
                                                    }
                                                } catch (Exception e5) {
                                                    e42 = e5;
                                                }
                                            }
                                            try {
                                                PhotoViewer.this.videoDuration = ((float) mediaHeaderBox2.getDuration()) / ((float) mediaHeaderBox2.getTimescale());
                                                trackBitrate = (long) ((int) (((float) (true * sampleSizes)) / PhotoViewer.this.videoDuration));
                                            } catch (Exception e6) {
                                                e42 = e6;
                                                isAvc2 = e42;
                                                FileLog.m3e(isAvc2);
                                                if (PhotoViewer.this.currentLoadingVideoRunnable == r1) {
                                                    isAvc3 = trackBox.getTrackHeaderBox();
                                                    if (isAvc3.getWidth() != 0.0d) {
                                                    }
                                                    PhotoViewer.this.audioFramesSize = PhotoViewer.this.audioFramesSize + sampleSizes;
                                                    trackHeaderBox3++;
                                                    isoFile2 = isoFile;
                                                    isAvc3 = isAvc;
                                                    boxes2 = boxes;
                                                    j = 0;
                                                } else {
                                                    return;
                                                }
                                            }
                                        } catch (Throwable e422) {
                                            isAvc = isAvc3;
                                            isoFile = isoFile2;
                                            boxes = boxes2;
                                            isAvc2 = e422;
                                            sampleSizes = 0;
                                            FileLog.m3e(isAvc2);
                                            if (PhotoViewer.this.currentLoadingVideoRunnable == r1) {
                                                isAvc3 = trackBox.getTrackHeaderBox();
                                                if (isAvc3.getWidth() != 0.0d) {
                                                }
                                                PhotoViewer.this.audioFramesSize = PhotoViewer.this.audioFramesSize + sampleSizes;
                                                trackHeaderBox3++;
                                                isoFile2 = isoFile;
                                                isAvc3 = isAvc;
                                                boxes2 = boxes;
                                                j = 0;
                                            } else {
                                                return;
                                            }
                                        }
                                        if (PhotoViewer.this.currentLoadingVideoRunnable == r1) {
                                            isAvc3 = trackBox.getTrackHeaderBox();
                                            if (isAvc3.getWidth() != 0.0d || isAvc3.getHeight() == 0.0d) {
                                                PhotoViewer.this.audioFramesSize = PhotoViewer.this.audioFramesSize + sampleSizes;
                                            } else if (trackHeaderBox == null || trackHeaderBox.getWidth() < isAvc3.getWidth() || trackHeaderBox.getHeight() < isAvc3.getHeight()) {
                                                trackHeaderBox2 = isAvc3;
                                                try {
                                                    PhotoViewer.this.originalBitrate = PhotoViewer.this.bitrate = (int) ((trackBitrate / 100000) * 100000);
                                                    if (PhotoViewer.this.bitrate > 900000) {
                                                        PhotoViewer.this.bitrate = 900000;
                                                    }
                                                    PhotoViewer.this.videoFramesSize = PhotoViewer.this.videoFramesSize + sampleSizes;
                                                    trackHeaderBox = trackHeaderBox2;
                                                } catch (Throwable e4222) {
                                                    e2 = e4222;
                                                    trackHeaderBox = trackHeaderBox2;
                                                }
                                            }
                                            trackHeaderBox3++;
                                            isoFile2 = isoFile;
                                            isAvc3 = isAvc;
                                            boxes2 = boxes;
                                            j = 0;
                                        } else {
                                            return;
                                        }
                                    }
                                    return;
                                } catch (Exception e7) {
                                    e4222 = e7;
                                    isAvc = isAvc3;
                                }
                            }
                            isAvc = isAvc3;
                        } catch (Exception e8) {
                            e4222 = e8;
                            isAvc = isAvc3;
                            trackHeaderBox = null;
                            e2 = e4222;
                            FileLog.m3e(e2);
                            isAvc3 = false;
                            if (trackHeaderBox == null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("video hasn't trackHeaderBox atom");
                                }
                                isAvc3 = false;
                            }
                            isAvcFinal = isAvc3;
                            trackHeaderBox2 = trackHeaderBox;
                            if (PhotoViewer.this.currentLoadingVideoRunnable != r1) {
                                PhotoViewer.this.currentLoadingVideoRunnable = null;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (PhotoViewer.this.parentActivity != null) {
                                            PhotoViewer.this.videoHasAudio = isAvcFinal;
                                            if (isAvcFinal) {
                                                StringBuilder stringBuilder;
                                                MediaCodecInfo codecInfo;
                                                String name;
                                                StringBuilder stringBuilder2;
                                                Matrix matrix = trackHeaderBox2.getMatrix();
                                                if (matrix.equals(Matrix.ROTATE_90)) {
                                                    PhotoViewer.this.rotationValue = 90;
                                                } else if (matrix.equals(Matrix.ROTATE_180)) {
                                                    PhotoViewer.this.rotationValue = 180;
                                                } else if (matrix.equals(Matrix.ROTATE_270)) {
                                                    PhotoViewer.this.rotationValue = 270;
                                                } else {
                                                    PhotoViewer.this.rotationValue = 0;
                                                }
                                                PhotoViewer.this.resultWidth = PhotoViewer.this.originalWidth = (int) trackHeaderBox2.getWidth();
                                                PhotoViewer.this.resultHeight = PhotoViewer.this.originalHeight = (int) trackHeaderBox2.getHeight();
                                                PhotoViewer.this.videoDuration = PhotoViewer.this.videoDuration * 1000.0f;
                                                PhotoViewer.this.selectedCompression = MessagesController.getGlobalMainSettings().getInt("compress_video2", 1);
                                                if (PhotoViewer.this.originalWidth <= 1280) {
                                                    if (PhotoViewer.this.originalHeight <= 1280) {
                                                        if (PhotoViewer.this.originalWidth <= 854) {
                                                            if (PhotoViewer.this.originalHeight <= 854) {
                                                                if (PhotoViewer.this.originalWidth <= 640) {
                                                                    if (PhotoViewer.this.originalHeight <= 640) {
                                                                        if (PhotoViewer.this.originalWidth <= 480) {
                                                                            if (PhotoViewer.this.originalHeight <= 480) {
                                                                                PhotoViewer.this.compressionsCount = 1;
                                                                                PhotoViewer.this.updateWidthHeightBitrateForCompression();
                                                                                PhotoViewer.this.setCompressItemEnabled(PhotoViewer.this.compressionsCount > 1, true);
                                                                                if (BuildVars.LOGS_ENABLED) {
                                                                                    stringBuilder = new StringBuilder();
                                                                                    stringBuilder.append("compressionsCount = ");
                                                                                    stringBuilder.append(PhotoViewer.this.compressionsCount);
                                                                                    stringBuilder.append(" w = ");
                                                                                    stringBuilder.append(PhotoViewer.this.originalWidth);
                                                                                    stringBuilder.append(" h = ");
                                                                                    stringBuilder.append(PhotoViewer.this.originalHeight);
                                                                                    FileLog.m0d(stringBuilder.toString());
                                                                                }
                                                                                if (VERSION.SDK_INT < 18 && PhotoViewer.this.compressItem.getTag() != null) {
                                                                                    codecInfo = MediaController.selectCodec("video/avc");
                                                                                    if (codecInfo == null) {
                                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                                            FileLog.m0d("no codec info for video/avc");
                                                                                        }
                                                                                        PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                                    } else {
                                                                                        name = codecInfo.getName();
                                                                                        if (!(name.equals("OMX.google.h264.encoder") || name.equals("OMX.ST.VFM.H264Enc") || name.equals("OMX.Exynos.avc.enc") || name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") || name.equals("OMX.MARVELL.VIDEO.H264ENCODER") || name.equals("OMX.k3.video.encoder.avc"))) {
                                                                                            if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                                                                if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
                                                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                                                        FileLog.m0d("no color format for video/avc");
                                                                                                    }
                                                                                                    PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                                            stringBuilder2 = new StringBuilder();
                                                                                            stringBuilder2.append("unsupported encoder = ");
                                                                                            stringBuilder2.append(name);
                                                                                            FileLog.m0d(stringBuilder2.toString());
                                                                                        }
                                                                                        PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                                    }
                                                                                }
                                                                                PhotoViewer.this.qualityChooseView.invalidate();
                                                                            }
                                                                        }
                                                                        PhotoViewer.this.compressionsCount = 2;
                                                                        PhotoViewer.this.updateWidthHeightBitrateForCompression();
                                                                        if (PhotoViewer.this.compressionsCount > 1) {
                                                                        }
                                                                        PhotoViewer.this.setCompressItemEnabled(PhotoViewer.this.compressionsCount > 1, true);
                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                            stringBuilder = new StringBuilder();
                                                                            stringBuilder.append("compressionsCount = ");
                                                                            stringBuilder.append(PhotoViewer.this.compressionsCount);
                                                                            stringBuilder.append(" w = ");
                                                                            stringBuilder.append(PhotoViewer.this.originalWidth);
                                                                            stringBuilder.append(" h = ");
                                                                            stringBuilder.append(PhotoViewer.this.originalHeight);
                                                                            FileLog.m0d(stringBuilder.toString());
                                                                        }
                                                                        codecInfo = MediaController.selectCodec("video/avc");
                                                                        if (codecInfo == null) {
                                                                            name = codecInfo.getName();
                                                                            if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                                                if (BuildVars.LOGS_ENABLED) {
                                                                                    stringBuilder2 = new StringBuilder();
                                                                                    stringBuilder2.append("unsupported encoder = ");
                                                                                    stringBuilder2.append(name);
                                                                                    FileLog.m0d(stringBuilder2.toString());
                                                                                }
                                                                                PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                            } else if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
                                                                                if (BuildVars.LOGS_ENABLED) {
                                                                                    FileLog.m0d("no color format for video/avc");
                                                                                }
                                                                                PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                            }
                                                                        } else {
                                                                            if (BuildVars.LOGS_ENABLED) {
                                                                                FileLog.m0d("no codec info for video/avc");
                                                                            }
                                                                            PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                        }
                                                                        PhotoViewer.this.qualityChooseView.invalidate();
                                                                    }
                                                                }
                                                                PhotoViewer.this.compressionsCount = 3;
                                                                PhotoViewer.this.updateWidthHeightBitrateForCompression();
                                                                if (PhotoViewer.this.compressionsCount > 1) {
                                                                }
                                                                PhotoViewer.this.setCompressItemEnabled(PhotoViewer.this.compressionsCount > 1, true);
                                                                if (BuildVars.LOGS_ENABLED) {
                                                                    stringBuilder = new StringBuilder();
                                                                    stringBuilder.append("compressionsCount = ");
                                                                    stringBuilder.append(PhotoViewer.this.compressionsCount);
                                                                    stringBuilder.append(" w = ");
                                                                    stringBuilder.append(PhotoViewer.this.originalWidth);
                                                                    stringBuilder.append(" h = ");
                                                                    stringBuilder.append(PhotoViewer.this.originalHeight);
                                                                    FileLog.m0d(stringBuilder.toString());
                                                                }
                                                                codecInfo = MediaController.selectCodec("video/avc");
                                                                if (codecInfo == null) {
                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                        FileLog.m0d("no codec info for video/avc");
                                                                    }
                                                                    PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                } else {
                                                                    name = codecInfo.getName();
                                                                    if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                            stringBuilder2 = new StringBuilder();
                                                                            stringBuilder2.append("unsupported encoder = ");
                                                                            stringBuilder2.append(name);
                                                                            FileLog.m0d(stringBuilder2.toString());
                                                                        }
                                                                        PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                    } else if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                            FileLog.m0d("no color format for video/avc");
                                                                        }
                                                                        PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                    }
                                                                }
                                                                PhotoViewer.this.qualityChooseView.invalidate();
                                                            }
                                                        }
                                                        PhotoViewer.this.compressionsCount = 4;
                                                        PhotoViewer.this.updateWidthHeightBitrateForCompression();
                                                        if (PhotoViewer.this.compressionsCount > 1) {
                                                        }
                                                        PhotoViewer.this.setCompressItemEnabled(PhotoViewer.this.compressionsCount > 1, true);
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append("compressionsCount = ");
                                                            stringBuilder.append(PhotoViewer.this.compressionsCount);
                                                            stringBuilder.append(" w = ");
                                                            stringBuilder.append(PhotoViewer.this.originalWidth);
                                                            stringBuilder.append(" h = ");
                                                            stringBuilder.append(PhotoViewer.this.originalHeight);
                                                            FileLog.m0d(stringBuilder.toString());
                                                        }
                                                        codecInfo = MediaController.selectCodec("video/avc");
                                                        if (codecInfo == null) {
                                                            name = codecInfo.getName();
                                                            if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                                if (BuildVars.LOGS_ENABLED) {
                                                                    stringBuilder2 = new StringBuilder();
                                                                    stringBuilder2.append("unsupported encoder = ");
                                                                    stringBuilder2.append(name);
                                                                    FileLog.m0d(stringBuilder2.toString());
                                                                }
                                                                PhotoViewer.this.setCompressItemEnabled(false, true);
                                                            } else if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
                                                                if (BuildVars.LOGS_ENABLED) {
                                                                    FileLog.m0d("no color format for video/avc");
                                                                }
                                                                PhotoViewer.this.setCompressItemEnabled(false, true);
                                                            }
                                                        } else {
                                                            if (BuildVars.LOGS_ENABLED) {
                                                                FileLog.m0d("no codec info for video/avc");
                                                            }
                                                            PhotoViewer.this.setCompressItemEnabled(false, true);
                                                        }
                                                        PhotoViewer.this.qualityChooseView.invalidate();
                                                    }
                                                }
                                                PhotoViewer.this.compressionsCount = 5;
                                                PhotoViewer.this.updateWidthHeightBitrateForCompression();
                                                if (PhotoViewer.this.compressionsCount > 1) {
                                                }
                                                PhotoViewer.this.setCompressItemEnabled(PhotoViewer.this.compressionsCount > 1, true);
                                                if (BuildVars.LOGS_ENABLED) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("compressionsCount = ");
                                                    stringBuilder.append(PhotoViewer.this.compressionsCount);
                                                    stringBuilder.append(" w = ");
                                                    stringBuilder.append(PhotoViewer.this.originalWidth);
                                                    stringBuilder.append(" h = ");
                                                    stringBuilder.append(PhotoViewer.this.originalHeight);
                                                    FileLog.m0d(stringBuilder.toString());
                                                }
                                                try {
                                                    codecInfo = MediaController.selectCodec("video/avc");
                                                    if (codecInfo == null) {
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            FileLog.m0d("no codec info for video/avc");
                                                        }
                                                        PhotoViewer.this.setCompressItemEnabled(false, true);
                                                    } else {
                                                        name = codecInfo.getName();
                                                        if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                            if (BuildVars.LOGS_ENABLED) {
                                                                stringBuilder2 = new StringBuilder();
                                                                stringBuilder2.append("unsupported encoder = ");
                                                                stringBuilder2.append(name);
                                                                FileLog.m0d(stringBuilder2.toString());
                                                            }
                                                            PhotoViewer.this.setCompressItemEnabled(false, true);
                                                        } else if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
                                                            if (BuildVars.LOGS_ENABLED) {
                                                                FileLog.m0d("no color format for video/avc");
                                                            }
                                                            PhotoViewer.this.setCompressItemEnabled(false, true);
                                                        }
                                                    }
                                                } catch (Throwable e) {
                                                    PhotoViewer.this.setCompressItemEnabled(false, true);
                                                    FileLog.m3e(e);
                                                }
                                                PhotoViewer.this.qualityChooseView.invalidate();
                                            } else {
                                                PhotoViewer.this.compressionsCount = 0;
                                            }
                                            PhotoViewer.this.updateVideoInfo();
                                            PhotoViewer.this.updateMuteButton();
                                        }
                                    }
                                });
                            }
                        }
                    } catch (Exception e9) {
                        e4222 = e9;
                        trackHeaderBox = null;
                        isAvc = true;
                        e2 = e4222;
                        FileLog.m3e(e2);
                        isAvc3 = false;
                        if (trackHeaderBox == null) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("video hasn't trackHeaderBox atom");
                            }
                            isAvc3 = false;
                        }
                        isAvcFinal = isAvc3;
                        trackHeaderBox2 = trackHeaderBox;
                        if (PhotoViewer.this.currentLoadingVideoRunnable != r1) {
                            PhotoViewer.this.currentLoadingVideoRunnable = null;
                            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                        }
                    }
                    if (trackHeaderBox == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("video hasn't trackHeaderBox atom");
                        }
                        isAvc3 = false;
                    }
                    isAvcFinal = isAvc3;
                    trackHeaderBox2 = trackHeaderBox;
                    if (PhotoViewer.this.currentLoadingVideoRunnable != r1) {
                        PhotoViewer.this.currentLoadingVideoRunnable = null;
                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    }
                }
            }
        };
        this.currentLoadingVideoRunnable = anonymousClass82;
        dispatchQueue.postRunnable(anonymousClass82);
    }

    private void setCompressItemEnabled(boolean enabled, boolean animated) {
        if (this.compressItem != null) {
            if ((!enabled || this.compressItem.getTag() == null) && (enabled || this.compressItem.getTag() != null)) {
                this.compressItem.setTag(enabled ? Integer.valueOf(1) : null);
                this.compressItem.setEnabled(enabled);
                this.compressItem.setClickable(enabled);
                if (this.compressItemAnimation != null) {
                    this.compressItemAnimation.cancel();
                    this.compressItemAnimation = null;
                }
                float f = 0.5f;
                if (animated) {
                    this.compressItemAnimation = new AnimatorSet();
                    AnimatorSet animatorSet = this.compressItemAnimation;
                    Animator[] animatorArr = new Animator[1];
                    ImageView imageView = this.compressItem;
                    String str = "alpha";
                    float[] fArr = new float[1];
                    if (enabled) {
                        f = 1.0f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView, str, fArr);
                    animatorSet.playTogether(animatorArr);
                    this.compressItemAnimation.setDuration(180);
                    this.compressItemAnimation.setInterpolator(decelerateInterpolator);
                    this.compressItemAnimation.start();
                } else {
                    ImageView imageView2 = this.compressItem;
                    if (enabled) {
                        f = 1.0f;
                    }
                    imageView2.setAlpha(f);
                }
            }
        }
    }
}
