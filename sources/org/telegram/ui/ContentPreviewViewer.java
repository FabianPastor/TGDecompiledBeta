package org.telegram.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import org.telegram.messenger.AccountInstance;
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
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class ContentPreviewViewer {
    private static final int CONTENT_TYPE_GIF = 1;
    private static final int CONTENT_TYPE_NONE = -1;
    private static final int CONTENT_TYPE_STICKER = 0;
    private static volatile ContentPreviewViewer Instance = null;
    private static TextPaint textPaint;
    private ColorDrawable backgroundDrawable = new ColorDrawable(NUM);
    private float blurProgress;
    private Bitmap blurrBitmap;
    /* access modifiers changed from: private */
    public ImageReceiver centerImage = new ImageReceiver();
    /* access modifiers changed from: private */
    public boolean clearsInputField;
    /* access modifiers changed from: private */
    public boolean closeOnDismiss;
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
    public boolean drawEffect;
    /* access modifiers changed from: private */
    public ImageReceiver effectImage = new ImageReceiver();
    /* access modifiers changed from: private */
    public float finalMoveY;
    /* access modifiers changed from: private */
    public SendMessagesHelper.ImportingSticker importingSticker;
    /* access modifiers changed from: private */
    public TLRPC.BotInlineResult inlineResult;
    /* access modifiers changed from: private */
    public boolean isRecentSticker;
    private boolean isVisible = false;
    /* access modifiers changed from: private */
    public int keyboardHeight = AndroidUtilities.dp(200.0f);
    /* access modifiers changed from: private */
    public WindowInsets lastInsets;
    private float lastTouchY;
    private long lastUpdateTime;
    /* access modifiers changed from: private */
    public boolean menuVisible;
    /* access modifiers changed from: private */
    public float moveY = 0.0f;
    private Runnable openPreviewRunnable;
    private Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public Object parentObject;
    ActionBarPopupWindow popupWindow;
    /* access modifiers changed from: private */
    public Theme.ResourcesProvider resourcesProvider;
    private float showProgress;
    private Runnable showSheetRunnable = new Runnable() {
        public void run() {
            boolean canDelete;
            int top;
            final ArrayList<Integer> actions;
            ArrayList<Integer> icons;
            int top2;
            int size;
            String str;
            int i;
            if (ContentPreviewViewer.this.parentActivity != null) {
                boolean unused = ContentPreviewViewer.this.closeOnDismiss = true;
                if (ContentPreviewViewer.this.currentContentType == 0) {
                    if (!MessageObject.isPremiumSticker(ContentPreviewViewer.this.currentDocument) || AccountInstance.getInstance(ContentPreviewViewer.this.currentAccount).getUserConfig().isPremium()) {
                        final boolean inFavs = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).isStickerInFavorites(ContentPreviewViewer.this.currentDocument);
                        ArrayList<CharSequence> items = new ArrayList<>();
                        ArrayList<Integer> actions2 = new ArrayList<>();
                        ArrayList<Integer> icons2 = new ArrayList<>();
                        boolean unused2 = ContentPreviewViewer.this.menuVisible = true;
                        ContentPreviewViewer.this.containerView.invalidate();
                        if (ContentPreviewViewer.this.delegate != null) {
                            if (!ContentPreviewViewer.this.delegate.needSend() || ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                                actions = actions2;
                                icons = icons2;
                            } else {
                                items.add(LocaleController.getString("SendStickerPreview", NUM));
                                icons = icons2;
                                icons.add(NUM);
                                actions = actions2;
                                actions.add(0);
                            }
                            if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
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
                        } else {
                            actions = actions2;
                            icons = icons2;
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
                            View.OnClickListener onItemClickListener = new View.OnClickListener() {
                                public void onClick(View v) {
                                    if (ContentPreviewViewer.this.parentActivity != null) {
                                        int which = ((Integer) v.getTag()).intValue();
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
                                            AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, stickerPreviewViewerDelegate.getDialogId(), new ContentPreviewViewer$1$1$$ExternalSyntheticLambda0(stickerPreviewViewerDelegate, sticker, query, parent));
                                        } else if (((Integer) actions.get(which)).intValue() == 4) {
                                            MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(0, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), true);
                                        } else if (((Integer) actions.get(which)).intValue() == 5) {
                                            ContentPreviewViewer.this.delegate.remove(ContentPreviewViewer.this.importingSticker);
                                        }
                                        if (ContentPreviewViewer.this.popupWindow != null) {
                                            ContentPreviewViewer.this.popupWindow.dismiss();
                                        }
                                    }
                                }
                            };
                            ActionBarPopupWindow.ActionBarPopupWindowLayout previewMenu = new ActionBarPopupWindow.ActionBarPopupWindowLayout(ContentPreviewViewer.this.containerView.getContext(), NUM, ContentPreviewViewer.this.resourcesProvider);
                            for (int i2 = 0; i2 < items.size(); i2++) {
                                View item = ActionBarMenuItem.addItem(previewMenu, icons.get(i2).intValue(), items.get(i2), false, ContentPreviewViewer.this.resourcesProvider);
                                item.setTag(Integer.valueOf(i2));
                                item.setOnClickListener(onItemClickListener);
                            }
                            ContentPreviewViewer.this.popupWindow = new ActionBarPopupWindow(previewMenu, -2, -2) {
                                public void dismiss() {
                                    super.dismiss();
                                    ContentPreviewViewer.this.popupWindow = null;
                                    boolean unused = ContentPreviewViewer.this.menuVisible = false;
                                    if (ContentPreviewViewer.this.closeOnDismiss) {
                                        ContentPreviewViewer.this.close();
                                    }
                                }
                            };
                            ContentPreviewViewer.this.popupWindow.setPauseNotifications(true);
                            ContentPreviewViewer.this.popupWindow.setDismissAnimationDuration(100);
                            ContentPreviewViewer.this.popupWindow.setScaleOut(true);
                            ContentPreviewViewer.this.popupWindow.setOutsideTouchable(true);
                            ContentPreviewViewer.this.popupWindow.setClippingEnabled(true);
                            ContentPreviewViewer.this.popupWindow.setAnimationStyle(NUM);
                            ContentPreviewViewer.this.popupWindow.setFocusable(true);
                            previewMenu.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                            ContentPreviewViewer.this.popupWindow.setInputMethodMode(2);
                            ContentPreviewViewer.this.popupWindow.getContentView().setFocusableInTouchMode(true);
                            int insets = 0;
                            if (Build.VERSION.SDK_INT < 21 || ContentPreviewViewer.this.lastInsets == null) {
                                top2 = AndroidUtilities.statusBarHeight;
                            } else {
                                insets = ContentPreviewViewer.this.lastInsets.getStableInsetBottom() + ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                                top2 = ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                            }
                            if (ContentPreviewViewer.this.currentContentType == 1) {
                                size = Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - insets) - AndroidUtilities.dp(40.0f);
                            } else if (ContentPreviewViewer.this.drawEffect) {
                                size = (int) (((float) Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - insets)) - AndroidUtilities.dpf2(40.0f));
                            } else {
                                size = (int) (((float) Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - insets)) / 1.8f);
                            }
                            int y = ((int) (ContentPreviewViewer.this.moveY + ((float) Math.max((size / 2) + top2 + (ContentPreviewViewer.this.stickerEmojiLayout != null ? AndroidUtilities.dp(40.0f) : 0), ((ContentPreviewViewer.this.containerView.getHeight() - insets) - ContentPreviewViewer.this.keyboardHeight) / 2)) + ((float) (size / 2)))) + AndroidUtilities.dp(24.0f);
                            if (ContentPreviewViewer.this.drawEffect) {
                                y += AndroidUtilities.dp(24.0f);
                            }
                            ContentPreviewViewer.this.popupWindow.showAtLocation(ContentPreviewViewer.this.containerView, 0, (int) (((float) (ContentPreviewViewer.this.containerView.getMeasuredWidth() - previewMenu.getMeasuredWidth())) / 2.0f), y);
                            ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                            return;
                        }
                        return;
                    }
                    ContentPreviewViewer.this.showUnlockPremiumView();
                    boolean unused3 = ContentPreviewViewer.this.menuVisible = true;
                    ContentPreviewViewer.this.containerView.invalidate();
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                } else if (ContentPreviewViewer.this.delegate != null) {
                    boolean unused4 = ContentPreviewViewer.this.menuVisible = true;
                    ArrayList<CharSequence> items2 = new ArrayList<>();
                    ArrayList<Integer> actions3 = new ArrayList<>();
                    ArrayList<Integer> icons3 = new ArrayList<>();
                    if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                        items2.add(LocaleController.getString("SendGifPreview", NUM));
                        icons3.add(NUM);
                        actions3.add(0);
                    }
                    if (ContentPreviewViewer.this.delegate.canSchedule()) {
                        items2.add(LocaleController.getString("Schedule", NUM));
                        icons3.add(NUM);
                        actions3.add(3);
                    }
                    if (ContentPreviewViewer.this.currentDocument != null) {
                        boolean hasRecentGif = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).hasRecentGif(ContentPreviewViewer.this.currentDocument);
                        canDelete = hasRecentGif;
                        if (hasRecentGif) {
                            items2.add(LocaleController.formatString("Delete", NUM, new Object[0]));
                            icons3.add(NUM);
                            actions3.add(1);
                        } else {
                            items2.add(LocaleController.formatString("SaveToGIFs", NUM, new Object[0]));
                            icons3.add(NUM);
                            actions3.add(2);
                        }
                    } else {
                        canDelete = false;
                    }
                    int[] ic2 = new int[icons3.size()];
                    for (int a2 = 0; a2 < icons3.size(); a2++) {
                        ic2[a2] = icons3.get(a2).intValue();
                    }
                    ActionBarPopupWindow.ActionBarPopupWindowLayout previewMenu2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(ContentPreviewViewer.this.containerView.getContext(), NUM, ContentPreviewViewer.this.resourcesProvider);
                    View.OnClickListener onItemClickListener2 = new ContentPreviewViewer$1$$ExternalSyntheticLambda1(this, actions3);
                    for (int i3 = 0; i3 < items2.size(); i3++) {
                        ActionBarMenuSubItem item2 = ActionBarMenuItem.addItem(previewMenu2, icons3.get(i3).intValue(), items2.get(i3), false, ContentPreviewViewer.this.resourcesProvider);
                        item2.setTag(Integer.valueOf(i3));
                        item2.setOnClickListener(onItemClickListener2);
                        if (canDelete && i3 == items2.size() - 1) {
                            item2.setColors(ContentPreviewViewer.this.getThemedColor("dialogTextRed2"), ContentPreviewViewer.this.getThemedColor("dialogRedIcon"));
                        }
                    }
                    ContentPreviewViewer.this.popupWindow = new ActionBarPopupWindow(previewMenu2, -2, -2) {
                        public void dismiss() {
                            super.dismiss();
                            ContentPreviewViewer.this.popupWindow = null;
                            boolean unused = ContentPreviewViewer.this.menuVisible = false;
                            if (ContentPreviewViewer.this.closeOnDismiss) {
                                ContentPreviewViewer.this.close();
                            }
                        }
                    };
                    ContentPreviewViewer.this.popupWindow.setPauseNotifications(true);
                    ContentPreviewViewer.this.popupWindow.setDismissAnimationDuration(150);
                    ContentPreviewViewer.this.popupWindow.setScaleOut(true);
                    ContentPreviewViewer.this.popupWindow.setOutsideTouchable(true);
                    ContentPreviewViewer.this.popupWindow.setClippingEnabled(true);
                    ContentPreviewViewer.this.popupWindow.setAnimationStyle(NUM);
                    ContentPreviewViewer.this.popupWindow.setFocusable(true);
                    previewMenu2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                    ContentPreviewViewer.this.popupWindow.setInputMethodMode(2);
                    ContentPreviewViewer.this.popupWindow.getContentView().setFocusableInTouchMode(true);
                    int insets2 = 0;
                    if (Build.VERSION.SDK_INT < 21 || ContentPreviewViewer.this.lastInsets == null) {
                        top = AndroidUtilities.statusBarHeight;
                    } else {
                        insets2 = ContentPreviewViewer.this.lastInsets.getStableInsetBottom() + ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                        top = ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                    }
                    int size2 = Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - insets2) - AndroidUtilities.dp(40.0f);
                    ContentPreviewViewer.this.popupWindow.showAtLocation(ContentPreviewViewer.this.containerView, 0, (int) (((float) (ContentPreviewViewer.this.containerView.getMeasuredWidth() - previewMenu2.getMeasuredWidth())) / 2.0f), (int) (((float) ((int) (ContentPreviewViewer.this.moveY + ((float) Math.max((size2 / 2) + top + (ContentPreviewViewer.this.stickerEmojiLayout != null ? AndroidUtilities.dp(40.0f) : 0), ((ContentPreviewViewer.this.containerView.getHeight() - insets2) - ContentPreviewViewer.this.keyboardHeight) / 2)) + ((float) (size2 / 2))))) + (((float) AndroidUtilities.dp(24.0f)) - ContentPreviewViewer.this.moveY)));
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    if (ContentPreviewViewer.this.moveY != 0.0f) {
                        if (ContentPreviewViewer.this.finalMoveY == 0.0f) {
                            float unused5 = ContentPreviewViewer.this.finalMoveY = 0.0f;
                            ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
                            float unused6 = contentPreviewViewer.startMoveY = contentPreviewViewer.moveY;
                        }
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        valueAnimator.addUpdateListener(new ContentPreviewViewer$1$$ExternalSyntheticLambda0(this));
                        valueAnimator.setDuration(350);
                        valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        valueAnimator.start();
                    }
                }
            }
        }

        /* renamed from: lambda$run$1$org-telegram-ui-ContentPreviewViewer$1  reason: not valid java name */
        public /* synthetic */ void m3348lambda$run$1$orgtelegramuiContentPreviewViewer$1(ArrayList actions, View v) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                int which = ((Integer) v.getTag()).intValue();
                if (((Integer) actions.get(which)).intValue() == 0) {
                    ContentPreviewViewer.this.delegate.sendGif(ContentPreviewViewer.this.currentDocument != null ? ContentPreviewViewer.this.currentDocument : ContentPreviewViewer.this.inlineResult, ContentPreviewViewer.this.parentObject, true, 0);
                } else if (((Integer) actions.get(which)).intValue() == 1) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).removeRecentGif(ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) actions.get(which)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentGif(ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), true);
                    MessagesController.getInstance(ContentPreviewViewer.this.currentAccount).saveGif("gif", ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) actions.get(which)).intValue() == 3) {
                    TLRPC.Document document = ContentPreviewViewer.this.currentDocument;
                    TLRPC.BotInlineResult result = ContentPreviewViewer.this.inlineResult;
                    Object parent = ContentPreviewViewer.this.parentObject;
                    ContentPreviewViewerDelegate stickerPreviewViewerDelegate = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog((Context) ContentPreviewViewer.this.parentActivity, stickerPreviewViewerDelegate.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ContentPreviewViewer$1$$ExternalSyntheticLambda2(stickerPreviewViewerDelegate, document, result, parent), ContentPreviewViewer.this.resourcesProvider);
                }
                if (ContentPreviewViewer.this.popupWindow != null) {
                    ContentPreviewViewer.this.popupWindow.dismiss();
                }
            }
        }

        /* JADX WARNING: type inference failed for: r3v0, types: [org.telegram.tgnet.TLRPC$BotInlineResult] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static /* synthetic */ void lambda$run$0(org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r1, org.telegram.tgnet.TLRPC.Document r2, org.telegram.tgnet.TLRPC.BotInlineResult r3, java.lang.Object r4, boolean r5, int r6) {
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
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.AnonymousClass1.lambda$run$0(org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, org.telegram.tgnet.TLRPC$Document, org.telegram.tgnet.TLRPC$BotInlineResult, java.lang.Object, boolean, int):void");
        }

        /* renamed from: lambda$run$2$org-telegram-ui-ContentPreviewViewer$1  reason: not valid java name */
        public /* synthetic */ void m3349lambda$run$2$orgtelegramuiContentPreviewViewer$1(ValueAnimator animation) {
            float unused = ContentPreviewViewer.this.currentMoveYProgress = ((Float) animation.getAnimatedValue()).floatValue();
            ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
            float unused2 = contentPreviewViewer.moveY = contentPreviewViewer.startMoveY + ((ContentPreviewViewer.this.finalMoveY - ContentPreviewViewer.this.startMoveY) * ContentPreviewViewer.this.currentMoveYProgress);
            ContentPreviewViewer.this.containerView.invalidate();
        }
    };
    private Drawable slideUpDrawable;
    /* access modifiers changed from: private */
    public float startMoveY;
    private int startX;
    private int startY;
    /* access modifiers changed from: private */
    public StaticLayout stickerEmojiLayout;
    private UnlockPremiumView unlockPremiumView;
    VibrationEffect vibrationEffect;
    private ActionBarPopupWindow visibleMenu;
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

    /* access modifiers changed from: private */
    public void showUnlockPremiumView() {
        if (this.unlockPremiumView == null) {
            UnlockPremiumView unlockPremiumView2 = new UnlockPremiumView(this.containerView.getContext(), 0, this.resourcesProvider);
            this.unlockPremiumView = unlockPremiumView2;
            this.containerView.addView(unlockPremiumView2, LayoutHelper.createFrame(-1, -1.0f));
            this.unlockPremiumView.setOnClickListener(new ContentPreviewViewer$$ExternalSyntheticLambda1(this));
            this.unlockPremiumView.premiumButtonView.buttonLayout.setOnClickListener(new ContentPreviewViewer$$ExternalSyntheticLambda2(this));
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.unlockPremiumView, false, 1.0f, false);
        AndroidUtilities.updateViewVisibilityAnimated(this.unlockPremiumView, true);
        this.unlockPremiumView.setTranslationY(0.0f);
    }

    /* renamed from: lambda$showUnlockPremiumView$0$org-telegram-ui-ContentPreviewViewer  reason: not valid java name */
    public /* synthetic */ void m3346x12b27cf1(View v) {
        this.menuVisible = false;
        this.containerView.invalidate();
        close();
    }

    /* renamed from: lambda$showUnlockPremiumView$1$org-telegram-ui-ContentPreviewViewer  reason: not valid java name */
    public /* synthetic */ void m3347x74051990(View v) {
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            LaunchActivity activity2 = (LaunchActivity) activity;
            if (!(activity2.getActionBarLayout() == null || activity2.getActionBarLayout().getLastFragment() == null)) {
                activity2.getActionBarLayout().getLastFragment().dismissCurrentDialog();
            }
            activity2.m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new PremiumPreviewFragment(PremiumPreviewFragment.featureTypeToServerString(5)));
        }
        this.menuVisible = false;
        this.containerView.invalidate();
        close();
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
        int left;
        String str;
        RecyclerListView recyclerListView = listView;
        this.delegate = contentPreviewViewerDelegate;
        this.resourcesProvider = resourcesProvider2;
        boolean z3 = false;
        if (this.openPreviewRunnable == null && !isVisible()) {
            Object obj = listener;
            return false;
        } else if (event.getAction() == 1 || event.getAction() == 3 || event.getAction() == 6) {
            AndroidUtilities.runOnUIThread(new ContentPreviewViewer$$ExternalSyntheticLambda4(recyclerListView, listener), 150);
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
                if (!this.menuVisible && this.showProgress == 1.0f) {
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
                int left2 = view.getLeft();
                int right = view.getRight();
                if (top > y || bottom < y || left2 > x) {
                    int i = left2;
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
                        this.clearsInputField = z;
                        this.menuVisible = z;
                        this.closeOnDismiss = z;
                        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
                        if (actionBarPopupWindow != null) {
                            actionBarPopupWindow.dismiss();
                        }
                        AndroidUtilities.updateViewVisibilityAnimated(this.unlockPremiumView, z);
                        View view4 = this.currentPreviewCell;
                        if (view4 instanceof StickerEmojiCell) {
                            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view4;
                            TLRPC.Document sticker = stickerEmojiCell.getSticker();
                            SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
                            String emoji = stickerEmojiCell.getEmoji();
                            int i4 = right;
                            ContentPreviewViewerDelegate contentPreviewViewerDelegate2 = this.delegate;
                            if (contentPreviewViewerDelegate2 != null) {
                                left = left2;
                                str = contentPreviewViewerDelegate2.getQuery(false);
                            } else {
                                left = left2;
                                str = null;
                            }
                            boolean isRecent = stickerEmojiCell.isRecent();
                            Object parentObject2 = stickerEmojiCell.getParentObject();
                            StickerEmojiCell stickerEmojiCell2 = stickerEmojiCell;
                            SendMessagesHelper.ImportingSticker importingSticker2 = stickerPath;
                            StickerEmojiCell stickerEmojiCell3 = stickerEmojiCell2;
                            int i5 = left;
                            String str2 = emoji;
                            int i6 = bottom;
                            int i7 = top;
                            ContextLinkCell contextLinkCell2 = view;
                            int i8 = a;
                            boolean z4 = isRecent;
                            int i9 = count;
                            Object obj3 = parentObject2;
                            int i10 = y;
                            open(sticker, importingSticker2, str2, str, (TLRPC.BotInlineResult) null, contentType, z4, obj3, resourcesProvider2);
                            stickerEmojiCell3.setScaled(true);
                            z2 = true;
                        } else {
                            int i11 = left2;
                            int i12 = bottom;
                            int i13 = top;
                            ContextLinkCell contextLinkCell3 = view;
                            int i14 = a;
                            int i15 = count;
                            int i16 = y;
                            if ((view4 instanceof StickerCell) != 0) {
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
                    int i17 = left2;
                    int i18 = bottom;
                    int i19 = top;
                    ContextLinkCell contextLinkCell5 = view;
                    int i20 = a;
                    int i21 = count;
                    int i22 = y;
                    return true;
                }
                a++;
                ContentPreviewViewerDelegate contentPreviewViewerDelegate5 = contentPreviewViewerDelegate;
                count = count;
                y = y;
                z3 = false;
            }
            int i23 = a;
            int i24 = count;
            int i25 = y;
            return true;
        } else if (this.openPreviewRunnable == null) {
            Object obj4 = listener;
            return false;
        } else if (event.getAction() != 2) {
            AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
            this.openPreviewRunnable = null;
            Object obj5 = listener;
            return false;
        } else if (Math.hypot((double) (((float) this.startX) - event.getX()), (double) (((float) this.startY) - event.getY())) > ((double) AndroidUtilities.dp(10.0f))) {
            AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
            this.openPreviewRunnable = null;
            Object obj6 = listener;
            return false;
        } else {
            Object obj7 = listener;
            return false;
        }
    }

    static /* synthetic */ void lambda$onTouch$2(RecyclerListView listView, Object listener) {
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

    public boolean onInterceptTouchEvent(MotionEvent event, RecyclerListView listView, int height, ContentPreviewViewerDelegate contentPreviewViewerDelegate, Theme.ResourcesProvider resourcesProvider2) {
        ContentPreviewViewer contentPreviewViewer = this;
        RecyclerListView recyclerListView = listView;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        contentPreviewViewer.delegate = contentPreviewViewerDelegate;
        contentPreviewViewer.resourcesProvider = resourcesProvider3;
        boolean z = false;
        if (event.getAction() != 0) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        int count = listView.getChildCount();
        int a = 0;
        while (a < count) {
            View view = null;
            if (recyclerListView instanceof RecyclerListView) {
                view = recyclerListView.getChildAt(a);
            }
            if (view == null) {
                return z;
            }
            int top = view.getTop();
            int bottom = view.getBottom();
            int left = view.getLeft();
            int right = view.getRight();
            if (top > y || bottom < y || left > x || right < x) {
                a++;
                contentPreviewViewer = this;
                recyclerListView = listView;
                ContentPreviewViewerDelegate contentPreviewViewerDelegate2 = contentPreviewViewerDelegate;
                z = false;
            } else {
                int contentType = -1;
                if (view instanceof StickerEmojiCell) {
                    if (((StickerEmojiCell) view).showingBitmap()) {
                        contentType = 0;
                        contentPreviewViewer.centerImage.setRoundRadius((int) z);
                    }
                } else if (view instanceof StickerCell) {
                    if (((StickerCell) view).showingBitmap()) {
                        contentType = 0;
                        contentPreviewViewer.centerImage.setRoundRadius((int) z);
                    }
                } else if (view instanceof ContextLinkCell) {
                    ContextLinkCell cell = (ContextLinkCell) view;
                    if (cell.showingBitmap()) {
                        if (cell.isSticker()) {
                            contentType = 0;
                            contentPreviewViewer.centerImage.setRoundRadius(z ? 1 : 0);
                        } else if (cell.isGif()) {
                            contentType = 1;
                            contentPreviewViewer.centerImage.setRoundRadius(AndroidUtilities.dp(6.0f));
                        }
                    }
                }
                if (contentType == -1) {
                    return false;
                }
                contentPreviewViewer.startX = x;
                contentPreviewViewer.startY = y;
                contentPreviewViewer.currentPreviewCell = view;
                ContentPreviewViewer$$ExternalSyntheticLambda6 contentPreviewViewer$$ExternalSyntheticLambda6 = new ContentPreviewViewer$$ExternalSyntheticLambda6(contentPreviewViewer, recyclerListView, contentType, resourcesProvider3);
                contentPreviewViewer.openPreviewRunnable = contentPreviewViewer$$ExternalSyntheticLambda6;
                AndroidUtilities.runOnUIThread(contentPreviewViewer$$ExternalSyntheticLambda6, 200);
                return true;
            }
        }
        return false;
    }

    /* renamed from: lambda$onInterceptTouchEvent$3$org-telegram-ui-ContentPreviewViewer  reason: not valid java name */
    public /* synthetic */ void m3343x68d28b0b(RecyclerListView listView, int contentTypeFinal, Theme.ResourcesProvider resourcesProvider2) {
        RecyclerListView recyclerListView = listView;
        if (this.openPreviewRunnable != null) {
            String str = null;
            recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) null);
            recyclerListView.requestDisallowInterceptTouchEvent(true);
            this.openPreviewRunnable = null;
            setParentActivity((Activity) listView.getContext());
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
        this.effectImage.setCurrentAccount(this.currentAccount);
        this.effectImage.setLayerNum(Integer.MAX_VALUE);
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
                    ContentPreviewViewer.this.effectImage.onAttachedToWindow();
                }

                /* access modifiers changed from: protected */
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    ContentPreviewViewer.this.centerImage.onDetachedFromWindow();
                    ContentPreviewViewer.this.effectImage.onDetachedFromWindow();
                }
            };
            this.containerView = r0;
            r0.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new ContentPreviewViewer$$ExternalSyntheticLambda3(this));
            MessagesController.getInstance(this.currentAccount);
            this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
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
            this.effectImage.setAspectFit(true);
            this.effectImage.setInvalidateAll(true);
            this.effectImage.setParentView(this.containerView);
        }
    }

    /* renamed from: lambda$setParentActivity$4$org-telegram-ui-ContentPreviewViewer  reason: not valid java name */
    public /* synthetic */ WindowInsets m3344lambda$setParentActivity$4$orgtelegramuiContentPreviewViewer(View v, WindowInsets insets) {
        this.lastInsets = insets;
        return insets;
    }

    /* renamed from: lambda$setParentActivity$5$org-telegram-ui-ContentPreviewViewer  reason: not valid java name */
    public /* synthetic */ boolean m3345lambda$setParentActivity$5$orgtelegramuiContentPreviewViewer(View v, MotionEvent event) {
        if (event.getAction() == 1 || event.getAction() == 6 || event.getAction() == 3) {
            close();
        }
        return true;
    }

    public void setKeyboardHeight(int height) {
        this.keyboardHeight = height;
    }

    public void open(TLRPC.Document document, SendMessagesHelper.ImportingSticker sticker, String emojiPath, String query, TLRPC.BotInlineResult botInlineResult, int contentType, boolean isRecent, Object parent, Theme.ResourcesProvider resourcesProvider2) {
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
            String str4 = null;
            this.stickerEmojiLayout = null;
            this.backgroundDrawable.setColor(Theme.getActiveTheme().isDark() ? NUM : NUM);
            this.drawEffect = false;
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
                    String str5 = query;
                    Object obj3 = parent;
                    return;
                } else if (botInlineResult2.content != null) {
                    if (!(botInlineResult2.thumb instanceof TLRPC.TL_webDocument) || !"video/mp4".equals(botInlineResult2.thumb.mime_type)) {
                        this.centerImage.setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.content)), (String) null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.thumb)), "90_90_b", (long) botInlineResult2.content.size, (String) null, "gif" + botInlineResult2, 1);
                    } else {
                        this.centerImage.setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.content)), (String) null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.thumb)), (String) null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult2.thumb)), "90_90_b", (Drawable) null, (long) botInlineResult2.content.size, (String) null, "gif" + botInlineResult2, 1);
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
                this.effectImage.clearImage();
                this.drawEffect = false;
                if (document2 != null) {
                    TLRPC.InputStickerSet newSet = null;
                    int a = 0;
                    while (true) {
                        if (a >= document2.attributes.size()) {
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
                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 1300);
                    }
                    this.currentStickerSet = newSet;
                    TLRPC.PhotoSize thumb2 = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
                    if (MessageObject.isVideoStickerDocument(document)) {
                        this.centerImage.setImage(ImageLocation.getForDocument(document), (String) null, ImageLocation.getForDocument(thumb2, document2), (String) null, (Drawable) null, 0, "webp", this.currentStickerSet, 1);
                    } else {
                        this.centerImage.setImage(ImageLocation.getForDocument(document), (String) null, ImageLocation.getForDocument(thumb2, document2), (String) null, "webp", (Object) this.currentStickerSet, 1);
                        if (MessageObject.isPremiumSticker(document)) {
                            this.drawEffect = true;
                            this.effectImage.setImage(ImageLocation.getForDocument(MessageObject.getPremiumStickerAnimation(document), document2), (String) null, (ImageLocation) null, (String) null, "tgs", (Object) this.currentStickerSet, 1);
                        }
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
                    ImageReceiver imageReceiver = this.centerImage;
                    String str6 = importingSticker2.path;
                    if (importingSticker2.animated) {
                        str4 = "tgs";
                    }
                    imageReceiver.setImage(str6, (String) null, (Drawable) null, str4, 0);
                    if (str != null) {
                        this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(str, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    }
                    if (this.delegate.needMenu()) {
                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 1300);
                    }
                }
            } else {
                return;
            }
            if (this.centerImage.getLottieAnimation() != null) {
                this.centerImage.getLottieAnimation().setCurrentFrame(0);
            }
            if (this.drawEffect && this.effectImage.getLottieAnimation() != null) {
                this.effectImage.getLottieAnimation().setCurrentFrame(0);
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
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
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

    public void closeWithMenu() {
        this.menuVisible = false;
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
            this.popupWindow = null;
        }
        close();
    }

    public void close() {
        if (this.parentActivity != null && !this.menuVisible) {
            AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
            this.showProgress = 1.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            this.containerView.invalidate();
            this.currentDocument = null;
            this.currentStickerSet = null;
            this.currentQuery = null;
            this.delegate = null;
            this.isVisible = false;
            UnlockPremiumView unlockPremiumView2 = this.unlockPremiumView;
            if (unlockPremiumView2 != null) {
                unlockPremiumView2.animate().alpha(0.0f).translationY((float) AndroidUtilities.dp(56.0f)).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 8);
        }
    }

    public void destroy() {
        this.isVisible = false;
        this.delegate = null;
        this.currentDocument = null;
        this.currentQuery = null;
        this.currentStickerSet = null;
        if (this.parentActivity != null && this.windowView != null) {
            Bitmap bitmap = this.blurrBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.blurrBitmap = null;
            }
            this.blurProgress = 0.0f;
            this.menuVisible = false;
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
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
        int top;
        int insets;
        int size;
        Drawable drawable;
        WindowInsets windowInsets;
        float f;
        Canvas canvas2 = canvas;
        if (this.containerView != null && this.backgroundDrawable != null) {
            if (this.menuVisible && this.blurrBitmap == null) {
                prepareBlurBitmap();
            }
            if (this.blurrBitmap != null) {
                boolean z = this.menuVisible;
                if (z) {
                    float f2 = this.blurProgress;
                    if (f2 != 1.0f) {
                        float f3 = f2 + 0.13333334f;
                        this.blurProgress = f3;
                        if (f3 > 1.0f) {
                            this.blurProgress = 1.0f;
                        }
                        this.containerView.invalidate();
                        f = this.blurProgress;
                        if (!(f == 0.0f || this.blurrBitmap == null)) {
                            this.paint.setAlpha((int) (f * 255.0f));
                            canvas.save();
                            canvas2.scale(12.0f, 12.0f);
                            canvas2.drawBitmap(this.blurrBitmap, 0.0f, 0.0f, this.paint);
                            canvas.restore();
                        }
                    }
                }
                if (!z) {
                    float f4 = this.blurProgress;
                    if (f4 != 0.0f) {
                        float f5 = f4 - 0.13333334f;
                        this.blurProgress = f5;
                        if (f5 < 0.0f) {
                            this.blurProgress = 0.0f;
                        }
                        this.containerView.invalidate();
                    }
                }
                f = this.blurProgress;
                this.paint.setAlpha((int) (f * 255.0f));
                canvas.save();
                canvas2.scale(12.0f, 12.0f);
                canvas2.drawBitmap(this.blurrBitmap, 0.0f, 0.0f, this.paint);
                canvas.restore();
            }
            this.backgroundDrawable.setAlpha((int) (this.showProgress * 180.0f));
            this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
            this.backgroundDrawable.draw(canvas2);
            canvas.save();
            if (Build.VERSION.SDK_INT < 21 || (windowInsets = this.lastInsets) == null) {
                top = AndroidUtilities.statusBarHeight;
                insets = 0;
            } else {
                int insets2 = windowInsets.getStableInsetBottom() + this.lastInsets.getStableInsetTop();
                top = this.lastInsets.getStableInsetTop();
                insets = insets2;
            }
            if (this.currentContentType == 1) {
                size = Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets) - AndroidUtilities.dp(40.0f);
            } else if (this.drawEffect != 0) {
                size = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets)) - AndroidUtilities.dpf2(40.0f));
            } else {
                size = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets)) / 1.8f);
            }
            float topOffset = (float) Math.max((size / 2) + top + (this.stickerEmojiLayout != null ? AndroidUtilities.dp(40.0f) : 0), ((this.containerView.getHeight() - insets) - this.keyboardHeight) / 2);
            if (this.drawEffect) {
                topOffset += (float) AndroidUtilities.dp(40.0f);
            }
            canvas2.translate((float) (this.containerView.getWidth() / 2), this.moveY + topOffset);
            float f6 = this.showProgress;
            int size2 = (int) (((float) size) * ((f6 * 0.8f) / 0.8f));
            if (this.drawEffect != 0) {
                float smallImageSize = ((float) size2) * 0.6669f;
                this.centerImage.setAlpha(f6);
                this.centerImage.setImageCoords(((((float) size2) - smallImageSize) - (((float) size2) / 2.0f)) - (((float) size2) * 0.0546875f), ((((float) size2) - smallImageSize) / 2.0f) - (((float) size2) / 2.0f), smallImageSize, smallImageSize);
                this.centerImage.draw(canvas2);
                this.effectImage.setAlpha(this.showProgress);
                this.effectImage.setImageCoords(((float) (-size2)) / 2.0f, ((float) (-size2)) / 2.0f, (float) size2, (float) size2);
                this.effectImage.draw(canvas2);
            } else {
                this.centerImage.setAlpha(f6);
                this.centerImage.setImageCoords(((float) (-size2)) / 2.0f, ((float) (-size2)) / 2.0f, (float) size2, (float) size2);
                this.centerImage.draw(canvas2);
            }
            if (this.currentContentType == 1 && (drawable = this.slideUpDrawable) != null) {
                int w = drawable.getIntrinsicWidth();
                int h = this.slideUpDrawable.getIntrinsicHeight();
                int y = (int) (this.centerImage.getDrawRegion().top - ((float) AndroidUtilities.dp(((this.currentMoveY / ((float) AndroidUtilities.dp(60.0f))) * 6.0f) + 17.0f)));
                this.slideUpDrawable.setAlpha((int) ((1.0f - this.currentMoveYProgress) * 255.0f));
                this.slideUpDrawable.setBounds((-w) / 2, (-h) + y, w / 2, y);
                this.slideUpDrawable.draw(canvas2);
            }
            if (this.stickerEmojiLayout != null) {
                if (this.drawEffect) {
                    canvas2.translate((float) (-AndroidUtilities.dp(50.0f)), ((-this.effectImage.getImageHeight()) / 2.0f) - ((float) AndroidUtilities.dp(30.0f)));
                } else {
                    canvas2.translate((float) (-AndroidUtilities.dp(50.0f)), ((-this.centerImage.getImageHeight()) / 2.0f) - ((float) AndroidUtilities.dp(30.0f)));
                }
                this.stickerEmojiLayout.draw(canvas2);
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
                    AndroidUtilities.runOnUIThread(new ContentPreviewViewer$$ExternalSyntheticLambda5(this));
                    Bitmap bitmap = this.blurrBitmap;
                    if (bitmap != null) {
                        bitmap.recycle();
                        this.blurrBitmap = null;
                    }
                    AndroidUtilities.updateViewVisibilityAnimated(this.unlockPremiumView, false, 1.0f, false);
                    this.blurProgress = 0.0f;
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

    /* renamed from: lambda$onDraw$6$org-telegram-ui-ContentPreviewViewer  reason: not valid java name */
    public /* synthetic */ void m3342lambda$onDraw$6$orgtelegramuiContentPreviewViewer() {
        this.centerImage.setImageBitmap((Bitmap) null);
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private void prepareBlurBitmap() {
        Activity activity = this.parentActivity;
        if (activity != null) {
            View parentView = activity.getWindow().getDecorView();
            int w = (int) (((float) parentView.getMeasuredWidth()) / 12.0f);
            int h = (int) (((float) parentView.getMeasuredHeight()) / 12.0f);
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(0.083333336f, 0.083333336f);
            canvas.drawColor(Theme.getColor("windowBackgroundWhite"));
            parentView.draw(canvas);
            Activity activity2 = this.parentActivity;
            if ((activity2 instanceof LaunchActivity) && ((LaunchActivity) activity2).getActionBarLayout().getLastFragment().getVisibleDialog() != null) {
                ((LaunchActivity) this.parentActivity).getActionBarLayout().getLastFragment().getVisibleDialog().getWindow().getDecorView().draw(canvas);
            }
            Utilities.stackBlurBitmap(bitmap, Math.max(10, Math.max(w, h) / 180));
            this.blurrBitmap = bitmap;
        }
    }

    public boolean showMenuFor(View view) {
        if (!(view instanceof StickerEmojiCell)) {
            return false;
        }
        Activity activity = AndroidUtilities.findActivity(view.getContext());
        if (activity == null) {
            return true;
        }
        setParentActivity(activity);
        StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
        TLRPC.Document sticker = stickerEmojiCell.getSticker();
        SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
        String emoji = stickerEmojiCell.getEmoji();
        ContentPreviewViewerDelegate contentPreviewViewerDelegate = this.delegate;
        open(sticker, stickerPath, emoji, contentPreviewViewerDelegate != null ? contentPreviewViewerDelegate.getQuery(false) : null, (TLRPC.BotInlineResult) null, 0, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject(), this.resourcesProvider);
        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 16);
        stickerEmojiCell.setScaled(true);
        return true;
    }
}
