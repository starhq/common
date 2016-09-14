package com.star.uuid;

import java.util.Arrays;
import java.util.UUID;

import com.star.string.HexUtil;

/**
 * 
 * 用来替代jdk的uuid
 * 
 * @author rolfl
 * 
 */
public final class NessUUID {

	/**
	 * 不知道派啥用处,是不是uuid中的连接符
	 */
	private static final int DASH = -1;
	/**
	 * 不知道派什么用处,就看到用来填充lookup
	 */
	private static final int ERROR = -2;

	/**
	 * lookup is an array indexed by the **char**, and it has valid values set
	 * with the decimal value of the hex char.
	 */
	private static final long[] LOOKUP = buildLookup();

	/**
	 * recode is 2-byte arrays representing the hex representation of every byte
	 * value (all 256)
	 */
	private static final char[][] RECODE = buildByteBlocks();

	private NessUUID() {
	}

	private static long[] buildLookup() {
		long[] lookup = new long[128];
		Arrays.fill(lookup, ERROR);
		lookup['0'] = 0;
		lookup['1'] = 1;
		lookup['2'] = 2;
		lookup['3'] = 3;
		lookup['4'] = 4;
		lookup['5'] = 5;
		lookup['6'] = 6;
		lookup['7'] = 7;
		lookup['8'] = 8;
		lookup['9'] = 9;
		lookup['a'] = 10;
		lookup['b'] = 11;
		lookup['c'] = 12;
		lookup['d'] = 13;
		lookup['e'] = 14;
		lookup['f'] = 15;
		lookup['A'] = 10;
		lookup['B'] = 11;
		lookup['C'] = 12;
		lookup['D'] = 13;
		lookup['E'] = 14;
		lookup['F'] = 15;
		lookup['-'] = DASH;
		return lookup;
	}

	private static char[][] buildByteBlocks() {
		final char[][] ret = new char[256][];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = HexUtil.byte2Hex((byte) i).toCharArray();
		}
		return ret;
	}

	/**
	 * 取代jdk中uuid的对应方法
	 * 
	 * @param str
	 * @return
	 */
	public static UUID fromString(final String str) {
		final int len = str.length();
		if (len != 36) {
			throw new IllegalArgumentException("Invalid UUID string (expected to be 36 characters long)");
		}
		final long[] vals = new long[2];
		int shift = 60;
		int index = 0;
		for (int i = 0; i < len; i++) {
			final int c = str.charAt(i);
			if (c >= LOOKUP.length || LOOKUP[c] == ERROR) {
				throw new IllegalArgumentException("Invalid UUID string (unexpected '" + str.charAt(i)
						+ "' at position " + i + " -> " + str + " )");
			}

			if (LOOKUP[c] == DASH) {
				if ((i - 8) % 5 != 0) {
					throw new IllegalArgumentException(
							"Invalid UUID string (unexpected '-' at position " + i + " -> " + str + " )");
				}
				continue;
			}
			vals[index] |= LOOKUP[c] << shift;
			shift -= 4;
			if (shift < 0) {
				shift = 60;
				index++;
			}
		}
		return new UUID(vals[0], vals[1]);
	}

	/**
	 * 取代jdk中uuid的对应方法
	 * 
	 * @param uuid
	 * @return
	 */
	public static String toString(final UUID uuid) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		char[] uuidChars = new char[36];
		int cursor = uuidChars.length;
		while (cursor > 24) {
			cursor -= 2;
			System.arraycopy(RECODE[(int) (lsb & 0xff)], 0, uuidChars, cursor, 2);
			lsb >>>= 8;
		}
		uuidChars[--cursor] = '-';
		while (cursor > 19) {
			cursor -= 2;
			System.arraycopy(RECODE[(int) (lsb & 0xff)], 0, uuidChars, cursor, 2);
			lsb >>>= 8;
		}
		uuidChars[--cursor] = '-';
		while (cursor > 14) {
			cursor -= 2;
			System.arraycopy(RECODE[(int) (msb & 0xff)], 0, uuidChars, cursor, 2);
			msb >>>= 8;
		}
		uuidChars[--cursor] = '-';
		while (cursor > 9) {
			cursor -= 2;
			System.arraycopy(RECODE[(int) (msb & 0xff)], 0, uuidChars, cursor, 2);
			msb >>>= 8;
		}
		uuidChars[--cursor] = '-';
		while (cursor > 0) {
			cursor -= 2;
			System.arraycopy(RECODE[(int) (msb & 0xff)], 0, uuidChars, cursor, 2);
			msb >>>= 8;
		}
		return new String(uuidChars);
	}

}
