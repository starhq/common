package com.star.io.file;

import java.nio.file.Path;

import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 文件名帮助类
 * 
 * @author starhq
 *
 */
public final class FileNameUtils {

	private FileNameUtils() {
	}

	/**
	 * 获得主文件名 如果是目录，返回最外层的目录
	 * 
	 * 当前目录的话要另外处理
	 */
	public static String mainName(final String fileName) {
		Assert.notNull(fileName, "get file's main name failure,the file name is null");
		final Path path = PathUtil.getAbsolutePath(fileName);
		return path.getFileName().toString();
	}

	/**
	 * 获得文件的扩展名
	 */
	public static String extName(final String fileName) {
		Assert.notNull(fileName, "get file's ext name failure,the file name is null");
		final String name = mainName(fileName);
		final int index = name.lastIndexOf(StringUtil.DOT);
		return index == -1 ? StringUtil.EMPTY : name.substring(index + 1);
	}

}
