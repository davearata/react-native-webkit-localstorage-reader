package com.reactnative.webkitlocalstoragereader;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

private class Database {
  private static final String TAG = "ReactNativeWebkitLocalStorageReaderDatabase";
  private static final String DBPATH = "/data/user/0/";
  private static final String DBNAME = "/app_xwalkcore/Default/Local\ Storage/file__0.localstorage";

  private SQLiteDatabase db;
  private final ReactApplicationContext reactContext;

  public Database(ReactApplicationContext reactContext) {
    this.reactContext = reactContext;
  }

  public void open() {
    final String packageName = this.reactContext.getPackageName();
    final String myPath = DBPATH + packageName + DBNAME;
    this.db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory() + myPath, null, SQLiteDatabase.OPEN_READWRITE);
  }

  public String loadDataAsString() {
    final Cursor queryCursor = rawQuery("SELECT key,value FROM ItemTable", []);
    try {
      while (queryCursor.moveToNext()) {
        final String key = getString(0);
        Log.v(TAG, "key=" + key);
      }
    } finally {
      queryCursor.close();
    }
    return "";
  }

  public synchronized void close() {
    db.close();
  }
}

class ReactNativeWebkitLocalStorageReaderModule extends ReactContextBaseJavaModule {
  private final ReactApplicationContext context;

  public ReactNativeMupdfModule(ReactApplicationContext reactContext) {
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
    database.open();
    database.close();
    successCallback.invoke(jsonStirng)
  }
}
