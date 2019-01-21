package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.FoundGif;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_foundGifs;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC.TL_messages_searchGifs;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_BotResults;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenterDelegate {
    private boolean allowCaption;
    private boolean allowIndices;
    private boolean bingSearchEndReached = true;
    private ChatActivity chatActivity;
    private PhotoPickerActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private FrameLayout frameLayout;
    private int giphyReqId;
    private boolean giphySearchEndReached = true;
    private AnimatorSet hintAnimation;
    private Runnable hintHideRunnable;
    private TextView hintTextView;
    private ImageView imageOrderToggleButton;
    private int imageReqId;
    private String initialSearchString;
    private int itemWidth = 100;
    private String lastSearchImageString;
    private String lastSearchString;
    private int lastSearchToken;
    private GridLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingRecent;
    private int maxSelectedPhotos;
    private int nextGiphySearchOffset;
    private String nextImagesSearchOffset;
    private PickerBottomLayout pickerBottomLayout;
    private PhotoViewerProvider provider = new EmptyPhotoViewerProvider() {
        public boolean scaleToFill() {
            return false;
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PhotoPickerPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell == null) {
                return null;
            }
            int[] coords = new int[2];
            cell.photoImage.getLocationInWindow(coords);
            PlaceProviderObject object = new PlaceProviderObject();
            object.viewX = coords[0];
            object.viewY = coords[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
            object.parentView = PhotoPickerActivity.this.listView;
            object.imageReceiver = cell.photoImage.getImageReceiver();
            object.thumb = object.imageReceiver.getBitmapSafe();
            object.scale = cell.photoImage.getScaleX();
            cell.showCheck(false);
            return object;
        }

        public void updatePhotoAtIndex(int index) {
            PhotoPickerPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell == null) {
                return;
            }
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                cell.photoImage.setOrientation(0, true);
                PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                if (photoEntry.thumbPath != null) {
                    cell.photoImage.setImage(photoEntry.thumbPath, null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
                    return;
                } else if (photoEntry.path != null) {
                    cell.photoImage.setOrientation(photoEntry.orientation, true);
                    if (photoEntry.isVideo) {
                        cell.photoImage.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
                        return;
                    } else {
                        cell.photoImage.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
                        return;
                    }
                } else {
                    cell.photoImage.setImageResource(R.drawable.nophotos);
                    return;
                }
            }
            ArrayList<SearchImage> array;
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                array = PhotoPickerActivity.this.recentImages;
            } else {
                array = PhotoPickerActivity.this.searchResult;
            }
            cell.setImage((SearchImage) array.get(index));
        }

        public boolean allowCaption() {
            return PhotoPickerActivity.this.allowCaption;
        }

        public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PhotoPickerPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell != null) {
                return cell.photoImage.getImageReceiver().getBitmapSafe();
            }
            return null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            int count = PhotoPickerActivity.this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (view.getTag() != null) {
                    PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) view;
                    int num = ((Integer) view.getTag()).intValue();
                    if (PhotoPickerActivity.this.selectedAlbum == null) {
                        ArrayList<SearchImage> array;
                        if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                            array = PhotoPickerActivity.this.recentImages;
                        } else {
                            array = PhotoPickerActivity.this.searchResult;
                        }
                        if (num < 0) {
                            continue;
                        } else if (num >= array.size()) {
                            continue;
                        }
                    } else if (num < 0) {
                        continue;
                    } else if (num >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                        continue;
                    }
                    if (num == index) {
                        cell.showCheck(true);
                        return;
                    }
                }
            }
        }

        public void willHidePhotoViewer() {
            int count = PhotoPickerActivity.this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (view instanceof PhotoPickerPhotoCell) {
                    ((PhotoPickerPhotoCell) view).showCheck(true);
                }
            }
        }

        public boolean isPhotoChecked(int index) {
            boolean z = true;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return index >= 0 && index < PhotoPickerActivity.this.selectedAlbum.photos.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(((PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index)).imageId));
            } else {
                ArrayList<SearchImage> array;
                if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                    array = PhotoPickerActivity.this.recentImages;
                } else {
                    array = PhotoPickerActivity.this.searchResult;
                }
                if (index < 0 || index >= array.size() || !PhotoPickerActivity.this.selectedPhotos.containsKey(((SearchImage) array.get(index)).id)) {
                    z = false;
                }
                return z;
            }
        }

        public int setPhotoUnchecked(Object object) {
            int position = -1;
            Object key = null;
            if (object instanceof PhotoEntry) {
                key = Integer.valueOf(((PhotoEntry) object).imageId);
            } else if (object instanceof SearchImage) {
                key = ((SearchImage) object).id;
            }
            if (key != null && PhotoPickerActivity.this.selectedPhotos.containsKey(key)) {
                PhotoPickerActivity.this.selectedPhotos.remove(key);
                position = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(key);
                if (position >= 0) {
                    PhotoPickerActivity.this.selectedPhotosOrder.remove(position);
                }
                if (PhotoPickerActivity.this.allowIndices) {
                    PhotoPickerActivity.this.updateCheckedPhotoIndices();
                }
            }
            return position;
        }

        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            int num;
            boolean add = true;
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                ArrayList<SearchImage> array;
                if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                    array = PhotoPickerActivity.this.recentImages;
                } else {
                    array = PhotoPickerActivity.this.searchResult;
                }
                if (index < 0 || index >= array.size()) {
                    return -1;
                }
                SearchImage photoEntry = (SearchImage) array.get(index);
                num = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                if (num == -1) {
                    num = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(photoEntry.id);
                } else {
                    add = false;
                }
            } else if (index < 0 || index >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                return -1;
            } else {
                PhotoEntry photoEntry2 = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                num = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry2, -1);
                if (num == -1) {
                    photoEntry2.editedInfo = videoEditedInfo;
                    num = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry2.imageId));
                } else {
                    add = false;
                    photoEntry2.editedInfo = null;
                }
            }
            int count = PhotoPickerActivity.this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (((Integer) view.getTag()).intValue() == index) {
                    int i;
                    PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) view;
                    if (PhotoPickerActivity.this.allowIndices) {
                        i = num;
                    } else {
                        i = -1;
                    }
                    photoPickerPhotoCell.setChecked(i, add, false);
                    PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size(), true);
                    PhotoPickerActivity.this.delegate.selectedPhotosChanged();
                    return num;
                }
            }
            PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size(), true);
            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            return num;
        }

        public boolean cancelButtonPressed() {
            PhotoPickerActivity.this.delegate.actionButtonPressed(true);
            PhotoPickerActivity.this.lambda$checkDiscard$70$PassportActivity();
            return true;
        }

        public int getSelectedCount() {
            return PhotoPickerActivity.this.selectedPhotos.size();
        }

        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
            if (PhotoPickerActivity.this.selectedPhotos.isEmpty()) {
                if (PhotoPickerActivity.this.selectedAlbum == null) {
                    ArrayList<SearchImage> array;
                    if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                        array = PhotoPickerActivity.this.recentImages;
                    } else {
                        array = PhotoPickerActivity.this.searchResult;
                    }
                    if (index >= 0 && index < array.size()) {
                        PhotoPickerActivity.this.addToSelectedPhotos(array.get(index), -1);
                    } else {
                        return;
                    }
                } else if (index >= 0 && index < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                    PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                    photoEntry.editedInfo = videoEditedInfo;
                    PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                } else {
                    return;
                }
            }
            PhotoPickerActivity.this.sendSelectedPhotos();
        }

        public void toggleGroupPhotosEnabled() {
            if (PhotoPickerActivity.this.imageOrderToggleButton != null) {
                PhotoPickerActivity.this.imageOrderToggleButton.setColorFilter(SharedConfig.groupPhotosEnabled ? new PorterDuffColorFilter(-10043398, Mode.MULTIPLY) : null);
            }
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return PhotoPickerActivity.this.selectedPhotosOrder;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return PhotoPickerActivity.this.selectedPhotos;
        }

        public boolean allowGroupPhotos() {
            return PhotoPickerActivity.this.imageOrderToggleButton != null;
        }
    };
    private ArrayList<SearchImage> recentImages;
    private ActionBarMenuItem searchItem;
    private ArrayList<SearchImage> searchResult = new ArrayList();
    private HashMap<String, SearchImage> searchResultKeys = new HashMap();
    private HashMap<String, SearchImage> searchResultUrls = new HashMap();
    private boolean searching;
    private boolean searchingUser;
    private int selectPhotoType;
    private AlbumEntry selectedAlbum;
    private HashMap<Object, Object> selectedPhotos;
    private ArrayList<Object> selectedPhotosOrder;
    private boolean sendPressed;
    private int type;

    public interface PhotoPickerActivityDelegate {
        void actionButtonPressed(boolean z);

        void selectedPhotosChanged();
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return true;
            }
            int position = holder.getAdapterPosition();
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                if (position < PhotoPickerActivity.this.recentImages.size()) {
                    return true;
                }
                return false;
            } else if (position >= PhotoPickerActivity.this.searchResult.size()) {
                return false;
            } else {
                return true;
            }
        }

        public int getItemCount() {
            int i = 0;
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                    return PhotoPickerActivity.this.recentImages.size();
                }
                int size;
                if (PhotoPickerActivity.this.type == 0) {
                    size = PhotoPickerActivity.this.searchResult.size();
                    if (!PhotoPickerActivity.this.bingSearchEndReached) {
                        i = 1;
                    }
                    return i + size;
                } else if (PhotoPickerActivity.this.type == 1) {
                    size = PhotoPickerActivity.this.searchResult.size();
                    if (!PhotoPickerActivity.this.giphySearchEndReached) {
                        i = 1;
                    }
                    return i + size;
                }
            }
            return PhotoPickerActivity.this.selectedAlbum.photos.size();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View cell = new PhotoPickerPhotoCell(this.mContext, true);
                    cell.checkFrame.setOnClickListener(new PhotoPickerActivity$ListAdapter$$Lambda$0(this));
                    cell.checkFrame.setVisibility(PhotoPickerActivity.this.selectPhotoType != 0 ? 8 : 0);
                    view = cell;
                    break;
                default:
                    View frameLayout = new FrameLayout(this.mContext);
                    view = frameLayout;
                    RadialProgressView progressBar = new RadialProgressView(this.mContext);
                    progressBar.setProgressColor(-1);
                    frameLayout.addView(progressBar, LayoutHelper.createFrame(-1, -1.0f));
                    break;
            }
            return new Holder(view);
        }

        final /* synthetic */ void lambda$onCreateViewHolder$0$PhotoPickerActivity$ListAdapter(View v) {
            boolean added = false;
            int num = -1;
            int index = ((Integer) ((View) v.getParent()).getTag()).intValue();
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                if (!PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId))) {
                    added = true;
                }
                if (!added || PhotoPickerActivity.this.maxSelectedPhotos <= 0 || PhotoPickerActivity.this.selectedPhotos.size() < PhotoPickerActivity.this.maxSelectedPhotos) {
                    if (PhotoPickerActivity.this.allowIndices && added) {
                        num = PhotoPickerActivity.this.selectedPhotosOrder.size();
                    }
                    ((PhotoPickerPhotoCell) v.getParent()).setChecked(num, added, true);
                    PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, index);
                } else {
                    return;
                }
            }
            SearchImage photoEntry2;
            AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                photoEntry2 = (SearchImage) PhotoPickerActivity.this.recentImages.get(((Integer) ((View) v.getParent()).getTag()).intValue());
            } else {
                photoEntry2 = (SearchImage) PhotoPickerActivity.this.searchResult.get(((Integer) ((View) v.getParent()).getTag()).intValue());
            }
            if (!PhotoPickerActivity.this.selectedPhotos.containsKey(photoEntry2.id)) {
                added = true;
            }
            if (!added || PhotoPickerActivity.this.maxSelectedPhotos <= 0 || PhotoPickerActivity.this.selectedPhotos.size() < PhotoPickerActivity.this.maxSelectedPhotos) {
                if (PhotoPickerActivity.this.allowIndices && added) {
                    num = PhotoPickerActivity.this.selectedPhotosOrder.size();
                }
                ((PhotoPickerPhotoCell) v.getParent()).setChecked(num, added, true);
                PhotoPickerActivity.this.addToSelectedPhotos(photoEntry2, index);
            } else {
                return;
            }
            PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size(), true);
            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    boolean showing;
                    int i;
                    PhotoPickerPhotoCell cell = holder.itemView;
                    cell.itemWidth = PhotoPickerActivity.this.itemWidth;
                    BackupImageView imageView = cell.photoImage;
                    imageView.setTag(Integer.valueOf(position));
                    cell.setTag(Integer.valueOf(position));
                    imageView.setOrientation(0, true);
                    if (PhotoPickerActivity.this.selectedAlbum != null) {
                        PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(position);
                        if (photoEntry.thumbPath != null) {
                            imageView.setImage(photoEntry.thumbPath, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                        } else if (photoEntry.path != null) {
                            imageView.setOrientation(photoEntry.orientation, true);
                            if (photoEntry.isVideo) {
                                cell.videoInfoContainer.setVisibility(0);
                                int seconds = photoEntry.duration - ((photoEntry.duration / 60) * 60);
                                cell.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)}));
                                imageView.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                            } else {
                                cell.videoInfoContainer.setVisibility(4);
                                imageView.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                            }
                        } else {
                            imageView.setImageResource(R.drawable.nophotos);
                        }
                        cell.setChecked(PhotoPickerActivity.this.allowIndices ? PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)) : -1, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                        showing = PhotoViewer.isShowingImage(photoEntry.path);
                    } else {
                        SearchImage photoEntry2;
                        if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                            photoEntry2 = (SearchImage) PhotoPickerActivity.this.recentImages.get(position);
                        } else {
                            photoEntry2 = (SearchImage) PhotoPickerActivity.this.searchResult.get(position);
                        }
                        cell.setImage(photoEntry2);
                        cell.videoInfoContainer.setVisibility(4);
                        cell.setChecked(PhotoPickerActivity.this.allowIndices ? PhotoPickerActivity.this.selectedPhotosOrder.indexOf(photoEntry2.id) : -1, PhotoPickerActivity.this.selectedPhotos.containsKey(photoEntry2.id), false);
                        showing = PhotoViewer.isShowingImage(photoEntry2.getPathToAttach());
                    }
                    imageView.getImageReceiver().setVisible(!showing, true);
                    CheckBox checkBox = cell.checkBox;
                    if (PhotoPickerActivity.this.selectPhotoType != 0 || showing) {
                        i = 8;
                    } else {
                        i = 0;
                    }
                    checkBox.setVisibility(i);
                    return;
                case 1:
                    LayoutParams params = holder.itemView.getLayoutParams();
                    if (params != null) {
                        params.width = PhotoPickerActivity.this.itemWidth;
                        params.height = PhotoPickerActivity.this.itemWidth;
                        holder.itemView.setLayoutParams(params);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (PhotoPickerActivity.this.selectedAlbum != null || ((PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null && i < PhotoPickerActivity.this.recentImages.size()) || i < PhotoPickerActivity.this.searchResult.size())) {
                return 0;
            }
            return 1;
        }
    }

    public PhotoPickerActivity(int type, AlbumEntry selectedAlbum, HashMap<Object, Object> selectedPhotos, ArrayList<Object> selectedPhotosOrder, ArrayList<SearchImage> recentImages, int selectPhotoType, boolean allowCaption, ChatActivity chatActivity) {
        this.selectedAlbum = selectedAlbum;
        this.selectedPhotos = selectedPhotos;
        this.selectedPhotosOrder = selectedPhotosOrder;
        this.type = type;
        this.recentImages = recentImages;
        this.selectPhotoType = selectPhotoType;
        this.chatActivity = chatActivity;
        this.allowCaption = allowCaption;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoad);
        if (this.selectedAlbum == null && this.recentImages.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).loadWebRecent(this.type);
            this.loadingRecent = true;
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentImagesDidLoad);
        if (this.giphyReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.giphyReqId, true);
            this.giphyReqId = 0;
        }
        if (this.imageReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
            this.imageReqId = 0;
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        boolean z;
        this.actionBar.setBackgroundColor(-13421773);
        this.actionBar.setItemsBackgroundColor(-12763843, false);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (this.selectedAlbum != null) {
            this.actionBar.setTitle(this.selectedAlbum.bucketName);
        } else if (this.type == 0) {
            this.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
        } else if (this.type == 1) {
            this.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PhotoPickerActivity.this.lambda$checkDiscard$70$PassportActivity();
                }
            }
        });
        if (this.selectedAlbum == null) {
            this.searchItem = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                }

                public boolean canCollapseSearch() {
                    PhotoPickerActivity.this.lambda$checkDiscard$70$PassportActivity();
                    return false;
                }

                public void onTextChanged(EditText editText) {
                    if (editText.getText().length() == 0) {
                        PhotoPickerActivity.this.searchResult.clear();
                        PhotoPickerActivity.this.searchResultKeys.clear();
                        PhotoPickerActivity.this.lastSearchString = null;
                        PhotoPickerActivity.this.bingSearchEndReached = true;
                        PhotoPickerActivity.this.giphySearchEndReached = true;
                        PhotoPickerActivity.this.searching = false;
                        if (PhotoPickerActivity.this.imageReqId != 0) {
                            ConnectionsManager.getInstance(PhotoPickerActivity.this.currentAccount).cancelRequest(PhotoPickerActivity.this.imageReqId, true);
                            PhotoPickerActivity.this.imageReqId = 0;
                        }
                        if (PhotoPickerActivity.this.giphyReqId != 0) {
                            ConnectionsManager.getInstance(PhotoPickerActivity.this.currentAccount).cancelRequest(PhotoPickerActivity.this.giphyReqId, true);
                            PhotoPickerActivity.this.giphyReqId = 0;
                        }
                        PhotoPickerActivity.this.emptyView.setText("");
                        PhotoPickerActivity.this.updateSearchInterface();
                    }
                }

                public void onSearchPressed(EditText editText) {
                    PhotoPickerActivity.this.processSearch(editText);
                }
            });
        }
        if (this.selectedAlbum == null) {
            if (this.type == 0) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
            } else if (this.type == 1) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
        }
        this.fragmentView = new FrameLayout(context);
        this.frameLayout = (FrameLayout) this.fragmentView;
        this.frameLayout.setBackgroundColor(-16777216);
        this.listView = new RecyclerListView(context);
        this.listView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager anonymousClass4 = new GridLayoutManager(context, 4) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass4;
        recyclerListView.setLayoutManager(anonymousClass4);
        this.listView.addItemDecoration(new ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                int dp;
                int i = 0;
                super.getItemOffsets(outRect, view, parent, state);
                int total = state.getItemCount();
                int position = parent.getChildAdapterPosition(view);
                int spanCount = PhotoPickerActivity.this.layoutManager.getSpanCount();
                int rowsCOunt = (int) Math.ceil((double) (((float) total) / ((float) spanCount)));
                int row = position / spanCount;
                if (position % spanCount != spanCount - 1) {
                    dp = AndroidUtilities.dp(4.0f);
                } else {
                    dp = 0;
                }
                outRect.right = dp;
                if (row != rowsCOunt - 1) {
                    i = AndroidUtilities.dp(4.0f);
                }
                outRect.bottom = i;
            }
        });
        this.frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.selectPhotoType != 0 ? 0.0f : 48.0f));
        recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setGlowColor(-13421773);
        this.listView.setOnItemClickListener(new PhotoPickerActivity$$Lambda$0(this));
        if (this.selectedAlbum == null) {
            this.listView.setOnItemLongClickListener(new PhotoPickerActivity$$Lambda$1(this));
        }
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setProgressBarColor(-1);
        this.emptyView.setShowAtCenter(true);
        if (this.selectedAlbum != null) {
            this.emptyView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
        } else {
            this.emptyView.setText("");
        }
        this.frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.selectPhotoType != 0 ? 0.0f : 48.0f));
        if (this.selectedAlbum == null) {
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1) {
                        AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int firstVisibleItem = PhotoPickerActivity.this.layoutManager.findFirstVisibleItemPosition();
                    int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(PhotoPickerActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                    if (visibleItemCount > 0) {
                        int totalItemCount = PhotoPickerActivity.this.layoutManager.getItemCount();
                        if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount > totalItemCount - 2 && !PhotoPickerActivity.this.searching) {
                            if (PhotoPickerActivity.this.type == 0 && !PhotoPickerActivity.this.bingSearchEndReached) {
                                PhotoPickerActivity.this.searchImages(PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.nextImagesSearchOffset, true);
                            } else if (PhotoPickerActivity.this.type == 1 && !PhotoPickerActivity.this.giphySearchEndReached) {
                                PhotoPickerActivity.this.searchGiphyImages(PhotoPickerActivity.this.searchItem.getSearchField().getText().toString(), PhotoPickerActivity.this.nextGiphySearchOffset);
                            }
                        }
                    }
                }
            });
            updateSearchInterface();
        }
        this.pickerBottomLayout = new PickerBottomLayout(context);
        this.frameLayout.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.pickerBottomLayout.cancelButton.setOnClickListener(new PhotoPickerActivity$$Lambda$2(this));
        this.pickerBottomLayout.doneButton.setOnClickListener(new PhotoPickerActivity$$Lambda$3(this));
        if (this.selectPhotoType != 0) {
            this.pickerBottomLayout.setVisibility(8);
        } else if ((this.selectedAlbum != null || this.type == 0) && this.chatActivity != null && this.chatActivity.allowGroupPhotos()) {
            this.imageOrderToggleButton = new ImageView(context);
            this.imageOrderToggleButton.setScaleType(ScaleType.CENTER);
            this.imageOrderToggleButton.setImageResource(R.drawable.photos_group);
            this.pickerBottomLayout.addView(this.imageOrderToggleButton, LayoutHelper.createFrame(48, -1, 17));
            this.imageOrderToggleButton.setOnClickListener(new PhotoPickerActivity$$Lambda$4(this));
            this.imageOrderToggleButton.setColorFilter(SharedConfig.groupPhotosEnabled ? new PorterDuffColorFilter(-10043398, Mode.MULTIPLY) : null);
        }
        if ((this.selectedAlbum != null || this.type == 0) && this.maxSelectedPhotos <= 0) {
            z = true;
        } else {
            z = false;
        }
        this.allowIndices = z;
        this.listView.setEmptyView(this.emptyView);
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size(), true);
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$PhotoPickerActivity(View view, int position) {
        ArrayList<Object> arrayList;
        if (this.selectedAlbum != null) {
            arrayList = this.selectedAlbum.photos;
        } else if (this.searchResult.isEmpty() && this.lastSearchString == null) {
            arrayList = this.recentImages;
        } else {
            arrayList = this.searchResult;
        }
        if (position >= 0 && position < arrayList.size()) {
            int type;
            if (this.searchItem != null) {
                AndroidUtilities.hideKeyboard(this.searchItem.getSearchField());
            }
            if (this.selectPhotoType == 1) {
                type = 1;
            } else if (this.selectPhotoType == 2) {
                type = 3;
            } else if (this.chatActivity == null) {
                type = 4;
            } else {
                type = 0;
            }
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos);
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, type, this.provider, this.chatActivity);
        }
    }

    final /* synthetic */ boolean lambda$createView$2$PhotoPickerActivity(View view, int position) {
        if (!this.searchResult.isEmpty() || this.lastSearchString != null) {
            return false;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
        builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new PhotoPickerActivity$$Lambda$12(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
        return true;
    }

    final /* synthetic */ void lambda$null$1$PhotoPickerActivity(DialogInterface dialogInterface, int i) {
        this.recentImages.clear();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        MessagesStorage.getInstance(this.currentAccount).clearWebRecent(this.type);
    }

    final /* synthetic */ void lambda$createView$3$PhotoPickerActivity(View view) {
        this.delegate.actionButtonPressed(true);
        lambda$checkDiscard$70$PassportActivity();
    }

    final /* synthetic */ void lambda$createView$5$PhotoPickerActivity(View v) {
        SharedConfig.toggleGroupPhotosEnabled();
        this.imageOrderToggleButton.setColorFilter(SharedConfig.groupPhotosEnabled ? new PorterDuffColorFilter(-10043398, Mode.MULTIPLY) : null);
        showHint(false, SharedConfig.groupPhotosEnabled);
        updateCheckedPhotoIndices();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.searchItem != null) {
            this.searchItem.openSearch(true);
            if (!TextUtils.isEmpty(this.initialSearchString)) {
                this.searchItem.setSearchFieldText(this.initialSearchString, false);
                this.initialSearchString = null;
                processSearch(this.searchItem.getSearchField());
            }
            getParentActivity().getWindow().setSoftInputMode(32);
        }
        fixLayout();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.closeChats) {
            lambda$null$9$ProfileActivity();
        } else if (id == NotificationCenter.recentImagesDidLoad && this.selectedAlbum == null && this.type == ((Integer) args[0]).intValue()) {
            this.recentImages = (ArrayList) args[1];
            this.loadingRecent = false;
            updateSearchInterface();
        }
    }

    private void hideHint() {
        this.hintAnimation = new AnimatorSet();
        AnimatorSet animatorSet = this.hintAnimation;
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        this.hintAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PhotoPickerActivity.this.hintAnimation)) {
                    PhotoPickerActivity.this.hintAnimation = null;
                    PhotoPickerActivity.this.hintHideRunnable = null;
                    if (PhotoPickerActivity.this.hintTextView != null) {
                        PhotoPickerActivity.this.hintTextView.setVisibility(8);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (animation.equals(PhotoPickerActivity.this.hintAnimation)) {
                    PhotoPickerActivity.this.hintHideRunnable = null;
                    PhotoPickerActivity.this.hintHideRunnable = null;
                }
            }
        });
        this.hintAnimation.setDuration(300);
        this.hintAnimation.start();
    }

    public void setInitialSearchString(String text) {
        this.initialSearchString = text;
    }

    private void processSearch(EditText editText) {
        if (editText.getText().toString().length() != 0) {
            this.searchResult.clear();
            this.searchResultKeys.clear();
            this.bingSearchEndReached = true;
            this.giphySearchEndReached = true;
            if (this.type == 0) {
                searchImages(editText.getText().toString(), "", true);
            } else if (this.type == 1) {
                this.nextGiphySearchOffset = 0;
                searchGiphyImages(editText.getText().toString(), 0);
            }
            this.lastSearchString = editText.getText().toString();
            if (this.lastSearchString.length() == 0) {
                this.lastSearchString = null;
                this.emptyView.setText("");
            } else {
                this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
            }
            updateSearchInterface();
        }
    }

    public void setMaxSelectedPhotos(int value) {
        this.maxSelectedPhotos = value;
    }

    private void showHint(boolean hide, boolean enabled) {
        if (getParentActivity() != null && this.fragmentView != null) {
            if (!hide || this.hintTextView != null) {
                if (this.hintTextView == null) {
                    this.hintTextView = new TextView(getParentActivity());
                    this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("chat_gifSaveHintBackground")));
                    this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
                    this.hintTextView.setTextSize(1, 14.0f);
                    this.hintTextView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
                    this.hintTextView.setGravity(16);
                    this.hintTextView.setAlpha(0.0f);
                    this.frameLayout.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 5.0f, 0.0f, 5.0f, 51.0f));
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
                this.hintTextView.setText(enabled ? LocaleController.getString("GroupPhotosHelp", R.string.GroupPhotosHelp) : LocaleController.getString("SinglePhotosHelp", R.string.SinglePhotosHelp));
                if (this.hintHideRunnable != null) {
                    if (this.hintAnimation != null) {
                        this.hintAnimation.cancel();
                        this.hintAnimation = null;
                    } else {
                        AndroidUtilities.cancelRunOnUIThread(this.hintHideRunnable);
                        Runnable photoPickerActivity$$Lambda$5 = new PhotoPickerActivity$$Lambda$5(this);
                        this.hintHideRunnable = photoPickerActivity$$Lambda$5;
                        AndroidUtilities.runOnUIThread(photoPickerActivity$$Lambda$5, 2000);
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
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(PhotoPickerActivity.this.hintAnimation)) {
                            PhotoPickerActivity.this.hintAnimation = null;
                            AndroidUtilities.runOnUIThread(PhotoPickerActivity.this.hintHideRunnable = new PhotoPickerActivity$8$$Lambda$0(this), 2000);
                        }
                    }

                    final /* synthetic */ void lambda$onAnimationEnd$0$PhotoPickerActivity$8() {
                        PhotoPickerActivity.this.hideHint();
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (animation.equals(PhotoPickerActivity.this.hintAnimation)) {
                            PhotoPickerActivity.this.hintAnimation = null;
                        }
                    }
                });
                this.hintAnimation.setDuration(300);
                this.hintAnimation.start();
            }
        }
    }

    private void updateCheckedPhotoIndices() {
        if (this.allowIndices) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.listView.getChildAt(a);
                if (view instanceof PhotoPickerPhotoCell) {
                    PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) view;
                    Integer index = (Integer) cell.getTag();
                    if (this.selectedAlbum != null) {
                        cell.setNum(this.allowIndices ? this.selectedPhotosOrder.indexOf(Integer.valueOf(((PhotoEntry) this.selectedAlbum.photos.get(index.intValue())).imageId)) : -1);
                    } else {
                        SearchImage photoEntry;
                        int indexOf;
                        if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                            photoEntry = (SearchImage) this.recentImages.get(index.intValue());
                        } else {
                            photoEntry = (SearchImage) this.searchResult.get(index.intValue());
                        }
                        if (this.allowIndices) {
                            indexOf = this.selectedPhotosOrder.indexOf(photoEntry.id);
                        } else {
                            indexOf = -1;
                        }
                        cell.setNum(indexOf);
                    }
                }
            }
        }
    }

    private PhotoPickerPhotoCell getCellForIndex(int index) {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (view instanceof PhotoPickerPhotoCell) {
                PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) view;
                int num = ((Integer) cell.photoImage.getTag()).intValue();
                if (this.selectedAlbum == null) {
                    ArrayList<SearchImage> array;
                    if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                        array = this.recentImages;
                    } else {
                        array = this.searchResult;
                    }
                    if (num < 0) {
                        continue;
                    } else if (num >= array.size()) {
                        continue;
                    }
                } else if (num < 0) {
                    continue;
                } else if (num >= this.selectedAlbum.photos.size()) {
                    continue;
                }
                if (num == index) {
                    return cell;
                }
            }
        }
        return null;
    }

    private int addToSelectedPhotos(Object object, int index) {
        int position = -1;
        Object key = null;
        if (object instanceof PhotoEntry) {
            key = Integer.valueOf(((PhotoEntry) object).imageId);
        } else if (object instanceof SearchImage) {
            key = ((SearchImage) object).id;
        }
        if (key != null) {
            if (this.selectedPhotos.containsKey(key)) {
                this.selectedPhotos.remove(key);
                position = this.selectedPhotosOrder.indexOf(key);
                if (position >= 0) {
                    this.selectedPhotosOrder.remove(position);
                }
                if (this.allowIndices) {
                    updateCheckedPhotoIndices();
                }
                if (index >= 0) {
                    if (object instanceof PhotoEntry) {
                        ((PhotoEntry) object).reset();
                    } else if (object instanceof SearchImage) {
                        ((SearchImage) object).reset();
                    }
                    this.provider.updatePhotoAtIndex(index);
                }
            } else {
                this.selectedPhotos.put(key, object);
                this.selectedPhotosOrder.add(key);
            }
        }
        return position;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.searchItem != null) {
            AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
        }
    }

    private void updateSearchInterface() {
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if ((this.searching && this.searchResult.isEmpty()) || (this.loadingRecent && this.lastSearchString == null)) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
    }

    private void searchGiphyImages(String query, int offset) {
        if (this.searching) {
            this.searching = false;
            if (this.giphyReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.giphyReqId, true);
                this.giphyReqId = 0;
            }
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }
        this.searching = true;
        TL_messages_searchGifs req = new TL_messages_searchGifs();
        req.q = query;
        req.offset = offset;
        int token = this.lastSearchToken + 1;
        this.lastSearchToken = token;
        this.giphyReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PhotoPickerActivity$$Lambda$6(this, token, query));
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.giphyReqId, this.classGuid);
    }

    final /* synthetic */ void lambda$searchGiphyImages$7$PhotoPickerActivity(int token, String query, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PhotoPickerActivity$$Lambda$11(this, token, response, query));
    }

    final /* synthetic */ void lambda$null$6$PhotoPickerActivity(int token, TLObject response, String query) {
        if (token == this.lastSearchToken) {
            int addedCount = 0;
            if (response != null) {
                boolean added = false;
                TL_messages_foundGifs res = (TL_messages_foundGifs) response;
                this.nextGiphySearchOffset = res.next_offset;
                for (int a = 0; a < res.results.size(); a++) {
                    FoundGif gif = (FoundGif) res.results.get(a);
                    if (!this.searchResultKeys.containsKey(gif.url)) {
                        added = true;
                        SearchImage bingImage = new SearchImage();
                        bingImage.id = gif.url;
                        if (gif.document != null) {
                            for (int b = 0; b < gif.document.attributes.size(); b++) {
                                DocumentAttribute attribute = (DocumentAttribute) gif.document.attributes.get(b);
                                if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                    bingImage.width = attribute.w;
                                    bingImage.height = attribute.h;
                                    break;
                                }
                            }
                        } else {
                            bingImage.width = gif.w;
                            bingImage.height = gif.h;
                        }
                        bingImage.size = 0;
                        bingImage.imageUrl = gif.content_url;
                        bingImage.thumbUrl = gif.thumb_url;
                        bingImage.localUrl = gif.url + "|" + query;
                        bingImage.document = gif.document;
                        if (!(gif.photo == null || gif.document == null)) {
                            PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(gif.photo.sizes, this.itemWidth, true);
                            if (size != null) {
                                gif.document.thumbs.add(size);
                                Document document = gif.document;
                                document.flags |= 1;
                            }
                        }
                        bingImage.type = 1;
                        this.searchResult.add(bingImage);
                        addedCount++;
                        this.searchResultKeys.put(bingImage.id, bingImage);
                    }
                }
                this.giphySearchEndReached = !added;
            }
            this.searching = false;
            if (addedCount != 0) {
                this.listAdapter.notifyItemRangeInserted(this.searchResult.size(), addedCount);
            } else if (this.giphySearchEndReached) {
                this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
            }
            if ((this.searching && this.searchResult.isEmpty()) || (this.loadingRecent && this.lastSearchString == null)) {
                this.emptyView.showProgress();
            } else {
                this.emptyView.showTextView();
            }
        }
    }

    private void searchBotUser() {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
            req.username = MessagesController.getInstance(this.currentAccount).imageSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PhotoPickerActivity$$Lambda$7(this));
        }
    }

    final /* synthetic */ void lambda$searchBotUser$9$PhotoPickerActivity(TLObject response, TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new PhotoPickerActivity$$Lambda$10(this, response));
        }
    }

    final /* synthetic */ void lambda$null$8$PhotoPickerActivity(TLObject response) {
        TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        String str = this.lastSearchImageString;
        this.lastSearchImageString = null;
        searchImages(str, "", false);
    }

    private void searchImages(String query, String offset, boolean searchUser) {
        if (this.searching) {
            this.searching = false;
            if (this.giphyReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.giphyReqId, true);
                this.giphyReqId = 0;
            }
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }
        this.lastSearchImageString = query;
        this.searching = true;
        TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat(MessagesController.getInstance(this.currentAccount).imageSearchBot);
        if (object instanceof User) {
            User user = (User) object;
            TL_messages_getInlineBotResults req = new TL_messages_getInlineBotResults();
            if (query == null) {
                query = "";
            }
            req.query = query;
            req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
            req.offset = offset;
            if (this.chatActivity != null) {
                long dialogId = this.chatActivity.getDialogId();
                int lower_id = (int) dialogId;
                int high_id = (int) (dialogId >> 32);
                if (lower_id != 0) {
                    req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id);
                } else {
                    req.peer = new TL_inputPeerEmpty();
                }
            } else {
                req.peer = new TL_inputPeerEmpty();
            }
            int token = this.lastSearchToken + 1;
            this.lastSearchToken = token;
            this.imageReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PhotoPickerActivity$$Lambda$8(this, token));
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.imageReqId, this.classGuid);
        } else if (searchUser) {
            searchBotUser();
        }
    }

    final /* synthetic */ void lambda$searchImages$11$PhotoPickerActivity(int token, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PhotoPickerActivity$$Lambda$9(this, token, response));
    }

    final /* synthetic */ void lambda$null$10$PhotoPickerActivity(int token, TLObject response) {
        if (token == this.lastSearchToken) {
            int addedCount = 0;
            if (response != null) {
                messages_BotResults res = (messages_BotResults) response;
                this.nextImagesSearchOffset = res.next_offset;
                boolean added = false;
                int count = res.results.size();
                for (int a = 0; a < count; a++) {
                    BotInlineResult result = (BotInlineResult) res.results.get(a);
                    if ("photo".equals(result.type) && !this.searchResultKeys.containsKey(result.id)) {
                        added = true;
                        SearchImage bingImage = new SearchImage();
                        if (result.photo != null) {
                            PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, AndroidUtilities.getPhotoSize());
                            PhotoSize size2 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, 320);
                            if (size != null) {
                                bingImage.width = size.w;
                                bingImage.height = size.h;
                                bingImage.photoSize = size;
                                bingImage.photo = result.photo;
                                bingImage.size = size.size;
                                bingImage.thumbPhotoSize = size2;
                            }
                        } else if (result.content != null) {
                            for (int b = 0; b < result.content.attributes.size(); b++) {
                                DocumentAttribute attribute = (DocumentAttribute) result.content.attributes.get(b);
                                if (attribute instanceof TL_documentAttributeImageSize) {
                                    bingImage.width = attribute.w;
                                    bingImage.height = attribute.h;
                                    break;
                                }
                            }
                            if (result.thumb != null) {
                                bingImage.thumbUrl = result.thumb.url;
                            } else {
                                bingImage.thumbUrl = null;
                            }
                            bingImage.imageUrl = result.content.url;
                            bingImage.size = result.content.size;
                        }
                        bingImage.id = result.id;
                        bingImage.type = 0;
                        bingImage.localUrl = "";
                        this.searchResult.add(bingImage);
                        this.searchResultKeys.put(bingImage.id, bingImage);
                        addedCount++;
                        added = true;
                    }
                }
                boolean z = !added || this.nextImagesSearchOffset == null;
                this.bingSearchEndReached = z;
            }
            this.searching = false;
            if (addedCount != 0) {
                this.listAdapter.notifyItemRangeInserted(this.searchResult.size(), addedCount);
            } else if (this.bingSearchEndReached) {
                this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
            }
            if ((this.searching && this.searchResult.isEmpty()) || (this.loadingRecent && this.lastSearchString == null)) {
                this.emptyView.showProgress();
            } else {
                this.emptyView.showTextView();
            }
        }
    }

    public void setDelegate(PhotoPickerActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private void sendSelectedPhotos() {
        if (!this.selectedPhotos.isEmpty() && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            this.delegate.actionButtonPressed(false);
            if (this.selectPhotoType != 2) {
                lambda$checkDiscard$70$PassportActivity();
            }
        }
    }

    private void fixLayout() {
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    PhotoPickerActivity.this.fixLayoutInternal();
                    if (PhotoPickerActivity.this.listView != null) {
                        PhotoPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private void fixLayoutInternal() {
        if (getParentActivity() != null) {
            int columnsCount;
            int position = this.layoutManager.findFirstVisibleItemPosition();
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            if (AndroidUtilities.isTablet()) {
                columnsCount = 3;
            } else if (rotation == 3 || rotation == 1) {
                columnsCount = 5;
            } else {
                columnsCount = 3;
            }
            this.layoutManager.setSpanCount(columnsCount);
            if (AndroidUtilities.isTablet()) {
                this.itemWidth = (AndroidUtilities.dp(490.0f) - ((columnsCount + 1) * AndroidUtilities.dp(4.0f))) / columnsCount;
            } else {
                this.itemWidth = (AndroidUtilities.displaySize.x - ((columnsCount + 1) * AndroidUtilities.dp(4.0f))) / columnsCount;
            }
            this.listAdapter.notifyDataSetChanged();
            this.layoutManager.scrollToPosition(position);
            if (this.selectedAlbum == null) {
                this.emptyView.setPadding(0, 0, 0, (int) (((float) (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight())) * 0.4f));
            }
        }
    }
}
