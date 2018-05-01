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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
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
    private ChatActivity chatActivity;
    private int columnsCount = 2;
    private PhotoAlbumPickerActivityDelegate delegate;
    private TextView emptyView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading = false;
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
    class C15792 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C15792() {
        }
    }

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$3 */
    class C15803 implements OnClickListener {
        C15803() {
        }

        public void onClick(View view) {
            PhotoAlbumPickerActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$4 */
    class C15814 implements OnClickListener {
        C15814() {
        }

        public void onClick(View view) {
            PhotoAlbumPickerActivity.this.sendSelectedPhotos(PhotoAlbumPickerActivity.this.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder);
            PhotoAlbumPickerActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$5 */
    class C15825 implements OnPreDrawListener {
        C15825() {
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
    class C22211 extends ActionBarMenuOnItemClick {
        C22211() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PhotoAlbumPickerActivity.this.finishFragment();
            } else if (i == 1 && PhotoAlbumPickerActivity.this.delegate != 0) {
                PhotoAlbumPickerActivity.this.finishFragment(false);
                PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$6 */
    class C22226 implements PhotoPickerActivityDelegate {
        C22226() {
        }

        public void selectedPhotosChanged() {
            if (PhotoAlbumPickerActivity.this.pickerBottomLayout != null) {
                PhotoAlbumPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoAlbumPickerActivity.this.selectedPhotos.size(), true);
            }
        }

        public void actionButtonPressed(boolean z) {
            PhotoAlbumPickerActivity.this.removeSelfFromStack();
            if (!z) {
                PhotoAlbumPickerActivity.this.sendSelectedPhotos(PhotoAlbumPickerActivity.this.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder);
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$7 */
    class C22237 implements PhotoPickerActivityDelegate {
        final /* synthetic */ ArrayList val$order;
        final /* synthetic */ HashMap val$photos;

        public void selectedPhotosChanged() {
        }

        C22237(HashMap hashMap, ArrayList arrayList) {
            this.val$photos = hashMap;
            this.val$order = arrayList;
        }

        public void actionButtonPressed(boolean z) {
            PhotoAlbumPickerActivity.this.removeSelfFromStack();
            if (!z) {
                PhotoAlbumPickerActivity.this.sendSelectedPhotos(this.val$photos, this.val$order);
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$ListAdapter$1 */
        class C22241 implements PhotoPickerAlbumsCellDelegate {
            C22241() {
            }

            public void didSelectAlbum(AlbumEntry albumEntry) {
                PhotoAlbumPickerActivity.this.openPhotoPicker(albumEntry, 0);
            }
        }

        /* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$ListAdapter$2 */
        class C22252 implements PhotoPickerSearchCellDelegate {
            C22252() {
            }

            public void didPressedSearchButton(int i) {
                PhotoAlbumPickerActivity.this.openPhotoPicker(null, i);
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            int i = 0;
            if (PhotoAlbumPickerActivity.this.singlePhoto) {
                if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                    i = (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount)));
                }
                return i;
            }
            if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                i = (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount)));
            }
            return 1 + i;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new PhotoPickerSearchCell(this.mContext, PhotoAlbumPickerActivity.this.allowGifs);
                viewGroup.setDelegate(new C22252());
            } else {
                viewGroup = new PhotoPickerAlbumsCell(this.mContext);
                viewGroup.setDelegate(new C22241());
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                PhotoPickerAlbumsCell photoPickerAlbumsCell = (PhotoPickerAlbumsCell) viewHolder.itemView;
                photoPickerAlbumsCell.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
                for (int i2 = 0; i2 < PhotoAlbumPickerActivity.this.columnsCount; i2++) {
                    int access$900;
                    if (PhotoAlbumPickerActivity.this.singlePhoto) {
                        access$900 = (PhotoAlbumPickerActivity.this.columnsCount * i) + i2;
                    } else {
                        access$900 = ((i - 1) * PhotoAlbumPickerActivity.this.columnsCount) + i2;
                    }
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
            return (!PhotoAlbumPickerActivity.this.singlePhoto && i == 0) ? 1 : 0;
        }
    }

    public PhotoAlbumPickerActivity(boolean z, boolean z2, boolean z3, ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        this.singlePhoto = z;
        this.allowGifs = z2;
        this.allowCaption = z3;
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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new C22211());
        this.actionBar.createMenu().addItem(1, (int) C0446R.drawable.ic_ab_other);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        this.actionBar.setTitle(LocaleController.getString("Gallery", C0446R.string.Gallery));
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
        this.emptyView.setText(LocaleController.getString("NoPhotos", C0446R.string.NoPhotos));
        frameLayout.addView(this.emptyView);
        LayoutParams layoutParams2 = (LayoutParams) this.emptyView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
        this.emptyView.setLayoutParams(layoutParams2);
        this.emptyView.setOnTouchListener(new C15792());
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
        this.pickerBottomLayout.cancelButton.setOnClickListener(new C15803());
        this.pickerBottomLayout.doneButton.setOnClickListener(new C15814());
        if (this.loading == null || (this.albumsSorted != null && (this.albumsSorted == null || this.albumsSorted.isEmpty() == null))) {
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

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.albumsDidLoaded) {
            if (this.classGuid == ((Integer) objArr[0]).intValue()) {
                if (this.singlePhoto != 0) {
                    this.albumsSorted = (ArrayList) objArr[2];
                } else {
                    this.albumsSorted = (ArrayList) objArr[1];
                }
                if (this.progressView != 0) {
                    this.progressView.setVisibility(8);
                }
                if (this.listView != 0 && this.listView.getEmptyView() == 0) {
                    this.listView.setEmptyView(this.emptyView);
                }
                if (this.listAdapter != 0) {
                    this.listAdapter.notifyDataSetChanged();
                }
                this.loading = false;
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.recentImagesDidLoaded) {
            i = ((Integer) objArr[0]).intValue();
            SearchImage searchImage;
            if (i == 0) {
                this.recentWebImages = (ArrayList) objArr[1];
                this.recentImagesWebKeys.clear();
                i = this.recentWebImages.iterator();
                while (i.hasNext() != 0) {
                    searchImage = (SearchImage) i.next();
                    this.recentImagesWebKeys.put(searchImage.id, searchImage);
                }
            } else if (i == 1) {
                this.recentGifImages = (ArrayList) objArr[1];
                this.recentImagesGifKeys.clear();
                i = this.recentGifImages.iterator();
                while (i.hasNext() != 0) {
                    searchImage = (SearchImage) i.next();
                    this.recentImagesGifKeys.put(searchImage.id, searchImage);
                }
            }
        }
    }

    public void setDelegate(PhotoAlbumPickerActivityDelegate photoAlbumPickerActivityDelegate) {
        this.delegate = photoAlbumPickerActivityDelegate;
    }

    private void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList) {
        if (!(hashMap.isEmpty() || this.delegate == null)) {
            if (!this.sendPressed) {
                this.sendPressed = true;
                ArrayList arrayList2 = new ArrayList();
                int i = 0;
                int i2 = i;
                int i3 = i2;
                while (i < arrayList.size()) {
                    Object obj = hashMap.get(arrayList.get(i));
                    SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
                    arrayList2.add(sendingMediaInfo);
                    ArrayList arrayList3 = null;
                    if (obj instanceof PhotoEntry) {
                        PhotoEntry photoEntry = (PhotoEntry) obj;
                        if (photoEntry.isVideo) {
                            sendingMediaInfo.path = photoEntry.path;
                            sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                        } else if (photoEntry.imagePath != null) {
                            sendingMediaInfo.path = photoEntry.imagePath;
                        } else if (photoEntry.path != null) {
                            sendingMediaInfo.path = photoEntry.path;
                        }
                        sendingMediaInfo.isVideo = photoEntry.isVideo;
                        sendingMediaInfo.caption = photoEntry.caption != null ? photoEntry.caption.toString() : null;
                        sendingMediaInfo.entities = photoEntry.entities;
                        if (!photoEntry.stickers.isEmpty()) {
                            arrayList3 = new ArrayList(photoEntry.stickers);
                        }
                        sendingMediaInfo.masks = arrayList3;
                        sendingMediaInfo.ttl = photoEntry.ttl;
                    } else if (obj instanceof SearchImage) {
                        SearchImage searchImage = (SearchImage) obj;
                        if (searchImage.imagePath != null) {
                            sendingMediaInfo.path = searchImage.imagePath;
                        } else {
                            sendingMediaInfo.searchImage = searchImage;
                        }
                        sendingMediaInfo.caption = searchImage.caption != null ? searchImage.caption.toString() : null;
                        sendingMediaInfo.entities = searchImage.entities;
                        if (!searchImage.stickers.isEmpty()) {
                            arrayList3 = new ArrayList(searchImage.stickers);
                        }
                        sendingMediaInfo.masks = arrayList3;
                        sendingMediaInfo.ttl = searchImage.ttl;
                        searchImage.date = (int) (System.currentTimeMillis() / 1000);
                        if (searchImage.type == 0) {
                            SearchImage searchImage2 = (SearchImage) this.recentImagesWebKeys.get(searchImage.id);
                            if (searchImage2 != null) {
                                this.recentWebImages.remove(searchImage2);
                                this.recentWebImages.add(0, searchImage2);
                            } else {
                                this.recentWebImages.add(0, searchImage);
                            }
                            i2 = true;
                        } else if (searchImage.type == 1) {
                            SearchImage searchImage3 = (SearchImage) this.recentImagesGifKeys.get(searchImage.id);
                            if (searchImage3 != null) {
                                this.recentGifImages.remove(searchImage3);
                                this.recentGifImages.add(0, searchImage3);
                            } else {
                                this.recentGifImages.add(0, searchImage);
                            }
                            i3 = true;
                        }
                    }
                    i++;
                }
                if (i2 != 0) {
                    MessagesStorage.getInstance(this.currentAccount).putWebRecent(this.recentWebImages);
                }
                if (i3 != 0) {
                    MessagesStorage.getInstance(this.currentAccount).putWebRecent(this.recentGifImages);
                }
                this.delegate.didSelectPhotos(arrayList2);
            }
        }
    }

    private void fixLayout() {
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C15825());
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

    private void openPhotoPicker(org.telegram.messenger.MediaController.AlbumEntry r13, int r14) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r11_1 org.telegram.ui.ActionBar.BaseFragment) in PHI: PHI: (r11_2 org.telegram.ui.ActionBar.BaseFragment) = (r11_0 org.telegram.ui.ActionBar.BaseFragment), (r11_1 org.telegram.ui.ActionBar.BaseFragment) binds: {(r11_0 org.telegram.ui.ActionBar.BaseFragment)=B:9:0x0011, (r11_1 org.telegram.ui.ActionBar.BaseFragment)=B:10:0x002d}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
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
        if (r13 == 0) goto L_0x002d;
    L_0x0011:
        r0 = new org.telegram.ui.PhotoPickerActivity;
        r4 = r12.selectedPhotos;
        r5 = r12.selectedPhotosOrder;
        r7 = r12.singlePhoto;
        r8 = r12.allowCaption;
        r9 = r12.chatActivity;
        r1 = r0;
        r2 = r14;
        r3 = r13;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9);
        r13 = new org.telegram.ui.PhotoAlbumPickerActivity$6;
        r13.<init>();
        r0.setDelegate(r13);
        r11 = r0;
        goto L_0x004f;
    L_0x002d:
        r0 = new java.util.HashMap;
        r0.<init>();
        r10 = new java.util.ArrayList;
        r10.<init>();
        r11 = new org.telegram.ui.PhotoPickerActivity;
        r7 = r12.singlePhoto;
        r8 = r12.allowCaption;
        r9 = r12.chatActivity;
        r1 = r11;
        r2 = r14;
        r3 = r13;
        r4 = r0;
        r5 = r10;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9);
        r13 = new org.telegram.ui.PhotoAlbumPickerActivity$7;
        r13.<init>(r0, r10);
        r11.setDelegate(r13);
    L_0x004f:
        r12.presentFragment(r11);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoAlbumPickerActivity.openPhotoPicker(org.telegram.messenger.MediaController$AlbumEntry, int):void");
    }
}
