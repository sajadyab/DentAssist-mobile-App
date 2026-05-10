package com.example.dentassist.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.dentassist.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneInputView extends LinearLayout {

    private Spinner spinnerCountryCode;
    private TextInputEditText etPhoneNumber;
    private PhoneNumberUtil phoneUtil;

    private String currentDialCode = "961";
    private String currentIsoCode = "LB";

    // Country dial codes (matches spinner order)
    private static final String[] COUNTRY_DIAL_CODES = {
            "961", "1", "1", "44", "33", "49", "39", "34", "86", "91", "92", "81", "82", "61", "55", "20", "27", "971", "966"
    };

    // Country ISO codes (for validation)
    private static final String[] COUNTRY_ISO_CODES = {
            "LB", "US", "59", "CA", "GB", "FR", "DE", "IT", "ES", "CN", "IN", "PK", "JP", "KR", "59", "AU", "BR", "EG", "ZA", "AE", "SA"
    };

    // Phone number length limits by country
    private static final java.util.Map<String, Integer> PHONE_LIMITS = new java.util.HashMap<String, Integer>() {{
        put("LB", 8);   // Lebanon
        put("US", 10);  // United States
        put("CA", 10);  // Canada
        put("GB", 10);  // United Kingdom
        put("FR", 9);   // France
        put("DE", 10);  // Germany
        put("IT", 10);  // Italy
        put("ES", 9);   // Spain
        put("CN", 11);  // China
        put("IN", 10);  // India
        put("PK", 10);  // Pakistan
        put("JP", 10);  // Japan
        put("KR", 11);  // South Korea
        put("AU", 9);   // Australia
        put("BR", 11);  // Brazil
        put("EG", 10);  // Egypt
        put("ZA", 9);   // South Africa
        put("AE", 9);   // UAE
        put("59", 9);   // Saudi Arabia
    }};

    public PhoneInputView(Context context) {
        super(context);
        init(context);
    }

    public PhoneInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PhoneInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_phone_input, this, true);
        setOrientation(HORIZONTAL);
        setGravity(android.view.Gravity.CENTER_VERTICAL);

        phoneUtil = PhoneNumberUtil.getInstance();

        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        setupSpinner();
        setupPhoneNumberLimit();
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.country_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountryCode.setAdapter(adapter);
        spinnerCountryCode.setSelection(0);
    }

    private void setupPhoneNumberLimit() {
        spinnerCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentIsoCode = COUNTRY_ISO_CODES[position];
                currentDialCode = COUNTRY_DIAL_CODES[position];
                Integer limit = PHONE_LIMITS.getOrDefault(currentIsoCode, 15);
                etPhoneNumber.setFilters(new android.text.InputFilter[]{
                        new android.text.InputFilter.LengthFilter(limit)
                });

                String current = etPhoneNumber.getText().toString();
                if (current.length() > limit) {
                    etPhoneNumber.setText(current.substring(0, limit));
                    etPhoneNumber.setSelection(limit);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.matches("^\\d*$")) {
                    String filtered = text.replaceAll("[^\\d]", "");
                    etPhoneNumber.setText(filtered);
                    etPhoneNumber.setSelection(filtered.length());
                }
            }
        });
    }

    /**
     * Get the full phone number with country code (e.g., "+96170123456")
     */
    public String getFullPhoneNumber() {
        String number = etPhoneNumber.getText().toString().trim();
        if (number.isEmpty()) {
            return "";
        }
        return "+" + currentDialCode + number;
    }

    /**
     * Get just the country dial code (e.g., "961")
     */
    public String getCountryDialCode() {
        return currentDialCode;
    }

    /**
     * Get just the country ISO code (e.g., "LB")
     */
    public String getCountryIsoCode() {
        return currentIsoCode;
    }

    /**
     * Get just the local number (without country code)
     */
    public String getLocalNumber() {
        return etPhoneNumber.getText().toString().trim();
    }

    /**
     * Get the expected digit length for the selected country
     */
    public int getExpectedLength() {
        return PHONE_LIMITS.getOrDefault(currentIsoCode, 15);
    }

    /**
     * Validate if the phone number is valid for the selected country
     */
    public boolean isValid() {
        String fullNumber = getFullPhoneNumber();
        if (fullNumber.isEmpty()) {
            return false;
        }

        try {
            Phonenumber.PhoneNumber parsed = phoneUtil.parse(fullNumber, currentIsoCode);
            return phoneUtil.isValidNumber(parsed);
        } catch (NumberParseException e) {
            return false;
        }
    }

    /**
     * Check if the field is empty
     */
    public boolean isEmpty() {
        return etPhoneNumber.getText().toString().trim().isEmpty();
    }

    /**
     * Set an existing phone number (format: "+96170123456")
     */
    public void setPhoneNumber(String fullNumber) {
        if (fullNumber == null || fullNumber.isEmpty()) {
            return;
        }

        try {
            Phonenumber.PhoneNumber parsed = phoneUtil.parse(fullNumber, null);
            String countryCode = String.valueOf(parsed.getCountryCode());
            String nationalNumber = String.valueOf(parsed.getNationalNumber());

            for (int i = 0; i < COUNTRY_DIAL_CODES.length; i++) {
                if (COUNTRY_DIAL_CODES[i].equals(countryCode)) {
                    spinnerCountryCode.setSelection(i);
                    break;
                }
            }

            etPhoneNumber.setText(nationalNumber);
        } catch (NumberParseException e) {
            etPhoneNumber.setText(fullNumber);
        }
    }

    /**
     * Clear the phone number field
     */
    public void clear() {
        etPhoneNumber.setText("");
        spinnerCountryCode.setSelection(0);
    }
}