package com.filesocketclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by kings on 17/2/7.
 */
public class MainActivity extends Activity {

   private EditText et_apk;
   private Button   btn_choose_apk;
   private Button   btn_send_apk;

   private Context   mContext;

   private String mFilePath;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      mContext = this;

      setContentView(R.layout.activity_main);

      et_apk = (EditText) findViewById(R.id.et_apk);
//      et_apk.setCompoundDrawables();
      et_apk.getPaddingRight();
      et_apk.getTotalPaddingRight(); // 获取删除图标左边缘到控件右边缘的距离
      et_apk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Log.i("zdy", "et_apk.setOnClickListener();");
         }
      });

      btn_choose_apk = (Button) findViewById(R.id.btn_choose_apk);
      btn_send_apk = (Button) findViewById(R.id.btn_send_apk);

      btn_choose_apk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            openApk();
         }
      });

      btn_send_apk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (!TextUtils.isEmpty(mFilePath)) {

               Log.d("lee", "mFilePath : " + mFilePath);

               ClientManager stClient = new ClientManager(mContext, mFilePath);

               stClient.setFilePath(mFilePath);
               stClient.actionUploadFile();

            } /* End if () */
            else {
               Log.d("lee", "please choose apk: ");

            } /* End else */
         }
      });
   }

   private void openApk() {
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("application/vnd.android.package-archive");
      try {
         startActivityForResult(Intent.createChooser(intent, "请选择一个要安装的文件"), 111);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static String getPath(Context context, Uri uri) {

      if ("content".equalsIgnoreCase(uri.getScheme())) {
         String[] projection = { "_data" };
         Cursor   cursor     = null;

         try {
            cursor = context.getContentResolver().query(uri, projection,null, null, null);
            int column_index = cursor.getColumnIndexOrThrow("_data");
            if (cursor.moveToFirst()) {
               return cursor.getString(column_index);
            }
         } catch (Exception e) {
            // Eat it
         }
      }

      else if ("file".equalsIgnoreCase(uri.getScheme())) {
         return uri.getPath();
      }

      return null;
   }
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (resultCode == Activity.RESULT_OK) {
         Uri    uri        = data.getData();
         String path       = uri.getPath();
         System.out.println("path : " + path);

         String szPath     =  getPath(this, uri);
         System.out.println("szPath : " + szPath);

//         szPath   = Uri.encode(szPath);
//         System.out.println("szPath : " + szPath);

//         String encodePath = uri.getEncodedPath();
//         System.out.println("path : " + path);
//         System.out.println("encodePath : " + encodePath);

         if (!TextUtils.isEmpty(szPath)) {
            mFilePath = szPath;
            et_apk.setText(mFilePath);
         }
      }
      super.onActivityResult(requestCode, resultCode, data);
   }


}
