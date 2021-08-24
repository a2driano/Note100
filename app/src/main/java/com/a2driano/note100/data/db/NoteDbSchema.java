package com.a2driano.note100.data.db;

/**
 * Created by Andrii Papai on 21.12.2016.
 */

public class NoteDbSchema {
    public static final class NoteTable {
        public static final String NAME = "notes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TEXT = "note_text";
            public static final String DATE = "date";
            public static final String COLOR = "color";
        }
    }
}
