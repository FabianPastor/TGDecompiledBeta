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
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
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
import org.telegram.messenger.exoplayer2.C0542C;
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
import org.telegram.ui.ActionBar.BaseFragment;
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
    private Runnable miniProgressShowRunnable = new C15891();
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
    private SurfaceTextureListener surfaceTextureListener = new C16044();
    private TextView switchCaptionTextView;
    private int switchImageAfterAnimation;
    private Runnable switchToInlineRunnable = new C15993();
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
    private Runnable updateProgressRunnable = new C15952();
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
    class C15891 implements Runnable {
        C15891() {
        }

        public void run() {
            PhotoViewer.this.toggleMiniProgressInternal(true);
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$2 */
    class C15952 implements Runnable {
        C15952() {
        }

        public void run() {
            if (PhotoViewer.this.videoPlayer != null) {
                float currentPosition;
                if (PhotoViewer.this.isCurrentVideo) {
                    if (!PhotoViewer.this.videoTimelineView.isDragging()) {
                        currentPosition = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                        if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                            PhotoViewer.this.videoTimelineView.setProgress(currentPosition);
                        } else if (currentPosition >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                            PhotoViewer.this.videoPlayer.pause();
                            PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                            PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                            PhotoViewer.this.containerView.invalidate();
                        } else {
                            currentPosition -= PhotoViewer.this.videoTimelineView.getLeftProgress();
                            if (currentPosition < 0.0f) {
                                currentPosition = 0.0f;
                            }
                            currentPosition /= PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress();
                            if (currentPosition > 1.0f) {
                                currentPosition = 1.0f;
                            }
                            PhotoViewer.this.videoTimelineView.setProgress(currentPosition);
                        }
                        PhotoViewer.this.updateVideoPlayerTime();
                    }
                } else if (!PhotoViewer.this.videoPlayerSeekbar.isDragging()) {
                    float f;
                    currentPosition = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (PhotoViewer.this.currentVideoFinishedLoading) {
                        f = 1.0f;
                    } else {
                        long uptimeMillis = SystemClock.uptimeMillis();
                        if (Math.abs(uptimeMillis - PhotoViewer.this.lastBufferedPositionCheck) >= 500) {
                            if (PhotoViewer.this.isStreaming) {
                                f = FileLoader.getInstance(PhotoViewer.this.currentAccount).getBufferedProgressFromPosition(PhotoViewer.this.seekToProgressPending != 0.0f ? PhotoViewer.this.seekToProgressPending : currentPosition, PhotoViewer.this.currentFileNames[0]);
                            } else {
                                f = 1.0f;
                            }
                            PhotoViewer.this.lastBufferedPositionCheck = uptimeMillis;
                        } else {
                            f = -1.0f;
                        }
                    }
                    if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                        if (PhotoViewer.this.seekToProgressPending == 0.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setProgress(currentPosition);
                        }
                        if (f != -1.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setBufferedProgress(f);
                            if (PhotoViewer.this.pipVideoView != null) {
                                PhotoViewer.this.pipVideoView.setBufferedProgress(f);
                            }
                        }
                    } else if (currentPosition >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                        PhotoViewer.this.videoPlayer.pause();
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                        PhotoViewer.this.containerView.invalidate();
                    } else {
                        currentPosition -= PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (currentPosition < 0.0f) {
                            currentPosition = 0.0f;
                        }
                        currentPosition /= PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (currentPosition > 1.0f) {
                            currentPosition = 1.0f;
                        }
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(currentPosition);
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
    class C15993 implements Runnable {
        C15993() {
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
                } catch (Throwable th) {
                    if (PhotoViewer.this.currentBitmap != null) {
                        PhotoViewer.this.currentBitmap.recycle();
                        PhotoViewer.this.currentBitmap = null;
                    }
                    FileLog.m3e(th);
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
    class C16044 implements SurfaceTextureListener {

        /* renamed from: org.telegram.ui.PhotoViewer$4$1 */
        class C16011 implements OnPreDrawListener {

            /* renamed from: org.telegram.ui.PhotoViewer$4$1$1 */
            class C16001 implements Runnable {
                C16001() {
                }

                public void run() {
                    if (PhotoViewer.this.isInline) {
                        PhotoViewer.this.dismissInternal();
                    }
                }
            }

            C16011() {
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
                AndroidUtilities.runOnUIThread(new C16001());
                PhotoViewer.this.waitingForFirstTextureUpload = 0;
                return true;
            }
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        C16044() {
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (PhotoViewer.this.videoTextureView == null || !PhotoViewer.this.changingTextureView) {
                return true;
            }
            if (PhotoViewer.this.switchingInlineMode) {
                PhotoViewer.this.waitingForFirstTextureUpload = 2;
            }
            PhotoViewer.this.videoTextureView.setSurfaceTexture(surfaceTexture);
            PhotoViewer.this.videoTextureView.setVisibility(0);
            PhotoViewer.this.changingTextureView = false;
            PhotoViewer.this.containerView.invalidate();
            return false;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            if (PhotoViewer.this.waitingForFirstTextureUpload == 1) {
                PhotoViewer.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new C16011());
                PhotoViewer.this.changedTextureView.invalidate();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$6 */
    class C16146 implements OnApplyWindowInsetsListener {
        C16146() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            WindowInsets windowInsets2 = (WindowInsets) PhotoViewer.this.lastInsets;
            PhotoViewer.this.lastInsets = windowInsets;
            if (windowInsets2 == null || windowInsets2.toString().equals(windowInsets.toString()) == null) {
                if (PhotoViewer.this.animationInProgress == 1) {
                    PhotoViewer.this.animatingImageView.setTranslationX(PhotoViewer.this.animatingImageView.getTranslationX() - ((float) PhotoViewer.this.getLeftInset()));
                    PhotoViewer.this.animationValues[0][2] = PhotoViewer.this.animatingImageView.getTranslationX();
                }
                PhotoViewer.this.windowView.requestLayout();
            }
            return windowInsets.consumeSystemWindowInsets();
        }
    }

    private class BackgroundDrawable extends ColorDrawable {
        private boolean allowDrawContent;
        private Runnable drawRunnable;

        /* renamed from: org.telegram.ui.PhotoViewer$BackgroundDrawable$1 */
        class C16211 implements Runnable {
            C16211() {
            }

            public void run() {
                if (PhotoViewer.this.parentAlert != null) {
                    PhotoViewer.this.parentAlert.setAllowDrawContent(BackgroundDrawable.this.allowDrawContent);
                }
            }
        }

        public BackgroundDrawable(int i) {
            super(i);
        }

        @Keep
        public void setAlpha(int i) {
            if (PhotoViewer.this.parentActivity instanceof LaunchActivity) {
                boolean z;
                if (PhotoViewer.this.isVisible) {
                    if (i == 255) {
                        z = false;
                        this.allowDrawContent = z;
                        ((LaunchActivity) PhotoViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(this.allowDrawContent);
                        if (PhotoViewer.this.parentAlert != null) {
                            if (!this.allowDrawContent) {
                                AndroidUtilities.runOnUIThread(new C16211(), 50);
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
                        AndroidUtilities.runOnUIThread(new C16211(), 50);
                    } else if (PhotoViewer.this.parentAlert != null) {
                        PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
                    }
                }
            }
            super.setAlpha(i);
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != null && this.drawRunnable != null) {
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
        public void setScaleX(float f) {
            super.setScaleX(f);
            invalidate();
        }

        @Keep
        public void setRotationX(float f) {
            this.rotation = f;
            invalidate();
        }

        public float getRotationX() {
            return this.rotation;
        }

        public void setCount(int i) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(Math.max(1, i));
            this.staticLayout = new StaticLayout(stringBuilder.toString(), this.textPaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.width = (int) Math.ceil((double) this.staticLayout.getLineWidth(0));
            this.height = this.staticLayout.getLineBottom(0);
            AnimatorSet animatorSet = new AnimatorSet();
            if (i == 0) {
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
            } else if (i < this.currentCount) {
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.1f, 1.0f}), ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.1f, 1.0f})});
                animatorSet.setInterpolator(new OvershootInterpolator());
            } else {
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "scaleX", new float[]{0.9f, 1.0f}), ObjectAnimator.ofFloat(this, "scaleY", new float[]{0.9f, 1.0f})});
                animatorSet.setInterpolator(new OvershootInterpolator());
            }
            animatorSet.setDuration(180);
            animatorSet.start();
            requestLayout();
            this.currentCount = i;
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(Math.max(this.width + AndroidUtilities.dp(NUM), AndroidUtilities.dp(NUM)), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
        }

        protected void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() / 2;
            this.paint.setAlpha(255);
            this.rect.set((float) AndroidUtilities.dp(1.0f), (float) (measuredHeight - AndroidUtilities.dp(14.0f)), (float) (getMeasuredWidth() - AndroidUtilities.dp(1.0f)), (float) (measuredHeight + AndroidUtilities.dp(14.0f)));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), this.paint);
            if (this.staticLayout != null) {
                this.textPaint.setAlpha((int) ((1.0f - this.rotation) * 255.0f));
                canvas.save();
                canvas.translate((float) ((getMeasuredWidth() - this.width) / 2), (((float) ((getMeasuredHeight() - this.height) / 2)) + AndroidUtilities.dpf2(0.2f)) + (this.rotation * ((float) AndroidUtilities.dp(5.0f))));
                this.staticLayout.draw(canvas);
                canvas.restore();
                this.paint.setAlpha((int) (this.rotation * 255.0f));
                measuredHeight = (int) this.rect.centerX();
                int centerY = (int) (((float) ((int) this.rect.centerY())) - ((((float) AndroidUtilities.dp(5.0f)) * (1.0f - this.rotation)) + ((float) AndroidUtilities.dp(3.0f))));
                canvas.drawLine((float) (AndroidUtilities.dp(0.5f) + measuredHeight), (float) (centerY - AndroidUtilities.dp(0.5f)), (float) (measuredHeight - AndroidUtilities.dp(6.0f)), (float) (AndroidUtilities.dp(6.0f) + centerY), this.paint);
                canvas.drawLine((float) (measuredHeight - AndroidUtilities.dp(0.5f)), (float) (centerY - AndroidUtilities.dp(0.5f)), (float) (measuredHeight + AndroidUtilities.dp(6.0f)), (float) (centerY + AndroidUtilities.dp(6.0f)), this.paint);
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

        public void onLongPress(MotionEvent motionEvent) {
        }

        public void onShowPress(MotionEvent motionEvent) {
        }

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
            boolean z;
            Object obj = null;
            int min;
            int i;
            MessageObject messageObject;
            int indexOf;
            MessageObject messageObject2;
            if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
                if (!PhotoViewer.this.imagesArr.isEmpty()) {
                    obj = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                    if (obj.messageOwner.grouped_id == this.currentGroupId) {
                        int access$3000;
                        min = Math.min(PhotoViewer.this.currentIndex + 10, PhotoViewer.this.imagesArr.size());
                        i = 0;
                        for (access$3000 = PhotoViewer.this.currentIndex; access$3000 < min; access$3000++) {
                            messageObject = (MessageObject) PhotoViewer.this.imagesArr.get(access$3000);
                            if (PhotoViewer.this.slideshowMessageId == 0 && messageObject.messageOwner.grouped_id != this.currentGroupId) {
                                break;
                            }
                            i++;
                        }
                        min = Math.max(PhotoViewer.this.currentIndex - 10, 0);
                        for (access$3000 = PhotoViewer.this.currentIndex - 1; access$3000 >= min; access$3000--) {
                            messageObject = (MessageObject) PhotoViewer.this.imagesArr.get(access$3000);
                            if (PhotoViewer.this.slideshowMessageId == 0 && messageObject.messageOwner.grouped_id != this.currentGroupId) {
                                break;
                            }
                            i++;
                        }
                    } else {
                        this.currentGroupId = obj.messageOwner.grouped_id;
                        i = 0;
                        z = true;
                    }
                } else {
                    z = false;
                    i = z;
                }
                if (obj == null) {
                    if (!z) {
                        if (i == this.currentPhotos.size()) {
                            if (this.currentObjects.indexOf(obj) == -1) {
                                indexOf = this.currentObjects.indexOf(obj);
                                if (!(this.currentImage == indexOf || indexOf == -1)) {
                                    if (this.animateAllLine) {
                                        fillImages(true, (this.currentImage - indexOf) * (this.itemWidth + this.itemSpacing));
                                        this.currentImage = indexOf;
                                        this.moving = false;
                                    } else {
                                        this.animateToItem = indexOf;
                                        this.nextImage = indexOf;
                                        this.animateToDX = (this.currentImage - indexOf) * (this.itemWidth + this.itemSpacing);
                                        this.moving = true;
                                        this.animateAllLine = false;
                                        this.lastUpdateTime = System.currentTimeMillis();
                                        invalidate();
                                    }
                                    this.drawDx = 0;
                                }
                            }
                        }
                        z = true;
                    }
                    if (z) {
                        this.animateAllLine = false;
                        this.currentPhotos.clear();
                        this.currentObjects.clear();
                        if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
                            this.currentObjects.addAll(PhotoViewer.this.imagesArrLocations);
                            this.currentPhotos.addAll(PhotoViewer.this.imagesArrLocations);
                            this.currentImage = PhotoViewer.this.currentIndex;
                            this.animateToItem = -1;
                        } else if (!PhotoViewer.this.imagesArr.isEmpty() && (this.currentGroupId != 0 || PhotoViewer.this.slideshowMessageId != 0)) {
                            indexOf = Math.min(PhotoViewer.this.currentIndex + 10, PhotoViewer.this.imagesArr.size());
                            for (min = PhotoViewer.this.currentIndex; min < indexOf; min++) {
                                messageObject = (MessageObject) PhotoViewer.this.imagesArr.get(min);
                                if (PhotoViewer.this.slideshowMessageId != 0 && messageObject.messageOwner.grouped_id != this.currentGroupId) {
                                    break;
                                }
                                this.currentObjects.add(messageObject);
                                this.currentPhotos.add(FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 56, true));
                            }
                            this.currentImage = 0;
                            this.animateToItem = -1;
                            indexOf = Math.max(PhotoViewer.this.currentIndex - 10, 0);
                            for (min = PhotoViewer.this.currentIndex - 1; min >= indexOf; min--) {
                                messageObject2 = (MessageObject) PhotoViewer.this.imagesArr.get(min);
                                if (PhotoViewer.this.slideshowMessageId != 0 && messageObject2.messageOwner.grouped_id != this.currentGroupId) {
                                    break;
                                }
                                this.currentObjects.add(0, messageObject2);
                                this.currentPhotos.add(0, FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 56, true));
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
            obj = (FileLocation) PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
            i = PhotoViewer.this.imagesArrLocations.size();
            z = false;
            if (obj == null) {
                if (z) {
                    if (i == this.currentPhotos.size()) {
                        if (this.currentObjects.indexOf(obj) == -1) {
                            indexOf = this.currentObjects.indexOf(obj);
                            if (this.animateAllLine) {
                                fillImages(true, (this.currentImage - indexOf) * (this.itemWidth + this.itemSpacing));
                                this.currentImage = indexOf;
                                this.moving = false;
                            } else {
                                this.animateToItem = indexOf;
                                this.nextImage = indexOf;
                                this.animateToDX = (this.currentImage - indexOf) * (this.itemWidth + this.itemSpacing);
                                this.moving = true;
                                this.animateAllLine = false;
                                this.lastUpdateTime = System.currentTimeMillis();
                                invalidate();
                            }
                            this.drawDx = 0;
                        }
                    }
                    z = true;
                }
                if (z) {
                    this.animateAllLine = false;
                    this.currentPhotos.clear();
                    this.currentObjects.clear();
                    if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
                        indexOf = Math.min(PhotoViewer.this.currentIndex + 10, PhotoViewer.this.imagesArr.size());
                        for (min = PhotoViewer.this.currentIndex; min < indexOf; min++) {
                            messageObject = (MessageObject) PhotoViewer.this.imagesArr.get(min);
                            if (PhotoViewer.this.slideshowMessageId != 0) {
                            }
                            this.currentObjects.add(messageObject);
                            this.currentPhotos.add(FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 56, true));
                        }
                        this.currentImage = 0;
                        this.animateToItem = -1;
                        indexOf = Math.max(PhotoViewer.this.currentIndex - 10, 0);
                        for (min = PhotoViewer.this.currentIndex - 1; min >= indexOf; min--) {
                            messageObject2 = (MessageObject) PhotoViewer.this.imagesArr.get(min);
                            if (PhotoViewer.this.slideshowMessageId != 0) {
                            }
                            this.currentObjects.add(0, messageObject2);
                            this.currentPhotos.add(0, FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 56, true));
                            this.currentImage++;
                        }
                    } else {
                        this.currentObjects.addAll(PhotoViewer.this.imagesArrLocations);
                        this.currentPhotos.addAll(PhotoViewer.this.imagesArrLocations);
                        this.currentImage = PhotoViewer.this.currentIndex;
                        this.animateToItem = -1;
                    }
                    if (this.currentPhotos.size() == 1) {
                        this.currentPhotos.clear();
                        this.currentObjects.clear();
                    }
                    fillImages(false, 0);
                }
            }
        }

        public void setMoveProgress(float f) {
            if (!this.scrolling) {
                if (this.animateToItem < 0) {
                    if (f > 0.0f) {
                        this.nextImage = this.currentImage - 1;
                    } else {
                        this.nextImage = this.currentImage + 1;
                    }
                    if (this.nextImage < 0 || this.nextImage >= this.currentPhotos.size()) {
                        this.currentItemProgress = 1.0f;
                    } else {
                        this.currentItemProgress = 1.0f - Math.abs(f);
                    }
                    this.nextItemProgress = 1.0f - this.currentItemProgress;
                    this.moving = f != 0.0f;
                    invalidate();
                    if (!this.currentPhotos.isEmpty() && (f >= 0.0f || this.currentImage != this.currentPhotos.size() - 1)) {
                        if (f <= 0.0f || this.currentImage != 0) {
                            this.drawDx = (int) (f * ((float) (this.itemWidth + this.itemSpacing)));
                            fillImages(true, this.drawDx);
                        }
                    }
                }
            }
        }

        private ImageReceiver getFreeReceiver() {
            ImageReceiver imageReceiver;
            if (this.unusedReceivers.isEmpty()) {
                imageReceiver = new ImageReceiver(this);
            } else {
                imageReceiver = (ImageReceiver) this.unusedReceivers.get(0);
                this.unusedReceivers.remove(0);
            }
            this.imagesToDraw.add(imageReceiver);
            imageReceiver.setCurrentAccount(PhotoViewer.this.currentAccount);
            return imageReceiver;
        }

        private void fillImages(boolean z, int i) {
            GroupedPhotosListView groupedPhotosListView = this;
            int i2 = 0;
            if (!(z || groupedPhotosListView.imagesToDraw.isEmpty())) {
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
                    int size;
                    int i3;
                    int i4;
                    int measuredWidth = getMeasuredWidth();
                    int measuredWidth2 = (getMeasuredWidth() / 2) - (groupedPhotosListView.itemWidth / 2);
                    if (z) {
                        size = groupedPhotosListView.imagesToDraw.size();
                        i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        i4 = Integer.MIN_VALUE;
                        while (i2 < size) {
                            ImageReceiver imageReceiver = (ImageReceiver) groupedPhotosListView.imagesToDraw.get(i2);
                            int param = imageReceiver.getParam();
                            int i5 = (((param - groupedPhotosListView.currentImage) * (groupedPhotosListView.itemWidth + groupedPhotosListView.itemSpacing)) + measuredWidth2) + i;
                            if (i5 > measuredWidth || i5 + groupedPhotosListView.itemWidth < 0) {
                                groupedPhotosListView.unusedReceivers.add(imageReceiver);
                                groupedPhotosListView.imagesToDraw.remove(i2);
                                size--;
                                i2--;
                            }
                            i3 = Math.min(i3, param - 1);
                            i4 = Math.max(i4, param + 1);
                            i2++;
                        }
                    } else {
                        i4 = groupedPhotosListView.currentImage;
                        i3 = groupedPhotosListView.currentImage - 1;
                    }
                    if (i4 != Integer.MIN_VALUE) {
                        size = groupedPhotosListView.currentPhotos.size();
                        while (i4 < size) {
                            i2 = (((i4 - groupedPhotosListView.currentImage) * (groupedPhotosListView.itemWidth + groupedPhotosListView.itemSpacing)) + measuredWidth2) + i;
                            if (i2 >= measuredWidth) {
                                break;
                            }
                            FileLocation fileLocation = (TLObject) groupedPhotosListView.currentPhotos.get(i4);
                            if (fileLocation instanceof PhotoSize) {
                                fileLocation = ((PhotoSize) fileLocation).location;
                            }
                            ImageReceiver freeReceiver = getFreeReceiver();
                            freeReceiver.setImageCoords(i2, groupedPhotosListView.itemY, groupedPhotosListView.itemWidth, groupedPhotosListView.itemHeight);
                            ImageReceiver imageReceiver2 = freeReceiver;
                            freeReceiver.setImage(null, null, null, null, fileLocation, "80_80", 0, null, 1);
                            imageReceiver2.setParam(i4);
                            i4++;
                        }
                    }
                    if (i3 != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        while (i3 >= 0) {
                            size = ((((i3 - groupedPhotosListView.currentImage) * (groupedPhotosListView.itemWidth + groupedPhotosListView.itemSpacing)) + measuredWidth2) + i) + groupedPhotosListView.itemWidth;
                            if (size <= 0) {
                                break;
                            }
                            FileLocation fileLocation2 = (TLObject) groupedPhotosListView.currentPhotos.get(i3);
                            if (fileLocation2 instanceof PhotoSize) {
                                fileLocation2 = ((PhotoSize) fileLocation2).location;
                            }
                            ImageReceiver freeReceiver2 = getFreeReceiver();
                            freeReceiver2.setImageCoords(size, groupedPhotosListView.itemY, groupedPhotosListView.itemWidth, groupedPhotosListView.itemHeight);
                            freeReceiver2.setImage(null, null, null, null, fileLocation2, "80_80", 0, null, 1);
                            freeReceiver2.setParam(i3);
                            i3--;
                        }
                    }
                }
            }
        }

        public boolean onDown(MotionEvent motionEvent) {
            if (this.scroll.isFinished() == null) {
                this.scroll.abortAnimation();
            }
            this.animateToItem = -1;
            return true;
        }

        public boolean onSingleTapUp(MotionEvent motionEvent) {
            stopScrolling();
            int size = this.imagesToDraw.size();
            for (int i = 0; i < size; i++) {
                ImageReceiver imageReceiver = (ImageReceiver) this.imagesToDraw.get(i);
                if (imageReceiver.isInsideImage(motionEvent.getX(), motionEvent.getY())) {
                    motionEvent = imageReceiver.getParam();
                    if (motionEvent >= null) {
                        if (motionEvent < this.currentObjects.size()) {
                            if (!PhotoViewer.this.imagesArr.isEmpty()) {
                                motionEvent = PhotoViewer.this.imagesArr.indexOf((MessageObject) this.currentObjects.get(motionEvent));
                                if (PhotoViewer.this.currentIndex == motionEvent) {
                                    return true;
                                }
                                this.moveLineProgress = 1.0f;
                                this.animateAllLine = true;
                                PhotoViewer.this.currentIndex = -1;
                                if (PhotoViewer.this.currentThumb != null) {
                                    PhotoViewer.this.currentThumb.release();
                                    PhotoViewer.this.currentThumb = null;
                                }
                                PhotoViewer.this.setImageIndex(motionEvent, true);
                            } else if (!PhotoViewer.this.imagesArrLocations.isEmpty()) {
                                motionEvent = PhotoViewer.this.imagesArrLocations.indexOf((FileLocation) this.currentObjects.get(motionEvent));
                                if (PhotoViewer.this.currentIndex == motionEvent) {
                                    return true;
                                }
                                this.moveLineProgress = 1.0f;
                                this.animateAllLine = true;
                                PhotoViewer.this.currentIndex = -1;
                                if (PhotoViewer.this.currentThumb != null) {
                                    PhotoViewer.this.currentThumb.release();
                                    PhotoViewer.this.currentThumb = null;
                                }
                                PhotoViewer.this.setImageIndex(motionEvent, true);
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
            int i = this.drawDx;
            if (Math.abs(i) > (this.itemWidth / 2) + this.itemSpacing) {
                int i2;
                if (i > 0) {
                    i2 = i - ((this.itemWidth / 2) + this.itemSpacing);
                    i = 1;
                } else {
                    i2 = i + ((this.itemWidth / 2) + this.itemSpacing);
                    i = -1;
                }
                i += i2 / (this.itemWidth + (this.itemSpacing * 2));
            } else {
                i = 0;
            }
            this.nextPhotoScrolling = this.currentImage - i;
            if (PhotoViewer.this.currentIndex != this.nextPhotoScrolling && this.nextPhotoScrolling >= 0 && this.nextPhotoScrolling < this.currentPhotos.size()) {
                Object obj = this.currentObjects.get(this.nextPhotoScrolling);
                if (!PhotoViewer.this.imagesArr.isEmpty()) {
                    i = PhotoViewer.this.imagesArr.indexOf((MessageObject) obj);
                } else if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
                    i = -1;
                } else {
                    i = PhotoViewer.this.imagesArrLocations.indexOf((FileLocation) obj);
                }
                if (i >= 0) {
                    this.ignoreChanges = true;
                    PhotoViewer.this.currentIndex = -1;
                    if (PhotoViewer.this.currentThumb != null) {
                        PhotoViewer.this.currentThumb.release();
                        PhotoViewer.this.currentThumb = null;
                    }
                    PhotoViewer.this.setImageIndex(i, true);
                }
            }
            if (!this.scrolling) {
                this.scrolling = true;
                this.stopedScrolling = false;
            }
            fillImages(true, this.drawDx);
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            this.drawDx = (int) (((float) this.drawDx) - f);
            motionEvent = getMinScrollX();
            motionEvent2 = getMaxScrollX();
            if (this.drawDx < motionEvent) {
                this.drawDx = motionEvent;
            } else if (this.drawDx > motionEvent2) {
                this.drawDx = motionEvent2;
            }
            updateAfterScroll();
            return null;
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            this.scroll.abortAnimation();
            if (this.currentPhotos.size() >= 10) {
                this.scroll.fling(this.drawDx, 0, Math.round(f), 0, getMinScrollX(), getMaxScrollX(), 0, 0);
            }
            return null;
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean z = false;
            if (!this.currentPhotos.isEmpty()) {
                if (getAlpha() == 1.0f) {
                    if (this.gestureDetector.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent)) {
                        z = true;
                    }
                    if (this.scrolling && motionEvent.getAction() == 1 && this.scroll.isFinished() != null) {
                        stopScrolling();
                    }
                    return z;
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

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            fillImages(false, 0);
        }

        protected void onDraw(Canvas canvas) {
            if (!this.imagesToDraw.isEmpty()) {
                PhotoSize photoSize;
                int max;
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), r0.backgroundPaint);
                int size = r0.imagesToDraw.size();
                int i = r0.drawDx;
                int i2 = (int) (((float) r0.itemWidth) * 2.0f);
                int dp = AndroidUtilities.dp(8.0f);
                TLObject tLObject = (TLObject) r0.currentPhotos.get(r0.currentImage);
                if (tLObject instanceof PhotoSize) {
                    photoSize = (PhotoSize) tLObject;
                    max = Math.max(r0.itemWidth, (int) (((float) photoSize.f43w) * (((float) r0.itemHeight) / ((float) photoSize.f42h))));
                } else {
                    max = r0.itemHeight;
                }
                float f = (float) (dp * 2);
                int i3 = (int) (r0.currentItemProgress * f);
                int min = (r0.itemWidth + ((int) (((float) (Math.min(i2, max) - r0.itemWidth)) * r0.currentItemProgress))) + i3;
                if (r0.nextImage < 0 || r0.nextImage >= r0.currentPhotos.size()) {
                    max = r0.itemWidth;
                } else {
                    tLObject = (TLObject) r0.currentPhotos.get(r0.nextImage);
                    if (tLObject instanceof PhotoSize) {
                        photoSize = (PhotoSize) tLObject;
                        max = Math.max(r0.itemWidth, (int) (((float) photoSize.f43w) * (((float) r0.itemHeight) / ((float) photoSize.f42h))));
                    } else {
                        max = r0.itemHeight;
                    }
                }
                i2 = Math.min(i2, max);
                dp = (int) (f * r0.nextItemProgress);
                i = (int) (((float) i) + ((((float) (((i2 + dp) - r0.itemWidth) / 2)) * r0.nextItemProgress) * ((float) (r0.nextImage > r0.currentImage ? -1 : 1))));
                max = (r0.itemWidth + ((int) (((float) (i2 - r0.itemWidth)) * r0.nextItemProgress))) + dp;
                i2 = (getMeasuredWidth() - min) / 2;
                for (int i4 = 0; i4 < size; i4++) {
                    ImageReceiver imageReceiver = (ImageReceiver) r0.imagesToDraw.get(i4);
                    int param = imageReceiver.getParam();
                    if (param == r0.currentImage) {
                        imageReceiver.setImageX((i2 + i) + (i3 / 2));
                        imageReceiver.setImageWidth(min - i3);
                    } else {
                        if (r0.nextImage < r0.currentImage) {
                            if (param >= r0.currentImage) {
                                imageReceiver.setImageX((((i2 + min) + r0.itemSpacing) + (((imageReceiver.getParam() - r0.currentImage) - 1) * (r0.itemWidth + r0.itemSpacing))) + i);
                            } else if (param <= r0.nextImage) {
                                imageReceiver.setImageX((((((imageReceiver.getParam() - r0.currentImage) + 1) * (r0.itemWidth + r0.itemSpacing)) + i2) - (r0.itemSpacing + max)) + i);
                            } else {
                                imageReceiver.setImageX((((imageReceiver.getParam() - r0.currentImage) * (r0.itemWidth + r0.itemSpacing)) + i2) + i);
                            }
                        } else if (param < r0.currentImage) {
                            imageReceiver.setImageX((((imageReceiver.getParam() - r0.currentImage) * (r0.itemWidth + r0.itemSpacing)) + i2) + i);
                        } else if (param <= r0.nextImage) {
                            imageReceiver.setImageX((((i2 + min) + r0.itemSpacing) + (((imageReceiver.getParam() - r0.currentImage) - 1) * (r0.itemWidth + r0.itemSpacing))) + i);
                        } else {
                            imageReceiver.setImageX(((((i2 + min) + r0.itemSpacing) + (((imageReceiver.getParam() - r0.currentImage) - 2) * (r0.itemWidth + r0.itemSpacing))) + (r0.itemSpacing + max)) + i);
                        }
                        if (param == r0.nextImage) {
                            imageReceiver.setImageWidth(max - dp);
                            imageReceiver.setImageX(imageReceiver.getImageX() + (dp / 2));
                        } else {
                            imageReceiver.setImageWidth(r0.itemWidth);
                        }
                    }
                    imageReceiver.draw(canvas);
                }
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - r0.lastUpdateTime;
                long j2 = 17;
                if (j <= 17) {
                    j2 = j;
                }
                r0.lastUpdateTime = currentTimeMillis;
                if (r0.animateToItem >= 0) {
                    if (r0.moveLineProgress > 0.0f) {
                        float f2 = ((float) j2) / 200.0f;
                        r0.moveLineProgress -= f2;
                        if (r0.animateToItem == r0.currentImage) {
                            if (r0.currentItemProgress < 1.0f) {
                                r0.currentItemProgress += f2;
                                if (r0.currentItemProgress > 1.0f) {
                                    r0.currentItemProgress = 1.0f;
                                }
                            }
                            r0.drawDx = r0.animateToDXStart + ((int) Math.ceil((double) (r0.currentItemProgress * ((float) (r0.animateToDX - r0.animateToDXStart)))));
                        } else {
                            r0.nextItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - r0.moveLineProgress);
                            if (r0.stopedScrolling) {
                                if (r0.currentItemProgress > 0.0f) {
                                    r0.currentItemProgress -= f2;
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
                    r0.currentItemProgress -= ((float) j2) / 200.0f;
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

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                textView = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return textView;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return null;
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

        public PhotoProgressView(Context context, View view) {
            if (PhotoViewer.decelerateInterpolator == null) {
                PhotoViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                PhotoViewer.progressPaint = new Paint(1);
                PhotoViewer.progressPaint.setStyle(Style.STROKE);
                PhotoViewer.progressPaint.setStrokeCap(Cap.ROUND);
                PhotoViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                PhotoViewer.progressPaint.setColor(-1);
            }
            this.parent = view;
        }

        private void updateAnimation() {
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - this.lastUpdateTime;
            long j2 = 18;
            if (j <= 18) {
                j2 = j;
            }
            this.lastUpdateTime = currentTimeMillis;
            if (this.animatedProgressValue != 1.0f) {
                this.radOffset += ((float) (360 * j2)) / 3000.0f;
                float f = this.currentProgress - this.animationProgressStart;
                if (f > 0.0f) {
                    this.currentProgressTime += j2;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = this.currentProgress;
                        this.animationProgressStart = this.currentProgress;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = this.animationProgressStart + (f * PhotoViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f));
                    }
                }
                this.parent.invalidate();
            }
            if (this.animatedProgressValue >= 1.0f && this.previousBackgroundState != -2) {
                this.animatedAlphaValue -= ((float) j2) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousBackgroundState = -2;
                }
                this.parent.invalidate();
            }
        }

        public void setProgress(float f, boolean z) {
            if (z) {
                this.animationProgressStart = this.animatedProgressValue;
            } else {
                this.animatedProgressValue = f;
                this.animationProgressStart = f;
            }
            this.currentProgress = f;
            this.currentProgressTime = 0.0f;
        }

        public void setBackgroundState(int i, boolean z) {
            if (this.backgroundState != i || !z) {
                this.lastUpdateTime = System.currentTimeMillis();
                if (!z || this.backgroundState == i) {
                    this.previousBackgroundState = true;
                } else {
                    this.previousBackgroundState = this.backgroundState;
                    this.animatedAlphaValue = true;
                }
                this.backgroundState = i;
                this.parent.invalidate();
            }
        }

        public void setAlpha(float f) {
            this.alpha = f;
        }

        public void setScale(float f) {
            this.scale = f;
        }

        public void onDraw(Canvas canvas) {
            Drawable drawable;
            int i = (int) (((float) this.size) * this.scale);
            int access$4000 = (PhotoViewer.this.getContainerViewWidth() - i) / 2;
            int access$4100 = (PhotoViewer.this.getContainerViewHeight() - i) / 2;
            if (this.previousBackgroundState >= 0 && this.previousBackgroundState < 4) {
                drawable = PhotoViewer.progressDrawables[this.previousBackgroundState];
                if (drawable != null) {
                    drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                    drawable.setBounds(access$4000, access$4100, access$4000 + i, access$4100 + i);
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
                    drawable.setBounds(access$4000, access$4100, access$4000 + i, access$4100 + i);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState == 0 || this.backgroundState == 1 || this.previousBackgroundState == 0 || this.previousBackgroundState == 1) {
                int dp = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    PhotoViewer.progressPaint.setAlpha((int) ((255.0f * this.animatedAlphaValue) * this.alpha));
                } else {
                    PhotoViewer.progressPaint.setAlpha((int) (255.0f * this.alpha));
                }
                this.progressRect.set((float) (access$4000 + dp), (float) (access$4100 + dp), (float) ((access$4000 + i) - dp), (float) ((access$4100 + i) - dp));
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            boolean z = false;
            int i;
            if (motionEvent.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                motionEvent = null;
                while (motionEvent < PhotoViewer.this.compressionsCount) {
                    i = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * motionEvent)) + (this.circleSize / 2);
                    if (x <= ((float) (i - AndroidUtilities.dp(15.0f))) || x >= ((float) (i + AndroidUtilities.dp(15.0f)))) {
                        motionEvent++;
                    } else {
                        if (motionEvent == PhotoViewer.this.selectedCompression) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingQuality = PhotoViewer.this.selectedCompression;
                    }
                }
            } else if (motionEvent.getAction() == 2) {
                if (this.startMoving != null) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving != null) {
                    motionEvent = null;
                    while (motionEvent < PhotoViewer.this.compressionsCount) {
                        i = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * motionEvent)) + (this.circleSize / 2);
                        int i2 = ((this.lineSize / 2) + (this.circleSize / 2)) + this.gapSize;
                        if (x <= ((float) (i - i2)) || x >= ((float) (i + i2))) {
                            motionEvent++;
                        } else if (PhotoViewer.this.selectedCompression != motionEvent) {
                            PhotoViewer.this.selectedCompression = motionEvent;
                            PhotoViewer.this.didChangedCompressionLevel(false);
                            invalidate();
                        }
                    }
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (this.moving == null) {
                    motionEvent = null;
                    while (motionEvent < PhotoViewer.this.compressionsCount) {
                        i = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * motionEvent)) + (this.circleSize / 2);
                        if (x <= ((float) (i - AndroidUtilities.dp(15.0f))) || x >= ((float) (i + AndroidUtilities.dp(15.0f)))) {
                            motionEvent++;
                        } else if (PhotoViewer.this.selectedCompression != motionEvent) {
                            PhotoViewer.this.selectedCompression = motionEvent;
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

        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.circleSize = AndroidUtilities.dp(NUM);
            this.gapSize = AndroidUtilities.dp(NUM);
            this.sideSide = AndroidUtilities.dp(NUM);
        }

        protected void onDraw(Canvas canvas) {
            if (PhotoViewer.this.compressionsCount != 1) {
                this.lineSize = (((getMeasuredWidth() - (this.circleSize * PhotoViewer.this.compressionsCount)) - (this.gapSize * 8)) - (this.sideSide * 2)) / (PhotoViewer.this.compressionsCount - 1);
            } else {
                this.lineSize = ((getMeasuredWidth() - (this.circleSize * PhotoViewer.this.compressionsCount)) - (this.gapSize * 8)) - (this.sideSide * 2);
            }
            int measuredHeight = (getMeasuredHeight() / 2) + AndroidUtilities.dp(6.0f);
            int i = 0;
            while (i < PhotoViewer.this.compressionsCount) {
                String stringBuilder;
                int i2 = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * i)) + (this.circleSize / 2);
                if (i <= PhotoViewer.this.selectedCompression) {
                    this.paint.setColor(-11292945);
                } else {
                    this.paint.setColor(NUM);
                }
                if (i == PhotoViewer.this.compressionsCount - 1) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(Math.min(PhotoViewer.this.originalWidth, PhotoViewer.this.originalHeight));
                    stringBuilder2.append(TtmlNode.TAG_P);
                    stringBuilder = stringBuilder2.toString();
                } else {
                    stringBuilder = i == 0 ? "240p" : i == 1 ? "360p" : i == 2 ? "480p" : "720p";
                }
                float measureText = this.textPaint.measureText(stringBuilder);
                float f = (float) i2;
                canvas.drawCircle(f, (float) measuredHeight, (float) (i == PhotoViewer.this.selectedCompression ? AndroidUtilities.dp(8.0f) : this.circleSize / 2), this.paint);
                canvas.drawText(stringBuilder, f - (measureText / 2.0f), (float) (measuredHeight - AndroidUtilities.dp(16.0f)), this.textPaint);
                if (i != 0) {
                    i2 = ((i2 - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    canvas.drawRect((float) i2, (float) (measuredHeight - AndroidUtilities.dp(1.0f)), (float) (i2 + this.lineSize), (float) (AndroidUtilities.dp(2.0f) + measuredHeight), this.paint);
                }
                i++;
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$8 */
    class C22368 extends ActionBarMenuOnItemClick {
        C22368() {
        }

        public void onItemClick(int i) {
            C22368 c22368 = this;
            int i2 = i;
            int i3 = 1;
            if (i2 == -1) {
                if (PhotoViewer.this.needCaptionLayout && (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible())) {
                    PhotoViewer.this.closeCaptionEnter(false);
                    return;
                }
                PhotoViewer.this.closePhoto(true, false);
            } else if (i2 == 1) {
                if (VERSION.SDK_INT < 23 || PhotoViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                    File pathToAttach;
                    if (PhotoViewer.this.currentMessageObject != null) {
                        pathToAttach = ((PhotoViewer.this.currentMessageObject.messageOwner.media instanceof TL_messageMediaWebPage) && PhotoViewer.this.currentMessageObject.messageOwner.media.webpage != null && PhotoViewer.this.currentMessageObject.messageOwner.media.webpage.document == null) ? FileLoader.getPathToAttach(PhotoViewer.this.getFileLocation(PhotoViewer.this.currentIndex, null), true) : FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
                    } else if (PhotoViewer.this.currentFileLocation != null) {
                        boolean z;
                        TLObject access$8200 = PhotoViewer.this.currentFileLocation;
                        if (PhotoViewer.this.avatarsDialogId == 0) {
                            if (!PhotoViewer.this.isEvent) {
                                z = false;
                                pathToAttach = FileLoader.getPathToAttach(access$8200, z);
                            }
                        }
                        z = true;
                        pathToAttach = FileLoader.getPathToAttach(access$8200, z);
                    } else {
                        pathToAttach = null;
                    }
                    if (pathToAttach == null || !pathToAttach.exists()) {
                        PhotoViewer.this.showDownloadAlert();
                    } else {
                        String file = pathToAttach.toString();
                        Context access$2500 = PhotoViewer.this.parentActivity;
                        if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isVideo()) {
                            i3 = 0;
                        }
                        MediaController.saveFile(file, access$2500, i3, null, null);
                    }
                } else {
                    PhotoViewer.this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                }
            } else if (i2 == 2) {
                if (PhotoViewer.this.currentDialogId != 0) {
                    PhotoViewer.this.disableShowCheck = true;
                    r2 = new Bundle();
                    r2.putLong("dialog_id", PhotoViewer.this.currentDialogId);
                    r5 = new MediaActivity(r2);
                    if (PhotoViewer.this.parentChatActivity != null) {
                        r5.setChatInfo(PhotoViewer.this.parentChatActivity.getCurrentChatInfo());
                    }
                    PhotoViewer.this.closePhoto(false, false);
                    ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(r5, false, true);
                }
            } else if (i2 == 4) {
                if (PhotoViewer.this.currentMessageObject != null) {
                    boolean z2;
                    r2 = new Bundle();
                    int access$8600 = (int) PhotoViewer.this.currentDialogId;
                    r7 = (int) (PhotoViewer.this.currentDialogId >> 32);
                    if (access$8600 == 0) {
                        r2.putInt("enc_id", r7);
                    } else if (r7 == 1) {
                        r2.putInt("chat_id", access$8600);
                    } else if (access$8600 > 0) {
                        r2.putInt("user_id", access$8600);
                    } else if (access$8600 < 0) {
                        Chat chat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-access$8600));
                        if (!(chat == null || chat.migrated_to == null)) {
                            r2.putInt("migrated_to", access$8600);
                            access$8600 = -chat.migrated_to.channel_id;
                        }
                        r2.putInt("chat_id", -access$8600);
                    }
                    r2.putInt("message_id", PhotoViewer.this.currentMessageObject.getId());
                    NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    LaunchActivity launchActivity = (LaunchActivity) PhotoViewer.this.parentActivity;
                    if (launchActivity.getMainFragmentsCount() <= 1) {
                        if (!AndroidUtilities.isTablet()) {
                            z2 = false;
                            launchActivity.presentFragment(new ChatActivity(r2), z2, true);
                            PhotoViewer.this.currentMessageObject = null;
                            PhotoViewer.this.closePhoto(false, false);
                        }
                    }
                    z2 = true;
                    launchActivity.presentFragment(new ChatActivity(r2), z2, true);
                    PhotoViewer.this.currentMessageObject = null;
                    PhotoViewer.this.closePhoto(false, false);
                }
            } else if (i2 == 3) {
                if (PhotoViewer.this.currentMessageObject != null) {
                    if (PhotoViewer.this.parentActivity != null) {
                        ((LaunchActivity) PhotoViewer.this.parentActivity).switchToAccount(PhotoViewer.this.currentMessageObject.currentAccount, true);
                        r2 = new Bundle();
                        r2.putBoolean("onlySelect", true);
                        r2.putInt("dialogsType", 3);
                        r5 = new DialogsActivity(r2);
                        final ArrayList arrayList = new ArrayList();
                        arrayList.add(PhotoViewer.this.currentMessageObject);
                        r5.setDelegate(new DialogsActivityDelegate() {
                            public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
                                C22351 c22351 = this;
                                ArrayList<Long> arrayList2 = arrayList;
                                int i = 0;
                                if (arrayList.size() <= 1 && ((Long) arrayList2.get(0)).longValue() != ((long) UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId())) {
                                    if (charSequence == null) {
                                        long longValue = ((Long) arrayList2.get(0)).longValue();
                                        int i2 = (int) longValue;
                                        int i3 = (int) (longValue >> 32);
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("scrollToTopOnResume", true);
                                        if (i2 == 0) {
                                            bundle.putInt("enc_id", i3);
                                        } else if (i2 > 0) {
                                            bundle.putInt("user_id", i2);
                                        } else if (i2 < 0) {
                                            bundle.putInt("chat_id", -i2);
                                        }
                                        NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                        BaseFragment chatActivity = new ChatActivity(bundle);
                                        if (((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(chatActivity, true, false)) {
                                            chatActivity.showReplyPanel(true, null, arrayList, null, false);
                                            return;
                                        } else {
                                            dialogsActivity.finishFragment();
                                            return;
                                        }
                                    }
                                }
                                while (i < arrayList.size()) {
                                    long longValue2 = ((Long) arrayList2.get(i)).longValue();
                                    if (charSequence != null) {
                                        SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(charSequence.toString(), longValue2, null, null, true, null, null, null);
                                    }
                                    SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(arrayList, longValue2);
                                    i++;
                                }
                                dialogsActivity.finishFragment();
                            }
                        });
                        ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(r5, false, true);
                        PhotoViewer.this.closePhoto(false, false);
                    }
                }
            } else if (i2 == 6) {
                if (PhotoViewer.this.parentActivity != null) {
                    Builder builder = new Builder(PhotoViewer.this.parentActivity);
                    if (PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.currentMessageObject.isVideo()) {
                        builder.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", C0446R.string.AreYouSureDeleteVideo, new Object[0]));
                    } else if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isGif()) {
                        builder.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", C0446R.string.AreYouSureDeletePhoto, new Object[0]));
                    } else {
                        builder.setMessage(LocaleController.formatString("AreYouSure", C0446R.string.AreYouSure, new Object[0]));
                    }
                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    final boolean[] zArr = new boolean[1];
                    if (PhotoViewer.this.currentMessageObject != null) {
                        r7 = (int) PhotoViewer.this.currentMessageObject.getDialogId();
                        if (r7 != 0) {
                            User user;
                            Chat chat2;
                            if (r7 > 0) {
                                user = MessagesController.getInstance(PhotoViewer.this.currentAccount).getUser(Integer.valueOf(r7));
                                chat2 = null;
                            } else {
                                chat2 = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-r7));
                                user = null;
                            }
                            if (!(user == null && ChatObject.isChannel(chat2))) {
                                int currentTime = ConnectionsManager.getInstance(PhotoViewer.this.currentAccount).getCurrentTime();
                                if (!((user == null || user.id == UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId()) && chat2 == null) && ((PhotoViewer.this.currentMessageObject.messageOwner.action == null || (PhotoViewer.this.currentMessageObject.messageOwner.action instanceof TL_messageActionEmpty)) && PhotoViewer.this.currentMessageObject.isOut() && currentTime - PhotoViewer.this.currentMessageObject.messageOwner.date <= 172800)) {
                                    View frameLayout = new FrameLayout(PhotoViewer.this.parentActivity);
                                    View checkBoxCell = new CheckBoxCell(PhotoViewer.this.parentActivity, 1);
                                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    if (chat2 != null) {
                                        checkBoxCell.setText(LocaleController.getString("DeleteForAll", C0446R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    } else {
                                        checkBoxCell.setText(LocaleController.formatString("DeleteForUser", C0446R.string.DeleteForUser, UserObject.getFirstName(user)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    }
                                    checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                                    frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                    checkBoxCell.setOnClickListener(new OnClickListener() {
                                        public void onClick(View view) {
                                            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                                            zArr[0] = zArr[0] ^ true;
                                            checkBoxCell.setChecked(zArr[0], true);
                                        }
                                    });
                                    builder.setView(frameLayout);
                                }
                            }
                        }
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
                        /* JADX WARNING: inconsistent code. */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (PhotoViewer.this.imagesArr.isEmpty() == null) {
                                if (PhotoViewer.this.currentIndex >= null) {
                                    if (PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size()) {
                                        MessageObject messageObject = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                                        if (messageObject.isSent()) {
                                            ArrayList arrayList;
                                            EncryptedChat encryptedChat;
                                            PhotoViewer.this.closePhoto(false, false);
                                            ArrayList arrayList2 = new ArrayList();
                                            if (PhotoViewer.this.slideshowMessageId != 0) {
                                                arrayList2.add(Integer.valueOf(PhotoViewer.this.slideshowMessageId));
                                            } else {
                                                arrayList2.add(Integer.valueOf(messageObject.getId()));
                                            }
                                            if (((int) messageObject.getDialogId()) != 0 || messageObject.messageOwner.random_id == 0) {
                                                arrayList = null;
                                                encryptedChat = arrayList;
                                            } else {
                                                i = new ArrayList();
                                                i.add(Long.valueOf(messageObject.messageOwner.random_id));
                                                arrayList = i;
                                                encryptedChat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
                                            }
                                            MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteMessages(arrayList2, arrayList, encryptedChat, messageObject.messageOwner.to_id.channel_id, zArr[0]);
                                        }
                                    }
                                }
                                return;
                            }
                            if (PhotoViewer.this.avatarsArr.isEmpty() == null && PhotoViewer.this.currentIndex >= null) {
                                if (PhotoViewer.this.currentIndex < PhotoViewer.this.avatarsArr.size()) {
                                    boolean z;
                                    dialogInterface = (Photo) PhotoViewer.this.avatarsArr.get(PhotoViewer.this.currentIndex);
                                    FileLocation fileLocation = (FileLocation) PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
                                    if (dialogInterface instanceof TL_photoEmpty) {
                                        dialogInterface = null;
                                    }
                                    if (PhotoViewer.this.currentUserAvatarLocation != null) {
                                        if (dialogInterface != null) {
                                            Iterator it = dialogInterface.sizes.iterator();
                                            while (it.hasNext()) {
                                                PhotoSize photoSize = (PhotoSize) it.next();
                                                if (photoSize.location.local_id == PhotoViewer.this.currentUserAvatarLocation.local_id && photoSize.location.volume_id == PhotoViewer.this.currentUserAvatarLocation.volume_id) {
                                                }
                                            }
                                        } else if (fileLocation.local_id == PhotoViewer.this.currentUserAvatarLocation.local_id) {
                                        }
                                        z = true;
                                        if (z) {
                                            MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(null);
                                            PhotoViewer.this.closePhoto(false, false);
                                        } else if (dialogInterface != null) {
                                            i = new TL_inputPhoto();
                                            i.id = dialogInterface.id;
                                            i.access_hash = dialogInterface.access_hash;
                                            MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(i);
                                            MessagesStorage.getInstance(PhotoViewer.this.currentAccount).clearUserPhoto(PhotoViewer.this.avatarsDialogId, dialogInterface.id);
                                            PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                                            PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                                            PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                                            if (PhotoViewer.this.imagesArrLocations.isEmpty() == null) {
                                                PhotoViewer.this.closePhoto(false, false);
                                            } else {
                                                dialogInterface = PhotoViewer.this.currentIndex;
                                                if (dialogInterface >= PhotoViewer.this.avatarsArr.size()) {
                                                    dialogInterface = PhotoViewer.this.avatarsArr.size() - 1;
                                                }
                                                PhotoViewer.this.currentIndex = -1;
                                                PhotoViewer.this.setImageIndex(dialogInterface, true);
                                            }
                                        }
                                    }
                                    z = false;
                                    if (z) {
                                        MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(null);
                                        PhotoViewer.this.closePhoto(false, false);
                                    } else if (dialogInterface != null) {
                                        i = new TL_inputPhoto();
                                        i.id = dialogInterface.id;
                                        i.access_hash = dialogInterface.access_hash;
                                        MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(i);
                                        MessagesStorage.getInstance(PhotoViewer.this.currentAccount).clearUserPhoto(PhotoViewer.this.avatarsDialogId, dialogInterface.id);
                                        PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                                        PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                                        PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                                        if (PhotoViewer.this.imagesArrLocations.isEmpty() == null) {
                                            dialogInterface = PhotoViewer.this.currentIndex;
                                            if (dialogInterface >= PhotoViewer.this.avatarsArr.size()) {
                                                dialogInterface = PhotoViewer.this.avatarsArr.size() - 1;
                                            }
                                            PhotoViewer.this.currentIndex = -1;
                                            PhotoViewer.this.setImageIndex(dialogInterface, true);
                                        } else {
                                            PhotoViewer.this.closePhoto(false, false);
                                        }
                                    }
                                }
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    PhotoViewer.this.showAlertDialog(builder);
                }
            } else if (i2 == 10) {
                PhotoViewer.this.onSharePressed();
            } else if (i2 == 11) {
                try {
                    AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                    PhotoViewer.this.closePhoto(false, false);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } else if (i2 == 13) {
                if (!(PhotoViewer.this.parentActivity == null || PhotoViewer.this.currentMessageObject == null || PhotoViewer.this.currentMessageObject.messageOwner.media == null)) {
                    if (PhotoViewer.this.currentMessageObject.messageOwner.media.photo != null) {
                        new StickersAlert(PhotoViewer.this.parentActivity, PhotoViewer.this.currentMessageObject.messageOwner.media.photo).show();
                    }
                }
            } else if (i2 == 5) {
                if (PhotoViewer.this.pipItem.getAlpha() == 1.0f) {
                    PhotoViewer.this.switchToPip();
                }
            } else if (i2 == 7 && PhotoViewer.this.currentMessageObject != null) {
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
        public boolean allowCaption() {
            return true;
        }

        public boolean allowGroupPhotos() {
            return true;
        }

        public boolean canScrollAway() {
            return true;
        }

        public boolean cancelButtonPressed() {
            return true;
        }

        public int getPhotoIndex(int i) {
            return -1;
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            return null;
        }

        public int getSelectedCount() {
            return 0;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return null;
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return null;
        }

        public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            return null;
        }

        public boolean isPhotoChecked(int i) {
            return false;
        }

        public void needAddMorePhotos() {
        }

        public boolean scaleToFill() {
            return false;
        }

        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo) {
        }

        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            return -1;
        }

        public void toggleGroupPhotosEnabled() {
        }

        public void updatePhotoAtIndex(int i) {
        }

        public void willHidePhotoViewer() {
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        }
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        private Paint paint = new Paint();

        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(null);
            this.paint.setColor(855638016);
        }

        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            int size2 = MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            measureChildWithMargins(PhotoViewer.this.captionEditText, i, 0, i2, 0);
            int measuredHeight = PhotoViewer.this.captionEditText.getMeasuredHeight();
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (childAt.getVisibility() != 8) {
                    if (childAt != PhotoViewer.this.captionEditText) {
                        if (childAt == PhotoViewer.this.aspectRatioFrameLayout) {
                            measureChildWithMargins(childAt, i, 0, MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0), NUM), 0);
                        } else if (!PhotoViewer.this.captionEditText.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), (size2 - measuredHeight) - AndroidUtilities.statusBarHeight), NUM));
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec((size2 - measuredHeight) - AndroidUtilities.statusBarHeight, NUM));
                        }
                    }
                }
            }
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            z = getChildCount();
            int emojiPadding = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
            for (boolean z2 = false; z2 < z; z2++) {
                View childAt = getChildAt(z2);
                if (childAt.getVisibility() != 8) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                    int measuredWidth = childAt.getMeasuredWidth();
                    int measuredHeight = childAt.getMeasuredHeight();
                    int i5 = layoutParams.gravity;
                    if (i5 == -1) {
                        i5 = 51;
                    }
                    int i6 = i5 & 7;
                    i5 &= 112;
                    i6 &= 7;
                    if (i6 == 1) {
                        i6 = ((((i3 - i) - measuredWidth) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                    } else if (i6 != 5) {
                        i6 = layoutParams.leftMargin;
                    } else {
                        i6 = ((i3 - i) - measuredWidth) - layoutParams.rightMargin;
                    }
                    int i7 = i5 != 16 ? i5 != 48 ? i5 != 80 ? layoutParams.topMargin : (((i4 - emojiPadding) - i2) - measuredHeight) - layoutParams.bottomMargin : layoutParams.topMargin : (((((i4 - emojiPadding) - i2) - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                    if (childAt == PhotoViewer.this.mentionListView) {
                        i7 -= PhotoViewer.this.captionEditText.getMeasuredHeight();
                    } else if (PhotoViewer.this.captionEditText.isPopupView(childAt)) {
                        if (AndroidUtilities.isInMultiwindow) {
                            i7 = (PhotoViewer.this.captionEditText.getTop() - childAt.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                        } else {
                            i7 = PhotoViewer.this.captionEditText.getBottom();
                        }
                    } else if (childAt == PhotoViewer.this.selectedPhotosListView) {
                        i7 = PhotoViewer.this.actionBar.getMeasuredHeight();
                    } else {
                        if (childAt != PhotoViewer.this.captionTextView) {
                            if (childAt != PhotoViewer.this.switchCaptionTextView) {
                                if (PhotoViewer.this.hintTextView != null && childAt == PhotoViewer.this.hintTextView) {
                                    i7 = PhotoViewer.this.selectedPhotosListView.getBottom() + AndroidUtilities.dp(3.0f);
                                } else if (childAt == PhotoViewer.this.cameraItem) {
                                    i7 = (PhotoViewer.this.pickerView.getTop() - AndroidUtilities.dp(15.0f)) - PhotoViewer.this.cameraItem.getMeasuredHeight();
                                }
                            }
                        }
                        i7 -= !PhotoViewer.this.groupedPhotosListView.currentPhotos.isEmpty() ? PhotoViewer.this.groupedPhotosListView.getMeasuredHeight() + 0 : 0;
                    }
                    childAt.layout(i6, i7, measuredWidth + i6, measuredHeight + i7);
                }
            }
            notifyHeightChanged();
        }

        protected void onDraw(Canvas canvas) {
            PhotoViewer.this.onDraw(canvas);
            if (VERSION.SDK_INT >= 21 && AndroidUtilities.statusBarHeight != 0 && PhotoViewer.this.actionBar != null) {
                this.paint.setAlpha((int) ((255.0f * PhotoViewer.this.actionBar.getAlpha()) * 0.2f));
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.statusBarHeight, this.paint);
            }
        }

        protected boolean drawChild(android.graphics.Canvas r5, android.view.View r6, long r7) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r4 = this;
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.mentionListView;
            r1 = 1;
            r2 = 0;
            if (r6 == r0) goto L_0x00c9;
        L_0x000a:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.captionEditText;
            if (r6 != r0) goto L_0x0014;
        L_0x0012:
            goto L_0x00c9;
        L_0x0014:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.cameraItem;
            if (r6 == r0) goto L_0x007c;
        L_0x001c:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.pickerView;
            if (r6 == r0) goto L_0x007c;
        L_0x0024:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.pickerViewSendButton;
            if (r6 == r0) goto L_0x007c;
        L_0x002c:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.captionTextView;
            if (r6 == r0) goto L_0x007c;
        L_0x0034:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.muteItem;
            r0 = r0.getVisibility();
            if (r0 != 0) goto L_0x0049;
        L_0x0040:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.bottomLayout;
            if (r6 != r0) goto L_0x0049;
        L_0x0048:
            goto L_0x007c;
        L_0x0049:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.checkImageView;
            if (r6 == r0) goto L_0x0063;
        L_0x0051:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.photosCounterView;
            if (r6 != r0) goto L_0x005a;
        L_0x0059:
            goto L_0x0063;
        L_0x005a:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.miniProgressView;
            if (r6 != r0) goto L_0x00f8;
        L_0x0062:
            return r2;
        L_0x0063:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.captionEditText;
            r0 = r0.getTag();
            if (r0 == 0) goto L_0x0075;
        L_0x006f:
            r5 = org.telegram.ui.PhotoViewer.this;
            r5.bottomTouchEnabled = r2;
            return r2;
        L_0x0075:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0.bottomTouchEnabled = r1;
            goto L_0x00f8;
        L_0x007c:
            r0 = r4.getKeyboardHeight();
            r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            if (r0 > r3) goto L_0x0097;
        L_0x0088:
            r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
            if (r0 != 0) goto L_0x0097;
        L_0x008c:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.captionEditText;
            r0 = r0.getEmojiPadding();
            goto L_0x0098;
        L_0x0097:
            r0 = r2;
        L_0x0098:
            r3 = org.telegram.ui.PhotoViewer.this;
            r3 = r3.captionEditText;
            r3 = r3.isPopupShowing();
            if (r3 != 0) goto L_0x00c3;
        L_0x00a4:
            r3 = org.telegram.messenger.AndroidUtilities.usingHardwareInput;
            if (r3 == 0) goto L_0x00b4;
        L_0x00a8:
            r3 = org.telegram.ui.PhotoViewer.this;
            r3 = r3.captionEditText;
            r3 = r3.getTag();
            if (r3 != 0) goto L_0x00c3;
        L_0x00b4:
            r3 = r4.getKeyboardHeight();
            if (r3 > 0) goto L_0x00c3;
        L_0x00ba:
            if (r0 == 0) goto L_0x00bd;
        L_0x00bc:
            goto L_0x00c3;
        L_0x00bd:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0.bottomTouchEnabled = r1;
            goto L_0x00f8;
        L_0x00c3:
            r5 = org.telegram.ui.PhotoViewer.this;
            r5.bottomTouchEnabled = r2;
            return r2;
        L_0x00c9:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.captionEditText;
            r0 = r0.isPopupShowing();
            if (r0 != 0) goto L_0x00f8;
        L_0x00d5:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.captionEditText;
            r0 = r0.getEmojiPadding();
            if (r0 != 0) goto L_0x00f8;
        L_0x00e1:
            r0 = org.telegram.messenger.AndroidUtilities.usingHardwareInput;
            if (r0 == 0) goto L_0x00f1;
        L_0x00e5:
            r0 = org.telegram.ui.PhotoViewer.this;
            r0 = r0.captionEditText;
            r0 = r0.getTag();
            if (r0 == 0) goto L_0x00f7;
        L_0x00f1:
            r0 = r4.getKeyboardHeight();
            if (r0 != 0) goto L_0x00f8;
        L_0x00f7:
            return r2;
        L_0x00f8:
            r0 = org.telegram.ui.PhotoViewer.this;	 Catch:{ Throwable -> 0x0109 }
            r0 = r0.aspectRatioFrameLayout;	 Catch:{ Throwable -> 0x0109 }
            if (r6 == r0) goto L_0x0107;	 Catch:{ Throwable -> 0x0109 }
        L_0x0100:
            r5 = super.drawChild(r5, r6, r7);	 Catch:{ Throwable -> 0x0109 }
            if (r5 == 0) goto L_0x0107;
        L_0x0106:
            goto L_0x0108;
        L_0x0107:
            r1 = r2;
        L_0x0108:
            return r1;
        L_0x0109:
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.FrameLayoutDrawer.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.PhotoViewer$ListAdapter$1 */
        class C16221 implements OnClickListener {
            C16221() {
            }

            public void onClick(View view) {
                view = PhotoViewer.this.imagesArrLocals.indexOf(((View) view.getParent()).getTag());
                if (view >= null) {
                    int photoChecked = PhotoViewer.this.placeProvider.setPhotoChecked(view, PhotoViewer.this.getCurrentVideoEditedInfo());
                    PhotoViewer.this.placeProvider.isPhotoChecked(view);
                    if (view == PhotoViewer.this.currentIndex) {
                        PhotoViewer.this.checkImageView.setChecked(-1, false, true);
                    }
                    if (photoChecked >= 0) {
                        if (PhotoViewer.this.placeProvider.allowGroupPhotos() != null) {
                            photoChecked++;
                        }
                        PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(photoChecked);
                    }
                    PhotoViewer.this.updateSelectedCount();
                }
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
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

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new ImageView(this.mContext) {
                    protected void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i2), NUM));
                    }
                };
                viewGroup.setScaleType(ScaleType.CENTER);
                viewGroup.setImageResource(C0446R.drawable.photos_group);
            } else {
                viewGroup = new PhotoPickerPhotoCell(this.mContext, false);
                viewGroup.checkFrame.setOnClickListener(new C16221());
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ColorFilter colorFilter = null;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) viewHolder.itemView;
                    photoPickerPhotoCell.itemWidth = AndroidUtilities.dp(82.0f);
                    BackupImageView backupImageView = photoPickerPhotoCell.photoImage;
                    backupImageView.setOrientation(0, true);
                    ArrayList selectedPhotosOrder = PhotoViewer.this.placeProvider.getSelectedPhotosOrder();
                    if (PhotoViewer.this.placeProvider.allowGroupPhotos()) {
                        i--;
                    }
                    i = PhotoViewer.this.placeProvider.getSelectedPhotos().get(selectedPhotosOrder.get(i));
                    if (i instanceof PhotoEntry) {
                        PhotoEntry photoEntry = (PhotoEntry) i;
                        photoPickerPhotoCell.setTag(photoEntry);
                        photoPickerPhotoCell.videoInfoContainer.setVisibility(4);
                        if (photoEntry.thumbPath != null) {
                            backupImageView.setImage(photoEntry.thumbPath, null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                        } else if (photoEntry.path != null) {
                            backupImageView.setOrientation(photoEntry.orientation, true);
                            StringBuilder stringBuilder;
                            if (photoEntry.isVideo) {
                                photoPickerPhotoCell.videoInfoContainer.setVisibility(0);
                                int i2 = photoEntry.duration - ((photoEntry.duration / 60) * 60);
                                photoPickerPhotoCell.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(r4), Integer.valueOf(i2)}));
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("vthumb://");
                                stringBuilder.append(photoEntry.imageId);
                                stringBuilder.append(":");
                                stringBuilder.append(photoEntry.path);
                                backupImageView.setImage(stringBuilder.toString(), null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                            } else {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("thumb://");
                                stringBuilder.append(photoEntry.imageId);
                                stringBuilder.append(":");
                                stringBuilder.append(photoEntry.path);
                                backupImageView.setImage(stringBuilder.toString(), null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                            }
                        } else {
                            backupImageView.setImageResource(C0446R.drawable.nophotos);
                        }
                        photoPickerPhotoCell.setChecked(-1, true, false);
                        photoPickerPhotoCell.checkBox.setVisibility(0);
                        return;
                    } else if (i instanceof SearchImage) {
                        SearchImage searchImage = (SearchImage) i;
                        photoPickerPhotoCell.setTag(searchImage);
                        if (searchImage.thumbPath != null) {
                            backupImageView.setImage(searchImage.thumbPath, null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                        } else if (searchImage.thumbUrl != null && searchImage.thumbUrl.length() > 0) {
                            backupImageView.setImage(searchImage.thumbUrl, null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                        } else if (searchImage.document == null || searchImage.document.thumb == null) {
                            backupImageView.setImageResource(C0446R.drawable.nophotos);
                        } else {
                            backupImageView.setImage(searchImage.document.thumb.location, null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                        }
                        photoPickerPhotoCell.videoInfoContainer.setVisibility(4);
                        photoPickerPhotoCell.setChecked(-1, true, false);
                        photoPickerPhotoCell.checkBox.setVisibility(0);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    ImageView imageView = (ImageView) viewHolder.itemView;
                    if (SharedConfig.groupPhotosEnabled != 0) {
                        colorFilter = new PorterDuffColorFilter(-10043398, Mode.MULTIPLY);
                    }
                    imageView.setColorFilter(colorFilter);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            return (i != 0 || PhotoViewer.this.placeProvider.allowGroupPhotos() == 0) ? 0 : 1;
        }
    }

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public static PhotoViewer getPipInstance() {
        return PipInstance;
    }

    public static PhotoViewer getInstance() {
        PhotoViewer photoViewer = Instance;
        if (photoViewer == null) {
            synchronized (PhotoViewer.class) {
                photoViewer = Instance;
                if (photoViewer == null) {
                    photoViewer = new PhotoViewer();
                    Instance = photoViewer;
                }
            }
        }
        return photoViewer;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public PhotoViewer() {
        this.blackPaint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        PhotoViewer photoViewer = this;
        int i3 = i;
        float f = 1.0f;
        int i4 = 3;
        boolean z = true;
        int i5 = 0;
        String str;
        if (i3 == NotificationCenter.FileDidFailedLoad) {
            str = (String) objArr[0];
            while (i5 < 3) {
                if (photoViewer.currentFileNames[i5] != null && photoViewer.currentFileNames[i5].equals(str)) {
                    photoViewer.photoProgressViews[i5].setProgress(1.0f, true);
                    checkProgress(i5, true);
                    break;
                }
                i5++;
            }
        } else if (i3 == NotificationCenter.FileDidLoaded) {
            str = (String) objArr[0];
            int i6 = 0;
            while (i6 < 3) {
                if (photoViewer.currentFileNames[i6] == null || !photoViewer.currentFileNames[i6].equals(str)) {
                    i6++;
                } else {
                    photoViewer.photoProgressViews[i6].setProgress(1.0f, true);
                    checkProgress(i6, true);
                    if (photoViewer.videoPlayer == null && i6 == 0 && ((photoViewer.currentMessageObject != null && photoViewer.currentMessageObject.isVideo()) || (photoViewer.currentBotInlineResult != null && (photoViewer.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(photoViewer.currentBotInlineResult.document))))) {
                        onActionClick(false);
                    }
                    if (i6 == 0 && photoViewer.videoPlayer != null) {
                        photoViewer.currentVideoFinishedLoading = true;
                    }
                }
            }
        } else if (i3 == NotificationCenter.FileLoadProgressChanged) {
            str = (String) objArr[0];
            r3 = 0;
            while (r3 < i4) {
                if (photoViewer.currentFileNames[r3] != null && photoViewer.currentFileNames[r3].equals(str)) {
                    Float f2 = (Float) objArr[z];
                    photoViewer.photoProgressViews[r3].setProgress(f2.floatValue(), z);
                    if (!(r3 != 0 || photoViewer.videoPlayer == null || photoViewer.videoPlayerSeekbar == null)) {
                        if (!photoViewer.currentVideoFinishedLoading) {
                            long uptimeMillis = SystemClock.uptimeMillis();
                            if (Math.abs(uptimeMillis - photoViewer.lastBufferedPositionCheck) >= 500) {
                                long j;
                                float bufferedProgressFromPosition;
                                long j2;
                                float f3 = 0.0f;
                                if (photoViewer.seekToProgressPending == 0.0f) {
                                    long duration = photoViewer.videoPlayer.getDuration();
                                    j = uptimeMillis;
                                    long currentPosition = photoViewer.videoPlayer.getCurrentPosition();
                                    if (duration >= 0 && duration != C0542C.TIME_UNSET && currentPosition >= 0) {
                                        f3 = ((float) currentPosition) / ((float) duration);
                                    }
                                } else {
                                    j = uptimeMillis;
                                    f3 = photoViewer.seekToProgressPending;
                                }
                                if (photoViewer.isStreaming) {
                                    bufferedProgressFromPosition = FileLoader.getInstance(photoViewer.currentAccount).getBufferedProgressFromPosition(f3, photoViewer.currentFileNames[0]);
                                    j2 = j;
                                } else {
                                    j2 = j;
                                    bufferedProgressFromPosition = 1.0f;
                                }
                                photoViewer.lastBufferedPositionCheck = j2;
                                f = bufferedProgressFromPosition;
                            } else {
                                f = -1.0f;
                            }
                        }
                        if (f != -1.0f) {
                            photoViewer.videoPlayerSeekbar.setBufferedProgress(f);
                            if (photoViewer.pipVideoView != null) {
                                photoViewer.pipVideoView.setBufferedProgress(f);
                            }
                            photoViewer.videoPlayerControlFrameLayout.invalidate();
                        }
                        checkBufferedProgress(f2.floatValue());
                    }
                }
                r3++;
                f = 1.0f;
                i4 = 3;
                z = true;
            }
        } else if (i3 == NotificationCenter.dialogPhotosLoaded) {
            i3 = ((Integer) objArr[3]).intValue();
            if (photoViewer.avatarsDialogId == ((Integer) objArr[0]).intValue() && photoViewer.classGuid == i3) {
                r1 = ((Boolean) objArr[2]).booleanValue();
                ArrayList arrayList = (ArrayList) objArr[4];
                if (!arrayList.isEmpty()) {
                    photoViewer.imagesArrLocations.clear();
                    photoViewer.imagesArrLocationsSizes.clear();
                    photoViewer.avatarsArr.clear();
                    r4 = -1;
                    for (r3 = 0; r3 < arrayList.size(); r3++) {
                        Photo photo = (Photo) arrayList.get(r3);
                        if (!(photo == null || (photo instanceof TL_photoEmpty))) {
                            if (photo.sizes != null) {
                                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 640);
                                if (closestPhotoSizeWithSize != null) {
                                    if (r4 == -1 && photoViewer.currentFileLocation != null) {
                                        for (int i7 = 0; i7 < photo.sizes.size(); i7++) {
                                            PhotoSize photoSize = (PhotoSize) photo.sizes.get(i7);
                                            if (photoSize.location.local_id == photoViewer.currentFileLocation.local_id && photoSize.location.volume_id == photoViewer.currentFileLocation.volume_id) {
                                                r4 = photoViewer.imagesArrLocations.size();
                                                break;
                                            }
                                        }
                                    }
                                    photoViewer.imagesArrLocations.add(closestPhotoSizeWithSize.location);
                                    photoViewer.imagesArrLocationsSizes.add(Integer.valueOf(closestPhotoSizeWithSize.size));
                                    photoViewer.avatarsArr.add(photo);
                                }
                            }
                        }
                    }
                    if (photoViewer.avatarsArr.isEmpty()) {
                        photoViewer.menuItem.hideSubItem(6);
                    } else {
                        photoViewer.menuItem.showSubItem(6);
                    }
                    photoViewer.needSearchImageInArr = false;
                    photoViewer.currentIndex = -1;
                    if (r4 != -1) {
                        setImageIndex(r4, true);
                    } else {
                        photoViewer.avatarsArr.add(0, new TL_photoEmpty());
                        photoViewer.imagesArrLocations.add(0, photoViewer.currentFileLocation);
                        photoViewer.imagesArrLocationsSizes.add(0, Integer.valueOf(0));
                        setImageIndex(0, true);
                    }
                    if (r1) {
                        MessagesController.getInstance(photoViewer.currentAccount).loadDialogPhotos(photoViewer.avatarsDialogId, 80, 0, false, photoViewer.classGuid);
                    }
                }
            }
        } else if (i3 == NotificationCenter.mediaCountDidLoaded) {
            r3 = ((Long) objArr[0]).longValue();
            if (r3 == photoViewer.currentDialogId || r3 == photoViewer.mergeDialogId) {
                if (r3 == photoViewer.currentDialogId) {
                    r1 = true;
                    photoViewer.totalImagesCount = ((Integer) objArr[1]).intValue();
                } else {
                    r1 = true;
                    if (r3 == photoViewer.mergeDialogId) {
                        photoViewer.totalImagesCountMerge = ((Integer) objArr[1]).intValue();
                    }
                }
                if (photoViewer.needSearchImageInArr && photoViewer.isFirstLoading) {
                    photoViewer.isFirstLoading = false;
                    photoViewer.loadingMoreImages = r1;
                    DataQuery.getInstance(photoViewer.currentAccount).loadMedia(photoViewer.currentDialogId, 80, 0, 0, true, photoViewer.classGuid);
                } else if (!photoViewer.imagesArr.isEmpty()) {
                    if (photoViewer.opennedFromMedia) {
                        photoViewer.actionBar.setTitle(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf(photoViewer.currentIndex + 1), Integer.valueOf(photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge)));
                    } else {
                        photoViewer.actionBar.setTitle(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf((((photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge) - photoViewer.imagesArr.size()) + photoViewer.currentIndex) + 1), Integer.valueOf(photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge)));
                    }
                }
            }
        } else if (i3 == NotificationCenter.mediaDidLoaded) {
            r3 = ((Long) objArr[0]).longValue();
            i3 = ((Integer) objArr[3]).intValue();
            if ((r3 == photoViewer.currentDialogId || r3 == photoViewer.mergeDialogId) && i3 == photoViewer.classGuid) {
                photoViewer.loadingMoreImages = false;
                i3 = r3 == photoViewer.currentDialogId ? 0 : 1;
                ArrayList arrayList2 = (ArrayList) objArr[2];
                photoViewer.endReached[i3] = ((Boolean) objArr[5]).booleanValue();
                if (!photoViewer.needSearchImageInArr) {
                    Iterator it = arrayList2.iterator();
                    r3 = 0;
                    while (it.hasNext()) {
                        MessageObject messageObject = (MessageObject) it.next();
                        if (photoViewer.imagesByIds[i3].indexOfKey(messageObject.getId()) < 0) {
                            r3++;
                            if (photoViewer.opennedFromMedia) {
                                photoViewer.imagesArr.add(messageObject);
                            } else {
                                photoViewer.imagesArr.add(0, messageObject);
                            }
                            photoViewer.imagesByIds[i3].put(messageObject.getId(), messageObject);
                        }
                    }
                    if (photoViewer.opennedFromMedia) {
                        if (r3 == 0) {
                            photoViewer.totalImagesCount = photoViewer.imagesArr.size();
                            photoViewer.totalImagesCountMerge = 0;
                        }
                    } else if (r3 != 0) {
                        i3 = photoViewer.currentIndex;
                        photoViewer.currentIndex = -1;
                        setImageIndex(i3 + r3, true);
                    } else {
                        photoViewer.totalImagesCount = photoViewer.imagesArr.size();
                        photoViewer.totalImagesCountMerge = 0;
                    }
                } else if (!arrayList2.isEmpty() || (i3 == 0 && photoViewer.mergeDialogId != 0)) {
                    MessageObject messageObject2 = (MessageObject) photoViewer.imagesArr.get(photoViewer.currentIndex);
                    int i8 = -1;
                    i5 = 0;
                    for (r4 = 0; r4 < arrayList2.size(); r4++) {
                        MessageObject messageObject3 = (MessageObject) arrayList2.get(r4);
                        if (photoViewer.imagesByIdsTemp[i3].indexOfKey(messageObject3.getId()) < 0) {
                            photoViewer.imagesByIdsTemp[i3].put(messageObject3.getId(), messageObject3);
                            if (photoViewer.opennedFromMedia) {
                                photoViewer.imagesArrTemp.add(messageObject3);
                                if (messageObject3.getId() == messageObject2.getId()) {
                                    i8 = i5;
                                }
                                i5++;
                            } else {
                                i5++;
                                photoViewer.imagesArrTemp.add(0, messageObject3);
                                if (messageObject3.getId() == messageObject2.getId()) {
                                    i8 = arrayList2.size() - i5;
                                }
                            }
                        }
                    }
                    if (i5 == 0 && (i3 != 0 || photoViewer.mergeDialogId == 0)) {
                        photoViewer.totalImagesCount = photoViewer.imagesArr.size();
                        photoViewer.totalImagesCountMerge = 0;
                    }
                    if (i8 != -1) {
                        boolean z2;
                        photoViewer.imagesArr.clear();
                        photoViewer.imagesArr.addAll(photoViewer.imagesArrTemp);
                        for (i3 = 0; i3 < 2; i3++) {
                            photoViewer.imagesByIds[i3] = photoViewer.imagesByIdsTemp[i3].clone();
                            photoViewer.imagesByIdsTemp[i3].clear();
                        }
                        photoViewer.imagesArrTemp.clear();
                        photoViewer.needSearchImageInArr = false;
                        photoViewer.currentIndex = -1;
                        if (i8 >= photoViewer.imagesArr.size()) {
                            z2 = true;
                            i8 = photoViewer.imagesArr.size() - 1;
                        } else {
                            z2 = true;
                        }
                        setImageIndex(i8, z2);
                    } else {
                        int i9;
                        if (photoViewer.opennedFromMedia) {
                            i5 = photoViewer.imagesArrTemp.isEmpty() ? 0 : ((MessageObject) photoViewer.imagesArrTemp.get(photoViewer.imagesArrTemp.size() - 1)).getId();
                            if (i3 == 0 && photoViewer.endReached[i3] && photoViewer.mergeDialogId != 0) {
                                if (!photoViewer.imagesArrTemp.isEmpty()) {
                                }
                                i9 = i5;
                                i3 = 1;
                                if (!photoViewer.endReached[i3]) {
                                    photoViewer.loadingMoreImages = true;
                                    if (photoViewer.opennedFromMedia) {
                                        DataQuery.getInstance(photoViewer.currentAccount).loadMedia(i3 == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i9, 0, true, photoViewer.classGuid);
                                    } else {
                                        DataQuery.getInstance(photoViewer.currentAccount).loadMedia(i3 == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i9, 0, true, photoViewer.classGuid);
                                    }
                                }
                            }
                            i9 = i5;
                            if (!photoViewer.endReached[i3]) {
                                photoViewer.loadingMoreImages = true;
                                if (photoViewer.opennedFromMedia) {
                                    if (i3 == 0) {
                                    }
                                    DataQuery.getInstance(photoViewer.currentAccount).loadMedia(i3 == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i9, 0, true, photoViewer.classGuid);
                                } else {
                                    if (i3 == 0) {
                                    }
                                    DataQuery.getInstance(photoViewer.currentAccount).loadMedia(i3 == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i9, 0, true, photoViewer.classGuid);
                                }
                            }
                        } else {
                            i5 = photoViewer.imagesArrTemp.isEmpty() ? 0 : ((MessageObject) photoViewer.imagesArrTemp.get(0)).getId();
                            if (i3 == 0 && photoViewer.endReached[i3] && photoViewer.mergeDialogId != 0) {
                                if (!(photoViewer.imagesArrTemp.isEmpty() || ((MessageObject) photoViewer.imagesArrTemp.get(0)).getDialogId() == photoViewer.mergeDialogId)) {
                                }
                                i9 = i5;
                                i3 = 1;
                                if (!photoViewer.endReached[i3]) {
                                    photoViewer.loadingMoreImages = true;
                                    if (photoViewer.opennedFromMedia) {
                                        if (i3 == 0) {
                                        }
                                        DataQuery.getInstance(photoViewer.currentAccount).loadMedia(i3 == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i9, 0, true, photoViewer.classGuid);
                                    } else {
                                        if (i3 == 0) {
                                        }
                                        DataQuery.getInstance(photoViewer.currentAccount).loadMedia(i3 == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i9, 0, true, photoViewer.classGuid);
                                    }
                                }
                            }
                            i9 = i5;
                            if (!photoViewer.endReached[i3]) {
                                photoViewer.loadingMoreImages = true;
                                if (photoViewer.opennedFromMedia) {
                                    if (i3 == 0) {
                                    }
                                    DataQuery.getInstance(photoViewer.currentAccount).loadMedia(i3 == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i9, 0, true, photoViewer.classGuid);
                                } else {
                                    if (i3 == 0) {
                                    }
                                    DataQuery.getInstance(photoViewer.currentAccount).loadMedia(i3 == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i9, 0, true, photoViewer.classGuid);
                                }
                            }
                        }
                        i3 = 1;
                        i9 = 0;
                        if (!photoViewer.endReached[i3]) {
                            photoViewer.loadingMoreImages = true;
                            if (photoViewer.opennedFromMedia) {
                                if (i3 == 0) {
                                }
                                DataQuery.getInstance(photoViewer.currentAccount).loadMedia(i3 == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i9, 0, true, photoViewer.classGuid);
                            } else {
                                if (i3 == 0) {
                                }
                                DataQuery.getInstance(photoViewer.currentAccount).loadMedia(i3 == 0 ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, i9, 0, true, photoViewer.classGuid);
                            }
                        }
                    }
                } else {
                    photoViewer.needSearchImageInArr = false;
                }
            }
        } else if (i3 == NotificationCenter.emojiDidLoaded) {
            if (photoViewer.captionTextView != null) {
                photoViewer.captionTextView.invalidate();
            }
        } else if (i3 == NotificationCenter.FilePreparingFailed) {
            MessageObject messageObject4 = (MessageObject) objArr[0];
            if (photoViewer.loadInitialVideo) {
                photoViewer.loadInitialVideo = false;
                photoViewer.progressView.setVisibility(4);
                preparePlayer(photoViewer.currentPlayingVideoFile, false, false);
            } else if (photoViewer.tryStartRequestPreviewOnFinish) {
                releasePlayer();
                photoViewer.tryStartRequestPreviewOnFinish = MediaController.getInstance().scheduleVideoConvert(photoViewer.videoPreviewMessageObject, true) ^ true;
            } else if (messageObject4 == photoViewer.videoPreviewMessageObject) {
                photoViewer.requestingPreview = false;
                photoViewer.progressView.setVisibility(4);
            }
        } else if (i3 == NotificationCenter.FileNewChunkAvailable && ((MessageObject) objArr[0]) == photoViewer.videoPreviewMessageObject) {
            String str2 = (String) objArr[1];
            if (((Long) objArr[3]).longValue() != 0) {
                photoViewer.requestingPreview = false;
                photoViewer.progressView.setVisibility(4);
                preparePlayer(Uri.fromFile(new File(str2)), false, true);
            }
        }
    }

    private void showDownloadAlert() {
        Builder builder = new Builder(this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
        int i = 0;
        if (this.currentMessageObject != null && this.currentMessageObject.isVideo() && FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingFile(this.currentFileNames[0])) {
            i = 1;
        }
        if (i != 0) {
            builder.setMessage(LocaleController.getString("PleaseStreamDownload", C0446R.string.PleaseStreamDownload));
        } else {
            builder.setMessage(LocaleController.getString("PleaseDownload", C0446R.string.PleaseDownload));
        }
        showAlertDialog(builder);
    }

    private void onSharePressed() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r6 = this;
        r0 = r6.parentActivity;
        if (r0 == 0) goto L_0x00c7;
    L_0x0004:
        r0 = r6.allowShare;
        if (r0 != 0) goto L_0x000a;
    L_0x0008:
        goto L_0x00c7;
    L_0x000a:
        r0 = r6.currentMessageObject;	 Catch:{ Exception -> 0x00c2 }
        r1 = 1;	 Catch:{ Exception -> 0x00c2 }
        r2 = 0;	 Catch:{ Exception -> 0x00c2 }
        r3 = 0;	 Catch:{ Exception -> 0x00c2 }
        if (r0 == 0) goto L_0x0041;	 Catch:{ Exception -> 0x00c2 }
    L_0x0011:
        r0 = r6.currentMessageObject;	 Catch:{ Exception -> 0x00c2 }
        r2 = r0.isVideo();	 Catch:{ Exception -> 0x00c2 }
        r0 = r6.currentMessageObject;	 Catch:{ Exception -> 0x00c2 }
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x00c2 }
        r0 = r0.attachPath;	 Catch:{ Exception -> 0x00c2 }
        r0 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x00c2 }
        if (r0 != 0) goto L_0x0036;	 Catch:{ Exception -> 0x00c2 }
    L_0x0023:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x00c2 }
        r4 = r6.currentMessageObject;	 Catch:{ Exception -> 0x00c2 }
        r4 = r4.messageOwner;	 Catch:{ Exception -> 0x00c2 }
        r4 = r4.attachPath;	 Catch:{ Exception -> 0x00c2 }
        r0.<init>(r4);	 Catch:{ Exception -> 0x00c2 }
        r4 = r0.exists();	 Catch:{ Exception -> 0x00c2 }
        if (r4 != 0) goto L_0x0035;	 Catch:{ Exception -> 0x00c2 }
    L_0x0034:
        goto L_0x0036;	 Catch:{ Exception -> 0x00c2 }
    L_0x0035:
        r3 = r0;	 Catch:{ Exception -> 0x00c2 }
    L_0x0036:
        if (r3 != 0) goto L_0x0057;	 Catch:{ Exception -> 0x00c2 }
    L_0x0038:
        r0 = r6.currentMessageObject;	 Catch:{ Exception -> 0x00c2 }
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x00c2 }
        r3 = org.telegram.messenger.FileLoader.getPathToMessage(r0);	 Catch:{ Exception -> 0x00c2 }
        goto L_0x0057;	 Catch:{ Exception -> 0x00c2 }
    L_0x0041:
        r0 = r6.currentFileLocation;	 Catch:{ Exception -> 0x00c2 }
        if (r0 == 0) goto L_0x0057;	 Catch:{ Exception -> 0x00c2 }
    L_0x0045:
        r0 = r6.currentFileLocation;	 Catch:{ Exception -> 0x00c2 }
        r3 = r6.avatarsDialogId;	 Catch:{ Exception -> 0x00c2 }
        if (r3 != 0) goto L_0x0052;	 Catch:{ Exception -> 0x00c2 }
    L_0x004b:
        r3 = r6.isEvent;	 Catch:{ Exception -> 0x00c2 }
        if (r3 == 0) goto L_0x0050;	 Catch:{ Exception -> 0x00c2 }
    L_0x004f:
        goto L_0x0052;	 Catch:{ Exception -> 0x00c2 }
    L_0x0050:
        r3 = r2;	 Catch:{ Exception -> 0x00c2 }
        goto L_0x0053;	 Catch:{ Exception -> 0x00c2 }
    L_0x0052:
        r3 = r1;	 Catch:{ Exception -> 0x00c2 }
    L_0x0053:
        r3 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r3);	 Catch:{ Exception -> 0x00c2 }
    L_0x0057:
        r0 = r3.exists();	 Catch:{ Exception -> 0x00c2 }
        if (r0 == 0) goto L_0x00be;	 Catch:{ Exception -> 0x00c2 }
    L_0x005d:
        r0 = new android.content.Intent;	 Catch:{ Exception -> 0x00c2 }
        r4 = "android.intent.action.SEND";	 Catch:{ Exception -> 0x00c2 }
        r0.<init>(r4);	 Catch:{ Exception -> 0x00c2 }
        if (r2 == 0) goto L_0x006c;	 Catch:{ Exception -> 0x00c2 }
    L_0x0066:
        r2 = "video/mp4";	 Catch:{ Exception -> 0x00c2 }
        r0.setType(r2);	 Catch:{ Exception -> 0x00c2 }
        goto L_0x007f;	 Catch:{ Exception -> 0x00c2 }
    L_0x006c:
        r2 = r6.currentMessageObject;	 Catch:{ Exception -> 0x00c2 }
        if (r2 == 0) goto L_0x007a;	 Catch:{ Exception -> 0x00c2 }
    L_0x0070:
        r2 = r6.currentMessageObject;	 Catch:{ Exception -> 0x00c2 }
        r2 = r2.getMimeType();	 Catch:{ Exception -> 0x00c2 }
        r0.setType(r2);	 Catch:{ Exception -> 0x00c2 }
        goto L_0x007f;	 Catch:{ Exception -> 0x00c2 }
    L_0x007a:
        r2 = "image/jpeg";	 Catch:{ Exception -> 0x00c2 }
        r0.setType(r2);	 Catch:{ Exception -> 0x00c2 }
    L_0x007f:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00c2 }
        r4 = 24;
        if (r2 < r4) goto L_0x00a0;
    L_0x0085:
        r2 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x0096 }
        r4 = r6.parentActivity;	 Catch:{ Exception -> 0x0096 }
        r5 = "org.telegram.messenger.provider";	 Catch:{ Exception -> 0x0096 }
        r4 = android.support.v4.content.FileProvider.getUriForFile(r4, r5, r3);	 Catch:{ Exception -> 0x0096 }
        r0.putExtra(r2, r4);	 Catch:{ Exception -> 0x0096 }
        r0.setFlags(r1);	 Catch:{ Exception -> 0x0096 }
        goto L_0x00a9;
    L_0x0096:
        r1 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x00c2 }
        r2 = android.net.Uri.fromFile(r3);	 Catch:{ Exception -> 0x00c2 }
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x00c2 }
        goto L_0x00a9;	 Catch:{ Exception -> 0x00c2 }
    L_0x00a0:
        r1 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x00c2 }
        r2 = android.net.Uri.fromFile(r3);	 Catch:{ Exception -> 0x00c2 }
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x00c2 }
    L_0x00a9:
        r1 = r6.parentActivity;	 Catch:{ Exception -> 0x00c2 }
        r2 = "ShareFile";	 Catch:{ Exception -> 0x00c2 }
        r3 = NUM; // 0x7f0c05ef float:1.8612273E38 double:1.053098149E-314;	 Catch:{ Exception -> 0x00c2 }
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Exception -> 0x00c2 }
        r0 = android.content.Intent.createChooser(r0, r2);	 Catch:{ Exception -> 0x00c2 }
        r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;	 Catch:{ Exception -> 0x00c2 }
        r1.startActivityForResult(r0, r2);	 Catch:{ Exception -> 0x00c2 }
        goto L_0x00c6;	 Catch:{ Exception -> 0x00c2 }
    L_0x00be:
        r6.showDownloadAlert();	 Catch:{ Exception -> 0x00c2 }
        goto L_0x00c6;
    L_0x00c2:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x00c6:
        return;
    L_0x00c7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onSharePressed():void");
    }

    private void setScaleToFill() {
        float bitmapWidth = (float) this.centerImage.getBitmapWidth();
        float containerViewWidth = (float) getContainerViewWidth();
        float bitmapHeight = (float) this.centerImage.getBitmapHeight();
        float containerViewHeight = (float) getContainerViewHeight();
        float min = Math.min(containerViewHeight / bitmapHeight, containerViewWidth / bitmapWidth);
        this.scale = Math.max(containerViewWidth / ((float) ((int) (bitmapWidth * min))), containerViewHeight / ((float) ((int) (bitmapHeight * min))));
        updateMinMax(this.scale);
    }

    public void setParentAlert(ChatAttachAlert chatAttachAlert) {
        this.parentAlert = chatAttachAlert;
    }

    public void setParentActivity(Activity activity) {
        Context context = activity;
        this.currentAccount = UserConfig.selectedAccount;
        this.centerImage.setCurrentAccount(this.currentAccount);
        this.leftImage.setCurrentAccount(this.currentAccount);
        this.rightImage.setCurrentAccount(this.currentAccount);
        if (this.parentActivity != context) {
            int i;
            float f;
            FrameLayout.LayoutParams layoutParams;
            float f2;
            FrameLayout.LayoutParams layoutParams2;
            RecyclerListView recyclerListView;
            Adapter listAdapter;
            Adapter mentionsAdapter;
            r0.parentActivity = context;
            r0.actvityContext = new ContextThemeWrapper(r0.parentActivity, C0446R.style.Theme.TMessages);
            if (progressDrawables == null) {
                progressDrawables = new Drawable[4];
                progressDrawables[0] = r0.parentActivity.getResources().getDrawable(C0446R.drawable.circle_big);
                progressDrawables[1] = r0.parentActivity.getResources().getDrawable(C0446R.drawable.cancel_big);
                progressDrawables[2] = r0.parentActivity.getResources().getDrawable(C0446R.drawable.load_big);
                progressDrawables[3] = r0.parentActivity.getResources().getDrawable(C0446R.drawable.play_big);
            }
            r0.scroller = new Scroller(context);
            r0.windowView = new FrameLayout(context) {
                private Runnable attachRunnable;

                /* renamed from: org.telegram.ui.PhotoViewer$5$1 */
                class C16051 implements Runnable {
                    C16051() {
                    }

                    public void run() {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.checkImageView.getLayoutParams();
                        ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
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

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return (!PhotoViewer.this.isVisible || super.onInterceptTouchEvent(motionEvent) == null) ? null : true;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return (!PhotoViewer.this.isVisible || PhotoViewer.this.onTouchEvent(motionEvent) == null) ? null : true;
                }

                protected boolean drawChild(Canvas canvas, View view, long j) {
                    j = super.drawChild(canvas, view, j);
                    if (VERSION.SDK_INT >= 21 && view == PhotoViewer.this.animatingImageView && PhotoViewer.this.lastInsets != null) {
                        Canvas canvas2 = canvas;
                        canvas2.drawRect(0.0f, (float) getMeasuredHeight(), (float) getMeasuredWidth(), (float) (getMeasuredHeight() + ((WindowInsets) PhotoViewer.this.lastInsets).getSystemWindowInsetBottom()), PhotoViewer.this.blackPaint);
                    }
                    return j;
                }

                protected void onMeasure(int i, int i2) {
                    i = MeasureSpec.getSize(i);
                    i2 = MeasureSpec.getSize(i2);
                    if (VERSION.SDK_INT >= 21 && PhotoViewer.this.lastInsets != null) {
                        WindowInsets windowInsets = (WindowInsets) PhotoViewer.this.lastInsets;
                        if (AndroidUtilities.incorrectDisplaySizeFix) {
                            if (i2 > AndroidUtilities.displaySize.y) {
                                i2 = AndroidUtilities.displaySize.y;
                            }
                            i2 += AndroidUtilities.statusBarHeight;
                        }
                        i2 -= windowInsets.getSystemWindowInsetBottom();
                        i -= windowInsets.getSystemWindowInsetRight();
                    } else if (i2 > AndroidUtilities.displaySize.y) {
                        i2 = AndroidUtilities.displaySize.y;
                    }
                    setMeasuredDimension(i, i2);
                    if (VERSION.SDK_INT >= 21 && PhotoViewer.this.lastInsets != null) {
                        i -= ((WindowInsets) PhotoViewer.this.lastInsets).getSystemWindowInsetLeft();
                    }
                    ViewGroup.LayoutParams layoutParams = PhotoViewer.this.animatingImageView.getLayoutParams();
                    PhotoViewer.this.animatingImageView.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
                    PhotoViewer.this.containerView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                }

                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    i = (VERSION.SDK_INT < 21 || PhotoViewer.this.lastInsets == 0) ? 0 : ((WindowInsets) PhotoViewer.this.lastInsets).getSystemWindowInsetLeft() + 0;
                    PhotoViewer.this.animatingImageView.layout(i, 0, PhotoViewer.this.animatingImageView.getMeasuredWidth() + i, PhotoViewer.this.animatingImageView.getMeasuredHeight());
                    PhotoViewer.this.containerView.layout(i, 0, PhotoViewer.this.containerView.getMeasuredWidth() + i, PhotoViewer.this.containerView.getMeasuredHeight());
                    PhotoViewer.this.wasLayout = 1;
                    if (z) {
                        if (!PhotoViewer.this.dontResetZoomOnFirstLayout) {
                            PhotoViewer.this.scale = NUM;
                            PhotoViewer.this.translationX = 0.0f;
                            PhotoViewer.this.translationY = 0.0f;
                            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                        }
                        if (PhotoViewer.this.checkImageView) {
                            PhotoViewer.this.checkImageView.post(new C16051());
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

                public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
                    if (keyEvent == null || keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 1) {
                        return super.dispatchKeyEventPreIme(keyEvent);
                    }
                    if (PhotoViewer.this.captionEditText.isPopupShowing() == null) {
                        if (PhotoViewer.this.captionEditText.isKeyboardVisible() == null) {
                            PhotoViewer.getInstance().closePhoto(true, false);
                            return true;
                        }
                    }
                    PhotoViewer.this.closeCaptionEnter(false);
                    return false;
                }

                public ActionMode startActionModeForChild(View view, Callback callback, int i) {
                    if (VERSION.SDK_INT >= 23) {
                        View findViewById = PhotoViewer.this.parentActivity.findViewById(16908290);
                        if (findViewById instanceof ViewGroup) {
                            try {
                                return ((ViewGroup) findViewById).startActionModeForChild(view, callback, i);
                            } catch (Throwable th) {
                                FileLog.m3e(th);
                            }
                        }
                    }
                    return super.startActionModeForChild(view, callback, i);
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
                r0.containerView.setOnApplyWindowInsetsListener(new C16146());
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
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    PhotoViewer.this.containerView.invalidate();
                }
            };
            r0.actionBar.setTitleColor(-1);
            r0.actionBar.setSubtitleColor(-1);
            r0.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.actionBar.setOccupyStatusBar(VERSION.SDK_INT >= 21);
            r0.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
            r0.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
            r0.actionBar.setTitle(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf(1), Integer.valueOf(1)));
            r0.containerView.addView(r0.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            r0.actionBar.setActionBarMenuOnItemClick(new C22368());
            ActionBarMenu createMenu = r0.actionBar.createMenu();
            r0.masksItem = createMenu.addItem(13, (int) C0446R.drawable.ic_masks_msk1);
            r0.pipItem = createMenu.addItem(5, (int) C0446R.drawable.ic_goinline);
            r0.sendItem = createMenu.addItem(3, (int) C0446R.drawable.msg_panel_reply);
            r0.menuItem = createMenu.addItem(0, (int) C0446R.drawable.ic_ab_other);
            r0.menuItem.addSubItem(11, LocaleController.getString("OpenInExternalApp", C0446R.string.OpenInExternalApp)).setTextColor(-328966);
            r0.menuItem.addSubItem(2, LocaleController.getString("ShowAllMedia", C0446R.string.ShowAllMedia)).setTextColor(-328966);
            r0.menuItem.addSubItem(4, LocaleController.getString("ShowInChat", C0446R.string.ShowInChat)).setTextColor(-328966);
            r0.menuItem.addSubItem(10, LocaleController.getString("ShareFile", C0446R.string.ShareFile)).setTextColor(-328966);
            r0.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery)).setTextColor(-328966);
            r0.menuItem.addSubItem(6, LocaleController.getString("Delete", C0446R.string.Delete)).setTextColor(-328966);
            r0.menuItem.addSubItem(7, LocaleController.getString("StopDownload", C0446R.string.StopDownload)).setTextColor(-328966);
            r0.menuItem.redrawPopup(-115203550);
            r0.bottomLayout = new FrameLayout(r0.actvityContext);
            r0.bottomLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.containerView.addView(r0.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            r0.groupedPhotosListView = new GroupedPhotosListView(r0.actvityContext);
            r0.containerView.addView(r0.groupedPhotosListView, LayoutHelper.createFrame(-1, 62.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            r0.captionTextView = createCaptionTextView();
            r0.switchCaptionTextView = createCaptionTextView();
            for (i = 0; i < 3; i++) {
                r0.photoProgressViews[i] = new PhotoProgressView(r0.containerView.getContext(), r0.containerView);
                r0.photoProgressViews[i].setBackgroundState(0, false);
            }
            r0.miniProgressView = new RadialProgressView(r0.actvityContext) {
                public void setAlpha(float f) {
                    super.setAlpha(f);
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
            r0.miniProgressView.setBackgroundResource(C0446R.drawable.circle_big);
            r0.miniProgressView.setVisibility(4);
            r0.miniProgressView.setAlpha(0.0f);
            r0.containerView.addView(r0.miniProgressView, LayoutHelper.createFrame(64, 64, 17));
            r0.shareButton = new ImageView(r0.containerView.getContext());
            r0.shareButton.setImageResource(C0446R.drawable.share);
            r0.shareButton.setScaleType(ScaleType.CENTER);
            r0.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.bottomLayout.addView(r0.shareButton, LayoutHelper.createFrame(50, -1, 53));
            r0.shareButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
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
            r0.progressView.setBackgroundResource(C0446R.drawable.circle_big);
            r0.progressView.setVisibility(4);
            r0.containerView.addView(r0.progressView, LayoutHelper.createFrame(54, 54, 17));
            r0.qualityPicker = new PickerBottomLayoutViewer(r0.parentActivity);
            r0.qualityPicker.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.qualityPicker.updateSelectedCount(0, false);
            r0.qualityPicker.setTranslationY((float) AndroidUtilities.dp(120.0f));
            r0.qualityPicker.doneButton.setText(LocaleController.getString("Done", C0446R.string.Done).toUpperCase());
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
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    return (!PhotoViewer.this.bottomTouchEnabled || super.dispatchTouchEvent(motionEvent) == null) ? null : true;
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return (!PhotoViewer.this.bottomTouchEnabled || super.onInterceptTouchEvent(motionEvent) == null) ? null : true;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return (!PhotoViewer.this.bottomTouchEnabled || super.onTouchEvent(motionEvent) == null) ? null : true;
                }
            };
            r0.pickerView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            r0.containerView.addView(r0.pickerView, LayoutHelper.createFrame(-1, -2, 83));
            r0.videoTimelineView = new VideoTimelinePlayView(r0.parentActivity);
            r0.videoTimelineView.setDelegate(new VideoTimelineViewDelegate() {
                public void didStartDragging() {
                }

                public void didStopDragging() {
                }

                public void onLeftProgressChanged(float f) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        if (PhotoViewer.this.videoPlayer.isPlaying()) {
                            PhotoViewer.this.videoPlayer.pause();
                            PhotoViewer.this.containerView.invalidate();
                        }
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * f)));
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                        PhotoViewer.this.updateVideoInfo();
                    }
                }

                public void onRightProgressChanged(float f) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        if (PhotoViewer.this.videoPlayer.isPlaying()) {
                            PhotoViewer.this.videoPlayer.pause();
                            PhotoViewer.this.containerView.invalidate();
                        }
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * f)));
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                        PhotoViewer.this.updateVideoInfo();
                    }
                }

                public void onPlayProgressChanged(float f) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * f)));
                    }
                }
            });
            r0.pickerView.addView(r0.videoTimelineView, LayoutHelper.createFrame(-1, 58.0f, 51, 0.0f, 8.0f, 0.0f, 88.0f));
            r0.pickerViewSendButton = new ImageView(r0.parentActivity);
            r0.pickerViewSendButton.setScaleType(ScaleType.CENTER);
            r0.pickerViewSendButton.setBackgroundDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), -10043398, -10043398));
            r0.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
            r0.pickerViewSendButton.setPadding(AndroidUtilities.dp(4.0f), 0, 0, 0);
            r0.pickerViewSendButton.setImageResource(C0446R.drawable.ic_send);
            r0.containerView.addView(r0.pickerViewSendButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 14.0f, 14.0f));
            r0.pickerViewSendButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.captionEditText.getTag() == null && PhotoViewer.this.placeProvider != null && PhotoViewer.this.doneButtonPressed == null) {
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
            r0.cropItem.setImageResource(C0446R.drawable.photo_crop);
            r0.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.itemsLayout.addView(r0.cropItem, LayoutHelper.createLinear(70, 48));
            r0.cropItem.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.captionEditText.getTag() == null) {
                        PhotoViewer.this.switchToEditMode(1);
                    }
                }
            });
            r0.paintItem = new ImageView(r0.parentActivity);
            r0.paintItem.setScaleType(ScaleType.CENTER);
            r0.paintItem.setImageResource(C0446R.drawable.photo_paint);
            r0.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.itemsLayout.addView(r0.paintItem, LayoutHelper.createLinear(70, 48));
            r0.paintItem.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
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
                r0.compressItem.setImageResource(C0446R.drawable.video_240);
            } else if (r0.selectedCompression == 1) {
                r0.compressItem.setImageResource(C0446R.drawable.video_360);
            } else if (r0.selectedCompression == 2) {
                r0.compressItem.setImageResource(C0446R.drawable.video_480);
            } else if (r0.selectedCompression == 3) {
                r0.compressItem.setImageResource(C0446R.drawable.video_720);
            } else if (r0.selectedCompression == 4) {
                r0.compressItem.setImageResource(C0446R.drawable.video_1080);
            }
            r0.itemsLayout.addView(r0.compressItem, LayoutHelper.createLinear(70, 48));
            r0.compressItem.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
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
                public void onClick(View view) {
                    if (PhotoViewer.this.captionEditText.getTag() == null) {
                        PhotoViewer.this.muteVideo = PhotoViewer.this.muteVideo ^ 1;
                        if (PhotoViewer.this.muteVideo == null || PhotoViewer.this.checkImageView.isChecked() != null) {
                            view = PhotoViewer.this.imagesArrLocals.get(PhotoViewer.this.currentIndex);
                            if (view instanceof PhotoEntry) {
                                ((PhotoEntry) view).editedInfo = PhotoViewer.this.getCurrentVideoEditedInfo();
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
            r0.cameraItem.setImageResource(C0446R.drawable.photo_add);
            r0.cameraItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.containerView.addView(r0.cameraItem, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 16.0f, 0.0f));
            r0.cameraItem.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
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
            r0.tuneItem.setImageResource(C0446R.drawable.photo_tools);
            r0.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.itemsLayout.addView(r0.tuneItem, LayoutHelper.createLinear(70, 48));
            r0.tuneItem.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.captionEditText.getTag() == null) {
                        PhotoViewer.this.switchToEditMode(2);
                    }
                }
            });
            r0.timeItem = new ImageView(r0.parentActivity);
            r0.timeItem.setScaleType(ScaleType.CENTER);
            r0.timeItem.setImageResource(C0446R.drawable.photo_timer);
            r0.timeItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            r0.itemsLayout.addView(r0.timeItem, LayoutHelper.createLinear(70, 48));
            r0.timeItem.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.PhotoViewer$22$1 */
                class C15901 implements OnTouchListener {
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }

                    C15901() {
                    }
                }

                /* renamed from: org.telegram.ui.PhotoViewer$22$2 */
                class C15912 implements OnTouchListener {
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }

                    C15912() {
                    }
                }

                /* renamed from: org.telegram.ui.PhotoViewer$22$3 */
                class C22333 implements Formatter {
                    C22333() {
                    }

                    public String format(int i) {
                        if (i == 0) {
                            return LocaleController.getString("ShortMessageLifetimeForever", C0446R.string.ShortMessageLifetimeForever);
                        }
                        if (i < 1 || i >= 21) {
                            return LocaleController.formatTTLString((i - 16) * 5);
                        }
                        return LocaleController.formatTTLString(i);
                    }
                }

                public void onClick(View view) {
                    if (PhotoViewer.this.parentActivity != null) {
                        if (PhotoViewer.this.captionEditText.getTag() == null) {
                            String str;
                            int i;
                            view = new BottomSheet.Builder(PhotoViewer.this.parentActivity);
                            view.setUseHardwareLayer(false);
                            View linearLayout = new LinearLayout(PhotoViewer.this.parentActivity);
                            linearLayout.setOrientation(1);
                            view.setCustomView(linearLayout);
                            View textView = new TextView(PhotoViewer.this.parentActivity);
                            textView.setLines(1);
                            textView.setSingleLine(true);
                            textView.setText(LocaleController.getString("MessageLifetime", C0446R.string.MessageLifetime));
                            textView.setTextColor(-1);
                            textView.setTextSize(1, 16.0f);
                            textView.setEllipsize(TruncateAt.MIDDLE);
                            textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(4.0f));
                            textView.setGravity(16);
                            linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f));
                            textView.setOnTouchListener(new C15901());
                            textView = new TextView(PhotoViewer.this.parentActivity);
                            if (PhotoViewer.this.isCurrentVideo) {
                                str = "MessageLifetimeVideo";
                                i = C0446R.string.MessageLifetimeVideo;
                            } else {
                                str = "MessageLifetimePhoto";
                                i = C0446R.string.MessageLifetimePhoto;
                            }
                            textView.setText(LocaleController.getString(str, i));
                            textView.setTextColor(-8355712);
                            textView.setTextSize(1, 14.0f);
                            textView.setEllipsize(TruncateAt.MIDDLE);
                            textView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
                            textView.setGravity(16);
                            linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f));
                            textView.setOnTouchListener(new C15912());
                            view = view.create();
                            textView = new NumberPicker(PhotoViewer.this.parentActivity);
                            textView.setMinValue(0);
                            textView.setMaxValue(28);
                            Object obj = PhotoViewer.this.imagesArrLocals.get(PhotoViewer.this.currentIndex);
                            int i2 = obj instanceof PhotoEntry ? ((PhotoEntry) obj).ttl : obj instanceof SearchImage ? ((SearchImage) obj).ttl : 0;
                            if (i2 == 0) {
                                textView.setValue(MessagesController.getGlobalMainSettings().getInt("self_destruct", 7));
                            } else if (i2 < 0 || i2 >= 21) {
                                textView.setValue((21 + (i2 / 5)) - 5);
                            } else {
                                textView.setValue(i2);
                            }
                            textView.setTextColor(-1);
                            textView.setSelectorColor(-11711155);
                            textView.setFormatter(new C22333());
                            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
                            View c15924 = new FrameLayout(PhotoViewer.this.parentActivity) {
                                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                                    z = getChildCount();
                                    i3 -= i;
                                    i = 0;
                                    for (boolean z2 = false; z2 < z; z2++) {
                                        i4 = getChildAt(z2);
                                        if (((Integer) i4.getTag()).intValue() == -1) {
                                            i4.layout((i3 - getPaddingRight()) - i4.getMeasuredWidth(), getPaddingTop(), (i3 - getPaddingRight()) + i4.getMeasuredWidth(), getPaddingTop() + i4.getMeasuredHeight());
                                            i = i4;
                                        } else if (((Integer) i4.getTag()).intValue() == -2) {
                                            int paddingRight = (i3 - getPaddingRight()) - i4.getMeasuredWidth();
                                            if (i != 0) {
                                                paddingRight -= i.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                            }
                                            i4.layout(paddingRight, getPaddingTop(), i4.getMeasuredWidth() + paddingRight, getPaddingTop() + i4.getMeasuredHeight());
                                        } else {
                                            i4.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + i4.getMeasuredWidth(), getPaddingTop() + i4.getMeasuredHeight());
                                        }
                                    }
                                }
                            };
                            c15924.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                            linearLayout.addView(c15924, LayoutHelper.createLinear(-1, 52));
                            linearLayout = new TextView(PhotoViewer.this.parentActivity);
                            linearLayout.setMinWidth(AndroidUtilities.dp(64.0f));
                            linearLayout.setTag(Integer.valueOf(-1));
                            linearLayout.setTextSize(1, 14.0f);
                            linearLayout.setTextColor(-11944718);
                            linearLayout.setGravity(17);
                            linearLayout.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                            linearLayout.setText(LocaleController.getString("Done", C0446R.string.Done).toUpperCase());
                            linearLayout.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                            linearLayout.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                            c15924.addView(linearLayout, LayoutHelper.createFrame(-2, 36, 53));
                            linearLayout.setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                    view = textView.getValue();
                                    Editor edit = MessagesController.getGlobalMainSettings().edit();
                                    edit.putInt("self_destruct", view);
                                    edit.commit();
                                    view.dismiss();
                                    if (view < null || view >= 21) {
                                        view = (view - 16) * 5;
                                    }
                                    Object obj = PhotoViewer.this.imagesArrLocals.get(PhotoViewer.this.currentIndex);
                                    if (obj instanceof PhotoEntry) {
                                        ((PhotoEntry) obj).ttl = view;
                                    } else if (obj instanceof SearchImage) {
                                        ((SearchImage) obj).ttl = view;
                                    }
                                    PhotoViewer.this.timeItem.setColorFilter(view != null ? new PorterDuffColorFilter(-12734994, Mode.MULTIPLY) : null);
                                    if (PhotoViewer.this.checkImageView.isChecked() == null) {
                                        PhotoViewer.this.checkImageView.callOnClick();
                                    }
                                }
                            });
                            linearLayout = new TextView(PhotoViewer.this.parentActivity);
                            linearLayout.setMinWidth(AndroidUtilities.dp(64.0f));
                            linearLayout.setTag(Integer.valueOf(-2));
                            linearLayout.setTextSize(1, 14.0f);
                            linearLayout.setTextColor(-11944718);
                            linearLayout.setGravity(17);
                            linearLayout.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                            linearLayout.setText(LocaleController.getString("Cancel", C0446R.string.Cancel).toUpperCase());
                            linearLayout.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                            linearLayout.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                            c15924.addView(linearLayout, LayoutHelper.createFrame(-2, 36, 53));
                            linearLayout.setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                    view.dismiss();
                                }
                            });
                            view.show();
                            view.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
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
                    if (PhotoViewer.this.currentEditMode != 1 || PhotoViewer.this.photoCropView.isReady() != null) {
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
            r0.resetButton.setText(LocaleController.getString("Reset", C0446R.string.CropReset).toUpperCase());
            r0.resetButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r0.editorDoneLayout.addView(r0.resetButton, LayoutHelper.createFrame(-2, -1, 49));
            r0.resetButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    PhotoViewer.this.photoCropView.reset();
                }
            });
            r0.gestureDetector = new GestureDetector(r0.containerView.getContext(), r0);
            r0.gestureDetector.setOnDoubleTapListener(r0);
            ImageReceiverDelegate anonymousClass26 = new ImageReceiverDelegate() {
                public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
                    if (imageReceiver == PhotoViewer.this.centerImage && z && !z2 && PhotoViewer.this.currentEditMode && PhotoViewer.this.photoCropView) {
                        z2 = imageReceiver.getBitmap();
                        if (z2) {
                            PhotoViewer.this.photoCropView.setBitmap(z2, imageReceiver.getOrientation(), PhotoViewer.this.sendPhotoType != 1);
                        }
                    }
                    if (imageReceiver != PhotoViewer.this.centerImage || !z || PhotoViewer.this.placeProvider == null || PhotoViewer.this.placeProvider.scaleToFill() == null || PhotoViewer.this.ignoreDidSetImage != null) {
                        return;
                    }
                    if (PhotoViewer.this.wasLayout == null) {
                        PhotoViewer.this.dontResetZoomOnFirstLayout = true;
                    } else {
                        PhotoViewer.this.setScaleToFill();
                    }
                }
            };
            r0.centerImage.setParentView(r0.containerView);
            r0.centerImage.setCrossfadeAlpha((byte) 2);
            r0.centerImage.setInvalidateAll(true);
            r0.centerImage.setDelegate(anonymousClass26);
            r0.leftImage.setParentView(r0.containerView);
            r0.leftImage.setCrossfadeAlpha((byte) 2);
            r0.leftImage.setInvalidateAll(true);
            r0.leftImage.setDelegate(anonymousClass26);
            r0.rightImage.setParentView(r0.containerView);
            r0.rightImage.setCrossfadeAlpha((byte) 2);
            r0.rightImage.setInvalidateAll(true);
            r0.rightImage.setDelegate(anonymousClass26);
            i = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            r0.checkImageView = new CheckBox(r0.containerView.getContext(), C0446R.drawable.selectphoto_large) {
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return (!PhotoViewer.this.bottomTouchEnabled || super.onTouchEvent(motionEvent) == null) ? null : true;
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
            if (i != 3) {
                if (i != 1) {
                    f = 68.0f;
                    frameLayoutDrawer.addView(view, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, f, 10.0f, 0.0f));
                    if (VERSION.SDK_INT >= 21) {
                        layoutParams = (FrameLayout.LayoutParams) r0.checkImageView.getLayoutParams();
                        layoutParams.topMargin += AndroidUtilities.statusBarHeight;
                    }
                    r0.checkImageView.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            if (PhotoViewer.this.captionEditText.getTag() == null) {
                                PhotoViewer.this.setPhotoChecked();
                            }
                        }
                    });
                    r0.photosCounterView = new CounterView(r0.parentActivity);
                    frameLayoutDrawer = r0.containerView;
                    view = r0.photosCounterView;
                    if (i != 3) {
                        if (i == 1) {
                            f2 = 68.0f;
                            frameLayoutDrawer.addView(view, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, f2, 66.0f, 0.0f));
                            if (VERSION.SDK_INT >= 21) {
                                layoutParams2 = (FrameLayout.LayoutParams) r0.photosCounterView.getLayoutParams();
                                layoutParams2.topMargin += AndroidUtilities.statusBarHeight;
                            }
                            r0.photosCounterView.setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                    if (!(PhotoViewer.this.captionEditText.getTag() != null || PhotoViewer.this.placeProvider == null || PhotoViewer.this.placeProvider.getSelectedPhotosOrder() == null)) {
                                        if (PhotoViewer.this.placeProvider.getSelectedPhotosOrder().isEmpty() == null) {
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
                                public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                                    recyclerView = recyclerView.getChildAdapterPosition(view);
                                    if ((view instanceof PhotoPickerPhotoCell) == null || recyclerView != null) {
                                        rect.left = null;
                                    } else {
                                        rect.left = AndroidUtilities.dp(3.0f);
                                    }
                                    rect.right = AndroidUtilities.dp(3.0f);
                                }
                            });
                            ((DefaultItemAnimator) r0.selectedPhotosListView.getItemAnimator()).setDelayAnimations(false);
                            r0.selectedPhotosListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                            r0.selectedPhotosListView.setPadding(0, AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f));
                            r0.selectedPhotosListView.setLayoutManager(new LinearLayoutManager(r0.parentActivity, 0, false) {
                                public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
                                    state = new LinearSmoothScrollerEnd(recyclerView.getContext());
                                    state.setTargetPosition(i);
                                    startSmoothScroll(state);
                                }
                            });
                            recyclerListView = r0.selectedPhotosListView;
                            listAdapter = new ListAdapter(r0.parentActivity);
                            r0.selectedPhotosAdapter = listAdapter;
                            recyclerListView.setAdapter(listAdapter);
                            r0.containerView.addView(r0.selectedPhotosListView, LayoutHelper.createFrame(-1, 88, 51));
                            r0.selectedPhotosListView.setOnItemClickListener(new OnItemClickListener() {
                                public void onItemClick(View view, int i) {
                                    if (i != 0 || PhotoViewer.this.placeProvider.allowGroupPhotos() == 0) {
                                        PhotoViewer.this.ignoreDidSetImage = true;
                                        view = PhotoViewer.this.imagesArrLocals.indexOf(view.getTag());
                                        if (view >= null) {
                                            PhotoViewer.this.currentIndex = -1;
                                            PhotoViewer.this.setImageIndex(view, true);
                                        }
                                        PhotoViewer.this.ignoreDidSetImage = false;
                                        return;
                                    }
                                    i = SharedConfig.groupPhotosEnabled;
                                    SharedConfig.toggleGroupPhotosEnabled();
                                    PhotoViewer.this.placeProvider.toggleGroupPhotosEnabled();
                                    ((ImageView) view).setColorFilter(i == 0 ? new PorterDuffColorFilter(-10043398, Mode.MULTIPLY) : null);
                                    PhotoViewer.this.showHint(false, i ^ 1);
                                }
                            });
                            r0.captionEditText = new PhotoViewerCaptionEnterView(r0.actvityContext, r0.containerView, r0.windowView) {
                                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                                    boolean z = false;
                                    try {
                                        if (!(PhotoViewer.this.bottomTouchEnabled || super.dispatchTouchEvent(motionEvent) == null)) {
                                            z = true;
                                        }
                                        return z;
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                        return false;
                                    }
                                }

                                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                                    boolean z = false;
                                    try {
                                        if (!(PhotoViewer.this.bottomTouchEnabled || super.onInterceptTouchEvent(motionEvent) == null)) {
                                            z = true;
                                        }
                                        return z;
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                        return false;
                                    }
                                }

                                public boolean onTouchEvent(MotionEvent motionEvent) {
                                    return (PhotoViewer.this.bottomTouchEnabled || super.onTouchEvent(motionEvent) == null) ? null : true;
                                }
                            };
                            r0.captionEditText.setDelegate(new PhotoViewerCaptionEnterViewDelegate() {
                                public void onCaptionEnter() {
                                    PhotoViewer.this.closeCaptionEnter(true);
                                }

                                public void onTextChanged(CharSequence charSequence) {
                                    if (PhotoViewer.this.mentionsAdapter != null && PhotoViewer.this.captionEditText != null && PhotoViewer.this.parentChatActivity != null && charSequence != null) {
                                        PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(charSequence.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages, false);
                                    }
                                }

                                public void onWindowSizeChanged(int i) {
                                    if (i - (ActionBar.getCurrentActionBarHeight() * 2) < AndroidUtilities.dp((float) ((36 * Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount())) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0)))) {
                                        PhotoViewer.this.allowMentions = false;
                                        if (PhotoViewer.this.mentionListView != 0 && PhotoViewer.this.mentionListView.getVisibility() == 0) {
                                            PhotoViewer.this.mentionListView.setVisibility(4);
                                            return;
                                        }
                                        return;
                                    }
                                    PhotoViewer.this.allowMentions = true;
                                    if (PhotoViewer.this.mentionListView != 0 && PhotoViewer.this.mentionListView.getVisibility() == 4) {
                                        PhotoViewer.this.mentionListView.setVisibility(0);
                                    }
                                }
                            });
                            r0.containerView.addView(r0.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
                            r0.mentionListView = new RecyclerListView(r0.actvityContext) {
                                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                                    return (PhotoViewer.this.bottomTouchEnabled || super.dispatchTouchEvent(motionEvent) == null) ? null : true;
                                }

                                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                                    return (PhotoViewer.this.bottomTouchEnabled || super.onInterceptTouchEvent(motionEvent) == null) ? null : true;
                                }

                                public boolean onTouchEvent(MotionEvent motionEvent) {
                                    return (PhotoViewer.this.bottomTouchEnabled || super.onTouchEvent(motionEvent) == null) ? null : true;
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
                                class C15961 extends AnimatorListenerAdapter {
                                    C15961() {
                                    }

                                    public void onAnimationEnd(Animator animator) {
                                        if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animator) != null) {
                                            PhotoViewer.this.mentionListAnimation = null;
                                        }
                                    }
                                }

                                /* renamed from: org.telegram.ui.PhotoViewer$37$2 */
                                class C15972 extends AnimatorListenerAdapter {
                                    C15972() {
                                    }

                                    public void onAnimationEnd(Animator animator) {
                                        if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animator) != null) {
                                            PhotoViewer.this.mentionListView.setVisibility(8);
                                            PhotoViewer.this.mentionListAnimation = null;
                                        }
                                    }
                                }

                                public void onContextClick(BotInlineResult botInlineResult) {
                                }

                                public void onContextSearch(boolean z) {
                                }

                                public void needChangePanelVisibility(boolean z) {
                                    if (z) {
                                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.mentionListView.getLayoutParams();
                                        float min = (float) ((36 * Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount())) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0));
                                        layoutParams.height = AndroidUtilities.dp(min);
                                        layoutParams.topMargin = -AndroidUtilities.dp(min);
                                        PhotoViewer.this.mentionListView.setLayoutParams(layoutParams);
                                        if (PhotoViewer.this.mentionListAnimation) {
                                            PhotoViewer.this.mentionListAnimation.cancel();
                                            PhotoViewer.this.mentionListAnimation = null;
                                        }
                                        if (PhotoViewer.this.mentionListView.getVisibility()) {
                                            PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
                                            if (PhotoViewer.this.allowMentions) {
                                                PhotoViewer.this.mentionListView.setVisibility(0);
                                                PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                                                PhotoViewer.this.mentionListAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[]{0.0f, 1.0f})});
                                                PhotoViewer.this.mentionListAnimation.addListener(new C15961());
                                                PhotoViewer.this.mentionListAnimation.setDuration(200);
                                                PhotoViewer.this.mentionListAnimation.start();
                                            } else {
                                                PhotoViewer.this.mentionListView.setAlpha(1.0f);
                                                PhotoViewer.this.mentionListView.setVisibility(4);
                                            }
                                        } else {
                                            PhotoViewer.this.mentionListView.setAlpha(1.0f);
                                            return;
                                        }
                                    }
                                    if (PhotoViewer.this.mentionListAnimation) {
                                        PhotoViewer.this.mentionListAnimation.cancel();
                                        PhotoViewer.this.mentionListAnimation = null;
                                    }
                                    if (!PhotoViewer.this.mentionListView.getVisibility()) {
                                        if (PhotoViewer.this.allowMentions) {
                                            PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                                            z = PhotoViewer.this.mentionListAnimation;
                                            Animator[] animatorArr = new Animator[1];
                                            animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[]{0.0f});
                                            z.playTogether(animatorArr);
                                            PhotoViewer.this.mentionListAnimation.addListener(new C15972());
                                            PhotoViewer.this.mentionListAnimation.setDuration(200);
                                            PhotoViewer.this.mentionListAnimation.start();
                                        } else {
                                            PhotoViewer.this.mentionListView.setVisibility(8);
                                        }
                                    }
                                }
                            });
                            r0.mentionsAdapter = mentionsAdapter;
                            recyclerListView.setAdapter(mentionsAdapter);
                            r0.mentionListView.setOnItemClickListener(new OnItemClickListener() {
                                public void onItemClick(View view, int i) {
                                    view = PhotoViewer.this.mentionsAdapter.getItem(i);
                                    i = PhotoViewer.this.mentionsAdapter.getResultStartPosition();
                                    int resultLength = PhotoViewer.this.mentionsAdapter.getResultLength();
                                    PhotoViewerCaptionEnterView access$4300;
                                    StringBuilder stringBuilder;
                                    if (view instanceof User) {
                                        User user = (User) view;
                                        if (user.username != null) {
                                            access$4300 = PhotoViewer.this.captionEditText;
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("@");
                                            stringBuilder.append(user.username);
                                            stringBuilder.append(" ");
                                            access$4300.replaceWithText(i, resultLength, stringBuilder.toString(), false);
                                            return;
                                        }
                                        String firstName = UserObject.getFirstName(user);
                                        StringBuilder stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append(firstName);
                                        stringBuilder2.append(" ");
                                        CharSequence spannableString = new SpannableString(stringBuilder2.toString());
                                        stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                        stringBuilder2.append(user.id);
                                        spannableString.setSpan(new URLSpanUserMentionPhotoViewer(stringBuilder2.toString(), true), 0, spannableString.length(), 33);
                                        PhotoViewer.this.captionEditText.replaceWithText(i, resultLength, spannableString, false);
                                    } else if (view instanceof String) {
                                        access$4300 = PhotoViewer.this.captionEditText;
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(view);
                                        stringBuilder.append(" ");
                                        access$4300.replaceWithText(i, resultLength, stringBuilder.toString(), false);
                                    } else if (view instanceof EmojiSuggestion) {
                                        view = ((EmojiSuggestion) view).emoji;
                                        PhotoViewer.this.captionEditText.addEmojiToRecent(view);
                                        PhotoViewer.this.captionEditText.replaceWithText(i, resultLength, view, true);
                                    }
                                }
                            });
                            r0.mentionListView.setOnItemLongClickListener(new OnItemLongClickListener() {

                                /* renamed from: org.telegram.ui.PhotoViewer$39$1 */
                                class C15981 implements DialogInterface.OnClickListener {
                                    C15981() {
                                    }

                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        PhotoViewer.this.mentionsAdapter.clearRecentHashtags();
                                    }
                                }

                                public boolean onItemClick(View view, int i) {
                                    if ((PhotoViewer.this.mentionsAdapter.getItem(i) instanceof String) == null) {
                                        return null;
                                    }
                                    view = new Builder(PhotoViewer.this.parentActivity);
                                    view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                    view.setMessage(LocaleController.getString("ClearSearch", C0446R.string.ClearSearch));
                                    view.setPositiveButton(LocaleController.getString("ClearButton", C0446R.string.ClearButton).toUpperCase(), new C15981());
                                    view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                                    PhotoViewer.this.showAlertDialog(view);
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
            if (i != 3) {
                if (i == 1) {
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
                new Builder(this.parentActivity).setTitle(LocaleController.getString("AppName", C0446R.string.AppName)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", C0446R.string.PermissionDrawAboveOtherApps)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", C0446R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                    @TargetApi(23)
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (PhotoViewer.this.parentActivity != null) {
                            try {
                                dialogInterface = PhotoViewer.this.parentActivity;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("package:");
                                stringBuilder.append(PhotoViewer.this.parentActivity.getPackageName());
                                dialogInterface.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(stringBuilder.toString())));
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
        TextView anonymousClass41 = new TextView(this.actvityContext) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return (!PhotoViewer.this.bottomTouchEnabled || super.onTouchEvent(motionEvent) == null) ? null : true;
            }
        };
        anonymousClass41.setMovementMethod(new LinkMovementMethodMy());
        anonymousClass41.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        anonymousClass41.setLinkTextColor(-1);
        anonymousClass41.setTextColor(-1);
        anonymousClass41.setHighlightColor(872415231);
        anonymousClass41.setEllipsize(TruncateAt.END);
        anonymousClass41.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        anonymousClass41.setTextSize(1, 16.0f);
        anonymousClass41.setVisibility(4);
        anonymousClass41.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (PhotoViewer.this.needCaptionLayout != null) {
                    PhotoViewer.this.openCaptionEnter();
                }
            }
        });
        return anonymousClass41;
    }

    private int getLeftInset() {
        return (this.lastInsets == null || VERSION.SDK_INT < 21) ? 0 : ((WindowInsets) this.lastInsets).getSystemWindowInsetLeft();
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
                    org.telegram.ui.Components.Rect pipRect = PipVideoView.getPipRect(this.aspectRatioFrameLayout.getAspectRatio());
                    float width = pipRect.width / ((float) this.videoTextureView.getWidth());
                    pipRect.f27y += (float) AndroidUtilities.statusBarHeight;
                    AnimatorSet animatorSet = new AnimatorSet();
                    Animator[] animatorArr = new Animator[13];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.textureImageView, "scaleX", new float[]{width});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.textureImageView, "scaleY", new float[]{width});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.textureImageView, "translationX", new float[]{pipRect.f26x});
                    animatorArr[3] = ObjectAnimator.ofFloat(this.textureImageView, "translationY", new float[]{pipRect.f27y});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.videoTextureView, "scaleX", new float[]{width});
                    animatorArr[5] = ObjectAnimator.ofFloat(this.videoTextureView, "scaleY", new float[]{width});
                    animatorArr[6] = ObjectAnimator.ofFloat(this.videoTextureView, "translationX", new float[]{pipRect.f26x - this.aspectRatioFrameLayout.getX()});
                    animatorArr[7] = ObjectAnimator.ofFloat(this.videoTextureView, "translationY", new float[]{pipRect.f27y - this.aspectRatioFrameLayout.getY()});
                    animatorArr[8] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[]{0});
                    animatorArr[9] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f});
                    animatorArr[10] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f});
                    animatorArr[11] = ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[]{0.0f});
                    animatorArr[12] = ObjectAnimator.ofFloat(this.groupedPhotosListView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.setDuration(250);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
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
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
            if (VERSION.SDK_INT >= 21) {
                this.pipAnimationInProgress = true;
                org.telegram.ui.Components.Rect pipRect = PipVideoView.getPipRect(this.aspectRatioFrameLayout.getAspectRatio());
                float f = pipRect.width / ((float) this.textureImageView.getLayoutParams().width);
                pipRect.f27y += (float) AndroidUtilities.statusBarHeight;
                this.textureImageView.setScaleX(f);
                this.textureImageView.setScaleY(f);
                this.textureImageView.setTranslationX(pipRect.f26x);
                this.textureImageView.setTranslationY(pipRect.f27y);
                this.videoTextureView.setScaleX(f);
                this.videoTextureView.setScaleY(f);
                this.videoTextureView.setTranslationX(pipRect.f26x - this.aspectRatioFrameLayout.getX());
                this.videoTextureView.setTranslationY(pipRect.f27y - this.aspectRatioFrameLayout.getY());
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
            } catch (Throwable e) {
                FileLog.m3e(e);
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
            public void onSeekBarDrag(float f) {
                if (PhotoViewer.this.videoPlayer != null) {
                    if (!PhotoViewer.this.inPreview && PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
                        f = PhotoViewer.this.videoTimelineView.getLeftProgress() + ((PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress()) * f);
                    }
                    long duration = PhotoViewer.this.videoPlayer.getDuration();
                    if (duration == C0542C.TIME_UNSET) {
                        PhotoViewer.this.seekToProgressPending = f;
                    } else {
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (f * ((float) duration))));
                    }
                }
            }
        });
        this.videoPlayerControlFrameLayout = new FrameLayout(this.containerView.getContext()) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                motionEvent.getX();
                motionEvent.getY();
                if (PhotoViewer.this.videoPlayerSeekbar.onTouch(motionEvent.getAction(), motionEvent.getX() - ((float) AndroidUtilities.dp(48.0f)), motionEvent.getY()) == null) {
                    return true;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }

            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                long j = 0;
                if (PhotoViewer.this.videoPlayer != 0) {
                    i = PhotoViewer.this.videoPlayer.getDuration();
                    if (i != C0542C.TIME_UNSET) {
                        j = i;
                    }
                }
                j /= 1000;
                i = PhotoViewer.this.videoPlayerTime.getPaint();
                r2 = new Object[4];
                long j2 = j / 60;
                r2[0] = Long.valueOf(j2);
                j %= 60;
                r2[1] = Long.valueOf(j);
                r2[2] = Long.valueOf(j2);
                r2[3] = Long.valueOf(j);
                PhotoViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) i.measureText(String.format("%02d:%02d / %02d:%02d", r2)))), getMeasuredHeight());
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (PhotoViewer.this.videoPlayer) {
                    z = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (PhotoViewer.this.inPreview == 0 && PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
                        z -= PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (z >= false) {
                            z = false;
                        }
                        z /= PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (z > true) {
                            z = true;
                        }
                    }
                } else {
                    z = false;
                }
                PhotoViewer.this.videoPlayerSeekbar.setProgress(z);
                PhotoViewer.this.videoTimelineView.setProgress(z);
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
            public void onClick(View view) {
                if (PhotoViewer.this.videoPlayer != null) {
                    if (PhotoViewer.this.isPlaying != null) {
                        PhotoViewer.this.videoPlayer.pause();
                    } else {
                        if (PhotoViewer.this.isCurrentVideo != null) {
                            if (Math.abs(PhotoViewer.this.videoTimelineView.getProgress() - NUM) < NUM || PhotoViewer.this.videoPlayer.getCurrentPosition() == PhotoViewer.this.videoPlayer.getDuration()) {
                                PhotoViewer.this.videoPlayer.seekTo(0);
                            }
                        } else if (Math.abs(PhotoViewer.this.videoPlayerSeekbar.getProgress() - NUM) < NUM || PhotoViewer.this.videoPlayer.getCurrentPosition() == PhotoViewer.this.videoPlayer.getDuration()) {
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
                        i = C0446R.string.GifCaption;
                    } else {
                        str = "VideoCaption";
                        i = C0446R.string.VideoCaption;
                    }
                    actionBar.setTitle(LocaleController.getString(str, i));
                    this.actionBar.setSubtitle(null);
                } else {
                    this.actionBar.setTitle(LocaleController.getString("PhotoCaption", C0446R.string.PhotoCaption));
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

    private void closeCaptionEnter(boolean z) {
        if (this.currentIndex >= 0) {
            if (this.currentIndex < this.imagesArrLocals.size()) {
                Object obj = this.imagesArrLocals.get(this.currentIndex);
                CharSequence[] charSequenceArr = new CharSequence[]{this.captionEditText.getFieldCharSequence()};
                if (z) {
                    z = DataQuery.getInstance(this.currentAccount).getEntities(charSequenceArr);
                    if (obj instanceof PhotoEntry) {
                        PhotoEntry photoEntry = (PhotoEntry) obj;
                        photoEntry.caption = charSequenceArr[0];
                        photoEntry.entities = z;
                    } else if (obj instanceof SearchImage) {
                        SearchImage searchImage = (SearchImage) obj;
                        searchImage.caption = charSequenceArr[0];
                        searchImage.entities = z;
                    }
                    if (this.captionEditText.getFieldCharSequence().length() && !this.placeProvider.isPhotoChecked(this.currentIndex)) {
                        setPhotoChecked();
                    }
                }
                this.captionEditText.setTag(null);
                if (this.lastTitle) {
                    this.actionBar.setTitle(this.lastTitle);
                    this.lastTitle = null;
                }
                if (this.isCurrentVideo) {
                    this.actionBar.setSubtitle(this.muteVideo ? null : this.currentSubtitle);
                }
                updateCaptionTextForCurrentPhoto(obj);
                setCurrentCaption(null, charSequenceArr[0], false);
                if (this.captionEditText.isPopupShowing()) {
                    this.captionEditText.hidePopup();
                }
                this.captionEditText.closeKeyboard();
            }
        }
    }

    private void updateVideoPlayerTime() {
        CharSequence format;
        if (this.videoPlayer == null) {
            format = String.format("%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)});
        } else {
            long currentPosition = this.videoPlayer.getCurrentPosition();
            long j = 0;
            if (currentPosition < 0) {
                currentPosition = 0;
            }
            long duration = this.videoPlayer.getDuration();
            if (duration >= 0) {
                j = duration;
            }
            if (j == C0542C.TIME_UNSET || currentPosition == C0542C.TIME_UNSET) {
                format = String.format("%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)});
            } else {
                if (!this.inPreview && this.videoTimelineView.getVisibility() == 0) {
                    j = (long) (((float) j) * (this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()));
                    currentPosition = (long) (((float) currentPosition) - (this.videoTimelineView.getLeftProgress() * ((float) j)));
                    if (currentPosition > j) {
                        currentPosition = j;
                    }
                }
                currentPosition /= 1000;
                j /= 1000;
                format = String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(currentPosition / 60), Long.valueOf(currentPosition % 60), Long.valueOf(j / 60), Long.valueOf(j % 60)});
            }
        }
        this.videoPlayerTime.setText(format);
    }

    private void checkBufferedProgress(float f) {
        if (!(!this.isStreaming || this.parentActivity == null || this.streamingAlertShown || this.videoPlayer == null)) {
            if (this.currentMessageObject != null) {
                Document document = this.currentMessageObject.getDocument();
                if (document != null && f < 0.9f && ((((float) document.size) * f >= 5242880.0f || f >= 0.5f) && Math.abs(SystemClock.elapsedRealtime() - this.startedPlayTime) >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS)) {
                    if (this.videoPlayer.getDuration() == C0542C.TIME_UNSET) {
                        Toast.makeText(this.parentActivity, LocaleController.getString("VideoDoesNotSupportStreaming", C0446R.string.VideoDoesNotSupportStreaming), 1).show();
                    }
                    this.streamingAlertShown = true;
                }
            }
        }
    }

    private void preparePlayer(Uri uri, boolean z, boolean z2) {
        if (!z2) {
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
            this.inPreview = z2;
            releasePlayer();
            if (this.videoTextureView == null) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity) {
                    protected void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        if (PhotoViewer.this.textureImageView != 0) {
                            i = PhotoViewer.this.textureImageView.getLayoutParams();
                            i.width = getMeasuredWidth();
                            i.height = getMeasuredHeight();
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
            this.videoPlayButton.setImageResource(C0446R.drawable.inline_video_play);
            if (this.videoPlayer == null) {
                this.videoPlayer = new VideoPlayer();
                this.videoPlayer.setTextureView(this.videoTextureView);
                this.videoPlayer.setDelegate(new VideoPlayerDelegate() {

                    /* renamed from: org.telegram.ui.PhotoViewer$48$1 */
                    class C16021 implements DialogInterface.OnClickListener {
                        C16021() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                                PhotoViewer.this.closePhoto(false, false);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    }

                    /* renamed from: org.telegram.ui.PhotoViewer$48$2 */
                    class C16032 extends AnimatorListenerAdapter {
                        C16032() {
                        }

                        public void onAnimationEnd(Animator animator) {
                            PhotoViewer.this.pipAnimationInProgress = false;
                        }
                    }

                    public void onStateChanged(boolean z, int i) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            if (PhotoViewer.this.isStreaming) {
                                PhotoViewer.this.toggleMiniProgress(i == 2, true);
                            }
                            if (!z || i == 4 || i == 1) {
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
                            if (PhotoViewer.this.seekToProgressPending && (i == 3 || i == 1)) {
                                PhotoViewer.this.videoPlayer.seekTo((long) ((int) (((float) PhotoViewer.this.videoPlayer.getDuration()) * PhotoViewer.this.seekToProgressPending)));
                                PhotoViewer.this.seekToProgressPending = 0.0f;
                            }
                            if (i == 3) {
                                if (PhotoViewer.this.aspectRatioFrameLayout.getVisibility()) {
                                    PhotoViewer.this.aspectRatioFrameLayout.setVisibility(0);
                                }
                                if (!PhotoViewer.this.pipItem.isEnabled()) {
                                    PhotoViewer.this.pipAvailable = true;
                                    PhotoViewer.this.pipItem.setEnabled(true);
                                    PhotoViewer.this.pipItem.setAlpha(1.0f);
                                }
                            }
                            if (!PhotoViewer.this.videoPlayer.isPlaying() || i == 4) {
                                if (PhotoViewer.this.isPlaying) {
                                    PhotoViewer.this.isPlaying = false;
                                    PhotoViewer.this.videoPlayButton.setImageResource(C0446R.drawable.inline_video_play);
                                    AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.updateProgressRunnable);
                                    if (i == 4) {
                                        if (!PhotoViewer.this.isCurrentVideo) {
                                            if (!PhotoViewer.this.isActionBarVisible) {
                                                PhotoViewer.this.toggleActionBar(true, true);
                                            }
                                            if (!PhotoViewer.this.videoPlayerSeekbar.isDragging()) {
                                                PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                                                PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                                                if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility()) {
                                                    PhotoViewer.this.videoPlayer.seekTo(0);
                                                } else {
                                                    PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                                                }
                                                PhotoViewer.this.videoPlayer.pause();
                                            }
                                        } else if (!PhotoViewer.this.videoTimelineView.isDragging()) {
                                            PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                                            if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility()) {
                                                PhotoViewer.this.videoPlayer.seekTo(0);
                                            } else {
                                                PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                                            }
                                            PhotoViewer.this.videoPlayer.pause();
                                            PhotoViewer.this.containerView.invalidate();
                                        }
                                        if (PhotoViewer.this.pipVideoView) {
                                            PhotoViewer.this.pipVideoView.onVideoCompleted();
                                        }
                                    }
                                }
                            } else if (!PhotoViewer.this.isPlaying) {
                                PhotoViewer.this.isPlaying = true;
                                PhotoViewer.this.videoPlayButton.setImageResource(C0446R.drawable.inline_video_pause);
                                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable);
                            }
                            if (PhotoViewer.this.pipVideoView) {
                                PhotoViewer.this.pipVideoView.updatePlayButton();
                            }
                            PhotoViewer.this.updateVideoPlayerTime();
                        }
                    }

                    public void onError(Exception exception) {
                        FileLog.m3e((Throwable) exception);
                        if (PhotoViewer.this.menuItem.isSubItemVisible(11) != null) {
                            exception = new Builder(PhotoViewer.this.parentActivity);
                            exception.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            exception.setMessage(LocaleController.getString("CantPlayVideo", C0446R.string.CantPlayVideo));
                            exception.setPositiveButton(LocaleController.getString("Open", C0446R.string.Open), new C16021());
                            exception.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                            PhotoViewer.this.showAlertDialog(exception);
                        }
                    }

                    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                        if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                            if (i3 != 90) {
                                if (i3 != 270) {
                                    int i4 = i2;
                                    i2 = i;
                                    i = i4;
                                }
                            }
                            PhotoViewer.this.aspectRatioFrameLayout.setAspectRatio(i == 0 ? NUM : (((float) i2) * f) / ((float) i), i3);
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
                                surfaceTexture = PhotoViewer.this.pipPosition;
                                surfaceTexture[0] = surfaceTexture[0] - PhotoViewer.this.getLeftInset();
                                surfaceTexture = PhotoViewer.this.pipPosition;
                                surfaceTexture[1] = (int) (((float) surfaceTexture[1]) - PhotoViewer.this.containerView.getTranslationY());
                                surfaceTexture = new AnimatorSet();
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
                                surfaceTexture.playTogether(animatorArr);
                                surfaceTexture.setInterpolator(new DecelerateInterpolator());
                                surfaceTexture.setDuration(250);
                                surfaceTexture.addListener(new C16032());
                                surfaceTexture.start();
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
            if (!(this.currentBotInlineResult == null || (this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) == null && MessageObject.isVideoDocument(this.currentBotInlineResult.document) == null))) {
                this.bottomLayout.setVisibility(0);
                this.bottomLayout.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
            }
            uri = this.videoPlayerControlFrameLayout;
            if (this.isCurrentVideo) {
                i = 8;
            }
            uri.setVisibility(i);
            this.dateTextView.setVisibility(8);
            this.nameTextView.setVisibility(8);
            if (this.allowShare != null) {
                this.shareButton.setVisibility(8);
                this.menuItem.showSubItem(10);
            }
            this.videoPlayer.setPlayWhenReady(z);
            this.inPreview = z2;
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
            this.videoPlayButton.setImageResource(C0446R.drawable.inline_video_play);
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

    private void updateCaptionTextForCurrentPhoto(Object obj) {
        if (obj instanceof PhotoEntry) {
            obj = ((PhotoEntry) obj).caption;
        } else {
            if (!(obj instanceof BotInlineResult)) {
                if (obj instanceof SearchImage) {
                    obj = ((SearchImage) obj).caption;
                }
            }
            obj = null;
        }
        if (obj != null) {
            if (obj.length() != 0) {
                this.captionEditText.setFieldText(obj);
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
                    public void onDismiss(DialogInterface dialogInterface) {
                        PhotoViewer.this.visibleDialog = null;
                    }
                });
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }
    }

    private void applyCurrentEditMode() {
        Bitmap bitmap;
        boolean z;
        Collection collection;
        SavedFilterState savedFilterState;
        TLObject scaleAndSaveImage;
        Object obj;
        int i;
        SearchImage searchImage;
        PhotoEntry photoEntry;
        float rectSizeX;
        float rectSizeY;
        if (this.currentEditMode == 1) {
            bitmap = r0.photoCropView.getBitmap();
            z = true;
            collection = null;
        } else {
            if (r0.currentEditMode == 2) {
                bitmap = r0.photoFilterView.getBitmap();
                savedFilterState = r0.photoFilterView.getSavedFilterState();
                collection = null;
            } else if (r0.currentEditMode == 3) {
                bitmap = r0.photoPaintView.getBitmap();
                collection = r0.photoPaintView.getMasks();
                z = true;
            } else {
                bitmap = null;
                collection = null;
                savedFilterState = null;
            }
            z = false;
            if (bitmap != null) {
                scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
                if (scaleAndSaveImage != null) {
                    obj = r0.imagesArrLocals.get(r0.currentIndex);
                    if (obj instanceof PhotoEntry) {
                        i = -12734994;
                        if (obj instanceof SearchImage) {
                            searchImage = (SearchImage) obj;
                            searchImage.imagePath = FileLoader.getPathToAttach(scaleAndSaveImage, true).toString();
                            scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(120.0f), (float) AndroidUtilities.dp(120.0f), 70, false, 101, 101);
                            if (scaleAndSaveImage != null) {
                                searchImage.thumbPath = FileLoader.getPathToAttach(scaleAndSaveImage, true).toString();
                            }
                            if (collection != null) {
                                searchImage.stickers.addAll(collection);
                            }
                            if (r0.currentEditMode == 1) {
                                r0.cropItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                searchImage.isCropped = true;
                            } else if (r0.currentEditMode == 2) {
                                r0.tuneItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                searchImage.isFiltered = true;
                            } else if (r0.currentEditMode == 3) {
                                r0.paintItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                searchImage.isPainted = true;
                            }
                            if (savedFilterState != null) {
                                searchImage.savedFilterState = savedFilterState;
                            } else if (z) {
                                searchImage.savedFilterState = null;
                            }
                        }
                    } else {
                        PhotoEntry photoEntry2 = (PhotoEntry) obj;
                        photoEntry2.imagePath = FileLoader.getPathToAttach(scaleAndSaveImage, true).toString();
                        photoEntry = photoEntry2;
                        i = -12734994;
                        scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(120.0f), (float) AndroidUtilities.dp(120.0f), 70, false, 101, 101);
                        if (scaleAndSaveImage != null) {
                            photoEntry.thumbPath = FileLoader.getPathToAttach(scaleAndSaveImage, true).toString();
                        }
                        if (collection != null) {
                            photoEntry.stickers.addAll(collection);
                        }
                        if (r0.currentEditMode == 1) {
                            r0.cropItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                            photoEntry.isCropped = true;
                        } else if (r0.currentEditMode == 2) {
                            r0.tuneItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                            photoEntry.isFiltered = true;
                        } else if (r0.currentEditMode == 3) {
                            r0.paintItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                            photoEntry.isPainted = true;
                        }
                        if (savedFilterState != null) {
                            photoEntry.savedFilterState = savedFilterState;
                        } else if (z) {
                            photoEntry.savedFilterState = null;
                        }
                    }
                    if (r0.sendPhotoType == 0 && r0.placeProvider != null) {
                        r0.placeProvider.updatePhotoAtIndex(r0.currentIndex);
                        if (!r0.placeProvider.isPhotoChecked(r0.currentIndex)) {
                            setPhotoChecked();
                        }
                    }
                    if (r0.currentEditMode == 1) {
                        rectSizeX = r0.photoCropView.getRectSizeX() / ((float) getContainerViewWidth());
                        rectSizeY = r0.photoCropView.getRectSizeY() / ((float) getContainerViewHeight());
                        if (rectSizeX > rectSizeY) {
                            rectSizeX = rectSizeY;
                        }
                        r0.scale = rectSizeX;
                        r0.translationX = (r0.photoCropView.getRectX() + (r0.photoCropView.getRectSizeX() / 2.0f)) - ((float) (getContainerViewWidth() / 2));
                        r0.translationY = (r0.photoCropView.getRectY() + (r0.photoCropView.getRectSizeY() / 2.0f)) - ((float) (getContainerViewHeight() / 2));
                        r0.zoomAnimation = true;
                        r0.applying = true;
                        r0.photoCropView.onDisappear();
                    }
                    r0.centerImage.setParentView(null);
                    r0.centerImage.setOrientation(0, true);
                    r0.ignoreDidSetImage = true;
                    r0.centerImage.setImageBitmap(bitmap);
                    r0.ignoreDidSetImage = false;
                    r0.centerImage.setParentView(r0.containerView);
                }
            }
        }
        savedFilterState = null;
        if (bitmap != null) {
            scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
            if (scaleAndSaveImage != null) {
                obj = r0.imagesArrLocals.get(r0.currentIndex);
                if (obj instanceof PhotoEntry) {
                    i = -12734994;
                    if (obj instanceof SearchImage) {
                        searchImage = (SearchImage) obj;
                        searchImage.imagePath = FileLoader.getPathToAttach(scaleAndSaveImage, true).toString();
                        scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(120.0f), (float) AndroidUtilities.dp(120.0f), 70, false, 101, 101);
                        if (scaleAndSaveImage != null) {
                            searchImage.thumbPath = FileLoader.getPathToAttach(scaleAndSaveImage, true).toString();
                        }
                        if (collection != null) {
                            searchImage.stickers.addAll(collection);
                        }
                        if (r0.currentEditMode == 1) {
                            r0.cropItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                            searchImage.isCropped = true;
                        } else if (r0.currentEditMode == 2) {
                            r0.tuneItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                            searchImage.isFiltered = true;
                        } else if (r0.currentEditMode == 3) {
                            r0.paintItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                            searchImage.isPainted = true;
                        }
                        if (savedFilterState != null) {
                            searchImage.savedFilterState = savedFilterState;
                        } else if (z) {
                            searchImage.savedFilterState = null;
                        }
                    }
                } else {
                    PhotoEntry photoEntry22 = (PhotoEntry) obj;
                    photoEntry22.imagePath = FileLoader.getPathToAttach(scaleAndSaveImage, true).toString();
                    photoEntry = photoEntry22;
                    i = -12734994;
                    scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(120.0f), (float) AndroidUtilities.dp(120.0f), 70, false, 101, 101);
                    if (scaleAndSaveImage != null) {
                        photoEntry.thumbPath = FileLoader.getPathToAttach(scaleAndSaveImage, true).toString();
                    }
                    if (collection != null) {
                        photoEntry.stickers.addAll(collection);
                    }
                    if (r0.currentEditMode == 1) {
                        r0.cropItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        photoEntry.isCropped = true;
                    } else if (r0.currentEditMode == 2) {
                        r0.tuneItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        photoEntry.isFiltered = true;
                    } else if (r0.currentEditMode == 3) {
                        r0.paintItem.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        photoEntry.isPainted = true;
                    }
                    if (savedFilterState != null) {
                        photoEntry.savedFilterState = savedFilterState;
                    } else if (z) {
                        photoEntry.savedFilterState = null;
                    }
                }
                r0.placeProvider.updatePhotoAtIndex(r0.currentIndex);
                if (r0.placeProvider.isPhotoChecked(r0.currentIndex)) {
                    setPhotoChecked();
                }
                if (r0.currentEditMode == 1) {
                    rectSizeX = r0.photoCropView.getRectSizeX() / ((float) getContainerViewWidth());
                    rectSizeY = r0.photoCropView.getRectSizeY() / ((float) getContainerViewHeight());
                    if (rectSizeX > rectSizeY) {
                        rectSizeX = rectSizeY;
                    }
                    r0.scale = rectSizeX;
                    r0.translationX = (r0.photoCropView.getRectX() + (r0.photoCropView.getRectSizeX() / 2.0f)) - ((float) (getContainerViewWidth() / 2));
                    r0.translationY = (r0.photoCropView.getRectY() + (r0.photoCropView.getRectSizeY() / 2.0f)) - ((float) (getContainerViewHeight() / 2));
                    r0.zoomAnimation = true;
                    r0.applying = true;
                    r0.photoCropView.onDisappear();
                }
                r0.centerImage.setParentView(null);
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
            int photoChecked = this.placeProvider.setPhotoChecked(this.currentIndex, getCurrentVideoEditedInfo());
            boolean isPhotoChecked = this.placeProvider.isPhotoChecked(this.currentIndex);
            this.checkImageView.setChecked(isPhotoChecked, true);
            if (photoChecked >= 0) {
                if (this.placeProvider.allowGroupPhotos()) {
                    photoChecked++;
                }
                if (isPhotoChecked) {
                    this.selectedPhotosAdapter.notifyItemInserted(photoChecked);
                    this.selectedPhotosListView.smoothScrollToPosition(photoChecked);
                } else {
                    this.selectedPhotosAdapter.notifyItemRemoved(photoChecked);
                }
            }
            updateSelectedCount();
        }
    }

    private void switchToEditMode(int i) {
        final int i2 = i;
        if (this.currentEditMode != i2 && r0.centerImage.getBitmap() != null && r0.changeModeAnimation == null && r0.imageMoveAnimation == null && r0.photoProgressViews[0].backgroundState == -1) {
            if (r0.captionEditText.getTag() == null) {
                if (i2 == 0) {
                    if (r0.centerImage.getBitmap() != null) {
                        int bitmapWidth = r0.centerImage.getBitmapWidth();
                        float f = (float) bitmapWidth;
                        float containerViewWidth = ((float) getContainerViewWidth()) / f;
                        float bitmapHeight = (float) r0.centerImage.getBitmapHeight();
                        float containerViewHeight = ((float) getContainerViewHeight()) / bitmapHeight;
                        float containerViewWidth2 = ((float) getContainerViewWidth(0)) / f;
                        float containerViewHeight2 = ((float) getContainerViewHeight(0)) / bitmapHeight;
                        if (containerViewWidth > containerViewHeight) {
                            containerViewWidth = containerViewHeight;
                        }
                        if (containerViewWidth2 > containerViewHeight2) {
                            containerViewWidth2 = containerViewHeight2;
                        }
                        if (r0.sendPhotoType != 1 || r0.applying) {
                            r0.animateToScale = containerViewWidth2 / containerViewWidth;
                        } else {
                            containerViewHeight2 = (float) Math.min(getContainerViewWidth(), getContainerViewHeight());
                            f = containerViewHeight2 / f;
                            containerViewHeight2 /= bitmapHeight;
                            if (f <= containerViewHeight2) {
                                f = containerViewHeight2;
                            }
                            r0.scale = f / containerViewWidth;
                            r0.animateToScale = (r0.scale * containerViewWidth2) / f;
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
                        AnimatorSet animatorSet2 = r0.imageMoveAnimation;
                        r5 = new Animator[2];
                        r5[0] = ObjectAnimator.ofFloat(r0.photoFilterView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(186.0f)});
                        r5[1] = ObjectAnimator.ofFloat(r0, "animationValue", new float[]{0.0f, 1.0f});
                        animatorSet2.playTogether(r5);
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
                        class C16061 extends AnimatorListenerAdapter {
                            C16061() {
                            }

                            public void onAnimationStart(Animator animator) {
                                PhotoViewer.this.pickerView.setVisibility(0);
                                PhotoViewer.this.pickerViewSendButton.setVisibility(0);
                                PhotoViewer.this.actionBar.setVisibility(0);
                                if (PhotoViewer.this.needCaptionLayout != null) {
                                    PhotoViewer.this.captionTextView.setVisibility(PhotoViewer.this.captionTextView.getTag() != null ? 0 : 4);
                                }
                                if (PhotoViewer.this.sendPhotoType == null || (PhotoViewer.this.sendPhotoType == 2 && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                    PhotoViewer.this.checkImageView.setVisibility(0);
                                    PhotoViewer.this.photosCounterView.setVisibility(0);
                                }
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
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
                            PhotoViewer.this.currentEditMode = i2;
                            PhotoViewer.this.applying = false;
                            PhotoViewer.this.animateToScale = 1.0f;
                            PhotoViewer.this.animateToX = 0.0f;
                            PhotoViewer.this.animateToY = 0.0f;
                            PhotoViewer.this.scale = 1.0f;
                            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                            PhotoViewer.this.containerView.invalidate();
                            animator = new AnimatorSet();
                            Collection arrayList = new ArrayList();
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
                            animator.playTogether(arrayList);
                            animator.setDuration(200);
                            animator.addListener(new C16061());
                            animator.start();
                        }
                    });
                    r0.imageMoveAnimation.start();
                } else if (i2 == 1) {
                    if (r0.photoCropView == null) {
                        r0.photoCropView = new PhotoCropView(r0.actvityContext);
                        r0.photoCropView.setVisibility(8);
                        r0.containerView.addView(r0.photoCropView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                        r0.photoCropView.setDelegate(new PhotoCropViewDelegate() {
                            public void needMoveImageTo(float f, float f2, float f3, boolean z) {
                                if (z) {
                                    PhotoViewer.this.animateTo(f3, f, f2, true);
                                    return;
                                }
                                PhotoViewer.this.translationX = f;
                                PhotoViewer.this.translationY = f2;
                                PhotoViewer.this.scale = f3;
                                PhotoViewer.this.containerView.invalidate();
                            }

                            public Bitmap getBitmap() {
                                return PhotoViewer.this.centerImage.getBitmap();
                            }

                            public void onChange(boolean z) {
                                PhotoViewer.this.resetButton.setVisibility(z ? true : false);
                            }
                        });
                    }
                    r0.photoCropView.onAppear();
                    r0.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", C0446R.string.Crop));
                    r0.editorDoneLayout.doneButton.setTextColor(-11420173);
                    r0.changeModeAnimation = new AnimatorSet();
                    r2 = new ArrayList();
                    r2.add(ObjectAnimator.ofFloat(r0.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    r2.add(ObjectAnimator.ofFloat(r0.pickerViewSendButton, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    r2.add(ObjectAnimator.ofFloat(r0.actionBar, "translationY", new float[]{0.0f, (float) (-r0.actionBar.getHeight())}));
                    if (r0.needCaptionLayout) {
                        r2.add(ObjectAnimator.ofFloat(r0.captionTextView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    }
                    if (r0.sendPhotoType == 0) {
                        r2.add(ObjectAnimator.ofFloat(r0.checkImageView, "alpha", new float[]{1.0f, 0.0f}));
                        r2.add(ObjectAnimator.ofFloat(r0.photosCounterView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.selectedPhotosListView.getVisibility() == 0) {
                        r2.add(ObjectAnimator.ofFloat(r0.selectedPhotosListView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.cameraItem.getTag() != null) {
                        r2.add(ObjectAnimator.ofFloat(r0.cameraItem, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    r0.changeModeAnimation.playTogether(r2);
                    r0.changeModeAnimation.setDuration(200);
                    r0.changeModeAnimation.addListener(new AnimatorListenerAdapter() {

                        /* renamed from: org.telegram.ui.PhotoViewer$52$1 */
                        class C16071 extends AnimatorListenerAdapter {
                            C16071() {
                            }

                            public void onAnimationStart(Animator animator) {
                                PhotoViewer.this.editorDoneLayout.setVisibility(0);
                                PhotoViewer.this.photoCropView.setVisibility(0);
                            }

                            public void onAnimationEnd(Animator animator) {
                                PhotoViewer.this.photoCropView.onAppeared();
                                PhotoViewer.this.imageMoveAnimation = null;
                                PhotoViewer.this.currentEditMode = i2;
                                PhotoViewer.this.animateToScale = 1.0f;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = 0.0f;
                                PhotoViewer.this.scale = 1.0f;
                                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                                PhotoViewer.this.containerView.invalidate();
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
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
                            if (PhotoViewer.this.needCaptionLayout != null) {
                                PhotoViewer.this.captionTextView.setVisibility(4);
                            }
                            if (PhotoViewer.this.sendPhotoType == null || (PhotoViewer.this.sendPhotoType == 2 && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                PhotoViewer.this.checkImageView.setVisibility(8);
                                PhotoViewer.this.photosCounterView.setVisibility(8);
                            }
                            animator = PhotoViewer.this.centerImage.getBitmap();
                            if (animator != null) {
                                PhotoViewer.this.photoCropView.setBitmap(animator, PhotoViewer.this.centerImage.getOrientation(), PhotoViewer.this.sendPhotoType != 1);
                                animator = (float) PhotoViewer.this.centerImage.getBitmapWidth();
                                float access$4000 = ((float) PhotoViewer.this.getContainerViewWidth()) / animator;
                                float bitmapHeight = (float) PhotoViewer.this.centerImage.getBitmapHeight();
                                float access$4100 = ((float) PhotoViewer.this.getContainerViewHeight()) / bitmapHeight;
                                float access$15300 = ((float) PhotoViewer.this.getContainerViewWidth(1)) / animator;
                                float access$15400 = ((float) PhotoViewer.this.getContainerViewHeight(1)) / bitmapHeight;
                                if (access$4000 > access$4100) {
                                    access$4000 = access$4100;
                                }
                                if (access$15300 > access$15400) {
                                    access$15300 = access$15400;
                                }
                                if (PhotoViewer.this.sendPhotoType == 1) {
                                    access$4100 = (float) Math.min(PhotoViewer.this.getContainerViewWidth(1), PhotoViewer.this.getContainerViewHeight(1));
                                    animator = access$4100 / animator;
                                    access$4100 /= bitmapHeight;
                                    access$15300 = animator > access$4100 ? animator : access$4100;
                                }
                                PhotoViewer.this.animateToScale = access$15300 / access$4000;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(56.0f)) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                                PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                                PhotoViewer.this.zoomAnimation = true;
                            }
                            PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                            animator = PhotoViewer.this.imageMoveAnimation;
                            r0 = new Animator[3];
                            r0[0] = ObjectAnimator.ofFloat(PhotoViewer.this.editorDoneLayout, "translationY", new float[]{(float) AndroidUtilities.dp(48.0f), 0.0f});
                            r0[1] = ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[]{0.0f, 1.0f});
                            r0[2] = ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, "alpha", new float[]{0.0f, 1.0f});
                            animator.playTogether(r0);
                            PhotoViewer.this.imageMoveAnimation.setDuration(200);
                            PhotoViewer.this.imageMoveAnimation.addListener(new C16071());
                            PhotoViewer.this.imageMoveAnimation.start();
                        }
                    });
                    r0.changeModeAnimation.start();
                } else if (i2 == 2) {
                    if (r0.photoFilterView == null) {
                        String str;
                        int i3;
                        Bitmap bitmap;
                        Bitmap bitmap2;
                        SavedFilterState savedFilterState = null;
                        if (!r0.imagesArrLocals.isEmpty()) {
                            Object obj = r0.imagesArrLocals.get(r0.currentIndex);
                            if (obj instanceof PhotoEntry) {
                                PhotoEntry photoEntry = (PhotoEntry) obj;
                                if (photoEntry.imagePath == null) {
                                    String str2 = photoEntry.path;
                                    str = str2;
                                    savedFilterState = photoEntry.savedFilterState;
                                } else {
                                    str = null;
                                }
                                i3 = photoEntry.orientation;
                            } else if (obj instanceof SearchImage) {
                                SearchImage searchImage = (SearchImage) obj;
                                savedFilterState = searchImage.savedFilterState;
                                str = searchImage.imageUrl;
                                i3 = 0;
                            }
                            if (savedFilterState != null) {
                                bitmap = r0.centerImage.getBitmap();
                                bitmap2 = bitmap;
                                i3 = r0.centerImage.getOrientation();
                            } else {
                                bitmap2 = BitmapFactory.decodeFile(str);
                            }
                            r0.photoFilterView = new PhotoFilterView(r0.parentActivity, bitmap2, i3, savedFilterState);
                            r0.containerView.addView(r0.photoFilterView, LayoutHelper.createFrame(-1, -1.0f));
                            r0.photoFilterView.getDoneTextView().setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                    PhotoViewer.this.applyCurrentEditMode();
                                    PhotoViewer.this.switchToEditMode(0);
                                }
                            });
                            r0.photoFilterView.getCancelTextView().setOnClickListener(new OnClickListener() {

                                /* renamed from: org.telegram.ui.PhotoViewer$54$1 */
                                class C16081 implements DialogInterface.OnClickListener {
                                    C16081() {
                                    }

                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        PhotoViewer.this.switchToEditMode(0);
                                    }
                                }

                                public void onClick(View view) {
                                    if (PhotoViewer.this.photoFilterView.hasChanges() == null) {
                                        PhotoViewer.this.switchToEditMode(0);
                                    } else if (PhotoViewer.this.parentActivity != null) {
                                        view = new Builder(PhotoViewer.this.parentActivity);
                                        view.setMessage(LocaleController.getString("DiscardChanges", C0446R.string.DiscardChanges));
                                        view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                        view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C16081());
                                        view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                                        PhotoViewer.this.showAlertDialog(view);
                                    }
                                }
                            });
                            r0.photoFilterView.getToolsView().setTranslationY((float) AndroidUtilities.dp(186.0f));
                        }
                        i3 = 0;
                        str = null;
                        if (savedFilterState != null) {
                            bitmap2 = BitmapFactory.decodeFile(str);
                        } else {
                            bitmap = r0.centerImage.getBitmap();
                            bitmap2 = bitmap;
                            i3 = r0.centerImage.getOrientation();
                        }
                        r0.photoFilterView = new PhotoFilterView(r0.parentActivity, bitmap2, i3, savedFilterState);
                        r0.containerView.addView(r0.photoFilterView, LayoutHelper.createFrame(-1, -1.0f));
                        r0.photoFilterView.getDoneTextView().setOnClickListener(/* anonymous class already generated */);
                        r0.photoFilterView.getCancelTextView().setOnClickListener(/* anonymous class already generated */);
                        r0.photoFilterView.getToolsView().setTranslationY((float) AndroidUtilities.dp(186.0f));
                    }
                    r0.changeModeAnimation = new AnimatorSet();
                    r2 = new ArrayList();
                    r2.add(ObjectAnimator.ofFloat(r0.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    r2.add(ObjectAnimator.ofFloat(r0.pickerViewSendButton, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    r2.add(ObjectAnimator.ofFloat(r0.actionBar, "translationY", new float[]{0.0f, (float) (-r0.actionBar.getHeight())}));
                    if (r0.sendPhotoType == 0) {
                        r2.add(ObjectAnimator.ofFloat(r0.checkImageView, "alpha", new float[]{1.0f, 0.0f}));
                        r2.add(ObjectAnimator.ofFloat(r0.photosCounterView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.selectedPhotosListView.getVisibility() == 0) {
                        r2.add(ObjectAnimator.ofFloat(r0.selectedPhotosListView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.cameraItem.getTag() != null) {
                        r2.add(ObjectAnimator.ofFloat(r0.cameraItem, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    r0.changeModeAnimation.playTogether(r2);
                    r0.changeModeAnimation.setDuration(200);
                    r0.changeModeAnimation.addListener(new AnimatorListenerAdapter() {

                        /* renamed from: org.telegram.ui.PhotoViewer$55$1 */
                        class C16091 extends AnimatorListenerAdapter {
                            public void onAnimationStart(Animator animator) {
                            }

                            C16091() {
                            }

                            public void onAnimationEnd(Animator animator) {
                                PhotoViewer.this.photoFilterView.init();
                                PhotoViewer.this.imageMoveAnimation = null;
                                PhotoViewer.this.currentEditMode = i2;
                                PhotoViewer.this.animateToScale = 1.0f;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = 0.0f;
                                PhotoViewer.this.scale = 1.0f;
                                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                                PhotoViewer.this.containerView.invalidate();
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
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
                            if (PhotoViewer.this.needCaptionLayout != null) {
                                PhotoViewer.this.captionTextView.setVisibility(4);
                            }
                            if (PhotoViewer.this.sendPhotoType == null || (PhotoViewer.this.sendPhotoType == 2 && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                PhotoViewer.this.checkImageView.setVisibility(8);
                                PhotoViewer.this.photosCounterView.setVisibility(8);
                            }
                            if (PhotoViewer.this.centerImage.getBitmap() != null) {
                                animator = (float) PhotoViewer.this.centerImage.getBitmapWidth();
                                float access$4000 = ((float) PhotoViewer.this.getContainerViewWidth()) / animator;
                                float bitmapHeight = (float) PhotoViewer.this.centerImage.getBitmapHeight();
                                float access$4100 = ((float) PhotoViewer.this.getContainerViewHeight()) / bitmapHeight;
                                float access$15300 = ((float) PhotoViewer.this.getContainerViewWidth(2)) / animator;
                                animator = ((float) PhotoViewer.this.getContainerViewHeight(2)) / bitmapHeight;
                                if (access$4000 > access$4100) {
                                    access$4000 = access$4100;
                                }
                                if (access$15300 <= animator) {
                                    animator = access$15300;
                                }
                                PhotoViewer.this.animateToScale = animator / access$4000;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(92.0f)) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                                PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                                PhotoViewer.this.zoomAnimation = true;
                            }
                            PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                            animator = PhotoViewer.this.imageMoveAnimation;
                            Animator[] animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[]{0.0f, 1.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(PhotoViewer.this.photoFilterView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(186.0f), 0.0f});
                            animator.playTogether(animatorArr);
                            PhotoViewer.this.imageMoveAnimation.setDuration(200);
                            PhotoViewer.this.imageMoveAnimation.addListener(new C16091());
                            PhotoViewer.this.imageMoveAnimation.start();
                        }
                    });
                    r0.changeModeAnimation.start();
                } else if (i2 == 3) {
                    if (r0.photoPaintView == null) {
                        r0.photoPaintView = new PhotoPaintView(r0.parentActivity, r0.centerImage.getBitmap(), r0.centerImage.getOrientation());
                        r0.containerView.addView(r0.photoPaintView, LayoutHelper.createFrame(-1, -1.0f));
                        r0.photoPaintView.getDoneTextView().setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                PhotoViewer.this.applyCurrentEditMode();
                                PhotoViewer.this.switchToEditMode(0);
                            }
                        });
                        r0.photoPaintView.getCancelTextView().setOnClickListener(new OnClickListener() {

                            /* renamed from: org.telegram.ui.PhotoViewer$57$1 */
                            class C16101 implements Runnable {
                                C16101() {
                                }

                                public void run() {
                                    PhotoViewer.this.switchToEditMode(0);
                                }
                            }

                            public void onClick(View view) {
                                PhotoViewer.this.photoPaintView.maybeShowDismissalAlert(PhotoViewer.this, PhotoViewer.this.parentActivity, new C16101());
                            }
                        });
                        r0.photoPaintView.getColorPicker().setTranslationY((float) AndroidUtilities.dp(126.0f));
                        r0.photoPaintView.getToolsView().setTranslationY((float) AndroidUtilities.dp(126.0f));
                    }
                    r0.changeModeAnimation = new AnimatorSet();
                    r2 = new ArrayList();
                    r2.add(ObjectAnimator.ofFloat(r0.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    r2.add(ObjectAnimator.ofFloat(r0.pickerViewSendButton, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    r2.add(ObjectAnimator.ofFloat(r0.actionBar, "translationY", new float[]{0.0f, (float) (-r0.actionBar.getHeight())}));
                    if (r0.needCaptionLayout) {
                        r2.add(ObjectAnimator.ofFloat(r0.captionTextView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
                    }
                    if (r0.sendPhotoType == 0) {
                        r2.add(ObjectAnimator.ofFloat(r0.checkImageView, "alpha", new float[]{1.0f, 0.0f}));
                        r2.add(ObjectAnimator.ofFloat(r0.photosCounterView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.selectedPhotosListView.getVisibility() == 0) {
                        r2.add(ObjectAnimator.ofFloat(r0.selectedPhotosListView, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    if (r0.cameraItem.getTag() != null) {
                        r2.add(ObjectAnimator.ofFloat(r0.cameraItem, "alpha", new float[]{1.0f, 0.0f}));
                    }
                    r0.changeModeAnimation.playTogether(r2);
                    r0.changeModeAnimation.setDuration(200);
                    r0.changeModeAnimation.addListener(new AnimatorListenerAdapter() {

                        /* renamed from: org.telegram.ui.PhotoViewer$58$1 */
                        class C16111 extends AnimatorListenerAdapter {
                            public void onAnimationStart(Animator animator) {
                            }

                            C16111() {
                            }

                            public void onAnimationEnd(Animator animator) {
                                PhotoViewer.this.photoPaintView.init();
                                PhotoViewer.this.imageMoveAnimation = null;
                                PhotoViewer.this.currentEditMode = i2;
                                PhotoViewer.this.animateToScale = 1.0f;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = 0.0f;
                                PhotoViewer.this.scale = 1.0f;
                                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                                PhotoViewer.this.containerView.invalidate();
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
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
                            if (PhotoViewer.this.needCaptionLayout != null) {
                                PhotoViewer.this.captionTextView.setVisibility(4);
                            }
                            if (PhotoViewer.this.sendPhotoType == null || (PhotoViewer.this.sendPhotoType == 2 && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                PhotoViewer.this.checkImageView.setVisibility(8);
                                PhotoViewer.this.photosCounterView.setVisibility(8);
                            }
                            if (PhotoViewer.this.centerImage.getBitmap() != null) {
                                animator = (float) PhotoViewer.this.centerImage.getBitmapWidth();
                                float access$4000 = ((float) PhotoViewer.this.getContainerViewWidth()) / animator;
                                float bitmapHeight = (float) PhotoViewer.this.centerImage.getBitmapHeight();
                                float access$4100 = ((float) PhotoViewer.this.getContainerViewHeight()) / bitmapHeight;
                                float access$15300 = ((float) PhotoViewer.this.getContainerViewWidth(3)) / animator;
                                animator = ((float) PhotoViewer.this.getContainerViewHeight(3)) / bitmapHeight;
                                if (access$4000 > access$4100) {
                                    access$4000 = access$4100;
                                }
                                if (access$15300 <= animator) {
                                    animator = access$15300;
                                }
                                PhotoViewer.this.animateToScale = animator / access$4000;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(44.0f)) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                                PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                                PhotoViewer.this.zoomAnimation = true;
                            }
                            PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                            animator = PhotoViewer.this.imageMoveAnimation;
                            r0 = new Animator[3];
                            r0[1] = ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getColorPicker(), "translationY", new float[]{(float) AndroidUtilities.dp(126.0f), 0.0f});
                            r0[2] = ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getToolsView(), "translationY", new float[]{(float) AndroidUtilities.dp(126.0f), 0.0f});
                            animator.playTogether(r0);
                            PhotoViewer.this.imageMoveAnimation.setDuration(200);
                            PhotoViewer.this.imageMoveAnimation.addListener(new C16111());
                            PhotoViewer.this.imageMoveAnimation.start();
                        }
                    });
                    r0.changeModeAnimation.start();
                }
            }
        }
    }

    private void toggleCheckImageView(boolean z) {
        AnimatorSet animatorSet = new AnimatorSet();
        Collection arrayList = new ArrayList();
        FrameLayout frameLayout = this.pickerView;
        String str = "alpha";
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout, str, fArr));
        ImageView imageView = this.pickerViewSendButton;
        str = "alpha";
        fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(imageView, str, fArr));
        if (this.needCaptionLayout) {
            TextView textView = this.captionTextView;
            str = "alpha";
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr));
        }
        if (this.sendPhotoType == 0) {
            CheckBox checkBox = this.checkImageView;
            str = "alpha";
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(checkBox, str, fArr));
            CounterView counterView = this.photosCounterView;
            str = "alpha";
            float[] fArr2 = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr2[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(counterView, str, fArr2));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void toggleMiniProgressInternal(final boolean z) {
        if (z) {
            this.miniProgressView.setVisibility(0);
        }
        this.miniProgressAnimator = new AnimatorSet();
        AnimatorSet animatorSet = this.miniProgressAnimator;
        Animator[] animatorArr = new Animator[1];
        RadialProgressView radialProgressView = this.miniProgressView;
        String str = "alpha";
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, str, fArr);
        animatorSet.playTogether(animatorArr);
        this.miniProgressAnimator.setDuration(200);
        this.miniProgressAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PhotoViewer.this.miniProgressAnimator) != null) {
                    if (z == null) {
                        PhotoViewer.this.miniProgressView.setVisibility(4);
                    }
                    PhotoViewer.this.miniProgressAnimator = null;
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(PhotoViewer.this.miniProgressAnimator) != null) {
                    PhotoViewer.this.miniProgressAnimator = null;
                }
            }
        });
        this.miniProgressAnimator.start();
    }

    private void toggleMiniProgress(boolean z, boolean z2) {
        int i = 0;
        if (z2) {
            toggleMiniProgressInternal(z);
            if (z) {
                if (this.miniProgressAnimator) {
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
            if (this.miniProgressAnimator) {
                this.miniProgressAnimator.cancel();
                toggleMiniProgressInternal(false);
                return;
            }
            return;
        }
        if (this.miniProgressAnimator) {
            this.miniProgressAnimator.cancel();
            this.miniProgressAnimator = null;
        }
        this.miniProgressView.setAlpha(z ? 1.0f : 0.0f);
        z2 = this.miniProgressView;
        if (!z) {
            i = 4;
        }
        z2.setVisibility(i);
    }

    private void toggleActionBar(final boolean r9, boolean r10) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r8 = this;
        r0 = r8.actionBarAnimator;
        if (r0 == 0) goto L_0x0009;
    L_0x0004:
        r0 = r8.actionBarAnimator;
        r0.cancel();
    L_0x0009:
        r0 = 0;
        if (r9 == 0) goto L_0x002b;
    L_0x000c:
        r1 = r8.actionBar;
        r1.setVisibility(r0);
        r1 = r8.bottomLayout;
        r1 = r1.getTag();
        if (r1 == 0) goto L_0x001e;
    L_0x0019:
        r1 = r8.bottomLayout;
        r1.setVisibility(r0);
    L_0x001e:
        r1 = r8.captionTextView;
        r1 = r1.getTag();
        if (r1 == 0) goto L_0x002b;
    L_0x0026:
        r1 = r8.captionTextView;
        r1.setVisibility(r0);
    L_0x002b:
        r8.isActionBarVisible = r9;
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 21;
        if (r1 < r2) goto L_0x0080;
    L_0x0033:
        if (r9 == 0) goto L_0x005b;
    L_0x0035:
        r1 = r8.windowLayoutParams;
        r1 = r1.flags;
        r1 = r1 & 1024;
        if (r1 == 0) goto L_0x0080;
    L_0x003d:
        r1 = r8.windowLayoutParams;
        r2 = r1.flags;
        r2 = r2 & -1025;
        r1.flags = r2;
        r1 = r8.windowView;
        if (r1 == 0) goto L_0x0080;
    L_0x0049:
        r1 = r8.parentActivity;	 Catch:{ Exception -> 0x0080 }
        r2 = "window";	 Catch:{ Exception -> 0x0080 }
        r1 = r1.getSystemService(r2);	 Catch:{ Exception -> 0x0080 }
        r1 = (android.view.WindowManager) r1;	 Catch:{ Exception -> 0x0080 }
        r2 = r8.windowView;	 Catch:{ Exception -> 0x0080 }
        r3 = r8.windowLayoutParams;	 Catch:{ Exception -> 0x0080 }
        r1.updateViewLayout(r2, r3);	 Catch:{ Exception -> 0x0080 }
        goto L_0x0080;
    L_0x005b:
        r1 = r8.windowLayoutParams;
        r1 = r1.flags;
        r1 = r1 & 1024;
        if (r1 != 0) goto L_0x0080;
    L_0x0063:
        r1 = r8.windowLayoutParams;
        r2 = r1.flags;
        r2 = r2 | 1024;
        r1.flags = r2;
        r1 = r8.windowView;
        if (r1 == 0) goto L_0x0080;
    L_0x006f:
        r1 = r8.parentActivity;	 Catch:{ Exception -> 0x0080 }
        r2 = "window";	 Catch:{ Exception -> 0x0080 }
        r1 = r1.getSystemService(r2);	 Catch:{ Exception -> 0x0080 }
        r1 = (android.view.WindowManager) r1;	 Catch:{ Exception -> 0x0080 }
        r2 = r8.windowView;	 Catch:{ Exception -> 0x0080 }
        r3 = r8.windowLayoutParams;	 Catch:{ Exception -> 0x0080 }
        r1.updateViewLayout(r2, r3);	 Catch:{ Exception -> 0x0080 }
    L_0x0080:
        r1 = 0;
        r2 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        if (r10 == 0) goto L_0x0108;
    L_0x0085:
        r10 = new java.util.ArrayList;
        r10.<init>();
        r3 = r8.actionBar;
        r4 = "alpha";
        r5 = 1;
        r6 = new float[r5];
        if (r9 == 0) goto L_0x0095;
    L_0x0093:
        r7 = r2;
        goto L_0x0096;
    L_0x0095:
        r7 = r1;
    L_0x0096:
        r6[r0] = r7;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6);
        r10.add(r3);
        r3 = r8.bottomLayout;
        if (r3 == 0) goto L_0x00b7;
    L_0x00a3:
        r3 = r8.bottomLayout;
        r4 = "alpha";
        r6 = new float[r5];
        if (r9 == 0) goto L_0x00ad;
    L_0x00ab:
        r7 = r2;
        goto L_0x00ae;
    L_0x00ad:
        r7 = r1;
    L_0x00ae:
        r6[r0] = r7;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6);
        r10.add(r3);
    L_0x00b7:
        r3 = r8.groupedPhotosListView;
        r4 = "alpha";
        r6 = new float[r5];
        if (r9 == 0) goto L_0x00c1;
    L_0x00bf:
        r7 = r2;
        goto L_0x00c2;
    L_0x00c1:
        r7 = r1;
    L_0x00c2:
        r6[r0] = r7;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6);
        r10.add(r3);
        r3 = r8.captionTextView;
        r3 = r3.getTag();
        if (r3 == 0) goto L_0x00e5;
    L_0x00d3:
        r3 = r8.captionTextView;
        r4 = "alpha";
        r5 = new float[r5];
        if (r9 == 0) goto L_0x00dc;
    L_0x00db:
        r1 = r2;
    L_0x00dc:
        r5[r0] = r1;
        r0 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5);
        r10.add(r0);
    L_0x00e5:
        r0 = new android.animation.AnimatorSet;
        r0.<init>();
        r8.actionBarAnimator = r0;
        r0 = r8.actionBarAnimator;
        r0.playTogether(r10);
        r10 = r8.actionBarAnimator;
        r0 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r10.setDuration(r0);
        r10 = r8.actionBarAnimator;
        r0 = new org.telegram.ui.PhotoViewer$60;
        r0.<init>(r9);
        r10.addListener(r0);
        r9 = r8.actionBarAnimator;
        r9.start();
        goto L_0x012e;
    L_0x0108:
        r10 = r8.actionBar;
        if (r9 == 0) goto L_0x010e;
    L_0x010c:
        r0 = r2;
        goto L_0x010f;
    L_0x010e:
        r0 = r1;
    L_0x010f:
        r10.setAlpha(r0);
        r10 = r8.bottomLayout;
        if (r9 == 0) goto L_0x0118;
    L_0x0116:
        r0 = r2;
        goto L_0x0119;
    L_0x0118:
        r0 = r1;
    L_0x0119:
        r10.setAlpha(r0);
        r10 = r8.groupedPhotosListView;
        if (r9 == 0) goto L_0x0122;
    L_0x0120:
        r0 = r2;
        goto L_0x0123;
    L_0x0122:
        r0 = r1;
    L_0x0123:
        r10.setAlpha(r0);
        r10 = r8.captionTextView;
        if (r9 == 0) goto L_0x012b;
    L_0x012a:
        r1 = r2;
    L_0x012b:
        r10.setAlpha(r1);
    L_0x012e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.toggleActionBar(boolean, boolean):void");
    }

    private void togglePhotosListView(boolean z, boolean z2) {
        if (z != this.isPhotosListViewVisible) {
            if (z) {
                this.selectedPhotosListView.setVisibility(0);
            }
            this.isPhotosListViewVisible = z;
            this.selectedPhotosListView.setEnabled(z);
            float f = 1.0f;
            if (z2) {
                z2 = new ArrayList();
                RecyclerListView recyclerListView = this.selectedPhotosListView;
                String str = "alpha";
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                z2.add(ObjectAnimator.ofFloat(recyclerListView, str, fArr));
                recyclerListView = this.selectedPhotosListView;
                str = "translationY";
                fArr = new float[1];
                fArr[0] = z ? 0.0f : (float) (-AndroidUtilities.dp(10.0f));
                z2.add(ObjectAnimator.ofFloat(recyclerListView, str, fArr));
                CounterView counterView = this.photosCounterView;
                String str2 = "rotationX";
                float[] fArr2 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                z2.add(ObjectAnimator.ofFloat(counterView, str2, fArr2));
                this.currentListViewAnimation = new AnimatorSet();
                this.currentListViewAnimation.playTogether(z2);
                if (!z) {
                    this.currentListViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (PhotoViewer.this.currentListViewAnimation != null && PhotoViewer.this.currentListViewAnimation.equals(animator) != null) {
                                PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                                PhotoViewer.this.currentListViewAnimation = null;
                            }
                        }
                    });
                }
                this.currentListViewAnimation.setDuration(200);
                this.currentListViewAnimation.start();
            } else {
                this.selectedPhotosListView.setAlpha(z ? 1.0f : 0.0f);
                this.selectedPhotosListView.setTranslationY(z ? 0.0f : (float) (-AndroidUtilities.dp(10.0f)));
                z2 = this.photosCounterView;
                if (!z) {
                    f = 0.0f;
                }
                z2.setRotationX(f);
                if (!z) {
                    this.selectedPhotosListView.setVisibility(true);
                }
            }
        }
    }

    private String getFileName(int i) {
        if (i < 0) {
            return null;
        }
        StringBuilder stringBuilder;
        if (this.imagesArrLocations.isEmpty()) {
            if (this.imagesArr.isEmpty()) {
                if (this.imagesArrLocals.isEmpty() || i >= this.imagesArrLocals.size()) {
                    return null;
                }
                i = this.imagesArrLocals.get(i);
                if (i instanceof SearchImage) {
                    SearchImage searchImage = (SearchImage) i;
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
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(Utilities.MD5(searchImage.imageUrl));
                    stringBuilder.append(".");
                    stringBuilder.append(ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg"));
                    return stringBuilder.toString();
                }
                if (i instanceof BotInlineResult) {
                    BotInlineResult botInlineResult = (BotInlineResult) i;
                    if (botInlineResult.document != null) {
                        return FileLoader.getAttachFileName(botInlineResult.document);
                    }
                    if (botInlineResult.photo != null) {
                        return FileLoader.getAttachFileName(FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize()));
                    }
                    if (botInlineResult.content instanceof TL_webDocument) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(Utilities.MD5(botInlineResult.content.url));
                        stringBuilder.append(".");
                        stringBuilder.append(ImageLoader.getHttpUrlExtension(botInlineResult.content.url, FileLoader.getExtensionByMime(botInlineResult.content.mime_type)));
                        return stringBuilder.toString();
                    }
                }
                return null;
            }
        }
        if (this.imagesArrLocations.isEmpty()) {
            if (this.imagesArr.isEmpty() || i >= this.imagesArr.size()) {
                return null;
            }
            return FileLoader.getMessageFileName(((MessageObject) this.imagesArr.get(i)).messageOwner);
        } else if (i >= this.imagesArrLocations.size()) {
            return null;
        } else {
            FileLocation fileLocation = (FileLocation) this.imagesArrLocations.get(i);
            stringBuilder = new StringBuilder();
            stringBuilder.append(fileLocation.volume_id);
            stringBuilder.append("_");
            stringBuilder.append(fileLocation.local_id);
            stringBuilder.append(".jpg");
            return stringBuilder.toString();
        }
    }

    private TLObject getFileLocation(int i, int[] iArr) {
        if (i < 0) {
            return null;
        }
        if (this.imagesArrLocations.isEmpty()) {
            if (this.imagesArr.isEmpty() || i >= this.imagesArr.size()) {
                return null;
            }
            MessageObject messageObject = (MessageObject) this.imagesArr.get(i);
            if (messageObject.messageOwner instanceof TL_messageService) {
                if (messageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                    return messageObject.messageOwner.action.newUserPhoto.photo_big;
                }
                i = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                if (i != 0) {
                    if (iArr != null) {
                        iArr[0] = i.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                    }
                    return i.location;
                } else if (iArr != null) {
                    iArr[0] = -1;
                }
            } else if (((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) && messageObject.messageOwner.media.photo != null) || ((messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && messageObject.messageOwner.media.webpage != null)) {
                i = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                if (i != 0) {
                    if (iArr != null) {
                        iArr[0] = i.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                    }
                    return i.location;
                } else if (iArr != null) {
                    iArr[0] = -1;
                }
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaInvoice) {
                return ((TL_messageMediaInvoice) messageObject.messageOwner.media).photo;
            } else {
                if (!(messageObject.getDocument() == null || messageObject.getDocument().thumb == null)) {
                    if (iArr != null) {
                        iArr[0] = messageObject.getDocument().thumb.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                    }
                    return messageObject.getDocument().thumb.location;
                }
            }
            return null;
        } else if (i >= this.imagesArrLocations.size()) {
            return null;
        } else {
            if (iArr != null) {
                iArr[0] = ((Integer) this.imagesArrLocationsSizes.get(i)).intValue();
            }
            return (TLObject) this.imagesArrLocations.get(i);
        }
    }

    private void updateSelectedCount() {
        if (this.placeProvider != null) {
            int selectedCount = this.placeProvider.getSelectedCount();
            this.photosCounterView.setCount(selectedCount);
            if (selectedCount == 0) {
                togglePhotosListView(false, true);
            }
        }
    }

    private void onPhotoShow(MessageObject messageObject, FileLocation fileLocation, ArrayList<MessageObject> arrayList, ArrayList<Object> arrayList2, int i, PlaceProviderObject placeProviderObject) {
        int i2;
        Object obj;
        MessageObject messageObject2 = messageObject;
        FileLocation fileLocation2 = fileLocation;
        ArrayList<MessageObject> arrayList3 = arrayList;
        ArrayList<Object> arrayList4 = arrayList2;
        int i3 = i;
        PlaceProviderObject placeProviderObject2 = placeProviderObject;
        this.classGuid = ConnectionsManager.generateClassGuid();
        User user = null;
        this.currentMessageObject = null;
        this.currentFileLocation = null;
        this.currentPathObject = null;
        this.fromCamera = false;
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
        r0.opennedFromMedia = false;
        r0.needCaptionLayout = false;
        r0.containerView.setTag(Integer.valueOf(1));
        r0.isCurrentVideo = false;
        r0.imagesArr.clear();
        r0.imagesArrLocations.clear();
        r0.imagesArrLocationsSizes.clear();
        r0.avatarsArr.clear();
        r0.imagesArrLocals.clear();
        for (i2 = 0; i2 < 2; i2++) {
            r0.imagesByIds[i2].clear();
            r0.imagesByIdsTemp[i2].clear();
        }
        r0.imagesArrTemp.clear();
        r0.currentUserAvatarLocation = null;
        r0.containerView.setPadding(0, 0, 0, 0);
        if (r0.currentThumb != null) {
            r0.currentThumb.release();
        }
        r0.currentThumb = placeProviderObject2 != null ? placeProviderObject2.thumb : null;
        boolean z = placeProviderObject2 != null && placeProviderObject2.isEvent;
        r0.isEvent = z;
        r0.menuItem.setVisibility(0);
        int i4 = 8;
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
        r0.muteItem.setImageResource(C0446R.drawable.volume_on);
        r0.editorDoneLayout.setVisibility(8);
        r0.captionTextView.setTag(null);
        r0.captionTextView.setVisibility(4);
        if (r0.photoCropView != null) {
            r0.photoCropView.setVisibility(8);
        }
        if (r0.photoFilterView != null) {
            r0.photoFilterView.setVisibility(8);
        }
        for (i2 = 0; i2 < 3; i2++) {
            if (r0.photoProgressViews[i2] != null) {
                r0.photoProgressViews[i2].setBackgroundState(-1, false);
            }
        }
        if (messageObject2 != null && arrayList3 == null) {
            if ((messageObject2.messageOwner.media instanceof TL_messageMediaWebPage) && messageObject2.messageOwner.media.webpage != null) {
                WebPage webPage = messageObject2.messageOwner.media.webpage;
                String str = webPage.site_name;
                if (str != null) {
                    str = str.toLowerCase();
                    if (str.equals("instagram") || str.equals("twitter") || "telegram_album".equals(webPage.type)) {
                        if (!TextUtils.isEmpty(webPage.author)) {
                            r0.nameOverride = webPage.author;
                        }
                        if (webPage.cached_page instanceof TL_pageFull) {
                            for (int i5 = 0; i5 < webPage.cached_page.blocks.size(); i5++) {
                                PageBlock pageBlock = (PageBlock) webPage.cached_page.blocks.get(i5);
                                if (pageBlock instanceof TL_pageBlockAuthorDate) {
                                    r0.dateOverride = ((TL_pageBlockAuthorDate) pageBlock).published_date;
                                    break;
                                }
                            }
                        }
                        Collection webPagePhotos = messageObject2.getWebPagePhotos(null, null);
                        if (!webPagePhotos.isEmpty()) {
                            r0.slideshowMessageId = messageObject.getId();
                            r0.needSearchImageInArr = false;
                            r0.imagesArr.addAll(webPagePhotos);
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
            r0.avatarsDialogId = placeProviderObject2.dialogId;
            r0.imagesArrLocations.add(fileLocation2);
            r0.imagesArrLocationsSizes.add(Integer.valueOf(placeProviderObject2.size));
            r0.avatarsArr.add(new TL_photoEmpty());
            ImageView imageView = r0.shareButton;
            if (r0.videoPlayerControlFrameLayout.getVisibility() != 0) {
                i4 = 0;
            }
            imageView.setVisibility(i4);
            r0.allowShare = true;
            r0.menuItem.hideSubItem(2);
            if (r0.shareButton.getVisibility() == 0) {
                r0.menuItem.hideSubItem(10);
            } else {
                r0.menuItem.showSubItem(10);
            }
            setImageIndex(0, true);
            r0.currentUserAvatarLocation = fileLocation2;
        } else if (arrayList3 != null) {
            r0.opennedFromMedia = true;
            r0.menuItem.showSubItem(4);
            r0.sendItem.setVisibility(0);
            r0.imagesArr.addAll(arrayList3);
            for (r1 = 0; r1 < r0.imagesArr.size(); r1++) {
                MessageObject messageObject3 = (MessageObject) r0.imagesArr.get(r1);
                r0.imagesByIds[messageObject3.getDialogId() == r0.currentDialogId ? 0 : 1].put(messageObject3.getId(), messageObject3);
            }
            setImageIndex(i3, true);
        } else if (arrayList4 != null) {
            boolean z2;
            if (r0.sendPhotoType == 0 || (r0.sendPhotoType == 2 && arrayList2.size() > 1)) {
                r0.checkImageView.setVisibility(0);
                r0.photosCounterView.setVisibility(0);
                r0.actionBar.setTitleRightMargin(AndroidUtilities.dp(100.0f));
            }
            if (r0.sendPhotoType == 2) {
                r0.cameraItem.setVisibility(0);
                r0.cameraItem.setTag(Integer.valueOf(1));
            }
            r0.menuItem.setVisibility(8);
            r0.imagesArrLocals.addAll(arrayList4);
            obj = r0.imagesArrLocals.get(i3);
            if (!(obj instanceof PhotoEntry)) {
                if (obj instanceof BotInlineResult) {
                    r0.cropItem.setVisibility(8);
                } else {
                    ImageView imageView2 = r0.cropItem;
                    r1 = ((obj instanceof SearchImage) && ((SearchImage) obj).type == 0) ? 0 : 8;
                    imageView2.setVisibility(r1);
                    if (r0.cropItem.getVisibility() == 0) {
                    }
                }
                z2 = false;
                if (r0.parentChatActivity != null && (r0.parentChatActivity.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(r0.parentChatActivity.currentEncryptedChat.layer) >= 46)) {
                    r0.mentionsAdapter.setChatInfo(r0.parentChatActivity.info);
                    r0.mentionsAdapter.setNeedUsernames(r0.parentChatActivity.currentChat == null);
                    r0.mentionsAdapter.setNeedBotContext(false);
                    z2 = z2 && (r0.placeProvider == null || (r0.placeProvider != null && r0.placeProvider.allowCaption()));
                    r0.needCaptionLayout = z2;
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
                r0.bottomLayout.setTag(null);
                r0.containerView.setTag(null);
                setImageIndex(i3, true);
                r0.paintItem.setVisibility(r0.cropItem.getVisibility());
                r0.tuneItem.setVisibility(r0.cropItem.getVisibility());
                updateSelectedCount();
            } else if (((PhotoEntry) obj).isVideo) {
                r0.cropItem.setVisibility(8);
                r0.bottomLayout.setVisibility(0);
                r0.bottomLayout.setTag(Integer.valueOf(1));
                r0.bottomLayout.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
            } else {
                r0.cropItem.setVisibility(0);
            }
            z2 = true;
            r0.mentionsAdapter.setChatInfo(r0.parentChatActivity.info);
            if (r0.parentChatActivity.currentChat == null) {
            }
            r0.mentionsAdapter.setNeedUsernames(r0.parentChatActivity.currentChat == null);
            r0.mentionsAdapter.setNeedBotContext(false);
            if (!z2) {
            }
            r0.needCaptionLayout = z2;
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
            r0.bottomLayout.setTag(null);
            r0.containerView.setTag(null);
            setImageIndex(i3, true);
            r0.paintItem.setVisibility(r0.cropItem.getVisibility());
            r0.tuneItem.setVisibility(r0.cropItem.getVisibility());
            updateSelectedCount();
        }
        if (r0.currentAnimation == null && !r0.isEvent) {
            if (r0.currentDialogId != 0 && r0.totalImagesCount == 0) {
                DataQuery.getInstance(r0.currentAccount).getMediaCount(r0.currentDialogId, 0, r0.classGuid, true);
                if (r0.mergeDialogId != 0) {
                    DataQuery.getInstance(r0.currentAccount).getMediaCount(r0.mergeDialogId, 0, r0.classGuid, true);
                }
            } else if (r0.avatarsDialogId != 0) {
                MessagesController.getInstance(r0.currentAccount).loadDialogPhotos(r0.avatarsDialogId, 80, 0, true, r0.classGuid);
            }
        }
        if ((r0.currentMessageObject != null && r0.currentMessageObject.isVideo()) || (r0.currentBotInlineResult != null && (r0.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(r0.currentBotInlineResult.document)))) {
            onActionClick(false);
        } else if (!r0.imagesArrLocals.isEmpty()) {
            obj = r0.imagesArrLocals.get(i3);
            if (r0.parentChatActivity != null) {
                user = r0.parentChatActivity.getCurrentUser();
            }
            boolean z3 = (r0.parentChatActivity == null || r0.parentChatActivity.isSecretChat() || user == null || user.bot) ? false : true;
            if (obj instanceof PhotoEntry) {
                PhotoEntry photoEntry = (PhotoEntry) obj;
                if (photoEntry.isVideo) {
                    preparePlayer(Uri.fromFile(new File(photoEntry.path)), false, false);
                }
            } else if (z3 && (obj instanceof SearchImage)) {
                z3 = ((SearchImage) obj).type == 0;
            }
            if (z3) {
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

    private void setIsAboutToSwitchToIndex(int i, boolean z) {
        PhotoViewer photoViewer = this;
        int i2 = i;
        if (z || photoViewer.switchingToIndex != i2) {
            CharSequence charSequence;
            MessageObject messageObject;
            photoViewer.switchingToIndex = i2;
            String fileName = getFileName(i);
            int i3 = 0;
            boolean isVideoDocument;
            boolean z2;
            CharSequence charSequence2;
            int i4;
            ImageView imageView;
            if (photoViewer.imagesArr.isEmpty()) {
                if (!photoViewer.imagesArrLocations.isEmpty()) {
                    if (i2 >= 0) {
                        if (i2 < photoViewer.imagesArrLocations.size()) {
                            photoViewer.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                            photoViewer.dateTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                            if (photoViewer.avatarsDialogId != UserConfig.getInstance(photoViewer.currentAccount).getClientUserId() || photoViewer.avatarsArr.isEmpty()) {
                                photoViewer.menuItem.hideSubItem(6);
                            } else {
                                photoViewer.menuItem.showSubItem(6);
                            }
                            if (photoViewer.isEvent) {
                                photoViewer.actionBar.setTitle(LocaleController.getString("AttachPhoto", C0446R.string.AttachPhoto));
                            } else {
                                photoViewer.actionBar.setTitle(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf(photoViewer.switchingToIndex + 1), Integer.valueOf(photoViewer.imagesArrLocations.size())));
                            }
                            photoViewer.menuItem.showSubItem(1);
                            photoViewer.allowShare = true;
                            ImageView imageView2 = photoViewer.shareButton;
                            if (photoViewer.videoPlayerControlFrameLayout.getVisibility() == 0) {
                                i3 = 8;
                            }
                            imageView2.setVisibility(i3);
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
                    if (i2 >= 0) {
                        if (i2 < photoViewer.imagesArrLocals.size()) {
                            boolean z3;
                            boolean z4;
                            Chat currentChat;
                            User currentUser;
                            ColorFilter porterDuffColorFilter;
                            Object obj = photoViewer.imagesArrLocals.get(i2);
                            if (obj instanceof BotInlineResult) {
                                BotInlineResult botInlineResult = (BotInlineResult) obj;
                                photoViewer.currentBotInlineResult = botInlineResult;
                                isVideoDocument = botInlineResult.document != null ? MessageObject.isVideoDocument(botInlineResult.document) : botInlineResult.content instanceof TL_webDocument ? botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) : false;
                                photoViewer.pickerView.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
                                z2 = isVideoDocument;
                            } else {
                                String str;
                                int i5;
                                boolean z5;
                                isVideoDocument = obj instanceof PhotoEntry;
                                if (isVideoDocument) {
                                    PhotoEntry photoEntry = (PhotoEntry) obj;
                                    str = photoEntry.path;
                                    z2 = photoEntry.isVideo;
                                    i5 = 0;
                                } else if (obj instanceof SearchImage) {
                                    String absolutePath;
                                    SearchImage searchImage = (SearchImage) obj;
                                    if (searchImage.document != null) {
                                        absolutePath = FileLoader.getPathToAttach(searchImage.document, true).getAbsolutePath();
                                    } else {
                                        absolutePath = searchImage.imageUrl;
                                    }
                                    str = absolutePath;
                                    if (searchImage.type == 1) {
                                        z2 = false;
                                        i5 = true;
                                    } else {
                                        z2 = false;
                                        i5 = z2;
                                    }
                                } else {
                                    z2 = false;
                                    i5 = z2;
                                    str = null;
                                }
                                if (z2) {
                                    photoViewer.muteItem.setVisibility(0);
                                    photoViewer.compressItem.setVisibility(0);
                                    photoViewer.isCurrentVideo = true;
                                    if (isVideoDocument) {
                                        PhotoEntry photoEntry2 = (PhotoEntry) obj;
                                        if (photoEntry2.editedInfo != null && photoEntry2.editedInfo.muted) {
                                            z5 = true;
                                            processOpenVideo(str, z5);
                                            photoViewer.videoTimelineView.setVisibility(0);
                                            photoViewer.paintItem.setVisibility(8);
                                            photoViewer.cropItem.setVisibility(8);
                                            photoViewer.tuneItem.setVisibility(8);
                                        }
                                    }
                                    z5 = false;
                                    processOpenVideo(str, z5);
                                    photoViewer.videoTimelineView.setVisibility(0);
                                    photoViewer.paintItem.setVisibility(8);
                                    photoViewer.cropItem.setVisibility(8);
                                    photoViewer.tuneItem.setVisibility(8);
                                } else {
                                    photoViewer.videoTimelineView.setVisibility(8);
                                    photoViewer.muteItem.setVisibility(8);
                                    photoViewer.isCurrentVideo = false;
                                    photoViewer.compressItem.setVisibility(8);
                                    if (i5 != 0) {
                                        photoViewer.pickerView.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
                                        photoViewer.paintItem.setVisibility(8);
                                        photoViewer.cropItem.setVisibility(8);
                                        photoViewer.tuneItem.setVisibility(8);
                                    } else {
                                        if (photoViewer.sendPhotoType != 1) {
                                            photoViewer.pickerView.setPadding(0, 0, 0, 0);
                                        }
                                        photoViewer.paintItem.setVisibility(0);
                                        photoViewer.cropItem.setVisibility(0);
                                        photoViewer.tuneItem.setVisibility(0);
                                    }
                                    photoViewer.actionBar.setSubtitle(null);
                                }
                                if (isVideoDocument) {
                                    PhotoEntry photoEntry3 = (PhotoEntry) obj;
                                    z5 = photoEntry3.bucketId == 0 && photoEntry3.dateTaken == 0 && photoViewer.imagesArrLocals.size() == 1;
                                    photoViewer.fromCamera = z5;
                                    charSequence2 = photoEntry3.caption;
                                    i4 = photoEntry3.ttl;
                                    z3 = photoEntry3.isFiltered;
                                    z4 = photoEntry3.isPainted;
                                    isVideoDocument = photoEntry3.isCropped;
                                } else if (obj instanceof SearchImage) {
                                    SearchImage searchImage2 = (SearchImage) obj;
                                    charSequence2 = searchImage2.caption;
                                    i4 = searchImage2.ttl;
                                    z3 = searchImage2.isFiltered;
                                    z4 = searchImage2.isPainted;
                                    isVideoDocument = searchImage2.isCropped;
                                }
                                charSequence = charSequence2;
                                if (photoViewer.bottomLayout.getVisibility() != 8) {
                                    photoViewer.bottomLayout.setVisibility(8);
                                }
                                messageObject = null;
                                photoViewer.bottomLayout.setTag(null);
                                if (photoViewer.fromCamera) {
                                    photoViewer.actionBar.setTitle(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf(photoViewer.switchingToIndex + 1), Integer.valueOf(photoViewer.imagesArrLocals.size())));
                                } else if (z2) {
                                    photoViewer.actionBar.setTitle(LocaleController.getString("AttachPhoto", C0446R.string.AttachPhoto));
                                } else {
                                    photoViewer.actionBar.setTitle(LocaleController.getString("AttachVideo", C0446R.string.AttachVideo));
                                }
                                if (photoViewer.parentChatActivity != null) {
                                    currentChat = photoViewer.parentChatActivity.getCurrentChat();
                                    if (currentChat == null) {
                                        photoViewer.actionBar.setTitle(currentChat.title);
                                    } else {
                                        currentUser = photoViewer.parentChatActivity.getCurrentUser();
                                        if (currentUser != null) {
                                            photoViewer.actionBar.setTitle(ContactsController.formatName(currentUser.first_name, currentUser.last_name));
                                        }
                                    }
                                }
                                if (photoViewer.sendPhotoType == 0 || (photoViewer.sendPhotoType == 2 && photoViewer.imagesArrLocals.size() > 1)) {
                                    photoViewer.checkImageView.setChecked(photoViewer.placeProvider.isPhotoChecked(photoViewer.switchingToIndex), false);
                                }
                                updateCaptionTextForCurrentPhoto(obj);
                                porterDuffColorFilter = new PorterDuffColorFilter(-12734994, Mode.MULTIPLY);
                                photoViewer.timeItem.setColorFilter(i4 == 0 ? porterDuffColorFilter : null);
                                photoViewer.paintItem.setColorFilter(z4 ? porterDuffColorFilter : null);
                                photoViewer.cropItem.setColorFilter(isVideoDocument ? porterDuffColorFilter : null);
                                imageView = photoViewer.tuneItem;
                                if (z3) {
                                    porterDuffColorFilter = null;
                                }
                                imageView.setColorFilter(porterDuffColorFilter);
                            }
                            isVideoDocument = false;
                            i4 = isVideoDocument;
                            z3 = i4;
                            z4 = z3;
                            charSequence = null;
                            if (photoViewer.bottomLayout.getVisibility() != 8) {
                                photoViewer.bottomLayout.setVisibility(8);
                            }
                            messageObject = null;
                            photoViewer.bottomLayout.setTag(null);
                            if (photoViewer.fromCamera) {
                                photoViewer.actionBar.setTitle(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf(photoViewer.switchingToIndex + 1), Integer.valueOf(photoViewer.imagesArrLocals.size())));
                            } else if (z2) {
                                photoViewer.actionBar.setTitle(LocaleController.getString("AttachPhoto", C0446R.string.AttachPhoto));
                            } else {
                                photoViewer.actionBar.setTitle(LocaleController.getString("AttachVideo", C0446R.string.AttachVideo));
                            }
                            if (photoViewer.parentChatActivity != null) {
                                currentChat = photoViewer.parentChatActivity.getCurrentChat();
                                if (currentChat == null) {
                                    currentUser = photoViewer.parentChatActivity.getCurrentUser();
                                    if (currentUser != null) {
                                        photoViewer.actionBar.setTitle(ContactsController.formatName(currentUser.first_name, currentUser.last_name));
                                    }
                                } else {
                                    photoViewer.actionBar.setTitle(currentChat.title);
                                }
                            }
                            photoViewer.checkImageView.setChecked(photoViewer.placeProvider.isPhotoChecked(photoViewer.switchingToIndex), false);
                            updateCaptionTextForCurrentPhoto(obj);
                            porterDuffColorFilter = new PorterDuffColorFilter(-12734994, Mode.MULTIPLY);
                            if (i4 == 0) {
                            }
                            photoViewer.timeItem.setColorFilter(i4 == 0 ? porterDuffColorFilter : null);
                            if (z4) {
                            }
                            photoViewer.paintItem.setColorFilter(z4 ? porterDuffColorFilter : null);
                            if (isVideoDocument) {
                            }
                            photoViewer.cropItem.setColorFilter(isVideoDocument ? porterDuffColorFilter : null);
                            imageView = photoViewer.tuneItem;
                            if (z3) {
                                porterDuffColorFilter = null;
                            }
                            imageView.setColorFilter(porterDuffColorFilter);
                        }
                    }
                    return;
                }
                messageObject = null;
                charSequence = null;
            } else {
                if (photoViewer.switchingToIndex >= 0) {
                    if (photoViewer.switchingToIndex < photoViewer.imagesArr.size()) {
                        MessageObject messageObject2 = (MessageObject) photoViewer.imagesArr.get(photoViewer.switchingToIndex);
                        z2 = messageObject2.isVideo();
                        boolean isInvoice = messageObject2.isInvoice();
                        if (isInvoice) {
                            photoViewer.masksItem.setVisibility(8);
                            photoViewer.menuItem.hideSubItem(6);
                            photoViewer.menuItem.hideSubItem(11);
                            fileName = messageObject2.messageOwner.media.description;
                            photoViewer.allowShare = false;
                            photoViewer.bottomLayout.setTranslationY((float) AndroidUtilities.dp(48.0f));
                            photoViewer.captionTextView.setTranslationY((float) AndroidUtilities.dp(48.0f));
                        } else {
                            long j;
                            ActionBarMenuItem actionBarMenuItem = photoViewer.masksItem;
                            i4 = (!messageObject2.hasPhotoStickers() || ((int) messageObject2.getDialogId()) == 0) ? 8 : 0;
                            actionBarMenuItem.setVisibility(i4);
                            if (messageObject2.canDeleteMessage(null) && photoViewer.slideshowMessageId == 0) {
                                photoViewer.menuItem.showSubItem(6);
                            } else {
                                photoViewer.menuItem.hideSubItem(6);
                            }
                            if (z2) {
                                photoViewer.menuItem.showSubItem(11);
                                if (photoViewer.pipItem.getVisibility() != 0) {
                                    photoViewer.pipItem.setVisibility(0);
                                }
                                if (!photoViewer.pipAvailable) {
                                    photoViewer.pipItem.setEnabled(false);
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
                            } else if (messageObject2.isFromUser()) {
                                User user = MessagesController.getInstance(photoViewer.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                                if (user != null) {
                                    photoViewer.nameTextView.setText(UserObject.getUserName(user));
                                } else {
                                    photoViewer.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                                }
                            } else {
                                Chat chat = MessagesController.getInstance(photoViewer.currentAccount).getChat(Integer.valueOf(messageObject2.messageOwner.to_id.channel_id));
                                if (chat != null) {
                                    photoViewer.nameTextView.setText(chat.title);
                                } else {
                                    photoViewer.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                                }
                            }
                            if (photoViewer.dateOverride != 0) {
                                j = ((long) photoViewer.dateOverride) * 1000;
                            } else {
                                j = ((long) messageObject2.messageOwner.date) * 1000;
                            }
                            charSequence2 = LocaleController.formatString("formatDateAtTime", C0446R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(j)), LocaleController.getInstance().formatterDay.format(new Date(j)));
                            if (fileName == null || !z2) {
                                photoViewer.dateTextView.setText(charSequence2);
                            } else {
                                photoViewer.dateTextView.setText(String.format("%s (%s)", new Object[]{charSequence2, AndroidUtilities.formatFileSize((long) messageObject2.getDocument().size)}));
                            }
                            fileName = messageObject2.caption;
                        }
                        charSequence = fileName;
                        if (photoViewer.currentAnimation != null) {
                            photoViewer.menuItem.hideSubItem(1);
                            photoViewer.menuItem.hideSubItem(10);
                            if (!messageObject2.canDeleteMessage(null)) {
                                photoViewer.menuItem.setVisibility(8);
                            }
                            photoViewer.allowShare = true;
                            photoViewer.shareButton.setVisibility(0);
                            photoViewer.actionBar.setTitle(LocaleController.getString("AttachGif", C0446R.string.AttachGif));
                        } else {
                            if (photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge == 0 || photoViewer.needSearchImageInArr) {
                                if (photoViewer.slideshowMessageId == 0 && (messageObject2.messageOwner.media instanceof TL_messageMediaWebPage)) {
                                    if (messageObject2.isVideo()) {
                                        photoViewer.actionBar.setTitle(LocaleController.getString("AttachVideo", C0446R.string.AttachVideo));
                                    } else {
                                        photoViewer.actionBar.setTitle(LocaleController.getString("AttachPhoto", C0446R.string.AttachPhoto));
                                    }
                                } else if (isInvoice) {
                                    photoViewer.actionBar.setTitle(messageObject2.messageOwner.media.title);
                                }
                            } else if (photoViewer.opennedFromMedia) {
                                if (photoViewer.imagesArr.size() < photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge && !photoViewer.loadingMoreImages && photoViewer.switchingToIndex > photoViewer.imagesArr.size() - 5) {
                                    r2 = photoViewer.imagesArr.isEmpty() ? 0 : ((MessageObject) photoViewer.imagesArr.get(photoViewer.imagesArr.size() - 1)).getId();
                                    if (!photoViewer.endReached[0] || photoViewer.mergeDialogId == 0) {
                                        r21 = r2;
                                        isVideoDocument = false;
                                    } else {
                                        r21 = (photoViewer.imagesArr.isEmpty() || ((MessageObject) photoViewer.imagesArr.get(photoViewer.imagesArr.size() - 1)).getDialogId() == photoViewer.mergeDialogId) ? r2 : 0;
                                        isVideoDocument = true;
                                    }
                                    DataQuery.getInstance(photoViewer.currentAccount).loadMedia(!isVideoDocument ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, r21, 0, true, photoViewer.classGuid);
                                    photoViewer.loadingMoreImages = true;
                                }
                                photoViewer.actionBar.setTitle(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf(photoViewer.switchingToIndex + 1), Integer.valueOf(photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge)));
                            } else {
                                if (photoViewer.imagesArr.size() < photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge && !photoViewer.loadingMoreImages && photoViewer.switchingToIndex < 5) {
                                    r2 = photoViewer.imagesArr.isEmpty() ? 0 : ((MessageObject) photoViewer.imagesArr.get(0)).getId();
                                    if (!photoViewer.endReached[0] || photoViewer.mergeDialogId == 0) {
                                        r21 = r2;
                                        isVideoDocument = false;
                                    } else {
                                        r21 = (photoViewer.imagesArr.isEmpty() || ((MessageObject) photoViewer.imagesArr.get(0)).getDialogId() == photoViewer.mergeDialogId) ? r2 : 0;
                                        isVideoDocument = true;
                                    }
                                    DataQuery.getInstance(photoViewer.currentAccount).loadMedia(!isVideoDocument ? photoViewer.currentDialogId : photoViewer.mergeDialogId, 80, r21, 0, true, photoViewer.classGuid);
                                    photoViewer.loadingMoreImages = true;
                                }
                                photoViewer.actionBar.setTitle(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf((((photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge) - photoViewer.imagesArr.size()) + photoViewer.switchingToIndex) + 1), Integer.valueOf(photoViewer.totalImagesCount + photoViewer.totalImagesCountMerge)));
                            }
                            if (((int) photoViewer.currentDialogId) == 0) {
                                photoViewer.sendItem.setVisibility(8);
                            }
                            if (messageObject2.messageOwner.ttl == 0 || messageObject2.messageOwner.ttl >= 3600) {
                                photoViewer.allowShare = true;
                                photoViewer.menuItem.showSubItem(1);
                                imageView = photoViewer.shareButton;
                                if (photoViewer.videoPlayerControlFrameLayout.getVisibility() == 0) {
                                    i3 = 8;
                                }
                                imageView.setVisibility(i3);
                                if (photoViewer.shareButton.getVisibility() == 0) {
                                    photoViewer.menuItem.hideSubItem(10);
                                } else {
                                    photoViewer.menuItem.showSubItem(10);
                                }
                            } else {
                                photoViewer.allowShare = false;
                                photoViewer.menuItem.hideSubItem(1);
                                photoViewer.shareButton.setVisibility(8);
                                photoViewer.menuItem.hideSubItem(10);
                            }
                        }
                        photoViewer.groupedPhotosListView.fillList();
                        messageObject = messageObject2;
                    }
                }
                return;
            }
            setCurrentCaption(messageObject, charSequence, z ^ 1);
        }
    }

    private void setImageIndex(int i, boolean z) {
        if (this.currentIndex != i) {
            if (this.placeProvider != null) {
                Uri uri;
                if (!(z || this.currentThumb == null)) {
                    this.currentThumb.release();
                    this.currentThumb = null;
                }
                this.currentFileNames[0] = getFileName(i);
                this.currentFileNames[1] = getFileName(i + 1);
                this.currentFileNames[2] = getFileName(i - 1);
                this.placeProvider.willSwitchFromPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
                int i2 = this.currentIndex;
                this.currentIndex = i;
                setIsAboutToSwitchToIndex(this.currentIndex, z);
                if (!this.imagesArr.isEmpty()) {
                    if (this.currentIndex >= 0) {
                        if (this.currentIndex < this.imagesArr.size()) {
                            MessageObject messageObject = (MessageObject) this.imagesArr.get(this.currentIndex);
                            z = this.currentMessageObject && this.currentMessageObject.getId() == messageObject.getId();
                            this.currentMessageObject = messageObject;
                            i = messageObject.isVideo();
                            uri = null;
                        }
                    }
                    closePhoto(false, false);
                    return;
                } else if (this.imagesArrLocations.isEmpty()) {
                    if (!this.imagesArrLocals.isEmpty()) {
                        if (i >= 0) {
                            if (i < this.imagesArrLocals.size()) {
                                i = this.imagesArrLocals.get(i);
                                if (i instanceof BotInlineResult) {
                                    BotInlineResult botInlineResult = (BotInlineResult) i;
                                    this.currentBotInlineResult = botInlineResult;
                                    if (botInlineResult.document) {
                                        this.currentPathObject = FileLoader.getPathToAttach(botInlineResult.document).getAbsolutePath();
                                        i = MessageObject.isVideoDocument(botInlineResult.document);
                                    } else {
                                        if (botInlineResult.photo) {
                                            this.currentPathObject = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize())).getAbsolutePath();
                                        } else if (botInlineResult.content instanceof TL_webDocument) {
                                            this.currentPathObject = botInlineResult.content.url;
                                            i = botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO);
                                        }
                                        i = 0;
                                    }
                                    uri = null;
                                } else if (i instanceof PhotoEntry) {
                                    PhotoEntry photoEntry = (PhotoEntry) i;
                                    this.currentPathObject = photoEntry.path;
                                    z = photoEntry.isVideo;
                                    uri = Uri.fromFile(new File(photoEntry.path));
                                    i = z;
                                } else if (i instanceof SearchImage) {
                                    SearchImage searchImage = (SearchImage) i;
                                    if (searchImage.document) {
                                        this.currentPathObject = FileLoader.getPathToAttach(searchImage.document, true).getAbsolutePath();
                                    } else {
                                        this.currentPathObject = searchImage.imageUrl;
                                    }
                                }
                                z = false;
                            }
                        }
                        closePhoto(false, false);
                        return;
                    }
                    uri = null;
                    i = 0;
                    z = i;
                } else {
                    if (i >= 0) {
                        if (i < this.imagesArrLocations.size()) {
                            z = this.currentFileLocation;
                            FileLocation fileLocation = (FileLocation) this.imagesArrLocations.get(i);
                            z = z && fileLocation != null && z.local_id == fileLocation.local_id && z.volume_id == fileLocation.volume_id;
                            this.currentFileLocation = (FileLocation) this.imagesArrLocations.get(i);
                            uri = null;
                            i = 0;
                        }
                    }
                    closePhoto(false, false);
                    return;
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
                if (!z) {
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
                    if (this.imagesArrLocals.isEmpty()) {
                        if (!this.currentFileNames[0] || i != 0 || !this.photoProgressViews[0].backgroundState) {
                            z = false;
                            this.canZoom = z;
                            updateMinMax(this.scale);
                            releasePlayer();
                        }
                    }
                    z = true;
                    this.canZoom = z;
                    updateMinMax(this.scale);
                    releasePlayer();
                }
                if (!(i == 0 || uri == null)) {
                    this.isStreaming = false;
                    preparePlayer(uri, false, false);
                }
                if (i2 == -1) {
                    setImages();
                    for (boolean z2 = false; z2 < true; z2++) {
                        checkProgress(z2, false);
                    }
                } else {
                    checkProgress(0, false);
                    if (i2 > this.currentIndex) {
                        i = this.rightImage;
                        this.rightImage = this.centerImage;
                        this.centerImage = this.leftImage;
                        this.leftImage = i;
                        i = this.photoProgressViews[0];
                        this.photoProgressViews[0] = this.photoProgressViews[2];
                        this.photoProgressViews[2] = i;
                        setIndexToImage(this.leftImage, this.currentIndex - true);
                        checkProgress(1, false);
                        checkProgress(2, false);
                    } else if (i2 < this.currentIndex) {
                        i = this.leftImage;
                        this.leftImage = this.centerImage;
                        this.centerImage = this.rightImage;
                        this.rightImage = i;
                        i = this.photoProgressViews[0];
                        this.photoProgressViews[0] = this.photoProgressViews[1];
                        this.photoProgressViews[1] = i;
                        setIndexToImage(this.rightImage, this.currentIndex + true);
                        checkProgress(1, false);
                        checkProgress(2, false);
                    }
                }
            }
        }
    }

    private void setCurrentCaption(MessageObject messageObject, CharSequence charSequence, boolean z) {
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
        boolean z2 = r1.captionTextView.getTag() != null;
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (!TextUtils.isEmpty(charSequence)) {
            CharSequence replaceEmoji;
            Theme.createChatResources(null, true);
            if (messageObject2 == null || messageObject2.messageOwner.entities.isEmpty()) {
                replaceEmoji = Emoji.replaceEmoji(new SpannableStringBuilder(charSequence), r1.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            } else {
                CharSequence valueOf = SpannableString.valueOf(charSequence.toString());
                messageObject2.addEntitiesToText(valueOf, true, false);
                replaceEmoji = Emoji.replaceEmoji(valueOf, r1.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            r1.captionTextView.setTag(replaceEmoji);
            if (r1.currentCaptionAnimation != null) {
                r1.currentCaptionAnimation.cancel();
                r1.currentCaptionAnimation = null;
            }
            try {
                r1.captionTextView.setText(replaceEmoji);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            r1.captionTextView.setTextColor(-1);
            boolean z3 = r1.isActionBarVisible && (r1.bottomLayout.getVisibility() == 0 || r1.pickerView.getVisibility() == 0);
            if (z3) {
                r1.captionTextView.setVisibility(0);
                if (!z || z2) {
                    r1.captionTextView.setAlpha(1.0f);
                    return;
                }
                r1.currentCaptionAnimation = new AnimatorSet();
                r1.currentCaptionAnimation.setDuration(200);
                r1.currentCaptionAnimation.setInterpolator(decelerateInterpolator);
                r1.currentCaptionAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(PhotoViewer.this.currentCaptionAnimation) != null) {
                            PhotoViewer.this.currentCaptionAnimation = null;
                        }
                    }
                });
                animatorSet = r1.currentCaptionAnimation;
                animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(r1.captionTextView, "alpha", new float[]{0.0f, 1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(r1.captionTextView, "translationY", new float[]{(float) AndroidUtilities.dp(5.0f), 0.0f});
                animatorSet.playTogether(animatorArr);
                r1.currentCaptionAnimation.start();
            } else if (r1.captionTextView.getVisibility() == 0) {
                r1.captionTextView.setVisibility(4);
                r1.captionTextView.setAlpha(0.0f);
            }
        } else if (r1.needCaptionLayout) {
            r1.captionTextView.setText(LocaleController.getString("AddCaption", C0446R.string.AddCaption));
            r1.captionTextView.setTag("empty");
            r1.captionTextView.setVisibility(0);
            r1.captionTextView.setTextColor(-NUM);
        } else {
            r1.captionTextView.setTextColor(-1);
            r1.captionTextView.setTag(null);
            if (z && z2) {
                r1.currentCaptionAnimation = new AnimatorSet();
                r1.currentCaptionAnimation.setDuration(200);
                r1.currentCaptionAnimation.setInterpolator(decelerateInterpolator);
                r1.currentCaptionAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(PhotoViewer.this.currentCaptionAnimation) != null) {
                            PhotoViewer.this.captionTextView.setVisibility(4);
                            PhotoViewer.this.currentCaptionAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (animator.equals(PhotoViewer.this.currentCaptionAnimation) != null) {
                            PhotoViewer.this.currentCaptionAnimation = null;
                        }
                    }
                });
                animatorSet = r1.currentCaptionAnimation;
                animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(r1.captionTextView, "alpha", new float[]{0.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(r1.captionTextView, "translationY", new float[]{(float) AndroidUtilities.dp(5.0f)});
                animatorSet.playTogether(animatorArr);
                r1.currentCaptionAnimation.start();
                return;
            }
            r1.captionTextView.setVisibility(4);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkProgress(int i, boolean z) {
        int i2 = this.currentIndex;
        boolean z2 = true;
        if (i == 1) {
            i2++;
        } else if (i == 2) {
            i2--;
        }
        boolean z3 = false;
        if (this.currentFileNames[i] != null) {
            boolean isVideo;
            File file = null;
            boolean z4;
            if (this.currentMessageObject != null) {
                if (i2 >= 0) {
                    if (i2 < this.imagesArr.size()) {
                        File file2;
                        MessageObject messageObject = (MessageObject) this.imagesArr.get(i2);
                        if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                            file2 = new File(messageObject.messageOwner.attachPath);
                        }
                        file2 = null;
                        if (file2 == null) {
                            File pathToAttach;
                            if ((messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.document == null) {
                                pathToAttach = FileLoader.getPathToAttach(getFileLocation(i2, null), true);
                            } else {
                                pathToAttach = FileLoader.getPathToMessage(messageObject.messageOwner);
                            }
                            file = pathToAttach;
                        } else {
                            file = file2;
                        }
                        z4 = SharedConfig.streamMedia && messageObject.isVideo() && ((int) messageObject.getDialogId()) != 0;
                        isVideo = messageObject.isVideo();
                    }
                }
                this.photoProgressViews[i].setBackgroundState(-1, z);
                return;
            } else if (this.currentBotInlineResult != null) {
                if (i2 >= 0) {
                    if (i2 < this.imagesArrLocals.size()) {
                        BotInlineResult botInlineResult = (BotInlineResult) this.imagesArrLocals.get(i2);
                        if (!botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                            if (!MessageObject.isVideoDocument(botInlineResult.document)) {
                                if (botInlineResult.document != null) {
                                    file = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                                } else if (botInlineResult.photo != null) {
                                    file = new File(FileLoader.getDirectory(0), this.currentFileNames[i]);
                                }
                                z4 = false;
                                if (file == null || !file.exists()) {
                                    file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                                }
                                isVideo = z4;
                                z4 = false;
                            }
                        }
                        if (botInlineResult.document != null) {
                            file = FileLoader.getPathToAttach(botInlineResult.document);
                        } else if (botInlineResult.content instanceof TL_webDocument) {
                            file = FileLoader.getDirectory(4);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(Utilities.MD5(botInlineResult.content.url));
                            stringBuilder.append(".");
                            stringBuilder.append(ImageLoader.getHttpUrlExtension(botInlineResult.content.url, "mp4"));
                            file = new File(file, stringBuilder.toString());
                        }
                        z4 = true;
                        file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                        isVideo = z4;
                        z4 = false;
                    }
                }
                this.photoProgressViews[i].setBackgroundState(-1, z);
                return;
            } else {
                if (this.currentFileLocation != null) {
                    if (i2 >= 0) {
                        if (i2 < this.imagesArrLocations.size()) {
                            FileLocation fileLocation = (FileLocation) this.imagesArrLocations.get(i2);
                            if (this.avatarsDialogId == 0) {
                                if (!this.isEvent) {
                                    isVideo = false;
                                    file = FileLoader.getPathToAttach(fileLocation, isVideo);
                                }
                            }
                            isVideo = true;
                            file = FileLoader.getPathToAttach(fileLocation, isVideo);
                        }
                    }
                    this.photoProgressViews[i].setBackgroundState(-1, z);
                    return;
                } else if (this.currentPathObject != null) {
                    file = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                    if (!file.exists()) {
                        file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                    }
                }
                z4 = false;
                isVideo = z4;
            }
            boolean exists = file.exists();
            if (file == null || !(exists || r0)) {
                if (!isVideo) {
                    this.photoProgressViews[i].setBackgroundState(0, z);
                } else if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[i])) {
                    this.photoProgressViews[i].setBackgroundState(1, false);
                } else {
                    this.photoProgressViews[i].setBackgroundState(2, false);
                }
                z = ImageLoader.getInstance().getFileProgress(this.currentFileNames[i]);
                if (!z) {
                    z = Float.valueOf(false);
                }
                this.photoProgressViews[i].setProgress(z.floatValue(), false);
            } else {
                if (isVideo) {
                    this.photoProgressViews[i].setBackgroundState(3, z);
                } else {
                    this.photoProgressViews[i].setBackgroundState(-1, z);
                }
                if (i == 0) {
                    if (exists) {
                        this.menuItem.hideSubItem(7);
                    } else if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[i])) {
                        this.menuItem.showSubItem(7);
                    } else {
                        this.menuItem.hideSubItem(7);
                    }
                }
            }
            if (i == 0) {
                if (this.imagesArrLocals.isEmpty() != 0) {
                    if (this.currentFileNames[0] == 0 || isVideo || this.photoProgressViews[0].backgroundState == 0) {
                        z2 = false;
                    }
                }
                this.canZoom = z2;
            }
        } else {
            if (!this.imagesArrLocals.isEmpty() && i2 >= 0 && i2 < this.imagesArrLocals.size()) {
                Object obj = this.imagesArrLocals.get(i2);
                if (obj instanceof PhotoEntry) {
                    z3 = ((PhotoEntry) obj).isVideo;
                }
            }
            if (z3) {
                this.photoProgressViews[i].setBackgroundState(3, z);
            } else {
                this.photoProgressViews[i].setBackgroundState(-1, z);
            }
        }
    }

    public int getSelectiongLength() {
        return this.captionEditText != null ? this.captionEditText.getSelectionLength() : 0;
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int i) {
        ImageReceiver imageReceiver2 = imageReceiver;
        int i2 = i;
        imageReceiver2.setOrientation(0, false);
        FileLocation fileLocation = null;
        BitmapHolder bitmapHolder;
        if (this.imagesArrLocals.isEmpty()) {
            int[] iArr = new int[1];
            TLObject fileLocation2 = getFileLocation(i2, iArr);
            if (fileLocation2 != null) {
                MessageObject messageObject = !r0.imagesArr.isEmpty() ? (MessageObject) r0.imagesArr.get(i2) : null;
                imageReceiver2.setParentMessageObject(messageObject);
                if (messageObject != null) {
                    imageReceiver2.setShouldGenerateQualityThumb(true);
                }
                if (messageObject != null && messageObject.isVideo()) {
                    imageReceiver2.setNeedsQualityThumb(true);
                    if (messageObject.photoThumbs == null || messageObject.photoThumbs.isEmpty()) {
                        imageReceiver2.setImageBitmap(r0.parentActivity.getResources().getDrawable(C0446R.drawable.photoview_placeholder));
                        return;
                    }
                    BitmapHolder bitmapHolder2 = (r0.currentThumb == null || imageReceiver2 != r0.centerImage) ? null : r0.currentThumb;
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
                    if (bitmapHolder2 != null) {
                        fileLocation = new BitmapDrawable(bitmapHolder2.bitmap);
                    }
                    imageReceiver2.setImage(null, null, null, fileLocation, closestPhotoSizeWithSize.location, "b", 0, null, 1);
                    return;
                } else if (messageObject == null || r0.currentAnimation == null) {
                    int i3;
                    Drawable bitmapDrawable;
                    imageReceiver2.setNeedsQualityThumb(true);
                    bitmapHolder = (r0.currentThumb == null || imageReceiver2 != r0.centerImage) ? null : r0.currentThumb;
                    if (iArr[0] == 0) {
                        iArr[0] = -1;
                    }
                    PhotoSize closestPhotoSizeWithSize2 = messageObject != null ? FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100) : null;
                    if (closestPhotoSizeWithSize2 != null && closestPhotoSizeWithSize2.location == fileLocation2) {
                        closestPhotoSizeWithSize2 = null;
                    }
                    if ((messageObject == null || !messageObject.isWebpage()) && r0.avatarsDialogId == 0) {
                        if (!r0.isEvent) {
                            i3 = 0;
                            bitmapDrawable = bitmapHolder == null ? new BitmapDrawable(bitmapHolder.bitmap) : null;
                            if (closestPhotoSizeWithSize2 != null) {
                                fileLocation = closestPhotoSizeWithSize2.location;
                            }
                            imageReceiver2.setImage(fileLocation2, null, null, bitmapDrawable, fileLocation, "b", iArr[0], null, i3);
                            return;
                        }
                    }
                    i3 = 1;
                    if (bitmapHolder == null) {
                    }
                    if (closestPhotoSizeWithSize2 != null) {
                        fileLocation = closestPhotoSizeWithSize2.location;
                    }
                    imageReceiver2.setImage(fileLocation2, null, null, bitmapDrawable, fileLocation, "b", iArr[0], null, i3);
                    return;
                } else {
                    imageReceiver2.setImageBitmap(r0.currentAnimation);
                    r0.currentAnimation.setSecondParentView(r0.containerView);
                    return;
                }
            }
            imageReceiver2.setNeedsQualityThumb(true);
            imageReceiver2.setParentMessageObject(null);
            if (iArr[0] == 0) {
                imageReceiver2.setImageBitmap((Bitmap) null);
                return;
            } else {
                imageReceiver2.setImageBitmap(r0.parentActivity.getResources().getDrawable(C0446R.drawable.photoview_placeholder));
                return;
            }
        }
        imageReceiver2.setParentMessageObject(null);
        if (i2 < 0 || i2 >= r0.imagesArrLocals.size()) {
            imageReceiver2.setImageBitmap((Bitmap) null);
            return;
        }
        String str;
        boolean z;
        boolean z2;
        String str2;
        TLObject tLObject;
        TLObject tLObject2;
        TLObject tLObject3;
        Drawable bitmapDrawable2;
        Drawable drawable;
        Object obj = r0.imagesArrLocals.get(i2);
        int photoSize = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
        bitmapHolder = (r0.currentThumb == null || imageReceiver2 != r0.centerImage) ? null : r0.currentThumb;
        if (bitmapHolder == null) {
            bitmapHolder = r0.placeProvider.getThumbForPhoto(null, null, i2);
        }
        boolean z3;
        String str3;
        if (obj instanceof PhotoEntry) {
            PhotoEntry photoEntry = (PhotoEntry) obj;
            z3 = photoEntry.isVideo;
            if (photoEntry.isVideo) {
                if (photoEntry.thumbPath != null) {
                    str3 = photoEntry.thumbPath;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("vthumb://");
                    stringBuilder.append(photoEntry.imageId);
                    stringBuilder.append(":");
                    stringBuilder.append(photoEntry.path);
                    str3 = stringBuilder.toString();
                }
                str = null;
            } else {
                if (photoEntry.imagePath != null) {
                    str3 = photoEntry.imagePath;
                } else {
                    imageReceiver2.setOrientation(photoEntry.orientation, false);
                    str3 = photoEntry.path;
                }
                str = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoSize), Integer.valueOf(photoSize)});
            }
            z = z3;
            z2 = false;
            str2 = str3;
            tLObject = null;
            tLObject2 = tLObject;
        } else {
            String str4;
            if (obj instanceof BotInlineResult) {
                boolean z4;
                Object obj2;
                BotInlineResult botInlineResult = (BotInlineResult) obj;
                if (!botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                    if (!MessageObject.isVideoDocument(botInlineResult.document)) {
                        if (!botInlineResult.type.equals("gif") || botInlineResult.document == null) {
                            if (botInlineResult.photo != null) {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize());
                                tLObject2 = closestPhotoSizeWithSize.location;
                                z3 = closestPhotoSizeWithSize.size;
                                str = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoSize), Integer.valueOf(photoSize)});
                                z4 = z3;
                                tLObject = null;
                                tLObject3 = tLObject;
                            } else {
                                if (botInlineResult.content instanceof TL_webDocument) {
                                    if (botInlineResult.type.equals("gif")) {
                                        str4 = "d";
                                    } else {
                                        str4 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoSize), Integer.valueOf(photoSize)});
                                    }
                                    str = str4;
                                    z4 = false;
                                    tLObject3 = (TL_webDocument) botInlineResult.content;
                                    tLObject = null;
                                    tLObject2 = tLObject;
                                }
                                z4 = false;
                                tLObject = null;
                                tLObject2 = tLObject;
                                obj2 = tLObject2;
                                tLObject3 = str;
                            }
                            z = false;
                            z2 = z4;
                            str2 = null;
                        } else {
                            tLObject = botInlineResult.document;
                            str = "d";
                            z4 = botInlineResult.document.size;
                            tLObject2 = null;
                            tLObject3 = tLObject2;
                            z = false;
                            z2 = z4;
                            str2 = null;
                        }
                    }
                }
                if (botInlineResult.document != null) {
                    tLObject2 = botInlineResult.document.thumb.location;
                    z4 = false;
                    tLObject = null;
                    str = tLObject;
                    tLObject3 = str;
                    z = false;
                    z2 = z4;
                    str2 = null;
                } else {
                    if (botInlineResult.thumb instanceof TL_webDocument) {
                        tLObject3 = (TL_webDocument) botInlineResult.thumb;
                        z4 = false;
                        tLObject = null;
                        tLObject2 = tLObject;
                        str = tLObject2;
                        z = false;
                        z2 = z4;
                        str2 = null;
                    }
                    z4 = false;
                    tLObject = null;
                    tLObject2 = tLObject;
                    obj2 = tLObject2;
                    tLObject3 = str;
                    z = false;
                    z2 = z4;
                    str2 = null;
                }
            } else if (obj instanceof SearchImage) {
                boolean z5;
                SearchImage searchImage = (SearchImage) obj;
                if (searchImage.imagePath != null) {
                    str3 = searchImage.imagePath;
                    z5 = false;
                } else if (searchImage.document != null) {
                    tLObject = searchImage.document;
                    z5 = searchImage.document.size;
                    str3 = null;
                    z = false;
                    str2 = str3;
                    tLObject2 = null;
                    z2 = z5;
                    str = "d";
                } else {
                    str4 = searchImage.imageUrl;
                    z5 = searchImage.size;
                    str3 = str4;
                }
                tLObject = null;
                z = false;
                str2 = str3;
                tLObject2 = null;
                z2 = z5;
                str = "d";
            } else {
                z2 = false;
                z = z2;
                tLObject = null;
                tLObject2 = tLObject;
                str = tLObject2;
                tLObject3 = str;
                str2 = tLObject3;
            }
            if (tLObject != null) {
                str = "d";
                Drawable bitmapDrawable3 = bitmapHolder == null ? new BitmapDrawable(bitmapHolder.bitmap) : null;
                if (bitmapHolder == null) {
                    fileLocation = tLObject.thumb.location;
                }
                imageReceiver2.setImage(tLObject, null, str, bitmapDrawable3, fileLocation, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoSize), Integer.valueOf(photoSize)}), z2, null, 0);
            } else if (tLObject2 != null) {
                if (bitmapHolder != null) {
                    fileLocation = new BitmapDrawable(bitmapHolder.bitmap);
                }
                imageReceiver2.setImage(tLObject2, null, str, fileLocation, null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoSize), Integer.valueOf(photoSize)}), z2, null, 0);
            } else if (tLObject3 == null) {
                if (bitmapHolder == null) {
                    bitmapDrawable2 = new BitmapDrawable(bitmapHolder.bitmap);
                } else if (z || r0.parentActivity == null) {
                    drawable = null;
                    imageReceiver2.setImage(tLObject3, str, drawable, null, (int) z2);
                } else {
                    bitmapDrawable2 = r0.parentActivity.getResources().getDrawable(C0446R.drawable.nophotos);
                }
                drawable = bitmapDrawable2;
                imageReceiver2.setImage(tLObject3, str, drawable, null, (int) z2);
            } else {
                if (bitmapHolder == null) {
                    bitmapDrawable2 = new BitmapDrawable(bitmapHolder.bitmap);
                } else if (z || r0.parentActivity == null) {
                    drawable = null;
                    imageReceiver2.setImage(str2, str, drawable, null, (int) z2);
                } else {
                    bitmapDrawable2 = r0.parentActivity.getResources().getDrawable(C0446R.drawable.nophotos);
                }
                drawable = bitmapDrawable2;
                imageReceiver2.setImage(str2, str, drawable, null, (int) z2);
            }
        }
        tLObject3 = tLObject2;
        if (tLObject != null) {
            str = "d";
            if (bitmapHolder == null) {
            }
            if (bitmapHolder == null) {
                fileLocation = tLObject.thumb.location;
            }
            imageReceiver2.setImage(tLObject, null, str, bitmapDrawable3, fileLocation, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoSize), Integer.valueOf(photoSize)}), z2, null, 0);
        } else if (tLObject2 != null) {
            if (bitmapHolder != null) {
                fileLocation = new BitmapDrawable(bitmapHolder.bitmap);
            }
            imageReceiver2.setImage(tLObject2, null, str, fileLocation, null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoSize), Integer.valueOf(photoSize)}), z2, null, 0);
        } else if (tLObject3 == null) {
            if (bitmapHolder == null) {
                if (z) {
                }
                drawable = null;
                imageReceiver2.setImage(str2, str, drawable, null, (int) z2);
            }
            bitmapDrawable2 = new BitmapDrawable(bitmapHolder.bitmap);
            drawable = bitmapDrawable2;
            imageReceiver2.setImage(str2, str, drawable, null, (int) z2);
        } else {
            if (bitmapHolder == null) {
                if (z) {
                }
                drawable = null;
                imageReceiver2.setImage(tLObject3, str, drawable, null, (int) z2);
            }
            bitmapDrawable2 = new BitmapDrawable(bitmapHolder.bitmap);
            drawable = bitmapDrawable2;
            imageReceiver2.setImage(tLObject3, str, drawable, null, (int) z2);
        }
    }

    public static boolean isShowingImage(MessageObject messageObject) {
        boolean z = (Instance == null || Instance.pipAnimationInProgress || !Instance.isVisible || Instance.disableShowCheck || messageObject == null || Instance.currentMessageObject == null || Instance.currentMessageObject.getId() != messageObject.getId()) ? false : true;
        if (z || PipInstance == null) {
            return z;
        }
        return (!PipInstance.isVisible || PipInstance.disableShowCheck || messageObject == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != messageObject.getId()) ? false : true;
    }

    public static boolean isShowingImage(FileLocation fileLocation) {
        if (Instance == null || !Instance.isVisible || Instance.disableShowCheck || fileLocation == null || Instance.currentFileLocation == null || fileLocation.local_id != Instance.currentFileLocation.local_id || fileLocation.volume_id != Instance.currentFileLocation.volume_id || fileLocation.dc_id != Instance.currentFileLocation.dc_id) {
            return false;
        }
        return true;
    }

    public static boolean isShowingImage(BotInlineResult botInlineResult) {
        if (Instance == null || !Instance.isVisible || Instance.disableShowCheck || botInlineResult == null || Instance.currentBotInlineResult == null || botInlineResult.id != Instance.currentBotInlineResult.id) {
            return false;
        }
        return true;
    }

    public static boolean isShowingImage(String str) {
        if (Instance == null || !Instance.isVisible || Instance.disableShowCheck || str == null || Instance.currentPathObject == null || str.equals(Instance.currentPathObject) == null) {
            return false;
        }
        return true;
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        this.parentChatActivity = chatActivity;
    }

    public boolean openPhoto(MessageObject messageObject, long j, long j2, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(messageObject, null, null, null, 0, photoViewerProvider, null, j, j2);
    }

    public boolean openPhoto(FileLocation fileLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, fileLocation, null, null, 0, photoViewerProvider, null, 0, 0);
    }

    public boolean openPhoto(ArrayList<MessageObject> arrayList, int i, long j, long j2, PhotoViewerProvider photoViewerProvider) {
        return openPhoto((MessageObject) arrayList.get(i), null, arrayList, null, i, photoViewerProvider, null, j, j2);
    }

    public boolean openPhotoForSelect(ArrayList<Object> arrayList, int i, int i2, PhotoViewerProvider photoViewerProvider, ChatActivity chatActivity) {
        this.sendPhotoType = i2;
        if (this.pickerViewSendButton != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) r12.itemsLayout.getLayoutParams();
            if (r12.sendPhotoType == 1) {
                r12.pickerView.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
                r12.pickerViewSendButton.setImageResource(C0446R.drawable.bigcheck);
                r12.pickerViewSendButton.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
                layoutParams.bottomMargin = AndroidUtilities.dp(16.0f);
            } else {
                r12.pickerView.setPadding(0, 0, 0, 0);
                r12.pickerViewSendButton.setImageResource(C0446R.drawable.ic_send);
                r12.pickerViewSendButton.setPadding(AndroidUtilities.dp(4.0f), 0, 0, 0);
                layoutParams.bottomMargin = 0;
            }
            r12.itemsLayout.setLayoutParams(layoutParams);
        }
        return openPhoto(null, null, null, arrayList, i, photoViewerProvider, chatActivity, 0, 0);
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

    public boolean openPhoto(org.telegram.messenger.MessageObject r19, org.telegram.tgnet.TLRPC.FileLocation r20, java.util.ArrayList<org.telegram.messenger.MessageObject> r21, java.util.ArrayList<java.lang.Object> r22, int r23, org.telegram.ui.PhotoViewer.PhotoViewerProvider r24, org.telegram.ui.ChatActivity r25, long r26, long r28) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r18 = this;
        r8 = r18;
        r2 = r19;
        r3 = r20;
        r9 = r22;
        r1 = r24;
        r4 = r8.parentActivity;
        r10 = 0;
        if (r4 == 0) goto L_0x03d5;
    L_0x000f:
        r4 = r8.isVisible;
        if (r4 != 0) goto L_0x03d5;
    L_0x0013:
        if (r1 != 0) goto L_0x001b;
    L_0x0015:
        r4 = r18.checkAnimation();
        if (r4 != 0) goto L_0x03d5;
    L_0x001b:
        if (r2 != 0) goto L_0x0025;
    L_0x001d:
        if (r3 != 0) goto L_0x0025;
    L_0x001f:
        if (r21 != 0) goto L_0x0025;
    L_0x0021:
        if (r9 != 0) goto L_0x0025;
    L_0x0023:
        goto L_0x03d5;
    L_0x0025:
        r6 = r23;
        r11 = r1.getPlaceForPhoto(r2, r3, r6);
        if (r11 != 0) goto L_0x0030;
    L_0x002d:
        if (r9 != 0) goto L_0x0030;
    L_0x002f:
        return r10;
    L_0x0030:
        r12 = 0;
        r8.lastInsets = r12;
        r5 = r8.parentActivity;
        r7 = "window";
        r5 = r5.getSystemService(r7);
        r5 = (android.view.WindowManager) r5;
        r7 = r8.attachedToWindow;
        if (r7 == 0) goto L_0x0046;
    L_0x0041:
        r7 = r8.windowView;	 Catch:{ Exception -> 0x0046 }
        r5.removeView(r7);	 Catch:{ Exception -> 0x0046 }
    L_0x0046:
        r7 = r8.windowLayoutParams;	 Catch:{ Exception -> 0x03ce }
        r13 = 99;	 Catch:{ Exception -> 0x03ce }
        r7.type = r13;	 Catch:{ Exception -> 0x03ce }
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x03ce }
        r13 = 21;	 Catch:{ Exception -> 0x03ce }
        if (r7 < r13) goto L_0x005a;	 Catch:{ Exception -> 0x03ce }
    L_0x0052:
        r7 = r8.windowLayoutParams;	 Catch:{ Exception -> 0x03ce }
        r14 = -NUM; // 0xffffffff80010108 float:-9.2205E-41 double:NaN;	 Catch:{ Exception -> 0x03ce }
        r7.flags = r14;	 Catch:{ Exception -> 0x03ce }
        goto L_0x0060;	 Catch:{ Exception -> 0x03ce }
    L_0x005a:
        r7 = r8.windowLayoutParams;	 Catch:{ Exception -> 0x03ce }
        r14 = 8;	 Catch:{ Exception -> 0x03ce }
        r7.flags = r14;	 Catch:{ Exception -> 0x03ce }
    L_0x0060:
        r7 = r8.windowLayoutParams;	 Catch:{ Exception -> 0x03ce }
        r14 = 272; // 0x110 float:3.81E-43 double:1.344E-321;	 Catch:{ Exception -> 0x03ce }
        r7.softInputMode = r14;	 Catch:{ Exception -> 0x03ce }
        r7 = r8.windowView;	 Catch:{ Exception -> 0x03ce }
        r7.setFocusable(r10);	 Catch:{ Exception -> 0x03ce }
        r7 = r8.containerView;	 Catch:{ Exception -> 0x03ce }
        r7.setFocusable(r10);	 Catch:{ Exception -> 0x03ce }
        r7 = r8.windowView;	 Catch:{ Exception -> 0x03ce }
        r15 = r8.windowLayoutParams;	 Catch:{ Exception -> 0x03ce }
        r5.addView(r7, r15);	 Catch:{ Exception -> 0x03ce }
        r8.doneButtonPressed = r10;
        r7 = r25;
        r8.parentChatActivity = r7;
        r7 = r8.actionBar;
        r15 = "Of";
        r14 = NUM; // 0x7f0c048d float:1.8611555E38 double:1.053097974E-314;
        r12 = 2;
        r13 = new java.lang.Object[r12];
        r12 = 1;
        r16 = java.lang.Integer.valueOf(r12);
        r13[r10] = r16;
        r16 = java.lang.Integer.valueOf(r12);
        r13[r12] = r16;
        r13 = org.telegram.messenger.LocaleController.formatString(r15, r14, r13);
        r7.setTitle(r13);
        r7 = r8.currentAccount;
        r7 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r13 = org.telegram.messenger.NotificationCenter.FileDidFailedLoad;
        r7.addObserver(r8, r13);
        r7 = r8.currentAccount;
        r7 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r13 = org.telegram.messenger.NotificationCenter.FileDidLoaded;
        r7.addObserver(r8, r13);
        r7 = r8.currentAccount;
        r7 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r13 = org.telegram.messenger.NotificationCenter.FileLoadProgressChanged;
        r7.addObserver(r8, r13);
        r7 = r8.currentAccount;
        r7 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r13 = org.telegram.messenger.NotificationCenter.mediaCountDidLoaded;
        r7.addObserver(r8, r13);
        r7 = r8.currentAccount;
        r7 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r13 = org.telegram.messenger.NotificationCenter.mediaDidLoaded;
        r7.addObserver(r8, r13);
        r7 = r8.currentAccount;
        r7 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r13 = org.telegram.messenger.NotificationCenter.dialogPhotosLoaded;
        r7.addObserver(r8, r13);
        r7 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r13 = org.telegram.messenger.NotificationCenter.emojiDidLoaded;
        r7.addObserver(r8, r13);
        r7 = r8.currentAccount;
        r7 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r13 = org.telegram.messenger.NotificationCenter.FilePreparingFailed;
        r7.addObserver(r8, r13);
        r7 = r8.currentAccount;
        r7 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r13 = org.telegram.messenger.NotificationCenter.FileNewChunkAvailable;
        r7.addObserver(r8, r13);
        r8.placeProvider = r1;
        r13 = r28;
        r8.mergeDialogId = r13;
        r13 = r26;
        r8.currentDialogId = r13;
        r1 = r8.selectedPhotosAdapter;
        r1.notifyDataSetChanged();
        r1 = r8.velocityTracker;
        if (r1 != 0) goto L_0x0115;
    L_0x010f:
        r1 = android.view.VelocityTracker.obtain();
        r8.velocityTracker = r1;
    L_0x0115:
        r8.isVisible = r12;
        r8.toggleActionBar(r12, r10);
        r8.togglePhotosListView(r10, r10);
        r13 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        if (r11 == 0) goto L_0x0388;
    L_0x0121:
        r8.disableShowCheck = r12;
        r8.animationInProgress = r12;
        if (r2 == 0) goto L_0x012f;
    L_0x0127:
        r1 = r11.imageReceiver;
        r1 = r1.getAnimation();
        r8.currentAnimation = r1;
    L_0x012f:
        r1 = r8;
        r4 = r21;
        r5 = r9;
        r7 = r11;
        r1.onPhotoShow(r2, r3, r4, r5, r6, r7);
        r1 = r11.imageReceiver;
        r1 = r1.getDrawRegion();
        r2 = r11.imageReceiver;
        r2 = r2.getOrientation();
        r3 = r11.imageReceiver;
        r3 = r3.getAnimatedOrientation();
        if (r3 == 0) goto L_0x014c;
    L_0x014b:
        r2 = r3;
    L_0x014c:
        r3 = r8.animatingImageView;
        r3.setVisibility(r10);
        r3 = r8.animatingImageView;
        r4 = r11.radius;
        r3.setRadius(r4);
        r3 = r8.animatingImageView;
        r3.setOrientation(r2);
        r2 = r8.animatingImageView;
        r3 = r11.radius;
        if (r3 == 0) goto L_0x0165;
    L_0x0163:
        r3 = r12;
        goto L_0x0166;
    L_0x0165:
        r3 = r10;
    L_0x0166:
        r2.setNeedRadius(r3);
        r2 = r8.animatingImageView;
        r3 = r11.thumb;
        r2.setImageBitmap(r3);
        r2 = r8.animatingImageView;
        r2.setAlpha(r13);
        r2 = r8.animatingImageView;
        r3 = 0;
        r2.setPivotX(r3);
        r2 = r8.animatingImageView;
        r2.setPivotY(r3);
        r2 = r8.animatingImageView;
        r4 = r11.scale;
        r2.setScaleX(r4);
        r2 = r8.animatingImageView;
        r4 = r11.scale;
        r2.setScaleY(r4);
        r2 = r8.animatingImageView;
        r4 = r11.viewX;
        r4 = (float) r4;
        r5 = r1.left;
        r5 = (float) r5;
        r6 = r11.scale;
        r5 = r5 * r6;
        r4 = r4 + r5;
        r2.setTranslationX(r4);
        r2 = r8.animatingImageView;
        r4 = r11.viewY;
        r4 = (float) r4;
        r5 = r1.top;
        r5 = (float) r5;
        r6 = r11.scale;
        r5 = r5 * r6;
        r4 = r4 + r5;
        r2.setTranslationY(r4);
        r2 = r8.animatingImageView;
        r2 = r2.getLayoutParams();
        r4 = r1.right;
        r5 = r1.left;
        r4 = r4 - r5;
        r2.width = r4;
        r4 = r1.bottom;
        r5 = r1.top;
        r4 = r4 - r5;
        r2.height = r4;
        r4 = r8.animatingImageView;
        r4.setLayoutParams(r2);
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r4 = (float) r4;
        r5 = r2.width;
        r5 = (float) r5;
        r4 = r4 / r5;
        r5 = org.telegram.messenger.AndroidUtilities.displaySize;
        r5 = r5.y;
        r6 = android.os.Build.VERSION.SDK_INT;
        r7 = 21;
        if (r6 < r7) goto L_0x01db;
    L_0x01d8:
        r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        goto L_0x01dc;
    L_0x01db:
        r6 = r10;
    L_0x01dc:
        r5 = r5 + r6;
        r5 = (float) r5;
        r6 = r2.height;
        r6 = (float) r6;
        r5 = r5 / r6;
        r6 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r6 <= 0) goto L_0x01e7;
    L_0x01e6:
        r4 = r5;
    L_0x01e7:
        r5 = r2.width;
        r5 = (float) r5;
        r5 = r5 * r4;
        r6 = r2.height;
        r6 = (float) r6;
        r6 = r6 * r4;
        r7 = org.telegram.messenger.AndroidUtilities.displaySize;
        r7 = r7.x;
        r7 = (float) r7;
        r7 = r7 - r5;
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r7 = r7 / r5;
        r13 = org.telegram.messenger.AndroidUtilities.displaySize;
        r13 = r13.y;
        r15 = android.os.Build.VERSION.SDK_INT;
        r3 = 21;
        if (r15 < r3) goto L_0x0205;
    L_0x0202:
        r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        goto L_0x0206;
    L_0x0205:
        r3 = r10;
    L_0x0206:
        r13 = r13 + r3;
        r3 = (float) r13;
        r3 = r3 - r6;
        r3 = r3 / r5;
        r5 = r1.left;
        r6 = r11.imageReceiver;
        r6 = r6.getImageX();
        r5 = r5 - r6;
        r5 = java.lang.Math.abs(r5);
        r6 = r1.top;
        r13 = r11.imageReceiver;
        r13 = r13.getImageY();
        r6 = r6 - r13;
        r6 = java.lang.Math.abs(r6);
        r13 = 2;
        r15 = new int[r13];
        r13 = r11.parentView;
        r13.getLocationInWindow(r15);
        r13 = r15[r12];
        r14 = android.os.Build.VERSION.SDK_INT;
        r10 = 21;
        if (r14 < r10) goto L_0x0236;
    L_0x0234:
        r10 = 0;
        goto L_0x0238;
    L_0x0236:
        r10 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
    L_0x0238:
        r13 = r13 - r10;
        r10 = r11.viewY;
        r14 = r1.top;
        r10 = r10 + r14;
        r13 = r13 - r10;
        r10 = r11.clipTopAddition;
        r10 = r10 + r13;
        if (r10 >= 0) goto L_0x0245;
    L_0x0244:
        r10 = 0;
    L_0x0245:
        r13 = r11.viewY;
        r1 = r1.top;
        r13 = r13 + r1;
        r1 = r2.height;
        r13 = r13 + r1;
        r1 = r15[r12];
        r2 = r11.parentView;
        r2 = r2.getHeight();
        r1 = r1 + r2;
        r2 = android.os.Build.VERSION.SDK_INT;
        r14 = 21;
        if (r2 < r14) goto L_0x025e;
    L_0x025c:
        r2 = 0;
        goto L_0x0260;
    L_0x025e:
        r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
    L_0x0260:
        r1 = r1 - r2;
        r13 = r13 - r1;
        r1 = r11.clipBottomAddition;
        r1 = r1 + r13;
        if (r1 >= 0) goto L_0x0268;
    L_0x0267:
        r1 = 0;
    L_0x0268:
        r2 = java.lang.Math.max(r10, r6);
        r1 = java.lang.Math.max(r1, r6);
        r6 = r8.animationValues;
        r10 = 0;
        r6 = r6[r10];
        r13 = r8.animatingImageView;
        r13 = r13.getScaleX();
        r6[r10] = r13;
        r6 = r8.animationValues;
        r6 = r6[r10];
        r13 = r8.animatingImageView;
        r13 = r13.getScaleY();
        r6[r12] = r13;
        r6 = r8.animationValues;
        r6 = r6[r10];
        r13 = r8.animatingImageView;
        r13 = r13.getTranslationX();
        r14 = 2;
        r6[r14] = r13;
        r6 = r8.animationValues;
        r6 = r6[r10];
        r13 = r8.animatingImageView;
        r13 = r13.getTranslationY();
        r14 = 3;
        r6[r14] = r13;
        r6 = r8.animationValues;
        r6 = r6[r10];
        r5 = (float) r5;
        r13 = r11.scale;
        r5 = r5 * r13;
        r13 = 4;
        r6[r13] = r5;
        r5 = r8.animationValues;
        r5 = r5[r10];
        r2 = (float) r2;
        r6 = r11.scale;
        r2 = r2 * r6;
        r6 = 5;
        r5[r6] = r2;
        r2 = r8.animationValues;
        r2 = r2[r10];
        r1 = (float) r1;
        r5 = r11.scale;
        r1 = r1 * r5;
        r5 = 6;
        r2[r5] = r1;
        r1 = r8.animationValues;
        r1 = r1[r10];
        r2 = r8.animatingImageView;
        r2 = r2.getRadius();
        r2 = (float) r2;
        r14 = 7;
        r1[r14] = r2;
        r1 = r8.animationValues;
        r1 = r1[r12];
        r1[r10] = r4;
        r1 = r8.animationValues;
        r1 = r1[r12];
        r1[r12] = r4;
        r1 = r8.animationValues;
        r1 = r1[r12];
        r2 = 2;
        r1[r2] = r7;
        r1 = r8.animationValues;
        r1 = r1[r12];
        r2 = 3;
        r1[r2] = r3;
        r1 = r8.animationValues;
        r1 = r1[r12];
        r2 = 0;
        r1[r13] = r2;
        r1 = r8.animationValues;
        r1 = r1[r12];
        r1[r6] = r2;
        r1 = r8.animationValues;
        r1 = r1[r12];
        r1[r5] = r2;
        r1 = r8.animationValues;
        r1 = r1[r12];
        r1[r14] = r2;
        r1 = r8.animatingImageView;
        r1.setAnimationProgress(r2);
        r1 = r8.backgroundDrawable;
        r3 = 0;
        r1.setAlpha(r3);
        r1 = r8.containerView;
        r1.setAlpha(r2);
        r1 = new android.animation.AnimatorSet;
        r1.<init>();
        r2 = 3;
        r2 = new android.animation.Animator[r2];
        r4 = r8.animatingImageView;
        r5 = "animationProgress";
        r6 = 2;
        r7 = new float[r6];
        r7 = {0, NUM};
        r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r7);
        r2[r3] = r4;
        r3 = r8.backgroundDrawable;
        r4 = "alpha";
        r5 = new int[r6];
        r5 = {0, 255};
        r3 = android.animation.ObjectAnimator.ofInt(r3, r4, r5);
        r2[r12] = r3;
        r3 = r8.containerView;
        r4 = "alpha";
        r5 = new float[r6];
        r5 = {0, NUM};
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5);
        r2[r6] = r3;
        r1.playTogether(r2);
        r2 = new org.telegram.ui.PhotoViewer$64;
        r2.<init>(r9);
        r8.animationEndRunnable = r2;
        r2 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r1.setDuration(r2);
        r2 = new org.telegram.ui.PhotoViewer$65;
        r2.<init>();
        r1.addListener(r2);
        r2 = java.lang.System.currentTimeMillis();
        r8.transitionAnimationStartTime = r2;
        r2 = new org.telegram.ui.PhotoViewer$66;
        r2.<init>(r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 18;
        if (r1 < r2) goto L_0x037d;
    L_0x0376:
        r1 = r8.containerView;
        r2 = 2;
        r3 = 0;
        r1.setLayerType(r2, r3);
    L_0x037d:
        r1 = r8.backgroundDrawable;
        r2 = new org.telegram.ui.PhotoViewer$67;
        r2.<init>(r11);
        r1.drawRunnable = r2;
        goto L_0x03cd;
    L_0x0388:
        if (r9 == 0) goto L_0x03b9;
    L_0x038a:
        r1 = r8.sendPhotoType;
        r7 = 3;
        if (r1 == r7) goto L_0x03b9;
    L_0x038f:
        r1 = android.os.Build.VERSION.SDK_INT;
        r7 = 21;
        if (r1 < r7) goto L_0x039d;
    L_0x0395:
        r1 = r8.windowLayoutParams;
        r7 = -NUM; // 0xffffffff80010100 float:-9.2194E-41 double:NaN;
        r1.flags = r7;
        goto L_0x03a2;
    L_0x039d:
        r1 = r8.windowLayoutParams;
        r7 = 0;
        r1.flags = r7;
    L_0x03a2:
        r1 = r8.windowLayoutParams;
        r7 = 272; // 0x110 float:3.81E-43 double:1.344E-321;
        r1.softInputMode = r7;
        r1 = r8.windowView;
        r7 = r8.windowLayoutParams;
        r5.updateViewLayout(r1, r7);
        r1 = r8.windowView;
        r1.setFocusable(r12);
        r1 = r8.containerView;
        r1.setFocusable(r12);
    L_0x03b9:
        r1 = r8.backgroundDrawable;
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r1.setAlpha(r5);
        r1 = r8.containerView;
        r1.setAlpha(r13);
        r1 = r8;
        r4 = r21;
        r5 = r9;
        r7 = r11;
        r1.onPhotoShow(r2, r3, r4, r5, r6, r7);
    L_0x03cd:
        return r12;
    L_0x03ce:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
        r1 = 0;
        return r1;
    L_0x03d5:
        r1 = r10;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.openPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, java.util.ArrayList, java.util.ArrayList, int, org.telegram.ui.PhotoViewer$PhotoViewerProvider, org.telegram.ui.ChatActivity, long, long):boolean");
    }

    public void closePhoto(boolean z, boolean z2) {
        PhotoViewer photoViewer = this;
        if (z2 || photoViewer.currentEditMode == 0) {
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
                        if (!photoViewer.captionEditText.hideActionMode() || z2) {
                            releasePlayer();
                            photoViewer.captionEditText.onDestroy();
                            photoViewer.parentChatActivity = null;
                            removeObservers();
                            photoViewer.isActionBarVisible = false;
                            if (photoViewer.velocityTracker != null) {
                                photoViewer.velocityTracker.recycle();
                                photoViewer.velocityTracker = null;
                            }
                            final PlaceProviderObject placeForPhoto = photoViewer.placeProvider.getPlaceForPhoto(photoViewer.currentMessageObject, photoViewer.currentFileLocation, photoViewer.currentIndex);
                            if (photoViewer.isInline) {
                                photoViewer.isInline = false;
                                photoViewer.animationInProgress = 0;
                                onPhotoClosed(placeForPhoto);
                                photoViewer.containerView.setScaleX(1.0f);
                                photoViewer.containerView.setScaleY(1.0f);
                            } else {
                                Object obj;
                                Animator[] animatorArr;
                                if (z) {
                                    Rect drawRegion;
                                    photoViewer.animationInProgress = 1;
                                    photoViewer.animatingImageView.setVisibility(0);
                                    photoViewer.containerView.invalidate();
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    ViewGroup.LayoutParams layoutParams2 = photoViewer.animatingImageView.getLayoutParams();
                                    int orientation = photoViewer.centerImage.getOrientation();
                                    int animatedOrientation = (placeForPhoto == null || placeForPhoto.imageReceiver == null) ? 0 : placeForPhoto.imageReceiver.getAnimatedOrientation();
                                    if (animatedOrientation != 0) {
                                        orientation = animatedOrientation;
                                    }
                                    photoViewer.animatingImageView.setOrientation(orientation);
                                    if (placeForPhoto != null) {
                                        photoViewer.animatingImageView.setNeedRadius(placeForPhoto.radius != 0);
                                        drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
                                        layoutParams2.width = drawRegion.right - drawRegion.left;
                                        layoutParams2.height = drawRegion.bottom - drawRegion.top;
                                        photoViewer.animatingImageView.setImageBitmap(placeForPhoto.thumb);
                                    } else {
                                        photoViewer.animatingImageView.setNeedRadius(false);
                                        layoutParams2.width = photoViewer.centerImage.getImageWidth();
                                        layoutParams2.height = photoViewer.centerImage.getImageHeight();
                                        photoViewer.animatingImageView.setImageBitmap(photoViewer.centerImage.getBitmapSafe());
                                        drawRegion = null;
                                    }
                                    photoViewer.animatingImageView.setLayoutParams(layoutParams2);
                                    float f = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams2.width);
                                    float f2 = ((float) (AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / ((float) layoutParams2.height);
                                    if (f <= f2) {
                                        f2 = f;
                                    }
                                    float f3 = (((float) (AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) - ((((float) layoutParams2.height) * photoViewer.scale) * f2)) / 2.0f;
                                    photoViewer.animatingImageView.setTranslationX(((((float) AndroidUtilities.displaySize.x) - ((((float) layoutParams2.width) * photoViewer.scale) * f2)) / 2.0f) + photoViewer.translationX);
                                    photoViewer.animatingImageView.setTranslationY(f3 + photoViewer.translationY);
                                    photoViewer.animatingImageView.setScaleX(photoViewer.scale * f2);
                                    photoViewer.animatingImageView.setScaleY(photoViewer.scale * f2);
                                    int height;
                                    if (placeForPhoto != null) {
                                        placeForPhoto.imageReceiver.setVisible(false, true);
                                        int abs = Math.abs(drawRegion.left - placeForPhoto.imageReceiver.getImageX());
                                        int abs2 = Math.abs(drawRegion.top - placeForPhoto.imageReceiver.getImageY());
                                        int[] iArr = new int[2];
                                        placeForPhoto.parentView.getLocationInWindow(iArr);
                                        int i = ((iArr[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (placeForPhoto.viewY + drawRegion.top)) + placeForPhoto.clipTopAddition;
                                        if (i < 0) {
                                            i = 0;
                                        }
                                        height = placeForPhoto.clipBottomAddition + (((placeForPhoto.viewY + drawRegion.top) + (drawRegion.bottom - drawRegion.top)) - ((iArr[1] + placeForPhoto.parentView.getHeight()) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)));
                                        if (height < 0) {
                                            height = 0;
                                        }
                                        int max = Math.max(i, abs2);
                                        height = Math.max(height, abs2);
                                        photoViewer.animationValues[0][0] = photoViewer.animatingImageView.getScaleX();
                                        photoViewer.animationValues[0][1] = photoViewer.animatingImageView.getScaleY();
                                        photoViewer.animationValues[0][2] = photoViewer.animatingImageView.getTranslationX();
                                        photoViewer.animationValues[0][3] = photoViewer.animatingImageView.getTranslationY();
                                        photoViewer.animationValues[0][4] = 0.0f;
                                        photoViewer.animationValues[0][5] = 0.0f;
                                        photoViewer.animationValues[0][6] = 0.0f;
                                        photoViewer.animationValues[0][7] = 0.0f;
                                        photoViewer.animationValues[1][0] = placeForPhoto.scale;
                                        photoViewer.animationValues[1][1] = placeForPhoto.scale;
                                        photoViewer.animationValues[1][2] = (((float) placeForPhoto.viewX) + (((float) drawRegion.left) * placeForPhoto.scale)) - ((float) getLeftInset());
                                        photoViewer.animationValues[1][3] = ((float) placeForPhoto.viewY) + (((float) drawRegion.top) * placeForPhoto.scale);
                                        photoViewer.animationValues[1][4] = ((float) abs) * placeForPhoto.scale;
                                        photoViewer.animationValues[1][5] = ((float) max) * placeForPhoto.scale;
                                        photoViewer.animationValues[1][6] = ((float) height) * placeForPhoto.scale;
                                        photoViewer.animationValues[1][7] = (float) placeForPhoto.radius;
                                        r3 = new Animator[3];
                                        r3[1] = ObjectAnimator.ofInt(photoViewer.backgroundDrawable, "alpha", new int[]{0});
                                        r3[2] = ObjectAnimator.ofFloat(photoViewer.containerView, "alpha", new float[]{0.0f});
                                        animatorSet.playTogether(r3);
                                    } else {
                                        height = AndroidUtilities.displaySize.y + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                                        animatorArr = new Animator[4];
                                        animatorArr[0] = ObjectAnimator.ofInt(photoViewer.backgroundDrawable, "alpha", new int[]{0});
                                        animatorArr[1] = ObjectAnimator.ofFloat(photoViewer.animatingImageView, "alpha", new float[]{0.0f});
                                        ClippingImageView clippingImageView = photoViewer.animatingImageView;
                                        String str = "translationY";
                                        float[] fArr = new float[1];
                                        if (photoViewer.translationY < 0.0f) {
                                            height = -height;
                                        }
                                        fArr[0] = (float) height;
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
                                            PhotoViewer.this.onPhotoClosed(placeForPhoto);
                                        }
                                    };
                                    animatorSet.setDuration(200);
                                    animatorSet.addListener(new AnimatorListenerAdapter() {

                                        /* renamed from: org.telegram.ui.PhotoViewer$70$1 */
                                        class C16151 implements Runnable {
                                            C16151() {
                                            }

                                            public void run() {
                                                if (PhotoViewer.this.animationEndRunnable != null) {
                                                    PhotoViewer.this.animationEndRunnable.run();
                                                    PhotoViewer.this.animationEndRunnable = null;
                                                }
                                            }
                                        }

                                        public void onAnimationEnd(Animator animator) {
                                            AndroidUtilities.runOnUIThread(new C16151());
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
                                                PhotoViewer.this.onPhotoClosed(placeForPhoto);
                                                PhotoViewer.this.containerView.setScaleX(1.0f);
                                                PhotoViewer.this.containerView.setScaleY(1.0f);
                                            }
                                        }
                                    };
                                    animatorSet2.setDuration(200);
                                    animatorSet2.addListener(new AnimatorListenerAdapter() {
                                        public void onAnimationEnd(Animator animator) {
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

    private void onPhotoClosed(PlaceProviderObject placeProviderObject) {
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
        for (int i = 0; i < 3; i++) {
            if (this.photoProgressViews[i] != null) {
                this.photoProgressViews[i].setBackgroundState(-1, false);
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
        if (placeProviderObject != null) {
            placeProviderObject.imageReceiver.setVisible(true, true);
        }
    }

    private void redraw(final int i) {
        if (i < 6 && this.containerView != null) {
            this.containerView.invalidate();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    PhotoViewer.this.redraw(i + 1);
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

    public void onConfigurationChanged(Configuration configuration) {
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

    private void updateMinMax(float f) {
        int imageWidth = ((int) ((((float) this.centerImage.getImageWidth()) * f) - ((float) getContainerViewWidth()))) / 2;
        f = ((int) ((((float) this.centerImage.getImageHeight()) * f) - ((float) getContainerViewHeight()))) / 2;
        if (imageWidth > 0) {
            this.minX = (float) (-imageWidth);
            this.maxX = (float) imageWidth;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (f > null) {
            this.minY = (float) (-f);
            this.maxY = (float) f;
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
        return (this.currentEditMode == 0 || this.currentEditMode == 3) ? 0 : AndroidUtilities.dp(14.0f);
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

    private int getContainerViewWidth(int i) {
        int width = this.containerView.getWidth();
        return (i == 0 || i == 3) ? width : width - AndroidUtilities.dp(NUM);
    }

    private int getContainerViewHeight() {
        return getContainerViewHeight(this.currentEditMode);
    }

    private int getContainerViewHeight(int i) {
        int i2 = AndroidUtilities.displaySize.y;
        if (i == 0 && VERSION.SDK_INT >= 21) {
            i2 += AndroidUtilities.statusBarHeight;
        }
        if (i == 1) {
            return i2 - AndroidUtilities.dp(NUM);
        }
        if (i == 2) {
            return i2 - AndroidUtilities.dp(NUM);
        }
        return i == 3 ? i2 - (AndroidUtilities.dp(NUM) + ActionBar.getCurrentActionBarHeight()) : i2;
    }

    private boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.animationInProgress == 0) {
            if (this.animationStartTime == 0) {
                if (this.currentEditMode == 2) {
                    this.photoFilterView.onTouch(motionEvent);
                    return true;
                } else if (this.currentEditMode == 1) {
                    return true;
                } else {
                    if (!this.captionEditText.isPopupShowing()) {
                        if (!this.captionEditText.isKeyboardVisible()) {
                            if (this.currentEditMode == 0 && motionEvent.getPointerCount() == 1 && this.gestureDetector.onTouchEvent(motionEvent) && this.doubleTap) {
                                this.doubleTap = false;
                                this.moving = false;
                                this.zooming = false;
                                checkMinMax(false);
                                return true;
                            }
                            if (motionEvent.getActionMasked() != 0) {
                                if (motionEvent.getActionMasked() != 5) {
                                    float f = 0.0f;
                                    float abs;
                                    if (motionEvent.getActionMasked() == 2) {
                                        if (this.currentEditMode == 1) {
                                            this.photoCropView.cancelAnimationRunnable();
                                        }
                                        if (this.canZoom && motionEvent.getPointerCount() == 2 && !this.draggingDown && this.zooming && !this.changingPage) {
                                            this.discardTap = true;
                                            this.scale = (((float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)))) / this.pinchStartDistance) * this.pinchStartScale;
                                            this.translationX = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                                            this.translationY = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (this.scale / this.pinchStartScale));
                                            updateMinMax(this.scale);
                                            this.containerView.invalidate();
                                        } else if (motionEvent.getPointerCount() == 1) {
                                            if (this.velocityTracker != null) {
                                                this.velocityTracker.addMovement(motionEvent);
                                            }
                                            abs = Math.abs(motionEvent.getX() - this.moveStartX);
                                            float abs2 = Math.abs(motionEvent.getY() - this.dragY);
                                            if (abs > ((float) AndroidUtilities.dp(3.0f)) || abs2 > ((float) AndroidUtilities.dp(3.0f))) {
                                                this.discardTap = true;
                                                if (this.qualityChooseView != null && this.qualityChooseView.getVisibility() == 0) {
                                                    return true;
                                                }
                                            }
                                            if (this.placeProvider.canScrollAway() && this.currentEditMode == 0 && this.canDragDown && !this.draggingDown && this.scale == 1.0f && abs2 >= ((float) AndroidUtilities.dp(30.0f)) && abs2 / 2.0f > abs) {
                                                this.draggingDown = true;
                                                this.moving = false;
                                                this.dragY = motionEvent.getY();
                                                if (this.isActionBarVisible != null && this.containerView.getTag() != null) {
                                                    toggleActionBar(false, true);
                                                } else if (this.pickerView.getVisibility() == null) {
                                                    toggleActionBar(false, true);
                                                    togglePhotosListView(false, true);
                                                    toggleCheckImageView(false);
                                                }
                                                return true;
                                            } else if (this.draggingDown) {
                                                this.translationY = motionEvent.getY() - this.dragY;
                                                this.containerView.invalidate();
                                            } else if (this.invalidCoords || this.animationStartTime != 0) {
                                                this.invalidCoords = false;
                                                this.moveStartX = motionEvent.getX();
                                                this.moveStartY = motionEvent.getY();
                                            } else {
                                                abs = this.moveStartX - motionEvent.getX();
                                                abs2 = this.moveStartY - motionEvent.getY();
                                                if (this.moving || this.currentEditMode != 0 || ((this.scale == 1.0f && Math.abs(abs2) + ((float) AndroidUtilities.dp(12.0f)) < Math.abs(abs)) || this.scale != 1.0f)) {
                                                    if (!this.moving) {
                                                        this.moving = true;
                                                        this.canDragDown = false;
                                                        abs = 0.0f;
                                                        abs2 = abs;
                                                    }
                                                    this.moveStartX = motionEvent.getX();
                                                    this.moveStartY = motionEvent.getY();
                                                    updateMinMax(this.scale);
                                                    if ((this.translationX < this.minX && (this.currentEditMode != null || this.rightImage.hasImage() == null)) || (this.translationX > this.maxX && (this.currentEditMode != null || this.leftImage.hasImage() == null))) {
                                                        abs /= 3.0f;
                                                    }
                                                    if (this.maxY == null && this.minY == null && this.currentEditMode == null) {
                                                        if (this.translationY - abs2 < this.minY) {
                                                            this.translationY = this.minY;
                                                        } else if (this.translationY - abs2 > this.maxY) {
                                                            this.translationY = this.maxY;
                                                        }
                                                        this.translationX -= abs;
                                                        this.translationY -= f;
                                                        this.containerView.invalidate();
                                                    } else {
                                                        if (this.translationY >= this.minY) {
                                                            if (this.translationY > this.maxY) {
                                                            }
                                                        }
                                                        f = abs2 / 3.0f;
                                                        this.translationX -= abs;
                                                        if (!(this.scale == NUM && this.currentEditMode == null)) {
                                                            this.translationY -= f;
                                                        }
                                                        this.containerView.invalidate();
                                                    }
                                                    f = abs2;
                                                    this.translationX -= abs;
                                                    this.translationY -= f;
                                                    this.containerView.invalidate();
                                                }
                                            }
                                        }
                                    } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                                        if (this.currentEditMode == 1) {
                                            this.photoCropView.startAnimationRunnable();
                                        }
                                        if (this.zooming) {
                                            this.invalidCoords = true;
                                            if (this.scale < NUM) {
                                                updateMinMax(1.0f);
                                                animateTo(1.0f, 0.0f, 0.0f, true);
                                            } else if (this.scale > NUM) {
                                                motionEvent = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                                                abs = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                                                updateMinMax(3.0f);
                                                if (motionEvent < this.minX) {
                                                    motionEvent = this.minX;
                                                } else if (motionEvent > this.maxX) {
                                                    motionEvent = this.maxX;
                                                }
                                                if (abs < this.minY) {
                                                    abs = this.minY;
                                                } else if (abs > this.maxY) {
                                                    abs = this.maxY;
                                                }
                                                animateTo(3.0f, motionEvent, abs, true);
                                            } else {
                                                checkMinMax(true);
                                            }
                                            this.zooming = false;
                                        } else if (this.draggingDown) {
                                            if (Math.abs(this.dragY - motionEvent.getY()) > ((float) getContainerViewHeight()) / 6.0f) {
                                                closePhoto(true, false);
                                            } else {
                                                if (this.pickerView.getVisibility() == null) {
                                                    toggleActionBar(true, true);
                                                    toggleCheckImageView(true);
                                                }
                                                animateTo(1.0f, 0.0f, 0.0f, false);
                                            }
                                            this.draggingDown = false;
                                        } else if (this.moving != null) {
                                            motionEvent = this.translationX;
                                            abs = this.translationY;
                                            updateMinMax(this.scale);
                                            this.moving = false;
                                            this.canDragDown = true;
                                            if (this.velocityTracker != null && this.scale == 1.0f) {
                                                this.velocityTracker.computeCurrentVelocity(1000);
                                                f = this.velocityTracker.getXVelocity();
                                            }
                                            if (this.currentEditMode == 0) {
                                                if ((this.translationX < this.minX - ((float) (getContainerViewWidth() / 3)) || r9 < ((float) (-AndroidUtilities.dp(650.0f)))) && this.rightImage.hasImage()) {
                                                    goToNext();
                                                    return true;
                                                } else if ((this.translationX > this.maxX + ((float) (getContainerViewWidth() / 3)) || r9 > ((float) AndroidUtilities.dp(650.0f))) && this.leftImage.hasImage()) {
                                                    goToPrev();
                                                    return true;
                                                }
                                            }
                                            if (this.translationX < this.minX) {
                                                motionEvent = this.minX;
                                            } else if (this.translationX > this.maxX) {
                                                motionEvent = this.maxX;
                                            }
                                            if (this.translationY < this.minY) {
                                                abs = this.minY;
                                            } else if (this.translationY > this.maxY) {
                                                abs = this.maxY;
                                            }
                                            animateTo(this.scale, motionEvent, abs, false);
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
                                if (this.canZoom && motionEvent.getPointerCount() == 2) {
                                    this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                                    this.pinchStartScale = this.scale;
                                    this.pinchCenterX = (motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f;
                                    this.pinchCenterY = (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f;
                                    this.pinchStartX = this.translationX;
                                    this.pinchStartY = this.translationY;
                                    this.zooming = true;
                                    this.moving = false;
                                    if (this.velocityTracker != null) {
                                        this.velocityTracker.clear();
                                    }
                                } else if (motionEvent.getPointerCount() == 1) {
                                    this.moveStartX = motionEvent.getX();
                                    motionEvent = motionEvent.getY();
                                    this.moveStartY = motionEvent;
                                    this.dragY = motionEvent;
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
                    if (motionEvent.getAction() == 1) {
                        closeCaptionEnter(true);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void checkMinMax(boolean z) {
        float f = this.translationX;
        float f2 = this.translationY;
        updateMinMax(this.scale);
        if (this.translationX < this.minX) {
            f = this.minX;
        } else if (this.translationX > this.maxX) {
            f = this.maxX;
        }
        if (this.translationY < this.minY) {
            f2 = this.minY;
        } else if (this.translationY > this.maxY) {
            f2 = this.maxY;
        }
        animateTo(this.scale, f, f2, z);
    }

    private void goToNext() {
        float containerViewWidth = this.scale != 1.0f ? ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale : 0.0f;
        this.switchImageAfterAnimation = 1;
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - containerViewWidth) - ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void goToPrev() {
        float containerViewWidth = this.scale != 1.0f ? ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale : 0.0f;
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, ((this.maxX + ((float) getContainerViewWidth())) + containerViewWidth) + ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void animateTo(float f, float f2, float f3, boolean z) {
        animateTo(f, f2, f3, z, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    }

    private void animateTo(float f, float f2, float f3, boolean z, int i) {
        if (this.scale != f || this.translationX != f2 || this.translationY != f3) {
            this.zoomAnimation = z;
            this.animateToScale = f;
            this.animateToX = f2;
            this.animateToY = f3;
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f})});
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) i);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PhotoViewer.this.imageMoveAnimation = null;
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    @Keep
    public void setAnimationValue(float f) {
        this.animationValue = f;
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
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PhotoViewer.this.hintAnimation) != null) {
                    PhotoViewer.this.hintAnimation = null;
                    PhotoViewer.this.hintHideRunnable = null;
                    if (PhotoViewer.this.hintTextView != null) {
                        PhotoViewer.this.hintTextView.setVisibility(8);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(PhotoViewer.this.hintAnimation) != null) {
                    PhotoViewer.this.hintHideRunnable = null;
                    PhotoViewer.this.hintHideRunnable = null;
                }
            }
        });
        this.hintAnimation.setDuration(300);
        this.hintAnimation.start();
    }

    private void showHint(boolean z, boolean z2) {
        if (this.containerView != null) {
            if (!z || this.hintTextView != null) {
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
                if (z) {
                    if (this.hintAnimation) {
                        this.hintAnimation.cancel();
                        this.hintAnimation = null;
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.hintHideRunnable);
                    this.hintHideRunnable = null;
                    hideHint();
                    return;
                }
                int i;
                z = this.hintTextView;
                if (z2) {
                    z2 = "GroupPhotosHelp";
                    i = C0446R.string.GroupPhotosHelp;
                } else {
                    z2 = "SinglePhotosHelp";
                    i = C0446R.string.SinglePhotosHelp;
                }
                z.setText(LocaleController.getString(z2, i));
                if (this.hintHideRunnable) {
                    if (this.hintAnimation) {
                        this.hintAnimation.cancel();
                        this.hintAnimation = null;
                    } else {
                        AndroidUtilities.cancelRunOnUIThread(this.hintHideRunnable);
                        z = new Runnable() {
                            public void run() {
                                PhotoViewer.this.hideHint();
                            }
                        };
                        this.hintHideRunnable = z;
                        AndroidUtilities.runOnUIThread(z, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        return;
                    }
                } else if (this.hintAnimation) {
                    return;
                }
                this.hintTextView.setVisibility(0);
                this.hintAnimation = new AnimatorSet();
                z = this.hintAnimation;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{1.0f});
                z.playTogether(animatorArr);
                this.hintAnimation.addListener(new AnimatorListenerAdapter() {

                    /* renamed from: org.telegram.ui.PhotoViewer$78$1 */
                    class C16161 implements Runnable {
                        C16161() {
                        }

                        public void run() {
                            PhotoViewer.this.hideHint();
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(PhotoViewer.this.hintAnimation) != null) {
                            PhotoViewer.this.hintAnimation = null;
                            AndroidUtilities.runOnUIThread(PhotoViewer.this.hintHideRunnable = new C16161(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (animator.equals(PhotoViewer.this.hintAnimation) != null) {
                            PhotoViewer.this.hintAnimation = null;
                        }
                    }
                });
                this.hintAnimation.setDuration(300);
                this.hintAnimation.start();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"NewApi", "DrawAllocation"})
    private void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.animationInProgress != 1) {
            if (r1.isVisible || r1.animationInProgress == 2 || r1.pipAnimationInProgress) {
                float f;
                float f2;
                float f3;
                float f4;
                float containerViewHeight;
                ImageReceiver imageReceiver;
                float f5;
                float f6;
                float f7;
                float f8;
                float containerViewWidth;
                float containerViewWidth2;
                int i;
                float f9;
                ImageReceiver imageReceiver2;
                Object obj;
                ImageReceiver imageReceiver3;
                int i2;
                PipVideoView pipVideoView;
                if (r1.imageMoveAnimation != null) {
                    if (!r1.scroller.isFinished()) {
                        r1.scroller.abortAnimation();
                    }
                    f = r1.scale + ((r1.animateToScale - r1.scale) * r1.animationValue);
                    f2 = r1.translationX + ((r1.animateToX - r1.translationX) * r1.animationValue);
                    f3 = r1.translationY + ((r1.animateToY - r1.translationY) * r1.animationValue);
                    if (r1.currentEditMode == 1) {
                        r1.photoCropView.setAnimationProgress(r1.animationValue);
                    }
                    f4 = (r1.animateToScale == 1.0f && r1.scale == 1.0f && r1.translationX == 0.0f) ? f3 : -1.0f;
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
                    f = r1.scale;
                    f3 = r1.translationY;
                    f2 = r1.translationX;
                    f4 = !r1.moving ? r1.translationY : -1.0f;
                }
                if (!(r1.animationInProgress == 2 || r1.pipAnimationInProgress || r1.isInline)) {
                    if (r1.currentEditMode != 0 || r1.scale != 1.0f || f4 == -1.0f || r1.zoomAnimation) {
                        r1.backgroundDrawable.setAlpha(255);
                    } else {
                        containerViewHeight = ((float) getContainerViewHeight()) / 4.0f;
                        r1.backgroundDrawable.setAlpha((int) Math.max(127.0f, 255.0f * (1.0f - (Math.min(Math.abs(f4), containerViewHeight) / containerViewHeight))));
                    }
                }
                if (r1.currentEditMode == 0) {
                    if (!(r1.scale < 1.0f || r1.zoomAnimation || r1.zooming)) {
                        if (f2 > r1.maxX + ((float) AndroidUtilities.dp(5.0f))) {
                            imageReceiver = r1.leftImage;
                        } else if (f2 < r1.minX - ((float) AndroidUtilities.dp(5.0f))) {
                            imageReceiver = r1.rightImage;
                        } else {
                            r1.groupedPhotosListView.setMoveProgress(0.0f);
                        }
                        r1.changingPage = imageReceiver == null;
                    }
                    imageReceiver = null;
                    if (imageReceiver == null) {
                    }
                    r1.changingPage = imageReceiver == null;
                } else {
                    imageReceiver = null;
                }
                if (imageReceiver == r1.rightImage) {
                    if (r1.zoomAnimation || f2 >= r1.minX) {
                        f5 = 0.0f;
                        f6 = 1.0f;
                        f7 = f2;
                    } else {
                        f6 = Math.min(1.0f, (r1.minX - f2) / ((float) canvas.getWidth()));
                        f5 = (1.0f - f6) * 0.3f;
                        f7 = (float) ((-canvas.getWidth()) - (AndroidUtilities.dp(30.0f) / 2));
                    }
                    if (imageReceiver.hasBitmapImage()) {
                        canvas.save();
                        canvas2.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                        canvas2.translate(((float) (canvas.getWidth() + (AndroidUtilities.dp(30.0f) / 2))) + f7, 0.0f);
                        f8 = 1.0f - f5;
                        canvas2.scale(f8, f8);
                        f8 = (float) imageReceiver.getBitmapWidth();
                        containerViewWidth = ((float) getContainerViewWidth()) / f8;
                        float bitmapHeight = (float) imageReceiver.getBitmapHeight();
                        f4 = ((float) getContainerViewHeight()) / bitmapHeight;
                        if (containerViewWidth <= f4) {
                            f4 = containerViewWidth;
                        }
                        int i3 = (int) (f8 * f4);
                        int i4 = (int) (bitmapHeight * f4);
                        imageReceiver.setAlpha(f6);
                        imageReceiver.setImageCoords((-i3) / 2, (-i4) / 2, i3, i4);
                        imageReceiver.draw(canvas2);
                        canvas.restore();
                    }
                    r1.groupedPhotosListView.setMoveProgress(-f6);
                    canvas.save();
                    canvas2.translate(f7, f3 / f);
                    canvas2.translate(((((float) canvas.getWidth()) * (r1.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f))) / 2.0f, (-f3) / f);
                    r1.photoProgressViews[1].setScale(1.0f - f5);
                    r1.photoProgressViews[1].setAlpha(f6);
                    r1.photoProgressViews[1].onDraw(canvas2);
                    canvas.restore();
                }
                if (r1.zoomAnimation || f2 <= r1.maxX || r1.currentEditMode != 0) {
                    f8 = 1.0f;
                    f6 = f2;
                    containerViewWidth = 0.0f;
                } else {
                    f8 = Math.min(1.0f, (f2 - r1.maxX) / ((float) canvas.getWidth()));
                    containerViewWidth = 0.3f * f8;
                    f8 = 1.0f - f8;
                    f6 = r1.maxX;
                }
                Object obj2 = (r1.aspectRatioFrameLayout == null || r1.aspectRatioFrameLayout.getVisibility() != 0) ? null : 1;
                if (r1.centerImage.hasBitmapImage()) {
                    long currentTimeMillis;
                    long j;
                    canvas.save();
                    canvas2.translate((float) ((getContainerViewWidth() / 2) + getAdditionX()), (float) ((getContainerViewHeight() / 2) + getAdditionY()));
                    canvas2.translate(f6, f3);
                    f5 = f - containerViewWidth;
                    canvas2.scale(f5, f5);
                    if (r1.currentEditMode == 1) {
                        r1.photoCropView.setBitmapParams(f, f6, f3);
                    }
                    int bitmapWidth = r1.centerImage.getBitmapWidth();
                    int bitmapHeight2 = r1.centerImage.getBitmapHeight();
                    if (obj2 != null && r1.textureUploaded && Math.abs((((float) bitmapWidth) / ((float) bitmapHeight2)) - (((float) r1.videoTextureView.getMeasuredWidth()) / ((float) r1.videoTextureView.getMeasuredHeight()))) > 0.01f) {
                        bitmapWidth = r1.videoTextureView.getMeasuredWidth();
                        bitmapHeight2 = r1.videoTextureView.getMeasuredHeight();
                    }
                    float f10 = (float) bitmapWidth;
                    containerViewWidth2 = ((float) getContainerViewWidth()) / f10;
                    f7 = (float) bitmapHeight2;
                    f5 = ((float) getContainerViewHeight()) / f7;
                    if (containerViewWidth2 <= f5) {
                        f5 = containerViewWidth2;
                    }
                    i = (int) (f10 * f5);
                    int i5 = (int) (f7 * f5);
                    if (obj2 != null && r1.textureUploaded && r1.videoCrossfadeStarted) {
                        if (r1.videoCrossfadeAlpha == 1.0f) {
                            f9 = f2;
                            if (obj2 != null) {
                                if (!r1.videoCrossfadeStarted && r1.textureUploaded) {
                                    r1.videoCrossfadeStarted = true;
                                    r1.videoCrossfadeAlpha = 0.0f;
                                    r1.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                                }
                                canvas2.translate((float) ((-i) / 2), (float) ((-i5) / 2));
                                r1.videoTextureView.setAlpha(r1.videoCrossfadeAlpha * f8);
                                r1.aspectRatioFrameLayout.draw(canvas2);
                                if (r1.videoCrossfadeStarted && r1.videoCrossfadeAlpha < 1.0f) {
                                    currentTimeMillis = System.currentTimeMillis();
                                    imageReceiver2 = imageReceiver;
                                    j = currentTimeMillis - r1.videoCrossfadeAlphaLastTime;
                                    r1.videoCrossfadeAlphaLastTime = currentTimeMillis;
                                    r1.videoCrossfadeAlpha += ((float) j) / 200.0f;
                                    r1.containerView.invalidate();
                                    if (r1.videoCrossfadeAlpha > 1.0f) {
                                        r1.videoCrossfadeAlpha = 1.0f;
                                    }
                                    canvas.restore();
                                }
                            }
                            imageReceiver2 = imageReceiver;
                            canvas.restore();
                        }
                    }
                    r1.centerImage.setAlpha(f8);
                    f9 = f2;
                    r1.centerImage.setImageCoords((-i) / 2, (-i5) / 2, i, i5);
                    r1.centerImage.draw(canvas2);
                    if (obj2 != null) {
                        r1.videoCrossfadeStarted = true;
                        r1.videoCrossfadeAlpha = 0.0f;
                        r1.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                        canvas2.translate((float) ((-i) / 2), (float) ((-i5) / 2));
                        r1.videoTextureView.setAlpha(r1.videoCrossfadeAlpha * f8);
                        r1.aspectRatioFrameLayout.draw(canvas2);
                        currentTimeMillis = System.currentTimeMillis();
                        imageReceiver2 = imageReceiver;
                        j = currentTimeMillis - r1.videoCrossfadeAlphaLastTime;
                        r1.videoCrossfadeAlphaLastTime = currentTimeMillis;
                        r1.videoCrossfadeAlpha += ((float) j) / 200.0f;
                        r1.containerView.invalidate();
                        if (r1.videoCrossfadeAlpha > 1.0f) {
                            r1.videoCrossfadeAlpha = 1.0f;
                        }
                        canvas.restore();
                    }
                    imageReceiver2 = imageReceiver;
                    canvas.restore();
                } else {
                    imageReceiver2 = imageReceiver;
                    f9 = f2;
                }
                if (r1.isCurrentVideo) {
                    if (r1.progressView.getVisibility() != 0) {
                        if (r1.videoPlayer != null) {
                        }
                    }
                    obj = null;
                    if (obj != null) {
                        canvas.save();
                        canvas2.translate(f6, f3 / f);
                        r1.photoProgressViews[0].setScale(1.0f - containerViewWidth);
                        r1.photoProgressViews[0].setAlpha(f8);
                        r1.photoProgressViews[0].onDraw(canvas2);
                        canvas.restore();
                    }
                    if (!r1.pipAnimationInProgress && (r1.miniProgressView.getVisibility() == 0 || r1.miniProgressAnimator != null)) {
                        canvas.save();
                        canvas2.translate(((float) r1.miniProgressView.getLeft()) + f6, ((float) r1.miniProgressView.getTop()) + (f3 / f));
                        r1.miniProgressView.draw(canvas2);
                        canvas.restore();
                    }
                    imageReceiver3 = imageReceiver2;
                    if (imageReceiver3 == r1.leftImage) {
                        if (imageReceiver3.hasBitmapImage()) {
                            canvas.save();
                            canvas2.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                            canvas2.translate(((-((((float) canvas.getWidth()) * (r1.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f) + f9, 0.0f);
                            containerViewWidth2 = (float) imageReceiver3.getBitmapWidth();
                            containerViewWidth = ((float) getContainerViewWidth()) / containerViewWidth2;
                            containerViewHeight = (float) imageReceiver3.getBitmapHeight();
                            f6 = ((float) getContainerViewHeight()) / containerViewHeight;
                            if (containerViewWidth > f6) {
                                containerViewWidth = f6;
                            }
                            i = (int) (containerViewWidth2 * containerViewWidth);
                            i2 = (int) (containerViewHeight * containerViewWidth);
                            containerViewWidth = 1.0f;
                            imageReceiver3.setAlpha(1.0f);
                            imageReceiver3.setImageCoords((-i) / 2, (-i2) / 2, i, i2);
                            imageReceiver3.draw(canvas2);
                            canvas.restore();
                        } else {
                            containerViewWidth = 1.0f;
                        }
                        r1.groupedPhotosListView.setMoveProgress(containerViewWidth - f8);
                        canvas.save();
                        canvas2.translate(f9, f3 / f);
                        canvas2.translate((-((((float) canvas.getWidth()) * (r1.scale + containerViewWidth)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f, (-f3) / f);
                        r1.photoProgressViews[2].setScale(1.0f);
                        r1.photoProgressViews[2].setAlpha(1.0f);
                        r1.photoProgressViews[2].onDraw(canvas2);
                        canvas.restore();
                    }
                    if (r1.waitingForDraw != 0) {
                        r1.waitingForDraw--;
                        if (r1.waitingForDraw == 0) {
                            if (r1.textureImageView != null) {
                                try {
                                    r1.currentBitmap = Bitmaps.createBitmap(r1.videoTextureView.getWidth(), r1.videoTextureView.getHeight(), Config.ARGB_8888);
                                    r1.changedTextureView.getBitmap(r1.currentBitmap);
                                } catch (Throwable th) {
                                    Throwable th2 = th;
                                    if (r1.currentBitmap != null) {
                                        r1.currentBitmap.recycle();
                                        r1.currentBitmap = null;
                                    }
                                    FileLog.m3e(th2);
                                }
                                if (r1.currentBitmap != null) {
                                    r1.textureImageView.setVisibility(0);
                                    r1.textureImageView.setImageBitmap(r1.currentBitmap);
                                } else {
                                    pipVideoView = null;
                                    r1.textureImageView.setImageDrawable(null);
                                    r1.pipVideoView.close();
                                    r1.pipVideoView = pipVideoView;
                                }
                            }
                            pipVideoView = null;
                            r1.pipVideoView.close();
                            r1.pipVideoView = pipVideoView;
                        } else {
                            r1.containerView.invalidate();
                        }
                    }
                }
                if (obj2 == null && r1.videoPlayerControlFrameLayout.getVisibility() != 0) {
                }
                obj = null;
                if (obj != null) {
                    canvas.save();
                    canvas2.translate(f6, f3 / f);
                    r1.photoProgressViews[0].setScale(1.0f - containerViewWidth);
                    r1.photoProgressViews[0].setAlpha(f8);
                    r1.photoProgressViews[0].onDraw(canvas2);
                    canvas.restore();
                }
                canvas.save();
                canvas2.translate(((float) r1.miniProgressView.getLeft()) + f6, ((float) r1.miniProgressView.getTop()) + (f3 / f));
                r1.miniProgressView.draw(canvas2);
                canvas.restore();
                imageReceiver3 = imageReceiver2;
                if (imageReceiver3 == r1.leftImage) {
                    if (imageReceiver3.hasBitmapImage()) {
                        containerViewWidth = 1.0f;
                    } else {
                        canvas.save();
                        canvas2.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                        canvas2.translate(((-((((float) canvas.getWidth()) * (r1.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f) + f9, 0.0f);
                        containerViewWidth2 = (float) imageReceiver3.getBitmapWidth();
                        containerViewWidth = ((float) getContainerViewWidth()) / containerViewWidth2;
                        containerViewHeight = (float) imageReceiver3.getBitmapHeight();
                        f6 = ((float) getContainerViewHeight()) / containerViewHeight;
                        if (containerViewWidth > f6) {
                            containerViewWidth = f6;
                        }
                        i = (int) (containerViewWidth2 * containerViewWidth);
                        i2 = (int) (containerViewHeight * containerViewWidth);
                        containerViewWidth = 1.0f;
                        imageReceiver3.setAlpha(1.0f);
                        imageReceiver3.setImageCoords((-i) / 2, (-i2) / 2, i, i2);
                        imageReceiver3.draw(canvas2);
                        canvas.restore();
                    }
                    r1.groupedPhotosListView.setMoveProgress(containerViewWidth - f8);
                    canvas.save();
                    canvas2.translate(f9, f3 / f);
                    canvas2.translate((-((((float) canvas.getWidth()) * (r1.scale + containerViewWidth)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f, (-f3) / f);
                    r1.photoProgressViews[2].setScale(1.0f);
                    r1.photoProgressViews[2].setAlpha(1.0f);
                    r1.photoProgressViews[2].onDraw(canvas2);
                    canvas.restore();
                }
                if (r1.waitingForDraw != 0) {
                    r1.waitingForDraw--;
                    if (r1.waitingForDraw == 0) {
                        r1.containerView.invalidate();
                    } else {
                        if (r1.textureImageView != null) {
                            r1.currentBitmap = Bitmaps.createBitmap(r1.videoTextureView.getWidth(), r1.videoTextureView.getHeight(), Config.ARGB_8888);
                            r1.changedTextureView.getBitmap(r1.currentBitmap);
                            if (r1.currentBitmap != null) {
                                pipVideoView = null;
                                r1.textureImageView.setImageDrawable(null);
                                r1.pipVideoView.close();
                                r1.pipVideoView = pipVideoView;
                            } else {
                                r1.textureImageView.setVisibility(0);
                                r1.textureImageView.setImageBitmap(r1.currentBitmap);
                            }
                        }
                        pipVideoView = null;
                        r1.pipVideoView.close();
                        r1.pipVideoView = pipVideoView;
                    }
                }
                obj = 1;
                if (obj != null) {
                    canvas.save();
                    canvas2.translate(f6, f3 / f);
                    r1.photoProgressViews[0].setScale(1.0f - containerViewWidth);
                    r1.photoProgressViews[0].setAlpha(f8);
                    r1.photoProgressViews[0].onDraw(canvas2);
                    canvas.restore();
                }
                canvas.save();
                canvas2.translate(((float) r1.miniProgressView.getLeft()) + f6, ((float) r1.miniProgressView.getTop()) + (f3 / f));
                r1.miniProgressView.draw(canvas2);
                canvas.restore();
                imageReceiver3 = imageReceiver2;
                if (imageReceiver3 == r1.leftImage) {
                    if (imageReceiver3.hasBitmapImage()) {
                        canvas.save();
                        canvas2.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                        canvas2.translate(((-((((float) canvas.getWidth()) * (r1.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f) + f9, 0.0f);
                        containerViewWidth2 = (float) imageReceiver3.getBitmapWidth();
                        containerViewWidth = ((float) getContainerViewWidth()) / containerViewWidth2;
                        containerViewHeight = (float) imageReceiver3.getBitmapHeight();
                        f6 = ((float) getContainerViewHeight()) / containerViewHeight;
                        if (containerViewWidth > f6) {
                            containerViewWidth = f6;
                        }
                        i = (int) (containerViewWidth2 * containerViewWidth);
                        i2 = (int) (containerViewHeight * containerViewWidth);
                        containerViewWidth = 1.0f;
                        imageReceiver3.setAlpha(1.0f);
                        imageReceiver3.setImageCoords((-i) / 2, (-i2) / 2, i, i2);
                        imageReceiver3.draw(canvas2);
                        canvas.restore();
                    } else {
                        containerViewWidth = 1.0f;
                    }
                    r1.groupedPhotosListView.setMoveProgress(containerViewWidth - f8);
                    canvas.save();
                    canvas2.translate(f9, f3 / f);
                    canvas2.translate((-((((float) canvas.getWidth()) * (r1.scale + containerViewWidth)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f, (-f3) / f);
                    r1.photoProgressViews[2].setScale(1.0f);
                    r1.photoProgressViews[2].setAlpha(1.0f);
                    r1.photoProgressViews[2].onDraw(canvas2);
                    canvas.restore();
                }
                if (r1.waitingForDraw != 0) {
                    r1.waitingForDraw--;
                    if (r1.waitingForDraw == 0) {
                        if (r1.textureImageView != null) {
                            r1.currentBitmap = Bitmaps.createBitmap(r1.videoTextureView.getWidth(), r1.videoTextureView.getHeight(), Config.ARGB_8888);
                            r1.changedTextureView.getBitmap(r1.currentBitmap);
                            if (r1.currentBitmap != null) {
                                r1.textureImageView.setVisibility(0);
                                r1.textureImageView.setImageBitmap(r1.currentBitmap);
                            } else {
                                pipVideoView = null;
                                r1.textureImageView.setImageDrawable(null);
                                r1.pipVideoView.close();
                                r1.pipVideoView = pipVideoView;
                            }
                        }
                        pipVideoView = null;
                        r1.pipVideoView.close();
                        r1.pipVideoView = pipVideoView;
                    } else {
                        r1.containerView.invalidate();
                    }
                }
            }
        }
    }

    private void onActionClick(boolean r10) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r9 = this;
        r0 = r9.currentMessageObject;
        if (r0 != 0) goto L_0x0008;
    L_0x0004:
        r0 = r9.currentBotInlineResult;
        if (r0 == 0) goto L_0x000f;
    L_0x0008:
        r0 = r9.currentFileNames;
        r1 = 0;
        r0 = r0[r1];
        if (r0 != 0) goto L_0x0010;
    L_0x000f:
        return;
    L_0x0010:
        r9.isStreaming = r1;
        r0 = r9.currentMessageObject;
        r2 = 1;
        r3 = 0;
        if (r0 == 0) goto L_0x0107;
    L_0x0018:
        r0 = r9.currentMessageObject;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x003d;
    L_0x0020:
        r0 = r9.currentMessageObject;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        r0 = r0.length();
        if (r0 == 0) goto L_0x003d;
    L_0x002c:
        r0 = new java.io.File;
        r4 = r9.currentMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.attachPath;
        r0.<init>(r4);
        r4 = r0.exists();
        if (r4 != 0) goto L_0x003e;
    L_0x003d:
        r0 = r3;
    L_0x003e:
        if (r0 != 0) goto L_0x0103;
    L_0x0040:
        r0 = r9.currentMessageObject;
        r0 = r0.messageOwner;
        r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0);
        r4 = r0.exists();
        if (r4 != 0) goto L_0x0103;
    L_0x004e:
        r0 = org.telegram.messenger.SharedConfig.streamMedia;
        if (r0 == 0) goto L_0x0162;
    L_0x0052:
        r0 = r9.currentMessageObject;
        r4 = r0.getDialogId();
        r0 = (int) r4;
        if (r0 == 0) goto L_0x0162;
    L_0x005b:
        r0 = r9.currentMessageObject;
        r0 = r0.isVideo();
        if (r0 == 0) goto L_0x0162;
    L_0x0063:
        r0 = r9.currentMessageObject;
        r0 = r0.canStreamVideo();
        if (r0 == 0) goto L_0x0162;
    L_0x006b:
        r0 = r9.currentAccount;	 Catch:{ Exception -> 0x0162 }
        r0 = org.telegram.messenger.FileLoader.getInstance(r0);	 Catch:{ Exception -> 0x0162 }
        r4 = r9.currentMessageObject;	 Catch:{ Exception -> 0x0162 }
        r4 = r4.getDocument();	 Catch:{ Exception -> 0x0162 }
        r0.loadFile(r4, r2, r1);	 Catch:{ Exception -> 0x0162 }
        r0 = r9.currentMessageObject;	 Catch:{ Exception -> 0x0162 }
        r0 = r0.getDocument();	 Catch:{ Exception -> 0x0162 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0162 }
        r4.<init>();	 Catch:{ Exception -> 0x0162 }
        r5 = "?account=";	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = r9.currentMessageObject;	 Catch:{ Exception -> 0x0162 }
        r5 = r5.currentAccount;	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = "&id=";	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = r0.id;	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = "&hash=";	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = r0.access_hash;	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = "&dc=";	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = r0.dc_id;	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = "&size=";	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = r0.size;	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = "&mime=";	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = r0.mime_type;	 Catch:{ Exception -> 0x0162 }
        r6 = "UTF-8";	 Catch:{ Exception -> 0x0162 }
        r5 = java.net.URLEncoder.encode(r5, r6);	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = "&name=";	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r0);	 Catch:{ Exception -> 0x0162 }
        r5 = "UTF-8";	 Catch:{ Exception -> 0x0162 }
        r0 = java.net.URLEncoder.encode(r0, r5);	 Catch:{ Exception -> 0x0162 }
        r4.append(r0);	 Catch:{ Exception -> 0x0162 }
        r0 = r4.toString();	 Catch:{ Exception -> 0x0162 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0162 }
        r4.<init>();	 Catch:{ Exception -> 0x0162 }
        r5 = "tg://";	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r5 = r9.currentMessageObject;	 Catch:{ Exception -> 0x0162 }
        r5 = r5.getFileName();	 Catch:{ Exception -> 0x0162 }
        r4.append(r5);	 Catch:{ Exception -> 0x0162 }
        r4.append(r0);	 Catch:{ Exception -> 0x0162 }
        r0 = r4.toString();	 Catch:{ Exception -> 0x0162 }
        r0 = android.net.Uri.parse(r0);	 Catch:{ Exception -> 0x0162 }
        r9.isStreaming = r2;	 Catch:{ Exception -> 0x0163 }
        r9.checkProgress(r1, r1);	 Catch:{ Exception -> 0x0163 }
        goto L_0x0163;
    L_0x0103:
        r8 = r3;
        r3 = r0;
        r0 = r8;
        goto L_0x0163;
    L_0x0107:
        r0 = r9.currentBotInlineResult;
        if (r0 == 0) goto L_0x0162;
    L_0x010b:
        r0 = r9.currentBotInlineResult;
        r0 = r0.document;
        if (r0 == 0) goto L_0x0120;
    L_0x0111:
        r0 = r9.currentBotInlineResult;
        r0 = r0.document;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0);
        r4 = r0.exists();
        if (r4 != 0) goto L_0x0103;
    L_0x011f:
        goto L_0x0162;
    L_0x0120:
        r0 = r9.currentBotInlineResult;
        r0 = r0.content;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r0 == 0) goto L_0x0162;
    L_0x0128:
        r0 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = r9.currentBotInlineResult;
        r6 = r6.content;
        r6 = r6.url;
        r6 = org.telegram.messenger.Utilities.MD5(r6);
        r5.append(r6);
        r6 = ".";
        r5.append(r6);
        r6 = r9.currentBotInlineResult;
        r6 = r6.content;
        r6 = r6.url;
        r7 = "mp4";
        r6 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r7);
        r5.append(r6);
        r5 = r5.toString();
        r0.<init>(r4, r5);
        r4 = r0.exists();
        if (r4 != 0) goto L_0x0103;
    L_0x0162:
        r0 = r3;
    L_0x0163:
        if (r3 == 0) goto L_0x016b;
    L_0x0165:
        if (r0 != 0) goto L_0x016b;
    L_0x0167:
        r0 = android.net.Uri.fromFile(r3);
    L_0x016b:
        if (r0 != 0) goto L_0x0213;
    L_0x016d:
        if (r10 == 0) goto L_0x0216;
    L_0x016f:
        r10 = r9.currentMessageObject;
        if (r10 == 0) goto L_0x01a5;
    L_0x0173:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.FileLoader.getInstance(r10);
        r0 = r9.currentFileNames;
        r0 = r0[r1];
        r10 = r10.isLoadingFile(r0);
        if (r10 != 0) goto L_0x0194;
    L_0x0183:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.FileLoader.getInstance(r10);
        r0 = r9.currentMessageObject;
        r0 = r0.getDocument();
        r10.loadFile(r0, r2, r1);
        goto L_0x0216;
    L_0x0194:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.FileLoader.getInstance(r10);
        r0 = r9.currentMessageObject;
        r0 = r0.getDocument();
        r10.cancelLoadFile(r0);
        goto L_0x0216;
    L_0x01a5:
        r10 = r9.currentBotInlineResult;
        if (r10 == 0) goto L_0x0216;
    L_0x01a9:
        r10 = r9.currentBotInlineResult;
        r10 = r10.document;
        if (r10 == 0) goto L_0x01db;
    L_0x01af:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.FileLoader.getInstance(r10);
        r0 = r9.currentFileNames;
        r0 = r0[r1];
        r10 = r10.isLoadingFile(r0);
        if (r10 != 0) goto L_0x01cd;
    L_0x01bf:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.FileLoader.getInstance(r10);
        r0 = r9.currentBotInlineResult;
        r0 = r0.document;
        r10.loadFile(r0, r2, r1);
        goto L_0x0216;
    L_0x01cd:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.FileLoader.getInstance(r10);
        r0 = r9.currentBotInlineResult;
        r0 = r0.document;
        r10.cancelLoadFile(r0);
        goto L_0x0216;
    L_0x01db:
        r10 = r9.currentBotInlineResult;
        r10 = r10.content;
        r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r10 == 0) goto L_0x0216;
    L_0x01e3:
        r10 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r9.currentBotInlineResult;
        r0 = r0.content;
        r0 = r0.url;
        r10 = r10.isLoadingHttpFile(r0);
        if (r10 != 0) goto L_0x0205;
    L_0x01f3:
        r10 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r9.currentBotInlineResult;
        r0 = r0.content;
        r0 = r0.url;
        r1 = "mp4";
        r2 = r9.currentAccount;
        r10.loadHttpFile(r0, r1, r2);
        goto L_0x0216;
    L_0x0205:
        r10 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r9.currentBotInlineResult;
        r0 = r0.content;
        r0 = r0.url;
        r10.cancelLoadHttpFile(r0);
        goto L_0x0216;
    L_0x0213:
        r9.preparePlayer(r0, r2, r1);
    L_0x0216:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onActionClick(boolean):void");
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.scale != NUM) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(f), Math.round(f2), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.containerView.postInvalidate();
        }
        return null;
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (this.discardTap) {
            return false;
        }
        int access$14100;
        if (this.containerView.getTag() != null) {
            boolean z = this.aspectRatioFrameLayout != null && this.aspectRatioFrameLayout.getVisibility() == 0;
            float x = motionEvent.getX();
            motionEvent = motionEvent.getY();
            if (!(this.photoProgressViews[0] == null || this.containerView == null || z)) {
                access$14100 = this.photoProgressViews[0].backgroundState;
                if (access$14100 > 0 && access$14100 <= 3 && x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && motionEvent >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && motionEvent <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                    onActionClick(true);
                    checkProgress(0, true);
                    return true;
                }
            }
            toggleActionBar(this.isActionBarVisible ^ 1, true);
        } else if (this.sendPhotoType == 0) {
            if (this.isCurrentVideo != null) {
                this.videoPlayButton.callOnClick();
            } else {
                this.checkImageView.performClick();
            }
        } else if (this.currentBotInlineResult != null && (this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
            access$14100 = this.photoProgressViews[0].backgroundState;
            if (access$14100 > 0 && access$14100 <= 3) {
                float x2 = motionEvent.getX();
                motionEvent = motionEvent.getY();
                if (x2 >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x2 <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && motionEvent >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && motionEvent <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                    onActionClick(true);
                    checkProgress(0, true);
                    return true;
                }
            }
        } else if (this.sendPhotoType == 2 && this.isCurrentVideo != null) {
            this.videoPlayButton.callOnClick();
        }
        return true;
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        long j = 0;
        if (this.videoPlayer != null && this.videoPlayerControlFrameLayout.getVisibility() == 0) {
            long currentPosition = this.videoPlayer.getCurrentPosition();
            long duration = this.videoPlayer.getDuration();
            if (duration >= 0 && currentPosition >= 0 && duration != C0542C.TIME_UNSET && currentPosition != C0542C.TIME_UNSET) {
                int containerViewWidth = getContainerViewWidth();
                float x = motionEvent.getX();
                containerViewWidth /= 3;
                long j2 = x >= ((float) (containerViewWidth * 2)) ? currentPosition + 10000 : x < ((float) containerViewWidth) ? currentPosition - 10000 : currentPosition;
                if (currentPosition != j2) {
                    if (j2 > duration) {
                        j = duration;
                    } else if (j2 >= 0) {
                        j = j2;
                    }
                    this.videoPlayer.seekTo(j);
                    this.containerView.invalidate();
                    this.videoPlayerSeekbar.setProgress(((float) j) / ((float) duration));
                    this.videoPlayerControlFrameLayout.invalidate();
                    return true;
                }
            }
        }
        if (this.canZoom) {
            if (this.scale == 1.0f) {
                if (this.translationY == 0.0f) {
                    if (this.translationX != 0.0f) {
                    }
                }
            }
            if (this.animationStartTime == 0) {
                if (this.animationInProgress == 0) {
                    if (this.scale == 1.0f) {
                        float x2 = (motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - (((motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
                        float y = (motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - (((motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
                        updateMinMax(3.0f);
                        if (x2 < this.minX) {
                            x2 = this.minX;
                        } else if (x2 > this.maxX) {
                            x2 = this.maxX;
                        }
                        if (y < this.minY) {
                            y = this.minY;
                        } else if (y > this.maxY) {
                            y = this.maxY;
                        }
                        animateTo(3.0f, x2, y, true);
                    } else {
                        animateTo(1.0f, 0.0f, 0.0f, true);
                    }
                    this.doubleTap = true;
                    return true;
                }
            }
            return false;
        }
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
                this.muteItem.setImageResource(C0446R.drawable.volume_off);
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
            this.muteItem.setImageResource(C0446R.drawable.volume_on);
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

    private void didChangedCompressionLevel(boolean z) {
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("compress_video2", this.selectedCompression);
        edit.commit();
        updateWidthHeightBitrateForCompression();
        updateVideoInfo();
        if (z) {
            requestVideoPreview(true);
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
            String format;
            int ceil;
            String format2;
            ActionBar actionBar;
            if (this.selectedCompression == 0) {
                this.compressItem.setImageResource(C0446R.drawable.video_240);
            } else if (this.selectedCompression == 1) {
                this.compressItem.setImageResource(C0446R.drawable.video_360);
            } else if (this.selectedCompression == 2) {
                this.compressItem.setImageResource(C0446R.drawable.video_480);
            } else if (this.selectedCompression == 3) {
                this.compressItem.setImageResource(C0446R.drawable.video_720);
            } else if (this.selectedCompression == 4) {
                this.compressItem.setImageResource(C0446R.drawable.video_1080);
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
                                    format = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                                    ceil = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                                    format2 = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(ceil), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                                    this.currentSubtitle = String.format("%s, %s", new Object[]{format, format2});
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
                            format = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                            ceil = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                            format2 = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(ceil), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                            this.currentSubtitle = String.format("%s, %s", new Object[]{format, format2});
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
                            format = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                            ceil = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                            format2 = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(ceil), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                            this.currentSubtitle = String.format("%s, %s", new Object[]{format, format2});
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
                    format = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                    ceil = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                    format2 = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(ceil), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                    this.currentSubtitle = String.format("%s, %s", new Object[]{format, format2});
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
                            format = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                            ceil = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                            format2 = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(ceil), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                            this.currentSubtitle = String.format("%s, %s", new Object[]{format, format2});
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
                    format = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                    ceil = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                    format2 = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(ceil), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                    this.currentSubtitle = String.format("%s, %s", new Object[]{format, format2});
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
                    format = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
                    ceil = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
                    format2 = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(ceil), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
                    this.currentSubtitle = String.format("%s, %s", new Object[]{format, format2});
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
            format = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
            ceil = ((int) Math.ceil((double) (this.estimatedDuration / 1000))) - (((int) ((this.estimatedDuration / 1000) / 60)) * 60);
            format2 = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.estimatedDuration / 1000) / 60)), Integer.valueOf(ceil), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
            this.currentSubtitle = String.format("%s, %s", new Object[]{format, format2});
            actionBar = this.actionBar;
            if (!this.muteVideo) {
                charSequence = this.currentSubtitle;
            }
            actionBar.setSubtitle(charSequence);
        }
    }

    private void requestVideoPreview(int i) {
        if (this.videoPreviewMessageObject != null) {
            MediaController.getInstance().cancelVideoConvert(this.videoPreviewMessageObject);
        }
        boolean z = this.requestingPreview && !this.tryStartRequestPreviewOnFinish;
        this.requestingPreview = false;
        this.loadInitialVideo = false;
        this.progressView.setVisibility(4);
        if (i != 1) {
            this.tryStartRequestPreviewOnFinish = false;
            if (i == 2) {
                preparePlayer(this.currentPlayingVideoFile, false, false);
            }
        } else if (this.selectedCompression == this.compressionsCount - 1) {
            this.tryStartRequestPreviewOnFinish = false;
            if (z) {
                this.progressView.setVisibility(0);
                this.loadInitialVideo = true;
            } else {
                preparePlayer(this.currentPlayingVideoFile, false, false);
            }
        } else {
            this.requestingPreview = true;
            releasePlayer();
            if (this.videoPreviewMessageObject == 0) {
                i = new TL_message();
                i.id = 0;
                i.message = TtmlNode.ANONYMOUS_REGION_ID;
                i.media = new TL_messageMediaEmpty();
                i.action = new TL_messageActionEmpty();
                this.videoPreviewMessageObject = new MessageObject(UserConfig.selectedAccount, i, false);
                this.videoPreviewMessageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), "video_preview.mp4").getAbsolutePath();
                this.videoPreviewMessageObject.videoEditedInfo = new VideoEditedInfo();
                this.videoPreviewMessageObject.videoEditedInfo.rotationValue = this.rotationValue;
                this.videoPreviewMessageObject.videoEditedInfo.originalWidth = this.originalWidth;
                this.videoPreviewMessageObject.videoEditedInfo.originalHeight = this.originalHeight;
                this.videoPreviewMessageObject.videoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
            }
            i = this.videoPreviewMessageObject.videoEditedInfo;
            long j = this.startTime;
            i.startTime = j;
            i = this.videoPreviewMessageObject.videoEditedInfo;
            long j2 = this.endTime;
            i.endTime = j2;
            if (j == -1) {
                j = 0;
            }
            if (j2 == -1) {
                j2 = (long) (this.videoDuration * NUM);
            }
            if (j2 - j > 5000000) {
                this.videoPreviewMessageObject.videoEditedInfo.endTime = j + 5000000;
            }
            this.videoPreviewMessageObject.videoEditedInfo.bitrate = this.bitrate;
            this.videoPreviewMessageObject.videoEditedInfo.resultWidth = this.resultWidth;
            this.videoPreviewMessageObject.videoEditedInfo.resultHeight = this.resultHeight;
            if (MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true) == 0) {
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
                float f;
                int i;
                switch (this.selectedCompression) {
                    case 0:
                        f = 426.0f;
                        i = 400000;
                        break;
                    case 1:
                        f = 640.0f;
                        i = 900000;
                        break;
                    case 2:
                        f = 854.0f;
                        i = 1100000;
                        break;
                    default:
                        i = 2500000;
                        f = 1280.0f;
                        break;
                }
                f /= (float) (this.originalWidth > this.originalHeight ? this.originalWidth : this.originalHeight);
                this.resultWidth = Math.round((((float) this.originalWidth) * f) / 2.0f) * 2;
                this.resultHeight = Math.round((((float) this.originalHeight) * f) / 2.0f) * 2;
                if (this.bitrate != 0) {
                    this.bitrate = Math.min(i, (int) (((float) this.originalBitrate) / f));
                    this.videoFramesSize = (long) ((((float) (this.bitrate / 8)) * this.videoDuration) / 1000.0f);
                }
            }
        }
    }

    private void showQualityView(final boolean z) {
        if (z) {
            this.previousCompression = this.selectedCompression;
        }
        if (this.qualityChooseViewAnimation != null) {
            this.qualityChooseViewAnimation.cancel();
        }
        this.qualityChooseViewAnimation = new AnimatorSet();
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (z) {
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
            class C16191 extends AnimatorListenerAdapter {
                C16191() {
                }

                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(PhotoViewer.this.qualityChooseViewAnimation) != null) {
                        PhotoViewer.this.qualityChooseViewAnimation = null;
                    }
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PhotoViewer.this.qualityChooseViewAnimation) != null) {
                    PhotoViewer.this.qualityChooseViewAnimation = new AnimatorSet();
                    Animator[] animatorArr;
                    if (z != null) {
                        PhotoViewer.this.qualityChooseView.setVisibility(0);
                        PhotoViewer.this.qualityPicker.setVisibility(0);
                        animator = PhotoViewer.this.qualityChooseViewAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.qualityChooseView, "translationY", new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(PhotoViewer.this.qualityPicker, "translationY", new float[]{0.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, "translationY", new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                        animator.playTogether(animatorArr);
                    } else {
                        PhotoViewer.this.qualityChooseView.setVisibility(4);
                        PhotoViewer.this.qualityPicker.setVisibility(4);
                        animator = PhotoViewer.this.qualityChooseViewAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, "translationY", new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, "translationY", new float[]{0.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, "translationY", new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                        animator.playTogether(animatorArr);
                    }
                    PhotoViewer.this.qualityChooseViewAnimation.addListener(new C16191());
                    PhotoViewer.this.qualityChooseViewAnimation.setDuration(200);
                    PhotoViewer.this.qualityChooseViewAnimation.setInterpolator(new AccelerateInterpolator());
                    PhotoViewer.this.qualityChooseViewAnimation.start();
                }
            }

            public void onAnimationCancel(Animator animator) {
                PhotoViewer.this.qualityChooseViewAnimation = null;
            }
        });
        this.qualityChooseViewAnimation.setDuration(200);
        this.qualityChooseViewAnimation.setInterpolator(new DecelerateInterpolator());
        this.qualityChooseViewAnimation.start();
    }

    private void processOpenVideo(final String str, boolean z) {
        if (this.currentLoadingVideoRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.currentLoadingVideoRunnable);
            this.currentLoadingVideoRunnable = null;
        }
        this.videoPreviewMessageObject = null;
        setCompressItemEnabled(false, true);
        this.muteVideo = z;
        this.videoTimelineView.setVideoPath(str);
        this.compressionsCount = true;
        this.rotationValue = 0;
        this.originalSize = new File(str).length();
        z = Utilities.globalQueue;
        Runnable anonymousClass82 = new Runnable() {
            public void run() {
                Throwable th;
                Throwable e;
                TrackHeaderBox trackHeaderBox;
                boolean z;
                TrackHeaderBox trackHeaderBox2;
                if (PhotoViewer.this.currentLoadingVideoRunnable == r1) {
                    try {
                        boolean z2;
                        Container isoFile = new IsoFile(str);
                        List paths = Path.getPaths(isoFile, "/moov/trak/");
                        if (Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") == null && BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("video hasn't mp4a atom");
                        }
                        if (Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("video hasn't avc1 atom");
                            }
                            z2 = false;
                        } else {
                            z2 = true;
                        }
                        long j = 0;
                        PhotoViewer.this.audioFramesSize = 0;
                        PhotoViewer.this.videoFramesSize = 0;
                        trackHeaderBox2 = null;
                        int i = 0;
                        while (i < paths.size()) {
                            if (PhotoViewer.this.currentLoadingVideoRunnable == r1) {
                                long j2;
                                TrackBox trackBox = (TrackBox) ((Box) paths.get(i));
                                try {
                                    MediaBox mediaBox = trackBox.getMediaBox();
                                    MediaHeaderBox mediaHeaderBox = mediaBox.getMediaHeaderBox();
                                    long[] sampleSizes = mediaBox.getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes();
                                    j2 = j;
                                    int i2 = 0;
                                    while (i2 < sampleSizes.length) {
                                        try {
                                            if (PhotoViewer.this.currentLoadingVideoRunnable == r1) {
                                                i2++;
                                                j2 += sampleSizes[i2];
                                            } else {
                                                return;
                                            }
                                        } catch (Throwable e2) {
                                            th = e2;
                                        }
                                    }
                                    PhotoViewer.this.videoDuration = ((float) mediaHeaderBox.getDuration()) / ((float) mediaHeaderBox.getTimescale());
                                    j = (long) ((int) (((float) (8 * j2)) / PhotoViewer.this.videoDuration));
                                } catch (Throwable e22) {
                                    th = e22;
                                    j2 = 0;
                                    try {
                                        FileLog.m3e(th);
                                        j = 0;
                                        if (PhotoViewer.this.currentLoadingVideoRunnable != r1) {
                                            trackHeaderBox = trackBox.getTrackHeaderBox();
                                            if (trackHeaderBox.getWidth() != 0.0d) {
                                            }
                                            PhotoViewer.this.audioFramesSize = PhotoViewer.this.audioFramesSize + j2;
                                            i++;
                                            j = 0;
                                        } else {
                                            return;
                                        }
                                    } catch (Exception e3) {
                                        e22 = e3;
                                    }
                                }
                                if (PhotoViewer.this.currentLoadingVideoRunnable != r1) {
                                    trackHeaderBox = trackBox.getTrackHeaderBox();
                                    if (trackHeaderBox.getWidth() != 0.0d || trackHeaderBox.getHeight() == 0.0d) {
                                        PhotoViewer.this.audioFramesSize = PhotoViewer.this.audioFramesSize + j2;
                                    } else if (trackHeaderBox2 == null || trackHeaderBox2.getWidth() < trackHeaderBox.getWidth() || trackHeaderBox2.getHeight() < trackHeaderBox.getHeight()) {
                                        try {
                                            PhotoViewer.this.originalBitrate = PhotoViewer.this.bitrate = (int) ((j / 100000) * 100000);
                                            if (PhotoViewer.this.bitrate > 900000) {
                                                PhotoViewer.this.bitrate = 900000;
                                            }
                                            PhotoViewer.this.videoFramesSize = PhotoViewer.this.videoFramesSize + j2;
                                            trackHeaderBox2 = trackHeaderBox;
                                        } catch (Exception e4) {
                                            e22 = e4;
                                            trackHeaderBox2 = trackHeaderBox;
                                        }
                                    }
                                    i++;
                                    j = 0;
                                } else {
                                    return;
                                }
                            }
                            return;
                        }
                        z = z2;
                    } catch (Throwable e222) {
                        th = e222;
                        trackHeaderBox2 = null;
                        FileLog.m3e(th);
                        z = false;
                        if (trackHeaderBox2 == null) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("video hasn't trackHeaderBox atom");
                            }
                            z = false;
                        }
                        if (PhotoViewer.this.currentLoadingVideoRunnable != r1) {
                            PhotoViewer.this.currentLoadingVideoRunnable = null;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (PhotoViewer.this.parentActivity != null) {
                                        PhotoViewer.this.videoHasAudio = z;
                                        if (z) {
                                            StringBuilder stringBuilder;
                                            MediaCodecInfo selectCodec;
                                            String name;
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
                                                                                selectCodec = MediaController.selectCodec("video/avc");
                                                                                if (selectCodec == null) {
                                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                                        FileLog.m0d("no codec info for video/avc");
                                                                                    }
                                                                                    PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                                } else {
                                                                                    name = selectCodec.getName();
                                                                                    if (!(name.equals("OMX.google.h264.encoder") || name.equals("OMX.ST.VFM.H264Enc") || name.equals("OMX.Exynos.avc.enc") || name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") || name.equals("OMX.MARVELL.VIDEO.H264ENCODER") || name.equals("OMX.k3.video.encoder.avc"))) {
                                                                                        if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                                                            if (MediaController.selectColorFormat(selectCodec, "video/avc") == 0) {
                                                                                                if (BuildVars.LOGS_ENABLED) {
                                                                                                    FileLog.m0d("no color format for video/avc");
                                                                                                }
                                                                                                PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                                        stringBuilder = new StringBuilder();
                                                                                        stringBuilder.append("unsupported encoder = ");
                                                                                        stringBuilder.append(name);
                                                                                        FileLog.m0d(stringBuilder.toString());
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
                                                                    selectCodec = MediaController.selectCodec("video/avc");
                                                                    if (selectCodec == null) {
                                                                        name = selectCodec.getName();
                                                                        if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                                            if (BuildVars.LOGS_ENABLED) {
                                                                                stringBuilder = new StringBuilder();
                                                                                stringBuilder.append("unsupported encoder = ");
                                                                                stringBuilder.append(name);
                                                                                FileLog.m0d(stringBuilder.toString());
                                                                            }
                                                                            PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                        } else if (MediaController.selectColorFormat(selectCodec, "video/avc") == 0) {
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
                                                            selectCodec = MediaController.selectCodec("video/avc");
                                                            if (selectCodec == null) {
                                                                if (BuildVars.LOGS_ENABLED) {
                                                                    FileLog.m0d("no codec info for video/avc");
                                                                }
                                                                PhotoViewer.this.setCompressItemEnabled(false, true);
                                                            } else {
                                                                name = selectCodec.getName();
                                                                if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                        stringBuilder = new StringBuilder();
                                                                        stringBuilder.append("unsupported encoder = ");
                                                                        stringBuilder.append(name);
                                                                        FileLog.m0d(stringBuilder.toString());
                                                                    }
                                                                    PhotoViewer.this.setCompressItemEnabled(false, true);
                                                                } else if (MediaController.selectColorFormat(selectCodec, "video/avc") == 0) {
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
                                                    selectCodec = MediaController.selectCodec("video/avc");
                                                    if (selectCodec == null) {
                                                        name = selectCodec.getName();
                                                        if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                            if (BuildVars.LOGS_ENABLED) {
                                                                stringBuilder = new StringBuilder();
                                                                stringBuilder.append("unsupported encoder = ");
                                                                stringBuilder.append(name);
                                                                FileLog.m0d(stringBuilder.toString());
                                                            }
                                                            PhotoViewer.this.setCompressItemEnabled(false, true);
                                                        } else if (MediaController.selectColorFormat(selectCodec, "video/avc") == 0) {
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
                                                selectCodec = MediaController.selectCodec("video/avc");
                                                if (selectCodec == null) {
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        FileLog.m0d("no codec info for video/avc");
                                                    }
                                                    PhotoViewer.this.setCompressItemEnabled(false, true);
                                                } else {
                                                    name = selectCodec.getName();
                                                    if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append("unsupported encoder = ");
                                                            stringBuilder.append(name);
                                                            FileLog.m0d(stringBuilder.toString());
                                                        }
                                                        PhotoViewer.this.setCompressItemEnabled(false, true);
                                                    } else if (MediaController.selectColorFormat(selectCodec, "video/avc") == 0) {
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
                    if (trackHeaderBox2 == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("video hasn't trackHeaderBox atom");
                        }
                        z = false;
                    }
                    if (PhotoViewer.this.currentLoadingVideoRunnable != r1) {
                        PhotoViewer.this.currentLoadingVideoRunnable = null;
                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    }
                }
                return;
                th = e222;
                FileLog.m3e(th);
                z = false;
                if (trackHeaderBox2 == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("video hasn't trackHeaderBox atom");
                    }
                    z = false;
                }
                if (PhotoViewer.this.currentLoadingVideoRunnable != r1) {
                    PhotoViewer.this.currentLoadingVideoRunnable = null;
                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                }
            }
        };
        this.currentLoadingVideoRunnable = anonymousClass82;
        z.postRunnable(anonymousClass82);
    }

    private void setCompressItemEnabled(boolean z, boolean z2) {
        if (this.compressItem != null) {
            if ((!z || this.compressItem.getTag() == null) && (z || this.compressItem.getTag() != null)) {
                this.compressItem.setTag(z ? Integer.valueOf(1) : null);
                this.compressItem.setEnabled(z);
                this.compressItem.setClickable(z);
                if (this.compressItemAnimation != null) {
                    this.compressItemAnimation.cancel();
                    this.compressItemAnimation = null;
                }
                float f = 0.5f;
                if (z2) {
                    this.compressItemAnimation = new AnimatorSet();
                    z2 = this.compressItemAnimation;
                    Animator[] animatorArr = new Animator[1];
                    ImageView imageView = this.compressItem;
                    String str = "alpha";
                    float[] fArr = new float[1];
                    if (z) {
                        f = 1.0f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView, str, fArr);
                    z2.playTogether(animatorArr);
                    this.compressItemAnimation.setDuration(180);
                    this.compressItemAnimation.setInterpolator(decelerateInterpolator);
                    this.compressItemAnimation.start();
                } else {
                    z2 = this.compressItem;
                    if (z) {
                        f = 1.0f;
                    }
                    z2.setAlpha(f);
                }
            }
        }
    }
}
