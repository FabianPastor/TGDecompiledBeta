package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionValues;
import android.util.Property;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputDocument;
import org.telegram.tgnet.TLRPC$TL_inputPhoto;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument;
import org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC$TL_stickers_checkShortName;
import org.telegram.tgnet.TLRPC$TL_stickers_suggestShortName;
import org.telegram.tgnet.TLRPC$TL_stickers_suggestedShortName;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;

public class StickersAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public GridAdapter adapter;
    private List<ThemeDescription> animatingDescriptions;
    private String buttonTextColorKey;
    private int checkReqId;
    private Runnable checkRunnable;
    /* access modifiers changed from: private */
    public boolean clearsInputField;
    private StickersAlertCustomButtonDelegate customButtonDelegate;
    /* access modifiers changed from: private */
    public StickersAlertDelegate delegate;
    /* access modifiers changed from: private */
    public TextView descriptionTextView;
    /* access modifiers changed from: private */
    public FrameLayout emptyView;
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    private String importingSoftware;
    /* access modifiers changed from: private */
    public ArrayList<Parcelable> importingStickers;
    /* access modifiers changed from: private */
    public ArrayList<SendMessagesHelper.ImportingSticker> importingStickersPaths;
    private TLRPC$InputStickerSet inputStickerSet;
    private StickersAlertInstallDelegate installDelegate;
    /* access modifiers changed from: private */
    public int itemHeight;
    /* access modifiers changed from: private */
    public int itemSize;
    private String lastCheckName;
    private boolean lastNameAvailable;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    private Runnable onDismissListener;
    private ActionBarMenuItem optionsButton;
    private Activity parentActivity;
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public FrameLayout pickerBottomFrameLayout;
    private TextView pickerBottomLayout;
    private PremiumButtonView premiumButtonView;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate previewDelegate;
    private TextView previewSendButton;
    private View previewSendButtonShadow;
    public boolean probablyEmojis;
    private int reqId;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private TLRPC$Document selectedSticker;
    private SendMessagesHelper.ImportingSticker selectedStickerPath;
    private String setTitle;
    /* access modifiers changed from: private */
    public View[] shadow;
    /* access modifiers changed from: private */
    public AnimatorSet[] shadowAnimation;
    /* access modifiers changed from: private */
    public boolean showEmoji;
    private boolean showTooltipWhenToggle;
    private TextView stickerEmojiTextView;
    /* access modifiers changed from: private */
    public BackupImageView stickerImageView;
    /* access modifiers changed from: private */
    public FrameLayout stickerPreviewLayout;
    /* access modifiers changed from: private */
    public TLRPC$TL_messages_stickerSet stickerSet;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$StickerSetCovered> stickerSetCovereds;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    private HashMap<String, SendMessagesHelper.ImportingSticker> uploadImportStickers;
    private Pattern urlPattern;

    public interface StickersAlertCustomButtonDelegate {
        String getCustomButtonColorKey();

        String getCustomButtonRippleColorKey();

        String getCustomButtonText();

        String getCustomButtonTextColorKey();

        boolean onCustomButtonPressed();
    }

    public interface StickersAlertDelegate {
        boolean canSchedule();

        boolean isInScheduleMode();

        void onStickerSelected(TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, boolean z2, int i);
    }

    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$init$9(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showNameEnterAlert$22(DialogInterface dialogInterface, int i) {
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public StickersAlert(Context context, Object obj, TLObject tLObject, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.showTooltipWhenToggle = true;
        this.previewDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
            public /* synthetic */ String getQuery(boolean z) {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z);
            }

            public /* synthetic */ void gifAddedOrDeleted() {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
            }

            public /* synthetic */ boolean needMenu() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
            }

            public boolean needOpen() {
                return false;
            }

            public void openSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z) {
            }

            public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
            }

            public boolean can() {
                return StickersAlert.this.stickerSet == null || StickersAlert.this.stickerSet.set == null || !StickersAlert.this.stickerSet.set.emojis;
            }

            public void sendSticker(TLRPC$Document tLRPC$Document, String str, Object obj, boolean z, int i) {
                if (StickersAlert.this.delegate != null) {
                    StickersAlert.this.delegate.onStickerSelected(tLRPC$Document, str, obj, (MessageObject.SendAnimationData) null, StickersAlert.this.clearsInputField, z, i);
                    StickersAlert.this.dismiss();
                }
            }

            public boolean canSchedule() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
            }

            public boolean isInScheduleMode() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
            }

            public boolean needRemove() {
                return StickersAlert.this.importingStickers != null;
            }

            public void remove(SendMessagesHelper.ImportingSticker importingSticker) {
                StickersAlert.this.removeSticker(importingSticker);
            }

            public boolean needSend() {
                return StickersAlert.this.delegate != null;
            }

            public long getDialogId() {
                if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                    return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
                }
                return 0;
            }
        };
        fixNavigationBar();
        this.resourcesProvider = resourcesProvider;
        this.parentActivity = (Activity) context;
        TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers = new TLRPC$TL_messages_getAttachedStickers();
        if (tLObject instanceof TLRPC$Photo) {
            TLRPC$Photo tLRPC$Photo = (TLRPC$Photo) tLObject;
            TLRPC$TL_inputStickeredMediaPhoto tLRPC$TL_inputStickeredMediaPhoto = new TLRPC$TL_inputStickeredMediaPhoto();
            TLRPC$TL_inputPhoto tLRPC$TL_inputPhoto = new TLRPC$TL_inputPhoto();
            tLRPC$TL_inputStickeredMediaPhoto.id = tLRPC$TL_inputPhoto;
            tLRPC$TL_inputPhoto.id = tLRPC$Photo.id;
            tLRPC$TL_inputPhoto.access_hash = tLRPC$Photo.access_hash;
            byte[] bArr = tLRPC$Photo.file_reference;
            tLRPC$TL_inputPhoto.file_reference = bArr;
            if (bArr == null) {
                tLRPC$TL_inputPhoto.file_reference = new byte[0];
            }
            tLRPC$TL_messages_getAttachedStickers.media = tLRPC$TL_inputStickeredMediaPhoto;
        } else if (tLObject instanceof TLRPC$Document) {
            TLRPC$Document tLRPC$Document = (TLRPC$Document) tLObject;
            TLRPC$TL_inputStickeredMediaDocument tLRPC$TL_inputStickeredMediaDocument = new TLRPC$TL_inputStickeredMediaDocument();
            TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
            tLRPC$TL_inputStickeredMediaDocument.id = tLRPC$TL_inputDocument;
            tLRPC$TL_inputDocument.id = tLRPC$Document.id;
            tLRPC$TL_inputDocument.access_hash = tLRPC$Document.access_hash;
            byte[] bArr2 = tLRPC$Document.file_reference;
            tLRPC$TL_inputDocument.file_reference = bArr2;
            if (bArr2 == null) {
                tLRPC$TL_inputDocument.file_reference = new byte[0];
            }
            tLRPC$TL_messages_getAttachedStickers.media = tLRPC$TL_inputStickeredMediaDocument;
        }
        this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getAttachedStickers, new StickersAlert$$ExternalSyntheticLambda29(this, obj, tLRPC$TL_messages_getAttachedStickers, new StickersAlert$$ExternalSyntheticLambda32(this, tLRPC$TL_messages_getAttachedStickers)));
        init(context);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda26(this, tLRPC$TL_error, tLObject, tLRPC$TL_messages_getAttachedStickers));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers) {
        this.reqId = 0;
        if (tLRPC$TL_error == null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            if (tLRPC$Vector.objects.isEmpty()) {
                dismiss();
            } else if (tLRPC$Vector.objects.size() == 1) {
                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                this.inputStickerSet = tLRPC$TL_inputStickerSetID;
                TLRPC$StickerSet tLRPC$StickerSet = ((TLRPC$StickerSetCovered) tLRPC$Vector.objects.get(0)).set;
                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                loadStickerSet();
            } else {
                this.stickerSetCovereds = new ArrayList<>();
                for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                    this.stickerSetCovereds.add((TLRPC$StickerSetCovered) tLRPC$Vector.objects.get(i));
                }
                this.gridView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                this.titleTextView.setVisibility(8);
                this.shadow[0].setVisibility(8);
                this.adapter.notifyDataSetChanged();
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this.parentFragment, tLRPC$TL_messages_getAttachedStickers, new Object[0]);
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(Object obj, TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers, RequestDelegate requestDelegate, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null || !FileRefController.isFileRefError(tLRPC$TL_error.text) || obj == null) {
            requestDelegate.run(tLObject, tLRPC$TL_error);
            return;
        }
        FileRefController.getInstance(this.currentAccount).requestReference(obj, tLRPC$TL_messages_getAttachedStickers, requestDelegate);
    }

    public StickersAlert(Context context, String str, ArrayList<Parcelable> arrayList, ArrayList<String> arrayList2, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.showTooltipWhenToggle = true;
        this.previewDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
            public /* synthetic */ String getQuery(boolean z) {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z);
            }

            public /* synthetic */ void gifAddedOrDeleted() {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
            }

            public /* synthetic */ boolean needMenu() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
            }

            public boolean needOpen() {
                return false;
            }

            public void openSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z) {
            }

            public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
            }

            public boolean can() {
                return StickersAlert.this.stickerSet == null || StickersAlert.this.stickerSet.set == null || !StickersAlert.this.stickerSet.set.emojis;
            }

            public void sendSticker(TLRPC$Document tLRPC$Document, String str, Object obj, boolean z, int i) {
                if (StickersAlert.this.delegate != null) {
                    StickersAlert.this.delegate.onStickerSelected(tLRPC$Document, str, obj, (MessageObject.SendAnimationData) null, StickersAlert.this.clearsInputField, z, i);
                    StickersAlert.this.dismiss();
                }
            }

            public boolean canSchedule() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
            }

            public boolean isInScheduleMode() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
            }

            public boolean needRemove() {
                return StickersAlert.this.importingStickers != null;
            }

            public void remove(SendMessagesHelper.ImportingSticker importingSticker) {
                StickersAlert.this.removeSticker(importingSticker);
            }

            public boolean needSend() {
                return StickersAlert.this.delegate != null;
            }

            public long getDialogId() {
                if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                    return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
                }
                return 0;
            }
        };
        fixNavigationBar();
        this.parentActivity = (Activity) context;
        this.importingStickers = arrayList;
        this.importingSoftware = str;
        Utilities.globalQueue.postRunnable(new StickersAlert$$ExternalSyntheticLambda22(this, arrayList, arrayList2));
        init(context);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(ArrayList arrayList, ArrayList arrayList2) {
        Uri uri;
        String stickerExt;
        int i;
        ArrayList arrayList3 = new ArrayList();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int size = arrayList.size();
        Boolean bool = null;
        for (int i2 = 0; i2 < size; i2++) {
            Object obj = arrayList.get(i2);
            if ((obj instanceof Uri) && (stickerExt = MediaController.getStickerExt(uri)) != null) {
                boolean equals = "tgs".equals(stickerExt);
                if (bool == null) {
                    bool = Boolean.valueOf(equals);
                } else if (bool.booleanValue() != equals) {
                    continue;
                }
                if (!isDismissed()) {
                    SendMessagesHelper.ImportingSticker importingSticker = new SendMessagesHelper.ImportingSticker();
                    importingSticker.animated = equals;
                    String copyFileToCache = MediaController.copyFileToCache((uri = (Uri) obj), stickerExt, (long) ((equals ? 64 : 512) * 1024));
                    importingSticker.path = copyFileToCache;
                    if (copyFileToCache != null) {
                        if (!equals) {
                            BitmapFactory.decodeFile(copyFileToCache, options);
                            int i3 = options.outWidth;
                            if ((i3 == 512 && (i = options.outHeight) > 0 && i <= 512) || (options.outHeight == 512 && i3 > 0 && i3 <= 512)) {
                                importingSticker.mimeType = "image/" + stickerExt;
                                importingSticker.validated = true;
                            }
                        } else {
                            importingSticker.mimeType = "application/x-tgsticker";
                        }
                        if (arrayList2 == null || arrayList2.size() != size || !(arrayList2.get(i2) instanceof String)) {
                            importingSticker.emoji = "#️⃣";
                        } else {
                            importingSticker.emoji = (String) arrayList2.get(i2);
                        }
                        arrayList3.add(importingSticker);
                        if (arrayList3.size() >= 200) {
                            break;
                        }
                    } else {
                        continue;
                    }
                } else {
                    return;
                }
            }
        }
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda21(this, arrayList3, bool));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(ArrayList arrayList, Boolean bool) {
        this.importingStickersPaths = arrayList;
        if (arrayList.isEmpty()) {
            dismiss();
            return;
        }
        this.adapter.notifyDataSetChanged();
        if (bool.booleanValue()) {
            this.uploadImportStickers = new HashMap<>();
            int size = this.importingStickersPaths.size();
            for (int i = 0; i < size; i++) {
                SendMessagesHelper.ImportingSticker importingSticker = this.importingStickersPaths.get(i);
                this.uploadImportStickers.put(importingSticker.path, importingSticker);
                FileLoader.getInstance(this.currentAccount).uploadFile(importingSticker.path, false, true, 67108864);
            }
        }
        updateFields();
    }

    public StickersAlert(Context context, BaseFragment baseFragment, TLRPC$InputStickerSet tLRPC$InputStickerSet, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, StickersAlertDelegate stickersAlertDelegate) {
        this(context, baseFragment, tLRPC$InputStickerSet, tLRPC$TL_messages_stickerSet, stickersAlertDelegate, (Theme.ResourcesProvider) null);
    }

    public StickersAlert(Context context, BaseFragment baseFragment, TLRPC$InputStickerSet tLRPC$InputStickerSet, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, StickersAlertDelegate stickersAlertDelegate, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.showTooltipWhenToggle = true;
        this.previewDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
            public /* synthetic */ String getQuery(boolean z) {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z);
            }

            public /* synthetic */ void gifAddedOrDeleted() {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
            }

            public /* synthetic */ boolean needMenu() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
            }

            public boolean needOpen() {
                return false;
            }

            public void openSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z) {
            }

            public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
            }

            public boolean can() {
                return StickersAlert.this.stickerSet == null || StickersAlert.this.stickerSet.set == null || !StickersAlert.this.stickerSet.set.emojis;
            }

            public void sendSticker(TLRPC$Document tLRPC$Document, String str, Object obj, boolean z, int i) {
                if (StickersAlert.this.delegate != null) {
                    StickersAlert.this.delegate.onStickerSelected(tLRPC$Document, str, obj, (MessageObject.SendAnimationData) null, StickersAlert.this.clearsInputField, z, i);
                    StickersAlert.this.dismiss();
                }
            }

            public boolean canSchedule() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
            }

            public boolean isInScheduleMode() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
            }

            public boolean needRemove() {
                return StickersAlert.this.importingStickers != null;
            }

            public void remove(SendMessagesHelper.ImportingSticker importingSticker) {
                StickersAlert.this.removeSticker(importingSticker);
            }

            public boolean needSend() {
                return StickersAlert.this.delegate != null;
            }

            public long getDialogId() {
                if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                    return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
                }
                return 0;
            }
        };
        fixNavigationBar();
        this.delegate = stickersAlertDelegate;
        this.inputStickerSet = tLRPC$InputStickerSet;
        this.stickerSet = tLRPC$TL_messages_stickerSet;
        this.parentFragment = baseFragment;
        loadStickerSet();
        init(context);
    }

    public void setClearsInputField(boolean z) {
        this.clearsInputField = z;
    }

    private void loadStickerSet() {
        String str;
        if (this.inputStickerSet != null) {
            MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
            if (this.stickerSet == null && (str = this.inputStickerSet.short_name) != null) {
                this.stickerSet = instance.getStickerSetByName(str);
            }
            if (this.stickerSet == null) {
                this.stickerSet = instance.getStickerSetById(this.inputStickerSet.id);
            }
            if (this.stickerSet == null) {
                TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
                tLRPC$TL_messages_getStickerSet.stickerset = this.inputStickerSet;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getStickerSet, new StickersAlert$$ExternalSyntheticLambda31(this, instance));
            } else {
                if (this.adapter != null) {
                    updateSendButton();
                    updateFields();
                    this.adapter.notifyDataSetChanged();
                }
                updateDescription();
                instance.preloadStickerSetThumb(this.stickerSet);
                checkPremiumStickers();
            }
        }
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSet;
        if (tLRPC$TL_messages_stickerSet != null) {
            this.showEmoji = !tLRPC$TL_messages_stickerSet.set.masks;
        }
        checkPremiumStickers();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickerSet$6(MediaDataController mediaDataController, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda25(this, tLRPC$TL_error, tLObject, mediaDataController));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickerSet$5(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, MediaDataController mediaDataController) {
        this.reqId = 0;
        if (tLRPC$TL_error == null) {
            if (Build.VERSION.SDK_INT >= 19) {
                AnonymousClass2 r3 = new Transition() {
                    public void captureStartValues(TransitionValues transitionValues) {
                        transitionValues.values.put("start", Boolean.TRUE);
                        transitionValues.values.put("offset", Integer.valueOf(StickersAlert.this.containerView.getTop() + StickersAlert.this.scrollOffsetY));
                    }

                    public void captureEndValues(TransitionValues transitionValues) {
                        transitionValues.values.put("start", Boolean.FALSE);
                        transitionValues.values.put("offset", Integer.valueOf(StickersAlert.this.containerView.getTop() + StickersAlert.this.scrollOffsetY));
                    }

                    public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
                        int access$600 = StickersAlert.this.scrollOffsetY;
                        int intValue = ((Integer) transitionValues.values.get("offset")).intValue() - ((Integer) transitionValues2.values.get("offset")).intValue();
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        ofFloat.setDuration(250);
                        ofFloat.addUpdateListener(new StickersAlert$2$$ExternalSyntheticLambda0(this, intValue, access$600));
                        return ofFloat;
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$createAnimator$0(int i, int i2, ValueAnimator valueAnimator) {
                        float animatedFraction = valueAnimator.getAnimatedFraction();
                        StickersAlert.this.gridView.setAlpha(animatedFraction);
                        StickersAlert.this.titleTextView.setAlpha(animatedFraction);
                        if (i != 0) {
                            int i3 = (int) (((float) i) * (1.0f - animatedFraction));
                            StickersAlert.this.setScrollOffsetY(i2 + i3);
                            StickersAlert.this.gridView.setTranslationY((float) i3);
                        }
                    }
                };
                r3.addTarget(this.containerView);
                TransitionManager.beginDelayedTransition(this.container, r3);
            }
            this.optionsButton.setVisibility(0);
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            this.stickerSet = tLRPC$TL_messages_stickerSet;
            this.showEmoji = !tLRPC$TL_messages_stickerSet.set.masks;
            checkPremiumStickers();
            mediaDataController.preloadStickerSetThumb(this.stickerSet);
            updateSendButton();
            updateFields();
            updateDescription();
            this.adapter.notifyDataSetChanged();
            return;
        }
        dismiss();
        BulletinFactory.of(this.parentFragment).createErrorBulletin(LocaleController.getString("AddStickersNotFound", R.string.AddStickersNotFound)).show();
    }

    private void checkPremiumStickers() {
        if (this.stickerSet != null) {
            TLRPC$TL_messages_stickerSet filterPremiumStickers = MessagesController.getInstance(this.currentAccount).filterPremiumStickers(this.stickerSet);
            this.stickerSet = filterPremiumStickers;
            if (filterPremiumStickers == null) {
                dismiss();
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r1 = r0.set;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isEmoji() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r2.stickerSet
            if (r0 == 0) goto L_0x000c
            org.telegram.tgnet.TLRPC$StickerSet r1 = r0.set
            if (r1 == 0) goto L_0x000c
            boolean r1 = r1.emojis
            if (r1 != 0) goto L_0x0012
        L_0x000c:
            if (r0 != 0) goto L_0x0014
            boolean r0 = r2.probablyEmojis
            if (r0 == 0) goto L_0x0014
        L_0x0012:
            r0 = 1
            goto L_0x0015
        L_0x0014:
            r0 = 0
        L_0x0015:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.isEmoji():boolean");
    }

    private void init(Context context) {
        Context context2 = context;
        AnonymousClass3 r1 = new FrameLayout(context2) {
            private boolean fullHeight;
            private int lastNotifyWidth;
            private RectF rect = new RectF();
            private Boolean statusBarOpen;

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || StickersAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) StickersAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                StickersAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !StickersAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x0197  */
            /* JADX WARNING: Removed duplicated region for block: B:34:0x01ae  */
            /* JADX WARNING: Removed duplicated region for block: B:37:0x01de  */
            /* JADX WARNING: Removed duplicated region for block: B:38:0x01e0  */
            /* JADX WARNING: Removed duplicated region for block: B:43:0x01ee  */
            /* JADX WARNING: Removed duplicated region for block: B:46:0x01fc  */
            /* JADX WARNING: Removed duplicated region for block: B:49:0x0216  */
            /* JADX WARNING: Removed duplicated region for block: B:52:0x0227  */
            /* JADX WARNING: Removed duplicated region for block: B:54:0x0254  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onMeasure(int r12, int r13) {
                /*
                    r11 = this;
                    int r13 = android.view.View.MeasureSpec.getSize(r13)
                    int r0 = android.os.Build.VERSION.SDK_INT
                    r1 = 1
                    r2 = 0
                    r3 = 21
                    if (r0 < r3) goto L_0x0027
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    boolean unused = r0.ignoreLayout = r1
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r0 = r0.backgroundPaddingLeft
                    int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    int r4 = r4.backgroundPaddingLeft
                    r11.setPadding(r0, r3, r4, r2)
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    boolean unused = r0.ignoreLayout = r2
                L_0x0027:
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    boolean r0 = r0.isEmoji()
                    r3 = 1114636288(0x42700000, float:60.0)
                    r4 = 1108344832(0x42100000, float:36.0)
                    if (r0 == 0) goto L_0x0083
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.ui.Components.RecyclerListView r0 = r0.gridView
                    int r0 = r0.getMeasuredWidth()
                    if (r0 != 0) goto L_0x0043
                    android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r0 = r0.x
                L_0x0043:
                    org.telegram.ui.Components.StickersAlert r5 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.ui.Components.StickersAlert$GridAdapter r5 = r5.adapter
                    boolean r6 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r6 == 0) goto L_0x0052
                    r6 = 1114636288(0x42700000, float:60.0)
                    goto L_0x0054
                L_0x0052:
                    r6 = 1110704128(0x42340000, float:45.0)
                L_0x0054:
                    int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                    int r0 = r0 / r6
                    int r0 = java.lang.Math.max(r1, r0)
                    int unused = r5.stickersPerRow = r0
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r5 = android.view.View.MeasureSpec.getSize(r12)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    int r5 = r5 - r4
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.ui.Components.StickersAlert$GridAdapter r4 = r4.adapter
                    int r4 = r4.stickersPerRow
                    int r5 = r5 / r4
                    int unused = r0.itemSize = r5
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r4 = r0.itemSize
                    int unused = r0.itemHeight = r4
                    goto L_0x00b1
                L_0x0083:
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.ui.Components.StickersAlert$GridAdapter r0 = r0.adapter
                    r5 = 5
                    int unused = r0.stickersPerRow = r5
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r5 = android.view.View.MeasureSpec.getSize(r12)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    int r5 = r5 - r4
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.ui.Components.StickersAlert$GridAdapter r4 = r4.adapter
                    int r4 = r4.stickersPerRow
                    int r5 = r5 / r4
                    int unused = r0.itemSize = r5
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    r4 = 1118044160(0x42a40000, float:82.0)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    int unused = r0.itemHeight = r4
                L_0x00b1:
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.ui.Components.StickersAlert$GridAdapter r0 = r0.adapter
                    int r0 = r0.stickersPerRow
                    float r0 = (float) r0
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.ui.Components.RecyclerListView r4 = r4.gridView
                    android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
                    android.view.ViewGroup$MarginLayoutParams r4 = (android.view.ViewGroup.MarginLayoutParams) r4
                    org.telegram.ui.Components.StickersAlert r5 = org.telegram.ui.Components.StickersAlert.this
                    java.util.ArrayList r5 = r5.importingStickers
                    r6 = 3
                    r7 = 1111490560(0x42400000, float:48.0)
                    r8 = 1090519040(0x41000000, float:8.0)
                    if (r5 == 0) goto L_0x0107
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r4 = r4.bottomMargin
                    int r3 = r3 + r4
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    java.util.ArrayList r4 = r4.importingStickers
                    int r4 = r4.size()
                    float r4 = (float) r4
                    float r4 = r4 / r0
                    double r4 = (double) r4
                    double r4 = java.lang.Math.ceil(r4)
                    int r0 = (int) r4
                    int r0 = java.lang.Math.max(r6, r0)
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    int r4 = r4.itemHeight
                    int r0 = r0 * r4
                    int r3 = r3 + r0
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r0 = r0.backgroundPaddingTop
                    int r3 = r3 + r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                L_0x0104:
                    int r3 = r3 + r0
                    goto L_0x018f
                L_0x0107:
                    org.telegram.ui.Components.StickersAlert r5 = org.telegram.ui.Components.StickersAlert.this
                    java.util.ArrayList r5 = r5.stickerSetCovereds
                    if (r5 == 0) goto L_0x0149
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r4 = r4.bottomMargin
                    int r0 = r0 + r4
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    java.util.ArrayList r4 = r4.stickerSetCovereds
                    int r4 = r4.size()
                    int r3 = r3 * r4
                    int r0 = r0 + r3
                    org.telegram.ui.Components.StickersAlert r3 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.ui.Components.StickersAlert$GridAdapter r3 = r3.adapter
                    int r3 = r3.stickersRowCount
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    int r4 = r4.itemHeight
                    int r3 = r3 * r4
                    int r0 = r0 + r3
                    org.telegram.ui.Components.StickersAlert r3 = org.telegram.ui.Components.StickersAlert.this
                    int r3 = r3.backgroundPaddingTop
                    int r0 = r0 + r3
                    r3 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    int r3 = r3 + r0
                    goto L_0x018f
                L_0x0149:
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r4 = r4.bottomMargin
                    int r3 = r3 + r4
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    boolean r4 = r4.isEmoji()
                    if (r4 == 0) goto L_0x0159
                    r6 = 2
                L_0x0159:
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = r4.stickerSet
                    if (r4 == 0) goto L_0x0176
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = r4.stickerSet
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r4.documents
                    int r4 = r4.size()
                    float r4 = (float) r4
                    float r4 = r4 / r0
                    double r4 = (double) r4
                    double r4 = java.lang.Math.ceil(r4)
                    int r0 = (int) r4
                    goto L_0x0177
                L_0x0176:
                    r0 = 0
                L_0x0177:
                    int r0 = java.lang.Math.max(r6, r0)
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    int r4 = r4.itemHeight
                    int r0 = r0 * r4
                    int r3 = r3 + r0
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r0 = r0.backgroundPaddingTop
                    int r3 = r3 + r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    goto L_0x0104
                L_0x018f:
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    boolean r0 = r0.isEmoji()
                    if (r0 == 0) goto L_0x01a6
                    float r0 = (float) r3
                    org.telegram.ui.Components.StickersAlert r3 = org.telegram.ui.Components.StickersAlert.this
                    int r3 = r3.itemHeight
                    float r3 = (float) r3
                    r4 = 1041865114(0x3e19999a, float:0.15)
                    float r3 = r3 * r4
                    float r0 = r0 + r3
                    int r3 = (int) r0
                L_0x01a6:
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    android.widget.TextView r0 = r0.descriptionTextView
                    if (r0 == 0) goto L_0x01ca
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    android.widget.TextView r0 = r0.descriptionTextView
                    r4 = 9999(0x270f, float:1.4012E-41)
                    r5 = -2147483648(0xfffffffvar_, float:-0.0)
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5)
                    r0.measure(r12, r4)
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    android.widget.TextView r0 = r0.descriptionTextView
                    int r0 = r0.getMeasuredHeight()
                    int r3 = r3 + r0
                L_0x01ca:
                    double r4 = (double) r3
                    float r0 = (float) r13
                    r6 = 1084227584(0x40a00000, float:5.0)
                    float r0 = r0 / r6
                    double r6 = (double) r0
                    r9 = 4614388178203810202(0x400999999999999a, double:3.2)
                    java.lang.Double.isNaN(r6)
                    double r6 = r6 * r9
                    int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                    if (r9 >= 0) goto L_0x01e0
                    r0 = 0
                    goto L_0x01e5
                L_0x01e0:
                    r4 = 1073741824(0x40000000, float:2.0)
                    float r0 = r0 * r4
                    int r0 = (int) r0
                L_0x01e5:
                    if (r0 == 0) goto L_0x01ec
                    if (r3 >= r13) goto L_0x01ec
                    int r4 = r13 - r3
                    int r0 = r0 - r4
                L_0x01ec:
                    if (r0 != 0) goto L_0x01f4
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r0 = r0.backgroundPaddingTop
                L_0x01f4:
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    android.widget.TextView r4 = r4.descriptionTextView
                    if (r4 == 0) goto L_0x020e
                    r4 = 1107296256(0x42000000, float:32.0)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    org.telegram.ui.Components.StickersAlert r5 = org.telegram.ui.Components.StickersAlert.this
                    android.widget.TextView r5 = r5.descriptionTextView
                    int r5 = r5.getMeasuredHeight()
                    int r4 = r4 + r5
                    int r0 = r0 + r4
                L_0x020e:
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    java.util.ArrayList r4 = r4.stickerSetCovereds
                    if (r4 == 0) goto L_0x021b
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r0 = r0 + r4
                L_0x021b:
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.ui.Components.RecyclerListView r4 = r4.gridView
                    int r4 = r4.getPaddingTop()
                    if (r4 == r0) goto L_0x0251
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    boolean unused = r4.ignoreLayout = r1
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    org.telegram.ui.Components.RecyclerListView r4 = r4.gridView
                    r5 = 1092616192(0x41200000, float:10.0)
                    int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
                    int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    r4.setPadding(r6, r0, r5, r7)
                    org.telegram.ui.Components.StickersAlert r4 = org.telegram.ui.Components.StickersAlert.this
                    android.widget.FrameLayout r4 = r4.emptyView
                    r4.setPadding(r2, r0, r2, r2)
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    boolean unused = r0.ignoreLayout = r2
                L_0x0251:
                    if (r3 < r13) goto L_0x0254
                    goto L_0x0255
                L_0x0254:
                    r1 = 0
                L_0x0255:
                    r11.fullHeight = r1
                    int r13 = java.lang.Math.min(r3, r13)
                    r0 = 1073741824(0x40000000, float:2.0)
                    int r13 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r0)
                    super.onMeasure(r12, r13)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.AnonymousClass3.onMeasure(int, int):void");
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5 = i3 - i;
                if (this.lastNotifyWidth != i5) {
                    this.lastNotifyWidth = i5;
                    if (!(StickersAlert.this.adapter == null || StickersAlert.this.stickerSetCovereds == null)) {
                        StickersAlert.this.adapter.notifyDataSetChanged();
                    }
                }
                super.onLayout(z, i, i2, i3, i4);
                StickersAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            private void updateLightStatusBar(boolean z) {
                Boolean bool = this.statusBarOpen;
                if (bool == null || bool.booleanValue() != z) {
                    boolean z2 = true;
                    boolean z3 = AndroidUtilities.computePerceivedBrightness(StickersAlert.this.getThemedColor("dialogBackground")) > 0.721f;
                    if (AndroidUtilities.computePerceivedBrightness(Theme.blendOver(StickersAlert.this.getThemedColor("actionBarDefault"), NUM)) <= 0.721f) {
                        z2 = false;
                    }
                    if (!z) {
                        z3 = z2;
                    }
                    AndroidUtilities.setLightStatusBar(StickersAlert.this.getWindow(), z3);
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:15:0x00b1  */
            /* JADX WARNING: Removed duplicated region for block: B:18:0x016d  */
            /* JADX WARNING: Removed duplicated region for block: B:21:0x0173  */
            /* JADX WARNING: Removed duplicated region for block: B:23:? A[RETURN, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onDraw(android.graphics.Canvas r14) {
                /*
                    r13 = this;
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r0 = r0.scrollOffsetY
                    org.telegram.ui.Components.StickersAlert r1 = org.telegram.ui.Components.StickersAlert.this
                    int r1 = r1.backgroundPaddingTop
                    int r0 = r0 - r1
                    r1 = 1086324736(0x40CLASSNAME, float:6.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    int r0 = r0 + r1
                    org.telegram.ui.Components.StickersAlert r1 = org.telegram.ui.Components.StickersAlert.this
                    int r1 = r1.scrollOffsetY
                    org.telegram.ui.Components.StickersAlert r2 = org.telegram.ui.Components.StickersAlert.this
                    int r2 = r2.backgroundPaddingTop
                    int r1 = r1 - r2
                    r2 = 1095761920(0x41500000, float:13.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    int r1 = r1 - r2
                    int r2 = r13.getMeasuredHeight()
                    r3 = 1097859072(0x41700000, float:15.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    int r2 = r2 + r3
                    org.telegram.ui.Components.StickersAlert r3 = org.telegram.ui.Components.StickersAlert.this
                    int r3 = r3.backgroundPaddingTop
                    int r2 = r2 + r3
                    int r3 = android.os.Build.VERSION.SDK_INT
                    r4 = 0
                    r5 = 1065353216(0x3var_, float:1.0)
                    r6 = 21
                    if (r3 < r6) goto L_0x0092
                    int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r1 = r1 + r3
                    int r0 = r0 + r3
                    int r2 = r2 - r3
                    boolean r3 = r13.fullHeight
                    if (r3 == 0) goto L_0x0092
                    org.telegram.ui.Components.StickersAlert r3 = org.telegram.ui.Components.StickersAlert.this
                    int r3 = r3.backgroundPaddingTop
                    int r3 = r3 + r1
                    int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r7 = r6 * 2
                    if (r3 >= r7) goto L_0x0077
                    int r3 = r6 * 2
                    int r3 = r3 - r1
                    org.telegram.ui.Components.StickersAlert r7 = org.telegram.ui.Components.StickersAlert.this
                    int r7 = r7.backgroundPaddingTop
                    int r3 = r3 - r7
                    int r3 = java.lang.Math.min(r6, r3)
                    int r1 = r1 - r3
                    int r2 = r2 + r3
                    int r3 = r3 * 2
                    float r3 = (float) r3
                    int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    float r6 = (float) r6
                    float r3 = r3 / r6
                    float r3 = java.lang.Math.min(r5, r3)
                    float r3 = r5 - r3
                    goto L_0x0079
                L_0x0077:
                    r3 = 1065353216(0x3var_, float:1.0)
                L_0x0079:
                    org.telegram.ui.Components.StickersAlert r6 = org.telegram.ui.Components.StickersAlert.this
                    int r6 = r6.backgroundPaddingTop
                    int r6 = r6 + r1
                    int r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    if (r6 >= r7) goto L_0x0094
                    int r6 = r7 - r1
                    org.telegram.ui.Components.StickersAlert r8 = org.telegram.ui.Components.StickersAlert.this
                    int r8 = r8.backgroundPaddingTop
                    int r6 = r6 - r8
                    int r6 = java.lang.Math.min(r7, r6)
                    goto L_0x0095
                L_0x0092:
                    r3 = 1065353216(0x3var_, float:1.0)
                L_0x0094:
                    r6 = 0
                L_0x0095:
                    org.telegram.ui.Components.StickersAlert r7 = org.telegram.ui.Components.StickersAlert.this
                    android.graphics.drawable.Drawable r7 = r7.shadowDrawable
                    int r8 = r13.getMeasuredWidth()
                    r7.setBounds(r4, r1, r8, r2)
                    org.telegram.ui.Components.StickersAlert r2 = org.telegram.ui.Components.StickersAlert.this
                    android.graphics.drawable.Drawable r2 = r2.shadowDrawable
                    r2.draw(r14)
                    java.lang.String r2 = "dialogBackground"
                    int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                    if (r7 == 0) goto L_0x0102
                    android.graphics.Paint r7 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    org.telegram.ui.Components.StickersAlert r8 = org.telegram.ui.Components.StickersAlert.this
                    int r8 = r8.getThemedColor(r2)
                    r7.setColor(r8)
                    android.graphics.RectF r7 = r13.rect
                    org.telegram.ui.Components.StickersAlert r8 = org.telegram.ui.Components.StickersAlert.this
                    int r8 = r8.backgroundPaddingLeft
                    float r8 = (float) r8
                    org.telegram.ui.Components.StickersAlert r9 = org.telegram.ui.Components.StickersAlert.this
                    int r9 = r9.backgroundPaddingTop
                    int r9 = r9 + r1
                    float r9 = (float) r9
                    int r10 = r13.getMeasuredWidth()
                    org.telegram.ui.Components.StickersAlert r11 = org.telegram.ui.Components.StickersAlert.this
                    int r11 = r11.backgroundPaddingLeft
                    int r10 = r10 - r11
                    float r10 = (float) r10
                    org.telegram.ui.Components.StickersAlert r11 = org.telegram.ui.Components.StickersAlert.this
                    int r11 = r11.backgroundPaddingTop
                    int r11 = r11 + r1
                    r1 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    int r11 = r11 + r1
                    float r1 = (float) r11
                    r7.set(r8, r9, r10, r1)
                    android.graphics.RectF r1 = r13.rect
                    r7 = 1094713344(0x41400000, float:12.0)
                    int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    float r8 = (float) r8
                    float r8 = r8 * r3
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    float r7 = (float) r7
                    float r7 = r7 * r3
                    android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r14.drawRoundRect(r1, r8, r7, r3)
                L_0x0102:
                    int r1 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    r1 = 1108344832(0x42100000, float:36.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    android.graphics.RectF r3 = r13.rect
                    int r7 = r13.getMeasuredWidth()
                    int r7 = r7 - r1
                    int r7 = r7 / 2
                    float r7 = (float) r7
                    float r8 = (float) r0
                    int r9 = r13.getMeasuredWidth()
                    int r9 = r9 + r1
                    int r9 = r9 / 2
                    float r1 = (float) r9
                    r9 = 1082130432(0x40800000, float:4.0)
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r9 = r9 + r0
                    float r9 = (float) r9
                    r3.set(r7, r8, r1, r9)
                    android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    org.telegram.ui.Components.StickersAlert r3 = org.telegram.ui.Components.StickersAlert.this
                    java.lang.String r7 = "key_sheet_scrollUp"
                    int r3 = r3.getThemedColor(r7)
                    r1.setColor(r3)
                    android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r3 = 1132396544(0x437var_, float:255.0)
                    r7 = 0
                    int r8 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r0 = r0 - r8
                    float r0 = (float) r0
                    r8 = 1098907648(0x41800000, float:16.0)
                    int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    float r8 = (float) r8
                    float r0 = r0 / r8
                    float r0 = java.lang.Math.min(r5, r0)
                    float r0 = java.lang.Math.max(r7, r0)
                    float r0 = r0 * r3
                    int r0 = (int) r0
                    r1.setAlpha(r0)
                    android.graphics.RectF r0 = r13.rect
                    r1 = 1073741824(0x40000000, float:2.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r3 = (float) r3
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r1 = (float) r1
                    android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r14.drawRoundRect(r0, r3, r1, r5)
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r0 = r0 / 2
                    if (r6 <= r0) goto L_0x016e
                    r4 = 1
                L_0x016e:
                    r13.updateLightStatusBar(r4)
                    if (r6 <= 0) goto L_0x019e
                    android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    org.telegram.ui.Components.StickersAlert r1 = org.telegram.ui.Components.StickersAlert.this
                    int r1 = r1.getThemedColor(r2)
                    r0.setColor(r1)
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r0 = r0.backgroundPaddingLeft
                    float r8 = (float) r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r0 = r0 - r6
                    float r9 = (float) r0
                    int r0 = r13.getMeasuredWidth()
                    org.telegram.ui.Components.StickersAlert r1 = org.telegram.ui.Components.StickersAlert.this
                    int r1 = r1.backgroundPaddingLeft
                    int r0 = r0 - r1
                    float r10 = (float) r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    float r11 = (float) r0
                    android.graphics.Paint r12 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r7 = r14
                    r7.drawRect(r8, r9, r10, r11, r12)
                L_0x019e:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.AnonymousClass3.onDraw(android.graphics.Canvas):void");
            }
        };
        this.containerView = r1;
        r1.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(48.0f);
        this.shadow[0] = new View(context2);
        this.shadow[0].setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(1);
        this.containerView.addView(this.shadow[0], layoutParams);
        AnonymousClass4 r12 = new RecyclerListView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickersAlert.this.gridView, 0, StickersAlert.this.previewDelegate, this.resourcesProvider);
            }

            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.gridView = r12;
        r12.setTag(14);
        RecyclerListView recyclerListView = this.gridView;
        AnonymousClass5 r2 = new GridLayoutManager(getContext(), 5) {
            /* access modifiers changed from: protected */
            public boolean isLayoutRTL() {
                return StickersAlert.this.stickerSetCovereds != null && LocaleController.isRTL;
            }
        };
        this.layoutManager = r2;
        recyclerListView.setLayoutManager(r2);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                if ((StickersAlert.this.stickerSetCovereds == null || !(StickersAlert.this.adapter.cache.get(i) instanceof Integer)) && i != StickersAlert.this.adapter.totalItems) {
                    return 1;
                }
                return StickersAlert.this.adapter.stickersPerRow;
            }
        });
        RecyclerListView recyclerListView2 = this.gridView;
        GridAdapter gridAdapter = new GridAdapter(context2);
        this.adapter = gridAdapter;
        recyclerListView2.setAdapter(gridAdapter);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new RecyclerView.ItemDecoration(this) {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                rect.left = 0;
                rect.right = 0;
                rect.bottom = 0;
                rect.top = 0;
            }
        });
        this.gridView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.gridView.setClipToPadding(false);
        this.gridView.setEnabled(true);
        this.gridView.setGlowColor(getThemedColor("dialogScrollGlow"));
        this.gridView.setOnTouchListener(new StickersAlert$$ExternalSyntheticLambda14(this));
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                StickersAlert.this.updateLayout();
            }
        });
        StickersAlert$$ExternalSyntheticLambda36 stickersAlert$$ExternalSyntheticLambda36 = new StickersAlert$$ExternalSyntheticLambda36(this);
        this.stickersOnItemClickListener = stickersAlert$$ExternalSyntheticLambda36;
        this.gridView.setOnItemClickListener((RecyclerListView.OnItemClickListener) stickersAlert$$ExternalSyntheticLambda36);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 48.0f));
        AnonymousClass9 r13 = new FrameLayout(context2) {
            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.emptyView = r13;
        this.containerView.addView(r13, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.gridView.setEmptyView(this.emptyView);
        this.emptyView.setOnTouchListener(StickersAlert$$ExternalSyntheticLambda15.INSTANCE);
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(getThemedColor("dialogTextBlack"));
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setLinkTextColor(getThemedColor("dialogTextLink"));
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.containerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenuItem actionBarMenuItem = r1;
        ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, getThemedColor("key_sheet_other"), this.resourcesProvider);
        this.optionsButton = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        this.optionsButton.setSubMenuOpenSide(2);
        this.optionsButton.setIcon(R.drawable.ic_ab_other);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("player_actionBarSelector"), 1));
        this.containerView.addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 5.0f, 5.0f, 0.0f));
        this.optionsButton.addSubItem(1, R.drawable.msg_share, LocaleController.getString("StickersShare", R.string.StickersShare));
        this.optionsButton.addSubItem(2, R.drawable.msg_link, LocaleController.getString("CopyLink", R.string.CopyLink));
        this.optionsButton.setOnClickListener(new StickersAlert$$ExternalSyntheticLambda3(this));
        this.optionsButton.setDelegate(new StickersAlert$$ExternalSyntheticLambda34(this));
        this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.optionsButton.setVisibility(this.inputStickerSet != null ? 0 : 8);
        this.emptyView.addView(new RadialProgressView(context2), LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
        this.shadow[1] = new View(context2);
        this.shadow[1].setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.containerView.addView(this.shadow[1], layoutParams2);
        TextView textView2 = new TextView(context2);
        this.pickerBottomLayout = textView2;
        textView2.setBackground(Theme.createSelectorWithBackgroundDrawable(getThemedColor("dialogBackground"), getThemedColor("listSelectorSDK21")));
        TextView textView3 = this.pickerBottomLayout;
        this.buttonTextColorKey = "dialogTextBlue2";
        textView3.setTextColor(getThemedColor("dialogTextBlue2"));
        this.pickerBottomLayout.setTextSize(1, 14.0f);
        this.pickerBottomLayout.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pickerBottomLayout.setGravity(17);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.pickerBottomFrameLayout = frameLayout;
        frameLayout.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48.0f));
        this.containerView.addView(this.pickerBottomFrameLayout, LayoutHelper.createFrame(-1, -2, 83));
        PremiumButtonView premiumButtonView2 = new PremiumButtonView(context2, false);
        this.premiumButtonView = premiumButtonView2;
        premiumButtonView2.setIcon(R.raw.unlock_icon);
        this.premiumButtonView.setVisibility(4);
        this.containerView.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 87, 8.0f, 0.0f, 8.0f, 8.0f));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.stickerPreviewLayout = frameLayout2;
        frameLayout2.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.stickerPreviewLayout.setOnClickListener(new StickersAlert$$ExternalSyntheticLambda6(this));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.stickerImageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.stickerImageView.setLayerNum(7);
        this.stickerPreviewLayout.addView(this.stickerImageView);
        TextView textView4 = new TextView(context2);
        this.stickerEmojiTextView = textView4;
        textView4.setTextSize(1, 30.0f);
        this.stickerEmojiTextView.setGravity(85);
        this.stickerPreviewLayout.addView(this.stickerEmojiTextView);
        TextView textView5 = new TextView(context2);
        this.previewSendButton = textView5;
        textView5.setTextSize(1, 14.0f);
        this.previewSendButton.setTextColor(getThemedColor("dialogTextBlue2"));
        this.previewSendButton.setBackground(Theme.createSelectorWithBackgroundDrawable(getThemedColor("dialogBackground"), getThemedColor("listSelectorSDK21")));
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.previewSendButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new StickersAlert$$ExternalSyntheticLambda4(this));
        FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams3.bottomMargin = AndroidUtilities.dp(48.0f);
        View view = new View(context2);
        this.previewSendButtonShadow = view;
        view.setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, layoutParams3);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        if (this.importingStickers != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
        }
        updateFields();
        updateSendButton();
        updateDescription();
        updateColors();
        this.adapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$init$7(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gridView, 0, this.stickersOnItemClickListener, this.previewDelegate, this.resourcesProvider);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$8(View view, int i) {
        boolean z;
        TLRPC$StickerSet tLRPC$StickerSet;
        int i2 = i;
        if (this.stickerSetCovereds != null) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) this.adapter.positionsToSets.get(i2);
            if (tLRPC$StickerSetCovered != null) {
                dismiss();
                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$StickerSetCovered.set;
                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet2.access_hash;
                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet2.id;
                new StickersAlert(this.parentActivity, this.parentFragment, tLRPC$TL_inputStickerSetID, (TLRPC$TL_messages_stickerSet) null, (StickersAlertDelegate) null, this.resourcesProvider).show();
                return;
            }
            return;
        }
        ArrayList<SendMessagesHelper.ImportingSticker> arrayList = this.importingStickersPaths;
        if (arrayList == null) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSet;
            if (tLRPC$TL_messages_stickerSet != null && i2 >= 0 && i2 < tLRPC$TL_messages_stickerSet.documents.size()) {
                this.selectedSticker = this.stickerSet.documents.get(i2);
                int i3 = 0;
                while (true) {
                    if (i3 >= this.selectedSticker.attributes.size()) {
                        break;
                    }
                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.selectedSticker.attributes.get(i3);
                    if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                        String str = tLRPC$DocumentAttribute.alt;
                        if (str != null && str.length() > 0) {
                            TextView textView = this.stickerEmojiTextView;
                            textView.setText(Emoji.replaceEmoji(tLRPC$DocumentAttribute.alt, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                            z = true;
                        }
                    } else {
                        i3++;
                    }
                }
                z = false;
                if (!z) {
                    this.stickerEmojiTextView.setText(Emoji.replaceEmoji(MediaDataController.getInstance(this.currentAccount).getEmojiForSticker(this.selectedSticker.id), this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                }
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = this.stickerSet;
                if ((tLRPC$TL_messages_stickerSet2 == null || (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet2.set) == null || !tLRPC$StickerSet.emojis) && !ContentPreviewViewer.getInstance().showMenuFor(view)) {
                    this.stickerImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.selectedSticker), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(this.selectedSticker.thumbs, 90), this.selectedSticker), (String) null, "webp", (Object) this.stickerSet, 1);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.stickerPreviewLayout.getLayoutParams();
                    layoutParams.topMargin = this.scrollOffsetY;
                    this.stickerPreviewLayout.setLayoutParams(layoutParams);
                    this.stickerPreviewLayout.setVisibility(0);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, new float[]{0.0f, 1.0f})});
                    animatorSet.setDuration(200);
                    animatorSet.start();
                }
            }
        } else if (i2 >= 0 && i2 < arrayList.size()) {
            SendMessagesHelper.ImportingSticker importingSticker = this.importingStickersPaths.get(i2);
            this.selectedStickerPath = importingSticker;
            if (importingSticker.validated) {
                TextView textView2 = this.stickerEmojiTextView;
                textView2.setText(Emoji.replaceEmoji(importingSticker.emoji, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                this.stickerImageView.setImage(ImageLocation.getForPath(this.selectedStickerPath.path), (String) null, (ImageLocation) null, (String) null, (Drawable) null, (Bitmap) null, this.selectedStickerPath.animated ? "tgs" : null, 0, (Object) null);
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.stickerPreviewLayout.getLayoutParams();
                layoutParams2.topMargin = this.scrollOffsetY;
                this.stickerPreviewLayout.setLayoutParams(layoutParams2);
                this.stickerPreviewLayout.setVisibility(0);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, new float[]{0.0f, 1.0f})});
                animatorSet2.setDuration(200);
                animatorSet2.start();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$10(View view) {
        this.optionsButton.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$11(View view) {
        hidePreview();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$12(View view) {
        if (this.importingStickersPaths != null) {
            removeSticker(this.selectedStickerPath);
            hidePreview();
            this.selectedStickerPath = null;
            return;
        }
        this.delegate.onStickerSelected(this.selectedSticker, (String) null, this.stickerSet, (MessageObject.SendAnimationData) null, this.clearsInputField, true, 0);
        dismiss();
    }

    private void updateDescription() {
        if (this.containerView != null && !UserConfig.getInstance(this.currentAccount).isPremium()) {
            MessageObject.isPremiumEmojiPack(this.stickerSet);
        }
    }

    private void updateSendButton() {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        Point point = AndroidUtilities.displaySize;
        int min = (int) (((float) (Math.min(point.x, point.y) / 2)) / AndroidUtilities.density);
        if (this.importingStickers != null) {
            this.previewSendButton.setText(LocaleController.getString("ImportStickersRemove", R.string.ImportStickersRemove));
            this.previewSendButton.setTextColor(getThemedColor("dialogTextRed"));
            float f = (float) min;
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.previewSendButton.setVisibility(0);
            this.previewSendButtonShadow.setVisibility(0);
        } else if (this.delegate == null || ((tLRPC$TL_messages_stickerSet = this.stickerSet) != null && tLRPC$TL_messages_stickerSet.set.masks)) {
            this.previewSendButton.setText(LocaleController.getString("Close", R.string.Close));
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
            this.previewSendButton.setVisibility(8);
            this.previewSendButtonShadow.setVisibility(8);
        } else {
            this.previewSendButton.setText(LocaleController.getString("SendSticker", R.string.SendSticker));
            float f2 = (float) min;
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, f2, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, f2, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.previewSendButton.setVisibility(0);
            this.previewSendButtonShadow.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    public void removeSticker(SendMessagesHelper.ImportingSticker importingSticker) {
        int indexOf = this.importingStickersPaths.indexOf(importingSticker);
        if (indexOf >= 0) {
            this.importingStickersPaths.remove(indexOf);
            this.adapter.notifyItemRemoved(indexOf);
            if (this.importingStickersPaths.isEmpty()) {
                dismiss();
            } else {
                updateFields();
            }
        }
    }

    public void setInstallDelegate(StickersAlertInstallDelegate stickersAlertInstallDelegate) {
        this.installDelegate = stickersAlertInstallDelegate;
    }

    public void setCustomButtonDelegate(StickersAlertCustomButtonDelegate stickersAlertCustomButtonDelegate) {
        this.customButtonDelegate = stickersAlertCustomButtonDelegate;
        updateFields();
    }

    /* access modifiers changed from: private */
    public void onSubItemClick(int i) {
        String str;
        BaseFragment baseFragment;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSet;
        if (tLRPC$TL_messages_stickerSet != null) {
            TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
            if (tLRPC$StickerSet == null || !tLRPC$StickerSet.emojis) {
                str = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/" + this.stickerSet.set.short_name;
            } else {
                str = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addemoji/" + this.stickerSet.set.short_name;
            }
            String str2 = str;
            if (i == 1) {
                Context context = this.parentActivity;
                if (context == null && (baseFragment = this.parentFragment) != null) {
                    context = baseFragment.getParentActivity();
                }
                if (context == null) {
                    context = getContext();
                }
                AnonymousClass10 r1 = new ShareAlert(context, (ArrayList) null, str2, false, str2, false, this.resourcesProvider) {
                    public void dismissInternal() {
                        super.dismissInternal();
                        if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                            AndroidUtilities.requestAdjustResize(StickersAlert.this.parentFragment.getParentActivity(), StickersAlert.this.parentFragment.getClassGuid());
                            if (((ChatActivity) StickersAlert.this.parentFragment).getChatActivityEnterView().getVisibility() == 0) {
                                StickersAlert.this.parentFragment.getFragmentView().requestLayout();
                            }
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onSend(LongSparseArray<TLRPC$Dialog> longSparseArray, int i) {
                        AndroidUtilities.runOnUIThread(new StickersAlert$10$$ExternalSyntheticLambda0(this, longSparseArray, i), 100);
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onSend$0(LongSparseArray longSparseArray, int i) {
                        UndoView undoView;
                        if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                            undoView = ((ChatActivity) StickersAlert.this.parentFragment).getUndoView();
                        } else {
                            undoView = StickersAlert.this.parentFragment instanceof ProfileActivity ? ((ProfileActivity) StickersAlert.this.parentFragment).getUndoView() : null;
                        }
                        UndoView undoView2 = undoView;
                        if (undoView2 == null) {
                            return;
                        }
                        if (longSparseArray.size() == 1) {
                            undoView2.showWithAction(((TLRPC$Dialog) longSparseArray.valueAt(0)).id, 53, (Object) Integer.valueOf(i));
                        } else {
                            undoView2.showWithAction(0, 53, (Object) Integer.valueOf(i), (Object) Integer.valueOf(longSparseArray.size()), (Runnable) null, (Runnable) null);
                        }
                    }
                };
                BaseFragment baseFragment2 = this.parentFragment;
                if (baseFragment2 != null) {
                    baseFragment2.showDialog(r1);
                    BaseFragment baseFragment3 = this.parentFragment;
                    if (baseFragment3 instanceof ChatActivity) {
                        r1.setCalcMandatoryInsets(((ChatActivity) baseFragment3).isKeyboardVisible());
                        return;
                    }
                    return;
                }
                r1.show();
            } else if (i == 2) {
                try {
                    AndroidUtilities.addToClipboard(str2);
                    BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createCopyLinkBulletin().show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0081, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0096  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00bc  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x017e  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01c1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFields() {
        /*
            r12 = this;
            android.widget.TextView r0 = r12.titleTextView
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r12.stickerSet
            java.lang.String r2 = "Stickers"
            r3 = 0
            r4 = 1
            r5 = 0
            if (r1 == 0) goto L_0x02ad
            java.util.regex.Pattern r0 = r12.urlPattern     // Catch:{ Exception -> 0x007c }
            if (r0 != 0) goto L_0x001a
            java.lang.String r0 = "@[a-zA-Z\\d_]{1,32}"
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x007c }
            r12.urlPattern = r0     // Catch:{ Exception -> 0x007c }
        L_0x001a:
            java.util.regex.Pattern r0 = r12.urlPattern     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r12.stickerSet     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set     // Catch:{ Exception -> 0x007c }
            java.lang.String r1 = r1.title     // Catch:{ Exception -> 0x007c }
            java.util.regex.Matcher r0 = r0.matcher(r1)     // Catch:{ Exception -> 0x007c }
            r1 = r3
        L_0x0027:
            boolean r6 = r0.find()     // Catch:{ Exception -> 0x007a }
            if (r6 == 0) goto L_0x0081
            if (r1 != 0) goto L_0x0049
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x007a }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = r12.stickerSet     // Catch:{ Exception -> 0x007a }
            org.telegram.tgnet.TLRPC$StickerSet r7 = r7.set     // Catch:{ Exception -> 0x007a }
            java.lang.String r7 = r7.title     // Catch:{ Exception -> 0x007a }
            r6.<init>(r7)     // Catch:{ Exception -> 0x007a }
            android.widget.TextView r1 = r12.titleTextView     // Catch:{ Exception -> 0x0046 }
            org.telegram.ui.Components.StickersAlert$LinkMovementMethodMy r7 = new org.telegram.ui.Components.StickersAlert$LinkMovementMethodMy     // Catch:{ Exception -> 0x0046 }
            r7.<init>()     // Catch:{ Exception -> 0x0046 }
            r1.setMovementMethod(r7)     // Catch:{ Exception -> 0x0046 }
            r1 = r6
            goto L_0x0049
        L_0x0046:
            r0 = move-exception
            r1 = r6
            goto L_0x007e
        L_0x0049:
            int r6 = r0.start()     // Catch:{ Exception -> 0x007a }
            int r7 = r0.end()     // Catch:{ Exception -> 0x007a }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = r12.stickerSet     // Catch:{ Exception -> 0x007a }
            org.telegram.tgnet.TLRPC$StickerSet r8 = r8.set     // Catch:{ Exception -> 0x007a }
            java.lang.String r8 = r8.title     // Catch:{ Exception -> 0x007a }
            char r8 = r8.charAt(r6)     // Catch:{ Exception -> 0x007a }
            r9 = 64
            if (r8 == r9) goto L_0x0061
            int r6 = r6 + 1
        L_0x0061:
            org.telegram.ui.Components.StickersAlert$11 r8 = new org.telegram.ui.Components.StickersAlert$11     // Catch:{ Exception -> 0x007a }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r9 = r12.stickerSet     // Catch:{ Exception -> 0x007a }
            org.telegram.tgnet.TLRPC$StickerSet r9 = r9.set     // Catch:{ Exception -> 0x007a }
            java.lang.String r9 = r9.title     // Catch:{ Exception -> 0x007a }
            int r10 = r6 + 1
            java.lang.CharSequence r9 = r9.subSequence(r10, r7)     // Catch:{ Exception -> 0x007a }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x007a }
            r8.<init>(r9)     // Catch:{ Exception -> 0x007a }
            r1.setSpan(r8, r6, r7, r5)     // Catch:{ Exception -> 0x007a }
            goto L_0x0027
        L_0x007a:
            r0 = move-exception
            goto L_0x007e
        L_0x007c:
            r0 = move-exception
            r1 = r3
        L_0x007e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0081:
            android.widget.TextView r0 = r12.titleTextView
            if (r1 == 0) goto L_0x0086
            goto L_0x008c
        L_0x0086:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            java.lang.String r1 = r1.title
        L_0x008c:
            r0.setText(r1)
            boolean r0 = r12.isEmoji()
            r1 = 5
            if (r0 == 0) goto L_0x00bc
            org.telegram.ui.Components.RecyclerListView r0 = r12.gridView
            int r0 = r0.getMeasuredWidth()
            if (r0 != 0) goto L_0x00a2
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x00a2:
            org.telegram.ui.Components.StickersAlert$GridAdapter r6 = r12.adapter
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r7 == 0) goto L_0x00ad
            r7 = 1114636288(0x42700000, float:60.0)
            goto L_0x00af
        L_0x00ad:
            r7 = 1110704128(0x42340000, float:45.0)
        L_0x00af:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 / r7
            int r0 = java.lang.Math.max(r4, r0)
            int unused = r6.stickersPerRow = r0
            goto L_0x00c1
        L_0x00bc:
            org.telegram.ui.Components.StickersAlert$GridAdapter r0 = r12.adapter
            int unused = r0.stickersPerRow = r1
        L_0x00c1:
            androidx.recyclerview.widget.GridLayoutManager r0 = r12.layoutManager
            org.telegram.ui.Components.StickersAlert$GridAdapter r6 = r12.adapter
            int r6 = r6.stickersPerRow
            r0.setSpanCount(r6)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r12.stickerSet
            if (r0 == 0) goto L_0x012d
            org.telegram.tgnet.TLRPC$StickerSet r0 = r0.set
            if (r0 == 0) goto L_0x012d
            boolean r0 = r0.emojis
            if (r0 == 0) goto L_0x012d
            int r0 = r12.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isPremium()
            if (r0 != 0) goto L_0x012d
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r12.stickerSet
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            if (r0 == 0) goto L_0x010a
            r0 = 0
        L_0x00eb:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r6 = r12.stickerSet
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r6.documents
            int r6 = r6.size()
            if (r0 >= r6) goto L_0x010a
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r6 = r12.stickerSet
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r6.documents
            java.lang.Object r6 = r6.get(r0)
            org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC$Document) r6
            boolean r6 = org.telegram.messenger.MessageObject.isFreeEmoji(r6)
            if (r6 != 0) goto L_0x0107
            r0 = 1
            goto L_0x010b
        L_0x0107:
            int r0 = r0 + 1
            goto L_0x00eb
        L_0x010a:
            r0 = 0
        L_0x010b:
            if (r0 == 0) goto L_0x0133
            org.telegram.ui.Components.Premium.PremiumButtonView r0 = r12.premiumButtonView
            r0.setVisibility(r5)
            android.widget.TextView r0 = r12.pickerBottomLayout
            r0.setBackground(r3)
            r12.setButton(r3, r3, r3)
            org.telegram.ui.Components.Premium.PremiumButtonView r0 = r12.premiumButtonView
            int r1 = org.telegram.messenger.R.string.UnlockPremiumEmoji
            java.lang.String r2 = "UnlockPremiumEmoji"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda12 r2 = new org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda12
            r2.<init>(r12)
            r0.setButton(r1, r2)
            return
        L_0x012d:
            org.telegram.ui.Components.Premium.PremiumButtonView r0 = r12.premiumButtonView
            r3 = 4
            r0.setVisibility(r3)
        L_0x0133:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r12.stickerSet
            if (r0 == 0) goto L_0x017e
            org.telegram.tgnet.TLRPC$StickerSet r3 = r0.set
            if (r3 == 0) goto L_0x017e
            boolean r3 = r3.emojis
            if (r3 == 0) goto L_0x017e
            int r0 = r12.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.util.ArrayList r0 = r0.getStickerSets(r1)
            r1 = 0
        L_0x014a:
            if (r0 == 0) goto L_0x017b
            int r3 = r0.size()
            if (r1 >= r3) goto L_0x017b
            java.lang.Object r3 = r0.get(r1)
            if (r3 == 0) goto L_0x0178
            java.lang.Object r3 = r0.get(r1)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r3
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.set
            if (r3 == 0) goto L_0x0178
            java.lang.Object r3 = r0.get(r1)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r3
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.set
            long r6 = r3.id
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.set
            long r8 = r3.id
            int r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r3 != 0) goto L_0x0178
            r0 = 1
            goto L_0x017c
        L_0x0178:
            int r1 = r1 + 1
            goto L_0x014a
        L_0x017b:
            r0 = 0
        L_0x017c:
            r0 = r0 ^ r4
            goto L_0x019a
        L_0x017e:
            if (r0 == 0) goto L_0x0199
            org.telegram.tgnet.TLRPC$StickerSet r0 = r0.set
            if (r0 == 0) goto L_0x0199
            int r0 = r12.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            long r6 = r1.id
            boolean r0 = r0.isStickerPackInstalled((long) r6)
            if (r0 != 0) goto L_0x0197
            goto L_0x0199
        L_0x0197:
            r0 = 0
            goto L_0x019a
        L_0x0199:
            r0 = 1
        L_0x019a:
            org.telegram.ui.Components.StickersAlert$StickersAlertCustomButtonDelegate r1 = r12.customButtonDelegate
            if (r1 == 0) goto L_0x01c1
            org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda10 r7 = new org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda10
            r7.<init>(r12)
            org.telegram.ui.Components.StickersAlert$StickersAlertCustomButtonDelegate r0 = r12.customButtonDelegate
            java.lang.String r8 = r0.getCustomButtonText()
            org.telegram.ui.Components.StickersAlert$StickersAlertCustomButtonDelegate r0 = r12.customButtonDelegate
            java.lang.String r9 = r0.getCustomButtonTextColorKey()
            org.telegram.ui.Components.StickersAlert$StickersAlertCustomButtonDelegate r0 = r12.customButtonDelegate
            java.lang.String r10 = r0.getCustomButtonColorKey()
            org.telegram.ui.Components.StickersAlert$StickersAlertCustomButtonDelegate r0 = r12.customButtonDelegate
            java.lang.String r11 = r0.getCustomButtonRippleColorKey()
            r6 = r12
            r6.setButton(r7, r8, r9, r10, r11)
            goto L_0x02a6
        L_0x01c1:
            java.lang.String r1 = "EmojiCountButton"
            java.lang.String r3 = "MasksCount"
            if (r0 == 0) goto L_0x0239
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r12.stickerSet
            java.lang.String r6 = "AddStickersCount"
            if (r0 == 0) goto L_0x01ed
            org.telegram.tgnet.TLRPC$StickerSet r7 = r0.set
            if (r7 == 0) goto L_0x01ed
            boolean r7 = r7.masks
            if (r7 == 0) goto L_0x01ed
            int r1 = org.telegram.messenger.R.string.AddStickersCount
            java.lang.Object[] r2 = new java.lang.Object[r4]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0, r4)
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r1, r2)
        L_0x01eb:
            r3 = r0
            goto L_0x0229
        L_0x01ed:
            if (r0 == 0) goto L_0x020e
            org.telegram.tgnet.TLRPC$StickerSet r3 = r0.set
            if (r3 == 0) goto L_0x020e
            boolean r3 = r3.emojis
            if (r3 == 0) goto L_0x020e
            int r2 = org.telegram.messenger.R.string.AddStickersCount
            java.lang.Object[] r3 = new java.lang.Object[r4]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r1, r0, r4)
            r3[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r2, r3)
            goto L_0x01eb
        L_0x020e:
            int r1 = org.telegram.messenger.R.string.AddStickersCount
            java.lang.Object[] r3 = new java.lang.Object[r4]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            if (r0 != 0) goto L_0x0218
            r0 = 0
            goto L_0x021c
        L_0x0218:
            int r0 = r0.size()
        L_0x021c:
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0, r4)
            r3[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r1, r3)
            goto L_0x01eb
        L_0x0229:
            org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda9 r2 = new org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda9
            r2.<init>(r12)
            java.lang.String r4 = "featuredStickers_buttonText"
            java.lang.String r5 = "featuredStickers_addButton"
            java.lang.String r6 = "featuredStickers_addButtonPressed"
            r1 = r12
            r1.setButton(r2, r3, r4, r5, r6)
            goto L_0x02a6
        L_0x0239:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r6 = r0.set
            boolean r7 = r6.masks
            java.lang.String r8 = "RemoveStickersCount"
            if (r7 == 0) goto L_0x025a
            int r1 = org.telegram.messenger.R.string.RemoveStickersCount
            java.lang.Object[] r2 = new java.lang.Object[r4]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0, r4)
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r1, r2)
            goto L_0x028b
        L_0x025a:
            boolean r3 = r6.emojis
            if (r3 == 0) goto L_0x0275
            int r2 = org.telegram.messenger.R.string.RemoveStickersCount
            java.lang.Object[] r3 = new java.lang.Object[r4]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r1, r0, r4)
            r3[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r2, r3)
            goto L_0x028b
        L_0x0275:
            int r1 = org.telegram.messenger.R.string.RemoveStickersCount
            java.lang.Object[] r3 = new java.lang.Object[r4]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0, r4)
            r3[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r1, r3)
        L_0x028b:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            boolean r1 = r1.official
            java.lang.String r2 = "dialogTextRed"
            if (r1 == 0) goto L_0x029e
            org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda11 r1 = new org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda11
            r1.<init>(r12)
            r12.setButton(r1, r0, r2)
            goto L_0x02a6
        L_0x029e:
            org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda5 r1 = new org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda5
            r1.<init>(r12)
            r12.setButton(r1, r0, r2)
        L_0x02a6:
            org.telegram.ui.Components.StickersAlert$GridAdapter r0 = r12.adapter
            r0.notifyDataSetChanged()
            goto L_0x0322
        L_0x02ad:
            java.util.ArrayList<android.os.Parcelable> r1 = r12.importingStickers
            java.lang.String r6 = "dialogTextBlue2"
            if (r1 == 0) goto L_0x0312
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$ImportingSticker> r7 = r12.importingStickersPaths
            if (r7 == 0) goto L_0x02bc
            int r1 = r7.size()
            goto L_0x02c0
        L_0x02bc:
            int r1 = r1.size()
        L_0x02c0:
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1, r7)
            r0.setText(r1)
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r0 = r12.uploadImportStickers
            if (r0 == 0) goto L_0x02e7
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02d4
            goto L_0x02e7
        L_0x02d4:
            int r0 = org.telegram.messenger.R.string.ImportStickersProcessing
            java.lang.String r1 = "ImportStickersProcessing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.String r1 = "dialogTextGray2"
            r12.setButton(r3, r0, r1)
            android.widget.TextView r0 = r12.pickerBottomLayout
            r0.setEnabled(r5)
            goto L_0x0322
        L_0x02e7:
            org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda7 r0 = new org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda7
            r0.<init>(r12)
            int r1 = org.telegram.messenger.R.string.ImportStickers
            java.lang.Object[] r3 = new java.lang.Object[r4]
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$ImportingSticker> r7 = r12.importingStickersPaths
            if (r7 == 0) goto L_0x02f5
            goto L_0x02f7
        L_0x02f5:
            java.util.ArrayList<android.os.Parcelable> r7 = r12.importingStickers
        L_0x02f7:
            int r7 = r7.size()
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r7, r8)
            r3[r5] = r2
            java.lang.String r2 = "ImportStickers"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            r12.setButton(r0, r1, r6)
            android.widget.TextView r0 = r12.pickerBottomLayout
            r0.setEnabled(r4)
            goto L_0x0322
        L_0x0312:
            int r0 = org.telegram.messenger.R.string.Close
            java.lang.String r1 = "Close"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda8 r1 = new org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda8
            r1.<init>(r12)
            r12.setButton(r1, r0, r6)
        L_0x0322:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.updateFields():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFields$13(View view) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            new PremiumFeatureBottomSheet(baseFragment, 11, false).show();
        } else if (getContext() instanceof LaunchActivity) {
            ((LaunchActivity) getContext()).lambda$runLinkRequest$62(new PremiumPreviewFragment((String) null));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFields$14(View view) {
        if (this.customButtonDelegate.onCustomButtonPressed()) {
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFields$17(View view) {
        dismiss();
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetInstalled();
        }
        if (this.inputStickerSet != null && !MediaDataController.getInstance(this.currentAccount).cancelRemovingStickerSet(this.inputStickerSet.id)) {
            TLRPC$TL_messages_installStickerSet tLRPC$TL_messages_installStickerSet = new TLRPC$TL_messages_installStickerSet();
            tLRPC$TL_messages_installStickerSet.stickerset = this.inputStickerSet;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_installStickerSet, new StickersAlert$$ExternalSyntheticLambda28(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFields$16(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda24(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFields$15(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        int i;
        TLRPC$StickerSet tLRPC$StickerSet = this.stickerSet.set;
        if (tLRPC$StickerSet.masks) {
            i = 1;
        } else {
            i = tLRPC$StickerSet.emojis ? 5 : 0;
        }
        if (tLRPC$TL_error == null) {
            try {
                if (this.showTooltipWhenToggle) {
                    Bulletin.make(this.parentFragment, (Bulletin.Layout) new StickerSetBulletinLayout(this.pickerBottomFrameLayout.getContext(), this.stickerSet, 2, (TLRPC$Document) null, this.resourcesProvider), 1500).show();
                }
                if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
                    MediaDataController.getInstance(this.currentAccount).processStickerSetInstallResultArchive(this.parentFragment, true, i, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            Toast.makeText(getContext(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
        }
        MediaDataController.getInstance(this.currentAccount).loadStickers(i, false, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFields$18(View view) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), this.stickerSet, 1, this.parentFragment, true, this.showTooltipWhenToggle);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFields$19(View view) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), this.stickerSet, 0, this.parentFragment, true, this.showTooltipWhenToggle);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFields$20(View view) {
        showNameEnterAlert();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFields$21(View view) {
        dismiss();
    }

    private void showNameEnterAlert() {
        Context context = getContext();
        final int[] iArr = {0};
        FrameLayout frameLayout = new FrameLayout(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("ImportStickersEnterName", R.string.ImportStickersEnterName));
        builder.setPositiveButton(LocaleController.getString("Next", R.string.Next), StickersAlert$$ExternalSyntheticLambda1.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
        final TextView textView = new TextView(context);
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 16.0f);
        textView2.setTextColor(getThemedColor("dialogTextHint"));
        textView2.setMaxLines(1);
        textView2.setLines(1);
        textView2.setText("t.me/addstickers/");
        textView2.setInputType(16385);
        textView2.setGravity(51);
        textView2.setSingleLine(true);
        textView2.setVisibility(4);
        textView2.setImeOptions(6);
        textView2.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        frameLayout.addView(textView2, LayoutHelper.createFrame(-2, 36, 51));
        final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        editTextBoldCursor.setBackground((Drawable) null);
        editTextBoldCursor.setLineColors(Theme.getColor("dialogInputField"), Theme.getColor("dialogInputFieldActivated"), Theme.getColor("dialogTextRed2"));
        editTextBoldCursor.setTextSize(1, 16.0f);
        editTextBoldCursor.setTextColor(getThemedColor("dialogTextBlack"));
        editTextBoldCursor.setMaxLines(1);
        editTextBoldCursor.setLines(1);
        editTextBoldCursor.setInputType(16385);
        editTextBoldCursor.setGravity(51);
        editTextBoldCursor.setSingleLine(true);
        editTextBoldCursor.setImeOptions(5);
        editTextBoldCursor.setCursorColor(getThemedColor("windowBackgroundWhiteBlackText"));
        editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
        editTextBoldCursor.setCursorWidth(1.5f);
        editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        editTextBoldCursor.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (iArr[0] == 2) {
                    StickersAlert.this.checkUrlAvailable(textView, editTextBoldCursor.getText().toString(), false);
                }
            }
        });
        frameLayout.addView(editTextBoldCursor, LayoutHelper.createFrame(-1, 36, 51));
        editTextBoldCursor.setOnEditorActionListener(new StickersAlert$$ExternalSyntheticLambda16(builder));
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new StickersAlert$$ExternalSyntheticLambda0(editTextBoldCursor));
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ImportStickersEnterNameInfo", R.string.ImportStickersEnterNameInfo)));
        textView.setTextSize(1, 14.0f);
        textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
        textView.setTextColor(getThemedColor("dialogTextGray2"));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        AlertDialog create = builder.create();
        create.setOnShowListener(new StickersAlert$$ExternalSyntheticLambda2(editTextBoldCursor));
        create.show();
        editTextBoldCursor.requestFocus();
        create.getButton(-1).setOnClickListener(new StickersAlert$$ExternalSyntheticLambda13(this, iArr, editTextBoldCursor, textView, textView2, builder));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$showNameEnterAlert$23(AlertDialog.Builder builder, TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        builder.create().getButton(-1).callOnClick();
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showNameEnterAlert$25(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showNameEnterAlert$30(int[] iArr, EditTextBoldCursor editTextBoldCursor, TextView textView, TextView textView2, AlertDialog.Builder builder, View view) {
        if (iArr[0] != 1) {
            if (iArr[0] == 0) {
                iArr[0] = 1;
                TLRPC$TL_stickers_suggestShortName tLRPC$TL_stickers_suggestShortName = new TLRPC$TL_stickers_suggestShortName();
                String obj = editTextBoldCursor.getText().toString();
                this.setTitle = obj;
                tLRPC$TL_stickers_suggestShortName.title = obj;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_stickers_suggestShortName, new StickersAlert$$ExternalSyntheticLambda33(this, editTextBoldCursor, textView, textView2, iArr));
            } else if (iArr[0] == 2) {
                iArr[0] = 3;
                if (!this.lastNameAvailable) {
                    AndroidUtilities.shakeView(editTextBoldCursor, 2.0f, 0);
                    editTextBoldCursor.performHapticFeedback(3, 2);
                }
                AndroidUtilities.hideKeyboard(editTextBoldCursor);
                SendMessagesHelper.getInstance(this.currentAccount).prepareImportStickers(this.setTitle, this.lastCheckName, this.importingSoftware, this.importingStickersPaths, new StickersAlert$$ExternalSyntheticLambda27(this));
                builder.getDismissRunnable().run();
                dismiss();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showNameEnterAlert$28(EditTextBoldCursor editTextBoldCursor, TextView textView, TextView textView2, int[] iArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda23(this, tLObject, editTextBoldCursor, textView, textView2, iArr));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showNameEnterAlert$27(TLObject tLObject, EditTextBoldCursor editTextBoldCursor, TextView textView, TextView textView2, int[] iArr) {
        String str;
        boolean z = true;
        if (!(tLObject instanceof TLRPC$TL_stickers_suggestedShortName) || (str = ((TLRPC$TL_stickers_suggestedShortName) tLObject).short_name) == null) {
            z = false;
        } else {
            editTextBoldCursor.setText(str);
            editTextBoldCursor.setSelection(0, editTextBoldCursor.length());
            checkUrlAvailable(textView, editTextBoldCursor.getText().toString(), true);
        }
        textView2.setVisibility(0);
        editTextBoldCursor.setPadding(textView2.getMeasuredWidth(), AndroidUtilities.dp(4.0f), 0, 0);
        if (!z) {
            editTextBoldCursor.setText("");
        }
        iArr[0] = 2;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showNameEnterAlert$29(String str) {
        new ImportingAlert(getContext(), this.lastCheckName, (ChatActivity) null, this.resourcesProvider).show();
    }

    /* access modifiers changed from: private */
    public void checkUrlAvailable(TextView textView, String str, boolean z) {
        if (z) {
            textView.setText(LocaleController.getString("ImportStickersLinkAvailable", R.string.ImportStickersLinkAvailable));
            textView.setTextColor(getThemedColor("windowBackgroundWhiteGreenText"));
            this.lastNameAvailable = true;
            this.lastCheckName = str;
            return;
        }
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        if (TextUtils.isEmpty(str)) {
            textView.setText(LocaleController.getString("ImportStickersEnterUrlInfo", R.string.ImportStickersEnterUrlInfo));
            textView.setTextColor(getThemedColor("dialogTextGray2"));
            return;
        }
        this.lastNameAvailable = false;
        if (str != null) {
            if (str.startsWith("_") || str.endsWith("_")) {
                textView.setText(LocaleController.getString("ImportStickersLinkInvalid", R.string.ImportStickersLinkInvalid));
                textView.setTextColor(getThemedColor("windowBackgroundWhiteRedText4"));
                return;
            }
            int length = str.length();
            for (int i = 0; i < length; i++) {
                char charAt = str.charAt(i);
                if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    textView.setText(LocaleController.getString("ImportStickersEnterUrlInfo", R.string.ImportStickersEnterUrlInfo));
                    textView.setTextColor(getThemedColor("windowBackgroundWhiteRedText4"));
                    return;
                }
            }
        }
        if (str == null || str.length() < 5) {
            textView.setText(LocaleController.getString("ImportStickersLinkInvalidShort", R.string.ImportStickersLinkInvalidShort));
            textView.setTextColor(getThemedColor("windowBackgroundWhiteRedText4"));
        } else if (str.length() > 32) {
            textView.setText(LocaleController.getString("ImportStickersLinkInvalidLong", R.string.ImportStickersLinkInvalidLong));
            textView.setTextColor(getThemedColor("windowBackgroundWhiteRedText4"));
        } else {
            textView.setText(LocaleController.getString("ImportStickersLinkChecking", R.string.ImportStickersLinkChecking));
            textView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText8"));
            this.lastCheckName = str;
            StickersAlert$$ExternalSyntheticLambda18 stickersAlert$$ExternalSyntheticLambda18 = new StickersAlert$$ExternalSyntheticLambda18(this, str, textView);
            this.checkRunnable = stickersAlert$$ExternalSyntheticLambda18;
            AndroidUtilities.runOnUIThread(stickersAlert$$ExternalSyntheticLambda18, 300);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUrlAvailable$33(String str, TextView textView) {
        TLRPC$TL_stickers_checkShortName tLRPC$TL_stickers_checkShortName = new TLRPC$TL_stickers_checkShortName();
        tLRPC$TL_stickers_checkShortName.short_name = str;
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_stickers_checkShortName, new StickersAlert$$ExternalSyntheticLambda30(this, str, textView), 2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUrlAvailable$32(String str, TextView textView, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda20(this, str, tLRPC$TL_error, tLObject, textView));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUrlAvailable$31(String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TextView textView) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 != null && str2.equals(str)) {
            if (tLRPC$TL_error != null || !(tLObject instanceof TLRPC$TL_boolTrue)) {
                textView.setText(LocaleController.getString("ImportStickersLinkTaken", R.string.ImportStickersLinkTaken));
                textView.setTextColor(getThemedColor("windowBackgroundWhiteRedText4"));
                this.lastNameAvailable = false;
                return;
            }
            textView.setText(LocaleController.getString("ImportStickersLinkAvailable", R.string.ImportStickersLinkAvailable));
            textView.setTextColor(getThemedColor("windowBackgroundWhiteGreenText"));
            this.lastNameAvailable = true;
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void updateLayout() {
        if (this.gridView.getChildCount() <= 0) {
            setScrollOffsetY(this.gridView.getPaddingTop());
            return;
        }
        int i = 0;
        View childAt = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(0, true);
        } else {
            runShadowAnimation(0, false);
            i = top;
        }
        runShadowAnimation(1, true);
        if (this.scrollOffsetY != i) {
            setScrollOffsetY(i);
        }
    }

    /* access modifiers changed from: private */
    public void setScrollOffsetY(int i) {
        this.scrollOffsetY = i;
        this.gridView.setTopGlowOffset(i);
        if (this.stickerSetCovereds == null) {
            float f = (float) i;
            this.titleTextView.setTranslationY(f);
            TextView textView = this.descriptionTextView;
            if (textView != null) {
                textView.setTranslationY(f);
            }
            if (this.importingStickers == null) {
                this.optionsButton.setTranslationY(f);
            }
            this.shadow[0].setTranslationY(f);
        }
        this.containerView.invalidate();
    }

    private void hidePreview() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, new float[]{0.0f})});
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                StickersAlert.this.stickerPreviewLayout.setVisibility(8);
                StickersAlert.this.stickerImageView.setImageDrawable((Drawable) null);
            }
        });
        animatorSet.start();
    }

    private void runShadowAnimation(final int i, final boolean z) {
        if (this.stickerSetCovereds == null) {
            if ((z && this.shadow[i].getTag() != null) || (!z && this.shadow[i].getTag() == null)) {
                this.shadow[i].setTag(z ? null : 1);
                if (z) {
                    this.shadow[i].setVisibility(0);
                }
                AnimatorSet[] animatorSetArr = this.shadowAnimation;
                if (animatorSetArr[i] != null) {
                    animatorSetArr[i].cancel();
                }
                this.shadowAnimation[i] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[i];
                Animator[] animatorArr = new Animator[1];
                View view = this.shadow[i];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[i].setDuration(150);
                this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (StickersAlert.this.shadowAnimation[i] != null && StickersAlert.this.shadowAnimation[i].equals(animator)) {
                            if (!z) {
                                StickersAlert.this.shadow[i].setVisibility(4);
                            }
                            StickersAlert.this.shadowAnimation[i] = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (StickersAlert.this.shadowAnimation[i] != null && StickersAlert.this.shadowAnimation[i].equals(animator)) {
                            StickersAlert.this.shadowAnimation[i] = null;
                        }
                    }
                });
                this.shadowAnimation[i].start();
            }
        }
    }

    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
    }

    public void setOnDismissListener(Runnable runnable) {
        this.onDismissListener = runnable;
    }

    public void dismiss() {
        super.dismiss();
        Runnable runnable = this.onDismissListener;
        if (runnable != null) {
            runnable.run();
        }
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        if (this.importingStickers != null) {
            ArrayList<SendMessagesHelper.ImportingSticker> arrayList = this.importingStickersPaths;
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    SendMessagesHelper.ImportingSticker importingSticker = this.importingStickersPaths.get(i);
                    if (!importingSticker.validated) {
                        FileLoader.getInstance(this.currentAccount).cancelFileUpload(importingSticker.path, false);
                    }
                    if (importingSticker.animated) {
                        new File(importingSticker.path).delete();
                    }
                }
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Bulletin.addDelegate((FrameLayout) this.containerView, (Bulletin.Delegate) new Bulletin.Delegate() {
            public /* synthetic */ void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            public /* synthetic */ void onOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onOffsetChange(this, f);
            }

            public /* synthetic */ void onShow(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onShow(this, bulletin);
            }

            public int getBottomOffset(int i) {
                if (StickersAlert.this.pickerBottomFrameLayout != null) {
                    return StickersAlert.this.pickerBottomFrameLayout.getHeight();
                }
                return 0;
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        Bulletin.removeDelegate((FrameLayout) this.containerView);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0024, code lost:
        r4 = r5[0];
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r3, int r4, java.lang.Object... r5) {
        /*
            r2 = this;
            int r4 = org.telegram.messenger.NotificationCenter.emojiLoaded
            r0 = 0
            if (r3 != r4) goto L_0x001b
            org.telegram.ui.Components.RecyclerListView r3 = r2.gridView
            if (r3 == 0) goto L_0x0063
            int r3 = r3.getChildCount()
        L_0x000d:
            if (r0 >= r3) goto L_0x0063
            org.telegram.ui.Components.RecyclerListView r4 = r2.gridView
            android.view.View r4 = r4.getChildAt(r0)
            r4.invalidate()
            int r0 = r0 + 1
            goto L_0x000d
        L_0x001b:
            int r4 = org.telegram.messenger.NotificationCenter.fileUploaded
            if (r3 != r4) goto L_0x0040
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r3 = r2.uploadImportStickers
            if (r3 != 0) goto L_0x0024
            return
        L_0x0024:
            r4 = r5[r0]
            java.lang.String r4 = (java.lang.String) r4
            java.lang.Object r3 = r3.get(r4)
            org.telegram.messenger.SendMessagesHelper$ImportingSticker r3 = (org.telegram.messenger.SendMessagesHelper.ImportingSticker) r3
            if (r3 == 0) goto L_0x0063
            int r0 = r2.currentAccount
            r1 = 1
            r5 = r5[r1]
            org.telegram.tgnet.TLRPC$InputFile r5 = (org.telegram.tgnet.TLRPC$InputFile) r5
            org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda19 r1 = new org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda19
            r1.<init>(r2, r4, r3)
            r3.uploadMedia(r0, r5, r1)
            goto L_0x0063
        L_0x0040:
            int r4 = org.telegram.messenger.NotificationCenter.fileUploadFailed
            if (r3 != r4) goto L_0x0063
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r3 = r2.uploadImportStickers
            if (r3 != 0) goto L_0x0049
            return
        L_0x0049:
            r4 = r5[r0]
            java.lang.String r4 = (java.lang.String) r4
            java.lang.Object r3 = r3.remove(r4)
            org.telegram.messenger.SendMessagesHelper$ImportingSticker r3 = (org.telegram.messenger.SendMessagesHelper.ImportingSticker) r3
            if (r3 == 0) goto L_0x0058
            r2.removeSticker(r3)
        L_0x0058:
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r3 = r2.uploadImportStickers
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0063
            r2.updateFields()
        L_0x0063:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$34(String str, SendMessagesHelper.ImportingSticker importingSticker) {
        if (!isDismissed()) {
            this.uploadImportStickers.remove(str);
            if (!"application/x-tgsticker".equals(importingSticker.mimeType)) {
                removeSticker(importingSticker);
            } else {
                importingSticker.validated = true;
                int indexOf = this.importingStickersPaths.indexOf(importingSticker);
                if (indexOf >= 0) {
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.gridView.findViewHolderForAdapterPosition(indexOf);
                    if (findViewHolderForAdapterPosition != null) {
                        ((StickerEmojiCell) findViewHolderForAdapterPosition.itemView).setSticker(importingSticker);
                    }
                } else {
                    this.adapter.notifyDataSetChanged();
                }
            }
            if (this.uploadImportStickers.isEmpty()) {
                updateFields();
            }
        }
    }

    private void setButton(View.OnClickListener onClickListener, String str, String str2) {
        setButton(onClickListener, str, str2, (String) null, (String) null);
    }

    private void setButton(View.OnClickListener onClickListener, String str, String str2, String str3, String str4) {
        if (str2 != null) {
            TextView textView = this.pickerBottomLayout;
            this.buttonTextColorKey = str2;
            textView.setTextColor(getThemedColor(str2));
        }
        this.pickerBottomLayout.setText(str);
        this.pickerBottomLayout.setOnClickListener(onClickListener);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.pickerBottomLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.shadow[1].getLayoutParams();
        ViewGroup.MarginLayoutParams marginLayoutParams3 = (ViewGroup.MarginLayoutParams) this.gridView.getLayoutParams();
        ViewGroup.MarginLayoutParams marginLayoutParams4 = (ViewGroup.MarginLayoutParams) this.emptyView.getLayoutParams();
        if (str3 == null || str4 == null) {
            this.pickerBottomLayout.setBackground(Theme.createSelectorWithBackgroundDrawable(getThemedColor("dialogBackground"), getThemedColor("listSelectorSDK21")));
            this.pickerBottomFrameLayout.setBackgroundColor(0);
            marginLayoutParams.bottomMargin = 0;
            marginLayoutParams.rightMargin = 0;
            marginLayoutParams.topMargin = 0;
            marginLayoutParams.leftMargin = 0;
            int dp = AndroidUtilities.dp(48.0f);
            marginLayoutParams2.bottomMargin = dp;
            marginLayoutParams3.bottomMargin = dp;
            marginLayoutParams4.bottomMargin = dp;
        } else {
            this.pickerBottomLayout.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor(str3), getThemedColor(str4)));
            this.pickerBottomFrameLayout.setBackgroundColor(getThemedColor("dialogBackground"));
            int dp2 = AndroidUtilities.dp(8.0f);
            marginLayoutParams.bottomMargin = dp2;
            marginLayoutParams.rightMargin = dp2;
            marginLayoutParams.topMargin = dp2;
            marginLayoutParams.leftMargin = dp2;
            int dp3 = AndroidUtilities.dp(64.0f);
            marginLayoutParams2.bottomMargin = dp3;
            marginLayoutParams3.bottomMargin = dp3;
            marginLayoutParams4.bottomMargin = dp3;
        }
        this.containerView.requestLayout();
    }

    public void setShowTooltipWhenToggle(boolean z) {
        this.showTooltipWhenToggle = z;
    }

    public void updateColors() {
        updateColors(false);
    }

    public void updateColors(boolean z) {
        this.adapter.updateColors();
        this.titleTextView.setHighlightColor(getThemedColor("dialogLinkSelection"));
        this.stickerPreviewLayout.setBackgroundColor(getThemedColor("dialogBackground") & -NUM);
        this.optionsButton.setIconColor(getThemedColor("key_sheet_other"));
        this.optionsButton.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItem"), false);
        this.optionsButton.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItemIcon"), true);
        this.optionsButton.setPopupItemsSelectorColor(getThemedColor("dialogButtonSelector"));
        this.optionsButton.redrawPopup(getThemedColor("actionBarDefaultSubmenuBackground"));
        if (z) {
            if (Theme.isAnimatingColor() && this.animatingDescriptions == null) {
                ArrayList<ThemeDescription> themeDescriptions = getThemeDescriptions();
                this.animatingDescriptions = themeDescriptions;
                int size = themeDescriptions.size();
                for (int i = 0; i < size; i++) {
                    this.animatingDescriptions.get(i).setDelegateDisabled();
                }
            }
            int size2 = this.animatingDescriptions.size();
            for (int i2 = 0; i2 < size2; i2++) {
                ThemeDescription themeDescription = this.animatingDescriptions.get(i2);
                themeDescription.setColor(getThemedColor(themeDescription.getCurrentKey()), false, false);
            }
        }
        if (!Theme.isAnimatingColor() && this.animatingDescriptions != null) {
            this.animatingDescriptions = null;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        StickersAlert$$ExternalSyntheticLambda35 stickersAlert$$ExternalSyntheticLambda35 = new StickersAlert$$ExternalSyntheticLambda35(this);
        arrayList.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.shadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        this.adapter.getThemeDescriptions(arrayList, stickersAlert$$ExternalSyntheticLambda35);
        arrayList.add(new ThemeDescription(this.shadow[0], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        arrayList.add(new ThemeDescription(this.shadow[1], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        arrayList.add(new ThemeDescription(this.gridView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        if (this.descriptionTextView != null) {
            arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_emojiPanelTrendingDescription"));
        }
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_LINKCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextLink"));
        arrayList.add(new ThemeDescription(this.optionsButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSelector"));
        arrayList.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.buttonTextColorKey));
        arrayList.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue2"));
        arrayList.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.previewSendButtonShadow, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        StickersAlert$$ExternalSyntheticLambda35 stickersAlert$$ExternalSyntheticLambda352 = stickersAlert$$ExternalSyntheticLambda35;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, stickersAlert$$ExternalSyntheticLambda352, "dialogLinkSelection"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, stickersAlert$$ExternalSyntheticLambda352, "dialogBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, stickersAlert$$ExternalSyntheticLambda352, "key_sheet_other"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, stickersAlert$$ExternalSyntheticLambda352, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, stickersAlert$$ExternalSyntheticLambda352, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, stickersAlert$$ExternalSyntheticLambda352, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, stickersAlert$$ExternalSyntheticLambda352, "actionBarDefaultSubmenuBackground"));
        return arrayList;
    }

    private class GridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private Context context;
        /* access modifiers changed from: private */
        public SparseArray<TLRPC$StickerSetCovered> positionsToSets = new SparseArray<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int stickersRowCount;
        /* access modifiers changed from: private */
        public int totalItems;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public GridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public int getItemViewType(int i) {
            if (StickersAlert.this.stickerSetCovereds == null) {
                return 0;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC$Document) {
                return 0;
            }
            return 2;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: org.telegram.ui.Components.StickersAlert$GridAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: org.telegram.ui.Cells.EmptyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v9, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r7, int r8) {
            /*
                r6 = this;
                if (r8 == 0) goto L_0x0025
                r7 = 1
                if (r8 == r7) goto L_0x001d
                r7 = 2
                if (r8 == r7) goto L_0x000a
                r7 = 0
                goto L_0x0035
            L_0x000a:
                org.telegram.ui.Cells.FeaturedStickerSetInfoCell r7 = new org.telegram.ui.Cells.FeaturedStickerSetInfoCell
                android.content.Context r1 = r6.context
                r2 = 8
                r3 = 1
                r4 = 0
                org.telegram.ui.Components.StickersAlert r8 = org.telegram.ui.Components.StickersAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r8.resourcesProvider
                r0 = r7
                r0.<init>(r1, r2, r3, r4, r5)
                goto L_0x0035
            L_0x001d:
                org.telegram.ui.Cells.EmptyCell r7 = new org.telegram.ui.Cells.EmptyCell
                android.content.Context r8 = r6.context
                r7.<init>(r8)
                goto L_0x0035
            L_0x0025:
                org.telegram.ui.Components.StickersAlert$GridAdapter$1 r7 = new org.telegram.ui.Components.StickersAlert$GridAdapter$1
                android.content.Context r8 = r6.context
                r0 = 0
                r7.<init>(r8, r0)
                org.telegram.messenger.ImageReceiver r8 = r7.getImageView()
                r0 = 7
                r8.setLayerNum(r0)
            L_0x0035:
                org.telegram.ui.Components.RecyclerListView$Holder r8 = new org.telegram.ui.Components.RecyclerListView$Holder
                r8.<init>(r7)
                return r8
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.GridAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType == 0) {
                    ((StickerEmojiCell) viewHolder.itemView).setSticker((TLRPC$Document) this.cache.get(i), this.positionsToSets.get(i), false);
                } else if (itemViewType == 1) {
                    ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                } else if (itemViewType == 2) {
                    ((FeaturedStickerSetInfoCell) viewHolder.itemView).setStickerSet((TLRPC$StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(((Integer) this.cache.get(i)).intValue()), false);
                }
            } else if (StickersAlert.this.importingStickers != null) {
                ((StickerEmojiCell) viewHolder.itemView).setSticker((SendMessagesHelper.ImportingSticker) StickersAlert.this.importingStickersPaths.get(i));
            } else {
                ((StickerEmojiCell) viewHolder.itemView).setSticker(StickersAlert.this.stickerSet.documents.get(i), StickersAlert.this.stickerSet, StickersAlert.this.showEmoji);
            }
        }

        public void notifyDataSetChanged() {
            int i;
            int i2;
            int i3 = 0;
            if (StickersAlert.this.stickerSetCovereds != null) {
                int measuredWidth = StickersAlert.this.gridView.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
                StickersAlert.this.layoutManager.setSpanCount(this.stickersPerRow);
                this.cache.clear();
                this.positionsToSets.clear();
                this.totalItems = 0;
                this.stickersRowCount = 0;
                for (int i4 = 0; i4 < StickersAlert.this.stickerSetCovereds.size(); i4++) {
                    TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(i4);
                    if (!tLRPC$StickerSetCovered.covers.isEmpty() || tLRPC$StickerSetCovered.cover != null) {
                        double d = (double) this.stickersRowCount;
                        double ceil = Math.ceil((double) (((float) StickersAlert.this.stickerSetCovereds.size()) / ((float) this.stickersPerRow)));
                        Double.isNaN(d);
                        this.stickersRowCount = (int) (d + ceil);
                        this.positionsToSets.put(this.totalItems, tLRPC$StickerSetCovered);
                        SparseArray<Object> sparseArray = this.cache;
                        int i5 = this.totalItems;
                        this.totalItems = i5 + 1;
                        sparseArray.put(i5, Integer.valueOf(i4));
                        int i6 = this.totalItems / this.stickersPerRow;
                        if (!tLRPC$StickerSetCovered.covers.isEmpty()) {
                            i = (int) Math.ceil((double) (((float) tLRPC$StickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                            for (int i7 = 0; i7 < tLRPC$StickerSetCovered.covers.size(); i7++) {
                                this.cache.put(this.totalItems + i7, tLRPC$StickerSetCovered.covers.get(i7));
                            }
                        } else {
                            this.cache.put(this.totalItems, tLRPC$StickerSetCovered.cover);
                            i = 1;
                        }
                        int i8 = 0;
                        while (true) {
                            i2 = this.stickersPerRow;
                            if (i8 >= i * i2) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + i8, tLRPC$StickerSetCovered);
                            i8++;
                        }
                        this.totalItems += i * i2;
                    }
                }
            } else if (StickersAlert.this.importingStickersPaths != null) {
                this.totalItems = StickersAlert.this.importingStickersPaths.size();
            } else {
                if (StickersAlert.this.stickerSet != null) {
                    i3 = StickersAlert.this.stickerSet.documents.size();
                }
                this.totalItems = i3;
            }
            super.notifyDataSetChanged();
        }

        public void notifyItemRemoved(int i) {
            if (StickersAlert.this.importingStickersPaths != null) {
                this.totalItems = StickersAlert.this.importingStickersPaths.size();
            }
            super.notifyItemRemoved(i);
        }

        public void updateColors() {
            if (StickersAlert.this.stickerSetCovereds != null) {
                int childCount = StickersAlert.this.gridView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = StickersAlert.this.gridView.getChildAt(i);
                    if (childAt instanceof FeaturedStickerSetInfoCell) {
                        ((FeaturedStickerSetInfoCell) childAt).updateColors();
                    }
                }
            }
        }

        public void getThemeDescriptions(List<ThemeDescription> list, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                FeaturedStickerSetInfoCell.createThemeDescriptions(list, StickersAlert.this.gridView, themeDescriptionDelegate);
            }
        }
    }

    public void onBackPressed() {
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().closeWithMenu();
        } else {
            super.onBackPressed();
        }
    }
}
