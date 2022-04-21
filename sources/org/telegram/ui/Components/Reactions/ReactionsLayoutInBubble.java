package org.telegram.ui.Components.Reactions;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.ArrayList;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AvatarsDarawable;
import org.telegram.ui.Components.CounterView;

public class ReactionsLayoutInBubble {
    private static final int ANIMATION_TYPE_IN = 1;
    private static final int ANIMATION_TYPE_MOVE = 3;
    private static final int ANIMATION_TYPE_OUT = 2;
    private static int animationUniq;
    private static final ButtonsComparator comparator = new ButtonsComparator();
    /* access modifiers changed from: private */
    public static Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public static TextPaint textPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public static final Comparator<TLRPC.User> usersComparator = ReactionsLayoutInBubble$$ExternalSyntheticLambda1.INSTANCE;
    private int animateFromTotalHeight;
    private boolean animateHeight;
    private boolean animateMove;
    private boolean animateWidth;
    HashMap<String, ImageReceiver> animatedReactions = new HashMap<>();
    boolean attached;
    int availableWidth;
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

    static /* synthetic */ int lambda$static$0(TLRPC.User user1, TLRPC.User user2) {
        return (int) (user1.id - user2.id);
    }

    public ReactionsLayoutInBubble(ChatMessageCell parentView2) {
        this.parentView = parentView2;
        this.currentAccount = UserConfig.selectedAccount;
        paint.setColor(Theme.getColor("chat_inLoader"));
        textPaint.setColor(Theme.getColor("featuredStickers_buttonText"));
        textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.touchSlop = (float) ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();
    }

