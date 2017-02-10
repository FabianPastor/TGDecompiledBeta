package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogCell.CustomDialog;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ThemePreviewActivity extends BaseFragment implements NotificationCenterDelegate {
    private boolean applied;
    private ThemeInfo applyingTheme;
    private DialogsAdapter dialogsAdapter;
    private RecyclerListView listView;
    private File themeFile;
    private ViewPager viewPager;

    public class DialogsAdapter extends SelectionAdapter {
        private ArrayList<CustomDialog> dialogs = new ArrayList();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            int date = (int) (System.currentTimeMillis() / 1000);
            CustomDialog customDialog = new CustomDialog();
            customDialog.name = "Eva Summer";
            customDialog.message = "Reminds me of a Chinese prove...";
            customDialog.id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = true;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = date;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = true;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Alexandra Smith";
            customDialog.message = "Reminds me of a Chinese prove...";
            customDialog.id = 1;
            customDialog.unread_count = 2;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = date - 3600;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Make Apple";
            customDialog.message = "🤷‍♂️ Sticker";
            customDialog.id = 2;
            customDialog.unread_count = 3;
            customDialog.pinned = false;
            customDialog.muted = true;
            customDialog.type = 0;
            customDialog.date = date - 7200;
            customDialog.verified = false;
            customDialog.isMedia = true;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Paul Newman";
            customDialog.message = "Any ideas?";
            customDialog.id = 3;
            customDialog.unread_count = 0;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 2;
            customDialog.date = date - 10800;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Old Pirates";
            customDialog.message = "Yo-ho-ho!";
            customDialog.id = 4;
            customDialog.unread_count = 0;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 1;
            customDialog.date = date - 14400;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = true;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Kate Bright";
            customDialog.message = "Hola!";
            customDialog.id = 5;
            customDialog.unread_count = 0;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = date - 18000;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Nick K";
            customDialog.message = "These are not the droids you are looking for";
            customDialog.id = 6;
            customDialog.unread_count = 0;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = date - 21600;
            customDialog.verified = true;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Adler Toberg";
            customDialog.message = "Did someone say peanut butter?";
            customDialog.id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = date - 25200;
            customDialog.verified = true;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
        }

        public int getItemCount() {
            return this.dialogs.size() + 1;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new DialogCell(this.mContext);
            } else if (viewType == 1) {
                view = new LoadingCell(this.mContext);
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                DialogCell cell = viewHolder.itemView;
                cell.useSeparator = i != getItemCount() + -1;
                cell.setDialog((CustomDialog) this.dialogs.get(i));
            }
        }

        public int getItemViewType(int i) {
            if (i == this.dialogs.size()) {
                return 1;
            }
            return 0;
        }
    }

    public ThemePreviewActivity(File file, ThemeInfo themeInfo) {
        this.swipeBackEnabled = false;
        this.applyingTheme = themeInfo;
        this.themeFile = file;
    }

    public View createView(Context context) {
        this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
            }

            public boolean canCollapseSearch() {
                return true;
            }

            public void onSearchCollapse() {
            }

            public void onTextChanged(EditText editText) {
            }
        }).getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        this.actionBar.setBackButtonDrawable(new MenuDrawable());
        this.actionBar.setTitle(LocaleController.getString("AppName", R.string.ThemePreview));
        View frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        ImageView floatingButton = new ImageView(context);
        floatingButton.setScaleType(ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        floatingButton.setBackgroundDrawable(drawable);
        floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
        floatingButton.setImageResource(R.drawable.floating_pencil);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            floatingButton.setStateListAnimator(animator);
            floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout.addView(floatingButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : BitmapDescriptorFactory.HUE_YELLOW, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 62.0f));
        this.dialogsAdapter = new DialogsAdapter(context);
        this.listView.setAdapter(this.dialogsAdapter);
        frameLayout = new View(context);
        frameLayout.setBackgroundResource(R.drawable.header_shadow_reverse);
        frameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        FrameLayout bottomLayout = new FrameLayout(context);
        bottomLayout.setBackgroundColor(-1);
        frameLayout.addView(bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        TextView cancelButton = new TextView(context);
        cancelButton.setTextSize(1, 14.0f);
        cancelButton.setTextColor(-15095832);
        cancelButton.setGravity(17);
        cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
        cancelButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        bottomLayout.addView(cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Theme.applyPreviousTheme();
                ThemePreviewActivity.this.parentLayout.rebuildAllFragmentViews(false);
                ThemePreviewActivity.this.finishFragment();
            }
        });
        TextView doneButton = new TextView(context);
        doneButton.setTextSize(1, 14.0f);
        doneButton.setTextColor(-15095832);
        doneButton.setGravity(17);
        doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
        doneButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        doneButton.setText(LocaleController.getString("ApplyTheme", R.string.ApplyTheme).toUpperCase());
        doneButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        bottomLayout.addView(doneButton, LayoutHelper.createFrame(-2, -1, 53));
        doneButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ThemePreviewActivity.this.applied = true;
                ThemePreviewActivity.this.parentLayout.rebuildAllFragmentViews(false);
                Theme.applyThemeFile(ThemePreviewActivity.this.themeFile, ThemePreviewActivity.this.applyingTheme.name, false);
                ThemePreviewActivity.this.finishFragment();
            }
        });
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        if (this.dialogsAdapter != null) {
            this.dialogsAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded && this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof DialogCell) {
                    ((DialogCell) child).update(0);
                }
            }
        }
    }
}
