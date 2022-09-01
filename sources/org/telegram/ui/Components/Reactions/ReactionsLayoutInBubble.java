package org.telegram.ui.Components.Reactions;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessagePeerReaction;
import org.telegram.tgnet.TLRPC$Reaction;
import org.telegram.tgnet.TLRPC$ReactionCount;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_reactionCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_reactionEmoji;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarsDarawable;
import org.telegram.ui.Components.CounterView;

public class ReactionsLayoutInBubble {
    private static int animationUniq;
    private static final ButtonsComparator comparator = new ButtonsComparator();
    /* access modifiers changed from: private */
    public static Paint paint = new Paint(1);
    private static int pointer = 1;
    /* access modifiers changed from: private */
    public static TextPaint textPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public static final Comparator<TLRPC$User> usersComparator = ReactionsLayoutInBubble$$ExternalSyntheticLambda1.INSTANCE;
    private int animateFromTotalHeight;
    private boolean animateHeight;
    private boolean animateMove;
    private boolean animateWidth;
    HashMap<VisibleReaction, ImageReceiver> animatedReactions = new HashMap<>();
    boolean attached;
    int currentAccount;
    public boolean drawServiceShaderBackground;
    public int fromWidth;
    private float fromX;
    private float fromY;
    public boolean hasUnreadReactions;
    public int height;
    public boolean isEmpty;
    public boolean isSmall;
    private int lastDrawTotalHeight;
    HashMap<String, ReactionButton> lastDrawingReactionButtons = new HashMap<>();
    HashMap<String, ReactionButton> lastDrawingReactionButtonsTmp = new HashMap<>();
    private int lastDrawnWidth;
    private float lastDrawnX;
    private float lastDrawnY;
    public int lastLineX;
    ReactionButton lastSelectedButton;
    float lastX;
    float lastY;
    Runnable longPressRunnable;
    MessageObject messageObject;
    ArrayList<ReactionButton> outButtons = new ArrayList<>();
    ChatMessageCell parentView;
    public int positionOffsetY;
    boolean pressed;
    ArrayList<ReactionButton> reactionButtons = new ArrayList<>();
    Theme.ResourcesProvider resourcesProvider;
    private String scrimViewReaction;
    public int totalHeight;
    private float touchSlop;
    private boolean wasDrawn;
    public int width;
    public int x;
    public int y;

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$0(TLRPC$User tLRPC$User, TLRPC$User tLRPC$User2) {
        return (int) (tLRPC$User.id - tLRPC$User2.id);
    }

    public ReactionsLayoutInBubble(ChatMessageCell chatMessageCell) {
        this.parentView = chatMessageCell;
        this.currentAccount = UserConfig.selectedAccount;
        paint.setColor(Theme.getColor("chat_inLoader", this.resourcesProvider));
        textPaint.setColor(Theme.getColor("featuredStickers_buttonText", this.resourcesProvider));
        textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.touchSlop = (float) ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();
    }

    public static boolean equalsTLReaction(TLRPC$Reaction tLRPC$Reaction, TLRPC$Reaction tLRPC$Reaction2) {
        if ((tLRPC$Reaction instanceof TLRPC$TL_reactionEmoji) && (tLRPC$Reaction2 instanceof TLRPC$TL_reactionEmoji)) {
            return TextUtils.equals(((TLRPC$TL_reactionEmoji) tLRPC$Reaction).emoticon, ((TLRPC$TL_reactionEmoji) tLRPC$Reaction2).emoticon);
        }
        if (!(tLRPC$Reaction instanceof TLRPC$TL_reactionCustomEmoji) || !(tLRPC$Reaction2 instanceof TLRPC$TL_reactionCustomEmoji) || ((TLRPC$TL_reactionCustomEmoji) tLRPC$Reaction).document_id != ((TLRPC$TL_reactionCustomEmoji) tLRPC$Reaction2).document_id) {
            return false;
        }
        return true;
    }

    public void setMessage(MessageObject messageObject2, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        this.resourcesProvider = resourcesProvider2;
        this.isSmall = z;
        this.messageObject = messageObject2;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).detach();
        }
        this.hasUnreadReactions = false;
        this.reactionButtons.clear();
        if (messageObject2 != null) {
            comparator.dialogId = messageObject2.getDialogId();
            TLRPC$TL_messageReactions tLRPC$TL_messageReactions = messageObject2.messageOwner.reactions;
            if (tLRPC$TL_messageReactions != null && tLRPC$TL_messageReactions.results != null) {
                int i2 = 0;
                for (int i3 = 0; i3 < messageObject2.messageOwner.reactions.results.size(); i3++) {
                    i2 += messageObject2.messageOwner.reactions.results.get(i3).count;
                }
                int i4 = 0;
                while (true) {
                    if (i4 >= messageObject2.messageOwner.reactions.results.size()) {
                        break;
                    }
                    TLRPC$ReactionCount tLRPC$ReactionCount = messageObject2.messageOwner.reactions.results.get(i4);
                    ReactionButton reactionButton = new ReactionButton(tLRPC$ReactionCount, z);
                    this.reactionButtons.add(reactionButton);
                    if (!z && messageObject2.messageOwner.reactions.recent_reactions != null) {
                        ArrayList arrayList = null;
                        if (messageObject2.getDialogId() > 0) {
                            ArrayList arrayList2 = new ArrayList();
                            if (tLRPC$ReactionCount.count == 2) {
                                arrayList2.add(UserConfig.getInstance(this.currentAccount).getCurrentUser());
                                arrayList2.add(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(messageObject2.getDialogId())));
                            } else if (tLRPC$ReactionCount.chosen) {
                                arrayList2.add(UserConfig.getInstance(this.currentAccount).getCurrentUser());
                            } else {
                                arrayList2.add(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(messageObject2.getDialogId())));
                            }
                            reactionButton.setUsers(arrayList2);
                            if (!arrayList2.isEmpty()) {
                                reactionButton.count = 0;
                                reactionButton.counterDrawable.setCount(0, false);
                            }
                        } else if (tLRPC$ReactionCount.count <= 3 && i2 <= 3) {
                            for (int i5 = 0; i5 < messageObject2.messageOwner.reactions.recent_reactions.size(); i5++) {
                                TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction = messageObject2.messageOwner.reactions.recent_reactions.get(i5);
                                if (VisibleReaction.fromTLReaction(tLRPC$MessagePeerReaction.reaction).equals(VisibleReaction.fromTLReaction(tLRPC$ReactionCount.reaction)) && MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(tLRPC$MessagePeerReaction.peer_id))) != null) {
                                    if (arrayList == null) {
                                        arrayList = new ArrayList();
                                    }
                                    arrayList.add(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(tLRPC$MessagePeerReaction.peer_id))));
                                }
                            }
                            reactionButton.setUsers(arrayList);
                            if (arrayList != null && !arrayList.isEmpty()) {
                                reactionButton.count = 0;
                                reactionButton.counterDrawable.setCount(0, false);
                            }
                        }
                    }
                    if (!z || tLRPC$ReactionCount.count <= 1 || !tLRPC$ReactionCount.chosen) {
                        if (z && i4 == 2) {
                            break;
                        }
                        if (this.attached) {
                            reactionButton.attach();
                        }
                        i4++;
                    } else {
                        this.reactionButtons.add(new ReactionButton(tLRPC$ReactionCount, z));
                        this.reactionButtons.get(0).isSelected = false;
                        this.reactionButtons.get(1).isSelected = true;
                        this.reactionButtons.get(0).realCount = 1;
                        this.reactionButtons.get(1).realCount = 1;
                        this.reactionButtons.get(1).key += "_";
                        break;
                    }
                }
            }
            if (!z && !this.reactionButtons.isEmpty()) {
                ButtonsComparator buttonsComparator = comparator;
                buttonsComparator.currentAccount = this.currentAccount;
                Collections.sort(this.reactionButtons, buttonsComparator);
                for (int i6 = 0; i6 < this.reactionButtons.size(); i6++) {
                    TLRPC$ReactionCount access$100 = this.reactionButtons.get(i6).reactionCount;
                    int i7 = pointer;
                    pointer = i7 + 1;
                    access$100.lastDrawnPosition = i7;
                }
            }
            this.hasUnreadReactions = MessageObject.hasUnreadReactions(messageObject2.messageOwner);
        }
        this.isEmpty = this.reactionButtons.isEmpty();
    }

    public void measure(int i, int i2) {
        int i3 = i;
        int i4 = i2;
        this.height = 0;
        this.width = 0;
        this.positionOffsetY = 0;
        this.totalHeight = 0;
        if (!this.isEmpty) {
            int i5 = 0;
            int i6 = 0;
            int i7 = 0;
            for (int i8 = 0; i8 < this.reactionButtons.size(); i8++) {
                ReactionButton reactionButton = this.reactionButtons.get(i8);
                if (reactionButton.isSmall) {
                    reactionButton.width = AndroidUtilities.dp(14.0f);
                    reactionButton.height = AndroidUtilities.dp(14.0f);
                } else {
                    reactionButton.width = AndroidUtilities.dp(8.0f) + AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(4.0f);
                    if (reactionButton.avatarsDarawable == null || reactionButton.users.size() <= 0) {
                        reactionButton.width = (int) (((float) reactionButton.width) + reactionButton.counterDrawable.textPaint.measureText(reactionButton.countText) + ((float) AndroidUtilities.dp(8.0f)));
                    } else {
                        reactionButton.users.size();
                        reactionButton.width = (int) (((float) reactionButton.width) + ((float) (AndroidUtilities.dp(2.0f) + (AndroidUtilities.dp(20.0f) * 1))) + (((float) ((reactionButton.users.size() > 1 ? reactionButton.users.size() - 1 : 0) * AndroidUtilities.dp(20.0f))) * 0.8f) + ((float) AndroidUtilities.dp(1.0f)));
                        reactionButton.avatarsDarawable.height = AndroidUtilities.dp(26.0f);
                    }
                    reactionButton.height = AndroidUtilities.dp(26.0f);
                }
                if (reactionButton.width + i5 > i3) {
                    i6 += reactionButton.height + AndroidUtilities.dp(4.0f);
                    i5 = 0;
                }
                reactionButton.x = i5;
                reactionButton.y = i6;
                i5 += reactionButton.width + AndroidUtilities.dp(4.0f);
                if (i5 > i7) {
                    i7 = i5;
                }
            }
            if (i4 == 5 && !this.reactionButtons.isEmpty()) {
                int i9 = this.reactionButtons.get(0).y;
                int i10 = 0;
                for (int i11 = 0; i11 < this.reactionButtons.size(); i11++) {
                    if (this.reactionButtons.get(i11).y != i9) {
                        int i12 = i11 - 1;
                        int i13 = (i3 - this.reactionButtons.get(i12).x) + this.reactionButtons.get(i12).width;
                        while (i10 < i11) {
                            this.reactionButtons.get(i10).x += i13;
                            i10++;
                        }
                        i10 = i11;
                    }
                }
                int size = this.reactionButtons.size() - 1;
                int i14 = i3 - (this.reactionButtons.get(size).x + this.reactionButtons.get(size).width);
                while (i10 <= size) {
                    this.reactionButtons.get(i10).x += i14;
                    i10++;
                }
            }
            this.lastLineX = i5;
            if (i4 == 5) {
                this.width = i3;
            } else {
                this.width = i7;
            }
            this.height = i6 + (this.reactionButtons.size() == 0 ? 0 : AndroidUtilities.dp(26.0f));
            this.drawServiceShaderBackground = false;
        }
    }

    public void draw(Canvas canvas, float f, String str) {
        float f2;
        if (!this.isEmpty || !this.outButtons.isEmpty()) {
            float f3 = (float) this.x;
            float f4 = (float) this.y;
            if (this.isEmpty) {
                f3 = this.lastDrawnX;
                f4 = this.lastDrawnY;
            } else if (this.animateMove) {
                float f5 = 1.0f - f;
                f3 = (f3 * f) + (this.fromX * f5);
                f4 = (f4 * f) + (this.fromY * f5);
            }
            canvas.save();
            canvas.translate(f3, f4);
            for (int i = 0; i < this.reactionButtons.size(); i++) {
                ReactionButton reactionButton = this.reactionButtons.get(i);
                if (!reactionButton.reaction.equals(this.scrimViewReaction) && (str == null || reactionButton.reaction.equals(str))) {
                    canvas.save();
                    int i2 = reactionButton.x;
                    float f6 = (float) i2;
                    int i3 = reactionButton.y;
                    float f7 = (float) i3;
                    if (f != 1.0f && reactionButton.animationType == 3) {
                        float f8 = 1.0f - f;
                        f6 = (((float) reactionButton.animateFromX) * f8) + (((float) i2) * f);
                        f7 = (((float) i3) * f) + (((float) reactionButton.animateFromY) * f8);
                    }
                    canvas.translate(f6, f7);
                    boolean z = true;
                    if (f == 1.0f || reactionButton.animationType != 1) {
                        f2 = 1.0f;
                    } else {
                        float f9 = (f * 0.5f) + 0.5f;
                        canvas.scale(f9, f9, ((float) reactionButton.width) / 2.0f, ((float) reactionButton.height) / 2.0f);
                        f2 = f;
                    }
                    float var_ = reactionButton.animationType == 3 ? f : 1.0f;
                    if (str == null) {
                        z = false;
                    }
                    reactionButton.draw(canvas, var_, f2, z);
                    canvas.restore();
                }
            }
            for (int i4 = 0; i4 < this.outButtons.size(); i4++) {
                ReactionButton reactionButton2 = this.outButtons.get(i4);
                canvas.save();
                canvas.translate((float) reactionButton2.x, (float) reactionButton2.y);
                float var_ = 1.0f - f;
                float var_ = (var_ * 0.5f) + 0.5f;
                canvas.scale(var_, var_, ((float) reactionButton2.width) / 2.0f, ((float) reactionButton2.height) / 2.0f);
                this.outButtons.get(i4).draw(canvas, 1.0f, var_, false);
                canvas.restore();
            }
            canvas.restore();
        }
    }

    public void recordDrawingState() {
        this.lastDrawingReactionButtons.clear();
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.lastDrawingReactionButtons.put(this.reactionButtons.get(i).key, this.reactionButtons.get(i));
        }
        this.wasDrawn = !this.isEmpty;
        this.lastDrawnX = (float) this.x;
        this.lastDrawnY = (float) this.y;
        this.lastDrawnWidth = this.width;
        this.lastDrawTotalHeight = this.totalHeight;
    }

    public boolean animateChange() {
        if (this.messageObject == null) {
            return false;
        }
        this.lastDrawingReactionButtonsTmp.clear();
        for (int i = 0; i < this.outButtons.size(); i++) {
            this.outButtons.get(i).detach();
        }
        this.outButtons.clear();
        this.lastDrawingReactionButtonsTmp.putAll(this.lastDrawingReactionButtons);
        boolean z = false;
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            ReactionButton reactionButton = this.reactionButtons.get(i2);
            ReactionButton reactionButton2 = this.lastDrawingReactionButtonsTmp.get(reactionButton.key);
            if (!(reactionButton2 == null || reactionButton.isSmall == reactionButton2.isSmall)) {
                reactionButton2 = null;
            }
            if (reactionButton2 != null) {
                this.lastDrawingReactionButtonsTmp.remove(reactionButton.key);
                int i3 = reactionButton.x;
                int i4 = reactionButton2.x;
                if (i3 == i4 && reactionButton.y == reactionButton2.y && reactionButton.width == reactionButton2.width && reactionButton.count == reactionButton2.count && reactionButton.choosen == reactionButton2.choosen && reactionButton.avatarsDarawable == null && reactionButton2.avatarsDarawable == null) {
                    reactionButton.animationType = 0;
                } else {
                    reactionButton.animateFromX = i4;
                    reactionButton.animateFromY = reactionButton2.y;
                    reactionButton.animateFromWidth = reactionButton2.width;
                    reactionButton.fromTextColor = reactionButton2.lastDrawnTextColor;
                    reactionButton.fromBackgroundColor = reactionButton2.lastDrawnBackgroundColor;
                    reactionButton.animationType = 3;
                    int i5 = reactionButton.count;
                    int i6 = reactionButton2.count;
                    if (i5 != i6) {
                        reactionButton.counterDrawable.setCount(i6, false);
                        reactionButton.counterDrawable.setCount(reactionButton.count, true);
                    }
                    AvatarsDarawable avatarsDarawable = reactionButton.avatarsDarawable;
                    if (!(avatarsDarawable == null && reactionButton2.avatarsDarawable == null)) {
                        if (avatarsDarawable == null) {
                            reactionButton.setUsers(new ArrayList());
                        }
                        if (reactionButton2.avatarsDarawable == null) {
                            reactionButton2.setUsers(new ArrayList());
                        }
                        if (!equalsUsersList(reactionButton2.users, reactionButton.users)) {
                            reactionButton.avatarsDarawable.animateFromState(reactionButton2.avatarsDarawable, this.currentAccount, false);
                        }
                    }
                }
            } else {
                reactionButton.animationType = 1;
            }
            z = true;
        }
        if (!this.lastDrawingReactionButtonsTmp.isEmpty()) {
            this.outButtons.addAll(this.lastDrawingReactionButtonsTmp.values());
            for (int i7 = 0; i7 < this.outButtons.size(); i7++) {
                this.outButtons.get(i7).drawImage = this.outButtons.get(i7).lastImageDrawn;
                this.outButtons.get(i7).attach();
            }
            z = true;
        }
        if (this.wasDrawn) {
            float f = this.lastDrawnX;
            if (!(f == ((float) this.x) && this.lastDrawnY == ((float) this.y))) {
                this.animateMove = true;
                this.fromX = f;
                this.fromY = this.lastDrawnY;
                z = true;
            }
        }
        int i8 = this.lastDrawnWidth;
        if (i8 != this.width) {
            this.animateWidth = true;
            this.fromWidth = i8;
            z = true;
        }
        int i9 = this.lastDrawTotalHeight;
        if (i9 == this.totalHeight) {
            return z;
        }
        this.animateHeight = true;
        this.animateFromTotalHeight = i9;
        return true;
    }

    private boolean equalsUsersList(ArrayList<TLRPC$User> arrayList, ArrayList<TLRPC$User> arrayList2) {
        if (arrayList.size() != arrayList2.size()) {
            return false;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).id != arrayList.get(i).id) {
                return false;
            }
        }
        return true;
    }

    public void resetAnimation() {
        for (int i = 0; i < this.outButtons.size(); i++) {
            this.outButtons.get(i).detach();
        }
        this.outButtons.clear();
        this.animateMove = false;
        this.animateWidth = false;
        this.animateHeight = false;
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            this.reactionButtons.get(i2).animationType = 0;
        }
    }

    public ReactionButton getReactionButton(VisibleReaction visibleReaction) {
        String str = visibleReaction.emojicon;
        if (str == null) {
            str = Long.toString(visibleReaction.documentId);
        }
        if (this.isSmall) {
            HashMap<String, ReactionButton> hashMap = this.lastDrawingReactionButtons;
            ReactionButton reactionButton = hashMap.get(str + "_");
            if (reactionButton != null) {
                return reactionButton;
            }
        }
        return this.lastDrawingReactionButtons.get(str);
    }

    public void setScrimReaction(String str) {
        this.scrimViewReaction = str;
    }

    public class ReactionButton {
        public int animateFromWidth;
        public int animateFromX;
        public int animateFromY;
        AnimatedEmojiDrawable animatedEmojiDrawable;
        public int animationType;
        AvatarsDarawable avatarsDarawable;
        int backgroundColor;
        public boolean choosen;
        public int choosenOrder;
        int count;
        String countText;
        CounterView.CounterDrawable counterDrawable;
        public boolean drawImage = true;
        Rect drawingImageRect = new Rect();
        public int fromBackgroundColor;
        public int fromTextColor;
        public int height;
        ImageReceiver imageReceiver = new ImageReceiver();
        boolean isSelected;
        /* access modifiers changed from: private */
        public final boolean isSmall;
        public String key;
        int lastDrawnBackgroundColor;
        int lastDrawnTextColor;
        public boolean lastImageDrawn;
        TLRPC$Reaction reaction;
        /* access modifiers changed from: private */
        public final TLRPC$ReactionCount reactionCount;
        public int realCount;
        int serviceBackgroundColor;
        int serviceTextColor;
        int textColor;
        ArrayList<TLRPC$User> users;
        VisibleReaction visibleReaction;
        public int width;
        public int x;
        public int y;

        public ReactionButton(TLRPC$ReactionCount tLRPC$ReactionCount, boolean z) {
            this.counterDrawable = new CounterView.CounterDrawable(ReactionsLayoutInBubble.this.parentView, false, (Theme.ResourcesProvider) null);
            this.reactionCount = tLRPC$ReactionCount;
            TLRPC$Reaction tLRPC$Reaction = tLRPC$ReactionCount.reaction;
            this.reaction = tLRPC$Reaction;
            this.visibleReaction = VisibleReaction.fromTLReaction(tLRPC$Reaction);
            int i = tLRPC$ReactionCount.count;
            this.count = i;
            this.choosen = tLRPC$ReactionCount.chosen;
            this.realCount = i;
            this.choosenOrder = tLRPC$ReactionCount.chosen_order;
            this.isSmall = z;
            TLRPC$Reaction tLRPC$Reaction2 = this.reaction;
            if (tLRPC$Reaction2 instanceof TLRPC$TL_reactionEmoji) {
                this.key = ((TLRPC$TL_reactionEmoji) tLRPC$Reaction2).emoticon;
            } else if (tLRPC$Reaction2 instanceof TLRPC$TL_reactionCustomEmoji) {
                this.key = Long.toString(((TLRPC$TL_reactionCustomEmoji) tLRPC$Reaction2).document_id);
            } else {
                throw new RuntimeException("unsupported");
            }
            this.countText = Integer.toString(tLRPC$ReactionCount.count);
            this.imageReceiver.setParentView(ReactionsLayoutInBubble.this.parentView);
            this.isSelected = tLRPC$ReactionCount.chosen;
            CounterView.CounterDrawable counterDrawable2 = this.counterDrawable;
            counterDrawable2.updateVisibility = false;
            counterDrawable2.shortFormat = true;
            if (this.reaction != null) {
                VisibleReaction visibleReaction2 = this.visibleReaction;
                if (visibleReaction2.emojicon != null) {
                    TLRPC$TL_availableReaction tLRPC$TL_availableReaction = MediaDataController.getInstance(ReactionsLayoutInBubble.this.currentAccount).getReactionsMap().get(this.visibleReaction.emojicon);
                    if (tLRPC$TL_availableReaction != null) {
                        this.imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.center_icon), "40_40_lastframe", DocumentObject.getSvgThumb(tLRPC$TL_availableReaction.static_icon, "windowBackgroundGray", 1.0f), "webp", tLRPC$TL_availableReaction, 1);
                    }
                } else if (visibleReaction2.documentId != 0) {
                    this.animatedEmojiDrawable = new AnimatedEmojiDrawable(3, ReactionsLayoutInBubble.this.currentAccount, this.visibleReaction.documentId);
                }
            }
            this.counterDrawable.setSize(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(100.0f));
            this.counterDrawable.textPaint = ReactionsLayoutInBubble.textPaint;
            this.counterDrawable.setCount(this.count, false);
            this.counterDrawable.setType(2);
            this.counterDrawable.gravity = 3;
        }

        public void draw(Canvas canvas, float f, float f2, boolean z) {
            int i;
            int i2;
            Theme.MessageDrawable currentBackgroundDrawable;
            String str;
            Canvas canvas2 = canvas;
            float f3 = f;
            float f4 = f2;
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
            ImageReceiver imageReceiver2 = animatedEmojiDrawable2 != null ? animatedEmojiDrawable2.getImageReceiver() : this.imageReceiver;
            if (!this.isSmall || imageReceiver2 == null) {
                String str2 = "chat_outReactionButtonBackground";
                if (this.choosen) {
                    if (ReactionsLayoutInBubble.this.messageObject.isOutOwner()) {
                        str = str2;
                    } else {
                        str = "chat_inReactionButtonBackground";
                    }
                    this.backgroundColor = Theme.getColor(str, ReactionsLayoutInBubble.this.resourcesProvider);
                    this.textColor = Theme.getColor(ReactionsLayoutInBubble.this.messageObject.isOutOwner() ? "chat_outReactionButtonTextSelected" : "chat_inReactionButtonTextSelected", ReactionsLayoutInBubble.this.resourcesProvider);
                    if (!ReactionsLayoutInBubble.this.messageObject.isOutOwner()) {
                        str2 = "chat_inReactionButtonBackground";
                    }
                    this.serviceTextColor = Theme.getColor(str2, ReactionsLayoutInBubble.this.resourcesProvider);
                    this.serviceBackgroundColor = Theme.getColor(ReactionsLayoutInBubble.this.messageObject.isOutOwner() ? "chat_outBubble" : "chat_inBubble");
                } else {
                    this.textColor = Theme.getColor(ReactionsLayoutInBubble.this.messageObject.isOutOwner() ? "chat_outReactionButtonText" : "chat_inReactionButtonText", ReactionsLayoutInBubble.this.resourcesProvider);
                    if (!ReactionsLayoutInBubble.this.messageObject.isOutOwner()) {
                        str2 = "chat_inReactionButtonBackground";
                    }
                    int color = Theme.getColor(str2, ReactionsLayoutInBubble.this.resourcesProvider);
                    this.backgroundColor = color;
                    this.backgroundColor = ColorUtils.setAlphaComponent(color, (int) (((float) Color.alpha(color)) * 0.156f));
                    this.serviceTextColor = Theme.getColor("chat_serviceText", ReactionsLayoutInBubble.this.resourcesProvider);
                    this.serviceBackgroundColor = 0;
                }
                updateColors(f3);
                ReactionsLayoutInBubble.textPaint.setColor(this.lastDrawnTextColor);
                ReactionsLayoutInBubble.paint.setColor(this.lastDrawnBackgroundColor);
                if (f4 != 1.0f) {
                    ReactionsLayoutInBubble.textPaint.setAlpha((int) (((float) ReactionsLayoutInBubble.textPaint.getAlpha()) * f4));
                    ReactionsLayoutInBubble.paint.setAlpha((int) (((float) ReactionsLayoutInBubble.paint.getAlpha()) * f4));
                }
                if (imageReceiver2 != null) {
                    imageReceiver2.setAlpha(f4);
                }
                int i3 = this.width;
                if (f3 != 1.0f && this.animationType == 3) {
                    i3 = (int) ((((float) i3) * f3) + (((float) this.animateFromWidth) * (1.0f - f3)));
                }
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) i3, (float) this.height);
                float f5 = ((float) this.height) / 2.0f;
                ReactionsLayoutInBubble reactionsLayoutInBubble = ReactionsLayoutInBubble.this;
                if (reactionsLayoutInBubble.drawServiceShaderBackground) {
                    Paint access$500 = reactionsLayoutInBubble.getThemedPaint("paintChatActionBackground");
                    Paint paint = Theme.chat_actionBackgroundGradientDarkenPaint;
                    int alpha = access$500.getAlpha();
                    int alpha2 = paint.getAlpha();
                    access$500.setAlpha((int) (((float) alpha) * f4));
                    paint.setAlpha((int) (((float) alpha2) * f4));
                    canvas2.drawRoundRect(rectF, f5, f5, access$500);
                    if (ReactionsLayoutInBubble.this.hasGradientService()) {
                        canvas2.drawRoundRect(rectF, f5, f5, paint);
                    }
                    access$500.setAlpha(alpha);
                    paint.setAlpha(alpha2);
                }
                ReactionsLayoutInBubble reactionsLayoutInBubble2 = ReactionsLayoutInBubble.this;
                if (!reactionsLayoutInBubble2.drawServiceShaderBackground && z && (currentBackgroundDrawable = reactionsLayoutInBubble2.parentView.getCurrentBackgroundDrawable(false)) != null) {
                    canvas2.drawRoundRect(rectF, f5, f5, currentBackgroundDrawable.getPaint());
                }
                canvas2.drawRoundRect(rectF, f5, f5, ReactionsLayoutInBubble.paint);
                if (imageReceiver2 != null) {
                    if (this.animatedEmojiDrawable != null) {
                        i2 = AndroidUtilities.dp(24.0f);
                        i = AndroidUtilities.dp(6.0f);
                        imageReceiver2.setRoundRadius(AndroidUtilities.dp(6.0f));
                    } else {
                        int dp = AndroidUtilities.dp(20.0f);
                        i = AndroidUtilities.dp(8.0f);
                        imageReceiver2.setRoundRadius(0);
                        i2 = dp;
                    }
                    int i4 = (int) (((float) (this.height - i2)) / 2.0f);
                    this.drawingImageRect.set(i, i4, i + i2, i2 + i4);
                    imageReceiver2.setImageCoords(this.drawingImageRect);
                    drawImage(canvas2, f4);
                }
                if (!(this.count == 0 && this.counterDrawable.countChangeProgress == 1.0f)) {
                    canvas.save();
                    canvas2.translate((float) (AndroidUtilities.dp(8.0f) + AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(2.0f)), 0.0f);
                    this.counterDrawable.draw(canvas2);
                    canvas.restore();
                }
                if (this.avatarsDarawable != null) {
                    canvas.save();
                    canvas2.translate((float) (AndroidUtilities.dp(10.0f) + AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(2.0f)), 0.0f);
                    this.avatarsDarawable.setAlpha(f4);
                    this.avatarsDarawable.setTransitionProgress(f3);
                    this.avatarsDarawable.onDraw(canvas2);
                    canvas.restore();
                    return;
                }
                return;
            }
            imageReceiver2.setAlpha(f4);
            this.drawingImageRect.set(0, 0, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f));
            imageReceiver2.setImageCoords(this.drawingImageRect);
            imageReceiver2.setRoundRadius(0);
            drawImage(canvas2, f4);
        }

        private void updateColors(float f) {
            if (ReactionsLayoutInBubble.this.drawServiceShaderBackground) {
                this.lastDrawnTextColor = ColorUtils.blendARGB(this.fromTextColor, this.serviceTextColor, f);
                this.lastDrawnBackgroundColor = ColorUtils.blendARGB(this.fromBackgroundColor, this.serviceBackgroundColor, f);
                return;
            }
            this.lastDrawnTextColor = ColorUtils.blendARGB(this.fromTextColor, this.textColor, f);
            this.lastDrawnBackgroundColor = ColorUtils.blendARGB(this.fromBackgroundColor, this.backgroundColor, f);
        }

        private void drawImage(Canvas canvas, float f) {
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
            ImageReceiver imageReceiver2 = animatedEmojiDrawable2 != null ? animatedEmojiDrawable2.getImageReceiver() : this.imageReceiver;
            boolean z = false;
            if (!this.drawImage || (this.realCount <= 1 && ReactionsEffectOverlay.isPlaying(ReactionsLayoutInBubble.this.messageObject.getId(), ReactionsLayoutInBubble.this.messageObject.getGroupId(), this.visibleReaction) && this.isSelected)) {
                imageReceiver2.setAlpha(0.0f);
                imageReceiver2.draw(canvas);
                this.lastImageDrawn = false;
                return;
            }
            ImageReceiver imageReceiver3 = ReactionsLayoutInBubble.this.animatedReactions.get(this.visibleReaction);
            if (imageReceiver3 != null) {
                if (imageReceiver3.getLottieAnimation() == null || !imageReceiver3.getLottieAnimation().hasBitmap()) {
                    z = true;
                }
                if (f != 1.0f) {
                    imageReceiver3.setAlpha(f);
                    if (f <= 0.0f) {
                        imageReceiver3.onDetachedFromWindow();
                        ReactionsLayoutInBubble.this.animatedReactions.remove(this.visibleReaction);
                    }
                } else if (imageReceiver3.getLottieAnimation() != null && !imageReceiver3.getLottieAnimation().isRunning()) {
                    float alpha = imageReceiver3.getAlpha() - 0.08f;
                    if (alpha <= 0.0f) {
                        imageReceiver3.onDetachedFromWindow();
                        ReactionsLayoutInBubble.this.animatedReactions.remove(this.visibleReaction);
                    } else {
                        imageReceiver3.setAlpha(alpha);
                    }
                    ReactionsLayoutInBubble.this.parentView.invalidate();
                    z = true;
                }
                imageReceiver3.setImageCoords(imageReceiver2.getImageX() - (imageReceiver2.getImageWidth() / 2.0f), imageReceiver2.getImageY() - (imageReceiver2.getImageWidth() / 2.0f), imageReceiver2.getImageWidth() * 2.0f, imageReceiver2.getImageHeight() * 2.0f);
                imageReceiver3.draw(canvas);
            } else {
                z = true;
            }
            if (z) {
                imageReceiver2.draw(canvas);
            }
            this.lastImageDrawn = true;
        }

        public void setUsers(ArrayList<TLRPC$User> arrayList) {
            this.users = arrayList;
            if (arrayList != null) {
                Collections.sort(arrayList, ReactionsLayoutInBubble.usersComparator);
                if (this.avatarsDarawable == null) {
                    AvatarsDarawable avatarsDarawable2 = new AvatarsDarawable(ReactionsLayoutInBubble.this.parentView, false);
                    this.avatarsDarawable = avatarsDarawable2;
                    avatarsDarawable2.transitionDuration = 250;
                    Interpolator interpolator = ChatListItemAnimator.DEFAULT_INTERPOLATOR;
                    avatarsDarawable2.setSize(AndroidUtilities.dp(20.0f));
                    this.avatarsDarawable.width = AndroidUtilities.dp(100.0f);
                    AvatarsDarawable avatarsDarawable3 = this.avatarsDarawable;
                    avatarsDarawable3.height = this.height;
                    if (ReactionsLayoutInBubble.this.attached) {
                        avatarsDarawable3.onAttachedToWindow();
                    }
                }
                int i = 0;
                while (i < arrayList.size() && i != 3) {
                    this.avatarsDarawable.setObject(i, ReactionsLayoutInBubble.this.currentAccount, arrayList.get(i));
                    i++;
                }
                this.avatarsDarawable.commitTransition(false);
            }
        }

        public void attach() {
            ImageReceiver imageReceiver2 = this.imageReceiver;
            if (imageReceiver2 != null) {
                imageReceiver2.onAttachedToWindow();
            }
            AvatarsDarawable avatarsDarawable2 = this.avatarsDarawable;
            if (avatarsDarawable2 != null) {
                avatarsDarawable2.onAttachedToWindow();
            }
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.addView((View) ReactionsLayoutInBubble.this.parentView);
            }
        }

        public void detach() {
            ImageReceiver imageReceiver2 = this.imageReceiver;
            if (imageReceiver2 != null) {
                imageReceiver2.onDetachedFromWindow();
            }
            AvatarsDarawable avatarsDarawable2 = this.avatarsDarawable;
            if (avatarsDarawable2 != null) {
                avatarsDarawable2.onDetachedFromWindow();
            }
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.removeView((Drawable.Callback) ReactionsLayoutInBubble.this.parentView);
            }
        }
    }

    public boolean chekTouchEvent(MotionEvent motionEvent) {
        MessageObject messageObject2;
        TLRPC$Message tLRPC$Message;
        int i = 0;
        if (this.isEmpty || this.isSmall || (messageObject2 = this.messageObject) == null || (tLRPC$Message = messageObject2.messageOwner) == null || tLRPC$Message.reactions == null) {
            return false;
        }
        float x2 = motionEvent.getX() - ((float) this.x);
        float y2 = motionEvent.getY() - ((float) this.y);
        if (motionEvent.getAction() == 0) {
            int size = this.reactionButtons.size();
            while (true) {
                if (i >= size) {
                    break;
                } else if (x2 <= ((float) this.reactionButtons.get(i).x) || x2 >= ((float) (this.reactionButtons.get(i).x + this.reactionButtons.get(i).width)) || y2 <= ((float) this.reactionButtons.get(i).y) || y2 >= ((float) (this.reactionButtons.get(i).y + this.reactionButtons.get(i).height))) {
                    i++;
                } else {
                    this.lastX = motionEvent.getX();
                    this.lastY = motionEvent.getY();
                    this.lastSelectedButton = this.reactionButtons.get(i);
                    Runnable runnable = this.longPressRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                        this.longPressRunnable = null;
                    }
                    ReactionButton reactionButton = this.lastSelectedButton;
                    MessageObject messageObject3 = this.messageObject;
                    if (messageObject3.messageOwner.reactions.can_see_list || messageObject3.getDialogId() >= 0) {
                        ReactionsLayoutInBubble$$ExternalSyntheticLambda0 reactionsLayoutInBubble$$ExternalSyntheticLambda0 = new ReactionsLayoutInBubble$$ExternalSyntheticLambda0(this, reactionButton);
                        this.longPressRunnable = reactionsLayoutInBubble$$ExternalSyntheticLambda0;
                        AndroidUtilities.runOnUIThread(reactionsLayoutInBubble$$ExternalSyntheticLambda0, (long) ViewConfiguration.getLongPressTimeout());
                    }
                    this.pressed = true;
                }
            }
        } else if (motionEvent.getAction() == 2) {
            if ((this.pressed && Math.abs(motionEvent.getX() - this.lastX) > this.touchSlop) || Math.abs(motionEvent.getY() - this.lastY) > this.touchSlop) {
                this.pressed = false;
                this.lastSelectedButton = null;
                Runnable runnable2 = this.longPressRunnable;
                if (runnable2 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable2);
                    this.longPressRunnable = null;
                }
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            Runnable runnable3 = this.longPressRunnable;
            if (runnable3 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable3);
                this.longPressRunnable = null;
            }
            if (this.pressed && this.lastSelectedButton != null && motionEvent.getAction() == 1 && this.parentView.getDelegate() != null) {
                this.parentView.getDelegate().didPressReaction(this.parentView, this.lastSelectedButton.reactionCount, false);
            }
            this.pressed = false;
            this.lastSelectedButton = null;
        }
        return this.pressed;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$chekTouchEvent$1(ReactionButton reactionButton) {
        this.parentView.getDelegate().didPressReaction(this.parentView, reactionButton.reactionCount, true);
        this.longPressRunnable = null;
    }

    /* access modifiers changed from: private */
    public boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        return resourcesProvider2 != null ? resourcesProvider2.hasGradientService() : Theme.hasGradientService();
    }

    /* access modifiers changed from: private */
    public Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Paint paint2 = resourcesProvider2 != null ? resourcesProvider2.getPaint(str) : null;
        return paint2 != null ? paint2 : Theme.getThemePaint(str);
    }

    public float getCurrentWidth(float f) {
        if (this.animateWidth) {
            return (((float) this.fromWidth) * (1.0f - f)) + (((float) this.width) * f);
        }
        return (float) this.width;
    }

    public float getCurrentTotalHeight(float f) {
        if (this.animateHeight) {
            return (((float) this.animateFromTotalHeight) * (1.0f - f)) + (((float) this.totalHeight) * f);
        }
        return (float) this.totalHeight;
    }

    private static class ButtonsComparator implements Comparator<ReactionButton> {
        int currentAccount;
        long dialogId;

        private ButtonsComparator() {
        }

        public int compare(ReactionButton reactionButton, ReactionButton reactionButton2) {
            int i;
            int i2;
            int i3;
            int i4;
            if (this.dialogId >= 0) {
                boolean z = reactionButton.isSelected;
                if (z != reactionButton2.isSelected) {
                    return z ? -1 : 1;
                }
                if (z && (i3 = reactionButton.choosenOrder) != (i4 = reactionButton2.choosenOrder)) {
                    return i3 - i4;
                }
                i = reactionButton.reactionCount.lastDrawnPosition;
                i2 = reactionButton2.reactionCount.lastDrawnPosition;
            } else {
                int i5 = reactionButton.realCount;
                int i6 = reactionButton2.realCount;
                if (i5 != i6) {
                    return i6 - i5;
                }
                i = reactionButton.reactionCount.lastDrawnPosition;
                i2 = reactionButton2.reactionCount.lastDrawnPosition;
            }
            return i - i2;
        }
    }

    public void onAttachToWindow() {
        this.attached = true;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).attach();
        }
    }

    public void onDetachFromWindow() {
        this.attached = false;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).detach();
        }
        if (!this.animatedReactions.isEmpty()) {
            for (ImageReceiver onDetachedFromWindow : this.animatedReactions.values()) {
                onDetachedFromWindow.onDetachedFromWindow();
            }
        }
        this.animatedReactions.clear();
    }

    public void animateReaction(VisibleReaction visibleReaction) {
        if (visibleReaction.documentId == 0 && this.animatedReactions.get(visibleReaction) == null) {
            ImageReceiver imageReceiver = new ImageReceiver();
            imageReceiver.setParentView(this.parentView);
            int i = animationUniq;
            animationUniq = i + 1;
            imageReceiver.setUniqKeyPrefix(Integer.toString(i));
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(visibleReaction.emojicon);
            if (tLRPC$TL_availableReaction != null) {
                imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.center_icon), "40_40_nolimit", (Drawable) null, "tgs", tLRPC$TL_availableReaction, 1);
            }
            imageReceiver.setAutoRepeat(0);
            imageReceiver.onAttachedToWindow();
            this.animatedReactions.put(visibleReaction, imageReceiver);
        }
    }

    public static class VisibleReaction {
        public long documentId;
        public String emojicon;

        public static VisibleReaction fromTLReaction(TLRPC$Reaction tLRPC$Reaction) {
            VisibleReaction visibleReaction = new VisibleReaction();
            if (tLRPC$Reaction instanceof TLRPC$TL_reactionEmoji) {
                visibleReaction.emojicon = ((TLRPC$TL_reactionEmoji) tLRPC$Reaction).emoticon;
            } else if (tLRPC$Reaction instanceof TLRPC$TL_reactionCustomEmoji) {
                visibleReaction.documentId = ((TLRPC$TL_reactionCustomEmoji) tLRPC$Reaction).document_id;
            }
            return visibleReaction;
        }

        public static VisibleReaction fromEmojicon(TLRPC$TL_availableReaction tLRPC$TL_availableReaction) {
            VisibleReaction visibleReaction = new VisibleReaction();
            visibleReaction.emojicon = tLRPC$TL_availableReaction.reaction;
            return visibleReaction;
        }

        public static VisibleReaction fromEmojicon(String str) {
            VisibleReaction visibleReaction = new VisibleReaction();
            if (str.startsWith("animated_")) {
                try {
                    visibleReaction.documentId = Long.parseLong(str.substring(9));
                } catch (Exception unused) {
                    visibleReaction.emojicon = str;
                }
            } else {
                visibleReaction.emojicon = str;
            }
            return visibleReaction;
        }

        public static VisibleReaction fromCustomEmoji(Long l) {
            VisibleReaction visibleReaction = new VisibleReaction();
            visibleReaction.documentId = l.longValue();
            return visibleReaction;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || VisibleReaction.class != obj.getClass()) {
                return false;
            }
            VisibleReaction visibleReaction = (VisibleReaction) obj;
            if (this.documentId != visibleReaction.documentId || !ObjectsCompat$$ExternalSyntheticBackport0.m(this.emojicon, visibleReaction.emojicon)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Arrays.hashCode(new Object[]{this.emojicon, Long.valueOf(this.documentId)});
        }
    }
}
