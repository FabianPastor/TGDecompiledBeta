package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$messages_BotResults;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerViewItemRangeSelector;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoViewer;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int alertOnlyOnce;
    /* access modifiers changed from: private */
    public boolean allowCaption;
    /* access modifiers changed from: private */
    public boolean allowIndices;
    /* access modifiers changed from: private */
    public boolean allowOrder;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private CharSequence caption;
    /* access modifiers changed from: private */
    public ChatActivity chatActivity;
    protected EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public PhotoPickerActivityDelegate delegate;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    protected FrameLayout frameLayout2;
    /* access modifiers changed from: private */
    public int imageReqId;
    /* access modifiers changed from: private */
    public boolean imageSearchEndReached;
    private String initialSearchString;
    private boolean isDocumentsPicker;
    private ActionBarMenuSubItem[] itemCells;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    /* access modifiers changed from: private */
    public int itemSize;
    /* access modifiers changed from: private */
    public int itemsPerRow;
    private String lastSearchImageString;
    /* access modifiers changed from: private */
    public String lastSearchString;
    private int lastSearchToken;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public boolean listSort;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int maxSelectedPhotos;
    private boolean needsBottomLayout;
    /* access modifiers changed from: private */
    public String nextImagesSearchOffset;
    /* access modifiers changed from: private */
    public Paint paint;
    private PhotoViewer.PhotoViewerProvider provider;
    /* access modifiers changed from: private */
    public ArrayList<String> recentSearches;
    /* access modifiers changed from: private */
    public RectF rect;
    private PhotoPickerActivitySearchDelegate searchDelegate;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.SearchImage> searchResult = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<String, MediaController.SearchImage> searchResultKeys = new HashMap<>();
    /* access modifiers changed from: private */
    public boolean searching;
    private boolean searchingUser;
    /* access modifiers changed from: private */
    public int selectPhotoType;
    /* access modifiers changed from: private */
    public MediaController.AlbumEntry selectedAlbum;
    protected View selectedCountView;
    /* access modifiers changed from: private */
    public HashMap<Object, Object> selectedPhotos;
    /* access modifiers changed from: private */
    public ArrayList<Object> selectedPhotosOrder;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    protected View shadow;
    /* access modifiers changed from: private */
    public boolean shouldSelect;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem showAsListItem;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public TextPaint textPaint;
    /* access modifiers changed from: private */
    public int type;
    private ImageView writeButton;
    protected FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public interface PhotoPickerActivityDelegate {

        /* renamed from: org.telegram.ui.PhotoPickerActivity$PhotoPickerActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onOpenInPressed(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
            }
        }

        void actionButtonPressed(boolean z, boolean z2, int i);

        void onCaptionChanged(CharSequence charSequence);

        void onOpenInPressed();

        void selectedPhotosChanged();
    }

    public interface PhotoPickerActivitySearchDelegate {
        void shouldClearRecentSearch();

        void shouldSearchText(String str);
    }

    static /* synthetic */ boolean lambda$createView$3(View view, MotionEvent motionEvent) {
        return true;
    }

    public PhotoPickerActivity(int i, MediaController.AlbumEntry albumEntry, HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, int i2, boolean z, ChatActivity chatActivity2) {
        new HashMap();
        this.recentSearches = new ArrayList<>();
        this.imageSearchEndReached = true;
        this.allowOrder = true;
        this.itemSize = 100;
        this.itemsPerRow = 3;
        this.textPaint = new TextPaint(1);
        this.rect = new RectF();
        this.paint = new Paint(1);
        this.needsBottomLayout = true;
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            public boolean scaleToFill() {
                return false;
            }

            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
                PhotoAttachPhotoCell access$000 = PhotoPickerActivity.this.getCellForIndex(i);
                if (access$000 == null) {
                    return null;
                }
                BackupImageView imageView = access$000.getImageView();
                int[] iArr = new int[2];
                imageView.getLocationInWindow(iArr);
                PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                placeProviderObject.parentView = PhotoPickerActivity.this.listView;
                ImageReceiver imageReceiver = imageView.getImageReceiver();
                placeProviderObject.imageReceiver = imageReceiver;
                placeProviderObject.thumb = imageReceiver.getBitmapSafe();
                placeProviderObject.scale = access$000.getScale();
                access$000.showCheck(false);
                return placeProviderObject;
            }

            public void updatePhotoAtIndex(int i) {
                PhotoAttachPhotoCell access$000 = PhotoPickerActivity.this.getCellForIndex(i);
                if (access$000 == null) {
                    return;
                }
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    BackupImageView imageView = access$000.getImageView();
                    imageView.setOrientation(0, true);
                    MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                    String str = photoEntry.thumbPath;
                    if (str != null) {
                        imageView.setImage(str, (String) null, Theme.chat_attachEmptyDrawable);
                    } else if (photoEntry.path != null) {
                        imageView.setOrientation(photoEntry.orientation, true);
                        if (photoEntry.isVideo) {
                            imageView.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
                            return;
                        }
                        imageView.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
                    } else {
                        imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
                    }
                } else {
                    access$000.setPhotoEntry((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i), true, false);
                }
            }

            public boolean allowCaption() {
                return PhotoPickerActivity.this.allowCaption;
            }

            public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i) {
                PhotoAttachPhotoCell access$000 = PhotoPickerActivity.this.getCellForIndex(i);
                if (access$000 != null) {
                    return access$000.getImageView().getImageReceiver().getBitmapSafe();
                }
                return null;
            }

            public void willSwitchFromPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i) {
                int childCount = PhotoPickerActivity.this.listView.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = PhotoPickerActivity.this.listView.getChildAt(i2);
                    if (childAt.getTag() != null) {
                        PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                        int intValue = ((Integer) childAt.getTag()).intValue();
                        if (PhotoPickerActivity.this.selectedAlbum == null ? !(intValue < 0 || intValue >= PhotoPickerActivity.this.searchResult.size()) : !(intValue < 0 || intValue >= PhotoPickerActivity.this.selectedAlbum.photos.size())) {
                            if (intValue == i) {
                                photoAttachPhotoCell.showCheck(true);
                                return;
                            }
                        }
                    }
                }
            }

            public void willHidePhotoViewer() {
                int childCount = PhotoPickerActivity.this.listView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = PhotoPickerActivity.this.listView.getChildAt(i);
                    if (childAt instanceof PhotoAttachPhotoCell) {
                        ((PhotoAttachPhotoCell) childAt).showCheck(true);
                    }
                }
            }

            public boolean isPhotoChecked(int i) {
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    if (i < 0 || i >= PhotoPickerActivity.this.selectedAlbum.photos.size() || !PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(i).imageId))) {
                        return false;
                    }
                    return true;
                } else if (i < 0 || i >= PhotoPickerActivity.this.searchResult.size() || !PhotoPickerActivity.this.selectedPhotos.containsKey(((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i)).id)) {
                    return false;
                } else {
                    return true;
                }
            }

            public int setPhotoUnchecked(Object obj) {
                Object obj2;
                if (obj instanceof MediaController.PhotoEntry) {
                    obj2 = Integer.valueOf(((MediaController.PhotoEntry) obj).imageId);
                } else {
                    obj2 = obj instanceof MediaController.SearchImage ? ((MediaController.SearchImage) obj).id : null;
                }
                if (obj2 == null || !PhotoPickerActivity.this.selectedPhotos.containsKey(obj2)) {
                    return -1;
                }
                PhotoPickerActivity.this.selectedPhotos.remove(obj2);
                int indexOf = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(obj2);
                if (indexOf >= 0) {
                    PhotoPickerActivity.this.selectedPhotosOrder.remove(indexOf);
                }
                if (PhotoPickerActivity.this.allowIndices) {
                    PhotoPickerActivity.this.updateCheckedPhotoIndices();
                }
                return indexOf;
            }

            /* JADX WARNING: Removed duplicated region for block: B:21:0x008e  */
            /* JADX WARNING: Removed duplicated region for block: B:30:0x00bb  */
            /* JADX WARNING: Removed duplicated region for block: B:35:0x00b6 A[EDGE_INSN: B:35:0x00b6->B:28:0x00b6 ?: BREAK  , SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public int setPhotoChecked(int r9, org.telegram.messenger.VideoEditedInfo r10) {
                /*
                    r8 = this;
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.messenger.MediaController$AlbumEntry r0 = r0.selectedAlbum
                    r1 = 0
                    r2 = 1
                    r3 = 0
                    r4 = -1
                    if (r0 == 0) goto L_0x004a
                    if (r9 < 0) goto L_0x0049
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.messenger.MediaController$AlbumEntry r0 = r0.selectedAlbum
                    java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r0 = r0.photos
                    int r0 = r0.size()
                    if (r9 < r0) goto L_0x001d
                    goto L_0x0049
                L_0x001d:
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.messenger.MediaController$AlbumEntry r0 = r0.selectedAlbum
                    java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r0 = r0.photos
                    java.lang.Object r0 = r0.get(r9)
                    org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
                    org.telegram.ui.PhotoPickerActivity r5 = org.telegram.ui.PhotoPickerActivity.this
                    int r5 = r5.addToSelectedPhotos(r0, r4)
                    if (r5 != r4) goto L_0x0046
                    r0.editedInfo = r10
                    org.telegram.ui.PhotoPickerActivity r10 = org.telegram.ui.PhotoPickerActivity.this
                    java.util.ArrayList r10 = r10.selectedPhotosOrder
                    int r0 = r0.imageId
                    java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                    int r5 = r10.indexOf(r0)
                    goto L_0x007c
                L_0x0046:
                    r0.editedInfo = r1
                    goto L_0x0080
                L_0x0049:
                    return r4
                L_0x004a:
                    if (r9 < 0) goto L_0x00c9
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    java.util.ArrayList r0 = r0.searchResult
                    int r0 = r0.size()
                    if (r9 < r0) goto L_0x005a
                    goto L_0x00c9
                L_0x005a:
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    java.util.ArrayList r0 = r0.searchResult
                    java.lang.Object r0 = r0.get(r9)
                    org.telegram.messenger.MediaController$SearchImage r0 = (org.telegram.messenger.MediaController.SearchImage) r0
                    org.telegram.ui.PhotoPickerActivity r5 = org.telegram.ui.PhotoPickerActivity.this
                    int r5 = r5.addToSelectedPhotos(r0, r4)
                    if (r5 != r4) goto L_0x007e
                    r0.editedInfo = r10
                    org.telegram.ui.PhotoPickerActivity r10 = org.telegram.ui.PhotoPickerActivity.this
                    java.util.ArrayList r10 = r10.selectedPhotosOrder
                    java.lang.String r0 = r0.id
                    int r5 = r10.indexOf(r0)
                L_0x007c:
                    r10 = 1
                    goto L_0x0081
                L_0x007e:
                    r0.editedInfo = r1
                L_0x0080:
                    r10 = 0
                L_0x0081:
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.RecyclerListView r0 = r0.listView
                    int r0 = r0.getChildCount()
                    r1 = 0
                L_0x008c:
                    if (r1 >= r0) goto L_0x00b6
                    org.telegram.ui.PhotoPickerActivity r6 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.RecyclerListView r6 = r6.listView
                    android.view.View r6 = r6.getChildAt(r1)
                    java.lang.Object r7 = r6.getTag()
                    java.lang.Integer r7 = (java.lang.Integer) r7
                    int r7 = r7.intValue()
                    if (r7 != r9) goto L_0x00b3
                    org.telegram.ui.Cells.PhotoAttachPhotoCell r6 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r6
                    org.telegram.ui.PhotoPickerActivity r9 = org.telegram.ui.PhotoPickerActivity.this
                    boolean r9 = r9.allowIndices
                    if (r9 == 0) goto L_0x00af
                    r4 = r5
                L_0x00af:
                    r6.setChecked(r4, r10, r3)
                    goto L_0x00b6
                L_0x00b3:
                    int r1 = r1 + 1
                    goto L_0x008c
                L_0x00b6:
                    org.telegram.ui.PhotoPickerActivity r9 = org.telegram.ui.PhotoPickerActivity.this
                    if (r10 == 0) goto L_0x00bb
                    goto L_0x00bc
                L_0x00bb:
                    r2 = 2
                L_0x00bc:
                    r9.updatePhotosButton(r2)
                    org.telegram.ui.PhotoPickerActivity r9 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.PhotoPickerActivity$PhotoPickerActivityDelegate r9 = r9.delegate
                    r9.selectedPhotosChanged()
                    return r5
                L_0x00c9:
                    return r4
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity.AnonymousClass1.setPhotoChecked(int, org.telegram.messenger.VideoEditedInfo):int");
            }

            public boolean cancelButtonPressed() {
                PhotoPickerActivity.this.delegate.actionButtonPressed(true, true, 0);
                PhotoPickerActivity.this.finishFragment();
                return true;
            }

            public int getSelectedCount() {
                return PhotoPickerActivity.this.selectedPhotos.size();
            }

            public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2) {
                if (PhotoPickerActivity.this.selectedPhotos.isEmpty()) {
                    if (PhotoPickerActivity.this.selectedAlbum != null) {
                        if (i >= 0 && i < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                            MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                            photoEntry.editedInfo = videoEditedInfo;
                            int unused = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                        } else {
                            return;
                        }
                    } else if (i >= 0 && i < PhotoPickerActivity.this.searchResult.size()) {
                        MediaController.SearchImage searchImage = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                        searchImage.editedInfo = videoEditedInfo;
                        int unused2 = PhotoPickerActivity.this.addToSelectedPhotos(searchImage, -1);
                    } else {
                        return;
                    }
                }
                PhotoPickerActivity.this.sendSelectedPhotos(z, i2);
            }

            public ArrayList<Object> getSelectedPhotosOrder() {
                return PhotoPickerActivity.this.selectedPhotosOrder;
            }

            public HashMap<Object, Object> getSelectedPhotos() {
                return PhotoPickerActivity.this.selectedPhotos;
            }
        };
        this.selectedAlbum = albumEntry;
        this.selectedPhotos = hashMap;
        this.selectedPhotosOrder = arrayList;
        this.type = i;
        this.selectPhotoType = i2;
        this.chatActivity = chatActivity2;
        this.allowCaption = z;
        if (albumEntry == null) {
            loadRecentSearch();
        }
    }

    public void setDocumentsPicker(boolean z) {
        this.isDocumentsPicker = z;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        if (this.imageReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
            this.imageReqId = 0;
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        int i;
        Context context2 = context;
        this.listSort = false;
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonImage(NUM);
        MediaController.AlbumEntry albumEntry = this.selectedAlbum;
        boolean z = true;
        if (albumEntry != null) {
            this.actionBar.setTitle(albumEntry.bucketName);
        } else {
            int i2 = this.type;
            if (i2 == 0) {
                this.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", NUM));
            } else if (i2 == 1) {
                this.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", NUM));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PhotoPickerActivity.this.finishFragment();
                } else if (i == 1) {
                    PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                    boolean unused = photoPickerActivity.listSort = true ^ photoPickerActivity.listSort;
                    if (PhotoPickerActivity.this.listSort) {
                        PhotoPickerActivity.this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
                    } else {
                        PhotoPickerActivity.this.listView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f));
                    }
                    PhotoPickerActivity.this.listView.stopScroll();
                    PhotoPickerActivity.this.layoutManager.scrollToPositionWithOffset(0, 0);
                    PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                } else if (i == 2) {
                    if (PhotoPickerActivity.this.delegate != null) {
                        PhotoPickerActivity.this.delegate.onOpenInPressed();
                    }
                    PhotoPickerActivity.this.finishFragment();
                }
            }
        });
        if (this.isDocumentsPicker) {
            ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
            addItem.setSubMenuDelegate(new ActionBarMenuItem.ActionBarSubMenuItemDelegate() {
                public void onHideSubMenu() {
                }

                public void onShowSubMenu() {
                    String str;
                    int i;
                    ActionBarMenuSubItem access$1500 = PhotoPickerActivity.this.showAsListItem;
                    if (PhotoPickerActivity.this.listSort) {
                        i = NUM;
                        str = "ShowAsGrid";
                    } else {
                        i = NUM;
                        str = "ShowAsList";
                    }
                    access$1500.setText(LocaleController.getString(str, i));
                    PhotoPickerActivity.this.showAsListItem.setIcon(PhotoPickerActivity.this.listSort ? NUM : NUM);
                }
            });
            this.showAsListItem = addItem.addSubItem(1, NUM, LocaleController.getString("ShowAsList", NUM));
            addItem.addSubItem(2, NUM, LocaleController.getString("OpenInExternalApp", NUM));
        }
        if (this.selectedAlbum == null) {
            ActionBarMenuItem addItem2 = this.actionBar.createMenu().addItem(0, NUM);
            addItem2.setIsSearchField(true);
            addItem2.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
                        String unused = PhotoPickerActivity.this.lastSearchString = null;
                        boolean unused2 = PhotoPickerActivity.this.imageSearchEndReached = true;
                        boolean unused3 = PhotoPickerActivity.this.searching = false;
                        if (PhotoPickerActivity.this.imageReqId != 0) {
                            ConnectionsManager.getInstance(PhotoPickerActivity.this.currentAccount).cancelRequest(PhotoPickerActivity.this.imageReqId, true);
                            int unused4 = PhotoPickerActivity.this.imageReqId = 0;
                        }
                        PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentSearches", NUM));
                        PhotoPickerActivity.this.updateSearchInterface();
                    }
                }

                public void onSearchPressed(EditText editText) {
                    PhotoPickerActivity.this.processSearch(editText);
                }
            });
            this.searchItem = addItem2;
            EditTextBoldCursor searchField = addItem2.getSearchField();
            searchField.setTextColor(Theme.getColor("dialogTextBlack"));
            searchField.setCursorColor(Theme.getColor("dialogTextBlack"));
            searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        }
        if (this.selectedAlbum == null) {
            int i3 = this.type;
            if (i3 == 0) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", NUM));
            } else if (i3 == 1) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchGifsTitle", NUM));
            }
        }
        AnonymousClass5 r3 = new SizeNotifierFrameLayout(context2, SharedConfig.smoothKeyboard) {
            private boolean ignoreLayout;
            private int lastItemSize;
            private int lastNotifyWidth;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i2);
                int size2 = View.MeasureSpec.getSize(i);
                if (AndroidUtilities.isTablet()) {
                    int unused = PhotoPickerActivity.this.itemsPerRow = 4;
                } else {
                    Point point = AndroidUtilities.displaySize;
                    if (point.x > point.y) {
                        int unused2 = PhotoPickerActivity.this.itemsPerRow = 4;
                    } else {
                        int unused3 = PhotoPickerActivity.this.itemsPerRow = 3;
                    }
                }
                this.ignoreLayout = true;
                int unused4 = PhotoPickerActivity.this.itemSize = ((size2 - AndroidUtilities.dp(12.0f)) - AndroidUtilities.dp(10.0f)) / PhotoPickerActivity.this.itemsPerRow;
                if (this.lastItemSize != PhotoPickerActivity.this.itemSize) {
                    this.lastItemSize = PhotoPickerActivity.this.itemSize;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            PhotoPickerActivity.AnonymousClass5.this.lambda$onMeasure$0$PhotoPickerActivity$5();
                        }
                    });
                }
                if (PhotoPickerActivity.this.listSort) {
                    PhotoPickerActivity.this.layoutManager.setSpanCount(1);
                } else {
                    PhotoPickerActivity.this.layoutManager.setSpanCount((PhotoPickerActivity.this.itemSize * PhotoPickerActivity.this.itemsPerRow) + (AndroidUtilities.dp(5.0f) * (PhotoPickerActivity.this.itemsPerRow - 1)));
                }
                this.ignoreLayout = false;
                onMeasureInternal(i, View.MeasureSpec.makeMeasureSpec(size, NUM));
            }

            public /* synthetic */ void lambda$onMeasure$0$PhotoPickerActivity$5() {
                PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
            }

            /* JADX WARNING: Removed duplicated region for block: B:28:0x0093  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            private void onMeasureInternal(int r13, int r14) {
                /*
                    r12 = this;
                    int r6 = android.view.View.MeasureSpec.getSize(r13)
                    int r0 = android.view.View.MeasureSpec.getSize(r14)
                    r12.setMeasuredDimension(r6, r0)
                    int r1 = r12.measureKeyboardHeight()
                    boolean r2 = org.telegram.messenger.SharedConfig.smoothKeyboard
                    r3 = 0
                    if (r2 == 0) goto L_0x0016
                    r2 = 0
                    goto L_0x0017
                L_0x0016:
                    r2 = r1
                L_0x0017:
                    r4 = 1101004800(0x41a00000, float:20.0)
                    int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    r7 = 1073741824(0x40000000, float:2.0)
                    if (r2 > r5) goto L_0x0043
                    boolean r2 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r2 != 0) goto L_0x0043
                    org.telegram.ui.PhotoPickerActivity r2 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r5 = r2.commentTextView
                    if (r5 == 0) goto L_0x0043
                    android.widget.FrameLayout r2 = r2.frameLayout2
                    android.view.ViewParent r2 = r2.getParent()
                    if (r2 != r12) goto L_0x0043
                    org.telegram.ui.PhotoPickerActivity r2 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r2 = r2.commentTextView
                    int r2 = r2.getEmojiPadding()
                    int r0 = r0 - r2
                    int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r7)
                    r9 = r0
                    r8 = r2
                    goto L_0x0045
                L_0x0043:
                    r8 = r14
                    r9 = r0
                L_0x0045:
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    if (r1 <= r0) goto L_0x0059
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r0 = r0.commentTextView
                    if (r0 == 0) goto L_0x0059
                    r1 = 1
                    r12.ignoreLayout = r1
                    r0.hideEmojiView()
                    r12.ignoreLayout = r3
                L_0x0059:
                    boolean r0 = org.telegram.messenger.SharedConfig.smoothKeyboard
                    if (r0 == 0) goto L_0x008c
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r0 = r0.commentTextView
                    if (r0 == 0) goto L_0x008c
                    boolean r0 = r0.isPopupShowing()
                    if (r0 == 0) goto L_0x008c
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    android.view.View r0 = r0.fragmentView
                    org.telegram.ui.PhotoPickerActivity r1 = org.telegram.ui.PhotoPickerActivity.this
                    int r1 = r1.getCurrentPanTranslationY()
                    float r1 = (float) r1
                    r0.setTranslationY(r1)
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.RecyclerListView r0 = r0.listView
                    r1 = 0
                    r0.setTranslationY(r1)
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.EmptyTextProgressView r0 = r0.emptyView
                    r0.setTranslationY(r1)
                L_0x008c:
                    int r10 = r12.getChildCount()
                    r11 = 0
                L_0x0091:
                    if (r11 >= r10) goto L_0x011b
                    android.view.View r1 = r12.getChildAt(r11)
                    if (r1 == 0) goto L_0x0117
                    int r0 = r1.getVisibility()
                    r2 = 8
                    if (r0 != r2) goto L_0x00a3
                    goto L_0x0117
                L_0x00a3:
                    org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r0 = r0.commentTextView
                    if (r0 == 0) goto L_0x010f
                    boolean r0 = r0.isPopupView(r1)
                    if (r0 == 0) goto L_0x010f
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r0 != 0) goto L_0x00cc
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r0 == 0) goto L_0x00ba
                    goto L_0x00cc
                L_0x00ba:
                    int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r7)
                    android.view.ViewGroup$LayoutParams r2 = r1.getLayoutParams()
                    int r2 = r2.height
                    int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7)
                    r1.measure(r0, r2)
                    goto L_0x0117
                L_0x00cc:
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r0 == 0) goto L_0x00fa
                    int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r7)
                    boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r2 == 0) goto L_0x00df
                    r2 = 1128792064(0x43480000, float:200.0)
                    goto L_0x00e1
                L_0x00df:
                    r2 = 1134559232(0x43a00000, float:320.0)
                L_0x00e1:
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r3 = r9 - r3
                    int r4 = r12.getPaddingTop()
                    int r3 = r3 + r4
                    int r2 = java.lang.Math.min(r2, r3)
                    int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7)
                    r1.measure(r0, r2)
                    goto L_0x0117
                L_0x00fa:
                    int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r7)
                    int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r2 = r9 - r2
                    int r3 = r12.getPaddingTop()
                    int r2 = r2 + r3
                    int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7)
                    r1.measure(r0, r2)
                    goto L_0x0117
                L_0x010f:
                    r3 = 0
                    r5 = 0
                    r0 = r12
                    r2 = r13
                    r4 = r8
                    r0.measureChildWithMargins(r1, r2, r3, r4, r5)
                L_0x0117:
                    int r11 = r11 + 1
                    goto L_0x0091
                L_0x011b:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity.AnonymousClass5.onMeasureInternal(int, int):void");
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:43:0x00c2  */
            /* JADX WARNING: Removed duplicated region for block: B:50:0x00dc  */
            /* JADX WARNING: Removed duplicated region for block: B:58:0x00fb  */
            /* JADX WARNING: Removed duplicated region for block: B:59:0x0104  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                    r9 = this;
                    int r10 = r9.lastNotifyWidth
                    int r13 = r13 - r11
                    if (r10 == r13) goto L_0x0035
                    r9.lastNotifyWidth = r13
                    org.telegram.ui.PhotoPickerActivity r10 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.PhotoPickerActivity$ListAdapter r10 = r10.listAdapter
                    if (r10 == 0) goto L_0x0018
                    org.telegram.ui.PhotoPickerActivity r10 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.PhotoPickerActivity$ListAdapter r10 = r10.listAdapter
                    r10.notifyDataSetChanged()
                L_0x0018:
                    org.telegram.ui.PhotoPickerActivity r10 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r10 = r10.sendPopupWindow
                    if (r10 == 0) goto L_0x0035
                    org.telegram.ui.PhotoPickerActivity r10 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r10 = r10.sendPopupWindow
                    boolean r10 = r10.isShowing()
                    if (r10 == 0) goto L_0x0035
                    org.telegram.ui.PhotoPickerActivity r10 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r10 = r10.sendPopupWindow
                    r10.dismiss()
                L_0x0035:
                    int r10 = r9.getChildCount()
                    boolean r11 = org.telegram.messenger.SharedConfig.smoothKeyboard
                    r0 = 0
                    if (r11 == 0) goto L_0x0040
                    r11 = 0
                    goto L_0x0044
                L_0x0040:
                    int r11 = r9.measureKeyboardHeight()
                L_0x0044:
                    org.telegram.ui.PhotoPickerActivity r1 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r2 = r1.commentTextView
                    if (r2 == 0) goto L_0x006d
                    android.widget.FrameLayout r1 = r1.frameLayout2
                    android.view.ViewParent r1 = r1.getParent()
                    if (r1 != r9) goto L_0x006d
                    r1 = 1101004800(0x41a00000, float:20.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    if (r11 > r1) goto L_0x006d
                    boolean r1 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r1 != 0) goto L_0x006d
                    boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r1 != 0) goto L_0x006d
                    org.telegram.ui.PhotoPickerActivity r1 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r1 = r1.commentTextView
                    int r1 = r1.getEmojiPadding()
                    goto L_0x006e
                L_0x006d:
                    r1 = 0
                L_0x006e:
                    r9.setBottomClip(r1)
                L_0x0071:
                    if (r0 >= r10) goto L_0x0117
                    android.view.View r2 = r9.getChildAt(r0)
                    int r3 = r2.getVisibility()
                    r4 = 8
                    if (r3 != r4) goto L_0x0081
                    goto L_0x0113
                L_0x0081:
                    android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
                    int r4 = r2.getMeasuredWidth()
                    int r5 = r2.getMeasuredHeight()
                    int r6 = r3.gravity
                    r7 = -1
                    if (r6 != r7) goto L_0x0096
                    r6 = 51
                L_0x0096:
                    r7 = r6 & 7
                    r6 = r6 & 112(0x70, float:1.57E-43)
                    r7 = r7 & 7
                    r8 = 1
                    if (r7 == r8) goto L_0x00b4
                    r8 = 5
                    if (r7 == r8) goto L_0x00aa
                    int r7 = r3.leftMargin
                    int r8 = r9.getPaddingLeft()
                    int r7 = r7 + r8
                    goto L_0x00be
                L_0x00aa:
                    int r7 = r13 - r4
                    int r8 = r3.rightMargin
                    int r7 = r7 - r8
                    int r8 = r9.getPaddingRight()
                    goto L_0x00bd
                L_0x00b4:
                    int r7 = r13 - r4
                    int r7 = r7 / 2
                    int r8 = r3.leftMargin
                    int r7 = r7 + r8
                    int r8 = r3.rightMargin
                L_0x00bd:
                    int r7 = r7 - r8
                L_0x00be:
                    r8 = 16
                    if (r6 == r8) goto L_0x00dc
                    r8 = 48
                    if (r6 == r8) goto L_0x00d4
                    r8 = 80
                    if (r6 == r8) goto L_0x00cd
                    int r3 = r3.topMargin
                    goto L_0x00e9
                L_0x00cd:
                    int r6 = r14 - r1
                    int r6 = r6 - r12
                    int r6 = r6 - r5
                    int r3 = r3.bottomMargin
                    goto L_0x00e7
                L_0x00d4:
                    int r3 = r3.topMargin
                    int r6 = r9.getPaddingTop()
                    int r3 = r3 + r6
                    goto L_0x00e9
                L_0x00dc:
                    int r6 = r14 - r1
                    int r6 = r6 - r12
                    int r6 = r6 - r5
                    int r6 = r6 / 2
                    int r8 = r3.topMargin
                    int r6 = r6 + r8
                    int r3 = r3.bottomMargin
                L_0x00e7:
                    int r3 = r6 - r3
                L_0x00e9:
                    org.telegram.ui.PhotoPickerActivity r6 = org.telegram.ui.PhotoPickerActivity.this
                    org.telegram.ui.Components.EditTextEmoji r6 = r6.commentTextView
                    if (r6 == 0) goto L_0x010e
                    boolean r6 = r6.isPopupView(r2)
                    if (r6 == 0) goto L_0x010e
                    boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r3 == 0) goto L_0x0104
                    int r3 = r9.getMeasuredHeight()
                    int r6 = r2.getMeasuredHeight()
                    goto L_0x010d
                L_0x0104:
                    int r3 = r9.getMeasuredHeight()
                    int r3 = r3 + r11
                    int r6 = r2.getMeasuredHeight()
                L_0x010d:
                    int r3 = r3 - r6
                L_0x010e:
                    int r4 = r4 + r7
                    int r5 = r5 + r3
                    r2.layout(r7, r3, r4, r5)
                L_0x0113:
                    int r0 = r0 + 1
                    goto L_0x0071
                L_0x0117:
                    r9.notifyHeightChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity.AnonymousClass5.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout = r3;
        r3.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.fragmentView = this.sizeNotifierFrameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass6 r6 = new GridLayoutManager(this, context2, 4) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r6;
        recyclerListView2.setLayoutManager(r6);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                if (PhotoPickerActivity.this.listAdapter.getItemViewType(i) == 1 || PhotoPickerActivity.this.listSort || (PhotoPickerActivity.this.selectedAlbum == null && TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString))) {
                    return PhotoPickerActivity.this.layoutManager.getSpanCount();
                }
                return PhotoPickerActivity.this.itemSize + (i % PhotoPickerActivity.this.itemsPerRow != PhotoPickerActivity.this.itemsPerRow - 1 ? AndroidUtilities.dp(5.0f) : 0);
            }
        });
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView3.setAdapter(listAdapter2);
        this.listView.setGlowColor(Theme.getColor("dialogBackground"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PhotoPickerActivity.this.lambda$createView$1$PhotoPickerActivity(view, i);
            }
        });
        if (this.maxSelectedPhotos != 1) {
            this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
                public final boolean onItemClick(View view, int i) {
                    return PhotoPickerActivity.this.lambda$createView$2$PhotoPickerActivity(view, i);
                }
            });
        }
        RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = new RecyclerViewItemRangeSelector(new RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate() {
            public void setSelected(View view, int i, boolean z) {
                if (z == PhotoPickerActivity.this.shouldSelect && (view instanceof PhotoAttachPhotoCell)) {
                    ((PhotoAttachPhotoCell) view).callDelegate();
                }
            }

            public boolean isSelected(int i) {
                Object obj;
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    obj = Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(i).imageId);
                } else {
                    obj = ((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i)).id;
                }
                return PhotoPickerActivity.this.selectedPhotos.containsKey(obj);
            }

            public boolean isIndexSelectable(int i) {
                return PhotoPickerActivity.this.listAdapter.getItemViewType(i) == 0;
            }

            public void onStartStopSelection(boolean z) {
                int unused = PhotoPickerActivity.this.alertOnlyOnce = z ? 1 : 0;
                if (z) {
                    PhotoPickerActivity.this.parentLayout.requestDisallowInterceptTouchEvent(true);
                }
                PhotoPickerActivity.this.listView.hideSelector(true);
            }
        });
        this.itemRangeSelector = recyclerViewItemRangeSelector;
        if (this.maxSelectedPhotos != 1) {
            this.listView.addOnItemTouchListener(recyclerViewItemRangeSelector);
        }
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setTextColor(-7104099);
        this.emptyView.setProgressBarColor(-11371101);
        if (this.selectedAlbum != null) {
            this.emptyView.setShowAtCenter(false);
            this.emptyView.setText(LocaleController.getString("NoPhotos", NUM));
        } else {
            this.emptyView.setShowAtTop(true);
            this.emptyView.setPadding(0, AndroidUtilities.dp(200.0f), 0, 0);
            this.emptyView.setText(LocaleController.getString("NoRecentSearches", NUM));
        }
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL ? 0.0f : 48.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int i3;
                if (PhotoPickerActivity.this.selectedAlbum == null) {
                    int findFirstVisibleItemPosition = PhotoPickerActivity.this.layoutManager.findFirstVisibleItemPosition();
                    boolean z = false;
                    if (findFirstVisibleItemPosition == -1) {
                        i3 = 0;
                    } else {
                        i3 = Math.abs(PhotoPickerActivity.this.layoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                    }
                    if (i3 > 0) {
                        int itemCount = PhotoPickerActivity.this.layoutManager.getItemCount();
                        if (i3 != 0 && findFirstVisibleItemPosition + i3 > itemCount - 2 && !PhotoPickerActivity.this.searching && !PhotoPickerActivity.this.imageSearchEndReached) {
                            PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                            if (photoPickerActivity.type == 1) {
                                z = true;
                            }
                            photoPickerActivity.searchImages(z, PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.nextImagesSearchOffset, true);
                        }
                    }
                }
            }
        });
        if (this.selectedAlbum == null) {
            updateSearchInterface();
        }
        if (this.needsBottomLayout) {
            View view = new View(context2);
            this.shadow = view;
            view.setBackgroundResource(NUM);
            this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            FrameLayout frameLayout = new FrameLayout(context2);
            this.frameLayout2 = frameLayout;
            frameLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
            this.frameLayout2.setVisibility(4);
            this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
            this.frameLayout2.setOnTouchListener($$Lambda$PhotoPickerActivity$UEkfqvbu1_SVwnH7n9JhVVbNS4o.INSTANCE);
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                editTextEmoji.onDestroy();
            }
            this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, (BaseFragment) null, 1);
            this.commentTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
            this.commentTextView.setHint(LocaleController.getString("AddCaption", NUM));
            this.commentTextView.onResume();
            EditTextBoldCursor editText = this.commentTextView.getEditText();
            editText.setMaxLines(1);
            editText.setSingleLine(true);
            this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
            CharSequence charSequence = this.caption;
            if (charSequence != null) {
                this.commentTextView.setText(charSequence);
            }
            this.commentTextView.getEditText().addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (PhotoPickerActivity.this.delegate != null) {
                        PhotoPickerActivity.this.delegate.onCaptionChanged(editable);
                    }
                }
            });
            AnonymousClass11 r32 = new FrameLayout(context2) {
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                    accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrSendPhotos", PhotoPickerActivity.this.selectedPhotos.size()));
                    accessibilityNodeInfo.setClassName(Button.class.getName());
                    accessibilityNodeInfo.setLongClickable(true);
                    accessibilityNodeInfo.setClickable(true);
                }
            };
            this.writeButtonContainer = r32;
            r32.setFocusable(true);
            this.writeButtonContainer.setFocusableInTouchMode(true);
            this.writeButtonContainer.setVisibility(4);
            this.writeButtonContainer.setScaleX(0.2f);
            this.writeButtonContainer.setScaleY(0.2f);
            this.writeButtonContainer.setAlpha(0.0f);
            this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
            this.writeButton = new ImageView(context2);
            int dp = AndroidUtilities.dp(56.0f);
            String str = "dialogFloatingButton";
            int color = Theme.getColor(str);
            if (Build.VERSION.SDK_INT >= 21) {
                str = "dialogFloatingButtonPressed";
            }
            this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(dp, color, Theme.getColor(str));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable mutate = context.getResources().getDrawable(NUM).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, this.writeButtonDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                this.writeButtonDrawable = combinedDrawable;
            }
            this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
            this.writeButton.setImageResource(NUM);
            this.writeButton.setImportantForAccessibility(2);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
            this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                this.writeButton.setOutlineProvider(new ViewOutlineProvider(this) {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
            this.writeButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoPickerActivity.this.lambda$createView$4$PhotoPickerActivity(view);
                }
            });
            this.writeButton.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return PhotoPickerActivity.this.lambda$createView$7$PhotoPickerActivity(view);
                }
            });
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            AnonymousClass14 r33 = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, PhotoPickerActivity.this.selectedPhotosOrder.size()))});
                    int ceil = (int) Math.ceil((double) PhotoPickerActivity.this.textPaint.measureText(format));
                    int max = Math.max(AndroidUtilities.dp(16.0f) + ceil, AndroidUtilities.dp(24.0f));
                    int measuredWidth = getMeasuredWidth() / 2;
                    int measuredHeight = getMeasuredHeight() / 2;
                    PhotoPickerActivity.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor("dialogBackground"));
                    int i = max / 2;
                    int i2 = measuredWidth - i;
                    int i3 = i + measuredWidth;
                    PhotoPickerActivity.this.rect.set((float) i2, 0.0f, (float) i3, (float) getMeasuredHeight());
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), PhotoPickerActivity.this.paint);
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                    PhotoPickerActivity.this.rect.set((float) (i2 + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (i3 - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), PhotoPickerActivity.this.paint);
                    canvas.drawText(format, (float) (measuredWidth - (ceil / 2)), (float) AndroidUtilities.dp(16.2f), PhotoPickerActivity.this.textPaint);
                }
            };
            this.selectedCountView = r33;
            r33.setAlpha(0.0f);
            this.selectedCountView.setScaleX(0.2f);
            this.selectedCountView.setScaleY(0.2f);
            this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
            if (this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL) {
                this.commentTextView.setVisibility(8);
            }
        }
        if (!(this.selectedAlbum != null || (i = this.type) == 0 || i == 1) || !this.allowOrder) {
            z = false;
        }
        this.allowIndices = z;
        this.listView.setEmptyView(this.emptyView);
        updatePhotosButton(0);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$PhotoPickerActivity(View view, int i) {
        ArrayList arrayList;
        int i2;
        if (this.selectedAlbum != null || !this.searchResult.isEmpty()) {
            MediaController.AlbumEntry albumEntry = this.selectedAlbum;
            if (albumEntry != null) {
                arrayList = albumEntry.photos;
            } else {
                arrayList = this.searchResult;
            }
            ArrayList arrayList2 = arrayList;
            if (i >= 0 && i < arrayList2.size()) {
                ActionBarMenuItem actionBarMenuItem = this.searchItem;
                if (actionBarMenuItem != null) {
                    AndroidUtilities.hideKeyboard(actionBarMenuItem.getSearchField());
                }
                if (this.listSort) {
                    onListItemClick(view, arrayList2.get(i));
                    return;
                }
                int i3 = this.selectPhotoType;
                if (i3 == PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR || i3 == PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR_VIDEO) {
                    i2 = 1;
                } else if (i3 == PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER) {
                    i2 = 3;
                } else {
                    i2 = i3 == PhotoAlbumPickerActivity.SELECT_TYPE_QR ? 10 : this.chatActivity == null ? 4 : 0;
                }
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
                PhotoViewer.getInstance().openPhotoForSelect(arrayList2, i, i2, this.isDocumentsPicker, this.provider, this.chatActivity);
            }
        } else if (i < this.recentSearches.size()) {
            String str = this.recentSearches.get(i);
            PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate = this.searchDelegate;
            if (photoPickerActivitySearchDelegate != null) {
                photoPickerActivitySearchDelegate.shouldSearchText(str);
                return;
            }
            this.searchItem.getSearchField().setText(str);
            this.searchItem.getSearchField().setSelection(str.length());
            processSearch(this.searchItem.getSearchField());
        } else if (i == this.recentSearches.size() + 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("ClearSearchAlertTitle", NUM));
            builder.setMessage(LocaleController.getString("ClearSearchAlert", NUM));
            builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PhotoPickerActivity.this.lambda$null$0$PhotoPickerActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    public /* synthetic */ void lambda$null$0$PhotoPickerActivity(DialogInterface dialogInterface, int i) {
        PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate = this.searchDelegate;
        if (photoPickerActivitySearchDelegate != null) {
            photoPickerActivitySearchDelegate.shouldClearRecentSearch();
        } else {
            clearRecentSearch();
        }
    }

    public /* synthetic */ boolean lambda$createView$2$PhotoPickerActivity(View view, int i) {
        if (this.listSort) {
            onListItemClick(view, this.selectedAlbum.photos.get(i));
            return true;
        } else if (!(view instanceof PhotoAttachPhotoCell)) {
            return false;
        } else {
            RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
            boolean z = !((PhotoAttachPhotoCell) view).isChecked();
            this.shouldSelect = z;
            recyclerViewItemRangeSelector.setIsActive(view, true, i, z);
            return false;
        }
    }

    public /* synthetic */ void lambda$createView$4$PhotoPickerActivity(View view) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
            sendSelectedPhotos(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    PhotoPickerActivity.this.sendSelectedPhotos(z, i);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$createView$7$PhotoPickerActivity(View view) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (!(chatActivity2 == null || this.maxSelectedPhotos == 1)) {
            chatActivity2.getCurrentChat();
            TLRPC$User currentUser = this.chatActivity.getCurrentUser();
            if (this.chatActivity.getCurrentEncryptedChat() != null) {
                return false;
            }
            if (this.sendPopupLayout == null) {
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity());
                this.sendPopupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setAnimationEnabled(false);
                this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                    private Rect popupRect = new Rect();

                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() != 0 || PhotoPickerActivity.this.sendPopupWindow == null || !PhotoPickerActivity.this.sendPopupWindow.isShowing()) {
                            return false;
                        }
                        view.getHitRect(this.popupRect);
                        if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            return false;
                        }
                        PhotoPickerActivity.this.sendPopupWindow.dismiss();
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        PhotoPickerActivity.this.lambda$null$5$PhotoPickerActivity(keyEvent);
                    }
                });
                this.sendPopupLayout.setShowedFromBotton(false);
                this.itemCells = new ActionBarMenuSubItem[2];
                for (int i = 0; i < 2; i++) {
                    if (i != 1 || !UserObject.isUserSelf(currentUser)) {
                        this.itemCells[i] = new ActionBarMenuSubItem(getParentActivity());
                        if (i == 0) {
                            if (UserObject.isUserSelf(currentUser)) {
                                this.itemCells[i].setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                            } else {
                                this.itemCells[i].setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                            }
                        } else if (i == 1) {
                            this.itemCells[i].setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                        }
                        this.itemCells[i].setMinimumWidth(AndroidUtilities.dp(196.0f));
                        this.sendPopupLayout.addView(this.itemCells[i], LayoutHelper.createLinear(-1, 48));
                        this.itemCells[i].setOnClickListener(new View.OnClickListener(i) {
                            public final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                PhotoPickerActivity.this.lambda$null$6$PhotoPickerActivity(this.f$1, view);
                            }
                        });
                    }
                }
                this.sendPopupLayout.setupRadialSelectors(Theme.getColor("dialogButtonSelector"));
                ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
                this.sendPopupWindow = actionBarPopupWindow;
                actionBarPopupWindow.setAnimationEnabled(false);
                this.sendPopupWindow.setAnimationStyle(NUM);
                this.sendPopupWindow.setOutsideTouchable(true);
                this.sendPopupWindow.setClippingEnabled(true);
                this.sendPopupWindow.setInputMethodMode(2);
                this.sendPopupWindow.setSoftInputMode(0);
                this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
            }
            this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.sendPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            this.sendPopupWindow.showAtLocation(view, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
            this.sendPopupWindow.dimBehind();
            view.performHapticFeedback(3, 2);
        }
        return false;
    }

    public /* synthetic */ void lambda$null$5$PhotoPickerActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$6$PhotoPickerActivity(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    PhotoPickerActivity.this.sendSelectedPhotos(z, i);
                }
            });
        } else if (i == 1) {
            sendSelectedPhotos(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(int i) {
        if (this.listView != null) {
            if (this.commentTextView.isPopupShowing()) {
                this.fragmentView.setTranslationY((float) i);
                this.listView.setTranslationY(0.0f);
                this.emptyView.setTranslationY(0.0f);
                return;
            }
            float f = (float) i;
            this.listView.setTranslationY(f);
            this.emptyView.setTranslationY(f);
        }
    }

    public void setLayoutViews(FrameLayout frameLayout, FrameLayout frameLayout3, View view, View view2, EditTextEmoji editTextEmoji) {
        this.frameLayout2 = frameLayout;
        this.writeButtonContainer = frameLayout3;
        this.commentTextView = editTextEmoji;
        this.selectedCountView = view;
        this.shadow = view2;
        this.needsBottomLayout = false;
    }

    private void applyCaption() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null && editTextEmoji.length() > 0) {
            Object obj = this.selectedPhotos.get(this.selectedPhotosOrder.get(0));
            if (obj instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) obj).caption = this.commentTextView.getText().toString();
            } else if (obj instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) obj).caption = this.commentTextView.getText().toString();
            }
        }
    }

    private void onListItemClick(View view, Object obj) {
        boolean z = false;
        int i = 1;
        boolean z2 = addToSelectedPhotos(obj, -1) == -1;
        if (view instanceof SharedDocumentCell) {
            SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
            if (this.selectedPhotosOrder.indexOf(Integer.valueOf(this.selectedAlbum.photos.get(((Integer) view.getTag()).intValue()).imageId)) >= 0) {
                z = true;
            }
            sharedDocumentCell.setChecked(z, true);
        }
        if (!z2) {
            i = 2;
        }
        updatePhotosButton(i);
        this.delegate.selectedPhotosChanged();
    }

    public void clearRecentSearch() {
        this.recentSearches.clear();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        saveRecentSearch();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.openSearch(true);
            if (!TextUtils.isEmpty(this.initialSearchString)) {
                this.searchItem.setSearchFieldText(this.initialSearchString, false);
                this.initialSearchString = null;
                processSearch(this.searchItem.getSearchField());
            }
            getParentActivity().getWindow().setSoftInputMode(SharedConfig.smoothKeyboard ? 32 : 16);
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    public void setCaption(CharSequence charSequence) {
        this.caption = charSequence;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.setText(charSequence);
        }
    }

    public void setInitialSearchString(String str) {
        this.initialSearchString = str;
    }

    private void saveRecentSearch() {
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("web_recent_search", 0).edit();
        edit.clear();
        edit.putInt("count", this.recentSearches.size());
        int size = this.recentSearches.size();
        for (int i = 0; i < size; i++) {
            edit.putString("recent" + i, this.recentSearches.get(i));
        }
        edit.commit();
    }

    private void loadRecentSearch() {
        int i = 0;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("web_recent_search", 0);
        int i2 = sharedPreferences.getInt("count", 0);
        while (i < i2) {
            String string = sharedPreferences.getString("recent" + i, (String) null);
            if (string != null) {
                this.recentSearches.add(string);
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void processSearch(EditText editText) {
        if (editText.getText().length() != 0) {
            String obj = editText.getText().toString();
            int size = this.recentSearches.size();
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                } else if (this.recentSearches.get(i).equalsIgnoreCase(obj)) {
                    this.recentSearches.remove(i);
                    break;
                } else {
                    i++;
                }
            }
            this.recentSearches.add(0, obj);
            while (this.recentSearches.size() > 20) {
                ArrayList<String> arrayList = this.recentSearches;
                arrayList.remove(arrayList.size() - 1);
            }
            saveRecentSearch();
            this.searchResult.clear();
            this.searchResultKeys.clear();
            this.imageSearchEndReached = true;
            if (this.type == 1) {
                z = true;
            }
            searchImages(z, obj, "", true);
            this.lastSearchString = obj;
            if (obj.length() == 0) {
                this.lastSearchString = null;
                this.emptyView.setText(LocaleController.getString("NoRecentSearches", NUM));
            } else {
                this.emptyView.setText(LocaleController.getString("NoResult", NUM));
            }
            updateSearchInterface();
        }
    }

    private boolean showCommentTextView(final boolean z, boolean z2) {
        if (this.commentTextView == null) {
            return false;
        }
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.frameLayout2.setTag(z ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        float f = 0.0f;
        float f2 = 0.2f;
        float f3 = 1.0f;
        if (z2) {
            this.animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            FrameLayout frameLayout = this.writeButtonContainer;
            Property property = View.SCALE_X;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout3, property2, fArr2));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout4, property3, fArr3));
            View view = this.selectedCountView;
            Property property4 = View.SCALE_X;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(view, property4, fArr4));
            View view2 = this.selectedCountView;
            Property property5 = View.SCALE_Y;
            float[] fArr5 = new float[1];
            if (z) {
                f2 = 1.0f;
            }
            fArr5[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(view2, property5, fArr5));
            View view3 = this.selectedCountView;
            Property property6 = View.ALPHA;
            float[] fArr6 = new float[1];
            if (!z) {
                f3 = 0.0f;
            }
            fArr6[0] = f3;
            arrayList.add(ObjectAnimator.ofFloat(view3, property6, fArr6));
            FrameLayout frameLayout5 = this.frameLayout2;
            Property property7 = View.TRANSLATION_Y;
            float[] fArr7 = new float[1];
            fArr7[0] = z ? 0.0f : (float) AndroidUtilities.dp(48.0f);
            arrayList.add(ObjectAnimator.ofFloat(frameLayout5, property7, fArr7));
            View view4 = this.shadow;
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            fArr8[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
            this.animatorSet.playTogether(arrayList);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(PhotoPickerActivity.this.animatorSet)) {
                        if (!z) {
                            PhotoPickerActivity.this.frameLayout2.setVisibility(4);
                            PhotoPickerActivity.this.writeButtonContainer.setVisibility(4);
                        }
                        AnimatorSet unused = PhotoPickerActivity.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(PhotoPickerActivity.this.animatorSet)) {
                        AnimatorSet unused = PhotoPickerActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
            View view5 = this.selectedCountView;
            if (z) {
                f2 = 1.0f;
            }
            view5.setScaleY(f2);
            View view6 = this.selectedCountView;
            if (!z) {
                f3 = 0.0f;
            }
            view6.setAlpha(f3);
            this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
            View view7 = this.shadow;
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            view7.setTranslationY(f);
            if (!z) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        this.maxSelectedPhotos = i;
        this.allowOrder = z;
        if (i > 0 && this.type == 1) {
            this.maxSelectedPhotos = 1;
        }
    }

    /* access modifiers changed from: private */
    public void updateCheckedPhotoIndices() {
        if (this.allowIndices) {
            int childCount = this.listView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    Integer num = (Integer) childAt.getTag();
                    MediaController.AlbumEntry albumEntry = this.selectedAlbum;
                    int i2 = -1;
                    if (albumEntry != null) {
                        MediaController.PhotoEntry photoEntry = albumEntry.photos.get(num.intValue());
                        if (this.allowIndices) {
                            i2 = this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        }
                        photoAttachPhotoCell.setNum(i2);
                    } else {
                        MediaController.SearchImage searchImage = this.searchResult.get(num.intValue());
                        if (this.allowIndices) {
                            i2 = this.selectedPhotosOrder.indexOf(searchImage.id);
                        }
                        photoAttachPhotoCell.setNum(i2);
                    }
                } else if (childAt instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) childAt).setChecked(this.selectedPhotosOrder.indexOf(Integer.valueOf(this.selectedAlbum.photos.get(((Integer) childAt.getTag()).intValue()).imageId)) != 0, false);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public PhotoAttachPhotoCell getCellForIndex(int i) {
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                int intValue = ((Integer) photoAttachPhotoCell.getTag()).intValue();
                MediaController.AlbumEntry albumEntry = this.selectedAlbum;
                if (albumEntry == null ? !(intValue < 0 || intValue >= this.searchResult.size()) : !(intValue < 0 || intValue >= albumEntry.photos.size())) {
                    if (intValue == i) {
                        return photoAttachPhotoCell;
                    }
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public int addToSelectedPhotos(Object obj, int i) {
        Object obj2;
        boolean z = obj instanceof MediaController.PhotoEntry;
        if (z) {
            obj2 = Integer.valueOf(((MediaController.PhotoEntry) obj).imageId);
        } else {
            obj2 = obj instanceof MediaController.SearchImage ? ((MediaController.SearchImage) obj).id : null;
        }
        if (obj2 == null) {
            return -1;
        }
        if (this.selectedPhotos.containsKey(obj2)) {
            this.selectedPhotos.remove(obj2);
            int indexOf = this.selectedPhotosOrder.indexOf(obj2);
            if (indexOf >= 0) {
                this.selectedPhotosOrder.remove(indexOf);
            }
            if (this.allowIndices) {
                updateCheckedPhotoIndices();
            }
            if (i >= 0) {
                if (z) {
                    ((MediaController.PhotoEntry) obj).reset();
                } else if (obj instanceof MediaController.SearchImage) {
                    ((MediaController.SearchImage) obj).reset();
                }
                this.provider.updatePhotoAtIndex(i);
            }
            return indexOf;
        }
        this.selectedPhotos.put(obj2, obj);
        this.selectedPhotosOrder.add(obj2);
        return -1;
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        ActionBarMenuItem actionBarMenuItem;
        if (z && (actionBarMenuItem = this.searchItem) != null) {
            AndroidUtilities.showKeyboard(actionBarMenuItem.getSearchField());
        }
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return super.onBackPressed();
        }
        this.commentTextView.hidePopup(true);
        return false;
    }

    public void updatePhotosButton(int i) {
        boolean z = true;
        if (this.selectedPhotos.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            if (i == 0) {
                z = false;
            }
            showCommentTextView(false, z);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true, i != 0) || i == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX((float) AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY((float) AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet2 = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = i == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        View view2 = this.selectedCountView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (i != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
        animatorSet2.playTogether(animatorArr);
        animatorSet2.setInterpolator(new OvershootInterpolator());
        animatorSet2.setDuration(180);
        animatorSet2.start();
    }

    /* access modifiers changed from: private */
    public void updateSearchInterface() {
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if (!this.searching || !this.searchResult.isEmpty()) {
            this.emptyView.showTextView();
        } else {
            this.emptyView.showProgress();
        }
    }

    private void searchBotUser(boolean z) {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            tLRPC$TL_contacts_resolveUsername.username = z ? instance.gifSearchBot : instance.imageSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PhotoPickerActivity.this.lambda$searchBotUser$9$PhotoPickerActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$searchBotUser$9$PhotoPickerActivity(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject, z) {
                public final /* synthetic */ TLObject f$1;
                public final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PhotoPickerActivity.this.lambda$null$8$PhotoPickerActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$8$PhotoPickerActivity(TLObject tLObject, boolean z) {
        TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_contacts_resolvedPeer.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, tLRPC$TL_contacts_resolvedPeer.chats, true, true);
        String str = this.lastSearchImageString;
        this.lastSearchImageString = null;
        searchImages(z, str, "", false);
    }

    /* access modifiers changed from: private */
    public void searchImages(boolean z, String str, String str2, boolean z2) {
        if (this.searching) {
            this.searching = false;
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }
        this.lastSearchImageString = str;
        this.searching = true;
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        MessagesController instance2 = MessagesController.getInstance(this.currentAccount);
        TLObject userOrChat = instance.getUserOrChat(z ? instance2.gifSearchBot : instance2.imageSearchBot);
        if (userOrChat instanceof TLRPC$User) {
            TLRPC$User tLRPC$User = (TLRPC$User) userOrChat;
            TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
            if (str == null) {
                str = "";
            }
            tLRPC$TL_messages_getInlineBotResults.query = str;
            tLRPC$TL_messages_getInlineBotResults.bot = MessagesController.getInstance(this.currentAccount).getInputUser(tLRPC$User);
            tLRPC$TL_messages_getInlineBotResults.offset = str2;
            ChatActivity chatActivity2 = this.chatActivity;
            if (chatActivity2 != null) {
                int dialogId = (int) chatActivity2.getDialogId();
                if (dialogId != 0) {
                    tLRPC$TL_messages_getInlineBotResults.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(dialogId);
                } else {
                    tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
                }
            } else {
                tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
            }
            int i = this.lastSearchToken + 1;
            this.lastSearchToken = i;
            this.imageReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, new RequestDelegate(i, z, tLRPC$User) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ boolean f$2;
                public final /* synthetic */ TLRPC$User f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PhotoPickerActivity.this.lambda$searchImages$11$PhotoPickerActivity(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                }
            });
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.imageReqId, this.classGuid);
        } else if (z2) {
            searchBotUser(z);
        }
    }

    public /* synthetic */ void lambda$searchImages$11$PhotoPickerActivity(int i, boolean z, TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(i, tLObject, z, tLRPC$User) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ boolean f$3;
            public final /* synthetic */ TLRPC$User f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                PhotoPickerActivity.this.lambda$null$10$PhotoPickerActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$10$PhotoPickerActivity(int i, TLObject tLObject, boolean z, TLRPC$User tLRPC$User) {
        int i2;
        int i3;
        TLRPC$Photo tLRPC$Photo;
        TLRPC$PhotoSize closestPhotoSizeWithSize;
        TLRPC$DocumentAttribute tLRPC$DocumentAttribute;
        if (i == this.lastSearchToken) {
            int size = this.searchResult.size();
            if (tLObject != null) {
                TLRPC$messages_BotResults tLRPC$messages_BotResults = (TLRPC$messages_BotResults) tLObject;
                this.nextImagesSearchOffset = tLRPC$messages_BotResults.next_offset;
                int size2 = tLRPC$messages_BotResults.results.size();
                i2 = 0;
                for (int i4 = 0; i4 < size2; i4++) {
                    TLRPC$BotInlineResult tLRPC$BotInlineResult = tLRPC$messages_BotResults.results.get(i4);
                    if ((z || "photo".equals(tLRPC$BotInlineResult.type)) && ((!z || "gif".equals(tLRPC$BotInlineResult.type)) && !this.searchResultKeys.containsKey(tLRPC$BotInlineResult.id))) {
                        MediaController.SearchImage searchImage = new MediaController.SearchImage();
                        if (z && tLRPC$BotInlineResult.document != null) {
                            int i5 = 0;
                            while (true) {
                                if (i5 >= tLRPC$BotInlineResult.document.attributes.size()) {
                                    break;
                                }
                                tLRPC$DocumentAttribute = tLRPC$BotInlineResult.document.attributes.get(i5);
                                if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo)) {
                                    searchImage.width = tLRPC$DocumentAttribute.w;
                                    searchImage.height = tLRPC$DocumentAttribute.h;
                                } else {
                                    i5++;
                                }
                            }
                            searchImage.width = tLRPC$DocumentAttribute.w;
                            searchImage.height = tLRPC$DocumentAttribute.h;
                            TLRPC$Document tLRPC$Document = tLRPC$BotInlineResult.document;
                            searchImage.document = tLRPC$Document;
                            searchImage.size = 0;
                            TLRPC$Photo tLRPC$Photo2 = tLRPC$BotInlineResult.photo;
                            if (!(tLRPC$Photo2 == null || tLRPC$Document == null || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo2.sizes, this.itemSize, true)) == null)) {
                                tLRPC$BotInlineResult.document.thumbs.add(closestPhotoSizeWithSize);
                                tLRPC$BotInlineResult.document.flags |= 1;
                            }
                        } else if (!z && (tLRPC$Photo = tLRPC$BotInlineResult.photo) != null) {
                            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, AndroidUtilities.getPhotoSize());
                            TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$BotInlineResult.photo.sizes, 320);
                            if (closestPhotoSizeWithSize2 != null) {
                                searchImage.width = closestPhotoSizeWithSize2.w;
                                searchImage.height = closestPhotoSizeWithSize2.h;
                                searchImage.photoSize = closestPhotoSizeWithSize2;
                                searchImage.photo = tLRPC$BotInlineResult.photo;
                                searchImage.size = closestPhotoSizeWithSize2.size;
                                searchImage.thumbPhotoSize = closestPhotoSizeWithSize3;
                            }
                        } else if (tLRPC$BotInlineResult.content != null) {
                            int i6 = 0;
                            while (true) {
                                if (i6 >= tLRPC$BotInlineResult.content.attributes.size()) {
                                    break;
                                }
                                TLRPC$DocumentAttribute tLRPC$DocumentAttribute2 = tLRPC$BotInlineResult.content.attributes.get(i6);
                                if (tLRPC$DocumentAttribute2 instanceof TLRPC$TL_documentAttributeImageSize) {
                                    searchImage.width = tLRPC$DocumentAttribute2.w;
                                    searchImage.height = tLRPC$DocumentAttribute2.h;
                                    break;
                                }
                                i6++;
                            }
                            TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult.thumb;
                            if (tLRPC$WebDocument != null) {
                                searchImage.thumbUrl = tLRPC$WebDocument.url;
                            } else {
                                searchImage.thumbUrl = null;
                            }
                            TLRPC$WebDocument tLRPC$WebDocument2 = tLRPC$BotInlineResult.content;
                            searchImage.imageUrl = tLRPC$WebDocument2.url;
                            if (z) {
                                i3 = 0;
                            } else {
                                i3 = tLRPC$WebDocument2.size;
                            }
                            searchImage.size = i3;
                        }
                        searchImage.id = tLRPC$BotInlineResult.id;
                        searchImage.type = z ? 1 : 0;
                        searchImage.inlineResult = tLRPC$BotInlineResult;
                        HashMap<String, String> hashMap = new HashMap<>();
                        searchImage.params = hashMap;
                        hashMap.put("id", tLRPC$BotInlineResult.id);
                        searchImage.params.put("query_id", "" + tLRPC$messages_BotResults.query_id);
                        searchImage.params.put("bot_name", tLRPC$User.username);
                        this.searchResult.add(searchImage);
                        this.searchResultKeys.put(searchImage.id, searchImage);
                        i2++;
                    }
                }
                this.imageSearchEndReached = size == this.searchResult.size() || this.nextImagesSearchOffset == null;
            } else {
                i2 = 0;
            }
            this.searching = false;
            if (i2 != 0) {
                this.listAdapter.notifyItemRangeInserted(size, i2);
            } else if (this.imageSearchEndReached) {
                this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
            }
            if (!this.searching || !this.searchResult.isEmpty()) {
                this.emptyView.showTextView();
            } else {
                this.emptyView.showProgress();
            }
        }
    }

    public void setDelegate(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
        this.delegate = photoPickerActivityDelegate;
    }

    public void setSearchDelegate(PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate) {
        this.searchDelegate = photoPickerActivitySearchDelegate;
    }

    /* access modifiers changed from: private */
    public void sendSelectedPhotos(boolean z, int i) {
        if (!this.selectedPhotos.isEmpty() && this.delegate != null && !this.sendPressed) {
            applyCaption();
            this.sendPressed = true;
            this.delegate.actionButtonPressed(false, z, i);
            if (this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER) {
                finishFragment();
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return true;
            }
            if (TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString)) {
                if (viewHolder.getItemViewType() == 3) {
                    return true;
                }
                return false;
            } else if (viewHolder.getAdapterPosition() < PhotoPickerActivity.this.searchResult.size()) {
                return true;
            } else {
                return false;
            }
        }

        public int getItemCount() {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return PhotoPickerActivity.this.selectedAlbum.photos.size();
            }
            if (!PhotoPickerActivity.this.searchResult.isEmpty()) {
                return PhotoPickerActivity.this.searchResult.size() + (PhotoPickerActivity.this.imageSearchEndReached ^ true ? 1 : 0);
            }
            if (!TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString) || PhotoPickerActivity.this.recentSearches.isEmpty()) {
                return 0;
            }
            return PhotoPickerActivity.this.recentSearches.size() + 2;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.ui.Cells.DividerCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.ui.Cells.DividerCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: android.widget.FrameLayout} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: android.widget.FrameLayout} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: org.telegram.ui.Cells.PhotoAttachPhotoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: org.telegram.ui.Cells.DividerCell} */
        /* JADX WARNING: type inference failed for: r6v5 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                if (r6 == 0) goto L_0x0056
                r5 = -2
                r0 = -1
                r1 = 1
                if (r6 == r1) goto L_0x002f
                r2 = 2
                if (r6 == r2) goto L_0x0027
                r2 = 3
                if (r6 == r2) goto L_0x0015
                org.telegram.ui.Cells.DividerCell r5 = new org.telegram.ui.Cells.DividerCell
                android.content.Context r6 = r4.mContext
                r5.<init>(r6)
                goto L_0x007a
            L_0x0015:
                org.telegram.ui.Cells.TextCell r6 = new org.telegram.ui.Cells.TextCell
                android.content.Context r2 = r4.mContext
                r3 = 23
                r6.<init>(r2, r3, r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1.<init>((int) r0, (int) r5)
                r6.setLayoutParams(r1)
                goto L_0x0054
            L_0x0027:
                org.telegram.ui.Cells.SharedDocumentCell r5 = new org.telegram.ui.Cells.SharedDocumentCell
                android.content.Context r6 = r4.mContext
                r5.<init>(r6, r1)
                goto L_0x007a
            L_0x002f:
                android.widget.FrameLayout r6 = new android.widget.FrameLayout
                android.content.Context r1 = r4.mContext
                r6.<init>(r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1.<init>((int) r0, (int) r5)
                r6.setLayoutParams(r1)
                org.telegram.ui.Components.RadialProgressView r5 = new org.telegram.ui.Components.RadialProgressView
                android.content.Context r1 = r4.mContext
                r5.<init>(r1)
                r1 = -11371101(0xfffffffffvar_da3, float:-2.7979022E38)
                r5.setProgressColor(r1)
                r1 = -1082130432(0xffffffffbvar_, float:-1.0)
                android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r1)
                r6.addView(r5, r0)
            L_0x0054:
                r5 = r6
                goto L_0x007a
            L_0x0056:
                org.telegram.ui.Cells.PhotoAttachPhotoCell r5 = new org.telegram.ui.Cells.PhotoAttachPhotoCell
                android.content.Context r6 = r4.mContext
                r5.<init>(r6)
                org.telegram.ui.PhotoPickerActivity$ListAdapter$1 r6 = new org.telegram.ui.PhotoPickerActivity$ListAdapter$1
                r6.<init>()
                r5.setDelegate(r6)
                android.widget.FrameLayout r6 = r5.getCheckFrame()
                org.telegram.ui.PhotoPickerActivity r0 = org.telegram.ui.PhotoPickerActivity.this
                int r0 = r0.selectPhotoType
                int r1 = org.telegram.ui.PhotoAlbumPickerActivity.SELECT_TYPE_ALL
                if (r0 == r1) goto L_0x0076
                r0 = 8
                goto L_0x0077
            L_0x0076:
                r0 = 0
            L_0x0077:
                r6.setVisibility(r0)
            L_0x007a:
                org.telegram.ui.Components.RecyclerListView$Holder r6 = new org.telegram.ui.Components.RecyclerListView$Holder
                r6.<init>(r5)
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            boolean z;
            int itemViewType = viewHolder.getItemViewType();
            int i2 = -1;
            int i3 = 0;
            if (itemViewType == 0) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
                photoAttachPhotoCell.setItemSize(PhotoPickerActivity.this.itemSize);
                BackupImageView imageView = photoAttachPhotoCell.getImageView();
                photoAttachPhotoCell.setTag(Integer.valueOf(i));
                imageView.setOrientation(0, true);
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                    photoAttachPhotoCell.setPhotoEntry(photoEntry, true, false);
                    if (PhotoPickerActivity.this.allowIndices) {
                        i2 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                    }
                    photoAttachPhotoCell.setChecked(i2, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                    z = PhotoViewer.isShowingImage(photoEntry.path);
                } else {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                    photoAttachPhotoCell.setPhotoEntry(searchImage, true, false);
                    photoAttachPhotoCell.getVideoInfoContainer().setVisibility(4);
                    if (PhotoPickerActivity.this.allowIndices) {
                        i2 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(searchImage.id);
                    }
                    photoAttachPhotoCell.setChecked(i2, PhotoPickerActivity.this.selectedPhotos.containsKey(searchImage.id), false);
                    z = PhotoViewer.isShowingImage(searchImage.getPathToAttach());
                }
                imageView.getImageReceiver().setVisible(!z, true);
                CheckBox2 checkBox = photoAttachPhotoCell.getCheckBox();
                if (PhotoPickerActivity.this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL || z) {
                    i3 = 8;
                }
                checkBox.setVisibility(i3);
            } else if (itemViewType == 1) {
                ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.width = -1;
                    layoutParams.height = PhotoPickerActivity.this.itemSize;
                    viewHolder.itemView.setLayoutParams(layoutParams);
                }
            } else if (itemViewType == 2) {
                MediaController.PhotoEntry photoEntry2 = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                sharedDocumentCell.setPhotoEntry(photoEntry2);
                sharedDocumentCell.setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry2.imageId)), false);
                sharedDocumentCell.setTag(Integer.valueOf(i));
            } else if (itemViewType == 3) {
                TextCell textCell = (TextCell) viewHolder.itemView;
                if (i < PhotoPickerActivity.this.recentSearches.size()) {
                    textCell.setTextAndIcon((String) PhotoPickerActivity.this.recentSearches.get(i), NUM, false);
                } else {
                    textCell.setTextAndIcon(LocaleController.getString("ClearRecentHistory", NUM), NUM, false);
                }
            }
        }

        public int getItemViewType(int i) {
            if (PhotoPickerActivity.this.listSort) {
                return 2;
            }
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return 0;
            }
            if (PhotoPickerActivity.this.searchResult.isEmpty()) {
                return i == PhotoPickerActivity.this.recentSearches.size() ? 4 : 3;
            }
            if (i < PhotoPickerActivity.this.searchResult.size()) {
                return 0;
            }
            return 1;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.sizeNotifierFrameLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelHint"));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        arrayList.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{Theme.chat_attachEmptyDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachEmptyImage"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachPhotoBackground"));
        return arrayList;
    }
}
