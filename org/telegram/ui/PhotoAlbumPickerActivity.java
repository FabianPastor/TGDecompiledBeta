package org.telegram.ui;

import android.content.Context;
import android.content.res.Configuration;
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
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate;
import org.telegram.ui.Cells.PhotoPickerSearchCell;
import org.telegram.ui.Cells.PhotoPickerSearchCell.PhotoPickerSearchCellDelegate;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PhotoAlbumPickerActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ArrayList<MediaController.AlbumEntry> albumsSorted = null;
  private boolean allowCaption;
  private boolean allowGifs;
  private ChatActivity chatActivity;
  private int columnsCount = 2;
  private PhotoAlbumPickerActivityDelegate delegate;
  private TextView emptyView;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean loading = false;
  private PickerBottomLayout pickerBottomLayout;
  private FrameLayout progressView;
  private ArrayList<MediaController.SearchImage> recentGifImages = new ArrayList();
  private HashMap<String, MediaController.SearchImage> recentImagesGifKeys = new HashMap();
  private HashMap<String, MediaController.SearchImage> recentImagesWebKeys = new HashMap();
  private ArrayList<MediaController.SearchImage> recentWebImages = new ArrayList();
  private HashMap<Object, Object> selectedPhotos = new HashMap();
  private ArrayList<Object> selectedPhotosOrder = new ArrayList();
  private boolean sendPressed;
  private boolean singlePhoto;
  
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
    for (;;)
    {
      return;
      int i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
      this.columnsCount = 2;
      if ((!AndroidUtilities.isTablet()) && ((i == 3) || (i == 1))) {
        this.columnsCount = 4;
      }
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  private void openPhotoPicker(MediaController.AlbumEntry paramAlbumEntry, int paramInt)
  {
    final HashMap localHashMap = null;
    Object localObject = localHashMap;
    if (paramAlbumEntry == null)
    {
      if (paramInt == 0) {
        localObject = this.recentWebImages;
      }
    }
    else
    {
      if (paramAlbumEntry == null) {
        break label91;
      }
      paramAlbumEntry = new PhotoPickerActivity(paramInt, paramAlbumEntry, this.selectedPhotos, this.selectedPhotosOrder, (ArrayList)localObject, this.singlePhoto, this.allowCaption, this.chatActivity);
      paramAlbumEntry.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate()
      {
        public void actionButtonPressed(boolean paramAnonymousBoolean)
        {
          PhotoAlbumPickerActivity.this.removeSelfFromStack();
          if (!paramAnonymousBoolean) {
            PhotoAlbumPickerActivity.this.sendSelectedPhotos(PhotoAlbumPickerActivity.this.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder);
          }
        }
        
        public void selectedPhotosChanged()
        {
          if (PhotoAlbumPickerActivity.this.pickerBottomLayout != null) {
            PhotoAlbumPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoAlbumPickerActivity.this.selectedPhotos.size(), true);
          }
        }
      });
    }
    for (;;)
    {
      presentFragment(paramAlbumEntry);
      return;
      localObject = localHashMap;
      if (paramInt != 1) {
        break;
      }
      localObject = this.recentGifImages;
      break;
      label91:
      localHashMap = new HashMap();
      final ArrayList localArrayList = new ArrayList();
      paramAlbumEntry = new PhotoPickerActivity(paramInt, paramAlbumEntry, localHashMap, localArrayList, (ArrayList)localObject, this.singlePhoto, this.allowCaption, this.chatActivity);
      paramAlbumEntry.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate()
      {
        public void actionButtonPressed(boolean paramAnonymousBoolean)
        {
          PhotoAlbumPickerActivity.this.removeSelfFromStack();
          if (!paramAnonymousBoolean) {
            PhotoAlbumPickerActivity.this.sendSelectedPhotos(localHashMap, localArrayList);
          }
        }
        
        public void selectedPhotosChanged() {}
      });
    }
  }
  
  private void sendSelectedPhotos(HashMap<Object, Object> paramHashMap, ArrayList<Object> paramArrayList)
  {
    if ((paramHashMap.isEmpty()) || (this.delegate == null) || (this.sendPressed)) {}
    for (;;)
    {
      return;
      this.sendPressed = true;
      int i = 0;
      int j = 0;
      ArrayList localArrayList = new ArrayList();
      int k = 0;
      if (k < paramArrayList.size())
      {
        Object localObject1 = paramHashMap.get(paramArrayList.get(k));
        SendMessagesHelper.SendingMediaInfo localSendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
        localArrayList.add(localSendingMediaInfo);
        Object localObject2;
        label125:
        label155:
        label197:
        int m;
        int n;
        if ((localObject1 instanceof MediaController.PhotoEntry))
        {
          localObject2 = (MediaController.PhotoEntry)localObject1;
          if (((MediaController.PhotoEntry)localObject2).isVideo)
          {
            localSendingMediaInfo.path = ((MediaController.PhotoEntry)localObject2).path;
            localSendingMediaInfo.videoEditedInfo = ((MediaController.PhotoEntry)localObject2).editedInfo;
            localSendingMediaInfo.isVideo = ((MediaController.PhotoEntry)localObject2).isVideo;
            if (((MediaController.PhotoEntry)localObject2).caption == null) {
              break label276;
            }
            localObject1 = ((MediaController.PhotoEntry)localObject2).caption.toString();
            localSendingMediaInfo.caption = ((String)localObject1);
            localSendingMediaInfo.entities = ((MediaController.PhotoEntry)localObject2).entities;
            if (((MediaController.PhotoEntry)localObject2).stickers.isEmpty()) {
              break label282;
            }
            localObject1 = new ArrayList(((MediaController.PhotoEntry)localObject2).stickers);
            localSendingMediaInfo.masks = ((ArrayList)localObject1);
            localSendingMediaInfo.ttl = ((MediaController.PhotoEntry)localObject2).ttl;
            m = j;
            n = i;
          }
        }
        for (;;)
        {
          k++;
          i = n;
          j = m;
          break;
          if (((MediaController.PhotoEntry)localObject2).imagePath != null)
          {
            localSendingMediaInfo.path = ((MediaController.PhotoEntry)localObject2).imagePath;
            break label125;
          }
          if (((MediaController.PhotoEntry)localObject2).path == null) {
            break label125;
          }
          localSendingMediaInfo.path = ((MediaController.PhotoEntry)localObject2).path;
          break label125;
          label276:
          localObject1 = null;
          break label155;
          label282:
          localObject1 = null;
          break label197;
          n = i;
          m = j;
          if ((localObject1 instanceof MediaController.SearchImage))
          {
            localObject2 = (MediaController.SearchImage)localObject1;
            if (((MediaController.SearchImage)localObject2).imagePath != null)
            {
              localSendingMediaInfo.path = ((MediaController.SearchImage)localObject2).imagePath;
              label328:
              if (((MediaController.SearchImage)localObject2).caption == null) {
                break label489;
              }
              localObject1 = ((MediaController.SearchImage)localObject2).caption.toString();
              label348:
              localSendingMediaInfo.caption = ((String)localObject1);
              localSendingMediaInfo.entities = ((MediaController.SearchImage)localObject2).entities;
              if (((MediaController.SearchImage)localObject2).stickers.isEmpty()) {
                break label495;
              }
            }
            label489:
            label495:
            for (localObject1 = new ArrayList(((MediaController.SearchImage)localObject2).stickers);; localObject1 = null)
            {
              localSendingMediaInfo.masks = ((ArrayList)localObject1);
              localSendingMediaInfo.ttl = ((MediaController.SearchImage)localObject2).ttl;
              ((MediaController.SearchImage)localObject2).date = ((int)(System.currentTimeMillis() / 1000L));
              if (((MediaController.SearchImage)localObject2).type != 0) {
                break label517;
              }
              m = 1;
              localObject1 = (MediaController.SearchImage)this.recentImagesWebKeys.get(((MediaController.SearchImage)localObject2).id);
              if (localObject1 == null) {
                break label501;
              }
              this.recentWebImages.remove(localObject1);
              this.recentWebImages.add(0, localObject1);
              n = i;
              break;
              localSendingMediaInfo.searchImage = ((MediaController.SearchImage)localObject2);
              break label328;
              localObject1 = null;
              break label348;
            }
            label501:
            this.recentWebImages.add(0, localObject2);
            n = i;
            continue;
            label517:
            n = i;
            m = j;
            if (((MediaController.SearchImage)localObject2).type == 1)
            {
              n = 1;
              localObject1 = (MediaController.SearchImage)this.recentImagesGifKeys.get(((MediaController.SearchImage)localObject2).id);
              if (localObject1 != null)
              {
                this.recentGifImages.remove(localObject1);
                this.recentGifImages.add(0, localObject1);
                m = j;
              }
              else
              {
                this.recentGifImages.add(0, localObject2);
                m = j;
              }
            }
          }
        }
      }
      if (j != 0) {
        MessagesStorage.getInstance(this.currentAccount).putWebRecent(this.recentWebImages);
      }
      if (i != 0) {
        MessagesStorage.getInstance(this.currentAccount).putWebRecent(this.recentGifImages);
      }
      this.delegate.didSelectPhotos(localArrayList);
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(-13421773);
    this.actionBar.setTitleColor(-1);
    this.actionBar.setItemsBackgroundColor(-12763843, false);
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          PhotoAlbumPickerActivity.this.finishFragment();
        }
        for (;;)
        {
          return;
          if ((paramAnonymousInt == 1) && (PhotoAlbumPickerActivity.this.delegate != null))
          {
            PhotoAlbumPickerActivity.this.finishFragment(false);
            PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
          }
        }
      }
    });
    this.actionBar.createMenu().addItem(1, NUM);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(-16777216);
    this.actionBar.setTitle(LocaleController.getString("Gallery", NUM));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F));
    this.listView.setClipToPadding(false);
    this.listView.setHorizontalScrollBarEnabled(false);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setDrawingCacheEnabled(false);
    localFrameLayout.addView(this.listView);
    Object localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).width = -1;
    ((FrameLayout.LayoutParams)localObject).height = -1;
    ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(48.0F);
    this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    localObject = this.listView;
    ListAdapter localListAdapter = new ListAdapter(paramContext);
    this.listAdapter = localListAdapter;
    ((RecyclerListView)localObject).setAdapter(localListAdapter);
    this.listView.setGlowColor(-13421773);
    this.emptyView = new TextView(paramContext);
    this.emptyView.setTextColor(-8355712);
    this.emptyView.setTextSize(20.0F);
    this.emptyView.setGravity(17);
    this.emptyView.setVisibility(8);
    this.emptyView.setText(LocaleController.getString("NoPhotos", NUM));
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
    localObject = new RadialProgressView(paramContext);
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
        PhotoAlbumPickerActivity.this.sendSelectedPhotos(PhotoAlbumPickerActivity.this.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder);
        PhotoAlbumPickerActivity.this.finishFragment();
      }
    });
    if ((this.loading) && ((this.albumsSorted == null) || ((this.albumsSorted != null) && (this.albumsSorted.isEmpty()))))
    {
      this.progressView.setVisibility(0);
      this.listView.setEmptyView(null);
    }
    for (;;)
    {
      this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size(), true);
      return this.fragmentView;
      this.progressView.setVisibility(8);
      this.listView.setEmptyView(this.emptyView);
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.albumsDidLoaded)
    {
      paramInt1 = ((Integer)paramVarArgs[0]).intValue();
      if (this.classGuid == paramInt1)
      {
        if (!this.singlePhoto) {
          break label106;
        }
        this.albumsSorted = ((ArrayList)paramVarArgs[2]);
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
      label106:
      this.albumsSorted = ((ArrayList)paramVarArgs[1]);
      break;
      if (paramInt1 == NotificationCenter.closeChats)
      {
        removeSelfFromStack();
      }
      else if (paramInt1 == NotificationCenter.recentImagesDidLoaded)
      {
        paramInt1 = ((Integer)paramVarArgs[0]).intValue();
        Iterator localIterator;
        if (paramInt1 == 0)
        {
          this.recentWebImages = ((ArrayList)paramVarArgs[1]);
          this.recentImagesWebKeys.clear();
          localIterator = this.recentWebImages.iterator();
          while (localIterator.hasNext())
          {
            paramVarArgs = (MediaController.SearchImage)localIterator.next();
            this.recentImagesWebKeys.put(paramVarArgs.id, paramVarArgs);
          }
        }
        else if (paramInt1 == 1)
        {
          this.recentGifImages = ((ArrayList)paramVarArgs[1]);
          this.recentImagesGifKeys.clear();
          localIterator = this.recentGifImages.iterator();
          while (localIterator.hasNext())
          {
            paramVarArgs = (MediaController.SearchImage)localIterator.next();
            this.recentImagesGifKeys.put(paramVarArgs.id, paramVarArgs);
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
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.albumsDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.albumsDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentImagesDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    super.onFragmentDestroy();
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
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      int i = 0;
      int j = 0;
      if (PhotoAlbumPickerActivity.this.singlePhoto) {
        if (PhotoAlbumPickerActivity.this.albumsSorted == null) {}
      }
      for (j = (int)Math.ceil(PhotoAlbumPickerActivity.this.albumsSorted.size() / PhotoAlbumPickerActivity.this.columnsCount);; j++)
      {
        return j;
        j = i;
        if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
          j = (int)Math.ceil(PhotoAlbumPickerActivity.this.albumsSorted.size() / PhotoAlbumPickerActivity.this.columnsCount);
        }
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      if (PhotoAlbumPickerActivity.this.singlePhoto) {}
      for (;;)
      {
        return i;
        if (paramInt == 0) {
          i = 1;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramViewHolder.getItemViewType() == 0)
      {
        paramViewHolder = (PhotoPickerAlbumsCell)paramViewHolder.itemView;
        paramViewHolder.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
        int i = 0;
        if (i < PhotoAlbumPickerActivity.this.columnsCount)
        {
          int j;
          if (PhotoAlbumPickerActivity.this.singlePhoto)
          {
            j = PhotoAlbumPickerActivity.this.columnsCount * paramInt + i;
            label62:
            if (j >= PhotoAlbumPickerActivity.this.albumsSorted.size()) {
              break label121;
            }
            paramViewHolder.setAlbum(i, (MediaController.AlbumEntry)PhotoAlbumPickerActivity.this.albumsSorted.get(j));
          }
          for (;;)
          {
            i++;
            break;
            j = (paramInt - 1) * PhotoAlbumPickerActivity.this.columnsCount + i;
            break label62;
            label121:
            paramViewHolder.setAlbum(i, null);
          }
        }
        paramViewHolder.requestLayout();
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new PhotoPickerSearchCell(this.mContext, PhotoAlbumPickerActivity.this.allowGifs);
        paramViewGroup.setDelegate(new PhotoPickerSearchCell.PhotoPickerSearchCellDelegate()
        {
          public void didPressedSearchButton(int paramAnonymousInt)
          {
            PhotoAlbumPickerActivity.this.openPhotoPicker(null, paramAnonymousInt);
          }
        });
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new PhotoPickerAlbumsCell(this.mContext);
        paramViewGroup.setDelegate(new PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate()
        {
          public void didSelectAlbum(MediaController.AlbumEntry paramAnonymousAlbumEntry)
          {
            PhotoAlbumPickerActivity.this.openPhotoPicker(paramAnonymousAlbumEntry, 0);
          }
        });
      }
    }
  }
  
  public static abstract interface PhotoAlbumPickerActivityDelegate
  {
    public abstract void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> paramArrayList);
    
    public abstract void startPhotoSelectActivity();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PhotoAlbumPickerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */