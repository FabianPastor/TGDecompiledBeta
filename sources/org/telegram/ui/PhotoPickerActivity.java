package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
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
import org.telegram.messenger.UserObject;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.messages_BotResults;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.RecyclerViewItemRangeSelector;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int change_sort = 1;
    private static final int open_in = 2;
    private int alertOnlyOnce;
    private boolean allowCaption;
    private boolean allowIndices;
    private boolean allowOrder = true;
    private AnimatorSet animatorSet;
    private CharSequence caption;
    private ChatActivity chatActivity;
    protected EditTextEmoji commentTextView;
    private PhotoPickerActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    protected FrameLayout frameLayout2;
    private int imageReqId;
    private boolean imageSearchEndReached = true;
    private String initialSearchString;
    private boolean isDocumentsPicker;
    private ActionBarMenuSubItem[] itemCells;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    private int itemSize = 100;
    private int itemsPerRow = 3;
    private String lastSearchImageString;
    private String lastSearchString;
    private int lastSearchToken;
    private GridLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private boolean listSort;
    private RecyclerListView listView;
    private int maxSelectedPhotos;
    private boolean needsBottomLayout = true;
    private String nextImagesSearchOffset;
    private Paint paint = new Paint(1);
    private PhotoViewerProvider provider = new EmptyPhotoViewerProvider() {
        public boolean scaleToFill() {
            return false;
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i, boolean z) {
            PhotoAttachPhotoCell access$000 = PhotoPickerActivity.this.getCellForIndex(i);
            if (access$000 == null) {
                return null;
            }
            BackupImageView imageView = access$000.getImageView();
            int[] iArr = new int[2];
            imageView.getLocationInWindow(iArr);
            PlaceProviderObject placeProviderObject = new PlaceProviderObject();
            placeProviderObject.viewX = iArr[0];
            placeProviderObject.viewY = iArr[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
            placeProviderObject.parentView = PhotoPickerActivity.this.listView;
            placeProviderObject.imageReceiver = imageView.getImageReceiver();
            placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
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
                PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                String str = photoEntry.thumbPath;
                if (str != null) {
                    imageView.setImage(str, null, Theme.chat_attachEmptyDrawable);
                    return;
                } else if (photoEntry.path != null) {
                    imageView.setOrientation(photoEntry.orientation, true);
                    String str2 = ":";
                    StringBuilder stringBuilder;
                    if (photoEntry.isVideo) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("vthumb://");
                        stringBuilder.append(photoEntry.imageId);
                        stringBuilder.append(str2);
                        stringBuilder.append(photoEntry.path);
                        imageView.setImage(stringBuilder.toString(), null, Theme.chat_attachEmptyDrawable);
                        return;
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("thumb://");
                    stringBuilder.append(photoEntry.imageId);
                    stringBuilder.append(str2);
                    stringBuilder.append(photoEntry.path);
                    imageView.setImage(stringBuilder.toString(), null, Theme.chat_attachEmptyDrawable);
                    return;
                } else {
                    imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
                    return;
                }
            }
            access$000.setPhotoEntry((SearchImage) PhotoPickerActivity.this.searchResult.get(i), true, false);
        }

        public boolean allowCaption() {
            return PhotoPickerActivity.this.allowCaption;
        }

        public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            PhotoAttachPhotoCell access$000 = PhotoPickerActivity.this.getCellForIndex(i);
            return access$000 != null ? access$000.getImageView().getImageReceiver().getBitmapSafe() : null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            int childCount = PhotoPickerActivity.this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = PhotoPickerActivity.this.listView.getChildAt(i2);
                if (childAt.getTag() != null) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    int intValue = ((Integer) childAt.getTag()).intValue();
                    if (PhotoPickerActivity.this.selectedAlbum == null ? intValue < 0 || intValue >= PhotoPickerActivity.this.searchResult.size() : intValue < 0 || intValue >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
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
            boolean z = true;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                if (i < 0 || i >= PhotoPickerActivity.this.selectedAlbum.photos.size() || !PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(((PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i)).imageId))) {
                    z = false;
                }
                return z;
            }
            if (i < 0 || i >= PhotoPickerActivity.this.searchResult.size() || !PhotoPickerActivity.this.selectedPhotos.containsKey(((SearchImage) PhotoPickerActivity.this.searchResult.get(i)).id)) {
                z = false;
            }
            return z;
        }

        public int setPhotoUnchecked(Object obj) {
            obj = obj instanceof PhotoEntry ? Integer.valueOf(((PhotoEntry) obj).imageId) : obj instanceof SearchImage ? ((SearchImage) obj).id : null;
            if (obj == null || !PhotoPickerActivity.this.selectedPhotos.containsKey(obj)) {
                return -1;
            }
            PhotoPickerActivity.this.selectedPhotos.remove(obj);
            int indexOf = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(obj);
            if (indexOf >= 0) {
                PhotoPickerActivity.this.selectedPhotosOrder.remove(indexOf);
            }
            if (PhotoPickerActivity.this.allowIndices) {
                PhotoPickerActivity.this.updateCheckedPhotoIndices();
            }
            return indexOf;
        }

        /* JADX WARNING: Removed duplicated region for block: B:29:0x00bc  */
        public int setPhotoChecked(int r9, org.telegram.messenger.VideoEditedInfo r10) {
            /*
            r8 = this;
            r0 = org.telegram.ui.PhotoPickerActivity.this;
            r0 = r0.selectedAlbum;
            r1 = 1;
            r2 = 0;
            r3 = -1;
            if (r0 == 0) goto L_0x004e;
        L_0x000b:
            if (r9 < 0) goto L_0x004d;
        L_0x000d:
            r0 = org.telegram.ui.PhotoPickerActivity.this;
            r0 = r0.selectedAlbum;
            r0 = r0.photos;
            r0 = r0.size();
            if (r9 < r0) goto L_0x001c;
        L_0x001b:
            goto L_0x004d;
        L_0x001c:
            r0 = org.telegram.ui.PhotoPickerActivity.this;
            r0 = r0.selectedAlbum;
            r0 = r0.photos;
            r0 = r0.get(r9);
            r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0;
            r4 = org.telegram.ui.PhotoPickerActivity.this;
            r4 = r4.addToSelectedPhotos(r0, r3);
            if (r4 != r3) goto L_0x0046;
        L_0x0032:
            r0.editedInfo = r10;
            r10 = org.telegram.ui.PhotoPickerActivity.this;
            r10 = r10.selectedPhotosOrder;
            r0 = r0.imageId;
            r0 = java.lang.Integer.valueOf(r0);
            r4 = r10.indexOf(r0);
            r10 = 1;
            goto L_0x004a;
        L_0x0046:
            r10 = 0;
            r0.editedInfo = r10;
            r10 = 0;
        L_0x004a:
            r0 = r10;
            r10 = r4;
            goto L_0x0082;
        L_0x004d:
            return r3;
        L_0x004e:
            if (r9 < 0) goto L_0x00ca;
        L_0x0050:
            r10 = org.telegram.ui.PhotoPickerActivity.this;
            r10 = r10.searchResult;
            r10 = r10.size();
            if (r9 < r10) goto L_0x005e;
        L_0x005c:
            goto L_0x00ca;
        L_0x005e:
            r10 = org.telegram.ui.PhotoPickerActivity.this;
            r10 = r10.searchResult;
            r10 = r10.get(r9);
            r10 = (org.telegram.messenger.MediaController.SearchImage) r10;
            r0 = org.telegram.ui.PhotoPickerActivity.this;
            r0 = r0.addToSelectedPhotos(r10, r3);
            if (r0 != r3) goto L_0x0080;
        L_0x0072:
            r0 = org.telegram.ui.PhotoPickerActivity.this;
            r0 = r0.selectedPhotosOrder;
            r10 = r10.id;
            r10 = r0.indexOf(r10);
            r0 = 1;
            goto L_0x0082;
        L_0x0080:
            r10 = r0;
            r0 = 0;
        L_0x0082:
            r4 = org.telegram.ui.PhotoPickerActivity.this;
            r4 = r4.listView;
            r4 = r4.getChildCount();
            r5 = 0;
        L_0x008d:
            if (r5 >= r4) goto L_0x00b7;
        L_0x008f:
            r6 = org.telegram.ui.PhotoPickerActivity.this;
            r6 = r6.listView;
            r6 = r6.getChildAt(r5);
            r7 = r6.getTag();
            r7 = (java.lang.Integer) r7;
            r7 = r7.intValue();
            if (r7 != r9) goto L_0x00b4;
        L_0x00a5:
            r6 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r6;
            r9 = org.telegram.ui.PhotoPickerActivity.this;
            r9 = r9.allowIndices;
            if (r9 == 0) goto L_0x00b0;
        L_0x00af:
            r3 = r10;
        L_0x00b0:
            r6.setChecked(r3, r0, r2);
            goto L_0x00b7;
        L_0x00b4:
            r5 = r5 + 1;
            goto L_0x008d;
        L_0x00b7:
            r9 = org.telegram.ui.PhotoPickerActivity.this;
            if (r0 == 0) goto L_0x00bc;
        L_0x00bb:
            goto L_0x00bd;
        L_0x00bc:
            r1 = 2;
        L_0x00bd:
            r9.updatePhotosButton(r1);
            r9 = org.telegram.ui.PhotoPickerActivity.this;
            r9 = r9.delegate;
            r9.selectedPhotosChanged();
            return r10;
        L_0x00ca:
            return r3;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity$AnonymousClass1.setPhotoChecked(int, org.telegram.messenger.VideoEditedInfo):int");
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
                        PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                        photoEntry.editedInfo = videoEditedInfo;
                        PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                    } else {
                        return;
                    }
                } else if (i >= 0 && i < PhotoPickerActivity.this.searchResult.size()) {
                    PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                    photoPickerActivity.addToSelectedPhotos(photoPickerActivity.searchResult.get(i), -1);
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
    private ArrayList<String> recentSearches = new ArrayList();
    private RectF rect = new RectF();
    private PhotoPickerActivitySearchDelegate searchDelegate;
    private ActionBarMenuItem searchItem;
    private ArrayList<SearchImage> searchResult = new ArrayList();
    private HashMap<String, SearchImage> searchResultKeys = new HashMap();
    private HashMap<String, SearchImage> searchResultUrls = new HashMap();
    private boolean searching;
    private boolean searchingUser;
    private int selectPhotoType;
    private AlbumEntry selectedAlbum;
    protected View selectedCountView;
    private HashMap<Object, Object> selectedPhotos;
    private ArrayList<Object> selectedPhotosOrder;
    private ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    protected View shadow;
    private boolean shouldSelect;
    private ActionBarMenuSubItem showAsListItem;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private TextPaint textPaint = new TextPaint(1);
    private int type;
    private ImageView writeButton;
    protected FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public interface PhotoPickerActivityDelegate {

        public final /* synthetic */ class -CC {
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

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            boolean z = true;
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                if (TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString)) {
                    if (viewHolder.getItemViewType() != 3) {
                        z = false;
                    }
                    return z;
                } else if (viewHolder.getAdapterPosition() >= PhotoPickerActivity.this.searchResult.size()) {
                    z = false;
                }
            }
            return z;
        }

        public int getItemCount() {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return PhotoPickerActivity.this.selectedAlbum.photos.size();
            }
            if (!PhotoPickerActivity.this.searchResult.isEmpty()) {
                return PhotoPickerActivity.this.searchResult.size() + (PhotoPickerActivity.this.imageSearchEndReached ^ 1);
            }
            int i = 0;
            if (TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString) && !PhotoPickerActivity.this.recentSearches.isEmpty()) {
                i = PhotoPickerActivity.this.recentSearches.size() + 2;
            }
            return i;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View sharedDocumentCell;
            if (i != 0) {
                View frameLayout;
                if (i == 1) {
                    frameLayout = new FrameLayout(this.mContext);
                    frameLayout.setLayoutParams(new LayoutParams(-1, -2));
                    RadialProgressView radialProgressView = new RadialProgressView(this.mContext);
                    radialProgressView.setProgressColor(-11371101);
                    frameLayout.addView(radialProgressView, LayoutHelper.createFrame(-1, -1.0f));
                } else if (i == 2) {
                    sharedDocumentCell = new SharedDocumentCell(this.mContext, true);
                } else if (i != 3) {
                    sharedDocumentCell = new DividerCell(this.mContext);
                } else {
                    frameLayout = new TextCell(this.mContext, 23, true);
                    frameLayout.setLayoutParams(new LayoutParams(-1, -2));
                }
                sharedDocumentCell = frameLayout;
            } else {
                sharedDocumentCell = new PhotoAttachPhotoCell(this.mContext);
                sharedDocumentCell.setDelegate(new PhotoAttachPhotoCellDelegate() {
                    private void checkSlowMode() {
                        if (PhotoPickerActivity.this.allowOrder && PhotoPickerActivity.this.chatActivity != null) {
                            Chat currentChat = PhotoPickerActivity.this.chatActivity.getCurrentChat();
                            if (currentChat != null && !ChatObject.hasAdminRights(currentChat) && currentChat.slowmode_enabled && PhotoPickerActivity.this.alertOnlyOnce != 2) {
                                AlertsCreator.showSimpleAlert(PhotoPickerActivity.this, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSelectSendError", NUM));
                                if (PhotoPickerActivity.this.alertOnlyOnce == 1) {
                                    PhotoPickerActivity.this.alertOnlyOnce = 2;
                                }
                            }
                        }
                    }

                    public void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell) {
                        int containsKey;
                        int intValue = ((Integer) photoAttachPhotoCell.getTag()).intValue();
                        int i = -1;
                        int i2 = 1;
                        if (PhotoPickerActivity.this.selectedAlbum != null) {
                            PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(intValue);
                            containsKey = PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)) ^ 1;
                            if (containsKey == 0 || PhotoPickerActivity.this.maxSelectedPhotos <= 0 || PhotoPickerActivity.this.selectedPhotos.size() < PhotoPickerActivity.this.maxSelectedPhotos) {
                                if (PhotoPickerActivity.this.allowIndices && containsKey != 0) {
                                    i = PhotoPickerActivity.this.selectedPhotosOrder.size();
                                }
                                photoAttachPhotoCell.setChecked(i, containsKey, true);
                                PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, intValue);
                            } else {
                                checkSlowMode();
                                return;
                            }
                        }
                        AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                        SearchImage searchImage = (SearchImage) PhotoPickerActivity.this.searchResult.get(intValue);
                        containsKey = PhotoPickerActivity.this.selectedPhotos.containsKey(searchImage.id) ^ 1;
                        if (containsKey == 0 || PhotoPickerActivity.this.maxSelectedPhotos <= 0 || PhotoPickerActivity.this.selectedPhotos.size() < PhotoPickerActivity.this.maxSelectedPhotos) {
                            if (PhotoPickerActivity.this.allowIndices && containsKey != 0) {
                                i = PhotoPickerActivity.this.selectedPhotosOrder.size();
                            }
                            photoAttachPhotoCell.setChecked(i, containsKey, true);
                            PhotoPickerActivity.this.addToSelectedPhotos(searchImage, intValue);
                        } else {
                            checkSlowMode();
                            return;
                        }
                        PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                        if (containsKey == 0) {
                            i2 = 2;
                        }
                        photoPickerActivity.updatePhotosButton(i2);
                        PhotoPickerActivity.this.delegate.selectedPhotosChanged();
                    }
                });
                sharedDocumentCell.getCheckFrame().setVisibility(PhotoPickerActivity.this.selectPhotoType != 0 ? 8 : 0);
            }
            return new Holder(sharedDocumentCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            int i2 = -1;
            int i3 = false;
            if (itemViewType == 0) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
                photoAttachPhotoCell.setItemSize(PhotoPickerActivity.this.itemSize);
                BackupImageView imageView = photoAttachPhotoCell.getImageView();
                photoAttachPhotoCell.setTag(Integer.valueOf(i));
                imageView.setOrientation(0, true);
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                    photoAttachPhotoCell.setPhotoEntry(photoEntry, true, false);
                    if (PhotoPickerActivity.this.allowIndices) {
                        i2 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                    }
                    photoAttachPhotoCell.setChecked(i2, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                    i = PhotoViewer.isShowingImage(photoEntry.path);
                } else {
                    SearchImage searchImage = (SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                    photoAttachPhotoCell.setPhotoEntry(searchImage, true, false);
                    photoAttachPhotoCell.getVideoInfoContainer().setVisibility(4);
                    if (PhotoPickerActivity.this.allowIndices) {
                        i2 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(searchImage.id);
                    }
                    photoAttachPhotoCell.setChecked(i2, PhotoPickerActivity.this.selectedPhotos.containsKey(searchImage.id), false);
                    i = PhotoViewer.isShowingImage(searchImage.getPathToAttach());
                }
                imageView.getImageReceiver().setVisible(i ^ 1, true);
                CheckBox2 checkBox = photoAttachPhotoCell.getCheckBox();
                if (!(PhotoPickerActivity.this.selectPhotoType == 0 && i == 0)) {
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
                PhotoEntry photoEntry2 = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                sharedDocumentCell.setPhotoEntry(photoEntry2);
                sharedDocumentCell.setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry2.imageId)), false);
                sharedDocumentCell.setTag(Integer.valueOf(i));
            } else if (itemViewType == 3) {
                TextCell textCell = (TextCell) viewHolder.itemView;
                if (i < PhotoPickerActivity.this.recentSearches.size()) {
                    textCell.setTextAndIcon((String) PhotoPickerActivity.this.recentSearches.get(i), NUM, false);
                } else {
                    textCell.setTextAndIcon(LocaleController.getString("ClearHistory", NUM), NUM, false);
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
            } else if (i < PhotoPickerActivity.this.searchResult.size()) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    public PhotoPickerActivity(int i, AlbumEntry albumEntry, HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, int i2, boolean z, ChatActivity chatActivity) {
        this.selectedAlbum = albumEntry;
        this.selectedPhotos = hashMap;
        this.selectedPhotosOrder = arrayList;
        this.type = i;
        this.selectPhotoType = i2;
        this.chatActivity = chatActivity;
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

    /* JADX WARNING: Missing block: B:65:0x0482, code skipped:
            if (r1 != 1) goto L_0x0489;
     */
    /* JADX WARNING: Missing block: B:67:0x0486, code skipped:
            if (r0.allowOrder != false) goto L_0x048a;
     */
    /* JADX WARNING: Missing block: B:68:0x0489, code skipped:
            r9 = false;
     */
    /* JADX WARNING: Missing block: B:69:0x048a, code skipped:
            r0.allowIndices = r9;
            r0.listView.setEmptyView(r0.emptyView);
            updatePhotosButton(0);
     */
    /* JADX WARNING: Missing block: B:70:0x0499, code skipped:
            return r0.fragmentView;
     */
    public android.view.View createView(android.content.Context r20) {
        /*
        r19 = this;
        r0 = r19;
        r1 = r20;
        r2 = 0;
        r0.listSort = r2;
        r3 = r0.actionBar;
        r4 = "dialogBackground";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setBackgroundColor(r5);
        r3 = r0.actionBar;
        r5 = "dialogTextBlack";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r3.setTitleColor(r6);
        r3 = r0.actionBar;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r3.setItemsColor(r6, r2);
        r3 = r0.actionBar;
        r6 = "dialogButtonSelector";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r3.setItemsBackgroundColor(r6, r2);
        r3 = r0.actionBar;
        r6 = NUM; // 0x7var_f0 float:1.7945065E38 double:1.0529356216E-314;
        r3.setBackButtonImage(r6);
        r3 = r0.selectedAlbum;
        r6 = "SearchGifsTitle";
        r7 = NUM; // 0x7f0e09ac float:1.888006E38 double:1.05316338E-314;
        r8 = "SearchImagesTitle";
        r9 = 1;
        if (r3 == 0) goto L_0x004d;
    L_0x0045:
        r10 = r0.actionBar;
        r3 = r3.bucketName;
        r10.setTitle(r3);
        goto L_0x0069;
    L_0x004d:
        r3 = r0.type;
        if (r3 != 0) goto L_0x005b;
    L_0x0051:
        r3 = r0.actionBar;
        r10 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r3.setTitle(r10);
        goto L_0x0069;
    L_0x005b:
        if (r3 != r9) goto L_0x0069;
    L_0x005d:
        r3 = r0.actionBar;
        r10 = NUM; // 0x7f0e09a9 float:1.8880053E38 double:1.0531633785E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r6, r10);
        r3.setTitle(r10);
    L_0x0069:
        r3 = r0.actionBar;
        r10 = new org.telegram.ui.PhotoPickerActivity$2;
        r10.<init>();
        r3.setActionBarMenuOnItemClick(r10);
        r3 = r0.isDocumentsPicker;
        if (r3 == 0) goto L_0x00ae;
    L_0x0077:
        r3 = r0.actionBar;
        r3 = r3.createMenu();
        r10 = NUM; // 0x7var_f7 float:1.7945079E38 double:1.052935625E-314;
        r3 = r3.addItem(r2, r10);
        r10 = new org.telegram.ui.-$$Lambda$PhotoPickerActivity$8wKttqoa04mIJ2FIva4uSGU4xCQ;
        r10.<init>(r0);
        r3.setSubMenuDelegate(r10);
        r10 = NUM; // 0x7var_df float:1.794555E38 double:1.0529357397E-314;
        r11 = NUM; // 0x7f0e0a48 float:1.8880376E38 double:1.053163457E-314;
        r12 = "ShowAsList";
        r11 = org.telegram.messenger.LocaleController.getString(r12, r11);
        r10 = r3.addSubItem(r9, r10, r11);
        r0.showAsListItem = r10;
        r10 = 2;
        r11 = NUM; // 0x7var_e8 float:1.7945568E38 double:1.052935744E-314;
        r12 = NUM; // 0x7f0e076a float:1.8878887E38 double:1.0531630944E-314;
        r13 = "OpenInExternalApp";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        r3.addSubItem(r10, r11, r12);
    L_0x00ae:
        r3 = r0.selectedAlbum;
        if (r3 != 0) goto L_0x00eb;
    L_0x00b2:
        r3 = r0.actionBar;
        r3 = r3.createMenu();
        r10 = NUM; // 0x7var_fa float:1.7945085E38 double:1.0529356265E-314;
        r3 = r3.addItem(r2, r10);
        r3 = r3.setIsSearchField(r9);
        r10 = new org.telegram.ui.PhotoPickerActivity$3;
        r10.<init>();
        r3 = r3.setActionBarMenuItemSearchListener(r10);
        r0.searchItem = r3;
        r3 = r0.searchItem;
        r3 = r3.getSearchField();
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r3.setTextColor(r10);
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r3.setCursorColor(r5);
        r5 = "chat_messagePanelHint";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r3.setHintTextColor(r5);
    L_0x00eb:
        r3 = r0.selectedAlbum;
        if (r3 != 0) goto L_0x010b;
    L_0x00ef:
        r3 = r0.type;
        if (r3 != 0) goto L_0x00fd;
    L_0x00f3:
        r3 = r0.searchItem;
        r5 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r3.setSearchFieldHint(r5);
        goto L_0x010b;
    L_0x00fd:
        if (r3 != r9) goto L_0x010b;
    L_0x00ff:
        r3 = r0.searchItem;
        r5 = NUM; // 0x7f0e09a9 float:1.8880053E38 double:1.0531633785E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.setSearchFieldHint(r5);
    L_0x010b:
        r3 = new org.telegram.ui.PhotoPickerActivity$4;
        r3.<init>(r1);
        r0.sizeNotifierFrameLayout = r3;
        r3 = r0.sizeNotifierFrameLayout;
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setBackgroundColor(r5);
        r3 = r0.sizeNotifierFrameLayout;
        r0.fragmentView = r3;
        r3 = new org.telegram.ui.Components.RecyclerListView;
        r3.<init>(r1);
        r0.listView = r3;
        r3 = r0.listView;
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r3.setPadding(r5, r6, r7, r8);
        r3 = r0.listView;
        r3.setClipToPadding(r2);
        r3 = r0.listView;
        r3.setHorizontalScrollBarEnabled(r2);
        r3 = r0.listView;
        r3.setVerticalScrollBarEnabled(r2);
        r3 = r0.listView;
        r5 = 0;
        r3.setItemAnimator(r5);
        r3 = r0.listView;
        r3.setLayoutAnimation(r5);
        r3 = r0.listView;
        r6 = new org.telegram.ui.PhotoPickerActivity$5;
        r7 = 4;
        r6.<init>(r1, r7);
        r0.layoutManager = r6;
        r3.setLayoutManager(r6);
        r3 = r0.layoutManager;
        r6 = new org.telegram.ui.PhotoPickerActivity$6;
        r6.<init>();
        r3.setSpanSizeLookup(r6);
        r3 = r0.sizeNotifierFrameLayout;
        r6 = r0.listView;
        r8 = 51;
        r10 = -1;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r10, r8);
        r3.addView(r6, r8);
        r3 = r0.listView;
        r6 = new org.telegram.ui.PhotoPickerActivity$ListAdapter;
        r6.<init>(r1);
        r0.listAdapter = r6;
        r3.setAdapter(r6);
        r3 = r0.listView;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setGlowColor(r6);
        r3 = r0.listView;
        r6 = new org.telegram.ui.-$$Lambda$PhotoPickerActivity$d2zefYpm4814gG5LmGJdgJH_M7Q;
        r6.<init>(r0);
        r3.setOnItemClickListener(r6);
        r3 = r0.listView;
        r6 = new org.telegram.ui.-$$Lambda$PhotoPickerActivity$IQu9lLTWj1Hz4Qe2UEofkLoJ4SA;
        r6.<init>(r0);
        r3.setOnItemLongClickListener(r6);
        r3 = new org.telegram.ui.Components.RecyclerViewItemRangeSelector;
        r6 = new org.telegram.ui.PhotoPickerActivity$7;
        r6.<init>();
        r3.<init>(r6);
        r0.itemRangeSelector = r3;
        r3 = r0.listView;
        r6 = r0.itemRangeSelector;
        r3.addOnItemTouchListener(r6);
        r3 = new org.telegram.ui.Components.EmptyTextProgressView;
        r3.<init>(r1);
        r0.emptyView = r3;
        r3 = r0.emptyView;
        r6 = -7104099; // 0xfffffffffvar_d float:NaN double:NaN;
        r3.setTextColor(r6);
        r3 = r0.emptyView;
        r6 = -11371101; // 0xfffffffffvar_da3 float:-2.7979022E38 double:NaN;
        r3.setProgressBarColor(r6);
        r3 = r0.selectedAlbum;
        if (r3 == 0) goto L_0x01ed;
    L_0x01d9:
        r3 = r0.emptyView;
        r3.setShowAtCenter(r2);
        r3 = r0.emptyView;
        r6 = NUM; // 0x7f0e06a8 float:1.8878494E38 double:1.0531629985E-314;
        r8 = "NoPhotos";
        r6 = org.telegram.messenger.LocaleController.getString(r8, r6);
        r3.setText(r6);
        goto L_0x020b;
    L_0x01ed:
        r3 = r0.emptyView;
        r3.setShowAtTop(r9);
        r3 = r0.emptyView;
        r6 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r3.setPadding(r2, r6, r2, r2);
        r3 = r0.emptyView;
        r6 = NUM; // 0x7f0e06b0 float:1.887851E38 double:1.0531630025E-314;
        r8 = "NoRecentSearches";
        r6 = org.telegram.messenger.LocaleController.getString(r8, r6);
        r3.setText(r6);
    L_0x020b:
        r3 = r0.sizeNotifierFrameLayout;
        r6 = r0.emptyView;
        r11 = -1;
        r12 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r13 = 51;
        r14 = 0;
        r15 = 0;
        r16 = 0;
        r8 = r0.selectPhotoType;
        r18 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r2 = 0;
        if (r8 == 0) goto L_0x0222;
    L_0x021f:
        r17 = 0;
        goto L_0x0224;
    L_0x0222:
        r17 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
    L_0x0224:
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17);
        r3.addView(r6, r8);
        r3 = r0.selectedAlbum;
        if (r3 != 0) goto L_0x023c;
    L_0x022f:
        r3 = r0.listView;
        r6 = new org.telegram.ui.PhotoPickerActivity$8;
        r6.<init>();
        r3.setOnScrollListener(r6);
        r19.updateSearchInterface();
    L_0x023c:
        r3 = r0.needsBottomLayout;
        if (r3 == 0) goto L_0x047a;
    L_0x0240:
        r3 = new android.view.View;
        r3.<init>(r1);
        r0.shadow = r3;
        r3 = r0.shadow;
        r6 = NUM; // 0x7var_ee float:1.794506E38 double:1.0529356206E-314;
        r3.setBackgroundResource(r6);
        r3 = r0.shadow;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r6 = (float) r6;
        r3.setTranslationY(r6);
        r3 = r0.sizeNotifierFrameLayout;
        r6 = r0.shadow;
        r11 = -1;
        r12 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r13 = 83;
        r14 = 0;
        r15 = 0;
        r16 = 0;
        r17 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17);
        r3.addView(r6, r8);
        r3 = new android.widget.FrameLayout;
        r3.<init>(r1);
        r0.frameLayout2 = r3;
        r3 = r0.frameLayout2;
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setBackgroundColor(r4);
        r3 = r0.frameLayout2;
        r3.setVisibility(r7);
        r3 = r0.frameLayout2;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r4 = (float) r4;
        r3.setTranslationY(r4);
        r3 = r0.sizeNotifierFrameLayout;
        r4 = r0.frameLayout2;
        r6 = 48;
        r8 = 83;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r6, r8);
        r3.addView(r4, r6);
        r3 = r0.frameLayout2;
        r4 = org.telegram.ui.-$$Lambda$PhotoPickerActivity$rkDwiiGipTPV4YsvDflFEXZwoHM.INSTANCE;
        r3.setOnTouchListener(r4);
        r3 = r0.commentTextView;
        if (r3 == 0) goto L_0x02ab;
    L_0x02a8:
        r3.onDestroy();
    L_0x02ab:
        r3 = new org.telegram.ui.Components.EditTextEmoji;
        r4 = r0.sizeNotifierFrameLayout;
        r3.<init>(r1, r4, r5, r9);
        r0.commentTextView = r3;
        r3 = new android.text.InputFilter[r9];
        r4 = new android.text.InputFilter$LengthFilter;
        r5 = org.telegram.messenger.UserConfig.selectedAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r5 = r5.maxCaptionLength;
        r4.<init>(r5);
        r5 = 0;
        r3[r5] = r4;
        r4 = r0.commentTextView;
        r4.setFilters(r3);
        r3 = r0.commentTextView;
        r4 = NUM; // 0x7f0e00ab float:1.8875384E38 double:1.053162241E-314;
        r5 = "AddCaption";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r3.setHint(r4);
        r3 = r0.commentTextView;
        r3.onResume();
        r3 = r0.commentTextView;
        r3 = r3.getEditText();
        r3.setMaxLines(r9);
        r3.setSingleLine(r9);
        r3 = r0.frameLayout2;
        r4 = r0.commentTextView;
        r10 = -1;
        r11 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r12 = 51;
        r13 = 0;
        r14 = 0;
        r15 = NUM; // 0x42a80000 float:84.0 double:5.525167263E-315;
        r16 = 0;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16);
        r3.addView(r4, r5);
        r3 = r0.caption;
        if (r3 == 0) goto L_0x0309;
    L_0x0304:
        r4 = r0.commentTextView;
        r4.setText(r3);
    L_0x0309:
        r3 = r0.commentTextView;
        r3 = r3.getEditText();
        r4 = new org.telegram.ui.PhotoPickerActivity$9;
        r4.<init>();
        r3.addTextChangedListener(r4);
        r3 = new android.widget.FrameLayout;
        r3.<init>(r1);
        r0.writeButtonContainer = r3;
        r3 = r0.writeButtonContainer;
        r3.setVisibility(r7);
        r3 = r0.writeButtonContainer;
        r4 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r3.setScaleX(r4);
        r3 = r0.writeButtonContainer;
        r3.setScaleY(r4);
        r3 = r0.writeButtonContainer;
        r3.setAlpha(r2);
        r3 = r0.writeButtonContainer;
        r5 = NUM; // 0x7f0e09ce float:1.8880128E38 double:1.0531633967E-314;
        r6 = "Send";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.setContentDescription(r5);
        r3 = r0.sizeNotifierFrameLayout;
        r5 = r0.writeButtonContainer;
        r10 = 60;
        r11 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r12 = 85;
        r13 = 0;
        r14 = 0;
        r15 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r16 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16);
        r3.addView(r5, r6);
        r3 = r0.writeButtonContainer;
        r5 = new org.telegram.ui.-$$Lambda$PhotoPickerActivity$dTiSUg75sRfWPRlo26sk1jZD--Q;
        r5.<init>(r0);
        r3.setOnClickListener(r5);
        r3 = r0.writeButtonContainer;
        r5 = new org.telegram.ui.-$$Lambda$PhotoPickerActivity$yQRzD-ZVwuD6b-bsifVLsGopz0U;
        r5.<init>(r0);
        r3.setOnLongClickListener(r5);
        r3 = new android.widget.ImageView;
        r3.<init>(r1);
        r0.writeButton = r3;
        r3 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r6 = "dialogFloatingButton";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r7 = "dialogFloatingButtonPressed";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r5 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r5, r6, r7);
        r0.writeButtonDrawable = r5;
        r5 = android.os.Build.VERSION.SDK_INT;
        r6 = 21;
        if (r5 >= r6) goto L_0x03c3;
    L_0x0393:
        r5 = r20.getResources();
        r7 = NUM; // 0x7var_cd float:1.7944994E38 double:1.0529356043E-314;
        r5 = r5.getDrawable(r7);
        r5 = r5.mutate();
        r7 = new android.graphics.PorterDuffColorFilter;
        r8 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r10 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r7.<init>(r8, r10);
        r5.setColorFilter(r7);
        r7 = new org.telegram.ui.Components.CombinedDrawable;
        r8 = r0.writeButtonDrawable;
        r10 = 0;
        r7.<init>(r5, r8, r10, r10);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r8 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r7.setIconSize(r5, r8);
        r0.writeButtonDrawable = r7;
    L_0x03c3:
        r5 = r0.writeButton;
        r7 = r0.writeButtonDrawable;
        r5.setBackgroundDrawable(r7);
        r5 = r0.writeButton;
        r7 = NUM; // 0x7var_ float:1.794475E38 double:1.052935545E-314;
        r5.setImageResource(r7);
        r5 = r0.writeButton;
        r7 = new android.graphics.PorterDuffColorFilter;
        r8 = "dialogFloatingIcon";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r10 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r7.<init>(r8, r10);
        r5.setColorFilter(r7);
        r5 = r0.writeButton;
        r7 = android.widget.ImageView.ScaleType.CENTER;
        r5.setScaleType(r7);
        r5 = android.os.Build.VERSION.SDK_INT;
        if (r5 < r6) goto L_0x03f9;
    L_0x03ef:
        r5 = r0.writeButton;
        r7 = new org.telegram.ui.PhotoPickerActivity$11;
        r7.<init>();
        r5.setOutlineProvider(r7);
    L_0x03f9:
        r5 = r0.writeButtonContainer;
        r7 = r0.writeButton;
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r6) goto L_0x0406;
    L_0x0401:
        r8 = 56;
        r10 = 56;
        goto L_0x040a;
    L_0x0406:
        r8 = 60;
        r10 = 60;
    L_0x040a:
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r6) goto L_0x0411;
    L_0x040e:
        r11 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        goto L_0x0415;
    L_0x0411:
        r3 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r11 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
    L_0x0415:
        r12 = 51;
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 < r6) goto L_0x0420;
    L_0x041b:
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r13 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x0421;
    L_0x0420:
        r13 = 0;
    L_0x0421:
        r14 = 0;
        r15 = 0;
        r16 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16);
        r5.addView(r7, r3);
        r3 = r0.textPaint;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r3.setTextSize(r5);
        r3 = r0.textPaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new org.telegram.ui.PhotoPickerActivity$12;
        r3.<init>(r1);
        r0.selectedCountView = r3;
        r1 = r0.selectedCountView;
        r1.setAlpha(r2);
        r1 = r0.selectedCountView;
        r1.setScaleX(r4);
        r1 = r0.selectedCountView;
        r1.setScaleY(r4);
        r1 = r0.sizeNotifierFrameLayout;
        r2 = r0.selectedCountView;
        r10 = 42;
        r11 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r12 = 85;
        r13 = 0;
        r15 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r16 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16);
        r1.addView(r2, r3);
        r1 = r0.selectPhotoType;
        if (r1 == 0) goto L_0x047a;
    L_0x0473:
        r1 = r0.commentTextView;
        r2 = 8;
        r1.setVisibility(r2);
    L_0x047a:
        r1 = r0.selectedAlbum;
        if (r1 != 0) goto L_0x0484;
    L_0x047e:
        r1 = r0.type;
        if (r1 == 0) goto L_0x0484;
    L_0x0482:
        if (r1 != r9) goto L_0x0489;
    L_0x0484:
        r1 = r0.allowOrder;
        if (r1 == 0) goto L_0x0489;
    L_0x0488:
        goto L_0x048a;
    L_0x0489:
        r9 = 0;
    L_0x048a:
        r0.allowIndices = r9;
        r1 = r0.listView;
        r2 = r0.emptyView;
        r1.setEmptyView(r2);
        r1 = 0;
        r0.updatePhotosButton(r1);
        r1 = r0.fragmentView;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ void lambda$createView$0$PhotoPickerActivity() {
        int i;
        String str;
        ActionBarMenuSubItem actionBarMenuSubItem = this.showAsListItem;
        if (this.listSort) {
            i = NUM;
            str = "ShowAsGrid";
        } else {
            i = NUM;
            str = "ShowAsList";
        }
        actionBarMenuSubItem.setText(LocaleController.getString(str, i));
        this.showAsListItem.setIcon(this.listSort ? NUM : NUM);
    }

    public /* synthetic */ void lambda$createView$2$PhotoPickerActivity(View view, int i) {
        if (this.selectedAlbum == null && this.searchResult.isEmpty()) {
            if (i < this.recentSearches.size()) {
                String str = (String) this.recentSearches.get(i);
                PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate = this.searchDelegate;
                if (photoPickerActivitySearchDelegate != null) {
                    photoPickerActivitySearchDelegate.shouldSearchText(str);
                } else {
                    this.searchItem.getSearchField().setText(str);
                    this.searchItem.getSearchField().setSelection(str.length());
                    processSearch(this.searchItem.getSearchField());
                }
            } else if (i == this.recentSearches.size() + 1) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ClearSearchAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new -$$Lambda$PhotoPickerActivity$V8BRocCcdRNwJ4kn9l4217_mJco(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                AlertDialog create = builder.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
            return;
        }
        ArrayList arrayList;
        AlbumEntry albumEntry = this.selectedAlbum;
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
                onListItemClick(view, this.searchResult.get(i));
            } else {
                int i2 = this.selectPhotoType;
                int i3 = i2 == 1 ? 1 : i2 == 2 ? 3 : i2 == 10 ? 10 : this.chatActivity == null ? 4 : 0;
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
                PhotoViewer.getInstance().openPhotoForSelect(arrayList2, i, i3, this.isDocumentsPicker, this.provider, this.chatActivity);
            }
        }
    }

    public /* synthetic */ void lambda$null$1$PhotoPickerActivity(DialogInterface dialogInterface, int i) {
        PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate = this.searchDelegate;
        if (photoPickerActivitySearchDelegate != null) {
            photoPickerActivitySearchDelegate.shouldClearRecentSearch();
        } else {
            clearRecentSearch();
        }
    }

    public /* synthetic */ boolean lambda$createView$3$PhotoPickerActivity(View view, int i) {
        if (this.listSort) {
            onListItemClick(view, this.selectedAlbum.photos.get(i));
            return true;
        }
        if (view instanceof PhotoAttachPhotoCell) {
            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) view;
            RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
            int isChecked = photoAttachPhotoCell.isChecked() ^ 1;
            this.shouldSelect = isChecked;
            recyclerViewItemRangeSelector.setIsActive(view, true, i, isChecked);
        }
        return false;
    }

    public /* synthetic */ void lambda$createView$5$PhotoPickerActivity(View view) {
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity == null || !chatActivity.isInScheduleMode()) {
            sendSelectedPhotos(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new -$$Lambda$PhotoPickerActivity$euYzkjcWzyeckwYcPmSeZL7u1Vc(this));
        }
    }

    public /* synthetic */ boolean lambda$createView$8$PhotoPickerActivity(View view) {
        View view2 = view;
        ChatActivity chatActivity = this.chatActivity;
        if (!(chatActivity == null || this.maxSelectedPhotos == 1)) {
            chatActivity.getCurrentChat();
            User currentUser = this.chatActivity.getCurrentUser();
            if (this.chatActivity.getCurrentEncryptedChat() != null) {
                return false;
            }
            if (this.sendPopupLayout == null) {
                this.sendPopupLayout = new ActionBarPopupWindowLayout(getParentActivity());
                this.sendPopupLayout.setAnimationEnabled(false);
                this.sendPopupLayout.setOnTouchListener(new OnTouchListener() {
                    private Rect popupRect = new Rect();

                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() == 0 && PhotoPickerActivity.this.sendPopupWindow != null && PhotoPickerActivity.this.sendPopupWindow.isShowing()) {
                            view.getHitRect(this.popupRect);
                            if (!this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                                PhotoPickerActivity.this.sendPopupWindow.dismiss();
                            }
                        }
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new -$$Lambda$PhotoPickerActivity$FuPSMH43Esnvar_zrDiwr0H_zbJU(this));
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
                        this.sendPopupLayout.addView(this.itemCells[i], LayoutHelper.createFrame(-1, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i * 48), 0.0f, 0.0f));
                        this.itemCells[i].setOnClickListener(new -$$Lambda$PhotoPickerActivity$Yld4xu1nRagAfOvzXJcO7zmBuRw(this, i));
                    }
                }
                this.sendPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
                this.sendPopupWindow.setAnimationEnabled(false);
                this.sendPopupWindow.setAnimationStyle(NUM);
                this.sendPopupWindow.setOutsideTouchable(true);
                this.sendPopupWindow.setClippingEnabled(true);
                this.sendPopupWindow.setInputMethodMode(2);
                this.sendPopupWindow.setSoftInputMode(0);
                this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
            }
            this.sendPopupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.sendPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            view2.getLocationInWindow(iArr);
            this.sendPopupWindow.showAtLocation(view2, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
            this.sendPopupWindow.dimBehind();
            view2.performHapticFeedback(3, 2);
        }
        return false;
    }

    public /* synthetic */ void lambda$null$6$PhotoPickerActivity(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$null$7$PhotoPickerActivity(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new -$$Lambda$PhotoPickerActivity$euYzkjcWzyeckwYcPmSeZL7u1Vc(this));
        } else if (i == 1) {
            sendSelectedPhotos(true, 0);
        }
    }

    public void setLayoutViews(FrameLayout frameLayout, FrameLayout frameLayout2, View view, View view2, EditTextEmoji editTextEmoji) {
        this.frameLayout2 = frameLayout;
        this.writeButtonContainer = frameLayout2;
        this.commentTextView = editTextEmoji;
        this.selectedCountView = view;
        this.shadow = view2;
        this.needsBottomLayout = false;
    }

    private void applyCaption() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null && editTextEmoji.length() > 0) {
            Object obj = this.selectedPhotos.get(this.selectedPhotosOrder.get(0));
            if (obj instanceof PhotoEntry) {
                ((PhotoEntry) obj).caption = this.commentTextView.getText().toString();
            } else if (obj instanceof SearchImage) {
                ((SearchImage) obj).caption = this.commentTextView.getText().toString();
            }
        }
    }

    private void onListItemClick(View view, Object obj) {
        boolean z = false;
        int i = 1;
        obj = addToSelectedPhotos(obj, -1) == -1 ? 1 : null;
        if (view instanceof SharedDocumentCell) {
            PhotoEntry photoEntry = (PhotoEntry) this.selectedAlbum.photos.get(((Integer) view.getTag()).intValue());
            SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
            if (this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)) >= 0) {
                z = true;
            }
            sharedDocumentCell.setChecked(z, true);
        }
        if (obj == null) {
            i = 2;
        }
        updatePhotosButton(i);
        this.delegate.selectedPhotosChanged();
    }

    public void clearRecentSearch() {
        this.recentSearches.clear();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        saveRecentSearch();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
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
            getParentActivity().getWindow().setSoftInputMode(16);
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
            editTextEmoji.setText(this.caption);
        }
    }

    public void setInitialSearchString(String str) {
        this.initialSearchString = str;
    }

    private void saveRecentSearch() {
        int i = 0;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("web_recent_search", 0).edit();
        edit.clear();
        edit.putInt("count", this.recentSearches.size());
        int size = this.recentSearches.size();
        while (i < size) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("recent");
            stringBuilder.append(i);
            edit.putString(stringBuilder.toString(), (String) this.recentSearches.get(i));
            i++;
        }
        edit.commit();
    }

    private void loadRecentSearch() {
        int i = 0;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("web_recent_search", 0);
        int i2 = sharedPreferences.getInt("count", 0);
        while (i < i2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("recent");
            stringBuilder.append(i);
            String string = sharedPreferences.getString(stringBuilder.toString(), null);
            if (string != null) {
                this.recentSearches.add(string);
                i++;
            } else {
                return;
            }
        }
    }

    private void processSearch(EditText editText) {
        if (editText.getText().length() != 0) {
            String obj = editText.getText().toString();
            int size = this.recentSearches.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                if (((String) this.recentSearches.get(i)).equalsIgnoreCase(obj)) {
                    this.recentSearches.remove(i);
                    break;
                }
            }
            this.recentSearches.add(0, obj);
            while (this.recentSearches.size() > 20) {
                ArrayList arrayList = this.recentSearches;
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
            if (this.lastSearchString.length() == 0) {
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
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(z ? Integer.valueOf(1) : null);
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
            frameLayout = this.writeButtonContainer;
            property = View.SCALE_Y;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            frameLayout = this.writeButtonContainer;
            property = View.ALPHA;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            View view = this.selectedCountView;
            property = View.SCALE_X;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
            view = this.selectedCountView;
            property = View.SCALE_Y;
            fArr = new float[1];
            if (z) {
                f2 = 1.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
            View view2 = this.selectedCountView;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!z) {
                f3 = 0.0f;
            }
            fArr2[0] = f3;
            arrayList.add(ObjectAnimator.ofFloat(view2, property2, fArr2));
            FrameLayout frameLayout2 = this.frameLayout2;
            Property property3 = View.TRANSLATION_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 0.0f : (float) AndroidUtilities.dp(48.0f);
            arrayList.add(ObjectAnimator.ofFloat(frameLayout2, property3, fArr3));
            view2 = this.shadow;
            property3 = View.TRANSLATION_Y;
            fArr3 = new float[1];
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            fArr3[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view2, property3, fArr3));
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
                        PhotoPickerActivity.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(PhotoPickerActivity.this.animatorSet)) {
                        PhotoPickerActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
            View view3 = this.selectedCountView;
            if (z) {
                f2 = 1.0f;
            }
            view3.setScaleY(f2);
            view3 = this.selectedCountView;
            if (!z) {
                f3 = 0.0f;
            }
            view3.setAlpha(f3);
            this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
            view3 = this.shadow;
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            view3.setTranslationY(f);
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

    private void updateCheckedPhotoIndices() {
        if (this.allowIndices) {
            int childCount = this.listView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    Integer num = (Integer) childAt.getTag();
                    AlbumEntry albumEntry = this.selectedAlbum;
                    int i2 = -1;
                    if (albumEntry != null) {
                        PhotoEntry photoEntry = (PhotoEntry) albumEntry.photos.get(num.intValue());
                        if (this.allowIndices) {
                            i2 = this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        }
                        photoAttachPhotoCell.setNum(i2);
                    } else {
                        SearchImage searchImage = (SearchImage) this.searchResult.get(num.intValue());
                        if (this.allowIndices) {
                            i2 = this.selectedPhotosOrder.indexOf(searchImage.id);
                        }
                        photoAttachPhotoCell.setNum(i2);
                    }
                } else if (childAt instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) childAt).setChecked(this.selectedPhotosOrder.indexOf(Integer.valueOf(((PhotoEntry) this.selectedAlbum.photos.get(((Integer) childAt.getTag()).intValue())).imageId)) != 0, false);
                }
            }
        }
    }

    private PhotoAttachPhotoCell getCellForIndex(int i) {
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                int intValue = ((Integer) photoAttachPhotoCell.getTag()).intValue();
                AlbumEntry albumEntry = this.selectedAlbum;
                if (albumEntry == null ? intValue < 0 || intValue >= this.searchResult.size() : intValue < 0 || intValue >= albumEntry.photos.size()) {
                    if (intValue == i) {
                        return photoAttachPhotoCell;
                    }
                }
            }
        }
        return null;
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
        if (z) {
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                AndroidUtilities.showKeyboard(actionBarMenuItem.getSearchField());
            }
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
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = i == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        view = this.selectedCountView;
        property = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (i != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view, property, fArr2);
        animatorSet.playTogether(animatorArr);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(180);
        animatorSet.start();
    }

    private void updateSearchInterface() {
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (this.searching && this.searchResult.isEmpty()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
    }

    private void searchBotUser(boolean z) {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TL_contacts_resolveUsername tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            tL_contacts_resolveUsername.username = z ? instance.gifSearchBot : instance.imageSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_resolveUsername, new -$$Lambda$PhotoPickerActivity$vgAbSreHE_zFOZzJ7Rl5avQ-oWY(this, z));
        }
    }

    public /* synthetic */ void lambda$searchBotUser$10$PhotoPickerActivity(boolean z, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PhotoPickerActivity$guaIjFlbWkiXUL3zLroB4xOXtgE(this, tLObject, z));
        }
    }

    public /* synthetic */ void lambda$null$9$PhotoPickerActivity(TLObject tLObject, boolean z) {
        TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_resolvedPeer.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_resolvedPeer.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, true, true);
        String str = this.lastSearchImageString;
        this.lastSearchImageString = null;
        searchImages(z, str, "", false);
    }

    private void searchImages(boolean z, String str, String str2, boolean z2) {
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
        if (userOrChat instanceof User) {
            User user = (User) userOrChat;
            TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TL_messages_getInlineBotResults();
            if (str == null) {
                str = "";
            }
            tL_messages_getInlineBotResults.query = str;
            tL_messages_getInlineBotResults.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
            tL_messages_getInlineBotResults.offset = str2;
            ChatActivity chatActivity = this.chatActivity;
            if (chatActivity != null) {
                int dialogId = (int) chatActivity.getDialogId();
                if (dialogId != 0) {
                    tL_messages_getInlineBotResults.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(dialogId);
                } else {
                    tL_messages_getInlineBotResults.peer = new TL_inputPeerEmpty();
                }
            } else {
                tL_messages_getInlineBotResults.peer = new TL_inputPeerEmpty();
            }
            int i = this.lastSearchToken + 1;
            this.lastSearchToken = i;
            this.imageReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getInlineBotResults, new -$$Lambda$PhotoPickerActivity$YlYtGKi2LwgC6UMh4JLKKv8JxjQ(this, i, z, user));
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.imageReqId, this.classGuid);
            return;
        }
        if (z2) {
            searchBotUser(z);
        }
    }

    public /* synthetic */ void lambda$searchImages$12$PhotoPickerActivity(int i, boolean z, User user, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PhotoPickerActivity$POfthmFVIca0ai_RsubQCCfjmDE(this, i, tLObject, z, user));
    }

    public /* synthetic */ void lambda$null$11$PhotoPickerActivity(int i, TLObject tLObject, boolean z, User user) {
        if (i == this.lastSearchToken) {
            int i2;
            i = this.searchResult.size();
            if (tLObject != null) {
                messages_BotResults messages_botresults = (messages_BotResults) tLObject;
                this.nextImagesSearchOffset = messages_botresults.next_offset;
                int size = messages_botresults.results.size();
                i2 = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    BotInlineResult botInlineResult = (BotInlineResult) messages_botresults.results.get(i3);
                    if (!z) {
                        if (!"photo".equals(botInlineResult.type)) {
                        }
                    }
                    if (z) {
                        if (!"gif".equals(botInlineResult.type)) {
                        }
                    }
                    if (!this.searchResultKeys.containsKey(botInlineResult.id)) {
                        SearchImage searchImage = new SearchImage();
                        PhotoSize closestPhotoSizeWithSize;
                        int i4;
                        DocumentAttribute documentAttribute;
                        if (!z || botInlineResult.document == null) {
                            if (!z) {
                                Photo photo = botInlineResult.photo;
                                if (photo != null) {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                                    PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, 320);
                                    if (closestPhotoSizeWithSize != null) {
                                        searchImage.width = closestPhotoSizeWithSize.w;
                                        searchImage.height = closestPhotoSizeWithSize.h;
                                        searchImage.photoSize = closestPhotoSizeWithSize;
                                        searchImage.photo = botInlineResult.photo;
                                        searchImage.size = closestPhotoSizeWithSize.size;
                                        searchImage.thumbPhotoSize = closestPhotoSizeWithSize2;
                                    }
                                }
                            }
                            if (botInlineResult.content != null) {
                                for (i4 = 0; i4 < botInlineResult.content.attributes.size(); i4++) {
                                    documentAttribute = (DocumentAttribute) botInlineResult.content.attributes.get(i4);
                                    if (documentAttribute instanceof TL_documentAttributeImageSize) {
                                        searchImage.width = documentAttribute.w;
                                        searchImage.height = documentAttribute.h;
                                        break;
                                    }
                                }
                                WebDocument webDocument = botInlineResult.thumb;
                                if (webDocument != null) {
                                    searchImage.thumbUrl = webDocument.url;
                                } else {
                                    searchImage.thumbUrl = null;
                                }
                                webDocument = botInlineResult.content;
                                searchImage.imageUrl = webDocument.url;
                                if (z) {
                                    i4 = 0;
                                } else {
                                    i4 = webDocument.size;
                                }
                                searchImage.size = i4;
                            }
                        } else {
                            for (i4 = 0; i4 < botInlineResult.document.attributes.size(); i4++) {
                                documentAttribute = (DocumentAttribute) botInlineResult.document.attributes.get(i4);
                                if ((documentAttribute instanceof TL_documentAttributeImageSize) || (documentAttribute instanceof TL_documentAttributeVideo)) {
                                    searchImage.width = documentAttribute.w;
                                    searchImage.height = documentAttribute.h;
                                    break;
                                }
                            }
                            Document document = botInlineResult.document;
                            searchImage.document = document;
                            searchImage.size = 0;
                            Photo photo2 = botInlineResult.photo;
                            if (!(photo2 == null || document == null)) {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, this.itemSize, true);
                                if (closestPhotoSizeWithSize != null) {
                                    botInlineResult.document.thumbs.add(closestPhotoSizeWithSize);
                                    document = botInlineResult.document;
                                    document.flags |= 1;
                                }
                            }
                        }
                        searchImage.id = botInlineResult.id;
                        searchImage.type = z;
                        searchImage.inlineResult = botInlineResult;
                        searchImage.params = new HashMap();
                        searchImage.params.put("id", botInlineResult.id);
                        HashMap hashMap = searchImage.params;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("");
                        stringBuilder.append(messages_botresults.query_id);
                        hashMap.put("query_id", stringBuilder.toString());
                        searchImage.params.put("bot_name", user.username);
                        this.searchResult.add(searchImage);
                        this.searchResultKeys.put(searchImage.id, searchImage);
                        i2++;
                    }
                }
                boolean z2 = i == this.searchResult.size() || this.nextImagesSearchOffset == null;
                this.imageSearchEndReached = z2;
            } else {
                i2 = 0;
            }
            this.searching = false;
            if (i2 != 0) {
                this.listAdapter.notifyItemRangeInserted(i, i2);
            } else if (this.imageSearchEndReached) {
                this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
            }
            if (this.searching && this.searchResult.isEmpty()) {
                this.emptyView.showProgress();
            } else {
                this.emptyView.showTextView();
            }
        }
    }

    public void setDelegate(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
        this.delegate = photoPickerActivityDelegate;
    }

    public void setSearchDelegate(PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate) {
        this.searchDelegate = photoPickerActivitySearchDelegate;
    }

    private void sendSelectedPhotos(boolean z, int i) {
        if (!this.selectedPhotos.isEmpty() && this.delegate != null && !this.sendPressed) {
            applyCaption();
            this.sendPressed = true;
            this.delegate.actionButtonPressed(false, z, i);
            if (this.selectPhotoType != 2) {
                finishFragment();
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[11];
        themeDescriptionArr[0] = new ThemeDescription(this.sizeNotifierFrameLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "dialogButtonSelector");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "chat_messagePanelHint");
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        themeDescriptionArr[7] = new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "dialogBackground");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{Theme.chat_attachEmptyDrawable}, null, "chat_attachEmptyImage");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, null, null, "chat_attachPhotoBackground");
        return themeDescriptionArr;
    }
}
