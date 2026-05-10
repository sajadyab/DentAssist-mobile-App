package com.example.dentassist;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.view.LayoutInflater;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.dentassist.adapters.CalendarGridAdapter;
import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.BookAppointmentRequest;
import com.example.dentassist.models.BookingCalendarResponse;
import com.example.dentassist.models.CancelRequest;
import com.example.dentassist.models.GenericResponse;
import com.example.dentassist.models.QueueRequestModel;
import com.example.dentassist.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAppointment extends BaseDrawerActivity {

    private TextInputLayout tilPreferredDate;
    private TextInputEditText etPreferredDate, etNotes;
    private AutoCompleteTextView spinnerDentist, spinnerVisitType;
    private Spinner spinnerPriority, spinnerCalendarDoctor;
    private Calendar calendar;
    private AppCompatButton btnSubmitRequest, btnCancel;
    private AppCompatButton btnPrevWeek, btnNextWeek, btnThisWeek;
    private TextView tvWeekRange;


    private CalendarGridAdapter calendarAdapter;
    private List<BookingCalendarResponse.DoctorInfo> doctorList = new ArrayList<>();
    private List<BookingCalendarResponse.VisitTypeInfo> visitTypeList = new ArrayList<>();
    private int currentDoctorId = -1;
    private int currentWeekOffset = 0;
    private String currentDoctorName = "";
    private String selectedDateYmd = "";
    private String selectedTimeHis = "";
    private com.google.android.material.card.MaterialCardView cardPendingRequests;
    private LinearLayout layoutPendingRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.book_appointment);
        calendar = Calendar.getInstance();
        initViews();
        setupSpinners();
        setupDatePicker();
        setupClickListeners();
        loadBookingData();
    }

    private void initViews() {
        tilPreferredDate = findViewById(R.id.tilPreferredDate);
        etPreferredDate = findViewById(R.id.etPreferredDate);
        etNotes = findViewById(R.id.etNotes);
        spinnerDentist = findViewById(R.id.spinnerDentist);
        spinnerVisitType = findViewById(R.id.spinnerVisitType);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        spinnerCalendarDoctor = findViewById(R.id.spinnerCalendarDoctor);
        tvWeekRange = findViewById(R.id.tvWeekRange);

        btnPrevWeek = findViewById(R.id.btnPrevWeek);
        btnNextWeek = findViewById(R.id.btnNextWeek);
        btnThisWeek = findViewById(R.id.btnThisWeek);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnCancel = findViewById(R.id.btnCancel);
        cardPendingRequests = findViewById(R.id.cardPendingRequests);
        layoutPendingRequests = findViewById(R.id.layoutPendingRequests);
   }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                this, R.array.priority_options, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);
        spinnerPriority.setSelection(1);
    }

    private void setupDatePicker() {
        etPreferredDate.setOnClickListener(v -> showDatePicker());
        tilPreferredDate.setEndIconOnClickListener(v -> showDatePicker());
    }

    private void showPendingRequests(List<BookingCalendarResponse.CalendarData.PendingRequest> requests) {
        layoutPendingRequests.removeAllViews();

        if (requests == null || requests.isEmpty()) {
            cardPendingRequests.setVisibility(View.GONE);
            return;
        }

        cardPendingRequests.setVisibility(View.VISIBLE);

        for (int i = 0; i < requests.size(); i++) {
            BookingCalendarResponse.CalendarData.PendingRequest pr = requests.get(i);

            // Add divider between items
            if (i > 0) {
                View divider = new View(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 1));
                divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider));
                layoutPendingRequests.addView(divider);
            }

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setPadding(0, 12, 0, 12);

            // Info column
            LinearLayout infoCol = new LinearLayout(this);
            infoCol.setOrientation(LinearLayout.VERTICAL);
            infoCol.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            String info = "Dr. " + pr.getDoctor_name() + " · " +
                    formatDisplayDate(pr.getRequested_date()) + " · " +
                    formatDisplayTime(pr.getRequested_time());

            TextView tvInfo = new TextView(this);
            tvInfo.setText(info);
            tvInfo.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvInfo.setTextSize(13);
            tvInfo.setTypeface(null, android.graphics.Typeface.BOLD);
            infoCol.addView(tvInfo);

            String details = pr.getTreatment_type();
            if (pr.getDescription() != null && !pr.getDescription().isEmpty()) {
                details += " · " + pr.getDescription();
            }
            if (pr.getCreated_at() != null) {
                details += " · Submitted " + pr.getCreated_at();
            }

            TextView tvDetails = new TextView(this);
            tvDetails.setText(details);
            tvDetails.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tvDetails.setTextSize(11);
            tvDetails.setPadding(0, 4, 0, 0);
            infoCol.addView(tvDetails);

            item.addView(infoCol);

            // Cancel button - smaller size
            AppCompatButton btnCancelReq = new AppCompatButton(this);
            btnCancelReq.setText("Cancel");
            btnCancelReq.setTextColor(Color.WHITE);
            btnCancelReq.setTextSize(10); // Smaller text
            btnCancelReq.setAllCaps(false);
            btnCancelReq.setBackgroundResource(R.drawable.button_red_gradient);
            btnCancelReq.setPadding(8, 2, 8, 2); // Less padding

// Fixed smaller size
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    dpToPx(60), dpToPx(28)); // 60dp wide, 28dp tall
            btnCancelReq.setLayoutParams(btnParams);
            final int requestId = pr.getId();
            btnCancelReq.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(BookAppointment.this)
                        .setTitle("Cancel Request")
                        .setMessage("Cancel this booking request?")
                        .setPositiveButton("Yes", (d, w) -> cancelPendingRequest(requestId))
                        .setNegativeButton("No", null)
                        .show();
            });

            item.addView(btnCancelReq);
            layoutPendingRequests.addView(item);
        }
    }

    private String formatDisplayDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int m = Integer.parseInt(parts[1]);
                return months[m - 1] + " " + Integer.parseInt(parts[2]) + ", " + parts[0];
            }
        } catch (Exception e) {}
        return dateStr;
    }

    private String formatDisplayTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return "";
        try {
            String[] parts = timeStr.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            String amPm = h >= 12 ? "PM" : "AM";
            if (h > 12) h -= 12;
            if (h == 0) h = 12;
            return h + ":" + String.format("%02d", m) + " " + amPm;
        } catch (Exception e) {}
        return timeStr;
    }
    private void cancelPendingRequest(int requestId) {
        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();

        CancelRequest cancelRequest = new CancelRequest(requestId);

        RetrofitClient.getClient().create(ApiService.class)
                .cancelRequest(token, cancelRequest)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> c, Response<GenericResponse> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            Toast.makeText(BookAppointment.this, "Request cancelled", Toast.LENGTH_SHORT).show();
                            loadBookingData(); // Refresh to update pending list
                        } else {
                            Toast.makeText(BookAppointment.this, "Failed to cancel", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<GenericResponse> c, Throwable t) {
                        Toast.makeText(BookAppointment.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String existingDate = etPreferredDate.getText() != null ? etPreferredDate.getText().toString().trim() : "";
        if (!existingDate.isEmpty() && existingDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
            String[] parts = existingDate.split("/");
            month = Integer.parseInt(parts[0]) - 1;
            day = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]);
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                (DatePicker view, int y, int m, int d) ->
                        etPreferredDate.setText(String.format(Locale.US, "%02d/%02d/%04d", m + 1, d, y)),
                year, month, day);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void setupClickListeners() {
        btnPrevWeek.setOnClickListener(v -> { currentWeekOffset--; loadCalendar(); });
        btnNextWeek.setOnClickListener(v -> { currentWeekOffset++; loadCalendar(); });
        btnThisWeek.setOnClickListener(v -> { currentWeekOffset = 0; loadCalendar(); });
        btnSubmitRequest.setOnClickListener(v -> submitAppointment());
        btnCancel.setOnClickListener(v -> finish());

        spinnerCalendarDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos >= 0 && pos < doctorList.size()) {
                    BookingCalendarResponse.DoctorInfo doc = doctorList.get(pos);
                    currentDoctorId = doc.getId();
                    currentDoctorName = doc.getFull_name();
                    currentWeekOffset = 0;
                    loadCalendar();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadBookingData() {
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) { finish(); return; }

        String token = "Bearer " + session.getToken();
        RetrofitClient.getClient().create(ApiService.class)
                .getBookingCalendar(token, 0, 0)
                .enqueue(new Callback<BookingCalendarResponse>() {
                    @Override
                    public void onResponse(Call<BookingCalendarResponse> c, Response<BookingCalendarResponse> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            BookingCalendarResponse.CalendarData data = r.body().getData();
                            if (data != null) {
                                doctorList = data.getDoctors() != null ? data.getDoctors() : new ArrayList<>();
                                visitTypeList = data.getVisit_types() != null ? data.getVisit_types() : new ArrayList<>();

                                if (data.getPending_requests() != null) {
                                    showPendingRequests(data.getPending_requests());
                                }
                                ArrayAdapter<BookingCalendarResponse.DoctorInfo> docAdapter =
                                        new ArrayAdapter<>(BookAppointment.this,
                                                android.R.layout.simple_dropdown_item_1line, doctorList);
                                spinnerCalendarDoctor.setAdapter(docAdapter);
                                spinnerDentist.setAdapter(docAdapter);

                                ArrayAdapter<BookingCalendarResponse.VisitTypeInfo> vtAdapter =
                                        new ArrayAdapter<>(BookAppointment.this,
                                                android.R.layout.simple_dropdown_item_1line, visitTypeList);
                                spinnerVisitType.setAdapter(vtAdapter);

                                if (!doctorList.isEmpty()) {
                                    currentDoctorId = doctorList.get(0).getId();
                                    currentDoctorName = doctorList.get(0).getFull_name();
                                    loadCalendar();
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<BookingCalendarResponse> c, Throwable t) {}
                });
    }

    private void loadCalendar() {
        if (currentDoctorId <= 0) return;

        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();

        RetrofitClient.getClient().create(ApiService.class)
                .getBookingCalendar(token, currentDoctorId, currentWeekOffset)
                .enqueue(new Callback<BookingCalendarResponse>() {
                    @Override
                    public void onResponse(Call<BookingCalendarResponse> c, Response<BookingCalendarResponse> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            BookingCalendarResponse.CalendarData data = r.body().getData();
                            if (data != null) {
                                List<BookingCalendarResponse.WeekDayInfo> weekDays = data.getWeek_days();
                                updateWeekRange(data.getWeek_start(), data.getWeek_end());
                                buildCalendarGrid(weekDays);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<BookingCalendarResponse> c, Throwable t) {}
                });
    }

    private void buildCalendarGrid(List<BookingCalendarResponse.WeekDayInfo> weekDays) {
        LinearLayout container = findViewById(R.id.layoutCalendarContainer);
        container.removeAllViews();

        if (weekDays == null || weekDays.isEmpty()) return;

        // Collect unique times
        List<String> allTimes = new ArrayList<>();
        for (BookingCalendarResponse.WeekDayInfo day : weekDays) {
            if (day.getSlots() != null) {
                for (BookingCalendarResponse.SlotInfo slot : day.getSlots()) {
                    if (!allTimes.contains(slot.getTime())) {
                        allTimes.add(slot.getTime());
                    }
                }
            }
        }
        java.util.Collections.sort(allTimes);
        if (allTimes.isEmpty()) return;

        // Calculate equal column width based on screen width
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int timeColWidth = dpToPx(51);
        int dayColWidth = dpToPx(40); // Fixed equal width for all day columns// 24 for padding
        int cellHeight = dpToPx(33); // Taller rows

        // Build table layout
        LinearLayout tableLayout = new LinearLayout(this);
        tableLayout.setOrientation(LinearLayout.VERTICAL);

        // Header row
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView timeHeader = createCell("Time", timeColWidth, cellHeight, true);
        headerRow.addView(timeHeader);

        for (BookingCalendarResponse.WeekDayInfo day : weekDays) {
            LinearLayout dayCol = new LinearLayout(this);
            dayCol.setOrientation(LinearLayout.VERTICAL);
            dayCol.setGravity(Gravity.CENTER);
            dayCol.setMinimumWidth(dayColWidth);

            TextView dayText = new TextView(this);
            dayText.setText(day.getDisplay_day() != null ? day.getDisplay_day() : "");
            dayText.setTextSize(10);
            dayText.setTypeface(null, android.graphics.Typeface.BOLD);
            dayText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            dayText.setGravity(Gravity.CENTER);

            TextView dateText = new TextView(this);
            dateText.setText(day.getDisplay_date() != null ? day.getDisplay_date() : "");
            dateText.setTextSize(9);
            dateText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            dateText.setGravity(Gravity.CENTER);

            dayCol.addView(dayText);
            dayCol.addView(dateText);
            headerRow.addView(dayCol);
        }
        tableLayout.addView(headerRow);

        // Add a thin spacer after header row
        View headerSpacer = new View(this);
        headerSpacer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1)));
        headerSpacer.setBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
        tableLayout.addView(headerSpacer);

        // Divider
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider));
        tableLayout.addView(divider);

        // Data rows
        for (String timeHis : allTimes) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setMinimumHeight(cellHeight);

            // Time label
            String label = timeHis.substring(0, 5);
            for (BookingCalendarResponse.WeekDayInfo day : weekDays) {
                if (day.getSlots() != null) {
                    for (BookingCalendarResponse.SlotInfo slot : day.getSlots()) {
                        if (slot.getTime().equals(timeHis)) {
                            label = slot.getLabel();
                            break;
                        }
                    }
                }
            }

            TextView timeCell = createCell(label, timeColWidth, cellHeight, false);
            timeCell.setTextSize(9);
            row.addView(timeCell);



            // Slot cells for each day
            for (final BookingCalendarResponse.WeekDayInfo day : weekDays) {
                BookingCalendarResponse.SlotInfo foundSlot = null;
                if (day.getSlots() != null) {
                    for (BookingCalendarResponse.SlotInfo slot : day.getSlots()) {
                        if (slot.getTime().equals(timeHis)) {
                            foundSlot = slot;
                            break;
                        }
                    }
                }

                if (foundSlot == null) {
                    TextView emptyCell = createCell("", dayColWidth, cellHeight, false);
                    emptyCell.setBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
                    row.addView(emptyCell);
                } else {
                    TextView slotCell = new TextView(this);
                    slotCell.setGravity(Gravity.CENTER);
                    slotCell.setPadding(3, 4, 3, 4);
                    slotCell.setTextSize(9);
                    slotCell.setMinWidth(dayColWidth);
                    slotCell.setMinHeight(cellHeight);

// Add margins for column spacing
                    LinearLayout.LayoutParams slotParams = new LinearLayout.LayoutParams(
                            dayColWidth, cellHeight);
                    slotParams.setMargins(dpToPx(1), 0, dpToPx(1), 0);
                    slotCell.setLayoutParams(slotParams);

                    final String finalDateYmd = day.getDate_ymd();
                    final String finalTimeHis = foundSlot.getTime();
                    final String finalSlotLabel = foundSlot.getLabel();
                    final String finalDoctorName = currentDoctorName;

                    switch (foundSlot.getState()) {
                        case "free":
                            slotCell.setText("+");
                            slotCell.setTextColor(Color.WHITE);
                            slotCell.setTextSize(14);
                            slotCell.setTypeface(null, android.graphics.Typeface.BOLD);
                            slotCell.setBackgroundResource(R.drawable.bg_slot_free);
                            slotCell.setClickable(true);
                            slotCell.setOnClickListener(v -> {
                                selectedDateYmd = finalDateYmd;
                                selectedTimeHis = finalTimeHis;
                                showBookingModal(finalDoctorName, finalSlotLabel);
                            });
                            break;
                        case "busy":
                            slotCell.setText("Taken");
                            slotCell.setTextColor(Color.WHITE);
                            slotCell.setBackgroundResource(R.drawable.bg_slot_busy);
                            break;
                        default: // past
                            slotCell.setText("Past");
                            slotCell.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
                            slotCell.setTextSize(8);
                            slotCell.setBackgroundResource(R.drawable.bg_slot_past);
                            break;
                    }
                    row.addView(slotCell);
                }
            }
            tableLayout.addView(row);
            // Add a 1dp spacer between rows
            View rowSpacer = new View(this);
            rowSpacer.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(2)));
            rowSpacer.setBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
            tableLayout.addView(rowSpacer);
        }

        container.addView(tableLayout);
    }

    private TextView createCell(String text, int widthPx, int heightPx, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setMinWidth(widthPx);
        tv.setMinHeight(heightPx);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(10);
        tv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tv.setPadding(7, 7, 7, 7);
        if (bold) tv.setTypeface(null, android.graphics.Typeface.BOLD);
        return tv;
    }



    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
    private List<com.example.dentassist.models.WeekDayData> convertWeekDays(List<BookingCalendarResponse.WeekDayInfo> apiDays) {
        List<com.example.dentassist.models.WeekDayData> result = new ArrayList<>();
        if (apiDays == null) return result;

        for (BookingCalendarResponse.WeekDayInfo apiDay : apiDays) {
            List<com.example.dentassist.models.CalendarSlot> slots = new ArrayList<>();
            if (apiDay.getSlots() != null) {
                for (BookingCalendarResponse.SlotInfo apiSlot : apiDay.getSlots()) {
                    com.example.dentassist.models.SlotState state;
                    switch (apiSlot.getState()) {
                        case "free": state = com.example.dentassist.models.SlotState.FREE; break;
                        case "busy": state = com.example.dentassist.models.SlotState.BUSY; break;
                        default: state = com.example.dentassist.models.SlotState.PAST; break;
                    }
                    slots.add(new com.example.dentassist.models.CalendarSlot(
                            apiSlot.getTime(), apiSlot.getLabel(), state));
                }
            }
            result.add(new com.example.dentassist.models.WeekDayData(
                    apiDay.getDate_ymd(), apiDay.getDisplay_date(), apiDay.getDisplay_day(), slots));
        }
        return result;
    }

    private void updateWeekRange(String start, String end) {
        String formattedStart = formatWeekDate(start);
        String formattedEnd = formatWeekDate(end);
        tvWeekRange.setText(formattedStart + " – " + formattedEnd);
    }

    private String formatWeekDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                if (month >= 1 && month <= 12) {
                    return months[month - 1] + " " + day;
                }
            }
        } catch (Exception e) {}
        return dateStr;
    }

    private void showBookingModal(String doctorName, String slotLabel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_booking, null);
        builder.setView(dialogView);

        TextView tvModalDoctor = dialogView.findViewById(R.id.tvModalDoctor);
        TextView tvModalWhen = dialogView.findViewById(R.id.tvModalWhen);
        Spinner spinnerModalVisitType = dialogView.findViewById(R.id.spinnerModalVisitType);
        TextInputEditText etModalNotes = dialogView.findViewById(R.id.etModalNotes);
        AppCompatButton btnModalCancel = dialogView.findViewById(R.id.btnModalCancel);
        AppCompatButton btnModalConfirm = dialogView.findViewById(R.id.btnModalConfirm);

        tvModalDoctor.setText(doctorName);
        tvModalWhen.setText(slotLabel);

        ArrayAdapter<BookingCalendarResponse.VisitTypeInfo> visitTypeAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, visitTypeList);
        visitTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModalVisitType.setAdapter(visitTypeAdapter);

        AlertDialog dialog = builder.create();

        btnModalCancel.setOnClickListener(v -> dialog.dismiss());

        btnModalConfirm.setOnClickListener(v -> {
            // ✅ Disable button immediately to prevent double-click
            btnModalConfirm.setEnabled(false);

            BookingCalendarResponse.VisitTypeInfo selectedVisitType =
                    (BookingCalendarResponse.VisitTypeInfo) spinnerModalVisitType.getSelectedItem();
            String notes = etModalNotes.getText() != null ? etModalNotes.getText().toString().trim() : "";

            if (selectedVisitType == null) {
                Toast.makeText(BookAppointment.this, "Please select a visit type", Toast.LENGTH_SHORT).show();
                btnModalConfirm.setEnabled(true); // Re-enable if validation fails
                return;
            }

            final String type = selectedVisitType.getName();
            final String noteText = notes;
            dialog.dismiss();
            submitBooking(type, noteText);
        });

        dialog.show();
    }

    private void submitBooking(String treatmentType, String notes) {
        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();

        BookAppointmentRequest request = new BookAppointmentRequest(
                currentDoctorId, selectedDateYmd, selectedTimeHis.substring(0, 5), treatmentType, notes);

        RetrofitClient.getClient().create(ApiService.class)
                .bookAppointment(token, request)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> c, Response<GenericResponse> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            Toast.makeText(BookAppointment.this, r.body().getMessage(), Toast.LENGTH_LONG).show();
                            etPreferredDate.setText(selectedDateYmd);
                            loadCalendar();
                            loadBookingData(); // ✅ Refresh pending requests
                        } else {
                            Toast.makeText(BookAppointment.this,
                                    r.body() != null ? r.body().getMessage() : "Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<GenericResponse> c, Throwable t) {
                        Toast.makeText(BookAppointment.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void submitAppointment() {
        String preferredDate = etPreferredDate.getText() != null ? etPreferredDate.getText().toString().trim() : "";

        if (preferredDate.isEmpty()) {
            tilPreferredDate.setError("Preferred date is required");
            return;
        }
        tilPreferredDate.setError(null);

        // Get selected dentist from AutoCompleteTextView text
        String dentistText = spinnerDentist.getText() != null ? spinnerDentist.getText().toString().trim() : "";
        String visitTypeText = spinnerVisitType.getText() != null ? spinnerVisitType.getText().toString().trim() : "";

        if (dentistText.isEmpty()) {
            Toast.makeText(this, "Please select a dentist", Toast.LENGTH_SHORT).show();
            return;
        }
        if (visitTypeText.isEmpty()) {
            Toast.makeText(this, "Please select a visit type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find the doctor object matching the selected text
        BookingCalendarResponse.DoctorInfo selectedDoctor = null;
        for (BookingCalendarResponse.DoctorInfo doc : doctorList) {
            if (doc.getFull_name().equals(dentistText)) {
                selectedDoctor = doc;
                break;
            }
        }

        // Find the visit type object matching the selected text
        BookingCalendarResponse.VisitTypeInfo selectedType = null;
        for (BookingCalendarResponse.VisitTypeInfo vt : visitTypeList) {
            if (vt.getName().equals(visitTypeText)) {
                selectedType = vt;
                break;
            }
        }

        if (selectedDoctor == null) {
            Toast.makeText(this, "Please select a valid dentist", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedType == null) {
            Toast.makeText(this, "Please select a valid visit type", Toast.LENGTH_SHORT).show();
            return;
        }

        String priority = spinnerPriority.getSelectedItem() != null ?
                spinnerPriority.getSelectedItem().toString() : "medium";
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

        // Convert MM/DD/YYYY to YYYY-MM-DD
        String[] parts = preferredDate.split("/");
        String dbDate = parts[2] + "-" + parts[0] + "-" + parts[1];

        // Show loading
        btnSubmitRequest.setEnabled(false);
        btnSubmitRequest.setText("Submitting...");

        QueueRequestModel request = new QueueRequestModel(
                selectedDoctor.getId(), dbDate, selectedType.getName(), priority, notes);

        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();

        RetrofitClient.getClient().create(ApiService.class)
                .submitQueueRequest(token, request)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> c, Response<GenericResponse> r) {
                        btnSubmitRequest.setEnabled(true);
                        btnSubmitRequest.setText("Submit");

                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            Toast.makeText(BookAppointment.this, r.body().getMessage(), Toast.LENGTH_LONG).show();
                            etPreferredDate.setText("");
                            etNotes.setText("");
                        } else {
                            String error = r.body() != null ? r.body().getMessage() : "Failed";
                            Toast.makeText(BookAppointment.this, error, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<GenericResponse> c, Throwable t) {
                        btnSubmitRequest.setEnabled(true);
                        btnSubmitRequest.setText("Submit");
                        Toast.makeText(BookAppointment.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}