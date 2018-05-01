package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AudioPlayerCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.LaunchActivity;

public class AudioPlayerAlert
  extends BottomSheet
  implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate
{
  private int TAG;
  private ActionBar actionBar;
  private AnimatorSet actionBarAnimation;
  private AnimatorSet animatorSet;
  private TextView authorTextView;
  private ChatAvatarContainer avatarContainer;
  private View[] buttons = new View[5];
  private TextView durationTextView;
  private float endTranslation;
  private float fullAnimationProgress;
  private boolean hasNoCover;
  private boolean hasOptions = true;
  private boolean inFullSize;
  private boolean isInFullMode;
  private int lastTime;
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private ActionBarMenuItem menuItem;
  private Drawable noCoverDrawable;
  private ActionBarMenuItem optionsButton;
  private Paint paint = new Paint(1);
  private float panelEndTranslation;
  private float panelStartTranslation;
  private LaunchActivity parentActivity;
  private BackupImageView placeholderImageView;
  private ImageView playButton;
  private Drawable[] playOrderButtons = new Drawable[2];
  private FrameLayout playerLayout;
  private ArrayList<MessageObject> playlist = new ArrayList();
  private LineProgressView progressView;
  private ImageView repeatButton;
  private int scrollOffsetY = Integer.MAX_VALUE;
  private boolean scrollToSong = true;
  private ActionBarMenuItem searchItem;
  private int searchOpenOffset;
  private int searchOpenPosition = -1;
  private boolean searchWas;
  private boolean searching;
  private SeekBarView seekBarView;
  private View shadow;
  private View shadow2;
  private Drawable shadowDrawable;
  private ActionBarMenuItem shuffleButton;
  private float startTranslation;
  private float thumbMaxScale;
  private int thumbMaxX;
  private int thumbMaxY;
  private SimpleTextView timeTextView;
  private TextView titleTextView;
  private int topBeforeSwitch;
  
