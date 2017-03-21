package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
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
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;
import org.telegram.ui.VideoEditorActivity.VideoEditorActivityDelegate;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private boolean allowCaption = true;
    private boolean bingSearchEndReached = true;
    private ChatActivity chatActivity;
    private AsyncTask<Void, Void, JSONObject> currentBingTask;
    private PhotoPickerActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private int giphyReqId;
    private boolean giphySearchEndReached = true;
    private int itemWidth = 100;
    private String lastSearchString;
    private int lastSearchToken;
    private GridLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingRecent;
    private int nextGiphySearchOffset;
    private PickerBottomLayout pickerBottomLayout;
    private ArrayList<SearchImage> recentImages;
    private ActionBarMenuItem searchItem;
    private ArrayList<SearchImage> searchResult = new ArrayList();
    private HashMap<String, SearchImage> searchResultKeys = new HashMap();
    private HashMap<String, SearchImage> searchResultUrls = new HashMap();
    private boolean searching;
    private AlbumEntry selectedAlbum;
    private HashMap<Integer, PhotoEntry> selectedPhotos;
    private HashMap<String, SearchImage> selectedWebPhotos;
    private boolean sendPressed;
    private boolean singlePhoto;
    private int type;

    public interface PhotoPickerActivityDelegate {
        void actionButtonPressed(boolean z);

        void didSelectVideo(String str, VideoEditedInfo videoEditedInfo, long j, long j2, String str2);

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
                    View cell = new PhotoPickerPhotoCell(this.mContext);
                    cell.checkFrame.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            int index = ((Integer) ((View) v.getParent()).getTag()).intValue();
                            if (PhotoPickerActivity.this.selectedAlbum != null) {
                                PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                                if (PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId))) {
                                    PhotoPickerActivity.this.selectedPhotos.remove(Integer.valueOf(photoEntry.imageId));
                                    photoEntry.imagePath = null;
                                    photoEntry.thumbPath = null;
                                    photoEntry.stickers.clear();
                                    PhotoPickerActivity.this.updatePhotoAtIndex(index);
                                } else {
                                    PhotoPickerActivity.this.selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
                                }
                                ((PhotoPickerPhotoCell) v.getParent()).setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), true);
                            } else {
                                SearchImage photoEntry2;
                                AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                                if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                                    photoEntry2 = (SearchImage) PhotoPickerActivity.this.recentImages.get(((Integer) ((View) v.getParent()).getTag()).intValue());
                                } else {
                                    photoEntry2 = (SearchImage) PhotoPickerActivity.this.searchResult.get(((Integer) ((View) v.getParent()).getTag()).intValue());
                                }
                                if (PhotoPickerActivity.this.selectedWebPhotos.containsKey(photoEntry2.id)) {
                                    PhotoPickerActivity.this.selectedWebPhotos.remove(photoEntry2.id);
                                    photoEntry2.imagePath = null;
                                    photoEntry2.thumbPath = null;
                                    PhotoPickerActivity.this.updatePhotoAtIndex(index);
                                } else {
                                    PhotoPickerActivity.this.selectedWebPhotos.put(photoEntry2.id, photoEntry2);
                                }
                                ((PhotoPickerPhotoCell) v.getParent()).setChecked(PhotoPickerActivity.this.selectedWebPhotos.containsKey(photoEntry2.id), true);
                            }
                            PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size() + PhotoPickerActivity.this.selectedWebPhotos.size(), true);
                            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
                        }
                    });
                    cell.checkFrame.setVisibility(PhotoPickerActivity.this.singlePhoto ? 8 : 0);
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

        public void onBindViewHolder(ViewHolder holder, int position) {
            int i = 0;
            switch (holder.getItemViewType()) {
                case 0:
                    boolean showing;
                    boolean z;
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
                                imageView.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                            } else {
                                imageView.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                            }
                        } else {
                            imageView.setImageResource(R.drawable.nophotos);
                        }
                        cell.setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                        showing = PhotoViewer.getInstance().isShowingImage(photoEntry.path);
                    } else {
                        SearchImage photoEntry2;
                        if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                            photoEntry2 = (SearchImage) PhotoPickerActivity.this.recentImages.get(position);
                        } else {
                            photoEntry2 = (SearchImage) PhotoPickerActivity.this.searchResult.get(position);
                        }
                        if (photoEntry2.thumbPath != null) {
                            imageView.setImage(photoEntry2.thumbPath, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                        } else if (photoEntry2.thumbUrl != null && photoEntry2.thumbUrl.length() > 0) {
                            imageView.setImage(photoEntry2.thumbUrl, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                        } else if (photoEntry2.document == null || photoEntry2.document.thumb == null) {
                            imageView.setImageResource(R.drawable.nophotos);
                        } else {
                            imageView.setImage(photoEntry2.document.thumb.location, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                        }
                        cell.setChecked(PhotoPickerActivity.this.selectedWebPhotos.containsKey(photoEntry2.id), false);
                        if (photoEntry2.document != null) {
                            showing = PhotoViewer.getInstance().isShowingImage(FileLoader.getPathToAttach(photoEntry2.document, true).getAbsolutePath());
                        } else {
                            showing = PhotoViewer.getInstance().isShowingImage(photoEntry2.imageUrl);
                        }
                    }
                    ImageReceiver imageReceiver = imageView.getImageReceiver();
                    if (showing) {
                        z = false;
                    } else {
                        z = true;
                    }
                    imageReceiver.setVisible(z, true);
                    CheckBox checkBox = cell.checkBox;
                    if (PhotoPickerActivity.this.singlePhoto || showing) {
                        i = 8;
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

    public PhotoPickerActivity(int type, AlbumEntry selectedAlbum, HashMap<Integer, PhotoEntry> selectedPhotos, HashMap<String, SearchImage> selectedWebPhotos, ArrayList<SearchImage> recentImages, boolean onlyOnePhoto, boolean allowCaption, ChatActivity chatActivity) {
        this.selectedAlbum = selectedAlbum;
        this.selectedPhotos = selectedPhotos;
        this.selectedWebPhotos = selectedWebPhotos;
        this.type = type;
        this.recentImages = recentImages;
        this.singlePhoto = onlyOnePhoto;
        this.chatActivity = chatActivity;
        this.allowCaption = allowCaption;
        if (selectedAlbum != null && selectedAlbum.isVideo) {
            this.singlePhoto = true;
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
        if (this.selectedAlbum == null && this.recentImages.isEmpty()) {
            MessagesStorage.getInstance().loadWebRecent(this.type);
            this.loadingRecent = true;
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentImagesDidLoaded);
        if (this.currentBingTask != null) {
            this.currentBingTask.cancel(true);
            this.currentBingTask = null;
        }
        if (this.giphyReqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
            this.giphyReqId = 0;
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false);
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
                    PhotoPickerActivity.this.finishFragment();
                }
            }
        });
        if (this.selectedAlbum == null) {
            this.searchItem = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
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
                            ConnectionsManager.getInstance().cancelRequest(PhotoPickerActivity.this.giphyReqId, true);
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
            });
        }
        if (this.selectedAlbum == null) {
            if (this.type == 0) {
                this.searchItem.getSearchField().setHint(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
            } else if (this.type == 1) {
                this.searchItem.getSearchField().setHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(-16777216);
        this.listView = new RecyclerListView(context);
        this.listView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager anonymousClass3 = new GridLayoutManager(context, 4) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass3;
        recyclerListView.setLayoutManager(anonymousClass3);
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
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.singlePhoto ? 0.0f : 48.0f));
        recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setGlowColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                int i = 1;
                if (PhotoPickerActivity.this.selectedAlbum == null || !PhotoPickerActivity.this.selectedAlbum.isVideo) {
                    ArrayList<Object> arrayList;
                    if (PhotoPickerActivity.this.selectedAlbum != null) {
                        arrayList = PhotoPickerActivity.this.selectedAlbum.photos;
                    } else if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                        arrayList = PhotoPickerActivity.this.recentImages;
                    } else {
                        arrayList = PhotoPickerActivity.this.searchResult;
                    }
                    if (position >= 0 && position < arrayList.size()) {
                        if (PhotoPickerActivity.this.searchItem != null) {
                            AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.searchItem.getSearchField());
                        }
                        PhotoViewer.getInstance().setParentActivity(PhotoPickerActivity.this.getParentActivity());
                        PhotoViewer instance = PhotoViewer.getInstance();
                        if (!PhotoPickerActivity.this.singlePhoto) {
                            i = 0;
                        }
                        instance.openPhotoForSelect(arrayList, position, i, PhotoPickerActivity.this, PhotoPickerActivity.this.chatActivity);
                    }
                } else if (position >= 0 && position < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                    String path = ((PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(position)).path;
                    if (VERSION.SDK_INT >= 16) {
                        Bundle args = new Bundle();
                        args.putString("videoPath", path);
                        VideoEditorActivity fragment = new VideoEditorActivity(args);
                        fragment.setDelegate(new VideoEditorActivityDelegate() {
                            public void didFinishEditVideo(String videoPath, long startTime, long endTime, int resultWidth, int resultHeight, int rotationValue, int originalWidth, int originalHeight, int bitrate, long estimatedSize, long estimatedDuration, String caption) {
                                PhotoPickerActivity.this.removeSelfFromStack();
                                VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
                                videoEditedInfo.startTime = startTime;
                                videoEditedInfo.endTime = endTime;
                                videoEditedInfo.rotationValue = rotationValue;
                                videoEditedInfo.originalWidth = originalWidth;
                                videoEditedInfo.originalHeight = originalHeight;
                                videoEditedInfo.bitrate = bitrate;
                                videoEditedInfo.resultWidth = resultWidth;
                                videoEditedInfo.resultHeight = resultHeight;
                                videoEditedInfo.originalPath = videoPath;
                                PhotoPickerActivity.this.delegate.didSelectVideo(videoPath, videoEditedInfo, estimatedSize, estimatedDuration, caption);
                            }
                        });
                        if (!fragment.onFragmentCreate()) {
                            PhotoPickerActivity.this.delegate.didSelectVideo(path, null, 0, 0, null);
                            PhotoPickerActivity.this.finishFragment();
                            return;
                        } else if (PhotoPickerActivity.this.parentLayout.presentFragment(fragment, false, false, true)) {
                            fragment.setParentChatActivity(PhotoPickerActivity.this.chatActivity);
                            return;
                        } else {
                            return;
                        }
                    }
                    PhotoPickerActivity.this.delegate.didSelectVideo(path, null, 0, 0, null);
                    PhotoPickerActivity.this.finishFragment();
                }
            }
        });
        if (this.selectedAlbum == null) {
            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemClick(View view, int position) {
                    if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                        return false;
                    }
                    Builder builder = new Builder(PhotoPickerActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PhotoPickerActivity.this.recentImages.clear();
                            if (PhotoPickerActivity.this.listAdapter != null) {
                                PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                            }
                            MessagesStorage.getInstance().clearWebRecent(PhotoPickerActivity.this.type);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    PhotoPickerActivity.this.showDialog(builder.create());
                    return true;
                }
            });
        }
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setProgressBarColor(-1);
        this.emptyView.setShowAtCenter(true);
        if (this.selectedAlbum != null) {
            this.emptyView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
        } else if (this.type == 0) {
            this.emptyView.setText(LocaleController.getString("NoRecentPhotos", R.string.NoRecentPhotos));
        } else if (this.type == 1) {
            this.emptyView.setText(LocaleController.getString("NoRecentGIFs", R.string.NoRecentGIFs));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.singlePhoto ? 0.0f : 48.0f));
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
                                PhotoPickerActivity.this.searchBingImages(PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.searchResult.size(), 54);
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
        frameLayout.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.pickerBottomLayout.cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotoPickerActivity.this.delegate.actionButtonPressed(true);
                PhotoPickerActivity.this.finishFragment();
            }
        });
        this.pickerBottomLayout.doneButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotoPickerActivity.this.sendSelectedPhotos();
            }
        });
        if (this.singlePhoto) {
            this.pickerBottomLayout.setVisibility(8);
        }
        this.listView.setEmptyView(this.emptyView);
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
        return this.fragmentView;
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

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.recentImagesDidLoaded && this.selectedAlbum == null && this.type == ((Integer) args[0]).intValue()) {
            this.recentImages = (ArrayList) args[1];
            this.loadingRecent = false;
            updateSearchInterface();
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

    public boolean scaleToFill() {
        return false;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        int i = 0;
        PhotoPickerPhotoCell cell = getCellForIndex(index);
        if (cell == null) {
            return null;
        }
        int[] coords = new int[2];
        cell.photoImage.getLocationInWindow(coords);
        PlaceProviderObject object = new PlaceProviderObject();
        object.viewX = coords[0];
        int i2 = coords[1];
        if (VERSION.SDK_INT < 21) {
            i = AndroidUtilities.statusBarHeight;
        }
        object.viewY = i2 - i;
        object.parentView = this.listView;
        object.imageReceiver = cell.photoImage.getImageReceiver();
        object.thumb = object.imageReceiver.getBitmap();
        object.scale = cell.photoImage.getScaleX();
        cell.checkBox.setVisibility(8);
        return object;
    }

    public void updatePhotoAtIndex(int index) {
        PhotoPickerPhotoCell cell = getCellForIndex(index);
        if (cell == null) {
            return;
        }
        if (this.selectedAlbum != null) {
            cell.photoImage.setOrientation(0, true);
            PhotoEntry photoEntry = (PhotoEntry) this.selectedAlbum.photos.get(index);
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
        if (this.searchResult.isEmpty() && this.lastSearchString == null) {
            array = this.recentImages;
        } else {
            array = this.searchResult;
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
        return this.allowCaption;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        PhotoPickerPhotoCell cell = getCellForIndex(index);
        if (cell != null) {
            return cell.photoImage.getImageReceiver().getBitmap();
        }
        return null;
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (view.getTag() != null) {
                PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) view;
                int num = ((Integer) view.getTag()).intValue();
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
                    continue;
                }
                if (num == index) {
                    cell.checkBox.setVisibility(0);
                    return;
                }
            }
        }
    }

    public void willHidePhotoViewer() {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (view instanceof PhotoPickerPhotoCell) {
                PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) view;
                if (cell.checkBox.getVisibility() != 0) {
                    cell.checkBox.setVisibility(0);
                }
            }
        }
    }

    public boolean isPhotoChecked(int index) {
        boolean z = true;
        if (this.selectedAlbum != null) {
            return index >= 0 && index < this.selectedAlbum.photos.size() && this.selectedPhotos.containsKey(Integer.valueOf(((PhotoEntry) this.selectedAlbum.photos.get(index)).imageId));
        } else {
            ArrayList<SearchImage> array;
            if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                array = this.recentImages;
            } else {
                array = this.searchResult;
            }
            if (index < 0 || index >= array.size() || !this.selectedWebPhotos.containsKey(((SearchImage) array.get(index)).id)) {
                z = false;
            }
            return z;
        }
    }

    public void setPhotoChecked(int index) {
        boolean add = true;
        if (this.selectedAlbum == null) {
            ArrayList<SearchImage> array;
            if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                array = this.recentImages;
            } else {
                array = this.searchResult;
            }
            if (index >= 0 && index < array.size()) {
                SearchImage photoEntry = (SearchImage) array.get(index);
                if (this.selectedWebPhotos.containsKey(photoEntry.id)) {
                    this.selectedWebPhotos.remove(photoEntry.id);
                    add = false;
                } else {
                    this.selectedWebPhotos.put(photoEntry.id, photoEntry);
                }
            } else {
                return;
            }
        } else if (index >= 0 && index < this.selectedAlbum.photos.size()) {
            PhotoEntry photoEntry2 = (PhotoEntry) this.selectedAlbum.photos.get(index);
            if (this.selectedPhotos.containsKey(Integer.valueOf(photoEntry2.imageId))) {
                this.selectedPhotos.remove(Integer.valueOf(photoEntry2.imageId));
                add = false;
            } else {
                this.selectedPhotos.put(Integer.valueOf(photoEntry2.imageId), photoEntry2);
            }
        } else {
            return;
        }
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (((Integer) view.getTag()).intValue() == index) {
                ((PhotoPickerPhotoCell) view).setChecked(add, false);
                break;
            }
        }
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
        this.delegate.selectedPhotosChanged();
    }

    public boolean cancelButtonPressed() {
        this.delegate.actionButtonPressed(true);
        finishFragment();
        return true;
    }

    public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
        if (this.selectedAlbum != null) {
            if (this.selectedPhotos.isEmpty()) {
                if (index >= 0 && index < this.selectedAlbum.photos.size()) {
                    PhotoEntry photoEntry = (PhotoEntry) this.selectedAlbum.photos.get(index);
                    this.selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
                } else {
                    return;
                }
            }
        } else if (this.selectedPhotos.isEmpty()) {
            ArrayList<SearchImage> array;
            if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                array = this.recentImages;
            } else {
                array = this.searchResult;
            }
            if (index >= 0 && index < array.size()) {
                SearchImage photoEntry2 = (SearchImage) array.get(index);
                this.selectedWebPhotos.put(photoEntry2.id, photoEntry2);
            } else {
                return;
            }
        }
        sendSelectedPhotos();
    }

    public int getSelectedCount() {
        return this.selectedPhotos.size() + this.selectedWebPhotos.size();
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
                ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
                this.giphyReqId = 0;
            }
            if (this.currentBingTask != null) {
                this.currentBingTask.cancel(true);
                this.currentBingTask = null;
            }
        }
        this.searching = true;
        TL_messages_searchGifs req = new TL_messages_searchGifs();
        req.q = query;
        req.offset = offset;
        final int token = this.lastSearchToken + 1;
        this.lastSearchToken = token;
        this.giphyReqId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        boolean z = true;
                        if (token == PhotoPickerActivity.this.lastSearchToken) {
                            int addedCount = 0;
                            if (response != null) {
                                boolean added = false;
                                TL_messages_foundGifs res = response;
                                PhotoPickerActivity.this.nextGiphySearchOffset = res.next_offset;
                                for (int a = 0; a < res.results.size(); a++) {
                                    FoundGif gif = (FoundGif) res.results.get(a);
                                    if (!PhotoPickerActivity.this.searchResultKeys.containsKey(gif.url)) {
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
                                            PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(gif.photo.sizes, PhotoPickerActivity.this.itemWidth, true);
                                            if (size != null) {
                                                gif.document.thumb = size;
                                            }
                                        }
                                        bingImage.type = 1;
                                        PhotoPickerActivity.this.searchResult.add(bingImage);
                                        addedCount++;
                                        PhotoPickerActivity.this.searchResultKeys.put(bingImage.id, bingImage);
                                    }
                                }
                                PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                                if (added) {
                                    z = false;
                                }
                                photoPickerActivity.giphySearchEndReached = z;
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
        ConnectionsManager.getInstance().bindRequestToGuid(this.giphyReqId, this.classGuid);
    }

    private void searchBingImages(String query, int offset, int count) {
        if (this.searching) {
            this.searching = false;
            if (this.giphyReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
                this.giphyReqId = 0;
            }
            if (this.currentBingTask != null) {
                this.currentBingTask.cancel(true);
                this.currentBingTask = null;
            }
        }
        try {
            boolean adult;
            this.searching = true;
            String phone = UserConfig.getCurrentUser().phone;
            if (phone.startsWith("44") || phone.startsWith("49") || phone.startsWith("43") || phone.startsWith("31") || phone.startsWith("1")) {
                adult = true;
            } else {
                adult = false;
            }
            Locale locale = Locale.US;
            String str = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q='%s'&offset=%d&count=%d&$format=json&safeSearch=%s";
            Object[] objArr = new Object[4];
            objArr[0] = URLEncoder.encode(query, "UTF-8");
            objArr[1] = Integer.valueOf(offset);
            objArr[2] = Integer.valueOf(count);
            objArr[3] = adult ? "Strict" : "Off";
            final String url = String.format(locale, str, objArr);
            this.currentBingTask = new AsyncTask<Void, Void, JSONObject>() {
                private boolean canRetry = true;

                /* JADX WARNING: inconsistent code. */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                private String downloadUrlContent(String url) {
                    Throwable e;
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
                    } catch (Throwable e2) {
                        if (e2 instanceof SocketTimeoutException) {
                            if (ConnectionsManager.isNetworkOnline()) {
                                canRetry = false;
                            }
                        } else if (e2 instanceof UnknownHostException) {
                            canRetry = false;
                        } else if (e2 instanceof SocketException) {
                            if (e2.getMessage() != null && e2.getMessage().contains("ECONNRESET")) {
                                canRetry = false;
                            }
                        } else if (e2 instanceof FileNotFoundException) {
                            canRetry = false;
                        }
                        FileLog.e(e2);
                    }
                    if (canRetry) {
                        if (httpConnection != null) {
                            try {
                                if (httpConnection instanceof HttpURLConnection) {
                                    int code = ((HttpURLConnection) httpConnection).getResponseCode();
                                    if (code != 200) {
                                        if (code != 202) {
                                        }
                                    }
                                }
                            } catch (Throwable e22) {
                                FileLog.e(e22);
                            }
                        }
                        if (httpConnectionStream != null) {
                            try {
                                byte[] data = new byte[32768];
                                StringBuilder result2 = null;
                                while (!isCancelled()) {
                                    try {
                                        try {
                                            int read = httpConnectionStream.read(data);
                                            if (read > 0) {
                                                if (result2 == null) {
                                                    result = new StringBuilder();
                                                } else {
                                                    result = result2;
                                                }
                                                try {
                                                    result.append(new String(data, 0, read, "UTF-8"));
                                                    result2 = result;
                                                } catch (Exception e3) {
                                                    e22 = e3;
                                                }
                                            } else if (read == -1) {
                                                done = true;
                                                result = result2;
                                            } else {
                                                result = result2;
                                            }
                                        } catch (Exception e4) {
                                            e22 = e4;
                                            result = result2;
                                        }
                                    } catch (Throwable th) {
                                        e22 = th;
                                        result = result2;
                                    }
                                }
                                result = result2;
                            } catch (Throwable th2) {
                                e22 = th2;
                                FileLog.e(e22);
                                if (httpConnectionStream != null) {
                                    try {
                                        httpConnectionStream.close();
                                    } catch (Throwable e222) {
                                        FileLog.e(e222);
                                    }
                                }
                                if (done) {
                                    return null;
                                }
                                return result.toString();
                            }
                        }
                        if (httpConnectionStream != null) {
                            httpConnectionStream.close();
                        }
                    }
                    if (done) {
                        return result.toString();
                    }
                    return null;
                    FileLog.e(e222);
                    if (httpConnectionStream != null) {
                        httpConnectionStream.close();
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
                        FileLog.e(e);
                        return null;
                    }
                }

                protected void onPostExecute(JSONObject response) {
                    boolean z = true;
                    int addedCount = 0;
                    if (response != null) {
                        try {
                            JSONArray result = response.getJSONArray(Param.VALUE);
                            boolean added = false;
                            for (int a = 0; a < result.length(); a++) {
                                try {
                                    JSONObject object = result.getJSONObject(a);
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
                                        addedCount++;
                                        added = true;
                                    }
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            }
                            PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                            if (added) {
                                z = false;
                            }
                            photoPickerActivity.bingSearchEndReached = z;
                        } catch (Throwable e2) {
                            FileLog.e(e2);
                        }
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
                    if ((PhotoPickerActivity.this.searching && PhotoPickerActivity.this.searchResult.isEmpty()) || (PhotoPickerActivity.this.loadingRecent && PhotoPickerActivity.this.lastSearchString == null)) {
                        PhotoPickerActivity.this.emptyView.showProgress();
                    } else {
                        PhotoPickerActivity.this.emptyView.showTextView();
                    }
                }
            };
            this.currentBingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
        } catch (Throwable e) {
            FileLog.e(e);
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
        if ((!this.selectedPhotos.isEmpty() || !this.selectedWebPhotos.isEmpty()) && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            this.delegate.actionButtonPressed(false);
            finishFragment();
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
