package com.reactnative.webkitlocalstoragereader;

import java.lang.Exception;
import java.lang.StringBuilder;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

class Database {
  private static final String TAG = "ReactNativeWebkitLocalStorageReaderDatabase";
  private static final String DBPATH = "/data/user/0/";
  private static final String DBNAME = "/app_xwalkcore/Default/Local Storage/file__0.localstorage";

  private SQLiteDatabase db;
  private final ReactApplicationContext reactContext;

  public Database(ReactApplicationContext reactContext) {
    this.reactContext = reactContext;
  }

  public void open() {
    final String packageName = this.reactContext.getPackageName();
    final String myPath = DBPATH + packageName + DBNAME;
    this.db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
  }

  public String loadDataAsString() {
    final StringBuilder sb = new StringBuilder("{");
    final Cursor queryCursor = this.db.rawQuery("SELECT key,value FROM ItemTable", null);
    try {
      while (queryCursor.moveToNext()) {
        final String key = queryCursor.getString(0);
        Log.v(TAG, "key=" + key);
        final byte[] data = queryCursor.getBlob(1);
        try {
          final String value = new String(data, "UTF-16LE");
          Log.v(TAG, "value=" + value);
          sb.append("\"").append(key).append("\"").append(":").append(value);
          if(!queryCursor.isLast()) {
            sb.append(",");
          }
        } catch (Exception e) {
          Log.v(TAG, "error while reading local storage db");
        }
      }
    } finally {
      queryCursor.close();
    }
    final String jsonString = sb.append("}").toString();
    Log.v(TAG, "jsonString=" + jsonString);
    return jsonString;
  }

  public synchronized void close() {
    db.close();
  }
}

class ReactNativeWebkitLocalStorageReaderModule extends ReactContextBaseJavaModule {
  private final ReactApplicationContext context;

  public ReactNativeWebkitLocalStorageReaderModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.context = reactContext;
  }

  /**
    * @return the name of this module. This will be the name used to {@code require()} this module
    * from javascript.
    */
  @Override
  public String getName() {
    return "WebkitLocalStorageReader";
  }

  @ReactMethod
  public void get(Callback successCallback) {
    final Database database = new Database(this.context);
    try {
      database.open();
      final String jsonString = database.loadDataAsString();
      database.close();
      successCallback.invoke(jsonString);
    } catch (Exception e) {
      successCallback.invoke("");
    }
  }
}
