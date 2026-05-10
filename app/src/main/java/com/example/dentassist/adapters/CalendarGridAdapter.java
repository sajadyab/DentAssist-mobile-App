package com.example.dentassist.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dentassist.R;
import com.example.dentassist.models.CalendarSlot;
import com.example.dentassist.models.SlotState;
import com.example.dentassist.models.WeekDayData;

import java.util.List;

public class CalendarGridAdapter extends RecyclerView.Adapter<CalendarGridAdapter.GridViewHolder> {

    private final List<WeekDayData> weekDays;
    private final List<String> timeRows;
    private final OnSlotClickListener listener;
    private final String doctorName;

    private static final int TIME_COLUMN_WIDTH_DP = 65;
    private static final int DAY_COLUMN_WIDTH_DP = 60;

    public interface OnSlotClickListener {
        void onFreeSlotClicked(String dateYmd, String timeHis, String slotLabel, String doctorName);
    }

    public CalendarGridAdapter(List<WeekDayData> weekDays, List<String> timeRows,
                               String doctorName, OnSlotClickListener listener) {
        this.weekDays = weekDays;
        this.timeRows = timeRows;
        this.doctorName = doctorName;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return timeRows.size() + 1; // +1 for header
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout row = new LinearLayout(parent.getContext());
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
        return new GridViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        holder.bind(position);
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rowLayout;
        Context context;

        GridViewHolder(LinearLayout rowLayout) {
            super(rowLayout);
            this.rowLayout = rowLayout;
            this.context = rowLayout.getContext();
        }

        void bind(int position) {
            rowLayout.removeAllViews();

            if (position == 0) {
                // Header row
                TextView timeHeader = createHeaderCell("Time", TIME_COLUMN_WIDTH_DP);
                rowLayout.addView(timeHeader);

                for (WeekDayData day : weekDays) {
                    LinearLayout dayCol = new LinearLayout(context);
                    dayCol.setOrientation(LinearLayout.VERTICAL);
                    dayCol.setGravity(Gravity.CENTER);
                    dayCol.setPadding(2, 8, 2, 8);
                    dayCol.setMinimumWidth(dpToPx(DAY_COLUMN_WIDTH_DP));

                    TextView dayText = new TextView(context);
                    dayText.setText(day.getDisplayDay());
                    dayText.setTypeface(null, Typeface.BOLD);
                    dayText.setTextSize(11);
                    dayText.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                    dayText.setGravity(Gravity.CENTER);

                    TextView dateText = new TextView(context);
                    dateText.setText(day.getDisplayDate());
                    dateText.setTextSize(10);
                    dateText.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                    dateText.setGravity(Gravity.CENTER);

                    dayCol.addView(dayText);
                    dayCol.addView(dateText);
                    rowLayout.addView(dayCol);
                }
            } else {
                // Time row
                int timeIndex = position - 1;
                String timeHis = timeRows.get(timeIndex);

                String displayLabel = timeHis.substring(0, 5);
                for (WeekDayData day : weekDays) {
                    for (CalendarSlot slot : day.getSlots()) {
                        if (slot.getTimeHis().equals(timeHis)) {
                            displayLabel = slot.getLabel();
                            break;
                        }
                    }
                }

                TextView timeCell = createCell(displayLabel, TIME_COLUMN_WIDTH_DP);
                rowLayout.addView(timeCell);

                for (WeekDayData day : weekDays) {
                    CalendarSlot slot = null;
                    for (CalendarSlot s : day.getSlots()) {
                        if (s.getTimeHis().equals(timeHis)) {
                            slot = s;
                            break;
                        }
                    }

                    if (slot == null) {
                        TextView emptyCell = createCell("", DAY_COLUMN_WIDTH_DP);
                        emptyCell.setBackgroundResource(R.drawable.bg_slot_past);
                        rowLayout.addView(emptyCell);
                    } else {
                        Button slotButton = new Button(context);
                        slotButton.setPadding(2, 8, 2, 8);
                        slotButton.setAllCaps(false);
                        slotButton.setTextSize(10);
                        slotButton.setMinHeight(dpToPx(32));
                        slotButton.setMinimumWidth(dpToPx(DAY_COLUMN_WIDTH_DP - 4));

                        final CalendarSlot finalSlot = slot;
                        final WeekDayData finalDay = day;

                        switch (finalSlot.getState()) {
                            case FREE:
                                slotButton.setText("+");
                                slotButton.setTextColor(Color.WHITE);
                                slotButton.setBackgroundResource(R.drawable.bg_slot_free);
                                slotButton.setOnClickListener(v -> {
                                    String slotLabel = finalDay.getDisplayDay() + ", " +
                                            finalDay.getDisplayDate() + " · " + finalSlot.getLabel();
                                    listener.onFreeSlotClicked(finalDay.getDateYmd(),
                                            finalSlot.getTimeHis(), slotLabel, doctorName);
                                });
                                break;
                            case BUSY:
                                slotButton.setText("Taken");
                                slotButton.setTextColor(Color.WHITE);
                                slotButton.setBackgroundResource(R.drawable.bg_slot_busy);
                                slotButton.setEnabled(false);
                                break;
                            default:
                                slotButton.setText("");
                                slotButton.setBackgroundResource(R.drawable.bg_slot_past);
                                slotButton.setEnabled(false);
                                break;
                        }
                        rowLayout.addView(slotButton);
                    }
                }
            }
        }

        private TextView createHeaderCell(String text, int widthDp) {
            TextView tv = new TextView(context);
            tv.setText(text);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setPadding(8, 10, 8, 10);
            tv.setWidth(dpToPx(widthDp));
            tv.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            tv.setTextSize(11);
            tv.setGravity(Gravity.CENTER);
            return tv;
        }

        private TextView createCell(String text, int widthDp) {
            TextView tv = new TextView(context);
            tv.setText(text);
            tv.setPadding(8, 10, 8, 10);
            tv.setGravity(Gravity.CENTER);
            tv.setWidth(dpToPx(widthDp));
            tv.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            tv.setTextSize(10);
            return tv;
        }

        private int dpToPx(int dp) {
            return (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, dp,
                    context.getResources().getDisplayMetrics());
        }
    }
}