package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.spoilers.SpoilerEffect;

public class SharedLinkCell extends FrameLayout {
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
    private List<SpoilerEffect> descriptionLayout2Spoilers;
    private List<SpoilerEffect> descriptionLayoutSpoilers;
    private TextPaint descriptionTextPaint;
    private int descriptionY;
    private boolean drawLinkImageView;
    private StaticLayout fromInfoLayout;
    private int fromInfoLayoutY;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private ArrayList<StaticLayout> linkLayout;
    private boolean linkPreviewPressed;
    private SparseArray<List<SpoilerEffect>> linkSpoilers;
    private int linkY;
    ArrayList<CharSequence> links;
    private MessageObject message;
    private boolean needDivider;
    private AtomicReference<Layout> patchedDescriptionLayout;
    private AtomicReference<Layout> patchedDescriptionLayout2;
    private Path path;
    /* access modifiers changed from: private */
    public CheckForLongPress pendingCheckForLongPress;
    private CheckForTap pendingCheckForTap;
    /* access modifiers changed from: private */
    public int pressCount;
    /* access modifiers changed from: private */
    public int pressedLink;
    private SpoilerEffect spoilerPressed;
    private int spoilerTypePressed;
    private Stack<SpoilerEffect> spoilersPool;
    private StaticLayout titleLayout;
    private TextPaint titleTextPaint;
    private int titleY;
    private LinkPath urlPath;
    private int viewType;

    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(TLRPC$WebPage tLRPC$WebPage, MessageObject messageObject);

        void onLinkPress(String str, boolean z);
    }

    static /* synthetic */ int access$104(SharedLinkCell sharedLinkCell) {
        int i = sharedLinkCell.pressCount + 1;
        sharedLinkCell.pressCount = i;
        return i;
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (SharedLinkCell.this.pendingCheckForLongPress == null) {
                SharedLinkCell sharedLinkCell = SharedLinkCell.this;
                CheckForLongPress unused = sharedLinkCell.pendingCheckForLongPress = new CheckForLongPress();
            }
            SharedLinkCell.this.pendingCheckForLongPress.currentPressCount = SharedLinkCell.access$104(SharedLinkCell.this);
            SharedLinkCell sharedLinkCell2 = SharedLinkCell.this;
            sharedLinkCell2.postDelayed(sharedLinkCell2.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
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
                    SharedLinkCellDelegate access$400 = SharedLinkCell.this.delegate;
                    SharedLinkCell sharedLinkCell = SharedLinkCell.this;
                    access$400.onLinkPress(sharedLinkCell.links.get(sharedLinkCell.pressedLink).toString(), true);
                }
                MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                SharedLinkCell.this.onTouchEvent(obtain);
                obtain.recycle();
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

    public SharedLinkCell(Context context, int i) {
        super(context);
        this.checkingForLongPress = false;
        this.pendingCheckForLongPress = null;
        this.pressCount = 0;
        this.pendingCheckForTap = null;
        this.links = new ArrayList<>();
        this.linkLayout = new ArrayList<>();
        this.linkSpoilers = new SparseArray<>();
        this.descriptionLayoutSpoilers = new ArrayList();
        this.descriptionLayout2Spoilers = new ArrayList();
        this.spoilersPool = new Stack<>();
        this.path = new Path();
        this.spoilerTypePressed = -1;
        this.titleY = AndroidUtilities.dp(10.0f);
        this.descriptionY = AndroidUtilities.dp(30.0f);
        this.patchedDescriptionLayout = new AtomicReference<>();
        this.description2Y = AndroidUtilities.dp(30.0f);
        this.patchedDescriptionLayout2 = new AtomicReference<>();
        this.captionY = AndroidUtilities.dp(30.0f);
        this.fromInfoLayoutY = AndroidUtilities.dp(30.0f);
        this.viewType = i;
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
        CheckBox2 checkBox22 = this.checkBox;
        boolean z = LocaleController.isRTL;
        addView(checkBox22, LayoutHelper.createFrame(24, 24.0f, (z ? 5 : 3) | 48, z ? 0.0f : 44.0f, 44.0f, z ? 44.0f : 0.0f, 0.0f));
        if (i == 1) {
            TextPaint textPaint2 = new TextPaint(1);
            this.description2TextPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
        }
        TextPaint textPaint3 = new TextPaint(1);
        this.captionTextPaint = textPaint3;
        textPaint3.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0252 A[Catch:{ Exception -> 0x029d }] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x02ba  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x02cf  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0305  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x030a A[SYNTHETIC, Splitter:B:145:0x030a] */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x035e  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0363  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x036f  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x0372  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0377 A[SYNTHETIC, Splitter:B:166:0x0377] */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x03c3 A[SYNTHETIC, Splitter:B:177:0x03c3] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0476  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x047b  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x049b  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x056a  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0575  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x058e  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0612  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x062e  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x0644  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x069e  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x06ac  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x06cd  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01fd  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r27, int r28) {
        /*
            r26 = this;
            r1 = r26
            r2 = 0
            r1.drawLinkImageView = r2
            r3 = 0
            r1.descriptionLayout = r3
            r1.titleLayout = r3
            r1.descriptionLayout2 = r3
            r1.captionLayout = r3
            java.util.ArrayList<android.text.StaticLayout> r0 = r1.linkLayout
            r0.clear()
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links
            r0.clear()
            int r0 = android.view.View.MeasureSpec.getSize(r27)
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r4 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r0 - r5
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r14 = 1
            if (r7 == 0) goto L_0x0061
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_webPage
            if (r7 == 0) goto L_0x0061
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r0.photoThumbs
            if (r7 != 0) goto L_0x0048
            org.telegram.tgnet.TLRPC$Photo r7 = r6.photo
            if (r7 == 0) goto L_0x0048
            r0.generateThumbs(r14)
        L_0x0048:
            org.telegram.tgnet.TLRPC$Photo r0 = r6.photo
            if (r0 == 0) goto L_0x0054
            org.telegram.messenger.MessageObject r0 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.photoThumbs
            if (r0 == 0) goto L_0x0054
            r0 = 1
            goto L_0x0055
        L_0x0054:
            r0 = 0
        L_0x0055:
            java.lang.String r7 = r6.title
            if (r7 != 0) goto L_0x005b
            java.lang.String r7 = r6.site_name
        L_0x005b:
            java.lang.String r8 = r6.description
            java.lang.String r6 = r6.url
            r15 = r0
            goto L_0x0065
        L_0x0061:
            r6 = r3
            r7 = r6
            r8 = r7
            r15 = 0
        L_0x0065:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x02ba
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x02ba
            r0 = r3
            r9 = 0
        L_0x0075:
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            int r10 = r10.size()
            if (r9 >= r10) goto L_0x02b8
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            java.lang.Object r10 = r10.get(r9)
            org.telegram.tgnet.TLRPC$MessageEntity r10 = (org.telegram.tgnet.TLRPC$MessageEntity) r10
            int r11 = r10.length
            if (r11 <= 0) goto L_0x02af
            int r11 = r10.offset
            if (r11 < 0) goto L_0x02af
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            java.lang.String r12 = r12.message
            int r12 = r12.length()
            if (r11 < r12) goto L_0x00a3
            goto L_0x02af
        L_0x00a3:
            int r11 = r10.offset
            int r12 = r10.length
            int r11 = r11 + r12
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            java.lang.String r12 = r12.message
            int r12 = r12.length()
            if (r11 <= r12) goto L_0x00c3
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            int r12 = r10.offset
            int r11 = r11 - r12
            r10.length = r11
        L_0x00c3:
            if (r9 != 0) goto L_0x0106
            if (r6 == 0) goto L_0x0106
            int r11 = r10.offset
            if (r11 != 0) goto L_0x00d9
            int r11 = r10.length
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            java.lang.String r12 = r12.message
            int r12 = r12.length()
            if (r11 == r12) goto L_0x0106
        L_0x00d9:
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r11 = r11.entities
            int r11 = r11.size()
            if (r11 != r14) goto L_0x00f7
            if (r8 != 0) goto L_0x0106
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r11, (android.text.Spannable) r0)
            goto L_0x0106
        L_0x00f7:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r11, (android.text.Spannable) r0)
        L_0x0106:
            r11 = r0
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl     // Catch:{ Exception -> 0x02aa }
            if (r0 != 0) goto L_0x0172
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl     // Catch:{ Exception -> 0x02aa }
            if (r0 == 0) goto L_0x0110
            goto L_0x0172
        L_0x0110:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityEmail     // Catch:{ Exception -> 0x02aa }
            if (r0 == 0) goto L_0x016f
            if (r7 == 0) goto L_0x011c
            int r0 = r7.length()     // Catch:{ Exception -> 0x02aa }
            if (r0 != 0) goto L_0x016f
        L_0x011c:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02aa }
            r0.<init>()     // Catch:{ Exception -> 0x02aa }
            java.lang.String r12 = "mailto:"
            r0.append(r12)     // Catch:{ Exception -> 0x02aa }
            org.telegram.messenger.MessageObject r12 = r1.message     // Catch:{ Exception -> 0x02aa }
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner     // Catch:{ Exception -> 0x02aa }
            java.lang.String r12 = r12.message     // Catch:{ Exception -> 0x02aa }
            int r13 = r10.offset     // Catch:{ Exception -> 0x02aa }
            int r3 = r10.length     // Catch:{ Exception -> 0x02aa }
            int r3 = r3 + r13
            java.lang.String r3 = r12.substring(r13, r3)     // Catch:{ Exception -> 0x02aa }
            r0.append(r3)     // Catch:{ Exception -> 0x02aa }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x02aa }
            org.telegram.messenger.MessageObject r3 = r1.message     // Catch:{ Exception -> 0x02aa }
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner     // Catch:{ Exception -> 0x02aa }
            java.lang.String r3 = r3.message     // Catch:{ Exception -> 0x02aa }
            int r12 = r10.offset     // Catch:{ Exception -> 0x02aa }
            int r13 = r10.length     // Catch:{ Exception -> 0x02aa }
            int r13 = r13 + r12
            java.lang.String r3 = r3.substring(r12, r13)     // Catch:{ Exception -> 0x02aa }
            int r7 = r10.offset     // Catch:{ Exception -> 0x02a7 }
            if (r7 != 0) goto L_0x015d
            int r7 = r10.length     // Catch:{ Exception -> 0x02a7 }
            org.telegram.messenger.MessageObject r12 = r1.message     // Catch:{ Exception -> 0x02a7 }
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner     // Catch:{ Exception -> 0x02a7 }
            java.lang.String r12 = r12.message     // Catch:{ Exception -> 0x02a7 }
            int r12 = r12.length()     // Catch:{ Exception -> 0x02a7 }
            if (r7 == r12) goto L_0x01fa
        L_0x015d:
            org.telegram.messenger.MessageObject r7 = r1.message     // Catch:{ Exception -> 0x02a7 }
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner     // Catch:{ Exception -> 0x02a7 }
            java.lang.String r7 = r7.message     // Catch:{ Exception -> 0x02a7 }
            android.text.SpannableStringBuilder r7 = android.text.SpannableStringBuilder.valueOf(r7)     // Catch:{ Exception -> 0x02a7 }
            org.telegram.messenger.MessageObject r12 = r1.message     // Catch:{ Exception -> 0x02a7 }
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r12, (android.text.Spannable) r7)     // Catch:{ Exception -> 0x02a7 }
        L_0x016c:
            r8 = r7
            goto L_0x01fa
        L_0x016f:
            r0 = 0
            goto L_0x01fb
        L_0x0172:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl     // Catch:{ Exception -> 0x02aa }
            if (r0 == 0) goto L_0x0186
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x02aa }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x02aa }
            java.lang.String r0 = r0.message     // Catch:{ Exception -> 0x02aa }
            int r3 = r10.offset     // Catch:{ Exception -> 0x02aa }
            int r12 = r10.length     // Catch:{ Exception -> 0x02aa }
            int r12 = r12 + r3
            java.lang.String r0 = r0.substring(r3, r12)     // Catch:{ Exception -> 0x02aa }
            goto L_0x0188
        L_0x0186:
            java.lang.String r0 = r10.url     // Catch:{ Exception -> 0x02aa }
        L_0x0188:
            if (r7 == 0) goto L_0x0190
            int r3 = r7.length()     // Catch:{ Exception -> 0x02aa }
            if (r3 != 0) goto L_0x01fb
        L_0x0190:
            java.lang.String r3 = r0.toString()     // Catch:{ Exception -> 0x02aa }
            android.net.Uri r7 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x02a7 }
            java.lang.String r3 = r7.getHost()     // Catch:{ Exception -> 0x02a7 }
            if (r3 != 0) goto L_0x01a2
            java.lang.String r3 = r0.toString()     // Catch:{ Exception -> 0x02a7 }
        L_0x01a2:
            if (r3 == 0) goto L_0x01d7
            r7 = 46
            int r12 = r3.lastIndexOf(r7)     // Catch:{ Exception -> 0x02a7 }
            if (r12 < 0) goto L_0x01d7
            java.lang.String r3 = r3.substring(r2, r12)     // Catch:{ Exception -> 0x02a7 }
            int r7 = r3.lastIndexOf(r7)     // Catch:{ Exception -> 0x02a7 }
            if (r7 < 0) goto L_0x01bc
            int r7 = r7 + 1
            java.lang.String r3 = r3.substring(r7)     // Catch:{ Exception -> 0x02a7 }
        L_0x01bc:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02a7 }
            r7.<init>()     // Catch:{ Exception -> 0x02a7 }
            java.lang.String r12 = r3.substring(r2, r14)     // Catch:{ Exception -> 0x02a7 }
            java.lang.String r12 = r12.toUpperCase()     // Catch:{ Exception -> 0x02a7 }
            r7.append(r12)     // Catch:{ Exception -> 0x02a7 }
            java.lang.String r12 = r3.substring(r14)     // Catch:{ Exception -> 0x02a7 }
            r7.append(r12)     // Catch:{ Exception -> 0x02a7 }
            java.lang.String r3 = r7.toString()     // Catch:{ Exception -> 0x02a7 }
        L_0x01d7:
            int r7 = r10.offset     // Catch:{ Exception -> 0x02a7 }
            if (r7 != 0) goto L_0x01e9
            int r7 = r10.length     // Catch:{ Exception -> 0x02a7 }
            org.telegram.messenger.MessageObject r12 = r1.message     // Catch:{ Exception -> 0x02a7 }
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner     // Catch:{ Exception -> 0x02a7 }
            java.lang.String r12 = r12.message     // Catch:{ Exception -> 0x02a7 }
            int r12 = r12.length()     // Catch:{ Exception -> 0x02a7 }
            if (r7 == r12) goto L_0x01fa
        L_0x01e9:
            org.telegram.messenger.MessageObject r7 = r1.message     // Catch:{ Exception -> 0x02a7 }
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner     // Catch:{ Exception -> 0x02a7 }
            java.lang.String r7 = r7.message     // Catch:{ Exception -> 0x02a7 }
            android.text.SpannableStringBuilder r7 = android.text.SpannableStringBuilder.valueOf(r7)     // Catch:{ Exception -> 0x02a7 }
            org.telegram.messenger.MessageObject r12 = r1.message     // Catch:{ Exception -> 0x02a7 }
            org.telegram.messenger.MediaDataController.addTextStyleRuns((org.telegram.messenger.MessageObject) r12, (android.text.Spannable) r7)     // Catch:{ Exception -> 0x02a7 }
            goto L_0x016c
        L_0x01fa:
            r7 = r3
        L_0x01fb:
            if (r0 == 0) goto L_0x02a1
            java.lang.String r3 = "://"
            boolean r3 = org.telegram.messenger.AndroidUtilities.charSequenceContains(r0, r3)     // Catch:{ Exception -> 0x029d }
            if (r3 != 0) goto L_0x0238
            java.lang.String r3 = r0.toString()     // Catch:{ Exception -> 0x02aa }
            java.lang.String r3 = r3.toLowerCase()     // Catch:{ Exception -> 0x02aa }
            java.lang.String r12 = "http"
            int r3 = r3.indexOf(r12)     // Catch:{ Exception -> 0x02aa }
            if (r3 == 0) goto L_0x0238
            java.lang.String r3 = r0.toString()     // Catch:{ Exception -> 0x02aa }
            java.lang.String r3 = r3.toLowerCase()     // Catch:{ Exception -> 0x02aa }
            java.lang.String r12 = "mailto"
            int r3 = r3.indexOf(r12)     // Catch:{ Exception -> 0x02aa }
            if (r3 == 0) goto L_0x0238
            java.lang.String r3 = "http://"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02aa }
            r12.<init>()     // Catch:{ Exception -> 0x02aa }
            r12.append(r3)     // Catch:{ Exception -> 0x02aa }
            r12.append(r0)     // Catch:{ Exception -> 0x02aa }
            java.lang.String r0 = r12.toString()     // Catch:{ Exception -> 0x02aa }
            r3 = 7
            goto L_0x0239
        L_0x0238:
            r3 = 0
        L_0x0239:
            android.text.SpannableString r0 = android.text.SpannableString.valueOf(r0)     // Catch:{ Exception -> 0x029d }
            int r12 = r10.offset     // Catch:{ Exception -> 0x029d }
            int r10 = r10.length     // Catch:{ Exception -> 0x029d }
            int r10 = r10 + r12
            org.telegram.messenger.MessageObject r13 = r1.message     // Catch:{ Exception -> 0x029d }
            org.telegram.tgnet.TLRPC$Message r13 = r13.messageOwner     // Catch:{ Exception -> 0x029d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r13 = r13.entities     // Catch:{ Exception -> 0x029d }
            java.util.Iterator r13 = r13.iterator()     // Catch:{ Exception -> 0x029d }
        L_0x024c:
            boolean r16 = r13.hasNext()     // Catch:{ Exception -> 0x029d }
            if (r16 == 0) goto L_0x0291
            java.lang.Object r16 = r13.next()     // Catch:{ Exception -> 0x029d }
            r2 = r16
            org.telegram.tgnet.TLRPC$MessageEntity r2 = (org.telegram.tgnet.TLRPC$MessageEntity) r2     // Catch:{ Exception -> 0x029d }
            int r4 = r2.offset     // Catch:{ Exception -> 0x029d }
            int r14 = r2.length     // Catch:{ Exception -> 0x029d }
            int r14 = r14 + r4
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler     // Catch:{ Exception -> 0x029d }
            if (r2 == 0) goto L_0x0288
            if (r12 > r14) goto L_0x0288
            if (r10 < r4) goto L_0x0288
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r2 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x029d }
            r2.<init>()     // Catch:{ Exception -> 0x029d }
            r17 = r7
            int r7 = r2.flags     // Catch:{ Exception -> 0x0299 }
            r7 = r7 | 256(0x100, float:3.59E-43)
            r2.flags = r7     // Catch:{ Exception -> 0x0299 }
            org.telegram.ui.Components.TextStyleSpan r7 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x0299 }
            r7.<init>(r2)     // Catch:{ Exception -> 0x0299 }
            int r2 = java.lang.Math.max(r12, r4)     // Catch:{ Exception -> 0x0299 }
            int r4 = java.lang.Math.min(r10, r14)     // Catch:{ Exception -> 0x0299 }
            int r4 = r4 + r3
            r14 = 33
            r0.setSpan(r7, r2, r4, r14)     // Catch:{ Exception -> 0x0299 }
            goto L_0x028a
        L_0x0288:
            r17 = r7
        L_0x028a:
            r7 = r17
            r2 = 0
            r4 = 1090519040(0x41000000, float:8.0)
            r14 = 1
            goto L_0x024c
        L_0x0291:
            r17 = r7
            java.util.ArrayList<java.lang.CharSequence> r2 = r1.links     // Catch:{ Exception -> 0x0299 }
            r2.add(r0)     // Catch:{ Exception -> 0x0299 }
            goto L_0x02a3
        L_0x0299:
            r0 = move-exception
            r7 = r17
            goto L_0x02ab
        L_0x029d:
            r0 = move-exception
            r17 = r7
            goto L_0x02ab
        L_0x02a1:
            r17 = r7
        L_0x02a3:
            r0 = r11
            r7 = r17
            goto L_0x02af
        L_0x02a7:
            r0 = move-exception
            r7 = r3
            goto L_0x02ab
        L_0x02aa:
            r0 = move-exception
        L_0x02ab:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r11
        L_0x02af:
            int r9 = r9 + 1
            r2 = 0
            r3 = 0
            r4 = 1090519040(0x41000000, float:8.0)
            r14 = 1
            goto L_0x0075
        L_0x02b8:
            r2 = r0
            goto L_0x02bb
        L_0x02ba:
            r2 = 0
        L_0x02bb:
            if (r6 == 0) goto L_0x02ca
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x02ca
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links
            r0.add(r6)
        L_0x02ca:
            int r0 = r1.viewType
            r3 = 1
            if (r0 != r3) goto L_0x0305
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            int r0 = r0.date
            long r3 = (long) r0
            java.lang.String r9 = org.telegram.messenger.LocaleController.stringForMessageListDate(r3)
            android.text.TextPaint r0 = r1.description2TextPaint
            float r0 = r0.measureText(r9)
            double r3 = (double) r0
            double r3 = java.lang.Math.ceil(r3)
            int r0 = (int) r3
            android.text.TextPaint r10 = r1.description2TextPaint
            r13 = 0
            r14 = 1
            r11 = r0
            r12 = r0
            android.text.StaticLayout r3 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r9, r10, r11, r12, r13, r14)
            r1.dateLayout = r3
            int r3 = r5 - r0
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            r1.dateLayoutX = r3
            r3 = 1094713344(0x41400000, float:12.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 + r3
            goto L_0x0306
        L_0x0305:
            r0 = 0
        L_0x0306:
            r3 = 1082130432(0x40800000, float:4.0)
            if (r7 == 0) goto L_0x0356
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x034d }
            java.util.ArrayList<java.lang.String> r4 = r4.highlightedWords     // Catch:{ Exception -> 0x034d }
            r6 = 0
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r7, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)     // Catch:{ Exception -> 0x034d }
            if (r4 == 0) goto L_0x0317
            r9 = r4
            goto L_0x0318
        L_0x0317:
            r9 = r7
        L_0x0318:
            android.text.TextPaint r10 = r1.titleTextPaint     // Catch:{ Exception -> 0x034d }
            int r0 = r5 - r0
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x034d }
            int r11 = r0 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x034d }
            int r12 = r0 - r4
            r13 = 0
            r14 = 3
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x034d }
            r1.titleLayout = r0     // Catch:{ Exception -> 0x034d }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x034d }
            if (r0 <= 0) goto L_0x0351
            int r0 = r1.titleY     // Catch:{ Exception -> 0x034d }
            android.text.StaticLayout r4 = r1.titleLayout     // Catch:{ Exception -> 0x034d }
            int r6 = r4.getLineCount()     // Catch:{ Exception -> 0x034d }
            r9 = 1
            int r6 = r6 - r9
            int r4 = r4.getLineBottom(r6)     // Catch:{ Exception -> 0x034d }
            int r0 = r0 + r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x034d }
            int r0 = r0 + r4
            r1.descriptionY = r0     // Catch:{ Exception -> 0x034d }
            goto L_0x0351
        L_0x034d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0351:
            org.telegram.ui.Components.LetterDrawable r0 = r1.letterDrawable
            r0.setTitle(r7)
        L_0x0356:
            int r0 = r1.descriptionY
            r1.description2Y = r0
            android.text.StaticLayout r0 = r1.titleLayout
            if (r0 == 0) goto L_0x0363
            int r0 = r0.getLineCount()
            goto L_0x0364
        L_0x0363:
            r0 = 0
        L_0x0364:
            int r0 = 4 - r0
            r4 = 1
            int r14 = java.lang.Math.max(r4, r0)
            int r0 = r1.viewType
            if (r0 != r4) goto L_0x0372
            r2 = 0
            r6 = 0
            goto L_0x0373
        L_0x0372:
            r6 = r8
        L_0x0373:
            r4 = 1084227584(0x40a00000, float:5.0)
            if (r6 == 0) goto L_0x03bf
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x03bb }
            r10 = 0
            r8 = r5
            r9 = r5
            r11 = r14
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x03bb }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x03bb }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x03bb }
            if (r0 <= 0) goto L_0x039f
            int r0 = r1.descriptionY     // Catch:{ Exception -> 0x03bb }
            android.text.StaticLayout r6 = r1.descriptionLayout     // Catch:{ Exception -> 0x03bb }
            int r7 = r6.getLineCount()     // Catch:{ Exception -> 0x03bb }
            r8 = 1
            int r7 = r7 - r8
            int r6 = r6.getLineBottom(r7)     // Catch:{ Exception -> 0x03bb }
            int r0 = r0 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x03bb }
            int r0 = r0 + r6
            r1.description2Y = r0     // Catch:{ Exception -> 0x03bb }
        L_0x039f:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilersPool     // Catch:{ Exception -> 0x03bb }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r6 = r1.descriptionLayoutSpoilers     // Catch:{ Exception -> 0x03bb }
            r0.addAll(r6)     // Catch:{ Exception -> 0x03bb }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.descriptionLayoutSpoilers     // Catch:{ Exception -> 0x03bb }
            r0.clear()     // Catch:{ Exception -> 0x03bb }
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x03bb }
            boolean r0 = r0.isSpoilersRevealed     // Catch:{ Exception -> 0x03bb }
            if (r0 != 0) goto L_0x03bf
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x03bb }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r6 = r1.spoilersPool     // Catch:{ Exception -> 0x03bb }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r7 = r1.descriptionLayoutSpoilers     // Catch:{ Exception -> 0x03bb }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r0, r6, r7)     // Catch:{ Exception -> 0x03bb }
            goto L_0x03bf
        L_0x03bb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03bf:
            r16 = 1092616192(0x41200000, float:10.0)
            if (r2 == 0) goto L_0x03fd
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x03f9 }
            r10 = 0
            r6 = r2
            r8 = r5
            r9 = r5
            r11 = r14
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x03f9 }
            r1.descriptionLayout2 = r0     // Catch:{ Exception -> 0x03f9 }
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x03f9 }
            if (r0 == 0) goto L_0x03dd
            int r0 = r1.description2Y     // Catch:{ Exception -> 0x03f9 }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x03f9 }
            int r0 = r0 + r2
            r1.description2Y = r0     // Catch:{ Exception -> 0x03f9 }
        L_0x03dd:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilersPool     // Catch:{ Exception -> 0x03f9 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.descriptionLayout2Spoilers     // Catch:{ Exception -> 0x03f9 }
            r0.addAll(r2)     // Catch:{ Exception -> 0x03f9 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.descriptionLayout2Spoilers     // Catch:{ Exception -> 0x03f9 }
            r0.clear()     // Catch:{ Exception -> 0x03f9 }
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x03f9 }
            boolean r0 = r0.isSpoilersRevealed     // Catch:{ Exception -> 0x03f9 }
            if (r0 != 0) goto L_0x03fd
            android.text.StaticLayout r0 = r1.descriptionLayout2     // Catch:{ Exception -> 0x03f9 }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilersPool     // Catch:{ Exception -> 0x03f9 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r6 = r1.descriptionLayout2Spoilers     // Catch:{ Exception -> 0x03f9 }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r0, r2, r6)     // Catch:{ Exception -> 0x03f9 }
            goto L_0x03fd
        L_0x03f9:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03fd:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x0476
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0476
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            java.lang.String r2 = "\n"
            java.lang.String r6 = " "
            java.lang.String r0 = r0.replace(r2, r6)
            java.lang.String r2 = " +"
            java.lang.String r0 = r0.replaceAll(r2, r6)
            java.lang.String r0 = r0.trim()
            android.text.TextPaint r2 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r2 = r2.getFontMetricsInt()
            r6 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r7 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r6, r7)
            org.telegram.messenger.MessageObject r2 = r1.message
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            r13 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r13)
            if (r0 == 0) goto L_0x0477
            org.telegram.messenger.MessageObject r2 = r1.message
            java.util.ArrayList<java.lang.String> r2 = r2.highlightedWords
            java.lang.Object r2 = r2.get(r7)
            java.lang.String r2 = (java.lang.String) r2
            android.text.TextPaint r6 = r1.captionTextPaint
            r7 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r2, r5, r6, r7)
            android.text.TextPaint r2 = r1.captionTextPaint
            float r6 = (float) r5
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r18 = android.text.TextUtils.ellipsize(r0, r2, r6, r7)
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r2 = r1.captionTextPaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r20 = r5 + r6
            android.text.Layout$Alignment r21 = android.text.Layout.Alignment.ALIGN_NORMAL
            r22 = 1065353216(0x3var_, float:1.0)
            r23 = 0
            r24 = 0
            r17 = r0
            r19 = r2
            r17.<init>(r18, r19, r20, r21, r22, r23, r24)
            r1.captionLayout = r0
            goto L_0x0477
        L_0x0476:
            r13 = 0
        L_0x0477:
            android.text.StaticLayout r0 = r1.captionLayout
            if (r0 == 0) goto L_0x0493
            int r2 = r1.descriptionY
            r1.captionY = r2
            int r6 = r0.getLineCount()
            r7 = 1
            int r6 = r6 - r7
            int r0 = r0.getLineBottom(r6)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r6
            int r2 = r2 + r0
            r1.descriptionY = r2
            r1.description2Y = r2
        L_0x0493:
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x055e
            r0 = 0
        L_0x049c:
            android.util.SparseArray<java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect>> r2 = r1.linkSpoilers
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x04b4
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r2 = r1.spoilersPool
            android.util.SparseArray<java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect>> r6 = r1.linkSpoilers
            java.lang.Object r6 = r6.get(r0)
            java.util.Collection r6 = (java.util.Collection) r6
            r2.addAll(r6)
            int r0 = r0 + 1
            goto L_0x049c
        L_0x04b4:
            android.util.SparseArray<java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect>> r0 = r1.linkSpoilers
            r0.clear()
            r2 = 0
        L_0x04ba:
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links
            int r0 = r0.size()
            if (r2 >= r0) goto L_0x055e
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links     // Catch:{ Exception -> 0x0550 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ Exception -> 0x0550 }
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0     // Catch:{ Exception -> 0x0550 }
            android.text.TextPaint r6 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0550 }
            int r7 = r0.length()     // Catch:{ Exception -> 0x0550 }
            r8 = 0
            float r6 = r6.measureText(r0, r8, r7)     // Catch:{ Exception -> 0x0550 }
            double r6 = (double) r6     // Catch:{ Exception -> 0x0550 }
            double r6 = java.lang.Math.ceil(r6)     // Catch:{ Exception -> 0x0550 }
            int r6 = (int) r6     // Catch:{ Exception -> 0x0550 }
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)     // Catch:{ Exception -> 0x0550 }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r0)     // Catch:{ Exception -> 0x0550 }
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0550 }
            int r6 = java.lang.Math.min(r6, r5)     // Catch:{ Exception -> 0x0550 }
            float r6 = (float) r6     // Catch:{ Exception -> 0x0550 }
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x0550 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r0, r7, r6, r8)     // Catch:{ Exception -> 0x0550 }
            android.text.StaticLayout r12 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0550 }
            android.text.TextPaint r8 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0550 }
            android.text.Layout$Alignment r10 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0550 }
            r11 = 1065353216(0x3var_, float:1.0)
            r17 = 0
            r18 = 0
            r6 = r12
            r7 = r0
            r9 = r5
            r3 = r12
            r12 = r17
            r17 = r13
            r13 = r18
            r6.<init>(r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x054e }
            int r6 = r1.description2Y     // Catch:{ Exception -> 0x054e }
            r1.linkY = r6     // Catch:{ Exception -> 0x054e }
            android.text.StaticLayout r6 = r1.descriptionLayout2     // Catch:{ Exception -> 0x054e }
            if (r6 == 0) goto L_0x052d
            int r6 = r6.getLineCount()     // Catch:{ Exception -> 0x054e }
            if (r6 == 0) goto L_0x052d
            int r6 = r1.linkY     // Catch:{ Exception -> 0x054e }
            android.text.StaticLayout r7 = r1.descriptionLayout2     // Catch:{ Exception -> 0x054e }
            int r8 = r7.getLineCount()     // Catch:{ Exception -> 0x054e }
            r9 = 1
            int r8 = r8 - r9
            int r7 = r7.getLineBottom(r8)     // Catch:{ Exception -> 0x054e }
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x054e }
            int r7 = r7 + r8
            int r6 = r6 + r7
            r1.linkY = r6     // Catch:{ Exception -> 0x054e }
        L_0x052d:
            org.telegram.messenger.MessageObject r6 = r1.message     // Catch:{ Exception -> 0x054e }
            boolean r6 = r6.isSpoilersRevealed     // Catch:{ Exception -> 0x054e }
            if (r6 != 0) goto L_0x0548
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Exception -> 0x054e }
            r6.<init>()     // Catch:{ Exception -> 0x054e }
            boolean r7 = r0 instanceof android.text.Spannable     // Catch:{ Exception -> 0x054e }
            if (r7 == 0) goto L_0x0543
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x054e }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r7 = r1.spoilersPool     // Catch:{ Exception -> 0x054e }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r3, r0, r7, r6)     // Catch:{ Exception -> 0x054e }
        L_0x0543:
            android.util.SparseArray<java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect>> r0 = r1.linkSpoilers     // Catch:{ Exception -> 0x054e }
            r0.put(r2, r6)     // Catch:{ Exception -> 0x054e }
        L_0x0548:
            java.util.ArrayList<android.text.StaticLayout> r0 = r1.linkLayout     // Catch:{ Exception -> 0x054e }
            r0.add(r3)     // Catch:{ Exception -> 0x054e }
            goto L_0x0556
        L_0x054e:
            r0 = move-exception
            goto L_0x0553
        L_0x0550:
            r0 = move-exception
            r17 = r13
        L_0x0553:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0556:
            int r2 = r2 + 1
            r13 = r17
            r3 = 1082130432(0x40800000, float:4.0)
            goto L_0x04ba
        L_0x055e:
            r17 = r13
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0575
            int r2 = android.view.View.MeasureSpec.getSize(r27)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r2 = r2 - r3
            int r2 = r2 - r0
            goto L_0x0579
        L_0x0575:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
        L_0x0579:
            org.telegram.ui.Components.LetterDrawable r3 = r1.letterDrawable
            r6 = 1093664768(0x41300000, float:11.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r8 = r2 + r0
            r9 = 1115422720(0x427CLASSNAME, float:63.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r3.setBounds(r2, r7, r8, r9)
            if (r15 == 0) goto L_0x060d
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.photoThumbs
            r7 = 1
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r0, r7)
            org.telegram.messenger.MessageObject r7 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r7.photoThumbs
            r8 = 80
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
            if (r7 != r3) goto L_0x05a5
            r7 = r17
        L_0x05a5:
            r8 = -1
            r3.size = r8
            if (r7 == 0) goto L_0x05ac
            r7.size = r8
        L_0x05ac:
            org.telegram.messenger.ImageReceiver r8 = r1.linkImageView
            float r2 = (float) r2
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r9 = (float) r0
            r8.setImageCoords(r2, r6, r9, r9)
            org.telegram.messenger.FileLoader.getAttachFileName(r3)
            java.util.Locale r2 = java.util.Locale.US
            r6 = 2
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r10 = 0
            r8[r10] = r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r11 = 1
            r8[r11] = r9
            java.lang.String r9 = "%d_%d"
            java.lang.String r19 = java.lang.String.format(r2, r9, r8)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Integer r8 = java.lang.Integer.valueOf(r0)
            r6[r10] = r8
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r6[r11] = r0
            java.lang.String r0 = "%d_%d_b"
            java.lang.String r21 = java.lang.String.format(r2, r0, r6)
            org.telegram.messenger.ImageReceiver r0 = r1.linkImageView
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLObject r2 = r2.photoThumbsObject
            org.telegram.messenger.ImageLocation r18 = org.telegram.messenger.ImageLocation.getForObject(r3, r2)
            org.telegram.messenger.MessageObject r2 = r1.message
            org.telegram.tgnet.TLObject r2 = r2.photoThumbsObject
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForObject(r7, r2)
            r22 = 0
            r23 = 0
            org.telegram.messenger.MessageObject r2 = r1.message
            r25 = 0
            r17 = r0
            r24 = r2
            r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25)
            r2 = 1
            r1.drawLinkImageView = r2
            goto L_0x060e
        L_0x060d:
            r2 = 1
        L_0x060e:
            int r0 = r1.viewType
            if (r0 != r2) goto L_0x0624
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r6 = org.telegram.ui.FilteredSearchView.createFromInfoString(r0)
            android.text.TextPaint r7 = r1.description2TextPaint
            r10 = 0
            r8 = r5
            r9 = r5
            r11 = r14
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)
            r1.fromInfoLayout = r0
        L_0x0624:
            android.text.StaticLayout r0 = r1.titleLayout
            if (r0 == 0) goto L_0x0644
            int r0 = r0.getLineCount()
            if (r0 == 0) goto L_0x0644
            android.text.StaticLayout r0 = r1.titleLayout
            int r2 = r0.getLineCount()
            r3 = 1
            int r2 = r2 - r3
            int r0 = r0.getLineBottom(r2)
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            r7 = 0
            int r0 = r0 + r7
            goto L_0x0646
        L_0x0644:
            r7 = 0
            r0 = 0
        L_0x0646:
            android.text.StaticLayout r2 = r1.captionLayout
            if (r2 == 0) goto L_0x0662
            int r2 = r2.getLineCount()
            if (r2 == 0) goto L_0x0662
            android.text.StaticLayout r2 = r1.captionLayout
            int r3 = r2.getLineCount()
            r5 = 1
            int r3 = r3 - r5
            int r2 = r2.getLineBottom(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r3
            int r0 = r0 + r2
        L_0x0662:
            android.text.StaticLayout r2 = r1.descriptionLayout
            if (r2 == 0) goto L_0x067e
            int r2 = r2.getLineCount()
            if (r2 == 0) goto L_0x067e
            android.text.StaticLayout r2 = r1.descriptionLayout
            int r3 = r2.getLineCount()
            r5 = 1
            int r3 = r3 - r5
            int r2 = r2.getLineBottom(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r3
            int r0 = r0 + r2
        L_0x067e:
            android.text.StaticLayout r2 = r1.descriptionLayout2
            if (r2 == 0) goto L_0x06a3
            int r2 = r2.getLineCount()
            if (r2 == 0) goto L_0x06a3
            android.text.StaticLayout r2 = r1.descriptionLayout2
            int r3 = r2.getLineCount()
            r5 = 1
            int r3 = r3 - r5
            int r2 = r2.getLineBottom(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r3
            int r0 = r0 + r2
            android.text.StaticLayout r2 = r1.descriptionLayout
            if (r2 == 0) goto L_0x06a3
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r0 = r0 + r2
        L_0x06a3:
            r2 = 0
        L_0x06a4:
            java.util.ArrayList<android.text.StaticLayout> r3 = r1.linkLayout
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x06c8
            java.util.ArrayList<android.text.StaticLayout> r3 = r1.linkLayout
            java.lang.Object r3 = r3.get(r2)
            android.text.StaticLayout r3 = (android.text.StaticLayout) r3
            int r5 = r3.getLineCount()
            if (r5 <= 0) goto L_0x06c5
            int r5 = r3.getLineCount()
            r6 = 1
            int r5 = r5 - r6
            int r3 = r3.getLineBottom(r5)
            int r7 = r7 + r3
        L_0x06c5:
            int r2 = r2 + 1
            goto L_0x06a4
        L_0x06c8:
            int r0 = r0 + r7
            android.text.StaticLayout r2 = r1.fromInfoLayout
            if (r2 == 0) goto L_0x06e9
            int r2 = r1.linkY
            int r2 = r2 + r7
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r3
            r1.fromInfoLayoutY = r2
            android.text.StaticLayout r2 = r1.fromInfoLayout
            int r3 = r2.getLineCount()
            r5 = 1
            int r3 = r3 - r5
            int r2 = r2.getLineBottom(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 + r3
            int r0 = r0 + r2
        L_0x06e9:
            org.telegram.ui.Components.CheckBox2 r2 = r1.checkBox
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r5 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r5)
            r2.measure(r4, r3)
            int r2 = android.view.View.MeasureSpec.getSize(r27)
            r3 = 1117257728(0x42980000, float:76.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 1099431936(0x41880000, float:17.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r4
            int r0 = java.lang.Math.max(r3, r0)
            boolean r3 = r1.needDivider
            int r0 = r0 + r3
            r1.setMeasuredDimension(r2, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedLinkCell.onMeasure(int, int):void");
    }

    public void setLink(MessageObject messageObject, boolean z) {
        this.needDivider = z;
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

    /* JADX WARNING: Removed duplicated region for block: B:122:0x01f4  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0204 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:139:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        /*
            r14 = this;
            org.telegram.messenger.MessageObject r0 = r14.message
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x01f8
            java.util.ArrayList<android.text.StaticLayout> r0 = r14.linkLayout
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01f8
            org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r0 = r14.delegate
            if (r0 == 0) goto L_0x01f8
            boolean r0 = r0.canPerformActions()
            if (r0 == 0) goto L_0x01f8
            int r0 = r15.getAction()
            if (r0 == 0) goto L_0x0039
            boolean r0 = r14.linkPreviewPressed
            if (r0 != 0) goto L_0x0026
            org.telegram.ui.Components.spoilers.SpoilerEffect r0 = r14.spoilerPressed
            if (r0 == 0) goto L_0x002d
        L_0x0026:
            int r0 = r15.getAction()
            if (r0 != r2) goto L_0x002d
            goto L_0x0039
        L_0x002d:
            int r0 = r15.getAction()
            r3 = 3
            if (r0 != r3) goto L_0x01fb
            r14.resetPressedLink()
            goto L_0x01fb
        L_0x0039:
            float r0 = r15.getX()
            int r0 = (int) r0
            float r3 = r15.getY()
            int r3 = (int) r3
            r4 = 0
            r5 = 0
        L_0x0045:
            java.util.ArrayList<android.text.StaticLayout> r6 = r14.linkLayout
            int r6 = r6.size()
            r7 = 1090519040(0x41000000, float:8.0)
            if (r4 >= r6) goto L_0x014b
            java.util.ArrayList<android.text.StaticLayout> r6 = r14.linkLayout
            java.lang.Object r6 = r6.get(r4)
            android.text.StaticLayout r6 = (android.text.StaticLayout) r6
            int r8 = r6.getLineCount()
            if (r8 <= 0) goto L_0x0147
            int r8 = r6.getLineCount()
            int r8 = r8 - r2
            int r8 = r6.getLineBottom(r8)
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x006d
            r9 = 1090519040(0x41000000, float:8.0)
            goto L_0x0070
        L_0x006d:
            int r9 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r9 = (float) r9
        L_0x0070:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r10 = (float) r0
            float r11 = (float) r9
            float r12 = r6.getLineLeft(r1)
            float r12 = r12 + r11
            int r12 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r12 < 0) goto L_0x0146
            float r12 = r6.getLineWidth(r1)
            float r11 = r11 + r12
            int r10 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
            if (r10 > 0) goto L_0x0146
            int r10 = r14.linkY
            int r11 = r10 + r5
            if (r3 < r11) goto L_0x0146
            int r10 = r10 + r5
            int r10 = r10 + r8
            if (r3 > r10) goto L_0x0146
            int r8 = r15.getAction()
            r10 = 0
            if (r8 != 0) goto L_0x00f8
            r14.resetPressedLink()
            r14.spoilerPressed = r10
            android.util.SparseArray<java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect>> r8 = r14.linkSpoilers
            java.lang.Object r8 = r8.get(r4, r10)
            if (r8 == 0) goto L_0x00d3
            android.util.SparseArray<java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect>> r8 = r14.linkSpoilers
            java.lang.Object r8 = r8.get(r4)
            java.util.List r8 = (java.util.List) r8
            java.util.Iterator r8 = r8.iterator()
        L_0x00b2:
            boolean r10 = r8.hasNext()
            if (r10 == 0) goto L_0x00d3
            java.lang.Object r10 = r8.next()
            org.telegram.ui.Components.spoilers.SpoilerEffect r10 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r10
            android.graphics.Rect r11 = r10.getBounds()
            int r12 = r0 - r9
            int r13 = r14.linkY
            int r13 = r3 - r13
            int r13 = r13 - r5
            boolean r11 = r11.contains(r12, r13)
            if (r11 == 0) goto L_0x00b2
            r14.spoilerPressed = r10
            r14.spoilerTypePressed = r1
        L_0x00d3:
            org.telegram.ui.Components.spoilers.SpoilerEffect r5 = r14.spoilerPressed
            if (r5 == 0) goto L_0x00d8
            goto L_0x0141
        L_0x00d8:
            r14.pressedLink = r4
            r14.linkPreviewPressed = r2
            r14.startCheckLongPress()
            org.telegram.ui.Components.LinkPath r4 = r14.urlPath     // Catch:{ Exception -> 0x00f3 }
            r5 = 0
            r4.setCurrentLayout(r6, r1, r5)     // Catch:{ Exception -> 0x00f3 }
            java.lang.CharSequence r4 = r6.getText()     // Catch:{ Exception -> 0x00f3 }
            int r4 = r4.length()     // Catch:{ Exception -> 0x00f3 }
            org.telegram.ui.Components.LinkPath r5 = r14.urlPath     // Catch:{ Exception -> 0x00f3 }
            r6.getSelectionPath(r1, r4, r5)     // Catch:{ Exception -> 0x00f3 }
            goto L_0x0141
        L_0x00f3:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
            goto L_0x0141
        L_0x00f8:
            boolean r4 = r14.linkPreviewPressed
            if (r4 == 0) goto L_0x013a
            int r4 = r14.pressedLink     // Catch:{ Exception -> 0x0132 }
            if (r4 != 0) goto L_0x010a
            org.telegram.messenger.MessageObject r4 = r14.message     // Catch:{ Exception -> 0x0132 }
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner     // Catch:{ Exception -> 0x0132 }
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media     // Catch:{ Exception -> 0x0132 }
            if (r4 == 0) goto L_0x010a
            org.telegram.tgnet.TLRPC$WebPage r10 = r4.webpage     // Catch:{ Exception -> 0x0132 }
        L_0x010a:
            if (r10 == 0) goto L_0x011e
            java.lang.String r4 = r10.embed_url     // Catch:{ Exception -> 0x0132 }
            if (r4 == 0) goto L_0x011e
            int r4 = r4.length()     // Catch:{ Exception -> 0x0132 }
            if (r4 == 0) goto L_0x011e
            org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r4 = r14.delegate     // Catch:{ Exception -> 0x0132 }
            org.telegram.messenger.MessageObject r5 = r14.message     // Catch:{ Exception -> 0x0132 }
            r4.needOpenWebView(r10, r5)     // Catch:{ Exception -> 0x0132 }
            goto L_0x0136
        L_0x011e:
            org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r4 = r14.delegate     // Catch:{ Exception -> 0x0132 }
            java.util.ArrayList<java.lang.CharSequence> r5 = r14.links     // Catch:{ Exception -> 0x0132 }
            int r6 = r14.pressedLink     // Catch:{ Exception -> 0x0132 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ Exception -> 0x0132 }
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5     // Catch:{ Exception -> 0x0132 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0132 }
            r4.onLinkPress(r5, r1)     // Catch:{ Exception -> 0x0132 }
            goto L_0x0136
        L_0x0132:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x0136:
            r14.resetPressedLink()
            goto L_0x0141
        L_0x013a:
            org.telegram.ui.Components.spoilers.SpoilerEffect r4 = r14.spoilerPressed
            if (r4 == 0) goto L_0x0143
            r14.startSpoilerRipples(r0, r3, r5)
        L_0x0141:
            r4 = 1
            goto L_0x0144
        L_0x0143:
            r4 = 0
        L_0x0144:
            r5 = 1
            goto L_0x014d
        L_0x0146:
            int r5 = r5 + r8
        L_0x0147:
            int r4 = r4 + 1
            goto L_0x0045
        L_0x014b:
            r4 = 0
            r5 = 0
        L_0x014d:
            int r6 = r15.getAction()
            if (r6 != 0) goto L_0x01e3
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0158
            goto L_0x015b
        L_0x0158:
            int r6 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r7 = (float) r6
        L_0x015b:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
            android.text.StaticLayout r7 = r14.descriptionLayout
            if (r7 == 0) goto L_0x01a1
            if (r0 < r6) goto L_0x01a1
            int r7 = r7.getWidth()
            int r7 = r7 + r6
            if (r0 > r7) goto L_0x01a1
            int r7 = r14.descriptionY
            if (r3 < r7) goto L_0x01a1
            android.text.StaticLayout r8 = r14.descriptionLayout
            int r8 = r8.getHeight()
            int r7 = r7 + r8
            if (r3 > r7) goto L_0x01a1
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r7 = r14.descriptionLayoutSpoilers
            java.util.Iterator r7 = r7.iterator()
        L_0x017f:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x01a1
            java.lang.Object r8 = r7.next()
            org.telegram.ui.Components.spoilers.SpoilerEffect r8 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r8
            android.graphics.Rect r9 = r8.getBounds()
            int r10 = r0 - r6
            int r11 = r14.descriptionY
            int r11 = r3 - r11
            boolean r9 = r9.contains(r10, r11)
            if (r9 == 0) goto L_0x017f
            r14.spoilerPressed = r8
            r14.spoilerTypePressed = r2
            r4 = 1
            r5 = 1
        L_0x01a1:
            android.text.StaticLayout r7 = r14.descriptionLayout2
            if (r7 == 0) goto L_0x01f2
            if (r0 < r6) goto L_0x01f2
            int r7 = r7.getWidth()
            int r7 = r7 + r6
            if (r0 > r7) goto L_0x01f2
            int r7 = r14.description2Y
            if (r3 < r7) goto L_0x01f2
            android.text.StaticLayout r8 = r14.descriptionLayout2
            int r8 = r8.getHeight()
            int r7 = r7 + r8
            if (r3 > r7) goto L_0x01f2
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r7 = r14.descriptionLayout2Spoilers
            java.util.Iterator r7 = r7.iterator()
        L_0x01c1:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x01f2
            java.lang.Object r8 = r7.next()
            org.telegram.ui.Components.spoilers.SpoilerEffect r8 = (org.telegram.ui.Components.spoilers.SpoilerEffect) r8
            android.graphics.Rect r9 = r8.getBounds()
            int r10 = r0 - r6
            int r11 = r14.description2Y
            int r11 = r3 - r11
            boolean r9 = r9.contains(r10, r11)
            if (r9 == 0) goto L_0x01c1
            r14.spoilerPressed = r8
            r0 = 2
            r14.spoilerTypePressed = r0
            goto L_0x01f0
        L_0x01e3:
            int r6 = r15.getAction()
            if (r6 != r2) goto L_0x01f2
            org.telegram.ui.Components.spoilers.SpoilerEffect r6 = r14.spoilerPressed
            if (r6 == 0) goto L_0x01f2
            r14.startSpoilerRipples(r0, r3, r1)
        L_0x01f0:
            r4 = 1
            r5 = 1
        L_0x01f2:
            if (r5 != 0) goto L_0x01fc
            r14.resetPressedLink()
            goto L_0x01fc
        L_0x01f8:
            r14.resetPressedLink()
        L_0x01fb:
            r4 = 0
        L_0x01fc:
            if (r4 != 0) goto L_0x0204
            boolean r15 = super.onTouchEvent(r15)
            if (r15 == 0) goto L_0x0205
        L_0x0204:
            r1 = 1
        L_0x0205:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedLinkCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void startSpoilerRipples(int i, int i2, int i3) {
        int dp = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
        resetPressedLink();
        this.spoilerPressed.setOnRippleEndCallback(new SharedLinkCell$$ExternalSyntheticLambda1(this));
        int i4 = i - dp;
        float sqrt = (float) Math.sqrt(Math.pow((double) getWidth(), 2.0d) + Math.pow((double) getHeight(), 2.0d));
        float f = 0.0f;
        int i5 = this.spoilerTypePressed;
        if (i5 == 0) {
            for (int i6 = 0; i6 < this.linkLayout.size(); i6++) {
                Layout layout = this.linkLayout.get(i6);
                f += (float) layout.getLineBottom(layout.getLineCount() - 1);
                for (SpoilerEffect startRipple : this.linkSpoilers.get(i6)) {
                    startRipple.startRipple((float) i4, ((float) ((i2 - getYOffsetForType(0)) - i3)) + f, sqrt);
                }
            }
        } else if (i5 == 1) {
            for (SpoilerEffect startRipple2 : this.descriptionLayoutSpoilers) {
                startRipple2.startRipple((float) i4, (float) (i2 - getYOffsetForType(1)), sqrt);
            }
        } else if (i5 == 2) {
            for (SpoilerEffect startRipple3 : this.descriptionLayout2Spoilers) {
                startRipple3.startRipple((float) i4, (float) (i2 - getYOffsetForType(2)), sqrt);
            }
        }
        for (int i7 = 0; i7 <= 2; i7++) {
            if (i7 != this.spoilerTypePressed) {
                if (i7 == 0) {
                    for (int i8 = 0; i8 < this.linkLayout.size(); i8++) {
                        Layout layout2 = this.linkLayout.get(i8);
                        layout2.getLineBottom(layout2.getLineCount() - 1);
                        for (SpoilerEffect spoilerEffect : this.linkSpoilers.get(i8)) {
                            spoilerEffect.startRipple((float) spoilerEffect.getBounds().centerX(), (float) spoilerEffect.getBounds().centerY(), sqrt);
                        }
                    }
                } else if (i7 == 1) {
                    for (SpoilerEffect next : this.descriptionLayoutSpoilers) {
                        next.startRipple((float) next.getBounds().centerX(), (float) next.getBounds().centerY(), sqrt);
                    }
                } else if (i7 == 2) {
                    for (SpoilerEffect next2 : this.descriptionLayout2Spoilers) {
                        next2.startRipple((float) next2.getBounds().centerX(), (float) next2.getBounds().centerY(), sqrt);
                    }
                }
            }
        }
        this.spoilerTypePressed = -1;
        this.spoilerPressed = null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startSpoilerRipples$1() {
        post(new SharedLinkCell$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startSpoilerRipples$0() {
        this.message.isSpoilersRevealed = true;
        this.linkSpoilers.clear();
        this.descriptionLayoutSpoilers.clear();
        this.descriptionLayout2Spoilers.clear();
        invalidate();
    }

    private int getYOffsetForType(int i) {
        if (i == 1) {
            return this.descriptionY;
        }
        if (i != 2) {
            return this.linkY;
        }
        return this.description2Y;
    }

    public String getLink(int i) {
        if (i < 0 || i >= this.links.size()) {
            return null;
        }
        return this.links.get(i).toString();
    }

    /* access modifiers changed from: protected */
    public void resetPressedLink() {
        this.pressedLink = -1;
        this.linkPreviewPressed = false;
        cancelCheckLongPress();
        invalidate();
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.viewType == 1) {
            this.description2TextPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        }
        float f = 8.0f;
        if (this.dateLayout != null) {
            canvas.save();
            canvas2.translate((float) (AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline) + (LocaleController.isRTL ? 0 : this.dateLayoutX)), (float) this.titleY);
            this.dateLayout.draw(canvas2);
            canvas.restore();
        }
        if (this.titleLayout != null) {
            canvas.save();
            float dp = (float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
            if (LocaleController.isRTL) {
                StaticLayout staticLayout = this.dateLayout;
                dp += staticLayout == null ? 0.0f : (float) (staticLayout.getWidth() + AndroidUtilities.dp(4.0f));
            }
            canvas2.translate(dp, (float) this.titleY);
            this.titleLayout.draw(canvas2);
            canvas.restore();
        }
        if (this.captionLayout != null) {
            this.captionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas2.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.captionY);
            this.captionLayout.draw(canvas2);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas2.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            SpoilerEffect.renderWithRipple(this, false, this.descriptionTextPaint.getColor(), -AndroidUtilities.dp(2.0f), this.patchedDescriptionLayout, this.descriptionLayout, this.descriptionLayoutSpoilers, canvas);
            canvas.restore();
        }
        if (this.descriptionLayout2 != null) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas2.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.description2Y);
            SpoilerEffect.renderWithRipple(this, false, this.descriptionTextPaint.getColor(), -AndroidUtilities.dp(2.0f), this.patchedDescriptionLayout2, this.descriptionLayout2, this.descriptionLayout2Spoilers, canvas);
            canvas.restore();
        }
        if (!this.linkLayout.isEmpty()) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            int i = 0;
            for (int i2 = 0; i2 < this.linkLayout.size(); i2++) {
                StaticLayout staticLayout2 = this.linkLayout.get(i2);
                List<SpoilerEffect> list = this.linkSpoilers.get(i2);
                if (staticLayout2.getLineCount() > 0) {
                    canvas.save();
                    canvas2.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) (this.linkY + i));
                    this.path.rewind();
                    if (list != null) {
                        for (SpoilerEffect bounds : list) {
                            Rect bounds2 = bounds.getBounds();
                            this.path.addRect((float) bounds2.left, (float) bounds2.top, (float) bounds2.right, (float) bounds2.bottom, Path.Direction.CW);
                        }
                    }
                    canvas.save();
                    canvas2.clipPath(this.path, Region.Op.DIFFERENCE);
                    if (this.pressedLink == i2) {
                        canvas2.drawPath(this.urlPath, Theme.linkSelectionPaint);
                    }
                    staticLayout2.draw(canvas2);
                    canvas.restore();
                    canvas.save();
                    canvas2.clipPath(this.path);
                    this.path.rewind();
                    if (list != null && !list.isEmpty()) {
                        ((SpoilerEffect) list.get(0)).getRipplePath(this.path);
                    }
                    canvas2.clipPath(this.path);
                    if (this.pressedLink == i2) {
                        canvas2.drawPath(this.urlPath, Theme.linkSelectionPaint);
                    }
                    staticLayout2.draw(canvas2);
                    canvas.restore();
                    if (list != null) {
                        for (SpoilerEffect draw : list) {
                            draw.draw(canvas2);
                        }
                    }
                    canvas.restore();
                    i += staticLayout2.getLineBottom(staticLayout2.getLineCount() - 1);
                }
            }
        }
        if (this.fromInfoLayout != null) {
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas2.translate((float) AndroidUtilities.dp(f), (float) this.fromInfoLayoutY);
            this.fromInfoLayout.draw(canvas2);
            canvas.restore();
        }
        this.letterDrawable.draw(canvas2);
        if (this.drawLinkImageView) {
            this.linkImageView.draw(canvas2);
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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
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
        accessibilityNodeInfo.setText(sb.toString());
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setChecked(true);
            accessibilityNodeInfo.setCheckable(true);
        }
    }
}
