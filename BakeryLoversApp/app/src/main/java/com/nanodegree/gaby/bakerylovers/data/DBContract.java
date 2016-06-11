package com.nanodegree.gaby.bakerylovers.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DBContract {
    public static final String CONTENT_AUTHORITY = "com.nanodegree.gaby.bakerylovers";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class UserEntry implements BaseColumns {
        //table name
        public static final String TABLE_NAME = "user_table";

        //columns
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_EMAIL = "user_email";
        public static final String COLUMN_FULL_NAME = "user_name";
        public static final String COLUMN_PHONE_NUMBER = "user_phone_number";
        public static final String COLUMN_ADDRESS = "user_address";
        public static final String COLUMN_GOOGLE_ACCOUNT= "user_google_account";

        //table create query
        public static final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_USER_ID + " INTEGER UNIQUE NOT NULL, " +
                COLUMN_EMAIL + " TEXT NOT NULL, " +
                COLUMN_FULL_NAME + " TEXT NOT NULL, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_PHONE_NUMBER + " NUMERIC, " +
                COLUMN_GOOGLE_ACCOUNT + " INTEGER DEFAULT 0" +
                ");";

        //Cursors
        public static final String[] DETAIL_COLUMNS = {
                _ID,
                COLUMN_USER_ID,
                COLUMN_FULL_NAME,
                COLUMN_EMAIL,
                COLUMN_ADDRESS,
                COLUMN_PHONE_NUMBER,
                COLUMN_GOOGLE_ACCOUNT
        };

        public static final int COLUMN_USER_ID_INDEX = 1;
        public static final int COLUMN_FULL_NAME_INDEX = 2;
        public static final int COLUMN_EMAIL_INDEX = 3;
        public static final int COLUMN_ADDRESS_INDEX = 4;
        public static final int COLUMN_PHONE_NUMBER_INDEX = 5;
        public static final int COLUMN_GOOGLE_ACCOUNT_INDEX = 6;

        //Uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ProductEntry implements BaseColumns {
        //table name
        public static final String TABLE_NAME = "product_table";

        //columns
        public static final String COLUMN_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME = "product_name";
        public static final String COLUMN_DESCRIPTION = "product_desc";
        public static final String COLUMN_PRICE = "product_price";
        public static final String COLUMN_AVAILABLE = "product_available";
        public static final String COLUMN_NUTRITIONAL_VALUE = "product_nut_value";
        public static final String COLUMN_PHOTO_URL = "product_photo_url";

        // join with current table
        public static final String PRODUCT_CURRENT_JOIN = TABLE_NAME + " LEFT OUTER JOIN " + CurrentOrderEntry.TABLE_NAME +
                " ON " + COLUMN_PRODUCT_ID + " = " + CurrentOrderEntry.COLUMN_PRODUCT_ID;
        public static final String CURRENT_PATH = "current";

        //table create query
        public static final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_PRODUCT_ID + " INTEGER UNIQUE NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_PRICE + " REAL NOT NULL, " +
                COLUMN_PHOTO_URL + " TEXT, " +
                COLUMN_NUTRITIONAL_VALUE + " REAL, " +
                COLUMN_AVAILABLE + " INTEGER DEFAULT 1" +
                ");";

        //Cursors
        public static final String[] DETAIL_COLUMNS = {
                _ID,
                COLUMN_PRODUCT_ID,
                COLUMN_NAME,
                COLUMN_DESCRIPTION,
                COLUMN_PRICE,
                COLUMN_PHOTO_URL,
                COLUMN_NUTRITIONAL_VALUE,
                COLUMN_AVAILABLE
        };

        public static final String[] DETAIL_COLUMNS_WITH_CURRENT = {
                TABLE_NAME+"."+_ID + " AS "+ _ID,
                COLUMN_PRODUCT_ID,
                COLUMN_NAME,
                COLUMN_DESCRIPTION,
                COLUMN_PRICE,
                COLUMN_PHOTO_URL,
                COLUMN_NUTRITIONAL_VALUE,
                COLUMN_AVAILABLE,
                CurrentOrderEntry.COLUMN_AMOUNT
        };

        public static final int COLUMN_PRODUCT_ID_INDEX = 1;
        public static final int COLUMN_NAME_INDEX = 2;
        public static final int COLUMN_DESCRIPTION_INDEX = 3;
        public static final int COLUMN_PRICE_INDEX = 4;
        public static final int COLUMN_PHOTO_URL_INDEX = 5;
        public static final int COLUMN_NUTRITIONAL_VALUE_INDEX = 6;
        public static final int COLUMN_AVAILABLE_INDEX = 7;
        public static final int COLUMN_CURRENT_AMOUNT_INDEX = 8;

        //Uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildProductUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildProductCurrentUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).appendPath(CURRENT_PATH).build();
        }
    }

    public static final class OrderEntry implements BaseColumns {
        //table name
        public static final String TABLE_NAME = "order_table";

        //columns
        public static final String COLUMN_ORDER_ID = "order_id";
        public static final String COLUMN_USER_ID = "order_user_id";
        public static final String COLUMN_PLACED_DATE = "order_placed_date";
        public static final String COLUMN_DELIVERED_DATE = "order_delivered_date";
        public static final String COLUMN_TOTAL_PRICE = "order_total_price";
        public static final String COLUMN_TOTAL_DELIVERY = "order_total_delivery";

        //table create query
        public static final String SQL_CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ORDER_ID + " INTEGER UNIQUE NOT NULL, " +
                COLUMN_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_PLACED_DATE + " TEXT NOT NULL, " +
                COLUMN_TOTAL_PRICE + " REAL NOT NULL, " +
                COLUMN_TOTAL_DELIVERY + " REAL NOT NULL, " +
                COLUMN_DELIVERED_DATE + " TEXT, " +
                "CONSTRAINT 'fk_order_user_id' FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_USER_ID + ") ON DELETE CASCADE" +
                ");";

        //Cursors
        public static final String[] DETAIL_COLUMNS = {
                _ID,
                COLUMN_ORDER_ID,
                COLUMN_USER_ID,
                COLUMN_PLACED_DATE,
                COLUMN_TOTAL_PRICE,
                COLUMN_TOTAL_DELIVERY,
                COLUMN_DELIVERED_DATE
        };

        public static final int COLUMN_ORDER_ID_INDEX = 1;
        public static final int COLUMN_USER_ID_INDEX = 2;
        public static final int COLUMN_PLACED_DATE_INDEX = 3;
        public static final int COLUMN_TOTAL_PRICE_INDEX = 4;
        public static final int COLUMN_TOTAL_DELIVERY_INDEX = 5;
        public static final int COLUMN_DELIVERED_DATE_INDEX = 6;

        //Uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildOrderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class OrderDetailEntry implements BaseColumns {
        //table name
        public static final String TABLE_NAME = "order_detail";

        //columns
        public static final String COLUMN_ORDER_ID = "detail_order_id";
        public static final String COLUMN_PRODUCT_ID = "detail_product_id";
        public static final String COLUMN_AMOUNT = "detail_amount";
        public static final String COLUMN_PRICE_UND = "detail_price_und";
        public static final String COLUMN_TOTAL_PRICE = "detail_total_price";

        //table create query
        public static final String SQL_CREATE_ORDER_DETAIL_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ORDER_ID + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
                COLUMN_AMOUNT + " REAL NOT NULL, " +
                COLUMN_PRICE_UND + " REAL NOT NULL, " +
                COLUMN_TOTAL_PRICE + " REAL NOT NULL, " +
                "CONSTRAINT 'fk_order_detail_order' FOREIGN KEY(" + COLUMN_ORDER_ID + ") REFERENCES " + OrderEntry.TABLE_NAME + "(" + OrderEntry.COLUMN_ORDER_ID+ ") ON DELETE CASCADE, " +
                "CONSTRAINT 'fk_order_detail_product' FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " + ProductEntry.TABLE_NAME + "(" + ProductEntry.COLUMN_PRODUCT_ID + "), " +
                "CONSTRAINT 'unique_order_detail_product_order' UNIQUE (" + COLUMN_ORDER_ID + "," + COLUMN_PRODUCT_ID + ") ON CONFLICT REPLACE" +
                ");";

        //Cursors
        public static final String[] DETAIL_COLUMNS = {
                _ID,
                COLUMN_ORDER_ID,
                COLUMN_PRODUCT_ID,
                COLUMN_AMOUNT,
                COLUMN_PRICE_UND,
                COLUMN_TOTAL_PRICE
        };

        public static final int COLUMN_ORDER_ID_INDEX = 1;
        public static final int COLUMN_PRODUCT_ID_INDEX = 2;
        public static final int COLUMN_AMOUNT_INDEX = 3;
        public static final int COLUMN_PRICE_UND_INDEX = 4;
        public static final int COLUMN_TOTAL_PRICE_INDEX = 5;

        //Uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildOrderDetailUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CurrentOrderEntry implements BaseColumns {
        //table name
        public static final String TABLE_NAME = "current_order";

        //columns
        public static final String COLUMN_PRODUCT_ID = "current_product_id";
        public static final String COLUMN_AMOUNT = "current_amount";
        public static final String COLUMN_PRICE_UND = "current_price_und";

        // join with product table
        public static final String CURRENT_PRODUCT_JOIN = TABLE_NAME + " INNER JOIN " + ProductEntry.TABLE_NAME +
                " ON " + COLUMN_PRODUCT_ID + " = " + ProductEntry.COLUMN_PRODUCT_ID;
        public static final String CURRENT_PATH = "current";

        //table create query
        public static final String SQL_CREATE_CURRENT_ORDER_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
                COLUMN_AMOUNT + " INTEGER NOT NULL, " +
                COLUMN_PRICE_UND + " REAL NOT NULL, " +
                "CONSTRAINT 'fk_current_order_product' FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " + ProductEntry.TABLE_NAME + "(" + ProductEntry.COLUMN_PRODUCT_ID + "), " +
                "CONSTRAINT 'unique_current_order_product' UNIQUE (" + COLUMN_PRODUCT_ID + ") ON CONFLICT REPLACE" +
                ");";

        //Cursors
        public static final String[] DETAIL_COLUMNS = {
                TABLE_NAME+"."+_ID + " AS "+ _ID,
                COLUMN_PRODUCT_ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_PHOTO_URL,
                COLUMN_AMOUNT,
                COLUMN_PRICE_UND
        };

        public static final String[] TOTAL_ORDER_COLUMN = {
                "SUM(" + COLUMN_AMOUNT + ")",
                "SUM(" + COLUMN_AMOUNT + "*" + COLUMN_PRICE_UND + ")"
        };

        public static final int COLUMN_PRODUCT_ID_INDEX = 1;
        public static final int COLUMN_PRODUCT_NAME_INDEX = 2;
        public static final int COLUMN_PRODUCT_PHOTO_URL_INDEX = 3;
        public static final int COLUMN_AMOUNT_INDEX = 4;
        public static final int COLUMN_PRICE_UND_INDEX = 5;

        //Uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildCurrentOrderByIdUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
