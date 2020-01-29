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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;

public class ContentPreviewViewer {
    private static final int CONTENT_TYPE_GIF = 1;
    private static final int CONTENT_TYPE_NONE = -1;
    private static final int CONTENT_TYPE_STICKER = 0;
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
    public TLRPC.Document currentDocument;
    private float currentMoveY;
    /* access modifiers changed from: private */
    public float currentMoveYProgress;
    private View currentPreviewCell;
    /* access modifiers changed from: private */
    public TLRPC.InputStickerSet currentStickerSet;
    /* access modifiers changed from: private */
    public ContentPreviewViewerDelegate delegate;
    /* access modifiers changed from: private */
    public float finalMoveY;
    /* access modifiers changed from: private */
    public TLRPC.BotInlineResult inlineResult;
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
                    if (!arrayList.isEmpty()) {
                        int[] iArr = new int[arrayList3.size()];
                        for (int i2 = 0; i2 < arrayList3.size(); i2++) {
                            iArr[i2] = ((Integer) arrayList3.get(i2)).intValue();
                        }
                        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), iArr, new DialogInterface.OnClickListener(arrayList2, isStickerInFavorites) {
                            private final /* synthetic */ ArrayList f$1;
                            private final /* synthetic */ boolean f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ContentPreviewViewer.AnonymousClass1.this.lambda$run$1$ContentPreviewViewer$1(this.f$1, this.f$2, dialogInterface, i);
                            }
                        });
                        builder.setDimBehind(false);
                        BottomSheet unused = ContentPreviewViewer.this.visibleDialog = builder.create();
                        ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            public final void onDismiss(DialogInterface dialogInterface) {
                                ContentPreviewViewer.AnonymousClass1.this.lambda$run$2$ContentPreviewViewer$1(dialogInterface);
                            }
                        });
                        ContentPreviewViewer.this.visibleDialog.show();
                        ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    }
                } else if (ContentPreviewViewer.this.delegate != null) {
                    boolean unused2 = ContentPreviewViewer.this.animateY = true;
                    ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
                    BottomSheet unused3 = contentPreviewViewer.visibleDialog = new BottomSheet(contentPreviewViewer.parentActivity, false) {
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
                    ContentPreviewViewer.this.visibleDialog.setItems((CharSequence[]) arrayList4.toArray(new CharSequence[0]), iArr2, new DialogInterface.OnClickListener(arrayList5) {
                        private final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ContentPreviewViewer.AnonymousClass1.this.lambda$run$4$ContentPreviewViewer$1(this.f$1, dialogInterface, i);
                        }
                    });
                    ContentPreviewViewer.this.visibleDialog.setDimBehind(false);
                    ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public final void onDismiss(DialogInterface dialogInterface) {
                            ContentPreviewViewer.AnonymousClass1.this.lambda$run$5$ContentPreviewViewer$1(dialogInterface);
                        }
                    });
                    ContentPreviewViewer.this.visibleDialog.show();
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    if (z) {
                        ContentPreviewViewer.this.visibleDialog.setItemColor(arrayList4.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                    }
                }
            }
        }

        public /* synthetic */ void lambda$run$1$ContentPreviewViewer$1(ArrayList arrayList, boolean z, DialogInterface dialogInterface, int i) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) arrayList.get(i)).intValue() == 0) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.sendSticker(ContentPreviewViewer.this.currentDocument, ContentPreviewViewer.this.parentObject, true, 0);
                    }
                } else if (((Integer) arrayList.get(i)).intValue() == 1) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.openSet(ContentPreviewViewer.this.currentStickerSet, ContentPreviewViewer.this.clearsInputField);
                    }
                } else if (((Integer) arrayList.get(i)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(2, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), z);
                } else if (((Integer) arrayList.get(i)).intValue() == 3) {
                    TLRPC.Document access$300 = ContentPreviewViewer.this.currentDocument;
                    Object access$1500 = ContentPreviewViewer.this.parentObject;
                    ContentPreviewViewerDelegate access$500 = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, access$500.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(access$300, access$1500) {
                        private final /* synthetic */ TLRPC.Document f$1;
                        private final /* synthetic */ Object f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            ContentPreviewViewer.ContentPreviewViewerDelegate.this.sendSticker(this.f$1, this.f$2, z, i);
                        }
                    });
                }
            }
        }

        public /* synthetic */ void lambda$run$2$ContentPreviewViewer$1(DialogInterface dialogInterface) {
            BottomSheet unused = ContentPreviewViewer.this.visibleDialog = null;
            ContentPreviewViewer.this.close();
        }

        public /* synthetic */ void lambda$run$4$ContentPreviewViewer$1(ArrayList arrayList, DialogInterface dialogInterface, int i) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) arrayList.get(i)).intValue() == 0) {
                    ContentPreviewViewer.this.delegate.sendGif(ContentPreviewViewer.this.currentDocument != null ? ContentPreviewViewer.this.currentDocument : ContentPreviewViewer.this.inlineResult, true, 0);
                } else if (((Integer) arrayList.get(i)).intValue() == 1) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).removeRecentGif(ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) arrayList.get(i)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentGif(ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000));
                    MessagesController.getInstance(ContentPreviewViewer.this.currentAccount).saveGif("gif", ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) arrayList.get(i)).intValue() == 3) {
                    TLRPC.Document access$300 = ContentPreviewViewer.this.currentDocument;
                    TLRPC.BotInlineResult access$1400 = ContentPreviewViewer.this.inlineResult;
                    Object unused = ContentPreviewViewer.this.parentObject;
                    ContentPreviewViewerDelegate access$500 = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, access$500.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(access$300, access$1400) {
                        private final /* synthetic */ TLRPC.Document f$1;
                        private final /* synthetic */ TLRPC.BotInlineResult f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            ContentPreviewViewer.AnonymousClass1.lambda$null$3(ContentPreviewViewer.ContentPreviewViewerDelegate.this, this.f$1, this.f$2, z, i);
                        }
                    });
                }
            }
        }

        /* JADX WARNING: type inference failed for: r2v0, types: [org.telegram.tgnet.TLRPC$BotInlineResult] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static /* synthetic */ void lambda$null$3(org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r0, org.telegram.tgnet.TLRPC.Document r1, org.telegram.tgnet.TLRPC.BotInlineResult r2, boolean r3, int r4) {
            /*
                if (r1 == 0) goto L_0x0003
                goto L_0x0004
            L_0x0003:
                r1 = r2
            L_0x0004:
                r0.sendGif(r1, r3, r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.AnonymousClass1.lambda$null$3(org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, org.telegram.tgnet.TLRPC$Document, org.telegram.tgnet.TLRPC$BotInlineResult, boolean, int):void");
        }

        public /* synthetic */ void lambda$run$5$ContentPreviewViewer$1(DialogInterface dialogInterface) {
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
    /* access modifiers changed from: private */
    public BottomSheet visibleDialog;
    private WindowManager.LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    public interface ContentPreviewViewerDelegate {

        /* renamed from: org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$gifAddedOrDeleted(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
            }

            public static boolean $default$needOpen(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
                return true;
            }

            public static void $default$sendGif(ContentPreviewViewerDelegate contentPreviewViewerDelegate, Object obj, boolean z, int i) {
            }
        }

        boolean canSchedule();

        long getDialogId();

        void gifAddedOrDeleted();

        boolean isInScheduleMode();

        boolean needOpen();

        boolean needSend();

        void openSet(TLRPC.InputStickerSet inputStickerSet, boolean z);

        void sendGif(Object obj, boolean z, int i);

        void sendSticker(TLRPC.Document document, Object obj, boolean z, int i);
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

    /* JADX WARNING: Removed duplicated region for block: B:69:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0162  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x017b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.MotionEvent r15, org.telegram.ui.Components.RecyclerListView r16, int r17, java.lang.Object r18, org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r19) {
        /*
            r14 = this;
            r6 = r14
            r0 = r16
            r1 = r19
            r6.delegate = r1
            java.lang.Runnable r1 = r6.openPreviewRunnable
            r2 = 0
            if (r1 != 0) goto L_0x0012
            boolean r1 = r14.isVisible()
            if (r1 == 0) goto L_0x023b
        L_0x0012:
            int r1 = r15.getAction()
            r3 = 0
            r7 = 1
            if (r1 == r7) goto L_0x01f9
            int r1 = r15.getAction()
            r4 = 3
            if (r1 == r4) goto L_0x01f9
            int r1 = r15.getAction()
            r4 = 6
            if (r1 != r4) goto L_0x002a
            goto L_0x01f9
        L_0x002a:
            int r1 = r15.getAction()
            if (r1 == 0) goto L_0x023b
            boolean r1 = r6.isVisible
            r4 = 2
            if (r1 == 0) goto L_0x01be
            int r1 = r15.getAction()
            if (r1 != r4) goto L_0x01bd
            int r1 = r6.currentContentType
            if (r1 != r7) goto L_0x00b9
            org.telegram.ui.ActionBar.BottomSheet r0 = r6.visibleDialog
            if (r0 != 0) goto L_0x00b8
            float r0 = r6.showProgress
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x00b8
            float r0 = r6.lastTouchY
            r1 = -971227136(0xffffffffCLASSNAMECLASSNAME, float:-10000.0)
            r2 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x0060
            float r0 = r15.getY()
            r6.lastTouchY = r0
            r6.currentMoveY = r2
            r6.moveY = r2
            goto L_0x00b8
        L_0x0060:
            float r0 = r15.getY()
            float r1 = r6.currentMoveY
            float r3 = r6.lastTouchY
            float r3 = r0 - r3
            float r1 = r1 + r3
            r6.currentMoveY = r1
            r6.lastTouchY = r0
            float r0 = r6.currentMoveY
            int r1 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x0078
            r6.currentMoveY = r2
            goto L_0x008c
        L_0x0078:
            r1 = 1114636288(0x42700000, float:60.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = -r2
            float r2 = (float) r2
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x008c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = -r0
            float r0 = (float) r0
            r6.currentMoveY = r0
        L_0x008c:
            float r0 = r6.currentMoveY
            r1 = 1128792064(0x43480000, float:200.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r0 = r14.rubberYPoisition(r0, r1)
            r6.moveY = r0
            org.telegram.ui.ContentPreviewViewer$FrameLayoutDrawer r0 = r6.containerView
            r0.invalidate()
            float r0 = r6.currentMoveY
            r1 = 1113325568(0x425CLASSNAME, float:55.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L_0x00b8
            java.lang.Runnable r0 = r6.showSheetRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            java.lang.Runnable r0 = r6.showSheetRunnable
            r0.run()
        L_0x00b8:
            return r7
        L_0x00b9:
            float r1 = r15.getX()
            int r1 = (int) r1
            float r4 = r15.getY()
            int r4 = (int) r4
            int r5 = r16.getChildCount()
            r8 = 0
        L_0x00c8:
            if (r8 >= r5) goto L_0x01bd
            boolean r9 = r0 instanceof org.telegram.ui.Components.RecyclerListView
            if (r9 == 0) goto L_0x00d3
            android.view.View r9 = r0.getChildAt(r8)
            goto L_0x00d4
        L_0x00d3:
            r9 = r3
        L_0x00d4:
            if (r9 != 0) goto L_0x00d7
            return r2
        L_0x00d7:
            int r10 = r9.getTop()
            int r11 = r9.getBottom()
            int r12 = r9.getLeft()
            int r13 = r9.getRight()
            if (r10 > r4) goto L_0x01b7
            if (r11 < r4) goto L_0x01b7
            if (r12 > r1) goto L_0x01b7
            if (r13 >= r1) goto L_0x00f1
            goto L_0x01b7
        L_0x00f1:
            boolean r0 = r9 instanceof org.telegram.ui.Cells.StickerEmojiCell
            r1 = -1
            if (r0 == 0) goto L_0x00fd
            org.telegram.messenger.ImageReceiver r0 = r6.centerImage
            r0.setRoundRadius(r2)
        L_0x00fb:
            r8 = 0
            goto L_0x012e
        L_0x00fd:
            boolean r0 = r9 instanceof org.telegram.ui.Cells.StickerCell
            if (r0 == 0) goto L_0x0107
            org.telegram.messenger.ImageReceiver r0 = r6.centerImage
            r0.setRoundRadius(r2)
            goto L_0x00fb
        L_0x0107:
            boolean r0 = r9 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r0 == 0) goto L_0x012d
            r0 = r9
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            boolean r3 = r0.isSticker()
            if (r3 == 0) goto L_0x011a
            org.telegram.messenger.ImageReceiver r0 = r6.centerImage
            r0.setRoundRadius(r2)
            goto L_0x00fb
        L_0x011a:
            boolean r0 = r0.isGif()
            if (r0 == 0) goto L_0x012d
            org.telegram.messenger.ImageReceiver r0 = r6.centerImage
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r0.setRoundRadius(r3)
            r8 = 1
            goto L_0x012e
        L_0x012d:
            r8 = -1
        L_0x012e:
            if (r8 == r1) goto L_0x01bd
            android.view.View r0 = r6.currentPreviewCell
            if (r9 != r0) goto L_0x0136
            goto L_0x01bd
        L_0x0136:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x0140
            org.telegram.ui.Cells.StickerEmojiCell r0 = (org.telegram.ui.Cells.StickerEmojiCell) r0
            r0.setScaled(r2)
            goto L_0x0153
        L_0x0140:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x014a
            org.telegram.ui.Cells.StickerCell r0 = (org.telegram.ui.Cells.StickerCell) r0
            r0.setScaled(r2)
            goto L_0x0153
        L_0x014a:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x0153
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            r0.setScaled(r2)
        L_0x0153:
            r6.currentPreviewCell = r9
            r9 = r17
            r14.setKeyboardHeight(r9)
            r6.clearsInputField = r2
            android.view.View r0 = r6.currentPreviewCell
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x017b
            r9 = r0
            org.telegram.ui.Cells.StickerEmojiCell r9 = (org.telegram.ui.Cells.StickerEmojiCell) r9
            org.telegram.tgnet.TLRPC$Document r1 = r9.getSticker()
            r2 = 0
            boolean r4 = r9.isRecent()
            java.lang.Object r5 = r9.getParentObject()
            r0 = r14
            r3 = r8
            r0.open(r1, r2, r3, r4, r5)
            r9.setScaled(r7)
            goto L_0x01b6
        L_0x017b:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x019b
            r9 = r0
            org.telegram.ui.Cells.StickerCell r9 = (org.telegram.ui.Cells.StickerCell) r9
            org.telegram.tgnet.TLRPC$Document r1 = r9.getSticker()
            r2 = 0
            r4 = 0
            java.lang.Object r5 = r9.getParentObject()
            r0 = r14
            r3 = r8
            r0.open(r1, r2, r3, r4, r5)
            r9.setScaled(r7)
            boolean r0 = r9.isClearsInputField()
            r6.clearsInputField = r0
            goto L_0x01b6
        L_0x019b:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x01b6
            r9 = r0
            org.telegram.ui.Cells.ContextLinkCell r9 = (org.telegram.ui.Cells.ContextLinkCell) r9
            org.telegram.tgnet.TLRPC$Document r1 = r9.getDocument()
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = r9.getBotInlineResult()
            r4 = 0
            r5 = 0
            r0 = r14
            r3 = r8
            r0.open(r1, r2, r3, r4, r5)
            if (r8 == r7) goto L_0x01b6
            r9.setScaled(r7)
        L_0x01b6:
            return r7
        L_0x01b7:
            r9 = r17
            int r8 = r8 + 1
            goto L_0x00c8
        L_0x01bd:
            return r7
        L_0x01be:
            java.lang.Runnable r0 = r6.openPreviewRunnable
            if (r0 == 0) goto L_0x023b
            int r0 = r15.getAction()
            if (r0 != r4) goto L_0x01f1
            int r0 = r6.startX
            float r0 = (float) r0
            float r1 = r15.getX()
            float r0 = r0 - r1
            double r0 = (double) r0
            int r4 = r6.startY
            float r4 = (float) r4
            float r5 = r15.getY()
            float r4 = r4 - r5
            double r4 = (double) r4
            double r0 = java.lang.Math.hypot(r0, r4)
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            double r4 = (double) r4
            int r7 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r7 <= 0) goto L_0x023b
            java.lang.Runnable r0 = r6.openPreviewRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r6.openPreviewRunnable = r3
            goto L_0x023b
        L_0x01f1:
            java.lang.Runnable r0 = r6.openPreviewRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r6.openPreviewRunnable = r3
            goto L_0x023b
        L_0x01f9:
            org.telegram.ui.-$$Lambda$ContentPreviewViewer$EMKDqwNyTHEkiYf1BXP5lN4E1U8 r1 = new org.telegram.ui.-$$Lambda$ContentPreviewViewer$EMKDqwNyTHEkiYf1BXP5lN4E1U8
            r4 = r18
            r1.<init>(r4)
            r4 = 150(0x96, double:7.4E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r4)
            java.lang.Runnable r0 = r6.openPreviewRunnable
            if (r0 == 0) goto L_0x020f
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r6.openPreviewRunnable = r3
            goto L_0x023b
        L_0x020f:
            boolean r0 = r14.isVisible()
            if (r0 == 0) goto L_0x023b
            r14.close()
            android.view.View r0 = r6.currentPreviewCell
            if (r0 == 0) goto L_0x023b
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x0226
            org.telegram.ui.Cells.StickerEmojiCell r0 = (org.telegram.ui.Cells.StickerEmojiCell) r0
            r0.setScaled(r2)
            goto L_0x0239
        L_0x0226:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x0230
            org.telegram.ui.Cells.StickerCell r0 = (org.telegram.ui.Cells.StickerCell) r0
            r0.setScaled(r2)
            goto L_0x0239
        L_0x0230:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x0239
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            r0.setScaled(r2)
        L_0x0239:
            r6.currentPreviewCell = r3
        L_0x023b:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onTouch(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, java.lang.Object, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate):boolean");
    }

    static /* synthetic */ void lambda$onTouch$0(RecyclerListView recyclerListView, Object obj) {
        if (recyclerListView instanceof RecyclerListView) {
            recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) obj);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x0098 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0099  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r9, org.telegram.ui.Components.RecyclerListView r10, int r11, org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r12) {
        /*
            r8 = this;
            r8.delegate = r12
            int r12 = r9.getAction()
            r0 = 0
            if (r12 != 0) goto L_0x00b2
            float r12 = r9.getX()
            int r12 = (int) r12
            float r9 = r9.getY()
            int r9 = (int) r9
            int r1 = r10.getChildCount()
            r2 = 0
        L_0x0018:
            if (r2 >= r1) goto L_0x00b2
            r3 = 0
            boolean r4 = r10 instanceof org.telegram.ui.Components.RecyclerListView
            if (r4 == 0) goto L_0x0023
            android.view.View r3 = r10.getChildAt(r2)
        L_0x0023:
            if (r3 != 0) goto L_0x0026
            return r0
        L_0x0026:
            int r4 = r3.getTop()
            int r5 = r3.getBottom()
            int r6 = r3.getLeft()
            int r7 = r3.getRight()
            if (r4 > r9) goto L_0x00ae
            if (r5 < r9) goto L_0x00ae
            if (r6 > r12) goto L_0x00ae
            if (r7 >= r12) goto L_0x0040
            goto L_0x00ae
        L_0x0040:
            boolean r1 = r3 instanceof org.telegram.ui.Cells.StickerEmojiCell
            r2 = -1
            r4 = 1
            if (r1 == 0) goto L_0x0056
            r1 = r3
            org.telegram.ui.Cells.StickerEmojiCell r1 = (org.telegram.ui.Cells.StickerEmojiCell) r1
            boolean r1 = r1.showingBitmap()
            if (r1 == 0) goto L_0x0095
            org.telegram.messenger.ImageReceiver r1 = r8.centerImage
            r1.setRoundRadius(r0)
        L_0x0054:
            r1 = 0
            goto L_0x0096
        L_0x0056:
            boolean r1 = r3 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x0069
            r1 = r3
            org.telegram.ui.Cells.StickerCell r1 = (org.telegram.ui.Cells.StickerCell) r1
            boolean r1 = r1.showingBitmap()
            if (r1 == 0) goto L_0x0095
            org.telegram.messenger.ImageReceiver r1 = r8.centerImage
            r1.setRoundRadius(r0)
            goto L_0x0054
        L_0x0069:
            boolean r1 = r3 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x0095
            r1 = r3
            org.telegram.ui.Cells.ContextLinkCell r1 = (org.telegram.ui.Cells.ContextLinkCell) r1
            boolean r5 = r1.showingBitmap()
            if (r5 == 0) goto L_0x0095
            boolean r5 = r1.isSticker()
            if (r5 == 0) goto L_0x0082
            org.telegram.messenger.ImageReceiver r1 = r8.centerImage
            r1.setRoundRadius(r0)
            goto L_0x0054
        L_0x0082:
            boolean r1 = r1.isGif()
            if (r1 == 0) goto L_0x0095
            org.telegram.messenger.ImageReceiver r1 = r8.centerImage
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.setRoundRadius(r5)
            r1 = 1
            goto L_0x0096
        L_0x0095:
            r1 = -1
        L_0x0096:
            if (r1 != r2) goto L_0x0099
            return r0
        L_0x0099:
            r8.startX = r12
            r8.startY = r9
            r8.currentPreviewCell = r3
            org.telegram.ui.-$$Lambda$ContentPreviewViewer$wFY_75sBoPTmhZIpv5QBGVqAeaE r9 = new org.telegram.ui.-$$Lambda$ContentPreviewViewer$wFY_75sBoPTmhZIpv5QBGVqAeaE
            r9.<init>(r10, r11, r1)
            r8.openPreviewRunnable = r9
            java.lang.Runnable r9 = r8.openPreviewRunnable
            r10 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r9, r10)
            return r4
        L_0x00ae:
            int r2 = r2 + 1
            goto L_0x0018
        L_0x00b2:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onInterceptTouchEvent(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate):boolean");
    }

    public /* synthetic */ void lambda$onInterceptTouchEvent$1$ContentPreviewViewer(RecyclerListView recyclerListView, int i, int i2) {
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
                open(stickerEmojiCell.getSticker(), (TLRPC.BotInlineResult) null, i2, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject());
                stickerEmojiCell.setScaled(true);
            } else if (view instanceof StickerCell) {
                StickerCell stickerCell = (StickerCell) view;
                open(stickerCell.getSticker(), (TLRPC.BotInlineResult) null, i2, false, stickerCell.getParentObject());
                stickerCell.setScaled(true);
                this.clearsInputField = stickerCell.isClearsInputField();
            } else if (view instanceof ContextLinkCell) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) view;
                open(contextLinkCell.getDocument(), contextLinkCell.getBotInlineResult(), i2, false, (Object) null);
                if (i2 != 1) {
                    contextLinkCell.setScaled(true);
                }
            }
        }
    }

    public void setDelegate(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        this.delegate = contentPreviewViewerDelegate;
    }

    public void setParentActivity(Activity activity) {
        this.currentAccount = UserConfig.selectedAccount;
        this.centerImage.setCurrentAccount(this.currentAccount);
        this.centerImage.setLayerNum(7);
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.slideUpDrawable = this.parentActivity.getResources().getDrawable(NUM);
            this.windowView = new FrameLayout(activity);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            if (Build.VERSION.SDK_INT >= 21) {
                this.windowView.setFitsSystemWindows(true);
                this.windowView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                        return ContentPreviewViewer.this.lambda$setParentActivity$2$ContentPreviewViewer(view, windowInsets);
                    }
                });
            }
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return ContentPreviewViewer.this.lambda$setParentActivity$3$ContentPreviewViewer(view, motionEvent);
                }
            });
            this.windowLayoutParams = new WindowManager.LayoutParams();
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            layoutParams.height = -1;
            layoutParams.format = -3;
            layoutParams.width = -1;
            layoutParams.gravity = 48;
            layoutParams.type = 99;
            if (Build.VERSION.SDK_INT >= 21) {
                layoutParams.flags = -NUM;
            } else {
                layoutParams.flags = 8;
            }
            this.centerImage.setAspectFit(true);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setParentView(this.containerView);
        }
    }

    public /* synthetic */ WindowInsets lambda$setParentActivity$2$ContentPreviewViewer(View view, WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
        return windowInsets;
    }

    public /* synthetic */ boolean lambda$setParentActivity$3$ContentPreviewViewer(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 6 || motionEvent.getAction() == 3) {
            close();
        }
        return true;
    }

    public void setKeyboardHeight(int i) {
        this.keyboardHeight = i;
    }

    public void open(TLRPC.Document document, TLRPC.BotInlineResult botInlineResult, int i, boolean z, Object obj) {
        TLRPC.WebDocument webDocument;
        TLRPC.InputStickerSet inputStickerSet;
        TLRPC.Document document2 = document;
        TLRPC.BotInlineResult botInlineResult2 = botInlineResult;
        int i2 = i;
        if (this.parentActivity != null && this.windowView != null) {
            this.stickerEmojiLayout = null;
            if (i2 == 0) {
                if (document2 != null) {
                    if (textPaint == null) {
                        textPaint = new TextPaint(1);
                        textPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
                    }
                    int i3 = 0;
                    while (true) {
                        if (i3 >= document2.attributes.size()) {
                            inputStickerSet = null;
                            break;
                        }
                        TLRPC.DocumentAttribute documentAttribute = document2.attributes.get(i3);
                        if ((documentAttribute instanceof TLRPC.TL_documentAttributeSticker) && (inputStickerSet = documentAttribute.stickerset) != null) {
                            break;
                        }
                        i3++;
                    }
                    if (inputStickerSet != null) {
                        try {
                            if (this.visibleDialog != null) {
                                this.visibleDialog.setOnDismissListener((DialogInterface.OnDismissListener) null);
                                this.visibleDialog.dismiss();
                                this.visibleDialog = null;
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 1300);
                    }
                    this.currentStickerSet = inputStickerSet;
                    this.parentObject = obj;
                    this.centerImage.setImage(ImageLocation.getForDocument(document), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90), document2), (String) null, "webp", (Object) this.currentStickerSet, 1);
                    int i4 = 0;
                    while (true) {
                        if (i4 >= document2.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute documentAttribute2 = document2.attributes.get(i4);
                        if ((documentAttribute2 instanceof TLRPC.TL_documentAttributeSticker) && !TextUtils.isEmpty(documentAttribute2.alt)) {
                            this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(documentAttribute2.alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            break;
                        }
                        i4++;
                    }
                } else {
                    return;
                }
            } else {
                if (document2 != null) {
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
                    this.centerImage.setImage(ImageLocation.getForDocument(document), (String) null, ImageLocation.getForDocument(closestPhotoSizeWithSize, document2), "90_90_b", document2.size, (String) null, "gif" + document2, 0);
                } else if (botInlineResult2 != null && (webDocument = botInlineResult2.content) != null) {
                    this.centerImage.setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(webDocument)), (String) null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.thumb)), "90_90_b", botInlineResult2.content.size, (String) null, "gif" + botInlineResult2, 1);
                } else {
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                AndroidUtilities.runOnUIThread(this.showSheetRunnable, 2000);
            }
            this.currentContentType = i2;
            this.currentDocument = document2;
            this.inlineResult = botInlineResult2;
            this.containerView.invalidate();
            if (!this.isVisible) {
                AndroidUtilities.lockOrientation(this.parentActivity);
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
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
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
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
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.currentDocument = null;
            this.currentStickerSet = null;
            this.delegate = null;
            this.isVisible = false;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
        }
    }

    public void destroy() {
        FrameLayout frameLayout;
        this.isVisible = false;
        this.delegate = null;
        this.currentDocument = null;
        this.currentStickerSet = null;
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
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
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
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
            int i7 = (-i6) / 2;
            this.centerImage.setImageCoords(i7, i7, i6, i6);
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
                canvas.translate((float) (-AndroidUtilities.dp(50.0f)), (float) (((-this.centerImage.getImageHeight()) / 2) - AndroidUtilities.dp(30.0f)));
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
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            ContentPreviewViewer.this.lambda$onDraw$4$ContentPreviewViewer();
                        }
                    });
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

    public /* synthetic */ void lambda$onDraw$4$ContentPreviewViewer() {
        this.centerImage.setImageBitmap((Bitmap) null);
    }
}
