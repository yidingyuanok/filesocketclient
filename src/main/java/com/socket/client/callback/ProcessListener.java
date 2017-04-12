package com.socket.client.callback;

public interface ProcessListener {
	/**
	 * 本地文件不存在
	 * @param filePath 文件路径
     */
	void onLocalFileDoNotExist(String filePath);

	/**
	 * 服务端没响应（身份验证、文件信息、传输结果）
	 * @param protocolCode 协议码
     */
	void onServerNotResponse(String protocolCode);

	/**
	 * 服务端响应错误（身份验证、文件信息、传输结果）
	 * @param protocolCode 协议码
     */
	void onServerErrorResponse(String protocolCode);

	/**
	 * 身份验证响应结果
	 * @param result 结果
     */
	void onAuthResult(boolean result);

	/**
	 * 服务端没有安装功能
	 */
	void onServerCanNotInstall(String path);

	/**
	 * 服务端不能读apk文件
	 * @param path
     */
	void onServerCanNotReadSdcardFile(String path);

	/**
	 * 服务器可以读apk文件
	 * @param path
     */
	void onServerCanReadSdcardFile(String path);


	void onGetTopApp(String str);

	/**
	 * 服务端不能创建文件
	 */
	void onServerCanNotCreateFolder();

	/**
	 * 文件传输进度
	 * @param total 文件大小
	 * @param length 传输进度
     */
	void onTransferProcess(long total, long length);

	/**
	 * 文件传输结果
	 * @param result 结果
	 */
	void onTransferResult(boolean result);

	/**
	 * 服务端文件接受成功，但是文件不可用
	 */
	void onTransferFileNotVaild();

	/**
	 * apk安装，第一步先不用
	 * @param result 结果
	 */
	void onInstallResult(boolean result);

	/**
	 * 发生异常
	 * @param e 异常信息
     */
	void onException(Exception e);

}
