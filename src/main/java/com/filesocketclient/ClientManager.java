package com.filesocketclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.socket.client.FileClient;
import com.socket.client.callback.ProcessListener;
import com.socket.client.util.ProtocolCode;
import com.socket.client.util.SimpleLog;
import com.socket.client.util.SingleThreadUtils;

import java.io.File;
import java.net.ConnectException;

/**
 * Created by kings on 17/2/12.
 */
public class ClientManager {

   private static final boolean DEBUG = SimpleLog.CLIENT_DEBUG;

   private FileClient mFileClient;
   private Context    mContext;

   public ClientManager() {

   }

   public ClientManager(String filePath) {
      this.mFilePath = filePath;
   }

   public ClientManager(Context context, String filePath) {
      this.mContext = context;
      this.mFilePath = filePath;

      return;
   }

   public void setFilePath(String filePath) {
      this.mFilePath = filePath;
   }

//   private String mHost     = "172.0.0.1"; // InetAddress.getLocalHost();
   private String mHost     = "127.0.0.1"; // InetAddress.getLocalHost();
   private int[]  mPorts    = {10240, 8807, 8806};
   private int    mPort     = mPorts[0]; // 10240
   private String mFilePath = "";
   private int select;

   private void resetPortAndStart() {

      if (DEBUG) {
         SimpleLog.d("client --- resetPortAndStart --- select : " + select);
      }

      select++;

      int length = mPorts.length;

      if (select >= length) {

         if (DEBUG) {
            SimpleLog.d("client --- try all port but all in used !!! ");
         }

         quit();

         return;
      }
      mPort = mPorts[select];
      actionUploadFile();
   }

   public void actionUploadFile() {

      SingleThreadUtils.execute(new Runnable() {

         @Override
         public void run() {
            quit();
            if (DEBUG) {
               SimpleLog.d("client --- try connect server use port : " + mPort);
            }
            mFileClient = new FileClient(mHost, mPort);


            mFileClient.upload(mFilePath, new ProcessListener() {
               @Override
               public void onLocalFileDoNotExist(String filePath) {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onLocalFileDoNotExist --- filePath : " + filePath);
                  }
                  quit();
               }

               @Override
               public void onServerNotResponse(String protocolCode) {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onServerNotResponse --- protocolCode : " + protocolCode);
                  }
                  if (ProtocolCode.AUTHENTICATION_RESPONSE_CODE.equals(protocolCode)) {
                     resetPortAndStart();
                  }
                  else if (ProtocolCode.FILE_MESSAGE_RESPONSE_CODE.equals(protocolCode)) {
                     quit();
                  }
                  else if (ProtocolCode.TRANSFER_RESULT_RESPONSE_CODE.equals(protocolCode)) {
                     quit();
                  }
                  else if (ProtocolCode.INSTALL_RESULT_RESPONSE_CODE.equals(protocolCode)) {
                     quit();
                  }
                  else {
                     quit();
                  }
               }

               @Override
               public void onServerErrorResponse(String protocolCode) {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onServerErrorResponse --- protocolCode : " + protocolCode);
                  }
                  if (ProtocolCode.AUTHENTICATION_RESPONSE_CODE.equals(protocolCode)) {
                     resetPortAndStart();
                  }
                  else if (ProtocolCode.FILE_MESSAGE_RESPONSE_CODE.equals(protocolCode)) {
                     quit();
                  }
                  else if (ProtocolCode.TRANSFER_RESULT_RESPONSE_CODE.equals(protocolCode)) {
                     quit();
                  }
                  else if (ProtocolCode.INSTALL_RESULT_RESPONSE_CODE.equals(protocolCode)) {
                     quit();
                  }
                  else {
                     quit();
                  }
               }

               @Override
               public void onAuthResult(boolean result) {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onAuthResult --- result : " + result);
                  }
                  if (result) {

                  }
                  else {
                     resetPortAndStart();
                  }
               }

               @Override
               public void onServerCanNotInstall(String path) {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onServerCanNotInstall --- path : " + path);
                  }
                  quit();
                  //自己装吧
                  installAppByCallSystemIntent(mFilePath);
               }

               @Override
               public void onServerCanNotReadSdcardFile(String path) {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onServerCanNotReadSdcardFile --- path : " + path);
                  }
               }

               @Override
               public void onServerCanReadSdcardFile(String path) {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onServerCanReadSdcardFile --- path : " + path);
                  }
                  quit();
               }

               @Override
               public void onServerCanNotCreateFolder() {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onServerCanNotCreateFolder ：");
                  }
                  quit();
                  //自己装吧
                  installAppByCallSystemIntent(mFilePath);
               }

               @Override
               public void onTransferProcess(long total, long length) {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onTransferProcess --- total : " + total + " --- :" + length);
                  }
               }

               @Override
               public void onTransferResult(boolean result) {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onTransferResult --- result : " + result);
                  }
                  if (result) {

                  }
                  else {
                     //自己装吧
                     installAppByCallSystemIntent(mFilePath);
                  }
                  quit();
               }

               @Override
               public void onTransferFileNotVaild() {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onTransferFileNotVaild ：");
                  }
                  //自己装吧
                  quit();
                  installAppByCallSystemIntent(mFilePath);
               }

               @Override
               public void onInstallResult(boolean result) {
                  if (DEBUG) {
                     SimpleLog.d("client --- upload --- onInstallResult --- result : " + result);
                  }
               }

               @Override
               public void onException(Exception e) {
                  if (e instanceof ConnectException) {
                     if (DEBUG) {
                        SimpleLog.d("client --- upload --- onException --- ConnectException --- resetPortAndStart : ");
                     }
                     resetPortAndStart();
                  }
                  else {
                     quit();
                  }
                  e.printStackTrace();
               }
            });
         }
      });
   }

   private void quit() {
      if (mFileClient != null) {
         mFileClient.quit();
         mFileClient = null;
      }
   }

   private void installAppByCallSystemIntent(String filePath) {
//        MyLog.e(TAG,"installAppByCallSystemIntent filePath : " + filePath );
      File   apk    = new File(filePath);
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      if (intent.resolveActivity(mContext.getPackageManager()) != null) {
         mContext.startActivity(intent);
      }
      else {
         Log.d("lee", "no intent action view !!!");
      }
   }
}
