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
import android.view.ViewGroup.LayoutParams;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
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
import org.telegram.messenger.beta.R;
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
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
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
import org.telegram.ui.Components.CheckBox;
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

        public void onItemClick(int id) {
            if (id == -1) {
                PhotoPickerActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$3 */
    class C22283 extends ActionBarMenuItemSearchListener {
        C22283() {
        }

        public void onSearchExpand() {
        }

        public boolean canCollapseSearch() {
            PhotoPickerActivity.this.finishFragment();
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
                if (PhotoPickerActivity.this.currentBingTask != null) {
                    PhotoPickerActivity.this.currentBingTask.cancel(true);
                    PhotoPickerActivity.this.currentBingTask = null;
                }
                if (PhotoPickerActivity.this.giphyReqId != 0) {
                    ConnectionsManager.getInstance(PhotoPickerActivity.this.currentAccount).cancelRequest(PhotoPickerActivity.this.giphyReqId, true);
                    PhotoPickerActivity.this.giphyReqId = 0;
                }
                if (PhotoPickerActivity.this.type == 0) {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", R.string.NoRecentPhotos));
                } else if (PhotoPickerActivity.this.type == 1) {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", R.string.NoRecentGIFs));
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
                if (PhotoPickerActivity.this.lastSearchString.length() == 0) {
                    PhotoPickerActivity.this.lastSearchString = null;
                    if (PhotoPickerActivity.this.type == 0) {
                        PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", R.string.NoRecentPhotos));
                    } else if (PhotoPickerActivity.this.type == 1) {
                        PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", R.string.NoRecentGIFs));
                    }
                } else {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                }
                PhotoPickerActivity.this.updateSearchInterface();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$5 */
    class C22295 extends ItemDecoration {
        C22295() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int total = state.getItemCount();
            int position = parent.getChildAdapterPosition(view);
            int spanCount = PhotoPickerActivity.this.layoutManager.getSpanCount();
            int rowsCOunt = (int) Math.ceil((double) (((float) total) / ((float) spanCount)));
            int row = position / spanCount;
            int i = 0;
            outRect.right = position % spanCount != spanCount + -1 ? AndroidUtilities.dp(4.0f) : 0;
            if (row != rowsCOunt - 1) {
                i = AndroidUtilities.dp(4.0f);
            }
            outRect.bottom = i;
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$6 */
    class C22306 implements OnItemClickListener {
        C22306() {
        }

        public void onItemClick(View view, int position) {
            ArrayList<Object> arrayList;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                arrayList = PhotoPickerActivity.this.selectedAlbum.photos;
            } else if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                arrayList = PhotoPickerActivity.this.recentImages;
            } else {
                arrayList = PhotoPickerActivity.this.searchResult;
                if (position >= 0) {
                    if (position >= arrayList.size()) {
                        if (PhotoPickerActivity.this.searchItem != null) {
                            AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.searchItem.getSearchField());
                        }
                        PhotoViewer.getInstance().setParentActivity(PhotoPickerActivity.this.getParentActivity());
                        PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, PhotoPickerActivity.this.singlePhoto, PhotoPickerActivity.this.provider, PhotoPickerActivity.this.chatActivity);
                    }
                }
            }
            if (position >= 0) {
                if (position >= arrayList.size()) {
                    if (PhotoPickerActivity.this.searchItem != null) {
                        AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.searchItem.getSearchField());
                    }
                    PhotoViewer.getInstance().setParentActivity(PhotoPickerActivity.this.getParentActivity());
                    PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, PhotoPickerActivity.this.singlePhoto, PhotoPickerActivity.this.provider, PhotoPickerActivity.this.chatActivity);
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

        public boolean onItemClick(View view, int position) {
            if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                return false;
            }
            Builder builder = new Builder(PhotoPickerActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
            builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new C15861());
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            PhotoPickerActivity.this.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$8 */
    class C22328 extends OnScrollListener {
        C22328() {
        }

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
                        PhotoPickerActivity.this.searchBingImages(PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.searchResult.size(), 54);
                    } else if (PhotoPickerActivity.this.type == 1 && !PhotoPickerActivity.this.giphySearchEndReached) {
                        PhotoPickerActivity.this.searchGiphyImages(PhotoPickerActivity.this.searchItem.getSearchField().getText().toString(), PhotoPickerActivity.this.nextGiphySearchOffset);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$1 */
    class C23461 extends EmptyPhotoViewerProvider {
        C23461() {
        }

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
                } else if (photoEntry.path != null) {
                    cell.photoImage.setOrientation(photoEntry.orientation, true);
                    BackupImageView backupImageView;
                    StringBuilder stringBuilder;
                    if (photoEntry.isVideo) {
                        backupImageView = cell.photoImage;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("vthumb://");
                        stringBuilder.append(photoEntry.imageId);
                        stringBuilder.append(":");
                        stringBuilder.append(photoEntry.path);
                        backupImageView.setImage(stringBuilder.toString(), null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
                    } else {
                        backupImageView = cell.photoImage;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("thumb://");
                        stringBuilder.append(photoEntry.imageId);
                        stringBuilder.append(":");
                        stringBuilder.append(photoEntry.path);
                        backupImageView.setImage(stringBuilder.toString(), null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
                    }
                } else {
                    cell.photoImage.setImageResource(R.drawable.nophotos);
                }
                return;
            }
            ArrayList<SearchImage> array;
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                array = PhotoPickerActivity.this.recentImages;
            } else {
                array = PhotoPickerActivity.this.searchResult;
            }
            SearchImage photoEntry2 = (SearchImage) array.get(index);
            if (photoEntry2.document != null && photoEntry2.document.thumb != null) {
                cell.photoImage.setImage(photoEntry2.document.thumb.location, null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
            } else if (photoEntry2.thumbPath != null) {
                cell.photoImage.setImage(photoEntry2.thumbPath, null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
            } else if (photoEntry2.thumbUrl == null || photoEntry2.thumbUrl.length() <= 0) {
                cell.photoImage.setImageResource(R.drawable.nophotos);
            } else {
                cell.photoImage.setImage(photoEntry2.thumbUrl, null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
            }
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
                        }
                    } else if (num < 0) {
                        continue;
                    } else if (num >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
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
            boolean z = false;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                if (index >= 0 && index < PhotoPickerActivity.this.selectedAlbum.photos.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(((PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index)).imageId))) {
                    z = true;
                }
                return z;
            }
            ArrayList<SearchImage> array;
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                array = PhotoPickerActivity.this.recentImages;
            } else {
                array = PhotoPickerActivity.this.searchResult;
            }
            if (index >= 0 && index < array.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(((SearchImage) array.get(index)).id)) {
                z = true;
            }
            return z;
        }

        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            int num;
            ArrayList<SearchImage> array;
            boolean add = true;
            int i = -1;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                if (index >= 0) {
                    if (index < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                        PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                        int access$800 = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                        num = access$800;
                        if (access$800 == -1) {
                            photoEntry.editedInfo = videoEditedInfo;
                            num = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        } else {
                            add = false;
                            photoEntry.editedInfo = null;
                        }
                    }
                }
                return -1;
            }
            int num2;
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                array = PhotoPickerActivity.this.recentImages;
            } else {
                array = PhotoPickerActivity.this.searchResult;
            }
            if (index >= 0) {
                if (index < array.size()) {
                    SearchImage photoEntry2 = (SearchImage) array.get(index);
                    num = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry2, -1);
                    num2 = num;
                    if (num == -1) {
                        num = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(photoEntry2.id);
                    } else {
                        add = false;
                        num = num2;
                    }
                }
            }
            return -1;
            array = PhotoPickerActivity.this.listView.getChildCount();
            for (num2 = 0; num2 < array; num2++) {
                View view = PhotoPickerActivity.this.listView.getChildAt(num2);
                if (((Integer) view.getTag()).intValue() == index) {
                    PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) view;
                    if (PhotoPickerActivity.this.allowIndices) {
                        i = num;
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
            PhotoPickerActivity.this.finishFragment();
            return true;
        }

        public int getSelectedCount() {
            return PhotoPickerActivity.this.selectedPhotos.size();
        }

        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
            if (PhotoPickerActivity.this.selectedPhotos.isEmpty()) {
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    if (index >= 0) {
                        if (index < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                            PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                            photoEntry.editedInfo = videoEditedInfo;
                            PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                        }
                    }
                    return;
                }
                ArrayList<SearchImage> array;
                if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                    array = PhotoPickerActivity.this.recentImages;
                } else {
                    array = PhotoPickerActivity.this.searchResult;
                }
                if (index >= 0) {
                    if (index < array.size()) {
                        PhotoPickerActivity.this.addToSelectedPhotos(array.get(index), -1);
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

            public void onClick(View v) {
                int index = ((Integer) ((View) v.getParent()).getTag()).intValue();
                int num = -1;
                boolean added;
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                    added = PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)) ^ true;
                    if (PhotoPickerActivity.this.allowIndices && added) {
                        num = PhotoPickerActivity.this.selectedPhotosOrder.size();
                    }
                    ((PhotoPickerPhotoCell) v.getParent()).setChecked(num, added, true);
                    PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, index);
                } else {
                    SearchImage photoEntry2;
                    AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                    if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                        photoEntry2 = (SearchImage) PhotoPickerActivity.this.recentImages.get(((Integer) ((View) v.getParent()).getTag()).intValue());
                    } else {
                        photoEntry2 = (SearchImage) PhotoPickerActivity.this.searchResult.get(((Integer) ((View) v.getParent()).getTag()).intValue());
                    }
                    added = PhotoPickerActivity.this.selectedPhotos.containsKey(photoEntry2.id) ^ true;
                    if (PhotoPickerActivity.this.allowIndices && added) {
                        num = PhotoPickerActivity.this.selectedPhotosOrder.size();
                    }
                    ((PhotoPickerPhotoCell) v.getParent()).setChecked(num, added, true);
                    PhotoPickerActivity.this.addToSelectedPhotos(photoEntry2, index);
                }
                PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size(), true);
                PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            boolean z = true;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return true;
            }
            int position = holder.getAdapterPosition();
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                if (position >= PhotoPickerActivity.this.recentImages.size()) {
                    z = false;
                }
                return z;
            }
            if (position >= PhotoPickerActivity.this.searchResult.size()) {
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

        public long getItemId(int i) {
            return (long) i;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            View frameLayout;
            if (viewType != 0) {
                frameLayout = new FrameLayout(this.mContext);
                view = frameLayout;
                RadialProgressView progressBar = new RadialProgressView(this.mContext);
                progressBar.setProgressColor(-1);
                frameLayout.addView(progressBar, LayoutHelper.createFrame(-1, -1.0f));
            } else {
                frameLayout = new PhotoPickerPhotoCell(this.mContext, true);
                frameLayout.checkFrame.setOnClickListener(new C15881());
                frameLayout.checkFrame.setVisibility(PhotoPickerActivity.this.singlePhoto ? 8 : 0);
                view = frameLayout;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            ListAdapter listAdapter = this;
            ViewHolder viewHolder = holder;
            int i = position;
            switch (holder.getItemViewType()) {
                case 0:
                    boolean showing;
                    PhotoPickerPhotoCell cell = viewHolder.itemView;
                    cell.itemWidth = PhotoPickerActivity.this.itemWidth;
                    BackupImageView imageView = cell.photoImage;
                    imageView.setTag(Integer.valueOf(position));
                    cell.setTag(Integer.valueOf(position));
                    int i2 = 0;
                    imageView.setOrientation(0, true);
                    int i3 = -1;
                    if (PhotoPickerActivity.this.selectedAlbum != null) {
                        PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                        if (photoEntry.thumbPath != null) {
                            imageView.setImage(photoEntry.thumbPath, null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                        } else if (photoEntry.path != null) {
                            imageView.setOrientation(photoEntry.orientation, true);
                            if (photoEntry.isVideo) {
                                cell.videoInfoContainer.setVisibility(0);
                                int seconds = photoEntry.duration - ((photoEntry.duration / 60) * 60);
                                cell.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)}));
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("vthumb://");
                                stringBuilder.append(photoEntry.imageId);
                                stringBuilder.append(":");
                                stringBuilder.append(photoEntry.path);
                                imageView.setImage(stringBuilder.toString(), null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                            } else {
                                cell.videoInfoContainer.setVisibility(4);
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("thumb://");
                                stringBuilder2.append(photoEntry.imageId);
                                stringBuilder2.append(":");
                                stringBuilder2.append(photoEntry.path);
                                imageView.setImage(stringBuilder2.toString(), null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                            }
                        } else {
                            imageView.setImageResource(R.drawable.nophotos);
                        }
                        if (PhotoPickerActivity.this.allowIndices) {
                            i3 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        }
                        cell.setChecked(i3, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                        showing = PhotoViewer.isShowingImage(photoEntry.path);
                    } else {
                        SearchImage photoEntry2;
                        if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                            photoEntry2 = (SearchImage) PhotoPickerActivity.this.recentImages.get(i);
                        } else {
                            photoEntry2 = (SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                        }
                        if (photoEntry2.thumbPath != null) {
                            imageView.setImage(photoEntry2.thumbPath, null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                        } else if (photoEntry2.thumbUrl != null && photoEntry2.thumbUrl.length() > 0) {
                            imageView.setImage(photoEntry2.thumbUrl, null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                        } else if (photoEntry2.document == null || photoEntry2.document.thumb == null) {
                            imageView.setImageResource(R.drawable.nophotos);
                        } else {
                            imageView.setImage(photoEntry2.document.thumb.location, null, listAdapter.mContext.getResources().getDrawable(R.drawable.nophotos));
                        }
                        cell.videoInfoContainer.setVisibility(4);
                        if (PhotoPickerActivity.this.allowIndices) {
                            i3 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(photoEntry2.id);
                        }
                        cell.setChecked(i3, PhotoPickerActivity.this.selectedPhotos.containsKey(photoEntry2.id), false);
                        if (photoEntry2.document != null) {
                            showing = PhotoViewer.isShowingImage(FileLoader.getPathToAttach(photoEntry2.document, true).getAbsolutePath());
                        } else {
                            showing = PhotoViewer.isShowingImage(photoEntry2.imageUrl);
                        }
                    }
                    imageView.getImageReceiver().setVisible(!showing, true);
                    CheckBox checkBox = cell.checkBox;
                    if (!PhotoPickerActivity.this.singlePhoto) {
                        if (!showing) {
                            checkBox.setVisibility(i2);
                            return;
                        }
                    }
                    i2 = 8;
                    checkBox.setVisibility(i2);
                    return;
                case 1:
                    LayoutParams params = viewHolder.itemView.getLayoutParams();
                    if (params != null) {
                        params.width = PhotoPickerActivity.this.itemWidth;
                        params.height = PhotoPickerActivity.this.itemWidth;
                        viewHolder.itemView.setLayoutParams(params);
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

    public PhotoPickerActivity(int type, AlbumEntry selectedAlbum, HashMap<Object, Object> selectedPhotos, ArrayList<Object> selectedPhotosOrder, ArrayList<SearchImage> recentImages, boolean onlyOnePhoto, boolean allowCaption, ChatActivity chatActivity) {
        this.selectedAlbum = selectedAlbum;
        this.selectedPhotos = selectedPhotos;
        this.selectedPhotosOrder = selectedPhotosOrder;
        this.type = type;
        this.recentImages = recentImages;
        this.singlePhoto = onlyOnePhoto;
        this.chatActivity = chatActivity;
        this.allowCaption = allowCaption;
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (this.selectedAlbum != null) {
            r0.actionBar.setTitle(r0.selectedAlbum.bucketName);
        } else if (r0.type == 0) {
            r0.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
        } else if (r0.type == 1) {
            r0.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
        }
        r0.actionBar.setActionBarMenuOnItemClick(new C22272());
        if (r0.selectedAlbum == null) {
            r0.searchItem = r0.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C22283());
        }
        if (r0.selectedAlbum == null) {
            if (r0.type == 0) {
                r0.searchItem.getSearchField().setHint(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
            } else if (r0.type == 1) {
                r0.searchItem.getSearchField().setHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
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
            r0.emptyView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
        } else if (r0.type == 0) {
            r0.emptyView.setText(LocaleController.getString("NoRecentPhotos", R.string.NoRecentPhotos));
        } else if (r0.type == 1) {
            r0.emptyView.setText(LocaleController.getString("NoRecentGIFs", R.string.NoRecentGIFs));
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
            r0.imageOrderToggleButton.setImageResource(R.drawable.photos_group);
            r0.pickerBottomLayout.addView(r0.imageOrderToggleButton, LayoutHelper.createFrame(48, -1, 17));
            r0.imageOrderToggleButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
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
        if (r0.selectedAlbum == null) {
            if (r0.type != 0) {
                r0.allowIndices = z;
                r0.listView.setEmptyView(r0.emptyView);
                r0.pickerBottomLayout.updateSelectedCount(r0.selectedPhotos.size(), true);
                return r0.fragmentView;
            }
        }
        z = true;
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

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.recentImagesDidLoaded && this.selectedAlbum == null && this.type == ((Integer) args[0]).intValue()) {
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

    private void showHint(boolean hide, boolean enabled) {
        if (!(getParentActivity() == null || this.fragmentView == null)) {
            if (!hide || this.hintTextView != null) {
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
                String str;
                int i;
                TextView textView = this.hintTextView;
                if (enabled) {
                    str = "GroupPhotosHelp";
                    i = R.string.GroupPhotosHelp;
                } else {
                    str = "SinglePhotosHelp";
                    i = R.string.SinglePhotosHelp;
                }
                textView.setText(LocaleController.getString(str, i));
                if (this.hintHideRunnable != null) {
                    if (this.hintAnimation != null) {
                        this.hintAnimation.cancel();
                        this.hintAnimation = null;
                    } else {
                        AndroidUtilities.cancelRunOnUIThread(this.hintHideRunnable);
                        Runnable anonymousClass13 = new Runnable() {
                            public void run() {
                                PhotoPickerActivity.this.hideHint();
                            }
                        };
                        this.hintHideRunnable = anonymousClass13;
                        AndroidUtilities.runOnUIThread(anonymousClass13, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
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

                    /* renamed from: org.telegram.ui.PhotoPickerActivity$14$1 */
                    class C15841 implements Runnable {
                        C15841() {
                        }

                        public void run() {
                            PhotoPickerActivity.this.hideHint();
                        }
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(PhotoPickerActivity.this.hintAnimation)) {
                            PhotoPickerActivity.this.hintAnimation = null;
                            AndroidUtilities.runOnUIThread(PhotoPickerActivity.this.hintHideRunnable = new C15841(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        }
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
                    int i = -1;
                    if (this.selectedAlbum != null) {
                        PhotoEntry photoEntry = (PhotoEntry) this.selectedAlbum.photos.get(index.intValue());
                        if (this.allowIndices) {
                            i = this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        }
                        cell.setNum(i);
                    } else {
                        SearchImage photoEntry2;
                        if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                            photoEntry2 = (SearchImage) this.recentImages.get(index.intValue());
                        } else {
                            photoEntry2 = (SearchImage) this.searchResult.get(index.intValue());
                        }
                        if (this.allowIndices) {
                            i = this.selectedPhotosOrder.indexOf(photoEntry2.id);
                        }
                        cell.setNum(i);
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
                    }
                } else if (num < 0) {
                    continue;
                } else if (num >= this.selectedAlbum.photos.size()) {
                }
                if (num == index) {
                    return cell;
                }
            }
        }
        return null;
    }

    private int addToSelectedPhotos(Object object, int index) {
        Object key = null;
        if (object instanceof PhotoEntry) {
            key = Integer.valueOf(((PhotoEntry) object).imageId);
        } else if (object instanceof SearchImage) {
            key = ((SearchImage) object).id;
        }
        if (key == null) {
            return -1;
        }
        if (this.selectedPhotos.containsKey(key)) {
            this.selectedPhotos.remove(key);
            int position = this.selectedPhotosOrder.indexOf(key);
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
            return position;
        }
        this.selectedPhotos.put(key, object);
        this.selectedPhotosOrder.add(key);
        return -1;
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

    private void searchGiphyImages(final String query, int offset) {
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
        TL_messages_searchGifs req = new TL_messages_searchGifs();
        req.f50q = query;
        req.offset = offset;
        int i = this.lastSearchToken + 1;
        this.lastSearchToken = i;
        final int token = i;
        this.giphyReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (token == PhotoPickerActivity.this.lastSearchToken) {
                            int addedCount = 0;
                            if (response != null) {
                                boolean added = false;
                                TL_messages_foundGifs res = response;
                                PhotoPickerActivity.this.nextGiphySearchOffset = res.next_offset;
                                int addedCount2 = 0;
                                for (addedCount = 0; addedCount < res.results.size(); addedCount++) {
                                    FoundGif gif = (FoundGif) res.results.get(addedCount);
                                    if (!PhotoPickerActivity.this.searchResultKeys.containsKey(gif.url)) {
                                        added = true;
                                        SearchImage bingImage = new SearchImage();
                                        bingImage.id = gif.url;
                                        if (gif.document != null) {
                                            int b = 0;
                                            while (b < gif.document.attributes.size()) {
                                                DocumentAttribute attribute = (DocumentAttribute) gif.document.attributes.get(b);
                                                if (!(attribute instanceof TL_documentAttributeImageSize)) {
                                                    if (!(attribute instanceof TL_documentAttributeVideo)) {
                                                        b++;
                                                    }
                                                }
                                                bingImage.width = attribute.f36w;
                                                bingImage.height = attribute.f35h;
                                            }
                                        } else {
                                            bingImage.width = gif.f38w;
                                            bingImage.height = gif.f37h;
                                        }
                                        bingImage.size = 0;
                                        bingImage.imageUrl = gif.content_url;
                                        bingImage.thumbUrl = gif.thumb_url;
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(gif.url);
                                        stringBuilder.append("|");
                                        stringBuilder.append(query);
                                        bingImage.localUrl = stringBuilder.toString();
                                        bingImage.document = gif.document;
                                        if (!(gif.photo == null || gif.document == null)) {
                                            PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(gif.photo.sizes, PhotoPickerActivity.this.itemWidth, true);
                                            if (size != null) {
                                                gif.document.thumb = size;
                                            }
                                        }
                                        bingImage.type = 1;
                                        PhotoPickerActivity.this.searchResult.add(bingImage);
                                        addedCount2++;
                                        PhotoPickerActivity.this.searchResultKeys.put(bingImage.id, bingImage);
                                    }
                                }
                                PhotoPickerActivity.this.giphySearchEndReached = !added;
                                addedCount = addedCount2;
                            }
                            PhotoPickerActivity.this.searching = false;
                            if (addedCount != 0) {
                                PhotoPickerActivity.this.listAdapter.notifyItemRangeInserted(PhotoPickerActivity.this.searchResult.size(), addedCount);
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

    private void searchBingImages(String query, int offset, int count) {
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
            boolean adult;
            String url;
            String str;
            Object[] objArr;
            this.searching = true;
            String phone = UserConfig.getInstance(this.currentAccount).getCurrentUser().phone;
            if (!(phone.startsWith("44") || phone.startsWith("49") || phone.startsWith("43") || phone.startsWith("31"))) {
                if (!phone.startsWith("1")) {
                    adult = false;
                    url = Locale.US;
                    str = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q='%s'&offset=%d&count=%d&$format=json&safeSearch=%s";
                    objArr = new Object[4];
                    objArr[0] = URLEncoder.encode(query, C0542C.UTF8_NAME);
                    objArr[1] = Integer.valueOf(offset);
                    objArr[2] = Integer.valueOf(count);
                    objArr[3] = adult ? "Strict" : "Off";
                    url = String.format(url, str, objArr);
                    this.currentBingTask = new AsyncTask<Void, Void, JSONObject>() {
                        private boolean canRetry = true;

                        private String downloadUrlContent(String url) {
                            boolean canRetry = true;
                            InputStream httpConnectionStream = null;
                            boolean done = false;
                            StringBuilder result = null;
                            URLConnection httpConnection = null;
                            try {
                                httpConnection = new URL(url).openConnection();
                                httpConnection.addRequestProperty("Ocp-Apim-Subscription-Key", BuildVars.BING_SEARCH_KEY);
                                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
                                httpConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                                httpConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                                httpConnection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                                httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                                httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                                if (httpConnection instanceof HttpURLConnection) {
                                    HttpURLConnection httpURLConnection = (HttpURLConnection) httpConnection;
                                    httpURLConnection.setInstanceFollowRedirects(true);
                                    int status = httpURLConnection.getResponseCode();
                                    if (status == 302 || status == 301 || status == 303) {
                                        String newUrl = httpURLConnection.getHeaderField("Location");
                                        String cookies = httpURLConnection.getHeaderField("Set-Cookie");
                                        httpConnection = new URL(newUrl).openConnection();
                                        httpConnection.setRequestProperty("Cookie", cookies);
                                        httpConnection.addRequestProperty("Ocp-Apim-Subscription-Key", BuildVars.BING_SEARCH_KEY);
                                        httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
                                        httpConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                                        httpConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                                        httpConnection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                                    }
                                }
                                httpConnection.connect();
                                httpConnectionStream = httpConnection.getInputStream();
                            } catch (Throwable e) {
                                if (e instanceof SocketTimeoutException) {
                                    if (ConnectionsManager.isNetworkOnline()) {
                                        canRetry = false;
                                    }
                                } else if (e instanceof UnknownHostException) {
                                    canRetry = false;
                                } else if (e instanceof SocketException) {
                                    if (e.getMessage() != null && e.getMessage().contains("ECONNRESET")) {
                                        canRetry = false;
                                    }
                                } else if (e instanceof FileNotFoundException) {
                                    canRetry = false;
                                }
                                FileLog.m3e(e);
                            }
                            if (canRetry) {
                                if (httpConnection != null) {
                                    try {
                                        if (httpConnection instanceof HttpURLConnection) {
                                            int code = ((HttpURLConnection) httpConnection).getResponseCode();
                                            if (!(code == Callback.DEFAULT_DRAG_ANIMATION_DURATION || code == 202)) {
                                            }
                                        }
                                    } catch (Throwable e2) {
                                        FileLog.m3e(e2);
                                    }
                                }
                                if (httpConnectionStream != null) {
                                    try {
                                        byte[] data = new byte[32768];
                                        while (!isCancelled()) {
                                            int read = httpConnectionStream.read(data);
                                            if (read > 0) {
                                                if (result == null) {
                                                    result = new StringBuilder();
                                                }
                                                result.append(new String(data, 0, read, C0542C.UTF8_NAME));
                                            } else if (read == -1) {
                                                done = true;
                                            }
                                        }
                                    } catch (Throwable e3) {
                                        FileLog.m3e(e3);
                                    } catch (Throwable e22) {
                                        FileLog.m3e(e22);
                                    }
                                }
                                if (httpConnectionStream != null) {
                                    try {
                                        httpConnectionStream.close();
                                    } catch (Throwable e222) {
                                        FileLog.m3e(e222);
                                    }
                                }
                            }
                            if (done) {
                                return result.toString();
                            }
                            return null;
                        }

                        protected JSONObject doInBackground(Void... voids) {
                            String code = downloadUrlContent(url);
                            if (isCancelled()) {
                                return null;
                            }
                            try {
                                return new JSONObject(code);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                                return null;
                            }
                        }

                        protected void onPostExecute(JSONObject response) {
                            Throwable e;
                            int addedCount = 0;
                            if (response != null) {
                                int addedCount2;
                                try {
                                    JSONArray result = response.getJSONArray("value");
                                    boolean added = false;
                                    addedCount2 = 0;
                                    for (addedCount = 0; addedCount < result.length(); addedCount++) {
                                        try {
                                            JSONObject object = result.getJSONObject(addedCount);
                                            String id = Utilities.MD5(object.getString("contentUrl"));
                                            if (!PhotoPickerActivity.this.searchResultKeys.containsKey(id)) {
                                                SearchImage bingImage = new SearchImage();
                                                bingImage.id = id;
                                                bingImage.width = object.getInt("width");
                                                bingImage.height = object.getInt("height");
                                                bingImage.size = Utilities.parseInt(object.getString("contentSize")).intValue();
                                                bingImage.imageUrl = object.getString("contentUrl");
                                                bingImage.thumbUrl = object.getString("thumbnailUrl");
                                                PhotoPickerActivity.this.searchResult.add(bingImage);
                                                PhotoPickerActivity.this.searchResultKeys.put(id, bingImage);
                                                addedCount2++;
                                                added = true;
                                            }
                                        } catch (Throwable e2) {
                                            try {
                                                FileLog.m3e(e2);
                                            } catch (Exception e3) {
                                                e = e3;
                                            }
                                        }
                                    }
                                    PhotoPickerActivity.this.bingSearchEndReached = !added;
                                } catch (Throwable e4) {
                                    addedCount2 = 0;
                                    e = e4;
                                    FileLog.m3e(e);
                                    addedCount = addedCount2;
                                    PhotoPickerActivity.this.searching = false;
                                    if (addedCount != 0) {
                                        PhotoPickerActivity.this.listAdapter.notifyItemRangeInserted(PhotoPickerActivity.this.searchResult.size(), addedCount);
                                    } else if (PhotoPickerActivity.this.giphySearchEndReached) {
                                        PhotoPickerActivity.this.listAdapter.notifyItemRemoved(PhotoPickerActivity.this.searchResult.size() - 1);
                                    }
                                    if (PhotoPickerActivity.this.searching) {
                                    }
                                    PhotoPickerActivity.this.emptyView.showTextView();
                                }
                                addedCount = addedCount2;
                                PhotoPickerActivity.this.searching = false;
                            } else {
                                PhotoPickerActivity.this.bingSearchEndReached = true;
                                PhotoPickerActivity.this.searching = false;
                            }
                            if (addedCount != 0) {
                                PhotoPickerActivity.this.listAdapter.notifyItemRangeInserted(PhotoPickerActivity.this.searchResult.size(), addedCount);
                            } else if (PhotoPickerActivity.this.giphySearchEndReached) {
                                PhotoPickerActivity.this.listAdapter.notifyItemRemoved(PhotoPickerActivity.this.searchResult.size() - 1);
                            }
                            if ((PhotoPickerActivity.this.searching || !PhotoPickerActivity.this.searchResult.isEmpty()) && !(PhotoPickerActivity.this.loadingRecent && PhotoPickerActivity.this.lastSearchString == null)) {
                                PhotoPickerActivity.this.emptyView.showTextView();
                            } else {
                                PhotoPickerActivity.this.emptyView.showProgress();
                            }
                        }
                    };
                    this.currentBingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                }
            }
            adult = true;
            url = Locale.US;
            str = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q='%s'&offset=%d&count=%d&$format=json&safeSearch=%s";
            objArr = new Object[4];
            objArr[0] = URLEncoder.encode(query, C0542C.UTF8_NAME);
            objArr[1] = Integer.valueOf(offset);
            objArr[2] = Integer.valueOf(count);
            if (adult) {
            }
            objArr[3] = adult ? "Strict" : "Off";
            url = String.format(url, str, objArr);
            this.currentBingTask = /* anonymous class already generated */;
            this.currentBingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
        } catch (Throwable e) {
            FileLog.m3e(e);
            this.bingSearchEndReached = true;
            this.searching = false;
            this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
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
            int columnsCount;
            int position = this.layoutManager.findFirstVisibleItemPosition();
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            int columnsCount2 = 3;
            if (AndroidUtilities.isTablet()) {
                columnsCount2 = 3;
            } else {
                if (rotation != 3) {
                    if (rotation != 1) {
                        columnsCount = columnsCount2;
                        this.layoutManager.setSpanCount(columnsCount);
                        if (AndroidUtilities.isTablet()) {
                            this.itemWidth = (AndroidUtilities.displaySize.x - ((columnsCount + 1) * AndroidUtilities.dp(4.0f))) / columnsCount;
                        } else {
                            this.itemWidth = (AndroidUtilities.dp(490.0f) - ((columnsCount + 1) * AndroidUtilities.dp(4.0f))) / columnsCount;
                        }
                        this.listAdapter.notifyDataSetChanged();
                        this.layoutManager.scrollToPosition(position);
                        if (this.selectedAlbum == null) {
                            this.emptyView.setPadding(0, 0, 0, (int) (((float) (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight())) * 0.4f));
                        }
                    }
                }
                columnsCount2 = 5;
            }
            columnsCount = columnsCount2;
            this.layoutManager.setSpanCount(columnsCount);
            if (AndroidUtilities.isTablet()) {
                this.itemWidth = (AndroidUtilities.displaySize.x - ((columnsCount + 1) * AndroidUtilities.dp(4.0f))) / columnsCount;
            } else {
                this.itemWidth = (AndroidUtilities.dp(490.0f) - ((columnsCount + 1) * AndroidUtilities.dp(4.0f))) / columnsCount;
            }
            this.listAdapter.notifyDataSetChanged();
            this.layoutManager.scrollToPosition(position);
            if (this.selectedAlbum == null) {
                this.emptyView.setPadding(0, 0, 0, (int) (((float) (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight())) * 0.4f));
            }
        }
    }
}
