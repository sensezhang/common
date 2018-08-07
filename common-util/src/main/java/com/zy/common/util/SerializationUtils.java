package com.zy.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationUtils {

	public static byte[] serialize(Object state) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
				ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(state);
			oos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T deserialize(byte[] byteArray) {
		try (ObjectInputStream oip = new ObjectInputStream(
				new ByteArrayInputStream(byteArray))) {
			@SuppressWarnings("unchecked")
			T result = (T) oip.readObject();
			return result;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	private SerializationUtils(){}
}
