package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.LinkPath;

public class SharedLinkCell extends FrameLayout {
    public static final int VIEW_TYPE_DEFAULT = 0;
    public static final int VIEW_TYPE_GLOBAL_SEARCH = 1;
    private StaticLayout captionLayout;
    private TextPaint captionTextPaint;
    private int captionY;
    private CheckBox2 checkBox;
    /* access modifiers changed from: private */
    public boolean checkingForLongPress;
    private StaticLayout dateLayout;
    private int dateLayoutX;
    /* access modifiers changed from: private */
    public SharedLinkCellDelegate delegate;
    private TextPaint description2TextPaint;
    private int description2Y;
    private StaticLayout descriptionLayout;
    private StaticLayout descriptionLayout2;
    private TextPaint descriptionTextPaint;
    private int descriptionY;
    private boolean drawLinkImageView;
    private StaticLayout fromInfoLayout;
    private int fromInfoLayoutY;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private ArrayList<StaticLayout> linkLayout;
    private boolean linkPreviewPressed;
    private int linkY;
    ArrayList<String> links;
    private MessageObject message;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public CheckForLongPress pendingCheckForLongPress;
    private CheckForTap pendingCheckForTap;
    /* access modifiers changed from: private */
    public int pressCount;
    /* access modifiers changed from: private */
    public int pressedLink;
    private StaticLayout titleLayout;
    private TextPaint titleTextPaint;
    private int titleY;
    private LinkPath urlPath;
    private int viewType;

    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(TLRPC.WebPage webPage, MessageObject messageObject);

        void onLinkPress(String str, boolean z);
    }

    static /* synthetic */ int access$104(SharedLinkCell x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (SharedLinkCell.this.pendingCheckForLongPress == null) {
                CheckForLongPress unused = SharedLinkCell.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            SharedLinkCell.this.pendingCheckForLongPress.currentPressCount = SharedLinkCell.access$104(SharedLinkCell.this);
            SharedLinkCell sharedLinkCell = SharedLinkCell.this;
            sharedLinkCell.postDelayed(sharedLinkCell.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
        }
    }

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (SharedLinkCell.this.checkingForLongPress && SharedLinkCell.this.getParent() != null && this.currentPressCount == SharedLinkCell.this.pressCount) {
                boolean unused = SharedLinkCell.this.checkingForLongPress = false;
                SharedLinkCell.this.performHapticFeedback(0);
                if (SharedLinkCell.this.pressedLink >= 0) {
                    SharedLinkCell.this.delegate.onLinkPress(SharedLinkCell.this.links.get(SharedLinkCell.this.pressedLink), true);
                }
                MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                SharedLinkCell.this.onTouchEvent(event);
                event.recycle();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
        CheckForTap checkForTap = this.pendingCheckForTap;
        if (checkForTap != null) {
            removeCallbacks(checkForTap);
        }
    }

    public SharedLinkCell(Context context) {
        this(context, 0);
    }

    public SharedLinkCell(Context context, int viewType2) {
        super(context);
        this.checkingForLongPress = false;
        this.pendingCheckForLongPress = null;
        this.pressCount = 0;
        this.pendingCheckForTap = null;
        this.links = new ArrayList<>();
        this.linkLayout = new ArrayList<>();
        this.titleY = AndroidUtilities.dp(10.0f);
        this.descriptionY = AndroidUtilities.dp(30.0f);
        this.description2Y = AndroidUtilities.dp(30.0f);
        this.captionY = AndroidUtilities.dp(30.0f);
        this.fromInfoLayoutY = AndroidUtilities.dp(30.0f);
        this.viewType = viewType2;
        setFocusable(true);
        LinkPath linkPath = new LinkPath();
        this.urlPath = linkPath;
        linkPath.setUseRoundRect(true);
        TextPaint textPaint = new TextPaint(1);
        this.titleTextPaint = textPaint;
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextPaint = new TextPaint(1);
        this.titleTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        setWillNotDraw(false);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.linkImageView = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(4.0f));
        this.letterDrawable = new LetterDrawable();
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 44.0f, 44.0f, LocaleController.isRTL ? 44.0f : 0.0f, 0.0f));
        if (viewType2 == 1) {
            TextPaint textPaint2 = new TextPaint(1);
            this.description2TextPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
        }
        TextPaint textPaint3 = new TextPaint(1);
        this.captionTextPaint = textPaint3;
        textPaint3.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x011d A[Catch:{ Exception -> 0x0248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0186 A[Catch:{ Exception -> 0x0248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0199 A[Catch:{ Exception -> 0x0248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01b0 A[Catch:{ Exception -> 0x0245 }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01b3 A[Catch:{ Exception -> 0x0245 }] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x0207  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r35, int r36) {
        /*
            r34 = this;
            r1 = r34
            r2 = 0
            r1.drawLinkImageView = r2
            r3 = 0
            r1.descriptionLayout = r3
            r1.titleLayout = r3
            r1.descriptionLayout2 = r3
            r1.captionLayout = r3
            java.util.ArrayList<android.text.StaticLayout> r0 = r1.linkLayout
            r0.clear()
            java.util.ArrayList<java.lang.String> r0 = r1.links
            r0.clear()
            int r0 = android.view.View.MeasureSpec.getSize(r35)
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r4 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r0 - r5
            r0 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            r14 = 1
            if (r10 == 0) goto L_0x007b
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            org.telegram.tgnet.TLRPC$WebPage r10 = r10.webpage
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_webPage
            if (r10 == 0) goto L_0x007b
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            org.telegram.tgnet.TLRPC$WebPage r10 = r10.webpage
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r11.photoThumbs
            if (r11 != 0) goto L_0x005f
            org.telegram.tgnet.TLRPC$Photo r11 = r10.photo
            if (r11 == 0) goto L_0x005f
            org.telegram.messenger.MessageObject r11 = r1.message
            r11.generateThumbs(r14)
        L_0x005f:
            org.telegram.tgnet.TLRPC$Photo r11 = r10.photo
            if (r11 == 0) goto L_0x006b
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r11.photoThumbs
            if (r11 == 0) goto L_0x006b
            r11 = 1
            goto L_0x006c
        L_0x006b:
            r11 = 0
        L_0x006c:
            r9 = r11
            java.lang.String r0 = r10.title
            if (r0 != 0) goto L_0x0073
            java.lang.String r0 = r10.site_name
        L_0x0073:
            java.lang.String r6 = r10.description
            java.lang.String r8 = r10.url
            r15 = r8
            r16 = r9
            goto L_0x007e
        L_0x007b:
            r15 = r8
            r16 = r9
        L_0x007e:
            org.telegram.messenger.MessageObject r8 = r1.message
            if (r8 == 0) goto L_0x0258
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r8.entities
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x0258
            r8 = 0
            r33 = r6
            r6 = r0
            r0 = r7
            r7 = r33
        L_0x0093:
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r9 = r9.entities
            int r9 = r9.size()
            if (r8 >= r9) goto L_0x0254
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r9 = r9.entities
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$MessageEntity r9 = (org.telegram.tgnet.TLRPC.MessageEntity) r9
            int r10 = r9.length
            if (r10 <= 0) goto L_0x024d
            int r10 = r9.offset
            if (r10 < 0) goto L_0x024d
            int r10 = r9.offset
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            if (r10 < r11) goto L_0x00c3
            goto L_0x024d
        L_0x00c3:
            int r10 = r9.offset
            int r11 = r9.length
            int r10 = r10 + r11
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            if (r10 <= r11) goto L_0x00e3
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.lang.String r10 = r10.message
            int r10 = r10.length()
            int r11 = r9.offset
            int r10 = r10 - r11
            r9.length = r10
        L_0x00e3:
            if (r8 != 0) goto L_0x0117
            if (r15 == 0) goto L_0x0117
            int r10 = r9.offset
            if (r10 != 0) goto L_0x00f9
            int r10 = r9.length
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            if (r10 == r11) goto L_0x0117
        L_0x00f9:
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            int r10 = r10.size()
            if (r10 != r14) goto L_0x010f
            if (r7 != 0) goto L_0x0117
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.lang.String r0 = r10.message
            r10 = r0
            goto L_0x0118
        L_0x010f:
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.lang.String r0 = r10.message
            r10 = r0
            goto L_0x0118
        L_0x0117:
            r10 = r0
        L_0x0118:
            r0 = 0
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl     // Catch:{ Exception -> 0x0248 }
            if (r11 != 0) goto L_0x0182
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl     // Catch:{ Exception -> 0x0248 }
            if (r11 == 0) goto L_0x0122
            goto L_0x0182
        L_0x0122:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityEmail     // Catch:{ Exception -> 0x0248 }
            if (r11 == 0) goto L_0x0205
            if (r6 == 0) goto L_0x012e
            int r11 = r6.length()     // Catch:{ Exception -> 0x0248 }
            if (r11 != 0) goto L_0x0205
        L_0x012e:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0248 }
            r11.<init>()     // Catch:{ Exception -> 0x0248 }
            java.lang.String r12 = "mailto:"
            r11.append(r12)     // Catch:{ Exception -> 0x0248 }
            org.telegram.messenger.MessageObject r12 = r1.message     // Catch:{ Exception -> 0x0248 }
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner     // Catch:{ Exception -> 0x0248 }
            java.lang.String r12 = r12.message     // Catch:{ Exception -> 0x0248 }
            int r13 = r9.offset     // Catch:{ Exception -> 0x0248 }
            int r3 = r9.offset     // Catch:{ Exception -> 0x0248 }
            int r4 = r9.length     // Catch:{ Exception -> 0x0248 }
            int r3 = r3 + r4
            java.lang.String r3 = r12.substring(r13, r3)     // Catch:{ Exception -> 0x0248 }
            r11.append(r3)     // Catch:{ Exception -> 0x0248 }
            java.lang.String r3 = r11.toString()     // Catch:{ Exception -> 0x0248 }
            r0 = r3
            org.telegram.messenger.MessageObject r3 = r1.message     // Catch:{ Exception -> 0x0248 }
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner     // Catch:{ Exception -> 0x0248 }
            java.lang.String r3 = r3.message     // Catch:{ Exception -> 0x0248 }
            int r4 = r9.offset     // Catch:{ Exception -> 0x0248 }
            int r11 = r9.offset     // Catch:{ Exception -> 0x0248 }
            int r12 = r9.length     // Catch:{ Exception -> 0x0248 }
            int r11 = r11 + r12
            java.lang.String r3 = r3.substring(r4, r11)     // Catch:{ Exception -> 0x0248 }
            int r4 = r9.offset     // Catch:{ Exception -> 0x0245 }
            if (r4 != 0) goto L_0x0178
            int r4 = r9.length     // Catch:{ Exception -> 0x0245 }
            org.telegram.messenger.MessageObject r6 = r1.message     // Catch:{ Exception -> 0x0245 }
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner     // Catch:{ Exception -> 0x0245 }
            java.lang.String r6 = r6.message     // Catch:{ Exception -> 0x0245 }
            int r6 = r6.length()     // Catch:{ Exception -> 0x0245 }
            if (r4 == r6) goto L_0x0175
            goto L_0x0178
        L_0x0175:
            r6 = r3
            goto L_0x0205
        L_0x0178:
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x0245 }
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner     // Catch:{ Exception -> 0x0245 }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x0245 }
            r6 = r3
            r7 = r4
            goto L_0x0205
        L_0x0182:
            boolean r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl     // Catch:{ Exception -> 0x0248 }
            if (r3 == 0) goto L_0x0199
            org.telegram.messenger.MessageObject r3 = r1.message     // Catch:{ Exception -> 0x0248 }
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner     // Catch:{ Exception -> 0x0248 }
            java.lang.String r3 = r3.message     // Catch:{ Exception -> 0x0248 }
            int r4 = r9.offset     // Catch:{ Exception -> 0x0248 }
            int r11 = r9.offset     // Catch:{ Exception -> 0x0248 }
            int r12 = r9.length     // Catch:{ Exception -> 0x0248 }
            int r11 = r11 + r12
            java.lang.String r3 = r3.substring(r4, r11)     // Catch:{ Exception -> 0x0248 }
            r0 = r3
            goto L_0x019c
        L_0x0199:
            java.lang.String r3 = r9.url     // Catch:{ Exception -> 0x0248 }
            r0 = r3
        L_0x019c:
            if (r6 == 0) goto L_0x01a4
            int r3 = r6.length()     // Catch:{ Exception -> 0x0248 }
            if (r3 != 0) goto L_0x0205
        L_0x01a4:
            r3 = r0
            android.net.Uri r4 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0245 }
            java.lang.String r6 = r4.getHost()     // Catch:{ Exception -> 0x0245 }
            r3 = r6
            if (r3 != 0) goto L_0x01b1
            r3 = r0
        L_0x01b1:
            if (r3 == 0) goto L_0x01eb
            r6 = 46
            int r11 = r3.lastIndexOf(r6)     // Catch:{ Exception -> 0x0245 }
            r12 = r11
            if (r11 < 0) goto L_0x01eb
            java.lang.String r11 = r3.substring(r2, r12)     // Catch:{ Exception -> 0x0245 }
            r3 = r11
            int r6 = r3.lastIndexOf(r6)     // Catch:{ Exception -> 0x0245 }
            r11 = r6
            if (r6 < 0) goto L_0x01cf
            int r6 = r11 + 1
            java.lang.String r6 = r3.substring(r6)     // Catch:{ Exception -> 0x0245 }
            r3 = r6
        L_0x01cf:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0245 }
            r6.<init>()     // Catch:{ Exception -> 0x0245 }
            java.lang.String r12 = r3.substring(r2, r14)     // Catch:{ Exception -> 0x0245 }
            java.lang.String r12 = r12.toUpperCase()     // Catch:{ Exception -> 0x0245 }
            r6.append(r12)     // Catch:{ Exception -> 0x0245 }
            java.lang.String r12 = r3.substring(r14)     // Catch:{ Exception -> 0x0245 }
            r6.append(r12)     // Catch:{ Exception -> 0x0245 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0245 }
            r3 = r6
        L_0x01eb:
            int r6 = r9.offset     // Catch:{ Exception -> 0x0245 }
            if (r6 != 0) goto L_0x01fd
            int r6 = r9.length     // Catch:{ Exception -> 0x0245 }
            org.telegram.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x0245 }
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner     // Catch:{ Exception -> 0x0245 }
            java.lang.String r11 = r11.message     // Catch:{ Exception -> 0x0245 }
            int r11 = r11.length()     // Catch:{ Exception -> 0x0245 }
            if (r6 == r11) goto L_0x0204
        L_0x01fd:
            org.telegram.messenger.MessageObject r6 = r1.message     // Catch:{ Exception -> 0x0245 }
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner     // Catch:{ Exception -> 0x0245 }
            java.lang.String r6 = r6.message     // Catch:{ Exception -> 0x0245 }
            r7 = r6
        L_0x0204:
            r6 = r3
        L_0x0205:
            if (r0 == 0) goto L_0x0243
            java.lang.String r3 = "://"
            boolean r3 = r0.contains(r3)     // Catch:{ Exception -> 0x0248 }
            if (r3 != 0) goto L_0x023e
            java.lang.String r3 = r0.toLowerCase()     // Catch:{ Exception -> 0x0248 }
            java.lang.String r4 = "http"
            int r3 = r3.indexOf(r4)     // Catch:{ Exception -> 0x0248 }
            if (r3 == 0) goto L_0x023e
            java.lang.String r3 = r0.toLowerCase()     // Catch:{ Exception -> 0x0248 }
            java.lang.String r4 = "mailto"
            int r3 = r3.indexOf(r4)     // Catch:{ Exception -> 0x0248 }
            if (r3 == 0) goto L_0x023e
            java.util.ArrayList<java.lang.String> r3 = r1.links     // Catch:{ Exception -> 0x0248 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0248 }
            r4.<init>()     // Catch:{ Exception -> 0x0248 }
            java.lang.String r11 = "http://"
            r4.append(r11)     // Catch:{ Exception -> 0x0248 }
            r4.append(r0)     // Catch:{ Exception -> 0x0248 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0248 }
            r3.add(r4)     // Catch:{ Exception -> 0x0248 }
            goto L_0x0243
        L_0x023e:
            java.util.ArrayList<java.lang.String> r3 = r1.links     // Catch:{ Exception -> 0x0248 }
            r3.add(r0)     // Catch:{ Exception -> 0x0248 }
        L_0x0243:
            r0 = r10
            goto L_0x024d
        L_0x0245:
            r0 = move-exception
            r6 = r3
            goto L_0x0249
        L_0x0248:
            r0 = move-exception
        L_0x0249:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r10
        L_0x024d:
            int r8 = r8 + 1
            r3 = 0
            r4 = 1090519040(0x41000000, float:8.0)
            goto L_0x0093
        L_0x0254:
            r3 = r6
            r6 = r7
            r7 = r0
            goto L_0x0259
        L_0x0258:
            r3 = r0
        L_0x0259:
            if (r15 == 0) goto L_0x0268
            java.util.ArrayList<java.lang.String> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0268
            java.util.ArrayList<java.lang.String> r0 = r1.links
            r0.add(r15)
        L_0x0268:
            r0 = 0
            int r4 = r1.viewType
            if (r4 != r14) goto L_0x02ad
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            int r4 = r4.date
            long r8 = (long) r4
            java.lang.String r4 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            android.text.TextPaint r8 = r1.description2TextPaint
            float r8 = r8.measureText(r4)
            double r8 = (double) r8
            double r8 = java.lang.Math.ceil(r8)
            int r8 = (int) r8
            android.text.TextPaint r9 = r1.description2TextPaint
            r23 = 0
            r24 = 1
            r19 = r4
            r20 = r9
            r21 = r8
            r22 = r8
            android.text.StaticLayout r9 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r19, r20, r21, r22, r23, r24)
            r1.dateLayout = r9
            int r9 = r5 - r8
            r10 = 1090519040(0x41000000, float:8.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
            r1.dateLayoutX = r9
            r9 = 1094713344(0x41400000, float:12.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r0 = r8 + r9
            r4 = r0
            goto L_0x02ae
        L_0x02ad:
            r4 = r0
        L_0x02ae:
            r18 = 1082130432(0x40800000, float:4.0)
            if (r3 == 0) goto L_0x0304
            r0 = r3
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x02fb }
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords     // Catch:{ Exception -> 0x02fb }
            r9 = 0
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)     // Catch:{ Exception -> 0x02fb }
            if (r8 == 0) goto L_0x02bf
            r0 = r8
        L_0x02bf:
            android.text.TextPaint r9 = r1.titleTextPaint     // Catch:{ Exception -> 0x02fb }
            int r10 = r5 - r4
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)     // Catch:{ Exception -> 0x02fb }
            int r21 = r10 - r11
            int r10 = r5 - r4
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)     // Catch:{ Exception -> 0x02fb }
            int r22 = r10 - r11
            r23 = 0
            r24 = 3
            r19 = r0
            r20 = r9
            android.text.StaticLayout r9 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r19, r20, r21, r22, r23, r24)     // Catch:{ Exception -> 0x02fb }
            r1.titleLayout = r9     // Catch:{ Exception -> 0x02fb }
            int r9 = r9.getLineCount()     // Catch:{ Exception -> 0x02fb }
            if (r9 <= 0) goto L_0x02fa
            int r9 = r1.titleY     // Catch:{ Exception -> 0x02fb }
            android.text.StaticLayout r10 = r1.titleLayout     // Catch:{ Exception -> 0x02fb }
            int r11 = r10.getLineCount()     // Catch:{ Exception -> 0x02fb }
            int r11 = r11 - r14
            int r10 = r10.getLineBottom(r11)     // Catch:{ Exception -> 0x02fb }
            int r9 = r9 + r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)     // Catch:{ Exception -> 0x02fb }
            int r9 = r9 + r10
            r1.descriptionY = r9     // Catch:{ Exception -> 0x02fb }
        L_0x02fa:
            goto L_0x02ff
        L_0x02fb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02ff:
            org.telegram.ui.Components.LetterDrawable r0 = r1.letterDrawable
            r0.setTitle(r3)
        L_0x0304:
            int r0 = r1.descriptionY
            r1.description2Y = r0
            android.text.StaticLayout r0 = r1.titleLayout
            if (r0 == 0) goto L_0x0311
            int r0 = r0.getLineCount()
            goto L_0x0312
        L_0x0311:
            r0 = 0
        L_0x0312:
            int r0 = 4 - r0
            int r19 = java.lang.Math.max(r14, r0)
            int r0 = r1.viewType
            if (r0 != r14) goto L_0x0323
            r6 = 0
            r7 = 0
            r20 = r6
            r21 = r7
            goto L_0x0327
        L_0x0323:
            r20 = r6
            r21 = r7
        L_0x0327:
            r22 = 1084227584(0x40a00000, float:5.0)
            if (r20 == 0) goto L_0x035a
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0356 }
            r10 = 0
            r6 = r20
            r8 = r5
            r9 = r5
            r11 = r19
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0356 }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x0356 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x0356 }
            if (r0 <= 0) goto L_0x0355
            int r0 = r1.descriptionY     // Catch:{ Exception -> 0x0356 }
            android.text.StaticLayout r6 = r1.descriptionLayout     // Catch:{ Exception -> 0x0356 }
            int r7 = r6.getLineCount()     // Catch:{ Exception -> 0x0356 }
            int r7 = r7 - r14
            int r6 = r6.getLineBottom(r7)     // Catch:{ Exception -> 0x0356 }
            int r0 = r0 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r22)     // Catch:{ Exception -> 0x0356 }
            int r0 = r0 + r6
            r1.description2Y = r0     // Catch:{ Exception -> 0x0356 }
        L_0x0355:
            goto L_0x035a
        L_0x0356:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x035a:
            r23 = 1092616192(0x41200000, float:10.0)
            if (r21 == 0) goto L_0x037f
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x037b }
            r10 = 0
            r6 = r21
            r8 = r5
            r9 = r5
            r11 = r19
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x037b }
            r1.descriptionLayout2 = r0     // Catch:{ Exception -> 0x037b }
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x037b }
            if (r0 == 0) goto L_0x037a
            int r0 = r1.description2Y     // Catch:{ Exception -> 0x037b }
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r23)     // Catch:{ Exception -> 0x037b }
            int r0 = r0 + r6
            r1.description2Y = r0     // Catch:{ Exception -> 0x037b }
        L_0x037a:
            goto L_0x037f
        L_0x037b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x037f:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x03f8
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x03f8
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            java.lang.String r6 = "\n"
            java.lang.String r7 = " "
            java.lang.String r0 = r0.replace(r6, r7)
            java.lang.String r6 = " +"
            java.lang.String r0 = r0.replaceAll(r6, r7)
            java.lang.String r0 = r0.trim()
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r6, r7, r2)
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r7 = 0
            java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
            if (r6 == 0) goto L_0x03f8
            org.telegram.messenger.MessageObject r7 = r1.message
            java.util.ArrayList<java.lang.String> r7 = r7.highlightedWords
            java.lang.Object r7 = r7.get(r2)
            java.lang.String r7 = (java.lang.String) r7
            android.text.TextPaint r8 = r1.captionTextPaint
            r9 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r7 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r6, r7, r5, r8, r9)
            android.text.TextPaint r8 = r1.captionTextPaint
            float r9 = (float) r5
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r6 = android.text.TextUtils.ellipsize(r7, r8, r9, r10)
            android.text.StaticLayout r7 = new android.text.StaticLayout
            android.text.TextPaint r8 = r1.captionTextPaint
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r27 = r5 + r9
            android.text.Layout$Alignment r28 = android.text.Layout.Alignment.ALIGN_NORMAL
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 0
            r31 = 0
            r24 = r7
            r25 = r6
            r26 = r8
            r24.<init>(r25, r26, r27, r28, r29, r30, r31)
            r1.captionLayout = r7
        L_0x03f8:
            android.text.StaticLayout r0 = r1.captionLayout
            if (r0 == 0) goto L_0x0413
            int r6 = r1.descriptionY
            r1.captionY = r6
            int r7 = r0.getLineCount()
            int r7 = r7 - r14
            int r0 = r0.getLineBottom(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 + r7
            int r6 = r6 + r0
            r1.descriptionY = r6
            r1.description2Y = r6
        L_0x0413:
            java.util.ArrayList<java.lang.String> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x04a2
            r0 = 0
            r13 = r0
        L_0x041d:
            java.util.ArrayList<java.lang.String> r0 = r1.links
            int r0 = r0.size()
            if (r13 >= r0) goto L_0x04a0
            java.util.ArrayList<java.lang.String> r0 = r1.links     // Catch:{ Exception -> 0x0496 }
            java.lang.Object r0 = r0.get(r13)     // Catch:{ Exception -> 0x0496 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x0496 }
            android.text.TextPaint r6 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0496 }
            float r6 = r6.measureText(r0)     // Catch:{ Exception -> 0x0496 }
            double r6 = (double) r6     // Catch:{ Exception -> 0x0496 }
            double r6 = java.lang.Math.ceil(r6)     // Catch:{ Exception -> 0x0496 }
            int r12 = (int) r6     // Catch:{ Exception -> 0x0496 }
            r6 = 10
            r7 = 32
            java.lang.String r6 = r0.replace(r6, r7)     // Catch:{ Exception -> 0x0496 }
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0496 }
            int r8 = java.lang.Math.min(r12, r5)     // Catch:{ Exception -> 0x0496 }
            float r8 = (float) r8     // Catch:{ Exception -> 0x0496 }
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x0496 }
            java.lang.CharSequence r7 = android.text.TextUtils.ellipsize(r6, r7, r8, r9)     // Catch:{ Exception -> 0x0496 }
            android.text.StaticLayout r17 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0496 }
            android.text.TextPaint r8 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0496 }
            android.text.Layout$Alignment r10 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0496 }
            r11 = 1065353216(0x3var_, float:1.0)
            r24 = 0
            r25 = 0
            r6 = r17
            r9 = r5
            r26 = r12
            r12 = r24
            r24 = r13
            r13 = r25
            r6.<init>(r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x0494 }
            r6 = r17
            int r8 = r1.description2Y     // Catch:{ Exception -> 0x0494 }
            r1.linkY = r8     // Catch:{ Exception -> 0x0494 }
            android.text.StaticLayout r8 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0494 }
            if (r8 == 0) goto L_0x048d
            int r8 = r8.getLineCount()     // Catch:{ Exception -> 0x0494 }
            if (r8 == 0) goto L_0x048d
            int r8 = r1.linkY     // Catch:{ Exception -> 0x0494 }
            android.text.StaticLayout r9 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0494 }
            int r10 = r9.getLineCount()     // Catch:{ Exception -> 0x0494 }
            int r10 = r10 - r14
            int r9 = r9.getLineBottom(r10)     // Catch:{ Exception -> 0x0494 }
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r22)     // Catch:{ Exception -> 0x0494 }
            int r9 = r9 + r10
            int r8 = r8 + r9
            r1.linkY = r8     // Catch:{ Exception -> 0x0494 }
        L_0x048d:
            java.util.ArrayList<android.text.StaticLayout> r8 = r1.linkLayout     // Catch:{ Exception -> 0x0494 }
            r8.add(r6)     // Catch:{ Exception -> 0x0494 }
            goto L_0x049c
        L_0x0494:
            r0 = move-exception
            goto L_0x0499
        L_0x0496:
            r0 = move-exception
            r24 = r13
        L_0x0499:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x049c:
            int r13 = r24 + 1
            goto L_0x041d
        L_0x04a0:
            r24 = r13
        L_0x04a2:
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x04b7
            int r6 = android.view.View.MeasureSpec.getSize(r35)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r6 = r6 - r7
            int r6 = r6 - r0
            goto L_0x04bb
        L_0x04b7:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r23)
        L_0x04bb:
            r12 = r6
            org.telegram.ui.Components.LetterDrawable r6 = r1.letterDrawable
            r7 = 1093664768(0x41300000, float:11.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r9 = r12 + r0
            r10 = 1115422720(0x427CLASSNAME, float:63.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r6.setBounds(r12, r8, r9, r10)
            if (r16 == 0) goto L_0x0552
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r0, r14)
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            r9 = 80
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            if (r8 != r6) goto L_0x04e6
            r8 = 0
        L_0x04e6:
            r9 = -1
            r6.size = r9
            if (r8 == 0) goto L_0x04ed
            r8.size = r9
        L_0x04ed:
            org.telegram.messenger.ImageReceiver r9 = r1.linkImageView
            float r10 = (float) r12
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r11 = (float) r0
            float r13 = (float) r0
            r9.setImageCoords(r10, r7, r11, r13)
            java.lang.String r7 = org.telegram.messenger.FileLoader.getAttachFileName(r6)
            java.util.Locale r9 = java.util.Locale.US
            r10 = 2
            java.lang.Object[] r11 = new java.lang.Object[r10]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r0)
            r11[r2] = r13
            java.lang.Integer r13 = java.lang.Integer.valueOf(r0)
            r11[r14] = r13
            java.lang.String r13 = "%d_%d"
            java.lang.String r9 = java.lang.String.format(r9, r13, r11)
            java.util.Locale r11 = java.util.Locale.US
            java.lang.Object[] r10 = new java.lang.Object[r10]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r0)
            r10[r2] = r13
            java.lang.Integer r2 = java.lang.Integer.valueOf(r0)
            r10[r14] = r2
            java.lang.String r2 = "%d_%d_b"
            java.lang.String r2 = java.lang.String.format(r11, r2, r10)
            org.telegram.messenger.ImageReceiver r10 = r1.linkImageView
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLObject r11 = r11.photoThumbsObject
            org.telegram.messenger.ImageLocation r25 = org.telegram.messenger.ImageLocation.getForObject(r6, r11)
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLObject r11 = r11.photoThumbsObject
            org.telegram.messenger.ImageLocation r27 = org.telegram.messenger.ImageLocation.getForObject(r8, r11)
            r29 = 0
            r30 = 0
            org.telegram.messenger.MessageObject r11 = r1.message
            r32 = 0
            r24 = r10
            r26 = r9
            r28 = r2
            r31 = r11
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            r1.drawLinkImageView = r14
        L_0x0552:
            int r2 = r1.viewType
            if (r2 != r14) goto L_0x0569
            org.telegram.messenger.MessageObject r2 = r1.message
            java.lang.CharSequence r6 = org.telegram.ui.FilteredSearchView.createFromInfoString(r2)
            android.text.TextPaint r7 = r1.description2TextPaint
            r10 = 0
            r8 = r5
            r9 = r5
            r11 = r19
            android.text.StaticLayout r2 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)
            r1.fromInfoLayout = r2
        L_0x0569:
            r2 = 0
            android.text.StaticLayout r6 = r1.titleLayout
            if (r6 == 0) goto L_0x0585
            int r6 = r6.getLineCount()
            if (r6 == 0) goto L_0x0585
            android.text.StaticLayout r6 = r1.titleLayout
            int r7 = r6.getLineCount()
            int r7 = r7 - r14
            int r6 = r6.getLineBottom(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r6 = r6 + r7
            int r2 = r2 + r6
        L_0x0585:
            android.text.StaticLayout r6 = r1.captionLayout
            if (r6 == 0) goto L_0x05a0
            int r6 = r6.getLineCount()
            if (r6 == 0) goto L_0x05a0
            android.text.StaticLayout r6 = r1.captionLayout
            int r7 = r6.getLineCount()
            int r7 = r7 - r14
            int r6 = r6.getLineBottom(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r6 = r6 + r7
            int r2 = r2 + r6
        L_0x05a0:
            android.text.StaticLayout r6 = r1.descriptionLayout
            if (r6 == 0) goto L_0x05bb
            int r6 = r6.getLineCount()
            if (r6 == 0) goto L_0x05bb
            android.text.StaticLayout r6 = r1.descriptionLayout
            int r7 = r6.getLineCount()
            int r7 = r7 - r14
            int r6 = r6.getLineBottom(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r6 = r6 + r7
            int r2 = r2 + r6
        L_0x05bb:
            android.text.StaticLayout r6 = r1.descriptionLayout2
            if (r6 == 0) goto L_0x05df
            int r6 = r6.getLineCount()
            if (r6 == 0) goto L_0x05df
            android.text.StaticLayout r6 = r1.descriptionLayout2
            int r7 = r6.getLineCount()
            int r7 = r7 - r14
            int r6 = r6.getLineBottom(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r6 = r6 + r7
            int r2 = r2 + r6
            android.text.StaticLayout r6 = r1.descriptionLayout
            if (r6 == 0) goto L_0x05df
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r23)
            int r2 = r2 + r6
        L_0x05df:
            r6 = 0
            r7 = 0
        L_0x05e1:
            java.util.ArrayList<android.text.StaticLayout> r8 = r1.linkLayout
            int r8 = r8.size()
            if (r7 >= r8) goto L_0x0604
            java.util.ArrayList<android.text.StaticLayout> r8 = r1.linkLayout
            java.lang.Object r8 = r8.get(r7)
            android.text.StaticLayout r8 = (android.text.StaticLayout) r8
            int r9 = r8.getLineCount()
            if (r9 <= 0) goto L_0x0601
            int r9 = r8.getLineCount()
            int r9 = r9 - r14
            int r9 = r8.getLineBottom(r9)
            int r6 = r6 + r9
        L_0x0601:
            int r7 = r7 + 1
            goto L_0x05e1
        L_0x0604:
            int r2 = r2 + r6
            android.text.StaticLayout r7 = r1.fromInfoLayout
            if (r7 == 0) goto L_0x0624
            int r7 = r1.linkY
            int r7 = r7 + r6
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r7 = r7 + r8
            r1.fromInfoLayoutY = r7
            android.text.StaticLayout r7 = r1.fromInfoLayout
            int r8 = r7.getLineCount()
            int r8 = r8 - r14
            int r7 = r7.getLineBottom(r8)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r7 = r7 + r8
            int r2 = r2 + r7
        L_0x0624:
            org.telegram.ui.Components.CheckBox2 r7 = r1.checkBox
            r8 = 1103101952(0x41CLASSNAME, float:24.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r10 = 1073741824(0x40000000, float:2.0)
            int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r10)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r10)
            r7.measure(r9, r8)
            int r7 = android.view.View.MeasureSpec.getSize(r35)
            r8 = 1117257728(0x42980000, float:76.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 1099431936(0x41880000, float:17.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r2
            int r8 = java.lang.Math.max(r8, r9)
            boolean r9 = r1.needDivider
            int r8 = r8 + r9
            r1.setMeasuredDimension(r7, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedLinkCell.onMeasure(int, int):void");
    }

    public void setLink(MessageObject messageObject, boolean divider) {
        this.needDivider = divider;
        resetPressedLink();
        this.message = messageObject;
        requestLayout();
    }

    public ImageReceiver getLinkImageView() {
        return this.linkImageView;
    }

    public void setDelegate(SharedLinkCellDelegate sharedLinkCellDelegate) {
        this.delegate = sharedLinkCellDelegate;
    }

    public MessageObject getMessage() {
        return this.message;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onAttachedToWindow();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        SharedLinkCellDelegate sharedLinkCellDelegate;
        boolean result = false;
        if (this.message == null || this.linkLayout.isEmpty() || (sharedLinkCellDelegate = this.delegate) == null || !sharedLinkCellDelegate.canPerformActions()) {
            resetPressedLink();
        } else if (event.getAction() == 0 || (this.linkPreviewPressed && event.getAction() == 1)) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            boolean ok = false;
            int a = 0;
            int offset = 0;
            while (true) {
                if (a >= this.linkLayout.size()) {
                    break;
                }
                StaticLayout layout = this.linkLayout.get(a);
                if (layout.getLineCount() > 0) {
                    int height = layout.getLineBottom(layout.getLineCount() - 1);
                    int linkPosX = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
                    if (((float) x) >= ((float) linkPosX) + layout.getLineLeft(0) && ((float) x) <= ((float) linkPosX) + layout.getLineWidth(0)) {
                        int i = this.linkY;
                        if (y >= i + offset && y <= i + offset + height) {
                            ok = true;
                            if (event.getAction() == 0) {
                                resetPressedLink();
                                this.pressedLink = a;
                                this.linkPreviewPressed = true;
                                startCheckLongPress();
                                try {
                                    this.urlPath.setCurrentLayout(layout, 0, 0.0f);
                                    layout.getSelectionPath(0, layout.getText().length(), this.urlPath);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                                result = true;
                            } else if (this.linkPreviewPressed) {
                                try {
                                    TLRPC.WebPage webPage = (this.pressedLink != 0 || this.message.messageOwner.media == null) ? null : this.message.messageOwner.media.webpage;
                                    if (webPage == null || webPage.embed_url == null || webPage.embed_url.length() == 0) {
                                        this.delegate.onLinkPress(this.links.get(this.pressedLink), false);
                                    } else {
                                        this.delegate.needOpenWebView(webPage, this.message);
                                    }
                                } catch (Exception e2) {
                                    FileLog.e((Throwable) e2);
                                }
                                resetPressedLink();
                                result = true;
                            }
                        }
                    }
                    offset += height;
                }
                a++;
            }
            if (!ok) {
                resetPressedLink();
            }
        } else if (event.getAction() == 3) {
            resetPressedLink();
        }
        if (result || super.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    public String getLink(int num) {
        if (num < 0 || num >= this.links.size()) {
            return null;
        }
        return this.links.get(num);
    }

    /* access modifiers changed from: protected */
    public void resetPressedLink() {
        this.pressedLink = -1;
        this.linkPreviewPressed = false;
        cancelCheckLongPress();
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.viewType == 1) {
            this.description2TextPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        }
        float f = 8.0f;
        if (this.dateLayout != null) {
            canvas.save();
            canvas.translate((float) (AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline) + (LocaleController.isRTL ? 0 : this.dateLayoutX)), (float) this.titleY);
            this.dateLayout.draw(canvas);
            canvas.restore();
        }
        if (this.titleLayout != null) {
            canvas.save();
            float x = (float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
            if (LocaleController.isRTL) {
                StaticLayout staticLayout = this.dateLayout;
                x += staticLayout == null ? 0.0f : (float) (staticLayout.getWidth() + AndroidUtilities.dp(4.0f));
            }
            canvas.translate(x, (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.captionLayout != null) {
            this.captionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.captionY);
            this.captionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout2 != null) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.description2Y);
            this.descriptionLayout2.draw(canvas);
            canvas.restore();
        }
        if (!this.linkLayout.isEmpty()) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            int offset = 0;
            for (int a = 0; a < this.linkLayout.size(); a++) {
                StaticLayout layout = this.linkLayout.get(a);
                if (layout.getLineCount() > 0) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) (this.linkY + offset));
                    if (this.pressedLink == a) {
                        canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
                    }
                    layout.draw(canvas);
                    canvas.restore();
                    offset += layout.getLineBottom(layout.getLineCount() - 1);
                }
            }
        }
        if (this.fromInfoLayout != null) {
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.fromInfoLayoutY);
            this.fromInfoLayout.draw(canvas);
            canvas.restore();
        }
        this.letterDrawable.draw(canvas);
        if (this.drawLinkImageView) {
            this.linkImageView.draw(canvas);
        }
        if (!this.needDivider) {
            return;
        }
        if (LocaleController.isRTL) {
            canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        } else {
            canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder sb = new StringBuilder();
        StaticLayout staticLayout = this.titleLayout;
        if (staticLayout != null) {
            sb.append(staticLayout.getText());
        }
        if (this.descriptionLayout != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout.getText());
        }
        if (this.descriptionLayout2 != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout2.getText());
        }
        info.setText(sb.toString());
        if (this.checkBox.isChecked()) {
            info.setChecked(true);
            info.setCheckable(true);
        }
    }
}
