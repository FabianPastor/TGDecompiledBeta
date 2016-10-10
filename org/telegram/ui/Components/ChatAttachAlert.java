package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.CameraView.CameraViewDelegate;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface;
import org.telegram.ui.ActionBar.BottomSheet.ContainerView;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class ChatAttachAlert
  extends BottomSheet
  implements NotificationCenter.NotificationCenterDelegate, PhotoViewer.PhotoViewerProvider, BottomSheet.BottomSheetDelegateInterface
{
  private ListAdapter adapter;
  private int[] animateCameraValues = new int[5];
  private LinearLayoutManager attachPhotoLayoutManager;
  private RecyclerListView attachPhotoRecyclerView;
  private ViewGroup attachView;
  private ChatActivity baseFragment;
  private boolean cameraAnimationInProgress;
  private File cameraFile;
  private FrameLayout cameraIcon;
  private boolean cameraInitied;
  private float cameraOpenProgress;
  private boolean cameraOpened;
  private FrameLayout cameraPanel;
  private ArrayList<Object> cameraPhoto;
  private CameraView cameraView;
  private int[] cameraViewLocation = new int[2];
  private int cameraViewOffsetX;
  private int cameraViewOffsetY;
  private AnimatorSet currentHintAnimation;
  private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
  private ChatAttachViewDelegate delegate;
  private boolean deviceHasGoodCamera;
  private boolean flashAnimationInProgress;
  private ImageView[] flashModeButton = new ImageView[2];
  private Runnable hideHintRunnable;
  private boolean hintShowed;
  private TextView hintTextView;
  private boolean ignoreLayout;
  private ArrayList<InnerAnimator> innerAnimators = new ArrayList();
  private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5F);
  private float lastY;
  private LinearLayoutManager layoutManager;
  private View lineView;
  private RecyclerListView listView;
  private boolean loading = true;
  private boolean maybeStartDraging;
  private PhotoAttachAdapter photoAttachAdapter;
  private boolean pressed;
  private EmptyTextProgressView progressView;
  private TextView recordTime;
  private boolean revealAnimationInProgress;
  private float revealRadius;
  private int revealX;
  private int revealY;
  private int scrollOffsetY;
  private AttachButton sendPhotosButton;
  private Drawable shadowDrawable;
  private ShutterButton shutterButton;
  private SwitchCameraButton switchCameraButton;
  private boolean takingPhoto;
  private boolean useRevealAnimation;
  private Runnable videoRecordRunnable;
  private int videoRecordTime;
  private View[] views = new View[20];
  private ArrayList<Holder> viewsCache = new ArrayList(8);
  
  public ChatAttachAlert(Context paramContext, final ChatActivity paramChatActivity)
  {
    super(paramContext, false);
    this.baseFragment = paramChatActivity;
    setDelegate(this);
    setUseRevealAnimation(true);
    checkCamera(false);
    if (this.deviceHasGoodCamera) {
      CameraController.getInstance().initCamera();
    }
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.albumsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadInlineHints);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.cameraInitied);
    this.shadowDrawable = paramContext.getResources().getDrawable(2130837983);
    Object localObject1 = new RecyclerListView(paramContext)
    {
      private int lastHeight;
      private int lastWidth;
      
      public void onDraw(Canvas paramAnonymousCanvas)
      {
        if ((ChatAttachAlert.this.useRevealAnimation) && (Build.VERSION.SDK_INT <= 19))
        {
          paramAnonymousCanvas.save();
          paramAnonymousCanvas.clipRect(ChatAttachAlert.backgroundPaddingLeft, ChatAttachAlert.this.scrollOffsetY, getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft, getMeasuredHeight());
          if (ChatAttachAlert.this.revealAnimationInProgress) {
            paramAnonymousCanvas.drawCircle(ChatAttachAlert.this.revealX, ChatAttachAlert.this.revealY, ChatAttachAlert.this.revealRadius, ChatAttachAlert.this.ciclePaint);
          }
          for (;;)
          {
            paramAnonymousCanvas.restore();
            return;
            paramAnonymousCanvas.drawRect(ChatAttachAlert.backgroundPaddingLeft, ChatAttachAlert.this.scrollOffsetY, getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft, getMeasuredHeight(), ChatAttachAlert.this.ciclePaint);
          }
        }
        ChatAttachAlert.this.shadowDrawable.setBounds(0, ChatAttachAlert.this.scrollOffsetY - ChatAttachAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
        ChatAttachAlert.this.shadowDrawable.draw(paramAnonymousCanvas);
      }
      
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if (ChatAttachAlert.this.cameraAnimationInProgress) {
          return true;
        }
        if (ChatAttachAlert.this.cameraOpened) {
          return ChatAttachAlert.this.processTouchEvent(paramAnonymousMotionEvent);
        }
        if ((paramAnonymousMotionEvent.getAction() == 0) && (ChatAttachAlert.this.scrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < ChatAttachAlert.this.scrollOffsetY))
        {
          ChatAttachAlert.this.dismiss();
          return true;
        }
        return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
      }
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        int i2 = paramAnonymousInt4 - paramAnonymousInt2;
        int n = -1;
        int i1 = 0;
        int i3 = ChatAttachAlert.this.listView.getChildCount();
        int k = -1;
        int m = 0;
        int i = k;
        int j = m;
        if (i3 > 0)
        {
          View localView = ChatAttachAlert.this.listView.getChildAt(ChatAttachAlert.this.listView.getChildCount() - 1);
          ChatAttachAlert.Holder localHolder = (ChatAttachAlert.Holder)ChatAttachAlert.this.listView.findContainingViewHolder(localView);
          i = k;
          j = m;
          if (localHolder != null)
          {
            i = localHolder.getAdapterPosition();
            j = localView.getTop();
          }
        }
        m = n;
        k = i1;
        if (i >= 0)
        {
          m = n;
          k = i1;
          if (i2 - this.lastHeight != 0)
          {
            k = j + i2 - this.lastHeight - getPaddingTop();
            m = i;
          }
        }
        super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        if (m != -1)
        {
          ChatAttachAlert.access$902(ChatAttachAlert.this, true);
          ChatAttachAlert.this.layoutManager.scrollToPositionWithOffset(m, k);
          super.onLayout(false, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
          ChatAttachAlert.access$902(ChatAttachAlert.this, false);
        }
        this.lastHeight = i2;
        this.lastWidth = (paramAnonymousInt3 - paramAnonymousInt1);
        ChatAttachAlert.this.updateLayout();
        ChatAttachAlert.this.checkCameraViewPosition();
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousInt2 = View.MeasureSpec.getSize(paramAnonymousInt2);
        int j = paramAnonymousInt2;
        if (Build.VERSION.SDK_INT >= 21) {
          j = paramAnonymousInt2 - AndroidUtilities.statusBarHeight;
        }
        int i = ChatAttachAlert.backgroundPaddingTop;
        int k = AndroidUtilities.dp(294.0F);
        if (SearchQuery.inlineBots.isEmpty())
        {
          paramAnonymousInt2 = 0;
          k = k + i + paramAnonymousInt2;
          if (k != AndroidUtilities.dp(294.0F)) {
            break label187;
          }
        }
        label187:
        for (i = 0;; i = Math.max(0, j - AndroidUtilities.dp(294.0F)))
        {
          paramAnonymousInt2 = i;
          if (i != 0)
          {
            paramAnonymousInt2 = i;
            if (k < j) {
              paramAnonymousInt2 = i - (j - k);
            }
          }
          i = paramAnonymousInt2;
          if (paramAnonymousInt2 == 0) {
            i = ChatAttachAlert.backgroundPaddingTop;
          }
          if (getPaddingTop() != i)
          {
            ChatAttachAlert.access$902(ChatAttachAlert.this, true);
            setPadding(ChatAttachAlert.backgroundPaddingLeft, i, ChatAttachAlert.backgroundPaddingLeft, 0);
            ChatAttachAlert.access$902(ChatAttachAlert.this, false);
          }
          super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(Math.min(k, j), 1073741824));
          return;
          paramAnonymousInt2 = (int)Math.ceil(SearchQuery.inlineBots.size() / 4.0F) * AndroidUtilities.dp(100.0F) + AndroidUtilities.dp(12.0F);
          break;
        }
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if (ChatAttachAlert.this.cameraAnimationInProgress) {}
        do
        {
          return true;
          if (ChatAttachAlert.this.cameraOpened) {
            return ChatAttachAlert.this.processTouchEvent(paramAnonymousMotionEvent);
          }
        } while ((!ChatAttachAlert.this.isDismissed()) && (super.onTouchEvent(paramAnonymousMotionEvent)));
        return false;
      }
      
      public void requestLayout()
      {
        if (ChatAttachAlert.this.ignoreLayout) {
          return;
        }
        super.requestLayout();
      }
      
      public void setTranslationY(float paramAnonymousFloat)
      {
        super.setTranslationY(paramAnonymousFloat);
        ChatAttachAlert.this.checkCameraViewPosition();
      }
    };
    this.listView = ((RecyclerListView)localObject1);
    this.containerView = ((ViewGroup)localObject1);
    this.listView.setWillNotDraw(false);
    this.listView.setClipToPadding(false);
    localObject1 = this.listView;
    Object localObject2 = new LinearLayoutManager(getContext());
    this.layoutManager = ((LinearLayoutManager)localObject2);
    ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
    this.layoutManager.setOrientation(1);
    localObject1 = this.listView;
    localObject2 = new ListAdapter(paramContext);
    this.adapter = ((ListAdapter)localObject2);
    ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setEnabled(true);
    this.listView.setGlowColor(-657673);
    this.listView.addItemDecoration(new RecyclerView.ItemDecoration()
    {
      public void getItemOffsets(Rect paramAnonymousRect, View paramAnonymousView, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
      {
        paramAnonymousRect.left = 0;
        paramAnonymousRect.right = 0;
        paramAnonymousRect.top = 0;
        paramAnonymousRect.bottom = 0;
      }
    });
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (ChatAttachAlert.this.listView.getChildCount() <= 0) {
          return;
        }
        if ((ChatAttachAlert.this.hintShowed) && (ChatAttachAlert.this.layoutManager.findLastVisibleItemPosition() > 1))
        {
          ChatAttachAlert.this.hideHint();
          ChatAttachAlert.access$3502(ChatAttachAlert.this, false);
          ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("bothint", true).commit();
        }
        ChatAttachAlert.this.updateLayout();
        ChatAttachAlert.this.checkCameraViewPosition();
      }
    });
    this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
    this.attachView = new FrameLayout(paramContext)
    {
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        paramAnonymousInt1 = paramAnonymousInt3 - paramAnonymousInt1;
        paramAnonymousInt2 = paramAnonymousInt4 - paramAnonymousInt2;
        paramAnonymousInt3 = AndroidUtilities.dp(8.0F);
        ChatAttachAlert.this.attachPhotoRecyclerView.layout(0, paramAnonymousInt3, paramAnonymousInt1, ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() + paramAnonymousInt3);
        ChatAttachAlert.this.progressView.layout(0, paramAnonymousInt3, paramAnonymousInt1, ChatAttachAlert.this.progressView.getMeasuredHeight() + paramAnonymousInt3);
        ChatAttachAlert.this.lineView.layout(0, AndroidUtilities.dp(96.0F), paramAnonymousInt1, AndroidUtilities.dp(96.0F) + ChatAttachAlert.this.lineView.getMeasuredHeight());
        ChatAttachAlert.this.hintTextView.layout(paramAnonymousInt1 - ChatAttachAlert.this.hintTextView.getMeasuredWidth() - AndroidUtilities.dp(5.0F), paramAnonymousInt2 - ChatAttachAlert.this.hintTextView.getMeasuredHeight() - AndroidUtilities.dp(5.0F), paramAnonymousInt1 - AndroidUtilities.dp(5.0F), paramAnonymousInt2 - AndroidUtilities.dp(5.0F));
        paramAnonymousInt2 = (paramAnonymousInt1 - AndroidUtilities.dp(360.0F)) / 3;
        paramAnonymousInt1 = 0;
        while (paramAnonymousInt1 < 8)
        {
          paramAnonymousInt3 = AndroidUtilities.dp(paramAnonymousInt1 / 4 * 95 + 105);
          paramAnonymousInt4 = AndroidUtilities.dp(10.0F) + paramAnonymousInt1 % 4 * (AndroidUtilities.dp(85.0F) + paramAnonymousInt2);
          ChatAttachAlert.this.views[paramAnonymousInt1].layout(paramAnonymousInt4, paramAnonymousInt3, ChatAttachAlert.this.views[paramAnonymousInt1].getMeasuredWidth() + paramAnonymousInt4, ChatAttachAlert.this.views[paramAnonymousInt1].getMeasuredHeight() + paramAnonymousInt3);
          paramAnonymousInt1 += 1;
        }
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(294.0F), 1073741824));
      }
    };
    localObject1 = this.views;
    localObject2 = new RecyclerListView(paramContext);
    this.attachPhotoRecyclerView = ((RecyclerListView)localObject2);
    localObject1[8] = localObject2;
    this.attachPhotoRecyclerView.setVerticalScrollBarEnabled(true);
    localObject1 = this.attachPhotoRecyclerView;
    localObject2 = new PhotoAttachAdapter(paramContext);
    this.photoAttachAdapter = ((PhotoAttachAdapter)localObject2);
    ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
    this.attachPhotoRecyclerView.setClipToPadding(false);
    this.attachPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), 0);
    this.attachPhotoRecyclerView.setItemAnimator(null);
    this.attachPhotoRecyclerView.setLayoutAnimation(null);
    this.attachPhotoRecyclerView.setOverScrollMode(2);
    this.attachView.addView(this.attachPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0F));
    this.attachPhotoLayoutManager = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.attachPhotoLayoutManager.setOrientation(0);
    this.attachPhotoRecyclerView.setLayoutManager(this.attachPhotoLayoutManager);
    this.attachPhotoRecyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if ((ChatAttachAlert.this.baseFragment == null) || (ChatAttachAlert.this.baseFragment.getParentActivity() == null)) {}
        int i;
        do
        {
          do
          {
            return;
            if ((ChatAttachAlert.this.deviceHasGoodCamera) && (paramAnonymousInt == 0)) {
              break;
            }
            i = paramAnonymousInt;
            if (ChatAttachAlert.this.deviceHasGoodCamera) {
              i = paramAnonymousInt - 1;
            }
          } while (MediaController.allPhotosAlbumEntry == null);
          paramAnonymousView = MediaController.allPhotosAlbumEntry.photos;
        } while ((i < 0) || (i >= paramAnonymousView.size()));
        PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
        PhotoViewer.getInstance().openPhotoForSelect(paramAnonymousView, i, 0, ChatAttachAlert.this, ChatAttachAlert.this.baseFragment);
        AndroidUtilities.hideKeyboard(ChatAttachAlert.this.baseFragment.getFragmentView().findFocus());
        return;
        ChatAttachAlert.this.openCamera();
      }
    });
    this.attachPhotoRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        ChatAttachAlert.this.checkCameraViewPosition();
      }
    });
    localObject1 = this.views;
    localObject2 = new EmptyTextProgressView(paramContext);
    this.progressView = ((EmptyTextProgressView)localObject2);
    localObject1[9] = localObject2;
    if ((Build.VERSION.SDK_INT >= 23) && (getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
    {
      this.progressView.setText(LocaleController.getString("PermissionStorage", 2131166102));
      this.progressView.setTextSize(16);
    }
    for (;;)
    {
      this.attachView.addView(this.progressView, LayoutHelper.createFrame(-1, 80.0F));
      this.attachPhotoRecyclerView.setEmptyView(this.progressView);
      localObject1 = this.views;
      localObject2 = new View(getContext())
      {
        public boolean hasOverlappingRendering()
        {
          return false;
        }
      };
      this.lineView = ((View)localObject2);
      localObject1[10] = localObject2;
      this.lineView.setBackgroundColor(-2960686);
      this.attachView.addView(this.lineView, new FrameLayout.LayoutParams(-1, 1, 51));
      localObject1 = LocaleController.getString("ChatCamera", 2131165490);
      localObject2 = LocaleController.getString("ChatGallery", 2131165492);
      String str1 = LocaleController.getString("ChatVideo", 2131165497);
      String str2 = LocaleController.getString("AttachMusic", 2131165342);
      String str3 = LocaleController.getString("ChatDocument", 2131165491);
      String str4 = LocaleController.getString("AttachContact", 2131165337);
      String str5 = LocaleController.getString("ChatLocation", 2131165495);
      i = 0;
      while (i < 8)
      {
        AttachButton localAttachButton = new AttachButton(paramContext);
        localAttachButton.setTextAndIcon(new CharSequence[] { localObject1, localObject2, str1, str2, str3, str4, str5, "" }[i], org.telegram.ui.ActionBar.Theme.attachButtonDrawables[i]);
        this.attachView.addView(localAttachButton, LayoutHelper.createFrame(85, 90, 51));
        localAttachButton.setTag(Integer.valueOf(i));
        this.views[i] = localAttachButton;
        if (i == 7)
        {
          this.sendPhotosButton = localAttachButton;
          this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0F), 0, 0);
        }
        localAttachButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            ChatAttachAlert.this.delegate.didPressedButton(((Integer)paramAnonymousView.getTag()).intValue());
          }
        });
        i += 1;
      }
      this.progressView.setText(LocaleController.getString("NoPhotos", 2131165943));
      this.progressView.setTextSize(20);
    }
    this.hintTextView = new TextView(paramContext);
    this.hintTextView.setBackgroundResource(2130838022);
    this.hintTextView.setTextColor(-1);
    this.hintTextView.setTextSize(1, 14.0F);
    this.hintTextView.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
    this.hintTextView.setText(LocaleController.getString("AttachBotsHelp", 2131165336));
    this.hintTextView.setGravity(16);
    this.hintTextView.setVisibility(4);
    this.hintTextView.setCompoundDrawablesWithIntrinsicBounds(2130837967, 0, 0, 0);
    this.hintTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
    this.attachView.addView(this.hintTextView, LayoutHelper.createFrame(-2, 32.0F, 85, 5.0F, 0.0F, 5.0F, 5.0F));
    int i = 0;
    while (i < 8)
    {
      this.viewsCache.add(this.photoAttachAdapter.createHolder());
      i += 1;
    }
    if (this.loading) {
      this.progressView.showProgress();
    }
    while (Build.VERSION.SDK_INT >= 16)
    {
      this.recordTime = new TextView(paramContext);
      this.recordTime.setBackgroundResource(2130838000);
      this.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(1711276032, PorterDuff.Mode.MULTIPLY));
      this.recordTime.setText("00:00");
      this.recordTime.setTextSize(1, 14.0F);
      this.recordTime.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.recordTime.setAlpha(0.0F);
      this.recordTime.setTextColor(-1);
      this.recordTime.setPadding(AndroidUtilities.dp(6.0F), AndroidUtilities.dp(3.0F), AndroidUtilities.dp(6.0F), AndroidUtilities.dp(3.0F));
      this.container.addView(this.recordTime, LayoutHelper.createFrame(-2, -2.0F, 49, 0.0F, 8.0F, 0.0F, 0.0F));
      this.cameraPanel = new FrameLayout(paramContext)
      {
        protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          paramAnonymousInt1 = getMeasuredWidth() / 2;
          paramAnonymousInt2 = getMeasuredHeight() / 2;
          ChatAttachAlert.this.shutterButton.layout(paramAnonymousInt1 - ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2, paramAnonymousInt2 - ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2, ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2 + paramAnonymousInt1, ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2 + paramAnonymousInt2);
          if (getMeasuredWidth() == AndroidUtilities.dp(100.0F)) {
            paramAnonymousInt1 = getMeasuredWidth() / 2;
          }
          for (paramAnonymousInt2 = getMeasuredHeight() - AndroidUtilities.dp(20.0F) - ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2;; paramAnonymousInt2 = getMeasuredHeight() / 2)
          {
            ChatAttachAlert.this.switchCameraButton.layout(paramAnonymousInt1 - ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2, paramAnonymousInt2 - ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2, ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2 + paramAnonymousInt1, ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2 + paramAnonymousInt2);
            return;
            paramAnonymousInt1 = AndroidUtilities.dp(20.0F) + ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2;
          }
        }
      };
      this.cameraPanel.setVisibility(8);
      this.cameraPanel.setAlpha(0.0F);
      this.container.addView(this.cameraPanel, LayoutHelper.createFrame(-1, 100, 83));
      this.shutterButton = new ShutterButton(paramContext);
      this.cameraPanel.addView(this.shutterButton, LayoutHelper.createFrame(84, 84, 17));
      this.shutterButton.setDelegate(new ShutterButton.ShutterButtonDelegate()
      {
        public void shutterCancel()
        {
          ChatAttachAlert.this.cameraFile.delete();
          ChatAttachAlert.this.resetRecordState();
          CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), true);
        }
        
        public void shutterLongPressed()
        {
          if ((ChatAttachAlert.this.takingPhoto) || (ChatAttachAlert.this.baseFragment == null) || (ChatAttachAlert.this.baseFragment.getParentActivity() == null)) {
            return;
          }
          if ((Build.VERSION.SDK_INT >= 23) && (ChatAttachAlert.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") != 0))
          {
            ChatAttachAlert.this.baseFragment.getParentActivity().requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 21);
            return;
          }
          int i = 0;
          while (i < 2)
          {
            ChatAttachAlert.this.flashModeButton[i].setAlpha(0.0F);
            i += 1;
          }
          ChatAttachAlert.this.switchCameraButton.setAlpha(0.0F);
          ChatAttachAlert.access$4902(ChatAttachAlert.this, AndroidUtilities.generateVideoPath());
          ChatAttachAlert.this.recordTime.setAlpha(1.0F);
          ChatAttachAlert.this.recordTime.setText("00:00");
          ChatAttachAlert.access$5102(ChatAttachAlert.this, 0);
          ChatAttachAlert.access$5202(ChatAttachAlert.this, new Runnable()
          {
            public void run()
            {
              if (ChatAttachAlert.this.videoRecordRunnable == null) {
                return;
              }
              ChatAttachAlert.access$5108(ChatAttachAlert.this);
              ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[] { Integer.valueOf(ChatAttachAlert.this.videoRecordTime / 60), Integer.valueOf(ChatAttachAlert.this.videoRecordTime % 60) }));
              AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000L);
            }
          });
          AndroidUtilities.lockOrientation(paramChatActivity.getParentActivity());
          CameraController.getInstance().recordVideo(ChatAttachAlert.this.cameraView.getCameraSession(), ChatAttachAlert.this.cameraFile, new Runnable()
          {
            public void run()
            {
              if ((ChatAttachAlert.this.cameraFile == null) || (ChatAttachAlert.this.baseFragment == null)) {
                return;
              }
              PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
              ChatAttachAlert.access$5402(ChatAttachAlert.this, new ArrayList());
              ChatAttachAlert.this.cameraPhoto.add(new MediaController.PhotoEntry(0, 0, 0L, ChatAttachAlert.this.cameraFile.getAbsolutePath(), 0, true));
              PhotoViewer.getInstance().openPhotoForSelect(ChatAttachAlert.this.cameraPhoto, 0, 2, new PhotoViewer.EmptyPhotoViewerProvider()
              {
                @TargetApi(16)
                public boolean cancelButtonPressed()
                {
                  if ((ChatAttachAlert.this.cameraOpened) && (ChatAttachAlert.this.cameraView != null) && (ChatAttachAlert.this.cameraFile != null))
                  {
                    ChatAttachAlert.this.cameraFile.delete();
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        if ((ChatAttachAlert.this.cameraView != null) && (!ChatAttachAlert.this.isDismissed()) && (Build.VERSION.SDK_INT >= 21)) {
                          ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                        }
                      }
                    }, 1000L);
                    CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                    ChatAttachAlert.access$4902(ChatAttachAlert.this, null);
                  }
                  return true;
                }
                
                public void sendButtonPressed(int paramAnonymous3Int)
                {
                  if (ChatAttachAlert.this.cameraFile == null) {
                    return;
                  }
                  AndroidUtilities.addMediaToGallery(ChatAttachAlert.this.cameraFile.getAbsolutePath());
                  ChatAttachAlert.this.baseFragment.sendMedia((MediaController.PhotoEntry)ChatAttachAlert.this.cameraPhoto.get(0), PhotoViewer.getInstance().isMuteVideo());
                  ChatAttachAlert.this.closeCamera(false);
                  ChatAttachAlert.this.dismiss();
                  ChatAttachAlert.access$4902(ChatAttachAlert.this, null);
                }
              }, ChatAttachAlert.this.baseFragment);
            }
          });
          AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000L);
          ChatAttachAlert.this.shutterButton.setState(ShutterButton.State.RECORDING, true);
        }
        
        public void shutterReleased()
        {
          if (ChatAttachAlert.this.takingPhoto) {
            return;
          }
          if (ChatAttachAlert.this.shutterButton.getState() == ShutterButton.State.RECORDING)
          {
            ChatAttachAlert.this.resetRecordState();
            CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), false);
            ChatAttachAlert.this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
            return;
          }
          ChatAttachAlert.access$4902(ChatAttachAlert.this, AndroidUtilities.generatePicturePath());
          ChatAttachAlert.access$4702(ChatAttachAlert.this, CameraController.getInstance().takePicture(ChatAttachAlert.this.cameraFile, ChatAttachAlert.this.cameraView.getCameraSession(), new Runnable()
          {
            public void run()
            {
              ChatAttachAlert.access$4702(ChatAttachAlert.this, false);
              if ((ChatAttachAlert.this.cameraFile == null) || (ChatAttachAlert.this.baseFragment == null)) {
                return;
              }
              PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
              ChatAttachAlert.access$5402(ChatAttachAlert.this, new ArrayList());
              j = 0;
              for (;;)
              {
                try
                {
                  int k = new ExifInterface(ChatAttachAlert.this.cameraFile.getAbsolutePath()).getAttributeInt("Orientation", 1);
                  i = j;
                  switch (k)
                  {
                  default: 
                    i = j;
                  }
                }
                catch (Exception localException)
                {
                  FileLog.e("tmessages", localException);
                  int i = j;
                  continue;
                }
                ChatAttachAlert.this.cameraPhoto.add(new MediaController.PhotoEntry(0, 0, 0L, ChatAttachAlert.this.cameraFile.getAbsolutePath(), i, false));
                PhotoViewer.getInstance().openPhotoForSelect(ChatAttachAlert.this.cameraPhoto, 0, 2, new PhotoViewer.EmptyPhotoViewerProvider()
                {
                  @TargetApi(16)
                  public boolean cancelButtonPressed()
                  {
                    if ((ChatAttachAlert.this.cameraOpened) && (ChatAttachAlert.this.cameraView != null) && (ChatAttachAlert.this.cameraFile != null))
                    {
                      ChatAttachAlert.this.cameraFile.delete();
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          if ((ChatAttachAlert.this.cameraView != null) && (!ChatAttachAlert.this.isDismissed()) && (Build.VERSION.SDK_INT >= 21)) {
                            ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                          }
                        }
                      }, 1000L);
                      CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                      ChatAttachAlert.access$4902(ChatAttachAlert.this, null);
                    }
                    return true;
                  }
                  
                  public void sendButtonPressed(int paramAnonymous3Int)
                  {
                    if (ChatAttachAlert.this.cameraFile == null) {
                      return;
                    }
                    AndroidUtilities.addMediaToGallery(ChatAttachAlert.this.cameraFile.getAbsolutePath());
                    ChatAttachAlert.this.baseFragment.sendMedia((MediaController.PhotoEntry)ChatAttachAlert.this.cameraPhoto.get(0), false);
                    ChatAttachAlert.this.closeCamera(false);
                    ChatAttachAlert.this.dismiss();
                    ChatAttachAlert.access$4902(ChatAttachAlert.this, null);
                  }
                }, ChatAttachAlert.this.baseFragment);
                return;
                i = 90;
                continue;
                i = 180;
                continue;
                i = 270;
              }
            }
          }));
        }
      });
      this.switchCameraButton = new SwitchCameraButton(paramContext);
      this.cameraPanel.addView(this.switchCameraButton, LayoutHelper.createFrame(60, 60, 19));
      this.switchCameraButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          boolean bool = false;
          if ((ChatAttachAlert.this.takingPhoto) || (ChatAttachAlert.this.cameraView == null) || (!ChatAttachAlert.this.cameraView.isInitied())) {
            return;
          }
          ChatAttachAlert.access$5602(ChatAttachAlert.this, false);
          ChatAttachAlert.this.cameraView.switchCamera();
          paramAnonymousView = ChatAttachAlert.this.switchCameraButton;
          if (!ChatAttachAlert.this.switchCameraButton.isFront()) {
            bool = true;
          }
          paramAnonymousView.setFront(bool, true);
        }
      });
      i = 0;
      while (i < 2)
      {
        this.flashModeButton[i] = new ImageView(paramContext);
        this.flashModeButton[i].setScaleType(ImageView.ScaleType.CENTER);
        this.flashModeButton[i].setVisibility(4);
        this.container.addView(this.flashModeButton[i], LayoutHelper.createFrame(48, 48, 53));
        this.flashModeButton[i].setOnClickListener(new View.OnClickListener()
        {
          public void onClick(final View paramAnonymousView)
          {
            if ((ChatAttachAlert.this.flashAnimationInProgress) || (ChatAttachAlert.this.cameraView == null) || (!ChatAttachAlert.this.cameraView.isInitied()) || (!ChatAttachAlert.this.cameraOpened)) {}
            Object localObject2;
            do
            {
              return;
              localObject1 = ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode();
              localObject2 = ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode();
            } while (((String)localObject1).equals(localObject2));
            ChatAttachAlert.this.cameraView.getCameraSession().setCurrentFlashMode((String)localObject2);
            ChatAttachAlert.access$5702(ChatAttachAlert.this, true);
            if (ChatAttachAlert.this.flashModeButton[0] == paramAnonymousView) {}
            for (Object localObject1 = ChatAttachAlert.this.flashModeButton[1];; localObject1 = ChatAttachAlert.this.flashModeButton[0])
            {
              ((ImageView)localObject1).setVisibility(0);
              ChatAttachAlert.this.setCameraFlashModeIcon((ImageView)localObject1, (String)localObject2);
              localObject2 = new AnimatorSet();
              ((AnimatorSet)localObject2).playTogether(new Animator[] { ObjectAnimator.ofFloat(paramAnonymousView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(48.0F) }), ObjectAnimator.ofFloat(localObject1, "translationY", new float[] { -AndroidUtilities.dp(48.0F), 0.0F }), ObjectAnimator.ofFloat(paramAnonymousView, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(localObject1, "alpha", new float[] { 0.0F, 1.0F }) });
              ((AnimatorSet)localObject2).setDuration(200L);
              ((AnimatorSet)localObject2).addListener(new AnimatorListenerAdapterProxy()
              {
                public void onAnimationEnd(Animator paramAnonymous2Animator)
                {
                  ChatAttachAlert.access$5702(ChatAttachAlert.this, false);
                  paramAnonymousView.setVisibility(4);
                }
              });
              ((AnimatorSet)localObject2).start();
              return;
            }
          }
        });
        i += 1;
      }
      this.progressView.showTextView();
    }
  }
  
  private void applyCameraViewPosition()
  {
    if (this.cameraView != null)
    {
      if (!this.cameraOpened)
      {
        this.cameraView.setTranslationX(this.cameraViewLocation[0]);
        this.cameraView.setTranslationY(this.cameraViewLocation[1]);
      }
      this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
      this.cameraIcon.setTranslationY(this.cameraViewLocation[1]);
      int i = AndroidUtilities.dp(80.0F) - this.cameraViewOffsetX;
      int j = AndroidUtilities.dp(80.0F) - this.cameraViewOffsetY;
      if (!this.cameraOpened)
      {
        this.cameraView.setClipLeft(this.cameraViewOffsetX);
        this.cameraView.setClipTop(this.cameraViewOffsetY);
        localLayoutParams = (FrameLayout.LayoutParams)this.cameraView.getLayoutParams();
        if ((localLayoutParams.height != j) || (localLayoutParams.width != i))
        {
          localLayoutParams.width = i;
          localLayoutParams.height = j;
          this.cameraView.setLayoutParams(localLayoutParams);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (ChatAttachAlert.this.cameraView != null) {
                ChatAttachAlert.this.cameraView.setLayoutParams(localLayoutParams);
              }
            }
          });
        }
      }
      final FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.cameraIcon.getLayoutParams();
      if ((localLayoutParams.height != j) || (localLayoutParams.width != i))
      {
        localLayoutParams.width = i;
        localLayoutParams.height = j;
        this.cameraIcon.setLayoutParams(localLayoutParams);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (ChatAttachAlert.this.cameraIcon != null) {
              ChatAttachAlert.this.cameraIcon.setLayoutParams(localLayoutParams);
            }
          }
        });
      }
    }
  }
  
  private void checkCameraViewPosition()
  {
    if (!this.deviceHasGoodCamera) {
      return;
    }
    int j = this.attachPhotoRecyclerView.getChildCount();
    int i = 0;
    for (;;)
    {
      Object localObject;
      if (i < j)
      {
        localObject = this.attachPhotoRecyclerView.getChildAt(i);
        if (!(localObject instanceof PhotoAttachCameraCell)) {
          break label312;
        }
        if ((Build.VERSION.SDK_INT < 19) || (((View)localObject).isAttachedToWindow())) {}
      }
      else
      {
        this.cameraViewOffsetX = 0;
        this.cameraViewOffsetY = 0;
        this.cameraViewLocation[0] = AndroidUtilities.dp(-100.0F);
        this.cameraViewLocation[1] = 0;
        applyCameraViewPosition();
        return;
      }
      ((View)localObject).getLocationInWindow(this.cameraViewLocation);
      float f = this.listView.getX() + backgroundPaddingLeft;
      if (this.cameraViewLocation[0] < f)
      {
        this.cameraViewOffsetX = ((int)(f - this.cameraViewLocation[0]));
        if (this.cameraViewOffsetX >= AndroidUtilities.dp(80.0F))
        {
          this.cameraViewOffsetX = 0;
          this.cameraViewLocation[0] = AndroidUtilities.dp(-100.0F);
          this.cameraViewLocation[1] = 0;
          if ((Build.VERSION.SDK_INT < 21) || (this.cameraViewLocation[1] >= AndroidUtilities.statusBarHeight)) {
            break label304;
          }
          this.cameraViewOffsetY = (AndroidUtilities.statusBarHeight - this.cameraViewLocation[1]);
          if (this.cameraViewOffsetY < AndroidUtilities.dp(80.0F)) {
            break label282;
          }
          this.cameraViewOffsetY = 0;
          this.cameraViewLocation[0] = AndroidUtilities.dp(-100.0F);
          this.cameraViewLocation[1] = 0;
        }
      }
      for (;;)
      {
        applyCameraViewPosition();
        return;
        localObject = this.cameraViewLocation;
        localObject[0] += this.cameraViewOffsetX;
        break;
        this.cameraViewOffsetX = 0;
        break;
        label282:
        localObject = this.cameraViewLocation;
        localObject[1] += this.cameraViewOffsetY;
        continue;
        label304:
        this.cameraViewOffsetY = 0;
      }
      label312:
      i += 1;
    }
  }
  
  private PhotoAttachPhotoCell getCellForIndex(int paramInt)
  {
    if (MediaController.allPhotosAlbumEntry == null) {
      return null;
    }
    int j = this.attachPhotoRecyclerView.getChildCount();
    int i = 0;
    if (i < j)
    {
      Object localObject = this.attachPhotoRecyclerView.getChildAt(i);
      int k;
      if ((localObject instanceof PhotoAttachPhotoCell))
      {
        localObject = (PhotoAttachPhotoCell)localObject;
        k = ((Integer)((PhotoAttachPhotoCell)localObject).getImageView().getTag()).intValue();
        if ((k >= 0) && (k < MediaController.allPhotosAlbumEntry.photos.size())) {
          break label90;
        }
      }
      label90:
      while (k != paramInt)
      {
        i += 1;
        break;
      }
      return (PhotoAttachPhotoCell)localObject;
    }
    return null;
  }
  
  private void hideHint()
  {
    if (this.hideHintRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.hideHintRunnable);
      this.hideHintRunnable = null;
    }
    if (this.hintTextView == null) {
      return;
    }
    this.currentHintAnimation = new AnimatorSet();
    this.currentHintAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[] { 0.0F }) });
    this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
    this.currentHintAnimation.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation != null) && (ChatAttachAlert.this.currentHintAnimation.equals(paramAnonymousAnimator))) {
          ChatAttachAlert.access$5902(ChatAttachAlert.this, null);
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation == null) || (!ChatAttachAlert.this.currentHintAnimation.equals(paramAnonymousAnimator))) {}
        do
        {
          return;
          ChatAttachAlert.access$5902(ChatAttachAlert.this, null);
        } while (ChatAttachAlert.this.hintTextView == null);
        ChatAttachAlert.this.hintTextView.setVisibility(4);
      }
    });
    this.currentHintAnimation.setDuration(300L);
    this.currentHintAnimation.start();
  }
  
  private void onRevealAnimationEnd(boolean paramBoolean)
  {
    NotificationCenter.getInstance().setAnimationInProgress(false);
    this.revealAnimationInProgress = false;
    if ((paramBoolean) && (Build.VERSION.SDK_INT <= 19) && (MediaController.allPhotosAlbumEntry == null)) {
      MediaController.loadGalleryPhotosAlbums(0);
    }
    if (paramBoolean)
    {
      checkCamera(true);
      showHint();
    }
  }
  
  @TargetApi(16)
  private void openCamera()
  {
    if (this.cameraView == null) {
      return;
    }
    this.animateCameraValues[0] = 0;
    this.animateCameraValues[1] = (AndroidUtilities.dp(80.0F) - this.cameraViewOffsetX);
    this.animateCameraValues[2] = (AndroidUtilities.dp(80.0F) - this.cameraViewOffsetY);
    this.cameraAnimationInProgress = true;
    this.cameraPanel.setVisibility(0);
    this.cameraPanel.setTag(null);
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[] { 0.0F, 1.0F }));
    localArrayList.add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[] { 1.0F }));
    int i = 0;
    for (;;)
    {
      if (i < 2)
      {
        if (this.flashModeButton[i].getVisibility() == 0) {
          localArrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i], "alpha", new float[] { 1.0F }));
        }
      }
      else
      {
        AnimatorSet localAnimatorSet = new AnimatorSet();
        localAnimatorSet.playTogether(localArrayList);
        localAnimatorSet.setDuration(200L);
        localAnimatorSet.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            ChatAttachAlert.access$1002(ChatAttachAlert.this, false);
          }
        });
        localAnimatorSet.start();
        if (Build.VERSION.SDK_INT >= 21) {
          this.cameraView.setSystemUiVisibility(1028);
        }
        this.cameraOpened = true;
        return;
      }
      i += 1;
    }
  }
  
  private boolean processTouchEvent(MotionEvent paramMotionEvent)
  {
    if (((!this.pressed) && (paramMotionEvent.getActionMasked() == 0)) || (paramMotionEvent.getActionMasked() == 5)) {
      if (!this.takingPhoto)
      {
        this.pressed = true;
        this.maybeStartDraging = true;
        this.lastY = paramMotionEvent.getY();
      }
    }
    label241:
    do
    {
      do
      {
        float f1;
        float f2;
        do
        {
          do
          {
            return true;
          } while (!this.pressed);
          if (paramMotionEvent.getActionMasked() != 2) {
            break label241;
          }
          f1 = paramMotionEvent.getY();
          f2 = f1 - this.lastY;
          if (!this.maybeStartDraging) {
            break;
          }
        } while (Math.abs(f2) <= AndroidUtilities.getPixelsInCM(0.4F, false));
        this.maybeStartDraging = false;
        return true;
        this.cameraView.setTranslationY(this.cameraView.getTranslationY() + f2);
        this.lastY = f1;
      } while (this.cameraPanel.getTag() != null);
      this.cameraPanel.setTag(Integer.valueOf(1));
      paramMotionEvent = new AnimatorSet();
      paramMotionEvent.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[] { 0.0F }) });
      paramMotionEvent.setDuration(200L);
      paramMotionEvent.start();
      return true;
    } while ((paramMotionEvent.getActionMasked() != 3) && (paramMotionEvent.getActionMasked() != 1) && (paramMotionEvent.getActionMasked() != 6));
    this.pressed = false;
    if (Math.abs(this.cameraView.getTranslationY()) > this.cameraView.getMeasuredHeight() / 6.0F)
    {
      closeCamera(true);
      return true;
    }
    paramMotionEvent = new AnimatorSet();
    paramMotionEvent.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.cameraView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[] { 1.0F }) });
    paramMotionEvent.setDuration(250L);
    paramMotionEvent.setInterpolator(this.interpolator);
    paramMotionEvent.start();
    this.cameraPanel.setTag(null);
    return true;
  }
  
  private void resetRecordState()
  {
    if (this.baseFragment == null) {
      return;
    }
    int i = 0;
    while (i < 2)
    {
      this.flashModeButton[i].setAlpha(1.0F);
      i += 1;
    }
    this.switchCameraButton.setAlpha(1.0F);
    this.recordTime.setAlpha(0.0F);
    AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
    this.videoRecordRunnable = null;
    AndroidUtilities.unlockOrientation(this.baseFragment.getParentActivity());
  }
  
  private void setCameraFlashModeIcon(ImageView paramImageView, String paramString)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        return;
        if (paramString.equals("off"))
        {
          i = 0;
          continue;
          if (paramString.equals("on"))
          {
            i = 1;
            continue;
            if (paramString.equals("auto")) {
              i = 2;
            }
          }
        }
        break;
      }
    }
    paramImageView.setImageResource(2130837731);
    return;
    paramImageView.setImageResource(2130837732);
    return;
    paramImageView.setImageResource(2130837730);
  }
  
  private void setUseRevealAnimation(boolean paramBoolean)
  {
    if ((!paramBoolean) || ((paramBoolean) && (Build.VERSION.SDK_INT >= 18) && (!AndroidUtilities.isTablet()))) {
      this.useRevealAnimation = paramBoolean;
    }
  }
  
  private void showHint()
  {
    if (SearchQuery.inlineBots.isEmpty()) {}
    while (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("bothint", false)) {
      return;
    }
    this.hintShowed = true;
    this.hintTextView.setVisibility(0);
    this.currentHintAnimation = new AnimatorSet();
    this.currentHintAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[] { 0.0F, 1.0F }) });
    this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
    this.currentHintAnimation.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation != null) && (ChatAttachAlert.this.currentHintAnimation.equals(paramAnonymousAnimator))) {
          ChatAttachAlert.access$5902(ChatAttachAlert.this, null);
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation == null) || (!ChatAttachAlert.this.currentHintAnimation.equals(paramAnonymousAnimator))) {
          return;
        }
        ChatAttachAlert.access$5902(ChatAttachAlert.this, null);
        AndroidUtilities.runOnUIThread(ChatAttachAlert.access$6202(ChatAttachAlert.this, new Runnable()
        {
          public void run()
          {
            if (ChatAttachAlert.this.hideHintRunnable != this) {
              return;
            }
            ChatAttachAlert.access$6202(ChatAttachAlert.this, null);
            ChatAttachAlert.this.hideHint();
          }
        }), 2000L);
      }
    });
    this.currentHintAnimation.setDuration(300L);
    this.currentHintAnimation.start();
  }
  
  @SuppressLint({"NewApi"})
  private void startRevealAnimation(final boolean paramBoolean)
  {
    this.containerView.setTranslationY(0.0F);
    final AnimatorSet localAnimatorSet = new AnimatorSet();
    final Object localObject1 = this.delegate.getRevealView();
    Object localObject2;
    float f1;
    if ((((View)localObject1).getVisibility() == 0) && (((ViewGroup)((View)localObject1).getParent()).getVisibility() == 0))
    {
      localObject2 = new int[2];
      ((View)localObject1).getLocationInWindow((int[])localObject2);
      if (Build.VERSION.SDK_INT <= 19)
      {
        f1 = AndroidUtilities.displaySize.y - this.containerView.getMeasuredHeight() - AndroidUtilities.statusBarHeight;
        this.revealX = (localObject2[0] + ((View)localObject1).getMeasuredWidth() / 2);
        this.revealY = ((int)(localObject2[1] + ((View)localObject1).getMeasuredHeight() / 2 - f1));
        if (Build.VERSION.SDK_INT > 19) {}
      }
    }
    int i;
    int k;
    int j;
    for (this.revealY -= AndroidUtilities.statusBarHeight;; this.revealY = ((int)(AndroidUtilities.displaySize.y - this.containerView.getY())))
    {
      localObject1 = new int[4][];
      localObject1[0] = { 0, 0 };
      localObject1[1] = { 0, AndroidUtilities.dp(304.0F) };
      localObject1[2] = { this.containerView.getMeasuredWidth(), 0 };
      localObject1[3] = { this.containerView.getMeasuredWidth(), AndroidUtilities.dp(304.0F) };
      i = 0;
      k = this.revealY - this.scrollOffsetY + backgroundPaddingTop;
      j = 0;
      while (j < 4)
      {
        i = Math.max(i, (int)Math.ceil(Math.sqrt((this.revealX - localObject1[j][0]) * (this.revealX - localObject1[j][0]) + (k - localObject1[j][1]) * (k - localObject1[j][1]))));
        j += 1;
      }
      f1 = this.containerView.getY();
      break;
      this.revealX = (AndroidUtilities.displaySize.x / 2 + backgroundPaddingLeft);
    }
    label418:
    float f2;
    if (this.revealX <= this.containerView.getMeasuredWidth())
    {
      j = this.revealX;
      localObject1 = new ArrayList(3);
      if (!paramBoolean) {
        break label1200;
      }
      f1 = 0.0F;
      if (!paramBoolean) {
        break label1207;
      }
      f2 = i;
      label426:
      ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this, "revealRadius", new float[] { f1, f2 }));
      localObject2 = this.backDrawable;
      if (!paramBoolean) {
        break label1212;
      }
      k = 51;
      label464:
      ((ArrayList)localObject1).add(ObjectAnimator.ofInt(localObject2, "alpha", new int[] { k }));
      if (Build.VERSION.SDK_INT < 21) {
        break label1243;
      }
    }
    for (;;)
    {
      try
      {
        localObject2 = this.containerView;
        k = this.revealY;
        if (!paramBoolean) {
          continue;
        }
        f1 = 0.0F;
      }
      catch (Exception localException)
      {
        int m;
        float f3;
        ArrayList localArrayList;
        label1200:
        label1207:
        label1212:
        FileLog.e("tmessages", localException);
        continue;
      }
      ((ArrayList)localObject1).add(ViewAnimationUtils.createCircularReveal((View)localObject2, j, k, f1, f2));
      localAnimatorSet.setDuration(320L);
      localAnimatorSet.playTogether((Collection)localObject1);
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((ChatAttachAlert.this.currentSheetAnimation != null) && (localAnimatorSet.equals(paramAnonymousAnimator))) {
            ChatAttachAlert.access$7602(ChatAttachAlert.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((ChatAttachAlert.this.currentSheetAnimation != null) && (ChatAttachAlert.this.currentSheetAnimation.equals(paramAnonymousAnimator)))
          {
            ChatAttachAlert.access$7102(ChatAttachAlert.this, null);
            ChatAttachAlert.this.onRevealAnimationEnd(paramBoolean);
            ChatAttachAlert.this.containerView.invalidate();
            ChatAttachAlert.this.containerView.setLayerType(0, null);
            if (paramBoolean) {}
          }
          try
          {
            ChatAttachAlert.this.dismissInternal();
            return;
          }
          catch (Exception paramAnonymousAnimator)
          {
            FileLog.e("tmessages", paramAnonymousAnimator);
          }
        }
      });
      if (paramBoolean)
      {
        this.innerAnimators.clear();
        NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.dialogsNeedReload });
        NotificationCenter.getInstance().setAnimationInProgress(true);
        this.revealAnimationInProgress = true;
        if (Build.VERSION.SDK_INT <= 19)
        {
          i = 11;
          j = 0;
          if (j >= i) {
            break label1478;
          }
          if (Build.VERSION.SDK_INT > 19) {
            break label1443;
          }
          if (j < 8)
          {
            this.views[j].setScaleX(0.1F);
            this.views[j].setScaleY(0.1F);
          }
          this.views[j].setAlpha(0.0F);
          localObject2 = new InnerAnimator(null);
          k = this.views[j].getLeft() + this.views[j].getMeasuredWidth() / 2;
          m = this.views[j].getTop() + this.attachView.getTop() + this.views[j].getMeasuredHeight() / 2;
          f1 = (float)Math.sqrt((this.revealX - k) * (this.revealX - k) + (this.revealY - m) * (this.revealY - m));
          f2 = (this.revealX - k) / f1;
          f3 = (this.revealY - m) / f1;
          this.views[j].setPivotX(this.views[j].getMeasuredWidth() / 2 + AndroidUtilities.dp(20.0F) * f2);
          this.views[j].setPivotY(this.views[j].getMeasuredHeight() / 2 + AndroidUtilities.dp(20.0F) * f3);
          InnerAnimator.access$6702((InnerAnimator)localObject2, f1 - AndroidUtilities.dp(81.0F));
          this.views[j].setTag(2131165299, Integer.valueOf(1));
          localArrayList = new ArrayList();
          if (j >= 8) {
            break label1472;
          }
          localArrayList.add(ObjectAnimator.ofFloat(this.views[j], "scaleX", new float[] { 0.7F, 1.05F }));
          localArrayList.add(ObjectAnimator.ofFloat(this.views[j], "scaleY", new float[] { 0.7F, 1.05F }));
          localObject1 = new AnimatorSet();
          ((AnimatorSet)localObject1).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.views[j], "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.views[j], "scaleY", new float[] { 1.0F }) });
          ((AnimatorSet)localObject1).setDuration(100L);
          ((AnimatorSet)localObject1).setInterpolator(this.decelerateInterpolator);
          if (Build.VERSION.SDK_INT <= 19) {
            localArrayList.add(ObjectAnimator.ofFloat(this.views[j], "alpha", new float[] { 1.0F }));
          }
          InnerAnimator.access$6802((InnerAnimator)localObject2, new AnimatorSet());
          ((InnerAnimator)localObject2).animatorSet.playTogether(localArrayList);
          ((InnerAnimator)localObject2).animatorSet.setDuration(150L);
          ((InnerAnimator)localObject2).animatorSet.setInterpolator(this.decelerateInterpolator);
          ((InnerAnimator)localObject2).animatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if (localObject1 != null) {
                localObject1.start();
              }
            }
          });
          this.innerAnimators.add(localObject2);
          j += 1;
          continue;
          j = this.containerView.getMeasuredWidth();
          break;
          f1 = i;
          break label418;
          f2 = 0.0F;
          break label426;
          k = 0;
          break label464;
          f1 = i;
          break label1490;
          f2 = 0.0F;
          continue;
          label1243:
          if (!paramBoolean)
          {
            localAnimatorSet.setDuration(200L);
            ViewGroup localViewGroup = this.containerView;
            if (this.revealX <= this.containerView.getMeasuredWidth()) {}
            for (f1 = this.revealX;; f1 = this.containerView.getMeasuredWidth())
            {
              localViewGroup.setPivotX(f1);
              this.containerView.setPivotY(this.revealY);
              ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.containerView, "scaleX", new float[] { 0.0F }));
              ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.containerView, "scaleY", new float[] { 0.0F }));
              ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }));
              break;
            }
          }
          localAnimatorSet.setDuration(250L);
          this.containerView.setScaleX(1.0F);
          this.containerView.setScaleY(1.0F);
          this.containerView.setAlpha(1.0F);
          if (Build.VERSION.SDK_INT > 19) {
            continue;
          }
          localAnimatorSet.setStartDelay(20L);
          continue;
        }
        i = 8;
        continue;
        label1443:
        this.views[j].setScaleX(0.7F);
        this.views[j].setScaleY(0.7F);
        continue;
        label1472:
        localObject1 = null;
        continue;
      }
      label1478:
      this.currentSheetAnimation = localAnimatorSet;
      localAnimatorSet.start();
      return;
      label1490:
      if (paramBoolean) {
        f2 = i;
      }
    }
  }
  
  @SuppressLint({"NewApi"})
  private void updateLayout()
  {
    int i;
    if (this.listView.getChildCount() <= 0)
    {
      localObject = this.listView;
      i = this.listView.getPaddingTop();
      this.scrollOffsetY = i;
      ((RecyclerListView)localObject).setTopGlowOffset(i);
      this.listView.invalidate();
    }
    do
    {
      return;
      localObject = this.listView.getChildAt(0);
      Holder localHolder = (Holder)this.listView.findContainingViewHolder((View)localObject);
      int j = ((View)localObject).getTop();
      int k = 0;
      i = k;
      if (j >= 0)
      {
        i = k;
        if (localHolder != null)
        {
          i = k;
          if (localHolder.getAdapterPosition() == 0) {
            i = j;
          }
        }
      }
    } while (this.scrollOffsetY == i);
    Object localObject = this.listView;
    this.scrollOffsetY = i;
    ((RecyclerListView)localObject).setTopGlowOffset(i);
    this.listView.invalidate();
  }
  
  public boolean allowCaption()
  {
    return true;
  }
  
  protected boolean canDismissWithSwipe()
  {
    return false;
  }
  
  protected boolean canDismissWithTouchOutside()
  {
    return !this.cameraOpened;
  }
  
  public boolean cancelButtonPressed()
  {
    return false;
  }
  
  public void checkCamera(boolean paramBoolean)
  {
    if (this.baseFragment == null) {
      return;
    }
    boolean bool = this.deviceHasGoodCamera;
    if (Build.VERSION.SDK_INT >= 23) {
      if (this.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0)
      {
        if (paramBoolean) {
          this.baseFragment.getParentActivity().requestPermissions(new String[] { "android.permission.CAMERA" }, 17);
        }
        this.deviceHasGoodCamera = false;
      }
    }
    for (;;)
    {
      if ((bool != this.deviceHasGoodCamera) && (this.photoAttachAdapter != null)) {
        this.photoAttachAdapter.notifyDataSetChanged();
      }
      if ((!isShowing()) || (!this.deviceHasGoodCamera) || (this.baseFragment == null) || (this.revealAnimationInProgress)) {
        break;
      }
      showCamera();
      return;
      CameraController.getInstance().initCamera();
      this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
      continue;
      if (Build.VERSION.SDK_INT >= 16)
      {
        CameraController.getInstance().initCamera();
        this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
      }
    }
  }
  
  @TargetApi(16)
  public void closeCamera(boolean paramBoolean)
  {
    if ((this.takingPhoto) || (this.cameraView == null)) {
      return;
    }
    this.animateCameraValues[1] = (AndroidUtilities.dp(80.0F) - this.cameraViewOffsetX);
    this.animateCameraValues[2] = (AndroidUtilities.dp(80.0F) - this.cameraViewOffsetY);
    if (paramBoolean)
    {
      Object localObject1 = (FrameLayout.LayoutParams)this.cameraView.getLayoutParams();
      Object localObject2 = this.animateCameraValues;
      i = (int)this.cameraView.getTranslationY();
      ((FrameLayout.LayoutParams)localObject1).topMargin = i;
      localObject2[0] = i;
      this.cameraView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.cameraView.setTranslationY(0.0F);
      this.cameraAnimationInProgress = true;
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[] { 0.0F }));
      ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[] { 0.0F }));
      i = 0;
      for (;;)
      {
        if (i < 2)
        {
          if (this.flashModeButton[i].getVisibility() == 0) {
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.flashModeButton[i], "alpha", new float[] { 0.0F }));
          }
        }
        else
        {
          localObject2 = new AnimatorSet();
          ((AnimatorSet)localObject2).playTogether((Collection)localObject1);
          ((AnimatorSet)localObject2).setDuration(200L);
          ((AnimatorSet)localObject2).addListener(new AnimatorListenerAdapterProxy()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              ChatAttachAlert.access$1002(ChatAttachAlert.this, false);
              ChatAttachAlert.this.cameraPanel.setVisibility(8);
              ChatAttachAlert.access$1102(ChatAttachAlert.this, false);
              if (Build.VERSION.SDK_INT >= 21) {
                ChatAttachAlert.this.cameraView.setSystemUiVisibility(1024);
              }
            }
          });
          ((AnimatorSet)localObject2).start();
          return;
        }
        i += 1;
      }
    }
    this.animateCameraValues[0] = 0;
    setCameraOpenProgress(0.0F);
    this.cameraPanel.setAlpha(0.0F);
    this.cameraPanel.setVisibility(8);
    int i = 0;
    for (;;)
    {
      if (i < 2)
      {
        if (this.flashModeButton[i].getVisibility() == 0) {
          this.flashModeButton[i].setAlpha(0.0F);
        }
      }
      else
      {
        this.cameraOpened = false;
        if (Build.VERSION.SDK_INT < 21) {
          break;
        }
        this.cameraView.setSystemUiVisibility(1024);
        return;
      }
      i += 1;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.albumsDidLoaded) {
      if (this.photoAttachAdapter != null)
      {
        this.loading = false;
        this.progressView.showTextView();
        this.photoAttachAdapter.notifyDataSetChanged();
      }
    }
    do
    {
      do
      {
        return;
        if (paramInt != NotificationCenter.reloadInlineHints) {
          break;
        }
      } while (this.adapter == null);
      this.adapter.notifyDataSetChanged();
      return;
    } while (paramInt != NotificationCenter.cameraInitied);
    checkCamera(false);
  }
  
  public void dismiss()
  {
    if (this.cameraAnimationInProgress) {
      return;
    }
    if (this.cameraOpened)
    {
      closeCamera(true);
      return;
    }
    hideCamera(true);
    super.dismiss();
  }
  
  public void dismissInternal()
  {
    if (this.containerView != null) {
      this.containerView.setVisibility(4);
    }
    super.dismissInternal();
  }
  
  public void dismissWithButtonClick(int paramInt)
  {
    super.dismissWithButtonClick(paramInt);
    if ((paramInt != 0) && (paramInt != 2)) {}
    for (boolean bool = true;; bool = false)
    {
      hideCamera(bool);
      return;
    }
  }
  
  public float getCameraOpenProgress()
  {
    return this.cameraOpenProgress;
  }
  
  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    int i = 0;
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null)
    {
      paramFileLocation = new int[2];
      paramMessageObject.getImageView().getLocationInWindow(paramFileLocation);
      PhotoViewer.PlaceProviderObject localPlaceProviderObject = new PhotoViewer.PlaceProviderObject();
      localPlaceProviderObject.viewX = paramFileLocation[0];
      int j = paramFileLocation[1];
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramInt = AndroidUtilities.statusBarHeight;
        localPlaceProviderObject.viewY = (j - paramInt);
        localPlaceProviderObject.parentView = this.attachPhotoRecyclerView;
        localPlaceProviderObject.imageReceiver = paramMessageObject.getImageView().getImageReceiver();
        localPlaceProviderObject.thumb = localPlaceProviderObject.imageReceiver.getBitmap();
        localPlaceProviderObject.scale = paramMessageObject.getImageView().getScaleX();
        if (Build.VERSION.SDK_INT < 21) {
          break label148;
        }
      }
      label148:
      for (paramInt = i;; paramInt = -AndroidUtilities.statusBarHeight)
      {
        localPlaceProviderObject.clipBottomAddition = paramInt;
        paramMessageObject.getCheckBox().setVisibility(8);
        return localPlaceProviderObject;
        paramInt = 0;
        break;
      }
    }
    return null;
  }
  
  protected float getRevealRadius()
  {
    return this.revealRadius;
  }
  
  public int getSelectedCount()
  {
    return this.photoAttachAdapter.getSelectedPhotos().size();
  }
  
  public HashMap<Integer, MediaController.PhotoEntry> getSelectedPhotos()
  {
    return this.photoAttachAdapter.getSelectedPhotos();
  }
  
  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null) {
      return paramMessageObject.getImageView().getImageReceiver().getBitmap();
    }
    return null;
  }
  
  public void hideCamera(boolean paramBoolean)
  {
    if ((!this.deviceHasGoodCamera) || (this.cameraView == null)) {}
    for (;;)
    {
      return;
      this.cameraView.destroy(paramBoolean);
      this.container.removeView(this.cameraView);
      this.container.removeView(this.cameraIcon);
      this.cameraView = null;
      this.cameraIcon = null;
      int j = this.attachPhotoRecyclerView.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = this.attachPhotoRecyclerView.getChildAt(i);
        if ((localView instanceof PhotoAttachCameraCell))
        {
          localView.setVisibility(0);
          return;
        }
        i += 1;
      }
    }
  }
  
  public void init()
  {
    if (MediaController.allPhotosAlbumEntry != null)
    {
      int i = 0;
      while (i < Math.min(100, MediaController.allPhotosAlbumEntry.photos.size()))
      {
        MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(i);
        localPhotoEntry.caption = null;
        localPhotoEntry.imagePath = null;
        localPhotoEntry.thumbPath = null;
        localPhotoEntry.stickers.clear();
        i += 1;
      }
    }
    if (this.currentHintAnimation != null)
    {
      this.currentHintAnimation.cancel();
      this.currentHintAnimation = null;
    }
    this.hintTextView.setAlpha(0.0F);
    this.hintTextView.setVisibility(4);
    this.attachPhotoLayoutManager.scrollToPositionWithOffset(0, 1000000);
    this.photoAttachAdapter.clearSelectedPhotos();
    this.layoutManager.scrollToPositionWithOffset(0, 1000000);
    updatePhotosButton();
  }
  
  public boolean isPhotoChecked(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < MediaController.allPhotosAlbumEntry.photos.size()) && (this.photoAttachAdapter.getSelectedPhotos().containsKey(Integer.valueOf(((MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt)).imageId)));
  }
  
  public void loadGalleryPhotos()
  {
    if ((MediaController.allPhotosAlbumEntry == null) && (Build.VERSION.SDK_INT >= 21)) {
      MediaController.loadGalleryPhotosAlbums(0);
    }
  }
  
  protected boolean onContainerTouchEvent(MotionEvent paramMotionEvent)
  {
    return (this.cameraOpened) && (processTouchEvent(paramMotionEvent));
  }
  
  protected boolean onCustomCloseAnimation()
  {
    boolean bool = false;
    if (this.useRevealAnimation)
    {
      this.backDrawable.setAlpha(51);
      startRevealAnimation(false);
      bool = true;
    }
    return bool;
  }
  
  protected boolean onCustomLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt3 - paramInt1;
    paramInt2 = paramInt4 - paramInt2;
    if (i < paramInt2) {
      paramInt1 = 1;
    }
    while (paramView == this.cameraPanel) {
      if (paramInt1 != 0)
      {
        this.cameraPanel.layout(0, paramInt4 - AndroidUtilities.dp(100.0F), i, paramInt4);
        return true;
        paramInt1 = 0;
      }
      else
      {
        this.cameraPanel.layout(paramInt3 - AndroidUtilities.dp(100.0F), 0, paramInt3, paramInt2);
        return true;
      }
    }
    if ((paramView == this.flashModeButton[0]) || (paramView == this.flashModeButton[1]))
    {
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramInt2 = AndroidUtilities.dp(10.0F);
        if (Build.VERSION.SDK_INT < 21) {
          break label169;
        }
      }
      label169:
      for (paramInt4 = AndroidUtilities.dp(8.0F);; paramInt4 = 0)
      {
        if (paramInt1 == 0) {
          break label175;
        }
        paramView.layout(paramInt3 - paramView.getMeasuredWidth() - paramInt4, paramInt2, paramInt3 - paramInt4, paramView.getMeasuredHeight() + paramInt2);
        return true;
        paramInt2 = 0;
        break;
      }
      label175:
      paramView.layout(paramInt4, paramInt2, paramView.getMeasuredWidth() + paramInt4, paramView.getMeasuredHeight() + paramInt2);
      return true;
    }
    return false;
  }
  
  protected boolean onCustomMeasure(View paramView, int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2) {}
    for (int i = 1; paramView == this.cameraView; i = 0)
    {
      if ((!this.cameraOpened) || (this.cameraAnimationInProgress)) {
        break label128;
      }
      this.cameraView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
      return true;
    }
    if (paramView == this.cameraPanel)
    {
      if (i != 0)
      {
        this.cameraPanel.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824));
        return true;
      }
      this.cameraPanel.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
      return true;
    }
    label128:
    return false;
  }
  
  protected boolean onCustomOpenAnimation()
  {
    if (this.useRevealAnimation)
    {
      startRevealAnimation(true);
      return true;
    }
    return false;
  }
  
  public void onDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.albumsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadInlineHints);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.cameraInitied);
    this.baseFragment = null;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((this.cameraOpened) && ((paramInt == 24) || (paramInt == 25)))
    {
      this.shutterButton.delegate.shutterReleased();
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public void onOpenAnimationEnd()
  {
    onRevealAnimationEnd(true);
  }
  
  public void onOpenAnimationStart() {}
  
  public void onPause()
  {
    if ((!this.cameraOpened) || (this.shutterButton.getState() != ShutterButton.State.RECORDING)) {
      return;
    }
    resetRecordState();
    CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
    this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
  }
  
  public void sendButtonPressed(int paramInt)
  {
    if (this.photoAttachAdapter.getSelectedPhotos().isEmpty())
    {
      if ((paramInt < 0) || (paramInt >= MediaController.allPhotosAlbumEntry.photos.size())) {
        return;
      }
      MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt);
      this.photoAttachAdapter.getSelectedPhotos().put(Integer.valueOf(localPhotoEntry.imageId), localPhotoEntry);
    }
    this.delegate.didPressedButton(7);
  }
  
  public void setCameraOpenProgress(float paramFloat)
  {
    if (this.cameraView == null) {
      return;
    }
    this.cameraOpenProgress = paramFloat;
    float f3 = this.animateCameraValues[1];
    float f4 = this.animateCameraValues[2];
    int i;
    float f2;
    float f1;
    label72:
    label156:
    FrameLayout.LayoutParams localLayoutParams;
    if (AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y)
    {
      i = 1;
      if (i == 0) {
        break label327;
      }
      f2 = this.container.getWidth();
      f1 = this.container.getHeight();
      if (paramFloat != 0.0F) {
        break label348;
      }
      this.cameraView.setClipLeft(this.cameraViewOffsetX);
      this.cameraView.setClipTop(this.cameraViewOffsetY);
      this.cameraView.setTranslationX(this.cameraViewLocation[0]);
      this.cameraView.setTranslationY(this.cameraViewLocation[1]);
      this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
      this.cameraIcon.setTranslationY(this.cameraViewLocation[1]);
      localLayoutParams = (FrameLayout.LayoutParams)this.cameraView.getLayoutParams();
      localLayoutParams.width = ((int)((f2 - f3) * paramFloat + f3));
      localLayoutParams.height = ((int)((f1 - f4) * paramFloat + f4));
      if (paramFloat == 0.0F) {
        break label391;
      }
      this.cameraView.setClipLeft((int)(this.cameraViewOffsetX * (1.0F - paramFloat)));
      this.cameraView.setClipTop((int)(this.cameraViewOffsetY * (1.0F - paramFloat)));
      localLayoutParams.leftMargin = ((int)(this.cameraViewLocation[0] * (1.0F - paramFloat)));
      f1 = this.animateCameraValues[0];
    }
    for (localLayoutParams.topMargin = ((int)((this.cameraViewLocation[1] - this.animateCameraValues[0]) * (1.0F - paramFloat) + f1));; localLayoutParams.topMargin = 0)
    {
      this.cameraView.setLayoutParams(localLayoutParams);
      if (paramFloat > 0.5F) {
        break label406;
      }
      this.cameraIcon.setAlpha(1.0F - paramFloat / 0.5F);
      return;
      i = 0;
      break;
      label327:
      f2 = this.container.getWidth();
      f1 = this.container.getHeight();
      break label72;
      label348:
      if ((this.cameraView.getTranslationX() == 0.0F) && (this.cameraView.getTranslationY() == 0.0F)) {
        break label156;
      }
      this.cameraView.setTranslationX(0.0F);
      this.cameraView.setTranslationY(0.0F);
      break label156;
      label391:
      localLayoutParams.leftMargin = 0;
    }
    label406:
    this.cameraIcon.setAlpha(0.0F);
  }
  
  public void setDelegate(ChatAttachViewDelegate paramChatAttachViewDelegate)
  {
    this.delegate = paramChatAttachViewDelegate;
  }
  
  public void setPhotoChecked(int paramInt)
  {
    boolean bool = true;
    if ((paramInt < 0) || (paramInt >= MediaController.allPhotosAlbumEntry.photos.size())) {
      return;
    }
    Object localObject = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt);
    int j;
    int i;
    if (this.photoAttachAdapter.getSelectedPhotos().containsKey(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId)))
    {
      this.photoAttachAdapter.getSelectedPhotos().remove(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId));
      bool = false;
      j = this.attachPhotoRecyclerView.getChildCount();
      i = 0;
    }
    for (;;)
    {
      if (i < j)
      {
        localObject = this.attachPhotoRecyclerView.getChildAt(i);
        if (((localObject instanceof PhotoAttachPhotoCell)) && (((Integer)((View)localObject).getTag()).intValue() == paramInt)) {
          ((PhotoAttachPhotoCell)localObject).setChecked(bool, false);
        }
      }
      else
      {
        updatePhotosButton();
        return;
        this.photoAttachAdapter.getSelectedPhotos().put(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId), localObject);
        break;
      }
      i += 1;
    }
  }
  
  @SuppressLint({"NewApi"})
  protected void setRevealRadius(float paramFloat)
  {
    this.revealRadius = paramFloat;
    if (Build.VERSION.SDK_INT <= 19) {
      this.listView.invalidate();
    }
    if (!isDismissed())
    {
      int i = 0;
      if (i < this.innerAnimators.size())
      {
        InnerAnimator localInnerAnimator = (InnerAnimator)this.innerAnimators.get(i);
        if (localInnerAnimator.startRadius > paramFloat) {}
        for (;;)
        {
          i += 1;
          break;
          localInnerAnimator.animatorSet.start();
          this.innerAnimators.remove(i);
          i -= 1;
        }
      }
    }
  }
  
  @TargetApi(16)
  public void showCamera()
  {
    if (this.cameraView == null)
    {
      this.cameraView = new CameraView(this.baseFragment.getParentActivity());
      this.container.addView(this.cameraView, 1, LayoutHelper.createFrame(80, 80.0F));
      this.cameraView.setDelegate(new CameraView.CameraViewDelegate()
      {
        public void onCameraInit()
        {
          int k = 0;
          int j = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
          int i = 0;
          for (;;)
          {
            if (i < j)
            {
              localObject = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(i);
              if ((localObject instanceof PhotoAttachCameraCell)) {
                ((View)localObject).setVisibility(4);
              }
            }
            else
            {
              if (!ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode())) {
                break;
              }
              i = 0;
              while (i < 2)
              {
                ChatAttachAlert.this.flashModeButton[i].setVisibility(4);
                ChatAttachAlert.this.flashModeButton[i].setAlpha(0.0F);
                ChatAttachAlert.this.flashModeButton[i].setTranslationY(0.0F);
                i += 1;
              }
            }
            i += 1;
          }
          ChatAttachAlert.this.setCameraFlashModeIcon(ChatAttachAlert.this.flashModeButton[0], ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode());
          i = 0;
          if (i < 2)
          {
            localObject = ChatAttachAlert.this.flashModeButton[i];
            if (i == 0)
            {
              j = 0;
              label193:
              ((ImageView)localObject).setVisibility(j);
              localObject = ChatAttachAlert.this.flashModeButton[i];
              if ((i != 0) || (!ChatAttachAlert.this.cameraOpened)) {
                break label257;
              }
            }
            label257:
            for (float f = 1.0F;; f = 0.0F)
            {
              ((ImageView)localObject).setAlpha(f);
              ChatAttachAlert.this.flashModeButton[i].setTranslationY(0.0F);
              i += 1;
              break;
              j = 4;
              break label193;
            }
          }
          ChatAttachAlert.this.switchCameraButton.setFront(ChatAttachAlert.this.cameraView.isFrontface(), false);
          Object localObject = ChatAttachAlert.this.switchCameraButton;
          if (ChatAttachAlert.this.cameraView.hasFrontFaceCamera()) {}
          for (i = k;; i = 4)
          {
            ((SwitchCameraButton)localObject).setVisibility(i);
            return;
          }
        }
      });
      this.cameraIcon = new FrameLayout(this.baseFragment.getParentActivity());
      this.container.addView(this.cameraIcon, 2, LayoutHelper.createFrame(80, 80.0F));
      ImageView localImageView = new ImageView(this.baseFragment.getParentActivity());
      localImageView.setScaleType(ImageView.ScaleType.CENTER);
      localImageView.setImageResource(2130837777);
      this.cameraIcon.addView(localImageView, LayoutHelper.createFrame(80, 80, 85));
    }
    this.cameraView.setTranslationX(this.cameraViewLocation[0]);
    this.cameraView.setTranslationY(this.cameraViewLocation[1]);
    this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
    this.cameraIcon.setTranslationY(this.cameraViewLocation[1]);
  }
  
  public void updatePhotoAtIndex(int paramInt)
  {
    PhotoAttachPhotoCell localPhotoAttachPhotoCell = getCellForIndex(paramInt);
    MediaController.PhotoEntry localPhotoEntry;
    if (localPhotoAttachPhotoCell != null)
    {
      localPhotoAttachPhotoCell.getImageView().setOrientation(0, true);
      localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt);
      if (localPhotoEntry.thumbPath != null) {
        localPhotoAttachPhotoCell.getImageView().setImage(localPhotoEntry.thumbPath, null, localPhotoAttachPhotoCell.getContext().getResources().getDrawable(2130837858));
      }
    }
    else
    {
      return;
    }
    if (localPhotoEntry.path != null)
    {
      localPhotoAttachPhotoCell.getImageView().setOrientation(localPhotoEntry.orientation, true);
      localPhotoAttachPhotoCell.getImageView().setImage("thumb://" + localPhotoEntry.imageId + ":" + localPhotoEntry.path, null, localPhotoAttachPhotoCell.getContext().getResources().getDrawable(2130837858));
      return;
    }
    localPhotoAttachPhotoCell.getImageView().setImageResource(2130837858);
  }
  
  public void updatePhotosButton()
  {
    int i = this.photoAttachAdapter.getSelectedPhotos().size();
    if (i == 0)
    {
      this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0F), 0, 0);
      this.sendPhotosButton.imageView.setBackgroundResource(2130837532);
      this.sendPhotosButton.imageView.setImageResource(2130837531);
      this.sendPhotosButton.textView.setText("");
    }
    while ((Build.VERSION.SDK_INT >= 23) && (getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
    {
      this.progressView.setText(LocaleController.getString("PermissionStorage", 2131166102));
      this.progressView.setTextSize(16);
      return;
      this.sendPhotosButton.imageView.setPadding(AndroidUtilities.dp(2.0F), 0, 0, 0);
      this.sendPhotosButton.imageView.setBackgroundResource(2130837539);
      this.sendPhotosButton.imageView.setImageResource(2130837538);
      this.sendPhotosButton.textView.setText(LocaleController.formatString("SendItems", 2131166239, new Object[] { String.format("(%d)", new Object[] { Integer.valueOf(i) }) }));
    }
    this.progressView.setText(LocaleController.getString("NoPhotos", 2131165943));
    this.progressView.setTextSize(20);
  }
  
  public void willHidePhotoViewer()
  {
    int j = this.attachPhotoRecyclerView.getChildCount();
    int i = 0;
    while (i < j)
    {
      Object localObject = this.attachPhotoRecyclerView.getChildAt(i);
      if ((localObject instanceof PhotoAttachPhotoCell))
      {
        localObject = (PhotoAttachPhotoCell)localObject;
        if (((PhotoAttachPhotoCell)localObject).getCheckBox().getVisibility() != 0) {
          ((PhotoAttachPhotoCell)localObject).getCheckBox().setVisibility(0);
        }
      }
      i += 1;
    }
  }
  
  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null) {
      paramMessageObject.getCheckBox().setVisibility(0);
    }
  }
  
  private class AttachBotButton
    extends FrameLayout
  {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private boolean checkingForLongPress = false;
    private TLRPC.User currentUser;
    private BackupImageView imageView;
    private TextView nameTextView;
    private CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    private int pressCount = 0;
    private boolean pressed;
    
    public AttachBotButton(Context paramContext)
    {
      super();
      this.imageView = new BackupImageView(paramContext);
      this.imageView.setRoundRadius(AndroidUtilities.dp(27.0F));
      addView(this.imageView, LayoutHelper.createFrame(54, 54.0F, 49, 0.0F, 7.0F, 0.0F, 0.0F));
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextColor(-9079435);
      this.nameTextView.setTextSize(1, 12.0F);
      this.nameTextView.setMaxLines(2);
      this.nameTextView.setGravity(49);
      this.nameTextView.setLines(2);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 6.0F, 65.0F, 6.0F, 0.0F));
    }
    
    private void onLongPress()
    {
      if ((ChatAttachAlert.this.baseFragment == null) || (this.currentUser == null)) {
        return;
      }
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getContext());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
      localBuilder.setMessage(LocaleController.formatString("ChatHintsDelete", 2131165494, new Object[] { ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name) }));
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          SearchQuery.removeInline(ChatAttachAlert.AttachBotButton.this.currentUser.id);
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
      localBuilder.show();
    }
    
    protected void cancelCheckLongPress()
    {
      this.checkingForLongPress = false;
      if (this.pendingCheckForLongPress != null) {
        removeCallbacks(this.pendingCheckForLongPress);
      }
      if (this.pendingCheckForTap != null) {
        removeCallbacks(this.pendingCheckForTap);
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824));
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramMotionEvent.getAction() == 0)
      {
        this.pressed = true;
        invalidate();
        bool1 = true;
        if (bool1) {
          break label190;
        }
        bool2 = super.onTouchEvent(paramMotionEvent);
      }
      for (;;)
      {
        if ((paramMotionEvent.getAction() != 0) && (paramMotionEvent.getAction() != 2)) {
          cancelCheckLongPress();
        }
        return bool2;
        bool1 = bool2;
        if (!this.pressed) {
          break;
        }
        if (paramMotionEvent.getAction() == 1)
        {
          getParent().requestDisallowInterceptTouchEvent(true);
          this.pressed = false;
          playSoundEffect(0);
          ChatAttachAlert.this.delegate.didSelectBot(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_topPeer)SearchQuery.inlineBots.get(((Integer)getTag()).intValue())).peer.user_id)));
          ChatAttachAlert.this.setUseRevealAnimation(false);
          ChatAttachAlert.this.dismiss();
          ChatAttachAlert.this.setUseRevealAnimation(true);
          invalidate();
          bool1 = bool2;
          break;
        }
        bool1 = bool2;
        if (paramMotionEvent.getAction() != 3) {
          break;
        }
        this.pressed = false;
        invalidate();
        bool1 = bool2;
        break;
        label190:
        bool2 = bool1;
        if (paramMotionEvent.getAction() == 0)
        {
          startCheckLongPress();
          bool2 = bool1;
        }
      }
    }
    
    public void setUser(TLRPC.User paramUser)
    {
      if (paramUser == null) {
        return;
      }
      this.currentUser = paramUser;
      Object localObject2 = null;
      this.nameTextView.setText(ContactsController.formatName(paramUser.first_name, paramUser.last_name));
      this.avatarDrawable.setInfo(paramUser);
      Object localObject1 = localObject2;
      if (paramUser != null)
      {
        localObject1 = localObject2;
        if (paramUser.photo != null) {
          localObject1 = paramUser.photo.photo_small;
        }
      }
      this.imageView.setImage((TLObject)localObject1, "50_50", this.avatarDrawable);
      requestLayout();
    }
    
    protected void startCheckLongPress()
    {
      if (this.checkingForLongPress) {
        return;
      }
      this.checkingForLongPress = true;
      if (this.pendingCheckForTap == null) {
        this.pendingCheckForTap = new CheckForTap(null);
      }
      postDelayed(this.pendingCheckForTap, ViewConfiguration.getTapTimeout());
    }
    
    class CheckForLongPress
      implements Runnable
    {
      public int currentPressCount;
      
      CheckForLongPress() {}
      
      public void run()
      {
        if ((ChatAttachAlert.AttachBotButton.this.checkingForLongPress) && (ChatAttachAlert.AttachBotButton.this.getParent() != null) && (this.currentPressCount == ChatAttachAlert.AttachBotButton.this.pressCount))
        {
          ChatAttachAlert.AttachBotButton.access$202(ChatAttachAlert.AttachBotButton.this, false);
          ChatAttachAlert.AttachBotButton.this.performHapticFeedback(0);
          ChatAttachAlert.AttachBotButton.this.onLongPress();
          MotionEvent localMotionEvent = MotionEvent.obtain(0L, 0L, 3, 0.0F, 0.0F, 0);
          ChatAttachAlert.AttachBotButton.this.onTouchEvent(localMotionEvent);
          localMotionEvent.recycle();
        }
      }
    }
    
    private final class CheckForTap
      implements Runnable
    {
      private CheckForTap() {}
      
      public void run()
      {
        if (ChatAttachAlert.AttachBotButton.this.pendingCheckForLongPress == null) {
          ChatAttachAlert.AttachBotButton.access$002(ChatAttachAlert.AttachBotButton.this, new ChatAttachAlert.AttachBotButton.CheckForLongPress(ChatAttachAlert.AttachBotButton.this));
        }
        ChatAttachAlert.AttachBotButton.this.pendingCheckForLongPress.currentPressCount = ChatAttachAlert.AttachBotButton.access$104(ChatAttachAlert.AttachBotButton.this);
        ChatAttachAlert.AttachBotButton.this.postDelayed(ChatAttachAlert.AttachBotButton.this.pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout());
      }
    }
  }
  
  private class AttachButton
    extends FrameLayout
  {
    private ImageView imageView;
    private TextView textView;
    
    public AttachButton(Context paramContext)
    {
      super();
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      addView(this.imageView, LayoutHelper.createFrame(64, 64, 49));
      this.textView = new TextView(paramContext);
      this.textView.setLines(1);
      this.textView.setSingleLine(true);
      this.textView.setGravity(1);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      this.textView.setTextColor(-9079435);
      this.textView.setTextSize(1, 12.0F);
      addView(this.textView, LayoutHelper.createFrame(-1, -2.0F, 51, 0.0F, 64.0F, 0.0F, 0.0F));
    }
    
    public boolean hasOverlappingRendering()
    {
      return false;
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0F), 1073741824));
    }
    
    public void setTextAndIcon(CharSequence paramCharSequence, Drawable paramDrawable)
    {
      this.textView.setText(paramCharSequence);
      this.imageView.setBackgroundDrawable(paramDrawable);
    }
  }
  
  public static abstract interface ChatAttachViewDelegate
  {
    public abstract void didPressedButton(int paramInt);
    
    public abstract void didSelectBot(TLRPC.User paramUser);
    
    public abstract View getRevealView();
  }
  
  private class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }
  
  private class InnerAnimator
  {
    private AnimatorSet animatorSet;
    private float startRadius;
    
    private InnerAnimator() {}
  }
  
  private class ListAdapter
    extends RecyclerView.Adapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      if (!SearchQuery.inlineBots.isEmpty()) {}
      for (int i = (int)Math.ceil(SearchQuery.inlineBots.size() / 4.0F) + 1;; i = 0) {
        return i + 1;
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return 2;
      case 0: 
        return 0;
      }
      return 1;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramInt > 1)
      {
        int i = (paramInt - 2) * 4;
        paramViewHolder = (FrameLayout)paramViewHolder.itemView;
        paramInt = 0;
        if (paramInt < 4)
        {
          ChatAttachAlert.AttachBotButton localAttachBotButton = (ChatAttachAlert.AttachBotButton)paramViewHolder.getChildAt(paramInt);
          if (i + paramInt >= SearchQuery.inlineBots.size()) {
            localAttachBotButton.setVisibility(4);
          }
          for (;;)
          {
            paramInt += 1;
            break;
            localAttachBotButton.setVisibility(0);
            localAttachBotButton.setTag(Integer.valueOf(i + paramInt));
            localAttachBotButton.setUser(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_topPeer)SearchQuery.inlineBots.get(i + paramInt)).peer.user_id)));
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      FrameLayout local1;
      switch (paramInt)
      {
      default: 
        local1 = new FrameLayout(this.mContext)
        {
          protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
          {
            paramAnonymousInt2 = (paramAnonymousInt3 - paramAnonymousInt1 - AndroidUtilities.dp(360.0F)) / 3;
            paramAnonymousInt1 = 0;
            while (paramAnonymousInt1 < 4)
            {
              paramAnonymousInt3 = AndroidUtilities.dp(10.0F) + paramAnonymousInt1 % 4 * (AndroidUtilities.dp(85.0F) + paramAnonymousInt2);
              View localView = getChildAt(paramAnonymousInt1);
              localView.layout(paramAnonymousInt3, 0, localView.getMeasuredWidth() + paramAnonymousInt3, localView.getMeasuredHeight());
              paramAnonymousInt1 += 1;
            }
          }
        };
        paramInt = 0;
      case 0: 
        while (paramInt < 4)
        {
          local1.addView(new ChatAttachAlert.AttachBotButton(ChatAttachAlert.this, this.mContext));
          paramInt += 1;
          continue;
          paramViewGroup = ChatAttachAlert.this.attachView;
        }
      }
      for (;;)
      {
        return new ChatAttachAlert.Holder(ChatAttachAlert.this, paramViewGroup);
        paramViewGroup = new ShadowSectionCell(this.mContext);
        continue;
        paramViewGroup = local1;
        local1.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
      }
    }
  }
  
  private class PhotoAttachAdapter
    extends RecyclerView.Adapter
  {
    private Context mContext;
    private HashMap<Integer, MediaController.PhotoEntry> selectedPhotos = new HashMap();
    
    public PhotoAttachAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public void clearSelectedPhotos()
    {
      if (!this.selectedPhotos.isEmpty())
      {
        Iterator localIterator = this.selectedPhotos.entrySet().iterator();
        while (localIterator.hasNext())
        {
          MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)((Map.Entry)localIterator.next()).getValue();
          localPhotoEntry.imagePath = null;
          localPhotoEntry.thumbPath = null;
          localPhotoEntry.caption = null;
          localPhotoEntry.stickers.clear();
        }
        this.selectedPhotos.clear();
        ChatAttachAlert.this.updatePhotosButton();
        notifyDataSetChanged();
      }
    }
    
    public ChatAttachAlert.Holder createHolder()
    {
      PhotoAttachPhotoCell localPhotoAttachPhotoCell = new PhotoAttachPhotoCell(this.mContext);
      localPhotoAttachPhotoCell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate()
      {
        public void onCheckClick(PhotoAttachPhotoCell paramAnonymousPhotoAttachPhotoCell)
        {
          MediaController.PhotoEntry localPhotoEntry = paramAnonymousPhotoAttachPhotoCell.getPhotoEntry();
          boolean bool;
          if (ChatAttachAlert.PhotoAttachAdapter.this.selectedPhotos.containsKey(Integer.valueOf(localPhotoEntry.imageId)))
          {
            ChatAttachAlert.PhotoAttachAdapter.this.selectedPhotos.remove(Integer.valueOf(localPhotoEntry.imageId));
            paramAnonymousPhotoAttachPhotoCell.setChecked(false, true);
            localPhotoEntry.imagePath = null;
            localPhotoEntry.thumbPath = null;
            localPhotoEntry.stickers.clear();
            if (((Integer)paramAnonymousPhotoAttachPhotoCell.getTag()).intValue() == MediaController.allPhotosAlbumEntry.photos.size() - 1)
            {
              bool = true;
              paramAnonymousPhotoAttachPhotoCell.setPhotoEntry(localPhotoEntry, bool);
            }
          }
          for (;;)
          {
            ChatAttachAlert.this.updatePhotosButton();
            return;
            bool = false;
            break;
            ChatAttachAlert.PhotoAttachAdapter.this.selectedPhotos.put(Integer.valueOf(localPhotoEntry.imageId), localPhotoEntry);
            paramAnonymousPhotoAttachPhotoCell.setChecked(true, true);
          }
        }
      });
      return new ChatAttachAlert.Holder(ChatAttachAlert.this, localPhotoAttachPhotoCell);
    }
    
    public int getItemCount()
    {
      int i = 0;
      if (ChatAttachAlert.this.deviceHasGoodCamera) {
        i = 0 + 1;
      }
      int j = i;
      if (MediaController.allPhotosAlbumEntry != null) {
        j = i + MediaController.allPhotosAlbumEntry.photos.size();
      }
      return j;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((ChatAttachAlert.this.deviceHasGoodCamera) && (paramInt == 0)) {
        return 1;
      }
      return 0;
    }
    
    public HashMap<Integer, MediaController.PhotoEntry> getSelectedPhotos()
    {
      return this.selectedPhotos;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if ((!ChatAttachAlert.this.deviceHasGoodCamera) || (paramInt != 0))
      {
        i = paramInt;
        if (ChatAttachAlert.this.deviceHasGoodCamera) {
          i = paramInt - 1;
        }
        paramViewHolder = (PhotoAttachPhotoCell)paramViewHolder.itemView;
        localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(i);
        if (i == MediaController.allPhotosAlbumEntry.photos.size() - 1)
        {
          bool = true;
          paramViewHolder.setPhotoEntry(localPhotoEntry, bool);
          paramViewHolder.setChecked(this.selectedPhotos.containsKey(Integer.valueOf(localPhotoEntry.imageId)), false);
          paramViewHolder.getImageView().setTag(Integer.valueOf(i));
          paramViewHolder.setTag(Integer.valueOf(i));
        }
      }
      while ((!ChatAttachAlert.this.deviceHasGoodCamera) || (paramInt != 0)) {
        for (;;)
        {
          int i;
          MediaController.PhotoEntry localPhotoEntry;
          return;
          boolean bool = false;
        }
      }
      if ((ChatAttachAlert.this.cameraView != null) && (ChatAttachAlert.this.cameraView.isInitied()))
      {
        paramViewHolder.itemView.setVisibility(4);
        return;
      }
      paramViewHolder.itemView.setVisibility(0);
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        if (!ChatAttachAlert.this.viewsCache.isEmpty())
        {
          paramViewGroup = (ChatAttachAlert.Holder)ChatAttachAlert.this.viewsCache.get(0);
          ChatAttachAlert.this.viewsCache.remove(0);
          return paramViewGroup;
        }
        break;
      case 1: 
        return new ChatAttachAlert.Holder(ChatAttachAlert.this, new PhotoAttachCameraCell(this.mContext));
      }
      return createHolder();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ChatAttachAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */