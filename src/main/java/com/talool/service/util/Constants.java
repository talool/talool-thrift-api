package com.talool.service.util;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;

/**
 * 
 * @author clintz
 * 
 */
public final class Constants {
  public static final Factory PROTOCOL_FACTORY = new TBinaryProtocol.Factory();

  /**
   * Android GCM token
   */
  public static final String HEADER_GCM_DEVICE_TOKEN = "GcmDeviceToken";

  /**
   * Generic deviceId for mobile
   */
  public static final String HEADER_DEVICE_ID = "DeviceId";

  /**
   * Apple push notification device token
   */
  public static final String HEADER_APN_DEVICE_TOKEN = "ApnDeviceToken";

  public static final String HEADER_USER_AGENT = "User-Agent";

  public static final String HEADER_X_CLIENT = "x-client";

}
