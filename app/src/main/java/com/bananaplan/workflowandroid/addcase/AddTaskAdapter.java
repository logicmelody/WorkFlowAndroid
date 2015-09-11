package com.bananaplan.workflowandroid.addcase;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.TaskItem;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.view.TimePickerDialogFragment;

import java.util.ArrayList;


/**
 * Adapter for add-tasks grid view in AddCaseFragment
 *
 * @author Danny Lin
 * @since 2015/9/1.
 */
public class AddTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AddTaskAdapter";

    private static final class DialogTag {
        public static final String TIME_PICKER = "dialog_time_picker";
    }

    private static final class ItemViewType {
        public static final int INFO_HEADER = 0;
        public static final int TASK = 1;
        public static final int ADD = 2;
    }

    private Context mContext;
    private ArrayList<TaskItem> mTasksData = new ArrayList<TaskItem>();

    private int mSpanCount = 0;


    private final class InfoHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView saveCaseButton;
        public EditText length;


        public InfoHeaderViewHolder(View v) {
            super(v);
            findViews(v);
            setupViews(v);
            setupOnClickListener();
        }

        private void findViews(View v) {
            saveCaseButton = (TextView) v.findViewById(R.id.save_case_button);
            length = (EditText) v.findViewById(R.id.length_edit_text);
        }

        private void setupViews(View v) {
            ((TextView) v.findViewById(R.id.measurement_text)).setPadding(length.getPaddingLeft(), 0, 0, 0);
        }

        private void setupOnClickListener() {
            saveCaseButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_case_button:
                    break;
            }
        }
    }

    private final class TaskViewHolder extends RecyclerView.ViewHolder implements
            View.OnFocusChangeListener, View.OnClickListener {

        public TextView index;

        public EditText title;
        public EditText expectedWorkingTime;
        public EditText equipment;
        public EditText worker;

        private TextWatcher mTextWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTasksData.get(TaskViewHolder.this.getAdapterPosition()).title = s.toString();
            }
        };

        public TaskViewHolder(View v) {
            super(v);
            findViews(v);
            setupListeners();
        }

        private void findViews(View v) {
            index = (TextView) v.findViewById(R.id.add_case_task_index);
            title = (EditText) v.findViewById(R.id.add_case_task_title_edit_text);
            expectedWorkingTime = (EditText) v.findViewById(R.id.add_case_task_expected_working_time_edit_text);
            equipment = (EditText) v.findViewById(R.id.add_case_task_equipment_edit_text);
            worker = (EditText) v.findViewById(R.id.add_case_task_worker_edit_text);
        }

        private void setupListeners() {
            title.addTextChangedListener(mTextWatcher);
            expectedWorkingTime.setOnFocusChangeListener(this);
            expectedWorkingTime.setOnClickListener(this);
            equipment.setOnFocusChangeListener(this);
            equipment.setOnClickListener(this);
            worker.setOnFocusChangeListener(this);
            worker.setOnClickListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) return;
            setupOnClickEvents(v.getId());
        }

        @Override
        public void onClick(View v) {
            setupOnClickEvents(v.getId());
        }

        private void setupOnClickEvents(int id) {
            switch (id) {
                case R.id.add_case_task_expected_working_time_edit_text:
                    showTimePicker();
                    break;
                case R.id.add_case_task_equipment_edit_text:
                    Toast.makeText(mContext, "Device " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.add_case_task_worker_edit_text:
                    Toast.makeText(mContext, "Worker " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        private void showTimePicker() {
            FragmentManager fm = ((Activity) mContext).getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prevTimePickerDialog = fm.findFragmentByTag(DialogTag.TIME_PICKER);

            if (prevTimePickerDialog != null) {
                ft.remove(prevTimePickerDialog);
            }

            TimePickerDialogFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mTasksData.get(TaskViewHolder.this.getAdapterPosition()).expectedWorkingTime =
                            Utils.timeToMilliseconds(hourOfDay, minute);
                    setExpectedWorkingTime(TaskViewHolder.this.expectedWorkingTime,
                                           mTasksData.get(TaskViewHolder.this.getAdapterPosition()).expectedWorkingTime);
                }

            }, 0, 0, true).show(ft, DialogTag.TIME_PICKER);
        }
    }

    private final class AddViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View view;

        public AddViewHolder(View v) {
            super(v);
            view = v;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            for (int i = 0 ; i < mSpanCount ; i++) {
                mTasksData.add(mTasksData.size()-1, new TaskItem());
            }
            notifyItemRangeInserted(mTasksData.size()-1-mSpanCount, mSpanCount);
            ((RecyclerView) v.getParent()).smoothScrollToPosition(mTasksData.size()-mSpanCount);
        }
    }

    public AddTaskAdapter(Context context, int spanCount) {
        mContext = context;
        mSpanCount = spanCount;
        initTasksData();
    }

    private void initTasksData() {
        // Header item
        mTasksData.add(new TaskItem());

        // Task item
        for (int i = 0 ; i < mSpanCount ; i++) {
            mTasksData.add(new TaskItem());
        }

        // Add item
        mTasksData.add(new TaskItem());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case ItemViewType.INFO_HEADER:
                v = LayoutInflater.from(mContext).inflate(R.layout.add_case_info_header, parent, false);
                viewHolder = new InfoHeaderViewHolder(v);
                break;
            case ItemViewType.ADD:
                v = LayoutInflater.from(mContext).inflate(R.layout.add_case_add_item, parent, false);
                viewHolder = new AddViewHolder(v);
                break;
            case ItemViewType.TASK:
                v = LayoutInflater.from(mContext).inflate(R.layout.add_case_task_item, parent, false);
                viewHolder = new TaskViewHolder(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isAddItem(position)) {
            return;
        } else if (isInfoHeader(position)) {
            onBindInfoHeaderViewHolder((InfoHeaderViewHolder) holder, position);
        } else {
            onBindTaskViewHolder((TaskViewHolder) holder, position);
        }
    }

    private void onBindInfoHeaderViewHolder(InfoHeaderViewHolder holder, int position) {
    }

    private void onBindTaskViewHolder(TaskViewHolder holder, int position) {
        holder.index.setText(String.valueOf(position));
        holder.title.setText(mTasksData.get(position).title);
        setExpectedWorkingTime(holder.expectedWorkingTime, mTasksData.get(position).expectedWorkingTime);
    }

    private void setExpectedWorkingTime(EditText timeEditText, long milliseconds) {
        if (milliseconds == -1L) {
            timeEditText.setText("");
        } else {
            int[] times = Utils.millisecondsToTime(milliseconds);
            String expectedWorkingTime = mContext.getString(R.string.add_case_task_expected_working_time);
            timeEditText.setText(String.format(expectedWorkingTime, Utils.pad(times[0]), Utils.pad(times[1])));
        }
    }

    private boolean isInfoHeader(int position) {
        return position == 0;
    }

    private boolean isAddItem(int position) {
        return position == mTasksData.size()-1;
    }

    @Override
    public int getItemCount() {
        return mTasksData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isInfoHeader(position)) {
            return ItemViewType.INFO_HEADER;
        } else if (isAddItem(position)) {
            return ItemViewType.ADD;
        } else {
            return ItemViewType.TASK;
        }
    }
}
