package org.telegram.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_webDocument;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class ContentPreviewViewer {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ContentPreviewViewer Instance;
    private static TextPaint textPaint;
    /* access modifiers changed from: private */
    public boolean animateY;
    private ColorDrawable backgroundDrawable = new ColorDrawable(NUM);
    /* access modifiers changed from: private */
    public ImageReceiver centerImage = new ImageReceiver();
    /* access modifiers changed from: private */
    public boolean clearsInputField;
    /* access modifiers changed from: private */
    public FrameLayoutDrawer containerView;
    /* access modifiers changed from: private */
    public int currentAccount;
    /* access modifiers changed from: private */
    public int currentContentType;
    /* access modifiers changed from: private */
    public TLRPC$Document currentDocument;
    private float currentMoveY;
    /* access modifiers changed from: private */
    public float currentMoveYProgress;
    private View currentPreviewCell;
    /* access modifiers changed from: private */
    public String currentQuery;
    /* access modifiers changed from: private */
    public TLRPC$InputStickerSet currentStickerSet;
    /* access modifiers changed from: private */
    public ContentPreviewViewerDelegate delegate;
    /* access modifiers changed from: private */
    public float finalMoveY;
    /* access modifiers changed from: private */
    public SendMessagesHelper.ImportingSticker importingSticker;
    /* access modifiers changed from: private */
    public TLRPC$BotInlineResult inlineResult;
    /* access modifiers changed from: private */
    public boolean isRecentSticker;
    private boolean isVisible = false;
    private int keyboardHeight = AndroidUtilities.dp(200.0f);
    private WindowInsets lastInsets;
    private float lastTouchY;
    private long lastUpdateTime;
    /* access modifiers changed from: private */
    public float moveY = 0.0f;
    private Runnable openPreviewRunnable;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public Object parentObject;
    /* access modifiers changed from: private */
    public Theme.ResourcesProvider resourcesProvider;
    private float showProgress;
    private Runnable showSheetRunnable = new Runnable() {
        public void run() {
            boolean z;
            String str;
            int i;
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (ContentPreviewViewer.this.currentContentType == 0) {
                    boolean isStickerInFavorites = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).isStickerInFavorites(ContentPreviewViewer.this.currentDocument);
                    BottomSheet.Builder builder = new BottomSheet.Builder(ContentPreviewViewer.this.parentActivity, true, ContentPreviewViewer.this.resourcesProvider);
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    ArrayList arrayList3 = new ArrayList();
                    if (ContentPreviewViewer.this.delegate != null) {
                        if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                            arrayList.add(LocaleController.getString("SendStickerPreview", NUM));
                            arrayList3.add(NUM);
                            arrayList2.add(0);
                        }
                        if (!ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                            arrayList.add(LocaleController.getString("SendWithoutSound", NUM));
                            arrayList3.add(NUM);
                            arrayList2.add(6);
                        }
                        if (ContentPreviewViewer.this.delegate.canSchedule()) {
                            arrayList.add(LocaleController.getString("Schedule", NUM));
                            arrayList3.add(NUM);
                            arrayList2.add(3);
                        }
                        if (ContentPreviewViewer.this.currentStickerSet != null && ContentPreviewViewer.this.delegate.needOpen()) {
                            arrayList.add(LocaleController.formatString("ViewPackPreview", NUM, new Object[0]));
                            arrayList3.add(NUM);
                            arrayList2.add(1);
                        }
                        if (ContentPreviewViewer.this.delegate.needRemove()) {
                            arrayList.add(LocaleController.getString("ImportStickersRemoveMenu", NUM));
                            arrayList3.add(NUM);
                            arrayList2.add(5);
                        }
                    }
                    if (!MessageObject.isMaskDocument(ContentPreviewViewer.this.currentDocument) && (isStickerInFavorites || (MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).canAddStickerToFavorites() && MessageObject.isStickerHasSet(ContentPreviewViewer.this.currentDocument)))) {
                        if (isStickerInFavorites) {
                            i = NUM;
                            str = "DeleteFromFavorites";
                        } else {
                            i = NUM;
                            str = "AddToFavorites";
                        }
                        arrayList.add(LocaleController.getString(str, i));
                        arrayList3.add(Integer.valueOf(isStickerInFavorites ? NUM : NUM));
                        arrayList2.add(2);
                    }
                    if (ContentPreviewViewer.this.isRecentSticker) {
                        arrayList.add(LocaleController.getString("DeleteFromRecent", NUM));
                        arrayList3.add(NUM);
                        arrayList2.add(4);
                    }
                    if (!arrayList.isEmpty()) {
                        int[] iArr = new int[arrayList3.size()];
                        for (int i2 = 0; i2 < arrayList3.size(); i2++) {
                            iArr[i2] = ((Integer) arrayList3.get(i2)).intValue();
                        }
                        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), iArr, new ContentPreviewViewer$1$$ExternalSyntheticLambda1(this, arrayList2, isStickerInFavorites));
                        builder.setDimBehind(false);
                        BottomSheet unused = ContentPreviewViewer.this.visibleDialog = builder.create();
                        ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new ContentPreviewViewer$1$$ExternalSyntheticLambda3(this));
                        ContentPreviewViewer.this.visibleDialog.show();
                        ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                        if (ContentPreviewViewer.this.delegate != null && ContentPreviewViewer.this.delegate.needRemove()) {
                            BottomSheet.BottomSheetCell bottomSheetCell = ContentPreviewViewer.this.visibleDialog.getItemViews().get(0);
                            bottomSheetCell.setTextColor(ContentPreviewViewer.this.getThemedColor("dialogTextRed"));
                            bottomSheetCell.setIconColor(ContentPreviewViewer.this.getThemedColor("dialogRedIcon"));
                        }
                    }
                } else if (ContentPreviewViewer.this.delegate != null) {
                    boolean unused2 = ContentPreviewViewer.this.animateY = true;
                    BottomSheet unused3 = ContentPreviewViewer.this.visibleDialog = new BottomSheet(ContentPreviewViewer.this.parentActivity, false) {
                        /* access modifiers changed from: protected */
                        public void onContainerTranslationYChanged(float f) {
                            if (ContentPreviewViewer.this.animateY) {
                                getSheetContainer();
                                if (ContentPreviewViewer.this.finalMoveY == 0.0f) {
                                    float unused = ContentPreviewViewer.this.finalMoveY = 0.0f;
                                    ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
                                    float unused2 = contentPreviewViewer.startMoveY = contentPreviewViewer.moveY;
                                }
                                float unused3 = ContentPreviewViewer.this.currentMoveYProgress = 1.0f - Math.min(1.0f, f / ((float) this.containerView.getMeasuredHeight()));
                                ContentPreviewViewer contentPreviewViewer2 = ContentPreviewViewer.this;
                                float unused4 = contentPreviewViewer2.moveY = contentPreviewViewer2.startMoveY + ((ContentPreviewViewer.this.finalMoveY - ContentPreviewViewer.this.startMoveY) * ContentPreviewViewer.this.currentMoveYProgress);
                                ContentPreviewViewer.this.containerView.invalidate();
                                if (ContentPreviewViewer.this.currentMoveYProgress == 1.0f) {
                                    boolean unused5 = ContentPreviewViewer.this.animateY = false;
                                }
                            }
                        }
                    };
                    ArrayList arrayList4 = new ArrayList();
                    ArrayList arrayList5 = new ArrayList();
                    ArrayList arrayList6 = new ArrayList();
                    if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                        arrayList4.add(LocaleController.getString("SendGifPreview", NUM));
                        arrayList6.add(NUM);
                        arrayList5.add(0);
                    }
                    if (ContentPreviewViewer.this.delegate.canSchedule()) {
                        arrayList4.add(LocaleController.getString("Schedule", NUM));
                        arrayList6.add(NUM);
                        arrayList5.add(3);
                    }
                    if (ContentPreviewViewer.this.currentDocument != null) {
                        z = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).hasRecentGif(ContentPreviewViewer.this.currentDocument);
                        if (z) {
                            arrayList4.add(LocaleController.formatString("Delete", NUM, new Object[0]));
                            arrayList6.add(NUM);
                            arrayList5.add(1);
                        } else {
                            arrayList4.add(LocaleController.formatString("SaveToGIFs", NUM, new Object[0]));
                            arrayList6.add(NUM);
                            arrayList5.add(2);
                        }
                    } else {
                        z = false;
                    }
                    int[] iArr2 = new int[arrayList6.size()];
                    for (int i3 = 0; i3 < arrayList6.size(); i3++) {
                        iArr2[i3] = ((Integer) arrayList6.get(i3)).intValue();
                    }
                    ContentPreviewViewer.this.visibleDialog.setItems((CharSequence[]) arrayList4.toArray(new CharSequence[0]), iArr2, new ContentPreviewViewer$1$$ExternalSyntheticLambda0(this, arrayList5));
                    ContentPreviewViewer.this.visibleDialog.setDimBehind(false);
                    ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new ContentPreviewViewer$1$$ExternalSyntheticLambda2(this));
                    ContentPreviewViewer.this.visibleDialog.show();
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    if (z) {
                        ContentPreviewViewer.this.visibleDialog.setItemColor(arrayList4.size() - 1, ContentPreviewViewer.this.getThemedColor("dialogTextRed2"), ContentPreviewViewer.this.getThemedColor("dialogRedIcon"));
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$1(ArrayList arrayList, boolean z, DialogInterface dialogInterface, int i) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) arrayList.get(i)).intValue() == 0 || ((Integer) arrayList.get(i)).intValue() == 6) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.sendSticker(ContentPreviewViewer.this.currentDocument, ContentPreviewViewer.this.currentQuery, ContentPreviewViewer.this.parentObject, ((Integer) arrayList.get(i)).intValue() == 0, 0);
                    }
                } else if (((Integer) arrayList.get(i)).intValue() == 1) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.openSet(ContentPreviewViewer.this.currentStickerSet, ContentPreviewViewer.this.clearsInputField);
                    }
                } else if (((Integer) arrayList.get(i)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(2, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), z);
                } else if (((Integer) arrayList.get(i)).intValue() == 3) {
                    TLRPC$Document access$300 = ContentPreviewViewer.this.currentDocument;
                    Object access$1800 = ContentPreviewViewer.this.parentObject;
                    String access$1900 = ContentPreviewViewer.this.currentQuery;
                    ContentPreviewViewerDelegate access$600 = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, access$600.getDialogId(), new ContentPreviewViewer$1$$ExternalSyntheticLambda4(access$600, access$300, access$1900, access$1800));
                } else if (((Integer) arrayList.get(i)).intValue() == 4) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(0, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), true);
                } else if (((Integer) arrayList.get(i)).intValue() == 5) {
                    ContentPreviewViewer.this.delegate.remove(ContentPreviewViewer.this.importingSticker);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$2(DialogInterface dialogInterface) {
            BottomSheet unused = ContentPreviewViewer.this.visibleDialog = null;
            ContentPreviewViewer.this.close();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$4(ArrayList arrayList, DialogInterface dialogInterface, int i) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) arrayList.get(i)).intValue() == 0) {
                    ContentPreviewViewer.this.delegate.sendGif(ContentPreviewViewer.this.currentDocument != null ? ContentPreviewViewer.this.currentDocument : ContentPreviewViewer.this.inlineResult, ContentPreviewViewer.this.parentObject, true, 0);
                } else if (((Integer) arrayList.get(i)).intValue() == 1) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).removeRecentGif(ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) arrayList.get(i)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentGif(ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000));
                    MessagesController.getInstance(ContentPreviewViewer.this.currentAccount).saveGif("gif", ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) arrayList.get(i)).intValue() == 3) {
                    TLRPC$Document access$300 = ContentPreviewViewer.this.currentDocument;
                    TLRPC$BotInlineResult access$1700 = ContentPreviewViewer.this.inlineResult;
                    Object access$1800 = ContentPreviewViewer.this.parentObject;
                    ContentPreviewViewerDelegate access$600 = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog((Context) ContentPreviewViewer.this.parentActivity, access$600.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ContentPreviewViewer$1$$ExternalSyntheticLambda5(access$600, access$300, access$1700, access$1800), ContentPreviewViewer.this.resourcesProvider);
                }
            }
        }

        /* JADX WARNING: type inference failed for: r2v0, types: [org.telegram.tgnet.TLRPC$BotInlineResult] */
        /* access modifiers changed from: private */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static /* synthetic */ void lambda$run$3(org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r0, org.telegram.tgnet.TLRPC$Document r1, org.telegram.tgnet.TLRPC$BotInlineResult r2, java.lang.Object r3, boolean r4, int r5) {
            /*
                if (r1 == 0) goto L_0x0003
                goto L_0x0004
            L_0x0003:
                r1 = r2
            L_0x0004:
                r0.sendGif(r1, r3, r4, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.AnonymousClass1.lambda$run$3(org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, org.telegram.tgnet.TLRPC$Document, org.telegram.tgnet.TLRPC$BotInlineResult, java.lang.Object, boolean, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$5(DialogInterface dialogInterface) {
            BottomSheet unused = ContentPreviewViewer.this.visibleDialog = null;
            ContentPreviewViewer.this.close();
        }
    };
    private Drawable slideUpDrawable;
    /* access modifiers changed from: private */
    public float startMoveY;
    private int startX;
    private int startY;
    private StaticLayout stickerEmojiLayout;
    VibrationEffect vibrationEffect;
    /* access modifiers changed from: private */
    public BottomSheet visibleDialog;
    private WindowManager.LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    public interface ContentPreviewViewerDelegate {

        /* renamed from: org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static String $default$getQuery(ContentPreviewViewerDelegate contentPreviewViewerDelegate, boolean z) {
                return null;
            }

            public static void $default$gifAddedOrDeleted(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
            }

            public static boolean $default$needMenu(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
                return true;
            }

            public static boolean $default$needOpen(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
                return true;
            }

            public static boolean $default$needRemove(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
                return false;
            }

            public static void $default$remove(ContentPreviewViewerDelegate contentPreviewViewerDelegate, SendMessagesHelper.ImportingSticker importingSticker) {
            }

            public static void $default$sendGif(ContentPreviewViewerDelegate contentPreviewViewerDelegate, Object obj, Object obj2, boolean z, int i) {
            }
        }

        boolean canSchedule();

        long getDialogId();

        String getQuery(boolean z);

        void gifAddedOrDeleted();

        boolean isInScheduleMode();

        boolean needMenu();

        boolean needOpen();

        boolean needRemove();

        boolean needSend();

        void openSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z);

        void remove(SendMessagesHelper.ImportingSticker importingSticker);

        void sendGif(Object obj, Object obj2, boolean z, int i);

        void sendSticker(TLRPC$Document tLRPC$Document, String str, Object obj, boolean z, int i);
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            ContentPreviewViewer.this.onDraw(canvas);
        }
    }

    public static ContentPreviewViewer getInstance() {
        ContentPreviewViewer contentPreviewViewer = Instance;
        if (contentPreviewViewer == null) {
            synchronized (PhotoViewer.class) {
                contentPreviewViewer = Instance;
                if (contentPreviewViewer == null) {
                    contentPreviewViewer = new ContentPreviewViewer();
                    Instance = contentPreviewViewer;
                }
            }
        }
        return contentPreviewViewer;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public void reset() {
        Runnable runnable = this.openPreviewRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.openPreviewRunnable = null;
        }
        View view = this.currentPreviewCell;
        if (view != null) {
            if (view instanceof StickerEmojiCell) {
                ((StickerEmojiCell) view).setScaled(false);
            } else if (view instanceof StickerCell) {
                ((StickerCell) view).setScaled(false);
            } else if (view instanceof ContextLinkCell) {
                ((ContextLinkCell) view).setScaled(false);
            }
            this.currentPreviewCell = null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x0136  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0193  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.MotionEvent r16, org.telegram.ui.Components.RecyclerListView r17, int r18, java.lang.Object r19, org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r20, org.telegram.ui.ActionBar.Theme.ResourcesProvider r21) {
        /*
            r15 = this;
            r10 = r15
            r0 = r17
            r1 = r20
            r10.delegate = r1
            r9 = r21
            r10.resourcesProvider = r9
            java.lang.Runnable r1 = r10.openPreviewRunnable
            r2 = 0
            if (r1 != 0) goto L_0x0016
            boolean r1 = r15.isVisible()
            if (r1 == 0) goto L_0x028c
        L_0x0016:
            int r1 = r16.getAction()
            r3 = 0
            r11 = 1
            if (r1 == r11) goto L_0x024a
            int r1 = r16.getAction()
            r4 = 3
            if (r1 == r4) goto L_0x024a
            int r1 = r16.getAction()
            r4 = 6
            if (r1 != r4) goto L_0x002e
            goto L_0x024a
        L_0x002e:
            int r1 = r16.getAction()
            if (r1 == 0) goto L_0x028c
            boolean r1 = r10.isVisible
            r4 = 2
            if (r1 == 0) goto L_0x020f
            int r1 = r16.getAction()
            if (r1 != r4) goto L_0x020e
            int r1 = r10.currentContentType
            if (r1 != r11) goto L_0x00bb
            org.telegram.ui.ActionBar.BottomSheet r0 = r10.visibleDialog
            if (r0 != 0) goto L_0x00ba
            float r0 = r10.showProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x00ba
            float r0 = r10.lastTouchY
            r1 = -971227136(0xffffffffCLASSNAMECLASSNAME, float:-10000.0)
            r2 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x0064
            float r0 = r16.getY()
            r10.lastTouchY = r0
            r10.currentMoveY = r2
            r10.moveY = r2
            goto L_0x00ba
        L_0x0064:
            float r0 = r16.getY()
            float r1 = r10.currentMoveY
            float r3 = r10.lastTouchY
            float r3 = r0 - r3
            float r1 = r1 + r3
            r10.currentMoveY = r1
            r10.lastTouchY = r0
            int r0 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x007a
            r10.currentMoveY = r2
            goto L_0x008e
        L_0x007a:
            r0 = 1114636288(0x42700000, float:60.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = -r2
            float r2 = (float) r2
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 >= 0) goto L_0x008e
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            float r0 = (float) r0
            r10.currentMoveY = r0
        L_0x008e:
            float r0 = r10.currentMoveY
            r1 = 1128792064(0x43480000, float:200.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r0 = r15.rubberYPoisition(r0, r1)
            r10.moveY = r0
            org.telegram.ui.ContentPreviewViewer$FrameLayoutDrawer r0 = r10.containerView
            r0.invalidate()
            float r0 = r10.currentMoveY
            r1 = 1113325568(0x425CLASSNAME, float:55.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L_0x00ba
            java.lang.Runnable r0 = r10.showSheetRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            java.lang.Runnable r0 = r10.showSheetRunnable
            r0.run()
        L_0x00ba:
            return r11
        L_0x00bb:
            float r1 = r16.getX()
            int r1 = (int) r1
            float r4 = r16.getY()
            int r4 = (int) r4
            int r5 = r17.getChildCount()
            r6 = 0
        L_0x00ca:
            if (r6 >= r5) goto L_0x020e
            android.view.View r7 = r0.getChildAt(r6)
            if (r7 != 0) goto L_0x00d3
            return r2
        L_0x00d3:
            int r8 = r7.getTop()
            int r12 = r7.getBottom()
            int r13 = r7.getLeft()
            int r14 = r7.getRight()
            if (r8 > r4) goto L_0x0208
            if (r12 < r4) goto L_0x0208
            if (r13 > r1) goto L_0x0208
            if (r14 >= r1) goto L_0x00ed
            goto L_0x0208
        L_0x00ed:
            boolean r0 = r7 instanceof org.telegram.ui.Cells.StickerEmojiCell
            r1 = -1
            if (r0 == 0) goto L_0x00f9
            org.telegram.messenger.ImageReceiver r0 = r10.centerImage
            r0.setRoundRadius((int) r2)
        L_0x00f7:
            r12 = 0
            goto L_0x012a
        L_0x00f9:
            boolean r0 = r7 instanceof org.telegram.ui.Cells.StickerCell
            if (r0 == 0) goto L_0x0103
            org.telegram.messenger.ImageReceiver r0 = r10.centerImage
            r0.setRoundRadius((int) r2)
            goto L_0x00f7
        L_0x0103:
            boolean r0 = r7 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r0 == 0) goto L_0x0129
            r0 = r7
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            boolean r4 = r0.isSticker()
            if (r4 == 0) goto L_0x0116
            org.telegram.messenger.ImageReceiver r0 = r10.centerImage
            r0.setRoundRadius((int) r2)
            goto L_0x00f7
        L_0x0116:
            boolean r0 = r0.isGif()
            if (r0 == 0) goto L_0x0129
            org.telegram.messenger.ImageReceiver r0 = r10.centerImage
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.setRoundRadius((int) r4)
            r12 = 1
            goto L_0x012a
        L_0x0129:
            r12 = -1
        L_0x012a:
            if (r12 == r1) goto L_0x020e
            android.view.View r0 = r10.currentPreviewCell
            if (r7 != r0) goto L_0x0132
            goto L_0x020e
        L_0x0132:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x013c
            org.telegram.ui.Cells.StickerEmojiCell r0 = (org.telegram.ui.Cells.StickerEmojiCell) r0
            r0.setScaled(r2)
            goto L_0x014f
        L_0x013c:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x0146
            org.telegram.ui.Cells.StickerCell r0 = (org.telegram.ui.Cells.StickerCell) r0
            r0.setScaled(r2)
            goto L_0x014f
        L_0x0146:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x014f
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            r0.setScaled(r2)
        L_0x014f:
            r10.currentPreviewCell = r7
            r7 = r18
            r15.setKeyboardHeight(r7)
            r10.clearsInputField = r2
            android.view.View r0 = r10.currentPreviewCell
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x0193
            r13 = r0
            org.telegram.ui.Cells.StickerEmojiCell r13 = (org.telegram.ui.Cells.StickerEmojiCell) r13
            org.telegram.tgnet.TLRPC$Document r1 = r13.getSticker()
            org.telegram.messenger.SendMessagesHelper$ImportingSticker r4 = r13.getStickerPath()
            java.lang.String r5 = r13.getEmoji()
            org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate r0 = r10.delegate
            if (r0 == 0) goto L_0x0177
            java.lang.String r0 = r0.getQuery(r2)
            r6 = r0
            goto L_0x0178
        L_0x0177:
            r6 = r3
        L_0x0178:
            r7 = 0
            boolean r8 = r13.isRecent()
            java.lang.Object r14 = r13.getParentObject()
            r0 = r15
            r2 = r4
            r3 = r5
            r4 = r6
            r5 = r7
            r6 = r12
            r7 = r8
            r8 = r14
            r9 = r21
            r0.open(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            r13.setScaled(r11)
            goto L_0x0204
        L_0x0193:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x01c8
            r13 = r0
            org.telegram.ui.Cells.StickerCell r13 = (org.telegram.ui.Cells.StickerCell) r13
            org.telegram.tgnet.TLRPC$Document r1 = r13.getSticker()
            r4 = 0
            r5 = 0
            org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate r0 = r10.delegate
            if (r0 == 0) goto L_0x01aa
            java.lang.String r0 = r0.getQuery(r2)
            r6 = r0
            goto L_0x01ab
        L_0x01aa:
            r6 = r3
        L_0x01ab:
            r7 = 0
            r8 = 0
            java.lang.Object r14 = r13.getParentObject()
            r0 = r15
            r2 = r4
            r3 = r5
            r4 = r6
            r5 = r7
            r6 = r12
            r7 = r8
            r8 = r14
            r9 = r21
            r0.open(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            r13.setScaled(r11)
            boolean r0 = r13.isClearsInputField()
            r10.clearsInputField = r0
            goto L_0x0204
        L_0x01c8:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x0204
            r13 = r0
            org.telegram.ui.Cells.ContextLinkCell r13 = (org.telegram.ui.Cells.ContextLinkCell) r13
            org.telegram.tgnet.TLRPC$Document r1 = r13.getDocument()
            r2 = 0
            r4 = 0
            org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate r0 = r10.delegate
            if (r0 == 0) goto L_0x01df
            java.lang.String r0 = r0.getQuery(r11)
            r5 = r0
            goto L_0x01e0
        L_0x01df:
            r5 = r3
        L_0x01e0:
            org.telegram.tgnet.TLRPC$BotInlineResult r6 = r13.getBotInlineResult()
            r7 = 0
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r13.getBotInlineResult()
            if (r0 == 0) goto L_0x01f0
            org.telegram.tgnet.TLRPC$User r0 = r13.getInlineBot()
            goto L_0x01f4
        L_0x01f0:
            java.lang.Object r0 = r13.getParentObject()
        L_0x01f4:
            r8 = r0
            r0 = r15
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r12
            r9 = r21
            r0.open(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            if (r12 == r11) goto L_0x0204
            r13.setScaled(r11)
        L_0x0204:
            r15.runSmoothHaptic()
            return r11
        L_0x0208:
            r7 = r18
            int r6 = r6 + 1
            goto L_0x00ca
        L_0x020e:
            return r11
        L_0x020f:
            java.lang.Runnable r0 = r10.openPreviewRunnable
            if (r0 == 0) goto L_0x028c
            int r0 = r16.getAction()
            if (r0 != r4) goto L_0x0242
            int r0 = r10.startX
            float r0 = (float) r0
            float r1 = r16.getX()
            float r0 = r0 - r1
            double r0 = (double) r0
            int r4 = r10.startY
            float r4 = (float) r4
            float r5 = r16.getY()
            float r4 = r4 - r5
            double r4 = (double) r4
            double r0 = java.lang.Math.hypot(r0, r4)
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r4 = (double) r4
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x028c
            java.lang.Runnable r0 = r10.openPreviewRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r10.openPreviewRunnable = r3
            goto L_0x028c
        L_0x0242:
            java.lang.Runnable r0 = r10.openPreviewRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r10.openPreviewRunnable = r3
            goto L_0x028c
        L_0x024a:
            org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda2
            r4 = r19
            r1.<init>(r0, r4)
            r4 = 150(0x96, double:7.4E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r4)
            java.lang.Runnable r0 = r10.openPreviewRunnable
            if (r0 == 0) goto L_0x0260
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r10.openPreviewRunnable = r3
            goto L_0x028c
        L_0x0260:
            boolean r0 = r15.isVisible()
            if (r0 == 0) goto L_0x028c
            r15.close()
            android.view.View r0 = r10.currentPreviewCell
            if (r0 == 0) goto L_0x028c
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x0277
            org.telegram.ui.Cells.StickerEmojiCell r0 = (org.telegram.ui.Cells.StickerEmojiCell) r0
            r0.setScaled(r2)
            goto L_0x028a
        L_0x0277:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x0281
            org.telegram.ui.Cells.StickerCell r0 = (org.telegram.ui.Cells.StickerCell) r0
            r0.setScaled(r2)
            goto L_0x028a
        L_0x0281:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x028a
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            r0.setScaled(r2)
        L_0x028a:
            r10.currentPreviewCell = r3
        L_0x028c:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onTouch(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, java.lang.Object, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, org.telegram.ui.ActionBar.Theme$ResourcesProvider):boolean");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onTouch$0(RecyclerListView recyclerListView, Object obj) {
        if (recyclerListView instanceof RecyclerListView) {
            recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) obj);
        }
    }

    /* access modifiers changed from: protected */
    public void runSmoothHaptic() {
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator vibrator = (Vibrator) this.containerView.getContext().getSystemService("vibrator");
            if (this.vibrationEffect == null) {
                this.vibrationEffect = VibrationEffect.createWaveform(new long[]{0, 2}, -1);
            }
            vibrator.cancel();
            vibrator.vibrate(this.vibrationEffect);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x009b A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x009c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r14, org.telegram.ui.Components.RecyclerListView r15, int r16, org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r17, org.telegram.ui.ActionBar.Theme.ResourcesProvider r18) {
        /*
            r13 = this;
            r6 = r13
            r0 = r17
            r6.delegate = r0
            r5 = r18
            r6.resourcesProvider = r5
            int r0 = r14.getAction()
            r1 = 0
            if (r0 != 0) goto L_0x00bb
            float r0 = r14.getX()
            int r0 = (int) r0
            float r2 = r14.getY()
            int r2 = (int) r2
            int r3 = r15.getChildCount()
            r4 = 0
        L_0x001f:
            if (r4 >= r3) goto L_0x00bb
            r7 = r15
            android.view.View r8 = r15.getChildAt(r4)
            if (r8 != 0) goto L_0x0029
            return r1
        L_0x0029:
            int r9 = r8.getTop()
            int r10 = r8.getBottom()
            int r11 = r8.getLeft()
            int r12 = r8.getRight()
            if (r9 > r2) goto L_0x00b7
            if (r10 < r2) goto L_0x00b7
            if (r11 > r0) goto L_0x00b7
            if (r12 >= r0) goto L_0x0043
            goto L_0x00b7
        L_0x0043:
            boolean r3 = r8 instanceof org.telegram.ui.Cells.StickerEmojiCell
            r4 = -1
            r9 = 1
            if (r3 == 0) goto L_0x0059
            r3 = r8
            org.telegram.ui.Cells.StickerEmojiCell r3 = (org.telegram.ui.Cells.StickerEmojiCell) r3
            boolean r3 = r3.showingBitmap()
            if (r3 == 0) goto L_0x0098
            org.telegram.messenger.ImageReceiver r3 = r6.centerImage
            r3.setRoundRadius((int) r1)
        L_0x0057:
            r10 = 0
            goto L_0x0099
        L_0x0059:
            boolean r3 = r8 instanceof org.telegram.ui.Cells.StickerCell
            if (r3 == 0) goto L_0x006c
            r3 = r8
            org.telegram.ui.Cells.StickerCell r3 = (org.telegram.ui.Cells.StickerCell) r3
            boolean r3 = r3.showingBitmap()
            if (r3 == 0) goto L_0x0098
            org.telegram.messenger.ImageReceiver r3 = r6.centerImage
            r3.setRoundRadius((int) r1)
            goto L_0x0057
        L_0x006c:
            boolean r3 = r8 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r3 == 0) goto L_0x0098
            r3 = r8
            org.telegram.ui.Cells.ContextLinkCell r3 = (org.telegram.ui.Cells.ContextLinkCell) r3
            boolean r10 = r3.showingBitmap()
            if (r10 == 0) goto L_0x0098
            boolean r10 = r3.isSticker()
            if (r10 == 0) goto L_0x0085
            org.telegram.messenger.ImageReceiver r3 = r6.centerImage
            r3.setRoundRadius((int) r1)
            goto L_0x0057
        L_0x0085:
            boolean r3 = r3.isGif()
            if (r3 == 0) goto L_0x0098
            org.telegram.messenger.ImageReceiver r3 = r6.centerImage
            r10 = 1086324736(0x40CLASSNAME, float:6.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r3.setRoundRadius((int) r10)
            r10 = 1
            goto L_0x0099
        L_0x0098:
            r10 = -1
        L_0x0099:
            if (r10 != r4) goto L_0x009c
            return r1
        L_0x009c:
            r6.startX = r0
            r6.startY = r2
            r6.currentPreviewCell = r8
            org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda4 r8 = new org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda4
            r0 = r8
            r1 = r13
            r2 = r15
            r3 = r16
            r4 = r10
            r5 = r18
            r0.<init>(r1, r2, r3, r4, r5)
            r6.openPreviewRunnable = r8
            r0 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r0)
            return r9
        L_0x00b7:
            int r4 = r4 + 1
            goto L_0x001f
        L_0x00bb:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onInterceptTouchEvent(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, org.telegram.ui.ActionBar.Theme$ResourcesProvider):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onInterceptTouchEvent$1(RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider2) {
        RecyclerListView recyclerListView2 = recyclerListView;
        if (this.openPreviewRunnable != null) {
            recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) null);
            recyclerListView.requestDisallowInterceptTouchEvent(true);
            this.openPreviewRunnable = null;
            setParentActivity((Activity) recyclerListView.getContext());
            setKeyboardHeight(i);
            this.clearsInputField = false;
            View view = this.currentPreviewCell;
            if (view instanceof StickerEmojiCell) {
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
                TLRPC$Document sticker = stickerEmojiCell.getSticker();
                SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
                String emoji = stickerEmojiCell.getEmoji();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate = this.delegate;
                open(sticker, stickerPath, emoji, contentPreviewViewerDelegate != null ? contentPreviewViewerDelegate.getQuery(false) : null, (TLRPC$BotInlineResult) null, i2, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject(), resourcesProvider2);
                stickerEmojiCell.setScaled(true);
            } else if (view instanceof StickerCell) {
                StickerCell stickerCell = (StickerCell) view;
                TLRPC$Document sticker2 = stickerCell.getSticker();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate2 = this.delegate;
                open(sticker2, (SendMessagesHelper.ImportingSticker) null, (String) null, contentPreviewViewerDelegate2 != null ? contentPreviewViewerDelegate2.getQuery(false) : null, (TLRPC$BotInlineResult) null, i2, false, stickerCell.getParentObject(), resourcesProvider2);
                stickerCell.setScaled(true);
                this.clearsInputField = stickerCell.isClearsInputField();
            } else if (view instanceof ContextLinkCell) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) view;
                TLRPC$Document document = contextLinkCell.getDocument();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate3 = this.delegate;
                open(document, (SendMessagesHelper.ImportingSticker) null, (String) null, contentPreviewViewerDelegate3 != null ? contentPreviewViewerDelegate3.getQuery(true) : null, contextLinkCell.getBotInlineResult(), i2, false, contextLinkCell.getBotInlineResult() != null ? contextLinkCell.getInlineBot() : contextLinkCell.getParentObject(), resourcesProvider2);
                if (i2 != 1) {
                    contextLinkCell.setScaled(true);
                }
            }
            this.currentPreviewCell.performHapticFeedback(0, 2);
        }
    }

    public void setParentActivity(Activity activity) {
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.centerImage.setCurrentAccount(i);
        this.centerImage.setLayerNum(Integer.MAX_VALUE);
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.slideUpDrawable = activity.getResources().getDrawable(NUM);
            FrameLayout frameLayout = new FrameLayout(activity);
            this.windowView = frameLayout;
            frameLayout.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            int i2 = Build.VERSION.SDK_INT;
            if (i2 >= 21) {
                this.windowView.setFitsSystemWindows(true);
                this.windowView.setOnApplyWindowInsetsListener(new ContentPreviewViewer$$ExternalSyntheticLambda0(this));
            }
            AnonymousClass2 r3 = new FrameLayoutDrawer(activity) {
                /* access modifiers changed from: protected */
                public void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    ContentPreviewViewer.this.centerImage.onAttachedToWindow();
                }

                /* access modifiers changed from: protected */
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    ContentPreviewViewer.this.centerImage.onDetachedFromWindow();
                }
            };
            this.containerView = r3;
            r3.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new ContentPreviewViewer$$ExternalSyntheticLambda1(this));
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            layoutParams.height = -1;
            layoutParams.format = -3;
            layoutParams.width = -1;
            layoutParams.gravity = 48;
            layoutParams.type = 99;
            if (i2 >= 21) {
                layoutParams.flags = -NUM;
            } else {
                layoutParams.flags = 8;
            }
            this.centerImage.setAspectFit(true);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setParentView(this.containerView);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ WindowInsets lambda$setParentActivity$2(View view, WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
        return windowInsets;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setParentActivity$3(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 6 || motionEvent.getAction() == 3) {
            close();
        }
        return true;
    }

    public void setKeyboardHeight(int i) {
        this.keyboardHeight = i;
    }

    public void open(TLRPC$Document tLRPC$Document, SendMessagesHelper.ImportingSticker importingSticker2, String str, String str2, TLRPC$BotInlineResult tLRPC$BotInlineResult, int i, boolean z, Object obj, Theme.ResourcesProvider resourcesProvider2) {
        TLRPC$InputStickerSet tLRPC$InputStickerSet;
        ContentPreviewViewerDelegate contentPreviewViewerDelegate;
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        SendMessagesHelper.ImportingSticker importingSticker3 = importingSticker2;
        String str3 = str;
        TLRPC$BotInlineResult tLRPC$BotInlineResult2 = tLRPC$BotInlineResult;
        int i2 = i;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        if (this.parentActivity != null && this.windowView != null) {
            this.resourcesProvider = resourcesProvider3;
            this.isRecentSticker = z;
            this.stickerEmojiLayout = null;
            if (i2 != 0) {
                if (tLRPC$Document2 != null) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90);
                    TLRPC$VideoSize documentVideoThumb = MessageObject.getDocumentVideoThumb(tLRPC$Document);
                    ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$Document);
                    forDocument.imageType = 2;
                    if (documentVideoThumb != null) {
                        this.centerImage.setImage(forDocument, (String) null, ImageLocation.getForDocument(documentVideoThumb, tLRPC$Document2), (String) null, ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document2), "90_90_b", (Drawable) null, tLRPC$Document2.size, (String) null, "gif" + tLRPC$Document2, 0);
                    } else {
                        this.centerImage.setImage(forDocument, (String) null, ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document2), "90_90_b", tLRPC$Document2.size, (String) null, "gif" + tLRPC$Document2, 0);
                    }
                } else if (tLRPC$BotInlineResult2 != null && tLRPC$BotInlineResult2.content != null) {
                    TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult2.thumb;
                    if (!(tLRPC$WebDocument instanceof TLRPC$TL_webDocument) || !"video/mp4".equals(tLRPC$WebDocument.mime_type)) {
                        this.centerImage.setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(tLRPC$BotInlineResult2.content)), (String) null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(tLRPC$BotInlineResult2.thumb)), "90_90_b", tLRPC$BotInlineResult2.content.size, (String) null, "gif" + tLRPC$BotInlineResult2, 1);
                    } else {
                        this.centerImage.setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(tLRPC$BotInlineResult2.content)), (String) null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(tLRPC$BotInlineResult2.thumb)), (String) null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(tLRPC$BotInlineResult2.thumb)), "90_90_b", (Drawable) null, tLRPC$BotInlineResult2.content.size, (String) null, "gif" + tLRPC$BotInlineResult2, 1);
                    }
                } else {
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                AndroidUtilities.runOnUIThread(this.showSheetRunnable, 2000);
            } else if (tLRPC$Document2 != null || importingSticker3 != null) {
                if (textPaint == null) {
                    TextPaint textPaint2 = new TextPaint(1);
                    textPaint = textPaint2;
                    textPaint2.setTextSize((float) AndroidUtilities.dp(24.0f));
                }
                if (tLRPC$Document2 != null) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= tLRPC$Document2.attributes.size()) {
                            tLRPC$InputStickerSet = null;
                            break;
                        }
                        TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document2.attributes.get(i3);
                        if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) && (tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset) != null) {
                            break;
                        }
                        i3++;
                    }
                    if (tLRPC$InputStickerSet != null && ((contentPreviewViewerDelegate = this.delegate) == null || contentPreviewViewerDelegate.needMenu())) {
                        try {
                            BottomSheet bottomSheet = this.visibleDialog;
                            if (bottomSheet != null) {
                                bottomSheet.setOnDismissListener((DialogInterface.OnDismissListener) null);
                                this.visibleDialog.dismiss();
                                this.visibleDialog = null;
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 1300);
                    }
                    this.currentStickerSet = tLRPC$InputStickerSet;
                    TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90);
                    if (MessageObject.isVideoStickerDocument(tLRPC$Document)) {
                        this.centerImage.setImage(ImageLocation.getForDocument(tLRPC$Document), (String) null, ImageLocation.getForDocument(closestPhotoSizeWithSize2, tLRPC$Document2), (String) null, (Drawable) null, 0, "webp", this.currentStickerSet, 1);
                    } else {
                        this.centerImage.setImage(ImageLocation.getForDocument(tLRPC$Document), (String) null, ImageLocation.getForDocument(closestPhotoSizeWithSize2, tLRPC$Document2), (String) null, "webp", (Object) this.currentStickerSet, 1);
                    }
                    int i4 = 0;
                    while (true) {
                        if (i4 >= tLRPC$Document2.attributes.size()) {
                            break;
                        }
                        TLRPC$DocumentAttribute tLRPC$DocumentAttribute2 = tLRPC$Document2.attributes.get(i4);
                        if ((tLRPC$DocumentAttribute2 instanceof TLRPC$TL_documentAttributeSticker) && !TextUtils.isEmpty(tLRPC$DocumentAttribute2.alt)) {
                            this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(tLRPC$DocumentAttribute2.alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            break;
                        }
                        i4++;
                    }
                } else if (importingSticker3 != null) {
                    this.centerImage.setImage(importingSticker3.path, (String) null, (Drawable) null, importingSticker3.animated ? "tgs" : null, 0);
                    if (str3 != null) {
                        this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(str3, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    }
                    if (this.delegate.needMenu()) {
                        try {
                            BottomSheet bottomSheet2 = this.visibleDialog;
                            if (bottomSheet2 != null) {
                                bottomSheet2.setOnDismissListener((DialogInterface.OnDismissListener) null);
                                this.visibleDialog.dismiss();
                                this.visibleDialog = null;
                            }
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 1300);
                    }
                }
            } else {
                return;
            }
            this.currentContentType = i2;
            this.currentDocument = tLRPC$Document2;
            this.importingSticker = importingSticker3;
            this.currentQuery = str2;
            this.inlineResult = tLRPC$BotInlineResult2;
            this.parentObject = obj;
            this.resourcesProvider = resourcesProvider3;
            this.containerView.invalidate();
            if (!this.isVisible) {
                AndroidUtilities.lockOrientation(this.parentActivity);
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                    }
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
                ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
                this.isVisible = true;
                this.showProgress = 0.0f;
                this.lastTouchY = -10000.0f;
                this.currentMoveYProgress = 0.0f;
                this.finalMoveY = 0.0f;
                this.currentMoveY = 0.0f;
                this.moveY = 0.0f;
                this.lastUpdateTime = System.currentTimeMillis();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 8);
            }
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void close() {
        if (this.parentActivity != null && this.visibleDialog == null) {
            AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
            this.showProgress = 1.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            this.containerView.invalidate();
            try {
                BottomSheet bottomSheet = this.visibleDialog;
                if (bottomSheet != null) {
                    bottomSheet.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.currentDocument = null;
            this.currentStickerSet = null;
            this.currentQuery = null;
            this.delegate = null;
            this.isVisible = false;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 8);
        }
    }

    public void destroy() {
        FrameLayout frameLayout;
        this.isVisible = false;
        this.delegate = null;
        this.currentDocument = null;
        this.currentQuery = null;
        this.currentStickerSet = null;
        try {
            BottomSheet bottomSheet = this.visibleDialog;
            if (bottomSheet != null) {
                bottomSheet.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (this.parentActivity != null && (frameLayout = this.windowView) != null) {
            try {
                if (frameLayout.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            Instance = null;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 8);
        }
    }

    private float rubberYPoisition(float f, float f2) {
        float f3 = 1.0f;
        float f4 = -((1.0f - (1.0f / (((Math.abs(f) * 0.55f) / f2) + 1.0f))) * f2);
        if (f >= 0.0f) {
            f3 = -1.0f;
        }
        return f4 * f3;
    }

    /* access modifiers changed from: private */
    @SuppressLint({"DrawAllocation"})
    public void onDraw(Canvas canvas) {
        ColorDrawable colorDrawable;
        int i;
        int i2;
        int i3;
        Drawable drawable;
        WindowInsets windowInsets;
        if (this.containerView != null && (colorDrawable = this.backgroundDrawable) != null) {
            colorDrawable.setAlpha((int) (this.showProgress * 180.0f));
            int i4 = 0;
            this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
            this.backgroundDrawable.draw(canvas);
            canvas.save();
            if (Build.VERSION.SDK_INT < 21 || (windowInsets = this.lastInsets) == null) {
                i = AndroidUtilities.statusBarHeight;
                i2 = 0;
            } else {
                i2 = windowInsets.getStableInsetBottom() + this.lastInsets.getStableInsetTop();
                i = this.lastInsets.getStableInsetTop();
            }
            if (this.currentContentType == 1) {
                i3 = Math.min(this.containerView.getWidth(), this.containerView.getHeight() - i2) - AndroidUtilities.dp(40.0f);
            } else {
                i3 = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight() - i2)) / 1.8f);
            }
            float width = (float) (this.containerView.getWidth() / 2);
            float f = this.moveY;
            int i5 = (i3 / 2) + i;
            if (this.stickerEmojiLayout != null) {
                i4 = AndroidUtilities.dp(40.0f);
            }
            canvas.translate(width, f + ((float) Math.max(i5 + i4, ((this.containerView.getHeight() - i2) - this.keyboardHeight) / 2)));
            float f2 = this.showProgress;
            int i6 = (int) (((float) i3) * ((f2 * 0.8f) / 0.8f));
            this.centerImage.setAlpha(f2);
            float f3 = (float) ((-i6) / 2);
            float f4 = (float) i6;
            this.centerImage.setImageCoords(f3, f3, f4, f4);
            this.centerImage.draw(canvas);
            if (this.currentContentType == 1 && (drawable = this.slideUpDrawable) != null) {
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = this.slideUpDrawable.getIntrinsicHeight();
                int dp = (int) (this.centerImage.getDrawRegion().top - ((float) AndroidUtilities.dp(((this.currentMoveY / ((float) AndroidUtilities.dp(60.0f))) * 6.0f) + 17.0f)));
                this.slideUpDrawable.setAlpha((int) ((1.0f - this.currentMoveYProgress) * 255.0f));
                this.slideUpDrawable.setBounds((-intrinsicWidth) / 2, (-intrinsicHeight) + dp, intrinsicWidth / 2, dp);
                this.slideUpDrawable.draw(canvas);
            }
            if (this.stickerEmojiLayout != null) {
                canvas.translate((float) (-AndroidUtilities.dp(50.0f)), ((-this.centerImage.getImageHeight()) / 2.0f) - ((float) AndroidUtilities.dp(30.0f)));
                this.stickerEmojiLayout.draw(canvas);
            }
            canvas.restore();
            if (this.isVisible) {
                if (this.showProgress != 1.0f) {
                    long currentTimeMillis = System.currentTimeMillis();
                    this.lastUpdateTime = currentTimeMillis;
                    this.showProgress += ((float) (currentTimeMillis - this.lastUpdateTime)) / 120.0f;
                    this.containerView.invalidate();
                    if (this.showProgress > 1.0f) {
                        this.showProgress = 1.0f;
                    }
                }
            } else if (this.showProgress != 0.0f) {
                long currentTimeMillis2 = System.currentTimeMillis();
                this.lastUpdateTime = currentTimeMillis2;
                this.showProgress -= ((float) (currentTimeMillis2 - this.lastUpdateTime)) / 120.0f;
                this.containerView.invalidate();
                if (this.showProgress < 0.0f) {
                    this.showProgress = 0.0f;
                }
                if (this.showProgress == 0.0f) {
                    this.centerImage.setImageBitmap((Drawable) null);
                    AndroidUtilities.unlockOrientation(this.parentActivity);
                    AndroidUtilities.runOnUIThread(new ContentPreviewViewer$$ExternalSyntheticLambda3(this));
                    try {
                        if (this.windowView.getParent() != null) {
                            ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDraw$4() {
        this.centerImage.setImageBitmap((Bitmap) null);
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
