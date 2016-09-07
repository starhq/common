package com.star.lang;

import java.io.BufferedReader;

/**
 * bufferedreader 读取文件的时候做下一步处理
 */
public interface ReaderHandler<T> {
	public T handle(BufferedReader reader);
}
