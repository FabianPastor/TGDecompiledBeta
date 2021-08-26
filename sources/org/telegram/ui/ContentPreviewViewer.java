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
    private ImageReceiver centerImage = new ImageReceiver();
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
    private float showProgress;
    private Runnable showSheetRunnable = new Runnable() {
        public void run() {
            boolean z;
            String str;
            int i;
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (ContentPreviewViewer.this.currentContentType == 0) {
                    boolean isStickerInFavorites = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).isStickerInFavorites(ContentPreviewViewer.this.currentDocument);
                    BottomSheet.Builder builder = new BottomSheet.Builder(ContentPreviewViewer.this.parentActivity);
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    ArrayList arrayList3 = new ArrayList();
                    if (ContentPreviewViewer.this.delegate != null) {
                        if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                            arrayList.add(LocaleController.getString("SendStickerPreview", NUM));
                            arrayList3.add(NUM);
                            arrayList2.add(0);
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
                            bottomSheetCell.setTextColor(Theme.getColor("dialogTextRed"));
                            bottomSheetCell.setIconColor(Theme.getColor("dialogRedIcon"));
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
                        ContentPreviewViewer.this.visibleDialog.setItemColor(arrayList4.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$1(ArrayList arrayList, boolean z, DialogInterface dialogInterface, int i) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) arrayList.get(i)).intValue() == 0) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.sendSticker(ContentPreviewViewer.this.currentDocument, ContentPreviewViewer.this.currentQuery, ContentPreviewViewer.this.parentObject, true, 0);
                    }
                } else if (((Integer) arrayList.get(i)).intValue() == 1) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.openSet(ContentPreviewViewer.this.currentStickerSet, ContentPreviewViewer.this.clearsInputField);
                    }
                } else if (((Integer) arrayList.get(i)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(2, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), z);
                } else if (((Integer) arrayList.get(i)).intValue() == 3) {
                    TLRPC$Document access$300 = ContentPreviewViewer.this.currentDocument;
                    Object access$1600 = ContentPreviewViewer.this.parentObject;
                    String access$1700 = ContentPreviewViewer.this.currentQuery;
                    ContentPreviewViewerDelegate access$500 = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, access$500.getDialogId(), new ContentPreviewViewer$1$$ExternalSyntheticLambda4(access$500, access$300, access$1700, access$1600));
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
                    TLRPC$BotInlineResult access$1500 = ContentPreviewViewer.this.inlineResult;
                    Object access$1600 = ContentPreviewViewer.this.parentObject;
                    ContentPreviewViewerDelegate access$500 = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, access$500.getDialogId(), new ContentPreviewViewer$1$$ExternalSyntheticLambda5(access$500, access$300, access$1500, access$1600));
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

    /* JADX WARNING: Removed duplicated region for block: B:66:0x0132  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0138  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x018d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.MotionEvent r15, org.telegram.ui.Components.RecyclerListView r16, int r17, java.lang.Object r18, org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r19) {
        /*
            r14 = this;
            r9 = r14
            r0 = r16
            r1 = r19
            r9.delegate = r1
            java.lang.Runnable r1 = r9.openPreviewRunnable
            r2 = 0
            if (r1 != 0) goto L_0x0012
            boolean r1 = r14.isVisible()
            if (r1 == 0) goto L_0x0282
        L_0x0012:
            int r1 = r15.getAction()
            r3 = 0
            r10 = 1
            if (r1 == r10) goto L_0x0240
            int r1 = r15.getAction()
            r4 = 3
            if (r1 == r4) goto L_0x0240
            int r1 = r15.getAction()
            r4 = 6
            if (r1 != r4) goto L_0x002a
            goto L_0x0240
        L_0x002a:
            int r1 = r15.getAction()
            if (r1 == 0) goto L_0x0282
            boolean r1 = r9.isVisible
            r4 = 2
            if (r1 == 0) goto L_0x0205
            int r1 = r15.getAction()
            if (r1 != r4) goto L_0x0204
            int r1 = r9.currentContentType
            if (r1 != r10) goto L_0x00b7
            org.telegram.ui.ActionBar.BottomSheet r0 = r9.visibleDialog
            if (r0 != 0) goto L_0x00b6
            float r0 = r9.showProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x00b6
            float r0 = r9.lastTouchY
            r1 = -971227136(0xffffffffCLASSNAMECLASSNAME, float:-10000.0)
            r2 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x0060
            float r0 = r15.getY()
            r9.lastTouchY = r0
            r9.currentMoveY = r2
            r9.moveY = r2
            goto L_0x00b6
        L_0x0060:
            float r0 = r15.getY()
            float r1 = r9.currentMoveY
            float r3 = r9.lastTouchY
            float r3 = r0 - r3
            float r1 = r1 + r3
            r9.currentMoveY = r1
            r9.lastTouchY = r0
            int r0 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x0076
            r9.currentMoveY = r2
            goto L_0x008a
        L_0x0076:
            r0 = 1114636288(0x42700000, float:60.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = -r2
            float r2 = (float) r2
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 >= 0) goto L_0x008a
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            float r0 = (float) r0
            r9.currentMoveY = r0
        L_0x008a:
            float r0 = r9.currentMoveY
            r1 = 1128792064(0x43480000, float:200.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r0 = r14.rubberYPoisition(r0, r1)
            r9.moveY = r0
            org.telegram.ui.ContentPreviewViewer$FrameLayoutDrawer r0 = r9.containerView
            r0.invalidate()
            float r0 = r9.currentMoveY
            r1 = 1113325568(0x425CLASSNAME, float:55.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L_0x00b6
            java.lang.Runnable r0 = r9.showSheetRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            java.lang.Runnable r0 = r9.showSheetRunnable
            r0.run()
        L_0x00b6:
            return r10
        L_0x00b7:
            float r1 = r15.getX()
            int r1 = (int) r1
            float r4 = r15.getY()
            int r4 = (int) r4
            int r5 = r16.getChildCount()
            r6 = 0
        L_0x00c6:
            if (r6 >= r5) goto L_0x0204
            android.view.View r7 = r0.getChildAt(r6)
            if (r7 != 0) goto L_0x00cf
            return r2
        L_0x00cf:
            int r8 = r7.getTop()
            int r11 = r7.getBottom()
            int r12 = r7.getLeft()
            int r13 = r7.getRight()
            if (r8 > r4) goto L_0x01fe
            if (r11 < r4) goto L_0x01fe
            if (r12 > r1) goto L_0x01fe
            if (r13 >= r1) goto L_0x00e9
            goto L_0x01fe
        L_0x00e9:
            boolean r0 = r7 instanceof org.telegram.ui.Cells.StickerEmojiCell
            r1 = -1
            if (r0 == 0) goto L_0x00f5
            org.telegram.messenger.ImageReceiver r0 = r9.centerImage
            r0.setRoundRadius((int) r2)
        L_0x00f3:
            r11 = 0
            goto L_0x0126
        L_0x00f5:
            boolean r0 = r7 instanceof org.telegram.ui.Cells.StickerCell
            if (r0 == 0) goto L_0x00ff
            org.telegram.messenger.ImageReceiver r0 = r9.centerImage
            r0.setRoundRadius((int) r2)
            goto L_0x00f3
        L_0x00ff:
            boolean r0 = r7 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r0 == 0) goto L_0x0125
            r0 = r7
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            boolean r4 = r0.isSticker()
            if (r4 == 0) goto L_0x0112
            org.telegram.messenger.ImageReceiver r0 = r9.centerImage
            r0.setRoundRadius((int) r2)
            goto L_0x00f3
        L_0x0112:
            boolean r0 = r0.isGif()
            if (r0 == 0) goto L_0x0125
            org.telegram.messenger.ImageReceiver r0 = r9.centerImage
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.setRoundRadius((int) r4)
            r11 = 1
            goto L_0x0126
        L_0x0125:
            r11 = -1
        L_0x0126:
            if (r11 == r1) goto L_0x0204
            android.view.View r0 = r9.currentPreviewCell
            if (r7 != r0) goto L_0x012e
            goto L_0x0204
        L_0x012e:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x0138
            org.telegram.ui.Cells.StickerEmojiCell r0 = (org.telegram.ui.Cells.StickerEmojiCell) r0
            r0.setScaled(r2)
            goto L_0x014b
        L_0x0138:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x0142
            org.telegram.ui.Cells.StickerCell r0 = (org.telegram.ui.Cells.StickerCell) r0
            r0.setScaled(r2)
            goto L_0x014b
        L_0x0142:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x014b
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            r0.setScaled(r2)
        L_0x014b:
            r9.currentPreviewCell = r7
            r7 = r17
            r14.setKeyboardHeight(r7)
            r9.clearsInputField = r2
            android.view.View r0 = r9.currentPreviewCell
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x018d
            r12 = r0
            org.telegram.ui.Cells.StickerEmojiCell r12 = (org.telegram.ui.Cells.StickerEmojiCell) r12
            org.telegram.tgnet.TLRPC$Document r1 = r12.getSticker()
            org.telegram.messenger.SendMessagesHelper$ImportingSticker r4 = r12.getStickerPath()
            java.lang.String r5 = r12.getEmoji()
            org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate r0 = r9.delegate
            if (r0 == 0) goto L_0x0173
            java.lang.String r0 = r0.getQuery(r2)
            r6 = r0
            goto L_0x0174
        L_0x0173:
            r6 = r3
        L_0x0174:
            r7 = 0
            boolean r8 = r12.isRecent()
            java.lang.Object r13 = r12.getParentObject()
            r0 = r14
            r2 = r4
            r3 = r5
            r4 = r6
            r5 = r7
            r6 = r11
            r7 = r8
            r8 = r13
            r0.open(r1, r2, r3, r4, r5, r6, r7, r8)
            r12.setScaled(r10)
            goto L_0x01fa
        L_0x018d:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x01c0
            r12 = r0
            org.telegram.ui.Cells.StickerCell r12 = (org.telegram.ui.Cells.StickerCell) r12
            org.telegram.tgnet.TLRPC$Document r1 = r12.getSticker()
            r4 = 0
            r5 = 0
            org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate r0 = r9.delegate
            if (r0 == 0) goto L_0x01a4
            java.lang.String r0 = r0.getQuery(r2)
            r6 = r0
            goto L_0x01a5
        L_0x01a4:
            r6 = r3
        L_0x01a5:
            r7 = 0
            r8 = 0
            java.lang.Object r13 = r12.getParentObject()
            r0 = r14
            r2 = r4
            r3 = r5
            r4 = r6
            r5 = r7
            r6 = r11
            r7 = r8
            r8 = r13
            r0.open(r1, r2, r3, r4, r5, r6, r7, r8)
            r12.setScaled(r10)
            boolean r0 = r12.isClearsInputField()
            r9.clearsInputField = r0
            goto L_0x01fa
        L_0x01c0:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x01fa
            r12 = r0
            org.telegram.ui.Cells.ContextLinkCell r12 = (org.telegram.ui.Cells.ContextLinkCell) r12
            org.telegram.tgnet.TLRPC$Document r1 = r12.getDocument()
            r2 = 0
            r4 = 0
            org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate r0 = r9.delegate
            if (r0 == 0) goto L_0x01d7
            java.lang.String r0 = r0.getQuery(r10)
            r5 = r0
            goto L_0x01d8
        L_0x01d7:
            r5 = r3
        L_0x01d8:
            org.telegram.tgnet.TLRPC$BotInlineResult r6 = r12.getBotInlineResult()
            r7 = 0
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r12.getBotInlineResult()
            if (r0 == 0) goto L_0x01e8
            org.telegram.tgnet.TLRPC$User r0 = r12.getInlineBot()
            goto L_0x01ec
        L_0x01e8:
            java.lang.Object r0 = r12.getParentObject()
        L_0x01ec:
            r8 = r0
            r0 = r14
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r11
            r0.open(r1, r2, r3, r4, r5, r6, r7, r8)
            if (r11 == r10) goto L_0x01fa
            r12.setScaled(r10)
        L_0x01fa:
            r14.runSmoothHaptic()
            return r10
        L_0x01fe:
            r7 = r17
            int r6 = r6 + 1
            goto L_0x00c6
        L_0x0204:
            return r10
        L_0x0205:
            java.lang.Runnable r0 = r9.openPreviewRunnable
            if (r0 == 0) goto L_0x0282
            int r0 = r15.getAction()
            if (r0 != r4) goto L_0x0238
            int r0 = r9.startX
            float r0 = (float) r0
            float r1 = r15.getX()
            float r0 = r0 - r1
            double r0 = (double) r0
            int r4 = r9.startY
            float r4 = (float) r4
            float r5 = r15.getY()
            float r4 = r4 - r5
            double r4 = (double) r4
            double r0 = java.lang.Math.hypot(r0, r4)
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r4 = (double) r4
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x0282
            java.lang.Runnable r0 = r9.openPreviewRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r9.openPreviewRunnable = r3
            goto L_0x0282
        L_0x0238:
            java.lang.Runnable r0 = r9.openPreviewRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r9.openPreviewRunnable = r3
            goto L_0x0282
        L_0x0240:
            org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda2
            r4 = r18
            r1.<init>(r0, r4)
            r4 = 150(0x96, double:7.4E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r4)
            java.lang.Runnable r0 = r9.openPreviewRunnable
            if (r0 == 0) goto L_0x0256
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r9.openPreviewRunnable = r3
            goto L_0x0282
        L_0x0256:
            boolean r0 = r14.isVisible()
            if (r0 == 0) goto L_0x0282
            r14.close()
            android.view.View r0 = r9.currentPreviewCell
            if (r0 == 0) goto L_0x0282
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x026d
            org.telegram.ui.Cells.StickerEmojiCell r0 = (org.telegram.ui.Cells.StickerEmojiCell) r0
            r0.setScaled(r2)
            goto L_0x0280
        L_0x026d:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x0277
            org.telegram.ui.Cells.StickerCell r0 = (org.telegram.ui.Cells.StickerCell) r0
            r0.setScaled(r2)
            goto L_0x0280
        L_0x0277:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x0280
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            r0.setScaled(r2)
        L_0x0280:
            r9.currentPreviewCell = r3
        L_0x0282:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onTouch(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, java.lang.Object, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate):boolean");
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

    /* JADX WARNING: Removed duplicated region for block: B:35:0x0093 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0094  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r9, org.telegram.ui.Components.RecyclerListView r10, int r11, org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r12) {
        /*
            r8 = this;
            r8.delegate = r12
            int r12 = r9.getAction()
            r0 = 0
            if (r12 != 0) goto L_0x00ab
            float r12 = r9.getX()
            int r12 = (int) r12
            float r9 = r9.getY()
            int r9 = (int) r9
            int r1 = r10.getChildCount()
            r2 = 0
        L_0x0018:
            if (r2 >= r1) goto L_0x00ab
            android.view.View r3 = r10.getChildAt(r2)
            if (r3 != 0) goto L_0x0021
            return r0
        L_0x0021:
            int r4 = r3.getTop()
            int r5 = r3.getBottom()
            int r6 = r3.getLeft()
            int r7 = r3.getRight()
            if (r4 > r9) goto L_0x00a7
            if (r5 < r9) goto L_0x00a7
            if (r6 > r12) goto L_0x00a7
            if (r7 >= r12) goto L_0x003b
            goto L_0x00a7
        L_0x003b:
            boolean r1 = r3 instanceof org.telegram.ui.Cells.StickerEmojiCell
            r2 = -1
            r4 = 1
            if (r1 == 0) goto L_0x0051
            r1 = r3
            org.telegram.ui.Cells.StickerEmojiCell r1 = (org.telegram.ui.Cells.StickerEmojiCell) r1
            boolean r1 = r1.showingBitmap()
            if (r1 == 0) goto L_0x0090
            org.telegram.messenger.ImageReceiver r1 = r8.centerImage
            r1.setRoundRadius((int) r0)
        L_0x004f:
            r1 = 0
            goto L_0x0091
        L_0x0051:
            boolean r1 = r3 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x0064
            r1 = r3
            org.telegram.ui.Cells.StickerCell r1 = (org.telegram.ui.Cells.StickerCell) r1
            boolean r1 = r1.showingBitmap()
            if (r1 == 0) goto L_0x0090
            org.telegram.messenger.ImageReceiver r1 = r8.centerImage
            r1.setRoundRadius((int) r0)
            goto L_0x004f
        L_0x0064:
            boolean r1 = r3 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x0090
            r1 = r3
            org.telegram.ui.Cells.ContextLinkCell r1 = (org.telegram.ui.Cells.ContextLinkCell) r1
            boolean r5 = r1.showingBitmap()
            if (r5 == 0) goto L_0x0090
            boolean r5 = r1.isSticker()
            if (r5 == 0) goto L_0x007d
            org.telegram.messenger.ImageReceiver r1 = r8.centerImage
            r1.setRoundRadius((int) r0)
            goto L_0x004f
        L_0x007d:
            boolean r1 = r1.isGif()
            if (r1 == 0) goto L_0x0090
            org.telegram.messenger.ImageReceiver r1 = r8.centerImage
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.setRoundRadius((int) r5)
            r1 = 1
            goto L_0x0091
        L_0x0090:
            r1 = -1
        L_0x0091:
            if (r1 != r2) goto L_0x0094
            return r0
        L_0x0094:
            r8.startX = r12
            r8.startY = r9
            r8.currentPreviewCell = r3
            org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda4 r9 = new org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda4
            r9.<init>(r8, r10, r11, r1)
            r8.openPreviewRunnable = r9
            r10 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r9, r10)
            return r4
        L_0x00a7:
            int r2 = r2 + 1
            goto L_0x0018
        L_0x00ab:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onInterceptTouchEvent(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onInterceptTouchEvent$1(RecyclerListView recyclerListView, int i, int i2) {
        if (this.openPreviewRunnable != null) {
            String str = null;
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
                if (contentPreviewViewerDelegate != null) {
                    str = contentPreviewViewerDelegate.getQuery(false);
                }
                open(sticker, stickerPath, emoji, str, (TLRPC$BotInlineResult) null, i2, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject());
                stickerEmojiCell.setScaled(true);
            } else if (view instanceof StickerCell) {
                StickerCell stickerCell = (StickerCell) view;
                TLRPC$Document sticker2 = stickerCell.getSticker();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate2 = this.delegate;
                if (contentPreviewViewerDelegate2 != null) {
                    str = contentPreviewViewerDelegate2.getQuery(false);
                }
                open(sticker2, (SendMessagesHelper.ImportingSticker) null, (String) null, str, (TLRPC$BotInlineResult) null, i2, false, stickerCell.getParentObject());
                stickerCell.setScaled(true);
                this.clearsInputField = stickerCell.isClearsInputField();
            } else if (view instanceof ContextLinkCell) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) view;
                TLRPC$Document document = contextLinkCell.getDocument();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate3 = this.delegate;
                if (contentPreviewViewerDelegate3 != null) {
                    str = contentPreviewViewerDelegate3.getQuery(true);
                }
                open(document, (SendMessagesHelper.ImportingSticker) null, (String) null, str, contextLinkCell.getBotInlineResult(), i2, false, contextLinkCell.getBotInlineResult() != null ? contextLinkCell.getInlineBot() : contextLinkCell.getParentObject());
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
        this.centerImage.setLayerNum(7);
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
            FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(activity);
            this.containerView = frameLayoutDrawer;
            frameLayoutDrawer.setFocusable(false);
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

    public void open(TLRPC$Document tLRPC$Document, SendMessagesHelper.ImportingSticker importingSticker2, String str, String str2, TLRPC$BotInlineResult tLRPC$BotInlineResult, int i, boolean z, Object obj) {
        TLRPC$InputStickerSet tLRPC$InputStickerSet;
        ContentPreviewViewerDelegate contentPreviewViewerDelegate;
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        SendMessagesHelper.ImportingSticker importingSticker3 = importingSticker2;
        String str3 = str;
        TLRPC$BotInlineResult tLRPC$BotInlineResult2 = tLRPC$BotInlineResult;
        int i2 = i;
        if (this.parentActivity != null && this.windowView != null) {
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
                    this.centerImage.setImage(ImageLocation.getForDocument(tLRPC$Document), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90), tLRPC$Document2), (String) null, "webp", (Object) this.currentStickerSet, 1);
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
}