  public AudioPlayerAlert(Context paramContext)
  {
    super(paramContext, true);
    Object localObject1 = MediaController.getInstance().getPlayingMessageObject();
    int i;
    int j;
    if (localObject1 != null)
    {
      this.currentAccount = ((MessageObject)localObject1).currentAccount;
      this.parentActivity = ((LaunchActivity)paramContext);
      this.noCoverDrawable = paramContext.getResources().getDrawable(NUM).mutate();
      this.noCoverDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_placeholder"), PorterDuff.Mode.MULTIPLY));
      this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStarted);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.musicDidLoaded);
      this.shadowDrawable = paramContext.getResources().getDrawable(NUM).mutate();
      this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_background"), PorterDuff.Mode.MULTIPLY));
      this.paint.setColor(Theme.getColor("player_placeholderBackground"));
      this.containerView = new FrameLayout(paramContext)
      {
        private boolean ignoreLayout = false;
        
        protected void onDraw(Canvas paramAnonymousCanvas)
        {
          AudioPlayerAlert.this.shadowDrawable.setBounds(0, Math.max(AudioPlayerAlert.this.actionBar.getMeasuredHeight(), AudioPlayerAlert.this.scrollOffsetY) - AudioPlayerAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
          AudioPlayerAlert.this.shadowDrawable.draw(paramAnonymousCanvas);
        }
        
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((paramAnonymousMotionEvent.getAction() == 0) && (AudioPlayerAlert.this.scrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < AudioPlayerAlert.this.scrollOffsetY) && (AudioPlayerAlert.this.placeholderImageView.getTranslationX() == 0.0F)) {
            AudioPlayerAlert.this.dismiss();
          }
          for (boolean bool = true;; bool = super.onInterceptTouchEvent(paramAnonymousMotionEvent)) {
            return bool;
          }
        }
        
        protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
          paramAnonymousInt1 = AudioPlayerAlert.this.actionBar.getMeasuredHeight();
          AudioPlayerAlert.this.shadow.layout(AudioPlayerAlert.this.shadow.getLeft(), paramAnonymousInt1, AudioPlayerAlert.this.shadow.getRight(), AudioPlayerAlert.this.shadow.getMeasuredHeight() + paramAnonymousInt1);
          AudioPlayerAlert.this.updateLayout();
          AudioPlayerAlert.this.setFullAnimationProgress(AudioPlayerAlert.this.fullAnimationProgress);
        }
        
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          int i = View.MeasureSpec.getSize(paramAnonymousInt2);
          paramAnonymousInt2 = AndroidUtilities.dp(178.0F) + AudioPlayerAlert.this.playlist.size() * AndroidUtilities.dp(56.0F) + AudioPlayerAlert.backgroundPaddingTop + ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
          int j = View.MeasureSpec.makeMeasureSpec(i, NUM);
          int k;
          int m;
          boolean bool;
          if (AudioPlayerAlert.this.searching)
          {
            k = AndroidUtilities.dp(178.0F);
            m = ActionBar.getCurrentActionBarHeight();
            if (Build.VERSION.SDK_INT >= 21)
            {
              paramAnonymousInt2 = AndroidUtilities.statusBarHeight;
              paramAnonymousInt2 = m + k + paramAnonymousInt2;
              if (AudioPlayerAlert.this.listView.getPaddingTop() != paramAnonymousInt2)
              {
                this.ignoreLayout = true;
                AudioPlayerAlert.this.listView.setPadding(0, paramAnonymousInt2, 0, AndroidUtilities.dp(8.0F));
                this.ignoreLayout = false;
              }
              super.onMeasure(paramAnonymousInt1, j);
              AudioPlayerAlert localAudioPlayerAlert = AudioPlayerAlert.this;
              if (getMeasuredHeight() < i) {
                break label425;
              }
              bool = true;
              label156:
              AudioPlayerAlert.access$602(localAudioPlayerAlert, bool);
              paramAnonymousInt2 = ActionBar.getCurrentActionBarHeight();
              if (Build.VERSION.SDK_INT < 21) {
                break label431;
              }
            }
          }
          label419:
          label425:
          label431:
          for (paramAnonymousInt1 = AndroidUtilities.statusBarHeight;; paramAnonymousInt1 = 0)
          {
            paramAnonymousInt1 = i - paramAnonymousInt2 - paramAnonymousInt1 - AndroidUtilities.dp(120.0F);
            paramAnonymousInt2 = Math.max(paramAnonymousInt1, getMeasuredWidth());
            AudioPlayerAlert.access$702(AudioPlayerAlert.this, (getMeasuredWidth() - paramAnonymousInt2) / 2 - AndroidUtilities.dp(17.0F));
            AudioPlayerAlert.access$802(AudioPlayerAlert.this, AndroidUtilities.dp(19.0F));
            AudioPlayerAlert.access$902(AudioPlayerAlert.this, getMeasuredHeight() - AudioPlayerAlert.this.playerLayout.getMeasuredHeight());
            AudioPlayerAlert.access$1102(AudioPlayerAlert.this, paramAnonymousInt2 / AudioPlayerAlert.this.placeholderImageView.getMeasuredWidth() - 1.0F);
            AudioPlayerAlert.access$1202(AudioPlayerAlert.this, ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(5.0F));
            paramAnonymousInt2 = (int)Math.ceil(AudioPlayerAlert.this.placeholderImageView.getMeasuredHeight() * (1.0F + AudioPlayerAlert.this.thumbMaxScale));
            if (paramAnonymousInt2 > paramAnonymousInt1) {
              AudioPlayerAlert.access$1202(AudioPlayerAlert.this, AudioPlayerAlert.this.endTranslation - (paramAnonymousInt2 - paramAnonymousInt1));
            }
            return;
            paramAnonymousInt2 = 0;
            break;
            if (paramAnonymousInt2 < i)
            {
              paramAnonymousInt2 = i - paramAnonymousInt2;
              k = ActionBar.getCurrentActionBarHeight();
              if (Build.VERSION.SDK_INT < 21) {
                break label419;
              }
            }
            for (m = AndroidUtilities.statusBarHeight;; m = 0)
            {
              paramAnonymousInt2 += m + k;
              break;
              if (paramAnonymousInt2 < i) {}
              for (paramAnonymousInt2 = 0;; paramAnonymousInt2 = i - i / 5 * 3) {
                break;
              }
            }
            bool = false;
            break label156;
          }
        }
        
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((!AudioPlayerAlert.this.isDismissed()) && (super.onTouchEvent(paramAnonymousMotionEvent))) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
        
        public void requestLayout()
        {
          if (this.ignoreLayout) {}
          for (;;)
          {
            return;
            super.requestLayout();
          }
        }
      };
      this.containerView.setWillNotDraw(false);
      this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
      this.actionBar = new ActionBar(paramContext);
      this.actionBar.setBackgroundColor(Theme.getColor("player_actionBar"));
      this.actionBar.setBackButtonImage(NUM);
      this.actionBar.setItemsColor(Theme.getColor("player_actionBarItems"), false);
      this.actionBar.setItemsBackgroundColor(Theme.getColor("player_actionBarSelector"), false);
      this.actionBar.setTitleColor(Theme.getColor("player_actionBarTitle"));
      this.actionBar.setSubtitleColor(Theme.getColor("player_actionBarSubtitle"));
      this.actionBar.setAlpha(0.0F);
      this.actionBar.setTitle("1");
      this.actionBar.setSubtitle("1");
      this.actionBar.getTitleTextView().setAlpha(0.0F);
      this.actionBar.getSubtitleTextView().setAlpha(0.0F);
      this.avatarContainer = new ChatAvatarContainer(paramContext, null, false);
      this.avatarContainer.setEnabled(false);
      this.avatarContainer.setTitleColors(Theme.getColor("player_actionBarTitle"), Theme.getColor("player_actionBarSubtitle"));
      if (localObject1 != null)
      {
        long l = ((MessageObject)localObject1).getDialogId();
        i = (int)l;
        j = (int)(l >> 32);
        if (i == 0) {
          break label2569;
        }
        if (i <= 0) {
          break label2526;
        }
        localObject1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
        if (localObject1 != null)
        {
          this.avatarContainer.setTitle(ContactsController.formatName(((TLRPC.User)localObject1).first_name, ((TLRPC.User)localObject1).last_name));
          this.avatarContainer.setUserAvatar((TLRPC.User)localObject1);
        }
      }
    }
    for (;;)
    {
      this.avatarContainer.setSubtitle(LocaleController.getString("AudioTitle", NUM));
      this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0F, 51, 56.0F, 0.0F, 40.0F, 0.0F));
      localObject1 = this.actionBar.createMenu();
      this.menuItem = ((ActionBarMenu)localObject1).addItem(0, NUM);
      this.menuItem.addSubItem(1, LocaleController.getString("Forward", NUM));
      this.menuItem.addSubItem(2, LocaleController.getString("ShareFile", NUM));
      this.menuItem.addSubItem(4, LocaleController.getString("ShowInChat", NUM));
      this.menuItem.setTranslationX(AndroidUtilities.dp(48.0F));
      this.menuItem.setAlpha(0.0F);
      this.searchItem = ((ActionBarMenu)localObject1).addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
      {
        public void onSearchCollapse()
        {
          AudioPlayerAlert.this.avatarContainer.setVisibility(0);
          if (AudioPlayerAlert.this.hasOptions) {
            AudioPlayerAlert.this.menuItem.setVisibility(4);
          }
          if (AudioPlayerAlert.this.searching)
          {
            AudioPlayerAlert.access$2202(AudioPlayerAlert.this, false);
            AudioPlayerAlert.access$402(AudioPlayerAlert.this, false);
            AudioPlayerAlert.this.setAllowNestedScroll(true);
            AudioPlayerAlert.this.listAdapter.search(null);
          }
        }
        
        public void onSearchExpand()
        {
          AudioPlayerAlert.access$2402(AudioPlayerAlert.this, AudioPlayerAlert.this.layoutManager.findLastVisibleItemPosition());
          View localView = AudioPlayerAlert.this.layoutManager.findViewByPosition(AudioPlayerAlert.this.searchOpenPosition);
          AudioPlayerAlert localAudioPlayerAlert = AudioPlayerAlert.this;
          if (localView == null) {}
          for (int i = 0;; i = localView.getTop())
          {
            AudioPlayerAlert.access$2602(localAudioPlayerAlert, i - AudioPlayerAlert.this.listView.getPaddingTop());
            AudioPlayerAlert.this.avatarContainer.setVisibility(8);
            if (AudioPlayerAlert.this.hasOptions) {
              AudioPlayerAlert.this.menuItem.setVisibility(8);
            }
            AudioPlayerAlert.access$402(AudioPlayerAlert.this, true);
            AudioPlayerAlert.this.setAllowNestedScroll(false);
            AudioPlayerAlert.this.listAdapter.notifyDataSetChanged();
            return;
          }
        }
        
        public void onTextChanged(EditText paramAnonymousEditText)
        {
          if (paramAnonymousEditText.length() > 0) {
            AudioPlayerAlert.this.listAdapter.search(paramAnonymousEditText.getText().toString());
          }
          for (;;)
          {
            return;
            AudioPlayerAlert.access$2202(AudioPlayerAlert.this, false);
            AudioPlayerAlert.this.listAdapter.search(null);
          }
        }
      });
      localObject1 = this.searchItem.getSearchField();
      ((EditTextBoldCursor)localObject1).setHint(LocaleController.getString("Search", NUM));
      ((EditTextBoldCursor)localObject1).setTextColor(Theme.getColor("player_actionBarTitle"));
      ((EditTextBoldCursor)localObject1).setHintTextColor(Theme.getColor("player_time"));
      ((EditTextBoldCursor)localObject1).setCursorColor(Theme.getColor("player_actionBarTitle"));
      if (!AndroidUtilities.isTablet())
      {
        this.actionBar.showActionModeTop();
        this.actionBar.setActionModeTopColor(Theme.getColor("player_actionBarTop"));
      }
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            AudioPlayerAlert.this.dismiss();
          }
          for (;;)
          {
            return;
            AudioPlayerAlert.this.onSubItemClick(paramAnonymousInt);
          }
        }
      });
      this.shadow = new View(paramContext);
      this.shadow.setAlpha(0.0F);
      this.shadow.setBackgroundResource(NUM);
      this.shadow2 = new View(paramContext);
      this.shadow2.setAlpha(0.0F);
      this.shadow2.setBackgroundResource(NUM);
      this.playerLayout = new FrameLayout(paramContext);
      this.playerLayout.setBackgroundColor(Theme.getColor("player_background"));
      this.placeholderImageView = new BackupImageView(paramContext)
      {
        private RectF rect = new RectF();
        
        protected void onDraw(Canvas paramAnonymousCanvas)
        {
          if (AudioPlayerAlert.this.hasNoCover)
          {
            this.rect.set(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight());
            paramAnonymousCanvas.drawRoundRect(this.rect, getRoundRadius(), getRoundRadius(), AudioPlayerAlert.this.paint);
            float f = AudioPlayerAlert.this.thumbMaxScale / getScaleX() / 3.0F;
            int i = (int)(AndroidUtilities.dp(63.0F) * Math.max(f / AudioPlayerAlert.this.thumbMaxScale, 1.0F / AudioPlayerAlert.this.thumbMaxScale));
            int j = (int)(this.rect.centerX() - i / 2);
            int k = (int)(this.rect.centerY() - i / 2);
            AudioPlayerAlert.this.noCoverDrawable.setBounds(j, k, j + i, k + i);
            AudioPlayerAlert.this.noCoverDrawable.draw(paramAnonymousCanvas);
          }
          for (;;)
          {
            return;
            super.onDraw(paramAnonymousCanvas);
          }
        }
      };
      this.placeholderImageView.setRoundRadius(AndroidUtilities.dp(20.0F));
      this.placeholderImageView.setPivotX(0.0F);
      this.placeholderImageView.setPivotY(0.0F);
      this.placeholderImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          float f1 = 0.0F;
          float f2 = 0.0F;
          if (AudioPlayerAlert.this.animatorSet != null)
          {
            AudioPlayerAlert.this.animatorSet.cancel();
            AudioPlayerAlert.access$3102(AudioPlayerAlert.this, null);
          }
          AudioPlayerAlert.access$3102(AudioPlayerAlert.this, new AnimatorSet());
          Object localObject1;
          boolean bool;
          if (AudioPlayerAlert.this.scrollOffsetY <= AudioPlayerAlert.this.actionBar.getMeasuredHeight())
          {
            paramAnonymousView = AudioPlayerAlert.this.animatorSet;
            localObject1 = AudioPlayerAlert.this;
            if (AudioPlayerAlert.this.isInFullMode)
            {
              paramAnonymousView.playTogether(new Animator[] { ObjectAnimator.ofFloat(localObject1, "fullAnimationProgress", new float[] { f2 }) });
              AudioPlayerAlert.this.animatorSet.setInterpolator(new DecelerateInterpolator());
              AudioPlayerAlert.this.animatorSet.setDuration(250L);
              AudioPlayerAlert.this.animatorSet.addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnonymous2Animator)
                {
                  if (paramAnonymous2Animator.equals(AudioPlayerAlert.this.animatorSet))
                  {
                    if (AudioPlayerAlert.this.isInFullMode) {
                      break label98;
                    }
                    AudioPlayerAlert.this.listView.setScrollEnabled(true);
                    if (AudioPlayerAlert.this.hasOptions) {
                      AudioPlayerAlert.this.menuItem.setVisibility(4);
                    }
                    AudioPlayerAlert.this.searchItem.setVisibility(0);
                  }
                  for (;;)
                  {
                    AudioPlayerAlert.access$3102(AudioPlayerAlert.this, null);
                    return;
                    label98:
                    if (AudioPlayerAlert.this.hasOptions) {
                      AudioPlayerAlert.this.menuItem.setVisibility(0);
                    }
                    AudioPlayerAlert.this.searchItem.setVisibility(4);
                  }
                }
              });
              AudioPlayerAlert.this.animatorSet.start();
              if (AudioPlayerAlert.this.hasOptions) {
                AudioPlayerAlert.this.menuItem.setVisibility(0);
              }
              AudioPlayerAlert.this.searchItem.setVisibility(0);
              paramAnonymousView = AudioPlayerAlert.this;
              if (AudioPlayerAlert.this.isInFullMode) {
                break label476;
              }
              bool = true;
              label226:
              AudioPlayerAlert.access$3202(paramAnonymousView, bool);
              AudioPlayerAlert.this.listView.setScrollEnabled(false);
              if (!AudioPlayerAlert.this.isInFullMode) {
                break label482;
              }
              AudioPlayerAlert.this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(68.0F));
            }
          }
          for (;;)
          {
            return;
            f2 = 1.0F;
            break;
            paramAnonymousView = AudioPlayerAlert.this.animatorSet;
            localObject1 = AudioPlayerAlert.this;
            label302:
            Object localObject2;
            label339:
            Object localObject3;
            label376:
            View localView;
            if (AudioPlayerAlert.this.isInFullMode)
            {
              f2 = 0.0F;
              localObject1 = ObjectAnimator.ofFloat(localObject1, "fullAnimationProgress", new float[] { f2 });
              localObject2 = AudioPlayerAlert.this.actionBar;
              if (!AudioPlayerAlert.this.isInFullMode) {
                break label461;
              }
              f2 = 0.0F;
              localObject2 = ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f2 });
              localObject3 = AudioPlayerAlert.this.shadow;
              if (!AudioPlayerAlert.this.isInFullMode) {
                break label466;
              }
              f2 = 0.0F;
              localObject3 = ObjectAnimator.ofFloat(localObject3, "alpha", new float[] { f2 });
              localView = AudioPlayerAlert.this.shadow2;
              if (!AudioPlayerAlert.this.isInFullMode) {
                break label471;
              }
            }
            label461:
            label466:
            label471:
            for (f2 = f1;; f2 = 1.0F)
            {
              paramAnonymousView.playTogether(new Animator[] { localObject1, localObject2, localObject3, ObjectAnimator.ofFloat(localView, "alpha", new float[] { f2 }) });
              break;
              f2 = 1.0F;
              break label302;
              f2 = 1.0F;
              break label339;
              f2 = 1.0F;
              break label376;
            }
            label476:
            bool = false;
            break label226;
            label482:
            AudioPlayerAlert.this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(10.0F));
          }
        }
      });
      this.titleTextView = new TextView(paramContext);
      this.titleTextView.setTextColor(Theme.getColor("player_actionBarTitle"));
      this.titleTextView.setTextSize(1, 15.0F);
      this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.titleTextView.setSingleLine(true);
      this.playerLayout.addView(this.titleTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 72.0F, 18.0F, 60.0F, 0.0F));
      this.authorTextView = new TextView(paramContext);
      this.authorTextView.setTextColor(Theme.getColor("player_time"));
      this.authorTextView.setTextSize(1, 14.0F);
      this.authorTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.authorTextView.setSingleLine(true);
      this.playerLayout.addView(this.authorTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 72.0F, 40.0F, 60.0F, 0.0F));
      this.optionsButton = new ActionBarMenuItem(paramContext, null, 0, Theme.getColor("player_actionBarItems"));
      this.optionsButton.setLongClickEnabled(false);
      this.optionsButton.setIcon(NUM);
      this.optionsButton.setAdditionalOffset(-AndroidUtilities.dp(120.0F));
      this.playerLayout.addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0F, 53, 0.0F, 19.0F, 10.0F, 0.0F));
      this.optionsButton.addSubItem(1, LocaleController.getString("Forward", NUM));
      this.optionsButton.addSubItem(2, LocaleController.getString("ShareFile", NUM));
      this.optionsButton.addSubItem(4, LocaleController.getString("ShowInChat", NUM));
      this.optionsButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          AudioPlayerAlert.this.optionsButton.toggleSubMenu();
        }
      });
      this.optionsButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          AudioPlayerAlert.this.onSubItemClick(paramAnonymousInt);
        }
      });
      this.seekBarView = new SeekBarView(paramContext);
      this.seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate()
      {
        public void onSeekBarDrag(float paramAnonymousFloat)
        {
          MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), paramAnonymousFloat);
        }
      });
      this.playerLayout.addView(this.seekBarView, LayoutHelper.createFrame(-1, 30.0F, 51, 8.0F, 62.0F, 8.0F, 0.0F));
      this.progressView = new LineProgressView(paramContext);
      this.progressView.setVisibility(4);
      this.progressView.setBackgroundColor(Theme.getColor("player_progressBackground"));
      this.progressView.setProgressColor(Theme.getColor("player_progress"));
      this.playerLayout.addView(this.progressView, LayoutHelper.createFrame(-1, 2.0F, 51, 20.0F, 78.0F, 20.0F, 0.0F));
      this.timeTextView = new SimpleTextView(paramContext);
      this.timeTextView.setTextSize(12);
      this.timeTextView.setTextColor(Theme.getColor("player_time"));
      this.playerLayout.addView(this.timeTextView, LayoutHelper.createFrame(100, -2.0F, 51, 20.0F, 92.0F, 0.0F, 0.0F));
      this.durationTextView = new TextView(paramContext);
      this.durationTextView.setTextSize(1, 12.0F);
      this.durationTextView.setTextColor(Theme.getColor("player_time"));
      this.durationTextView.setGravity(17);
      this.playerLayout.addView(this.durationTextView, LayoutHelper.createFrame(-2, -2.0F, 53, 0.0F, 90.0F, 20.0F, 0.0F));
      localObject1 = new FrameLayout(paramContext)
      {
        protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          paramAnonymousInt2 = (paramAnonymousInt3 - paramAnonymousInt1 - AndroidUtilities.dp(248.0F)) / 4;
          for (paramAnonymousInt1 = 0; paramAnonymousInt1 < 5; paramAnonymousInt1++)
          {
            paramAnonymousInt4 = AndroidUtilities.dp(paramAnonymousInt1 * 48 + 4) + paramAnonymousInt2 * paramAnonymousInt1;
            paramAnonymousInt3 = AndroidUtilities.dp(9.0F);
            AudioPlayerAlert.this.buttons[paramAnonymousInt1].layout(paramAnonymousInt4, paramAnonymousInt3, AudioPlayerAlert.this.buttons[paramAnonymousInt1].getMeasuredWidth() + paramAnonymousInt4, AudioPlayerAlert.this.buttons[paramAnonymousInt1].getMeasuredHeight() + paramAnonymousInt3);
          }
        }
      };
      this.playerLayout.addView((View)localObject1, LayoutHelper.createFrame(-1, 66.0F, 51, 0.0F, 106.0F, 0.0F, 0.0F));
      Object localObject2 = this.buttons;
      Object localObject3 = new ActionBarMenuItem(paramContext, null, 0, 0);
      this.shuffleButton = ((ActionBarMenuItem)localObject3);
      localObject2[0] = localObject3;
      this.shuffleButton.setLongClickEnabled(false);
      this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(10.0F));
      ((FrameLayout)localObject1).addView(this.shuffleButton, LayoutHelper.createFrame(48, 48, 51));
      this.shuffleButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          AudioPlayerAlert.this.shuffleButton.toggleSubMenu();
        }
      });
      localObject3 = this.shuffleButton.addSubItem(1, LocaleController.getString("ReverseOrder", NUM));
      ((TextView)localObject3).setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(16.0F), 0);
      this.playOrderButtons[0] = paramContext.getResources().getDrawable(NUM).mutate();
      ((TextView)localObject3).setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
      ((TextView)localObject3).setCompoundDrawablesWithIntrinsicBounds(this.playOrderButtons[0], null, null, null);
      localObject3 = this.shuffleButton.addSubItem(2, LocaleController.getString("Shuffle", NUM));
      ((TextView)localObject3).setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(16.0F), 0);
      this.playOrderButtons[1] = paramContext.getResources().getDrawable(NUM).mutate();
      ((TextView)localObject3).setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
      ((TextView)localObject3).setCompoundDrawablesWithIntrinsicBounds(this.playOrderButtons[1], null, null, null);
      this.shuffleButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          MediaController.getInstance().toggleShuffleMusic(paramAnonymousInt);
          AudioPlayerAlert.this.updateShuffleButton();
          AudioPlayerAlert.this.listAdapter.notifyDataSetChanged();
        }
      });
      localObject3 = this.buttons;
      localObject2 = new ImageView(paramContext);
      localObject3[1] = localObject2;
      ((ImageView)localObject2).setScaleType(ImageView.ScaleType.CENTER);
      ((ImageView)localObject2).setImageDrawable(Theme.createSimpleSelectorDrawable(paramContext, NUM, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(48, 48, 51));
      ((ImageView)localObject2).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          MediaController.getInstance().playPreviousMessage();
        }
      });
      localObject2 = this.buttons;
      localObject3 = new ImageView(paramContext);
      this.playButton = ((ImageView)localObject3);
      localObject2[2] = localObject3;
      this.playButton.setScaleType(ImageView.ScaleType.CENTER);
      this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(paramContext, NUM, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
      ((FrameLayout)localObject1).addView(this.playButton, LayoutHelper.createFrame(48, 48, 51));
      this.playButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (MediaController.getInstance().isDownloadingCurrentMessage()) {}
          for (;;)
          {
            return;
            if (MediaController.getInstance().isMessagePaused()) {
              MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
              MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
            }
          }
        }
      });
      localObject3 = this.buttons;
      localObject2 = new ImageView(paramContext);
      localObject3[3] = localObject2;
      ((ImageView)localObject2).setScaleType(ImageView.ScaleType.CENTER);
      ((ImageView)localObject2).setImageDrawable(Theme.createSimpleSelectorDrawable(paramContext, NUM, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(48, 48, 51));
      ((ImageView)localObject2).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          MediaController.getInstance().playNextMessage();
        }
      });
      localObject2 = this.buttons;
      localObject3 = new ImageView(paramContext);
      this.repeatButton = ((ImageView)localObject3);
      localObject2[4] = localObject3;
      this.repeatButton.setScaleType(ImageView.ScaleType.CENTER);
      this.repeatButton.setPadding(0, 0, AndroidUtilities.dp(8.0F), 0);
      ((FrameLayout)localObject1).addView(this.repeatButton, LayoutHelper.createFrame(50, 48, 51));
      this.repeatButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          SharedConfig.toggleRepeatMode();
          AudioPlayerAlert.this.updateRepeatButton();
        }
      });
      this.listView = new RecyclerListView(paramContext)
      {
        boolean ignoreLayout;
        
        protected boolean allowSelectChildAtPosition(float paramAnonymousFloat1, float paramAnonymousFloat2)
        {
          AudioPlayerAlert.this.playerLayout.getY();
          paramAnonymousFloat1 = AudioPlayerAlert.this.playerLayout.getMeasuredHeight();
          if ((AudioPlayerAlert.this.playerLayout == null) || (paramAnonymousFloat2 > AudioPlayerAlert.this.playerLayout.getY() + AudioPlayerAlert.this.playerLayout.getMeasuredHeight())) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
        
        public boolean drawChild(Canvas paramAnonymousCanvas, View paramAnonymousView, long paramAnonymousLong)
        {
          paramAnonymousCanvas.save();
          if (AudioPlayerAlert.this.actionBar != null) {}
          for (int i = AudioPlayerAlert.this.actionBar.getMeasuredHeight();; i = 0)
          {
            paramAnonymousCanvas.clipRect(0, i + AndroidUtilities.dp(50.0F), getMeasuredWidth(), getMeasuredHeight());
            boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
            paramAnonymousCanvas.restore();
            return bool;
          }
        }
        
        protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
          if ((AudioPlayerAlert.this.searchOpenPosition != -1) && (!AudioPlayerAlert.this.actionBar.isSearchFieldVisible()))
          {
            this.ignoreLayout = true;
            AudioPlayerAlert.this.layoutManager.scrollToPositionWithOffset(AudioPlayerAlert.this.searchOpenPosition, AudioPlayerAlert.this.searchOpenOffset);
            super.onLayout(false, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
            this.ignoreLayout = false;
            AudioPlayerAlert.access$2402(AudioPlayerAlert.this, -1);
          }
          int k;
          label140:
          int m;
          do
          {
            MessageObject localMessageObject;
            do
            {
              int i;
              do
              {
                return;
                if (!AudioPlayerAlert.this.scrollToSong) {
                  break;
                }
                AudioPlayerAlert.access$4002(AudioPlayerAlert.this, false);
                i = 0;
                localMessageObject = MediaController.getInstance().getPlayingMessageObject();
              } while (localMessageObject == null);
              int j = AudioPlayerAlert.this.listView.getChildCount();
              k = 0;
              m = i;
              if (k < j)
              {
                View localView = AudioPlayerAlert.this.listView.getChildAt(k);
                if ((!(localView instanceof AudioPlayerCell)) || (((AudioPlayerCell)localView).getMessageObject() != localMessageObject)) {
                  break label271;
                }
                m = i;
                if (localView.getBottom() <= getMeasuredHeight()) {
                  m = 1;
                }
              }
            } while (m != 0);
            m = AudioPlayerAlert.this.playlist.indexOf(localMessageObject);
          } while (m < 0);
          this.ignoreLayout = true;
          if (SharedConfig.playOrderReversed) {
            AudioPlayerAlert.this.layoutManager.scrollToPosition(m);
          }
          for (;;)
          {
            super.onLayout(false, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
            this.ignoreLayout = false;
            break;
            break;
            label271:
            k++;
            break label140;
            AudioPlayerAlert.this.layoutManager.scrollToPosition(AudioPlayerAlert.this.playlist.size() - m);
          }
        }
        
        public void requestLayout()
        {
          if (this.ignoreLayout) {}
          for (;;)
          {
            return;
            super.requestLayout();
          }
        }
      };
      this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      this.listView.setClipToPadding(false);
      localObject1 = this.listView;
      localObject3 = new LinearLayoutManager(getContext(), 1, false);
      this.layoutManager = ((LinearLayoutManager)localObject3);
      ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject3);
      this.listView.setHorizontalScrollBarEnabled(false);
      this.listView.setVerticalScrollBarEnabled(false);
      this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
      localObject1 = this.listView;
      paramContext = new ListAdapter(paramContext);
      this.listAdapter = paramContext;
      ((RecyclerListView)localObject1).setAdapter(paramContext);
      this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((paramAnonymousView instanceof AudioPlayerCell)) {
            ((AudioPlayerCell)paramAnonymousView).didPressedButton();
          }
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == 1) && (AudioPlayerAlert.this.searching) && (AudioPlayerAlert.this.searchWas)) {
            AndroidUtilities.hideKeyboard(AudioPlayerAlert.this.getCurrentFocus());
          }
        }
        
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          AudioPlayerAlert.this.updateLayout();
        }
      });
      this.playlist = MediaController.getInstance().getPlaylist();
      this.listAdapter.notifyDataSetChanged();
      this.containerView.addView(this.playerLayout, LayoutHelper.createFrame(-1, 178.0F));
      this.containerView.addView(this.shadow2, LayoutHelper.createFrame(-1, 3.0F));
      this.containerView.addView(this.placeholderImageView, LayoutHelper.createFrame(40, 40.0F, 51, 17.0F, 19.0F, 0.0F, 0.0F));
      this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0F));
      this.containerView.addView(this.actionBar);
      updateTitle(false);
      updateRepeatButton();
      updateShuffleButton();
      return;
      this.currentAccount = UserConfig.selectedAccount;
      break;
      label2526:
      localObject1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
      if (localObject1 != null)
      {
        this.avatarContainer.setTitle(((TLRPC.Chat)localObject1).title);
        this.avatarContainer.setChatAvatar((TLRPC.Chat)localObject1);
        continue;
        label2569:
        localObject1 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(j));
        if (localObject1 != null)
        {
          localObject1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.EncryptedChat)localObject1).user_id));
          if (localObject1 != null)
          {
            this.avatarContainer.setTitle(ContactsController.formatName(((TLRPC.User)localObject1).first_name, ((TLRPC.User)localObject1).last_name));
            this.avatarContainer.setUserAvatar((TLRPC.User)localObject1);
          }
        }
      }
    }
  }
  
  private void checkIfMusicDownloaded(MessageObject paramMessageObject)
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (paramMessageObject.messageOwner.attachPath != null)
    {
      localObject2 = localObject1;
      if (paramMessageObject.messageOwner.attachPath.length() > 0)
      {
        localObject1 = new File(paramMessageObject.messageOwner.attachPath);
        localObject2 = localObject1;
        if (!((File)localObject1).exists()) {
          localObject2 = null;
        }
      }
    }
    localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = FileLoader.getPathToMessage(paramMessageObject.messageOwner);
    }
    int i;
    float f;
    if ((SharedConfig.streamMedia) && ((int)paramMessageObject.getDialogId() != 0) && (paramMessageObject.isMusic()))
    {
      i = 1;
      if ((((File)localObject1).exists()) || (i != 0)) {
        break label189;
      }
      paramMessageObject = paramMessageObject.getFileName();
      DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(paramMessageObject, this);
      localObject2 = ImageLoader.getInstance().getFileProgress(paramMessageObject);
      paramMessageObject = this.progressView;
      if (localObject2 == null) {
        break label183;
      }
      f = ((Float)localObject2).floatValue();
      label145:
      paramMessageObject.setProgress(f, false);
      this.progressView.setVisibility(0);
      this.seekBarView.setVisibility(4);
      this.playButton.setEnabled(false);
    }
    for (;;)
    {
      return;
      i = 0;
      break;
      label183:
      f = 0.0F;
      break label145;
      label189:
      DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
      this.progressView.setVisibility(4);
      this.seekBarView.setVisibility(0);
      this.playButton.setEnabled(true);
    }
  }
  
  private int getCurrentTop()
  {
    int i = 0;
    int j;
    if (this.listView.getChildCount() != 0)
    {
      View localView = this.listView.getChildAt(0);
      RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.listView.findContainingViewHolder(localView);
      if (localHolder != null)
      {
        j = this.listView.getPaddingTop();
        k = i;
        if (localHolder.getAdapterPosition() == 0)
        {
          k = i;
          if (localView.getTop() >= 0) {
            k = localView.getTop();
          }
        }
      }
    }
    for (int k = j - k;; k = 64536) {
      return k;
    }
  }
  
  /* Error */
  private void onSubItemClick(int paramInt)
  {
    // Byte code:
    //   0: invokestatic 181	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   3: invokevirtual 185	org/telegram/messenger/MediaController:getPlayingMessageObject	()Lorg/telegram/messenger/MessageObject;
    //   6: astore_2
    //   7: aload_2
    //   8: ifnull +10 -> 18
    //   11: aload_0
    //   12: getfield 195	org/telegram/ui/Components/AudioPlayerAlert:parentActivity	Lorg/telegram/ui/LaunchActivity;
    //   15: ifnonnull +4 -> 19
    //   18: return
    //   19: iload_1
    //   20: iconst_1
    //   21: if_icmpne +103 -> 124
    //   24: getstatic 825	org/telegram/messenger/UserConfig:selectedAccount	I
    //   27: aload_0
    //   28: getfield 191	org/telegram/ui/Components/AudioPlayerAlert:currentAccount	I
    //   31: if_icmpeq +15 -> 46
    //   34: aload_0
    //   35: getfield 195	org/telegram/ui/Components/AudioPlayerAlert:parentActivity	Lorg/telegram/ui/LaunchActivity;
    //   38: aload_0
    //   39: getfield 191	org/telegram/ui/Components/AudioPlayerAlert:currentAccount	I
    //   42: iconst_1
    //   43: invokevirtual 1062	org/telegram/ui/LaunchActivity:switchToAccount	(IZ)V
    //   46: new 1064	android/os/Bundle
    //   49: dup
    //   50: invokespecial 1065	android/os/Bundle:<init>	()V
    //   53: astore_3
    //   54: aload_3
    //   55: ldc_w 1067
    //   58: iconst_1
    //   59: invokevirtual 1071	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
    //   62: aload_3
    //   63: ldc_w 1073
    //   66: iconst_3
    //   67: invokevirtual 1077	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   70: new 1079	org/telegram/ui/DialogsActivity
    //   73: dup
    //   74: aload_3
    //   75: invokespecial 1082	org/telegram/ui/DialogsActivity:<init>	(Landroid/os/Bundle;)V
    //   78: astore 4
    //   80: new 167	java/util/ArrayList
    //   83: dup
    //   84: invokespecial 170	java/util/ArrayList:<init>	()V
    //   87: astore_3
    //   88: aload_3
    //   89: aload_2
    //   90: invokevirtual 1086	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   93: pop
    //   94: aload 4
    //   96: new 30	org/telegram/ui/Components/AudioPlayerAlert$19
    //   99: dup
    //   100: aload_0
    //   101: aload_3
    //   102: invokespecial 1089	org/telegram/ui/Components/AudioPlayerAlert$19:<init>	(Lorg/telegram/ui/Components/AudioPlayerAlert;Ljava/util/ArrayList;)V
    //   105: invokevirtual 1092	org/telegram/ui/DialogsActivity:setDelegate	(Lorg/telegram/ui/DialogsActivity$DialogsActivityDelegate;)V
    //   108: aload_0
    //   109: getfield 195	org/telegram/ui/Components/AudioPlayerAlert:parentActivity	Lorg/telegram/ui/LaunchActivity;
    //   112: aload 4
    //   114: invokevirtual 1096	org/telegram/ui/LaunchActivity:presentFragment	(Lorg/telegram/ui/ActionBar/BaseFragment;)V
    //   117: aload_0
    //   118: invokevirtual 1099	org/telegram/ui/Components/AudioPlayerAlert:dismiss	()V
    //   121: goto -103 -> 18
    //   124: iload_1
    //   125: iconst_2
    //   126: if_icmpne +270 -> 396
    //   129: aconst_null
    //   130: astore_3
    //   131: aload_2
    //   132: getfield 969	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   135: getfield 974	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   138: invokestatic 1105	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   141: ifne +31 -> 172
    //   144: new 981	java/io/File
    //   147: astore_3
    //   148: aload_3
    //   149: aload_2
    //   150: getfield 969	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   153: getfield 974	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   156: invokespecial 984	java/io/File:<init>	(Ljava/lang/String;)V
    //   159: aload_3
    //   160: invokevirtual 987	java/io/File:exists	()Z
    //   163: istore 5
    //   165: iload 5
    //   167: ifne +891 -> 1058
    //   170: aconst_null
    //   171: astore_3
    //   172: aload_3
    //   173: astore 4
    //   175: aload_3
    //   176: ifnonnull +12 -> 188
    //   179: aload_2
    //   180: getfield 969	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   183: invokestatic 993	org/telegram/messenger/FileLoader:getPathToMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Ljava/io/File;
    //   186: astore 4
    //   188: aload 4
    //   190: invokevirtual 987	java/io/File:exists	()Z
    //   193: ifeq +140 -> 333
    //   196: new 1107	android/content/Intent
    //   199: astore_3
    //   200: aload_3
    //   201: ldc_w 1109
    //   204: invokespecial 1110	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   207: aload_2
    //   208: ifnull +81 -> 289
    //   211: aload_3
    //   212: aload_2
    //   213: invokevirtual 1113	org/telegram/messenger/MessageObject:getMimeType	()Ljava/lang/String;
    //   216: invokevirtual 1117	android/content/Intent:setType	(Ljava/lang/String;)Landroid/content/Intent;
    //   219: pop
    //   220: getstatic 1122	android/os/Build$VERSION:SDK_INT	I
    //   223: istore_1
    //   224: iload_1
    //   225: bipush 24
    //   227: if_icmplt +90 -> 317
    //   230: aload_3
    //   231: ldc_w 1124
    //   234: getstatic 1130	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   237: ldc_w 1132
    //   240: aload 4
    //   242: invokestatic 1138	android/support/v4/content/FileProvider:getUriForFile	(Landroid/content/Context;Ljava/lang/String;Ljava/io/File;)Landroid/net/Uri;
    //   245: invokevirtual 1142	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   248: pop
    //   249: aload_3
    //   250: iconst_1
    //   251: invokevirtual 1146	android/content/Intent:setFlags	(I)Landroid/content/Intent;
    //   254: pop
    //   255: aload_0
    //   256: getfield 195	org/telegram/ui/Components/AudioPlayerAlert:parentActivity	Lorg/telegram/ui/LaunchActivity;
    //   259: aload_3
    //   260: ldc_w 461
    //   263: ldc_w 462
    //   266: invokestatic 423	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   269: invokestatic 1150	android/content/Intent:createChooser	(Landroid/content/Intent;Ljava/lang/CharSequence;)Landroid/content/Intent;
    //   272: sipush 500
    //   275: invokevirtual 1154	org/telegram/ui/LaunchActivity:startActivityForResult	(Landroid/content/Intent;I)V
    //   278: goto -260 -> 18
    //   281: astore_3
    //   282: aload_3
    //   283: invokestatic 1160	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   286: goto -268 -> 18
    //   289: aload_3
    //   290: ldc_w 1162
    //   293: invokevirtual 1117	android/content/Intent:setType	(Ljava/lang/String;)Landroid/content/Intent;
    //   296: pop
    //   297: goto -77 -> 220
    //   300: astore_2
    //   301: aload_3
    //   302: ldc_w 1124
    //   305: aload 4
    //   307: invokestatic 1168	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   310: invokevirtual 1142	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   313: pop
    //   314: goto -59 -> 255
    //   317: aload_3
    //   318: ldc_w 1124
    //   321: aload 4
    //   323: invokestatic 1168	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   326: invokevirtual 1142	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   329: pop
    //   330: goto -75 -> 255
    //   333: new 1170	org/telegram/ui/ActionBar/AlertDialog$Builder
    //   336: astore_3
    //   337: aload_3
    //   338: aload_0
    //   339: getfield 195	org/telegram/ui/Components/AudioPlayerAlert:parentActivity	Lorg/telegram/ui/LaunchActivity;
    //   342: invokespecial 1171	org/telegram/ui/ActionBar/AlertDialog$Builder:<init>	(Landroid/content/Context;)V
    //   345: aload_3
    //   346: ldc_w 1173
    //   349: ldc_w 1174
    //   352: invokestatic 423	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   355: invokevirtual 1177	org/telegram/ui/ActionBar/AlertDialog$Builder:setTitle	(Ljava/lang/CharSequence;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   358: pop
    //   359: aload_3
    //   360: ldc_w 1179
    //   363: ldc_w 1180
    //   366: invokestatic 423	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   369: aconst_null
    //   370: invokevirtual 1184	org/telegram/ui/ActionBar/AlertDialog$Builder:setPositiveButton	(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   373: pop
    //   374: aload_3
    //   375: ldc_w 1186
    //   378: ldc_w 1187
    //   381: invokestatic 423	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   384: invokevirtual 1190	org/telegram/ui/ActionBar/AlertDialog$Builder:setMessage	(Ljava/lang/CharSequence;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   387: pop
    //   388: aload_3
    //   389: invokevirtual 1194	org/telegram/ui/ActionBar/AlertDialog$Builder:show	()Lorg/telegram/ui/ActionBar/AlertDialog;
    //   392: pop
    //   393: goto -375 -> 18
    //   396: iload_1
    //   397: iconst_3
    //   398: if_icmpne +430 -> 828
    //   401: new 1170	org/telegram/ui/ActionBar/AlertDialog$Builder
    //   404: dup
    //   405: aload_0
    //   406: getfield 195	org/telegram/ui/Components/AudioPlayerAlert:parentActivity	Lorg/telegram/ui/LaunchActivity;
    //   409: invokespecial 1171	org/telegram/ui/ActionBar/AlertDialog$Builder:<init>	(Landroid/content/Context;)V
    //   412: astore 6
    //   414: aload 6
    //   416: ldc_w 1173
    //   419: ldc_w 1174
    //   422: invokestatic 423	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   425: invokevirtual 1177	org/telegram/ui/ActionBar/AlertDialog$Builder:setTitle	(Ljava/lang/CharSequence;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   428: pop
    //   429: iconst_1
    //   430: newarray <illegal type>
    //   432: astore 7
    //   434: aload_2
    //   435: invokevirtual 379	org/telegram/messenger/MessageObject:getDialogId	()J
    //   438: l2i
    //   439: istore_1
    //   440: iload_1
    //   441: ifeq +259 -> 700
    //   444: iload_1
    //   445: ifle +306 -> 751
    //   448: aload_0
    //   449: getfield 191	org/telegram/ui/Components/AudioPlayerAlert:currentAccount	I
    //   452: invokestatic 384	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   455: iload_1
    //   456: invokestatic 390	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   459: invokevirtual 394	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   462: astore 4
    //   464: aconst_null
    //   465: astore_3
    //   466: aload 4
    //   468: ifnonnull +10 -> 478
    //   471: aload_3
    //   472: invokestatic 1200	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   475: ifne +225 -> 700
    //   478: aload_0
    //   479: getfield 191	org/telegram/ui/Components/AudioPlayerAlert:currentAccount	I
    //   482: invokestatic 1205	org/telegram/tgnet/ConnectionsManager:getInstance	(I)Lorg/telegram/tgnet/ConnectionsManager;
    //   485: invokevirtual 1208	org/telegram/tgnet/ConnectionsManager:getCurrentTime	()I
    //   488: istore_1
    //   489: aload 4
    //   491: ifnull +21 -> 512
    //   494: aload 4
    //   496: getfield 1211	org/telegram/tgnet/TLRPC$User:id	I
    //   499: aload_0
    //   500: getfield 191	org/telegram/ui/Components/AudioPlayerAlert:currentAccount	I
    //   503: invokestatic 1214	org/telegram/messenger/UserConfig:getInstance	(I)Lorg/telegram/messenger/UserConfig;
    //   506: invokevirtual 1217	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   509: if_icmpne +7 -> 516
    //   512: aload_3
    //   513: ifnull +187 -> 700
    //   516: aload_2
    //   517: getfield 969	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   520: getfield 1221	org/telegram/tgnet/TLRPC$Message:action	Lorg/telegram/tgnet/TLRPC$MessageAction;
    //   523: ifnull +16 -> 539
    //   526: aload_2
    //   527: getfield 969	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   530: getfield 1221	org/telegram/tgnet/TLRPC$Message:action	Lorg/telegram/tgnet/TLRPC$MessageAction;
    //   533: instanceof 1223
    //   536: ifeq +164 -> 700
    //   539: aload_2
    //   540: invokevirtual 1226	org/telegram/messenger/MessageObject:isOut	()Z
    //   543: ifeq +157 -> 700
    //   546: iload_1
    //   547: aload_2
    //   548: getfield 969	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   551: getfield 1229	org/telegram/tgnet/TLRPC$Message:date	I
    //   554: isub
    //   555: ldc_w 1230
    //   558: if_icmpgt +142 -> 700
    //   561: new 542	android/widget/FrameLayout
    //   564: dup
    //   565: aload_0
    //   566: getfield 195	org/telegram/ui/Components/AudioPlayerAlert:parentActivity	Lorg/telegram/ui/LaunchActivity;
    //   569: invokespecial 543	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   572: astore 8
    //   574: new 1232	org/telegram/ui/Cells/CheckBoxCell
    //   577: dup
    //   578: aload_0
    //   579: getfield 195	org/telegram/ui/Components/AudioPlayerAlert:parentActivity	Lorg/telegram/ui/LaunchActivity;
    //   582: iconst_1
    //   583: invokespecial 1235	org/telegram/ui/Cells/CheckBoxCell:<init>	(Landroid/content/Context;I)V
    //   586: astore 9
    //   588: aload 9
    //   590: iconst_0
    //   591: invokestatic 1239	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   594: invokevirtual 1242	org/telegram/ui/Cells/CheckBoxCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   597: aload_3
    //   598: ifnull +175 -> 773
    //   601: aload 9
    //   603: ldc_w 1244
    //   606: ldc_w 1245
    //   609: invokestatic 423	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   612: ldc_w 1247
    //   615: iconst_0
    //   616: iconst_0
    //   617: invokevirtual 1251	org/telegram/ui/Cells/CheckBoxCell:setText	(Ljava/lang/String;Ljava/lang/String;ZZ)V
    //   620: getstatic 1254	org/telegram/messenger/LocaleController:isRTL	Z
    //   623: ifeq +184 -> 807
    //   626: ldc_w 690
    //   629: invokestatic 472	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   632: istore_1
    //   633: getstatic 1254	org/telegram/messenger/LocaleController:isRTL	Z
    //   636: ifeq +181 -> 817
    //   639: ldc_w 645
    //   642: invokestatic 472	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   645: istore 10
    //   647: aload 9
    //   649: iload_1
    //   650: iconst_0
    //   651: iload 10
    //   653: iconst_0
    //   654: invokevirtual 1255	org/telegram/ui/Cells/CheckBoxCell:setPadding	(IIII)V
    //   657: aload 8
    //   659: aload 9
    //   661: iconst_m1
    //   662: ldc_w 466
    //   665: bipush 51
    //   667: fconst_0
    //   668: fconst_0
    //   669: fconst_0
    //   670: fconst_0
    //   671: invokestatic 433	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   674: invokevirtual 607	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   677: aload 9
    //   679: new 34	org/telegram/ui/Components/AudioPlayerAlert$20
    //   682: dup
    //   683: aload_0
    //   684: aload 7
    //   686: invokespecial 1258	org/telegram/ui/Components/AudioPlayerAlert$20:<init>	(Lorg/telegram/ui/Components/AudioPlayerAlert;[Z)V
    //   689: invokevirtual 1259	org/telegram/ui/Cells/CheckBoxCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   692: aload 6
    //   694: aload 8
    //   696: invokevirtual 1263	org/telegram/ui/ActionBar/AlertDialog$Builder:setView	(Landroid/view/View;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   699: pop
    //   700: aload 6
    //   702: ldc_w 1179
    //   705: ldc_w 1180
    //   708: invokestatic 423	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   711: new 36	org/telegram/ui/Components/AudioPlayerAlert$21
    //   714: dup
    //   715: aload_0
    //   716: aload_2
    //   717: aload 7
    //   719: invokespecial 1266	org/telegram/ui/Components/AudioPlayerAlert$21:<init>	(Lorg/telegram/ui/Components/AudioPlayerAlert;Lorg/telegram/messenger/MessageObject;[Z)V
    //   722: invokevirtual 1184	org/telegram/ui/ActionBar/AlertDialog$Builder:setPositiveButton	(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   725: pop
    //   726: aload 6
    //   728: ldc_w 1268
    //   731: ldc_w 1269
    //   734: invokestatic 423	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   737: aconst_null
    //   738: invokevirtual 1272	org/telegram/ui/ActionBar/AlertDialog$Builder:setNegativeButton	(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
    //   741: pop
    //   742: aload 6
    //   744: invokevirtual 1194	org/telegram/ui/ActionBar/AlertDialog$Builder:show	()Lorg/telegram/ui/ActionBar/AlertDialog;
    //   747: pop
    //   748: goto -730 -> 18
    //   751: aconst_null
    //   752: astore 4
    //   754: aload_0
    //   755: getfield 191	org/telegram/ui/Components/AudioPlayerAlert:currentAccount	I
    //   758: invokestatic 384	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   761: iload_1
    //   762: ineg
    //   763: invokestatic 390	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   766: invokevirtual 829	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   769: astore_3
    //   770: goto -304 -> 466
    //   773: aload 9
    //   775: ldc_w 1274
    //   778: ldc_w 1275
    //   781: iconst_1
    //   782: anewarray 1277	java/lang/Object
    //   785: dup
    //   786: iconst_0
    //   787: aload 4
    //   789: invokestatic 1283	org/telegram/messenger/UserObject:getFirstName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   792: aastore
    //   793: invokestatic 1287	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   796: ldc_w 1247
    //   799: iconst_0
    //   800: iconst_0
    //   801: invokevirtual 1251	org/telegram/ui/Cells/CheckBoxCell:setText	(Ljava/lang/String;Ljava/lang/String;ZZ)V
    //   804: goto -184 -> 620
    //   807: ldc_w 645
    //   810: invokestatic 472	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   813: istore_1
    //   814: goto -181 -> 633
    //   817: ldc_w 690
    //   820: invokestatic 472	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   823: istore 10
    //   825: goto -178 -> 647
    //   828: iload_1
    //   829: iconst_4
    //   830: if_icmpne -812 -> 18
    //   833: getstatic 825	org/telegram/messenger/UserConfig:selectedAccount	I
    //   836: aload_0
    //   837: getfield 191	org/telegram/ui/Components/AudioPlayerAlert:currentAccount	I
    //   840: if_icmpeq +15 -> 855
    //   843: aload_0
    //   844: getfield 195	org/telegram/ui/Components/AudioPlayerAlert:parentActivity	Lorg/telegram/ui/LaunchActivity;
    //   847: aload_0
    //   848: getfield 191	org/telegram/ui/Components/AudioPlayerAlert:currentAccount	I
    //   851: iconst_1
    //   852: invokevirtual 1062	org/telegram/ui/LaunchActivity:switchToAccount	(IZ)V
    //   855: new 1064	android/os/Bundle
    //   858: dup
    //   859: invokespecial 1065	android/os/Bundle:<init>	()V
    //   862: astore_3
    //   863: aload_2
    //   864: invokevirtual 379	org/telegram/messenger/MessageObject:getDialogId	()J
    //   867: lstore 11
    //   869: lload 11
    //   871: l2i
    //   872: istore 10
    //   874: lload 11
    //   876: bipush 32
    //   878: lshr
    //   879: l2i
    //   880: istore_1
    //   881: iload 10
    //   883: ifeq +160 -> 1043
    //   886: iload_1
    //   887: iconst_1
    //   888: if_icmpne +65 -> 953
    //   891: aload_3
    //   892: ldc_w 1289
    //   895: iload 10
    //   897: invokevirtual 1077	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   900: aload_3
    //   901: ldc_w 1291
    //   904: aload_2
    //   905: invokevirtual 1294	org/telegram/messenger/MessageObject:getId	()I
    //   908: invokevirtual 1077	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   911: aload_0
    //   912: getfield 191	org/telegram/ui/Components/AudioPlayerAlert:currentAccount	I
    //   915: invokestatic 253	org/telegram/messenger/NotificationCenter:getInstance	(I)Lorg/telegram/messenger/NotificationCenter;
    //   918: getstatic 1297	org/telegram/messenger/NotificationCenter:closeChats	I
    //   921: iconst_0
    //   922: anewarray 1277	java/lang/Object
    //   925: invokevirtual 1301	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   928: aload_0
    //   929: getfield 195	org/telegram/ui/Components/AudioPlayerAlert:parentActivity	Lorg/telegram/ui/LaunchActivity;
    //   932: new 1303	org/telegram/ui/ChatActivity
    //   935: dup
    //   936: aload_3
    //   937: invokespecial 1304	org/telegram/ui/ChatActivity:<init>	(Landroid/os/Bundle;)V
    //   940: iconst_0
    //   941: iconst_0
    //   942: invokevirtual 1307	org/telegram/ui/LaunchActivity:presentFragment	(Lorg/telegram/ui/ActionBar/BaseFragment;ZZ)Z
    //   945: pop
    //   946: aload_0
    //   947: invokevirtual 1099	org/telegram/ui/Components/AudioPlayerAlert:dismiss	()V
    //   950: goto -932 -> 18
    //   953: iload 10
    //   955: ifle +15 -> 970
    //   958: aload_3
    //   959: ldc_w 1308
    //   962: iload 10
    //   964: invokevirtual 1077	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   967: goto -67 -> 900
    //   970: iload 10
    //   972: ifge -72 -> 900
    //   975: aload_0
    //   976: getfield 191	org/telegram/ui/Components/AudioPlayerAlert:currentAccount	I
    //   979: invokestatic 384	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   982: iload 10
    //   984: ineg
    //   985: invokestatic 390	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   988: invokevirtual 829	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   991: astore 4
    //   993: iload 10
    //   995: istore_1
    //   996: aload 4
    //   998: ifnull +33 -> 1031
    //   1001: iload 10
    //   1003: istore_1
    //   1004: aload 4
    //   1006: getfield 1312	org/telegram/tgnet/TLRPC$Chat:migrated_to	Lorg/telegram/tgnet/TLRPC$InputChannel;
    //   1009: ifnull +22 -> 1031
    //   1012: aload_3
    //   1013: ldc_w 1313
    //   1016: iload 10
    //   1018: invokevirtual 1077	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   1021: aload 4
    //   1023: getfield 1312	org/telegram/tgnet/TLRPC$Chat:migrated_to	Lorg/telegram/tgnet/TLRPC$InputChannel;
    //   1026: getfield 1318	org/telegram/tgnet/TLRPC$InputChannel:channel_id	I
    //   1029: ineg
    //   1030: istore_1
    //   1031: aload_3
    //   1032: ldc_w 1289
    //   1035: iload_1
    //   1036: ineg
    //   1037: invokevirtual 1077	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   1040: goto -140 -> 900
    //   1043: aload_3
    //   1044: ldc_w 1320
    //   1047: iload_1
    //   1048: invokevirtual 1077	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   1051: goto -151 -> 900
    //   1054: astore_3
    //   1055: goto -773 -> 282
    //   1058: goto -886 -> 172
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1061	0	this	AudioPlayerAlert
    //   0	1061	1	paramInt	int
    //   6	207	2	localMessageObject	MessageObject
    //   300	605	2	localException1	Exception
    //   53	207	3	localObject1	Object
    //   281	37	3	localException2	Exception
    //   336	708	3	localObject2	Object
    //   1054	1	3	localException3	Exception
    //   78	944	4	localObject3	Object
    //   163	3	5	bool	boolean
    //   412	331	6	localBuilder	org.telegram.ui.ActionBar.AlertDialog.Builder
    //   432	286	7	arrayOfBoolean	boolean[]
    //   572	123	8	localFrameLayout	FrameLayout
    //   586	188	9	localCheckBoxCell	CheckBoxCell
    //   645	372	10	i	int
    //   867	8	11	l	long
    // Exception table:
    //   from	to	target	type
    //   131	159	281	java/lang/Exception
    //   179	188	281	java/lang/Exception
    //   188	207	281	java/lang/Exception
    //   211	220	281	java/lang/Exception
    //   220	224	281	java/lang/Exception
    //   255	278	281	java/lang/Exception
    //   289	297	281	java/lang/Exception
    //   301	314	281	java/lang/Exception
    //   317	330	281	java/lang/Exception
    //   333	393	281	java/lang/Exception
    //   230	255	300	java/lang/Exception
    //   159	165	1054	java/lang/Exception
  }
  
  private void updateLayout()
  {
    if (this.listView.getChildCount() <= 0) {
      return;
    }
    Object localObject = this.listView.getChildAt(0);
    RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.listView.findContainingViewHolder((View)localObject);
    int i = ((View)localObject).getTop();
    if ((i > 0) && (localHolder != null) && (localHolder.getAdapterPosition() == 0))
    {
      label52:
      if ((this.searchWas) || (this.searching)) {
        i = 0;
      }
      if (this.scrollOffsetY != i)
      {
        localObject = this.listView;
        this.scrollOffsetY = i;
        ((RecyclerListView)localObject).setTopGlowOffset(i);
        this.playerLayout.setTranslationY(Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY));
        this.placeholderImageView.setTranslationY(Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY));
        this.shadow2.setTranslationY(Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY) + this.playerLayout.getMeasuredHeight());
        this.containerView.invalidate();
        if (((!this.inFullSize) || (this.scrollOffsetY > this.actionBar.getMeasuredHeight())) && (!this.searchWas)) {
          break label381;
        }
        if (this.actionBar.getTag() == null)
        {
          if (this.actionBarAnimation != null) {
            this.actionBarAnimation.cancel();
          }
          this.actionBar.setTag(Integer.valueOf(1));
          this.actionBarAnimation = new AnimatorSet();
          this.actionBarAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.shadow, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.shadow2, "alpha", new float[] { 1.0F }) });
          this.actionBarAnimation.setDuration(180L);
          this.actionBarAnimation.start();
        }
      }
    }
    for (;;)
    {
      this.startTranslation = Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY);
      this.panelStartTranslation = Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY);
      break;
      i = 0;
      break label52;
      label381:
      if (this.actionBar.getTag() != null)
      {
        if (this.actionBarAnimation != null) {
          this.actionBarAnimation.cancel();
        }
        this.actionBar.setTag(null);
        this.actionBarAnimation = new AnimatorSet();
        this.actionBarAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.shadow, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.shadow2, "alpha", new float[] { 0.0F }) });
        this.actionBarAnimation.setDuration(180L);
        this.actionBarAnimation.start();
      }
    }
  }
  
  private void updateProgress(MessageObject paramMessageObject)
  {
    if (this.seekBarView != null)
    {
      if (!this.seekBarView.isDragging())
      {
        this.seekBarView.setProgress(paramMessageObject.audioProgress);
        this.seekBarView.setBufferedProgress(paramMessageObject.bufferedProgress);
      }
      if (this.lastTime != paramMessageObject.audioProgressSec)
      {
        this.lastTime = paramMessageObject.audioProgressSec;
        this.timeTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(paramMessageObject.audioProgressSec / 60), Integer.valueOf(paramMessageObject.audioProgressSec % 60) }));
      }
    }
  }
  
  private void updateRepeatButton()
  {
    int i = SharedConfig.repeatMode;
    if (i == 0)
    {
      this.repeatButton.setImageResource(NUM);
      this.repeatButton.setTag("player_button");
      this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_button"), PorterDuff.Mode.MULTIPLY));
    }
    for (;;)
    {
      return;
      if (i == 1)
      {
        this.repeatButton.setImageResource(NUM);
        this.repeatButton.setTag("player_buttonActive");
        this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), PorterDuff.Mode.MULTIPLY));
      }
      else if (i == 2)
      {
        this.repeatButton.setImageResource(NUM);
        this.repeatButton.setTag("player_buttonActive");
        this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), PorterDuff.Mode.MULTIPLY));
      }
    }
  }
  
  private void updateShuffleButton()
  {
    Drawable localDrawable;
    if (SharedConfig.shuffleMusic)
    {
      localObject = getContext().getResources().getDrawable(NUM).mutate();
      ((Drawable)localObject).setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), PorterDuff.Mode.MULTIPLY));
      this.shuffleButton.setIcon((Drawable)localObject);
      localDrawable = this.playOrderButtons[0];
      if (!SharedConfig.playOrderReversed) {
        break label199;
      }
      localObject = "player_buttonActive";
      label68:
      localDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor((String)localObject), PorterDuff.Mode.MULTIPLY));
      localDrawable = this.playOrderButtons[1];
      if (!SharedConfig.shuffleMusic) {
        break label206;
      }
    }
    label199:
    label206:
    for (Object localObject = "player_buttonActive";; localObject = "player_button")
    {
      localDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor((String)localObject), PorterDuff.Mode.MULTIPLY));
      return;
      localObject = getContext().getResources().getDrawable(NUM).mutate();
      if (SharedConfig.playOrderReversed) {
        ((Drawable)localObject).setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), PorterDuff.Mode.MULTIPLY));
      }
      for (;;)
      {
        this.shuffleButton.setIcon((Drawable)localObject);
        break;
        ((Drawable)localObject).setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_button"), PorterDuff.Mode.MULTIPLY));
      }
      localObject = "player_button";
      break label68;
    }
  }
  
  private void updateTitle(boolean paramBoolean)
  {
    Object localObject1 = MediaController.getInstance().getPlayingMessageObject();
    if (((localObject1 == null) && (paramBoolean)) || ((localObject1 != null) && (!((MessageObject)localObject1).isMusic())))
    {
      dismiss();
      return;
    }
    label65:
    label116:
    Object localObject2;
    label195:
    int i;
    if (localObject1 != null)
    {
      if (((MessageObject)localObject1).eventId == 0L) {
        break label259;
      }
      this.hasOptions = false;
      this.menuItem.setVisibility(4);
      this.optionsButton.setVisibility(4);
      checkIfMusicDownloaded((MessageObject)localObject1);
      updateProgress((MessageObject)localObject1);
      if (!MediaController.getInstance().isMessagePaused()) {
        break label293;
      }
      this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(this.playButton.getContext(), NUM, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
      localObject2 = ((MessageObject)localObject1).getMusicTitle();
      String str = ((MessageObject)localObject1).getMusicAuthor();
      this.titleTextView.setText((CharSequence)localObject2);
      this.authorTextView.setText(str);
      this.actionBar.setTitle((CharSequence)localObject2);
      this.actionBar.setSubtitle(str);
      localObject2 = MediaController.getInstance().getAudioInfo();
      if ((localObject2 == null) || (((AudioInfo)localObject2).getCover() == null)) {
        break label328;
      }
      this.hasNoCover = false;
      this.placeholderImageView.setImageBitmap(((AudioInfo)localObject2).getCover());
      if (this.durationTextView == null) {
        break label349;
      }
      i = ((MessageObject)localObject1).getDuration();
      localObject2 = this.durationTextView;
      if (i == 0) {
        break label351;
      }
    }
    label259:
    label293:
    label328:
    label349:
    label351:
    for (localObject1 = String.format("%d:%02d", new Object[] { Integer.valueOf(i / 60), Integer.valueOf(i % 60) });; localObject1 = "-:--")
    {
      ((TextView)localObject2).setText((CharSequence)localObject1);
      break;
      break;
      this.hasOptions = true;
      if (!this.actionBar.isSearchFieldVisible()) {
        this.menuItem.setVisibility(0);
      }
      this.optionsButton.setVisibility(0);
      break label65;
      this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(this.playButton.getContext(), NUM, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
      break label116;
      this.hasNoCover = true;
      this.placeholderImageView.invalidate();
      this.placeholderImageView.setImageDrawable(null);
      break label195;
      break;
    }
  }
  
  protected boolean canDismissWithSwipe()
  {
    return false;
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    MessageObject localMessageObject;
    if ((paramInt1 == NotificationCenter.messagePlayingDidStarted) || (paramInt1 == NotificationCenter.messagePlayingPlayStateChanged) || (paramInt1 == NotificationCenter.messagePlayingDidReset))
    {
      if ((paramInt1 == NotificationCenter.messagePlayingDidReset) && (((Boolean)paramVarArgs[1]).booleanValue())) {}
      for (boolean bool = true;; bool = false)
      {
        updateTitle(bool);
        if ((paramInt1 != NotificationCenter.messagePlayingDidReset) && (paramInt1 != NotificationCenter.messagePlayingPlayStateChanged)) {
          break;
        }
        paramInt2 = this.listView.getChildCount();
        for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
        {
          paramVarArgs = this.listView.getChildAt(paramInt1);
          if ((paramVarArgs instanceof AudioPlayerCell))
          {
            paramVarArgs = (AudioPlayerCell)paramVarArgs;
            localMessageObject = paramVarArgs.getMessageObject();
            if ((localMessageObject != null) && ((localMessageObject.isVoice()) || (localMessageObject.isMusic()))) {
              paramVarArgs.updateButtonState(false);
            }
          }
        }
      }
      if ((paramInt1 == NotificationCenter.messagePlayingDidStarted) && (((MessageObject)paramVarArgs[0]).eventId == 0L)) {}
    }
    for (;;)
    {
      return;
      paramInt2 = this.listView.getChildCount();
      for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
      {
        paramVarArgs = this.listView.getChildAt(paramInt1);
        if ((paramVarArgs instanceof AudioPlayerCell))
        {
          paramVarArgs = (AudioPlayerCell)paramVarArgs;
          localMessageObject = paramVarArgs.getMessageObject();
          if ((localMessageObject != null) && ((localMessageObject.isVoice()) || (localMessageObject.isMusic()))) {
            paramVarArgs.updateButtonState(false);
          }
        }
      }
      continue;
      if (paramInt1 == NotificationCenter.messagePlayingProgressDidChanged)
      {
        paramVarArgs = MediaController.getInstance().getPlayingMessageObject();
        if ((paramVarArgs != null) && (paramVarArgs.isMusic())) {
          updateProgress(paramVarArgs);
        }
      }
      else if (paramInt1 == NotificationCenter.musicDidLoaded)
      {
        this.playlist = MediaController.getInstance().getPlaylist();
        this.listAdapter.notifyDataSetChanged();
      }
    }
  }
  
  public void dismiss()
  {
    super.dismiss();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.musicDidLoaded);
    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
  }
  
  @Keep
  public float getFullAnimationProgress()
  {
    return this.fullAnimationProgress;
  }
  
  public int getObserverTag()
  {
    return this.TAG;
  }
  
  public void onBackPressed()
  {
    if ((this.actionBar != null) && (this.actionBar.isSearchFieldVisible())) {
      this.actionBar.closeSearchField();
    }
    for (;;)
    {
      return;
      super.onBackPressed();
    }
  }
  
  public void onFailedDownload(String paramString) {}
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.progressView.setProgress(paramFloat, true);
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean) {}
  
  public void onSuccessDownload(String paramString) {}
  
  @Keep
  public void setFullAnimationProgress(float paramFloat)
  {
    this.fullAnimationProgress = paramFloat;
    this.placeholderImageView.setRoundRadius(AndroidUtilities.dp(20.0F * (1.0F - this.fullAnimationProgress)));
    paramFloat = 1.0F + this.thumbMaxScale * this.fullAnimationProgress;
    this.placeholderImageView.setScaleX(paramFloat);
    this.placeholderImageView.setScaleY(paramFloat);
    this.placeholderImageView.getTranslationY();
    this.placeholderImageView.setTranslationX(this.thumbMaxX * this.fullAnimationProgress);
    this.placeholderImageView.setTranslationY(this.startTranslation + (this.endTranslation - this.startTranslation) * this.fullAnimationProgress);
    this.playerLayout.setTranslationY(this.panelStartTranslation + (this.panelEndTranslation - this.panelStartTranslation) * this.fullAnimationProgress);
    this.shadow2.setTranslationY(this.panelStartTranslation + (this.panelEndTranslation - this.panelStartTranslation) * this.fullAnimationProgress + this.playerLayout.getMeasuredHeight());
    this.menuItem.setAlpha(this.fullAnimationProgress);
    this.searchItem.setAlpha(1.0F - this.fullAnimationProgress);
    this.avatarContainer.setAlpha(1.0F - this.fullAnimationProgress);
    this.actionBar.getTitleTextView().setAlpha(this.fullAnimationProgress);
    this.actionBar.getSubtitleTextView().setAlpha(this.fullAnimationProgress);
  }
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context context;
    private ArrayList<MessageObject> searchResult = new ArrayList();
    private Timer searchTimer;
    
    public ListAdapter(Context paramContext)
    {
      this.context = paramContext;
    }
    
    private void processSearch(final String paramString)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          final ArrayList localArrayList = new ArrayList(AudioPlayerAlert.this.playlist);
          Utilities.searchQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              String str = AudioPlayerAlert.ListAdapter.2.this.val$query.trim().toLowerCase();
              if (str.length() == 0) {
                AudioPlayerAlert.ListAdapter.this.updateSearchResults(new ArrayList());
              }
              for (;;)
              {
                return;
                Object localObject1 = LocaleController.getInstance().getTranslitString(str);
                Object localObject2;
                if (!str.equals(localObject1))
                {
                  localObject2 = localObject1;
                  if (((String)localObject1).length() != 0) {}
                }
                else
                {
                  localObject2 = null;
                }
                int i;
                ArrayList localArrayList;
                if (localObject2 != null)
                {
                  i = 1;
                  localObject1 = new String[i + 1];
                  localObject1[0] = str;
                  if (localObject2 != null) {
                    localObject1[1] = localObject2;
                  }
                  localArrayList = new ArrayList();
                  i = 0;
                }
                for (;;)
                {
                  if (i < localArrayList.size())
                  {
                    MessageObject localMessageObject = (MessageObject)localArrayList.get(i);
                    int j = 0;
                    for (;;)
                    {
                      if (j < localObject1.length)
                      {
                        str = localObject1[j];
                        localObject2 = localMessageObject.getDocumentName();
                        if ((localObject2 == null) || (((String)localObject2).length() == 0))
                        {
                          j++;
                          continue;
                          i = 0;
                          break;
                        }
                        if (!((String)localObject2).toLowerCase().contains(str)) {
                          break label200;
                        }
                        localArrayList.add(localMessageObject);
                      }
                    }
                    label194:
                    i++;
                    continue;
                    label200:
                    label223:
                    boolean bool1;
                    boolean bool2;
                    if (localMessageObject.type == 0)
                    {
                      localObject2 = localMessageObject.messageOwner.media.webpage.document;
                      bool1 = false;
                      bool2 = false;
                    }
                    for (int k = 0;; k++)
                    {
                      boolean bool3 = bool1;
                      if (k < ((TLRPC.Document)localObject2).attributes.size())
                      {
                        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject2).attributes.get(k);
                        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio)) {
                          continue;
                        }
                        if (localDocumentAttribute.performer != null) {
                          bool2 = localDocumentAttribute.performer.toLowerCase().contains(str);
                        }
                        bool3 = bool2;
                        if (!bool2)
                        {
                          bool3 = bool2;
                          if (localDocumentAttribute.title != null) {
                            bool3 = localDocumentAttribute.title.toLowerCase().contains(str);
                          }
                        }
                      }
                      if (!bool3) {
                        break;
                      }
                      localArrayList.add(localMessageObject);
                      break label194;
                      localObject2 = localMessageObject.messageOwner.media.document;
                      break label223;
                    }
                  }
                }
                AudioPlayerAlert.ListAdapter.this.updateSearchResults(localArrayList);
              }
            }
          });
        }
      });
    }
    
    private void updateSearchResults(final ArrayList<MessageObject> paramArrayList)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          AudioPlayerAlert.access$2202(AudioPlayerAlert.this, true);
          AudioPlayerAlert.ListAdapter.access$5102(AudioPlayerAlert.ListAdapter.this, paramArrayList);
          AudioPlayerAlert.ListAdapter.this.notifyDataSetChanged();
          AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
        }
      });
    }
    
    public int getItemCount()
    {
      int i;
      if (AudioPlayerAlert.this.searchWas) {
        i = this.searchResult.size();
      }
      for (;;)
      {
        return i;
        if (AudioPlayerAlert.this.searching) {
          i = AudioPlayerAlert.this.playlist.size();
        } else {
          i = AudioPlayerAlert.this.playlist.size() + 1;
        }
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 1;
      int j = i;
      if (!AudioPlayerAlert.this.searchWas)
      {
        if (!AudioPlayerAlert.this.searching) {
          break label28;
        }
        j = i;
      }
      for (;;)
      {
        return j;
        label28:
        j = i;
        if (paramInt == 0) {
          j = 0;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((AudioPlayerAlert.this.searchWas) || (paramViewHolder.getAdapterPosition() > 0)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramViewHolder.getItemViewType() == 1)
      {
        paramViewHolder = (AudioPlayerCell)paramViewHolder.itemView;
        if (!AudioPlayerAlert.this.searchWas) {
          break label42;
        }
        paramViewHolder.setMessageObject((MessageObject)this.searchResult.get(paramInt));
      }
      for (;;)
      {
        return;
        label42:
        if (AudioPlayerAlert.this.searching)
        {
          if (SharedConfig.playOrderReversed) {
            paramViewHolder.setMessageObject((MessageObject)AudioPlayerAlert.this.playlist.get(paramInt));
          } else {
            paramViewHolder.setMessageObject((MessageObject)AudioPlayerAlert.this.playlist.get(AudioPlayerAlert.this.playlist.size() - paramInt - 1));
          }
        }
        else if (paramInt > 0) {
          if (SharedConfig.playOrderReversed) {
            paramViewHolder.setMessageObject((MessageObject)AudioPlayerAlert.this.playlist.get(paramInt - 1));
          } else {
            paramViewHolder.setMessageObject((MessageObject)AudioPlayerAlert.this.playlist.get(AudioPlayerAlert.this.playlist.size() - paramInt));
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new AudioPlayerCell(this.context);
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new View(this.context);
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(178.0F)));
      }
    }
    
    public void search(final String paramString)
    {
      try
      {
        if (this.searchTimer != null) {
          this.searchTimer.cancel();
        }
        if (paramString == null)
        {
          this.searchResult.clear();
          notifyDataSetChanged();
          return;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
          continue;
          this.searchTimer = new Timer();
          this.searchTimer.schedule(new TimerTask()
          {
            public void run()
            {
              try
              {
                AudioPlayerAlert.ListAdapter.this.searchTimer.cancel();
                AudioPlayerAlert.ListAdapter.access$4802(AudioPlayerAlert.ListAdapter.this, null);
                AudioPlayerAlert.ListAdapter.this.processSearch(paramString);
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
          }, 200L, 300L);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/AudioPlayerAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */