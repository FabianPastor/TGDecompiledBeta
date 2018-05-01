package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
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
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
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
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
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
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.FoundGif;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_foundGifs;
import org.telegram.tgnet.TLRPC.TL_messages_searchGifs;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenterDelegate {
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
    private PhotoViewerProvider provider = new C23461();
    private ArrayList<SearchImage> recentImages;
    private ActionBarMenuItem searchItem;
    private ArrayList<SearchImage> searchResult = new ArrayList();
    private HashMap<String, SearchImage> searchResultKeys = new HashMap();
    private HashMap<String, SearchImage> searchResultUrls = new HashMap();
    private boolean searching;
    private AlbumEntry selectedAlbum;
    private HashMap<Object, Object> selectedPhotos;
    private ArrayList<Object> selectedPhotosOrder;
    private boolean sendPressed;
    private boolean singlePhoto;
    private int type;

    /* renamed from: org.telegram.ui.PhotoPickerActivity$9 */
    class C15879 implements OnClickListener {
        C15879() {
        }

        public void onClick(View view) {
            PhotoPickerActivity.this.delegate.actionButtonPressed(true);
            PhotoPickerActivity.this.finishFragment();
        }
    }

    public interface PhotoPickerActivityDelegate {
        void actionButtonPressed(boolean z);

        void selectedPhotosChanged();
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$2 */
    class C22272 extends ActionBarMenuOnItemClick {
        C22272() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PhotoPickerActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$3 */
    class C22283 extends ActionBarMenuItemSearchListener {
        public void onSearchExpand() {
        }

        C22283() {
        }

        public boolean canCollapseSearch() {
            PhotoPickerActivity.this.finishFragment();
            return false;
        }

        public void onTextChanged(EditText editText) {
            if (editText.getText().length() == null) {
                PhotoPickerActivity.this.searchResult.clear();
                PhotoPickerActivity.this.searchResultKeys.clear();
                PhotoPickerActivity.this.lastSearchString = null;
                PhotoPickerActivity.this.bingSearchEndReached = true;
                PhotoPickerActivity.this.giphySearchEndReached = true;
                PhotoPickerActivity.this.searching = false;
                if (PhotoPickerActivity.this.currentBingTask != null) {
                    PhotoPickerActivity.this.currentBingTask.cancel(true);
                    PhotoPickerActivity.this.currentBingTask = null;
                }
                if (PhotoPickerActivity.this.giphyReqId != null) {
                    ConnectionsManager.getInstance(PhotoPickerActivity.this.currentAccount).cancelRequest(PhotoPickerActivity.this.giphyReqId, true);
                    PhotoPickerActivity.this.giphyReqId = 0;
                }
                if (PhotoPickerActivity.this.type == null) {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", C0446R.string.NoRecentPhotos));
                } else if (PhotoPickerActivity.this.type == 1) {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", C0446R.string.NoRecentGIFs));
                }
                PhotoPickerActivity.this.updateSearchInterface();
            }
        }

        public void onSearchPressed(EditText editText) {
            if (editText.getText().toString().length() != 0) {
                PhotoPickerActivity.this.searchResult.clear();
                PhotoPickerActivity.this.searchResultKeys.clear();
                PhotoPickerActivity.this.bingSearchEndReached = true;
                PhotoPickerActivity.this.giphySearchEndReached = true;
                if (PhotoPickerActivity.this.type == 0) {
                    PhotoPickerActivity.this.searchBingImages(editText.getText().toString(), 0, 53);
                } else if (PhotoPickerActivity.this.type == 1) {
                    PhotoPickerActivity.this.nextGiphySearchOffset = 0;
                    PhotoPickerActivity.this.searchGiphyImages(editText.getText().toString(), 0);
                }
                PhotoPickerActivity.this.lastSearchString = editText.getText().toString();
                if (PhotoPickerActivity.this.lastSearchString.length() == null) {
                    PhotoPickerActivity.this.lastSearchString = null;
                    if (PhotoPickerActivity.this.type == null) {
                        PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", C0446R.string.NoRecentPhotos));
                    } else if (PhotoPickerActivity.this.type == 1) {
                        PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", C0446R.string.NoRecentGIFs));
                    }
                } else {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
                }
                PhotoPickerActivity.this.updateSearchInterface();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$5 */
    class C22295 extends ItemDecoration {
        C22295() {
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
            super.getItemOffsets(rect, view, recyclerView, state);
            state = state.getItemCount();
            view = recyclerView.getChildAdapterPosition(view);
            recyclerView = PhotoPickerActivity.this.layoutManager.getSpanCount();
            state = (int) Math.ceil((double) (((float) state) / ((float) recyclerView)));
            int i = view / recyclerView;
            int i2 = 0;
            rect.right = view % recyclerView != recyclerView + -1 ? AndroidUtilities.dp(4.0f) : null;
            if (i != state - 1) {
                i2 = AndroidUtilities.dp(4.0f);
            }
            rect.bottom = i2;
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$6 */
    class C22306 implements OnItemClickListener {
        C22306() {
        }

        public void onItemClick(View view, int i) {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                view = PhotoPickerActivity.this.selectedAlbum.photos;
            } else if (PhotoPickerActivity.this.searchResult.isEmpty() == null || PhotoPickerActivity.this.lastSearchString != null) {
                view = PhotoPickerActivity.this.searchResult;
            } else {
                view = PhotoPickerActivity.this.recentImages;
            }
            View view2 = view;
            if (i >= 0) {
                if (i < view2.size()) {
                    if (PhotoPickerActivity.this.searchItem != null) {
                        AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.searchItem.getSearchField());
                    }
                    PhotoViewer.getInstance().setParentActivity(PhotoPickerActivity.this.getParentActivity());
                    PhotoViewer.getInstance().openPhotoForSelect(view2, i, PhotoPickerActivity.this.singlePhoto, PhotoPickerActivity.this.provider, PhotoPickerActivity.this.chatActivity);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$7 */
    class C22317 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.PhotoPickerActivity$7$1 */
        class C15861 implements DialogInterface.OnClickListener {
            C15861() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                PhotoPickerActivity.this.recentImages.clear();
                if (PhotoPickerActivity.this.listAdapter != null) {
                    PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                }
                MessagesStorage.getInstance(PhotoPickerActivity.this.currentAccount).clearWebRecent(PhotoPickerActivity.this.type);
            }
        }

        C22317() {
        }

        public boolean onItemClick(View view, int i) {
            if (PhotoPickerActivity.this.searchResult.isEmpty() == null || PhotoPickerActivity.this.lastSearchString != null) {
                return null;
            }
            view = new Builder(PhotoPickerActivity.this.getParentActivity());
            view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            view.setMessage(LocaleController.getString("ClearSearch", C0446R.string.ClearSearch));
            view.setPositiveButton(LocaleController.getString("ClearButton", C0446R.string.ClearButton).toUpperCase(), new C15861());
            view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
            PhotoPickerActivity.this.showDialog(view.create());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$8 */
    class C22328 extends OnScrollListener {
        C22328() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1) {
                AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            recyclerView = PhotoPickerActivity.this.layoutManager.findFirstVisibleItemPosition();
            if (recyclerView == -1) {
                i2 = 0;
            } else {
                i2 = Math.abs(PhotoPickerActivity.this.layoutManager.findLastVisibleItemPosition() - recyclerView) + 1;
            }
            if (i2 > 0) {
                int itemCount = PhotoPickerActivity.this.layoutManager.getItemCount();
                if (i2 != 0 && recyclerView + i2 > itemCount - 2 && PhotoPickerActivity.this.searching == null) {
                    if (PhotoPickerActivity.this.type == null && PhotoPickerActivity.this.bingSearchEndReached == null) {
                        PhotoPickerActivity.this.searchBingImages(PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.searchResult.size(), 54);
                    } else if (PhotoPickerActivity.this.type == 1 && PhotoPickerActivity.this.giphySearchEndReached == null) {
                        PhotoPickerActivity.this.searchGiphyImages(PhotoPickerActivity.this.searchItem.getSearchField().getText().toString(), PhotoPickerActivity.this.nextGiphySearchOffset);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$1 */
    class C23461 extends EmptyPhotoViewerProvider {
        public boolean scaleToFill() {
            return false;
        }

        C23461() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            messageObject = PhotoPickerActivity.this.getCellForIndex(i);
            if (messageObject == null) {
                return null;
            }
            fileLocation = new int[2];
            messageObject.photoImage.getLocationInWindow(fileLocation);
            i = new PlaceProviderObject();
            i.viewX = fileLocation[0];
            i.viewY = fileLocation[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
            i.parentView = PhotoPickerActivity.this.listView;
            i.imageReceiver = messageObject.photoImage.getImageReceiver();
            i.thumb = i.imageReceiver.getBitmapSafe();
            i.scale = messageObject.photoImage.getScaleX();
            messageObject.showCheck(false);
            return i;
        }

        public void updatePhotoAtIndex(int i) {
            PhotoPickerPhotoCell access$000 = PhotoPickerActivity.this.getCellForIndex(i);
            if (access$000 == null) {
                return;
            }
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                access$000.photoImage.setOrientation(0, true);
                PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                if (photoEntry.thumbPath != null) {
                    access$000.photoImage.setImage(photoEntry.thumbPath, null, access$000.getContext().getResources().getDrawable(C0446R.drawable.nophotos));
                    return;
                } else if (photoEntry.path != null) {
                    access$000.photoImage.setOrientation(photoEntry.orientation, true);
                    BackupImageView backupImageView;
                    StringBuilder stringBuilder;
                    if (photoEntry.isVideo) {
                        backupImageView = access$000.photoImage;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("vthumb://");
                        stringBuilder.append(photoEntry.imageId);
                        stringBuilder.append(":");
                        stringBuilder.append(photoEntry.path);
                        backupImageView.setImage(stringBuilder.toString(), null, access$000.getContext().getResources().getDrawable(C0446R.drawable.nophotos));
                        return;
                    }
                    backupImageView = access$000.photoImage;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("thumb://");
                    stringBuilder.append(photoEntry.imageId);
                    stringBuilder.append(":");
                    stringBuilder.append(photoEntry.path);
                    backupImageView.setImage(stringBuilder.toString(), null, access$000.getContext().getResources().getDrawable(C0446R.drawable.nophotos));
                    return;
                } else {
                    access$000.photoImage.setImageResource(C0446R.drawable.nophotos);
                    return;
                }
            }
            ArrayList access$500;
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                access$500 = PhotoPickerActivity.this.recentImages;
            } else {
                access$500 = PhotoPickerActivity.this.searchResult;
            }
            SearchImage searchImage = (SearchImage) access$500.get(i);
            if (searchImage.document != null && searchImage.document.thumb != null) {
                access$000.photoImage.setImage(searchImage.document.thumb.location, null, access$000.getContext().getResources().getDrawable(C0446R.drawable.nophotos));
            } else if (searchImage.thumbPath != null) {
                access$000.photoImage.setImage(searchImage.thumbPath, null, access$000.getContext().getResources().getDrawable(C0446R.drawable.nophotos));
            } else if (searchImage.thumbUrl == null || searchImage.thumbUrl.length() <= 0) {
                access$000.photoImage.setImageResource(C0446R.drawable.nophotos);
            } else {
                access$000.photoImage.setImage(searchImage.thumbUrl, null, access$000.getContext().getResources().getDrawable(C0446R.drawable.nophotos));
            }
        }

        public boolean allowCaption() {
            return PhotoPickerActivity.this.allowCaption;
        }

        public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            messageObject = PhotoPickerActivity.this.getCellForIndex(i);
            return messageObject != null ? messageObject.photoImage.getImageReceiver().getBitmapSafe() : null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            FileLocation childCount = PhotoPickerActivity.this.listView.getChildCount();
            for (fileLocation = null; fileLocation < childCount; fileLocation++) {
                View childAt = PhotoPickerActivity.this.listView.getChildAt(fileLocation);
                if (childAt.getTag() != null) {
                    PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) childAt;
                    int intValue = ((Integer) childAt.getTag()).intValue();
                    if (PhotoPickerActivity.this.selectedAlbum == null) {
                        ArrayList access$500;
                        if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                            access$500 = PhotoPickerActivity.this.recentImages;
                        } else {
                            access$500 = PhotoPickerActivity.this.searchResult;
                        }
                        if (intValue < 0) {
                            continue;
                        } else if (intValue >= access$500.size()) {
                        }
                    } else if (intValue < 0) {
                        continue;
                    } else if (intValue >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                    }
                    if (intValue == i) {
                        photoPickerPhotoCell.showCheck(true);
                        return;
                    }
                }
            }
        }

        public void willHidePhotoViewer() {
            int childCount = PhotoPickerActivity.this.listView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = PhotoPickerActivity.this.listView.getChildAt(i);
                if (childAt instanceof PhotoPickerPhotoCell) {
                    ((PhotoPickerPhotoCell) childAt).showCheck(true);
                }
            }
        }

        public boolean isPhotoChecked(int i) {
            boolean z = false;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                if (i >= 0 && i < PhotoPickerActivity.this.selectedAlbum.photos.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(((PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i)).imageId)) != 0) {
                    z = true;
                }
                return z;
            }
            ArrayList access$500;
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                access$500 = PhotoPickerActivity.this.recentImages;
            } else {
                access$500 = PhotoPickerActivity.this.searchResult;
            }
            if (i >= 0 && i < r0.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(((SearchImage) r0.get(i)).id) != 0) {
                z = true;
            }
            return z;
        }

        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            int access$800;
            int i2 = -1;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                if (i >= 0) {
                    if (i < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                        PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                        access$800 = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                        if (access$800 == -1) {
                            photoEntry.editedInfo = videoEditedInfo;
                            access$800 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                            videoEditedInfo = 1;
                        } else {
                            photoEntry.editedInfo = null;
                            videoEditedInfo = null;
                        }
                        boolean z = videoEditedInfo;
                        videoEditedInfo = access$800;
                    }
                }
                return -1;
            }
            if (PhotoPickerActivity.this.searchResult.isEmpty() == null || PhotoPickerActivity.this.lastSearchString != null) {
                videoEditedInfo = PhotoPickerActivity.this.searchResult;
            } else {
                videoEditedInfo = PhotoPickerActivity.this.recentImages;
            }
            if (i >= 0) {
                if (i < videoEditedInfo.size()) {
                    SearchImage searchImage = (SearchImage) videoEditedInfo.get(i);
                    int access$8002 = PhotoPickerActivity.this.addToSelectedPhotos(searchImage, -1);
                    if (access$8002 == -1) {
                        videoEditedInfo = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(searchImage.id);
                        z = true;
                    } else {
                        videoEditedInfo = access$8002;
                        z = false;
                    }
                }
            }
            return -1;
            access$800 = PhotoPickerActivity.this.listView.getChildCount();
            for (int i3 = 0; i3 < access$800; i3++) {
                View childAt = PhotoPickerActivity.this.listView.getChildAt(i3);
                if (((Integer) childAt.getTag()).intValue() == i) {
                    PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) childAt;
                    if (PhotoPickerActivity.this.allowIndices != 0) {
                        i2 = videoEditedInfo;
                    }
                    photoPickerPhotoCell.setChecked(i2, z, false);
                    PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size(), true);
                    PhotoPickerActivity.this.delegate.selectedPhotosChanged();
                    return videoEditedInfo;
                }
            }
            PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size(), true);
            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            return videoEditedInfo;
        }

        public boolean cancelButtonPressed() {
            PhotoPickerActivity.this.delegate.actionButtonPressed(true);
            PhotoPickerActivity.this.finishFragment();
            return true;
        }

        public int getSelectedCount() {
            return PhotoPickerActivity.this.selectedPhotos.size();
        }

        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo) {
            if (PhotoPickerActivity.this.selectedPhotos.isEmpty()) {
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    if (i >= 0) {
                        if (i < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                            PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                            photoEntry.editedInfo = videoEditedInfo;
                            PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                        }
                    }
                    return;
                }
                if (PhotoPickerActivity.this.searchResult.isEmpty() == null || PhotoPickerActivity.this.lastSearchString != null) {
                    videoEditedInfo = PhotoPickerActivity.this.searchResult;
                } else {
                    videoEditedInfo = PhotoPickerActivity.this.recentImages;
                }
                if (i >= 0) {
                    if (i < videoEditedInfo.size()) {
                        PhotoPickerActivity.this.addToSelectedPhotos(videoEditedInfo.get(i), -1);
                    }
                }
                return;
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
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.PhotoPickerActivity$ListAdapter$1 */
        class C15881 implements OnClickListener {
            C15881() {
            }

            public void onClick(View view) {
                int intValue = ((Integer) ((View) view.getParent()).getTag()).intValue();
                int i = -1;
                boolean containsKey;
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(intValue);
                    containsKey = PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)) ^ true;
                    if (PhotoPickerActivity.this.allowIndices && containsKey) {
                        i = PhotoPickerActivity.this.selectedPhotosOrder.size();
                    }
                    ((PhotoPickerPhotoCell) view.getParent()).setChecked(i, containsKey, true);
                    PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, intValue);
                } else {
                    SearchImage searchImage;
                    AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                    if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                        searchImage = (SearchImage) PhotoPickerActivity.this.recentImages.get(((Integer) ((View) view.getParent()).getTag()).intValue());
                    } else {
                        searchImage = (SearchImage) PhotoPickerActivity.this.searchResult.get(((Integer) ((View) view.getParent()).getTag()).intValue());
                    }
                    containsKey = PhotoPickerActivity.this.selectedPhotos.containsKey(searchImage.id) ^ true;
                    if (PhotoPickerActivity.this.allowIndices && containsKey) {
                        i = PhotoPickerActivity.this.selectedPhotosOrder.size();
                    }
                    ((PhotoPickerPhotoCell) view.getParent()).setChecked(i, containsKey, true);
                    PhotoPickerActivity.this.addToSelectedPhotos(searchImage, intValue);
                }
                PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size(), true);
                PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            }
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            boolean z = true;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return true;
            }
            viewHolder = viewHolder.getAdapterPosition();
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                if (viewHolder >= PhotoPickerActivity.this.recentImages.size()) {
                    z = false;
                }
                return z;
            }
            if (viewHolder >= PhotoPickerActivity.this.searchResult.size()) {
                z = false;
            }
            return z;
        }

        public int getItemCount() {
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                    return PhotoPickerActivity.this.recentImages.size();
                }
                if (PhotoPickerActivity.this.type == 0) {
                    return PhotoPickerActivity.this.searchResult.size() + (1 ^ PhotoPickerActivity.this.bingSearchEndReached);
                }
                if (PhotoPickerActivity.this.type == 1) {
                    return PhotoPickerActivity.this.searchResult.size() + (1 ^ PhotoPickerActivity.this.giphySearchEndReached);
                }
            }
            return PhotoPickerActivity.this.selectedAlbum.photos.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new FrameLayout(this.mContext);
                i = new RadialProgressView(this.mContext);
                i.setProgressColor(-1);
                viewGroup.addView(i, LayoutHelper.createFrame(-1, -1.0f));
            } else {
                viewGroup = new PhotoPickerPhotoCell(this.mContext, true);
                viewGroup.checkFrame.setOnClickListener(new C15881());
                viewGroup.checkFrame.setVisibility(PhotoPickerActivity.this.singlePhoto ? 8 : 0);
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()) {
                case 0:
                    PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) viewHolder.itemView;
                    photoPickerPhotoCell.itemWidth = PhotoPickerActivity.this.itemWidth;
                    BackupImageView backupImageView = photoPickerPhotoCell.photoImage;
                    backupImageView.setTag(Integer.valueOf(i));
                    photoPickerPhotoCell.setTag(Integer.valueOf(i));
                    int i2 = 0;
                    backupImageView.setOrientation(0, true);
                    int i3 = -1;
                    if (PhotoPickerActivity.this.selectedAlbum != null) {
                        PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                        if (photoEntry.thumbPath != null) {
                            backupImageView.setImage(photoEntry.thumbPath, null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                        } else if (photoEntry.path != null) {
                            backupImageView.setOrientation(photoEntry.orientation, true);
                            StringBuilder stringBuilder;
                            if (photoEntry.isVideo) {
                                photoPickerPhotoCell.videoInfoContainer.setVisibility(0);
                                int i4 = photoEntry.duration - ((photoEntry.duration / 60) * 60);
                                photoPickerPhotoCell.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(r3), Integer.valueOf(i4)}));
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("vthumb://");
                                stringBuilder.append(photoEntry.imageId);
                                stringBuilder.append(":");
                                stringBuilder.append(photoEntry.path);
                                backupImageView.setImage(stringBuilder.toString(), null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                            } else {
                                photoPickerPhotoCell.videoInfoContainer.setVisibility(4);
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
                        if (PhotoPickerActivity.this.allowIndices) {
                            i3 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        }
                        photoPickerPhotoCell.setChecked(i3, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                        i = PhotoViewer.isShowingImage(photoEntry.path);
                    } else {
                        if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                            i = (SearchImage) PhotoPickerActivity.this.recentImages.get(i);
                        } else {
                            i = (SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                        }
                        if (i.thumbPath != null) {
                            backupImageView.setImage(i.thumbPath, null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                        } else if (i.thumbUrl != null && i.thumbUrl.length() > 0) {
                            backupImageView.setImage(i.thumbUrl, null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                        } else if (i.document == null || i.document.thumb == null) {
                            backupImageView.setImageResource(C0446R.drawable.nophotos);
                        } else {
                            backupImageView.setImage(i.document.thumb.location, null, this.mContext.getResources().getDrawable(C0446R.drawable.nophotos));
                        }
                        photoPickerPhotoCell.videoInfoContainer.setVisibility(4);
                        if (PhotoPickerActivity.this.allowIndices) {
                            i3 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(i.id);
                        }
                        photoPickerPhotoCell.setChecked(i3, PhotoPickerActivity.this.selectedPhotos.containsKey(i.id), false);
                        if (i.document != null) {
                            i = PhotoViewer.isShowingImage(FileLoader.getPathToAttach(i.document, true).getAbsolutePath());
                        } else {
                            i = PhotoViewer.isShowingImage(i.imageUrl);
                        }
                    }
                    backupImageView.getImageReceiver().setVisible(i == 0, true);
                    viewHolder = photoPickerPhotoCell.checkBox;
                    if (PhotoPickerActivity.this.singlePhoto || i != 0) {
                        i2 = 8;
                    }
                    viewHolder.setVisibility(i2);
                    return;
                case 1:
                    i = viewHolder.itemView.getLayoutParams();
                    if (i != 0) {
                        i.width = PhotoPickerActivity.this.itemWidth;
                        i.height = PhotoPickerActivity.this.itemWidth;
                        viewHolder.itemView.setLayoutParams(i);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (PhotoPickerActivity.this.selectedAlbum == null && !(PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null && i < PhotoPickerActivity.this.recentImages.size())) {
                if (i >= PhotoPickerActivity.this.searchResult.size()) {
                    return 1;
                }
            }
            return 0;
        }
    }

    public PhotoPickerActivity(int i, AlbumEntry albumEntry, HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, ArrayList<SearchImage> arrayList2, boolean z, boolean z2, ChatActivity chatActivity) {
        this.selectedAlbum = albumEntry;
        this.selectedPhotos = hashMap;
        this.selectedPhotosOrder = arrayList;
        this.type = i;
        this.recentImages = arrayList2;
        this.singlePhoto = z;
        this.chatActivity = chatActivity;
        this.allowCaption = z2;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoaded);
        if (this.selectedAlbum == null && this.recentImages.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).loadWebRecent(this.type);
            this.loadingRecent = true;
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentImagesDidLoaded);
        if (this.currentBingTask != null) {
            this.currentBingTask.cancel(true);
            this.currentBingTask = null;
        }
        if (this.giphyReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.giphyReqId, true);
            this.giphyReqId = 0;
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        boolean z = false;
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        if (this.selectedAlbum != null) {
            r0.actionBar.setTitle(r0.selectedAlbum.bucketName);
        } else if (r0.type == 0) {
            r0.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", C0446R.string.SearchImagesTitle));
        } else if (r0.type == 1) {
            r0.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", C0446R.string.SearchGifsTitle));
        }
        r0.actionBar.setActionBarMenuOnItemClick(new C22272());
        if (r0.selectedAlbum == null) {
            r0.searchItem = r0.actionBar.createMenu().addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C22283());
        }
        if (r0.selectedAlbum == null) {
            if (r0.type == 0) {
                r0.searchItem.getSearchField().setHint(LocaleController.getString("SearchImagesTitle", C0446R.string.SearchImagesTitle));
            } else if (r0.type == 1) {
                r0.searchItem.getSearchField().setHint(LocaleController.getString("SearchGifsTitle", C0446R.string.SearchGifsTitle));
            }
        }
        r0.fragmentView = new FrameLayout(context2);
        r0.frameLayout = (FrameLayout) r0.fragmentView;
        r0.frameLayout.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        r0.listView = new RecyclerListView(context2);
        r0.listView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        r0.listView.setClipToPadding(false);
        r0.listView.setHorizontalScrollBarEnabled(false);
        r0.listView.setVerticalScrollBarEnabled(false);
        ColorFilter colorFilter = null;
        r0.listView.setItemAnimator(null);
        r0.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView = r0.listView;
        LayoutManager c23554 = new GridLayoutManager(context2, 4) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        r0.layoutManager = c23554;
        recyclerListView.setLayoutManager(c23554);
        r0.listView.addItemDecoration(new C22295());
        r0.frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, r0.singlePhoto ? 0.0f : 48.0f));
        recyclerListView = r0.listView;
        Adapter listAdapter = new ListAdapter(context2);
        r0.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        r0.listView.setGlowColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        r0.listView.setOnItemClickListener(new C22306());
        if (r0.selectedAlbum == null) {
            r0.listView.setOnItemLongClickListener(new C22317());
        }
        r0.emptyView = new EmptyTextProgressView(context2);
        r0.emptyView.setTextColor(-8355712);
        r0.emptyView.setProgressBarColor(-1);
        r0.emptyView.setShowAtCenter(true);
        if (r0.selectedAlbum != null) {
            r0.emptyView.setText(LocaleController.getString("NoPhotos", C0446R.string.NoPhotos));
        } else if (r0.type == 0) {
            r0.emptyView.setText(LocaleController.getString("NoRecentPhotos", C0446R.string.NoRecentPhotos));
        } else if (r0.type == 1) {
            r0.emptyView.setText(LocaleController.getString("NoRecentGIFs", C0446R.string.NoRecentGIFs));
        }
        r0.frameLayout.addView(r0.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, r0.singlePhoto ? 0.0f : 48.0f));
        if (r0.selectedAlbum == null) {
            r0.listView.setOnScrollListener(new C22328());
            updateSearchInterface();
        }
        r0.pickerBottomLayout = new PickerBottomLayout(context2);
        r0.frameLayout.addView(r0.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        r0.pickerBottomLayout.cancelButton.setOnClickListener(new C15879());
        r0.pickerBottomLayout.doneButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotoPickerActivity.this.sendSelectedPhotos();
            }
        });
        if (r0.singlePhoto) {
            r0.pickerBottomLayout.setVisibility(8);
        } else if ((r0.selectedAlbum != null || r0.type == 0) && r0.chatActivity != null && r0.chatActivity.allowGroupPhotos()) {
            r0.imageOrderToggleButton = new ImageView(context2);
            r0.imageOrderToggleButton.setScaleType(ScaleType.CENTER);
            r0.imageOrderToggleButton.setImageResource(C0446R.drawable.photos_group);
            r0.pickerBottomLayout.addView(r0.imageOrderToggleButton, LayoutHelper.createFrame(48, -1, 17));
            r0.imageOrderToggleButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    SharedConfig.toggleGroupPhotosEnabled();
                    PhotoPickerActivity.this.imageOrderToggleButton.setColorFilter(SharedConfig.groupPhotosEnabled ? new PorterDuffColorFilter(-10043398, Mode.MULTIPLY) : null);
                    PhotoPickerActivity.this.showHint(false, SharedConfig.groupPhotosEnabled);
                    PhotoPickerActivity.this.updateCheckedPhotoIndices();
                }
            });
            ImageView imageView = r0.imageOrderToggleButton;
            if (SharedConfig.groupPhotosEnabled) {
                colorFilter = new PorterDuffColorFilter(-10043398, Mode.MULTIPLY);
            }
            imageView.setColorFilter(colorFilter);
        }
        if (r0.selectedAlbum != null || r0.type == 0) {
            z = true;
        }
        r0.allowIndices = z;
        r0.listView.setEmptyView(r0.emptyView);
        r0.pickerBottomLayout.updateSelectedCount(r0.selectedPhotos.size(), true);
        return r0.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.searchItem != null) {
            this.searchItem.openSearch(true);
            getParentActivity().getWindow().setSoftInputMode(32);
        }
        fixLayout();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.recentImagesDidLoaded && this.selectedAlbum == 0 && this.type == ((Integer) objArr[0]).intValue()) {
            this.recentImages = (ArrayList) objArr[1];
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
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PhotoPickerActivity.this.hintAnimation) != null) {
                    PhotoPickerActivity.this.hintAnimation = null;
                    PhotoPickerActivity.this.hintHideRunnable = null;
                    if (PhotoPickerActivity.this.hintTextView != null) {
                        PhotoPickerActivity.this.hintTextView.setVisibility(8);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(PhotoPickerActivity.this.hintAnimation) != null) {
                    PhotoPickerActivity.this.hintHideRunnable = null;
                    PhotoPickerActivity.this.hintHideRunnable = null;
                }
            }
        });
        this.hintAnimation.setDuration(300);
        this.hintAnimation.start();
    }

    private void showHint(boolean z, boolean z2) {
        if (!(getParentActivity() == null || this.fragmentView == null)) {
            if (!z || this.hintTextView != null) {
                if (this.hintTextView == null) {
                    this.hintTextView = new TextView(getParentActivity());
                    this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
                    this.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
                    this.hintTextView.setTextSize(1, 14.0f);
                    this.hintTextView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
                    this.hintTextView.setGravity(16);
                    this.hintTextView.setAlpha(0.0f);
                    this.frameLayout.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 5.0f, 0.0f, 5.0f, 51.0f));
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
                                PhotoPickerActivity.this.hideHint();
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

                    /* renamed from: org.telegram.ui.PhotoPickerActivity$14$1 */
                    class C15841 implements Runnable {
                        C15841() {
                        }

                        public void run() {
                            PhotoPickerActivity.this.hideHint();
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(PhotoPickerActivity.this.hintAnimation) != null) {
                            PhotoPickerActivity.this.hintAnimation = null;
                            AndroidUtilities.runOnUIThread(PhotoPickerActivity.this.hintHideRunnable = new C15841(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (animator.equals(PhotoPickerActivity.this.hintAnimation) != null) {
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
            int childCount = this.listView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof PhotoPickerPhotoCell) {
                    PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) childAt;
                    Integer num = (Integer) photoPickerPhotoCell.getTag();
                    int i2 = -1;
                    if (this.selectedAlbum != null) {
                        PhotoEntry photoEntry = (PhotoEntry) this.selectedAlbum.photos.get(num.intValue());
                        if (this.allowIndices) {
                            i2 = this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        }
                        photoPickerPhotoCell.setNum(i2);
                    } else {
                        SearchImage searchImage;
                        if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                            searchImage = (SearchImage) this.recentImages.get(num.intValue());
                        } else {
                            searchImage = (SearchImage) this.searchResult.get(num.intValue());
                        }
                        if (this.allowIndices) {
                            i2 = this.selectedPhotosOrder.indexOf(searchImage.id);
                        }
                        photoPickerPhotoCell.setNum(i2);
                    }
                }
            }
        }
    }

    private PhotoPickerPhotoCell getCellForIndex(int i) {
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof PhotoPickerPhotoCell) {
                PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) childAt;
                int intValue = ((Integer) photoPickerPhotoCell.photoImage.getTag()).intValue();
                if (this.selectedAlbum == null) {
                    ArrayList arrayList;
                    if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                        arrayList = this.recentImages;
                    } else {
                        arrayList = this.searchResult;
                    }
                    if (intValue < 0) {
                        continue;
                    } else if (intValue >= arrayList.size()) {
                    }
                } else if (intValue < 0) {
                    continue;
                } else if (intValue >= this.selectedAlbum.photos.size()) {
                }
                if (intValue == i) {
                    return photoPickerPhotoCell;
                }
            }
        }
        return 0;
    }

    private int addToSelectedPhotos(Object obj, int i) {
        boolean z = obj instanceof PhotoEntry;
        Object valueOf = z ? Integer.valueOf(((PhotoEntry) obj).imageId) : obj instanceof SearchImage ? ((SearchImage) obj).id : null;
        if (valueOf == null) {
            return -1;
        }
        if (this.selectedPhotos.containsKey(valueOf)) {
            this.selectedPhotos.remove(valueOf);
            int indexOf = this.selectedPhotosOrder.indexOf(valueOf);
            if (indexOf >= 0) {
                this.selectedPhotosOrder.remove(indexOf);
            }
            if (this.allowIndices) {
                updateCheckedPhotoIndices();
            }
            if (i >= 0) {
                if (z) {
                    ((PhotoEntry) obj).reset();
                } else if (obj instanceof SearchImage) {
                    ((SearchImage) obj).reset();
                }
                this.provider.updatePhotoAtIndex(i);
            }
            return indexOf;
        }
        this.selectedPhotos.put(valueOf, obj);
        this.selectedPhotosOrder.add(valueOf);
        return -1;
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.searchItem) {
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

    private void searchGiphyImages(final String str, int i) {
        if (this.searching) {
            this.searching = false;
            if (this.giphyReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.giphyReqId, true);
                this.giphyReqId = 0;
            }
            if (this.currentBingTask != null) {
                this.currentBingTask.cancel(true);
                this.currentBingTask = null;
            }
        }
        this.searching = true;
        TLObject tL_messages_searchGifs = new TL_messages_searchGifs();
        tL_messages_searchGifs.f50q = str;
        tL_messages_searchGifs.offset = i;
        i = this.lastSearchToken + 1;
        this.lastSearchToken = i;
        this.giphyReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_searchGifs, new RequestDelegate() {
            public void run(final TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (i == PhotoPickerActivity.this.lastSearchToken) {
                            int i;
                            if (tLObject != null) {
                                TL_messages_foundGifs tL_messages_foundGifs = (TL_messages_foundGifs) tLObject;
                                PhotoPickerActivity.this.nextGiphySearchOffset = tL_messages_foundGifs.next_offset;
                                int i2 = 0;
                                int i3 = i2;
                                i = i3;
                                while (i2 < tL_messages_foundGifs.results.size()) {
                                    FoundGif foundGif = (FoundGif) tL_messages_foundGifs.results.get(i2);
                                    if (!PhotoPickerActivity.this.searchResultKeys.containsKey(foundGif.url)) {
                                        SearchImage searchImage = new SearchImage();
                                        searchImage.id = foundGif.url;
                                        if (foundGif.document != null) {
                                            int i4 = 0;
                                            while (i4 < foundGif.document.attributes.size()) {
                                                DocumentAttribute documentAttribute = (DocumentAttribute) foundGif.document.attributes.get(i4);
                                                if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                                    if (!(documentAttribute instanceof TL_documentAttributeVideo)) {
                                                        i4++;
                                                    }
                                                }
                                                searchImage.width = documentAttribute.f36w;
                                                searchImage.height = documentAttribute.f35h;
                                            }
                                        } else {
                                            searchImage.width = foundGif.f38w;
                                            searchImage.height = foundGif.f37h;
                                        }
                                        searchImage.size = 0;
                                        searchImage.imageUrl = foundGif.content_url;
                                        searchImage.thumbUrl = foundGif.thumb_url;
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(foundGif.url);
                                        stringBuilder.append("|");
                                        stringBuilder.append(str);
                                        searchImage.localUrl = stringBuilder.toString();
                                        searchImage.document = foundGif.document;
                                        if (!(foundGif.photo == null || foundGif.document == null)) {
                                            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(foundGif.photo.sizes, PhotoPickerActivity.this.itemWidth, true);
                                            if (closestPhotoSizeWithSize != null) {
                                                foundGif.document.thumb = closestPhotoSizeWithSize;
                                            }
                                        }
                                        searchImage.type = 1;
                                        PhotoPickerActivity.this.searchResult.add(searchImage);
                                        i++;
                                        PhotoPickerActivity.this.searchResultKeys.put(searchImage.id, searchImage);
                                        i3 = 1;
                                    }
                                    i2++;
                                }
                                PhotoPickerActivity.this.giphySearchEndReached = i3 ^ 1;
                            } else {
                                i = 0;
                            }
                            PhotoPickerActivity.this.searching = false;
                            if (i != 0) {
                                PhotoPickerActivity.this.listAdapter.notifyItemRangeInserted(PhotoPickerActivity.this.searchResult.size(), i);
                            } else if (PhotoPickerActivity.this.giphySearchEndReached) {
                                PhotoPickerActivity.this.listAdapter.notifyItemRemoved(PhotoPickerActivity.this.searchResult.size() - 1);
                            }
                            if ((PhotoPickerActivity.this.searching && PhotoPickerActivity.this.searchResult.isEmpty()) || (PhotoPickerActivity.this.loadingRecent && PhotoPickerActivity.this.lastSearchString == null)) {
                                PhotoPickerActivity.this.emptyView.showProgress();
                            } else {
                                PhotoPickerActivity.this.emptyView.showTextView();
                            }
                        }
                    }
                });
            }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.giphyReqId, this.classGuid);
    }

    private void searchBingImages(String str, int i, int i2) {
        if (this.searching) {
            this.searching = false;
            if (this.giphyReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.giphyReqId, true);
                this.giphyReqId = 0;
            }
            if (this.currentBingTask != null) {
                this.currentBingTask.cancel(true);
                this.currentBingTask = null;
            }
        }
        try {
            boolean z;
            Locale locale;
            String str2;
            Object[] objArr;
            this.searching = true;
            String str3 = UserConfig.getInstance(this.currentAccount).getCurrentUser().phone;
            if (!(str3.startsWith("44") || str3.startsWith("49") || str3.startsWith("43") || str3.startsWith("31"))) {
                if (!str3.startsWith("1")) {
                    z = false;
                    locale = Locale.US;
                    str2 = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q='%s'&offset=%d&count=%d&$format=json&safeSearch=%s";
                    objArr = new Object[4];
                    objArr[0] = URLEncoder.encode(str, C0542C.UTF8_NAME);
                    objArr[1] = Integer.valueOf(i);
                    objArr[2] = Integer.valueOf(i2);
                    objArr[3] = z ? "Strict" : "Off";
                    str = String.format(locale, str2, objArr);
                    this.currentBingTask = new AsyncTask<Void, Void, JSONObject>() {
                        private boolean canRetry = true;

                        /* JADX WARNING: inconsistent code. */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        private String downloadUrlContent(String str) {
                            Throwable th;
                            boolean z;
                            InputStream inputStream;
                            StringBuilder stringBuilder;
                            Throwable e;
                            int read;
                            int i = 0;
                            try {
                                str = new URL(str).openConnection();
                                try {
                                    str.addRequestProperty("Ocp-Apim-Subscription-Key", BuildVars.BING_SEARCH_KEY);
                                    str.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
                                    str.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                                    str.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                                    str.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                                    str.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                                    str.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                                    if (str instanceof HttpURLConnection) {
                                        HttpURLConnection httpURLConnection = (HttpURLConnection) str;
                                        httpURLConnection.setInstanceFollowRedirects(true);
                                        int responseCode = httpURLConnection.getResponseCode();
                                        if (responseCode == 302 || responseCode == 301 || responseCode == 303) {
                                            String headerField = httpURLConnection.getHeaderField("Location");
                                            String headerField2 = httpURLConnection.getHeaderField("Set-Cookie");
                                            URLConnection openConnection = new URL(headerField).openConnection();
                                            try {
                                                openConnection.setRequestProperty("Cookie", headerField2);
                                                openConnection.addRequestProperty("Ocp-Apim-Subscription-Key", BuildVars.BING_SEARCH_KEY);
                                                openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
                                                openConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                                                openConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                                                openConnection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                                                str = openConnection;
                                            } catch (String str2) {
                                                th = str2;
                                                str2 = openConnection;
                                                if (th instanceof SocketTimeoutException) {
                                                    if (th instanceof UnknownHostException) {
                                                        if (!(th instanceof SocketException)) {
                                                            if (th instanceof FileNotFoundException) {
                                                            }
                                                            z = true;
                                                            FileLog.m3e(th);
                                                            inputStream = null;
                                                            if (z) {
                                                                stringBuilder = null;
                                                            } else {
                                                                if (str2 != null) {
                                                                    try {
                                                                    } catch (Throwable e2) {
                                                                        FileLog.m3e(e2);
                                                                    }
                                                                }
                                                                if (inputStream == null) {
                                                                    try {
                                                                        str2 = new byte[32768];
                                                                        stringBuilder = null;
                                                                        while (!isCancelled()) {
                                                                            try {
                                                                                read = inputStream.read(str2);
                                                                                if (read <= 0) {
                                                                                    if (stringBuilder == null) {
                                                                                        stringBuilder = new StringBuilder();
                                                                                    }
                                                                                    stringBuilder.append(new String(str2, 0, read, C0542C.UTF8_NAME));
                                                                                } else if (read == -1) {
                                                                                    i = 1;
                                                                                }
                                                                            } catch (Throwable e22) {
                                                                                FileLog.m3e(e22);
                                                                            } catch (Throwable th2) {
                                                                                e22 = th2;
                                                                            }
                                                                        }
                                                                    } catch (Throwable th3) {
                                                                        e22 = th3;
                                                                        stringBuilder = null;
                                                                        FileLog.m3e(e22);
                                                                        if (inputStream != null) {
                                                                            try {
                                                                                inputStream.close();
                                                                            } catch (Throwable e222) {
                                                                                FileLog.m3e(e222);
                                                                            }
                                                                        }
                                                                        if (i != 0) {
                                                                            return stringBuilder.toString();
                                                                        }
                                                                        return null;
                                                                    }
                                                                }
                                                                stringBuilder = null;
                                                                if (inputStream != null) {
                                                                    inputStream.close();
                                                                }
                                                            }
                                                            if (i != 0) {
                                                                return stringBuilder.toString();
                                                            }
                                                            return null;
                                                        }
                                                    }
                                                }
                                                z = false;
                                                FileLog.m3e(th);
                                                inputStream = null;
                                                if (z) {
                                                    stringBuilder = null;
                                                } else {
                                                    if (str2 != null) {
                                                    }
                                                    if (inputStream == null) {
                                                        stringBuilder = null;
                                                    } else {
                                                        str2 = new byte[32768];
                                                        stringBuilder = null;
                                                        while (!isCancelled()) {
                                                            read = inputStream.read(str2);
                                                            if (read <= 0) {
                                                                if (stringBuilder == null) {
                                                                    stringBuilder = new StringBuilder();
                                                                }
                                                                stringBuilder.append(new String(str2, 0, read, C0542C.UTF8_NAME));
                                                            } else if (read == -1) {
                                                                i = 1;
                                                            }
                                                        }
                                                    }
                                                    if (inputStream != null) {
                                                        inputStream.close();
                                                    }
                                                }
                                                if (i != 0) {
                                                    return null;
                                                }
                                                return stringBuilder.toString();
                                            }
                                        }
                                    }
                                    str2.connect();
                                    inputStream = str2.getInputStream();
                                    z = true;
                                } catch (Throwable th4) {
                                    th = th4;
                                    if (th instanceof SocketTimeoutException) {
                                        if (th instanceof UnknownHostException) {
                                            if (!(th instanceof SocketException)) {
                                                if (th.getMessage() != null && th.getMessage().contains("ECONNRESET")) {
                                                }
                                                z = true;
                                                FileLog.m3e(th);
                                                inputStream = null;
                                                if (z) {
                                                    stringBuilder = null;
                                                } else {
                                                    if (str2 != null) {
                                                    }
                                                    if (inputStream == null) {
                                                        stringBuilder = null;
                                                    } else {
                                                        str2 = new byte[32768];
                                                        stringBuilder = null;
                                                        while (!isCancelled()) {
                                                            read = inputStream.read(str2);
                                                            if (read <= 0) {
                                                                if (stringBuilder == null) {
                                                                    stringBuilder = new StringBuilder();
                                                                }
                                                                stringBuilder.append(new String(str2, 0, read, C0542C.UTF8_NAME));
                                                            } else if (read == -1) {
                                                                i = 1;
                                                            }
                                                        }
                                                    }
                                                    if (inputStream != null) {
                                                        inputStream.close();
                                                    }
                                                }
                                                if (i != 0) {
                                                    return null;
                                                }
                                                return stringBuilder.toString();
                                            }
                                            if (th instanceof FileNotFoundException) {
                                            }
                                            z = true;
                                            FileLog.m3e(th);
                                            inputStream = null;
                                            if (z) {
                                                if (str2 != null) {
                                                }
                                                if (inputStream == null) {
                                                    str2 = new byte[32768];
                                                    stringBuilder = null;
                                                    while (!isCancelled()) {
                                                        read = inputStream.read(str2);
                                                        if (read <= 0) {
                                                            if (stringBuilder == null) {
                                                                stringBuilder = new StringBuilder();
                                                            }
                                                            stringBuilder.append(new String(str2, 0, read, C0542C.UTF8_NAME));
                                                        } else if (read == -1) {
                                                            i = 1;
                                                        }
                                                    }
                                                } else {
                                                    stringBuilder = null;
                                                }
                                                if (inputStream != null) {
                                                    inputStream.close();
                                                }
                                            } else {
                                                stringBuilder = null;
                                            }
                                            if (i != 0) {
                                                return stringBuilder.toString();
                                            }
                                            return null;
                                        }
                                    }
                                    z = false;
                                    FileLog.m3e(th);
                                    inputStream = null;
                                    if (z) {
                                        stringBuilder = null;
                                    } else {
                                        if (str2 != null) {
                                        }
                                        if (inputStream == null) {
                                            stringBuilder = null;
                                        } else {
                                            str2 = new byte[32768];
                                            stringBuilder = null;
                                            while (!isCancelled()) {
                                                read = inputStream.read(str2);
                                                if (read <= 0) {
                                                    if (stringBuilder == null) {
                                                        stringBuilder = new StringBuilder();
                                                    }
                                                    stringBuilder.append(new String(str2, 0, read, C0542C.UTF8_NAME));
                                                } else if (read == -1) {
                                                    i = 1;
                                                }
                                            }
                                        }
                                        if (inputStream != null) {
                                            inputStream.close();
                                        }
                                    }
                                    if (i != 0) {
                                        return null;
                                    }
                                    return stringBuilder.toString();
                                }
                            } catch (String str22) {
                                th = str22;
                                str22 = null;
                                if (th instanceof SocketTimeoutException) {
                                    if (th instanceof UnknownHostException) {
                                        if (!(th instanceof SocketException)) {
                                            if (th instanceof FileNotFoundException) {
                                            }
                                            z = true;
                                            FileLog.m3e(th);
                                            inputStream = null;
                                            if (z) {
                                                if (str22 != null) {
                                                }
                                                if (inputStream == null) {
                                                    str22 = new byte[32768];
                                                    stringBuilder = null;
                                                    while (!isCancelled()) {
                                                        read = inputStream.read(str22);
                                                        if (read <= 0) {
                                                            if (stringBuilder == null) {
                                                                stringBuilder = new StringBuilder();
                                                            }
                                                            stringBuilder.append(new String(str22, 0, read, C0542C.UTF8_NAME));
                                                        } else if (read == -1) {
                                                            i = 1;
                                                        }
                                                    }
                                                } else {
                                                    stringBuilder = null;
                                                }
                                                if (inputStream != null) {
                                                    inputStream.close();
                                                }
                                            } else {
                                                stringBuilder = null;
                                            }
                                            if (i != 0) {
                                                return stringBuilder.toString();
                                            }
                                            return null;
                                        }
                                    }
                                }
                                z = false;
                                FileLog.m3e(th);
                                inputStream = null;
                                if (z) {
                                    stringBuilder = null;
                                } else {
                                    if (str22 != null) {
                                    }
                                    if (inputStream == null) {
                                        stringBuilder = null;
                                    } else {
                                        str22 = new byte[32768];
                                        stringBuilder = null;
                                        while (!isCancelled()) {
                                            read = inputStream.read(str22);
                                            if (read <= 0) {
                                                if (stringBuilder == null) {
                                                    stringBuilder = new StringBuilder();
                                                }
                                                stringBuilder.append(new String(str22, 0, read, C0542C.UTF8_NAME));
                                            } else if (read == -1) {
                                                i = 1;
                                            }
                                        }
                                    }
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                }
                                if (i != 0) {
                                    return null;
                                }
                                return stringBuilder.toString();
                            }
                            if (z) {
                                if (str22 != null) {
                                    if ((str22 instanceof HttpURLConnection) && ((HttpURLConnection) str22).getResponseCode() != 200) {
                                    }
                                }
                                if (inputStream == null) {
                                    str22 = new byte[32768];
                                    stringBuilder = null;
                                    while (!isCancelled()) {
                                        read = inputStream.read(str22);
                                        if (read <= 0) {
                                            if (stringBuilder == null) {
                                                stringBuilder = new StringBuilder();
                                            }
                                            stringBuilder.append(new String(str22, 0, read, C0542C.UTF8_NAME));
                                        } else if (read == -1) {
                                            i = 1;
                                        }
                                    }
                                } else {
                                    stringBuilder = null;
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            } else {
                                stringBuilder = null;
                            }
                            if (i != 0) {
                                return stringBuilder.toString();
                            }
                            return null;
                        }

                        protected JSONObject doInBackground(Void... voidArr) {
                            voidArr = downloadUrlContent(str);
                            if (isCancelled()) {
                                return null;
                            }
                            try {
                                return new JSONObject(voidArr);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                                return null;
                            }
                        }

                        protected void onPostExecute(JSONObject jSONObject) {
                            Throwable e;
                            int i = 0;
                            if (jSONObject != null) {
                                int i2;
                                try {
                                    jSONObject = jSONObject.getJSONArray("value");
                                    int i3 = 0;
                                    int i4 = i3;
                                    i2 = i4;
                                    while (i3 < jSONObject.length()) {
                                        try {
                                            JSONObject jSONObject2 = jSONObject.getJSONObject(i3);
                                            String MD5 = Utilities.MD5(jSONObject2.getString("contentUrl"));
                                            if (!PhotoPickerActivity.this.searchResultKeys.containsKey(MD5)) {
                                                SearchImage searchImage = new SearchImage();
                                                searchImage.id = MD5;
                                                searchImage.width = jSONObject2.getInt("width");
                                                searchImage.height = jSONObject2.getInt("height");
                                                searchImage.size = Utilities.parseInt(jSONObject2.getString("contentSize")).intValue();
                                                searchImage.imageUrl = jSONObject2.getString("contentUrl");
                                                searchImage.thumbUrl = jSONObject2.getString("thumbnailUrl");
                                                PhotoPickerActivity.this.searchResult.add(searchImage);
                                                PhotoPickerActivity.this.searchResultKeys.put(MD5, searchImage);
                                                i2++;
                                                i4 = 1;
                                            }
                                        } catch (Throwable e2) {
                                            try {
                                                FileLog.m3e(e2);
                                            } catch (Exception e3) {
                                                e = e3;
                                            }
                                        }
                                        i3++;
                                    }
                                    PhotoPickerActivity.this.bingSearchEndReached = i4 ^ 1;
                                } catch (Exception e4) {
                                    e = e4;
                                    i2 = 0;
                                    FileLog.m3e(e);
                                    PhotoPickerActivity.this.searching = false;
                                    i = i2;
                                    if (i != 0) {
                                        PhotoPickerActivity.this.listAdapter.notifyItemRangeInserted(PhotoPickerActivity.this.searchResult.size(), i);
                                    } else if (PhotoPickerActivity.this.giphySearchEndReached != null) {
                                        PhotoPickerActivity.this.listAdapter.notifyItemRemoved(PhotoPickerActivity.this.searchResult.size() - 1);
                                    }
                                    if (PhotoPickerActivity.this.searching != null) {
                                    }
                                    PhotoPickerActivity.this.emptyView.showTextView();
                                }
                                PhotoPickerActivity.this.searching = false;
                                i = i2;
                            } else {
                                PhotoPickerActivity.this.bingSearchEndReached = true;
                                PhotoPickerActivity.this.searching = false;
                            }
                            if (i != 0) {
                                PhotoPickerActivity.this.listAdapter.notifyItemRangeInserted(PhotoPickerActivity.this.searchResult.size(), i);
                            } else if (PhotoPickerActivity.this.giphySearchEndReached != null) {
                                PhotoPickerActivity.this.listAdapter.notifyItemRemoved(PhotoPickerActivity.this.searchResult.size() - 1);
                            }
                            if ((PhotoPickerActivity.this.searching != null || PhotoPickerActivity.this.searchResult.isEmpty() == null) && (PhotoPickerActivity.this.loadingRecent == null || PhotoPickerActivity.this.lastSearchString != null)) {
                                PhotoPickerActivity.this.emptyView.showTextView();
                            } else {
                                PhotoPickerActivity.this.emptyView.showProgress();
                            }
                        }
                    };
                    this.currentBingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                }
            }
            z = true;
            locale = Locale.US;
            str2 = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q='%s'&offset=%d&count=%d&$format=json&safeSearch=%s";
            objArr = new Object[4];
            objArr[0] = URLEncoder.encode(str, C0542C.UTF8_NAME);
            objArr[1] = Integer.valueOf(i);
            objArr[2] = Integer.valueOf(i2);
            if (z) {
            }
            objArr[3] = z ? "Strict" : "Off";
            str = String.format(locale, str2, objArr);
            this.currentBingTask = /* anonymous class already generated */;
            this.currentBingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
        } catch (Throwable e) {
            FileLog.m3e(e);
            this.bingSearchEndReached = true;
            this.searching = false;
            this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
            if ((this.searching == null || this.searchResult.isEmpty() == null) && (this.loadingRecent == null || this.lastSearchString != null)) {
                this.emptyView.showTextView();
            } else {
                this.emptyView.showProgress();
            }
        }
    }

    public void setDelegate(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
        this.delegate = photoPickerActivityDelegate;
    }

    private void sendSelectedPhotos() {
        if (!(this.selectedPhotos.isEmpty() || this.delegate == null)) {
            if (!this.sendPressed) {
                this.sendPressed = true;
                this.delegate.actionButtonPressed(false);
                finishFragment();
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
            int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            int i = 3;
            if (!AndroidUtilities.isTablet()) {
                if (rotation == 3 || rotation == 1) {
                    i = 5;
                }
            }
            this.layoutManager.setSpanCount(i);
            if (AndroidUtilities.isTablet()) {
                this.itemWidth = (AndroidUtilities.dp(490.0f) - ((i + 1) * AndroidUtilities.dp(4.0f))) / i;
            } else {
                this.itemWidth = (AndroidUtilities.displaySize.x - ((i + 1) * AndroidUtilities.dp(4.0f))) / i;
            }
            this.listAdapter.notifyDataSetChanged();
            this.layoutManager.scrollToPosition(findFirstVisibleItemPosition);
            if (this.selectedAlbum == null) {
                this.emptyView.setPadding(0, 0, 0, (int) (((float) (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight())) * 0.4f));
            }
        }
    }
}
