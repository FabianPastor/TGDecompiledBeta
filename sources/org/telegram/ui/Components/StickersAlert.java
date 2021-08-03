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
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
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
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.ContentPreviewViewer;

public class StickersAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public GridAdapter adapter;
    private List<ThemeDescription> animatingDescriptions;
    private String buttonTextColorKey;
    private int checkReqId;
    private Runnable checkRunnable;
    /* access modifiers changed from: private */
    public boolean clearsInputField;
    /* access modifiers changed from: private */
    public StickersAlertDelegate delegate;
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
    public int itemSize;
    private String lastCheckName;
    private boolean lastNameAvailable;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    private ActionBarMenuItem optionsButton;
    private Activity parentActivity;
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public TextView pickerBottomLayout;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate previewDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
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
            return StickersAlert.this.previewSendButton.getVisibility() == 0 && StickersAlert.this.importingStickers == null;
        }

        public long getDialogId() {
            if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
            }
            return 0;
        }
    };
    /* access modifiers changed from: private */
    public TextView previewSendButton;
    private View previewSendButtonShadow;
    private int reqId;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private TLRPC$Document selectedSticker;
    private SendMessagesHelper.ImportingSticker selectedStickerPath;
    private String setTitle;
    /* access modifiers changed from: private */
    public View[] shadow = new View[2];
    /* access modifiers changed from: private */
    public AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    /* access modifiers changed from: private */
    public boolean showEmoji;
    private boolean showTooltipWhenToggle = true;
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

    public interface StickersAlertDelegate {
        boolean canSchedule();

        boolean isInScheduleMode();

        void onStickerSelected(TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, boolean z2, int i);
    }

    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    static /* synthetic */ boolean lambda$init$9(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$showNameEnterAlert$20(DialogInterface dialogInterface, int i) {
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

    public StickersAlert(Context context, Object obj, TLObject tLObject) {
        super(context, false);
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
        this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getAttachedStickers, new RequestDelegate(obj, tLRPC$TL_messages_getAttachedStickers, new RequestDelegate(tLRPC$TL_messages_getAttachedStickers) {
            public final /* synthetic */ TLRPC$TL_messages_getAttachedStickers f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                StickersAlert.this.lambda$new$1$StickersAlert(this.f$1, tLObject, tLRPC$TL_error);
            }
        }) {
            public final /* synthetic */ Object f$1;
            public final /* synthetic */ TLRPC$TL_messages_getAttachedStickers f$2;
            public final /* synthetic */ RequestDelegate f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                StickersAlert.this.lambda$new$2$StickersAlert(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
            }
        });
        init(context);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$StickersAlert(TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, tLRPC$TL_messages_getAttachedStickers) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_messages_getAttachedStickers f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                StickersAlert.this.lambda$new$0$StickersAlert(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$StickersAlert(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers) {
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
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$StickersAlert(Object obj, TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers, RequestDelegate requestDelegate, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null || !FileRefController.isFileRefError(tLRPC$TL_error.text) || obj == null) {
            requestDelegate.run(tLObject, tLRPC$TL_error);
            return;
        }
        FileRefController.getInstance(this.currentAccount).requestReference(obj, tLRPC$TL_messages_getAttachedStickers, requestDelegate);
    }

    public StickersAlert(Context context, String str, ArrayList<Parcelable> arrayList, ArrayList<String> arrayList2) {
        super(context, false);
        this.parentActivity = (Activity) context;
        this.importingStickers = arrayList;
        this.importingSoftware = str;
        Utilities.globalQueue.postRunnable(new Runnable(arrayList, arrayList2) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                StickersAlert.this.lambda$new$4$StickersAlert(this.f$1, this.f$2);
            }
        });
        init(context);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$StickersAlert(ArrayList arrayList, ArrayList arrayList2) {
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
        AndroidUtilities.runOnUIThread(new Runnable(arrayList3, bool) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ Boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                StickersAlert.this.lambda$new$3$StickersAlert(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$StickersAlert(ArrayList arrayList, Boolean bool) {
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
        super(context, false);
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate(instance) {
                    public final /* synthetic */ MediaDataController f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        StickersAlert.this.lambda$loadStickerSet$6$StickersAlert(this.f$1, tLObject, tLRPC$TL_error);
                    }
                });
            } else {
                if (this.adapter != null) {
                    updateSendButton();
                    updateFields();
                    this.adapter.notifyDataSetChanged();
                }
                instance.preloadStickerSetThumb(this.stickerSet);
            }
        }
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSet;
        if (tLRPC$TL_messages_stickerSet != null) {
            this.showEmoji = !tLRPC$TL_messages_stickerSet.set.masks;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadStickerSet$6 */
    public /* synthetic */ void lambda$loadStickerSet$6$StickersAlert(MediaDataController mediaDataController, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, mediaDataController) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ MediaDataController f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                StickersAlert.this.lambda$loadStickerSet$5$StickersAlert(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadStickerSet$5 */
    public /* synthetic */ void lambda$loadStickerSet$5$StickersAlert(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, MediaDataController mediaDataController) {
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
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(intValue, access$600) {
                            public final /* synthetic */ int f$1;
                            public final /* synthetic */ int f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                StickersAlert.AnonymousClass2.this.lambda$createAnimator$0$StickersAlert$2(this.f$1, this.f$2, valueAnimator);
                            }
                        });
                        return ofFloat;
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$createAnimator$0 */
                    public /* synthetic */ void lambda$createAnimator$0$StickersAlert$2(int i, int i2, ValueAnimator valueAnimator) {
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
            mediaDataController.preloadStickerSetThumb(tLRPC$TL_messages_stickerSet);
            updateSendButton();
            updateFields();
            this.adapter.notifyDataSetChanged();
            return;
        }
        Toast.makeText(getContext(), LocaleController.getString("AddStickersNotFound", NUM), 0).show();
        dismiss();
    }

    private void init(Context context) {
        Context context2 = context;
        AnonymousClass3 r2 = new FrameLayout(context2) {
            private boolean fullHeight;
            private int lastNotifyWidth;
            private RectF rect = new RectF();

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
            public void onMeasure(int i, int i2) {
                int dp;
                int i3;
                int size = View.MeasureSpec.getSize(i2);
                boolean z = true;
                if (Build.VERSION.SDK_INT >= 21) {
                    boolean unused = StickersAlert.this.ignoreLayout = true;
                    setPadding(StickersAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, StickersAlert.this.backgroundPaddingLeft, 0);
                    boolean unused2 = StickersAlert.this.ignoreLayout = false;
                }
                int unused3 = StickersAlert.this.itemSize = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(36.0f)) / 5;
                if (StickersAlert.this.importingStickers != null) {
                    dp = AndroidUtilities.dp(96.0f) + (Math.max(3, (int) Math.ceil((double) (((float) StickersAlert.this.importingStickers.size()) / 5.0f))) * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop;
                    i3 = AndroidUtilities.statusBarHeight;
                } else if (StickersAlert.this.stickerSetCovereds != null) {
                    dp = AndroidUtilities.dp(56.0f) + (AndroidUtilities.dp(60.0f) * StickersAlert.this.stickerSetCovereds.size()) + (StickersAlert.this.adapter.stickersRowCount * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop;
                    i3 = AndroidUtilities.dp(24.0f);
                } else {
                    dp = AndroidUtilities.dp(96.0f) + (Math.max(3, StickersAlert.this.stickerSet != null ? (int) Math.ceil((double) (((float) StickersAlert.this.stickerSet.documents.size()) / 5.0f)) : 0) * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop;
                    i3 = AndroidUtilities.statusBarHeight;
                }
                int i4 = dp + i3;
                int i5 = size / 5;
                double d = (double) i5;
                Double.isNaN(d);
                int i6 = ((double) i4) < d * 3.2d ? 0 : i5 * 2;
                if (i6 != 0 && i4 < size) {
                    i6 -= size - i4;
                }
                if (i6 == 0) {
                    i6 = StickersAlert.this.backgroundPaddingTop;
                }
                if (StickersAlert.this.stickerSetCovereds != null) {
                    i6 += AndroidUtilities.dp(8.0f);
                }
                if (StickersAlert.this.gridView.getPaddingTop() != i6) {
                    boolean unused4 = StickersAlert.this.ignoreLayout = true;
                    StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0f), i6, AndroidUtilities.dp(10.0f), 0);
                    StickersAlert.this.emptyView.setPadding(0, i6, 0, 0);
                    boolean unused5 = StickersAlert.this.ignoreLayout = false;
                }
                if (i4 < size) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min(i4, size), NUM));
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

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:15:0x00b1  */
            /* JADX WARNING: Removed duplicated region for block: B:18:0x0144  */
            /* JADX WARNING: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
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
                    int r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                    if (r4 == 0) goto L_0x0100
                    android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r4.setColor(r5)
                    android.graphics.RectF r4 = r13.rect
                    org.telegram.ui.Components.StickersAlert r5 = org.telegram.ui.Components.StickersAlert.this
                    int r5 = r5.backgroundPaddingLeft
                    float r5 = (float) r5
                    org.telegram.ui.Components.StickersAlert r7 = org.telegram.ui.Components.StickersAlert.this
                    int r7 = r7.backgroundPaddingTop
                    int r7 = r7 + r1
                    float r7 = (float) r7
                    int r8 = r13.getMeasuredWidth()
                    org.telegram.ui.Components.StickersAlert r9 = org.telegram.ui.Components.StickersAlert.this
                    int r9 = r9.backgroundPaddingLeft
                    int r8 = r8 - r9
                    float r8 = (float) r8
                    org.telegram.ui.Components.StickersAlert r9 = org.telegram.ui.Components.StickersAlert.this
                    int r9 = r9.backgroundPaddingTop
                    int r9 = r9 + r1
                    r1 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    int r9 = r9 + r1
                    float r1 = (float) r9
                    r4.set(r5, r7, r8, r1)
                    android.graphics.RectF r1 = r13.rect
                    r4 = 1094713344(0x41400000, float:12.0)
                    int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    float r5 = (float) r5
                    float r5 = r5 * r3
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    float r4 = (float) r4
                    float r4 = r4 * r3
                    android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r14.drawRoundRect(r1, r5, r4, r3)
                L_0x0100:
                    r1 = 1108344832(0x42100000, float:36.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    android.graphics.RectF r3 = r13.rect
                    int r4 = r13.getMeasuredWidth()
                    int r4 = r4 - r1
                    int r4 = r4 / 2
                    float r4 = (float) r4
                    float r5 = (float) r0
                    int r7 = r13.getMeasuredWidth()
                    int r7 = r7 + r1
                    int r7 = r7 / 2
                    float r1 = (float) r7
                    r7 = 1082130432(0x40800000, float:4.0)
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r0 = r0 + r7
                    float r0 = (float) r0
                    r3.set(r4, r5, r1, r0)
                    android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    java.lang.String r1 = "key_sheet_scrollUp"
                    int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                    r0.setColor(r1)
                    android.graphics.RectF r0 = r13.rect
                    r1 = 1073741824(0x40000000, float:2.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r3 = (float) r3
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r1 = (float) r1
                    android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r14.drawRoundRect(r0, r3, r1, r4)
                    if (r6 <= 0) goto L_0x018e
                    int r0 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r1 = 255(0xff, float:3.57E-43)
                    int r2 = android.graphics.Color.red(r0)
                    float r2 = (float) r2
                    r3 = 1061997773(0x3f4ccccd, float:0.8)
                    float r2 = r2 * r3
                    int r2 = (int) r2
                    int r4 = android.graphics.Color.green(r0)
                    float r4 = (float) r4
                    float r4 = r4 * r3
                    int r4 = (int) r4
                    int r0 = android.graphics.Color.blue(r0)
                    float r0 = (float) r0
                    float r0 = r0 * r3
                    int r0 = (int) r0
                    int r0 = android.graphics.Color.argb(r1, r2, r4, r0)
                    android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r1.setColor(r0)
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
                L_0x018e:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.AnonymousClass3.onDraw(android.graphics.Canvas):void");
            }
        };
        this.containerView = r2;
        r2.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(48.0f);
        this.shadow[0] = new View(context2);
        this.shadow[0].setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(1);
        this.containerView.addView(this.shadow[0], layoutParams);
        AnonymousClass4 r22 = new RecyclerListView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                boolean onInterceptTouchEvent = ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickersAlert.this.gridView, 0, StickersAlert.this.previewDelegate);
                if (super.onInterceptTouchEvent(motionEvent) || onInterceptTouchEvent) {
                    return true;
                }
                return false;
            }

            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.gridView = r22;
        r22.setTag(14);
        RecyclerListView recyclerListView = this.gridView;
        AnonymousClass5 r6 = new GridLayoutManager(getContext(), 5) {
            /* access modifiers changed from: protected */
            public boolean isLayoutRTL() {
                return StickersAlert.this.stickerSetCovereds != null && LocaleController.isRTL;
            }
        };
        this.layoutManager = r6;
        recyclerListView.setLayoutManager(r6);
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
        this.gridView.addItemDecoration(new RecyclerView.ItemDecoration() {
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
        this.gridView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.gridView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return StickersAlert.this.lambda$init$7$StickersAlert(view, motionEvent);
            }
        });
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                StickersAlert.this.updateLayout();
            }
        });
        $$Lambda$StickersAlert$6tkUH8gce8OgMl1b5uYLB5HBmGQ r23 = new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                StickersAlert.this.lambda$init$8$StickersAlert(view, i);
            }
        };
        this.stickersOnItemClickListener = r23;
        this.gridView.setOnItemClickListener((RecyclerListView.OnItemClickListener) r23);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 48.0f));
        AnonymousClass9 r24 = new FrameLayout(context2) {
            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.emptyView = r24;
        this.containerView.addView(r24, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.gridView.setEmptyView(this.emptyView);
        this.emptyView.setOnTouchListener($$Lambda$StickersAlert$U2fzU6MGFyYds8j1PrPauRe8d8o.INSTANCE);
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setLinkTextColor(Theme.getColor("dialogTextLink"));
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.containerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, (ActionBarMenu) null, 0, Theme.getColor("key_sheet_other"));
        this.optionsButton = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        this.optionsButton.setSubMenuOpenSide(2);
        this.optionsButton.setIcon(NUM);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("player_actionBarSelector"), 1));
        this.containerView.addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 5.0f, 5.0f, 0.0f));
        this.optionsButton.addSubItem(1, NUM, LocaleController.getString("StickersShare", NUM));
        this.optionsButton.addSubItem(2, NUM, LocaleController.getString("CopyLink", NUM));
        this.optionsButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickersAlert.this.lambda$init$10$StickersAlert(view);
            }
        });
        this.optionsButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
            public final void onItemClick(int i) {
                StickersAlert.this.onSubItemClick(i);
            }
        });
        this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.optionsButton.setVisibility(this.inputStickerSet != null ? 0 : 8);
        this.emptyView.addView(new RadialProgressView(context2), LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
        this.shadow[1] = new View(context2);
        this.shadow[1].setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.shadow[1], layoutParams2);
        TextView textView2 = new TextView(context2);
        this.pickerBottomLayout = textView2;
        textView2.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("dialogBackground"), Theme.getColor("listSelectorSDK21")));
        TextView textView3 = this.pickerBottomLayout;
        this.buttonTextColorKey = "dialogTextBlue2";
        textView3.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.pickerBottomLayout.setTextSize(1, 14.0f);
        this.pickerBottomLayout.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pickerBottomLayout.setGravity(17);
        this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.stickerPreviewLayout = frameLayout;
        frameLayout.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.stickerPreviewLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickersAlert.this.lambda$init$11$StickersAlert(view);
            }
        });
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
        this.previewSendButton.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.previewSendButton.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("dialogBackground"), Theme.getColor("listSelectorSDK21")));
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.previewSendButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickersAlert.this.lambda$init$12$StickersAlert(view);
            }
        });
        FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams3.bottomMargin = AndroidUtilities.dp(48.0f);
        View view = new View(context2);
        this.previewSendButtonShadow = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, layoutParams3);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        if (this.importingStickers != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
        }
        updateFields();
        updateSendButton();
        updateColors();
        this.adapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$7 */
    public /* synthetic */ boolean lambda$init$7$StickersAlert(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gridView, 0, this.stickersOnItemClickListener, this.previewDelegate);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$8 */
    public /* synthetic */ void lambda$init$8$StickersAlert(View view, int i) {
        boolean z;
        int i2 = i;
        if (this.stickerSetCovereds != null) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) this.adapter.positionsToSets.get(i2);
            if (tLRPC$StickerSetCovered != null) {
                dismiss();
                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                TLRPC$StickerSet tLRPC$StickerSet = tLRPC$StickerSetCovered.set;
                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                new StickersAlert(this.parentActivity, this.parentFragment, tLRPC$TL_inputStickerSetID, (TLRPC$TL_messages_stickerSet) null, (StickersAlertDelegate) null).show();
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
    /* renamed from: lambda$init$10 */
    public /* synthetic */ void lambda$init$10$StickersAlert(View view) {
        this.optionsButton.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$11 */
    public /* synthetic */ void lambda$init$11$StickersAlert(View view) {
        hidePreview();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$12 */
    public /* synthetic */ void lambda$init$12$StickersAlert(View view) {
        if (this.importingStickersPaths != null) {
            removeSticker(this.selectedStickerPath);
            hidePreview();
            this.selectedStickerPath = null;
            return;
        }
        this.delegate.onStickerSelected(this.selectedSticker, (String) null, this.stickerSet, (MessageObject.SendAnimationData) null, this.clearsInputField, true, 0);
        dismiss();
    }

    private void updateSendButton() {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        Point point = AndroidUtilities.displaySize;
        int min = (int) (((float) (Math.min(point.x, point.y) / 2)) / AndroidUtilities.density);
        if (this.importingStickers != null) {
            this.previewSendButton.setText(LocaleController.getString("ImportStickersRemove", NUM).toUpperCase());
            this.previewSendButton.setTextColor(Theme.getColor("dialogTextRed"));
            float f = (float) min;
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.previewSendButton.setVisibility(0);
            this.previewSendButtonShadow.setVisibility(0);
        } else if (this.delegate == null || ((tLRPC$TL_messages_stickerSet = this.stickerSet) != null && tLRPC$TL_messages_stickerSet.set.masks)) {
            this.previewSendButton.setText(LocaleController.getString("Close", NUM).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
            this.previewSendButton.setVisibility(8);
            this.previewSendButtonShadow.setVisibility(8);
        } else {
            this.previewSendButton.setText(LocaleController.getString("SendSticker", NUM).toUpperCase());
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

    /* access modifiers changed from: private */
    public void onSubItemClick(int i) {
        BaseFragment baseFragment;
        if (this.stickerSet != null) {
            String str = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/" + this.stickerSet.set.short_name;
            if (i == 1) {
                Context context = this.parentActivity;
                if (context == null && (baseFragment = this.parentFragment) != null) {
                    context = baseFragment.getParentActivity();
                }
                if (context == null) {
                    context = getContext();
                }
                ShareAlert shareAlert = new ShareAlert(context, (ArrayList<MessageObject>) null, str, false, str, false);
                BaseFragment baseFragment2 = this.parentFragment;
                if (baseFragment2 != null) {
                    baseFragment2.showDialog(shareAlert);
                } else {
                    shareAlert.show();
                }
            } else if (i == 2) {
                try {
                    AndroidUtilities.addToClipboard(str);
                    BulletinFactory.of((FrameLayout) this.containerView).createCopyLinkBulletin().show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0084, code lost:
        r1 = r1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0103  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFields() {
        /*
            r12 = this;
            android.widget.TextView r0 = r12.titleTextView
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r12.stickerSet
            java.lang.String r2 = "dialogTextBlue2"
            r3 = 0
            java.lang.String r4 = "Stickers"
            r5 = 0
            r6 = 1
            if (r1 == 0) goto L_0x014e
            java.util.regex.Pattern r0 = r12.urlPattern     // Catch:{ Exception -> 0x007f }
            if (r0 != 0) goto L_0x001c
            java.lang.String r0 = "@[a-zA-Z\\d_]{1,32}"
            java.util.regex.Pattern r0 = java.util.regex.Pattern.compile(r0)     // Catch:{ Exception -> 0x007f }
            r12.urlPattern = r0     // Catch:{ Exception -> 0x007f }
        L_0x001c:
            java.util.regex.Pattern r0 = r12.urlPattern     // Catch:{ Exception -> 0x007f }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r12.stickerSet     // Catch:{ Exception -> 0x007f }
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set     // Catch:{ Exception -> 0x007f }
            java.lang.String r1 = r1.title     // Catch:{ Exception -> 0x007f }
            java.util.regex.Matcher r0 = r0.matcher(r1)     // Catch:{ Exception -> 0x007f }
            r1 = r3
        L_0x0029:
            boolean r7 = r0.find()     // Catch:{ Exception -> 0x007c }
            if (r7 == 0) goto L_0x0084
            if (r1 != 0) goto L_0x004b
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = r12.stickerSet     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$StickerSet r8 = r8.set     // Catch:{ Exception -> 0x007c }
            java.lang.String r8 = r8.title     // Catch:{ Exception -> 0x007c }
            r7.<init>(r8)     // Catch:{ Exception -> 0x007c }
            android.widget.TextView r1 = r12.titleTextView     // Catch:{ Exception -> 0x0048 }
            org.telegram.ui.Components.StickersAlert$LinkMovementMethodMy r8 = new org.telegram.ui.Components.StickersAlert$LinkMovementMethodMy     // Catch:{ Exception -> 0x0048 }
            r8.<init>()     // Catch:{ Exception -> 0x0048 }
            r1.setMovementMethod(r8)     // Catch:{ Exception -> 0x0048 }
            r1 = r7
            goto L_0x004b
        L_0x0048:
            r0 = move-exception
            r3 = r7
            goto L_0x0080
        L_0x004b:
            int r7 = r0.start()     // Catch:{ Exception -> 0x007c }
            int r8 = r0.end()     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r9 = r12.stickerSet     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$StickerSet r9 = r9.set     // Catch:{ Exception -> 0x007c }
            java.lang.String r9 = r9.title     // Catch:{ Exception -> 0x007c }
            char r9 = r9.charAt(r7)     // Catch:{ Exception -> 0x007c }
            r10 = 64
            if (r9 == r10) goto L_0x0063
            int r7 = r7 + 1
        L_0x0063:
            org.telegram.ui.Components.StickersAlert$10 r9 = new org.telegram.ui.Components.StickersAlert$10     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r10 = r12.stickerSet     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$StickerSet r10 = r10.set     // Catch:{ Exception -> 0x007c }
            java.lang.String r10 = r10.title     // Catch:{ Exception -> 0x007c }
            int r11 = r7 + 1
            java.lang.CharSequence r10 = r10.subSequence(r11, r8)     // Catch:{ Exception -> 0x007c }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x007c }
            r9.<init>(r10)     // Catch:{ Exception -> 0x007c }
            r1.setSpan(r9, r7, r8, r5)     // Catch:{ Exception -> 0x007c }
            goto L_0x0029
        L_0x007c:
            r0 = move-exception
            r3 = r1
            goto L_0x0080
        L_0x007f:
            r0 = move-exception
        L_0x0080:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = r3
        L_0x0084:
            android.widget.TextView r0 = r12.titleTextView
            if (r1 == 0) goto L_0x0089
            goto L_0x008f
        L_0x0089:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            java.lang.String r1 = r1.title
        L_0x008f:
            r0.setText(r1)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r0 = r0.set
            java.lang.String r1 = "MasksCount"
            if (r0 == 0) goto L_0x0103
            int r0 = r12.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.set
            long r7 = r3.id
            boolean r0 = r0.isStickerPackInstalled((long) r7)
            if (r0 != 0) goto L_0x00ad
            goto L_0x0103
        L_0x00ad:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r2 = r0.set
            boolean r2 = r2.masks
            r3 = 2131627197(0x7f0e0cbd, float:1.8881652E38)
            java.lang.String r7 = "RemoveStickersCount"
            if (r2 == 0) goto L_0x00d1
            java.lang.Object[] r2 = new java.lang.Object[r6]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r1, r0)
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r3, r2)
            java.lang.String r0 = r0.toUpperCase()
            goto L_0x00e7
        L_0x00d1:
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r4, r0)
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r3, r1)
            java.lang.String r0 = r0.toUpperCase()
        L_0x00e7:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            boolean r1 = r1.official
            java.lang.String r2 = "dialogTextRed"
            if (r1 == 0) goto L_0x00fa
            org.telegram.ui.Components.-$$Lambda$StickersAlert$A1hIlli8L3tdnQw0uSxtdKcC7LI r1 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$A1hIlli8L3tdnQw0uSxtdKcC7LI
            r1.<init>()
            r12.setButton(r1, r0, r2)
            goto L_0x0147
        L_0x00fa:
            org.telegram.ui.Components.-$$Lambda$StickersAlert$WGj0GKU5g8gyUROewrFNTuL7Eks r1 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$WGj0GKU5g8gyUROewrFNTuL7Eks
            r1.<init>()
            r12.setButton(r1, r0, r2)
            goto L_0x0147
        L_0x0103:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r12.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r3 = r0.set
            r7 = 2131624220(0x7f0e011c, float:1.8875614E38)
            java.lang.String r8 = "AddStickersCount"
            if (r3 == 0) goto L_0x0129
            boolean r3 = r3.masks
            if (r3 == 0) goto L_0x0129
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r1, r0)
            r3[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r3)
            java.lang.String r0 = r0.toUpperCase()
            goto L_0x013f
        L_0x0129:
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r4, r0)
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            java.lang.String r0 = r0.toUpperCase()
        L_0x013f:
            org.telegram.ui.Components.-$$Lambda$StickersAlert$cLHS20lbkJb9L7dPEhhXIMv4dQ4 r1 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$cLHS20lbkJb9L7dPEhhXIMv4dQ4
            r1.<init>()
            r12.setButton(r1, r0, r2)
        L_0x0147:
            org.telegram.ui.Components.StickersAlert$GridAdapter r0 = r12.adapter
            r0.notifyDataSetChanged()
            goto L_0x01cc
        L_0x014e:
            java.util.ArrayList<android.os.Parcelable> r1 = r12.importingStickers
            if (r1 == 0) goto L_0x01b7
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$ImportingSticker> r7 = r12.importingStickersPaths
            if (r7 == 0) goto L_0x015b
            int r1 = r7.size()
            goto L_0x015f
        L_0x015b:
            int r1 = r1.size()
        L_0x015f:
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r4, r1)
            r0.setText(r1)
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r0 = r12.uploadImportStickers
            if (r0 == 0) goto L_0x0189
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0171
            goto L_0x0189
        L_0x0171:
            r0 = 2131625853(0x7f0e077d, float:1.8878926E38)
            java.lang.String r1 = "ImportStickersProcessing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.String r0 = r0.toUpperCase()
            java.lang.String r1 = "dialogTextGray2"
            r12.setButton(r3, r0, r1)
            android.widget.TextView r0 = r12.pickerBottomLayout
            r0.setEnabled(r5)
            goto L_0x01cc
        L_0x0189:
            org.telegram.ui.Components.-$$Lambda$StickersAlert$ekNx1b09uSe2umZqR4EaIlPaJH0 r0 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$ekNx1b09uSe2umZqR4EaIlPaJH0
            r0.<init>()
            r1 = 2131625840(0x7f0e0770, float:1.88789E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$ImportingSticker> r7 = r12.importingStickersPaths
            if (r7 == 0) goto L_0x0198
            goto L_0x019a
        L_0x0198:
            java.util.ArrayList<android.os.Parcelable> r7 = r12.importingStickers
        L_0x019a:
            int r7 = r7.size()
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r7)
            r3[r5] = r4
            java.lang.String r4 = "ImportStickers"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            java.lang.String r1 = r1.toUpperCase()
            r12.setButton(r0, r1, r2)
            android.widget.TextView r0 = r12.pickerBottomLayout
            r0.setEnabled(r6)
            goto L_0x01cc
        L_0x01b7:
            r0 = 2131624947(0x7f0e03f3, float:1.8877088E38)
            java.lang.String r1 = "Close"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.String r0 = r0.toUpperCase()
            org.telegram.ui.Components.-$$Lambda$StickersAlert$NYfa2lp5kamtx6KM7_-7AwCFf4g r1 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$NYfa2lp5kamtx6KM7_-7AwCFf4g
            r1.<init>()
            r12.setButton(r1, r0, r2)
        L_0x01cc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.updateFields():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateFields$15 */
    public /* synthetic */ void lambda$updateFields$15$StickersAlert(View view) {
        dismiss();
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetInstalled();
        }
        if (this.inputStickerSet != null && !MediaDataController.getInstance(this.currentAccount).cancelRemovingStickerSet(this.inputStickerSet.id)) {
            TLRPC$TL_messages_installStickerSet tLRPC$TL_messages_installStickerSet = new TLRPC$TL_messages_installStickerSet();
            tLRPC$TL_messages_installStickerSet.stickerset = this.inputStickerSet;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_installStickerSet, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    StickersAlert.this.lambda$updateFields$14$StickersAlert(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateFields$14 */
    public /* synthetic */ void lambda$updateFields$14$StickersAlert(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                StickersAlert.this.lambda$updateFields$13$StickersAlert(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateFields$13 */
    public /* synthetic */ void lambda$updateFields$13$StickersAlert(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        boolean z = this.stickerSet.set.masks;
        if (tLRPC$TL_error == null) {
            try {
                if (this.showTooltipWhenToggle) {
                    Bulletin.make(this.parentFragment, (Bulletin.Layout) new StickerSetBulletinLayout(this.pickerBottomLayout.getContext(), this.stickerSet, 2), 1500).show();
                }
                if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
                    MediaDataController.getInstance(this.currentAccount).processStickerSetInstallResultArchive(this.parentFragment, true, z, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            Toast.makeText(getContext(), LocaleController.getString("ErrorOccurred", NUM), 0).show();
        }
        MediaDataController.getInstance(this.currentAccount).loadStickers(z ? 1 : 0, false, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateFields$16 */
    public /* synthetic */ void lambda$updateFields$16$StickersAlert(View view) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), this.stickerSet, 1, this.parentFragment, true, this.showTooltipWhenToggle);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateFields$17 */
    public /* synthetic */ void lambda$updateFields$17$StickersAlert(View view) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), this.stickerSet, 0, this.parentFragment, true, this.showTooltipWhenToggle);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateFields$18 */
    public /* synthetic */ void lambda$updateFields$18$StickersAlert(View view) {
        showNameEnterAlert();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateFields$19 */
    public /* synthetic */ void lambda$updateFields$19$StickersAlert(View view) {
        dismiss();
    }

    private void showNameEnterAlert() {
        Context context = getContext();
        final int[] iArr = {0};
        FrameLayout frameLayout = new FrameLayout(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("ImportStickersEnterName", NUM));
        builder.setPositiveButton(LocaleController.getString("Next", NUM), $$Lambda$StickersAlert$jhjBFLXHowefmVI8fpPTvqlBZgY.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
        final TextView textView = new TextView(context);
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 16.0f);
        textView2.setTextColor(Theme.getColor("dialogTextHint"));
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
        editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(context, true));
        editTextBoldCursor.setTextSize(1, 16.0f);
        editTextBoldCursor.setTextColor(Theme.getColor("dialogTextBlack"));
        editTextBoldCursor.setMaxLines(1);
        editTextBoldCursor.setLines(1);
        editTextBoldCursor.setInputType(16385);
        editTextBoldCursor.setGravity(51);
        editTextBoldCursor.setSingleLine(true);
        editTextBoldCursor.setImeOptions(5);
        editTextBoldCursor.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
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
        editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return StickersAlert.lambda$showNameEnterAlert$21(AlertDialog.Builder.this, textView, i, keyEvent);
            }
        });
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
            }
        });
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ImportStickersEnterNameInfo", NUM)));
        textView.setTextSize(1, 14.0f);
        textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
        textView.setTextColor(Theme.getColor("dialogTextGray2"));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        AlertDialog create = builder.create();
        create.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        StickersAlert.lambda$showNameEnterAlert$23(EditTextBoldCursor.this);
                    }
                });
            }
        });
        create.show();
        editTextBoldCursor.requestFocus();
        create.getButton(-1).setOnClickListener(new View.OnClickListener(iArr, editTextBoldCursor, textView, textView2, builder) {
            public final /* synthetic */ int[] f$1;
            public final /* synthetic */ EditTextBoldCursor f$2;
            public final /* synthetic */ TextView f$3;
            public final /* synthetic */ TextView f$4;
            public final /* synthetic */ AlertDialog.Builder f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void onClick(View view) {
                StickersAlert.this.lambda$showNameEnterAlert$28$StickersAlert(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
            }
        });
    }

    static /* synthetic */ boolean lambda$showNameEnterAlert$21(AlertDialog.Builder builder, TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        builder.create().getButton(-1).callOnClick();
        return true;
    }

    static /* synthetic */ void lambda$showNameEnterAlert$23(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showNameEnterAlert$28 */
    public /* synthetic */ void lambda$showNameEnterAlert$28$StickersAlert(int[] iArr, EditTextBoldCursor editTextBoldCursor, TextView textView, TextView textView2, AlertDialog.Builder builder, View view) {
        if (iArr[0] != 1) {
            if (iArr[0] == 0) {
                iArr[0] = 1;
                TLRPC$TL_stickers_suggestShortName tLRPC$TL_stickers_suggestShortName = new TLRPC$TL_stickers_suggestShortName();
                String obj = editTextBoldCursor.getText().toString();
                this.setTitle = obj;
                tLRPC$TL_stickers_suggestShortName.title = obj;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_stickers_suggestShortName, new RequestDelegate(editTextBoldCursor, textView, textView2, iArr) {
                    public final /* synthetic */ EditTextBoldCursor f$1;
                    public final /* synthetic */ TextView f$2;
                    public final /* synthetic */ TextView f$3;
                    public final /* synthetic */ int[] f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        StickersAlert.this.lambda$showNameEnterAlert$26$StickersAlert(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                    }
                });
            } else if (iArr[0] == 2) {
                iArr[0] = 3;
                if (!this.lastNameAvailable) {
                    AndroidUtilities.shakeView(editTextBoldCursor, 2.0f, 0);
                    editTextBoldCursor.performHapticFeedback(3, 2);
                }
                AndroidUtilities.hideKeyboard(editTextBoldCursor);
                SendMessagesHelper.getInstance(this.currentAccount).prepareImportStickers(this.setTitle, this.lastCheckName, this.importingSoftware, this.importingStickersPaths, new MessagesStorage.StringCallback() {
                    public final void run(String str) {
                        StickersAlert.this.lambda$showNameEnterAlert$27$StickersAlert(str);
                    }
                });
                builder.getDismissRunnable().run();
                dismiss();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showNameEnterAlert$26 */
    public /* synthetic */ void lambda$showNameEnterAlert$26$StickersAlert(EditTextBoldCursor editTextBoldCursor, TextView textView, TextView textView2, int[] iArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, editTextBoldCursor, textView, textView2, iArr) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ EditTextBoldCursor f$2;
            public final /* synthetic */ TextView f$3;
            public final /* synthetic */ TextView f$4;
            public final /* synthetic */ int[] f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                StickersAlert.this.lambda$showNameEnterAlert$25$StickersAlert(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showNameEnterAlert$25 */
    public /* synthetic */ void lambda$showNameEnterAlert$25$StickersAlert(TLObject tLObject, EditTextBoldCursor editTextBoldCursor, TextView textView, TextView textView2, int[] iArr) {
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
    /* renamed from: lambda$showNameEnterAlert$27 */
    public /* synthetic */ void lambda$showNameEnterAlert$27$StickersAlert(String str) {
        new ImportingAlert(getContext(), this.lastCheckName, (ChatActivity) null).show();
    }

    /* access modifiers changed from: private */
    public void checkUrlAvailable(TextView textView, String str, boolean z) {
        if (z) {
            textView.setText(LocaleController.getString("ImportStickersLinkAvailable", NUM));
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
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
            textView.setText(LocaleController.getString("ImportStickersEnterUrlInfo", NUM));
            textView.setTextColor(Theme.getColor("dialogTextGray2"));
            return;
        }
        this.lastNameAvailable = false;
        if (str != null) {
            if (str.startsWith("_") || str.endsWith("_")) {
                textView.setText(LocaleController.getString("ImportStickersLinkInvalid", NUM));
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                return;
            }
            int length = str.length();
            for (int i = 0; i < length; i++) {
                char charAt = str.charAt(i);
                if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    textView.setText(LocaleController.getString("ImportStickersEnterUrlInfo", NUM));
                    textView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                    return;
                }
            }
        }
        if (str == null || str.length() < 5) {
            textView.setText(LocaleController.getString("ImportStickersLinkInvalidShort", NUM));
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
        } else if (str.length() > 32) {
            textView.setText(LocaleController.getString("ImportStickersLinkInvalidLong", NUM));
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
        } else {
            textView.setText(LocaleController.getString("ImportStickersLinkChecking", NUM));
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
            this.lastCheckName = str;
            $$Lambda$StickersAlert$m3CJ98f8mkvar_z2oVEW0Xquakw r9 = new Runnable(str, textView) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ TextView f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    StickersAlert.this.lambda$checkUrlAvailable$31$StickersAlert(this.f$1, this.f$2);
                }
            };
            this.checkRunnable = r9;
            AndroidUtilities.runOnUIThread(r9, 300);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUrlAvailable$31 */
    public /* synthetic */ void lambda$checkUrlAvailable$31$StickersAlert(String str, TextView textView) {
        TLRPC$TL_stickers_checkShortName tLRPC$TL_stickers_checkShortName = new TLRPC$TL_stickers_checkShortName();
        tLRPC$TL_stickers_checkShortName.short_name = str;
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_stickers_checkShortName, new RequestDelegate(str, textView) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ TextView f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                StickersAlert.this.lambda$checkUrlAvailable$30$StickersAlert(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUrlAvailable$30 */
    public /* synthetic */ void lambda$checkUrlAvailable$30$StickersAlert(String str, TextView textView, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, tLRPC$TL_error, tLObject, textView) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ TextView f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                StickersAlert.this.lambda$checkUrlAvailable$29$StickersAlert(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUrlAvailable$29 */
    public /* synthetic */ void lambda$checkUrlAvailable$29$StickersAlert(String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TextView textView) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 != null && str2.equals(str)) {
            if (tLRPC$TL_error != null || !(tLObject instanceof TLRPC$TL_boolTrue)) {
                textView.setText(LocaleController.getString("ImportStickersLinkTaken", NUM));
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                this.lastNameAvailable = false;
                return;
            }
            textView.setText(LocaleController.getString("ImportStickersLinkAvailable", NUM));
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
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

    public void dismiss() {
        super.dismiss();
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

            public int getBottomOffset() {
                if (StickersAlert.this.pickerBottomLayout != null) {
                    return StickersAlert.this.pickerBottomLayout.getHeight();
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

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003d, code lost:
        r4 = r5[0];
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r3, int r4, java.lang.Object... r5) {
        /*
            r2 = this;
            int r4 = org.telegram.messenger.NotificationCenter.emojiLoaded
            r0 = 0
            if (r3 != r4) goto L_0x0034
            org.telegram.ui.Components.RecyclerListView r3 = r2.gridView
            if (r3 == 0) goto L_0x001b
            int r3 = r3.getChildCount()
        L_0x000d:
            if (r0 >= r3) goto L_0x001b
            org.telegram.ui.Components.RecyclerListView r4 = r2.gridView
            android.view.View r4 = r4.getChildAt(r0)
            r4.invalidate()
            int r0 = r0 + 1
            goto L_0x000d
        L_0x001b:
            org.telegram.ui.ContentPreviewViewer r3 = org.telegram.ui.ContentPreviewViewer.getInstance()
            boolean r3 = r3.isVisible()
            if (r3 == 0) goto L_0x002c
            org.telegram.ui.ContentPreviewViewer r3 = org.telegram.ui.ContentPreviewViewer.getInstance()
            r3.close()
        L_0x002c:
            org.telegram.ui.ContentPreviewViewer r3 = org.telegram.ui.ContentPreviewViewer.getInstance()
            r3.reset()
            goto L_0x007c
        L_0x0034:
            int r4 = org.telegram.messenger.NotificationCenter.fileUploaded
            if (r3 != r4) goto L_0x0059
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r3 = r2.uploadImportStickers
            if (r3 != 0) goto L_0x003d
            return
        L_0x003d:
            r4 = r5[r0]
            java.lang.String r4 = (java.lang.String) r4
            java.lang.Object r3 = r3.get(r4)
            org.telegram.messenger.SendMessagesHelper$ImportingSticker r3 = (org.telegram.messenger.SendMessagesHelper.ImportingSticker) r3
            if (r3 == 0) goto L_0x007c
            int r0 = r2.currentAccount
            r1 = 1
            r5 = r5[r1]
            org.telegram.tgnet.TLRPC$InputFile r5 = (org.telegram.tgnet.TLRPC$InputFile) r5
            org.telegram.ui.Components.-$$Lambda$StickersAlert$9CLASSNAME-3jT9rPDvH81RRXvCMhUbZk r1 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$9CLASSNAME-3jT9rPDvH81RRXvCMhUbZk
            r1.<init>(r4, r3)
            r3.uploadMedia(r0, r5, r1)
            goto L_0x007c
        L_0x0059:
            int r4 = org.telegram.messenger.NotificationCenter.fileUploadFailed
            if (r3 != r4) goto L_0x007c
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r3 = r2.uploadImportStickers
            if (r3 != 0) goto L_0x0062
            return
        L_0x0062:
            r4 = r5[r0]
            java.lang.String r4 = (java.lang.String) r4
            java.lang.Object r3 = r3.remove(r4)
            org.telegram.messenger.SendMessagesHelper$ImportingSticker r3 = (org.telegram.messenger.SendMessagesHelper.ImportingSticker) r3
            if (r3 == 0) goto L_0x0071
            r2.removeSticker(r3)
        L_0x0071:
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r3 = r2.uploadImportStickers
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x007c
            r2.updateFields()
        L_0x007c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$32 */
    public /* synthetic */ void lambda$didReceivedNotification$32$StickersAlert(String str, SendMessagesHelper.ImportingSticker importingSticker) {
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
        TextView textView = this.pickerBottomLayout;
        this.buttonTextColorKey = str2;
        textView.setTextColor(Theme.getColor(str2));
        this.pickerBottomLayout.setText(str.toUpperCase());
        this.pickerBottomLayout.setOnClickListener(onClickListener);
    }

    public void setShowTooltipWhenToggle(boolean z) {
        this.showTooltipWhenToggle = z;
    }

    public void updateColors() {
        updateColors(false);
    }

    public void updateColors(boolean z) {
        this.adapter.updateColors();
        this.titleTextView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
        this.stickerPreviewLayout.setBackgroundColor(Theme.getColor("dialogBackground") & -NUM);
        this.optionsButton.setIconColor(Theme.getColor("key_sheet_other"));
        this.optionsButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        this.optionsButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true);
        this.optionsButton.setPopupItemsSelectorColor(Theme.getColor("dialogButtonSelector"));
        this.optionsButton.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
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
                themeDescription.setColor(Theme.getColor(themeDescription.getCurrentKey()), false, false);
            }
        }
        if (!Theme.isAnimatingColor() && this.animatingDescriptions != null) {
            this.animatingDescriptions = null;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$x_I0i8843__VMVpctgNXSVapMAQ r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                StickersAlert.this.updateColors();
            }
        };
        arrayList.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.shadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        this.adapter.getThemeDescriptions(arrayList, r10);
        arrayList.add(new ThemeDescription(this.shadow[0], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        arrayList.add(new ThemeDescription(this.shadow[1], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        arrayList.add(new ThemeDescription(this.gridView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_LINKCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextLink"));
        arrayList.add(new ThemeDescription(this.optionsButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSelector"));
        arrayList.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.buttonTextColorKey));
        arrayList.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue2"));
        arrayList.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.previewSendButtonShadow, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        $$Lambda$x_I0i8843__VMVpctgNXSVapMAQ r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "dialogLinkSelection"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "dialogBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "key_sheet_other"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "actionBarDefaultSubmenuBackground"));
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

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            FrameLayout frameLayout2;
            if (i != 0) {
                if (i == 1) {
                    frameLayout = new EmptyCell(this.context);
                } else if (i != 2) {
                    frameLayout = null;
                } else {
                    frameLayout2 = new FeaturedStickerSetInfoCell(this.context, 8, true, false);
                }
                return new RecyclerListView.Holder(frameLayout);
            }
            AnonymousClass1 r5 = new StickerEmojiCell(this.context, false) {
                public void onMeasure(int i, int i2) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(StickersAlert.this.itemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                }
            };
            r5.getImageView().setLayerNum(7);
            frameLayout2 = r5;
            frameLayout = frameLayout2;
            return new RecyclerListView.Holder(frameLayout);
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
}