    public void setMessage(MessageObject messageObject2, boolean isSmall2, Theme.ResourcesProvider resourcesProvider2) {
        this.resourcesProvider = resourcesProvider2;
        this.isSmall = isSmall2;
        this.messageObject = messageObject2;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).detach();
        }
        this.hasUnreadReactions = false;
        this.reactionButtons.clear();
        if (messageObject2 != null) {
            if (messageObject2.messageOwner.reactions != null && messageObject2.messageOwner.reactions.results != null) {
                int totalCount = 0;
                for (int i2 = 0; i2 < messageObject2.messageOwner.reactions.results.size(); i2++) {
                    totalCount += ((TLRPC.TL_reactionCount) messageObject2.messageOwner.reactions.results.get(i2)).count;
                }
                int i3 = 0;
                while (true) {
                    if (i3 >= messageObject2.messageOwner.reactions.results.size()) {
                        break;
                    }
                    TLRPC.TL_reactionCount reactionCount = (TLRPC.TL_reactionCount) messageObject2.messageOwner.reactions.results.get(i3);
                    ReactionButton button = new ReactionButton(reactionCount);
                    this.reactionButtons.add(button);
                    if (!isSmall2 && messageObject2.messageOwner.reactions.recent_reactions != null) {
                        ArrayList<TLRPC.User> users = null;
                        if (reactionCount.count <= 3 && totalCount <= 3) {
                            for (int j = 0; j < messageObject2.messageOwner.reactions.recent_reactions.size(); j++) {
                                TLRPC.TL_messagePeerReaction reccent = (TLRPC.TL_messagePeerReaction) messageObject2.messageOwner.reactions.recent_reactions.get(j);
                                if (reccent.reaction.equals(reactionCount.reaction) && MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(reccent.peer_id))) != null) {
                                    if (users == null) {
                                        users = new ArrayList<>();
                                    }
                                    users.add(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(reccent.peer_id))));
                                }
                            }
                            button.setUsers(users);
                            if (users != null && !users.isEmpty()) {
                                button.count = 0;
                                button.counterDrawable.setCount(0, false);
                            }
                        }
                    }
                    if (!isSmall2 || reactionCount.count <= 1 || !reactionCount.chosen) {
                        if (isSmall2 && i3 == 2) {
                            break;
                        }
                        if (this.attached) {
                            button.attach();
                        }
                        i3++;
                    } else {
                        this.reactionButtons.add(new ReactionButton(reactionCount));
                        this.reactionButtons.get(0).isSelected = false;
                        this.reactionButtons.get(1).isSelected = true;
                        this.reactionButtons.get(0).realCount = 1;
                        this.reactionButtons.get(1).realCount = 1;
                        this.reactionButtons.get(1).key += "_";
                        break;
                    }
                }
            }
            if (!isSmall2) {
                ButtonsComparator buttonsComparator = comparator;
                buttonsComparator.currentAccount = this.currentAccount;
                Collections.sort(this.reactionButtons, buttonsComparator);
            }
            this.hasUnreadReactions = MessageObject.hasUnreadReactions(messageObject2.messageOwner);
        }
        this.isEmpty = this.reactionButtons.isEmpty();
    }

    public void measure(int availableWidth2, int gravity) {
        int i = availableWidth2;
        int i2 = gravity;
        this.height = 0;
        this.width = 0;
        this.positionOffsetY = 0;
        this.totalHeight = 0;
        if (!this.isEmpty) {
            this.availableWidth = i;
            int maxWidth = 0;
            int currentX = 0;
            int currentY = 0;
            for (int i3 = 0; i3 < this.reactionButtons.size(); i3++) {
                ReactionButton button = this.reactionButtons.get(i3);
                if (this.isSmall) {
                    button.width = AndroidUtilities.dp(14.0f);
                    button.height = AndroidUtilities.dp(14.0f);
                } else {
                    button.width = AndroidUtilities.dp(8.0f) + AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(4.0f);
                    if (button.avatarsDarawable == null || button.users.size() <= 0) {
                        button.width = (int) (((float) button.width) + button.counterDrawable.textPaint.measureText(button.countText) + ((float) AndroidUtilities.dp(8.0f)));
                    } else {
                        button.users.size();
                        button.width = (int) (((float) button.width) + ((float) (AndroidUtilities.dp(2.0f) + (AndroidUtilities.dp(20.0f) * 1))) + (((float) (AndroidUtilities.dp(20.0f) * (button.users.size() > 1 ? button.users.size() - 1 : 0))) * 0.8f) + ((float) AndroidUtilities.dp(1.0f)));
                        button.avatarsDarawable.height = AndroidUtilities.dp(26.0f);
                    }
                    button.height = AndroidUtilities.dp(26.0f);
                }
                if (button.width + currentX > i) {
                    currentX = 0;
                    currentY += button.height + AndroidUtilities.dp(4.0f);
                }
                button.x = currentX;
                button.y = currentY;
                currentX += button.width + AndroidUtilities.dp(4.0f);
                if (currentX > maxWidth) {
                    maxWidth = currentX;
                }
            }
            if (i2 == 5 && !this.reactionButtons.isEmpty()) {
                int fromP = 0;
                int startY = this.reactionButtons.get(0).y;
                for (int i4 = 0; i4 < this.reactionButtons.size(); i4++) {
                    if (this.reactionButtons.get(i4).y != startY) {
                        int lineOffset = (i - this.reactionButtons.get(i4 - 1).x) + this.reactionButtons.get(i4 - 1).width;
                        for (int k = fromP; k < i4; k++) {
                            this.reactionButtons.get(k).x += lineOffset;
                        }
                        fromP = i4;
                    }
                }
                int last = this.reactionButtons.size() - 1;
                if (fromP != last) {
                    int lineOffset2 = i - (this.reactionButtons.get(last).x + this.reactionButtons.get(last).width);
                    for (int k2 = fromP; k2 <= last; k2++) {
                        this.reactionButtons.get(k2).x += lineOffset2;
                    }
                }
            }
            this.lastLineX = currentX;
            if (i2 == 5) {
                this.width = i;
            } else {
                this.width = maxWidth;
            }
            this.height = (this.reactionButtons.size() == 0 ? 0 : AndroidUtilities.dp(26.0f)) + currentY;
            this.drawServiceShaderBackground = false;
        }
    }

    public void draw(Canvas canvas, float animationProgress, String drawOnlyReaction) {
        Canvas canvas2 = canvas;
        String str = drawOnlyReaction;
        if (!this.isEmpty || !this.outButtons.isEmpty()) {
            float totalX = (float) this.x;
            float totalY = (float) this.y;
            if (this.isEmpty) {
                totalX = this.lastDrawnX;
                totalY = this.lastDrawnY;
            } else if (this.animateMove) {
                totalX = (totalX * animationProgress) + (this.fromX * (1.0f - animationProgress));
                totalY = (totalY * animationProgress) + (this.fromY * (1.0f - animationProgress));
            }
            canvas.save();
            canvas2.translate(totalX, totalY);
            for (int i = 0; i < this.reactionButtons.size(); i++) {
                ReactionButton reactionButton = this.reactionButtons.get(i);
                if (!reactionButton.reaction.equals(this.scrimViewReaction) && (str == null || reactionButton.reaction.equals(str))) {
                    canvas.save();
                    float x2 = (float) reactionButton.x;
                    float y2 = (float) reactionButton.y;
                    if (animationProgress != 1.0f && reactionButton.animationType == 3) {
                        x2 = (((float) reactionButton.x) * animationProgress) + (((float) reactionButton.animateFromX) * (1.0f - animationProgress));
                        y2 = (((float) reactionButton.y) * animationProgress) + (((float) reactionButton.animateFromY) * (1.0f - animationProgress));
                    }
                    canvas2.translate(x2, y2);
                    float alpha = 1.0f;
                    if (animationProgress != 1.0f && reactionButton.animationType == 1) {
                        float s = (animationProgress * 0.5f) + 0.5f;
                        alpha = animationProgress;
                        canvas2.scale(s, s, ((float) reactionButton.width) / 2.0f, ((float) reactionButton.height) / 2.0f);
                    }
                    reactionButton.draw(canvas2, reactionButton.animationType == 3 ? animationProgress : 1.0f, alpha, str != null);
                    canvas.restore();
                }
            }
            for (int i2 = 0; i2 < this.outButtons.size(); i2++) {
                ReactionButton reactionButton2 = this.outButtons.get(i2);
                canvas.save();
                canvas2.translate((float) reactionButton2.x, (float) reactionButton2.y);
                float s2 = ((1.0f - animationProgress) * 0.5f) + 0.5f;
                canvas2.scale(s2, s2, ((float) reactionButton2.width) / 2.0f, ((float) reactionButton2.height) / 2.0f);
                this.outButtons.get(i2).draw(canvas2, 1.0f, 1.0f - animationProgress, false);
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
        this.wasDrawn = this.isEmpty ^ 1;
        this.lastDrawnX = (float) this.x;
        this.lastDrawnY = (float) this.y;
        this.lastDrawnWidth = this.width;
        this.lastDrawTotalHeight = this.totalHeight;
    }

    public boolean animateChange() {
        if (this.messageObject == null) {
            return false;
        }
        boolean changed = false;
        this.lastDrawingReactionButtonsTmp.clear();
        for (int i = 0; i < this.outButtons.size(); i++) {
            this.outButtons.get(i).detach();
        }
        this.outButtons.clear();
        this.lastDrawingReactionButtonsTmp.putAll(this.lastDrawingReactionButtons);
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            ReactionButton button = this.reactionButtons.get(i2);
            ReactionButton lastButton = this.lastDrawingReactionButtonsTmp.remove(button.key);
            if (lastButton == null) {
                changed = true;
                button.animationType = 1;
            } else if (button.x == lastButton.x && button.y == lastButton.y && button.width == lastButton.width && button.count == lastButton.count && button.backgroundColor == lastButton.backgroundColor && button.avatarsDarawable == null && lastButton.avatarsDarawable == null) {
                button.animationType = 0;
            } else {
                button.animateFromX = lastButton.x;
                button.animateFromY = lastButton.y;
                button.animateFromWidth = lastButton.width;
                button.fromTextColor = lastButton.lastDrawnTextColor;
                button.fromBackgroundColor = lastButton.lastDrawnBackgroundColor;
                button.animationType = 3;
                if (button.count != lastButton.count) {
                    button.counterDrawable.setCount(lastButton.count, false);
                    button.counterDrawable.setCount(button.count, true);
                }
                if (!(button.avatarsDarawable == null && lastButton.avatarsDarawable == null)) {
                    if (button.avatarsDarawable == null) {
                        button.setUsers(new ArrayList());
                    }
                    if (lastButton.avatarsDarawable == null) {
                        lastButton.setUsers(new ArrayList());
                    }
                    button.avatarsDarawable.animateFromState(lastButton.avatarsDarawable, this.currentAccount, false);
                }
                changed = true;
            }
        }
        if (!this.lastDrawingReactionButtonsTmp.isEmpty()) {
            changed = true;
            this.outButtons.addAll(this.lastDrawingReactionButtonsTmp.values());
            for (int i3 = 0; i3 < this.outButtons.size(); i3++) {
                this.outButtons.get(i3).drawImage = this.outButtons.get(i3).lastImageDrawn;
                this.outButtons.get(i3).attach();
            }
        }
        if (this.wasDrawn != 0) {
            float f = this.lastDrawnX;
            if (!(f == ((float) this.x) && this.lastDrawnY == ((float) this.y))) {
                this.animateMove = true;
                this.fromX = f;
                this.fromY = this.lastDrawnY;
                changed = true;
            }
        }
        int i4 = this.lastDrawnWidth;
        if (i4 != this.width) {
            this.animateWidth = true;
            this.fromWidth = i4;
            changed = true;
        }
        int i5 = this.lastDrawTotalHeight;
        if (i5 == this.totalHeight) {
            return changed;
        }
        this.animateHeight = true;
        this.animateFromTotalHeight = i5;
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

    public ReactionButton getReactionButton(String reaction) {
        if (this.isSmall) {
            HashMap<String, ReactionButton> hashMap = this.lastDrawingReactionButtons;
            ReactionButton button = hashMap.get(reaction + "_");
            if (button != null) {
                return button;
            }
        }
        return this.lastDrawingReactionButtons.get(reaction);
    }

    public void setScrimReaction(String scrimViewReaction2) {
        this.scrimViewReaction = scrimViewReaction2;
    }

    public class ReactionButton {
        public int animateFromWidth;
        public int animateFromX;
        public int animateFromY;
        public int animationType;
        AvatarsDarawable avatarsDarawable;
        int backgroundColor;
        int count;
        String countText;
        CounterView.CounterDrawable counterDrawable;
        public boolean drawImage = true;
        public int fromBackgroundColor;
        public int fromTextColor;
        public int height;
        ImageReceiver imageReceiver = new ImageReceiver();
        boolean isSelected;
        public String key;
        int lastDrawnBackgroundColor;
        int lastDrawnTextColor;
        public boolean lastImageDrawn;
        String reaction;
        /* access modifiers changed from: private */
        public final TLRPC.TL_reactionCount reactionCount;
        public int realCount;
        int serviceBackgroundColor;
        int serviceTextColor;
        int textColor;
        ArrayList<TLRPC.User> users;
        public boolean wasDrawn;
        public int width;
        public int x;
        public int y;

        public ReactionButton(TLRPC.TL_reactionCount reactionCount2) {
            TLRPC.TL_availableReaction r;
            String str;
            this.counterDrawable = new CounterView.CounterDrawable(ReactionsLayoutInBubble.this.parentView, false, (Theme.ResourcesProvider) null);
            this.reactionCount = reactionCount2;
            this.reaction = reactionCount2.reaction;
            this.count = reactionCount2.count;
            this.realCount = reactionCount2.count;
            this.key = this.reaction;
            this.countText = Integer.toString(reactionCount2.count);
            this.imageReceiver.setParentView(ReactionsLayoutInBubble.this.parentView);
            this.isSelected = reactionCount2.chosen;
            this.counterDrawable.updateVisibility = false;
            this.counterDrawable.shortFormat = true;
            String str2 = "chat_outReactionButtonBackground";
            if (reactionCount2.chosen) {
                if (ReactionsLayoutInBubble.this.messageObject.isOutOwner()) {
                    str = str2;
                } else {
                    str = "chat_inReactionButtonBackground";
                }
                this.backgroundColor = Theme.getColor(str, ReactionsLayoutInBubble.this.resourcesProvider);
                this.textColor = Theme.getColor(ReactionsLayoutInBubble.this.messageObject.isOutOwner() ? "chat_outReactionButtonTextSelected" : "chat_inReactionButtonTextSelected", ReactionsLayoutInBubble.this.resourcesProvider);
                this.serviceTextColor = Theme.getColor(!ReactionsLayoutInBubble.this.messageObject.isOutOwner() ? "chat_inReactionButtonBackground" : str2, ReactionsLayoutInBubble.this.resourcesProvider);
                this.serviceBackgroundColor = Theme.getColor(ReactionsLayoutInBubble.this.messageObject.isOutOwner() ? "chat_outBubble" : "chat_inBubble");
            } else {
                this.textColor = Theme.getColor(ReactionsLayoutInBubble.this.messageObject.isOutOwner() ? "chat_outReactionButtonText" : "chat_inReactionButtonText", ReactionsLayoutInBubble.this.resourcesProvider);
                int color = Theme.getColor(!ReactionsLayoutInBubble.this.messageObject.isOutOwner() ? "chat_inReactionButtonBackground" : str2, ReactionsLayoutInBubble.this.resourcesProvider);
                this.backgroundColor = color;
                this.backgroundColor = ColorUtils.setAlphaComponent(color, (int) (((float) Color.alpha(color)) * 0.156f));
                this.serviceTextColor = Theme.getColor("chat_serviceText", ReactionsLayoutInBubble.this.resourcesProvider);
                this.serviceBackgroundColor = 0;
            }
            if (!(this.reaction == null || (r = MediaDataController.getInstance(ReactionsLayoutInBubble.this.currentAccount).getReactionsMap().get(this.reaction)) == null)) {
                this.imageReceiver.setImage(ImageLocation.getForDocument(r.center_icon), "40_40_lastframe", DocumentObject.getSvgThumb(r.static_icon, "windowBackgroundGray", 1.0f), "webp", r, 1);
            }
            this.counterDrawable.setSize(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(100.0f));
            this.counterDrawable.textPaint = ReactionsLayoutInBubble.textPaint;
            this.counterDrawable.setCount(this.count, false);
            this.counterDrawable.setType(2);
            this.counterDrawable.gravity = 3;
        }

        public void draw(Canvas canvas, float progress, float alpha, boolean drawOverlayScrim) {
            Theme.MessageDrawable messageBackground;
            Canvas canvas2 = canvas;
            float f = progress;
            float f2 = alpha;
            this.wasDrawn = true;
            if (ReactionsLayoutInBubble.this.isSmall) {
                this.imageReceiver.setAlpha(f2);
                this.imageReceiver.setImageCoords(0.0f, 0.0f, (float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(14.0f));
                drawImage(canvas2, f2);
                return;
            }
            updateColors(f);
            ReactionsLayoutInBubble.textPaint.setColor(this.lastDrawnTextColor);
            ReactionsLayoutInBubble.paint.setColor(this.lastDrawnBackgroundColor);
            if (f2 != 1.0f) {
                ReactionsLayoutInBubble.textPaint.setAlpha((int) (((float) ReactionsLayoutInBubble.textPaint.getAlpha()) * f2));
                ReactionsLayoutInBubble.paint.setAlpha((int) (((float) ReactionsLayoutInBubble.paint.getAlpha()) * f2));
            }
            this.imageReceiver.setAlpha(f2);
            int w = this.width;
            if (f != 1.0f && this.animationType == 3) {
                w = (int) ((((float) this.width) * f) + (((float) this.animateFromWidth) * (1.0f - f)));
            }
            AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) w, (float) this.height);
            float rad = ((float) this.height) / 2.0f;
            if (ReactionsLayoutInBubble.this.drawServiceShaderBackground) {
                Paint paint1 = ReactionsLayoutInBubble.this.getThemedPaint("paintChatActionBackground");
                Paint paint2 = Theme.chat_actionBackgroundGradientDarkenPaint;
                int oldAlpha = paint1.getAlpha();
                int oldAlpha2 = paint2.getAlpha();
                paint1.setAlpha((int) (((float) oldAlpha) * f2));
                paint2.setAlpha((int) (((float) oldAlpha2) * f2));
                canvas2.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, paint1);
                if (ReactionsLayoutInBubble.this.hasGradientService()) {
                    canvas2.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, paint2);
                }
                paint1.setAlpha(oldAlpha);
                paint2.setAlpha(oldAlpha2);
            }
            if (!ReactionsLayoutInBubble.this.drawServiceShaderBackground && drawOverlayScrim && (messageBackground = ReactionsLayoutInBubble.this.parentView.getCurrentBackgroundDrawable(false)) != null) {
                canvas2.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, messageBackground.getPaint());
            }
            canvas2.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, ReactionsLayoutInBubble.paint);
            this.imageReceiver.setImageCoords((float) AndroidUtilities.dp(8.0f), ((float) (this.height - AndroidUtilities.dp(20.0f))) / 2.0f, (float) AndroidUtilities.dp(20.0f), (float) AndroidUtilities.dp(20.0f));
            drawImage(canvas2, f2);
            if (!(this.count == 0 && this.counterDrawable.countChangeProgress == 1.0f)) {
                canvas.save();
                canvas2.translate((float) (AndroidUtilities.dp(8.0f) + AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(2.0f)), 0.0f);
                this.counterDrawable.draw(canvas2);
                canvas.restore();
            }
            if (this.avatarsDarawable != null) {
                canvas.save();
                canvas2.translate((float) (AndroidUtilities.dp(10.0f) + AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(2.0f)), 0.0f);
                this.avatarsDarawable.setAlpha(f2);
                this.avatarsDarawable.setTransitionProgress(f);
                this.avatarsDarawable.onDraw(canvas2);
                canvas.restore();
            }
        }

        private void updateColors(float progress) {
            if (ReactionsLayoutInBubble.this.drawServiceShaderBackground) {
                this.lastDrawnTextColor = ColorUtils.blendARGB(this.fromTextColor, this.serviceTextColor, progress);
                this.lastDrawnBackgroundColor = ColorUtils.blendARGB(this.fromBackgroundColor, this.serviceBackgroundColor, progress);
                return;
            }
            this.lastDrawnTextColor = ColorUtils.blendARGB(this.fromTextColor, this.textColor, progress);
            this.lastDrawnBackgroundColor = ColorUtils.blendARGB(this.fromBackgroundColor, this.backgroundColor, progress);
        }

        private void drawImage(Canvas canvas, float alpha) {
            if (!this.drawImage || (this.realCount <= 1 && ReactionsEffectOverlay.isPlaying(ReactionsLayoutInBubble.this.messageObject.getId(), ReactionsLayoutInBubble.this.messageObject.getGroupId(), this.reaction) && this.isSelected)) {
                this.imageReceiver.setAlpha(0.0f);
                this.imageReceiver.draw(canvas);
                this.lastImageDrawn = false;
                return;
            }
            ImageReceiver imageReceiver2 = ReactionsLayoutInBubble.this.animatedReactions.get(this.reaction);
            boolean drawStaticImage = true;
            if (imageReceiver2 != null) {
                if (imageReceiver2.getLottieAnimation() != null && imageReceiver2.getLottieAnimation().hasBitmap()) {
                    drawStaticImage = false;
                }
                if (alpha != 1.0f) {
                    imageReceiver2.setAlpha(alpha);
                    if (alpha <= 0.0f) {
                        imageReceiver2.onDetachedFromWindow();
                        ReactionsLayoutInBubble.this.animatedReactions.remove(this.reaction);
                    }
                } else if (imageReceiver2.getLottieAnimation() != null && !imageReceiver2.getLottieAnimation().isRunning()) {
                    drawStaticImage = true;
                    float alpha1 = imageReceiver2.getAlpha() - 0.08f;
                    if (alpha1 <= 0.0f) {
                        imageReceiver2.onDetachedFromWindow();
                        ReactionsLayoutInBubble.this.animatedReactions.remove(this.reaction);
                    } else {
                        imageReceiver2.setAlpha(alpha1);
                    }
                    ReactionsLayoutInBubble.this.parentView.invalidate();
                }
                imageReceiver2.setImageCoords(this.imageReceiver.getImageX() - (this.imageReceiver.getImageWidth() / 2.0f), this.imageReceiver.getImageY() - (this.imageReceiver.getImageWidth() / 2.0f), this.imageReceiver.getImageWidth() * 2.0f, this.imageReceiver.getImageHeight() * 2.0f);
                imageReceiver2.draw(canvas);
            }
            if (drawStaticImage) {
                this.imageReceiver.draw(canvas);
            }
            this.lastImageDrawn = true;
        }

        public void setUsers(ArrayList<TLRPC.User> users2) {
            this.users = users2;
            if (users2 != null) {
                Collections.sort(users2, ReactionsLayoutInBubble.usersComparator);
                if (this.avatarsDarawable == null) {
                    AvatarsDarawable avatarsDarawable2 = new AvatarsDarawable(ReactionsLayoutInBubble.this.parentView, false);
                    this.avatarsDarawable = avatarsDarawable2;
                    avatarsDarawable2.transitionDuration = 250;
                    this.avatarsDarawable.transitionInterpolator = ChatListItemAnimator.DEFAULT_INTERPOLATOR;
                    this.avatarsDarawable.setSize(AndroidUtilities.dp(20.0f));
                    this.avatarsDarawable.width = AndroidUtilities.dp(100.0f);
                    this.avatarsDarawable.height = this.height;
                    if (ReactionsLayoutInBubble.this.attached) {
                        this.avatarsDarawable.onAttachedToWindow();
                    }
                }
                int i = 0;
                while (i < users2.size() && i != 3) {
                    this.avatarsDarawable.setObject(i, ReactionsLayoutInBubble.this.currentAccount, users2.get(i));
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
        }
    }

    public boolean chekTouchEvent(MotionEvent event) {
        MessageObject messageObject2;
        if (this.isEmpty || this.isSmall || (messageObject2 = this.messageObject) == null || messageObject2.messageOwner == null || this.messageObject.messageOwner.reactions == null) {
            return false;
        }
        float x2 = event.getX() - ((float) this.x);
        float y2 = event.getY() - ((float) this.y);
        if (event.getAction() == 0) {
            int i = 0;
            int n = this.reactionButtons.size();
            while (true) {
                if (i >= n) {
                    break;
                } else if (x2 <= ((float) this.reactionButtons.get(i).x) || x2 >= ((float) (this.reactionButtons.get(i).x + this.reactionButtons.get(i).width)) || y2 <= ((float) this.reactionButtons.get(i).y) || y2 >= ((float) (this.reactionButtons.get(i).y + this.reactionButtons.get(i).height))) {
                    i++;
                } else {
                    this.lastX = event.getX();
                    this.lastY = event.getY();
                    this.lastSelectedButton = this.reactionButtons.get(i);
                    Runnable runnable = this.longPressRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                        this.longPressRunnable = null;
                    }
                    ReactionButton selectedButtonFinal = this.lastSelectedButton;
                    if (this.messageObject.messageOwner.reactions.can_see_list) {
                        ReactionsLayoutInBubble$$ExternalSyntheticLambda0 reactionsLayoutInBubble$$ExternalSyntheticLambda0 = new ReactionsLayoutInBubble$$ExternalSyntheticLambda0(this, selectedButtonFinal);
                        this.longPressRunnable = reactionsLayoutInBubble$$ExternalSyntheticLambda0;
                        AndroidUtilities.runOnUIThread(reactionsLayoutInBubble$$ExternalSyntheticLambda0, (long) ViewConfiguration.getLongPressTimeout());
                    }
                    this.pressed = true;
                }
            }
        } else if (event.getAction() == 2) {
            if ((this.pressed && Math.abs(event.getX() - this.lastX) > this.touchSlop) || Math.abs(event.getY() - this.lastY) > this.touchSlop) {
                this.pressed = false;
                this.lastSelectedButton = null;
                Runnable runnable2 = this.longPressRunnable;
                if (runnable2 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable2);
                    this.longPressRunnable = null;
                }
            }
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            Runnable runnable3 = this.longPressRunnable;
            if (runnable3 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable3);
                this.longPressRunnable = null;
            }
            if (this.pressed && this.lastSelectedButton != null && event.getAction() == 1 && this.parentView.getDelegate() != null) {
                this.parentView.getDelegate().didPressReaction(this.parentView, this.lastSelectedButton.reactionCount, false);
            }
            this.pressed = false;
            this.lastSelectedButton = null;
        }
        return this.pressed;
    }

    /* renamed from: lambda$chekTouchEvent$1$org-telegram-ui-Components-Reactions-ReactionsLayoutInBubble  reason: not valid java name */
    public /* synthetic */ void m4284xed008495(ReactionButton selectedButtonFinal) {
        this.parentView.getDelegate().didPressReaction(this.parentView, selectedButtonFinal.reactionCount, true);
        this.longPressRunnable = null;
    }

    /* access modifiers changed from: private */
    public boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        return resourcesProvider2 != null ? resourcesProvider2.hasGradientService() : Theme.hasGradientService();
    }

    /* access modifiers changed from: private */
    public Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Paint paint2 = resourcesProvider2 != null ? resourcesProvider2.getPaint(paintKey) : null;
        return paint2 != null ? paint2 : Theme.getThemePaint(paintKey);
    }

    public float getCurrentWidth(float transitionProgress) {
        if (this.animateWidth) {
            return (((float) this.fromWidth) * (1.0f - transitionProgress)) + (((float) this.width) * transitionProgress);
        }
        return (float) this.width;
    }

    public float getCurrentTotalHeight(float transitionProgress) {
        if (this.animateHeight) {
            return (((float) this.animateFromTotalHeight) * (1.0f - transitionProgress)) + (((float) this.totalHeight) * transitionProgress);
        }
        return (float) this.totalHeight;
    }

    private static class ButtonsComparator implements Comparator<ReactionButton> {
        int currentAccount;

        private ButtonsComparator() {
        }

        public int compare(ReactionButton o1, ReactionButton o2) {
            if (o1.realCount != o2.realCount) {
                return o2.realCount - o1.realCount;
            }
            TLRPC.TL_availableReaction availableReaction1 = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(o1.reaction);
            TLRPC.TL_availableReaction availableReaction2 = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(o2.reaction);
            if (availableReaction1 == null || availableReaction2 == null) {
                return 0;
            }
            return availableReaction1.positionInList - availableReaction2.positionInList;
        }
    }

    public void onAttachToWindow() {
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).attach();
        }
    }

    public void onDetachFromWindow() {
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).detach();
        }
        if (!this.animatedReactions.isEmpty()) {
            for (ImageReceiver imageReceiver : this.animatedReactions.values()) {
                imageReceiver.onDetachedFromWindow();
            }
        }
        this.animatedReactions.clear();
    }

    public void animateReaction(String reaction) {
        TLRPC.TL_availableReaction r;
        if (this.animatedReactions.get(reaction) == null) {
            ImageReceiver imageReceiver = new ImageReceiver();
            imageReceiver.setParentView(this.parentView);
            int i = animationUniq;
            animationUniq = i + 1;
            imageReceiver.setUniqKeyPrefix(Integer.toString(i));
            if (!(reaction == null || (r = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(reaction)) == null)) {
                imageReceiver.setImage(ImageLocation.getForDocument(r.center_icon), "40_40_nolimit", (Drawable) null, "tgs", r, 1);
            }
            imageReceiver.setAutoRepeat(0);
            imageReceiver.onAttachedToWindow();
            this.animatedReactions.put(reaction, imageReceiver);
        }
    }
}
