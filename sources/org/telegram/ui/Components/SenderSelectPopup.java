package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;

public class SenderSelectPopup extends ActionBarPopupWindow {
    public static final int AVATAR_SIZE_DP = 40;
    private static final float SCALE_START = 0.25f;
    private static final int SHADOW_DURATION = 150;
    public static final float SPRING_STIFFNESS = 750.0f;
    private TLRPC.ChatFull chatFull;
    private boolean clicked;
    public View dimView;
    private boolean dismissed;
    /* access modifiers changed from: private */
    public View headerShadow;
    public TextView headerText;
    /* access modifiers changed from: private */
    public Boolean isHeaderShadowVisible;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    public LinearLayout recyclerContainer;
    private RecyclerListView recyclerView;
    protected boolean runningCustomSprings;
    private FrameLayout scrimPopupContainerLayout;
    private TLRPC.TL_channels_sendAsPeers sendAsPeers;
    protected List<SpringAnimation> springAnimations = new ArrayList();

    public interface OnSelectCallback {
        void onPeerSelected(RecyclerView recyclerView, SenderView senderView, TLRPC.Peer peer);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SenderSelectPopup(Context context, ChatActivity parentFragment, MessagesController messagesController, TLRPC.ChatFull chatFull2, TLRPC.TL_channels_sendAsPeers sendAsPeers2, OnSelectCallback selectCallback) {
        super(context);
        Context context2 = context;
        final TLRPC.ChatFull chatFull3 = chatFull2;
        TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers = sendAsPeers2;
        this.chatFull = chatFull3;
        this.sendAsPeers = tL_channels_sendAsPeers;
        BackButtonFrameLayout backButtonFrameLayout = new BackButtonFrameLayout(context2);
        this.scrimPopupContainerLayout = backButtonFrameLayout;
        backButtonFrameLayout.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f));
        setContentView(this.scrimPopupContainerLayout);
        setWidth(-2);
        setHeight(-2);
        setBackgroundDrawable((Drawable) null);
        Drawable shadowDrawable = ContextCompat.getDrawable(context2, NUM).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultSubmenuBackground"), PorterDuff.Mode.MULTIPLY));
        this.scrimPopupContainerLayout.setBackground(shadowDrawable);
        Rect padding = new Rect();
        shadowDrawable.getPadding(padding);
        this.scrimPopupContainerLayout.setPadding(padding.left, padding.top, padding.right, padding.bottom);
        View view = new View(context2);
        this.dimView = view;
        view.setBackgroundColor(NUM);
        final int maxHeight = AndroidUtilities.dp(450.0f);
        final int maxWidth = (int) (((float) parentFragment.contentView.getWidth()) * 0.75f);
        AnonymousClass1 r10 = new LinearLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(widthMeasureSpec), maxWidth), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(heightMeasureSpec), maxHeight), View.MeasureSpec.getMode(heightMeasureSpec)));
            }

            /* access modifiers changed from: protected */
            public int getSuggestedMinimumWidth() {
                return AndroidUtilities.dp(260.0f);
            }
        };
        this.recyclerContainer = r10;
        r10.setOrientation(1);
        TextView textView = new TextView(context2);
        this.headerText = textView;
        textView.setTextColor(Theme.getColor("dialogTextBlue"));
        this.headerText.setTextSize(1, 16.0f);
        this.headerText.setText(LocaleController.getString("SendMessageAsTitle", NUM));
        this.headerText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 1);
        int dp = AndroidUtilities.dp(18.0f);
        this.headerText.setPadding(dp, AndroidUtilities.dp(12.0f), dp, AndroidUtilities.dp(12.0f));
        this.recyclerContainer.addView(this.headerText);
        FrameLayout recyclerFrameLayout = new FrameLayout(context2);
        final List<TLRPC.Peer> peers = tL_channels_sendAsPeers.peers;
        this.recyclerView = new RecyclerListView(context2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2);
        this.layoutManager = linearLayoutManager;
        this.recyclerView.setLayoutManager(linearLayoutManager);
        final MessagesController messagesController2 = messagesController;
        this.recyclerView.setAdapter(new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(new SenderView(parent.getContext()));
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                SenderView senderView = (SenderView) holder.itemView;
                TLRPC.Peer peer = (TLRPC.Peer) peers.get(position);
                long peerId = 0;
                if (peer.channel_id != 0) {
                    peerId = -peer.channel_id;
                }
                if (peerId == 0 && peer.user_id != 0) {
                    peerId = peer.user_id;
                }
                boolean z = true;
                if (peerId < 0) {
                    TLRPC.Chat chat = messagesController2.getChat(Long.valueOf(-peerId));
                    if (chat != null) {
                        senderView.title.setText(chat.title);
                        senderView.subtitle.setText(LocaleController.formatPluralString((!ChatObject.isChannel(chat) || chat.megagroup) ? "Members" : "Subscribers", chat.participants_count));
                        senderView.avatar.setAvatar(chat);
                    }
                    SimpleAvatarView simpleAvatarView = senderView.avatar;
                    if (chatFull3.default_send_as == null || chatFull3.default_send_as.channel_id != peer.channel_id) {
                        z = false;
                    }
                    simpleAvatarView.setSelected(z, false);
                    return;
                }
                TLRPC.User user = messagesController2.getUser(Long.valueOf(peerId));
                if (user != null) {
                    senderView.title.setText(UserObject.getUserName(user));
                    senderView.subtitle.setText(LocaleController.getString("VoipGroupPersonalAccount", NUM));
                    senderView.avatar.setAvatar(user);
                }
                SimpleAvatarView simpleAvatarView2 = senderView.avatar;
                if (chatFull3.default_send_as == null || chatFull3.default_send_as.user_id != peer.user_id) {
                    z = false;
                }
                simpleAvatarView2.setSelected(z, false);
            }

            public int getItemCount() {
                return peers.size();
            }
        });
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean show = SenderSelectPopup.this.layoutManager.findFirstCompletelyVisibleItemPosition() != 0;
                if (SenderSelectPopup.this.isHeaderShadowVisible == null || show != SenderSelectPopup.this.isHeaderShadowVisible.booleanValue()) {
                    SenderSelectPopup.this.headerShadow.animate().cancel();
                    SenderSelectPopup.this.headerShadow.animate().alpha(show ? 1.0f : 0.0f).setDuration(150).start();
                    Boolean unused = SenderSelectPopup.this.isHeaderShadowVisible = Boolean.valueOf(show);
                }
            }
        });
        this.recyclerView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SenderSelectPopup$$ExternalSyntheticLambda8(this, selectCallback, peers));
        recyclerFrameLayout.addView(this.recyclerView);
        this.headerShadow = new View(context2);
        Drawable shadowDrawable2 = ContextCompat.getDrawable(context2, NUM);
        shadowDrawable2.setAlpha(153);
        this.headerShadow.setBackground(shadowDrawable2);
        this.headerShadow.setAlpha(0.0f);
        recyclerFrameLayout.addView(this.headerShadow, LayoutHelper.createFrame(-1, 4.0f));
        this.recyclerContainer.addView(recyclerFrameLayout, LayoutHelper.createFrame(-1, -2.0f));
        this.scrimPopupContainerLayout.addView(this.recyclerContainer);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SenderSelectPopup  reason: not valid java name */
    public /* synthetic */ void m4321lambda$new$0$orgtelegramuiComponentsSenderSelectPopup(OnSelectCallback selectCallback, List peers, View view, int position) {
        if (!this.clicked) {
            this.clicked = true;
            selectCallback.onPeerSelected(this.recyclerView, (SenderView) view, (TLRPC.Peer) peers.get(position));
        }
    }

    public void dismiss() {
        if (!this.dismissed) {
            this.dismissed = true;
            super.dismiss();
        }
    }

    public void startShowAnimation() {
        int itemHeight;
        for (SpringAnimation springAnimation : this.springAnimations) {
            springAnimation.cancel();
        }
        this.springAnimations.clear();
        this.scrimPopupContainerLayout.setPivotX((float) AndroidUtilities.dp(8.0f));
        FrameLayout frameLayout = this.scrimPopupContainerLayout;
        frameLayout.setPivotY((float) (frameLayout.getMeasuredHeight() - AndroidUtilities.dp(8.0f)));
        this.recyclerContainer.setPivotX(0.0f);
        this.recyclerContainer.setPivotY(0.0f);
        List<TLRPC.Peer> peers = this.sendAsPeers.peers;
        TLRPC.Peer defPeer = this.chatFull.default_send_as != null ? this.chatFull.default_send_as : null;
        if (defPeer != null) {
            int itemHeight2 = AndroidUtilities.dp(54.0f);
            int totalRecyclerHeight = peers.size() * itemHeight2;
            int i = 0;
            while (true) {
                if (i >= peers.size()) {
                    break;
                }
                TLRPC.Peer p = peers.get(i);
                if (p.channel_id != 0) {
                    itemHeight = itemHeight2;
                    if (p.channel_id == defPeer.channel_id) {
                        break;
                    }
                } else {
                    itemHeight = itemHeight2;
                }
                if ((p.user_id != 0 && p.user_id == defPeer.user_id) || (p.chat_id != 0 && p.chat_id == defPeer.chat_id)) {
                    break;
                }
                i++;
                itemHeight2 = itemHeight;
            }
            int off = 0;
            if (i != peers.size() - 1 && this.recyclerView.getMeasuredHeight() < totalRecyclerHeight) {
                off = this.recyclerView.getMeasuredHeight() % itemHeight;
            }
            this.layoutManager.scrollToPositionWithOffset(i, AndroidUtilities.dp(7.0f) + off + (totalRecyclerHeight - ((peers.size() - 2) * itemHeight)));
            if (this.recyclerView.computeVerticalScrollOffset() > 0) {
                this.headerShadow.animate().cancel();
                this.headerShadow.animate().alpha(1.0f).setDuration(150).start();
            }
        }
        this.scrimPopupContainerLayout.setScaleX(0.25f);
        this.scrimPopupContainerLayout.setScaleY(0.25f);
        this.recyclerContainer.setAlpha(0.25f);
        this.dimView.setAlpha(0.0f);
        for (SpringAnimation animation : Arrays.asList(new SpringAnimation[]{(SpringAnimation) new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_X).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new SenderSelectPopup$$ExternalSyntheticLambda6(this)), (SpringAnimation) new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new SenderSelectPopup$$ExternalSyntheticLambda7(this)), new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.ALPHA).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.recyclerContainer, DynamicAnimation.ALPHA).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.dimView, DynamicAnimation.ALPHA).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f))})) {
            this.springAnimations.add(animation);
            animation.addEndListener(new SenderSelectPopup$$ExternalSyntheticLambda3(this, animation));
            animation.start();
        }
    }

    /* renamed from: lambda$startShowAnimation$1$org-telegram-ui-Components-SenderSelectPopup  reason: not valid java name */
    public /* synthetic */ void m4327xdbb89fef(DynamicAnimation animation, float value, float velocity) {
        this.recyclerContainer.setScaleX(1.0f / value);
    }

    /* renamed from: lambda$startShowAnimation$2$org-telegram-ui-Components-SenderSelectPopup  reason: not valid java name */
    public /* synthetic */ void m4328x95302d8e(DynamicAnimation animation, float value, float velocity) {
        this.recyclerContainer.setScaleY(1.0f / value);
    }

    /* renamed from: lambda$startShowAnimation$3$org-telegram-ui-Components-SenderSelectPopup  reason: not valid java name */
    public /* synthetic */ void m4329x4ea7bb2d(SpringAnimation animation, DynamicAnimation animation1, boolean canceled, float value, float velocity) {
        if (!canceled) {
            this.springAnimations.remove(animation);
            animation1.cancel();
        }
    }

    public void startDismissAnimation(SpringAnimation... animations) {
        for (SpringAnimation springAnimation : this.springAnimations) {
            springAnimation.cancel();
        }
        this.springAnimations.clear();
        this.scrimPopupContainerLayout.setPivotX((float) AndroidUtilities.dp(8.0f));
        FrameLayout frameLayout = this.scrimPopupContainerLayout;
        frameLayout.setPivotY((float) (frameLayout.getMeasuredHeight() - AndroidUtilities.dp(8.0f)));
        this.recyclerContainer.setPivotX(0.0f);
        this.recyclerContainer.setPivotY(0.0f);
        this.scrimPopupContainerLayout.setScaleX(1.0f);
        this.scrimPopupContainerLayout.setScaleY(1.0f);
        this.recyclerContainer.setAlpha(1.0f);
        this.dimView.setAlpha(1.0f);
        List<SpringAnimation> newSpringAnimations = new ArrayList<>();
        boolean z = true;
        newSpringAnimations.addAll(Arrays.asList(new SpringAnimation[]{(SpringAnimation) new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_X).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new SenderSelectPopup$$ExternalSyntheticLambda4(this)), (SpringAnimation) new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new SenderSelectPopup$$ExternalSyntheticLambda5(this)), new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.ALPHA).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.recyclerContainer, DynamicAnimation.ALPHA).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)), (SpringAnimation) new SpringAnimation(this.dimView, DynamicAnimation.ALPHA).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addEndListener(new SenderSelectPopup$$ExternalSyntheticLambda0(this))}));
        newSpringAnimations.addAll(Arrays.asList(animations));
        if (animations.length <= 0) {
            z = false;
        }
        this.runningCustomSprings = z;
        newSpringAnimations.get(0).addEndListener(new SenderSelectPopup$$ExternalSyntheticLambda1(this));
        for (SpringAnimation springAnimation2 : newSpringAnimations) {
            this.springAnimations.add(springAnimation2);
            springAnimation2.addEndListener(new SenderSelectPopup$$ExternalSyntheticLambda2(this, springAnimation2));
            springAnimation2.start();
        }
    }

    /* renamed from: lambda$startDismissAnimation$4$org-telegram-ui-Components-SenderSelectPopup  reason: not valid java name */
    public /* synthetic */ void m4322x4cc0e76f(DynamicAnimation animation, float value, float velocity) {
        this.recyclerContainer.setScaleX(1.0f / value);
    }

    /* renamed from: lambda$startDismissAnimation$5$org-telegram-ui-Components-SenderSelectPopup  reason: not valid java name */
    public /* synthetic */ void m4323x638750e(DynamicAnimation animation, float value, float velocity) {
        this.recyclerContainer.setScaleY(1.0f / value);
    }

    /* renamed from: lambda$startDismissAnimation$6$org-telegram-ui-Components-SenderSelectPopup  reason: not valid java name */
    public /* synthetic */ void m4324xbfb002ad(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (this.dimView.getParent() != null) {
            ((ViewGroup) this.dimView.getParent()).removeView(this.dimView);
        }
        dismiss();
    }

    /* renamed from: lambda$startDismissAnimation$7$org-telegram-ui-Components-SenderSelectPopup  reason: not valid java name */
    public /* synthetic */ void m4325x7927904c(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        this.runningCustomSprings = false;
    }

    /* renamed from: lambda$startDismissAnimation$8$org-telegram-ui-Components-SenderSelectPopup  reason: not valid java name */
    public /* synthetic */ void m4326x329f1deb(SpringAnimation springAnimation, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (!canceled) {
            this.springAnimations.remove(springAnimation);
            animation.cancel();
        }
    }

    public static final class SenderView extends LinearLayout {
        public final SimpleAvatarView avatar;
        public final TextView subtitle;
        public final TextView title;

        public SenderView(Context context) {
            super(context);
            setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            setOrientation(0);
            setGravity(16);
            int padding = AndroidUtilities.dp(14.0f);
            setPadding(padding, padding / 2, padding, padding / 2);
            SimpleAvatarView simpleAvatarView = new SimpleAvatarView(context);
            this.avatar = simpleAvatarView;
            addView(simpleAvatarView, LayoutHelper.createFrame(40, 40.0f));
            LinearLayout textRow = new LinearLayout(context);
            textRow.setOrientation(1);
            addView(textRow, LayoutHelper.createLinear(0, -1, 1.0f, 12, 0, 0, 0));
            TextView textView = new TextView(context);
            this.title = textView;
            textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView.setTextSize(1, 16.0f);
            textView.setTag(textView);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textRow.addView(textView);
            TextView textView2 = new TextView(context);
            this.subtitle = textView2;
            textView2.setTextColor(ColorUtils.setAlphaComponent(Theme.getColor("actionBarDefaultSubmenuItem"), 102));
            textView2.setTextSize(1, 14.0f);
            textView2.setTag(textView2);
            textView2.setMaxLines(1);
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            textRow.addView(textView2);
        }
    }

    private class BackButtonFrameLayout extends FrameLayout {
        public BackButtonFrameLayout(Context context) {
            super(context);
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == 4 && event.getRepeatCount() == 0 && SenderSelectPopup.this.isShowing()) {
                SenderSelectPopup.this.dismiss();
            }
            return super.dispatchKeyEvent(event);
        }
    }
}
