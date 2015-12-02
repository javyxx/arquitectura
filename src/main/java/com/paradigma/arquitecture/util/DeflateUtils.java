package com.paradigma.arquitecture.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DeflateUtils<T> {

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}

	public T compressObject(final T objectToCompress, final OutputStream outsStrema) throws IOException {
		final GZIPOutputStream gz = new GZIPOutputStream(outsStrema);
		final ObjectOutputStream oos = new ObjectOutputStream(gz);

		try {
			oos.writeObject(objectToCompress);
			oos.flush();
			return objectToCompress;
		} finally {
			oos.close();
			outsStrema.close();
		}
	}

	public T expandObject(final T objectToExpand, final InputStream inputStream)
			throws IOException, ClassNotFoundException {
		final GZIPInputStream gz = new GZIPInputStream(inputStream);
		final ObjectInputStream ois = new ObjectInputStream(gz);

		try {
			T expandObject = (T) ois.readObject();
			return expandObject;
		} finally {
			gz.close();
			ois.close();
		}
	}

	private DeflateUtils() {

	}
}