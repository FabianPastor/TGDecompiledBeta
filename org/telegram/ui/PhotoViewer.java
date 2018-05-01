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
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
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
import android.view.Display;
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
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URLEncoder;
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
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.LinearSmoothScrollerEnd;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputPhoto;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Page;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.Peer;
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
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
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
import org.telegram.ui.Components.Paint.Views.ColorPicker;
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

public class PhotoViewer
  implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, NotificationCenter.NotificationCenterDelegate
{
  @SuppressLint({"StaticFieldLeak"})
  private static volatile PhotoViewer Instance = null;
  private static volatile PhotoViewer PipInstance = null;
  private static DecelerateInterpolator decelerateInterpolator;
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
  private float[][] animationValues = new float[2][8];
  private boolean applying;
  private AspectRatioFrameLayout aspectRatioFrameLayout;
  private boolean attachedToWindow;
  private long audioFramesSize;
  private ArrayList<TLRPC.Photo> avatarsArr = new ArrayList();
  private int avatarsDialogId;
  private BackgroundDrawable backgroundDrawable = new BackgroundDrawable(-16777216);
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
  private TLRPC.BotInlineResult currentBotInlineResult;
  private AnimatorSet currentCaptionAnimation;
  private long currentDialogId;
  private int currentEditMode;
  private TLRPC.FileLocation currentFileLocation;
  private String[] currentFileNames = new String[3];
  private int currentIndex;
  private AnimatorSet currentListViewAnimation;
  private Runnable currentLoadingVideoRunnable;
  private MessageObject currentMessageObject;
  private String currentPathObject;
  private PlaceProviderObject currentPlaceObject;
  private Uri currentPlayingVideoFile;
  private String currentSubtitle;
  private ImageReceiver.BitmapHolder currentThumb;
  private TLRPC.FileLocation currentUserAvatarLocation = null;
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
  private boolean[] endReached = { 0, 1 };
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
  private ArrayList<TLRPC.FileLocation> imagesArrLocations = new ArrayList();
  private ArrayList<Integer> imagesArrLocationsSizes = new ArrayList();
  private ArrayList<MessageObject> imagesArrTemp = new ArrayList();
  private SparseArray<MessageObject>[] imagesByIds = { new SparseArray(), new SparseArray() };
  private SparseArray<MessageObject>[] imagesByIdsTemp = { new SparseArray(), new SparseArray() };
  private boolean inPreview;
  private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5F);
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
  private Runnable miniProgressShowRunnable = new Runnable()
  {
    public void run()
    {
      PhotoViewer.this.toggleMiniProgressInternal(true);
    }
  };
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
  private float pinchStartScale = 1.0F;
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
  private float scale = 1.0F;
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
  private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener()
  {
    public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2) {}
    
    public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
    {
      boolean bool = true;
      if (PhotoViewer.this.videoTextureView == null) {}
      for (;;)
      {
        return bool;
        if (PhotoViewer.this.changingTextureView)
        {
          if (PhotoViewer.this.switchingInlineMode) {
            PhotoViewer.access$2702(PhotoViewer.this, 2);
          }
          PhotoViewer.this.videoTextureView.setSurfaceTexture(paramAnonymousSurfaceTexture);
          PhotoViewer.this.videoTextureView.setVisibility(0);
          PhotoViewer.access$2002(PhotoViewer.this, false);
          PhotoViewer.this.containerView.invalidate();
          bool = false;
        }
      }
    }
    
    public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2) {}
    
    public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture)
    {
      if (PhotoViewer.this.waitingForFirstTextureUpload == 1)
      {
        PhotoViewer.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
          public boolean onPreDraw()
          {
            PhotoViewer.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
            if (PhotoViewer.this.textureImageView != null)
            {
              PhotoViewer.this.textureImageView.setVisibility(4);
              PhotoViewer.this.textureImageView.setImageDrawable(null);
              if (PhotoViewer.this.currentBitmap != null)
              {
                PhotoViewer.this.currentBitmap.recycle();
                PhotoViewer.access$1902(PhotoViewer.this, null);
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (PhotoViewer.this.isInline) {
                  PhotoViewer.this.dismissInternal();
                }
              }
            });
            PhotoViewer.access$2702(PhotoViewer.this, 0);
            return true;
          }
        });
        PhotoViewer.this.changedTextureView.invalidate();
      }
    }
  };
  private TextView switchCaptionTextView;
  private int switchImageAfterAnimation;
  private Runnable switchToInlineRunnable = new Runnable()
  {
    public void run()
    {
      PhotoViewer.access$1802(PhotoViewer.this, false);
      if (PhotoViewer.this.currentBitmap != null)
      {
        PhotoViewer.this.currentBitmap.recycle();
        PhotoViewer.access$1902(PhotoViewer.this, null);
      }
      PhotoViewer.access$2002(PhotoViewer.this, true);
      if (PhotoViewer.this.textureImageView != null) {}
      try
      {
        PhotoViewer.access$1902(PhotoViewer.this, Bitmaps.createBitmap(PhotoViewer.this.videoTextureView.getWidth(), PhotoViewer.this.videoTextureView.getHeight(), Bitmap.Config.ARGB_8888));
        PhotoViewer.this.videoTextureView.getBitmap(PhotoViewer.this.currentBitmap);
        if (PhotoViewer.this.currentBitmap != null)
        {
          PhotoViewer.this.textureImageView.setVisibility(0);
          PhotoViewer.this.textureImageView.setImageBitmap(PhotoViewer.this.currentBitmap);
          PhotoViewer.access$2302(PhotoViewer.this, true);
          PhotoViewer.access$1402(PhotoViewer.this, new PipVideoView());
          PhotoViewer.access$2402(PhotoViewer.this, PhotoViewer.this.pipVideoView.show(PhotoViewer.this.parentActivity, PhotoViewer.this, PhotoViewer.this.aspectRatioFrameLayout.getAspectRatio(), PhotoViewer.this.aspectRatioFrameLayout.getVideoRotation()));
          PhotoViewer.this.changedTextureView.setVisibility(4);
          PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoTextureView);
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          if (PhotoViewer.this.currentBitmap != null)
          {
            PhotoViewer.this.currentBitmap.recycle();
            PhotoViewer.access$1902(PhotoViewer.this, null);
          }
          FileLog.e(localThrowable);
          continue;
          PhotoViewer.this.textureImageView.setImageDrawable(null);
        }
      }
    }
  };
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
  private Runnable updateProgressRunnable = new Runnable()
  {
    public void run()
    {
      float f1;
      if (PhotoViewer.this.videoPlayer != null)
      {
        if (!PhotoViewer.this.isCurrentVideo) {
          break label276;
        }
        if (!PhotoViewer.this.videoTimelineView.isDragging())
        {
          f1 = (float)PhotoViewer.this.videoPlayer.getCurrentPosition() / (float)PhotoViewer.this.videoPlayer.getDuration();
          if ((PhotoViewer.this.inPreview) || (PhotoViewer.this.videoTimelineView.getVisibility() != 0)) {
            break label262;
          }
          if (f1 < PhotoViewer.this.videoTimelineView.getRightProgress()) {
            break label191;
          }
          PhotoViewer.this.videoPlayer.pause();
          PhotoViewer.this.videoTimelineView.setProgress(0.0F);
          PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoTimelineView.getLeftProgress() * (float)PhotoViewer.this.videoPlayer.getDuration()));
          PhotoViewer.this.containerView.invalidate();
          PhotoViewer.this.updateVideoPlayerTime();
        }
      }
      label191:
      label262:
      label276:
      while (PhotoViewer.this.videoPlayerSeekbar.isDragging()) {
        for (;;)
        {
          if (PhotoViewer.this.isPlaying) {
            AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 17L);
          }
          return;
          f2 = f1 - PhotoViewer.this.videoTimelineView.getLeftProgress();
          f1 = f2;
          if (f2 < 0.0F) {
            f1 = 0.0F;
          }
          f2 = f1 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
          f1 = f2;
          if (f2 > 1.0F) {
            f1 = 1.0F;
          }
          PhotoViewer.this.videoTimelineView.setProgress(f1);
          continue;
          PhotoViewer.this.videoTimelineView.setProgress(f1);
        }
      }
      float f2 = (float)PhotoViewer.this.videoPlayer.getCurrentPosition() / (float)PhotoViewer.this.videoPlayer.getDuration();
      if (PhotoViewer.this.currentVideoFinishedLoading)
      {
        f1 = 1.0F;
        label325:
        if ((PhotoViewer.this.inPreview) || (PhotoViewer.this.videoTimelineView.getVisibility() != 0)) {
          break label628;
        }
        if (f2 < PhotoViewer.this.videoTimelineView.getRightProgress()) {
          break label557;
        }
        PhotoViewer.this.videoPlayer.pause();
        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0F);
        PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoTimelineView.getLeftProgress() * (float)PhotoViewer.this.videoPlayer.getDuration()));
        PhotoViewer.this.containerView.invalidate();
      }
      for (;;)
      {
        PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
        PhotoViewer.this.updateVideoPlayerTime();
        break;
        long l = SystemClock.uptimeMillis();
        if (Math.abs(l - PhotoViewer.this.lastBufferedPositionCheck) >= 500L)
        {
          FileLoader localFileLoader;
          if (PhotoViewer.this.isStreaming)
          {
            localFileLoader = FileLoader.getInstance(PhotoViewer.this.currentAccount);
            if (PhotoViewer.this.seekToProgressPending != 0.0F) {
              f1 = PhotoViewer.this.seekToProgressPending;
            }
          }
          label513:
          for (f1 = localFileLoader.getBufferedProgressFromPosition(f1, PhotoViewer.this.currentFileNames[0]);; f1 = 1.0F)
          {
            PhotoViewer.access$902(PhotoViewer.this, l);
            break;
            f1 = f2;
            break label513;
          }
        }
        f1 = -1.0F;
        break label325;
        label557:
        f2 -= PhotoViewer.this.videoTimelineView.getLeftProgress();
        f1 = f2;
        if (f2 < 0.0F) {
          f1 = 0.0F;
        }
        f2 = f1 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
        f1 = f2;
        if (f2 > 1.0F) {
          f1 = 1.0F;
        }
        PhotoViewer.this.videoPlayerSeekbar.setProgress(f1);
        continue;
        label628:
        if (PhotoViewer.this.seekToProgressPending == 0.0F) {
          PhotoViewer.this.videoPlayerSeekbar.setProgress(f2);
        }
        if (f1 != -1.0F)
        {
          PhotoViewer.this.videoPlayerSeekbar.setBufferedProgress(f1);
          if (PhotoViewer.this.pipVideoView != null) {
            PhotoViewer.this.pipVideoView.setBufferedProgress(f1);
          }
        }
      }
    }
  };
  private VelocityTracker velocityTracker;
  private ImageView videoBackwardButton;
  private float videoCrossfadeAlpha;
  private long videoCrossfadeAlphaLastTime;
  private boolean videoCrossfadeStarted;
  private float videoDuration;
  private ImageView videoForwardButton;
  private int videoFramerate;
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
  private WindowManager.LayoutParams windowLayoutParams;
  private FrameLayout windowView;
  private boolean zoomAnimation;
  private boolean zooming;
  
  public PhotoViewer()
  {
    this.blackPaint.setColor(-16777216);
  }
  
  private void animateTo(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean)
  {
    animateTo(paramFloat1, paramFloat2, paramFloat3, paramBoolean, 250);
  }
  
  private void animateTo(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean, int paramInt)
  {
    if ((this.scale == paramFloat1) && (this.translationX == paramFloat2) && (this.translationY == paramFloat3)) {}
    for (;;)
    {
      return;
      this.zoomAnimation = paramBoolean;
      this.animateToScale = paramFloat1;
      this.animateToX = paramFloat2;
      this.animateToY = paramFloat3;
      this.animationStartTime = System.currentTimeMillis();
      this.imageMoveAnimation = new AnimatorSet();
      this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }) });
      this.imageMoveAnimation.setInterpolator(this.interpolator);
      this.imageMoveAnimation.setDuration(paramInt);
      this.imageMoveAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          PhotoViewer.access$14502(PhotoViewer.this, null);
          PhotoViewer.this.containerView.invalidate();
        }
      });
      this.imageMoveAnimation.start();
    }
  }
  
  private void applyCurrentEditMode()
  {
    Bitmap localBitmap = null;
    Object localObject1 = null;
    TLRPC.PhotoSize localPhotoSize = null;
    int i = 0;
    Object localObject2;
    Object localObject3;
    label191:
    label203:
    float f1;
    float f2;
    if (this.currentEditMode == 1)
    {
      localBitmap = this.photoCropView.getBitmap();
      i = 1;
      localObject2 = localPhotoSize;
      if (localBitmap != null)
      {
        localPhotoSize = ImageLoader.scaleAndSaveImage(localBitmap, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
        if (localPhotoSize != null)
        {
          localObject3 = this.imagesArrLocals.get(this.currentIndex);
          if (!(localObject3 instanceof MediaController.PhotoEntry)) {
            break label576;
          }
          localObject3 = (MediaController.PhotoEntry)localObject3;
          ((MediaController.PhotoEntry)localObject3).imagePath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
          localPhotoSize = ImageLoader.scaleAndSaveImage(localBitmap, AndroidUtilities.dp(120.0F), AndroidUtilities.dp(120.0F), 70, false, 101, 101);
          if (localPhotoSize != null) {
            ((MediaController.PhotoEntry)localObject3).thumbPath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
          }
          if (localObject1 != null) {
            ((MediaController.PhotoEntry)localObject3).stickers.addAll((Collection)localObject1);
          }
          if (this.currentEditMode != 1) {
            break label488;
          }
          this.cropItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
          ((MediaController.PhotoEntry)localObject3).isCropped = true;
          if (localObject2 == null) {
            break label562;
          }
          ((MediaController.PhotoEntry)localObject3).savedFilterState = ((MediaController.SavedFilterState)localObject2);
          if ((this.sendPhotoType == 0) && (this.placeProvider != null))
          {
            this.placeProvider.updatePhotoAtIndex(this.currentIndex);
            if (!this.placeProvider.isPhotoChecked(this.currentIndex)) {
              setPhotoChecked();
            }
          }
          if (this.currentEditMode == 1)
          {
            f1 = this.photoCropView.getRectSizeX() / getContainerViewWidth();
            f2 = this.photoCropView.getRectSizeY() / getContainerViewHeight();
            if (f1 <= f2) {
              break label798;
            }
          }
        }
      }
    }
    for (;;)
    {
      this.scale = f1;
      this.translationX = (this.photoCropView.getRectX() + this.photoCropView.getRectSizeX() / 2.0F - getContainerViewWidth() / 2);
      this.translationY = (this.photoCropView.getRectY() + this.photoCropView.getRectSizeY() / 2.0F - getContainerViewHeight() / 2);
      this.zoomAnimation = true;
      this.applying = true;
      this.photoCropView.onDisappear();
      this.centerImage.setParentView(null);
      this.centerImage.setOrientation(0, true);
      this.ignoreDidSetImage = true;
      this.centerImage.setImageBitmap(localBitmap);
      this.ignoreDidSetImage = false;
      this.centerImage.setParentView(this.containerView);
      return;
      if (this.currentEditMode == 2)
      {
        localBitmap = this.photoFilterView.getBitmap();
        localObject2 = this.photoFilterView.getSavedFilterState();
        break;
      }
      localObject2 = localPhotoSize;
      if (this.currentEditMode != 3) {
        break;
      }
      localBitmap = this.photoPaintView.getBitmap();
      localObject1 = this.photoPaintView.getMasks();
      i = 1;
      localObject2 = localPhotoSize;
      break;
      label488:
      if (this.currentEditMode == 2)
      {
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
        ((MediaController.PhotoEntry)localObject3).isFiltered = true;
        break label191;
      }
      if (this.currentEditMode != 3) {
        break label191;
      }
      this.paintItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
      ((MediaController.PhotoEntry)localObject3).isPainted = true;
      break label191;
      label562:
      if (i == 0) {
        break label203;
      }
      ((MediaController.PhotoEntry)localObject3).savedFilterState = null;
      break label203;
      label576:
      if (!(localObject3 instanceof MediaController.SearchImage)) {
        break label203;
      }
      localObject3 = (MediaController.SearchImage)localObject3;
      ((MediaController.SearchImage)localObject3).imagePath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
      localPhotoSize = ImageLoader.scaleAndSaveImage(localBitmap, AndroidUtilities.dp(120.0F), AndroidUtilities.dp(120.0F), 70, false, 101, 101);
      if (localPhotoSize != null) {
        ((MediaController.SearchImage)localObject3).thumbPath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
      }
      if (localObject1 != null) {
        ((MediaController.SearchImage)localObject3).stickers.addAll((Collection)localObject1);
      }
      if (this.currentEditMode == 1)
      {
        this.cropItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
        ((MediaController.SearchImage)localObject3).isCropped = true;
      }
      for (;;)
      {
        if (localObject2 == null) {
          break label784;
        }
        ((MediaController.SearchImage)localObject3).savedFilterState = ((MediaController.SavedFilterState)localObject2);
        break;
        if (this.currentEditMode == 2)
        {
          this.tuneItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
          ((MediaController.SearchImage)localObject3).isFiltered = true;
        }
        else if (this.currentEditMode == 3)
        {
          this.paintItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
          ((MediaController.SearchImage)localObject3).isPainted = true;
        }
      }
      label784:
      if (i == 0) {
        break label203;
      }
      ((MediaController.SearchImage)localObject3).savedFilterState = null;
      break label203;
      label798:
      f1 = f2;
    }
  }
  
  private boolean checkAnimation()
  {
    boolean bool = false;
    if ((this.animationInProgress != 0) && (Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500L))
    {
      if (this.animationEndRunnable != null)
      {
        this.animationEndRunnable.run();
        this.animationEndRunnable = null;
      }
      this.animationInProgress = 0;
    }
    if (this.animationInProgress != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void checkBufferedProgress(float paramFloat)
  {
    if ((!this.isStreaming) || (this.parentActivity == null) || (this.streamingAlertShown) || (this.videoPlayer == null) || (this.currentMessageObject == null)) {}
    for (;;)
    {
      return;
      TLRPC.Document localDocument = this.currentMessageObject.getDocument();
      if ((localDocument != null) && (paramFloat < 0.9F) && ((localDocument.size * paramFloat >= 5242880.0F) || (paramFloat >= 0.5F)) && (Math.abs(SystemClock.elapsedRealtime() - this.startedPlayTime) >= 2000L))
      {
        if (this.videoPlayer.getDuration() == -9223372036854775807L) {
          Toast.makeText(this.parentActivity, LocaleController.getString("VideoDoesNotSupportStreaming", NUM), 1).show();
        }
        this.streamingAlertShown = true;
      }
    }
  }
  
  private boolean checkInlinePermissions()
  {
    boolean bool = false;
    if (this.parentActivity == null) {}
    for (;;)
    {
      return bool;
      if ((Build.VERSION.SDK_INT < 23) || (Settings.canDrawOverlays(this.parentActivity))) {
        bool = true;
      } else {
        new AlertDialog.Builder(this.parentActivity).setTitle(LocaleController.getString("AppName", NUM)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener()
        {
          @TargetApi(23)
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            if (PhotoViewer.this.parentActivity != null) {}
            try
            {
              Activity localActivity = PhotoViewer.this.parentActivity;
              Intent localIntent = new android/content/Intent;
              paramAnonymousDialogInterface = new java/lang/StringBuilder;
              paramAnonymousDialogInterface.<init>();
              localIntent.<init>("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + PhotoViewer.this.parentActivity.getPackageName()));
              localActivity.startActivity(localIntent);
              return;
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              for (;;)
              {
                FileLog.e(paramAnonymousDialogInterface);
              }
            }
          }
        }).show();
      }
    }
  }
  
  private void checkMinMax(boolean paramBoolean)
  {
    float f1 = this.translationX;
    float f2 = this.translationY;
    updateMinMax(this.scale);
    if (this.translationX < this.minX)
    {
      f1 = this.minX;
      if (this.translationY >= this.minY) {
        break label84;
      }
      f2 = this.minY;
    }
    for (;;)
    {
      animateTo(this.scale, f1, f2, paramBoolean);
      return;
      if (this.translationX <= this.maxX) {
        break;
      }
      f1 = this.maxX;
      break;
      label84:
      if (this.translationY > this.maxY) {
        f2 = this.maxY;
      }
    }
  }
  
  private void checkProgress(int paramInt, boolean paramBoolean)
  {
    int i = this.currentIndex;
    int j;
    Object localObject1;
    Object localObject2;
    MessageObject localMessageObject;
    Object localObject3;
    boolean bool1;
    boolean bool2;
    if (paramInt == 1)
    {
      j = i + 1;
      if (this.currentFileNames[paramInt] == null) {
        break label1037;
      }
      localObject1 = null;
      localObject2 = null;
      localMessageObject = null;
      localObject3 = null;
      bool1 = false;
      bool2 = false;
      i = 0;
      if (this.currentMessageObject == null) {
        break label404;
      }
      if ((j >= 0) && (j < this.imagesArr.size())) {
        break label96;
      }
      this.photoProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
    }
    for (;;)
    {
      return;
      j = i;
      if (paramInt != 2) {
        break;
      }
      j = i - 1;
      break;
      label96:
      localMessageObject = (MessageObject)this.imagesArr.get(j);
      if (!TextUtils.isEmpty(localMessageObject.messageOwner.attachPath))
      {
        localObject2 = new File(localMessageObject.messageOwner.attachPath);
        localObject3 = localObject2;
        if (!((File)localObject2).exists()) {
          localObject3 = null;
        }
      }
      localObject2 = localObject3;
      if (localObject3 == null)
      {
        if (((localMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (localMessageObject.messageOwner.media.webpage != null) && (localMessageObject.messageOwner.media.webpage.document == null)) {
          localObject2 = FileLoader.getPathToAttach(getFileLocation(j, null), true);
        }
      }
      else
      {
        label223:
        if ((!SharedConfig.streamMedia) || (!localMessageObject.isVideo()) || ((int)localMessageObject.getDialogId() == 0)) {
          break label398;
        }
        j = 1;
        label249:
        bool1 = localMessageObject.isVideo();
        localObject3 = localObject2;
        label260:
        bool2 = ((File)localObject3).exists();
        if ((localObject3 == null) || ((!bool2) && (j == 0))) {
          break label922;
        }
        if (!bool1) {
          break label884;
        }
        this.photoProgressViews[paramInt].setBackgroundState(3, paramBoolean);
        label298:
        if (paramInt == 0)
        {
          if (bool2) {
            break label910;
          }
          if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[paramInt])) {
            break label898;
          }
          this.menuItem.hideSubItem(7);
        }
        label335:
        if (paramInt != 0) {
          break label1002;
        }
        if ((this.imagesArrLocals.isEmpty()) && ((this.currentFileNames[0] == null) || (bool1) || (this.photoProgressViews[0].backgroundState == 0))) {
          break label1032;
        }
      }
      label398:
      label404:
      label499:
      label884:
      label898:
      label910:
      label922:
      label1002:
      label1032:
      for (paramBoolean = true;; paramBoolean = false)
      {
        this.canZoom = paramBoolean;
        break;
        localObject2 = FileLoader.getPathToMessage(localMessageObject.messageOwner);
        break label223;
        j = 0;
        break label249;
        if (this.currentBotInlineResult != null)
        {
          if ((j < 0) || (j >= this.imagesArrLocals.size()))
          {
            this.photoProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
            break;
          }
          localObject3 = (TLRPC.BotInlineResult)this.imagesArrLocals.get(j);
          if ((((TLRPC.BotInlineResult)localObject3).type.equals("video")) || (MessageObject.isVideoDocument(((TLRPC.BotInlineResult)localObject3).document))) {
            if (((TLRPC.BotInlineResult)localObject3).document != null)
            {
              localObject2 = FileLoader.getPathToAttach(((TLRPC.BotInlineResult)localObject3).document);
              bool2 = true;
            }
          }
          for (;;)
          {
            if (localObject2 != null)
            {
              j = i;
              localObject3 = localObject2;
              bool1 = bool2;
              if (((File)localObject2).exists()) {
                break;
              }
            }
            localObject3 = new File(FileLoader.getDirectory(4), this.currentFileNames[paramInt]);
            j = i;
            bool1 = bool2;
            break;
            if (!(((TLRPC.BotInlineResult)localObject3).content instanceof TLRPC.TL_webDocument)) {
              break label499;
            }
            localObject2 = new File(FileLoader.getDirectory(4), Utilities.MD5(((TLRPC.BotInlineResult)localObject3).content.url) + "." + ImageLoader.getHttpUrlExtension(((TLRPC.BotInlineResult)localObject3).content.url, "mp4"));
            break label499;
            if (((TLRPC.BotInlineResult)localObject3).document != null)
            {
              localObject2 = new File(FileLoader.getDirectory(3), this.currentFileNames[paramInt]);
              bool2 = bool1;
            }
            else
            {
              localObject2 = localMessageObject;
              bool2 = bool1;
              if (((TLRPC.BotInlineResult)localObject3).photo != null)
              {
                localObject2 = new File(FileLoader.getDirectory(0), this.currentFileNames[paramInt]);
                bool2 = bool1;
              }
            }
          }
        }
        if (this.currentFileLocation != null)
        {
          if ((j < 0) || (j >= this.imagesArrLocations.size()))
          {
            this.photoProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
            break;
          }
          localObject3 = (TLRPC.FileLocation)this.imagesArrLocations.get(j);
          if ((this.avatarsDialogId != 0) || (this.isEvent)) {}
          for (bool1 = true;; bool1 = false)
          {
            localObject3 = FileLoader.getPathToAttach((TLObject)localObject3, bool1);
            j = i;
            bool1 = bool2;
            break;
          }
        }
        j = i;
        localObject3 = localObject1;
        bool1 = bool2;
        if (this.currentPathObject == null) {
          break label260;
        }
        localObject2 = new File(FileLoader.getDirectory(3), this.currentFileNames[paramInt]);
        j = i;
        localObject3 = localObject2;
        bool1 = bool2;
        if (((File)localObject2).exists()) {
          break label260;
        }
        localObject3 = new File(FileLoader.getDirectory(4), this.currentFileNames[paramInt]);
        j = i;
        bool1 = bool2;
        break label260;
        this.photoProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
        break label298;
        this.menuItem.showSubItem(7);
        break label335;
        this.menuItem.hideSubItem(7);
        break label335;
        if (bool1) {
          if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[paramInt])) {
            this.photoProgressViews[paramInt].setBackgroundState(2, false);
          }
        }
        for (;;)
        {
          localObject2 = ImageLoader.getInstance().getFileProgress(this.currentFileNames[paramInt]);
          localObject3 = localObject2;
          if (localObject2 == null) {
            localObject3 = Float.valueOf(0.0F);
          }
          this.photoProgressViews[paramInt].setProgress(((Float)localObject3).floatValue(), false);
          break label335;
          break;
          this.photoProgressViews[paramInt].setBackgroundState(1, false);
          continue;
          this.photoProgressViews[paramInt].setBackgroundState(0, paramBoolean);
        }
      }
      label1037:
      bool2 = false;
      bool1 = bool2;
      if (!this.imagesArrLocals.isEmpty())
      {
        bool1 = bool2;
        if (j >= 0)
        {
          bool1 = bool2;
          if (j < this.imagesArrLocals.size())
          {
            localObject3 = this.imagesArrLocals.get(j);
            bool1 = bool2;
            if ((localObject3 instanceof MediaController.PhotoEntry)) {
              bool1 = ((MediaController.PhotoEntry)localObject3).isVideo;
            }
          }
        }
      }
      if (bool1) {
        this.photoProgressViews[paramInt].setBackgroundState(3, paramBoolean);
      } else {
        this.photoProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
      }
    }
  }
  
  private ByteArrayInputStream cleanBuffer(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[paramArrayOfByte.length];
    int i = 0;
    int j = 0;
    while (i < paramArrayOfByte.length) {
      if ((paramArrayOfByte[i] == 0) && (paramArrayOfByte[(i + 1)] == 0) && (paramArrayOfByte[(i + 2)] == 3))
      {
        arrayOfByte[j] = ((byte)0);
        arrayOfByte[(j + 1)] = ((byte)0);
        i += 3;
        j += 2;
      }
      else
      {
        arrayOfByte[j] = ((byte)paramArrayOfByte[i]);
        i++;
        j++;
      }
    }
    return new ByteArrayInputStream(arrayOfByte, 0, j);
  }
  
  private void closeCaptionEnter(boolean paramBoolean)
  {
    if ((this.currentIndex < 0) || (this.currentIndex >= this.imagesArrLocals.size())) {
      return;
    }
    Object localObject1 = this.imagesArrLocals.get(this.currentIndex);
    Object localObject2 = this.captionEditText.getFieldCharSequence();
    CharSequence[] arrayOfCharSequence = new CharSequence[1];
    arrayOfCharSequence[0] = localObject2;
    Object localObject3;
    if (paramBoolean)
    {
      localObject2 = DataQuery.getInstance(this.currentAccount).getEntities(arrayOfCharSequence);
      if (!(localObject1 instanceof MediaController.PhotoEntry)) {
        break label234;
      }
      localObject3 = (MediaController.PhotoEntry)localObject1;
      ((MediaController.PhotoEntry)localObject3).caption = arrayOfCharSequence[0];
      ((MediaController.PhotoEntry)localObject3).entities = ((ArrayList)localObject2);
      label98:
      if ((this.captionEditText.getFieldCharSequence().length() != 0) && (!this.placeProvider.isPhotoChecked(this.currentIndex))) {
        setPhotoChecked();
      }
    }
    this.captionEditText.setTag(null);
    if (this.lastTitle != null)
    {
      this.actionBar.setTitle(this.lastTitle);
      this.lastTitle = null;
    }
    if (this.isCurrentVideo)
    {
      localObject3 = this.actionBar;
      if (!this.muteVideo) {
        break label265;
      }
    }
    label234:
    label265:
    for (localObject2 = null;; localObject2 = this.currentSubtitle)
    {
      ((ActionBar)localObject3).setSubtitle((CharSequence)localObject2);
      updateCaptionTextForCurrentPhoto(localObject1);
      setCurrentCaption(null, arrayOfCharSequence[0], false);
      if (this.captionEditText.isPopupShowing()) {
        this.captionEditText.hidePopup();
      }
      this.captionEditText.closeKeyboard();
      break;
      if (!(localObject1 instanceof MediaController.SearchImage)) {
        break label98;
      }
      localObject3 = (MediaController.SearchImage)localObject1;
      ((MediaController.SearchImage)localObject3).caption = arrayOfCharSequence[0];
      ((MediaController.SearchImage)localObject3).entities = ((ArrayList)localObject2);
      break label98;
    }
  }
  
  private TextView createCaptionTextView()
  {
    TextView local41 = new TextView(this.actvityContext)
    {
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramAnonymousMotionEvent))) {}
        for (boolean bool = true;; bool = false) {
          return bool;
        }
      }
    };
    local41.setMovementMethod(new LinkMovementMethodMy(null));
    local41.setPadding(AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F));
    local41.setLinkTextColor(-1);
    local41.setTextColor(-1);
    local41.setHighlightColor(872415231);
    local41.setEllipsize(TextUtils.TruncateAt.END);
    if (LocaleController.isRTL) {}
    for (int i = 5;; i = 3)
    {
      local41.setGravity(i | 0x10);
      local41.setTextSize(1, 16.0F);
      local41.setVisibility(4);
      local41.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (!PhotoViewer.this.needCaptionLayout) {}
          for (;;)
          {
            return;
            PhotoViewer.this.openCaptionEnter();
          }
        }
      });
      return local41;
    }
  }
  
  private void createVideoControlsInterface()
  {
    this.videoPlayerSeekbar = new SeekBar(this.containerView.getContext());
    this.videoPlayerSeekbar.setLineHeight(AndroidUtilities.dp(4.0F));
    this.videoPlayerSeekbar.setColors(NUM, NUM, -2764585, -1, -1);
    this.videoPlayerSeekbar.setDelegate(new SeekBar.SeekBarDelegate()
    {
      public void onSeekBarDrag(float paramAnonymousFloat)
      {
        float f;
        long l;
        if (PhotoViewer.this.videoPlayer != null)
        {
          f = paramAnonymousFloat;
          if (!PhotoViewer.this.inPreview)
          {
            f = paramAnonymousFloat;
            if (PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
              f = PhotoViewer.this.videoTimelineView.getLeftProgress() + (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress()) * paramAnonymousFloat;
            }
          }
          l = PhotoViewer.this.videoPlayer.getDuration();
          if (l != -9223372036854775807L) {
            break label101;
          }
          PhotoViewer.access$1102(PhotoViewer.this, f);
        }
        for (;;)
        {
          return;
          label101:
          PhotoViewer.this.videoPlayer.seekTo((int)((float)l * f));
        }
      }
    });
    this.videoPlayerControlFrameLayout = new FrameLayout(this.containerView.getContext())
    {
      protected void onDraw(Canvas paramAnonymousCanvas)
      {
        paramAnonymousCanvas.save();
        paramAnonymousCanvas.translate(AndroidUtilities.dp(48.0F), 0.0F);
        PhotoViewer.this.videoPlayerSeekbar.draw(paramAnonymousCanvas);
        paramAnonymousCanvas.restore();
      }
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        float f1 = 0.0F;
        if (PhotoViewer.this.videoPlayer != null)
        {
          float f2 = (float)PhotoViewer.this.videoPlayer.getCurrentPosition() / (float)PhotoViewer.this.videoPlayer.getDuration();
          f1 = f2;
          if (!PhotoViewer.this.inPreview)
          {
            f1 = f2;
            if (PhotoViewer.this.videoTimelineView.getVisibility() == 0)
            {
              f2 -= PhotoViewer.this.videoTimelineView.getLeftProgress();
              f1 = f2;
              if (f2 < 0.0F) {
                f1 = 0.0F;
              }
              f2 = f1 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
              f1 = f2;
              if (f2 > 1.0F) {
                f1 = 1.0F;
              }
            }
          }
        }
        PhotoViewer.this.videoPlayerSeekbar.setProgress(f1);
        PhotoViewer.this.videoTimelineView.setProgress(f1);
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        super.onMeasure(paramAnonymousInt1, paramAnonymousInt2);
        if (PhotoViewer.this.videoPlayer != null)
        {
          long l1 = PhotoViewer.this.videoPlayer.getDuration();
          l2 = l1;
          if (l1 != -9223372036854775807L) {}
        }
        for (long l2 = 0L;; l2 = 0L)
        {
          l2 /= 1000L;
          paramAnonymousInt1 = (int)Math.ceil(PhotoViewer.this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l2 / 60L), Long.valueOf(l2 % 60L), Long.valueOf(l2 / 60L), Long.valueOf(l2 % 60L) })));
          PhotoViewer.this.videoPlayerSeekbar.setSize(getMeasuredWidth() - AndroidUtilities.dp(64.0F) - paramAnonymousInt1, getMeasuredHeight());
          return;
        }
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        int i = (int)paramAnonymousMotionEvent.getX();
        i = (int)paramAnonymousMotionEvent.getY();
        if (PhotoViewer.this.videoPlayerSeekbar.onTouch(paramAnonymousMotionEvent.getAction(), paramAnonymousMotionEvent.getX() - AndroidUtilities.dp(48.0F), paramAnonymousMotionEvent.getY()))
        {
          getParent().requestDisallowInterceptTouchEvent(true);
          invalidate();
        }
        return true;
      }
    };
    this.videoPlayerControlFrameLayout.setWillNotDraw(false);
    this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
    this.videoPlayButton = new ImageView(this.containerView.getContext());
    this.videoPlayButton.setScaleType(ImageView.ScaleType.CENTER);
    this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48.0F, 51, 4.0F, 0.0F, 0.0F, 0.0F));
    this.videoPlayButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (PhotoViewer.this.videoPlayer == null) {}
        for (;;)
        {
          return;
          if (!PhotoViewer.this.isPlaying) {
            break;
          }
          PhotoViewer.this.videoPlayer.pause();
          PhotoViewer.this.containerView.invalidate();
        }
        if (PhotoViewer.this.isCurrentVideo) {
          if ((Math.abs(PhotoViewer.this.videoTimelineView.getProgress() - 1.0F) < 0.01F) || (PhotoViewer.this.videoPlayer.getCurrentPosition() == PhotoViewer.this.videoPlayer.getDuration())) {
            PhotoViewer.this.videoPlayer.seekTo(0L);
          }
        }
        for (;;)
        {
          PhotoViewer.this.videoPlayer.play();
          break;
          if ((Math.abs(PhotoViewer.this.videoPlayerSeekbar.getProgress() - 1.0F) < 0.01F) || (PhotoViewer.this.videoPlayer.getCurrentPosition() == PhotoViewer.this.videoPlayer.getDuration())) {
            PhotoViewer.this.videoPlayer.seekTo(0L);
          }
        }
      }
    });
    this.videoPlayerTime = new SimpleTextView(this.containerView.getContext());
    this.videoPlayerTime.setTextColor(-1);
    this.videoPlayerTime.setGravity(53);
    this.videoPlayerTime.setTextSize(13);
    this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0F, 53, 0.0F, 17.0F, 7.0F, 0.0F));
  }
  
  private void didChangedCompressionLevel(boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = MessagesController.getGlobalMainSettings().edit();
    localEditor.putInt("compress_video2", this.selectedCompression);
    localEditor.commit();
    updateWidthHeightBitrateForCompression();
    updateVideoInfo();
    if (paramBoolean) {
      requestVideoPreview(1);
    }
  }
  
  private void dismissInternal()
  {
    try
    {
      if (this.windowView.getParent() != null)
      {
        ((LaunchActivity)this.parentActivity).drawerLayoutContainer.setAllowDrawContent(true);
        ((WindowManager)this.parentActivity.getSystemService("window")).removeView(this.windowView);
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  private int getAdditionX()
  {
    if ((this.currentEditMode != 0) && (this.currentEditMode != 3)) {}
    for (int i = AndroidUtilities.dp(14.0F);; i = 0) {
      return i;
    }
  }
  
  private int getAdditionY()
  {
    int i = 0;
    int j = 0;
    int k = 0;
    if (this.currentEditMode == 3)
    {
      j = AndroidUtilities.dp(8.0F);
      if (Build.VERSION.SDK_INT >= 21) {
        k = AndroidUtilities.statusBarHeight;
      }
      k += j;
    }
    for (;;)
    {
      return k;
      k = i;
      if (this.currentEditMode != 0)
      {
        i = AndroidUtilities.dp(14.0F);
        k = j;
        if (Build.VERSION.SDK_INT >= 21) {
          k = AndroidUtilities.statusBarHeight;
        }
        k += i;
      }
    }
  }
  
  private int getContainerViewHeight()
  {
    return getContainerViewHeight(this.currentEditMode);
  }
  
  private int getContainerViewHeight(int paramInt)
  {
    int i = AndroidUtilities.displaySize.y;
    int j = i;
    if (paramInt == 0)
    {
      j = i;
      if (Build.VERSION.SDK_INT >= 21) {
        j = i + AndroidUtilities.statusBarHeight;
      }
    }
    if (paramInt == 1) {
      i = j - AndroidUtilities.dp(144.0F);
    }
    for (;;)
    {
      return i;
      if (paramInt == 2)
      {
        i = j - AndroidUtilities.dp(214.0F);
      }
      else
      {
        i = j;
        if (paramInt == 3) {
          i = j - (AndroidUtilities.dp(48.0F) + ActionBar.getCurrentActionBarHeight());
        }
      }
    }
  }
  
  private int getContainerViewWidth()
  {
    return getContainerViewWidth(this.currentEditMode);
  }
  
  private int getContainerViewWidth(int paramInt)
  {
    int i = this.containerView.getWidth();
    int j = i;
    if (paramInt != 0)
    {
      j = i;
      if (paramInt != 3) {
        j = i - AndroidUtilities.dp(28.0F);
      }
    }
    return j;
  }
  
  private VideoEditedInfo getCurrentVideoEditedInfo()
  {
    int i = -1;
    if ((!this.isCurrentVideo) || (this.currentPlayingVideoFile == null) || (this.compressionsCount == 0))
    {
      localVideoEditedInfo = null;
      return localVideoEditedInfo;
    }
    VideoEditedInfo localVideoEditedInfo = new VideoEditedInfo();
    localVideoEditedInfo.startTime = this.startTime;
    localVideoEditedInfo.endTime = this.endTime;
    localVideoEditedInfo.rotationValue = this.rotationValue;
    localVideoEditedInfo.originalWidth = this.originalWidth;
    localVideoEditedInfo.originalHeight = this.originalHeight;
    localVideoEditedInfo.bitrate = this.bitrate;
    localVideoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
    localVideoEditedInfo.estimatedSize = this.estimatedSize;
    localVideoEditedInfo.estimatedDuration = this.estimatedDuration;
    localVideoEditedInfo.framerate = this.videoFramerate;
    if ((!this.muteVideo) && ((this.compressItem.getTag() == null) || (this.selectedCompression == this.compressionsCount - 1)))
    {
      localVideoEditedInfo.resultWidth = this.originalWidth;
      localVideoEditedInfo.resultHeight = this.originalHeight;
      if (this.muteVideo) {}
      for (;;)
      {
        localVideoEditedInfo.bitrate = i;
        localVideoEditedInfo.muted = this.muteVideo;
        break;
        i = this.originalBitrate;
      }
    }
    if (this.muteVideo)
    {
      this.selectedCompression = 1;
      updateWidthHeightBitrateForCompression();
    }
    localVideoEditedInfo.resultWidth = this.resultWidth;
    localVideoEditedInfo.resultHeight = this.resultHeight;
    if (this.muteVideo) {}
    for (;;)
    {
      localVideoEditedInfo.bitrate = i;
      localVideoEditedInfo.muted = this.muteVideo;
      break;
      i = this.bitrate;
    }
  }
  
  private TLObject getFileLocation(int paramInt, int[] paramArrayOfInt)
  {
    Object localObject1 = null;
    Object localObject2;
    if (paramInt < 0) {
      localObject2 = localObject1;
    }
    for (;;)
    {
      return (TLObject)localObject2;
      if (!this.imagesArrLocations.isEmpty())
      {
        localObject2 = localObject1;
        if (paramInt < this.imagesArrLocations.size())
        {
          if (paramArrayOfInt != null) {
            paramArrayOfInt[0] = ((Integer)this.imagesArrLocationsSizes.get(paramInt)).intValue();
          }
          localObject2 = (TLObject)this.imagesArrLocations.get(paramInt);
        }
      }
      else
      {
        localObject2 = localObject1;
        if (!this.imagesArr.isEmpty())
        {
          localObject2 = localObject1;
          if (paramInt < this.imagesArr.size())
          {
            MessageObject localMessageObject = (MessageObject)this.imagesArr.get(paramInt);
            if ((localMessageObject.messageOwner instanceof TLRPC.TL_messageService))
            {
              if ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto))
              {
                localObject2 = localMessageObject.messageOwner.action.newUserPhoto.photo_big;
              }
              else
              {
                localObject2 = FileLoader.getClosestPhotoSizeWithSize(localMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                if (localObject2 != null)
                {
                  if (paramArrayOfInt != null)
                  {
                    paramArrayOfInt[0] = ((TLRPC.PhotoSize)localObject2).size;
                    if (paramArrayOfInt[0] == 0) {
                      paramArrayOfInt[0] = -1;
                    }
                  }
                  localObject2 = ((TLRPC.PhotoSize)localObject2).location;
                }
                else
                {
                  localObject2 = localObject1;
                  if (paramArrayOfInt != null)
                  {
                    paramArrayOfInt[0] = -1;
                    localObject2 = localObject1;
                  }
                }
              }
            }
            else if ((((localMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (localMessageObject.messageOwner.media.photo != null)) || (((localMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (localMessageObject.messageOwner.media.webpage != null)))
            {
              localObject2 = FileLoader.getClosestPhotoSizeWithSize(localMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
              if (localObject2 != null)
              {
                if (paramArrayOfInt != null)
                {
                  paramArrayOfInt[0] = ((TLRPC.PhotoSize)localObject2).size;
                  if (paramArrayOfInt[0] == 0) {
                    paramArrayOfInt[0] = -1;
                  }
                }
                localObject2 = ((TLRPC.PhotoSize)localObject2).location;
              }
              else
              {
                localObject2 = localObject1;
                if (paramArrayOfInt != null)
                {
                  paramArrayOfInt[0] = -1;
                  localObject2 = localObject1;
                }
              }
            }
            else if ((localMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice))
            {
              localObject2 = ((TLRPC.TL_messageMediaInvoice)localMessageObject.messageOwner.media).photo;
            }
            else
            {
              localObject2 = localObject1;
              if (localMessageObject.getDocument() != null)
              {
                localObject2 = localObject1;
                if (localMessageObject.getDocument().thumb != null)
                {
                  if (paramArrayOfInt != null)
                  {
                    paramArrayOfInt[0] = localMessageObject.getDocument().thumb.size;
                    if (paramArrayOfInt[0] == 0) {
                      paramArrayOfInt[0] = -1;
                    }
                  }
                  localObject2 = localMessageObject.getDocument().thumb.location;
                }
              }
            }
          }
        }
      }
    }
  }
  
  private String getFileName(int paramInt)
  {
    File localFile = null;
    Object localObject1;
    if (paramInt < 0) {
      localObject1 = localFile;
    }
    for (;;)
    {
      return (String)localObject1;
      if ((!this.imagesArrLocations.isEmpty()) || (!this.imagesArr.isEmpty()))
      {
        if (!this.imagesArrLocations.isEmpty())
        {
          localObject1 = localFile;
          if (paramInt < this.imagesArrLocations.size())
          {
            localObject1 = (TLRPC.FileLocation)this.imagesArrLocations.get(paramInt);
            localObject1 = ((TLRPC.FileLocation)localObject1).volume_id + "_" + ((TLRPC.FileLocation)localObject1).local_id + ".jpg";
          }
        }
        else
        {
          localObject1 = localFile;
          if (!this.imagesArr.isEmpty())
          {
            localObject1 = localFile;
            if (paramInt < this.imagesArr.size()) {
              localObject1 = FileLoader.getMessageFileName(((MessageObject)this.imagesArr.get(paramInt)).messageOwner);
            }
          }
        }
      }
      else
      {
        localObject1 = localFile;
        if (!this.imagesArrLocals.isEmpty())
        {
          localObject1 = localFile;
          if (paramInt < this.imagesArrLocals.size())
          {
            Object localObject2 = this.imagesArrLocals.get(paramInt);
            if ((localObject2 instanceof MediaController.SearchImage))
            {
              localObject1 = (MediaController.SearchImage)localObject2;
              if (((MediaController.SearchImage)localObject1).document != null)
              {
                localObject1 = FileLoader.getAttachFileName(((MediaController.SearchImage)localObject1).document);
              }
              else if ((((MediaController.SearchImage)localObject1).type != 1) && (((MediaController.SearchImage)localObject1).localUrl != null) && (((MediaController.SearchImage)localObject1).localUrl.length() > 0))
              {
                localFile = new File(((MediaController.SearchImage)localObject1).localUrl);
                if (localFile.exists()) {
                  localObject1 = localFile.getName();
                } else {
                  ((MediaController.SearchImage)localObject1).localUrl = "";
                }
              }
              else
              {
                localObject1 = Utilities.MD5(((MediaController.SearchImage)localObject1).imageUrl) + "." + ImageLoader.getHttpUrlExtension(((MediaController.SearchImage)localObject1).imageUrl, "jpg");
              }
            }
            else
            {
              localObject1 = localFile;
              if ((localObject2 instanceof TLRPC.BotInlineResult))
              {
                localObject2 = (TLRPC.BotInlineResult)localObject2;
                if (((TLRPC.BotInlineResult)localObject2).document != null)
                {
                  localObject1 = FileLoader.getAttachFileName(((TLRPC.BotInlineResult)localObject2).document);
                }
                else if (((TLRPC.BotInlineResult)localObject2).photo != null)
                {
                  localObject1 = FileLoader.getAttachFileName(FileLoader.getClosestPhotoSizeWithSize(((TLRPC.BotInlineResult)localObject2).photo.sizes, AndroidUtilities.getPhotoSize()));
                }
                else
                {
                  localObject1 = localFile;
                  if ((((TLRPC.BotInlineResult)localObject2).content instanceof TLRPC.TL_webDocument)) {
                    localObject1 = Utilities.MD5(((TLRPC.BotInlineResult)localObject2).content.url) + "." + ImageLoader.getHttpUrlExtension(((TLRPC.BotInlineResult)localObject2).content.url, FileLoader.getExtensionByMime(((TLRPC.BotInlineResult)localObject2).content.mime_type));
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  /* Error */
  public static PhotoViewer getInstance()
  {
    // Byte code:
    //   0: getstatic 609	org/telegram/ui/PhotoViewer:Instance	Lorg/telegram/ui/PhotoViewer;
    //   3: astore_0
    //   4: aload_0
    //   5: astore_1
    //   6: aload_0
    //   7: ifnonnull +31 -> 38
    //   10: ldc 2
    //   12: monitorenter
    //   13: getstatic 609	org/telegram/ui/PhotoViewer:Instance	Lorg/telegram/ui/PhotoViewer;
    //   16: astore_0
    //   17: aload_0
    //   18: astore_1
    //   19: aload_0
    //   20: ifnonnull +15 -> 35
    //   23: new 2	org/telegram/ui/PhotoViewer
    //   26: astore_1
    //   27: aload_1
    //   28: invokespecial 2325	org/telegram/ui/PhotoViewer:<init>	()V
    //   31: aload_1
    //   32: putstatic 609	org/telegram/ui/PhotoViewer:Instance	Lorg/telegram/ui/PhotoViewer;
    //   35: ldc 2
    //   37: monitorexit
    //   38: aload_1
    //   39: areturn
    //   40: astore_1
    //   41: ldc 2
    //   43: monitorexit
    //   44: aload_1
    //   45: athrow
    //   46: astore_1
    //   47: goto -6 -> 41
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	17	0	localPhotoViewer1	PhotoViewer
    //   5	34	1	localPhotoViewer2	PhotoViewer
    //   40	5	1	localObject1	Object
    //   46	1	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   13	17	40	finally
    //   23	31	40	finally
    //   35	38	40	finally
    //   41	44	40	finally
    //   31	35	46	finally
  }
  
  private int getLeftInset()
  {
    if ((this.lastInsets != null) && (Build.VERSION.SDK_INT >= 21)) {}
    for (int i = ((WindowInsets)this.lastInsets).getSystemWindowInsetLeft();; i = 0) {
      return i;
    }
  }
  
  public static PhotoViewer getPipInstance()
  {
    return PipInstance;
  }
  
  private void goToNext()
  {
    float f = 0.0F;
    if (this.scale != 1.0F) {
      f = (getContainerViewWidth() - this.centerImage.getImageWidth()) / 2 * this.scale;
    }
    this.switchImageAfterAnimation = 1;
    animateTo(this.scale, this.minX - getContainerViewWidth() - f - AndroidUtilities.dp(30.0F) / 2, this.translationY, false);
  }
  
  private void goToPrev()
  {
    float f = 0.0F;
    if (this.scale != 1.0F) {
      f = (getContainerViewWidth() - this.centerImage.getImageWidth()) / 2 * this.scale;
    }
    this.switchImageAfterAnimation = 2;
    animateTo(this.scale, this.maxX + getContainerViewWidth() + f + AndroidUtilities.dp(30.0F) / 2, this.translationY, false);
  }
  
  public static boolean hasInstance()
  {
    if (Instance != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void hideHint()
  {
    this.hintAnimation = new AnimatorSet();
    this.hintAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[] { 0.0F }) });
    this.hintAnimation.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if (paramAnonymousAnimator.equals(PhotoViewer.this.hintAnimation))
        {
          PhotoViewer.access$17102(PhotoViewer.this, null);
          PhotoViewer.access$17102(PhotoViewer.this, null);
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (paramAnonymousAnimator.equals(PhotoViewer.this.hintAnimation))
        {
          PhotoViewer.access$17002(PhotoViewer.this, null);
          PhotoViewer.access$17102(PhotoViewer.this, null);
          if (PhotoViewer.this.hintTextView != null) {
            PhotoViewer.this.hintTextView.setVisibility(8);
          }
        }
      }
    });
    this.hintAnimation.setDuration(300L);
    this.hintAnimation.start();
  }
  
  public static boolean isShowingImage(String paramString)
  {
    boolean bool = false;
    if (Instance != null) {
      if ((!Instance.isVisible) || (Instance.disableShowCheck) || (paramString == null) || (Instance.currentPathObject == null) || (!paramString.equals(Instance.currentPathObject))) {
        break label56;
      }
    }
    label56:
    for (bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isShowingImage(MessageObject paramMessageObject)
  {
    boolean bool1 = false;
    if (Instance != null)
    {
      if ((!Instance.pipAnimationInProgress) && (Instance.isVisible) && (!Instance.disableShowCheck) && (paramMessageObject != null) && (Instance.currentMessageObject != null) && (Instance.currentMessageObject.getId() == paramMessageObject.getId())) {
        bool1 = true;
      }
    }
    else
    {
      bool2 = bool1;
      if (!bool1)
      {
        bool2 = bool1;
        if (PipInstance != null) {
          if ((!PipInstance.isVisible) || (PipInstance.disableShowCheck) || (paramMessageObject == null) || (PipInstance.currentMessageObject == null) || (PipInstance.currentMessageObject.getId() != paramMessageObject.getId())) {
            break label136;
          }
        }
      }
    }
    label136:
    for (boolean bool2 = true;; bool2 = false)
    {
      return bool2;
      bool1 = false;
      break;
    }
  }
  
  public static boolean isShowingImage(TLRPC.BotInlineResult paramBotInlineResult)
  {
    boolean bool = false;
    if (Instance != null) {
      if ((!Instance.isVisible) || (Instance.disableShowCheck) || (paramBotInlineResult == null) || (Instance.currentBotInlineResult == null) || (paramBotInlineResult.id != Instance.currentBotInlineResult.id)) {
        break label59;
      }
    }
    label59:
    for (bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isShowingImage(TLRPC.FileLocation paramFileLocation)
  {
    boolean bool = false;
    if (Instance != null) {
      if ((!Instance.isVisible) || (Instance.disableShowCheck) || (paramFileLocation == null) || (Instance.currentFileLocation == null) || (paramFileLocation.local_id != Instance.currentFileLocation.local_id) || (paramFileLocation.volume_id != Instance.currentFileLocation.volume_id) || (paramFileLocation.dc_id != Instance.currentFileLocation.dc_id)) {
        break label92;
      }
    }
    label92:
    for (bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void onActionClick(boolean paramBoolean)
  {
    if (((this.currentMessageObject == null) && (this.currentBotInlineResult == null)) || (this.currentFileNames[0] == null)) {}
    for (;;)
    {
      return;
      Object localObject1 = null;
      Object localObject2 = null;
      localObject3 = null;
      Object localObject4 = null;
      this.isStreaming = false;
      Object localObject6;
      if (this.currentMessageObject != null)
      {
        localObject3 = localObject4;
        if (this.currentMessageObject.messageOwner.attachPath != null)
        {
          localObject3 = localObject4;
          if (this.currentMessageObject.messageOwner.attachPath.length() != 0)
          {
            localObject4 = new File(this.currentMessageObject.messageOwner.attachPath);
            localObject3 = localObject4;
            if (!((File)localObject4).exists()) {
              localObject3 = null;
            }
          }
        }
        localObject4 = localObject3;
        localObject6 = localObject2;
        if (localObject3 == null)
        {
          localObject3 = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
          localObject4 = localObject3;
          localObject6 = localObject2;
          if (!((File)localObject3).exists())
          {
            localObject3 = null;
            localObject4 = localObject3;
            localObject6 = localObject2;
            if (SharedConfig.streamMedia)
            {
              localObject4 = localObject3;
              localObject6 = localObject2;
              if ((int)this.currentMessageObject.getDialogId() != 0)
              {
                localObject4 = localObject3;
                localObject6 = localObject2;
                if (this.currentMessageObject.isVideo())
                {
                  localObject4 = localObject3;
                  localObject6 = localObject2;
                  if (this.currentMessageObject.canStreamVideo()) {
                    localObject6 = localObject1;
                  }
                }
              }
            }
          }
        }
      }
      for (;;)
      {
        try
        {
          FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
          localObject6 = localObject1;
          localObject2 = this.currentMessageObject.getDocument();
          localObject6 = localObject1;
          localObject4 = new java/lang/StringBuilder;
          localObject6 = localObject1;
          ((StringBuilder)localObject4).<init>();
          localObject6 = localObject1;
          localObject4 = "?account=" + this.currentMessageObject.currentAccount + "&id=" + ((TLRPC.Document)localObject2).id + "&hash=" + ((TLRPC.Document)localObject2).access_hash + "&dc=" + ((TLRPC.Document)localObject2).dc_id + "&size=" + ((TLRPC.Document)localObject2).size + "&mime=" + URLEncoder.encode(((TLRPC.Document)localObject2).mime_type, "UTF-8") + "&name=" + URLEncoder.encode(FileLoader.getDocumentFileName((TLRPC.Document)localObject2), "UTF-8");
          localObject6 = localObject1;
          localObject2 = new java/lang/StringBuilder;
          localObject6 = localObject1;
          ((StringBuilder)localObject2).<init>();
          localObject6 = localObject1;
          localObject4 = Uri.parse("tg://" + this.currentMessageObject.getFileName() + (String)localObject4);
          localObject6 = localObject4;
          this.isStreaming = true;
          localObject6 = localObject4;
          checkProgress(0, false);
          localObject6 = localObject4;
          localObject4 = localObject3;
        }
        catch (Exception localException)
        {
          Object localObject5 = localObject3;
          continue;
        }
        localObject3 = localObject6;
        if (localObject4 != null)
        {
          localObject3 = localObject6;
          if (localObject6 == null) {
            localObject3 = Uri.fromFile((File)localObject4);
          }
        }
        if (localObject3 != null) {
          continue;
        }
        if (!paramBoolean) {
          break;
        }
        if (this.currentMessageObject == null) {
          continue;
        }
        if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
          continue;
        }
        FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
        break;
        localObject4 = localObject3;
        localObject6 = localObject2;
        if (this.currentBotInlineResult != null) {
          if (this.currentBotInlineResult.document != null)
          {
            localObject3 = FileLoader.getPathToAttach(this.currentBotInlineResult.document);
            localObject4 = localObject3;
            localObject6 = localObject2;
            if (!((File)localObject3).exists())
            {
              localObject4 = null;
              localObject6 = localObject2;
            }
          }
          else
          {
            localObject4 = localObject3;
            localObject6 = localObject2;
            if ((this.currentBotInlineResult.content instanceof TLRPC.TL_webDocument))
            {
              localObject3 = new File(FileLoader.getDirectory(4), Utilities.MD5(this.currentBotInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.currentBotInlineResult.content.url, "mp4"));
              localObject4 = localObject3;
              localObject6 = localObject2;
              if (!((File)localObject3).exists())
              {
                localObject4 = null;
                localObject6 = localObject2;
              }
            }
          }
        }
      }
      FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
      continue;
      if (this.currentBotInlineResult != null) {
        if (this.currentBotInlineResult.document != null)
        {
          if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentBotInlineResult.document, true, 0);
          } else {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentBotInlineResult.document);
          }
        }
        else if ((this.currentBotInlineResult.content instanceof TLRPC.TL_webDocument)) {
          if (!ImageLoader.getInstance().isLoadingHttpFile(this.currentBotInlineResult.content.url))
          {
            ImageLoader.getInstance().loadHttpFile(this.currentBotInlineResult.content.url, "mp4", this.currentAccount);
          }
          else
          {
            ImageLoader.getInstance().cancelLoadHttpFile(this.currentBotInlineResult.content.url);
            continue;
            preparePlayer((Uri)localObject3, true, false);
          }
        }
      }
    }
  }
  
  @SuppressLint({"NewApi", "DrawAllocation"})
  private void onDraw(Canvas paramCanvas)
  {
    if ((this.animationInProgress == 1) || ((!this.isVisible) && (this.animationInProgress != 2) && (!this.pipAnimationInProgress))) {}
    for (;;)
    {
      return;
      float f1 = -1.0F;
      float f3;
      float f6;
      float f7;
      float f8;
      float f9;
      float f10;
      float f11;
      label315:
      Object localObject1;
      Object localObject2;
      label386:
      boolean bool;
      label394:
      int i;
      if (this.imageMoveAnimation != null)
      {
        if (!this.scroller.isFinished()) {
          this.scroller.abortAnimation();
        }
        float f2 = this.scale;
        f3 = this.animateToScale;
        float f4 = this.scale;
        float f5 = this.animationValue;
        f6 = this.translationX;
        f7 = this.animateToX;
        f8 = this.translationX;
        f9 = this.animationValue;
        f10 = this.translationY + (this.animateToY - this.translationY) * this.animationValue;
        if (this.currentEditMode == 1) {
          this.photoCropView.setAnimationProgress(this.animationValue);
        }
        f11 = f1;
        if (this.animateToScale == 1.0F)
        {
          f11 = f1;
          if (this.scale == 1.0F)
          {
            f11 = f1;
            if (this.translationX == 0.0F) {
              f11 = f10;
            }
          }
        }
        f3 = f2 + (f3 - f4) * f5;
        f1 = f6 + (f7 - f8) * f9;
        this.containerView.invalidate();
        if ((this.animationInProgress != 2) && (!this.pipAnimationInProgress) && (!this.isInline))
        {
          if ((this.currentEditMode != 0) || (this.scale != 1.0F) || (f11 == -1.0F) || (this.zoomAnimation)) {
            break label2181;
          }
          f6 = getContainerViewHeight() / 4.0F;
          this.backgroundDrawable.setAlpha((int)Math.max(127.0F, 255.0F * (1.0F - Math.min(Math.abs(f11), f6) / f6)));
        }
        localObject1 = null;
        localObject2 = null;
        if (this.currentEditMode == 0)
        {
          localObject1 = localObject2;
          if (this.scale >= 1.0F)
          {
            localObject1 = localObject2;
            if (!this.zoomAnimation)
            {
              localObject1 = localObject2;
              if (!this.zooming)
              {
                if (f1 <= this.maxX + AndroidUtilities.dp(5.0F)) {
                  break label2194;
                }
                localObject1 = this.leftImage;
              }
            }
          }
          if (localObject1 == null) {
            break label2235;
          }
          bool = true;
          this.changingPage = bool;
        }
        int j;
        if (localObject1 == this.rightImage)
        {
          f9 = f1;
          f8 = 0.0F;
          f5 = 1.0F;
          f7 = f5;
          f6 = f8;
          f11 = f9;
          if (!this.zoomAnimation)
          {
            f7 = f5;
            f6 = f8;
            f11 = f9;
            if (f9 < this.minX)
            {
              f7 = Math.min(1.0F, (this.minX - f9) / paramCanvas.getWidth());
              f6 = (1.0F - f7) * 0.3F;
              f11 = -paramCanvas.getWidth() - AndroidUtilities.dp(30.0F) / 2;
            }
          }
          if (((ImageReceiver)localObject1).hasBitmapImage())
          {
            paramCanvas.save();
            paramCanvas.translate(getContainerViewWidth() / 2, getContainerViewHeight() / 2);
            paramCanvas.translate(paramCanvas.getWidth() + AndroidUtilities.dp(30.0F) / 2 + f11, 0.0F);
            paramCanvas.scale(1.0F - f6, 1.0F - f6);
            i = ((ImageReceiver)localObject1).getBitmapWidth();
            j = ((ImageReceiver)localObject1).getBitmapHeight();
            f8 = getContainerViewWidth() / i;
            f9 = getContainerViewHeight() / j;
            if (f8 <= f9) {
              break label2241;
            }
            label614:
            i = (int)(i * f9);
            j = (int)(j * f9);
            ((ImageReceiver)localObject1).setAlpha(f7);
            ((ImageReceiver)localObject1).setImageCoords(-i / 2, -j / 2, i, j);
            ((ImageReceiver)localObject1).draw(paramCanvas);
            paramCanvas.restore();
          }
          this.groupedPhotosListView.setMoveProgress(-f7);
          paramCanvas.save();
          paramCanvas.translate(f11, f10 / f3);
          paramCanvas.translate((paramCanvas.getWidth() * (this.scale + 1.0F) + AndroidUtilities.dp(30.0F)) / 2.0F, -f10 / f3);
          this.photoProgressViews[1].setScale(1.0F - f6);
          this.photoProgressViews[1].setAlpha(f7);
          this.photoProgressViews[1].onDraw(paramCanvas);
          paramCanvas.restore();
        }
        f9 = f1;
        f5 = 0.0F;
        f8 = 1.0F;
        f11 = f8;
        f7 = f5;
        f6 = f9;
        if (!this.zoomAnimation)
        {
          f11 = f8;
          f7 = f5;
          f6 = f9;
          if (f9 > this.maxX)
          {
            f11 = f8;
            f7 = f5;
            f6 = f9;
            if (this.currentEditMode == 0)
            {
              f11 = Math.min(1.0F, (f9 - this.maxX) / paramCanvas.getWidth());
              f7 = f11 * 0.3F;
              f11 = 1.0F - f11;
              f6 = this.maxX;
            }
          }
        }
        if ((this.aspectRatioFrameLayout == null) || (this.aspectRatioFrameLayout.getVisibility() != 0)) {
          break label2248;
        }
        i = 1;
        label893:
        if (this.centerImage.hasBitmapImage())
        {
          paramCanvas.save();
          paramCanvas.translate(getContainerViewWidth() / 2 + getAdditionX(), getContainerViewHeight() / 2 + getAdditionY());
          paramCanvas.translate(f6, f10);
          paramCanvas.scale(f3 - f7, f3 - f7);
          if (this.currentEditMode == 1) {
            this.photoCropView.setBitmapParams(f3, f6, f10);
          }
          int k = this.centerImage.getBitmapWidth();
          int m = this.centerImage.getBitmapHeight();
          j = m;
          int n = k;
          if (i != 0)
          {
            j = m;
            n = k;
            if (this.textureUploaded)
            {
              j = m;
              n = k;
              if (Math.abs(k / m - this.videoTextureView.getMeasuredWidth() / this.videoTextureView.getMeasuredHeight()) > 0.01F)
              {
                n = this.videoTextureView.getMeasuredWidth();
                j = this.videoTextureView.getMeasuredHeight();
              }
            }
          }
          f8 = getContainerViewWidth() / n;
          f9 = getContainerViewHeight() / j;
          if (f8 <= f9) {
            break label2254;
          }
          label1116:
          n = (int)(n * f9);
          j = (int)(j * f9);
          if ((i == 0) || (!this.textureUploaded) || (!this.videoCrossfadeStarted) || (this.videoCrossfadeAlpha != 1.0F))
          {
            this.centerImage.setAlpha(f11);
            this.centerImage.setImageCoords(-n / 2, -j / 2, n, j);
            this.centerImage.draw(paramCanvas);
          }
          if (i != 0)
          {
            if ((!this.videoCrossfadeStarted) && (this.textureUploaded))
            {
              this.videoCrossfadeStarted = true;
              this.videoCrossfadeAlpha = 0.0F;
              this.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
            }
            paramCanvas.translate(-n / 2, -j / 2);
            this.videoTextureView.setAlpha(this.videoCrossfadeAlpha * f11);
            this.aspectRatioFrameLayout.draw(paramCanvas);
            if ((this.videoCrossfadeStarted) && (this.videoCrossfadeAlpha < 1.0F))
            {
              long l1 = System.currentTimeMillis();
              long l2 = this.videoCrossfadeAlphaLastTime;
              this.videoCrossfadeAlphaLastTime = l1;
              this.videoCrossfadeAlpha += (float)(l1 - l2) / 200.0F;
              this.containerView.invalidate();
              if (this.videoCrossfadeAlpha > 1.0F) {
                this.videoCrossfadeAlpha = 1.0F;
              }
            }
          }
          paramCanvas.restore();
        }
        if (!this.isCurrentVideo) {
          break label2267;
        }
        if ((this.progressView.getVisibility() == 0) || ((this.videoPlayer != null) && (this.videoPlayer.isPlaying()))) {
          break label2261;
        }
        i = 1;
        label1389:
        if (i != 0)
        {
          paramCanvas.save();
          paramCanvas.translate(f6, f10 / f3);
          this.photoProgressViews[0].setScale(1.0F - f7);
          this.photoProgressViews[0].setAlpha(f11);
          this.photoProgressViews[0].onDraw(paramCanvas);
          paramCanvas.restore();
        }
        if ((!this.pipAnimationInProgress) && ((this.miniProgressView.getVisibility() == 0) || (this.miniProgressAnimator != null)))
        {
          paramCanvas.save();
          paramCanvas.translate(this.miniProgressView.getLeft() + f6, this.miniProgressView.getTop() + f10 / f3);
          this.miniProgressView.draw(paramCanvas);
          paramCanvas.restore();
        }
        if (localObject1 == this.leftImage)
        {
          if (((ImageReceiver)localObject1).hasBitmapImage())
          {
            paramCanvas.save();
            paramCanvas.translate(getContainerViewWidth() / 2, getContainerViewHeight() / 2);
            paramCanvas.translate(-(paramCanvas.getWidth() * (this.scale + 1.0F) + AndroidUtilities.dp(30.0F)) / 2.0F + f1, 0.0F);
            j = ((ImageReceiver)localObject1).getBitmapWidth();
            i = ((ImageReceiver)localObject1).getBitmapHeight();
            f7 = getContainerViewWidth() / j;
            f6 = getContainerViewHeight() / i;
            if (f7 <= f6) {
              break label2294;
            }
            j = (int)(j * f6);
            i = (int)(i * f6);
            ((ImageReceiver)localObject1).setAlpha(1.0F);
            ((ImageReceiver)localObject1).setImageCoords(-j / 2, -i / 2, j, i);
            ((ImageReceiver)localObject1).draw(paramCanvas);
            paramCanvas.restore();
          }
          this.groupedPhotosListView.setMoveProgress(1.0F - f11);
          paramCanvas.save();
          paramCanvas.translate(f1, f10 / f3);
          paramCanvas.translate(-(paramCanvas.getWidth() * (this.scale + 1.0F) + AndroidUtilities.dp(30.0F)) / 2.0F, -f10 / f3);
          this.photoProgressViews[2].setScale(1.0F);
          this.photoProgressViews[2].setAlpha(1.0F);
          this.photoProgressViews[2].onDraw(paramCanvas);
          paramCanvas.restore();
        }
        if (this.waitingForDraw == 0) {
          continue;
        }
        this.waitingForDraw -= 1;
        if (this.waitingForDraw != 0) {
          break label2339;
        }
        if (this.textureImageView == null) {}
      }
      try
      {
        this.currentBitmap = Bitmaps.createBitmap(this.videoTextureView.getWidth(), this.videoTextureView.getHeight(), Bitmap.Config.ARGB_8888);
        this.changedTextureView.getBitmap(this.currentBitmap);
        if (this.currentBitmap != null)
        {
          this.textureImageView.setVisibility(0);
          this.textureImageView.setImageBitmap(this.currentBitmap);
          this.pipVideoView.close();
          this.pipVideoView = null;
          continue;
          if (this.animationStartTime != 0L)
          {
            this.translationX = this.animateToX;
            this.translationY = this.animateToY;
            this.scale = this.animateToScale;
            this.animationStartTime = 0L;
            if (this.currentEditMode == 1) {
              this.photoCropView.setAnimationProgress(1.0F);
            }
            updateMinMax(this.scale);
            this.zoomAnimation = false;
          }
          if ((!this.scroller.isFinished()) && (this.scroller.computeScrollOffset()))
          {
            if ((this.scroller.getStartX() < this.maxX) && (this.scroller.getStartX() > this.minX)) {
              this.translationX = this.scroller.getCurrX();
            }
            if ((this.scroller.getStartY() < this.maxY) && (this.scroller.getStartY() > this.minY)) {
              this.translationY = this.scroller.getCurrY();
            }
            this.containerView.invalidate();
          }
          if (this.switchImageAfterAnimation != 0)
          {
            if (this.switchImageAfterAnimation != 1) {
              break label2159;
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                PhotoViewer.this.setImageIndex(PhotoViewer.this.currentIndex + 1, false);
              }
            });
          }
          for (;;)
          {
            this.switchImageAfterAnimation = 0;
            f6 = this.scale;
            f9 = this.translationY;
            f7 = this.translationX;
            f11 = f1;
            f3 = f6;
            f1 = f7;
            f10 = f9;
            if (this.moving) {
              break;
            }
            f11 = this.translationY;
            f3 = f6;
            f1 = f7;
            f10 = f9;
            break;
            label2159:
            if (this.switchImageAfterAnimation == 2) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  PhotoViewer.this.setImageIndex(PhotoViewer.this.currentIndex - 1, false);
                }
              });
            }
          }
          label2181:
          this.backgroundDrawable.setAlpha(255);
          break label315;
          label2194:
          if (f1 < this.minX - AndroidUtilities.dp(5.0F))
          {
            localObject1 = this.rightImage;
            break label386;
          }
          this.groupedPhotosListView.setMoveProgress(0.0F);
          localObject1 = localObject2;
          break label386;
          label2235:
          bool = false;
          break label394;
          label2241:
          f9 = f8;
          break label614;
          label2248:
          i = 0;
          break label893;
          label2254:
          f9 = f8;
          break label1116;
          label2261:
          i = 0;
          break label1389;
          label2267:
          if ((i == 0) && (this.videoPlayerControlFrameLayout.getVisibility() != 0)) {}
          for (i = 1;; i = 0) {
            break;
          }
          label2294:
          f6 = f7;
        }
      }
      catch (Throwable paramCanvas)
      {
        for (;;)
        {
          if (this.currentBitmap != null)
          {
            this.currentBitmap.recycle();
            this.currentBitmap = null;
          }
          FileLog.e(paramCanvas);
          continue;
          this.textureImageView.setImageDrawable(null);
        }
      }
      label2339:
      this.containerView.invalidate();
    }
  }
  
  private void onPhotoClosed(PlaceProviderObject paramPlaceProviderObject)
  {
    this.isVisible = false;
    this.disableShowCheck = true;
    this.currentMessageObject = null;
    this.currentBotInlineResult = null;
    this.currentFileLocation = null;
    this.currentPathObject = null;
    if (this.currentThumb != null)
    {
      this.currentThumb.release();
      this.currentThumb = null;
    }
    this.parentAlert = null;
    if (this.currentAnimation != null)
    {
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
    this.centerImage.setImageBitmap((Bitmap)null);
    this.leftImage.setImageBitmap((Bitmap)null);
    this.rightImage.setImageBitmap((Bitmap)null);
    this.containerView.post(new Runnable()
    {
      public void run()
      {
        PhotoViewer.this.animatingImageView.setImageBitmap(null);
        try
        {
          if (PhotoViewer.this.windowView.getParent() != null) {
            ((WindowManager)PhotoViewer.this.parentActivity.getSystemService("window")).removeView(PhotoViewer.this.windowView);
          }
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
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
    if (paramPlaceProviderObject != null) {
      paramPlaceProviderObject.imageReceiver.setVisible(true, true);
    }
  }
  
  private void onPhotoShow(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, ArrayList<MessageObject> paramArrayList, ArrayList<Object> paramArrayList1, int paramInt, PlaceProviderObject paramPlaceProviderObject)
  {
    this.classGuid = ConnectionsManager.generateClassGuid();
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
    Object localObject = this.endReached;
    if (this.mergeDialogId == 0L) {}
    int i;
    for (boolean bool = true;; bool = false)
    {
      localObject[1] = bool;
      this.opennedFromMedia = false;
      this.needCaptionLayout = false;
      this.containerView.setTag(Integer.valueOf(1));
      this.isCurrentVideo = false;
      this.imagesArr.clear();
      this.imagesArrLocations.clear();
      this.imagesArrLocationsSizes.clear();
      this.avatarsArr.clear();
      this.imagesArrLocals.clear();
      for (i = 0; i < 2; i++)
      {
        this.imagesByIds[i].clear();
        this.imagesByIdsTemp[i].clear();
      }
    }
    this.imagesArrTemp.clear();
    this.currentUserAvatarLocation = null;
    this.containerView.setPadding(0, 0, 0, 0);
    if (this.currentThumb != null) {
      this.currentThumb.release();
    }
    if (paramPlaceProviderObject != null)
    {
      localObject = paramPlaceProviderObject.thumb;
      this.currentThumb = ((ImageReceiver.BitmapHolder)localObject);
      if ((paramPlaceProviderObject == null) || (!paramPlaceProviderObject.isEvent)) {
        break label807;
      }
    }
    label807:
    for (bool = true;; bool = false)
    {
      this.isEvent = bool;
      this.menuItem.setVisibility(0);
      this.sendItem.setVisibility(8);
      this.pipItem.setVisibility(8);
      this.cameraItem.setVisibility(8);
      this.cameraItem.setTag(null);
      this.bottomLayout.setVisibility(0);
      this.bottomLayout.setTag(Integer.valueOf(1));
      this.bottomLayout.setTranslationY(0.0F);
      this.captionTextView.setTranslationY(0.0F);
      this.shareButton.setVisibility(8);
      if (this.qualityChooseView != null)
      {
        this.qualityChooseView.setVisibility(4);
        this.qualityPicker.setVisibility(4);
        this.qualityChooseView.setTag(null);
      }
      if (this.qualityChooseViewAnimation != null)
      {
        this.qualityChooseViewAnimation.cancel();
        this.qualityChooseViewAnimation = null;
      }
      this.allowShare = false;
      this.slideshowMessageId = 0;
      this.nameOverride = null;
      this.dateOverride = 0;
      this.menuItem.hideSubItem(2);
      this.menuItem.hideSubItem(4);
      this.menuItem.hideSubItem(10);
      this.menuItem.hideSubItem(11);
      this.actionBar.setTranslationY(0.0F);
      this.checkImageView.setAlpha(1.0F);
      this.checkImageView.setVisibility(8);
      this.actionBar.setTitleRightMargin(0);
      this.photosCounterView.setAlpha(1.0F);
      this.photosCounterView.setVisibility(8);
      this.pickerView.setVisibility(8);
      this.pickerViewSendButton.setVisibility(8);
      this.pickerViewSendButton.setTranslationY(0.0F);
      this.pickerView.setAlpha(1.0F);
      this.pickerViewSendButton.setAlpha(1.0F);
      this.pickerView.setTranslationY(0.0F);
      this.paintItem.setVisibility(8);
      this.cropItem.setVisibility(8);
      this.tuneItem.setVisibility(8);
      this.timeItem.setVisibility(8);
      this.videoTimelineView.setVisibility(8);
      this.compressItem.setVisibility(8);
      this.captionEditText.setVisibility(8);
      this.mentionListView.setVisibility(8);
      this.muteItem.setVisibility(8);
      this.actionBar.setSubtitle(null);
      this.masksItem.setVisibility(8);
      this.muteVideo = false;
      this.muteItem.setImageResource(NUM);
      this.editorDoneLayout.setVisibility(8);
      this.captionTextView.setTag(null);
      this.captionTextView.setVisibility(4);
      if (this.photoCropView != null) {
        this.photoCropView.setVisibility(8);
      }
      if (this.photoFilterView != null) {
        this.photoFilterView.setVisibility(8);
      }
      for (i = 0; i < 3; i++) {
        if (this.photoProgressViews[i] != null) {
          this.photoProgressViews[i].setBackgroundState(-1, false);
        }
      }
      localObject = null;
      break;
    }
    if ((paramMessageObject != null) && (paramArrayList == null))
    {
      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (paramMessageObject.messageOwner.media.webpage != null))
      {
        paramFileLocation = paramMessageObject.messageOwner.media.webpage;
        paramArrayList = paramFileLocation.site_name;
        if (paramArrayList != null)
        {
          paramArrayList = paramArrayList.toLowerCase();
          if ((paramArrayList.equals("instagram")) || (paramArrayList.equals("twitter")) || ("telegram_album".equals(paramFileLocation.type)))
          {
            if (!TextUtils.isEmpty(paramFileLocation.author)) {
              this.nameOverride = paramFileLocation.author;
            }
            if ((paramFileLocation.cached_page instanceof TLRPC.TL_pageFull))
            {
              i = 0;
              if (i < paramFileLocation.cached_page.blocks.size())
              {
                paramArrayList = (TLRPC.PageBlock)paramFileLocation.cached_page.blocks.get(i);
                if (!(paramArrayList instanceof TLRPC.TL_pageBlockAuthorDate)) {
                  break label1226;
                }
                this.dateOverride = ((TLRPC.TL_pageBlockAuthorDate)paramArrayList).published_date;
              }
            }
            paramFileLocation = paramMessageObject.getWebPagePhotos(null, null);
            if (!paramFileLocation.isEmpty())
            {
              this.slideshowMessageId = paramMessageObject.getId();
              this.needSearchImageInArr = false;
              this.imagesArr.addAll(paramFileLocation);
              this.totalImagesCount = this.imagesArr.size();
              setImageIndex(this.imagesArr.indexOf(paramMessageObject), true);
            }
          }
        }
      }
      if (this.slideshowMessageId == 0)
      {
        this.imagesArr.add(paramMessageObject);
        if ((this.currentAnimation != null) || (paramMessageObject.eventId != 0L))
        {
          this.needSearchImageInArr = false;
          label1082:
          setImageIndex(0, true);
        }
      }
      else
      {
        label1088:
        if ((this.currentAnimation == null) && (!this.isEvent))
        {
          if ((this.currentDialogId == 0L) || (this.totalImagesCount != 0)) {
            break label2121;
          }
          DataQuery.getInstance(this.currentAccount).getMediaCount(this.currentDialogId, 0, this.classGuid, true);
          if (this.mergeDialogId != 0L) {
            DataQuery.getInstance(this.currentAccount).getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
          }
        }
        label1167:
        if (((this.currentMessageObject == null) || (!this.currentMessageObject.isVideo())) && ((this.currentBotInlineResult == null) || ((!this.currentBotInlineResult.type.equals("video")) && (!MessageObject.isVideoDocument(this.currentBotInlineResult.document))))) {
          break label2153;
        }
        onActionClick(false);
      }
    }
    label1226:
    label1390:
    label1449:
    label1751:
    label1754:
    label1821:
    label1869:
    label2067:
    label2096:
    label2102:
    label2108:
    label2114:
    label2121:
    label2153:
    while (this.imagesArrLocals.isEmpty())
    {
      return;
      i++;
      break;
      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) || ((paramMessageObject.messageOwner.action != null) && (!(paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty)))) {
        break label1082;
      }
      this.needSearchImageInArr = true;
      this.imagesByIds[0].put(paramMessageObject.getId(), paramMessageObject);
      this.menuItem.showSubItem(2);
      this.sendItem.setVisibility(0);
      break label1082;
      if (paramFileLocation != null)
      {
        this.avatarsDialogId = paramPlaceProviderObject.dialogId;
        this.imagesArrLocations.add(paramFileLocation);
        this.imagesArrLocationsSizes.add(Integer.valueOf(paramPlaceProviderObject.size));
        this.avatarsArr.add(new TLRPC.TL_photoEmpty());
        paramMessageObject = this.shareButton;
        if (this.videoPlayerControlFrameLayout.getVisibility() != 0)
        {
          i = 0;
          paramMessageObject.setVisibility(i);
          this.allowShare = true;
          this.menuItem.hideSubItem(2);
          if (this.shareButton.getVisibility() != 0) {
            break label1449;
          }
          this.menuItem.hideSubItem(10);
        }
        for (;;)
        {
          setImageIndex(0, true);
          this.currentUserAvatarLocation = paramFileLocation;
          break;
          i = 8;
          break label1390;
          this.menuItem.showSubItem(10);
        }
      }
      if (paramArrayList != null)
      {
        this.opennedFromMedia = true;
        this.menuItem.showSubItem(4);
        this.sendItem.setVisibility(0);
        this.imagesArr.addAll(paramArrayList);
        i = 0;
        if (i < this.imagesArr.size())
        {
          paramFileLocation = (MessageObject)this.imagesArr.get(i);
          paramMessageObject = this.imagesByIds;
          if (paramFileLocation.getDialogId() == this.currentDialogId) {}
          for (int j = 0;; j = 1)
          {
            paramMessageObject[j].put(paramFileLocation.getId(), paramFileLocation);
            i++;
            break;
          }
        }
        setImageIndex(paramInt, true);
        break label1088;
      }
      if (paramArrayList1 == null) {
        break label1088;
      }
      if ((this.sendPhotoType == 0) || ((this.sendPhotoType == 2) && (paramArrayList1.size() > 1)))
      {
        this.checkImageView.setVisibility(0);
        this.photosCounterView.setVisibility(0);
        this.actionBar.setTitleRightMargin(AndroidUtilities.dp(100.0F));
      }
      if (this.sendPhotoType == 2)
      {
        this.cameraItem.setVisibility(0);
        this.cameraItem.setTag(Integer.valueOf(1));
      }
      this.menuItem.setVisibility(8);
      this.imagesArrLocals.addAll(paramArrayList1);
      paramMessageObject = this.imagesArrLocals.get(paramInt);
      if ((paramMessageObject instanceof MediaController.PhotoEntry)) {
        if (((MediaController.PhotoEntry)paramMessageObject).isVideo)
        {
          this.cropItem.setVisibility(8);
          this.bottomLayout.setVisibility(0);
          this.bottomLayout.setTag(Integer.valueOf(1));
          this.bottomLayout.setTranslationY(-AndroidUtilities.dp(48.0F));
          i = 1;
          if ((this.parentChatActivity != null) && ((this.parentChatActivity.currentEncryptedChat == null) || (AndroidUtilities.getPeerLayerVersion(this.parentChatActivity.currentEncryptedChat.layer) >= 46)))
          {
            this.mentionsAdapter.setChatInfo(this.parentChatActivity.info);
            paramMessageObject = this.mentionsAdapter;
            if (this.parentChatActivity.currentChat == null) {
              break label2102;
            }
            bool = true;
            paramMessageObject.setNeedUsernames(bool);
            this.mentionsAdapter.setNeedBotContext(false);
            if ((i == 0) || ((this.placeProvider != null) && ((this.placeProvider == null) || (!this.placeProvider.allowCaption())))) {
              break label2108;
            }
            bool = true;
            this.needCaptionLayout = bool;
            paramMessageObject = this.captionEditText;
            if (!this.needCaptionLayout) {
              break label2114;
            }
          }
        }
      }
      for (i = 0;; i = 8)
      {
        paramMessageObject.setVisibility(i);
        if (this.needCaptionLayout) {
          this.captionEditText.onCreate();
        }
        this.pickerView.setVisibility(0);
        this.pickerViewSendButton.setVisibility(0);
        this.pickerViewSendButton.setTranslationY(0.0F);
        this.pickerViewSendButton.setAlpha(1.0F);
        this.bottomLayout.setVisibility(8);
        this.bottomLayout.setTag(null);
        this.containerView.setTag(null);
        setImageIndex(paramInt, true);
        this.paintItem.setVisibility(this.cropItem.getVisibility());
        this.tuneItem.setVisibility(this.cropItem.getVisibility());
        updateSelectedCount();
        break;
        this.cropItem.setVisibility(0);
        break label1751;
        if ((paramMessageObject instanceof TLRPC.BotInlineResult))
        {
          this.cropItem.setVisibility(8);
          i = 0;
          break label1754;
        }
        paramFileLocation = this.cropItem;
        if (((paramMessageObject instanceof MediaController.SearchImage)) && (((MediaController.SearchImage)paramMessageObject).type == 0))
        {
          i = 0;
          paramFileLocation.setVisibility(i);
          if (this.cropItem.getVisibility() != 0) {
            break label2096;
          }
        }
        for (i = 1;; i = 0)
        {
          break;
          i = 8;
          break label2067;
        }
        bool = false;
        break label1821;
        bool = false;
        break label1869;
      }
      if (this.avatarsDialogId == 0) {
        break label1167;
      }
      MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.avatarsDialogId, 80, 0L, true, this.classGuid);
      break label1167;
    }
    paramFileLocation = this.imagesArrLocals.get(paramInt);
    if (this.parentChatActivity != null)
    {
      paramMessageObject = this.parentChatActivity.getCurrentUser();
      label2188:
      if ((this.parentChatActivity == null) || (this.parentChatActivity.isSecretChat()) || (paramMessageObject == null) || (paramMessageObject.bot)) {
        break label2287;
      }
      paramInt = 1;
      label2219:
      if (!(paramFileLocation instanceof MediaController.PhotoEntry)) {
        break label2293;
      }
      paramMessageObject = (MediaController.PhotoEntry)paramFileLocation;
      i = paramInt;
      if (paramMessageObject.isVideo)
      {
        preparePlayer(Uri.fromFile(new File(paramMessageObject.path)), false, false);
        i = paramInt;
      }
    }
    for (;;)
    {
      label2266:
      if (i != 0)
      {
        this.timeItem.setVisibility(0);
        break;
        paramMessageObject = null;
        break label2188;
        label2287:
        paramInt = 0;
        break label2219;
        label2293:
        i = paramInt;
        if (paramInt != 0)
        {
          i = paramInt;
          if ((paramFileLocation instanceof MediaController.SearchImage)) {
            if (((MediaController.SearchImage)paramFileLocation).type != 0) {
              break label2333;
            }
          }
        }
      }
    }
    label2333:
    for (paramInt = 1;; paramInt = 0)
    {
      i = paramInt;
      break label2266;
      break;
    }
  }
  
  /* Error */
  private void onSharePressed()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_1
    //   2: aload_0
    //   3: getfield 1135	org/telegram/ui/PhotoViewer:parentActivity	Landroid/app/Activity;
    //   6: ifnull +10 -> 16
    //   9: aload_0
    //   10: getfield 2717	org/telegram/ui/PhotoViewer:allowShare	Z
    //   13: ifne +4 -> 17
    //   16: return
    //   17: aconst_null
    //   18: astore_2
    //   19: aconst_null
    //   20: astore_3
    //   21: iconst_0
    //   22: istore 4
    //   24: aload_0
    //   25: getfield 1353	org/telegram/ui/PhotoViewer:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   28: ifnull +192 -> 220
    //   31: aload_0
    //   32: getfield 1353	org/telegram/ui/PhotoViewer:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   35: invokevirtual 1779	org/telegram/messenger/MessageObject:isVideo	()Z
    //   38: istore 4
    //   40: aload_0
    //   41: getfield 1353	org/telegram/ui/PhotoViewer:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   44: getfield 1736	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   47: getfield 1741	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   50: invokestatic 1747	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   53: ifne +34 -> 87
    //   56: new 1494	java/io/File
    //   59: astore_3
    //   60: aload_3
    //   61: aload_0
    //   62: getfield 1353	org/telegram/ui/PhotoViewer:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   65: getfield 1736	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   68: getfield 1741	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   71: invokespecial 1750	java/io/File:<init>	(Ljava/lang/String;)V
    //   74: aload_3
    //   75: invokevirtual 1753	java/io/File:exists	()Z
    //   78: istore 5
    //   80: iload 5
    //   82: ifne +273 -> 355
    //   85: aconst_null
    //   86: astore_3
    //   87: aload_3
    //   88: astore_2
    //   89: iload 4
    //   91: istore 5
    //   93: aload_3
    //   94: ifnonnull +18 -> 112
    //   97: aload_0
    //   98: getfield 1353	org/telegram/ui/PhotoViewer:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   101: getfield 1736	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   104: invokestatic 1805	org/telegram/messenger/FileLoader:getPathToMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Ljava/io/File;
    //   107: astore_2
    //   108: iload 4
    //   110: istore 5
    //   112: aload_2
    //   113: invokevirtual 1753	java/io/File:exists	()Z
    //   116: ifeq +228 -> 344
    //   119: new 2883	android/content/Intent
    //   122: astore 6
    //   124: aload 6
    //   126: ldc_w 2885
    //   129: invokespecial 2886	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   132: iload 5
    //   134: ifeq +142 -> 276
    //   137: aload 6
    //   139: ldc_w 2888
    //   142: invokevirtual 2892	android/content/Intent:setType	(Ljava/lang/String;)Landroid/content/Intent;
    //   145: pop
    //   146: getstatic 1682	android/os/Build$VERSION:SDK_INT	I
    //   149: istore 7
    //   151: iload 7
    //   153: bipush 24
    //   155: if_icmplt +173 -> 328
    //   158: aload 6
    //   160: ldc_w 2894
    //   163: aload_0
    //   164: getfield 1135	org/telegram/ui/PhotoViewer:parentActivity	Landroid/app/Activity;
    //   167: ldc_w 2896
    //   170: aload_2
    //   171: invokestatic 2902	android/support/v4/content/FileProvider:getUriForFile	(Landroid/content/Context;Ljava/lang/String;Ljava/io/File;)Landroid/net/Uri;
    //   174: invokevirtual 2906	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   177: pop
    //   178: aload 6
    //   180: iconst_1
    //   181: invokevirtual 2910	android/content/Intent:setFlags	(I)Landroid/content/Intent;
    //   184: pop
    //   185: aload_0
    //   186: getfield 1135	org/telegram/ui/PhotoViewer:parentActivity	Landroid/app/Activity;
    //   189: aload 6
    //   191: ldc_w 2912
    //   194: ldc_w 2913
    //   197: invokestatic 1667	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   200: invokestatic 2917	android/content/Intent:createChooser	(Landroid/content/Intent;Ljava/lang/CharSequence;)Landroid/content/Intent;
    //   203: sipush 500
    //   206: invokevirtual 2921	android/app/Activity:startActivityForResult	(Landroid/content/Intent;I)V
    //   209: goto -193 -> 16
    //   212: astore_2
    //   213: aload_2
    //   214: invokestatic 2153	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   217: goto -201 -> 16
    //   220: iload 4
    //   222: istore 5
    //   224: aload_0
    //   225: getfield 1365	org/telegram/ui/PhotoViewer:currentFileLocation	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   228: ifnull -116 -> 112
    //   231: aload_0
    //   232: getfield 1365	org/telegram/ui/PhotoViewer:currentFileLocation	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   235: astore_2
    //   236: iload_1
    //   237: istore 5
    //   239: aload_0
    //   240: getfield 1368	org/telegram/ui/PhotoViewer:avatarsDialogId	I
    //   243: ifne +13 -> 256
    //   246: aload_0
    //   247: getfield 1371	org/telegram/ui/PhotoViewer:isEvent	Z
    //   250: ifeq +20 -> 270
    //   253: iload_1
    //   254: istore 5
    //   256: aload_2
    //   257: iload 5
    //   259: invokestatic 1492	org/telegram/messenger/FileLoader:getPathToAttach	(Lorg/telegram/tgnet/TLObject;Z)Ljava/io/File;
    //   262: astore_2
    //   263: iload 4
    //   265: istore 5
    //   267: goto -155 -> 112
    //   270: iconst_0
    //   271: istore 5
    //   273: goto -17 -> 256
    //   276: aload_0
    //   277: getfield 1353	org/telegram/ui/PhotoViewer:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   280: ifnull +19 -> 299
    //   283: aload 6
    //   285: aload_0
    //   286: getfield 1353	org/telegram/ui/PhotoViewer:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   289: invokevirtual 2924	org/telegram/messenger/MessageObject:getMimeType	()Ljava/lang/String;
    //   292: invokevirtual 2892	android/content/Intent:setType	(Ljava/lang/String;)Landroid/content/Intent;
    //   295: pop
    //   296: goto -150 -> 146
    //   299: aload 6
    //   301: ldc_w 2926
    //   304: invokevirtual 2892	android/content/Intent:setType	(Ljava/lang/String;)Landroid/content/Intent;
    //   307: pop
    //   308: goto -162 -> 146
    //   311: astore_3
    //   312: aload 6
    //   314: ldc_w 2894
    //   317: aload_2
    //   318: invokestatic 2413	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   321: invokevirtual 2906	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   324: pop
    //   325: goto -140 -> 185
    //   328: aload 6
    //   330: ldc_w 2894
    //   333: aload_2
    //   334: invokestatic 2413	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   337: invokevirtual 2906	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   340: pop
    //   341: goto -156 -> 185
    //   344: aload_0
    //   345: invokespecial 1375	org/telegram/ui/PhotoViewer:showDownloadAlert	()V
    //   348: goto -332 -> 16
    //   351: astore_2
    //   352: goto -139 -> 213
    //   355: goto -268 -> 87
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	358	0	this	PhotoViewer
    //   1	253	1	bool1	boolean
    //   18	153	2	localObject1	Object
    //   212	2	2	localException1	Exception
    //   235	99	2	localObject2	Object
    //   351	1	2	localException2	Exception
    //   20	74	3	localFile	File
    //   311	1	3	localException3	Exception
    //   22	242	4	bool2	boolean
    //   78	194	5	bool3	boolean
    //   122	207	6	localIntent	Intent
    //   149	7	7	i	int
    // Exception table:
    //   from	to	target	type
    //   24	40	212	java/lang/Exception
    //   40	74	212	java/lang/Exception
    //   97	108	212	java/lang/Exception
    //   112	132	212	java/lang/Exception
    //   137	146	212	java/lang/Exception
    //   146	151	212	java/lang/Exception
    //   185	209	212	java/lang/Exception
    //   224	236	212	java/lang/Exception
    //   239	253	212	java/lang/Exception
    //   256	263	212	java/lang/Exception
    //   276	296	212	java/lang/Exception
    //   299	308	212	java/lang/Exception
    //   312	325	212	java/lang/Exception
    //   328	341	212	java/lang/Exception
    //   344	348	212	java/lang/Exception
    //   158	185	311	java/lang/Exception
    //   74	80	351	java/lang/Exception
  }
  
  private boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if ((this.animationInProgress != 0) || (this.animationStartTime != 0L)) {
      bool = false;
    }
    label350:
    float f1;
    float f2;
    float f3;
    for (;;)
    {
      return bool;
      if (this.currentEditMode == 2)
      {
        this.photoFilterView.onTouch(paramMotionEvent);
        bool = true;
      }
      else if (this.currentEditMode == 1)
      {
        bool = true;
      }
      else if ((this.captionEditText.isPopupShowing()) || (this.captionEditText.isKeyboardVisible()))
      {
        if (paramMotionEvent.getAction() == 1) {
          closeCaptionEnter(true);
        }
        bool = true;
      }
      else if ((this.currentEditMode == 0) && (paramMotionEvent.getPointerCount() == 1) && (this.gestureDetector.onTouchEvent(paramMotionEvent)) && (this.doubleTap))
      {
        this.doubleTap = false;
        this.moving = false;
        this.zooming = false;
        checkMinMax(false);
        bool = true;
      }
      else
      {
        if ((paramMotionEvent.getActionMasked() == 0) || (paramMotionEvent.getActionMasked() == 5))
        {
          if (this.currentEditMode == 1) {
            this.photoCropView.cancelAnimationRunnable();
          }
          this.discardTap = false;
          if (!this.scroller.isFinished()) {
            this.scroller.abortAnimation();
          }
          if ((!this.draggingDown) && (!this.changingPage))
          {
            if ((!this.canZoom) || (paramMotionEvent.getPointerCount() != 2)) {
              break label350;
            }
            this.pinchStartDistance = ((float)Math.hypot(paramMotionEvent.getX(1) - paramMotionEvent.getX(0), paramMotionEvent.getY(1) - paramMotionEvent.getY(0)));
            this.pinchStartScale = this.scale;
            this.pinchCenterX = ((paramMotionEvent.getX(0) + paramMotionEvent.getX(1)) / 2.0F);
            this.pinchCenterY = ((paramMotionEvent.getY(0) + paramMotionEvent.getY(1)) / 2.0F);
            this.pinchStartX = this.translationX;
            this.pinchStartY = this.translationY;
            this.zooming = true;
            this.moving = false;
            if (this.velocityTracker != null) {
              this.velocityTracker.clear();
            }
          }
        }
        label1099:
        label1288:
        label1485:
        label1536:
        do
        {
          do
          {
            for (;;)
            {
              bool = false;
              break;
              if (paramMotionEvent.getPointerCount() == 1)
              {
                this.moveStartX = paramMotionEvent.getX();
                f1 = paramMotionEvent.getY();
                this.moveStartY = f1;
                this.dragY = f1;
                this.draggingDown = false;
                this.canDragDown = true;
                if (this.velocityTracker != null)
                {
                  this.velocityTracker.clear();
                  continue;
                  if (paramMotionEvent.getActionMasked() != 2) {
                    break label1288;
                  }
                  if (this.currentEditMode == 1) {
                    this.photoCropView.cancelAnimationRunnable();
                  }
                  if ((this.canZoom) && (paramMotionEvent.getPointerCount() == 2) && (!this.draggingDown) && (this.zooming) && (!this.changingPage))
                  {
                    this.discardTap = true;
                    this.scale = ((float)Math.hypot(paramMotionEvent.getX(1) - paramMotionEvent.getX(0), paramMotionEvent.getY(1) - paramMotionEvent.getY(0)) / this.pinchStartDistance * this.pinchStartScale);
                    this.translationX = (this.pinchCenterX - getContainerViewWidth() / 2 - (this.pinchCenterX - getContainerViewWidth() / 2 - this.pinchStartX) * (this.scale / this.pinchStartScale));
                    this.translationY = (this.pinchCenterY - getContainerViewHeight() / 2 - (this.pinchCenterY - getContainerViewHeight() / 2 - this.pinchStartY) * (this.scale / this.pinchStartScale));
                    updateMinMax(this.scale);
                    this.containerView.invalidate();
                  }
                  else if (paramMotionEvent.getPointerCount() == 1)
                  {
                    if (this.velocityTracker != null) {
                      this.velocityTracker.addMovement(paramMotionEvent);
                    }
                    f1 = Math.abs(paramMotionEvent.getX() - this.moveStartX);
                    f2 = Math.abs(paramMotionEvent.getY() - this.dragY);
                    if ((f1 > AndroidUtilities.dp(3.0F)) || (f2 > AndroidUtilities.dp(3.0F)))
                    {
                      this.discardTap = true;
                      if ((this.qualityChooseView != null) && (this.qualityChooseView.getVisibility() == 0))
                      {
                        bool = true;
                        break;
                      }
                    }
                    if ((this.placeProvider.canScrollAway()) && (this.currentEditMode == 0) && (this.canDragDown) && (!this.draggingDown) && (this.scale == 1.0F) && (f2 >= AndroidUtilities.dp(30.0F)) && (f2 / 2.0F > f1))
                    {
                      this.draggingDown = true;
                      this.moving = false;
                      this.dragY = paramMotionEvent.getY();
                      if ((this.isActionBarVisible) && (this.containerView.getTag() != null)) {
                        toggleActionBar(false, true);
                      }
                      for (;;)
                      {
                        bool = true;
                        break;
                        if (this.pickerView.getVisibility() == 0)
                        {
                          toggleActionBar(false, true);
                          togglePhotosListView(false, true);
                          toggleCheckImageView(false);
                        }
                      }
                    }
                    if (this.draggingDown)
                    {
                      this.translationY = (paramMotionEvent.getY() - this.dragY);
                      this.containerView.invalidate();
                    }
                    else if ((!this.invalidCoords) && (this.animationStartTime == 0L))
                    {
                      f1 = this.moveStartX - paramMotionEvent.getX();
                      f2 = this.moveStartY - paramMotionEvent.getY();
                      if ((this.moving) || (this.currentEditMode != 0) || ((this.scale == 1.0F) && (Math.abs(f2) + AndroidUtilities.dp(12.0F) < Math.abs(f1))) || (this.scale != 1.0F))
                      {
                        if (!this.moving)
                        {
                          f1 = 0.0F;
                          f2 = 0.0F;
                          this.moving = true;
                          this.canDragDown = false;
                        }
                        this.moveStartX = paramMotionEvent.getX();
                        this.moveStartY = paramMotionEvent.getY();
                        updateMinMax(this.scale);
                        if ((this.translationX >= this.minX) || ((this.currentEditMode == 0) && (this.rightImage.hasImage())))
                        {
                          f3 = f1;
                          if (this.translationX <= this.maxX) {
                            break label1099;
                          }
                          if (this.currentEditMode == 0)
                          {
                            f3 = f1;
                            if (this.leftImage.hasImage()) {
                              break label1099;
                            }
                          }
                        }
                        f3 = f1 / 3.0F;
                        if ((this.maxY == 0.0F) && (this.minY == 0.0F) && (this.currentEditMode == 0)) {
                          if (this.translationY - f2 < this.minY)
                          {
                            this.translationY = this.minY;
                            f1 = 0.0F;
                          }
                        }
                        for (;;)
                        {
                          this.translationX -= f3;
                          if ((this.scale != 1.0F) || (this.currentEditMode != 0)) {
                            this.translationY -= f1;
                          }
                          this.containerView.invalidate();
                          break;
                          f1 = f2;
                          if (this.translationY - f2 > this.maxY)
                          {
                            this.translationY = this.maxY;
                            f1 = 0.0F;
                            continue;
                            if (this.translationY >= this.minY)
                            {
                              f1 = f2;
                              if (this.translationY <= this.maxY) {}
                            }
                            else
                            {
                              f1 = f2 / 3.0F;
                            }
                          }
                        }
                      }
                    }
                    else
                    {
                      this.invalidCoords = false;
                      this.moveStartX = paramMotionEvent.getX();
                      this.moveStartY = paramMotionEvent.getY();
                    }
                  }
                }
              }
            }
          } while ((paramMotionEvent.getActionMasked() != 3) && (paramMotionEvent.getActionMasked() != 1) && (paramMotionEvent.getActionMasked() != 6));
          if (this.currentEditMode == 1) {
            this.photoCropView.startAnimationRunnable();
          }
          if (this.zooming)
          {
            this.invalidCoords = true;
            if (this.scale < 1.0F)
            {
              updateMinMax(1.0F);
              animateTo(1.0F, 0.0F, 0.0F, true);
            }
            for (;;)
            {
              this.zooming = false;
              break;
              if (this.scale > 3.0F)
              {
                f2 = this.pinchCenterX - getContainerViewWidth() / 2 - (this.pinchCenterX - getContainerViewWidth() / 2 - this.pinchStartX) * (3.0F / this.pinchStartScale);
                f3 = this.pinchCenterY - getContainerViewHeight() / 2 - (this.pinchCenterY - getContainerViewHeight() / 2 - this.pinchStartY) * (3.0F / this.pinchStartScale);
                updateMinMax(3.0F);
                if (f2 < this.minX)
                {
                  f1 = this.minX;
                  if (f3 >= this.minY) {
                    break label1536;
                  }
                  f2 = this.minY;
                }
                for (;;)
                {
                  animateTo(3.0F, f1, f2, true);
                  break;
                  f1 = f2;
                  if (f2 <= this.maxX) {
                    break label1485;
                  }
                  f1 = this.maxX;
                  break label1485;
                  f2 = f3;
                  if (f3 > this.maxY) {
                    f2 = this.maxY;
                  }
                }
              }
              checkMinMax(true);
            }
          }
          if (this.draggingDown)
          {
            if (Math.abs(this.dragY - paramMotionEvent.getY()) > getContainerViewHeight() / 6.0F) {
              closePhoto(true, false);
            }
            for (;;)
            {
              this.draggingDown = false;
              break;
              if (this.pickerView.getVisibility() == 0)
              {
                toggleActionBar(true, true);
                toggleCheckImageView(true);
              }
              animateTo(1.0F, 0.0F, 0.0F, false);
            }
          }
        } while (!this.moving);
        f3 = this.translationX;
        f2 = this.translationY;
        updateMinMax(this.scale);
        this.moving = false;
        this.canDragDown = true;
        float f4 = 0.0F;
        f1 = f4;
        if (this.velocityTracker != null)
        {
          f1 = f4;
          if (this.scale == 1.0F)
          {
            this.velocityTracker.computeCurrentVelocity(1000);
            f1 = this.velocityTracker.getXVelocity();
          }
        }
        if (this.currentEditMode != 0) {
          break;
        }
        if (((this.translationX < this.minX - getContainerViewWidth() / 3) || (f1 < -AndroidUtilities.dp(650.0F))) && (this.rightImage.hasImage()))
        {
          goToNext();
          bool = true;
        }
        else
        {
          if (((this.translationX <= this.maxX + getContainerViewWidth() / 3) && (f1 <= AndroidUtilities.dp(650.0F))) || (!this.leftImage.hasImage())) {
            break;
          }
          goToPrev();
          bool = true;
        }
      }
    }
    if (this.translationX < this.minX)
    {
      f1 = this.minX;
      label1852:
      if (this.translationY >= this.minY) {
        break label1908;
      }
      f2 = this.minY;
    }
    for (;;)
    {
      animateTo(this.scale, f1, f2, false);
      break;
      f1 = f3;
      if (this.translationX <= this.maxX) {
        break label1852;
      }
      f1 = this.maxX;
      break label1852;
      label1908:
      if (this.translationY > this.maxY) {
        f2 = this.maxY;
      }
    }
  }
  
  private void openCaptionEnter()
  {
    if ((this.imageMoveAnimation != null) || (this.changeModeAnimation != null) || (this.currentEditMode != 0)) {}
    for (;;)
    {
      return;
      this.selectedPhotosListView.setVisibility(8);
      this.selectedPhotosListView.setEnabled(false);
      this.selectedPhotosListView.setAlpha(0.0F);
      this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0F));
      this.photosCounterView.setRotationX(0.0F);
      this.isPhotosListViewVisible = false;
      this.captionEditText.setTag(Integer.valueOf(1));
      this.captionEditText.openKeyboard();
      this.lastTitle = this.actionBar.getTitle();
      if (this.isCurrentVideo)
      {
        ActionBar localActionBar = this.actionBar;
        if (this.muteVideo) {}
        for (String str = LocaleController.getString("GifCaption", NUM);; str = LocaleController.getString("VideoCaption", NUM))
        {
          localActionBar.setTitle(str);
          this.actionBar.setSubtitle(null);
          break;
        }
      }
      this.actionBar.setTitle(LocaleController.getString("PhotoCaption", NUM));
    }
  }
  
  private void preparePlayer(Uri paramUri, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 0;
    if (!paramBoolean2) {
      this.currentPlayingVideoFile = paramUri;
    }
    if (this.parentActivity == null) {}
    for (;;)
    {
      return;
      this.streamingAlertShown = false;
      this.startedPlayTime = SystemClock.elapsedRealtime();
      this.currentVideoFinishedLoading = false;
      this.lastBufferedPositionCheck = 0L;
      this.seekToProgressPending = 0.0F;
      this.firstAnimationDelay = true;
      this.inPreview = paramBoolean2;
      releasePlayer();
      if (this.videoTextureView == null)
      {
        this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity)
        {
          protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            super.onMeasure(paramAnonymousInt1, paramAnonymousInt2);
            if (PhotoViewer.this.textureImageView != null)
            {
              ViewGroup.LayoutParams localLayoutParams = PhotoViewer.this.textureImageView.getLayoutParams();
              localLayoutParams.width = getMeasuredWidth();
              localLayoutParams.height = getMeasuredHeight();
            }
          }
        };
        this.aspectRatioFrameLayout.setVisibility(4);
        this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
        this.videoTextureView = new TextureView(this.parentActivity);
        this.videoTextureView.setPivotX(0.0F);
        this.videoTextureView.setPivotY(0.0F);
        this.videoTextureView.setOpaque(false);
        this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
      }
      if ((Build.VERSION.SDK_INT >= 21) && (this.textureImageView == null))
      {
        this.textureImageView = new ImageView(this.parentActivity);
        this.textureImageView.setBackgroundColor(-65536);
        this.textureImageView.setPivotX(0.0F);
        this.textureImageView.setPivotY(0.0F);
        this.textureImageView.setVisibility(4);
        this.containerView.addView(this.textureImageView);
      }
      this.textureUploaded = false;
      this.videoCrossfadeStarted = false;
      TextureView localTextureView = this.videoTextureView;
      this.videoCrossfadeAlpha = 0.0F;
      localTextureView.setAlpha(0.0F);
      this.videoPlayButton.setImageResource(NUM);
      if (this.videoPlayer == null)
      {
        this.videoPlayer = new VideoPlayer();
        this.videoPlayer.setTextureView(this.videoTextureView);
        this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate()
        {
          public void onError(Exception paramAnonymousException)
          {
            FileLog.e(paramAnonymousException);
            if (!PhotoViewer.this.menuItem.isSubItemVisible(11)) {}
            for (;;)
            {
              return;
              paramAnonymousException = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
              paramAnonymousException.setTitle(LocaleController.getString("AppName", NUM));
              paramAnonymousException.setMessage(LocaleController.getString("CantPlayVideo", NUM));
              paramAnonymousException.setPositiveButton(LocaleController.getString("Open", NUM), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  try
                  {
                    AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                    PhotoViewer.this.closePhoto(false, false);
                    return;
                  }
                  catch (Exception paramAnonymous2DialogInterface)
                  {
                    for (;;)
                    {
                      FileLog.e(paramAnonymous2DialogInterface);
                    }
                  }
                }
              });
              paramAnonymousException.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
              PhotoViewer.this.showAlertDialog(paramAnonymousException);
            }
          }
          
          public void onRenderedFirstFrame()
          {
            if (!PhotoViewer.this.textureUploaded)
            {
              PhotoViewer.access$13602(PhotoViewer.this, true);
              PhotoViewer.this.containerView.invalidate();
            }
          }
          
          public void onStateChanged(boolean paramAnonymousBoolean, int paramAnonymousInt)
          {
            if (PhotoViewer.this.videoPlayer == null) {
              return;
            }
            boolean bool;
            if (PhotoViewer.this.isStreaming)
            {
              PhotoViewer localPhotoViewer = PhotoViewer.this;
              if (paramAnonymousInt == 2)
              {
                bool = true;
                localPhotoViewer.toggleMiniProgress(bool, true);
              }
            }
            else
            {
              if ((!paramAnonymousBoolean) || (paramAnonymousInt == 4) || (paramAnonymousInt == 1)) {
                break label322;
              }
            }
            label322:
            label358:
            do
            {
              do
              {
                for (;;)
                {
                  try
                  {
                    PhotoViewer.this.parentActivity.getWindow().addFlags(128);
                    PhotoViewer.access$13002(PhotoViewer.this, true);
                    if ((PhotoViewer.this.seekToProgressPending != 0.0F) && ((paramAnonymousInt == 3) || (paramAnonymousInt == 1)))
                    {
                      int i = (int)((float)PhotoViewer.this.videoPlayer.getDuration() * PhotoViewer.this.seekToProgressPending);
                      PhotoViewer.this.videoPlayer.seekTo(i);
                      PhotoViewer.access$1102(PhotoViewer.this, 0.0F);
                    }
                    if (paramAnonymousInt == 3)
                    {
                      if (PhotoViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                        PhotoViewer.this.aspectRatioFrameLayout.setVisibility(0);
                      }
                      if (!PhotoViewer.this.pipItem.isEnabled())
                      {
                        PhotoViewer.access$13102(PhotoViewer.this, true);
                        PhotoViewer.this.pipItem.setEnabled(true);
                        PhotoViewer.this.pipItem.setAlpha(1.0F);
                      }
                    }
                    if ((!PhotoViewer.this.videoPlayer.isPlaying()) || (paramAnonymousInt == 4)) {
                      break label358;
                    }
                    if (!PhotoViewer.this.isPlaying)
                    {
                      PhotoViewer.access$1602(PhotoViewer.this, true);
                      PhotoViewer.this.videoPlayButton.setImageResource(NUM);
                      AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable);
                    }
                    if (PhotoViewer.this.pipVideoView != null) {
                      PhotoViewer.this.pipVideoView.updatePlayButton();
                    }
                    PhotoViewer.this.updateVideoPlayerTime();
                    break;
                    bool = false;
                  }
                  catch (Exception localException1)
                  {
                    FileLog.e(localException1);
                    continue;
                  }
                  try
                  {
                    PhotoViewer.this.parentActivity.getWindow().clearFlags(128);
                    PhotoViewer.access$13002(PhotoViewer.this, false);
                  }
                  catch (Exception localException2)
                  {
                    FileLog.e(localException2);
                  }
                }
              } while (!PhotoViewer.this.isPlaying);
              PhotoViewer.access$1602(PhotoViewer.this, false);
              PhotoViewer.this.videoPlayButton.setImageResource(NUM);
              AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.updateProgressRunnable);
            } while (paramAnonymousInt != 4);
            if (PhotoViewer.this.isCurrentVideo) {
              if (!PhotoViewer.this.videoTimelineView.isDragging())
              {
                PhotoViewer.this.videoTimelineView.setProgress(0.0F);
                if ((PhotoViewer.this.inPreview) || (PhotoViewer.this.videoTimelineView.getVisibility() != 0)) {
                  break label538;
                }
                PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoTimelineView.getLeftProgress() * (float)PhotoViewer.this.videoPlayer.getDuration()));
                label495:
                PhotoViewer.this.videoPlayer.pause();
                PhotoViewer.this.containerView.invalidate();
              }
            }
            for (;;)
            {
              label515:
              if (PhotoViewer.this.pipVideoView != null)
              {
                PhotoViewer.this.pipVideoView.onVideoCompleted();
                break;
                label538:
                PhotoViewer.this.videoPlayer.seekTo(0L);
                break label495;
                if (!PhotoViewer.this.isActionBarVisible) {
                  PhotoViewer.this.toggleActionBar(true, true);
                }
                if (!PhotoViewer.this.videoPlayerSeekbar.isDragging())
                {
                  PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0F);
                  PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                  if ((PhotoViewer.this.inPreview) || (PhotoViewer.this.videoTimelineView.getVisibility() != 0)) {
                    break label675;
                  }
                  PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoTimelineView.getLeftProgress() * (float)PhotoViewer.this.videoPlayer.getDuration()));
                }
              }
            }
            for (;;)
            {
              PhotoViewer.this.videoPlayer.pause();
              break label515;
              break;
              label675:
              PhotoViewer.this.videoPlayer.seekTo(0L);
            }
          }
          
          public boolean onSurfaceDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
          {
            boolean bool = true;
            if (PhotoViewer.this.changingTextureView)
            {
              PhotoViewer.access$2002(PhotoViewer.this, false);
              if (PhotoViewer.this.isInline)
              {
                if (PhotoViewer.this.isInline) {
                  PhotoViewer.access$2702(PhotoViewer.this, 1);
                }
                PhotoViewer.this.changedTextureView.setSurfaceTexture(paramAnonymousSurfaceTexture);
                PhotoViewer.this.changedTextureView.setSurfaceTextureListener(PhotoViewer.this.surfaceTextureListener);
                PhotoViewer.this.changedTextureView.setVisibility(0);
              }
            }
            for (;;)
            {
              return bool;
              bool = false;
            }
          }
          
          public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture)
          {
            if (PhotoViewer.this.waitingForFirstTextureUpload == 2)
            {
              if (PhotoViewer.this.textureImageView != null)
              {
                PhotoViewer.this.textureImageView.setVisibility(4);
                PhotoViewer.this.textureImageView.setImageDrawable(null);
                if (PhotoViewer.this.currentBitmap != null)
                {
                  PhotoViewer.this.currentBitmap.recycle();
                  PhotoViewer.access$1902(PhotoViewer.this, null);
                }
              }
              PhotoViewer.access$1802(PhotoViewer.this, false);
              if (Build.VERSION.SDK_INT >= 21)
              {
                PhotoViewer.this.aspectRatioFrameLayout.getLocationInWindow(PhotoViewer.this.pipPosition);
                paramAnonymousSurfaceTexture = PhotoViewer.this.pipPosition;
                paramAnonymousSurfaceTexture[0] -= PhotoViewer.this.getLeftInset();
                paramAnonymousSurfaceTexture = PhotoViewer.this.pipPosition;
                paramAnonymousSurfaceTexture[1] = ((int)(paramAnonymousSurfaceTexture[1] - PhotoViewer.this.containerView.getTranslationY()));
                paramAnonymousSurfaceTexture = new AnimatorSet();
                paramAnonymousSurfaceTexture.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, "translationX", new float[] { PhotoViewer.this.pipPosition[0] }), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, "translationY", new float[] { PhotoViewer.this.pipPosition[1] }), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, "translationX", new float[] { PhotoViewer.this.pipPosition[0] - PhotoViewer.this.aspectRatioFrameLayout.getX() }), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, "translationY", new float[] { PhotoViewer.this.pipPosition[1] - PhotoViewer.this.aspectRatioFrameLayout.getY() }), ObjectAnimator.ofInt(PhotoViewer.this.backgroundDrawable, "alpha", new int[] { 255 }), ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.groupedPhotosListView, "alpha", new float[] { 1.0F }) });
                paramAnonymousSurfaceTexture.setInterpolator(new DecelerateInterpolator());
                paramAnonymousSurfaceTexture.setDuration(250L);
                paramAnonymousSurfaceTexture.addListener(new AnimatorListenerAdapter()
                {
                  public void onAnimationEnd(Animator paramAnonymous2Animator)
                  {
                    PhotoViewer.access$12602(PhotoViewer.this, false);
                  }
                });
                paramAnonymousSurfaceTexture.start();
              }
              PhotoViewer.access$2702(PhotoViewer.this, 0);
            }
          }
          
          public void onVideoSizeChanged(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, float paramAnonymousFloat)
          {
            int i;
            int j;
            AspectRatioFrameLayout localAspectRatioFrameLayout;
            if (PhotoViewer.this.aspectRatioFrameLayout != null)
            {
              if (paramAnonymousInt3 != 90)
              {
                i = paramAnonymousInt1;
                j = paramAnonymousInt2;
                if (paramAnonymousInt3 != 270) {}
              }
              else
              {
                j = paramAnonymousInt1;
                i = paramAnonymousInt2;
              }
              localAspectRatioFrameLayout = PhotoViewer.this.aspectRatioFrameLayout;
              if (j != 0) {
                break label61;
              }
            }
            label61:
            for (paramAnonymousFloat = 1.0F;; paramAnonymousFloat = i * paramAnonymousFloat / j)
            {
              localAspectRatioFrameLayout.setAspectRatio(paramAnonymousFloat, paramAnonymousInt3);
              return;
            }
          }
        });
      }
      this.videoPlayer.preparePlayer(paramUri, "other");
      this.videoPlayerSeekbar.setProgress(0.0F);
      this.videoTimelineView.setProgress(0.0F);
      this.videoPlayerSeekbar.setBufferedProgress(0.0F);
      if ((this.currentBotInlineResult != null) && ((this.currentBotInlineResult.type.equals("video")) || (MessageObject.isVideoDocument(this.currentBotInlineResult.document))))
      {
        this.bottomLayout.setVisibility(0);
        this.bottomLayout.setTranslationY(-AndroidUtilities.dp(48.0F));
      }
      paramUri = this.videoPlayerControlFrameLayout;
      if (this.isCurrentVideo) {
        i = 8;
      }
      paramUri.setVisibility(i);
      this.dateTextView.setVisibility(8);
      this.nameTextView.setVisibility(8);
      if (this.allowShare)
      {
        this.shareButton.setVisibility(8);
        this.menuItem.showSubItem(10);
      }
      this.videoPlayer.setPlayWhenReady(paramBoolean1);
      this.inPreview = paramBoolean2;
    }
  }
  
  private void processOpenVideo(final String paramString, boolean paramBoolean)
  {
    if (this.currentLoadingVideoRunnable != null)
    {
      Utilities.globalQueue.cancelRunnable(this.currentLoadingVideoRunnable);
      this.currentLoadingVideoRunnable = null;
    }
    this.videoPreviewMessageObject = null;
    setCompressItemEnabled(false, true);
    this.muteVideo = paramBoolean;
    this.videoTimelineView.setVideoPath(paramString);
    this.compressionsCount = -1;
    this.rotationValue = 0;
    this.videoFramerate = 25;
    this.originalSize = new File(paramString).length();
    DispatchQueue localDispatchQueue = Utilities.globalQueue;
    paramString = new Runnable()
    {
      public void run()
      {
        if (PhotoViewer.this.currentLoadingVideoRunnable != this) {}
        final Object localObject1;
        Object localObject4;
        int i;
        long l1;
        label324:
        int k;
        for (;;)
        {
          return;
          localObject1 = null;
          Object localObject2 = null;
          boolean bool1 = true;
          localObject4 = localObject1;
          final boolean bool2;
          try
          {
            Object localObject5 = new com/coremedia/iso/IsoFile;
            localObject4 = localObject1;
            ((IsoFile)localObject5).<init>(paramString);
            localObject4 = localObject1;
            List localList = Path.getPaths((Container)localObject5, "/moov/trak/");
            localObject4 = localObject1;
            if (Path.getPath((Container)localObject5, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") == null)
            {
              localObject4 = localObject1;
              if (BuildVars.LOGS_ENABLED)
              {
                localObject4 = localObject1;
                FileLog.d("video hasn't mp4a atom");
              }
            }
            localObject4 = localObject1;
            if (Path.getPath((Container)localObject5, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null)
            {
              localObject4 = localObject1;
              if (BuildVars.LOGS_ENABLED)
              {
                localObject4 = localObject1;
                FileLog.d("video hasn't avc1 atom");
              }
              bool1 = false;
            }
            localObject4 = localObject1;
            PhotoViewer.access$18002(PhotoViewer.this, 0L);
            localObject4 = localObject1;
            PhotoViewer.access$18102(PhotoViewer.this, 0L);
            i = 0;
            localObject4 = localObject2;
            bool2 = bool1;
            localObject1 = localObject2;
            long l2;
            long l3;
            if (i < localList.size())
            {
              localObject4 = localObject2;
              if (PhotoViewer.this.currentLoadingVideoRunnable != this) {
                continue;
              }
              localObject4 = localObject2;
              Object localObject6 = (TrackBox)localList.get(i);
              l1 = 0L;
              l2 = 0L;
              localObject5 = null;
              Object localObject7 = null;
              localObject1 = localObject7;
              l3 = l1;
              try
              {
                localObject4 = ((TrackBox)localObject6).getMediaBox();
                localObject5 = localObject4;
                localObject1 = localObject7;
                l3 = l1;
                localObject7 = ((MediaBox)localObject4).getMediaHeaderBox();
                localObject5 = localObject4;
                localObject1 = localObject7;
                l3 = l1;
                long[] arrayOfLong = ((MediaBox)localObject4).getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes();
                for (j = 0;; j++)
                {
                  localObject5 = localObject4;
                  localObject1 = localObject7;
                  l3 = l1;
                  if (j >= arrayOfLong.length) {
                    break label324;
                  }
                  localObject5 = localObject4;
                  localObject1 = localObject7;
                  l3 = l1;
                  if (PhotoViewer.this.currentLoadingVideoRunnable != this) {
                    break;
                  }
                  l1 += arrayOfLong[j];
                }
                localObject5 = localObject4;
                localObject1 = localObject7;
                l3 = l1;
                PhotoViewer.access$10102(PhotoViewer.this, (float)((MediaHeaderBox)localObject7).getDuration() / (float)((MediaHeaderBox)localObject7).getTimescale());
                float f1 = (float)(8L * l1);
                localObject5 = localObject4;
                localObject1 = localObject7;
                l3 = l1;
                float f2 = PhotoViewer.this.videoDuration;
                l3 = (int)(f1 / f2);
                localObject1 = localObject7;
                localObject5 = localObject4;
              }
              catch (Exception localException2)
              {
                for (;;)
                {
                  int j;
                  localObject4 = localObject2;
                  FileLog.e(localException2);
                  l1 = l3;
                  l3 = l2;
                }
              }
              localObject4 = localObject2;
              if (PhotoViewer.this.currentLoadingVideoRunnable != this) {
                continue;
              }
              localObject4 = localObject2;
              localObject6 = ((TrackBox)localObject6).getTrackHeaderBox();
              localObject4 = localObject2;
              if (((TrackHeaderBox)localObject6).getWidth() == 0.0D) {
                break label815;
              }
              localObject4 = localObject2;
              if (((TrackHeaderBox)localObject6).getHeight() == 0.0D) {
                break label815;
              }
              if (localObject2 != null)
              {
                localObject4 = localObject2;
                if (((TrackHeaderBox)localObject2).getWidth() >= ((TrackHeaderBox)localObject6).getWidth())
                {
                  localObject4 = localObject2;
                  localObject7 = localObject2;
                  if (((TrackHeaderBox)localObject2).getHeight() >= ((TrackHeaderBox)localObject6).getHeight()) {
                    break label806;
                  }
                }
              }
              localObject2 = localObject6;
              localObject4 = localObject2;
              PhotoViewer.access$18202(PhotoViewer.this, PhotoViewer.access$18302(PhotoViewer.this, (int)(l3 / 100000L * 100000L)));
              localObject4 = localObject2;
              if (PhotoViewer.this.bitrate > 900000)
              {
                localObject4 = localObject2;
                PhotoViewer.access$18302(PhotoViewer.this, 900000);
              }
              localObject4 = localObject2;
              PhotoViewer.access$18102(PhotoViewer.this, PhotoViewer.this.videoFramesSize + l1);
              localObject7 = localObject2;
              if (localObject5 == null) {
                break label806;
              }
              localObject7 = localObject2;
              if (localObject1 == null) {
                break label806;
              }
              localObject4 = localObject2;
              localObject5 = ((MediaBox)localObject5).getMediaInformationBox().getSampleTableBox().getTimeToSampleBox();
              localObject7 = localObject2;
              if (localObject5 == null) {
                break label806;
              }
              localObject4 = localObject2;
              localObject5 = ((TimeToSampleBox)localObject5).getEntries();
              l1 = 0L;
              localObject4 = localObject2;
              k = Math.min(((List)localObject5).size(), 11);
              for (j = 1; j < k; j++)
              {
                localObject4 = localObject2;
                l1 += ((TimeToSampleBox.Entry)((List)localObject5).get(j)).getDelta();
              }
            }
            if (PhotoViewer.this.currentLoadingVideoRunnable != this) {
              continue;
            }
          }
          catch (Exception localException1)
          {
            FileLog.e(localException1);
            bool2 = false;
            localObject1 = localObject4;
            if (localObject1 == null)
            {
              if (BuildVars.LOGS_ENABLED) {
                FileLog.d("video hasn't trackHeaderBox atom");
              }
              bool2 = false;
            }
          }
          PhotoViewer.access$17902(PhotoViewer.this, null);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (PhotoViewer.this.parentActivity == null) {
                return;
              }
              PhotoViewer.access$18502(PhotoViewer.this, bool2);
              Object localObject1;
              label67:
              label217:
              boolean bool;
              if (bool2)
              {
                localObject1 = localObject1.getMatrix();
                if (((Matrix)localObject1).equals(Matrix.ROTATE_90))
                {
                  PhotoViewer.access$18602(PhotoViewer.this, 90);
                  PhotoViewer.access$18702(PhotoViewer.this, PhotoViewer.access$17402(PhotoViewer.this, (int)localObject1.getWidth()));
                  PhotoViewer.access$18802(PhotoViewer.this, PhotoViewer.access$17502(PhotoViewer.this, (int)localObject1.getHeight()));
                  PhotoViewer.access$10102(PhotoViewer.this, PhotoViewer.this.videoDuration * 1000.0F);
                  localObject1 = MessagesController.getGlobalMainSettings();
                  PhotoViewer.access$9602(PhotoViewer.this, ((SharedPreferences)localObject1).getInt("compress_video2", 1));
                  if ((PhotoViewer.this.originalWidth <= 1280) && (PhotoViewer.this.originalHeight <= 1280)) {
                    break label492;
                  }
                  PhotoViewer.access$17302(PhotoViewer.this, 5);
                  PhotoViewer.this.updateWidthHeightBitrateForCompression();
                  localObject1 = PhotoViewer.this;
                  if (PhotoViewer.this.compressionsCount <= 1) {
                    break label648;
                  }
                  bool = true;
                  label251:
                  ((PhotoViewer)localObject1).setCompressItemEnabled(bool, true);
                  if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("compressionsCount = " + PhotoViewer.this.compressionsCount + " w = " + PhotoViewer.this.originalWidth + " h = " + PhotoViewer.this.originalHeight);
                  }
                  if ((Build.VERSION.SDK_INT >= 18) || (PhotoViewer.this.compressItem.getTag() == null)) {}
                }
                try
                {
                  localObject2 = MediaController.selectCodec("video/avc");
                  if (localObject2 != null) {
                    break label653;
                  }
                  if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("no codec info for video/avc");
                  }
                  PhotoViewer.this.setCompressItemEnabled(false, true);
                }
                catch (Exception localException)
                {
                  for (;;)
                  {
                    Object localObject2;
                    label387:
                    PhotoViewer.this.setCompressItemEnabled(false, true);
                    FileLog.e(localException);
                    continue;
                    if (MediaController.selectColorFormat((MediaCodecInfo)localObject2, "video/avc") == 0)
                    {
                      if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("no color format for video/avc");
                      }
                      PhotoViewer.this.setCompressItemEnabled(false, true);
                    }
                  }
                }
                PhotoViewer.this.qualityChooseView.invalidate();
              }
              for (;;)
              {
                PhotoViewer.this.updateVideoInfo();
                PhotoViewer.this.updateMuteButton();
                break;
                if (((Matrix)localObject1).equals(Matrix.ROTATE_180))
                {
                  PhotoViewer.access$18602(PhotoViewer.this, 180);
                  break label67;
                }
                if (((Matrix)localObject1).equals(Matrix.ROTATE_270))
                {
                  PhotoViewer.access$18602(PhotoViewer.this, 270);
                  break label67;
                }
                PhotoViewer.access$18602(PhotoViewer.this, 0);
                break label67;
                label492:
                if ((PhotoViewer.this.originalWidth > 854) || (PhotoViewer.this.originalHeight > 854))
                {
                  PhotoViewer.access$17302(PhotoViewer.this, 4);
                  break label217;
                }
                if ((PhotoViewer.this.originalWidth > 640) || (PhotoViewer.this.originalHeight > 640))
                {
                  PhotoViewer.access$17302(PhotoViewer.this, 3);
                  break label217;
                }
                if ((PhotoViewer.this.originalWidth > 480) || (PhotoViewer.this.originalHeight > 480))
                {
                  PhotoViewer.access$17302(PhotoViewer.this, 2);
                  break label217;
                }
                PhotoViewer.access$17302(PhotoViewer.this, 1);
                break label217;
                label648:
                bool = false;
                break label251;
                label653:
                localObject1 = ((MediaCodecInfo)localObject2).getName();
                if ((((String)localObject1).equals("OMX.google.h264.encoder")) || (((String)localObject1).equals("OMX.ST.VFM.H264Enc")) || (((String)localObject1).equals("OMX.Exynos.avc.enc")) || (((String)localObject1).equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER")) || (((String)localObject1).equals("OMX.MARVELL.VIDEO.H264ENCODER")) || (((String)localObject1).equals("OMX.k3.video.encoder.avc")) || (((String)localObject1).equals("OMX.TI.DUCATI1.VIDEO.H264E")))
                {
                  if (BuildVars.LOGS_ENABLED)
                  {
                    localObject2 = new java/lang/StringBuilder;
                    ((StringBuilder)localObject2).<init>();
                    FileLog.d("unsupported encoder = " + (String)localObject1);
                  }
                  PhotoViewer.this.setCompressItemEnabled(false, true);
                  break label387;
                }
                PhotoViewer.access$17302(PhotoViewer.this, 0);
              }
            }
          });
        }
        Object localObject8 = localException1;
        if (l1 != 0L)
        {
          localObject4 = localException1;
          PhotoViewer.access$18402(PhotoViewer.this, (int)(((MediaHeaderBox)localObject1).getTimescale() / (l1 / (k - 1))));
        }
        label806:
        Object localObject3;
        for (localObject8 = localException1;; localObject8 = localObject3)
        {
          i++;
          localObject3 = localObject8;
          break;
          label815:
          localObject4 = localObject3;
          PhotoViewer.access$18002(PhotoViewer.this, PhotoViewer.this.audioFramesSize + l1);
        }
      }
    };
    this.currentLoadingVideoRunnable = paramString;
    localDispatchQueue.postRunnable(paramString);
  }
  
  private void redraw(final int paramInt)
  {
    if ((paramInt < 6) && (this.containerView != null))
    {
      this.containerView.invalidate();
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          PhotoViewer.this.redraw(paramInt + 1);
        }
      }, 100L);
    }
  }
  
  private void releasePlayer()
  {
    if (this.videoPlayer != null)
    {
      this.videoPlayer.releasePlayer();
      this.videoPlayer = null;
    }
    toggleMiniProgress(false, false);
    this.pipAvailable = false;
    if (this.pipItem.isEnabled())
    {
      this.pipItem.setEnabled(false);
      this.pipItem.setAlpha(0.5F);
    }
    if (this.keepScreenOnFlagSet) {}
    try
    {
      this.parentActivity.getWindow().clearFlags(128);
      this.keepScreenOnFlagSet = false;
      if (this.aspectRatioFrameLayout != null)
      {
        this.containerView.removeView(this.aspectRatioFrameLayout);
        this.aspectRatioFrameLayout = null;
      }
      if (this.videoTextureView != null) {
        this.videoTextureView = null;
      }
      if (this.isPlaying)
      {
        this.isPlaying = false;
        this.videoPlayButton.setImageResource(NUM);
        AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
      }
      if ((!this.inPreview) && (!this.requestingPreview))
      {
        this.videoPlayerControlFrameLayout.setVisibility(8);
        this.dateTextView.setVisibility(0);
        this.nameTextView.setVisibility(0);
        if (this.allowShare)
        {
          this.shareButton.setVisibility(0);
          this.menuItem.hideSubItem(10);
        }
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  private void removeObservers()
  {
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
  
  private void requestVideoPreview(int paramInt)
  {
    if (this.videoPreviewMessageObject != null) {
      MediaController.getInstance().cancelVideoConvert(this.videoPreviewMessageObject);
    }
    int i;
    if ((this.requestingPreview) && (!this.tryStartRequestPreviewOnFinish))
    {
      i = 1;
      this.requestingPreview = false;
      this.loadInitialVideo = false;
      this.progressView.setVisibility(4);
      if (paramInt != 1) {
        break label490;
      }
      if (this.selectedCompression != this.compressionsCount - 1) {
        break label117;
      }
      this.tryStartRequestPreviewOnFinish = false;
      if (i != 0) {
        break label101;
      }
      preparePlayer(this.currentPlayingVideoFile, false, false);
    }
    for (;;)
    {
      this.containerView.invalidate();
      return;
      i = 0;
      break;
      label101:
      this.progressView.setVisibility(0);
      this.loadInitialVideo = true;
      continue;
      label117:
      this.requestingPreview = true;
      releasePlayer();
      if (this.videoPreviewMessageObject == null)
      {
        localObject = new TLRPC.TL_message();
        ((TLRPC.TL_message)localObject).id = 0;
        ((TLRPC.TL_message)localObject).message = "";
        ((TLRPC.TL_message)localObject).media = new TLRPC.TL_messageMediaEmpty();
        ((TLRPC.TL_message)localObject).action = new TLRPC.TL_messageActionEmpty();
        this.videoPreviewMessageObject = new MessageObject(UserConfig.selectedAccount, (TLRPC.Message)localObject, false);
        this.videoPreviewMessageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), "video_preview.mp4").getAbsolutePath();
        this.videoPreviewMessageObject.videoEditedInfo = new VideoEditedInfo();
        this.videoPreviewMessageObject.videoEditedInfo.rotationValue = this.rotationValue;
        this.videoPreviewMessageObject.videoEditedInfo.originalWidth = this.originalWidth;
        this.videoPreviewMessageObject.videoEditedInfo.originalHeight = this.originalHeight;
        this.videoPreviewMessageObject.videoEditedInfo.framerate = this.videoFramerate;
        this.videoPreviewMessageObject.videoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
      }
      Object localObject = this.videoPreviewMessageObject.videoEditedInfo;
      long l1 = this.startTime;
      ((VideoEditedInfo)localObject).startTime = l1;
      localObject = this.videoPreviewMessageObject.videoEditedInfo;
      long l2 = this.endTime;
      ((VideoEditedInfo)localObject).endTime = l2;
      long l3 = l1;
      if (l1 == -1L) {
        l3 = 0L;
      }
      l1 = l2;
      if (l2 == -1L) {
        l1 = (this.videoDuration * 1000.0F);
      }
      if (l1 - l3 > 5000000L) {
        this.videoPreviewMessageObject.videoEditedInfo.endTime = (5000000L + l3);
      }
      this.videoPreviewMessageObject.videoEditedInfo.bitrate = this.bitrate;
      this.videoPreviewMessageObject.videoEditedInfo.resultWidth = this.resultWidth;
      this.videoPreviewMessageObject.videoEditedInfo.resultHeight = this.resultHeight;
      if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {
        this.tryStartRequestPreviewOnFinish = true;
      }
      this.requestingPreview = true;
      this.progressView.setVisibility(0);
      continue;
      label490:
      this.tryStartRequestPreviewOnFinish = false;
      if (paramInt == 2) {
        preparePlayer(this.currentPlayingVideoFile, false, false);
      }
    }
  }
  
  private void setCompressItemEnabled(boolean paramBoolean1, boolean paramBoolean2)
  {
    float f = 1.0F;
    if (this.compressItem == null) {
      return;
    }
    Object localObject2;
    if (((!paramBoolean1) || (this.compressItem.getTag() == null)) && ((paramBoolean1) || (this.compressItem.getTag() != null)))
    {
      Object localObject1 = this.compressItem;
      if (paramBoolean1)
      {
        localObject2 = Integer.valueOf(1);
        label54:
        ((ImageView)localObject1).setTag(localObject2);
        this.compressItem.setEnabled(paramBoolean1);
        this.compressItem.setClickable(paramBoolean1);
        if (this.compressItemAnimation != null)
        {
          this.compressItemAnimation.cancel();
          this.compressItemAnimation = null;
        }
        if (!paramBoolean2) {
          break label200;
        }
        this.compressItemAnimation = new AnimatorSet();
        localObject1 = this.compressItemAnimation;
        localObject2 = this.compressItem;
        if (!paramBoolean1) {
          break label193;
        }
      }
      label193:
      for (f = 1.0F;; f = 0.5F)
      {
        ((AnimatorSet)localObject1).playTogether(new Animator[] { ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f }) });
        this.compressItemAnimation.setDuration(180L);
        this.compressItemAnimation.setInterpolator(decelerateInterpolator);
        this.compressItemAnimation.start();
        break;
        localObject2 = null;
        break label54;
      }
      label200:
      localObject2 = this.compressItem;
      if (!paramBoolean1) {
        break label219;
      }
    }
    for (;;)
    {
      ((ImageView)localObject2).setAlpha(f);
      break;
      break;
      label219:
      f = 0.5F;
    }
  }
  
  private void setCurrentCaption(MessageObject paramMessageObject, CharSequence paramCharSequence, boolean paramBoolean)
  {
    label91:
    int i;
    if (this.needCaptionLayout)
    {
      if (this.captionTextView.getParent() != this.pickerView)
      {
        this.captionTextView.setBackgroundDrawable(null);
        this.containerView.removeView(this.captionTextView);
        this.pickerView.addView(this.captionTextView, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 76.0F, 48.0F));
      }
      if (!this.isCurrentVideo) {
        break label452;
      }
      this.captionTextView.setMaxLines(1);
      this.captionTextView.setSingleLine(true);
      if (this.captionTextView.getTag() == null) {
        break label472;
      }
      i = 1;
      label104:
      if (TextUtils.isEmpty(paramCharSequence)) {
        break label564;
      }
      Theme.createChatResources(null, true);
      if ((paramMessageObject == null) || (paramMessageObject.messageOwner.entities.isEmpty())) {
        break label478;
      }
      paramCharSequence = SpannableString.valueOf(paramCharSequence.toString());
      paramMessageObject.addEntitiesToText(paramCharSequence, true, false);
      paramMessageObject = Emoji.replaceEmoji(paramCharSequence, this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      this.captionTextView.setTag(paramMessageObject);
      if (this.currentCaptionAnimation != null)
      {
        this.currentCaptionAnimation.cancel();
        this.currentCaptionAnimation = null;
      }
    }
    for (;;)
    {
      try
      {
        this.captionTextView.setText(paramMessageObject);
        this.captionTextView.setTextColor(-1);
        if ((this.isActionBarVisible) && ((this.bottomLayout.getVisibility() == 0) || (this.pickerView.getVisibility() == 0)))
        {
          j = 1;
          if (j == 0) {
            continue;
          }
          this.captionTextView.setVisibility(0);
          if ((!paramBoolean) || (i != 0)) {
            continue;
          }
          this.currentCaptionAnimation = new AnimatorSet();
          this.currentCaptionAnimation.setDuration(200L);
          this.currentCaptionAnimation.setInterpolator(decelerateInterpolator);
          this.currentCaptionAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if (paramAnonymousAnimator.equals(PhotoViewer.this.currentCaptionAnimation)) {
                PhotoViewer.access$16002(PhotoViewer.this, null);
              }
            }
          });
          this.currentCaptionAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[] { AndroidUtilities.dp(5.0F), 0.0F }) });
          this.currentCaptionAnimation.start();
          return;
          if (this.captionTextView.getParent() == this.containerView) {
            break;
          }
          this.captionTextView.setBackgroundColor(NUM);
          this.pickerView.removeView(this.captionTextView);
          this.containerView.addView(this.captionTextView, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
          break;
          label452:
          this.captionTextView.setSingleLine(false);
          this.captionTextView.setMaxLines(10);
          break label91;
          label472:
          i = 0;
          break label104;
          label478:
          paramMessageObject = Emoji.replaceEmoji(new SpannableStringBuilder(paramCharSequence), this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        }
      }
      catch (Exception paramMessageObject)
      {
        FileLog.e(paramMessageObject);
        continue;
        int j = 0;
        continue;
        this.captionTextView.setAlpha(1.0F);
        continue;
        if (this.captionTextView.getVisibility() != 0) {
          continue;
        }
        this.captionTextView.setVisibility(4);
        this.captionTextView.setAlpha(0.0F);
        continue;
      }
      label564:
      if (this.needCaptionLayout)
      {
        this.captionTextView.setText(LocaleController.getString("AddCaption", NUM));
        this.captionTextView.setTag("empty");
        this.captionTextView.setVisibility(0);
        this.captionTextView.setTextColor(-NUM);
      }
      else
      {
        this.captionTextView.setTextColor(-1);
        this.captionTextView.setTag(null);
        if ((paramBoolean) && (i != 0))
        {
          this.currentCaptionAnimation = new AnimatorSet();
          this.currentCaptionAnimation.setDuration(200L);
          this.currentCaptionAnimation.setInterpolator(decelerateInterpolator);
          this.currentCaptionAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationCancel(Animator paramAnonymousAnimator)
            {
              if (paramAnonymousAnimator.equals(PhotoViewer.this.currentCaptionAnimation)) {
                PhotoViewer.access$16002(PhotoViewer.this, null);
              }
            }
            
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if (paramAnonymousAnimator.equals(PhotoViewer.this.currentCaptionAnimation))
              {
                PhotoViewer.this.captionTextView.setVisibility(4);
                PhotoViewer.access$16002(PhotoViewer.this, null);
              }
            }
          });
          this.currentCaptionAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[] { AndroidUtilities.dp(5.0F) }) });
          this.currentCaptionAnimation.start();
        }
        else
        {
          this.captionTextView.setVisibility(4);
        }
      }
    }
  }
  
  private void setImageIndex(int paramInt, boolean paramBoolean)
  {
    if ((this.currentIndex == paramInt) || (this.placeProvider == null)) {}
    for (;;)
    {
      return;
      if ((!paramBoolean) && (this.currentThumb != null))
      {
        this.currentThumb.release();
        this.currentThumb = null;
      }
      this.currentFileNames[0] = getFileName(paramInt);
      this.currentFileNames[1] = getFileName(paramInt + 1);
      this.currentFileNames[2] = getFileName(paramInt - 1);
      this.placeProvider.willSwitchFromPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
      int i = this.currentIndex;
      this.currentIndex = paramInt;
      setIsAboutToSwitchToIndex(this.currentIndex, paramBoolean);
      boolean bool = false;
      int j = 0;
      int k = 0;
      Object localObject1 = null;
      Object localObject2;
      int m;
      if (!this.imagesArr.isEmpty())
      {
        if ((this.currentIndex < 0) || (this.currentIndex >= this.imagesArr.size()))
        {
          closePhoto(false, false);
          continue;
        }
        localObject2 = (MessageObject)this.imagesArr.get(this.currentIndex);
        if ((this.currentMessageObject != null) && (this.currentMessageObject.getId() == ((MessageObject)localObject2).getId()))
        {
          m = 1;
          label209:
          this.currentMessageObject = ((MessageObject)localObject2);
          paramBoolean = ((MessageObject)localObject2).isVideo();
          localObject2 = localObject1;
          label225:
          if (this.currentPlaceObject != null)
          {
            if (this.animationInProgress != 0) {
              break label1056;
            }
            this.currentPlaceObject.imageReceiver.setVisible(true, true);
          }
          label251:
          this.currentPlaceObject = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
          if (this.currentPlaceObject != null)
          {
            if (this.animationInProgress != 0) {
              break label1067;
            }
            this.currentPlaceObject.imageReceiver.setVisible(false, true);
          }
          label302:
          if (m == 0)
          {
            this.draggingDown = false;
            this.translationX = 0.0F;
            this.translationY = 0.0F;
            this.scale = 1.0F;
            this.animateToX = 0.0F;
            this.animateToY = 0.0F;
            this.animateToScale = 1.0F;
            this.animationStartTime = 0L;
            this.imageMoveAnimation = null;
            this.changeModeAnimation = null;
            if (this.aspectRatioFrameLayout != null) {
              this.aspectRatioFrameLayout.setVisibility(4);
            }
            this.pinchStartDistance = 0.0F;
            this.pinchStartScale = 1.0F;
            this.pinchCenterX = 0.0F;
            this.pinchCenterY = 0.0F;
            this.pinchStartX = 0.0F;
            this.pinchStartY = 0.0F;
            this.moveStartX = 0.0F;
            this.moveStartY = 0.0F;
            this.zooming = false;
            this.moving = false;
            this.doubleTap = false;
            this.invalidCoords = false;
            this.canDragDown = true;
            this.changingPage = false;
            this.switchImageAfterAnimation = 0;
            if ((this.imagesArrLocals.isEmpty()) && ((this.currentFileNames[0] == null) || (paramBoolean) || (this.photoProgressViews[0].backgroundState == 0))) {
              break label1078;
            }
          }
        }
      }
      label1056:
      label1067:
      label1078:
      for (bool = true;; bool = false)
      {
        this.canZoom = bool;
        updateMinMax(this.scale);
        releasePlayer();
        if ((paramBoolean) && (localObject2 != null))
        {
          this.isStreaming = false;
          preparePlayer((Uri)localObject2, false, false);
        }
        if (i != -1) {
          break label1084;
        }
        setImages();
        for (paramInt = 0; paramInt < 3; paramInt++) {
          checkProgress(paramInt, false);
        }
        break;
        m = 0;
        break label209;
        if (!this.imagesArrLocations.isEmpty())
        {
          if ((paramInt < 0) || (paramInt >= this.imagesArrLocations.size()))
          {
            closePhoto(false, false);
            break;
          }
          localObject3 = this.currentFileLocation;
          localObject2 = (TLRPC.FileLocation)this.imagesArrLocations.get(paramInt);
          m = j;
          if (localObject3 != null)
          {
            m = j;
            if (localObject2 != null)
            {
              m = j;
              if (((TLRPC.FileLocation)localObject3).local_id == ((TLRPC.FileLocation)localObject2).local_id)
              {
                m = j;
                if (((TLRPC.FileLocation)localObject3).volume_id == ((TLRPC.FileLocation)localObject2).volume_id) {
                  m = 1;
                }
              }
            }
          }
          this.currentFileLocation = ((TLRPC.FileLocation)this.imagesArrLocations.get(paramInt));
          paramBoolean = bool;
          localObject2 = localObject1;
          break label225;
        }
        paramBoolean = bool;
        m = k;
        localObject2 = localObject1;
        if (this.imagesArrLocals.isEmpty()) {
          break label225;
        }
        if ((paramInt < 0) || (paramInt >= this.imagesArrLocals.size()))
        {
          closePhoto(false, false);
          break;
        }
        Object localObject3 = this.imagesArrLocals.get(paramInt);
        if ((localObject3 instanceof TLRPC.BotInlineResult))
        {
          localObject3 = (TLRPC.BotInlineResult)localObject3;
          this.currentBotInlineResult = ((TLRPC.BotInlineResult)localObject3);
          if (((TLRPC.BotInlineResult)localObject3).document != null)
          {
            this.currentPathObject = FileLoader.getPathToAttach(((TLRPC.BotInlineResult)localObject3).document).getAbsolutePath();
            paramBoolean = MessageObject.isVideoDocument(((TLRPC.BotInlineResult)localObject3).document);
            m = k;
            localObject2 = localObject1;
            break label225;
          }
          if (((TLRPC.BotInlineResult)localObject3).photo != null)
          {
            this.currentPathObject = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(((TLRPC.BotInlineResult)localObject3).photo.sizes, AndroidUtilities.getPhotoSize())).getAbsolutePath();
            paramBoolean = bool;
            m = k;
            localObject2 = localObject1;
            break label225;
          }
          paramBoolean = bool;
          m = k;
          localObject2 = localObject1;
          if (!(((TLRPC.BotInlineResult)localObject3).content instanceof TLRPC.TL_webDocument)) {
            break label225;
          }
          this.currentPathObject = ((TLRPC.BotInlineResult)localObject3).content.url;
          paramBoolean = ((TLRPC.BotInlineResult)localObject3).type.equals("video");
          m = k;
          localObject2 = localObject1;
          break label225;
        }
        if ((localObject3 instanceof MediaController.PhotoEntry))
        {
          localObject2 = (MediaController.PhotoEntry)localObject3;
          this.currentPathObject = ((MediaController.PhotoEntry)localObject2).path;
          paramBoolean = ((MediaController.PhotoEntry)localObject2).isVideo;
          localObject2 = Uri.fromFile(new File(((MediaController.PhotoEntry)localObject2).path));
          m = k;
          break label225;
        }
        paramBoolean = bool;
        m = k;
        localObject2 = localObject1;
        if (!(localObject3 instanceof MediaController.SearchImage)) {
          break label225;
        }
        localObject2 = (MediaController.SearchImage)localObject3;
        if (((MediaController.SearchImage)localObject2).document != null)
        {
          this.currentPathObject = FileLoader.getPathToAttach(((MediaController.SearchImage)localObject2).document, true).getAbsolutePath();
          paramBoolean = bool;
          m = k;
          localObject2 = localObject1;
          break label225;
        }
        this.currentPathObject = ((MediaController.SearchImage)localObject2).imageUrl;
        paramBoolean = bool;
        m = k;
        localObject2 = localObject1;
        break label225;
        this.showAfterAnimation = this.currentPlaceObject;
        break label251;
        this.hideAfterAnimation = this.currentPlaceObject;
        break label302;
      }
      label1084:
      checkProgress(0, false);
      if (i > this.currentIndex)
      {
        localObject2 = this.rightImage;
        this.rightImage = this.centerImage;
        this.centerImage = this.leftImage;
        this.leftImage = ((ImageReceiver)localObject2);
        localObject2 = this.photoProgressViews[0];
        this.photoProgressViews[0] = this.photoProgressViews[2];
        this.photoProgressViews[2] = localObject2;
        setIndexToImage(this.leftImage, this.currentIndex - 1);
        checkProgress(1, false);
        checkProgress(2, false);
      }
      else if (i < this.currentIndex)
      {
        localObject2 = this.leftImage;
        this.leftImage = this.centerImage;
        this.centerImage = this.rightImage;
        this.rightImage = ((ImageReceiver)localObject2);
        localObject2 = this.photoProgressViews[0];
        this.photoProgressViews[0] = this.photoProgressViews[1];
        this.photoProgressViews[1] = localObject2;
        setIndexToImage(this.rightImage, this.currentIndex + 1);
        checkProgress(1, false);
        checkProgress(2, false);
      }
    }
  }
  
  private void setImages()
  {
    if (this.animationInProgress == 0)
    {
      setIndexToImage(this.centerImage, this.currentIndex);
      setIndexToImage(this.rightImage, this.currentIndex + 1);
      setIndexToImage(this.leftImage, this.currentIndex - 1);
    }
  }
  
  private void setIndexToImage(ImageReceiver paramImageReceiver, int paramInt)
  {
    paramImageReceiver.setOrientation(0, false);
    Object localObject1;
    int i;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    Object localObject6;
    TLRPC.BotInlineResult localBotInlineResult;
    Object localObject7;
    Object localObject8;
    Object localObject9;
    int j;
    int k;
    Object localObject10;
    boolean bool1;
    boolean bool2;
    Object localObject11;
    Object localObject12;
    Object localObject13;
    if (!this.imagesArrLocals.isEmpty())
    {
      paramImageReceiver.setParentMessageObject(null);
      if ((paramInt >= 0) && (paramInt < this.imagesArrLocals.size()))
      {
        localObject1 = this.imagesArrLocals.get(paramInt);
        i = (int)(AndroidUtilities.getPhotoSize() / AndroidUtilities.density);
        localObject2 = null;
        localObject3 = localObject2;
        if (this.currentThumb != null)
        {
          localObject3 = localObject2;
          if (paramImageReceiver == this.centerImage) {
            localObject3 = this.currentThumb;
          }
        }
        localObject4 = localObject3;
        if (localObject3 == null) {
          localObject4 = this.placeProvider.getThumbForPhoto(null, null, paramInt);
        }
        localObject5 = null;
        localObject6 = null;
        localBotInlineResult = null;
        localObject7 = null;
        localObject8 = null;
        localObject9 = null;
        j = 0;
        k = 0;
        localObject10 = null;
        bool1 = false;
        if ((localObject1 instanceof MediaController.PhotoEntry))
        {
          localObject3 = (MediaController.PhotoEntry)localObject1;
          bool2 = ((MediaController.PhotoEntry)localObject3).isVideo;
          if (!((MediaController.PhotoEntry)localObject3).isVideo) {
            if (((MediaController.PhotoEntry)localObject3).imagePath != null)
            {
              localObject2 = ((MediaController.PhotoEntry)localObject3).imagePath;
              localObject3 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) });
              localObject11 = localObject9;
              paramInt = k;
              localObject12 = localObject8;
              localObject13 = localObject7;
              label230:
              if (localObject13 == null) {
                break label1011;
              }
              if (localObject4 == null) {
                break label999;
              }
              localObject3 = new BitmapDrawable(((ImageReceiver.BitmapHolder)localObject4).bitmap);
              label254:
              if (localObject4 != null) {
                break label1005;
              }
              localObject2 = ((TLRPC.Document)localObject13).thumb.location;
              label269:
              paramImageReceiver.setImage((TLObject)localObject13, null, "d", (Drawable)localObject3, (TLRPC.FileLocation)localObject2, String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }), paramInt, null, 0);
            }
          }
        }
      }
    }
    for (;;)
    {
      return;
      paramImageReceiver.setOrientation(((MediaController.PhotoEntry)localObject3).orientation, false);
      localObject2 = ((MediaController.PhotoEntry)localObject3).path;
      break;
      if (((MediaController.PhotoEntry)localObject3).thumbPath != null)
      {
        localObject2 = ((MediaController.PhotoEntry)localObject3).thumbPath;
        localObject13 = localObject7;
        localObject12 = localObject8;
        paramInt = k;
        localObject11 = localObject9;
        localObject3 = localObject10;
        break label230;
      }
      localObject2 = "vthumb://" + ((MediaController.PhotoEntry)localObject3).imageId + ":" + ((MediaController.PhotoEntry)localObject3).path;
      localObject13 = localObject7;
      localObject12 = localObject8;
      paramInt = k;
      localObject11 = localObject9;
      localObject3 = localObject10;
      break label230;
      if ((localObject1 instanceof TLRPC.BotInlineResult))
      {
        localBotInlineResult = (TLRPC.BotInlineResult)localObject1;
        if ((localBotInlineResult.type.equals("video")) || (MessageObject.isVideoDocument(localBotInlineResult.document)))
        {
          if (localBotInlineResult.document != null)
          {
            localObject11 = localBotInlineResult.document.thumb.location;
            localObject13 = localObject7;
            localObject12 = localObject8;
            paramInt = k;
            localObject3 = localObject10;
            localObject2 = localObject6;
            bool2 = bool1;
            break label230;
          }
          localObject13 = localObject7;
          localObject12 = localObject8;
          paramInt = k;
          localObject11 = localObject9;
          localObject3 = localObject10;
          localObject2 = localObject6;
          bool2 = bool1;
          if (!(localBotInlineResult.thumb instanceof TLRPC.TL_webDocument)) {
            break label230;
          }
          localObject12 = (TLRPC.TL_webDocument)localBotInlineResult.thumb;
          localObject13 = localObject7;
          paramInt = k;
          localObject11 = localObject9;
          localObject3 = localObject10;
          localObject2 = localObject6;
          bool2 = bool1;
          break label230;
        }
        if ((localBotInlineResult.type.equals("gif")) && (localBotInlineResult.document != null))
        {
          localObject13 = localBotInlineResult.document;
          paramInt = localBotInlineResult.document.size;
          localObject3 = "d";
          localObject12 = localObject8;
          localObject11 = localObject9;
          localObject2 = localObject6;
          bool2 = bool1;
          break label230;
        }
        if (localBotInlineResult.photo != null)
        {
          localObject3 = FileLoader.getClosestPhotoSizeWithSize(localBotInlineResult.photo.sizes, AndroidUtilities.getPhotoSize());
          localObject11 = ((TLRPC.PhotoSize)localObject3).location;
          paramInt = ((TLRPC.PhotoSize)localObject3).size;
          localObject3 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) });
          localObject13 = localObject7;
          localObject12 = localObject8;
          localObject2 = localObject6;
          bool2 = bool1;
          break label230;
        }
        localObject13 = localObject7;
        localObject12 = localObject8;
        paramInt = k;
        localObject11 = localObject9;
        localObject3 = localObject10;
        localObject2 = localObject6;
        bool2 = bool1;
        if (!(localBotInlineResult.content instanceof TLRPC.TL_webDocument)) {
          break label230;
        }
        if (localBotInlineResult.type.equals("gif")) {}
        for (localObject3 = "d";; localObject3 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }))
        {
          localObject12 = (TLRPC.TL_webDocument)localBotInlineResult.content;
          localObject13 = localObject7;
          paramInt = k;
          localObject11 = localObject9;
          localObject2 = localObject6;
          bool2 = bool1;
          break;
        }
      }
      localObject13 = localObject7;
      localObject12 = localObject8;
      paramInt = k;
      localObject11 = localObject9;
      localObject3 = localObject10;
      localObject2 = localObject6;
      bool2 = bool1;
      if (!(localObject1 instanceof MediaController.SearchImage)) {
        break label230;
      }
      localObject3 = (MediaController.SearchImage)localObject1;
      if (((MediaController.SearchImage)localObject3).imagePath != null)
      {
        localObject2 = ((MediaController.SearchImage)localObject3).imagePath;
        paramInt = j;
        localObject13 = localBotInlineResult;
      }
      for (;;)
      {
        localObject3 = "d";
        localObject12 = localObject8;
        localObject11 = localObject9;
        bool2 = bool1;
        break;
        if (((MediaController.SearchImage)localObject3).document != null)
        {
          localObject13 = ((MediaController.SearchImage)localObject3).document;
          paramInt = ((MediaController.SearchImage)localObject3).document.size;
          localObject2 = localObject5;
        }
        else
        {
          localObject2 = ((MediaController.SearchImage)localObject3).imageUrl;
          paramInt = ((MediaController.SearchImage)localObject3).size;
          localObject13 = localBotInlineResult;
        }
      }
      label999:
      localObject3 = null;
      break label254;
      label1005:
      localObject2 = null;
      break label269;
      label1011:
      if (localObject11 != null)
      {
        if (localObject4 != null) {}
        for (localObject2 = new BitmapDrawable(((ImageReceiver.BitmapHolder)localObject4).bitmap);; localObject2 = null)
        {
          paramImageReceiver.setImage((TLObject)localObject11, null, (String)localObject3, (Drawable)localObject2, null, String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }), paramInt, null, 0);
          break;
        }
      }
      if (localObject12 != null)
      {
        if (localObject4 != null) {
          localObject2 = new BitmapDrawable(((ImageReceiver.BitmapHolder)localObject4).bitmap);
        }
        for (;;)
        {
          paramImageReceiver.setImage((TLObject)localObject12, (String)localObject3, (Drawable)localObject2, null, paramInt);
          break;
          if ((bool2) && (this.parentActivity != null)) {
            localObject2 = this.parentActivity.getResources().getDrawable(NUM);
          } else {
            localObject2 = null;
          }
        }
      }
      if (localObject4 != null) {
        localObject13 = new BitmapDrawable(((ImageReceiver.BitmapHolder)localObject4).bitmap);
      }
      for (;;)
      {
        paramImageReceiver.setImage((String)localObject2, (String)localObject3, (Drawable)localObject13, null, paramInt);
        break;
        if ((bool2) && (this.parentActivity != null)) {
          localObject13 = this.parentActivity.getResources().getDrawable(NUM);
        } else {
          localObject13 = null;
        }
      }
      paramImageReceiver.setImageBitmap((Bitmap)null);
      continue;
      localObject11 = new int[1];
      localObject4 = getFileLocation(paramInt, (int[])localObject11);
      if (localObject4 != null)
      {
        localObject2 = null;
        if (!this.imagesArr.isEmpty()) {
          localObject2 = (MessageObject)this.imagesArr.get(paramInt);
        }
        paramImageReceiver.setParentMessageObject((MessageObject)localObject2);
        if (localObject2 != null) {
          paramImageReceiver.setShouldGenerateQualityThumb(true);
        }
        if ((localObject2 != null) && (((MessageObject)localObject2).isVideo()))
        {
          paramImageReceiver.setNeedsQualityThumb(true);
          if ((((MessageObject)localObject2).photoThumbs != null) && (!((MessageObject)localObject2).photoThumbs.isEmpty()))
          {
            localObject13 = null;
            localObject3 = localObject13;
            if (this.currentThumb != null)
            {
              localObject3 = localObject13;
              if (paramImageReceiver == this.centerImage) {
                localObject3 = this.currentThumb;
              }
            }
            localObject2 = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject2).photoThumbs, 100);
            if (localObject3 != null) {}
            for (localObject3 = new BitmapDrawable(((ImageReceiver.BitmapHolder)localObject3).bitmap);; localObject3 = null)
            {
              paramImageReceiver.setImage(null, null, null, (Drawable)localObject3, ((TLRPC.PhotoSize)localObject2).location, "b", 0, null, 1);
              break;
            }
          }
          paramImageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(NUM));
        }
        else if ((localObject2 != null) && (this.currentAnimation != null))
        {
          paramImageReceiver.setImageBitmap(this.currentAnimation);
          this.currentAnimation.setSecondParentView(this.containerView);
        }
        else
        {
          paramImageReceiver.setNeedsQualityThumb(true);
          localObject3 = null;
          localObject13 = localObject3;
          if (this.currentThumb != null)
          {
            localObject13 = localObject3;
            if (paramImageReceiver == this.centerImage) {
              localObject13 = this.currentThumb;
            }
          }
          if (localObject11[0] == 0) {
            localObject11[0] = -1;
          }
          if (localObject2 != null)
          {
            localObject3 = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject2).photoThumbs, 100);
            label1554:
            localObject12 = localObject3;
            if (localObject3 != null)
            {
              localObject12 = localObject3;
              if (((TLRPC.PhotoSize)localObject3).location == localObject4) {
                localObject12 = null;
              }
            }
            if (((localObject2 == null) || (!((MessageObject)localObject2).isWebpage())) && (this.avatarsDialogId == 0) && (!this.isEvent)) {
              break label1680;
            }
            paramInt = 1;
            label1609:
            if (localObject13 == null) {
              break label1685;
            }
            localObject3 = new BitmapDrawable(((ImageReceiver.BitmapHolder)localObject13).bitmap);
            label1628:
            if (localObject12 == null) {
              break label1691;
            }
            localObject2 = ((TLRPC.PhotoSize)localObject12).location;
            label1640:
            k = localObject11[0];
            if (paramInt == 0) {
              break label1697;
            }
          }
          label1680:
          label1685:
          label1691:
          label1697:
          for (paramInt = 1;; paramInt = 0)
          {
            paramImageReceiver.setImage((TLObject)localObject4, null, null, (Drawable)localObject3, (TLRPC.FileLocation)localObject2, "b", k, null, paramInt);
            break;
            localObject3 = null;
            break label1554;
            paramInt = 0;
            break label1609;
            localObject3 = null;
            break label1628;
            localObject2 = null;
            break label1640;
          }
        }
      }
      else
      {
        paramImageReceiver.setNeedsQualityThumb(true);
        paramImageReceiver.setParentMessageObject(null);
        if (localObject11[0] == 0) {
          paramImageReceiver.setImageBitmap((Bitmap)null);
        } else {
          paramImageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(NUM));
        }
      }
    }
  }
  
  private void setIsAboutToSwitchToIndex(int paramInt, boolean paramBoolean)
  {
    if ((!paramBoolean) && (this.switchingToIndex == paramInt)) {}
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    do
    {
      return;
      this.switchingToIndex = paramInt;
      bool1 = false;
      bool2 = false;
      localObject1 = null;
      localObject2 = null;
      localObject3 = getFileName(paramInt);
      localObject4 = null;
      if (this.imagesArr.isEmpty()) {
        break;
      }
    } while ((this.switchingToIndex < 0) || (this.switchingToIndex >= this.imagesArr.size()));
    Object localObject5 = (MessageObject)this.imagesArr.get(this.switchingToIndex);
    boolean bool2 = ((MessageObject)localObject5).isVideo();
    boolean bool1 = ((MessageObject)localObject5).isInvoice();
    if (bool1)
    {
      this.masksItem.setVisibility(8);
      this.menuItem.hideSubItem(6);
      this.menuItem.hideSubItem(11);
      localObject2 = ((MessageObject)localObject5).messageOwner.media.description;
      this.allowShare = false;
      this.bottomLayout.setTranslationY(AndroidUtilities.dp(48.0F));
      this.captionTextView.setTranslationY(AndroidUtilities.dp(48.0F));
      if (this.currentAnimation == null) {
        break label739;
      }
      this.menuItem.hideSubItem(1);
      this.menuItem.hideSubItem(10);
      if (!((MessageObject)localObject5).canDeleteMessage(null)) {
        this.menuItem.setVisibility(8);
      }
      this.allowShare = true;
      this.shareButton.setVisibility(0);
      this.actionBar.setTitle(LocaleController.getString("AttachGif", NUM));
      label247:
      this.groupedPhotosListView.fillList();
      label254:
      if (paramBoolean) {
        break label2869;
      }
    }
    label297:
    label328:
    label385:
    label403:
    label421:
    label541:
    label553:
    label586:
    label709:
    label727:
    label739:
    label819:
    label938:
    label1109:
    label1164:
    label1365:
    label1383:
    label1484:
    label1549:
    label1642:
    label1665:
    label1696:
    label1747:
    label1795:
    label1801:
    label1903:
    label1933:
    label1990:
    label2023:
    label2105:
    label2127:
    label2148:
    label2245:
    label2309:
    label2526:
    label2531:
    label2667:
    label2672:
    label2741:
    label2760:
    label2808:
    label2845:
    label2851:
    label2857:
    label2863:
    label2869:
    for (paramBoolean = true;; paramBoolean = false)
    {
      setCurrentCaption((MessageObject)localObject5, (CharSequence)localObject2, paramBoolean);
      break;
      localObject2 = this.masksItem;
      long l;
      if ((((MessageObject)localObject5).hasPhotoStickers()) && ((int)((MessageObject)localObject5).getDialogId() != 0))
      {
        paramInt = 0;
        ((ActionBarMenuItem)localObject2).setVisibility(paramInt);
        if ((!((MessageObject)localObject5).canDeleteMessage(null)) || (this.slideshowMessageId != 0)) {
          break label541;
        }
        this.menuItem.showSubItem(6);
        if (!bool2) {
          break label553;
        }
        this.menuItem.showSubItem(11);
        if (this.pipItem.getVisibility() != 0) {
          this.pipItem.setVisibility(0);
        }
        if (!this.pipAvailable)
        {
          this.pipItem.setEnabled(false);
          this.pipItem.setAlpha(0.5F);
        }
        if (this.nameOverride == null) {
          break label586;
        }
        this.nameTextView.setText(this.nameOverride);
        if (this.dateOverride == 0) {
          break label709;
        }
        l = this.dateOverride * 1000L;
        localObject2 = LocaleController.formatString("formatDateAtTime", NUM, new Object[] { LocaleController.getInstance().formatterYear.format(new Date(l)), LocaleController.getInstance().formatterDay.format(new Date(l)) });
        if ((localObject3 == null) || (!bool2)) {
          break label727;
        }
        this.dateTextView.setText(String.format("%s (%s)", new Object[] { localObject2, AndroidUtilities.formatFileSize(((MessageObject)localObject5).getDocument().size) }));
      }
      for (;;)
      {
        localObject2 = ((MessageObject)localObject5).caption;
        break;
        paramInt = 8;
        break label297;
        this.menuItem.hideSubItem(6);
        break label328;
        this.menuItem.hideSubItem(11);
        if (this.pipItem.getVisibility() == 8) {
          break label385;
        }
        this.pipItem.setVisibility(8);
        break label385;
        if (((MessageObject)localObject5).isFromUser())
        {
          localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((MessageObject)localObject5).messageOwner.from_id));
          if (localObject2 != null)
          {
            this.nameTextView.setText(UserObject.getUserName((TLRPC.User)localObject2));
            break label403;
          }
          this.nameTextView.setText("");
          break label403;
        }
        localObject2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(((MessageObject)localObject5).messageOwner.to_id.channel_id));
        if (localObject2 != null)
        {
          this.nameTextView.setText(((TLRPC.Chat)localObject2).title);
          break label403;
        }
        this.nameTextView.setText("");
        break label403;
        l = ((MessageObject)localObject5).messageOwner.date * 1000L;
        break label421;
        this.dateTextView.setText((CharSequence)localObject2);
      }
      int i;
      int j;
      if ((this.totalImagesCount + this.totalImagesCountMerge != 0) && (!this.needSearchImageInArr)) {
        if (this.opennedFromMedia) {
          if ((this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge) && (!this.loadingMoreImages) && (this.switchingToIndex > this.imagesArr.size() - 5))
          {
            if (this.imagesArr.isEmpty())
            {
              paramInt = 0;
              i = 0;
              j = paramInt;
              k = i;
              if (this.endReached[0] != 0)
              {
                j = paramInt;
                k = i;
                if (this.mergeDialogId != 0L)
                {
                  i = 1;
                  j = paramInt;
                  k = i;
                  if (!this.imagesArr.isEmpty())
                  {
                    j = paramInt;
                    k = i;
                    if (((MessageObject)this.imagesArr.get(this.imagesArr.size() - 1)).getDialogId() != this.mergeDialogId)
                    {
                      j = 0;
                      k = i;
                    }
                  }
                }
              }
              localObject1 = DataQuery.getInstance(this.currentAccount);
              if (k != 0) {
                break label1109;
              }
              l = this.currentDialogId;
              ((DataQuery)localObject1).loadMedia(l, 80, j, 0, true, this.classGuid);
              this.loadingMoreImages = true;
            }
          }
          else {
            this.actionBar.setTitle(LocaleController.formatString("Of", NUM, new Object[] { Integer.valueOf(this.switchingToIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
          }
        }
      }
      for (;;)
      {
        if ((int)this.currentDialogId == 0) {
          this.sendItem.setVisibility(8);
        }
        if ((((MessageObject)localObject5).messageOwner.ttl == 0) || (((MessageObject)localObject5).messageOwner.ttl >= 3600)) {
          break label1484;
        }
        this.allowShare = false;
        this.menuItem.hideSubItem(1);
        this.shareButton.setVisibility(8);
        this.menuItem.hideSubItem(10);
        break;
        paramInt = ((MessageObject)this.imagesArr.get(this.imagesArr.size() - 1)).getId();
        break label819;
        l = this.mergeDialogId;
        break label938;
        if ((this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge) && (!this.loadingMoreImages) && (this.switchingToIndex < 5))
        {
          if (!this.imagesArr.isEmpty()) {
            break label1365;
          }
          paramInt = 0;
          i = 0;
          j = paramInt;
          k = i;
          if (this.endReached[0] != 0)
          {
            j = paramInt;
            k = i;
            if (this.mergeDialogId != 0L)
            {
              i = 1;
              j = paramInt;
              k = i;
              if (!this.imagesArr.isEmpty())
              {
                j = paramInt;
                k = i;
                if (((MessageObject)this.imagesArr.get(0)).getDialogId() != this.mergeDialogId)
                {
                  j = 0;
                  k = i;
                }
              }
            }
          }
          localObject1 = DataQuery.getInstance(this.currentAccount);
          if (k != 0) {
            break label1383;
          }
        }
        for (l = this.currentDialogId;; l = this.mergeDialogId)
        {
          ((DataQuery)localObject1).loadMedia(l, 80, j, 0, true, this.classGuid);
          this.loadingMoreImages = true;
          this.actionBar.setTitle(LocaleController.formatString("Of", NUM, new Object[] { Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge - this.imagesArr.size() + this.switchingToIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
          break;
          paramInt = ((MessageObject)this.imagesArr.get(0)).getId();
          break label1164;
        }
        if ((this.slideshowMessageId == 0) && ((((MessageObject)localObject5).messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)))
        {
          if (((MessageObject)localObject5).isVideo()) {
            this.actionBar.setTitle(LocaleController.getString("AttachVideo", NUM));
          } else {
            this.actionBar.setTitle(LocaleController.getString("AttachPhoto", NUM));
          }
        }
        else if (bool1) {
          this.actionBar.setTitle(((MessageObject)localObject5).messageOwner.media.title);
        }
      }
      this.allowShare = true;
      this.menuItem.showSubItem(1);
      localObject1 = this.shareButton;
      if (this.videoPlayerControlFrameLayout.getVisibility() != 0) {}
      for (paramInt = 0;; paramInt = 8)
      {
        ((ImageView)localObject1).setVisibility(paramInt);
        if (this.shareButton.getVisibility() != 0) {
          break label1549;
        }
        this.menuItem.hideSubItem(10);
        break;
      }
      this.menuItem.showSubItem(10);
      break label247;
      if (!this.imagesArrLocations.isEmpty())
      {
        if ((paramInt < 0) || (paramInt >= this.imagesArrLocations.size())) {
          break;
        }
        this.nameTextView.setText("");
        this.dateTextView.setText("");
        if ((this.avatarsDialogId == UserConfig.getInstance(this.currentAccount).getClientUserId()) && (!this.avatarsArr.isEmpty()))
        {
          this.menuItem.showSubItem(6);
          if (!this.isEvent) {
            break label1747;
          }
          this.actionBar.setTitle(LocaleController.getString("AttachPhoto", NUM));
          this.menuItem.showSubItem(1);
          this.allowShare = true;
          localObject5 = this.shareButton;
          if (this.videoPlayerControlFrameLayout.getVisibility() == 0) {
            break label1795;
          }
          paramInt = 0;
          ((ImageView)localObject5).setVisibility(paramInt);
          if (this.shareButton.getVisibility() != 0) {
            break label1801;
          }
          this.menuItem.hideSubItem(10);
        }
        for (;;)
        {
          this.groupedPhotosListView.fillList();
          localObject5 = localObject4;
          break;
          this.menuItem.hideSubItem(6);
          break label1642;
          this.actionBar.setTitle(LocaleController.formatString("Of", NUM, new Object[] { Integer.valueOf(this.switchingToIndex + 1), Integer.valueOf(this.imagesArrLocations.size()) }));
          break label1665;
          paramInt = 8;
          break label1696;
          this.menuItem.showSubItem(10);
        }
      }
      localObject5 = localObject4;
      if (this.imagesArrLocals.isEmpty()) {
        break label254;
      }
      if ((paramInt < 0) || (paramInt >= this.imagesArrLocals.size())) {
        break;
      }
      localObject3 = this.imagesArrLocals.get(paramInt);
      int k = 0;
      boolean bool3 = false;
      boolean bool4 = false;
      boolean bool5 = false;
      boolean bool6;
      if ((localObject3 instanceof TLRPC.BotInlineResult))
      {
        localObject2 = (TLRPC.BotInlineResult)localObject3;
        this.currentBotInlineResult = ((TLRPC.BotInlineResult)localObject2);
        if (((TLRPC.BotInlineResult)localObject2).document != null)
        {
          bool2 = MessageObject.isVideoDocument(((TLRPC.BotInlineResult)localObject2).document);
          this.pickerView.setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
          paramInt = k;
          bool6 = bool2;
          bool1 = bool5;
          localObject2 = localObject1;
          if (this.bottomLayout.getVisibility() != 8) {
            this.bottomLayout.setVisibility(8);
          }
          this.bottomLayout.setTag(null);
          if (!this.fromCamera) {
            break label2760;
          }
          if (!bool6) {
            break label2741;
          }
          this.actionBar.setTitle(LocaleController.getString("AttachVideo", NUM));
          if (this.parentChatActivity != null)
          {
            localObject5 = this.parentChatActivity.getCurrentChat();
            if (localObject5 == null) {
              break label2808;
            }
            this.actionBar.setTitle(((TLRPC.Chat)localObject5).title);
          }
          if ((this.sendPhotoType == 0) || ((this.sendPhotoType == 2) && (this.imagesArrLocals.size() > 1))) {
            this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.switchingToIndex), false);
          }
          updateCaptionTextForCurrentPhoto(localObject3);
          localObject5 = new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY);
          localObject3 = this.timeItem;
          if (paramInt == 0) {
            break label2845;
          }
          localObject1 = localObject5;
          ((ImageView)localObject3).setColorFilter((ColorFilter)localObject1);
          localObject3 = this.paintItem;
          if (!bool4) {
            break label2851;
          }
          localObject1 = localObject5;
          ((ImageView)localObject3).setColorFilter((ColorFilter)localObject1);
          localObject3 = this.cropItem;
          if (!bool1) {
            break label2857;
          }
          localObject1 = localObject5;
          ((ImageView)localObject3).setColorFilter((ColorFilter)localObject1);
          localObject1 = this.tuneItem;
          if (!bool3) {
            break label2863;
          }
        }
      }
      for (;;)
      {
        ((ImageView)localObject1).setColorFilter((ColorFilter)localObject5);
        localObject5 = localObject4;
        break;
        if (!(((TLRPC.BotInlineResult)localObject2).content instanceof TLRPC.TL_webDocument)) {
          break label1903;
        }
        bool2 = ((TLRPC.BotInlineResult)localObject2).type.equals("video");
        break label1903;
        localObject2 = null;
        j = 0;
        if ((localObject3 instanceof MediaController.PhotoEntry))
        {
          localObject5 = (MediaController.PhotoEntry)localObject3;
          localObject2 = ((MediaController.PhotoEntry)localObject5).path;
          bool2 = ((MediaController.PhotoEntry)localObject5).isVideo;
          paramInt = j;
          if (!bool2) {
            break label2531;
          }
          this.muteItem.setVisibility(0);
          this.compressItem.setVisibility(0);
          this.isCurrentVideo = true;
          bool1 = false;
          if ((localObject3 instanceof MediaController.PhotoEntry))
          {
            localObject5 = (MediaController.PhotoEntry)localObject3;
            if ((((MediaController.PhotoEntry)localObject5).editedInfo == null) || (!((MediaController.PhotoEntry)localObject5).editedInfo.muted)) {
              break label2526;
            }
            bool1 = true;
          }
          processOpenVideo((String)localObject2, bool1);
          this.videoTimelineView.setVisibility(0);
          this.paintItem.setVisibility(8);
          this.cropItem.setVisibility(8);
          this.tuneItem.setVisibility(8);
          if (!(localObject3 instanceof MediaController.PhotoEntry)) {
            break label2672;
          }
          localObject5 = (MediaController.PhotoEntry)localObject3;
          if ((((MediaController.PhotoEntry)localObject5).bucketId != 0) || (((MediaController.PhotoEntry)localObject5).dateTaken != 0L) || (this.imagesArrLocals.size() != 1)) {
            break label2667;
          }
        }
        for (bool1 = true;; bool1 = false)
        {
          this.fromCamera = bool1;
          localObject2 = ((MediaController.PhotoEntry)localObject5).caption;
          paramInt = ((MediaController.PhotoEntry)localObject5).ttl;
          bool3 = ((MediaController.PhotoEntry)localObject5).isFiltered;
          bool4 = ((MediaController.PhotoEntry)localObject5).isPainted;
          bool1 = ((MediaController.PhotoEntry)localObject5).isCropped;
          bool6 = bool2;
          break;
          paramInt = j;
          bool2 = bool1;
          if (!(localObject3 instanceof MediaController.SearchImage)) {
            break label2245;
          }
          MediaController.SearchImage localSearchImage = (MediaController.SearchImage)localObject3;
          if (localSearchImage.document != null) {}
          for (localObject5 = FileLoader.getPathToAttach(localSearchImage.document, true).getAbsolutePath();; localObject5 = localSearchImage.imageUrl)
          {
            paramInt = j;
            bool2 = bool1;
            localObject2 = localObject5;
            if (localSearchImage.type != 1) {
              break;
            }
            paramInt = 1;
            bool2 = bool1;
            localObject2 = localObject5;
            break;
          }
          bool1 = false;
          break label2309;
          this.videoTimelineView.setVisibility(8);
          this.muteItem.setVisibility(8);
          this.isCurrentVideo = false;
          this.compressItem.setVisibility(8);
          if (paramInt != 0)
          {
            this.pickerView.setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
            this.paintItem.setVisibility(8);
            this.cropItem.setVisibility(8);
            this.tuneItem.setVisibility(8);
          }
          for (;;)
          {
            this.actionBar.setSubtitle(null);
            break;
            if (this.sendPhotoType != 1) {
              this.pickerView.setPadding(0, 0, 0, 0);
            }
            this.paintItem.setVisibility(0);
            this.cropItem.setVisibility(0);
            this.tuneItem.setVisibility(0);
          }
        }
        localObject2 = localObject1;
        bool1 = bool5;
        bool6 = bool2;
        paramInt = k;
        if (!(localObject3 instanceof MediaController.SearchImage)) {
          break label1933;
        }
        localObject5 = (MediaController.SearchImage)localObject3;
        localObject2 = ((MediaController.SearchImage)localObject5).caption;
        paramInt = ((MediaController.SearchImage)localObject5).ttl;
        bool3 = ((MediaController.SearchImage)localObject5).isFiltered;
        bool4 = ((MediaController.SearchImage)localObject5).isPainted;
        bool1 = ((MediaController.SearchImage)localObject5).isCropped;
        bool6 = bool2;
        break label1933;
        this.actionBar.setTitle(LocaleController.getString("AttachPhoto", NUM));
        break label1990;
        this.actionBar.setTitle(LocaleController.formatString("Of", NUM, new Object[] { Integer.valueOf(this.switchingToIndex + 1), Integer.valueOf(this.imagesArrLocals.size()) }));
        break label1990;
        localObject5 = this.parentChatActivity.getCurrentUser();
        if (localObject5 == null) {
          break label2023;
        }
        this.actionBar.setTitle(ContactsController.formatName(((TLRPC.User)localObject5).first_name, ((TLRPC.User)localObject5).last_name));
        break label2023;
        localObject1 = null;
        break label2105;
        localObject1 = null;
        break label2127;
        localObject1 = null;
        break label2148;
        localObject5 = null;
      }
    }
  }
  
  private void setPhotoChecked()
  {
    int j;
    if (this.placeProvider != null)
    {
      int i = this.placeProvider.setPhotoChecked(this.currentIndex, getCurrentVideoEditedInfo());
      boolean bool = this.placeProvider.isPhotoChecked(this.currentIndex);
      this.checkImageView.setChecked(bool, true);
      if (i >= 0)
      {
        j = i;
        if (this.placeProvider.allowGroupPhotos()) {
          j = i + 1;
        }
        if (!bool) {
          break label95;
        }
        this.selectedPhotosAdapter.notifyItemInserted(j);
        this.selectedPhotosListView.smoothScrollToPosition(j);
      }
    }
    for (;;)
    {
      updateSelectedCount();
      return;
      label95:
      this.selectedPhotosAdapter.notifyItemRemoved(j);
    }
  }
  
  private void setScaleToFill()
  {
    float f1 = this.centerImage.getBitmapWidth();
    float f2 = getContainerViewWidth();
    float f3 = this.centerImage.getBitmapHeight();
    float f4 = getContainerViewHeight();
    float f5 = Math.min(f4 / f3, f2 / f1);
    f1 = (int)(f1 * f5);
    f5 = (int)(f3 * f5);
    this.scale = Math.max(f2 / f1, f4 / f5);
    updateMinMax(this.scale);
  }
  
  private void showDownloadAlert()
  {
    int i = 0;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.parentActivity);
    localBuilder.setTitle(LocaleController.getString("AppName", NUM));
    localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
    int j = i;
    if (this.currentMessageObject != null)
    {
      j = i;
      if (this.currentMessageObject.isVideo())
      {
        j = i;
        if (FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingFile(this.currentFileNames[0])) {
          j = 1;
        }
      }
    }
    if (j != 0) {
      localBuilder.setMessage(LocaleController.getString("PleaseStreamDownload", NUM));
    }
    for (;;)
    {
      showAlertDialog(localBuilder);
      return;
      localBuilder.setMessage(LocaleController.getString("PleaseDownload", NUM));
    }
  }
  
  private void showHint(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((this.containerView == null) || ((paramBoolean1) && (this.hintTextView == null))) {}
    for (;;)
    {
      return;
      if (this.hintTextView == null)
      {
        this.hintTextView = new TextView(this.containerView.getContext());
        this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0F), Theme.getColor("chat_gifSaveHintBackground")));
        this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.hintTextView.setTextSize(1, 14.0F);
        this.hintTextView.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(7.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(7.0F));
        this.hintTextView.setGravity(16);
        this.hintTextView.setAlpha(0.0F);
        this.containerView.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 5.0F, 0.0F, 5.0F, 3.0F));
      }
      if (paramBoolean1)
      {
        if (this.hintAnimation != null)
        {
          this.hintAnimation.cancel();
          this.hintAnimation = null;
        }
        AndroidUtilities.cancelRunOnUIThread(this.hintHideRunnable);
        this.hintHideRunnable = null;
        hideHint();
      }
      else
      {
        TextView localTextView = this.hintTextView;
        Object localObject;
        if (paramBoolean2)
        {
          localObject = LocaleController.getString("GroupPhotosHelp", NUM);
          label231:
          localTextView.setText((CharSequence)localObject);
          if (this.hintHideRunnable == null) {
            break label397;
          }
          if (this.hintAnimation == null) {
            break label363;
          }
          this.hintAnimation.cancel();
          this.hintAnimation = null;
        }
        label363:
        label397:
        while (this.hintAnimation == null)
        {
          this.hintTextView.setVisibility(0);
          this.hintAnimation = new AnimatorSet();
          this.hintAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[] { 1.0F }) });
          this.hintAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationCancel(Animator paramAnonymousAnimator)
            {
              if (paramAnonymousAnimator.equals(PhotoViewer.this.hintAnimation)) {
                PhotoViewer.access$17002(PhotoViewer.this, null);
              }
            }
            
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if (paramAnonymousAnimator.equals(PhotoViewer.this.hintAnimation))
              {
                PhotoViewer.access$17002(PhotoViewer.this, null);
                AndroidUtilities.runOnUIThread(PhotoViewer.access$17102(PhotoViewer.this, new Runnable()
                {
                  public void run()
                  {
                    PhotoViewer.this.hideHint();
                  }
                }), 2000L);
              }
            }
          });
          this.hintAnimation.setDuration(300L);
          this.hintAnimation.start();
          break;
          localObject = LocaleController.getString("SinglePhotosHelp", NUM);
          break label231;
          AndroidUtilities.cancelRunOnUIThread(this.hintHideRunnable);
          localObject = new Runnable()
          {
            public void run()
            {
              PhotoViewer.this.hideHint();
            }
          };
          this.hintHideRunnable = ((Runnable)localObject);
          AndroidUtilities.runOnUIThread((Runnable)localObject, 2000L);
          break;
        }
      }
    }
  }
  
  private void showQualityView(final boolean paramBoolean)
  {
    if (paramBoolean) {
      this.previousCompression = this.selectedCompression;
    }
    if (this.qualityChooseViewAnimation != null) {
      this.qualityChooseViewAnimation.cancel();
    }
    this.qualityChooseViewAnimation = new AnimatorSet();
    if (paramBoolean)
    {
      this.qualityChooseView.setTag(Integer.valueOf(1));
      this.qualityChooseViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(152.0F) }), ObjectAnimator.ofFloat(this.pickerViewSendButton, "translationY", new float[] { 0.0F, AndroidUtilities.dp(152.0F) }), ObjectAnimator.ofFloat(this.bottomLayout, "translationY", new float[] { -AndroidUtilities.dp(48.0F), AndroidUtilities.dp(104.0F) }) });
    }
    for (;;)
    {
      this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          PhotoViewer.access$17602(PhotoViewer.this, null);
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (!paramAnonymousAnimator.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
            return;
          }
          PhotoViewer.access$17602(PhotoViewer.this, new AnimatorSet());
          if (paramBoolean)
          {
            PhotoViewer.this.qualityChooseView.setVisibility(0);
            PhotoViewer.this.qualityPicker.setVisibility(0);
            PhotoViewer.this.qualityChooseViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this.qualityChooseView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.qualityPicker, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, "translationY", new float[] { -AndroidUtilities.dp(48.0F) }) });
          }
          for (;;)
          {
            PhotoViewer.this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymous2Animator)
              {
                if (paramAnonymous2Animator.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                  PhotoViewer.access$17602(PhotoViewer.this, null);
                }
              }
            });
            PhotoViewer.this.qualityChooseViewAnimation.setDuration(200L);
            PhotoViewer.this.qualityChooseViewAnimation.setInterpolator(new AccelerateInterpolator());
            PhotoViewer.this.qualityChooseViewAnimation.start();
            break;
            PhotoViewer.this.qualityChooseView.setVisibility(4);
            PhotoViewer.this.qualityPicker.setVisibility(4);
            PhotoViewer.this.qualityChooseViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, "translationY", new float[] { -AndroidUtilities.dp(48.0F) }) });
          }
        }
      });
      this.qualityChooseViewAnimation.setDuration(200L);
      this.qualityChooseViewAnimation.setInterpolator(new DecelerateInterpolator());
      this.qualityChooseViewAnimation.start();
      return;
      this.qualityChooseView.setTag(null);
      this.qualityChooseViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.qualityChooseView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(166.0F) }), ObjectAnimator.ofFloat(this.qualityPicker, "translationY", new float[] { 0.0F, AndroidUtilities.dp(166.0F) }), ObjectAnimator.ofFloat(this.bottomLayout, "translationY", new float[] { -AndroidUtilities.dp(48.0F), AndroidUtilities.dp(118.0F) }) });
    }
  }
  
  private void switchToEditMode(final int paramInt)
  {
    if ((this.currentEditMode == paramInt) || (this.centerImage.getBitmap() == null) || (this.changeModeAnimation != null) || (this.imageMoveAnimation != null) || (this.photoProgressViews[0].backgroundState != -1) || (this.captionEditText.getTag() != null)) {}
    for (;;)
    {
      return;
      int i;
      int j;
      if (paramInt == 0)
      {
        float f1;
        float f2;
        float f3;
        float f4;
        if (this.centerImage.getBitmap() != null)
        {
          i = this.centerImage.getBitmapWidth();
          j = this.centerImage.getBitmapHeight();
          f1 = getContainerViewWidth() / i;
          f2 = getContainerViewHeight() / j;
          f3 = getContainerViewWidth(0) / i;
          f4 = getContainerViewHeight(0) / j;
          if (f1 > f2)
          {
            f1 = f2;
            label140:
            if (f3 <= f4) {
              break label422;
            }
            f3 = f4;
            label152:
            if ((this.sendPhotoType != 1) || (this.applying)) {
              break label432;
            }
            f2 = Math.min(getContainerViewWidth(), getContainerViewHeight());
            f4 = f2 / i;
            f2 /= j;
            if (f4 <= f2) {
              break label425;
            }
            label203:
            this.scale = (f4 / f1);
            this.animateToScale = (this.scale * f3 / f4);
            label226:
            this.animateToX = 0.0F;
            if (this.currentEditMode != 1) {
              break label444;
            }
            this.animateToY = AndroidUtilities.dp(58.0F);
            label250:
            if (Build.VERSION.SDK_INT >= 21) {
              this.animateToY -= AndroidUtilities.statusBarHeight / 2;
            }
            this.animationStartTime = System.currentTimeMillis();
            this.zoomAnimation = true;
          }
        }
        else
        {
          this.imageMoveAnimation = new AnimatorSet();
          if (this.currentEditMode != 1) {
            break label488;
          }
          this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.editorDoneLayout, "translationY", new float[] { AndroidUtilities.dp(48.0F) }), ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.photoCropView, "alpha", new float[] { 0.0F }) });
        }
        for (;;)
        {
          this.imageMoveAnimation.setDuration(200L);
          this.imageMoveAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if (PhotoViewer.this.currentEditMode == 1)
              {
                PhotoViewer.this.editorDoneLayout.setVisibility(8);
                PhotoViewer.this.photoCropView.setVisibility(8);
              }
              for (;;)
              {
                PhotoViewer.access$14502(PhotoViewer.this, null);
                PhotoViewer.access$11002(PhotoViewer.this, paramInt);
                PhotoViewer.access$14602(PhotoViewer.this, false);
                PhotoViewer.access$14702(PhotoViewer.this, 1.0F);
                PhotoViewer.access$14802(PhotoViewer.this, 0.0F);
                PhotoViewer.access$14902(PhotoViewer.this, 0.0F);
                PhotoViewer.access$6802(PhotoViewer.this, 1.0F);
                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                PhotoViewer.this.containerView.invalidate();
                AnimatorSet localAnimatorSet = new AnimatorSet();
                paramAnonymousAnimator = new ArrayList();
                paramAnonymousAnimator.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, "translationY", new float[] { 0.0F }));
                paramAnonymousAnimator.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, "translationY", new float[] { 0.0F }));
                paramAnonymousAnimator.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, "translationY", new float[] { 0.0F }));
                if (PhotoViewer.this.needCaptionLayout) {
                  paramAnonymousAnimator.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, "translationY", new float[] { 0.0F }));
                }
                if (PhotoViewer.this.sendPhotoType == 0)
                {
                  paramAnonymousAnimator.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, "alpha", new float[] { 1.0F }));
                  paramAnonymousAnimator.add(ObjectAnimator.ofFloat(PhotoViewer.this.photosCounterView, "alpha", new float[] { 1.0F }));
                }
                if (PhotoViewer.this.cameraItem.getTag() != null)
                {
                  PhotoViewer.this.cameraItem.setVisibility(0);
                  paramAnonymousAnimator.add(ObjectAnimator.ofFloat(PhotoViewer.this.cameraItem, "alpha", new float[] { 1.0F }));
                }
                localAnimatorSet.playTogether(paramAnonymousAnimator);
                localAnimatorSet.setDuration(200L);
                localAnimatorSet.addListener(new AnimatorListenerAdapter()
                {
                  public void onAnimationStart(Animator paramAnonymous2Animator)
                  {
                    PhotoViewer.this.pickerView.setVisibility(0);
                    PhotoViewer.this.pickerViewSendButton.setVisibility(0);
                    PhotoViewer.this.actionBar.setVisibility(0);
                    if (PhotoViewer.this.needCaptionLayout)
                    {
                      paramAnonymous2Animator = PhotoViewer.this.captionTextView;
                      if (PhotoViewer.this.captionTextView.getTag() == null) {
                        break label162;
                      }
                    }
                    label162:
                    for (int i = 0;; i = 4)
                    {
                      paramAnonymous2Animator.setVisibility(i);
                      if ((PhotoViewer.this.sendPhotoType == 0) || ((PhotoViewer.this.sendPhotoType == 2) && (PhotoViewer.this.imagesArrLocals.size() > 1)))
                      {
                        PhotoViewer.this.checkImageView.setVisibility(0);
                        PhotoViewer.this.photosCounterView.setVisibility(0);
                      }
                      return;
                    }
                  }
                });
                localAnimatorSet.start();
                return;
                if (PhotoViewer.this.currentEditMode == 2)
                {
                  PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoFilterView);
                  PhotoViewer.access$14302(PhotoViewer.this, null);
                }
                else if (PhotoViewer.this.currentEditMode == 3)
                {
                  PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoPaintView);
                  PhotoViewer.access$14402(PhotoViewer.this, null);
                }
              }
            }
          });
          this.imageMoveAnimation.start();
          break;
          break label140;
          label422:
          break label152;
          label425:
          f4 = f2;
          break label203;
          label432:
          this.animateToScale = (f3 / f1);
          break label226;
          label444:
          if (this.currentEditMode == 2)
          {
            this.animateToY = AndroidUtilities.dp(92.0F);
            break label250;
          }
          if (this.currentEditMode != 3) {
            break label250;
          }
          this.animateToY = AndroidUtilities.dp(44.0F);
          break label250;
          label488:
          if (this.currentEditMode == 2)
          {
            this.photoFilterView.shutdown();
            this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.photoFilterView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(186.0F) }), ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }) });
          }
          else if (this.currentEditMode == 3)
          {
            this.photoPaintView.shutdown();
            this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(126.0F) }), ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), "translationY", new float[] { AndroidUtilities.dp(126.0F) }), ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }) });
          }
        }
      }
      Object localObject1;
      if (paramInt == 1)
      {
        if (this.photoCropView == null)
        {
          this.photoCropView = new PhotoCropView(this.actvityContext);
          this.photoCropView.setVisibility(8);
          this.containerView.addView(this.photoCropView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
          this.photoCropView.setDelegate(new PhotoCropView.PhotoCropViewDelegate()
          {
            public Bitmap getBitmap()
            {
              return PhotoViewer.this.centerImage.getBitmap();
            }
            
            public void needMoveImageTo(float paramAnonymousFloat1, float paramAnonymousFloat2, float paramAnonymousFloat3, boolean paramAnonymousBoolean)
            {
              if (paramAnonymousBoolean) {
                PhotoViewer.this.animateTo(paramAnonymousFloat3, paramAnonymousFloat1, paramAnonymousFloat2, true);
              }
              for (;;)
              {
                return;
                PhotoViewer.access$6902(PhotoViewer.this, paramAnonymousFloat1);
                PhotoViewer.access$7002(PhotoViewer.this, paramAnonymousFloat2);
                PhotoViewer.access$6802(PhotoViewer.this, paramAnonymousFloat3);
                PhotoViewer.this.containerView.invalidate();
              }
            }
            
            public void onChange(boolean paramAnonymousBoolean)
            {
              TextView localTextView = PhotoViewer.this.resetButton;
              if (paramAnonymousBoolean) {}
              for (int i = 8;; i = 0)
              {
                localTextView.setVisibility(i);
                return;
              }
            }
          });
        }
        this.photoCropView.onAppear();
        this.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", NUM));
        this.editorDoneLayout.doneButton.setTextColor(-11420173);
        this.changeModeAnimation = new AnimatorSet();
        localObject1 = new ArrayList();
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.pickerViewSendButton, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[] { 0.0F, -this.actionBar.getHeight() }));
        if (this.needCaptionLayout) {
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
        }
        if (this.sendPhotoType == 0)
        {
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[] { 1.0F, 0.0F }));
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.photosCounterView, "alpha", new float[] { 1.0F, 0.0F }));
        }
        if (this.selectedPhotosListView.getVisibility() == 0) {
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.selectedPhotosListView, "alpha", new float[] { 1.0F, 0.0F }));
        }
        if (this.cameraItem.getTag() != null) {
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.cameraItem, "alpha", new float[] { 1.0F, 0.0F }));
        }
        this.changeModeAnimation.playTogether((Collection)localObject1);
        this.changeModeAnimation.setDuration(200L);
        this.changeModeAnimation.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            PhotoViewer.access$15202(PhotoViewer.this, null);
            PhotoViewer.this.pickerView.setVisibility(8);
            PhotoViewer.this.pickerViewSendButton.setVisibility(8);
            PhotoViewer.this.cameraItem.setVisibility(8);
            PhotoViewer.this.selectedPhotosListView.setVisibility(8);
            PhotoViewer.this.selectedPhotosListView.setAlpha(0.0F);
            PhotoViewer.this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0F));
            PhotoViewer.this.photosCounterView.setRotationX(0.0F);
            PhotoViewer.this.selectedPhotosListView.setEnabled(false);
            PhotoViewer.access$11702(PhotoViewer.this, false);
            if (PhotoViewer.this.needCaptionLayout) {
              PhotoViewer.this.captionTextView.setVisibility(4);
            }
            if ((PhotoViewer.this.sendPhotoType == 0) || ((PhotoViewer.this.sendPhotoType == 2) && (PhotoViewer.this.imagesArrLocals.size() > 1)))
            {
              PhotoViewer.this.checkImageView.setVisibility(8);
              PhotoViewer.this.photosCounterView.setVisibility(8);
            }
            paramAnonymousAnimator = PhotoViewer.this.centerImage.getBitmap();
            boolean bool;
            int j;
            float f1;
            float f2;
            float f3;
            float f4;
            if (paramAnonymousAnimator != null)
            {
              PhotoCropView localPhotoCropView = PhotoViewer.this.photoCropView;
              i = PhotoViewer.this.centerImage.getOrientation();
              if (PhotoViewer.this.sendPhotoType == 1) {
                break label634;
              }
              bool = true;
              localPhotoCropView.setBitmap(paramAnonymousAnimator, i, bool);
              i = PhotoViewer.this.centerImage.getBitmapWidth();
              j = PhotoViewer.this.centerImage.getBitmapHeight();
              f1 = PhotoViewer.this.getContainerViewWidth() / i;
              f2 = PhotoViewer.this.getContainerViewHeight() / j;
              f3 = PhotoViewer.this.getContainerViewWidth(1) / i;
              f4 = PhotoViewer.this.getContainerViewHeight(1) / j;
              if (f1 <= f2) {
                break label640;
              }
              label339:
              if (f3 <= f4) {
                break label647;
              }
              label347:
              if (PhotoViewer.this.sendPhotoType == 1)
              {
                f3 = Math.min(PhotoViewer.this.getContainerViewWidth(1), PhotoViewer.this.getContainerViewHeight(1));
                f4 = f3 / i;
                f3 /= j;
                if (f4 <= f3) {
                  break label654;
                }
              }
              label403:
              PhotoViewer.access$14702(PhotoViewer.this, f4 / f2);
              PhotoViewer.access$14802(PhotoViewer.this, 0.0F);
              paramAnonymousAnimator = PhotoViewer.this;
              j = -AndroidUtilities.dp(56.0F);
              if (Build.VERSION.SDK_INT < 21) {
                break label661;
              }
            }
            label634:
            label640:
            label647:
            label654:
            label661:
            for (int i = AndroidUtilities.statusBarHeight / 2;; i = 0)
            {
              PhotoViewer.access$14902(paramAnonymousAnimator, i + j);
              PhotoViewer.access$15502(PhotoViewer.this, System.currentTimeMillis());
              PhotoViewer.access$15602(PhotoViewer.this, true);
              PhotoViewer.access$14502(PhotoViewer.this, new AnimatorSet());
              PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this.editorDoneLayout, "translationY", new float[] { AndroidUtilities.dp(48.0F), 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, "alpha", new float[] { 0.0F, 1.0F }) });
              PhotoViewer.this.imageMoveAnimation.setDuration(200L);
              PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnonymous2Animator)
                {
                  PhotoViewer.this.photoCropView.onAppeared();
                  PhotoViewer.access$14502(PhotoViewer.this, null);
                  PhotoViewer.access$11002(PhotoViewer.this, PhotoViewer.52.this.val$mode);
                  PhotoViewer.access$14702(PhotoViewer.this, 1.0F);
                  PhotoViewer.access$14802(PhotoViewer.this, 0.0F);
                  PhotoViewer.access$14902(PhotoViewer.this, 0.0F);
                  PhotoViewer.access$6802(PhotoViewer.this, 1.0F);
                  PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                  PhotoViewer.this.containerView.invalidate();
                }
                
                public void onAnimationStart(Animator paramAnonymous2Animator)
                {
                  PhotoViewer.this.editorDoneLayout.setVisibility(0);
                  PhotoViewer.this.photoCropView.setVisibility(0);
                }
              });
              PhotoViewer.this.imageMoveAnimation.start();
              return;
              bool = false;
              break;
              f2 = f1;
              break label339;
              f4 = f3;
              break label347;
              f4 = f3;
              break label403;
            }
          }
        });
        this.changeModeAnimation.start();
      }
      else
      {
        if (paramInt == 2)
        {
          MediaController.PhotoEntry localPhotoEntry;
          Object localObject3;
          Object localObject5;
          Object localObject6;
          if (this.photoFilterView == null)
          {
            localPhotoEntry = null;
            Object localObject2 = null;
            localObject3 = null;
            Object localObject4 = null;
            j = 0;
            i = j;
            localObject5 = localObject3;
            localObject1 = localPhotoEntry;
            if (!this.imagesArrLocals.isEmpty())
            {
              localObject6 = this.imagesArrLocals.get(this.currentIndex);
              if (!(localObject6 instanceof MediaController.PhotoEntry)) {
                break label1658;
              }
              localPhotoEntry = (MediaController.PhotoEntry)localObject6;
              localObject5 = localObject4;
              localObject1 = localObject2;
              if (localPhotoEntry.imagePath == null)
              {
                localObject5 = localPhotoEntry.path;
                localObject1 = localPhotoEntry.savedFilterState;
              }
              i = localPhotoEntry.orientation;
            }
            label1243:
            if (localObject1 != null) {
              break label1702;
            }
            localObject5 = this.centerImage.getBitmap();
            i = this.centerImage.getOrientation();
          }
          for (;;)
          {
            this.photoFilterView = new PhotoFilterView(this.parentActivity, (Bitmap)localObject5, i, (MediaController.SavedFilterState)localObject1);
            this.containerView.addView(this.photoFilterView, LayoutHelper.createFrame(-1, -1.0F));
            this.photoFilterView.getDoneTextView().setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymousView)
              {
                PhotoViewer.this.applyCurrentEditMode();
                PhotoViewer.this.switchToEditMode(0);
              }
            });
            this.photoFilterView.getCancelTextView().setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymousView)
              {
                if (PhotoViewer.this.photoFilterView.hasChanges()) {
                  if (PhotoViewer.this.parentActivity != null) {}
                }
                for (;;)
                {
                  return;
                  paramAnonymousView = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
                  paramAnonymousView.setMessage(LocaleController.getString("DiscardChanges", NUM));
                  paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
                  paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                    {
                      PhotoViewer.this.switchToEditMode(0);
                    }
                  });
                  paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                  PhotoViewer.this.showAlertDialog(paramAnonymousView);
                  continue;
                  PhotoViewer.this.switchToEditMode(0);
                }
              }
            });
            this.photoFilterView.getToolsView().setTranslationY(AndroidUtilities.dp(186.0F));
            this.changeModeAnimation = new AnimatorSet();
            localObject1 = new ArrayList();
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.pickerViewSendButton, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[] { 0.0F, -this.actionBar.getHeight() }));
            if (this.sendPhotoType == 0)
            {
              ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[] { 1.0F, 0.0F }));
              ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.photosCounterView, "alpha", new float[] { 1.0F, 0.0F }));
            }
            if (this.selectedPhotosListView.getVisibility() == 0) {
              ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.selectedPhotosListView, "alpha", new float[] { 1.0F, 0.0F }));
            }
            if (this.cameraItem.getTag() != null) {
              ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.cameraItem, "alpha", new float[] { 1.0F, 0.0F }));
            }
            this.changeModeAnimation.playTogether((Collection)localObject1);
            this.changeModeAnimation.setDuration(200L);
            this.changeModeAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymousAnimator)
              {
                PhotoViewer.access$15202(PhotoViewer.this, null);
                PhotoViewer.this.pickerView.setVisibility(8);
                PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                PhotoViewer.this.actionBar.setVisibility(8);
                PhotoViewer.this.cameraItem.setVisibility(8);
                PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                PhotoViewer.this.selectedPhotosListView.setAlpha(0.0F);
                PhotoViewer.this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0F));
                PhotoViewer.this.photosCounterView.setRotationX(0.0F);
                PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                PhotoViewer.access$11702(PhotoViewer.this, false);
                if (PhotoViewer.this.needCaptionLayout) {
                  PhotoViewer.this.captionTextView.setVisibility(4);
                }
                if ((PhotoViewer.this.sendPhotoType == 0) || ((PhotoViewer.this.sendPhotoType == 2) && (PhotoViewer.this.imagesArrLocals.size() > 1)))
                {
                  PhotoViewer.this.checkImageView.setVisibility(8);
                  PhotoViewer.this.photosCounterView.setVisibility(8);
                }
                int i;
                if (PhotoViewer.this.centerImage.getBitmap() != null)
                {
                  i = PhotoViewer.this.centerImage.getBitmapWidth();
                  j = PhotoViewer.this.centerImage.getBitmapHeight();
                  float f1 = PhotoViewer.this.getContainerViewWidth() / i;
                  float f2 = PhotoViewer.this.getContainerViewHeight() / j;
                  float f3 = PhotoViewer.this.getContainerViewWidth(2) / i;
                  float f4 = PhotoViewer.this.getContainerViewHeight(2) / j;
                  if (f1 <= f2) {
                    break label527;
                  }
                  f1 = f2;
                  if (f3 <= f4) {
                    break label530;
                  }
                  f3 = f4;
                  label321:
                  PhotoViewer.access$14702(PhotoViewer.this, f3 / f1);
                  PhotoViewer.access$14802(PhotoViewer.this, 0.0F);
                  paramAnonymousAnimator = PhotoViewer.this;
                  i = -AndroidUtilities.dp(92.0F);
                  if (Build.VERSION.SDK_INT < 21) {
                    break label533;
                  }
                }
                label527:
                label530:
                label533:
                for (int j = AndroidUtilities.statusBarHeight / 2;; j = 0)
                {
                  PhotoViewer.access$14902(paramAnonymousAnimator, j + i);
                  PhotoViewer.access$15502(PhotoViewer.this, System.currentTimeMillis());
                  PhotoViewer.access$15602(PhotoViewer.this, true);
                  PhotoViewer.access$14502(PhotoViewer.this, new AnimatorSet());
                  PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.photoFilterView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(186.0F), 0.0F }) });
                  PhotoViewer.this.imageMoveAnimation.setDuration(200L);
                  PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter()
                  {
                    public void onAnimationEnd(Animator paramAnonymous2Animator)
                    {
                      PhotoViewer.this.photoFilterView.init();
                      PhotoViewer.access$14502(PhotoViewer.this, null);
                      PhotoViewer.access$11002(PhotoViewer.this, PhotoViewer.55.this.val$mode);
                      PhotoViewer.access$14702(PhotoViewer.this, 1.0F);
                      PhotoViewer.access$14802(PhotoViewer.this, 0.0F);
                      PhotoViewer.access$14902(PhotoViewer.this, 0.0F);
                      PhotoViewer.access$6802(PhotoViewer.this, 1.0F);
                      PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                      PhotoViewer.this.containerView.invalidate();
                    }
                    
                    public void onAnimationStart(Animator paramAnonymous2Animator) {}
                  });
                  PhotoViewer.this.imageMoveAnimation.start();
                  return;
                  break;
                  break label321;
                }
              }
            });
            this.changeModeAnimation.start();
            break;
            label1658:
            i = j;
            localObject5 = localObject3;
            localObject1 = localPhotoEntry;
            if (!(localObject6 instanceof MediaController.SearchImage)) {
              break label1243;
            }
            localObject5 = (MediaController.SearchImage)localObject6;
            localObject1 = ((MediaController.SearchImage)localObject5).savedFilterState;
            localObject5 = ((MediaController.SearchImage)localObject5).imageUrl;
            i = j;
            break label1243;
            label1702:
            localObject5 = BitmapFactory.decodeFile((String)localObject5);
          }
        }
        if (paramInt == 3)
        {
          if (this.photoPaintView == null)
          {
            this.photoPaintView = new PhotoPaintView(this.parentActivity, this.centerImage.getBitmap(), this.centerImage.getOrientation());
            this.containerView.addView(this.photoPaintView, LayoutHelper.createFrame(-1, -1.0F));
            this.photoPaintView.getDoneTextView().setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymousView)
              {
                PhotoViewer.this.applyCurrentEditMode();
                PhotoViewer.this.switchToEditMode(0);
              }
            });
            this.photoPaintView.getCancelTextView().setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymousView)
              {
                PhotoViewer.this.photoPaintView.maybeShowDismissalAlert(PhotoViewer.this, PhotoViewer.this.parentActivity, new Runnable()
                {
                  public void run()
                  {
                    PhotoViewer.this.switchToEditMode(0);
                  }
                });
              }
            });
            this.photoPaintView.getColorPicker().setTranslationY(AndroidUtilities.dp(126.0F));
            this.photoPaintView.getToolsView().setTranslationY(AndroidUtilities.dp(126.0F));
          }
          this.changeModeAnimation = new AnimatorSet();
          localObject1 = new ArrayList();
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.pickerViewSendButton, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[] { 0.0F, -this.actionBar.getHeight() }));
          if (this.needCaptionLayout) {
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
          }
          if (this.sendPhotoType == 0)
          {
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[] { 1.0F, 0.0F }));
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.photosCounterView, "alpha", new float[] { 1.0F, 0.0F }));
          }
          if (this.selectedPhotosListView.getVisibility() == 0) {
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.selectedPhotosListView, "alpha", new float[] { 1.0F, 0.0F }));
          }
          if (this.cameraItem.getTag() != null) {
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.cameraItem, "alpha", new float[] { 1.0F, 0.0F }));
          }
          this.changeModeAnimation.playTogether((Collection)localObject1);
          this.changeModeAnimation.setDuration(200L);
          this.changeModeAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              PhotoViewer.access$15202(PhotoViewer.this, null);
              PhotoViewer.this.pickerView.setVisibility(8);
              PhotoViewer.this.pickerViewSendButton.setVisibility(8);
              PhotoViewer.this.cameraItem.setVisibility(8);
              PhotoViewer.this.selectedPhotosListView.setVisibility(8);
              PhotoViewer.this.selectedPhotosListView.setAlpha(0.0F);
              PhotoViewer.this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0F));
              PhotoViewer.this.photosCounterView.setRotationX(0.0F);
              PhotoViewer.this.selectedPhotosListView.setEnabled(false);
              PhotoViewer.access$11702(PhotoViewer.this, false);
              if (PhotoViewer.this.needCaptionLayout) {
                PhotoViewer.this.captionTextView.setVisibility(4);
              }
              if ((PhotoViewer.this.sendPhotoType == 0) || ((PhotoViewer.this.sendPhotoType == 2) && (PhotoViewer.this.imagesArrLocals.size() > 1)))
              {
                PhotoViewer.this.checkImageView.setVisibility(8);
                PhotoViewer.this.photosCounterView.setVisibility(8);
              }
              int i;
              float f1;
              float f2;
              if (PhotoViewer.this.centerImage.getBitmap() != null)
              {
                i = PhotoViewer.this.centerImage.getBitmapWidth();
                j = PhotoViewer.this.centerImage.getBitmapHeight();
                f1 = PhotoViewer.this.getContainerViewWidth() / i;
                f2 = PhotoViewer.this.getContainerViewHeight() / j;
                float f3 = PhotoViewer.this.getContainerViewWidth(3) / i;
                float f4 = PhotoViewer.this.getContainerViewHeight(3) / j;
                if (f1 <= f2) {
                  break label545;
                }
                if (f3 <= f4) {
                  break label552;
                }
                f3 = f4;
                label305:
                PhotoViewer.access$14702(PhotoViewer.this, f3 / f2);
                PhotoViewer.access$14802(PhotoViewer.this, 0.0F);
                paramAnonymousAnimator = PhotoViewer.this;
                i = -AndroidUtilities.dp(44.0F);
                if (Build.VERSION.SDK_INT < 21) {
                  break label555;
                }
              }
              label545:
              label552:
              label555:
              for (int j = AndroidUtilities.statusBarHeight / 2;; j = 0)
              {
                PhotoViewer.access$14902(paramAnonymousAnimator, j + i);
                PhotoViewer.access$15502(PhotoViewer.this, System.currentTimeMillis());
                PhotoViewer.access$15602(PhotoViewer.this, true);
                PhotoViewer.access$14502(PhotoViewer.this, new AnimatorSet());
                PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getColorPicker(), "translationY", new float[] { AndroidUtilities.dp(126.0F), 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(126.0F), 0.0F }) });
                PhotoViewer.this.imageMoveAnimation.setDuration(200L);
                PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter()
                {
                  public void onAnimationEnd(Animator paramAnonymous2Animator)
                  {
                    PhotoViewer.this.photoPaintView.init();
                    PhotoViewer.access$14502(PhotoViewer.this, null);
                    PhotoViewer.access$11002(PhotoViewer.this, PhotoViewer.58.this.val$mode);
                    PhotoViewer.access$14702(PhotoViewer.this, 1.0F);
                    PhotoViewer.access$14802(PhotoViewer.this, 0.0F);
                    PhotoViewer.access$14902(PhotoViewer.this, 0.0F);
                    PhotoViewer.access$6802(PhotoViewer.this, 1.0F);
                    PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                    PhotoViewer.this.containerView.invalidate();
                  }
                  
                  public void onAnimationStart(Animator paramAnonymous2Animator) {}
                });
                PhotoViewer.this.imageMoveAnimation.start();
                return;
                f2 = f1;
                break;
                break label305;
              }
            }
          });
          this.changeModeAnimation.start();
        }
      }
    }
  }
  
  private void switchToPip()
  {
    if ((this.videoPlayer == null) || (!this.textureUploaded) || (!checkInlinePermissions()) || (this.changingTextureView) || (this.switchingInlineMode) || (this.isInline)) {}
    for (;;)
    {
      return;
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
      if (Build.VERSION.SDK_INT >= 21)
      {
        this.pipAnimationInProgress = true;
        org.telegram.ui.Components.Rect localRect = PipVideoView.getPipRect(this.aspectRatioFrameLayout.getAspectRatio());
        float f = localRect.width / this.videoTextureView.getWidth();
        localRect.y += AndroidUtilities.statusBarHeight;
        AnimatorSet localAnimatorSet = new AnimatorSet();
        localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.textureImageView, "scaleX", new float[] { f }), ObjectAnimator.ofFloat(this.textureImageView, "scaleY", new float[] { f }), ObjectAnimator.ofFloat(this.textureImageView, "translationX", new float[] { localRect.x }), ObjectAnimator.ofFloat(this.textureImageView, "translationY", new float[] { localRect.y }), ObjectAnimator.ofFloat(this.videoTextureView, "scaleX", new float[] { f }), ObjectAnimator.ofFloat(this.videoTextureView, "scaleY", new float[] { f }), ObjectAnimator.ofFloat(this.videoTextureView, "translationX", new float[] { localRect.x - this.aspectRatioFrameLayout.getX() }), ObjectAnimator.ofFloat(this.videoTextureView, "translationY", new float[] { localRect.y - this.aspectRatioFrameLayout.getY() }), ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.groupedPhotosListView, "alpha", new float[] { 0.0F }) });
        localAnimatorSet.setInterpolator(new DecelerateInterpolator());
        localAnimatorSet.setDuration(250L);
        localAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            PhotoViewer.access$12602(PhotoViewer.this, false);
            PhotoViewer.this.switchToInlineRunnable.run();
          }
        });
        localAnimatorSet.start();
      }
      else
      {
        this.switchToInlineRunnable.run();
        dismissInternal();
      }
    }
  }
  
  private void toggleActionBar(final boolean paramBoolean1, boolean paramBoolean2)
  {
    float f1 = 1.0F;
    if (this.actionBarAnimator != null) {
      this.actionBarAnimator.cancel();
    }
    if (paramBoolean1)
    {
      this.actionBar.setVisibility(0);
      if (this.bottomLayout.getTag() != null) {
        this.bottomLayout.setVisibility(0);
      }
      if (this.captionTextView.getTag() != null) {
        this.captionTextView.setVisibility(0);
      }
    }
    this.isActionBarVisible = paramBoolean1;
    Object localObject1;
    if (Build.VERSION.SDK_INT >= 21)
    {
      if (!paramBoolean1) {
        break label369;
      }
      if ((this.windowLayoutParams.flags & 0x400) != 0)
      {
        localObject1 = this.windowLayoutParams;
        ((WindowManager.LayoutParams)localObject1).flags &= 0xFBFF;
        if (this.windowView == null) {}
      }
    }
    try
    {
      ((WindowManager)this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
      float f2;
      if (paramBoolean2)
      {
        localObject1 = new ArrayList();
        Object localObject3 = this.actionBar;
        if (paramBoolean1)
        {
          f2 = 1.0F;
          label174:
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject3, "alpha", new float[] { f2 }));
          if (this.bottomLayout != null)
          {
            localObject3 = this.bottomLayout;
            if (!paramBoolean1) {
              break label450;
            }
            f2 = 1.0F;
            label216:
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject3, "alpha", new float[] { f2 }));
          }
          localObject3 = this.groupedPhotosListView;
          if (!paramBoolean1) {
            break label456;
          }
          f2 = 1.0F;
          label251:
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject3, "alpha", new float[] { f2 }));
          if (this.captionTextView.getTag() != null)
          {
            localObject3 = this.captionTextView;
            if (!paramBoolean1) {
              break label462;
            }
          }
        }
        for (;;)
        {
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject3, "alpha", new float[] { f1 }));
          this.actionBarAnimator = new AnimatorSet();
          this.actionBarAnimator.playTogether((Collection)localObject1);
          this.actionBarAnimator.setDuration(200L);
          this.actionBarAnimator.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationCancel(Animator paramAnonymousAnimator)
            {
              if (paramAnonymousAnimator.equals(PhotoViewer.this.actionBarAnimator)) {
                PhotoViewer.access$15802(PhotoViewer.this, null);
              }
            }
            
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if (paramAnonymousAnimator.equals(PhotoViewer.this.actionBarAnimator))
              {
                if (!paramBoolean1)
                {
                  PhotoViewer.this.actionBar.setVisibility(4);
                  if (PhotoViewer.this.bottomLayout.getTag() != null) {
                    PhotoViewer.this.bottomLayout.setVisibility(4);
                  }
                  if (PhotoViewer.this.captionTextView.getTag() != null) {
                    PhotoViewer.this.captionTextView.setVisibility(4);
                  }
                }
                PhotoViewer.access$15802(PhotoViewer.this, null);
              }
            }
          });
          this.actionBarAnimator.start();
          label368:
          return;
          label369:
          if ((this.windowLayoutParams.flags & 0x400) != 0) {
            break label544;
          }
          localObject1 = this.windowLayoutParams;
          ((WindowManager.LayoutParams)localObject1).flags |= 0x400;
          if (this.windowView == null) {
            break;
          }
          try
          {
            ((WindowManager)this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
          }
          catch (Exception localException1) {}
          break;
          f2 = 0.0F;
          break label174;
          label450:
          f2 = 0.0F;
          break label216;
          label456:
          f2 = 0.0F;
          break label251;
          label462:
          f1 = 0.0F;
        }
      }
      Object localObject2 = this.actionBar;
      if (paramBoolean1)
      {
        f2 = 1.0F;
        label480:
        ((ActionBar)localObject2).setAlpha(f2);
        localObject2 = this.bottomLayout;
        if (!paramBoolean1) {
          break label552;
        }
        f2 = 1.0F;
        label500:
        ((FrameLayout)localObject2).setAlpha(f2);
        localObject2 = this.groupedPhotosListView;
        if (!paramBoolean1) {
          break label558;
        }
        f2 = 1.0F;
        label520:
        ((GroupedPhotosListView)localObject2).setAlpha(f2);
        localObject2 = this.captionTextView;
        if (!paramBoolean1) {
          break label564;
        }
      }
      for (;;)
      {
        ((TextView)localObject2).setAlpha(f1);
        break label368;
        label544:
        break;
        f2 = 0.0F;
        break label480;
        label552:
        f2 = 0.0F;
        break label500;
        label558:
        f2 = 0.0F;
        break label520;
        label564:
        f1 = 0.0F;
      }
    }
    catch (Exception localException2)
    {
      for (;;) {}
    }
  }
  
  private void toggleCheckImageView(boolean paramBoolean)
  {
    float f1 = 1.0F;
    AnimatorSet localAnimatorSet = new AnimatorSet();
    ArrayList localArrayList = new ArrayList();
    Object localObject = this.pickerView;
    if (paramBoolean)
    {
      f2 = 1.0F;
      localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 }));
      localObject = this.pickerViewSendButton;
      if (!paramBoolean) {
        break label233;
      }
      f2 = 1.0F;
      label67:
      localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 }));
      if (this.needCaptionLayout)
      {
        localObject = this.captionTextView;
        if (!paramBoolean) {
          break label239;
        }
        f2 = 1.0F;
        label109:
        localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 }));
      }
      if (this.sendPhotoType == 0)
      {
        localObject = this.checkImageView;
        if (!paramBoolean) {
          break label245;
        }
        f2 = 1.0F;
        label151:
        localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 }));
        localObject = this.photosCounterView;
        if (!paramBoolean) {
          break label251;
        }
      }
    }
    label233:
    label239:
    label245:
    label251:
    for (float f2 = f1;; f2 = 0.0F)
    {
      localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 }));
      localAnimatorSet.playTogether(localArrayList);
      localAnimatorSet.setDuration(200L);
      localAnimatorSet.start();
      return;
      f2 = 0.0F;
      break;
      f2 = 0.0F;
      break label67;
      f2 = 0.0F;
      break label109;
      f2 = 0.0F;
      break label151;
    }
  }
  
  private void toggleMiniProgress(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2)
    {
      toggleMiniProgressInternal(paramBoolean1);
      if (paramBoolean1)
      {
        if (this.miniProgressAnimator != null)
        {
          this.miniProgressAnimator.cancel();
          this.miniProgressAnimator = null;
        }
        AndroidUtilities.cancelRunOnUIThread(this.miniProgressShowRunnable);
        if (this.firstAnimationDelay)
        {
          this.firstAnimationDelay = false;
          toggleMiniProgressInternal(true);
        }
      }
      for (;;)
      {
        return;
        AndroidUtilities.runOnUIThread(this.miniProgressShowRunnable, 500L);
        continue;
        AndroidUtilities.cancelRunOnUIThread(this.miniProgressShowRunnable);
        if (this.miniProgressAnimator != null)
        {
          this.miniProgressAnimator.cancel();
          toggleMiniProgressInternal(false);
        }
      }
    }
    if (this.miniProgressAnimator != null)
    {
      this.miniProgressAnimator.cancel();
      this.miniProgressAnimator = null;
    }
    RadialProgressView localRadialProgressView = this.miniProgressView;
    float f;
    if (paramBoolean1)
    {
      f = 1.0F;
      label130:
      localRadialProgressView.setAlpha(f);
      localRadialProgressView = this.miniProgressView;
      if (!paramBoolean1) {
        break label163;
      }
    }
    label163:
    for (int i = 0;; i = 4)
    {
      localRadialProgressView.setVisibility(i);
      break;
      f = 0.0F;
      break label130;
    }
  }
  
  private void toggleMiniProgressInternal(final boolean paramBoolean)
  {
    if (paramBoolean) {
      this.miniProgressView.setVisibility(0);
    }
    this.miniProgressAnimator = new AnimatorSet();
    AnimatorSet localAnimatorSet = this.miniProgressAnimator;
    RadialProgressView localRadialProgressView = this.miniProgressView;
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(localRadialProgressView, "alpha", new float[] { f }) });
      this.miniProgressAnimator.setDuration(200L);
      this.miniProgressAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if (paramAnonymousAnimator.equals(PhotoViewer.this.miniProgressAnimator)) {
            PhotoViewer.access$15702(PhotoViewer.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (paramAnonymousAnimator.equals(PhotoViewer.this.miniProgressAnimator))
          {
            if (!paramBoolean) {
              PhotoViewer.this.miniProgressView.setVisibility(4);
            }
            PhotoViewer.access$15702(PhotoViewer.this, null);
          }
        }
      });
      this.miniProgressAnimator.start();
      return;
    }
  }
  
  private void togglePhotosListView(boolean paramBoolean1, boolean paramBoolean2)
  {
    float f1 = 1.0F;
    if (paramBoolean1 == this.isPhotosListViewVisible) {
      return;
    }
    if (paramBoolean1) {
      this.selectedPhotosListView.setVisibility(0);
    }
    this.isPhotosListViewVisible = paramBoolean1;
    this.selectedPhotosListView.setEnabled(paramBoolean1);
    float f2;
    if (paramBoolean2)
    {
      localObject1 = new ArrayList();
      Object localObject2 = this.selectedPhotosListView;
      if (paramBoolean1)
      {
        f2 = 1.0F;
        label62:
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f2 }));
        localObject2 = this.selectedPhotosListView;
        if (!paramBoolean1) {
          break label216;
        }
        f2 = 0.0F;
        label97:
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "translationY", new float[] { f2 }));
        localObject2 = this.photosCounterView;
        if (!paramBoolean1) {
          break label229;
        }
      }
      for (;;)
      {
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "rotationX", new float[] { f1 }));
        this.currentListViewAnimation = new AnimatorSet();
        this.currentListViewAnimation.playTogether((Collection)localObject1);
        if (!paramBoolean1) {
          this.currentListViewAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if ((PhotoViewer.this.currentListViewAnimation != null) && (PhotoViewer.this.currentListViewAnimation.equals(paramAnonymousAnimator)))
              {
                PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                PhotoViewer.access$15902(PhotoViewer.this, null);
              }
            }
          });
        }
        this.currentListViewAnimation.setDuration(200L);
        this.currentListViewAnimation.start();
        break;
        f2 = 0.0F;
        break label62;
        label216:
        f2 = -AndroidUtilities.dp(10.0F);
        break label97;
        label229:
        f1 = 0.0F;
      }
    }
    Object localObject1 = this.selectedPhotosListView;
    if (paramBoolean1)
    {
      f2 = 1.0F;
      label247:
      ((RecyclerListView)localObject1).setAlpha(f2);
      localObject1 = this.selectedPhotosListView;
      if (!paramBoolean1) {
        break label312;
      }
      f2 = 0.0F;
      label267:
      ((RecyclerListView)localObject1).setTranslationY(f2);
      localObject1 = this.photosCounterView;
      if (!paramBoolean1) {
        break label325;
      }
    }
    for (;;)
    {
      ((CounterView)localObject1).setRotationX(f1);
      if (paramBoolean1) {
        break;
      }
      this.selectedPhotosListView.setVisibility(8);
      break;
      f2 = 0.0F;
      break label247;
      label312:
      f2 = -AndroidUtilities.dp(10.0F);
      break label267;
      label325:
      f1 = 0.0F;
    }
  }
  
  private void updateCaptionTextForCurrentPhoto(Object paramObject)
  {
    Object localObject1 = null;
    Object localObject2;
    if ((paramObject instanceof MediaController.PhotoEntry))
    {
      localObject2 = ((MediaController.PhotoEntry)paramObject).caption;
      if ((localObject2 != null) && (((CharSequence)localObject2).length() != 0)) {
        break label70;
      }
      this.captionEditText.setFieldText("");
    }
    for (;;)
    {
      return;
      localObject2 = localObject1;
      if ((paramObject instanceof TLRPC.BotInlineResult)) {
        break;
      }
      localObject2 = localObject1;
      if (!(paramObject instanceof MediaController.SearchImage)) {
        break;
      }
      localObject2 = ((MediaController.SearchImage)paramObject).caption;
      break;
      label70:
      this.captionEditText.setFieldText((CharSequence)localObject2);
    }
  }
  
  private void updateMinMax(float paramFloat)
  {
    int i = (int)(this.centerImage.getImageWidth() * paramFloat - getContainerViewWidth()) / 2;
    int j = (int)(this.centerImage.getImageHeight() * paramFloat - getContainerViewHeight()) / 2;
    if (i > 0)
    {
      this.minX = (-i);
      this.maxX = i;
      if (j <= 0) {
        break label160;
      }
      this.minY = (-j);
      this.maxY = j;
    }
    for (;;)
    {
      if (this.currentEditMode == 1)
      {
        this.maxX += this.photoCropView.getLimitX();
        this.maxY += this.photoCropView.getLimitY();
        this.minX -= this.photoCropView.getLimitWidth();
        this.minY -= this.photoCropView.getLimitHeight();
      }
      return;
      this.maxX = 0.0F;
      this.minX = 0.0F;
      break;
      label160:
      this.maxY = 0.0F;
      this.minY = 0.0F;
    }
  }
  
  private void updateSelectedCount()
  {
    if (this.placeProvider == null) {}
    for (;;)
    {
      return;
      int i = this.placeProvider.getSelectedCount();
      this.photosCounterView.setCount(i);
      if (i == 0) {
        togglePhotosListView(false, true);
      }
    }
  }
  
  private void updateVideoInfo()
  {
    if (this.actionBar == null) {}
    for (;;)
    {
      return;
      if (this.compressionsCount != 0) {
        break;
      }
      this.actionBar.setSubtitle(null);
    }
    label43:
    int i;
    label119:
    int j;
    label143:
    label183:
    label202:
    ActionBar localActionBar;
    if (this.selectedCompression == 0)
    {
      this.compressItem.setImageResource(NUM);
      this.estimatedDuration = (Math.ceil((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
      if ((this.compressItem.getTag() != null) && (this.selectedCompression != this.compressionsCount - 1)) {
        break label438;
      }
      if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
        break label422;
      }
      i = this.originalHeight;
      if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
        break label430;
      }
      j = this.originalWidth;
      this.estimatedSize = ((int)((float)this.originalSize * ((float)this.estimatedDuration / this.videoDuration)));
      if (this.videoTimelineView.getLeftProgress() != 0.0F) {
        break label551;
      }
      this.startTime = -1L;
      if (this.videoTimelineView.getRightProgress() != 1.0F) {
        break label575;
      }
      this.endTime = -1L;
      str = String.format("%dx%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
      i = (int)(this.estimatedDuration / 1000L / 60L);
      this.currentSubtitle = String.format("%s, %s", new Object[] { str, String.format("%d:%02d, ~%s", new Object[] { Integer.valueOf(i), Integer.valueOf((int)Math.ceil(this.estimatedDuration / 1000L) - i * 60), AndroidUtilities.formatFileSize(this.estimatedSize) }) });
      localActionBar = this.actionBar;
      if (!this.muteVideo) {
        break label599;
      }
    }
    label422:
    label430:
    label438:
    label462:
    label543:
    label551:
    label575:
    label599:
    for (String str = null;; str = this.currentSubtitle)
    {
      localActionBar.setSubtitle(str);
      break;
      if (this.selectedCompression == 1)
      {
        this.compressItem.setImageResource(NUM);
        break label43;
      }
      if (this.selectedCompression == 2)
      {
        this.compressItem.setImageResource(NUM);
        break label43;
      }
      if (this.selectedCompression == 3)
      {
        this.compressItem.setImageResource(NUM);
        break label43;
      }
      if (this.selectedCompression != 4) {
        break label43;
      }
      this.compressItem.setImageResource(NUM);
      break label43;
      i = this.originalWidth;
      break label119;
      j = this.originalHeight;
      break label143;
      if ((this.rotationValue == 90) || (this.rotationValue == 270))
      {
        i = this.resultHeight;
        if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
          break label543;
        }
      }
      for (j = this.resultWidth;; j = this.resultHeight)
      {
        this.estimatedSize = ((int)((float)(this.audioFramesSize + this.videoFramesSize) * ((float)this.estimatedDuration / this.videoDuration)));
        this.estimatedSize += this.estimatedSize / 32768 * 16;
        break;
        i = this.resultWidth;
        break label462;
      }
      this.startTime = ((this.videoTimelineView.getLeftProgress() * this.videoDuration) * 1000L);
      break label183;
      this.endTime = ((this.videoTimelineView.getRightProgress() * this.videoDuration) * 1000L);
      break label202;
    }
  }
  
  private void updateVideoPlayerTime()
  {
    String str;
    if (this.videoPlayer == null) {
      str = String.format("%02d:%02d / %02d:%02d", new Object[] { Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0) });
    }
    for (;;)
    {
      this.videoPlayerTime.setText(str);
      return;
      long l1 = this.videoPlayer.getCurrentPosition();
      long l2 = l1;
      if (l1 < 0L) {
        l2 = 0L;
      }
      l1 = this.videoPlayer.getDuration();
      long l3 = l1;
      if (l1 < 0L) {
        l3 = 0L;
      }
      if ((l3 != -9223372036854775807L) && (l2 != -9223372036854775807L))
      {
        l1 = l2;
        long l4 = l3;
        if (!this.inPreview)
        {
          l1 = l2;
          l4 = l3;
          if (this.videoTimelineView.getVisibility() == 0)
          {
            l3 = ((float)l3 * (this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()));
            l2 = ((float)l2 - this.videoTimelineView.getLeftProgress() * (float)l3);
            l1 = l2;
            l4 = l3;
            if (l2 > l3)
            {
              l1 = l3;
              l4 = l3;
            }
          }
        }
        l2 = l1 / 1000L;
        l1 = l4 / 1000L;
        str = String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l2 / 60L), Long.valueOf(l2 % 60L), Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L) });
      }
      else
      {
        str = String.format("%02d:%02d / %02d:%02d", new Object[] { Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0) });
      }
    }
  }
  
  private void updateWidthHeightBitrateForCompression()
  {
    if (this.compressionsCount <= 0) {}
    do
    {
      return;
      if (this.selectedCompression >= this.compressionsCount) {
        this.selectedCompression = (this.compressionsCount - 1);
      }
    } while (this.selectedCompression == this.compressionsCount - 1);
    int i;
    float f;
    switch (this.selectedCompression)
    {
    default: 
      i = 2500000;
      f = 1280.0F;
      label80:
      if (this.originalWidth <= this.originalHeight) {
        break;
      }
    }
    for (f /= this.originalWidth;; f /= this.originalHeight)
    {
      this.resultWidth = (Math.round(this.originalWidth * f / 2.0F) * 2);
      this.resultHeight = (Math.round(this.originalHeight * f / 2.0F) * 2);
      if (this.bitrate == 0) {
        break;
      }
      this.bitrate = Math.min(i, (int)(this.originalBitrate / f));
      this.videoFramesSize = ((this.bitrate / 8 * this.videoDuration / 1000.0F));
      break;
      f = 426.0F;
      i = 400000;
      break label80;
      f = 640.0F;
      i = 900000;
      break label80;
      f = 854.0F;
      i = 1100000;
      break label80;
    }
  }
  
  public void closePhoto(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean2) && (this.currentEditMode != 0)) {
      if ((this.currentEditMode == 3) && (this.photoPaintView != null)) {
        this.photoPaintView.maybeShowDismissalAlert(this, this.parentActivity, new Runnable()
        {
          public void run()
          {
            PhotoViewer.this.switchToEditMode(0);
          }
        });
      }
    }
    final PlaceProviderObject localPlaceProviderObject;
    for (;;)
    {
      return;
      if (this.currentEditMode == 1) {
        this.photoCropView.cancelAnimationRunnable();
      }
      switchToEditMode(0);
      continue;
      if ((this.qualityChooseView != null) && (this.qualityChooseView.getTag() != null)) {
        this.qualityPicker.cancelButton.callOnClick();
      } else {
        try
        {
          if (this.visibleDialog != null)
          {
            this.visibleDialog.dismiss();
            this.visibleDialog = null;
          }
          if ((Build.VERSION.SDK_INT >= 21) && (this.actionBar != null) && ((this.windowLayoutParams.flags & 0x400) != 0))
          {
            WindowManager.LayoutParams localLayoutParams = this.windowLayoutParams;
            localLayoutParams.flags &= 0xFBFF;
            ((WindowManager)this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
          }
          if (this.currentEditMode != 0)
          {
            if (this.currentEditMode == 2)
            {
              this.photoFilterView.shutdown();
              this.containerView.removeView(this.photoFilterView);
              this.photoFilterView = null;
              this.currentEditMode = 0;
            }
          }
          else
          {
            if ((this.parentActivity == null) || ((!this.isInline) && (!this.isVisible)) || (checkAnimation()) || (this.placeProvider == null) || ((this.captionEditText.hideActionMode()) && (!paramBoolean2))) {
              continue;
            }
            releasePlayer();
            this.captionEditText.onDestroy();
            this.parentChatActivity = null;
            removeObservers();
            this.isActionBarVisible = false;
            if (this.velocityTracker != null)
            {
              this.velocityTracker.recycle();
              this.velocityTracker = null;
            }
            localPlaceProviderObject = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
            if (!this.isInline) {
              break label430;
            }
            this.isInline = false;
            this.animationInProgress = 0;
            onPhotoClosed(localPlaceProviderObject);
            this.containerView.setScaleX(1.0F);
            this.containerView.setScaleY(1.0F);
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
            continue;
            if (this.currentEditMode == 1)
            {
              this.editorDoneLayout.setVisibility(8);
              this.photoCropView.setVisibility(8);
            }
          }
        }
      }
    }
    label430:
    AnimatorSet localAnimatorSet;
    Object localObject2;
    Object localObject1;
    int i;
    int k;
    label555:
    label609:
    float f1;
    if (paramBoolean1)
    {
      this.animationInProgress = 1;
      this.animatingImageView.setVisibility(0);
      this.containerView.invalidate();
      localAnimatorSet = new AnimatorSet();
      localObject2 = this.animatingImageView.getLayoutParams();
      localObject1 = null;
      i = this.centerImage.getOrientation();
      int j = 0;
      k = j;
      if (localPlaceProviderObject != null)
      {
        k = j;
        if (localPlaceProviderObject.imageReceiver != null) {
          k = localPlaceProviderObject.imageReceiver.getAnimatedOrientation();
        }
      }
      if (k != 0) {
        i = k;
      }
      this.animatingImageView.setOrientation(i);
      if (localPlaceProviderObject != null)
      {
        localObject1 = this.animatingImageView;
        if (localPlaceProviderObject.radius != 0)
        {
          paramBoolean1 = true;
          ((ClippingImageView)localObject1).setNeedRadius(paramBoolean1);
          localObject1 = localPlaceProviderObject.imageReceiver.getDrawRegion();
          ((ViewGroup.LayoutParams)localObject2).width = (((android.graphics.Rect)localObject1).right - ((android.graphics.Rect)localObject1).left);
          ((ViewGroup.LayoutParams)localObject2).height = (((android.graphics.Rect)localObject1).bottom - ((android.graphics.Rect)localObject1).top);
          this.animatingImageView.setImageBitmap(localPlaceProviderObject.thumb);
          this.animatingImageView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
          f1 = AndroidUtilities.displaySize.x / ((ViewGroup.LayoutParams)localObject2).width;
          i = AndroidUtilities.displaySize.y;
          if (Build.VERSION.SDK_INT < 21) {
            break label1559;
          }
          k = AndroidUtilities.statusBarHeight;
          label655:
          float f2 = (k + i) / ((ViewGroup.LayoutParams)localObject2).height;
          if (f1 <= f2) {
            break label1565;
          }
          f1 = f2;
          label682:
          float f3 = ((ViewGroup.LayoutParams)localObject2).width;
          float f4 = this.scale;
          float f5 = ((ViewGroup.LayoutParams)localObject2).height;
          f2 = this.scale;
          f3 = (AndroidUtilities.displaySize.x - f3 * f4 * f1) / 2.0F;
          i = AndroidUtilities.displaySize.y;
          if (Build.VERSION.SDK_INT < 21) {
            break label1568;
          }
          k = AndroidUtilities.statusBarHeight;
          label751:
          f2 = (k + i - f5 * f2 * f1) / 2.0F;
          this.animatingImageView.setTranslationX(this.translationX + f3);
          this.animatingImageView.setTranslationY(this.translationY + f2);
          this.animatingImageView.setScaleX(this.scale * f1);
          this.animatingImageView.setScaleY(this.scale * f1);
          if (localPlaceProviderObject == null) {
            break label1590;
          }
          localPlaceProviderObject.imageReceiver.setVisible(false, true);
          int m = Math.abs(((android.graphics.Rect)localObject1).left - localPlaceProviderObject.imageReceiver.getImageX());
          int n = Math.abs(((android.graphics.Rect)localObject1).top - localPlaceProviderObject.imageReceiver.getImageY());
          localObject2 = new int[2];
          localPlaceProviderObject.parentView.getLocationInWindow((int[])localObject2);
          i = localObject2[1];
          if (Build.VERSION.SDK_INT < 21) {
            break label1574;
          }
          k = 0;
          label909:
          i = i - k - (localPlaceProviderObject.viewY + ((android.graphics.Rect)localObject1).top) + localPlaceProviderObject.clipTopAddition;
          k = i;
          if (i < 0) {
            k = 0;
          }
          int i1 = localPlaceProviderObject.viewY;
          int i2 = ((android.graphics.Rect)localObject1).top;
          int i3 = ((android.graphics.Rect)localObject1).bottom;
          j = ((android.graphics.Rect)localObject1).top;
          int i4 = localObject2[1];
          int i5 = localPlaceProviderObject.parentView.getHeight();
          if (Build.VERSION.SDK_INT < 21) {
            break label1582;
          }
          i = 0;
          label997:
          j = i3 - j + (i1 + i2) - (i5 + i4 - i) + localPlaceProviderObject.clipBottomAddition;
          i = j;
          if (j < 0) {
            i = 0;
          }
          k = Math.max(k, n);
          i = Math.max(i, n);
          this.animationValues[0][0] = this.animatingImageView.getScaleX();
          this.animationValues[0][1] = this.animatingImageView.getScaleY();
          this.animationValues[0][2] = this.animatingImageView.getTranslationX();
          this.animationValues[0][3] = this.animatingImageView.getTranslationY();
          this.animationValues[0][4] = 0.0F;
          this.animationValues[0][5] = 0.0F;
          this.animationValues[0][6] = 0.0F;
          this.animationValues[0][7] = 0.0F;
          this.animationValues[1][0] = localPlaceProviderObject.scale;
          this.animationValues[1][1] = localPlaceProviderObject.scale;
          this.animationValues[1][2] = (localPlaceProviderObject.viewX + ((android.graphics.Rect)localObject1).left * localPlaceProviderObject.scale - getLeftInset());
          this.animationValues[1][3] = (localPlaceProviderObject.viewY + ((android.graphics.Rect)localObject1).top * localPlaceProviderObject.scale);
          this.animationValues[1][4] = (m * localPlaceProviderObject.scale);
          this.animationValues[1][5] = (k * localPlaceProviderObject.scale);
          this.animationValues[1][6] = (i * localPlaceProviderObject.scale);
          this.animationValues[1][7] = localPlaceProviderObject.radius;
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }) });
          this.animationEndRunnable = new Runnable()
          {
            public void run()
            {
              if (Build.VERSION.SDK_INT >= 18) {
                PhotoViewer.this.containerView.setLayerType(0, null);
              }
              PhotoViewer.access$7502(PhotoViewer.this, 0);
              PhotoViewer.this.onPhotoClosed(localPlaceProviderObject);
            }
          };
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (PhotoViewer.this.animationEndRunnable != null)
                  {
                    PhotoViewer.this.animationEndRunnable.run();
                    PhotoViewer.access$16602(PhotoViewer.this, null);
                  }
                }
              });
            }
          });
          this.transitionAnimationStartTime = System.currentTimeMillis();
          if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(2, null);
          }
          localAnimatorSet.start();
        }
      }
    }
    for (;;)
    {
      if (this.currentAnimation != null)
      {
        this.currentAnimation.setSecondParentView(null);
        this.currentAnimation = null;
        this.centerImage.setImageBitmap((Drawable)null);
      }
      if ((this.placeProvider == null) || (this.placeProvider.canScrollAway())) {
        break;
      }
      this.placeProvider.cancelButtonPressed();
      break;
      paramBoolean1 = false;
      break label555;
      this.animatingImageView.setNeedRadius(false);
      ((ViewGroup.LayoutParams)localObject2).width = this.centerImage.getImageWidth();
      ((ViewGroup.LayoutParams)localObject2).height = this.centerImage.getImageHeight();
      this.animatingImageView.setImageBitmap(this.centerImage.getBitmapSafe());
      break label609;
      label1559:
      k = 0;
      break label655;
      label1565:
      break label682;
      label1568:
      k = 0;
      break label751;
      label1574:
      k = AndroidUtilities.statusBarHeight;
      break label909;
      label1582:
      i = AndroidUtilities.statusBarHeight;
      break label997;
      label1590:
      i = AndroidUtilities.displaySize.y;
      label1611:
      ClippingImageView localClippingImageView;
      if (Build.VERSION.SDK_INT >= 21)
      {
        k = AndroidUtilities.statusBarHeight;
        k = i + k;
        localObject2 = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0 });
        localObject1 = ObjectAnimator.ofFloat(this.animatingImageView, "alpha", new float[] { 0.0F });
        localClippingImageView = this.animatingImageView;
        if (this.translationY < 0.0F) {
          break label1741;
        }
      }
      label1741:
      for (f1 = k;; f1 = -k)
      {
        localAnimatorSet.playTogether(new Animator[] { localObject2, localObject1, ObjectAnimator.ofFloat(localClippingImageView, "translationY", new float[] { f1 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }) });
        break;
        k = 0;
        break label1611;
      }
      localObject1 = new AnimatorSet();
      ((AnimatorSet)localObject1).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "scaleX", new float[] { 0.9F }), ObjectAnimator.ofFloat(this.containerView, "scaleY", new float[] { 0.9F }), ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }) });
      this.animationInProgress = 2;
      this.animationEndRunnable = new Runnable()
      {
        public void run()
        {
          if (PhotoViewer.this.containerView == null) {}
          for (;;)
          {
            return;
            if (Build.VERSION.SDK_INT >= 18) {
              PhotoViewer.this.containerView.setLayerType(0, null);
            }
            PhotoViewer.access$7502(PhotoViewer.this, 0);
            PhotoViewer.this.onPhotoClosed(localPlaceProviderObject);
            PhotoViewer.this.containerView.setScaleX(1.0F);
            PhotoViewer.this.containerView.setScaleY(1.0F);
          }
        }
      };
      ((AnimatorSet)localObject1).setDuration(200L);
      ((AnimatorSet)localObject1).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (PhotoViewer.this.animationEndRunnable != null)
          {
            PhotoViewer.this.animationEndRunnable.run();
            PhotoViewer.access$16602(PhotoViewer.this, null);
          }
        }
      });
      this.transitionAnimationStartTime = System.currentTimeMillis();
      if (Build.VERSION.SDK_INT >= 18) {
        this.containerView.setLayerType(2, null);
      }
      ((AnimatorSet)localObject1).start();
    }
  }
  
  public void destroyPhotoViewer()
  {
    if ((this.parentActivity == null) || (this.windowView == null)) {}
    for (;;)
    {
      return;
      if (this.pipVideoView != null)
      {
        this.pipVideoView.close();
        this.pipVideoView = null;
      }
      removeObservers();
      releasePlayer();
      try
      {
        if (this.windowView.getParent() != null) {
          ((WindowManager)this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
        }
        this.windowView = null;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
        Instance = null;
      }
      if (this.currentThumb != null)
      {
        this.currentThumb.release();
        this.currentThumb = null;
      }
      this.animatingImageView.setImageBitmap(null);
      if (this.captionEditText != null) {
        this.captionEditText.onDestroy();
      }
      if (this == PipInstance) {
        PipInstance = null;
      }
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.FileDidFailedLoad)
    {
      paramVarArgs = (String)paramVarArgs[0];
      paramInt1 = 0;
      if (paramInt1 < 3)
      {
        if ((this.currentFileNames[paramInt1] == null) || (!this.currentFileNames[paramInt1].equals(paramVarArgs))) {
          break label61;
        }
        this.photoProgressViews[paramInt1].setProgress(1.0F, true);
        checkProgress(paramInt1, true);
      }
    }
    for (;;)
    {
      return;
      label61:
      paramInt1++;
      break;
      if (paramInt1 == NotificationCenter.FileDidLoaded)
      {
        paramVarArgs = (String)paramVarArgs[0];
        for (paramInt1 = 0;; paramInt1++)
        {
          if (paramInt1 >= 3) {
            break label219;
          }
          if ((this.currentFileNames[paramInt1] != null) && (this.currentFileNames[paramInt1].equals(paramVarArgs)))
          {
            this.photoProgressViews[paramInt1].setProgress(1.0F, true);
            checkProgress(paramInt1, true);
            if ((this.videoPlayer == null) && (paramInt1 == 0) && (((this.currentMessageObject != null) && (this.currentMessageObject.isVideo())) || ((this.currentBotInlineResult != null) && ((this.currentBotInlineResult.type.equals("video")) || (MessageObject.isVideoDocument(this.currentBotInlineResult.document)))))) {
              onActionClick(false);
            }
            if ((paramInt1 != 0) || (this.videoPlayer == null)) {
              break;
            }
            this.currentVideoFinishedLoading = true;
            break;
          }
        }
      }
      else
      {
        label219:
        Object localObject1;
        label238:
        Object localObject2;
        label373:
        long l2;
        if (paramInt1 == NotificationCenter.FileLoadProgressChanged)
        {
          localObject1 = (String)paramVarArgs[0];
          paramInt1 = 0;
          float f;
          if (paramInt1 < 3) {
            if ((this.currentFileNames[paramInt1] != null) && (this.currentFileNames[paramInt1].equals(localObject1)))
            {
              localObject2 = (Float)paramVarArgs[1];
              this.photoProgressViews[paramInt1].setProgress(((Float)localObject2).floatValue(), true);
              if ((paramInt1 == 0) && (this.videoPlayer != null) && (this.videoPlayerSeekbar != null))
              {
                if (!this.currentVideoFinishedLoading) {
                  break label373;
                }
                f = 1.0F;
              }
            }
          }
          for (;;)
          {
            if (f != -1.0F)
            {
              this.videoPlayerSeekbar.setBufferedProgress(f);
              if (this.pipVideoView != null) {
                this.pipVideoView.setBufferedProgress(f);
              }
              this.videoPlayerControlFrameLayout.invalidate();
            }
            checkBufferedProgress(((Float)localObject2).floatValue());
            paramInt1++;
            break label238;
            break;
            long l1 = SystemClock.uptimeMillis();
            if (Math.abs(l1 - this.lastBufferedPositionCheck) >= 500L)
            {
              if (this.seekToProgressPending == 0.0F)
              {
                l2 = this.videoPlayer.getDuration();
                long l3 = this.videoPlayer.getCurrentPosition();
                if ((l2 >= 0L) && (l2 != -9223372036854775807L) && (l3 >= 0L))
                {
                  f = (float)l3 / (float)l2;
                  label454:
                  if (!this.isStreaming) {
                    break label505;
                  }
                }
              }
              label505:
              for (f = FileLoader.getInstance(this.currentAccount).getBufferedProgressFromPosition(f, this.currentFileNames[0]);; f = 1.0F)
              {
                this.lastBufferedPositionCheck = l1;
                break;
                f = 0.0F;
                break label454;
                f = this.seekToProgressPending;
                break label454;
              }
            }
            f = -1.0F;
          }
        }
        boolean bool;
        int i;
        label668:
        int j;
        if (paramInt1 == NotificationCenter.dialogPhotosLoaded)
        {
          paramInt1 = ((Integer)paramVarArgs[3]).intValue();
          paramInt2 = ((Integer)paramVarArgs[0]).intValue();
          if ((this.avatarsDialogId == paramInt2) && (this.classGuid == paramInt1))
          {
            bool = ((Boolean)paramVarArgs[2]).booleanValue();
            paramInt1 = -1;
            paramVarArgs = (ArrayList)paramVarArgs[4];
            if (!paramVarArgs.isEmpty())
            {
              this.imagesArrLocations.clear();
              this.imagesArrLocationsSizes.clear();
              this.avatarsArr.clear();
              i = 0;
              if (i < paramVarArgs.size())
              {
                localObject2 = (TLRPC.Photo)paramVarArgs.get(i);
                paramInt2 = paramInt1;
                if (localObject2 != null)
                {
                  paramInt2 = paramInt1;
                  if (!(localObject2 instanceof TLRPC.TL_photoEmpty))
                  {
                    if (((TLRPC.Photo)localObject2).sizes != null) {
                      break label668;
                    }
                    paramInt2 = paramInt1;
                  }
                }
                do
                {
                  i++;
                  paramInt1 = paramInt2;
                  break;
                  localObject1 = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo)localObject2).sizes, 640);
                  paramInt2 = paramInt1;
                } while (localObject1 == null);
                paramInt2 = paramInt1;
                if (paramInt1 == -1)
                {
                  paramInt2 = paramInt1;
                  if (this.currentFileLocation == null) {}
                }
                for (j = 0;; j++)
                {
                  paramInt2 = paramInt1;
                  if (j < ((TLRPC.Photo)localObject2).sizes.size())
                  {
                    TLRPC.PhotoSize localPhotoSize = (TLRPC.PhotoSize)((TLRPC.Photo)localObject2).sizes.get(j);
                    if ((localPhotoSize.location.local_id == this.currentFileLocation.local_id) && (localPhotoSize.location.volume_id == this.currentFileLocation.volume_id)) {
                      paramInt2 = this.imagesArrLocations.size();
                    }
                  }
                  else
                  {
                    this.imagesArrLocations.add(((TLRPC.PhotoSize)localObject1).location);
                    this.imagesArrLocationsSizes.add(Integer.valueOf(((TLRPC.PhotoSize)localObject1).size));
                    this.avatarsArr.add(localObject2);
                    break;
                  }
                }
              }
              if (!this.avatarsArr.isEmpty())
              {
                this.menuItem.showSubItem(6);
                label849:
                this.needSearchImageInArr = false;
                this.currentIndex = -1;
                if (paramInt1 == -1) {
                  break label912;
                }
                setImageIndex(paramInt1, true);
              }
              for (;;)
              {
                if (!bool) {
                  break label958;
                }
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.avatarsDialogId, 80, 0L, false, this.classGuid);
                break;
                this.menuItem.hideSubItem(6);
                break label849;
                label912:
                this.avatarsArr.add(0, new TLRPC.TL_photoEmpty());
                this.imagesArrLocations.add(0, this.currentFileLocation);
                this.imagesArrLocationsSizes.add(0, Integer.valueOf(0));
                setImageIndex(0, true);
              }
            }
          }
        }
        else
        {
          label958:
          if (paramInt1 == NotificationCenter.mediaCountDidLoaded)
          {
            l2 = ((Long)paramVarArgs[0]).longValue();
            if ((l2 == this.currentDialogId) || (l2 == this.mergeDialogId))
            {
              if (l2 == this.currentDialogId) {
                this.totalImagesCount = ((Integer)paramVarArgs[1]).intValue();
              }
              for (;;)
              {
                if ((!this.needSearchImageInArr) || (!this.isFirstLoading)) {
                  break label1097;
                }
                this.isFirstLoading = false;
                this.loadingMoreImages = true;
                DataQuery.getInstance(this.currentAccount).loadMedia(this.currentDialogId, 80, 0, 0, true, this.classGuid);
                break;
                if (l2 == this.mergeDialogId) {
                  this.totalImagesCountMerge = ((Integer)paramVarArgs[1]).intValue();
                }
              }
              label1097:
              if (!this.imagesArr.isEmpty()) {
                if (this.opennedFromMedia) {
                  this.actionBar.setTitle(LocaleController.formatString("Of", NUM, new Object[] { Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
                } else {
                  this.actionBar.setTitle(LocaleController.formatString("Of", NUM, new Object[] { Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge - this.imagesArr.size() + this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
                }
              }
            }
          }
          else if (paramInt1 == NotificationCenter.mediaDidLoaded)
          {
            l2 = ((Long)paramVarArgs[0]).longValue();
            paramInt1 = ((Integer)paramVarArgs[3]).intValue();
            if (((l2 == this.currentDialogId) || (l2 == this.mergeDialogId)) && (paramInt1 == this.classGuid))
            {
              this.loadingMoreImages = false;
              if (l2 == this.currentDialogId) {}
              for (paramInt2 = 0;; paramInt2 = 1)
              {
                localObject2 = (ArrayList)paramVarArgs[2];
                this.endReached[paramInt2] = ((Boolean)paramVarArgs[5]).booleanValue();
                if (!this.needSearchImageInArr) {
                  break label2087;
                }
                if ((!((ArrayList)localObject2).isEmpty()) || ((paramInt2 == 0) && (this.mergeDialogId != 0L))) {
                  break label1369;
                }
                this.needSearchImageInArr = false;
                break;
              }
              label1369:
              paramInt1 = -1;
              paramVarArgs = (MessageObject)this.imagesArr.get(this.currentIndex);
              i = 0;
              int k = 0;
              if (k < ((ArrayList)localObject2).size())
              {
                localObject1 = (MessageObject)((ArrayList)localObject2).get(k);
                int m = i;
                j = paramInt1;
                if (this.imagesByIdsTemp[paramInt2].indexOfKey(((MessageObject)localObject1).getId()) < 0)
                {
                  this.imagesByIdsTemp[paramInt2].put(((MessageObject)localObject1).getId(), localObject1);
                  if (!this.opennedFromMedia) {
                    break label1508;
                  }
                  this.imagesArrTemp.add(localObject1);
                  if (((MessageObject)localObject1).getId() == paramVarArgs.getId()) {
                    paramInt1 = i;
                  }
                  m = i + 1;
                  j = paramInt1;
                }
                for (;;)
                {
                  k++;
                  i = m;
                  paramInt1 = j;
                  break;
                  label1508:
                  i++;
                  this.imagesArrTemp.add(0, localObject1);
                  m = i;
                  j = paramInt1;
                  if (((MessageObject)localObject1).getId() == paramVarArgs.getId())
                  {
                    j = ((ArrayList)localObject2).size() - i;
                    m = i;
                  }
                }
              }
              if ((i == 0) && ((paramInt2 != 0) || (this.mergeDialogId == 0L)))
              {
                this.totalImagesCount = this.imagesArr.size();
                this.totalImagesCountMerge = 0;
              }
              if (paramInt1 != -1)
              {
                this.imagesArr.clear();
                this.imagesArr.addAll(this.imagesArrTemp);
                for (paramInt2 = 0; paramInt2 < 2; paramInt2++)
                {
                  this.imagesByIds[paramInt2] = this.imagesByIdsTemp[paramInt2].clone();
                  this.imagesByIdsTemp[paramInt2].clear();
                }
                this.imagesArrTemp.clear();
                this.needSearchImageInArr = false;
                this.currentIndex = -1;
                paramInt2 = paramInt1;
                if (paramInt1 >= this.imagesArr.size()) {
                  paramInt2 = this.imagesArr.size() - 1;
                }
                setImageIndex(paramInt2, true);
              }
              else
              {
                if (this.opennedFromMedia) {
                  if (this.imagesArrTemp.isEmpty())
                  {
                    j = 0;
                    label1721:
                    paramInt1 = j;
                    i = paramInt2;
                    if (paramInt2 == 0)
                    {
                      paramInt1 = j;
                      i = paramInt2;
                      if (this.endReached[paramInt2] != 0)
                      {
                        paramInt1 = j;
                        i = paramInt2;
                        if (this.mergeDialogId != 0L)
                        {
                          paramInt2 = 1;
                          paramInt1 = j;
                          i = paramInt2;
                          if (!this.imagesArrTemp.isEmpty())
                          {
                            paramInt1 = j;
                            i = paramInt2;
                            if (((MessageObject)this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getDialogId() != this.mergeDialogId)
                            {
                              paramInt1 = 0;
                              i = paramInt2;
                            }
                          }
                        }
                      }
                    }
                    label1820:
                    if (this.endReached[i] != 0) {
                      break label2011;
                    }
                    this.loadingMoreImages = true;
                    if (!this.opennedFromMedia) {
                      break label2041;
                    }
                    paramVarArgs = DataQuery.getInstance(this.currentAccount);
                    if (i != 0) {
                      break label2032;
                    }
                  }
                }
                label2011:
                label2032:
                for (l2 = this.currentDialogId;; l2 = this.mergeDialogId)
                {
                  paramVarArgs.loadMedia(l2, 80, paramInt1, 0, true, this.classGuid);
                  break;
                  j = ((MessageObject)this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getId();
                  break label1721;
                  if (this.imagesArrTemp.isEmpty()) {}
                  for (j = 0;; j = ((MessageObject)this.imagesArrTemp.get(0)).getId())
                  {
                    paramInt1 = j;
                    i = paramInt2;
                    if (paramInt2 != 0) {
                      break label1820;
                    }
                    paramInt1 = j;
                    i = paramInt2;
                    if (this.endReached[paramInt2] == 0) {
                      break label1820;
                    }
                    paramInt1 = j;
                    i = paramInt2;
                    if (this.mergeDialogId == 0L) {
                      break label1820;
                    }
                    paramInt2 = 1;
                    paramInt1 = j;
                    i = paramInt2;
                    if (this.imagesArrTemp.isEmpty()) {
                      break label1820;
                    }
                    paramInt1 = j;
                    i = paramInt2;
                    if (((MessageObject)this.imagesArrTemp.get(0)).getDialogId() == this.mergeDialogId) {
                      break label1820;
                    }
                    paramInt1 = 0;
                    i = paramInt2;
                    break label1820;
                    break;
                  }
                }
                label2041:
                paramVarArgs = DataQuery.getInstance(this.currentAccount);
                if (i == 0) {}
                for (l2 = this.currentDialogId;; l2 = this.mergeDialogId)
                {
                  paramVarArgs.loadMedia(l2, 80, paramInt1, 0, true, this.classGuid);
                  break;
                }
                label2087:
                paramInt1 = 0;
                localObject2 = ((ArrayList)localObject2).iterator();
                while (((Iterator)localObject2).hasNext())
                {
                  paramVarArgs = (MessageObject)((Iterator)localObject2).next();
                  if (this.imagesByIds[paramInt2].indexOfKey(paramVarArgs.getId()) < 0)
                  {
                    paramInt1++;
                    if (this.opennedFromMedia) {
                      this.imagesArr.add(paramVarArgs);
                    }
                    for (;;)
                    {
                      this.imagesByIds[paramInt2].put(paramVarArgs.getId(), paramVarArgs);
                      break;
                      this.imagesArr.add(0, paramVarArgs);
                    }
                  }
                }
                if (this.opennedFromMedia)
                {
                  if (paramInt1 == 0)
                  {
                    this.totalImagesCount = this.imagesArr.size();
                    this.totalImagesCountMerge = 0;
                  }
                }
                else if (paramInt1 != 0)
                {
                  paramInt2 = this.currentIndex;
                  this.currentIndex = -1;
                  setImageIndex(paramInt2 + paramInt1, true);
                }
                else
                {
                  this.totalImagesCount = this.imagesArr.size();
                  this.totalImagesCountMerge = 0;
                }
              }
            }
          }
          else if (paramInt1 == NotificationCenter.emojiDidLoaded)
          {
            if (this.captionTextView != null) {
              this.captionTextView.invalidate();
            }
          }
          else if (paramInt1 == NotificationCenter.FilePreparingFailed)
          {
            paramVarArgs = (MessageObject)paramVarArgs[0];
            if (this.loadInitialVideo)
            {
              this.loadInitialVideo = false;
              this.progressView.setVisibility(4);
              preparePlayer(this.currentPlayingVideoFile, false, false);
            }
            else
            {
              if (this.tryStartRequestPreviewOnFinish)
              {
                releasePlayer();
                if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {}
                for (bool = true;; bool = false)
                {
                  this.tryStartRequestPreviewOnFinish = bool;
                  break;
                }
              }
              if (paramVarArgs == this.videoPreviewMessageObject)
              {
                this.requestingPreview = false;
                this.progressView.setVisibility(4);
              }
            }
          }
          else if ((paramInt1 == NotificationCenter.FileNewChunkAvailable) && ((MessageObject)paramVarArgs[0] == this.videoPreviewMessageObject))
          {
            localObject2 = (String)paramVarArgs[1];
            if (((Long)paramVarArgs[3]).longValue() != 0L)
            {
              this.requestingPreview = false;
              this.progressView.setVisibility(4);
              preparePlayer(Uri.fromFile(new File((String)localObject2)), false, true);
            }
          }
        }
      }
    }
  }
  
  public void exitFromPip()
  {
    if (!this.isInline) {}
    for (;;)
    {
      return;
      if (Instance != null) {
        Instance.closePhoto(false, true);
      }
      Instance = PipInstance;
      PipInstance = null;
      this.switchingInlineMode = true;
      if (this.currentBitmap != null)
      {
        this.currentBitmap.recycle();
        this.currentBitmap = null;
      }
      this.changingTextureView = true;
      this.isInline = false;
      this.videoTextureView.setVisibility(4);
      this.aspectRatioFrameLayout.addView(this.videoTextureView);
      if (ApplicationLoader.mainInterfacePaused) {}
      try
      {
        Activity localActivity = this.parentActivity;
        Object localObject = new android/content/Intent;
        ((Intent)localObject).<init>(ApplicationLoader.applicationContext, BringAppForegroundService.class);
        localActivity.startService((Intent)localObject);
        if (Build.VERSION.SDK_INT >= 21)
        {
          this.pipAnimationInProgress = true;
          localObject = PipVideoView.getPipRect(this.aspectRatioFrameLayout.getAspectRatio());
          float f = ((org.telegram.ui.Components.Rect)localObject).width / this.textureImageView.getLayoutParams().width;
          ((org.telegram.ui.Components.Rect)localObject).y += AndroidUtilities.statusBarHeight;
          this.textureImageView.setScaleX(f);
          this.textureImageView.setScaleY(f);
          this.textureImageView.setTranslationX(((org.telegram.ui.Components.Rect)localObject).x);
          this.textureImageView.setTranslationY(((org.telegram.ui.Components.Rect)localObject).y);
          this.videoTextureView.setScaleX(f);
          this.videoTextureView.setScaleY(f);
          this.videoTextureView.setTranslationX(((org.telegram.ui.Components.Rect)localObject).x - this.aspectRatioFrameLayout.getX());
          this.videoTextureView.setTranslationY(((org.telegram.ui.Components.Rect)localObject).y - this.aspectRatioFrameLayout.getY());
        }
      }
      catch (Throwable localThrowable)
      {
        try
        {
          for (;;)
          {
            this.isVisible = true;
            ((WindowManager)this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
            if (this.currentPlaceObject != null) {
              this.currentPlaceObject.imageReceiver.setVisible(false, false);
            }
            if (Build.VERSION.SDK_INT < 21) {
              break;
            }
            this.waitingForDraw = 4;
            break;
            localThrowable = localThrowable;
            FileLog.e(localThrowable);
            continue;
            this.pipVideoView.close();
            this.pipVideoView = null;
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    }
  }
  
  @Keep
  public float getAnimationValue()
  {
    return this.animationValue;
  }
  
  public int getSelectiongLength()
  {
    if (this.captionEditText != null) {}
    for (int i = this.captionEditText.getSelectionLength();; i = 0) {
      return i;
    }
  }
  
  public VideoPlayer getVideoPlayer()
  {
    return this.videoPlayer;
  }
  
  public boolean isMuteVideo()
  {
    return this.muteVideo;
  }
  
  public boolean isVisible()
  {
    if ((this.isVisible) && (this.placeProvider != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (this.pipVideoView != null) {
      this.pipVideoView.onConfigurationChanged();
    }
  }
  
  public boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    long l1;
    int i;
    float f1;
    long l3;
    long l4;
    label119:
    boolean bool;
    if ((this.videoPlayer != null) && (this.videoPlayerControlFrameLayout.getVisibility() == 0))
    {
      l1 = this.videoPlayer.getCurrentPosition();
      long l2 = this.videoPlayer.getDuration();
      if ((l2 >= 0L) && (l1 >= 0L) && (l2 != -9223372036854775807L) && (l1 != -9223372036854775807L))
      {
        i = getContainerViewWidth();
        f1 = paramMotionEvent.getX();
        if (f1 >= i / 3 * 2)
        {
          l3 = l1 + 10000L;
          l4 = l3;
          if (l1 == l4) {
            break label203;
          }
          if (l4 <= l2) {
            break label186;
          }
          l3 = l2;
          this.videoPlayer.seekTo(l3);
          this.containerView.invalidate();
          this.videoPlayerSeekbar.setProgress((float)l3 / (float)l2);
          this.videoPlayerControlFrameLayout.invalidate();
          bool = true;
        }
      }
    }
    for (;;)
    {
      return bool;
      l3 = l1;
      if (f1 >= i / 3) {
        break;
      }
      l3 = l1 - 10000L;
      break;
      label186:
      l3 = l4;
      if (l4 >= 0L) {
        break label119;
      }
      l3 = 0L;
      break label119;
      label203:
      if ((!this.canZoom) || ((this.scale == 1.0F) && ((this.translationY != 0.0F) || (this.translationX != 0.0F))))
      {
        bool = false;
      }
      else
      {
        if ((this.animationStartTime == 0L) && (this.animationInProgress == 0)) {
          break label265;
        }
        bool = false;
      }
    }
    label265:
    float f2;
    float f3;
    if (this.scale == 1.0F)
    {
      f2 = paramMotionEvent.getX() - getContainerViewWidth() / 2 - (paramMotionEvent.getX() - getContainerViewWidth() / 2 - this.translationX) * (3.0F / this.scale);
      f3 = paramMotionEvent.getY() - getContainerViewHeight() / 2 - (paramMotionEvent.getY() - getContainerViewHeight() / 2 - this.translationY) * (3.0F / this.scale);
      updateMinMax(3.0F);
      if (f2 < this.minX)
      {
        f1 = this.minX;
        label379:
        if (f3 >= this.minY) {
          break label441;
        }
        f2 = this.minY;
        label395:
        animateTo(3.0F, f1, f2, true);
      }
    }
    for (;;)
    {
      this.doubleTap = true;
      bool = true;
      break;
      f1 = f2;
      if (f2 <= this.maxX) {
        break label379;
      }
      f1 = this.maxX;
      break label379;
      label441:
      f2 = f3;
      if (f3 <= this.maxY) {
        break label395;
      }
      f2 = this.maxY;
      break label395;
      animateTo(1.0F, 0.0F, 0.0F, true);
    }
  }
  
  public boolean onDoubleTapEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean onDown(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    if (this.scale != 1.0F)
    {
      this.scroller.abortAnimation();
      this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(paramFloat1), Math.round(paramFloat2), (int)this.minX, (int)this.maxX, (int)this.minY, (int)this.maxY);
      this.containerView.postInvalidate();
    }
    return false;
  }
  
  public void onLongPress(MotionEvent paramMotionEvent) {}
  
  public void onPause()
  {
    if (this.currentAnimation != null) {
      closePhoto(false, false);
    }
    for (;;)
    {
      return;
      if (this.lastTitle != null) {
        closeCaptionEnter(true);
      }
    }
  }
  
  public void onResume()
  {
    redraw(0);
    if (this.videoPlayer != null) {
      this.videoPlayer.seekTo(this.videoPlayer.getCurrentPosition() + 1L);
    }
  }
  
  public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    return false;
  }
  
  public void onShowPress(MotionEvent paramMotionEvent) {}
  
  public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    if (this.discardTap) {
      bool1 = false;
    }
    for (;;)
    {
      return bool1;
      int i;
      float f1;
      float f2;
      if (this.containerView.getTag() != null)
      {
        if ((this.aspectRatioFrameLayout != null) && (this.aspectRatioFrameLayout.getVisibility() == 0)) {}
        for (i = 1;; i = 0)
        {
          f1 = paramMotionEvent.getX();
          f2 = paramMotionEvent.getY();
          if ((this.photoProgressViews[0] == null) || (this.containerView == null) || (i != 0)) {
            break label202;
          }
          i = this.photoProgressViews[0].backgroundState;
          if ((i <= 0) || (i > 3) || (f1 < (getContainerViewWidth() - AndroidUtilities.dp(100.0F)) / 2.0F) || (f1 > (getContainerViewWidth() + AndroidUtilities.dp(100.0F)) / 2.0F) || (f2 < (getContainerViewHeight() - AndroidUtilities.dp(100.0F)) / 2.0F) || (f2 > (getContainerViewHeight() + AndroidUtilities.dp(100.0F)) / 2.0F)) {
            break label202;
          }
          onActionClick(true);
          checkProgress(0, true);
          bool1 = bool2;
          break;
        }
        label202:
        if (!this.isActionBarVisible) {
          bool1 = true;
        }
        toggleActionBar(bool1, true);
        bool1 = bool2;
      }
      else if (this.sendPhotoType == 0)
      {
        if (this.isCurrentVideo)
        {
          this.videoPlayButton.callOnClick();
          bool1 = bool2;
        }
        else
        {
          this.checkImageView.performClick();
          bool1 = bool2;
        }
      }
      else if ((this.currentBotInlineResult != null) && ((this.currentBotInlineResult.type.equals("video")) || (MessageObject.isVideoDocument(this.currentBotInlineResult.document))))
      {
        i = this.photoProgressViews[0].backgroundState;
        bool1 = bool2;
        if (i > 0)
        {
          bool1 = bool2;
          if (i <= 3)
          {
            f2 = paramMotionEvent.getX();
            f1 = paramMotionEvent.getY();
            bool1 = bool2;
            if (f2 >= (getContainerViewWidth() - AndroidUtilities.dp(100.0F)) / 2.0F)
            {
              bool1 = bool2;
              if (f2 <= (getContainerViewWidth() + AndroidUtilities.dp(100.0F)) / 2.0F)
              {
                bool1 = bool2;
                if (f1 >= (getContainerViewHeight() - AndroidUtilities.dp(100.0F)) / 2.0F)
                {
                  bool1 = bool2;
                  if (f1 <= (getContainerViewHeight() + AndroidUtilities.dp(100.0F)) / 2.0F)
                  {
                    onActionClick(true);
                    checkProgress(0, true);
                    bool1 = bool2;
                  }
                }
              }
            }
          }
        }
      }
      else
      {
        bool1 = bool2;
        if (this.sendPhotoType == 2)
        {
          bool1 = bool2;
          if (this.isCurrentVideo)
          {
            this.videoPlayButton.callOnClick();
            bool1 = bool2;
          }
        }
      }
    }
  }
  
  public boolean onSingleTapUp(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean openPhoto(ArrayList<MessageObject> paramArrayList, int paramInt, long paramLong1, long paramLong2, PhotoViewerProvider paramPhotoViewerProvider)
  {
    return openPhoto((MessageObject)paramArrayList.get(paramInt), null, paramArrayList, null, paramInt, paramPhotoViewerProvider, null, paramLong1, paramLong2);
  }
  
  public boolean openPhoto(MessageObject paramMessageObject, long paramLong1, long paramLong2, PhotoViewerProvider paramPhotoViewerProvider)
  {
    return openPhoto(paramMessageObject, null, null, null, 0, paramPhotoViewerProvider, null, paramLong1, paramLong2);
  }
  
  public boolean openPhoto(final MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, ArrayList<MessageObject> paramArrayList, final ArrayList<Object> paramArrayList1, int paramInt, PhotoViewerProvider paramPhotoViewerProvider, ChatActivity paramChatActivity, long paramLong1, long paramLong2)
  {
    boolean bool;
    if ((this.parentActivity == null) || (this.isVisible) || ((paramPhotoViewerProvider == null) && (checkAnimation())) || ((paramMessageObject == null) && (paramFileLocation == null) && (paramArrayList == null) && (paramArrayList1 == null))) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      final PlaceProviderObject localPlaceProviderObject = paramPhotoViewerProvider.getPlaceForPhoto(paramMessageObject, paramFileLocation, paramInt);
      if ((localPlaceProviderObject == null) && (paramArrayList1 == null))
      {
        bool = false;
        continue;
      }
      this.lastInsets = null;
      WindowManager localWindowManager = (WindowManager)this.parentActivity.getSystemService("window");
      if (this.attachedToWindow) {}
      try
      {
        localWindowManager.removeView(this.windowView);
        for (;;)
        {
          float f1;
          try
          {
            this.windowLayoutParams.type = 99;
            if (Build.VERSION.SDK_INT >= 21)
            {
              this.windowLayoutParams.flags = -NUM;
              this.windowLayoutParams.softInputMode = 272;
              this.windowView.setFocusable(false);
              this.containerView.setFocusable(false);
              localWindowManager.addView(this.windowView, this.windowLayoutParams);
              this.doneButtonPressed = false;
              this.parentChatActivity = paramChatActivity;
              this.actionBar.setTitle(LocaleController.formatString("Of", NUM, new Object[] { Integer.valueOf(1), Integer.valueOf(1) }));
              NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailedLoad);
              NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidLoaded);
              NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileLoadProgressChanged);
              NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountDidLoaded);
              NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoaded);
              NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogPhotosLoaded);
              NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
              NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FilePreparingFailed);
              NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileNewChunkAvailable);
              this.placeProvider = paramPhotoViewerProvider;
              this.mergeDialogId = paramLong2;
              this.currentDialogId = paramLong1;
              this.selectedPhotosAdapter.notifyDataSetChanged();
              if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
              }
              this.isVisible = true;
              toggleActionBar(true, false);
              togglePhotosListView(false, false);
              if (localPlaceProviderObject == null) {
                break label1498;
              }
              this.disableShowCheck = true;
              this.animationInProgress = 1;
              if (paramMessageObject != null) {
                this.currentAnimation = localPlaceProviderObject.imageReceiver.getAnimation();
              }
              onPhotoShow(paramMessageObject, paramFileLocation, paramArrayList, paramArrayList1, paramInt, localPlaceProviderObject);
              paramMessageObject = localPlaceProviderObject.imageReceiver.getDrawRegion();
              paramInt = localPlaceProviderObject.imageReceiver.getOrientation();
              i = localPlaceProviderObject.imageReceiver.getAnimatedOrientation();
              if (i != 0) {
                paramInt = i;
              }
              this.animatingImageView.setVisibility(0);
              this.animatingImageView.setRadius(localPlaceProviderObject.radius);
              this.animatingImageView.setOrientation(paramInt);
              paramFileLocation = this.animatingImageView;
              if (localPlaceProviderObject.radius == 0) {
                break label1457;
              }
              bool = true;
              paramFileLocation.setNeedRadius(bool);
              this.animatingImageView.setImageBitmap(localPlaceProviderObject.thumb);
              this.animatingImageView.setAlpha(1.0F);
              this.animatingImageView.setPivotX(0.0F);
              this.animatingImageView.setPivotY(0.0F);
              this.animatingImageView.setScaleX(localPlaceProviderObject.scale);
              this.animatingImageView.setScaleY(localPlaceProviderObject.scale);
              this.animatingImageView.setTranslationX(localPlaceProviderObject.viewX + paramMessageObject.left * localPlaceProviderObject.scale);
              this.animatingImageView.setTranslationY(localPlaceProviderObject.viewY + paramMessageObject.top * localPlaceProviderObject.scale);
              paramFileLocation = this.animatingImageView.getLayoutParams();
              paramFileLocation.width = (paramMessageObject.right - paramMessageObject.left);
              paramFileLocation.height = (paramMessageObject.bottom - paramMessageObject.top);
              this.animatingImageView.setLayoutParams(paramFileLocation);
              f1 = AndroidUtilities.displaySize.x / paramFileLocation.width;
              i = AndroidUtilities.displaySize.y;
              if (Build.VERSION.SDK_INT < 21) {
                break label1463;
              }
              paramInt = AndroidUtilities.statusBarHeight;
              f2 = (paramInt + i) / paramFileLocation.height;
              if (f1 <= f2) {
                break label1469;
              }
              f1 = paramFileLocation.width;
              float f3 = paramFileLocation.height;
              f1 = (AndroidUtilities.displaySize.x - f1 * f2) / 2.0F;
              i = AndroidUtilities.displaySize.y;
              if (Build.VERSION.SDK_INT < 21) {
                break label1476;
              }
              paramInt = AndroidUtilities.statusBarHeight;
              f3 = (paramInt + i - f3 * f2) / 2.0F;
              int j = Math.abs(paramMessageObject.left - localPlaceProviderObject.imageReceiver.getImageX());
              int k = Math.abs(paramMessageObject.top - localPlaceProviderObject.imageReceiver.getImageY());
              paramArrayList = new int[2];
              localPlaceProviderObject.parentView.getLocationInWindow(paramArrayList);
              i = paramArrayList[1];
              if (Build.VERSION.SDK_INT < 21) {
                break label1482;
              }
              paramInt = 0;
              i = i - paramInt - (localPlaceProviderObject.viewY + paramMessageObject.top) + localPlaceProviderObject.clipTopAddition;
              paramInt = i;
              if (i < 0) {
                paramInt = 0;
              }
              int m = localPlaceProviderObject.viewY;
              int n = paramMessageObject.top;
              int i1 = paramFileLocation.height;
              int i2 = paramArrayList[1];
              int i3 = localPlaceProviderObject.parentView.getHeight();
              if (Build.VERSION.SDK_INT < 21) {
                break label1490;
              }
              i = 0;
              i3 = i1 + (m + n) - (i3 + i2 - i) + localPlaceProviderObject.clipBottomAddition;
              i = i3;
              if (i3 < 0) {
                i = 0;
              }
              paramInt = Math.max(paramInt, k);
              i = Math.max(i, k);
              this.animationValues[0][0] = this.animatingImageView.getScaleX();
              this.animationValues[0][1] = this.animatingImageView.getScaleY();
              this.animationValues[0][2] = this.animatingImageView.getTranslationX();
              this.animationValues[0][3] = this.animatingImageView.getTranslationY();
              this.animationValues[0][4] = (j * localPlaceProviderObject.scale);
              this.animationValues[0][5] = (paramInt * localPlaceProviderObject.scale);
              this.animationValues[0][6] = (i * localPlaceProviderObject.scale);
              this.animationValues[0][7] = this.animatingImageView.getRadius();
              this.animationValues[1][0] = f2;
              this.animationValues[1][1] = f2;
              this.animationValues[1][2] = f1;
              this.animationValues[1][3] = f3;
              this.animationValues[1][4] = 0.0F;
              this.animationValues[1][5] = 0.0F;
              this.animationValues[1][6] = 0.0F;
              this.animationValues[1][7] = 0.0F;
              this.animatingImageView.setAnimationProgress(0.0F);
              this.backgroundDrawable.setAlpha(0);
              this.containerView.setAlpha(0.0F);
              paramMessageObject = new AnimatorSet();
              paramMessageObject.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0, 255 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F, 1.0F }) });
              this.animationEndRunnable = new Runnable()
              {
                public void run()
                {
                  if ((PhotoViewer.this.containerView == null) || (PhotoViewer.this.windowView == null)) {}
                  do
                  {
                    return;
                    if (Build.VERSION.SDK_INT >= 18) {
                      PhotoViewer.this.containerView.setLayerType(0, null);
                    }
                    PhotoViewer.access$7502(PhotoViewer.this, 0);
                    PhotoViewer.access$16102(PhotoViewer.this, 0L);
                    PhotoViewer.this.setImages();
                    PhotoViewer.this.containerView.invalidate();
                    PhotoViewer.this.animatingImageView.setVisibility(8);
                    if (PhotoViewer.this.showAfterAnimation != null) {
                      PhotoViewer.this.showAfterAnimation.imageReceiver.setVisible(true, true);
                    }
                    if (PhotoViewer.this.hideAfterAnimation != null) {
                      PhotoViewer.this.hideAfterAnimation.imageReceiver.setVisible(false, true);
                    }
                  } while ((paramArrayList1 == null) || (PhotoViewer.this.sendPhotoType == 3));
                  if (Build.VERSION.SDK_INT >= 21) {}
                  for (PhotoViewer.this.windowLayoutParams.flags = -NUM;; PhotoViewer.this.windowLayoutParams.flags = 0)
                  {
                    PhotoViewer.this.windowLayoutParams.softInputMode = 272;
                    ((WindowManager)PhotoViewer.this.parentActivity.getSystemService("window")).updateViewLayout(PhotoViewer.this.windowView, PhotoViewer.this.windowLayoutParams);
                    PhotoViewer.this.windowView.setFocusable(true);
                    PhotoViewer.this.containerView.setFocusable(true);
                    break;
                  }
                }
              };
              paramMessageObject.setDuration(200L);
              paramMessageObject.addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnonymousAnimator)
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationCenter.getInstance(PhotoViewer.this.currentAccount).setAnimationInProgress(false);
                      if (PhotoViewer.this.animationEndRunnable != null)
                      {
                        PhotoViewer.this.animationEndRunnable.run();
                        PhotoViewer.access$16602(PhotoViewer.this, null);
                      }
                    }
                  });
                }
              });
              this.transitionAnimationStartTime = System.currentTimeMillis();
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance(PhotoViewer.this.currentAccount).setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded, NotificationCenter.mediaDidLoaded, NotificationCenter.dialogPhotosLoaded });
                  NotificationCenter.getInstance(PhotoViewer.this.currentAccount).setAnimationInProgress(true);
                  paramMessageObject.start();
                }
              });
              if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, null);
              }
              BackgroundDrawable.access$16702(this.backgroundDrawable, new Runnable()
              {
                public void run()
                {
                  PhotoViewer.access$8702(PhotoViewer.this, false);
                  localPlaceProviderObject.imageReceiver.setVisible(false, true);
                }
              });
              bool = true;
              break;
            }
            this.windowLayoutParams.flags = 8;
            continue;
          }
          catch (Exception paramMessageObject)
          {
            FileLog.e(paramMessageObject);
            bool = false;
          }
          label1457:
          bool = false;
          continue;
          label1463:
          paramInt = 0;
          continue;
          label1469:
          float f2 = f1;
          continue;
          label1476:
          paramInt = 0;
          continue;
          label1482:
          paramInt = AndroidUtilities.statusBarHeight;
          continue;
          label1490:
          int i = AndroidUtilities.statusBarHeight;
        }
        label1498:
        if ((paramArrayList1 != null) && (this.sendPhotoType != 3)) {
          if (Build.VERSION.SDK_INT < 21) {
            break label1604;
          }
        }
        label1604:
        for (this.windowLayoutParams.flags = -NUM;; this.windowLayoutParams.flags = 0)
        {
          this.windowLayoutParams.softInputMode = 272;
          localWindowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
          this.windowView.setFocusable(true);
          this.containerView.setFocusable(true);
          this.backgroundDrawable.setAlpha(255);
          this.containerView.setAlpha(1.0F);
          onPhotoShow(paramMessageObject, paramFileLocation, paramArrayList, paramArrayList1, paramInt, localPlaceProviderObject);
          break;
        }
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
  }
  
  public boolean openPhoto(TLRPC.FileLocation paramFileLocation, PhotoViewerProvider paramPhotoViewerProvider)
  {
    return openPhoto(null, paramFileLocation, null, null, 0, paramPhotoViewerProvider, null, 0L, 0L);
  }
  
  public boolean openPhotoForSelect(ArrayList<Object> paramArrayList, int paramInt1, int paramInt2, PhotoViewerProvider paramPhotoViewerProvider, ChatActivity paramChatActivity)
  {
    this.sendPhotoType = paramInt2;
    FrameLayout.LayoutParams localLayoutParams;
    if (this.pickerViewSendButton != null)
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.itemsLayout.getLayoutParams();
      if (this.sendPhotoType != 1) {
        break label108;
      }
      this.pickerView.setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
      this.pickerViewSendButton.setImageResource(NUM);
      this.pickerViewSendButton.setPadding(0, AndroidUtilities.dp(1.0F), 0, 0);
    }
    for (localLayoutParams.bottomMargin = AndroidUtilities.dp(16.0F);; localLayoutParams.bottomMargin = 0)
    {
      this.itemsLayout.setLayoutParams(localLayoutParams);
      return openPhoto(null, null, null, paramArrayList, paramInt1, paramPhotoViewerProvider, paramChatActivity, 0L, 0L);
      label108:
      this.pickerView.setPadding(0, 0, 0, 0);
      this.pickerViewSendButton.setImageResource(NUM);
      this.pickerViewSendButton.setPadding(AndroidUtilities.dp(4.0F), 0, 0, 0);
    }
  }
  
  @Keep
  public void setAnimationValue(float paramFloat)
  {
    this.animationValue = paramFloat;
    this.containerView.invalidate();
  }
  
  public void setParentActivity(Activity paramActivity)
  {
    this.currentAccount = UserConfig.selectedAccount;
    this.centerImage.setCurrentAccount(this.currentAccount);
    this.leftImage.setCurrentAccount(this.currentAccount);
    this.rightImage.setCurrentAccount(this.currentAccount);
    if (this.parentActivity == paramActivity) {
      return;
    }
    this.parentActivity = paramActivity;
    this.actvityContext = new ContextThemeWrapper(this.parentActivity, NUM);
    if (progressDrawables == null)
    {
      progressDrawables = new Drawable[4];
      progressDrawables[0] = this.parentActivity.getResources().getDrawable(NUM);
      progressDrawables[1] = this.parentActivity.getResources().getDrawable(NUM);
      progressDrawables[2] = this.parentActivity.getResources().getDrawable(NUM);
      progressDrawables[3] = this.parentActivity.getResources().getDrawable(NUM);
    }
    this.scroller = new Scroller(paramActivity);
    this.windowView = new FrameLayout(paramActivity)
    {
      private Runnable attachRunnable;
      
      public boolean dispatchKeyEventPreIme(KeyEvent paramAnonymousKeyEvent)
      {
        boolean bool = true;
        if ((paramAnonymousKeyEvent != null) && (paramAnonymousKeyEvent.getKeyCode() == 4) && (paramAnonymousKeyEvent.getAction() == 1)) {
          if ((PhotoViewer.this.captionEditText.isPopupShowing()) || (PhotoViewer.this.captionEditText.isKeyboardVisible()))
          {
            PhotoViewer.this.closeCaptionEnter(false);
            bool = false;
          }
        }
        for (;;)
        {
          return bool;
          PhotoViewer.getInstance().closePhoto(true, false);
          continue;
          bool = super.dispatchKeyEventPreIme(paramAnonymousKeyEvent);
        }
      }
      
      protected boolean drawChild(Canvas paramAnonymousCanvas, View paramAnonymousView, long paramAnonymousLong)
      {
        boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
        if ((Build.VERSION.SDK_INT >= 21) && (paramAnonymousView == PhotoViewer.this.animatingImageView) && (PhotoViewer.this.lastInsets != null))
        {
          paramAnonymousView = (WindowInsets)PhotoViewer.this.lastInsets;
          paramAnonymousCanvas.drawRect(0.0F, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight() + paramAnonymousView.getSystemWindowInsetBottom(), PhotoViewer.this.blackPaint);
        }
        return bool;
      }
      
      protected void onAttachedToWindow()
      {
        super.onAttachedToWindow();
        PhotoViewer.access$7302(PhotoViewer.this, true);
      }
      
      protected void onDetachedFromWindow()
      {
        super.onDetachedFromWindow();
        PhotoViewer.access$7302(PhotoViewer.this, false);
        PhotoViewer.access$6602(PhotoViewer.this, false);
      }
      
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((PhotoViewer.this.isVisible) && (super.onInterceptTouchEvent(paramAnonymousMotionEvent))) {}
        for (boolean bool = true;; bool = false) {
          return bool;
        }
      }
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        paramAnonymousInt2 = 0;
        paramAnonymousInt1 = paramAnonymousInt2;
        if (Build.VERSION.SDK_INT >= 21)
        {
          paramAnonymousInt1 = paramAnonymousInt2;
          if (PhotoViewer.this.lastInsets != null) {
            paramAnonymousInt1 = 0 + ((WindowInsets)PhotoViewer.this.lastInsets).getSystemWindowInsetLeft();
          }
        }
        PhotoViewer.this.animatingImageView.layout(paramAnonymousInt1, 0, PhotoViewer.this.animatingImageView.getMeasuredWidth() + paramAnonymousInt1, PhotoViewer.this.animatingImageView.getMeasuredHeight());
        PhotoViewer.this.containerView.layout(paramAnonymousInt1, 0, PhotoViewer.this.containerView.getMeasuredWidth() + paramAnonymousInt1, PhotoViewer.this.containerView.getMeasuredHeight());
        PhotoViewer.access$6602(PhotoViewer.this, true);
        if (paramAnonymousBoolean)
        {
          if (!PhotoViewer.this.dontResetZoomOnFirstLayout)
          {
            PhotoViewer.access$6802(PhotoViewer.this, 1.0F);
            PhotoViewer.access$6902(PhotoViewer.this, 0.0F);
            PhotoViewer.access$7002(PhotoViewer.this, 0.0F);
            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
          }
          if (PhotoViewer.this.checkImageView != null) {
            PhotoViewer.this.checkImageView.post(new Runnable()
            {
              public void run()
              {
                int i = 0;
                FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)PhotoViewer.this.checkImageView.getLayoutParams();
                ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                int j = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(40.0F)) / 2;
                if (Build.VERSION.SDK_INT >= 21) {}
                for (int k = AndroidUtilities.statusBarHeight;; k = 0)
                {
                  localLayoutParams.topMargin = (k + j);
                  PhotoViewer.this.checkImageView.setLayoutParams(localLayoutParams);
                  localLayoutParams = (FrameLayout.LayoutParams)PhotoViewer.this.photosCounterView.getLayoutParams();
                  j = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(40.0F)) / 2;
                  k = i;
                  if (Build.VERSION.SDK_INT >= 21) {
                    k = AndroidUtilities.statusBarHeight;
                  }
                  localLayoutParams.topMargin = (j + k);
                  PhotoViewer.this.photosCounterView.setLayoutParams(localLayoutParams);
                  return;
                }
              }
            });
          }
        }
        if (PhotoViewer.this.dontResetZoomOnFirstLayout)
        {
          PhotoViewer.this.setScaleToFill();
          PhotoViewer.access$6702(PhotoViewer.this, false);
        }
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        int i = View.MeasureSpec.getSize(paramAnonymousInt1);
        paramAnonymousInt1 = View.MeasureSpec.getSize(paramAnonymousInt2);
        Object localObject;
        int j;
        if ((Build.VERSION.SDK_INT >= 21) && (PhotoViewer.this.lastInsets != null))
        {
          localObject = (WindowInsets)PhotoViewer.this.lastInsets;
          paramAnonymousInt2 = paramAnonymousInt1;
          if (AndroidUtilities.incorrectDisplaySizeFix)
          {
            paramAnonymousInt2 = paramAnonymousInt1;
            if (paramAnonymousInt1 > AndroidUtilities.displaySize.y) {
              paramAnonymousInt2 = AndroidUtilities.displaySize.y;
            }
            paramAnonymousInt2 += AndroidUtilities.statusBarHeight;
          }
          j = paramAnonymousInt2 - ((WindowInsets)localObject).getSystemWindowInsetBottom();
          paramAnonymousInt2 = i - ((WindowInsets)localObject).getSystemWindowInsetRight();
        }
        for (;;)
        {
          setMeasuredDimension(paramAnonymousInt2, j);
          paramAnonymousInt1 = paramAnonymousInt2;
          if (Build.VERSION.SDK_INT >= 21)
          {
            paramAnonymousInt1 = paramAnonymousInt2;
            if (PhotoViewer.this.lastInsets != null) {
              paramAnonymousInt1 = paramAnonymousInt2 - ((WindowInsets)PhotoViewer.this.lastInsets).getSystemWindowInsetLeft();
            }
          }
          localObject = PhotoViewer.this.animatingImageView.getLayoutParams();
          PhotoViewer.this.animatingImageView.measure(View.MeasureSpec.makeMeasureSpec(((ViewGroup.LayoutParams)localObject).width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(((ViewGroup.LayoutParams)localObject).height, Integer.MIN_VALUE));
          PhotoViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(paramAnonymousInt1, NUM), View.MeasureSpec.makeMeasureSpec(j, NUM));
          return;
          j = paramAnonymousInt1;
          paramAnonymousInt2 = i;
          if (paramAnonymousInt1 > AndroidUtilities.displaySize.y)
          {
            j = AndroidUtilities.displaySize.y;
            paramAnonymousInt2 = i;
          }
        }
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((PhotoViewer.this.isVisible) && (PhotoViewer.this.onTouchEvent(paramAnonymousMotionEvent))) {}
        for (boolean bool = true;; bool = false) {
          return bool;
        }
      }
      
      public ActionMode startActionModeForChild(View paramAnonymousView, ActionMode.Callback paramAnonymousCallback, int paramAnonymousInt)
      {
        Object localObject;
        if (Build.VERSION.SDK_INT >= 23)
        {
          localObject = PhotoViewer.this.parentActivity.findViewById(16908290);
          if (!(localObject instanceof ViewGroup)) {}
        }
        for (;;)
        {
          try
          {
            localObject = ((ViewGroup)localObject).startActionModeForChild(paramAnonymousView, paramAnonymousCallback, paramAnonymousInt);
            paramAnonymousView = (View)localObject;
            return paramAnonymousView;
          }
          catch (Throwable localThrowable)
          {
            FileLog.e(localThrowable);
          }
          paramAnonymousView = super.startActionModeForChild(paramAnonymousView, paramAnonymousCallback, paramAnonymousInt);
        }
      }
    };
    this.windowView.setBackgroundDrawable(this.backgroundDrawable);
    this.windowView.setClipChildren(true);
    this.windowView.setFocusable(false);
    this.animatingImageView = new ClippingImageView(paramActivity);
    this.animatingImageView.setAnimationValues(this.animationValues);
    this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0F));
    this.containerView = new FrameLayoutDrawer(paramActivity);
    this.containerView.setFocusable(false);
    this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.containerView.setFitsSystemWindows(true);
      this.containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
      {
        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View paramAnonymousView, WindowInsets paramAnonymousWindowInsets)
        {
          paramAnonymousView = (WindowInsets)PhotoViewer.this.lastInsets;
          PhotoViewer.access$6402(PhotoViewer.this, paramAnonymousWindowInsets);
          if ((paramAnonymousView == null) || (!paramAnonymousView.toString().equals(paramAnonymousWindowInsets.toString())))
          {
            if (PhotoViewer.this.animationInProgress == 1)
            {
              PhotoViewer.this.animatingImageView.setTranslationX(PhotoViewer.this.animatingImageView.getTranslationX() - PhotoViewer.this.getLeftInset());
              PhotoViewer.this.animationValues[0][2] = PhotoViewer.this.animatingImageView.getTranslationX();
            }
            PhotoViewer.this.windowView.requestLayout();
          }
          return paramAnonymousWindowInsets.consumeSystemWindowInsets();
        }
      });
      this.containerView.setSystemUiVisibility(1280);
    }
    this.windowLayoutParams = new WindowManager.LayoutParams();
    this.windowLayoutParams.height = -1;
    this.windowLayoutParams.format = -3;
    this.windowLayoutParams.width = -1;
    this.windowLayoutParams.gravity = 51;
    this.windowLayoutParams.type = 99;
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.windowLayoutParams.flags = -NUM;
      this.actionBar = new ActionBar(paramActivity)
      {
        public void setAlpha(float paramAnonymousFloat)
        {
          super.setAlpha(paramAnonymousFloat);
          PhotoViewer.this.containerView.invalidate();
        }
      };
      this.actionBar.setTitleColor(-1);
      this.actionBar.setSubtitleColor(-1);
      this.actionBar.setBackgroundColor(NUM);
      paramActivity = this.actionBar;
      if (Build.VERSION.SDK_INT < 21) {
        break label945;
      }
    }
    int i;
    label945:
    for (boolean bool = true;; bool = false)
    {
      paramActivity.setOccupyStatusBar(bool);
      this.actionBar.setItemsBackgroundColor(NUM, false);
      this.actionBar.setBackButtonImage(NUM);
      this.actionBar.setTitle(LocaleController.formatString("Of", NUM, new Object[] { Integer.valueOf(1), Integer.valueOf(1) }));
      this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0F));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public boolean canOpenMenu()
        {
          boolean bool1 = true;
          if (PhotoViewer.this.currentMessageObject != null) {
            if (!FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner).exists()) {
              break label84;
            }
          }
          label84:
          label89:
          label92:
          for (;;)
          {
            return bool1;
            TLRPC.FileLocation localFileLocation;
            if (PhotoViewer.this.currentFileLocation != null)
            {
              localFileLocation = PhotoViewer.this.currentFileLocation;
              if ((PhotoViewer.this.avatarsDialogId == 0) && (!PhotoViewer.this.isEvent)) {
                break label89;
              }
            }
            for (boolean bool2 = true;; bool2 = false)
            {
              if (FileLoader.getPathToAttach(localFileLocation, bool2).exists()) {
                break label92;
              }
              bool1 = false;
              break;
            }
          }
        }
        
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            if ((PhotoViewer.this.needCaptionLayout) && ((PhotoViewer.this.captionEditText.isPopupShowing()) || (PhotoViewer.this.captionEditText.isKeyboardVisible()))) {
              PhotoViewer.this.closeCaptionEnter(false);
            }
          }
          for (;;)
          {
            return;
            PhotoViewer.this.closePhoto(true, false);
            continue;
            final Object localObject1;
            label205:
            Object localObject2;
            boolean bool;
            if (paramAnonymousInt == 1)
            {
              if ((Build.VERSION.SDK_INT >= 23) && (PhotoViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
              {
                PhotoViewer.this.parentActivity.requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 4);
              }
              else
              {
                localObject1 = null;
                if (PhotoViewer.this.currentMessageObject != null) {
                  if (((PhotoViewer.this.currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (PhotoViewer.this.currentMessageObject.messageOwner.media.webpage != null) && (PhotoViewer.this.currentMessageObject.messageOwner.media.webpage.document == null))
                  {
                    localObject1 = FileLoader.getPathToAttach(PhotoViewer.this.getFileLocation(PhotoViewer.this.currentIndex, null), true);
                    if ((localObject1 == null) || (!((File)localObject1).exists())) {
                      break label344;
                    }
                    localObject2 = ((File)localObject1).toString();
                    localObject1 = PhotoViewer.this.parentActivity;
                    if ((PhotoViewer.this.currentMessageObject == null) || (!PhotoViewer.this.currentMessageObject.isVideo())) {
                      break label339;
                    }
                  }
                }
                label339:
                for (paramAnonymousInt = 1;; paramAnonymousInt = 0)
                {
                  MediaController.saveFile((String)localObject2, (Context)localObject1, paramAnonymousInt, null, null);
                  break;
                  localObject1 = FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
                  break label205;
                  if (PhotoViewer.this.currentFileLocation == null) {
                    break label205;
                  }
                  localObject1 = PhotoViewer.this.currentFileLocation;
                  if ((PhotoViewer.this.avatarsDialogId != 0) || (PhotoViewer.this.isEvent)) {}
                  for (bool = true;; bool = false)
                  {
                    localObject1 = FileLoader.getPathToAttach((TLObject)localObject1, bool);
                    break;
                  }
                }
                label344:
                PhotoViewer.this.showDownloadAlert();
              }
            }
            else if (paramAnonymousInt == 2)
            {
              if (PhotoViewer.this.currentDialogId != 0L)
              {
                PhotoViewer.access$8702(PhotoViewer.this, true);
                localObject1 = new Bundle();
                ((Bundle)localObject1).putLong("dialog_id", PhotoViewer.this.currentDialogId);
                localObject1 = new MediaActivity((Bundle)localObject1);
                if (PhotoViewer.this.parentChatActivity != null) {
                  ((MediaActivity)localObject1).setChatInfo(PhotoViewer.this.parentChatActivity.getCurrentChatInfo());
                }
                PhotoViewer.this.closePhoto(false, false);
                ((LaunchActivity)PhotoViewer.this.parentActivity).presentFragment((BaseFragment)localObject1, false, true);
              }
            }
            else
            {
              int i;
              if (paramAnonymousInt == 4)
              {
                if (PhotoViewer.this.currentMessageObject != null)
                {
                  localObject1 = new Bundle();
                  i = (int)PhotoViewer.this.currentDialogId;
                  paramAnonymousInt = (int)(PhotoViewer.this.currentDialogId >> 32);
                  if (i != 0) {
                    if (paramAnonymousInt == 1)
                    {
                      ((Bundle)localObject1).putInt("chat_id", i);
                      label526:
                      ((Bundle)localObject1).putInt("message_id", PhotoViewer.this.currentMessageObject.getId());
                      NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                      localObject2 = (LaunchActivity)PhotoViewer.this.parentActivity;
                      if ((((LaunchActivity)localObject2).getMainFragmentsCount() <= 1) && (!AndroidUtilities.isTablet())) {
                        break label725;
                      }
                    }
                  }
                  label725:
                  for (bool = true;; bool = false)
                  {
                    ((LaunchActivity)localObject2).presentFragment(new ChatActivity((Bundle)localObject1), bool, true);
                    PhotoViewer.access$8002(PhotoViewer.this, null);
                    PhotoViewer.this.closePhoto(false, false);
                    break;
                    if (i > 0)
                    {
                      ((Bundle)localObject1).putInt("user_id", i);
                      break label526;
                    }
                    if (i >= 0) {
                      break label526;
                    }
                    localObject2 = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-i));
                    paramAnonymousInt = i;
                    if (localObject2 != null)
                    {
                      paramAnonymousInt = i;
                      if (((TLRPC.Chat)localObject2).migrated_to != null)
                      {
                        ((Bundle)localObject1).putInt("migrated_to", i);
                        paramAnonymousInt = -((TLRPC.Chat)localObject2).migrated_to.channel_id;
                      }
                    }
                    ((Bundle)localObject1).putInt("chat_id", -paramAnonymousInt);
                    break label526;
                    ((Bundle)localObject1).putInt("enc_id", paramAnonymousInt);
                    break label526;
                  }
                }
              }
              else if (paramAnonymousInt == 3)
              {
                if ((PhotoViewer.this.currentMessageObject != null) && (PhotoViewer.this.parentActivity != null))
                {
                  ((LaunchActivity)PhotoViewer.this.parentActivity).switchToAccount(PhotoViewer.this.currentMessageObject.currentAccount, true);
                  localObject1 = new Bundle();
                  ((Bundle)localObject1).putBoolean("onlySelect", true);
                  ((Bundle)localObject1).putInt("dialogsType", 3);
                  localObject2 = new DialogsActivity((Bundle)localObject1);
                  localObject1 = new ArrayList();
                  ((ArrayList)localObject1).add(PhotoViewer.this.currentMessageObject);
                  ((DialogsActivity)localObject2).setDelegate(new DialogsActivity.DialogsActivityDelegate()
                  {
                    public void didSelectDialogs(DialogsActivity paramAnonymous2DialogsActivity, ArrayList<Long> paramAnonymous2ArrayList, CharSequence paramAnonymous2CharSequence, boolean paramAnonymous2Boolean)
                    {
                      int i;
                      long l;
                      if ((paramAnonymous2ArrayList.size() > 1) || (((Long)paramAnonymous2ArrayList.get(0)).longValue() == UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId()) || (paramAnonymous2CharSequence != null))
                      {
                        for (i = 0; i < paramAnonymous2ArrayList.size(); i++)
                        {
                          l = ((Long)paramAnonymous2ArrayList.get(i)).longValue();
                          if (paramAnonymous2CharSequence != null) {
                            SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(paramAnonymous2CharSequence.toString(), l, null, null, true, null, null, null);
                          }
                          SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(localObject1, l);
                        }
                        paramAnonymous2DialogsActivity.finishFragment();
                      }
                      for (;;)
                      {
                        return;
                        l = ((Long)paramAnonymous2ArrayList.get(0)).longValue();
                        int j = (int)l;
                        i = (int)(l >> 32);
                        paramAnonymous2ArrayList = new Bundle();
                        paramAnonymous2ArrayList.putBoolean("scrollToTopOnResume", true);
                        if (j != 0) {
                          if (j > 0) {
                            paramAnonymous2ArrayList.putInt("user_id", j);
                          }
                        }
                        for (;;)
                        {
                          NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                          paramAnonymous2ArrayList = new ChatActivity(paramAnonymous2ArrayList);
                          if (!((LaunchActivity)PhotoViewer.this.parentActivity).presentFragment(paramAnonymous2ArrayList, true, false)) {
                            break label294;
                          }
                          paramAnonymous2ArrayList.showReplyPanel(true, null, localObject1, null, false);
                          break;
                          if (j < 0)
                          {
                            paramAnonymous2ArrayList.putInt("chat_id", -j);
                            continue;
                            paramAnonymous2ArrayList.putInt("enc_id", i);
                          }
                        }
                        label294:
                        paramAnonymous2DialogsActivity.finishFragment();
                      }
                    }
                  });
                  ((LaunchActivity)PhotoViewer.this.parentActivity).presentFragment((BaseFragment)localObject2, false, true);
                  PhotoViewer.this.closePhoto(false, false);
                }
              }
              else if (paramAnonymousInt == 6)
              {
                if (PhotoViewer.this.parentActivity != null)
                {
                  AlertDialog.Builder localBuilder = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
                  label949:
                  final boolean[] arrayOfBoolean;
                  label1019:
                  FrameLayout localFrameLayout;
                  CheckBoxCell localCheckBoxCell;
                  if ((PhotoViewer.this.currentMessageObject != null) && (PhotoViewer.this.currentMessageObject.isVideo()))
                  {
                    localBuilder.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", NUM, new Object[0]));
                    localBuilder.setTitle(LocaleController.getString("AppName", NUM));
                    arrayOfBoolean = new boolean[1];
                    if (PhotoViewer.this.currentMessageObject != null)
                    {
                      paramAnonymousInt = (int)PhotoViewer.this.currentMessageObject.getDialogId();
                      if (paramAnonymousInt != 0)
                      {
                        if (paramAnonymousInt <= 0) {
                          break label1406;
                        }
                        localObject2 = MessagesController.getInstance(PhotoViewer.this.currentAccount).getUser(Integer.valueOf(paramAnonymousInt));
                        localObject1 = null;
                        if ((localObject2 != null) || (!ChatObject.isChannel((TLRPC.Chat)localObject1)))
                        {
                          paramAnonymousInt = ConnectionsManager.getInstance(PhotoViewer.this.currentAccount).getCurrentTime();
                          if (((localObject2 != null) && (((TLRPC.User)localObject2).id != UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId())) || ((localObject1 != null) && ((PhotoViewer.this.currentMessageObject.messageOwner.action == null) || ((PhotoViewer.this.currentMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty))) && (PhotoViewer.this.currentMessageObject.isOut()) && (paramAnonymousInt - PhotoViewer.this.currentMessageObject.messageOwner.date <= 172800)))
                          {
                            localFrameLayout = new FrameLayout(PhotoViewer.this.parentActivity);
                            localCheckBoxCell = new CheckBoxCell(PhotoViewer.this.parentActivity, 1);
                            localCheckBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            if (localObject1 == null) {
                              break label1430;
                            }
                            localCheckBoxCell.setText(LocaleController.getString("DeleteForAll", NUM), "", false, false);
                            label1206:
                            if (!LocaleController.isRTL) {
                              break label1463;
                            }
                            paramAnonymousInt = AndroidUtilities.dp(16.0F);
                            label1219:
                            if (!LocaleController.isRTL) {
                              break label1473;
                            }
                          }
                        }
                      }
                    }
                  }
                  label1406:
                  label1430:
                  label1463:
                  label1473:
                  for (i = AndroidUtilities.dp(8.0F);; i = AndroidUtilities.dp(16.0F))
                  {
                    localCheckBoxCell.setPadding(paramAnonymousInt, 0, i, 0);
                    localFrameLayout.addView(localCheckBoxCell, LayoutHelper.createFrame(-1, 48.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
                    localCheckBoxCell.setOnClickListener(new View.OnClickListener()
                    {
                      public void onClick(View paramAnonymous2View)
                      {
                        paramAnonymous2View = (CheckBoxCell)paramAnonymous2View;
                        boolean[] arrayOfBoolean = arrayOfBoolean;
                        if (arrayOfBoolean[0] == 0) {}
                        for (int i = 1;; i = 0)
                        {
                          arrayOfBoolean[0] = i;
                          paramAnonymous2View.setChecked(arrayOfBoolean[0], true);
                          return;
                        }
                      }
                    });
                    localBuilder.setView(localFrameLayout);
                    localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                    {
                      public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                      {
                        if (!PhotoViewer.this.imagesArr.isEmpty()) {
                          if ((PhotoViewer.this.currentIndex >= 0) && (PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size())) {}
                        }
                        for (;;)
                        {
                          return;
                          MessageObject localMessageObject = (MessageObject)PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                          if (localMessageObject.isSent())
                          {
                            PhotoViewer.this.closePhoto(false, false);
                            ArrayList localArrayList = new ArrayList();
                            if (PhotoViewer.this.slideshowMessageId != 0) {
                              localArrayList.add(Integer.valueOf(PhotoViewer.this.slideshowMessageId));
                            }
                            Object localObject2;
                            Object localObject3;
                            for (;;)
                            {
                              Object localObject1 = null;
                              localObject2 = null;
                              localObject3 = localObject1;
                              paramAnonymous2DialogInterface = (DialogInterface)localObject2;
                              if ((int)localMessageObject.getDialogId() == 0)
                              {
                                localObject3 = localObject1;
                                paramAnonymous2DialogInterface = (DialogInterface)localObject2;
                                if (localMessageObject.messageOwner.random_id != 0L)
                                {
                                  localObject3 = new ArrayList();
                                  ((ArrayList)localObject3).add(Long.valueOf(localMessageObject.messageOwner.random_id));
                                  paramAnonymous2DialogInterface = MessagesController.getInstance(PhotoViewer.this.currentAccount).getEncryptedChat(Integer.valueOf((int)(localMessageObject.getDialogId() >> 32)));
                                }
                              }
                              MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteMessages(localArrayList, (ArrayList)localObject3, paramAnonymous2DialogInterface, localMessageObject.messageOwner.to_id.channel_id, arrayOfBoolean[0]);
                              break;
                              localArrayList.add(Integer.valueOf(localMessageObject.getId()));
                            }
                            if ((!PhotoViewer.this.avatarsArr.isEmpty()) && (PhotoViewer.this.currentIndex >= 0) && (PhotoViewer.this.currentIndex < PhotoViewer.this.avatarsArr.size()))
                            {
                              localObject3 = (TLRPC.Photo)PhotoViewer.this.avatarsArr.get(PhotoViewer.this.currentIndex);
                              localObject2 = (TLRPC.FileLocation)PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
                              paramAnonymous2DialogInterface = (DialogInterface)localObject3;
                              if ((localObject3 instanceof TLRPC.TL_photoEmpty)) {
                                paramAnonymous2DialogInterface = null;
                              }
                              int i = 0;
                              paramAnonymous2Int = i;
                              if (PhotoViewer.this.currentUserAvatarLocation != null)
                              {
                                if (paramAnonymous2DialogInterface == null) {
                                  break label560;
                                }
                                localObject3 = paramAnonymous2DialogInterface.sizes.iterator();
                                do
                                {
                                  paramAnonymous2Int = i;
                                  if (!((Iterator)localObject3).hasNext()) {
                                    break;
                                  }
                                  localObject2 = (TLRPC.PhotoSize)((Iterator)localObject3).next();
                                } while ((((TLRPC.PhotoSize)localObject2).location.local_id != PhotoViewer.this.currentUserAvatarLocation.local_id) || (((TLRPC.PhotoSize)localObject2).location.volume_id != PhotoViewer.this.currentUserAvatarLocation.volume_id));
                                paramAnonymous2Int = 1;
                              }
                              for (;;)
                              {
                                if (paramAnonymous2Int == 0) {
                                  break label614;
                                }
                                MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(null);
                                PhotoViewer.this.closePhoto(false, false);
                                break;
                                label560:
                                paramAnonymous2Int = i;
                                if (((TLRPC.FileLocation)localObject2).local_id == PhotoViewer.this.currentUserAvatarLocation.local_id)
                                {
                                  paramAnonymous2Int = i;
                                  if (((TLRPC.FileLocation)localObject2).volume_id == PhotoViewer.this.currentUserAvatarLocation.volume_id) {
                                    paramAnonymous2Int = 1;
                                  }
                                }
                              }
                              label614:
                              if (paramAnonymous2DialogInterface != null)
                              {
                                localObject3 = new TLRPC.TL_inputPhoto();
                                ((TLRPC.TL_inputPhoto)localObject3).id = paramAnonymous2DialogInterface.id;
                                ((TLRPC.TL_inputPhoto)localObject3).access_hash = paramAnonymous2DialogInterface.access_hash;
                                MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto((TLRPC.InputPhoto)localObject3);
                                MessagesStorage.getInstance(PhotoViewer.this.currentAccount).clearUserPhoto(PhotoViewer.this.avatarsDialogId, paramAnonymous2DialogInterface.id);
                                PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                                PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                                PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                                if (PhotoViewer.this.imagesArrLocations.isEmpty())
                                {
                                  PhotoViewer.this.closePhoto(false, false);
                                }
                                else
                                {
                                  i = PhotoViewer.this.currentIndex;
                                  paramAnonymous2Int = i;
                                  if (i >= PhotoViewer.this.avatarsArr.size()) {
                                    paramAnonymous2Int = PhotoViewer.this.avatarsArr.size() - 1;
                                  }
                                  PhotoViewer.access$3002(PhotoViewer.this, -1);
                                  PhotoViewer.this.setImageIndex(paramAnonymous2Int, true);
                                }
                              }
                            }
                          }
                        }
                      }
                    });
                    localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    PhotoViewer.this.showAlertDialog(localBuilder);
                    break;
                    if ((PhotoViewer.this.currentMessageObject != null) && (PhotoViewer.this.currentMessageObject.isGif()))
                    {
                      localBuilder.setMessage(LocaleController.formatString("AreYouSure", NUM, new Object[0]));
                      break label949;
                    }
                    localBuilder.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", NUM, new Object[0]));
                    break label949;
                    localObject2 = null;
                    localObject1 = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-paramAnonymousInt));
                    break label1019;
                    localCheckBoxCell.setText(LocaleController.formatString("DeleteForUser", NUM, new Object[] { UserObject.getFirstName((TLRPC.User)localObject2) }), "", false, false);
                    break label1206;
                    paramAnonymousInt = AndroidUtilities.dp(8.0F);
                    break label1219;
                  }
                }
              }
              else if (paramAnonymousInt == 10)
              {
                PhotoViewer.this.onSharePressed();
              }
              else if (paramAnonymousInt == 11)
              {
                try
                {
                  AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                  PhotoViewer.this.closePhoto(false, false);
                }
                catch (Exception localException)
                {
                  FileLog.e(localException);
                }
              }
              else if (paramAnonymousInt == 13)
              {
                if ((PhotoViewer.this.parentActivity != null) && (PhotoViewer.this.currentMessageObject != null) && (PhotoViewer.this.currentMessageObject.messageOwner.media != null) && (PhotoViewer.this.currentMessageObject.messageOwner.media.photo != null)) {
                  new StickersAlert(PhotoViewer.this.parentActivity, PhotoViewer.this.currentMessageObject.messageOwner.media.photo).show();
                }
              }
              else if (paramAnonymousInt == 5)
              {
                if (PhotoViewer.this.pipItem.getAlpha() == 1.0F) {
                  PhotoViewer.this.switchToPip();
                }
              }
              else if ((paramAnonymousInt == 7) && (PhotoViewer.this.currentMessageObject != null))
              {
                FileLoader.getInstance(PhotoViewer.this.currentAccount).cancelLoadFile(PhotoViewer.this.currentMessageObject.getDocument());
                PhotoViewer.this.releasePlayer();
                PhotoViewer.this.bottomLayout.setTag(Integer.valueOf(1));
                PhotoViewer.this.bottomLayout.setVisibility(0);
              }
            }
          }
        }
      });
      paramActivity = this.actionBar.createMenu();
      this.masksItem = paramActivity.addItem(13, NUM);
      this.pipItem = paramActivity.addItem(5, NUM);
      this.sendItem = paramActivity.addItem(3, NUM);
      this.menuItem = paramActivity.addItem(0, NUM);
      this.menuItem.addSubItem(11, LocaleController.getString("OpenInExternalApp", NUM)).setTextColor(-328966);
      this.menuItem.addSubItem(2, LocaleController.getString("ShowAllMedia", NUM)).setTextColor(-328966);
      this.menuItem.addSubItem(4, LocaleController.getString("ShowInChat", NUM)).setTextColor(-328966);
      this.menuItem.addSubItem(10, LocaleController.getString("ShareFile", NUM)).setTextColor(-328966);
      this.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", NUM)).setTextColor(-328966);
      this.menuItem.addSubItem(6, LocaleController.getString("Delete", NUM)).setTextColor(-328966);
      this.menuItem.addSubItem(7, LocaleController.getString("StopDownload", NUM)).setTextColor(-328966);
      this.menuItem.redrawPopup(-115203550);
      this.bottomLayout = new FrameLayout(this.actvityContext);
      this.bottomLayout.setBackgroundColor(NUM);
      this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
      this.groupedPhotosListView = new GroupedPhotosListView(this.actvityContext);
      this.containerView.addView(this.groupedPhotosListView, LayoutHelper.createFrame(-1, 62.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
      this.captionTextView = createCaptionTextView();
      this.switchCaptionTextView = createCaptionTextView();
      for (i = 0; i < 3; i++)
      {
        this.photoProgressViews[i] = new PhotoProgressView(this.containerView.getContext(), this.containerView);
        this.photoProgressViews[i].setBackgroundState(0, false);
      }
      this.windowLayoutParams.flags = 8;
      break;
    }
    this.miniProgressView = new RadialProgressView(this.actvityContext)
    {
      public void invalidate()
      {
        super.invalidate();
        if (PhotoViewer.this.containerView != null) {
          PhotoViewer.this.containerView.invalidate();
        }
      }
      
      public void setAlpha(float paramAnonymousFloat)
      {
        super.setAlpha(paramAnonymousFloat);
        if (PhotoViewer.this.containerView != null) {
          PhotoViewer.this.containerView.invalidate();
        }
      }
    };
    this.miniProgressView.setUseSelfAlpha(true);
    this.miniProgressView.setProgressColor(-1);
    this.miniProgressView.setSize(AndroidUtilities.dp(54.0F));
    this.miniProgressView.setBackgroundResource(NUM);
    this.miniProgressView.setVisibility(4);
    this.miniProgressView.setAlpha(0.0F);
    this.containerView.addView(this.miniProgressView, LayoutHelper.createFrame(64, 64, 17));
    this.shareButton = new ImageView(this.containerView.getContext());
    this.shareButton.setImageResource(NUM);
    this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
    this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
    this.bottomLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1, 53));
    this.shareButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoViewer.this.onSharePressed();
      }
    });
    this.nameTextView = new TextView(this.containerView.getContext());
    this.nameTextView.setTextSize(1, 14.0F);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.nameTextView.setTextColor(-1);
    this.nameTextView.setGravity(3);
    this.bottomLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 16.0F, 5.0F, 60.0F, 0.0F));
    this.dateTextView = new TextView(this.containerView.getContext());
    this.dateTextView.setTextSize(1, 13.0F);
    this.dateTextView.setSingleLine(true);
    this.dateTextView.setMaxLines(1);
    this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.dateTextView.setTextColor(-1);
    this.dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.dateTextView.setGravity(3);
    this.bottomLayout.addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 16.0F, 25.0F, 50.0F, 0.0F));
    createVideoControlsInterface();
    this.progressView = new RadialProgressView(this.parentActivity);
    this.progressView.setProgressColor(-1);
    this.progressView.setBackgroundResource(NUM);
    this.progressView.setVisibility(4);
    this.containerView.addView(this.progressView, LayoutHelper.createFrame(54, 54, 17));
    this.qualityPicker = new PickerBottomLayoutViewer(this.parentActivity);
    this.qualityPicker.setBackgroundColor(NUM);
    this.qualityPicker.updateSelectedCount(0, false);
    this.qualityPicker.setTranslationY(AndroidUtilities.dp(120.0F));
    this.qualityPicker.doneButton.setText(LocaleController.getString("Done", NUM).toUpperCase());
    this.containerView.addView(this.qualityPicker, LayoutHelper.createFrame(-1, 48, 83));
    this.qualityPicker.cancelButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoViewer.access$9602(PhotoViewer.this, PhotoViewer.this.previousCompression);
        PhotoViewer.this.didChangedCompressionLevel(false);
        PhotoViewer.this.showQualityView(false);
        PhotoViewer.this.requestVideoPreview(2);
      }
    });
    this.qualityPicker.doneButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoViewer.this.showQualityView(false);
        PhotoViewer.this.requestVideoPreview(2);
      }
    });
    this.qualityChooseView = new QualityChooseView(this.parentActivity);
    this.qualityChooseView.setTranslationY(AndroidUtilities.dp(120.0F));
    this.qualityChooseView.setVisibility(4);
    this.qualityChooseView.setBackgroundColor(NUM);
    this.containerView.addView(this.qualityChooseView, LayoutHelper.createFrame(-1, 70.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
    this.pickerView = new FrameLayout(this.actvityContext)
    {
      public boolean dispatchTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((PhotoViewer.this.bottomTouchEnabled) && (super.dispatchTouchEvent(paramAnonymousMotionEvent))) {}
        for (boolean bool = true;; bool = false) {
          return bool;
        }
      }
      
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((PhotoViewer.this.bottomTouchEnabled) && (super.onInterceptTouchEvent(paramAnonymousMotionEvent))) {}
        for (boolean bool = true;; bool = false) {
          return bool;
        }
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramAnonymousMotionEvent))) {}
        for (boolean bool = true;; bool = false) {
          return bool;
        }
      }
    };
    this.pickerView.setBackgroundColor(NUM);
    this.containerView.addView(this.pickerView, LayoutHelper.createFrame(-1, -2, 83));
    this.videoTimelineView = new VideoTimelinePlayView(this.parentActivity);
    this.videoTimelineView.setDelegate(new VideoTimelinePlayView.VideoTimelineViewDelegate()
    {
      public void didStartDragging() {}
      
      public void didStopDragging() {}
      
      public void onLeftProgressChanged(float paramAnonymousFloat)
      {
        if (PhotoViewer.this.videoPlayer == null) {}
        for (;;)
        {
          return;
          if (PhotoViewer.this.videoPlayer.isPlaying())
          {
            PhotoViewer.this.videoPlayer.pause();
            PhotoViewer.this.containerView.invalidate();
          }
          PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoDuration * paramAnonymousFloat));
          PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0F);
          PhotoViewer.this.videoTimelineView.setProgress(0.0F);
          PhotoViewer.this.updateVideoInfo();
        }
      }
      
      public void onPlayProgressChanged(float paramAnonymousFloat)
      {
        if (PhotoViewer.this.videoPlayer == null) {}
        for (;;)
        {
          return;
          PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoDuration * paramAnonymousFloat));
        }
      }
      
      public void onRightProgressChanged(float paramAnonymousFloat)
      {
        if (PhotoViewer.this.videoPlayer == null) {}
        for (;;)
        {
          return;
          if (PhotoViewer.this.videoPlayer.isPlaying())
          {
            PhotoViewer.this.videoPlayer.pause();
            PhotoViewer.this.containerView.invalidate();
          }
          PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoDuration * paramAnonymousFloat));
          PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0F);
          PhotoViewer.this.videoTimelineView.setProgress(0.0F);
          PhotoViewer.this.updateVideoInfo();
        }
      }
    });
    this.pickerView.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 58.0F, 51, 0.0F, 8.0F, 0.0F, 88.0F));
    this.pickerViewSendButton = new ImageView(this.parentActivity);
    this.pickerViewSendButton.setScaleType(ImageView.ScaleType.CENTER);
    paramActivity = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), -10043398, -10043398);
    this.pickerViewSendButton.setBackgroundDrawable(paramActivity);
    this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
    this.pickerViewSendButton.setPadding(AndroidUtilities.dp(4.0F), 0, 0, 0);
    this.pickerViewSendButton.setImageResource(NUM);
    this.containerView.addView(this.pickerViewSendButton, LayoutHelper.createFrame(56, 56.0F, 85, 0.0F, 0.0F, 14.0F, 14.0F));
    this.pickerViewSendButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (PhotoViewer.this.captionEditText.getTag() != null) {}
        for (;;)
        {
          return;
          if ((PhotoViewer.this.placeProvider != null) && (!PhotoViewer.this.doneButtonPressed))
          {
            paramAnonymousView = PhotoViewer.this.getCurrentVideoEditedInfo();
            PhotoViewer.this.placeProvider.sendButtonPressed(PhotoViewer.this.currentIndex, paramAnonymousView);
            PhotoViewer.access$10402(PhotoViewer.this, true);
            PhotoViewer.this.closePhoto(false, false);
          }
        }
      }
    });
    this.itemsLayout = new LinearLayout(this.parentActivity);
    this.itemsLayout.setOrientation(0);
    this.pickerView.addView(this.itemsLayout, LayoutHelper.createFrame(-2, 48.0F, 81, 0.0F, 0.0F, 34.0F, 0.0F));
    this.cropItem = new ImageView(this.parentActivity);
    this.cropItem.setScaleType(ImageView.ScaleType.CENTER);
    this.cropItem.setImageResource(NUM);
    this.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
    this.itemsLayout.addView(this.cropItem, LayoutHelper.createLinear(70, 48));
    this.cropItem.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (PhotoViewer.this.captionEditText.getTag() != null) {}
        for (;;)
        {
          return;
          PhotoViewer.this.switchToEditMode(1);
        }
      }
    });
    this.paintItem = new ImageView(this.parentActivity);
    this.paintItem.setScaleType(ImageView.ScaleType.CENTER);
    this.paintItem.setImageResource(NUM);
    this.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
    this.itemsLayout.addView(this.paintItem, LayoutHelper.createLinear(70, 48));
    this.paintItem.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (PhotoViewer.this.captionEditText.getTag() != null) {}
        for (;;)
        {
          return;
          PhotoViewer.this.switchToEditMode(3);
        }
      }
    });
    this.compressItem = new ImageView(this.parentActivity);
    this.compressItem.setTag(Integer.valueOf(1));
    this.compressItem.setScaleType(ImageView.ScaleType.CENTER);
    this.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
    this.selectedCompression = MessagesController.getGlobalMainSettings().getInt("compress_video2", 1);
    label2157:
    Object localObject;
    if (this.selectedCompression <= 0)
    {
      this.compressItem.setImageResource(NUM);
      this.itemsLayout.addView(this.compressItem, LayoutHelper.createLinear(70, 48));
      this.compressItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (PhotoViewer.this.captionEditText.getTag() != null) {}
          for (;;)
          {
            return;
            PhotoViewer.this.showQualityView(true);
            PhotoViewer.this.requestVideoPreview(1);
          }
        }
      });
      this.muteItem = new ImageView(this.parentActivity);
      this.muteItem.setScaleType(ImageView.ScaleType.CENTER);
      this.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
      this.itemsLayout.addView(this.muteItem, LayoutHelper.createLinear(70, 48));
      this.muteItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (PhotoViewer.this.captionEditText.getTag() != null) {
            return;
          }
          paramAnonymousView = PhotoViewer.this;
          boolean bool;
          if (!PhotoViewer.this.muteVideo)
          {
            bool = true;
            label31:
            PhotoViewer.access$10702(paramAnonymousView, bool);
            if ((!PhotoViewer.this.muteVideo) || (PhotoViewer.this.checkImageView.isChecked())) {
              break label86;
            }
            PhotoViewer.this.checkImageView.callOnClick();
          }
          for (;;)
          {
            PhotoViewer.this.updateMuteButton();
            break;
            bool = false;
            break label31;
            label86:
            paramAnonymousView = PhotoViewer.this.imagesArrLocals.get(PhotoViewer.this.currentIndex);
            if ((paramAnonymousView instanceof MediaController.PhotoEntry)) {
              ((MediaController.PhotoEntry)paramAnonymousView).editedInfo = PhotoViewer.this.getCurrentVideoEditedInfo();
            }
          }
        }
      });
      this.cameraItem = new ImageView(this.parentActivity);
      this.cameraItem.setScaleType(ImageView.ScaleType.CENTER);
      this.cameraItem.setImageResource(NUM);
      this.cameraItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
      this.containerView.addView(this.cameraItem, LayoutHelper.createFrame(48, 48.0F, 85, 0.0F, 0.0F, 16.0F, 0.0F));
      this.cameraItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((PhotoViewer.this.placeProvider == null) || (PhotoViewer.this.captionEditText.getTag() != null)) {}
          for (;;)
          {
            return;
            PhotoViewer.this.placeProvider.needAddMorePhotos();
            PhotoViewer.this.closePhoto(true, false);
          }
        }
      });
      this.tuneItem = new ImageView(this.parentActivity);
      this.tuneItem.setScaleType(ImageView.ScaleType.CENTER);
      this.tuneItem.setImageResource(NUM);
      this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
      this.itemsLayout.addView(this.tuneItem, LayoutHelper.createLinear(70, 48));
      this.tuneItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (PhotoViewer.this.captionEditText.getTag() != null) {}
          for (;;)
          {
            return;
            PhotoViewer.this.switchToEditMode(2);
          }
        }
      });
      this.timeItem = new ImageView(this.parentActivity);
      this.timeItem.setScaleType(ImageView.ScaleType.CENTER);
      this.timeItem.setImageResource(NUM);
      this.timeItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
      this.itemsLayout.addView(this.timeItem, LayoutHelper.createLinear(70, 48));
      this.timeItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(final View paramAnonymousView)
        {
          if ((PhotoViewer.this.parentActivity == null) || (PhotoViewer.this.captionEditText.getTag() != null)) {
            return;
          }
          final Object localObject1 = new BottomSheet.Builder(PhotoViewer.this.parentActivity);
          ((BottomSheet.Builder)localObject1).setUseHardwareLayer(false);
          Object localObject2 = new LinearLayout(PhotoViewer.this.parentActivity);
          ((LinearLayout)localObject2).setOrientation(1);
          ((BottomSheet.Builder)localObject1).setCustomView((View)localObject2);
          paramAnonymousView = new TextView(PhotoViewer.this.parentActivity);
          paramAnonymousView.setLines(1);
          paramAnonymousView.setSingleLine(true);
          paramAnonymousView.setText(LocaleController.getString("MessageLifetime", NUM));
          paramAnonymousView.setTextColor(-1);
          paramAnonymousView.setTextSize(1, 16.0F);
          paramAnonymousView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
          paramAnonymousView.setPadding(AndroidUtilities.dp(21.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(21.0F), AndroidUtilities.dp(4.0F));
          paramAnonymousView.setGravity(16);
          ((LinearLayout)localObject2).addView(paramAnonymousView, LayoutHelper.createFrame(-1, -2.0F));
          paramAnonymousView.setOnTouchListener(new View.OnTouchListener()
          {
            public boolean onTouch(View paramAnonymous2View, MotionEvent paramAnonymous2MotionEvent)
            {
              return true;
            }
          });
          Object localObject3 = new TextView(PhotoViewer.this.parentActivity);
          label213:
          int i;
          if (PhotoViewer.this.isCurrentVideo)
          {
            paramAnonymousView = LocaleController.getString("MessageLifetimeVideo", NUM);
            ((TextView)localObject3).setText(paramAnonymousView);
            ((TextView)localObject3).setTextColor(-8355712);
            ((TextView)localObject3).setTextSize(1, 14.0F);
            ((TextView)localObject3).setEllipsize(TextUtils.TruncateAt.MIDDLE);
            ((TextView)localObject3).setPadding(AndroidUtilities.dp(21.0F), 0, AndroidUtilities.dp(21.0F), AndroidUtilities.dp(8.0F));
            ((TextView)localObject3).setGravity(16);
            ((LinearLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(-1, -2.0F));
            ((TextView)localObject3).setOnTouchListener(new View.OnTouchListener()
            {
              public boolean onTouch(View paramAnonymous2View, MotionEvent paramAnonymous2MotionEvent)
              {
                return true;
              }
            });
            paramAnonymousView = ((BottomSheet.Builder)localObject1).create();
            localObject1 = new NumberPicker(PhotoViewer.this.parentActivity);
            ((NumberPicker)localObject1).setMinValue(0);
            ((NumberPicker)localObject1).setMaxValue(28);
            localObject3 = PhotoViewer.this.imagesArrLocals.get(PhotoViewer.this.currentIndex);
            if (!(localObject3 instanceof MediaController.PhotoEntry)) {
              break label757;
            }
            i = ((MediaController.PhotoEntry)localObject3).ttl;
            label363:
            if (i != 0) {
              break label784;
            }
            ((NumberPicker)localObject1).setValue(MessagesController.getGlobalMainSettings().getInt("self_destruct", 7));
          }
          for (;;)
          {
            ((NumberPicker)localObject1).setTextColor(-1);
            ((NumberPicker)localObject1).setSelectorColor(-11711155);
            ((NumberPicker)localObject1).setFormatter(new NumberPicker.Formatter()
            {
              public String format(int paramAnonymous2Int)
              {
                String str;
                if (paramAnonymous2Int == 0) {
                  str = LocaleController.getString("ShortMessageLifetimeForever", NUM);
                }
                for (;;)
                {
                  return str;
                  if ((paramAnonymous2Int >= 1) && (paramAnonymous2Int < 21)) {
                    str = LocaleController.formatTTLString(paramAnonymous2Int);
                  } else {
                    str = LocaleController.formatTTLString((paramAnonymous2Int - 16) * 5);
                  }
                }
              }
            });
            ((LinearLayout)localObject2).addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
            localObject3 = new FrameLayout(PhotoViewer.this.parentActivity)
            {
              protected void onLayout(boolean paramAnonymous2Boolean, int paramAnonymous2Int1, int paramAnonymous2Int2, int paramAnonymous2Int3, int paramAnonymous2Int4)
              {
                paramAnonymous2Int4 = getChildCount();
                Object localObject = null;
                int i = paramAnonymous2Int3 - paramAnonymous2Int1;
                paramAnonymous2Int1 = 0;
                if (paramAnonymous2Int1 < paramAnonymous2Int4)
                {
                  View localView = getChildAt(paramAnonymous2Int1);
                  if (((Integer)localView.getTag()).intValue() == -1)
                  {
                    localObject = localView;
                    localView.layout(i - getPaddingRight() - localView.getMeasuredWidth(), getPaddingTop(), i - getPaddingRight() + localView.getMeasuredWidth(), getPaddingTop() + localView.getMeasuredHeight());
                  }
                  for (;;)
                  {
                    paramAnonymous2Int1++;
                    break;
                    if (((Integer)localView.getTag()).intValue() == -2)
                    {
                      paramAnonymous2Int3 = i - getPaddingRight() - localView.getMeasuredWidth();
                      paramAnonymous2Int2 = paramAnonymous2Int3;
                      if (localObject != null) {
                        paramAnonymous2Int2 = paramAnonymous2Int3 - (((View)localObject).getMeasuredWidth() + AndroidUtilities.dp(8.0F));
                      }
                      localView.layout(paramAnonymous2Int2, getPaddingTop(), localView.getMeasuredWidth() + paramAnonymous2Int2, getPaddingTop() + localView.getMeasuredHeight());
                    }
                    else
                    {
                      localView.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + localView.getMeasuredWidth(), getPaddingTop() + localView.getMeasuredHeight());
                    }
                  }
                }
              }
            };
            ((FrameLayout)localObject3).setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F));
            ((LinearLayout)localObject2).addView((View)localObject3, LayoutHelper.createLinear(-1, 52));
            localObject2 = new TextView(PhotoViewer.this.parentActivity);
            ((TextView)localObject2).setMinWidth(AndroidUtilities.dp(64.0F));
            ((TextView)localObject2).setTag(Integer.valueOf(-1));
            ((TextView)localObject2).setTextSize(1, 14.0F);
            ((TextView)localObject2).setTextColor(-11944718);
            ((TextView)localObject2).setGravity(17);
            ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            ((TextView)localObject2).setText(LocaleController.getString("Done", NUM).toUpperCase());
            ((TextView)localObject2).setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
            ((TextView)localObject2).setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
            ((FrameLayout)localObject3).addView((View)localObject2, LayoutHelper.createFrame(-2, 36, 53));
            ((TextView)localObject2).setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymous2View)
              {
                int i = localObject1.getValue();
                paramAnonymous2View = MessagesController.getGlobalMainSettings().edit();
                paramAnonymous2View.putInt("self_destruct", i);
                paramAnonymous2View.commit();
                paramAnonymousView.dismiss();
                label90:
                ImageView localImageView;
                if ((i >= 0) && (i < 21))
                {
                  paramAnonymous2View = PhotoViewer.this.imagesArrLocals.get(PhotoViewer.this.currentIndex);
                  if (!(paramAnonymous2View instanceof MediaController.PhotoEntry)) {
                    break label164;
                  }
                  ((MediaController.PhotoEntry)paramAnonymous2View).ttl = i;
                  localImageView = PhotoViewer.this.timeItem;
                  if (i == 0) {
                    break label182;
                  }
                }
                label164:
                label182:
                for (paramAnonymous2View = new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY);; paramAnonymous2View = null)
                {
                  localImageView.setColorFilter(paramAnonymous2View);
                  if (!PhotoViewer.this.checkImageView.isChecked()) {
                    PhotoViewer.this.checkImageView.callOnClick();
                  }
                  return;
                  i = (i - 16) * 5;
                  break;
                  if (!(paramAnonymous2View instanceof MediaController.SearchImage)) {
                    break label90;
                  }
                  ((MediaController.SearchImage)paramAnonymous2View).ttl = i;
                  break label90;
                }
              }
            });
            localObject2 = new TextView(PhotoViewer.this.parentActivity);
            ((TextView)localObject2).setMinWidth(AndroidUtilities.dp(64.0F));
            ((TextView)localObject2).setTag(Integer.valueOf(-2));
            ((TextView)localObject2).setTextSize(1, 14.0F);
            ((TextView)localObject2).setTextColor(-11944718);
            ((TextView)localObject2).setGravity(17);
            ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            ((TextView)localObject2).setText(LocaleController.getString("Cancel", NUM).toUpperCase());
            ((TextView)localObject2).setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
            ((TextView)localObject2).setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
            ((FrameLayout)localObject3).addView((View)localObject2, LayoutHelper.createFrame(-2, 36, 53));
            ((TextView)localObject2).setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymous2View)
              {
                paramAnonymousView.dismiss();
              }
            });
            paramAnonymousView.show();
            paramAnonymousView.setBackgroundColor(-16777216);
            break;
            paramAnonymousView = LocaleController.getString("MessageLifetimePhoto", NUM);
            break label213;
            label757:
            if ((localObject3 instanceof MediaController.SearchImage))
            {
              i = ((MediaController.SearchImage)localObject3).ttl;
              break label363;
            }
            i = 0;
            break label363;
            label784:
            if ((i >= 0) && (i < 21)) {
              ((NumberPicker)localObject1).setValue(i);
            } else {
              ((NumberPicker)localObject1).setValue(i / 5 + 21 - 5);
            }
          }
        }
      });
      this.editorDoneLayout = new PickerBottomLayoutViewer(this.actvityContext);
      this.editorDoneLayout.setBackgroundColor(NUM);
      this.editorDoneLayout.updateSelectedCount(0, false);
      this.editorDoneLayout.setVisibility(8);
      this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
      this.editorDoneLayout.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (PhotoViewer.this.currentEditMode == 1) {
            PhotoViewer.this.photoCropView.cancelAnimationRunnable();
          }
          PhotoViewer.this.switchToEditMode(0);
        }
      });
      this.editorDoneLayout.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((PhotoViewer.this.currentEditMode == 1) && (!PhotoViewer.this.photoCropView.isReady())) {}
          for (;;)
          {
            return;
            PhotoViewer.this.applyCurrentEditMode();
            PhotoViewer.this.switchToEditMode(0);
          }
        }
      });
      this.resetButton = new TextView(this.actvityContext);
      this.resetButton.setVisibility(8);
      this.resetButton.setTextSize(1, 14.0F);
      this.resetButton.setTextColor(-1);
      this.resetButton.setGravity(17);
      this.resetButton.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
      this.resetButton.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
      this.resetButton.setText(LocaleController.getString("Reset", NUM).toUpperCase());
      this.resetButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.editorDoneLayout.addView(this.resetButton, LayoutHelper.createFrame(-2, -1, 49));
      this.resetButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          PhotoViewer.this.photoCropView.reset();
        }
      });
      this.gestureDetector = new GestureDetector(this.containerView.getContext(), this);
      this.gestureDetector.setOnDoubleTapListener(this);
      paramActivity = new ImageReceiver.ImageReceiverDelegate()
      {
        public void didSetImage(ImageReceiver paramAnonymousImageReceiver, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
        {
          if ((paramAnonymousImageReceiver == PhotoViewer.this.centerImage) && (paramAnonymousBoolean1) && (!paramAnonymousBoolean2) && (PhotoViewer.this.currentEditMode == 1) && (PhotoViewer.this.photoCropView != null))
          {
            Bitmap localBitmap = paramAnonymousImageReceiver.getBitmap();
            if (localBitmap != null)
            {
              PhotoCropView localPhotoCropView = PhotoViewer.this.photoCropView;
              int i = paramAnonymousImageReceiver.getOrientation();
              if (PhotoViewer.this.sendPhotoType == 1) {
                break label159;
              }
              paramAnonymousBoolean2 = true;
              localPhotoCropView.setBitmap(localBitmap, i, paramAnonymousBoolean2);
            }
          }
          if ((paramAnonymousImageReceiver == PhotoViewer.this.centerImage) && (paramAnonymousBoolean1) && (PhotoViewer.this.placeProvider != null) && (PhotoViewer.this.placeProvider.scaleToFill()) && (!PhotoViewer.this.ignoreDidSetImage))
          {
            if (PhotoViewer.this.wasLayout) {
              break label164;
            }
            PhotoViewer.access$6702(PhotoViewer.this, true);
          }
          for (;;)
          {
            return;
            label159:
            paramAnonymousBoolean2 = false;
            break;
            label164:
            PhotoViewer.this.setScaleToFill();
          }
        }
      };
      this.centerImage.setParentView(this.containerView);
      this.centerImage.setCrossfadeAlpha((byte)2);
      this.centerImage.setInvalidateAll(true);
      this.centerImage.setDelegate(paramActivity);
      this.leftImage.setParentView(this.containerView);
      this.leftImage.setCrossfadeAlpha((byte)2);
      this.leftImage.setInvalidateAll(true);
      this.leftImage.setDelegate(paramActivity);
      this.rightImage.setParentView(this.containerView);
      this.rightImage.setCrossfadeAlpha((byte)2);
      this.rightImage.setInvalidateAll(true);
      this.rightImage.setDelegate(paramActivity);
      i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
      this.checkImageView = new CheckBox(this.containerView.getContext(), NUM)
      {
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramAnonymousMotionEvent))) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
      };
      this.checkImageView.setDrawBackground(true);
      this.checkImageView.setHasBorder(true);
      this.checkImageView.setSize(40);
      this.checkImageView.setCheckOffset(AndroidUtilities.dp(1.0F));
      this.checkImageView.setColor(-10043398, -1);
      this.checkImageView.setVisibility(8);
      paramActivity = this.containerView;
      localObject = this.checkImageView;
      if ((i != 3) && (i != 1)) {
        break label3732;
      }
      f = 58.0F;
      label3030:
      paramActivity.addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, 53, 0.0F, f, 10.0F, 0.0F));
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramActivity = (FrameLayout.LayoutParams)this.checkImageView.getLayoutParams();
        paramActivity.topMargin += AndroidUtilities.statusBarHeight;
      }
      this.checkImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (PhotoViewer.this.captionEditText.getTag() != null) {}
          for (;;)
          {
            return;
            PhotoViewer.this.setPhotoChecked();
          }
        }
      });
      this.photosCounterView = new CounterView(this.parentActivity);
      paramActivity = this.containerView;
      localObject = this.photosCounterView;
      if ((i != 3) && (i != 1)) {
        break label3740;
      }
    }
    label3732:
    label3740:
    for (float f = 58.0F;; f = 68.0F)
    {
      paramActivity.addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, 53, 0.0F, f, 66.0F, 0.0F));
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramActivity = (FrameLayout.LayoutParams)this.photosCounterView.getLayoutParams();
        paramActivity.topMargin += AndroidUtilities.statusBarHeight;
      }
      this.photosCounterView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((PhotoViewer.this.captionEditText.getTag() != null) || (PhotoViewer.this.placeProvider == null) || (PhotoViewer.this.placeProvider.getSelectedPhotosOrder() == null) || (PhotoViewer.this.placeProvider.getSelectedPhotosOrder().isEmpty())) {
            return;
          }
          paramAnonymousView = PhotoViewer.this;
          if (!PhotoViewer.this.isPhotosListViewVisible) {}
          for (boolean bool = true;; bool = false)
          {
            paramAnonymousView.togglePhotosListView(bool, true);
            break;
          }
        }
      });
      this.selectedPhotosListView = new RecyclerListView(this.parentActivity);
      this.selectedPhotosListView.setVisibility(8);
      this.selectedPhotosListView.setAlpha(0.0F);
      this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0F));
      this.selectedPhotosListView.addItemDecoration(new RecyclerView.ItemDecoration()
      {
        public void getItemOffsets(android.graphics.Rect paramAnonymousRect, View paramAnonymousView, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
        {
          int i = paramAnonymousRecyclerView.getChildAdapterPosition(paramAnonymousView);
          if (((paramAnonymousView instanceof PhotoPickerPhotoCell)) && (i == 0)) {}
          for (paramAnonymousRect.left = AndroidUtilities.dp(3.0F);; paramAnonymousRect.left = 0)
          {
            paramAnonymousRect.right = AndroidUtilities.dp(3.0F);
            return;
          }
        }
      });
      ((DefaultItemAnimator)this.selectedPhotosListView.getItemAnimator()).setDelayAnimations(false);
      this.selectedPhotosListView.setBackgroundColor(NUM);
      this.selectedPhotosListView.setPadding(0, AndroidUtilities.dp(3.0F), 0, AndroidUtilities.dp(3.0F));
      this.selectedPhotosListView.setLayoutManager(new LinearLayoutManager(this.parentActivity, 0, false)
      {
        public void smoothScrollToPosition(RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState, int paramAnonymousInt)
        {
          paramAnonymousRecyclerView = new LinearSmoothScrollerEnd(paramAnonymousRecyclerView.getContext());
          paramAnonymousRecyclerView.setTargetPosition(paramAnonymousInt);
          startSmoothScroll(paramAnonymousRecyclerView);
        }
      });
      localObject = this.selectedPhotosListView;
      paramActivity = new ListAdapter(this.parentActivity);
      this.selectedPhotosAdapter = paramActivity;
      ((RecyclerListView)localObject).setAdapter(paramActivity);
      this.containerView.addView(this.selectedPhotosListView, LayoutHelper.createFrame(-1, 88, 51));
      this.selectedPhotosListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          boolean bool;
          if ((paramAnonymousInt == 0) && (PhotoViewer.this.placeProvider.allowGroupPhotos()))
          {
            bool = SharedConfig.groupPhotosEnabled;
            SharedConfig.toggleGroupPhotosEnabled();
            PhotoViewer.this.placeProvider.toggleGroupPhotosEnabled();
            ImageView localImageView = (ImageView)paramAnonymousView;
            if (!bool)
            {
              paramAnonymousView = new PorterDuffColorFilter(-10043398, PorterDuff.Mode.MULTIPLY);
              localImageView.setColorFilter(paramAnonymousView);
              paramAnonymousView = PhotoViewer.this;
              if (bool) {
                break label90;
              }
              bool = true;
              label78:
              paramAnonymousView.showHint(false, bool);
            }
          }
          for (;;)
          {
            return;
            paramAnonymousView = null;
            break;
            label90:
            bool = false;
            break label78;
            PhotoViewer.access$11502(PhotoViewer.this, true);
            paramAnonymousInt = PhotoViewer.this.imagesArrLocals.indexOf(paramAnonymousView.getTag());
            if (paramAnonymousInt >= 0)
            {
              PhotoViewer.access$3002(PhotoViewer.this, -1);
              PhotoViewer.this.setImageIndex(paramAnonymousInt, true);
            }
            PhotoViewer.access$11502(PhotoViewer.this, false);
          }
        }
      });
      this.captionEditText = new PhotoViewerCaptionEnterView(this.actvityContext, this.containerView, this.windowView)
      {
        public boolean dispatchTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          bool1 = false;
          bool2 = bool1;
          try
          {
            if (!PhotoViewer.this.bottomTouchEnabled)
            {
              boolean bool3 = super.dispatchTouchEvent(paramAnonymousMotionEvent);
              bool2 = bool1;
              if (bool3) {
                bool2 = true;
              }
            }
          }
          catch (Exception paramAnonymousMotionEvent)
          {
            for (;;)
            {
              FileLog.e(paramAnonymousMotionEvent);
              bool2 = bool1;
            }
          }
          return bool2;
        }
        
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          bool1 = false;
          bool2 = bool1;
          try
          {
            if (!PhotoViewer.this.bottomTouchEnabled)
            {
              boolean bool3 = super.onInterceptTouchEvent(paramAnonymousMotionEvent);
              bool2 = bool1;
              if (bool3) {
                bool2 = true;
              }
            }
          }
          catch (Exception paramAnonymousMotionEvent)
          {
            for (;;)
            {
              FileLog.e(paramAnonymousMotionEvent);
              bool2 = bool1;
            }
          }
          return bool2;
        }
        
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((!PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramAnonymousMotionEvent))) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
      };
      this.captionEditText.setDelegate(new PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate()
      {
        public void onCaptionEnter()
        {
          PhotoViewer.this.closeCaptionEnter(true);
        }
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence)
        {
          if ((PhotoViewer.this.mentionsAdapter != null) && (PhotoViewer.this.captionEditText != null) && (PhotoViewer.this.parentChatActivity != null) && (paramAnonymousCharSequence != null)) {
            PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(paramAnonymousCharSequence.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages, false);
          }
        }
        
        public void onWindowSizeChanged(int paramAnonymousInt)
        {
          int i = Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount());
          int j;
          if (PhotoViewer.this.mentionsAdapter.getItemCount() > 3)
          {
            j = 18;
            j = AndroidUtilities.dp(j + i * 36);
            if (paramAnonymousInt - ActionBar.getCurrentActionBarHeight() * 2 >= j) {
              break label103;
            }
            PhotoViewer.access$12102(PhotoViewer.this, false);
            if ((PhotoViewer.this.mentionListView != null) && (PhotoViewer.this.mentionListView.getVisibility() == 0)) {
              PhotoViewer.this.mentionListView.setVisibility(4);
            }
          }
          for (;;)
          {
            return;
            j = 0;
            break;
            label103:
            PhotoViewer.access$12102(PhotoViewer.this, true);
            if ((PhotoViewer.this.mentionListView != null) && (PhotoViewer.this.mentionListView.getVisibility() == 4)) {
              PhotoViewer.this.mentionListView.setVisibility(0);
            }
          }
        }
      });
      this.containerView.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
      this.mentionListView = new RecyclerListView(this.actvityContext)
      {
        public boolean dispatchTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((!PhotoViewer.this.bottomTouchEnabled) && (super.dispatchTouchEvent(paramAnonymousMotionEvent))) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
        
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((!PhotoViewer.this.bottomTouchEnabled) && (super.onInterceptTouchEvent(paramAnonymousMotionEvent))) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
        
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((!PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramAnonymousMotionEvent))) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
      };
      this.mentionListView.setTag(Integer.valueOf(5));
      this.mentionLayoutManager = new LinearLayoutManager(this.actvityContext)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.mentionLayoutManager.setOrientation(1);
      this.mentionListView.setLayoutManager(this.mentionLayoutManager);
      this.mentionListView.setBackgroundColor(NUM);
      this.mentionListView.setVisibility(8);
      this.mentionListView.setClipToPadding(true);
      this.mentionListView.setOverScrollMode(2);
      this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
      localObject = this.mentionListView;
      paramActivity = new MentionsAdapter(this.actvityContext, true, 0L, new MentionsAdapter.MentionsAdapterDelegate()
      {
        public void needChangePanelVisibility(boolean paramAnonymousBoolean)
        {
          int j;
          if (paramAnonymousBoolean)
          {
            FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)PhotoViewer.this.mentionListView.getLayoutParams();
            int i = Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount());
            if (PhotoViewer.this.mentionsAdapter.getItemCount() > 3)
            {
              j = 18;
              j = i * 36 + j;
              localLayoutParams.height = AndroidUtilities.dp(j);
              localLayoutParams.topMargin = (-AndroidUtilities.dp(j));
              PhotoViewer.this.mentionListView.setLayoutParams(localLayoutParams);
              if (PhotoViewer.this.mentionListAnimation != null)
              {
                PhotoViewer.this.mentionListAnimation.cancel();
                PhotoViewer.access$12202(PhotoViewer.this, null);
              }
              if (PhotoViewer.this.mentionListView.getVisibility() != 0) {
                break label152;
              }
              PhotoViewer.this.mentionListView.setAlpha(1.0F);
            }
          }
          for (;;)
          {
            return;
            j = 0;
            break;
            label152:
            PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
            if (PhotoViewer.this.allowMentions)
            {
              PhotoViewer.this.mentionListView.setVisibility(0);
              PhotoViewer.access$12202(PhotoViewer.this, new AnimatorSet());
              PhotoViewer.this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[] { 0.0F, 1.0F }) });
              PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnonymous2Animator)
                {
                  if ((PhotoViewer.this.mentionListAnimation != null) && (PhotoViewer.this.mentionListAnimation.equals(paramAnonymous2Animator))) {
                    PhotoViewer.access$12202(PhotoViewer.this, null);
                  }
                }
              });
              PhotoViewer.this.mentionListAnimation.setDuration(200L);
              PhotoViewer.this.mentionListAnimation.start();
            }
            else
            {
              PhotoViewer.this.mentionListView.setAlpha(1.0F);
              PhotoViewer.this.mentionListView.setVisibility(4);
              continue;
              if (PhotoViewer.this.mentionListAnimation != null)
              {
                PhotoViewer.this.mentionListAnimation.cancel();
                PhotoViewer.access$12202(PhotoViewer.this, null);
              }
              if (PhotoViewer.this.mentionListView.getVisibility() != 8) {
                if (PhotoViewer.this.allowMentions)
                {
                  PhotoViewer.access$12202(PhotoViewer.this, new AnimatorSet());
                  PhotoViewer.this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[] { 0.0F }) });
                  PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter()
                  {
                    public void onAnimationEnd(Animator paramAnonymous2Animator)
                    {
                      if ((PhotoViewer.this.mentionListAnimation != null) && (PhotoViewer.this.mentionListAnimation.equals(paramAnonymous2Animator)))
                      {
                        PhotoViewer.this.mentionListView.setVisibility(8);
                        PhotoViewer.access$12202(PhotoViewer.this, null);
                      }
                    }
                  });
                  PhotoViewer.this.mentionListAnimation.setDuration(200L);
                  PhotoViewer.this.mentionListAnimation.start();
                }
                else
                {
                  PhotoViewer.this.mentionListView.setVisibility(8);
                }
              }
            }
          }
        }
        
        public void onContextClick(TLRPC.BotInlineResult paramAnonymousBotInlineResult) {}
        
        public void onContextSearch(boolean paramAnonymousBoolean) {}
      });
      this.mentionsAdapter = paramActivity;
      ((RecyclerListView)localObject).setAdapter(paramActivity);
      this.mentionListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          paramAnonymousView = PhotoViewer.this.mentionsAdapter.getItem(paramAnonymousInt);
          int i = PhotoViewer.this.mentionsAdapter.getResultStartPosition();
          paramAnonymousInt = PhotoViewer.this.mentionsAdapter.getResultLength();
          if ((paramAnonymousView instanceof TLRPC.User))
          {
            paramAnonymousView = (TLRPC.User)paramAnonymousView;
            if (paramAnonymousView.username != null) {
              PhotoViewer.this.captionEditText.replaceWithText(i, paramAnonymousInt, "@" + paramAnonymousView.username + " ", false);
            }
          }
          for (;;)
          {
            return;
            Object localObject = UserObject.getFirstName(paramAnonymousView);
            localObject = new SpannableString((String)localObject + " ");
            ((Spannable)localObject).setSpan(new URLSpanUserMentionPhotoViewer("" + paramAnonymousView.id, true), 0, ((Spannable)localObject).length(), 33);
            PhotoViewer.this.captionEditText.replaceWithText(i, paramAnonymousInt, (CharSequence)localObject, false);
            continue;
            if ((paramAnonymousView instanceof String))
            {
              PhotoViewer.this.captionEditText.replaceWithText(i, paramAnonymousInt, paramAnonymousView + " ", false);
            }
            else if ((paramAnonymousView instanceof EmojiSuggestion))
            {
              paramAnonymousView = ((EmojiSuggestion)paramAnonymousView).emoji;
              PhotoViewer.this.captionEditText.addEmojiToRecent(paramAnonymousView);
              PhotoViewer.this.captionEditText.replaceWithText(i, paramAnonymousInt, paramAnonymousView, true);
            }
          }
        }
      });
      this.mentionListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((PhotoViewer.this.mentionsAdapter.getItem(paramAnonymousInt) instanceof String))
          {
            paramAnonymousView = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
            paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
            paramAnonymousView.setMessage(LocaleController.getString("ClearSearch", NUM));
            paramAnonymousView.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                PhotoViewer.this.mentionsAdapter.clearRecentHashtags();
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            PhotoViewer.this.showAlertDialog(paramAnonymousView);
          }
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
      });
      break;
      if (this.selectedCompression == 1)
      {
        this.compressItem.setImageResource(NUM);
        break label2157;
      }
      if (this.selectedCompression == 2)
      {
        this.compressItem.setImageResource(NUM);
        break label2157;
      }
      if (this.selectedCompression == 3)
      {
        this.compressItem.setImageResource(NUM);
        break label2157;
      }
      if (this.selectedCompression != 4) {
        break label2157;
      }
      this.compressItem.setImageResource(NUM);
      break label2157;
      f = 68.0F;
      break label3030;
    }
  }
  
  public void setParentAlert(ChatAttachAlert paramChatAttachAlert)
  {
    this.parentAlert = paramChatAttachAlert;
  }
  
  public void setParentChatActivity(ChatActivity paramChatActivity)
  {
    this.parentChatActivity = paramChatActivity;
  }
  
  public void showAlertDialog(AlertDialog.Builder paramBuilder)
  {
    if (this.parentActivity == null) {}
    for (;;)
    {
      return;
      try
      {
        if (this.visibleDialog != null)
        {
          this.visibleDialog.dismiss();
          this.visibleDialog = null;
        }
        try
        {
          this.visibleDialog = paramBuilder.show();
          this.visibleDialog.setCanceledOnTouchOutside(true);
          AlertDialog localAlertDialog = this.visibleDialog;
          paramBuilder = new org/telegram/ui/PhotoViewer$49;
          paramBuilder.<init>(this);
          localAlertDialog.setOnDismissListener(paramBuilder);
        }
        catch (Exception paramBuilder)
        {
          FileLog.e(paramBuilder);
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
  }
  
  public void updateMuteButton()
  {
    if (this.videoPlayer != null) {
      this.videoPlayer.setMute(this.muteVideo);
    }
    if (!this.videoHasAudio)
    {
      this.muteItem.setEnabled(false);
      this.muteItem.setClickable(false);
      this.muteItem.setAlpha(0.5F);
    }
    for (;;)
    {
      return;
      this.muteItem.setEnabled(true);
      this.muteItem.setClickable(true);
      this.muteItem.setAlpha(1.0F);
      if (this.muteVideo)
      {
        this.actionBar.setSubtitle(null);
        this.muteItem.setImageResource(NUM);
        this.muteItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
        if (this.compressItem.getTag() != null)
        {
          this.compressItem.setClickable(false);
          this.compressItem.setAlpha(0.5F);
          this.compressItem.setEnabled(false);
        }
        this.videoTimelineView.setMaxProgressDiff(30000.0F / this.videoDuration);
      }
      else
      {
        this.muteItem.setColorFilter(null);
        this.actionBar.setSubtitle(this.currentSubtitle);
        this.muteItem.setImageResource(NUM);
        if (this.compressItem.getTag() != null)
        {
          this.compressItem.setClickable(true);
          this.compressItem.setAlpha(1.0F);
          this.compressItem.setEnabled(true);
        }
        this.videoTimelineView.setMaxProgressDiff(1.0F);
      }
    }
  }
  
  private class BackgroundDrawable
    extends ColorDrawable
  {
    private boolean allowDrawContent;
    private Runnable drawRunnable;
    
    public BackgroundDrawable(int paramInt)
    {
      super();
    }
    
    public void draw(Canvas paramCanvas)
    {
      super.draw(paramCanvas);
      if ((getAlpha() != 0) && (this.drawRunnable != null))
      {
        AndroidUtilities.runOnUIThread(this.drawRunnable);
        this.drawRunnable = null;
      }
    }
    
    @Keep
    public void setAlpha(int paramInt)
    {
      boolean bool;
      if ((PhotoViewer.this.parentActivity instanceof LaunchActivity))
      {
        if ((PhotoViewer.this.isVisible) && (paramInt == 255)) {
          break label94;
        }
        bool = true;
        this.allowDrawContent = bool;
        ((LaunchActivity)PhotoViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(this.allowDrawContent);
        if (PhotoViewer.this.parentAlert != null)
        {
          if (this.allowDrawContent) {
            break label99;
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (PhotoViewer.this.parentAlert != null) {
                PhotoViewer.this.parentAlert.setAllowDrawContent(PhotoViewer.BackgroundDrawable.this.allowDrawContent);
              }
            }
          }, 50L);
        }
      }
      for (;;)
      {
        super.setAlpha(paramInt);
        return;
        label94:
        bool = false;
        break;
        label99:
        if (PhotoViewer.this.parentAlert != null) {
          PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
        }
      }
    }
  }
  
  private class CounterView
    extends View
  {
    private int currentCount = 0;
    private int height;
    private Paint paint;
    private RectF rect;
    private float rotation;
    private StaticLayout staticLayout;
    private TextPaint textPaint = new TextPaint(1);
    private int width;
    
    public CounterView(Context paramContext)
    {
      super();
      this.textPaint.setTextSize(AndroidUtilities.dp(18.0F));
      this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.textPaint.setColor(-1);
      this.paint = new Paint(1);
      this.paint.setColor(-1);
      this.paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
      this.paint.setStyle(Paint.Style.STROKE);
      this.paint.setStrokeJoin(Paint.Join.ROUND);
      this.rect = new RectF();
      setCount(0);
    }
    
    public float getRotationX()
    {
      return this.rotation;
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int i = getMeasuredHeight() / 2;
      this.paint.setAlpha(255);
      this.rect.set(AndroidUtilities.dp(1.0F), i - AndroidUtilities.dp(14.0F), getMeasuredWidth() - AndroidUtilities.dp(1.0F), AndroidUtilities.dp(14.0F) + i);
      paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(15.0F), AndroidUtilities.dp(15.0F), this.paint);
      if (this.staticLayout != null)
      {
        this.textPaint.setAlpha((int)((1.0F - this.rotation) * 255.0F));
        paramCanvas.save();
        paramCanvas.translate((getMeasuredWidth() - this.width) / 2, (getMeasuredHeight() - this.height) / 2 + AndroidUtilities.dpf2(0.2F) + this.rotation * AndroidUtilities.dp(5.0F));
        this.staticLayout.draw(paramCanvas);
        paramCanvas.restore();
        this.paint.setAlpha((int)(this.rotation * 255.0F));
        int j = (int)this.rect.centerX();
        i = (int)((int)this.rect.centerY() - (AndroidUtilities.dp(5.0F) * (1.0F - this.rotation) + AndroidUtilities.dp(3.0F)));
        paramCanvas.drawLine(AndroidUtilities.dp(0.5F) + j, i - AndroidUtilities.dp(0.5F), j - AndroidUtilities.dp(6.0F), AndroidUtilities.dp(6.0F) + i, this.paint);
        paramCanvas.drawLine(j - AndroidUtilities.dp(0.5F), i - AndroidUtilities.dp(0.5F), AndroidUtilities.dp(6.0F) + j, AndroidUtilities.dp(6.0F) + i, this.paint);
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.max(this.width + AndroidUtilities.dp(20.0F), AndroidUtilities.dp(30.0F)), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0F), NUM));
    }
    
    public void setCount(int paramInt)
    {
      this.staticLayout = new StaticLayout("" + Math.max(1, paramInt), this.textPaint, AndroidUtilities.dp(100.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      this.width = ((int)Math.ceil(this.staticLayout.getLineWidth(0)));
      this.height = this.staticLayout.getLineBottom(0);
      AnimatorSet localAnimatorSet = new AnimatorSet();
      if (paramInt == 0)
      {
        localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "scaleX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this, "scaleY", new float[] { 0.0F }), ObjectAnimator.ofInt(this.paint, "alpha", new int[] { 0 }), ObjectAnimator.ofInt(this.textPaint, "alpha", new int[] { 0 }) });
        localAnimatorSet.setInterpolator(new DecelerateInterpolator());
      }
      for (;;)
      {
        localAnimatorSet.setDuration(180L);
        localAnimatorSet.start();
        requestLayout();
        this.currentCount = paramInt;
        return;
        if (this.currentCount == 0)
        {
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "scaleX", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this, "scaleY", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofInt(this.paint, "alpha", new int[] { 0, 255 }), ObjectAnimator.ofInt(this.textPaint, "alpha", new int[] { 0, 255 }) });
          localAnimatorSet.setInterpolator(new DecelerateInterpolator());
        }
        else if (paramInt < this.currentCount)
        {
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "scaleX", new float[] { 1.1F, 1.0F }), ObjectAnimator.ofFloat(this, "scaleY", new float[] { 1.1F, 1.0F }) });
          localAnimatorSet.setInterpolator(new OvershootInterpolator());
        }
        else
        {
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "scaleX", new float[] { 0.9F, 1.0F }), ObjectAnimator.ofFloat(this, "scaleY", new float[] { 0.9F, 1.0F }) });
          localAnimatorSet.setInterpolator(new OvershootInterpolator());
        }
      }
    }
    
    @Keep
    public void setRotationX(float paramFloat)
    {
      this.rotation = paramFloat;
      invalidate();
    }
    
    @Keep
    public void setScaleX(float paramFloat)
    {
      super.setScaleX(paramFloat);
      invalidate();
    }
  }
  
  public static class EmptyPhotoViewerProvider
    implements PhotoViewer.PhotoViewerProvider
  {
    public boolean allowCaption()
    {
      return true;
    }
    
    public boolean allowGroupPhotos()
    {
      return true;
    }
    
    public boolean canScrollAway()
    {
      return true;
    }
    
    public boolean cancelButtonPressed()
    {
      return true;
    }
    
    public int getPhotoIndex(int paramInt)
    {
      return -1;
    }
    
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
    {
      return null;
    }
    
    public int getSelectedCount()
    {
      return 0;
    }
    
    public HashMap<Object, Object> getSelectedPhotos()
    {
      return null;
    }
    
    public ArrayList<Object> getSelectedPhotosOrder()
    {
      return null;
    }
    
    public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
    {
      return null;
    }
    
    public boolean isPhotoChecked(int paramInt)
    {
      return false;
    }
    
    public void needAddMorePhotos() {}
    
    public boolean scaleToFill()
    {
      return false;
    }
    
    public void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo) {}
    
    public int setPhotoChecked(int paramInt, VideoEditedInfo paramVideoEditedInfo)
    {
      return -1;
    }
    
    public void toggleGroupPhotosEnabled() {}
    
    public void updatePhotoAtIndex(int paramInt) {}
    
    public void willHidePhotoViewer() {}
    
    public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt) {}
  }
  
  private class FrameLayoutDrawer
    extends SizeNotifierFrameLayoutPhoto
  {
    private Paint paint = new Paint();
    
    public FrameLayoutDrawer(Context paramContext)
    {
      super();
      setWillNotDraw(false);
      this.paint.setColor(855638016);
    }
    
    protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
    {
      boolean bool1 = false;
      boolean bool2;
      if ((paramView == PhotoViewer.this.mentionListView) || (paramView == PhotoViewer.this.captionEditText))
      {
        if ((PhotoViewer.this.captionEditText.isPopupShowing()) || (PhotoViewer.this.captionEditText.getEmojiPadding() != 0)) {
          break label261;
        }
        if (AndroidUtilities.usingHardwareInput)
        {
          bool2 = bool1;
          if (PhotoViewer.this.captionEditText.getTag() == null) {}
        }
        else
        {
          if (getKeyboardHeight() != 0) {
            break label261;
          }
          bool2 = bool1;
        }
      }
      for (;;)
      {
        return bool2;
        if ((paramView == PhotoViewer.this.cameraItem) || (paramView == PhotoViewer.this.pickerView) || (paramView == PhotoViewer.this.pickerViewSendButton) || (paramView == PhotoViewer.this.captionTextView) || ((PhotoViewer.this.muteItem.getVisibility() == 0) && (paramView == PhotoViewer.this.bottomLayout)))
        {
          if ((getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) && (!AndroidUtilities.isInMultiwindow)) {}
          for (int i = PhotoViewer.this.captionEditText.getEmojiPadding();; i = 0)
          {
            if ((!PhotoViewer.this.captionEditText.isPopupShowing()) && ((!AndroidUtilities.usingHardwareInput) || (PhotoViewer.this.captionEditText.getTag() == null)) && (getKeyboardHeight() <= 0) && (i == 0)) {
              break label252;
            }
            PhotoViewer.access$5802(PhotoViewer.this, false);
            bool2 = bool1;
            break;
          }
          label252:
          PhotoViewer.access$5802(PhotoViewer.this, true);
        }
        label261:
        label363:
        do
        {
          for (;;)
          {
            bool2 = bool1;
            try
            {
              if (paramView == PhotoViewer.this.aspectRatioFrameLayout) {
                break;
              }
              boolean bool3 = super.drawChild(paramCanvas, paramView, paramLong);
              bool2 = bool1;
              if (!bool3) {
                break;
              }
              bool2 = true;
            }
            catch (Throwable paramCanvas)
            {
              bool2 = true;
            }
            if ((paramView != PhotoViewer.this.checkImageView) && (paramView != PhotoViewer.this.photosCounterView)) {
              break label363;
            }
            if (PhotoViewer.this.captionEditText.getTag() != null)
            {
              PhotoViewer.access$5802(PhotoViewer.this, false);
              bool2 = bool1;
              break;
            }
            PhotoViewer.access$5802(PhotoViewer.this, true);
          }
        } while (paramView != PhotoViewer.this.miniProgressView);
        bool2 = bool1;
      }
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      PhotoViewer.this.onDraw(paramCanvas);
      if ((Build.VERSION.SDK_INT >= 21) && (AndroidUtilities.statusBarHeight != 0) && (PhotoViewer.this.actionBar != null))
      {
        this.paint.setAlpha((int)(255.0F * PhotoViewer.this.actionBar.getAlpha() * 0.2F));
        paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), AndroidUtilities.statusBarHeight, this.paint);
      }
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = getChildCount();
      if ((getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) && (!AndroidUtilities.isInMultiwindow)) {}
      View localView;
      for (int j = PhotoViewer.this.captionEditText.getEmojiPadding();; j = 0) {
        for (int k = 0;; k++)
        {
          if (k >= i) {
            break label607;
          }
          localView = getChildAt(k);
          if (localView.getVisibility() != 8) {
            break;
          }
        }
      }
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
      int m = localView.getMeasuredWidth();
      int n = localView.getMeasuredHeight();
      int i1 = localLayoutParams.gravity;
      int i2 = i1;
      if (i1 == -1) {
        i2 = 51;
      }
      switch (i2 & 0x7 & 0x7)
      {
      default: 
        i1 = localLayoutParams.leftMargin;
        label163:
        switch (i2 & 0x70)
        {
        default: 
          i2 = localLayoutParams.topMargin;
          label211:
          if (localView == PhotoViewer.this.mentionListView) {
            i2 -= PhotoViewer.this.captionEditText.getMeasuredHeight();
          }
          break;
        }
        break;
      }
      for (;;)
      {
        localView.layout(i1, i2, i1 + m, i2 + n);
        break;
        i1 = (paramInt3 - paramInt1 - m) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
        break label163;
        i1 = paramInt3 - paramInt1 - m - localLayoutParams.rightMargin;
        break label163;
        i2 = localLayoutParams.topMargin;
        break label211;
        i2 = (paramInt4 - j - paramInt2 - n) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
        break label211;
        i2 = paramInt4 - j - paramInt2 - n - localLayoutParams.bottomMargin;
        break label211;
        if (PhotoViewer.this.captionEditText.isPopupView(localView))
        {
          if (AndroidUtilities.isInMultiwindow) {
            i2 = PhotoViewer.this.captionEditText.getTop() - localView.getMeasuredHeight() + AndroidUtilities.dp(1.0F);
          } else {
            i2 = PhotoViewer.this.captionEditText.getBottom();
          }
        }
        else if (localView == PhotoViewer.this.selectedPhotosListView)
        {
          i2 = PhotoViewer.this.actionBar.getMeasuredHeight();
        }
        else if ((localView == PhotoViewer.this.captionTextView) || (localView == PhotoViewer.this.switchCaptionTextView))
        {
          int i3 = 0;
          if (!PhotoViewer.GroupedPhotosListView.access$5000(PhotoViewer.this.groupedPhotosListView).isEmpty()) {
            i3 = 0 + PhotoViewer.this.groupedPhotosListView.getMeasuredHeight();
          }
          i2 -= i3;
        }
        else if ((PhotoViewer.this.hintTextView != null) && (localView == PhotoViewer.this.hintTextView))
        {
          i2 = PhotoViewer.this.selectedPhotosListView.getBottom() + AndroidUtilities.dp(3.0F);
        }
        else if (localView == PhotoViewer.this.cameraItem)
        {
          i2 = PhotoViewer.this.pickerView.getTop() - AndroidUtilities.dp(15.0F) - PhotoViewer.this.cameraItem.getMeasuredHeight();
        }
      }
      label607:
      notifyHeightChanged();
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      int j = View.MeasureSpec.getSize(paramInt2);
      setMeasuredDimension(i, j);
      measureChildWithMargins(PhotoViewer.this.captionEditText, paramInt1, 0, paramInt2, 0);
      int k = PhotoViewer.this.captionEditText.getMeasuredHeight();
      int m = getChildCount();
      int n = 0;
      if (n < m)
      {
        View localView = getChildAt(n);
        if ((localView.getVisibility() == 8) || (localView == PhotoViewer.this.captionEditText)) {}
        for (;;)
        {
          n++;
          break;
          if (localView == PhotoViewer.this.aspectRatioFrameLayout)
          {
            int i1 = AndroidUtilities.displaySize.y;
            if (Build.VERSION.SDK_INT >= 21) {}
            for (int i2 = AndroidUtilities.statusBarHeight;; i2 = 0)
            {
              measureChildWithMargins(localView, paramInt1, 0, View.MeasureSpec.makeMeasureSpec(i2 + i1, NUM), 0);
              break;
            }
          }
          if (PhotoViewer.this.captionEditText.isPopupView(localView))
          {
            if (AndroidUtilities.isInMultiwindow)
            {
              if (AndroidUtilities.isTablet()) {
                localView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0F), j - k - AndroidUtilities.statusBarHeight), NUM));
              } else {
                localView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(j - k - AndroidUtilities.statusBarHeight, NUM));
              }
            }
            else {
              localView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(localView.getLayoutParams().height, NUM));
            }
          }
          else {
            measureChildWithMargins(localView, paramInt1, 0, paramInt2, 0);
          }
        }
      }
    }
  }
  
  private class GroupedPhotosListView
    extends View
    implements GestureDetector.OnGestureListener
  {
    private boolean animateAllLine;
    private int animateToDX;
    private int animateToDXStart;
    private int animateToItem = -1;
    private Paint backgroundPaint = new Paint();
    private long currentGroupId;
    private int currentImage;
    private float currentItemProgress = 1.0F;
    private ArrayList<Object> currentObjects = new ArrayList();
    private ArrayList<TLObject> currentPhotos = new ArrayList();
    private int drawDx;
    private GestureDetector gestureDetector = new GestureDetector(paramContext, this);
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
    private float nextItemProgress = 0.0F;
    private int nextPhotoScrolling = -1;
    private Scroller scroll;
    private boolean scrolling;
    private boolean stopedScrolling;
    private ArrayList<ImageReceiver> unusedReceivers = new ArrayList();
    
    public GroupedPhotosListView(Context paramContext)
    {
      super();
      this.scroll = new Scroller(paramContext);
      this.itemWidth = AndroidUtilities.dp(42.0F);
      this.itemHeight = AndroidUtilities.dp(56.0F);
      this.itemSpacing = AndroidUtilities.dp(1.0F);
      this.itemY = AndroidUtilities.dp(3.0F);
      this.backgroundPaint.setColor(NUM);
    }
    
    private void fillImages(boolean paramBoolean, int paramInt)
    {
      if ((!paramBoolean) && (!this.imagesToDraw.isEmpty()))
      {
        this.unusedReceivers.addAll(this.imagesToDraw);
        this.imagesToDraw.clear();
        this.moving = false;
        this.moveLineProgress = 1.0F;
        this.currentItemProgress = 1.0F;
        this.nextItemProgress = 0.0F;
      }
      invalidate();
      if ((getMeasuredWidth() == 0) || (this.currentPhotos.isEmpty())) {}
      label575:
      for (;;)
      {
        return;
        int i = getMeasuredWidth();
        int j = getMeasuredWidth() / 2 - this.itemWidth / 2;
        int k;
        int m;
        int i1;
        Object localObject1;
        if (paramBoolean)
        {
          k = Integer.MIN_VALUE;
          m = Integer.MAX_VALUE;
          int n = this.imagesToDraw.size();
          i1 = 0;
          for (;;)
          {
            i2 = m;
            i3 = k;
            if (i1 >= n) {
              break;
            }
            localObject1 = (ImageReceiver)this.imagesToDraw.get(i1);
            int i4 = ((ImageReceiver)localObject1).getParam();
            int i5 = (i4 - this.currentImage) * (this.itemWidth + this.itemSpacing) + j + paramInt;
            if (i5 <= i)
            {
              i3 = i1;
              i2 = n;
              if (this.itemWidth + i5 >= 0) {}
            }
            else
            {
              this.unusedReceivers.add(localObject1);
              this.imagesToDraw.remove(i1);
              i2 = n - 1;
              i3 = i1 - 1;
            }
            m = Math.min(m, i4 - 1);
            k = Math.max(k, i4 + 1);
            i1 = i3 + 1;
            n = i2;
          }
        }
        int i3 = this.currentImage;
        int i2 = this.currentImage - 1;
        Object localObject2;
        if (i3 != Integer.MIN_VALUE)
        {
          m = this.currentPhotos.size();
          for (k = i3; k < m; k++)
          {
            i1 = (k - this.currentImage) * (this.itemWidth + this.itemSpacing) + j + paramInt;
            if (i1 >= i) {
              break;
            }
            localObject2 = (TLObject)this.currentPhotos.get(k);
            localObject1 = localObject2;
            if ((localObject2 instanceof TLRPC.PhotoSize)) {
              localObject1 = ((TLRPC.PhotoSize)localObject2).location;
            }
            localObject2 = getFreeReceiver();
            ((ImageReceiver)localObject2).setImageCoords(i1, this.itemY, this.itemWidth, this.itemHeight);
            ((ImageReceiver)localObject2).setImage(null, null, null, null, (TLRPC.FileLocation)localObject1, "80_80", 0, null, 1);
            ((ImageReceiver)localObject2).setParam(k);
          }
        }
        if (i2 != Integer.MAX_VALUE) {
          for (k = i2;; k--)
          {
            if (k < 0) {
              break label575;
            }
            m = (k - this.currentImage) * (this.itemWidth + this.itemSpacing) + j + paramInt + this.itemWidth;
            if (m <= 0) {
              break;
            }
            localObject2 = (TLObject)this.currentPhotos.get(k);
            localObject1 = localObject2;
            if ((localObject2 instanceof TLRPC.PhotoSize)) {
              localObject1 = ((TLRPC.PhotoSize)localObject2).location;
            }
            localObject2 = getFreeReceiver();
            ((ImageReceiver)localObject2).setImageCoords(m, this.itemY, this.itemWidth, this.itemHeight);
            ((ImageReceiver)localObject2).setImage(null, null, null, null, (TLRPC.FileLocation)localObject1, "80_80", 0, null, 1);
            ((ImageReceiver)localObject2).setParam(k);
          }
        }
      }
    }
    
    private ImageReceiver getFreeReceiver()
    {
      ImageReceiver localImageReceiver;
      if (this.unusedReceivers.isEmpty()) {
        localImageReceiver = new ImageReceiver(this);
      }
      for (;;)
      {
        this.imagesToDraw.add(localImageReceiver);
        localImageReceiver.setCurrentAccount(PhotoViewer.this.currentAccount);
        return localImageReceiver;
        localImageReceiver = (ImageReceiver)this.unusedReceivers.get(0);
        this.unusedReceivers.remove(0);
      }
    }
    
    private int getMaxScrollX()
    {
      return this.currentImage * (this.itemWidth + this.itemSpacing * 2);
    }
    
    private int getMinScrollX()
    {
      return -(this.currentPhotos.size() - this.currentImage - 1) * (this.itemWidth + this.itemSpacing * 2);
    }
    
    private void stopScrolling()
    {
      this.scrolling = false;
      if (!this.scroll.isFinished()) {
        this.scroll.abortAnimation();
      }
      if ((this.nextPhotoScrolling >= 0) && (this.nextPhotoScrolling < this.currentObjects.size()))
      {
        this.stopedScrolling = true;
        int i = this.nextPhotoScrolling;
        this.animateToItem = i;
        this.nextImage = i;
        this.animateToDX = ((this.currentImage - this.nextPhotoScrolling) * (this.itemWidth + this.itemSpacing));
        this.animateToDXStart = this.drawDx;
        this.moveLineProgress = 1.0F;
        this.nextPhotoScrolling = -1;
      }
      invalidate();
    }
    
    private void updateAfterScroll()
    {
      int i = 0;
      int j = this.drawDx;
      Object localObject;
      if (Math.abs(j) > this.itemWidth / 2 + this.itemSpacing)
      {
        if (j > 0)
        {
          j -= this.itemWidth / 2 + this.itemSpacing;
          i = 0 + 1;
          i += j / (this.itemWidth + this.itemSpacing * 2);
        }
      }
      else
      {
        this.nextPhotoScrolling = (this.currentImage - i);
        if ((PhotoViewer.this.currentIndex != this.nextPhotoScrolling) && (this.nextPhotoScrolling >= 0) && (this.nextPhotoScrolling < this.currentPhotos.size()))
        {
          localObject = this.currentObjects.get(this.nextPhotoScrolling);
          i = -1;
          if (PhotoViewer.this.imagesArr.isEmpty()) {
            break label256;
          }
          localObject = (MessageObject)localObject;
          i = PhotoViewer.this.imagesArr.indexOf(localObject);
        }
      }
      for (;;)
      {
        if (i >= 0)
        {
          this.ignoreChanges = true;
          PhotoViewer.access$3002(PhotoViewer.this, -1);
          if (PhotoViewer.this.currentThumb != null)
          {
            PhotoViewer.this.currentThumb.release();
            PhotoViewer.access$3302(PhotoViewer.this, null);
          }
          PhotoViewer.this.setImageIndex(i, true);
        }
        if (!this.scrolling)
        {
          this.scrolling = true;
          this.stopedScrolling = false;
        }
        fillImages(true, this.drawDx);
        return;
        j += this.itemWidth / 2 + this.itemSpacing;
        i = 0 - 1;
        break;
        label256:
        if (!PhotoViewer.this.imagesArrLocations.isEmpty())
        {
          localObject = (TLRPC.FileLocation)localObject;
          i = PhotoViewer.this.imagesArrLocations.indexOf(localObject);
        }
      }
    }
    
    public void clear()
    {
      this.currentPhotos.clear();
      this.currentObjects.clear();
      this.imagesToDraw.clear();
    }
    
    public void fillList()
    {
      label12:
      int i;
      int j;
      int k;
      Object localObject1;
      int m;
      int n;
      if (this.ignoreChanges)
      {
        this.ignoreChanges = false;
        return;
      }
      else
      {
        i = 0;
        j = 0;
        k = 0;
        localObject1 = null;
        if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
          break label228;
        }
        localObject1 = (TLRPC.FileLocation)PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
        m = PhotoViewer.this.imagesArrLocations.size();
        n = i;
      }
      label72:
      if (localObject1 != null)
      {
        j = n;
        if (n == 0)
        {
          if ((m == this.currentPhotos.size()) && (this.currentObjects.indexOf(localObject1) != -1)) {
            break label510;
          }
          j = 1;
        }
        label112:
        if (j == 0) {
          break label612;
        }
        this.animateAllLine = false;
        this.currentPhotos.clear();
        this.currentObjects.clear();
        if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
          break label650;
        }
        this.currentObjects.addAll(PhotoViewer.this.imagesArrLocations);
        this.currentPhotos.addAll(PhotoViewer.this.imagesArrLocations);
        this.currentImage = PhotoViewer.this.currentIndex;
        this.animateToItem = -1;
      }
      label228:
      label510:
      label612:
      label650:
      label931:
      for (;;)
      {
        if (this.currentPhotos.size() == 1)
        {
          this.currentPhotos.clear();
          this.currentObjects.clear();
        }
        fillImages(false, 0);
        break label12;
        n = i;
        m = k;
        if (PhotoViewer.this.imagesArr.isEmpty()) {
          break label72;
        }
        localObject1 = (MessageObject)PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
        Object localObject2 = localObject1;
        if (((MessageObject)localObject1).messageOwner.grouped_id != this.currentGroupId)
        {
          n = 1;
          this.currentGroupId = ((MessageObject)localObject1).messageOwner.grouped_id;
          localObject1 = localObject2;
          m = k;
          break label72;
          break label12;
        }
        m = Math.min(PhotoViewer.this.currentIndex + 10, PhotoViewer.this.imagesArr.size());
        for (n = PhotoViewer.this.currentIndex; n < m; n++)
        {
          localObject1 = (MessageObject)PhotoViewer.this.imagesArr.get(n);
          if ((PhotoViewer.this.slideshowMessageId == 0) && (((MessageObject)localObject1).messageOwner.grouped_id != this.currentGroupId)) {
            break;
          }
          j++;
        }
        int i1 = Math.max(PhotoViewer.this.currentIndex - 10, 0);
        for (k = PhotoViewer.this.currentIndex - 1;; k--)
        {
          n = i;
          localObject1 = localObject2;
          m = j;
          if (k < i1) {
            break;
          }
          MessageObject localMessageObject = (MessageObject)PhotoViewer.this.imagesArr.get(k);
          if (PhotoViewer.this.slideshowMessageId == 0)
          {
            n = i;
            localObject1 = localObject2;
            m = j;
            if (localMessageObject.messageOwner.grouped_id != this.currentGroupId) {
              break;
            }
          }
          j++;
        }
        m = this.currentObjects.indexOf(localObject1);
        j = n;
        if (this.currentImage == m) {
          break label112;
        }
        j = n;
        if (m == -1) {
          break label112;
        }
        if (this.animateAllLine)
        {
          this.animateToItem = m;
          this.nextImage = m;
          this.animateToDX = ((this.currentImage - m) * (this.itemWidth + this.itemSpacing));
          this.moving = true;
          this.animateAllLine = false;
          this.lastUpdateTime = System.currentTimeMillis();
          invalidate();
        }
        for (;;)
        {
          this.drawDx = 0;
          j = n;
          break label112;
          break;
          fillImages(true, (this.currentImage - m) * (this.itemWidth + this.itemSpacing));
          this.currentImage = m;
          this.moving = false;
        }
        if ((!PhotoViewer.this.imagesArr.isEmpty()) && ((this.currentGroupId != 0L) || (PhotoViewer.this.slideshowMessageId != 0)))
        {
          m = Math.min(PhotoViewer.this.currentIndex + 10, PhotoViewer.this.imagesArr.size());
          for (n = PhotoViewer.this.currentIndex; n < m; n++)
          {
            localObject1 = (MessageObject)PhotoViewer.this.imagesArr.get(n);
            if ((PhotoViewer.this.slideshowMessageId == 0) && (((MessageObject)localObject1).messageOwner.grouped_id != this.currentGroupId)) {
              break;
            }
            this.currentObjects.add(localObject1);
            this.currentPhotos.add(FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject1).photoThumbs, 56, true));
          }
          this.currentImage = 0;
          this.animateToItem = -1;
          m = Math.max(PhotoViewer.this.currentIndex - 10, 0);
          for (n = PhotoViewer.this.currentIndex - 1;; n--)
          {
            if (n < m) {
              break label931;
            }
            localObject1 = (MessageObject)PhotoViewer.this.imagesArr.get(n);
            if ((PhotoViewer.this.slideshowMessageId == 0) && (((MessageObject)localObject1).messageOwner.grouped_id != this.currentGroupId)) {
              break;
            }
            this.currentObjects.add(0, localObject1);
            this.currentPhotos.add(0, FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject1).photoThumbs, 56, true));
            this.currentImage += 1;
          }
        }
      }
    }
    
    public boolean onDown(MotionEvent paramMotionEvent)
    {
      if (!this.scroll.isFinished()) {
        this.scroll.abortAnimation();
      }
      this.animateToItem = -1;
      return true;
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      if (this.imagesToDraw.isEmpty()) {
        return;
      }
      paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight(), this.backgroundPaint);
      int i = this.imagesToDraw.size();
      int j = this.drawDx;
      int k = (int)(this.itemWidth * 2.0F);
      int m = AndroidUtilities.dp(8.0F);
      Object localObject = (TLObject)this.currentPhotos.get(this.currentImage);
      int n;
      int i2;
      label249:
      label310:
      int i3;
      label362:
      int i4;
      if ((localObject instanceof TLRPC.PhotoSize))
      {
        localObject = (TLRPC.PhotoSize)localObject;
        n = Math.max(this.itemWidth, (int)(((TLRPC.PhotoSize)localObject).w * (this.itemHeight / ((TLRPC.PhotoSize)localObject).h)));
        n = Math.min(k, n);
        int i1 = (int)(m * 2 * this.currentItemProgress);
        i2 = this.itemWidth + (int)((n - this.itemWidth) * this.currentItemProgress) + i1;
        if ((this.nextImage < 0) || (this.nextImage >= this.currentPhotos.size())) {
          break label453;
        }
        localObject = (TLObject)this.currentPhotos.get(this.nextImage);
        if (!(localObject instanceof TLRPC.PhotoSize)) {
          break label444;
        }
        localObject = (TLRPC.PhotoSize)localObject;
        n = Math.max(this.itemWidth, (int)(((TLRPC.PhotoSize)localObject).w * (this.itemHeight / ((TLRPC.PhotoSize)localObject).h)));
        k = Math.min(k, n);
        m = (int)(m * 2 * this.nextItemProgress);
        float f1 = j;
        float f2 = (k + m - this.itemWidth) / 2;
        float f3 = this.nextItemProgress;
        if (this.nextImage <= this.currentImage) {
          break label462;
        }
        n = -1;
        j = (int)(n * (f3 * f2) + f1);
        i3 = this.itemWidth + (int)((k - this.itemWidth) * this.nextItemProgress) + m;
        k = (getMeasuredWidth() - i2) / 2;
        n = 0;
        if (n >= i) {
          break label807;
        }
        localObject = (ImageReceiver)this.imagesToDraw.get(n);
        i4 = ((ImageReceiver)localObject).getParam();
        if (i4 != this.currentImage) {
          break label468;
        }
        ((ImageReceiver)localObject).setImageX(k + j + i1 / 2);
        ((ImageReceiver)localObject).setImageWidth(i2 - i1);
      }
      for (;;)
      {
        ((ImageReceiver)localObject).draw(paramCanvas);
        n++;
        break label362;
        n = this.itemHeight;
        break;
        label444:
        n = this.itemHeight;
        break label249;
        label453:
        n = this.itemWidth;
        break label249;
        label462:
        n = 1;
        break label310;
        label468:
        if (this.nextImage < this.currentImage) {
          if (i4 < this.currentImage) {
            if (i4 <= this.nextImage) {
              ((ImageReceiver)localObject).setImageX((((ImageReceiver)localObject).getParam() - this.currentImage + 1) * (this.itemWidth + this.itemSpacing) + k - (this.itemSpacing + i3) + j);
            }
          }
        }
        for (;;)
        {
          if (i4 != this.nextImage) {
            break label795;
          }
          ((ImageReceiver)localObject).setImageWidth(i3 - m);
          ((ImageReceiver)localObject).setImageX(((ImageReceiver)localObject).getImageX() + m / 2);
          break;
          ((ImageReceiver)localObject).setImageX((((ImageReceiver)localObject).getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing) + k + j);
          continue;
          ((ImageReceiver)localObject).setImageX(k + i2 + this.itemSpacing + (((ImageReceiver)localObject).getParam() - this.currentImage - 1) * (this.itemWidth + this.itemSpacing) + j);
          continue;
          if (i4 < this.currentImage) {
            ((ImageReceiver)localObject).setImageX((((ImageReceiver)localObject).getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing) + k + j);
          } else if (i4 <= this.nextImage) {
            ((ImageReceiver)localObject).setImageX(k + i2 + this.itemSpacing + (((ImageReceiver)localObject).getParam() - this.currentImage - 1) * (this.itemWidth + this.itemSpacing) + j);
          } else {
            ((ImageReceiver)localObject).setImageX(k + i2 + this.itemSpacing + (((ImageReceiver)localObject).getParam() - this.currentImage - 2) * (this.itemWidth + this.itemSpacing) + (this.itemSpacing + i3) + j);
          }
        }
        label795:
        ((ImageReceiver)localObject).setImageWidth(this.itemWidth);
      }
      label807:
      long l1 = System.currentTimeMillis();
      long l2 = l1 - this.lastUpdateTime;
      long l3 = l2;
      if (l2 > 17L) {
        l3 = 17L;
      }
      this.lastUpdateTime = l1;
      if (this.animateToItem >= 0) {
        if (this.moveLineProgress > 0.0F)
        {
          this.moveLineProgress -= (float)l3 / 200.0F;
          if (this.animateToItem != this.currentImage) {
            break label1127;
          }
          if (this.currentItemProgress < 1.0F)
          {
            this.currentItemProgress += (float)l3 / 200.0F;
            if (this.currentItemProgress > 1.0F) {
              this.currentItemProgress = 1.0F;
            }
          }
          this.drawDx = (this.animateToDXStart + (int)Math.ceil(this.currentItemProgress * (this.animateToDX - this.animateToDXStart)));
        }
      }
      for (;;)
      {
        if (this.moveLineProgress <= 0.0F)
        {
          this.currentImage = this.animateToItem;
          this.moveLineProgress = 1.0F;
          this.currentItemProgress = 1.0F;
          this.nextItemProgress = 0.0F;
          this.moving = false;
          this.stopedScrolling = false;
          this.drawDx = 0;
          this.animateToItem = -1;
        }
        fillImages(true, this.drawDx);
        invalidate();
        if ((this.scrolling) && (this.currentItemProgress > 0.0F))
        {
          this.currentItemProgress -= (float)l3 / 200.0F;
          if (this.currentItemProgress < 0.0F) {
            this.currentItemProgress = 0.0F;
          }
          invalidate();
        }
        if (this.scroll.isFinished()) {
          break;
        }
        if (this.scroll.computeScrollOffset())
        {
          this.drawDx = this.scroll.getCurrX();
          updateAfterScroll();
          invalidate();
        }
        if (!this.scroll.isFinished()) {
          break;
        }
        stopScrolling();
        break;
        label1127:
        this.nextItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0F - this.moveLineProgress);
        if (this.stopedScrolling)
        {
          if (this.currentItemProgress > 0.0F)
          {
            this.currentItemProgress -= (float)l3 / 200.0F;
            if (this.currentItemProgress < 0.0F) {
              this.currentItemProgress = 0.0F;
            }
          }
          this.drawDx = (this.animateToDXStart + (int)Math.ceil(this.nextItemProgress * (this.animateToDX - this.animateToDXStart)));
        }
        else
        {
          this.currentItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.moveLineProgress);
          this.drawDx = ((int)Math.ceil(this.nextItemProgress * this.animateToDX));
        }
      }
    }
    
    public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      this.scroll.abortAnimation();
      if (this.currentPhotos.size() >= 10) {
        this.scroll.fling(this.drawDx, 0, Math.round(paramFloat1), 0, getMinScrollX(), getMaxScrollX(), 0, 0);
      }
      return false;
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      fillImages(false, 0);
    }
    
    public void onLongPress(MotionEvent paramMotionEvent) {}
    
    public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      this.drawDx = ((int)(this.drawDx - paramFloat1));
      int i = getMinScrollX();
      int j = getMaxScrollX();
      if (this.drawDx < i) {
        this.drawDx = i;
      }
      for (;;)
      {
        updateAfterScroll();
        return false;
        if (this.drawDx > j) {
          this.drawDx = j;
        }
      }
    }
    
    public void onShowPress(MotionEvent paramMotionEvent) {}
    
    public boolean onSingleTapUp(MotionEvent paramMotionEvent)
    {
      boolean bool1 = true;
      stopScrolling();
      int i = this.imagesToDraw.size();
      for (int j = 0;; j++)
      {
        boolean bool2;
        if (j < i)
        {
          ImageReceiver localImageReceiver = (ImageReceiver)this.imagesToDraw.get(j);
          if (localImageReceiver.isInsideImage(paramMotionEvent.getX(), paramMotionEvent.getY()))
          {
            j = localImageReceiver.getParam();
            bool2 = bool1;
            if (j >= 0)
            {
              if (j < this.currentObjects.size()) {
                break label86;
              }
              bool2 = bool1;
            }
            label86:
            do
            {
              return bool2;
              if (PhotoViewer.this.imagesArr.isEmpty()) {
                break;
              }
              paramMotionEvent = (MessageObject)this.currentObjects.get(j);
              j = PhotoViewer.this.imagesArr.indexOf(paramMotionEvent);
              bool2 = bool1;
            } while (PhotoViewer.this.currentIndex == j);
            this.moveLineProgress = 1.0F;
            this.animateAllLine = true;
            PhotoViewer.access$3002(PhotoViewer.this, -1);
            if (PhotoViewer.this.currentThumb != null)
            {
              PhotoViewer.this.currentThumb.release();
              PhotoViewer.access$3302(PhotoViewer.this, null);
            }
            PhotoViewer.this.setImageIndex(j, true);
          }
        }
        else
        {
          for (;;)
          {
            bool2 = false;
            break;
            if (!PhotoViewer.this.imagesArrLocations.isEmpty())
            {
              paramMotionEvent = (TLRPC.FileLocation)this.currentObjects.get(j);
              j = PhotoViewer.this.imagesArrLocations.indexOf(paramMotionEvent);
              bool2 = bool1;
              if (PhotoViewer.this.currentIndex == j) {
                break;
              }
              this.moveLineProgress = 1.0F;
              this.animateAllLine = true;
              PhotoViewer.access$3002(PhotoViewer.this, -1);
              if (PhotoViewer.this.currentThumb != null)
              {
                PhotoViewer.this.currentThumb.release();
                PhotoViewer.access$3302(PhotoViewer.this, null);
              }
              PhotoViewer.this.setImageIndex(j, true);
            }
          }
        }
      }
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = bool2;
      if (!this.currentPhotos.isEmpty())
      {
        if (getAlpha() == 1.0F) {
          break label32;
        }
        bool3 = bool2;
      }
      for (;;)
      {
        return bool3;
        label32:
        if (!this.gestureDetector.onTouchEvent(paramMotionEvent))
        {
          bool2 = bool1;
          if (!super.onTouchEvent(paramMotionEvent)) {}
        }
        else
        {
          bool2 = true;
        }
        bool3 = bool2;
        if (this.scrolling)
        {
          bool3 = bool2;
          if (paramMotionEvent.getAction() == 1)
          {
            bool3 = bool2;
            if (this.scroll.isFinished())
            {
              stopScrolling();
              bool3 = bool2;
            }
          }
        }
      }
    }
    
    public void setMoveProgress(float paramFloat)
    {
      if ((this.scrolling) || (this.animateToItem >= 0)) {
        return;
      }
      if (paramFloat > 0.0F)
      {
        this.nextImage = (this.currentImage - 1);
        label31:
        if ((this.nextImage < 0) || (this.nextImage >= this.currentPhotos.size())) {
          break label176;
        }
        this.currentItemProgress = (1.0F - Math.abs(paramFloat));
        label62:
        this.nextItemProgress = (1.0F - this.currentItemProgress);
        if (paramFloat == 0.0F) {
          break label184;
        }
      }
      label176:
      label184:
      for (boolean bool = true;; bool = false)
      {
        this.moving = bool;
        invalidate();
        if ((this.currentPhotos.isEmpty()) || ((paramFloat < 0.0F) && (this.currentImage == this.currentPhotos.size() - 1)) || ((paramFloat > 0.0F) && (this.currentImage == 0))) {
          break;
        }
        this.drawDx = ((int)((this.itemWidth + this.itemSpacing) * paramFloat));
        fillImages(true, this.drawDx);
        break;
        this.nextImage = (this.currentImage + 1);
        break label31;
        this.currentItemProgress = 1.0F;
        break label62;
      }
    }
  }
  
  private static class LinkMovementMethodMy
    extends LinkMovementMethod
  {
    public boolean onTouchEvent(TextView paramTextView, Spannable paramSpannable, MotionEvent paramMotionEvent)
    {
      try
      {
        boolean bool1 = super.onTouchEvent(paramTextView, paramSpannable, paramMotionEvent);
        if (paramMotionEvent.getAction() != 1)
        {
          bool2 = bool1;
          if (paramMotionEvent.getAction() != 3) {}
        }
        else
        {
          Selection.removeSelection(paramSpannable);
          bool2 = bool1;
        }
      }
      catch (Exception paramTextView)
      {
        for (;;)
        {
          FileLog.e(paramTextView);
          boolean bool2 = false;
        }
      }
      return bool2;
    }
  }
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      int i;
      if ((PhotoViewer.this.placeProvider != null) && (PhotoViewer.this.placeProvider.getSelectedPhotosOrder() != null)) {
        if (PhotoViewer.this.placeProvider.allowGroupPhotos()) {
          i = PhotoViewer.this.placeProvider.getSelectedPhotosOrder().size() + 1;
        }
      }
      for (;;)
      {
        return i;
        i = PhotoViewer.this.placeProvider.getSelectedPhotosOrder().size();
        continue;
        i = 0;
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt == 0) && (PhotoViewer.this.placeProvider.allowGroupPhotos())) {}
      for (paramInt = 1;; paramInt = 0) {
        return paramInt;
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      default: 
      case 0: 
        do
        {
          return;
          paramViewHolder = (PhotoPickerPhotoCell)paramViewHolder.itemView;
          paramViewHolder.itemWidth = AndroidUtilities.dp(82.0F);
          localObject1 = paramViewHolder.photoImage;
          ((BackupImageView)localObject1).setOrientation(0, true);
          localObject2 = PhotoViewer.this.placeProvider.getSelectedPhotosOrder();
          int i = paramInt;
          if (PhotoViewer.this.placeProvider.allowGroupPhotos()) {
            i = paramInt - 1;
          }
          localObject2 = PhotoViewer.this.placeProvider.getSelectedPhotos().get(((ArrayList)localObject2).get(i));
          if ((localObject2 instanceof MediaController.PhotoEntry))
          {
            localObject2 = (MediaController.PhotoEntry)localObject2;
            paramViewHolder.setTag(localObject2);
            paramViewHolder.videoInfoContainer.setVisibility(4);
            if (((MediaController.PhotoEntry)localObject2).thumbPath != null) {
              ((BackupImageView)localObject1).setImage(((MediaController.PhotoEntry)localObject2).thumbPath, null, this.mContext.getResources().getDrawable(NUM));
            }
            for (;;)
            {
              paramViewHolder.setChecked(-1, true, false);
              paramViewHolder.checkBox.setVisibility(0);
              break;
              if (((MediaController.PhotoEntry)localObject2).path != null)
              {
                ((BackupImageView)localObject1).setOrientation(((MediaController.PhotoEntry)localObject2).orientation, true);
                if (((MediaController.PhotoEntry)localObject2).isVideo)
                {
                  paramViewHolder.videoInfoContainer.setVisibility(0);
                  paramInt = ((MediaController.PhotoEntry)localObject2).duration / 60;
                  i = ((MediaController.PhotoEntry)localObject2).duration;
                  paramViewHolder.videoTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i - paramInt * 60) }));
                  ((BackupImageView)localObject1).setImage("vthumb://" + ((MediaController.PhotoEntry)localObject2).imageId + ":" + ((MediaController.PhotoEntry)localObject2).path, null, this.mContext.getResources().getDrawable(NUM));
                }
                else
                {
                  ((BackupImageView)localObject1).setImage("thumb://" + ((MediaController.PhotoEntry)localObject2).imageId + ":" + ((MediaController.PhotoEntry)localObject2).path, null, this.mContext.getResources().getDrawable(NUM));
                }
              }
              else
              {
                ((BackupImageView)localObject1).setImageResource(NUM);
              }
            }
          }
        } while (!(localObject2 instanceof MediaController.SearchImage));
        Object localObject2 = (MediaController.SearchImage)localObject2;
        paramViewHolder.setTag(localObject2);
        if (((MediaController.SearchImage)localObject2).thumbPath != null) {
          ((BackupImageView)localObject1).setImage(((MediaController.SearchImage)localObject2).thumbPath, null, this.mContext.getResources().getDrawable(NUM));
        }
        for (;;)
        {
          paramViewHolder.videoInfoContainer.setVisibility(4);
          paramViewHolder.setChecked(-1, true, false);
          paramViewHolder.checkBox.setVisibility(0);
          break;
          if ((((MediaController.SearchImage)localObject2).thumbUrl != null) && (((MediaController.SearchImage)localObject2).thumbUrl.length() > 0)) {
            ((BackupImageView)localObject1).setImage(((MediaController.SearchImage)localObject2).thumbUrl, null, this.mContext.getResources().getDrawable(NUM));
          } else if ((((MediaController.SearchImage)localObject2).document != null) && (((MediaController.SearchImage)localObject2).document.thumb != null)) {
            ((BackupImageView)localObject1).setImage(((MediaController.SearchImage)localObject2).document.thumb.location, null, this.mContext.getResources().getDrawable(NUM));
          } else {
            ((BackupImageView)localObject1).setImageResource(NUM);
          }
        }
      }
      Object localObject1 = (ImageView)paramViewHolder.itemView;
      if (SharedConfig.groupPhotosEnabled) {}
      for (paramViewHolder = new PorterDuffColorFilter(-10043398, PorterDuff.Mode.MULTIPLY);; paramViewHolder = null)
      {
        ((ImageView)localObject1).setColorFilter(paramViewHolder);
        break;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new ImageView(this.mContext)
        {
          protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0F), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramAnonymousInt2), NUM));
          }
        };
        paramViewGroup.setScaleType(ImageView.ScaleType.CENTER);
        paramViewGroup.setImageResource(NUM);
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new PhotoPickerPhotoCell(this.mContext, false);
        paramViewGroup.checkFrame.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = ((View)paramAnonymousView.getParent()).getTag();
            int i = PhotoViewer.this.imagesArrLocals.indexOf(paramAnonymousView);
            if (i >= 0)
            {
              int j = PhotoViewer.this.placeProvider.setPhotoChecked(i, PhotoViewer.this.getCurrentVideoEditedInfo());
              PhotoViewer.this.placeProvider.isPhotoChecked(i);
              if (i == PhotoViewer.this.currentIndex) {
                PhotoViewer.this.checkImageView.setChecked(-1, false, true);
              }
              if (j >= 0)
              {
                i = j;
                if (PhotoViewer.this.placeProvider.allowGroupPhotos()) {
                  i = j + 1;
                }
                PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(i);
              }
              PhotoViewer.this.updateSelectedCount();
            }
          }
        });
      }
    }
  }
  
  private class PhotoProgressView
  {
    private float alpha = 1.0F;
    private float animatedAlphaValue = 1.0F;
    private float animatedProgressValue = 0.0F;
    private float animationProgressStart = 0.0F;
    private int backgroundState = -1;
    private float currentProgress = 0.0F;
    private long currentProgressTime = 0L;
    private long lastUpdateTime = 0L;
    private View parent = null;
    private int previousBackgroundState = -2;
    private RectF progressRect = new RectF();
    private float radOffset = 0.0F;
    private float scale = 1.0F;
    private int size = AndroidUtilities.dp(64.0F);
    
    public PhotoProgressView(Context paramContext, View paramView)
    {
      if (PhotoViewer.decelerateInterpolator == null)
      {
        PhotoViewer.access$3802(new DecelerateInterpolator(1.5F));
        PhotoViewer.access$3902(new Paint(1));
        PhotoViewer.progressPaint.setStyle(Paint.Style.STROKE);
        PhotoViewer.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        PhotoViewer.progressPaint.setStrokeWidth(AndroidUtilities.dp(3.0F));
        PhotoViewer.progressPaint.setColor(-1);
      }
      this.parent = paramView;
    }
    
    private void updateAnimation()
    {
      long l1 = System.currentTimeMillis();
      long l2 = l1 - this.lastUpdateTime;
      long l3 = l2;
      if (l2 > 18L) {
        l3 = 18L;
      }
      this.lastUpdateTime = l1;
      float f;
      if (this.animatedProgressValue != 1.0F)
      {
        this.radOffset += (float)(360L * l3) / 3000.0F;
        f = this.currentProgress - this.animationProgressStart;
        if (f > 0.0F)
        {
          this.currentProgressTime += l3;
          if (this.currentProgressTime < 300L) {
            break label189;
          }
          this.animatedProgressValue = this.currentProgress;
          this.animationProgressStart = this.currentProgress;
          this.currentProgressTime = 0L;
        }
      }
      for (;;)
      {
        this.parent.invalidate();
        if ((this.animatedProgressValue >= 1.0F) && (this.previousBackgroundState != -2))
        {
          this.animatedAlphaValue -= (float)l3 / 200.0F;
          if (this.animatedAlphaValue <= 0.0F)
          {
            this.animatedAlphaValue = 0.0F;
            this.previousBackgroundState = -2;
          }
          this.parent.invalidate();
        }
        return;
        label189:
        this.animatedProgressValue = (this.animationProgressStart + PhotoViewer.decelerateInterpolator.getInterpolation((float)this.currentProgressTime / 300.0F) * f);
      }
    }
    
    public void onDraw(Canvas paramCanvas)
    {
      int i = (int)(this.size * this.scale);
      int j = (PhotoViewer.this.getContainerViewWidth() - i) / 2;
      int k = (PhotoViewer.this.getContainerViewHeight() - i) / 2;
      Drawable localDrawable;
      if ((this.previousBackgroundState >= 0) && (this.previousBackgroundState < 4))
      {
        localDrawable = PhotoViewer.progressDrawables[this.previousBackgroundState];
        if (localDrawable != null)
        {
          localDrawable.setAlpha((int)(this.animatedAlphaValue * 255.0F * this.alpha));
          localDrawable.setBounds(j, k, j + i, k + i);
          localDrawable.draw(paramCanvas);
        }
      }
      if ((this.backgroundState >= 0) && (this.backgroundState < 4))
      {
        localDrawable = PhotoViewer.progressDrawables[this.backgroundState];
        if (localDrawable != null)
        {
          if (this.previousBackgroundState == -2) {
            break label320;
          }
          localDrawable.setAlpha((int)((1.0F - this.animatedAlphaValue) * 255.0F * this.alpha));
          localDrawable.setBounds(j, k, j + i, k + i);
          localDrawable.draw(paramCanvas);
        }
      }
      int m;
      if ((this.backgroundState == 0) || (this.backgroundState == 1) || (this.previousBackgroundState == 0) || (this.previousBackgroundState == 1))
      {
        m = AndroidUtilities.dp(4.0F);
        if (this.previousBackgroundState == -2) {
          break label336;
        }
        PhotoViewer.progressPaint.setAlpha((int)(this.animatedAlphaValue * 255.0F * this.alpha));
      }
      for (;;)
      {
        this.progressRect.set(j + m, k + m, j + i - m, k + i - m);
        paramCanvas.drawArc(this.progressRect, this.radOffset - 90.0F, Math.max(4.0F, 360.0F * this.animatedProgressValue), false, PhotoViewer.progressPaint);
        updateAnimation();
        return;
        label320:
        localDrawable.setAlpha((int)(this.alpha * 255.0F));
        break;
        label336:
        PhotoViewer.progressPaint.setAlpha((int)(this.alpha * 255.0F));
      }
    }
    
    public void setAlpha(float paramFloat)
    {
      this.alpha = paramFloat;
    }
    
    public void setBackgroundState(int paramInt, boolean paramBoolean)
    {
      if ((this.backgroundState == paramInt) && (paramBoolean)) {
        return;
      }
      this.lastUpdateTime = System.currentTimeMillis();
      if ((paramBoolean) && (this.backgroundState != paramInt))
      {
        this.previousBackgroundState = this.backgroundState;
        this.animatedAlphaValue = 1.0F;
      }
      for (;;)
      {
        this.backgroundState = paramInt;
        this.parent.invalidate();
        break;
        this.previousBackgroundState = -2;
      }
    }
    
    public void setProgress(float paramFloat, boolean paramBoolean)
    {
      if (!paramBoolean) {
        this.animatedProgressValue = paramFloat;
      }
      for (this.animationProgressStart = paramFloat;; this.animationProgressStart = this.animatedProgressValue)
      {
        this.currentProgress = paramFloat;
        this.currentProgressTime = 0L;
        return;
      }
    }
    
    public void setScale(float paramFloat)
    {
      this.scale = paramFloat;
    }
  }
  
  public static abstract interface PhotoViewerProvider
  {
    public abstract boolean allowCaption();
    
    public abstract boolean allowGroupPhotos();
    
    public abstract boolean canScrollAway();
    
    public abstract boolean cancelButtonPressed();
    
    public abstract int getPhotoIndex(int paramInt);
    
    public abstract PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt);
    
    public abstract int getSelectedCount();
    
    public abstract HashMap<Object, Object> getSelectedPhotos();
    
    public abstract ArrayList<Object> getSelectedPhotosOrder();
    
    public abstract ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt);
    
    public abstract boolean isPhotoChecked(int paramInt);
    
    public abstract void needAddMorePhotos();
    
    public abstract boolean scaleToFill();
    
    public abstract void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo);
    
    public abstract int setPhotoChecked(int paramInt, VideoEditedInfo paramVideoEditedInfo);
    
    public abstract void toggleGroupPhotosEnabled();
    
    public abstract void updatePhotoAtIndex(int paramInt);
    
    public abstract void willHidePhotoViewer();
    
    public abstract void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt);
  }
  
  public static class PlaceProviderObject
  {
    public int clipBottomAddition;
    public int clipTopAddition;
    public int dialogId;
    public ImageReceiver imageReceiver;
    public int index;
    public boolean isEvent;
    public View parentView;
    public int radius;
    public float scale = 1.0F;
    public int size;
    public ImageReceiver.BitmapHolder thumb;
    public int viewX;
    public int viewY;
  }
  
  private class QualityChooseView
    extends View
  {
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
    
    public QualityChooseView(Context paramContext)
    {
      super();
      this.textPaint.setTextSize(AndroidUtilities.dp(12.0F));
      this.textPaint.setColor(-3289651);
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int i;
      int j;
      label72:
      int k;
      label135:
      String str;
      label185:
      float f1;
      float f2;
      float f3;
      if (PhotoViewer.this.compressionsCount != 1)
      {
        this.lineSize = ((getMeasuredWidth() - this.circleSize * PhotoViewer.this.compressionsCount - this.gapSize * 8 - this.sideSide * 2) / (PhotoViewer.this.compressionsCount - 1));
        i = getMeasuredHeight() / 2 + AndroidUtilities.dp(6.0F);
        j = 0;
        if (j >= PhotoViewer.this.compressionsCount) {
          return;
        }
        k = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * j + this.circleSize / 2;
        if (j > PhotoViewer.this.selectedCompression) {
          break label367;
        }
        this.paint.setColor(-11292945);
        if (j != PhotoViewer.this.compressionsCount - 1) {
          break label379;
        }
        str = Math.min(PhotoViewer.this.originalWidth, PhotoViewer.this.originalHeight) + "p";
        f1 = this.textPaint.measureText(str);
        f2 = k;
        f3 = i;
        if (j != PhotoViewer.this.selectedCompression) {
          break label421;
        }
      }
      label367:
      label379:
      label421:
      for (float f4 = AndroidUtilities.dp(8.0F);; f4 = this.circleSize / 2)
      {
        paramCanvas.drawCircle(f2, f3, f4, this.paint);
        paramCanvas.drawText(str, k - f1 / 2.0F, i - AndroidUtilities.dp(16.0F), this.textPaint);
        if (j != 0)
        {
          k = k - this.circleSize / 2 - this.gapSize - this.lineSize;
          paramCanvas.drawRect(k, i - AndroidUtilities.dp(1.0F), this.lineSize + k, AndroidUtilities.dp(2.0F) + i, this.paint);
        }
        j++;
        break label72;
        this.lineSize = (getMeasuredWidth() - this.circleSize * PhotoViewer.this.compressionsCount - this.gapSize * 8 - this.sideSide * 2);
        break;
        this.paint.setColor(NUM);
        break label135;
        if (j == 0)
        {
          str = "240p";
          break label185;
        }
        if (j == 1)
        {
          str = "360p";
          break label185;
        }
        if (j == 2)
        {
          str = "480p";
          break label185;
        }
        str = "720p";
        break label185;
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      this.circleSize = AndroidUtilities.dp(12.0F);
      this.gapSize = AndroidUtilities.dp(2.0F);
      this.sideSide = AndroidUtilities.dp(18.0F);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool = false;
      float f = paramMotionEvent.getX();
      int i;
      int j;
      if (paramMotionEvent.getAction() == 0)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
        i = 0;
        if (i < PhotoViewer.this.compressionsCount)
        {
          j = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
          if ((f <= j - AndroidUtilities.dp(15.0F)) || (f >= AndroidUtilities.dp(15.0F) + j)) {
            break label137;
          }
          if (i == PhotoViewer.this.selectedCompression) {
            bool = true;
          }
          this.startMoving = bool;
          this.startX = f;
          this.startMovingQuality = PhotoViewer.this.selectedCompression;
        }
      }
      label137:
      label328:
      label330:
      do
      {
        for (;;)
        {
          return true;
          i++;
          break;
          if (paramMotionEvent.getAction() != 2) {
            break label330;
          }
          if (this.startMoving)
          {
            if (Math.abs(this.startX - f) >= AndroidUtilities.getPixelsInCM(0.5F, true))
            {
              this.moving = true;
              this.startMoving = false;
            }
          }
          else if (this.moving) {
            for (i = 0;; i++)
            {
              if (i >= PhotoViewer.this.compressionsCount) {
                break label328;
              }
              int k = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
              j = this.lineSize / 2 + this.circleSize / 2 + this.gapSize;
              if ((f > k - j) && (f < k + j))
              {
                if (PhotoViewer.this.selectedCompression == i) {
                  break;
                }
                PhotoViewer.access$9602(PhotoViewer.this, i);
                PhotoViewer.this.didChangedCompressionLevel(false);
                invalidate();
                break;
              }
            }
          }
        }
      } while ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3));
      if (!this.moving)
      {
        i = 0;
        label356:
        if (i < PhotoViewer.this.compressionsCount)
        {
          j = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
          if ((f <= j - AndroidUtilities.dp(15.0F)) || (f >= AndroidUtilities.dp(15.0F) + j)) {
            break label476;
          }
          if (PhotoViewer.this.selectedCompression != i)
          {
            PhotoViewer.access$9602(PhotoViewer.this, i);
            PhotoViewer.this.didChangedCompressionLevel(true);
            invalidate();
          }
        }
      }
      for (;;)
      {
        this.startMoving = false;
        this.moving = false;
        break;
        label476:
        i++;
        break label356;
        if (PhotoViewer.this.selectedCompression != this.startMovingQuality) {
          PhotoViewer.this.requestVideoPreview(1);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PhotoViewer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */