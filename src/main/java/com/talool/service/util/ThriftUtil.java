package com.talool.service.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.TBase;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TProtocolFactory;

import com.talool.api.thrift.CustomerServiceConstants;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.Token_t;
import com.talool.service.RequestUtils;

/**
 * @author clintz
 * 
 */
public final class ThriftUtil
{

	public static byte[] serialize(final TBase obj, final TProtocolFactory protocolFactory) throws TException
	{
		final TSerializer serializer = new TSerializer(protocolFactory);
		final byte[] bytes = serializer.serialize(obj);
		return bytes;
	}

	public static void writeToDisk(byte[] bytes, final TProtocolFactory protocolFactory, final String fileName)
			throws TException, IOException
	{
		OutputStream out = null;

		try
		{
			out = new BufferedOutputStream(new FileOutputStream(fileName));
			out.write(bytes);
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}

	}

	public static byte[] readFromFile(final File file, final TBase obj, final TProtocolFactory protocolFactory)
			throws IOException, TException
	{
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE)
		{
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
		{
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length)
		{
			throw new IOException("Could not completely read file " + file.getName());
		}

		// Close the input stream and return bytes
		is.close();

		final TDeserializer deSerializer = new TDeserializer(protocolFactory);
		deSerializer.deserialize(obj, bytes);

		return bytes;
	}

	public static byte[] serializeToDisk(final TBase obj, final TProtocolFactory protocolFactory, final String fileName)
			throws TException, IOException
	{
		final TSerializer serializer = new TSerializer(protocolFactory);
		final byte[] bytes = serializer.serialize(obj);

		OutputStream out = null;

		try
		{
			out = new BufferedOutputStream(new FileOutputStream(fileName));
			out.write(bytes);
			return bytes;
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}

	}

	public static void deserialize(final byte[] bytes, final TBase obj, final TProtocolFactory protocolFactory)
			throws TException
	{
		final TDeserializer deserializer = new TDeserializer(protocolFactory);
		deserializer.deserialize(obj, bytes);
	}

	public static byte[] byteBufferToByteArray(final ByteBuffer byteBuffer)
	{
		return TBaseHelper.byteBufferToByteArray(byteBuffer);
	}

	public static int byteBufferToByteArray(final ByteBuffer byteBuffer, final byte[] target, final int offset)
	{
		return TBaseHelper.byteBufferToByteArray(byteBuffer, target, offset);
	}

}
