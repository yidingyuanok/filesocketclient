package com.socket.client;

import android.text.TextUtils;

import com.socket.client.bean.SocketHeader;
import com.socket.client.callback.ProcessListener;
import com.socket.client.util.AES;
import com.socket.client.util.ProtocolCode;
import com.socket.client.util.SimpleLog;
import com.socket.client.util.StreamUtils;

import java.io.File;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;

public class FileClient {
   private static final boolean DEBUG = SimpleLog.CLIENT_DEBUG;

   public static final int FILE_WRITE_BUFFER = 1024;

   private String mHost;
   private int    mPort;
   private Socket mSocket;

   private OutputStream        mOutStream;
   private PushbackInputStream mInStream;

   public FileClient(String _Host, int _Port) {
      mHost = _Host;
      mPort = _Port;
   }

   public void upload(String _Path, ProcessListener _Listener) {
      if (DEBUG) {
         SimpleLog.d("client --- upload --- thread : " + Thread.currentThread().getName());
      }
      try {
         File f = new File(_Path);

         if (!f.exists()) {
            //本地文件不存在
            if (null != _Listener) {

               _Listener.onLocalFileDoNotExist(_Path);

            } /* End if () */

            return;
         }



         try {

            if (null == mSocket) {
               SimpleLog.d("InetAddress.getLocalHost() : " + InetAddress.getLocalHost());  // localhost/127.0.0.1
               mHost = InetAddress.getLocalHost().getHostAddress();
//               mHost = "localhost/127.0.0.1";
               SimpleLog.d("client --- host : " + mHost);
               SimpleLog.d("client --- port : " + mPort);
               mSocket = new Socket(InetAddress.getLocalHost(), mPort);
               mSocket.setSoTimeout(10 * 1000);

            } /* End if () */

            SimpleLog.d("client --- socket : " + mSocket);
            SimpleLog.d("client --- socket : " + mSocket);

         }
         catch (Exception _E) {

            SimpleLog.d("client --- socket : Exception : " + _E);
            SimpleLog.d("client --- socket : Exception : " + _E);

         }

         //发送
         mOutStream = mSocket.getOutputStream();
         //接收
         mInStream = new PushbackInputStream(mSocket.getInputStream());
         //身份验证
         String encrypt = AES.encrypt(ProtocolCode.MESSAGE_AUTHENTICATION, ProtocolCode.AES_PASSWORD);
         String authMsg = ProtocolCode.C_AUTHENTICATION_CODE + ProtocolCode.SEPARATOR + encrypt + "\r\n";
         mOutStream.write(authMsg.getBytes());
         if (DEBUG) {
            SimpleLog.d("client --- send auth msg : " + authMsg);
         }

         String response = StreamUtils.readLine(mInStream);

         if (DEBUG) {
            SimpleLog.d("client --- receive auth response : " + response);
         }
         if (responseIsNull(response)) {
            //服务端没响应身份验证
            if (_Listener != null) {
               _Listener.onServerNotResponse(ProtocolCode.AUTHENTICATION_RESPONSE_CODE);
            }
            return;
         }
         if (responseCodeIsCorrect(response, ProtocolCode.S_AUTHENTICATION_CODE)) {
            if (resultSuccess(response)) {
               //身份验证成功
               if (_Listener != null) {
                  _Listener.onAuthResult(true);
               }
               SocketHeader header = new SocketHeader();
               String       name   = f.getName();
               int          i      = name.hashCode();
               header.setId(i + "");
               final long fileLength = f.length();
               header.setLength(fileLength);
               header.setName(f.getName());
               header.setPosition(0);
               header.setFilePath(_Path);
               //文件信息
               String fileMsg = ProtocolCode.C_FILE_MESSAGE_JSON_CODE + ProtocolCode.SEPARATOR + header.toResponse();
               mOutStream.write(fileMsg.getBytes());
               if (DEBUG) {
                  SimpleLog.d("client --- send file msg : " + fileMsg);
               }
               response = StreamUtils.readLine(mInStream);
               if (DEBUG) {
                  SimpleLog.d("client --- receive file msg response : " + response);
               }
               if (responseIsNull(response)) {
                  //服务端没响应文件信息
                  if (_Listener != null) {
                     _Listener.onServerNotResponse(ProtocolCode.FILE_MESSAGE_RESPONSE_CODE);
                  }
                  return;
               }
               if (responseCodeIsCorrect(response, ProtocolCode.S_CAN_NOT_INSTALL_CODE)) {
                  //服务端没有安装功能
                  if (_Listener != null) {
                     _Listener.onServerCanNotInstall(_Path);
                  }
               }
               else if (responseCodeIsCorrect(response, ProtocolCode.S_CAN_READ_PATH_CODE)) {
                  if (_Listener != null) {
                     _Listener.onServerCanReadSdcardFile(_Path);
                  }
               }
               else if (responseCodeIsCorrect(response, ProtocolCode.S_CAN_NOT_MAKE_FILE_CODE)) {
                  if (_Listener != null) {
                     _Listener.onServerCanNotCreateFolder();
                  }
               }
               else if (responseCodeIsCorrect(response, ProtocolCode.S_CAN_NOT_READ_PATH_CODE)) {
                  //服务端有安装功能但不能读sd卡, 就现场传输
                  if (_Listener != null) {
                     _Listener.onServerCanNotReadSdcardFile(_Path);
                  }

                  String one = splitResponseOne(response);
                  header = SocketHeader.toHeader(one);
                  RandomAccessFile fileOutStream = new RandomAccessFile(f, "r");
                  fileOutStream.seek(header.getPosition());
                  int    len    = 0;
                  byte[] buffer = new byte[FILE_WRITE_BUFFER];
                  long   length = header.getPosition();
                  while ((len = fileOutStream.read(buffer)) != -1) {
                     mOutStream.write(buffer, 0, len);
                     length += len;

                     if (_Listener != null) {
                        _Listener.onTransferProcess(fileLength, length);
                     }
                  }
                  response = StreamUtils.readLine(mInStream);
                  if (DEBUG) {
                     SimpleLog.d("client --- receive file transfer finish response : " + response);
                  }
                  if (responseIsNull(response)) {
                     //服务端没返回传输结果
                     if (_Listener != null) {
                        _Listener.onServerNotResponse(ProtocolCode.TRANSFER_RESULT_RESPONSE_CODE);
                     }
                     return;
                  }
                  if (responseCodeIsCorrect(response, ProtocolCode.S_TRANSFER_RESULT_CODE)) {
                     if (resultSuccess(response)) {
                        //文件传输成功
                        if (_Listener != null) {
                           _Listener.onTransferResult(true);
                        }
                     }
                     else {
                        //文件传输失败
                        if (_Listener != null) {
                           _Listener.onTransferResult(false);
                        }
                     }
                  }
                  else if (responseCodeIsCorrect(response, ProtocolCode.S_TRANSFER_FILE_NOT_VAILD__CODE)) {  //  not valid
                     if (_Listener != null) {
                        _Listener.onTransferFileNotVaild();
                     }
                  }
                  else {
                     //传输结果返回码不对
                     if (_Listener != null) {
                        _Listener.onServerErrorResponse(ProtocolCode.TRANSFER_RESULT_RESPONSE_CODE);
                     }
                  }
                  fileOutStream.close();
               }
               else {
                  //文件信息返回码不对
                  if (_Listener != null) {
                     _Listener.onServerErrorResponse(ProtocolCode.FILE_MESSAGE_RESPONSE_CODE);
                  }
               }

            }
            else {
               //身份验证失败
               if (_Listener != null) {
                  _Listener.onAuthResult(false);
               }
            }
         }
         else {
            //身份验证返回码不对
            if (_Listener != null) {
               _Listener.onServerErrorResponse(ProtocolCode.AUTHENTICATION_RESPONSE_CODE);
            }
         }
      }
      catch (Exception e) {
         if (_Listener != null) {
            _Listener.onException(e);
         }
         e.printStackTrace();
      }
   }

   public void quit() {
      if (mSocket != null) {
         try {
            if (mSocket.getOutputStream() != null) {
               mSocket.getOutputStream().close();
            }
            if (mSocket.getInputStream() != null) {
               mSocket.getInputStream().close();
            }
            mSocket.close();
         }
         catch (Exception e) {
            // e.printStackTrace();
            mSocket = null;
         }
      }
   }

   private boolean resultSuccess(String response) {
      String success = splitResponseOne(response);
      return TextUtils.equals(ProtocolCode.MESSAGE_SUCCESS, success);
   }

   private boolean responseIsNull(String response) {
      return TextUtils.isEmpty(response);
   }

   private boolean responseCodeIsCorrect(String response, String code) {
      return response.startsWith(code);
   }

   private String splitResponseOne(String response) {
      String[] authSplit = response.split(ProtocolCode.SEPARATOR);
      if (authSplit.length != 2) {
         return null;
      }
      return authSplit[1];
   }
}
