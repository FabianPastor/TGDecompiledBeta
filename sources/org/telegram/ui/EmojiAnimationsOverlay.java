package org.telegram.ui;

import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_messages_setTyping;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_sendMessageEmojiInteraction;
import org.telegram.tgnet.TLRPC$TL_stickerPack;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerSetBulletinLayout;

public class EmojiAnimationsOverlay implements NotificationCenter.NotificationCenterDelegate {
    private static final HashSet<String> excludeEmojiFromPack;
    private static final HashSet<String> supportedEmoji = new HashSet<>();
    ArrayList<Integer> animationIndexes = new ArrayList<>();
    private boolean attached;
    ChatActivity chatActivity;
    FrameLayout contentLayout;
    int currentAccount;
    long dialogId;
    ArrayList<DrawingObject> drawingObjects = new ArrayList<>();
    HashMap<String, ArrayList<TLRPC$Document>> emojiInteractionsStickersMap = new HashMap<>();
    Runnable hintRunnable;
    boolean inited = false;
    HashMap<Long, Integer> lastAnimationIndex = new HashMap<>();
    String lastTappedEmoji;
    int lastTappedMsgId = -1;
    long lastTappedTime = 0;
    RecyclerListView listView;
    Random random = new Random();
    Runnable sentInteractionsRunnable;
    TLRPC$TL_messages_stickerSet set;
    int threadMsgId;
    ArrayList<Long> timeIntervals = new ArrayList<>();

    static {
        HashSet<String> hashSet = new HashSet<>();
        excludeEmojiFromPack = hashSet;
        hashSet.add("0⃣");
        hashSet.add("1⃣");
        hashSet.add("2⃣");
        hashSet.add("3⃣");
        hashSet.add("4⃣");
        hashSet.add("5⃣");
        hashSet.add("6⃣");
        hashSet.add("7⃣");
        hashSet.add("8⃣");
        hashSet.add("9⃣");
    }

    public EmojiAnimationsOverlay(ChatActivity chatActivity2, FrameLayout frameLayout, RecyclerListView recyclerListView, int i, long j, int i2) {
        this.chatActivity = chatActivity2;
        this.contentLayout = frameLayout;
        this.listView = recyclerListView;
        this.currentAccount = i;
        this.dialogId = j;
        this.threadMsgId = i2;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        this.attached = true;
        checkStickerPack();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onEmojiInteractionsReceived);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.attached = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.onEmojiInteractionsReceived);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void checkStickerPack() {
        if (!this.inited) {
            TLRPC$TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("EmojiAnimations");
            this.set = stickerSetByName;
            if (stickerSetByName == null) {
                this.set = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("EmojiAnimations");
            }
            if (this.set == null) {
                MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("EmojiAnimations", false, true);
            }
            if (this.set != null) {
                HashMap hashMap = new HashMap();
                for (int i = 0; i < this.set.documents.size(); i++) {
                    hashMap.put(Long.valueOf(this.set.documents.get(i).id), this.set.documents.get(i));
                }
                for (int i2 = 0; i2 < this.set.packs.size(); i2++) {
                    TLRPC$TL_stickerPack tLRPC$TL_stickerPack = this.set.packs.get(i2);
                    if (!excludeEmojiFromPack.contains(tLRPC$TL_stickerPack.emoticon) && tLRPC$TL_stickerPack.documents.size() > 0) {
                        supportedEmoji.add(tLRPC$TL_stickerPack.emoticon);
                        ArrayList arrayList = new ArrayList();
                        this.emojiInteractionsStickersMap.put(tLRPC$TL_stickerPack.emoticon, arrayList);
                        for (int i3 = 0; i3 < tLRPC$TL_stickerPack.documents.size(); i3++) {
                            arrayList.add((TLRPC$Document) hashMap.get(tLRPC$TL_stickerPack.documents.get(i3)));
                        }
                        if (tLRPC$TL_stickerPack.emoticon.equals("❤")) {
                            String[] strArr = {"🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎"};
                            for (int i4 = 0; i4 < 8; i4++) {
                                String str = strArr[i4];
                                supportedEmoji.add(str);
                                this.emojiInteractionsStickersMap.put(str, arrayList);
                            }
                        }
                    }
                }
                this.inited = true;
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Integer printingStringType;
        if (i == NotificationCenter.diceStickersDidLoad) {
            if ("EmojiAnimations".equals(objArr[0])) {
                checkStickerPack();
            }
        } else if (i == NotificationCenter.onEmojiInteractionsReceived) {
            long longValue = objArr[0].longValue();
            TLRPC$TL_sendMessageEmojiInteraction tLRPC$TL_sendMessageEmojiInteraction = objArr[1];
            if (longValue == this.dialogId && supportedEmoji.contains(tLRPC$TL_sendMessageEmojiInteraction.emoticon)) {
                final int i3 = tLRPC$TL_sendMessageEmojiInteraction.msg_id;
                if (tLRPC$TL_sendMessageEmojiInteraction.interaction.data != null) {
                    try {
                        JSONArray jSONArray = new JSONObject(tLRPC$TL_sendMessageEmojiInteraction.interaction.data).getJSONArray("a");
                        for (int i4 = 0; i4 < jSONArray.length(); i4++) {
                            JSONObject jSONObject = jSONArray.getJSONObject(i4);
                            final int optInt = jSONObject.optInt("i", 1) - 1;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    EmojiAnimationsOverlay.this.findViewAndShowAnimation(i3, optInt);
                                }
                            }, (long) (jSONObject.optDouble("t", 0.0d) * 1000.0d));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (i == NotificationCenter.updateInterfaces && (printingStringType = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.dialogId, this.threadMsgId)) != null && printingStringType.intValue() == 5) {
            cancelHintRunnable();
        }
    }

    /* access modifiers changed from: private */
    public void findViewAndShowAnimation(int i, int i2) {
        if (this.attached) {
            ChatMessageCell chatMessageCell = null;
            int i3 = 0;
            while (true) {
                if (i3 >= this.listView.getChildCount()) {
                    break;
                }
                View childAt = this.listView.getChildAt(i3);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt;
                    if (chatMessageCell2.getPhotoImage().hasNotThumb() && chatMessageCell2.getMessageObject().getStickerEmoji() != null && chatMessageCell2.getMessageObject().getId() == i) {
                        chatMessageCell = chatMessageCell2;
                        break;
                    }
                }
                i3++;
            }
            if (chatMessageCell != null) {
                this.chatActivity.restartSticker(chatMessageCell);
                if (!EmojiData.hasEmojiSupportVibration(chatMessageCell.getMessageObject().getStickerEmoji())) {
                    chatMessageCell.performHapticFeedback(3);
                }
                showAnimationForCell(chatMessageCell, i2, false, true);
            }
        }
    }

    public void draw(Canvas canvas) {
        float f;
        if (!this.drawingObjects.isEmpty()) {
            int i = 0;
            while (i < this.drawingObjects.size()) {
                DrawingObject drawingObject = this.drawingObjects.get(i);
                drawingObject.viewFound = false;
                int i2 = 0;
                while (true) {
                    if (i2 >= this.listView.getChildCount()) {
                        break;
                    }
                    View childAt = this.listView.getChildAt(i2);
                    if (childAt instanceof ChatMessageCell) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                        if (chatMessageCell.getMessageObject().getId() == drawingObject.messageId) {
                            drawingObject.viewFound = true;
                            float x = this.listView.getX() + childAt.getX() + chatMessageCell.getPhotoImage().getImageX();
                            float y = this.listView.getY() + childAt.getY() + chatMessageCell.getPhotoImage().getImageY();
                            if (drawingObject.isOut) {
                                f = ((-chatMessageCell.getPhotoImage().getImageWidth()) * 2.0f) + ((float) AndroidUtilities.dp(24.0f));
                            } else {
                                f = (float) (-AndroidUtilities.dp(24.0f));
                            }
                            drawingObject.lastX = x + f;
                            drawingObject.lastY = y - chatMessageCell.getPhotoImage().getImageWidth();
                            drawingObject.lastW = chatMessageCell.getPhotoImage().getImageWidth();
                        }
                    }
                    i2++;
                }
                ImageReceiver imageReceiver = drawingObject.imageReceiver;
                float f2 = drawingObject.lastX + drawingObject.randomOffsetX;
                float f3 = drawingObject.lastY + drawingObject.randomOffsetY;
                float f4 = drawingObject.lastW;
                imageReceiver.setImageCoords(f2, f3, f4 * 3.0f, f4 * 3.0f);
                if (!drawingObject.isOut) {
                    canvas.save();
                    canvas.scale(-1.0f, 1.0f, drawingObject.imageReceiver.getCenterX(), drawingObject.imageReceiver.getCenterY());
                    drawingObject.imageReceiver.draw(canvas);
                    canvas.restore();
                } else {
                    drawingObject.imageReceiver.draw(canvas);
                }
                if (drawingObject.wasPlayed && drawingObject.imageReceiver.getLottieAnimation() != null && drawingObject.imageReceiver.getLottieAnimation().getCurrentFrame() == drawingObject.imageReceiver.getLottieAnimation().getFramesCount() - 2) {
                    this.drawingObjects.remove(i);
                    i--;
                } else if (drawingObject.imageReceiver.getLottieAnimation() != null && drawingObject.imageReceiver.getLottieAnimation().isRunning()) {
                    drawingObject.wasPlayed = true;
                } else if (drawingObject.imageReceiver.getLottieAnimation() != null && !drawingObject.imageReceiver.getLottieAnimation().isRunning()) {
                    drawingObject.imageReceiver.getLottieAnimation().setCurrentFrame(0, true);
                    drawingObject.imageReceiver.getLottieAnimation().start();
                }
                i++;
            }
            this.contentLayout.invalidate();
        }
    }

    public void onTapItem(ChatMessageCell chatMessageCell, ChatActivity chatActivity2) {
        if (chatActivity2.currentUser != null && !chatActivity2.isSecretChat() && chatMessageCell.getMessageObject() != null && chatMessageCell.getMessageObject().getId() >= 0) {
            boolean showAnimationForCell = showAnimationForCell(chatMessageCell, -1, true, false);
            if (showAnimationForCell && !EmojiData.hasEmojiSupportVibration(chatMessageCell.getMessageObject().getStickerEmoji())) {
                chatMessageCell.performHapticFeedback(3);
            }
            Integer printingStringType = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.dialogId, this.threadMsgId);
            if ((printingStringType == null || printingStringType.intValue() != 5) && this.hintRunnable == null && showAnimationForCell) {
                if ((Bulletin.getVisibleBulletin() == null || !Bulletin.getVisibleBulletin().isShowing()) && SharedConfig.emojiInteractionsHintCount > 0 && UserConfig.getInstance(this.currentAccount).getClientUserId() != chatActivity2.currentUser.id) {
                    SharedConfig.updateEmojiInteractionsHintCount(SharedConfig.emojiInteractionsHintCount - 1);
                    StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(chatActivity2.getParentActivity(), (TLObject) null, -1, MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(chatMessageCell.getMessageObject().getStickerEmoji()), chatActivity2.getResourceProvider());
                    stickerSetBulletinLayout.subtitleTextView.setVisibility(8);
                    stickerSetBulletinLayout.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EmojiInteractionTapHint", NUM, chatActivity2.currentUser.first_name)));
                    stickerSetBulletinLayout.titleTextView.setTypeface((Typeface) null);
                    stickerSetBulletinLayout.titleTextView.setMaxLines(3);
                    stickerSetBulletinLayout.titleTextView.setSingleLine(false);
                    final Bulletin make = Bulletin.make((BaseFragment) chatActivity2, (Bulletin.Layout) stickerSetBulletinLayout, 2750);
                    AnonymousClass2 r12 = new Runnable() {
                        public void run() {
                            make.show();
                            EmojiAnimationsOverlay.this.hintRunnable = null;
                        }
                    };
                    this.hintRunnable = r12;
                    AndroidUtilities.runOnUIThread(r12, 1500);
                }
            }
        }
    }

    public void cancelHintRunnable() {
        Runnable runnable = this.hintRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.hintRunnable = null;
    }

    private boolean showAnimationForCell(ChatMessageCell chatMessageCell, int i, boolean z, boolean z2) {
        String stickerEmoji;
        ArrayList arrayList;
        int i2;
        Runnable runnable;
        int i3 = i;
        if (this.drawingObjects.size() > 12 || !chatMessageCell.getPhotoImage().hasNotThumb() || (stickerEmoji = chatMessageCell.getMessageObject().getStickerEmoji()) == null) {
            return false;
        }
        float imageHeight = chatMessageCell.getPhotoImage().getImageHeight();
        float imageWidth = chatMessageCell.getPhotoImage().getImageWidth();
        if (imageHeight > 0.0f && imageWidth > 0.0f) {
            String unwrapEmoji = unwrapEmoji(stickerEmoji);
            if (supportedEmoji.contains(unwrapEmoji) && (arrayList = this.emojiInteractionsStickersMap.get(unwrapEmoji)) != null && !arrayList.isEmpty()) {
                int i4 = 0;
                for (int i5 = 0; i5 < this.drawingObjects.size(); i5++) {
                    if (this.drawingObjects.get(i5).messageId == chatMessageCell.getMessageObject().getId()) {
                        i4++;
                        if (this.drawingObjects.get(i5).imageReceiver.getLottieAnimation() == null || this.drawingObjects.get(i5).imageReceiver.getLottieAnimation().isGeneratingCache()) {
                            return false;
                        }
                    }
                }
                if (i4 >= 4) {
                    return false;
                }
                if (i3 < 0 || i3 > arrayList.size() - 1) {
                    i3 = Math.abs(this.random.nextInt()) % arrayList.size();
                }
                TLRPC$Document tLRPC$Document = (TLRPC$Document) arrayList.get(i3);
                DrawingObject drawingObject = new DrawingObject();
                drawingObject.randomOffsetX = (imageWidth / 4.0f) * (((float) (this.random.nextInt() % 101)) / 100.0f);
                drawingObject.randomOffsetY = (imageHeight / 4.0f) * (((float) (this.random.nextInt() % 101)) / 100.0f);
                drawingObject.messageId = chatMessageCell.getMessageObject().getId();
                drawingObject.isOut = chatMessageCell.getMessageObject().isOutOwner();
                Integer num = this.lastAnimationIndex.get(Long.valueOf(tLRPC$Document.id));
                if (num == null) {
                    i2 = 0;
                } else {
                    i2 = num.intValue();
                }
                this.lastAnimationIndex.put(Long.valueOf(tLRPC$Document.id), Integer.valueOf((i2 + 1) % 4));
                ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$Document);
                ImageReceiver imageReceiver = drawingObject.imageReceiver;
                imageReceiver.setUniqKeyPrefix(i2 + "_" + drawingObject.messageId + "_");
                int i6 = (int) ((imageWidth * 2.0f) / AndroidUtilities.density);
                ImageReceiver imageReceiver2 = drawingObject.imageReceiver;
                imageReceiver2.setImage(forDocument, i6 + "_" + i6 + "_pcache", (Drawable) null, "tgs", this.set, 1);
                drawingObject.imageReceiver.setLayerNum(Integer.MAX_VALUE);
                drawingObject.imageReceiver.setAllowStartAnimation(true);
                drawingObject.imageReceiver.setAutoRepeat(0);
                if (drawingObject.imageReceiver.getLottieAnimation() != null) {
                    drawingObject.imageReceiver.getLottieAnimation().start();
                }
                this.drawingObjects.add(drawingObject);
                drawingObject.imageReceiver.onAttachedToWindow();
                drawingObject.imageReceiver.setParentView(this.contentLayout);
                this.contentLayout.invalidate();
                if (z) {
                    int i7 = this.lastTappedMsgId;
                    if (!(i7 == 0 || i7 == chatMessageCell.getMessageObject().getId() || (runnable = this.sentInteractionsRunnable) == null)) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                        this.sentInteractionsRunnable.run();
                    }
                    this.lastTappedMsgId = chatMessageCell.getMessageObject().getId();
                    this.lastTappedEmoji = unwrapEmoji;
                    if (this.lastTappedTime == 0) {
                        this.lastTappedTime = System.currentTimeMillis();
                        this.timeIntervals.clear();
                        this.animationIndexes.clear();
                        this.timeIntervals.add(0L);
                        this.animationIndexes.add(Integer.valueOf(i3));
                    } else {
                        this.timeIntervals.add(Long.valueOf(System.currentTimeMillis() - this.lastTappedTime));
                        this.animationIndexes.add(Integer.valueOf(i3));
                    }
                    Runnable runnable2 = this.sentInteractionsRunnable;
                    if (runnable2 != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable2);
                        this.sentInteractionsRunnable = null;
                    }
                    EmojiAnimationsOverlay$$ExternalSyntheticLambda0 emojiAnimationsOverlay$$ExternalSyntheticLambda0 = new EmojiAnimationsOverlay$$ExternalSyntheticLambda0(this);
                    this.sentInteractionsRunnable = emojiAnimationsOverlay$$ExternalSyntheticLambda0;
                    AndroidUtilities.runOnUIThread(emojiAnimationsOverlay$$ExternalSyntheticLambda0, 500);
                }
                if (z2) {
                    MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadMsgId, 11, unwrapEmoji, 0);
                }
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showAnimationForCell$0() {
        sendCurrentTaps();
        this.sentInteractionsRunnable = null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0043, code lost:
        if (r9.charAt(r3) != 9794) goto L_0x0060;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0029, code lost:
        if (r9.charAt(r3) <= 57343) goto L_0x0045;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String unwrapEmoji(java.lang.String r9) {
        /*
            r8 = this;
            int r0 = r9.length()
            r1 = 0
            r2 = 0
        L_0x0006:
            if (r2 >= r0) goto L_0x0088
            int r3 = r0 + -1
            r4 = 2
            r5 = 1
            if (r2 >= r3) goto L_0x0060
            char r3 = r9.charAt(r2)
            r6 = 55356(0xd83c, float:7.757E-41)
            if (r3 != r6) goto L_0x002b
            int r3 = r2 + 1
            char r6 = r9.charAt(r3)
            r7 = 57339(0xdffb, float:8.0349E-41)
            if (r6 < r7) goto L_0x002b
            char r3 = r9.charAt(r3)
            r6 = 57343(0xdfff, float:8.0355E-41)
            if (r3 <= r6) goto L_0x0045
        L_0x002b:
            char r3 = r9.charAt(r2)
            r6 = 8205(0x200d, float:1.1498E-41)
            if (r3 != r6) goto L_0x0060
            int r3 = r2 + 1
            char r6 = r9.charAt(r3)
            r7 = 9792(0x2640, float:1.3722E-41)
            if (r6 == r7) goto L_0x0045
            char r3 = r9.charAt(r3)
            r6 = 9794(0x2642, float:1.3724E-41)
            if (r3 != r6) goto L_0x0060
        L_0x0045:
            java.lang.CharSequence[] r3 = new java.lang.CharSequence[r4]
            java.lang.CharSequence r4 = r9.subSequence(r1, r2)
            r3[r1] = r4
            int r4 = r2 + 2
            int r6 = r9.length()
            java.lang.CharSequence r9 = r9.subSequence(r4, r6)
            r3[r5] = r9
            java.lang.CharSequence r9 = android.text.TextUtils.concat(r3)
            int r0 = r0 + -2
            goto L_0x0083
        L_0x0060:
            char r3 = r9.charAt(r2)
            r6 = 65039(0xfe0f, float:9.1139E-41)
            if (r3 != r6) goto L_0x0085
            java.lang.CharSequence[] r3 = new java.lang.CharSequence[r4]
            java.lang.CharSequence r4 = r9.subSequence(r1, r2)
            r3[r1] = r4
            int r4 = r2 + 1
            int r6 = r9.length()
            java.lang.CharSequence r9 = r9.subSequence(r4, r6)
            r3[r5] = r9
            java.lang.CharSequence r9 = android.text.TextUtils.concat(r3)
            int r0 = r0 + -1
        L_0x0083:
            int r2 = r2 + -1
        L_0x0085:
            int r2 = r2 + r5
            goto L_0x0006
        L_0x0088:
            java.lang.String r9 = r9.toString()
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EmojiAnimationsOverlay.unwrapEmoji(java.lang.String):java.lang.String");
    }

    private void sendCurrentTaps() {
        if (this.lastTappedMsgId != 0) {
            TLRPC$TL_sendMessageEmojiInteraction tLRPC$TL_sendMessageEmojiInteraction = new TLRPC$TL_sendMessageEmojiInteraction();
            tLRPC$TL_sendMessageEmojiInteraction.msg_id = this.lastTappedMsgId;
            tLRPC$TL_sendMessageEmojiInteraction.emoticon = this.lastTappedEmoji;
            tLRPC$TL_sendMessageEmojiInteraction.interaction = new TLRPC$TL_dataJSON();
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("v", 1);
                JSONArray jSONArray = new JSONArray();
                for (int i = 0; i < this.timeIntervals.size(); i++) {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("i", this.animationIndexes.get(i).intValue() + 1);
                    jSONObject2.put("t", (double) (((float) this.timeIntervals.get(i).longValue()) / 1000.0f));
                    jSONArray.put(i, jSONObject2);
                }
                jSONObject.put("a", jSONArray);
                tLRPC$TL_sendMessageEmojiInteraction.interaction.data = jSONObject.toString();
                TLRPC$TL_messages_setTyping tLRPC$TL_messages_setTyping = new TLRPC$TL_messages_setTyping();
                int i2 = this.threadMsgId;
                if (i2 != 0) {
                    tLRPC$TL_messages_setTyping.top_msg_id = i2;
                    tLRPC$TL_messages_setTyping.flags |= 1;
                }
                tLRPC$TL_messages_setTyping.action = tLRPC$TL_sendMessageEmojiInteraction;
                tLRPC$TL_messages_setTyping.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_setTyping, (RequestDelegate) null);
                clearSendingInfo();
            } catch (JSONException e) {
                clearSendingInfo();
                FileLog.e((Throwable) e);
            }
        }
    }

    private void clearSendingInfo() {
        this.lastTappedMsgId = 0;
        this.lastTappedEmoji = null;
        this.lastTappedTime = 0;
        this.timeIntervals.clear();
        this.animationIndexes.clear();
    }

    public void onScrolled(int i) {
        for (int i2 = 0; i2 < this.drawingObjects.size(); i2++) {
            if (!this.drawingObjects.get(i2).viewFound) {
                this.drawingObjects.get(i2).lastY -= (float) i;
            }
        }
    }

    private class DrawingObject {
        ImageReceiver imageReceiver;
        boolean isOut;
        public float lastW;
        public float lastX;
        public float lastY;
        int messageId;
        public float randomOffsetX;
        public float randomOffsetY;
        public boolean viewFound;
        boolean wasPlayed;

        private DrawingObject(EmojiAnimationsOverlay emojiAnimationsOverlay) {
            this.imageReceiver = new ImageReceiver();
        }
    }
}
