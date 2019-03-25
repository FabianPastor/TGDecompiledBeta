package org.telegram.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class ContentPreviewViewer {
    private static final int CONTENT_TYPE_GIF = 1;
    private static final int CONTENT_TYPE_NONE = -1;
    private static final int CONTENT_TYPE_STICKER = 0;
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ContentPreviewViewer Instance = null;
    private static TextPaint textPaint;
    private boolean animateY;
    private ColorDrawable backgroundDrawable = new ColorDrawable(NUM);
    private ImageReceiver centerImage = new ImageReceiver();
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
    private boolean isVisible = false;
    private int keyboardHeight = AndroidUtilities.dp(200.0f);
    private WindowInsets lastInsets;
    private float lastTouchY;
    private long lastUpdateTime;
    private float moveY = 0.0f;
    private Runnable openPreviewRunnable;
    private Activity parentActivity;
    private float showProgress;
    private Runnable showSheetRunnable = new Runnable() {
        public void run() {
            if (ContentPreviewViewer.this.parentActivity != null) {
                ArrayList<CharSequence> items;
                ArrayList<Integer> actions;
                ArrayList<Integer> icons;
                int[] ic;
                int a;
                if (ContentPreviewViewer.this.currentContentType == 0) {
                    if (ContentPreviewViewer.this.currentStickerSet != null) {
                        boolean inFavs = DataQuery.getInstance(ContentPreviewViewer.this.currentAccount).isStickerInFavorites(ContentPreviewViewer.this.currentDocument);
                        Builder builder = new Builder(ContentPreviewViewer.this.parentActivity);
                        items = new ArrayList();
                        actions = new ArrayList();
                        icons = new ArrayList();
                        if (ContentPreviewViewer.this.delegate != null) {
                            if (ContentPreviewViewer.this.delegate.needSend()) {
                                items.add(LocaleController.getString("SendStickerPreview", NUM));
                                icons.add(Integer.valueOf(NUM));
                                actions.add(Integer.valueOf(0));
                            }
                            if (ContentPreviewViewer.this.delegate.needOpen()) {
                                items.add(LocaleController.formatString("ViewPackPreview", NUM, new Object[0]));
                                icons.add(Integer.valueOf(NUM));
                                actions.add(Integer.valueOf(1));
                            }
                        }
                        if (!MessageObject.isMaskDocument(ContentPreviewViewer.this.currentDocument) && (inFavs || DataQuery.getInstance(ContentPreviewViewer.this.currentAccount).canAddStickerToFavorites())) {
                            items.add(inFavs ? LocaleController.getString("DeleteFromFavorites", NUM) : LocaleController.getString("AddToFavorites", NUM));
                            icons.add(Integer.valueOf(inFavs ? NUM : NUM));
                            actions.add(Integer.valueOf(2));
                        }
                        if (!items.isEmpty()) {
                            ic = new int[icons.size()];
                            for (a = 0; a < icons.size(); a++) {
                                ic[a] = ((Integer) icons.get(a)).intValue();
                            }
                            builder.setItems((CharSequence[]) items.toArray(new CharSequence[0]), ic, new ContentPreviewViewer$1$$Lambda$0(this, actions, inFavs));
                            builder.setDimBehind(false);
                            ContentPreviewViewer.this.visibleDialog = builder.create();
                            ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new ContentPreviewViewer$1$$Lambda$1(this));
                            ContentPreviewViewer.this.visibleDialog.show();
                            ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                        }
                    }
                } else if (ContentPreviewViewer.this.delegate != null) {
                    ContentPreviewViewer.this.animateY = true;
                    ContentPreviewViewer.this.visibleDialog = new BottomSheet(ContentPreviewViewer.this.parentActivity, false) {
                        /* Access modifiers changed, original: protected */
                        public void onContainerTranslationYChanged(float translationY) {
                            if (ContentPreviewViewer.this.animateY) {
                                ViewGroup container = getSheetContainer();
                                if (ContentPreviewViewer.this.finalMoveY == 0.0f) {
                                    ContentPreviewViewer.this.finalMoveY = 0.0f;
                                    ContentPreviewViewer.this.startMoveY = ContentPreviewViewer.this.moveY;
                                }
                                ContentPreviewViewer.this.currentMoveYProgress = 1.0f - Math.min(1.0f, translationY / ((float) this.containerView.getMeasuredHeight()));
                                ContentPreviewViewer.this.moveY = ContentPreviewViewer.this.startMoveY + ((ContentPreviewViewer.this.finalMoveY - ContentPreviewViewer.this.startMoveY) * ContentPreviewViewer.this.currentMoveYProgress);
                                ContentPreviewViewer.this.containerView.invalidate();
                                if (ContentPreviewViewer.this.currentMoveYProgress == 1.0f) {
                                    ContentPreviewViewer.this.animateY = false;
                                }
                            }
                        }
                    };
                    items = new ArrayList();
                    actions = new ArrayList();
                    icons = new ArrayList();
                    if (ContentPreviewViewer.this.delegate.needSend()) {
                        items.add(LocaleController.getString("SendGifPreview", NUM));
                        icons.add(Integer.valueOf(NUM));
                        actions.add(Integer.valueOf(0));
                    }
                    boolean canDelete = DataQuery.getInstance(ContentPreviewViewer.this.currentAccount).hasRecentGif(ContentPreviewViewer.this.currentDocument);
                    if (canDelete) {
                        items.add(LocaleController.formatString("Delete", NUM, new Object[0]));
                        icons.add(Integer.valueOf(NUM));
                        actions.add(Integer.valueOf(1));
                    } else {
                        items.add(LocaleController.formatString("SaveToGIFs", NUM, new Object[0]));
                        icons.add(Integer.valueOf(NUM));
                        actions.add(Integer.valueOf(2));
                    }
                    ic = new int[icons.size()];
                    for (a = 0; a < icons.size(); a++) {
                        ic[a] = ((Integer) icons.get(a)).intValue();
                    }
                    ContentPreviewViewer.this.visibleDialog.setItems((CharSequence[]) items.toArray(new CharSequence[0]), ic, new ContentPreviewViewer$1$$Lambda$2(this, actions));
                    ContentPreviewViewer.this.visibleDialog.setDimBehind(false);
                    ContentPreviewViewer.this.visibleDialog.setOnDismissListener(new ContentPreviewViewer$1$$Lambda$3(this));
                    ContentPreviewViewer.this.visibleDialog.show();
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    if (canDelete) {
                        ContentPreviewViewer.this.visibleDialog.setItemColor(items.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                    }
                }
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$run$0$ContentPreviewViewer$1(ArrayList actions, boolean inFavs, DialogInterface dialog, int which) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) actions.get(which)).intValue() == 0) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.sendSticker(ContentPreviewViewer.this.currentDocument, ContentPreviewViewer.this.currentStickerSet);
                    }
                } else if (((Integer) actions.get(which)).intValue() == 1) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.openSet(ContentPreviewViewer.this.currentStickerSet);
                    }
                } else if (((Integer) actions.get(which)).intValue() == 2) {
                    DataQuery.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(2, ContentPreviewViewer.this.currentStickerSet, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), inFavs);
                }
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$run$1$ContentPreviewViewer$1(DialogInterface dialog) {
            ContentPreviewViewer.this.visibleDialog = null;
            ContentPreviewViewer.this.close();
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$run$2$ContentPreviewViewer$1(ArrayList actions, DialogInterface dialog, int which) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                if (((Integer) actions.get(which)).intValue() == 0) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.sendGif(ContentPreviewViewer.this.currentDocument);
                    }
                } else if (((Integer) actions.get(which)).intValue() == 1) {
                    DataQuery.getInstance(ContentPreviewViewer.this.currentAccount).removeRecentGif(ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) actions.get(which)).intValue() == 2) {
                    DataQuery.getInstance(ContentPreviewViewer.this.currentAccount).addRecentGif(ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000));
                    MessagesController.getInstance(ContentPreviewViewer.this.currentAccount).saveGif("gif", ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                }
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$run$3$ContentPreviewViewer$1(DialogInterface dialog) {
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
        void gifAddedOrDeleted();

        boolean needOpen();

        boolean needSend();

        void openSet(InputStickerSet inputStickerSet);

        void sendGif(Document document);

        void sendSticker(Document document, Object obj);
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
        Throwable th;
        ContentPreviewViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        ContentPreviewViewer localInstance2 = new ContentPreviewViewer();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public void reset() {
        if (this.openPreviewRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
            this.openPreviewRunnable = null;
        }
        if (this.currentPreviewCell != null) {
            if (this.currentPreviewCell instanceof StickerEmojiCell) {
                ((StickerEmojiCell) this.currentPreviewCell).setScaled(false);
            } else if (this.currentPreviewCell instanceof StickerCell) {
                ((StickerCell) this.currentPreviewCell).setScaled(false);
            } else if (this.currentPreviewCell instanceof ContextLinkCell) {
                ((ContextLinkCell) this.currentPreviewCell).setScaled(false);
            }
            this.currentPreviewCell = null;
        }
    }

    public boolean onTouch(MotionEvent event, RecyclerListView listView, int height, Object listener, ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        this.delegate = contentPreviewViewerDelegate;
        if (this.openPreviewRunnable != null || isVisible()) {
            if (event.getAction() == 1 || event.getAction() == 3 || event.getAction() == 6) {
                AndroidUtilities.runOnUIThread(new ContentPreviewViewer$$Lambda$0(listView, listener), 150);
                if (this.openPreviewRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
                    this.openPreviewRunnable = null;
                } else if (isVisible()) {
                    close();
                    if (this.currentPreviewCell != null) {
                        if (this.currentPreviewCell instanceof StickerEmojiCell) {
                            ((StickerEmojiCell) this.currentPreviewCell).setScaled(false);
                        } else if (this.currentPreviewCell instanceof StickerCell) {
                            ((StickerCell) this.currentPreviewCell).setScaled(false);
                        } else if (this.currentPreviewCell instanceof ContextLinkCell) {
                            ((ContextLinkCell) this.currentPreviewCell).setScaled(false);
                        }
                        this.currentPreviewCell = null;
                    }
                }
            } else if (event.getAction() != 0) {
                if (this.isVisible) {
                    if (event.getAction() == 2) {
                        if (this.currentContentType == 1) {
                            if (this.visibleDialog == null && this.showProgress == 1.0f) {
                                if (this.lastTouchY == -10000.0f) {
                                    this.lastTouchY = event.getY();
                                    this.currentMoveY = 0.0f;
                                    this.moveY = 0.0f;
                                } else {
                                    float newY = event.getY();
                                    this.currentMoveY += newY - this.lastTouchY;
                                    this.lastTouchY = newY;
                                    if (this.currentMoveY > 0.0f) {
                                        this.currentMoveY = 0.0f;
                                    } else if (this.currentMoveY < ((float) (-AndroidUtilities.dp(60.0f)))) {
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
                            View view = null;
                            if (listView instanceof RecyclerListView) {
                                view = listView.getChildAt(a);
                            }
                            if (view == null) {
                                return false;
                            }
                            int top = view.getTop();
                            int bottom = view.getBottom();
                            int left = view.getLeft();
                            int right = view.getRight();
                            if (top > y || bottom < y || left > x || right < x) {
                                a++;
                            } else {
                                int contentType = -1;
                                if (view instanceof StickerEmojiCell) {
                                    contentType = 0;
                                    this.centerImage.setRoundRadius(0);
                                } else if (view instanceof StickerCell) {
                                    contentType = 0;
                                    this.centerImage.setRoundRadius(0);
                                } else if (view instanceof ContextLinkCell) {
                                    ContextLinkCell cell = (ContextLinkCell) view;
                                    if (cell.isSticker()) {
                                        contentType = 0;
                                        this.centerImage.setRoundRadius(0);
                                    } else if (cell.isGif()) {
                                        contentType = 1;
                                        this.centerImage.setRoundRadius(AndroidUtilities.dp(6.0f));
                                    }
                                }
                                if (!(contentType == -1 || view == this.currentPreviewCell)) {
                                    if (this.currentPreviewCell instanceof StickerEmojiCell) {
                                        ((StickerEmojiCell) this.currentPreviewCell).setScaled(false);
                                    } else if (this.currentPreviewCell instanceof StickerCell) {
                                        ((StickerCell) this.currentPreviewCell).setScaled(false);
                                    } else if (this.currentPreviewCell instanceof ContextLinkCell) {
                                        ((ContextLinkCell) this.currentPreviewCell).setScaled(false);
                                    }
                                    this.currentPreviewCell = view;
                                    setKeyboardHeight(height);
                                    if (this.currentPreviewCell instanceof StickerEmojiCell) {
                                        StickerEmojiCell stickerEmojiCell = this.currentPreviewCell;
                                        open(stickerEmojiCell.getSticker(), contentType, ((StickerEmojiCell) this.currentPreviewCell).isRecent());
                                        stickerEmojiCell.setScaled(true);
                                    } else if (this.currentPreviewCell instanceof StickerCell) {
                                        StickerCell stickerCell = this.currentPreviewCell;
                                        open(stickerCell.getSticker(), contentType, false);
                                        stickerCell.setScaled(true);
                                    } else if (this.currentPreviewCell instanceof ContextLinkCell) {
                                        ContextLinkCell contextLinkCell = this.currentPreviewCell;
                                        open(contextLinkCell.getDocument(), contentType, false);
                                        if (contentType != 1) {
                                            contextLinkCell.setScaled(true);
                                        }
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                    return true;
                } else if (this.openPreviewRunnable != null) {
                    if (event.getAction() != 2) {
                        AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
                        this.openPreviewRunnable = null;
                    } else if (Math.hypot((double) (((float) this.startX) - event.getX()), (double) (((float) this.startY) - event.getY())) > ((double) AndroidUtilities.dp(10.0f))) {
                        AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
                        this.openPreviewRunnable = null;
                    }
                }
            }
        }
        return false;
    }

    static final /* synthetic */ void lambda$onTouch$0$ContentPreviewViewer(RecyclerListView listView, Object listener) {
        if (listView instanceof RecyclerListView) {
            listView.setOnItemClickListener((OnItemClickListener) listener);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event, RecyclerListView listView, int height, ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        this.delegate = contentPreviewViewerDelegate;
        if (event.getAction() == 0) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int count = listView.getChildCount();
            int a = 0;
            while (a < count) {
                View view = null;
                if (listView instanceof RecyclerListView) {
                    view = listView.getChildAt(a);
                }
                if (view == null) {
                    return false;
                }
                int top = view.getTop();
                int bottom = view.getBottom();
                int left = view.getLeft();
                int right = view.getRight();
                if (top > y || bottom < y || left > x || right < x) {
                    a++;
                } else {
                    int contentType = -1;
                    if (view instanceof StickerEmojiCell) {
                        if (((StickerEmojiCell) view).showingBitmap()) {
                            contentType = 0;
                            this.centerImage.setRoundRadius(0);
                        }
                    } else if (view instanceof StickerCell) {
                        if (((StickerCell) view).showingBitmap()) {
                            contentType = 0;
                            this.centerImage.setRoundRadius(0);
                        }
                    } else if (view instanceof ContextLinkCell) {
                        ContextLinkCell cell = (ContextLinkCell) view;
                        if (cell.showingBitmap()) {
                            if (cell.isSticker()) {
                                contentType = 0;
                                this.centerImage.setRoundRadius(0);
                            } else if (cell.isGif()) {
                                contentType = 1;
                                this.centerImage.setRoundRadius(AndroidUtilities.dp(6.0f));
                            }
                        }
                    }
                    if (contentType == -1) {
                        return false;
                    }
                    this.startX = x;
                    this.startY = y;
                    this.currentPreviewCell = view;
                    this.openPreviewRunnable = new ContentPreviewViewer$$Lambda$1(this, listView, height, contentType);
                    AndroidUtilities.runOnUIThread(this.openPreviewRunnable, 200);
                    return true;
                }
            }
        }
        return false;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onInterceptTouchEvent$1$ContentPreviewViewer(RecyclerListView listView, int height, int contentTypeFinal) {
        if (this.openPreviewRunnable != null) {
            listView.setOnItemClickListener((OnItemClickListener) null);
            listView.requestDisallowInterceptTouchEvent(true);
            this.openPreviewRunnable = null;
            setParentActivity((Activity) listView.getContext());
            setKeyboardHeight(height);
            if (this.currentPreviewCell instanceof StickerEmojiCell) {
                StickerEmojiCell stickerEmojiCell = this.currentPreviewCell;
                open(stickerEmojiCell.getSticker(), contentTypeFinal, ((StickerEmojiCell) this.currentPreviewCell).isRecent());
                stickerEmojiCell.setScaled(true);
            } else if (this.currentPreviewCell instanceof StickerCell) {
                StickerCell stickerCell = this.currentPreviewCell;
                open(stickerCell.getSticker(), contentTypeFinal, false);
                stickerCell.setScaled(true);
            } else if (this.currentPreviewCell instanceof ContextLinkCell) {
                ContextLinkCell contextLinkCell = this.currentPreviewCell;
                open(contextLinkCell.getDocument(), contentTypeFinal, false);
                if (contentTypeFinal != 1) {
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
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.slideUpDrawable = this.parentActivity.getResources().getDrawable(NUM);
            this.windowView = new FrameLayout(activity);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            if (VERSION.SDK_INT >= 21) {
                this.windowView.setFitsSystemWindows(true);
                this.windowView.setOnApplyWindowInsetsListener(new ContentPreviewViewer$$Lambda$2(this));
            }
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new ContentPreviewViewer$$Lambda$3(this));
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -NUM;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.centerImage.setAspectFit(true);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setParentView(this.containerView);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ WindowInsets lambda$setParentActivity$2$ContentPreviewViewer(View v, WindowInsets insets) {
        this.lastInsets = insets;
        return insets;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$setParentActivity$3$ContentPreviewViewer(View v, MotionEvent event) {
        if (event.getAction() == 1 || event.getAction() == 6 || event.getAction() == 3) {
            close();
        }
        return true;
    }

    public void setKeyboardHeight(int height) {
        this.keyboardHeight = height;
    }

    public void open(Document document, int contentType, boolean isRecent) {
        if (this.parentActivity != null && document != null) {
            this.stickerEmojiLayout = null;
            if (contentType == 0) {
                int a;
                DocumentAttribute attribute;
                if (textPaint == null) {
                    textPaint = new TextPaint(1);
                    textPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
                }
                InputStickerSet newSet = null;
                for (a = 0; a < document.attributes.size(); a++) {
                    attribute = (DocumentAttribute) document.attributes.get(a);
                    if ((attribute instanceof TL_documentAttributeSticker) && attribute.stickerset != null) {
                        newSet = attribute.stickerset;
                        break;
                    }
                }
                if (newSet != null) {
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
                this.currentStickerSet = newSet;
                TLObject tLObject = document;
                this.centerImage.setImage(tLObject, null, FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), null, "webp", this.currentStickerSet, 1);
                for (a = 0; a < document.attributes.size(); a++) {
                    attribute = (DocumentAttribute) document.attributes.get(a);
                    if ((attribute instanceof TL_documentAttributeSticker) && !TextUtils.isEmpty(attribute.alt)) {
                        this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(attribute.alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        break;
                    }
                }
            } else {
                this.centerImage.setImage(document, null, FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), "90_90_b", document.size, null, "gif" + document, 0);
                AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                AndroidUtilities.runOnUIThread(this.showSheetRunnable, 2000);
            }
            this.currentContentType = contentType;
            this.currentDocument = document;
            this.containerView.invalidate();
            if (!this.isVisible) {
                AndroidUtilities.lockOrientation(this.parentActivity);
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
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
        if (this.parentActivity != null && this.windowView != null) {
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            Instance = null;
        }
    }

    private float rubberYPoisition(float offset, float factor) {
        float f = 1.0f;
        float f2 = -((1.0f - (1.0f / (((0.55f * Math.abs(offset)) / factor) + 1.0f))) * factor);
        if (offset >= 0.0f) {
            f = -1.0f;
        }
        return f * f2;
    }

    @SuppressLint({"DrawAllocation"})
    private void onDraw(Canvas canvas) {
        if (this.containerView != null && this.backgroundDrawable != null) {
            int top;
            int size;
            this.backgroundDrawable.setAlpha((int) (180.0f * this.showProgress));
            this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
            this.backgroundDrawable.draw(canvas);
            canvas.save();
            int insets = 0;
            if (VERSION.SDK_INT < 21 || this.lastInsets == null) {
                top = AndroidUtilities.statusBarHeight;
            } else {
                insets = this.lastInsets.getStableInsetBottom() + this.lastInsets.getStableInsetTop();
                top = this.lastInsets.getStableInsetTop();
            }
            if (this.currentContentType == 1) {
                size = Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets) - AndroidUtilities.dp(40.0f);
            } else {
                size = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets)) / 1.8f);
            }
            canvas.translate((float) (this.containerView.getWidth() / 2), ((float) Math.max((this.stickerEmojiLayout != null ? AndroidUtilities.dp(40.0f) : 0) + ((size / 2) + top), ((this.containerView.getHeight() - insets) - this.keyboardHeight) / 2)) + this.moveY);
            if (this.centerImage.getBitmap() != null) {
                size = (int) (((float) size) * ((0.8f * this.showProgress) / 0.8f));
                this.centerImage.setAlpha(this.showProgress);
                this.centerImage.setImageCoords((-size) / 2, (-size) / 2, size, size);
                this.centerImage.draw(canvas);
                if (this.currentContentType == 1 && this.slideUpDrawable != null) {
                    int w = this.slideUpDrawable.getIntrinsicWidth();
                    int h = this.slideUpDrawable.getIntrinsicHeight();
                    int y = this.centerImage.getDrawRegion().top - AndroidUtilities.dp(17.0f + (6.0f * (this.currentMoveY / ((float) AndroidUtilities.dp(60.0f)))));
                    this.slideUpDrawable.setAlpha((int) (255.0f * (1.0f - this.currentMoveYProgress)));
                    this.slideUpDrawable.setBounds((-w) / 2, (-h) + y, w / 2, y);
                    this.slideUpDrawable.draw(canvas);
                }
            }
            if (this.stickerEmojiLayout != null) {
                canvas.translate((float) (-AndroidUtilities.dp(50.0f)), (float) (((-this.centerImage.getImageHeight()) / 2) - AndroidUtilities.dp(30.0f)));
                this.stickerEmojiLayout.draw(canvas);
            }
            canvas.restore();
            long newTime;
            long dt;
            if (this.isVisible) {
                if (this.showProgress != 1.0f) {
                    newTime = System.currentTimeMillis();
                    dt = newTime - this.lastUpdateTime;
                    this.lastUpdateTime = newTime;
                    this.showProgress += ((float) dt) / 120.0f;
                    this.containerView.invalidate();
                    if (this.showProgress > 1.0f) {
                        this.showProgress = 1.0f;
                    }
                }
            } else if (this.showProgress != 0.0f) {
                newTime = System.currentTimeMillis();
                dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                this.showProgress -= ((float) dt) / 120.0f;
                this.containerView.invalidate();
                if (this.showProgress < 0.0f) {
                    this.showProgress = 0.0f;
                }
                if (this.showProgress == 0.0f) {
                    this.centerImage.setImageBitmap((Drawable) null);
                    AndroidUtilities.unlockOrientation(this.parentActivity);
                    AndroidUtilities.runOnUIThread(new ContentPreviewViewer$$Lambda$4(this));
                    try {
                        if (this.windowView.getParent() != null) {
                            ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onDraw$4$ContentPreviewViewer() {
        this.centerImage.setImageBitmap((Bitmap) null);
    }
}
