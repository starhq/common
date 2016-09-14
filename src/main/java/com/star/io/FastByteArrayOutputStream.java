package com.star.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Objects;

import com.star.exception.pojo.ToolException;
import com.star.string.StringUtil;

/**
 * 基于快速缓冲区的outputstream
 * 
 * 替代bytearrayoutputstream,具体场景还没直观的概念
 * 
 * @author starhq
 *
 */
public class FastByteArrayOutputStream extends OutputStream {

	/** 快于缓冲区 */
	private final transient FastByteBuffer buffer;

	/**
	 * 构造方法,快速缓冲区大小设置为1k
	 */
	public FastByteArrayOutputStream() {
		this(1024);
	}

	/**
	 * 构造方法，设置缓冲区大小
	 */
	public FastByteArrayOutputStream(final int size) {
		super();
		buffer = new FastByteBuffer(size);
	}

	/**
	 * 往快速缓冲区中写入字节数组
	 */
	@Override
	public void write(final byte[] bytes, final int off, final int len) throws IOException {
		buffer.append(bytes, off, len);
	}

	/**
	 * 往快速缓冲区中写入一个字节
	 */
	@Override
	public void write(final int data) throws IOException {
		buffer.append((byte) data);
	}

	/**
	 * 获得流的大小
	 */
	public int size() {
		return buffer.getSize();
	}

	/**
	 * 关闭流
	 */
	@Override
	public void close() throws IOException {
		// 基于数据的，嘛都不干
	}

	/**
	 * 重置流
	 */
	public void reset() {
		buffer.reset();
	}

	/**
	 * 将快速缓冲区内容写入输出流
	 * 
	 * @param outputStream
	 *            输出流
	 */
	public void writeTo(final OutputStream outputStream) {
		final int index = buffer.index();
		try {
			for (int i = 0; i < index; i++) {
				final byte[] buf = buffer.array(i);
				outputStream.write(buf);
			}
			outputStream.write(buffer.array(index), 0, buffer.getOffset());
		} catch (IOException e) {
			throw new ToolException(StringUtil
					.format("fast byte buffer write to output stream failue,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 获得流中字节数组
	 */
	public byte[] toByteArray() {
		return buffer.toArray();
	}

	/**
	 * 这都要注解
	 */
	@Override
	public String toString() {
		return new String(toByteArray());
	}

	/**
	 * 这都要注解
	 */
	public String toString(final Charset charset) {
		return new String(toByteArray(), Objects.isNull(charset) ? Charset.defaultCharset() : charset);
	}
}
