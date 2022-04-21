package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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
import java.util.regex.Matcher;
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
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
    private TLRPC.InputStickerSet inputStickerSet;
    private StickersAlertInstallDelegate installDelegate;
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
    public TextView pickerBottomLayout;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate previewDelegate;
    /* access modifiers changed from: private */
    public TextView previewSendButton;
    private View previewSendButtonShadow;
    private int reqId;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private TLRPC.Document selectedSticker;
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
    public TLRPC.TL_messages_stickerSet stickerSet;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.StickerSetCovered> stickerSetCovereds;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    private HashMap<String, SendMessagesHelper.ImportingSticker> uploadImportStickers;
    private Pattern urlPattern;

    public interface StickersAlertDelegate {
        boolean canSchedule();

        boolean isInScheduleMode();

        void onStickerSelected(TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, boolean z2, int i);
    }

    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public StickersAlert(Context context, Object parentObject, TLObject object, Theme.ResourcesProvider resourcesProvider) {
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

            public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
            }

            public void sendSticker(TLRPC.Document sticker, String query, Object parent, boolean notify, int scheduleDate) {
                if (StickersAlert.this.delegate != null) {
                    StickersAlert.this.delegate.onStickerSelected(sticker, query, parent, (MessageObject.SendAnimationData) null, StickersAlert.this.clearsInputField, notify, scheduleDate);
                    StickersAlert.this.dismiss();
                }
            }

            public boolean canSchedule() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
            }

            public boolean isInScheduleMode() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
            }

            public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
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

            public boolean needOpen() {
                return false;
            }

            public long getDialogId() {
                if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                    return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
                }
                return 0;
            }
        };
        this.resourcesProvider = resourcesProvider;
        this.parentActivity = (Activity) context;
        TLRPC.TL_messages_getAttachedStickers req = new TLRPC.TL_messages_getAttachedStickers();
        if (object instanceof TLRPC.Photo) {
            TLRPC.Photo photo = (TLRPC.Photo) object;
            TLRPC.TL_inputStickeredMediaPhoto inputStickeredMediaPhoto = new TLRPC.TL_inputStickeredMediaPhoto();
            inputStickeredMediaPhoto.id = new TLRPC.TL_inputPhoto();
            inputStickeredMediaPhoto.id.id = photo.id;
            inputStickeredMediaPhoto.id.access_hash = photo.access_hash;
            inputStickeredMediaPhoto.id.file_reference = photo.file_reference;
            if (inputStickeredMediaPhoto.id.file_reference == null) {
                inputStickeredMediaPhoto.id.file_reference = new byte[0];
            }
            req.media = inputStickeredMediaPhoto;
        } else if (object instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) object;
            TLRPC.TL_inputStickeredMediaDocument inputStickeredMediaDocument = new TLRPC.TL_inputStickeredMediaDocument();
            inputStickeredMediaDocument.id = new TLRPC.TL_inputDocument();
            inputStickeredMediaDocument.id.id = document.id;
            inputStickeredMediaDocument.id.access_hash = document.access_hash;
            inputStickeredMediaDocument.id.file_reference = document.file_reference;
            if (inputStickeredMediaDocument.id.file_reference == null) {
                inputStickeredMediaDocument.id.file_reference = new byte[0];
            }
            req.media = inputStickeredMediaDocument;
        }
        this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersAlert$$ExternalSyntheticLambda19(this, parentObject, req, new StickersAlert$$ExternalSyntheticLambda23(this, req)));
        init(context);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4429lambda$new$1$orgtelegramuiComponentsStickersAlert(TLRPC.TL_messages_getAttachedStickers req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda16(this, error, response, req));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4428lambda$new$0$orgtelegramuiComponentsStickersAlert(TLRPC.TL_error error, TLObject response, TLRPC.TL_messages_getAttachedStickers req) {
        this.reqId = 0;
        if (error == null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            if (vector.objects.isEmpty()) {
                dismiss();
            } else if (vector.objects.size() == 1) {
                TLRPC.StickerSetCovered set = (TLRPC.StickerSetCovered) vector.objects.get(0);
                TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
                this.inputStickerSet = tL_inputStickerSetID;
                tL_inputStickerSetID.id = set.set.id;
                this.inputStickerSet.access_hash = set.set.access_hash;
                loadStickerSet();
            } else {
                this.stickerSetCovereds = new ArrayList<>();
                for (int a = 0; a < vector.objects.size(); a++) {
                    this.stickerSetCovereds.add((TLRPC.StickerSetCovered) vector.objects.get(a));
                }
                this.gridView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                this.titleTextView.setVisibility(8);
                this.shadow[0].setVisibility(8);
                this.adapter.notifyDataSetChanged();
            }
        } else {
            AlertsCreator.processError(this.currentAccount, error, this.parentFragment, req, new Object[0]);
            dismiss();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4430lambda$new$2$orgtelegramuiComponentsStickersAlert(Object parentObject, TLRPC.TL_messages_getAttachedStickers req, RequestDelegate requestDelegate, TLObject response, TLRPC.TL_error error) {
        if (error == null || !FileRefController.isFileRefError(error.text) || parentObject == null) {
            requestDelegate.run(response, error);
            return;
        }
        FileRefController.getInstance(this.currentAccount).requestReference(parentObject, req, requestDelegate);
    }

    public StickersAlert(Context context, String software, ArrayList<Parcelable> uris, ArrayList<String> emoji, Theme.ResourcesProvider resourcesProvider) {
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

            public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
            }

            public void sendSticker(TLRPC.Document sticker, String query, Object parent, boolean notify, int scheduleDate) {
                if (StickersAlert.this.delegate != null) {
                    StickersAlert.this.delegate.onStickerSelected(sticker, query, parent, (MessageObject.SendAnimationData) null, StickersAlert.this.clearsInputField, notify, scheduleDate);
                    StickersAlert.this.dismiss();
                }
            }

            public boolean canSchedule() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
            }

            public boolean isInScheduleMode() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
            }

            public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
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

            public boolean needOpen() {
                return false;
            }

            public long getDialogId() {
                if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                    return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
                }
                return 0;
            }
        };
        this.parentActivity = (Activity) context;
        this.importingStickers = uris;
        this.importingSoftware = software;
        Utilities.globalQueue.postRunnable(new StickersAlert$$ExternalSyntheticLambda12(this, uris, emoji));
        init(context);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4432lambda$new$4$orgtelegramuiComponentsStickersAlert(ArrayList uris, ArrayList emoji) {
        Uri uri;
        String ext;
        ArrayList arrayList = emoji;
        ArrayList<SendMessagesHelper.ImportingSticker> stickers = new ArrayList<>();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Boolean isAnimated = null;
        int a = 0;
        int N = uris.size();
        while (true) {
            if (a >= N) {
                ArrayList arrayList2 = uris;
                break;
            }
            Object obj = uris.get(a);
            if ((obj instanceof Uri) && (ext = MediaController.getStickerExt(uri)) != null) {
                boolean animated = "tgs".equals(ext);
                if (isAnimated == null) {
                    isAnimated = Boolean.valueOf(animated);
                } else if (isAnimated.booleanValue() != animated) {
                    continue;
                }
                if (!isDismissed()) {
                    SendMessagesHelper.ImportingSticker importingSticker = new SendMessagesHelper.ImportingSticker();
                    importingSticker.animated = animated;
                    importingSticker.path = MediaController.copyFileToCache((uri = (Uri) obj), ext, (long) ((animated ? 64 : 512) * 1024));
                    if (importingSticker.path != null) {
                        if (!animated) {
                            BitmapFactory.decodeFile(importingSticker.path, opts);
                            if ((opts.outWidth == 512 && opts.outHeight > 0 && opts.outHeight <= 512) || (opts.outHeight == 512 && opts.outWidth > 0 && opts.outWidth <= 512)) {
                                importingSticker.mimeType = "image/" + ext;
                                importingSticker.validated = true;
                            }
                        } else {
                            importingSticker.mimeType = "application/x-tgsticker";
                        }
                        if (arrayList == null || emoji.size() != N || !(arrayList.get(a) instanceof String)) {
                            importingSticker.emoji = "#️⃣";
                        } else {
                            importingSticker.emoji = (String) arrayList.get(a);
                        }
                        stickers.add(importingSticker);
                        if (stickers.size() >= 200) {
                            break;
                        }
                    } else {
                        continue;
                    }
                } else {
                    return;
                }
            }
            a++;
        }
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda10(this, stickers, isAnimated));
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4431lambda$new$3$orgtelegramuiComponentsStickersAlert(ArrayList stickers, Boolean isAnimatedFinal) {
        this.importingStickersPaths = stickers;
        if (stickers.isEmpty()) {
            dismiss();
            return;
        }
        this.adapter.notifyDataSetChanged();
        if (isAnimatedFinal.booleanValue()) {
            this.uploadImportStickers = new HashMap<>();
            int N = this.importingStickersPaths.size();
            for (int a = 0; a < N; a++) {
                SendMessagesHelper.ImportingSticker sticker = this.importingStickersPaths.get(a);
                this.uploadImportStickers.put(sticker.path, sticker);
                FileLoader.getInstance(this.currentAccount).uploadFile(sticker.path, false, true, 67108864);
            }
        }
        updateFields();
    }

    public StickersAlert(Context context, BaseFragment baseFragment, TLRPC.InputStickerSet set, TLRPC.TL_messages_stickerSet loadedSet, StickersAlertDelegate stickersAlertDelegate) {
        this(context, baseFragment, set, loadedSet, stickersAlertDelegate, (Theme.ResourcesProvider) null);
    }

    public StickersAlert(Context context, BaseFragment baseFragment, TLRPC.InputStickerSet set, TLRPC.TL_messages_stickerSet loadedSet, StickersAlertDelegate stickersAlertDelegate, Theme.ResourcesProvider resourcesProvider) {
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

            public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
            }

            public void sendSticker(TLRPC.Document sticker, String query, Object parent, boolean notify, int scheduleDate) {
                if (StickersAlert.this.delegate != null) {
                    StickersAlert.this.delegate.onStickerSelected(sticker, query, parent, (MessageObject.SendAnimationData) null, StickersAlert.this.clearsInputField, notify, scheduleDate);
                    StickersAlert.this.dismiss();
                }
            }

            public boolean canSchedule() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
            }

            public boolean isInScheduleMode() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
            }

            public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
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

            public boolean needOpen() {
                return false;
            }

            public long getDialogId() {
                if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                    return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
                }
                return 0;
            }
        };
        this.delegate = stickersAlertDelegate;
        this.inputStickerSet = set;
        this.stickerSet = loadedSet;
        this.parentFragment = baseFragment;
        loadStickerSet();
        init(context);
    }

    public void setClearsInputField(boolean value) {
        this.clearsInputField = value;
    }

    public boolean isClearsInputField() {
        return this.clearsInputField;
    }

    private void loadStickerSet() {
        if (this.inputStickerSet != null) {
            MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
            if (this.stickerSet == null && this.inputStickerSet.short_name != null) {
                this.stickerSet = mediaDataController.getStickerSetByName(this.inputStickerSet.short_name);
            }
            if (this.stickerSet == null) {
                this.stickerSet = mediaDataController.getStickerSetById(this.inputStickerSet.id);
            }
            if (this.stickerSet == null) {
                TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
                req.stickerset = this.inputStickerSet;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersAlert$$ExternalSyntheticLambda21(this, mediaDataController));
            } else {
                if (this.adapter != null) {
                    updateSendButton();
                    updateFields();
                    this.adapter.notifyDataSetChanged();
                }
                mediaDataController.preloadStickerSetThumb(this.stickerSet);
            }
        }
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSet;
        if (tL_messages_stickerSet != null) {
            this.showEmoji = !tL_messages_stickerSet.set.masks;
        }
    }

    /* renamed from: lambda$loadStickerSet$6$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4427lambda$loadStickerSet$6$orgtelegramuiComponentsStickersAlert(MediaDataController mediaDataController, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda15(this, error, response, mediaDataController));
    }

    /* renamed from: lambda$loadStickerSet$5$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4426lambda$loadStickerSet$5$orgtelegramuiComponentsStickersAlert(TLRPC.TL_error error, TLObject response, MediaDataController mediaDataController) {
        this.reqId = 0;
        if (error == null) {
            if (Build.VERSION.SDK_INT >= 19) {
                Transition addTarget = new Transition() {
                    public void captureStartValues(TransitionValues transitionValues) {
                        transitionValues.values.put("start", true);
                        transitionValues.values.put("offset", Integer.valueOf(StickersAlert.this.containerView.getTop() + StickersAlert.this.scrollOffsetY));
                    }

                    public void captureEndValues(TransitionValues transitionValues) {
                        transitionValues.values.put("start", false);
                        transitionValues.values.put("offset", Integer.valueOf(StickersAlert.this.containerView.getTop() + StickersAlert.this.scrollOffsetY));
                    }

                    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
                        int scrollOffsetY = StickersAlert.this.scrollOffsetY;
                        int startValue = ((Integer) startValues.values.get("offset")).intValue() - ((Integer) endValues.values.get("offset")).intValue();
                        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        animator.setDuration(250);
                        animator.addUpdateListener(new StickersAlert$2$$ExternalSyntheticLambda0(this, startValue, scrollOffsetY));
                        return animator;
                    }

                    /* renamed from: lambda$createAnimator$0$org-telegram-ui-Components-StickersAlert$2  reason: not valid java name */
                    public /* synthetic */ void m4444xe8fa752b(int startValue, int scrollOffsetY, ValueAnimator a) {
                        float fraction = a.getAnimatedFraction();
                        StickersAlert.this.gridView.setAlpha(fraction);
                        StickersAlert.this.titleTextView.setAlpha(fraction);
                        if (startValue != 0) {
                            int value = (int) (((float) startValue) * (1.0f - fraction));
                            StickersAlert.this.setScrollOffsetY(scrollOffsetY + value);
                            StickersAlert.this.gridView.setTranslationY((float) value);
                        }
                    }
                };
                addTarget.addTarget(this.containerView);
                TransitionManager.beginDelayedTransition(this.container, addTarget);
            }
            this.optionsButton.setVisibility(0);
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) response;
            this.stickerSet = tL_messages_stickerSet;
            this.showEmoji = !tL_messages_stickerSet.set.masks;
            mediaDataController.preloadStickerSetThumb(this.stickerSet);
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
        this.containerView = new FrameLayout(context2) {
            private boolean fullHeight;
            private int lastNotifyWidth;
            private RectF rect = new RectF();

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || StickersAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) StickersAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(ev);
                }
                StickersAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent e) {
                return !StickersAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int contentSize;
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                boolean z = true;
                if (Build.VERSION.SDK_INT >= 21) {
                    boolean unused = StickersAlert.this.ignoreLayout = true;
                    setPadding(StickersAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, StickersAlert.this.backgroundPaddingLeft, 0);
                    boolean unused2 = StickersAlert.this.ignoreLayout = false;
                }
                int unused3 = StickersAlert.this.itemSize = (View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(36.0f)) / 5;
                if (StickersAlert.this.importingStickers != null) {
                    contentSize = AndroidUtilities.dp(96.0f) + (Math.max(3, (int) Math.ceil((double) (((float) StickersAlert.this.importingStickers.size()) / 5.0f))) * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop + AndroidUtilities.statusBarHeight;
                } else if (StickersAlert.this.stickerSetCovereds != null) {
                    contentSize = AndroidUtilities.dp(56.0f) + (AndroidUtilities.dp(60.0f) * StickersAlert.this.stickerSetCovereds.size()) + (StickersAlert.this.adapter.stickersRowCount * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop + AndroidUtilities.dp(24.0f);
                } else {
                    contentSize = AndroidUtilities.dp(96.0f) + (Math.max(3, StickersAlert.this.stickerSet != null ? (int) Math.ceil((double) (((float) StickersAlert.this.stickerSet.documents.size()) / 5.0f)) : 0) * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop + AndroidUtilities.statusBarHeight;
                }
                double d = (double) (height / 5);
                Double.isNaN(d);
                int padding = ((double) contentSize) < d * 3.2d ? 0 : (height / 5) * 2;
                if (padding != 0 && contentSize < height) {
                    padding -= height - contentSize;
                }
                if (padding == 0) {
                    padding = StickersAlert.this.backgroundPaddingTop;
                }
                if (StickersAlert.this.stickerSetCovereds != null) {
                    padding += AndroidUtilities.dp(8.0f);
                }
                if (StickersAlert.this.gridView.getPaddingTop() != padding) {
                    boolean unused4 = StickersAlert.this.ignoreLayout = true;
                    StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0f), padding, AndroidUtilities.dp(10.0f), 0);
                    StickersAlert.this.emptyView.setPadding(0, padding, 0, 0);
                    boolean unused5 = StickersAlert.this.ignoreLayout = false;
                }
                if (contentSize < height) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                if (this.lastNotifyWidth != right - left) {
                    this.lastNotifyWidth = right - left;
                    if (!(StickersAlert.this.adapter == null || StickersAlert.this.stickerSetCovereds == null)) {
                        StickersAlert.this.adapter.notifyDataSetChanged();
                    }
                }
                super.onLayout(changed, left, top, right, bottom);
                StickersAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                float radProgress;
                int statusBarHeight;
                int height;
                int top;
                int y;
                Canvas canvas2 = canvas;
                int y2 = (StickersAlert.this.scrollOffsetY - StickersAlert.this.backgroundPaddingTop) + AndroidUtilities.dp(6.0f);
                int top2 = (StickersAlert.this.scrollOffsetY - StickersAlert.this.backgroundPaddingTop) - AndroidUtilities.dp(13.0f);
                int height2 = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + StickersAlert.this.backgroundPaddingTop;
                float radProgress2 = 1.0f;
                if (Build.VERSION.SDK_INT >= 21) {
                    int top3 = top2 + AndroidUtilities.statusBarHeight;
                    int y3 = y2 + AndroidUtilities.statusBarHeight;
                    int height3 = height2 - AndroidUtilities.statusBarHeight;
                    if (this.fullHeight) {
                        if (StickersAlert.this.backgroundPaddingTop + top3 < AndroidUtilities.statusBarHeight * 2) {
                            int diff = Math.min(AndroidUtilities.statusBarHeight, ((AndroidUtilities.statusBarHeight * 2) - top3) - StickersAlert.this.backgroundPaddingTop);
                            top3 -= diff;
                            height3 += diff;
                            radProgress2 = 1.0f - Math.min(1.0f, ((float) (diff * 2)) / ((float) AndroidUtilities.statusBarHeight));
                        }
                        if (StickersAlert.this.backgroundPaddingTop + top3 < AndroidUtilities.statusBarHeight) {
                            y = y3;
                            top = top3;
                            height = height3;
                            statusBarHeight = Math.min(AndroidUtilities.statusBarHeight, (AndroidUtilities.statusBarHeight - top3) - StickersAlert.this.backgroundPaddingTop);
                            radProgress = radProgress2;
                        } else {
                            y = y3;
                            top = top3;
                            height = height3;
                            statusBarHeight = 0;
                            radProgress = radProgress2;
                        }
                    } else {
                        y = y3;
                        top = top3;
                        height = height3;
                        statusBarHeight = 0;
                        radProgress = 1.0f;
                    }
                } else {
                    y = y2;
                    top = top2;
                    height = height2;
                    statusBarHeight = 0;
                    radProgress = 1.0f;
                }
                StickersAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                StickersAlert.this.shadowDrawable.draw(canvas2);
                if (radProgress != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(StickersAlert.this.getThemedColor("dialogBackground"));
                    this.rect.set((float) StickersAlert.this.backgroundPaddingLeft, (float) (StickersAlert.this.backgroundPaddingTop + top), (float) (getMeasuredWidth() - StickersAlert.this.backgroundPaddingLeft), (float) (StickersAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f)));
                    canvas2.drawRoundRect(this.rect, ((float) AndroidUtilities.dp(12.0f)) * radProgress, ((float) AndroidUtilities.dp(12.0f)) * radProgress, Theme.dialogs_onlineCirclePaint);
                }
                int w = AndroidUtilities.dp(36.0f);
                this.rect.set((float) ((getMeasuredWidth() - w) / 2), (float) y, (float) ((getMeasuredWidth() + w) / 2), (float) (AndroidUtilities.dp(4.0f) + y));
                Theme.dialogs_onlineCirclePaint.setColor(StickersAlert.this.getThemedColor("key_sheet_scrollUp"));
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                if (statusBarHeight > 0) {
                    int color1 = StickersAlert.this.getThemedColor("dialogBackground");
                    Theme.dialogs_onlineCirclePaint.setColor(Color.argb(255, (int) (((float) Color.red(color1)) * 0.8f), (int) (((float) Color.green(color1)) * 0.8f), (int) (((float) Color.blue(color1)) * 0.8f)));
                    canvas.drawRect((float) StickersAlert.this.backgroundPaddingLeft, (float) (AndroidUtilities.statusBarHeight - statusBarHeight), (float) (getMeasuredWidth() - StickersAlert.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
                }
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams.topMargin = AndroidUtilities.dp(48.0f);
        this.shadow[0] = new View(context2);
        this.shadow[0].setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(1);
        this.containerView.addView(this.shadow[0], frameLayoutParams);
        AnonymousClass4 r1 = new RecyclerListView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                return super.onInterceptTouchEvent(event) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, StickersAlert.this.gridView, 0, StickersAlert.this.previewDelegate, this.resourcesProvider);
            }

            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.gridView = r1;
        r1.setTag(14);
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
            public int getSpanSize(int position) {
                if ((StickersAlert.this.stickerSetCovereds == null || !(StickersAlert.this.adapter.cache.get(position) instanceof Integer)) && position != StickersAlert.this.adapter.totalItems) {
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
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = 0;
                outRect.top = 0;
            }
        });
        this.gridView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.gridView.setClipToPadding(false);
        this.gridView.setEnabled(true);
        this.gridView.setGlowColor(getThemedColor("dialogScrollGlow"));
        this.gridView.setOnTouchListener(new StickersAlert$$ExternalSyntheticLambda3(this));
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                StickersAlert.this.updateLayout();
            }
        });
        StickersAlert$$ExternalSyntheticLambda27 stickersAlert$$ExternalSyntheticLambda27 = new StickersAlert$$ExternalSyntheticLambda27(this);
        this.stickersOnItemClickListener = stickersAlert$$ExternalSyntheticLambda27;
        this.gridView.setOnItemClickListener((RecyclerListView.OnItemClickListener) stickersAlert$$ExternalSyntheticLambda27);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 48.0f));
        this.emptyView = new FrameLayout(context2) {
            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.gridView.setEmptyView(this.emptyView);
        this.emptyView.setOnTouchListener(StickersAlert$$ExternalSyntheticLambda4.INSTANCE);
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
        this.optionsButton.setIcon(NUM);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("player_actionBarSelector"), 1));
        this.containerView.addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 5.0f, 5.0f, 0.0f));
        this.optionsButton.addSubItem(1, NUM, (CharSequence) LocaleController.getString("StickersShare", NUM));
        this.optionsButton.addSubItem(2, NUM, (CharSequence) LocaleController.getString("CopyLink", NUM));
        this.optionsButton.setOnClickListener(new StickersAlert$$ExternalSyntheticLambda28(this));
        this.optionsButton.setDelegate(new StickersAlert$$ExternalSyntheticLambda25(this));
        this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.optionsButton.setVisibility(this.inputStickerSet != null ? 0 : 8);
        this.emptyView.addView(new RadialProgressView(context2), LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout.LayoutParams frameLayoutParams2 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        frameLayoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
        this.shadow[1] = new View(context2);
        this.shadow[1].setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.containerView.addView(this.shadow[1], frameLayoutParams2);
        TextView textView2 = new TextView(context2);
        this.pickerBottomLayout = textView2;
        textView2.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(getThemedColor("dialogBackground"), getThemedColor("listSelectorSDK21")));
        TextView textView3 = this.pickerBottomLayout;
        this.buttonTextColorKey = "dialogTextBlue2";
        textView3.setTextColor(getThemedColor("dialogTextBlue2"));
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
        this.stickerPreviewLayout.setOnClickListener(new StickersAlert$$ExternalSyntheticLambda29(this));
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
        this.previewSendButton.setOnClickListener(new StickersAlert$$ExternalSyntheticLambda30(this));
        FrameLayout.LayoutParams frameLayoutParams3 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        frameLayoutParams3.bottomMargin = AndroidUtilities.dp(48.0f);
        View view = new View(context2);
        this.previewSendButtonShadow = view;
        view.setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, frameLayoutParams3);
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

    /* renamed from: lambda$init$7$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ boolean m4424lambda$init$7$orgtelegramuiComponentsStickersAlert(View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.gridView, 0, this.stickersOnItemClickListener, this.previewDelegate, this.resourcesProvider);
    }

    /* renamed from: lambda$init$8$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4425lambda$init$8$orgtelegramuiComponentsStickersAlert(View view, int position) {
        int i = position;
        if (this.stickerSetCovereds != null) {
            TLRPC.StickerSetCovered pack = (TLRPC.StickerSetCovered) this.adapter.positionsToSets.get(i);
            if (pack != null) {
                dismiss();
                TLRPC.TL_inputStickerSetID inputStickerSetID = new TLRPC.TL_inputStickerSetID();
                inputStickerSetID.access_hash = pack.set.access_hash;
                inputStickerSetID.id = pack.set.id;
                new StickersAlert(this.parentActivity, this.parentFragment, inputStickerSetID, (TLRPC.TL_messages_stickerSet) null, (StickersAlertDelegate) null, this.resourcesProvider).show();
                return;
            }
            return;
        }
        ArrayList<SendMessagesHelper.ImportingSticker> arrayList = this.importingStickersPaths;
        if (arrayList == null) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSet;
            if (tL_messages_stickerSet != null && i >= 0 && i < tL_messages_stickerSet.documents.size()) {
                this.selectedSticker = (TLRPC.Document) this.stickerSet.documents.get(i);
                boolean set = false;
                int a = 0;
                while (true) {
                    if (a >= this.selectedSticker.attributes.size()) {
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = this.selectedSticker.attributes.get(a);
                    if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                        a++;
                    } else if (attribute.alt != null && attribute.alt.length() > 0) {
                        this.stickerEmojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                        set = true;
                    }
                }
                if (!set) {
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
        } else if (i >= 0 && i < arrayList.size()) {
            SendMessagesHelper.ImportingSticker importingSticker = this.importingStickersPaths.get(i);
            this.selectedStickerPath = importingSticker;
            if (importingSticker.validated) {
                this.stickerEmojiTextView.setText(Emoji.replaceEmoji(this.selectedStickerPath.emoji, this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
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

    static /* synthetic */ boolean lambda$init$9(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$init$10$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4421lambda$init$10$orgtelegramuiComponentsStickersAlert(View v) {
        this.optionsButton.toggleSubMenu();
    }

    /* renamed from: lambda$init$11$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4422lambda$init$11$orgtelegramuiComponentsStickersAlert(View v) {
        hidePreview();
    }

    /* renamed from: lambda$init$12$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4423lambda$init$12$orgtelegramuiComponentsStickersAlert(View v) {
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
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet;
        int size = (int) (((float) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2)) / AndroidUtilities.density);
        if (this.importingStickers != null) {
            this.previewSendButton.setText(LocaleController.getString("ImportStickersRemove", NUM).toUpperCase());
            this.previewSendButton.setTextColor(getThemedColor("dialogTextRed"));
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(size, (float) size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(size, (float) size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.previewSendButton.setVisibility(0);
            this.previewSendButtonShadow.setVisibility(0);
        } else if (this.delegate == null || ((tL_messages_stickerSet = this.stickerSet) != null && tL_messages_stickerSet.set.masks)) {
            this.previewSendButton.setText(LocaleController.getString("Close", NUM).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(size, size, 17));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(size, size, 17));
            this.previewSendButton.setVisibility(8);
            this.previewSendButtonShadow.setVisibility(8);
        } else {
            this.previewSendButton.setText(LocaleController.getString("SendSticker", NUM).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(size, (float) size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(size, (float) size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.previewSendButton.setVisibility(0);
            this.previewSendButtonShadow.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    public void removeSticker(SendMessagesHelper.ImportingSticker sticker) {
        int idx = this.importingStickersPaths.indexOf(sticker);
        if (idx >= 0) {
            this.importingStickersPaths.remove(idx);
            this.adapter.notifyItemRemoved(idx);
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
    public void onSubItemClick(int id) {
        BaseFragment baseFragment;
        if (this.stickerSet != null) {
            String stickersUrl = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/" + this.stickerSet.set.short_name;
            if (id == 1) {
                Context context = this.parentActivity;
                if (context == null && (baseFragment = this.parentFragment) != null) {
                    context = baseFragment.getParentActivity();
                }
                if (context == null) {
                    context = getContext();
                }
                ShareAlert alert = new ShareAlert(context, (ArrayList<MessageObject>) null, stickersUrl, false, stickersUrl, false, this.resourcesProvider);
                BaseFragment baseFragment2 = this.parentFragment;
                if (baseFragment2 != null) {
                    baseFragment2.showDialog(alert);
                } else {
                    alert.show();
                }
            } else if (id == 2) {
                try {
                    AndroidUtilities.addToClipboard(stickersUrl);
                    BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createCopyLinkBulletin().show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    private void updateFields() {
        String text;
        String text2;
        TextView textView = this.titleTextView;
        if (textView != null) {
            if (this.stickerSet != null) {
                SpannableStringBuilder stringBuilder = null;
                try {
                    if (this.urlPattern == null) {
                        this.urlPattern = Pattern.compile("@[a-zA-Z\\d_]{1,32}");
                    }
                    Matcher matcher = this.urlPattern.matcher(this.stickerSet.set.title);
                    while (matcher.find()) {
                        if (stringBuilder == null) {
                            stringBuilder = new SpannableStringBuilder(this.stickerSet.set.title);
                            this.titleTextView.setMovementMethod(new LinkMovementMethodMy());
                        }
                        int start = matcher.start();
                        int end = matcher.end();
                        if (this.stickerSet.set.title.charAt(start) != '@') {
                            start++;
                        }
                        stringBuilder.setSpan(new URLSpanNoUnderline(this.stickerSet.set.title.subSequence(start + 1, end).toString()) {
                            public void onClick(View widget) {
                                MessagesController.getInstance(StickersAlert.this.currentAccount).openByUserName(getURL(), StickersAlert.this.parentFragment, 1);
                                StickersAlert.this.dismiss();
                            }
                        }, start, end, 0);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                this.titleTextView.setText(stringBuilder != null ? stringBuilder : this.stickerSet.set.title);
                if (this.stickerSet.set == null || !MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(this.stickerSet.set.id)) {
                    if (this.stickerSet.set == null || !this.stickerSet.set.masks) {
                        text = LocaleController.formatString("AddStickersCount", NUM, LocaleController.formatPluralString("Stickers", this.stickerSet.documents.size())).toUpperCase();
                    } else {
                        text = LocaleController.formatString("AddStickersCount", NUM, LocaleController.formatPluralString("MasksCount", this.stickerSet.documents.size())).toUpperCase();
                    }
                    setButton(new StickersAlert$$ExternalSyntheticLambda31(this), text, "dialogTextBlue2");
                } else {
                    if (this.stickerSet.set.masks) {
                        text2 = LocaleController.formatString("RemoveStickersCount", NUM, LocaleController.formatPluralString("MasksCount", this.stickerSet.documents.size())).toUpperCase();
                    } else {
                        text2 = LocaleController.formatString("RemoveStickersCount", NUM, LocaleController.formatPluralString("Stickers", this.stickerSet.documents.size())).toUpperCase();
                    }
                    if (this.stickerSet.set.official) {
                        setButton(new StickersAlert$$ExternalSyntheticLambda32(this), text2, "dialogTextRed");
                    } else {
                        setButton(new StickersAlert$$ExternalSyntheticLambda33(this), text2, "dialogTextRed");
                    }
                }
                this.adapter.notifyDataSetChanged();
                return;
            }
            ArrayList<Parcelable> arrayList = this.importingStickers;
            if (arrayList != null) {
                ArrayList<SendMessagesHelper.ImportingSticker> arrayList2 = this.importingStickersPaths;
                textView.setText(LocaleController.formatPluralString("Stickers", arrayList2 != null ? arrayList2.size() : arrayList.size()));
                HashMap<String, SendMessagesHelper.ImportingSticker> hashMap = this.uploadImportStickers;
                if (hashMap == null || hashMap.isEmpty()) {
                    StickersAlert$$ExternalSyntheticLambda34 stickersAlert$$ExternalSyntheticLambda34 = new StickersAlert$$ExternalSyntheticLambda34(this);
                    Object[] objArr = new Object[1];
                    ArrayList arrayList3 = this.importingStickersPaths;
                    if (arrayList3 == null) {
                        arrayList3 = this.importingStickers;
                    }
                    objArr[0] = LocaleController.formatPluralString("Stickers", arrayList3.size());
                    setButton(stickersAlert$$ExternalSyntheticLambda34, LocaleController.formatString("ImportStickers", NUM, objArr).toUpperCase(), "dialogTextBlue2");
                    this.pickerBottomLayout.setEnabled(true);
                    return;
                }
                setButton((View.OnClickListener) null, LocaleController.getString("ImportStickersProcessing", NUM).toUpperCase(), "dialogTextGray2");
                this.pickerBottomLayout.setEnabled(false);
                return;
            }
            setButton(new StickersAlert$$ExternalSyntheticLambda1(this), LocaleController.getString("Close", NUM).toUpperCase(), "dialogTextBlue2");
        }
    }

    /* renamed from: lambda$updateFields$15$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4439lambda$updateFields$15$orgtelegramuiComponentsStickersAlert(View v) {
        dismiss();
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetInstalled();
        }
        if (this.inputStickerSet != null && !MediaDataController.getInstance(this.currentAccount).cancelRemovingStickerSet(this.inputStickerSet.id)) {
            TLRPC.TL_messages_installStickerSet req = new TLRPC.TL_messages_installStickerSet();
            req.stickerset = this.inputStickerSet;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersAlert$$ExternalSyntheticLambda18(this));
        }
    }

    /* renamed from: lambda$updateFields$14$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4438lambda$updateFields$14$orgtelegramuiComponentsStickersAlert(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda14(this, error, response));
    }

    /* renamed from: lambda$updateFields$13$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4437lambda$updateFields$13$orgtelegramuiComponentsStickersAlert(TLRPC.TL_error error, TLObject response) {
        int type = this.stickerSet.set.masks;
        if (error == null) {
            try {
                if (this.showTooltipWhenToggle) {
                    Bulletin.make(this.parentFragment, (Bulletin.Layout) new StickerSetBulletinLayout(this.pickerBottomLayout.getContext(), this.stickerSet, 2, (TLRPC.Document) null, this.resourcesProvider), 1500).show();
                }
                if (response instanceof TLRPC.TL_messages_stickerSetInstallResultArchive) {
                    MediaDataController.getInstance(this.currentAccount).processStickerSetInstallResultArchive(this.parentFragment, true, type, (TLRPC.TL_messages_stickerSetInstallResultArchive) response);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            Toast.makeText(getContext(), LocaleController.getString("ErrorOccurred", NUM), 0).show();
        }
        MediaDataController.getInstance(this.currentAccount).loadStickers((int) type, false, true);
    }

    /* renamed from: lambda$updateFields$16$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4440lambda$updateFields$16$orgtelegramuiComponentsStickersAlert(View v) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), this.stickerSet, 1, this.parentFragment, true, this.showTooltipWhenToggle);
    }

    /* renamed from: lambda$updateFields$17$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4441lambda$updateFields$17$orgtelegramuiComponentsStickersAlert(View v) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), this.stickerSet, 0, this.parentFragment, true, this.showTooltipWhenToggle);
    }

    /* renamed from: lambda$updateFields$18$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4442lambda$updateFields$18$orgtelegramuiComponentsStickersAlert(View v) {
        showNameEnterAlert();
    }

    /* renamed from: lambda$updateFields$19$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4443lambda$updateFields$19$orgtelegramuiComponentsStickersAlert(View v) {
        dismiss();
    }

    private void showNameEnterAlert() {
        Context context = getContext();
        final int[] state = {0};
        FrameLayout fieldLayout = new FrameLayout(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("ImportStickersEnterName", NUM));
        builder.setPositiveButton(LocaleController.getString("Next", NUM), StickersAlert$$ExternalSyntheticLambda11.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        linearLayout.addView(fieldLayout, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
        final TextView message = new TextView(context);
        TextView textView = new TextView(context);
        textView.setTextSize(1, 16.0f);
        textView.setTextColor(getThemedColor("dialogTextHint"));
        textView.setMaxLines(1);
        textView.setLines(1);
        textView.setText("t.me/addstickers/");
        textView.setInputType(16385);
        textView.setGravity(51);
        textView.setSingleLine(true);
        textView.setVisibility(4);
        textView.setImeOptions(6);
        textView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        fieldLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 51));
        final EditTextBoldCursor editText = new EditTextBoldCursor(context);
        editText.setBackground((Drawable) null);
        editText.setLineColors(Theme.getColor("dialogInputField"), Theme.getColor("dialogInputFieldActivated"), Theme.getColor("dialogTextRed2"));
        editText.setTextSize(1, 16.0f);
        editText.setTextColor(getThemedColor("dialogTextBlack"));
        editText.setMaxLines(1);
        editText.setLines(1);
        editText.setInputType(16385);
        editText.setGravity(51);
        editText.setSingleLine(true);
        editText.setImeOptions(5);
        editText.setCursorColor(getThemedColor("windowBackgroundWhiteBlackText"));
        editText.setCursorSize(AndroidUtilities.dp(20.0f));
        editText.setCursorWidth(1.5f);
        editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (state[0] == 2) {
                    StickersAlert.this.checkUrlAvailable(message, editText.getText().toString(), false);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
        fieldLayout.addView(editText, LayoutHelper.createFrame(-1, 36, 51));
        editText.setOnEditorActionListener(new StickersAlert$$ExternalSyntheticLambda5(builder));
        editText.setSelection(editText.length());
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new StickersAlert$$ExternalSyntheticLambda0(editText));
        message.setText(AndroidUtilities.replaceTags(LocaleController.getString("ImportStickersEnterNameInfo", NUM)));
        message.setTextSize(1, 14.0f);
        message.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
        message.setTextColor(getThemedColor("dialogTextGray2"));
        linearLayout.addView(message, LayoutHelper.createLinear(-1, -2));
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new StickersAlert$$ExternalSyntheticLambda22(editText));
        alertDialog.show();
        editText.requestFocus();
        StickersAlert$$ExternalSyntheticLambda2 stickersAlert$$ExternalSyntheticLambda2 = r0;
        Context context2 = context;
        View button = alertDialog.getButton(-1);
        StickersAlert$$ExternalSyntheticLambda2 stickersAlert$$ExternalSyntheticLambda22 = new StickersAlert$$ExternalSyntheticLambda2(this, state, editText, message, textView, builder);
        button.setOnClickListener(stickersAlert$$ExternalSyntheticLambda2);
    }

    static /* synthetic */ void lambda$showNameEnterAlert$20(DialogInterface dialog, int which) {
    }

    static /* synthetic */ boolean lambda$showNameEnterAlert$21(AlertDialog.Builder builder, TextView view, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        builder.create().getButton(-1).callOnClick();
        return true;
    }

    static /* synthetic */ void lambda$showNameEnterAlert$23(EditTextBoldCursor editText) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    /* renamed from: lambda$showNameEnterAlert$28$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4436xafbvar_(int[] state, EditTextBoldCursor editText, TextView message, TextView textView, AlertDialog.Builder builder, View v) {
        if (state[0] != 1) {
            if (state[0] == 0) {
                state[0] = 1;
                TLRPC.TL_stickers_suggestShortName req = new TLRPC.TL_stickers_suggestShortName();
                String obj = editText.getText().toString();
                this.setTitle = obj;
                req.title = obj;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersAlert$$ExternalSyntheticLambda24(this, editText, message, textView, state));
            } else if (state[0] == 2) {
                state[0] = 3;
                if (!this.lastNameAvailable) {
                    AndroidUtilities.shakeView(editText, 2.0f, 0);
                    editText.performHapticFeedback(3, 2);
                }
                AndroidUtilities.hideKeyboard(editText);
                SendMessagesHelper.getInstance(this.currentAccount).prepareImportStickers(this.setTitle, this.lastCheckName, this.importingSoftware, this.importingStickersPaths, new StickersAlert$$ExternalSyntheticLambda17(this));
                builder.getDismissRunnable().run();
                dismiss();
            }
        }
    }

    /* renamed from: lambda$showNameEnterAlert$26$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4434xfCLASSNAMEa2a(EditTextBoldCursor editText, TextView message, TextView textView, int[] state, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda13(this, response, editText, message, textView, state));
    }

    /* renamed from: lambda$showNameEnterAlert$25$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4433xf4cCLASSNAMEb(TLObject response, EditTextBoldCursor editText, TextView message, TextView textView, int[] state) {
        boolean set = false;
        if (response instanceof TLRPC.TL_stickers_suggestedShortName) {
            TLRPC.TL_stickers_suggestedShortName res = (TLRPC.TL_stickers_suggestedShortName) response;
            if (res.short_name != null) {
                editText.setText(res.short_name);
                editText.setSelection(0, editText.length());
                checkUrlAvailable(message, editText.getText().toString(), true);
                set = true;
            }
        }
        textView.setVisibility(0);
        editText.setPadding(textView.getMeasuredWidth(), AndroidUtilities.dp(4.0f), 0, 0);
        if (!set) {
            editText.setText("");
        }
        state[0] = 2;
    }

    /* renamed from: lambda$showNameEnterAlert$27$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4435x396bvar_(String param) {
        new ImportingAlert(getContext(), this.lastCheckName, (ChatActivity) null, this.resourcesProvider).show();
    }

    /* access modifiers changed from: private */
    public void checkUrlAvailable(TextView message, String text, boolean forceAvailable) {
        if (forceAvailable) {
            message.setText(LocaleController.getString("ImportStickersLinkAvailable", NUM));
            message.setTextColor(getThemedColor("windowBackgroundWhiteGreenText"));
            this.lastNameAvailable = true;
            this.lastCheckName = text;
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
        if (TextUtils.isEmpty(text)) {
            message.setText(LocaleController.getString("ImportStickersEnterUrlInfo", NUM));
            message.setTextColor(getThemedColor("dialogTextGray2"));
            return;
        }
        this.lastNameAvailable = false;
        if (text != null) {
            if (text.startsWith("_") || text.endsWith("_")) {
                message.setText(LocaleController.getString("ImportStickersLinkInvalid", NUM));
                message.setTextColor(getThemedColor("windowBackgroundWhiteRedText4"));
                return;
            }
            int N = text.length();
            for (int a = 0; a < N; a++) {
                char ch = text.charAt(a);
                if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    message.setText(LocaleController.getString("ImportStickersEnterUrlInfo", NUM));
                    message.setTextColor(getThemedColor("windowBackgroundWhiteRedText4"));
                    return;
                }
            }
        }
        if (text == null || text.length() < 5) {
            message.setText(LocaleController.getString("ImportStickersLinkInvalidShort", NUM));
            message.setTextColor(getThemedColor("windowBackgroundWhiteRedText4"));
        } else if (text.length() > 32) {
            message.setText(LocaleController.getString("ImportStickersLinkInvalidLong", NUM));
            message.setTextColor(getThemedColor("windowBackgroundWhiteRedText4"));
        } else {
            message.setText(LocaleController.getString("ImportStickersLinkChecking", NUM));
            message.setTextColor(getThemedColor("windowBackgroundWhiteGrayText8"));
            this.lastCheckName = text;
            StickersAlert$$ExternalSyntheticLambda7 stickersAlert$$ExternalSyntheticLambda7 = new StickersAlert$$ExternalSyntheticLambda7(this, text, message);
            this.checkRunnable = stickersAlert$$ExternalSyntheticLambda7;
            AndroidUtilities.runOnUIThread(stickersAlert$$ExternalSyntheticLambda7, 300);
        }
    }

    /* renamed from: lambda$checkUrlAvailable$31$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4419xb8bfea4c(String text, TextView message) {
        TLRPC.TL_stickers_checkShortName req = new TLRPC.TL_stickers_checkShortName();
        req.short_name = text;
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersAlert$$ExternalSyntheticLambda20(this, text, message), 2);
    }

    /* renamed from: lambda$checkUrlAvailable$30$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4418xb15ab52d(String text, TextView message, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda9(this, text, error, response, message));
    }

    /* renamed from: lambda$checkUrlAvailable$29$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4417xea82483(String text, TLRPC.TL_error error, TLObject response, TextView message) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(text)) {
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                message.setText(LocaleController.getString("ImportStickersLinkTaken", NUM));
                message.setTextColor(getThemedColor("windowBackgroundWhiteRedText4"));
                this.lastNameAvailable = false;
                return;
            }
            message.setText(LocaleController.getString("ImportStickersLinkAvailable", NUM));
            message.setTextColor(getThemedColor("windowBackgroundWhiteGreenText"));
            this.lastNameAvailable = true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.gridView.getChildCount() <= 0) {
            setScrollOffsetY(this.gridView.getPaddingTop());
            return;
        }
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(0, true);
        } else {
            newOffset = top;
            runShadowAnimation(0, false);
        }
        if (this.scrollOffsetY != newOffset) {
            setScrollOffsetY(newOffset);
        }
    }

    /* access modifiers changed from: private */
    public void setScrollOffsetY(int newOffset) {
        this.scrollOffsetY = newOffset;
        this.gridView.setTopGlowOffset(newOffset);
        if (this.stickerSetCovereds == null) {
            this.titleTextView.setTranslationY((float) newOffset);
            if (this.importingStickers == null) {
                this.optionsButton.setTranslationY((float) newOffset);
            }
            this.shadow[0].setTranslationY((float) newOffset);
        }
        this.containerView.invalidate();
    }

    private void hidePreview() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, new float[]{0.0f})});
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                StickersAlert.this.stickerPreviewLayout.setVisibility(8);
                StickersAlert.this.stickerImageView.setImageDrawable((Drawable) null);
            }
        });
        animatorSet.start();
    }

    private void runShadowAnimation(final int num, final boolean show) {
        if (this.stickerSetCovereds == null) {
            if ((show && this.shadow[num].getTag() != null) || (!show && this.shadow[num].getTag() == null)) {
                this.shadow[num].setTag(show ? null : 1);
                if (show) {
                    this.shadow[num].setVisibility(0);
                }
                AnimatorSet[] animatorSetArr = this.shadowAnimation;
                if (animatorSetArr[num] != null) {
                    animatorSetArr[num].cancel();
                }
                this.shadowAnimation[num] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[num];
                Animator[] animatorArr = new Animator[1];
                View view = this.shadow[num];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[num].setDuration(150);
                this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (StickersAlert.this.shadowAnimation[num] != null && StickersAlert.this.shadowAnimation[num].equals(animation)) {
                            if (!show) {
                                StickersAlert.this.shadow[num].setVisibility(4);
                            }
                            StickersAlert.this.shadowAnimation[num] = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (StickersAlert.this.shadowAnimation[num] != null && StickersAlert.this.shadowAnimation[num].equals(animation)) {
                            StickersAlert.this.shadowAnimation[num] = null;
                        }
                    }
                });
                this.shadowAnimation[num].start();
            }
        }
    }

    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
    }

    public void setOnDismissListener(Runnable onDismissListener2) {
        this.onDismissListener = onDismissListener2;
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
                int N = arrayList.size();
                for (int a = 0; a < N; a++) {
                    SendMessagesHelper.ImportingSticker sticker = this.importingStickersPaths.get(a);
                    if (!sticker.validated) {
                        FileLoader.getInstance(this.currentAccount).cancelFileUpload(sticker.path, false);
                    }
                    if (sticker.animated) {
                        new File(sticker.path).delete();
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

            public int getBottomOffset(int tag) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003e, code lost:
        r1 = r8[0];
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r6, int r7, java.lang.Object... r8) {
        /*
            r5 = this;
            int r0 = org.telegram.messenger.NotificationCenter.emojiLoaded
            if (r6 != r0) goto L_0x0034
            org.telegram.ui.Components.RecyclerListView r0 = r5.gridView
            if (r0 == 0) goto L_0x001b
            int r0 = r0.getChildCount()
            r1 = 0
        L_0x000d:
            if (r1 >= r0) goto L_0x001b
            org.telegram.ui.Components.RecyclerListView r2 = r5.gridView
            android.view.View r2 = r2.getChildAt(r1)
            r2.invalidate()
            int r1 = r1 + 1
            goto L_0x000d
        L_0x001b:
            org.telegram.ui.ContentPreviewViewer r0 = org.telegram.ui.ContentPreviewViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x002c
            org.telegram.ui.ContentPreviewViewer r0 = org.telegram.ui.ContentPreviewViewer.getInstance()
            r0.close()
        L_0x002c:
            org.telegram.ui.ContentPreviewViewer r0 = org.telegram.ui.ContentPreviewViewer.getInstance()
            r0.reset()
            goto L_0x007f
        L_0x0034:
            int r0 = org.telegram.messenger.NotificationCenter.fileUploaded
            r1 = 0
            if (r6 != r0) goto L_0x005a
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r0 = r5.uploadImportStickers
            if (r0 != 0) goto L_0x003e
            return
        L_0x003e:
            r1 = r8[r1]
            java.lang.String r1 = (java.lang.String) r1
            java.lang.Object r0 = r0.get(r1)
            org.telegram.messenger.SendMessagesHelper$ImportingSticker r0 = (org.telegram.messenger.SendMessagesHelper.ImportingSticker) r0
            if (r0 == 0) goto L_0x007e
            int r2 = r5.currentAccount
            r3 = 1
            r3 = r8[r3]
            org.telegram.tgnet.TLRPC$InputFile r3 = (org.telegram.tgnet.TLRPC.InputFile) r3
            org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda8 r4 = new org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda8
            r4.<init>(r5, r1, r0)
            r0.uploadMedia(r2, r3, r4)
            goto L_0x007e
        L_0x005a:
            int r0 = org.telegram.messenger.NotificationCenter.fileUploadFailed
            if (r6 != r0) goto L_0x007e
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r0 = r5.uploadImportStickers
            if (r0 != 0) goto L_0x0063
            return
        L_0x0063:
            r1 = r8[r1]
            java.lang.String r1 = (java.lang.String) r1
            java.lang.Object r0 = r0.remove(r1)
            org.telegram.messenger.SendMessagesHelper$ImportingSticker r0 = (org.telegram.messenger.SendMessagesHelper.ImportingSticker) r0
            if (r0 == 0) goto L_0x0072
            r5.removeSticker(r0)
        L_0x0072:
            java.util.HashMap<java.lang.String, org.telegram.messenger.SendMessagesHelper$ImportingSticker> r2 = r5.uploadImportStickers
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x007f
            r5.updateFields()
            goto L_0x007f
        L_0x007e:
        L_0x007f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* renamed from: lambda$didReceivedNotification$32$org-telegram-ui-Components-StickersAlert  reason: not valid java name */
    public /* synthetic */ void m4420x16fvar_f4(String location, SendMessagesHelper.ImportingSticker sticker) {
        if (!isDismissed()) {
            this.uploadImportStickers.remove(location);
            if (!"application/x-tgsticker".equals(sticker.mimeType)) {
                removeSticker(sticker);
            } else {
                sticker.validated = true;
                int idx = this.importingStickersPaths.indexOf(sticker);
                if (idx >= 0) {
                    RecyclerView.ViewHolder holder = this.gridView.findViewHolderForAdapterPosition(idx);
                    if (holder != null) {
                        ((StickerEmojiCell) holder.itemView).setSticker(sticker);
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

    private void setButton(View.OnClickListener onClickListener, String title, String colorKey) {
        TextView textView = this.pickerBottomLayout;
        this.buttonTextColorKey = colorKey;
        textView.setTextColor(getThemedColor(colorKey));
        this.pickerBottomLayout.setText(title.toUpperCase());
        this.pickerBottomLayout.setOnClickListener(onClickListener);
    }

    public boolean isShowTooltipWhenToggle() {
        return this.showTooltipWhenToggle;
    }

    public void setShowTooltipWhenToggle(boolean showTooltipWhenToggle2) {
        this.showTooltipWhenToggle = showTooltipWhenToggle2;
    }

    public void updateColors() {
        updateColors(false);
    }

    public void updateColors(boolean applyDescriptions) {
        this.adapter.updateColors();
        this.titleTextView.setHighlightColor(getThemedColor("dialogLinkSelection"));
        this.stickerPreviewLayout.setBackgroundColor(getThemedColor("dialogBackground") & -NUM);
        this.optionsButton.setIconColor(getThemedColor("key_sheet_other"));
        this.optionsButton.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItem"), false);
        this.optionsButton.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItemIcon"), true);
        this.optionsButton.setPopupItemsSelectorColor(getThemedColor("dialogButtonSelector"));
        this.optionsButton.redrawPopup(getThemedColor("actionBarDefaultSubmenuBackground"));
        if (applyDescriptions) {
            if (Theme.isAnimatingColor() && this.animatingDescriptions == null) {
                ArrayList<ThemeDescription> themeDescriptions = getThemeDescriptions();
                this.animatingDescriptions = themeDescriptions;
                int N = themeDescriptions.size();
                for (int i = 0; i < N; i++) {
                    this.animatingDescriptions.get(i).setDelegateDisabled();
                }
            }
            int N2 = this.animatingDescriptions.size();
            for (int i2 = 0; i2 < N2; i2++) {
                ThemeDescription description = this.animatingDescriptions.get(i2);
                description.setColor(getThemedColor(description.getCurrentKey()), false, false);
            }
        }
        if (Theme.isAnimatingColor() == 0 && this.animatingDescriptions != null) {
            this.animatingDescriptions = null;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> descriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate delegate2 = new StickersAlert$$ExternalSyntheticLambda26(this);
        descriptions.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.shadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        descriptions.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        this.adapter.getThemeDescriptions(descriptions, delegate2);
        descriptions.add(new ThemeDescription(this.shadow[0], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        descriptions.add(new ThemeDescription(this.shadow[1], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        descriptions.add(new ThemeDescription(this.gridView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        descriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        descriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_LINKCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextLink"));
        descriptions.add(new ThemeDescription(this.optionsButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSelector"));
        descriptions.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        descriptions.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        descriptions.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, this.buttonTextColorKey));
        descriptions.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue2"));
        descriptions.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        descriptions.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        descriptions.add(new ThemeDescription(this.previewSendButtonShadow, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = delegate2;
        descriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "dialogLinkSelection"));
        descriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "dialogBackground"));
        descriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "key_sheet_other"));
        descriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "actionBarDefaultSubmenuItem"));
        descriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "actionBarDefaultSubmenuItemIcon"));
        descriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "dialogButtonSelector"));
        descriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "actionBarDefaultSubmenuBackground"));
        return descriptions;
    }

    private class GridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private Context context;
        /* access modifiers changed from: private */
        public SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int stickersRowCount;
        /* access modifiers changed from: private */
        public int totalItems;

        public GridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public int getItemViewType(int position) {
            if (StickersAlert.this.stickerSetCovereds == null) {
                return 0;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            return 2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    StickerEmojiCell cell = new StickerEmojiCell(this.context, false) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(StickersAlert.this.itemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    cell.getImageView().setLayerNum(7);
                    view = cell;
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new FeaturedStickerSetInfoCell(this.context, 8, true, false, StickersAlert.this.resourcesProvider);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                switch (holder.getItemViewType()) {
                    case 0:
                        ((StickerEmojiCell) holder.itemView).setSticker((TLRPC.Document) this.cache.get(position), this.positionsToSets.get(position), false);
                        return;
                    case 1:
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                        return;
                    case 2:
                        ((FeaturedStickerSetInfoCell) holder.itemView).setStickerSet((TLRPC.StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(((Integer) this.cache.get(position)).intValue()), false);
                        return;
                    default:
                        return;
                }
            } else if (StickersAlert.this.importingStickers != null) {
                ((StickerEmojiCell) holder.itemView).setSticker((SendMessagesHelper.ImportingSticker) StickersAlert.this.importingStickersPaths.get(position));
            } else {
                ((StickerEmojiCell) holder.itemView).setSticker((TLRPC.Document) StickersAlert.this.stickerSet.documents.get(position), StickersAlert.this.stickerSet, StickersAlert.this.showEmoji);
            }
        }

        public void notifyDataSetChanged() {
            int count;
            int i;
            int i2 = 0;
            if (StickersAlert.this.stickerSetCovereds != null) {
                int width = StickersAlert.this.gridView.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
                StickersAlert.this.layoutManager.setSpanCount(this.stickersPerRow);
                this.cache.clear();
                this.positionsToSets.clear();
                this.totalItems = 0;
                this.stickersRowCount = 0;
                for (int a = 0; a < StickersAlert.this.stickerSetCovereds.size(); a++) {
                    TLRPC.StickerSetCovered pack = (TLRPC.StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(a);
                    if (!pack.covers.isEmpty() || pack.cover != null) {
                        double d = (double) this.stickersRowCount;
                        double ceil = Math.ceil((double) (((float) StickersAlert.this.stickerSetCovereds.size()) / ((float) this.stickersPerRow)));
                        Double.isNaN(d);
                        this.stickersRowCount = (int) (d + ceil);
                        this.positionsToSets.put(this.totalItems, pack);
                        SparseArray<Object> sparseArray = this.cache;
                        int i3 = this.totalItems;
                        this.totalItems = i3 + 1;
                        sparseArray.put(i3, Integer.valueOf(a));
                        int i4 = this.totalItems / this.stickersPerRow;
                        if (!pack.covers.isEmpty()) {
                            count = (int) Math.ceil((double) (((float) pack.covers.size()) / ((float) this.stickersPerRow)));
                            for (int b = 0; b < pack.covers.size(); b++) {
                                this.cache.put(this.totalItems + b, pack.covers.get(b));
                            }
                        } else {
                            count = 1;
                            this.cache.put(this.totalItems, pack.cover);
                        }
                        int b2 = 0;
                        while (true) {
                            i = this.stickersPerRow;
                            if (b2 >= count * i) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + b2, pack);
                            b2++;
                        }
                        this.totalItems += i * count;
                    }
                }
            } else if (StickersAlert.this.importingStickersPaths != null) {
                this.totalItems = StickersAlert.this.importingStickersPaths.size();
            } else {
                if (StickersAlert.this.stickerSet != null) {
                    i2 = StickersAlert.this.stickerSet.documents.size();
                }
                this.totalItems = i2;
            }
            super.notifyDataSetChanged();
        }

        public void notifyItemRemoved(int position) {
            if (StickersAlert.this.importingStickersPaths != null) {
                this.totalItems = StickersAlert.this.importingStickersPaths.size();
            }
            super.notifyItemRemoved(position);
        }

        public void updateColors() {
            if (StickersAlert.this.stickerSetCovereds != null) {
                int size = StickersAlert.this.gridView.getChildCount();
                for (int i = 0; i < size; i++) {
                    View child = StickersAlert.this.gridView.getChildAt(i);
                    if (child instanceof FeaturedStickerSetInfoCell) {
                        ((FeaturedStickerSetInfoCell) child).updateColors();
                    }
                }
            }
        }

        public void getThemeDescriptions(List<ThemeDescription> descriptions, ThemeDescription.ThemeDescriptionDelegate delegate) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                FeaturedStickerSetInfoCell.createThemeDescriptions(descriptions, StickersAlert.this.gridView, delegate);
            }
        }
    }
}
