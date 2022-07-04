package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.StickerImageView;

public class SuggestClearDatabaseBottomSheet extends BottomSheet {
    private static SuggestClearDatabaseBottomSheet dialog;
    BaseFragment fragment;

    public static void show(BaseFragment fragment2) {
        if (dialog == null) {
            SuggestClearDatabaseBottomSheet suggestClearDatabaseBottomSheet = new SuggestClearDatabaseBottomSheet(fragment2);
            dialog = suggestClearDatabaseBottomSheet;
            suggestClearDatabaseBottomSheet.show();
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    private SuggestClearDatabaseBottomSheet(BaseFragment fragment2) {
        super(fragment2.getParentActivity(), false);
        BaseFragment baseFragment = fragment2;
        this.fragment = baseFragment;
        Context context = fragment2.getParentActivity();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        StickerImageView imageView = new StickerImageView(context, this.currentAccount);
        imageView.setStickerNum(7);
        imageView.getImageReceiver().setAutoRepeat(1);
        linearLayout.addView(imageView, LayoutHelper.createLinear(144, 144, 1, 0, 16, 0, 0));
        TextView title = new TextView(context);
        title.setGravity(8388611);
        title.setTextColor(Theme.getColor("dialogTextBlack"));
        title.setTextSize(1, 20.0f);
        title.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        title.setText(LocaleController.getString("SuggestClearDatabaseTitle", NUM));
        linearLayout.addView(title, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 30.0f, 21.0f, 0.0f));
        TextView description = new TextView(context);
        description.setGravity(8388611);
        description.setTextSize(1, 15.0f);
        description.setTextColor(Theme.getColor("dialogTextBlack"));
        description.setText(AndroidUtilities.replaceTags(LocaleController.formatString("SuggestClearDatabaseMessage", NUM, AndroidUtilities.formatFileSize(fragment2.getMessagesStorage().getDatabaseSize()))));
        linearLayout.addView(description, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
        TextView buttonTextView = new TextView(context);
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        buttonTextView.setText(LocaleController.getString("ClearLocalDatabase", NUM));
        buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), 120)));
        linearLayout.addView(buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
        buttonTextView.setOnClickListener(new SuggestClearDatabaseBottomSheet$$ExternalSyntheticLambda1(this, baseFragment));
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-SuggestClearDatabaseBottomSheet  reason: not valid java name */
    public /* synthetic */ void m4650lambda$new$1$orgtelegramuiSuggestClearDatabaseBottomSheet(BaseFragment fragment2, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment2.getParentActivity());
        builder.setTitle(LocaleController.getString("LocalDatabaseClearTextTitle", NUM));
        builder.setMessage(LocaleController.getString("LocalDatabaseClearText", NUM));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("CacheClear", NUM), new SuggestClearDatabaseBottomSheet$$ExternalSyntheticLambda0(this, fragment2));
        AlertDialog alertDialog = builder.create();
        fragment2.showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-SuggestClearDatabaseBottomSheet  reason: not valid java name */
    public /* synthetic */ void m4649lambda$new$0$orgtelegramuiSuggestClearDatabaseBottomSheet(BaseFragment fragment2, DialogInterface dialogInterface, int i) {
        if (fragment2.getParentActivity() != null) {
            MessagesController.getInstance(this.currentAccount).clearQueryTime();
            fragment2.getMessagesStorage().clearLocalDatabase();
        }
    }

    public void dismiss() {
        super.dismiss();
        dialog = null;
    }

    public static void dismissDialog() {
        SuggestClearDatabaseBottomSheet suggestClearDatabaseBottomSheet = dialog;
        if (suggestClearDatabaseBottomSheet != null) {
            suggestClearDatabaseBottomSheet.dismiss();
            dialog = null;
        }
    }
}
