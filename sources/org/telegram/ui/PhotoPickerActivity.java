package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
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
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DividerCell;
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
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerViewItemRangeSelector;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PhotoViewer;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int change_sort = 1;
    private static final int open_in = 2;
    /* access modifiers changed from: private */
    public int alertOnlyOnce;
    /* access modifiers changed from: private */
    public boolean allowCaption;
    /* access modifiers changed from: private */
    public boolean allowIndices;
    /* access modifiers changed from: private */
    public boolean allowOrder = true;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private CharSequence caption;
    /* access modifiers changed from: private */
    public ChatActivity chatActivity;
    protected EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public PhotoPickerActivityDelegate delegate;
    /* access modifiers changed from: private */
    public final String dialogBackgroundKey;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public final boolean forceDarckTheme;
    protected FrameLayout frameLayout2;
    /* access modifiers changed from: private */
    public int imageReqId;
    /* access modifiers changed from: private */
    public boolean imageSearchEndReached = true;
    private String initialSearchString;
    private boolean isDocumentsPicker;
    private ActionBarMenuSubItem[] itemCells;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    /* access modifiers changed from: private */
    public int itemSize = 100;
    /* access modifiers changed from: private */
    public int itemsPerRow = 3;
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
    private boolean needsBottomLayout = true;
    /* access modifiers changed from: private */
    public String nextImagesSearchOffset;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public boolean scaleToFill() {
            return false;
        }

        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            PhotoAttachPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell == null) {
                return null;
            }
            BackupImageView imageView = cell.getImageView();
            int[] coords = new int[2];
            imageView.getLocationInWindow(coords);
            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
            object.viewX = coords[0];
            object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
            object.parentView = PhotoPickerActivity.this.listView;
            object.imageReceiver = imageView.getImageReceiver();
            object.thumb = object.imageReceiver.getBitmapSafe();
            object.scale = cell.getScale();
            cell.showCheck(false);
            return object;
        }

        public void updatePhotoAtIndex(int index) {
            PhotoAttachPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell == null) {
                return;
            }
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                BackupImageView imageView = cell.getImageView();
                imageView.setOrientation(0, true);
                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                if (photoEntry.thumbPath != null) {
                    imageView.setImage(photoEntry.thumbPath, (String) null, Theme.chat_attachEmptyDrawable);
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
                cell.setPhotoEntry((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index), true, false);
            }
        }

        public boolean allowCaption() {
            return PhotoPickerActivity.this.allowCaption;
        }

        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            PhotoAttachPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell != null) {
                return cell.getImageView().getImageReceiver().getBitmapSafe();
            }
            return null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            int count = PhotoPickerActivity.this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (view.getTag() != null) {
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                    int num = ((Integer) view.getTag()).intValue();
                    if (PhotoPickerActivity.this.selectedAlbum == null ? !(num < 0 || num >= PhotoPickerActivity.this.searchResult.size()) : !(num < 0 || num >= PhotoPickerActivity.this.selectedAlbum.photos.size())) {
                        if (num == index) {
                            cell.showCheck(true);
                            return;
                        }
                    }
                }
            }
        }

        public void willHidePhotoViewer() {
            int count = PhotoPickerActivity.this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    ((PhotoAttachPhotoCell) view).showCheck(true);
                }
            }
        }

        public boolean isPhotoChecked(int index) {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                if (index < 0 || index >= PhotoPickerActivity.this.selectedAlbum.photos.size() || !PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(index).imageId))) {
                    return false;
                }
                return true;
            } else if (index < 0 || index >= PhotoPickerActivity.this.searchResult.size() || !PhotoPickerActivity.this.selectedPhotos.containsKey(((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index)).id)) {
                return false;
            } else {
                return true;
            }
        }

        public int setPhotoUnchecked(Object object) {
            Object key = null;
            if (object instanceof MediaController.PhotoEntry) {
                key = Integer.valueOf(((MediaController.PhotoEntry) object).imageId);
            } else if (object instanceof MediaController.SearchImage) {
                key = ((MediaController.SearchImage) object).id;
            }
            if (key == null || !PhotoPickerActivity.this.selectedPhotos.containsKey(key)) {
                return -1;
            }
            PhotoPickerActivity.this.selectedPhotos.remove(key);
            int position = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(key);
            if (position >= 0) {
                PhotoPickerActivity.this.selectedPhotosOrder.remove(position);
            }
            if (PhotoPickerActivity.this.allowIndices) {
                PhotoPickerActivity.this.updateCheckedPhotoIndices();
            }
            return position;
        }

        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            int num;
            boolean add = true;
            int i = -1;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                if (index < 0 || index >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                    return -1;
                }
                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                int access$900 = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                num = access$900;
                if (access$900 == -1) {
                    photoEntry.editedInfo = videoEditedInfo;
                    num = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                } else {
                    add = false;
                    photoEntry.editedInfo = null;
                }
            } else if (index < 0 || index >= PhotoPickerActivity.this.searchResult.size()) {
                return -1;
            } else {
                MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index);
                int access$9002 = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry2, -1);
                num = access$9002;
                if (access$9002 == -1) {
                    photoEntry2.editedInfo = videoEditedInfo;
                    num = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(photoEntry2.id);
                } else {
                    add = false;
                    photoEntry2.editedInfo = null;
                }
            }
            int count = PhotoPickerActivity.this.listView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (((Integer) view.getTag()).intValue() == index) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) view;
                    if (PhotoPickerActivity.this.allowIndices) {
                        i = num;
                    }
                    photoAttachPhotoCell.setChecked(i, add, false);
                } else {
                    a++;
                }
            }
            PhotoPickerActivity.this.updatePhotosButton(add ? 1 : 2);
            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            return num;
        }

        public boolean cancelButtonPressed() {
            PhotoPickerActivity.this.delegate.actionButtonPressed(true, true, 0);
            PhotoPickerActivity.this.finishFragment();
            return true;
        }

        public int getSelectedCount() {
            return PhotoPickerActivity.this.selectedPhotos.size();
        }

        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {
            if (PhotoPickerActivity.this.selectedPhotos.isEmpty()) {
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    if (index >= 0 && index < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                        MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                        photoEntry.editedInfo = videoEditedInfo;
                        int unused = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                    } else {
                        return;
                    }
                } else if (index >= 0 && index < PhotoPickerActivity.this.searchResult.size()) {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index);
                    searchImage.editedInfo = videoEditedInfo;
                    int unused2 = PhotoPickerActivity.this.addToSelectedPhotos(searchImage, -1);
                } else {
                    return;
                }
            }
            PhotoPickerActivity.this.sendSelectedPhotos(notify, scheduleDate);
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return PhotoPickerActivity.this.selectedPhotosOrder;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return PhotoPickerActivity.this.selectedPhotos;
        }
    };
    /* access modifiers changed from: private */
    public ArrayList<String> recentSearches = new ArrayList<>();
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    private PhotoPickerActivitySearchDelegate searchDelegate;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.SearchImage> searchResult = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<String, MediaController.SearchImage> searchResultKeys = new HashMap<>();
    private HashMap<String, MediaController.SearchImage> searchResultUrls = new HashMap<>();
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
    private final String selectorKey;
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
    public final String textKey;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public int type;
    private ImageView writeButton;
    protected FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public interface PhotoPickerActivitySearchDelegate {
        void shouldClearRecentSearch();

        void shouldSearchText(String str);
    }

    public interface PhotoPickerActivityDelegate {
        void actionButtonPressed(boolean z, boolean z2, int i);

        void onCaptionChanged(CharSequence charSequence);

        void onOpenInPressed();

        void selectedPhotosChanged();

        /* renamed from: org.telegram.ui.PhotoPickerActivity$PhotoPickerActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onOpenInPressed(PhotoPickerActivityDelegate _this) {
            }
        }
    }

    public PhotoPickerActivity(int type2, MediaController.AlbumEntry selectedAlbum2, HashMap<Object, Object> selectedPhotos2, ArrayList<Object> selectedPhotosOrder2, int selectPhotoType2, boolean allowCaption2, ChatActivity chatActivity2, boolean forceDarkTheme) {
        this.selectedAlbum = selectedAlbum2;
        this.selectedPhotos = selectedPhotos2;
        this.selectedPhotosOrder = selectedPhotosOrder2;
        this.type = type2;
        this.selectPhotoType = selectPhotoType2;
        this.chatActivity = chatActivity2;
        this.allowCaption = allowCaption2;
        this.forceDarckTheme = forceDarkTheme;
        if (selectedAlbum2 == null) {
            loadRecentSearch();
        }
        if (forceDarkTheme) {
            this.dialogBackgroundKey = "voipgroup_dialogBackground";
            this.textKey = "voipgroup_actionBarItems";
            this.selectorKey = "voipgroup_actionBarItemsSelector";
            return;
        }
        this.dialogBackgroundKey = "dialogBackground";
        this.textKey = "dialogTextBlack";
        this.selectorKey = "dialogButtonSelector";
    }

    public void setDocumentsPicker(boolean value) {
        this.isDocumentsPicker = value;
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
        this.actionBar.setBackgroundColor(Theme.getColor(this.dialogBackgroundKey));
        this.actionBar.setTitleColor(Theme.getColor(this.textKey));
        this.actionBar.setItemsColor(Theme.getColor(this.textKey), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(this.selectorKey), false);
        this.actionBar.setBackButtonImage(NUM);
        boolean z = true;
        if (this.selectedAlbum != null) {
            this.actionBar.setTitle(this.selectedAlbum.bucketName);
        } else {
            int i2 = this.type;
            if (i2 == 0) {
                this.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", NUM));
            } else if (i2 == 1) {
                this.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", NUM));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PhotoPickerActivity.this.finishFragment();
                } else if (id == 1) {
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
                } else if (id == 2) {
                    if (PhotoPickerActivity.this.delegate != null) {
                        PhotoPickerActivity.this.delegate.onOpenInPressed();
                    }
                    PhotoPickerActivity.this.finishFragment();
                }
            }
        });
        if (this.isDocumentsPicker) {
            ActionBarMenuItem menuItem = this.actionBar.createMenu().addItem(0, NUM);
            menuItem.setSubMenuDelegate(new ActionBarMenuItem.ActionBarSubMenuItemDelegate() {
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

                public void onHideSubMenu() {
                }
            });
            this.showAsListItem = menuItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("ShowAsList", NUM));
            menuItem.addSubItem(2, NUM, (CharSequence) LocaleController.getString("OpenInExternalApp", NUM));
        }
        if (this.selectedAlbum == null) {
            ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
            this.searchItem = actionBarMenuItemSearchListener;
            EditTextBoldCursor editText = actionBarMenuItemSearchListener.getSearchField();
            editText.setTextColor(Theme.getColor(this.textKey));
            editText.setCursorColor(Theme.getColor(this.textKey));
            editText.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        }
        if (this.selectedAlbum == null) {
            int i3 = this.type;
            if (i3 == 0) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", NUM));
            } else if (i3 == 1) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchGifsTitle", NUM));
            }
        }
        AnonymousClass5 r3 = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private int lastItemSize;
            private int lastNotifyWidth;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                int availableWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                if (AndroidUtilities.isTablet()) {
                    int unused = PhotoPickerActivity.this.itemsPerRow = 4;
                } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    int unused2 = PhotoPickerActivity.this.itemsPerRow = 4;
                } else {
                    int unused3 = PhotoPickerActivity.this.itemsPerRow = 3;
                }
                this.ignoreLayout = true;
                int unused4 = PhotoPickerActivity.this.itemSize = ((availableWidth - AndroidUtilities.dp(12.0f)) - AndroidUtilities.dp(10.0f)) / PhotoPickerActivity.this.itemsPerRow;
                if (this.lastItemSize != PhotoPickerActivity.this.itemSize) {
                    this.lastItemSize = PhotoPickerActivity.this.itemSize;
                    AndroidUtilities.runOnUIThread(new PhotoPickerActivity$5$$ExternalSyntheticLambda0(this));
                }
                if (PhotoPickerActivity.this.listSort) {
                    PhotoPickerActivity.this.layoutManager.setSpanCount(1);
                } else {
                    PhotoPickerActivity.this.layoutManager.setSpanCount((PhotoPickerActivity.this.itemSize * PhotoPickerActivity.this.itemsPerRow) + (AndroidUtilities.dp(5.0f) * (PhotoPickerActivity.this.itemsPerRow - 1)));
                }
                this.ignoreLayout = false;
                onMeasureInternal(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, NUM));
            }

            /* renamed from: lambda$onMeasure$0$org-telegram-ui-PhotoPickerActivity$5  reason: not valid java name */
            public /* synthetic */ void m3553lambda$onMeasure$0$orgtelegramuiPhotoPickerActivity$5() {
                PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
            }

            private void onMeasureInternal(int widthMeasureSpec, int heightMeasureSpec) {
                int heightSize;
                int heightMeasureSpec2;
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize2);
                int kbHeight = measureKeyboardHeight();
                if ((SharedConfig.smoothKeyboard ? 0 : kbHeight) > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || PhotoPickerActivity.this.commentTextView == null || PhotoPickerActivity.this.frameLayout2.getParent() != this) {
                    heightMeasureSpec2 = heightMeasureSpec;
                    heightSize = heightSize2;
                } else {
                    int heightSize3 = heightSize2 - PhotoPickerActivity.this.commentTextView.getEmojiPadding();
                    heightSize = heightSize3;
                    heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(heightSize3, NUM);
                }
                if (kbHeight > AndroidUtilities.dp(20.0f) && PhotoPickerActivity.this.commentTextView != null) {
                    this.ignoreLayout = true;
                    PhotoPickerActivity.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                if (SharedConfig.smoothKeyboard && PhotoPickerActivity.this.commentTextView != null && PhotoPickerActivity.this.commentTextView.isPopupShowing()) {
                    PhotoPickerActivity.this.fragmentView.setTranslationY(0.0f);
                    PhotoPickerActivity.this.listView.setTranslationY(0.0f);
                    PhotoPickerActivity.this.emptyView.setTranslationY(0.0f);
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8)) {
                        if (PhotoPickerActivity.this.commentTextView == null || !PhotoPickerActivity.this.commentTextView.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec((heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                if (this.lastNotifyWidth != r - l) {
                    this.lastNotifyWidth = r - l;
                    if (PhotoPickerActivity.this.listAdapter != null) {
                        PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                    }
                    if (PhotoPickerActivity.this.sendPopupWindow != null && PhotoPickerActivity.this.sendPopupWindow.isShowing()) {
                        PhotoPickerActivity.this.sendPopupWindow.dismiss();
                    }
                }
                int count = getChildCount();
                int paddingBottom = 0;
                int keyboardSize = SharedConfig.smoothKeyboard ? 0 : measureKeyboardHeight();
                if (PhotoPickerActivity.this.commentTextView != null && PhotoPickerActivity.this.frameLayout2.getParent() == this && keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                    paddingBottom = PhotoPickerActivity.this.commentTextView.getEmojiPadding();
                }
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch (gravity & 7 & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (((r - l) - width) - lp.rightMargin) - getPaddingRight();
                                break;
                            default:
                                childLeft = lp.leftMargin + getPaddingLeft();
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                break;
                            case 80:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (PhotoPickerActivity.this.commentTextView != null && PhotoPickerActivity.this.commentTextView.isPopupView(child)) {
                            if (AndroidUtilities.isTablet()) {
                                childTop = getMeasuredHeight() - child.getMeasuredHeight();
                            } else {
                                childTop = (getMeasuredHeight() + keyboardSize) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout = r3;
        r3.setBackgroundColor(Theme.getColor(this.dialogBackgroundKey));
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
        AnonymousClass6 r5 = new GridLayoutManager(context2, 4) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r5;
        recyclerListView2.setLayoutManager(r5);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                if (PhotoPickerActivity.this.listAdapter.getItemViewType(position) == 1 || PhotoPickerActivity.this.listSort || (PhotoPickerActivity.this.selectedAlbum == null && TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString))) {
                    return PhotoPickerActivity.this.layoutManager.getSpanCount();
                }
                return PhotoPickerActivity.this.itemSize + (position % PhotoPickerActivity.this.itemsPerRow != PhotoPickerActivity.this.itemsPerRow - 1 ? AndroidUtilities.dp(5.0f) : 0);
            }
        });
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView3.setAdapter(listAdapter2);
        this.listView.setGlowColor(Theme.getColor(this.dialogBackgroundKey));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PhotoPickerActivity$$ExternalSyntheticLambda2(this));
        if (this.maxSelectedPhotos != 1) {
            this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new PhotoPickerActivity$$ExternalSyntheticLambda3(this));
        }
        RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = new RecyclerViewItemRangeSelector(new RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate() {
            public int getItemCount() {
                return PhotoPickerActivity.this.listAdapter.getItemCount();
            }

            public void setSelected(View view, int index, boolean selected) {
                if (selected == PhotoPickerActivity.this.shouldSelect && (view instanceof PhotoAttachPhotoCell)) {
                    ((PhotoAttachPhotoCell) view).callDelegate();
                }
            }

            public boolean isSelected(int index) {
                Object key;
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    key = Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(index).imageId);
                } else {
                    key = ((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index)).id;
                }
                return PhotoPickerActivity.this.selectedPhotos.containsKey(key);
            }

            public boolean isIndexSelectable(int index) {
                return PhotoPickerActivity.this.listAdapter.getItemViewType(index) == 0;
            }

            public void onStartStopSelection(boolean start) {
                int unused = PhotoPickerActivity.this.alertOnlyOnce = start;
                if (start) {
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
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (PhotoPickerActivity.this.selectedAlbum == null) {
                    int firstVisibleItem = PhotoPickerActivity.this.layoutManager.findFirstVisibleItemPosition();
                    boolean z = false;
                    int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(PhotoPickerActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                    if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount > PhotoPickerActivity.this.layoutManager.getItemCount() - 2 && !PhotoPickerActivity.this.searching && !PhotoPickerActivity.this.imageSearchEndReached) {
                        PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                        if (photoPickerActivity.type == 1) {
                            z = true;
                        }
                        photoPickerActivity.searchImages(z, PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.nextImagesSearchOffset, true);
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
            frameLayout.setBackgroundColor(Theme.getColor(this.dialogBackgroundKey));
            this.frameLayout2.setVisibility(4);
            this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
            this.frameLayout2.setOnTouchListener(PhotoPickerActivity$$ExternalSyntheticLambda7.INSTANCE);
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                editTextEmoji.onDestroy();
            }
            this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, (BaseFragment) null, 1);
            this.commentTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
            this.commentTextView.setHint(LocaleController.getString("AddCaption", NUM));
            this.commentTextView.onResume();
            EditTextBoldCursor editText2 = this.commentTextView.getEditText();
            editText2.setMaxLines(1);
            editText2.setSingleLine(true);
            this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
            CharSequence charSequence = this.caption;
            if (charSequence != null) {
                this.commentTextView.setText(charSequence);
            }
            this.commentTextView.getEditText().addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (PhotoPickerActivity.this.delegate != null) {
                        PhotoPickerActivity.this.delegate.onCaptionChanged(s);
                    }
                }
            });
            AnonymousClass11 r52 = new FrameLayout(context2) {
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                    super.onInitializeAccessibilityNodeInfo(info);
                    info.setText(LocaleController.formatPluralString("AccDescrSendPhotos", PhotoPickerActivity.this.selectedPhotos.size()));
                    info.setClassName(Button.class.getName());
                    info.setLongClickable(true);
                    info.setClickable(true);
                }
            };
            this.writeButtonContainer = r52;
            r52.setFocusable(true);
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
                Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, this.writeButtonDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                this.writeButtonDrawable = combinedDrawable;
            }
            this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
            this.writeButton.setImageResource(NUM);
            this.writeButton.setImportantForAccessibility(2);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
            this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
            this.writeButton.setOnClickListener(new PhotoPickerActivity$$ExternalSyntheticLambda4(this));
            this.writeButton.setOnLongClickListener(new PhotoPickerActivity$$ExternalSyntheticLambda6(this));
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            AnonymousClass14 r53 = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    String text = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, PhotoPickerActivity.this.selectedPhotosOrder.size()))});
                    int textSize = (int) Math.ceil((double) PhotoPickerActivity.this.textPaint.measureText(text));
                    int size = Math.max(AndroidUtilities.dp(16.0f) + textSize, AndroidUtilities.dp(24.0f));
                    int cx = getMeasuredWidth() / 2;
                    int measuredHeight = getMeasuredHeight() / 2;
                    PhotoPickerActivity.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor(PhotoPickerActivity.this.dialogBackgroundKey));
                    PhotoPickerActivity.this.rect.set((float) (cx - (size / 2)), 0.0f, (float) ((size / 2) + cx), (float) getMeasuredHeight());
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), PhotoPickerActivity.this.paint);
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                    PhotoPickerActivity.this.rect.set((float) ((cx - (size / 2)) + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (((size / 2) + cx) - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), PhotoPickerActivity.this.paint);
                    canvas.drawText(text, (float) (cx - (textSize / 2)), (float) AndroidUtilities.dp(16.2f), PhotoPickerActivity.this.textPaint);
                }
            };
            this.selectedCountView = r53;
            r53.setAlpha(0.0f);
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

    /* renamed from: lambda$createView$1$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ void m3543lambda$createView$1$orgtelegramuiPhotoPickerActivity(View view, int position) {
        ArrayList arrayList;
        int type2;
        if (this.selectedAlbum != null || !this.searchResult.isEmpty()) {
            MediaController.AlbumEntry albumEntry = this.selectedAlbum;
            if (albumEntry != null) {
                arrayList = albumEntry.photos;
            } else {
                arrayList = this.searchResult;
            }
            if (position >= 0 && position < arrayList.size()) {
                ActionBarMenuItem actionBarMenuItem = this.searchItem;
                if (actionBarMenuItem != null) {
                    AndroidUtilities.hideKeyboard(actionBarMenuItem.getSearchField());
                }
                if (this.listSort) {
                    onListItemClick(view, arrayList.get(position));
                    return;
                }
                if (this.selectPhotoType == PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR || this.selectPhotoType == PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR_VIDEO) {
                    type2 = 1;
                } else if (this.selectPhotoType == PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER) {
                    type2 = 3;
                } else if (this.selectPhotoType == PhotoAlbumPickerActivity.SELECT_TYPE_QR) {
                    type2 = 10;
                } else if (this.chatActivity == null) {
                    type2 = 4;
                } else {
                    type2 = 0;
                }
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
                PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, type2, this.isDocumentsPicker, this.provider, this.chatActivity);
            }
        } else if (position < this.recentSearches.size()) {
            String text = this.recentSearches.get(position);
            PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate = this.searchDelegate;
            if (photoPickerActivitySearchDelegate != null) {
                photoPickerActivitySearchDelegate.shouldSearchText(text);
                return;
            }
            this.searchItem.getSearchField().setText(text);
            this.searchItem.getSearchField().setSelection(text.length());
            processSearch(this.searchItem.getSearchField());
        } else if (position == this.recentSearches.size() + 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("ClearSearchAlertTitle", NUM));
            builder.setMessage(LocaleController.getString("ClearSearchAlert", NUM));
            builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new PhotoPickerActivity$$ExternalSyntheticLambda0(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog dialog = builder.create();
            showDialog(dialog);
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ void m3542lambda$createView$0$orgtelegramuiPhotoPickerActivity(DialogInterface dialogInterface, int i) {
        PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate = this.searchDelegate;
        if (photoPickerActivitySearchDelegate != null) {
            photoPickerActivitySearchDelegate.shouldClearRecentSearch();
        } else {
            clearRecentSearch();
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ boolean m3544lambda$createView$2$orgtelegramuiPhotoPickerActivity(View view, int position) {
        if (this.listSort) {
            onListItemClick(view, this.selectedAlbum.photos.get(position));
            return true;
        } else if (!(view instanceof PhotoAttachPhotoCell)) {
            return false;
        } else {
            RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
            boolean z = !((PhotoAttachPhotoCell) view).isChecked();
            this.shouldSelect = z;
            recyclerViewItemRangeSelector.setIsActive(view, true, position, z);
            return false;
        }
    }

    static /* synthetic */ boolean lambda$createView$3(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ void m3545lambda$createView$4$orgtelegramuiPhotoPickerActivity(View v) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
            sendSelectedPhotos(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new PhotoPickerActivity$$ExternalSyntheticLambda1(this));
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ boolean m3548lambda$createView$7$orgtelegramuiPhotoPickerActivity(View view) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null || this.maxSelectedPhotos == 1) {
            return false;
        }
        TLRPC.Chat currentChat = chatActivity2.getCurrentChat();
        TLRPC.User user = this.chatActivity.getCurrentUser();
        if (this.sendPopupLayout == null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity());
            this.sendPopupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setAnimationEnabled(false);
            this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() != 0 || PhotoPickerActivity.this.sendPopupWindow == null || !PhotoPickerActivity.this.sendPopupWindow.isShowing()) {
                        return false;
                    }
                    v.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                        return false;
                    }
                    PhotoPickerActivity.this.sendPopupWindow.dismiss();
                    return false;
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new PhotoPickerActivity$$ExternalSyntheticLambda12(this));
            this.sendPopupLayout.setShownFromBotton(false);
            this.itemCells = new ActionBarMenuSubItem[2];
            int a = 0;
            while (a < 2) {
                if ((a != 0 || this.chatActivity.canScheduleMessage()) && (a != 1 || !UserObject.isUserSelf(user))) {
                    int num = a;
                    this.itemCells[a] = new ActionBarMenuSubItem(getParentActivity(), a == 0, a == 1);
                    if (num != 0) {
                        this.itemCells[a].setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                    } else if (UserObject.isUserSelf(user)) {
                        this.itemCells[a].setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                    } else {
                        this.itemCells[a].setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                    }
                    this.itemCells[a].setMinimumWidth(AndroidUtilities.dp(196.0f));
                    this.sendPopupLayout.addView(this.itemCells[a], LayoutHelper.createLinear(-1, 48));
                    this.itemCells[a].setOnClickListener(new PhotoPickerActivity$$ExternalSyntheticLambda5(this, num));
                }
                a++;
            }
            this.sendPopupLayout.setupRadialSelectors(Theme.getColor(this.selectorKey));
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
        int[] location = new int[2];
        view.getLocationInWindow(location);
        this.sendPopupWindow.showAtLocation(view, 51, ((location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (location[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
        this.sendPopupWindow.dimBehind();
        view.performHapticFeedback(3, 2);
        return false;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ void m3546lambda$createView$5$orgtelegramuiPhotoPickerActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ void m3547lambda$createView$6$orgtelegramuiPhotoPickerActivity(int num, View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (num == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new PhotoPickerActivity$$ExternalSyntheticLambda1(this));
        } else {
            sendSelectedPhotos(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(float y) {
        if (this.listView != null) {
            if (this.commentTextView.isPopupShowing()) {
                this.fragmentView.setTranslationY(y);
                this.listView.setTranslationY(0.0f);
                this.emptyView.setTranslationY(0.0f);
                return;
            }
            this.listView.setTranslationY(y);
            this.emptyView.setTranslationY(y);
        }
    }

    public void setLayoutViews(FrameLayout f2, FrameLayout button, View count, View s, EditTextEmoji emoji) {
        this.frameLayout2 = f2;
        this.writeButtonContainer = button;
        this.commentTextView = emoji;
        this.selectedCountView = count;
        this.shadow = s;
        this.needsBottomLayout = false;
    }

    private void applyCaption() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null && editTextEmoji.length() > 0) {
            Object entry = this.selectedPhotos.get(this.selectedPhotosOrder.get(0));
            if (entry instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) entry).caption = this.commentTextView.getText().toString();
            } else if (entry instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) entry).caption = this.commentTextView.getText().toString();
            }
        }
    }

    private void onListItemClick(View view, Object item) {
        boolean add;
        if (addToSelectedPhotos(item, -1) == -1) {
            add = true;
        } else {
            add = false;
        }
        int i = 1;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(this.selectedPhotosOrder.contains(Integer.valueOf(this.selectedAlbum.photos.get(((Integer) view.getTag()).intValue()).imageId)), true);
        }
        if (!add) {
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    public void setCaption(CharSequence text) {
        this.caption = text;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.setText(text);
        }
    }

    public void setInitialSearchString(String text) {
        this.initialSearchString = text;
    }

    private void saveRecentSearch() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("web_recent_search", 0).edit();
        editor.clear();
        editor.putInt("count", this.recentSearches.size());
        int N = this.recentSearches.size();
        for (int a = 0; a < N; a++) {
            editor.putString("recent" + a, this.recentSearches.get(a));
        }
        editor.commit();
    }

    private void loadRecentSearch() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("web_recent_search", 0);
        int count = preferences.getInt("count", 0);
        int a = 0;
        while (a < count) {
            String str = preferences.getString("recent" + a, (String) null);
            if (str != null) {
                this.recentSearches.add(str);
                a++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void processSearch(EditText editText) {
        if (editText.getText().length() != 0) {
            String text = editText.getText().toString();
            int a = 0;
            int N = this.recentSearches.size();
            while (true) {
                if (a >= N) {
                    break;
                } else if (this.recentSearches.get(a).equalsIgnoreCase(text)) {
                    this.recentSearches.remove(a);
                    break;
                } else {
                    a++;
                }
            }
            boolean z = false;
            this.recentSearches.add(0, text);
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
            searchImages(z, text, "", true);
            this.lastSearchString = text;
            if (text.length() == 0) {
                this.lastSearchString = null;
                this.emptyView.setText(LocaleController.getString("NoRecentSearches", NUM));
            } else {
                this.emptyView.setText(LocaleController.getString("NoResult", NUM));
            }
            updateSearchInterface();
        }
    }

    private boolean showCommentTextView(final boolean show, boolean animated) {
        if (this.commentTextView == null) {
            return false;
        }
        if (show == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.frameLayout2.setTag(show ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (show) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        float f = 0.0f;
        float f2 = 0.2f;
        float f3 = 1.0f;
        if (animated) {
            this.animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            FrameLayout frameLayout = this.writeButtonContainer;
            Property property = View.SCALE_X;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout3, property2, fArr2));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(frameLayout4, property3, fArr3));
            View view = this.selectedCountView;
            Property property4 = View.SCALE_X;
            float[] fArr4 = new float[1];
            fArr4[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(view, property4, fArr4));
            View view2 = this.selectedCountView;
            Property property5 = View.SCALE_Y;
            float[] fArr5 = new float[1];
            if (show) {
                f2 = 1.0f;
            }
            fArr5[0] = f2;
            animators.add(ObjectAnimator.ofFloat(view2, property5, fArr5));
            View view3 = this.selectedCountView;
            Property property6 = View.ALPHA;
            float[] fArr6 = new float[1];
            if (!show) {
                f3 = 0.0f;
            }
            fArr6[0] = f3;
            animators.add(ObjectAnimator.ofFloat(view3, property6, fArr6));
            FrameLayout frameLayout5 = this.frameLayout2;
            Property property7 = View.TRANSLATION_Y;
            float[] fArr7 = new float[1];
            fArr7[0] = show ? 0.0f : (float) AndroidUtilities.dp(48.0f);
            animators.add(ObjectAnimator.ofFloat(frameLayout5, property7, fArr7));
            View view4 = this.shadow;
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            if (!show) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            fArr8[0] = f;
            animators.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
            this.animatorSet.playTogether(animators);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(PhotoPickerActivity.this.animatorSet)) {
                        if (!show) {
                            PhotoPickerActivity.this.frameLayout2.setVisibility(4);
                            PhotoPickerActivity.this.writeButtonContainer.setVisibility(4);
                        }
                        AnimatorSet unused = PhotoPickerActivity.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(PhotoPickerActivity.this.animatorSet)) {
                        AnimatorSet unused = PhotoPickerActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.writeButtonContainer.setScaleX(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(show ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(show ? 1.0f : 0.2f);
            View view5 = this.selectedCountView;
            if (show) {
                f2 = 1.0f;
            }
            view5.setScaleY(f2);
            View view6 = this.selectedCountView;
            if (!show) {
                f3 = 0.0f;
            }
            view6.setAlpha(f3);
            this.frameLayout2.setTranslationY(show ? 0.0f : (float) AndroidUtilities.dp(48.0f));
            View view7 = this.shadow;
            if (!show) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            view7.setTranslationY(f);
            if (!show) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        this.maxSelectedPhotos = value;
        this.allowOrder = order;
        if (value > 0 && this.type == 1) {
            this.maxSelectedPhotos = 1;
        }
    }

    /* access modifiers changed from: private */
    public void updateCheckedPhotoIndices() {
        if (this.allowIndices) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.listView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                    Integer index = (Integer) view.getTag();
                    MediaController.AlbumEntry albumEntry = this.selectedAlbum;
                    int i = -1;
                    if (albumEntry != null) {
                        MediaController.PhotoEntry photoEntry = albumEntry.photos.get(index.intValue());
                        if (this.allowIndices) {
                            i = this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        }
                        cell.setNum(i);
                    } else {
                        MediaController.SearchImage photoEntry2 = this.searchResult.get(index.intValue());
                        if (this.allowIndices) {
                            i = this.selectedPhotosOrder.indexOf(photoEntry2.id);
                        }
                        cell.setNum(i);
                    }
                } else if (view instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) view).setChecked(this.selectedPhotosOrder.indexOf(Integer.valueOf(this.selectedAlbum.photos.get(((Integer) view.getTag()).intValue()).imageId)) != 0, false);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public PhotoAttachPhotoCell getCellForIndex(int index) {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (view instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                int num = ((Integer) cell.getTag()).intValue();
                MediaController.AlbumEntry albumEntry = this.selectedAlbum;
                if (albumEntry == null ? !(num < 0 || num >= this.searchResult.size()) : !(num < 0 || num >= albumEntry.photos.size())) {
                    if (num == index) {
                        return cell;
                    }
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public int addToSelectedPhotos(Object object, int index) {
        Object key = null;
        if (object instanceof MediaController.PhotoEntry) {
            key = Integer.valueOf(((MediaController.PhotoEntry) object).imageId);
        } else if (object instanceof MediaController.SearchImage) {
            key = ((MediaController.SearchImage) object).id;
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
                if (object instanceof MediaController.PhotoEntry) {
                    ((MediaController.PhotoEntry) object).reset();
                } else if (object instanceof MediaController.SearchImage) {
                    ((MediaController.SearchImage) object).reset();
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
        ActionBarMenuItem actionBarMenuItem;
        if (isOpen && (actionBarMenuItem = this.searchItem) != null) {
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

    public void updatePhotosButton(int animated) {
        boolean z = true;
        if (this.selectedPhotos.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            if (animated == 0) {
                z = false;
            }
            showCommentTextView(false, z);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true, animated != 0) || animated == 0) {
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
        fArr[0] = animated == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        View view2 = this.selectedCountView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (animated != 1) {
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

    private void searchBotUser(boolean gif) {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            req.username = gif ? instance.gifSearchBot : instance.imageSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PhotoPickerActivity$$ExternalSyntheticLambda11(this, gif));
        }
    }

    /* renamed from: lambda$searchBotUser$9$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ void m3550lambda$searchBotUser$9$orgtelegramuiPhotoPickerActivity(boolean gif, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new PhotoPickerActivity$$ExternalSyntheticLambda9(this, response, gif));
        }
    }

    /* renamed from: lambda$searchBotUser$8$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ void m3549lambda$searchBotUser$8$orgtelegramuiPhotoPickerActivity(TLObject response, boolean gif) {
        TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        String str = this.lastSearchImageString;
        this.lastSearchImageString = null;
        searchImages(gif, str, "", false);
    }

    /* access modifiers changed from: private */
    public void searchImages(boolean gif, String query, String offset, boolean searchUser) {
        if (this.searching) {
            this.searching = false;
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }
        this.lastSearchImageString = query;
        this.searching = true;
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        MessagesController instance2 = MessagesController.getInstance(this.currentAccount);
        TLObject object = instance.getUserOrChat(gif ? instance2.gifSearchBot : instance2.imageSearchBot);
        if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
            req.query = query == null ? "" : query;
            req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
            req.offset = offset;
            ChatActivity chatActivity2 = this.chatActivity;
            if (chatActivity2 != null) {
                long dialogId = chatActivity2.getDialogId();
                if (DialogObject.isEncryptedDialog(dialogId)) {
                    req.peer = new TLRPC.TL_inputPeerEmpty();
                } else {
                    req.peer = getMessagesController().getInputPeer(dialogId);
                }
            } else {
                req.peer = new TLRPC.TL_inputPeerEmpty();
            }
            int token = this.lastSearchToken + 1;
            this.lastSearchToken = token;
            this.imageReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PhotoPickerActivity$$ExternalSyntheticLambda10(this, token, gif, user));
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.imageReqId, this.classGuid);
        } else if (searchUser) {
            searchBotUser(gif);
        }
    }

    /* renamed from: lambda$searchImages$11$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ void m3552lambda$searchImages$11$orgtelegramuiPhotoPickerActivity(int token, boolean gif, TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PhotoPickerActivity$$ExternalSyntheticLambda8(this, token, response, gif, user));
    }

    /* renamed from: lambda$searchImages$10$org-telegram-ui-PhotoPickerActivity  reason: not valid java name */
    public /* synthetic */ void m3551lambda$searchImages$10$orgtelegramuiPhotoPickerActivity(int token, TLObject response, boolean gif, TLRPC.User user) {
        TLRPC.PhotoSize size;
        TLRPC.DocumentAttribute attribute;
        boolean z = gif;
        if (token == this.lastSearchToken) {
            int addedCount = 0;
            int oldCount = this.searchResult.size();
            if (response != null) {
                TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
                this.nextImagesSearchOffset = res.next_offset;
                int count = res.results.size();
                for (int a = 0; a < count; a++) {
                    TLRPC.BotInlineResult result = res.results.get(a);
                    if ((z || "photo".equals(result.type)) && ((!z || "gif".equals(result.type)) && !this.searchResultKeys.containsKey(result.id))) {
                        MediaController.SearchImage image = new MediaController.SearchImage();
                        if (z && result.document != null) {
                            int b = 0;
                            while (true) {
                                if (b >= result.document.attributes.size()) {
                                    break;
                                }
                                attribute = result.document.attributes.get(b);
                                if ((attribute instanceof TLRPC.TL_documentAttributeImageSize) || (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                    image.width = attribute.w;
                                    image.height = attribute.h;
                                } else {
                                    b++;
                                }
                            }
                            image.width = attribute.w;
                            image.height = attribute.h;
                            image.document = result.document;
                            image.size = 0;
                            if (!(result.photo == null || (size = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, this.itemSize, true)) == null)) {
                                result.document.thumbs.add(size);
                                result.document.flags |= 1;
                            }
                        } else if (!z && result.photo != null) {
                            TLRPC.PhotoSize size2 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, AndroidUtilities.getPhotoSize());
                            TLRPC.PhotoSize size22 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, 320);
                            if (size2 != null) {
                                image.width = size2.w;
                                image.height = size2.h;
                                image.photoSize = size2;
                                image.photo = result.photo;
                                image.size = size2.size;
                                image.thumbPhotoSize = size22;
                            }
                        } else if (result.content != null) {
                            int b2 = 0;
                            while (true) {
                                if (b2 >= result.content.attributes.size()) {
                                    break;
                                }
                                TLRPC.DocumentAttribute attribute2 = result.content.attributes.get(b2);
                                if (attribute2 instanceof TLRPC.TL_documentAttributeImageSize) {
                                    image.width = attribute2.w;
                                    image.height = attribute2.h;
                                    break;
                                }
                                b2++;
                            }
                            if (result.thumb != null) {
                                image.thumbUrl = result.thumb.url;
                            } else {
                                image.thumbUrl = null;
                            }
                            image.imageUrl = result.content.url;
                            image.size = z ? 0 : result.content.size;
                        }
                        image.id = result.id;
                        image.type = z ? 1 : 0;
                        image.inlineResult = result;
                        image.params = new HashMap<>();
                        image.params.put("id", result.id);
                        image.params.put("query_id", "" + res.query_id);
                        image.params.put("bot_name", user.username);
                        this.searchResult.add(image);
                        this.searchResultKeys.put(image.id, image);
                        addedCount++;
                    }
                    TLRPC.User user2 = user;
                }
                TLRPC.User user3 = user;
                this.imageSearchEndReached = oldCount == this.searchResult.size() || this.nextImagesSearchOffset == null;
            } else {
                TLRPC.User user4 = user;
            }
            this.searching = false;
            if (addedCount != 0) {
                this.listAdapter.notifyItemRangeInserted(oldCount, addedCount);
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
    public void sendSelectedPhotos(boolean notify, int scheduleDate) {
        if (!this.selectedPhotos.isEmpty() && this.delegate != null && !this.sendPressed) {
            applyCaption();
            this.sendPressed = true;
            this.delegate.actionButtonPressed(false, notify, scheduleDate);
            if (this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER) {
                finishFragment();
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return true;
            }
            if (TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString)) {
                if (holder.getItemViewType() == 3) {
                    return true;
                }
                return false;
            } else if (holder.getAdapterPosition() < PhotoPickerActivity.this.searchResult.size()) {
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

        public long getItemId(int i) {
            return (long) i;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    PhotoAttachPhotoCell cell = new PhotoAttachPhotoCell(this.mContext, (Theme.ResourcesProvider) null);
                    cell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate() {
                        private void checkSlowMode() {
                            TLRPC.Chat chat;
                            if (PhotoPickerActivity.this.allowOrder && PhotoPickerActivity.this.chatActivity != null && (chat = PhotoPickerActivity.this.chatActivity.getCurrentChat()) != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled && PhotoPickerActivity.this.alertOnlyOnce != 2) {
                                AlertsCreator.showSimpleAlert(PhotoPickerActivity.this, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSelectSendError", NUM));
                                if (PhotoPickerActivity.this.alertOnlyOnce == 1) {
                                    int unused = PhotoPickerActivity.this.alertOnlyOnce = 2;
                                }
                            }
                        }

                        public void onCheckClick(PhotoAttachPhotoCell v) {
                            boolean added;
                            int index = ((Integer) v.getTag()).intValue();
                            int num = -1;
                            int i = 1;
                            if (PhotoPickerActivity.this.selectedAlbum != null) {
                                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                                added = !PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
                                if (!added || PhotoPickerActivity.this.maxSelectedPhotos <= 0 || PhotoPickerActivity.this.selectedPhotos.size() < PhotoPickerActivity.this.maxSelectedPhotos) {
                                    if (PhotoPickerActivity.this.allowIndices && added) {
                                        num = PhotoPickerActivity.this.selectedPhotosOrder.size();
                                    }
                                    v.setChecked(num, added, true);
                                    int unused = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, index);
                                } else {
                                    checkSlowMode();
                                    return;
                                }
                            } else {
                                AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                                MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index);
                                added = !PhotoPickerActivity.this.selectedPhotos.containsKey(photoEntry2.id);
                                if (!added || PhotoPickerActivity.this.maxSelectedPhotos <= 0 || PhotoPickerActivity.this.selectedPhotos.size() < PhotoPickerActivity.this.maxSelectedPhotos) {
                                    if (PhotoPickerActivity.this.allowIndices && added) {
                                        num = PhotoPickerActivity.this.selectedPhotosOrder.size();
                                    }
                                    v.setChecked(num, added, true);
                                    int unused2 = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry2, index);
                                } else {
                                    checkSlowMode();
                                    return;
                                }
                            }
                            PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                            if (!added) {
                                i = 2;
                            }
                            photoPickerActivity.updatePhotosButton(i);
                            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
                        }
                    });
                    cell.getCheckFrame().setVisibility(PhotoPickerActivity.this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL ? 8 : 0);
                    PhotoAttachPhotoCell photoAttachPhotoCell = cell;
                    view = cell;
                    break;
                case 1:
                    FrameLayout frameLayout = new FrameLayout(this.mContext);
                    frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    RadialProgressView progressBar = new RadialProgressView(this.mContext);
                    progressBar.setProgressColor(-11371101);
                    frameLayout.addView(progressBar, LayoutHelper.createFrame(-1, -1.0f));
                    view = frameLayout;
                    break;
                case 2:
                    view = new SharedDocumentCell(this.mContext, 1);
                    break;
                case 3:
                    View view2 = new TextCell(this.mContext, 23, true);
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view = view2;
                    if (PhotoPickerActivity.this.forceDarckTheme) {
                        TextCell textCell = (TextCell) view2;
                        textCell.textView.setTextColor(Theme.getColor(PhotoPickerActivity.this.textKey));
                        textCell.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("voipgroup_mutedIcon"), PorterDuff.Mode.MULTIPLY));
                        view = view2;
                        break;
                    }
                    break;
                default:
                    View view3 = new DividerCell(this.mContext);
                    ((DividerCell) view3).setForceDarkTheme(PhotoPickerActivity.this.forceDarckTheme);
                    view = view3;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean showing;
            int i = -1;
            int i2 = 0;
            switch (holder.getItemViewType()) {
                case 0:
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) holder.itemView;
                    cell.setItemSize(PhotoPickerActivity.this.itemSize);
                    BackupImageView imageView = cell.getImageView();
                    cell.setTag(Integer.valueOf(position));
                    imageView.setOrientation(0, true);
                    if (PhotoPickerActivity.this.selectedAlbum != null) {
                        MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(position);
                        cell.setPhotoEntry(photoEntry, true, false);
                        if (PhotoPickerActivity.this.allowIndices) {
                            i = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        }
                        cell.setChecked(i, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                        showing = PhotoViewer.isShowingImage(photoEntry.path);
                    } else {
                        MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(position);
                        cell.setPhotoEntry(photoEntry2, true, false);
                        cell.getVideoInfoContainer().setVisibility(4);
                        if (PhotoPickerActivity.this.allowIndices) {
                            i = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(photoEntry2.id);
                        }
                        cell.setChecked(i, PhotoPickerActivity.this.selectedPhotos.containsKey(photoEntry2.id), false);
                        showing = PhotoViewer.isShowingImage(photoEntry2.getPathToAttach());
                    }
                    imageView.getImageReceiver().setVisible(!showing, true);
                    CheckBox2 checkBox = cell.getCheckBox();
                    if (PhotoPickerActivity.this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL || showing) {
                        i2 = 8;
                    }
                    checkBox.setVisibility(i2);
                    return;
                case 1:
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    if (params != null) {
                        params.width = -1;
                        params.height = PhotoPickerActivity.this.itemSize;
                        holder.itemView.setLayoutParams(params);
                        return;
                    }
                    return;
                case 2:
                    MediaController.PhotoEntry photoEntry3 = PhotoPickerActivity.this.selectedAlbum.photos.get(position);
                    SharedDocumentCell documentCell = (SharedDocumentCell) holder.itemView;
                    documentCell.setPhotoEntry(photoEntry3);
                    documentCell.setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry3.imageId)), false);
                    documentCell.setTag(Integer.valueOf(position));
                    return;
                case 3:
                    TextCell cell2 = (TextCell) holder.itemView;
                    if (position < PhotoPickerActivity.this.recentSearches.size()) {
                        cell2.setTextAndIcon((String) PhotoPickerActivity.this.recentSearches.get(position), NUM, false);
                        return;
                    } else {
                        cell2.setTextAndIcon(LocaleController.getString("ClearRecentHistory", NUM), NUM, false);
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (PhotoPickerActivity.this.listSort) {
                return 2;
            }
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return 0;
            }
            if (PhotoPickerActivity.this.searchResult.isEmpty()) {
                return position == PhotoPickerActivity.this.recentSearches.size() ? 4 : 3;
            }
            if (position < PhotoPickerActivity.this.searchResult.size()) {
                return 0;
            }
            return 1;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.sizeNotifierFrameLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.dialogBackgroundKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.dialogBackgroundKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.textKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.textKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.selectorKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.textKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelHint"));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        themeDescriptions.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.textKey));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.dialogBackgroundKey));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{Theme.chat_attachEmptyDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachEmptyImage"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachPhotoBackground"));
        return themeDescriptions;
    }
}
