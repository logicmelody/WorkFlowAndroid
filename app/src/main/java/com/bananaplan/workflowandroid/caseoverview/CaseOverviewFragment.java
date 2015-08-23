package com.bananaplan.workflowandroid.caseoverview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.main.WorkingData;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.workers.Vendor;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.main.MainActivity;
import com.bananaplan.workflowandroid.utility.BarChartData;
import com.bananaplan.workflowandroid.utility.IconSpinnerAdapter;
import com.bananaplan.workflowandroid.utility.Utils;

import org.achartengine.GraphicalView;
import org.achartengine.model.SeriesSelection;

import java.util.ArrayList;

import it.sephiroth.android.library.widget.*;

/**
 * @author Ben Lai
 * @since 2015/7/16.
 */
public class CaseOverviewFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener
        , View.OnClickListener, View.OnTouchListener, it.sephiroth.android.library.widget.AdapterView.OnItemClickListener {
    private MainActivity mActivity;

    // views
    private Spinner mVendorsSpinner;
    private EditText mEtCaseSearch;
    private ListView mTaskCaseListView;
    private ListView mTaskItemListView;
    private TextView mTvCaseNameSelected;
    private TextView mTvCaseVendorSelected;
    private TextView mTvCasePersonInChargeSelected;
    private ProgressBar mPbCaseSelected;
    private TextView mTvCaseHoursPassedBy;
    private TextView mTvCaseHoursUnfinished;
    private TextView mTvCaseHoursExpected;
    private TextView mTvEditCase;
    private HListView mWorkerListView;
    private LinearLayout mStatisticsViewGroup;
    private LinearLayout mWeekPickerViewGroup;
    private View mToastView;
    private TextView mTvTaskItemCount;
    private TextView mTvTotalHoursPerWeek;
    private TextView mTvCaseDeliveryDate;
    private TextView mTvCaseFeedDate;
    private TextView mTvCaseFigureDate;
    private TextView mTvCaseSize;
    private TextView mTvCaseSheetCount;
    private TextView mTvCaseModelCount;
    private TextView mTvCaseOthers;
    private TextView mTvCaseProgress;

    // data
    private VendorSpinnerAdapter mVendorSpinnerAdapter;
    private TaskCaseListViewAdapter mTaskCaseListViewAdapter;
    private TaskItemListViewAdapter mTaskItemListViewAdapter;
    private WorkerItemListViewAdapter mWorkerItemListViewAdapter;
    private TaskCase mSelectedTaskCase;

    private int mTaskItemListViewHeaderHeight;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_taskcase_ov, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mVendorsSpinner = (Spinner) getActivity().findViewById(R.id.ov_leftpane_spinner);
        mEtCaseSearch = (EditText) getActivity().findViewById(R.id.ov_leftpane_search_edittext);
        mTaskCaseListView = (ListView) getActivity().findViewById(R.id.ov_leftpane_listview);
        mTaskItemListView = (ListView) getActivity().findViewById(R.id.case_listview_task_item);
        mWorkerListView = (HListView) getActivity().findViewById(R.id.case_workers_listview);
        mWorkerListView.setOnItemClickListener(this);

        mEtCaseSearch.addTextChangedListener(this);
        mVendorSpinnerAdapter = new VendorSpinnerAdapter(getActivity(), getSpinnerVendorData());
        mVendorsSpinner.setAdapter(mVendorSpinnerAdapter);
        mVendorsSpinner.setOnItemSelectedListener(this);

        // right pane
        mTvCaseNameSelected = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_case_name);
        mTvCaseVendorSelected = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_vendor_name);
        mTvCasePersonInChargeSelected = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_worker_name);
        mPbCaseSelected = (ProgressBar) getActivity().findViewById(R.id.case_ov_right_pane_case_progress_bar);
        mTvCaseHoursPassedBy = (TextView) getActivity().findViewById(R.id.case_tv_hours_passed_by);
        mTvCaseHoursUnfinished = (TextView) getActivity().findViewById(R.id.case_tv_hours_unfinished);
        mTvCaseHoursExpected = (TextView) getActivity().findViewById(R.id.case_tv_hours_forecast);
        mTvEditCase = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_edit_case);
        mStatisticsViewGroup = (LinearLayout) getActivity().findViewById(R.id.ov_statistics_chart_container);
        ((LinearLayout.LayoutParams)mStatisticsViewGroup.getLayoutParams()).topMargin = getResources().getDimensionPixelOffset(R.dimen.case_ov_statistics_margin_top);
        mTvTaskItemCount = (TextView) getActivity().findViewById(R.id.case_tv_task_item_count);
        mTvTotalHoursPerWeek = (TextView) getActivity().findViewById(R.id.ov_statistics_working_hour_tv);
        mWeekPickerViewGroup = (LinearLayout) getActivity().findViewById(R.id.ov_statistics_week_chooser);
        mWeekPickerViewGroup.setOnClickListener(this);
        mTvCaseDeliveryDate = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_delivery_date);
        mTvCaseFeedDate = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_feed_date);
        mTvCaseFigureDate = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_figure_date);
        mTvCaseSize = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_size);
        mTvCaseSheetCount = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_sheet_count);
        mTvCaseModelCount = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_model_count);
        mTvCaseOthers = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_others);
        mTvCaseProgress = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_case_progress);

        mTaskCaseListViewAdapter = new TaskCaseListViewAdapter(getActivity(), getTaskCases());
        mTaskCaseListView.setAdapter(mTaskCaseListViewAdapter);
        mTaskCaseListView.setOnItemClickListener(this);
        mTaskItemListView.setOnItemClickListener(this);
        mTaskItemListView.addHeaderView(getTaskItemHeaderView(), null, false);

        mTvEditCase.setOnClickListener(this);

        onTaskCaseSelected(mSelectedTaskCase);
    }

    private ArrayList<Vendor> getSpinnerVendorData() {
        ArrayList<Vendor> tmp = new ArrayList<Vendor>();
        tmp.add(new Vendor(-1L, getResources().getString(R.string.case_spinner_all_vendors))); // all vendors
        tmp.addAll(WorkingData.getInstance(mActivity).getVendors());
        return tmp;
    }

    private ArrayList<TaskCase> getTaskCases() {
        ArrayList<TaskCase> cases = new ArrayList<>();
        for (Vendor vendor : WorkingData.getInstance(mActivity).getVendors()) {
            for (TaskCase taskCase : vendor.taskCases) {
                if (mSelectedTaskCase == null) {
                    mSelectedTaskCase = taskCase;
                }
                cases.add(taskCase);
            }
        }
        return cases;
    }

    private ArrayList<WorkerItem> getWorkerItems() {
        ArrayList<WorkerItem> workers = new ArrayList<>();
        if (mSelectedTaskCase != null) {
            for (TaskItem item : mSelectedTaskCase.taskItems) {
                workers.add(WorkingData.getInstance(mActivity).getWorkerItemById(item.workerId));
            }
        }
        return workers;
    }

    private ArrayList<TaskItem> getTaskItems() {
        if (mSelectedTaskCase != null) {
            return new ArrayList<>(mSelectedTaskCase.taskItems);
        }
        return new ArrayList<>();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        mTaskCaseListViewAdapter.getFilter().filter(s.toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == mVendorsSpinner.getId()) {
            mTaskCaseListViewAdapter.getFilter().filter(mEtCaseSearch.getText().toString());
        }
    }

    private void onTaskCaseSelected(TaskCase taskCase) {
        if (taskCase == null) return;
        updateTaskItemListView();
        updateWorkerItemListView();
        updateStatisticsView();
        mTvCaseNameSelected.setText(taskCase.name);
        mTvCaseVendorSelected.setText(WorkingData.getInstance(mActivity).getVendorById(taskCase.vendorId).name);
        if (mSelectedTaskCase.workerId > 0) {
            mTvCasePersonInChargeSelected.setText(WorkingData.getInstance(mActivity).getWorkerItemById(taskCase.workerId).name);
        }
        mTvCaseHoursPassedBy.setText(taskCase.getHoursPassedBy());
        mTvCaseHoursUnfinished.setText(taskCase.getHoursUnFinished());
        mTvCaseHoursExpected.setText(taskCase.getHoursForecast());
        mPbCaseSelected.setProgress(taskCase.getFinishPercent());
        mTvTaskItemCount.setText(String.valueOf(taskCase.taskItems.size()));
        mTvCaseProgress.setText(taskCase.getFinishItemsCount() + "/" + taskCase.taskItems.size());
        mTvCaseFeedDate.setText(Utils.timestamp2Date(taskCase.feedDate, true));
        mTvCaseFigureDate.setText(Utils.timestamp2Date(taskCase.figureDate, true));
        mTvCaseDeliveryDate.setText(Utils.timestamp2Date(taskCase.deliveryDate, true));
        mTvCaseSheetCount.setText(String.valueOf(taskCase.sheetCount));
        mTvCaseModelCount.setText(String.valueOf(taskCase.modelCount));
        mTvCaseOthers.setText(taskCase.others);
        mTvCaseSize.setText(taskCase.getSize());
    }

    private void updateStatisticsView() {
        BarChartData data = new BarChartData(this.getClass().getName());
        data.genRandomData(mActivity, 1);
        mStatisticsViewGroup.removeAllViews();
        mStatisticsViewGroup.addView(Utils.genBarChart(mActivity, data),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        mTvTotalHoursPerWeek.setText(getResources().getString(R.string.overview_finish_hours, data.getWorkingHours()));
    }

    private void updateTaskItemListView() {
        ArrayList<TaskItem> items = getTaskItems();
        if (mTaskItemListViewAdapter == null) {
            mTaskItemListViewAdapter = new TaskItemListViewAdapter(getActivity(), items);
            mTaskItemListView.setAdapter(mTaskItemListViewAdapter);
        } else {
            mTaskItemListViewAdapter.clear();
            mTaskItemListViewAdapter.addAll(items);
        }
        if (items.size() > 0) {
            ViewGroup.LayoutParams params = mTaskItemListView.getLayoutParams();
            params.height = (int) (items.size() * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height)) + mTaskItemListViewHeaderHeight;
        }
        mTaskItemListViewAdapter.notifyDataSetChanged();
    }

    private void updateWorkerItemListView() {
        ArrayList<WorkerItem> workers = getWorkerItems();
        if (mWorkerItemListViewAdapter == null) {
            mWorkerItemListViewAdapter = new WorkerItemListViewAdapter(getActivity(), workers);
            mWorkerListView.setAdapter(mWorkerItemListViewAdapter);
        } else {
            mWorkerItemListViewAdapter.clear();
            mWorkerItemListViewAdapter.addAll(workers);
        }
        mWorkerItemListViewAdapter.notifyDataSetChanged();
    }

    private View getTaskItemHeaderView() {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.case_taskitem_listview_view, null);
        if (view == null) throw new NullPointerException();
        TaskItemListViewAdapterViewHolder holder = new TaskItemListViewAdapterViewHolder(view);
        float textSize = getResources().getDimension(R.dimen.case_overview_taskitem_listview_header_text_size);
        holder.tvStatus.setTextSize(textSize);
        holder.tvTool.setTextSize(textSize);
        holder.tvWorkTime.setTextSize(textSize);
        holder.tvExpectedTime.setTextSize(textSize);
        holder.tvId.setTextSize(textSize);
        holder.tvWarning.setTextSize(textSize);
        holder.tvName.setTextSize(textSize);
        holder.llWorkerInfo.setVisibility(View.GONE);
        holder.tvWorkerNameString.setVisibility(View.VISIBLE);
        for (View divider : holder.dividerViews) {
            divider.setVisibility(View.INVISIBLE);
        }
        for (View divider : holder.horizontalDividerViews) {
            divider.setVisibility(View.VISIBLE);
        }
        ViewTreeObserver observer = view.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mTaskItemListViewHeaderHeight = view.getHeight();
                    if (mTaskItemListViewAdapter != null && mTaskItemListViewAdapter.getCount() > 0) {
                        ViewGroup.LayoutParams params = mTaskItemListView.getLayoutParams();
                        params.height = (int) (mTaskItemListViewAdapter.getCount() * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height)) + mTaskItemListViewHeaderHeight;
                        mTaskItemListViewAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == mTaskCaseListView.getId()) {
            mSelectedTaskCase = mTaskCaseListViewAdapter.getItem(position);
            onTaskCaseSelected(mSelectedTaskCase);
            mTaskCaseListViewAdapter.setPositionSelected(position);
            mTaskCaseListViewAdapter.notifyDataSetChanged();
        } else if (parent.getId() == mTaskItemListView.getId()) {
            // TODO
        }
    }

    private void editTaskCase() {
        // TODO
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.case_ov_right_pane_edit_case:
                editTaskCase();
                break;
            case R.id.ov_statistics_week_chooser:
                // TODO
                break;
            default:
                break;
        }
        if (v instanceof GraphicalView) {
            GraphicalView gView = (GraphicalView) v;
            SeriesSelection currentSelection = gView.getCurrentSeriesAndPoint();
            if (currentSelection == null) return;
            // TODO
        }
    }

    private View getToastView() {
        if (mToastView == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            mToastView = inflater.inflate(R.layout.toast, null);
        }
        return mToastView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof GraphicalView) {
            // TODO
        }
        return false;
    }

    private class VendorSpinnerAdapter extends IconSpinnerAdapter<Vendor> {

        public VendorSpinnerAdapter(Context context, ArrayList<Vendor> objects) {
            super(context, -1, objects);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).id;
        }

        @Override
        public Vendor getItem(int position) {
            return (Vendor) super.getItem(position);
        }

        @Override
        public String getSpinnerViewDisplayString(int position) {
            return getItem(position).name;
        }

        @Override
        public int getSpinnerIconResourceId() {
            return R.drawable.ic_work_black;
        }

        @Override
        public String getDropdownSpinnerViewDisplayString(int position) {
            return getItem(position).name;
        }

        @Override
        public boolean isDropdownSelectedIconVisible(int position) {
            return mVendorsSpinner.getSelectedItemId() == getItem(position).id;
        }
    }

    private class WorkerItemListViewAdapter extends ArrayAdapter<WorkerItem> {
        private LayoutInflater mInflater;

        public WorkerItemListViewAdapter(Context context, ArrayList<WorkerItem> items) {
            super(context, 0, items);
            mInflater = getActivity().getLayoutInflater();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.case_workers_listview_view, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            WorkerItem worker = getItem(position);
            holder.avator.setImageDrawable(worker.getAvator());
            return convertView;
        }

        private class ViewHolder {
            ImageView avator;

            public ViewHolder(View view) {
                avator = (ImageView) view.findViewById(R.id.case_workers_listview_item_iv);
            }
        }
    }

    private class TaskItemListViewAdapter extends ArrayAdapter<TaskItem> {

        public TaskItemListViewAdapter(Context context, ArrayList<TaskItem> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskItemListViewAdapterViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.case_taskitem_listview_view, parent, false);
                holder = new TaskItemListViewAdapterViewHolder(convertView);
                holder.tvWarning.setText("");
                convertView.setTag(holder);
                final ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.height = (int) getResources().getDimension(R.dimen.ov_taskitem_listview_item_height);
                if (position % 2 == 0) {
                    convertView.setBackgroundColor(getResources().getColor(R.color.gray4));
                } else {
                    convertView.setBackgroundColor(Color.WHITE);
                }
            } else {
                holder = (TaskItemListViewAdapterViewHolder) convertView.getTag();
            }
            final TaskItem taskItem = getItem(position);
            holder.tvId.setText(String.valueOf(position + 1));
            holder.tvStatus.setText(Utils.getTaskItemStatusString(getActivity(), taskItem));
            if (TaskItem.Status.FINISH == taskItem.status) {
                holder.tvStatus.setBackground(null);
                holder.tvStatus.setTextColor(getResources().getColor(R.color.gray1));
                holder.tvName.setTextColor(getResources().getColor(R.color.gray1));
                holder.tvExpectedTime.setTextColor(getResources().getColor(R.color.gray1));
                holder.tvWorkTime.setTextColor(getResources().getColor(R.color.gray1));
                holder.tvTool.setTextColor(getResources().getColor(R.color.gray1));
            } else {
                if (TaskItem.Status.WORKING == taskItem.status) {
                    holder.tvStatus.setBackground(getResources().getDrawable(R.drawable.border_textview_bg_green, null));
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.green));
                } else {
                    holder.tvStatus.setBackground(null);
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.black2));
                }
                holder.tvName.setTextColor(getResources().getColor(R.color.black2));
                holder.tvExpectedTime.setTextColor(getResources().getColor(R.color.black2));
                holder.tvWorkTime.setTextColor(getResources().getColor(R.color.black2));
                holder.tvTool.setTextColor(getResources().getColor(R.color.black2));
            }
            Utils.setTaskItemWarningTextView(getActivity(), taskItem, holder.tvWarning, true);
            holder.tvName.setText(taskItem.title);
            holder.tvExpectedTime.setText(taskItem.getExpectedFinishedTime());
            holder.tvWorkTime.setText(taskItem.getWorkingTime());
            if (taskItem.toolId > 0) {
                holder.tvTool.setText(WorkingData.getInstance(mActivity).getToolById(taskItem.toolId).name);
            } else {
                holder.tvTool.setText("");
            }
            if (taskItem.workerId > 0) {
                final WorkerItem worker = WorkingData.getInstance(getActivity()).getWorkerItemById(taskItem.workerId);
                holder.tvWorkerName.setText(worker.name);
                holder.ivWorkerAvator.setVisibility(View.VISIBLE);
                holder.ivWorkerAvator.setImageDrawable(worker.getAvator());
                holder.llWorkerInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: view worker's info
                        Toast.makeText(getActivity(), "View worker's info", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                holder.tvWorkerName.setText("");
                holder.ivWorkerAvator.setImageDrawable(null);
            }
            return convertView;
        }
    }

    public static class TaskItemListViewAdapterViewHolder {
        TextView tvId;
        TextView tvStatus;
        TextView tvName;
        TextView tvExpectedTime;
        TextView tvWorkTime;
        TextView tvTool;
        TextView tvWarning;
        TextView tvWorkerName;
        TextView tvWorkerNameString;
        ImageView ivWorkerAvator;
        LinearLayout llWorkerInfo;
        ArrayList<View> dividerViews = new ArrayList<>();
        ArrayList<View> horizontalDividerViews = new ArrayList<>();

        public TaskItemListViewAdapterViewHolder(View view) {
            if (!(view instanceof LinearLayout)) return;
            LinearLayout root = (LinearLayout) view;
            tvId = (TextView) view.findViewById(R.id.taskitem_listview_id);
            tvStatus = (TextView) view.findViewById(R.id.taskitem_listview_status);
            tvName = (TextView) view.findViewById(R.id.taskitem_listview_name);
            tvExpectedTime = (TextView) view.findViewById(R.id.taskitem_listview_expected_time);
            tvWorkTime = (TextView) view.findViewById(R.id.taskitem_listview_work_time);
            tvTool = (TextView) view.findViewById(R.id.taskitem_listview_work_tool);
            tvWarning = (TextView) view.findViewById(R.id.taskitem_listview_warning);
            tvWorkerName = (TextView) view.findViewById(R.id.taskitem_listview_worker_name);
            tvWorkerNameString = (TextView) view.findViewById(R.id.taskitem_listview_worker_name_string);
            ivWorkerAvator = (ImageView) view.findViewById(R.id.taskitem_listview_worker_avator);
            llWorkerInfo = (LinearLayout) view.findViewById(R.id.taskitem_listview_worker_info);
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                if (child.getId() == R.id.horozontal_divider) {
                    horizontalDividerViews.add(child);
                }
                if (!(child instanceof LinearLayout)) continue;
                LinearLayout secondRoot = (LinearLayout) child;
                for (int j = 0; j < secondRoot.getChildCount(); j++) {
                    if (secondRoot.getChildAt(j).getId() == R.id.listview_taskitem_divider) {
                        dividerViews.add(secondRoot.getChildAt(j));
                    }
                }
            }
        }
    }

    private class TaskCaseListViewAdapter extends ArrayAdapter<TaskCase> implements Filterable {
        private ArrayList<TaskCase> mOrigCases;
        private ArrayList<TaskCase> mFilteredCases;
        private CustomFilter mFilter;
        private int mPositionSelected;

        public TaskCaseListViewAdapter(Context context, ArrayList<TaskCase> cases) {
            super(context, 0, cases);
            mOrigCases = cases;
            mFilteredCases = new ArrayList<TaskCase>(cases);
            mFilter = new CustomFilter();
        }

        @Override
        public TaskCase getItem(int position) {
            return mFilteredCases.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mFilteredCases.get(position).id;
        }

        @Override
        public int getCount() {
            return mFilteredCases.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.case_taskcase_listview_view, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TaskCase taskCase = getItem(position);
            holder.mTvCaseName.setText(taskCase.name);
            holder.mTvVendor.setText(WorkingData.getInstance(mActivity).getVendorById(taskCase.vendorId).name);
            if (taskCase.getFinishPercent() == 100) {
                holder.mTvStatus.setText(getResources().getString(R.string.case_finished));
                holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.bg_solid_textview_bg_gray, null));
                holder.mTvCaseName.setTextColor(getResources().getColor(R.color.gray1));
            } else {
                holder.mTvStatus.setText(taskCase.getFinishItemsCount() + "/" + taskCase.taskItems.size());
                holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.bg_solid_textview_bg_red, null));
                holder.mTvCaseName.setTextColor(getResources().getColor(R.color.black1));
            }
            if (position == mPositionSelected) {
                holder.mRoot.setBackgroundColor(getResources().getColor(R.color.blue1));
                holder.mTvCaseName.setTextColor(Color.WHITE);
                holder.mTvVendor.setTextColor(Color.WHITE);
            } else {
                holder.mRoot.setBackgroundColor(Color.TRANSPARENT);
                holder.mTvCaseName.setTextColor(getResources().getColor(R.color.black1));
                holder.mTvVendor.setTextColor(getResources().getColor(R.color.gray1));
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        public void setPositionSelected(int position) {
            mPositionSelected = position;
        }

        private class ViewHolder {
            RelativeLayout mRoot;
            TextView mTvStatus;
            TextView mTvVendor;
            TextView mTvCaseName;

            public ViewHolder(View view) {
                mRoot = (RelativeLayout) view.findViewById(R.id.case_listview_root);
                mTvStatus = (TextView) view.findViewById(R.id.case_listview_view_tv_status);
                mTvVendor = (TextView) view.findViewById(R.id.case_listview_view_tv_vendor_name);
                mTvCaseName = (TextView) view.findViewById(R.id.case_listview_view_tv_case_name);
            }
        }

        private class CustomFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                ArrayList<TaskCase> filterResult = new ArrayList<TaskCase>();
                for (TaskCase taskCase : mOrigCases) {
                    if ((TextUtils.isEmpty(constraint) || taskCase.name.toLowerCase().contains(constraint))
                            && (mVendorsSpinner.getSelectedItemId() == -1 || taskCase.vendorId == mVendorsSpinner.getSelectedItemId())) {
                        filterResult.add(taskCase);
                    }
                }
                result.values = filterResult;
                result.count = filterResult.size();
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredCases.clear();
                mFilteredCases.addAll((ArrayList<TaskCase>) results.values);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> adapterView, View view, int i, long l) {
        
    }

    private void launchWorkerPage(WorkerItem worker) {

    }
}
