package com.feup.pesi.calmdown;

public class LocalDataContract {
    private LocalDataContract() {}

    public static class LocalDataEntry {
        public static final String TABLE_NAME = "jacket_data";
        public static final String COLUMN_ID = "id"; // Assuming you have an ID column as the primary key
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_BATTERY_LEVEL = "battery_level";
        public static final String COLUMN_BPM = "bpm";
        public static final String COLUMN_BPMI = "bpmi";
        public static final String COLUMN_NBYTES = "nbytes";
        public static final String COLUMN_NLEADS = "nleads";
        public static final String COLUMN_POSITION = "position";
        public static final String COLUMN_PULSE = "pulse";
        public static final String COLUMN_RR = "rr";
        public static final String COLUMN_DATE_TIME_SPAN = "date_time_span";
        public static final String COLUMN_USER_ID = "user_id";
    }

    public static class AverageDataEntry {
        public static final String TABLE_NAME = "average_data";
        public static final String COLUMN_ID = "id"; // Assuming you have an ID column as the primary key
        public static final String COLUMN_AVERAGE_PULSE = "average_pulse";
        public static final String COLUMN_AVERAGE_BATTERY_LEVEL = "average_battery_level";
        public static final String COLUMN_AVERAGE_POSITION = "average_position";
        public static final String COLUMN_AVERAGE_RR = "average_rr";
        public static final String COLUMN_AVERAGE_BPMI = "average_bpmi";
        public static final String COLUMN_AVERAGE_BPM = "average_bpm";
        public static final String COLUMN_AVERAGE_NLEADS = "average_nleads";
        public static final String COLUMN_AVERAGE_NBYTES = "average_nbytes";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_DATE_TIME = "date_time";


    }
}
