package org.telegram.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate;
import org.telegram.ui.Cells.PhotoPickerSearchCell;
import org.telegram.ui.Cells.PhotoPickerSearchCell.PhotoPickerSearchCellDelegate;
import org.telegram.ui.Components.PickerBottomLayout;

public class PhotoAlbumPickerActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int item_photos = 2;
  private static final int item_video = 3;
  private ArrayList<MediaController.AlbumEntry> albumsSorted = null;
  private boolean allowCaption;
  private boolean allowGifs;
  private ChatActivity chatActivity;
  private int columnsCount = 2;
  private PhotoAlbumPickerActivityDelegate delegate;
  private TextView dropDown;
  private ActionBarMenuItem dropDownContainer;
  private TextView emptyView;
  private ListAdapter listAdapter;
  private ListView listView;
  private boolean loading = false;
  private PickerBottomLayout pickerBottomLayout;
  private FrameLayout progressView;
  private ArrayList<MediaController.SearchImage> recentGifImages = new ArrayList();
  private HashMap<String, MediaController.SearchImage> recentImagesGifKeys = new HashMap();
  private HashMap<String, MediaController.SearchImage> recentImagesWebKeys = new HashMap();
  private ArrayList<MediaController.SearchImage> recentWebImages = new ArrayList();
  private int selectedMode;
  private HashMap<Integer, MediaController.PhotoEntry> selectedPhotos = new HashMap();
  private HashMap<String, MediaController.SearchImage> selectedWebPhotos = new HashMap();
  private boolean sendPressed;
  private boolean singlePhoto;
  private ArrayList<MediaController.AlbumEntry> videoAlbumsSorted = null;
  
  public PhotoAlbumPickerActivity(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, ChatActivity paramChatActivity)
  {
    this.chatActivity = paramChatActivity;
    this.singlePhoto = paramBoolean1;
    this.allowGifs = paramBoolean2;
    this.allowCaption = paramBoolean3;
  }
  
  private void fixLayout()
  {
    if (this.listView != null) {
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          PhotoAlbumPickerActivity.this.fixLayoutInternal();
          if (PhotoAlbumPickerActivity.this.listView != null) {
            PhotoAlbumPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          }
          return true;
        }
      });
    }
  }
  
  private void fixLayoutInternal()
  {
    if (getParentActivity() == null) {}
    do
    {
      return;
      i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
      this.columnsCount = 2;
      if ((!AndroidUtilities.isTablet()) && ((i == 3) || (i == 1))) {
        this.columnsCount = 4;
      }
      this.listAdapter.notifyDataSetChanged();
    } while (this.dropDownContainer == null);
    FrameLayout.LayoutParams localLayoutParams;
    if (!AndroidUtilities.isTablet())
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.dropDownContainer.getLayoutParams();
      if (Build.VERSION.SDK_INT < 21) {
        break label143;
      }
    }
    label143:
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      localLayoutParams.topMargin = i;
      this.dropDownContainer.setLayoutParams(localLayoutParams);
      if ((AndroidUtilities.isTablet()) || (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2)) {
        break;
      }
      this.dropDown.setTextSize(18.0F);
      return;
    }
    this.dropDown.setTextSize(20.0F);
  }
  
  private void openPhotoPicker(MediaController.AlbumEntry paramAlbumEntry, int paramInt)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramAlbumEntry == null)
    {
      if (paramInt != 0) {
        break label69;
      }
      localObject1 = this.recentWebImages;
    }
    for (;;)
    {
      paramAlbumEntry = new PhotoPickerActivity(paramInt, paramAlbumEntry, this.selectedPhotos, this.selectedWebPhotos, (ArrayList)localObject1, this.singlePhoto, this.allowCaption, this.chatActivity);
      paramAlbumEntry.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate()
      {
        public void actionButtonPressed(boolean paramAnonymousBoolean)
        {
          PhotoAlbumPickerActivity.this.removeSelfFromStack();
          if (!paramAnonymousBoolean) {
            PhotoAlbumPickerActivity.this.sendSelectedPhotos();
          }
        }
        
        public boolean didSelectVideo(String paramAnonymousString)
        {
          PhotoAlbumPickerActivity.this.removeSelfFromStack();
          return PhotoAlbumPickerActivity.this.delegate.didSelectVideo(paramAnonymousString);
        }
        
        public void selectedPhotosChanged()
        {
          if (PhotoAlbumPickerActivity.this.pickerBottomLayout != null) {
            PhotoAlbumPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoAlbumPickerActivity.this.selectedPhotos.size() + PhotoAlbumPickerActivity.this.selectedWebPhotos.size(), true);
          }
        }
      });
      presentFragment(paramAlbumEntry);
      return;
      label69:
      localObject1 = localObject2;
      if (paramInt == 1) {
        localObject1 = this.recentGifImages;
      }
    }
  }
  
  private void sendSelectedPhotos()
  {
    if (((this.selectedPhotos.isEmpty()) && (this.selectedWebPhotos.isEmpty())) || (this.delegate == null) || (this.sendPressed)) {
      return;
    }
    this.sendPressed = true;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    Object localObject2 = this.selectedPhotos.entrySet().iterator();
    Object localObject1;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (MediaController.PhotoEntry)((Map.Entry)((Iterator)localObject2).next()).getValue();
      if (((MediaController.PhotoEntry)localObject3).imagePath != null)
      {
        localArrayList1.add(((MediaController.PhotoEntry)localObject3).imagePath);
        if (((MediaController.PhotoEntry)localObject3).caption != null)
        {
          localObject1 = ((MediaController.PhotoEntry)localObject3).caption.toString();
          label149:
          localArrayList2.add(localObject1);
          if (((MediaController.PhotoEntry)localObject3).stickers.isEmpty()) {
            break label195;
          }
        }
        label195:
        for (localObject1 = new ArrayList(((MediaController.PhotoEntry)localObject3).stickers);; localObject1 = null)
        {
          localArrayList3.add(localObject1);
          break;
          localObject1 = null;
          break label149;
        }
      }
      if (((MediaController.PhotoEntry)localObject3).path != null)
      {
        localArrayList1.add(((MediaController.PhotoEntry)localObject3).path);
        if (((MediaController.PhotoEntry)localObject3).caption != null)
        {
          localObject1 = ((MediaController.PhotoEntry)localObject3).caption.toString();
          label238:
          localArrayList2.add(localObject1);
          if (((MediaController.PhotoEntry)localObject3).stickers.isEmpty()) {
            break label284;
          }
        }
        label284:
        for (localObject1 = new ArrayList(((MediaController.PhotoEntry)localObject3).stickers);; localObject1 = null)
        {
          localArrayList3.add(localObject1);
          break;
          localObject1 = null;
          break label238;
        }
      }
    }
    localObject2 = new ArrayList();
    int j = 0;
    int i = 0;
    Object localObject3 = this.selectedWebPhotos.entrySet().iterator();
    while (((Iterator)localObject3).hasNext())
    {
      MediaController.SearchImage localSearchImage = (MediaController.SearchImage)((Map.Entry)((Iterator)localObject3).next()).getValue();
      if (localSearchImage.imagePath != null)
      {
        localArrayList1.add(localSearchImage.imagePath);
        if (localSearchImage.caption != null)
        {
          localObject1 = localSearchImage.caption.toString();
          label384:
          localArrayList2.add(localObject1);
          if (localSearchImage.stickers.isEmpty()) {
            break label491;
          }
          localObject1 = new ArrayList(localSearchImage.stickers);
          label415:
          localArrayList3.add(localObject1);
        }
      }
      for (;;)
      {
        localSearchImage.date = ((int)(System.currentTimeMillis() / 1000L));
        if (localSearchImage.type != 0) {
          break label520;
        }
        i = 1;
        localObject1 = (MediaController.SearchImage)this.recentImagesWebKeys.get(localSearchImage.id);
        if (localObject1 == null) {
          break label507;
        }
        this.recentWebImages.remove(localObject1);
        this.recentWebImages.add(0, localObject1);
        break;
        localObject1 = null;
        break label384;
        label491:
        localObject1 = null;
        break label415;
        ((ArrayList)localObject2).add(localSearchImage);
      }
      label507:
      this.recentWebImages.add(0, localSearchImage);
      continue;
      label520:
      if (localSearchImage.type == 1)
      {
        j = 1;
        localObject1 = (MediaController.SearchImage)this.recentImagesGifKeys.get(localSearchImage.id);
        if (localObject1 != null)
        {
          this.recentGifImages.remove(localObject1);
          this.recentGifImages.add(0, localObject1);
        }
        else
        {
          this.recentGifImages.add(0, localSearchImage);
        }
      }
    }
    if (i != 0) {
      MessagesStorage.getInstance().putWebRecent(this.recentWebImages);
    }
    if (j != 0) {
      MessagesStorage.getInstance().putWebRecent(this.recentGifImages);
    }
    this.delegate.didSelectPhotos(localArrayList1, localArrayList2, localArrayList3, (ArrayList)localObject2);
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(-13421773);
    this.actionBar.setItemsBackgroundColor(-12763843);
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          PhotoAlbumPickerActivity.this.finishFragment();
        }
        do
        {
          do
          {
            do
            {
              return;
              if (paramAnonymousInt != 1) {
                break;
              }
            } while (PhotoAlbumPickerActivity.this.delegate == null);
            PhotoAlbumPickerActivity.this.finishFragment(false);
            PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
            return;
            if (paramAnonymousInt != 2) {
              break;
            }
          } while (PhotoAlbumPickerActivity.this.selectedMode == 0);
          PhotoAlbumPickerActivity.access$102(PhotoAlbumPickerActivity.this, 0);
          PhotoAlbumPickerActivity.this.dropDown.setText(LocaleController.getString("PickerPhotos", 2131166120));
          PhotoAlbumPickerActivity.this.emptyView.setText(LocaleController.getString("NoPhotos", 2131165943));
          PhotoAlbumPickerActivity.this.listAdapter.notifyDataSetChanged();
          return;
        } while ((paramAnonymousInt != 3) || (PhotoAlbumPickerActivity.this.selectedMode == 1));
        PhotoAlbumPickerActivity.access$102(PhotoAlbumPickerActivity.this, 1);
        PhotoAlbumPickerActivity.this.dropDown.setText(LocaleController.getString("PickerVideo", 2131166121));
        PhotoAlbumPickerActivity.this.emptyView.setText(LocaleController.getString("NoVideo", 2131165959));
        PhotoAlbumPickerActivity.this.listAdapter.notifyDataSetChanged();
      }
    });
    Object localObject = this.actionBar.createMenu();
    ((ActionBarMenu)localObject).addItem(1, 2130837708);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(-16777216);
    int i;
    if (!this.singlePhoto)
    {
      this.selectedMode = 0;
      this.dropDownContainer = new ActionBarMenuItem(paramContext, (ActionBarMenu)localObject, 0);
      this.dropDownContainer.setSubMenuOpenSide(1);
      this.dropDownContainer.addSubItem(2, LocaleController.getString("PickerPhotos", 2131166120), 0);
      this.dropDownContainer.addSubItem(3, LocaleController.getString("PickerVideo", 2131166121), 0);
      this.actionBar.addView(this.dropDownContainer);
      localObject = (FrameLayout.LayoutParams)this.dropDownContainer.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).height = -1;
      ((FrameLayout.LayoutParams)localObject).width = -2;
      ((FrameLayout.LayoutParams)localObject).rightMargin = AndroidUtilities.dp(40.0F);
      if (AndroidUtilities.isTablet())
      {
        i = AndroidUtilities.dp(64.0F);
        ((FrameLayout.LayoutParams)localObject).leftMargin = i;
        ((FrameLayout.LayoutParams)localObject).gravity = 51;
        this.dropDownContainer.setLayoutParams((ViewGroup.LayoutParams)localObject);
        this.dropDownContainer.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PhotoAlbumPickerActivity.this.dropDownContainer.toggleSubMenu();
          }
        });
        this.dropDown = new TextView(paramContext);
        this.dropDown.setGravity(3);
        this.dropDown.setSingleLine(true);
        this.dropDown.setLines(1);
        this.dropDown.setMaxLines(1);
        this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
        this.dropDown.setTextColor(-1);
        this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.dropDown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 2130837716, 0);
        this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
        this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0F), 0);
        this.dropDown.setText(LocaleController.getString("PickerPhotos", 2131166120));
        this.dropDownContainer.addView(this.dropDown);
        localObject = (FrameLayout.LayoutParams)this.dropDown.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).width = -2;
        ((FrameLayout.LayoutParams)localObject).height = -2;
        ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(16.0F);
        ((FrameLayout.LayoutParams)localObject).gravity = 16;
        this.dropDown.setLayoutParams((ViewGroup.LayoutParams)localObject);
        label458:
        this.listView = new ListView(paramContext);
        this.listView.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setSelector(new ColorDrawable(0));
        this.listView.setDividerHeight(0);
        this.listView.setDivider(null);
        this.listView.setDrawingCacheEnabled(false);
        this.listView.setScrollingCacheEnabled(false);
        localFrameLayout.addView(this.listView);
        localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).width = -1;
        ((FrameLayout.LayoutParams)localObject).height = -1;
        ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(48.0F);
        this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        localObject = this.listView;
        ListAdapter localListAdapter = new ListAdapter(paramContext);
        this.listAdapter = localListAdapter;
        ((ListView)localObject).setAdapter(localListAdapter);
        AndroidUtilities.setListViewEdgeEffectColor(this.listView, -13421773);
        this.emptyView = new TextView(paramContext);
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setTextSize(20.0F);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.emptyView.setText(LocaleController.getString("NoPhotos", 2131165943));
        localFrameLayout.addView(this.emptyView);
        localObject = (FrameLayout.LayoutParams)this.emptyView.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).width = -1;
        ((FrameLayout.LayoutParams)localObject).height = -1;
        ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(48.0F);
        this.emptyView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        this.emptyView.setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            return true;
          }
        });
        this.progressView = new FrameLayout(paramContext);
        this.progressView.setVisibility(8);
        localFrameLayout.addView(this.progressView);
        localObject = (FrameLayout.LayoutParams)this.progressView.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).width = -1;
        ((FrameLayout.LayoutParams)localObject).height = -1;
        ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(48.0F);
        this.progressView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        localObject = new ProgressBar(paramContext);
        this.progressView.addView((View)localObject);
        localObject = (FrameLayout.LayoutParams)this.progressView.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).width = -2;
        ((FrameLayout.LayoutParams)localObject).height = -2;
        ((FrameLayout.LayoutParams)localObject).gravity = 17;
        this.progressView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        this.pickerBottomLayout = new PickerBottomLayout(paramContext);
        localFrameLayout.addView(this.pickerBottomLayout);
        paramContext = (FrameLayout.LayoutParams)this.pickerBottomLayout.getLayoutParams();
        paramContext.width = -1;
        paramContext.height = AndroidUtilities.dp(48.0F);
        paramContext.gravity = 80;
        this.pickerBottomLayout.setLayoutParams(paramContext);
        this.pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PhotoAlbumPickerActivity.this.finishFragment();
          }
        });
        this.pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PhotoAlbumPickerActivity.this.sendSelectedPhotos();
            PhotoAlbumPickerActivity.this.finishFragment();
          }
        });
        if ((!this.loading) || ((this.albumsSorted != null) && ((this.albumsSorted == null) || (!this.albumsSorted.isEmpty())))) {
          break label1126;
        }
        this.progressView.setVisibility(0);
        this.listView.setEmptyView(null);
      }
    }
    for (;;)
    {
      this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
      return this.fragmentView;
      i = AndroidUtilities.dp(56.0F);
      break;
      this.actionBar.setTitle(LocaleController.getString("Gallery", 2131165712));
      break label458;
      label1126:
      this.progressView.setVisibility(8);
      this.listView.setEmptyView(this.emptyView);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.albumsDidLoaded)
    {
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (this.classGuid == paramInt)
      {
        this.albumsSorted = ((ArrayList)paramVarArgs[1]);
        this.videoAlbumsSorted = ((ArrayList)paramVarArgs[3]);
        if (this.progressView != null) {
          this.progressView.setVisibility(8);
        }
        if ((this.listView != null) && (this.listView.getEmptyView() == null)) {
          this.listView.setEmptyView(this.emptyView);
        }
        if (this.listAdapter != null) {
          this.listAdapter.notifyDataSetChanged();
        }
        this.loading = false;
      }
    }
    for (;;)
    {
      return;
      if (paramInt == NotificationCenter.closeChats)
      {
        removeSelfFromStack();
        return;
      }
      if (paramInt == NotificationCenter.recentImagesDidLoaded)
      {
        paramInt = ((Integer)paramVarArgs[0]).intValue();
        MediaController.SearchImage localSearchImage;
        if (paramInt == 0)
        {
          this.recentWebImages = ((ArrayList)paramVarArgs[1]);
          this.recentImagesWebKeys.clear();
          paramVarArgs = this.recentWebImages.iterator();
          while (paramVarArgs.hasNext())
          {
            localSearchImage = (MediaController.SearchImage)paramVarArgs.next();
            this.recentImagesWebKeys.put(localSearchImage.id, localSearchImage);
          }
        }
        else if (paramInt == 1)
        {
          this.recentGifImages = ((ArrayList)paramVarArgs[1]);
          this.recentImagesGifKeys.clear();
          paramVarArgs = this.recentGifImages.iterator();
          while (paramVarArgs.hasNext())
          {
            localSearchImage = (MediaController.SearchImage)paramVarArgs.next();
            this.recentImagesGifKeys.put(localSearchImage.id, localSearchImage);
          }
        }
      }
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }
  
  public boolean onFragmentCreate()
  {
    this.loading = true;
    MediaController.loadGalleryPhotosAlbums(this.classGuid);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.albumsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.albumsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentImagesDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    super.onFragmentDestroy();
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.dropDownContainer != null) {
      this.dropDownContainer.closeSubMenu();
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    fixLayout();
  }
  
  public void setDelegate(PhotoAlbumPickerActivityDelegate paramPhotoAlbumPickerActivityDelegate)
  {
    this.delegate = paramPhotoAlbumPickerActivityDelegate;
  }
  
  private class ListAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return true;
    }
    
    public int getCount()
    {
      int j = 0;
      int i = 0;
      if ((PhotoAlbumPickerActivity.this.singlePhoto) || (PhotoAlbumPickerActivity.this.selectedMode == 0)) {
        if (PhotoAlbumPickerActivity.this.singlePhoto) {
          if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
            i = (int)Math.ceil(PhotoAlbumPickerActivity.this.albumsSorted.size() / PhotoAlbumPickerActivity.this.columnsCount);
          }
        }
      }
      while (PhotoAlbumPickerActivity.this.videoAlbumsSorted == null)
      {
        return i;
        i = j;
        if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
          i = (int)Math.ceil(PhotoAlbumPickerActivity.this.albumsSorted.size() / PhotoAlbumPickerActivity.this.columnsCount);
        }
        return i + 1;
      }
      return (int)Math.ceil(PhotoAlbumPickerActivity.this.videoAlbumsSorted.size() / PhotoAlbumPickerActivity.this.columnsCount);
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 1;
      if ((PhotoAlbumPickerActivity.this.singlePhoto) || (PhotoAlbumPickerActivity.this.selectedMode == 1)) {
        i = 0;
      }
      while (paramInt == 0) {
        return i;
      }
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      if (i == 0)
      {
        label59:
        int j;
        if (paramView == null)
        {
          paramView = new PhotoPickerAlbumsCell(this.mContext);
          paramViewGroup = (PhotoPickerAlbumsCell)paramView;
          paramViewGroup.setDelegate(new PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate()
          {
            public void didSelectAlbum(MediaController.AlbumEntry paramAnonymousAlbumEntry)
            {
              PhotoAlbumPickerActivity.this.openPhotoPicker(paramAnonymousAlbumEntry, 0);
            }
          });
          paramViewGroup.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
          i = 0;
          if (i >= PhotoAlbumPickerActivity.this.columnsCount) {
            break label257;
          }
          if ((!PhotoAlbumPickerActivity.this.singlePhoto) && (PhotoAlbumPickerActivity.this.selectedMode != 1)) {
            break label179;
          }
          j = PhotoAlbumPickerActivity.this.columnsCount * paramInt + i;
          label106:
          if ((!PhotoAlbumPickerActivity.this.singlePhoto) && (PhotoAlbumPickerActivity.this.selectedMode != 0)) {
            break label208;
          }
          if (j >= PhotoAlbumPickerActivity.this.albumsSorted.size()) {
            break label198;
          }
          paramViewGroup.setAlbum(i, (MediaController.AlbumEntry)PhotoAlbumPickerActivity.this.albumsSorted.get(j));
        }
        for (;;)
        {
          i += 1;
          break label59;
          paramViewGroup = (PhotoPickerAlbumsCell)paramView;
          break;
          label179:
          j = (paramInt - 1) * PhotoAlbumPickerActivity.this.columnsCount + i;
          break label106;
          label198:
          paramViewGroup.setAlbum(i, null);
          continue;
          label208:
          if (j < PhotoAlbumPickerActivity.this.videoAlbumsSorted.size()) {
            paramViewGroup.setAlbum(i, (MediaController.AlbumEntry)PhotoAlbumPickerActivity.this.videoAlbumsSorted.get(j));
          } else {
            paramViewGroup.setAlbum(i, null);
          }
        }
        label257:
        paramViewGroup.requestLayout();
        paramViewGroup = paramView;
      }
      do
      {
        do
        {
          return paramViewGroup;
          paramViewGroup = paramView;
        } while (i != 1);
        paramViewGroup = paramView;
      } while (paramView != null);
      paramView = new PhotoPickerSearchCell(this.mContext, PhotoAlbumPickerActivity.this.allowGifs);
      ((PhotoPickerSearchCell)paramView).setDelegate(new PhotoPickerSearchCell.PhotoPickerSearchCellDelegate()
      {
        public void didPressedSearchButton(int paramAnonymousInt)
        {
          PhotoAlbumPickerActivity.this.openPhotoPicker(null, paramAnonymousInt);
        }
      });
      return paramView;
    }
    
    public int getViewTypeCount()
    {
      if ((PhotoAlbumPickerActivity.this.singlePhoto) || (PhotoAlbumPickerActivity.this.selectedMode == 1)) {
        return 1;
      }
      return 2;
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
    
    public boolean isEmpty()
    {
      return getCount() == 0;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return true;
    }
  }
  
  public static abstract interface PhotoAlbumPickerActivityDelegate
  {
    public abstract void didSelectPhotos(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, ArrayList<ArrayList<TLRPC.InputDocument>> paramArrayList, ArrayList<MediaController.SearchImage> paramArrayList3);
    
    public abstract boolean didSelectVideo(String paramString);
    
    public abstract void startPhotoSelectActivity();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PhotoAlbumPickerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */