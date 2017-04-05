package com.socket.client.util;

/**
 * Created by kings on 17/2/7.
 */
public class ProtocolCode {
    public static final String AUTHENTICATION_RESPONSE_CODE     = "00";
    public static final String C_AUTHENTICATION_CODE            = "01";
    public static final String S_AUTHENTICATION_CODE            = "02";

    public static final String FILE_MESSAGE_RESPONSE_CODE       = "10";
    public static final String C_FILE_MESSAGE_JSON_CODE         = "11";
    public static final String S_CAN_NOT_INSTALL_CODE           = "12";
    public static final String S_CAN_NOT_READ_PATH_CODE         = "14";
    public static final String S_CAN_READ_PATH_CODE             = "16";
    public static final String S_CAN_NOT_MAKE_FILE_CODE         = "18";

    public static final String TRANSFER_RESULT_RESPONSE_CODE    = "20";
    public static final String S_TRANSFER_RESULT_CODE           = "22";
    public static final String S_TRANSFER_FILE_NOT_VAILD__CODE  = "24";

    public static final String INSTALL_RESULT_RESPONSE_CODE     = "30";
    public static final String S_INSTALL_RESULT_CODE            = "32";

    public static final String SEPARATOR                        = "##";

    public static final String MESSAGE_AUTHENTICATION           = "hello_nuts";

    public static final String MESSAGE_SUCCESS                  = "success";
    public static final String MESSAGE_FAILURE                  = "failure";

    public static final String AUTHENTICATION_SUCCESS_MESSAGE   = S_AUTHENTICATION_CODE + SEPARATOR + MESSAGE_SUCCESS;
    public static final String AUTHENTICATION_FAILURE_MESSAGE   = S_AUTHENTICATION_CODE + SEPARATOR + MESSAGE_FAILURE;

    public static final String TRANSFER_SUCCESS_MESSAGE         = S_TRANSFER_RESULT_CODE + SEPARATOR + MESSAGE_SUCCESS;
    public static final String TRANSFER_FAILURE_MESSAGE         = S_TRANSFER_RESULT_CODE + SEPARATOR + MESSAGE_FAILURE;

    public static final String INSTALL_SUCCESS_MESSAGE          = S_INSTALL_RESULT_CODE + SEPARATOR + MESSAGE_SUCCESS;
    public static final String INSTALL_FAILURE_MESSAGE          = S_INSTALL_RESULT_CODE + SEPARATOR + MESSAGE_FAILURE;

    public static final String AES_PASSWORD                     = "71AF062C2EB5821E9D104AA48EB985B5";





}
