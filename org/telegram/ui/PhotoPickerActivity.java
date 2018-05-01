package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.Editable;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.FoundGif;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_foundGifs;
import org.telegram.tgnet.TLRPC.TL_messages_searchGifs;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PhotoPickerActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private boolean allowCaption = true;
  private boolean allowIndices;
  private boolean bingSearchEndReached = true;
  private ChatActivity chatActivity;
  private AsyncTask<Void, Void, JSONObject> currentBingTask;
  private PhotoPickerActivityDelegate delegate;
  private EmptyTextProgressView emptyView;
  private FrameLayout frameLayout;
  private int giphyReqId;
  private boolean giphySearchEndReached = true;
  private AnimatorSet hintAnimation;
  private Runnable hintHideRunnable;
  private TextView hintTextView;
  private ImageView imageOrderToggleButton;
  private int itemWidth = 100;
  private String lastSearchString;
  private int lastSearchToken;
  private GridLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean loadingRecent;
  private int nextGiphySearchOffset;
  private PickerBottomLayout pickerBottomLayout;
  private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider()
  {
    public boolean allowCaption()
    {
      return PhotoPickerActivity.this.allowCaption;
    }
    
    public boolean allowGroupPhotos()
    {
      if (PhotoPickerActivity.this.imageOrderToggleButton != null) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean cancelButtonPressed()
    {
      PhotoPickerActivity.this.delegate.actionButtonPressed(true);
      PhotoPickerActivity.this.finishFragment();
      return true;
    }
    
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramAnonymousMessageObject, TLRPC.FileLocation paramAnonymousFileLocation, int paramAnonymousInt)
    {
      paramAnonymousFileLocation = PhotoPickerActivity.this.getCellForIndex(paramAnonymousInt);
      if (paramAnonymousFileLocation != null)
      {
        int[] arrayOfInt = new int[2];
        paramAnonymousFileLocation.photoImage.getLocationInWindow(arrayOfInt);
        paramAnonymousMessageObject = new PhotoViewer.PlaceProviderObject();
        paramAnonymousMessageObject.viewX = arrayOfInt[0];
        int i = arrayOfInt[1];
        if (Build.VERSION.SDK_INT >= 21)
        {
          paramAnonymousInt = 0;
          paramAnonymousMessageObject.viewY = (i - paramAnonymousInt);
          paramAnonymousMessageObject.parentView = PhotoPickerActivity.this.listView;
          paramAnonymousMessageObject.imageReceiver = paramAnonymousFileLocation.photoImage.getImageReceiver();
          paramAnonymousMessageObject.thumb = paramAnonymousMessageObject.imageReceiver.getBitmapSafe();
          paramAnonymousMessageObject.scale = paramAnonymousFileLocation.photoImage.getScaleX();
          paramAnonymousFileLocation.showCheck(false);
        }
      }
      for (;;)
      {
        return paramAnonymousMessageObject;
        paramAnonymousInt = AndroidUtilities.statusBarHeight;
        break;
        paramAnonymousMessageObject = null;
      }
    }
    
    public int getSelectedCount()
    {
      return PhotoPickerActivity.this.selectedPhotos.size();
    }
    
    public HashMap<Object, Object> getSelectedPhotos()
    {
      return PhotoPickerActivity.this.selectedPhotos;
    }
    
    public ArrayList<Object> getSelectedPhotosOrder()
    {
      return PhotoPickerActivity.this.selectedPhotosOrder;
    }
    
    public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject paramAnonymousMessageObject, TLRPC.FileLocation paramAnonymousFileLocation, int paramAnonymousInt)
    {
      paramAnonymousMessageObject = PhotoPickerActivity.this.getCellForIndex(paramAnonymousInt);
      if (paramAnonymousMessageObject != null) {}
      for (paramAnonymousMessageObject = paramAnonymousMessageObject.photoImage.getImageReceiver().getBitmapSafe();; paramAnonymousMessageObject = null) {
        return paramAnonymousMessageObject;
      }
    }
    
    public boolean isPhotoChecked(int paramAnonymousInt)
    {
      boolean bool = true;
      if (PhotoPickerActivity.this.selectedAlbum != null)
      {
        if ((paramAnonymousInt >= 0) && (paramAnonymousInt < PhotoPickerActivity.this.selectedAlbum.photos.size()) && (PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(paramAnonymousInt)).imageId)))) {}
        for (bool = true;; bool = false) {
          return bool;
        }
      }
      ArrayList localArrayList;
      if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
      {
        localArrayList = PhotoPickerActivity.this.recentImages;
        label109:
        if ((paramAnonymousInt < 0) || (paramAnonymousInt >= localArrayList.size()) || (!PhotoPickerActivity.this.selectedPhotos.containsKey(((MediaController.SearchImage)localArrayList.get(paramAnonymousInt)).id))) {
          break label159;
        }
      }
      for (;;)
      {
        break;
        localArrayList = PhotoPickerActivity.this.searchResult;
        break label109;
        label159:
        bool = false;
      }
    }
    
    public boolean scaleToFill()
    {
      return false;
    }
    
    public void sendButtonPressed(int paramAnonymousInt, VideoEditedInfo paramAnonymousVideoEditedInfo)
    {
      if (PhotoPickerActivity.this.selectedPhotos.isEmpty())
      {
        if (PhotoPickerActivity.this.selectedAlbum == null) {
          break label88;
        }
        if ((paramAnonymousInt >= 0) && (paramAnonymousInt < PhotoPickerActivity.this.selectedAlbum.photos.size())) {}
      }
      label88:
      label157:
      for (;;)
      {
        return;
        MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(paramAnonymousInt);
        localPhotoEntry.editedInfo = paramAnonymousVideoEditedInfo;
        PhotoPickerActivity.this.addToSelectedPhotos(localPhotoEntry, -1);
        PhotoPickerActivity.this.sendSelectedPhotos();
        continue;
        if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null)) {}
        for (paramAnonymousVideoEditedInfo = PhotoPickerActivity.this.recentImages;; paramAnonymousVideoEditedInfo = PhotoPickerActivity.this.searchResult)
        {
          if ((paramAnonymousInt < 0) || (paramAnonymousInt >= paramAnonymousVideoEditedInfo.size())) {
            break label157;
          }
          PhotoPickerActivity.this.addToSelectedPhotos(paramAnonymousVideoEditedInfo.get(paramAnonymousInt), -1);
          break;
        }
      }
    }
    
    public int setPhotoChecked(int paramAnonymousInt, VideoEditedInfo paramAnonymousVideoEditedInfo)
    {
      int i = -1;
      boolean bool = true;
      int j;
      MediaController.PhotoEntry localPhotoEntry;
      label108:
      int k;
      if (PhotoPickerActivity.this.selectedAlbum != null)
      {
        j = i;
        if (paramAnonymousInt >= 0)
        {
          if (paramAnonymousInt >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
            j = i;
          }
        }
        else {
          return j;
        }
        localPhotoEntry = (MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(paramAnonymousInt);
        j = PhotoPickerActivity.this.addToSelectedPhotos(localPhotoEntry, -1);
        if (j == -1)
        {
          localPhotoEntry.editedInfo = paramAnonymousVideoEditedInfo;
          j = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(localPhotoEntry.imageId));
          k = PhotoPickerActivity.this.listView.getChildCount();
        }
      }
      for (i = 0;; i++)
      {
        if (i < k)
        {
          paramAnonymousVideoEditedInfo = PhotoPickerActivity.this.listView.getChildAt(i);
          if (((Integer)paramAnonymousVideoEditedInfo.getTag()).intValue() != paramAnonymousInt) {
            continue;
          }
          paramAnonymousVideoEditedInfo = (PhotoPickerPhotoCell)paramAnonymousVideoEditedInfo;
          if (!PhotoPickerActivity.this.allowIndices) {
            break label339;
          }
        }
        label333:
        label339:
        for (paramAnonymousInt = j;; paramAnonymousInt = -1)
        {
          paramAnonymousVideoEditedInfo.setChecked(paramAnonymousInt, bool, false);
          PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size(), true);
          PhotoPickerActivity.this.delegate.selectedPhotosChanged();
          break;
          bool = false;
          localPhotoEntry.editedInfo = null;
          break label108;
          if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null)) {}
          for (paramAnonymousVideoEditedInfo = PhotoPickerActivity.this.recentImages;; paramAnonymousVideoEditedInfo = PhotoPickerActivity.this.searchResult)
          {
            j = i;
            if (paramAnonymousInt < 0) {
              break;
            }
            j = i;
            if (paramAnonymousInt >= paramAnonymousVideoEditedInfo.size()) {
              break;
            }
            paramAnonymousVideoEditedInfo = (MediaController.SearchImage)paramAnonymousVideoEditedInfo.get(paramAnonymousInt);
            j = PhotoPickerActivity.this.addToSelectedPhotos(paramAnonymousVideoEditedInfo, -1);
            if (j != -1) {
              break label333;
            }
            j = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(paramAnonymousVideoEditedInfo.id);
            break label108;
          }
          bool = false;
          break label108;
        }
      }
    }
    
    public void toggleGroupPhotosEnabled()
    {
      ImageView localImageView;
      if (PhotoPickerActivity.this.imageOrderToggleButton != null)
      {
        localImageView = PhotoPickerActivity.this.imageOrderToggleButton;
        if (!SharedConfig.groupPhotosEnabled) {
          break label44;
        }
      }
      label44:
      for (PorterDuffColorFilter localPorterDuffColorFilter = new PorterDuffColorFilter(-10043398, PorterDuff.Mode.MULTIPLY);; localPorterDuffColorFilter = null)
      {
        localImageView.setColorFilter(localPorterDuffColorFilter);
        return;
      }
    }
    
    public void updatePhotoAtIndex(int paramAnonymousInt)
    {
      PhotoPickerPhotoCell localPhotoPickerPhotoCell = PhotoPickerActivity.this.getCellForIndex(paramAnonymousInt);
      Object localObject;
      if (localPhotoPickerPhotoCell != null)
      {
        if (PhotoPickerActivity.this.selectedAlbum == null) {
          break label242;
        }
        localPhotoPickerPhotoCell.photoImage.setOrientation(0, true);
        localObject = (MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(paramAnonymousInt);
        if (((MediaController.PhotoEntry)localObject).thumbPath == null) {
          break label83;
        }
        localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.PhotoEntry)localObject).thumbPath, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(NUM));
      }
      for (;;)
      {
        return;
        label83:
        if (((MediaController.PhotoEntry)localObject).path != null)
        {
          localPhotoPickerPhotoCell.photoImage.setOrientation(((MediaController.PhotoEntry)localObject).orientation, true);
          if (((MediaController.PhotoEntry)localObject).isVideo) {
            localPhotoPickerPhotoCell.photoImage.setImage("vthumb://" + ((MediaController.PhotoEntry)localObject).imageId + ":" + ((MediaController.PhotoEntry)localObject).path, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(NUM));
          } else {
            localPhotoPickerPhotoCell.photoImage.setImage("thumb://" + ((MediaController.PhotoEntry)localObject).imageId + ":" + ((MediaController.PhotoEntry)localObject).path, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(NUM));
          }
        }
        else
        {
          localPhotoPickerPhotoCell.photoImage.setImageResource(NUM);
          continue;
          label242:
          if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null)) {}
          for (localObject = PhotoPickerActivity.this.recentImages;; localObject = PhotoPickerActivity.this.searchResult)
          {
            localObject = (MediaController.SearchImage)((ArrayList)localObject).get(paramAnonymousInt);
            if ((((MediaController.SearchImage)localObject).document == null) || (((MediaController.SearchImage)localObject).document.thumb == null)) {
              break label344;
            }
            localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.SearchImage)localObject).document.thumb.location, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(NUM));
            break;
          }
          label344:
          if (((MediaController.SearchImage)localObject).thumbPath != null) {
            localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.SearchImage)localObject).thumbPath, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(NUM));
          } else if ((((MediaController.SearchImage)localObject).thumbUrl != null) && (((MediaController.SearchImage)localObject).thumbUrl.length() > 0)) {
            localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.SearchImage)localObject).thumbUrl, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(NUM));
          } else {
            localPhotoPickerPhotoCell.photoImage.setImageResource(NUM);
          }
        }
      }
    }
    
    public void willHidePhotoViewer()
    {
      int i = PhotoPickerActivity.this.listView.getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = PhotoPickerActivity.this.listView.getChildAt(j);
        if ((localView instanceof PhotoPickerPhotoCell)) {
          ((PhotoPickerPhotoCell)localView).showCheck(true);
        }
      }
    }
    
    public void willSwitchFromPhoto(MessageObject paramAnonymousMessageObject, TLRPC.FileLocation paramAnonymousFileLocation, int paramAnonymousInt)
    {
      int i = PhotoPickerActivity.this.listView.getChildCount();
      int j = 0;
      if (j < i)
      {
        paramAnonymousMessageObject = PhotoPickerActivity.this.listView.getChildAt(j);
        if (paramAnonymousMessageObject.getTag() == null) {
          j++;
        }
      }
      label42:
      label167:
      for (;;)
      {
        break;
        paramAnonymousFileLocation = (PhotoPickerPhotoCell)paramAnonymousMessageObject;
        int k = ((Integer)paramAnonymousMessageObject.getTag()).intValue();
        if (PhotoPickerActivity.this.selectedAlbum != null)
        {
          if ((k >= 0) && (k < PhotoPickerActivity.this.selectedAlbum.photos.size())) {
            if (k == paramAnonymousInt) {
              paramAnonymousFileLocation.showCheck(true);
            }
          }
        }
        else
        {
          if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null)) {}
          for (paramAnonymousMessageObject = PhotoPickerActivity.this.recentImages;; paramAnonymousMessageObject = PhotoPickerActivity.this.searchResult)
          {
            if (k < 0) {
              break label167;
            }
            if (k < paramAnonymousMessageObject.size()) {
              break;
            }
            break label42;
          }
        }
      }
    }
  };
  private ArrayList<MediaController.SearchImage> recentImages;
  private ActionBarMenuItem searchItem;
  private ArrayList<MediaController.SearchImage> searchResult = new ArrayList();
  private HashMap<String, MediaController.SearchImage> searchResultKeys = new HashMap();
  private HashMap<String, MediaController.SearchImage> searchResultUrls = new HashMap();
  private boolean searching;
  private MediaController.AlbumEntry selectedAlbum;
  private HashMap<Object, Object> selectedPhotos;
  private ArrayList<Object> selectedPhotosOrder;
  private boolean sendPressed;
  private boolean singlePhoto;
  private int type;
  
  public PhotoPickerActivity(int paramInt, MediaController.AlbumEntry paramAlbumEntry, HashMap<Object, Object> paramHashMap, ArrayList<Object> paramArrayList, ArrayList<MediaController.SearchImage> paramArrayList1, boolean paramBoolean1, boolean paramBoolean2, ChatActivity paramChatActivity)
  {
    this.selectedAlbum = paramAlbumEntry;
    this.selectedPhotos = paramHashMap;
    this.selectedPhotosOrder = paramArrayList;
    this.type = paramInt;
    this.recentImages = paramArrayList1;
    this.singlePhoto = paramBoolean1;
    this.chatActivity = paramChatActivity;
    this.allowCaption = paramBoolean2;
  }
  
  private int addToSelectedPhotos(Object paramObject, int paramInt)
  {
    int i = -1;
    Object localObject = null;
    if ((paramObject instanceof MediaController.PhotoEntry))
    {
      localObject = Integer.valueOf(((MediaController.PhotoEntry)paramObject).imageId);
      if (localObject != null) {
        break label50;
      }
    }
    for (;;)
    {
      return i;
      if (!(paramObject instanceof MediaController.SearchImage)) {
        break;
      }
      localObject = ((MediaController.SearchImage)paramObject).id;
      break;
      label50:
      if (this.selectedPhotos.containsKey(localObject))
      {
        this.selectedPhotos.remove(localObject);
        int j = this.selectedPhotosOrder.indexOf(localObject);
        if (j >= 0) {
          this.selectedPhotosOrder.remove(j);
        }
        if (this.allowIndices) {
          updateCheckedPhotoIndices();
        }
        i = j;
        if (paramInt >= 0)
        {
          if ((paramObject instanceof MediaController.PhotoEntry)) {
            ((MediaController.PhotoEntry)paramObject).reset();
          }
          for (;;)
          {
            this.provider.updatePhotoAtIndex(paramInt);
            i = j;
            break;
            if ((paramObject instanceof MediaController.SearchImage)) {
              ((MediaController.SearchImage)paramObject).reset();
            }
          }
        }
      }
      else
      {
        this.selectedPhotos.put(localObject, paramObject);
        this.selectedPhotosOrder.add(localObject);
      }
    }
  }
  
  private void fixLayout()
  {
    if (this.listView != null) {
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          PhotoPickerActivity.this.fixLayoutInternal();
          if (PhotoPickerActivity.this.listView != null) {
            PhotoPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          }
          return true;
        }
      });
    }
  }
  
  private void fixLayoutInternal()
  {
    if (getParentActivity() == null) {
      return;
    }
    int i = this.layoutManager.findFirstVisibleItemPosition();
    int j = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
    if (AndroidUtilities.isTablet())
    {
      j = 3;
      label45:
      this.layoutManager.setSpanCount(j);
      if (!AndroidUtilities.isTablet()) {
        break label153;
      }
    }
    label153:
    for (this.itemWidth = ((AndroidUtilities.dp(490.0F) - (j + 1) * AndroidUtilities.dp(4.0F)) / j);; this.itemWidth = ((AndroidUtilities.displaySize.x - (j + 1) * AndroidUtilities.dp(4.0F)) / j))
    {
      this.listAdapter.notifyDataSetChanged();
      this.layoutManager.scrollToPosition(i);
      if (this.selectedAlbum != null) {
        break;
      }
      this.emptyView.setPadding(0, 0, 0, (int)((AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) * 0.4F));
      break;
      if ((j == 3) || (j == 1))
      {
        j = 5;
        break label45;
      }
      j = 3;
      break label45;
    }
  }
  
  private PhotoPickerPhotoCell getCellForIndex(int paramInt)
  {
    int i = this.listView.getChildCount();
    int j = 0;
    PhotoPickerPhotoCell localPhotoPickerPhotoCell;
    if (j < i)
    {
      localObject = this.listView.getChildAt(j);
      int k;
      if ((localObject instanceof PhotoPickerPhotoCell))
      {
        localPhotoPickerPhotoCell = (PhotoPickerPhotoCell)localObject;
        k = ((Integer)localPhotoPickerPhotoCell.photoImage.getTag()).intValue();
        if (this.selectedAlbum == null) {
          break label89;
        }
        if ((k >= 0) && (k < this.selectedAlbum.photos.size())) {
          break label127;
        }
      }
      label89:
      label112:
      label127:
      while (k != paramInt)
      {
        j++;
        break;
        if ((!this.searchResult.isEmpty()) || (this.lastSearchString != null)) {
          break label140;
        }
        localObject = this.recentImages;
        if ((k < 0) || (k >= ((ArrayList)localObject).size())) {
          break label147;
        }
      }
    }
    for (Object localObject = localPhotoPickerPhotoCell;; localObject = null)
    {
      return (PhotoPickerPhotoCell)localObject;
      label140:
      localObject = this.searchResult;
      break label112;
      label147:
      break;
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
        if (paramAnonymousAnimator.equals(PhotoPickerActivity.this.hintAnimation))
        {
          PhotoPickerActivity.access$3802(PhotoPickerActivity.this, null);
          PhotoPickerActivity.access$3802(PhotoPickerActivity.this, null);
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (paramAnonymousAnimator.equals(PhotoPickerActivity.this.hintAnimation))
        {
          PhotoPickerActivity.access$3702(PhotoPickerActivity.this, null);
          PhotoPickerActivity.access$3802(PhotoPickerActivity.this, null);
          if (PhotoPickerActivity.this.hintTextView != null) {
            PhotoPickerActivity.this.hintTextView.setVisibility(8);
          }
        }
      }
    });
    this.hintAnimation.setDuration(300L);
    this.hintAnimation.start();
  }
  
  private void searchBingImages(String paramString, int paramInt1, int paramInt2)
  {
    if (this.searching)
    {
      this.searching = false;
      if (this.giphyReqId != 0)
      {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.giphyReqId, true);
        this.giphyReqId = 0;
      }
      if (this.currentBingTask != null)
      {
        this.currentBingTask.cancel(true);
        this.currentBingTask = null;
      }
    }
    for (;;)
    {
      try
      {
        this.searching = true;
        Object localObject = UserConfig.getInstance(this.currentAccount).getCurrentUser().phone;
        if ((((String)localObject).startsWith("44")) || (((String)localObject).startsWith("49")) || (((String)localObject).startsWith("43")) || (((String)localObject).startsWith("31")) || (((String)localObject).startsWith("1")))
        {
          i = 1;
          localObject = Locale.US;
          String str = URLEncoder.encode(paramString, "UTF-8");
          if (i == 0) {
            continue;
          }
          paramString = "Strict";
          localObject = String.format((Locale)localObject, "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q='%s'&offset=%d&count=%d&$format=json&safeSearch=%s", new Object[] { str, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString });
          paramString = new org/telegram/ui/PhotoPickerActivity$16;
          paramString.<init>(this, (String)localObject);
          this.currentBingTask = paramString;
          this.currentBingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          return;
        }
      }
      catch (Exception paramString)
      {
        int i;
        FileLog.e(paramString);
        this.bingSearchEndReached = true;
        this.searching = false;
        this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
        if (((!this.searching) || (!this.searchResult.isEmpty())) && ((!this.loadingRecent) || (this.lastSearchString != null))) {
          continue;
        }
        this.emptyView.showProgress();
        continue;
        this.emptyView.showTextView();
        continue;
      }
      i = 0;
      continue;
      paramString = "Off";
    }
  }
  
  private void searchGiphyImages(final String paramString, final int paramInt)
  {
    if (this.searching)
    {
      this.searching = false;
      if (this.giphyReqId != 0)
      {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.giphyReqId, true);
        this.giphyReqId = 0;
      }
      if (this.currentBingTask != null)
      {
        this.currentBingTask.cancel(true);
        this.currentBingTask = null;
      }
    }
    this.searching = true;
    TLRPC.TL_messages_searchGifs localTL_messages_searchGifs = new TLRPC.TL_messages_searchGifs();
    localTL_messages_searchGifs.q = paramString;
    localTL_messages_searchGifs.offset = paramInt;
    paramInt = this.lastSearchToken + 1;
    this.lastSearchToken = paramInt;
    this.giphyReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_searchGifs, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            boolean bool = true;
            if (PhotoPickerActivity.15.this.val$token != PhotoPickerActivity.this.lastSearchToken) {}
            for (;;)
            {
              return;
              int i = 0;
              int j = 0;
              if (paramAnonymousTLObject != null)
              {
                int k = 0;
                Object localObject1 = (TLRPC.TL_messages_foundGifs)paramAnonymousTLObject;
                PhotoPickerActivity.access$2602(PhotoPickerActivity.this, ((TLRPC.TL_messages_foundGifs)localObject1).next_offset);
                int m = 0;
                i = j;
                j = k;
                while (m < ((TLRPC.TL_messages_foundGifs)localObject1).results.size())
                {
                  TLRPC.FoundGif localFoundGif = (TLRPC.FoundGif)((TLRPC.TL_messages_foundGifs)localObject1).results.get(m);
                  if (PhotoPickerActivity.this.searchResultKeys.containsKey(localFoundGif.url))
                  {
                    m++;
                  }
                  else
                  {
                    k = 1;
                    MediaController.SearchImage localSearchImage = new MediaController.SearchImage();
                    localSearchImage.id = localFoundGif.url;
                    label157:
                    Object localObject2;
                    if (localFoundGif.document != null)
                    {
                      j = 0;
                      if (j < localFoundGif.document.attributes.size())
                      {
                        localObject2 = (TLRPC.DocumentAttribute)localFoundGif.document.attributes.get(j);
                        if ((!(localObject2 instanceof TLRPC.TL_documentAttributeImageSize)) && (!(localObject2 instanceof TLRPC.TL_documentAttributeVideo))) {
                          break label406;
                        }
                        localSearchImage.width = ((TLRPC.DocumentAttribute)localObject2).w;
                      }
                    }
                    for (localSearchImage.height = ((TLRPC.DocumentAttribute)localObject2).h;; localSearchImage.height = localFoundGif.h)
                    {
                      localSearchImage.size = 0;
                      localSearchImage.imageUrl = localFoundGif.content_url;
                      localSearchImage.thumbUrl = localFoundGif.thumb_url;
                      localSearchImage.localUrl = (localFoundGif.url + "|" + PhotoPickerActivity.15.this.val$query);
                      localSearchImage.document = localFoundGif.document;
                      if ((localFoundGif.photo != null) && (localFoundGif.document != null))
                      {
                        localObject2 = FileLoader.getClosestPhotoSizeWithSize(localFoundGif.photo.sizes, PhotoPickerActivity.this.itemWidth, true);
                        if (localObject2 != null) {
                          localFoundGif.document.thumb = ((TLRPC.PhotoSize)localObject2);
                        }
                      }
                      localSearchImage.type = 1;
                      PhotoPickerActivity.this.searchResult.add(localSearchImage);
                      i++;
                      PhotoPickerActivity.this.searchResultKeys.put(localSearchImage.id, localSearchImage);
                      j = k;
                      break;
                      label406:
                      j++;
                      break label157;
                      localSearchImage.width = localFoundGif.w;
                    }
                  }
                }
                localObject1 = PhotoPickerActivity.this;
                if (j == 0) {
                  label448:
                  PhotoPickerActivity.access$1702((PhotoPickerActivity)localObject1, bool);
                }
              }
              else
              {
                PhotoPickerActivity.access$1802(PhotoPickerActivity.this, false);
                if (i == 0) {
                  break label574;
                }
                PhotoPickerActivity.this.listAdapter.notifyItemRangeInserted(PhotoPickerActivity.this.searchResult.size(), i);
              }
              for (;;)
              {
                if (((!PhotoPickerActivity.this.searching) || (!PhotoPickerActivity.this.searchResult.isEmpty())) && ((!PhotoPickerActivity.this.loadingRecent) || (PhotoPickerActivity.this.lastSearchString != null))) {
                  break label618;
                }
                PhotoPickerActivity.this.emptyView.showProgress();
                break;
                bool = false;
                break label448;
                label574:
                if (PhotoPickerActivity.this.giphySearchEndReached) {
                  PhotoPickerActivity.this.listAdapter.notifyItemRemoved(PhotoPickerActivity.this.searchResult.size() - 1);
                }
              }
              label618:
              PhotoPickerActivity.this.emptyView.showTextView();
            }
          }
        });
      }
    });
    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.giphyReqId, this.classGuid);
  }
  
  private void sendSelectedPhotos()
  {
    if ((this.selectedPhotos.isEmpty()) || (this.delegate == null) || (this.sendPressed)) {}
    for (;;)
    {
      return;
      this.sendPressed = true;
      this.delegate.actionButtonPressed(false);
      finishFragment();
    }
  }
  
  private void showHint(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((getParentActivity() == null) || (this.fragmentView == null) || ((paramBoolean1) && (this.hintTextView == null))) {}
    for (;;)
    {
      return;
      if (this.hintTextView == null)
      {
        this.hintTextView = new TextView(getParentActivity());
        this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0F), Theme.getColor("chat_gifSaveHintBackground")));
        this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.hintTextView.setTextSize(1, 14.0F);
        this.hintTextView.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(7.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(7.0F));
        this.hintTextView.setGravity(16);
        this.hintTextView.setAlpha(0.0F);
        this.frameLayout.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0F, 81, 5.0F, 0.0F, 5.0F, 51.0F));
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
          label235:
          localTextView.setText((CharSequence)localObject);
          if (this.hintHideRunnable == null) {
            break label401;
          }
          if (this.hintAnimation == null) {
            break label367;
          }
          this.hintAnimation.cancel();
          this.hintAnimation = null;
        }
        label367:
        label401:
        while (this.hintAnimation == null)
        {
          this.hintTextView.setVisibility(0);
          this.hintAnimation = new AnimatorSet();
          this.hintAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[] { 1.0F }) });
          this.hintAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationCancel(Animator paramAnonymousAnimator)
            {
              if (paramAnonymousAnimator.equals(PhotoPickerActivity.this.hintAnimation)) {
                PhotoPickerActivity.access$3702(PhotoPickerActivity.this, null);
              }
            }
            
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if (paramAnonymousAnimator.equals(PhotoPickerActivity.this.hintAnimation))
              {
                PhotoPickerActivity.access$3702(PhotoPickerActivity.this, null);
                AndroidUtilities.runOnUIThread(PhotoPickerActivity.access$3802(PhotoPickerActivity.this, new Runnable()
                {
                  public void run()
                  {
                    PhotoPickerActivity.this.hideHint();
                  }
                }), 2000L);
              }
            }
          });
          this.hintAnimation.setDuration(300L);
          this.hintAnimation.start();
          break;
          localObject = LocaleController.getString("SinglePhotosHelp", NUM);
          break label235;
          AndroidUtilities.cancelRunOnUIThread(this.hintHideRunnable);
          localObject = new Runnable()
          {
            public void run()
            {
              PhotoPickerActivity.this.hideHint();
            }
          };
          this.hintHideRunnable = ((Runnable)localObject);
          AndroidUtilities.runOnUIThread((Runnable)localObject, 2000L);
          break;
        }
      }
    }
  }
  
  private void updateCheckedPhotoIndices()
  {
    if (!this.allowIndices) {
      return;
    }
    int i = this.listView.getChildCount();
    int j = 0;
    label18:
    Object localObject;
    PhotoPickerPhotoCell localPhotoPickerPhotoCell;
    if (j < i)
    {
      localObject = this.listView.getChildAt(j);
      if ((localObject instanceof PhotoPickerPhotoCell))
      {
        localPhotoPickerPhotoCell = (PhotoPickerPhotoCell)localObject;
        localObject = (Integer)localPhotoPickerPhotoCell.getTag();
        if (this.selectedAlbum == null) {
          break label121;
        }
        localObject = (MediaController.PhotoEntry)this.selectedAlbum.photos.get(((Integer)localObject).intValue());
        if (!this.allowIndices) {
          break label115;
        }
      }
    }
    label115:
    for (int k = this.selectedPhotosOrder.indexOf(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId));; k = -1)
    {
      localPhotoPickerPhotoCell.setNum(k);
      j++;
      break label18;
      break;
    }
    label121:
    if ((this.searchResult.isEmpty()) && (this.lastSearchString == null))
    {
      localObject = (MediaController.SearchImage)this.recentImages.get(((Integer)localObject).intValue());
      label153:
      if (!this.allowIndices) {
        break label201;
      }
    }
    label201:
    for (k = this.selectedPhotosOrder.indexOf(((MediaController.SearchImage)localObject).id);; k = -1)
    {
      localPhotoPickerPhotoCell.setNum(k);
      break;
      localObject = (MediaController.SearchImage)this.searchResult.get(((Integer)localObject).intValue());
      break label153;
    }
  }
  
  private void updateSearchInterface()
  {
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    if (((this.searching) && (this.searchResult.isEmpty())) || ((this.loadingRecent) && (this.lastSearchString == null))) {
      this.emptyView.showProgress();
    }
    for (;;)
    {
      return;
      this.emptyView.showTextView();
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(-13421773);
    this.actionBar.setItemsBackgroundColor(-12763843, false);
    this.actionBar.setTitleColor(-1);
    this.actionBar.setBackButtonImage(NUM);
    label148:
    Object localObject2;
    float f;
    if (this.selectedAlbum != null)
    {
      this.actionBar.setTitle(this.selectedAlbum.bucketName);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            PhotoPickerActivity.this.finishFragment();
          }
        }
      });
      if (this.selectedAlbum == null) {
        this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
        {
          public boolean canCollapseSearch()
          {
            PhotoPickerActivity.this.finishFragment();
            return false;
          }
          
          public void onSearchExpand() {}
          
          public void onSearchPressed(EditText paramAnonymousEditText)
          {
            if (paramAnonymousEditText.getText().toString().length() == 0) {
              return;
            }
            PhotoPickerActivity.this.searchResult.clear();
            PhotoPickerActivity.this.searchResultKeys.clear();
            PhotoPickerActivity.access$1602(PhotoPickerActivity.this, true);
            PhotoPickerActivity.access$1702(PhotoPickerActivity.this, true);
            if (PhotoPickerActivity.this.type == 0)
            {
              PhotoPickerActivity.this.searchBingImages(paramAnonymousEditText.getText().toString(), 0, 53);
              label79:
              PhotoPickerActivity.access$402(PhotoPickerActivity.this, paramAnonymousEditText.getText().toString());
              if (PhotoPickerActivity.this.lastSearchString.length() != 0) {
                break label222;
              }
              PhotoPickerActivity.access$402(PhotoPickerActivity.this, null);
              if (PhotoPickerActivity.this.type != 0) {
                break label191;
              }
              PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", NUM));
            }
            for (;;)
            {
              PhotoPickerActivity.this.updateSearchInterface();
              break;
              if (PhotoPickerActivity.this.type != 1) {
                break label79;
              }
              PhotoPickerActivity.access$2602(PhotoPickerActivity.this, 0);
              PhotoPickerActivity.this.searchGiphyImages(paramAnonymousEditText.getText().toString(), 0);
              break label79;
              label191:
              if (PhotoPickerActivity.this.type == 1)
              {
                PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", NUM));
                continue;
                label222:
                PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
              }
            }
          }
          
          public void onTextChanged(EditText paramAnonymousEditText)
          {
            if (paramAnonymousEditText.getText().length() == 0)
            {
              PhotoPickerActivity.this.searchResult.clear();
              PhotoPickerActivity.this.searchResultKeys.clear();
              PhotoPickerActivity.access$402(PhotoPickerActivity.this, null);
              PhotoPickerActivity.access$1602(PhotoPickerActivity.this, true);
              PhotoPickerActivity.access$1702(PhotoPickerActivity.this, true);
              PhotoPickerActivity.access$1802(PhotoPickerActivity.this, false);
              if (PhotoPickerActivity.this.currentBingTask != null)
              {
                PhotoPickerActivity.this.currentBingTask.cancel(true);
                PhotoPickerActivity.access$1902(PhotoPickerActivity.this, null);
              }
              if (PhotoPickerActivity.this.giphyReqId != 0)
              {
                ConnectionsManager.getInstance(PhotoPickerActivity.this.currentAccount).cancelRequest(PhotoPickerActivity.this.giphyReqId, true);
                PhotoPickerActivity.access$2002(PhotoPickerActivity.this, 0);
              }
              if (PhotoPickerActivity.this.type != 0) {
                break label174;
              }
              PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", NUM));
            }
            for (;;)
            {
              PhotoPickerActivity.this.updateSearchInterface();
              return;
              label174:
              if (PhotoPickerActivity.this.type == 1) {
                PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", NUM));
              }
            }
          }
        });
      }
      if (this.selectedAlbum == null)
      {
        if (this.type != 0) {
          break label732;
        }
        this.searchItem.getSearchField().setHint(LocaleController.getString("SearchImagesTitle", NUM));
      }
      this.fragmentView = new FrameLayout(paramContext);
      this.frameLayout = ((FrameLayout)this.fragmentView);
      this.frameLayout.setBackgroundColor(-16777216);
      this.listView = new RecyclerListView(paramContext);
      this.listView.setPadding(AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F));
      this.listView.setClipToPadding(false);
      this.listView.setHorizontalScrollBarEnabled(false);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setItemAnimator(null);
      this.listView.setLayoutAnimation(null);
      Object localObject1 = this.listView;
      localObject2 = new GridLayoutManager(paramContext, 4)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.layoutManager = ((GridLayoutManager)localObject2);
      ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
      this.listView.addItemDecoration(new RecyclerView.ItemDecoration()
      {
        public void getItemOffsets(Rect paramAnonymousRect, View paramAnonymousView, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
        {
          int i = 0;
          super.getItemOffsets(paramAnonymousRect, paramAnonymousView, paramAnonymousRecyclerView, paramAnonymousState);
          int j = paramAnonymousState.getItemCount();
          int k = paramAnonymousRecyclerView.getChildAdapterPosition(paramAnonymousView);
          int m = PhotoPickerActivity.this.layoutManager.getSpanCount();
          j = (int)Math.ceil(j / m);
          int n = k / m;
          if (k % m != m - 1) {}
          for (k = AndroidUtilities.dp(4.0F);; k = 0)
          {
            paramAnonymousRect.right = k;
            k = i;
            if (n != j - 1) {
              k = AndroidUtilities.dp(4.0F);
            }
            paramAnonymousRect.bottom = k;
            return;
          }
        }
      });
      localObject1 = this.frameLayout;
      localObject2 = this.listView;
      if (!this.singlePhoto) {
        break label762;
      }
      f = 0.0F;
      label325:
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, f));
      localObject2 = this.listView;
      localObject1 = new ListAdapter(paramContext);
      this.listAdapter = ((ListAdapter)localObject1);
      ((RecyclerListView)localObject2).setAdapter((RecyclerView.Adapter)localObject1);
      this.listView.setGlowColor(-13421773);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (PhotoPickerActivity.this.selectedAlbum != null) {
            paramAnonymousView = PhotoPickerActivity.this.selectedAlbum.photos;
          }
          while ((paramAnonymousInt < 0) || (paramAnonymousInt >= paramAnonymousView.size()))
          {
            return;
            if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null)) {
              paramAnonymousView = PhotoPickerActivity.this.recentImages;
            } else {
              paramAnonymousView = PhotoPickerActivity.this.searchResult;
            }
          }
          if (PhotoPickerActivity.this.searchItem != null) {
            AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.searchItem.getSearchField());
          }
          PhotoViewer.getInstance().setParentActivity(PhotoPickerActivity.this.getParentActivity());
          PhotoViewer localPhotoViewer = PhotoViewer.getInstance();
          if (PhotoPickerActivity.this.singlePhoto) {}
          for (int i = 1;; i = 0)
          {
            localPhotoViewer.openPhotoForSelect(paramAnonymousView, paramAnonymousInt, i, PhotoPickerActivity.this.provider, PhotoPickerActivity.this.chatActivity);
            break;
          }
        }
      });
      if (this.selectedAlbum == null) {
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
        {
          public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
          {
            if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
            {
              paramAnonymousView = new AlertDialog.Builder(PhotoPickerActivity.this.getParentActivity());
              paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
              paramAnonymousView.setMessage(LocaleController.getString("ClearSearch", NUM));
              paramAnonymousView.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  PhotoPickerActivity.this.recentImages.clear();
                  if (PhotoPickerActivity.this.listAdapter != null) {
                    PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                  }
                  MessagesStorage.getInstance(PhotoPickerActivity.this.currentAccount).clearWebRecent(PhotoPickerActivity.this.type);
                }
              });
              paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
              PhotoPickerActivity.this.showDialog(paramAnonymousView.create());
            }
            for (boolean bool = true;; bool = false) {
              return bool;
            }
          }
        });
      }
      this.emptyView = new EmptyTextProgressView(paramContext);
      this.emptyView.setTextColor(-8355712);
      this.emptyView.setProgressBarColor(-1);
      this.emptyView.setShowAtCenter(true);
      if (this.selectedAlbum == null) {
        break label770;
      }
      this.emptyView.setText(LocaleController.getString("NoPhotos", NUM));
      label477:
      localObject1 = this.frameLayout;
      localObject2 = this.emptyView;
      if (!this.singlePhoto) {
        break label823;
      }
      f = 0.0F;
      label497:
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, f));
      if (this.selectedAlbum == null)
      {
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
          public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
          {
            if (paramAnonymousInt == 1) {
              AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
            }
          }
          
          public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            paramAnonymousInt2 = PhotoPickerActivity.this.layoutManager.findFirstVisibleItemPosition();
            if (paramAnonymousInt2 == -1)
            {
              paramAnonymousInt1 = 0;
              if (paramAnonymousInt1 > 0)
              {
                int i = PhotoPickerActivity.this.layoutManager.getItemCount();
                if ((paramAnonymousInt1 != 0) && (paramAnonymousInt2 + paramAnonymousInt1 > i - 2) && (!PhotoPickerActivity.this.searching))
                {
                  if ((PhotoPickerActivity.this.type != 0) || (PhotoPickerActivity.this.bingSearchEndReached)) {
                    break label126;
                  }
                  PhotoPickerActivity.this.searchBingImages(PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.searchResult.size(), 54);
                }
              }
            }
            for (;;)
            {
              return;
              paramAnonymousInt1 = Math.abs(PhotoPickerActivity.this.layoutManager.findLastVisibleItemPosition() - paramAnonymousInt2) + 1;
              break;
              label126:
              if ((PhotoPickerActivity.this.type == 1) && (!PhotoPickerActivity.this.giphySearchEndReached)) {
                PhotoPickerActivity.this.searchGiphyImages(PhotoPickerActivity.this.searchItem.getSearchField().getText().toString(), PhotoPickerActivity.this.nextGiphySearchOffset);
              }
            }
          }
        });
        updateSearchInterface();
      }
      this.pickerBottomLayout = new PickerBottomLayout(paramContext);
      this.frameLayout.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 80));
      this.pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          PhotoPickerActivity.this.delegate.actionButtonPressed(true);
          PhotoPickerActivity.this.finishFragment();
        }
      });
      this.pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          PhotoPickerActivity.this.sendSelectedPhotos();
        }
      });
      if (!this.singlePhoto) {
        break label831;
      }
      this.pickerBottomLayout.setVisibility(8);
      label625:
      if ((this.selectedAlbum == null) && (this.type != 0)) {
        break label966;
      }
    }
    label732:
    label762:
    label770:
    label823:
    label831:
    label966:
    for (boolean bool = true;; bool = false)
    {
      this.allowIndices = bool;
      this.listView.setEmptyView(this.emptyView);
      this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size(), true);
      return this.fragmentView;
      if (this.type == 0)
      {
        this.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", NUM));
        break;
      }
      if (this.type != 1) {
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", NUM));
      break;
      if (this.type != 1) {
        break label148;
      }
      this.searchItem.getSearchField().setHint(LocaleController.getString("SearchGifsTitle", NUM));
      break label148;
      f = 48.0F;
      break label325;
      if (this.type == 0)
      {
        this.emptyView.setText(LocaleController.getString("NoRecentPhotos", NUM));
        break label477;
      }
      if (this.type != 1) {
        break label477;
      }
      this.emptyView.setText(LocaleController.getString("NoRecentGIFs", NUM));
      break label477;
      f = 48.0F;
      break label497;
      if (((this.selectedAlbum == null) && (this.type != 0)) || (this.chatActivity == null) || (!this.chatActivity.allowGroupPhotos())) {
        break label625;
      }
      this.imageOrderToggleButton = new ImageView(paramContext);
      this.imageOrderToggleButton.setScaleType(ImageView.ScaleType.CENTER);
      this.imageOrderToggleButton.setImageResource(NUM);
      this.pickerBottomLayout.addView(this.imageOrderToggleButton, LayoutHelper.createFrame(48, -1, 17));
      this.imageOrderToggleButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          SharedConfig.toggleGroupPhotosEnabled();
          ImageView localImageView = PhotoPickerActivity.this.imageOrderToggleButton;
          if (SharedConfig.groupPhotosEnabled) {}
          for (paramAnonymousView = new PorterDuffColorFilter(-10043398, PorterDuff.Mode.MULTIPLY);; paramAnonymousView = null)
          {
            localImageView.setColorFilter(paramAnonymousView);
            PhotoPickerActivity.this.showHint(false, SharedConfig.groupPhotosEnabled);
            PhotoPickerActivity.this.updateCheckedPhotoIndices();
            return;
          }
        }
      });
      localObject2 = this.imageOrderToggleButton;
      if (SharedConfig.groupPhotosEnabled) {}
      for (paramContext = new PorterDuffColorFilter(-10043398, PorterDuff.Mode.MULTIPLY);; paramContext = null)
      {
        ((ImageView)localObject2).setColorFilter(paramContext);
        break;
      }
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.closeChats) {
      removeSelfFromStack();
    }
    for (;;)
    {
      return;
      if ((paramInt1 == NotificationCenter.recentImagesDidLoaded) && (this.selectedAlbum == null) && (this.type == ((Integer)paramVarArgs[0]).intValue()))
      {
        this.recentImages = ((ArrayList)paramVarArgs[1]);
        this.loadingRecent = false;
        updateSearchInterface();
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
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoaded);
    if ((this.selectedAlbum == null) && (this.recentImages.isEmpty()))
    {
      MessagesStorage.getInstance(this.currentAccount).loadWebRecent(this.type);
      this.loadingRecent = true;
    }
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentImagesDidLoaded);
    if (this.currentBingTask != null)
    {
      this.currentBingTask.cancel(true);
      this.currentBingTask = null;
    }
    if (this.giphyReqId != 0)
    {
      ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.giphyReqId, true);
      this.giphyReqId = 0;
    }
    super.onFragmentDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    if (this.searchItem != null)
    {
      this.searchItem.openSearch(true);
      getParentActivity().getWindow().setSoftInputMode(32);
    }
    fixLayout();
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.searchItem != null)) {
      AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
    }
  }
  
  public void setDelegate(PhotoPickerActivityDelegate paramPhotoPickerActivityDelegate)
  {
    this.delegate = paramPhotoPickerActivityDelegate;
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
      if (PhotoPickerActivity.this.selectedAlbum == null) {
        if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null)) {
          j = PhotoPickerActivity.this.recentImages.size();
        }
      }
      for (;;)
      {
        return j;
        if (PhotoPickerActivity.this.type == 0)
        {
          i = PhotoPickerActivity.this.searchResult.size();
          if (PhotoPickerActivity.this.bingSearchEndReached) {}
          for (;;)
          {
            j += i;
            break;
            j = 1;
          }
        }
        if (PhotoPickerActivity.this.type == 1)
        {
          int k = PhotoPickerActivity.this.searchResult.size();
          if (PhotoPickerActivity.this.giphySearchEndReached) {}
          for (j = i;; j = 1)
          {
            j += k;
            break;
          }
        }
        j = PhotoPickerActivity.this.selectedAlbum.photos.size();
      }
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((PhotoPickerActivity.this.selectedAlbum != null) || ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null) && (paramInt < PhotoPickerActivity.this.recentImages.size())) || (paramInt < PhotoPickerActivity.this.searchResult.size())) {}
      for (paramInt = 0;; paramInt = 1) {
        return paramInt;
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool1 = true;
      boolean bool2 = bool1;
      int i;
      if (PhotoPickerActivity.this.selectedAlbum == null)
      {
        i = paramViewHolder.getAdapterPosition();
        if ((!PhotoPickerActivity.this.searchResult.isEmpty()) || (PhotoPickerActivity.this.lastSearchString != null)) {
          break label67;
        }
        if (i >= PhotoPickerActivity.this.recentImages.size()) {
          break label62;
        }
        bool2 = bool1;
      }
      for (;;)
      {
        return bool2;
        label62:
        bool2 = false;
        continue;
        label67:
        bool2 = bool1;
        if (i >= PhotoPickerActivity.this.searchResult.size()) {
          bool2 = false;
        }
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        Object localObject = (PhotoPickerPhotoCell)paramViewHolder.itemView;
        ((PhotoPickerPhotoCell)localObject).itemWidth = PhotoPickerActivity.this.itemWidth;
        BackupImageView localBackupImageView = ((PhotoPickerPhotoCell)localObject).photoImage;
        localBackupImageView.setTag(Integer.valueOf(paramInt));
        ((PhotoPickerPhotoCell)localObject).setTag(Integer.valueOf(paramInt));
        localBackupImageView.setOrientation(0, true);
        label135:
        label163:
        boolean bool1;
        label195:
        boolean bool2;
        if (PhotoPickerActivity.this.selectedAlbum != null)
        {
          paramViewHolder = (MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(paramInt);
          if (paramViewHolder.thumbPath != null)
          {
            localBackupImageView.setImage(paramViewHolder.thumbPath, null, this.mContext.getResources().getDrawable(NUM));
            if (!PhotoPickerActivity.this.allowIndices) {
              break label458;
            }
            paramInt = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(paramViewHolder.imageId));
            ((PhotoPickerPhotoCell)localObject).setChecked(paramInt, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(paramViewHolder.imageId)), false);
            bool1 = PhotoViewer.isShowingImage(paramViewHolder.path);
            paramViewHolder = localBackupImageView.getImageReceiver();
            if (bool1) {
              break label744;
            }
            bool2 = true;
            label209:
            paramViewHolder.setVisible(bool2, true);
            paramViewHolder = ((PhotoPickerPhotoCell)localObject).checkBox;
            if ((!PhotoPickerActivity.this.singlePhoto) && (!bool1)) {
              break label750;
            }
          }
        }
        label458:
        label501:
        label530:
        label627:
        label727:
        label732:
        label744:
        label750:
        for (paramInt = 8;; paramInt = 0)
        {
          paramViewHolder.setVisibility(paramInt);
          break;
          if (paramViewHolder.path != null)
          {
            localBackupImageView.setOrientation(paramViewHolder.orientation, true);
            if (paramViewHolder.isVideo)
            {
              ((PhotoPickerPhotoCell)localObject).videoInfoContainer.setVisibility(0);
              paramInt = paramViewHolder.duration / 60;
              int i = paramViewHolder.duration;
              ((PhotoPickerPhotoCell)localObject).videoTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i - paramInt * 60) }));
              localBackupImageView.setImage("vthumb://" + paramViewHolder.imageId + ":" + paramViewHolder.path, null, this.mContext.getResources().getDrawable(NUM));
              break label135;
            }
            ((PhotoPickerPhotoCell)localObject).videoInfoContainer.setVisibility(4);
            localBackupImageView.setImage("thumb://" + paramViewHolder.imageId + ":" + paramViewHolder.path, null, this.mContext.getResources().getDrawable(NUM));
            break label135;
          }
          localBackupImageView.setImageResource(NUM);
          break label135;
          paramInt = -1;
          break label163;
          if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
          {
            paramViewHolder = (MediaController.SearchImage)PhotoPickerActivity.this.recentImages.get(paramInt);
            if (paramViewHolder.thumbPath == null) {
              break label627;
            }
            localBackupImageView.setImage(paramViewHolder.thumbPath, null, this.mContext.getResources().getDrawable(NUM));
            ((PhotoPickerPhotoCell)localObject).videoInfoContainer.setVisibility(4);
            if (!PhotoPickerActivity.this.allowIndices) {
              break label727;
            }
          }
          for (paramInt = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(paramViewHolder.id);; paramInt = -1)
          {
            ((PhotoPickerPhotoCell)localObject).setChecked(paramInt, PhotoPickerActivity.this.selectedPhotos.containsKey(paramViewHolder.id), false);
            if (paramViewHolder.document == null) {
              break label732;
            }
            bool1 = PhotoViewer.isShowingImage(FileLoader.getPathToAttach(paramViewHolder.document, true).getAbsolutePath());
            break;
            paramViewHolder = (MediaController.SearchImage)PhotoPickerActivity.this.searchResult.get(paramInt);
            break label501;
            if ((paramViewHolder.thumbUrl != null) && (paramViewHolder.thumbUrl.length() > 0))
            {
              localBackupImageView.setImage(paramViewHolder.thumbUrl, null, this.mContext.getResources().getDrawable(NUM));
              break label530;
            }
            if ((paramViewHolder.document != null) && (paramViewHolder.document.thumb != null))
            {
              localBackupImageView.setImage(paramViewHolder.document.thumb.location, null, this.mContext.getResources().getDrawable(NUM));
              break label530;
            }
            localBackupImageView.setImageResource(NUM);
            break label530;
          }
          bool1 = PhotoViewer.isShowingImage(paramViewHolder.imageUrl);
          break label195;
          bool2 = false;
          break label209;
        }
        localObject = paramViewHolder.itemView.getLayoutParams();
        if (localObject != null)
        {
          ((ViewGroup.LayoutParams)localObject).width = PhotoPickerActivity.this.itemWidth;
          ((ViewGroup.LayoutParams)localObject).height = PhotoPickerActivity.this.itemWidth;
          paramViewHolder.itemView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        localFrameLayout = new FrameLayout(this.mContext);
        paramViewGroup = localFrameLayout;
        RadialProgressView localRadialProgressView = new RadialProgressView(this.mContext);
        localRadialProgressView.setProgressColor(-1);
        localFrameLayout.addView(localRadialProgressView, LayoutHelper.createFrame(-1, -1.0F));
        return new RecyclerListView.Holder(paramViewGroup);
      }
      paramViewGroup = new PhotoPickerPhotoCell(this.mContext, true);
      paramViewGroup.checkFrame.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          boolean bool1 = false;
          boolean bool2 = false;
          int i = -1;
          int j = ((Integer)((View)paramAnonymousView.getParent()).getTag()).intValue();
          int k;
          if (PhotoPickerActivity.this.selectedAlbum != null)
          {
            localObject = (MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(j);
            if (!PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId))) {
              bool2 = true;
            }
            k = i;
            if (PhotoPickerActivity.this.allowIndices)
            {
              k = i;
              if (bool2) {
                k = PhotoPickerActivity.this.selectedPhotosOrder.size();
              }
            }
            ((PhotoPickerPhotoCell)paramAnonymousView.getParent()).setChecked(k, bool2, true);
            PhotoPickerActivity.this.addToSelectedPhotos(localObject, j);
            PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size(), true);
            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            return;
          }
          AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
          if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null)) {}
          for (Object localObject = (MediaController.SearchImage)PhotoPickerActivity.this.recentImages.get(((Integer)((View)paramAnonymousView.getParent()).getTag()).intValue());; localObject = (MediaController.SearchImage)PhotoPickerActivity.this.searchResult.get(((Integer)((View)paramAnonymousView.getParent()).getTag()).intValue()))
          {
            bool2 = bool1;
            if (!PhotoPickerActivity.this.selectedPhotos.containsKey(((MediaController.SearchImage)localObject).id)) {
              bool2 = true;
            }
            k = i;
            if (PhotoPickerActivity.this.allowIndices)
            {
              k = i;
              if (bool2) {
                k = PhotoPickerActivity.this.selectedPhotosOrder.size();
              }
            }
            ((PhotoPickerPhotoCell)paramAnonymousView.getParent()).setChecked(k, bool2, true);
            PhotoPickerActivity.this.addToSelectedPhotos(localObject, j);
            break;
          }
        }
      });
      FrameLayout localFrameLayout = paramViewGroup.checkFrame;
      if (PhotoPickerActivity.this.singlePhoto) {}
      for (paramInt = 8;; paramInt = 0)
      {
        localFrameLayout.setVisibility(paramInt);
        break;
      }
    }
  }
  
  public static abstract interface PhotoPickerActivityDelegate
  {
    public abstract void actionButtonPressed(boolean paramBoolean);
    
    public abstract void selectedPhotosChanged();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PhotoPickerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */