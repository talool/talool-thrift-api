package com.talool.service.util;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;

/**
 * 
 * @author clintz
 * 
 */
public final class Constants
{
	public static final Factory PROTOCOL_FACTORY = new TBinaryProtocol.Factory();
}
