package org.telegram.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.style.CharacterStyle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogCell.CustomDialog;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class ThemePreviewActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ActionBar actionBar2;
  private boolean applied;
  private Theme.ThemeInfo applyingTheme;
  private DialogsAdapter dialogsAdapter;
  private View dotsContainer;
  private ImageView floatingButton;
  private RecyclerListView listView;
  private RecyclerListView listView2;
  private MessagesAdapter messagesAdapter;
  private FrameLayout page1;
  private SizeNotifierFrameLayout page2;
  private File themeFile;
  
  public ThemePreviewActivity(File paramFile, Theme.ThemeInfo paramThemeInfo)
  {
    this.swipeBackEnabled = false;
    this.applyingTheme = paramThemeInfo;
    this.themeFile = paramFile;
  }
  
  public View createView(Context paramContext)
  {
    this.page1 = new FrameLayout(paramContext);
    this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public boolean canCollapseSearch()
      {
        return true;
      }
      
      public void onSearchCollapse() {}
      
      public void onSearchExpand() {}
      
      public void onTextChanged(EditText paramAnonymousEditText) {}
    }).getSearchField().setHint(LocaleController.getString("Search", NUM));
    this.actionBar.setBackButtonDrawable(new MenuDrawable());
    this.actionBar.setAddToContainer(false);
    this.actionBar.setTitle(LocaleController.getString("ThemePreview", NUM));
    this.page1 = new FrameLayout(paramContext)
    {
      protected boolean drawChild(Canvas paramAnonymousCanvas, View paramAnonymousView, long paramAnonymousLong)
      {
        boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
        if ((paramAnonymousView == ThemePreviewActivity.this.actionBar) && (ThemePreviewActivity.this.parentLayout != null))
        {
          paramAnonymousView = ThemePreviewActivity.this.parentLayout;
          if (ThemePreviewActivity.this.actionBar.getVisibility() != 0) {
            break label73;
          }
        }
        label73:
        for (int i = ThemePreviewActivity.this.actionBar.getMeasuredHeight();; i = 0)
        {
          paramAnonymousView.drawHeaderShadow(paramAnonymousCanvas, i);
          return bool;
        }
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        int i = View.MeasureSpec.getSize(paramAnonymousInt1);
        int j = View.MeasureSpec.getSize(paramAnonymousInt2);
        setMeasuredDimension(i, j);
        measureChildWithMargins(ThemePreviewActivity.this.actionBar, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
        int k = ThemePreviewActivity.this.actionBar.getMeasuredHeight();
        int m = j;
        if (ThemePreviewActivity.this.actionBar.getVisibility() == 0) {
          m = j - k;
        }
        ((FrameLayout.LayoutParams)ThemePreviewActivity.this.listView.getLayoutParams()).topMargin = k;
        ThemePreviewActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(m, NUM));
        measureChildWithMargins(ThemePreviewActivity.this.floatingButton, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
      }
    };
    this.page1.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(true);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    Object localObject1 = this.listView;
    final Object localObject2;
    label528:
    float f1;
    label541:
    int j;
    label550:
    float f2;
    label561:
    float f3;
    if (LocaleController.isRTL)
    {
      i = 1;
      ((RecyclerListView)localObject1).setVerticalScrollbarPosition(i);
      this.page1.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
      this.floatingButton = new ImageView(paramContext);
      this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
      localObject2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
      localObject1 = localObject2;
      if (Build.VERSION.SDK_INT < 21)
      {
        localObject1 = paramContext.getResources().getDrawable(NUM).mutate();
        ((Drawable)localObject1).setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        localObject1 = new CombinedDrawable((Drawable)localObject1, (Drawable)localObject2, 0, 0);
        ((CombinedDrawable)localObject1).setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
      }
      this.floatingButton.setBackgroundDrawable((Drawable)localObject1);
      this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
      this.floatingButton.setImageResource(NUM);
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject1 = new StateListAnimator();
        localObject2 = ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        ((StateListAnimator)localObject1).addState(new int[] { 16842919 }, (Animator)localObject2);
        localObject2 = ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        ((StateListAnimator)localObject1).addState(new int[0], (Animator)localObject2);
        this.floatingButton.setStateListAnimator((StateListAnimator)localObject1);
        this.floatingButton.setOutlineProvider(new ViewOutlineProvider()
        {
          @SuppressLint({"NewApi"})
          public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
          {
            paramAnonymousOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
          }
        });
      }
      localObject1 = this.page1;
      localObject2 = this.floatingButton;
      if (Build.VERSION.SDK_INT < 21) {
        break label1283;
      }
      i = 56;
      if (Build.VERSION.SDK_INT < 21) {
        break label1289;
      }
      f1 = 56.0F;
      if (!LocaleController.isRTL) {
        break label1297;
      }
      j = 3;
      if (!LocaleController.isRTL) {
        break label1303;
      }
      f2 = 14.0F;
      if (!LocaleController.isRTL) {
        break label1309;
      }
      f3 = 0.0F;
      label570:
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(i, f1, j | 0x50, f2, 0.0F, f3, 14.0F));
      this.dialogsAdapter = new DialogsAdapter(paramContext);
      this.listView.setAdapter(this.dialogsAdapter);
      this.page2 = new SizeNotifierFrameLayout(paramContext)
      {
        protected boolean drawChild(Canvas paramAnonymousCanvas, View paramAnonymousView, long paramAnonymousLong)
        {
          boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
          if ((paramAnonymousView == ThemePreviewActivity.this.actionBar2) && (ThemePreviewActivity.this.parentLayout != null))
          {
            paramAnonymousView = ThemePreviewActivity.this.parentLayout;
            if (ThemePreviewActivity.this.actionBar2.getVisibility() != 0) {
              break label73;
            }
          }
          label73:
          for (int i = ThemePreviewActivity.this.actionBar2.getMeasuredHeight();; i = 0)
          {
            paramAnonymousView.drawHeaderShadow(paramAnonymousCanvas, i);
            return bool;
          }
        }
        
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          int i = View.MeasureSpec.getSize(paramAnonymousInt1);
          int j = View.MeasureSpec.getSize(paramAnonymousInt2);
          setMeasuredDimension(i, j);
          measureChildWithMargins(ThemePreviewActivity.this.actionBar2, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
          paramAnonymousInt2 = ThemePreviewActivity.this.actionBar2.getMeasuredHeight();
          paramAnonymousInt1 = j;
          if (ThemePreviewActivity.this.actionBar2.getVisibility() == 0) {
            paramAnonymousInt1 = j - paramAnonymousInt2;
          }
          ((FrameLayout.LayoutParams)ThemePreviewActivity.this.listView2.getLayoutParams()).topMargin = paramAnonymousInt2;
          ThemePreviewActivity.this.listView2.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(paramAnonymousInt1, NUM));
        }
      };
      this.page2.setBackgroundImage(Theme.getCachedWallpaper());
      this.actionBar2 = createActionBar(paramContext);
      this.actionBar2.setBackButtonDrawable(new BackDrawable(false));
      this.actionBar2.setTitle("Reinhardt");
      this.actionBar2.setSubtitle(LocaleController.formatDateOnline(System.currentTimeMillis() / 1000L - 3600L));
      this.page2.addView(this.actionBar2, LayoutHelper.createFrame(-1, -2.0F));
      this.listView2 = new RecyclerListView(paramContext);
      this.listView2.setVerticalScrollBarEnabled(true);
      this.listView2.setItemAnimator(null);
      this.listView2.setLayoutAnimation(null);
      this.listView2.setPadding(0, AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F));
      this.listView2.setClipToPadding(false);
      this.listView2.setLayoutManager(new LinearLayoutManager(paramContext, 1, true));
      localObject1 = this.listView2;
      if (!LocaleController.isRTL) {
        break label1317;
      }
    }
    label1283:
    label1289:
    label1297:
    label1303:
    label1309:
    label1317:
    for (int i = 1;; i = 2)
    {
      ((RecyclerListView)localObject1).setVerticalScrollbarPosition(i);
      this.page2.addView(this.listView2, LayoutHelper.createFrame(-1, -1, 51));
      this.messagesAdapter = new MessagesAdapter(paramContext);
      this.listView2.setAdapter(this.messagesAdapter);
      this.fragmentView = new FrameLayout(paramContext);
      FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
      localObject2 = new ViewPager(paramContext);
      ((ViewPager)localObject2).addOnPageChangeListener(new ViewPager.OnPageChangeListener()
      {
        public void onPageScrollStateChanged(int paramAnonymousInt) {}
        
        public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2) {}
        
        public void onPageSelected(int paramAnonymousInt)
        {
          ThemePreviewActivity.this.dotsContainer.invalidate();
        }
      });
      ((ViewPager)localObject2).setAdapter(new PagerAdapter()
      {
        public void destroyItem(ViewGroup paramAnonymousViewGroup, int paramAnonymousInt, Object paramAnonymousObject)
        {
          paramAnonymousViewGroup.removeView((View)paramAnonymousObject);
        }
        
        public int getCount()
        {
          return 2;
        }
        
        public int getItemPosition(Object paramAnonymousObject)
        {
          return -1;
        }
        
        public Object instantiateItem(ViewGroup paramAnonymousViewGroup, int paramAnonymousInt)
        {
          if (paramAnonymousInt == 0) {}
          for (Object localObject = ThemePreviewActivity.this.page1;; localObject = ThemePreviewActivity.this.page2)
          {
            paramAnonymousViewGroup.addView((View)localObject);
            return localObject;
          }
        }
        
        public boolean isViewFromObject(View paramAnonymousView, Object paramAnonymousObject)
        {
          if (paramAnonymousObject == paramAnonymousView) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
        
        public void unregisterDataSetObserver(DataSetObserver paramAnonymousDataSetObserver)
        {
          if (paramAnonymousDataSetObserver != null) {
            super.unregisterDataSetObserver(paramAnonymousDataSetObserver);
          }
        }
      });
      AndroidUtilities.setViewPagerEdgeEffectColor((ViewPager)localObject2, Theme.getColor("actionBarDefault"));
      localFrameLayout.addView((View)localObject2, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
      localObject1 = new View(paramContext);
      ((View)localObject1).setBackgroundResource(NUM);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
      localObject1 = new FrameLayout(paramContext);
      ((FrameLayout)localObject1).setBackgroundColor(-1);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-1, 48, 83));
      this.dotsContainer = new View(paramContext)
      {
        private Paint paint = new Paint(1);
        
        protected void onDraw(Canvas paramAnonymousCanvas)
        {
          int i = localObject2.getCurrentItem();
          int j = 0;
          if (j < 2)
          {
            Paint localPaint = this.paint;
            if (j == i) {}
            for (int k = -6710887;; k = -3355444)
            {
              localPaint.setColor(k);
              paramAnonymousCanvas.drawCircle(AndroidUtilities.dp(j * 15 + 3), AndroidUtilities.dp(4.0F), AndroidUtilities.dp(3.0F), this.paint);
              j++;
              break;
            }
          }
        }
      };
      ((FrameLayout)localObject1).addView(this.dotsContainer, LayoutHelper.createFrame(22, 8, 17));
      localObject2 = new TextView(paramContext);
      ((TextView)localObject2).setTextSize(1, 14.0F);
      ((TextView)localObject2).setTextColor(-15095832);
      ((TextView)localObject2).setGravity(17);
      ((TextView)localObject2).setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      ((TextView)localObject2).setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
      ((TextView)localObject2).setText(LocaleController.getString("Cancel", NUM).toUpperCase());
      ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -1, 51));
      ((TextView)localObject2).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Theme.applyPreviousTheme();
          ThemePreviewActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
          ThemePreviewActivity.this.finishFragment();
        }
      });
      paramContext = new TextView(paramContext);
      paramContext.setTextSize(1, 14.0F);
      paramContext.setTextColor(-15095832);
      paramContext.setGravity(17);
      paramContext.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      paramContext.setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
      paramContext.setText(LocaleController.getString("ApplyTheme", NUM).toUpperCase());
      paramContext.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((FrameLayout)localObject1).addView(paramContext, LayoutHelper.createFrame(-2, -1, 53));
      paramContext.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ThemePreviewActivity.access$1802(ThemePreviewActivity.this, true);
          ThemePreviewActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
          Theme.applyThemeFile(ThemePreviewActivity.this.themeFile, ThemePreviewActivity.this.applyingTheme.name, false);
          ThemePreviewActivity.this.finishFragment();
        }
      });
      return this.fragmentView;
      i = 2;
      break;
      i = 60;
      break label528;
      f1 = 60.0F;
      break label541;
      j = 5;
      break label550;
      f2 = 0.0F;
      break label561;
      f3 = 14.0F;
      break label570;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if ((paramInt1 != NotificationCenter.emojiDidLoaded) || (this.listView == null)) {}
    for (;;)
    {
      return;
      paramInt2 = this.listView.getChildCount();
      for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
      {
        paramVarArgs = this.listView.getChildAt(paramInt1);
        if ((paramVarArgs instanceof DialogCell)) {
          ((DialogCell)paramVarArgs).update(0);
        }
      }
    }
  }
  
  public boolean onBackPressed()
  {
    Theme.applyPreviousTheme();
    this.parentLayout.rebuildAllFragmentViews(false, false);
    return super.onBackPressed();
  }
  
  public boolean onFragmentCreate()
  {
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    super.onFragmentDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.dialogsAdapter != null) {
      this.dialogsAdapter.notifyDataSetChanged();
    }
    if (this.messagesAdapter != null) {
      this.messagesAdapter.notifyDataSetChanged();
    }
  }
  
  public class DialogsAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private ArrayList<DialogCell.CustomDialog> dialogs;
    private Context mContext;
    
    public DialogsAdapter(Context paramContext)
    {
      this.mContext = paramContext;
      this.dialogs = new ArrayList();
      int i = (int)(System.currentTimeMillis() / 1000L);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Eva Summer";
      ThemePreviewActivity.this.message = "Reminds me of a Chinese prove...";
      ThemePreviewActivity.this.id = 0;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = true;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = i;
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = true;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Alexandra Smith";
      ThemePreviewActivity.this.message = "Reminds me of a Chinese prove...";
      ThemePreviewActivity.this.id = 1;
      ThemePreviewActivity.this.unread_count = 2;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = (i - 3600);
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Make Apple";
      ThemePreviewActivity.this.message = "🤷‍♂️ Sticker";
      ThemePreviewActivity.this.id = 2;
      ThemePreviewActivity.this.unread_count = 3;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = true;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = (i - 7200);
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = true;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Paul Newman";
      ThemePreviewActivity.this.message = "Any ideas?";
      ThemePreviewActivity.this.id = 3;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 2;
      ThemePreviewActivity.this.date = (i - 10800);
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Old Pirates";
      ThemePreviewActivity.this.message = "Yo-ho-ho!";
      ThemePreviewActivity.this.id = 4;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 1;
      ThemePreviewActivity.this.date = (i - 14400);
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = true;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Kate Bright";
      ThemePreviewActivity.this.message = "Hola!";
      ThemePreviewActivity.this.id = 5;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = (i - 18000);
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Nick K";
      ThemePreviewActivity.this.message = "These are not the droids you are looking for";
      ThemePreviewActivity.this.id = 6;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = (i - 21600);
      ThemePreviewActivity.this.verified = true;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Adler Toberg";
      ThemePreviewActivity.this.message = "Did someone say peanut butter?";
      ThemePreviewActivity.this.id = 0;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = (i - 25200);
      ThemePreviewActivity.this.verified = true;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
    }
    
    public int getItemCount()
    {
      return this.dialogs.size() + 1;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (paramInt == this.dialogs.size()) {}
      for (paramInt = 1;; paramInt = 0) {
        return paramInt;
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool = true;
      if (paramViewHolder.getItemViewType() != 1) {}
      for (;;)
      {
        return bool;
        bool = false;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramViewHolder.getItemViewType() == 0)
      {
        paramViewHolder = (DialogCell)paramViewHolder.itemView;
        if (paramInt == getItemCount() - 1) {
          break label48;
        }
      }
      label48:
      for (boolean bool = true;; bool = false)
      {
        paramViewHolder.useSeparator = bool;
        paramViewHolder.setDialog((DialogCell.CustomDialog)this.dialogs.get(paramInt));
        return;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      if (paramInt == 0) {
        paramViewGroup = new DialogCell(this.mContext, false);
      }
      for (;;)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        if (paramInt == 1) {
          paramViewGroup = new LoadingCell(this.mContext);
        }
      }
    }
  }
  
  public class MessagesAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    private ArrayList<MessageObject> messages;
    
    public MessagesAdapter(Context paramContext)
    {
      this.mContext = paramContext;
      this.messages = new ArrayList();
      int i = (int)(System.currentTimeMillis() / 1000L) - 3600;
      paramContext = new TLRPC.TL_message();
      paramContext.message = "Reinhardt, we need to find you some new tunes 🎶.";
      paramContext.date = (i + 60);
      paramContext.dialog_id = 1L;
      paramContext.flags = 259;
      paramContext.from_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
      paramContext.id = 1;
      paramContext.media = new TLRPC.TL_messageMediaEmpty();
      paramContext.out = true;
      paramContext.to_id = new TLRPC.TL_peerUser();
      paramContext.to_id.user_id = 0;
      paramContext = new MessageObject(ThemePreviewActivity.this.currentAccount, paramContext, true);
      Object localObject1 = new TLRPC.TL_message();
      ((TLRPC.Message)localObject1).message = "I can't even take you seriously right now.";
      ((TLRPC.Message)localObject1).date = (i + 960);
      ((TLRPC.Message)localObject1).dialog_id = 1L;
      ((TLRPC.Message)localObject1).flags = 259;
      ((TLRPC.Message)localObject1).from_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
      ((TLRPC.Message)localObject1).id = 1;
      ((TLRPC.Message)localObject1).media = new TLRPC.TL_messageMediaEmpty();
      ((TLRPC.Message)localObject1).out = true;
      ((TLRPC.Message)localObject1).to_id = new TLRPC.TL_peerUser();
      ((TLRPC.Message)localObject1).to_id.user_id = 0;
      this.messages.add(new MessageObject(ThemePreviewActivity.this.currentAccount, (TLRPC.Message)localObject1, true));
      localObject1 = new TLRPC.TL_message();
      ((TLRPC.Message)localObject1).date = (i + 130);
      ((TLRPC.Message)localObject1).dialog_id = 1L;
      ((TLRPC.Message)localObject1).flags = 259;
      ((TLRPC.Message)localObject1).from_id = 0;
      ((TLRPC.Message)localObject1).id = 5;
      ((TLRPC.Message)localObject1).media = new TLRPC.TL_messageMediaDocument();
      Object localObject2 = ((TLRPC.Message)localObject1).media;
      ((TLRPC.MessageMedia)localObject2).flags |= 0x3;
      ((TLRPC.Message)localObject1).media.document = new TLRPC.TL_document();
      ((TLRPC.Message)localObject1).media.document.mime_type = "audio/mp4";
      ((TLRPC.Message)localObject1).media.document.thumb = new TLRPC.TL_photoSizeEmpty();
      ((TLRPC.Message)localObject1).media.document.thumb.type = "s";
      localObject2 = new TLRPC.TL_documentAttributeAudio();
      ((TLRPC.TL_documentAttributeAudio)localObject2).duration = 243;
      ((TLRPC.TL_documentAttributeAudio)localObject2).performer = "David Hasselhoff";
      ((TLRPC.TL_documentAttributeAudio)localObject2).title = "True Survivor";
      ((TLRPC.Message)localObject1).media.document.attributes.add(localObject2);
      ((TLRPC.Message)localObject1).out = false;
      ((TLRPC.Message)localObject1).to_id = new TLRPC.TL_peerUser();
      ((TLRPC.Message)localObject1).to_id.user_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
      this.messages.add(new MessageObject(ThemePreviewActivity.this.currentAccount, (TLRPC.Message)localObject1, true));
      localObject1 = new TLRPC.TL_message();
      ((TLRPC.Message)localObject1).message = "Ah, you kids today with techno music! You should enjoy the classics, like Hasselhoff!";
      ((TLRPC.Message)localObject1).date = (i + 60);
      ((TLRPC.Message)localObject1).dialog_id = 1L;
      ((TLRPC.Message)localObject1).flags = 265;
      ((TLRPC.Message)localObject1).from_id = 0;
      ((TLRPC.Message)localObject1).id = 1;
      ((TLRPC.Message)localObject1).reply_to_msg_id = 5;
      ((TLRPC.Message)localObject1).media = new TLRPC.TL_messageMediaEmpty();
      ((TLRPC.Message)localObject1).out = false;
      ((TLRPC.Message)localObject1).to_id = new TLRPC.TL_peerUser();
      ((TLRPC.Message)localObject1).to_id.user_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
      localObject1 = new MessageObject(ThemePreviewActivity.this.currentAccount, (TLRPC.Message)localObject1, true);
      ((MessageObject)localObject1).customReplyName = "Lucio";
      ((MessageObject)localObject1).replyMessageObject = paramContext;
      this.messages.add(localObject1);
      localObject1 = new TLRPC.TL_message();
      ((TLRPC.Message)localObject1).date = (i + 120);
      ((TLRPC.Message)localObject1).dialog_id = 1L;
      ((TLRPC.Message)localObject1).flags = 259;
      ((TLRPC.Message)localObject1).from_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
      ((TLRPC.Message)localObject1).id = 1;
      ((TLRPC.Message)localObject1).media = new TLRPC.TL_messageMediaDocument();
      localObject2 = ((TLRPC.Message)localObject1).media;
      ((TLRPC.MessageMedia)localObject2).flags |= 0x3;
      ((TLRPC.Message)localObject1).media.document = new TLRPC.TL_document();
      ((TLRPC.Message)localObject1).media.document.mime_type = "audio/ogg";
      ((TLRPC.Message)localObject1).media.document.thumb = new TLRPC.TL_photoSizeEmpty();
      ((TLRPC.Message)localObject1).media.document.thumb.type = "s";
      localObject2 = new TLRPC.TL_documentAttributeAudio();
      ((TLRPC.TL_documentAttributeAudio)localObject2).flags = 1028;
      ((TLRPC.TL_documentAttributeAudio)localObject2).duration = 3;
      ((TLRPC.TL_documentAttributeAudio)localObject2).voice = true;
      ((TLRPC.TL_documentAttributeAudio)localObject2).waveform = new byte[] { 0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1 };
      ((TLRPC.Message)localObject1).media.document.attributes.add(localObject2);
      ((TLRPC.Message)localObject1).out = true;
      ((TLRPC.Message)localObject1).to_id = new TLRPC.TL_peerUser();
      ((TLRPC.Message)localObject1).to_id.user_id = 0;
      localObject1 = new MessageObject(ThemePreviewActivity.this.currentAccount, (TLRPC.Message)localObject1, true);
      ((MessageObject)localObject1).audioProgressSec = 1;
      ((MessageObject)localObject1).audioProgress = 0.3F;
      ((MessageObject)localObject1).useCustomPhoto = true;
      this.messages.add(localObject1);
      this.messages.add(paramContext);
      paramContext = new TLRPC.TL_message();
      paramContext.date = (i + 10);
      paramContext.dialog_id = 1L;
      paramContext.flags = 257;
      paramContext.from_id = 0;
      paramContext.id = 1;
      paramContext.media = new TLRPC.TL_messageMediaPhoto();
      localObject1 = paramContext.media;
      ((TLRPC.MessageMedia)localObject1).flags |= 0x3;
      paramContext.media.photo = new TLRPC.TL_photo();
      paramContext.media.photo.has_stickers = false;
      paramContext.media.photo.id = 1L;
      paramContext.media.photo.access_hash = 0L;
      paramContext.media.photo.date = i;
      localObject1 = new TLRPC.TL_photoSize();
      ((TLRPC.TL_photoSize)localObject1).size = 0;
      ((TLRPC.TL_photoSize)localObject1).w = 500;
      ((TLRPC.TL_photoSize)localObject1).h = 302;
      ((TLRPC.TL_photoSize)localObject1).type = "s";
      ((TLRPC.TL_photoSize)localObject1).location = new TLRPC.TL_fileLocationUnavailable();
      paramContext.media.photo.sizes.add(localObject1);
      paramContext.message = "Bring it on! I LIVE for this!";
      paramContext.out = false;
      paramContext.to_id = new TLRPC.TL_peerUser();
      paramContext.to_id.user_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
      paramContext = new MessageObject(ThemePreviewActivity.this.currentAccount, paramContext, true);
      paramContext.useCustomPhoto = true;
      this.messages.add(paramContext);
      paramContext = new TLRPC.TL_message();
      paramContext.message = LocaleController.formatDateChat(i);
      paramContext.id = 0;
      paramContext.date = i;
      this$1 = new MessageObject(ThemePreviewActivity.this.currentAccount, paramContext, false);
      ThemePreviewActivity.this.type = 10;
      ThemePreviewActivity.this.contentType = 1;
      ThemePreviewActivity.this.isDateObject = true;
      this.messages.add(ThemePreviewActivity.this);
    }
    
    public int getItemCount()
    {
      return this.messages.size();
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < this.messages.size())) {}
      for (paramInt = ((MessageObject)this.messages.get(paramInt)).contentType;; paramInt = 4) {
        return paramInt;
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      MessageObject localMessageObject1 = (MessageObject)this.messages.get(paramInt);
      Object localObject = paramViewHolder.itemView;
      boolean bool1;
      boolean bool2;
      if ((localObject instanceof ChatMessageCell))
      {
        localObject = (ChatMessageCell)localObject;
        ((ChatMessageCell)localObject).isChat = false;
        int i = getItemViewType(paramInt - 1);
        int j = getItemViewType(paramInt + 1);
        if ((!(localMessageObject1.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) && (i == paramViewHolder.getItemViewType()))
        {
          MessageObject localMessageObject2 = (MessageObject)this.messages.get(paramInt - 1);
          if ((localMessageObject2.isOutOwner() == localMessageObject1.isOutOwner()) && (Math.abs(localMessageObject2.messageOwner.date - localMessageObject1.messageOwner.date) <= 300))
          {
            bool1 = true;
            if (j != paramViewHolder.getItemViewType()) {
              break label244;
            }
            paramViewHolder = (MessageObject)this.messages.get(paramInt + 1);
            if (((paramViewHolder.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) || (paramViewHolder.isOutOwner() != localMessageObject1.isOutOwner()) || (Math.abs(paramViewHolder.messageOwner.date - localMessageObject1.messageOwner.date) > 300)) {
              break label238;
            }
            bool2 = true;
            label208:
            ((ChatMessageCell)localObject).setFullyDraw(true);
            ((ChatMessageCell)localObject).setMessageObject(localMessageObject1, null, bool1, bool2);
          }
        }
      }
      for (;;)
      {
        return;
        bool1 = false;
        break;
        bool1 = false;
        break;
        label238:
        bool2 = false;
        break label208;
        label244:
        bool2 = false;
        break label208;
        if ((localObject instanceof ChatActionCell))
        {
          paramViewHolder = (ChatActionCell)localObject;
          paramViewHolder.setMessageObject(localMessageObject1);
          paramViewHolder.setAlpha(1.0F);
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      if (paramInt == 0)
      {
        paramViewGroup = new ChatMessageCell(this.mContext);
        ((ChatMessageCell)paramViewGroup).setDelegate(new ChatMessageCell.ChatMessageCellDelegate()
        {
          public boolean canPerformActions()
          {
            return false;
          }
          
          public void didLongPressed(ChatMessageCell paramAnonymousChatMessageCell) {}
          
          public void didPressedBotButton(ChatMessageCell paramAnonymousChatMessageCell, TLRPC.KeyboardButton paramAnonymousKeyboardButton) {}
          
          public void didPressedCancelSendButton(ChatMessageCell paramAnonymousChatMessageCell) {}
          
          public void didPressedChannelAvatar(ChatMessageCell paramAnonymousChatMessageCell, TLRPC.Chat paramAnonymousChat, int paramAnonymousInt) {}
          
          public void didPressedImage(ChatMessageCell paramAnonymousChatMessageCell) {}
          
          public void didPressedInstantButton(ChatMessageCell paramAnonymousChatMessageCell, int paramAnonymousInt) {}
          
          public void didPressedOther(ChatMessageCell paramAnonymousChatMessageCell) {}
          
          public void didPressedReplyMessage(ChatMessageCell paramAnonymousChatMessageCell, int paramAnonymousInt) {}
          
          public void didPressedShare(ChatMessageCell paramAnonymousChatMessageCell) {}
          
          public void didPressedUrl(MessageObject paramAnonymousMessageObject, CharacterStyle paramAnonymousCharacterStyle, boolean paramAnonymousBoolean) {}
          
          public void didPressedUserAvatar(ChatMessageCell paramAnonymousChatMessageCell, TLRPC.User paramAnonymousUser) {}
          
          public void didPressedViaBot(ChatMessageCell paramAnonymousChatMessageCell, String paramAnonymousString) {}
          
          public boolean isChatAdminCell(int paramAnonymousInt)
          {
            return false;
          }
          
          public void needOpenWebView(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, String paramAnonymousString4, int paramAnonymousInt1, int paramAnonymousInt2) {}
          
          public boolean needPlayMessage(MessageObject paramAnonymousMessageObject)
          {
            return false;
          }
        });
      }
      for (;;)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        if (paramInt == 1)
        {
          paramViewGroup = new ChatActionCell(this.mContext);
          ((ChatActionCell)paramViewGroup).setDelegate(new ChatActionCell.ChatActionCellDelegate()
          {
            public void didClickedImage(ChatActionCell paramAnonymousChatActionCell) {}
            
            public void didLongPressed(ChatActionCell paramAnonymousChatActionCell) {}
            
            public void didPressedBotButton(MessageObject paramAnonymousMessageObject, TLRPC.KeyboardButton paramAnonymousKeyboardButton) {}
            
            public void didPressedReplyMessage(ChatActionCell paramAnonymousChatActionCell, int paramAnonymousInt) {}
            
            public void needOpenUserProfile(int paramAnonymousInt) {}
          });
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ThemePreviewActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */