package org.telegram.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemeCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ThemeEditorView;

public class ThemeActivity extends BaseFragment {
    private ListAdapter listAdapter;
    private RecyclerListView listView;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return Theme.themes.size() + 3;
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 0 || type == 1) {
                return true;
            }
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ThemeCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextSettingsCell(this.mContext);
                    ((TextSettingsCell) view).setText(LocaleController.getString("CreateNewTheme", R.string.CreateNewTheme), false);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("CreateNewThemeInfo", R.string.CreateNewThemeInfo));
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                default:
                    view = new ShadowSectionCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                position -= 2;
                ((ThemeCell) holder.itemView).setTheme((ThemeInfo) Theme.themes.get(position), position != Theme.themes.size() + -1);
            }
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == Theme.themes.size() + 2) {
                return 3;
            }
            return 0;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setTitle(LocaleController.getString("Theme", R.string.Theme));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ThemeActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView = frameLayout;
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (position != 0) {
                    position -= 2;
                    if (position < Theme.themes.size()) {
                        Theme.applyTheme((ThemeInfo) Theme.themes.get(position));
                        ThemeActivity.this.parentLayout.rebuildAllFragmentViews(false);
                        ThemeActivity.this.finishFragment();
                    }
                } else if (ThemeActivity.this.getParentActivity() != null) {
                    final EditText editText = new EditText(ThemeActivity.this.getParentActivity());
                    Builder builder = new Builder(ThemeActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("NewTheme", R.string.NewTheme));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    LinearLayout linearLayout = new LinearLayout(ThemeActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    builder.setView(linearLayout);
                    TextView message = new TextView(ThemeActivity.this.getParentActivity());
                    message.setText(LocaleController.formatString("EnterThemeName", R.string.EnterThemeName, new Object[0]));
                    message.setTextSize(16.0f);
                    message.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
                    message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    linearLayout.addView(message, LayoutHelper.createLinear(-1, -2));
                    editText.setTextSize(1, 16.0f);
                    editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    editText.setMaxLines(1);
                    editText.setLines(1);
                    editText.setInputType(16385);
                    editText.setGravity(51);
                    editText.setSingleLine(true);
                    editText.setImeOptions(6);
                    AndroidUtilities.clearCursorDrawable(editText);
                    editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                    linearLayout.addView(editText, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
                    editText.setOnEditorActionListener(new OnEditorActionListener() {
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            AndroidUtilities.hideKeyboard(textView);
                            return false;
                        }
                    });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new OnShowListener() {
                        public void onShow(DialogInterface dialog) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    editText.requestFocus();
                                    AndroidUtilities.showKeyboard(editText);
                                }
                            });
                        }
                    });
                    ThemeActivity.this.showDialog(alertDialog);
                    alertDialog.getButton(-1).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (editText.length() == 0) {
                                Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
                                if (vibrator != null) {
                                    vibrator.vibrate(200);
                                }
                                AndroidUtilities.shakeView(editText, 2.0f, 0);
                                return;
                            }
                            ThemeEditorView themeEditorView = new ThemeEditorView();
                            String name = editText.getText().toString() + ".attheme";
                            themeEditorView.show(ThemeActivity.this.getParentActivity(), name);
                            Theme.saveCurrentTheme(name, true);
                            ThemeActivity.this.listAdapter.notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemClick(View view, int position) {
                position -= 2;
                if (position < 0 || position >= Theme.themes.size()) {
                    return false;
                }
                final ThemeInfo themeInfo = (ThemeInfo) Theme.themes.get(position);
                if (themeInfo.pathToFile == null || ThemeActivity.this.getParentActivity() == null) {
                    return false;
                }
                BottomSheet.Builder builder = new BottomSheet.Builder(ThemeActivity.this.getParentActivity());
                builder.setItems(new CharSequence[]{LocaleController.getString("ShareFile", R.string.ShareFile), LocaleController.getString("Edit", R.string.Edit), LocaleController.getString("Delete", R.string.Delete)}, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            File currentFile = new File(themeInfo.pathToFile);
                            File finalFile = new File(FileLoader.getInstance().getDirectory(4), currentFile.getName());
                            try {
                                if (AndroidUtilities.copyFile(currentFile, finalFile)) {
                                    Intent intent = new Intent("android.intent.action.SEND");
                                    intent.setType("text/xml");
                                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(finalFile));
                                    ThemeActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                                }
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        } else if (which == 1) {
                            Theme.applyTheme(themeInfo);
                            ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true);
                            ThemeActivity.this.parentLayout.showLastFragment();
                            new ThemeEditorView().show(ThemeActivity.this.getParentActivity(), themeInfo.name);
                        } else {
                            Builder builder = new Builder(ThemeActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("DeleteThemeAlert", R.string.DeleteThemeAlert));
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (Theme.deleteTheme(themeInfo)) {
                                        ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true);
                                        ThemeActivity.this.parentLayout.showLastFragment();
                                    }
                                    if (ThemeActivity.this.listAdapter != null) {
                                        ThemeActivity.this.listAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            ThemeActivity.this.showDialog(builder.create());
                        }
                    }
                });
                ThemeActivity.this.showDialog(builder.create());
                return true;
            }
        });
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[15];
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[9] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon);
        r9[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        return r9;
    }
}
