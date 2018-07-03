package org.telegram.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate;
import org.telegram.ui.Cells.PhotoPickerSearchCell;
import org.telegram.ui.Cells.PhotoPickerSearchCell.PhotoPickerSearchCellDelegate;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate;

public class PhotoAlbumPickerActivity extends BaseFragment implements NotificationCenterDelegate {
    private ArrayList<AlbumEntry> albumsSorted = null;
    private boolean allowCaption;
    private boolean allowGifs;
    private boolean allowSearchImages = true;
    private ChatActivity chatActivity;
    private int columnsCount = 2;
    private PhotoAlbumPickerActivityDelegate delegate;
    private TextView emptyView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading = false;
    private int maxSelectedPhotos = 100;
    private PickerBottomLayout pickerBottomLayout;
    private FrameLayout progressView;
    private ArrayList<SearchImage> recentGifImages = new ArrayList();
    private HashMap<String, SearchImage> recentImagesGifKeys = new HashMap();
    private HashMap<String, SearchImage> recentImagesWebKeys = new HashMap();
    private ArrayList<SearchImage> recentWebImages = new ArrayList();
    private HashMap<Object, Object> selectedPhotos = new HashMap();
    private ArrayList<Object> selectedPhotosOrder = new ArrayList();
    private boolean sendPressed;
    private boolean singlePhoto;

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$2 */
    class C17062 implements OnTouchListener {
        C17062() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$3 */
    class C17073 implements OnClickListener {
        C17073() {
        }

        public void onClick(View view) {
            PhotoAlbumPickerActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$4 */
    class C17084 implements OnClickListener {
        C17084() {
        }

        public void onClick(View view) {
            PhotoAlbumPickerActivity.this.sendSelectedPhotos(PhotoAlbumPickerActivity.this.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder);
            PhotoAlbumPickerActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$5 */
    class C17095 implements OnPreDrawListener {
        C17095() {
        }

        public boolean onPreDraw() {
            PhotoAlbumPickerActivity.this.fixLayoutInternal();
            if (PhotoAlbumPickerActivity.this.listView != null) {
                PhotoAlbumPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            return true;
        }
    }

    public interface PhotoAlbumPickerActivityDelegate {
        void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList);

        void startPhotoSelectActivity();
    }

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$1 */
    class C24091 extends ActionBarMenuOnItemClick {
        C24091() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                PhotoAlbumPickerActivity.this.finishFragment();
            } else if (id == 1 && PhotoAlbumPickerActivity.this.delegate != null) {
                PhotoAlbumPickerActivity.this.finishFragment(false);
                PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$6 */
    class C24106 implements PhotoPickerActivityDelegate {
        C24106() {
        }

        public void selectedPhotosChanged() {
            if (PhotoAlbumPickerActivity.this.pickerBottomLayout != null) {
                PhotoAlbumPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoAlbumPickerActivity.this.selectedPhotos.size(), true);
            }
        }

        public void actionButtonPressed(boolean canceled) {
            PhotoAlbumPickerActivity.this.removeSelfFromStack();
            if (!canceled) {
                PhotoAlbumPickerActivity.this.sendSelectedPhotos(PhotoAlbumPickerActivity.this.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder);
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$ListAdapter$1 */
        class C24121 implements PhotoPickerAlbumsCellDelegate {
            C24121() {
            }

            public void didSelectAlbum(AlbumEntry albumEntry) {
                PhotoAlbumPickerActivity.this.openPhotoPicker(albumEntry, 0);
            }
        }

        /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$ListAdapter$2 */
        class C24132 implements PhotoPickerSearchCellDelegate {
            C24132() {
            }

            public void didPressedSearchButton(int index) {
                PhotoAlbumPickerActivity.this.openPhotoPicker(null, index);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            int i = 0;
            if (!PhotoAlbumPickerActivity.this.singlePhoto && PhotoAlbumPickerActivity.this.allowSearchImages) {
                if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                    i = (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount)));
                }
                return i + 1;
            } else if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                return (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount)));
            } else {
                return 0;
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            View cell;
            switch (viewType) {
                case 0:
                    cell = new PhotoPickerAlbumsCell(this.mContext);
                    cell.setDelegate(new C24121());
                    view = cell;
                    break;
                default:
                    cell = new PhotoPickerSearchCell(this.mContext, PhotoAlbumPickerActivity.this.allowGifs);
                    cell.setDelegate(new C24132());
                    view = cell;
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                PhotoPickerAlbumsCell photoPickerAlbumsCell = holder.itemView;
                photoPickerAlbumsCell.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
                for (int a = 0; a < PhotoAlbumPickerActivity.this.columnsCount; a++) {
                    int index;
                    if (PhotoAlbumPickerActivity.this.singlePhoto || !PhotoAlbumPickerActivity.this.allowSearchImages) {
                        index = (PhotoAlbumPickerActivity.this.columnsCount * position) + a;
                    } else {
                        index = ((position - 1) * PhotoAlbumPickerActivity.this.columnsCount) + a;
                    }
                    if (index < PhotoAlbumPickerActivity.this.albumsSorted.size()) {
                        photoPickerAlbumsCell.setAlbum(a, (AlbumEntry) PhotoAlbumPickerActivity.this.albumsSorted.get(index));
                    } else {
                        photoPickerAlbumsCell.setAlbum(a, null);
                    }
                }
                photoPickerAlbumsCell.requestLayout();
            }
        }

        public int getItemViewType(int i) {
            if (!PhotoAlbumPickerActivity.this.singlePhoto && PhotoAlbumPickerActivity.this.allowSearchImages && i == 0) {
                return 1;
            }
            return 0;
        }
    }

    public PhotoAlbumPickerActivity(boolean singlePhoto, boolean allowGifs, boolean allowCaption, ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        this.singlePhoto = singlePhoto;
        this.allowGifs = allowGifs;
        this.allowCaption = allowCaption;
    }

    public boolean onFragmentCreate() {
        this.loading = true;
        MediaController.loadGalleryPhotosAlbums(this.classGuid);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.albumsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.albumsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentImagesDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new C24091());
        this.actionBar.createMenu().addItem(1, (int) R.drawable.ic_ab_other);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        this.actionBar.setTitle(LocaleController.getString("Gallery", R.string.Gallery));
        this.listView = new RecyclerListView(context);
        this.listView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setDrawingCacheEnabled(false);
        frameLayout.addView(this.listView);
        LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        this.listView.setLayoutParams(layoutParams);
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setGlowColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.emptyView = new TextView(context);
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setTextSize(20.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.emptyView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
        frameLayout.addView(this.emptyView);
        layoutParams = (LayoutParams) this.emptyView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        this.emptyView.setLayoutParams(layoutParams);
        this.emptyView.setOnTouchListener(new C17062());
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(8);
        frameLayout.addView(this.progressView);
        layoutParams = (LayoutParams) this.progressView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        this.progressView.setLayoutParams(layoutParams);
        this.progressView.addView(new RadialProgressView(context));
        layoutParams = (LayoutParams) this.progressView.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.gravity = 17;
        this.progressView.setLayoutParams(layoutParams);
        this.pickerBottomLayout = new PickerBottomLayout(context);
        frameLayout.addView(this.pickerBottomLayout);
        layoutParams = (LayoutParams) this.pickerBottomLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        layoutParams.gravity = 80;
        this.pickerBottomLayout.setLayoutParams(layoutParams);
        this.pickerBottomLayout.cancelButton.setOnClickListener(new C17073());
        this.pickerBottomLayout.doneButton.setOnClickListener(new C17084());
        if (!this.loading || (this.albumsSorted != null && (this.albumsSorted == null || !this.albumsSorted.isEmpty()))) {
            this.progressView.setVisibility(8);
            this.listView.setEmptyView(this.emptyView);
        } else {
            this.progressView.setVisibility(0);
            this.listView.setEmptyView(null);
        }
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size(), true);
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        fixLayout();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.albumsDidLoaded) {
            if (this.classGuid == ((Integer) args[0]).intValue()) {
                if (this.singlePhoto || !this.allowSearchImages) {
                    this.albumsSorted = (ArrayList) args[2];
                } else {
                    this.albumsSorted = (ArrayList) args[1];
                }
                if (this.progressView != null) {
                    this.progressView.setVisibility(8);
                }
                if (this.listView != null && this.listView.getEmptyView() == null) {
                    this.listView.setEmptyView(this.emptyView);
                }
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
                this.loading = false;
            }
        } else if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.recentImagesDidLoaded) {
            int type = ((Integer) args[0]).intValue();
            Iterator it;
            SearchImage searchImage;
            if (type == 0) {
                this.recentWebImages = (ArrayList) args[1];
                this.recentImagesWebKeys.clear();
                it = this.recentWebImages.iterator();
                while (it.hasNext()) {
                    searchImage = (SearchImage) it.next();
                    this.recentImagesWebKeys.put(searchImage.id, searchImage);
                }
            } else if (type == 1) {
                this.recentGifImages = (ArrayList) args[1];
                this.recentImagesGifKeys.clear();
                it = this.recentGifImages.iterator();
                while (it.hasNext()) {
                    searchImage = (SearchImage) it.next();
                    this.recentImagesGifKeys.put(searchImage.id, searchImage);
                }
            }
        }
    }

    public void setMaxSelectedPhotos(int value) {
        this.maxSelectedPhotos = value;
    }

    public void setAllowSearchImages(boolean value) {
        this.allowSearchImages = value;
    }

    public void setDelegate(PhotoAlbumPickerActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private void sendSelectedPhotos(HashMap<Object, Object> photos, ArrayList<Object> order) {
        if (!photos.isEmpty() && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            boolean gifChanged = false;
            boolean webChange = false;
            ArrayList<SendingMediaInfo> media = new ArrayList();
            for (int a = 0; a < order.size(); a++) {
                PhotoEntry object = photos.get(order.get(a));
                SendingMediaInfo info = new SendingMediaInfo();
                media.add(info);
                if (object instanceof PhotoEntry) {
                    PhotoEntry photoEntry = object;
                    if (photoEntry.isVideo) {
                        info.path = photoEntry.path;
                        info.videoEditedInfo = photoEntry.editedInfo;
                    } else if (photoEntry.imagePath != null) {
                        info.path = photoEntry.imagePath;
                    } else if (photoEntry.path != null) {
                        info.path = photoEntry.path;
                    }
                    info.isVideo = photoEntry.isVideo;
                    info.caption = photoEntry.caption != null ? photoEntry.caption.toString() : null;
                    info.entities = photoEntry.entities;
                    info.masks = !photoEntry.stickers.isEmpty() ? new ArrayList(photoEntry.stickers) : null;
                    info.ttl = photoEntry.ttl;
                } else if (object instanceof SearchImage) {
                    SearchImage searchImage = (SearchImage) object;
                    if (searchImage.imagePath != null) {
                        info.path = searchImage.imagePath;
                    } else {
                        info.searchImage = searchImage;
                    }
                    info.caption = searchImage.caption != null ? searchImage.caption.toString() : null;
                    info.entities = searchImage.entities;
                    info.masks = !searchImage.stickers.isEmpty() ? new ArrayList(searchImage.stickers) : null;
                    info.ttl = searchImage.ttl;
                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                    SearchImage recentImage;
                    if (searchImage.type == 0) {
                        webChange = true;
                        recentImage = (SearchImage) this.recentImagesWebKeys.get(searchImage.id);
                        if (recentImage != null) {
                            this.recentWebImages.remove(recentImage);
                            this.recentWebImages.add(0, recentImage);
                        } else {
                            this.recentWebImages.add(0, searchImage);
                        }
                    } else if (searchImage.type == 1) {
                        gifChanged = true;
                        recentImage = (SearchImage) this.recentImagesGifKeys.get(searchImage.id);
                        if (recentImage != null) {
                            this.recentGifImages.remove(recentImage);
                            this.recentGifImages.add(0, recentImage);
                        } else {
                            this.recentGifImages.add(0, searchImage);
                        }
                    }
                }
            }
            if (webChange) {
                MessagesStorage.getInstance(this.currentAccount).putWebRecent(this.recentWebImages);
            }
            if (gifChanged) {
                MessagesStorage.getInstance(this.currentAccount).putWebRecent(this.recentGifImages);
            }
            this.delegate.didSelectPhotos(media);
        }
    }

    private void fixLayout() {
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C17095());
        }
    }

    private void fixLayoutInternal() {
        if (getParentActivity() != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.columnsCount = 2;
            if (!AndroidUtilities.isTablet() && (rotation == 3 || rotation == 1)) {
                this.columnsCount = 4;
            }
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void openPhotoPicker(AlbumEntry albumEntry, int type) {
        PhotoPickerActivity fragment;
        ArrayList<SearchImage> recentImages = null;
        if (albumEntry == null) {
            if (type == 0) {
                recentImages = this.recentWebImages;
            } else if (type == 1) {
                recentImages = this.recentGifImages;
            }
        }
        if (albumEntry != null) {
            fragment = new PhotoPickerActivity(type, albumEntry, this.selectedPhotos, this.selectedPhotosOrder, recentImages, this.singlePhoto, this.allowCaption, this.chatActivity);
            fragment.setDelegate(new C24106());
        } else {
            final HashMap<Object, Object> photos = new HashMap();
            final ArrayList<Object> order = new ArrayList();
            fragment = new PhotoPickerActivity(type, albumEntry, photos, order, recentImages, this.singlePhoto, this.allowCaption, this.chatActivity);
            fragment.setDelegate(new PhotoPickerActivityDelegate() {
                public void selectedPhotosChanged() {
                }

                public void actionButtonPressed(boolean canceled) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (!canceled) {
                        PhotoAlbumPickerActivity.this.sendSelectedPhotos(photos, order);
                    }
                }
            });
        }
        fragment.setMaxSelectedPhotos(this.maxSelectedPhotos);
        presentFragment(fragment);
    }
}
