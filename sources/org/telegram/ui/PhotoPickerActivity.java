package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
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
import org.telegram.messenger.UserConfig;
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
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate;
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
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.RecyclerViewItemRangeSelector;
import org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenterDelegate {
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
    private ActionBarMenuSubItem[] itemCells;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    private int itemSize = 100;
    private int itemsPerRow = 3;
    private String lastSearchImageString;
    private String lastSearchString;
    private int lastSearchToken;
    private GridLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingRecent;
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
            ArrayList access$500;
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                access$500 = PhotoPickerActivity.this.recentImages;
            } else {
                access$500 = PhotoPickerActivity.this.searchResult;
            }
            access$000.setPhotoEntry((SearchImage) access$500.get(i), true, false);
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
                            continue;
                        }
                    } else if (intValue < 0) {
                        continue;
                    } else if (intValue >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                        continue;
                    }
                    if (intValue == i) {
                        photoAttachPhotoCell.showCheck(true);
                        return;
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
            ArrayList access$500;
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                access$500 = PhotoPickerActivity.this.recentImages;
            } else {
                access$500 = PhotoPickerActivity.this.searchResult;
            }
            if (i < 0 || i >= access$500.size() || !PhotoPickerActivity.this.selectedPhotos.containsKey(((SearchImage) access$500.get(i)).id)) {
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

        /* JADX WARNING: Removed duplicated region for block: B:35:0x00d0  */
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
            goto L_0x0096;
        L_0x004d:
            return r3;
        L_0x004e:
            r10 = org.telegram.ui.PhotoPickerActivity.this;
            r10 = r10.searchResult;
            r10 = r10.isEmpty();
            if (r10 == 0) goto L_0x0069;
        L_0x005a:
            r10 = org.telegram.ui.PhotoPickerActivity.this;
            r10 = r10.lastSearchString;
            if (r10 != 0) goto L_0x0069;
        L_0x0062:
            r10 = org.telegram.ui.PhotoPickerActivity.this;
            r10 = r10.recentImages;
            goto L_0x006f;
        L_0x0069:
            r10 = org.telegram.ui.PhotoPickerActivity.this;
            r10 = r10.searchResult;
        L_0x006f:
            if (r9 < 0) goto L_0x00de;
        L_0x0071:
            r0 = r10.size();
            if (r9 < r0) goto L_0x0078;
        L_0x0077:
            goto L_0x00de;
        L_0x0078:
            r10 = r10.get(r9);
            r10 = (org.telegram.messenger.MediaController.SearchImage) r10;
            r0 = org.telegram.ui.PhotoPickerActivity.this;
            r0 = r0.addToSelectedPhotos(r10, r3);
            if (r0 != r3) goto L_0x0094;
        L_0x0086:
            r0 = org.telegram.ui.PhotoPickerActivity.this;
            r0 = r0.selectedPhotosOrder;
            r10 = r10.id;
            r10 = r0.indexOf(r10);
            r0 = 1;
            goto L_0x0096;
        L_0x0094:
            r10 = r0;
            r0 = 0;
        L_0x0096:
            r4 = org.telegram.ui.PhotoPickerActivity.this;
            r4 = r4.listView;
            r4 = r4.getChildCount();
            r5 = 0;
        L_0x00a1:
            if (r5 >= r4) goto L_0x00cb;
        L_0x00a3:
            r6 = org.telegram.ui.PhotoPickerActivity.this;
            r6 = r6.listView;
            r6 = r6.getChildAt(r5);
            r7 = r6.getTag();
            r7 = (java.lang.Integer) r7;
            r7 = r7.intValue();
            if (r7 != r9) goto L_0x00c8;
        L_0x00b9:
            r6 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r6;
            r9 = org.telegram.ui.PhotoPickerActivity.this;
            r9 = r9.allowIndices;
            if (r9 == 0) goto L_0x00c4;
        L_0x00c3:
            r3 = r10;
        L_0x00c4:
            r6.setChecked(r3, r0, r2);
            goto L_0x00cb;
        L_0x00c8:
            r5 = r5 + 1;
            goto L_0x00a1;
        L_0x00cb:
            r9 = org.telegram.ui.PhotoPickerActivity.this;
            if (r0 == 0) goto L_0x00d0;
        L_0x00cf:
            goto L_0x00d1;
        L_0x00d0:
            r1 = 2;
        L_0x00d1:
            r9.updatePhotosButton(r1);
            r9 = org.telegram.ui.PhotoPickerActivity.this;
            r9 = r9.delegate;
            r9.selectedPhotosChanged();
            return r10;
        L_0x00de:
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
                if (PhotoPickerActivity.this.selectedAlbum == null) {
                    ArrayList access$500;
                    if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                        access$500 = PhotoPickerActivity.this.recentImages;
                    } else {
                        access$500 = PhotoPickerActivity.this.searchResult;
                    }
                    if (i >= 0 && i < access$500.size()) {
                        PhotoPickerActivity.this.addToSelectedPhotos(access$500.get(i), -1);
                    } else {
                        return;
                    }
                } else if (i >= 0 && i < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                    PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                    photoEntry.editedInfo = videoEditedInfo;
                    PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
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
    private ArrayList<SearchImage> recentImages;
    private RectF rect = new RectF();
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
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private TextPaint textPaint = new TextPaint(1);
    private int type;
    private ImageView writeButton;
    protected FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public interface PhotoPickerActivityDelegate {
        void actionButtonPressed(boolean z, boolean z2, int i);

        void onCaptionChanged(CharSequence charSequence);

        void selectedPhotosChanged();
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
                int adapterPosition = viewHolder.getAdapterPosition();
                if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                    if (adapterPosition >= PhotoPickerActivity.this.recentImages.size()) {
                        z = false;
                    }
                    return z;
                } else if (adapterPosition >= PhotoPickerActivity.this.searchResult.size()) {
                    z = false;
                }
            }
            return z;
        }

        public int getItemCount() {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return PhotoPickerActivity.this.selectedAlbum.photos.size();
            }
            if (PhotoPickerActivity.this.searchResult.isEmpty()) {
                return 0;
            }
            return PhotoPickerActivity.this.searchResult.size() + (PhotoPickerActivity.this.imageSearchEndReached ^ 1);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View frameLayout;
            if (i != 0) {
                frameLayout = new FrameLayout(this.mContext);
                RadialProgressView radialProgressView = new RadialProgressView(this.mContext);
                radialProgressView.setProgressColor(-11371101);
                frameLayout.addView(radialProgressView, LayoutHelper.createFrame(-1, -1.0f));
            } else {
                frameLayout = new PhotoAttachPhotoCell(this.mContext);
                frameLayout.setDelegate(new PhotoAttachPhotoCellDelegate() {
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
                        SearchImage searchImage;
                        AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                        if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                            searchImage = (SearchImage) PhotoPickerActivity.this.recentImages.get(intValue);
                        } else {
                            searchImage = (SearchImage) PhotoPickerActivity.this.searchResult.get(intValue);
                        }
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
                frameLayout.getCheckFrame().setVisibility(PhotoPickerActivity.this.selectPhotoType != 0 ? 8 : 0);
            }
            return new Holder(frameLayout);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
                photoAttachPhotoCell.setItemSize(PhotoPickerActivity.this.itemSize);
                BackupImageView imageView = photoAttachPhotoCell.getImageView();
                photoAttachPhotoCell.setTag(Integer.valueOf(i));
                int i2 = false;
                imageView.setOrientation(0, true);
                int i3 = -1;
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                    photoAttachPhotoCell.setPhotoEntry(photoEntry, true, false);
                    if (PhotoPickerActivity.this.allowIndices) {
                        i3 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                    }
                    photoAttachPhotoCell.setChecked(i3, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                    i = PhotoViewer.isShowingImage(photoEntry.path);
                } else {
                    SearchImage searchImage;
                    if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                        searchImage = (SearchImage) PhotoPickerActivity.this.recentImages.get(i);
                    } else {
                        searchImage = (SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                    }
                    photoAttachPhotoCell.setPhotoEntry(searchImage, true, false);
                    photoAttachPhotoCell.getVideoInfoContainer().setVisibility(4);
                    if (PhotoPickerActivity.this.allowIndices) {
                        i3 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(searchImage.id);
                    }
                    photoAttachPhotoCell.setChecked(i3, PhotoPickerActivity.this.selectedPhotos.containsKey(searchImage.id), false);
                    i = PhotoViewer.isShowingImage(searchImage.getPathToAttach());
                }
                imageView.getImageReceiver().setVisible(i ^ 1, true);
                CheckBox2 checkBox = photoAttachPhotoCell.getCheckBox();
                if (!(PhotoPickerActivity.this.selectPhotoType == 0 && i == 0)) {
                    i2 = 8;
                }
                checkBox.setVisibility(i2);
            } else if (itemViewType == 1) {
                LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.width = PhotoPickerActivity.this.itemSize;
                    layoutParams.height = PhotoPickerActivity.this.itemSize;
                    viewHolder.itemView.setLayoutParams(layoutParams);
                }
            }
        }

        public int getItemViewType(int i) {
            return (PhotoPickerActivity.this.selectedAlbum != null || ((PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null && i < PhotoPickerActivity.this.recentImages.size()) || i < PhotoPickerActivity.this.searchResult.size())) ? 0 : 1;
        }
    }

    public PhotoPickerActivity(int i, AlbumEntry albumEntry, HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, ArrayList<SearchImage> arrayList2, int i2, boolean z, ChatActivity chatActivity) {
        ArrayList arrayList22;
        this.selectedAlbum = albumEntry;
        this.selectedPhotos = hashMap;
        this.selectedPhotosOrder = arrayList;
        this.type = i;
        if (arrayList22 == null) {
            arrayList22 = new ArrayList();
        }
        this.recentImages = arrayList22;
        this.selectPhotoType = i2;
        this.chatActivity = chatActivity;
        this.allowCaption = z;
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
        EditTextBoldCursor searchField;
        Context context2 = context;
        String str = "dialogBackground";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        String str2 = "dialogTextBlack";
        this.actionBar.setTitleColor(Theme.getColor(str2));
        this.actionBar.setItemsColor(Theme.getColor(str2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonImage(NUM);
        AlbumEntry albumEntry = this.selectedAlbum;
        String str3 = "SearchGifsTitle";
        String str4 = "SearchImagesTitle";
        boolean z = true;
        if (albumEntry != null) {
            this.actionBar.setTitle(albumEntry.bucketName);
        } else {
            i = this.type;
            if (i == 0) {
                this.actionBar.setTitle(LocaleController.getString(str4, NUM));
            } else if (i == 1) {
                this.actionBar.setTitle(LocaleController.getString(str3, NUM));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PhotoPickerActivity.this.finishFragment();
                }
            }
        });
        if (this.selectedAlbum == null) {
            this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
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
                        PhotoPickerActivity.this.imageSearchEndReached = true;
                        PhotoPickerActivity.this.searching = false;
                        if (PhotoPickerActivity.this.imageReqId != 0) {
                            ConnectionsManager.getInstance(PhotoPickerActivity.this.currentAccount).cancelRequest(PhotoPickerActivity.this.imageReqId, true);
                            PhotoPickerActivity.this.imageReqId = 0;
                        }
                        PhotoPickerActivity.this.emptyView.setText("");
                        PhotoPickerActivity.this.updateSearchInterface();
                    }
                }

                public void onSearchPressed(EditText editText) {
                    PhotoPickerActivity.this.processSearch(editText);
                }
            });
            searchField = this.searchItem.getSearchField();
            searchField.setTextColor(Theme.getColor(str2));
            searchField.setCursorColor(Theme.getColor(str2));
            searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        }
        if (this.selectedAlbum == null) {
            i = this.type;
            if (i == 0) {
                this.searchItem.setSearchFieldHint(LocaleController.getString(str4, NUM));
            } else if (i == 1) {
                this.searchItem.setSearchFieldHint(LocaleController.getString(str3, NUM));
            }
        }
        this.sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private int lastItemSize;
            private int lastNotifyWidth;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                i2 = MeasureSpec.getSize(i2);
                int size = MeasureSpec.getSize(i);
                if (AndroidUtilities.isTablet()) {
                    PhotoPickerActivity.this.itemsPerRow = 4;
                } else {
                    Point point = AndroidUtilities.displaySize;
                    if (point.x > point.y) {
                        PhotoPickerActivity.this.itemsPerRow = 4;
                    } else {
                        PhotoPickerActivity.this.itemsPerRow = 3;
                    }
                }
                this.ignoreLayout = true;
                PhotoPickerActivity.this.itemSize = ((size - AndroidUtilities.dp(12.0f)) - AndroidUtilities.dp(10.0f)) / PhotoPickerActivity.this.itemsPerRow;
                if (this.lastItemSize != PhotoPickerActivity.this.itemSize) {
                    this.lastItemSize = PhotoPickerActivity.this.itemSize;
                    AndroidUtilities.runOnUIThread(new -$$Lambda$PhotoPickerActivity$4$_Zbxsw9EOyk58Bs7hGSJ19K5aQo(this));
                }
                PhotoPickerActivity.this.layoutManager.setSpanCount((PhotoPickerActivity.this.itemSize * PhotoPickerActivity.this.itemsPerRow) + (AndroidUtilities.dp(5.0f) * (PhotoPickerActivity.this.itemsPerRow - 1)));
                this.ignoreLayout = false;
                onMeasureInternal(i, MeasureSpec.makeMeasureSpec(i2, NUM));
            }

            public /* synthetic */ void lambda$onMeasure$0$PhotoPickerActivity$4() {
                PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
            }

            private void onMeasureInternal(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int i3 = 0;
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    EditTextEmoji editTextEmoji = PhotoPickerActivity.this.commentTextView;
                    if (editTextEmoji != null) {
                        this.ignoreLayout = true;
                        editTextEmoji.hideEmojiView();
                        this.ignoreLayout = false;
                    }
                } else if (!AndroidUtilities.isInMultiwindow) {
                    PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                    if (photoPickerActivity.commentTextView != null && photoPickerActivity.frameLayout2.getParent() == this) {
                        size2 -= PhotoPickerActivity.this.commentTextView.getEmojiPadding();
                        i2 = MeasureSpec.makeMeasureSpec(size2, NUM);
                    }
                }
                int childCount = getChildCount();
                while (i3 < childCount) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8)) {
                        EditTextEmoji editTextEmoji2 = PhotoPickerActivity.this.commentTextView;
                        if (editTextEmoji2 == null || !editTextEmoji2.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                    i3++;
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:55:0x00fe  */
            /* JADX WARNING: Removed duplicated region for block: B:54:0x00f5  */
            /* JADX WARNING: Removed duplicated region for block: B:46:0x00d6  */
            /* JADX WARNING: Removed duplicated region for block: B:39:0x00bc  */
            /* JADX WARNING: Removed duplicated region for block: B:54:0x00f5  */
            /* JADX WARNING: Removed duplicated region for block: B:55:0x00fe  */
            public void onLayout(boolean r9, int r10, int r11, int r12, int r13) {
                /*
                r8 = this;
                r9 = r8.lastNotifyWidth;
                r12 = r12 - r10;
                if (r9 == r12) goto L_0x0035;
            L_0x0005:
                r8.lastNotifyWidth = r12;
                r9 = org.telegram.ui.PhotoPickerActivity.this;
                r9 = r9.listAdapter;
                if (r9 == 0) goto L_0x0018;
            L_0x000f:
                r9 = org.telegram.ui.PhotoPickerActivity.this;
                r9 = r9.listAdapter;
                r9.notifyDataSetChanged();
            L_0x0018:
                r9 = org.telegram.ui.PhotoPickerActivity.this;
                r9 = r9.sendPopupWindow;
                if (r9 == 0) goto L_0x0035;
            L_0x0020:
                r9 = org.telegram.ui.PhotoPickerActivity.this;
                r9 = r9.sendPopupWindow;
                r9 = r9.isShowing();
                if (r9 == 0) goto L_0x0035;
            L_0x002c:
                r9 = org.telegram.ui.PhotoPickerActivity.this;
                r9 = r9.sendPopupWindow;
                r9.dismiss();
            L_0x0035:
                r9 = r8.getChildCount();
                r10 = org.telegram.ui.PhotoPickerActivity.this;
                r0 = r10.commentTextView;
                r1 = 0;
                if (r0 == 0) goto L_0x0067;
            L_0x0040:
                r10 = r10.frameLayout2;
                r10 = r10.getParent();
                if (r10 != r8) goto L_0x0067;
            L_0x0048:
                r10 = r8.getKeyboardHeight();
                r0 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
                if (r10 > r0) goto L_0x0067;
            L_0x0054:
                r10 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
                if (r10 != 0) goto L_0x0067;
            L_0x0058:
                r10 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r10 != 0) goto L_0x0067;
            L_0x005e:
                r10 = org.telegram.ui.PhotoPickerActivity.this;
                r10 = r10.commentTextView;
                r10 = r10.getEmojiPadding();
                goto L_0x0068;
            L_0x0067:
                r10 = 0;
            L_0x0068:
                r8.setBottomClip(r10);
            L_0x006b:
                if (r1 >= r9) goto L_0x0115;
            L_0x006d:
                r0 = r8.getChildAt(r1);
                r2 = r0.getVisibility();
                r3 = 8;
                if (r2 != r3) goto L_0x007b;
            L_0x0079:
                goto L_0x0111;
            L_0x007b:
                r2 = r0.getLayoutParams();
                r2 = (android.widget.FrameLayout.LayoutParams) r2;
                r3 = r0.getMeasuredWidth();
                r4 = r0.getMeasuredHeight();
                r5 = r2.gravity;
                r6 = -1;
                if (r5 != r6) goto L_0x0090;
            L_0x008e:
                r5 = 51;
            L_0x0090:
                r6 = r5 & 7;
                r5 = r5 & 112;
                r6 = r6 & 7;
                r7 = 1;
                if (r6 == r7) goto L_0x00ae;
            L_0x0099:
                r7 = 5;
                if (r6 == r7) goto L_0x00a4;
            L_0x009c:
                r6 = r2.leftMargin;
                r7 = r8.getPaddingLeft();
                r6 = r6 + r7;
                goto L_0x00b8;
            L_0x00a4:
                r6 = r12 - r3;
                r7 = r2.rightMargin;
                r6 = r6 - r7;
                r7 = r8.getPaddingRight();
                goto L_0x00b7;
            L_0x00ae:
                r6 = r12 - r3;
                r6 = r6 / 2;
                r7 = r2.leftMargin;
                r6 = r6 + r7;
                r7 = r2.rightMargin;
            L_0x00b7:
                r6 = r6 - r7;
            L_0x00b8:
                r7 = 16;
                if (r5 == r7) goto L_0x00d6;
            L_0x00bc:
                r7 = 48;
                if (r5 == r7) goto L_0x00ce;
            L_0x00c0:
                r7 = 80;
                if (r5 == r7) goto L_0x00c7;
            L_0x00c4:
                r2 = r2.topMargin;
                goto L_0x00e3;
            L_0x00c7:
                r5 = r13 - r10;
                r5 = r5 - r11;
                r5 = r5 - r4;
                r2 = r2.bottomMargin;
                goto L_0x00e1;
            L_0x00ce:
                r2 = r2.topMargin;
                r5 = r8.getPaddingTop();
                r2 = r2 + r5;
                goto L_0x00e3;
            L_0x00d6:
                r5 = r13 - r10;
                r5 = r5 - r11;
                r5 = r5 - r4;
                r5 = r5 / 2;
                r7 = r2.topMargin;
                r5 = r5 + r7;
                r2 = r2.bottomMargin;
            L_0x00e1:
                r2 = r5 - r2;
            L_0x00e3:
                r5 = org.telegram.ui.PhotoPickerActivity.this;
                r5 = r5.commentTextView;
                if (r5 == 0) goto L_0x010c;
            L_0x00e9:
                r5 = r5.isPopupView(r0);
                if (r5 == 0) goto L_0x010c;
            L_0x00ef:
                r2 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r2 == 0) goto L_0x00fe;
            L_0x00f5:
                r2 = r8.getMeasuredHeight();
                r5 = r0.getMeasuredHeight();
                goto L_0x010b;
            L_0x00fe:
                r2 = r8.getMeasuredHeight();
                r5 = r8.getKeyboardHeight();
                r2 = r2 + r5;
                r5 = r0.getMeasuredHeight();
            L_0x010b:
                r2 = r2 - r5;
            L_0x010c:
                r3 = r3 + r6;
                r4 = r4 + r2;
                r0.layout(r6, r2, r3, r4);
            L_0x0111:
                r1 = r1 + 1;
                goto L_0x006b;
            L_0x0115:
                r8.notifyHeightChanged();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity$AnonymousClass4.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout.setBackgroundColor(Theme.getColor(str));
        this.fragmentView = this.sizeNotifierFrameLayout;
        this.listView = new RecyclerListView(context2);
        this.listView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass5 anonymousClass5 = new GridLayoutManager(context2, 4) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass5;
        recyclerListView.setLayoutManager(anonymousClass5);
        this.layoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int i) {
                return PhotoPickerActivity.this.itemSize + (i % PhotoPickerActivity.this.itemsPerRow != PhotoPickerActivity.this.itemsPerRow + -1 ? AndroidUtilities.dp(5.0f) : 0);
            }
        });
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setGlowColor(Theme.getColor(str));
        this.listView.setOnItemClickListener(new -$$Lambda$PhotoPickerActivity$zD_mr7IpQctZxAyf7EKDChtrGZc(this));
        this.listView.setOnItemLongClickListener(new -$$Lambda$PhotoPickerActivity$4u0PJyG8xINVMQEjkL37unlv3jE(this));
        this.itemRangeSelector = new RecyclerViewItemRangeSelector(new RecyclerViewItemRangeSelectorDelegate() {
            public int getItemCount() {
                return PhotoPickerActivity.this.listAdapter.getItemCount();
            }

            public void setSelected(View view, int i, boolean z) {
                if (z == PhotoPickerActivity.this.shouldSelect && (view instanceof PhotoAttachPhotoCell)) {
                    ((PhotoAttachPhotoCell) view).callDelegate();
                }
            }

            public boolean isSelected(int i) {
                Object valueOf;
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    valueOf = Integer.valueOf(((PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i)).imageId);
                } else {
                    SearchImage searchImage;
                    if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                        searchImage = (SearchImage) PhotoPickerActivity.this.recentImages.get(i);
                    } else {
                        searchImage = (SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                    }
                    valueOf = searchImage.id;
                }
                return PhotoPickerActivity.this.selectedPhotos.containsKey(valueOf);
            }

            public boolean isIndexSelectable(int i) {
                return PhotoPickerActivity.this.listAdapter.getItemViewType(i) == 0;
            }

            public void onStartStopSelection(boolean z) {
                PhotoPickerActivity.this.alertOnlyOnce = z;
                if (z) {
                    PhotoPickerActivity.this.parentLayout.requestDisallowInterceptTouchEvent(true);
                }
                PhotoPickerActivity.this.listView.hideSelector(true);
            }
        });
        this.listView.addOnItemTouchListener(this.itemRangeSelector);
        this.emptyView = new EmptyTextProgressView(context2);
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setProgressBarColor(-11371101);
        this.emptyView.setShowAtCenter(false);
        if (this.selectedAlbum != null) {
            this.emptyView.setText(LocaleController.getString("NoPhotos", NUM));
        } else {
            this.emptyView.setText("");
        }
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.selectPhotoType != 0 ? 0.0f : 48.0f));
        if (this.selectedAlbum == null) {
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1) {
                        AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    int i3;
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
            });
            updateSearchInterface();
        }
        if (this.needsBottomLayout) {
            this.shadow = new View(context2);
            this.shadow.setBackgroundResource(NUM);
            this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.frameLayout2 = new FrameLayout(context2);
            this.frameLayout2.setBackgroundColor(Theme.getColor(str));
            this.frameLayout2.setVisibility(4);
            this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
            this.frameLayout2.setOnTouchListener(-$$Lambda$PhotoPickerActivity$G6ab6ulK0rbaAFnb70ekz3wbWJM.INSTANCE);
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                editTextEmoji.onDestroy();
            }
            this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, null, 1);
            this.commentTextView.setFilters(new InputFilter[]{new LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
            this.commentTextView.setHint(LocaleController.getString("AddCaption", NUM));
            this.commentTextView.onResume();
            searchField = this.commentTextView.getEditText();
            searchField.setMaxLines(1);
            searchField.setSingleLine(true);
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
            this.writeButtonContainer = new FrameLayout(context2);
            this.writeButtonContainer.setVisibility(4);
            this.writeButtonContainer.setScaleX(0.2f);
            this.writeButtonContainer.setScaleY(0.2f);
            this.writeButtonContainer.setAlpha(0.0f);
            this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", NUM));
            this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
            this.writeButtonContainer.setOnClickListener(new -$$Lambda$PhotoPickerActivity$KmEu4SYAqKOt718fgSd59sdqoWM(this));
            this.writeButtonContainer.setOnLongClickListener(new -$$Lambda$PhotoPickerActivity$YrhMRxTTeoMxrldjT40BAA8Vglk(this));
            this.writeButton = new ImageView(context2);
            this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("dialogFloatingButton"), Theme.getColor("dialogFloatingButtonPressed"));
            if (VERSION.SDK_INT < 21) {
                Drawable mutate = context.getResources().getDrawable(NUM).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, this.writeButtonDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                this.writeButtonDrawable = combinedDrawable;
            }
            this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
            this.writeButton.setImageResource(NUM);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), Mode.MULTIPLY));
            this.writeButton.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 21) {
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedCountView = new View(context2) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, PhotoPickerActivity.this.selectedPhotosOrder.size()))});
                    int ceil = (int) Math.ceil((double) PhotoPickerActivity.this.textPaint.measureText(format));
                    int max = Math.max(AndroidUtilities.dp(16.0f) + ceil, AndroidUtilities.dp(24.0f));
                    int measuredWidth = getMeasuredWidth() / 2;
                    int measuredHeight = getMeasuredHeight() / 2;
                    PhotoPickerActivity.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor("dialogBackground"));
                    max /= 2;
                    int i = measuredWidth - max;
                    max += measuredWidth;
                    PhotoPickerActivity.this.rect.set((float) i, 0.0f, (float) max, (float) getMeasuredHeight());
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), PhotoPickerActivity.this.paint);
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                    PhotoPickerActivity.this.rect.set((float) (i + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (max - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), PhotoPickerActivity.this.paint);
                    canvas.drawText(format, (float) (measuredWidth - (ceil / 2)), (float) AndroidUtilities.dp(16.2f), PhotoPickerActivity.this.textPaint);
                }
            };
            this.selectedCountView.setAlpha(0.0f);
            this.selectedCountView.setScaleX(0.2f);
            this.selectedCountView.setScaleY(0.2f);
            this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
            if (this.selectPhotoType != 0) {
                this.commentTextView.setVisibility(8);
            }
        }
        if ((this.selectedAlbum == null && this.type != 0) || !this.allowOrder) {
            z = false;
        }
        this.allowIndices = z;
        this.listView.setEmptyView(this.emptyView);
        updatePhotosButton(0);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$PhotoPickerActivity(View view, int i) {
        ArrayList arrayList;
        AlbumEntry albumEntry = this.selectedAlbum;
        if (albumEntry != null) {
            arrayList = albumEntry.photos;
        } else if (this.searchResult.isEmpty() && this.lastSearchString == null) {
            arrayList = this.recentImages;
        } else {
            arrayList = this.searchResult;
        }
        ArrayList arrayList2 = arrayList;
        if (i >= 0 && i < arrayList2.size()) {
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                AndroidUtilities.hideKeyboard(actionBarMenuItem.getSearchField());
            }
            int i2 = this.selectPhotoType;
            int i3 = i2 == 1 ? 1 : i2 == 2 ? 3 : i2 == 10 ? 10 : this.chatActivity == null ? 4 : 0;
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            PhotoViewer.getInstance().openPhotoForSelect(arrayList2, i, i3, this.provider, this.chatActivity);
        }
    }

    public /* synthetic */ boolean lambda$createView$1$PhotoPickerActivity(View view, int i) {
        if (view instanceof PhotoAttachPhotoCell) {
            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) view;
            RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
            int isChecked = photoAttachPhotoCell.isChecked() ^ 1;
            this.shouldSelect = isChecked;
            recyclerViewItemRangeSelector.setIsActive(view, true, i, isChecked);
        }
        return false;
    }

    public /* synthetic */ void lambda$createView$3$PhotoPickerActivity(View view) {
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity == null || !chatActivity.isInScheduleMode()) {
            sendSelectedPhotos(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.chatActivity.getCurrentUser()), new -$$Lambda$PhotoPickerActivity$euYzkjcWzyeckwYcPmSeZL7u1Vc(this));
        }
    }

    public /* synthetic */ boolean lambda$createView$6$PhotoPickerActivity(View view) {
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
                this.sendPopupLayout.setDispatchKeyEventListener(new -$$Lambda$PhotoPickerActivity$QVfQkI0yndHryS9tj9438DeCLASSNAMEw(this));
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
                        this.itemCells[i].setOnClickListener(new -$$Lambda$PhotoPickerActivity$YC9YO3UsIp3QAOVyfh5QyaXPk9M(this, i));
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

    public /* synthetic */ void lambda$null$4$PhotoPickerActivity(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$null$5$PhotoPickerActivity(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.chatActivity.getCurrentUser()), new -$$Lambda$PhotoPickerActivity$euYzkjcWzyeckwYcPmSeZL7u1Vc(this));
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
        } else if (i == NotificationCenter.recentImagesDidLoad && this.selectedAlbum == null && this.type == ((Integer) objArr[0]).intValue()) {
            this.recentImages = (ArrayList) objArr[1];
            this.loadingRecent = false;
            updateSearchInterface();
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

    private void processSearch(EditText editText) {
        if (editText.getText().toString().length() != 0) {
            this.searchResult.clear();
            this.searchResultKeys.clear();
            this.imageSearchEndReached = true;
            String str = "";
            searchImages(this.type == 1, editText.getText().toString(), str, true);
            this.lastSearchString = editText.getText().toString();
            if (this.lastSearchString.length() == 0) {
                this.lastSearchString = null;
                this.emptyView.setText(str);
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
                    Integer num = (Integer) photoAttachPhotoCell.getTag();
                    AlbumEntry albumEntry = this.selectedAlbum;
                    int i2 = -1;
                    if (albumEntry != null) {
                        PhotoEntry photoEntry = (PhotoEntry) albumEntry.photos.get(num.intValue());
                        if (this.allowIndices) {
                            i2 = this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        }
                        photoAttachPhotoCell.setNum(i2);
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
                        photoAttachPhotoCell.setNum(i2);
                    }
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
                if (albumEntry == null) {
                    ArrayList arrayList;
                    if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                        arrayList = this.recentImages;
                    } else {
                        arrayList = this.searchResult;
                    }
                    if (intValue < 0) {
                        continue;
                    } else if (intValue >= arrayList.size()) {
                        continue;
                    }
                } else if (intValue < 0) {
                    continue;
                } else if (intValue >= albumEntry.photos.size()) {
                    continue;
                }
                if (intValue == i) {
                    return photoAttachPhotoCell;
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
        if ((this.searching && this.searchResult.isEmpty()) || (this.loadingRecent && this.lastSearchString == null)) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_resolveUsername, new -$$Lambda$PhotoPickerActivity$R0VtlmkIi0TBXQhMe1bcxbWDr_0(this, z));
        }
    }

    public /* synthetic */ void lambda$searchBotUser$8$PhotoPickerActivity(boolean z, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PhotoPickerActivity$e388R-QKMOsdRaDkvxlI03tKlM4(this, tLObject, z));
        }
    }

    public /* synthetic */ void lambda$null$7$PhotoPickerActivity(TLObject tLObject, boolean z) {
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
            this.imageReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getInlineBotResults, new -$$Lambda$PhotoPickerActivity$SCippbea62G33Up_qVr5M3gXJJE(this, i, z, user));
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.imageReqId, this.classGuid);
            return;
        }
        if (z2) {
            searchBotUser(z);
        }
    }

    public /* synthetic */ void lambda$searchImages$10$PhotoPickerActivity(int i, boolean z, User user, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PhotoPickerActivity$pLdQr9HEp3rU89J2iSxTypjdglw(this, i, tLObject, z, user));
    }

    public /* synthetic */ void lambda$null$9$PhotoPickerActivity(int i, TLObject tLObject, boolean z, User user) {
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
            if ((this.searching && this.searchResult.isEmpty()) || (this.loadingRecent && this.lastSearchString == null)) {
                this.emptyView.showProgress();
            } else {
                this.emptyView.showTextView();
            }
        }
    }

    public void setDelegate(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
        this.delegate = photoPickerActivityDelegate;
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
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground");
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
