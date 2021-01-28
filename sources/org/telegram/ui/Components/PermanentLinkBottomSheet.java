package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;

public class PermanentLinkBottomSheet extends BottomSheet {
    private int chatId;
    private final RLottieImageView imageView;
    TLRPC$TL_chatInviteExported invite;
    private final LinkActionView linkActionView;
    boolean linkGenerating;
    RLottieDrawable linkIcon;
    private final TextView subtitle;
    private final TextView titleView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PermanentLinkBottomSheet(Context context, boolean z, BaseFragment baseFragment, TLRPC$ChatFull tLRPC$ChatFull, int i) {
        super(context, z);
        TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported;
        Context context2 = context;
        TLRPC$ChatFull tLRPC$ChatFull2 = tLRPC$ChatFull;
        int i2 = i;
        this.chatId = i2;
        setAllowNestedScroll(true);
        setApplyBottomPadding(false);
        LinkActionView linkActionView2 = new LinkActionView(context, baseFragment, this, i2, true);
        this.linkActionView = linkActionView2;
        linkActionView2.setPermanent(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(90.0f), AndroidUtilities.dp(90.0f), false, (int[]) null);
        this.linkIcon = rLottieDrawable;
        rLottieDrawable.setCustomEndFrame(42);
        rLottieImageView.setAnimation(this.linkIcon);
        linkActionView2.setUsers(0, (ArrayList<TLRPC$User>) null);
        linkActionView2.setPublic(true);
        TextView textView = new TextView(context2);
        this.titleView = textView;
        textView.setText(LocaleController.getString("InviteLink", NUM));
        textView.setTextSize(24.0f);
        textView.setGravity(1);
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        TextView textView2 = new TextView(context2);
        this.subtitle = textView2;
        textView2.setText(LocaleController.getString("LinkInfo", NUM));
        textView2.setTextSize(14.0f);
        textView2.setGravity(1);
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(90, 90, 1, 0, 24, 0, 0));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 60, 16, 60, 0));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 60, 16, 60, 0));
        linearLayout.addView(linkActionView2, LayoutHelper.createLinear(-1, -2));
        NestedScrollView nestedScrollView = new NestedScrollView(context2);
        nestedScrollView.setVerticalScrollBarEnabled(false);
        nestedScrollView.addView(linearLayout);
        setCustomView(nestedScrollView);
        if (tLRPC$ChatFull2 == null || (tLRPC$TL_chatInviteExported = tLRPC$ChatFull2.exported_invite) == null) {
            generateLink();
        } else {
            linkActionView2.setLink(tLRPC$TL_chatInviteExported.link);
        }
        lambda$getThemeDescriptions$3();
    }

    private void generateLink() {
        if (!this.linkGenerating) {
            this.linkGenerating = true;
            TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
            tLRPC$TL_messages_exportChatInvite.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_exportChatInvite, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PermanentLinkBottomSheet.this.lambda$generateLink$1$PermanentLinkBottomSheet(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$generateLink$1 */
    public /* synthetic */ void lambda$generateLink$1$PermanentLinkBottomSheet(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PermanentLinkBottomSheet.this.lambda$null$0$PermanentLinkBottomSheet(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$PermanentLinkBottomSheet(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            this.invite = (TLRPC$TL_chatInviteExported) tLObject;
            TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chatId);
            if (chatFull != null) {
                chatFull.exported_invite = this.invite;
            }
            this.linkActionView.setLink(this.invite.link);
        }
        this.linkGenerating = false;
    }

    public void show() {
        super.show();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                PermanentLinkBottomSheet.this.lambda$show$2$PermanentLinkBottomSheet();
            }
        }, 50);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$show$2 */
    public /* synthetic */ void lambda$show$2$PermanentLinkBottomSheet() {
        this.linkIcon.start();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$PermanentLinkBottomSheet$uRoXyJwz4Er5fYsaK3mjtq3ebQ8 r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                PermanentLinkBottomSheet.this.lambda$getThemeDescriptions$3$PermanentLinkBottomSheet();
            }
        };
        arrayList.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        $$Lambda$PermanentLinkBottomSheet$uRoXyJwz4Er5fYsaK3mjtq3ebQ8 r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "windowBackgroundWhiteBlueText"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: updateColors */
    public void lambda$getThemeDescriptions$3() {
        this.imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(90.0f), Theme.getColor("featuredStickers_addButton")));
        int color = Theme.getColor("featuredStickers_buttonText");
        this.linkIcon.setLayerColor("Top.**", color);
        this.linkIcon.setLayerColor("Bottom.**", color);
        this.linkIcon.setLayerColor("Center.**", color);
        this.linkActionView.updateColors();
        setBackgroundColor(Theme.getColor("dialogBackground"));
    }
}
