package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class JoinCallByUrlAlert extends BottomSheet {
    private boolean joinAfterDismiss;

    /* access modifiers changed from: protected */
    public void onJoin() {
        throw null;
    }

    public static class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
        private TextView textView;

        public BottomSheetCell(Context context) {
            super(context);
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }
    }

    public JoinCallByUrlAlert(Context context, TLRPC$Chat tLRPC$Chat) {
        super(context, true);
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        FrameLayout frameLayout = new FrameLayout(context);
        setCustomView(frameLayout);
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setRoundRadius(AndroidUtilities.dp(45.0f));
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(90, 90.0f, 49, 0.0f, 29.0f, 0.0f, 0.0f));
        backupImageView.setImage(ImageLocation.getForChat(tLRPC$Chat, false), "50_50", (Drawable) new AvatarDrawable(tLRPC$Chat), (Object) tLRPC$Chat);
        TextView textView = new TextView(context);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 18.0f);
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 17.0f, 147.0f, 17.0f, 0.0f));
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor("dialogTextGray3"));
        textView2.setGravity(1);
        frameLayout.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, 49, 30.0f, 178.0f, 30.0f, 44.0f));
        ChatObject.Call groupCall = AccountInstance.getInstance(this.currentAccount).getMessagesController().getGroupCall(tLRPC$Chat.id, false);
        if (groupCall != null) {
            if (TextUtils.isEmpty(groupCall.call.title)) {
                textView.setText(tLRPC$Chat.title);
            } else {
                textView.setText(groupCall.call.title);
            }
            int i = groupCall.call.participants_count;
            if (i == 0) {
                textView2.setText(LocaleController.getString("NoOneJoinedYet", NUM));
            } else {
                textView2.setText(LocaleController.formatPluralString("Members", i));
            }
        } else {
            textView.setText(tLRPC$Chat.title);
            textView2.setText(LocaleController.getString("NoOneJoinedYet", NUM));
        }
        BottomSheetCell bottomSheetCell = new BottomSheetCell(context);
        bottomSheetCell.setBackground((Drawable) null);
        bottomSheetCell.setText(LocaleController.getString("VoipGroupJoinVoiceChatUrl", NUM));
        bottomSheetCell.background.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                JoinCallByUrlAlert.this.lambda$new$0$JoinCallByUrlAlert(view);
            }
        });
        frameLayout.addView(bottomSheetCell, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 223.0f, 0.0f, 0.0f));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$JoinCallByUrlAlert(View view) {
        this.joinAfterDismiss = true;
        dismiss();
    }

    public void dismissInternal() {
        super.dismissInternal();
        if (this.joinAfterDismiss) {
            onJoin();
        }
    }
}
