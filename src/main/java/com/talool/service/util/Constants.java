package com.talool.service.util;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;

/**
 * 
 * @author clintz
 * 
 */
public class Constants {
  public static final Factory PROTOCOL_FACTORY = new TBinaryProtocol.Factory();

  /**
   * Android GCM token
   */
  public static final String HEADER_GCM_DEVICE_TOKEN = "GcmDeviceToken";

  /**
   * Generic deviceId for mobile
   */
  public static final String HEADER_DEVICE_ID = "deviceid";

  /**
   * Apple push notification device token
   */
  public static final String HEADER_APN_DEVICE_TOKEN = "ApnDeviceToken";

  public static final String HEADER_USER_AGENT = "user-agent";

  public static final String HEADER_X_CLIENT = "x-client";

  /**
   * Mobile clients that send this header support returning free books
   */
  public static final String HEADER_X_SUPPORTS_FREE_BOOKS = "x-supports-free-books";

}
