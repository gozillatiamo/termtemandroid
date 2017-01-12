package com.worldwidewealth.wealthwallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.worldwidewealth.wealthwallet.R;

/**
 * Created by user on 12-Jan-17.
 */

public class DialogHelp extends Dialog {

    private ExpandableListView mExpandableList;

    public DialogHelp(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_help);
        initWidgets();
        initData();
    }

    private void initWidgets(){
        mExpandableList = (ExpandableListView) findViewById(R.id.expanded_menu);
    }

    private void initData(){
        mExpandableList.setAdapter(new HelpExpandableAdapter());
    }

    private class HelpExpandableAdapter extends BaseExpandableListAdapter{
        private String[] mMainMenu = getContext().getResources().getStringArray(R.array.menu_help);

        @Override
        public int getGroupCount() {
            return mMainMenu.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mMainMenu[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            LayoutInflater infalInflater = null;
            if (convertView == null){
                infalInflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);

            }
            TextView title = (TextView) convertView.findViewById(android.R.id.text1);
            title.setText((String)getGroup(groupPosition));
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(getContext().getResources().getColor(android.R.color.secondary_text_dark));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);


            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            switch (groupPosition) {
                case 0:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bank_detail, null);
                    break;
                case 1:
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
                    TextView title = (TextView) convertView.findViewById(android.R.id.text1);
                    title.setText("(+66)2-331-4793" + " หรือ " + "(+66)99-447-0474");
                    title.setTypeface(Typeface.DEFAULT_BOLD);
                    title.setTextColor(getContext().getResources().getColor(android.R.color.secondary_text_dark));
                    title.setLinkTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    Linkify.addLinks(title, Linkify.ALL);
                    break;
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
