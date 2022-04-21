package org.telegram.ui;

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
import android.view.ViewGroup;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class ContentPreviewViewer {
    private static final int CONTENT_TYPE_GIF = 1;
    private static final int CONTENT_TYPE_NONE = -1;
    private static final int CONTENT_TYPE_STICKER = 0;
    private static volatile ContentPreviewViewer Instance = null;
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
    public TLRPC.Document currentDocument;
    private float currentMoveY;
    /* access modifiers changed from: private */
    public float currentMoveYProgress;
    private View currentPreviewCell;
    /* access modifiers changed from: private */
    public String currentQuery;
    /* access modifiers changed from: private */
    public TLRPC.InputStickerSet currentStickerSet;
    /* access modifiers changed from: private */
    public ContentPreviewViewerDelegate delegate;
    /* access modifiers changed from: private */
    public float finalMoveY;
    /* access modifiers changed from: private */
    public SendMessagesHelper.ImportingSticker importingSticker;
    /* access modifiers changed from: private */
    public TLRPC.BotInlineResult inlineResult;
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
            boolean canDelete;
            String str;
            int i;
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (ContentPreviewViewer.this.currentContentType == 0) {
                    boolean inFavs = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).isStickerInFavorites(ContentPreviewViewer.this.currentDocument);
                    BottomSheet.Builder builder = new BottomSheet.Builder(ContentPreviewViewer.this.parentActivity, true, ContentPreviewViewer.this.resourcesProvider);
                    ArrayList<CharSequence> items = new ArrayList<>();
                    ArrayList<Integer> actions = new ArrayList<>();
                    ArrayList<Integer> icons = new ArrayList<>();
                    if (ContentPreviewViewer.this.delegate != null) {
                        if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                            items.add(LocaleController.getString("SendStickerPreview", NUM));
                            icons.add(NUM);
                            actions.add(0);
                        }
                        if (!ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                            items.add(LocaleController.getString("SendWithoutSound", NUM));
                            icons.add(NUM);
                            actions.add(6);
                        }
                        if (ContentPreviewViewer.this.delegate.canSchedule()) {
                            items.add(LocaleController.getString("Schedule", NUM));
                            icons.add(NUM);
                            actions.add(3);
                        }
                        if (ContentPreviewViewer.this.currentStickerSet != null && ContentPreviewViewer.this.delegate.needOpen()) {
                            items.add(LocaleController.formatString("ViewPackPreview", NUM, new Object[0]));
                            icons.add(NUM);
                            actions.add(1);
                        }
                        if (ContentPreviewViewer.this.delegate.needRemove()) {
                            items.add(LocaleController.getString("ImportStickersRemoveMenu", NUM));
                            icons.add(NUM);
                            actions.add(5);
                        }
                    }
                    if (!MessageObject.isMaskDocument(ContentPreviewViewer.this.currentDocument) && (inFavs || (MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).canAddStickerToFavorites() && MessageObject.isStickerHasSet(ContentPreviewViewer.this.currentDocument)))) {
                        if (inFavs) {
                            i = NUM;
                            str = "DeleteFromFavorites";
                        } else {
                            i = NUM;
                            str = "AddToFavorites";
                        }
                        items.add(LocaleController.getString(str, i));
                        icons.add(Integer.valueOf(inFavs ? NUM : NUM));
                        actions.add(2);
                    }
                    if (ContentPreviewViewer.this.isRecentSticker) {
                        items.add(LocaleController.getString("DeleteFromRecent", NUM));
                        icons.add(NUM);
                        actions.add(4);
                    }
                    if (!items.isEmpty()) {
                        int[] ic = new int[icons.size()];
                        for (int a = 0; a < icons.size(); a++) {
                            ic[a] = icons.get(a).intValue();
                        }
                        builder.setItems((CharSequence[]) items.toArray(new CharSequence[0]), ic, new ContentPreviewViewer$1$$ExternalSyntheticLambda1(this, actions, inFavs));
                        builder.setDimBehind(false);
                        BottomSheet unused = ContentPreviewViewer.this.visibleDialog = builder.create();
                        ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new ContentPreviewViewer$1$$ExternalSyntheticLambda2(this));
                        ContentPreviewViewer.this.visibleDialog.show();
                        ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                        if (ContentPreviewViewer.this.delegate != null && ContentPreviewViewer.this.delegate.needRemove()) {
                            BottomSheet.BottomSheetCell cell = ContentPreviewViewer.this.visibleDialog.getItemViews().get(0);
                            cell.setTextColor(ContentPreviewViewer.this.getThemedColor("dialogTextRed"));
                            cell.setIconColor(ContentPreviewViewer.this.getThemedColor("dialogRedIcon"));
                        }
                    }
                } else if (ContentPreviewViewer.this.delegate != null) {
                    boolean unused2 = ContentPreviewViewer.this.animateY = true;
                    BottomSheet unused3 = ContentPreviewViewer.this.visibleDialog = new BottomSheet(ContentPreviewViewer.this.parentActivity, false) {
                        /* access modifiers changed from: protected */
                        public void onContainerTranslationYChanged(float translationY) {
                            if (ContentPreviewViewer.this.animateY) {
                                ViewGroup sheetContainer = getSheetContainer();
                                if (ContentPreviewViewer.this.finalMoveY == 0.0f) {
                                    float unused = ContentPreviewViewer.this.finalMoveY = 0.0f;
                                    float unused2 = ContentPreviewViewer.this.startMoveY = ContentPreviewViewer.this.moveY;
                                }
                                float unused3 = ContentPreviewViewer.this.currentMoveYProgress = 1.0f - Math.min(1.0f, translationY / ((float) this.containerView.getMeasuredHeight()));
                                float unused4 = ContentPreviewViewer.this.moveY = ContentPreviewViewer.this.startMoveY + ((ContentPreviewViewer.this.finalMoveY - ContentPreviewViewer.this.startMoveY) * ContentPreviewViewer.this.currentMoveYProgress);
                                ContentPreviewViewer.this.containerView.invalidate();
                                if (ContentPreviewViewer.this.currentMoveYProgress == 1.0f) {
                                    boolean unused5 = ContentPreviewViewer.this.animateY = false;
                                }
                            }
                        }
                    };
                    ArrayList<CharSequence> items2 = new ArrayList<>();
                    ArrayList<Integer> actions2 = new ArrayList<>();
                    ArrayList<Integer> icons2 = new ArrayList<>();
                    if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                        items2.add(LocaleController.getString("SendGifPreview", NUM));
                        icons2.add(NUM);
                        actions2.add(0);
                    }
                    if (ContentPreviewViewer.this.delegate.canSchedule()) {
                        items2.add(LocaleController.getString("Schedule", NUM));
                        icons2.add(NUM);
                        actions2.add(3);
                    }
                    if (ContentPreviewViewer.this.currentDocument != null) {
                        boolean hasRecentGif = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).hasRecentGif(ContentPreviewViewer.this.currentDocument);
                        canDelete = hasRecentGif;
                        if (hasRecentGif) {
                            items2.add(LocaleController.formatString("Delete", NUM, new Object[0]));
                            icons2.add(NUM);
                            actions2.add(1);
                        } else {
                            items2.add(LocaleController.formatString("SaveToGIFs", NUM, new Object[0]));
                            icons2.add(NUM);
                            actions2.add(2);
                        }
                    } else {
                        canDelete = false;
                    }
                    int[] ic2 = new int[icons2.size()];
                    for (int a2 = 0; a2 < icons2.size(); a2++) {
                        ic2[a2] = icons2.get(a2).intValue();
                    }
                    ContentPreviewViewer.this.visibleDialog.setItems((CharSequence[]) items2.toArray(new CharSequence[0]), ic2, new ContentPreviewViewer$1$$ExternalSyntheticLambda0(this, actions2));
                    ContentPreviewViewer.this.visibleDialog.setDimBehind(false);
                    ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new ContentPreviewViewer$1$$ExternalSyntheticLambda3(this));
                    ContentPreviewViewer.this.visibleDialog.show();
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    if (canDelete) {
                        ContentPreviewViewer.this.visibleDialog.setItemColor(items2.size() - 1, ContentPreviewViewer.this.getThemedColor("dialogTextRed2"), ContentPreviewViewer.this.getThemedColor("dialogRedIcon"));
                    }
                }
            }
        }

        /* renamed from: lambda$run$1$org-telegram-ui-ContentPreviewViewer$1  reason: not valid java name */
        public /* synthetic */ void m2045lambda$run$1$orgtelegramuiContentPreviewViewer$1(ArrayList actions, boolean inFavs, DialogInterface dialog, int which) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) actions.get(which)).intValue() == 0 || ((Integer) actions.get(which)).intValue() == 6) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.sendSticker(ContentPreviewViewer.this.currentDocument, ContentPreviewViewer.this.currentQuery, ContentPreviewViewer.this.parentObject, ((Integer) actions.get(which)).intValue() == 0, 0);
                    }
                } else if (((Integer) actions.get(which)).intValue() == 1) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.openSet(ContentPreviewViewer.this.currentStickerSet, ContentPreviewViewer.this.clearsInputField);
                    }
                } else if (((Integer) actions.get(which)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(2, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), inFavs);
                } else if (((Integer) actions.get(which)).intValue() == 3) {
                    TLRPC.Document sticker = ContentPreviewViewer.this.currentDocument;
                    Object parent = ContentPreviewViewer.this.parentObject;
                    String query = ContentPreviewViewer.this.currentQuery;
                    ContentPreviewViewerDelegate stickerPreviewViewerDelegate = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, stickerPreviewViewerDelegate.getDialogId(), new ContentPreviewViewer$1$$ExternalSyntheticLambda4(stickerPreviewViewerDelegate, sticker, query, parent));
                } else if (((Integer) actions.get(which)).intValue() == 4) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(0, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), true);
                } else if (((Integer) actions.get(which)).intValue() == 5) {
                    ContentPreviewViewer.this.delegate.remove(ContentPreviewViewer.this.importingSticker);
                }
            }
        }

        /* renamed from: lambda$run$2$org-telegram-ui-ContentPreviewViewer$1  reason: not valid java name */
        public /* synthetic */ void m2046lambda$run$2$orgtelegramuiContentPreviewViewer$1(DialogInterface dialog) {
            BottomSheet unused = ContentPreviewViewer.this.visibleDialog = null;
            ContentPreviewViewer.this.close();
        }

        /* renamed from: lambda$run$4$org-telegram-ui-ContentPreviewViewer$1  reason: not valid java name */
        public /* synthetic */ void m2047lambda$run$4$orgtelegramuiContentPreviewViewer$1(ArrayList actions, DialogInterface dialog, int which) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) actions.get(which)).intValue() == 0) {
                    ContentPreviewViewer.this.delegate.sendGif(ContentPreviewViewer.this.currentDocument != null ? ContentPreviewViewer.this.currentDocument : ContentPreviewViewer.this.inlineResult, ContentPreviewViewer.this.parentObject, true, 0);
                } else if (((Integer) actions.get(which)).intValue() == 1) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).removeRecentGif(ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) actions.get(which)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentGif(ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000));
                    MessagesController.getInstance(ContentPreviewViewer.this.currentAccount).saveGif("gif", ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) actions.get(which)).intValue() == 3) {
                    TLRPC.Document document = ContentPreviewViewer.this.currentDocument;
                    TLRPC.BotInlineResult result = ContentPreviewViewer.this.inlineResult;
                    Object parent = ContentPreviewViewer.this.parentObject;
                    ContentPreviewViewerDelegate stickerPreviewViewerDelegate = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog((Context) ContentPreviewViewer.this.parentActivity, stickerPreviewViewerDelegate.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ContentPreviewViewer$1$$ExternalSyntheticLambda5(stickerPreviewViewerDelegate, document, result, parent), ContentPreviewViewer.this.resourcesProvider);
                }
            }
        }

        /* JADX WARNING: type inference failed for: r3v0, types: [org.telegram.tgnet.TLRPC$BotInlineResult] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static /* synthetic */ void lambda$run$3(org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r1, org.telegram.tgnet.TLRPC.Document r2, org.telegram.tgnet.TLRPC.BotInlineResult r3, java.lang.Object r4, boolean r5, int r6) {
            /*
                if (r2 == 0) goto L_0x0004
                r0 = r2
                goto L_0x0005
            L_0x0004:
                r0 = r3
            L_0x0005:
                r1.sendGif(r0, r4, r5, r6)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.AnonymousClass1.lambda$run$3(org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, org.telegram.tgnet.TLRPC$Document, org.telegram.tgnet.TLRPC$BotInlineResult, java.lang.Object, boolean, int):void");
        }

        /* renamed from: lambda$run$5$org-telegram-ui-ContentPreviewViewer$1  reason: not valid java name */
        public /* synthetic */ void m2048lambda$run$5$orgtelegramuiContentPreviewViewer$1(DialogInterface dialog) {
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

    public interface ContentPreviewViewerDelegate {
        boolean canSchedule();

        long getDialogId();

        String getQuery(boolean z);

        void gifAddedOrDeleted();

        boolean isInScheduleMode();

        boolean needMenu();

        boolean needOpen();

        boolean needRemove();

        boolean needSend();

        void openSet(TLRPC.InputStickerSet inputStickerSet, boolean z);

        void remove(SendMessagesHelper.ImportingSticker importingSticker);

        void sendGif(Object obj, Object obj2, boolean z, int i);

        void sendSticker(TLRPC.Document document, String str, Object obj, boolean z, int i);

        /* renamed from: org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$needRemove(ContentPreviewViewerDelegate _this) {
                return false;
            }

            public static void $default$remove(ContentPreviewViewerDelegate _this, SendMessagesHelper.ImportingSticker sticker) {
            }

            public static String $default$getQuery(ContentPreviewViewerDelegate _this, boolean isGif) {
                return null;
            }

            public static boolean $default$needOpen(ContentPreviewViewerDelegate _this) {
                return true;
            }

            public static void $default$sendGif(ContentPreviewViewerDelegate _this, Object gif, Object parent, boolean notify, int scheduleDate) {
            }

            public static void $default$gifAddedOrDeleted(ContentPreviewViewerDelegate _this) {
            }

            public static boolean $default$needMenu(ContentPreviewViewerDelegate _this) {
                return true;
            }
        }
    }

    public static ContentPreviewViewer getInstance() {
        ContentPreviewViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    ContentPreviewViewer contentPreviewViewer = new ContentPreviewViewer();
                    localInstance = contentPreviewViewer;
                    Instance = contentPreviewViewer;
                }
            }
        }
        return localInstance;
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

    public boolean onTouch(MotionEvent event, RecyclerListView listView, int height, Object listener, ContentPreviewViewerDelegate contentPreviewViewerDelegate, Theme.ResourcesProvider resourcesProvider2) {
        ContextLinkCell view;
        int contentType;
        boolean z;
        boolean z2;
        RecyclerListView recyclerListView = listView;
        this.delegate = contentPreviewViewerDelegate;
        this.resourcesProvider = resourcesProvider2;
        boolean z3 = false;
        if (this.openPreviewRunnable == null && !isVisible()) {
            Object obj = listener;
            return false;
        } else if (event.getAction() == 1 || event.getAction() == 3 || event.getAction() == 6) {
            AndroidUtilities.runOnUIThread(new ContentPreviewViewer$$ExternalSyntheticLambda2(recyclerListView, listener), 150);
            Runnable runnable = this.openPreviewRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.openPreviewRunnable = null;
                return false;
            } else if (!isVisible()) {
                return false;
            } else {
                close();
                View view2 = this.currentPreviewCell;
                if (view2 == null) {
                    return false;
                }
                if (view2 instanceof StickerEmojiCell) {
                    ((StickerEmojiCell) view2).setScaled(false);
                } else if (view2 instanceof StickerCell) {
                    ((StickerCell) view2).setScaled(false);
                } else if (view2 instanceof ContextLinkCell) {
                    ((ContextLinkCell) view2).setScaled(false);
                }
                this.currentPreviewCell = null;
                return false;
            }
        } else if (event.getAction() == 0) {
            Object obj2 = listener;
            return false;
        } else if (this.isVisible) {
            if (event.getAction() != 2) {
                return true;
            }
            if (this.currentContentType == 1) {
                if (this.visibleDialog == null && this.showProgress == 1.0f) {
                    if (this.lastTouchY == -10000.0f) {
                        this.lastTouchY = event.getY();
                        this.currentMoveY = 0.0f;
                        this.moveY = 0.0f;
                    } else {
                        float newY = event.getY();
                        float f = this.currentMoveY + (newY - this.lastTouchY);
                        this.currentMoveY = f;
                        this.lastTouchY = newY;
                        if (f > 0.0f) {
                            this.currentMoveY = 0.0f;
                        } else if (f < ((float) (-AndroidUtilities.dp(60.0f)))) {
                            this.currentMoveY = (float) (-AndroidUtilities.dp(60.0f));
                        }
                        this.moveY = rubberYPoisition(this.currentMoveY, (float) AndroidUtilities.dp(200.0f));
                        this.containerView.invalidate();
                        if (this.currentMoveY <= ((float) (-AndroidUtilities.dp(55.0f)))) {
                            AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                            this.showSheetRunnable.run();
                            return true;
                        }
                    }
                }
                return true;
            }
            int x = (int) event.getX();
            int y = (int) event.getY();
            int count = listView.getChildCount();
            int a = 0;
            while (a < count) {
                if (recyclerListView instanceof RecyclerListView) {
                    view = recyclerListView.getChildAt(a);
                } else {
                    view = null;
                }
                if (view == null) {
                    return z3;
                }
                int top = view.getTop();
                int bottom = view.getBottom();
                int left = view.getLeft();
                int right = view.getRight();
                if (top > y || bottom < y || left > x) {
                    int i = left;
                    int i2 = bottom;
                    int i3 = top;
                    ContextLinkCell contextLinkCell = view;
                } else if (right >= x) {
                    if (view instanceof StickerEmojiCell) {
                        this.centerImage.setRoundRadius((int) z3);
                        contentType = 0;
                    } else if (view instanceof StickerCell) {
                        this.centerImage.setRoundRadius((int) z3);
                        contentType = 0;
                    } else {
                        if (view instanceof ContextLinkCell) {
                            ContextLinkCell cell = view;
                            if (cell.isSticker()) {
                                this.centerImage.setRoundRadius(z3 ? 1 : 0);
                                contentType = 0;
                            } else if (cell.isGif()) {
                                this.centerImage.setRoundRadius(AndroidUtilities.dp(6.0f));
                                contentType = 1;
                            }
                        }
                        contentType = -1;
                    }
                    if (contentType != -1) {
                        View view3 = this.currentPreviewCell;
                        if (view == view3) {
                            return true;
                        }
                        if (view3 instanceof StickerEmojiCell) {
                            z = false;
                            ((StickerEmojiCell) view3).setScaled(false);
                        } else if (view3 instanceof StickerCell) {
                            z = false;
                            ((StickerCell) view3).setScaled(false);
                        } else if (view3 instanceof ContextLinkCell) {
                            z = false;
                            ((ContextLinkCell) view3).setScaled(false);
                        } else {
                            z = false;
                        }
                        this.currentPreviewCell = view;
                        setKeyboardHeight(height);
                        this.clearsInputField = z;
                        View view4 = this.currentPreviewCell;
                        int i4 = right;
                        if ((view4 instanceof StickerEmojiCell) != 0) {
                            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view4;
                            TLRPC.Document sticker = stickerEmojiCell.getSticker();
                            SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
                            String emoji = stickerEmojiCell.getEmoji();
                            ContentPreviewViewerDelegate contentPreviewViewerDelegate2 = this.delegate;
                            int i5 = left;
                            int i6 = bottom;
                            int i7 = top;
                            ContextLinkCell contextLinkCell2 = view;
                            int i8 = a;
                            int i9 = count;
                            int i10 = y;
                            open(sticker, stickerPath, emoji, contentPreviewViewerDelegate2 != null ? contentPreviewViewerDelegate2.getQuery(false) : null, (TLRPC.BotInlineResult) null, contentType, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject(), resourcesProvider2);
                            stickerEmojiCell.setScaled(true);
                            z2 = true;
                        } else {
                            int i11 = bottom;
                            int i12 = top;
                            ContextLinkCell contextLinkCell3 = view;
                            int i13 = a;
                            int i14 = count;
                            int i15 = y;
                            if (view4 instanceof StickerCell) {
                                StickerCell stickerCell = (StickerCell) view4;
                                TLRPC.Document sticker2 = stickerCell.getSticker();
                                ContentPreviewViewerDelegate contentPreviewViewerDelegate3 = this.delegate;
                                StickerCell stickerCell2 = stickerCell;
                                open(sticker2, (SendMessagesHelper.ImportingSticker) null, (String) null, contentPreviewViewerDelegate3 != null ? contentPreviewViewerDelegate3.getQuery(false) : null, (TLRPC.BotInlineResult) null, contentType, false, stickerCell.getParentObject(), resourcesProvider2);
                                stickerCell2.setScaled(true);
                                this.clearsInputField = stickerCell2.isClearsInputField();
                                z2 = true;
                            } else if (view4 instanceof ContextLinkCell) {
                                ContextLinkCell contextLinkCell4 = (ContextLinkCell) view4;
                                TLRPC.Document document = contextLinkCell4.getDocument();
                                ContentPreviewViewerDelegate contentPreviewViewerDelegate4 = this.delegate;
                                open(document, (SendMessagesHelper.ImportingSticker) null, (String) null, contentPreviewViewerDelegate4 != null ? contentPreviewViewerDelegate4.getQuery(true) : null, contextLinkCell4.getBotInlineResult(), contentType, false, contextLinkCell4.getBotInlineResult() != null ? contextLinkCell4.getInlineBot() : contextLinkCell4.getParentObject(), resourcesProvider2);
                                z2 = true;
                                if (contentType != 1) {
                                    contextLinkCell4.setScaled(true);
                                }
                            } else {
                                z2 = true;
                            }
                        }
                        runSmoothHaptic();
                        return z2;
                    }
                    int i16 = left;
                    int i17 = bottom;
                    int i18 = top;
                    ContextLinkCell contextLinkCell5 = view;
                    int i19 = a;
                    int i20 = count;
                    int i21 = y;
                    return true;
                }
                a++;
                ContentPreviewViewerDelegate contentPreviewViewerDelegate5 = contentPreviewViewerDelegate;
                count = count;
                y = y;
                z3 = false;
            }
            int i22 = a;
            int i23 = count;
            int i24 = y;
            return true;
        } else if (this.openPreviewRunnable == null) {
            Object obj3 = listener;
            return false;
        } else if (event.getAction() != 2) {
            AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
            this.openPreviewRunnable = null;
            Object obj4 = listener;
            return false;
        } else if (Math.hypot((double) (((float) this.startX) - event.getX()), (double) (((float) this.startY) - event.getY())) > ((double) AndroidUtilities.dp(10.0f))) {
            AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
            this.openPreviewRunnable = null;
            Object obj5 = listener;
            return false;
        } else {
            Object obj6 = listener;
            return false;
        }
    }

    static /* synthetic */ void lambda$onTouch$0(RecyclerListView listView, Object listener) {
        if (listView instanceof RecyclerListView) {
            listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) listener);
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

    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b2 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00b4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r22, org.telegram.ui.Components.RecyclerListView r23, int r24, org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r25, org.telegram.ui.ActionBar.Theme.ResourcesProvider r26) {
        /*
            r21 = this;
            r6 = r21
            r7 = r23
            r8 = r25
            r6.delegate = r8
            r9 = r26
            r6.resourcesProvider = r9
            int r0 = r22.getAction()
            r1 = 0
            if (r0 != 0) goto L_0x00e6
            float r0 = r22.getX()
            int r10 = (int) r0
            float r0 = r22.getY()
            int r11 = (int) r0
            int r12 = r23.getChildCount()
            r0 = 0
            r13 = r0
        L_0x0023:
            if (r13 >= r12) goto L_0x00e6
            r0 = 0
            boolean r2 = r7 instanceof org.telegram.ui.Components.RecyclerListView
            if (r2 == 0) goto L_0x0030
            android.view.View r0 = r7.getChildAt(r13)
            r14 = r0
            goto L_0x0031
        L_0x0030:
            r14 = r0
        L_0x0031:
            if (r14 != 0) goto L_0x0034
            return r1
        L_0x0034:
            int r15 = r14.getTop()
            int r5 = r14.getBottom()
            int r3 = r14.getLeft()
            int r2 = r14.getRight()
            if (r15 > r11) goto L_0x00db
            if (r5 < r11) goto L_0x00db
            if (r3 > r10) goto L_0x00db
            if (r2 >= r10) goto L_0x004e
            goto L_0x00e1
        L_0x004e:
            r0 = -1
            boolean r4 = r14 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r4 == 0) goto L_0x0064
            r4 = r14
            org.telegram.ui.Cells.StickerEmojiCell r4 = (org.telegram.ui.Cells.StickerEmojiCell) r4
            boolean r4 = r4.showingBitmap()
            if (r4 == 0) goto L_0x00ae
            r0 = 0
            org.telegram.messenger.ImageReceiver r4 = r6.centerImage
            r4.setRoundRadius((int) r1)
            r1 = r0
            goto L_0x00af
        L_0x0064:
            boolean r4 = r14 instanceof org.telegram.ui.Cells.StickerCell
            if (r4 == 0) goto L_0x0079
            r4 = r14
            org.telegram.ui.Cells.StickerCell r4 = (org.telegram.ui.Cells.StickerCell) r4
            boolean r4 = r4.showingBitmap()
            if (r4 == 0) goto L_0x00ae
            r0 = 0
            org.telegram.messenger.ImageReceiver r4 = r6.centerImage
            r4.setRoundRadius((int) r1)
            r1 = r0
            goto L_0x00af
        L_0x0079:
            boolean r4 = r14 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r4 == 0) goto L_0x00ae
            r4 = r14
            org.telegram.ui.Cells.ContextLinkCell r4 = (org.telegram.ui.Cells.ContextLinkCell) r4
            boolean r16 = r4.showingBitmap()
            if (r16 == 0) goto L_0x00ae
            boolean r16 = r4.isSticker()
            if (r16 == 0) goto L_0x0097
            r0 = 0
            r16 = r0
            org.telegram.messenger.ImageReceiver r0 = r6.centerImage
            r0.setRoundRadius((int) r1)
            r1 = r16
            goto L_0x00af
        L_0x0097:
            boolean r16 = r4.isGif()
            if (r16 == 0) goto L_0x00ae
            r0 = 1
            org.telegram.messenger.ImageReceiver r1 = r6.centerImage
            r17 = 1086324736(0x40CLASSNAME, float:6.0)
            r18 = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.setRoundRadius((int) r0)
            r1 = r18
            goto L_0x00af
        L_0x00ae:
            r1 = r0
        L_0x00af:
            r0 = -1
            if (r1 != r0) goto L_0x00b4
            r0 = 0
            return r0
        L_0x00b4:
            r6.startX = r10
            r6.startY = r11
            r6.currentPreviewCell = r14
            r4 = r1
            org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda4 r0 = new org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda4
            r16 = r0
            r18 = r1
            r1 = r21
            r17 = r2
            r2 = r23
            r19 = r3
            r3 = r24
            r20 = r5
            r5 = r26
            r0.<init>(r1, r2, r3, r4, r5)
            r6.openPreviewRunnable = r0
            r1 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
            r0 = 1
            return r0
        L_0x00db:
            r17 = r2
            r19 = r3
            r20 = r5
        L_0x00e1:
            int r13 = r13 + 1
            r1 = 0
            goto L_0x0023
        L_0x00e6:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onInterceptTouchEvent(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, org.telegram.ui.ActionBar.Theme$ResourcesProvider):boolean");
    }

    /* renamed from: lambda$onInterceptTouchEvent$1$org-telegram-ui-ContentPreviewViewer  reason: not valid java name */
    public /* synthetic */ void m2042xa62d51cd(RecyclerListView listView, int height, int contentTypeFinal, Theme.ResourcesProvider resourcesProvider2) {
        RecyclerListView recyclerListView = listView;
        if (this.openPreviewRunnable != null) {
            String str = null;
            recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) null);
            recyclerListView.requestDisallowInterceptTouchEvent(true);
            this.openPreviewRunnable = null;
            setParentActivity((Activity) listView.getContext());
            setKeyboardHeight(height);
            this.clearsInputField = false;
            View view = this.currentPreviewCell;
            if (view instanceof StickerEmojiCell) {
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
                TLRPC.Document sticker = stickerEmojiCell.getSticker();
                SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
                String emoji = stickerEmojiCell.getEmoji();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate = this.delegate;
                if (contentPreviewViewerDelegate != null) {
                    str = contentPreviewViewerDelegate.getQuery(false);
                }
                open(sticker, stickerPath, emoji, str, (TLRPC.BotInlineResult) null, contentTypeFinal, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject(), resourcesProvider2);
                stickerEmojiCell.setScaled(true);
                int i = contentTypeFinal;
            } else if (view instanceof StickerCell) {
                StickerCell stickerCell = (StickerCell) view;
                TLRPC.Document sticker2 = stickerCell.getSticker();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate2 = this.delegate;
                if (contentPreviewViewerDelegate2 != null) {
                    str = contentPreviewViewerDelegate2.getQuery(false);
                }
                open(sticker2, (SendMessagesHelper.ImportingSticker) null, (String) null, str, (TLRPC.BotInlineResult) null, contentTypeFinal, false, stickerCell.getParentObject(), resourcesProvider2);
                stickerCell.setScaled(true);
                this.clearsInputField = stickerCell.isClearsInputField();
                int i2 = contentTypeFinal;
            } else if (view instanceof ContextLinkCell) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) view;
                TLRPC.Document document = contextLinkCell.getDocument();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate3 = this.delegate;
                if (contentPreviewViewerDelegate3 != null) {
                    str = contentPreviewViewerDelegate3.getQuery(true);
                }
                open(document, (SendMessagesHelper.ImportingSticker) null, (String) null, str, contextLinkCell.getBotInlineResult(), contentTypeFinal, false, contextLinkCell.getBotInlineResult() != null ? contextLinkCell.getInlineBot() : contextLinkCell.getParentObject(), resourcesProvider2);
                if (contentTypeFinal != 1) {
                    contextLinkCell.setScaled(true);
                }
            } else {
                int i3 = contentTypeFinal;
            }
            this.currentPreviewCell.performHapticFeedback(0, 2);
        }
    }

    public void setDelegate(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        this.delegate = contentPreviewViewerDelegate;
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
            if (Build.VERSION.SDK_INT >= 21) {
                this.windowView.setFitsSystemWindows(true);
                this.windowView.setOnApplyWindowInsetsListener(new ContentPreviewViewer$$ExternalSyntheticLambda0(this));
            }
            AnonymousClass2 r0 = new FrameLayoutDrawer(activity) {
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
            this.containerView = r0;
            r0.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new ContentPreviewViewer$$ExternalSyntheticLambda1(this));
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            layoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            if (Build.VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -NUM;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.centerImage.setAspectFit(true);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setParentView(this.containerView);
        }
    }

    /* renamed from: lambda$setParentActivity$2$org-telegram-ui-ContentPreviewViewer  reason: not valid java name */
    public /* synthetic */ WindowInsets m2043lambda$setParentActivity$2$orgtelegramuiContentPreviewViewer(View v, WindowInsets insets) {
        this.lastInsets = insets;
        return insets;
    }

    /* renamed from: lambda$setParentActivity$3$org-telegram-ui-ContentPreviewViewer  reason: not valid java name */
    public /* synthetic */ boolean m2044lambda$setParentActivity$3$orgtelegramuiContentPreviewViewer(View v, MotionEvent event) {
        if (event.getAction() == 1 || event.getAction() == 6 || event.getAction() == 3) {
            close();
        }
        return true;
    }

    public void setKeyboardHeight(int height) {
        this.keyboardHeight = height;
    }

    public void open(TLRPC.Document document, SendMessagesHelper.ImportingSticker sticker, String emojiPath, String query, TLRPC.BotInlineResult botInlineResult, int contentType, boolean isRecent, Object parent, Theme.ResourcesProvider resourcesProvider2) {
        TLRPC.InputStickerSet newSet;
        ContentPreviewViewerDelegate contentPreviewViewerDelegate;
        TLRPC.Document document2 = document;
        SendMessagesHelper.ImportingSticker importingSticker2 = sticker;
        String str = emojiPath;
        TLRPC.BotInlineResult botInlineResult2 = botInlineResult;
        int i = contentType;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        if (this.parentActivity == null) {
            String str2 = query;
            Object obj = parent;
        } else if (this.windowView == null) {
            String str3 = query;
            Object obj2 = parent;
        } else {
            this.resourcesProvider = resourcesProvider3;
            this.isRecentSticker = isRecent;
            this.stickerEmojiLayout = null;
            if (i != 0) {
                if (document2 != null) {
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
                    TLRPC.VideoSize videoSize = MessageObject.getDocumentVideoThumb(document);
                    ImageLocation location = ImageLocation.getForDocument(document);
                    location.imageType = 2;
                    if (videoSize != null) {
                        this.centerImage.setImage(location, (String) null, ImageLocation.getForDocument(videoSize, document2), (String) null, ImageLocation.getForDocument(thumb, document2), "90_90_b", (Drawable) null, document2.size, (String) null, "gif" + document2, 0);
                    } else {
                        this.centerImage.setImage(location, (String) null, ImageLocation.getForDocument(thumb, document2), "90_90_b", document2.size, (String) null, "gif" + document2, 0);
                    }
                } else if (botInlineResult2 == null) {
                    String str4 = query;
                    Object obj3 = parent;
                    return;
                } else if (botInlineResult2.content != null) {
                    if (!(botInlineResult2.thumb instanceof TLRPC.TL_webDocument) || !"video/mp4".equals(botInlineResult2.thumb.mime_type)) {
                        this.centerImage.setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.content)), (String) null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.thumb)), "90_90_b", botInlineResult2.content.size, (String) null, "gif" + botInlineResult2, 1);
                    } else {
                        this.centerImage.setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.content)), (String) null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.thumb)), (String) null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.thumb)), "90_90_b", (Drawable) null, botInlineResult2.content.size, (String) null, "gif" + botInlineResult2, 1);
                    }
                } else {
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                AndroidUtilities.runOnUIThread(this.showSheetRunnable, 2000);
            } else if (document2 != null || importingSticker2 != null) {
                if (textPaint == null) {
                    TextPaint textPaint2 = new TextPaint(1);
                    textPaint = textPaint2;
                    textPaint2.setTextSize((float) AndroidUtilities.dp(24.0f));
                }
                if (document2 != null) {
                    int a = 0;
                    while (true) {
                        if (a >= document2.attributes.size()) {
                            newSet = null;
                            break;
                        }
                        TLRPC.DocumentAttribute attribute = document2.attributes.get(a);
                        if ((attribute instanceof TLRPC.TL_documentAttributeSticker) && attribute.stickerset != null) {
                            newSet = attribute.stickerset;
                            break;
                        }
                        a++;
                    }
                    if (newSet != null && ((contentPreviewViewerDelegate = this.delegate) == null || contentPreviewViewerDelegate.needMenu())) {
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
                    this.currentStickerSet = newSet;
                    TLRPC.PhotoSize thumb2 = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
                    if (MessageObject.isVideoStickerDocument(document)) {
                        this.centerImage.setImage(ImageLocation.getForDocument(document), (String) null, ImageLocation.getForDocument(thumb2, document2), (String) null, (Drawable) null, 0, "webp", this.currentStickerSet, 1);
                    } else {
                        this.centerImage.setImage(ImageLocation.getForDocument(document), (String) null, ImageLocation.getForDocument(thumb2, document2), (String) null, "webp", (Object) this.currentStickerSet, 1);
                    }
                    int a2 = 0;
                    while (true) {
                        if (a2 >= document2.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute2 = document2.attributes.get(a2);
                        if ((attribute2 instanceof TLRPC.TL_documentAttributeSticker) && !TextUtils.isEmpty(attribute2.alt)) {
                            this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(attribute2.alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            break;
                        } else {
                            a2++;
                            boolean z = isRecent;
                        }
                    }
                } else if (importingSticker2 != null) {
                    this.centerImage.setImage(importingSticker2.path, (String) null, (Drawable) null, importingSticker2.animated ? "tgs" : null, 0);
                    if (str != null) {
                        this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(str, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
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
            this.currentContentType = i;
            this.currentDocument = document2;
            this.importingSticker = importingSticker2;
            this.currentQuery = query;
            this.inlineResult = botInlineResult2;
            this.parentObject = parent;
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

    private float rubberYPoisition(float offset, float factor) {
        float f = 1.0f;
        float f2 = -((1.0f - (1.0f / (((0.55f * Math.abs(offset)) / factor) + 1.0f))) * factor);
        if (offset >= 0.0f) {
            f = -1.0f;
        }
        return f2 * f;
    }

    /* access modifiers changed from: private */
    public void onDraw(Canvas canvas) {
        ColorDrawable colorDrawable;
        int top;
        int size;
        Drawable drawable;
        WindowInsets windowInsets;
        if (this.containerView != null && (colorDrawable = this.backgroundDrawable) != null) {
            colorDrawable.setAlpha((int) (this.showProgress * 180.0f));
            int i = 0;
            this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
            this.backgroundDrawable.draw(canvas);
            canvas.save();
            int insets = 0;
            if (Build.VERSION.SDK_INT < 21 || (windowInsets = this.lastInsets) == null) {
                top = AndroidUtilities.statusBarHeight;
            } else {
                insets = windowInsets.getStableInsetBottom() + this.lastInsets.getStableInsetTop();
                top = this.lastInsets.getStableInsetTop();
            }
            if (this.currentContentType == 1) {
                size = Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets) - AndroidUtilities.dp(40.0f);
            } else {
                size = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets)) / 1.8f);
            }
            float width = (float) (this.containerView.getWidth() / 2);
            float f = this.moveY;
            int i2 = (size / 2) + top;
            if (this.stickerEmojiLayout != null) {
                i = AndroidUtilities.dp(40.0f);
            }
            canvas.translate(width, f + ((float) Math.max(i2 + i, ((this.containerView.getHeight() - insets) - this.keyboardHeight) / 2)));
            float f2 = this.showProgress;
            int size2 = (int) (((float) size) * ((f2 * 0.8f) / 0.8f));
            this.centerImage.setAlpha(f2);
            this.centerImage.setImageCoords((float) ((-size2) / 2), (float) ((-size2) / 2), (float) size2, (float) size2);
            this.centerImage.draw(canvas);
            if (this.currentContentType == 1 && (drawable = this.slideUpDrawable) != null) {
                int w = drawable.getIntrinsicWidth();
                int h = this.slideUpDrawable.getIntrinsicHeight();
                int y = (int) (this.centerImage.getDrawRegion().top - ((float) AndroidUtilities.dp(((this.currentMoveY / ((float) AndroidUtilities.dp(60.0f))) * 6.0f) + 17.0f)));
                this.slideUpDrawable.setAlpha((int) ((1.0f - this.currentMoveYProgress) * 255.0f));
                this.slideUpDrawable.setBounds((-w) / 2, (-h) + y, w / 2, y);
                this.slideUpDrawable.draw(canvas);
            }
            if (this.stickerEmojiLayout != null) {
                canvas.translate((float) (-AndroidUtilities.dp(50.0f)), ((-this.centerImage.getImageHeight()) / 2.0f) - ((float) AndroidUtilities.dp(30.0f)));
                this.stickerEmojiLayout.draw(canvas);
            }
            canvas.restore();
            if (this.isVisible) {
                if (this.showProgress != 1.0f) {
                    long newTime = System.currentTimeMillis();
                    this.lastUpdateTime = newTime;
                    this.showProgress += ((float) (newTime - this.lastUpdateTime)) / 120.0f;
                    this.containerView.invalidate();
                    if (this.showProgress > 1.0f) {
                        this.showProgress = 1.0f;
                    }
                }
            } else if (this.showProgress != 0.0f) {
                long newTime2 = System.currentTimeMillis();
                this.lastUpdateTime = newTime2;
                this.showProgress -= ((float) (newTime2 - this.lastUpdateTime)) / 120.0f;
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

    /* renamed from: lambda$onDraw$4$org-telegram-ui-ContentPreviewViewer  reason: not valid java name */
    public /* synthetic */ void m2041lambda$onDraw$4$orgtelegramuiContentPreviewViewer() {
        this.centerImage.setImageBitmap((Bitmap) null);
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
