package com.star.io;

import com.star.collection.ArrayUtil;
import com.star.lang.Assert;

/**
 * 快速缓冲，将数据存放在缓冲集中，取代以往的单一数组
 * 
 * 功能和bytebuffer差不多,测下来性能要好一点，可以考虑在实战中使用
 * 
 * @author http://git.oschina.net/loolly/hutool
 */
public class FastByteBuffer {

	/**
	 * 缓冲集
	 */
	private transient byte[][] buffers = new byte[16][];
	/**
	 * 缓冲数
	 */
	private transient int buffersCount;
	/**
	 * 当前缓冲索引
	 */
	private transient int currenIndex = -1;
	/**
	 * 当前缓冲
	 */
	private transient byte[] currentBuffer;
	/**
	 * 当前缓冲偏移量
	 */
	private transient int offset;
	/**
	 * 缓冲字节数
	 */
	private transient int size;

	/**
	 * 一个缓冲区的最小字节数
	 */
	private transient final int minChunkLen;

	/**
	 * 构造方法，初始化最小字节数
	 */
	public FastByteBuffer() {
		this.minChunkLen = 1024;
	}

	/**
	 * 构造方法，设置最小字节数
	 */
	public FastByteBuffer(final int size) {
		this.minChunkLen = Math.abs(size);
	}

	/**
	 * 分配下一个缓冲区，不会小于1024
	 */
	private void needNewBuffer(final int newSize) {
		final int delta = newSize - size;
		final int newBufferSize = Math.max(minChunkLen, delta);

		currenIndex++;
		currentBuffer = new byte[newBufferSize];
		offset = 0;

		// add buffer

		if (currenIndex >= buffers.length) {
			final int newLen = buffers.length << 1;
			final byte[][] newBuffers = new byte[newLen][];
			System.arraycopy(buffers, 0, newBuffers, 0, buffers.length);
			buffers = newBuffers;
		}
		buffers[currenIndex] = currentBuffer;
		buffersCount++;
	}

	/**
	 * 
	 * 向快速缓冲加入数据
	 * 
	 */
	public FastByteBuffer append(final byte[] array, final int off, final int len) {
		final int end = off + len;
		// 这个还需要验证
		Assert.isTrue(off >= 0 && len >= 0 && end <= array.length,
				"append data to fast byte array buffer failure,the reason is index out of bounds");
		if (len > 0) {
			int remaining = len;

			if (ArrayUtil.isEmpty(currentBuffer)) {
				// first try to fill current buffer

				final int part = Math.min(remaining, currentBuffer.length - offset);
				System.arraycopy(array, end - remaining, currentBuffer, offset, part);
				remaining -= part;
				offset += part;
				size += part;
			}

			if (remaining > 0) {
				// still some data left

				// ask for new buffer

				needNewBuffer(size + len);

				// then copy remaining

				// but this time we are sure that it will fit

				final int part = Math.min(remaining, currentBuffer.length - offset);
				System.arraycopy(array, end - remaining, currentBuffer, offset, part);
				offset += part;
				size += part;
			}
		}
		return this;
	}

	/**
	 * 
	 * 向快速缓冲加入数据
	 */
	public FastByteBuffer append(final byte[] array) {
		return append(array, 0, array.length);
	}

	/**
	 * 
	 * 向快速缓冲加入一个字节
	 */
	public FastByteBuffer append(final byte element) {
		if (ArrayUtil.isEmpty(currentBuffer) || offset == currentBuffer.length) {
			needNewBuffer(size + 1);
		}

		currentBuffer[offset] = element;
		offset++;
		size++;

		return this;
	}

	/**
	 * 
	 * 将另一个快速缓冲加入到自身
	 */
	public FastByteBuffer append(final FastByteBuffer buff) {
		if (buff.size > 0) {
			for (int i = 0; i < buff.currenIndex; i++) {
				append(buff.buffers[i]);
			}
			append(buff.currentBuffer, 0, buff.offset);
		}
		return this;
	}

	/**
	 * 获取快速缓冲区大小
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 缓冲区是否为空
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * 当前缓冲位于缓冲区的索引位
	 */
	public int index() {
		return currenIndex;
	}

	/**
	 * 缓冲区当前偏移量
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * 根据索引位返回缓冲集中的缓冲
	 */
	public byte[] array(final int index) {
		return buffers[index].clone();
	}

	/**
	 * 重置缓冲区
	 */
	public void reset() {
		size = 0;
		offset = 0;
		currenIndex = -1;
		currentBuffer = new byte[0];
		buffersCount = 0;
	}

	/**
	 * 
	 * 返回快速缓冲中的数据
	 * 
	 */
	public byte[] toArray() {
		byte[] array;
		if (currenIndex == -1) {
			array = new byte[0];
		} else {
			int pos = 0;
			array = new byte[size];
			for (int i = 0; i < currenIndex; i++) {
				final int len = buffers[i].length;
				System.arraycopy(buffers[i], 0, array, pos, len);
				pos += len;
			}
			System.arraycopy(buffers[currenIndex], 0, array, pos, offset);
		}
		return array;
	}

	/**
	 * 
	 * 返回快速缓冲中的数据
	 * 
	 */
	public byte[] toArray(final int start, final int len) {
		int startIndex = start;
		byte[] array;

		if (len == 0) {
			array = new byte[0];
		} else {
			int index = 0;
			while (startIndex >= buffers[index].length) {
				startIndex -= buffers[index].length;
				index++;
			}

			int remaining = len;
			int pos = 0;
			array = new byte[len];
			while (index < buffersCount) {
				final byte[] buf = buffers[index];
				final int length = Math.min(buf.length - startIndex, remaining);
				System.arraycopy(buf, start, array, pos, length);
				pos += length;
				remaining -= length;
				if (remaining == 0) {
					break;
				}
				startIndex = 0;
				index++;
			}
		}

		return array;
	}

	/**
	 * 
	 * 根据索引位返回一个字节
	 */
	public byte get(final int index) {
		int idx = index;
		Assert.isTrue(idx < size && idx >= 0,
				"get content from fast byte buffer failue,the reason is index out of bounds");
		int ndx = 0;
		while (true) {
			final byte[] bytes = buffers[ndx];
			if (index < bytes.length) {
				return bytes[index];
			}
			ndx++;
			idx -= bytes.length;
		}
	}

}
