package org.telegram.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_webDocument;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.tgnet.TLRPC$WebDocument;
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
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ContentPreviewViewer Instance;
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
    public boolean drawEffect;
    /* access modifiers changed from: private */
    public ImageReceiver effectImage = new ImageReceiver();
    /* access modifiers changed from: private */
    public float finalMoveY;
    /* access modifiers changed from: private */
    public SendMessagesHelper.ImportingSticker importingSticker;
    /* access modifiers changed from: private */
    public TLRPC$BotInlineResult inlineResult;
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
            boolean z;
            int i;
            int i2;
            int i3;
            int i4;
            int i5;
            float f;
            String str;
            int i6;
            if (ContentPreviewViewer.this.parentActivity != null) {
                boolean unused = ContentPreviewViewer.this.closeOnDismiss = true;
                if (ContentPreviewViewer.this.currentContentType == 0) {
                    if (!MessageObject.isPremiumSticker(ContentPreviewViewer.this.currentDocument) || AccountInstance.getInstance(ContentPreviewViewer.this.currentAccount).getUserConfig().isPremium()) {
                        final boolean isStickerInFavorites = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).isStickerInFavorites(ContentPreviewViewer.this.currentDocument);
                        ArrayList arrayList = new ArrayList();
                        final ArrayList arrayList2 = new ArrayList();
                        ArrayList arrayList3 = new ArrayList();
                        boolean unused2 = ContentPreviewViewer.this.menuVisible = true;
                        ContentPreviewViewer.this.containerView.invalidate();
                        if (ContentPreviewViewer.this.delegate != null) {
                            if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                                arrayList.add(LocaleController.getString("SendStickerPreview", NUM));
                                arrayList3.add(NUM);
                                arrayList2.add(0);
                            }
                            if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
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
                                i6 = NUM;
                                str = "DeleteFromFavorites";
                            } else {
                                i6 = NUM;
                                str = "AddToFavorites";
                            }
                            arrayList.add(LocaleController.getString(str, i6));
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
                            for (int i7 = 0; i7 < arrayList3.size(); i7++) {
                                iArr[i7] = ((Integer) arrayList3.get(i7)).intValue();
                            }
                            AnonymousClass1 r3 = new View.OnClickListener() {
                                public void onClick(View view) {
                                    if (ContentPreviewViewer.this.parentActivity != null) {
                                        int intValue = ((Integer) view.getTag()).intValue();
                                        if (((Integer) arrayList2.get(intValue)).intValue() == 0 || ((Integer) arrayList2.get(intValue)).intValue() == 6) {
                                            if (ContentPreviewViewer.this.delegate != null) {
                                                ContentPreviewViewer.this.delegate.sendSticker(ContentPreviewViewer.this.currentDocument, ContentPreviewViewer.this.currentQuery, ContentPreviewViewer.this.parentObject, ((Integer) arrayList2.get(intValue)).intValue() == 0, 0);
                                            }
                                        } else if (((Integer) arrayList2.get(intValue)).intValue() == 1) {
                                            if (ContentPreviewViewer.this.delegate != null) {
                                                ContentPreviewViewer.this.delegate.openSet(ContentPreviewViewer.this.currentStickerSet, ContentPreviewViewer.this.clearsInputField);
                                            }
                                        } else if (((Integer) arrayList2.get(intValue)).intValue() == 2) {
                                            MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(2, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), isStickerInFavorites);
                                        } else if (((Integer) arrayList2.get(intValue)).intValue() == 3) {
                                            TLRPC$Document access$400 = ContentPreviewViewer.this.currentDocument;
                                            Object access$1300 = ContentPreviewViewer.this.parentObject;
                                            String access$1200 = ContentPreviewViewer.this.currentQuery;
                                            ContentPreviewViewerDelegate access$900 = ContentPreviewViewer.this.delegate;
                                            AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, access$900.getDialogId(), new ContentPreviewViewer$1$1$$ExternalSyntheticLambda0(access$900, access$400, access$1200, access$1300));
                                        } else if (((Integer) arrayList2.get(intValue)).intValue() == 4) {
                                            MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(0, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), true);
                                        } else if (((Integer) arrayList2.get(intValue)).intValue() == 5) {
                                            ContentPreviewViewer.this.delegate.remove(ContentPreviewViewer.this.importingSticker);
                                        }
                                        ActionBarPopupWindow actionBarPopupWindow = ContentPreviewViewer.this.popupWindow;
                                        if (actionBarPopupWindow != null) {
                                            actionBarPopupWindow.dismiss();
                                        }
                                    }
                                }
                            };
                            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(ContentPreviewViewer.this.containerView.getContext(), NUM, ContentPreviewViewer.this.resourcesProvider);
                            for (int i8 = 0; i8 < arrayList.size(); i8++) {
                                ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, ((Integer) arrayList3.get(i8)).intValue(), (CharSequence) arrayList.get(i8), false, ContentPreviewViewer.this.resourcesProvider);
                                addItem.setTag(Integer.valueOf(i8));
                                addItem.setOnClickListener(r3);
                            }
                            ContentPreviewViewer.this.popupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2) {
                                public void dismiss() {
                                    super.dismiss();
                                    ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
                                    contentPreviewViewer.popupWindow = null;
                                    boolean unused = contentPreviewViewer.menuVisible = false;
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
                            actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                            ContentPreviewViewer.this.popupWindow.setInputMethodMode(2);
                            ContentPreviewViewer.this.popupWindow.getContentView().setFocusableInTouchMode(true);
                            if (Build.VERSION.SDK_INT < 21 || ContentPreviewViewer.this.lastInsets == null) {
                                i3 = AndroidUtilities.statusBarHeight;
                                i4 = 0;
                            } else {
                                i4 = ContentPreviewViewer.this.lastInsets.getStableInsetBottom() + ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                                i3 = ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                            }
                            if (ContentPreviewViewer.this.currentContentType == 1) {
                                i5 = Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - i4) - AndroidUtilities.dp(40.0f);
                            } else {
                                if (ContentPreviewViewer.this.drawEffect) {
                                    f = ((float) Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - i4)) - AndroidUtilities.dpf2(40.0f);
                                } else {
                                    f = ((float) Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - i4)) / 1.8f;
                                }
                                i5 = (int) f;
                            }
                            int i9 = i5 / 2;
                            int access$1900 = ((int) (ContentPreviewViewer.this.moveY + ((float) Math.max(i3 + i9 + (ContentPreviewViewer.this.stickerEmojiLayout != null ? AndroidUtilities.dp(40.0f) : 0), ((ContentPreviewViewer.this.containerView.getHeight() - i4) - ContentPreviewViewer.this.keyboardHeight) / 2)) + ((float) i9))) + AndroidUtilities.dp(24.0f);
                            if (ContentPreviewViewer.this.drawEffect) {
                                access$1900 += AndroidUtilities.dp(24.0f);
                            }
                            ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
                            contentPreviewViewer.popupWindow.showAtLocation(contentPreviewViewer.containerView, 0, (int) (((float) (ContentPreviewViewer.this.containerView.getMeasuredWidth() - actionBarPopupWindowLayout.getMeasuredWidth())) / 2.0f), access$1900);
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
                    for (int i10 = 0; i10 < arrayList6.size(); i10++) {
                        iArr2[i10] = ((Integer) arrayList6.get(i10)).intValue();
                    }
                    ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(ContentPreviewViewer.this.containerView.getContext(), NUM, ContentPreviewViewer.this.resourcesProvider);
                    ContentPreviewViewer$1$$ExternalSyntheticLambda1 contentPreviewViewer$1$$ExternalSyntheticLambda1 = new ContentPreviewViewer$1$$ExternalSyntheticLambda1(this, arrayList5);
                    for (int i11 = 0; i11 < arrayList4.size(); i11++) {
                        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(actionBarPopupWindowLayout2, ((Integer) arrayList6.get(i11)).intValue(), (CharSequence) arrayList4.get(i11), false, ContentPreviewViewer.this.resourcesProvider);
                        addItem2.setTag(Integer.valueOf(i11));
                        addItem2.setOnClickListener(contentPreviewViewer$1$$ExternalSyntheticLambda1);
                        if (z && i11 == arrayList4.size() - 1) {
                            addItem2.setColors(ContentPreviewViewer.this.getThemedColor("dialogTextRed2"), ContentPreviewViewer.this.getThemedColor("dialogRedIcon"));
                        }
                    }
                    ContentPreviewViewer.this.popupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout2, -2, -2) {
                        public void dismiss() {
                            super.dismiss();
                            ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
                            contentPreviewViewer.popupWindow = null;
                            boolean unused = contentPreviewViewer.menuVisible = false;
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
                    actionBarPopupWindowLayout2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                    ContentPreviewViewer.this.popupWindow.setInputMethodMode(2);
                    ContentPreviewViewer.this.popupWindow.getContentView().setFocusableInTouchMode(true);
                    if (Build.VERSION.SDK_INT < 21 || ContentPreviewViewer.this.lastInsets == null) {
                        i = AndroidUtilities.statusBarHeight;
                        i2 = 0;
                    } else {
                        i2 = ContentPreviewViewer.this.lastInsets.getStableInsetBottom() + ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                        i = ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                    }
                    int min = Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - i2) - AndroidUtilities.dp(40.0f);
                    float access$19002 = ContentPreviewViewer.this.moveY;
                    int i12 = min / 2;
                    int i13 = i + i12;
                    int dp = ContentPreviewViewer.this.stickerEmojiLayout != null ? AndroidUtilities.dp(40.0f) : 0;
                    ContentPreviewViewer contentPreviewViewer2 = ContentPreviewViewer.this;
                    contentPreviewViewer2.popupWindow.showAtLocation(contentPreviewViewer2.containerView, 0, (int) (((float) (ContentPreviewViewer.this.containerView.getMeasuredWidth() - actionBarPopupWindowLayout2.getMeasuredWidth())) / 2.0f), (int) (((float) ((int) (access$19002 + ((float) Math.max(i13 + dp, ((ContentPreviewViewer.this.containerView.getHeight() - i2) - ContentPreviewViewer.this.keyboardHeight) / 2)) + ((float) i12)))) + (((float) AndroidUtilities.dp(24.0f)) - ContentPreviewViewer.this.moveY)));
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    if (ContentPreviewViewer.this.moveY != 0.0f) {
                        if (ContentPreviewViewer.this.finalMoveY == 0.0f) {
                            float unused5 = ContentPreviewViewer.this.finalMoveY = 0.0f;
                            ContentPreviewViewer contentPreviewViewer3 = ContentPreviewViewer.this;
                            float unused6 = contentPreviewViewer3.startMoveY = contentPreviewViewer3.moveY;
                        }
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        ofFloat.addUpdateListener(new ContentPreviewViewer$1$$ExternalSyntheticLambda0(this));
                        ofFloat.setDuration(350);
                        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        ofFloat.start();
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$1(ArrayList arrayList, View view) {
            if (ContentPreviewViewer.this.parentActivity != null) {
                int intValue = ((Integer) view.getTag()).intValue();
                if (((Integer) arrayList.get(intValue)).intValue() == 0) {
                    ContentPreviewViewer.this.delegate.sendGif(ContentPreviewViewer.this.currentDocument != null ? ContentPreviewViewer.this.currentDocument : ContentPreviewViewer.this.inlineResult, ContentPreviewViewer.this.parentObject, true, 0);
                } else if (((Integer) arrayList.get(intValue)).intValue() == 1) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).removeRecentGif(ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) arrayList.get(intValue)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentGif(ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000));
                    MessagesController.getInstance(ContentPreviewViewer.this.currentAccount).saveGif("gif", ContentPreviewViewer.this.currentDocument);
                    ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
                } else if (((Integer) arrayList.get(intValue)).intValue() == 3) {
                    TLRPC$Document access$400 = ContentPreviewViewer.this.currentDocument;
                    TLRPC$BotInlineResult access$2600 = ContentPreviewViewer.this.inlineResult;
                    Object access$1300 = ContentPreviewViewer.this.parentObject;
                    ContentPreviewViewerDelegate access$900 = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog((Context) ContentPreviewViewer.this.parentActivity, access$900.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ContentPreviewViewer$1$$ExternalSyntheticLambda2(access$900, access$400, access$2600, access$1300), ContentPreviewViewer.this.resourcesProvider);
                }
                ActionBarPopupWindow actionBarPopupWindow = ContentPreviewViewer.this.popupWindow;
                if (actionBarPopupWindow != null) {
                    actionBarPopupWindow.dismiss();
                }
            }
        }

        /* JADX WARNING: type inference failed for: r2v0, types: [org.telegram.tgnet.TLRPC$BotInlineResult] */
        /* access modifiers changed from: private */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static /* synthetic */ void lambda$run$0(org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r0, org.telegram.tgnet.TLRPC$Document r1, org.telegram.tgnet.TLRPC$BotInlineResult r2, java.lang.Object r3, boolean r4, int r5) {
            /*
                if (r1 == 0) goto L_0x0003
                goto L_0x0004
            L_0x0003:
                r1 = r2
            L_0x0004:
                r0.sendGif(r1, r3, r4, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.AnonymousClass1.lambda$run$0(org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, org.telegram.tgnet.TLRPC$Document, org.telegram.tgnet.TLRPC$BotInlineResult, java.lang.Object, boolean, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$2(ValueAnimator valueAnimator) {
            float unused = ContentPreviewViewer.this.currentMoveYProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
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

    /* access modifiers changed from: private */
    public void showUnlockPremiumView() {
        if (this.unlockPremiumView == null) {
            UnlockPremiumView unlockPremiumView2 = new UnlockPremiumView(this.containerView.getContext(), 0, this.resourcesProvider);
            this.unlockPremiumView = unlockPremiumView2;
            this.containerView.addView(unlockPremiumView2, LayoutHelper.createFrame(-1, -1.0f));
            this.unlockPremiumView.setOnClickListener(new ContentPreviewViewer$$ExternalSyntheticLambda1(this));
            this.unlockPremiumView.premiumButtonView.buttonTextView.setOnClickListener(new ContentPreviewViewer$$ExternalSyntheticLambda2(this));
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.unlockPremiumView, false, 1.0f, false);
        AndroidUtilities.updateViewVisibilityAnimated(this.unlockPremiumView, true);
        this.unlockPremiumView.setTranslationY(0.0f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showUnlockPremiumView$0(View view) {
        this.menuVisible = false;
        this.containerView.invalidate();
        close();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showUnlockPremiumView$1(View view) {
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            LaunchActivity launchActivity = (LaunchActivity) activity;
            if (!(launchActivity.getActionBarLayout() == null || launchActivity.getActionBarLayout().getLastFragment() == null)) {
                launchActivity.getActionBarLayout().getLastFragment().dismissCurrentDialog();
            }
            launchActivity.lambda$runLinkRequest$59(new PremiumPreviewFragment());
        }
        this.menuVisible = false;
        this.containerView.invalidate();
        close();
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
    /* JADX WARNING: Removed duplicated region for block: B:75:0x015b  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0169  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x019e  */
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
            if (r1 == 0) goto L_0x0295
        L_0x0016:
            int r1 = r16.getAction()
            r3 = 0
            r11 = 1
            if (r1 == r11) goto L_0x0253
            int r1 = r16.getAction()
            r4 = 3
            if (r1 == r4) goto L_0x0253
            int r1 = r16.getAction()
            r4 = 6
            if (r1 != r4) goto L_0x002e
            goto L_0x0253
        L_0x002e:
            int r1 = r16.getAction()
            if (r1 == 0) goto L_0x0295
            boolean r1 = r10.isVisible
            r4 = 2
            if (r1 == 0) goto L_0x0218
            int r1 = r16.getAction()
            if (r1 != r4) goto L_0x0217
            int r1 = r10.currentContentType
            if (r1 != r11) goto L_0x00bb
            boolean r0 = r10.menuVisible
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
            if (r6 >= r5) goto L_0x0217
            android.view.View r7 = r0.getChildAt(r6)
            if (r7 != 0) goto L_0x00d3
            return r2
        L_0x00d3:
            int r8 = r7.getTop()
            int r12 = r7.getBottom()
            int r13 = r7.getLeft()
            int r14 = r7.getRight()
            if (r8 > r4) goto L_0x0213
            if (r12 < r4) goto L_0x0213
            if (r13 > r1) goto L_0x0213
            if (r14 >= r1) goto L_0x00ed
            goto L_0x0213
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
            if (r12 == r1) goto L_0x0217
            android.view.View r0 = r10.currentPreviewCell
            if (r7 != r0) goto L_0x0132
            goto L_0x0217
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
            r10.clearsInputField = r2
            r10.menuVisible = r2
            r10.closeOnDismiss = r2
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r10.popupWindow
            if (r0 == 0) goto L_0x015e
            r0.dismiss()
        L_0x015e:
            org.telegram.ui.UnlockPremiumView r0 = r10.unlockPremiumView
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r2)
            android.view.View r0 = r10.currentPreviewCell
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x019e
            r13 = r0
            org.telegram.ui.Cells.StickerEmojiCell r13 = (org.telegram.ui.Cells.StickerEmojiCell) r13
            org.telegram.tgnet.TLRPC$Document r1 = r13.getSticker()
            org.telegram.messenger.SendMessagesHelper$ImportingSticker r4 = r13.getStickerPath()
            java.lang.String r5 = r13.getEmoji()
            org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate r0 = r10.delegate
            if (r0 == 0) goto L_0x0182
            java.lang.String r0 = r0.getQuery(r2)
            r6 = r0
            goto L_0x0183
        L_0x0182:
            r6 = r3
        L_0x0183:
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
            goto L_0x020f
        L_0x019e:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x01d3
            r13 = r0
            org.telegram.ui.Cells.StickerCell r13 = (org.telegram.ui.Cells.StickerCell) r13
            org.telegram.tgnet.TLRPC$Document r1 = r13.getSticker()
            r4 = 0
            r5 = 0
            org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate r0 = r10.delegate
            if (r0 == 0) goto L_0x01b5
            java.lang.String r0 = r0.getQuery(r2)
            r6 = r0
            goto L_0x01b6
        L_0x01b5:
            r6 = r3
        L_0x01b6:
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
            goto L_0x020f
        L_0x01d3:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x020f
            r13 = r0
            org.telegram.ui.Cells.ContextLinkCell r13 = (org.telegram.ui.Cells.ContextLinkCell) r13
            org.telegram.tgnet.TLRPC$Document r1 = r13.getDocument()
            r2 = 0
            r4 = 0
            org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate r0 = r10.delegate
            if (r0 == 0) goto L_0x01ea
            java.lang.String r0 = r0.getQuery(r11)
            r5 = r0
            goto L_0x01eb
        L_0x01ea:
            r5 = r3
        L_0x01eb:
            org.telegram.tgnet.TLRPC$BotInlineResult r6 = r13.getBotInlineResult()
            r7 = 0
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r13.getBotInlineResult()
            if (r0 == 0) goto L_0x01fb
            org.telegram.tgnet.TLRPC$User r0 = r13.getInlineBot()
            goto L_0x01ff
        L_0x01fb:
            java.lang.Object r0 = r13.getParentObject()
        L_0x01ff:
            r8 = r0
            r0 = r15
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r12
            r9 = r21
            r0.open(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            if (r12 == r11) goto L_0x020f
            r13.setScaled(r11)
        L_0x020f:
            r15.runSmoothHaptic()
            return r11
        L_0x0213:
            int r6 = r6 + 1
            goto L_0x00ca
        L_0x0217:
            return r11
        L_0x0218:
            java.lang.Runnable r0 = r10.openPreviewRunnable
            if (r0 == 0) goto L_0x0295
            int r0 = r16.getAction()
            if (r0 != r4) goto L_0x024b
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
            if (r6 <= 0) goto L_0x0295
            java.lang.Runnable r0 = r10.openPreviewRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r10.openPreviewRunnable = r3
            goto L_0x0295
        L_0x024b:
            java.lang.Runnable r0 = r10.openPreviewRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r10.openPreviewRunnable = r3
            goto L_0x0295
        L_0x0253:
            org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda4 r1 = new org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda4
            r4 = r19
            r1.<init>(r0, r4)
            r4 = 150(0x96, double:7.4E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r4)
            java.lang.Runnable r0 = r10.openPreviewRunnable
            if (r0 == 0) goto L_0x0269
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r10.openPreviewRunnable = r3
            goto L_0x0295
        L_0x0269:
            boolean r0 = r15.isVisible()
            if (r0 == 0) goto L_0x0295
            r15.close()
            android.view.View r0 = r10.currentPreviewCell
            if (r0 == 0) goto L_0x0295
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerEmojiCell
            if (r1 == 0) goto L_0x0280
            org.telegram.ui.Cells.StickerEmojiCell r0 = (org.telegram.ui.Cells.StickerEmojiCell) r0
            r0.setScaled(r2)
            goto L_0x0293
        L_0x0280:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.StickerCell
            if (r1 == 0) goto L_0x028a
            org.telegram.ui.Cells.StickerCell r0 = (org.telegram.ui.Cells.StickerCell) r0
            r0.setScaled(r2)
            goto L_0x0293
        L_0x028a:
            boolean r1 = r0 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r1 == 0) goto L_0x0293
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            r0.setScaled(r2)
        L_0x0293:
            r10.currentPreviewCell = r3
        L_0x0295:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onTouch(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, java.lang.Object, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, org.telegram.ui.ActionBar.Theme$ResourcesProvider):boolean");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onTouch$2(RecyclerListView recyclerListView, Object obj) {
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

    /* JADX WARNING: Removed duplicated region for block: B:35:0x0095 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0096  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r8, org.telegram.ui.Components.RecyclerListView r9, int r10, org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate r11, org.telegram.ui.ActionBar.Theme.ResourcesProvider r12) {
        /*
            r7 = this;
            r7.delegate = r11
            r7.resourcesProvider = r12
            int r10 = r8.getAction()
            r11 = 0
            if (r10 != 0) goto L_0x00ad
            float r10 = r8.getX()
            int r10 = (int) r10
            float r8 = r8.getY()
            int r8 = (int) r8
            int r0 = r9.getChildCount()
            r1 = 0
        L_0x001a:
            if (r1 >= r0) goto L_0x00ad
            android.view.View r2 = r9.getChildAt(r1)
            if (r2 != 0) goto L_0x0023
            return r11
        L_0x0023:
            int r3 = r2.getTop()
            int r4 = r2.getBottom()
            int r5 = r2.getLeft()
            int r6 = r2.getRight()
            if (r3 > r8) goto L_0x00a9
            if (r4 < r8) goto L_0x00a9
            if (r5 > r10) goto L_0x00a9
            if (r6 >= r10) goto L_0x003d
            goto L_0x00a9
        L_0x003d:
            boolean r0 = r2 instanceof org.telegram.ui.Cells.StickerEmojiCell
            r1 = -1
            r3 = 1
            if (r0 == 0) goto L_0x0053
            r0 = r2
            org.telegram.ui.Cells.StickerEmojiCell r0 = (org.telegram.ui.Cells.StickerEmojiCell) r0
            boolean r0 = r0.showingBitmap()
            if (r0 == 0) goto L_0x0092
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            r0.setRoundRadius((int) r11)
        L_0x0051:
            r0 = 0
            goto L_0x0093
        L_0x0053:
            boolean r0 = r2 instanceof org.telegram.ui.Cells.StickerCell
            if (r0 == 0) goto L_0x0066
            r0 = r2
            org.telegram.ui.Cells.StickerCell r0 = (org.telegram.ui.Cells.StickerCell) r0
            boolean r0 = r0.showingBitmap()
            if (r0 == 0) goto L_0x0092
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            r0.setRoundRadius((int) r11)
            goto L_0x0051
        L_0x0066:
            boolean r0 = r2 instanceof org.telegram.ui.Cells.ContextLinkCell
            if (r0 == 0) goto L_0x0092
            r0 = r2
            org.telegram.ui.Cells.ContextLinkCell r0 = (org.telegram.ui.Cells.ContextLinkCell) r0
            boolean r4 = r0.showingBitmap()
            if (r4 == 0) goto L_0x0092
            boolean r4 = r0.isSticker()
            if (r4 == 0) goto L_0x007f
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            r0.setRoundRadius((int) r11)
            goto L_0x0051
        L_0x007f:
            boolean r0 = r0.isGif()
            if (r0 == 0) goto L_0x0092
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.setRoundRadius((int) r4)
            r0 = 1
            goto L_0x0093
        L_0x0092:
            r0 = -1
        L_0x0093:
            if (r0 != r1) goto L_0x0096
            return r11
        L_0x0096:
            r7.startX = r10
            r7.startY = r8
            r7.currentPreviewCell = r2
            org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda6 r8 = new org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda6
            r8.<init>(r7, r9, r0, r12)
            r7.openPreviewRunnable = r8
            r9 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r9)
            return r3
        L_0x00a9:
            int r1 = r1 + 1
            goto L_0x001a
        L_0x00ad:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContentPreviewViewer.onInterceptTouchEvent(android.view.MotionEvent, org.telegram.ui.Components.RecyclerListView, int, org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate, org.telegram.ui.ActionBar.Theme$ResourcesProvider):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onInterceptTouchEvent$3(RecyclerListView recyclerListView, int i, Theme.ResourcesProvider resourcesProvider2) {
        RecyclerListView recyclerListView2 = recyclerListView;
        if (this.openPreviewRunnable != null) {
            recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) null);
            recyclerListView.requestDisallowInterceptTouchEvent(true);
            this.openPreviewRunnable = null;
            setParentActivity((Activity) recyclerListView.getContext());
            this.clearsInputField = false;
            View view = this.currentPreviewCell;
            if (view instanceof StickerEmojiCell) {
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
                TLRPC$Document sticker = stickerEmojiCell.getSticker();
                SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
                String emoji = stickerEmojiCell.getEmoji();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate = this.delegate;
                open(sticker, stickerPath, emoji, contentPreviewViewerDelegate != null ? contentPreviewViewerDelegate.getQuery(false) : null, (TLRPC$BotInlineResult) null, i, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject(), resourcesProvider2);
                stickerEmojiCell.setScaled(true);
            } else if (view instanceof StickerCell) {
                StickerCell stickerCell = (StickerCell) view;
                TLRPC$Document sticker2 = stickerCell.getSticker();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate2 = this.delegate;
                open(sticker2, (SendMessagesHelper.ImportingSticker) null, (String) null, contentPreviewViewerDelegate2 != null ? contentPreviewViewerDelegate2.getQuery(false) : null, (TLRPC$BotInlineResult) null, i, false, stickerCell.getParentObject(), resourcesProvider2);
                stickerCell.setScaled(true);
                this.clearsInputField = stickerCell.isClearsInputField();
            } else if (view instanceof ContextLinkCell) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) view;
                TLRPC$Document document = contextLinkCell.getDocument();
                ContentPreviewViewerDelegate contentPreviewViewerDelegate3 = this.delegate;
                open(document, (SendMessagesHelper.ImportingSticker) null, (String) null, contentPreviewViewerDelegate3 != null ? contentPreviewViewerDelegate3.getQuery(true) : null, contextLinkCell.getBotInlineResult(), i, false, contextLinkCell.getBotInlineResult() != null ? contextLinkCell.getInlineBot() : contextLinkCell.getParentObject(), resourcesProvider2);
                if (i != 1) {
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
        this.effectImage.setCurrentAccount(this.currentAccount);
        this.effectImage.setLayerNum(Integer.MAX_VALUE);
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
                    ContentPreviewViewer.this.effectImage.onAttachedToWindow();
                }

                /* access modifiers changed from: protected */
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    ContentPreviewViewer.this.centerImage.onDetachedFromWindow();
                    ContentPreviewViewer.this.effectImage.onDetachedFromWindow();
                }
            };
            this.containerView = r3;
            r3.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new ContentPreviewViewer$$ExternalSyntheticLambda3(this));
            MessagesController.getInstance(this.currentAccount);
            this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
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
            this.effectImage.setAspectFit(true);
            this.effectImage.setInvalidateAll(true);
            this.effectImage.setParentView(this.containerView);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ WindowInsets lambda$setParentActivity$4(View view, WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
        return windowInsets;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setParentActivity$5(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 6 || motionEvent.getAction() == 3) {
            close();
        }
        return true;
    }

    public void open(TLRPC$Document tLRPC$Document, SendMessagesHelper.ImportingSticker importingSticker2, String str, String str2, TLRPC$BotInlineResult tLRPC$BotInlineResult, int i, boolean z, Object obj, Theme.ResourcesProvider resourcesProvider2) {
        int i2;
        TLRPC$InputStickerSet tLRPC$InputStickerSet;
        ContentPreviewViewerDelegate contentPreviewViewerDelegate;
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        SendMessagesHelper.ImportingSticker importingSticker3 = importingSticker2;
        String str3 = str;
        TLRPC$BotInlineResult tLRPC$BotInlineResult2 = tLRPC$BotInlineResult;
        int i3 = i;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        if (this.parentActivity != null && this.windowView != null) {
            this.resourcesProvider = resourcesProvider3;
            this.isRecentSticker = z;
            this.stickerEmojiLayout = null;
            this.backgroundDrawable.setColor(Theme.getActiveTheme().isDark() ? NUM : NUM);
            this.drawEffect = false;
            if (i3 != 0) {
                if (tLRPC$Document2 != null) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90);
                    TLRPC$VideoSize documentVideoThumb = MessageObject.getDocumentVideoThumb(tLRPC$Document);
                    ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$Document);
                    forDocument.imageType = 2;
                    if (documentVideoThumb != null) {
                        ImageReceiver imageReceiver = this.centerImage;
                        ImageLocation forDocument2 = ImageLocation.getForDocument(documentVideoThumb, tLRPC$Document2);
                        ImageLocation forDocument3 = ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document2);
                        long j = tLRPC$Document2.size;
                        imageReceiver.setImage(forDocument, (String) null, forDocument2, (String) null, forDocument3, "90_90_b", (Drawable) null, j, (String) null, "gif" + tLRPC$Document2, 0);
                    } else {
                        ImageReceiver imageReceiver2 = this.centerImage;
                        ImageLocation forDocument4 = ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document2);
                        long j2 = tLRPC$Document2.size;
                        imageReceiver2.setImage(forDocument, (String) null, forDocument4, "90_90_b", j2, (String) null, "gif" + tLRPC$Document2, 0);
                    }
                } else if (tLRPC$BotInlineResult2 != null && tLRPC$BotInlineResult2.content != null) {
                    TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult2.thumb;
                    if (!(tLRPC$WebDocument instanceof TLRPC$TL_webDocument) || !"video/mp4".equals(tLRPC$WebDocument.mime_type)) {
                        ImageReceiver imageReceiver3 = this.centerImage;
                        ImageLocation forWebFile = ImageLocation.getForWebFile(WebFile.createWithWebDocument(tLRPC$BotInlineResult2.content));
                        ImageLocation forWebFile2 = ImageLocation.getForWebFile(WebFile.createWithWebDocument(tLRPC$BotInlineResult2.thumb));
                        imageReceiver3.setImage(forWebFile, (String) null, forWebFile2, "90_90_b", (long) tLRPC$BotInlineResult2.content.size, (String) null, "gif" + tLRPC$BotInlineResult2, 1);
                    } else {
                        ImageReceiver imageReceiver4 = this.centerImage;
                        ImageLocation forWebFile3 = ImageLocation.getForWebFile(WebFile.createWithWebDocument(tLRPC$BotInlineResult2.content));
                        ImageLocation forWebFile4 = ImageLocation.getForWebFile(WebFile.createWithWebDocument(tLRPC$BotInlineResult2.thumb));
                        ImageLocation forWebFile5 = ImageLocation.getForWebFile(WebFile.createWithWebDocument(tLRPC$BotInlineResult2.thumb));
                        imageReceiver4.setImage(forWebFile3, (String) null, forWebFile4, (String) null, forWebFile5, "90_90_b", (Drawable) null, (long) tLRPC$BotInlineResult2.content.size, (String) null, "gif" + tLRPC$BotInlineResult2, 1);
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
                this.effectImage.clearImage();
                this.drawEffect = false;
                if (tLRPC$Document2 != null) {
                    int i4 = 0;
                    while (true) {
                        if (i4 >= tLRPC$Document2.attributes.size()) {
                            tLRPC$InputStickerSet = null;
                            break;
                        }
                        TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document2.attributes.get(i4);
                        if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) && (tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset) != null) {
                            break;
                        }
                        i4++;
                    }
                    if (tLRPC$InputStickerSet != null && ((contentPreviewViewerDelegate = this.delegate) == null || contentPreviewViewerDelegate.needMenu())) {
                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 1300);
                    }
                    this.currentStickerSet = tLRPC$InputStickerSet;
                    TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90);
                    if (MessageObject.isVideoStickerDocument(tLRPC$Document)) {
                        this.centerImage.setImage(ImageLocation.getForDocument(tLRPC$Document), (String) null, ImageLocation.getForDocument(closestPhotoSizeWithSize2, tLRPC$Document2), (String) null, (Drawable) null, 0, "webp", this.currentStickerSet, 1);
                    } else {
                        this.centerImage.setImage(ImageLocation.getForDocument(tLRPC$Document), (String) null, ImageLocation.getForDocument(closestPhotoSizeWithSize2, tLRPC$Document2), (String) null, "webp", (Object) this.currentStickerSet, 1);
                        if (MessageObject.isPremiumSticker(tLRPC$Document)) {
                            this.drawEffect = true;
                            this.effectImage.setImage(ImageLocation.getForDocument(MessageObject.getPremiumStickerAnimation(tLRPC$Document), tLRPC$Document2), (String) null, (ImageLocation) null, (String) null, "tgs", (Object) this.currentStickerSet, 1);
                        }
                    }
                    int i5 = 0;
                    while (true) {
                        if (i5 >= tLRPC$Document2.attributes.size()) {
                            break;
                        }
                        TLRPC$DocumentAttribute tLRPC$DocumentAttribute2 = tLRPC$Document2.attributes.get(i5);
                        if ((tLRPC$DocumentAttribute2 instanceof TLRPC$TL_documentAttributeSticker) && !TextUtils.isEmpty(tLRPC$DocumentAttribute2.alt)) {
                            this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(tLRPC$DocumentAttribute2.alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            break;
                        }
                        i5++;
                    }
                } else if (importingSticker3 != null) {
                    this.centerImage.setImage(importingSticker3.path, (String) null, (Drawable) null, importingSticker3.animated ? "tgs" : null, 0);
                    if (str3 != null) {
                        this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(str3, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
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
                i2 = 0;
                this.centerImage.getLottieAnimation().setCurrentFrame(0);
            } else {
                i2 = 0;
            }
            if (this.drawEffect && this.effectImage.getLottieAnimation() != null) {
                this.effectImage.getLottieAnimation().setCurrentFrame(i2);
            }
            this.currentContentType = i3;
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
        int i;
        int i2;
        int i3;
        Drawable drawable;
        float f;
        WindowInsets windowInsets;
        float f2;
        if (this.containerView != null && this.backgroundDrawable != null) {
            if (this.menuVisible && this.blurrBitmap == null) {
                prepareBlurBitmap();
            }
            if (this.blurrBitmap != null) {
                boolean z = this.menuVisible;
                if (z) {
                    float f3 = this.blurProgress;
                    if (f3 != 1.0f) {
                        float f4 = f3 + 0.13333334f;
                        this.blurProgress = f4;
                        if (f4 > 1.0f) {
                            this.blurProgress = 1.0f;
                        }
                        this.containerView.invalidate();
                        f2 = this.blurProgress;
                        if (!(f2 == 0.0f || this.blurrBitmap == null)) {
                            this.paint.setAlpha((int) (f2 * 255.0f));
                            canvas.save();
                            canvas.scale(12.0f, 12.0f);
                            canvas.drawBitmap(this.blurrBitmap, 0.0f, 0.0f, this.paint);
                            canvas.restore();
                        }
                    }
                }
                if (!z) {
                    float f5 = this.blurProgress;
                    if (f5 != 0.0f) {
                        float f6 = f5 - 0.13333334f;
                        this.blurProgress = f6;
                        if (f6 < 0.0f) {
                            this.blurProgress = 0.0f;
                        }
                        this.containerView.invalidate();
                    }
                }
                f2 = this.blurProgress;
                this.paint.setAlpha((int) (f2 * 255.0f));
                canvas.save();
                canvas.scale(12.0f, 12.0f);
                canvas.drawBitmap(this.blurrBitmap, 0.0f, 0.0f, this.paint);
                canvas.restore();
            }
            this.backgroundDrawable.setAlpha((int) (this.showProgress * 180.0f));
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
                if (this.drawEffect) {
                    f = ((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight() - i2)) - AndroidUtilities.dpf2(40.0f);
                } else {
                    f = ((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight() - i2)) / 1.8f;
                }
                i3 = (int) f;
            }
            float max = (float) Math.max((i3 / 2) + i + (this.stickerEmojiLayout != null ? AndroidUtilities.dp(40.0f) : 0), ((this.containerView.getHeight() - i2) - this.keyboardHeight) / 2);
            if (this.drawEffect) {
                max += (float) AndroidUtilities.dp(40.0f);
            }
            canvas.translate((float) (this.containerView.getWidth() / 2), this.moveY + max);
            float f7 = this.showProgress;
            int i4 = (int) (((float) i3) * ((f7 * 0.8f) / 0.8f));
            if (this.drawEffect) {
                float f8 = (float) i4;
                float f9 = 0.6669f * f8;
                this.centerImage.setAlpha(f7);
                float var_ = f8 - f9;
                float var_ = f8 / 2.0f;
                this.centerImage.setImageCoords((var_ - var_) - (0.0546875f * f8), (var_ / 2.0f) - var_, f9, f9);
                this.centerImage.draw(canvas);
                this.effectImage.setAlpha(this.showProgress);
                float var_ = ((float) (-i4)) / 2.0f;
                this.effectImage.setImageCoords(var_, var_, f8, f8);
                this.effectImage.draw(canvas);
            } else {
                this.centerImage.setAlpha(f7);
                float var_ = ((float) (-i4)) / 2.0f;
                float var_ = (float) i4;
                this.centerImage.setImageCoords(var_, var_, var_, var_);
                this.centerImage.draw(canvas);
            }
            if (this.currentContentType == 1 && (drawable = this.slideUpDrawable) != null) {
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = this.slideUpDrawable.getIntrinsicHeight();
                int dp = (int) (this.centerImage.getDrawRegion().top - ((float) AndroidUtilities.dp(((this.currentMoveY / ((float) AndroidUtilities.dp(60.0f))) * 6.0f) + 17.0f)));
                this.slideUpDrawable.setAlpha((int) ((1.0f - this.currentMoveYProgress) * 255.0f));
                this.slideUpDrawable.setBounds((-intrinsicWidth) / 2, (-intrinsicHeight) + dp, intrinsicWidth / 2, dp);
                this.slideUpDrawable.draw(canvas);
            }
            if (this.stickerEmojiLayout != null) {
                if (this.drawEffect) {
                    canvas.translate((float) (-AndroidUtilities.dp(50.0f)), ((-this.effectImage.getImageHeight()) / 2.0f) - ((float) AndroidUtilities.dp(30.0f)));
                } else {
                    canvas.translate((float) (-AndroidUtilities.dp(50.0f)), ((-this.centerImage.getImageHeight()) / 2.0f) - ((float) AndroidUtilities.dp(30.0f)));
                }
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDraw$6() {
        this.centerImage.setImageBitmap((Bitmap) null);
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private void prepareBlurBitmap() {
        Activity activity = this.parentActivity;
        if (activity != null) {
            View decorView = activity.getWindow().getDecorView();
            int measuredWidth = (int) (((float) decorView.getMeasuredWidth()) / 12.0f);
            int measuredHeight = (int) (((float) decorView.getMeasuredHeight()) / 12.0f);
            Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.scale(0.083333336f, 0.083333336f);
            canvas.drawColor(Theme.getColor("windowBackgroundWhite"));
            decorView.draw(canvas);
            Activity activity2 = this.parentActivity;
            if ((activity2 instanceof LaunchActivity) && ((LaunchActivity) activity2).getActionBarLayout().getLastFragment().getVisibleDialog() != null) {
                ((LaunchActivity) this.parentActivity).getActionBarLayout().getLastFragment().getVisibleDialog().getWindow().getDecorView().draw(canvas);
            }
            Utilities.stackBlurBitmap(createBitmap, Math.max(10, Math.max(measuredWidth, measuredHeight) / 180));
            this.blurrBitmap = createBitmap;
        }
    }

    public boolean showMenuFor(View view) {
        if (!(view instanceof StickerEmojiCell)) {
            return false;
        }
        Activity findActivity = AndroidUtilities.findActivity(view.getContext());
        if (findActivity == null) {
            return true;
        }
        setParentActivity(findActivity);
        StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
        TLRPC$Document sticker = stickerEmojiCell.getSticker();
        SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
        String emoji = stickerEmojiCell.getEmoji();
        ContentPreviewViewerDelegate contentPreviewViewerDelegate = this.delegate;
        open(sticker, stickerPath, emoji, contentPreviewViewerDelegate != null ? contentPreviewViewerDelegate.getQuery(false) : null, (TLRPC$BotInlineResult) null, 0, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject(), this.resourcesProvider);
        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 16);
        stickerEmojiCell.setScaled(true);
        return true;
    }
}
