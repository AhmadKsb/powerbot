package org.powerbot.game.bot.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;

import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.SecureStore;

/**
 * @author Timer
 */
public class UIDManager {
	private static final String FILE_NAME = "gameuids.txt";
	private static String newline = System.getProperty("line.separator");
	private static String separator = "#";

	private HashMap<String, byte[]> uids = new HashMap<String, byte[]>();
	private String lastUsed = "";

	public UIDManager() {
		try {
			final InputStream inputStream = SecureStore.getInstance().read(FILE_NAME);
			if (inputStream != null) {
				for (final String line : IOHelper.readString(inputStream).split("\n")) {
					if (!line.isEmpty()) {
						final String[] data = line.split(separator, 2);
						if (data.length == 2) {
							uids.put(data[0], data[1].getBytes());
						}
					}
				}
			}
		} catch (final Throwable ignored) {
		}
	}

	public String getLastUsed() {
		return lastUsed;
	}

	public byte[] getUID(String name) {
		if (name.equals("")) {
			name = "DEFAULT";
		}
		lastUsed = name;

		final byte[] data = uids.get(name);
		if (data == null) {
			return new byte[0];
		}
		return data;
	}

	public void setUID(String name, final byte[] uid) {
		if (name.equals("")) {
			name = "DEFAULT";
		}

		uids.put(name, uid);
	}

	public void save() throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (final String key : uids.keySet()) {
			out.write(key.getBytes());
			out.write(separator.getBytes());
			out.write(uids.get(key));
			out.write(newline.getBytes());
		}
		try {
			SecureStore.getInstance().write(FILE_NAME, out.toByteArray());
		} catch (GeneralSecurityException ignored) {
			throw new IOException("failed to write data");
		}
	}
}
