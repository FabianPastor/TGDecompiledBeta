package org.telegram.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
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
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell;
import org.telegram.ui.Cells.PhotoPickerSearchCell;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

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
    private int maxSelectedPhotos;
    private PickerBottomLayout pickerBottomLayout;
    private FrameLayout progressView;
    private ArrayList<SearchImage> recentGifImages = new ArrayList();
    private HashMap<String, SearchImage> recentImagesGifKeys = new HashMap();
    private HashMap<String, SearchImage> recentImagesWebKeys = new HashMap();
    private ArrayList<SearchImage> recentWebImages = new ArrayList();
    private int selectPhotoType;
    private HashMap<Object, Object> selectedPhotos = new HashMap();
    private ArrayList<Object> selectedPhotosOrder = new ArrayList();
    private boolean sendPressed;

    public interface PhotoAlbumPickerActivityDelegate {
        void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList);

        void startPhotoSelectActivity();
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            int i = 0;
            if (PhotoAlbumPickerActivity.this.allowSearchImages) {
                if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                    i = (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount)));
                }
                return i + 1;
            }
            if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                i = (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount)));
            }
            return i;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PhotoAlbumPickerActivity$ListAdapter(AlbumEntry albumEntry) {
            PhotoAlbumPickerActivity.this.openPhotoPicker(albumEntry, 0);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View photoPickerSearchCell;
            if (i != 0) {
                photoPickerSearchCell = new PhotoPickerSearchCell(this.mContext, PhotoAlbumPickerActivity.this.allowGifs);
                photoPickerSearchCell.setDelegate(new -$$Lambda$PhotoAlbumPickerActivity$ListAdapter$q2tmI74nlxknK7EWcU-vq6eNttA(this));
            } else {
                photoPickerSearchCell = new PhotoPickerAlbumsCell(this.mContext);
                photoPickerSearchCell.setDelegate(new -$$Lambda$PhotoAlbumPickerActivity$ListAdapter$wak8B6ZyJqrggtYN7y4fwAyLKCg(this));
            }
            return new Holder(photoPickerSearchCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1$PhotoAlbumPickerActivity$ListAdapter(int i) {
            PhotoAlbumPickerActivity.this.openPhotoPicker(null, i);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                PhotoPickerAlbumsCell photoPickerAlbumsCell = (PhotoPickerAlbumsCell) viewHolder.itemView;
                photoPickerAlbumsCell.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
                for (int i2 = 0; i2 < PhotoAlbumPickerActivity.this.columnsCount; i2++) {
                    int access$900;
                    if (PhotoAlbumPickerActivity.this.allowSearchImages) {
                        access$900 = (i - 1) * PhotoAlbumPickerActivity.this.columnsCount;
                    } else {
                        access$900 = PhotoAlbumPickerActivity.this.columnsCount * i;
                    }
                    access$900 += i2;
                    if (access$900 < PhotoAlbumPickerActivity.this.albumsSorted.size()) {
                        photoPickerAlbumsCell.setAlbum(i2, (AlbumEntry) PhotoAlbumPickerActivity.this.albumsSorted.get(access$900));
                    } else {
                        photoPickerAlbumsCell.setAlbum(i2, null);
                    }
                }
                photoPickerAlbumsCell.requestLayout();
            }
        }

        public int getItemViewType(int i) {
            return (PhotoAlbumPickerActivity.this.allowSearchImages && i == 0) ? 1 : 0;
        }
    }

    public PhotoAlbumPickerActivity(int i, boolean z, boolean z2, ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        this.selectPhotoType = i;
        this.allowGifs = z;
        this.allowCaption = z2;
    }

    public boolean onFragmentCreate() {
        this.loading = true;
        MediaController.loadGalleryPhotosAlbums(this.classGuid);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentImagesDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(-13421773);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsColor(-1, false);
        this.actionBar.setItemsBackgroundColor(-12763843, false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PhotoAlbumPickerActivity.this.finishFragment();
                } else if (i == 1 && PhotoAlbumPickerActivity.this.delegate != null) {
                    PhotoAlbumPickerActivity.this.finishFragment(false);
                    PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
                }
            }
        });
        this.actionBar.createMenu().addItem(1, NUM).setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(-16777216);
        this.actionBar.setTitle(LocaleController.getString("Gallery", NUM));
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
        ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setGlowColor(-13421773);
        this.emptyView = new TextView(context);
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setTextSize(20.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.emptyView.setText(LocaleController.getString("NoPhotos", NUM));
        frameLayout.addView(this.emptyView);
        LayoutParams layoutParams2 = (LayoutParams) this.emptyView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
        this.emptyView.setLayoutParams(layoutParams2);
        this.emptyView.setOnTouchListener(-$$Lambda$PhotoAlbumPickerActivity$2ZdkXHoPXptp2wpUszGZ5G4bMiQ.INSTANCE);
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(8);
        frameLayout.addView(this.progressView);
        layoutParams2 = (LayoutParams) this.progressView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
        this.progressView.setLayoutParams(layoutParams2);
        this.progressView.addView(new RadialProgressView(context));
        layoutParams2 = (LayoutParams) this.progressView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.gravity = 17;
        this.progressView.setLayoutParams(layoutParams2);
        this.pickerBottomLayout = new PickerBottomLayout(context);
        frameLayout.addView(this.pickerBottomLayout);
        LayoutParams layoutParams3 = (LayoutParams) this.pickerBottomLayout.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = AndroidUtilities.dp(48.0f);
        layoutParams3.gravity = 80;
        this.pickerBottomLayout.setLayoutParams(layoutParams3);
        this.pickerBottomLayout.cancelButton.setOnClickListener(new -$$Lambda$PhotoAlbumPickerActivity$jb2cegQfO3FSxtdYFSya79907zs(this));
        this.pickerBottomLayout.doneButton.setOnClickListener(new -$$Lambda$PhotoAlbumPickerActivity$vKBIYwKpMugrhprvLvaDbPzl8M0(this));
        if (this.loading) {
            ArrayList arrayList = this.albumsSorted;
            if (arrayList == null || (arrayList != null && arrayList.isEmpty())) {
                this.progressView.setVisibility(0);
                this.listView.setEmptyView(null);
                this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size(), true);
                return this.fragmentView;
            }
        }
        this.progressView.setVisibility(8);
        this.listView.setEmptyView(this.emptyView);
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size(), true);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$PhotoAlbumPickerActivity(View view) {
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$2$PhotoAlbumPickerActivity(View view) {
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder);
        finishFragment();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        fixLayout();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.albumsDidLoad) {
            if (this.classGuid == ((Integer) objArr[0]).intValue()) {
                if (this.selectPhotoType == 0 && this.allowSearchImages) {
                    this.albumsSorted = (ArrayList) objArr[1];
                } else {
                    this.albumsSorted = (ArrayList) objArr[2];
                }
                FrameLayout frameLayout = this.progressView;
                if (frameLayout != null) {
                    frameLayout.setVisibility(8);
                }
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null && recyclerListView.getEmptyView() == null) {
                    this.listView.setEmptyView(this.emptyView);
                }
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
                this.loading = false;
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.recentImagesDidLoad) {
            i = ((Integer) objArr[0]).intValue();
            Iterator it;
            SearchImage searchImage;
            if (i == 0) {
                this.recentWebImages = (ArrayList) objArr[1];
                this.recentImagesWebKeys.clear();
                it = this.recentWebImages.iterator();
                while (it.hasNext()) {
                    searchImage = (SearchImage) it.next();
                    this.recentImagesWebKeys.put(searchImage.id, searchImage);
                }
            } else if (i == 1) {
                this.recentGifImages = (ArrayList) objArr[1];
                this.recentImagesGifKeys.clear();
                it = this.recentGifImages.iterator();
                while (it.hasNext()) {
                    searchImage = (SearchImage) it.next();
                    this.recentImagesGifKeys.put(searchImage.id, searchImage);
                }
            }
        }
    }

    public void setMaxSelectedPhotos(int i) {
        this.maxSelectedPhotos = i;
    }

    public void setAllowSearchImages(boolean z) {
        this.allowSearchImages = z;
    }

    public void setDelegate(PhotoAlbumPickerActivityDelegate photoAlbumPickerActivityDelegate) {
        this.delegate = photoAlbumPickerActivityDelegate;
    }

    private void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList) {
        if (!hashMap.isEmpty() && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList arrayList2 = new ArrayList();
            Object obj = null;
            Object obj2 = null;
            for (int i = 0; i < arrayList.size(); i++) {
                Object obj3 = hashMap.get(arrayList.get(i));
                SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
                arrayList2.add(sendingMediaInfo);
                ArrayList arrayList3 = null;
                String str;
                CharSequence charSequence;
                if (obj3 instanceof PhotoEntry) {
                    PhotoEntry photoEntry = (PhotoEntry) obj3;
                    if (photoEntry.isVideo) {
                        sendingMediaInfo.path = photoEntry.path;
                        sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                    } else {
                        str = photoEntry.imagePath;
                        if (str != null) {
                            sendingMediaInfo.path = str;
                        } else {
                            str = photoEntry.path;
                            if (str != null) {
                                sendingMediaInfo.path = str;
                            }
                        }
                    }
                    sendingMediaInfo.isVideo = photoEntry.isVideo;
                    charSequence = photoEntry.caption;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = photoEntry.entities;
                    if (!photoEntry.stickers.isEmpty()) {
                        arrayList3 = new ArrayList(photoEntry.stickers);
                    }
                    sendingMediaInfo.masks = arrayList3;
                    sendingMediaInfo.ttl = photoEntry.ttl;
                } else if (obj3 instanceof SearchImage) {
                    SearchImage searchImage = (SearchImage) obj3;
                    str = searchImage.imagePath;
                    if (str != null) {
                        sendingMediaInfo.path = str;
                    } else {
                        sendingMediaInfo.searchImage = searchImage;
                    }
                    charSequence = searchImage.caption;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = searchImage.entities;
                    if (!searchImage.stickers.isEmpty()) {
                        arrayList3 = new ArrayList(searchImage.stickers);
                    }
                    sendingMediaInfo.masks = arrayList3;
                    sendingMediaInfo.ttl = searchImage.ttl;
                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                    int i2 = searchImage.type;
                    if (i2 == 0) {
                        SearchImage searchImage2 = (SearchImage) this.recentImagesWebKeys.get(searchImage.id);
                        if (searchImage2 != null) {
                            this.recentWebImages.remove(searchImage2);
                            this.recentWebImages.add(0, searchImage2);
                        } else {
                            this.recentWebImages.add(0, searchImage);
                        }
                        obj = 1;
                    } else if (i2 == 1) {
                        SearchImage searchImage3 = (SearchImage) this.recentImagesGifKeys.get(searchImage.id);
                        if (searchImage3 != null) {
                            this.recentGifImages.remove(searchImage3);
                            this.recentGifImages.add(0, searchImage3);
                        } else {
                            this.recentGifImages.add(0, searchImage);
                        }
                        obj2 = 1;
                    }
                }
            }
            if (obj != null) {
                MessagesStorage.getInstance(this.currentAccount).putWebRecent(this.recentWebImages);
            }
            if (obj2 != null) {
                MessagesStorage.getInstance(this.currentAccount).putWebRecent(this.recentGifImages);
            }
            this.delegate.didSelectPhotos(arrayList2);
        }
    }

    private void fixLayout() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    PhotoAlbumPickerActivity.this.fixLayoutInternal();
                    if (PhotoAlbumPickerActivity.this.listView != null) {
                        PhotoAlbumPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
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

    /* JADX WARNING: Removed duplicated region for block: B:10:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0011  */
    private void openPhotoPicker(org.telegram.messenger.MediaController.AlbumEntry r13, int r14) {
        /*
        r12 = this;
        if (r13 != 0) goto L_0x000d;
    L_0x0002:
        if (r14 != 0) goto L_0x0007;
    L_0x0004:
        r0 = r12.recentWebImages;
        goto L_0x000e;
    L_0x0007:
        r0 = 1;
        if (r14 != r0) goto L_0x000d;
    L_0x000a:
        r0 = r12.recentGifImages;
        goto L_0x000e;
    L_0x000d:
        r0 = 0;
    L_0x000e:
        r6 = r0;
        if (r13 == 0) goto L_0x002c;
    L_0x0011:
        r0 = new org.telegram.ui.PhotoPickerActivity;
        r4 = r12.selectedPhotos;
        r5 = r12.selectedPhotosOrder;
        r7 = r12.selectPhotoType;
        r8 = r12.allowCaption;
        r9 = r12.chatActivity;
        r1 = r0;
        r2 = r14;
        r3 = r13;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9);
        r13 = new org.telegram.ui.PhotoAlbumPickerActivity$3;
        r13.<init>();
        r0.setDelegate(r13);
        goto L_0x004f;
    L_0x002c:
        r0 = new java.util.HashMap;
        r0.<init>();
        r10 = new java.util.ArrayList;
        r10.<init>();
        r11 = new org.telegram.ui.PhotoPickerActivity;
        r7 = r12.selectPhotoType;
        r8 = r12.allowCaption;
        r9 = r12.chatActivity;
        r1 = r11;
        r2 = r14;
        r3 = r13;
        r4 = r0;
        r5 = r10;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9);
        r13 = new org.telegram.ui.PhotoAlbumPickerActivity$4;
        r13.<init>(r0, r10);
        r11.setDelegate(r13);
        r0 = r11;
    L_0x004f:
        r13 = r12.maxSelectedPhotos;
        r0.setMaxSelectedPhotos(r13);
        r12.presentFragment(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoAlbumPickerActivity.openPhotoPicker(org.telegram.messenger.MediaController$AlbumEntry, int):void");
    }
}
