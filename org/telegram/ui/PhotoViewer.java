package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Point;
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
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
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
import org.telegram.messenger.exoplayer.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer.util.PlayerControl;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputPhoto;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
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
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Views.ColorPicker;
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
import org.telegram.ui.Components.VideoPlayer.ExtractorRendererBuilder;
import org.telegram.ui.Components.VideoPlayer.Listener;

public class PhotoViewer
  implements NotificationCenter.NotificationCenterDelegate, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener
{
  private static volatile PhotoViewer Instance = null;
  private static DecelerateInterpolator decelerateInterpolator = null;
  private static final int gallery_menu_caption_done = 9;
  private static final int gallery_menu_delete = 6;
  private static final int gallery_menu_masks = 13;
  private static final int gallery_menu_mute = 12;
  private static final int gallery_menu_openin = 11;
  private static final int gallery_menu_save = 1;
  private static final int gallery_menu_send = 3;
  private static final int gallery_menu_share = 10;
  private static final int gallery_menu_showall = 2;
  private static Drawable[] progressDrawables;
  private static Paint progressPaint = null;
  private ActionBar actionBar;
  private Context actvityContext;
  private boolean allowMentions;
  private boolean allowShare;
  private float animateToScale;
  private float animateToX;
  private float animateToY;
  private ClippingImageView animatingImageView;
  private Runnable animationEndRunnable = null;
  private int animationInProgress = 0;
  private long animationStartTime;
  private float animationValue;
  private float[][] animationValues = (float[][])Array.newInstance(Float.TYPE, new int[] { 2, 8 });
  private AspectRatioFrameLayout aspectRatioFrameLayout;
  private boolean attachedToWindow;
  private ArrayList<TLRPC.Photo> avatarsArr = new ArrayList();
  private int avatarsDialogId;
  private BackgroundDrawable backgroundDrawable = new BackgroundDrawable(-16777216);
  private FrameLayout bottomLayout;
  private boolean canDragDown = true;
  private boolean canShowBottom = true;
  private boolean canZoom = true;
  private ActionBarMenuItem captionDoneItem;
  private PhotoViewerCaptionEnterView captionEditText;
  private TextView captionTextView;
  private TextView captionTextViewNew;
  private TextView captionTextViewOld;
  private ImageReceiver centerImage = new ImageReceiver();
  private AnimatorSet changeModeAnimation;
  private boolean changingPage = false;
  private CheckBox checkImageView;
  private int classGuid;
  private FrameLayoutDrawer containerView;
  private ImageView cropItem;
  private AnimatorSet currentActionBarAnimation;
  private AnimatedFileDrawable currentAnimation;
  private TLRPC.BotInlineResult currentBotInlineResult;
  private long currentDialogId;
  private int currentEditMode;
  private TLRPC.FileLocation currentFileLocation;
  private String[] currentFileNames = new String[3];
  private int currentIndex;
  private MessageObject currentMessageObject;
  private String currentPathObject;
  private PlaceProviderObject currentPlaceObject;
  private int currentRotation;
  private Bitmap currentThumb = null;
  private TLRPC.FileLocation currentUserAvatarLocation = null;
  private TextView dateTextView;
  private boolean disableShowCheck = false;
  private boolean discardTap = false;
  private boolean doubleTap = false;
  private float dragY;
  private boolean draggingDown = false;
  private PickerBottomLayoutViewer editorDoneLayout;
  private boolean[] endReached = { 0, 1 };
  private GestureDetector gestureDetector;
  private PlaceProviderObject hideAfterAnimation;
  private AnimatorSet imageMoveAnimation;
  private ArrayList<MessageObject> imagesArr = new ArrayList();
  private ArrayList<Object> imagesArrLocals = new ArrayList();
  private ArrayList<TLRPC.FileLocation> imagesArrLocations = new ArrayList();
  private ArrayList<Integer> imagesArrLocationsSizes = new ArrayList();
  private ArrayList<MessageObject> imagesArrTemp = new ArrayList();
  private HashMap<Integer, MessageObject>[] imagesByIds = { new HashMap(), new HashMap() };
  private HashMap<Integer, MessageObject>[] imagesByIdsTemp = { new HashMap(), new HashMap() };
  private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5F);
  private boolean invalidCoords = false;
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
  private boolean moving = false;
  private ActionBarMenuItem muteItem;
  private boolean muteItemAvailable;
  private boolean muteVideo;
  private TextView nameTextView;
  private boolean needCaptionLayout;
  private boolean needSearchImageInArr;
  private boolean opennedFromMedia;
  private ImageView paintItem;
  private Activity parentActivity;
  private ChatActivity parentChatActivity;
  private PhotoCropView photoCropView;
  private PhotoFilterView photoFilterView;
  private PhotoPaintView photoPaintView;
  private PickerBottomLayoutViewer pickerView;
  private float pinchCenterX;
  private float pinchCenterY;
  private float pinchStartDistance;
  private float pinchStartScale = 1.0F;
  private float pinchStartX;
  private float pinchStartY;
  private PhotoViewerProvider placeProvider;
  private boolean playerNeedsPrepare;
  private RadialProgressView[] radialProgressViews = new RadialProgressView[3];
  private ImageReceiver rightImage = new ImageReceiver();
  private float scale = 1.0F;
  private Scroller scroller = null;
  private int sendPhotoType;
  private ImageView shareButton;
  private PlaceProviderObject showAfterAnimation;
  private int switchImageAfterAnimation = 0;
  private boolean textureUploaded;
  private int totalImagesCount;
  private int totalImagesCountMerge;
  private long transitionAnimationStartTime = 0L;
  private float translationX;
  private float translationY;
  private ImageView tuneItem;
  private Runnable updateProgressRunnable = new Runnable()
  {
    public void run()
    {
      if ((PhotoViewer.this.videoPlayer != null) && (PhotoViewer.this.videoPlayerSeekbar != null) && (!PhotoViewer.this.videoPlayerSeekbar.isDragging()))
      {
        PlayerControl localPlayerControl = PhotoViewer.this.videoPlayer.getPlayerControl();
        float f = localPlayerControl.getCurrentPosition() / localPlayerControl.getDuration();
        PhotoViewer.this.videoPlayerSeekbar.setProgress(f);
        PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
        PhotoViewer.this.updateVideoPlayerTime();
      }
      if (PhotoViewer.this.isPlaying) {
        AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 100L);
      }
    }
  };
  private VelocityTracker velocityTracker = null;
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
  private WindowManager.LayoutParams windowLayoutParams;
  private FrameLayout windowView;
  private boolean zoomAnimation = false;
  private boolean zooming = false;
  
  private void animateTo(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean)
  {
    animateTo(paramFloat1, paramFloat2, paramFloat3, paramBoolean, 250);
  }
  
  private void animateTo(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean, int paramInt)
  {
    if ((this.scale == paramFloat1) && (this.translationX == paramFloat2) && (this.translationY == paramFloat3)) {
      return;
    }
    this.zoomAnimation = paramBoolean;
    this.animateToScale = paramFloat1;
    this.animateToX = paramFloat2;
    this.animateToY = paramFloat3;
    this.animationStartTime = System.currentTimeMillis();
    this.imageMoveAnimation = new AnimatorSet();
    this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }) });
    this.imageMoveAnimation.setInterpolator(this.interpolator);
    this.imageMoveAnimation.setDuration(paramInt);
    this.imageMoveAnimation.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        PhotoViewer.access$5802(PhotoViewer.this, null);
        PhotoViewer.this.containerView.invalidate();
      }
    });
    this.imageMoveAnimation.start();
  }
  
  private void applyCurrentEditMode()
  {
    Bitmap localBitmap = null;
    Object localObject1 = null;
    TLRPC.PhotoSize localPhotoSize;
    Object localObject2;
    label155:
    float f1;
    float f2;
    if (this.currentEditMode == 1)
    {
      localBitmap = this.photoCropView.getBitmap();
      if (localBitmap != null)
      {
        localPhotoSize = ImageLoader.scaleAndSaveImage(localBitmap, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
        if (localPhotoSize != null)
        {
          localObject2 = this.imagesArrLocals.get(this.currentIndex);
          if (!(localObject2 instanceof MediaController.PhotoEntry)) {
            break label430;
          }
          localObject2 = (MediaController.PhotoEntry)localObject2;
          ((MediaController.PhotoEntry)localObject2).imagePath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
          localPhotoSize = ImageLoader.scaleAndSaveImage(localBitmap, AndroidUtilities.dp(120.0F), AndroidUtilities.dp(120.0F), 70, false, 101, 101);
          if (localPhotoSize != null) {
            ((MediaController.PhotoEntry)localObject2).thumbPath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
          }
          if (localObject1 != null) {
            ((MediaController.PhotoEntry)localObject2).stickers.addAll((Collection)localObject1);
          }
          if ((this.sendPhotoType == 0) && (this.placeProvider != null))
          {
            this.placeProvider.updatePhotoAtIndex(this.currentIndex);
            if (!this.placeProvider.isPhotoChecked(this.currentIndex))
            {
              this.placeProvider.setPhotoChecked(this.currentIndex);
              this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), true);
              updateSelectedCount();
            }
          }
          if (this.currentEditMode == 1)
          {
            f1 = this.photoCropView.getRectSizeX() / getContainerViewWidth();
            f2 = this.photoCropView.getRectSizeY() / getContainerViewHeight();
            if (f1 <= f2) {
              break label524;
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
      this.centerImage.setParentView(null);
      this.centerImage.setOrientation(0, true);
      this.centerImage.setImageBitmap(localBitmap);
      this.centerImage.setParentView(this.containerView);
      return;
      if (this.currentEditMode == 2)
      {
        localBitmap = this.photoFilterView.getBitmap();
        break;
      }
      if (this.currentEditMode != 3) {
        break;
      }
      localBitmap = this.photoPaintView.getBitmap();
      localObject1 = this.photoPaintView.getMasks();
      break;
      label430:
      if (!(localObject2 instanceof MediaController.SearchImage)) {
        break label155;
      }
      localObject2 = (MediaController.SearchImage)localObject2;
      ((MediaController.SearchImage)localObject2).imagePath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
      localPhotoSize = ImageLoader.scaleAndSaveImage(localBitmap, AndroidUtilities.dp(120.0F), AndroidUtilities.dp(120.0F), 70, false, 101, 101);
      if (localPhotoSize != null) {
        ((MediaController.SearchImage)localObject2).thumbPath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
      }
      if (localObject1 == null) {
        break label155;
      }
      ((MediaController.SearchImage)localObject2).stickers.addAll((Collection)localObject1);
      break label155;
      label524:
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
    if (this.currentFileNames[paramInt] != null)
    {
      int j = this.currentIndex;
      int i;
      Object localObject3;
      Object localObject2;
      MessageObject localMessageObject;
      Object localObject1;
      boolean bool1;
      boolean bool2;
      if (paramInt == 1)
      {
        i = j + 1;
        localObject3 = null;
        localObject2 = null;
        localMessageObject = null;
        localObject1 = null;
        bool1 = false;
        bool2 = false;
        if (this.currentMessageObject == null) {
          break label232;
        }
        localMessageObject = (MessageObject)this.imagesArr.get(i);
        if (!TextUtils.isEmpty(localMessageObject.messageOwner.attachPath))
        {
          localObject2 = new File(localMessageObject.messageOwner.attachPath);
          localObject1 = localObject2;
          if (!((File)localObject2).exists()) {
            localObject1 = null;
          }
        }
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = FileLoader.getPathToMessage(localMessageObject.messageOwner);
        }
        bool1 = localMessageObject.isVideo();
        localObject1 = localObject2;
        label139:
        if ((localObject1 == null) || (!((File)localObject1).exists())) {
          break label646;
        }
        if (!bool1) {
          break label632;
        }
        this.radialProgressViews[paramInt].setBackgroundState(3, paramBoolean);
        label168:
        if (paramInt == 0) {
          if ((this.imagesArrLocals.isEmpty()) && ((this.currentFileNames[0] == null) || (bool1) || (this.radialProgressViews[0].backgroundState == 0))) {
            break label752;
          }
        }
      }
      label232:
      label295:
      label632:
      label646:
      label752:
      for (paramBoolean = true;; paramBoolean = false)
      {
        this.canZoom = paramBoolean;
        return;
        i = j;
        if (paramInt != 2) {
          break;
        }
        i = j - 1;
        break;
        if (this.currentBotInlineResult != null)
        {
          localObject1 = (TLRPC.BotInlineResult)this.imagesArrLocals.get(i);
          if ((((TLRPC.BotInlineResult)localObject1).type.equals("video")) || (MessageObject.isVideoDocument(((TLRPC.BotInlineResult)localObject1).document))) {
            if (((TLRPC.BotInlineResult)localObject1).document != null)
            {
              localObject2 = FileLoader.getPathToAttach(((TLRPC.BotInlineResult)localObject1).document);
              bool2 = true;
            }
          }
          for (;;)
          {
            if (localObject2 != null)
            {
              localObject1 = localObject2;
              bool1 = bool2;
              if (((File)localObject2).exists()) {
                break;
              }
            }
            localObject1 = new File(FileLoader.getInstance().getDirectory(4), this.currentFileNames[paramInt]);
            bool1 = bool2;
            break;
            if (((TLRPC.BotInlineResult)localObject1).content_url == null) {
              break label295;
            }
            localObject2 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(((TLRPC.BotInlineResult)localObject1).content_url) + "." + ImageLoader.getHttpUrlExtension(((TLRPC.BotInlineResult)localObject1).content_url, "mp4"));
            break label295;
            if (((TLRPC.BotInlineResult)localObject1).document != null)
            {
              localObject2 = new File(FileLoader.getInstance().getDirectory(3), this.currentFileNames[paramInt]);
              bool2 = bool1;
            }
            else
            {
              localObject2 = localMessageObject;
              bool2 = bool1;
              if (((TLRPC.BotInlineResult)localObject1).photo != null)
              {
                localObject2 = new File(FileLoader.getInstance().getDirectory(0), this.currentFileNames[paramInt]);
                bool2 = bool1;
              }
            }
          }
        }
        if (this.currentFileLocation != null)
        {
          localObject1 = (TLRPC.FileLocation)this.imagesArrLocations.get(i);
          if (this.avatarsDialogId != 0) {}
          for (bool1 = true;; bool1 = false)
          {
            localObject1 = FileLoader.getPathToAttach((TLObject)localObject1, bool1);
            bool1 = bool2;
            break;
          }
        }
        localObject1 = localObject3;
        bool1 = bool2;
        if (this.currentPathObject == null) {
          break label139;
        }
        localObject2 = new File(FileLoader.getInstance().getDirectory(3), this.currentFileNames[paramInt]);
        localObject1 = localObject2;
        bool1 = bool2;
        if (((File)localObject2).exists()) {
          break label139;
        }
        localObject1 = new File(FileLoader.getInstance().getDirectory(4), this.currentFileNames[paramInt]);
        bool1 = bool2;
        break label139;
        this.radialProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
        break label168;
        if (bool1) {
          if (!FileLoader.getInstance().isLoadingFile(this.currentFileNames[paramInt])) {
            this.radialProgressViews[paramInt].setBackgroundState(2, false);
          }
        }
        for (;;)
        {
          localObject2 = ImageLoader.getInstance().getFileProgress(this.currentFileNames[paramInt]);
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = Float.valueOf(0.0F);
          }
          this.radialProgressViews[paramInt].setProgress(((Float)localObject1).floatValue(), false);
          break;
          this.radialProgressViews[paramInt].setBackgroundState(1, false);
          continue;
          this.radialProgressViews[paramInt].setBackgroundState(0, paramBoolean);
        }
      }
    }
    this.radialProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
  }
  
  private void closeCaptionEnter(boolean paramBoolean)
  {
    if ((this.currentIndex < 0) || (this.currentIndex >= this.imagesArrLocals.size())) {
      return;
    }
    Object localObject = this.imagesArrLocals.get(this.currentIndex);
    if (paramBoolean)
    {
      if (!(localObject instanceof MediaController.PhotoEntry)) {
        break label309;
      }
      ((MediaController.PhotoEntry)localObject).caption = this.captionEditText.getFieldCharSequence();
    }
    for (;;)
    {
      if ((this.captionEditText.getFieldCharSequence().length() != 0) && (!this.placeProvider.isPhotoChecked(this.currentIndex)))
      {
        this.placeProvider.setPhotoChecked(this.currentIndex);
        this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), true);
        updateSelectedCount();
      }
      this.cropItem.setVisibility(0);
      if (Build.VERSION.SDK_INT >= 16)
      {
        this.paintItem.setVisibility(0);
        this.tuneItem.setVisibility(0);
      }
      if (this.sendPhotoType == 0) {
        this.checkImageView.setVisibility(0);
      }
      this.captionDoneItem.setVisibility(8);
      this.pickerView.setVisibility(0);
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.captionEditText.getLayoutParams();
      localLayoutParams.bottomMargin = (-AndroidUtilities.dp(400.0F));
      this.captionEditText.setLayoutParams(localLayoutParams);
      localLayoutParams = (FrameLayout.LayoutParams)this.mentionListView.getLayoutParams();
      localLayoutParams.bottomMargin = (-AndroidUtilities.dp(400.0F));
      this.mentionListView.setLayoutParams(localLayoutParams);
      if (this.lastTitle != null)
      {
        this.actionBar.setTitle(this.lastTitle);
        this.lastTitle = null;
      }
      updateCaptionTextForCurrentPhoto(localObject);
      setCurrentCaption(this.captionEditText.getFieldCharSequence());
      if (!this.captionEditText.isPopupShowing()) {
        break;
      }
      this.captionEditText.hidePopup();
      return;
      label309:
      if ((localObject instanceof MediaController.SearchImage)) {
        ((MediaController.SearchImage)localObject).caption = this.captionEditText.getFieldCharSequence();
      }
    }
    this.captionEditText.closeKeyboard();
  }
  
  private int getAdditionX()
  {
    if ((this.currentEditMode != 0) && (this.currentEditMode != 3)) {
      return AndroidUtilities.dp(14.0F);
    }
    return 0;
  }
  
  private int getAdditionY()
  {
    if (this.currentEditMode == 3) {
      return ActionBar.getCurrentActionBarHeight();
    }
    if (this.currentEditMode != 0) {
      return AndroidUtilities.dp(14.0F);
    }
    return 0;
  }
  
  private int getContainerViewHeight()
  {
    return getContainerViewHeight(this.currentEditMode);
  }
  
  private int getContainerViewHeight(int paramInt)
  {
    int j = AndroidUtilities.displaySize.y;
    int i;
    if (paramInt == 1) {
      i = j - AndroidUtilities.dp(76.0F);
    }
    do
    {
      return i;
      if (paramInt == 2) {
        return j - AndroidUtilities.dp(154.0F);
      }
      i = j;
    } while (paramInt != 3);
    return j - (AndroidUtilities.dp(48.0F) + ActionBar.getCurrentActionBarHeight());
  }
  
  private int getContainerViewWidth()
  {
    return getContainerViewWidth(this.currentEditMode);
  }
  
  private int getContainerViewWidth(int paramInt)
  {
    int j = this.containerView.getWidth();
    int i = j;
    if (paramInt != 0)
    {
      i = j;
      if (paramInt != 3) {
        i = j - AndroidUtilities.dp(28.0F);
      }
    }
    return i;
  }
  
  private TLRPC.FileLocation getFileLocation(int paramInt, int[] paramArrayOfInt)
  {
    if (paramInt < 0) {}
    Object localObject;
    do
    {
      do
      {
        do
        {
          return null;
          if (this.imagesArrLocations.isEmpty()) {
            break;
          }
        } while (paramInt >= this.imagesArrLocations.size());
        paramArrayOfInt[0] = ((Integer)this.imagesArrLocationsSizes.get(paramInt)).intValue();
        return (TLRPC.FileLocation)this.imagesArrLocations.get(paramInt);
      } while ((this.imagesArr.isEmpty()) || (paramInt >= this.imagesArr.size()));
      localObject = (MessageObject)this.imagesArr.get(paramInt);
      if ((((MessageObject)localObject).messageOwner instanceof TLRPC.TL_messageService))
      {
        if ((((MessageObject)localObject).messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto)) {
          return ((MessageObject)localObject).messageOwner.action.newUserPhoto.photo_big;
        }
        localObject = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject).photoThumbs, AndroidUtilities.getPhotoSize());
        if (localObject != null)
        {
          paramArrayOfInt[0] = ((TLRPC.PhotoSize)localObject).size;
          if (paramArrayOfInt[0] == 0) {
            paramArrayOfInt[0] = -1;
          }
          return ((TLRPC.PhotoSize)localObject).location;
        }
        paramArrayOfInt[0] = -1;
        return null;
      }
      if ((((((MessageObject)localObject).messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (((MessageObject)localObject).messageOwner.media.photo != null)) || (((((MessageObject)localObject).messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (((MessageObject)localObject).messageOwner.media.webpage != null)))
      {
        localObject = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject).photoThumbs, AndroidUtilities.getPhotoSize());
        if (localObject != null)
        {
          paramArrayOfInt[0] = ((TLRPC.PhotoSize)localObject).size;
          if (paramArrayOfInt[0] == 0) {
            paramArrayOfInt[0] = -1;
          }
          return ((TLRPC.PhotoSize)localObject).location;
        }
        paramArrayOfInt[0] = -1;
        return null;
      }
    } while ((((MessageObject)localObject).getDocument() == null) || (((MessageObject)localObject).getDocument().thumb == null));
    paramArrayOfInt[0] = ((MessageObject)localObject).getDocument().thumb.size;
    if (paramArrayOfInt[0] == 0) {
      paramArrayOfInt[0] = -1;
    }
    return ((MessageObject)localObject).getDocument().thumb.location;
  }
  
  private String getFileName(int paramInt)
  {
    if (paramInt < 0) {}
    Object localObject;
    label135:
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              return null;
              if ((this.imagesArrLocations.isEmpty()) && (this.imagesArr.isEmpty())) {
                break label135;
              }
              if (this.imagesArrLocations.isEmpty()) {
                break;
              }
            } while (paramInt >= this.imagesArrLocations.size());
            localObject = (TLRPC.FileLocation)this.imagesArrLocations.get(paramInt);
            return ((TLRPC.FileLocation)localObject).volume_id + "_" + ((TLRPC.FileLocation)localObject).local_id + ".jpg";
          } while ((this.imagesArr.isEmpty()) || (paramInt >= this.imagesArr.size()));
          return FileLoader.getMessageFileName(((MessageObject)this.imagesArr.get(paramInt)).messageOwner);
        } while ((this.imagesArrLocals.isEmpty()) || (paramInt >= this.imagesArrLocals.size()));
        localObject = this.imagesArrLocals.get(paramInt);
        if ((localObject instanceof MediaController.SearchImage))
        {
          localObject = (MediaController.SearchImage)localObject;
          if (((MediaController.SearchImage)localObject).document != null) {
            return FileLoader.getAttachFileName(((MediaController.SearchImage)localObject).document);
          }
          if ((((MediaController.SearchImage)localObject).type != 1) && (((MediaController.SearchImage)localObject).localUrl != null) && (((MediaController.SearchImage)localObject).localUrl.length() > 0))
          {
            File localFile = new File(((MediaController.SearchImage)localObject).localUrl);
            if (localFile.exists()) {
              return localFile.getName();
            }
            ((MediaController.SearchImage)localObject).localUrl = "";
          }
          return Utilities.MD5(((MediaController.SearchImage)localObject).imageUrl) + "." + ImageLoader.getHttpUrlExtension(((MediaController.SearchImage)localObject).imageUrl, "jpg");
        }
      } while (!(localObject instanceof TLRPC.BotInlineResult));
      localObject = (TLRPC.BotInlineResult)localObject;
      if (((TLRPC.BotInlineResult)localObject).document != null) {
        return FileLoader.getAttachFileName(((TLRPC.BotInlineResult)localObject).document);
      }
      if (((TLRPC.BotInlineResult)localObject).photo != null) {
        return FileLoader.getAttachFileName(FileLoader.getClosestPhotoSizeWithSize(((TLRPC.BotInlineResult)localObject).photo.sizes, AndroidUtilities.getPhotoSize()));
      }
    } while (((TLRPC.BotInlineResult)localObject).content_url == null);
    return Utilities.MD5(((TLRPC.BotInlineResult)localObject).content_url) + "." + ImageLoader.getHttpUrlExtension(((TLRPC.BotInlineResult)localObject).content_url, "jpg");
  }
  
  public static PhotoViewer getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          PhotoViewer localPhotoViewer2 = Instance;
          localObject1 = localPhotoViewer2;
          if (localPhotoViewer2 == null) {
            localObject1 = new PhotoViewer();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (PhotoViewer)localObject1;
          return (PhotoViewer)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localPhotoViewer1;
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
  
  private void onActionClick(boolean paramBoolean)
  {
    if (((this.currentMessageObject == null) && (this.currentBotInlineResult == null)) || (this.currentFileNames[0] == null)) {}
    Object localObject1;
    label289:
    label303:
    do
    {
      return;
      localObject2 = null;
      localObject1 = null;
      if (this.currentMessageObject != null)
      {
        localObject2 = localObject1;
        if (this.currentMessageObject.messageOwner.attachPath != null)
        {
          localObject2 = localObject1;
          if (this.currentMessageObject.messageOwner.attachPath.length() != 0)
          {
            localObject1 = new File(this.currentMessageObject.messageOwner.attachPath);
            localObject2 = localObject1;
            if (!((File)localObject1).exists()) {
              localObject2 = null;
            }
          }
        }
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject2 = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
          localObject1 = localObject2;
          if (!((File)localObject2).exists()) {
            localObject1 = null;
          }
        }
      }
      for (;;)
      {
        if (localObject1 != null) {
          break label412;
        }
        if (!paramBoolean) {
          break;
        }
        if (this.currentMessageObject == null) {
          break label303;
        }
        if (FileLoader.getInstance().isLoadingFile(this.currentFileNames[0])) {
          break label289;
        }
        FileLoader.getInstance().loadFile(this.currentMessageObject.getDocument(), true, false);
        return;
        localObject1 = localObject2;
        if (this.currentBotInlineResult != null) {
          if (this.currentBotInlineResult.document != null)
          {
            localObject2 = FileLoader.getPathToAttach(this.currentBotInlineResult.document);
            localObject1 = localObject2;
            if (!((File)localObject2).exists()) {
              localObject1 = null;
            }
          }
          else
          {
            localObject2 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.currentBotInlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.currentBotInlineResult.content_url, "mp4"));
            localObject1 = localObject2;
            if (!((File)localObject2).exists()) {
              localObject1 = null;
            }
          }
        }
      }
      FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.getDocument());
      return;
    } while (this.currentBotInlineResult == null);
    if (this.currentBotInlineResult.document != null)
    {
      if (!FileLoader.getInstance().isLoadingFile(this.currentFileNames[0]))
      {
        FileLoader.getInstance().loadFile(this.currentBotInlineResult.document, true, false);
        return;
      }
      FileLoader.getInstance().cancelLoadFile(this.currentBotInlineResult.document);
      return;
    }
    if (!ImageLoader.getInstance().isLoadingHttpFile(this.currentBotInlineResult.content_url))
    {
      ImageLoader.getInstance().loadHttpFile(this.currentBotInlineResult.content_url, "mp4");
      return;
    }
    ImageLoader.getInstance().cancelLoadHttpFile(this.currentBotInlineResult.content_url);
    return;
    label412:
    if (Build.VERSION.SDK_INT >= 16)
    {
      preparePlayer((File)localObject1, true);
      return;
    }
    Object localObject2 = new Intent("android.intent.action.VIEW");
    if (Build.VERSION.SDK_INT >= 24)
    {
      ((Intent)localObject2).setFlags(1);
      ((Intent)localObject2).setDataAndType(FileProvider.getUriForFile(this.parentActivity, "org.telegram.messenger.beta.provider", (File)localObject1), "video/mp4");
    }
    for (;;)
    {
      this.parentActivity.startActivityForResult((Intent)localObject2, 500);
      return;
      ((Intent)localObject2).setDataAndType(Uri.fromFile((File)localObject1), "video/mp4");
    }
  }
  
  @SuppressLint({"NewApi"})
  private void onDraw(Canvas paramCanvas)
  {
    if ((this.animationInProgress == 1) || ((!this.isVisible) && (this.animationInProgress != 2))) {}
    float f1;
    float f4;
    float f5;
    float f6;
    float f7;
    float f3;
    float f2;
    label282:
    Object localObject1;
    Object localObject2;
    label353:
    boolean bool;
    label361:
    int j;
    int i;
    label581:
    label850:
    label1075:
    do
    {
      return;
      f1 = -1.0F;
      if (this.imageMoveAnimation == null) {
        break;
      }
      if (!this.scroller.isFinished()) {
        this.scroller.abortAnimation();
      }
      f4 = this.scale;
      float f9 = this.animateToScale;
      float f10 = this.scale;
      float f11 = this.animationValue;
      f5 = this.translationX;
      f6 = this.animateToX;
      f7 = this.translationX;
      float f8 = this.animationValue;
      f3 = this.translationY + (this.animateToY - this.translationY) * this.animationValue;
      if (this.currentEditMode == 1) {
        this.photoCropView.setAnimationProgress(this.animationValue);
      }
      f2 = f1;
      if (this.animateToScale == 1.0F)
      {
        f2 = f1;
        if (this.scale == 1.0F)
        {
          f2 = f1;
          if (this.translationX == 0.0F) {
            f2 = f3;
          }
        }
      }
      f4 += (f9 - f10) * f11;
      f1 = f5 + (f6 - f7) * f8;
      this.containerView.invalidate();
      if ((this.currentEditMode != 0) || (this.scale != 1.0F) || (f2 == -1.0F) || (this.zoomAnimation)) {
        break label1928;
      }
      f5 = getContainerViewHeight() / 4.0F;
      this.backgroundDrawable.setAlpha((int)Math.max(127.0F, 255.0F * (1.0F - Math.min(Math.abs(f2), f5) / f5)));
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
                break label1941;
              }
              localObject1 = this.leftImage;
            }
          }
        }
        if (localObject1 == null) {
          break label1971;
        }
        bool = true;
        this.changingPage = bool;
      }
      if (localObject1 == this.rightImage)
      {
        f7 = f1;
        f8 = 0.0F;
        f9 = 1.0F;
        f6 = f9;
        f5 = f8;
        f2 = f7;
        if (!this.zoomAnimation)
        {
          f6 = f9;
          f5 = f8;
          f2 = f7;
          if (f7 < this.minX)
          {
            f6 = Math.min(1.0F, (this.minX - f7) / paramCanvas.getWidth());
            f5 = (1.0F - f6) * 0.3F;
            f2 = -paramCanvas.getWidth() - AndroidUtilities.dp(30.0F) / 2;
          }
        }
        if (((ImageReceiver)localObject1).hasBitmapImage())
        {
          paramCanvas.save();
          paramCanvas.translate(getContainerViewWidth() / 2, getContainerViewHeight() / 2);
          paramCanvas.translate(paramCanvas.getWidth() + AndroidUtilities.dp(30.0F) / 2 + f2, 0.0F);
          paramCanvas.scale(1.0F - f5, 1.0F - f5);
          j = ((ImageReceiver)localObject1).getBitmapWidth();
          i = ((ImageReceiver)localObject1).getBitmapHeight();
          f7 = getContainerViewWidth() / j;
          f8 = getContainerViewHeight() / i;
          if (f7 <= f8) {
            break label1977;
          }
          f7 = f8;
          j = (int)(j * f7);
          i = (int)(i * f7);
          ((ImageReceiver)localObject1).setAlpha(f6);
          ((ImageReceiver)localObject1).setImageCoords(-j / 2, -i / 2, j, i);
          ((ImageReceiver)localObject1).draw(paramCanvas);
          paramCanvas.restore();
        }
        paramCanvas.save();
        paramCanvas.translate(f2, f3 / f4);
        paramCanvas.translate((paramCanvas.getWidth() * (this.scale + 1.0F) + AndroidUtilities.dp(30.0F)) / 2.0F, -f3 / f4);
        this.radialProgressViews[1].setScale(1.0F - f5);
        this.radialProgressViews[1].setAlpha(f6);
        this.radialProgressViews[1].onDraw(paramCanvas);
        paramCanvas.restore();
      }
      f7 = f1;
      f8 = 0.0F;
      f9 = 1.0F;
      f6 = f9;
      f5 = f8;
      f2 = f7;
      if (!this.zoomAnimation)
      {
        f6 = f9;
        f5 = f8;
        f2 = f7;
        if (f7 > this.maxX)
        {
          f6 = f9;
          f5 = f8;
          f2 = f7;
          if (this.currentEditMode == 0)
          {
            f2 = Math.min(1.0F, (f7 - this.maxX) / paramCanvas.getWidth());
            f5 = f2 * 0.3F;
            f6 = 1.0F - f2;
            f2 = this.maxX;
          }
        }
      }
      if ((Build.VERSION.SDK_INT < 16) || (this.aspectRatioFrameLayout == null) || (this.aspectRatioFrameLayout.getVisibility() != 0)) {
        break label1980;
      }
      i = 1;
      if (this.centerImage.hasBitmapImage())
      {
        paramCanvas.save();
        paramCanvas.translate(getContainerViewWidth() / 2 + getAdditionX(), getContainerViewHeight() / 2 + getAdditionY());
        paramCanvas.translate(f2, f3);
        paramCanvas.scale(f4 - f5, f4 - f5);
        if (this.currentEditMode == 1) {
          this.photoCropView.setBitmapParams(f4, f2, f3);
        }
        int m = this.centerImage.getBitmapWidth();
        int n = this.centerImage.getBitmapHeight();
        j = n;
        int k = m;
        if (i != 0)
        {
          j = n;
          k = m;
          if (this.textureUploaded)
          {
            j = n;
            k = m;
            if (Math.abs(m / n - this.videoTextureView.getMeasuredWidth() / this.videoTextureView.getMeasuredHeight()) > 0.01F)
            {
              k = this.videoTextureView.getMeasuredWidth();
              j = this.videoTextureView.getMeasuredHeight();
            }
          }
        }
        f7 = getContainerViewWidth() / k;
        f8 = getContainerViewHeight() / j;
        if (f7 <= f8) {
          break label1986;
        }
        f7 = f8;
        k = (int)(k * f7);
        j = (int)(j * f7);
        if ((i == 0) || (!this.textureUploaded) || (!this.videoCrossfadeStarted) || (this.videoCrossfadeAlpha != 1.0F))
        {
          this.centerImage.setAlpha(f6);
          this.centerImage.setImageCoords(-k / 2, -j / 2, k, j);
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
          paramCanvas.translate(-k / 2, -j / 2);
          this.videoTextureView.setAlpha(this.videoCrossfadeAlpha * f6);
          this.aspectRatioFrameLayout.draw(paramCanvas);
          if ((this.videoCrossfadeStarted) && (this.videoCrossfadeAlpha < 1.0F))
          {
            long l1 = System.currentTimeMillis();
            long l2 = this.videoCrossfadeAlphaLastTime;
            this.videoCrossfadeAlphaLastTime = l1;
            this.videoCrossfadeAlpha += (float)(l1 - l2) / 300.0F;
            this.containerView.invalidate();
            if (this.videoCrossfadeAlpha > 1.0F) {
              this.videoCrossfadeAlpha = 1.0F;
            }
          }
        }
        paramCanvas.restore();
      }
      if ((i == 0) && ((this.videoPlayerControlFrameLayout == null) || (this.videoPlayerControlFrameLayout.getVisibility() != 0)))
      {
        paramCanvas.save();
        paramCanvas.translate(f2, f3 / f4);
        this.radialProgressViews[0].setScale(1.0F - f5);
        this.radialProgressViews[0].setAlpha(f6);
        this.radialProgressViews[0].onDraw(paramCanvas);
        paramCanvas.restore();
      }
    } while (localObject1 != this.leftImage);
    if (((ImageReceiver)localObject1).hasBitmapImage())
    {
      paramCanvas.save();
      paramCanvas.translate(getContainerViewWidth() / 2, getContainerViewHeight() / 2);
      paramCanvas.translate(-(paramCanvas.getWidth() * (this.scale + 1.0F) + AndroidUtilities.dp(30.0F)) / 2.0F + f1, 0.0F);
      j = ((ImageReceiver)localObject1).getBitmapWidth();
      i = ((ImageReceiver)localObject1).getBitmapHeight();
      f2 = getContainerViewWidth() / j;
      f5 = getContainerViewHeight() / i;
      if (f2 <= f5) {
        break label1989;
      }
      f2 = f5;
    }
    label1906:
    label1928:
    label1941:
    label1971:
    label1977:
    label1980:
    label1986:
    label1989:
    for (;;)
    {
      j = (int)(j * f2);
      i = (int)(i * f2);
      ((ImageReceiver)localObject1).setAlpha(1.0F);
      ((ImageReceiver)localObject1).setImageCoords(-j / 2, -i / 2, j, i);
      ((ImageReceiver)localObject1).draw(paramCanvas);
      paramCanvas.restore();
      paramCanvas.save();
      paramCanvas.translate(f1, f3 / f4);
      paramCanvas.translate(-(paramCanvas.getWidth() * (this.scale + 1.0F) + AndroidUtilities.dp(30.0F)) / 2.0F, -f3 / f4);
      this.radialProgressViews[2].setScale(1.0F);
      this.radialProgressViews[2].setAlpha(1.0F);
      this.radialProgressViews[2].onDraw(paramCanvas);
      paramCanvas.restore();
      return;
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
          break label1906;
        }
        setImageIndex(this.currentIndex + 1, false);
      }
      for (;;)
      {
        this.switchImageAfterAnimation = 0;
        f5 = this.scale;
        f6 = this.translationY;
        f7 = this.translationX;
        f2 = f1;
        f4 = f5;
        f1 = f7;
        f3 = f6;
        if (this.moving) {
          break;
        }
        f2 = this.translationY;
        f4 = f5;
        f1 = f7;
        f3 = f6;
        break;
        if (this.switchImageAfterAnimation == 2) {
          setImageIndex(this.currentIndex - 1, false);
        }
      }
      this.backgroundDrawable.setAlpha(255);
      break label282;
      localObject1 = localObject2;
      if (f1 >= this.minX - AndroidUtilities.dp(5.0F)) {
        break label353;
      }
      localObject1 = this.rightImage;
      break label353;
      bool = false;
      break label361;
      break label581;
      i = 0;
      break label850;
      break label1075;
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
    this.currentThumb = null;
    if (this.currentAnimation != null)
    {
      this.currentAnimation.setSecondParentView(null);
      this.currentAnimation = null;
    }
    int i = 0;
    while (i < 3)
    {
      if (this.radialProgressViews[i] != null) {
        this.radialProgressViews[i].setBackgroundState(-1, false);
      }
      i += 1;
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
          FileLog.e("tmessages", localException);
        }
      }
    });
    if (this.placeProvider != null) {
      this.placeProvider.willHidePhotoViewer();
    }
    this.placeProvider = null;
    this.disableShowCheck = false;
    if (paramPlaceProviderObject != null) {
      paramPlaceProviderObject.imageReceiver.setVisible(true, true);
    }
  }
  
  private void onPhotoShow(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, ArrayList<MessageObject> paramArrayList, ArrayList<Object> paramArrayList1, int paramInt, PlaceProviderObject paramPlaceProviderObject)
  {
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
    Object localObject = this.endReached;
    if (this.mergeDialogId == 0L) {}
    int i;
    for (boolean bool = true;; bool = false)
    {
      localObject[1] = bool;
      this.opennedFromMedia = false;
      this.needCaptionLayout = false;
      this.canShowBottom = true;
      this.imagesArr.clear();
      this.imagesArrLocations.clear();
      this.imagesArrLocationsSizes.clear();
      this.avatarsArr.clear();
      this.imagesArrLocals.clear();
      i = 0;
      while (i < 2)
      {
        this.imagesByIds[i].clear();
        this.imagesByIdsTemp[i].clear();
        i += 1;
      }
    }
    this.imagesArrTemp.clear();
    this.currentUserAvatarLocation = null;
    this.containerView.setPadding(0, 0, 0, 0);
    if (paramPlaceProviderObject != null) {}
    for (localObject = paramPlaceProviderObject.thumb;; localObject = null)
    {
      this.currentThumb = ((Bitmap)localObject);
      this.menuItem.setVisibility(0);
      this.bottomLayout.setVisibility(0);
      this.bottomLayout.setTranslationY(0.0F);
      this.shareButton.setVisibility(8);
      this.allowShare = false;
      this.menuItem.hideSubItem(2);
      this.menuItem.hideSubItem(10);
      this.menuItem.hideSubItem(11);
      this.actionBar.setTranslationY(0.0F);
      this.pickerView.setTranslationY(0.0F);
      this.checkImageView.setAlpha(1.0F);
      this.pickerView.setAlpha(1.0F);
      this.checkImageView.setVisibility(8);
      this.pickerView.setVisibility(8);
      this.paintItem.setVisibility(8);
      this.cropItem.setVisibility(8);
      this.tuneItem.setVisibility(8);
      this.captionDoneItem.setVisibility(8);
      this.captionEditText.setVisibility(8);
      this.mentionListView.setVisibility(8);
      this.muteItem.setVisibility(8);
      this.masksItem.setVisibility(8);
      this.muteItemAvailable = false;
      this.muteVideo = false;
      this.muteItem.setIcon(2130838030);
      this.editorDoneLayout.setVisibility(8);
      this.captionTextView.setTag(null);
      this.captionTextView.setVisibility(4);
      if (this.photoCropView != null) {
        this.photoCropView.setVisibility(8);
      }
      if (this.photoFilterView != null) {
        this.photoFilterView.setVisibility(8);
      }
      i = 0;
      while (i < 3)
      {
        if (this.radialProgressViews[i] != null) {
          this.radialProgressViews[i].setBackgroundState(-1, false);
        }
        i += 1;
      }
    }
    if ((paramMessageObject != null) && (paramArrayList == null))
    {
      this.imagesArr.add(paramMessageObject);
      if (this.currentAnimation != null) {
        this.needSearchImageInArr = false;
      }
    }
    label605:
    label721:
    label870:
    label933:
    label1223:
    label1290:
    label1343:
    label1364:
    label1563:
    label1569:
    label1575:
    label1582:
    label1593:
    label1622:
    do
    {
      do
      {
        do
        {
          break label721;
          break label721;
          break label721;
          setImageIndex(0, true);
          i = paramInt;
          if (this.currentAnimation == null)
          {
            if ((this.currentDialogId == 0L) || (this.totalImagesCount != 0)) {
              break label1593;
            }
            SharedMediaQuery.getMediaCount(this.currentDialogId, 0, this.classGuid, true);
            if (this.mergeDialogId != 0L) {
              SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
            }
          }
          for (;;)
          {
            if (((this.currentMessageObject == null) || (!this.currentMessageObject.isVideo())) && ((this.currentBotInlineResult == null) || ((!this.currentBotInlineResult.type.equals("video")) && (!MessageObject.isVideoDocument(this.currentBotInlineResult.document))))) {
              break label1622;
            }
            onActionClick(false);
            return;
            if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) || ((paramMessageObject.messageOwner.action != null) && (!(paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty)))) {
              break;
            }
            this.needSearchImageInArr = true;
            this.imagesByIds[0].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
            this.menuItem.showSubItem(2);
            break;
            if (paramFileLocation != null)
            {
              this.avatarsDialogId = paramPlaceProviderObject.dialogId;
              this.imagesArrLocations.add(paramFileLocation);
              this.imagesArrLocationsSizes.add(Integer.valueOf(paramPlaceProviderObject.size));
              this.avatarsArr.add(new TLRPC.TL_photoEmpty());
              paramMessageObject = this.shareButton;
              if ((this.videoPlayerControlFrameLayout == null) || (this.videoPlayerControlFrameLayout.getVisibility() != 0))
              {
                i = 0;
                paramMessageObject.setVisibility(i);
                this.allowShare = true;
                this.menuItem.hideSubItem(2);
                if (this.shareButton.getVisibility() != 0) {
                  break label933;
                }
                this.menuItem.hideSubItem(10);
              }
              for (;;)
              {
                setImageIndex(0, true);
                this.currentUserAvatarLocation = paramFileLocation;
                i = paramInt;
                break;
                i = 8;
                break label870;
                this.menuItem.showSubItem(10);
              }
            }
            if (paramArrayList != null)
            {
              this.menuItem.showSubItem(2);
              this.opennedFromMedia = true;
              this.imagesArr.addAll(paramArrayList);
              i = paramInt;
              if (!this.opennedFromMedia)
              {
                Collections.reverse(this.imagesArr);
                i = this.imagesArr.size() - paramInt - 1;
              }
              paramInt = 0;
              if (paramInt < this.imagesArr.size())
              {
                paramMessageObject = (MessageObject)this.imagesArr.get(paramInt);
                paramFileLocation = this.imagesByIds;
                if (paramMessageObject.getDialogId() == this.currentDialogId) {}
                for (int j = 0;; j = 1)
                {
                  paramFileLocation[j].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
                  paramInt += 1;
                  break;
                }
              }
              setImageIndex(i, true);
              break label605;
            }
            i = paramInt;
            if (paramArrayList1 == null) {
              break label605;
            }
            if (this.sendPhotoType == 0) {
              this.checkImageView.setVisibility(0);
            }
            this.menuItem.setVisibility(8);
            this.imagesArrLocals.addAll(paramArrayList1);
            setImageIndex(paramInt, true);
            this.pickerView.setVisibility(0);
            this.bottomLayout.setVisibility(8);
            this.canShowBottom = false;
            paramMessageObject = this.imagesArrLocals.get(paramInt);
            if ((paramMessageObject instanceof MediaController.PhotoEntry)) {
              if (((MediaController.PhotoEntry)paramMessageObject).isVideo)
              {
                this.cropItem.setVisibility(8);
                this.bottomLayout.setVisibility(0);
                this.bottomLayout.setTranslationY(-AndroidUtilities.dp(48.0F));
                if ((this.parentChatActivity != null) && ((this.parentChatActivity.currentEncryptedChat == null) || (AndroidUtilities.getPeerLayerVersion(this.parentChatActivity.currentEncryptedChat.layer) >= 46)))
                {
                  this.mentionsAdapter.setChatInfo(this.parentChatActivity.info);
                  paramMessageObject = this.mentionsAdapter;
                  if (this.parentChatActivity.currentChat == null) {
                    break label1563;
                  }
                  bool = true;
                  paramMessageObject.setNeedUsernames(bool);
                  this.mentionsAdapter.setNeedBotContext(false);
                  if ((this.cropItem.getVisibility() != 0) || ((this.placeProvider != null) && ((this.placeProvider == null) || (!this.placeProvider.allowCaption())))) {
                    break label1569;
                  }
                  bool = true;
                  this.needCaptionLayout = bool;
                  paramMessageObject = this.captionEditText;
                  if (!this.needCaptionLayout) {
                    break label1575;
                  }
                  i = 0;
                  paramMessageObject.setVisibility(i);
                  if ((this.captionTextView.getTag() != null) || (!this.needCaptionLayout)) {
                    break label1582;
                  }
                  this.captionTextView.setText(LocaleController.getString("AddCaption", 2131165255));
                  this.captionTextView.setTag("empty");
                  this.captionTextView.setTextColor(-1291845633);
                  this.captionTextView.setVisibility(0);
                }
              }
            }
            for (;;)
            {
              if (this.needCaptionLayout) {
                this.captionEditText.onCreate();
              }
              if (Build.VERSION.SDK_INT >= 16)
              {
                this.paintItem.setVisibility(this.cropItem.getVisibility());
                this.tuneItem.setVisibility(this.cropItem.getVisibility());
              }
              updateSelectedCount();
              i = paramInt;
              break;
              this.cropItem.setVisibility(0);
              break label1223;
              if ((paramMessageObject instanceof TLRPC.BotInlineResult))
              {
                this.cropItem.setVisibility(8);
                break label1223;
              }
              paramFileLocation = this.cropItem;
              if (((paramMessageObject instanceof MediaController.SearchImage)) && (((MediaController.SearchImage)paramMessageObject).type == 0)) {}
              for (i = 0;; i = 8)
              {
                paramFileLocation.setVisibility(i);
                break;
              }
              bool = false;
              break label1290;
              bool = false;
              break label1343;
              i = 8;
              break label1364;
              this.captionTextView.setTextColor(-1);
            }
            if (this.avatarsDialogId != 0) {
              MessagesController.getInstance().loadDialogPhotos(this.avatarsDialogId, 0, 80, 0L, true, this.classGuid);
            }
          }
        } while (this.imagesArrLocals.isEmpty());
        paramMessageObject = this.imagesArrLocals.get(i);
      } while (!(paramMessageObject instanceof MediaController.PhotoEntry));
      paramMessageObject = (MediaController.PhotoEntry)paramMessageObject;
    } while (!paramMessageObject.isVideo);
    preparePlayer(new File(paramMessageObject.path), false);
  }
  
  private void onSharePressed()
  {
    if ((this.parentActivity == null) || (!this.allowShare)) {
      return;
    }
    File localFile = null;
    boolean bool2 = false;
    Intent localIntent;
    label77:
    do
    {
      try
      {
        if (this.currentMessageObject != null)
        {
          bool1 = this.currentMessageObject.isVideo();
          localFile = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
          if (!localFile.exists()) {
            break label170;
          }
          localIntent = new Intent("android.intent.action.SEND");
          if (!bool1) {
            break;
          }
          localIntent.setType("video/mp4");
          localIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(localFile));
          this.parentActivity.startActivityForResult(Intent.createChooser(localIntent, LocaleController.getString("ShareFile", 2131166274)), 500);
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
        return;
      }
      bool1 = bool2;
    } while (this.currentFileLocation == null);
    Object localObject = this.currentFileLocation;
    if (this.avatarsDialogId != 0) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      localObject = FileLoader.getPathToAttach((TLObject)localObject, bool1);
      bool1 = bool2;
      break;
      localIntent.setType("image/jpeg");
      break label77;
      label170:
      localObject = new AlertDialog.Builder(this.parentActivity);
      ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165299));
      ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166044), null);
      ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PleaseDownload", 2131166127));
      showAlertDialog((AlertDialog.Builder)localObject);
      return;
    }
  }
  
  private boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.animationInProgress != 0) || (this.animationStartTime != 0L)) {
      return false;
    }
    if (this.currentEditMode == 2)
    {
      this.photoFilterView.onTouch(paramMotionEvent);
      return true;
    }
    if (this.currentEditMode == 1) {
      if (paramMotionEvent.getPointerCount() == 1)
      {
        if (this.photoCropView.onTouch(paramMotionEvent))
        {
          updateMinMax(this.scale);
          return true;
        }
      }
      else {
        this.photoCropView.onTouch(null);
      }
    }
    if ((this.captionEditText.isPopupShowing()) || (this.captionEditText.isKeyboardVisible()))
    {
      if (paramMotionEvent.getAction() == 1) {
        closeCaptionEnter(true);
      }
      return true;
    }
    if ((this.currentEditMode == 0) && (paramMotionEvent.getPointerCount() == 1) && (this.gestureDetector.onTouchEvent(paramMotionEvent)) && (this.doubleTap))
    {
      this.doubleTap = false;
      this.moving = false;
      this.zooming = false;
      checkMinMax(false);
      return true;
    }
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
          break label369;
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
    label369:
    label1075:
    label1454:
    label1501:
    do
    {
      do
      {
        for (;;)
        {
          return false;
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
                break;
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
                if ((f1 > AndroidUtilities.dp(3.0F)) || (f2 > AndroidUtilities.dp(3.0F))) {
                  this.discardTap = true;
                }
                if ((!(this.placeProvider instanceof EmptyPhotoViewerProvider)) && (this.currentEditMode == 0) && (this.canDragDown) && (!this.draggingDown) && (this.scale == 1.0F) && (f2 >= AndroidUtilities.dp(30.0F)) && (f2 / 2.0F > f1))
                {
                  this.draggingDown = true;
                  this.moving = false;
                  this.dragY = paramMotionEvent.getY();
                  if ((this.isActionBarVisible) && (this.canShowBottom)) {
                    toggleActionBar(false, true);
                  }
                  for (;;)
                  {
                    return true;
                    if (this.pickerView.getVisibility() == 0)
                    {
                      toggleActionBar(false, true);
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
                        break label1075;
                      }
                      if (this.currentEditMode == 0)
                      {
                        f3 = f1;
                        if (this.leftImage.hasImage()) {
                          break label1075;
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
                break label1501;
              }
              f2 = this.minY;
            }
            for (;;)
            {
              animateTo(3.0F, f1, f2, true);
              break;
              f1 = f2;
              if (f2 <= this.maxX) {
                break label1454;
              }
              f1 = this.maxX;
              break label1454;
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
    float f3 = this.translationX;
    float f2 = this.translationY;
    updateMinMax(this.scale);
    this.moving = false;
    this.canDragDown = true;
    float f4 = 0.0F;
    float f1 = f4;
    if (this.velocityTracker != null)
    {
      f1 = f4;
      if (this.scale == 1.0F)
      {
        this.velocityTracker.computeCurrentVelocity(1000);
        f1 = this.velocityTracker.getXVelocity();
      }
    }
    if (this.currentEditMode == 0)
    {
      if (((this.translationX < this.minX - getContainerViewWidth() / 3) || (f1 < -AndroidUtilities.dp(650.0F))) && (this.rightImage.hasImage()))
      {
        goToNext();
        return true;
      }
      if (((this.translationX > this.maxX + getContainerViewWidth() / 3) || (f1 > AndroidUtilities.dp(650.0F))) && (this.leftImage.hasImage()))
      {
        goToPrev();
        return true;
      }
    }
    if (this.translationX < this.minX)
    {
      f1 = this.minX;
      label1808:
      if (this.translationY >= this.minY) {
        break label1862;
      }
      f2 = this.minY;
    }
    for (;;)
    {
      animateTo(this.scale, f1, f2, false);
      break;
      f1 = f3;
      if (this.translationX <= this.maxX) {
        break label1808;
      }
      f1 = this.maxX;
      break label1808;
      label1862:
      if (this.translationY > this.maxY) {
        f2 = this.maxY;
      }
    }
  }
  
  private void openCaptionEnter()
  {
    if ((this.imageMoveAnimation != null) || (this.changeModeAnimation != null)) {
      return;
    }
    this.paintItem.setVisibility(8);
    this.cropItem.setVisibility(8);
    this.tuneItem.setVisibility(8);
    this.checkImageView.setVisibility(8);
    this.captionDoneItem.setVisibility(0);
    this.pickerView.setVisibility(8);
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.captionEditText.getLayoutParams();
    localLayoutParams.bottomMargin = 0;
    this.captionEditText.setLayoutParams(localLayoutParams);
    localLayoutParams = (FrameLayout.LayoutParams)this.mentionListView.getLayoutParams();
    localLayoutParams.bottomMargin = 0;
    this.mentionListView.setLayoutParams(localLayoutParams);
    this.captionTextView.setVisibility(4);
    this.captionEditText.openKeyboard();
    this.lastTitle = this.actionBar.getTitle();
    this.actionBar.setTitle(LocaleController.getString("PhotoCaption", 2131166112));
  }
  
  @SuppressLint({"NewApi"})
  private void preparePlayer(File paramFile, boolean paramBoolean)
  {
    if (this.parentActivity == null) {
      return;
    }
    releasePlayer();
    if (this.videoTextureView == null)
    {
      this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity);
      this.aspectRatioFrameLayout.setVisibility(4);
      this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
      this.videoTextureView = new TextureView(this.parentActivity);
      this.videoTextureView.setOpaque(false);
      this.videoTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
      {
        public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if (PhotoViewer.this.videoPlayer != null) {
            PhotoViewer.this.videoPlayer.setSurface(new Surface(PhotoViewer.this.videoTextureView.getSurfaceTexture()));
          }
        }
        
        public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
        {
          if (PhotoViewer.this.videoPlayer != null) {
            PhotoViewer.this.videoPlayer.blockingClearSurface();
          }
          return true;
        }
        
        public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2) {}
        
        public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture)
        {
          if (!PhotoViewer.this.textureUploaded)
          {
            PhotoViewer.access$6702(PhotoViewer.this, true);
            PhotoViewer.this.containerView.invalidate();
          }
        }
      });
      this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
    }
    this.textureUploaded = false;
    this.videoCrossfadeStarted = false;
    TextureView localTextureView = this.videoTextureView;
    this.videoCrossfadeAlpha = 0.0F;
    localTextureView.setAlpha(0.0F);
    this.videoPlayButton.setImageResource(2130837776);
    if (this.videoPlayer == null)
    {
      this.videoPlayer = new VideoPlayer(new VideoPlayer.ExtractorRendererBuilder(this.parentActivity, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36", Uri.fromFile(paramFile)));
      this.videoPlayer.addListener(new VideoPlayer.Listener()
      {
        public void onError(Exception paramAnonymousException)
        {
          FileLog.e("tmessages", paramAnonymousException);
        }
        
        public void onStateChanged(boolean paramAnonymousBoolean, int paramAnonymousInt)
        {
          if (PhotoViewer.this.videoPlayer == null) {
            return;
          }
          if ((paramAnonymousInt != 5) && (paramAnonymousInt != 1)) {}
          for (;;)
          {
            try
            {
              PhotoViewer.this.parentActivity.getWindow().addFlags(128);
              if ((paramAnonymousInt == 4) && (PhotoViewer.this.aspectRatioFrameLayout.getVisibility() != 0)) {
                PhotoViewer.this.aspectRatioFrameLayout.setVisibility(0);
              }
              if ((!PhotoViewer.this.videoPlayer.getPlayerControl().isPlaying()) || (paramAnonymousInt == 5)) {
                break label175;
              }
              if (!PhotoViewer.this.isPlaying)
              {
                PhotoViewer.access$402(PhotoViewer.this, true);
                PhotoViewer.this.videoPlayButton.setImageResource(2130837775);
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable);
              }
              PhotoViewer.this.updateVideoPlayerTime();
              return;
            }
            catch (Exception localException1)
            {
              FileLog.e("tmessages", localException1);
              continue;
            }
            try
            {
              PhotoViewer.this.parentActivity.getWindow().clearFlags(128);
            }
            catch (Exception localException2)
            {
              FileLog.e("tmessages", localException2);
            }
            continue;
            label175:
            if (PhotoViewer.this.isPlaying)
            {
              PhotoViewer.access$402(PhotoViewer.this, false);
              PhotoViewer.this.videoPlayButton.setImageResource(2130837776);
              AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.updateProgressRunnable);
              if ((paramAnonymousInt == 5) && (!PhotoViewer.this.videoPlayerSeekbar.isDragging()))
              {
                PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0F);
                PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                PhotoViewer.this.videoPlayer.seekTo(0L);
                PhotoViewer.this.videoPlayer.getPlayerControl().pause();
              }
            }
          }
        }
        
        public void onVideoSizeChanged(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, float paramAnonymousFloat)
        {
          int j;
          int i;
          AspectRatioFrameLayout localAspectRatioFrameLayout;
          if (PhotoViewer.this.aspectRatioFrameLayout != null)
          {
            if (paramAnonymousInt3 != 90)
            {
              j = paramAnonymousInt1;
              i = paramAnonymousInt2;
              if (paramAnonymousInt3 != 270) {}
            }
            else
            {
              i = paramAnonymousInt1;
              j = paramAnonymousInt2;
            }
            localAspectRatioFrameLayout = PhotoViewer.this.aspectRatioFrameLayout;
            if (i != 0) {
              break label61;
            }
          }
          label61:
          for (paramAnonymousFloat = 1.0F;; paramAnonymousFloat = j * paramAnonymousFloat / i)
          {
            localAspectRatioFrameLayout.setAspectRatio(paramAnonymousFloat, paramAnonymousInt3);
            return;
          }
        }
      });
      if (this.videoPlayer == null) {
        break label500;
      }
      long l2 = this.videoPlayer.getDuration();
      l1 = l2;
      if (l2 != -1L) {}
    }
    label500:
    for (long l1 = 0L;; l1 = 0L)
    {
      l1 /= 1000L;
      int i = (int)Math.ceil(this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L), Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L) })));
      this.playerNeedsPrepare = true;
      if (this.playerNeedsPrepare)
      {
        this.videoPlayer.prepare();
        this.playerNeedsPrepare = false;
      }
      if (this.videoPlayerControlFrameLayout != null)
      {
        if ((this.currentBotInlineResult != null) && ((this.currentBotInlineResult.type.equals("video")) || (MessageObject.isVideoDocument(this.currentBotInlineResult.document))))
        {
          this.bottomLayout.setVisibility(0);
          this.bottomLayout.setTranslationY(-AndroidUtilities.dp(48.0F));
        }
        this.videoPlayerControlFrameLayout.setVisibility(0);
        this.dateTextView.setVisibility(8);
        this.nameTextView.setVisibility(8);
        if (this.allowShare)
        {
          this.shareButton.setVisibility(8);
          this.menuItem.showSubItem(10);
        }
      }
      if (this.videoTextureView.getSurfaceTexture() != null) {
        this.videoPlayer.setSurface(new Surface(this.videoTextureView.getSurfaceTexture()));
      }
      this.videoPlayer.setPlayWhenReady(paramBoolean);
      return;
    }
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
      this.videoPlayer.release();
      this.videoPlayer = null;
    }
    try
    {
      this.parentActivity.getWindow().clearFlags(128);
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
        this.videoPlayButton.setImageResource(2130837776);
        AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
      }
      if (this.videoPlayerControlFrameLayout != null)
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
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  private void setCurrentCaption(CharSequence paramCharSequence)
  {
    if ((paramCharSequence != null) && (paramCharSequence.length() > 0))
    {
      this.captionTextView = this.captionTextViewOld;
      this.captionTextViewOld = this.captionTextViewNew;
      this.captionTextViewNew = this.captionTextView;
      paramCharSequence = Emoji.replaceEmoji(new SpannableStringBuilder(paramCharSequence.toString()), MessageObject.getTextPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      this.captionTextView.setTag(paramCharSequence);
      this.captionTextView.setText(paramCharSequence);
      this.captionTextView.setTextColor(-1);
      paramCharSequence = this.captionTextView;
      if ((this.bottomLayout.getVisibility() == 0) || (this.pickerView.getVisibility() == 0)) {}
      for (float f = 1.0F;; f = 0.0F)
      {
        paramCharSequence.setAlpha(f);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int i = 4;
            PhotoViewer.this.captionTextViewOld.setTag(null);
            PhotoViewer.this.captionTextViewOld.setVisibility(4);
            TextView localTextView = PhotoViewer.this.captionTextViewNew;
            if ((PhotoViewer.this.bottomLayout.getVisibility() == 0) || (PhotoViewer.this.pickerView.getVisibility() == 0)) {
              i = 0;
            }
            localTextView.setVisibility(i);
          }
        });
        return;
      }
    }
    if (this.needCaptionLayout)
    {
      this.captionTextView.setText(LocaleController.getString("AddCaption", 2131165255));
      this.captionTextView.setTag("empty");
      this.captionTextView.setVisibility(0);
      this.captionTextView.setTextColor(-1291845633);
      return;
    }
    this.captionTextView.setTextColor(-1);
    this.captionTextView.setTag(null);
    this.captionTextView.setVisibility(4);
  }
  
  private void setImageIndex(int paramInt, boolean paramBoolean)
  {
    if ((this.currentIndex == paramInt) || (this.placeProvider == null)) {}
    int n;
    label182:
    label225:
    label251:
    label272:
    label320:
    label446:
    label532:
    label558:
    label609:
    label842:
    label847:
    label859:
    label871:
    label884:
    label941:
    label953:
    label1033:
    label1143:
    label1286:
    label1341:
    label1532:
    label1550:
    label1623:
    label1697:
    label1997:
    label2116:
    label2129:
    label2437:
    label2456:
    label2504:
    label2515:
    label2526:
    label2531:
    do
    {
      return;
      if (!paramBoolean) {
        this.currentThumb = null;
      }
      this.currentFileNames[0] = getFileName(paramInt);
      this.currentFileNames[1] = getFileName(paramInt + 1);
      this.currentFileNames[2] = getFileName(paramInt - 1);
      this.placeProvider.willSwitchFromPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
      n = this.currentIndex;
      this.currentIndex = paramInt;
      boolean bool = false;
      paramBoolean = false;
      int i = 0;
      int j = 0;
      long l;
      if (!this.imagesArr.isEmpty())
      {
        if ((this.currentIndex < 0) || (this.currentIndex >= this.imagesArr.size()))
        {
          closePhoto(false, false);
          return;
        }
        localObject1 = (MessageObject)this.imagesArr.get(this.currentIndex);
        if ((this.currentMessageObject != null) && (this.currentMessageObject.getId() == ((MessageObject)localObject1).getId()))
        {
          paramInt = 1;
          this.currentMessageObject = ((MessageObject)localObject1);
          paramBoolean = this.currentMessageObject.isVideo();
          localObject1 = this.masksItem;
          if ((!this.currentMessageObject.hasPhotoStickers()) || ((int)this.currentMessageObject.getDialogId() == 0)) {
            break label842;
          }
          i = 0;
          ((ActionBarMenuItem)localObject1).setVisibility(i);
          if (!this.currentMessageObject.canDeleteMessage(null)) {
            break label847;
          }
          this.menuItem.showSubItem(6);
          if ((!paramBoolean) || (Build.VERSION.SDK_INT < 16)) {
            break label859;
          }
          this.menuItem.showSubItem(11);
          if (!this.currentMessageObject.isFromUser()) {
            break label884;
          }
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
          if (localObject1 == null) {
            break label871;
          }
          this.nameTextView.setText(UserObject.getUserName((TLRPC.User)localObject1));
          l = this.currentMessageObject.messageOwner.date * 1000L;
          localObject1 = LocaleController.formatString("formatDateAtTime", 2131166435, new Object[] { LocaleController.getInstance().formatterYear.format(new Date(l)), LocaleController.getInstance().formatterDay.format(new Date(l)) });
          if ((this.currentFileNames[0] == null) || (!paramBoolean)) {
            break label941;
          }
          this.dateTextView.setText(String.format("%s (%s)", new Object[] { localObject1, AndroidUtilities.formatFileSize(this.currentMessageObject.getDocument().size) }));
          setCurrentCaption(this.currentMessageObject.caption);
          if (this.currentAnimation == null) {
            break label953;
          }
          this.menuItem.hideSubItem(1);
          this.menuItem.hideSubItem(10);
          if (!this.currentMessageObject.canDeleteMessage(null)) {
            this.menuItem.setVisibility(8);
          }
          this.allowShare = true;
          this.shareButton.setVisibility(0);
          this.actionBar.setTitle(LocaleController.getString("AttachGif", 2131165340));
          i = paramInt;
          if (this.currentPlaceObject != null)
          {
            if (this.animationInProgress != 0) {
              break label2504;
            }
            this.currentPlaceObject.imageReceiver.setVisible(true, true);
          }
          this.currentPlaceObject = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
          if (this.currentPlaceObject != null)
          {
            if (this.animationInProgress != 0) {
              break label2515;
            }
            this.currentPlaceObject.imageReceiver.setVisible(false, true);
          }
          if (i == 0)
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
            releasePlayer();
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
            if ((this.imagesArrLocals.isEmpty()) && ((this.currentFileNames[0] == null) || (paramBoolean) || (this.radialProgressViews[0].backgroundState == 0))) {
              break label2526;
            }
          }
        }
      }
      for (paramBoolean = true;; paramBoolean = false)
      {
        this.canZoom = paramBoolean;
        updateMinMax(this.scale);
        if (n != -1) {
          break label2531;
        }
        setImages();
        paramInt = 0;
        while (paramInt < 3)
        {
          checkProgress(paramInt, false);
          paramInt += 1;
        }
        break;
        paramInt = 0;
        break label182;
        i = 4;
        break label225;
        this.menuItem.hideSubItem(6);
        break label251;
        this.menuItem.hideSubItem(11);
        break label272;
        this.nameTextView.setText("");
        break label320;
        localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
        if (localObject1 != null)
        {
          this.nameTextView.setText(((TLRPC.Chat)localObject1).title);
          break label320;
        }
        this.nameTextView.setText("");
        break label320;
        this.dateTextView.setText((CharSequence)localObject1);
        break label446;
        int m;
        int k;
        if ((this.totalImagesCount + this.totalImagesCountMerge != 0) && (!this.needSearchImageInArr)) {
          if (this.opennedFromMedia) {
            if ((this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge) && (!this.loadingMoreImages) && (this.currentIndex > this.imagesArr.size() - 5))
            {
              if (this.imagesArr.isEmpty())
              {
                i = 0;
                m = 0;
                k = i;
                j = m;
                if (this.endReached[0] != 0)
                {
                  k = i;
                  j = m;
                  if (this.mergeDialogId != 0L)
                  {
                    m = 1;
                    k = i;
                    j = m;
                    if (!this.imagesArr.isEmpty())
                    {
                      k = i;
                      j = m;
                      if (((MessageObject)this.imagesArr.get(this.imagesArr.size() - 1)).getDialogId() != this.mergeDialogId)
                      {
                        k = 0;
                        j = m;
                      }
                    }
                  }
                }
                if (j != 0) {
                  break label1286;
                }
                l = this.currentDialogId;
                SharedMediaQuery.loadMedia(l, 0, 80, k, 0, true, this.classGuid);
                this.loadingMoreImages = true;
              }
            }
            else {
              this.actionBar.setTitle(LocaleController.formatString("Of", 2131166045, new Object[] { Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
            }
          }
        }
        for (;;)
        {
          if (this.currentMessageObject.messageOwner.ttl == 0) {
            break label1623;
          }
          this.allowShare = false;
          this.menuItem.hideSubItem(1);
          this.shareButton.setVisibility(8);
          this.menuItem.hideSubItem(10);
          i = paramInt;
          break;
          i = ((MessageObject)this.imagesArr.get(this.imagesArr.size() - 1)).getId();
          break label1033;
          l = this.mergeDialogId;
          break label1143;
          if ((this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge) && (!this.loadingMoreImages) && (this.currentIndex < 5))
          {
            if (!this.imagesArr.isEmpty()) {
              break label1532;
            }
            i = 0;
            m = 0;
            k = i;
            j = m;
            if (this.endReached[0] != 0)
            {
              k = i;
              j = m;
              if (this.mergeDialogId != 0L)
              {
                m = 1;
                k = i;
                j = m;
                if (!this.imagesArr.isEmpty())
                {
                  k = i;
                  j = m;
                  if (((MessageObject)this.imagesArr.get(0)).getDialogId() != this.mergeDialogId)
                  {
                    k = 0;
                    j = m;
                  }
                }
              }
            }
            if (j != 0) {
              break label1550;
            }
          }
          for (l = this.currentDialogId;; l = this.mergeDialogId)
          {
            SharedMediaQuery.loadMedia(l, 0, 80, k, 0, true, this.classGuid);
            this.loadingMoreImages = true;
            this.actionBar.setTitle(LocaleController.formatString("Of", 2131166045, new Object[] { Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge - this.imagesArr.size() + this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
            break;
            i = ((MessageObject)this.imagesArr.get(0)).getId();
            break label1341;
          }
          if ((this.currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
            if (this.currentMessageObject.isVideo()) {
              this.actionBar.setTitle(LocaleController.getString("AttachVideo", 2131165345));
            } else {
              this.actionBar.setTitle(LocaleController.getString("AttachPhoto", 2131165343));
            }
          }
        }
        this.allowShare = true;
        this.menuItem.showSubItem(1);
        localObject1 = this.shareButton;
        if ((this.videoPlayerControlFrameLayout == null) || (this.videoPlayerControlFrameLayout.getVisibility() != 0)) {}
        for (i = 0;; i = 8)
        {
          ((ImageView)localObject1).setVisibility(i);
          if (this.shareButton.getVisibility() != 0) {
            break label1697;
          }
          this.menuItem.hideSubItem(10);
          i = paramInt;
          break;
        }
        this.menuItem.showSubItem(10);
        i = paramInt;
        break label532;
        if (!this.imagesArrLocations.isEmpty())
        {
          this.nameTextView.setText("");
          this.dateTextView.setText("");
          if ((this.avatarsDialogId == UserConfig.getClientUserId()) && (!this.avatarsArr.isEmpty())) {
            this.menuItem.showSubItem(6);
          }
          for (;;)
          {
            localObject1 = this.currentFileLocation;
            if ((paramInt >= 0) && (paramInt < this.imagesArrLocations.size())) {
              break;
            }
            closePhoto(false, false);
            return;
            this.menuItem.hideSubItem(6);
          }
          this.currentFileLocation = ((TLRPC.FileLocation)this.imagesArrLocations.get(paramInt));
          paramInt = i;
          if (localObject1 != null)
          {
            paramInt = i;
            if (this.currentFileLocation != null)
            {
              paramInt = i;
              if (((TLRPC.FileLocation)localObject1).local_id == this.currentFileLocation.local_id)
              {
                paramInt = i;
                if (((TLRPC.FileLocation)localObject1).volume_id == this.currentFileLocation.volume_id) {
                  paramInt = 1;
                }
              }
            }
          }
          this.actionBar.setTitle(LocaleController.formatString("Of", 2131166045, new Object[] { Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.imagesArrLocations.size()) }));
          this.menuItem.showSubItem(1);
          this.allowShare = true;
          localObject1 = this.shareButton;
          if ((this.videoPlayerControlFrameLayout == null) || (this.videoPlayerControlFrameLayout.getVisibility() != 0)) {}
          for (i = 0;; i = 8)
          {
            ((ImageView)localObject1).setVisibility(i);
            if (this.shareButton.getVisibility() != 0) {
              break label1997;
            }
            this.menuItem.hideSubItem(10);
            i = paramInt;
            break;
          }
          this.menuItem.showSubItem(10);
          i = paramInt;
          break label532;
        }
        i = j;
        if (this.imagesArrLocals.isEmpty()) {
          break label532;
        }
        if ((paramInt < 0) || (paramInt >= this.imagesArrLocals.size()))
        {
          closePhoto(false, false);
          return;
        }
        Object localObject2 = this.imagesArrLocals.get(paramInt);
        i = 0;
        MediaController.PhotoEntry localPhotoEntry = null;
        if ((localObject2 instanceof MediaController.PhotoEntry))
        {
          localPhotoEntry = (MediaController.PhotoEntry)localObject2;
          this.currentPathObject = localPhotoEntry.path;
          if ((localPhotoEntry.bucketId == 0) && (localPhotoEntry.dateTaken == 0L) && (this.imagesArrLocals.size() == 1))
          {
            paramInt = 1;
            localObject1 = localPhotoEntry.caption;
            paramBoolean = localPhotoEntry.isVideo;
            if (paramInt == 0) {
              break label2456;
            }
            if (!paramBoolean) {
              break label2437;
            }
            this.muteItemAvailable = true;
            this.actionBar.setTitle(LocaleController.getString("AttachVideo", 2131165345));
          }
        }
        for (;;)
        {
          if (this.sendPhotoType == 0) {
            this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), false);
          }
          setCurrentCaption((CharSequence)localObject1);
          updateCaptionTextForCurrentPhoto(localObject2);
          i = j;
          break;
          paramInt = 0;
          break label2116;
          if ((localObject2 instanceof TLRPC.BotInlineResult))
          {
            TLRPC.BotInlineResult localBotInlineResult = (TLRPC.BotInlineResult)localObject2;
            this.currentBotInlineResult = localBotInlineResult;
            if (localBotInlineResult.document != null)
            {
              paramBoolean = MessageObject.isVideoDocument(localBotInlineResult.document);
              this.currentPathObject = FileLoader.getPathToAttach(localBotInlineResult.document).getAbsolutePath();
              localObject1 = localPhotoEntry;
              paramInt = i;
              break label2129;
            }
            if (localBotInlineResult.photo != null)
            {
              this.currentPathObject = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(localBotInlineResult.photo.sizes, AndroidUtilities.getPhotoSize())).getAbsolutePath();
              localObject1 = localPhotoEntry;
              paramInt = i;
              paramBoolean = bool;
              break label2129;
            }
            localObject1 = localPhotoEntry;
            paramInt = i;
            paramBoolean = bool;
            if (localBotInlineResult.content_url == null) {
              break label2129;
            }
            this.currentPathObject = localBotInlineResult.content_url;
            paramBoolean = localBotInlineResult.type.equals("video");
            localObject1 = localPhotoEntry;
            paramInt = i;
            break label2129;
          }
          localObject1 = localPhotoEntry;
          paramInt = i;
          paramBoolean = bool;
          if (!(localObject2 instanceof MediaController.SearchImage)) {
            break label2129;
          }
          localObject1 = (MediaController.SearchImage)localObject2;
          if (((MediaController.SearchImage)localObject1).document != null) {}
          for (this.currentPathObject = FileLoader.getPathToAttach(((MediaController.SearchImage)localObject1).document, true).getAbsolutePath();; this.currentPathObject = ((MediaController.SearchImage)localObject1).imageUrl)
          {
            localObject1 = ((MediaController.SearchImage)localObject1).caption;
            paramInt = i;
            paramBoolean = bool;
            break;
          }
          this.actionBar.setTitle(LocaleController.getString("AttachPhoto", 2131165343));
          continue;
          this.actionBar.setTitle(LocaleController.formatString("Of", 2131166045, new Object[] { Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.imagesArrLocals.size()) }));
        }
        this.showAfterAnimation = this.currentPlaceObject;
        break label558;
        this.hideAfterAnimation = this.currentPlaceObject;
        break label609;
      }
      checkProgress(0, false);
      if (n > this.currentIndex)
      {
        localObject1 = this.rightImage;
        this.rightImage = this.centerImage;
        this.centerImage = this.leftImage;
        this.leftImage = ((ImageReceiver)localObject1);
        localObject1 = this.radialProgressViews[0];
        this.radialProgressViews[0] = this.radialProgressViews[2];
        this.radialProgressViews[2] = localObject1;
        setIndexToImage(this.leftImage, this.currentIndex - 1);
        checkProgress(1, false);
        checkProgress(2, false);
        return;
      }
    } while (n >= this.currentIndex);
    Object localObject1 = this.leftImage;
    this.leftImage = this.centerImage;
    this.centerImage = this.rightImage;
    this.rightImage = ((ImageReceiver)localObject1);
    localObject1 = this.radialProgressViews[0];
    this.radialProgressViews[0] = this.radialProgressViews[1];
    this.radialProgressViews[1] = localObject1;
    setIndexToImage(this.rightImage, this.currentIndex + 1);
    checkProgress(1, false);
    checkProgress(2, false);
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
    Object localObject2;
    Object localObject1;
    Object localObject3;
    if (!this.imagesArrLocals.isEmpty())
    {
      paramImageReceiver.setParentMessageObject(null);
      if ((paramInt >= 0) && (paramInt < this.imagesArrLocals.size()))
      {
        Object localObject9 = this.imagesArrLocals.get(paramInt);
        int k = (int)(AndroidUtilities.getPhotoSize() / AndroidUtilities.density);
        localObject2 = null;
        localObject1 = localObject2;
        if (this.currentThumb != null)
        {
          localObject1 = localObject2;
          if (paramImageReceiver == this.centerImage) {
            localObject1 = this.currentThumb;
          }
        }
        localObject5 = localObject1;
        if (localObject1 == null) {
          localObject5 = this.placeProvider.getThumbForPhoto(null, null, paramInt);
        }
        Object localObject8 = null;
        localObject2 = null;
        TLRPC.BotInlineResult localBotInlineResult = null;
        Object localObject7 = null;
        Object localObject6 = null;
        int j = 0;
        int i = 0;
        localObject1 = null;
        if ((localObject9 instanceof MediaController.PhotoEntry))
        {
          localObject1 = (MediaController.PhotoEntry)localObject9;
          if (((MediaController.PhotoEntry)localObject1).imagePath != null)
          {
            localObject2 = ((MediaController.PhotoEntry)localObject1).imagePath;
            localObject1 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(k), Integer.valueOf(k) });
            localObject4 = localObject6;
            paramInt = i;
            localObject3 = localObject7;
            label206:
            if (localObject3 == null) {
              break label738;
            }
            if (localObject5 == null) {
              break label726;
            }
            localObject1 = new BitmapDrawable(null, (Bitmap)localObject5);
            label228:
            if (localObject5 != null) {
              break label732;
            }
          }
        }
        label726:
        label732:
        for (localObject2 = ((TLRPC.Document)localObject3).thumb.location;; localObject2 = null)
        {
          paramImageReceiver.setImage((TLObject)localObject3, null, "d", (Drawable)localObject1, (TLRPC.FileLocation)localObject2, String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(k), Integer.valueOf(k) }), paramInt, null, false);
          return;
          paramImageReceiver.setOrientation(((MediaController.PhotoEntry)localObject1).orientation, false);
          localObject2 = ((MediaController.PhotoEntry)localObject1).path;
          break;
          if ((localObject9 instanceof TLRPC.BotInlineResult))
          {
            localBotInlineResult = (TLRPC.BotInlineResult)localObject9;
            if ((localBotInlineResult.type.equals("video")) || (MessageObject.isVideoDocument(localBotInlineResult.document)))
            {
              if (localBotInlineResult.document != null)
              {
                localObject4 = localBotInlineResult.document.thumb.location;
                localObject3 = localObject7;
                paramInt = i;
                break label206;
              }
              localObject2 = localBotInlineResult.thumb_url;
              localObject3 = localObject7;
              paramInt = i;
              localObject4 = localObject6;
              break label206;
            }
            if ((localBotInlineResult.type.equals("gif")) && (localBotInlineResult.document != null))
            {
              localObject3 = localBotInlineResult.document;
              paramInt = localBotInlineResult.document.size;
              localObject1 = "d";
              localObject4 = localObject6;
              break label206;
            }
            if (localBotInlineResult.photo != null)
            {
              localObject1 = FileLoader.getClosestPhotoSizeWithSize(localBotInlineResult.photo.sizes, AndroidUtilities.getPhotoSize());
              localObject4 = ((TLRPC.PhotoSize)localObject1).location;
              paramInt = ((TLRPC.PhotoSize)localObject1).size;
              localObject1 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(k), Integer.valueOf(k) });
              localObject3 = localObject7;
              break label206;
            }
            localObject3 = localObject7;
            paramInt = i;
            localObject4 = localObject6;
            if (localBotInlineResult.content_url == null) {
              break label206;
            }
            if (localBotInlineResult.type.equals("gif")) {}
            for (localObject1 = "d";; localObject1 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(k), Integer.valueOf(k) }))
            {
              localObject2 = localBotInlineResult.content_url;
              localObject3 = localObject7;
              paramInt = i;
              localObject4 = localObject6;
              break;
            }
          }
          localObject3 = localObject7;
          paramInt = i;
          localObject4 = localObject6;
          if (!(localObject9 instanceof MediaController.SearchImage)) {
            break label206;
          }
          localObject1 = (MediaController.SearchImage)localObject9;
          if (((MediaController.SearchImage)localObject1).imagePath != null)
          {
            localObject2 = ((MediaController.SearchImage)localObject1).imagePath;
            paramInt = j;
            localObject3 = localBotInlineResult;
          }
          for (;;)
          {
            localObject1 = "d";
            localObject4 = localObject6;
            break;
            if (((MediaController.SearchImage)localObject1).document != null)
            {
              localObject3 = ((MediaController.SearchImage)localObject1).document;
              paramInt = ((MediaController.SearchImage)localObject1).document.size;
              localObject2 = localObject8;
            }
            else
            {
              localObject2 = ((MediaController.SearchImage)localObject1).imageUrl;
              paramInt = ((MediaController.SearchImage)localObject1).size;
              localObject3 = localBotInlineResult;
            }
          }
          localObject1 = null;
          break label228;
        }
        label738:
        if (localObject4 != null)
        {
          if (localObject5 != null) {}
          for (localObject2 = new BitmapDrawable(null, (Bitmap)localObject5);; localObject2 = null)
          {
            paramImageReceiver.setImage((TLObject)localObject4, null, (String)localObject1, (Drawable)localObject2, null, String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(k), Integer.valueOf(k) }), paramInt, null, false);
            return;
          }
        }
        if (localObject5 != null) {}
        for (localObject3 = new BitmapDrawable(null, (Bitmap)localObject5);; localObject3 = null)
        {
          paramImageReceiver.setImage((String)localObject2, (String)localObject1, (Drawable)localObject3, null, paramInt);
          return;
        }
      }
      paramImageReceiver.setImageBitmap((Bitmap)null);
      return;
    }
    Object localObject4 = new int[1];
    Object localObject5 = getFileLocation(paramInt, (int[])localObject4);
    if (localObject5 != null)
    {
      localObject1 = null;
      if (!this.imagesArr.isEmpty()) {
        localObject1 = (MessageObject)this.imagesArr.get(paramInt);
      }
      paramImageReceiver.setParentMessageObject((MessageObject)localObject1);
      if (localObject1 != null) {
        paramImageReceiver.setShouldGenerateQualityThumb(true);
      }
      if ((localObject1 != null) && (((MessageObject)localObject1).isVideo()))
      {
        paramImageReceiver.setNeedsQualityThumb(true);
        if ((((MessageObject)localObject1).photoThumbs != null) && (!((MessageObject)localObject1).photoThumbs.isEmpty()))
        {
          localObject3 = null;
          localObject2 = localObject3;
          if (this.currentThumb != null)
          {
            localObject2 = localObject3;
            if (paramImageReceiver == this.centerImage) {
              localObject2 = this.currentThumb;
            }
          }
          localObject3 = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject1).photoThumbs, 100);
          if (localObject2 != null) {}
          for (localObject1 = new BitmapDrawable(null, (Bitmap)localObject2);; localObject1 = null)
          {
            paramImageReceiver.setImage(null, null, null, (Drawable)localObject1, ((TLRPC.PhotoSize)localObject3).location, "b", 0, null, true);
            return;
          }
        }
        paramImageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(2130837914));
        return;
      }
      if ((localObject1 != null) && (this.currentAnimation != null))
      {
        paramImageReceiver.setImageBitmap(this.currentAnimation);
        this.currentAnimation.setSecondParentView(this.containerView);
        return;
      }
      paramImageReceiver.setNeedsQualityThumb(false);
      localObject3 = null;
      localObject2 = localObject3;
      if (this.currentThumb != null)
      {
        localObject2 = localObject3;
        if (paramImageReceiver == this.centerImage) {
          localObject2 = this.currentThumb;
        }
      }
      if (localObject4[0] == 0) {
        localObject4[0] = -1;
      }
      if (localObject1 != null)
      {
        localObject1 = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject1).photoThumbs, 100);
        if (localObject2 == null) {
          break label1228;
        }
        localObject2 = new BitmapDrawable(null, (Bitmap)localObject2);
        label1175:
        if (localObject1 == null) {
          break label1234;
        }
        localObject1 = ((TLRPC.PhotoSize)localObject1).location;
        label1187:
        paramInt = localObject4[0];
        if (this.avatarsDialogId == 0) {
          break label1240;
        }
      }
      label1228:
      label1234:
      label1240:
      for (boolean bool = true;; bool = false)
      {
        paramImageReceiver.setImage((TLObject)localObject5, null, null, (Drawable)localObject2, (TLRPC.FileLocation)localObject1, "b", paramInt, null, bool);
        return;
        localObject1 = null;
        break;
        localObject2 = null;
        break label1175;
        localObject1 = null;
        break label1187;
      }
    }
    paramImageReceiver.setNeedsQualityThumb(false);
    paramImageReceiver.setParentMessageObject(null);
    if (localObject4[0] == 0)
    {
      paramImageReceiver.setImageBitmap((Bitmap)null);
      return;
    }
    paramImageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(2130837914));
  }
  
  private void switchToEditMode(final int paramInt)
  {
    if ((this.currentEditMode == paramInt) || (this.centerImage.getBitmap() == null) || (this.changeModeAnimation != null) || (this.imageMoveAnimation != null) || (this.radialProgressViews[0].backgroundState != -1)) {}
    label171:
    label202:
    label349:
    label352:
    label402:
    do
    {
      return;
      if (paramInt == 0)
      {
        if ((this.currentEditMode == 2) && (this.photoFilterView.getToolsView().getVisibility() != 0))
        {
          this.photoFilterView.switchToOrFromEditMode();
          return;
        }
        if (this.centerImage.getBitmap() != null)
        {
          int i = this.centerImage.getBitmapWidth();
          int j = this.centerImage.getBitmapHeight();
          float f1 = getContainerViewWidth() / i;
          float f4 = getContainerViewHeight() / j;
          float f2 = getContainerViewWidth(0) / i;
          float f3 = getContainerViewHeight(0) / j;
          if (f1 > f4)
          {
            f1 = f4;
            if (f2 <= f3) {
              break label349;
            }
            f2 = f3;
            this.animateToScale = (f2 / f1);
            this.animateToX = 0.0F;
            if (this.currentEditMode != 1) {
              break label352;
            }
            this.animateToY = AndroidUtilities.dp(24.0F);
            this.animationStartTime = System.currentTimeMillis();
            this.zoomAnimation = true;
          }
        }
        else
        {
          this.imageMoveAnimation = new AnimatorSet();
          if (this.currentEditMode != 1) {
            break label402;
          }
          this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.editorDoneLayout, "translationY", new float[] { AndroidUtilities.dp(48.0F) }), ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.photoCropView, "alpha", new float[] { 0.0F }) });
        }
        for (;;)
        {
          this.imageMoveAnimation.setDuration(200L);
          this.imageMoveAnimation.addListener(new AnimatorListenerAdapterProxy()
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
                PhotoViewer.access$5802(PhotoViewer.this, null);
                PhotoViewer.access$5602(PhotoViewer.this, paramInt);
                PhotoViewer.access$7402(PhotoViewer.this, 1.0F);
                PhotoViewer.access$7502(PhotoViewer.this, 0.0F);
                PhotoViewer.access$7602(PhotoViewer.this, 0.0F);
                PhotoViewer.access$2302(PhotoViewer.this, 1.0F);
                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                PhotoViewer.this.containerView.invalidate();
                paramAnonymousAnimator = new AnimatorSet();
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, "translationY", new float[] { 0.0F }));
                localArrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, "translationY", new float[] { 0.0F }));
                if (PhotoViewer.this.needCaptionLayout) {
                  localArrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, "translationY", new float[] { 0.0F }));
                }
                if (PhotoViewer.this.sendPhotoType == 0) {
                  localArrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, "alpha", new float[] { 1.0F }));
                }
                paramAnonymousAnimator.playTogether(localArrayList);
                paramAnonymousAnimator.setDuration(200L);
                paramAnonymousAnimator.addListener(new AnimatorListenerAdapterProxy()
                {
                  public void onAnimationStart(Animator paramAnonymous2Animator)
                  {
                    PhotoViewer.this.pickerView.setVisibility(0);
                    PhotoViewer.this.actionBar.setVisibility(0);
                    if (PhotoViewer.this.needCaptionLayout)
                    {
                      paramAnonymous2Animator = PhotoViewer.this.captionTextView;
                      if (PhotoViewer.this.captionTextView.getTag() == null) {
                        break label103;
                      }
                    }
                    label103:
                    for (int i = 0;; i = 4)
                    {
                      paramAnonymous2Animator.setVisibility(i);
                      if (PhotoViewer.this.sendPhotoType == 0) {
                        PhotoViewer.this.checkImageView.setVisibility(0);
                      }
                      return;
                    }
                  }
                });
                paramAnonymousAnimator.start();
                return;
                if (PhotoViewer.this.currentEditMode == 2)
                {
                  PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoFilterView);
                  PhotoViewer.access$7202(PhotoViewer.this, null);
                }
                else if (PhotoViewer.this.currentEditMode == 3)
                {
                  PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoPaintView);
                  PhotoViewer.access$7302(PhotoViewer.this, null);
                }
              }
            }
          });
          this.imageMoveAnimation.start();
          return;
          break;
          break label171;
          if (this.currentEditMode == 2)
          {
            this.animateToY = AndroidUtilities.dp(62.0F);
            break label202;
          }
          if (this.currentEditMode != 3) {
            break label202;
          }
          this.animateToY = ((AndroidUtilities.dp(48.0F) - ActionBar.getCurrentActionBarHeight()) / 2);
          break label202;
          if (this.currentEditMode == 2)
          {
            this.photoFilterView.shutdown();
            this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.photoFilterView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(126.0F) }), ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }) });
          }
          else if (this.currentEditMode == 3)
          {
            this.photoPaintView.shutdown();
            this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(126.0F) }), ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), "translationX", new float[] { AndroidUtilities.dp(60.0F) }), ObjectAnimator.ofFloat(this.photoPaintView.getActionBar(), "translationY", new float[] { -ActionBar.getCurrentActionBarHeight() }), ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }) });
          }
        }
      }
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
              if (paramAnonymousBoolean)
              {
                PhotoViewer.this.animateTo(paramAnonymousFloat3, paramAnonymousFloat1, paramAnonymousFloat2, true);
                return;
              }
              PhotoViewer.access$2402(PhotoViewer.this, paramAnonymousFloat1);
              PhotoViewer.access$2502(PhotoViewer.this, paramAnonymousFloat2);
              PhotoViewer.access$2302(PhotoViewer.this, paramAnonymousFloat3);
              PhotoViewer.this.containerView.invalidate();
            }
          });
        }
        this.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", 2131165536));
        this.changeModeAnimation = new AnimatorSet();
        localArrayList = new ArrayList();
        localArrayList.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
        localArrayList.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[] { 0.0F, -this.actionBar.getHeight() }));
        if (this.needCaptionLayout) {
          localArrayList.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
        }
        if (this.sendPhotoType == 0) {
          localArrayList.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[] { 1.0F, 0.0F }));
        }
        this.changeModeAnimation.playTogether(localArrayList);
        this.changeModeAnimation.setDuration(200L);
        this.changeModeAnimation.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            PhotoViewer.access$8002(PhotoViewer.this, null);
            PhotoViewer.this.pickerView.setVisibility(8);
            if (PhotoViewer.this.needCaptionLayout) {
              PhotoViewer.this.captionTextView.setVisibility(4);
            }
            if (PhotoViewer.this.sendPhotoType == 0) {
              PhotoViewer.this.checkImageView.setVisibility(8);
            }
            paramAnonymousAnimator = PhotoViewer.this.centerImage.getBitmap();
            boolean bool;
            float f1;
            float f2;
            if (paramAnonymousAnimator != null)
            {
              PhotoCropView localPhotoCropView = PhotoViewer.this.photoCropView;
              int i = PhotoViewer.this.centerImage.getOrientation();
              if (PhotoViewer.this.sendPhotoType == 1) {
                break label431;
              }
              bool = true;
              localPhotoCropView.setBitmap(paramAnonymousAnimator, i, bool);
              i = PhotoViewer.this.centerImage.getBitmapWidth();
              int j = PhotoViewer.this.centerImage.getBitmapHeight();
              f1 = PhotoViewer.this.getContainerViewWidth() / i;
              float f4 = PhotoViewer.this.getContainerViewHeight() / j;
              f2 = PhotoViewer.this.getContainerViewWidth(1) / i;
              float f3 = PhotoViewer.this.getContainerViewHeight(1) / j;
              if (f1 <= f4) {
                break label437;
              }
              f1 = f4;
              label214:
              if (f2 <= f3) {
                break label440;
              }
              f2 = f3;
            }
            label431:
            label437:
            label440:
            for (;;)
            {
              PhotoViewer.access$7402(PhotoViewer.this, f2 / f1);
              PhotoViewer.access$7502(PhotoViewer.this, 0.0F);
              PhotoViewer.access$7602(PhotoViewer.this, -AndroidUtilities.dp(24.0F));
              PhotoViewer.access$8302(PhotoViewer.this, System.currentTimeMillis());
              PhotoViewer.access$8402(PhotoViewer.this, true);
              PhotoViewer.access$5802(PhotoViewer.this, new AnimatorSet());
              PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this.editorDoneLayout, "translationY", new float[] { AndroidUtilities.dp(48.0F), 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, "alpha", new float[] { 0.0F, 1.0F }) });
              PhotoViewer.this.imageMoveAnimation.setDuration(200L);
              PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapterProxy()
              {
                public void onAnimationEnd(Animator paramAnonymous2Animator)
                {
                  PhotoViewer.access$5802(PhotoViewer.this, null);
                  PhotoViewer.access$5602(PhotoViewer.this, PhotoViewer.30.this.val$mode);
                  PhotoViewer.access$7402(PhotoViewer.this, 1.0F);
                  PhotoViewer.access$7502(PhotoViewer.this, 0.0F);
                  PhotoViewer.access$7602(PhotoViewer.this, 0.0F);
                  PhotoViewer.access$2302(PhotoViewer.this, 1.0F);
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
              break label214;
            }
          }
        });
        this.changeModeAnimation.start();
        return;
      }
      if (paramInt == 2)
      {
        if (this.photoFilterView == null)
        {
          this.photoFilterView = new PhotoFilterView(this.parentActivity, this.centerImage.getBitmap(), this.centerImage.getOrientation());
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
              if (PhotoViewer.this.photoFilterView.hasChanges())
              {
                if (PhotoViewer.this.parentActivity == null) {
                  return;
                }
                paramAnonymousView = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
                paramAnonymousView.setMessage(LocaleController.getString("DiscardChanges", 2131165588));
                paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
                paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    PhotoViewer.this.switchToEditMode(0);
                  }
                });
                paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                PhotoViewer.this.showAlertDialog(paramAnonymousView);
                return;
              }
              PhotoViewer.this.switchToEditMode(0);
            }
          });
          this.photoFilterView.getToolsView().setTranslationY(AndroidUtilities.dp(126.0F));
        }
        this.changeModeAnimation = new AnimatorSet();
        localArrayList = new ArrayList();
        localArrayList.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
        localArrayList.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[] { 0.0F, -this.actionBar.getHeight() }));
        if (this.needCaptionLayout) {
          localArrayList.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
        }
        if (this.sendPhotoType == 0) {
          localArrayList.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[] { 1.0F, 0.0F }));
        }
        this.changeModeAnimation.playTogether(localArrayList);
        this.changeModeAnimation.setDuration(200L);
        this.changeModeAnimation.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            PhotoViewer.access$8002(PhotoViewer.this, null);
            PhotoViewer.this.pickerView.setVisibility(8);
            PhotoViewer.this.actionBar.setVisibility(8);
            if (PhotoViewer.this.needCaptionLayout) {
              PhotoViewer.this.captionTextView.setVisibility(4);
            }
            if (PhotoViewer.this.sendPhotoType == 0) {
              PhotoViewer.this.checkImageView.setVisibility(8);
            }
            float f1;
            float f2;
            if (PhotoViewer.this.centerImage.getBitmap() != null)
            {
              int i = PhotoViewer.this.centerImage.getBitmapWidth();
              int j = PhotoViewer.this.centerImage.getBitmapHeight();
              f1 = PhotoViewer.this.getContainerViewWidth() / i;
              float f4 = PhotoViewer.this.getContainerViewHeight() / j;
              f2 = PhotoViewer.this.getContainerViewWidth(2) / i;
              float f3 = PhotoViewer.this.getContainerViewHeight(2) / j;
              if (f1 <= f4) {
                break label373;
              }
              f1 = f4;
              if (f2 <= f3) {
                break label376;
              }
              f2 = f3;
            }
            label373:
            label376:
            for (;;)
            {
              PhotoViewer.access$7402(PhotoViewer.this, f2 / f1);
              PhotoViewer.access$7502(PhotoViewer.this, 0.0F);
              PhotoViewer.access$7602(PhotoViewer.this, -AndroidUtilities.dp(62.0F));
              PhotoViewer.access$8302(PhotoViewer.this, System.currentTimeMillis());
              PhotoViewer.access$8402(PhotoViewer.this, true);
              PhotoViewer.access$5802(PhotoViewer.this, new AnimatorSet());
              PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.photoFilterView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(126.0F), 0.0F }) });
              PhotoViewer.this.imageMoveAnimation.setDuration(200L);
              PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapterProxy()
              {
                public void onAnimationEnd(Animator paramAnonymous2Animator)
                {
                  PhotoViewer.this.photoFilterView.init();
                  PhotoViewer.access$5802(PhotoViewer.this, null);
                  PhotoViewer.access$5602(PhotoViewer.this, PhotoViewer.33.this.val$mode);
                  PhotoViewer.access$7402(PhotoViewer.this, 1.0F);
                  PhotoViewer.access$7502(PhotoViewer.this, 0.0F);
                  PhotoViewer.access$7602(PhotoViewer.this, 0.0F);
                  PhotoViewer.access$2302(PhotoViewer.this, 1.0F);
                  PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                  PhotoViewer.this.containerView.invalidate();
                }
                
                public void onAnimationStart(Animator paramAnonymous2Animator) {}
              });
              PhotoViewer.this.imageMoveAnimation.start();
              return;
              break;
            }
          }
        });
        this.changeModeAnimation.start();
        return;
      }
    } while (paramInt != 3);
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
      this.photoPaintView.getColorPicker().setTranslationX(AndroidUtilities.dp(60.0F));
      this.photoPaintView.getToolsView().setTranslationY(AndroidUtilities.dp(126.0F));
      this.photoPaintView.getActionBar().setTranslationY(-ActionBar.getCurrentActionBarHeight());
    }
    this.changeModeAnimation = new AnimatorSet();
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
    localArrayList.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[] { 0.0F, -this.actionBar.getHeight() }));
    if (this.needCaptionLayout) {
      localArrayList.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
    }
    if (this.sendPhotoType == 0) {
      localArrayList.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[] { 1.0F, 0.0F }));
    }
    this.changeModeAnimation.playTogether(localArrayList);
    this.changeModeAnimation.setDuration(200L);
    this.changeModeAnimation.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        PhotoViewer.access$8002(PhotoViewer.this, null);
        PhotoViewer.this.pickerView.setVisibility(8);
        if (PhotoViewer.this.needCaptionLayout) {
          PhotoViewer.this.captionTextView.setVisibility(4);
        }
        if (PhotoViewer.this.sendPhotoType == 0) {
          PhotoViewer.this.checkImageView.setVisibility(8);
        }
        float f1;
        float f2;
        if (PhotoViewer.this.centerImage.getBitmap() != null)
        {
          int i = PhotoViewer.this.centerImage.getBitmapWidth();
          int j = PhotoViewer.this.centerImage.getBitmapHeight();
          f1 = PhotoViewer.this.getContainerViewWidth() / i;
          float f4 = PhotoViewer.this.getContainerViewHeight() / j;
          f2 = PhotoViewer.this.getContainerViewWidth(3) / i;
          float f3 = PhotoViewer.this.getContainerViewHeight(3) / j;
          if (f1 <= f4) {
            break label433;
          }
          f1 = f4;
          if (f2 <= f3) {
            break label436;
          }
          f2 = f3;
        }
        label433:
        label436:
        for (;;)
        {
          PhotoViewer.access$7402(PhotoViewer.this, f2 / f1);
          PhotoViewer.access$7502(PhotoViewer.this, 0.0F);
          PhotoViewer.access$7602(PhotoViewer.this, (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0F)) / 2);
          PhotoViewer.access$8302(PhotoViewer.this, System.currentTimeMillis());
          PhotoViewer.access$8402(PhotoViewer.this, true);
          PhotoViewer.access$5802(PhotoViewer.this, new AnimatorSet());
          PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getColorPicker(), "translationX", new float[] { AndroidUtilities.dp(60.0F), 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(126.0F), 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getActionBar(), "translationY", new float[] { -ActionBar.getCurrentActionBarHeight(), 0.0F }) });
          PhotoViewer.this.imageMoveAnimation.setDuration(200L);
          PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapterProxy()
          {
            public void onAnimationEnd(Animator paramAnonymous2Animator)
            {
              PhotoViewer.this.photoPaintView.init();
              PhotoViewer.access$5802(PhotoViewer.this, null);
              PhotoViewer.access$5602(PhotoViewer.this, PhotoViewer.36.this.val$mode);
              PhotoViewer.access$7402(PhotoViewer.this, 1.0F);
              PhotoViewer.access$7502(PhotoViewer.this, 0.0F);
              PhotoViewer.access$7602(PhotoViewer.this, 0.0F);
              PhotoViewer.access$2302(PhotoViewer.this, 1.0F);
              PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
              PhotoViewer.this.containerView.invalidate();
            }
            
            public void onAnimationStart(Animator paramAnonymous2Animator) {}
          });
          PhotoViewer.this.imageMoveAnimation.start();
          return;
          break;
        }
      }
    });
    this.changeModeAnimation.start();
  }
  
  private void toggleActionBar(boolean paramBoolean1, boolean paramBoolean2)
  {
    float f1 = 1.0F;
    if (paramBoolean1)
    {
      this.actionBar.setVisibility(0);
      if (this.canShowBottom)
      {
        this.bottomLayout.setVisibility(0);
        if (this.captionTextView.getTag() != null) {
          this.captionTextView.setVisibility(0);
        }
      }
    }
    this.isActionBarVisible = paramBoolean1;
    this.actionBar.setEnabled(paramBoolean1);
    this.bottomLayout.setEnabled(paramBoolean1);
    float f2;
    if (paramBoolean2)
    {
      localObject1 = new ArrayList();
      Object localObject2 = this.actionBar;
      if (paramBoolean1)
      {
        f2 = 1.0F;
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f2 }));
        localObject2 = this.bottomLayout;
        if (!paramBoolean1) {
          break label256;
        }
        f2 = 1.0F;
        label129:
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f2 }));
        if (this.captionTextView.getTag() != null)
        {
          localObject2 = this.captionTextView;
          if (!paramBoolean1) {
            break label262;
          }
        }
      }
      for (;;)
      {
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f1 }));
        this.currentActionBarAnimation = new AnimatorSet();
        this.currentActionBarAnimation.playTogether((Collection)localObject1);
        if (!paramBoolean1) {
          this.currentActionBarAnimation.addListener(new AnimatorListenerAdapterProxy()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if ((PhotoViewer.this.currentActionBarAnimation != null) && (PhotoViewer.this.currentActionBarAnimation.equals(paramAnonymousAnimator)))
              {
                PhotoViewer.this.actionBar.setVisibility(8);
                if (PhotoViewer.this.canShowBottom)
                {
                  PhotoViewer.this.bottomLayout.setVisibility(8);
                  if (PhotoViewer.this.captionTextView.getTag() != null) {
                    PhotoViewer.this.captionTextView.setVisibility(4);
                  }
                }
                PhotoViewer.access$8502(PhotoViewer.this, null);
              }
            }
          });
        }
        this.currentActionBarAnimation.setDuration(200L);
        this.currentActionBarAnimation.start();
        return;
        f2 = 0.0F;
        break;
        label256:
        f2 = 0.0F;
        break label129;
        label262:
        f1 = 0.0F;
      }
    }
    Object localObject1 = this.actionBar;
    if (paramBoolean1)
    {
      f2 = 1.0F;
      label280:
      ((ActionBar)localObject1).setAlpha(f2);
      localObject1 = this.bottomLayout;
      if (!paramBoolean1) {
        break label387;
      }
      f2 = 1.0F;
      label300:
      ((FrameLayout)localObject1).setAlpha(f2);
      if (this.captionTextView.getTag() != null)
      {
        localObject1 = this.captionTextView;
        if (!paramBoolean1) {
          break label393;
        }
      }
    }
    for (;;)
    {
      ((TextView)localObject1).setAlpha(f1);
      if (paramBoolean1) {
        break;
      }
      this.actionBar.setVisibility(8);
      if (!this.canShowBottom) {
        break;
      }
      this.bottomLayout.setVisibility(8);
      if (this.captionTextView.getTag() == null) {
        break;
      }
      this.captionTextView.setVisibility(4);
      return;
      f2 = 0.0F;
      break label280;
      label387:
      f2 = 0.0F;
      break label300;
      label393:
      f1 = 0.0F;
    }
  }
  
  private void toggleCheckImageView(boolean paramBoolean)
  {
    float f2 = 1.0F;
    AnimatorSet localAnimatorSet = new AnimatorSet();
    ArrayList localArrayList = new ArrayList();
    Object localObject = this.pickerView;
    if (paramBoolean)
    {
      f1 = 1.0F;
      localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f1 }));
      if (this.needCaptionLayout)
      {
        localObject = this.captionTextView;
        if (!paramBoolean) {
          break label160;
        }
        f1 = 1.0F;
        label72:
        localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f1 }));
      }
      if (this.sendPhotoType == 0)
      {
        localObject = this.checkImageView;
        if (!paramBoolean) {
          break label165;
        }
      }
    }
    label160:
    label165:
    for (float f1 = f2;; f1 = 0.0F)
    {
      localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f1 }));
      localAnimatorSet.playTogether(localArrayList);
      localAnimatorSet.setDuration(200L);
      localAnimatorSet.start();
      return;
      f1 = 0.0F;
      break;
      f1 = 0.0F;
      break label72;
    }
  }
  
  private void updateCaptionTextForCurrentPhoto(Object paramObject)
  {
    Object localObject2 = null;
    Object localObject1;
    if ((paramObject instanceof MediaController.PhotoEntry)) {
      localObject1 = ((MediaController.PhotoEntry)paramObject).caption;
    }
    while ((localObject1 == null) || (((CharSequence)localObject1).length() == 0))
    {
      this.captionEditText.setFieldText("");
      return;
      localObject1 = localObject2;
      if (!(paramObject instanceof TLRPC.BotInlineResult))
      {
        localObject1 = localObject2;
        if ((paramObject instanceof MediaController.SearchImage)) {
          localObject1 = ((MediaController.SearchImage)paramObject).caption;
        }
      }
    }
    this.captionEditText.setFieldText((CharSequence)localObject1);
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
    if (this.placeProvider == null) {
      return;
    }
    this.pickerView.updateSelectedCount(this.placeProvider.getSelectedCount(), false);
  }
  
  private void updateVideoPlayerTime()
  {
    String str;
    if (this.videoPlayer == null) {
      str = "00:00 / 00:00";
    }
    for (;;)
    {
      if (!TextUtils.equals(this.videoPlayerTime.getText(), str)) {
        this.videoPlayerTime.setText(str);
      }
      return;
      long l1 = this.videoPlayer.getCurrentPosition() / 1000L;
      long l2 = this.videoPlayer.getDuration();
      if (this.muteItemAvailable)
      {
        if (l2 < 30000L) {
          break label172;
        }
        if (this.muteItem.getVisibility() == 0) {
          this.muteItem.setVisibility(8);
        }
      }
      for (;;)
      {
        l2 /= 1000L;
        if ((l2 == -1L) || (l1 == -1L)) {
          break label193;
        }
        str = String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L), Long.valueOf(l2 / 60L), Long.valueOf(l2 % 60L) });
        break;
        label172:
        if (this.muteItem.getVisibility() != 0) {
          this.muteItem.setVisibility(0);
        }
      }
      label193:
      str = "00:00 / 00:00";
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
    for (;;)
    {
      return;
      if (this.currentEditMode == 1) {
        this.photoCropView.cancelAnimationRunnable();
      }
      switchToEditMode(0);
      return;
      try
      {
        if (this.visibleDialog != null)
        {
          this.visibleDialog.dismiss();
          this.visibleDialog = null;
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
          if ((this.parentActivity == null) || (!this.isVisible) || (checkAnimation()) || (this.placeProvider == null) || ((this.captionEditText.hideActionMode()) && (!paramBoolean2))) {
            continue;
          }
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
          if (this.velocityTracker != null)
          {
            this.velocityTracker.recycle();
            this.velocityTracker = null;
          }
          ConnectionsManager.getInstance().cancelRequestsForGuid(this.classGuid);
          localPlaceProviderObject = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
          if (!paramBoolean1) {
            break label1536;
          }
          this.animationInProgress = 1;
          this.animatingImageView.setVisibility(0);
          this.containerView.invalidate();
          localAnimatorSet = new AnimatorSet();
          localObject3 = this.animatingImageView.getLayoutParams();
          Object localObject1 = null;
          int j = this.centerImage.getOrientation();
          int k = 0;
          int i = k;
          if (localPlaceProviderObject != null)
          {
            i = k;
            if (localPlaceProviderObject.imageReceiver != null) {
              i = localPlaceProviderObject.imageReceiver.getAnimatedOrientation();
            }
          }
          if (i != 0) {
            j = i;
          }
          this.animatingImageView.setOrientation(j);
          if (localPlaceProviderObject == null) {
            break label1351;
          }
          localObject1 = this.animatingImageView;
          if (localPlaceProviderObject.radius == 0) {
            break label1346;
          }
          paramBoolean1 = true;
          ((ClippingImageView)localObject1).setNeedRadius(paramBoolean1);
          localObject1 = localPlaceProviderObject.imageReceiver.getDrawRegion();
          ((ViewGroup.LayoutParams)localObject3).width = (((Rect)localObject1).right - ((Rect)localObject1).left);
          ((ViewGroup.LayoutParams)localObject3).height = (((Rect)localObject1).bottom - ((Rect)localObject1).top);
          this.animatingImageView.setImageBitmap(localPlaceProviderObject.thumb);
          this.animatingImageView.setLayoutParams((ViewGroup.LayoutParams)localObject3);
          f1 = AndroidUtilities.displaySize.x / ((ViewGroup.LayoutParams)localObject3).width;
          float f2 = AndroidUtilities.displaySize.y / ((ViewGroup.LayoutParams)localObject3).height;
          if (f1 <= f2) {
            break label1400;
          }
          f1 = f2;
          float f4 = ((ViewGroup.LayoutParams)localObject3).width;
          float f5 = this.scale;
          f2 = ((ViewGroup.LayoutParams)localObject3).height;
          float f3 = this.scale;
          f4 = (AndroidUtilities.displaySize.x - f4 * f5 * f1) / 2.0F;
          f2 = (AndroidUtilities.displaySize.y - f2 * f3 * f1) / 2.0F;
          this.animatingImageView.setTranslationX(this.translationX + f4);
          this.animatingImageView.setTranslationY(this.translationY + f2);
          this.animatingImageView.setScaleX(this.scale * f1);
          this.animatingImageView.setScaleY(this.scale * f1);
          if (localPlaceProviderObject == null) {
            break label1403;
          }
          localPlaceProviderObject.imageReceiver.setVisible(false, true);
          int m = Math.abs(((Rect)localObject1).left - localPlaceProviderObject.imageReceiver.getImageX());
          int n = Math.abs(((Rect)localObject1).top - localPlaceProviderObject.imageReceiver.getImageY());
          localObject3 = new int[2];
          localPlaceProviderObject.parentView.getLocationInWindow((int[])localObject3);
          j = localObject3[1] - AndroidUtilities.statusBarHeight - (localPlaceProviderObject.viewY + ((Rect)localObject1).top) + localPlaceProviderObject.clipTopAddition;
          i = j;
          if (j < 0) {
            i = 0;
          }
          k = localPlaceProviderObject.viewY + ((Rect)localObject1).top + (((Rect)localObject1).bottom - ((Rect)localObject1).top) - (localObject3[1] + localPlaceProviderObject.parentView.getHeight() - AndroidUtilities.statusBarHeight) + localPlaceProviderObject.clipBottomAddition;
          j = k;
          if (k < 0) {
            j = 0;
          }
          i = Math.max(i, n);
          j = Math.max(j, n);
          this.animationValues[0][0] = this.animatingImageView.getScaleX();
          this.animationValues[0][1] = this.animatingImageView.getScaleY();
          this.animationValues[0][2] = this.animatingImageView.getTranslationX();
          this.animationValues[0][3] = this.animatingImageView.getTranslationY();
          this.animationValues[0][4] = 0;
          this.animationValues[0][5] = 0;
          this.animationValues[0][6] = 0;
          this.animationValues[0][7] = 0;
          this.animationValues[1][0] = localPlaceProviderObject.scale;
          this.animationValues[1][1] = localPlaceProviderObject.scale;
          this.animationValues[1][2] = (localPlaceProviderObject.viewX + ((Rect)localObject1).left * localPlaceProviderObject.scale);
          this.animationValues[1][3] = (localPlaceProviderObject.viewY + ((Rect)localObject1).top * localPlaceProviderObject.scale);
          this.animationValues[1][4] = (m * localPlaceProviderObject.scale);
          this.animationValues[1][5] = (i * localPlaceProviderObject.scale);
          this.animationValues[1][6] = (j * localPlaceProviderObject.scale);
          this.animationValues[1][7] = localPlaceProviderObject.radius;
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }) });
          this.animationEndRunnable = new Runnable()
          {
            public void run()
            {
              if (Build.VERSION.SDK_INT >= 18) {
                PhotoViewer.this.containerView.setLayerType(0, null);
              }
              PhotoViewer.access$8802(PhotoViewer.this, 0);
              PhotoViewer.this.onPhotoClosed(localPlaceProviderObject);
            }
          };
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.addListener(new AnimatorListenerAdapterProxy()
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
                    PhotoViewer.access$9402(PhotoViewer.this, null);
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
          if (this.currentAnimation != null)
          {
            this.currentAnimation.setSecondParentView(null);
            this.currentAnimation = null;
            this.centerImage.setImageBitmap((Drawable)null);
          }
          if (!(this.placeProvider instanceof EmptyPhotoViewerProvider)) {
            continue;
          }
          this.placeProvider.cancelButtonPressed();
          return;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          final PlaceProviderObject localPlaceProviderObject;
          AnimatorSet localAnimatorSet;
          Object localObject3;
          float f1;
          FileLog.e("tmessages", localException);
          continue;
          if (this.currentEditMode == 1)
          {
            this.editorDoneLayout.setVisibility(8);
            this.photoCropView.setVisibility(8);
            continue;
            label1346:
            paramBoolean1 = false;
            continue;
            label1351:
            this.animatingImageView.setNeedRadius(false);
            ((ViewGroup.LayoutParams)localObject3).width = this.centerImage.getImageWidth();
            ((ViewGroup.LayoutParams)localObject3).height = this.centerImage.getImageHeight();
            this.animatingImageView.setImageBitmap(this.centerImage.getBitmap());
            continue;
            label1400:
            continue;
            label1403:
            Object localObject2 = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0 });
            localObject3 = ObjectAnimator.ofFloat(this.animatingImageView, "alpha", new float[] { 0.0F });
            ClippingImageView localClippingImageView = this.animatingImageView;
            if (this.translationY >= 0.0F) {}
            for (f1 = AndroidUtilities.displaySize.y;; f1 = -AndroidUtilities.displaySize.y)
            {
              localAnimatorSet.playTogether(new Animator[] { localObject2, localObject3, ObjectAnimator.ofFloat(localClippingImageView, "translationY", new float[] { f1 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }) });
              break;
            }
            label1536:
            localObject2 = new AnimatorSet();
            ((AnimatorSet)localObject2).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "scaleX", new float[] { 0.9F }), ObjectAnimator.ofFloat(this.containerView, "scaleY", new float[] { 0.9F }), ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }) });
            this.animationInProgress = 2;
            this.animationEndRunnable = new Runnable()
            {
              public void run()
              {
                if (PhotoViewer.this.containerView == null) {
                  return;
                }
                if (Build.VERSION.SDK_INT >= 18) {
                  PhotoViewer.this.containerView.setLayerType(0, null);
                }
                PhotoViewer.access$8802(PhotoViewer.this, 0);
                PhotoViewer.this.onPhotoClosed(localPlaceProviderObject);
                PhotoViewer.this.containerView.setScaleX(1.0F);
                PhotoViewer.this.containerView.setScaleY(1.0F);
              }
            };
            ((AnimatorSet)localObject2).setDuration(200L);
            ((AnimatorSet)localObject2).addListener(new AnimatorListenerAdapterProxy()
            {
              public void onAnimationEnd(Animator paramAnonymousAnimator)
              {
                if (PhotoViewer.this.animationEndRunnable != null)
                {
                  PhotoViewer.this.animationEndRunnable.run();
                  PhotoViewer.access$9402(PhotoViewer.this, null);
                }
              }
            });
            this.transitionAnimationStartTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 18) {
              this.containerView.setLayerType(2, null);
            }
            ((AnimatorSet)localObject2).start();
          }
        }
      }
    }
  }
  
  public void destroyPhotoViewer()
  {
    if ((this.parentActivity == null) || (this.windowView == null)) {
      return;
    }
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
        FileLog.e("tmessages", localException);
      }
    }
    if (this.captionEditText != null) {
      this.captionEditText.onDestroy();
    }
    Instance = null;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.FileDidFailedLoad)
    {
      paramVarArgs = (String)paramVarArgs[0];
      paramInt = 0;
      if (paramInt < 3)
      {
        if ((this.currentFileNames[paramInt] == null) || (!this.currentFileNames[paramInt].equals(paramVarArgs))) {
          break label61;
        }
        this.radialProgressViews[paramInt].setProgress(1.0F, true);
        checkProgress(paramInt, true);
      }
    }
    label61:
    label204:
    label433:
    label675:
    label721:
    label723:
    label983:
    label1267:
    label1491:
    label1590:
    label1771:
    label1792:
    label1801:
    label1837:
    label1970:
    label2010:
    do
    {
      int i;
      do
      {
        Object localObject1;
        Object localObject2;
        int k;
        do
        {
          do
          {
            do
            {
              for (;;)
              {
                return;
                paramInt += 1;
                break;
                if (paramInt == NotificationCenter.FileDidLoaded)
                {
                  paramVarArgs = (String)paramVarArgs[0];
                  paramInt = 0;
                  for (;;)
                  {
                    if (paramInt >= 3) {
                      break label204;
                    }
                    if ((this.currentFileNames[paramInt] != null) && (this.currentFileNames[paramInt].equals(paramVarArgs)))
                    {
                      this.radialProgressViews[paramInt].setProgress(1.0F, true);
                      checkProgress(paramInt, true);
                      if ((Build.VERSION.SDK_INT < 16) || (paramInt != 0) || (((this.currentMessageObject == null) || (!this.currentMessageObject.isVideo())) && ((this.currentBotInlineResult == null) || ((!this.currentBotInlineResult.type.equals("video")) && (!MessageObject.isVideoDocument(this.currentBotInlineResult.document)))))) {
                        break;
                      }
                      onActionClick(false);
                      return;
                    }
                    paramInt += 1;
                  }
                }
                else if (paramInt == NotificationCenter.FileLoadProgressChanged)
                {
                  localObject1 = (String)paramVarArgs[0];
                  paramInt = 0;
                  while (paramInt < 3)
                  {
                    if ((this.currentFileNames[paramInt] != null) && (this.currentFileNames[paramInt].equals(localObject1)))
                    {
                      localObject2 = (Float)paramVarArgs[1];
                      this.radialProgressViews[paramInt].setProgress(((Float)localObject2).floatValue(), true);
                    }
                    paramInt += 1;
                  }
                }
                else
                {
                  if (paramInt != NotificationCenter.dialogPhotosLoaded) {
                    break label723;
                  }
                  paramInt = ((Integer)paramVarArgs[4]).intValue();
                  i = ((Integer)paramVarArgs[0]).intValue();
                  if ((this.avatarsDialogId == i) && (this.classGuid == paramInt))
                  {
                    boolean bool = ((Boolean)paramVarArgs[3]).booleanValue();
                    paramInt = -1;
                    paramVarArgs = (ArrayList)paramVarArgs[5];
                    if (!paramVarArgs.isEmpty())
                    {
                      this.imagesArrLocations.clear();
                      this.imagesArrLocationsSizes.clear();
                      this.avatarsArr.clear();
                      j = 0;
                      if (j < paramVarArgs.size())
                      {
                        localObject1 = (TLRPC.Photo)paramVarArgs.get(j);
                        i = paramInt;
                        if (localObject1 != null)
                        {
                          i = paramInt;
                          if (!(localObject1 instanceof TLRPC.TL_photoEmpty))
                          {
                            if (((TLRPC.Photo)localObject1).sizes != null) {
                              break label433;
                            }
                            i = paramInt;
                          }
                        }
                        do
                        {
                          j += 1;
                          paramInt = i;
                          break;
                          localObject2 = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo)localObject1).sizes, 640);
                          i = paramInt;
                        } while (localObject2 == null);
                        i = paramInt;
                        if (paramInt == -1)
                        {
                          i = paramInt;
                          if (this.currentFileLocation != null) {
                            k = 0;
                          }
                        }
                        for (;;)
                        {
                          i = paramInt;
                          if (k < ((TLRPC.Photo)localObject1).sizes.size())
                          {
                            TLRPC.PhotoSize localPhotoSize = (TLRPC.PhotoSize)((TLRPC.Photo)localObject1).sizes.get(k);
                            if ((localPhotoSize.location.local_id == this.currentFileLocation.local_id) && (localPhotoSize.location.volume_id == this.currentFileLocation.volume_id)) {
                              i = this.imagesArrLocations.size();
                            }
                          }
                          else
                          {
                            this.imagesArrLocations.add(((TLRPC.PhotoSize)localObject2).location);
                            this.imagesArrLocationsSizes.add(Integer.valueOf(((TLRPC.PhotoSize)localObject2).size));
                            this.avatarsArr.add(localObject1);
                            break;
                          }
                          k += 1;
                        }
                      }
                      if (!this.avatarsArr.isEmpty())
                      {
                        this.menuItem.showSubItem(6);
                        this.needSearchImageInArr = false;
                        this.currentIndex = -1;
                        if (paramInt == -1) {
                          break label675;
                        }
                        setImageIndex(paramInt, true);
                      }
                      for (;;)
                      {
                        if (!bool) {
                          break label721;
                        }
                        MessagesController.getInstance().loadDialogPhotos(this.avatarsDialogId, 0, 80, 0L, false, this.classGuid);
                        return;
                        this.menuItem.hideSubItem(6);
                        break;
                        this.avatarsArr.add(0, new TLRPC.TL_photoEmpty());
                        this.imagesArrLocations.add(0, this.currentFileLocation);
                        this.imagesArrLocationsSizes.add(0, Integer.valueOf(0));
                        setImageIndex(0, true);
                      }
                    }
                  }
                }
              }
              if (paramInt != NotificationCenter.mediaCountDidLoaded) {
                break label983;
              }
              l = ((Long)paramVarArgs[0]).longValue();
            } while ((l != this.currentDialogId) && (l != this.mergeDialogId));
            if (l == this.currentDialogId) {
              this.totalImagesCount = ((Integer)paramVarArgs[1]).intValue();
            }
            while ((this.needSearchImageInArr) && (this.isFirstLoading))
            {
              this.isFirstLoading = false;
              this.loadingMoreImages = true;
              SharedMediaQuery.loadMedia(this.currentDialogId, 0, 80, 0, 0, true, this.classGuid);
              return;
              if (l == this.mergeDialogId) {
                this.totalImagesCountMerge = ((Integer)paramVarArgs[1]).intValue();
              }
            }
          } while (this.imagesArr.isEmpty());
          if (this.opennedFromMedia)
          {
            this.actionBar.setTitle(LocaleController.formatString("Of", 2131166045, new Object[] { Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
            return;
          }
          this.actionBar.setTitle(LocaleController.formatString("Of", 2131166045, new Object[] { Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge - this.imagesArr.size() + this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
          return;
          if (paramInt != NotificationCenter.mediaDidLoaded) {
            break label2010;
          }
          l = ((Long)paramVarArgs[0]).longValue();
          paramInt = ((Integer)paramVarArgs[3]).intValue();
        } while (((l != this.currentDialogId) && (l != this.mergeDialogId)) || (paramInt != this.classGuid));
        this.loadingMoreImages = false;
        if (l == this.currentDialogId) {}
        for (i = 0;; i = 1)
        {
          localObject1 = (ArrayList)paramVarArgs[2];
          this.endReached[i] = ((Boolean)paramVarArgs[5]).booleanValue();
          if (!this.needSearchImageInArr) {
            break label1837;
          }
          if ((!((ArrayList)localObject1).isEmpty()) || ((i == 0) && (this.mergeDialogId != 0L))) {
            break;
          }
          this.needSearchImageInArr = false;
          return;
        }
        paramInt = -1;
        paramVarArgs = (MessageObject)this.imagesArr.get(this.currentIndex);
        int j = 0;
        int n = 0;
        if (n < ((ArrayList)localObject1).size())
        {
          localObject2 = (MessageObject)((ArrayList)localObject1).get(n);
          k = j;
          int m = paramInt;
          if (!this.imagesByIdsTemp[i].containsKey(Integer.valueOf(((MessageObject)localObject2).getId())))
          {
            this.imagesByIdsTemp[i].put(Integer.valueOf(((MessageObject)localObject2).getId()), localObject2);
            if (!this.opennedFromMedia) {
              break label1267;
            }
            this.imagesArrTemp.add(localObject2);
            if (((MessageObject)localObject2).getId() == paramVarArgs.getId()) {
              paramInt = j;
            }
            k = j + 1;
            m = paramInt;
          }
          for (;;)
          {
            n += 1;
            j = k;
            paramInt = m;
            break;
            j += 1;
            this.imagesArrTemp.add(0, localObject2);
            k = j;
            m = paramInt;
            if (((MessageObject)localObject2).getId() == paramVarArgs.getId())
            {
              m = ((ArrayList)localObject1).size() - j;
              k = j;
            }
          }
        }
        if ((j == 0) && ((i != 0) || (this.mergeDialogId == 0L)))
        {
          this.totalImagesCount = this.imagesArr.size();
          this.totalImagesCountMerge = 0;
        }
        if (paramInt != -1)
        {
          this.imagesArr.clear();
          this.imagesArr.addAll(this.imagesArrTemp);
          i = 0;
          while (i < 2)
          {
            this.imagesByIds[i].clear();
            this.imagesByIds[i].putAll(this.imagesByIdsTemp[i]);
            this.imagesByIdsTemp[i].clear();
            i += 1;
          }
          this.imagesArrTemp.clear();
          this.needSearchImageInArr = false;
          this.currentIndex = -1;
          i = paramInt;
          if (paramInt >= this.imagesArr.size()) {
            i = this.imagesArr.size() - 1;
          }
          setImageIndex(i, true);
          return;
        }
        if (this.opennedFromMedia) {
          if (this.imagesArrTemp.isEmpty())
          {
            k = 0;
            paramInt = k;
            j = i;
            if (i == 0)
            {
              paramInt = k;
              j = i;
              if (this.endReached[i] != 0)
              {
                paramInt = k;
                j = i;
                if (this.mergeDialogId != 0L)
                {
                  i = 1;
                  paramInt = k;
                  j = i;
                  if (!this.imagesArrTemp.isEmpty())
                  {
                    paramInt = k;
                    j = i;
                    if (((MessageObject)this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getDialogId() != this.mergeDialogId)
                    {
                      paramInt = 0;
                      j = i;
                    }
                  }
                }
              }
            }
            if (this.endReached[j] != 0) {
              break label1771;
            }
            this.loadingMoreImages = true;
            if (!this.opennedFromMedia) {
              break label1801;
            }
            if (j != 0) {
              break label1792;
            }
          }
        }
        for (long l = this.currentDialogId;; l = this.mergeDialogId)
        {
          SharedMediaQuery.loadMedia(l, 0, 80, paramInt, 0, true, this.classGuid);
          return;
          k = ((MessageObject)this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getId();
          break label1491;
          if (this.imagesArrTemp.isEmpty()) {}
          for (k = 0;; k = ((MessageObject)this.imagesArrTemp.get(0)).getId())
          {
            paramInt = k;
            j = i;
            if (i != 0) {
              break label1590;
            }
            paramInt = k;
            j = i;
            if (this.endReached[i] == 0) {
              break label1590;
            }
            paramInt = k;
            j = i;
            if (this.mergeDialogId == 0L) {
              break label1590;
            }
            i = 1;
            paramInt = k;
            j = i;
            if (this.imagesArrTemp.isEmpty()) {
              break label1590;
            }
            paramInt = k;
            j = i;
            if (((MessageObject)this.imagesArrTemp.get(0)).getDialogId() == this.mergeDialogId) {
              break label1590;
            }
            paramInt = 0;
            j = i;
            break label1590;
            break;
          }
        }
        if (j == 0) {}
        for (l = this.currentDialogId;; l = this.mergeDialogId)
        {
          SharedMediaQuery.loadMedia(l, 0, 80, paramInt, 0, true, this.classGuid);
          return;
        }
        paramInt = 0;
        paramVarArgs = ((ArrayList)localObject1).iterator();
        while (paramVarArgs.hasNext())
        {
          localObject1 = (MessageObject)paramVarArgs.next();
          if (!this.imagesByIds[i].containsKey(Integer.valueOf(((MessageObject)localObject1).getId())))
          {
            paramInt += 1;
            if (this.opennedFromMedia) {
              this.imagesArr.add(localObject1);
            }
            for (;;)
            {
              this.imagesByIds[i].put(Integer.valueOf(((MessageObject)localObject1).getId()), localObject1);
              break;
              this.imagesArr.add(0, localObject1);
            }
          }
        }
        if (!this.opennedFromMedia) {
          break label1970;
        }
      } while (paramInt != 0);
      this.totalImagesCount = this.imagesArr.size();
      this.totalImagesCountMerge = 0;
      return;
      if (paramInt != 0)
      {
        i = this.currentIndex;
        this.currentIndex = -1;
        setImageIndex(i + paramInt, true);
        return;
      }
      this.totalImagesCount = this.imagesArr.size();
      this.totalImagesCountMerge = 0;
      return;
    } while ((paramInt != NotificationCenter.emojiDidLoaded) || (this.captionTextView == null));
    this.captionTextView.invalidate();
  }
  
  public float getAnimationValue()
  {
    return this.animationValue;
  }
  
  public boolean isMuteVideo()
  {
    return this.muteVideo;
  }
  
  public boolean isShowingImage(String paramString)
  {
    return (this.isVisible) && (!this.disableShowCheck) && (paramString != null) && (this.currentPathObject != null) && (paramString.equals(this.currentPathObject));
  }
  
  public boolean isShowingImage(MessageObject paramMessageObject)
  {
    return (this.isVisible) && (!this.disableShowCheck) && (paramMessageObject != null) && (this.currentMessageObject != null) && (this.currentMessageObject.getId() == paramMessageObject.getId());
  }
  
  public boolean isShowingImage(TLRPC.FileLocation paramFileLocation)
  {
    return (this.isVisible) && (!this.disableShowCheck) && (paramFileLocation != null) && (this.currentFileLocation != null) && (paramFileLocation.local_id == this.currentFileLocation.local_id) && (paramFileLocation.volume_id == this.currentFileLocation.volume_id) && (paramFileLocation.dc_id == this.currentFileLocation.dc_id);
  }
  
  public boolean isVisible()
  {
    return (this.isVisible) && (this.placeProvider != null);
  }
  
  public boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    if ((!this.canZoom) || ((this.scale == 1.0F) && ((this.translationY != 0.0F) || (this.translationX != 0.0F)))) {
      return false;
    }
    if ((this.animationStartTime != 0L) || (this.animationInProgress != 0)) {
      return false;
    }
    float f2;
    float f3;
    float f1;
    if (this.scale == 1.0F)
    {
      f2 = paramMotionEvent.getX() - getContainerViewWidth() / 2 - (paramMotionEvent.getX() - getContainerViewWidth() / 2 - this.translationX) * (3.0F / this.scale);
      f3 = paramMotionEvent.getY() - getContainerViewHeight() / 2 - (paramMotionEvent.getY() - getContainerViewHeight() / 2 - this.translationY) * (3.0F / this.scale);
      updateMinMax(3.0F);
      if (f2 < this.minX)
      {
        f1 = this.minX;
        if (f3 >= this.minY) {
          break label216;
        }
        f2 = this.minY;
        label180:
        animateTo(3.0F, f1, f2, true);
      }
    }
    for (;;)
    {
      this.doubleTap = true;
      return true;
      f1 = f2;
      if (f2 <= this.maxX) {
        break;
      }
      f1 = this.maxX;
      break;
      label216:
      f2 = f3;
      if (f3 <= this.maxY) {
        break label180;
      }
      f2 = this.maxY;
      break label180;
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
    while (this.captionDoneItem.getVisibility() == 8) {
      return;
    }
    closeCaptionEnter(true);
  }
  
  public void onResume()
  {
    redraw(0);
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
    float f2;
    do
    {
      do
      {
        float f1;
        do
        {
          do
          {
            int i;
            do
            {
              do
              {
                do
                {
                  do
                  {
                    return bool1;
                    if (this.canShowBottom)
                    {
                      if ((Build.VERSION.SDK_INT >= 16) && (this.aspectRatioFrameLayout != null) && (this.aspectRatioFrameLayout.getVisibility() == 0)) {}
                      for (i = 1; (this.radialProgressViews[0] != null) && (this.containerView != null) && (i == 0); i = 0)
                      {
                        i = this.radialProgressViews[0].backgroundState;
                        if ((i <= 0) || (i > 3)) {
                          break;
                        }
                        f1 = paramMotionEvent.getX();
                        f2 = paramMotionEvent.getY();
                        if ((f1 < (getContainerViewWidth() - AndroidUtilities.dp(100.0F)) / 2.0F) || (f1 > (getContainerViewWidth() + AndroidUtilities.dp(100.0F)) / 2.0F) || (f2 < (getContainerViewHeight() - AndroidUtilities.dp(100.0F)) / 2.0F) || (f2 > (getContainerViewHeight() + AndroidUtilities.dp(100.0F)) / 2.0F)) {
                          break;
                        }
                        onActionClick(true);
                        checkProgress(0, true);
                        return true;
                      }
                      if (!this.isActionBarVisible) {
                        bool1 = true;
                      }
                      toggleActionBar(bool1, true);
                      return true;
                    }
                    if (this.sendPhotoType == 0)
                    {
                      this.checkImageView.performClick();
                      return true;
                    }
                    bool1 = bool2;
                  } while (this.currentBotInlineResult == null);
                  if (this.currentBotInlineResult.type.equals("video")) {
                    break;
                  }
                  bool1 = bool2;
                } while (!MessageObject.isVideoDocument(this.currentBotInlineResult.document));
                i = this.radialProgressViews[0].backgroundState;
                bool1 = bool2;
              } while (i <= 0);
              bool1 = bool2;
            } while (i > 3);
            f1 = paramMotionEvent.getX();
            f2 = paramMotionEvent.getY();
            bool1 = bool2;
          } while (f1 < (getContainerViewWidth() - AndroidUtilities.dp(100.0F)) / 2.0F);
          bool1 = bool2;
        } while (f1 > (getContainerViewWidth() + AndroidUtilities.dp(100.0F)) / 2.0F);
        bool1 = bool2;
      } while (f2 < (getContainerViewHeight() - AndroidUtilities.dp(100.0F)) / 2.0F);
      bool1 = bool2;
    } while (f2 > (getContainerViewHeight() + AndroidUtilities.dp(100.0F)) / 2.0F);
    onActionClick(true);
    checkProgress(0, true);
    return true;
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
    if ((this.parentActivity == null) || (this.isVisible) || ((paramPhotoViewerProvider == null) && (checkAnimation())) || ((paramMessageObject == null) && (paramFileLocation == null) && (paramArrayList == null) && (paramArrayList1 == null))) {
      return false;
    }
    final PlaceProviderObject localPlaceProviderObject = paramPhotoViewerProvider.getPlaceForPhoto(paramMessageObject, paramFileLocation, paramInt);
    if ((localPlaceProviderObject == null) && (paramArrayList1 == null)) {
      return false;
    }
    WindowManager localWindowManager = (WindowManager)this.parentActivity.getSystemService("window");
    if (this.attachedToWindow) {}
    try
    {
      localWindowManager.removeView(this.windowView);
      for (;;)
      {
        try
        {
          this.windowLayoutParams.type = 99;
          this.windowLayoutParams.flags = 8;
          this.windowLayoutParams.softInputMode = 0;
          this.windowView.setFocusable(false);
          this.containerView.setFocusable(false);
          localWindowManager.addView(this.windowView, this.windowLayoutParams);
          this.parentChatActivity = paramChatActivity;
          this.actionBar.setTitle(LocaleController.formatString("Of", 2131166045, new Object[] { Integer.valueOf(1), Integer.valueOf(1) }));
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileLoadProgressChanged);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaCountDidLoaded);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaDidLoaded);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogPhotosLoaded);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
          this.placeProvider = paramPhotoViewerProvider;
          this.mergeDialogId = paramLong2;
          this.currentDialogId = paramLong1;
          if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
          }
          this.isVisible = true;
          toggleActionBar(true, false);
          if (localPlaceProviderObject == null) {
            break label1275;
          }
          this.disableShowCheck = true;
          this.animationInProgress = 1;
          if (paramMessageObject != null) {
            this.currentAnimation = localPlaceProviderObject.imageReceiver.getAnimation();
          }
          onPhotoShow(paramMessageObject, paramFileLocation, paramArrayList, paramArrayList1, paramInt, localPlaceProviderObject);
          paramMessageObject = localPlaceProviderObject.imageReceiver.getDrawRegion();
          paramInt = localPlaceProviderObject.imageReceiver.getOrientation();
          int i = localPlaceProviderObject.imageReceiver.getAnimatedOrientation();
          if (i != 0) {
            paramInt = i;
          }
          this.animatingImageView.setVisibility(0);
          this.animatingImageView.setRadius(localPlaceProviderObject.radius);
          this.animatingImageView.setOrientation(paramInt);
          paramFileLocation = this.animatingImageView;
          if (localPlaceProviderObject.radius != 0)
          {
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
            float f1 = AndroidUtilities.displaySize.x / paramFileLocation.width;
            float f2 = AndroidUtilities.displaySize.y / paramFileLocation.height;
            if (f1 <= f2) {
              break label1272;
            }
            f1 = f2;
            float f3 = paramFileLocation.width;
            f2 = paramFileLocation.height;
            f3 = (AndroidUtilities.displaySize.x - f3 * f1) / 2.0F;
            f2 = (AndroidUtilities.displaySize.y - f2 * f1) / 2.0F;
            int k = Math.abs(paramMessageObject.left - localPlaceProviderObject.imageReceiver.getImageX());
            int m = Math.abs(paramMessageObject.top - localPlaceProviderObject.imageReceiver.getImageY());
            paramArrayList = new int[2];
            localPlaceProviderObject.parentView.getLocationInWindow(paramArrayList);
            i = paramArrayList[1] - AndroidUtilities.statusBarHeight - (localPlaceProviderObject.viewY + paramMessageObject.top) + localPlaceProviderObject.clipTopAddition;
            paramInt = i;
            if (i < 0) {
              paramInt = 0;
            }
            int j = localPlaceProviderObject.viewY + paramMessageObject.top + paramFileLocation.height - (paramArrayList[1] + localPlaceProviderObject.parentView.getHeight() - AndroidUtilities.statusBarHeight) + localPlaceProviderObject.clipBottomAddition;
            i = j;
            if (j < 0) {
              i = 0;
            }
            paramInt = Math.max(paramInt, m);
            i = Math.max(i, m);
            this.animationValues[0][0] = this.animatingImageView.getScaleX();
            this.animationValues[0][1] = this.animatingImageView.getScaleY();
            this.animationValues[0][2] = this.animatingImageView.getTranslationX();
            this.animationValues[0][3] = this.animatingImageView.getTranslationY();
            this.animationValues[0][4] = (k * localPlaceProviderObject.scale);
            this.animationValues[0][5] = (paramInt * localPlaceProviderObject.scale);
            this.animationValues[0][6] = (i * localPlaceProviderObject.scale);
            this.animationValues[0][7] = this.animatingImageView.getRadius();
            this.animationValues[1][0] = f1;
            this.animationValues[1][1] = f1;
            this.animationValues[1][2] = f3;
            this.animationValues[1][3] = f2;
            this.animationValues[1][4] = 0;
            this.animationValues[1][5] = 0;
            this.animationValues[1][6] = 0;
            this.animationValues[1][7] = 0;
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
                  PhotoViewer.access$8802(PhotoViewer.this, 0);
                  PhotoViewer.access$8902(PhotoViewer.this, 0L);
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
                PhotoViewer.this.windowLayoutParams.flags = 0;
                PhotoViewer.this.windowLayoutParams.softInputMode = 32;
                ((WindowManager)PhotoViewer.this.parentActivity.getSystemService("window")).updateViewLayout(PhotoViewer.this.windowView, PhotoViewer.this.windowLayoutParams);
                PhotoViewer.this.windowView.setFocusable(true);
                PhotoViewer.this.containerView.setFocusable(true);
              }
            };
            paramMessageObject.setDuration(200L);
            paramMessageObject.addListener(new AnimatorListenerAdapterProxy()
            {
              public void onAnimationEnd(Animator paramAnonymousAnimator)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    NotificationCenter.getInstance().setAnimationInProgress(false);
                    if (PhotoViewer.this.animationEndRunnable != null)
                    {
                      PhotoViewer.this.animationEndRunnable.run();
                      PhotoViewer.access$9402(PhotoViewer.this, null);
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
                NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded, NotificationCenter.mediaDidLoaded, NotificationCenter.dialogPhotosLoaded });
                NotificationCenter.getInstance().setAnimationInProgress(true);
                paramMessageObject.start();
              }
            });
            if (Build.VERSION.SDK_INT >= 18) {
              this.containerView.setLayerType(2, null);
            }
            BackgroundDrawable.access$9502(this.backgroundDrawable, new Runnable()
            {
              public void run()
              {
                PhotoViewer.access$3802(PhotoViewer.this, false);
                localPlaceProviderObject.imageReceiver.setVisible(false, true);
              }
            });
            return true;
          }
        }
        catch (Exception paramMessageObject)
        {
          FileLog.e("tmessages", paramMessageObject);
          return false;
        }
        boolean bool = false;
        continue;
        label1272:
        continue;
        label1275:
        if ((paramArrayList1 != null) && (this.sendPhotoType != 3))
        {
          this.windowLayoutParams.flags = 0;
          this.windowLayoutParams.softInputMode = 32;
          localWindowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
          this.windowView.setFocusable(true);
          this.containerView.setFocusable(true);
        }
        this.backgroundDrawable.setAlpha(255);
        this.containerView.setAlpha(1.0F);
        onPhotoShow(paramMessageObject, paramFileLocation, paramArrayList, paramArrayList1, paramInt, localPlaceProviderObject);
      }
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public boolean openPhoto(TLRPC.FileLocation paramFileLocation, PhotoViewerProvider paramPhotoViewerProvider)
  {
    return openPhoto(null, paramFileLocation, null, null, 0, paramPhotoViewerProvider, null, 0L, 0L);
  }
  
  public boolean openPhotoForSelect(ArrayList<Object> paramArrayList, int paramInt1, int paramInt2, PhotoViewerProvider paramPhotoViewerProvider, ChatActivity paramChatActivity)
  {
    this.sendPhotoType = paramInt2;
    TextView localTextView;
    if (this.pickerView != null)
    {
      localTextView = this.pickerView.doneButton;
      if (this.sendPhotoType != 1) {
        break label66;
      }
    }
    label66:
    for (String str = LocaleController.getString("Set", 2131166259).toUpperCase();; str = LocaleController.getString("Send", 2131166233).toUpperCase())
    {
      localTextView.setText(str);
      return openPhoto(null, null, null, paramArrayList, paramInt1, paramPhotoViewerProvider, paramChatActivity, 0L, 0L);
    }
  }
  
  public void setAnimationValue(float paramFloat)
  {
    this.animationValue = paramFloat;
    this.containerView.invalidate();
  }
  
  public void setParentActivity(Activity paramActivity)
  {
    if (this.parentActivity == paramActivity) {
      return;
    }
    this.parentActivity = paramActivity;
    this.actvityContext = new ContextThemeWrapper(this.parentActivity, 2131296262);
    if (progressDrawables == null)
    {
      progressDrawables = new Drawable[4];
      progressDrawables[0] = this.parentActivity.getResources().getDrawable(2130837598);
      progressDrawables[1] = this.parentActivity.getResources().getDrawable(2130837587);
      progressDrawables[2] = this.parentActivity.getResources().getDrawable(2130837800);
      progressDrawables[3] = this.parentActivity.getResources().getDrawable(2130837933);
    }
    this.scroller = new Scroller(paramActivity);
    this.windowView = new FrameLayout(paramActivity)
    {
      private Runnable attachRunnable;
      
      public boolean dispatchKeyEventPreIme(KeyEvent paramAnonymousKeyEvent)
      {
        if ((paramAnonymousKeyEvent != null) && (paramAnonymousKeyEvent.getKeyCode() == 4) && (paramAnonymousKeyEvent.getAction() == 1))
        {
          if ((PhotoViewer.this.captionEditText.isPopupShowing()) || (PhotoViewer.this.captionEditText.isKeyboardVisible()))
          {
            PhotoViewer.this.closeCaptionEnter(false);
            return false;
          }
          PhotoViewer.getInstance().closePhoto(true, false);
          return true;
        }
        return super.dispatchKeyEventPreIme(paramAnonymousKeyEvent);
      }
      
      protected void onAttachedToWindow()
      {
        super.onAttachedToWindow();
        PhotoViewer.access$2802(PhotoViewer.this, true);
      }
      
      protected void onDetachedFromWindow()
      {
        super.onDetachedFromWindow();
        PhotoViewer.access$2802(PhotoViewer.this, false);
      }
      
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        return (PhotoViewer.this.isVisible) && (super.onInterceptTouchEvent(paramAnonymousMotionEvent));
      }
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        PhotoViewer.this.animatingImageView.layout(0, 0, PhotoViewer.this.animatingImageView.getMeasuredWidth(), PhotoViewer.this.animatingImageView.getMeasuredHeight());
        PhotoViewer.this.containerView.layout(0, 0, PhotoViewer.this.containerView.getMeasuredWidth(), PhotoViewer.this.containerView.getMeasuredHeight());
        if ((BuildVars.DEBUG_VERSION) && (PhotoViewer.this.pickerView != null)) {
          FileLog.e("tmessages", "bottom of picker = " + PhotoViewer.this.pickerView.getBottom());
        }
        if (paramAnonymousBoolean)
        {
          PhotoViewer.access$2302(PhotoViewer.this, 1.0F);
          PhotoViewer.access$2402(PhotoViewer.this, 0.0F);
          PhotoViewer.access$2502(PhotoViewer.this, 0.0F);
          PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
          if (PhotoViewer.this.checkImageView != null) {
            PhotoViewer.this.checkImageView.post(new Runnable()
            {
              public void run()
              {
                FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)PhotoViewer.this.checkImageView.getLayoutParams();
                int i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                if ((i == 3) || (i == 1)) {}
                for (float f = 58.0F;; f = 68.0F)
                {
                  localLayoutParams.topMargin = AndroidUtilities.dp(f);
                  PhotoViewer.this.checkImageView.setLayoutParams(localLayoutParams);
                  return;
                }
              }
            });
          }
        }
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        int i = View.MeasureSpec.getSize(paramAnonymousInt1);
        paramAnonymousInt2 = View.MeasureSpec.getSize(paramAnonymousInt2) - AndroidUtilities.getViewInset(this);
        setMeasuredDimension(i, paramAnonymousInt2);
        paramAnonymousInt1 = paramAnonymousInt2;
        if (paramAnonymousInt2 > AndroidUtilities.displaySize.y) {
          paramAnonymousInt1 = AndroidUtilities.displaySize.y;
        }
        ViewGroup.LayoutParams localLayoutParams = PhotoViewer.this.animatingImageView.getLayoutParams();
        PhotoViewer.this.animatingImageView.measure(View.MeasureSpec.makeMeasureSpec(localLayoutParams.width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, Integer.MIN_VALUE));
        PhotoViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(paramAnonymousInt1, 1073741824));
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        return (PhotoViewer.this.isVisible) && (PhotoViewer.this.onTouchEvent(paramAnonymousMotionEvent));
      }
      
      public ActionMode startActionModeForChild(View paramAnonymousView, ActionMode.Callback paramAnonymousCallback, int paramAnonymousInt)
      {
        if (Build.VERSION.SDK_INT >= 23)
        {
          Object localObject = PhotoViewer.this.parentActivity.findViewById(16908290);
          if ((localObject instanceof ViewGroup)) {
            try
            {
              localObject = ((ViewGroup)localObject).startActionModeForChild(paramAnonymousView, paramAnonymousCallback, paramAnonymousInt);
              return (ActionMode)localObject;
            }
            catch (Throwable localThrowable)
            {
              FileLog.e("tmessages", localThrowable);
            }
          }
        }
        return super.startActionModeForChild(paramAnonymousView, paramAnonymousCallback, paramAnonymousInt);
      }
    };
    this.windowView.setBackgroundDrawable(this.backgroundDrawable);
    this.windowView.setFocusable(false);
    if (Build.VERSION.SDK_INT >= 21)
    {
      if (Build.VERSION.SDK_INT >= 23) {
        this.windowView.setFitsSystemWindows(true);
      }
      this.windowView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
      {
        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View paramAnonymousView, WindowInsets paramAnonymousWindowInsets)
        {
          PhotoViewer.access$3002(PhotoViewer.this, paramAnonymousWindowInsets);
          PhotoViewer.this.windowView.requestLayout();
          return paramAnonymousWindowInsets;
        }
      });
    }
    this.animatingImageView = new ClippingImageView(paramActivity);
    this.animatingImageView.setAnimationValues(this.animationValues);
    this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0F));
    this.containerView = new FrameLayoutDrawer(paramActivity);
    this.containerView.setFocusable(false);
    this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
    this.windowLayoutParams = new WindowManager.LayoutParams();
    this.windowLayoutParams.height = -1;
    this.windowLayoutParams.format = -3;
    this.windowLayoutParams.width = -1;
    this.windowLayoutParams.gravity = 48;
    this.windowLayoutParams.type = 99;
    this.windowLayoutParams.flags = 8;
    this.actionBar = new ActionBar(paramActivity);
    this.actionBar.setBackgroundColor(2130706432);
    this.actionBar.setOccupyStatusBar(false);
    this.actionBar.setItemsBackgroundColor(1090519039);
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setTitle(LocaleController.formatString("Of", 2131166045, new Object[] { Integer.valueOf(1), Integer.valueOf(1) }));
    this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0F));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public boolean canOpenMenu()
      {
        if (PhotoViewer.this.currentMessageObject != null) {
          if (!FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner).exists()) {
            break label72;
          }
        }
        for (;;)
        {
          return true;
          TLRPC.FileLocation localFileLocation;
          if (PhotoViewer.this.currentFileLocation != null)
          {
            localFileLocation = PhotoViewer.this.currentFileLocation;
            if (PhotoViewer.this.avatarsDialogId == 0) {
              break label74;
            }
          }
          label72:
          label74:
          for (boolean bool = true; !FileLoader.getPathToAttach(localFileLocation, bool).exists(); bool = false) {
            return false;
          }
        }
      }
      
      public void onItemClick(int paramAnonymousInt)
      {
        boolean bool = true;
        int i = 1;
        if (paramAnonymousInt == -1) {
          if ((PhotoViewer.this.needCaptionLayout) && ((PhotoViewer.this.captionEditText.isPopupShowing()) || (PhotoViewer.this.captionEditText.isKeyboardVisible()))) {
            PhotoViewer.this.closeCaptionEnter(false);
          }
        }
        label253:
        label258:
        do
        {
          do
          {
            do
            {
              do
              {
                return;
                PhotoViewer.this.closePhoto(true, false);
                return;
                if (paramAnonymousInt == 1)
                {
                  if ((Build.VERSION.SDK_INT >= 23) && (PhotoViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
                  {
                    PhotoViewer.this.parentActivity.requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 4);
                    return;
                  }
                  localObject = null;
                  Activity localActivity;
                  if (PhotoViewer.this.currentMessageObject != null)
                  {
                    localObject = FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
                    if ((localObject == null) || (!((File)localObject).exists())) {
                      break label258;
                    }
                    localObject = ((File)localObject).toString();
                    localActivity = PhotoViewer.this.parentActivity;
                    if ((PhotoViewer.this.currentMessageObject == null) || (!PhotoViewer.this.currentMessageObject.isVideo())) {
                      break label253;
                    }
                  }
                  for (paramAnonymousInt = i;; paramAnonymousInt = 0)
                  {
                    MediaController.saveFile((String)localObject, localActivity, paramAnonymousInt, null, null);
                    return;
                    if (PhotoViewer.this.currentFileLocation == null) {
                      break;
                    }
                    localObject = PhotoViewer.this.currentFileLocation;
                    if (PhotoViewer.this.avatarsDialogId != 0) {}
                    for (bool = true;; bool = false)
                    {
                      localObject = FileLoader.getPathToAttach((TLObject)localObject, bool);
                      break;
                    }
                  }
                  localObject = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
                  ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165299));
                  ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166044), null);
                  ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PleaseDownload", 2131166127));
                  PhotoViewer.this.showAlertDialog((AlertDialog.Builder)localObject);
                  return;
                }
                if (paramAnonymousInt != 2) {
                  break;
                }
                if (PhotoViewer.this.opennedFromMedia)
                {
                  PhotoViewer.this.closePhoto(true, false);
                  return;
                }
              } while (PhotoViewer.this.currentDialogId == 0L);
              PhotoViewer.access$3802(PhotoViewer.this, true);
              localObject = new Bundle();
              ((Bundle)localObject).putLong("dialog_id", PhotoViewer.this.currentDialogId);
              localObject = new MediaActivity((Bundle)localObject);
              if (PhotoViewer.this.parentChatActivity != null) {
                ((MediaActivity)localObject).setChatInfo(PhotoViewer.this.parentChatActivity.getCurrentChatInfo());
              }
              PhotoViewer.this.closePhoto(false, false);
              ((LaunchActivity)PhotoViewer.this.parentActivity).presentFragment((BaseFragment)localObject, false, true);
              return;
            } while (paramAnonymousInt == 3);
            if (paramAnonymousInt != 6) {
              break;
            }
          } while (PhotoViewer.this.parentActivity == null);
          Object localObject = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
          if ((PhotoViewer.this.currentMessageObject != null) && (PhotoViewer.this.currentMessageObject.isVideo())) {
            ((AlertDialog.Builder)localObject).setMessage(LocaleController.formatString("AreYouSureDeleteVideo", 2131165323, new Object[0]));
          }
          for (;;)
          {
            ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165299));
            ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (!PhotoViewer.this.imagesArr.isEmpty()) {
                  if ((PhotoViewer.this.currentIndex >= 0) && (PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size())) {}
                }
                label489:
                do
                {
                  do
                  {
                    MessageObject localMessageObject;
                    do
                    {
                      return;
                      localMessageObject = (MessageObject)PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                    } while (!localMessageObject.isSent());
                    PhotoViewer.this.closePhoto(false, false);
                    ArrayList localArrayList = new ArrayList();
                    localArrayList.add(Integer.valueOf(localMessageObject.getId()));
                    localObject2 = null;
                    Object localObject3 = null;
                    localObject1 = localObject3;
                    paramAnonymous2DialogInterface = (DialogInterface)localObject2;
                    if ((int)localMessageObject.getDialogId() == 0)
                    {
                      localObject1 = localObject3;
                      paramAnonymous2DialogInterface = (DialogInterface)localObject2;
                      if (localMessageObject.messageOwner.random_id != 0L)
                      {
                        paramAnonymous2DialogInterface = new ArrayList();
                        paramAnonymous2DialogInterface.add(Long.valueOf(localMessageObject.messageOwner.random_id));
                        localObject1 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(localMessageObject.getDialogId() >> 32)));
                      }
                    }
                    MessagesController.getInstance().deleteMessages(localArrayList, paramAnonymous2DialogInterface, (TLRPC.EncryptedChat)localObject1, localMessageObject.messageOwner.to_id.channel_id);
                    return;
                  } while ((PhotoViewer.this.avatarsArr.isEmpty()) || (PhotoViewer.this.currentIndex < 0) || (PhotoViewer.this.currentIndex >= PhotoViewer.this.avatarsArr.size()));
                  localObject1 = (TLRPC.Photo)PhotoViewer.this.avatarsArr.get(PhotoViewer.this.currentIndex);
                  Object localObject2 = (TLRPC.FileLocation)PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
                  paramAnonymous2DialogInterface = (DialogInterface)localObject1;
                  if ((localObject1 instanceof TLRPC.TL_photoEmpty)) {
                    paramAnonymous2DialogInterface = null;
                  }
                  i = 0;
                  paramAnonymous2Int = i;
                  if (PhotoViewer.this.currentUserAvatarLocation != null)
                  {
                    if (paramAnonymous2DialogInterface == null) {
                      break label489;
                    }
                    localObject1 = paramAnonymous2DialogInterface.sizes.iterator();
                    do
                    {
                      paramAnonymous2Int = i;
                      if (!((Iterator)localObject1).hasNext()) {
                        break;
                      }
                      localObject2 = (TLRPC.PhotoSize)((Iterator)localObject1).next();
                    } while ((((TLRPC.PhotoSize)localObject2).location.local_id != PhotoViewer.this.currentUserAvatarLocation.local_id) || (((TLRPC.PhotoSize)localObject2).location.volume_id != PhotoViewer.this.currentUserAvatarLocation.volume_id));
                    paramAnonymous2Int = 1;
                  }
                  while (paramAnonymous2Int != 0)
                  {
                    MessagesController.getInstance().deleteUserPhoto(null);
                    PhotoViewer.this.closePhoto(false, false);
                    return;
                    paramAnonymous2Int = i;
                    if (((TLRPC.FileLocation)localObject2).local_id == PhotoViewer.this.currentUserAvatarLocation.local_id)
                    {
                      paramAnonymous2Int = i;
                      if (((TLRPC.FileLocation)localObject2).volume_id == PhotoViewer.this.currentUserAvatarLocation.volume_id) {
                        paramAnonymous2Int = 1;
                      }
                    }
                  }
                } while (paramAnonymous2DialogInterface == null);
                Object localObject1 = new TLRPC.TL_inputPhoto();
                ((TLRPC.TL_inputPhoto)localObject1).id = paramAnonymous2DialogInterface.id;
                ((TLRPC.TL_inputPhoto)localObject1).access_hash = paramAnonymous2DialogInterface.access_hash;
                MessagesController.getInstance().deleteUserPhoto((TLRPC.InputPhoto)localObject1);
                MessagesStorage.getInstance().clearUserPhoto(PhotoViewer.this.avatarsDialogId, paramAnonymous2DialogInterface.id);
                PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                if (PhotoViewer.this.imagesArrLocations.isEmpty())
                {
                  PhotoViewer.this.closePhoto(false, false);
                  return;
                }
                int i = PhotoViewer.this.currentIndex;
                paramAnonymous2Int = i;
                if (i >= PhotoViewer.this.avatarsArr.size()) {
                  paramAnonymous2Int = PhotoViewer.this.avatarsArr.size() - 1;
                }
                PhotoViewer.access$4102(PhotoViewer.this, -1);
                PhotoViewer.this.setImageIndex(paramAnonymous2Int, true);
              }
            });
            ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            PhotoViewer.this.showAlertDialog((AlertDialog.Builder)localObject);
            return;
            if ((PhotoViewer.this.currentMessageObject != null) && (PhotoViewer.this.currentMessageObject.isGif())) {
              ((AlertDialog.Builder)localObject).setMessage(LocaleController.formatString("AreYouSure", 2131165313, new Object[0]));
            } else {
              ((AlertDialog.Builder)localObject).setMessage(LocaleController.formatString("AreYouSureDeletePhoto", 2131165321, new Object[0]));
            }
          }
          if (paramAnonymousInt == 9)
          {
            PhotoViewer.this.closeCaptionEnter(true);
            return;
          }
          if (paramAnonymousInt == 10)
          {
            PhotoViewer.this.onSharePressed();
            return;
          }
          if (paramAnonymousInt == 11) {
            try
            {
              AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
              PhotoViewer.this.closePhoto(false, false);
              return;
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
              return;
            }
          }
          if (paramAnonymousInt == 12)
          {
            PhotoViewer localPhotoViewer = PhotoViewer.this;
            if (!PhotoViewer.this.muteVideo) {}
            for (;;)
            {
              PhotoViewer.access$4802(localPhotoViewer, bool);
              if (PhotoViewer.this.videoPlayer != null) {
                PhotoViewer.this.videoPlayer.setMute(PhotoViewer.this.muteVideo);
              }
              if (!PhotoViewer.this.muteVideo) {
                break;
              }
              PhotoViewer.this.actionBar.setTitle(LocaleController.getString("AttachGif", 2131165340));
              PhotoViewer.this.muteItem.setIcon(2130838029);
              return;
              bool = false;
            }
            PhotoViewer.this.actionBar.setTitle(LocaleController.getString("AttachVideo", 2131165345));
            PhotoViewer.this.muteItem.setIcon(2130838030);
            return;
          }
        } while ((paramAnonymousInt != 13) || (PhotoViewer.this.parentActivity == null) || (PhotoViewer.this.currentMessageObject == null) || (PhotoViewer.this.currentMessageObject.messageOwner.media == null) || (PhotoViewer.this.currentMessageObject.messageOwner.media.photo == null));
        new StickersAlert(PhotoViewer.this.parentActivity, PhotoViewer.this.currentMessageObject.messageOwner.media.photo).show();
      }
    });
    paramActivity = this.actionBar.createMenu();
    this.masksItem = paramActivity.addItem(13, 2130837739);
    this.muteItem = paramActivity.addItem(12, 2130838030);
    this.menuItem = paramActivity.addItem(0, 2130837708);
    this.menuItem.addSubItem(11, LocaleController.getString("OpenInExternalApp", 2131166058), 0);
    this.menuItem.addSubItem(2, LocaleController.getString("ShowAllMedia", 2131166291), 0);
    this.menuItem.addSubItem(10, LocaleController.getString("ShareFile", 2131166274), 0);
    this.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", 2131166202), 0);
    this.menuItem.addSubItem(6, LocaleController.getString("Delete", 2131165560), 0);
    this.captionDoneItem = paramActivity.addItemWithWidth(9, 2130837720, AndroidUtilities.dp(56.0F));
    this.bottomLayout = new FrameLayout(this.actvityContext);
    this.bottomLayout.setBackgroundColor(2130706432);
    this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
    this.captionTextViewOld = new TextView(this.actvityContext);
    this.captionTextViewOld.setMaxLines(10);
    this.captionTextViewOld.setBackgroundColor(2130706432);
    this.captionTextViewOld.setPadding(AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F));
    this.captionTextViewOld.setLinkTextColor(-1);
    this.captionTextViewOld.setTextColor(-1);
    this.captionTextViewOld.setGravity(19);
    this.captionTextViewOld.setTextSize(1, 16.0F);
    this.captionTextViewOld.setVisibility(4);
    this.containerView.addView(this.captionTextViewOld, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
    this.captionTextViewOld.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (PhotoViewer.this.cropItem.getVisibility() == 0) {
          PhotoViewer.this.openCaptionEnter();
        }
      }
    });
    paramActivity = new TextView(this.actvityContext);
    this.captionTextViewNew = paramActivity;
    this.captionTextView = paramActivity;
    this.captionTextViewNew.setMaxLines(10);
    this.captionTextViewNew.setBackgroundColor(2130706432);
    this.captionTextViewNew.setPadding(AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F));
    this.captionTextViewNew.setLinkTextColor(-1);
    this.captionTextViewNew.setTextColor(-1);
    this.captionTextViewNew.setGravity(19);
    this.captionTextViewNew.setTextSize(1, 16.0F);
    this.captionTextViewNew.setVisibility(4);
    this.containerView.addView(this.captionTextViewNew, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
    this.captionTextViewNew.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
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
    this.shareButton.setImageResource(2130837979);
    this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
    this.shareButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(1090519039));
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
    if (Build.VERSION.SDK_INT >= 16)
    {
      this.videoPlayerSeekbar = new SeekBar(this.containerView.getContext());
      this.videoPlayerSeekbar.setColors(1728053247, -1, -1);
      this.videoPlayerSeekbar.setDelegate(new SeekBar.SeekBarDelegate()
      {
        public void onSeekBarDrag(float paramAnonymousFloat)
        {
          if (PhotoViewer.this.videoPlayer != null) {
            PhotoViewer.this.videoPlayer.getPlayerControl().seekTo((int)((float)PhotoViewer.this.videoPlayer.getDuration() * paramAnonymousFloat));
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
          float f = 0.0F;
          if (PhotoViewer.this.videoPlayer != null)
          {
            PlayerControl localPlayerControl = PhotoViewer.this.videoPlayer.getPlayerControl();
            f = localPlayerControl.getCurrentPosition() / localPlayerControl.getDuration();
          }
          PhotoViewer.this.videoPlayerSeekbar.setProgress(f);
        }
        
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          super.onMeasure(paramAnonymousInt1, paramAnonymousInt2);
          if (PhotoViewer.this.videoPlayer != null)
          {
            long l2 = PhotoViewer.this.videoPlayer.getDuration();
            l1 = l2;
            if (l2 != -1L) {}
          }
          for (long l1 = 0L;; l1 = 0L)
          {
            l1 /= 1000L;
            paramAnonymousInt1 = (int)Math.ceil(PhotoViewer.this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L), Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L) })));
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
            return true;
          }
          return super.onTouchEvent(paramAnonymousMotionEvent);
        }
      };
      this.videoPlayerControlFrameLayout.setWillNotDraw(false);
      this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
      this.videoPlayButton = new ImageView(this.containerView.getContext());
      this.videoPlayButton.setScaleType(ImageView.ScaleType.CENTER);
      this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
      this.videoPlayButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (PhotoViewer.this.videoPlayer != null)
          {
            if (PhotoViewer.this.isPlaying) {
              PhotoViewer.this.videoPlayer.getPlayerControl().pause();
            }
          }
          else {
            return;
          }
          PhotoViewer.this.videoPlayer.getPlayerControl().start();
        }
      });
      this.videoPlayerTime = new TextView(this.containerView.getContext());
      this.videoPlayerTime.setTextColor(-1);
      this.videoPlayerTime.setGravity(16);
      this.videoPlayerTime.setTextSize(1, 13.0F);
      this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0F, 53, 0.0F, 0.0F, 8.0F, 0.0F));
    }
    this.pickerView = new PickerBottomLayoutViewer(this.actvityContext);
    this.pickerView.setBackgroundColor(2130706432);
    this.containerView.addView(this.pickerView, LayoutHelper.createFrame(-1, 48, 83));
    this.pickerView.cancelButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((PhotoViewer.this.placeProvider instanceof PhotoViewer.EmptyPhotoViewerProvider)) {
          PhotoViewer.this.closePhoto(false, false);
        }
        while (PhotoViewer.this.placeProvider == null) {
          return;
        }
        paramAnonymousView = PhotoViewer.this;
        if (!PhotoViewer.this.placeProvider.cancelButtonPressed()) {}
        for (boolean bool = true;; bool = false)
        {
          paramAnonymousView.closePhoto(bool, false);
          return;
        }
      }
    });
    this.pickerView.doneButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (PhotoViewer.this.placeProvider != null)
        {
          PhotoViewer.this.placeProvider.sendButtonPressed(PhotoViewer.this.currentIndex);
          PhotoViewer.this.closePhoto(false, false);
        }
      }
    });
    paramActivity = new LinearLayout(this.parentActivity);
    paramActivity.setOrientation(0);
    this.pickerView.addView(paramActivity, LayoutHelper.createFrame(-2, 48, 49));
    this.tuneItem = new ImageView(this.parentActivity);
    this.tuneItem.setScaleType(ImageView.ScaleType.CENTER);
    this.tuneItem.setImageResource(2130837891);
    this.tuneItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(1090519039));
    paramActivity.addView(this.tuneItem, LayoutHelper.createLinear(56, 48));
    this.tuneItem.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoViewer.this.switchToEditMode(2);
      }
    });
    this.paintItem = new ImageView(this.parentActivity);
    this.paintItem.setScaleType(ImageView.ScaleType.CENTER);
    this.paintItem.setImageResource(2130837883);
    this.paintItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(1090519039));
    paramActivity.addView(this.paintItem, LayoutHelper.createLinear(56, 48));
    this.paintItem.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoViewer.this.switchToEditMode(3);
      }
    });
    this.cropItem = new ImageView(this.parentActivity);
    this.cropItem.setScaleType(ImageView.ScaleType.CENTER);
    this.cropItem.setImageResource(2130837880);
    this.cropItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(1090519039));
    paramActivity.addView(this.cropItem, LayoutHelper.createLinear(56, 48));
    this.cropItem.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoViewer.this.switchToEditMode(1);
      }
    });
    this.editorDoneLayout = new PickerBottomLayoutViewer(this.actvityContext);
    this.editorDoneLayout.setBackgroundColor(2130706432);
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
        if (PhotoViewer.this.currentEditMode == 1)
        {
          PhotoViewer.this.photoCropView.cancelAnimationRunnable();
          if (PhotoViewer.this.imageMoveAnimation != null) {
            return;
          }
        }
        PhotoViewer.this.applyCurrentEditMode();
        PhotoViewer.this.switchToEditMode(0);
      }
    });
    paramActivity = new ImageView(this.actvityContext);
    paramActivity.setScaleType(ImageView.ScaleType.CENTER);
    paramActivity.setImageResource(2130838016);
    paramActivity.setBackgroundDrawable(Theme.createBarSelectorDrawable(1090519039));
    this.editorDoneLayout.addView(paramActivity, LayoutHelper.createFrame(48, 48, 17));
    paramActivity.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoViewer.this.centerImage.setOrientation(PhotoViewer.this.centerImage.getOrientation() - 90, false);
        PhotoViewer.this.photoCropView.setOrientation(PhotoViewer.this.centerImage.getOrientation());
        PhotoViewer.this.containerView.invalidate();
      }
    });
    this.gestureDetector = new GestureDetector(this.containerView.getContext(), this);
    this.gestureDetector.setOnDoubleTapListener(this);
    this.centerImage.setParentView(this.containerView);
    this.centerImage.setCrossfadeAlpha((byte)2);
    this.centerImage.setInvalidateAll(true);
    this.leftImage.setParentView(this.containerView);
    this.leftImage.setCrossfadeAlpha((byte)2);
    this.leftImage.setInvalidateAll(true);
    this.rightImage.setParentView(this.containerView);
    this.rightImage.setCrossfadeAlpha((byte)2);
    this.rightImage.setInvalidateAll(true);
    int i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
    this.checkImageView = new CheckBox(this.containerView.getContext(), 2130837978);
    this.checkImageView.setDrawBackground(true);
    this.checkImageView.setSize(45);
    this.checkImageView.setCheckOffset(AndroidUtilities.dp(1.0F));
    this.checkImageView.setColor(-12793105);
    this.checkImageView.setVisibility(8);
    paramActivity = this.containerView;
    Object localObject = this.checkImageView;
    if ((i == 3) || (i == 1)) {}
    for (float f = 58.0F;; f = 68.0F)
    {
      paramActivity.addView((View)localObject, LayoutHelper.createFrame(45, 45.0F, 53, 0.0F, f, 10.0F, 0.0F));
      this.checkImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (PhotoViewer.this.placeProvider != null)
          {
            PhotoViewer.this.placeProvider.setPhotoChecked(PhotoViewer.this.currentIndex);
            PhotoViewer.this.checkImageView.setChecked(PhotoViewer.this.placeProvider.isPhotoChecked(PhotoViewer.this.currentIndex), true);
            PhotoViewer.this.updateSelectedCount();
          }
        }
      });
      this.captionEditText = new PhotoViewerCaptionEnterView(this.actvityContext, this.containerView, this.windowView);
      this.captionEditText.setDelegate(new PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate()
      {
        public void onCaptionEnter()
        {
          PhotoViewer.this.closeCaptionEnter(true);
        }
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence)
        {
          if ((PhotoViewer.this.mentionsAdapter != null) && (PhotoViewer.this.captionEditText != null) && (PhotoViewer.this.parentChatActivity != null) && (paramAnonymousCharSequence != null)) {
            PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(paramAnonymousCharSequence.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages);
          }
        }
        
        public void onWindowSizeChanged(int paramAnonymousInt)
        {
          int j = Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount());
          int i;
          if (PhotoViewer.this.mentionsAdapter.getItemCount() > 3)
          {
            i = 18;
            i = AndroidUtilities.dp(i + j * 36);
            if (paramAnonymousInt - ActionBar.getCurrentActionBarHeight() * 2 >= i) {
              break label103;
            }
            PhotoViewer.access$6302(PhotoViewer.this, false);
            if ((PhotoViewer.this.mentionListView != null) && (PhotoViewer.this.mentionListView.getVisibility() == 0)) {
              PhotoViewer.this.mentionListView.setVisibility(4);
            }
          }
          label103:
          do
          {
            return;
            i = 0;
            break;
            PhotoViewer.access$6302(PhotoViewer.this, true);
          } while ((PhotoViewer.this.mentionListView == null) || (PhotoViewer.this.mentionListView.getVisibility() != 4));
          PhotoViewer.this.mentionListView.setVisibility(0);
        }
      });
      this.containerView.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, -400.0F));
      this.mentionListView = new RecyclerListView(this.actvityContext);
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
      this.mentionListView.setBackgroundColor(2130706432);
      this.mentionListView.setVisibility(8);
      this.mentionListView.setClipToPadding(true);
      this.mentionListView.setOverScrollMode(2);
      this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
      paramActivity = this.mentionListView;
      localObject = new MentionsAdapter(this.actvityContext, true, 0L, new MentionsAdapter.MentionsAdapterDelegate()
      {
        public void needChangePanelVisibility(boolean paramAnonymousBoolean)
        {
          int i;
          if (paramAnonymousBoolean)
          {
            FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)PhotoViewer.this.mentionListView.getLayoutParams();
            int j = Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount());
            if (PhotoViewer.this.mentionsAdapter.getItemCount() > 3)
            {
              i = 18;
              i = j * 36 + i;
              localLayoutParams.height = AndroidUtilities.dp(i);
              localLayoutParams.topMargin = (-AndroidUtilities.dp(i));
              PhotoViewer.this.mentionListView.setLayoutParams(localLayoutParams);
              if (PhotoViewer.this.mentionListAnimation != null)
              {
                PhotoViewer.this.mentionListAnimation.cancel();
                PhotoViewer.access$6402(PhotoViewer.this, null);
              }
              if (PhotoViewer.this.mentionListView.getVisibility() != 0) {
                break label150;
              }
              PhotoViewer.this.mentionListView.setAlpha(1.0F);
            }
          }
          label150:
          do
          {
            return;
            i = 0;
            break;
            PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
            if (PhotoViewer.this.allowMentions)
            {
              PhotoViewer.this.mentionListView.setVisibility(0);
              PhotoViewer.access$6402(PhotoViewer.this, new AnimatorSet());
              PhotoViewer.this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[] { 0.0F, 1.0F }) });
              PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapterProxy()
              {
                public void onAnimationEnd(Animator paramAnonymous2Animator)
                {
                  if ((PhotoViewer.this.mentionListAnimation != null) && (PhotoViewer.this.mentionListAnimation.equals(paramAnonymous2Animator))) {
                    PhotoViewer.access$6402(PhotoViewer.this, null);
                  }
                }
              });
              PhotoViewer.this.mentionListAnimation.setDuration(200L);
              PhotoViewer.this.mentionListAnimation.start();
              return;
            }
            PhotoViewer.this.mentionListView.setAlpha(1.0F);
            PhotoViewer.this.mentionListView.setVisibility(4);
            return;
            if (PhotoViewer.this.mentionListAnimation != null)
            {
              PhotoViewer.this.mentionListAnimation.cancel();
              PhotoViewer.access$6402(PhotoViewer.this, null);
            }
          } while (PhotoViewer.this.mentionListView.getVisibility() == 8);
          if (PhotoViewer.this.allowMentions)
          {
            PhotoViewer.access$6402(PhotoViewer.this, new AnimatorSet());
            PhotoViewer.this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[] { 0.0F }) });
            PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapterProxy()
            {
              public void onAnimationEnd(Animator paramAnonymous2Animator)
              {
                if ((PhotoViewer.this.mentionListAnimation != null) && (PhotoViewer.this.mentionListAnimation.equals(paramAnonymous2Animator)))
                {
                  PhotoViewer.this.mentionListView.setVisibility(8);
                  PhotoViewer.access$6402(PhotoViewer.this, null);
                }
              }
            });
            PhotoViewer.this.mentionListAnimation.setDuration(200L);
            PhotoViewer.this.mentionListAnimation.start();
            return;
          }
          PhotoViewer.this.mentionListView.setVisibility(8);
        }
        
        public void onContextClick(TLRPC.BotInlineResult paramAnonymousBotInlineResult) {}
        
        public void onContextSearch(boolean paramAnonymousBoolean) {}
      });
      this.mentionsAdapter = ((MentionsAdapter)localObject);
      paramActivity.setAdapter((RecyclerView.Adapter)localObject);
      this.mentionsAdapter.setAllowNewMentions(false);
      this.mentionListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          paramAnonymousView = PhotoViewer.this.mentionsAdapter.getItem(paramAnonymousInt);
          paramAnonymousInt = PhotoViewer.this.mentionsAdapter.getResultStartPosition();
          int i = PhotoViewer.this.mentionsAdapter.getResultLength();
          if ((paramAnonymousView instanceof TLRPC.User))
          {
            paramAnonymousView = (TLRPC.User)paramAnonymousView;
            if (paramAnonymousView != null) {
              PhotoViewer.this.captionEditText.replaceWithText(paramAnonymousInt, i, "@" + paramAnonymousView.username + " ");
            }
          }
          while (!(paramAnonymousView instanceof String)) {
            return;
          }
          PhotoViewer.this.captionEditText.replaceWithText(paramAnonymousInt, i, paramAnonymousView + " ");
        }
      });
      this.mentionListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((PhotoViewer.this.mentionsAdapter.getItem(paramAnonymousInt) instanceof String))
          {
            paramAnonymousView = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
            paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
            paramAnonymousView.setMessage(LocaleController.getString("ClearSearch", 2131165514));
            paramAnonymousView.setPositiveButton(LocaleController.getString("ClearButton", 2131165508).toUpperCase(), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                PhotoViewer.this.mentionsAdapter.clearRecentHashtags();
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            PhotoViewer.this.showAlertDialog(paramAnonymousView);
            return true;
          }
          return false;
        }
      });
      return;
    }
  }
  
  public void setParentChatActivity(ChatActivity paramChatActivity)
  {
    this.parentChatActivity = paramChatActivity;
  }
  
  public void showAlertDialog(AlertDialog.Builder paramBuilder)
  {
    if (this.parentActivity == null) {
      return;
    }
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
        this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
          public void onDismiss(DialogInterface paramAnonymousDialogInterface)
          {
            PhotoViewer.access$6902(PhotoViewer.this, null);
          }
        });
        return;
      }
      catch (Exception paramBuilder)
      {
        FileLog.e("tmessages", paramBuilder);
        return;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  private class BackgroundDrawable
    extends ColorDrawable
  {
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
        this.drawRunnable.run();
        this.drawRunnable = null;
      }
    }
    
    public void setAlpha(int paramInt)
    {
      DrawerLayoutContainer localDrawerLayoutContainer;
      if ((PhotoViewer.this.parentActivity instanceof LaunchActivity))
      {
        localDrawerLayoutContainer = ((LaunchActivity)PhotoViewer.this.parentActivity).drawerLayoutContainer;
        if ((PhotoViewer.this.isVisible) && (paramInt == 255)) {
          break label57;
        }
      }
      label57:
      for (boolean bool = true;; bool = false)
      {
        localDrawerLayoutContainer.setAllowDrawContent(bool);
        super.setAlpha(paramInt);
        return;
      }
    }
  }
  
  public static class EmptyPhotoViewerProvider
    implements PhotoViewer.PhotoViewerProvider
  {
    public boolean allowCaption()
    {
      return true;
    }
    
    public boolean cancelButtonPressed()
    {
      return true;
    }
    
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
    {
      return null;
    }
    
    public int getSelectedCount()
    {
      return 0;
    }
    
    public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
    {
      return null;
    }
    
    public boolean isPhotoChecked(int paramInt)
    {
      return false;
    }
    
    public void sendButtonPressed(int paramInt) {}
    
    public void setPhotoChecked(int paramInt) {}
    
    public void updatePhotoAtIndex(int paramInt) {}
    
    public void willHidePhotoViewer() {}
    
    public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt) {}
  }
  
  private class FrameLayoutDrawer
    extends SizeNotifierFrameLayoutPhoto
  {
    public FrameLayoutDrawer(Context paramContext)
    {
      super();
      setWillNotDraw(false);
    }
    
    protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
    {
      return (paramView != PhotoViewer.this.aspectRatioFrameLayout) && (super.drawChild(paramCanvas, paramView, paramLong));
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      PhotoViewer.getInstance().onDraw(paramCanvas);
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i1 = getChildCount();
      if ((getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) && (!AndroidUtilities.isInMultiwindow)) {}
      View localView;
      for (int m = PhotoViewer.this.captionEditText.getEmojiPadding();; m = 0)
      {
        int n = 0;
        for (;;)
        {
          if (n >= i1) {
            break label639;
          }
          localView = getChildAt(n);
          if (localView.getVisibility() != 8) {
            break;
          }
          n += 1;
        }
      }
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
      int i2 = localView.getMeasuredWidth();
      int i3 = localView.getMeasuredHeight();
      int j = localLayoutParams.gravity;
      int i = j;
      if (j == -1) {
        i = 51;
      }
      int k;
      switch (i & 0x7 & 0x7)
      {
      default: 
        k = localLayoutParams.leftMargin;
        label167:
        switch (i & 0x70)
        {
        default: 
          j = localLayoutParams.topMargin;
          label215:
          if (localView == PhotoViewer.this.mentionListView) {
            if ((!PhotoViewer.this.captionEditText.isPopupShowing()) && (!PhotoViewer.this.captionEditText.isKeyboardVisible()) && (PhotoViewer.this.captionEditText.getEmojiPadding() == 0)) {
              i = j + AndroidUtilities.dp(400.0F);
            }
          }
          break;
        }
        break;
      }
      for (;;)
      {
        localView.layout(k, i, k + i2, i + i3);
        break;
        k = (paramInt3 - paramInt1 - i2) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
        break label167;
        k = paramInt3 - i2 - localLayoutParams.rightMargin;
        break label167;
        j = localLayoutParams.topMargin;
        break label215;
        j = (paramInt4 - m - paramInt2 - i3) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
        break label215;
        j = paramInt4 - m - paramInt2 - i3 - localLayoutParams.bottomMargin;
        break label215;
        i = j - PhotoViewer.this.captionEditText.getMeasuredHeight();
        continue;
        if (localView == PhotoViewer.this.captionEditText)
        {
          i = j;
          if (!PhotoViewer.this.captionEditText.isPopupShowing())
          {
            i = j;
            if (!PhotoViewer.this.captionEditText.isKeyboardVisible())
            {
              i = j;
              if (PhotoViewer.this.captionEditText.getEmojiPadding() == 0) {
                i = j + AndroidUtilities.dp(400.0F);
              }
            }
          }
        }
        else if ((localView == PhotoViewer.this.pickerView) || (localView == PhotoViewer.this.captionTextViewNew) || (localView == PhotoViewer.this.captionTextViewOld))
        {
          if (!PhotoViewer.this.captionEditText.isPopupShowing())
          {
            i = j;
            if (!PhotoViewer.this.captionEditText.isKeyboardVisible()) {}
          }
          else
          {
            i = j + AndroidUtilities.dp(400.0F);
          }
        }
        else
        {
          i = j;
          if (PhotoViewer.this.captionEditText.isPopupView(localView)) {
            if (AndroidUtilities.isInMultiwindow) {
              i = PhotoViewer.this.captionEditText.getTop() - localView.getMeasuredHeight() + AndroidUtilities.dp(1.0F);
            } else {
              i = PhotoViewer.this.captionEditText.getBottom();
            }
          }
        }
      }
      label639:
      notifyHeightChanged();
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int j = View.MeasureSpec.getSize(paramInt1);
      int k = View.MeasureSpec.getSize(paramInt2);
      setMeasuredDimension(j, k);
      measureChildWithMargins(PhotoViewer.this.captionEditText, paramInt1, 0, paramInt2, 0);
      int m = PhotoViewer.this.captionEditText.getMeasuredHeight();
      int n = getChildCount();
      int i = 0;
      if (i < n)
      {
        View localView = getChildAt(i);
        if ((localView.getVisibility() == 8) || (localView == PhotoViewer.this.captionEditText)) {}
        for (;;)
        {
          i += 1;
          break;
          if (PhotoViewer.this.captionEditText.isPopupView(localView))
          {
            if (AndroidUtilities.isInMultiwindow)
            {
              if (AndroidUtilities.isTablet()) {
                localView.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0F), k - m - AndroidUtilities.statusBarHeight), 1073741824));
              } else {
                localView.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824), View.MeasureSpec.makeMeasureSpec(k - m - AndroidUtilities.statusBarHeight, 1073741824));
              }
            }
            else {
              localView.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824), View.MeasureSpec.makeMeasureSpec(localView.getLayoutParams().height, 1073741824));
            }
          }
          else {
            measureChildWithMargins(localView, paramInt1, 0, paramInt2, 0);
          }
        }
      }
    }
  }
  
  public static abstract interface PhotoViewerProvider
  {
    public abstract boolean allowCaption();
    
    public abstract boolean cancelButtonPressed();
    
    public abstract PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt);
    
    public abstract int getSelectedCount();
    
    public abstract Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt);
    
    public abstract boolean isPhotoChecked(int paramInt);
    
    public abstract void sendButtonPressed(int paramInt);
    
    public abstract void setPhotoChecked(int paramInt);
    
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
    public View parentView;
    public int radius;
    public float scale = 1.0F;
    public int size;
    public Bitmap thumb;
    public int viewX;
    public int viewY;
  }
  
  private class RadialProgressView
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
    
    public RadialProgressView(Context paramContext, View paramView)
    {
      if (PhotoViewer.decelerateInterpolator == null)
      {
        PhotoViewer.access$802(new DecelerateInterpolator(1.5F));
        PhotoViewer.access$902(new Paint(1));
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
      this.lastUpdateTime = l1;
      float f;
      if (this.animatedProgressValue != 1.0F)
      {
        this.radOffset += (float)(360L * l2) / 3000.0F;
        f = this.currentProgress - this.animationProgressStart;
        if (f > 0.0F)
        {
          this.currentProgressTime += l2;
          if (this.currentProgressTime < 300L) {
            break label172;
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
          this.animatedAlphaValue -= (float)l2 / 200.0F;
          if (this.animatedAlphaValue <= 0.0F)
          {
            this.animatedAlphaValue = 0.0F;
            this.previousBackgroundState = -2;
          }
          this.parent.invalidate();
        }
        return;
        label172:
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
        return;
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PhotoViewer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */