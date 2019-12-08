package org.telegram.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
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
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class ContentPreviewViewer {
    private static final int CONTENT_TYPE_GIF = 1;
    private static final int CONTENT_TYPE_NONE = -1;
    private static final int CONTENT_TYPE_STICKER = 0;
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ContentPreviewViewer Instance;
    private static TextPaint textPaint;
    private boolean animateY;
    private ColorDrawable backgroundDrawable = new ColorDrawable(NUM);
    private ImageReceiver centerImage = new ImageReceiver();
    private boolean clearsInputField;
    private FrameLayoutDrawer containerView;
    private int currentAccount;
    private int currentContentType;
    private Document currentDocument;
    private float currentMoveY;
    private float currentMoveYProgress;
    private View currentPreviewCell;
    private InputStickerSet currentStickerSet;
    private ContentPreviewViewerDelegate delegate;
    private float finalMoveY;
    private BotInlineResult inlineResult;
    private boolean isVisible = false;
    private int keyboardHeight = AndroidUtilities.dp(200.0f);
    private WindowInsets lastInsets;
    private float lastTouchY;
    private long lastUpdateTime;
    private float moveY = 0.0f;
    private Runnable openPreviewRunnable;
    private Activity parentActivity;
    private Object parentObject;
    private float showProgress;
    private Runnable showSheetRunnable = new Runnable() {
        public void run() {
            if (ContentPreviewViewer.this.parentActivity != null) {
                String str = "Schedule";
                ArrayList arrayList;
                int[] iArr;
                if (ContentPreviewViewer.this.currentContentType == 0) {
                    int i;
                    boolean isStickerInFavorites = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).isStickerInFavorites(ContentPreviewViewer.this.currentDocument);
                    Builder builder = new Builder(ContentPreviewViewer.this.parentActivity);
                    arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    ArrayList arrayList3 = new ArrayList();
                    if (ContentPreviewViewer.this.delegate != null) {
                        if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                            arrayList.add(LocaleController.getString("SendStickerPreview", NUM));
                            arrayList3.add(Integer.valueOf(NUM));
                            arrayList2.add(Integer.valueOf(0));
                        }
                        if (ContentPreviewViewer.this.delegate.canSchedule()) {
                            arrayList.add(LocaleController.getString(str, NUM));
                            arrayList3.add(Integer.valueOf(NUM));
                            arrayList2.add(Integer.valueOf(3));
                        }
                        if (ContentPreviewViewer.this.currentStickerSet != null && ContentPreviewViewer.this.delegate.needOpen()) {
                            arrayList.add(LocaleController.formatString("ViewPackPreview", NUM, new Object[0]));
                            arrayList3.add(Integer.valueOf(NUM));
                            arrayList2.add(Integer.valueOf(1));
                        }
                    }
                    if (!MessageObject.isMaskDocument(ContentPreviewViewer.this.currentDocument) && (isStickerInFavorites || MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).canAddStickerToFavorites())) {
                        String str2;
                        if (isStickerInFavorites) {
                            i = NUM;
                            str2 = "DeleteFromFavorites";
                        } else {
                            i = NUM;
                            str2 = "AddToFavorites";
                        }
                        arrayList.add(LocaleController.getString(str2, i));
                        arrayList3.add(Integer.valueOf(isStickerInFavorites ? NUM : NUM));
                        arrayList2.add(Integer.valueOf(2));
                    }
                    if (!arrayList.isEmpty()) {
                        iArr = new int[arrayList3.size()];
                        for (i = 0; i < arrayList3.size(); i++) {
                            iArr[i] = ((Integer) arrayList3.get(i)).intValue();
                        }
                        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), iArr, new -$$Lambda$ContentPreviewViewer$1$imL7s2qfRP4bUjAtc6JLJW8Hhgw(this, arrayList2, isStickerInFavorites));
                        builder.setDimBehind(false);
                        ContentPreviewViewer.this.visibleDialog = builder.create();
                        ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new -$$Lambda$ContentPreviewViewer$1$NZBUQP7-vH9TTdKIqLQEN7It1ok(this));
                        ContentPreviewViewer.this.visibleDialog.show();
                        ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    }
                } else if (ContentPreviewViewer.this.delegate != null) {
                    boolean hasRecentGif;
                    ContentPreviewViewer.this.animateY = true;
                    ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
                    contentPreviewViewer.visibleDialog = new BottomSheet(contentPreviewViewer.parentActivity, false, 0) {
                        /* Access modifiers changed, original: protected */
                        public void onContainerTranslationYChanged(float f) {
                            if (ContentPreviewViewer.this.animateY) {
                                getSheetContainer();
                                if (ContentPreviewViewer.this.finalMoveY == 0.0f) {
                                    ContentPreviewViewer.this.finalMoveY = 0.0f;
                                    ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
                                    contentPreviewViewer.startMoveY = contentPreviewViewer.moveY;
                                }
                                ContentPreviewViewer.this.currentMoveYProgress = 1.0f - Math.min(1.0f, f / ((float) this.containerView.getMeasuredHeight()));
                                ContentPreviewViewer contentPreviewViewer2 = ContentPreviewViewer.this;
                                contentPreviewViewer2.moveY = contentPreviewViewer2.startMoveY + ((ContentPreviewViewer.this.finalMoveY - ContentPreviewViewer.this.startMoveY) * ContentPreviewViewer.this.currentMoveYProgress);
                                ContentPreviewViewer.this.containerView.invalidate();
                                if (ContentPreviewViewer.this.currentMoveYProgress == 1.0f) {
                                    ContentPreviewViewer.this.animateY = false;
                                }
                            }
                        }
                    };
                    ArrayList arrayList4 = new ArrayList();
                    ArrayList arrayList5 = new ArrayList();
                    arrayList = new ArrayList();
                    if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                        arrayList4.add(LocaleController.getString("SendGifPreview", NUM));
                        arrayList.add(Integer.valueOf(NUM));
                        arrayList5.add(Integer.valueOf(0));
                    }
                    if (ContentPreviewViewer.this.delegate.canSchedule()) {
                        arrayList4.add(LocaleController.getString(str, NUM));
                        arrayList.add(Integer.valueOf(NUM));
                        arrayList5.add(Integer.valueOf(3));
                    }
                    if (ContentPreviewViewer.this.currentDocument != null) {
                        hasRecentGif = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).hasRecentGif(ContentPreviewViewer.this.currentDocument);
                        if (hasRecentGif) {
                            arrayList4.add(LocaleController.formatString("Delete", NUM, new Object[0]));
                            arrayList.add(Integer.valueOf(NUM));
                            arrayList5.add(Integer.valueOf(1));
                        } else {
                            arrayList4.add(LocaleController.formatString("SaveToGIFs", NUM, new Object[0]));
                            arrayList.add(Integer.valueOf(NUM));
                            arrayList5.add(Integer.valueOf(2));
                        }
                    } else {
                        hasRecentGif = false;
                    }
                    iArr = new int[arrayList.size()];
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        iArr[i2] = ((Integer) arrayList.get(i2)).intValue();
                    }
                    ContentPreviewViewer.this.visibleDialog.setItems((CharSequence[]) arrayList4.toArray(new CharSequence[0]), iArr, new -$$Lambda$ContentPreviewViewer$1$ctJp2_zc8S3VjDWUO8qfR09OkLI(this, arrayList5));
                    ContentPreviewViewer.this.visibleDialog.setDimBehind(false);
                    ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new -$$Lambda$ContentPreviewViewer$1$VGh98iTBEiBGO7rG6tFMvm6L3hY(this));
                    ContentPreviewViewer.this.visibleDialog.show();
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    if (hasRecentGif) {
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
                    Document access$300 = ContentPreviewViewer.this.currentDocument;
                    Object access$1500 = ContentPreviewViewer.this.parentObject;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, false, new -$$Lambda$ContentPreviewViewer$1$VeEKHdYoA5-UzCB0iok2LPLcTCo(ContentPreviewViewer.this.delegate, access$300, access$1500));
                }
            }
        }

        public /* synthetic */ void lambda$run$2$ContentPreviewViewer$1(DialogInterface dialogInterface) {
            ContentPreviewViewer.this.visibleDialog = null;
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
                    Document access$300 = ContentPreviewViewer.this.currentDocument;
                    BotInlineResult access$1400 = ContentPreviewViewer.this.inlineResult;
                    ContentPreviewViewer.this.parentObject;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, false, new -$$Lambda$ContentPreviewViewer$1$OPsVklic0vV-NhdYCfmrpeZgJyo(ContentPreviewViewer.this.delegate, access$300, access$1400));
                }
            }
        }

        static /* synthetic */ void lambda$null$3(ContentPreviewViewerDelegate contentPreviewViewerDelegate, Document document, BotInlineResult botInlineResult, boolean z, int i) {
            Object document2;
            if (document2 == null) {
                document2 = botInlineResult;
            }
            contentPreviewViewerDelegate.sendGif(document2, z, i);
        }

        public /* synthetic */ void lambda$run$5$ContentPreviewViewer$1(DialogInterface dialogInterface) {
            ContentPreviewViewer.this.visibleDialog = null;
            ContentPreviewViewer.this.close();
        }
    };
    private Drawable slideUpDrawable;
    private float startMoveY;
    private int startX;
    private int startY;
    private StaticLayout stickerEmojiLayout;
    private BottomSheet visibleDialog;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    public interface ContentPreviewViewerDelegate {

        public final /* synthetic */ class -CC {
            public static void $default$gifAddedOrDeleted(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
            }

            public static boolean $default$needOpen(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
                return true;
            }

            public static void $default$sendGif(ContentPreviewViewerDelegate contentPreviewViewerDelegate, Object obj, boolean z, int i) {
            }
        }

        boolean canSchedule();

        void gifAddedOrDeleted();

        boolean isInScheduleMode();

        boolean needOpen();

        boolean needSend();

        void openSet(InputStickerSet inputStickerSet, boolean z);

        void sendGif(Object obj, boolean z, int i);

        void sendSticker(Document document, Object obj, boolean z, int i);
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        /* Access modifiers changed, original: protected */
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

    /* JADX WARNING: Removed duplicated region for block: B:65:0x0130  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0130  */
    public boolean onTouch(android.view.MotionEvent r15, org.telegram.ui.Components.RecyclerListView r16, int r17, java.lang.Object r18, org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r19) {
        /*
        r14 = this;
        r6 = r14;
        r0 = r16;
        r1 = r19;
        r6.delegate = r1;
        r1 = r6.openPreviewRunnable;
        r2 = 0;
        if (r1 != 0) goto L_0x0012;
    L_0x000c:
        r1 = r14.isVisible();
        if (r1 == 0) goto L_0x023b;
    L_0x0012:
        r1 = r15.getAction();
        r3 = 0;
        r7 = 1;
        if (r1 == r7) goto L_0x01f9;
    L_0x001a:
        r1 = r15.getAction();
        r4 = 3;
        if (r1 == r4) goto L_0x01f9;
    L_0x0021:
        r1 = r15.getAction();
        r4 = 6;
        if (r1 != r4) goto L_0x002a;
    L_0x0028:
        goto L_0x01f9;
    L_0x002a:
        r1 = r15.getAction();
        if (r1 == 0) goto L_0x023b;
    L_0x0030:
        r1 = r6.isVisible;
        r4 = 2;
        if (r1 == 0) goto L_0x01be;
    L_0x0035:
        r1 = r15.getAction();
        if (r1 != r4) goto L_0x01bd;
    L_0x003b:
        r1 = r6.currentContentType;
        if (r1 != r7) goto L_0x00b9;
    L_0x003f:
        r0 = r6.visibleDialog;
        if (r0 != 0) goto L_0x00b8;
    L_0x0043:
        r0 = r6.showProgress;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 != 0) goto L_0x00b8;
    L_0x004b:
        r0 = r6.lastTouchY;
        r1 = -NUM; // 0xffffffffCLASSNAMECLASSNAME float:-10000.0 double:NaN;
        r2 = 0;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 != 0) goto L_0x0060;
    L_0x0055:
        r0 = r15.getY();
        r6.lastTouchY = r0;
        r6.currentMoveY = r2;
        r6.moveY = r2;
        goto L_0x00b8;
    L_0x0060:
        r0 = r15.getY();
        r1 = r6.currentMoveY;
        r3 = r6.lastTouchY;
        r3 = r0 - r3;
        r1 = r1 + r3;
        r6.currentMoveY = r1;
        r6.lastTouchY = r0;
        r0 = r6.currentMoveY;
        r1 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r1 <= 0) goto L_0x0078;
    L_0x0075:
        r6.currentMoveY = r2;
        goto L_0x008c;
    L_0x0078:
        r1 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = -r2;
        r2 = (float) r2;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x008c;
    L_0x0084:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0 = -r0;
        r0 = (float) r0;
        r6.currentMoveY = r0;
    L_0x008c:
        r0 = r6.currentMoveY;
        r1 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r0 = r14.rubberYPoisition(r0, r1);
        r6.moveY = r0;
        r0 = r6.containerView;
        r0.invalidate();
        r0 = r6.currentMoveY;
        r1 = NUM; // 0x425CLASSNAME float:55.0 double:5.50055916E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = -r1;
        r1 = (float) r1;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 > 0) goto L_0x00b8;
    L_0x00ae:
        r0 = r6.showSheetRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r0 = r6.showSheetRunnable;
        r0.run();
    L_0x00b8:
        return r7;
    L_0x00b9:
        r1 = r15.getX();
        r1 = (int) r1;
        r4 = r15.getY();
        r4 = (int) r4;
        r5 = r16.getChildCount();
        r8 = 0;
    L_0x00c8:
        if (r8 >= r5) goto L_0x01bd;
    L_0x00ca:
        r9 = r0 instanceof org.telegram.ui.Components.RecyclerListView;
        if (r9 == 0) goto L_0x00d3;
    L_0x00ce:
        r9 = r0.getChildAt(r8);
        goto L_0x00d4;
    L_0x00d3:
        r9 = r3;
    L_0x00d4:
        if (r9 != 0) goto L_0x00d7;
    L_0x00d6:
        return r2;
    L_0x00d7:
        r10 = r9.getTop();
        r11 = r9.getBottom();
        r12 = r9.getLeft();
        r13 = r9.getRight();
        if (r10 > r4) goto L_0x01b7;
    L_0x00e9:
        if (r11 < r4) goto L_0x01b7;
    L_0x00eb:
        if (r12 > r1) goto L_0x01b7;
    L_0x00ed:
        if (r13 >= r1) goto L_0x00f1;
    L_0x00ef:
        goto L_0x01b7;
    L_0x00f1:
        r0 = r9 instanceof org.telegram.ui.Cells.StickerEmojiCell;
        r1 = -1;
        if (r0 == 0) goto L_0x00fd;
    L_0x00f6:
        r0 = r6.centerImage;
        r0.setRoundRadius(r2);
    L_0x00fb:
        r8 = 0;
        goto L_0x012e;
    L_0x00fd:
        r0 = r9 instanceof org.telegram.ui.Cells.StickerCell;
        if (r0 == 0) goto L_0x0107;
    L_0x0101:
        r0 = r6.centerImage;
        r0.setRoundRadius(r2);
        goto L_0x00fb;
    L_0x0107:
        r0 = r9 instanceof org.telegram.ui.Cells.ContextLinkCell;
        if (r0 == 0) goto L_0x012d;
    L_0x010b:
        r0 = r9;
        r0 = (org.telegram.ui.Cells.ContextLinkCell) r0;
        r3 = r0.isSticker();
        if (r3 == 0) goto L_0x011a;
    L_0x0114:
        r0 = r6.centerImage;
        r0.setRoundRadius(r2);
        goto L_0x00fb;
    L_0x011a:
        r0 = r0.isGif();
        if (r0 == 0) goto L_0x012d;
    L_0x0120:
        r0 = r6.centerImage;
        r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0.setRoundRadius(r3);
        r8 = 1;
        goto L_0x012e;
    L_0x012d:
        r8 = -1;
    L_0x012e:
        if (r8 == r1) goto L_0x01bd;
    L_0x0130:
        r0 = r6.currentPreviewCell;
        if (r9 != r0) goto L_0x0136;
    L_0x0134:
        goto L_0x01bd;
    L_0x0136:
        r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell;
        if (r1 == 0) goto L_0x0140;
    L_0x013a:
        r0 = (org.telegram.ui.Cells.StickerEmojiCell) r0;
        r0.setScaled(r2);
        goto L_0x0153;
    L_0x0140:
        r1 = r0 instanceof org.telegram.ui.Cells.StickerCell;
        if (r1 == 0) goto L_0x014a;
    L_0x0144:
        r0 = (org.telegram.ui.Cells.StickerCell) r0;
        r0.setScaled(r2);
        goto L_0x0153;
    L_0x014a:
        r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell;
        if (r1 == 0) goto L_0x0153;
    L_0x014e:
        r0 = (org.telegram.ui.Cells.ContextLinkCell) r0;
        r0.setScaled(r2);
    L_0x0153:
        r6.currentPreviewCell = r9;
        r9 = r17;
        r14.setKeyboardHeight(r9);
        r6.clearsInputField = r2;
        r0 = r6.currentPreviewCell;
        r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell;
        if (r1 == 0) goto L_0x017b;
    L_0x0162:
        r9 = r0;
        r9 = (org.telegram.ui.Cells.StickerEmojiCell) r9;
        r1 = r9.getSticker();
        r2 = 0;
        r4 = r9.isRecent();
        r5 = r9.getParentObject();
        r0 = r14;
        r3 = r8;
        r0.open(r1, r2, r3, r4, r5);
        r9.setScaled(r7);
        goto L_0x01b6;
    L_0x017b:
        r1 = r0 instanceof org.telegram.ui.Cells.StickerCell;
        if (r1 == 0) goto L_0x019b;
    L_0x017f:
        r9 = r0;
        r9 = (org.telegram.ui.Cells.StickerCell) r9;
        r1 = r9.getSticker();
        r2 = 0;
        r4 = 0;
        r5 = r9.getParentObject();
        r0 = r14;
        r3 = r8;
        r0.open(r1, r2, r3, r4, r5);
        r9.setScaled(r7);
        r0 = r9.isClearsInputField();
        r6.clearsInputField = r0;
        goto L_0x01b6;
    L_0x019b:
        r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell;
        if (r1 == 0) goto L_0x01b6;
    L_0x019f:
        r9 = r0;
        r9 = (org.telegram.ui.Cells.ContextLinkCell) r9;
        r1 = r9.getDocument();
        r2 = r9.getBotInlineResult();
        r4 = 0;
        r5 = 0;
        r0 = r14;
        r3 = r8;
        r0.open(r1, r2, r3, r4, r5);
        if (r8 == r7) goto L_0x01b6;
    L_0x01b3:
        r9.setScaled(r7);
    L_0x01b6:
        return r7;
    L_0x01b7:
        r9 = r17;
        r8 = r8 + 1;
        goto L_0x00c8;
    L_0x01bd:
        return r7;
    L_0x01be:
        r0 = r6.openPreviewRunnable;
        if (r0 == 0) goto L_0x023b;
    L_0x01c2:
        r0 = r15.getAction();
        if (r0 != r4) goto L_0x01f1;
    L_0x01c8:
        r0 = r6.startX;
        r0 = (float) r0;
        r1 = r15.getX();
        r0 = r0 - r1;
        r0 = (double) r0;
        r4 = r6.startY;
        r4 = (float) r4;
        r5 = r15.getY();
        r4 = r4 - r5;
        r4 = (double) r4;
        r0 = java.lang.Math.hypot(r0, r4);
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (double) r4;
        r7 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r7 <= 0) goto L_0x023b;
    L_0x01e9:
        r0 = r6.openPreviewRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r6.openPreviewRunnable = r3;
        goto L_0x023b;
    L_0x01f1:
        r0 = r6.openPreviewRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r6.openPreviewRunnable = r3;
        goto L_0x023b;
    L_0x01f9:
        r1 = new org.telegram.ui.-$$Lambda$ContentPreviewViewer$EMKDqwNyTHEkiYf1BXP5lN4E1U8;
        r4 = r18;
        r1.<init>(r0, r4);
        r4 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r4);
        r0 = r6.openPreviewRunnable;
        if (r0 == 0) goto L_0x020f;
    L_0x0209:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r6.openPreviewRunnable = r3;
        goto L_0x023b;
    L_0x020f:
        r0 = r14.isVisible();
        if (r0 == 0) goto L_0x023b;
    L_0x0215:
        r14.close();
        r0 = r6.currentPreviewCell;
        if (r0 == 0) goto L_0x023b;
    L_0x021c:
        r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell;
        if (r1 == 0) goto L_0x0226;
    L_0x0220:
        r0 = (org.telegram.ui.Cells.StickerEmojiCell) r0;
        r0.setScaled(r2);
        goto L_0x0239;
    L_0x0226:
        r1 = r0 instanceof org.telegram.ui.Cells.StickerCell;
        if (r1 == 0) goto L_0x0230;
    L_0x022a:
        r0 = (org.telegram.ui.Cells.StickerCell) r0;
        r0.setScaled(r2);
        goto L_0x0239;
    L_0x0230:
        r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell;
        if (r1 == 0) goto L_0x0239;
    L_0x0234:
        r0 = (org.telegram.ui.Cells.ContextLinkCell) r0;
        r0.setScaled(r2);
    L_0x0239:
        r6.currentPreviewCell = r3;
    L_0x023b:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onTouch(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, java.lang.Object, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate):boolean");
    }

    static /* synthetic */ void lambda$onTouch$0(RecyclerListView recyclerListView, Object obj) {
        if (recyclerListView instanceof RecyclerListView) {
            recyclerListView.setOnItemClickListener((OnItemClickListener) obj);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0098 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0098 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0098 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0098 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0099  */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r9, org.telegram.ui.Components.RecyclerListView r10, int r11, org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r12) {
        /*
        r8 = this;
        r8.delegate = r12;
        r12 = r9.getAction();
        r0 = 0;
        if (r12 != 0) goto L_0x00b2;
    L_0x0009:
        r12 = r9.getX();
        r12 = (int) r12;
        r9 = r9.getY();
        r9 = (int) r9;
        r1 = r10.getChildCount();
        r2 = 0;
    L_0x0018:
        if (r2 >= r1) goto L_0x00b2;
    L_0x001a:
        r3 = 0;
        r4 = r10 instanceof org.telegram.ui.Components.RecyclerListView;
        if (r4 == 0) goto L_0x0023;
    L_0x001f:
        r3 = r10.getChildAt(r2);
    L_0x0023:
        if (r3 != 0) goto L_0x0026;
    L_0x0025:
        return r0;
    L_0x0026:
        r4 = r3.getTop();
        r5 = r3.getBottom();
        r6 = r3.getLeft();
        r7 = r3.getRight();
        if (r4 > r9) goto L_0x00ae;
    L_0x0038:
        if (r5 < r9) goto L_0x00ae;
    L_0x003a:
        if (r6 > r12) goto L_0x00ae;
    L_0x003c:
        if (r7 >= r12) goto L_0x0040;
    L_0x003e:
        goto L_0x00ae;
    L_0x0040:
        r1 = r3 instanceof org.telegram.ui.Cells.StickerEmojiCell;
        r2 = -1;
        r4 = 1;
        if (r1 == 0) goto L_0x0056;
    L_0x0046:
        r1 = r3;
        r1 = (org.telegram.ui.Cells.StickerEmojiCell) r1;
        r1 = r1.showingBitmap();
        if (r1 == 0) goto L_0x0095;
    L_0x004f:
        r1 = r8.centerImage;
        r1.setRoundRadius(r0);
    L_0x0054:
        r1 = 0;
        goto L_0x0096;
    L_0x0056:
        r1 = r3 instanceof org.telegram.ui.Cells.StickerCell;
        if (r1 == 0) goto L_0x0069;
    L_0x005a:
        r1 = r3;
        r1 = (org.telegram.ui.Cells.StickerCell) r1;
        r1 = r1.showingBitmap();
        if (r1 == 0) goto L_0x0095;
    L_0x0063:
        r1 = r8.centerImage;
        r1.setRoundRadius(r0);
        goto L_0x0054;
    L_0x0069:
        r1 = r3 instanceof org.telegram.ui.Cells.ContextLinkCell;
        if (r1 == 0) goto L_0x0095;
    L_0x006d:
        r1 = r3;
        r1 = (org.telegram.ui.Cells.ContextLinkCell) r1;
        r5 = r1.showingBitmap();
        if (r5 == 0) goto L_0x0095;
    L_0x0076:
        r5 = r1.isSticker();
        if (r5 == 0) goto L_0x0082;
    L_0x007c:
        r1 = r8.centerImage;
        r1.setRoundRadius(r0);
        goto L_0x0054;
    L_0x0082:
        r1 = r1.isGif();
        if (r1 == 0) goto L_0x0095;
    L_0x0088:
        r1 = r8.centerImage;
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1.setRoundRadius(r5);
        r1 = 1;
        goto L_0x0096;
    L_0x0095:
        r1 = -1;
    L_0x0096:
        if (r1 != r2) goto L_0x0099;
    L_0x0098:
        return r0;
    L_0x0099:
        r8.startX = r12;
        r8.startY = r9;
        r8.currentPreviewCell = r3;
        r9 = new org.telegram.ui.-$$Lambda$ContentPreviewViewer$wFY_75sBoPTmhZIpv5QBGVqAeaE;
        r9.<init>(r8, r10, r11, r1);
        r8.openPreviewRunnable = r9;
        r9 = r8.openPreviewRunnable;
        r10 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r9, r10);
        return r4;
    L_0x00ae:
        r2 = r2 + 1;
        goto L_0x0018;
    L_0x00b2:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onInterceptTouchEvent(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate):boolean");
    }

    public /* synthetic */ void lambda$onInterceptTouchEvent$1$ContentPreviewViewer(RecyclerListView recyclerListView, int i, int i2) {
        if (this.openPreviewRunnable != null) {
            recyclerListView.setOnItemClickListener(null);
            recyclerListView.requestDisallowInterceptTouchEvent(true);
            this.openPreviewRunnable = null;
            setParentActivity((Activity) recyclerListView.getContext());
            setKeyboardHeight(i);
            this.clearsInputField = false;
            View view = this.currentPreviewCell;
            if (view instanceof StickerEmojiCell) {
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
                open(stickerEmojiCell.getSticker(), null, i2, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject());
                stickerEmojiCell.setScaled(true);
            } else if (view instanceof StickerCell) {
                StickerCell stickerCell = (StickerCell) view;
                open(stickerCell.getSticker(), null, i2, false, stickerCell.getParentObject());
                stickerCell.setScaled(true);
                this.clearsInputField = stickerCell.isClearsInputField();
            } else if (view instanceof ContextLinkCell) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) view;
                open(contextLinkCell.getDocument(), contextLinkCell.getBotInlineResult(), i2, false, null);
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
            if (VERSION.SDK_INT >= 21) {
                this.windowView.setFitsSystemWindows(true);
                this.windowView.setOnApplyWindowInsetsListener(new -$$Lambda$ContentPreviewViewer$-mc9Jej9PVWpKQwOMkaWMnnQlFE(this));
            }
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new -$$Lambda$ContentPreviewViewer$SmRs4xgfa5hV52_8SV4XVqx9cV4(this));
            this.windowLayoutParams = new LayoutParams();
            LayoutParams layoutParams = this.windowLayoutParams;
            layoutParams.height = -1;
            layoutParams.format = -3;
            layoutParams.width = -1;
            layoutParams.gravity = 48;
            layoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
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

    public void open(Document document, BotInlineResult botInlineResult, int i, boolean z, Object obj) {
        Document document2 = document;
        BotInlineResult botInlineResult2 = botInlineResult;
        int i2 = i;
        String str = "window";
        if (!(this.parentActivity == null || this.windowView == null)) {
            this.stickerEmojiLayout = null;
            if (i2 == 0) {
                if (document2 != null) {
                    InputStickerSet inputStickerSet;
                    if (textPaint == null) {
                        textPaint = new TextPaint(1);
                        textPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
                    }
                    for (int i3 = 0; i3 < document2.attributes.size(); i3++) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) document2.attributes.get(i3);
                        if (documentAttribute instanceof TL_documentAttributeSticker) {
                            inputStickerSet = documentAttribute.stickerset;
                            if (inputStickerSet != null) {
                                break;
                            }
                        }
                    }
                    inputStickerSet = null;
                    if (inputStickerSet != null) {
                        try {
                            if (this.visibleDialog != null) {
                                this.visibleDialog.setOnDismissListener(null);
                                this.visibleDialog.dismiss();
                                this.visibleDialog = null;
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 1300);
                    }
                    this.currentStickerSet = inputStickerSet;
                    this.parentObject = obj;
                    this.centerImage.setImage(ImageLocation.getForDocument(document), null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90), document2), null, "webp", (Object) this.currentStickerSet, 1);
                    for (int i4 = 0; i4 < document2.attributes.size(); i4++) {
                        DocumentAttribute documentAttribute2 = (DocumentAttribute) document2.attributes.get(i4);
                        if ((documentAttribute2 instanceof TL_documentAttributeSticker) && !TextUtils.isEmpty(documentAttribute2.alt)) {
                            this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(documentAttribute2.alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            break;
                        }
                    }
                } else {
                    return;
                }
            }
            String str2 = "gif";
            ImageReceiver imageReceiver;
            ImageLocation forDocument;
            ImageLocation forDocument2;
            int i5;
            StringBuilder stringBuilder;
            if (document2 != null) {
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
                imageReceiver = this.centerImage;
                forDocument = ImageLocation.getForDocument(document);
                forDocument2 = ImageLocation.getForDocument(closestPhotoSizeWithSize, document2);
                i5 = document2.size;
                stringBuilder = new StringBuilder();
                stringBuilder.append(str2);
                stringBuilder.append(document2);
                imageReceiver.setImage(forDocument, null, forDocument2, "90_90_b", i5, null, stringBuilder.toString(), 0);
            } else if (botInlineResult2 != null) {
                WebDocument webDocument = botInlineResult2.content;
                if (webDocument != null) {
                    imageReceiver = this.centerImage;
                    forDocument = ImageLocation.getForWebFile(WebFile.createWithWebDocument(webDocument));
                    forDocument2 = ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.thumb));
                    i5 = botInlineResult2.content.size;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append(botInlineResult2);
                    imageReceiver.setImage(forDocument, null, forDocument2, "90_90_b", i5, null, stringBuilder.toString(), 1);
                } else {
                    return;
                }
            }
            AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
            AndroidUtilities.runOnUIThread(this.showSheetRunnable, 2000);
            this.currentContentType = i2;
            this.currentDocument = document2;
            this.inlineResult = botInlineResult2;
            this.containerView.invalidate();
            if (!this.isVisible) {
                AndroidUtilities.lockOrientation(this.parentActivity);
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService(str)).removeView(this.windowView);
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                ((WindowManager) this.parentActivity.getSystemService(str)).addView(this.windowView, this.windowLayoutParams);
                this.isVisible = true;
                this.showProgress = 0.0f;
                this.lastTouchY = -10000.0f;
                this.currentMoveYProgress = 0.0f;
                this.finalMoveY = 0.0f;
                this.currentMoveY = 0.0f;
                this.moveY = 0.0f;
                this.lastUpdateTime = System.currentTimeMillis();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(4));
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
                FileLog.e(e);
            }
            this.currentDocument = null;
            this.currentStickerSet = null;
            this.delegate = null;
            this.isVisible = false;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(4));
        }
    }

    public void destroy() {
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
            FileLog.e(e);
        }
        if (this.parentActivity != null) {
            FrameLayout frameLayout = this.windowView;
            if (frameLayout != null) {
                try {
                    if (frameLayout.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                    }
                    this.windowView = null;
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                Instance = null;
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(4));
            }
        }
    }

    private float rubberYPoisition(float f, float f2) {
        float f3 = 1.0f;
        f2 = -((1.0f - (1.0f / (((Math.abs(f) * 0.55f) / f2) + 1.0f))) * f2);
        if (f >= 0.0f) {
            f3 = -1.0f;
        }
        return f2 * f3;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0172  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x014d  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    private void onDraw(android.graphics.Canvas r10) {
        /*
        r9 = this;
        r0 = r9.containerView;
        if (r0 == 0) goto L_0x01cc;
    L_0x0004:
        r0 = r9.backgroundDrawable;
        if (r0 != 0) goto L_0x000a;
    L_0x0008:
        goto L_0x01cc;
    L_0x000a:
        r1 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r2 = r9.showProgress;
        r2 = r2 * r1;
        r1 = (int) r2;
        r0.setAlpha(r1);
        r0 = r9.backgroundDrawable;
        r1 = r9.containerView;
        r1 = r1.getWidth();
        r2 = r9.containerView;
        r2 = r2.getHeight();
        r3 = 0;
        r0.setBounds(r3, r3, r1, r2);
        r0 = r9.backgroundDrawable;
        r0.draw(r10);
        r10.save();
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 21;
        if (r0 < r1) goto L_0x004a;
    L_0x0034:
        r0 = r9.lastInsets;
        if (r0 == 0) goto L_0x004a;
    L_0x0038:
        r0 = r0.getStableInsetBottom();
        r1 = r9.lastInsets;
        r1 = r1.getStableInsetTop();
        r0 = r0 + r1;
        r1 = r9.lastInsets;
        r1 = r1.getStableInsetTop();
        goto L_0x004d;
    L_0x004a:
        r1 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r0 = 0;
    L_0x004d:
        r2 = r9.currentContentType;
        r4 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r5 = 1;
        if (r2 != r5) goto L_0x006b;
    L_0x0054:
        r2 = r9.containerView;
        r2 = r2.getWidth();
        r6 = r9.containerView;
        r6 = r6.getHeight();
        r6 = r6 - r0;
        r2 = java.lang.Math.min(r2, r6);
        r6 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r6;
        goto L_0x0082;
    L_0x006b:
        r2 = r9.containerView;
        r2 = r2.getWidth();
        r6 = r9.containerView;
        r6 = r6.getHeight();
        r6 = r6 - r0;
        r2 = java.lang.Math.min(r2, r6);
        r2 = (float) r2;
        r6 = NUM; // 0x3fe66666 float:1.8 double:5.29670043E-315;
        r2 = r2 / r6;
        r2 = (int) r2;
    L_0x0082:
        r6 = r9.containerView;
        r6 = r6.getWidth();
        r6 = r6 / 2;
        r6 = (float) r6;
        r7 = r9.moveY;
        r8 = r2 / 2;
        r8 = r8 + r1;
        r1 = r9.stickerEmojiLayout;
        if (r1 == 0) goto L_0x0098;
    L_0x0094:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0098:
        r8 = r8 + r3;
        r1 = r9.containerView;
        r1 = r1.getHeight();
        r1 = r1 - r0;
        r0 = r9.keyboardHeight;
        r1 = r1 - r0;
        r1 = r1 / 2;
        r0 = java.lang.Math.max(r8, r1);
        r0 = (float) r0;
        r7 = r7 + r0;
        r10.translate(r6, r7);
        r0 = r9.showProgress;
        r1 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
        r3 = r0 * r1;
        r3 = r3 / r1;
        r1 = (float) r2;
        r1 = r1 * r3;
        r1 = (int) r1;
        r2 = r9.centerImage;
        r2.setAlpha(r0);
        r0 = r9.centerImage;
        r2 = -r1;
        r2 = r2 / 2;
        r0.setImageCoords(r2, r2, r1, r1);
        r0 = r9.centerImage;
        r0.draw(r10);
        r0 = r9.currentContentType;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r0 != r5) goto L_0x011f;
    L_0x00d2:
        r0 = r9.slideUpDrawable;
        if (r0 == 0) goto L_0x011f;
    L_0x00d6:
        r0 = r0.getIntrinsicWidth();
        r2 = r9.slideUpDrawable;
        r2 = r2.getIntrinsicHeight();
        r3 = r9.centerImage;
        r3 = r3.getDrawRegion();
        r3 = r3.top;
        r4 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = r9.currentMoveY;
        r7 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = (float) r7;
        r6 = r6 / r7;
        r6 = r6 * r5;
        r6 = r6 + r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = (float) r4;
        r3 = r3 - r4;
        r3 = (int) r3;
        r4 = r9.slideUpDrawable;
        r5 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r6 = r9.currentMoveYProgress;
        r6 = r1 - r6;
        r6 = r6 * r5;
        r5 = (int) r6;
        r4.setAlpha(r5);
        r4 = r9.slideUpDrawable;
        r5 = -r0;
        r5 = r5 / 2;
        r2 = -r2;
        r2 = r2 + r3;
        r0 = r0 / 2;
        r4.setBounds(r5, r2, r0, r3);
        r0 = r9.slideUpDrawable;
        r0.draw(r10);
    L_0x011f:
        r0 = r9.stickerEmojiLayout;
        if (r0 == 0) goto L_0x0144;
    L_0x0123:
        r0 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = -r0;
        r0 = (float) r0;
        r2 = r9.centerImage;
        r2 = r2.getImageHeight();
        r2 = -r2;
        r2 = r2 / 2;
        r3 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r2 = (float) r2;
        r10.translate(r0, r2);
        r0 = r9.stickerEmojiLayout;
        r0.draw(r10);
    L_0x0144:
        r10.restore();
        r10 = r9.isVisible;
        r0 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        if (r10 == 0) goto L_0x0172;
    L_0x014d:
        r10 = r9.showProgress;
        r10 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r10 == 0) goto L_0x01cc;
    L_0x0153:
        r2 = java.lang.System.currentTimeMillis();
        r4 = r9.lastUpdateTime;
        r4 = r2 - r4;
        r9.lastUpdateTime = r2;
        r10 = r9.showProgress;
        r2 = (float) r4;
        r2 = r2 / r0;
        r10 = r10 + r2;
        r9.showProgress = r10;
        r10 = r9.containerView;
        r10.invalidate();
        r10 = r9.showProgress;
        r10 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r10 <= 0) goto L_0x01cc;
    L_0x016f:
        r9.showProgress = r1;
        goto L_0x01cc;
    L_0x0172:
        r10 = r9.showProgress;
        r1 = 0;
        r10 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r10 == 0) goto L_0x01cc;
    L_0x0179:
        r2 = java.lang.System.currentTimeMillis();
        r4 = r9.lastUpdateTime;
        r4 = r2 - r4;
        r9.lastUpdateTime = r2;
        r10 = r9.showProgress;
        r2 = (float) r4;
        r2 = r2 / r0;
        r10 = r10 - r2;
        r9.showProgress = r10;
        r10 = r9.containerView;
        r10.invalidate();
        r10 = r9.showProgress;
        r10 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r10 >= 0) goto L_0x0197;
    L_0x0195:
        r9.showProgress = r1;
    L_0x0197:
        r10 = r9.showProgress;
        r10 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r10 != 0) goto L_0x01cc;
    L_0x019d:
        r10 = r9.centerImage;
        r0 = 0;
        r10.setImageBitmap(r0);
        r10 = r9.parentActivity;
        org.telegram.messenger.AndroidUtilities.unlockOrientation(r10);
        r10 = new org.telegram.ui.-$$Lambda$ContentPreviewViewer$3zyytvnhTcdtAb2UIfBrX-cZ_go;
        r10.<init>(r9);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r10);
        r10 = r9.windowView;	 Catch:{ Exception -> 0x01c8 }
        r10 = r10.getParent();	 Catch:{ Exception -> 0x01c8 }
        if (r10 == 0) goto L_0x01cc;
    L_0x01b8:
        r10 = r9.parentActivity;	 Catch:{ Exception -> 0x01c8 }
        r0 = "window";
        r10 = r10.getSystemService(r0);	 Catch:{ Exception -> 0x01c8 }
        r10 = (android.view.WindowManager) r10;	 Catch:{ Exception -> 0x01c8 }
        r0 = r9.windowView;	 Catch:{ Exception -> 0x01c8 }
        r10.removeView(r0);	 Catch:{ Exception -> 0x01c8 }
        goto L_0x01cc;
    L_0x01c8:
        r10 = move-exception;
        org.telegram.messenger.FileLog.e(r10);
    L_0x01cc:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onDraw(android.graphics.Canvas):void");
    }

    public /* synthetic */ void lambda$onDraw$4$ContentPreviewViewer() {
        this.centerImage.setImageBitmap(null);
    }
}
