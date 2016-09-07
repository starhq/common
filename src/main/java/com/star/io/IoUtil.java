package com.star.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Objects;

import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * io工具类
 * 
 * @author starhq
 *
 */
public final class IoUtil {

	/**
	 * 缓冲区大小
	 */
	public final static int BUFFER_SIZE = 1024;

	private IoUtil() {
	}

	// =======================copy start=============================
	/**
	 * 将Reader中的内容复制到Writer中
	 */
	public static int copy(final Reader reader, final Writer writer, final int bufferSize) {
		Assert.notNull(reader, "reader copy to writer failure,the reader is null");
		Assert.notNull(writer, "reader copy to writer failure,the writer is null");
		final char[] buffer = new char[bufferSize <= 0 ? BUFFER_SIZE : bufferSize];
		int count = 0;
		int readSize;
		try {
			while ((readSize = reader.read(buffer, 0, bufferSize <= 0 ? BUFFER_SIZE : bufferSize)) >= 0) {
				writer.write(buffer, 0, readSize);
				count += readSize;
				writer.flush();
			}
			return count;
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("reader copy to writer failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 将InputStream中的内容复制到outputStream中
	 * 
	 * bufferSize=0 会用默认的1024
	 * 
	 */
	public static int copy(final InputStream inputStream, final OutputStream outputStream, final int bufferSize) {
		Assert.notNull(inputStream, "inputStream copy to outputStream failure,the reader is null");
		Assert.notNull(outputStream, "inputStream copy to outputStream failure,the writer is null");
		final byte[] buffer = new byte[bufferSize <= 0 ? BUFFER_SIZE : bufferSize];
		int count = 0;
		try {
			for (int n = -1; (n = inputStream.read(buffer)) != -1;) {
				outputStream.write(buffer, 0, n);
				count += n;
				outputStream.flush();
			}
			return count;
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("将inputStream copy to outputStream failure the reason is: {}", e.getMessage()),
					e);
		}
	}
	// =======================copy end=============================

	// =====================rw start==================================
	/**
	 * 获得一个读取器
	 */
	public static BufferedReader getReader(final InputStream inputStream, final Charset charset) {
		Assert.notNull(inputStream, "wrap inputstream to reader failure,the inputstream is null");

		final InputStreamReader reader = charset == null ? new InputStreamReader(inputStream)
				: new InputStreamReader(inputStream, charset);

		return new BufferedReader(reader);
	}

	/**
	 * 获得一个写出器
	 */
	public static BufferedWriter getWriter(final OutputStream outputStream, final Charset charset) {
		Assert.notNull(outputStream, "wrap outputStream to writer failure,the outputStream is null");
		final OutputStreamWriter writer = charset == null ? new OutputStreamWriter(outputStream)
				: new OutputStreamWriter(outputStream, charset);

		return new BufferedWriter(writer);
	}
	// =====================rw end==================================

	/**
	 * 
	 * 从流中读取内容
	 * 
	 * byte[] output.toByteArray();
	 * 
	 * String output.toString();
	 * 
	 */
	public static FastByteArrayOutputStream read(final InputStream input) {
		Assert.notNull(input, "copy input stream content to output stream failure,the inpustream is null");
		final FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		try {
			copy(input, output, input.available());
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("conver inputstream to outputstream failure,the reason is: {}", e.getMessage()),
					e);
		}
		return output;
	}

	/**
	 * 从reader中读取内容
	 */
	public static String read(final Reader reader) {
		Assert.notNull(reader, "get string from reader failure,the reader is null");
		final StringBuilder stringBuilder = new StringBuilder();
		final CharBuffer buffer = CharBuffer.allocate(BUFFER_SIZE);
		try {
			while (-1 != reader.read(buffer)) {
				stringBuilder.append(buffer.flip().toString());
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("get string from reader failure,the reasone is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 从流中读取内容分装成collection
	 */
	public static <T extends Collection<String>> T readLines(final InputStream inputStream, final String charset,
			final T collection) {
		Assert.notNull(inputStream, "get string collection from inputstream failure,the inputstream is null");
		final BufferedReader bufferedReader = getReader(inputStream, Charset.forName(charset));
		String line = null;
		try {
			while (null != (line = bufferedReader.readLine())) {
				collection.add(line);
			}
			return collection;
		} catch (IOException e) {
			throw new ToolException(StringUtil
					.format("get string collection from inputstream failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * string转化为流
	 */
	public static ByteArrayInputStream toStream(final String content, final String charset) {
		Assert.notNull(content, "string convert to inputstream failure,the input sting is null");
		final byte[] data = StringUtil.string2Byte(content, CharsetUtil.charset(charset));
		return new ByteArrayInputStream(data);
	}

	/**
	 * 
	 * 将多部分内容写到流中，自动转换为字符串
	 * 
	 */
	public static void write(final OutputStream outputStream, final String charset, final Object... contents) {
		final BufferedWriter bufferedWriter = getWriter(outputStream, CharsetUtil.charset(charset));
		for (final Object content : contents) {
			if (!Objects.isNull(content)) {
				final String cont = content.toString();
				try {
					bufferedWriter.write(StringUtil.isBlank(cont) ? "" : cont);
					bufferedWriter.flush();
				} catch (IOException e) {
					throw new ToolException(StringUtil
							.format("object array convert to stream failure,the reason is: {}", e.getMessage()), e);
				}
			}
		}
		close(bufferedWriter);
	}

	/**
	 * 
	 * 关闭
	 * 
	 */
	public static void close(final Closeable closeable) {
		if (!Objects.isNull(closeable)) {
			try {
				closeable.close();
			} catch (IOException e) {
				throw new ToolException(StringUtil.format("close stream failue,the reason is: {}", e.getMessage()), e);
			}
		}
	}

	/**
	 * 
	 * 关闭
	 * 
	 * @param closeable
	 *            被关闭的对象
	 * 
	 * @since 1.7
	 * 
	 */
	public static void close(final AutoCloseable closeable) {
		if (!Objects.isNull(closeable)) {
			try {
				closeable.close();
			} catch (Exception e) {
				throw new ToolException(StringUtil.format("close stream failue,the reason is: {}", e.getMessage()), e);
			}
		}
	}
}
